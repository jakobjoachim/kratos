package Bank;

import Enums.ServiceType;
import Enums.TransactionPhase;
import Enums.TransactionStatus;
import Exceptions.*;
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

    //Transfer von einem zum anderen Spieler
    String playerToPlayerTransfer(String bankId, String fromId, String toId, String amount, String searchingTransaction) throws Exception {
        String searching = ("\"/banks/" + bankId + "\"");
        Transfer transfer = new Transfer();
        transfer.setAmount(Integer.parseInt(amount));
        if (bankMap.containsKey(searching)) {
            for (Map.Entry<String, BankAccount> entry: bankMap.get(searching).getAccounts().entrySet()) {
                if (entry.getValue().getPlayerId().equals(fromId)) {
                    String fromAccount = entry.getKey();
                    transfer.setFrom(fromAccount);
                } else {
                    throw new BankAccountDoesNotExistException();
                }
                if (entry.getValue().getPlayerId().equals(toId)) {
                    String toAccount = entry.getKey();
                    transfer.setTo(toAccount);
                } else {
                    throw new BankAccountDoesNotExistException();
                }
            }
            bankMap.get(searching).getTransactionMap().get(searchingTransaction).setTransferInTransaction(transfer);
            int oldBalanceFrom = bankMap.get(searching).getAccounts().get(transfer.getFrom()).getBalance();
            bankMap.get(searching).getAccounts().get(transfer.getFrom()).setBalance(oldBalanceFrom - Integer.parseInt(amount));
            int oldBalanceTo = bankMap.get(searching).getAccounts().get(transfer.getTo()).getBalance();
            bankMap.get(searching).getAccounts().get(transfer.getTo()).setBalance(oldBalanceTo + Integer.parseInt(amount));
            bankMap.get(searching).getTransfers().add(transfer);
        } else {
            throw new BankDoesNotExistException();
        }
        return "";
    }

    //Transfer von der Bank zu einem Spieler
    String bankToPlayerTransfer(String bankId, String playerId, String amount, String searchingTransaction) throws BankDoesNotExistException {
        String searching = ("\"/banks/" + bankId + "\"");
        Transfer transfer = new Transfer();
        if (bankMap.containsKey(searching)) {
            for (Map.Entry<String, BankAccount> entry: bankMap.get(searching).getAccounts().entrySet()) {
                if (entry.getValue().getPlayerId().equals(playerId)) {
                    String toAccount = entry.getKey();
                    transfer.setFrom(searching);
                    transfer.setTo(toAccount);
                    transfer.setAmount(Integer.parseInt(amount));
                    bankMap.get(searching).getTransactionMap().get(searchingTransaction).setTransferInTransaction(transfer);
                    entry.getValue().setBalance(entry.getValue().getBalance() + Integer.parseInt(amount));
                    bankMap.get(searching).getTransfers().add(transfer);
                }
            }
        } else {
            throw new BankDoesNotExistException();
        }
        return "";
    }

    //Transfer von dem Spieler zur Bank
    String playerToBankTransfer(String bankId, String playerId, String amount, String searchingTransaction) throws BankDoesNotExistException {
        String searching = ("\"/banks/" + bankId + "\"");
        Transfer transfer = new Transfer();
        if (bankMap.containsKey(searching)) {
            for (Map.Entry<String,BankAccount> entry: bankMap.get(searching).getAccounts().entrySet()) {
                if (entry.getValue().getPlayerId().equals(playerId)) {
                    String fromAccount = entry.getKey();

                    transfer.setFrom(fromAccount);
                    transfer.setTo(searching);
                    transfer.setAmount(Integer.parseInt(amount));
                    bankMap.get(searching).getTransactionMap().get(searchingTransaction).setTransferInTransaction(transfer);
                    entry.getValue().setBalance(entry.getValue().getBalance() - Integer.parseInt(amount));
                    bankMap.get(searching).getTransfers().add(transfer);
                }
            }
        } else {
            throw new BankDoesNotExistException();
        }
        return "";
    }

    //Beginn einer Transaktion
    String beginOfTransaction(String bankId, TransactionPhase phase) throws BankDoesNotExistException{
        String searching = ("\"/banks/" + bankId + "\"");
        if (bankMap.containsKey(searching)) {
            Transaction transaction = new Transaction();
            transaction.setPhases(phase);
            String id = Tools.Helper.nextId();
            String transactionuri = ("\"/transaction/" + id + "\"");
            bankMap.get(searching).getTransactionMap().put(transactionuri,transaction);
        } else {
            throw new BankDoesNotExistException();
        }
        return Tools.Helper.dataToJson(bankMap.get(searching).getTransactionMap());
    }

    //Gibt den Transaktionsstatus wieder
    String getTransactionStatus(String bankId, String transactionId) throws Exception{
        String searching = ("\"/banks/" + bankId + "\"");
        String searchingTransaction = ("\"/transaction/" + transactionId + "\"");
        if (bankMap.containsKey(searching)) {
            if (bankMap.get(searching).getTransactionMap().containsKey(searchingTransaction)) {
                return Tools.Helper.dataToJson(bankMap.get(searching).getTransactionMap().get(searchingTransaction).getStatus());
            } else {
                throw new TransactionDoesNotExistException();
            }
        } else {
            throw new BankDoesNotExistException();
        }
    }

    //Commit einer Transaktion
    String commitTransaction(String bankId, String transactionId, TransactionStatus state) throws Exception{
        String searching = ("\"/banks/" + bankId + "\"");
        String searchingTransaction = ("\"/transaction/" + transactionId + "\"");
        if (bankMap.containsKey(searching)) {
            if (bankMap.get(searching).getTransactionMap().containsKey(searchingTransaction)) {
                bankMap.get(searching).getTransactionMap().get(searchingTransaction).setStatus(state);
                return Tools.Helper.dataToJson(bankMap.get(searching).getTransactionMap().get(searchingTransaction));
            } else {
                throw new TransactionDoesNotExistException();
            }
        } else {
            throw new BankDoesNotExistException();
        }
    }

    //Rollback einer Transaktion
    String rollbackTransaction(String bankId, String transactionId) throws Exception{
        String searching = ("\"/banks/" + bankId + "\"");
        String searchingTransaction = ("\"/transaction/" + transactionId + "\"");
        if (bankMap.containsKey(searching)) {
            if (bankMap.get(searching).getTransactionMap().containsKey(searchingTransaction)) {
                bankMap.get(searching).getTransactionMap().remove(searchingTransaction);
            } else {
                throw new TransactionDoesNotExistException();
            }
        } else {
            throw new BankDoesNotExistException();
        }
        return "";
    }

    //Gibt alle Konten wieder
    String getAllAccounts(String bankId) throws BankDoesNotExistException{
        String searching = ("\"/banks/" + bankId + "\"");
        if (bankMap.containsKey(searching)) {
            return Tools.Helper.dataToJson(bankMap.get(searching).getAccounts());
        } else {
            throw new BankDoesNotExistException();
        }
    }

    //Erstellung eines neuen Bankkontos
    String createNewAccount(String bankId, HashMap<String,String> accountinfo) throws BankDoesNotExistException{
        String searching = ("\"/banks/" + bankId + "\"");
        if (bankMap.containsKey(searching)) {
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(Integer.parseInt(accountinfo.get("saldo")));
            bankAccount.setPlayerId(accountinfo.get("player"));
            String id = Tools.Helper.nextId();
            String accountUri = ("\"/accounts/" + id + "\"");
            bankAccount.setId(id);
            bankMap.get(searching).getAccounts().put(accountUri,bankAccount);
        } else {
            throw new BankDoesNotExistException();
        }
        return "";
    }

    //Gibt das Saldo einen Kontos wieder
    String getBankAccountBalance(String bankId, String accountId) throws BankDoesNotExistException {
        String searching = ("\"/banks/" + bankId + "\"");
        String accountUri = ("\"/accounts/" + accountId + "\"");
        if (bankMap.containsKey(searching)) {
            if (bankMap.get(searching).getAccounts().containsKey(accountUri)) {
               return Tools.Helper.dataToJson(bankMap.get(searching).getAccounts().get(accountUri).getBalance());
            }
        } else {
            throw new BankDoesNotExistException();
        }
        return "";
    }
}
