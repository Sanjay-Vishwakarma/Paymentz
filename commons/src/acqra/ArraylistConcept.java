package acqra;

import java.awt.geom.Arc2D;
import java.util.ArrayList;

/**
 * Created by Admin on 4/26/2021.
 */
public class ArraylistConcept
{
    public static void main(String[] args)
    {
        ArrayList arrayList = new ArrayList();
        arrayList.add(147);
        arrayList.add(97);
        arrayList.add(8.9);
        arrayList.add("hi");
        arrayList.add('q');
        arrayList.add(8.9f);

        System.out.println(arrayList);
        System.out.println(arrayList.get(0));

        //To print Arraylist values in new line
        for(int i=0;i<arrayList.size();i++)
        {
            System.out.println(arrayList.get(i));
        }

        ArrayList<Integer> arrayList1 = new ArrayList<Integer>();
        arrayList1.add(79778);

       // ArrayList<float> arrayList2 = new ArrayList<float>(); //Mot allowed

    }
}
