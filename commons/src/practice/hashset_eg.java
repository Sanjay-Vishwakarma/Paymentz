package practice;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Admin on 2/11/2021.
 */
public class hashset_eg
{
    public static void main(String[] args)
    {
        HashSet <String> hashSet = new HashSet<String>();
        hashSet.add("vbc");
        hashSet.add("ebc");
        hashSet.add("yyy");
        hashSet.add("vvv");
        hashSet.add("opu");
        hashSet.add("rrta");
        hashSet.add("sagfs");
        hashSet.add("vvv");
        hashSet.add(null);

        System.out.println(hashSet); //[sagfs, opu, vbc, ebc, vvv, yyy, rrta]

        System.out.println("for loop");
        for(int i=0;i<=hashSet.size();i++)
        {
            System.out.println(hashSet);
            break;
        }

        System.out.println("Iterator");
        Iterator<String> iterator = hashSet.iterator();
        while (iterator.hasNext())
        {
            System.out.println(iterator.next());
        }
    }
}
