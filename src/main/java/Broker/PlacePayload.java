package Broker;

import Interfaces.Validable;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
class PlacePayload implements Validable{
    private String type;
    private String description;
    private int buycost;
    private Map<Integer, Integer> rentMap = new HashMap<>();
    private int hypothecarycreditAmount;

    @Override
    public boolean isValid() {
        return type != null && !type.isEmpty() && description != null && !description.isEmpty();
    }
}
