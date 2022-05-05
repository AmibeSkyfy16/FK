package ch.skyfy.fk.logic.data;


import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.json.JsonDataClass;

public class FKGameAllData {

    public static final JsonDataClass<FKGameData> FK_GAME_DATA = new JsonDataClass<>(
            "data\\FKGameData.json",
            FKGameData.class, new FKGameData(
            FKMod.GameState.NOT_STARTED,
            new ch.skyfy.fk.logic.data.TimelineData(1, 0, 0)
    ));

}
