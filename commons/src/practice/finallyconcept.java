package practice;

/**
 * Created by Admin on 2/17/2021.
 */
public class finallyconcept
{
    public static void tst1()
    {
        int i=10;

        try
        {
            System.out.println(i/0);
            System.out.println("inside try");
            throw new RuntimeException("test");


        }
        catch(Exception e)
        {
            System.out.println("inside catch");
        }
        finally
        {
            System.out.println("inside finally even after any Exception");
        }
    }
    public static void main(String[] args)
    {
tst1();
    }

}
