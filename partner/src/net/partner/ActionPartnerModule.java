package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.directi.pg.SystemError;
import com.manager.MerchantModuleManager;
import com.manager.PartnerModuleManager;
import org.owasp.esapi.User;
import com.payment.exceptionHandler.PZDBViolationException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created by admin on 4/27/2016.
 */
public class ActionPartnerModule extends HttpServlet
{

    static Logger logger = new Logger(ActionPartnerModule.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();

        PartnerFunctions partner=new PartnerFunctions();
        PartnerModuleManager partnerModuleManager = new PartnerModuleManager();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }


        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        String action=request.getParameter("action");
        String mappingId=request.getParameter("mappingid");

        String userId = request.getParameter("userid");
        String login = request.getParameter("login");
        String statusMsg="";
        String successMsg="";


        if("remove".equalsIgnoreCase(action))
        {
            try
            {
                boolean b=partnerModuleManager.removePartnerModuleMapping(mappingId);
                request.setAttribute("login",login);
                request.setAttribute("userid",userId);
                RequestDispatcher rd = request.getRequestDispatcher("/partnerModuleMappingList.jsp?ctoken="+user.getCSRFToken());


                if(b)
                {
                    successMsg="Module mapping removed successfully";
                    Connection conn = null;
                    Hashtable hash = null;
                    ResultSet rs = null;
                    try
                    {
                        conn = Database.getConnection();
                        StringBuffer query = new StringBuffer("select amm.mappingid,amm.moduleid,ams.modulename FROM partner_users_modules_mapping as amm join partner_modules_master as ams on amm.moduleid=ams.moduleid and amm.moduleid>0 and userid="+userId);
                        StringBuffer countquery = new StringBuffer("select count(*)FROM partner_users_modules_mapping as amm join partner_modules_master as ams on amm.moduleid=ams.moduleid and amm.moduleid>0 and userid="+userId);

                        hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
                        rs = Database.executeQuery(countquery.toString(), conn);
                        rs.next();
                        int totalrecords = rs.getInt(1);

                        hash.put("totalrecords", "" + totalrecords);
                        hash.put("records", "0");

                        if (totalrecords > 0)
                            hash.put("records", "" + (hash.size() - 2));
                        request.setAttribute("transdetails", hash);
                        /*rd.forward(request, response);*/
                    }
                    catch (SystemError s)
                    {
                        logger.error("SystemError:::::::", s);
                        statusMsg="Internal error while processing your request";

                        /*request.setAttribute("errormessage","Internal error while processing your request");*/
                        /*rd.forward(request,response);
                        return;*/
                    }
                    catch (SQLException e)
                    {
                        logger.error("SQLException:::::::", e);
                        statusMsg="Internal error while processing your request";
                       /* request.setAttribute("errormessage","Internal error while processing your request");*/
                        /*rd.forward(request,response);
                        return;
*/                    }
                    finally
                    {
                        Database.closeResultSet(rs);
                        Database.closeConnection(conn);
                    }
                }

                else
                {
                    statusMsg="Module mapping removing failed";
                }

                request.setAttribute("errormessage",statusMsg);
                request.setAttribute("success",successMsg);

                rd.forward(request, response);
                return;
            }
            catch (PZDBViolationException e)
            {
                request.setAttribute("login",login);
                request.setAttribute("userid",userId);
                RequestDispatcher rd = request.getRequestDispatcher("/partnerModuleMappingList.jsp?ctoken="+user.getCSRFToken());

                request.setAttribute("errormessage","Internal error while processing your request");
                rd.forward(request,response);
                return;
            }
        }
        else
        {
            request.setAttribute("login",login);
            request.setAttribute("userid",userId);
            RequestDispatcher rd = request.getRequestDispatcher("/partnerModuleMappingList.jsp?ctoken="+user.getCSRFToken());

            request.setAttribute("errormessage","Invalid Action");
            rd.forward(request,response);
            return;
        }
    }
}
