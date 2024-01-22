package com.automaticalechoes.chainimprove.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public interface ChainNode {
    Predicate<Entity> NODE_PREDICATE = entity -> entity instanceof ChainNode;
    String NODE_UUID = "follow_node";
    void setNodeUuid(UUID uuid);
    UUID getNodeUuid();
    void setNode(Entity node);
    Entity getNode();
    void resetNode();

    default void chainTo(@Nonnull Entity entity){
        this.setNode(entity);
    }

    default void readNode(CompoundTag compoundTag){
        if(compoundTag.contains(NODE_UUID)){
            this.setNodeUuid(compoundTag.getUUID(NODE_UUID));
        }
    }

    default void writeNode(CompoundTag compoundTag){
        if(this.getNode() != null){
            compoundTag.putUUID(NODE_UUID,this.getNode().getUUID());
        }
    }

    default boolean interactNode(Player p_38330_, InteractionHand p_38331_, Entity thos) {
        ItemStack itemInHand = p_38330_.getItemInHand(p_38331_);
        boolean validItem = itemInHand.isEmpty() || itemInHand.is(Items.CHAIN);
        if(this.getNode() != null && ((this.getNode() == p_38330_ && validItem) || itemInHand.is(Items.SHEARS))){
            resetNode();
            thos.playSound(SoundEvents.CHAIN_BREAK,1.0F, 1.0F);
            thos.spawnAtLocation(Items.CHAIN);
            return true;
        }else if(this.getNode() == null){
            if(validItem && ChainNode.bindChainedNode(p_38330_,p_38330_.level,thos)){
                return true;
            }else if(itemInHand.is(Items.CHAIN)) {
                this.chainTo(p_38330_);
                thos.playSound(SoundEvents.CHAIN_PLACE, 1.0F, 1.0F);
                itemInHand.shrink(1);
                return true;
            }
        }
        return false;
    }


    static boolean bindChainedNode(Entity nodeOld, Level p_42831_, Entity nodeNew) {
        for(Entity entity : getChainedEntityOfNode(nodeOld, p_42831_)) {
                ((ChainNode)entity).chainTo(nodeNew);
                nodeNew.playSound(SoundEvents.CHAIN_PLACE, 1.0F, 1.0F);
                return true;
        }
       return false;
    }

    static List<Entity> getChainedEntityOfNode(Entity node, Level p_42831_){
        return p_42831_.getEntitiesOfClass(Entity.class, node.getBoundingBox().inflate(7.0D), NODE_PREDICATE)
                .stream().filter(entity -> entity != node && ((ChainNode)entity).getNode() == node)
                .toList();
    }

    @Nullable
    static Entity searchEntity(Level level, Entity thos, UUID uuid){
        List<Entity> entitiesOfClass = level.getEntitiesOfClass(Entity.class, thos.getBoundingBox().inflate(24.0D), entity -> entity != thos);
        for(Entity entity : entitiesOfClass) {
            if(entity.getUUID().equals(uuid)) return entity;
        }
        return null;
    }

}
