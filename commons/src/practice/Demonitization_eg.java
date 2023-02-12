package practice;

import java.util.Scanner;

/**
 * Created by Admin on 5/31/2020.
 */
public class Demonitization_eg
{
    public static void main(String[] args)
    {
        int types_of_money[]= {2000,500,200,100,50,20,10,5,2,1};
        Scanner sc = new Scanner(System.in);
        System.out.println("Please Enter Amount ");
        int enteramount = sc.nextInt();
        int size =types_of_money.length;
      // int store =enteramount;
        int totalnotes=0;
        int count=0;
        for(int i=0;i<size;i++)
        {
            count=enteramount/types_of_money[i];
            if(count!=0)
            {
                System.out.println(types_of_money[i]+"*"+count+"="+types_of_money[i]*count);

            }
            totalnotes=totalnotes+count;
            enteramount=enteramount%types_of_money[i];

        }
        System.out.println("totalnotes "+totalnotes);
        System.out.println("enteramount "+enteramount);

    }
}
