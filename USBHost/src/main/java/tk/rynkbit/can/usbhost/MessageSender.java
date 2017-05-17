package tk.rynkbit.can.usbhost;

import tk.rynkbit.can.logic.CANRepository;
import tk.rynkbit.can.logic.models.TimedCANMessage;

import javax.usb.*;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by michael on 18.05.17.
 */
public class MessageSender implements Runnable {
    private boolean running = true;

    @Override
    public void run() {
        List<UsbDevice> devices;
        List<TimedCANMessage> canMessages;

        UsbConfiguration configuration;
        UsbInterface usbInterface;
        UsbControlIrp controlIrp;

        ByteBuffer buffer;
        byte[] data;

        while (running == true){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                running = false;
            }
            devices = DeviceRepository.getInstance().getDevices();
            canMessages = new LinkedList<>();

            canMessages.addAll(CANRepository.getInstance().getMessageMap().values());

            buffer = ByteBuffer.allocate(canMessages.size() * (4+8+1+1+8));

            for (TimedCANMessage canMessage :
                 canMessages) {

                data = canMessage.getData().length == 8 ?
                        canMessage.getData() :
                        ByteBuffer.allocate(8).put(canMessage.getData()).array();

                buffer.putInt(canMessage.getId());
                buffer.put(data);
                buffer.put((byte)(canMessage.isExtended() ? 1 : 0));
                buffer.put((byte)(canMessage.isRtr() ? 1 : 0));
                buffer.putLong(canMessage.getTimestamp().getTime());
            }

            DeviceRepository.getInstance().sendDataToAll(buffer.array());

            if(Thread.currentThread().isInterrupted()){
                running = false;
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
