package ch.skyfy.fk.config.data;

import lombok.Getter;

@SuppressWarnings({"ClassCanBeRecord"})
public class Base {

    @Getter
    private final String name;

    @Getter
    private final Cube cube;

    public Base(String name, Cube cube) {
        this.name = name;
        this.cube = cube;
    }

}
