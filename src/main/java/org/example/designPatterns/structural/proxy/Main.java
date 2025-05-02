package org.example.designPatterns.structural.proxy;

public class Main {
    public static void main(String[] args) {
        Image img = new ProxyImage("rishabh_photo.jpg");

        System.out.println("First call:");
        img.display(); // loads and displays

        System.out.println("Second call:");
        img.display(); // just displays
    }
}

