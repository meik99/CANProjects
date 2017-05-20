package rk.rynkbit.can.presenter.speedometer;

import de.fischl.usbtin.CANMessage;
import de.fischl.usbtin.CANMessageListener;
import de.fischl.usbtin.USBtin;
import de.fischl.usbtin.USBtinException;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Marker;
import eu.hansolo.medusa.Section;
import eu.hansolo.medusa.tools.GradientLookup;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import rk.rynkbit.can.presenter.speedometer.factory.GaugeFactory;
import tk.rynkbit.can.logic.CANRepository;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by michael on 16.05.17.
 */
public class SpeedometerController implements Initializable, CANMessageListener {
    public HBox primaryBox;
    public HBox secondaryBox;

    private SpeedometerModel model = new SpeedometerModel();

    private Gauge rpmGauge;
    private Gauge velocityGauge;
    private Gauge throttleGauge;
    private Gauge breakGauge;
    private Gauge clutchGauge;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        primaryBox.setPadding(new Insets(10));
        primaryBox.setSpacing(50);
        primaryBox.setAlignment(Pos.CENTER);

        secondaryBox.setPadding(new Insets(10));
        secondaryBox.setSpacing(50);
        secondaryBox.setAlignment(Pos.CENTER);

        rpmGauge =
                GaugeFactory.createRPMGauge();
        velocityGauge =
                GaugeFactory.createVelocityGauge();
        throttleGauge =
                GaugeFactory.createThrottleGauge();
        breakGauge =
                GaugeFactory.createBreakGauge();
        clutchGauge =
                GaugeFactory.createClutchGauge();

        primaryBox.getChildren().add(rpmGauge);
        primaryBox.getChildren().add(velocityGauge);
        secondaryBox.getChildren().add(clutchGauge);
        secondaryBox.getChildren().add(breakGauge);
        secondaryBox.getChildren().add(throttleGauge);

//        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
//                Runtime.getRuntime().availableProcessors()
//        );
//        executor.execute(new Runnable() {
//            Random random = new Random();
//
//            @Override
//            public void run() {
//                while (true){
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    receiveCANMessage(new CANMessage(0x201,
//                            new byte[]{
//                                    (byte)random.nextInt(0x27), (byte)random.nextInt(0xff), (byte)0x0,
//                                    (byte)0x0, (byte)random.nextInt(33), (byte)random.nextInt(0xff),
//                                    (byte)random.nextInt(0xb4), (byte)0x0}));
//                }
//            }
//        });
//
        receiveCANMessage(new CANMessage(0x201,
                new byte[]{(byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x00, (byte)0x00, (byte)0x0, (byte)0x0}));
//        receiveCANMessage(new CANMessage(0x201,
//                new byte[]{(byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0xb4, (byte)0x0}));
//        receiveCANMessage(new CANMessage(0x201,
//                new byte[]{(byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x3f, (byte)0x0}));
//        receiveCANMessage(new CANMessage(0x201,
//                new byte[]{(byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0xa5, (byte)0x0}));
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
                double rpm1 = Byte.toUnsignedInt(canMessage.getData()[0]);
                double rpm2 = Byte.toUnsignedInt(canMessage.getData()[1]);
                double v1 = Byte.toUnsignedInt(canMessage.getData()[4]);
                double v2 = Byte.toUnsignedInt(canMessage.getData()[5]);
                double throttle1 = Byte.toUnsignedInt(canMessage.getData()[6]);

                double vScale = v2 / (double)0xff;

                double v = (v1 + v2/255.0) * 195.0 / 255.0 * 3.6;
                double rpm = rpm1 * 250 + rpm2;
                double throttle = 100.0 / 0xb4 * throttle1;

                rpmGauge.valueProperty().set(rpm);
                velocityGauge.valueProperty().set(v);
                throttleGauge.valueProperty().set(throttle);
                clutchGauge.valueProperty().set(v1);
                breakGauge.valueProperty().set(v2);
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
