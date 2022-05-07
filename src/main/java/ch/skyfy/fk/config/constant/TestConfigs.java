package ch.skyfy.fk.config.constant;

import ch.skyfy.fk.config.FKConfig;
import ch.skyfy.fk.config.TeamsConfig;
import ch.skyfy.fk.config.WorldBorderConfig;
import ch.skyfy.fk.config.data.*;
import ch.skyfy.fk.config.features.ChestRoomFeatureConfig;
import net.minecraft.util.Formatting;

import java.util.List;

@SuppressWarnings("unused")
public class TestConfigs {

    public static final FKConfig TEST_FKCONFIG = new FKConfig(
            6,
            3,
            3,
            2,
            false,
            new WaitingRoom(
                    new Cube((short) 4, 50, 80, 0, 63, 0),
                    new SpawnLocation("minecraft:overworld", 0, -34, 0, 8, 8)
            ),
            new SpawnLocation("minecraft:overworld", 50, -34, 50, 8, 8)
    );

    public static final TeamsConfig TEST_TEAMS_CONFIG = new TeamsConfig(List.of(
            new FKTeam("The_Green_Team", Formatting.GREEN.name(), List.of("Skyfy16"),
                    new Base("The_Green_Base",
                            new Cube((short) 9, 40, 500, 20, -34, 20),
                            new Cube((short) 14, 5, 5, 20, -34, 20),
                            new SpawnLocation("minecraft:overworld", 20, -31, 20, 8f, 8f)
                    )
            ),
            new FKTeam("The_Red_Team", Formatting.RED.name(), List.of("AmibeSkyfy16"),
                    new Base("The_Red_Base",
                            new Cube((short) 9, 40, 500, -20, -34, -20),
                            new Cube((short) 14, 5, 5, -20, -34, -20),
                            new SpawnLocation("minecraft:overworld", -20, -31, -20, 8f, 8f)
                    )
            )
    ));

    public static final WorldBorderConfig TEST_WORLD_INFO_CONFIG = new WorldBorderConfig(new WorldBorderData("minecraft:overworld",
            new Cube((short) 500, 64, 319, 0, 0, 0))
    );

    public static final ChestRoomFeatureConfig CHEST_ROOM_CONFIG = new ChestRoomFeatureConfig(
            true,
            4,
            4,
            2,
            20
    );

}
