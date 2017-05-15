package tk.rynkbit.can.analyzer.main;

/**
 * Created by mrynkiewicz on 14.05.17.
 */
public class UpdateMessages implements Runnable {
    private MainController mainController;
    private boolean running = true;
    private boolean paused = false;

    public UpdateMessages(MainController mainController){
        this.mainController = mainController;
    }

    @Override
    public void run() {
        while(running){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(paused == false){
                mainController.updateTable();
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
