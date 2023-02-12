package acqra;

/**
 * Created by Admin on 4/8/2021.
 */
public class Java_alias
{
    public void func1()
    {
        System.out.println("called func1");
    }

}
class Java_alias_one extends Java_alias
{
    public void func1()
    {
        System.out.println("called func1");
    }
    public void func2()
    {
        System.out.println("called func2");
    }
}
 class AliasEg
{
    public static void main(String[] args)
    {
        Java_alias_one[] obj = new Java_alias_one[10];
        Java_alias[] obj1= obj;
        obj1[0]= new Java_alias_one();
        obj[0].func1();
    }
}
