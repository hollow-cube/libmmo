package net.hollowcube.debug;

import java.util.concurrent.atomic.AtomicBoolean;

public final class Simulation {
    private static final AtomicBoolean running = new AtomicBoolean(true);

    private Simulation() {}

    public static void play() {
        running.set(true);
    }

    public static void pause() {
        running.set(false);
    }

    public static boolean isRunning() {
        return running.get();
    }
}
