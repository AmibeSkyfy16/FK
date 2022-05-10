package ch.skyfy.fk.config;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.config.data.*;
import ch.skyfy.fk.config.features.VaultFeatureConfig;
import ch.skyfy.fk.json.Defaultable;
import ch.skyfy.fk.json.JsonDataClass;
import net.minecraft.util.Formatting;

import java.util.List;

/**
 * This is where the configurations are loaded
 * Each configuration has a validate method to check that the data is correct
 */
public class Configs {

    public static final JsonDataClass<WorldBorderConfig, WorldBorderConfigDefault> WORLD_CONFIG;
    public static final JsonDataClass<FKConfig, FKConfigDefault> FK_CONFIG;
    public static final JsonDataClass<TeamsConfig, TeamsConfigDefault> TEAMS;

    public static final JsonDataClass<VaultFeatureConfig, VaultFeatureConfigDefault> VAULT_CONFIG;

    static {
        WORLD_CONFIG = new JsonDataClass<>("worldBorderConfig.json5", WorldBorderConfig.class, WorldBorderConfigDefault.class);
        FK_CONFIG = new JsonDataClass<>("fkconfig.json5", FKConfig.class, FKConfigDefault.class);
        TEAMS = new JsonDataClass<>("teams.json5", TeamsConfig.class, TeamsConfigDefault.class);
        VAULT_CONFIG = new JsonDataClass<>("features\\vault.json5", VaultFeatureConfig.class, VaultFeatureConfigDefault.class);

        WORLD_CONFIG.data.validate();
        FK_CONFIG.data.validate();
        TEAMS.data.validate();
        VAULT_CONFIG.data.validate();

        FKMod.LOGGER.info(Configs.class.getName() + " has been loaded");
    }

    public static class WorldBorderConfigDefault implements Defaultable<WorldBorderConfig> {

        @Override
        public WorldBorderConfig getDefault() {
            // Config for FallenKingRun12 map
            return new WorldBorderConfig(new WorldBorderData("minecraft:overworld",
                    new Cube((short) 210, 64, 319, 0, 0, 0))
            );
        }
    }

    public static class TeamsConfigDefault implements Defaultable<TeamsConfig> {

        @Override
        public TeamsConfig getDefault() {
            // Teams for FallenKingRun12 map
            return new TeamsConfig(List.of(
                    new FKTeam("Yellow team", Formatting.YELLOW.name(), List.of("Alex"),
                            new Base("Yellow base",
                                    new Cube((short) 14, 50, 500, -64, 100, -113),
                                    new Cube((short) 34, 50, 500, -64, 100, -113),
                                    new SpawnLocation("minecraft:overworld", -64.6, 107, -113.4, 2f, 38f)
                            )
                    ),
                    new FKTeam("Purple team", Formatting.DARK_PURPLE.name(), List.of("Skyfy16"),
                            new Base("Purple base",
                                    new Cube((short) 14, 50, 500, 121, 99, -19),
                                    new Cube((short) 55, 50, 500, 121, 99, -19),
                                    new SpawnLocation("minecraft:overworld", 121.5, 106, -19.3, 90f, 69f)
                            )
                    )
            ));
        }
    }

    public static class FKConfigDefault implements Defaultable<FKConfig> {

        @Override
        public FKConfig getDefault() {
            // config for FallenKingRun12 map
            return new FKConfig(
                    5,
                    2,
                    3,
                    2,
                    20,
                    false,
                    new WaitingRoom(
                            new Cube((short) 22, 250, 255, 0, 101, 4),
                            new SpawnLocation("minecraft:overworld", 0, 101, 16, 180, 2.8f)
                    ),
                    new SpawnLocation("minecraft:overworld", 0, 123, 7, 88, 88)
            );
        }
    }

    public static class VaultFeatureConfigDefault implements Defaultable<VaultFeatureConfig> {

        @Override
        public VaultFeatureConfig getDefault() {
            return new VaultFeatureConfig(
                    true,
                    4,
                    4,
                    2,
                    20
            );
        }
    }
}
