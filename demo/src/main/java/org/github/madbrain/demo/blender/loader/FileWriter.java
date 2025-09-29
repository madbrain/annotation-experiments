package org.github.madbrain.demo.blender.loader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class FileWriter {
    private final FileOutputStream stream;
    private final ByteBuffer buffer = ByteBuffer.allocate(30);

    public FileWriter(FileOutputStream stream) {
        this.stream = stream;
    }

    public void writeInt(int value) throws IOException {
        buffer.clear();
        buffer.putInt(value);
        stream.write(buffer.array(), 0, buffer.position());
    }

    public void writeFloat(float value) throws IOException {
        buffer.clear();
        buffer.putFloat(value);
        stream.write(buffer.array(), 0, buffer.position());
    }

    public void writeDouble(double value) throws IOException {
        buffer.clear();
        buffer.putDouble(value);
        stream.write(buffer.array(), 0, buffer.position());
    }
}
