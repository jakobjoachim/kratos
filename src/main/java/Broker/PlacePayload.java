package Broker;

import Interfaces.Validable;
import lombok.Data;

@Data
public class PlacePayload implements Validable{
    private String type;
    private String description;

    @Override
    public boolean isValid() {
        return type != null && !type.isEmpty() && description != null && !description.isEmpty();
    }
}
