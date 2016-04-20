package Events;

import Interfaces.Validable;
import lombok.Data;

@Data
public class EventPayload implements Validable {
    // the url to the event on the event server
    private String id;

    // human readable name for this event
    private String name;

    // the uri of the game this event belongs to
    private String game;

    // internal type of the event (e.g bank transfer, rent, got to jail, estate transfer)
    private String type;

    // a description why this event occured
    private String reason;

    // the uri of the resource related to this event where more information may be found (e.g. an uri to a transfer orsimilar)
    private String resource;

    // The uri of the player triggering it
    private String player;

    // A timestamp when this event was given to the eventPayloadList service
    private String time;

    @Override
    public boolean isValid() {
        if (this.id != null && this.name != null && this.game != null && this.type != null && this.reason != null) {
            return true;
        }

        return false;
    }
}
