package Game;

import Exceptions.GameAlreadyExistsException;
import Exceptions.GameDoesNotExistException;
import Exceptions.WrongDataTypeException;
import Tools.Helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by jakob on 09/05/16.
 */
public class GameModel {

    private final String RUNNING = "running";
    private final String FINISHED = "finished";


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

    public String getGameInfo(String game) throws GameDoesNotExistException {
        String searching = "\"/users/" + game + "\"";
        if (gameMap.keySet().contains(searching)) {
            Game result = new Game();
            String id = "/games/" + game;
            result.setId(id);
            result.setName(gameMap.get(searching).getName().toLowerCase());
            result.setServices(gameMap.get(searching).getServices());
            result.setPlayers(gameMap.get(searching).getPlayers());
            return Helper.dataToJson(result);
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String getGameStatus(String game) throws GameDoesNotExistException {
        String searching = "\"/users/" + game + "\"";
        if (gameMap.keySet().contains(searching)) {
            return Helper.dataToJson(gameMap.get(searching).getStatus());
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String setStatus(String game, String status) throws Exception {
        if ((status.equals(RUNNING))||(status.equals(FINISHED))) {
            String searching = "\"/users/" + game + "\"";
            if (gameMap.keySet().contains(searching)) {
                gameMap.get(searching).setStatus(status);
                return Helper.dataToJson(gameMap.get(searching).getStatus());
            } else {
                throw new GameDoesNotExistException();
            }
        } else {
            throw new WrongDataTypeException();
        }
    }

    public String getPlayers(String game) throws GameDoesNotExistException {
        String searching = "\"/users/" + game + "\"";
        if (gameMap.keySet().contains(searching)) {
            return Helper.dataToJson(gameMap.get(searching).getPlayers());
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String addUser(String name, boolean ready, String game) throws GameDoesNotExistException {
        String searching = "\"/users/" + game + "\"";
        if (gameMap.keySet().contains(searching)) {
            gameMap.get(searching).getPlayers().put(name, ready);
            return "";
        } else {
            throw new GameDoesNotExistException();
        }
    }
}
