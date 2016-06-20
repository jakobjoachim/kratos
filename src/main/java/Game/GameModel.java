package Game;

import Enums.GameStatus;
import Enums.ServiceType;
import Exceptions.*;
import Tools.Helper;
import Tools.Mutex;
import Tools.YellowService;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.*;
import java.util.stream.Collectors;

class GameModel {

    private static Map<String, Game> gameMap = new HashMap<>();

    String getAllGames() {
        return Helper.dataToJson(gameMap.keySet());
    }

    String createGame(String name, Map<String, String> services) throws GameAlreadyExistsException {
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
        return ("\""+ url + "\"").toLowerCase();
    }

    String getGameInfo(String game) throws GameDoesNotExistException {
        String searching = "/games/" + game;
        if (gameMap.keySet().contains(searching)) {
            Game g = gameMap.get(searching);
            ArrayList players = g.getPlayers().keySet().stream().map(player -> new PlayerPayload(g.getPlayers().get(player), player)).collect(Collectors.toCollection(ArrayList::new));
            EnesSuperDuperPayload load = new EnesSuperDuperPayload(g.getName(), g.getId(), players, g.getServices(), g.getStatus() ,g.getPlayerQueue(), g.getPlayerMutex());
            return Helper.dataToJson(load);
        } else {
            throw new GameDoesNotExistException();
        }
    }

    String getGameStatus(String game) throws GameDoesNotExistException {
        String searching = "/games/" + game;
        if (gameMap.keySet().contains(searching)) {
            return Helper.dataToJson(gameMap.get(game).getStatus());
        } else {
            throw new GameDoesNotExistException();
        }
    }

    String setStatus(String game, GameStatus status) throws Exception {
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

    String getPlayers(String game) throws GameDoesNotExistException {
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

    String addUser(String name, boolean ready, String game) throws GameDoesNotExistException {
        String searching = "/games/" + game;
        if (gameMap.keySet().contains(searching)) {
            gameMap.get(searching).getPlayers().put(name, ready);
            return "";
        } else {
            throw new GameDoesNotExistException();
        }
    }

    String getServices(String game) throws GameDoesNotExistException {
        String searching = "/games/" + game;
        if (gameMap.keySet().contains(searching)) {
            return Helper.dataToJson(gameMap.get(searching).getServices());
        } else {
            throw new GameDoesNotExistException();
        }
    }

    String getComponents(String game) throws GameDoesNotExistException {
        String searching = "/games/" + game;
        if (gameMap.keySet().contains(searching)) {
            return Helper.dataToJson(gameMap.get(searching).getComponents());
        } else {
            throw new GameDoesNotExistException();
        }
    }

    String removePlayer(String game, String player) throws Exception {
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

    String getPlayerReady(String game, String player) throws Exception {
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

    String putPlayerReady(String game, String player) throws Exception {
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

    String getCurrentPlayer(String game) throws GameDoesNotExistException {
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
        Unirest.post(uri).header("Content-Type", "application/json").body(loadDaShip).asJson();
    }
}
