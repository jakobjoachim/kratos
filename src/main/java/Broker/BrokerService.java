package Broker;


import Exceptions.GameAlreadyExistsException;
import Exceptions.GameDoesNotExistException;
import Exceptions.PlaceAlreadyExistException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static spark.Spark.put;

public class BrokerService {

    private static final int HTTP_BAD_REQUEST = 400;
    private static final int UNPROCESSABLE_ENTITY = 422;
    private static final int RESOURCE_NOT_FOUND = 404;
    private static final int OK = 200;
    private static final int CREATED = 201;

    public static void main(String[] args) {
        BrokerModel model = new BrokerModel();

        // Registers a new broker for a game.
        put("/broker/:gameId", (request, response) -> {
            response.status(CREATED);
            response.type("application/json");
            String game = request.params(":gameId");
            try {
                return model.createNewGame(game);
            } catch (GameAlreadyExistsException e) {
                response.status(UNPROCESSABLE_ENTITY);
                return "";
            }
        });

        // Registers the place with the broker, won't change anything if already registered.
        put("/broker/:gameId/places/:placeId", (request, response) -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                PlacePayload creation = mapper.readValue(request.body(), PlacePayload.class);
                if (!(creation.isValid())) {
                    response.status(HTTP_BAD_REQUEST);
                    return "";
                }
                String createdPlace = model.createPlace(request.params(":placeId"), creation.getDescription(), creation.getType(), request.params(":gameId"));
                response.status(CREATED);
                response.type("application/json");
                return createdPlace;
            } catch (Exception e) {
                if (e instanceof GameDoesNotExistException) {
                    response.status(UNPROCESSABLE_ENTITY);
                }
                if (e instanceof JsonParseException) {
                    response.status(HTTP_BAD_REQUEST);
                }
                if (e instanceof PlaceAlreadyExistException) {
                    response.status(OK);
                }
                return "";
            }
        });
    }
}
