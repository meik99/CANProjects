package tk.rynkbit.can.analyzer.main.factories;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import tk.rynkbit.can.logic.CANRepository;
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
       return param -> new SimpleObjectProperty<>(CANRepository.bytesToHex(param.getValue().getData()));
    }

    public static Callback<TableColumn.CellDataFeatures<TimedCANMessage, String>, ObservableValue<String>> getTimestampCellFactory(){
        return param -> new SimpleObjectProperty<>(sdf.format(param.getValue().getTimestamp()));
    }

}
