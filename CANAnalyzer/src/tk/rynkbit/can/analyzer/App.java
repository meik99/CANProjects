package tk.rynkbit.can.analyzer;

import de.fischl.usbtin.USBtinException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tk.rynkbit.can.analyzer.main.MainController;

/**
 * Created by mrynkiewicz on 14.05.17.
 */
public class App extends Application{
    public static void main(String[] args) throws USBtinException {
        launch(args);
    }

    private FXMLLoader loader;
    private MainController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("main/mainLayout.fxml"));

        AnchorPane anchorPane = loader.load();
        Scene rootScene = new Scene(anchorPane);
        Stage root = primaryStage;

        root.setWidth(1200);
        root.setHeight(800);
        root.setScene(rootScene);
        root.show();

        controller = loader.getController();
    }

    @Override
    public void stop() throws Exception {

        if(controller != null){
            controller.stop();
        }

        super.stop();
    }
}
