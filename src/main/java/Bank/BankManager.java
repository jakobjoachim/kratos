package Bank;


import Enums.TransactionPhase;
import Enums.TransactionStatus;
import Exceptions.*;


import java.util.*;

public class BankManager {

    //HashMap mit BankUri und der Bank
    private static Map<String, Bank> bankMap = new HashMap<>();

    //Gibt alle Banken wieder
    Set<String> getAllBanks() {
        return bankMap.keySet();
    }

    private Transaction getTransaction(String bankId, String transactionId) {
        String url = ("\"/banks/" + bankId + "\"");
        return bankMap.get(url).getTransactionMap().get(transactionId);
    }

    //Erstellt eine neue Bank
    String createNewBank(String bankId) throws BankAlreadyExistsException {
        Bank bank = new Bank();
        String url = ("\"/banks/" + bankId + "\"");
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
        String searching = ("\"/banks/" + bankId + "\"");
        if (bankMap.containsKey(searching)) {
            return bankMap.get(searching).getMoneyTransfers();
        } else {
            throw new BankDoesNotExistException();
        }
    }

    //Gibt einen MoneyTransfer wieder
    String getTransfer(String bankId, String tranferId) throws BankDoesNotExistException {
        String searching = ("\"/banks/" + bankId + "\"");
        if (bankMap.containsKey(searching)) {

            for (MoneyTransfer moneyTransfer : bankMap.get(searching).getMoneyTransfers()) {
                if (moneyTransfer.getTransferId().equals(tranferId)) {
                    return Tools.Helper.dataToJson(moneyTransfer);
                }
            }
        } else {
            throw new BankDoesNotExistException();
        }
        return "";
    }

    //MoneyTransfer von einem zum anderen Spieler
    String playerToPlayerTransfer(String bankId, String fromId, String toId, String amount, String searchingTransaction) throws Exception {
        String searching = ("\"/banks/" + bankId + "\"");
        String fromUri = ("\"/accounts/" + fromId + "\"");
        String toUri = ("\"/accounts/" + toId + "\"");
        if (getTransaction(searching, searchingTransaction).getStatus() == TransactionStatus.ready) {
            int money = Integer.parseInt(amount);
            MoneyTransfer moneyTransfer = new MoneyTransfer(fromId, toId,Integer.getInteger(amount));
            if (bankMap.containsKey(searching)) {
                if (bankMap.get(searching).getAccounts().containsKey(fromUri)) {
                    moneyTransfer.setFrom(fromUri);
                } else {
                    throw new BankAccountDoesNotExistException();
                }
                if (bankMap.get(searching).getAccounts().containsKey(toUri)) {
                    moneyTransfer.setTo(toUri);
                } else {
                    throw new BankAccountDoesNotExistException();
                }
                getTransaction(searching, searchingTransaction).getMoneyTransferInTransaction().add(moneyTransfer);
                bankMap.get(searching).getAccounts().get(moneyTransfer.getFrom()).subMoney(money);
                bankMap.get(searching).getAccounts().get(moneyTransfer.getTo()).addMoney(money);
                bankMap.get(searching).getMoneyTransfers().add(moneyTransfer);
            } else {
                throw new BankDoesNotExistException();
            }

        } else {
            throw new TransactionIsNotReadyException();
        }
        return "";
    }

    //MoneyTransfer von der Bank zu einem Spieler
    String bankToPlayerTransfer(String bankId, String playerId, String amount, String searchingTransaction) throws Exception {
        String searching = ("\"/banks/" + bankId + "\"");
        String playerUri = ("\"/accounts/" + playerId + "\"");
        if (getTransaction(searching, searchingTransaction).getStatus() == TransactionStatus.ready) {
            MoneyTransfer moneyTransfer = new MoneyTransfer(searching, playerId,Integer.getInteger(amount));
            int money = Integer.parseInt(amount);
            moneyTransfer.setAmount(money);
            if (bankMap.containsKey(searching)) {
                moneyTransfer.setFrom(searching);
                if (bankMap.get(searching).getAccounts().containsKey(playerUri)) {
                    moneyTransfer.setTo(playerUri);
                } else {
                    throw new BankAccountDoesNotExistException();
                }
            } else {
                throw new BankDoesNotExistException();
            }
            getTransaction(searching, searchingTransaction).getMoneyTransferInTransaction().add(moneyTransfer);
            bankMap.get(searching).getAccounts().get(moneyTransfer.getTo()).addMoney(money);
            bankMap.get(searching).getMoneyTransfers().add(moneyTransfer);
        } else {
            throw new TransactionIsNotReadyException();
        }
        return "";
    }

    //MoneyTransfer von dem Spieler zur Bank
    String playerToBankTransfer(String bankId, String playerId, String amount, String searchingTransaction) throws Exception {
        String searching = ("\"/banks/" + bankId + "\"");
        String playerUri = ("\"/accounts/" + playerId + "\"");
        if (getTransaction(searching, searchingTransaction).getStatus() == TransactionStatus.ready) {
            MoneyTransfer moneyTransfer = new MoneyTransfer(playerId, searching,Integer.getInteger(amount));
            int money = Integer.parseInt(amount);
            moneyTransfer.setAmount(money);
            if (bankMap.containsKey(searching)) {
                moneyTransfer.setTo(searching);
                if (bankMap.get(searching).getAccounts().containsKey(playerUri)) {
                    moneyTransfer.setFrom(playerUri);
                } else {
                    throw new BankAccountDoesNotExistException();
                }
            } else {
                throw new BankDoesNotExistException();
            }
            getTransaction(searching, searchingTransaction).getMoneyTransferInTransaction().add(moneyTransfer);
            bankMap.get(searching).getAccounts().get(moneyTransfer.getFrom()).subMoney(money);
            bankMap.get(searching).getMoneyTransfers().add(moneyTransfer);
        } else {
            throw new TransactionIsNotReadyException();
        }
        return "";
    }

    //Beginn einer Transaktion
    String beginOfTransaction(String bankId, TransactionPhase phase) throws BankDoesNotExistException {
        String searching = ("\"/banks/" + bankId + "\"");
        if (bankMap.containsKey(searching)) {
            Transaction transaction = new Transaction();
            transaction.setPhases(phase);
            String id = Tools.Helper.nextId();
            String transactionuri = ("\"/transaction/" + id + "\"");
            bankMap.get(searching).getTransactionMap().put(transactionuri, transaction);
        } else {
            throw new BankDoesNotExistException();
        }
        return Tools.Helper.dataToJson(bankMap.get(searching).getTransactionMap());
    }

    //Gibt den Transaktionsstatus wieder
    String getTransactionStatus(String bankId, String transactionId) throws Exception {
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
    String commitTransaction(String bankId, String transactionId, TransactionStatus state) throws Exception {
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
    String rollbackTransaction(String bankId, String transactionId) throws Exception {
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
    String getAllAccounts(String bankId) throws BankDoesNotExistException {
        String searching = ("\"/banks/" + bankId + "\"");
        if (bankMap.containsKey(searching)) {
            return Tools.Helper.dataToJson(bankMap.get(searching).getAccounts());
        } else {
            throw new BankDoesNotExistException();
        }
    }

    //Erstellung eines neuen Bankkontos
    String createNewAccount(String bankId, HashMap<String, String> accountinfo) throws BankDoesNotExistException {
        String searching = ("\"/banks/" + bankId + "\"");
        if (bankMap.containsKey(searching)) {
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(Integer.parseInt(accountinfo.get("saldo")));
            bankAccount.setPlayerId(accountinfo.get("player"));
            String id = Tools.Helper.nextId();
            String accountUri = ("\"/accounts/" + id + "\"");
            bankAccount.setId(id);
            bankMap.get(searching).getAccounts().put(accountUri, bankAccount);
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
