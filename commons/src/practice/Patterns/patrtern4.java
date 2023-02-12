package practice.Patterns;

/**
 * Created by Admin on 3/25/2021.
 */
public class patrtern4
{
    public static void main(String[] args)
    {
    /*    int i,j,x=2;
        for(i=1;i<=5;i++)
        {
            for(j=1;j<=i;j++)
            {
                System.out.print(x + " ");
                x+=2;
            }
            System.out.println();
        }*/

        int i,j;
        for(i=1;i<=5;i++)
        {
            for(j=1;j<=i;j++)
            {
                System.out.print(i*j + " ");
               }
            System.out.println();
        }
    }
}
/*

        2
        4 6
        8 10 12
        14 16 18 20
        22 24 26 28 30
*/
