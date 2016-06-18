package Broker;

import lombok.Data;

import java.util.Map;

@Data
class Place {
    private String description;
    private String type;
    private String owner;
    private int buycost;
    private Map<Integer, Integer> rentMap;
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
    }
}
