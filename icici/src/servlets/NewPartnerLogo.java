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
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 11/26/13
 * Time: 2:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewPartnerLogo extends HttpServlet
{
    private static Logger Log = new Logger(NewPartnerLogo.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        Log.debug("Enter in New partner ");
        if (!Admin.isLoggedIn(session))
        {
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String data = request.getParameter("data");

        String redirectpage = null;
        String errormsg = "";
        String msg = "F";
        String EOL = "<BR>";

        try
        {
            String partnerLogoPath = null;
            String merchantLogoPath = null;
            String agentLogoPath = null;
            String orderLogoPath = null;
            String pathtolog =null;

            merchantLogoPath = ApplicationProperties.getProperty("MERCHANT_LOGO_PATH");
            partnerLogoPath = ApplicationProperties.getProperty("PARTNER_LOGO_PATH");
            agentLogoPath = ApplicationProperties.getProperty("AGENT_LOGO_PATH");
            orderLogoPath = ApplicationProperties.getProperty("ORDER_LOGO_PATH");
            pathtolog = ApplicationProperties.getProperty("LOG_STORE");

            FileUploadBean fub = new FileUploadBean();
            fub.setSavePath(partnerLogoPath);
            fub.setPath1(merchantLogoPath);
            fub.setPath2(agentLogoPath);
            fub.setPath3(orderLogoPath);
            fub.setLogpath(pathtolog);
            fub.uploadPartnerLogo(request);

            String uploadedFileName = fub.getFilename();
            String partnerid = fub.getFieldValue("partid").trim();
            String logowidth= fub.getFieldValue("logowi").trim();
            String logoheight= fub.getFieldValue("logohe").trim();
            if (!ESAPI.validator().isValidInput("partid",partnerid,"Numbers",3,false))
            {
                throw new SystemError("Invalid or Empty Partner ID.");
            }
            else
            {
                boolean b=updatePartnerLogo(partnerid,uploadedFileName,logoheight,logowidth);
                if(b)
                {
                    Merchants.refresh();
                }
                redirectpage = "/partnerLogo.jsp?MES=T&ctoken="+user.getCSRFToken();
                request.setAttribute("partid",partnerid);
                request.setAttribute("logowidth",logowidth);
                request.setAttribute("logoheight",logoheight);
            }
            RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
            rd.forward(request, response);

        }
        catch(SystemError sys)
        {
            errormsg = errormsg + sys.getMessage()+EOL;
            Log.debug(errormsg);
            redirectpage = "/partnerLogo.jsp?MES=" +msg+"&ctoken="+user.getCSRFToken();
            request.setAttribute("error",errormsg);
            RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
            rd.forward(request, response);
        }
    }

    public boolean updatePartnerLogo(String partnerid, String logoname, String logoheight, String logowidth) throws SystemError
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        boolean status=false;
        try
        {
            conn = Database.getConnection();
            String selquery = "update partners set logoName =?,logoheight =?, logowidth =? where partnerId = ? ";
            pstmt= conn.prepareStatement(selquery);
            pstmt.setString(1,logoname);
            pstmt.setString(2,logoheight);
            pstmt.setString(3,logowidth);
            pstmt.setString(4,partnerid);
            int i = pstmt.executeUpdate();
            Log.error("query for log height update:::"+pstmt);
            if(i>0)
            {
                status=true;
            }
        }
        catch (SystemError se)
        {
            Log.error(" SystemError in isMember method: ",se);
            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            Log.error("Exception in isMember method: ", e);
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
