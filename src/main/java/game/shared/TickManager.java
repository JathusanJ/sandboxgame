package game.shared;

import java.util.ArrayList;

public class TickManager {
    public ArrayList<Tickable> tickables = new ArrayList<>();

    public Thread thread;

    private double counter = 0;
    private double lastTimeValue = 0;
    public double lastTickTime = 0;
    public boolean isRunning = false;

    public void tick() {
        for(Tickable tickable : this.tickables) {
            tickable.tick();
        }
    }

    public void start() {
        this.lastTimeValue = System.nanoTime() / Math.pow(10,9);
        this.lastTickTime = lastTimeValue;
        this.isRunning = true;
        this.thread = new Thread(this::loop);
        this.thread.start();
    }

    public void stop() {
        this.isRunning = false;
    }

    public void loop() {
        try {
            while (isRunning) {
                double currentTime = System.nanoTime() / Math.pow(10,9);
                if (this.tickables.isEmpty()) {
                    counter = 0;
                    lastTimeValue = currentTime;
                    continue;
                }
                counter = counter + (currentTime - lastTimeValue);
                lastTimeValue = currentTime;
                while (counter >= (1 / 20D)) {
                    this.tick();
                    this.lastTickTime = this.lastTimeValue;
                    counter = counter - (1 / 20D);
                }
            }
        } catch(Exception e) {
            throw new RuntimeException("Exception while ticking", e);
        }
    }
}
