package org.github.madbrain.demo.blender;

import org.github.madbrain.demo.blender.loader.FileLoader;
import org.github.madbrain.demo.blender.model.*;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        var scene = new Scene();
        scene.camera = new Camera();
        scene.camera.position = new Point(10.0, 20.0, 5.0);
        scene.camera.lookAt = new Point(11.0, 21.0, 15.0);

        var light = new Light();
        light.type = LightType.Spot;
        light.intensity = 100.0f;
        light.position = new Point(1.0, 2.0, 3.0);
        light.color = new Color(0.8f, 0.1f, 0.1f);
        scene.lights.add(light);
        
        var loader = new FileLoader();

        var filename = "test.blend";
        loader.save(Paths.get(filename).toFile(), scene);

        var result = loader.load(Paths.get(filename).toFile());

        System.out.println(result.camera.position);
    }
}
