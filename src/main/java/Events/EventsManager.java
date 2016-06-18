package Events;

import Exceptions.EventDoesNotExistException;
import Exceptions.EventPayloadIsInvalidException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.naming.directory.InvalidSearchFilterException;
import java.io.IOException;
import java.util.*;

class EventsManager {

    private List<EventPayload> eventPayloadList;

    protected EventsManager() {
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
        event.setId("/events/" + Tools.Helper.nextId());

        this.eventPayloadList.add(event);

        if (!(event.isValid())) {
            throw new EventPayloadIsInvalidException();
        }

        return Tools.Helper.dataToJson(event);
    }

    ArrayList<EventPayload> searchEvent(Map<String, String> searchValues) throws InvalidSearchFilterException {

        if (searchValues.size() == 0) {
            throw new InvalidSearchFilterException("Please enter some search values");
        }

        ArrayList<EventPayload> matching = new ArrayList<>();
        eventPayloadList.forEach(eventPayload -> matching.add(eventPayload));

        // Filter down and remove those frickin' ol' bastards...
        for (int i = 0; i < matching.size(); i++) {

            if (searchValues.get("name") != null && !(matching.get(i).getName().equals(searchValues.get("name")))) {
                matching.remove(i);
            }
            if (searchValues.get("player") != null && !(matching.get(i).getPlayer().equals(searchValues.get("player")))) {
                matching.remove(i);
            }
            if (searchValues.get("game") != null && !(matching.get(i).getGame().equals(searchValues.get("game")))) {
                matching.remove(i);
            }
            if (searchValues.get("reason") != null && !(matching.get(i).getReason().equals(searchValues.get("reason")))) {
                matching.remove(i);
            }
            if (searchValues.get("resource") != null && !(matching.get(i).getResource().equals(searchValues.get("resource")))) {
                matching.remove(i);
            }
            if (searchValues.get("type") != null && !(matching.get(i).getType().equals(searchValues.get("type")))) {
                matching.remove(i);
            }

            // TODO add all matching instead of removing

        }

        return matching;
    }

    void deleteEvent(Map<String, String> searchValues) throws InvalidSearchFilterException {
        if (searchValues.size() == 0) {
            throw new InvalidSearchFilterException("Please enter some search values");
        }

        for (int i = 0; i < eventPayloadList.size(); i++) {

            if (searchValues.get("name") != null && !(eventPayloadList.get(i).getName().equals(searchValues.get("name")))) {
                eventPayloadList.remove(i);
            }
            if (searchValues.get("player") != null && !(eventPayloadList.get(i).getPlayer().equals(searchValues.get("player")))) {
                eventPayloadList.remove(i);
            }
            if (searchValues.get("game") != null && !(eventPayloadList.get(i).getGame().equals(searchValues.get("game")))) {
                eventPayloadList.remove(i);
            }
            if (searchValues.get("reason") != null && !(eventPayloadList.get(i).getReason().equals(searchValues.get("reason")))) {
                eventPayloadList.remove(i);
            }
            if (searchValues.get("resource") != null && !(eventPayloadList.get(i).getResource().equals(searchValues.get("resource")))) {
                eventPayloadList.remove(i);
            }
            if (searchValues.get("type") != null && !(eventPayloadList.get(i).getType().equals(searchValues.get("type")))) {
                eventPayloadList.remove(i);
            }

        }
    }


    EventPayload searchID(String searchedID) throws EventDoesNotExistException {
        EventPayload searchedEvent = null;

        if (searchedID.equals("")) {
        }
        for (EventPayload event : eventPayloadList) {

            if (event.getId().equals(searchedID)) {
                searchedEvent = event;
            }
        }
        if (searchedEvent != null) {
            return searchedEvent;
        }
        else {
            throw new EventDoesNotExistException();
        }
    }
}
