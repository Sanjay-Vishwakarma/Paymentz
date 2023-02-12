package com.manager;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.validators.CommonInputValidator;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by IntelliJ IDEA.
 * User: Sandip
 * Date: June 1, 2016
 * Time: 1:51:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class AESEncryptionManager
{
    private static Logger log = new Logger(CommonInputValidator.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(CommonInputValidator.class.getName());

    private static String initialVector = "0123456789123456";
    private static String md5(final String input) throws NoSuchAlgorithmException
    {
        final MessageDigest md = MessageDigest.getInstance("MD5");
        final byte[] messageDigest = md.digest(input.getBytes());
        final BigInteger number = new BigInteger(1, messageDigest);
        return String.format("%032x", number);
    }

    public static void main(final String[] args)
    {
        final String iv = "0123456789123456"; // This has to be 16 characters
        final String secretKey = "W8CyfwnRFRbQschifbQnWIYWFWDJgq3v";
        AESEncryptionManager encryptionManager=new AESEncryptionManager();
        final String encryptedData = encryptionManager.encrypt("This is a test message.", secretKey);
        //System.out.println(encryptedData);

        final String decryptedData = encryptionManager.decrypt(encryptedData,secretKey);
        //System.out.println(decryptedData);
    }

    private Cipher initCipher(final int mode, final String initialVectorString, final String secretKey)throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException
    {
        final SecretKeySpec skeySpec = new SecretKeySpec(md5(secretKey).getBytes(), "AES");
        final IvParameterSpec initialVector = new IvParameterSpec(initialVectorString.getBytes());
        final Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        cipher.init(mode, skeySpec, initialVector);
        return cipher;
    }

    public String encrypt(final String dataToEncrypt,final String secretKey)
    {
        String encryptedData = null;
        try
        {
            // Initialize the cipher
            final Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, initialVector, secretKey);
            // Encrypt the data
            final byte[] encryptedByteArray = cipher.doFinal(dataToEncrypt.getBytes());
            // Encode using Base64
            encryptedData = (new BASE64Encoder()).encode(encryptedByteArray);
        }
        catch (Exception e)
        {
            transactionLogger.error("Problem encrypting the data:::::" + e);
        }
        return encryptedData;
    }

    public String decrypt(final String encryptedData,final String secretKey)
    {
        String decryptedData = null;
        try
        {
            // Initialize the cipher
            final Cipher cipher = initCipher(Cipher.DECRYPT_MODE, initialVector, secretKey);
            // Decode using Base64
            final byte[] encryptedByteArray = (new BASE64Decoder()).decodeBuffer(encryptedData);
            // Decrypt the data
            final byte[] decryptedByteArray = cipher.doFinal(encryptedByteArray);
            decryptedData = new String(decryptedByteArray, "UTF8");
        } catch (Exception e)
        {
            transactionLogger.error("Problem decrypting the data:::::" + e);
        }
        return decryptedData;
    }


}
