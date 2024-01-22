package com.automaticalechoes.chainimprove.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class ChainBlockItem extends BlockItem {
    public static Item CHAIN_OVERRIDE = new ChainBlockItem(Blocks.CHAIN,new Properties());
    public ChainBlockItem(Block p_43060_, Properties p_43061_) {
        super(p_43060_, p_43061_);
    }

    @Nullable
    public BlockPlaceContext updatePlacementContext(BlockPlaceContext p_43063_) {
        Vec3 clickLocation = p_43063_.getClickLocation();
        BlockPos realPos = new BlockPos((int)Math.floor(clickLocation.x), (int) Math.floor(clickLocation.y), (int) Math.floor(clickLocation.z));
        BlockPos blockpos = p_43063_.getPlayer() != null && p_43063_.getPlayer().isShiftKeyDown() ? realPos : p_43063_.getClickedPos();
        Level level = p_43063_.getLevel();
        BlockState blockstate = level.getBlockState(blockpos);
        Block block = this.getBlock();
        if (blockstate.is(block) ){
            Direction.Axis axis = blockstate.getValue(BlockStateProperties.AXIS);
            double click_axis_length = clickLocation.get(axis);
            double pos_axis_length = Vec3.atCenterOf(blockpos).get(axis);
            Direction.AxisDirection axisDirection = click_axis_length > pos_axis_length ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
            Direction direction = Direction.get(axisDirection, axis);
            BlockPos.MutableBlockPos mutableBlockPos = directionPos(direction, blockpos, level);
            return mutableBlockPos == null ? null : BlockPlaceContext.at(p_43063_,mutableBlockPos,direction);
        }
        return super.updatePlacementContext(p_43063_);
    }

    @Nullable
    public BlockPos.MutableBlockPos directionPos(Direction direction, BlockPos pos, Level level){
        BlockPos.MutableBlockPos mutable = pos.mutable();
        int i = 0;
        while (level.isInWorldBounds(mutable) && level.getBlockState(mutable).is(this.getBlock()) && i <= 7){
            mutable.move(direction);
            i++;
        }
        return i > 7 ? null :mutable;
    }

//    public InteractionResult useOn(UseOnContext p_42834_) {
//        Level level = p_42834_.getLevel();
//        BlockPos blockpos = p_42834_.getClickedPos();
//        BlockState blockstate = level.getBlockState(blockpos);
//        if (blockstate.is(BlockTags.FENCES)) {
//            Player player = p_42834_.getPlayer();
//            if (!level.isClientSide && player != null) {
//                List<Entity> chainedEntityOfNode = ChainNode.getChainedEntityOfNode(player, level);
//                for (Entity entity : chainedEntityOfNode) {
//                    ChainKnotEntity chainKnot = ChainKnotEntity.getOrCreateChainKnot(level, blockpos);
//                    ((ChainNode)entity).chainTo(chainKnot);
//                }
//            }
//
//            return InteractionResult.sidedSuccess(level.isClientSide);
//        }
//        return super.useOn(p_42834_);
//
//    }

    @Override
    public int getUseDuration(ItemStack p_41454_) {
        return 15;
    }
}
