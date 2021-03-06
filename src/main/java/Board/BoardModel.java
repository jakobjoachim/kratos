package Board;

import Broker.UserPayload;
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
        //String brokerUri = "http://localhost:1234/broker";
        String url = brokerUri + "/" + gameId + "/places/" + fieldId;
        //System.out.println(url);
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post(url).body(Helper.dataToJson(place)).asJson();
            JSONObject data = jsonResponse.getBody().getObject();
            ObjectMapper mapper = new ObjectMapper();
            PlaceUriPayload creation = mapper.readValue(data.toString(), PlaceUriPayload.class);
            board.getPlaceUri().put(fieldId, creation.getPlaceUri());
        } catch (Exception e) {
            e.printStackTrace();
            throw new PlaceAlreadyExistException();
        }
    }

    String createBoard(String gameUri) throws Exception {
        String id = gameUri;
        if (boards.containsKey(id)) {
            throw new BoardAlreadyExistException();
        }
        Board board = new Board(id);
        boards.put(id, board);
        Map<Integer, Integer> rent = new HashMap<>();
        rent.put(1, 1);

        try {
            createPlaceAndField("Startfeld", "LOS", 0, rent, 0, gameUri, board, 0, id);
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("Straße", "Badstraße", 60, rent, 30, gameUri, board, 1, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Gemeinschaftsfeld", 0, rent, 0, gameUri, board, 2, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Turmstraße", 60, rent, 30, gameUri, board, 3, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Einkommenssteuer", 0, rent, 0, gameUri, board, 4, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Südbahnhof", 200, rent, 100, gameUri, board, 5, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Chausseestraße", 100, rent, 50, gameUri, board, 6, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Ereignisfeld", 0, rent, 0, gameUri, board, 7, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Elisenstraße", 100, rent, 50, gameUri, board, 8, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Poststraße", 120, rent, 60, gameUri, board, 9, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Im Gefängis / Nur zu Besuch", 0, rent, 0, gameUri, board, 10, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Seestraße", 140, rent, 70, gameUri, board, 11, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Elektizitätswerk", 150, rent, 75, gameUri, board, 12, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Hafenstraße", 140, rent, 70, gameUri, board, 13, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Neue Straße", 160, rent, 80, gameUri, board, 14, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Westbahnhof", 200, rent, 100, gameUri, board, 15, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Münchener Straße", 180, rent, 90, gameUri, board, 16, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Gemeinschaftsfeld", 0, rent, 0, gameUri, board, 17, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Wiener Straße", 180, rent, 90, gameUri, board, 18, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Berliner Straße", 200, rent, 100, gameUri, board, 19, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Frei  Parken", 0, rent, 0, gameUri, board, 20, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Theaterstraße", 220, rent, 110, gameUri, board, 21, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Ereignisfeld", 0, rent, 0, gameUri, board, 22, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Museumsstraße", 220, rent, 110, gameUri, board, 23, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Opernplatz", 240, rent, 120, gameUri, board, 24, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Nordbahnhof", 200, rent, 100, gameUri, board, 25, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Lessingstraße", 260, rent, 130, gameUri, board, 26, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Schillerstraße", 260, rent, 130, gameUri, board, 27, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Wasserwerk", 150, rent, 75, gameUri, board, 28, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Goethestraße", 280, rent, 140, gameUri, board, 29, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Gehe ins Gefängnis", 0, rent, 0, gameUri, board, 30, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Rathausplatz", 300, rent, 150, gameUri, board, 31, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Hauptstraße", 300, rent, 150, gameUri, board, 32, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Gemeinschaftsfeld", 0, rent, 0, gameUri, board, 33, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Bahnhofstraße", 320, rent, 160, gameUri, board, 34, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Hauptbahnhof", 200, rent, 100, gameUri, board, 35, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Ereignisfeld", 0, rent, 0, gameUri, board, 36, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Parkstraße", 350, rent, 175, gameUri, board, 37, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Zusatzsteuer", 0, rent, 0, gameUri, board, 38, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }
        try {
            createPlaceAndField("were", "Schlossallee", 400, rent, 200, gameUri, board, 39, board.getId());
        } catch (PlaceAlreadyExistException e) {
            e.printStackTrace();
        }

        // Beispiel für Event broadcasting, diese gut diese

//        PayloadPayload payloadPayload = new PayloadPayload("ajshdkajshd", "ahsldkalksdlkas");
//        EventPayload eventPayload = new EventPayload("",);
//        eventPayload.setPayload(payloadPayload);
//
//        Helper.broadcastEvent(eventPayload);

        return Helper.dataToJson(board);
    }

    public String placePawn(String player, String place, String position, String id) throws Exception {
        if (player.isEmpty() || place.isEmpty() || position.isEmpty()) {
            throw new MissingBodyException();
        }
        if (!boards.containsKey(id)) {
            throw new BoardDoesNotExistException();
        }
        String before = Helper.dataToJson(boards.get(id).getPawns());
        int index = player.lastIndexOf("/") + 1;
        String idPawn = "";
        for (int i = index; i < player.length(); i++) {
            idPawn += player.charAt(i);
        }
        Pawn pawn = new Pawn();
        pawn.setPlayer(player);
        pawn.setPlace(place);
        pawn.setPosition(Integer.parseInt(position));
        pawn.setId(idPawn);
        for (Pawn pawns : boards.get(id).getPawns()) {
            if (pawns.getId().equals(pawn.getId())) {
                throw new PawnAlreadyExistsException();
            }
        }
        boards.get(id).getPawns().add(pawn);
        return Helper.dataToJson("Player successfully created");
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

    public String removeBoard(String gameId) throws Exception {
        if (boards.containsKey(gameId)) {
            boards.remove(gameId);
            return "Game with ID " + gameId + " removed successfully";
        } else {
            throw new BoardDoesNotExistException();
        }
    }

    public String getAllPlayerPositions(String gameId) throws Exception {
        if (boards.containsKey(gameId)) {
            return Helper.dataToJson(boards.get(gameId).getPawns());
        } else {
            throw new BoardDoesNotExistException();
        }
    }

    public String removePawn(String gameId, String pawnId) throws Exception {
        if (boards.containsKey(gameId)) {
            for (Pawn paws : boards.get(gameId).getPawns()) {
                if (paws.getId().equals(pawnId)) {
                    boards.get(gameId).getPawns().remove(boards.get(gameId).getPawns().indexOf(paws));
                    return pawnId + " was sucessfully removed";
                }
            }
            throw new PawnDoesNotExistException();
        } else {
            throw new BoardDoesNotExistException();
        }
    }

    public String getPawn(String gameId, String pawnId) throws Exception {
        if (boards.containsKey(gameId)) {
            for (Pawn paws : boards.get(gameId).getPawns()) {
                if (paws.getId().equals(pawnId)) {
                    Pawn[] pawn = new Pawn[1];
                    pawn[0] = paws;
                    return Helper.dataToJson(pawn);
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
                if (paws.getId().equals(pawnId)) {
                    String before = Helper.dataToJson(paws.getPosition());
                    paws.move(steps);
                    PayloadPayload payloadPayload = new PayloadPayload(before, Helper.dataToJson(paws.getPosition()));
                    EventPayload eventPayload = new EventPayload("Player Pawn moved", gameId, "player_position_changed", "Player has changed his positon", "/boards/" + gameId + "/" + paws.getId(), paws.getId());
                    eventPayload.setPayload(payloadPayload);
                    Helper.broadcastEvent(eventPayload);
                    return Helper.dataToJson("The new position from " + pawnId + " is " + paws.getPosition());
                }
            }
            throw new PawnDoesNotExistException();
        } else {
            throw new BoardDoesNotExistException();
        }
    }

    public String getRolls(String gameId, String pawnId) throws Exception {
        if (boards.containsKey(gameId)) {
            for (Pawn paws : boards.get(gameId).getPawns()) {
                if (paws.getId().equals(pawnId)) {
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
                if (paws.getPlayer().equals(pawnId)) {
                    pawn = paws;
                }
            }
        } else {
            throw new BoardDoesNotExistException();
        }
        String dice = YellowService.getServiceUrlForType(ServiceType.DICE) + "?game=" + gameId + "&player=" + pawnId;
        HttpResponse<JsonNode> jsonRes = Unirest.get(dice).asJson();
        ObjectMapper mapper = new ObjectMapper();
        RollPayload creation = mapper.readValue(jsonRes.getBody().toString(), RollPayload.class);
        movePawn(creation.getNumber(), gameId, pawnId);

        UserPayload userPayload = new UserPayload();
        userPayload.setUserId(pawn.getPlayer());
        String url = YellowService.getServiceUrlForType(ServiceType.BROKER) + "/" + gameId + "/places/" + pawn.getPosition() + "/owner";
        HttpResponse<JsonNode> jsonResponse = Unirest.post(url).body(Helper.dataToJson(userPayload)).asJson();
        JSONObject data = jsonResponse.getBody().getObject();
        if (jsonResponse.getStatus() == 409) {
            url = YellowService.getServiceUrlForType(ServiceType.BROKER) + "/" + gameId + "/places/" + pawn.getPosition() + "/visit";
            jsonResponse = Unirest.post(url).body(Helper.dataToJson(userPayload)).asJson();
            data = jsonResponse.getBody().getObject();
            if (jsonResponse.getStatus() == 402) {
                url = YellowService.getServiceUrlForType(ServiceType.GAME) + "/" + gameId + "/status?status=finished";
                Unirest.put(url).asJson();
                EventPayload eventPayload = new EventPayload("Player is broke", gameId, "player_is_broke", "Player has no money to pay the rent", "/games/" + gameId + "/status", pawn.getId());
                Helper.broadcastEvent(eventPayload);
                EventPayload eventPayloadFinished = new EventPayload("Games is over", gameId, "game_has_finished", "Player is, so the game is over", "/games/" + gameId + "/status", pawn.getId());
                Helper.broadcastEvent(eventPayloadFinished);
                return "Player is Broke and the game is over";
            }
            return Helper.dataToJson(data);
        }

        return pawn.getId() + " bought the place";
    }
}