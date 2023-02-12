package practice;

/**
 * Created by Admin on 3/2/2021.
 */
public class thread2 extends Thread
{

        public void run ()
        {
            for (int i = 1; i <= 5; i++)
            {
                System.out.println("Laughing");
                try
                {
                    Thread.sleep(100);
                }
                catch (Exception e)
                {

                }

            }

    }}