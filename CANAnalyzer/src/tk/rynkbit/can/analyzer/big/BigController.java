package tk.rynkbit.can.analyzer.big;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import tk.rynkbit.can.analyzer.Controller;
import tk.rynkbit.can.analyzer.UpdateMessages;
import tk.rynkbit.can.logic.CANRepository;
import tk.rynkbit.can.logic.models.TimedCANMessage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by mrynkiewicz on 19.05.17.
 */
public class BigController extends Controller implements Initializable{
    public AnchorPane rootPane;
    public HBox hbox;
    public Label labelId;

    private int canId = -1;

    public void setCanId(int id){
        this.canId = id;
        labelId.setText("Id: " + Integer.toHexString(canId));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        executor.execute(new UpdateMessages(this));
    }

    @Override
    public void updateRepository() {
        if(canId > -1){
            TimedCANMessage message = CANRepository
                    .getInstance()
                    .getMessageMap()
                    .get(canId);
            Font font = new Font("Sans Serif", 48);

            if(message != null){
                Platform.runLater(() -> {
                    ObservableList<Node> nodes = hbox.getChildren();

                    int start = message.getData().length >= 4 ? 4 : 0;

                    if(nodes.size() < message.getData().length){
                        nodes.clear();


                        for (int i = start; i < message.getData().length; i++){
                            Label label = new Label(
                                    String.valueOf(
                                            Byte.toUnsignedInt(
                                                    message.getData()[i]))
                                    + " | "
                            );
                            label.setFont(font);
                            hbox.getChildren().add(label);
                        }
                    }else{
                        for (int i = start; i < message.getData().length; i++){
                            if(nodes.get(i) instanceof Label){
                                Label label = (Label) nodes.get(i);
                                label.setText(
                                        String.valueOf(
                                                Byte.toUnsignedInt(
                                                        message.getData()[i]
                                                ))
                                                + " | "
                                );
                            }else{
                                Label label = new Label(
                                        String.valueOf(
                                                Byte.toUnsignedInt(
                                                        message.getData()[i]
                                                ))
                                                + " | "
                                );
                                label.setFont(font);
                                nodes.set(i, label);
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void stop() {

    }
}
