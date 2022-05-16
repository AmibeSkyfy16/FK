package ch.skyfy.fk.logic.persistant;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.json.Validatable;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains data saved and loaded through a file called FKGameData.json
 * @see PersistantFKGame
 */
public class PersistantFKGameData implements Validatable {

    @Getter
    @Setter
    private FKMod.GameState gameState;

    @Getter
    private final ch.skyfy.fk.logic.persistant.TimelineData timelineData;

    public PersistantFKGameData(FKMod.GameState gameState, TimelineData timelineData) {
        this.gameState = gameState;
        this.timelineData = timelineData;
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();
        validateNonNull(errors);
        confirmValidate(errors);
    }

    @Override
    public void validatePrimitivesType(List<String> errors) {

    }
}
