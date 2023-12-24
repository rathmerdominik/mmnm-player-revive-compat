package net.hammerclock.mmnmrevive;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.FMLNetworkConstants;

@Mod(PlayerReviveCompatMod.PROJECT_ID)
public class PlayerReviveCompatMod
{
	public static final String PROJECT_ID = "mmnmrevive";

	@SuppressWarnings("java:S1118")
	// java:S1118 Forge needs this to be public
	public PlayerReviveCompatMod() {
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
			() -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

		MinecraftForge.EVENT_BUS.register(this);
	}
}
