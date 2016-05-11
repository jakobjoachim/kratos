package Dice;

import Events.EventPayload;
import Tools.Helper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static spark.Spark.*;

public class DiceService {


    static final String eventCreationUrl = "http://172.18.0.44:4567/events";



    public static void main(String[] args) {
        before((request, response) -> response.type("application/json"));
        before((request, response) -> response.header("Desciption", "Gives you a single dice roll"));
        get("/dice", (request, response) -> {
            System.out.println("Gonna create event");
            createEvent(request.queryMap().get("player").value(), request.queryMap().get("game").value());

            return randomDice();
        });


    }

    public static String randomDice() {
        int random = (int)(Math.random() * 6) +1;
        String result = ("{ \"number\": " + random +" }");
        return result;
    }


    public static void createEvent(String player, String game) {
        DicePayload payload = new DicePayload();
        payload.setPlayer(player);
        payload.setGame(game);
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post(eventCreationUrl)
                    .header("accept", "application/json")
                    .body(Helper.dataToJson(payload))
                    .asJson();
        }
        catch (Exception e) {

            System.out.print(e.getMessage());

        }

    }

}
