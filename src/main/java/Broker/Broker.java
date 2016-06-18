package Broker;

import lombok.Data;

import java.util.Map;

@Data
class Broker {
    private Map<String, Place> places;
    private String gameId;
}
