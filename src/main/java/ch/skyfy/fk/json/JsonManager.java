package ch.skyfy.fk.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public class JsonManager<T> {

    private final Class<T> tClass;

    private final TypeToken<T> typeToken;

    private final Gson gson;

    private final File file;

    public JsonManager(Class<T> tClass, Gson gson, File file) {
        this.tClass = tClass;
        this.typeToken = TypeToken.of(tClass);
        this.gson = gson;
        this.file = file;
    }

    public JsonManager(Class<T> tClass, File file) {
        this(tClass, new GsonBuilder().setPrettyPrinting().create(), file);
    }

    public @Nullable T getOrCreateConfig() {
        T config;
        try {
            if (file.exists())
                config = get();
            else {
                config = tClass.getDeclaredConstructor().newInstance();
                save(config);
            }
        } catch (IOException | JsonIOException | InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return null;
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
