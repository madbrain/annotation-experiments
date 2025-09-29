package org.github.madbrain.demo.blender.model;

import org.github.madbrain.demo.blender.DNA;

@DNA
public class Point {
    public double x;
    public double y;
    public double z;

    public Point() {
    }

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "[" + x + "," + y + "," + z + "]";
    }
}
