package ch.skyfy.fk.json;


import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.utils.ModUtils;

public class JsonDataClass<C extends Validable> {
    public final C config;
    public final String relativeFilePath;
    public final Class<C> cClass;

    public final JsonManager<C> jsonManager;

    public JsonDataClass(String relativeFilePath, Class<C> cClass) {
        this.relativeFilePath = relativeFilePath;
        this.cClass = cClass;

        this.jsonManager = new JsonManager<>(cClass, ModUtils.getRelativeFile(relativeFilePath));

        try {
            config = jsonManager.getOrCreateConfig();
            var result = config.validate();
            if (result.size() != 0){
                FKMod.LOGGER.fatal("Some .json file are not valid");
                result.forEach(FKMod.LOGGER::fatal);
                throw new RuntimeException();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
