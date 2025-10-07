package org.github.madbrain.demo.blender.loader;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class FileReader {
    private final FileInputStream stream;

    public FileReader(FileInputStream stream) {
        this.stream = stream;
    }

    public int readInt() throws IOException {
        var data = new byte[4];
        stream.read(data);
        return ByteBuffer.wrap(data).getInt();
    }

    public float readFloat() throws IOException {
        var data = new byte[4];
        stream.read(data);
        return ByteBuffer.wrap(data).getFloat();
    }

    public double readDouble() throws IOException {
        var data = new byte[8];
        stream.read(data);
        return ByteBuffer.wrap(data).getDouble();
    }
}
