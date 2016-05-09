package Game;

import static spark.Spark.get;

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

        
    }
}
