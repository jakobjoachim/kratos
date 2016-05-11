package Board;

import Interfaces.Validable;
import lombok.Data;

@Data
public class FieldPayload implements Validable{
    private String place;
    private String[] players;

    public boolean isValid(){
        return place != null && players != null;
    }
}
