package Events;

import Enums.ServiceType;
import Exceptions.EventDoesNotExistException;
import Exceptions.EventPayloadIsEmpty;
import Exceptions.EventPayloadIsInvalidException;
import Tools.Helper;
import Tools.YellowService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.naming.directory.InvalidSearchFilterException;
import java.io.IOException;
import java.util.*;

class EventsManager {

    private List<Event> eventList;

    protected EventsManager() {
        this.eventList = new ArrayList<>();
    }

    /**
     * Create a new event from payload.
     *
     * @param payload JSON String payload
     * @return The created payload
     */
    String createNewEvent(String payload) throws IOException, EventPayloadIsInvalidException, EventPayloadIsEmpty {

        if (payload.isEmpty()) {
            throw new EventPayloadIsEmpty();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Event event = objectMapper.readValue(payload, Event.class);
        event.setId("/events/" + Tools.Helper.nextId());

        if (!(event.isValid())) {
            throw new EventPayloadIsInvalidException();
        }

        event.setSubmitted(this.submitToClient(event));
        this.eventList.add(event);

        return Tools.Helper.dataToJson(event);
    }

    ArrayList<Event> searchEvent(Map<String, String> searchValues) throws InvalidSearchFilterException {

        if (searchValues.size() == 0) {
            throw new InvalidSearchFilterException("Please enter some search values");
        }

        ArrayList<Event> matching = new ArrayList<>();
        eventList.forEach(eventPayload -> matching.add(eventPayload));

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

        for (int i = 0; i < eventList.size(); i++) {

            if (searchValues.get("name") != null && !(eventList.get(i).getName().equals(searchValues.get("name")))) {
                eventList.remove(i);
            }
            if (searchValues.get("player") != null && !(eventList.get(i).getPlayer().equals(searchValues.get("player")))) {
                eventList.remove(i);
            }
            if (searchValues.get("game") != null && !(eventList.get(i).getGame().equals(searchValues.get("game")))) {
                eventList.remove(i);
            }
            if (searchValues.get("reason") != null && !(eventList.get(i).getReason().equals(searchValues.get("reason")))) {
                eventList.remove(i);
            }
            if (searchValues.get("resource") != null && !(eventList.get(i).getResource().equals(searchValues.get("resource")))) {
                eventList.remove(i);
            }
            if (searchValues.get("type") != null && !(eventList.get(i).getType().equals(searchValues.get("type")))) {
                eventList.remove(i);
            }

        }
    }

    Event searchID(String searchedID) throws EventDoesNotExistException {
        Event searchedEvent = null;

        if (searchedID.equals("")) {
        }
        for (Event event : eventList) {

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

    private boolean submitToClient(Event event) {
        String clientServiceURL = YellowService.getServiceUrlForType(ServiceType.CLIENT);
        clientServiceURL = clientServiceURL + "/event";

        try {
            HttpResponse<JsonNode> response = Unirest.post(clientServiceURL)
                    .header("Content-Type", "application/json")
                    .body(Helper.dataToJson(event))
                    .asJson();

            if (response.getStatus() == 200) {
                return true;
            }

        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return false;
    }
}
