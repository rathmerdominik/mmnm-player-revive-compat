package net.hammerclock.mmnmrevive;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.VersionChecker.CheckResult;
import net.minecraftforge.fml.VersionChecker.Status;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.network.FMLNetworkConstants;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PlayerReviveCompatMod.PROJECT_ID)
public class PlayerReviveCompatMod
{
	private static final Logger LOGGER = LogManager.getLogger(PlayerReviveCompatMod.PROJECT_ID);

	public static final String PROJECT_ID = "mmnmrevive";

	@SuppressWarnings("java:S1118")
	// java:S1118 Forge needs this to be public
	public PlayerReviveCompatMod() {
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
			() -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

		MinecraftForge.EVENT_BUS.register(this);

		MinecraftForge.EVENT_BUS.addListener(PlayerReviveCompatMod::onServerStarted);
	}

	private static void onServerStarted(FMLServerStartedEvent event) {
		CheckResult result = VersionChecker.getResult(ModList.get().getModContainerById(PROJECT_ID).orElseThrow(IllegalArgumentException::new).getModInfo());
		if(result.status == Status.OUTDATED) {
			LOGGER.warn("YOUR MOD IS OUTDATED. The latest version is {}. Please get the latest version here: {}", result.target, result.url);
		}
		LOGGER.info("Player Revive Compatibility Mod Started!");
	}
}
