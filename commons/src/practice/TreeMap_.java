package practice;

/**
 * Created by Admin on 3/8/2021.
 */
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
public class TreeMap_
{
    public static void main(String[] args)
    {
        TreeMap<String,Integer> treeMap= new TreeMap();
        treeMap.put("sagar",786786786);
        treeMap.put("pant",565654654);
        treeMap.put("rishi",534353486);
        treeMap.put("johny",124234234);
        treeMap.put("kemp",780320923);
        treeMap.put("austinr",700322786);
        treeMap.put("kevin",435345455);
        treeMap.put("hghgh",null);
        Set set=treeMap.entrySet();
        Iterator iterator =set.iterator();
        while(iterator.hasNext())
        {
            Map.Entry obj=(Map.Entry)iterator.next();
            System.out.println("Name : "+obj.getKey()+" "+"Phone No : "+obj.getValue() );
        }


    }
}
