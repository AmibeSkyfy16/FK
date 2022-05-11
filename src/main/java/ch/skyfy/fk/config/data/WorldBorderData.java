package ch.skyfy.fk.config.data;

import lombok.Getter;

import java.util.Map;

@SuppressWarnings("ClassCanBeRecord")
public class WorldBorderData {

    @Getter
    private final Map<String, Cube> spawns;

    public WorldBorderData(Map<String, Cube> spawns) {
        this.spawns = spawns;
    }

}
