package practice;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Admin on 2/9/2021.
 */
public class Linkedlist_Eg
{
    public static void main(String[] args)
    {
        LinkedList <String> linkedList = new LinkedList<String>();
        linkedList.add("xac");
        linkedList.add("dioiui");
        linkedList.add("trtrti");
        linkedList.add("brri");
        linkedList.add("amjdjdj");
        linkedList.add("oopp");
        linkedList.add("uruiooi");
        System.out.println("for loop");
        for(int i=0;i<linkedList.size();i++)
        {
            System.out.println(linkedList.get(i));
        }

        System.out.println("\nAdvanced for loop");
        for(String foreachvar:linkedList)
        {
            System.out.println(foreachvar);
        }

        System.out.println("\nIterator");
        Iterator i =linkedList.iterator();
        while(i.hasNext())
        {
            System.out.println(i.next());
        }

        System.out.println("\nwhile loop");



    }
}
