package Game;

import Enums.GameStatus;
import Enums.ServiceType;
import Exceptions.*;
import Tools.Helper;
import Tools.Mutex;
import Tools.YellowService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameModel {

    //TODO: status = null nicht gut mach mal heil! heute
    private static Map<String, Game> gameMap = new HashMap<>();

    public String getAllGames() {
        return Helper.dataToJson(gameMap.keySet());
    }

    public String createGame(String name, Map<String, String> services) throws GameAlreadyExistsException {
        Game game = new Game();
        game.setName(name);
        game.setServices(services);
        game.setStatus(GameStatus.registration);
        String url = ("/games/" + name).toLowerCase();
        if (gameMap.containsKey(url)) {
            throw new GameAlreadyExistsException();
        }
        game.setId(name);
        gameMap.put(url, game);
        String json = ("\""+ url + "\"").toLowerCase();
        return json;
    }

    public String getGameInfo(String game) throws GameDoesNotExistException {
        String searching = "/games/" + game;
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
        String searching = "/games/" + game;
        if (gameMap.keySet().contains(searching)) {
            return Helper.dataToJson(gameMap.get(searching).getStatus());
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String setStatus(String game, GameStatus status) throws Exception {
        if ((status.equals(GameStatus.running)) || (status.equals(GameStatus.finished))) {
            String searching = "/games/" + game;
            if (gameMap.keySet().contains(searching)) {
                gameMap.get(searching).setStatus(status);
                if (status.equals(GameStatus.running)) {
                    Set<String> players = gameMap.get(searching).getPlayers().keySet();
                    for (String player : players) {
                        gameMap.get(searching).getPlayerQueue().add(player);
                    }
                }
                return Helper.dataToJson(gameMap.get(searching).getStatus());
            } else {
                throw new GameDoesNotExistException();
            }
        } else {
            throw new WrongDataTypeException();
        }
    }

    public String getPlayers(String game) throws GameDoesNotExistException {
        String searching = "/games/" + game;
        if (gameMap.keySet().contains(searching)) {
            ArrayList<String> playerArray = new ArrayList<>();
            for (String id : gameMap.get(searching).getPlayers().keySet()) {
                String toAdd = "/games/" + game + "/players/" + id;
                playerArray.add(toAdd);
            }
            return Helper.dataToJson(playerArray);
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String addUser(String name, boolean ready, String game) throws GameDoesNotExistException {
        String searching = "/games/" + game;
        if (gameMap.keySet().contains(searching)) {
            gameMap.get(searching).getPlayers().put(name, ready);
            return "";
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String getServices(String game) throws GameDoesNotExistException {
        String searching = "/games/" + game;
        if (gameMap.keySet().contains(searching)) {
            return Helper.dataToJson(gameMap.get(searching).getServices());
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String getComponents(String game) throws GameDoesNotExistException {
        String searching = "/games/" + game;
        if (gameMap.keySet().contains(searching)) {
            return Helper.dataToJson(gameMap.get(searching).getComponents());
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String removePlayer(String game, String player) throws Exception {
        String searchingGame = "/games/" + game;
        if (gameMap.keySet().contains(searchingGame)) {
            if (gameMap.get(searchingGame).getPlayers().containsKey(player)) {
                gameMap.get(searchingGame).getPlayers().remove(player);
                return Helper.dataToJson(player);
            } else {
                throw new UserDoesNotExistException();
            }
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String getPlayerReady(String game, String player) throws Exception {
        String searchingGame = "/games/" + game;
        if (gameMap.keySet().contains(searchingGame)) {
            if (gameMap.get(searchingGame).getPlayers().containsKey(player)) {
                return Helper.dataToJson(gameMap.get(searchingGame).getPlayers().get(player).toString());
            } else {
                throw new UserDoesNotExistException();
            }
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String putPlayerReady(String game, String player) throws Exception {
        String searchingGame = "/games/" + game;
        if (gameMap.keySet().contains(searchingGame)) {
            if (gameMap.get(searchingGame).getPlayers().containsKey(player)) {
                if (gameMap.get(searchingGame).getPlayers().get(player).equals(true)) {
                    gameMap.get(searchingGame).getPlayers().remove(player);
                    gameMap.get(searchingGame).getPlayers().put(player, false);
                } else {
                    gameMap.get(searchingGame).getPlayers().remove(player);
                    gameMap.get(searchingGame).getPlayers().put(player, true);
                }
                return Helper.dataToJson(player);
            } else {
                throw new UserDoesNotExistException();
            }
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String getCurrentPlayer(String game) throws GameDoesNotExistException {
        String searchingGame = "/games/" + game;
        if (gameMap.keySet().contains(searchingGame)) {
            return Helper.dataToJson(gameMap.get(searchingGame).getPlayerQueue().peek());
        } else {
            throw new GameDoesNotExistException();
        }
    }

    String getTurnHolder(String game) throws GameDoesNotExistException {
        String searchingGame = "/games/" + game;
        if (gameMap.keySet().contains(searchingGame)) {
            String holder = gameMap.get(searchingGame).getPlayerMutex().getLockHolder();
            if (holder == null) {
                return Helper.dataToJson("No one holding the turn, " + gameMap.get(searchingGame).getPlayerQueue().peek() + " should take the turn");
            } else {
                return holder;
            }
        } else {
            throw new GameDoesNotExistException();
        }
    }

    String endTurn(String game) throws Exception {
        String searchingGame = "/games/" + game;
        if (gameMap.keySet().contains(searchingGame)) {
            Mutex playerMutex = gameMap.get(searchingGame).getPlayerMutex();
            if (playerMutex.getLockHolder() == null) {
                throw new NoTurnActiveException();
            }
            if (playerMutex.getLockHolder().equals(gameMap.get(searchingGame).getPlayerQueue().peek())) {
                playerMutex.unlock();
                String toAdd = gameMap.get(searchingGame).getPlayerQueue().poll();
                gameMap.get(searchingGame).getPlayerQueue().add(toAdd);
            } else {
                String returnMessage = playerMutex.getLockHolder() + " stole the turn! Mutex unlocked now";
                playerMutex.unlock();
                return returnMessage;
            }
            turnMessenger(gameMap.get(searchingGame).getPlayerQueue().peek(), game);
            return "";
        } else {
            throw new GameDoesNotExistException();
        }
    }
    int putTurn(String game, String player) throws Exception {
        String searchingGame = "/games/" + game;
        if (gameMap.keySet().contains(searchingGame)) {
            if (gameMap.get(searchingGame).getPlayers().keySet().contains(player)) {
                Mutex playerMutex = gameMap.get(searchingGame).getPlayerMutex();
                if (playerMutex.getLockHolder() == null) {
                    playerMutex.lock(player);
                    return 201;
                } else {
                    if (playerMutex.getLockHolder().equals(player)) {
                        return 200;
                    } else {
                        return 409;
                    }
                }
            } else {
                throw new UserDoesNotExistException();
            }
        } else {
            throw new GameDoesNotExistException();
        }
    }

    private void turnMessenger(String nextPlayer, String game) throws UnirestException {
        String uri = YellowService.getServiceUrlForType(ServiceType.CLIENT) + "/turn";
        MessengerPayload loadDaShip = new MessengerPayload(nextPlayer, game);
        HttpResponse<JsonNode> jsonResponse =
                Unirest.post(uri)
                        .header("Content-Type", "application/json")
                        .body(loadDaShip)
                        .asJson();
    }
}
