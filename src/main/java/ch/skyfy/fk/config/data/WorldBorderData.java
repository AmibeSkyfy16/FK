package ch.skyfy.fk.config.data;

import lombok.Getter;

@SuppressWarnings("ClassCanBeRecord")
public class WorldBorderData {

    @Getter
    private final String dimensionName;

    @Getter
    private final Cube cube;

    public WorldBorderData(String dimensionName, Cube cube) {
        this.dimensionName = dimensionName;
        this.cube = cube;
    }

}
