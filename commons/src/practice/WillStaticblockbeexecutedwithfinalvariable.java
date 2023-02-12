package practice;

/**
 * Created by Admin on 3/22/2021.
 */
public class WillStaticblockbeexecutedwithfinalvariable
{
    public static void main(String[] args)
    {
        System.out.println(main.x);
    }

}
class main
{
    public static final int x=100;
    static
    {
        System.out.println("main class static block");
    }
}
