package ch.skyfy.fk.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public class JsonManager<DATA extends Validatable, DEFAULT extends Defaultable<DATA>> {

    private final TypeToken<DATA> typeToken;

    private final Gson gson;

    private final File file;

    private final Class<DEFAULT> defaultConfigClass;

    public JsonManager(Class<DATA> tClass, Gson gson, File file, Class<DEFAULT> defaultConfigClass) {
        this.typeToken = TypeToken.of(tClass);
        this.gson = gson;
        this.file = file;
        this.defaultConfigClass = defaultConfigClass;
    }

    public JsonManager(Class<DATA> tClass, File file, Class<DEFAULT> defaultConfigClass) {
        this(tClass, new GsonBuilder().setPrettyPrinting().serializeNulls().create(), file, defaultConfigClass);
    }

    public @NotNull DATA getOrCreateConfig() {
        DATA config;
        try {
            if (file.exists())
                config = get();
            else {
                config = this.defaultConfigClass.getDeclaredConstructor().newInstance().getDefault();
                save(config);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return config;
    }

    public DATA get() throws IOException {
        try (var reader = new FileReader(file)) {
            return gson.fromJson(reader, typeToken.getType());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void save(DATA DATA) throws IOException {
        file.getParentFile().mkdirs();
        try (var writer = new FileWriter(file)) {
            gson.toJson(DATA, typeToken.getType(), writer);
        }
    }

}
