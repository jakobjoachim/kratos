package Game;

import Exceptions.GameAlreadyExistsException;
import Exceptions.GameDoesNotExistException;
import Exceptions.WrongDataTypeException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

public class GameService {

    private static final int HTTP_BAD_REQUEST = 400;
    private static final int UNPROCESSABLE_ENTITY = 422;
    private static final int RESOURCE_NOT_FOUND = 404;
    private static final int OK = 200;

    public static void main(String[] args) {
        GameModel model = new GameModel();

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
            String status = request.queryMap().get("status").value();
            try {
                return model.setStatus(game, status);
            } catch (Exception e) {
                if (e instanceof WrongDataTypeException) {
                    response.status(UNPROCESSABLE_ENTITY);
                }
                if (e instanceof GameDoesNotExistException) {
                    response.status(RESOURCE_NOT_FOUND);
                }
                return "";
            }
        });

        get("/games/:gameId/player", (request, response) -> {
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
            String result = model.addUser(creation.getName(), creation.isReady(), request.params(":gameId"));
            response.status(OK);
            response.type("application/json");
            return ""; //TODO
        });
    }
}
