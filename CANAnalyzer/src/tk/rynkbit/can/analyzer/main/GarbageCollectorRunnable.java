package tk.rynkbit.can.analyzer.main;

/**
 * Created by michael on 16.05.17.
 */
public class GarbageCollectorRunnable implements Runnable {
    private boolean running = true;

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        running = true;
        Runtime.getRuntime().gc();
        running = false;
    }
}
