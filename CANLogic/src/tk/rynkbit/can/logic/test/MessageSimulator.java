package tk.rynkbit.can.logic.test;

import de.fischl.usbtin.CANMessage;
import tk.rynkbit.can.logic.CANRepository;

import java.util.Random;

/**
 * Created by michael on 15.05.17.
 */
public class MessageSimulator implements Runnable {
    private boolean running = true;

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        Random random = new Random();
        while(running){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                running = false;
            }
            int id = 0x100 + random.nextInt(0x400);
            byte[] bytes = new byte[3 + random.nextInt(5)];

            for(int i = 0; i < bytes.length; i++){
                bytes[i] = (byte)random.nextInt(0xff);
            }

            CANRepository.getInstance()
                    .receiveCANMessage(new CANMessage(
                            id, bytes
                    ));
        }
    }
}
