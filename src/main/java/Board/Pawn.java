package Board;

import lombok.Data;

@Data
public class Pawn {
    // description":"uri of the resource itself
    private String id;
    // uri to the player resource
    private String player;
    // uri of the place the player stands on
    private String place;
    // numeric position on the board
    private int position;
    // uri to the rolls of the player
    private String roll;
    // steps to move
    private String move;

    public void move(int fields){
        this.position += fields;
        if(this.position > 39){
            this.position -= 39;
        }
    }


}
