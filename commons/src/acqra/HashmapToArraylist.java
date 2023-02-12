package acqra;

import java.util.*;

/**
 * Created by Admin on 4/26/2021.
 */
public class HashmapToArraylist
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

        Iterator iterator=hashMap.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry pairs=(Map.Entry)iterator.next();
            System.out.println(pairs.getKey()+" "+pairs.getValue());
        }

        hashMap.forEach((k,v)-> System.out.println("key= "+k+" value= "+v));
        //Convert hashmap into arraylist for values
        List<String> arrayList= new ArrayList<String>(hashMap.values());
        for(String str:arrayList)
        {
            System.out.println(str);
        }
        //Convert hashmap into arraylist for keys
        List<Integer> arrayList1= new ArrayList<Integer>(hashMap.keySet());
        for(int str:arrayList1)
        {
            System.out.println(str);
        }
    }
}
