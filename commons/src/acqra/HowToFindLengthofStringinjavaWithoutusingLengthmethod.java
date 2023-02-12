package acqra;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Admin on 5/10/2021.
 */
public class HowToFindLengthofStringinjavaWithoutusingLengthmethod
{
    public static void main(String[] args)
    {
        String str = "Developer";
        //1st way
        System.out.println(str.toCharArray().length);
        //2nd way
        System.out.println(str.lastIndexOf(""));
        //3rd way
        Matcher matcher = Pattern.compile("$").matcher(str);
        matcher.find();
        int len = matcher.end();
        System.out.println(len);
        //4th way
        System.out.println(str.split("").length);
        //5th Way
        int count = 0;
        for (char c : str.toCharArray())
        {
            count++;
        }
        System.out.println(count);

        System.out.println(getstringlength("Developer"));
    }

    //6th way
        public static int getstringlength(String str_for_split)
        {
          String st[]=str_for_split.split("");
          int count=0;
            for(String s: st)
            {
                count += s.toCharArray().length;
            }
            return count;
        }
    }
