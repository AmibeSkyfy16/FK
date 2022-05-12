package ch.skyfy.fk.utils;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.exceptions.FKModException;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
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
        var map = new HashMap<Class<?>, String>();
        map.put(Block.class, "method_9539");
        map.put(Item.class, "method_7876");
        map.put(Potion.class, "");
        map.put(EntityType.class, "method_5882");
        var yarnMethodName = map.get(typeClass);
        if (yarnMethodName == null) return list;
        for (var field : tClass.getDeclaredFields()) {
            if (field.getType() == typeClass) {
                try {
                    var object = (TYPE) field.get(null);
                    if (object instanceof Potion potion) {
                        list.add(Registry.POTION.getId(potion).toString());
                    }else {
                        var method = typeClass.getMethod(yarnMethodName); // yarn mapping for getTranslationKey() method
                        list.add((String) method.invoke(object));
                    }

                } catch (IllegalAccessException | NoSuchMethodException | ClassCastException |
                         InvocationTargetException ignored) {
                    ignored.printStackTrace();
                }
            }
        }
        return list;
    }

}
