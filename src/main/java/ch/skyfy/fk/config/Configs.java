package ch.skyfy.fk.config;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.config.constant.TestConfigs;
import ch.skyfy.fk.config.features.VaultFeatureConfig;
import ch.skyfy.fk.json.JsonDataClass;

/**
 * This is where the configurations are loaded
 * Each configuration has a validate method to check that the data is correct
 */
@SuppressWarnings("CommentedOutCode")
public class Configs {

    public static final JsonDataClass<WorldBorderConfig> WORLD_CONFIG;
    public static final JsonDataClass<FKConfig> FK_CONFIG;
    public static final JsonDataClass<TeamsConfig> TEAMS;

    public static final JsonDataClass<VaultFeatureConfig> VAULT_CONFIG;

    static {

        // Test configs
        WORLD_CONFIG = new JsonDataClass<>("worldBorderConfig.json5", WorldBorderConfig.class, TestConfigs.TEST_WORLD_INFO_CONFIG);
        FK_CONFIG = new JsonDataClass<>("fkconfig.json5", FKConfig.class, TestConfigs.TEST_FKCONFIG);
        TEAMS = new JsonDataClass<>("teams.json5", TeamsConfig.class, TestConfigs.TEST_TEAMS_CONFIG);

        VAULT_CONFIG = new JsonDataClass<>("features\\vault.json5", VaultFeatureConfig.class, TestConfigs.CHEST_ROOM_CONFIG);

        WORLD_CONFIG.data.validate();
        FK_CONFIG.data.validate();
        TEAMS.data.validate();
        VAULT_CONFIG.data.validate();


        // The default config
//        WORLD_CONFIG = new JsonDataClass<>("worldBorderConfig.json5", WorldBorderConfig.class, DefaultConfigs.DEFAULT_WORLD_INFO_CONFIG);
//        FK_CONFIG = new JsonDataClass<>("fkconfig.json5", FKConfig.class,  DefaultConfigs.DEFAULT_FKCONFIG);
//        TEAMS = new JsonDataClass<>("teams.json5", TeamsConfig.class, DefaultConfigs.DEFAULT_TEAMS_CONFIG);

        FKMod.LOGGER.info(Configs.class.getName() + " has been loaded");
    }

}
