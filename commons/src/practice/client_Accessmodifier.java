package practice;
import acqra.AccessModifier_2;
//import acqra.two; // As the class is default it is not accessible outside package
/**
 * Created by Admin on 5/4/2021.
 */

class child extends AccessModifier_2
{

}
public class client_Accessmodifier
{
    public static void main(String[] args)
    {
    AccessModifier_2 obj = new AccessModifier_2();
        obj.publicfunc();

    }
}

//Class
//Public  is accessible outside your package
//default is not accessible outside package

