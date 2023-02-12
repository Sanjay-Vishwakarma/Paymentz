package practice;

/**
 * Created by Admin on 3/2/2021.
 */
public class multithreading
{
    public static void main(String[] args)
    {
        thread1 obj=new thread1();
        thread2 obj2=new thread2();
        obj.start();
        try{
            Thread.sleep(30);

        }
        catch (Exception e)
        {

        }
        obj2.start();
    }
}
