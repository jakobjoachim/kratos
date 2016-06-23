package Board;

import lombok.Data;

@Data
public class PlaceUriPayload {
    private String placeUri;

    public boolean isValid() {
        return placeUri != null && !placeUri.isEmpty();
    }
}
