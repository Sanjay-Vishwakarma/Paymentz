package acqra;

/**
 * Created by Admin on 5/31/2021.
 */
public class largestNumber
{
    public static void main(String[] args)
    {
        int a=10;
        int b=2;
        int c=87;

        if(a>c && a>b)
        {
            System.out.println("a is largest");
        }
        else if(b>c)
        {
            System.out.println("b is largest");
        }
        else
        {
            System.out.println("c is largest");
        }

        int i=1;
        int j=i++;
        System.out.println("i ---"+i);
        System.out.println("j ---"+j);
    }
}
