package Broker;

import Exceptions.*;
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

    public String buyPlace(String placeId, String gameId, String playerUri, String placeType) throws Exception {
        if (brokerMap.containsKey(gameId)) {
            if (brokerMap.get(gameId).getPlaces().keySet().contains(placeId)) {
                Place place = brokerMap.get(gameId).getPlaces().get(placeId);
                if (place.getOwner() == null) {
                    //TODO: Notify Bank and make sure place only gets sold if transaction is complete
                    place.setOwner(playerUri);
                    return Helper.dataToJson(place);
                } else {
                    throw new PlaceAlreadySoldException();
                }
            } else {
                throw new PlaceDoesNotExistException();
            }
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String tradePlace(String placeId, String gameId, String playerUri) throws Exception{
        if (brokerMap.containsKey(gameId)) {
            if (brokerMap.get(gameId).getPlaces().keySet().contains(placeId)) {
                Place place = brokerMap.get(gameId).getPlaces().get(placeId);
                if (!(place.getOwner() == null)) {
                    place.setOwner(playerUri);
                    return Helper.dataToJson(place);
                } else {
                    throw new PlaceNotSoldException();
                }
            } else {
                throw new PlaceDoesNotExistException();
            }
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String takeHypothecarycredit(String placeId, String gameId) throws Exception {
        if (brokerMap.containsKey(gameId)) {
            if (brokerMap.get(gameId).getPlaces().keySet().contains(placeId)) {
                Place place = brokerMap.get(gameId).getPlaces().get(placeId);
                if (!(place.isHypothecarycredit())) {
                    //TODO: Notify Bank and make sure place is only Hypothecarycredited if transaction is successful
                    place.setHypothecarycredit(true);
                    return Helper.dataToJson(place);
                } else {
                    throw new AlreadyHasAHypothecarycreditException();
                }
            } else {
                throw new PlaceDoesNotExistException();
            }
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String payHypothecarycredit(String placeId, String gameId) throws Exception {
        if (brokerMap.containsKey(gameId)) {
            if (brokerMap.get(gameId).getPlaces().keySet().contains(placeId)) {
                Place place = brokerMap.get(gameId).getPlaces().get(placeId);
                if (place.isHypothecarycredit()) {
                    //TODO: Notify Bank and make sure place is only anti Hypothecarycredited if transaction is successful
                    place.setHypothecarycredit(false);
                    return Helper.dataToJson(place);
                } else {
                    throw new NoHypothecarycreditOnThePlaceException();
                }
            } else {
                throw new PlaceDoesNotExistException();
            }
        } else {
            throw new GameDoesNotExistException();
        }
    }

    public String visitPlace(String placeId, String gameId, String playerUri, String placeType) throws Exception{
        if (brokerMap.containsKey(gameId)) {
            if (brokerMap.get(gameId).getPlaces().keySet().contains(placeId)) {
                Place place = brokerMap.get(gameId).getPlaces().get(placeId);
                if (!(place.getOwner() == null)) {
                    String playerToPay = place.getOwner();
                    //TODO: pay owner depending on the placeType??
                    return Helper.dataToJson(place);
                } else {
                    throw new PlaceNotSoldException();
                }
            } else {
                throw new PlaceDoesNotExistException();
            }
        } else {
            throw new GameDoesNotExistException();
        }
    }
}
