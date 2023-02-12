package practice;

/**
 * Created by Admin on 3/15/2021.
 */
public class sandglass_pattern
{
    public static void main(String[] args)
    {

        int rows = 5;
        for (int i = 0; i <= rows - 1; i++)
        {
            for (int j = 0; j < i; j++)
            {
                System.out.print(" ");
            }
            for (int k = i; k <= rows - 1; k++)
            {
                System.out.print("*" + " ");
            }
            System.out.println("");
        }
        for (int i = rows - 1; i >= 0; i--)
        {
            for (int j = 0; j < i; j++)
            {
                System.out.print(" ");
            }
            for (int k = i; k <= rows - 1; k++)
            {
                System.out.print("*" + " ");
            }
            System.out.println("");
        }
    }
}