package com.payment.icici.com.sfa.java;

import java.io.*;
import java.security.*;

import com.directi.pg.TransactionLogger;
import cryptix.provider.rsa.*;
import java.math.*;
import cryptix.util.core.Hex;


public class EPGMerchantEncryptionLib {
	private static TransactionLogger transactionLogger = new TransactionLogger(EPGMerchantEncryptionLib.class.getName());
	byte[] bytarrMsg = null;
	static SecureRandom random = new SecureRandom();

	public byte[] encryptDataWithPubLicKey(String astrClearData, RawRSAPublicKey oRawRSAPublicKey) throws Exception {
		try {
			xjava.security.Cipher cp = xjava.security.Cipher.getInstance("RSA/ECB/PKCS7", "Cryptix");
			cp.initEncrypt(oRawRSAPublicKey);
			int blocksize = cp.getInputBlockSize();
			byte[] plaintext = astrClearData.getBytes();
			byte[] plain = new byte[blocksize];
			for(int i=0; i<plaintext.length; i++)
				plain[i] = plaintext[i];
			byte[] ciphertext = cp.crypt(plain);
			return(ciphertext);
		}
		finally {
		}
	}


	public String getEncryptedStringWithPubLicKey(String astrClearData, RawRSAPublicKey oRawRSAPublicKey) throws Exception {
		try {
			xjava.security.Cipher cp = xjava.security.Cipher.getInstance("RSA/ECB/PKCS7", "Cryptix");
			cp.initEncrypt(oRawRSAPublicKey);
			int blocksize = cp.getInputBlockSize();
			byte[] plaintext = astrClearData.getBytes();
			byte[] plain = new byte[blocksize];
			for(int i=0; i<plaintext.length; i++)
				plain[i] = plaintext[i];
			byte[] ciphertext = cp.crypt(plain);
			return(Hex.toString(ciphertext));
		}
		finally {
		}
	}


	public byte[] encryptDataWithPublicKeyContents(String astrClearData, String astrModulus, String astrExponent) throws Exception {
		try {
			RawRSAPublicKey oRawRSAPublicKey = new RawRSAPublicKey(new BigInteger(astrModulus), new BigInteger(astrExponent));
			return(encryptDataWithPubLicKey(astrClearData, oRawRSAPublicKey));
		}
		finally {
		}
	}




	public String getEncryptedStringWithPubLicKey(String astrClearData, String astrModulus, String astrExponent) throws Exception {
		try {
			RawRSAPublicKey oRawRSAPublicKey = new RawRSAPublicKey(new BigInteger(astrModulus), new BigInteger(astrExponent));
			return(getEncryptedStringWithPubLicKey(astrClearData, oRawRSAPublicKey));
		}
		finally {
		}
	}


	public String decryptDataWithPublicKey(byte[] abytarrEncryptedData, RawRSAPublicKey oRawRSAPublicKey) throws Exception {
		try {
			xjava.security.Cipher cp = xjava.security.Cipher.getInstance("RSA/ECB/PKCS7", "Cryptix");
			cp.initDecrypt(oRawRSAPublicKey);
			byte[] decrypted = cp.doFinal(abytarrEncryptedData);
			String output = new String(decrypted);
			return(output);
		}
		finally {
		}
	}



	public String decryptDataWithPublicKey(String astrEncryptedData, RawRSAPublicKey oRawRSAPublicKey) throws Exception {
		try {
			xjava.security.Cipher cp = xjava.security.Cipher.getInstance("RSA/ECB/PKCS7", "Cryptix");
			cp.initDecrypt(oRawRSAPublicKey);
			byte[] decrypted = cp.doFinal(Hex.fromString(astrEncryptedData));
			String output = new String(decrypted);
			return(output);
		}
		finally {
		}
	}




	public String decryptDataWithPublicKeyContents(byte[] abytarrEncryptedData, String astrModulus, String astrExponent) throws Exception {
		try {
			RawRSAPublicKey oRawRSAPublicKey = new RawRSAPublicKey(new BigInteger(astrModulus), new BigInteger(astrExponent));
			return(decryptDataWithPublicKey(abytarrEncryptedData, oRawRSAPublicKey));
		}
		finally {
		}
	}



	public String decryptDataWithPublicKeyContents(String astrEncryptedData, String astrModulus, String astrExponent) throws Exception {
		try {
			RawRSAPublicKey oRawRSAPublicKey = new RawRSAPublicKey(new BigInteger(astrModulus), new BigInteger(astrExponent));
			return(decryptDataWithPublicKey(astrEncryptedData, oRawRSAPublicKey));
		}
		finally {
		}
	}


	public byte[] encryptDataWithPrivateKey(String astrClearData, RawRSAPrivateKey oRawRSAPrivateKey) throws Exception {
		try {
			xjava.security.Cipher cp = xjava.security.Cipher.getInstance("RSA/ECB/PKCS7", "Cryptix");
			cp.initEncrypt(oRawRSAPrivateKey);
			int blocksize = cp.getInputBlockSize();
			byte[] plaintext = astrClearData.getBytes();
			byte[] plain = new byte[blocksize];
			for(int i=0; i<plaintext.length; i++)
				plain[i] = plaintext[i];
			byte[] ciphertext = cp.crypt(plain);
			return(ciphertext);
		}
		finally {
		}
	}


	public String getEncryptedStringWithPrivateKey(String astrClearData, RawRSAPrivateKey oRawRSAPrivateKey) throws Exception {
		try {
			xjava.security.Cipher cp = xjava.security.Cipher.getInstance("RSA/ECB/PKCS7", "Cryptix");
			cp.initEncrypt(oRawRSAPrivateKey);
			int blocksize = cp.getInputBlockSize();
			byte[] plaintext = astrClearData.getBytes();
			byte[] plain = new byte[blocksize];
			for(int i=0; i<plaintext.length; i++)
				plain[i] = plaintext[i];
			byte[] ciphertext = cp.crypt(plain);
			return(Hex.toString(ciphertext));
		}
		finally {
		}
	}


	public byte[] encryptDataWithPrivateKeyContents(String astrClearData, String astrModulus, String astrExponent) throws Exception {
		try {
			RawRSAPrivateKey oRawRSAPrivateKey = new RawRSAPrivateKey(new BigInteger(astrModulus), new BigInteger(astrExponent));
			return(encryptDataWithPrivateKey(astrClearData, oRawRSAPrivateKey));
		}
		finally {
		}
	}



	public String getEncryptedKeyWithPrivateKeyContents(String astrClearData, String astrModulus, String astrExponent) throws Exception {
		try {
			RawRSAPrivateKey oRawRSAPrivateKey = new RawRSAPrivateKey(new BigInteger(astrModulus), new BigInteger(astrExponent));
			return(getEncryptedStringWithPrivateKey(astrClearData, oRawRSAPrivateKey));
		}
		finally {
		}
	}


	public String decryptDataWithPrivateKey(byte[]  abytarrEncryptedData, RawRSAPrivateKey oRawRSAPrivateKey) throws Exception {
		try {
			xjava.security.Cipher cp = xjava.security.Cipher.getInstance("RSA/ECB/PKCS7", "Cryptix");
			cp.initDecrypt(oRawRSAPrivateKey);
			byte[] decrypted = cp.doFinal(abytarrEncryptedData);
			String output = new String(decrypted);
			return(output);
		}
		finally {
		}
	}


	public String decryptDataWithPrivateKey(String  astrEncryptedData, RawRSAPrivateKey oRawRSAPrivateKey) throws Exception {
		try {
			xjava.security.Cipher cp = xjava.security.Cipher.getInstance("RSA/ECB/PKCS7", "Cryptix");
			cp.initDecrypt(oRawRSAPrivateKey);
			byte[] decrypted = cp.doFinal(Hex.fromString(astrEncryptedData));
			String output = new String(decrypted);
			return(output);
		}
		finally {
		}
	}


	public String decryptDataWithPrivateKeyContents(byte[] abytarrEncryptedData, String astrModulus, String astrExponent) throws Exception {
		try {
			RawRSAPrivateKey oRawRSAPrivateKey = new RawRSAPrivateKey(new BigInteger(astrModulus), new BigInteger(astrExponent));
			return(decryptDataWithPrivateKey(abytarrEncryptedData, oRawRSAPrivateKey));
		}
		finally {
		}
	}


	public String decryptDataWithPrivateKeyContents(String astrEncryptedData, String astrModulus, String astrExponent) throws Exception {
		try {
			RawRSAPrivateKey oRawRSAPrivateKey = new RawRSAPrivateKey(new BigInteger(astrModulus), new BigInteger(astrExponent));
			return(decryptDataWithPrivateKey(astrEncryptedData, oRawRSAPrivateKey));
		}
		finally {
		}
	}



	public KeyPair generateKeyPair() throws Exception {
		try {
			Provider pd = new cryptix.provider.Cryptix();
			Security.addProvider(pd);
			BaseRSAKeyPairGenerator kpg =(BaseRSAKeyPairGenerator)KeyPairGenerator.getInstance("RSA", "Cryptix");
			kpg.initialize(1024,random);
			KeyPair kp = kpg.generateKeyPair();
			return(kp);
		}
		finally {
		}
	}




	public RawRSAPublicKey getPublicKey(KeyPair oKeyPair) throws Exception {
		try {
			RawRSAPublicKey pubkey = (RawRSAPublicKey)oKeyPair.getPublic();
			return(pubkey);
		}
		finally {
		}
	}




	public RawRSAPrivateKey getPrivateKey(KeyPair oKeyPair) throws Exception {
		try {
			RawRSAPrivateKey privatekey = (RawRSAPrivateKey)oKeyPair.getPrivate();
			return(privatekey);
		}
		finally {
		}
	}


	public void print(byte[] bytarrMsg)  {
		try {
			for(int i=0;i<bytarrMsg.length;i++)
			{
					transactionLogger.debug("Index : " + i + " Value : 0x" +  hex[(bytarrMsg[i]>>4)&0x0f]+hex[bytarrMsg[i]&0x0f]);
			}
		}
		catch(Exception oException) {
		}
	}


	public void print(byte bytarrMsg)  {
		try {
					System.out.println(" Value : 0x" +  hex[(bytarrMsg>>4) & 0x0f]+hex[bytarrMsg & 0x0f]);
		}
		catch(Exception oException) {
		}
	}

	char hex[] = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};



	public static void main(String args[]) {
		try {
			EPGMerchantEncryptionLib oLib = new EPGMerchantEncryptionLib();
			KeyPair oKeyPair = oLib.generateKeyPair();
			RawRSAPublicKey oRawRSAPublicKey = (RawRSAPublicKey)oKeyPair.getPublic();
			RawRSAPrivateKey oRawRSAPrivateKey = (RawRSAPrivateKey)oKeyPair.getPrivate();
			byte[] EncryptedData = oLib.encryptDataWithPubLicKey("This is a test string", oRawRSAPublicKey);
			String DecryptedData = oLib.decryptDataWithPrivateKey(EncryptedData, oRawRSAPrivateKey);
			//System.out.println("DecryptedData : " + DecryptedData);

		}
		catch(Exception oException) {
			transactionLogger.error("Exception :::",oException);
		}
		finally {
		}
	}


	 public String encryptData(String strData , String strKey)throws Exception {
			if(strData==null || strData==""){
				return null;
			}
			if(strKey==null || strKey==""){
				return null;
			}
			EPGCryptLib moEPGCryptLib = new EPGCryptLib();
			String strEncrypt=moEPGCryptLib.Encrypt(strKey,strData);
			return strEncrypt;
	}



	public String decryptData(String strData , String strKey)throws Exception {

		if(strData==null || strData==""){

				return null;
		}

		if(strKey==null || strKey==""){
				return null;
		}


		EPGCryptLib moEPGCryptLib = new EPGCryptLib();

		String strDecrypt=moEPGCryptLib.Decrypt(strKey, strData);
//		System.out.println("strDecrypt==>"+strDecrypt);

		return strDecrypt;
	}



	public String encryptMerchantKey(String astrData , String astrMerchantId) throws Exception {
		return(encryptData(astrData, (astrMerchantId+astrMerchantId).substring(0, 16)));
	}


	public String decryptMerchantKey(String astrData , String astrMerchantId) throws Exception {

		return(decryptData(astrData, (astrMerchantId+astrMerchantId).substring(0, 16)));
	}



	public String encryptMerchantData(String astrMerchantId, String astrDirectoryPath, String astrMerchantTxnId, String astrAmount )
		throws Exception {
		try {

			String astrData = astrMerchantId+","+System.currentTimeMillis()+","+astrMerchantTxnId+","+astrAmount;
			FileInputStream oFileInputStream =  new FileInputStream(new File(astrDirectoryPath + astrMerchantId+".key"));

			BufferedReader oBuffRead = new BufferedReader(new InputStreamReader(oFileInputStream));
			String strModulus = oBuffRead.readLine();
			if(strModulus == null) {

				throw new SFAApplicationException("Invalid credentials. Transaction cannot be processed");
			}
			strModulus = decryptMerchantKey(strModulus, astrMerchantId);
			if(strModulus == null) {
				throw new SFAApplicationException("Invalid credentials. Transaction cannot be processed");
			}
			String strExponent = oBuffRead.readLine();
			if(strExponent == null) {
				throw new SFAApplicationException("Invalid credentials. Transaction cannot be processed");
			}
			strExponent = decryptMerchantKey(strExponent, astrMerchantId);
			if(strExponent == null) {
				throw new SFAApplicationException("Invalid credentials. Transaction cannot be processed");
			}
			return(getEncryptedStringWithPubLicKey(astrData,strModulus,strExponent));
		}
		finally {
		}
	}


}