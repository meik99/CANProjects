package tk.rynkbit.can.usbclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

/**
 * Created by michael on 18.05.17.
 */

public class UsbDetachBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.hasExtra(UsbManager.EXTRA_DEVICE)) {
            final UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        }
    }
}
