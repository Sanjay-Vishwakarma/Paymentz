package acqra;

import java.util.Hashtable;

/**
 * Created by Admin on 4/26/2021.
 */
public class HashtableConcept
{
    public static void main(String[] args)
    {
        Hashtable hashtable=new Hashtable();
        hashtable.put("a","w");
        hashtable.put(1,"kii");
        hashtable.put("aa",8.9);
        hashtable.put(11,1.3f);
        System.out.println(hashtable);
        System.out.println(hashtable.containsKey("aa"));
        System.out.println(hashtable.contains(8.9));
        hashtable.remove(1);
        System.out.println(hashtable.size());
        System.out.println(hashtable);

        Hashtable<String,Integer>hashtable1 = new Hashtable<>();
        hashtable1.put("kevin",90009000);
        hashtable1.put("k",90009000);
    }
}
