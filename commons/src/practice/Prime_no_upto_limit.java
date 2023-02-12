package practice;

/**
 * Created by Admin on 2/8/2021.
 */
public class Prime_no_upto_limit
{
    public static void main(String[] args)
    {
         int limit = 30;
        for (int i = 1; i <= limit; i++)
        {
           int count=0;
            for(int j=1;j<=i;j++)
            {
                if(i%j==0)
                {
                    count++;
                }
            }
            if(count==2)
            {
                System.out.println(" "+i);
            }
        }
    }
}
