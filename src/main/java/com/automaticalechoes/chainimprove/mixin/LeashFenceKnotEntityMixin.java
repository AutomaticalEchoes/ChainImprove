package com.automaticalechoes.chainimprove.mixin;

import com.automaticalechoes.chainimprove.api.ChainNode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;
import java.util.function.Predicate;

@Mixin(LeashFenceKnotEntity.class)
public abstract class LeashFenceKnotEntityMixin extends HangingEntity {
    static final Predicate<Entity> KNOTABLE_PREDICATE = entity -> entity instanceof ChainNode|| entity instanceof Mob;
    protected static final String SIZE = "knot_size";
    protected static final EntityDataAccessor<Integer> KNOT_SIZE = SynchedEntityData.defineId(LeashFenceKnotEntity.class, EntityDataSerializers.INT);

    protected Integer kont_size = 0;
    protected LeashFenceKnotEntityMixin(EntityType<? extends HangingEntity> p_31703_, Level p_31704_) {
        super(p_31703_, p_31704_);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(KNOT_SIZE, 0);
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
    public int getWidth() {
        return (9 + kont_size) / 12 + 9;
    }

    @Override
    public int getHeight() {
        return 3 + Math.min(kont_size, 9);
    }


    @Override
    public void playPlacementSound() {

    }

//    public boolean merge(Level level, Entity player){
//        if(this.level().isClientSide ) return false;
//        boolean flag = false;
//        List<Entity> list = this.level().getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(7.0D), KNOTABLE_PREDICATE)
//                .stream().filter(entity -> entity != player).toList();
//        for(Entity entity : list) {
//            if(kont_size >= 24) return false;
//            else if (entity instanceof Mob mob && mob.getLeashHolder() == player) {
//                mob.setLeashedTo(this, true);
//                kont_size++;
//                flag = true;
//            }else if (entity instanceof ChainNode node && node.getNode() == player){
//                node.chainTo(this);
//                kont_size++;
//                flag = true;
//            }
//        }
//        return flag;
//    }


    public void getALL(Entity entity){
        if (this.level().isClientSide) return;
        List<Entity> list = this.level().getEntitiesOfClass(Entity.class, entity.getBoundingBox().inflate(7.0D), KNOTABLE_PREDICATE)
                .stream().toList();
        for (Entity entity1 : list) {
            if(entity1 == this) return;
            if(entity1 instanceof Mob mob && mob.getLeashHolder() == this){
                mob.setLeashedTo(entity, true);
            }else if(entity1 instanceof ChainNode node && node.getChainedNode() == this){
                node.chainTo(entity);
            }
        }
    }

    public void merge(List<Entity> entities){
        if(this.level().isClientSide) return;
        for (Entity entity: entities){
            if (entity instanceof Mob mob) {
                mob.setLeashedTo(this, true);
                kont_size++;
            }else if (entity instanceof ChainNode node){
                node.chainTo(this);
                kont_size++;
            }
        }
    }

}

