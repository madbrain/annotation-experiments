package org.github.madbrain.demo.blender.loader;

import org.github.madbrain.demo.blender.model.Light;
import org.github.madbrain.demo.blender.model.LightType;
import org.github.madbrain.demo.blender.model.Scene;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

public class FileLoader {

    private Map<Class<?>, StructLoader<?>> loaders = new HashMap<>();

    public FileLoader() {
        ServiceLoader.load(StructLoader.class).forEach(structLoader -> {
            loaders.put(structLoader.getTargetClass(), structLoader);
        });
    }

    public Scene load(File file) throws IOException {
        try (FileInputStream stream = new FileInputStream(file)) {
            var reader = new FileReader(stream);
            return readStruct(reader, Scene.class);
        }
    }

    public void save(File file, Scene scene) throws IOException {
        try (FileOutputStream stream = new FileOutputStream(file)) {
            var writer = new FileWriter(stream);
            writeStruct(writer, scene);
        }
    }

    public <T> void writeStruct(FileWriter writer, T value) throws IOException {
        getStructLoader((Class<T>)value.getClass()).save(this, writer, value);
    }

    public <T> void writeList(FileWriter writer, List<T> values) throws IOException {
        writer.writeInt(values.size());
        for(var value : values) {
            writeStruct(writer, value);
        }
    }

    public <T extends Enum<T>> void writeEnum(FileWriter writer, T value) throws IOException {
        writer.writeInt(value.ordinal());
    }

    public <T> T readStruct(FileReader reader, Class<T> targetClass) throws IOException {
        return getStructLoader(targetClass).load(this, reader);
    }

    public <T> List<T> readList(FileReader reader, Class<T> targetClass) throws IOException {
        var size = reader.readInt();
        var result = new ArrayList<T>();
        for (int i = 0; i < size; ++i) {
            result.add(readStruct(reader, targetClass));
        }
        return result;
    }

    public <T extends Enum<T>> T readEnum(FileReader reader, Class<T> enumClass) throws IOException {
        var ordinal = reader.readInt();
        return enumClass.getEnumConstants()[ordinal];
    }

    private <T> StructLoader<T> getStructLoader(Class<T> targetClass) {
        return Optional.ofNullable((StructLoader<T>) loaders.get(targetClass))
                .orElseThrow(() -> new IllegalArgumentException("Cannot find loader for " + targetClass));
    }
}
