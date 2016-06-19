package Tools.SharedPayloads;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EventPayload {

    // human readable name for this event
    private final String name;

    // the uri of the game this event belongs to
    private final String game;

    // internal type of the event (e.g bank transfer, rent, got to jail, estate transfer)
    private final String type;

    // a description why this event occured
    private final String reason;

    // the uri of the resource related to this event where more information may be found (e.g. an uri to a transfer orsimilar)
    private final String resource;

    // The uri of the player triggering it
    private final String player;

    private PayloadPayload payload;

}
