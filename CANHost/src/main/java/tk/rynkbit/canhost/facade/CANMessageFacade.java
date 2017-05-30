package tk.rynkbit.canhost.facade;

import tk.rynkbit.can.logic.CANRepository;
import tk.rynkbit.can.logic.models.TimedCANMessage;

import javax.json.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by michael on 30.05.17.
 */
@Path("/messages")
public class CANMessageFacade {
    @GET
    @Produces({
            MediaType.APPLICATION_JSON
    })
    public JsonArray getMessages(){
        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<TimedCANMessage> messages = new ArrayList<TimedCANMessage>();
        Iterator<TimedCANMessage> repo = CANRepository.getInstance().getMessageMap().values().iterator();

        while(repo.hasNext()){
            messages.add(repo.next());
        }

        for (TimedCANMessage message :
                messages) {
            JsonObjectBuilder messageBuilder = Json.createObjectBuilder();
            JsonArrayBuilder dataBuilder = Json.createArrayBuilder();

            for (Byte dataByte :
                    message.getData()) {
                dataBuilder.add(Byte.toUnsignedInt(dataByte));
            }

            messageBuilder.add("id", message.getId());
            messageBuilder.add("data", dataBuilder);
            messageBuilder.add("timestamp", message.getTimestamp().getTime());
            messageBuilder.add("rtr", message.isRtr());
            messageBuilder.add("extended", message.isExtended());

            builder.add(messageBuilder);
        }

        return builder.build();
    }
}
