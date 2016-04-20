package Events;
import Exceptions.EventDoesNotExistException;
import Exceptions.EventPayloadIsInvalidException;
import Tools.Helper;
import com.google.gson.Gson;

import javax.naming.directory.InvalidSearchFilterException;
import java.beans.Expression;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class EventsService {

    private static final int HTTP_BAD_REQUEST = 400;

    public static void main(String[] args) {

        EventsManager eventsManager = new EventsManager();

        before(((request, response) -> response.type("application/json")));
        before(((request, response) -> {

            if (!request.contentType().equals("application/json")) {
                response.status(HTTP_BAD_REQUEST);
                return;
            }

            response.header("Description", "An events manager for RESTopoly");
            response.type("application/json");
        }
        ));

        // get events based on search params
        get("/events", (request, response) -> {

            try {

                Map<String, String> queryMap = new HashMap<>();

                queryMap.put("game", request.queryMap().get("game").value());
                queryMap.put("type", request.queryMap().get("type").value());
                queryMap.put("name", request.queryMap().get("name").value());
                queryMap.put("reason", request.queryMap().get("reason").value());
                queryMap.put("resource", request.queryMap().get("resource").value());
                queryMap.put("player", request.queryMap().get("player").value());

                return Tools.Helper.dataToJson(eventsManager.searchEvent(queryMap));

            } catch (Exception e) {
                if (e instanceof InvalidSearchFilterException) {
                    response.status(HTTP_BAD_REQUEST);
                }
            }
            return "";

        });

        delete("/events", ((request, response) -> {

            try {

                Map<String, String> queryMap = new HashMap<>();

                queryMap.put("game", request.queryMap().get("game").value());
                queryMap.put("type", request.queryMap().get("type").value());
                queryMap.put("name", request.queryMap().get("name").value());
                queryMap.put("reason", request.queryMap().get("reason").value());
                queryMap.put("resource", request.queryMap().get("resource").value());
                queryMap.put("player", request.queryMap().get("player").value());

                eventsManager.deleteEvent(queryMap);

            } catch (Exception e) {
                if (e instanceof InvalidSearchFilterException) {
                    response.status(HTTP_BAD_REQUEST);
                    return "ERROR";
                }
            }

            return "DELETED";

        }));

        // Create a new eventPayloadList resource
        post("/events", (request, response) -> {
            try {
                return eventsManager.createNewEvent(request.body());
            } catch (Exception e) {
                if (e instanceof EventPayloadIsInvalidException) {
                    response.status(HTTP_BAD_REQUEST);
                }
            }
            return "";
        });

        get("/events/:eventid", (request, response) -> {
            try {
                return new Gson().toJson(eventsManager.searchID(request.params(":eventid")));
            } catch (EventDoesNotExistException e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });


    }

}
