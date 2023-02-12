package practice;

/**
 * Created by Admin on 2/9/2021.
 */
public class count_whitespaces_without_split
{
    public static void main(String[] args)
    {
        int count =0;
        String eg="India is my country";
        for(int i=0;i<eg.length();i++)
        {
            if(eg.charAt(i)==' ')
            {
                count++;
            }

        }
        System.out.println("Length of eg--"+eg.length());
        System.out.println("whitespaces in string--"+count);
    }
}
