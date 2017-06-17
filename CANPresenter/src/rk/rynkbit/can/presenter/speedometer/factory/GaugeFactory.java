package rk.rynkbit.can.presenter.speedometer.factory;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

/**
 * Created by mrynkiewicz on 20.05.17.
 */
public class GaugeFactory {
    private GaugeFactory(){}

    private static final int MIN_WIDTH_PRIMARY = 512;
    private static final int MAX_HEIGHT_SECONDARY = 266;

    public static Gauge createRPMGauge(){
        return GaugeBuilder.create()
                .title("RPM")
                .skinType(Gauge.SkinType.DIGITAL)
                .minValue(0)
                .maxValue(7000)
                .barColor(Color.RED)
                .foregroundBaseColor(Color.RED)
                .knobVisible(false)
                .needleShape(Gauge.NeedleShape.FLAT)
                .needleSize(Gauge.NeedleSize.THIN)
                .gradientBarEnabled(true)
                .animated(false)
                .startFromZero(true)
                .build();
    }

    public static Gauge createVelocityGauge() {
        return GaugeBuilder
                .create()
                .skinType(Gauge.SkinType.DIGITAL)
                .title("Speed")
                .unit("km/h")
                .minValue(0)
                .maxValue(200)
                .foregroundBaseColor(Color.RED)
                .barColor(Color.RED)
                .animated(false)
                .startFromZero(true)
                .build();
    }

    public static Gauge createThrottleGauge() {
        return GaugeBuilder
                .create()
                .skinType(Gauge.SkinType.DIGITAL)
                .maxHeight(MAX_HEIGHT_SECONDARY)
                .title("Throttle")
                .unit("%")
                .minValue(0)
                .maxValue(100)
                .startFromZero(true)
                .foregroundBaseColor(Color.CYAN)
                .barColor(Color.CYAN)
                .animated(false)
                .startFromZero(true)
                .build();
    }

    public static Gauge createActualFuelUsageGauge() {
        return GaugeBuilder
                .create()
                .maxHeight(MAX_HEIGHT_SECONDARY)
                .title("Actual Usage")
                .unit("Liter/100 km")
                .skinType(Gauge.SkinType.DIGITAL)
                .minValue(0)
                .maxValue(20)
                .decimals(2)
                .startFromZero(true)
                .foregroundBaseColor(Color.LIGHTGREEN)
                .barColor(Color.LIGHTGREEN)
                .animated(false)
                .startFromZero(true)
                .build();
    }

    public static Gauge createFuelGauge() {
        return GaugeBuilder
                .create()
                .maxHeight(MAX_HEIGHT_SECONDARY)
                .title("Fuel")
                .unit("Liter")
                .skinType(Gauge.SkinType.DIGITAL)
                .minValue(0)
                .maxValue(55)
                .valueVisible(true)
                .startFromZero(true)
                .foregroundBaseColor(Color.LIGHTGREEN)
                .barColor(Color.LIGHTGREEN)
                .animated(false)
                .startFromZero(true)
                .build();
    }

    public static Gauge createRefrigarateGauge() {
        return GaugeBuilder
                .create()
                .maxHeight(MAX_HEIGHT_SECONDARY)
                .title("Cooling Fluid")
                .unit("°C")
                .skinType(Gauge.SkinType.DIGITAL)
                .minValue(0)
                .maxValue(120)
                .startFromZero(true)
                .foregroundBaseColor(Color.ORANGE)
                .barColor(Color.ORANGE)
                .animated(false)
                .startFromZero(true)
                .build();
    }

    public static Gauge createAverageUsageGauge(){
        return GaugeBuilder
                .create()
                .maxHeight(175)
                .averagingPeriod(100)
                .title("Usage")
                .unit("l/100km")
                .skinType(Gauge.SkinType.TILE_SPARK_LINE)
                .startFromZero(true)
//                .foregroundBaseColor(Color.ORANGE)
//                .barColor(Color.ORANGE)
                .animated(false)
                .startFromZero(true)
                .padding(new Insets(0, 10, 0, 0))
                .build();
    }

    public static Gauge createRemainingDistanceGauge(){
        return GaugeBuilder
                .create()
                .maxHeight(175)
                .averagingPeriod(100)
                .title("Remaining")
                .unit("km")
                .skinType(Gauge.SkinType.TILE_SPARK_LINE)
                .startFromZero(true)
//                .foregroundBaseColor(Color.ORANGE)
//                .barColor(Color.ORANGE)
                .animated(false)
                .startFromZero(true)
                .build();
    }
}
