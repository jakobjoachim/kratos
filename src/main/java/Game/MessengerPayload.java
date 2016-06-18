package Game;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MessengerPayload {
    private final String playerId;
    private final String gameId;
}
