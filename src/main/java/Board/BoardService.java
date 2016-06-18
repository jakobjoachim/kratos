package Board;


import Exceptions.PlayerDoesNotExistException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

public class BoardService {
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int UNPROCESSABLE_ENTITY = 422;
    private static final int RESOURCE_NOT_FOUND = 404;
    private static final int OK = 200;

    public static void main(String[] args) {
        BoardModel model = new BoardModel();

        get("/boards", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            return model.getAllGames();
        });

        post("/games", (request, response) -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                BoardPayload creation = mapper.readValue(request.body(), BoardPayload.class);
                if (!(creation.isValid())) {
                    response.status(HTTP_BAD_REQUEST);
                    return "";
                }
                String createdUrl = model.createBoard(creation.gameUri);
                response.status(OK);
                response.type("application/json");
                return createdUrl;
            } catch (Exception e) {
                if (e instanceof JsonParseException) {
                    response.status(HTTP_BAD_REQUEST);
                }
                return "";
            }
        });

        get("/boards/:gameId", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            return model.getBoard(request.params(":gameId"));
        });

        delete("/boards/:gameId", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            try {
                return model.removeBoard(request.params(":gameId"));
            } catch (Exception e) {
                response.status(RESOURCE_NOT_FOUND);
                return "";
            }
        });

        post("/boards/:gameId/pawns", (request, response) -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                PawnPayload creation = mapper.readValue(request.body(), PawnPayload.class);
                if (!(creation.isValid())) {
                    response.status(HTTP_BAD_REQUEST);
                    return "";
                }
                String createdUrl = model.placePawn(creation.getPlayer(), creation.getPlace(), creation.getPosition(), request.params("gameId"));
                response.status(OK);
                response.type("application/json");
                return createdUrl;
            } catch (Exception e) {
                if (e instanceof JsonParseException) {
                    response.status(HTTP_BAD_REQUEST);
                }
                return "";
            }
        });

        get("/boards/:gameId/pawns", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            return model.getAllPlayerPositions(request.params(":gameId"));
        });

        get("/boards/:gameId/pawns/:pawnId", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            return model.getPawns(request.params(":gameId"), request.params(":pawnId"));
        });

        delete("/boards/:gameId/pawns/:pawnId", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            try {
                return model.removePawn(request.params(":gameId"), request.params(":pawnId"));
            } catch (Exception e) {
                if (e instanceof PlayerDoesNotExistException) {
                    response.status(RESOURCE_NOT_FOUND);
                }
                return "";
            }
        });

        post("/boards/:gameId/pawns/:pawnId/move", (request, response) -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                PawnPayload creation = mapper.readValue(request.body(), PawnPayload.class);
                if (!(creation.isValid())) {
                    response.status(HTTP_BAD_REQUEST);
                    return "";
                }
                String createdUrl = model.movePawn(creation.getMove(), request.params("gameId"), request.params("pawnId"));
                response.status(OK);
                response.type("application/json");
                return createdUrl;
            } catch (Exception e) {
                if (e instanceof JsonParseException) {
                    response.status(HTTP_BAD_REQUEST);
                }
                return "";
            }
        });

        get("/boards/:gameId/pawns/:pawnId/roll", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            return model.getRolls(request.params(":gameId"), request.params(":pawnId"));
        });

        post("/boards/:gameId/pawns/:pawnId/roll", (request, response) -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                PawnPayload creation = mapper.readValue(request.body(), PawnPayload.class);
                if (!(creation.isValid())) {
                    response.status(HTTP_BAD_REQUEST);
                    return "";
                }
                String createdUrl = model.rollDice(request.params("gameId"), request.params("pawnId"));
                response.status(OK);
                response.type("application/json");
                return createdUrl;
            } catch (Exception e) {
                if (e instanceof JsonParseException) {
                    response.status(HTTP_BAD_REQUEST);
                }
                return "";
            }
        });


    }
}
