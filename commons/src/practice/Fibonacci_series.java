package practice;

/**
 * Created by Admin on 2/16/2021.
 */
public class Fibonacci_series
{
    public static void main(String[] args)
    {
        int a=0,b=1;
        int n=5;
        int sum=a+b;
        for(int i=0;i<=n;i++)
        {
         //   for(int j=1;j<=i;j++)
            {
            System.out.print(a+ " ");
            sum=a+b;
            a=b;
            b=sum;
        }
            System.out.println();
        }

    }
}
