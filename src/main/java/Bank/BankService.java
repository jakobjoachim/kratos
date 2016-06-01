package Bank;

import Enums.TransactionPhase;
import Enums.TransactionStatus;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static spark.Spark.*;

public class BankService {

    public void main(String[] args) {
        BankManager bankManager = new BankManager();

        before(((request, response) -> response.type("application/json")));
        before(((request, response) -> {
            if (!request.contentType().equals("application/json")) {
                response.status(HTTP_BAD_REQUEST);
                return;
            }
            response.header("Description", "A BankService for RESTopoly");
            response.type("application/json");
        }
        ));

        //Liste aller Banken
        get("/banks", (request, response) -> {
            try {
                return Tools.Helper.dataToJson(bankManager.getAllBanks());
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Neue Bank erstellen
        put("/banks/:bankid", (request, response) -> {
            try {
                return Tools.Helper.dataToJson(bankManager.createNewBank(request.params(":bankid")));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Alle möglichen Transfers
        get("/banks/:bankid/transfers", (request, response) -> {
            try {
                return Tools.Helper.dataToJson(bankManager.getAllTransfers(request.params(":bankid")));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Gibt einen Transfer wieder
        get("/banks/{bankid}/transfers/:transferid", (request, response) -> {
            try {
                return bankManager.getTransfer(request.params(":bankid"), request.params(":transferid"));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Geld vom Spieler zu Spieler überweisen
        post("/banks/:bankid/transfer/from/:from/to/:to/:amount", (request, response) -> {
            try {
                String transaction = request.queryMap().get("transaction").value();
                return Tools.Helper.dataToJson(bankManager.playerToPlayerTransfer(request.params(":bankid"), request.params(":from"), request.params(":to"), request.params(":amount"), transaction));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Geld von Bank zum Spieler überweisen
        post("/banks/:bankid/transfer/to/:to/:amount", (request, response) -> {
            try {
                String transaction = request.queryMap().get("transaction").value();
                return Tools.Helper.dataToJson(bankManager.bankToPlayerTransfer(request.params(":bankid"), request.params(":to"), request.params(":amount"), transaction));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Geld vom Spieler einziehen
        post("/banks/:bankid/transfer/from/:from/:amount", (request, response) -> {
            try {
                String transaction = request.queryMap().get("transaction").value();
                return Tools.Helper.dataToJson(bankManager.playerToBankTransfer(request.params(":bankid"), request.params(":to"), request.params(":amount"), transaction));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Beginnt neue Transaktion
        post("/banks/:bankid/transaction", (request, response) -> {
            try {
                TransactionPhase phases;
                if (request.queryMap().hasValue()) {
                    phases = TransactionPhase.fromInteger(Integer.parseInt(request.queryMap().get("phases").value()));
                } else {
                    phases = TransactionPhase.Eins;
                }
                return Tools.Helper.dataToJson(bankManager.beginOfTransaction(request.params(":bankid"), phases));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Gibt Status einer Transaktion wieder
        get("/banks/:bankid/transaction/:tid", (request, response) -> {
            try {
                return Tools.Helper.dataToJson(bankManager.getTransactionStatus(request.params(":bankid"),request.params(":tid")));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Commited Transaktion
        put("/banks/:bankid/transaction/:tid", (request, response) -> {
            try {
                TransactionStatus state;
                if (request.queryMap().get("state").hasValue()) {
                    state = TransactionStatus.valueOf(request.queryMap().get("state").value());
                } else {
                    state = TransactionStatus.commit;
                }
                return Tools.Helper.dataToJson(bankManager.commitTransaction(request.params(":bankid"),request.params(":tid"), state));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        // Rollback einer Transaktion
        delete("/banks/:bankid/transaction/:tid", (request, response) -> {
            try {
                return Tools.Helper.dataToJson(bankManager.rollbackTransaction(request.params(":bankid"),request.params(":tid")));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Liste aller Konten
        get("/banks/:bankid/accounts", (request, response) -> {
            try {
                return Tools.Helper.dataToJson(bankManager.getAllAccounts(request.params(":bankid")));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Erstellt neues Bankkonto
        post("/banks/:bankid/accounts", (request, response) -> {
            try {
                HashMap<String,String> queryMap = new HashMap<>();
                queryMap.put("player", request.queryMap().get("player").value());
                queryMap.put("saldo", request.queryMap().get("saldo").value());
                return bankManager.createNewAccount(request.params(":bankid"), queryMap);
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Kontostand abfragen
        get("/banks/:bankid/accounts/:accountid", (request, response) -> {
            try {
                return Tools.Helper.dataToJson(bankManager.getBankAccountBalance(request.params(":bankid"),request.params(":accountid")));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });
    }
}
