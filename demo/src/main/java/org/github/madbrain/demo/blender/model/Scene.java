package org.github.madbrain.demo.blender.model;

import org.github.madbrain.demo.blender.DNA;

import java.util.ArrayList;
import java.util.List;

@DNA
public class Scene {
    public Camera camera;
    public List<Light> lights = new ArrayList<>();
    public List<Shape> shapes = new ArrayList<>();
}
