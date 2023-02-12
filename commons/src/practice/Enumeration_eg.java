package practice;

import java.util.Enumeration;

/**
 * Created by Admin on 5/18/2020.
 */
public class Enumeration_eg
{
    public static void main(String[] args)
    {
        Enumeration enumerationObj;
        java.util.Vector vectorObj = new java.util.Vector();
        {
            vectorObj.add("One");
            vectorObj.add("three");
            enumerationObj=vectorObj.elements();
            while(enumerationObj.hasMoreElements())
            {
                System.out.println(enumerationObj.nextElement());
            }
        }
    }
}
