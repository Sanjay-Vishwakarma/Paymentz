package acqra;

/**
 * Created by Admin on 6/5/2021.
 */
public class Pattern23_25
{
    public static void main(String[] args)
    {
      /*  int i, j, k, z = 1;
        for (i = 1; i <= 4; i++)
        {
            for (j = 3; j >= i; j--)
            {
                System.out.print(" ");
            }
            for (k = 1; k <= z; k++)
            {
                System.out.print(i);
            }
            z += 2;

            System.out.println();
        }*/

        int i, j, k, z = 7;
        for (i = 4; i >=1 ; i--)
        {
            for (j = i; j <= 3; j++)
            {
                System.out.print(" ");
            }
            for (k = 1; k <= z; k++)
            {
                System.out.print(z);
            }
            z -= 2;

            System.out.println();
        }

        /*7777777
           55555
            333
             1*/
    }
}
