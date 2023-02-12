import com.directi.pg.Database;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class checkMerchantServlet extends HttpServlet
{

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        ServletContext ctx = getServletContext();
        PrintWriter out = response.getWriter();
        String memberId = request.getParameter("merchantid");
        String key = request.getParameter("key");
        String result = "Merchant Id or Key is not specified";
        String color = "red";
        if (memberId != null && key != null)
        {
            result = "Invalid Merchant Key";
            String strSql = "select clkey from members where memberid=?";
            try
            {
                String clkey = "";
                Connection cn = Database.getRDBConnection();
                PreparedStatement p1=cn.prepareStatement(strSql);
                p1.setString(1,memberId);
                ResultSet rs = p1.executeQuery();
                if (rs.next())
                {
                    clkey = rs.getString("clkey");
                    if (clkey.equals(key))
                    {
                        result = "Merchant Key is valid";
                        color = "green";
                    }
                }
                if (rs != null)
                {
                    rs.close();
                }
                if (cn != null)
                {
                    cn.close();
                }
            }
            catch (Exception e)
            {
                out.println("ERROR :" + e);
            }
        }
        out.println("<font color=\"" + color + "\" size=\"2\" face=\"verdana\"><b>" + result + "</b></font>");
    }


}
