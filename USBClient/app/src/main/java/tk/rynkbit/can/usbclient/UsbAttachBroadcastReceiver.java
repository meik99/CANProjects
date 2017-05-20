package tk.rynkbit.can.usbclient;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConfiguration;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by michael on 18.05.17.
 */

class UsbAttachBroadcastReceiver extends BroadcastReceiver {
    private static final String ACTION_USB_PERMISSION = "tk.rynkbit.can.USB_PERMISSION";
    private static final String TAG = UsbAttachBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.hasExtra(UsbManager.EXTRA_DEVICE)){
            final UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            final UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
            final UsbConfiguration configuration = device.getConfiguration(0);
            final UsbInterface usbInterface = configuration.getInterface(0);
            final UsbEndpoint endpoint = usbInterface.getEndpoint(0);
            final byte[] buffer = new String("CON_CAN").getBytes();


            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (ACTION_USB_PERMISSION.equals(action)) {
                        synchronized (this) {
                            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                                UsbDeviceConnection connection = usbManager.openDevice(device);
                                connection.claimInterface(usbInterface, true);
                                connection.bulkTransfer(endpoint, buffer, buffer.length, 10);

                                UsbRequest request = new UsbRequest();
                                ByteBuffer byteBuffer = ByteBuffer.allocate(10);
                                request.initialize(connection, endpoint);
                                request.queue(byteBuffer, 10);

                                request = connection.requestWait();
                                connection.releaseInterface(usbInterface);
                            }
                            else {
                                Log.d(TAG, "permission denied for device " + device);
                            }
                        }
                    }
                }
            }, filter);

            usbManager.requestPermission(device,
                    PendingIntent.getBroadcast(
                            context, 0, new Intent(ACTION_USB_PERMISSION), 0));

        }
    }
}
