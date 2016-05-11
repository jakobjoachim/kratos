package Dice;

import lombok.Data;

@Data
public class DicePayload {

    private String reason = "dice roll occured";
    private String type = "dice roll";
    private String name = "dice roll";
    private String game;
    private String player;
}
