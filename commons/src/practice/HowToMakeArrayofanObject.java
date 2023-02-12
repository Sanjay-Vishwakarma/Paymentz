package practice;

/**
 * Created by Admin on 10/25/2020.
 */
public class HowToMakeArrayofanObject
{
        String type ="Spider Monkey";
    public static void method_name()
    {
        System.out.println("Thing to print");
    }
}
    class HowToMakeArrayofanObject_Main
    {
    public static void main(String args[])
    {
        HowToMakeArrayofanObject h = new HowToMakeArrayofanObject();
        HowToMakeArrayofanObject h1 = new HowToMakeArrayofanObject();
        HowToMakeArrayofanObject h2 = new HowToMakeArrayofanObject();

        HowToMakeArrayofanObject[] howToMakeArrayofanObjects ={h,h1,h2};
        for(HowToMakeArrayofanObject foreachobj : howToMakeArrayofanObjects)
        {
            foreachobj.method_name();

        }


    }
}
