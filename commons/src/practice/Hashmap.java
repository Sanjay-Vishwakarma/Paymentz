package practice;

import java.util.*;

/**
 * Created by Admin on 3/8/2021.
 */
public class Hashmap
{
    public static void main(String[] args)
    {
        HashMap<Integer,String> hashMap=new HashMap<Integer,String>();
        hashMap.put(78,null);
        hashMap.put(18,"kohli");
        hashMap.put(90,"axar");
        hashMap.put(10,"ishant");
        hashMap.put(100,"rohit");
        hashMap.put(86,"kishan");
        hashMap.put(38,"dhawan");
        hashMap.put(27,"sehwag");
        hashMap.put(27,"sachin");
        hashMap.put(null,null);

        Set set =hashMap.entrySet();

        Iterator iterator=set.iterator();
        while (iterator.hasNext())
        {
            Map.Entry obj=(Map.Entry)iterator.next();
            System.out.println(obj.getKey()+" "+obj.getValue());
        }
    }
}
