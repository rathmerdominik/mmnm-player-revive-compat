package net.hammerclock.mmnmrevive.mixins;

import net.hammerclock.mmnmrevive.config.CommonConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.cap.BleedingStorage;
import team.creative.playerrevive.server.PlayerReviveServer;
import team.creative.playerrevive.server.ReviveEventServer;

import xyz.pixelatedw.mineminenomi.events.ChallengesEvents;
import xyz.pixelatedw.mineminenomi.init.ModEffects;
import xyz.pixelatedw.mineminenomi.wypi.WyHelper;

@Mixin(ReviveEventServer.class)
public class ConfigurableBleedOutInChallengeDimensionMixin {

    @Inject(method = "playerDied", at = @At("HEAD"), cancellable = true, remap = false)
    public void playerDied(LivingDeathEvent event, CallbackInfo ci)
    {
        LivingEntity livingEntity = event.getEntityLiving();
        if(!(livingEntity instanceof ServerPlayerEntity)) {
            return;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) livingEntity;
        World world = player.getCommandSenderWorld();

        if(WyHelper.isInChallengeDimension(world)) {
            if(!CommonConfig.INSTANCE.isReviveAllowed()) {
                ci.cancel();
                return;
            }

            IBleeding bledOutPlayer = PlayerReviveServer.getBleeding(player);
            if (bledOutPlayer.bledOut()) {
                bledOutPlayer.revive();
                ci.cancel();
            }
        }
    }

    @Inject(method = "playerTick", at = @At(value = "INVOKE", target = "Lteam/creative/playerrevive/server/PlayerReviveServer;kill(Lnet/minecraft/entity/player/PlayerEntity;)V"), cancellable = true, remap = false)
    public void playerTick(TickEvent.PlayerTickEvent event, CallbackInfo ci)
    {
        event.player.revive();
        event.player.addEffect(new EffectInstance(ModEffects.CHALLENGE_FAILED.get(), 40, 0, false, false));
        event.player.setHealth(1.0F);
        ci.cancel();
    }
}
