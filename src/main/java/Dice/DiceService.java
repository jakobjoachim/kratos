package Dice;

import Events.EventPayload;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static spark.Spark.*;

public class DiceService {

    static final String reason = "dice roll occured";
    static final String type = "dice roll";
    static final String name = "dice roll";
    static final String eventCreationUrl = "http://172.18.0.42:4567/events";



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

        try {
            System.out.println("Trying: create event");
            URL url = new URL(eventCreationUrl);
            System.out.println("Done: create URL");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            System.out.println("Done: openConnection");
            httpCon.setRequestMethod("POST");
            System.out.println("Done: setRequestmethod");
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream()); //TODO
            System.out.println("Done: new Outputstream");
            EventPayload send = new EventPayload();
            System.out.println("Done: OutputStream created");
            send.setPlayer(player);
            send.setGame(game);
            send.setType(type);
            send.setName(name);
            send.setReason(reason);
            String ukuku = Tools.Helper.dataToJson(send);
            System.out.print(ukuku);
            out.write(Tools.Helper.dataToJson(send));
        }
        catch (Exception e) {

            System.out.print(e.getMessage());

        }

    }

}
