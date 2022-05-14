package ch.skyfy.fk.config.features;

import ch.skyfy.fk.features.VaultFeature;
import ch.skyfy.fk.json.Validatable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class VaultFeatureConfig extends FeatureConfig implements Validatable {

    @Getter
    private final int minWidth;
    @Getter
    private final int minLength;
    @Getter
    private final int minHeight;
    @Getter
    private final int maximumNumberOfBlocksDown;

    @Getter
    private final VaultFeature.Mode mode;

    public VaultFeatureConfig(boolean enabled, int minWidth, int minLength, int minHeight, int maximumNumberOfBlocksDown, VaultFeature.Mode mode) {
        super(enabled);
        this.minWidth = minWidth;
        this.minLength = minLength;
        this.minHeight = minHeight;
        this.maximumNumberOfBlocksDown = maximumNumberOfBlocksDown;
        this.mode = mode;
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();
        validatePrimitivesType(errors);
        confirmValidate(errors);
    }

    @Override
    public void validatePrimitivesType(List<String> errors) {
        if (minWidth < 0) errors.add("minWidth value is currently " + minWidth + ". It must be greater than 0 !");
        if (minLength < 0) errors.add("minLength value is currently " + minLength + ". It must be greater than 0 !");
        if (minHeight < 0) errors.add("minHeight value is currently " + minHeight + ". It must be greater than 0 !");
        if (maximumNumberOfBlocksDown < 0) errors.add("maximumNumberOfBlocksDown value is currently " + maximumNumberOfBlocksDown + ". It must be greater than 0 !");
    }

}
