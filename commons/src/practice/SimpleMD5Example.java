package practice;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Admin on 10/25/2020.
 */
public class SimpleMD5Example
{
    public static void main(String[] args)
    {
        String passwordToHash ="password";
        String generatedPassword = null;
        try{
            MessageDigest messageDigest =MessageDigest.getInstance("MD5");
            messageDigest.update(passwordToHash.getBytes());
            byte[] bytes=messageDigest.digest();
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        System.out.println(generatedPassword);
    }
}
