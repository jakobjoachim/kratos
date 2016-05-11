package Board;

import Interfaces.Validable;

import java.util.List;

import lombok.Data;

@Data
public class BoardPayload implements Validable{
    private String player;
    private int[] positions;
    private Field[] fields;

    public boolean isValid() {
        return player != null && fields != null && positions != null;
    }

}
