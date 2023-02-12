package practice;

/**
 * Created by Admin on 1/12/2021.
 */
public class reversestring
{
    public static void main(String[] args)
    {
        String str="sagar pradeep sonar";
        String []strarray =str.split(" ");
        for(String temp:strarray)
        {
            System.out.println("temp---"+temp);
        }
        System.out.println("length---"+str.length());
        for(int i=0;i<str.length();i++)
        {
            char[] revstr =strarray[i].toCharArray();
            for(int j=revstr.length-1;j>=0;j--)
            {
                System.out.print(revstr[j]);
            }
            System.out.print(" ");
        }
    }
}
