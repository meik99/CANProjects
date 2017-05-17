package tk.rynkbit.can.usbhost;

import javax.usb.UsbDevice;
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
}
