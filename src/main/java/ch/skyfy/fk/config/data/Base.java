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

    public Base(String name, Cube cube, Cube proximityCube) {
        this.name = name;
        this.cube = cube;
        this.proximityCube = proximityCube;
    }

}
