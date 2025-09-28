package org.github.madbrain.demo.builder;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        var person = PersonBuilder.make()
                .withName("hello")
                .withAge(20)
                .addHobbies(new Person.Hobby("football"))
                .addHobbies(new Person.Hobby("music"))

                .build();
        System.out.println(person);
    }
}
