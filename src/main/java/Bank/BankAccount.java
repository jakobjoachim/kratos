package Bank;

import Exceptions.PlayerIsBrokeException;
import Interfaces.Validable;
import lombok.Data;



@Data
public class BankAccount implements Validable {

    // the balance on this bank account
    private int balance;

    // the player Uri for this bank account
    private String player;

    @Override
    public boolean isValid() {
        return  (this.balance >= 0 && player != null);
    }

    public void addMoney(int amount) {
        this.balance += amount;
    }

    public void subMoney(int amount) throws Exception {
        if (this.balance >= amount) {
            this.balance -= amount;
        } else {
            throw new PlayerIsBrokeException();
        }
    }
}
