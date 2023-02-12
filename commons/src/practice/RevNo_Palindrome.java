package practice;

/**
 * Created by Admin on 3/8/2021.
 */
public class RevNo_Palindrome
{
    public static void main(String[] args)
    {
        int num=383;
        int b,c=0;
        while(num>0)
        {
            b=num%10;
            num=num/10;
            c=(c*10)+b;
        }
        if(c==num)
        {
            System.out.println(c+" is Palindrome");
        }
        else
        {
            System.out.println(c+" is not Palindrome");
        }

    }
}
