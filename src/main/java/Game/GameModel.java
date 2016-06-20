package Game;

import Enums.GameStatus;
import Exceptions.*;
import Tools.Helper;
import Tools.Mutex;
import Tools.SharedPayloads.EventPayload;
import Tools.SharedPayloads.PayloadPayload;

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
                    EventPayload eventPayload = new EventPayload("game now running", game, "game_status_changed", "game_status_changed", "game", "");
                    Helper.broadcastEvent(eventPayload);
                    EventPayload newPlayer = new EventPayload("player changed", game, "turn_changed", "player changed", "game", gameMap.get(searching).getPlayerQueue().peek());
                    Helper.broadcastEvent(newPlayer);
                } else {
                    PayloadPayload payloadPayload = new PayloadPayload(GameStatus.running.toString(), GameStatus.finished.toString());
                    EventPayload eventPayload = new EventPayload("game finished", game, "game_has_finished", "game_status_changed", "game", "");
                    eventPayload.setPayload(payloadPayload);
                    Helper.broadcastEvent(eventPayload);
                    gameMap.get(searching).setStatus(GameStatus.finished);
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
                    PayloadPayload payloadPayload = new PayloadPayload("true", "false");
                    EventPayload eventPayload = new EventPayload("Player not ready", game, "player_ready_changed", "player not ready", "game", player);
                    eventPayload.setPayload(payloadPayload);
                    Helper.broadcastEvent(eventPayload);
                } else {
                    gameMap.get(searchingGame).getPlayers().remove(player);
                    gameMap.get(searchingGame).getPlayers().put(player, true);
                    PayloadPayload payloadPayload = new PayloadPayload("false", "true");
                    EventPayload eventPayload = new EventPayload("Player ready", game, "player_ready_changed", "player ready", "game", player);
                    eventPayload.setPayload(payloadPayload);
                    Helper.broadcastEvent(eventPayload);
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
            EventPayload eventPayload = new EventPayload("turn changed", game, "turn_changed", "players turn is over", "game", gameMap.get(searchingGame).getPlayerQueue().peek());
            Helper.broadcastEvent(eventPayload);
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
}
