package Game;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by jakob on 09/05/16.
 */
public class GameModel {
    private static Map<String, Game> gameMap = new HashMap<>();

    public Set<String> getAllGames(){
        return gameMap.keySet();
    }
}
