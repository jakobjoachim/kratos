package Game;

import Exceptions.GameAlreadyExistsException;
import Exceptions.UserAlreadyExistsException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by jakob on 09/05/16.
 */
public class GameModel {
    private static Map<String, Game> gameMap = new HashMap<>();

    public Set<String> getAllGames(){
        return gameMap.keySet();
    }

    public String createGame(String name, Map<String, String> services) throws GameAlreadyExistsException {
        Game game = new Game();
        game.setName(name);
        game.setServices(services);
        String url = ("\"/games/" + name + "\"").toLowerCase();
        if (gameMap.containsKey(url)) {
            throw new GameAlreadyExistsException();
        }
        game.setId(name);
        gameMap.put(url, game);
        return url;
    }
}
