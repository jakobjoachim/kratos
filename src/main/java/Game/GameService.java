package Game;

import Exceptions.UserAlreadyExistsException;
import User.UserPayload;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by jakob on 09/05/16.
 */
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
            try{
                ObjectMapper mapper = new ObjectMapper();
                GamePayload creation = mapper.readValue(request.body(), GamePayload.class);
                if (!(creation.isValid())){
                    response.status(HTTP_BAD_REQUEST);
                    return "";
                }
                String createdUrl = model.createUser(creation.getName(), creation.getUri());
                response.status(OK);
                response.type("application/json");
                return createdUrl;
            }catch(Exception e){
                if (e instanceof UserAlreadyExistsException) {
                    response.status(UNPROCESSABLE_ENTITY);
                }
                if (e instanceof JsonParseException) {
                    response.status(HTTP_BAD_REQUEST);
                }
                return "";
            }
        });

    }
}
