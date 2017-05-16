package tk.rynkbit.can.analyzer.main;

import de.fischl.usbtin.USBtin;
import de.fischl.usbtin.USBtinException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tk.rynkbit.can.analyzer.Controller;
import tk.rynkbit.can.analyzer.UpdateMessages;
import tk.rynkbit.can.analyzer.main.factories.ConnectOptionFactory;
import tk.rynkbit.can.analyzer.main.factories.MessageTableCellFactory;
import tk.rynkbit.can.analyzer.note.NoteController;
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
    public TextField txtPort;
    public ComboBox<Integer> comboBaudrate;
    public ComboBox<USBtin.OpenMode> comboOpenMode;
    public HBox boxToolbarRight;

    private MainModel model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = new MainModel();
        model.setUpdateRunnable(new UpdateMessages(this));
        model.getUpdateRunnable().setTimespan(20);

        colId.prefWidthProperty().bind(tableMessages.widthProperty().divide(10));
        colData.prefWidthProperty().bind(tableMessages.widthProperty().divide(10 / 4));
        colTimestamp.prefWidthProperty().bind(tableMessages.widthProperty().divide(10 / 4));

        colId.setCellValueFactory(MessageTableCellFactory.getIdCellFactory());
        colData.setCellValueFactory(MessageTableCellFactory.getDataCellFactory());
        colTimestamp.setCellValueFactory(MessageTableCellFactory.getTimestampCellFactory());

        comboBaudrate.setItems(ConnectOptionFactory.getBaudRates());
        comboOpenMode.setItems(ConnectOptionFactory.getOpenModes());

        comboBaudrate.getSelectionModel().select(4);
        comboOpenMode.getSelectionModel().select(0);

        tableMessages.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

        executor.execute(model.getUpdateRunnable());
    }

    public void clickConnect(ActionEvent actionEvent) {
        if (System.getenv("DEBUG") == null || System.getenv("DEBUG").equals("false")) {
            String port = txtPort.getText();
            int baudRate = comboBaudrate.getSelectionModel().getSelectedItem();
            USBtin.OpenMode openMode = comboOpenMode.getSelectionModel().getSelectedItem();

            if(port != null && port.length() > 0 && openMode != null){
                USBtin usBtin;
                if(model.getUSBtin() != null){
                    try {
                        model.getUSBtin().closeCANChannel();
                    } catch (USBtinException ignored) {

                    }
                    try {
                        model.getUSBtin().disconnect();
                    } catch (USBtinException e) {
                        e.printStackTrace();
                    }
                    usBtin = model.getUSBtin();
                }else{
                    usBtin = new USBtin();
                    usBtin.addMessageListener(CANRepository.getInstance());
                }

                try {
                    usBtin.connect(port);
                    usBtin.openCANChannel(baudRate, openMode);

                } catch (USBtinException e) {
                    Alert dialog = new Alert(Alert.AlertType.ERROR);
                    dialog.setTitle("Connection error");
                    dialog.setContentText(e.getMessage());
                    dialog.show();
                }

                model.setUSBtin(usBtin);
            }

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
        model.getUpdateRunnable().setPaused(true);
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
            model.getUpdateRunnable().setPaused(false);
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

    public void clickNote(ActionEvent actionEvent) {
        TimedCANMessage timedCANMessage = tableMessages.getSelectionModel().getSelectedItem();

        if(timedCANMessage != null){
            openNotes(timedCANMessage);
        }
    }

    public void clickNotebook(ActionEvent actionEvent) {
        openNotes(null);
    }

    private void openNotes(TimedCANMessage timedCANMessage){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../note/noteLayout.fxml"));

        try {
            Parent root = loader.load();
            NoteController controller = loader.getController();
            Stage stage = new Stage();

            stage.setTitle("Note");
            stage.setScene(new Scene(root));
            controller.setTimedCANMesage(timedCANMessage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
