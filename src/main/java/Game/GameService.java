package Game;

import Enums.GameStatus;
import Exceptions.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static spark.Spark.*;

public class GameService {

    private static final int HTTP_BAD_REQUEST = 400;
    private static final int UNPROCESSABLE_ENTITY = 422;
    private static final int RESOURCE_NOT_FOUND = 404;
    private static final int OK = 200;
    private static final int TEAPOT = 418;

    public static void main(String[] args) {
        GameModel model = new GameModel();

        get("/", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            return "";
        });

        get("/games", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            return model.getAllGames();
        });

        post("/games", (request, response) -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                GamePayload creation = mapper.readValue(request.body(), GamePayload.class);
                if (!(creation.isValid())) {
                    response.status(HTTP_BAD_REQUEST);
                    return "";
                }
                String createdUrl = model.createGame(creation.getName(), creation.getServices());
                response.status(OK);
                response.type("application/json");
                return createdUrl;
            } catch (Exception e) {
                if (e instanceof GameAlreadyExistsException) {
                    response.status(UNPROCESSABLE_ENTITY);
                }
                if (e instanceof JsonParseException) {
                    response.status(HTTP_BAD_REQUEST);
                }
                return "";
            }
        });

        get("/games/:gameId", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            try {
                return model.getGameInfo(request.params(":gameId"));
            } catch (GameDoesNotExistException e) {
                response.status(RESOURCE_NOT_FOUND);
                return "";
            }
        });

        get("/games/:gameId/status", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            try {
                return model.getGameStatus(request.params(":gameId"));
            } catch (GameDoesNotExistException e) {
                response.status(RESOURCE_NOT_FOUND);
                return "";
            }
        });

        put("/games/:gameId/status", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            String game = request.params(":gameId");
            try {
                GameStatus status = GameStatus.valueOf(request.queryMap().get("status").value());

                return model.setStatus(game, status);
            } catch (Exception e) {
                if (e instanceof GameDoesNotExistException) {
                    response.status(RESOURCE_NOT_FOUND);
                } else if (e instanceof WrongDataTypeException) {
                    response.status(TEAPOT);
                } else if (e instanceof IllegalArgumentException) {
                    response.status(UNPROCESSABLE_ENTITY);
                } else {
                    response.status(HTTP_BAD_REQUEST);
                }
                return "";
            }
        });

        get("/games/:gameId/players", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            try {
                return model.getPlayers(request.params(":gameId"));
            } catch (GameDoesNotExistException e) {
                response.status(RESOURCE_NOT_FOUND);
                return "";
            }
        });

        post("/games/:gameId/players", (request, response) -> {
            ObjectMapper mapper = new ObjectMapper();
            PlayerPayload creation = mapper.readValue(request.body(), PlayerPayload.class);
            if (!(creation.isValid())) {
                response.status(HTTP_BAD_REQUEST);
                return "";
            }
            try {
                String result = model.addUser(creation.getName(), creation.isReady(), request.params(":gameId"));
                response.status(OK);
                response.type("application/json");
                return result;
            } catch (GameDoesNotExistException e) {
                response.status(RESOURCE_NOT_FOUND);
                return "";
            }
        });

        get("/games/:gameId/services", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            try {
                return model.getServices(request.params(":gameId"));
            } catch (GameDoesNotExistException e) {
                response.status(RESOURCE_NOT_FOUND);
                return "";
            }
        });

        get("/games/:gameId/components", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            try {
                return model.getComponents(request.params(":gameId"));
            } catch (GameDoesNotExistException e) {
                response.status(RESOURCE_NOT_FOUND);
                return "";
            }
        });

        delete("/games/:gameId/players/turn", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            try {
                return model.endTurn(request.params(":gameId"));
            } catch (Exception e) {
                if (e instanceof NoTurnActiveException) {
                    response.status(OK);
                    return "Turn Already free";
                }
                response.status(RESOURCE_NOT_FOUND);
                return "";
            }
        });

        delete("/games/:gameId/players/:playerId", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            try {
                return model.removePlayer(request.params(":gameId"), request.params(":playerId"));
            } catch (Exception e) {
                response.status(RESOURCE_NOT_FOUND);
                return "";
            }
        });

        get("/games/:gameId/players/:playerId/ready", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            try {
                return model.getPlayerReady(request.params(":gameId"), request.params(":playerId"));
            } catch (Exception e) {
                response.status(RESOURCE_NOT_FOUND);
                return "";
            }
        });

        put("/games/:gameId/players/:playerId/ready", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            try {
                return model.putPlayerReady(request.params(":gameId"), request.params(":playerId"));
            } catch (Exception e) {
                response.status(RESOURCE_NOT_FOUND);
                return "";
            }
        });

        get("/games/:gameId/players/current", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            try {
                return model.getCurrentPlayer(request.params(":gameId"));
            } catch (Exception e) {
                response.status(RESOURCE_NOT_FOUND);
                return "";
            }
        });

        get("/games/:gameId/players/turn", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            try {
                return model.getTurnHolder(request.params(":gameId"));
            } catch (Exception e) {
                response.status(RESOURCE_NOT_FOUND);
                return "";
            }
        });

        put("/games/:gameId/players/turn", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            try {
                String player = request.queryMap().get("player").value();
                int status = model.putTurn(request.params(":gameId"), player);
                response.status(status);
                if (status == 200) return "already holding the mutex";
                if (status == 201) return "aquired the mutex";
                if (status == 409) return "already aquired by an other player";
                return "";
            } catch (Exception e) {
                response.status(RESOURCE_NOT_FOUND);
                return "";
            }
        });
    }
}
