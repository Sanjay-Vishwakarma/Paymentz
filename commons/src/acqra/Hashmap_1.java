package acqra;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 7/15/2021.
 */
public class Hashmap_1
{
    public static void main(String[] args)
    {
        HashMap<String, Integer> hashMap = new HashMap<>();

        hashMap.put("Sagar", 808877);
        hashMap.put("Rishabh", 9098898);
        hashMap.put("Virat", 736261);
        hashMap.put("Jadega", 670027);
        hashMap.put("Sagar", 123456);
        if(hashMap.containsKey("12345"))
        {
            Integer a=hashMap.get("Sagar");
            System.out.println("Value of Sagar is "+a);
        }
    }
    public static void print(Map<String,Integer> map)
    {
        if(map.isEmpty())
        {
            System.out.println("Map is Empty");
        }
        else
        {
            System.out.println(map);
        }
    }

}
