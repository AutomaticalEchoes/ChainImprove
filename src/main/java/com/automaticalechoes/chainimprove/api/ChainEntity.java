package com.automaticalechoes.chainimprove.api;

import com.automaticalechoes.chainimprove.ChainImprove;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ChainEntity extends Entity {
    Entity LeftNode;
    Entity RightNode;
    int size;
    double length;

    public ChainEntity(EntityType<?> p_19870_, Level p_19871_) {
        super(ChainImprove.CHAIN_ENTITY.get(), p_19871_);
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 subtract = LeftNode.position().subtract(RightNode.position());
        length = subtract.length();
        Vec3 v1 = LeftNode.getDeltaMovement();
        Vec3 v2 = RightNode.getDeltaMovement();
        Vec3 merge = v2.add(v1);
        LeftNode.setDeltaMovement(merge);
        RightNode.setDeltaMovement(merge);
        setDeltaMovement(merge);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_20052_) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_20139_) {

    }
}
