package ch.skyfy.fk.config.actions;

import ch.skyfy.fk.constants.Where;
import ch.skyfy.fk.json.Validatable;

import java.util.List;
import java.util.Map;

public class EmptyingBucketConfig extends AbstractPlayerActionConfig implements Validatable {

    protected EmptyingBucketConfig(Map<String, Map<Where, List<String>>> allowed) {
        super(allowed);
    }

    @Override
    public void validate() {

    }

    @Override
    public void validatePrimitivesType(List<String> errors) {

    }
}
