package practice;

/**
 * Created by Admin on 3/26/2021.
 */
interface firstinterface
{
public void methodfirstinterface();
}
interface secondinterface
{
public void methodsecondinterface();
}
public class interface2_educba implements firstinterface,secondinterface
{
    public void methodfirstinterface()
    {
        System.out.println("Welcome to first interface");
    }

    public void methodsecondinterface()
    {
        System.out.println("Again Welcome to second interface");
    }

    public static void main(String[] args)
    {
        interface2_educba obj=new interface2_educba();
        obj.methodfirstinterface();
        obj.methodsecondinterface();
    }
}
