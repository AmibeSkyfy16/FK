package ch.skyfy.fk.utils;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.exceptions.FKModException;

public class ReflectionUtils {

    public static void loadConfigByReflection(Class<?>[] classesToLoad) {
        for (var config : classesToLoad) {
            var canonicalName = config.getCanonicalName();
            try {
                Class.forName(canonicalName);
            } catch (ClassNotFoundException e) {
                FKMod.LOGGER.fatal("A FATAL ERROR OCCURRED WITH FKMod");
                throw new FKModException(e);
            }
        }
    }

}
