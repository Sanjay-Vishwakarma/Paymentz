package acqra;

/**
 * Created by Admin on 4/20/2021.
 */
public class method_in_java
{
    //below are non static methods
    //Main mthod means starting point of execution
    public static void main(String[] args)
    {
        method_in_java obj = new method_in_java();
        obj.test();
        int pqr_var=obj.pqr();
        System.out.println(pqr_var);

    }
    public void test()//no input
    {
        System.out.println("test method");
    }
    public int pqr()//no input some output
    {
        System.out.println("pqr method");
        int a=1;
        int b=2;
        int c=a+b;
        return c;
    }
    public String method1() //no input some output
    {
        System.out.println("Method1");
        String str="Hi";
        return str;
    }
    public float division(int num1,int num2)// num1,num2 are inputs parameters
    {
        System.out.println("Division method");
        float div=num1/num2;
        return div;
    }
}
