package ch.skyfy.fk.config;


import ch.skyfy.fk.data.WorldBorder;
import ch.skyfy.fk.json.Validatable;
import ch.skyfy.fk.utils.ValidateUtils;
import lombok.Getter;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class WorldBorderConfig implements Validatable {

    @Getter
    private final WorldBorder worldBorder;

    public WorldBorderConfig(WorldBorder worldBorder) {
        this.worldBorder = worldBorder;
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();

        validateNonNull(errors);
        validatePrimitivesType(errors);

        worldBorder.getSpawns().forEach((s, cube) -> {
            if(!Identifier.isValid(s))
                errors.add("dimensionName " + s + " is not a valid dimension name");
        });


        confirmValidate(errors);
    }

    @Override
    public void validatePrimitivesType(List<String> errors) {
        worldBorder.getSpawns().forEach((s, cube) -> ValidateUtils.checkForNegativeValueInCubeClass(cube, errors));
    }
}
