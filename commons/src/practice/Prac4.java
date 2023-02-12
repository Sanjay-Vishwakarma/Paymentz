/*
package practice;*/
/*
package practice;

import com.directi.pg.PaymentzEncryptor;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.crypto.CipherText;
import org.owasp.esapi.crypto.PlainText;

import javax.crypto.SecretKey;

*//*


import com.directi.pg.Logger;
import com.directi.pg.PaymentzEncryptor;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.crypto.CipherText;
import org.owasp.esapi.crypto.PlainText;a

import javax.crypto.SecretKey;

*/
/**
 * Created by Admin on 6/26/2020.
 *//*

public class Prac4
{
    private static Logger logger = new Logger(Prac4.class.getName());

    String key =    PaymentzEncryptor.decryptPAN("ETLbewAAAWp9y5mvABRBRVMvQ0JDL1BLQ1M1UGFkZGluZwEAABAAEHF+IQsxvdlOhW4S3VdFPQoAAAAg0HHOnWBFi9oZlxxGtmlopNABHDWiZJlLOuY2br9U5kUAFHO/hIlwoxthj39wD2XhJtLNSMtu");

    //updateSecretKeyInDatabase(key);
    String ciper =  encryptString("Kumar");

    logger.debug("dencrypt Kumar ===   " + decryptString(ciper));
    try
    {
        logger.debug(" Secret key from DB ===   " + getSecretKey().getEncoded());

    }
    catch(Exception e)
    {
        logger.error(" Exception while getting secret key from DB ===   " + e);
    }
    private static String encryptString(String sPlainText)
    {
        String b64str = "";
        //Data Encryption Key from Database
        SecretKey dataEncryptKey = null;


        try
        {
            dataEncryptKey = getSecretKey();
        }
        catch (Exception e)
        {
            return b64str;
        }


        try
        {
            CipherText ct = ESAPI.encryptor().encrypt(dataEncryptKey, new PlainText(sPlainText));
            byte[] serializedCiphertext = ct.asPortableSerializedByteArray();
            b64str = ESAPI.encoder().encodeForBase64(serializedCiphertext, false);
        }
        catch (Exception e)
        {

            logger.error(org.owasp.esapi.Logger.SECURITY_FAILURE,"Encryption exception  thrown while encrypting data :  ",e);

        }
        return b64str;

    }

    private static String decryptString(String sCipherText)
    {

        SecretKey dataEncryptKey = null;
        //Data Encryption Key from Database


        try
        {
            dataEncryptKey = getSecretKey();
        }
        catch (Exception e)
        {
            logger.error(org.owasp.esapi.Logger.SECURITY_FAILURE, "Decryption Exception  thrown while fetching key from database  ", e);
        }


        PlainText plaintext = new PlainText("");

        try
        {
            byte[] serializedCiphertext = ESAPI.encoder().decodeFromBase64(sCipherText);
            CipherText restoredCipherText = CipherText.fromPortableSerializedBytes(serializedCiphertext);
            plaintext = ESAPI.encryptor().decrypt(dataEncryptKey, restoredCipherText);

        }
        catch (Exception e)
        {
            logger.error(org.owasp.esapi.Logger.SECURITY_FAILURE, "Decryption Exception  thrown while decrypting data  ", e);

        }
        return plaintext.toString();

    }

 }
*/
