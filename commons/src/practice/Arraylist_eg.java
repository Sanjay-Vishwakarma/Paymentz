package practice;

import java.util.ArrayList;

/**
 * Created by Admin on 8/30/2020.
 */
public class Arraylist_eg
{
    public static void main(String[] args)
    {
        ArrayList arrayList = new ArrayList();
        arrayList.add("kholi");
        arrayList.add("Anderson");
        arrayList.add("Burn");
        arrayList.add(arrayList.size());
        System.out.println(arrayList);
        for(int i=0;i<arrayList.size();i++)
        {
            System.out.println(arrayList);
        }
    }
}
