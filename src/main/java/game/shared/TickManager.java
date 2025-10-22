package game.shared;

import java.util.ArrayList;

public class TickManager {
    public ArrayList<Tickable> tickables = new ArrayList<>();

    public Thread thread;

    private double counter = 0;
    private double tpsTimer = 0;
    private int tpsCounter = 0;
    public int tps = 0;
    private double lastTimeValue = 0;
    public double lastTickTime = 0;
    public boolean isRunning = false;
    public Thread.UncaughtExceptionHandler exceptionHandler;

    public void tick() {
        for(Tickable tickable : this.tickables) {
            tickable.tick();
        }
    }

    public void start(Thread.UncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        this.start();
    }

    public void start() {
        this.lastTimeValue = System.nanoTime() / Math.pow(10,9);
        this.lastTickTime = lastTimeValue;
        this.isRunning = true;
        this.thread = new Thread(this::loop);
        if(this.exceptionHandler != null) {
            this.thread.setUncaughtExceptionHandler(this.exceptionHandler);
        }
        this.thread.start();
    }

    public void stop() {
        this.isRunning = false;
    }

    public void loop() {
        try {
            while(this.isRunning) {
                double currentTime = System.nanoTime() / Math.pow(10,9);
                if (this.tickables.isEmpty()) {
                    this.counter = 0;
                    this.lastTimeValue = currentTime;
                    continue;
                }
                this.counter = this.counter + (currentTime - this.lastTimeValue);
                this.tpsTimer = this.tpsTimer + (currentTime - this.lastTimeValue);
                this.lastTimeValue = currentTime;
                while(this.tpsTimer >= 1) {
                    this.tpsTimer = this.tpsTimer - 1;
                    this.tps = this.tpsCounter;
                    this.tpsCounter = 0;
                }

                while(counter >= (1 / 20D)) {
                    this.tick();
                    this.lastTickTime = this.lastTimeValue;
                    counter = counter - (1 / 20D);
                    this.tpsCounter++;
                }
            }
        } catch(Exception e) {
            throw new RuntimeException("Exception while ticking", e);
        }
    }
}
