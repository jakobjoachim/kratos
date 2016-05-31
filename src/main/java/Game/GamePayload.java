package Game;

import Interfaces.Validable;
import lombok.Data;

import java.util.Map;

@Data
public class GamePayload implements Validable{

    private String name;
    private Map<String, String> services;

    public boolean isValid() {
        return name != null && services != null && !name.isEmpty() && !services.isEmpty();
    }
}
