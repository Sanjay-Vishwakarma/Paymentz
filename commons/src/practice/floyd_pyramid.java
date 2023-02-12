package practice;

/**
 * Created by Admin on 3/1/2021.
 */
public class floyd_pyramid
{
    public static void main(String[] args)
    {
        int i,j,k=1;
        for(i=1;i<=4;i++)
        {
            for(j=1;j<i+1;j++)
            {
                System.out.print(k +" ");
                k++;
            }
            System.out.println();
        }
    }
}
