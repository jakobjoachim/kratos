package Bank;

import Enums.ServiceType;
import Exceptions.BankAlreadyExistsException;
import Exceptions.BankDoesNotExistException;
import Exceptions.TransferDoesNotExistException;
import Exceptions.UserDoesNotExistException;
import Tools.Helper;
import Tools.YellowService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import java.util.*;

public class BankManager {

    //HashMap mit BankUri und der Bank
    private static Map<String,Bank> bankMap = new HashMap<>();

    //Gibt alle Banken wieder
    Set<String> getAllBanks(){
        return bankMap.keySet();
    }

    //Erstellt eine neue Bank
    String createNewBank(String bankId) throws BankAlreadyExistsException {
        Bank bank = new Bank();
        String url = ("\"/banks/" + bankId + "\"");
        if (!(bankMap.containsKey(url))) {
            bank.setId(bankId);
            bankMap.put(url,bank);
        } else {
            throw new BankAlreadyExistsException();
        }
        return url;
    }

    //Alle Transfers
    List<Transfer> getAllTransfers(String bankId) throws BankDoesNotExistException {
        String searching = ("\"/banks/" + bankId + "\"");
        if (bankMap.containsKey(searching)) {
            return bankMap.get(searching).getTransfers();
        } else {
            throw new BankDoesNotExistException();
        }
    }

    //Gibt einen Transfer wieder
    String getTransfer(String bankId, String tranferId) throws BankDoesNotExistException{
        String searching = ("\"/banks/" + bankId + "\"");
        if (bankMap.containsKey(searching)) {

            for (Transfer transfer : bankMap.get(searching).getTransfers()) {
                if (transfer.getTransferId().equals(tranferId)) {
                    return Tools.Helper.dataToJson(transfer);
                }
            }
        } else {
            throw new BankDoesNotExistException();
        }
        return "";
    }

//    //Erstellt ein neues Bankkonto
//    String createNewBankAccounts(String bankId, String gameId) throws BankDoesNotExistException {
//
//        String searching = ("\"/banks/" + bankId+ "\"");
//        String gameURL = YellowService.getServiceUrlForType(ServiceType.GAME);
//
//        try {
//            HttpResponse<JsonNode> jsonResponse = Unirest.get(gameURL)
//                    .header("Content-Type", "application/json")
//                    .asJson();
//
//            if (bankMap.containsKey(searching)) {
//                //TODO
//                for (String playerId: jsonResponse.getBody()) {
//                    BankAccount bankAccount = new BankAccount();
//                    // TODO bankAccount.setId
//                    bankAccount.setGameId(gameId);
//                    bankAccount.setBalance(0);
//                    bankAccount.setUserId(playerId);
//                    bankMap.get(searching).getAccounts().add(bankAccount);
//
//                    if (!(bankAccount.isValid())) {
//                        throw new BankDoesNotExistException();
//                    }
//                }
//            }
//        } catch (Exception e) {
//            System.out.print(e.getMessage());
//        }
//        return "";
//    }

    //Gibt das Saldo einen Kontos wieder
    String getBankAccountBalance(String bankId, String gameId, String playerId) throws BankDoesNotExistException {
        String searching = ("\"/banks/" + bankId + "\"");
        if (bankMap.containsKey(searching)) {
            for (BankAccount bankaccount : bankMap.get(searching).getAccounts()) {
                if (bankaccount.getUserId().equals(playerId)) {
                    return Tools.Helper.dataToJson(bankaccount.getBalance());
                }
            }
        } else {
            throw new BankDoesNotExistException();
        }
        return "";
    }

    //Transfer von der Bank zu einem Spieler
    String bankToPlayerTransfer(String bankId, String playerId, String amount) throws BankDoesNotExistException {
        String searching = ("\"/banks/" + bankId + "\"");
        if (bankMap.containsKey(searching)) {
            for (BankAccount bankaccount: bankMap.get(searching).getAccounts()) {
                if (bankaccount.getUserId().equals(playerId)) {
                    bankaccount.setBalance(bankaccount.getBalance() + Integer.parseInt(amount));
                }
            }
        } else {
            throw new BankDoesNotExistException();
        }
        return "";
    }

    //Transfer von dem Spieler zur Bank
    String playerToBankTransfer(String bankId, String playerId, String amount) throws BankDoesNotExistException {
        String searching = ("\"/banks/" + bankId + "\"");
        if (bankMap.containsKey(searching)) {
            for (BankAccount bankaccount: bankMap.get(searching).getAccounts()) {
                if (bankaccount.getUserId().equals(playerId)) {
                    bankaccount.setBalance(bankaccount.getBalance() - Integer.parseInt(amount));
                }
            }
        } else {
            throw new BankDoesNotExistException();
        }
        return "";
    }

    //Transfer von einem zum anderen Spieler
    String playerToPlayerTransfer(String bankId, String fromId, String toId, String amount, String transaction) throws BankDoesNotExistException {
        String searching = ("\"/banks/" + bankId + "\"");
        Transfer transfer = new Transfer();
        transfer.setAmount(Integer.parseInt(amount));
        if (bankMap.containsKey(searching)) {
            for (BankAccount bankaccount: bankMap.get(searching).getAccounts()) {
                    if (bankaccount.getUserId().equals(fromId)) {
                        bankaccount.setBalance(bankaccount.getBalance() - Integer.parseInt(amount));
                        transfer.setFrom(fromId);
                    }
                    if (bankaccount.getUserId().equals(toId)) {
                        bankaccount.setBalance(bankaccount.getBalance() + Integer.parseInt(amount));
                        transfer.setTo(toId);
                    }
            }
            bankMap.get(searching).getTransfers().add(transfer);

        } else {
            throw new BankDoesNotExistException();
        }
        return "";
    }

    String getTransactionStatus(String bankId, String transactionId) throws BankDoesNotExistException{
        String searching = ("\"/banks/" + bankId + "\"");
        if (bankMap.containsKey(searching)) {
            return "";
        } else {
            throw new BankDoesNotExistException();
        }
    }

    List<BankAccount> getAllAccounts(String bankId) throws BankDoesNotExistException{
        String searching = ("\"/banks/" + bankId + "\"");
        if (bankMap.containsKey(searching)) {
            return bankMap.get(searching).getAccounts();
        } else {
            throw new BankDoesNotExistException();
        }
    }

    String createNewAccount(String bankId, HashMap<String,String> accountinfo) throws BankDoesNotExistException{
        String searching = ("\"/banks/" + bankId + "\"");
        if (bankMap.containsKey(searching)) {
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(Integer.parseInt(accountinfo.get("saldo")));
            bankAccount.setUserId(accountinfo.get("player"));
            bankAccount.setId("\"/banks/" + bankId + "/" + bankAccount.getUserId() + "\"");
        } else {
            throw new BankDoesNotExistException();
        }
        return "";
    }
}
