package Broker;


import Exceptions.*;
import Game.PlayerPayload;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static spark.Spark.*;

public class BrokerService {

    private static final int HTTP_BAD_REQUEST = 400;
    private static final int UNPROCESSABLE_ENTITY = 422;
    private static final int RESOURCE_NOT_FOUND = 404;
    private static final int OK = 200;
    private static final int CREATED = 201;
    private static final int CONFLICT = 409;

    public static void main(String[] args) {
        BrokerModel model = new BrokerModel();

        get("/", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            return "";
        });

        // Registers a new broker for a game.
        post("/broker/:gameId", (request, response) -> {
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
        post("/broker/:gameId/places/:placeId", (request, response) -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                PlacePayload creation = mapper.readValue(request.body(), PlacePayload.class);
                if (!(creation.isValid())) {
                    response.status(HTTP_BAD_REQUEST);
                    return "";
                }
                String placeUri = model.createPlace(request.params(":placeId"), creation.getDescription(), creation.getType(), request.params(":gameId"), creation.getBuycost(), creation.getRentMap(), creation.getHypothecarycreditAmount());
                response.status(CREATED);
                response.type("application/json");
                return placeUri;
            } catch (Exception e) {
                if (e instanceof GameDoesNotExistException) {
                    response.status(RESOURCE_NOT_FOUND);
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

        // Buy the estate in question. It will fail if it is not for sale
        post("/broker/:gameId/places/:placeId/owner", (request, response) -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                UserPayload creation = mapper.readValue(request.body(), UserPayload.class);
                if (!(creation.isValid())) {
                    response.status(HTTP_BAD_REQUEST);
                    return "";
                }
                String place = model.buyPlace(request.params(":placeId"), request.params(":gameId"), creation.getUserId());
                response.status(OK);
                response.type("application/json");
                return place;
            } catch (Exception e) {
                if (e instanceof PlaceAlreadySoldException) {
                    response.status(CONFLICT);
                }
                if (e instanceof PlaceDoesNotExistException) {
                    response.status(RESOURCE_NOT_FOUND);
                }
                if (e instanceof GameDoesNotExistException) {
                    response.status(RESOURCE_NOT_FOUND);
                }
                return "";
            }
        });

        // Trade the place - changing the owner
        put("/broker/:gameId/places/:placeId/owner", (request, response) -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                PlacePayload creation = mapper.readValue(request.body(), PlacePayload.class);
                if (!(creation.isValid())) {
                    response.status(HTTP_BAD_REQUEST);
                    return "";
                }
                String place = model.buyPlace(request.params(":placeId"), request.params(":gameId"), creation.getDescription());
                response.status(OK);
                response.type("application/json");
                return place;
            } catch (Exception e) {
                if (e instanceof PlaceNotSoldException) {
                    response.status(CONFLICT);
                }
                if (e instanceof PlaceDoesNotExistException) {
                    response.status(RESOURCE_NOT_FOUND);
                }
                if (e instanceof GameDoesNotExistException) {
                    response.status(RESOURCE_NOT_FOUND);
                }
                return "";
            }
        });

        // takes a hypothecary credit onto the place
        put("/broker/:gameId/places/:placeId/hypothecarycredit", (request, response) -> {
            try {
                String place = model.takeHypothecarycredit(request.params(":placeId"), request.params(":gameId"));
                response.status(OK);
                response.type("application/json");
                return place;
            } catch (Exception e) {
                if (e instanceof AlreadyHasAHypothecarycreditException) {
                    response.status(CONFLICT);
                }
                if (e instanceof PlaceDoesNotExistException) {
                    response.status(RESOURCE_NOT_FOUND);
                }
                if (e instanceof GameDoesNotExistException) {
                    response.status(RESOURCE_NOT_FOUND);
                }
                return "";
            }
        });

        // removes the hypothecary credit from the place
        delete("/broker/:gameId/places/:placeId/hypothecarycredit", (request, response) -> {
            try {
                String place = model.payHypothecarycredit(request.params(":placeId"), request.params(":gameId"));
                response.status(OK);
                response.type("application/json");
                return place;
            } catch (Exception e) {
                if (e instanceof NoHypothecarycreditOnThePlaceException) {
                    response.status(CONFLICT);
                }
                if (e instanceof PlaceDoesNotExistException) {
                    response.status(RESOURCE_NOT_FOUND);
                }
                if (e instanceof GameDoesNotExistException) {
                    response.status(RESOURCE_NOT_FOUND);
                }
                return "";
            }
        });

        // indicates, that the player has visited this place, may be resulting in money transfer
        post("/broker/:gameId/places/:placeId/visit", (request, response) -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                UserPayload creation = mapper.readValue(request.body(), UserPayload.class);
                if (!(creation.isValid())) {
                    response.status(HTTP_BAD_REQUEST);
                    return "";
                }
                String place = model.visitPlace(request.params(":placeId"), request.params(":gameId"), creation.getUserId());
                response.status(OK);
                response.type("application/json");
                return place;
            } catch (Exception e) {
                if (e instanceof NoHypothecarycreditOnThePlaceException) {
                    response.status(CONFLICT);
                }
                if (e instanceof PlaceDoesNotExistException) {
                    response.status(RESOURCE_NOT_FOUND);
                }
                if (e instanceof GameDoesNotExistException) {
                    response.status(RESOURCE_NOT_FOUND);
                }
                return "";
            }
        });
    }
}
