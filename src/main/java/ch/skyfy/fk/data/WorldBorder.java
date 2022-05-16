package ch.skyfy.fk.data;

import lombok.Getter;

import java.util.Map;

@SuppressWarnings("ClassCanBeRecord")
public class WorldBorder {

    @Getter
    private final Map<String, Cube> spawns;

    public WorldBorder(Map<String, Cube> spawns) {
        this.spawns = spawns;
    }

}
