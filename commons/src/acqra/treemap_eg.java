package acqra;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by Admin on 4/1/2021.
 */
public class treemap_eg
{
    public static void main(String[] args)
    {


// Tree Map creation
        TreeMap tmap = new TreeMap();
// Add elements to the treemap tmap
        tmap.put("Anna", new Double(3459.70));
        tmap.put("Annamu", new Double(321.56));
        tmap.put("David", new Double(1234.87));
        tmap.put("Adam", new Double(89.35));
        tmap.put("sagar", new Double(-20.98));
        Set set = tmap.entrySet();
        Iterator itr=set.iterator();
        while (itr.hasNext())
        {
            Map.Entry mp =(Map.Entry)itr.next();
            System.out.print(" Key : " + mp.getKey() + ";");
            System.out.println(" Value : "+ mp.getValue());
        }
        System.out.println();
        double val = ((Double)tmap.get("sagar")).doubleValue();
        tmap.put("sagar", new Double(val + 2500));
        System.out.println("Anabeth's new value: " + tmap.get("sagar"));
    }
}
