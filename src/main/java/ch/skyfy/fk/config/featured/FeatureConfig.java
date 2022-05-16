package ch.skyfy.fk.config.featured;

import lombok.Getter;

public abstract class FeatureConfig {
    @Getter
    protected final boolean enabled;

    protected FeatureConfig(boolean enabled) {
        this.enabled = enabled;
    }
}
