package ch.skyfy.fk.config.features;

import ch.skyfy.fk.json.Validatable;

import java.util.ArrayList;

public class ChestRoomFeatureConfig extends FeatureConfig implements Validatable {

    public ChestRoomFeatureConfig(boolean enabled) {
        super(enabled);
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();
        confirmValidate(errors);
    }

}
