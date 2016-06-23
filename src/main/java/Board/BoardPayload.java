package Board;

import lombok.Data;
import Interfaces.Validable;
import java.util.List;

@Data
public class BoardPayload {
    public String gameUri;

    public boolean isValid() {
        return gameUri != null && !gameUri.isEmpty();
    }
}
