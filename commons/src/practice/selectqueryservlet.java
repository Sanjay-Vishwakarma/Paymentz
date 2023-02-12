package practice;

import com.directi.pg.Database;
import com.directi.pg.SystemError;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * Created by Admin on 7/9/2020.
 */
public class selectqueryservlet
{
    public static void main(String[] args)
    {


       // PrintWriter printWriter=response.getWriter();

/*     response.setContentType("text/html");
        String name = request.getParameter("Name");
        String age = request.getParameter("Age");
        String mobile = request.getParameter("Mobile");
        String email = request.getParameter("Email");
        String address  = request.getParameter("Address");
        String education = request.getParameter("Education");
        String designation = request.getParameter("Designation");

        int var1=request.getServerPort();
        System.out.println(var1);*/


        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/paymentz", "root", "root");
            Statement stmt = conn.createStatement();

            StringBuilder selectquery = new StringBuilder("select*from blacklist_cards ");
            selectquery.append("where first_six=444444");
            ResultSet rs = stmt.executeQuery(String.valueOf(selectquery));
            while (rs.next())
            {
                System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5) + " " + rs.getString(6));
            }
        }
            catch (Exception e)
            {
                System.out.println(e);;
            }
        }
    }

       /*        selectquery.append("select * from blacklist_cards where id > 0");
            selectquery.append(" and first_six=");
            selectquery.append(" and last_four=");
            selectquery.append(" remark=stolen card");
            selectquery.append("order by LIMIT");
          PreparedStatement ps =conn.prepareStatement(selectquery.toString());
            rs=ps.executeQuery();
            System.out.println("query->"+rs);
        ps.setString(1,name);
            ps.setString(2,age);
            ps.setString(3,mobile);
            ps.setString(4,email);
            ps.setString(5,address);
            ps.setString(6,education);
            ps.setString(7,designation);
     int i =ps.executeUpdate();
            if(i>0)
            {
                System.out.println(ps);
            }
*/

