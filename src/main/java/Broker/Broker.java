package Broker;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
class Broker {
    private Map<String, Place> places = new HashMap<>();
    private String gameId;
}
