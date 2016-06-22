package Broker;

import Interfaces.Validable;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PlacePayload {
    private String type;
    private String description;
    private int buycost;
    private Map<Integer, Integer> rentMap = new HashMap<>();
    private int hypothecarycreditAmount;

}
