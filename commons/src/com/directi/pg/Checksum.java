package com.directi.pg;

import org.apache.log4j.Category;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Adler32;

public class Checksum
{
    static Category cat = Category.getInstance(Checksum.class.getName());
    private static Logger log = new Logger(Checksum.class.getName());

    @Deprecated
    public static String getChecksum(String str)
    {
        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        cat.debug("Checksum get    : " + str + " = " + adl.getValue());
        return String.valueOf(adl.getValue());
    }

    public static boolean verifyChecksum(String key, String checksum)
    {
        cat.debug("Entering verifyChecksum");
        String str = key;
        cat.debug("String : " + str);
        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        long adler = adl.getValue();
        cat.debug("Checksum verify    : " + key + " = " + adler);
        cat.debug("Leaving verifyChecksum");
        if (("" + adler).equals(checksum))
            return true;
        else
            return false;
    }

    private static String getChecksum(String memberId, String status, String message, String description, String amount, String key)
    {

        String str = memberId + "|" + status + "|" + message + "|" + description + "|" + amount + "|" + key;
        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        cat.debug("Checksum generated    : " + str + " = " + adl.getValue());
        return String.valueOf(adl.getValue());
    }

    private static String generateMD5ChecksumV1(String memberId, String status, String message, String description, String amount, String key) throws NoSuchAlgorithmException
    {
        cat.debug("Entering generateMD5ChecksumV1");
        String str = memberId + "|" + status + "|" + message + "|" + description + "|" + amount + "|" + key;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("Generated Checksum  : " + str + " = " + generatedCheckSum);
        cat.debug("Leaving generateMD5ChecksumV1");
        return generatedCheckSum;
    }

    public static String generateChecksumV1(String memberId, String status, String message, String description, String amount, String key, String algorithm) throws NoSuchAlgorithmException
    {

        if ("Adler32".equals(algorithm))
        {
            return getChecksum(memberId, status, message, description, amount, key);
        }
        else
        {
            return generateMD5ChecksumV1(memberId, status, message, description, amount, key);
        }

    }

    private static boolean verifyChecksum(String memberId, String status, String message, String description, String amount, String key, String checksum)
    {
        cat.debug("Entering verifyChecksum");
        String str = memberId + "|" + status + "|" + message + "|" + description + "|" + amount + "|" + key;
        cat.debug("String : " + str);
        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        long adler = adl.getValue();
        cat.debug("Checksum Match    : " );
        cat.debug("Leaving verifyChecksum");
        if (("" + adler).equals(checksum))
            return true;
        else
            return false;
    }

    private static boolean verifyMD5ChecksumV1(String memberId, String status, String message, String description, String amount, String key, String checksum) throws NoSuchAlgorithmException
    {
        cat.debug("Entering verifyMD5ChecksumV1");
        String str = memberId + "|" + status + "|" + message + "|" + description + "|" + amount + "|" + key;
        cat.debug("Stlring  : " + str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("Checksum generated    : ");
        cat.debug("Leaving verifyMD5ChecksumV1");
        return generatedCheckSum.equals(checksum);
    }

    private static boolean verifyMD5ChecksumV2(String memberId, String status, String message, String description, String key, String checksum) throws NoSuchAlgorithmException
    {
        cat.debug("Entering verifyMD5ChecksumV1");
        String str = memberId + "|" + status + "|" + message + "|" + description + "|" + key;
        cat.debug("Stlring  : " + str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("Checksum generated    : ");
        cat.debug("Leaving verifyMD5ChecksumV1");
        return generatedCheckSum.equals(checksum);
    }

    public static boolean verifyMD5GenerateInvoiceChecksumV2(String memberId, String key, String description, String amount, String redirectUrl, String quantityTotal, String checksum) throws NoSuchAlgorithmException
    {
        cat.debug("Entering verifyMD5GenerateInvoiceChecksumV2");
        String str = memberId + "|" + key + "|" + description + "|" + amount + "|" + redirectUrl;
        if(!"".equalsIgnoreCase(quantityTotal) && quantityTotal!=null)
            str+="|"+quantityTotal;
        cat.debug("Stlring  : " + str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("Checksum generated    : ");
        cat.debug("Leaving verifyMD5ChecksumV1");
        return generatedCheckSum.equals(checksum);
    }


    public static boolean verifyChecksumV1(String memberId, String status, String message, String description, String amount, String key, String checksum, String algorithm) throws NoSuchAlgorithmException
    {

        if ("Adler32".equals(algorithm))
        {
            return verifyChecksum(memberId, status, message, description, amount, key, checksum);
        }
        else
        {
            return verifyMD5ChecksumV1(memberId, status, message, description, amount, key, checksum);
        }
    }

    public static boolean verifyChecksumV3(String memberId, String status, String message, String description, String key, String checksum, String algorithm) throws NoSuchAlgorithmException
    {

        if ("Adler32".equals(algorithm))
        {
            return verifyChecksum(memberId, status, message, description, key, checksum);
        }
        else
        {
            return verifyMD5ChecksumV2(memberId, status, message, description, key, checksum);
        }
    }



    private static String getChecksum(String description, String amount, String status, String key)
    {
        String str = description + "|" + amount + "|" + status + "|" + key;
        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        cat.debug("Checksum get    : " );
        return String.valueOf(adl.getValue());
    }

    private static String getChecksumWithTwoParameters(String description, String status, String key)
    {
        String str = description + "|" + status + "|" + key;
        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        cat.debug("Checksum get    : " );
        return String.valueOf(adl.getValue());
    }

    private static String generateMD5ChecksumV2(String description, String status, String key) throws NoSuchAlgorithmException
    {
        String str = description + "|"+ status + "|" + key;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("Checksum generated    : " );
        return generatedCheckSum;
    }
    private static String generateMD5ChecksumV2(String description, String amount, String status, String key) throws NoSuchAlgorithmException
    {
        String str = description + "|" + amount + "|" + status + "|" + key;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("Checksum generated    : " );
        return generatedCheckSum;
    }
    private static String generateMD5ChecksumV2(String trackingId,String description, String amount, String status, String key) throws NoSuchAlgorithmException
    {
        String str = trackingId +"|"+ description + "|" + amount + "|" + status + "|" + key;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("Checksum generated :::"+str );
        return generatedCheckSum;
    }
    public static String generateChecksumV2(String description, String amount,String status, String key, String algorithm) throws NoSuchAlgorithmException
    {
        if ("Adler32".equals(algorithm))
        {
            return getChecksum(description, amount, status, key);
        }
        else
        {
            return generateMD5ChecksumV2(description, amount, status, key);
        }
    }
    public static String generateChecksumForFetchCardResponse(String description, String status, String key, String algorithm) throws NoSuchAlgorithmException
    {
        if ("Adler32".equals(algorithm))
        {
            return getChecksumWithTwoParameters(description, status, key);
        }
        else
        {
            return generateMD5ChecksumV2(description, status, key);
        }
    }
    public static String generateChecksumV3(String description, String amount, String status, String key, String algorithm) throws NoSuchAlgorithmException
    {
        if ("Adler32".equals(algorithm))
        {
            return getChecksum(description, amount, status, key);
        }
        else
        {
            return generateMD5ChecksumV2(description, amount, status, key);
        }
    }
    public static String generateChecksumForVOID(String description, String status, String key, String algorithm) throws NoSuchAlgorithmException
    {
        return generateMD5ChecksumV2(description, status, key);
    }
    public static String generateChecksumForCardRegistration(String token, String status, String key) throws NoSuchAlgorithmException
    {

        return generateMD5ChecksumV2(token, status, key);

    }
    public static String generateChecksumForStandardKit(String trackingId,String description, String amount, String status, String key) throws NoSuchAlgorithmException
    {
        return generateMD5ChecksumV2(trackingId,description, amount, status, key);
    }
    public static String generateChecksumMyMonedero(String trackingid,String ctoken)
    {
        String str=trackingid+"|"+ctoken;
        cat.debug("String : " + str);
        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        long adler = adl.getValue();

        return adler+"";
    }

    private static boolean verifyChecksum(String description, String amount, String status, String key, String checksum)
    {
        String str = description + "|" + amount + "|" + status + "|" + key;
        cat.debug("String : " + str);
        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        long adler = adl.getValue();
        cat.debug("Checksum verify    : " );
        if (("" + adler).equals(checksum))
            return true;
        else
            return false;
    }

    private static boolean verifyChecksum(String description, String amount, String status, String email, String key, String checksum)
    {
        String str = description + "|" + amount + "|" + status + "|" + email + "|" + key;
        cat.debug("String : " + str);
        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        long adler = adl.getValue();
        cat.debug("Checksum verify    : " );
        if (("" + adler).equals(checksum))
            return true;
        else
            return false;
    }

    private static boolean verifyMD5ChecksumV2(String description, String amount, String status, String key, String checksum) throws NoSuchAlgorithmException
    {
        cat.debug("toid---"+description+"key---"+amount+"orderid==="+status+"amount----"+key+"checksum----"+checksum);
        String str = description + "|" + amount + "|" + status + "|" + key;
        cat.debug("String :" + str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("Checksum verify    : "+generatedCheckSum);
        return generatedCheckSum.equals(checksum);
    }

    public static boolean verifyMD5ChecksumRest(String toid, String key, String orderId, String amount, String checksum) throws NoSuchAlgorithmException
    {
        cat.debug("toid---"+toid+"key---"+key+"orderid==="+orderId+"amount----"+amount+"checksum----"+checksum);
        String str = toid + "|" + key + "|" + orderId + "|" + amount;
        cat.debug("String :" + str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("Checksum verify    : "+generatedCheckSum);
        return generatedCheckSum.equals(checksum);
    }

    public static boolean verifyMD5ChecksumForMerchantSignup(String login, String key, String website, String firstName, String lastName, String partnerId, String checksum) throws NoSuchAlgorithmException
    {
        String str = login + "|" + key + "|" + website + "|" + firstName + "|" + lastName + "|" + partnerId;
        cat.debug("String :" + str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("Checksum verify    : "+generatedCheckSum);
        return generatedCheckSum.equals(checksum);
    }

    public static boolean verifyMD5ChecksumForMerchantSignup(String login, String key, String password, String partnerId, String checksum) throws NoSuchAlgorithmException
    {
        String str = login + "|" + key + "|" + password + "|" + partnerId;
        log.debug("String :" + str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        log.debug("Checksum verify    : "+generatedCheckSum);
        return generatedCheckSum.equals(checksum);
    }

    public static boolean verifyMD5ChecksumForMerchantCurrencies(String memberID, String key,String checksum) throws NoSuchAlgorithmException
    {
        String str = memberID + "|" + key;
        cat.debug("String :" + str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("Checksum verify    : "+generatedCheckSum);
        return generatedCheckSum.equals(checksum);
    }

    public static boolean verifyMD5ChecksumForMerchantCurrenciesInvoiceConfig(String memberID, String key,String url,String checksum) throws NoSuchAlgorithmException
    {
        String str = memberID + "|" + key +"|" +url;
        cat.debug("String :" + str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("Checksum verify    : "+generatedCheckSum);
        return generatedCheckSum.equals(checksum);
    }

    public static boolean verifyMD5ChecksumForMerchantChangePassword(String memberID, String key, String loginName, String checksum) throws NoSuchAlgorithmException
    {
        String str = memberID + "|" + key + "|" + loginName;
        cat.debug("String----"+str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedChecksum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("checksum verification----"+generatedChecksum);
        return generatedChecksum.equals(checksum);
    }


    public static boolean verifyMD5ChecksumForCardHolderRegistration(String partnerId, String key, String firstName, String lastName, String email, String checksum) throws NoSuchAlgorithmException
    {
        String str = partnerId + "|" + key + "|" + firstName + "|" + lastName + "|" + email;
        cat.debug("String :" + str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("Checksum verify    : "+generatedCheckSum);
        return generatedCheckSum.equals(checksum);
    }


    public static boolean verifyChecksumV3(String description, String key, String status,String checksum) throws NoSuchAlgorithmException
    {
        String str = description + "|" + key + "|" + status;
        log.debug("String :" + str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        log.debug("Checksum verify    : "+generatedCheckSum);
        return generatedCheckSum.equals(checksum);

    }
    public static boolean verifyChecksumV7(String memberID, String key, String invoiceAction, String checksum) throws NoSuchAlgorithmException
    {
        String str = memberID + "|" + key + "|" +invoiceAction;
        cat.debug("String :" + str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("Checksum verify    : "+generatedCheckSum);
        return generatedCheckSum.equals(checksum);

    }

    public static boolean verifyChecksumV2(String description, String amount, String status, String email, String key, String checksum, String algorithm) throws NoSuchAlgorithmException
    {
        if ("Adler32".equals(algorithm))
        {
            return verifyChecksum(description, amount, status, email, key, checksum);
        }
        else
        {
            return verifyMerchantSignUpChecksum(description, amount, status, email, key, checksum);
        }
    }

    public static boolean verifyChecksumV2(String description, String amount, String status, String key, String checksum, String algorithm) throws NoSuchAlgorithmException
    {
        if ("Adler32".equals(algorithm))
        {
            return verifyChecksum(description, amount, status, key, checksum);
        }
        else
        {
            return verifyMD5ChecksumV2(description, amount, status, key, checksum);
        }
    }

// added this method as we will now pass chargeamount to merchant who has upgraded kit to version 2.0

    private static String getChecksum(String memberId, String status, String message, String description, String amount, String chargeamount, String key)
    {
        String str = memberId + "|" + status + "|" + message + "|" + description + "|" + amount + "|" + chargeamount + "|" + key;
        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        cat.debug("Checksum get    : " );
        return String.valueOf(adl.getValue());
    }

    private static String generateMD5ChecksumV3(String memberId, String status, String message, String description, String amount, String chargeamount, String key) throws NoSuchAlgorithmException
    {
        String str = memberId + "|" + status + "|" + message + "|" + description + "|" + amount + "|" + chargeamount + "|" + key;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("Checksum generated    : ");
        return generatedCheckSum;
    }

    public static String generateChecksumV3(String memberId, String status, String message, String description, String amount, String chargeamount, String key, String algorithm) throws NoSuchAlgorithmException
    {
        if ("Adler32".equals(algorithm))
        {
            return getChecksum(memberId, status, message, description, amount, chargeamount, key);
        }
        else
        {
            return generateMD5ChecksumV3(memberId, status, message, description, amount, chargeamount, key);
        }
    }

    private static boolean verifyChecksum(String memberId, String status, String message, String description, String amount, String chargeamount, String key, String checksum)
    {
        String str = memberId + "|" + status + "|" + message + "|" + description + "|" + amount + "|" + chargeamount + "|" + key;
        cat.debug("String : " + str);
        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        long adler = adl.getValue();
        cat.debug("Checksum verify    : ");
        if (("" + adler).equals(checksum))
            return true;
        else
            return false;
    }

    private static boolean verifyMD5ChecksumV3(String memberId, String status, String message, String description, String amount, String chargeamount, String key, String checksum) throws NoSuchAlgorithmException
    {
        String str = memberId + "|" + status + "|" + message + "|" + description + "|" + amount + "|" + chargeamount + "|" + key;
        cat.debug("String : " + str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("Checksum verify    : " );
        return generatedCheckSum.equals(checksum);
    }

    public static boolean verifyChecksumV3(String memberId, String status, String message, String description, String amount, String chargeamount, String key, String checksum, String algorithm) throws NoSuchAlgorithmException
    {
        if ("Adler32".equals(algorithm))
        {
            return verifyChecksum(memberId, status, message, description, amount, chargeamount, key, checksum);
        }
        else
        {
            return verifyMD5ChecksumV3(memberId, status, message, description, amount, chargeamount, key, checksum);
        }
    }

    public static boolean verifyChecksumWithV3(String memberId, String cardholderId, String key, String checksum, String algorithm) throws NoSuchAlgorithmException
    {
        if ("Adler32".equals(algorithm))
        {
            return verifyChecksumV2(memberId, cardholderId, key, checksum);
        }
        else
        {
            return verifyChecksumV3(memberId, cardholderId, key, checksum);
        }
    }

    //new

    public static boolean verifyChecksumWithV4(String memberId, String key, String invoiceAction, String checksum) throws NoSuchAlgorithmException
    {

            return verifyChecksumV7(memberId, key,invoiceAction, checksum);

    }

//Use for merchant with version-2 which also receive chargeamount in query string

    private static String getChecksum(String description, String amount, String chargeamount, String status, String key, int random)
    {
        String str = description + "|" + amount + "|" + chargeamount + "|" + status + "|" + key + "|" + random;
        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        cat.debug("Checksum get    : " );
        return String.valueOf(adl.getValue());
    }

    private static String generateMD5ChecksumV4(String description, String amount, String chargeamount, String status, String key, int random) throws NoSuchAlgorithmException
    {
        String str = description + "|" + amount + "|" + chargeamount + "|" + status + "|" + key + "|" + random;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("Checksum generated    : " );
        return generatedCheckSum;
    }

    public static String generateChecksumV4(String description, String amount, String chargeamount, String status, String key, int random, String algorithm) throws NoSuchAlgorithmException
    {
        if ("Adler32".equals(algorithm))
        {
            return getChecksum(description, amount, chargeamount, status, key, random);
        }
        else
        {
            return generateMD5ChecksumV4(description, amount, chargeamount, status, key, random);
        }
    }


    public static String generateNewChecksum(String trackingId, String memberId) throws NoSuchAlgorithmException
    {

        String str = trackingId + "|" + memberId + "|" + "JLEIWxX8oYSgYrscmLwMBlZ0RF3x7DBy";
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("New Checksum generated    : " );
        return generatedCheckSum;

    }

    public static boolean verifyNewChecksum(String trackingId, String memberId, String checksum) throws NoSuchAlgorithmException
    {
        cat.debug("Checksum Received " );
        String str = trackingId + "|" + memberId + "|" + "JLEIWxX8oYSgYrscmLwMBlZ0RF3x7DBy";
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("New Checksum generated    : " );
        return generatedCheckSum.equals(checksum);
    }

    public static boolean verifyMerchantSignUpChecksum(String loginName, String website, String contactName, String partnerId,String clKey, String checksum) throws NoSuchAlgorithmException
    {
        String str = loginName + "|" + website + "|" + contactName+ "|" + partnerId+ "|" +clKey;
        cat.debug("String :" + str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("Checksum verify    : " + generatedCheckSum);
        return generatedCheckSum.equals(checksum);
    }

    public static boolean verifyMD5ChecksumForPayout(String memerId, String orderId, String amount, String key, String checksum) throws NoSuchAlgorithmException
    {
        String str = memerId + "|" + orderId + "|" + amount + "|" + key;
        log.debug("str===="+str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        log.debug("Checksum verify    : " + generatedCheckSum);
        log.debug("Checksum     : " + checksum);
        return generatedCheckSum.equals(checksum);
    }

    public static boolean verifyExchanger(String customerId, String memberId, String amount, String secretKey, String checksum)throws NoSuchAlgorithmException
    {
        //System.out.println("checksum---");
        String str = customerId+ "|" +memberId+ "|" +amount+ "|" +secretKey;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        log.debug("Checksum verify    : " + generatedCheckSum);
        return generatedCheckSum.equals(checksum);
    }

    public static String getMerchantSignUpResponseChecksum(String memberId, String status, String partnerId, String partnerSecretKey)throws NoSuchAlgorithmException
    {
        String str = memberId+ "|" +status+ "|" +partnerId+ "|" +partnerSecretKey;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("Checksum get    : " );
        return generatedCheckSum;
    }

    public static boolean verifyInvalidateTokenChecksum(String partnerId, String token, String key, String checksum, String algorithm) throws NoSuchAlgorithmException
    {
        if ("Adler32".equals(algorithm))
        {
            return verifyChecksumV2(partnerId, token, key, checksum);
        }
        else
        {
            return verifyMD5ChecksumV3(partnerId,token,key,checksum);
        }
    }
    private static boolean verifyChecksumV2(String toId, String token,String key, String checksum)
    {
        String str = toId + "|" + token + "|" + key;
        cat.debug("String : " + str);
        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        long adler = adl.getValue();
        cat.debug("Checksum verify    : " );
        if (("" + adler).equals(checksum))
            return true;
        else
            return false;
    }
    private static boolean verifyMD5ChecksumV3(String toId, String token,String key, String checksum) throws NoSuchAlgorithmException
    {

        String str = toId + "|" + token + "|" + key;
        cat.debug("String :" + str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("Checksum verify    : " + generatedCheckSum);
        return generatedCheckSum.equals(checksum);
    }


    public static boolean verifyChecksumV4(String userId, String key, String checksum, String algorithm) throws NoSuchAlgorithmException
    {

        if ("Adler32".equals(algorithm))
        {
            return verifyAdlerChecksumV4(userId, key, checksum);
        }
        else
        {
            return verifyMD5ChecksumV4(userId, key, checksum);
        }
    }

    private static boolean verifyAdlerChecksumV4(String userId, String key, String checksum)
    {
        cat.debug("Entering verifyChecksum");
        String str = userId + "|" + key;
        cat.debug("String : " + str);
        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        long adler = adl.getValue();
        cat.debug("Checksum Match    : " );
        cat.debug("Leaving verifyChecksum");
        if (("" + adler).equals(checksum))
            return true;
        else
            return false;
    }

    private static boolean verifyMD5ChecksumV4(String userId, String key, String checksum) throws NoSuchAlgorithmException
    {
        cat.debug("Entering verifyMD5ChecksumV1");
        String str = userId + "|" + key;
        cat.debug("Stlring  : " + str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        cat.debug("Checksum generated    : ");
        cat.debug("Leaving verifyMD5ChecksumV1");
        return generatedCheckSum.equals(checksum);
    }


    private static String getString(byte buf[])
    {
        StringBuffer sb = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++)
        {
            int h = (buf[i] & 0xf0) >> 4;
            int l = (buf[i] & 0x0f);
            sb.append(new Character((char) ((h > 9) ? 'a' + h - 10 : '0' + h)));
            sb.append(new Character((char) ((l > 9) ? 'a' + l - 10 : '0' + l)));
        }
        return sb.toString();
    }
    public static boolean verifyMD5ChecksumForMerchantSendReceiptEmail(String memberID, String key, String email, String checksum) throws NoSuchAlgorithmException
    {
        String str = memberID + "|" + key + "|" + email;
        log.debug("String :" + str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        log.debug("Checksum verify    : "+generatedCheckSum);
        return generatedCheckSum.equals(checksum);
    }

    public static boolean verifyMD5ChecksumForMerchantSendReceiptSms(String memberID, String key, String phone, String checksum) throws NoSuchAlgorithmException
    {
        String str = memberID + "|" + key + "|" + phone;
        log.debug("String :" + str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        log.debug("Checksum verify    : "+generatedCheckSum);
        return generatedCheckSum.equals(checksum);
    }


}

