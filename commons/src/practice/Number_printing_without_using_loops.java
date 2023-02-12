package practice;

/**
 * Created by Admin on 3/22/2021.
 */
public class Number_printing_without_using_loops
{
    static int count=1;

    static void printNumbers(int n)
    {
        System.out.println(count);
        count++;

        if(count==n+1)
        return;
        printNumbers(n);
    }
    public static void main(String[] args)
    {


    }

}
