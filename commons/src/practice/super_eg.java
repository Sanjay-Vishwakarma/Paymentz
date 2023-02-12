package practice;

/**
 * Created by Admin on 3/18/2021.
 */
public class super_eg
{
    public void method_one()
    {
        System.out.println("Base class");
    }

}
 class super_eg2 extends super_eg
{
    public void method_two()
    {
        super.method_one();
    }
}

class test_super_main
{
    public static void main(String[] args)
    {
        super_eg2 obj = new super_eg2();
        obj.method_two();
    }
}
