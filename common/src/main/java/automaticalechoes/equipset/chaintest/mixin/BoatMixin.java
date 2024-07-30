package automaticalechoes.equipset.chaintest.mixin;

import automaticalechoes.equipset.chaintest.ChainEntity;
import automaticalechoes.equipset.chaintest.ChainTest;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Boat.class)
public abstract class BoatMixin extends Entity{
    @Shadow public abstract void lerpTo(double p_38299_, double p_38300_, double p_38301_, float p_38302_, float p_38303_, int p_38304_, boolean p_38305_);

    @Shadow public abstract void tick();
    private static final EntityDataAccessor<Integer> NODE_ID = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.INT);
    private @Nullable Entity node;
    private @Nullable UUID nodeUuid;
    public BoatMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }


    @Inject(method = "interact", at = @At("RETURN") ,cancellable = true)
    public void interact(Player p_38330_, InteractionHand p_38331_, CallbackInfoReturnable<InteractionResult> callbackInfoReturnable) {
        if(p_38330_.getItemInHand(p_38331_).is(Items.CHAIN)){
            ChainEntity chainEntity = new ChainEntity(ChainTest.CHAIN_ENTITY, level());
            chainEntity.leftChain2(p_38330_);
            chainEntity.rightChain2(this);
            level().addFreshEntity(chainEntity);
        }
    }

}
