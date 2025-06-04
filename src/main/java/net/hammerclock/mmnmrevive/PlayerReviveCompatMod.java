package net.hammerclock.mmnmrevive;

import net.hammerclock.mmnmrevive.config.CommonConfig;
import net.hammerclock.mmnmrevive.packets.client.CRemoveHelper;
import net.hammerclock.mmnmrevive.packets.client.CStartCarry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.VersionChecker.CheckResult;
import net.minecraftforge.fml.VersionChecker.Status;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PlayerReviveCompatMod.PROJECT_ID)
public class PlayerReviveCompatMod {
    private static final Logger LOGGER = LogManager.getLogger(PlayerReviveCompatMod.PROJECT_ID);

    public static final String CONFIG_NAME = "mmnm-revive-compat.toml";

    public static final String PROJECT_ID = "mmnmrevive";

    private static int packet = 0;


    public PlayerReviveCompatMod() {
        ModLoadingContext context = ModLoadingContext.get();

        context.registerConfig(ModConfig.Type.COMMON, CommonConfig.CONFIG, CONFIG_NAME);

        MinecraftForge.EVENT_BUS.addListener(PlayerReviveCompatMod::onServerStarted);

        this.registerPackages();
    }

    private static void onServerStarted(FMLServerStartedEvent event) {
        CheckResult result = VersionChecker.getResult(ModList.get().getModContainerById(PROJECT_ID).orElseThrow(IllegalArgumentException::new).getModInfo());
        if (result.status == Status.OUTDATED) {
            LOGGER.warn("YOUR MOD IS OUTDATED. The latest version is {}. Please get the latest version here: {}", result.target, result.url);
        }
        LOGGER.info("Player Revive Compatibility Mod Started!");
    }

    private void registerPackages() {
        PlayerReviveCompatPacketHandler.INSTANCE.registerMessage(packet++, CStartCarry.class, CStartCarry::encode, CStartCarry::decode, CStartCarry::handle);
        PlayerReviveCompatPacketHandler.INSTANCE.registerMessage(packet++, CRemoveHelper.class, CRemoveHelper::encode, CRemoveHelper::decode, CRemoveHelper::handle);
    }
}
