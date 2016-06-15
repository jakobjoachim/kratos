package Broker;

import Bank.MoneyTransfer;
import Enums.TransactionPhase;
import Exceptions.MoneyTransferFailedException;
import Exceptions.TransactionDoesNotExistException;
import Exceptions.TransactionFailedException;
import Tools.Helper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DistributedTransaction {
    private Map<String, PlaceTransaction> transactionMap = new HashMap<>();

    public String beginOfTransaction() {
        PlaceTransaction transaction = new PlaceTransaction();
        transaction.setPhases(TransactionPhase.Eins);
        String id = Helper.nextId();
        transaction.setId(id);
        transactionMap.put(id, transaction);
        return id;
    }

    public void transfer(String from, String to, String bankUri, int amount, String transactionId, Place place) throws Exception {
        if (transactionMap.containsKey(transactionId)) {
            PlaceTransaction transaction = transactionMap.get(transactionId);
            MoneyTransfer moneyTransfer = new MoneyTransfer();
            moneyTransfer.setAmount(amount);
            moneyTransfer.setFrom(from);
            moneyTransfer.setTo(to);
            transaction.setMoneyTransfer(moneyTransfer);
            PlaceTransfer placeTransfer = new PlaceTransfer(to, place);
            transaction.setPlaceTransfer(placeTransfer);
            if (placeTransfer.commit()) {
                try {
                    moneyTransfer(bankUri, moneyTransfer);
                } catch (Exception e){
                    placeTransfer.rollback();
                }
            } else {
                throw new TransactionFailedException();
            }
        } else {
            throw new TransactionDoesNotExistException();
        }
    }

    private String moneyTransfer(String bankUri, MoneyTransfer moneyTransfer) throws Exception{
        String url = bankUri + "/transfer/from/" + moneyTransfer.getFrom() + "/to/" + moneyTransfer.getTo() + "/" + moneyTransfer.getAmount();
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post(url).asJson();
            JSONObject data = jsonResponse.getBody().getObject();
            return (String) data.get("transactionId");
        } catch (Exception e){
            throw new MoneyTransferFailedException();
        }
    }
}
