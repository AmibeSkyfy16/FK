package ch.skyfy.fk.config.data;

import lombok.Getter;

@SuppressWarnings("ClassCanBeRecord")
public class WorldInfo {

    @Getter
    private final String dimensionName;

    @Getter
    private final Cube cube;

    public WorldInfo(String dimensionName, Cube cube) {
        this.dimensionName = dimensionName;
        this.cube = cube;
    }

}
