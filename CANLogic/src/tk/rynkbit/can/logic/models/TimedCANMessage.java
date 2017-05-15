package tk.rynkbit.can.logic.models;

import de.fischl.usbtin.CANMessage;

import java.util.Date;

/**
 * Created by mrynkiewicz on 14.05.17.
 */
public class TimedCANMessage extends CANMessage {
    private Date timestamp = new Date();
    private Date lastUpdate = new Date();
    private long count = 1;
    private long period = 0;

    public TimedCANMessage(int i, byte[] bytes) {
        super(i, bytes);
    }

    public TimedCANMessage(int i, byte[] bytes, boolean b, boolean b1) {
        super(i, bytes, b, b1);
    }

    public TimedCANMessage(String s) {
        super(s);
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void updateTimestamp(){
        timestamp = new Date();
        lastUpdate = new Date();
    }

    public void increaseCount() {
        period = new Date().getTime() - lastUpdate.getTime();
        count++;
        lastUpdate = new Date();
    }

    public long getCount() {
        return count;
    }

    public long getPeriod() {
        return period;
    }
}
