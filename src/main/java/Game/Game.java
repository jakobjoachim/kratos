package Game;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Game {
    private String name;
    private String id;
    private List<String> players;
    private Map<String, String> services;
    private List<String> components;
}