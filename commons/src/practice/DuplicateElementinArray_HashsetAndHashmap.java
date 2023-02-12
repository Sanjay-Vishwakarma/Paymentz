package practice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Created by Admin on 8/30/2020.
 */
public class DuplicateElementinArray_HashsetAndHashmap
{
    public static void main(String[] args)
    {
        String cricketers[]={"rishabh","kohli","rohit","kohli","anderson","rishabh"};

        //Using Hashset  0(n)
       /* Set<String> set=new HashSet<String>();//set is interface & hashset is class
        for(String foreachobj:cricketers)
        {
            if(set.add(foreachobj)==false)//Because Set doesnt add duplicate elements
            {
                System.out.println("Duplicate "+foreachobj);
            }

        }*/

        //Using Hashmap     0(2n)
        Map<String,Integer> map = new HashMap<String,Integer>();
        for(String foreachobj:cricketers)
        {
          Integer count= map.get(foreachobj);
            if(count==null)
            {
            map.put(foreachobj,1);
            }
            else
            {
                map.put(foreachobj, ++count);
            }
        }
       Set<Entry<String,Integer>> entryset=map.entrySet(); //entryset is a collection
        for(Entry<String,Integer> entry:entryset)// to iterate above collection we are using this foreach loop
        {
            if(entry.getValue()>1)
            {
                System.out.println("Duplicate value is "+entry.getKey());//to get value we are using key
            }
        }

    }
}
