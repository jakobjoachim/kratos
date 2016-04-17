package Dice;

import static spark.Spark.*;

public class DiceService {
    public static void main(String[] args) {
        before((request, response) -> response.type("application/json"));
        before((request, response) -> response.header("Desciption", "Gives you a single dice roll"));
        get("/dice", (request, response) -> {
            System.out.println(request.queryMap().get("player").value());
            System.out.println(request.queryMap().get("game").value());
            return randomDice();
        });
    }

    public static String randomDice() {
        int random = (int)(Math.random() * 6) +1;
        String result = ("{ \"number\": " + random +" }");
        return result;
    }

}
