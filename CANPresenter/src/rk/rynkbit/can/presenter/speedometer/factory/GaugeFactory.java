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

    public static Gauge createGauge(String title,
                                    String unit,
                                    double minValue,
                                    double maxValue,
                                    boolean thresholdVisible,
                                    double thresholdValue,
                                    Stop... stops){
        return GaugeBuilder
                .create()
                .minWidth(380)
                .minHeight(180)
                .foregroundBaseColor(Color.WHITE)
                .tickLabelColor(Color.BLACK)
                .valueColor(Color.NAVY)
                .knobColor(Color.BLACK)
                .title(title)
                .titleColor(Color.BLACK)
                .unit(unit)
                .unitColor(Color.BLACK)
                .minValue(minValue)
                .maxValue(maxValue)
                .angleRange(270)
                .needleShape(Gauge.NeedleShape.ANGLED)
                .needleSize(Gauge.NeedleSize.STANDARD)
                .needleColor(Color.BLUE)
                .startFromZero(true)
                .returnToZero(false)
                .threshold(thresholdValue)
                .thresholdColor(Color.RED)
                .thresholdVisible(thresholdVisible)
                .gradientBarEnabled(true)
                .gradientBarStops(stops)
                .animated(true)
                .animationDuration(10)
                .build();
    }
}
