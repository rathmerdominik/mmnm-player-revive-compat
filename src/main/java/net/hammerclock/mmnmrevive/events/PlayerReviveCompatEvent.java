package net.hammerclock.mmnmrevive.events;

import java.util.UUID;

import team.creative.playerrevive.api.event.PlayerBleedOutEvent;
import team.creative.playerrevive.api.event.PlayerRevivedEvent;
import team.creative.playerrevive.server.PlayerReviveServer;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.hammerclock.mmnmrevive.PlayerReviveCompatMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.pixelatedw.mineminenomi.abilities.KnockdownAbility;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.api.helpers.SoulboundItemHelper;
import xyz.pixelatedw.mineminenomi.data.entity.ability.AbilityDataCapability;
import xyz.pixelatedw.mineminenomi.data.entity.ability.IAbilityData;
import xyz.pixelatedw.mineminenomi.data.entity.entitystats.EntityStatsCapability;
import xyz.pixelatedw.mineminenomi.data.entity.entitystats.IEntityStats;
import xyz.pixelatedw.mineminenomi.init.ModAbilityKeys;
import xyz.pixelatedw.mineminenomi.init.ModEffects;
import xyz.pixelatedw.mineminenomi.init.ModItems;
import xyz.pixelatedw.mineminenomi.wypi.WyHelper;

public class PlayerReviveCompatEvent {

	private static final Logger LOGGER = LogManager.getLogger(PlayerReviveCompatMod.PROJECT_ID);

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerDeathEvent(LivingDeathEvent event) {
		if (event.getEntity() instanceof ServerPlayerEntity) {
			ServerPlayerEntity living = (ServerPlayerEntity) event.getEntityLiving();
			DamageSource source = event.getSource();
			Entity directEntity = source.getDirectEntity();
			Entity trueEntity = source.getEntity();

			LivingEntity attacker = null;
			
			if (directEntity instanceof LivingEntity) {
				attacker = (LivingEntity) directEntity;
			}
			else if (trueEntity instanceof LivingEntity) {
				attacker = (LivingEntity) trueEntity;
			}
			LOGGER.debug("Player {} has died and is bleeding out", living.getDisplayName().getString());

			IEntityStats deadPlayerEntityStats = EntityStatsCapability.get(living);
			LOGGER.debug("Source of death was {}", source);
			LOGGER.debug("Logging entity of deathcause of player: {}", source.getEntity());

			if (attacker != null) {
			IAbilityData props = AbilityDataCapability.get(attacker);

			boolean isKnockdownActive = false;
				if (props != null) {
					KnockdownAbility abl = props.getPassiveAbility(KnockdownAbility.INSTANCE);
					if (abl != null) {
						isKnockdownActive = !abl.getComponent(ModAbilityKeys.PAUSE_TICK).get().isPaused();
					}
				}
				
				if (isKnockdownActive) {
					living.addEffect(new EffectInstance(ModEffects.UNCONSCIOUS.get(), 1800, 1));
					living.setHealth(2.0f);
					living.clearFire();
					event.setCanceled(true);
					return;
				}
			}

			if (source.getMsgId().equals("heart_damage")) {
				PlayerReviveServer.kill(living);
			}

			if (attacker instanceof ServerPlayerEntity && !deadPlayerEntityStats.hasStrawDoll()) {
				ServerPlayerEntity deathCausePlayer = (ServerPlayerEntity) attacker.getEntity();
				LOGGER.debug("Got the following Player as the death cause {}",
						deathCausePlayer.getDisplayName().getString());
				for (int i = 0; i < deathCausePlayer.inventory.items.size(); i++) {
					ItemStack stack = deathCausePlayer.inventory.getItem(i);
					if (stack.getItem() == ModItems.STRAW_DOLL.get()) {
						LOGGER.info("Found a strawdoll in {} inventory!",
								deathCausePlayer.getDisplayName().getString());
						Pair<UUID, LivingEntity> strawDollOwner = SoulboundItemHelper.getOwner(deathCausePlayer.level,
								stack);

						if (strawDollOwner.getValue() == null) {
							LOGGER.debug("Strawdoll has no owner. Skipping!");
							continue;
						}

						if (strawDollOwner.getValue() != living) {
							LOGGER.debug("Value is not a player but {}", strawDollOwner.getValue());
							continue;
						}

						if (strawDollOwner.getValue() == living) {
							LOGGER.debug("Strawdoll is soulbound to player!");
							this.spawnParticles((ServerWorld) deathCausePlayer.level, deathCausePlayer.getX(),
									deathCausePlayer.getY(), deathCausePlayer.getZ());
							this.spawnParticles((ServerWorld) strawDollOwner.getValue().level,
									strawDollOwner.getValue().getX(), strawDollOwner.getValue().getY(),
									strawDollOwner.getValue().getZ());
							LOGGER.debug("Removing straw doll from death cause player's inventory");
							deathCausePlayer.inventory.removeItem(stack);
							deadPlayerEntityStats.setStrawDoll(true);
							break;
						}

					}

				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if (event.player instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) event.player;
			if (PlayerReviveServer.getBleeding(player).isBleeding()) {
				AbilityHelper.disableAbilities(player, Integer.MAX_VALUE, abl -> true);
			}
		}
	}

	@SubscribeEvent
	public void onReviveEvent(PlayerRevivedEvent event) {
		if (event.getPlayer() instanceof ServerPlayerEntity) {
			AbilityHelper.enableAbilities(event.getPlayer(), abl -> true);
		}
	}

	@SubscribeEvent
	public void onBleedOutEvent(PlayerBleedOutEvent event) {
		if (event.getPlayer() instanceof ServerPlayerEntity) {
			AbilityHelper.enableAbilities(event.getPlayer(), abl -> true);
		}
	}

	private void spawnParticles(ServerWorld world, double posX, double posY, double posZ) {
		for (int i = 0; i < 5; i++) {
			double offsetX = WyHelper.randomDouble() / 2;
			double offsetY = WyHelper.randomDouble() / 2;
			double offsetZ = WyHelper.randomDouble() / 2;
			WyHelper.spawnParticles(ParticleTypes.DRAGON_BREATH, world, posX + offsetX, posY + offsetY, posZ + offsetZ,
					0F, 0F, 0F, 25);
		}
	}
}
