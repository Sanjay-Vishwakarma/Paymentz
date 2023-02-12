package acqra;

/**
 * Created by Admin on 4/1/2021.
 */
public class super1//parent class
{
    super1()
    {
        System.out.println("My parent name is Pradeep");
    }

}
class child extends super1
        {
        child()
        {
            super();
            System.out.println("My name is Bunty");
        }
        }
class main_super

{
    public static void main(String[] args)
    {
        child obj = new child();

    }
}
