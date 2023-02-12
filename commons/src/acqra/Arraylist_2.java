package acqra;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Admin on 4/26/2021.
 */
public class Arraylist_2<E>
{
    public static void main(String[] args)
    {
        Employee e1=new Employee("Virat",32,"Rcb");
        Employee e2=new Employee("Rishabh",23,"Dc");
        Employee e3=new Employee("Jadega",33,"Csk");
        Employee e4=new Employee("Graeme",29,"RR");

        //create arraylist using constructor
        ArrayList<Employee> arraylist=new ArrayList<Employee>();
        arraylist.add(e1);
        arraylist.add(e2);
        arraylist.add(e3);
        arraylist.add(e4);

        //Iterate to traverse the values
        Iterator<Employee>iterator=arraylist.iterator();
        while (iterator.hasNext())
        {
            Employee emp=iterator.next();
            System.out.println(emp.name);
            System.out.println(emp.age);
            System.out.println(emp.dept);
        }


    }
}
