package com.automaticalechoes.chainimprove.mixin;

import com.automaticalechoes.chainimprove.api.ChainNode;
import com.automaticalechoes.chainimprove.common.ChainKnotEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Boat.class)
public abstract class BoatMixin extends Entity implements ChainNode {
    @Shadow public abstract void lerpTo(double p_38299_, double p_38300_, double p_38301_, float p_38302_, float p_38303_, int p_38304_, boolean p_38305_);

    @Shadow public abstract void tick();

    private static final EntityDataAccessor<Integer> NODE_ID = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.INT);
    private @Nullable Entity node;
    private @Nullable UUID nodeUuid;
    public BoatMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "defineSynchedData", at = @At("RETURN"))
    public void defineData(CallbackInfo info){
        this.entityData.define(NODE_ID, Integer.MIN_VALUE);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    protected void readAdditionalSaveData(CompoundTag p_38338_, CallbackInfo info) {
        readNode(p_38338_);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    protected void addAdditionalSaveData(CompoundTag p_38359_, CallbackInfo info) {
        writeNode(p_38359_);
    }

    @Inject(method = "interact", at = @At("RETURN") ,cancellable = true)
    public void interact(Player p_38330_, InteractionHand p_38331_, CallbackInfoReturnable<InteractionResult> callbackInfoReturnable) {
        callbackInfoReturnable.setReturnValue(interactNode(p_38330_,p_38331_,this) ? InteractionResult.SUCCESS : super.interact(p_38330_, p_38331_));
    }


    @Inject(method = "hurt" ,
            at = @At(value = "INVOKE" , target = "Lnet/minecraft/world/entity/vehicle/Boat;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;",
                    shift = At.Shift.AFTER)
    )
    public void hurt(DamageSource p_38319_, float p_38320_, CallbackInfoReturnable<Boolean> cir){
        if(node != null){
            chainBreak(false);
        }
    }

    @Override
    public void kill() {
        super.kill();
        if(node != null){
            chainBreak(false);
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo callbackInfo){
        if(level.isClientSide)
            ClientNodeTick();
        else
            ServerNodeTick();
        NodePull();
//        IRot(this.getDeltaMovement());
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0D, 0.2F, this.getBbWidth() * 0.6F);
    }

    @Override
    public Vec3 getRopeHoldPosition(float p_36374_) {
//        float f = Mth.lerp(p_36374_ * 0.5F, this.getXRot(), this.xRotO) * ((float)Math.PI / 180F);
        float f1 = Mth.lerp(p_36374_, this.yRotO, this.getYRot()) * ((float)Math.PI / 180F);
        return this.getPosition(p_36374_).add(new Vec3(0.0D, (double)this.getBbHeight() * 0.5D, - this.getBbWidth() * 0.6F).yRot(-f1));
    }



    @Override
    public UUID getNodeUuid() {
        return this.nodeUuid;
    }

    @Override
    public void setNodeUuid(UUID uuid) {
        this.nodeUuid = uuid;
    }

    public void NodePull(){
        if(node != null){
            Vec3 subtract = this.node.position().subtract(this.position());
            double length = subtract.length();
            double v1 = this.getDeltaMovement().length();
            double v2 = node.getDeltaMovement().length();
            if(length < 5) return;
            if(!this.level.isClientSide && length > 24 + 12 * v2){
                if(v2 > 1.5){
                    chainBreak(true);
                    return;
                }else {
                    node.setDeltaMovement(node.getDeltaMovement().add(node.getDeltaMovement().scale(- 0.1)));
                }

            }
            double f = 0.06D + length / 100 + (v2 - v1) / 4;
            Vec3 v = subtract.normalize().scale(f);
            if(subtract.horizontalDistance() > 4) {
                float yRotNeo =  (float) (Mth.atan2(subtract.z, subtract.x) * (double) (180F / (float) Math.PI)) - 90.0F;
                if(this.getYRot() != yRotNeo){
                    float yRot = this.getYRot();
                    float rot = Mth.wrapDegrees(yRotNeo - yRot) / 5;
                    this.setYRot(yRot + rot);
                }
            }

            this.setDeltaMovement(getDeltaMovement().add(v));
        }
    }

    public void ClientNodeTick(){
        int id = entityData.get(NODE_ID);
        if(id == Integer.MIN_VALUE){
            resetNode();
        }else if(this.node == null || node.getId() != id){
            Entity entity = this.level.getEntity(id);
            this.setNode(entity);
        }
    }

    public void ServerNodeTick(){
        if(node != null && !node.isAlive()){
            chainBreak(false);
            return;
        }
        if(node == null && this.nodeUuid != null) {
            Entity entity = ChainNode.searchEntity(level, this, nodeUuid);
            if(entity == null) {
                chainBreak(false);
            }else {
                this.setNode(entity);
            }
        }

    }

    public void chainBreak(boolean shouldNodePlaySound){
        this.playSound(SoundEvents.CHAIN_BREAK,1.0F ,1.0F);
        if(shouldNodePlaySound){
            node.playSound(SoundEvents.CHAIN_BREAK,1.0F ,1.0F);
        }
        resetNode();
        spawnAtLocation(Items.CHAIN);
    }

    @Override
    public void resetNode() {
        if(this.node instanceof ChainKnotEntity chainKnotEntity){
            chainKnotEntity.discard();
        }
        this.node = null;
        this.nodeUuid = null;
        this.entityData.set(NODE_ID, Integer.MIN_VALUE);
    }

    @Override
    public void setNode(@Nullable Entity entity) {
        this.node = entity;
        this.nodeUuid = entity != null ? entity.getUUID() : null;
        this.entityData.set(NODE_ID, entity != null ? entity.getId() : Integer.MIN_VALUE);
    }

    @Override
    public @Nullable Entity getNode() {
        return this.node;
    }
    //    public void IRot(Vec3 p_20034_) {
//        boolean flag = this.status == Boat.Status.IN_WATER || this.status == Boat.Status.ON_LAND;
//        if(p_20034_.length() > 0.08F){
//            double d0 = p_20034_.x;
//            double d1 = p_20034_.y;
//            double d2 = p_20034_.z;
//            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
//            float xRot = Mth.wrapDegrees((float) (-(Mth.atan2(d1, d3) * (double) (180F / (float) Math.PI))));
//            this.lerpXRot = - xRot;
//        }else if(flag){
//            this.lerpXRot = 0;
//        }
//
//        float xRot = this.getXRot();
//        if(!flag){
//            this.setXRot((float) (xRot + ((lerpXRot - xRot) / 20)));
//        }
//    }
}
