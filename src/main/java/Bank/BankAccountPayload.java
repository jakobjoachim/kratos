package Bank;

import Interfaces.Validable;
import lombok.Data;

@Data
public class BankAccountPayload implements Validable{
    private String id;
    private int balance;
    private String player;

    @Override
    public boolean isValid() {
        return id != null && !id.isEmpty() && balance >= 0 && player != null && !player.isEmpty();
    }


}
