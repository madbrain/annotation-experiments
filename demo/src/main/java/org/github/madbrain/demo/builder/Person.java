package org.github.madbrain.demo.builder;

import com.github.madbrain.playmobuild.api.PlaymoBuild;

import java.util.List;

@PlaymoBuild
public record Person(String name, int age, List<Hobby> hobbies) {
    public record Hobby(String name) {}
}
