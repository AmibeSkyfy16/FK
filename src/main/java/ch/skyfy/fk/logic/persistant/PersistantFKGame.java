package ch.skyfy.fk.logic.persistant;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.json.Defaultable;
import ch.skyfy.fk.json.JsonData;
import ch.skyfy.fk.logic.FKGame;

/**
 * This class contain static field
 * representing data that are saved in a json file.
 *
 * this class is loaded by reflection in a static block inside FKGame class.
 *
 * @see FKGame
 */
public class PersistantFKGame {

    public static final JsonData<PersistantFKGameData, FKGameDataDefault> FK_GAME_DATA = new JsonData<>("data\\FKGameData.json", PersistantFKGameData.class, FKGameDataDefault.class);

    public static class FKGameDataDefault implements Defaultable<PersistantFKGameData> {

        @Override
        public PersistantFKGameData getDefault() {
            return new PersistantFKGameData(
                    FKMod.GameState.NOT_STARTED,
                    false,
                    new ch.skyfy.fk.logic.persistant.TimelineData(1, 0, 0, 0)
            );
        }
    }

}
