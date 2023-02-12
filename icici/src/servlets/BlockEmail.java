import com.directi.pg.*;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BlockEmail extends HttpServlet
{
   private static Logger logger = new Logger(BlockEmail.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Entering in BlockEmail");
        res.setContentType("text/html");
        ServletContext application = getServletContext();
        PrintWriter out = res.getWriter();
        int count = 0;
        int result = 0;
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("member is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        String incorrectemail = "";
        String repeatemail = "";
        Connection con = null;
        PreparedStatement pstmt=null;
        PreparedStatement ps=null;
        ResultSet rs=null;

        try
        {
            for (int i = 1; i <= 10; i++)
            {
                String emailaddr = Functions.checkStringNull(req.getParameter("email" + i));
                if (emailaddr != null)
                {
                    if (!ESAPI.validator().isValidInput("email",req.getParameter("email" + i),"Email",50,false))
                    {
                        incorrectemail = incorrectemail + emailaddr + "<br>";
                        continue;
                    }
                    con = Database.getConnection();
                    String query = "select * from blockedemail where emailaddr=?";
                    pstmt=con.prepareStatement(query);
                    pstmt.setString(1,emailaddr);
                    rs = pstmt.executeQuery();
                    if (rs.next())
                    {
                        repeatemail = repeatemail + emailaddr + "<br>";
                        continue;
                    }
                    StringBuffer emailquery = new StringBuffer("insert into blockedemail(emailaddr,type) values(?,'email')");

                    ps=con.prepareStatement(emailquery.toString());
                    ps.setString(1,emailaddr);
                    result = ps.executeUpdate();
                    if (result > 0)
                    {
                        count++;
                    }
                }
            }
            String message = "";
            if (Functions.checkStringNull(incorrectemail) != null)
                message = "Below emails are not blocked due to incorrect email address.<br>" + incorrectemail;

            if (Functions.checkStringNull(repeatemail) != null)
                message = message + "Below emails are already blocked.<br>" + repeatemail;

            if (count > 0)
            {
            sSuccessMessage.append("Email addresses are blocked. <br>  \r\n"+ message);
            }
            else
            {
            sErrorMessage.append("No email address is blocked.<br> \r\n"+ message);
            }
        }
        catch (SystemError se)
        {   logger.error("System Error in BlockEmail:::",se);
            sErrorMessage.append("Internal Error While Block Email");
        }
        catch (Exception e)
        {   logger.error("Exception in BlockEmail::::",e);
            sErrorMessage.append("Internal Error While Block Email");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }

        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());

        String redirectpage = "/servlet/BlockedEmailList?ctoken="+user.getCSRFToken();
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);
    }
}
