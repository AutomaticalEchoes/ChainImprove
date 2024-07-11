package com.automaticalechoes.chainimprove.mixin;

import com.automaticalechoes.chainimprove.api.ChainNode;
import com.automaticalechoes.chainimprove.api.ILeashFenceKnotEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(LeashFenceKnotEntity.class)
public abstract class LeashFenceKnotEntityMixin extends HangingEntity implements ILeashFenceKnotEntity {

    private static final String SIZE = "knot_size";
    private static final EntityDataAccessor<Integer> KNOT_SIZE = SynchedEntityData.defineId(LeashFenceKnotEntity.class, EntityDataSerializers.INT);

    protected Integer kont_size = 0;
    protected LeashFenceKnotEntityMixin(EntityType<? extends HangingEntity> p_31703_, Level p_31704_) {
        super(p_31703_, p_31704_);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(KNOT_SIZE, 1);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_31736_) {
        super.addAdditionalSaveData(p_31736_);
        p_31736_.putInt(SIZE, kont_size);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_31730_) {
        super.readAdditionalSaveData(p_31730_);
        kont_size = p_31730_.getInt(SIZE);
    }

    @Override
    public void shrunk() {
        kont_size --;
        if(kont_size <=0) this.discard();
    }

    @Override
    public int getWidth() {
        return (9 + kont_size) / 12 + 9;
    }

    @Override
    public int getHeight() {
        return 3 + Math.min(kont_size, 9);
    }

    /**
     * @author AutomaticalEchoes
     * @reason change the rule, easier to add leading entities.
     * 1.let all the player leading entities leash to this.
     * 2.let all leading entities leash to player if shift key down.
     * 3.nothing happen Except for those 2 situations.
     * now, if u want let only one entity leash to u, should interact the target entity (must leading by knot entity) with empty item in main hand.
     */
    @Overwrite
    public @NotNull InteractionResult interact(Player p_31842_, InteractionHand p_31843_) {
        if (this.level().isClientSide) {
            return InteractionResult.SUCCESS;
        } else if(p_31842_.isShiftKeyDown()){
            kont_size -= ILeashFenceKnotEntity.fromALeash2B(this,p_31842_);
            this.discard();
            return InteractionResult.SUCCESS;
        }else{
            kont_size += ILeashFenceKnotEntity.fromALeash2B(p_31842_,this);
            return InteractionResult.SUCCESS;
        }
    }



}

