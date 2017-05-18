package tk.rynkbit.can.usbclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    BroadcastReceiver usbReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter usbConnectFilter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        IntentFilter usbDisconnectFilter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);

        usbReceiver = new UsbBroadcastReceiver();

        registerReceiver(usbReceiver, usbConnectFilter);
        registerReceiver(usbReceiver, usbDisconnectFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(usbReceiver);

        super.onDestroy();
    }
}
