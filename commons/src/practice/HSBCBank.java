package practice;

/**
 * Created by Admin on 2/12/2021.
 */
public class HSBCBank implements  USBank
{
    public void credit()
    {
        System.out.println("HSBCBank credit");
    }
    public void debit()
    {
        System.out.println("HSBCBank debit");
    }
    public void moneytransfer()
    {
        System.out.println("HSBCBank moneytransfer");
    }

    public void carloan()
    {
        System.out.println("HSBCBank carloan");
    }
    public void educationloan()
    {
        System.out.println("HSBCBank educationloan");
    }

}
