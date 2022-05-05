package ch.skyfy.fk.utils;

import ch.skyfy.fk.FKMod;

public class ReflectionUtils {

    public static void loadConfigByReflection(Class<?>[] classesToLoad) {
        for (Class<?> config : classesToLoad) {
            var canonicalName = config.getCanonicalName();
            try {
                Class.forName(canonicalName);
            } catch (ClassNotFoundException e) {
                FKMod.LOGGER.fatal("GAME IS DISABLE DU TO ERROR WITH CONFIGS");
                throw new RuntimeException(e);
            }
        }
    }

}
