package com.automaticalechoes.chainimprove.api;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
    int getNodeId();
    void setNode(Entity node);
    Entity getChainedNode();
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
        if(this.getChainedNode() != null){
            compoundTag.putUUID(NODE_UUID,this.getChainedNode().getUUID());
        }
    }

    default void breakChain(Entity entity, boolean shouldNodePlaySound){
        if(shouldNodePlaySound){
            getChainedNode().playSound(SoundEvents.CHAIN_BREAK);
        }
        resetNode();
        entity.playSound(SoundEvents.CHAIN_BREAK);
        entity.spawnAtLocation(Items.CHAIN);
    }

    default boolean interactNode(Player p_38330_, InteractionHand p_38331_, Entity thos) {
        ItemStack itemInHand = p_38330_.getItemInHand(p_38331_);
        boolean validItem = itemInHand.isEmpty() || itemInHand.is(Items.CHAIN);
        if(this.getChainedNode() != null){
            if((this.getChainedNode() == p_38330_ && validItem) || itemInHand.is(Items.SHEARS)){
                breakChain(thos, false);
                return true;
            }else if (this.getChainedNode() instanceof LeashFenceKnotEntity knotEntity && validItem){
                chainTo(p_38330_);
                ((ILeashFenceKnotEntity)knotEntity).shrunk();
                return true;
            }
        }else if(this.getChainedNode() == null){
            if(validItem && ChainNode.bindChainedNode(p_38330_,p_38330_.level(),thos)){
                return true;
            }else if(itemInHand.is(Items.CHAIN)) {
                this.chainTo(p_38330_);
                thos.playSound(SoundEvents.CHAIN_PLACE);
                itemInHand.shrink(1);
                return true;
            }
        }
        return false;
    }


    static boolean bindChainedNode(Entity nodeOld, Level p_42831_, Entity nodeNew) {
        for(Entity entity : getChainedEntityOfNode(nodeOld, p_42831_)) {
                ((ChainNode)entity).chainTo(nodeNew);
                nodeNew.playSound(SoundEvents.CHAIN_PLACE);
                return true;
        }
       return false;
    }

    static List<Entity> getChainedEntityOfNode(Entity node, Level p_42831_){
        return p_42831_.getEntitiesOfClass(Entity.class, node.getBoundingBox().inflate(7.0D), NODE_PREDICATE)
                .stream().filter(entity -> entity != node && ((ChainNode)entity).getChainedNode() == node)
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

    default void ClientNodeTick(ClientLevel clientLevel){
        int id = getNodeId();
        if(id == Integer.MIN_VALUE){
            resetNode();
        }else if(getChainedNode() == null || getChainedNode().getId() != id){
            Entity entity = clientLevel.getEntity(id);
            this.setNode(entity);
        }
    }

    default void ServerNodeTick(ServerLevel serverLevel, Entity thos){
        if(getChainedNode() != null && !getChainedNode().isAlive()){
            breakChain(thos, false);
            return;
        }
        if(getChainedNode() == null && getNodeUuid() != null) {
            Entity entity = ChainNode.searchEntity(serverLevel, thos, getNodeUuid());
            if(entity == null) {
                breakChain(thos, false);
            }else {
                this.setNode(entity);
            }
        }

    }

    default void close2Node(Entity thos){
        Entity chainedNode = this.getChainedNode();
        Vec3 subtract = chainedNode.position().subtract(thos.position());
        double length = subtract.length();
        Vec3 vec = subtract.normalize();
        if(length < 2) return;
        Vec3 nodeVec = chainedNode.getDeltaMovement();
        double v1 = nodeVec.length();
        double cos = nodeVec.dot(vec) /(length * v1);
        if(!thos.level().isClientSide ){
            if(length > 24){
                breakChain(thos, true);
                return;
            }else {
                chainedNode.addDeltaMovement(chainedNode.getDeltaMovement().scale(- 0.1));
            }
        }
        double f = 0.06 * length + Mth.clamp(cos != 0 ? 1 / cos : 1 , 0, 2) * v1;
        Vec3 v = vec.scale(f);
        thos.setDeltaMovement(v);
    }

    default void nodeTick(Level level, Entity thos){
        if(level instanceof ClientLevel clientLevel)
            ClientNodeTick(clientLevel);
        else if(level instanceof ServerLevel serverLevel)
            ServerNodeTick(serverLevel, thos);
        if(getChainedNode() != null) close2Node(thos);
    }
}
