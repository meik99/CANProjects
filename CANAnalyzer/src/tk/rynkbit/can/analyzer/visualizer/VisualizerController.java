package tk.rynkbit.can.analyzer.visualizer;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import tk.rynkbit.can.logic.models.TimedCANMessage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by michael on 15.05.17.
 */
public class VisualizerController implements Initializable{
    public AnchorPane rootPane;
    public ComboBox comboMessages;
    public AreaChart chart;

    private VisualizerModel model = new VisualizerModel();

    public void setCANMessage(TimedCANMessage timedCANMessage) {
        model.setCANMessage(timedCANMessage);
    }

    public void setParent(Parent previousWindow){
        model.setParent(previousWindow);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void clickBack(ActionEvent actionEvent) {
        rootPane.getScene().getWindow().getScene().setRoot(model.getParent());
    }

    public void changeMessage(ActionEvent actionEvent) {
    }
}
