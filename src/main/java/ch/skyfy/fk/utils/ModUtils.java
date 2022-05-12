package ch.skyfy.fk.utils;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import static ch.skyfy.fk.FKMod.CONFIG_DIRECTORY;

public class ModUtils {

    public static File getRelativeFile(String relativeFilePath) {
        return CONFIG_DIRECTORY.resolve(relativeFilePath).toFile();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void generateMinecraftIdentifier() {

        var subFolder = CONFIG_DIRECTORY.resolve("identifiers");

        if (!subFolder.toFile().exists()) subFolder.toFile().mkdirs();

        var blocksIdentifier = ReflectionUtils.getListOfTranslationKey(Blocks.class, Block.class);
        var blocksPath = subFolder.resolve("blocksIdentifier.txt");

        var itemIdentifier = ReflectionUtils.getListOfTranslationKey(Items.class, Item.class);
        var ItemsPath = subFolder.resolve("itemsIdentifier.txt");

        var potionIdentifier = ReflectionUtils.getListOfTranslationKey(Potions.class, Potion.class);
        var potionsPath = subFolder.resolve("potionsIdentifier.txt");

        var killEntitiesIdentifier = ReflectionUtils.getListOfTranslationKey(EntityType.class, EntityType.class);
        var killEntitiesPath = subFolder.resolve("killEntitiesIdentifier.txt");

        try {
            if (!blocksPath.toFile().exists())
                Files.write(blocksPath, blocksIdentifier, Charset.defaultCharset());
            if (!ItemsPath.toFile().exists())
                Files.write(ItemsPath, itemIdentifier, Charset.defaultCharset());
            if (!potionsPath.toFile().exists())
                Files.write(potionsPath, potionIdentifier, Charset.defaultCharset());
            if (!killEntitiesPath.toFile().exists())
                Files.write(killEntitiesPath, killEntitiesIdentifier, Charset.defaultCharset());
        } catch (IOException ignored) {
        }

    }

}
