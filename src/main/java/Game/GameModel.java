package Game;

import Exceptions.GameAlreadyExistsException;
import Exceptions.GameDoesNotExistException;
import Exceptions.UserDoesNotExistException;
import Exceptions.WrongDataTypeException;
import Tools.Helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        String searching = "\"/games/" + game + "\"";
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
        String searching = "\"/games/" + game + "\"";
        if (gameMap.keySet().contains(searching)) {
            return Helper.dataToJson(gameMap.get(searching).getStatus());
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String setStatus(String game, String status) throws Exception {
        if ((status.equals(RUNNING))||(status.equals(FINISHED))) {
            String searching = "\"/games/" + game + "\"";
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
        String searching = "\"/games/" + game + "\"";
        if (gameMap.keySet().contains(searching)) {
            return Helper.dataToJson(gameMap.get(searching).getPlayers());
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String addUser(String name, boolean ready, String game) throws GameDoesNotExistException {
        String searching = "\"/games/" + game + "\"";
        if (gameMap.keySet().contains(searching)) {
            gameMap.get(searching).getPlayers().put(name, ready);
            return "";
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String getServices(String game) throws GameDoesNotExistException {
        String searching = "\"/users/" + game + "\"";
        if (gameMap.keySet().contains(searching)) {
            return Helper.dataToJson(gameMap.get(searching).getServices());
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String getComponents(String game) throws GameDoesNotExistException {
        String searching = "\"/users/" + game + "\"";
        if (gameMap.keySet().contains(searching)) {
            return Helper.dataToJson(gameMap.get(searching).getComponents());
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String removePlayer(String game, String player) throws Exception {
        String searchingGame = "\"/games/" + game + "\"";
        if (gameMap.keySet().contains(searchingGame)) {
            String searchingPlayer = "\"/users/" + player + "\"";
            if (gameMap.get(searchingGame).getPlayers().containsKey(searchingPlayer)) {
                gameMap.get(searchingGame).getPlayers().remove(searchingPlayer);
                return Helper.dataToJson(searchingPlayer);
            } else {
                throw new UserDoesNotExistException();
            }
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String getPlayerReady(String game, String player) throws Exception {
        String searchingGame = "\"/games/" + game + "\"";
        if (gameMap.keySet().contains(searchingGame)) {
            String searchingPlayer = "\"/users/" + player + "\"";
            if (gameMap.get(searchingGame).getPlayers().containsKey(searchingPlayer)) {
                return (gameMap.get(searchingGame).getPlayers().get(searchingPlayer).toString());
            } else {
                throw new UserDoesNotExistException();
            }
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String putPlayerReady(String game, String player) throws Exception {
        String searchingGame = "\"/games/" + game + "\"";
        if (gameMap.keySet().contains(searchingGame)) {
            String searchingPlayer = "\"/users/" + player + "\"";
            if (gameMap.get(searchingGame).getPlayers().containsKey(searchingPlayer)) {
                if (gameMap.get(searchingGame).getPlayers().get(searchingPlayer).equals(true)) {
                    gameMap.get(searchingGame).getPlayers().remove(searchingPlayer);
                    gameMap.get(searchingGame).getPlayers().put(searchingPlayer, false);
                } else {
                    gameMap.get(searchingGame).getPlayers().remove(searchingPlayer);
                    gameMap.get(searchingGame).getPlayers().put(searchingPlayer, true);
                }
                return Helper.dataToJson(searchingPlayer);
            } else {
                throw new UserDoesNotExistException();
            }
        } else {
            throw new GameDoesNotExistException();
        }
    }
}
