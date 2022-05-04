package ch.skyfy.fk.utils;

public class ReflectionUtils {

    public static boolean loadConfigByReflection(Class<?>[] classesToLoad) {
        for (Class<?> config : classesToLoad) {
            var canonicalName = config.getCanonicalName();
            try {
                Class.forName(canonicalName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

}
