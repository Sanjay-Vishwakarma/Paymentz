package practice;

/**
 * Created by Admin on 2/8/2021.
 */
public class PyramidPattern1
{
/*    public static void main(String[] args)
    {
        for(int i=5;i>=1;i--)
        {
            for(int j=1;j<=i;j++)
            {
                System.out.print(i);
            }
            System.out.println();
        }
    }*/
public static void main(String[] args)
{
    int n=5;
    for(int i=1;i<=n;i++)
    {
        for(int j=n-1;j>=i;j--)
        {
            System.out.print(" ");
        }
        for(int k=1;k<=i;k++)
        {
            System.out.print("*");
        }
        System.out.println();
    }
}

   /* public static void main(String[] args)
    {
        int i,j,k,n,z;
        z=1;
        n=5;
        for(i=1;i<=n;i++)
        {
            for(j=n-1;j>=i;j--)
            {
                System.out.print(" ");
            }
            for(k=1;k<=z;k++)
            {
                System.out.print(z+" ");
            }
            z+=2;
            System.out.println(" ");
        }
    }*/

   /* public static void main(String[] args)
    {
        int i,j,px=4,py=4;
        for(i=1;i<=4;i++)
        {
            for(j=1;j<=8;j++)
            {
                if(j>=px && j<=py)
                {
                    System.out.print("*");
                }
                else
                {
                    System.out.print(" ");
                }

            }
            px--;
            py++;
            System.out.println("");
        }
    }
*/

}
