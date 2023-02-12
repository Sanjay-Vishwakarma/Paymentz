package practice;

import com.directi.pg.TransactionLogger;

/**
 * Created by Admin on 6/20/2020.
 */

public class PatternPrg1
{
    public static void main(String[] args)
    {
        PatternPrg1 p=new PatternPrg1();
        System.out.println(p instanceof PatternPrg1);
    }
   /* {
        String var ="SuCCesS";
        int amt = 123;
        if("success".equalsIgnoreCase(var))
        {
            System.out.println("Transaction Successful");

        }
        else
        {
            System.out.println("Failed");
        }
    }*/
    /*{
        int i,j;
        for(i=5;i>=1;i--)
        {
            for(j=1;j<i;j++)
            {
                System.out.print(i +" ");
            }
            System.out.println(i);
        }
    }*/
}
