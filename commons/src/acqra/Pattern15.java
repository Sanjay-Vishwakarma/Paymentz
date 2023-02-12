package acqra;

/**
 * Created by Admin on 6/5/2021.
 */
public class Pattern15
{
    public static void main(String[] args)
    {
        int i,j;
        char ch='A';
        for(i=4;i>=0;i--)
        {
            for(j=0;j<=i;j++)
            {
                System.out.print((char)(j+65));
            }
            System.out.println();
        }
    }
}
