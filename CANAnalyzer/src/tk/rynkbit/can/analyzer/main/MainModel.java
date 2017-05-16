package tk.rynkbit.can.analyzer.main;

import de.fischl.usbtin.USBtin;
import tk.rynkbit.can.analyzer.UpdateMessages;
import tk.rynkbit.can.analyzer.visualizer.VisualizerController;
import tk.rynkbit.can.logic.models.TimedCANMessage;
import tk.rynkbit.can.logic.test.MessageSimulator;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by mrynkiewicz on 14.05.17.
 */
public class MainModel {
    private de.fischl.usbtin.USBtin USBtin;
    private UpdateMessages updateRunnable;
    private TimedCANMessage fitlerMessage;
    private ScheduledThreadPoolExecutor executor;
    private MessageSimulator messageSimulator;
    private VisualizerController virtualizationController;

    public void setUSBtin(USBtin USBtin) {
        this.USBtin = USBtin;
    }

    public USBtin getUSBtin() {
        return USBtin;
    }

    public void setUpdateRunnable(UpdateMessages updateRunnable) {
        this.updateRunnable = updateRunnable;
    }

    public UpdateMessages getUpdateRunnable() {
        return updateRunnable;
    }

    public void setFitlerMessage(TimedCANMessage fitlerMessage) {
        this.fitlerMessage = fitlerMessage;
    }

    public TimedCANMessage getFitlerMessage() {
        return fitlerMessage;
    }

    public void setExecutor(ScheduledThreadPoolExecutor executor) {
        this.executor = executor;
    }

    public ScheduledThreadPoolExecutor getExecutor() {
        return executor;
    }

    public void setMessageSimulator(MessageSimulator messageSimulator) {
        this.messageSimulator = messageSimulator;
    }

    public MessageSimulator getMessageSimulator() {
        return messageSimulator;
    }

    public void setVirtualizationController(VisualizerController virtualizationController) {
        this.virtualizationController = virtualizationController;
    }

    public VisualizerController getVirtualizationController() {
        return virtualizationController;
    }
}
