package com.automaticalechoes.chainimprove.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChainBlock.class)
public class ChainBlockMixin extends RotatedPillarBlock {
    public ChainBlockMixin(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public void entityInside(BlockState p_60495_, Level p_60496_, BlockPos p_60497_, Entity p_60498_) {
        super.entityInside(p_60495_, p_60496_, p_60497_, p_60498_);
        if(!p_60498_.onGround() || p_60498_.getBlockStateOn().is(Blocks.CHAIN)){
            Direction.Axis axis = p_60495_.getValue(AXIS);
            Vec3 deltaMovement = p_60498_.getDeltaMovement();
            Vec3 scaleV = deltaMovement.scale(0.1);
            p_60498_.setDeltaMovement(scaleV.with(axis, deltaMovement.get(axis)));
            if(p_60495_.getValue(AXIS) == Direction.Axis.Y && !p_60498_.isShiftKeyDown()){
                p_60498_.setDeltaMovement(p_60498_.getDeltaMovement()
                        .with(Direction.Axis.Y, deltaMovement.y > 0 ? 0.2 : -0.2));
            }
        }
    }
}
