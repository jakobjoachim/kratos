package Events;

import Exceptions.EventPayloadIsInvalidException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.naming.directory.InvalidSearchFilterException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    ArrayList<EventPayload> searchEvent(Map<String, String> searchValues) throws InvalidSearchFilterException {

        if (searchValues.size() == 0) {
            throw new InvalidSearchFilterException("Please enter some search values");
        }

        ArrayList<EventPayload> matching = new ArrayList<>();
        eventPayloadList.forEach(eventPayload -> matching.add(eventPayload));

        // Filter down and remove those frickin' ol' bastards...
        for (int i = 0; i < matching.size(); i++) {

            if (searchValues.get("name") != null && !(matching.get(i).getId().equals(searchValues.get("name")))) {
                matching.remove(i);
            }
            if (searchValues.get("player") != null && !(matching.get(i).getId().equals(searchValues.get("player")))) {
                matching.remove(i);
            }
            if (searchValues.get("game") != null && !(matching.get(i).getId().equals(searchValues.get("game")))) {
                matching.remove(i);
            }
            if (searchValues.get("reason") != null && !(matching.get(i).getId().equals(searchValues.get("reason")))) {
                matching.remove(i);
            }
            if (searchValues.get("resource") != null && !(matching.get(i).getId().equals(searchValues.get("resource")))) {
                matching.remove(i);
            }
            if (searchValues.get("type") != null && !(matching.get(i).getId().equals(searchValues.get("type")))) {
                matching.remove(i);
            }

        }

        return matching;
    }

    void deleteEvent(Map<String, String> searchValues) throws InvalidSearchFilterException {

        if (searchValues.size() == 0) {
            throw new InvalidSearchFilterException("Please enter some search values");
        }

        for (int i = 0; i < eventPayloadList.size(); i++) {

            if (searchValues.get("name") != null && !(eventPayloadList.get(i).getId().equals(searchValues.get("name")))) {
                eventPayloadList.remove(i);
            }
            if (searchValues.get("player") != null && !(eventPayloadList.get(i).getId().equals(searchValues.get("player")))) {
                eventPayloadList.remove(i);
            }
            if (searchValues.get("game") != null && !(eventPayloadList.get(i).getId().equals(searchValues.get("game")))) {
                eventPayloadList.remove(i);
            }
            if (searchValues.get("reason") != null && !(eventPayloadList.get(i).getId().equals(searchValues.get("reason")))) {
                eventPayloadList.remove(i);
            }
            if (searchValues.get("resource") != null && !(eventPayloadList.get(i).getId().equals(searchValues.get("resource")))) {
                eventPayloadList.remove(i);
            }
            if (searchValues.get("type") != null && !(eventPayloadList.get(i).getId().equals(searchValues.get("type")))) {
                eventPayloadList.remove(i);
            }

        }

    }

}
