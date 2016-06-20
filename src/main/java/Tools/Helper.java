package Tools;

import Enums.ServiceType;
import Tools.SharedPayloads.EventPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.SecureRandom;

public class Helper {
    private static SecureRandom random = new SecureRandom();

    public static String dataToJson(Object data){
        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            StringWriter sw = new StringWriter();
            mapper.writeValue(sw,data);
            return sw.toString();
        }catch(IOException e){
            throw new RuntimeException("IOException from a StringWriter??");
        }
    }

    public static String nextId() {
        return new BigInteger(130, random).toString(32);
    }

    public static boolean broadcastEvent(EventPayload eventPayload) {

        String eventServiceUrl = YellowService.getServiceUrlForType(ServiceType.EVENTS);

        try {
            HttpResponse<JsonNode> response = Unirest.post(eventServiceUrl)
                    .body(Helper.dataToJson(eventPayload))
                    .asJson();

            System.out.println(response.getBody());
            if (response.getStatus() == 200) {
                return true;
            }

        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return false;

    }
}

