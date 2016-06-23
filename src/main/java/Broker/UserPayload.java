package Broker;

import Interfaces.Validable;
import lombok.Data;

@Data
public class UserPayload implements Validable{

    private String userId;

    @Override
    public boolean isValid() {
        return !userId.isEmpty();
    }
}
