package acqra;

/**
 * Created by Admin on 4/1/2021.
 */
public class super_with_parameter_constructor
{
    protected String country="India";
    super_with_parameter_constructor()
    {

    }//If default constructor is not there it will give error
    super_with_parameter_constructor(String name)
    {
        System.out.println("My name is "+name +" "+"country name is "+country);
    }
}
class childone extends super_with_parameter_constructor
{
    childone()
    {
        super("Sagar");
        System.out.println("My country name is "+super.country);
        System.out.println("My name is vijay");
    }
}
class Main
        {
            public static void main(String[] args)
            {
                childone obj = new childone();
            }
        }