package org.example.designPatterns.creational.builder;

public class Main {
    public static void main(String[] args) {
        User user = new User.Builder("Rishabh", "Sharma")
                .age(30)
                .phone("1234567890")
                .address("Delhi, India")
                .build();

        System.out.println("Name: " + user.getFirstName() + " " + user.getLastName());
    }
}

