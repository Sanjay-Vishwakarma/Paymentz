package practice.Patterns;

/**
 * Created by Admin on 3/25/2021.
 */
public class alternate_one_zero
{
    public static void main(String[] args)
    {
        int i,j;
        for(i=1;i<=5;i++)
        {
            for(j=1;j<=5;j++)
            {
                System.out.print(i%2);
            }
            System.out.println();
        }
    }
}
