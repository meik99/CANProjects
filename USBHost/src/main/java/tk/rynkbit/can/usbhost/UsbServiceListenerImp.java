package tk.rynkbit.can.usbhost;

import javax.usb.UsbConfiguration;
import javax.usb.UsbControlIrp;
import javax.usb.UsbDevice;
import javax.usb.UsbException;
import javax.usb.event.UsbServicesEvent;
import javax.usb.event.UsbServicesListener;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mrynkiewicz on 17.05.17.
 */
public class UsbServiceListenerImp implements UsbServicesListener {

    @Override
    public void usbDeviceAttached(UsbServicesEvent usbServicesEvent) {
        UsbDevice device = usbServicesEvent.getUsbDevice();
        device.addUsbDeviceListener(new UsbDeviceListenerImp());
    }

    @Override
    public void usbDeviceDetached(UsbServicesEvent usbServicesEvent) {

    }
}
