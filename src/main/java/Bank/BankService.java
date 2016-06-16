package Bank;

import Enums.TransactionPhase;
import Enums.TransactionStatus;


import java.util.HashMap;



import static spark.Spark.*;

public class BankService {

    private static final int HTTP_BAD_REQUEST = 400;

    public static void main(String[] args) {
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

        //Liste aller Banken TODO checked
        get("/banks", (request, response) -> {
            try {
                return Tools.Helper.dataToJson(bankManager.getAllBanks());
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Neue Bank erstellen mit random Id TODO checked
        post("/banks", (request, response) -> {
            try {
                return Tools.Helper.dataToJson(bankManager.createNewBank(Tools.Helper.nextId()));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Neue Bank erstellen TODO checked
        put("/banks/:bankid", (request, response) -> {
            try {
                return Tools.Helper.dataToJson(bankManager.createNewBank(request.params(":bankid")));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Alle möglichen Transfers todo checked without content
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
                String transaction;
                if (request.queryMap().hasValue()) {
                    transaction = request.queryMap().get("transaction").value();
                } else {
                    transaction = "withoutTransaction";
                }
                return Tools.Helper.dataToJson(bankManager.playerToPlayerTransfer(request.params(":bankid"), request.params(":from"), request.params(":to"), request.params(":amount"), transaction, request.body()));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Geld von der Bank zum Spieler überweisen
        post("/banks/:bankid/transfer/to/:to/:amount", (request, response) -> {
            try {
                String transaction;
                if (request.queryMap().hasValue()) {
                    transaction = request.queryMap().get("transaction").value();
                } else {
                    transaction = "withoutTransaction";
                }
                return Tools.Helper.dataToJson(bankManager.bankToPlayerTransfer(request.params(":bankid"), request.params(":to"), request.params(":amount"), transaction, request.body()));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Geld vom Spieler einziehen
        post("/banks/:bankid/transfer/from/:from/:amount", (request, response) -> {
            try {
                String transaction;
                if (request.queryMap().hasValue()) {
                    transaction = request.queryMap().get("transaction").value();
                } else {
                    transaction = "withoutTransaction";
                }
                return Tools.Helper.dataToJson(bankManager.playerToBankTransfer(request.params(":bankid"), request.params(":to"), request.params(":amount"), transaction, request.body()));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Beginnt neue Transaktion todo check again
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

        //Gibt Status einer Transaktion wieder todo checked
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

        //Liste aller Konten todo checked
        get("/banks/:bankid/accounts", (request, response) -> {
            try {
                return bankManager.getAllAccounts(request.params(":bankid"));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Erstellt neues Bankkonto todo checked
        post("/banks/:bankid/accounts", (request, response) ->
                bankManager.createNewAccount(request.params(":bankid"),request.body())
        );

        //Kontostand abfragen todo check again with full account response
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
