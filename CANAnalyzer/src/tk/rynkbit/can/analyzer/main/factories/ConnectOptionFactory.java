package tk.rynkbit.can.analyzer.main.factories;

import de.fischl.usbtin.USBtin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 16.05.17.
 */
public class ConnectOptionFactory {
    private static final List<Integer> BAUD_RATES = new ArrayList<>();
    private static final List<USBtin.OpenMode> OPEN_MODES = new ArrayList<>();

    private ConnectOptionFactory(){}

    public static ObservableList<Integer> getBaudRates(){
        if(BAUD_RATES.size() == 0){
            BAUD_RATES.add(10000);
            BAUD_RATES.add(20000);
            BAUD_RATES.add(50000);
            BAUD_RATES.add(100000);
            BAUD_RATES.add(125000);
            BAUD_RATES.add(250000);
            BAUD_RATES.add(500000);
            BAUD_RATES.add(800000);
            BAUD_RATES.add(1000000);
        }

        return FXCollections.observableList(BAUD_RATES);
    }

    public static ObservableList<USBtin.OpenMode> getOpenModes(){
        if(OPEN_MODES.size() == 0){
            USBtin.OpenMode[] openModes = USBtin.OpenMode.values();
            for (USBtin.OpenMode openMode :
                    openModes) {
                OPEN_MODES.add(openMode);
            }
        }

        return FXCollections.observableList(OPEN_MODES);
    }
}
