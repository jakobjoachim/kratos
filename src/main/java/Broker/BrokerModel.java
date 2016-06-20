package Broker;

import Enums.ServiceType;
import Exceptions.*;
import Tools.Helper;
import Tools.SharedPayloads.EventPayload;
import Tools.YellowService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

class BrokerModel {

    private static final String REASON = "\"reason\": \"";
    private static final String END = "\"";
    private static Map<String, Broker> brokerMap = new HashMap<>();

    String createNewGame(String game) throws GameAlreadyExistsException{
        if (!(brokerMap.containsKey(game))) {
            Broker broker = new Broker();
            broker.setGameId(game);
            brokerMap.put(game, broker);
            return Helper.dataToJson(game);
        } else {
            throw new GameAlreadyExistsException();
        }
    }

    String createPlace(String placeId, String type, String description, String gameId, int buyCost, Map<Integer, Integer> rentMap, int hypothecarycreditAmount) throws Exception {
        if (brokerMap.containsKey(gameId)) {
            Broker broker = brokerMap.get(gameId);
            if (!(brokerMap.get(gameId).getPlaces().keySet().contains(placeId))) {
                Place place = new Place(description, type, buyCost, rentMap, hypothecarycreditAmount);
                broker.getPlaces().put(placeId, place);
                return "{\"placeUri\": \"/broker/" + gameId + "/places/" + placeId + "\"}";
            } else {
                throw new PlaceAlreadyExistException();
            }
        } else {
            throw new GameDoesNotExistException();
        }
    }

    String buyPlace(String placeId, String gameId, String playerUri) throws Exception {
        if (brokerMap.containsKey(gameId)) {
            if (brokerMap.get(gameId).getPlaces().keySet().contains(placeId)) {
                Place place = brokerMap.get(gameId).getPlaces().get(placeId);
                if (place.getOwner() == null) {
                    buyThePlace(playerUri, place.getBuycost(), place, gameId);
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

    String takeHypothecarycredit(String placeId, String gameId) throws Exception {
        if (brokerMap.containsKey(gameId)) {
            if (brokerMap.get(gameId).getPlaces().keySet().contains(placeId)) {
                Place place = brokerMap.get(gameId).getPlaces().get(placeId);
                if (!(place.isHypothecarycredit())) {
                    getHypothecarycredit(place.getOwner(), place.getHypothecarycreditAmount(), gameId, place);
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

    String payHypothecarycredit(String placeId, String gameId) throws Exception {
        if (brokerMap.containsKey(gameId)) {
            if (brokerMap.get(gameId).getPlaces().keySet().contains(placeId)) {
                Place place = brokerMap.get(gameId).getPlaces().get(placeId);
                if (place.isHypothecarycredit()) {
                    removeHypothecarycredit(place.getOwner(), place.getHypothecarycreditAmount(), place, gameId);
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

    String visitPlace(String placeId, String gameId, String playerUri) throws Exception{
        if (brokerMap.containsKey(gameId)) {
            if (brokerMap.get(gameId).getPlaces().keySet().contains(placeId)) {
                Place place = brokerMap.get(gameId).getPlaces().get(placeId);
                if (!(place.getOwner() == null)) {
                    payRent(playerUri, place.getOwner(), place.getRentMap().get(place.getNumberHouses()), gameId);
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

    private void payRent(String from, String to, int amount, String gameId) throws Exception {
        String bankUri = getBankUri(gameId);
        String url = bankUri + "/transfer/from/" + from + "/to" + to + "/amount/" + amount;
        String reason = REASON + "rent" + END;
        transferMoney(url, reason);
    }

    private boolean buyThePlace(String buyer, int amount, Place place, String gameId) throws NoBankRegisteredException {
        PlaceTransfer placeTransfer = new PlaceTransfer(buyer, place);
        String bankUri = getBankUri(gameId);
        if (placeTransfer.commit()) {
            try {
                String url = bankUri + "/transfer/from/" + buyer + "/" + amount;
                String reason = REASON + "buying: "+ place.getDescription() + END;
                transferMoney(url, reason);
                EventPayload eventPayload = new EventPayload("Place bought from Bank", gameId, "player_bought_street", "PlaceBought", "broker", buyer);
                Helper.broadcastEvent(eventPayload);
            } catch (Exception e){
                placeTransfer.rollback();
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    private void getHypothecarycredit(String player, int amount, String gameId, Place place) throws NoBankRegisteredException {
        String bankUri = getBankUri(gameId);
        String url = bankUri + "/transfer/to" + player + "/amount/" + amount;
        String reason = REASON + "Hypothecarycredit" + END;
        HypothecarycreditTransfer transfer = new HypothecarycreditTransfer(place, true);
        if(transfer.commit()) {
            try {
                transferMoney(url, reason);
            } catch (Exception e) {
                transfer.rollback();
            }
        }
    }

    private void removeHypothecarycredit(String player, int amount, Place place, String gameId) throws NoBankRegisteredException {
        String bankUri = getBankUri(gameId);
        String url = bankUri + "/transfer/from" + player + "/amount/" + amount;
        String reason = REASON + "Paying the Hypothecarycredit" + END;
        HypothecarycreditTransfer transfer = new HypothecarycreditTransfer(place, false);
        if(transfer.commit()) {
            try {
                transferMoney(url, reason);
            } catch (Exception e) {
                transfer.rollback();
            }
        }
    }

    private String transferMoney(String uri, String jsonBody) throws Exception{
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post(uri).body(jsonBody).asJson();
            JSONObject data = jsonResponse.getBody().getObject();
            return (String) data.get("transactionId");
        } catch (Exception e){
            throw new MoneyTransferFailedException();
        }
    }

    private String getBankUri(String gameId) throws NoBankRegisteredException {
        String gameServiceUri = YellowService.getServiceUrlForType(ServiceType.GAME) + gameId + "/services";
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(gameServiceUri).asJson();
            JSONObject data = jsonResponse.getBody().getObject();
            return (String) data.get("bank");
        } catch (Exception e){
            throw new NoBankRegisteredException();
        }
    }
}
