package com.payment.billdesk;

import com.directi.pg.TransactionLogger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

public class TestHMac
{
	private static TransactionLogger transactionlogger = new TransactionLogger(TestHMac.class.getName());
	public static void main(String[] args)
	{

		try
		{
			String ChecksumKey ="Test1234567";

			//System.out.println("HmacSHA256="+HmacSHA256("MERCHANT|1000000000|NA|12.00|XXX|NA|NA|INR|DIRECT|R|NA|NA|NA|F|111111111|NA|NA|NA|NA|NA|NA|NA|",ChecksumKey));
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}


	}

	public static String HmacSHA256(String message,String secret)
	{
		MessageDigest md = null;
		try
		{
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);


			byte raw[] = sha256_HMAC.doFinal(message.getBytes());

			StringBuffer ls_sb=new StringBuffer();
			for(int i=0;i<raw.length;i++)
				ls_sb.append(char2hex(raw[i]));
				return ls_sb.toString(); //step 6
		}catch(Exception e){
			transactionlogger.error("Exception TestHMac ",e);
			return null;
		}
	}

	 public static String char2hex(byte x)

	{
		char arr[]={
					  '0','1','2','3',
					  '4','5','6','7',
					  '8','9','A','B',
					  'C','D','E','F'
					};

		char c[] = {arr[(x & 0xF0)>>4],arr[x & 0x0F]};
		return (new String(c));
	  }

}