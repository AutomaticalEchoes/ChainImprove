package com.automaticalechoes.chainimprove.mixin;

import com.automaticalechoes.chainimprove.api.ChainNode;
import com.automaticalechoes.chainimprove.api.ILeashFenceKnotEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
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

    @Inject(method = "destroy" ,at = @At("RETURN"))
    public void destroy(DamageSource p_219862_, CallbackInfo ci){
        if(node != null){
            breakChain(this,false);
        }
    }

    @Override
    public void kill() {
        super.kill();
        if(node != null){
            breakChain(this,false);
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo callbackInfo){
        nodeTick(level(), this);
        Vec3 v2 = this.getDeltaMovement();
        if(v2.horizontalDistance() > 0.1) {
            float yRotNeo = (float) (Mth.atan2(v2.z, v2.x) * (double) (180F / (float) Math.PI)) - 90.0F;
            if(this.getYRot() != yRotNeo){
                float yRot = this.getYRot();
                float rot = Mth.wrapDegrees(yRotNeo - yRot) / 5;
                this.setYRot(yRot + rot);
            }
        }
    }

    @Override
    protected Vec3 getLeashOffset() {
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

    @Override
    public int getNodeId() {
        return entityData.get(NODE_ID);
    }


    @Override
    public void resetNode() {
        if(this.node instanceof LeashFenceKnotEntity knotEntity){
            ((ILeashFenceKnotEntity)knotEntity).shrunk();
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
    public @Nullable Entity getChainedNode() {
        return this.node;
    }
}
