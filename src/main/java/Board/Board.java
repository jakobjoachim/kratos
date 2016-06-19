package Board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Board {
    // Uri to the list of players on the board
    @NonNull private String id;
    // Array including active Pawns
    private List<Pawn> pawns = new ArrayList<>();
    // String UriPlace Integer PawnId
    private Map<Integer, String> placeUri = new HashMap<>();
}
