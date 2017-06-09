package rk.rynkbit.can.presenter.speedometer;

import de.fischl.usbtin.*;
import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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
    private static final String VERSION = "0.1.0";

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
//
//        receiveCANMessage(new CANMessage(0x201,
//                new byte[]{(byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x00, (byte)0x00, (byte)0x0, (byte)0x0}));

        setUSBTin(new USBtin());
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
        boolean usbConnected = false;

        if(portNames.length > 0){
            do{
                if(usbConnected == false) {
                    System.out.println("Connecting to " + portNames[count]);
                    try {
                        usBtin.connect(portNames[count]);
//                    try {
//                        usBtin.setFilter(new FilterChain[]{
//                                new FilterChain(new FilterMask(0xff), new FilterValue[]{
//                                        new FilterValue(0x201)
//                                })
//                        });
//                    } catch (USBtinException e) {
//                        e.printStackTrace();
//                    }
                        usbConnected = true;
                    } catch (USBtinException e) {
                        System.out.println(e.getMessage());
                    }
                }
                if(usbConnected == true) {
                    try {
                        usBtin.openCANChannel(125000, USBtin.OpenMode.ACTIVE);


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
        if(canMessage.getId() == 0x201){
            double rpm1 = Byte.toUnsignedInt(canMessage.getData()[0]);
            double rpm2 = Byte.toUnsignedInt(canMessage.getData()[1]);
            double v1 = Byte.toUnsignedInt(canMessage.getData()[4]);
            double v2 = Byte.toUnsignedInt(canMessage.getData()[5]);
            double throttle1 = Byte.toUnsignedInt(canMessage.getData()[6]);

            double v = (v1 + v2/255.0) * 0.75 * 3.6;
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
