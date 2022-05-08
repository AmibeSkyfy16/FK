package ch.skyfy.fk.config.constant;

import ch.skyfy.fk.config.FKConfig;
import ch.skyfy.fk.config.TeamsConfig;
import ch.skyfy.fk.config.WorldBorderConfig;
import ch.skyfy.fk.config.data.*;
import ch.skyfy.fk.config.features.VaultFeatureConfig;
import net.minecraft.util.Formatting;

import java.util.List;

public class FallenKingRun12Config {

    public static final FKConfig FKCONFIG = new FKConfig(
            5,
            2,
            3,
            2,
            false,
            new WaitingRoom(
                    new Cube((short) 22, 250, 255, 0, 101, 4),
                    new SpawnLocation("minecraft:overworld", 0, 101, 16, 180, 2.8f)
            ),
            new SpawnLocation("minecraft:overworld", 0, 123, 7, 88, 88)
    );

    public static final TeamsConfig TEAMS = new TeamsConfig(List.of(
            new FKTeam("Yellow team", Formatting.YELLOW.name(), List.of("Alex"),
                    new Base("Yellow base",
                            new Cube((short) 14, 50, 500, -64, 100, -113),
                            new Cube((short)34, 50, 500, -64, 100, -113),
                            new SpawnLocation("minecraft:overworld",-64.6, 107, -113.4, 2f, 38f)
                    )
            ),
            new FKTeam("Purple team", Formatting.DARK_PURPLE.name(), List.of("Skyfy16"),
                    new Base("Purple base",
                            new Cube((short) 14, 50, 500, 121, 99, -19),
                            new Cube((short) 55, 50, 500, 121, 99, -19),
                            new SpawnLocation("minecraft:overworld",121.5, 106, -19.3, 90f, 69f)
                    )
            )
    ));

    public static final WorldBorderConfig WORLD_BORDER_CONFIG = new WorldBorderConfig(new WorldBorderData("minecraft:overworld",
            new Cube((short) 210, 64, 319, 0, 0, 0))
    );

    public static final VaultFeatureConfig CHEST_ROOM_CONFIG = new VaultFeatureConfig(
            true,
            4,
            4,
            2,
            20
    );

}
