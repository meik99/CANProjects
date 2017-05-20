package rk.rynkbit.can.presenter.speedometer.factory;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;

/**
 * Created by mrynkiewicz on 20.05.17.
 */
public class GaugeFactory {
    private GaugeFactory(){}

    private static final int MIN_WIDTH_PRIMARY = 320;
    private static final int MAX_HEIGHT_SECONDARY = 180;

    public static Gauge createRPMGauge(){
        return GaugeBuilder.create()
                .title("RPM")
                .minWidth(MIN_WIDTH_PRIMARY)
                .minHeight(140)
                .skinType(Gauge.SkinType.DIGITAL)
                .minValue(0)
                .maxValue(7000)
                .barColor(Color.RED)
                .backgroundPaint(Color.BLACK)
                .foregroundBaseColor(Color.RED)
                .knobVisible(false)
                .needleShape(Gauge.NeedleShape.FLAT)
                .needleSize(Gauge.NeedleSize.THIN)
                .gradientBarEnabled(true)
                .gradientBarStops(
                        new Stop(0, Color.BLACK),
                        new Stop(1.0 / 7000 * 5999, Color.BLACK),
                        new Stop(1.0 / 7000 * 6000, Color.DARKRED),
                        new Stop(1, Color.DARKRED))
                .animated(true)
                .animationDuration(100)
                .startFromZero(true)
                .build();
    }

    public static Gauge createVelocityGauge() {
        return GaugeBuilder
                .create()
                .title("Speed")
                .unit("km/h")
                .minWidth(MIN_WIDTH_PRIMARY)
                .minHeight(140)
                .minValue(0)
                .maxValue(200)
                .skinType(Gauge.SkinType.DIGITAL)
                .foregroundBaseColor(Color.RED)
                .barColor(Color.RED)
                .gradientBarEnabled(true)
                .gradientBarStops(
                        new Stop(0, Color.DARKRED),
                        new Stop(1, Color.GREEN))
                .animated(true)
                .animationDuration(100)
                .startFromZero(true)
                .build();
    }

    public static Gauge createThrottleGauge() {
        return GaugeBuilder
                .create()
                .maxHeight(MAX_HEIGHT_SECONDARY)
                .title("Throttle")
                .unit("%")
                .skinType(Gauge.SkinType.DIGITAL)
                .minValue(0)
                .maxValue(100)
                .startFromZero(true)
                .barBackgroundColor(Color.BLACK)
                .foregroundBaseColor(Color.GREEN)
                .barColor(Color.GREEN)
                .animated(true)
                .animationDuration(100)
                .startFromZero(true)
                .build();
    }

    public static Gauge createClutchGauge() {
        return GaugeBuilder
                .create()
                .maxHeight(MAX_HEIGHT_SECONDARY)
                .title("V1")
                .unit("Byte")
                .skinType(Gauge.SkinType.DIGITAL)
                .minValue(0)
                .maxValue(0xff)
                .startFromZero(true)
                .barBackgroundColor(Color.BLACK)
                .foregroundBaseColor(Color.GREEN)
                .barColor(Color.GREEN)
                .animated(true)
                .animationDuration(1)
                .startFromZero(true)
                .build();
    }

    public static Gauge createBreakGauge() {
        return GaugeBuilder
                .create()
                .maxHeight(MAX_HEIGHT_SECONDARY)
                .title("V2")
                .unit("Byte")
                .skinType(Gauge.SkinType.DIGITAL)
                .minValue(0)
                .maxValue(0xff)
                .startFromZero(true)
                .barBackgroundColor(Color.BLACK)
                .foregroundBaseColor(Color.GREEN)
                .barColor(Color.GREEN)
                .animated(true)
                .animationDuration(1)
                .startFromZero(true)
                .build();
    }
}
