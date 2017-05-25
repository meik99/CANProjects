package rk.rynkbit.can.presenter.connect;

import de.fischl.usbtin.USBtin;
import de.fischl.usbtin.USBtinException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import rk.rynkbit.can.presenter.speedometer.SpeedometerController;

import java.io.IOException;

/**
 * Created by mrynkiewicz on 25.05.17.
 */
public class ConnectController {
    public AnchorPane rootPane;

    private USBtin usBtin;
    private SpeedometerController controller;

    public void clickStart(ActionEvent actionEvent) {
        try {
            usBtin = new USBtin();
            usBtin.connect("/dev/ttyACM0");

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../speedometer/speedometerLayout.fxml"));

            Parent root = loader.load();
            controller = loader.getController();
            controller.setUSBTin(usBtin);
            rootPane
                    .getScene()
                    .getWindow()
                    .getScene()
                    .setRoot(root);

        } catch (USBtinException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.setTitle("Error");
            alert.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        if(controller != null){
            controller.stop();
        }
    }
}
