package ch.skyfy.fk.json;

import ch.skyfy.fk.utils.ModUtils;

public class JsonDataClass<T extends Validatable> {

    public final T data;
    public final String relativeFilePath;
    public final Class<T> cClass;

    public final JsonManager<T> jsonManager;

    public JsonDataClass(String relativeFilePath, Class<T> cClass, T defaultConfig) {
        this.relativeFilePath = relativeFilePath;
        this.cClass = cClass;

        this.jsonManager = new JsonManager<>(cClass, ModUtils.getRelativeFile(relativeFilePath), defaultConfig);

        data = jsonManager.getOrCreateConfig();
    }

}
