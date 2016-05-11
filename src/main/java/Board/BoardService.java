package Board;

import Exceptions.BoardAlreadyExistsException;
import Exceptions.BoardDoesNotExistException;
import Exceptions.PawnDoesNotExistException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static spark.Spark.*;

public class BoardService {
    private static final int OK = 200;
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int UNPROCESSABLE_ENTITY = 422;
    private static final int RESOURCE_NOT_FOUND = 404;

    public static void main(String[] args) {
        BoardModel boardModel = new BoardModel();
        PawnModel pawnModel = new PawnModel();


        get("/games", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            return boardModel.getAllActiveGames();
        });

        post("/board", (request, response) -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                BoardPayload creation = mapper.readValue(request.body(), BoardPayload.class);
                if (!(creation.isValid())) {
                    response.status(HTTP_BAD_REQUEST);
                    return "";
                }
                String createdUrl = boardModel.createBoard(creation.getPlayer());
                response.status(OK);
                response.type("application/json");
                return createdUrl;

            } catch (Exception e) {
                if (e instanceof BoardAlreadyExistsException) {
                    response.status(UNPROCESSABLE_ENTITY);
                }
                if (e instanceof JsonParseException) {
                    response.status(HTTP_BAD_REQUEST);
                }
                return "";
            }
        });

        get("/board/:gameId", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            try {
                return boardModel.getBoardInfo(request.params(":gameId"));
            } catch (BoardDoesNotExistException e) {
                response.status(RESOURCE_NOT_FOUND);
                return "";
            }
        });

        delete("/board/:gameId", (request, response) -> {
            response.status(OK);
            response.type("application/json");

            String id = request.params(":gameId");
            try {
                BoardModel.deleteBoard(id);
            } catch (BoardDoesNotExistException e) {
                response.status(RESOURCE_NOT_FOUND);
                return "";
            }
            return "{ \"status\": \"success\" }";
        });

        get("/board/:gameId/pawns/:userId", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            try {
                return boardModel.getPlayerPositions(request.params(":userId"));
            } catch (BoardDoesNotExistException e) {
                response.status(RESOURCE_NOT_FOUND);
                return "";
            }
        });

        put("/boards/:gameId/places/:position", (request, response) -> {
            response.status(OK);
            response.type("application/json");

            String gameId = request.params(":gameId");
            String position = request.params(":position");
            if(!boardModel.pawnAlreadyPlaced){
                boardModel.placePawn(gameId,position);
            }
            return "";
        });

        delete("/board/:gameId/places/position", (request, response) -> {
            response.status(OK);
            response.type("application/json");

            String position = request.params(":position");
            try {
                boardModel.deletePawn(position);
            } catch (PawnDoesNotExistException e) {
                response.status(RESOURCE_NOT_FOUND);
                return "";
            }
            return "{ \"status\": \"success\" }";
        });

        post("/board/:gameId/places/position", (request, response) -> {
                ObjectMapper mapper = new ObjectMapper();
                BoardPayload creation = mapper.readValue(request.body(), BoardPayload.class);
                if (!(creation.isValid())) {
                    response.status(HTTP_BAD_REQUEST);
                    return "";
                }
                return "";
        });

    }
}
