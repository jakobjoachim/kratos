package Events;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class EventsManager {

    EventsManager() {
        
    }

    /**
     * Create a new event from payload.
     *
     * @param payload JSON String payload
     * @return The created payload
     */
    String createNewEvent(String payload) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        EventPayload event = objectMapper.readValue(payload, EventPayload.class);

        return "";
    }

}
