    package practice;

    import com.manager.AESEncryptionManager;
    import com.payment.bhartiPay.BhartiPayUtils;
    import org.apache.commons.codec.binary.*;
    import org.apache.commons.httpclient.HttpClient;
    import org.apache.commons.httpclient.HttpException;
    import org.apache.commons.httpclient.methods.PostMethod;
    import sun.misc.BASE64Decoder;
    import sun.misc.BASE64Encoder;

/*import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;*/
    import java.io.IOException;
    import java.security.MessageDigest;
    import java.security.NoSuchAlgorithmException;
    import java.util.*;
//import java.util.Base64;



//import
    import org.apache.commons.codec.binary.Hex;
    import org.apache.commons.codec.binary.Base64;

    import javax.crypto.Cipher;
    import javax.crypto.SecretKey;
    import javax.crypto.spec.IvParameterSpec;
    import javax.crypto.spec.SecretKeySpec;
    import java.nio.charset.Charset;
    import java.util.HashMap;
    import java.util.Map;
    /**
     * Created by Admin on 12/24/2020.
     */
    public class Practice_02012021
    {
        private final static String separator       = "~";
        private final static String equator         = "=";
        private final static String hashingAlgo     = "SHA-256";
        final static String CryptoKey               = "B6B9B57E71DF7B52B17D191D36259E0B";
        private static Stack<MessageDigest> stack   = new Stack<MessageDigest>();
        String PG_SALT                              = "8535b0d335e545d4";
        private static final String key             = "B6B9B57E71DF7B52B17D191D36259E0B";
        private static final String initVector      = "B6B9B57E71DF7B52";
        private static final Charset ASCII          = Charset.forName("US-ASCII");
        public static void main(String[] args)
        {
            processS2SSale();
        }
        static void processS2SSale(){

            String PG_REQUEST_URL       = "https://uat.bhartipay.com/crm/jsp/hostedPaymentRequest";
            String PG_RESPONSE_URL      = "http://new.rs.bp.h.teamat.work/response.php";
            String PG_RESPONSE_MODE     = "SALE";
            // String returnURL            = "http://localhost:8081/transaction/BhartiPayFrontEndServlet?trackingId=";
            String returnURL            = "http://new.rs.bp.h.teamat.work/response.php";


            String pay_id               = "2001141020561000";
            String salt                 = "8535b0d335e545d4";
            String order_id             = "BHARTID3012201304";
            String cust_email           = "test@bhartipay.com";
            String cust_name            = "Test Merchant";
            String cust_street_address1 = "";
            String cust_city            = "";
            String cust_state           = "";
            String cust_country         = "";
            String cust_zip             = "";
            String cust_phone           = "9999999999";
            String currency_code        = "356";
            String amount               = "100";
            String product_desc         = "";
            String cust_ship_street_address1  = "";
            String cust_ship_city        = "";
            String cust_ship_state       = "";
            String cust_ship_country     = "";
            String cust_ship_zip         = "";
            String cust_ship_phone       = "";
            String cust_ship_name        = "";
            String txn_type              = "";
            String pg_request_url        = "";
            String payment_type          = "CARD";
            String mop_type              = "";
            String card_number           = "4000000000000002";
            String card_exp_dt           = "012021";
            String card_customer_name    = "test";
            String cvv                   = "375";

            String hash                  = "";
            String requestParameters     = "";
            String responseParameter     = "";
            String encryptString          = "";
            String dencryptString          = "";

            HashMap<String, String> hashMap  = new HashMap<String,String>();


            try
            {

                hashMap.put("PAY_ID", pay_id);
                hashMap.put("ORDER_ID", order_id);
                hashMap.put("RETURN_URL", returnURL);
                hashMap.put("CURRENCY_CODE", currency_code);
                hashMap.put("AMOUNT", amount);
                hashMap.put("PAYMENT_TYPE", payment_type);
                hashMap.put("CUST_NAME", card_customer_name);
                hashMap.put("CARD_NUMBER", card_number);
                hashMap.put("CUST_EMAIL", cust_email);
                hashMap.put("CUST_PHONE", cust_phone);
                hashMap.put("CARD_EXP_DT", card_exp_dt);
                hashMap.put("CVV", cvv);
                System.out.println("map ------> " + hashMap);



                hash = generateCheckSum(hashMap, salt);
                hashMap.put("HASH",hash);
                System.out.println("generate Hash ----------> " + hash);
                encryptString = generateEncryption(hashMap,hash);
                //encryptString = BhartiPayUtils.dencrypt("ztI1/JmCcdsAnpWz2cmeBGlxrAzdQ/rO/4z8QroPA92eW52OdcaxR66l++hUZ2PR2u49eECD+hGg6jN1TvueKm4CeyM+h3/wRsf9IKrJoZ/nO2GtyNMg0rvVxOLIeSeIQweoJOOc1/QYt1qYoE7RE3lYG2qHDylX6Q8zKbMJzj//oYp/ETR6RF/HkyKbJ6WQZXku/k87wU6/g3jx8D8NEW0kXpYuWR+NlsP0lVE68tfDgdBkTXqhJiQsnEQjTKFvT87h7fvtxXhZ6ZaZqGCfkGoUmFkF3KqiQKP3wcGa8DryoDmD4eSFziWk2z+twZ1onP+ZpBsNtk1vefkDXlXry57xHuYqnCpR2vnfdY4Dvye2oTB9CZgvJi/37mJ5HVorYFUPQc/VXwgX73qCb8D7qTGMNngEIlv0lNAl13SCri+rLXw/5+4O0aqpdDbjMhwESA2dc9KR3thppgOCIRSRobkZP8nJ8Q/WqVCyYAZ3q9EHo9BeCSObi/1BKlO/BMtkyjQzBhW5cjfNKagHwoZ8GdOvksemn1ayMEL2RriID5Cck2vxN7HAdANXc3H0sGflAywH60IsOD9obCMPqfznxYH/eagS9GLXeoGWm6liMRnH1rgzsbTNH+IexE6Gv9s2f9oeln/OGvwmEy8cWWjOZJ10wF1a22Tkw2jomztvvt6VDUV2/W+SQmKZlqb87hOyvvwcbR4OaS6PyOK2yX3BDeabhX8bBpGncqiV+/pQZTgrFTJoTpHTw4cWbfGzCgcUCpSofk72FpB0+P/7+vO74Q", key);
                System.out.println("before Encryption ----------> " + encryptString);
                //encryptString = encryption1(encryptString);
                // encryptString = encryption2(hash);
                //encryptString = encrypt3(hash);
                // encryptString = bharatiPayEncryption.encrypt("B6B9B57E71DF7B52","B6B9B57E71DF7B52",encryptString);
                //encryptString = openssl_encrypt(encryptString,"B6B9B57E71DF7B52B17D191D36259E0B", "B6B9B57E71DF7B52B17D191D36259E0B");
                encryptString = encrypt7(encryptString, key);
                //encryptString = BhartiPayUtils.encrypt(encryptString, key);

                //encryptString = encrypt5(encryptString);
                System.out.println("after Encryption ----------> "+encryptString);
                //System.out.println("after Encryption ----------> "+encryptString.split(":")[1]);

                hashMap  = new HashMap<String,String>();
                hashMap.put("PAY_ID", pay_id);
                hashMap.put("ENCDATA",encryptString);
                //System.out.println("final hashMap  ----------> "+hashMap);

                //dencryptString = decryption1(encryptString);
                // dencryptString = bharatiPayEncryption.decrypt("B6B9B57E71DF7B52","ztI1/JmCcdsAnpWz2cmeBNi6nN1XUeCg7iNRaUC5F6jkAszkCv/MGYY/2+Rsp2iCj4I7/SRmmfsxOd9xDlBum/oRjzVu14CSpksMkqKGvma43XFD67qRghWcb1Sro+gKWW86eBsvP/uvbBtW7iGUalyTNS6EumTKX9gara5z6WZqEY3WFCXHz7d4mnQ0sT+XjoJkaga0uAJ/0PWLg9Q+L5P62TCKtsQnE+XI1gG6r7pKJVZieL0U66djZsb+4+ZDaOfNBJkRSVYpGlj3UvEb6+84ozDLmMXgc/RX7j3cM6wnf7KYfQb3JoS2vX78ChiW8Z4vaZ+Rl4kR+f4C0zV7XHgHNj5iTFlqu9UrKXNBKJEQqytI/9I1CyeEI7GP4SNwxPiIeDvMadONJa0DUSz8ZNXRgRgXiMOkZyo5jkIn0ynA2u0gA91X7CwcuA3CyNB0JvP7n9z8zSq58/Yu2YUJBmRAGKiv0aQjI2cSp4UKCMU");
                //dencryptString = decrypt5("aVFLTBaBxCdF45alCHeeeyOayleGfCEwbtUayZVaWmx3VH32SXZGg5qBZ14f1Yiwv+15ToGnRsjtMjVOPn5R3tKc0bQXDx0h1aVoTCSRGlCCPJbbo/SWMEjUKAOsyoFJ2LhcaNj3PX/Jf8wrclKIBqrwyOcGqd9jqP3mnTbFlaNq0VlE+TrFMMoUHUfIMOvzH1albIfCvdyu7BMelIJwk162MHi9lsH2MkCfh8wosf5sk8msu/xLE2IsUPA6b7PTkNkEZkm1jwfD7j5tINUJqPEnmfT8MDH76eLwEQiKIOPtkTMZ939BPBZNy2lrBiZ/IAz0rxDH1D2sVUa/SKFIjoybKGIKRl3M6DdKeqBQuK9DblJoBMGJDHQW+hFXgLu6jtbpbMxoxvVu8rzMaZ+XBoi2cPZ7NXDfBZMIYIXy+D5psFaoYW7bd33cqjeHI+CphlcV9ZzRObsN61xwbBI8A5ngb3Bj+joYQR4+vk9Agnw=");
                //decrypt6(encryptString);
                dencryptString   = dencrypt7(encryptString, key);
                // dencryptString   = BhartiPayUtils.dencrypt(encryptString, key);
                System.out.println("after DEncryption ----------> " + dencryptString);

                String requestForm = getConfirmationForm(hashMap,PG_REQUEST_URL);
                //System.out.println("requestForm ----------> "+requestForm);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        public  static String encrypt7(String data,String key){
            /* String plainCipher = hashString;*/
            //  String plainCipher = "amount=100~card_exp_dt=012021~card_number=4000000000000002~currency_code=356~cust_email=test@bhartipay.com~CUST_NAME=Test Merchant~CUST_PHONE=9999999999~CVV=123~ORDER_ID=BHARTID3012201304~PAYMENT_TYPE=CARD~PAY_ID=2001141020561000~RETURN_URL=http://new.rs.bp.h.teamat.work/response.php~HASH=3B4A0992F799FC857F121C8515E06FCAF8AD4C9BCC6CAA82002899406E84CACA";

            String ciphertext ="";
            try{

                byte [] iv           = "B6B9B57E71DF7B52".getBytes(ASCII);
                byte [] keyBytes    = "B6B9B57E71DF7B52B17D191D36259E0B".getBytes(ASCII);

                System.out.println("iv--->"+iv.length);
                System.out.println("keyBytes--->"+keyBytes.length);

                SecretKey aesKey    = new SecretKeySpec(keyBytes, "AES");

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));

                byte[] result   = cipher.doFinal(data.getBytes());

                ciphertext = new String(Base64.encodeBase64(result));

                System.out.println("ciphertext--->"+ciphertext);

            }catch (Exception e){
                e.printStackTrace();
            }
            return  ciphertext;
        }
        public  static String dencrypt7(String data,String key){
            // data = "ztI1/JmCcdsAnpWz2cmeBGlxrAzdQ/rO/4z8QroPA92eW52OdcaxR66l++hUZ2PR2u49eECD+hGg6jN1TvueKm4CeyM+h3/wRsf9IKrJoZ/nO2GtyNMg0rvVxOLIeSeIQweoJOOc1/QYt1qYoE7RE3lYG2qHDylX6Q8zKbMJzj//oYp/ETR6RF/HkyKbJ6WQZXku/k87wU6/g3jx8D8NEW0kXpYuWR+NlsP0lVE68tfDgdBkTXqhJiQsnEQjTKFvT87h7fvtxXhZ6ZaZqGCfkGoUmFkF3KqiQKP3wcGa8DryoDmD4eSFziWk2z+twZ1onP+ZpBsNtk1vefkDXlXry57xHuYqnCpR2vnfdY4Dvye2oTB9CZgvJi/37mJ5HVorYFUPQc/VXwgX73qCb8D7qTGMNngEIlv0lNAl13SCri+rLXw/5+4O0aqpdDbjMhwESA2dc9KR3thppgOCIRSRobkZP8nJ8Q/WqVCyYAZ3q9EHo9BeCSObi/1BKlO/BMtkyjQzBhW5cjfNKagHwoZ8GdOvksemn1ayMEL2RriID5Cck2vxN7HAdANXc3H0sGflAywH60IsOD9obCMPqfznxYH/eagS9GLXeoGWm6liMRnH1rgzsbTNH+IexE6Gv9s2f9oeln/OGvwmEy8cWWjOZJ10wF1a22Tkw2jomztvvt6VDUV2/W+SQmKZlqb87hOyvvwcbR4OaS6PyOK2yX3BDeabhX8bBpGncqiV+/pQZTgrFTJoTpHTw4cWbfGzCgcUCpSofk72FpB0+P/7+vO74Q";
            String finalresult = "";
            try{
                byte [] cipherBytes = Base64.decodeBase64(data);
                byte [] iv          = "B6B9B57E71DF7B52".getBytes(ASCII);
                byte [] keyBytes    = "B6B9B57E71DF7B52B17D191D36259E0B".getBytes(ASCII);

                SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");

                Cipher cipher = Cipher.getInstance("AES/CBC/NOPADDING");
                cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));

                byte[] result = cipher.doFinal(cipherBytes);

                finalresult     = new String(result);


            }catch (Exception e){
                e.printStackTrace();
            }
            return  finalresult;
        }

        public static String encrypt5(String value) {
            try {
                IvParameterSpec iv      = new IvParameterSpec(initVector.getBytes("UTF-16"));
                SecretKeySpec skeySpec  = new SecretKeySpec(key.getBytes("UTF-16"), "AES");

                Cipher cipher   = Cipher.getInstance("AES/CBC/NOPADDING");
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

                byte[] encrypted = cipher.doFinal(value.getBytes());
                //return Base64.getEncoder().encodeToString(encrypted);
                return Base64.encodeBase64String(encrypted);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        public static String encrypt6(String encrypted) {

            try{
                String plainCipher  = "amount=100~card_exp_dt=012021~card_number=4000000000000002~currency_code=356~cust_email=test@bhartipay.com~CUST_NAME=Test Merchant~CUST_PHONE=9999999999~CVV=123~ORDER_ID=BHARTID3012201304~PAYMENT_TYPE=CARD~PAY_ID=2001141020561000~RETURN_URL=http://new.rs.bp.h.teamat.work/response.php~HASH=3B4A0992F799FC857F121C8515E06FCAF8AD4C9BCC6CAA82002899406E84CACA";
                byte [] iv          = "B6B9B57E71DF7B52".getBytes("UTF-8");
                byte [] keyBytes    = "B6B9B57E71DF7B52B17D191D36259E0B".getBytes("UTF-8");

                SecretKey aesKey    = new SecretKeySpec(keyBytes, "AES");
                Cipher cipher       = Cipher.getInstance("AES/CBC/PKCS5PADDING");

                cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));

                byte[] result = cipher.doFinal(encrypted.getBytes());
                // System.out.println(Hex.encodeHexString(result));
                String ciphertext = new String(Base64.encodeBase64(result));

                System.out.println("ciphertext--->"+ciphertext);
                //   String decodedText = com.sun.xml.internal.messaging.saaj.util.Base64.base64Decode(String.);
            }catch (Exception e){
                e.printStackTrace();
            }
            return  "";

        }
        public static String decrypt6(String encrypted) {
            try{
                String base64Cipher = "ztI1/JmCcdsAnpWz2cmeBGlxrAzdQ/rO/4z8QroPA92eW52OdcaxR66l++hUZ2PR2u49eECD+hGg6jN1TvueKm4CeyM+h3/wRsf9IKrJoZ/nO2GtyNMg0rvVxOLIeSeIQweoJOOc1/QYt1qYoE7RE3lYG2qHDylX6Q8zKbMJzj//oYp/ETR6RF/HkyKbJ6WQZXku/k87wU6/g3jx8D8NEW0kXpYuWR+NlsP0lVE68tfDgdBkTXqhJiQsnEQjTKFvT87h7fvtxXhZ6ZaZqGCfkGoUmFkF3KqiQKP3wcGa8DryoDmD4eSFziWk2z+twZ1onP+ZpBsNtk1vefkDXlXry57xHuYqnCpR2vnfdY4Dvye2oTB9CZgvJi/37mJ5HVorYFUPQc/VXwgX73qCb8D7qTGMNngEIlv0lNAl13SCri+rLXw/5+4O0aqpdDbjMhwESA2dc9KR3thppgOCIRSRobkZP8nJ8Q/WqVCyYAZ3q9EHo9BeCSObi/1BKlO/BMtkyjQzBhW5cjfNKagHwoZ8GdOvksemn1ayMEL2RriID5Cck2vxN7HAdANXc3H0sGflAywH60IsOD9obCMPqfznxYH/eagS9GLXeoGWm6liMRnH1rgzsbTNH+IexE6Gv9s2f9oeln/OGvwmEy8cWWjOZJ10wF1a22Tkw2jomztvvt6VDUV2/W+SQmKZlqb87hOyvvwcbR4OaS6PyOK2yX3BDeabhX8bBpGncqiV+/pQZTgrFTJoTpHTw4cWbfGzCgcUCpSofk72FpB0+P/7+vO74Q";
                byte [] cipherBytes     = Base64.decodeBase64(base64Cipher);
                byte [] iv          = "B6B9B57E71DF7B52".getBytes("US-ASCII");
                byte [] keyBytes    = "B6B9B57E71DF7B52B17D191D36259E0B".getBytes("US-ASCII");

                SecretKey aesKey    = new SecretKeySpec(keyBytes, "AES");

                Cipher cipher       = Cipher.getInstance("AES/CBC/NOPADDING");
                cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));

                byte[] result = cipher.doFinal(cipherBytes);
                System.out.println(Hex.encodeHexString(result));
                String finalresult= new String(result);
                System.out.println("finalresult--->"+finalresult);
            }catch (Exception e){
                e.printStackTrace();
            }
            return "";
        }
        public static String decrypt5(String encrypted) {
            try {
                IvParameterSpec iv      = new IvParameterSpec(initVector.getBytes("UTF-8"));
                SecretKeySpec skeySpec  = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
                byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

                return new String(original);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }
        public static String openssl_encrypt(String data, String strKey, String strIv) throws Exception {

            Cipher ciper        = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec key   = new SecretKeySpec(strKey.getBytes(), "AES");
            IvParameterSpec iv  = new IvParameterSpec(strIv.getBytes(), 0, ciper.getBlockSize());

            // Encrypt
            ciper.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] encryptedCiperBytes = ciper.doFinal(data.getBytes());

            String s = new String(encryptedCiperBytes);
            System.out.println("Ciper : " + s);
            return s;
        }
        public  static String encryption4(String hashString){
            String secretKey            = "B6B9B57E71DF7B52B17D191D36259E0B";
            String initialVectorString  = secretKey.substring(0,16);
            String encryptedData        = "";
            try
            {
                final SecretKeySpec skeySpec            = new SecretKeySpec(initialVectorString.getBytes(), "AES");
                final IvParameterSpec initialVector     = new IvParameterSpec(initialVectorString.getBytes());
                final Cipher cipher                     = Cipher.getInstance("AES/CFB8/NoPadding");

                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, initialVector);

                final byte[] encryptedByteArray = cipher.doFinal(hashString.getBytes());
                // Encode using Base64
                //encryptedData = (new BASE64Encoder()).encode(encryptedByteArray);

                encryptedData = Base64.encodeBase64String(cipher.doFinal(encryptedData.getBytes("UTF-8")));
            }catch (Exception e){
                e.printStackTrace();
            }
            return encryptedData;
        }

        public static String encryption3(String strToEncrypt)
        {
            String secret            = "B6B9B57E71DF7B52B17D191D36259E0B";
            try
            {
                String iv       = secret.substring(0,16);
                String iv1      = "B6B9B57E71DF7B52B17D191D36259E0B";

                System.out.println("iv1.getBytes() " + iv1.getBytes());
                System.out.println("secret.getBytes() " +secret.getBytes());
                IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());


                SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "AES");

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
                return Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
            }
            catch (Exception e)
            {
                System.out.println("Error while encrypting: " + e.toString());
            }
            return null;
        }



        public  static String encryption2(String hashString){
            String iv = "1234567812345678";

            //AES/CBC/PKCS5Padding defaults to PHP: AES-128-CBC
            String CBC_PKCS5_PADDING    = "AES/CBC/PKCS5Padding";

            String AES                  = "AES";
            String secretKey            = "B6B9B57E71DF7B52B17D191D36259E0B";
            iv                          = secretKey.substring(0,16);
            try{
                Cipher cipher                   = Cipher.getInstance(CBC_PKCS5_PADDING);
                SecretKeySpec keyspec           = new SecretKeySpec(secretKey.getBytes(), AES);
                IvParameterSpec ivspec          = new IvParameterSpec(iv.getBytes());

                cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

                byte[] encrypted = cipher.doFinal(hashString.getBytes());

                //base64 encoding
                //return Base64Encoder.encode(encrypted);
                return (new BASE64Encoder()).encode(encrypted).toString();
            }catch (Exception e){
                e.printStackTrace();
            }
            return "";
        }
        public  static String encryption1(String hashString){
            String secretKey            = "B6B9B57E71DF7B52B17D191D36259E0B";
            String initialVectorString  = secretKey.substring(0,16);
            String encryptedData        = "";
            try
            {
                final SecretKeySpec skeySpec            = new SecretKeySpec(initialVectorString.getBytes(), "AES");
                final IvParameterSpec initialVector     = new IvParameterSpec(initialVectorString.getBytes());
                final Cipher cipher                     = Cipher.getInstance("AES/CFB8/NoPadding");

                //cipher.init(Cipher.ENCRYPT_MODE, skeySpec, initialVector);
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, initialVector);

                final byte[] encryptedByteArray = cipher.doFinal(hashString.getBytes());
                // Encode using Base64
                encryptedData = (new BASE64Encoder()).encode(encryptedByteArray);
            }catch (Exception e){
                e.printStackTrace();
            }
            return encryptedData;
        }
        public static String decryption2(String strToDecrypt) {
            String secret            = "B6B9B57E71DF7B52B17D191D36259E0B";
            try
            {
                String iv = secret.substring(0,16);
                IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

                SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "AES");

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
                return new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt)));
            }
            catch (Exception e) {
                System.out.println("Error while decrypting: " + e.toString());
            }
            return null;
        }
        public static String decryption1(String hastString){
            String secretKey            = "B6B9B57E71DF7B52B17D191D36259E0B";
            String initialVectorString  = secretKey.substring(0,16);
            String decryptedData        = "";
            try{
                final SecretKeySpec skeySpec        = new SecretKeySpec(initialVectorString.getBytes(), "AES");
                final IvParameterSpec initialVector = new IvParameterSpec(initialVectorString.getBytes());
                final Cipher cipher                 = Cipher.getInstance("AES/CFB8/NoPadding");
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, initialVector);

                final byte[] encryptedByteArray = (new BASE64Decoder()).decodeBuffer(hastString);
                // Decrypt the data
                final byte[] decryptedByteArray = cipher.doFinal(encryptedByteArray);
                decryptedData = new String(decryptedByteArray, "UTF8");
            }catch (Exception e){

            }
            return decryptedData;
        }


        public static  String getConfirmationForm(HashMap<String, String> map,String requestUrl){


            String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +requestUrl+ "\">" ;
            Iterator keys   = map.keySet().iterator();
            while (keys.hasNext())
            {
                String key      = (String) keys.next();
                form           += "<input type=\"hidden\" name=\""+key+"\"  value=\""+map.get(key)+"\">"+"\n";

            }
            form  += "</form>"+"\n"+"<script language=\"javascript\">document.creditcard_checkout.submit();</script>";


            return form.toString();
        }

        static void processSale(){

            String returnURL        = "http://localhost:8081/transaction/BhartiPayFrontEndServlet?trackingId=";
            String requestURL       = "https://uat.bhartipay.com/crm/jsp/paymentrequest";

            String orderId          = "ESN78452";
            String payId            = "";
            String paymentType      = "CC";
            String amount           = "10.00";
            String cardNumber      = "4000000000000002";
            String cardExpDate      = "12"+"12"+"22";
            String cvv              = "475";
            String currencyCode     = "356";
            String txnType          = "SALE";
            String salt             = "";
            String discrtiption     = "CD Player";
            String custEmail        = "abca@gmail.com";
            String custName         = "John Pal";
            String requestParameters        = "";
            String responseParameter        = "";
            String hash                     = "";
            String processorName            = "";
            String payment_url              = "";
            String MOP_TYPE                 = "";


            HashMap<String, String> map = null;

            try{
                map = new HashMap<String,String>();

                map.put("AMOUNT",amount);
                map.put("CURRENCY_CODE", currencyCode);
                map.put("CUST_EMAIL", custEmail);
                map.put("CUST_NAME",custName );
                map.put("ORDER_ID", orderId);// rrn
                map.put("PAY_ID", payId);// static
                map.put("MERCHANT_PAYMENT_TYPE", paymentType);
                map.put("RETURN_URL", returnURL);// callback -
                map.put("TXNTYPE",txnType);//
                map.put("CARD_NUMBER",cardNumber);
                map.put("CARD_EXP_DT",cardExpDate);
                map.put("CVV",cvv);
                map.put("PRODUCT_DESC",discrtiption);

                hash  = generateCheckSum(map, salt);// static
                map.put("HASH",hash);
                System.out.println("HashMap ---------> "+map);

                requestParameters = "AMOUNT="+amount+"&"
                        +"CURRENCY_CODE="+currencyCode+"&"
                        +"CUST_EMAIL="+custEmail+"&"
                        +"CUST_NAME="+custName+"&"
                        +"ORDER_ID="+orderId+"&"
                        +"PAY_ID="+payId+"&"
                        +"MERCHANT_PAYMENT_TYPE="+paymentType+"&"
                        +"RETURN_URL="+returnURL+"&"
                        +"TXNTYPE="+txnType+"&"
                        +"HASH="+hash+"&"
                        +"CARD_NUMBER"+cardNumber+"&"
                        +"CARD_EXP_DT"+cardExpDate+"&"
                        +"CVV"+cvv+"&"
                        +"PRODUCT_DESC"+discrtiption;

                System.out.println("responseParameter ---------> "+requestParameters);
                //responseParameter = doHttpPostConnection(requestParameters,requestURL);
                System.out.println("responseParameter ---------> "+responseParameter);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        public static String generateCheckSum(HashMap<String, String> parameters, String secretKey) throws NoSuchAlgorithmException
        {

            Map<String, String> treeMap = new TreeMap<String, String>(parameters);

            StringBuilder allFields = new StringBuilder();

            for (String key : treeMap.keySet()) {

                allFields.append(separator);
                allFields.append(key);
                allFields.append(equator);
                allFields.append(treeMap.get(key));
            }

            allFields.deleteCharAt(0); // Remove first FIELD_SEPARATOR
            allFields.append(secretKey);
            System.out.println("generateCheckSum  ----->> "+allFields.toString());
            return getHash(allFields.toString());
        }

        public static String generateEncryption(HashMap<String, String> parameters, String hashValues) throws NoSuchAlgorithmException
        {

            Map<String, String> treeMap = new TreeMap<String, String>(parameters);

            StringBuilder allFields = new StringBuilder();

            for (String key : treeMap.keySet()) {

                allFields.append(separator);
                allFields.append(key);
                allFields.append(equator);
                allFields.append(treeMap.get(key));
            }

            allFields.deleteCharAt(0); // Remove first FIELD_SEPARATOR
            allFields.append(separator);
            allFields.append("HASH");
            allFields.append(equator);
            allFields.append(hashValues);
            System.out.println("generateEncryption ----->> "+allFields.toString());
            return allFields.toString();
        }

        public static String getHash(String input) throws NoSuchAlgorithmException {
            String response = null;

            MessageDigest messageDigest = provide();
            messageDigest.update(input.getBytes());
            consume(messageDigest);

            response = new String(Hex.encodeHex(messageDigest.digest()));

            return response.toUpperCase();
        }

        private static MessageDigest provide() throws NoSuchAlgorithmException {
            MessageDigest digest = null;

            try {
                digest = stack.pop();
            } catch (EmptyStackException emptyStackException) {
                digest = MessageDigest.getInstance(hashingAlgo);
            }
            return digest;
        }

        private static void consume(MessageDigest digest) {
            stack.push(digest);
        }

        public static String doHttpPostConnection(String url, String request)
        {
            String result   = "";
            PostMethod post = new PostMethod(url);
            try
            {
                HttpClient httpClient = new HttpClient();
                post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                post.setRequestBody(request);
                httpClient.executeMethod(post);
                String response = new String(post.getResponseBody());
                result = response;

            }
            catch (HttpException e)
            {
                System.out.println("HttpException --->" + e);
            }
            catch (IOException e)
            {
                System.out.println("IOException --->" + e);
            }
            return result;
        }

    }


