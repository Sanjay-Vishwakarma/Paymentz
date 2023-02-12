package practice;

/**
 * Created by Admin on 10/5/2020.
 */
class Thread_sleep_eg extends Thread
{
    public void run()
    {
        System.out.println("Thread A execute");
        try
        {
            System.out.println("Thread A Sleep");
            sleep(2000);
            System.out.println("Thread A Sleep Over");
        }
        catch (Exception e)
        {

        }
    }
}
class  Thread_sleep_eg2 extends Thread
{
    public void run()
    {
        System.out.println("Thread B execute");
    }
}
class  Thread_sleep_eg3 extends Thread
{
    public void run()
    {
        System.out.println("Thread C execute");
    }
}
class Thread_Main
{
    public static void main(String[] args)
    {
        System.out.println("Main thread execute");
        Thread_sleep_eg a= new Thread_sleep_eg();
        Thread_sleep_eg2 b= new Thread_sleep_eg2();
        Thread_sleep_eg3 c= new Thread_sleep_eg3();
        a.start();
        b.start();
        c.start();
    }
}
