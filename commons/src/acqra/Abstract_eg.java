package acqra;

/**
 * Created by Admin on 3/30/2021.
 */
public abstract class Abstract_eg
{
    public abstract void IplTeam();
    public void NationalTeam()
{
    System.out.println("National Team is India");
}
}
class player1 extends Abstract_eg
{
    String player1name="virat";
    public void IplTeam()
    {
        System.out.println("RCB is IplTeam of "+player1name);
    }

    @Override
    public void NationalTeam()
    {
        super.NationalTeam();
    }
}
class player2 extends Abstract_eg
{
    String player2name="Rishabh";

    @Override
    public void IplTeam()
    {
        System.out.println("RCB is IplTeam of "+player2name);

    }
    public void NationalTeam()
    {
        super.NationalTeam();
    }
}
class main_Abstract_Eg
{
    public static void main(String[] args)
    {
        Abstract_eg obj1= new player1();
        obj1.IplTeam();
        obj1.NationalTeam();
        Abstract_eg obj2= new player2();
        obj2.IplTeam();
        obj2.NationalTeam();
    }
}