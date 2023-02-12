package practice;

/**
 * Created by Admin on 11/5/2020.
 */
public class Sumofallnaturalnumbers
{
    public static void main(String[] args)
    {//using for loop
       /* int num_limit=10, sum=0;
        for(int i=1;i<=num_limit;i++)
        {
            sum =sum +i;
        }
        System.out.println("sum-->"+sum);*/
     //using while loop
        int num1=10,i=1,sum=0;
        while(i<=num1)
        {
            sum=sum+i;
            i++;
        }
        System.out.println("sum-->"+sum);
    }
}
