package acqra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Created by Admin on 5/15/2021.
 */
    public class jdbc_durgesh_create
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

            String createQuery="create table tableone(id int primary key,name varchar(100),city varchar(100))";
            //create a statement
            Statement st =con.createStatement();
            st.executeUpdate(createQuery);// if query is bringing data than executeUpdate // If any error comes then code doesnot go ahead of this line
            System.out.println("Table created");

            con.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
