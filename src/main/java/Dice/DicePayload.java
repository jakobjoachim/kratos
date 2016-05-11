package Dice;

import lombok.Data;

@Data
public class DicePayload {

    static final String reason = "dice roll occured";
    static final String type = "dice roll";
    static final String name = "dice roll";
    private String game;
    private String player;
}
