package ch.skyfy.fk.config;


import ch.skyfy.fk.config.data.Cube;
import ch.skyfy.fk.config.data.WorldBorderData;
import ch.skyfy.fk.json.Validatable;
import lombok.Getter;

import java.util.ArrayList;

public class WorldBorderConfig implements Validatable {

    @Getter
    private final WorldBorderData worldBorderData;

    public WorldBorderConfig(WorldBorderData worldBorderData) {
        this.worldBorderData = worldBorderData;
    }

    public WorldBorderConfig() {
        worldBorderData = new WorldBorderData("minecraft:overworld", new Cube((short)500,64, 319, 0, 0, 0));
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();

        confirmValidate(errors);
    }
}
