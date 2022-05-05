package ch.skyfy.fk.logic.data;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.json.Validatable;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FKGameData implements Validatable {

    @Getter
    @Setter
    private FKMod.GameState gameState;

    @Getter
    private final ch.skyfy.fk.logic.data.TimelineData timelineData;

    public FKGameData(FKMod.GameState gameState, TimelineData timelineData) {
        this.gameState = gameState;
        this.timelineData = timelineData;
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();

        confirmValidate(errors);
    }
}
