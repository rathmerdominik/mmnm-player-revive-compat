package net.hammerclock.mmnmrevive.config;

import net.hammerclock.mmnmrevive.PlayerReviveCompatMod;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.tuple.Pair;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

public class CommonConfig {
    public static final Path CONFIG_PATH = Paths.get("config", PlayerReviveCompatMod.CONFIG_NAME);
	public static final CommonConfig INSTANCE;
	public static final ForgeConfigSpec CONFIG;

    private final ForgeConfigSpec.BooleanValue challengeImmediateDeath;
	private final ForgeConfigSpec.BooleanValue enableStrawDollReturn;
	private final ForgeConfigSpec.BooleanValue knockdownPreferred;
	private final ForgeConfigSpec.BooleanValue heartInstantDeath;

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
		this.challengeImmediateDeath = builder
				.comment("Will restore vanilla Mine Mine no Mi behaviour by bringing you back to the surface when you die in a challenge dimension")
				.define("Instantly kill in challenge dimensions", false);

		this.enableStrawDollReturn = builder
				.comment("Will remove the strawdoll of a player when they are downed instead of fully dead")
				.define("Enable Straw Doll Return", true);

		this.knockdownPreferred = builder
				.comment("Will make the player be knocked down instead of dying when they are downed")
				.define("Knockdown Preferred", true);

		this.heartInstantDeath = builder
				.comment("Will make the player die instantly when their heart is squashed instead of downing them")
				.define("Heart Instant Death", true);
	}

    public boolean isChallengeImmediateDeath() {
        return this.challengeImmediateDeath.get();
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
}
