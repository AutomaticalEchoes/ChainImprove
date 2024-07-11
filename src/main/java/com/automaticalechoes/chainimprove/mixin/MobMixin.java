package com.automaticalechoes.chainimprove.mixin;

import com.automaticalechoes.chainimprove.api.ILeashFenceKnotEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;

@Mixin(Mob.class)
public abstract class MobMixin {

    @Shadow @Nullable public abstract Entity getLeashHolder();

    @Shadow public abstract InteractionResult interact(Player p_21420_, InteractionHand p_21421_);

    @Shadow public abstract void setLeashedTo(Entity p_21464_, boolean p_21465_);

    @Inject(method = "checkAndHandleImportantInteractions", at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", shift = At.Shift.AFTER )} , locals = LocalCapture.CAPTURE_FAILHARD)
    public void checkAndHandleImportantInteractions(Player p_21500_, InteractionHand p_21501_, CallbackInfoReturnable<InteractionResult> cir){
        if(p_21501_ == InteractionHand.MAIN_HAND && getLeashHolder() instanceof LeashFenceKnotEntity knotEntity && (p_21500_.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() || p_21500_.getItemInHand(InteractionHand.MAIN_HAND).is(Items.LEAD))){
            setLeashedTo(p_21500_, true);
            ((ILeashFenceKnotEntity)knotEntity).shrunk();
        }
    }
}
