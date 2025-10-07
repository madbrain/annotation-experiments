package org.github.madbrain.demo.blender.model;

import org.github.madbrain.demo.blender.DNA;

@DNA
public class Light {
    public Point position;
    public LightType type;
    public Color color;
    public float intensity;
}
