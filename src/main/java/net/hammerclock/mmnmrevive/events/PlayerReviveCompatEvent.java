package net.hammerclock.mmnmrevive.events;

import team.creative.playerrevive.api.event.PlayerBleedOutEvent;
import team.creative.playerrevive.api.event.PlayerRevivedEvent;

import net.hammerclock.mmnmrevive.PlayerReviveCompatMod;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;

@Mod.EventBusSubscriber(modid = PlayerReviveCompatMod.PROJECT_ID)
public class PlayerReviveCompatEvent {

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onPlayerDeathEvent(LivingDeathEvent event) {
		if (event.getEntity() instanceof ServerPlayerEntity){
			AbilityHelper.disableAbilities((ServerPlayerEntity) event.getEntity(), Integer.MAX_VALUE , abl -> true);
		}
	}

	@SubscribeEvent
	public static void onReviveEvent(PlayerRevivedEvent event) {
		if (event.getPlayer() instanceof ServerPlayerEntity) {
			AbilityHelper.enableAbilities(event.getPlayer(), abl -> true);
		}
	}

	@SubscribeEvent
	public static void onBleedOutEvent(PlayerBleedOutEvent event) {
		if (event.getPlayer() instanceof ServerPlayerEntity) {
			AbilityHelper.enableAbilities(event.getPlayer(), abl -> true);
		}
	}
}
