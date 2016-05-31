package Bank;

import Interfaces.Validable;
import lombok.Data;



@Data
public class BankAccount implements Validable {

    // the url to the bank account on the bank service
    private String id;

    // the balance on this bank account
    private int balance;

    // the player ID for this bank account
    private String userId;

    @Override
    public boolean isValid() {
        return  (this.id != null && this.balance >= 0 && userId != null);
    }
}
