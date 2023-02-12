package acqra;

/**
 * Created by Admin on 6/5/2021.
 */
public class Pattern18
{
    public static void main(String[] args)
    {
        int i,j,k;
        for(i=5;i>=1;i--)
        {
            for(j=4;j>=i;j--)
            {
                System.out.print(" ");
            }
            for(k=1;k<=i;k++)
            {
                System.out.print("*"+" ");
            }
            System.out.println();
        }
    }
}
