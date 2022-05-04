package ch.skyfy.fk.config.data;

import lombok.Getter;

@SuppressWarnings("ClassCanBeRecord")
public class WaitingRoom {

    @Getter
    private final Cube cube;

    @Getter
    private final SpawnLocation spawnLocation;

    public WaitingRoom(Cube cube, SpawnLocation spawnLocation) {
        this.cube = cube;
        this.spawnLocation = spawnLocation;
    }
}
