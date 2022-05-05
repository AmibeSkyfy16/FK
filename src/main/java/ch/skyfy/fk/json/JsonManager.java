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
public class JsonManager<T extends Validatable> {

    private final Class<T> tClass;

    private final TypeToken<T> typeToken;

    private final Gson gson;

    private final File file;

    private final T defaultConfig;

    public JsonManager(Class<T> tClass, Gson gson, File file, T defaultConfig) {
        this.tClass = tClass;
        this.typeToken = TypeToken.of(tClass);
        this.gson = gson;
        this.file = file;
        this.defaultConfig = defaultConfig;
    }

    public JsonManager(Class<T> tClass, File file, T defaultConfig) {
        this(tClass, new GsonBuilder().setPrettyPrinting().create(), file, defaultConfig);
    }

    public @NotNull T getOrCreateConfig() {
        T config;
        try {
            if (file.exists())
                config = get();
            else {
                config = defaultConfig;
                save(defaultConfig);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return config;
    }

    public T get() throws IOException {
        try (var reader = new FileReader(file)) {
            return gson.fromJson(reader, typeToken.getType());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void save(T t) throws IOException {
        file.getParentFile().mkdirs();
        try (var writer = new FileWriter(file)) {
            gson.toJson(t, typeToken.getType(), writer);
        }
    }

}
