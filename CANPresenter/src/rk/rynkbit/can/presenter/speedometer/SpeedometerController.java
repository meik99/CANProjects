package rk.rynkbit.can.presenter.speedometer;

import de.fischl.usbtin.CANMessage;
import de.fischl.usbtin.CANMessageListener;
import de.fischl.usbtin.USBtin;
import de.fischl.usbtin.USBtinException;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import rk.rynkbit.can.presenter.speedometer.factory.GaugeFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by michael on 16.05.17.
 */
public class SpeedometerController implements Initializable, CANMessageListener {
    public HBox primaryBox;
    public HBox secondaryBox;

    private SpeedometerModel model = new SpeedometerModel();

    private double width;
    private double height;

    private final double throttleScale = 100.0 / 0xb4;

    private final Color BACKGROUND_COLOR = Color.WHITE;
    private final Color MAIN_COLOR = Color.BLUE;
    private final Color TEXT_COLOR = Color.BLACK;

    private Gauge rpmGauge;
    private Gauge velocityGauge;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rpmGauge = GaugeFactory.createGauge(
                "RPM", "rotations / minute", 0, 7000, true, 6000,
                new Stop(0, Color.WHITE), new Stop(1.0 / 7 * 5, Color.BLUE),
                new Stop(1.0/7 * 6, Color.RED), new Stop(1.0, Color.DARKRED)
        );
        velocityGauge = GaugeFactory.createGauge(
                "Velocity", "km/h", 0, 270, false, 0,
                new Stop(0, Color.WHITE), new Stop(1, Color.RED)
        );

        primaryBox.getChildren().add(rpmGauge);
        primaryBox.getChildren().add(velocityGauge);

        receiveCANMessage(new CANMessage(0x201,
                new byte[]{(byte)0x01, (byte)0xe8, (byte)0x0, (byte)0x0, (byte)0x33, (byte)0x40, (byte)0x0, (byte)0x0}));
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
                int rpm1 = Byte.toUnsignedInt(canMessage.getData()[0]);
                int rpm2 = Byte.toUnsignedInt(canMessage.getData()[1]);
                int v1 = Byte.toUnsignedInt(canMessage.getData()[4]);
                int v2 = Byte.toUnsignedInt(canMessage.getData()[5]);

                if(v2 < 150 || v2 > 250){
                    v2 = 200;
                }
                int rpm = rpm1 * 250 + rpm2;
                double v = v1 * 3.6 * (v2 / 255.0);

                rpmGauge.valueProperty().setValue(rpm);
                velocityGauge.valueProperty().setValue(v);
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
