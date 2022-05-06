package ch.skyfy.fk.features;

import ch.skyfy.fk.config.Configs;
import ch.skyfy.fk.config.features.ChestRoomFeatureConfig;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class ChestRoomFeature {

    private final ChestRoomFeatureConfig config = Configs.CHEST_ROOM_CONFIG.config;

    public ChestRoomFeature() {


        if(!config.isEnabled())return;

        registerEvents();

        // TODO Chest Room detection, elliminate if none chest room built when assault day is enabled, create chest room
    }

    private void registerEvents(){

    }
}
