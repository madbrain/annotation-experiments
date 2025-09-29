package org.github.madbrain.demo.builder;

public class Main {
    public static void main(String[] args) {
        var person = PersonBuilder.make()
                .withName("hello")
                .withAge(20)
                .addHobby(new Person.Hobby("football"))
                .addHobby(new Person.Hobby("music"))
                .build();
        System.out.println(person);
    }
}
