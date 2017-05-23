package rk.rynkbit.can.presenter.speedometer;

import de.fischl.usbtin.USBtin;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by michael on 16.05.17.
 */
public class SpeedometerModel {
    private de.fischl.usbtin.USBtin USBtin;
    private ScheduledThreadPoolExecutor executor;

    public void setUSBtin(USBtin USBtin) {
        this.USBtin = USBtin;
    }

    public synchronized USBtin getUSBtin() {
        return USBtin;
    }

    public void setExecutor(ScheduledThreadPoolExecutor executor) {
        this.executor = executor;
    }

    public ScheduledThreadPoolExecutor getExecutor() {
        return executor;
    }
}
