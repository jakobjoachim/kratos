package Game;

import Enums.GameStatus;
import Exceptions.GameAlreadyExistsException;
import Exceptions.GameDoesNotExistException;
import Exceptions.UserDoesNotExistException;
import Exceptions.WrongDataTypeException;
import Tools.Helper;
import Tools.Mutex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameModel {

    private static Map<String, Game> gameMap = new HashMap<>();

    public Set<String> getAllGames(){
        return gameMap.keySet();
    }

    public String createGame(String name, Map<String, String> services) throws GameAlreadyExistsException {
        Game game = new Game();
        game.setName(name);
        game.setServices(services);
        game.setStatus(GameStatus.registration);
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

    public String setStatus(String game, GameStatus status) throws Exception {
        if ((status.equals(GameStatus.running))||(status.equals(GameStatus.finished))) {
            String searching = "\"/games/" + game + "\"";
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
        String searching = "\"/games/" + game + "\"";
        if (gameMap.keySet().contains(searching)) {
            ArrayList<String> playerArray = new ArrayList<>();
            for (String id : gameMap.get(searching).getPlayers().keySet()) {
                String toAdd = "/games/"+ game + "/players/" + id;
                playerArray.add(toAdd);
            }
            return Helper.dataToJson(playerArray);
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

    public String getCurrentPlayer(String game) throws GameDoesNotExistException {
        String searchingGame = "\"/games/" + game + "\"";
        if (gameMap.keySet().contains(searchingGame)) {
            return Helper.dataToJson(gameMap.get(searchingGame).getPlayerQueue().peek());
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String getTurnHolder(String game) throws GameDoesNotExistException {
        String searchingGame = "\"/games/" + game + "\"";
        if (gameMap.keySet().contains(searchingGame)) {
            String holder = gameMap.get(searchingGame).getPlayerMutex().getLockHolder();
            if (holder == null){
                return Helper.dataToJson("No one holding the turn, "+ gameMap.get(searchingGame).getPlayerQueue().peek() + "should take the turn");
            } else {
                return holder;
            }
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String endTurn(String game) throws GameDoesNotExistException {
        String searchingGame = "\"/games/" + game + "\"";
        if (gameMap.keySet().contains(searchingGame)) {
            Mutex playerMutex = gameMap.get(searchingGame).getPlayerMutex();
            if (playerMutex.getLockHolder().equals(gameMap.get(searchingGame).getPlayerQueue().peek())) {
                playerMutex.unlock();
                String toAdd = gameMap.get(searchingGame).getPlayerQueue().poll();
                gameMap.get(searchingGame).getPlayerQueue().add(toAdd);
            } else {
                String returnMessage = playerMutex.getLockHolder() + "stole the turn! Mutex unlocked now";
                playerMutex.unlock();
                return returnMessage;
            }
            return "";
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public int putTurn(String game, String player) throws Exception {
        String searchingGame = "\"/games/" + game + "\"";
        String searchingPlayer = "\"/players/" + player + "\"";
        if (gameMap.keySet().contains(searchingGame)) {
            if (gameMap.get(searchingGame).getPlayers().keySet().contains(searchingPlayer)) {
                Mutex playerMutex = gameMap.get(searchingGame).getPlayerMutex();
                if (playerMutex.getLockHolder()==null) {
                    playerMutex.lock(searchingPlayer);
                    return 201;
                } else {
                    if (playerMutex.getLockHolder().equals(searchingPlayer)) {
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
}
