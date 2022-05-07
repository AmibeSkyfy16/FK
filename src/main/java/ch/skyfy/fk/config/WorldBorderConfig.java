package ch.skyfy.fk.config;


import ch.skyfy.fk.config.data.WorldBorderData;
import ch.skyfy.fk.json.Validatable;
import ch.skyfy.fk.utils.ValidateUtils;
import lombok.Getter;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class WorldBorderConfig implements Validatable {

    @Getter
    private final WorldBorderData worldBorderData;

    public WorldBorderConfig(WorldBorderData worldBorderData) {
        this.worldBorderData = worldBorderData;
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();

        validateNonNull(errors);
        validatePrimitivesType(errors);

        if(!Identifier.isValid(worldBorderData.getDimensionName()))
            errors.add("dimensionName " + worldBorderData.getDimensionName() + " is not a valid dimension name");

        confirmValidate(errors);
    }

    @Override
    public void validatePrimitivesType(List<String> errors) {
        ValidateUtils.checkForNegativeValueInCubeClass(worldBorderData.getCube(), errors);
    }
}
