package User;

import Exceptions.UserAlreadyExistsException;
import Exceptions.UserDoesNotExistException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.StringWriter;

import static spark.Spark.*;

public class UserService {

    private static final int HTTP_BAD_REQUEST = 400;
    private static final int UNPROCESSABLE_ENTITY = 422;
    private static final int RESOURCE_NOT_FOUND = 404;
    private static final int OK = 200;

    public static void main(String[] args){
        UserModel model = new UserModel();

        post("/users", (request, response) -> {
            try{
                ObjectMapper mapper = new ObjectMapper();
                UserPayload creation = mapper.readValue(request.body(), UserPayload.class);
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

        get("/users", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            return model.getAllUsers();
        });

        get("/users/:userId", (request, response) -> {
            response.status(OK);
            response.type("application/json");
            try {
                return model.getUserInfo(request.params(":userId"));
            }
            catch (UserDoesNotExistException e) {
                response.status(RESOURCE_NOT_FOUND);
                return "";
            }
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
            try {
                UserModel.deleteUser(id);
            } catch (UserDoesNotExistException e) {
                response.status(RESOURCE_NOT_FOUND);
                return "";
            }
            return "{ \"status\": \"success\" }";
        });
    }
}
