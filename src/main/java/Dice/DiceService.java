package Dice;

import Enums.ServiceType;
import Exceptions.BadDicePayloadException;
import Tools.Helper;
import Tools.YellowService;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import static spark.Spark.*;

public class DiceService {

    public static void main(String[] args) {

        before((request, response) -> response.header("Desciption", "Gives you a single dice roll"));

        get("/dice", (request, response) -> {

            if (request.queryMap().get("player").value() == null
                    || request.queryMap().get("game").value() == null) {

                throw new BadDicePayloadException();
            }

            createEvent(request.queryMap().get("player").value(), request.queryMap().get("game").value());

            response.header("Content-Type", "application/json");
            return randomDice();
        });

        exception(BadDicePayloadException.class, (exception, request, response) -> {
            response.status(422);
            response.header("Content-Type", "application/json");
            response.body("{ \"error\": \"game and/or player missing\", \"status\": 422 }");
        });

    }

    private static String randomDice() {
        int random = (int) (Math.random() * 6) + 1;
        return ("{ \"number\": " + random + " }");
    }


    private static void createEvent(String player, String game) {

        String eventCreationUrl = YellowService.getServiceUrlForType(ServiceType.EVENTS);
        System.out.println(eventCreationUrl);

        DicePayload dicePayload = new DicePayload();
        dicePayload.setGame(game);
        dicePayload.setPlayer(player);

        try {

            URL url = new URL(eventCreationUrl);
            Unirest.post(url.toString())
                    .header("Content-Type", "application/json")
                    .body(Helper.dataToJson(dicePayload))
                    .asString();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
