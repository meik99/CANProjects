package rk.rynkbit.can.presenter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import rk.rynkbit.can.presenter.connect.ConnectController;
import rk.rynkbit.can.presenter.speedometer.SpeedometerController;
import rk.rynkbit.can.presenter.speedometer.SpeedometerModel;

/**
 * Created by michael on 16.05.17.
 */
public class App extends Application{
    public static void main(String[] args){
        launch(args);
    }

    ConnectController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("connect/connectLayout.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        primaryStage.setTitle("CANPresenter");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        controller.stop();
        super.stop();
    }
}
