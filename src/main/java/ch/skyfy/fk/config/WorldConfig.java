package ch.skyfy.fk.config;


import ch.skyfy.fk.config.data.Cube;
import ch.skyfy.fk.config.data.WorldInfo;
import ch.skyfy.fk.json.Validatable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldConfig implements Validatable {

    @Getter
    private final WorldInfo worldInfo;

    public WorldConfig() {
        worldInfo = new WorldInfo("minecraft:overworld", new Cube((short)500,10, 20, 0, 0, 0));
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();

        confirmValidate(errors);
    }
}
