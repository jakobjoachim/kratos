package Dice;

import Enums.ServiceType;
import Exceptions.BadDicePayloadException;
import Tools.Helper;
import Tools.JsonErrorGenerator;
import Tools.SharedPayloads.EventPayload;
import Tools.YellowService;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import static spark.Spark.*;

public class DiceService {

    public static void main(String[] args) {

        before((request, response) -> response.header("Description", "Gives you a single dice roll"));

        get("/", (request, response) -> {
            response.status(200);
            return "OK";
        });

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
            response.body(JsonErrorGenerator.getErrorJsonString(422, "game and/or player missing"));
        });

    }

    private static String randomDice() {
        int random = (int) (Math.random() * 6) + 1;
        return ("{ \"number\": " + random + " }");
    }


    private static void createEvent(String player, String game) {
        EventPayload eventPayload =
                new EventPayload("dice roll", game, "dice roll", "dice roll occured", "", player);
        Helper.broadcastEvent(eventPayload);
    }

}
