package ch.skyfy.fk.utils;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.exceptions.FKModException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {

    public static void loadClassesByReflection(Class<?>[] classesToLoad) {
        for (var config : classesToLoad) {
            var canonicalName = config.getCanonicalName();
            try {
                Class.forName(canonicalName);
            } catch (ClassNotFoundException e) {
                FKMod.LOGGER.fatal("A FATAL ERROR OCCURRED WITH FKMod");
                FKMod.LOGGER.fatal("A FATAL ERROR OCCURRED WITH FKMod");
                FKMod.LOGGER.fatal("A FATAL ERROR OCCURRED WITH FKMod");
                FKMod.LOGGER.fatal("A FATAL ERROR OCCURRED WITH FKMod");
                throw new FKModException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T, TYPE> List<String> getListOfTranslationKey(Class<T> tClass, Class<TYPE> typeClass) {
        var list = new ArrayList<String>();
        for (var field : tClass.getDeclaredFields()) {
            if (field.getType() == typeClass) {
                try {
                    var block = (TYPE) field.get(null);
                    var method = typeClass.getMethod("method_9539"); // yarn mapping for getTranslationKey() method
                    list.add((String) method.invoke(block));
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ignored) {
                }
            }
        }
        return list;
    }

}
