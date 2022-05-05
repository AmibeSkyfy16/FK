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

    public FKGameData() {
        gameState = FKMod.GameState.NOT_STARTED;
        timelineData = new ch.skyfy.fk.logic.data.TimelineData(1, 0, 0);
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();

        confirmValidate(errors);
    }
}
