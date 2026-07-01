package org.github.madbrain.demo.builder;

import com.github.madbrain.playmobuild.api.PlaymoBuild;
import com.github.madbrain.playmobuild.api.Required;

import java.util.List;

@PlaymoBuild
public record Person(@Required String name, @Required int age, List<Hobby> hobbies) {
    public record Hobby(String name) {}
}
