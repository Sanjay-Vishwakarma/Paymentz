package practice;

/**
 * Created by Admin on 3/18/2021.
 */
public class pattern_1
{
    public static void main(String[] args)
    {
        int i,j,k,n=4;
        for(i=1;i<=n;i++)
        {
           for(j=n-1;j>=i;j--)
           {
               System.out.print(" ");
           }
            for(k=1;k<=i;k++)
            {
                System.out.print(k + " ");
            }
            System.out.println();
        }
    }
}
