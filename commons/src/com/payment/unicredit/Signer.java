package com.payment.unicredit;

import com.directi.pg.Logger;

import java.security.*;
import java.security.cert.*;
import java.io.*;

/**
 * Created by Admin on 12/29/2017.
 */
public class Signer
{
    private static Logger logger=new Logger(Signer.class.getName());
    private KeyStore keyStore = null;
    private String keystoreFileName = null;
    private String keystorePassword = null;
    private String keystorePrivateKey = null;
    private String keystorePrivateKeyPassword = null;
    private String keystoreInstance = "JKS";

    /**
     *Static method to initialize the Signer component. To be called before *signing.
     *@param keystoreFileName String The (fully qualified) name of the file with *the java keystore
     *@param keystorePassword The password of the keystore
     *@param keystorePrivateKey String The key alias in the keystore
     *@param keystorePrivateKeyPassword String The password of the key
     */
    public Signer(String keystoreFileName, String keystorePassword,String keystorePrivateKey, String keystorePrivateKeyPassword)
    {
        this.keystoreFileName = keystoreFileName;
        this.keystorePassword = keystorePassword;
        this.keystorePrivateKey = keystorePrivateKey;
        this.keystorePrivateKeyPassword = keystorePrivateKeyPassword;
        try
        {
            FileInputStream fis;
            fis = new FileInputStream(this.keystoreFileName);
            keyStore = KeyStore.getInstance(keystoreInstance);
            keyStore.load(fis, this.keystorePassword.toCharArray());
        }
        catch (Exception e)
        {
            logger.error("Exception--->",e);
        }
    }
    /**
     *This This method make a signature of message
     *@param data byte[] data for signature
     *return byte[] Signature of message	*/
    public byte[] createSignature(byte[] data)
    {
        try
        {
            //keystorePrivateKey = "res\\EuroFootball_eBorica_TestKey.key";
            //keystorePrivateKeyPassword = "changeit";
            //System.out.println("keystorePrivateKey---"+keystorePrivateKey+"---"+keystorePrivateKeyPassword);
            PrivateKey key;
            key = (PrivateKey) keyStore.getKey(keystorePrivateKey, keystorePrivateKeyPassword.toCharArray());
            Signature signature;
            signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(key);
            signature.update(data);
            return signature.sign();
        }
        catch (Exception e)
        {
            logger.error("Exception--->",e);
            return null;
        }
    }

    /**
     *This method make verification signature of response message
     *	@param messageData byte[] Signed data
     *	@param signatureOfmessage byte[] Signature to verify
     *	@param publicKeyAPGW name of public key used for verification
     *	@return Boolean	*/

    public boolean verifySignature(byte[]messageData,byte[]signatureOfmessage, String publicKeyAPGW)
    {

        try
        {
            X509Certificate certificateX509;
            certificateX509 = (X509Certificate) keyStore.getCertificate(publicKeyAPGW);
            PublicKey publicKey;
            publicKey = (PublicKey)certificateX509.getPublicKey();
            //System.out.println("=5=="+publicKey.toString());
            Signature signature;
            signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(publicKey);
            signature.update(messageData);
            return signature.verify(signatureOfmessage);
        }
        catch (Exception e)
        {
            logger.error("Exception--->",e);
            return false;
        }
    }
}
