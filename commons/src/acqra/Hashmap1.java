package acqra;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Admin on 4/12/2021.
 */
public class Hashmap1
{
    public static void main(String[] args)
    {
        HashMap<String,String> capitalMap= new HashMap<String ,String>();
        capitalMap.put("India","New Delhi");
        capitalMap.put("USA","Washington DC");
        capitalMap.put("UK","London");
        capitalMap.put("UK","London11");
        capitalMap.put(null,"Berlin");
        capitalMap.put(null,"LA");
        System.out.println(capitalMap.get("USA"));
        System.out.println(capitalMap.get("UK"));
        System.out.println(capitalMap.get(null));

        //Iterate by using Keyset
        Iterator<String>iterator = capitalMap.keySet().iterator();
        while (iterator.hasNext())
        {
            String key =iterator.next();
            String value=capitalMap.get(key);
            System.out.println(key+" : "+value);
        }
        //Iterate by using entrySet
        Iterator<Map.Entry<String,String>> iterator1=capitalMap.entrySet().iterator();
        while (iterator1.hasNext())
        {
            Map.Entry<String,String> entry=iterator1.next();
            System.out.println("Key = "+entry.getKey()+" and value = "+entry.getValue());
        }
        //Iterate hashmap using java 8 for each & lambda
        capitalMap.forEach((k,v)-> System.out.println("key = "+k+" and value = "+v));
    }
}
