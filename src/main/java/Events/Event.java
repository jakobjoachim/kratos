package Events;

import Interfaces.Validable;
import Tools.SharedPayloads.PayloadPayload;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Data
public class Event implements Validable {
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

    private boolean submitted;

    private Map<String, String> payload;

    // A timestamp when this event was given to the eventPayloadList service
    private String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

    @Override
    public boolean isValid() {
        if (this.name != null && this.game != null && this.type != null && this.reason != null) {
            return true;
        }

        return false;
    }
}
