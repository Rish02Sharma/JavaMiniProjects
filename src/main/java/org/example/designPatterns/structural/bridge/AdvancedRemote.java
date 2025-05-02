package org.example.designPatterns.structural.bridge;

public class AdvancedRemote extends RemoteControl {
    private boolean power = false;
    private int volume = 30;

    public AdvancedRemote(Device device) {
        super(device);
    }

    public void togglePower() {
        if (power) {
            device.turnOff();
        } else {
            device.turnOn();
        }
        power = !power;
    }

    public void volumeUp() {
        volume += 10;
        if (volume > 100) volume = 100;
        device.setVolume(volume);
    }
}

