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

    private ForgeConfigSpec.BooleanValue challengeImmediateDeath;




	static {
		Pair<CommonConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);

		CONFIG = pair.getRight();
		INSTANCE = pair.getLeft();

		CommentedFileConfig file = CommentedFileConfig.builder(CONFIG_PATH)
                                                        .sync()
                                                        .autoreload()
                                                        .writingMode(WritingMode.REPLACE)
                                                        .build();

		file.load();
		file.save();

		CONFIG.setConfig(file);
	}

    public CommonConfig(ForgeConfigSpec.Builder builder) {
		this.challengeImmediateDeath = builder.define("Instantly kill in challenge dimensions", false);
	}

    public boolean isChallengeImmediateDeath() {
        return this.challengeImmediateDeath.get();
    }
}
