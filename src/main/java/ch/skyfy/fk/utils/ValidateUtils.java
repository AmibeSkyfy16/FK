package ch.skyfy.fk.utils;

import ch.skyfy.fk.config.data.Cube;

import java.util.List;
import java.util.function.Supplier;

public class ValidateUtils {

    public static void checkForNegativeValueInCubeClass(Cube cube, List<String> errors){
        if(cube.getSize() <= 0)
            errors.add("The size of the base is "+cube.getSize()+", this is not a correct value");
        if(cube.getNumberOfBlocksDown() <= 0) errors.add("The numberOfBlocksDown of the base is "+cube.getNumberOfBlocksDown()+", this is not a correct value");
        if(cube.getNumberOfBlocksUp() <= 0) errors.add("The numberOfBlocksUp of the base is "+cube.getNumberOfBlocksUp()+", this is not a correct value");

    }

}
