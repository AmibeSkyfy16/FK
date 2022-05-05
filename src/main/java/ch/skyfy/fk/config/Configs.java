package ch.skyfy.fk.config;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.config.constant.TestConfigs;
import ch.skyfy.fk.json.JsonDataClass;

public class Configs {

//    public static final JsonDataClass<FKConfig> FK_CONFIG = new JsonDataClass<>("fkconfig.json5", FKConfig.class, DefaultConfigs.DEFAULT_FKCONFIG);
    public static final JsonDataClass<FKConfig> FK_CONFIG = new JsonDataClass<>("fkconfig.json5", FKConfig.class, TestConfigs.TEST_FKCONFIG);
//    public static final JsonDataClass<TeamsConfig> TEAMS = new JsonDataClass<>("teams.json5", TeamsConfig.class, DefaultConfigs.DEFAULT_TEAMS_CONFIG);
    public static final JsonDataClass<TeamsConfig> TEAMS = new JsonDataClass<>("teams.json5", TeamsConfig.class, TestConfigs.TEST_TEAMS_CONFIG);
//    public static final JsonDataClass<WorldConfig> WORLD_CONFIG = new JsonDataClass<>("worldconfig.json5", WorldConfig.class, DefaultConfigs.DEFAULT_WORLD_INFO_CONFIG);
    public static final JsonDataClass<WorldBorderConfig> WORLD_CONFIG = new JsonDataClass<>("worldBorderConfig.json5", WorldBorderConfig.class, TestConfigs.TEST_WORLD_INFO_CONFIG);

    static {
        FKMod.LOGGER.info(Configs.class.getName() + " has been loaded");
    }

}
