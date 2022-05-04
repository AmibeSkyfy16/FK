package ch.skyfy.fk.config.data;

import lombok.Getter;

@SuppressWarnings("ClassCanBeRecord")
public class WorldInfo {

    @Getter
    private final String dimensionName;

    @Getter
    private final Cube worldDimension;

    public WorldInfo(String dimensionName, Cube worldDimension) {
        this.dimensionName = dimensionName;
        this.worldDimension = worldDimension;
    }

}
