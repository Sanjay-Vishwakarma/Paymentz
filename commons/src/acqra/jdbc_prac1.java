package acqra;

import java.sql.*;

/**
 * Created by Admin on 4/22/2021.
 */
public class jdbc_prac1
{
    public static void main(String[] args) throws ClassNotFoundException, SQLException
    {
        // Prog 1
/*        String url="jdbc:mysql://localhost:3306/sagar";
        String username="root";
        String password="root";
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn= DriverManager.getConnection(url,username,password);
        Statement st=conn.createStatement();
        ResultSet resultSet=st.executeQuery("select emailAddress from blacklist_email where id=1056");
        resultSet.next();
        String name=resultSet.getString("emailAddress");
        System.out.println(name);
        st.close();
        conn.close();*/

        //Prog 2

        /*String url="jdbc:mysql://localhost:3306/sagar";
        String username="root";
        String password="root";
        String query="select * from blacklist_email";
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn= DriverManager.getConnection(url,username,password);
        Statement st=conn.createStatement();
        ResultSet resultSet=st.executeQuery(query);
        String blacklist_email_data="";
        while(resultSet.next())
        {
            blacklist_email_data=resultSet.getInt(1)+":"+resultSet.getString(2)+":"+resultSet.getTimestamp(3)+":"+resultSet.getString(4)+":"+resultSet.getString(5)+":"+resultSet.getString(6);
            System.out.println(blacklist_email_data);
        }
        st.close();
        conn.close();*/

        //Prog 3
     /*   String url="jdbc:mysql://localhost:3306/sagar";
        String username="root";
        String password="root";
        String query="insert into blacklist_email values(1,'abc@abc.com','2020-04-22 17:01:07.0','myTransaction','Admin-12','Abc121')";
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn= DriverManager.getConnection(url,username,password);
        Statement st=conn.createStatement();
        int count=st.executeUpdate(query);
        System.out.println(count);

        st.close();
        conn.close();*/

        //Prog 4   Imp way to insert data uing Statement
       /* String url="jdbc:mysql://localhost:3306/sagar";
        String username="root";
        String password="root";
        int id=2;
        String emailAddress="def@gmail.com";
        String timestamp="2020-03-22 16:01:07.0";
        String reason="ReasonONE";
        String actionExecutorId="Admin-5";
        String actionExecutorName="Xyz0900";
        String query="insert into blacklist_email values("+id+",'"+emailAddress+"','"+timestamp+"','"+reason+"','"+actionExecutorId+"','"+actionExecutorName+"')";
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn= DriverManager.getConnection(url,username,password);
        Statement st=conn.createStatement();
        int count=st.executeUpdate(query);
        System.out.println(count);

        st.close();
        conn.close();
*/

        //Prog 5   Imp way to insert data using PreparedStatement

        String url="jdbc:mysql://localhost:3306/sagar";
        String username="root";
        String password="root";
        int id=3;
        String emailAddress="xtz@gmail.com";
        String timestamp="2019-01-10 19:00:06.0";
        String reason="ReasonTWO";
        String actionExecutorId="Admin-9";
        String actionExecutorName="P0900";
        String query="insert into blacklist_email values(?,?,?,?,?,?)";
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn= DriverManager.getConnection(url,username,password);
        PreparedStatement st=conn.prepareStatement(query);
        st.setInt(1,id);
        st.setString(2,emailAddress);
        st.setString(3,timestamp);
        st.setString(4,reason);
        st.setString(5,actionExecutorId);
        st.setString(6,actionExecutorName);
        int count=st.executeUpdate();
        System.out.println(count + " row affected");

        st.close();
        conn.close();


    }
}
