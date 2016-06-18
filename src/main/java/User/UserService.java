package User;

import Exceptions.UserAlreadyExistsException;
import Exceptions.UserDoesNotExistException;
import Exceptions.UserPayloadIsInvalidException;
import Tools.Helper;
import Tools.JsonErrorGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static spark.Spark.*;

public class UserService {

    private static final int HTTP_BAD_REQUEST = 400;
    private static final int UNPROCESSABLE_ENTITY = 422;
    private static final int RESOURCE_NOT_FOUND = 404;
    private static final int OK = 200;

    public static void main(String[] args){
        UserModel model = new UserModel();

        get("/", (request, response) -> {
            response.status(200);
            return "OK";
        });

        post("/users", (request, response) -> {
            ObjectMapper mapper = new ObjectMapper();
            UserPayload creation = mapper.readValue(request.body(), UserPayload.class);

            if (!(creation.isValid())){
                throw new UserPayloadIsInvalidException();
            }

            String createdUrl = model.createUser(creation.getName(), creation.getUri());
            response.status(OK);
            response.type("application/json");

            return String.format("{ \"id\" : \"%s\", \"success\" : true }", createdUrl);
        });

        get("/users", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            return model.getAllUsers();
        });

        get("/users/:userId", (request, response) -> {
            response.status(OK);
            response.type("application/json");

            return model.getUserInfo(request.params(":userId"));
        });

        put("/users/:userId", (request, response) -> {
            response.status(OK);
            response.type("application/json");

            String url = request.params(":userId");
            String name = request.queryMap().get("name").value();
            String uri = request.queryMap().get("uri").value();
            
            try {
                return model.createUser(name, uri);
            }
            catch (UserAlreadyExistsException e) {
                return model.editUser(url, name, uri);
            }
        });

        delete("/users/:userId", (request, response) -> {
            response.status(OK);
            response.type("application/json");

            String id = request.params(":userId");
            UserModel.deleteUser(id);

            return "{ \"success\": true }";
        });

        exception(UserAlreadyExistsException.class, (e, request, response) -> {
            response.status(UNPROCESSABLE_ENTITY);
            response.type("application/json");

            response.body(
                    JsonErrorGenerator.getErrorJsonString(UNPROCESSABLE_ENTITY, "user already exists")
            );
        });

        exception(UserDoesNotExistException.class, (e, request, response) -> {
            response.status(RESOURCE_NOT_FOUND);
            response.type("application/json");

            response.body(
                    JsonErrorGenerator.getErrorJsonString(RESOURCE_NOT_FOUND, "user does not exist")
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
