package Board;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Board {
    // Uri to the list of players on the board
    @NonNull private String id;
    // List of Pawn positions
    private Map<Pawn ,Integer> pawnPositions = new HashMap<>();
    // String UriPlace Integer PawnId
    private Map<Integer, String> placeUri = new HashMap<>();
}
