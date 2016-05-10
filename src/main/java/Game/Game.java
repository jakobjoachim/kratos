package Game;

import Enums.GameStatus;
import lombok.Data;

import java.util.Map;

@Data
public class Game {
    private String name;
    private String id;
    private Map<String, Boolean> players;
    private Map<String, String> services;
    private Map<String, String> components;
    private GameStatus status;
}
