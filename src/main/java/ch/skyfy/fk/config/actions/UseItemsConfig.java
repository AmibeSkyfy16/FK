package ch.skyfy.fk.config.actions;

import ch.skyfy.fk.constants.Where;
import ch.skyfy.fk.json.Validatable;

import java.util.List;
import java.util.Map;

public class UseItemsConfig extends AbstractPlayerActionConfig implements Validatable {

    public UseItemsConfig(Map<String, Map<Where, List<String>>> allowed, Map<String, Map<Where, List<String>>> denied) {
        super(allowed, denied);
    }

    @Override
    public void validate() {

    }

    @Override
    public void validatePrimitivesType(List<String> errors) {

    }

}
