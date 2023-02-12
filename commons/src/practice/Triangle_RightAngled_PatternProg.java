package practice;

/**
 * Created by Admin on 3/1/2021.
 */
public class Triangle_RightAngled_PatternProg
{
    public static void main(String[] args)
    {
        int i,j,k;
        for(i=1;i<=5;i++)
        {
            for(j=4;j>=i;j--)
            {
                System.out.print(" ");
            }
            for(k=1;k<=i;k++)
            {
               // System.out.print(k + " ");
                System.out.print(k);
            }
            System.out.println();
        }
    }
}
