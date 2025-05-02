package org.example.designPatterns.structural.bridge;

public class Main {
    public static void main(String[] args) {
        Device tv = new TV();
        RemoteControl remote = new AdvancedRemote(tv);

        remote.togglePower(); // Turns on TV
        remote.volumeUp();    // Sets TV volume
        remote.togglePower(); // Turns off TV

        System.out.println("---");

        Device radio = new Radio();
        remote = new AdvancedRemote(radio);

        remote.togglePower(); // Turns on Radio
        remote.volumeUp();    // Sets Radio volume
    }
}

