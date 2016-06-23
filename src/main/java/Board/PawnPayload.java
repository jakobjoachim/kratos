package Board;

import lombok.Data;

@Data
public class PawnPayload {
    private String position;
    private String player;
    private String place;
    private String move;


    public boolean isValid(){
        return player != null && place != null && position != null;
    }
}
