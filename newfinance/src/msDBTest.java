import java.sql.*;

/**
 * Created by IntelliJ IDEA.
 * User: jignesh.r
 * <p/>
 * Date: Jun 6, 2006
 * Time: 2:19:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class msDBTest
{
    static String s = null;

    public static void main(String[] args) throws Exception
    {

/*        System.out.println("Entering main");
        try
        {
            System.out.println(msDBTest.foo());
        }
        catch (Exception e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        System.out.println("B4 second call");
        System.out.println(msDBTest.foo());
        System.out.println("Leaving main");*/
        //print("asasd");
        try
        {
            Class.forName("org.gjt.mm.mysql.Driver");
        }
        catch (ClassNotFoundException e)
        {

        }
        String url = "jdbc:mysql://localhost:3306/ver2";
        String user = "root";
        String passw = "mysql";

        long startTime = System.currentTimeMillis();
        Connection conn = (Connection) DriverManager.getConnection(url, user, passw);
        System.out.println("Total Time to get connection:  " + (System.currentTimeMillis() - startTime) / 1000 + " secs");
        Statement stmnt = (Statement) conn.createStatement();
        try
        {
            ResultSet rs = (ResultSet) stmnt.executeQuery("select icicitransid as trackingid,status as stat,toid from transaction_icicicredit");
            ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
            System.out.println(rsmd.getColumnLabel(2) + "\t||\t" + rsmd.getColumnLabel(3));
            while (rs.next())
            {
                System.out.println(rs.getString(2) + "\t||\t" + rs.getString(3));
            }
        }
        catch (SQLException e)
        {

        }
        finally
        {
            stmnt.close();
            conn.close();
        }

        /*  String s1=generateCheckSum("AddFund-C-60-710",1000.0,221367.5,"Y","0.7821886852661073","44q9dn7WCUrLHgi8bPsdiBIlLi6WaHI0");
                String s2=generateCheckSum("AddFund-C-60-710",1000.0,221367.5,"N","0.7821886852661073","44q9dn7WCUrLHgi8bPsdiBIlLi6WaHI0");

                System.out.println("Original= "+(Long.parseLong(s2)-Long.parseLong(s1)));



                s1=generateCheckSum("AddFund-C-60-810",1000.0,221367.5,"Y","0.7821886852661073","44q9dCUrLHgi8bPsdiBIlLi6WaHI0");
                s2=generateCheckSum("AddFund-C-60-810",1000.0,221367.5,"N","0.7821886852661073","44q9dCUrLHgi8bPsdiBIlLi6WaHI0");


                System.out.println("transid changed = "+(Long.parseLong(s2)-Long.parseLong(s1)));

                 s1=generateCheckSum("AddFund-C-60-715",1000.0,221367.5,"Y","0.7821886852661073","44q9dn7WrLHgi8bPsdiBIlLi6WaHI0");
                 s2=generateCheckSum("AddFund-C-60-715",1000.0,221367.5,"N","0.7821886852661073","44q9dn7WrLHgi8bPsdiBIlLi6WaHI0");

                System.out.println("transid changed = "+(Long.parseLong(s2)-Long.parseLong(s1)));

                 s1=generateCheckSum("AddFund-C-60-711",1000.0,221367.5,"Y","0.7821886852661073","44q9dn7WCUrLHgi8bPsdiBIlLi6WaHI");
                 s2=generateCheckSum("AddFund-C-60-711",1000.0,221367.5,"N","0.7821886852661073","44q9dn7WCUrLHgi8bPsdiBIlLi6WaHI");

                System.out.println("transid and amount changed = "+(Long.parseLong(s2)-Long.parseLong(s1)));

                 s1=generateCheckSum("Y","AddFund-C-60-610",54354,6435667.5,"0.127821886852661073","44q9dn7WCUrLHgi8bPsdiBIlLi6WaHI0");
                 s2=generateCheckSum("N","AddFund-C-60-610",54354,6435667.5,"0.127821886852661073","44q9dn7WCUrLHgi8bPsdiBIlLi6WaHI0");

                System.out.println("transid and amount changed = "+(Long.parseLong(s2)-Long.parseLong(s1)));

                s1=generateCheckSum("0.127821886852661073","AddFund-C-60-710",54354,16435667.5,"Y","44q9dn7WCUrLHgi8bPsdiBIlLi6WaHI0");
                 s2=generateCheckSum("0.127821886852661073","AddFund-C-60-710",54354,16435667.5,"N","44q9dn7WCUrLHgi8bPsdiBIlLi6WaHI0");

                System.out.println("transid and amount changed = "+(Long.parseLong(s2)-Long.parseLong(s1)));

                s1=generateCheckSum("0.1127821886852661073","AddFund-C-60-610",54354,16435667.5,"Y","qwertyuioplkjhgfdsazxcvbnmlkjhgf");
                s2=generateCheckSum("0.1127821886852661073","AddFund-C-60-610",54354,16435667.5,"N","qwertyuioplkjhgfdsazxcvbnmlkjhgf");


                System.out.println("transid and amount changed = "+(Long.parseLong(s2)-Long.parseLong(s1)));

                s1=generateCheckSum("AddFund-C-60-710",1000.0,221367.5,"Y","0.7821886852661073","12345678901234567890123456789012");
                s2=generateCheckSum("AddFund-C-60-710",1000.0,221367.5,"N","0.7821886852661073","12345678901234567890123456789012");

                System.out.println("Original= "+(Long.parseLong(s2)-Long.parseLong(s1)));
        */
/*
       String  s1=generateCheckSum("AddFund-C-60-710",1000.0,221367.5,"N","0.1234886852661073","qwertyuioplkjhgfdsazxcvbnmlkjhgf");

        String s3=generateCheckSum("AddFund-C-60-710",1000.0,221367.5,"N","0.1234886852661073","12345678901234567890123456789012");
        String s4=generateCheckSum("AddFund-C-60-710",1000.0,221367.5,"Y","0.1234886852661073","12345678901234567890123456789012");

        String s2=generateCheckSum("AddFund-C-60-710",1000.0,221367.5,"Y","0.1234886852661073","qwertyuioplkjhgfdsazxcvbnmlkjhgf");
*/

        //long diff=Long.parseLong(s4)-Long.parseLong(s3);
/*
        System.out.println("hacker diff = "+diff);


        System.out.println("reseller checksum for Y= "+Long.parseLong(s2));

        System.out.println("reseller diff = "+(Long.parseLong(s2)-Long.parseLong(s1)));


        System.out.println("hacker checksum for Y= "+(Long.parseLong(s1)+diff));
        //System.out.println("hacker  checksum for Y= "+(Long.parseLong(s1)-diff));

        System.out.println("check this = "+(Long.parseLong(s2)-Long.parseLong(s1)));

        s1=generateCheckSum("AddFund-C-60-710",1000.0,221367.5,"Y","0.1234886852661073","qwertyuioplkjhgfdsazxcvbnmlkjhgf");
        s2=generateCheckSum("AddFund-C-60-710",1000.0,221367.5,"N","0.1234886852661073","qwertyuioplkjhgfdsazxcvbnmlkjhgf");

        System.out.println("Original= "+(Long.parseLong(s2)-Long.parseLong(s1)));
*/


    }

/*
     private static String generateCheckSum(String transId, double sellingAmount, double accountingAmount, String status,
                                            String rkey, String key)
    {
        String str = transId + "|" + sellingAmount + "|" + accountingAmount + "|" +
                status + "|" + rkey + "|" + key;

        Adler32 adl = new Adler32();
        System.out.println("CheckSumStr: " + str);
        adl.update(str.getBytes());

        return String.valueOf(adl.getValue());
    }

       private static String generateCheckSum(String status,String transId, double sellingAmount, double accountingAmount,
                                              String rkey, String key)
    {
        String str = status+"|"+transId + "|" + sellingAmount + "|" + accountingAmount + "|" +
                 rkey + "|" + key;

        Adler32 adl = new Adler32();
        System.out.println("CheckSumStr: " + str);
        adl.update(str.getBytes());

        return String.valueOf(adl.getValue());
    }*/

    private static String generateCheckSum(String transId, double sellingAmount, double accountingAmount, String status,
                                           String rkey, String key)
    {
        String str = transId + "|" + sellingAmount + "|" + accountingAmount + "|" +
                status + "|" + rkey + "|" + key;

        String checksum = null;


        return checksum;
    }

    public static void print(String ...a)
    {
        if (a.length != 0)
        {
            System.out.println(a[0]);
        }

    }


}
