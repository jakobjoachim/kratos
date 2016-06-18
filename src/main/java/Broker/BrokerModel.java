package Broker;

import Bank.MoneyTransfer;
import Enums.ServiceType;
import Exceptions.*;
import Tools.Helper;
import Tools.YellowService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

class BrokerModel {

    private static Map<String, Broker> brokerMap = new HashMap<>();

    String createNewGame(String game) throws GameAlreadyExistsException{
        if (!(brokerMap.containsKey(game))) {
            Broker broker = new Broker();
            broker.setGameId(game);
            return game;
        } else {
            throw new GameAlreadyExistsException();
        }
    }

    String createPlace(int placeId, String type, String description, String gameId, int buyCost, Map<Integer, Integer> rentMap, int hypothecarycreditAmount) throws Exception {
        if (brokerMap.containsKey(gameId)) {
            Broker broker = brokerMap.get(gameId);
            if (!(brokerMap.get(gameId).getPlaces().keySet().contains(placeId))) {
                Place place = new Place(description, type, buyCost, rentMap, hypothecarycreditAmount);
                broker.getPlaces().put(placeId, place);
                return Helper.dataToJson(place);
            } else {
                throw new PlaceAlreadyExistException();
            }
        } else {
            throw new GameDoesNotExistException();
        }
    }

    String buyPlace(int placeId, String gameId, String playerUri, String placeType) throws Exception {
        if (brokerMap.containsKey(gameId)) {
            if (brokerMap.get(gameId).getPlaces().keySet().contains(placeId)) {
                Place place = brokerMap.get(gameId).getPlaces().get(placeId);
                if (place.getOwner() == null) {
                    //TODO: Get correct uri for the bank
                    String bankUri = YellowService.getServiceUrlForType(ServiceType.BANK);
                    distributedTransaction(playerUri, "bank", place.getBuycost(), place, "bankuri");
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

    String tradePlace(int placeId, String gameId, String playerUri) throws Exception{
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

    String takeHypothecarycredit(int placeId, String gameId) throws Exception {
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

    String payHypothecarycredit(int placeId, String gameId) throws Exception {
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

    String visitPlace(int placeId, String gameId, String playerUri, String placeType) throws Exception{
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

    private boolean distributedTransaction(String from, String to, int amount, Place place, String bankUri) {
        MoneyTransfer moneyTransfer = new MoneyTransfer(from, to, amount);
        PlaceTransfer placeTransfer = new PlaceTransfer(to, place);
        if (placeTransfer.commit()) {
            try {
                transferMoney(bankUri, moneyTransfer);
            } catch (Exception e){
                placeTransfer.rollback();
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    private String transferMoney(String bankUri, MoneyTransfer moneyTransfer) throws Exception{
        String url = bankUri + "/transfer/from/" + moneyTransfer.getFrom() + "/to/" + moneyTransfer.getTo() + "/" + moneyTransfer.getAmount();
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post(url).asJson();
            JSONObject data = jsonResponse.getBody().getObject();
            return (String) data.get("transactionId");
        } catch (Exception e){
            throw new MoneyTransferFailedException();
        }
    }
}
