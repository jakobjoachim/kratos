package Bank;


import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

public class BankManager {

    private List<BankAccount> bankAccountlist;

    BankManager() {

        this.bankAccountlist = new ArrayList<>();
    }

    String createNewBankAccount(String gameID) throws InputMismatchException {
        //TODO: Request für playerID

        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(gameID);
        bankAccount.setBalance(0);

        this.bankAccountlist.add(bankAccount);

        if (!(bankAccount.isValid())) {
            throw new InputMismatchException();
        }
        return Tools.Helper.dataToJson(bankAccount);
    }

    String getBankAccountBalance(String gameID, String playerID) {

        for (BankAccount bankaccount : bankAccountlist) {
            if (bankaccount.getGameID().equals(gameID) && bankaccount.getUserID().equals(playerID)) {
                return Tools.Helper.dataToJson(bankaccount.getBalance());
            }
        }

        return "";

    }



    String bankToPlayerTransfer(String gameID, String playerID, String amount) {

        for (BankAccount bankaccount:bankAccountlist) {
            if (bankaccount.getGameID().equals(gameID) && bankaccount.getUserID().equals(playerID)) {
                bankaccount.setBalance(bankaccount.getBalance() + Integer.parseInt(amount));
            }
        }

        return "";
    }

}
