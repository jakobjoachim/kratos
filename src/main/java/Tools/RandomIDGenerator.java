package Tools;

import java.security.SecureRandom;
import java.math.BigInteger;

public final class RandomIDGenerator {
    private static SecureRandom random = new SecureRandom();

    public static String nextId() {
        return new BigInteger(130, random).toString(32);
    }
}
