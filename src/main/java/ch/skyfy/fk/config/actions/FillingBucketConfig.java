package ch.skyfy.fk.config.actions;

import ch.skyfy.fk.constants.Where;
import ch.skyfy.fk.json.Validatable;

import java.util.List;
import java.util.Map;

public class FillingBucketConfig extends AbstractPlayerActionConfig implements Validatable {

    protected FillingBucketConfig(Map<String, Map<Where, List<String>>> allowed) {
        super(allowed);
    }

    @Override
    public void validate() {

    }

    @Override
    public void validatePrimitivesType(List<String> errors) {

    }
}
