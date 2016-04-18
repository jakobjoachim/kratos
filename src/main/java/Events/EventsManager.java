package Events;

import Exceptions.EventPayloadIsInvalidException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class EventsManager {

    List<EventPayload> eventPayloadList;

    EventsManager() {
        this.eventPayloadList = new ArrayList<>();
    }

    /**
     * Create a new event from payload.
     *
     * @param payload JSON String payload
     * @return The created payload
     */
    String createNewEvent(String payload) throws IOException, EventPayloadIsInvalidException {

        ObjectMapper objectMapper = new ObjectMapper();
        EventPayload event = objectMapper.readValue(payload, EventPayload.class);

        this.eventPayloadList.add(event);

        if (!(event.isValid())) {
            throw new EventPayloadIsInvalidException();
        }

        return Tools.Helper.dataToJson(event); //TODO
    }

}
