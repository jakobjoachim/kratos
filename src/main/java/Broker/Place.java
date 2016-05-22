package Broker;

import lombok.Data;

@Data
public class Place {
    private String description;
    private String type;
    private String owner;
    private boolean hypothecarycredit;

    public Place (String theDescription, String theType){
        description = theDescription;
        type = theType;
        owner = null;
        hypothecarycredit = false;
    }
}
