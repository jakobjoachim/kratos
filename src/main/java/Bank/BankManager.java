package Bank;

import Enums.TransactionPhase;
import Enums.TransactionStatus;
import Exceptions.*;
import Tools.Helper;
import Tools.SharedPayloads.EventPayload;
import Tools.SharedPayloads.PayloadPayload;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class BankManager {

    //HashMap mit BankUri und der Bank
    private static Map<String, Bank> bankMap = new HashMap<>();

    //Gibt alle Banken wieder
    String getAllBanks() {
        return Tools.Helper.dataToJson(bankMap.keySet());
    }

    private Transaction getTransaction(String bankUri, String transactionUri) {
        return bankMap.get(bankUri).getTransactionMap().get(transactionUri);
    }

    //Erstellt eine neue Bank
    String createNewBank(String bankId) throws BankAlreadyExistsException {
        Bank bank = new Bank();
        String bankUri = ("/banks/" + bankId);
        if (!(bankMap.containsKey(bankUri))) {
            bank.setId(bankId);
            bankMap.put(bankUri, bank);
        } else {
            throw new BankAlreadyExistsException();
        }
        return Tools.Helper.dataToJson(bankUri);
    }

    String getBank(String bankId) throws BankDoesNotExistException {
        String bankUri = ("/banks/" + bankId);
        if (bankMap.containsKey(bankUri)) {
            return Tools.Helper.dataToJson(bankMap.get(bankUri));
        } else {
            throw new BankDoesNotExistException();
        }
    }

    //Alle Transfers
    String getAllTransfers(String bankId) throws BankDoesNotExistException {
        String searching = ("/banks/" + bankId);
        if (bankMap.containsKey(searching)) {
            return Tools.Helper.dataToJson(bankMap.get(searching).getTransfers());
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
    String playerToPlayerTransfer(String bankId, String fromId, String toId, String amount, String transactionId, String reason) throws Exception {
        String searching = ("/banks/" + bankId);
        String searchingTransaction = ("/transaction/" + transactionId);
        String fromUri = ("/accounts/" + fromId);
        String toUri = ("/accounts/" + toId);
        if (bankMap.containsKey(searching)) {
            if (bankMap.get(searching).getAccounts().containsKey(fromUri) && bankMap.get(searching).getAccounts().containsKey(toUri)) {
                int money = Integer.parseInt(amount);
                MoneyTransfer transfer = new MoneyTransfer(fromUri, toUri, money);
                transfer.setTransferId(Helper.nextId());
                transfer.setReason(reason);
                int beforeFrom = bankMap.get(searching).getAccounts().get(fromUri).getBalance();
                int beforeTo = bankMap.get(searching).getAccounts().get(toUri).getBalance();

                if (searchingTransaction.equals("/transaction/withoutTransaction")) {
                    bankMap.get(searching).getAccounts().get(transfer.getFrom()).subMoney(money);
                    bankMap.get(searching).getAccounts().get(transfer.getTo()).addMoney(money);
                    bankMap.get(searching).getTransfers().add(transfer);

                    EventPayload eventPayload = new EventPayload("The account balance decreased", bankMap.get(searching).getId(), "account_balance_changed", "", "", fromId);
                    PayloadPayload payloadPayload = new PayloadPayload("" + beforeFrom,"" + bankMap.get(searching).getAccounts().get(transfer.getFrom()).getBalance());
                    eventPayload.setPayload(payloadPayload);
                    Helper.broadcastEvent(eventPayload);

                    EventPayload eventPayload2 = new EventPayload("The account balance increased", bankMap.get(searching).getId(), "account_balance_changed", "", "", toId);
                    PayloadPayload payloadPayload2 = new PayloadPayload("" + beforeTo,"" + bankMap.get(searching).getAccounts().get(transfer.getTo()).getBalance());
                    eventPayload2.setPayload(payloadPayload2);
                    Helper.broadcastEvent(eventPayload2);
                    
                } else if (getTransaction(searching, searchingTransaction).getStatus() == TransactionStatus.ready) {
                    getTransaction(searching, searchingTransaction).getTransferInTransaction().add(transfer);
                    bankMap.get(searching).getAccounts().get(transfer.getFrom()).subMoney(money);
                    getTransaction(searching, searchingTransaction).setPhases(TransactionPhase.Zwei);
                    bankMap.get(searching).getAccounts().get(transfer.getTo()).addMoney(money);
                    getTransaction(searching, searchingTransaction).setPhases(TransactionPhase.Drei);
                    bankMap.get(searching).getTransfers().add(transfer);

                    EventPayload eventPayload = new EventPayload("Transaction: The account balance decreased", bankMap.get(searching).getId(), "account_balance_changed", "", "", fromId);
                    PayloadPayload payloadPayload = new PayloadPayload("" + beforeFrom,"" + bankMap.get(searching).getAccounts().get(transfer.getFrom()).getBalance());
                    eventPayload.setPayload(payloadPayload);
                    Helper.broadcastEvent(eventPayload);

                    EventPayload eventPayload2 = new EventPayload("Transaction: The account balance increased", bankMap.get(searching).getId(), "account_balance_changed", "", "", toId);
                    PayloadPayload payloadPayload2 = new PayloadPayload("" + beforeTo,"" + bankMap.get(searching).getAccounts().get(transfer.getTo()).getBalance());
                    eventPayload.setPayload(payloadPayload2);
                    Helper.broadcastEvent(eventPayload2);
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

    //Transfer von der Bank zu einem Spieler
    String bankToPlayerTransfer(String bankId, String playerId, String amount, String transactionId, String reason) throws Exception {
        String searching = ("/banks/" + bankId);
        String playerUri = ("/accounts/" + playerId);
        String searchingTransaction = ("/transaction/" + transactionId);
        if (bankMap.containsKey(searching)) {
            if (bankMap.get(searching).getAccounts().containsKey(playerUri)) {
                int money = Integer.parseInt(amount);
                MoneyTransfer transfer = new MoneyTransfer(searching, playerUri, money);
                transfer.setReason(reason);
                transfer.setTransferId(Helper.nextId());
                int before = bankMap.get(searching).getAccounts().get(playerUri).getBalance();
                if (searchingTransaction.equals("/transaction/withoutTransaction")) {
                    bankMap.get(searching).getAccounts().get(transfer.getTo()).addMoney(money);
                    bankMap.get(searching).getTransfers().add(transfer);

                    EventPayload eventPayload = new EventPayload("The account balance increased", bankMap.get(searching).getId(), "account_balance_changed", "", "", playerId);
                    PayloadPayload payloadPayload = new PayloadPayload("" + before,"" + bankMap.get(searching).getAccounts().get(transfer.getTo()).getBalance());
                    eventPayload.setPayload(payloadPayload);
                    Helper.broadcastEvent(eventPayload);

                } else if (getTransaction(searching, searchingTransaction).getStatus() == TransactionStatus.ready) {
                    getTransaction(searching, searchingTransaction).getTransferInTransaction().add(transfer);
                    bankMap.get(searching).getAccounts().get(transfer.getTo()).addMoney(money);
                    getTransaction(searching, searchingTransaction).setPhases(TransactionPhase.Zwei);
                    bankMap.get(searching).getTransfers().add(transfer);

                    EventPayload eventPayload = new EventPayload("Transaction: The account balance increased", bankMap.get(searching).getId(), "account_balance_changed", "", "", playerId);
                    PayloadPayload payloadPayload = new PayloadPayload("" + before,"" + bankMap.get(searching).getAccounts().get(transfer.getTo()).getBalance());
                    eventPayload.setPayload(payloadPayload);
                    Helper.broadcastEvent(eventPayload);
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
    String playerToBankTransfer(String bankId, String playerId, String amount, String transactionId, String reason) throws Exception {
        String searching = ("/banks/" + bankId);
        String playerUri = ("/accounts/" + playerId);
        String searchingTransaction = ("/transaction/" + transactionId);

        if (bankMap.containsKey(searching)) {
            if (bankMap.get(searching).getAccounts().containsKey(playerUri)) {
                int money = Integer.parseInt(amount);
                MoneyTransfer transfer = new MoneyTransfer(playerUri, searching, money);
                transfer.setReason(reason);
                transfer.setTransferId(Helper.nextId());
                int before = bankMap.get(searching).getAccounts().get(playerUri).getBalance();
                if (searchingTransaction.equals("/transaction/withoutTransaction")) {
                    bankMap.get(searching).getAccounts().get(transfer.getFrom()).subMoney(money);
                    bankMap.get(searching).getTransfers().add(transfer);

                    EventPayload eventPayload = new EventPayload("The account balance decreased", bankMap.get(searching).getId(), "account_balance_changed", "dasdasd", "dasdasd", playerId);
                    PayloadPayload payloadPayload = new PayloadPayload("" + before,"" + bankMap.get(searching).getAccounts().get(transfer.getFrom()).getBalance());
                    eventPayload.setPayload(payloadPayload);
                    Helper.broadcastEvent(eventPayload);

                } else if (getTransaction(searching, searchingTransaction).getStatus() == TransactionStatus.ready) {
                    getTransaction(searching, searchingTransaction).getTransferInTransaction().add(transfer);
                    bankMap.get(searching).getAccounts().get(transfer.getFrom()).subMoney(money);
                    getTransaction(searching, searchingTransaction).setPhases(TransactionPhase.Zwei);
                    bankMap.get(searching).getTransfers().add(transfer);

                    EventPayload eventPayload = new EventPayload("Transaction: The account balance decreased", bankMap.get(searching).getId(), "account_balance_changed", "", "", playerId);
                    PayloadPayload payloadPayload = new PayloadPayload("" + before,"" + bankMap.get(searching).getAccounts().get(transfer.getFrom()).getBalance());
                    eventPayload.setPayload(payloadPayload);
                    Helper.broadcastEvent(eventPayload);
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
                String toUri = transaction.getTransferInTransaction().get(0).getTo();
                String fromUri = transaction.getTransferInTransaction().get(0).getFrom();
                int amount =  transaction.getTransferInTransaction().get(0).getAmount();
                if (transaction.getPhases() == TransactionPhase.Zwei) {
                    if (fromUri.contains("/banks/")) {
                        bankMap.get(searching).getAccounts().get(toUri).subMoney(amount);
                    } else {
                        bankMap.get(searching).getAccounts().get(fromUri).addMoney(amount);
                    }
                    bankMap.get(searching).getTransactionMap().remove(searchingTransaction);
                } else if (transaction.getPhases() == TransactionPhase.Drei) {
                    bankMap.get(searching).getAccounts().get(toUri).subMoney(amount);
                    bankMap.get(searching).getAccounts().get(fromUri).addMoney(amount);
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
            List<BankAccountPayload> bankAccountsPayload = new ArrayList<>();
            for (Map.Entry<String, BankAccount> entry : bankMap.get(searching).getAccounts().entrySet()) {
                BankAccountPayload bankAccountPayload = new BankAccountPayload();
                bankAccountPayload.setId(entry.getKey());
                bankAccountPayload.setBalance(entry.getValue().getBalance());
                bankAccountPayload.setPlayer(entry.getValue().getPlayer());
                bankAccountsPayload.add(bankAccountPayload);
            }
            return Tools.Helper.dataToJson(bankAccountsPayload);
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
            if (bankMap.get(searching).getAccounts().containsKey(accountUri)) {
                throw new BankAccountDoesAlreadyExistException();
            } else {
                bankMap.get(searching).getAccounts().put(accountUri, bankAccount);

            }

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
