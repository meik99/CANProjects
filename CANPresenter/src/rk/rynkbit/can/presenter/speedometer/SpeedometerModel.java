package rk.rynkbit.can.presenter.speedometer;

import de.fischl.usbtin.USBtin;

/**
 * Created by michael on 16.05.17.
 */
public class SpeedometerModel {
    private de.fischl.usbtin.USBtin USBtin;

    public void setUSBtin(USBtin USBtin) {
        this.USBtin = USBtin;
    }

    public USBtin getUSBtin() {
        return USBtin;
    }
}
