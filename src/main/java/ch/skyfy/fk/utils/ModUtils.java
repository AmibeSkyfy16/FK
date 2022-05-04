package ch.skyfy.fk.utils;

import java.io.File;

import static ch.skyfy.fk.FKMod.CONFIG_DIRECTORY;

public class ModUtils {

    public static File getRelativeFile(String relativeFilePath){
        return CONFIG_DIRECTORY.resolve(relativeFilePath).toFile();
    }

}
