package tk.rynkbit.can.analyzer.note;

import tk.rynkbit.can.logic.models.TimedCANMessage;

/**
 * Created by michael on 16.05.17.
 */
public class NoteModel {
    private TimedCANMessage timedCANMessage;

    public void setTimedCANMessage(TimedCANMessage timedCANMessage) {
        this.timedCANMessage = timedCANMessage;
    }

    public TimedCANMessage getTimedCANMessage() {
        return timedCANMessage;
    }
}
