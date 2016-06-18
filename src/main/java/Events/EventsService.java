package Events;
import Exceptions.EventDoesNotExistException;
import Exceptions.EventPayloadIsInvalidException;
import Tools.JsonErrorGenerator;

import javax.naming.directory.InvalidSearchFilterException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class EventsService {

    private static final int HTTP_BAD_REQUEST = 400;

    public static void main(String[] args) {

        EventsManager eventsManager = new EventsManager();

        before(((request, response) -> {
            response.header("Description", "An events manager for RESTopoly");
            response.type("application/json");
        }));

        get("/", (request, response) -> {
            response.status(200);
            response.type("text/html");
            return "OK";
        });

        // get events based on search params
        get("/events", (request, response) -> {

            Map<String, String> queryMap = new HashMap<>();

            queryMap.put("game", request.queryMap().get("game").value());
            queryMap.put("type", request.queryMap().get("type").value());
            queryMap.put("name", request.queryMap().get("name").value());
            queryMap.put("reason", request.queryMap().get("reason").value());
            queryMap.put("resource", request.queryMap().get("resource").value());
            queryMap.put("player", request.queryMap().get("player").value());

            return Tools.Helper.dataToJson(eventsManager.searchEvent(queryMap));

        });

        delete("/events", ((request, response) -> {

            Map<String, String> queryMap = new HashMap<>();

            queryMap.put("game", request.queryMap().get("game").value());
            queryMap.put("type", request.queryMap().get("type").value());
            queryMap.put("name", request.queryMap().get("name").value());
            queryMap.put("reason", request.queryMap().get("reason").value());
            queryMap.put("resource", request.queryMap().get("resource").value());
            queryMap.put("player", request.queryMap().get("player").value());

            eventsManager.deleteEvent(queryMap);

            return "DELETED";

        }));

        post("/events", (request, response) -> eventsManager.createNewEvent(request.body()) );

        get("/events/:eventid", (request, response) -> Tools.Helper.dataToJson(eventsManager.searchID(request.params(":eventid"))) );

        exception(EventDoesNotExistException.class, (exception, request, response) -> {
            response.status(HTTP_BAD_REQUEST);
            response.body(JsonErrorGenerator.getErrorJsonString(HTTP_BAD_REQUEST, "event does not exist"));
        });

        exception(EventPayloadIsInvalidException.class, (exception, request, response) -> {
            response.status(HTTP_BAD_REQUEST);
            response.body(JsonErrorGenerator.getErrorJsonString(HTTP_BAD_REQUEST, "event payload is invalid"));
        });

        exception(InvalidSearchFilterException.class, (exception, request, response) -> {
            response.status(HTTP_BAD_REQUEST);
            response.body(JsonErrorGenerator.getErrorJsonString(HTTP_BAD_REQUEST, "invalid search filter"));
        });
    }

}
