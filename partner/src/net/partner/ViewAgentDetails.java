package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created by Kanchan on 28-01-2021.
 * Time : 5:26 pm
 */
public class ViewAgentDetails extends HttpServlet
{
    private static Logger log = new Logger(ViewAgentDetails.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        PartnerFunctions partner = new PartnerFunctions();
        log.debug("entering in view agent details....");
        HttpSession session= request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        log.debug("ctoken==="+request.getParameter("ctoken"));
        if (!partner.isLoggedInPartner(session))
        {
            log.debug("Partner is logout");
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        String agentid="";
        String action = "";
        String isReadOnly= "View";
        try
        {
            agentid= ESAPI.validator().getValidInput("agentid",request.getParameter("agentid"),"Numbers",10,true);
            action= ESAPI.validator().getValidInput("action",request.getParameter("action"),"SafeString",30,true);
        }
        catch (ValidationException e)
        {
           log.error("Validation Exception while getting Agent Details...",e);
        }

        Connection conn= null;
        String str= "select * from partners";
        String count= "select count(*) from partners";
        PreparedStatement ps= null;
        ResultSet rs= null;
        ResultSet rs1= null;

        try
        {
            Hashtable hash= null;
            conn= Database.getConnection();
            String query= "Select login,agentName,siteurl,contact_persons,contact_emails,supporturl,notifyemail,maincontact_ccmailid,maincontact_phone,cbcontact_name,cbcontact_mailid,cbcontact_ccmailid,cbcontact_phone,refundcontact_name,refundcontact_mailid,refundcontact_ccmailid,refundcontact_phone,salescontact_name,salesemail as salescontact_mailid,salescontact_ccmailid,salescontact_phone,fraudcontact_name,fraudcontact_mailid,fraudcontact_ccmailid,fraudcontact_phone,technicalcontact_name,technicalcontact_mailid,technicalcontact_ccmailid,technicalcontact_phone,billingcontact_ccmailid,billingcontact_phone,billingcontact_name,billingemail as billingcontact_mailid,country,telno,partnerId,isIpWhitelisted,actionExecutorId,actionExecutorName,emailTemplateLang FROM agents WHERE agentId=?";
            ps= conn.prepareStatement(query);
            ps.setString(1, agentid);
            rs= ps.executeQuery();
            hash= Database.getHashFromResultSet(rs);

            Hashtable hash1=  Database.getHashFromResultSet(Database.executeQuery(str, conn));

            rs1= Database.executeQuery(count, conn);
            rs1.next();
            int totalrecords1= rs1.getInt(1);

            hash1.put("totalrecords1", "" + totalrecords1);
            hash1.put("records1", "0");

            if (totalrecords1 > 0)

                hash1.put("records1", "" + (hash1.size() - 2));
                request.setAttribute("partners", hash1);

            log.debug("===rec===" + hash);
            if (action.equalsIgnoreCase("modify"))
            {
                isReadOnly="modify";
            }

            request.setAttribute("isreadonly", isReadOnly);
            request.setAttribute("agentdetails",hash);
            request.setAttribute("agentid",agentid);
            request.setAttribute("action",action);
            RequestDispatcher rd= request.getRequestDispatcher("/viewAgentDetails.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request,response);
        }
        catch (SystemError systemError)
        {
            log.error("Sql Exception :::::",systemError);
            log.error("Exception while updating: " + systemError.getMessage());
        }
        catch (SQLException s)
        {
            log.error("Sql Exception :::::",s);
            log.error("Exception while updating: " + s.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs1);
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }

    }
}
