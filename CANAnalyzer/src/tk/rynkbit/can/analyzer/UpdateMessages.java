package tk.rynkbit.can.analyzer;

import tk.rynkbit.can.analyzer.main.MainController;

/**
 * Created by mrynkiewicz on 14.05.17.
 */
public class UpdateMessages implements Runnable {
    private Controller controller;
    private boolean running = true;
    private boolean paused = false;
    private long timespan = 10;

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public long getTimespan() {
        return timespan;
    }

    public void setTimespan(long timespan) {
        this.timespan = timespan;
    }

    public UpdateMessages(Controller controller){
        this.controller = controller;
    }

    @Override
    public void run() {
        while(running){
            try {
                Thread.sleep(timespan);
            } catch (InterruptedException e) {
                running = false;
            }
            if(paused == false){
                controller.updateRepository();
            }
        }
    }

    public boolean isPaused(){
        return paused;
    }
    public void setPaused(boolean paused){
        this.paused = paused;
    }

    public void stop() {
        running = false;
    }
}
