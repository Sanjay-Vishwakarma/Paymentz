package practice;

/**
 * Created by Admin on 2/19/2021.
 */
public class Encapsulation
{
    private int custid;
    private String custname;
    private long accno;

    public int getCustid()
    {
    return custid;
    }

    public void setCustid(int custid)
    {
        this.custid=custid;
    }

    public String getCustname()
    {
        return custname;
    }

    public void setCustname(String custname)
    {
        this.custname=custname;
    }

    public long getAccno()
    {
        return accno;
    }

    public void setAccno(long accno)
    {
        this.accno = accno;
    }

    public static void main(String[] args)
    {
        Encapsulation encapsulation=new Encapsulation();
        encapsulation.setCustid(1);
        encapsulation.setCustname("bunty");
        encapsulation.setAccno(555554545);
        System.out.println(encapsulation.getCustid()+" "+encapsulation.getCustname()+" "+encapsulation.getAccno());
    }
}
