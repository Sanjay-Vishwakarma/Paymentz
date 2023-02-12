package com.directi.pg;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.crypto.CipherText;
import org.owasp.esapi.crypto.CryptoHelper;
import org.owasp.esapi.crypto.PlainText;
import org.owasp.esapi.errors.EncryptionException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Jul 11, 2012
 * Time: 9:38:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaymentzEncryptor
{


    /** The logger used by the class. */
    private static org.owasp.esapi.Logger logger = ESAPI.getLogger("PaymentzEncryptor");
    private static SecretKey secretKey =null;


    public static void main(String[] args)
    {
        Logger logger =new Logger(PaymentzEncryptor.class.getName());

        String key = generateSecretKey();
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

    }
    public static String hashExpiryDate(String sPlainText, String memberid) throws EncryptionException
    {
        logger.info(org.owasp.esapi.Logger.SECURITY_AUDIT, "Ecrypting the Expiry Date ");
        return ESAPI.encryptor().hash(sPlainText,memberid);
    }
    public static String encryptExpiryDate(String sPlainText)
    {
        logger.info(org.owasp.esapi.Logger.SECURITY_AUDIT, "Ecrypting the Expiry Date ");
       return encryptString(sPlainText);
    }
    public static String encryptName(String sPlainText)
    {
        logger.info(org.owasp.esapi.Logger.SECURITY_AUDIT, "Ecrypting the Card holder Name ");

        return encryptString(sPlainText);
    }

    public static String encryptCVV(String sPlainText)
    {
        logger.info(org.owasp.esapi.Logger.SECURITY_AUDIT, "Ecrypting the CVV ");

        return encryptString(sPlainText);
    }

    public static String decryptExpiryDate(String sCipherText)
    {
        logger.info(org.owasp.esapi.Logger.SECURITY_AUDIT, "Decrypting the Expiry Date ");
        return decryptString(sCipherText);
    }

    public static String decryptName(String sCipherText)
    {
        logger.info(org.owasp.esapi.Logger.SECURITY_AUDIT, "Decrypting the Card holder Name ");
        return decryptString(sCipherText);
    }

    public static String decryptCVV(String sCipherText)
    {
        logger.info(org.owasp.esapi.Logger.SECURITY_AUDIT, "Decrypting the CVV ");
        return decryptString(sCipherText);
    }

    public static String encryptPAN(String sPlainText)
    {
        logger.info(org.owasp.esapi.Logger.SECURITY_AUDIT, "Ecrypting the PAN ");

        return encryptString(sPlainText);
    }

    public static String decryptPAN(String sCipherText)
    {

        logger.info(org.owasp.esapi.Logger.SECURITY_AUDIT, "Decrypting the PAN ");
        return decryptString(sCipherText);

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


    private static SecretKey getSecretKey() throws Exception
    {
        logger.info(org.owasp.esapi.Logger.SECURITY_AUDIT, "Fetching Key from Database");

        String secretkey = "";
        Connection conn = null;
        //Getting login name from member table

        if (secretKey == null)
        {
            try
            {
                conn = Database.getConnection();
                String query2 = "select encryptkey from datakey where id = ?";
                PreparedStatement pstmt2 = conn.prepareStatement(query2);
                pstmt2.setInt(1, 1);

                ResultSet rs = pstmt2.executeQuery();
                if (rs.next())
                {
                    secretkey = rs.getString("encryptkey");

                }

            }
            catch (SystemError se)
            {
                logger.error(org.owasp.esapi.Logger.SECURITY_FAILURE, "Decryption Exception  thrown while fetching key from Database  ", se);
                throw se;
            }
            catch (Exception e)
            {
                logger.error(org.owasp.esapi.Logger.SECURITY_FAILURE, "Decryption Exception  thrown while fetching key from Database  ", e);
                throw e;
            }
            finally
            {
                Database.closeConnection(conn);
            }

        }
        else
        {
            return secretKey;
        }

        PlainText plaintext = new PlainText("");

        try
        {
            byte[] serializedCiphertext = ESAPI.encoder().decodeFromBase64(secretkey);
            //logger.info(org.owasp.esapi.Logger.SECURITY_AUDIT, "Get Secret key : Secret Key Length after de serialization  :: "+ serializedCiphertext.length*8);
            CipherText restoredCipherText = CipherText.fromPortableSerializedBytes(serializedCiphertext);
            //logger.info(org.owasp.esapi.Logger.SECURITY_AUDIT, "Secret Key Length of Cipher Text  :: "+ restoredCipherText.getRawCipherTextByteLength());
            plaintext = ESAPI.encryptor().decrypt(restoredCipherText);

            //logger.info(org.owasp.esapi.Logger.SECURITY_AUDIT, "Get Secret key : Secret Key after decryption length :: "+ plaintext.asBytes().length);
        }
        catch (Exception e)
        {
            logger.error(org.owasp.esapi.Logger.SECURITY_FAILURE, "Decryption Exception  thrown while decrypting data  ", e);
            throw e;

        }

        String encryptAlgorithm = "AES";
        encryptAlgorithm = ESAPI.securityConfiguration().getEncryptionAlgorithm();

        SecretKeySpec secretKeySpec = new SecretKeySpec(plaintext.asBytes(), encryptAlgorithm);
        //logger.info(org.owasp.esapi.Logger.SECURITY_AUDIT, "Get Secret key : Secret Key Legth after creating KeySpecification :: "+ secretKeySpec.getEncoded().length);
        secretKey = secretKeySpec;

        return secretKeySpec;


    }


    public static String generateSecretKey()
    {

        logger.info(org.owasp.esapi.Logger.SECURITY_AUDIT, "Generating Data encryption  Key ");

        String encryptAlgorithm = "AES";
        int encryptionKeyLength = 256;


        String cipherString = null;


        try{

            encryptAlgorithm = ESAPI.securityConfiguration().getEncryptionAlgorithm();
            encryptionKeyLength = ESAPI.securityConfiguration().getEncryptionKeyLength();
            SecretKey secretKey = CryptoHelper.generateSecretKey(encryptAlgorithm, encryptionKeyLength);
            byte[] raw = secretKey.getEncoded();
            //logger.info(org.owasp.esapi.Logger.SECURITY_AUDIT, "Secret Key Length ::::::: "+ raw.length*8);
            String plainText = new String(raw);


            //Encrypting Secret Key
            //CipherText ct = ESAPI.encryptor().encrypt(new PlainText(plainText));
            CipherText ct = ESAPI.encryptor().encrypt(new PlainText(raw));
            //logger.info(org.owasp.esapi.Logger.SECURITY_AUDIT, "Secret Key Length of Cipher Text :: "+ ct.getRawCipherTextByteLength());
            byte[] serializedCiphertext = ct.asPortableSerializedByteArray();
            //logger.info(org.owasp.esapi.Logger.SECURITY_AUDIT, "Secret Key Length after serialization  :: "+ serializedCiphertext.length*8);
            cipherString = ESAPI.encoder().encodeForBase64(serializedCiphertext, false);


        }
        catch(Exception e)
        {

            logger.error(org.owasp.esapi.Logger.SECURITY_FAILURE, "Exception  thrown while generating secret key ", e);


        }

        return cipherString;

    }

    public static String generateMasterKey()
    {

        logger.info(org.owasp.esapi.Logger.SECURITY_AUDIT, "Generating Data encryption  Key " );

        String encryptAlgorithm = "AES";
        int encryptionKeyLength = 256;

        String cipherString=null;


        try
        {

            // setup algorithms -- Each of these have defaults if not set, although
            //					   someone could set them to something invalid. If
            //					   so a suitable exception will be thrown and displayed.
            encryptAlgorithm = ESAPI.securityConfiguration().getEncryptionAlgorithm();
            encryptionKeyLength = ESAPI.securityConfiguration().getEncryptionKeyLength();

            SecretKey secretKey = CryptoHelper.generateSecretKey(encryptAlgorithm, encryptionKeyLength);
            byte[] raw = secretKey.getEncoded();
            logger.info(org.owasp.esapi.Logger.SECURITY_AUDIT, "Master Key Length :: " + raw.length * 8);
            cipherString = ESAPI.encoder().encodeForBase64(raw, false);


        }
        catch (Exception e)
        {

            logger.error(org.owasp.esapi.Logger.SECURITY_FAILURE, "Exception  thrown while generating secret key ", e);


        }


        return cipherString;

    }


    public static String generateMasterSalt()
    {


        String randomAlgorithm = "SHA1PRNG";
        String cipherString=null;


        try
        {
            randomAlgorithm = ESAPI.securityConfiguration().getRandomAlgorithm();
            SecureRandom random = SecureRandom.getInstance(randomAlgorithm);
            byte[] salt = new byte[20];	// Or 160-bits; big enough for SHA1, but not SHA-256 or SHA-512.
            random.nextBytes( salt );
            logger.info(org.owasp.esapi.Logger.SECURITY_AUDIT, "Master Salt Length :: "+ salt.length*8);
            cipherString = ESAPI.encoder().encodeForBase64(salt, false);

        }
        catch (Exception e)
        {

            logger.error(org.owasp.esapi.Logger.SECURITY_FAILURE, "Exception  thrown while generating master salt ", e);


        }

        return cipherString;

    }

    public static int updateSecretKeyInDatabase(String key)
    {
        Connection conn = null;
        int a = 0;
        try
        {
            conn = Database.getConnection();
            String query2 = "update datakey set encryptkey=? where id=1";
            PreparedStatement pstmt2 = conn.prepareStatement(query2);
            pstmt2.setString(1, key);
            a = pstmt2.executeUpdate();
            //System.out.println("it's OK" + a);
        }
        catch (Exception se)
        {
            logger.error(org.owasp.esapi.Logger.SECURITY_FAILURE, "Not update", se);

        }
        finally
        {
            Database.closeConnection(conn);
        }
        return a;
    }

}