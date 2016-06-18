package Board;

import lombok.Data;

@Data
public class PawnPayload {
    private String position;
    private String player;
    private String place;


    public boolean isValid(){
        return player != null && place != null && position != null;
    }
}
