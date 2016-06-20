package Board;


import Exceptions.*;
import Tools.JsonErrorGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static spark.Spark.*;
import static spark.Spark.exception;

public class BoardService {
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int UNPROCESSABLE_ENTITY = 422;
    private static final int RESOURCE_NOT_FOUND = 404;
    private static final int OK = 200;

    public static void main(String[] args) {
        BoardModel model = new BoardModel();

        //Getestet und funktioniert
        get("/boards", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            return model.getAllGames();
        });

        //Getestet und funktioniert
        post("/boards", (request, response) -> {
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
        });

        //Getestet und funktioniert
        get("/boards/:gameId", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            return model.getBoard(request.params(":gameId"));
        });

        //Getestet und funktioniert
        delete("/boards/:gameId", (request, response) -> {
            response.status(OK);
            return model.removeBoard(request.params(":gameId"));
        });

        //Getestet und funktioniert
        post("/boards/:gameId/pawns", (request, response) -> {
            ObjectMapper mapper = new ObjectMapper();
            PawnPayload creation = mapper.readValue(request.body(), PawnPayload.class);
            if (!(creation.isValid())) {
                response.status(HTTP_BAD_REQUEST);
                return "Pawn not valid";
            }
            String createdUrl = model.placePawn(creation.getPlayer(), creation.getPlace(), creation.getPosition(), request.params(":gameId"));
            response.status(OK);
            response.type("application/json");
            return createdUrl;
        });

        //Getestet und funktioniert
        get("/boards/:gameId/pawns", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            return model.getAllPlayerPositions(request.params(":gameId"));
        });

        //Getestet und funktioniert
        get("/boards/:gameId/pawns/:pawnId", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            return model.getPawn(request.params(":gameId"), request.params(":pawnId"));
        });

        //Getestet und funktioniert
        delete("/boards/:gameId/pawns/:pawnId", (request, response) -> {
            response.status(OK);
            return model.removePawn(request.params(":gameId"), request.params(":pawnId"));
        });

        post("/boards/:gameId/pawns/:pawnId/move", (request, response) -> {
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

        exception(BoardAlreadyExistException.class, (e, request, response) -> {
            response.status(UNPROCESSABLE_ENTITY);
            response.type("application/json");

            response.body(
                    JsonErrorGenerator.getErrorJsonString(UNPROCESSABLE_ENTITY, "Board already exists")
            );
        });

        exception(BoardDoesNotExistException.class, (e, request, response) -> {
            response.status(RESOURCE_NOT_FOUND);
            response.type("application/json");

            response.body(
                    JsonErrorGenerator.getErrorJsonString(RESOURCE_NOT_FOUND, "Board does not exist")
            );
        });

        exception(PawnAlreadyExistsException.class, (e, request, response) -> {
            response.status(UNPROCESSABLE_ENTITY);
            response.type("application/json");

            response.body(
                    JsonErrorGenerator.getErrorJsonString(UNPROCESSABLE_ENTITY, "Pawn already exists")
            );
        });

        exception(PlaceAlreadyExistException.class, (e, request, response) -> {
            response.status(UNPROCESSABLE_ENTITY);
            response.type("application/json");

            response.body(
                    JsonErrorGenerator.getErrorJsonString(UNPROCESSABLE_ENTITY, "Place already exists")
            );
        });

        exception(PawnDoesNotExistException.class, (e, request, response) -> {
            response.status(RESOURCE_NOT_FOUND);
            response.type("application/json");

            response.body(
                    JsonErrorGenerator.getErrorJsonString(RESOURCE_NOT_FOUND, "Pawn does not exist")
            );
        });

        exception(MissingBodyException.class, (e, request, response) -> {
            response.status(UNPROCESSABLE_ENTITY);
            response.type("application/json");

            response.body(
                    JsonErrorGenerator.getErrorJsonString(UNPROCESSABLE_ENTITY, "No body was found, please provide one")
            );
        });

        exception(JsonParseException.class, (e, request, response) -> {
            response.status(HTTP_BAD_REQUEST);
            response.type("application/json");

            response.body(
                    JsonErrorGenerator.getErrorJsonString(HTTP_BAD_REQUEST, "json could not be parsed")
            );
        });
    }
}
