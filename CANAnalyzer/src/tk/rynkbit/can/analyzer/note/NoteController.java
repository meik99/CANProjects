package tk.rynkbit.can.analyzer.note;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tk.rynkbit.can.logic.CANRepository;
import tk.rynkbit.can.logic.models.TimedCANMessage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by michael on 16.05.17.
 */
public class NoteController implements Initializable{
    public TextField txtId;
    public TextField txtData;
    public TextArea txtNote;
    public Button btnSave;
    public Button btnCancel;
    public AnchorPane rootPane;
    public ListView<String> listNotes;

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
        }
    }

    public void clickSave(ActionEvent actionEvent) {

        if(model.getTimedCANMessage() != null){
            TimedCANMessage message = model.getTimedCANMessage();
            JsonObject object = new JsonObject();

            object.addProperty("id", Integer.toHexString(message.getId()));
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateNoteList();
    }

    private void updateNoteList(){
        File[] fileList;
        ObservableList<String> notes = FXCollections.observableList(new LinkedList<>());
        File dir = new File("./");
        fileList = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".note");
            }
        });

        for (File file:
                fileList) {
            notes.add(file.getName().substring(0, file.getName().indexOf(".note")));
        }

        listNotes.setItems(notes);
    }

    public void clickNotes(MouseEvent mouseEvent) {
        String item = listNotes.getSelectionModel().getSelectedItem();

        if(item != null) {
            File note = new File(item + ".note");

            if(note.exists()){
                JsonParser parser = new JsonParser();
                JsonElement element;
                try {
                    element = parser.parse(new FileReader(note));

                    if(element != null && element.isJsonObject()){
                        JsonObject object = element.getAsJsonObject();

                        txtId.setText(object.get("id").getAsString());
                        txtData.setText(object.get("data").getAsString());
                        txtNote.setText(object.get("note").getAsString());
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void clickDelete(ActionEvent actionEvent) {
        String item = listNotes.getSelectionModel().getSelectedItem();

        if(item != null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete note");
            alert.setContentText("Really delete note " + item + "?");

            ButtonType type = alert.showAndWait().get();

            if(type == ButtonType.OK){
                new File(item + ".note").delete();
                updateNoteList();
            }
        }
    }
}
