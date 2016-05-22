package Broker;

import Exceptions.GameAlreadyExistsException;
import Exceptions.GameDoesNotExistException;
import Exceptions.PlaceAlreadyExistException;
import Tools.Helper;

import java.util.HashMap;
import java.util.Map;

public class BrokerModel {

    private static Map<String, Broker> brokerMap = new HashMap<>();

    public String createNewGame(String game) throws GameAlreadyExistsException{
        if (!(brokerMap.containsKey(game))) {
            Broker broker = new Broker();
            broker.setGameId(game);
            return game;
        } else {
            throw new GameAlreadyExistsException();
        }
    }

    public String createPlace(String placeId, String type, String description, String gameId) throws Exception {
        if (brokerMap.containsKey(gameId)) {
            Broker broker = brokerMap.get(gameId);
            if (!(brokerMap.get(gameId).getPlaces().keySet().contains(placeId))) {
                Place place = new Place(description, type);
                broker.getPlaces().put(placeId, place);
                return Helper.dataToJson(place);
            } else {
                throw new PlaceAlreadyExistException();
            }
        } else {
            throw new GameDoesNotExistException();
        }
    }
}
