package ch.skyfy.fk.config;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.json.JsonDataClass;

public class Configs {

    public static final JsonDataClass<FKConfig> FK_CONFIG = new JsonDataClass<>("fkconfig.json5", FKConfig.class);
    public static final JsonDataClass<TeamsConfig> TEAMS = new JsonDataClass<>("teams.json5", TeamsConfig.class);
    public static final JsonDataClass<WorldConfig> WORLD_CONFIG = new JsonDataClass<>("worldconfig.json5", WorldConfig.class);

    static {
        FKMod.LOGGER.info(Configs.class.getName() + " has been loaded");
    }

}
