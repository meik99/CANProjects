package tk.rynkbit.can.usbhost;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by mrynkiewicz on 17.05.17.
 */
@Singleton
@Startup
public class InitBean {
    private UsbServiceListenerImp deviceListener;
    private ScheduledThreadPoolExecutor executor;

    @PostConstruct
    public void setup(){
        deviceListener = new UsbServiceListenerImp();
        executor = new ScheduledThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors()
        );

        try {
            UsbHostManager.getUsbServices().addUsbServicesListener(
                    deviceListener
            );
        } catch (UsbException e) {
            e.printStackTrace();
        }

        executor.execute(new MessageSender());
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

        executor.shutdown();
    }
}
