package ch.skyfy.fk.json;

import ch.skyfy.fk.utils.ModUtils;

public class JsonData<DATA extends Validatable, DEFAULT extends Defaultable<DATA>> {

    public final String relativeFilePath;

    public final Class<DATA> cClass;

    public final JsonManager<DATA, DEFAULT> jsonManager;

    public final DATA data;

    public JsonData(String relativeFilePath, Class<DATA> cClass, Class<DEFAULT> defaultConfigClass) {
        this.relativeFilePath = relativeFilePath;
        this.cClass = cClass;
        this.jsonManager = new JsonManager<>(cClass, ModUtils.getRelativeFile(relativeFilePath), defaultConfigClass);
        data = jsonManager.getOrCreateConfig();
    }

}
