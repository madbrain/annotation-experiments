package org.github.madbrain.demo.blender.model;

import org.github.madbrain.demo.blender.DNA;

@DNA
public class Color {
    public float r;
    public float g;
    public float b;

    public Color() {}

    public Color(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }
}
