package org.github.madbrain.demo.builder;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        var person = PersonBuilder.make()
                .withName("hello")
                .withAge(20)
                .withHobbies(List.of(
                        new Person.Hobby("football"),
                        new Person.Hobby("music"))
                )
                .build();
        System.out.println(person);
    }
}
