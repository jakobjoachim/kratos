package Bank;

import Enums.TransactionPhase;
import Enums.TransactionStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import static spark.Spark.*;

public class BankService {

    private static final int HTTP_BAD_REQUEST = 400;
    private static final int RESOURCE_NOT_FOUND = 404;
    private static final int INSUFFICENT_FONDS = 403;
    private static final int PLAYER_ALREADY_GOT_A_ACCOUNT = 409;
    private static final int CREATED = 201;
    private static final int OK = 200;

    public static void main(String[] args) {
        BankManager bankManager = new BankManager();

        //Liste aller Banken TODO checked
        get("/banks", (request, response) -> {
            try {
                response.status(OK);
                response.type("application/json");
                return bankManager.getAllBanks();
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Neue Bank erstellen mit random Id TODO checked
        post("/banks", (request, response) -> {
            response.status(CREATED);
            try {
                response.type("application/json");
                return bankManager.createNewBank(Tools.Helper.nextId());
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Neue Bank erstellen TODO checked
        post("/banks/:bankid", (request, response) -> {
            try {
                response.status(CREATED);
                response.type("application/json");
                return bankManager.createNewBank(request.params(":bankid"));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Gibt eine Bank TODO checked
        get("/banks/:bankid", (request, response) -> {
            try {
                response.status(RESOURCE_NOT_FOUND);
                response.type("application/json");
                return bankManager.getBank(request.params(":bankid"));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Alle möglichen Transfers todo checked without content
        get("/banks/:bankid/transfers", (request, response) -> {
            try {
                response.status(OK);
                response.type("application/json");
                return bankManager.getAllTransfers(request.params(":bankid"));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Gibt einen MoneyTransfer wieder
        get("/banks/{bankid}/moneyTransferInTransaction/:transferid", (request, response) -> {
            try {
                response.status(OK);
                response.type("application/json");
                return bankManager.getTransfer(request.params(":bankid"), request.params(":transferid"));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Geld vom Spieler zu Spieler überweisen todo checked
        post("/banks/:bankid/transfer/from/:from/to/:to/:amount", (request, response) -> {
            try {
                response.status(CREATED);
                response.type("application/json");
                String transaction;
                ObjectMapper mapper = new ObjectMapper();
                ReasonPayload reason = mapper.readValue(request.body(), ReasonPayload.class);

                if (request.queryMap().get("transaction").hasValue()) {
                    transaction = request.queryMap().get("transaction").value();
                } else {
                    transaction = "withoutTransaction";
                }
                return bankManager.playerToPlayerTransfer(request.params(":bankid"), request.params(":from"), request.params(":to"), request.params(":amount"), transaction, reason.reason);
            } catch (Exception e) {
                e.printStackTrace();
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Geld von der Bank zum Spieler überweisen todo checked
        post("/banks/:bankid/transfer/to/:to/:amount", (request, response) -> {
            try {
                response.status(CREATED);
                response.type("application/json");
                String transaction;
                ObjectMapper mapper = new ObjectMapper();
                ReasonPayload reason = mapper.readValue(request.body(), ReasonPayload.class);

                if (request.queryMap().get("transaction").hasValue()) {
                    transaction = request.queryMap().get("transaction").value();
                } else {
                    transaction = "withoutTransaction";
                }
                return bankManager.bankToPlayerTransfer(request.params(":bankid"), request.params(":to"), request.params(":amount"), transaction, reason.reason);
            } catch (Exception e) {
                e.printStackTrace();
                response.status(INSUFFICENT_FONDS);
            }
            return "";
        });

        //Geld vom Spieler einziehen todo checked
        post("/banks/:bankid/transfer/from/:from/:amount", (request, response) -> {
            try {
                response.status(CREATED);
                response.type("application/json");
                String transaction;
                ObjectMapper mapper = new ObjectMapper();
                ReasonPayload reason = mapper.readValue(request.body(), ReasonPayload.class);

                if (request.queryMap().get("transaction").hasValue()) {
                    transaction = request.queryMap().get("transaction").value();
                } else {
                    transaction = "withoutTransaction";
                }
                return bankManager.playerToBankTransfer(request.params(":bankid"), request.params(":from"), request.params(":amount"), transaction, reason.reason);
            } catch (Exception e) {
                e.printStackTrace();
                response.status(INSUFFICENT_FONDS);
            }
            return "";
        });

        //Beginnt neue Transaktion todo checked
        post("/banks/:bankid/transaction", (request, response) -> {
            try {
                response.status(OK);
                response.type("application/json");
                TransactionPhase phases;
                if (request.queryMap().hasValue()) {
                    phases = TransactionPhase.fromInteger(Integer.parseInt(request.queryMap().get("phases").value()));
                } else {
                    phases = TransactionPhase.Eins;
                }
                return bankManager.beginOfTransaction(request.params(":bankid"), phases);
            } catch (Exception e) {
                response.status(INSUFFICENT_FONDS);
            }
            return "";
        });

        //Gibt Status einer Transaktion wieder todo checked
        get("/banks/:bankid/transaction/:tid", (request, response) -> {
            try {
                response.status(OK);
                response.type("application/json");
                return bankManager.getTransactionStatus(request.params(":bankid"),request.params(":tid"));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Commited Transaktion todo checked
        put("/banks/:bankid/transaction/:tid", (request, response) -> {
            try {
                response.status(OK);
                response.type("application/json");
                TransactionStatus state;
                if (request.queryMap().get("state").hasValue()) {
                    state = TransactionStatus.valueOf(request.queryMap().get("state").value());
                } else {
                    state = TransactionStatus.commit;
                }
                return bankManager.commitTransaction(request.params(":bankid"),request.params(":tid"), state);
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        // Rollback einer Transaktion todo checked
        delete("/banks/:bankid/transaction/:tid", (request, response) -> {
            try {
                response.status(OK);
                response.type("application/json");
                return bankManager.rollbackTransaction(request.params(":bankid"),request.params(":tid"));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Liste aller Konten todo checked
        get("/banks/:bankid/accounts", (request, response) -> {
            try {
                response.status(OK);
                response.type("application/json");
                return bankManager.getAllAccounts(request.params(":bankid"));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });

        //Erstellt neues Bankkonto todo checked
        post("/banks/:bankid/accounts", (request, response) -> {
            try {
                response.type("application/json");
                response.status(CREATED);
                return bankManager.createNewAccount(request.params(":bankid"), request.body());
            } catch (Exception e) {
                response.status(PLAYER_ALREADY_GOT_A_ACCOUNT);
            }
            return "";
        });

        //Kontostand abfragen todo checked
        get("/banks/:bankid/accounts/:accountid", (request, response) -> {
            try {
                response.status(OK);
                response.type("application/json");
                return bankManager.getBankAccountBalance(request.params(":bankid"),request.params(":accountid"));
            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
            }
            return "";
        });
    }
}
