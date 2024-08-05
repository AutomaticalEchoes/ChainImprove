package automaticalechoes.equipset.chaintest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class ChainEntity extends Entity {
    private static final EntityDataAccessor<Integer> NODE_1_ID = SynchedEntityData.defineId(ChainEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> NODE_2_ID = SynchedEntityData.defineId(ChainEntity.class, EntityDataSerializers.INT);
    private Entity LeftNode;
    private Entity RightNode;
    private int size;
    private double length;

    public ChainEntity(EntityType<?> p_19870_, Level p_19871_) {
        super(ChainTest.CHAIN_ENTITY, p_19871_);
    }

    @Override
    public void tick() {
        super.tick();
        if(LeftNode == null && entityData.get(NODE_1_ID) != Integer.MIN_VALUE){
            this.LeftNode = level().getEntity(entityData.get(NODE_1_ID));
        }
        if(RightNode == null && entityData.get(NODE_2_ID) != Integer.MIN_VALUE){
            this.RightNode = level().getEntity(entityData.get(NODE_2_ID));
        }
        if(LeftNode == null || RightNode ==null) return;
        if(LeftNode.getType().equals(EntityType.LEASH_KNOT) || RightNode.getType().equals(EntityType.LEASH_KNOT)){
            return;
        }
        Vec3 subtract = LeftNode.position().subtract(RightNode.position());
        length = subtract.length();
        Vec3 v1 = LeftNode.getDeltaMovement();
        Vec3 v2 = RightNode.getDeltaMovement();
        Vec3 v11 = getV(length, size, subtract, v2, v1);
        this.setPos(RightNode.position());

        RightNode.setDeltaMovement(v11);
        this.setDeltaMovement(v11);
    }

    public Vec3 getV(double length, double size, Vec3 sub, Vec3 selfV , Vec3 v2){
        if(length > size){
            if(length < size + 1) return selfV.lerp(v2, length - size + 1);
            return v2.add(sub.normalize().scale(length - size));
        }
        return selfV;
    }


    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return super.getAddEntityPacket();
    }


    public void leftChain2(Entity entity){
        LeftNode = entity;
        Optional.ofNullable(entity).ifPresentOrElse(entity1 -> entityData.set(NODE_1_ID,entity1.getId()), () -> entityData.set(NODE_1_ID, Integer.MIN_VALUE));
    }

    public void rightChain2(Entity entity){
        RightNode = entity;
        Optional.ofNullable(entity).ifPresentOrElse(entity1 -> entityData.set(NODE_2_ID,entity1.getId()), () -> entityData.set(NODE_2_ID, Integer.MIN_VALUE));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(NODE_1_ID, Integer.MIN_VALUE);
        this.entityData.define(NODE_2_ID, Integer.MIN_VALUE);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_20052_) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_20139_) {

    }
}
