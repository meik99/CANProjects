package tk.rynkbit.can.analyzer.main;

import java.util.concurrent.RejectedExecutionException;

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
        try {
            running = true;
            Runtime.getRuntime().gc();
            running = false;
        }catch (RejectedExecutionException ignored){}
    }
}
