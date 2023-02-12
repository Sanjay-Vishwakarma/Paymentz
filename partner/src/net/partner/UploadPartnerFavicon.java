package net.partner;


import com.directi.pg.*;
import com.logicboxes.util.ApplicationProperties;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by Namrata on 20/05/2020. */
public class UploadPartnerFavicon extends HttpServlet
{
    private Logger logger = new Logger(UploadPartnerFavicon.class.getName());
    private Functions functions = new Functions();
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner = new PartnerFunctions();
        logger.debug("Enter UploadPartnerFavicon");


        String errorMsg = "";
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String data = request.getParameter("data");

        String redirectpage = null;
        String errormsg = "";
        String msg = "F";
        String EOL = "<BR>";

        try
        {
            String partnerIconPath = null;
            String merchantIconPath = null;
            String agentLogoPath = null;
            String orderLogoPath = null;
            String pathtolog =null;

            merchantIconPath = ApplicationProperties.getProperty("MERCHANT_ICON_PATH");
            partnerIconPath = ApplicationProperties.getProperty("PARTNER_ICON_PATH");
            agentLogoPath = ApplicationProperties.getProperty("AGENT_LOGO_PATH");
            orderLogoPath = ApplicationProperties.getProperty("ORDER_LOGO_PATH");
            pathtolog = ApplicationProperties.getProperty("LOG_STORE");

            FileUploadBean fub = new FileUploadBean();

            fub.setSavePath(partnerIconPath);
            fub.setPath1(merchantIconPath);
            fub.setPath2(agentLogoPath);
            fub.setPath3(orderLogoPath);
            fub.setLogpath(pathtolog);

            fub.uploadPartnerFavicon(request);

            String uploadedFileName = fub.getFilename();
            String partnerid = fub.getFieldValue("partid").trim();
            logger.debug("partnerid::::"+partnerid);
            logger.debug("uploadedFileName::::"+uploadedFileName);

            if (!ESAPI.validator().isValidInput("partid",partnerid,"Numbers",3,false))
            {
                throw new SystemError("Invalid or Empty Partner ID.");
            }
            else
            {
                boolean b=updatePartnerFavicon(partnerid, uploadedFileName);
                if(b)
                {
                    Merchants.refresh();
                }
                redirectpage = "/partnerFavicon.jsp?MES=T&ctoken="+user.getCSRFToken();
                request.setAttribute("partid",partnerid);
            }
            RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
            rd.forward(request, response);

        }
        catch(SystemError sys)
        {
            String ch="Please provide valid file";
            if(ch.equals(sys.getMessage())){
                errormsg = sys.getMessage();
                redirectpage = "/partnerFavicon.jsp?MES=S&ctoken=" + user.getCSRFToken();
            }
            else if (sys.getMessage().contains("File size should not exceed 500KB"))
            {
                redirectpage = "/partnerFavicon.jsp?MES=FS&ctoken=" + user.getCSRFToken();
            }
            else
            {
                errormsg = errormsg + sys.getMessage() + EOL;
                redirectpage = "/partnerFavicon.jsp?MES=" + msg + "&ctoken=" + user.getCSRFToken();
            }
            request.setAttribute("error", errormsg);
            RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
            rd.forward(request, response);
        }
    }


    public boolean updatePartnerFavicon(String partnerid, String faviconname) throws SystemError
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        boolean status=false;
        try
        {
            conn = Database.getConnection();
            String selquery = "update partners set faviconName = ? where partnerId = ? ";
            pstmt= conn.prepareStatement(selquery);
            pstmt.setString(1,faviconname);
            pstmt.setString(2,partnerid);
            int i = pstmt.executeUpdate();
            if(i>0)
            {
                status=true;
            }
        }
        catch (SystemError se)
        {
            logger.error(" SystemError in isMember method: ",se);
            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Exception in isMember method: ", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }
}
