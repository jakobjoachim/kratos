package Game;

import Interfaces.Validable;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
public class PlayerPayload implements Validable {
    private  boolean ready;
    private String name;

    PlayerPayload(){
    }

    PlayerPayload(boolean rdy, String nme) {
        ready = rdy;
        name = nme;
    }

    public boolean isValid() {
        return name != null && !name.isEmpty();
    }
}