package tk.rynkbit.can.analyzer.main.factories;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import tk.rynkbit.can.logic.models.TimedCANMessage;

import java.text.SimpleDateFormat;

/**
 * Created by mrynkiewicz on 14.05.17.
 */
public class MessageTableCellFactory {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

    private MessageTableCellFactory(){}

    public static Callback<TableColumn.CellDataFeatures<TimedCANMessage, String>, ObservableValue<String>> getIdCellFactory(){
        return param -> new SimpleObjectProperty<>(Integer.toHexString(param.getValue().getId()));
    }

    public static Callback<TableColumn.CellDataFeatures<TimedCANMessage, String>, ObservableValue<String>> getDataCellFactory(){
       return param -> new SimpleObjectProperty<>(bytesToHex(param.getValue().getData()));
    }

    public static Callback<TableColumn.CellDataFeatures<TimedCANMessage, String>, ObservableValue<String>> getTimestampCellFactory(){
        return param -> new SimpleObjectProperty<>(sdf.format(param.getValue().getTimestamp()));
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 3 + 3];

        hexChars[0] = '0';
        hexChars[1] = 'x';
        hexChars[2] = ' ';

        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3 + 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 4] = hexArray[v & 0x0F];
            hexChars[j * 3 + 5] = ' ';
        }
        return new String(hexChars);
    }
}
