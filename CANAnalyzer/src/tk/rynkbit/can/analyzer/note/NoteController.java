package tk.rynkbit.can.analyzer.note;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tk.rynkbit.can.logic.CANRepository;
import tk.rynkbit.can.logic.models.TimedCANMessage;

import java.io.*;

/**
 * Created by michael on 16.05.17.
 */
public class NoteController{
    public TextField txtId;
    public TextField txtData;
    public TextArea txtNote;
    public Button btnSave;
    public Button btnCancel;
    public AnchorPane rootPane;

    NoteModel model = new NoteModel();

    public void setTimedCANMesage(TimedCANMessage timedCANMessage){
        model.setTimedCANMessage(timedCANMessage);

        if(timedCANMessage != null){
            txtId.setText(Integer.toHexString(timedCANMessage.getId()));
            txtData.setText(CANRepository.bytesToHex(timedCANMessage.getData()));

            File file = new File(timedCANMessage.getId() + ".note");
            if(file.exists()){
                JsonParser parser = new JsonParser();
                try {
                    JsonElement element = parser.parse(new FileReader(file));
                    if(element != null) {
                        if(element.isJsonObject() == true) {
                            JsonObject object = element.getAsJsonObject();

                            txtNote.setText(object.get("note").getAsString());
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }else{
            throw new NullPointerException("timedCANMessage cannot be null");
        }
    }

    public void clickSave(ActionEvent actionEvent) {

        if(model.getTimedCANMessage() != null){
            TimedCANMessage message = model.getTimedCANMessage();
            JsonObject object = new JsonObject();

            object.addProperty("id", message.getId());
            object.addProperty("data", CANRepository.bytesToHex(message.getData()));
            object.addProperty("note", txtNote.getText());

            File file = new File(Integer.toHexString(message.getId()) + ".note");
            if(file.exists() == true){
                file.delete();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                FileWriter fileWriter = new FileWriter(
                        file
                );
                fileWriter.write(object.toString());
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        closeWindow();
    }

    public void clickCancel(ActionEvent actionEvent) {
        closeWindow();
    }

    private void closeWindow(){
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
}
