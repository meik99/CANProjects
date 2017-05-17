package tk.rynkbit.can.usbhost;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;

/**
 * Created by mrynkiewicz on 17.05.17.
 */
@Singleton
@Startup
public class InitBean {
    private UsbDeviceListener deviceListener;

    @PostConstruct
    public void setup(){
        deviceListener = new UsbDeviceListener();

        try {
            UsbHostManager.getUsbServices().addUsbServicesListener(
                    deviceListener
            );
        } catch (UsbException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void tearDown(){
        try {
            UsbHostManager.getUsbServices().removeUsbServicesListener(
                    deviceListener
            );
        } catch (UsbException e) {
            e.printStackTrace();
        }
    }
}
