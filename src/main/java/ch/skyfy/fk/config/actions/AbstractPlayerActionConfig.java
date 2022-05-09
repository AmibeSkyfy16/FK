package ch.skyfy.fk.config.actions;

import ch.skyfy.fk.constants.Where;
import lombok.Getter;

import java.util.List;
import java.util.Map;

public class AbstractPlayerActionConfig {

    @Getter
    protected final Map<String, Map<Where, List<String>>> allowed;

    protected AbstractPlayerActionConfig(Map<String, Map<Where, List<String>>> allowed) {
        this.allowed = allowed;
    }

}
