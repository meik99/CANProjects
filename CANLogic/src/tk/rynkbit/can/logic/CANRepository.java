package tk.rynkbit.can.logic;

import de.fischl.usbtin.CANMessage;
import de.fischl.usbtin.CANMessageListener;
import tk.rynkbit.can.logic.models.TimedCANMessage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mrynkiewicz on 14.05.17.
 */
public class CANRepository implements CANMessageListener {
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static CANRepository instance;

    private Map<Integer, TimedCANMessage> messageMap;

    public static CANRepository getInstance(){
        if(instance == null){
            instance = new CANRepository();
        }

        return instance;
    }

    private CANRepository(){
        messageMap = new ConcurrentHashMap<>();
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 3 + 3];

        hexChars[0] = '0';
        hexChars[1] = 'x';
        hexChars[2] = ' ';

        for ( int j = 0; j < bytes.length; j++ ) {
            int v = Byte.toUnsignedInt(bytes[j]);
            hexChars[j * 3 + 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 4] = hexArray[v & 0x0F];
            hexChars[j * 3 + 5] = ' ';
        }
        return new String(hexChars);
    }

    @Override
    public void receiveCANMessage(CANMessage canMessage) {
        TimedCANMessage message = messageMap.get(canMessage.getId());

        if(message == null){
            messageMap.put(canMessage.getId(), new TimedCANMessage(
                    canMessage.getId(),
                    canMessage.getData(),
                    canMessage.isExtended(),
                    canMessage.isRtr()));
        }else{
            message.increaseCount();

            if(canMessage.getData().equals(message.getData()) == false){
                message.setData(canMessage.getData());
                message.updateTimestamp();
            }
        }

    }

    public Map<Integer, TimedCANMessage> getMessageMap() {
        return messageMap;
    }
}
