package com.payment.paysec;

import com.payment.clearsettle.ClearSettleUtills;
import org.apache.commons.codec.digest.DigestUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.security.NoSuchAlgorithmException;

/**
 * Created by admin on 7/20/2018.
 */
public class PaySecTestManager
{
    public static void main(String[] args)
    {

        try
        {

            System.out.println("welcome");

            String sampleKey = "$2a$12$KQmhTM2bTR7iigEBhPYP3.";
            String str = "Transaction-ID-123453;10.00;CNY;c9884d45-bf55-4059-b0e0-7cc2fbb03fb4;3.0";

            String hash256 = hash256(str);
            String generatedHash = generateHash(hash256, sampleKey);

            String request = "{" +
                    "\"header\" : {" +
                    "\"version\" : \"3.0\"," +
                    "\"merchantCode\" : \"c9884d45-bf55-4059-b0e0-7cc2fbb03fb4\",\n" +
                    "\"signature\" : \"" + generatedHash + "\"\n" +
                    "}," +
                    "\"body\" : {" +
                    "\"channelCode\" : \"BANK_TRANSFER\"," +
                    "\"notifyURL\" : \"http://merchant.paysec.com/samples/notifyurl.asp\"," +
                    "\"returnURL\" : \"https://blue.pz.com/TestApp/redirecturl.jsp\"," +
                    "\"orderAmount\" : \"10.00\"," +
                    "\"orderTime\" : \"1509979555000\"," +
                    "\"cartId\" : \"Transaction-ID-123453\"," +
                    "\"currency\" : \"CNY\"" +
                    "}" +
                    "" +
                    "}";

           /* //String sendToken="{\n" +
                    "\"token\" :\n" +
                    "\"eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJwYXltZW50LnBheXNlYy5jb20iLCJzdWIiOiIxMjkwODg6bnVsbCIsImV4cCI6MjEyNDY4NzUxOCwiaWF0IjoxNTMyMDk0OTM4LCJqdGkiOiIwLjEifQ.SDJ5DDp2Tk34sFitrG9ItlKl4hmc6HzIH8OhTvyjsvCQJzGvUlGtu8GysCq3PViXGqSl6saoKfU3a_shGvQ4HA" +
                    "}";
*/


            ClearSettleUtills clearSettleUtills = new ClearSettleUtills();
            String response = clearSettleUtills.doPostHTTPSURLConnectionClient("https://pg-staging.paysec.com/Intrapay/paysec/v1/payIn/requestToken", request);
            //String response = clearSettleUtills.doPostHTTPSURLConnectionClient("https://pg-staging.paysec.com/Intrapay/paysec/v1/payIn/requestToken", request);
            System.out.println("response:::" + response);
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
    }

    static String hash256(String sampleStr) throws NoSuchAlgorithmException
    {
        String sha256hex = DigestUtils.sha256Hex(sampleStr);
        System.out.println("sha256Hex : " + sha256hex);
        return sha256hex;
    }

    public static String generateHash(String plainText, String key)
    {
        String hashValue = BCrypt.hashpw(plainText, key);
        hashValue = hashValue.replace(key, "");
        return (hashValue);
    }
}
