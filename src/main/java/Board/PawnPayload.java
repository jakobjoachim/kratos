package Board;

import lombok.Data;
import Interfaces.Validable;


@Data
public class PawnPayload implements Validable{
    //uri of the resource itself
    private String id;
    //uri to the player resource
    private String player;
    //uri of the place the player stands on
    private String place;
    //numeric position on the board
    private int position;
    //uri to the rolls of the player
    private String roll;
    //uri to the moves or the player
    private String move;

    public boolean isValid(){
        return id != null && player != null && place != null && position != 0 && roll != null && move != null;
    }
}









