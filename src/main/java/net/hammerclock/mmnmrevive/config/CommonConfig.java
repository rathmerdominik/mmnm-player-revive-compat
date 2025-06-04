package net.hammerclock.mmnmrevive.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.hammerclock.mmnmrevive.PlayerReviveCompatMod;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CommonConfig {
    public static final Path CONFIG_PATH = Paths.get("config", PlayerReviveCompatMod.CONFIG_NAME);
    public static final CommonConfig INSTANCE;
    public static final ForgeConfigSpec CONFIG;

    private final ForgeConfigSpec.BooleanValue reviveAllowedInChallenge;
    private final ForgeConfigSpec.BooleanValue enableStrawDollReturn;
    private final ForgeConfigSpec.BooleanValue knockdownPreferred;
    private final ForgeConfigSpec.BooleanValue heartInstantDeath;
    private final ForgeConfigSpec.BooleanValue giveKnockdownEffect;

    static {
        Pair<CommonConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);

        CONFIG = pair.getRight();
        INSTANCE = pair.getLeft();

        CommentedFileConfig file = CommentedFileConfig
                .builder(CONFIG_PATH)
                .sync()
                .autoreload()
                .writingMode(WritingMode.REPLACE)
                .build();

        file.load();
        file.save();

        CONFIG.setConfig(file);
    }

    public CommonConfig(ForgeConfigSpec.Builder builder) {
        this.reviveAllowedInChallenge = builder
                .comment("If false will respawn players normally back into the overworld. If true will allow players to revive others in challenge dimensions")
                .define("Allow reviving in challenge dimensions ", false);

        this.enableStrawDollReturn = builder
                .comment("Will remove the strawdoll of a player when they are bleeding out opposed to them having to fully die")
                .define("Enable Straw Doll Return", true);

        this.knockdownPreferred = builder
                .comment("Will make the player be knocked down instead of dying when they are bleeding out")
                .define("Knockdown Preferred", true);

        this.heartInstantDeath = builder
                .comment("Will make the player die instantly when their heart is squashed instead of making them bleed out")
                .define("Heart Instant Death", true);

        this.giveKnockdownEffect = builder
                .comment("Will give the player the knockdown effect when bleeding out which allows players to carry them")
                .define("Give Knockdown Effect on bleeding", false);
    }

    public boolean isReviveAllowedInChallenge() {
        return this.reviveAllowedInChallenge.get();
    }

    public boolean isEnableStrawDollReturn() {
        return this.enableStrawDollReturn.get();
    }

    public boolean isKnockDownPreferred() {
        return this.knockdownPreferred.get();
    }

    public boolean isHeartDamageInstantDeath() {
        return this.heartInstantDeath.get();
    }

    public boolean isGiveKnockdownEffect() {
        return this.giveKnockdownEffect.get();
    }
}
