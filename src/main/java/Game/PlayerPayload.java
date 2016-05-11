package Game;

import Interfaces.Validable;
import lombok.Data;

@Data
public class PlayerPayload implements Validable {
    private boolean ready;
    private String name;

    public boolean isValid() {
        return name != null && !name.isEmpty();
    }
}