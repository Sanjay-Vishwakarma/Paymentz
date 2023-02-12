package acqra;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Admin on 4/17/2021.
 */
public class Hashset_eg
{
    public static void main(String[] args)
    {
        Set<Integer> set1 = new HashSet<Integer>();
        set1.addAll(Arrays.asList(new Integer[] {10,29,39,90,14}));

        Set<Integer> set2 = new HashSet<Integer>();
        set2.addAll(Arrays.asList(new Integer[] {1,80,10,29,90,11}));

        //get union as Hashset will add repeated values only once
        Set<Integer> union= new HashSet<Integer>(set1);
        union.addAll(set2);
        System.out.println(union);


    }
}
