package ch.skyfy.fk.utils;

import ch.skyfy.fk.data.Cube;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ValidateUtils {

    public static void checkForNegativeValueInCubeClass(Cube cube, List<String> errors){
        if(cube.getSize() <= 0) errors.add("The size of the base is "+cube.getSize()+", this is not a correct value");
        if(cube.getNumberOfBlocksDown() <= 0) errors.add("The numberOfBlocksDown of the base is "+cube.getNumberOfBlocksDown()+", this is not a correct value");
        if(cube.getNumberOfBlocksUp() <= 0) errors.add("The numberOfBlocksUp of the base is "+cube.getNumberOfBlocksUp()+", this is not a correct value");
    }

    public static void traverse(@NotNull Object obj, List<String> errors) {
        for (var clazz = obj.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            if(clazz.isPrimitive() || !clazz.getPackage().getName().startsWith("ch.skyfy")) return;
            for (var field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    var next = field.get(obj);
                    var msg = field.getName() + " of class "+clazz.getCanonicalName()+" should not be null";
                    if(next == null) errors.add(msg);
                    Objects.requireNonNull(next, msg);
                    traverse(next, errors);
                } catch (IllegalAccessException ignored) {}
            }
        }
    }

}
