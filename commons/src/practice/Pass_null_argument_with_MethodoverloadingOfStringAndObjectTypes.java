package practice;

/**
 * Created by Admin on 3/16/2021.
 */
public class Pass_null_argument_with_MethodoverloadingOfStringAndObjectTypes
{

    public static void main(String[] args)
    {
        method(null);
    }
        public static void method(Object o)
        {
            System.out.println("Object Argument");
        }
    public static void method(String s)
    {
        System.out.println("String Argument");
    }

}
