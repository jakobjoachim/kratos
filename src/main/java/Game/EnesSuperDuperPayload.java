package Game;

import Enums.GameStatus;
import Tools.Mutex;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.*;

@Data
@RequiredArgsConstructor
public class EnesSuperDuperPayload {
    private final String name;
    private final String id;
    private final ArrayList<PlayerPayload> players;
    private final Map<String, String> services;
    private final GameStatus status;
    private final Queue<String> playerQueue;
    private final Mutex playerMutex;
}
