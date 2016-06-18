package Bank;


import Enums.TransactionPhase;
import Enums.TransactionStatus;
import Exceptions.*;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.*;

public class BankManager {

    //HashMap mit BankUri und der Bank
    private static Map<String, Bank> bankMap = new HashMap<>();

    //Gibt alle Banken wieder
    Set<String> getAllBanks() {
        return bankMap.keySet();
    }

    private Transaction getTransaction(String bankId, String transactionId) {
        String url = ("/banks/" + bankId);
        return bankMap.get(url).getTransactionMap().get(transactionId);
    }

    //Erstellt eine neue Bank
    String createNewBank(String bankId) throws BankAlreadyExistsException {
        Bank bank = new Bank();
        String url = ("/banks/" + bankId);
        if (!(bankMap.containsKey(url))) {
            bank.setId(bankId);
            bankMap.put(url, bank);
        } else {
            throw new BankAlreadyExistsException();
        }
        return url;
    }

    //Alle Transfers
    List<MoneyTransfer> getAllTransfers(String bankId) throws BankDoesNotExistException {
        String searching = ("/banks/" + bankId);
        if (bankMap.containsKey(searching)) {
            return bankMap.get(searching).getTransfers();
        } else {
            throw new BankDoesNotExistException();
        }
    }

    //Gibt einen MoneyTransfer wieder
    String getTransfer(String bankId, String tranferId) throws BankDoesNotExistException {
        String searching = ("/banks/" + bankId);
        if (bankMap.containsKey(searching)) {

            for (MoneyTransfer moneyTransfer : bankMap.get(searching).getTransfers()) {
                if (moneyTransfer.getTransferId().equals(tranferId)) {
                    return Tools.Helper.dataToJson(moneyTransfer);
                }
            }
        } else {
            throw new BankDoesNotExistException();
        }
        return "";
    }
    //Transfer von einem zum anderen Spieler mit Transaction
    String playerToPlayerTransfer(String bankId, String fromId, String toId, String amount, String searchingTransaction, String reason) throws Exception {
        String searching = ("banks/" + bankId);
        String fromUri = ("accounts/" + fromId);
        String toUri = ("accounts/" + toId);
        if (bankMap.containsKey(searching)) {
            if (bankMap.get(searching).getAccounts().containsKey(fromUri) && bankMap.get(searching).getAccounts().containsKey(toUri)) {
                int money = Integer.parseInt(amount);
                MoneyTransfer transfer = new MoneyTransfer(fromUri, toUri, money);
                transfer.setReason(reason);
                if (searchingTransaction.equals("withoutTransaction")) {
                    bankMap.get(searching).getAccounts().get(transfer.getFrom()).subMoney(money);
                    bankMap.get(searching).getAccounts().get(transfer.getTo()).addMoney(money);
                    bankMap.get(searching).getTransfers().add(transfer);
                } else if (getTransaction(searching, searchingTransaction).getStatus() == TransactionStatus.ready) {
                    getTransaction(searching, searchingTransaction).getTransferInTransaction().add(transfer);
                    bankMap.get(searching).getAccounts().get(transfer.getFrom()).subMoney(money);
                    getTransaction(searching, searchingTransaction).setPhases(TransactionPhase.Zwei);
                    bankMap.get(searching).getAccounts().get(transfer.getTo()).addMoney(money);
                    bankMap.get(searching).getTransfers().add(transfer);
                } else {
                    throw new BankAccountDoesNotExistException();
                }
                return Tools.Helper.dataToJson(transfer);
            } else {
                throw new TransactionIsNotReadyException();
            }
        } else {
            throw new BankDoesNotExistException();
        }
    }

    //Transfer von der Bank zu einem Spieler
    String bankToPlayerTransfer(String bankId, String playerId, String amount, String searchingTransaction, String reason) throws Exception {
        String searching = ("/banks/" + bankId);
        String playerUri = ("/accounts/" + playerId);
        if (bankMap.containsKey(searching)) {
            if (bankMap.get(searching).getAccounts().containsKey(playerUri)) {
                int money = Integer.parseInt(amount);
                MoneyTransfer transfer = new MoneyTransfer(searching, playerUri, Integer.getInteger(amount));
                transfer.setReason(reason);
                if (searchingTransaction.equals("withoutTransaction")) {
                    bankMap.get(searching).getAccounts().get(transfer.getTo()).addMoney(money);
                    bankMap.get(searching).getTransfers().add(transfer);
                } else if (getTransaction(searching, searchingTransaction).getStatus() == TransactionStatus.ready) {
                    getTransaction(searching, searchingTransaction).getTransferInTransaction().add(transfer);
                    bankMap.get(searching).getAccounts().get(transfer.getTo()).addMoney(money);
                    getTransaction(searching, searchingTransaction).setPhases(TransactionPhase.Zwei);
                    bankMap.get(searching).getTransfers().add(transfer);
                } else {
                    throw new TransactionIsNotReadyException();
                }
                return Tools.Helper.dataToJson(transfer);
            } else {
                throw new BankAccountDoesNotExistException();
            }

        } else {
            throw new BankDoesNotExistException();
        }
    }

    //Transfer von dem Spieler zur Bank
    String playerToBankTransfer(String bankId, String playerId, String amount, String searchingTransaction, String reason) throws Exception {
        String searching = ("/banks/" + bankId);
        String playerUri = ("/accounts/" + playerId);
        if (bankMap.containsKey(searching)) {
            if (bankMap.get(searching).getAccounts().containsKey(playerUri)) {
                int money = Integer.getInteger(amount);
                MoneyTransfer transfer = new MoneyTransfer(playerUri, searching, money);
                transfer.setReason(reason);
                if (searchingTransaction.equals("withoutTransaction")) {
                    bankMap.get(searching).getAccounts().get(transfer.getFrom()).subMoney(money);
                    bankMap.get(searching).getTransfers().add(transfer);
                } else if (getTransaction(searching, searchingTransaction).getStatus() == TransactionStatus.ready) {
                    getTransaction(searching, searchingTransaction).getTransferInTransaction().add(transfer);
                    bankMap.get(searching).getAccounts().get(transfer.getFrom()).subMoney(money);
                    getTransaction(searching, searchingTransaction).setPhases(TransactionPhase.Zwei);
                    bankMap.get(searching).getTransfers().add(transfer);
                } else {
                    throw new TransactionIsNotReadyException();
                }
                return Tools.Helper.dataToJson(transfer);
            } else {
                throw new BankAccountDoesNotExistException();
            }
        } else {
            throw new BankDoesNotExistException();
        }
    }

    //Beginn einer Transaktion
    String beginOfTransaction(String bankId, TransactionPhase phase) throws BankDoesNotExistException {
        String searching = ("/banks/" + bankId);
        if (bankMap.containsKey(searching)) {
            Transaction transaction = new Transaction();
            transaction.setPhases(phase);
            String id = Tools.Helper.nextId();
            transaction.setId(id);
            transaction.setStatus(TransactionStatus.ready);
            String transactionuri = ("/transaction/" + id);
            bankMap.get(searching).getTransactionMap().put(transactionuri, transaction);
            return Tools.Helper.dataToJson(bankMap.get(searching).getTransactionMap().get(transactionuri));

        } else {
            throw new BankDoesNotExistException();
        }
    }

    //Gibt den Transaktionsstatus wieder
    String getTransactionStatus(String bankId, String transactionId) throws Exception {
        String searching = ("/banks/" + bankId);
        String searchingTransaction = ("/transaction/" + transactionId);
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
    String commitTransaction(String bankId, String transactionId, TransactionStatus state) throws Exception {
        String searching = ("/banks/" + bankId);
        String searchingTransaction = ("/transaction/" + transactionId);
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
    String rollbackTransaction(String bankId, String transactionId) throws Exception {
        String searching = ("/banks/" + bankId);
        String searchingTransaction = ("/transaction/" + transactionId);
        if (bankMap.containsKey(searching)) {
            if (bankMap.get(searching).getTransactionMap().containsKey(searchingTransaction)) {
                Transaction transaction = getTransaction(searching, searchingTransaction);
                if (transaction.getPhases() == TransactionPhase.Zwei) {
                    String toUri = transaction.getTransferInTransaction().get(0).getTo();
                    String fromUri = transaction.getTransferInTransaction().get(0).getFrom();
                    int amount =  transaction.getTransferInTransaction().get(0).getAmount();
                    if (fromUri.contains("/banks/")) {
                        bankMap.get(searching).getAccounts().get(toUri).subMoney(amount);
                    } else {
                        bankMap.get(searching).getAccounts().get(fromUri).addMoney(amount);
                    }
                    bankMap.get(searching).getTransactionMap().remove(searchingTransaction);
                }

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
    String getAllAccounts(String bankId) throws BankDoesNotExistException {
        String searching = ("/banks/" + bankId);
        if (bankMap.containsKey(searching)) {
            return Tools.Helper.dataToJson(bankMap.get(searching).getAccounts());
        } else {
            throw new BankDoesNotExistException();
        }
    }

    //Erstellung eines neuen Bankkontos
    String createNewAccount(String bankId, String payload) throws Exception {
        String searching = ("/banks/" + bankId);
        if (bankMap.containsKey(searching)) {
            ObjectMapper objectMapper = new ObjectMapper();
            BankAccount bankAccount = objectMapper.readValue(payload, BankAccount.class);
            String accountUri = ("/accounts/" + bankAccount.getPlayer());
            bankMap.get(searching).getAccounts().put(accountUri, bankAccount);

            return Tools.Helper.dataToJson(bankMap.get(searching).getAccounts().get(accountUri));
        } else {
            throw new BankDoesNotExistException();
        }
    }

    //Gibt das Saldo einen Kontos wieder
    String getBankAccountBalance(String bankId, String accountId) throws BankDoesNotExistException {
        String searching = ("/banks/" + bankId);
        String accountUri = ("/accounts/" + accountId);
        if (bankMap.containsKey(searching)) {
            if (bankMap.get(searching).getAccounts().containsKey(accountUri)) {
                return Tools.Helper.dataToJson(bankMap.get(searching).getAccounts().get(accountUri));
            }
        } else {
            throw new BankDoesNotExistException();
        }
        return "";
    }
}
