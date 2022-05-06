package ch.skyfy.fk.config.data;

import lombok.Getter;

@SuppressWarnings({"ClassCanBeRecord"})
public class Base {

    @Getter
    private final String name;

    @Getter
    private final Cube cube;

    @Getter
    private final Cube proximityCube;

    @Getter
    private final SpawnLocation spawnLocation;

    public Base(String name, Cube cube, Cube proximityCube, SpawnLocation spawnLocation) {
        this.name = name;
        this.cube = cube;
        this.proximityCube = proximityCube;
        this.spawnLocation = spawnLocation;
    }

}
