package practice;

/**
 * Created by Admin on 10/6/2020.
 */
public class Check_Alphabet
{
    public static void main(String[] args)
    {
        char c= 'u';
        if((c>='a'&& c<='z')||(c>='A'&& c<='Z'))
        {
            System.out.println(c + " is alphabet");
        }
        else
        {
            System.out.println(c +" is not alphabet");
        }
    }
}
