package practice;

import java.util.Scanner;

/**
 * Created by Admin on 1/15/2021.
 */
public class Freq_of_chars_eg
{
    public static void main(String[] args)
    {
        String str;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter statement ");
        str = sc.nextLine();
        int count = 0, len = 0;
        do
        {
            try
            {
                char store[] = str.toCharArray();
                len = store.length;
                count=0;
                for (int i = 0; i < len; i++)
                {
                    if ((store[0] == store[i]) && ((store[0] >= 65 && store[0] <= 91) || (store[0] >= 97 && store[0] <= 123)))
                    {
                        count++;
                    }
                }
                if (count != 0)
                {
                    System.out.println(store[0] + " " + count + " Times");
                }
                str = str.replace(""+store[0],"");
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }
        while (len!= 1);
    }
}
