package com.automaticalechoes.chainimprove.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;

import java.util.List;
import java.util.function.Predicate;

public interface ILeashFenceKnotEntity {
    Predicate<Entity> KNOTABLE_PREDICATE = entity -> entity instanceof ChainNode|| entity instanceof Mob;
    void shrunk();

    static int fromALeash2B(Entity entityA, Entity entityB){
        if (entityA.level().isClientSide) return 0;
        int i = 0;
        List<Entity> list = entityA.level().getEntitiesOfClass(Entity.class, entityA.getBoundingBox().inflate(7.0D), KNOTABLE_PREDICATE)
                .stream().toList();
        for (Entity entity1 : list) {
            if(entity1 == entityA) return 0;
            if(entity1 instanceof Mob mob && mob.getLeashHolder() == entityA){
                mob.setLeashedTo(entityB, true);
                i ++;
            }else if(entity1 instanceof ChainNode node && node.getChainedNode() ==  entityA){
                node.chainTo(entityB);
                i ++;
            }
        }
        return i;
    }

    static int fromALeash2Neo(Entity entityA, BlockPos blockpos){
        if (entityA.level().isClientSide) return 0;
        int i = 0;
        List<Entity> list = entityA.level().getEntitiesOfClass(Entity.class, entityA.getBoundingBox().inflate(7.0D), KNOTABLE_PREDICATE)
                .stream().toList();
        for (Entity entity1 : list) {
            if(entity1 == entityA) return 0;
            LeashFenceKnotEntity entityB = LeashFenceKnotEntity.getOrCreateKnot(entityA.level(), blockpos);
            if(entity1 instanceof Mob mob && mob.getLeashHolder() == entityA){
                mob.setLeashedTo(entityB, true);
                i ++;
            }else if(entity1 instanceof ChainNode node && node.getChainedNode() ==  entityA){
                node.chainTo(entityB);
                i ++;
            }
        }
        return i;
    }
}
