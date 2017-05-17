package tk.rynkbit.can.usbhost;

import javax.usb.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by michael on 18.05.17.
 */
public class DeviceRepository {
    private static DeviceRepository instance;

    public static DeviceRepository getInstance(){
        if(instance == null){
            instance = new DeviceRepository();
        }
        return instance;
    }

    private List<UsbDevice> devices;

    private DeviceRepository(){
        devices = new CopyOnWriteArrayList<>();
    }

    public void addDevice(UsbDevice device) {
        if(device != null){
            devices.add(device);
        }
    }

    public List<UsbDevice> getDevices(){
        return devices;
    }

    public void sendDataToAll(byte[] buffer){

        for (UsbDevice device :
                devices) {
            sendData(device, buffer);
        }
    }

    public void sendData(int index, byte[] buffer){
        if(index >= 0 && index < devices.size()){
            sendData(devices.get(index), buffer);
        }
    }

    public void sendData(UsbDevice device, byte[] buffer){
        UsbConfiguration configuration;
        UsbInterface usbInterface;
        UsbControlIrp controlIrp;

        configuration = device.getActiveUsbConfiguration();
        usbInterface = configuration.getUsbInterface((byte)1);
        try {
            usbInterface.claim();
            controlIrp = device.createUsbControlIrp(
                    (byte)(UsbConst.REQUESTTYPE_TYPE_CLASS |
                            UsbConst.REQUESTTYPE_RECIPIENT_INTERFACE),
                    (byte) 0x09,
                    (short) 2,
                    (short) 1
            );
            controlIrp.setData(buffer);
            device.syncSubmit(controlIrp);
            usbInterface.release();
        } catch (UsbException e) {
            e.printStackTrace();
        }
    }
}
