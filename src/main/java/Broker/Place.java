package Broker;

import lombok.Data;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

@Data
class Place {
    @NonNull String description;
    @NonNull String type;
    private int numberHouses;
    private String owner;
    private int buycost;
    private Map<Integer, Integer> rentMap = new HashMap<>();
    private int hypothecarycreditAmount;
    private boolean hypothecarycredit;

    Place (String theDescription, String theType, int theBuyCost, Map<Integer, Integer> theRentMap, int thehypoAmount){
        description = theDescription;
        type = theType;
        owner = null;
        hypothecarycredit = false;
        hypothecarycreditAmount = thehypoAmount;
        rentMap = theRentMap;
        buycost = theBuyCost;
        numberHouses = 0;
    }
}
