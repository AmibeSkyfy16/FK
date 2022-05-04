package ch.skyfy.fk.config;


import ch.skyfy.fk.config.data.Cube;
import ch.skyfy.fk.config.data.WorldInfo;
import ch.skyfy.fk.json.Validable;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

public class WorldConfig implements Validable {

    @Getter
    private final WorldInfo worldInfo;

    public WorldConfig() {
        worldInfo = new WorldInfo("minecraft:overworld", new Cube((short)200,500, 500, 0, -33, 0));
    }

    @Override
    public List<String> validate() {
        return Collections.emptyList();
    }
}
