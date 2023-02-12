package acqra;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Admin on 4/12/2021.
 */
public class HashMapCompare
{
    public static void main(String[] args)
    {
        HashMap<Integer,String>hashMap1= new HashMap<Integer,String>();
        hashMap1.put(1,"A");
        hashMap1.put(2,"B");
        hashMap1.put(3,"C");

        HashMap<Integer,String>hashMap2= new HashMap<Integer,String>();
        hashMap2.put(3,"C");
        hashMap2.put(1,"A");
        hashMap2.put(2,"B");

        HashMap<Integer,String>hashMap3= new HashMap<Integer,String>();
        hashMap3.put(3,"C");
        hashMap3.put(9,"A");
        hashMap3.put(2,"B");
        hashMap3.put(3,"D");

        //1.on the basis of key value use equals method
        System.out.println(hashMap1.equals(hashMap2));
        System.out.println(hashMap2.equals(hashMap3));
        System.out.println(hashMap1.equals(hashMap3));

        //2.Compare same keys using keySet()
        System.out.println(hashMap1.keySet().equals(hashMap2.keySet()));
        System.out.println(hashMap2.keySet().equals(hashMap3.keySet()));
        System.out.println(hashMap1.keySet().equals(hashMap3.keySet()));

        //3.find out extra keys by using HashSet
        HashMap<Integer,String> hashMap4= new HashMap<Integer,String>();
        hashMap4.put(1,"A");
        hashMap4.put(2,"B");
        hashMap4.put(3,"C");
        hashMap4.put(4,"D");

        HashSet<Integer> combineKeys = new HashSet<>(hashMap1.keySet());
        combineKeys.addAll(hashMap4.keySet());
        combineKeys.removeAll(hashMap1.keySet());
        System.out.println(combineKeys);



    }
}
