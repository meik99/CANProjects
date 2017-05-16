package tk.rynkbit.can.analyzer;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by michael on 16.05.17.
 */
public abstract class Controller {
    protected final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors()
    );

    public abstract void updateRepository();

    public abstract void stop();
}
