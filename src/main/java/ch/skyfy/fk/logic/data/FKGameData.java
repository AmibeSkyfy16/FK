package ch.skyfy.fk.logic.data;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.json.Validable;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

public class FKGameData implements Validable {

    @Getter
    @Setter
    private FKMod.GameState gameState;

    @Getter
    private final ch.skyfy.fk.logic.data.TimelineData timelineData;

    public FKGameData() {
        gameState = FKMod.GameState.NOT_STARTED;
        timelineData = new ch.skyfy.fk.logic.data.TimelineData(1, 0, 0);
    }

    @Override
    public List<String> validate() {
        return Collections.emptyList();
    }
}
