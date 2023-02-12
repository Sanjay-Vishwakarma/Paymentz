package practice;

/**
 * Created by Admin on 3/15/2021.
 */
public class zero_one_pattern
{
    public static void main(String[] args)
    {
        int rows = 5;
        for (int i = 1; i <= rows; i++)
        {
            for (int j = 1; j <= i; j++)
            {
                if (j % 2 == 0)
                {
                    System.out.print(0);
                }
                else
                {
                    System.out.print(1);
                }
            }

            System.out.println();
        }
    }
}