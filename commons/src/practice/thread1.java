package practice;

/**
 * Created by Admin on 3/2/2021.
 */
public class thread1 extends Thread
{
    public void run()
    {
        for(int i=1;i<=5;i++)
        {
            System.out.println("Dancing");
            try
            {
                Thread.sleep(70);
            }
            catch (Exception e)
            {

            }

        }
    }
}
