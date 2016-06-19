package Board;


import Broker.PlacePayload;
import Enums.ServiceType;
import Exceptions.*;
import Tools.Helper;
import Tools.SharedPayloads.EventPayload;
import Tools.SharedPayloads.PayloadPayload;
import Tools.YellowService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;

import java.util.*;

public class BoardModel {
    private static Map<String, Board> boards = new HashMap<>();

    private void createPlaceAndField(String description, String type, int buyCost, Map<Integer, Integer> rentMap, int hypoAmount, String id, Board board, int fieldId, String gameId) throws PlaceAlreadyExistException {
        String brokerUri = YellowService.getServiceUrlForType(ServiceType.BROKER);
        PlacePayload place = new PlacePayload();
        place.setType(type);
        place.setDescription(description);
        place.setBuycost(buyCost);
        place.setRentMap(rentMap);
        place.setHypothecarycreditAmount(hypoAmount);

        String url = brokerUri + gameId + "/places/" + id;
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post(url).body(Helper.dataToJson(place)).asJson();
            JSONObject data = jsonResponse.getBody().getObject();
            String uri = (String) data.get("placeUri");
            board.getPlaceUri().put(fieldId, uri);
        } catch (Exception e) {
            throw new PlaceAlreadyExistException();
        }
    }

    String createBoard(String gameUri) throws BoardAlreadyExistException {
        Board board = new Board(Helper.nextId());
        String boardUri = "/boards/" + board.getId();
        boards.put(board.getId(), board);
        Map<Integer, Integer> rent = new HashMap<>();
        rent.put(1, 1);

        try {
            createPlaceAndField("Startfeld", "LOS", 0, rent, 5, gameUri, board, 0, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("a", "Badstraße", 100, rent, 5, gameUri, board, 1, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("as", "Talstrasse", 100, rent, 5, gameUri, board, 2, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("dfgdf", "Grosse Freiheit", 100, rent, 5, gameUri, board, 3, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("sdfsdf", "Korachstrasse", 100, rent, 5, gameUri, board, 4, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("jhjhj", "Unterberg", 100, rent, 5, gameUri, board, 5, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Berliner Tor", 100, rent, 5, gameUri, board, 6, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
//        createPlaceAndField(2, "Gemeinschaftsfeld", gameUri);
//        createPlaceAndField(3, "Turmstraße", gameUri);
//        createPlaceAndField(4, "Einkommenssteuer", gameUri);
//        createPlaceAndField(5, "Südbahnhof", gameUri);
//        createPlaceAndField(6, "Chausseestraße", gameUri);
//        createPlaceAndField(7, "Ereignisfeld", gameUri);
//        createPlaceAndField(8, "Elisenstraße", gameUri);
//        createPlaceAndField(9, "Poststraße", gameUri);
//        createPlaceAndField(10, "Im Gefängis / Nur zu Besuch", gameUri);
//        createPlaceAndField(11, "Seestraße", gameUri);
//        createPlaceAndField(12, "Elektizitätswerk", gameUri);
//        createPlaceAndField(13, "Hafenstraße", gameUri);
//        createPlaceAndField(14, "Neue Straße", gameUri);
//        createPlaceAndField(15, "Westbahnhof", gameUri);
//        createPlaceAndField(16, "Münchener Straße", gameUri);
//        createPlaceAndField(17, "Gemeinschaftsfeld", gameUri);
//        createPlaceAndField(18, "Wiener Straße", gameUri);
//        createPlaceAndField(19, "Berliner Straße", gameUri);
//        createPlaceAndField(20, "Frei  Parken", gameUri);
//        createPlaceAndField(21, "Theaterstraße", gameUri);
//        createPlaceAndField(22, "Ereignisfeld", gameUri);
//        createPlaceAndField(23, "Museumsstraße", gameUri);
//        createPlaceAndField(24, "Opernplatz", gameUri);
//        createPlaceAndField(25, "Nordbahnhof", gameUri);
//        createPlaceAndField(26, "Lessingstraße", gameUri);
//        createPlaceAndField(27, "Schillerstraße", gameUri);
//        createPlaceAndField(28, "Wasserwerk", gameUri);
//        createPlaceAndField(29, "Goethestraße", gameUri);
//        createPlaceAndField(30, "Gehe ins Gefängnis", gameUri);
//        createPlaceAndField(31, "Rathausplatz", gameUri);
//        createPlaceAndField(32, "Hauptstraße", gameUri);
//        createPlaceAndField(33, "Gemeinschaftsfeld", gameUri);
//        createPlaceAndField(34, "Bahnhofstraße", gameUri);
//        createPlaceAndField(35, "Hauptbahnhof", gameUri);
//        createPlaceAndField(36, "Ereignisfeld", gameUri);
//        createPlaceAndField(37, "Parkstraße", gameUri);
//        createPlaceAndField(38, "Zusatzsteuer", gameUri);
//        createPlaceAndField(39, "Schlossallee", gameUri);

        // Beispiel für Event broadcasting, diese gut diese

//        PayloadPayload payloadPayload = new PayloadPayload("ajshdkajshd", "ahsldkalksdlkas");
//        EventPayload eventPayload = new EventPayload("",);
//        eventPayload.setPayload(payloadPayload);
//
//        Helper.broadcastEvent(eventPayload);

        return Helper.dataToJson(board);
    }

    public String placePawn(String player, String place, String position, String id) {
        Pawn pawn = new Pawn();
        pawn.setPlayer(player);
        pawn.setPlace(place);
        pawn.setPosition(Integer.parseInt(position));
        pawn.setId(Helper.nextId());
        boards.get(id).getPawns().add(pawn);
        boards.get(id).getPlaceUri().put(Integer.parseInt(pawn.getId()), place);
        return Helper.dataToJson(pawn);
    }


    public String getAllGames() {
        return Tools.Helper.dataToJson(boards.keySet());
    }


    public String getBoard(String gameId) throws BoardDoesNotExistException {
        if (boards.containsKey(gameId)) {
            return Helper.dataToJson(boards.get(gameId));
        } else {
            throw new BoardDoesNotExistException();
        }
    }


    public String removeBoard(String gameId) throws BoardDoesNotExistException {
        if (boards.containsKey(gameId)) {
            boards.remove(gameId);
            return "Game with ID " + gameId + " removed successfully";
        } else {
            throw new BoardDoesNotExistException();
        }
    }

    public String getAllPlayerPositions(String gameId) throws NoPlayersInGameException {
        if (boards.containsKey(gameId)) {
            //TODO gibt komisches json-etwas wieder // fixen
            return Helper.dataToJson(boards.get(gameId).getPawns());
        } else {
            throw new NoPlayersInGameException();
        }
    }

    public String removePawn(String gameId, String pawnId) throws Exception {
        if (boards.containsKey(gameId)) {
            for (Pawn paws : boards.get(gameId).getPawns()) {
                if (paws.getId() == pawnId) {
                    boards.get(gameId).getPawns().remove(boards.get(gameId).getPawns().indexOf(paws));
                    if (boards.get(gameId).getPlaceUri().containsKey(pawnId)) {
                        boards.get(gameId).getPlaceUri().remove(pawnId);
                        return "Pawn with ID" + pawnId + "was sucessfully removed";
                    }
                }
            }
        } else {
            throw new BoardDoesNotExistException();
        }
        return null;
    }

    public String getPawns(String gameId, String pawnId) throws Exception {
        if (boards.containsKey(gameId)) {
            for (Pawn paws : boards.get(gameId).getPawns()) {
                if (paws.getId() == pawnId) {
                    return Helper.dataToJson(paws);
                }
            }
        } else {
            throw new BoardDoesNotExistException();
        }
        return null;
    }

    public String movePawn(String move, String gameId, String pawnId) throws Exception {
        int steps = Integer.parseInt(move);
        if (boards.containsKey(gameId)) {
            for (Pawn paws : boards.get(gameId).getPawns()) {
                if (paws.getId() == pawnId) {
                    paws.move(steps);
                    return Helper.dataToJson(paws);
                }
            }
        } else {
            throw new BoardDoesNotExistException();
        }
        return null;
    }


    public String getRolls(String gameId, String pawnId) throws Exception {
        if (boards.containsKey(gameId)) {
            for (Pawn paws : boards.get(gameId).getPawns()) {
                if (paws.getId() == pawnId) {
                    return Helper.dataToJson(paws.getRoll());
                }
            }
        } else {
            throw new BoardDoesNotExistException();
        }
        return null;
    }


    public String rollDice(String gameId, String pawnId) throws Exception {
        Pawn pawn = new Pawn();
        if (boards.containsKey(gameId)) {
            for (Pawn paws : boards.get(gameId).getPawns()) {
                if (paws.getId() == pawnId) {
                    pawn = paws;
                }
            }
        } else {
            throw new BoardDoesNotExistException();
        }
        String playerUri = pawn.getPlayer();
        String playerName = "";
        int pos = playerUri.lastIndexOf("/") - 1;
        for (int i = pos; pos < playerUri.length(); i++) {
            playerName += playerUri.charAt(i);
        }
        String placeId;
        if (boards.containsKey(gameId)) {
            if (boards.get(gameId).getPlaceUri().containsKey(pawnId)) {
                placeId = boards.get(gameId).getPlaceUri().get(pawnId);
            } else {
                throw new PawnDoesNotExistException();
            }
        } else {
            throw new BoardDoesNotExistException();
        }
        String url = YellowService.getServiceUrlForType(ServiceType.BROKER) + gameId + "/places/" + placeId;
        HttpResponse<JsonNode> jsonResponse = Unirest.get(url).asJson();
        if (jsonResponse.getStatus() == 409) {
            url = YellowService.getServiceUrlForType(ServiceType.BROKER) + gameId + "/places/" + placeId + "/visit";
            jsonResponse = Unirest.post(playerName).asJson();
        }
        JSONObject data = jsonResponse.getBody().getObject();

        String dice = YellowService.getServiceUrlForType(ServiceType.DICE) + "?game=" + gameId + "&player=" + pawnId;
        HttpResponse<JsonNode> jsonRes = Unirest.get(dice).asJson();
        ObjectMapper mapper = new ObjectMapper();
        RollPayload creation = mapper.readValue(jsonRes.getBody().toString(), RollPayload.class);
        int steps = Integer.parseInt(creation.getNumber());
        if (boards.containsKey(gameId)) {
                for (Pawn paws : boards.get(gameId).getPawns()) {
                    if (paws.getId() == pawnId) {
                        paws.move(steps);
                        return Helper.dataToJson(paws);
                    }
                }
        } else {
            throw new BoardDoesNotExistException();
        }
        return Helper.dataToJson(data);
    }
}
