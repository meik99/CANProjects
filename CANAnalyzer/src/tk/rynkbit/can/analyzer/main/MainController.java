package tk.rynkbit.can.analyzer.main;

import de.fischl.usbtin.USBtin;
import de.fischl.usbtin.USBtinException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import tk.rynkbit.can.analyzer.Controller;
import tk.rynkbit.can.analyzer.UpdateMessages;
import tk.rynkbit.can.analyzer.main.factories.MessageTableCellFactory;
import tk.rynkbit.can.analyzer.visualizer.VisualizerController;
import tk.rynkbit.can.logic.CANRepository;
import tk.rynkbit.can.logic.models.TimedCANMessage;
import tk.rynkbit.can.logic.test.MessageSimulator;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by mrynkiewicz on 14.05.17.
 */
public class MainController extends Controller implements Initializable {
    public Button btnConnect;
    public AnchorPane paneRoot;
    public TableColumn<TimedCANMessage, String> colId;
    public TableColumn<TimedCANMessage, String> colData;
    public TableColumn<TimedCANMessage, String> colTimestamp;
    public TableView<TimedCANMessage> tableMessages;
    public MenuItem btnFilter;
    public MenuItem btnResetFilter;
    public CheckBox chkRecent;

    private MainModel model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = new MainModel();
        model.setUpdateRunnable(new UpdateMessages(this));

        colId.prefWidthProperty().bind(tableMessages.widthProperty().divide(10));
        colData.prefWidthProperty().bind(tableMessages.widthProperty().divide(10 / 4));
        colTimestamp.prefWidthProperty().bind(tableMessages.widthProperty().divide(10 / 4));

        colId.setCellValueFactory(MessageTableCellFactory.getIdCellFactory());
        colData.setCellValueFactory(MessageTableCellFactory.getDataCellFactory());
        colTimestamp.setCellValueFactory(MessageTableCellFactory.getTimestampCellFactory());

        tableMessages.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

        executor.execute(model.getUpdateRunnable());
    }

    public void clickConnect(ActionEvent actionEvent) {
        if (System.getenv("DEBUG") == null || System.getenv("DEBUG").equals("false")) {
            USBtin usBtin = new USBtin();
            usBtin.addMessageListener(CANRepository.getInstance());
            try {
                usBtin.connect("/dev/ttyACM0");
                usBtin.openCANChannel(125000, USBtin.OpenMode.ACTIVE);
            } catch (USBtinException e) {
                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Connection error");
                dialog.setContentText(e.getMessage());
                dialog.show();
            }
            model.setUSBtin(usBtin);
        } else {
            if(model.getMessageSimulator() == null){
                model.setMessageSimulator(new MessageSimulator());
                executor.execute(model.getMessageSimulator());
            }
        }
    }


    public void stop() {
        if (model.getUSBtin() != null) {
            try {
                model.getUSBtin().closeCANChannel();
            } catch (USBtinException ignored) {

            }
            try {
                model.getUSBtin().disconnect();
            } catch (USBtinException ignored) {

            }
        }
        model.getUpdateRunnable().stop();
        executor.shutdownNow();

        if(model.getVirtualizationController() != null){
            model.getVirtualizationController().stop();
        }
    }

    @Override
    public void updateRepository() {
            List<TimedCANMessage> canMessages = new CopyOnWriteArrayList<>();

            if (model.getFitlerMessage() == null) {
                canMessages.addAll(CANRepository.getInstance().getMessageMap().values());

                if (chkRecent.selectedProperty().get() == true) {
                    canMessages.removeIf(m -> {
                        Date later = new Date();

                        return later.getTime() - m.getTimestamp().getTime() >= 500;
                    });
                }
            } else {
                canMessages.add(CANRepository.getInstance().getMessageMap().get(
                        model.getFitlerMessage().getId()));
            }

        Platform.runLater(() -> {
            tableMessages.setItems(
                    FXCollections.observableList(canMessages)
            );
            tableMessages.refresh();
        });
    }

    public void clickFilter(ActionEvent actionEvent) {
        TimedCANMessage filterMessage = tableMessages.getSelectionModel().getSelectedItem();
        if (filterMessage != null) {
            model.setFitlerMessage(filterMessage);
        }
    }

    public void clickResetFilter(ActionEvent actionEvent) {
        model.setFitlerMessage(null);
    }

    public void clickVisualize(ActionEvent actionEvent) {
        TimedCANMessage timedCANMessage = tableMessages.getSelectionModel().getSelectedItem();

        if (timedCANMessage != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../visualizer/visualizerLayout.fxml"));
            try {
                Parent parent = loader.load();
                VisualizerController controller = loader.getController();

                paneRoot
                        .getScene()
                        .getWindow()
                        .getScene()
                        .setRoot(parent);

                controller.setCANMessage(timedCANMessage);
                controller.setParent(paneRoot);

                model.setVirtualizationController(controller);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
