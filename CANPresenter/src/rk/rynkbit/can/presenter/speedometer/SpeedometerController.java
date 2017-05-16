package rk.rynkbit.can.presenter.speedometer;

import com.sun.javafx.tk.FontMetrics;
import de.fischl.usbtin.CANMessage;
import de.fischl.usbtin.CANMessageListener;
import de.fischl.usbtin.USBtin;
import de.fischl.usbtin.USBtinException;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import tk.rynkbit.can.logic.CANRepository;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by michael on 16.05.17.
 */
public class SpeedometerController implements Initializable, CANMessageListener {
    public Canvas canvas;

    private SpeedometerModel model = new SpeedometerModel();

    private double width;
    private double height;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        width = canvas.getWidth();
        height = canvas.getHeight();

        gc.setStroke(Color.BLUE);

        //Middle line
//        gc.strokeLine(width / 2, 0, width / 2, height);
        //Rectangle left
        gc.strokeRoundRect(10, 10, width / 2 - 20, 100,10, 10);
        //Rectangle right
        gc.strokeRoundRect(10 + width / 2, 10, width / 2 - 20, 100,10, 10);

        receiveCANMessage(new CANMessage(0x201, new byte[]{(byte)0x02, (byte)0x12}));
        connect();
    }

    private void connect() {
        USBtin usBtin = new USBtin();
        model.setUSBtin(usBtin);
        try {
            usBtin.addMessageListener(this);
            usBtin.connect("/dev/ttyACM0");
            usBtin.openCANChannel(125000, USBtin.OpenMode.ACTIVE);
        } catch (USBtinException ignored) {

        }
    }

    @Override
    public void receiveCANMessage(CANMessage canMessage) {
        if(canMessage.getId() == 0x201){
            Platform.runLater(() -> {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                int byte1 = Byte.toUnsignedInt(canMessage.getData()[0]);
                int byte2 = Byte.toUnsignedInt(canMessage.getData()[1]);
                int rpm = byte1 * 1000 + byte2 * 4;
                String rpmString = rpm + " RPM";
                Text rpmText = new Text(rpmString);
                Font bigFont = Font.font("Sans Serif", 60);

                rpmText.setFont(bigFont);

                gc.setFont(bigFont);
                gc.setStroke(Color.ALICEBLUE);
                gc.setFill(Color.ALICEBLUE);
                gc.fillText(rpmString,
                        -30 + width / 2 - rpmText.getBoundsInLocal().getWidth(),
                        115 - rpmText.getBoundsInLocal().getHeight() / 2);
            });
        }
    }

    public void stop(){
        try {
            model.getUSBtin().closeCANChannel();
        } catch (USBtinException ignored) {

        }
        try {
            model.getUSBtin().disconnect();
        } catch (USBtinException ignored) {

        }
    }
}
