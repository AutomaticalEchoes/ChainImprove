package automaticalechoes.equipset.chaintest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ChainEntity extends Entity {
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
        if(LeftNode != null && RightNode !=null && !level().isClientSide){
            Vec3 subtract = LeftNode.position().subtract(RightNode.position());
            length = subtract.length();
            Vec3 v1 = LeftNode.getDeltaMovement();
            Vec3 v2 = RightNode.getDeltaMovement();
            this.setPos(RightNode.position().add(subtract.scale(0.5)));
            if (v2.normalize().dot(v1.normalize()) > 0.8 && Math.abs(v2.length() - v1.length()) < 0.2) return;
            Vec3 merge = v2.add(v1);
            LeftNode.setDeltaMovement(v1.lerp(merge, 0.5));
            RightNode.setDeltaMovement(v2.lerp(merge,0.5));
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return super.getAddEntityPacket();
    }


    public void leftChain2(Entity entity){
        LeftNode = entity;
    }

    public void rightChain2(Entity entity){
        RightNode = entity;
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
