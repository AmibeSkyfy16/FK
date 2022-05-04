package ch.skyfy.fk.logic.data;

import ch.skyfy.fk.FKMod;
import lombok.Getter;
import lombok.Setter;

public class FKGameData {

    @Getter
    @Setter
    private FKMod.GameState gameState;

    @Getter
    private final ch.skyfy.fk.logic.data.TimelineData timelineData;

    public FKGameData() {
        gameState = FKMod.GameState.NOT_STARTED;
        timelineData = new ch.skyfy.fk.logic.data.TimelineData(1, 0, 0);
    }
}
