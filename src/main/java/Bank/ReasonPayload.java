package Bank;

import Interfaces.Validable;
import lombok.Data;

@Data
public class ReasonPayload implements Validable{
    String reason;


    @Override
    public boolean isValid() {
        return reason != null && !reason.isEmpty();
    }
}
