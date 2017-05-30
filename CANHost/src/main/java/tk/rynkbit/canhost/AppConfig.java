package tk.rynkbit.canhost;

import de.fischl.usbtin.USBtin;
import de.fischl.usbtin.USBtinException;
import jssc.SerialPortList;
import tk.rynkbit.can.logic.CANRepository;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.regex.Pattern;

/**
 * Created by michael on 30.05.17.
 */
@ApplicationPath("/canhost")
public class AppConfig extends Application {
    public AppConfig() {
        super();

        USBtin usBtin = new USBtin();
        String[] portNames = SerialPortList.getPortNames(Pattern.compile("/g/ttyACM.*"));
        boolean connected = false;
        int count = 0;

        System.out.println("Ports found: ");

        for (String port :
                portNames) {
            System.out.println("\t* " + port + "\n");
        }

        System.out.println();

        if(portNames.length > 0){
            do{
                System.out.println("Connecting to " + portNames[count]);
                try {
                    usBtin.connect(portNames[count]);
                } catch (USBtinException e) {
                    System.out.println(e.getMessage());
                }
                try {
                    usBtin.openCANChannel(125000, USBtin.OpenMode.ACTIVE);
                    connected = true;
                } catch (USBtinException e) {
                    System.out.println(e.getMessage());
                    try {
                        usBtin.disconnect();
                    } catch (USBtinException e1) {
                        System.out.println(e1.getMessage());
                    }
                }

                count++;
            }while (connected == false && count < portNames.length);
        }

        if(connected == true){
            usBtin.addMessageListener(CANRepository.getInstance());
        }else{
            System.out.println("Could not connect to a port");
        }
    }
}
