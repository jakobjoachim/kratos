package User;

import Exceptions.UserAlreadyExistsException;
import Exceptions.UserDoesNotExistException;

import java.util.*;

public class UserModel {
    private Map<String, User> userMap = new HashMap<>();

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

    public List<String> getUserInfo(String user) throws UserDoesNotExistException {
        String searching = "\"/users/" + user + "\"";
        List<String> result = new ArrayList<>();
        if (userMap.keySet().contains(searching)) {
            result.add(userMap.get(searching).getUri());
            result.add(userMap.get(searching).getName());
            result.add(searching);
            return result;
        } else {
            throw new UserDoesNotExistException();
        }

    }
    public String editUser(String url, String name, String uri) {
        return null;
    }
}
