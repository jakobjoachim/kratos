package Dice;

import Enums.ServiceType;
import Events.EventPayload;
import Tools.Helper;
import Yellow.YellowService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static spark.Spark.*;

public class DiceService {

    public static void main(String[] args) {

        before((request, response) -> response.type("application/json"));
        before((request, response) -> response.header("Desciption", "Gives you a single dice roll"));
        get("/dice", (request, response) -> {
            createEvent(request.queryMap().get("player").value(), request.queryMap().get("game").value());
            //TODO: without queryParams
            return randomDice();
        });

    }

    public static String randomDice() {
        int random = (int) (Math.random() * 6) + 1;
        String result = ("{ \"number\": " + random + " }");
        return result;
    }


    public static void createEvent(String player, String game) {

        String eventCreationUrl = YellowService.getServiceUrlForType(ServiceType.EVENTS);
        System.out.println(eventCreationUrl);

        DicePayload dicePayload = new DicePayload();
        dicePayload.setGame(game);
        dicePayload.setPlayer(player);

        try {
            Unirest.post(eventCreationUrl)
                    .header("Content-Type", "application/json")
                    .body(Helper.dataToJson(dicePayload))
                    .asJson();

        } catch (Exception e) {

            System.out.print(e.getMessage());

        }

    }

}
