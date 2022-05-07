package ch.skyfy.fk.config.features;

import ch.skyfy.fk.json.Validatable;
import lombok.Getter;

import java.util.ArrayList;

public class ChestRoomFeatureConfig extends FeatureConfig implements Validatable {

    @Getter
    private final int minWidth;

    @Getter
    private final int minHeight;

    @Getter
    private final int minLength;

    @Getter
    private final int maximumNumberOfBlocksDown;

    public ChestRoomFeatureConfig(boolean enabled, int minWidth, int minHeight, int minLength, int maximumNumberOfBlocksDown) {
        super(enabled);
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        this.minLength = minLength;
        this.maximumNumberOfBlocksDown = maximumNumberOfBlocksDown;
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();
        confirmValidate(errors);
    }

}
