package User;

import Exceptions.UserAlreadyExistsException;
import Exceptions.UserDoesNotExistException;

import java.util.*;

public class UserModel {
    private static Map<String, User> userMap = new HashMap<>();

    public String createUser(String name, String uri) throws UserAlreadyExistsException {
        User user = new User();
        user.setName(name);
        user.setUri(uri);
        String url = ("\"/users/" + name + "\"").toLowerCase();
        if (userMap.containsKey(url)) {
            throw new UserAlreadyExistsException();
        }
        userMap.put(url, user);
        return url;
    }

    public Set<String> getAllUsers(){
        return userMap.keySet();
    }

    public String getUserInfo(String user) throws UserDoesNotExistException {
        String searching = "\"/users/" + user + "\"";
        if (userMap.keySet().contains(searching)) {
            UserInfoPayload result = new UserInfoPayload();
            String id = "/users/" + user;
            result.setId(id);
            result.setName(userMap.get(searching).getName().toLowerCase());
            result.setUri(userMap.get(searching).getUri());
            return UserService.dataToJson(result);
        } else {
            throw new UserDoesNotExistException();
        }

    }
    public String editUser(String url, String name, String uri) {
        return null;
    }

    public static void deleteUser(String id) throws UserDoesNotExistException {
        String searching = "\"/users/" + id + "\"";
        if (!(userMap.containsKey(searching))) {
            throw new UserDoesNotExistException();
        }
        userMap.remove(searching);
    }
}
