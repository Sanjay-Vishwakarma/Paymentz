package practice;

import com.directi.pg.Database;
import com.payment.b4payment.vos.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

/**
 * Created by Admin on 7/26/2020.
 */
public class crud_Eg
{
    public static void main(String[] args)
    {
        try
        {
          Connection conn = Database.getConnection();
            if(conn!=null)
            {
                System.out.println("Connection is created");
            }
            insert(conn);
          //  select(conn);
        }
        catch(Exception e)
        {

        }
    }

    public static void insert(Connection conn)
    {
        System.out.println("Inside insert query");
        String Name="Kobe";
        String Acc_number="1403267895467865";
        String Balance="80900";
        String Branch="America";
        String Bankname="AB Bank";
        Date date = new Date();
       try

        {
            System.out.println("startTime---" + date.getTime());
            String insertquery ="insert into banktable(Name,Acc_number,Balance,Branch,Bankname)values (?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(insertquery);
            ps.setString(1, Name);
            ps.setString(2, Acc_number);
            ps.setString(3, Balance);
            ps.setString(4, Branch);
            ps.setString(5, Bankname);

            int var=ps.executeUpdate();
            if(var>0)
            {
                System.out.println("Successfully inserted");
            }
            else
            {
                System.out.println("Failed");
            }
            System.out.println("EndTime---"+new Date().getTime());
            System.out.println("Difference---"+(new Date().getTime()-date.getTime()));
        }
        catch(Exception e)

        {
            e.printStackTrace();
        }
    }

    public static void select(Connection conn)
    {
        System.out.println("Inside Select ");
        try
        {
            String selectquery="select * from banktable where Balance='89000'";
            PreparedStatement ps = conn.prepareStatement(selectquery);
            ResultSet resultSet = ps.executeQuery();
            if(resultSet!=null)
            {
                while(resultSet.next())
                {
                    String name=resultSet.getString("Name");
                    String acc_number=resultSet.getString("Acc_Number");
                    String balance=resultSet.getString("Balance");
                    String branch=resultSet.getString("Branch");
                    String bankname=resultSet.getString("Bankname");
                    System.out.println(name+"\t"+acc_number+"\t"+balance+"\t"+branch+"\t"+bankname+"\t");

                }
            }
            else
            {
                System.out.println("Failed no data found");
            }
        }
        catch(Exception e)

        {
e.printStackTrace();
        }
    }
}
