package ch.skyfy.fk.json;


import ch.skyfy.fk.utils.ModUtils;

public class JsonDataClass<C> {
    public final C config;
    public final String relativeFilePath;
    public final Class<C> cClass;

    public final JsonManager<C> jsonManager;

    public JsonDataClass(String relativeFilePath, Class<C> cClass) {
        this.relativeFilePath = relativeFilePath;
        this.cClass = cClass;
        
        this.jsonManager = new JsonManager<>(cClass, ModUtils.getRelativeFile(relativeFilePath));
        config = jsonManager.getOrCreateConfig();
    }
}
