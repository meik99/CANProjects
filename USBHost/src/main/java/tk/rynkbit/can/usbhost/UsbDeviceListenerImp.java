package tk.rynkbit.can.usbhost;

import tk.rynkbit.can.logic.protocol.USBHostProtocol;

import javax.usb.event.UsbDeviceDataEvent;
import javax.usb.event.UsbDeviceErrorEvent;
import javax.usb.event.UsbDeviceEvent;
import javax.usb.event.UsbDeviceListener;
import java.nio.charset.Charset;

/**
 * Created by michael on 18.05.17.
 */
public class UsbDeviceListenerImp implements UsbDeviceListener {
    @Override
    public void usbDeviceDetached(UsbDeviceEvent event) {
        event.getUsbDevice().removeUsbDeviceListener(this);
    }

    @Override
    public void errorEventOccurred(UsbDeviceErrorEvent event) {

    }

    @Override
    public void dataEventOccurred(UsbDeviceDataEvent event) {
        byte[] data = event.getData();
        String text = new String(data, Charset.forName("UTF-8"));

        if(text.equals(USBHostProtocol.DEVICE_CONNECT)){
            DeviceRepository.getInstance().addDevice(event.getUsbDevice());
        }
    }
}
