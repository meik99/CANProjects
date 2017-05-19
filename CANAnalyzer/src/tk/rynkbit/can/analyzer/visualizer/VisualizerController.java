package tk.rynkbit.can.analyzer.visualizer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import tk.rynkbit.can.analyzer.Controller;
import tk.rynkbit.can.analyzer.UpdateMessages;
import tk.rynkbit.can.analyzer.log.CSVLogger;
import tk.rynkbit.can.logic.CANRepository;
import tk.rynkbit.can.logic.models.TimedCANMessage;

import java.net.URL;
import java.util.*;

/**
 * Created by michael on 15.05.17.
 */
public class VisualizerController extends Controller implements Initializable{
    public AnchorPane rootPane;
    public ComboBox<String> comboMessages;
    public LineChart<String, Integer> chart;
    public List<LineChart.Series<String, Integer>> seriesList;

    private VisualizerModel model = new VisualizerModel();

    public void setCANMessage(TimedCANMessage timedCANMessage) {
        model.setCANMessage(timedCANMessage);
        selectCANMessage(timedCANMessage.getId());
    }

    public void setParent(Parent previousWindow){
        model.setParent(previousWindow);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeComboBox();

        seriesList = new ArrayList<>();

        chart.setAnimated(false);
        chart.setCreateSymbols(false);

        model.setUpdateRunnable(new UpdateMessages(this));
        model.getUpdateRunnable().setTimespan(100);
        executor.execute(model.getUpdateRunnable());
    }

    private void initializeComboBox() {
        Set<Integer> messageIds = CANRepository.getInstance().getMessageMap().keySet();
        ObservableList<String> idStrings = FXCollections.observableList(new LinkedList<>());

        for (int message :
                messageIds) {
            idStrings.add(Integer.toHexString(message));
        }

        comboMessages.setItems(idStrings);
    }

    public void clickBack(ActionEvent actionEvent) {
        rootPane.getScene().getWindow().getScene().setRoot(model.getParent());
    }


    @Override
    public void updateRepository() {
        Platform.runLater(()->{
            TimedCANMessage message = CANRepository.getInstance().getMessageMap()
                    .get(model.getCANMessage().getId());

            if(message != null){
                byte[] data = message.getData();
                int[] logdata = new int[data.length];
                Date newDate = new Date();
                String dateString = String.valueOf(newDate.getTime());

                for(int i = 0; i < (data.length >= 2 ? 2 : data.length); i++){
                    if(seriesList.size() < (i+1)){
                        XYChart.Series<String, Integer> series = new XYChart.Series();
                        series.setName("Byte " + i);

                        seriesList.add(series);
                        chart.getData().add(seriesList.get(i));
                    }

                    seriesList.get(i).getData().add(
                            new XYChart.Data<>(dateString, Byte.toUnsignedInt(data[i])
                    ));

                    logdata[i] = Byte.toUnsignedInt(data[i]);

                    if(seriesList.get(i).getData().size() > 50){
                        seriesList.get(i).getData().remove(0);
                    }
                }

                CSVLogger.getInstance().log(logdata);
            }
        });
    }

    @Override
    public void stop() {
        executor.shutdownNow();
    }

    public void clickMessage(ActionEvent mouseEvent) {
        String selectedItem = comboMessages.getSelectionModel().getSelectedItem();
        selectCANMessage(selectedItem);
    }

    private void selectCANMessage(String idString){
        if(idString != null){
            Integer selectedInteger = Integer.parseInt(idString, 16);
            selectCANMessage(selectedInteger);
        }
    }

    private void selectCANMessage(int id) {
        model.setCANMessage(
                CANRepository.getInstance().getMessageMap().values()
                        .stream()
                        .filter(m -> m.getId() == id)
                        .findFirst()
                        .orElse(null)
        );
        Platform.runLater(() ->{
            comboMessages.getSelectionModel().select(
                    comboMessages.getItems().stream()
                            .filter(m -> m.equals(Integer.toHexString(id)))
                            .findFirst().orElse(null)
            );
            chart.getData().clear();
            seriesList.clear();
        });

    }
}
