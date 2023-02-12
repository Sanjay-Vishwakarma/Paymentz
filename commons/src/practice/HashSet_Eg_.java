package practice;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Admin on 3/19/2021.
 */
public class HashSet_Eg_
{
    public static void main(String[] args)
    {
        HashSet hashSet = new HashSet();
        hashSet.add("hhhhhh");
        hashSet.add("lllll");
        hashSet.add("iiiiiii");
        hashSet.add("qqqqqqq");
        hashSet.add("aa");
        hashSet.add("zzzzz");
        hashSet.add("uuuuuu");
        hashSet.add("qqqqqqq");
        hashSet.add("fffffff");

        Iterator<String> iterator=hashSet.iterator();
        while (iterator.hasNext())
        {
            System.out.println(iterator.next());
        }

    }
}
