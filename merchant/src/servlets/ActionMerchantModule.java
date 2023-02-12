import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.directi.pg.SystemError;
import com.manager.MerchantModuleManager;
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
public class ActionMerchantModule extends HttpServlet
{

    static Logger logger = new Logger(ActionMerchantModule.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Merchants merchants = new Merchants();
        HttpSession session = request.getSession();

        if (!merchants.isLoggedIn(session))
        {   logger.debug("member is logout ");
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }


        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        String action=request.getParameter("action");
        String mappingId=request.getParameter("mappingid");

        String userId = request.getParameter("userid");
        String login = request.getParameter("login");
        String statusMsg="";


        if("remove".equalsIgnoreCase(action))
        {
            try
            {
                MerchantModuleManager merchantModuleManager=new MerchantModuleManager();
                boolean b=merchantModuleManager.removeMerchantModuleMapping(mappingId);
                request.setAttribute("login",login);
                request.setAttribute("userid",userId);
                RequestDispatcher rd = request.getRequestDispatcher("/merchantModuleMappingList.jsp?ctoken="+user.getCSRFToken());


                if(b)
                {
                    statusMsg="Module mapping removed successfully";
                    Connection conn = null;
                    ResultSet rs = null;
                    Hashtable hash = null;
                    try
                    {
                        conn = Database.getConnection();
                        StringBuffer query = new StringBuffer("select amm.mappingid,amm.moduleid,ams.modulename FROM merchant_users_modules_mapping as amm join merchant_modules_master as ams on amm.moduleid=ams.moduleid and amm.moduleid>0 and userid="+userId);
                        StringBuffer countquery = new StringBuffer("select count(*)FROM merchant_users_modules_mapping as amm join merchant_modules_master as ams on amm.moduleid=ams.moduleid and amm.moduleid>0 and userid="+userId);

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

                //System.out.println("error-----"+statusMsg);
                rd.forward(request, response);
                return;
            }
            catch (PZDBViolationException e)
            {
                request.setAttribute("login",login);
                request.setAttribute("userid",userId);
                RequestDispatcher rd = request.getRequestDispatcher("/merchantModuleMappingList.jsp?ctoken="+user.getCSRFToken());

                request.setAttribute("errormessage","Internal error while processing your request");
                rd.forward(request,response);
                return;
            }
        }
        else
        {
            request.setAttribute("login",login);
            request.setAttribute("userid",userId);
            RequestDispatcher rd = request.getRequestDispatcher("/merchantModuleMappingList.jsp?ctoken="+user.getCSRFToken());

            request.setAttribute("errormessage","Invalid Action");
            rd.forward(request,response);
            return;
        }
    }
}
