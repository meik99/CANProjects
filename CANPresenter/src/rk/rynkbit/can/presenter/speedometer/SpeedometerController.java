package rk.rynkbit.can.presenter.speedometer;

import de.fischl.usbtin.*;
import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import rk.rynkbit.can.presenter.speedometer.factory.GaugeFactory;

import java.net.URL;
import java.util.ResourceBundle;



/**
 * Created by michael on 16.05.17.
 */
public class SpeedometerController implements Initializable, CANMessageListener {
    private static final String VERSION = "0.0.6";

    public HBox primaryBox;
    public HBox secondaryBox;
    public Label labelVersion;
    public Label labelMessageCount;
    public AnchorPane rootPane;

    private SpeedometerModel model = new SpeedometerModel();

    private Gauge rpmGauge;
    private Gauge velocityGauge;
    private Gauge throttleGauge;
    private Gauge breakGauge;
    private Gauge clutchGauge;

    private volatile int messageCount = 0;

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

        labelVersion.setText(VERSION);

        receiveCANMessage(new CANMessage(0x201,
                new byte[]{(byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x00, (byte)0x00, (byte)0x0, (byte)0x0}));

        //setUSBTin(new USBtin());
    }

    public void setUSBTin(USBtin usbTin) {
        model.setUSBtin(usbTin);

        boolean dirty = true;
        int count = 0;

        while(dirty && count < 10000){
            try {
                usbTin.connect("/dev/ttyACM0");
                dirty = false;
            } catch (USBtinException e) {
                System.out.println(e.getMessage());
                count ++;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            usbTin.setFilter(new FilterChain[]{
                    new FilterChain(
                            new FilterMask(0xfff, (byte)0x00, (byte)0x00),
                            new FilterValue[]{
                                    new FilterValue(0x201, (byte)0x00, (byte)0x00)
                            }
                    )
            });
            usbTin.openCANChannel(125000, USBtin.OpenMode.ACTIVE);
            usbTin.addMessageListener(this);
        } catch (USBtinException ignored) {
            System.out.println(ignored.getMessage());
        }
    }

    @Override
    public void receiveCANMessage(CANMessage canMessage) {
        if(canMessage.getId() == 0x201){
            double rpm1 = Byte.toUnsignedInt(canMessage.getData()[0]);
            double rpm2 = Byte.toUnsignedInt(canMessage.getData()[1]);
            double v1 = Byte.toUnsignedInt(canMessage.getData()[4]);
            double v2 = Byte.toUnsignedInt(canMessage.getData()[5]);
            double throttle1 = Byte.toUnsignedInt(canMessage.getData()[6]);

            double v = (v1 + v2/255.0) * 195.0 / 255.0 * 3.6;
            double rpm = rpm1 * 250 + rpm2;
            double throttle = 100.0 / 0xb2 * throttle1;

            Platform.runLater(() -> {
                rpmGauge.valueProperty().set(rpm);
                velocityGauge.valueProperty().set(v);
                throttleGauge.valueProperty().set(throttle);
                clutchGauge.valueProperty().set(v1);
                breakGauge.valueProperty().set(v2);
                messageCount++;
                labelMessageCount.textProperty().setValue(String.valueOf(messageCount));
            });
        }
    }

    public void stop(){
        if(model.getUSBtin() != null) {
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

    public void clickHBox(MouseEvent mouseEvent) {
        stop();
        System.exit(0);
    }
}
