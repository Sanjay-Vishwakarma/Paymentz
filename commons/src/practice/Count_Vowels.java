package practice;

/**
 * Created by Admin on 3/20/2021.
 */
public class Count_Vowels
{
    static boolean isVowel(char ch)
    {
        ch=Character.toLowerCase(ch);
        return (ch=='a'||ch=='e'||ch=='i'||ch=='o'||ch=='u');
    }
    static int countvowels(String str)
    {
        int count = 0;
        for (int i = 0; i < str.length(); i++)
        {
            if (isVowel(str.charAt(i)))
            {
                count++;
            }

        }
        return count++;
    }

    public static void main(String[] args)
    {
        String str_in_psvn="Virat kohli and Rohit Sharma";
        System.out.println(countvowels(str_in_psvn));
    }
}
