package acqra;

/**
 * Created by Admin on 5/3/2021.
 */
 class AccessModifier_1
{
    private static int a=10;

    private static void method1()
    {
        System.out.println("Method1 Data");
    }
   public static void main(String[] args)
    {
        a=20;
        System.out.println(a);
    }
}
class OtherClass
{
    public static void main(String[] args)
    {
       //  System.out.println(AccessModifier_1.a);
        // AccessModifier_1.method1();
    }
}
