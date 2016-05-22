package Broker;

import lombok.Data;

import java.util.ArrayList;
import java.util.Map;

@Data
public class Broker {
    private Map<String, Place> places;
    private String gameId;
}
