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

    private final double throttleScale = 100.0 / 0xb4;

    private final Color BACKGROUND_COLOR = Color.WHITE;
    private final Color MAIN_COLOR = Color.BLUE;
    private final Color TEXT_COLOR = Color.BLACK;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        width = canvas.getWidth();
        height = canvas.getHeight();



        receiveCANMessage(new CANMessage(0x201,
                new byte[]{(byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0}));
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
                int rpm1 = Byte.toUnsignedInt(canMessage.getData()[0]);
                int rpm2 = Byte.toUnsignedInt(canMessage.getData()[1]);
                int rpm = rpm1 * 100 + rpm2 * 4;
                String rpmString = rpm + " RPM";
                Text rpmText = new Text(rpmString);
                Font bigFont = Font.font("Sans Serif", 60);

                double throttle = Byte.toUnsignedInt(canMessage.getData()[6]) * throttleScale;
                String throttleString = String.format("%.0f °", throttle);
                Text throttleText = new Text(throttleString);





                rpmText.setFont(bigFont);
                throttleText.setFont(bigFont);

                gc.setFill(BACKGROUND_COLOR);
                gc.clearRect(0, 0, width, height);
                gc.fillRect(0, 0, width, height);

                gc.setStroke(MAIN_COLOR);

                //Rectangle top left
                gc.strokeRoundRect(10, 10, width / 2 - 20, 100,10, 10);
                //Rectangle top right
                gc.strokeRoundRect(10 + width / 2, 10, width / 2 - 20, 100,10, 10);
                //Rectangle clutch (middle left)
                gc.strokeRoundRect(10, 220, width / 3 - 20, 100,10, 10);
                //Rectangle break (middle middle)
                gc.strokeRoundRect(10 + width - width / 3, 220, width / 3 - 20, 100,10, 10);
                //Rectangle throttle (middle right)
                gc.strokeRoundRect(10 + width - 2*width / 3, 220, width / 3 - 20, 100,10, 10);

                gc.setFont(bigFont);
                gc.setStroke(TEXT_COLOR);
                gc.setFill(TEXT_COLOR);

                gc.fillText(rpmString,
                        -30 + width / 2 - rpmText.getBoundsInLocal().getWidth(),
                        115 - rpmText.getBoundsInLocal().getHeight() / 2);
                gc.fillText(throttleString,
                        -30 + width - throttleText.getBoundsInLocal().getWidth(),
                        330 - throttleText.getBoundsInLocal().getHeight() / 2);


                gc.fillText("Clutch", 10, 200);
                gc.fillText("Break", 10 + width - 2*width / 3, 200);
                gc.fillText("Throttle", 10 + width - width / 3, 200);
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
