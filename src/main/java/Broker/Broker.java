package Broker;

import lombok.Data;

import java.util.Map;

@Data
public class Broker {
    private Map<String, String> placeMap;
    private String gameId;
}
