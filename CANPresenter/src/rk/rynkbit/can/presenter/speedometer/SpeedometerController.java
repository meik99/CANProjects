package rk.rynkbit.can.presenter.speedometer;

import de.fischl.usbtin.*;
import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import jssc.SerialPortList;
import rk.rynkbit.can.presenter.speedometer.factory.GaugeFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;


/**
 * Created by michael on 16.05.17.
 */
public class SpeedometerController implements Initializable, CANMessageListener {
    private static final String VERSION = "0.1.0-2";

    public HBox primaryBox;
    public HBox secondaryBox;
    public Label labelVersion;
    public Label labelMessageCount;
    public AnchorPane rootPane;

    private SpeedometerModel model = new SpeedometerModel();

    private Gauge rpmGauge;
    private Gauge velocityGauge;
    private Gauge throttleGauge;
    private Gauge fuelGauge;
    private Gauge fuelUsageGauge;
    private Gauge coolingGauge;

    private final SimpleDoubleProperty rpm = new SimpleDoubleProperty();
    private final SimpleDoubleProperty v = new SimpleDoubleProperty();
    private final SimpleDoubleProperty cooling = new SimpleDoubleProperty();
    private final SimpleDoubleProperty fuelUsage = new SimpleDoubleProperty();
    private final SimpleDoubleProperty fuel = new SimpleDoubleProperty();
    private final SimpleDoubleProperty throttle = new SimpleDoubleProperty();

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
        fuelGauge =
                GaugeFactory.createFuelGauge();
        fuelUsageGauge =
                GaugeFactory.createActualFuelUsageGauge();
        coolingGauge =
                GaugeFactory.createRefrigarateGauge();

        rpmGauge.valueProperty().bind(rpm);
        velocityGauge.valueProperty().bind(v);
        throttleGauge.valueProperty().bind(throttle);
        fuelGauge.valueProperty().bind(fuel);
        fuelUsageGauge.valueProperty().bind(fuelUsage);
        coolingGauge.valueProperty().bind(cooling);

        primaryBox.getChildren().add(rpmGauge);
        primaryBox.getChildren().add(velocityGauge);
        secondaryBox.getChildren().add(coolingGauge);
        secondaryBox.getChildren().add(fuelUsageGauge);
        secondaryBox.getChildren().add(fuelGauge);
        secondaryBox.getChildren().add(throttleGauge);

        labelVersion.setText(VERSION);

        setUSBTin(new USBtin());

        receiveCANMessage(new CANMessage(0x280,
                new byte[]{(byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x00, (byte)0x00, (byte)0x0, (byte)0x0}));
        receiveCANMessage(new CANMessage(0x320,
                new byte[]{(byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x00, (byte)0x00, (byte)0x0, (byte)0x0}));

    }

    public void setUSBTin(USBtin usBtin) {
        model.setUSBtin(usBtin);

        String[] portNames = SerialPortList.getPortNames(Pattern.compile("ttyACM[0-9]*"));
        boolean connected = false;

        final StringBuilder ports = new StringBuilder();
        int count = 0;

        System.out.println("Ports found: ");

        for (String port :
                portNames) {
            ports.append(port);
            ports.append(System.lineSeparator());
        }

        System.out.println(ports.toString());
        System.out.println();

        if(portNames.length > 0){
            do{
                boolean usbConnected = false;
                System.out.println("Connecting to " + portNames[count]);
                try {
                    usBtin.connect(portNames[count]);
                    usbConnected = true;
                } catch (USBtinException e) {
                    System.out.println(e.getMessage());
                }
                if(usbConnected == true) {
                    try {
                        usBtin.openCANChannel(500000, USBtin.OpenMode.ACTIVE);
                        connected = true;
                    } catch (USBtinException e) {
                        System.out.println(e.getMessage());
                        try {
                            usBtin.disconnect();
                        } catch (USBtinException e1) {
                            System.out.println(e1.getMessage());
                        }
                    }
                }

                count++;
            }while (connected == false && count < portNames.length);
        }

        if(connected == true){
            usBtin.addMessageListener(this);
        }else{
            System.out.println("Could not connect to a port");
            Platform.runLater(() ->{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Port error");
                alert.setContentText(
                        "No usable port found\n" +
                                "Searched ports: " +
                                ports.toString()
                );
                alert.initOwner(rootPane.getScene().getWindow());
                alert.showAndWait()
                        .filter(response -> response == ButtonType.OK);

            });
        }
    }

    @Override
    public void receiveCANMessage(CANMessage canMessage) {
        Platform.runLater(() -> {
            if(canMessage.getId() == 0x280){
                    double rpm1 = Byte.toUnsignedInt(canMessage.getData()[2]);
                    double rpm2 = Byte.toUnsignedInt(canMessage.getData()[3]);
                    throttle.set(Byte.toUnsignedInt(canMessage.getData()[5]) * 0.4);
                    fuelUsage.set(Byte.toUnsignedInt(canMessage.getData()[7]) * 0.1);

                    System.out.println(rpm1);
                    System.out.println(rpm2);

                    if(rpm2 == 0) rpm2 = 0xff;

                    rpm.set(((rpm2 + rpm1 / 255) * 0.5));
            }
            if(canMessage.getId() == 0x320){
                fuel.set(Byte.toUnsignedInt(canMessage.getData()[2]));
                double v1 = Byte.toUnsignedInt(canMessage.getData()[3]);
                double v2 = Byte.toUnsignedInt(canMessage.getData()[4]);
                double vTemp = (v2 + v1 / 255) * 3.6 * 0.38;


                v.set(vTemp);
            }
            if(canMessage.getId() == 0x288){
                cooling.set(
                        (Byte.toUnsignedInt(canMessage.getData()[1]) - 64) * 0.75
                );
            }
        });
    }

    public void stop(){
        if(model.getUSBtin() != null) {
            try {
                model.getUSBtin().closeCANChannel();
            } catch (USBtinException | NullPointerException ignored) {

            }
            try {
                model.getUSBtin().disconnect();
            } catch (USBtinException | NullPointerException ignored) {

            }
        }
    }

    public void clickHBox(MouseEvent mouseEvent) {
        stop();
        System.exit(0);
    }
}
