package User;

import Interfaces.Validable;
import lombok.Data;

@Data
class UserPayload implements Validable {
    private String name;
    private String uri;

    public boolean isValid() {
        return name != null && uri != null && !name.isEmpty() && !uri.isEmpty();
    }
}
