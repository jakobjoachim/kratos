package Game;

import Enums.GameStatus;
import Tools.Mutex;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

@Data
public class Game {
    private String name;
    private String id;
    private Map<String, Boolean> players = new HashMap<>();
    private Map<String, String> services = new HashMap<>();
    private Map<String, String> components = new HashMap<>();
    private GameStatus status;
    private Queue<String> playerQueue;
    private Mutex playerMutex;
}
