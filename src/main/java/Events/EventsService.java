package Events;
import java.util.ArrayList;
import java.util.List;

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

                response.header("Description", "An eventPayloadList manager for RESTopoly");
                response.type("application/json");
            }
        ));

//        get("/eventPayloadList", (request, response) -> {
//
//            String game = request.queryMap().get("game").value();
//            String type = request.queryMap().get("type").value();
//            String name = request.queryMap().get("name").value();
//            String reason = request.queryMap().get("reason").value();
//            String resource = request.queryMap().get("resource").value();
//            String player = request.queryMap().get("player").value();
//
//        });

        // Create a new eventPayloadList resource
        post("/eventPayloadList", (request, response) -> {
            try {
                return eventsManager.createNewEvent(request.body());
            }
            catch (Exception e) {
            }
            return "LMAO";
        });

    }

}
