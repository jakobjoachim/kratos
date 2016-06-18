package User;

import Exceptions.UserAlreadyExistsException;
import Exceptions.UserDoesNotExistException;
import Tools.Helper;

import java.util.*;

class UserModel {
    private static Map<String, User> userMap = new HashMap<>();

    String createUser(String name, String uri) throws UserAlreadyExistsException {
        User user = new User();
        user.setName(name);
        user.setUri(uri);
        String url = ("/users/" + name).toLowerCase();
        if (userMap.containsKey(url)) {
            throw new UserAlreadyExistsException();
        }
        userMap.put(url, user);
        String json = ("\""+ url + "\"").toLowerCase();
        return json;
    }

    String getAllUsers(){
        return Helper.dataToJson(userMap.keySet());
    }

    String getUserInfo(String user) throws UserDoesNotExistException {
        String searching = "/users/" + user;
        if (userMap.keySet().contains(searching)) {
            UserInfoPayload result = new UserInfoPayload();
            String id = "/users/" + user;
            result.setId(id);
            result.setName(userMap.get(searching).getName().toLowerCase());
            result.setUri(userMap.get(searching).getUri());
            return Helper.dataToJson(result);
        } else {
            throw new UserDoesNotExistException();
        }
    }

    String editUser(String url, String name, String uri) throws UserDoesNotExistException {
        String searching = "/users/" + url;
        if (userMap.containsKey(searching)){
            userMap.get(searching).setName(name);
            userMap.get(searching).setUri(uri);
        }
        return getUserInfo(url);
    }

    static void deleteUser(String id) throws UserDoesNotExistException {
        String searching = "/users/" + id;
        if (!(userMap.containsKey(searching))) {
            throw new UserDoesNotExistException();
        }
        userMap.remove(searching);
    }
}
