package tk.rynkbit.can.analyzer.visualizer;

import javafx.scene.Parent;
import tk.rynkbit.can.analyzer.UpdateMessages;
import tk.rynkbit.can.logic.models.TimedCANMessage;

/**
 * Created by michael on 15.05.17.
 */
public class VisualizerModel {
    private TimedCANMessage CANMessage;
    private Parent parent;
    private UpdateMessages updateRunnable;

    public void setCANMessage(TimedCANMessage CANMessage) {
        this.CANMessage = CANMessage;
    }

    public TimedCANMessage getCANMessage() {
        return CANMessage;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public Parent getParent() {
        return parent;
    }

    public void setUpdateRunnable(UpdateMessages updateRunnable) {
        this.updateRunnable = updateRunnable;
    }

    public UpdateMessages getUpdateRunnable() {
        return updateRunnable;
    }
}
