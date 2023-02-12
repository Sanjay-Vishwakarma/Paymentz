package acqra;

/**
 * Created by Admin on 5/11/2021.
 */
public class Count_the_Number_of_Occurrences_of_Character_in_String_using_Java_8_Streams
{
    public static void main(String[] args)
    {
        String str="Sagar Sonar";
        System.out.println(getcharcount(str,'r'));
    }
    public static long getcharcount(String str,char c)
    {
        return str.chars().filter(var->(char)var =='r').count();

    }
}
