package practice;

import com.directi.pg.Functions;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by Admin on 6/27/2020.
 */
public class Practice_1
{

    public static void main(String[] args)
    {
        String var = "";
        System.out.println(var.length());


        //To get current date & time
/*
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        System.out.println("calender->"+currentDate);
*/
        //To check values are string or number
      /*  public boolean isNumericVal(String var)
        {
            return var.matches("^.*\\d$");
        }
        String var1 ="987";
        Practice_1 practice_1 = new Practice_1();
        System.out.println(practice_1.isNumericVal(var1));
      */  //Date
        /*Date date = new Date();
        System.out.println(date.getTime());*/
// Eg to check null with Functions use
/*
    Functions functions = new Functions();
    String name ="";
    int score =183;
    String strikerate = "";

    if(functions.isValueNull(name))
    {
        System.out.println("Name->"+name);
    }
        else
    {
        System.out.println("Empty name");
    }

        if(functions.isValueNull(String.valueOf(score)))
        {
            System.out.println("score->"+score);
        }
        else
        {
            System.out.println("Empty score");
        }

        if(functions.isValueNull(strikerate))
        {
            System.out.println("strikerate->"+strikerate);
        }
        else
        {
            System.out.println("Empty strikerate");
        }
*/
           //to check whether a char is alphabet or not
    /*public static void main(String[] args)
    {
        char character ='@';
        if((character>='a'&character<='z')||(character>='A'&character<='Z'))
        {
            System.out.println(character + " is alphabet");
        }
        else
        {
            System.out.println(character + " is not alphabet");
        }
    *///MD5 method
   /* public static String getMD5method(String input) throws NoSuchAlgorithmException
    {
        try
        {

            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] bytevar = messageDigest.digest(input.getBytes());
            BigInteger bigInteger = new BigInteger(1, bytevar);
            String hashtext = bigInteger.toString(16);
            while (hashtext.length() < 32)
            {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
            catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        }
        public static void main(String[] args) throws NoSuchAlgorithmException
        {
        String str = "GeeksForGeeks";
        System.out.println("Answer->"+getMD5method(str));
*/
    //Tokenizer eg to split
       /* String var="abc_def_fgh";
        StringTokenizer st = new StringTokenizer(var,"_");
        while (st.hasMoreElements())
        {
            String token=st.nextToken();
            System.out.println(token+"\n");
        }
*/
        //Eg of class & method
    /*int var1 = 10;
    float var2 = 10.9f;

    void method()
    {
       System.out.println("Integer var1->"+var1);
    }
}
class Practice
{
    public static void main(String[] args)
    {
     Practice_1 obj = new Practice_1();
        obj.method();
    }
}*/
    }
}