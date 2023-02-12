package practice;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Admin on 5/14/2020.
 */
public class linkedlist
{
    public static void main(String[] args)
    {
        LinkedList<String> linkedList = new LinkedList<String>();
        linkedList.add("ciei");
        linkedList.add("abc");
        linkedList.add("452");
        linkedList.add("ciei");
        linkedList.addFirst("iamfirst");
        linkedList.addLast("iamlast");
      for(int i=0;i<linkedList.size();i++)
      {
          System.out.println(linkedList.get(i));
      }
    }
}
