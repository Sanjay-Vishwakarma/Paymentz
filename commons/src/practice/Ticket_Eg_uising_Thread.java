package practice;

/**
 * Created by Admin on 3/20/2021.
 */
class Ticket_Eg_uising_Thread implements Runnable
{
    String str;
    Ticket_Eg_uising_Thread(String str)
    {
        this.str=str;
    }


    public void run()
    {
        for(int i=0;i<=5;i++)
        {
            System.out.println(str +" " +i);
            try
            {
                Thread.sleep(3000);
            }
            catch (InterruptedException ie)
            {
                ie.printStackTrace();
            }
        }
    }
}
class Main_Thread
{
    public static void main(String[] args)
    {
        Ticket_Eg_uising_Thread ticket_eg_uising_thread1= new Ticket_Eg_uising_Thread("Give me ticket");
        Ticket_Eg_uising_Thread ticket_eg_uising_thread2= new Ticket_Eg_uising_Thread("Show me movie");
        Thread t1= new Thread(ticket_eg_uising_thread1);
        Thread t2= new Thread(ticket_eg_uising_thread2);
        t1.run();
        t2.run();
    }
}
