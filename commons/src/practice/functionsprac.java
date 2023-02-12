package practice;

import com.directi.pg.Functions;

/**
 * Created by Admin on 11/4/2020.
 */
public class functionsprac
{
    public static void main(String[] args)
    {
        Functions functions = new Functions();
        String cardno="4444333322221111";
        System.out.println("Card Number is "+functions.maskingPan(cardno));
    }
}
