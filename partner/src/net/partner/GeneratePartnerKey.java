package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import javax.servlet.RequestDispatcher;
import org.owasp.esapi.User;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

/**
 * Created by Sneha on 1/8/2016.
 */
public class GeneratePartnerKey extends HttpServlet
{
    private static Logger Log = new Logger(GeneratePartnerKey.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Functions functions = new Functions();
        HttpSession session = functions.getNewSession(request);

        PartnerFunctions partnerFunctions=new PartnerFunctions();
        if (!partnerFunctions.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        String pwdData = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int len = pwdData.length();
        Date date = new Date();
        Random rand = new Random(date.getTime());
        StringBuffer key = new StringBuffer();
        int index = -1;
        for (int i = 0; i < 32; i++)
        {
            index = rand.nextInt(len);
            key.append(pwdData.substring(index, index + 1));
        }
        request.setAttribute("key", key.toString());
        String partnerId = (String) session.getAttribute("merchantid");
        StringBuffer errorMsg = new StringBuffer();
        Connection con = null;
        PreparedStatement pstmt = null;

        try
        {
            con = Database.getConnection();
            StringBuilder updateQry = new StringBuilder("UPDATE partners SET clkey=? WHERE partnerId=?");
            pstmt=con.prepareStatement(updateQry.toString());
            pstmt.setString(1,key.toString());
            pstmt.setString(2,partnerId);
            pstmt.executeUpdate();
            Log.debug("Key generated successfully");
        }
        catch (SystemError systemError)
        {
            Log.error("SystemError while generating partner secret key::",systemError);
            errorMsg.append("Internal error while processing your request");
        }
        catch (SQLException e)
        {
            Log.error("SQLException while generating partner secret key::",e);
            errorMsg.append("Internal error while processing your request");
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        request.setAttribute("errorMsg",errorMsg.toString());
        RequestDispatcher rd = request.getRequestDispatcher("/generatedPartnerKey.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(request,response);
    }
}
