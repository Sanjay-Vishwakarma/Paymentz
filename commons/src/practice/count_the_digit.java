package practice;

/**
 * Created by Admin on 3/12/2021.
 */
public class count_the_digit
{
    public static void main(String[] args)
    {
         int num=1892098789;
        int count=0;//initially
        while (num!=0)
        {
            num/=10;
            count++;
        }

        System.out.println("num of digit--"+count);
    }
}
