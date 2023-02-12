/* This class is a wrapper for DES encryption and decryption.
 * The application developer has to just pass the key and data
 * to be encrypted as byte arrays.
 * dated :6/2/2002
 */
/*
 * @(#)EPGCryptLib
 *
 * Copyright  Opus Software Pvt Ltd.All Rights Reserved.
 *
 * This software is the proprietary information of Opus Software Pvt Ltd
 * Use is subject to license terms.
 */

package com.payment.icici.com.sfa.java;
import cryptix.provider.key.RawSecretKey;
import cryptix.util.core.ArrayUtil;
import cryptix.util.core.Hex;
import cryptix.util.test.BaseTest;
import xjava.security.Cipher;
import xjava.security.FeedbackCipher;
import java.io.*;
import java.security.Security;

 /*
 * @author  Ashish
 * @version 1.0, 06/18/2003
 */

public class EPGCryptLib
{
	Cipher alg;
	//public byte ciphertext[];

    	public EPGCryptLib() throws Exception
    	{

    				Security.addProvider(new cryptix.provider.Cryptix());

				alg = Cipher.getInstance("DES/ECB/PKCS7", "Cryptix");


    	}

    	public  String Encrypt(String pKey,String pData) throws Exception
    	{
				byte[] lKey = Hex.fromString(pKey);
				byte[] lData = Hex.fromString(pData);
				byte[] ect;

				RawSecretKey key = new RawSecretKey("DES/ECB/PKCS7", lKey);
				alg.initEncrypt(key);
				ect = alg.crypt(lData);
				String lStrEncrypted = Hex.toString(ect);
				return lStrEncrypted;

		}


		public String Decrypt(String pKey,String pData) throws Exception
    	{

			byte[] lKey = Hex.fromString(pKey);
				byte[] lData = Hex.fromString(pData);
		    	byte[] dct;

			RawSecretKey key = new RawSecretKey("DES/ECB/PKCS7", lKey);

			alg.initDecrypt(key);
				dct = alg.crypt(lData);
				String lStrDecrypted = Hex.toString(dct);

				return lStrDecrypted;

		}


    	/*public static void main(String args[])
    	{
    	    try
    	    {
				String chiave;
				chiave = "DD7F121CA5015619";//111000026";
				String lKey = new String("0101010101010101");
		    	com.opus.epg.classes.utils.EPGCryptLib lobj  = new com.opus.epg.classes.utils.EPGCryptLib();
		    	String lencrypted;
		    	String ldecrypted;

		    	lencrypted = lobj.Encrypt(lKey,chiave);
		    	ldecrypted = lobj.Decrypt(lKey,lencrypted);

		    	System.out.println("Encrypted Data is:" + lencrypted);
		    	System.out.println("Decrypted Data is:" + ldecrypted);
    	    }
    	    catch(Exception exception)
    	    {
    	        System.out.println("Exception");
    	        exception.printStackTrace();
    	    }
    	}*/
}

