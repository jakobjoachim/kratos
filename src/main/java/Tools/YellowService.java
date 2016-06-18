package Tools;

import Enums.ServiceType;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

public class YellowService {

    private static String yellowServiceUrl = "http://172.18.0.5:4567";

    public static String getServiceUrlForType(ServiceType type) {

        String result = "";

        switch (type) {

            case EVENTS:
                String eventTypeServiceURL = yellowServiceUrl + "/services/of/name/KRATOSEventService";

                try {
                    result = getServiceUrl(eventTypeServiceURL);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                break;

            case DICE:
                String diceTypeServiceURL = yellowServiceUrl + "/services/of/name/KRATOSDiceService";

                try {
                    result = getServiceUrl(diceTypeServiceURL);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                break;
            case GAME:
                String gameTypeServiceURL = yellowServiceUrl + "/services/of/name/KRATOSGameService";

                try {
                    result = getServiceUrl(gameTypeServiceURL);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                break;
            case BOARD:
                String boardTypeServiceURL = yellowServiceUrl + "/services/of/name/KRATOSBoardService";

                try {
                    result = getServiceUrl(boardTypeServiceURL);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                break;
            case USER:
                String userTypeServiceURL = yellowServiceUrl + "/services/of/name/KRATOSUserService";

                try {
                    result = getServiceUrl(userTypeServiceURL);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                break;
            case BANK:
                String bankTypeServiceURL = yellowServiceUrl + "/services/of/name/KRATOSBankService";

                try {
                    result = getServiceUrl(bankTypeServiceURL);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                break;

            case CLIENT:
                String clientTypeServiceURL = yellowServiceUrl + "/services/of/name/KRATOSClientService";

                try {
                    result = getServiceUrl(clientTypeServiceURL);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                break;

            default: break;
        }

        return result;
    }

    private static String getServiceUrl(String url) throws UnirestException {

        HttpResponse<JsonNode> jsonResponse = Unirest.get(url).asJson();
        JSONObject data = jsonResponse.getBody().getObject();
        JSONArray urls = (JSONArray) data.get("services");
        String serviceUrl = (String) urls.get(0);

        serviceUrl = yellowServiceUrl + serviceUrl;

        jsonResponse = Unirest.get(serviceUrl).asJson();
        data = jsonResponse.getBody().getObject();

        return (String) data.get("uri");
    }
    
}
