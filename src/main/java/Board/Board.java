package Board;

import lombok.Data;
import java.util.List;

@Data
public class Board {
    private String player;
    private int[] positions;
    private List<Field> fields;
}
