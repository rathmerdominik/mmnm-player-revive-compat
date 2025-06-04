package net.hammerclock.mmnmrevive.mixins;

import net.hammerclock.mmnmrevive.config.CommonConfig;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.creative.playerrevive.server.PlayerReviveServer;
import team.creative.playerrevive.server.ReviveEventServer;
import xyz.pixelatedw.mineminenomi.init.ModEffects;
import xyz.pixelatedw.mineminenomi.wypi.WyHelper;

@Mixin(ReviveEventServer.class)
abstract class DeathInChallengeFixMixin {

    @Inject(method = "playerDied", at = @At("HEAD"), cancellable = true, remap = false)
    public void mmnmrevive$playerDied(LivingDeathEvent event, CallbackInfo ci) {
        if (!CommonConfig.INSTANCE.isReviveAllowedInChallenge()) ci.cancel();
    }

    @Inject(method = "playerTick", at = @At(value = "INVOKE", target = "Lteam/creative/playerrevive/server/PlayerReviveServer;kill(Lnet/minecraft/entity/player/PlayerEntity;)V"), cancellable = true, remap = false)
    public void mmnmrevive$playerTick(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        if (!WyHelper.isInChallengeDimension(event.player.level)) return;

        PlayerReviveServer.revive(event.player);
        event.player.addEffect(new EffectInstance(ModEffects.CHALLENGE_FAILED.get(), 40, 0, false, false));
        ci.cancel();
    }
}
