package Bank;


import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

public class BankManager {

    private List<BankAccount> bankAccountlist;

    BankManager() {

        this.bankAccountlist = new ArrayList<>();
    }

    String createNewBankAccount(String gameID, List<String> playerIDs) throws InputMismatchException {

        for (String playerID: playerIDs) {
            BankAccount bankAccount = new BankAccount();
            bankAccount.setId(gameID);
            bankAccount.setBalance(0);
            bankAccount.setUserID(playerID);

            this.bankAccountlist.add(bankAccount);

            if (!(bankAccount.isValid())) {
                throw new InputMismatchException();
            }
        }

        return "";
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

    String playerToBankTransfer(String gameID, String playerID, String amount) {
        for (BankAccount bankaccount:bankAccountlist) {
            if (bankaccount.getGameID().equals(gameID) && bankaccount.getUserID().equals(playerID)) {
                bankaccount.setBalance(bankaccount.getBalance() - Integer.parseInt(amount));
            }
        }

        return "";
    }

    String playerToPlayerTransfer(String gameID, String fromID, String toID, String amount) {
        for (BankAccount bankaccount:bankAccountlist) {
            if (bankaccount.getGameID().equals(gameID)) {
                if (bankaccount.getUserID().equals(fromID)) {
                    bankaccount.setBalance(bankaccount.getBalance() - Integer.parseInt(amount));
                }
                if (bankaccount.getUserID().equals(toID)) {
                    bankaccount.setBalance(bankaccount.getBalance() + Integer.parseInt(amount));
                }
            }
        }

        return "";
    }


}
