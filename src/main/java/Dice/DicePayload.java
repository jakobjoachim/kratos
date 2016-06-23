package Dice;

import Interfaces.Validable;
import lombok.Data;

@Data
class DicePayload implements Validable{

    private String reason = "dice roll occured";
    private String type = "dice_roll";
    private String name = "dice roll";
    private String game;
    private String player;

    @Override
    public boolean isValid() {
        return game != null && player != null && !game.isEmpty() && !player.isEmpty();
    }
}
