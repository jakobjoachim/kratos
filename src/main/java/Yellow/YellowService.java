package Yellow;

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

        switch (type) {

            case EVENTS:
                String eventTypeServiceURL = yellowServiceUrl + "/services/of/name/KRATOSEventsService";

                try {
                    getServiceUrl(eventTypeServiceURL);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                break;

            case DICE:
                String diceTypeServiceURL = yellowServiceUrl + "/services/of/name/KRATOSDiceService";

                try {
                    getServiceUrl(diceTypeServiceURL);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                break;
            case GAME:
                String gameTypeServiceURL = yellowServiceUrl + "/services/of/name/KRATOSGameService";

                try {
                    getServiceUrl(gameTypeServiceURL);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                break;
            case BOARD:
                String boardTypeServiceURL = yellowServiceUrl + "/services/of/name/KRATOSBoardService";

                try {
                    getServiceUrl(boardTypeServiceURL);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                break;
            case USER:
                String userTypeServiceURL = yellowServiceUrl + "/services/of/name/KRATOSUserService";

                try {
                    getServiceUrl(userTypeServiceURL);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                break;

            default: break;
        }

        return "";
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
