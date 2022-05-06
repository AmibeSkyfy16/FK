package ch.skyfy.fk.config;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.config.constant.DefaultConfigs;
import ch.skyfy.fk.config.constant.TestConfigs;
import ch.skyfy.fk.json.JsonDataClass;

/**
 * This is where the configurations are loaded
 * Each configuration has a validate method to check that the data is correct
 *
 * IMPORTANT: The order in which the variables are initialized must not be changed
 */
@SuppressWarnings("CommentedOutCode")
public class Configs {

    public static final JsonDataClass<WorldBorderConfig> WORLD_CONFIG;

    public static final JsonDataClass<FKConfig> FK_CONFIG;
    public static final JsonDataClass<TeamsConfig> TEAMS;

    static {

        // Test configs
        WORLD_CONFIG = new JsonDataClass<>("worldBorderConfig.json5", WorldBorderConfig.class, TestConfigs.TEST_WORLD_INFO_CONFIG);
        FK_CONFIG = new JsonDataClass<>("fkconfig.json5", FKConfig.class, TestConfigs.TEST_FKCONFIG);
        TEAMS = new JsonDataClass<>("teams.json5", TeamsConfig.class, TestConfigs.TEST_TEAMS_CONFIG);

        // The default config
//        WORLD_CONFIG = new JsonDataClass<>("worldBorderConfig.json5", WorldBorderConfig.class, DefaultConfigs.DEFAULT_WORLD_INFO_CONFIG);
//        FK_CONFIG = new JsonDataClass<>("fkconfig.json5", FKConfig.class,  DefaultConfigs.DEFAULT_FKCONFIG);
//        TEAMS = new JsonDataClass<>("teams.json5", TeamsConfig.class, DefaultConfigs.DEFAULT_TEAMS_CONFIG);

        FKMod.LOGGER.info(Configs.class.getName() + " has been loaded");
    }

}
