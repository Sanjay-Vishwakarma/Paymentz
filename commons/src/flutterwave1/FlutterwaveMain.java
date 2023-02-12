package flutterwave1;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * Created by Admin on 1/24/2020.
 */
public class FlutterwaveMain
{
    public static void main(String[] args)
    {
sale();

    }
    public static void  sale()
    {
        String publickey = "FLWPUBK_TEST-f01c74964dd47f882a0ca80645a905d0-X";
        String request = "{" +
                "\"amount\": 1000," +
                "\"PBFPubKey\":\" " + publickey + "\"," +
                "\"currency\":\"NGN\"," +
                "\"country\":\"NG\"," +
                "\"email\":\"test@example.com\"," +
                "\"txRef\":\"FX_XXXXXXXXX_XXXX_XX\"," +
                "\"meta\": [" +
                "{" +
                "\"metaname\":\"flight_id\"," +
                "\"metavalue\":\"LH0568\"" +
                "}" +
                "]," +
                "\"subaccounts\": null," +
                "\"is_barter\": 1," +
                "\"payment_type\":\"barter\"," +
                "\"payment_page\": null," +
                "\"campaign_id\": null," +
                "\"redirect_url\":\"https://redirect.mercchant.com/pay/callback\"," +
                "\"device_fingerprint\":\"ea466c56bd03366bae5f300ac1d42fe\"," +
                "\"ip\": \"123.40.56.189\"," +
                "\"firstname\":\"John\"," +
                "\"lastname\":\"Doe\"," +
                "\"charge_type\": null," +
                "\"cycle\": null," +
                "\"phonenumber\":\"08031112222\"" +
                "}";
        System.out.println("1st request---" + request);

        String key ="FLWSECK_TEST-daa712a7f495b5ea334d1864de1d3ea4-X"; //Secret Key
        String encryptedKey = getKey(key);
        String encryptData = encryptData(request, encryptedKey);
        System.out.println(encryptData);
    }


    public static String getKey(String seedKey)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] hashedString = md.digest(seedKey.getBytes("utf-8"));
            byte[] subHashString = toHexStr(Arrays.copyOfRange(hashedString, hashedString.length - 12, hashedString.length)).getBytes("utf-8");
            String subSeedKey = seedKey.replace("FLWSECK-", "");
            subSeedKey = subSeedKey.substring(0,12);   // subSeedKey is the key
            byte[] combineArray = new byte[24];
            System.arraycopy(subSeedKey.getBytes(), 0, combineArray, 0, 12);
            System.arraycopy(subHashString, subHashString.length - 12, combineArray, 12, 12);
            return new String(combineArray);
        }
        catch (NoSuchAlgorithmException ex)
        {
            System.out.println("Exception in FlwBarterUtils :: " + ex);
        }
        catch (UnsupportedEncodingException ex)
        {
            System.out.println("Exception in FlwBarterUtils :: " + ex);
        }
        return null;
    }
    public static String encryptData(String message, String _encryptionKey)
    {
        try
        {
            final byte[] digestOfPassword = _encryptionKey.getBytes("utf-8");
            final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

            final SecretKey key = new SecretKeySpec( keyBytes , "DESede");
            final Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final byte[] plainTextBytes = message.getBytes("utf-8");
            final byte[] cipherText = cipher.doFinal(plainTextBytes);
            return Base64.getEncoder().encodeToString(cipherText);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }
    public static String toHexStr(byte[] bytes)
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++)
        {
            builder.append(String.format("%02x", bytes[i]));
        }
        return builder.toString();
    }


    }

