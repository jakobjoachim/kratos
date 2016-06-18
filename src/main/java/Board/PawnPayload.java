package Board;

import lombok.Data;

@Data
public class PawnPayload {
    private String gameId;
    private String pawnId;
    private String player;
    private String place;
    private String position;
    private String move;

    public boolean isValid(){
        return gameId != null && pawnId != null && player != null;
    }
}
