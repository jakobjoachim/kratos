package Broker;

import Interfaces.Validable;
import lombok.Data;

@Data
class UserPayload implements Validable{

    private String userId;

    @Override
    public boolean isValid() {
        return !userId.isEmpty();
    }
}
