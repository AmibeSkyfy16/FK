package ch.skyfy.fk.config;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.config.featured.VaultFeatureConfig;
import ch.skyfy.fk.data.*;
import ch.skyfy.fk.features.vault.VaultFeature;
import ch.skyfy.fk.json.Defaultable;
import ch.skyfy.fk.json.JsonData;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.List;

/**
 * This is where the configurations are loaded
 * Each configuration has a validate method to check that the data is correct
 */
public class Configs {

    public static final JsonData<WorldBorderConfig, WorldBorderConfigDefault> WORLD_BORDER_CONFIG;
    public static final JsonData<FKConfig, FKConfigDefault> FK_CONFIG;
    public static final JsonData<FKTeamsConfig, FKTeamsConfigDefault> FK_TEAMS_CONFIG;

    public static final JsonData<VaultFeatureConfig, VaultFeatureConfigDefault> VAULT_FEATURE_CONFIG;

    static {
        WORLD_BORDER_CONFIG = new JsonData<>("worldBorderConfig.json5", WorldBorderConfig.class, WorldBorderConfigDefault.class);
        FK_CONFIG = new JsonData<>("fkconfig.json5", FKConfig.class, FKConfigDefault.class);
        FK_TEAMS_CONFIG = new JsonData<>("teams.json5", FKTeamsConfig.class, FKTeamsConfigDefault.class);
        VAULT_FEATURE_CONFIG = new JsonData<>("features\\vault.json5", VaultFeatureConfig.class, VaultFeatureConfigDefault.class);

        WORLD_BORDER_CONFIG.data.validate();
        FK_CONFIG.data.validate();
        FK_TEAMS_CONFIG.data.validate();
        VAULT_FEATURE_CONFIG.data.validate();

        FKMod.LOGGER.info(Configs.class.getName() + " has been loaded");
    }

    public static class WorldBorderConfigDefault implements Defaultable<WorldBorderConfig> {

        @Override
        public WorldBorderConfig getDefault() {
            // Config for FallenKingRun12 map
            var map = new HashMap<String, Cube>();
            map.put("minecraft:overworld", new Cube((short) 210, 64, 319, 0, 0, 0));
            map.put("minecraft:the_nether", new Cube((short) 300, 64, 319, 0, 0, 0));
            map.put("minecraft:the_end", new Cube((short) 1000, 64, 319, 0, 0, 0));
            return new WorldBorderConfig(new WorldBorder(map));
        }
    }

    public static class FKTeamsConfigDefault implements Defaultable<FKTeamsConfig> {

        @Override
        public FKTeamsConfig getDefault() {
            // Teams for FallenKingRun12 map
            return new FKTeamsConfig(List.of(
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
                    false,
                    new WaitingRoom(
                            new Cube((short) 22, 250, 255, 0, 101, 4),
                            new SpawnLocation("minecraft:overworld", 0, 101, 16, 180, 2.8f)
                    )
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
                    20,
                    VaultFeature.Mode.NORMAL
            );
        }
    }
}
