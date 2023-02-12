/*
Comm3DResponseVO comm3DResponseVOObject = new Comm3DResponseVO();
CommRequestVO commRequestVOObject = (CommRequestVO) requestVO; // Narrowing Typecasting
CommMerchantVO commmerchantVOObject = commRequestVOObject.getCommMerchantVO();
CommTransactionDetailsVO commtransactiondetailsVOObject = commRequestVO.getTransDetailsVO();
*/
package practice;



public class p1
{
    private String name;
    private int rollno;
    private char grade;
    private String address;
    private float percentage;
    public String getname()
    {
        return name;
    }
    public void setname(String name)
    {
        this.name=name;
    }
    public int getrollno()
    {
        return rollno;
    }
    public void setrollno(int rollno)
    {
        this.rollno=rollno;
    }
    public char getgrade()
    {
        return grade;
    }
    public void setgrade(char grade)
    {
        this.grade=grade;
    }
    public String getaddress()
    {
        return address;
    }
    public void setaddress(String address)
    {
        this.address=address;
    }
    public float getpercentage()
    {
        return percentage;
    }
    public void setpercentage(float percentage)
    {
        this.percentage=percentage;
    }
}
