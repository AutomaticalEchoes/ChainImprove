package com.automaticalechoes.chainimprove.mixin;

import com.automaticalechoes.chainimprove.api.ChainNode;
import com.automaticalechoes.chainimprove.common.ChainKnotEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FenceBlock.class)
public class FenceBlockMixin {
    @Inject(method = "use" , at = @At("HEAD"), cancellable = true)
    public void use(BlockState p_53316_, Level level, BlockPos blockpos, Player player, InteractionHand p_53320_, BlockHitResult p_53321, CallbackInfoReturnable<InteractionResult> callbackInfoReturnable){
        if(level.isClientSide){
            ItemStack itemInHand = player.getItemInHand(p_53320_);
            callbackInfoReturnable.setReturnValue(itemInHand.isEmpty() || itemInHand.is(Items.CHAIN) ? InteractionResult.SUCCESS : InteractionResult.PASS);
        }else{
            callbackInfoReturnable.setReturnValue(bindNode(level, blockpos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS);
        }

    }

    public boolean bindNode(Level level, BlockPos blockpos, Player player){
        List<Entity> chainedEntityOfNode = ChainNode.getChainedEntityOfNode(player, level);
        for (Entity entity : chainedEntityOfNode) {
            ChainKnotEntity chainKnot = ChainKnotEntity.getOrCreateChainKnot(level, blockpos);
            ((ChainNode)entity).chainTo(chainKnot);
        }
        return chainedEntityOfNode.size() > 0;
    }
}
