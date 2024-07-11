package com.automaticalechoes.chainimprove.mixin;

import com.automaticalechoes.chainimprove.api.ILeashFenceKnotEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(FenceBlock.class)
public class FenceBlockMixin {
    /**
     * @author Automaticalechoes
     * @reason change rule same with knot entity
     * {@link LeashFenceKnotEntityMixin#interact(Player, InteractionHand)}
     */
    @Overwrite
    public InteractionResult  use(BlockState p_53316_, Level level, BlockPos blockpos, Player player, InteractionHand p_53320_, BlockHitResult p_53321){
        if (level.isClientSide) {
            ItemStack itemstack = player.getItemInHand(p_53320_);
            return itemstack.is(Items.LEAD) || itemstack.is(Items.CHAIN) || itemstack.isEmpty() ? InteractionResult.SUCCESS : InteractionResult.PASS;
        } else {
            int i = ILeashFenceKnotEntity.fromALeash2Neo(player, blockpos);
            return i != 0 ? InteractionResult.SUCCESS: InteractionResult.CONSUME;
        }
    }
}
