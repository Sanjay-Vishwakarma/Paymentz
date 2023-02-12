package acqra;

/**
 * Created by Admin on 6/7/2021.
 */
public class PAttern_Part_26
{
    public static void main(String[] args)
    {
      /*  int n=3;
        int i,j,k;
        for(i=n;i>=-n;i--)//3 2 1 0 -1 -2 -3
        {
            for(j=n;j>=Math.abs(i);j--)
            {
                System.out.print("*");
            }
            System.out.println();
        }*/

        int n=3;
        int i,j,k;
        for(i=n;i>=-n;i--)//3 2 1 0 -1 -2 -3
        {
            for(j=1;j<=Math.abs(i);j++)
            {
                System.out.print(" ");
            }
            for(k=n;k>=Math.abs(i);k--)
            {
                System.out.print("*");
            }
            System.out.println();
        }
    }
}
