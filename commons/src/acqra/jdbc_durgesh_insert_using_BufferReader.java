package acqra;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.io.*;

/**
 * Created by Admin on 5/15/2021.
 */
public class jdbc_durgesh_insert_using_BufferReader
{
    public static void main(String[] args)
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            String url="jdbc:mysql://localhost:3306/sagar";
            String username="root";
            String password="root";

            Connection con= DriverManager.getConnection(url,username,password);
            String insertQuery="insert into tableone(id,name,city)values(?,?,?)";
            PreparedStatement ps=con.prepareStatement(insertQuery);
        /*  ps.setString(1,"1");
            ps.setString(2,"bunty");
            ps.setString(3,"Mumbai");*/

            //below code is inserting using bufferedReader
            BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter id ");
            String id=br.readLine();
            System.out.println("Enter name ");
            String name=br.readLine();
            System.out.println("Enter city");
            String city=br.readLine();
            ps.setString(1,id);
            ps.setString(2,name);
            ps.setString(3,city);


            ps.executeUpdate();
            System.out.println("Inserted Data");
            con.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }


    }
}
