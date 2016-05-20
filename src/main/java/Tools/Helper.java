package Tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
}

