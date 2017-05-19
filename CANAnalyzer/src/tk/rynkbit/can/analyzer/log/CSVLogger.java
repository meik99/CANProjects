package tk.rynkbit.can.analyzer.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mrynkiewicz on 19.05.17.
 */
public class CSVLogger {
    private static CSVLogger instance;

    private File logFile;
    private FileWriter fileWriter;
    private StringBuilder line;

    public static CSVLogger getInstance(){
        if(instance == null){
            instance = new CSVLogger();
        }
        return instance;
    }

    private CSVLogger(){
        logFile = new File("log.csv");
        if(logFile.exists() == false){
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        line = new StringBuilder();
    }

    public void log(int... data){
        try {
            if(fileWriter == null){
                fileWriter = new FileWriter(logFile);
            }
            Date timestamp = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

            for (int i = 0; i < data.length; i++){
                line.append(data[i]);
                line.append(";");
            }
            line.append(sdf.format(timestamp));
            line.append("\n");

            fileWriter.append(line);
            fileWriter.flush();

            line = new StringBuilder();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
