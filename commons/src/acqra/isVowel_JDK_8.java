package acqra;

import com.google.common.base.CharMatcher;

import java.util.function.IntPredicate;

/**
 * Created by Admin on 5/12/2021.
 */
public class isVowel_JDK_8
{
    //java
    public static boolean isVowel(char chr)
    {
        return chr=='a'||chr=='e'||chr=='i'||chr=='o'||chr=='u'||chr=='A'||chr=='E'||chr=='I'||chr=='O'||chr=='U';
    }
    public static void main(String[] args)
    {
        String str="sagar";
        int vCount=0;
        for(int i=0;i<str.length();i++)
        {
            if(isVowel(str.charAt(i)))
            {
                vCount++;
            }
        }
        System.out.println(vCount);

        //java 8 streams
        String str2="virat kohli";
        IntPredicate intPredicate = new IntPredicate()//it is interface
        {
            @Override
            public boolean test(int chr)
            {
                return chr=='a'||chr=='e'||chr=='i'||chr=='o'||chr=='u'||chr=='A'||chr=='E'||chr=='I'||chr=='O'||chr=='U';

            }
        };
       long count= str2.chars().filter(intPredicate).count();
        System.out.println("using java 8 streams --- "+count);

        //using google guava
        String str3="rishabh pant";
        int a= CharMatcher.anyOf("aeiouAEIOU").countIn(str3);
        System.out.println(a);
    }
}
