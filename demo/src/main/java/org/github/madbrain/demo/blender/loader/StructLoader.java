package org.github.madbrain.demo.blender.loader;

import java.io.IOException;

public interface StructLoader<T> {
    Class<T> getTargetClass();

    T load(FileLoader fileLoader, FileReader reader) throws IOException;

    void save(FileLoader fileLoader, FileWriter writer, T value) throws IOException;
}
