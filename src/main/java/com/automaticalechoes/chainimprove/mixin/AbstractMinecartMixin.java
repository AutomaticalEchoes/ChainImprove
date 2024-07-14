package com.automaticalechoes.chainimprove.mixin;

import com.automaticalechoes.chainimprove.api.ChainNode;
import com.automaticalechoes.chainimprove.api.ILeashFenceKnotEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.UUID;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin extends Entity implements ChainNode {
    private static final EntityDataAccessor<Integer> NODE_ID = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);
    private @Nullable Entity node;
    private @Nullable UUID nodeUuid;

    public AbstractMinecartMixin(EntityType<?> p_19870_, Level p_19871_) {
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
    public void setNode(@Nullable Entity entity) {
        this.node = entity;
        this.nodeUuid = entity != null ? entity.getUUID() : null;
        this.entityData.set(NODE_ID, entity != null ? entity.getId() : Integer.MIN_VALUE);
    }

    @Override
    public @Nullable Entity getChainedNode() {
        return this.node;
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
    public InteractionResult interact(Player p_19978_, InteractionHand p_19979_) {
        return interactNode(p_19978_, p_19979_ ,this) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci){
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
    public void kill() {
        super.kill();
        if(node != null){
            breakChain(this,false);
        }
    }

    @Override
    protected Vec3 getLeashOffset() {
        return new Vec3(this.getBbWidth() * 0.6F, (double)this.getBbHeight() * 0.5D, 0F);
    }

    @Override
    public Vec3 getRopeHoldPosition(float p_36374_) {
//        float f = Mth.lerp(p_36374_ * 0.5F, this.getXRot(), this.xRotO) * ((float)Math.PI / 180F);
        float f1 = Mth.lerp(p_36374_, this.yRotO, this.getYRot()) * ((float)Math.PI / 180F);
        return this.getPosition(p_36374_).add(new Vec3(- this.getBbWidth() * 0.6F, (double)this.getBbHeight() * 0.5D, 0F).yRot(-f1));
    }

    @Inject(method = "moveMinecartOnRail", at = {@At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;")}, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void moveOnRail(BlockPos pos, CallbackInfo ci, AbstractMinecart mc, double d24, double d25){

    }
}
