package practice;

/**
 * Created by Admin on 3/16/2021.
 */
public class Print_one_to_hundred_without_using_numbers
{
    public static void main(String[] args)
    {
        int one='A'/'A';
        String s1="abcdefghij";
        //sol1
        for(int i=one;i<=(s1.length()*s1.length());i++)
        {
            System.out.println(i);
        }
        //sol2
        for(int i=one;i<='d';i++)
        {
            System.out.println(i);
        }
    }
}
