import com.directi.pg.*;
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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;


public class BlockIP extends HttpServlet
{
    private static Logger logger = new Logger(BlockIP.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        logger.debug("success");
        logger.debug("Entering in BlockIP" );
        res.setContentType("text/html");
        ServletContext application = getServletContext();
        PrintWriter out = res.getWriter();
        int count = 0;
        int insertcount = 0;
        int result = 0;
        String incorrectip = "";
        long startipcode, endipcode;
        if (!Admin.isLoggedIn(session))
        {   logger.debug("member is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        String startipaddr=null;
        String endipaddr=null;
        Connection con = null;
        PreparedStatement pstmt=null;
        try
        {
            for (int i = 1; i <= 5; i++)
            {
                startipaddr = Functions.checkStringNull(req.getParameter("startip" + i));
                endipaddr = Functions.checkStringNull(req.getParameter("endip" + i));
                if (startipaddr != null && endipaddr != null)
                {
                    startipaddr = startipaddr.trim();
                    endipaddr = endipaddr.trim();
                    try
                    {
                        int ip = InetAddress.getByName(startipaddr).hashCode();
                        startipcode = ip - 2147483647;
                        startipcode = startipcode + 2147483647L;  //convert into long value.

                        ip = InetAddress.getByName(endipaddr).hashCode();
                        endipcode = ip - 2147483647;
                        endipcode = endipcode + 2147483647L;  //convert into long value.
                    }
                    catch (UnknownHostException e)
                    {
                        incorrectip = incorrectip + startipaddr + "-" + endipaddr + "<br>";
                        continue;
                    }
                    if (startipcode > endipcode)
                    {
                        logger.debug("start ip is higher than end ip");
                        incorrectip = incorrectip + startipaddr + "-" + endipaddr + "<br>";
                        continue;
                    }
                    StringBuffer ipquery = new StringBuffer("insert into blockedip(startipaddress,endipaddress,startipcode,endipcode) values(?,?,?,?)");
                    con=Database.getConnection();
                    pstmt = con.prepareStatement(ipquery.toString());
                    pstmt.setString(1,startipaddr);
                    pstmt.setString(2,endipaddr);
                    pstmt.setFloat(3,startipcode);
                    pstmt.setFloat(4,endipcode);
                    result = pstmt.executeUpdate();
                    if (result > 0)
                        insertcount++;
                }
                if (startipaddr != null ^ endipaddr != null)
                {
                    incorrectip = incorrectip + startipaddr + "-" + endipaddr + "<br>";
                }
            }
            String message = "";
            if (Functions.checkStringNull(incorrectip) != null)
                message = "Below IPs are not blocked due to incorrect IP value.<br>" + incorrectip;

            if (insertcount > 0)
            {
                sSuccessMessage.append("IP addresses  are blocked. <br>  \r\n");
            }
            else
                sErrorMessage.append("No IP address is blocked.<br> \r\n");
        }
        catch (SystemError se)
        {
            logger.error("System Error in BlockIP:::::",se);
            sErrorMessage.append("Internal Error while Block IP<br> \r\n");
        }
        catch (Exception e)
        {   logger.error("Exception in BlockIP::::",e);
            sErrorMessage.append("Internal Error while Block IP<br> \r\n");
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }

        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());

        String redirectpage = "/servlet/BlockedIPList?ctoken="+user.getCSRFToken();
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);
    }
}
