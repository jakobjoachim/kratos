package Bank;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static spark.Spark.before;
import static spark.Spark.post;

public class BankService {

    public void main(String[] args) {

        BankManager bankManager = new BankManager();

        before(((request, response) -> response.type("application/json")));
        before(((request, response) -> {

            if (!request.contentType().equals("application/json")) {
                response.status(HTTP_BAD_REQUEST);
                return;
            }

            response.header("Description", "An BankService for RESTopoly");
            response.type("application/json");
        }
        ));


        post("/banks/:gameid/players", (request, response) -> {
            try {
          //      return Tools.Helper.dataToJson(bankManager.createNewBankAccount(request.params(":gameid")));
            } catch (Exception e) {
                if (e instanceof Exception) {
                    response.status(HTTP_BAD_REQUEST);
                }
            }
            return "";
        });

    }

}
