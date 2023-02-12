package practice;

/**
 * Created by Admin on 2/13/2021.
 */
public class reverse_str
{
    public static void main(String[] args)
    {
        String str= "ragas ranos";
        char[] char_=str.toCharArray();
        for(int i=char_.length-1;i>=0;i--)
        {
            System.out.print(char_[i]-1);
        }
    }
}
