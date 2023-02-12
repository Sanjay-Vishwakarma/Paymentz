package com.directi.pg.core.lpb;

import com.directi.pg.Base64;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created with IntelliJ IDEA.
 * User: Chandan
 * Date: 8/31/13
 * Time: 8:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class LpbUtils
{
    private static Logger logger = new Logger(LpbUtils.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(LpbUtils.class.getName());

    public static class Package {

        public String data;
        public String key;

        public Package() {}

        public Package(final String data, final String key) {
            this.data = data;
            this.key = key;
        }
    }
    static{
        Security.addProvider(new BouncyCastleProvider());
    }

    public static String sign(final String data, final String privateKeyStr) throws PZTechnicalViolationException
    {
        String base64Binary=null;
        try {
            final java.security.Signature s = java.security.Signature.getInstance("SHA1withRSA");
            s.initSign(getPemPrivateKey(privateKeyStr));
            s.update(data.getBytes("UTF-8"));
            base64Binary=DatatypeConverter.printBase64Binary(s.sign());
        }
        catch (SignatureException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(),"sign()",null,"common","Technical Exception while placing transaction", PZTechnicalExceptionEnum.SIGANTURE_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(),"sign()",null,"common","Technical Encoding Exception while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(),"sign()",null,"common","Technical Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (InvalidKeyException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(), "sign()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.INVALID_KEY_EXCEPTION,null, e.getMessage(), e.getCause());
        }

        return base64Binary;
    }

    public static Package encrypt(final String data, final String publicKeyStr) throws PZTechnicalViolationException
    {
            final Package pkg = new Package();
        try {

            final SecretKey key = KeyGenerator.getInstance("RC4").generateKey();
            final PublicKey publicKey = getPemPublicKey(publicKeyStr);

            pkg.key = new String(DatatypeConverter.printBase64Binary(encryptRSA(key.getEncoded(), publicKey)));
            pkg.data = new String(DatatypeConverter.printBase64Binary(encryptRC4(data.getBytes("UTF-8"), key)));


        }
        catch (NoSuchPaddingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(),"encrypt()",null,"common","Technical Exception while placing transaction",PZTechnicalExceptionEnum.NOSUCH_PADDING_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (BadPaddingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(),"encrypt()",null,"common","Technical Exception while placing transaction",PZTechnicalExceptionEnum.BAD_PADDING_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(),"encrypt()",null,"common","Technical Exception while placing transaction",PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (IllegalBlockSizeException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(),"encrypt()",null,"common","Technical Exception while placing transaction",PZTechnicalExceptionEnum.ILLEGAL_BLOCK_SIZE_EXCEPTION,null,e.getMessage(),e.getCause());
        }

        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(),"encrypt()",null,"common","Technical Encoding Exception while placing transaction",PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (InvalidKeyException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(),"encrypt()",null,"common","Technical Exception while placing transaction",PZTechnicalExceptionEnum.INVALID_KEY_EXCEPTION,null,e.getMessage(),e.getCause());
        }

        return pkg;

    }
    public static String decrypt(final Package pkg, final String privateKeyStr) throws PZTechnicalViolationException
    {
          String plainTextCipher=null;
        try {
            final PrivateKey privateKey = getPemPrivateKey(privateKeyStr);
            final byte[] plainKey = decryptRSA(DatatypeConverter.parseBase64Binary(pkg.key), privateKey);
            final SecretKey key = new SecretKeySpec(plainKey, "RC4");
            final byte[] plaintext = decryptRC4(DatatypeConverter.parseBase64Binary(pkg.data), key);
            plainTextCipher= new String(plaintext);
        }
        catch (NoSuchAlgorithmException e)
        {
           PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(),"decrypt()",null,"common","Technical Exception while placing transaction",PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (InvalidKeyException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(),"decrypt()",null,"common","Technical Exception while placing transaction",PZTechnicalExceptionEnum.INVALID_KEY_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (NoSuchPaddingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(),"decrypt()",null,"common","Technical Exception while placing transaction",PZTechnicalExceptionEnum.NOSUCH_PADDING_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (BadPaddingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(),"decrypt()",null,"common","Technical Exception while placing transaction",PZTechnicalExceptionEnum.BAD_PADDING_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (IllegalBlockSizeException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(),"decrypt()",null,"common","Technical Exception while placing transaction",PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,e.getMessage(),e.getCause());
        }

      return plainTextCipher;
    }

    public static PrivateKey getPemPrivateKey(String filename) throws PZTechnicalViolationException
    {

        PrivateKey privateKey = null;
        DataInputStream dis = null;
        try
        {
            File f = new File(filename);
            FileInputStream fis = new FileInputStream(f);
            dis = new DataInputStream(fis);
            byte[] keyBytes = new byte[(int) f.length()];
            dis.readFully(keyBytes);


            String str = new String(keyBytes);
            String BEGIN = "-----BEGIN RSA PRIVATE KEY-----";
            String END = "-----END RSA PRIVATE KEY-----";
            if (str.contains(BEGIN) && str.contains(END))
            {
                str = str.substring(BEGIN.length(), str.lastIndexOf(END));
            }

            Base64 b64 = new Base64();
            byte[] decoded = b64.decode(str);

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            privateKey = kf.generatePrivate(spec);
        }
        catch (InvalidKeySpecException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(), "getPemPrivateKey()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.INVALID_KEY_SPEC_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (FileNotFoundException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(), "getPemPrivateKey()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.FILE_NOTFOUND_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(), "getPemPrivateKey()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(), "getPemPrivateKey()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            if (dis != null)
            {
                try
                {
                    dis.close();
                }
                catch (IOException e)
                {
                    logger.error("IO Exception while closing the DataInputStream",e);
                    transactionLogger.error("IO Exception while closing the DataInputStream",e);
                }
            }
        }
        return privateKey;
    }

    public static PublicKey getPemPublicKey(String filename) throws PZTechnicalViolationException
    {
        PublicKey publicKey = null;

        DataInputStream dis = null;
        try
        {
            File f = new File(filename);
            FileInputStream fis = new FileInputStream(f);
            dis = new DataInputStream(fis);
            byte[] keyBytes = new byte[(int) f.length()];
            dis.readFully(keyBytes);
            dis.close();

            String str = new String(keyBytes);
            String BEGIN = "-----BEGIN PUBLIC KEY-----";
            String END = "-----END PUBLIC KEY-----";
            if (str.contains(BEGIN) && str.contains(END))
            {
                str = str.substring(BEGIN.length(), str.lastIndexOf(END));
            }

            //return publicKeyPEM;
            Base64 b64 = new Base64();
            byte[] decoded = b64.decode(str);

            X509EncodedKeySpec spec =
                    new X509EncodedKeySpec(decoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            publicKey = kf.generatePublic(spec);
        }
        catch (InvalidKeySpecException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(), "getPemPublicKey()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.INVALID_KEY_SPEC_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (FileNotFoundException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(), "getPemPublicKey()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.FILE_NOTFOUND_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(), "getPemPublicKey()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbUtils.class.getName(), "getPemPublicKey()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            if (dis != null)
            {
                try
                {
                    dis.close();
                }
                catch (IOException e)
                {
                    logger.error("IO exception while placing transaction",e);
                    transactionLogger.error("IO exception while placing transaction",e);
                }
            }
        }
        return publicKey;
    }
    private static byte[] encryptRC4(final byte[] data, final Key key) throws IllegalBlockSizeException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException
    {
        return cipher(data, key, "RC4", true);
    }

    private static byte[] decryptRC4(final byte[] data, final Key key) throws IllegalBlockSizeException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException
    {
        return cipher(data, key, "RC4", false);
    }

    private static byte[] encryptRSA(final byte[] data, final Key publicKey) throws IllegalBlockSizeException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException
    {
        return cipher(data, publicKey, "RSA", true);
    }

    private static byte[] decryptRSA(final byte[] data, final Key privateKey) throws IllegalBlockSizeException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException
    {
        return cipher(data, privateKey, "RSA", false);
    }

    private static byte[] cipher(final byte[] data, Key key, final String alg, final boolean encrypt) throws InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, BadPaddingException, NoSuchPaddingException
    {
        final Cipher cipher = Cipher.getInstance(alg);
        cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }
}
