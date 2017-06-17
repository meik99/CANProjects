package rk.rynkbit.can.presenter.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import jdk.nashorn.internal.runtime.JSONFunctions;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by michael on 17.06.17.
 */
public class Usage {
    private static final String FILENAME = "data.json";

    private static Usage instance;
    public static Usage getInstance(){
        if(instance == null){
            instance = new Usage();
        }
        return instance;
    }

    double sum = 0;
    int count = 0;

    private Usage(){
        File file = new File(FILENAME);
        if(file.exists() == false){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            JsonParser parser = new JsonParser();
            try {
                FileReader reader = new FileReader(file);
                JsonElement element = parser.parse(reader);

                if(element != null && element.isJsonObject()) {
                    JsonObject object = element.getAsJsonObject();

                    sum = object.get("sum").getAsDouble();
                    count = object.get("count").getAsInt();
                }
                reader.close();
                reader = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addUsage(double usage){
        sum += usage;
        count++;

        JsonObject object = new JsonObject();
        object.addProperty("sum", sum);
        object.addProperty("count", count);

        try {
            FileWriter writer = new FileWriter(FILENAME);
            writer.write(object.toString());
            writer.flush();
            writer.close();
            writer = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getAverageUsage(){
        return sum / count;
    }
}
