package dev.diamond.luafy.util;

public class RemovalMarkedRunnable {

    private boolean markedForRemoval = false;
    private final Runnable runnable;

    private RemovalMarkedRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public void run() {
        runnable.run();
        markedForRemoval = true;
    };
    public boolean markedForRemoval() {
        return markedForRemoval;
    }


    public static RemovalMarkedRunnable of(Runnable runnable) {
        return new RemovalMarkedRunnable(runnable);
    }
}
