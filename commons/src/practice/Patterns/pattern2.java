package practice.Patterns;

/**
 * Created by Admin on 3/25/2021.
 */
public class pattern2
{
    public static void main(String[] args)
    {
        int i,j;
        for(i=5;i>=1;i--)
        {
            for(j=5;j>=i;j--)
            {
                System.out.print(j + " ");
            }
            System.out.println();
        }
    }
}
      /*5
        5 4
        5 4 3
        5 4 3 2
        5 4 3 2 1
*/