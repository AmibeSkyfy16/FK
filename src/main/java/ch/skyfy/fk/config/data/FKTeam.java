package ch.skyfy.fk.config.data;

import lombok.Getter;

import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class FKTeam {

    @Getter
    private final String name;

    @Getter
    private final String color;

    @Getter
    private final List<String> players;

    @Getter
    private final Base base;

    public FKTeam(String name, String color, List<String> players, Base base) {
        this.name = name;
        this.color = color;
        this.players = players;
        this.base = base;
    }
}
