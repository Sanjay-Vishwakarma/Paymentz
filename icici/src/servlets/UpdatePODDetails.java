import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;

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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Hashtable;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 3/5/14
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class UpdatePODDetails extends HttpServlet
{
    private static Logger log = new Logger(UpdatePODDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        log.debug("success");


        if (!Admin.isLoggedIn(session))
        {   log.debug("invalid user");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        Connection conn = null;

        try
        {
            conn = Database.getConnection();
        }
        catch (SystemError systemError)
        {
            log.error("Error while reversal :",systemError);
            sErrorMessage.append("Error while connecting to Database.");
            req.setAttribute("cbmessage", sErrorMessage.toString());
            log.debug("forwarding to EditShippingDetailList");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/EditShippingDetailList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        //variable list
        String[] icicitransidStr =null;
        String trackingid=null;
        String podbatch=null;
        String pod=null;
        String accountid=null;
        String gateway=null;
        Collection<Hashtable> listOfRefunds = null;
        String tablename="";
        String query="";

        if (req.getParameterValues("trackingId")!= null)
        {
            icicitransidStr = req.getParameterValues("trackingId");
        }
        else
        {
            sErrorMessage.append("Invalid TransactionID.<BR>");
            req.setAttribute("cbmessage", sErrorMessage.toString());
            log.debug("forwarding to EditShippingDetailList");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/EditShippingDetailList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        if (Functions.checkArrayNull(icicitransidStr) == null)
        {
            sErrorMessage.append("Select at least one transaction.<BR>");
            req.setAttribute("cbmessage", sErrorMessage.toString());
            log.debug("forwarding to EditShippingDetailList");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/EditShippingDetailList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        Functions functions=new Functions();
        for (String icicitransid : icicitransidStr)
        {

            if (!ESAPI.validator().isValidInput("trackingid",icicitransid,"Numbers",10,false))
            {
                sErrorMessage.append("Invalid TrackingId");
                req.setAttribute("cbmessage", sErrorMessage.toString());
                log.debug("forwarding to member preference");
                RequestDispatcher rd = req.getRequestDispatcher("/servlet/EditShippingDetailList?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            else
            {
                trackingid = icicitransid;
            }
            if (!ESAPI.validator().isValidInput("accountid",req.getParameter("accountid_"+icicitransid),"SafeString",10,false))
            {
                sErrorMessage.append("Invalid AccountId");
                req.setAttribute("cbmessage", sErrorMessage.toString());
                log.debug("forwarding to member preference");
                RequestDispatcher rd = req.getRequestDispatcher("/servlet/EditShippingDetailList?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            else
            {
                accountid = req.getParameter("accountid_"+icicitransid);
            }
            if((req.getParameter("pod_"+icicitransid)==null || req.getParameter("pod_"+icicitransid).equals("")) && (req.getParameter("podbatch_"+icicitransid)==null || req.getParameter("podbatch_"+icicitransid).equals("")))
            {
                sErrorMessage.append("POD Or Site Address can not be blank");
                req.setAttribute("cbmessage", sErrorMessage.toString());
                log.debug("forwarding to member preference");
                RequestDispatcher rd = req.getRequestDispatcher("/servlet/EditShippingDetailList?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }


            if(!ESAPI.validator().isValidInput("pod",req.getParameter("pod_"+icicitransid),"alphanum",40,false) || req.getParameter("pod_"+icicitransid).equalsIgnoreCase("null"))
            {
                sErrorMessage.append("Invalid POD");
                req.setAttribute("cbmessage", sErrorMessage.toString());
                log.debug("forwarding to member preference");
                RequestDispatcher rd = req.getRequestDispatcher("/servlet/EditShippingDetailList?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            else
            {
                pod = req.getParameter("pod_"+icicitransid);
            }
            String regex="^[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

            if(!req.getParameter("podbatch_"+icicitransid).trim().matches(regex) || !ESAPI.validator().isValidInput("podbatch",req.getParameter("podbatch_"+icicitransid),"URL",50,false) || req.getParameter("podbatch_"+icicitransid).equalsIgnoreCase("null"))
            {
                sErrorMessage.append("Invalid POD Batch/Site Name");
                req.setAttribute("cbmessage", sErrorMessage.toString());
                log.debug("forwarding to member preference");
                RequestDispatcher rd = req.getRequestDispatcher("/servlet/EditShippingDetailList?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            else
            {
                podbatch = req.getParameter("podbatch_"+icicitransid);
            }
            if(accountid != null || accountid.equals(""))
            {
                gateway = GatewayAccountService.getGatewayAccount(accountid).getGateway();

                tablename = Database.getTableName(gateway);
                PreparedStatement pstmt = null;
                try
                {
                    conn = Database.getConnection();
                    if(tablename.equals("transaction_qwipi"))
                    {
                        query ="UPDATE transaction_qwipi SET pod=?, podbatch=? WHERE trackingid=?";
                    }
                    else if(tablename.equals("transaction_ecore"))
                    {
                        query ="UPDATE transaction_ecore SET pod=?, podbatch=? WHERE trackingid=?";
                    }
                    else
                    {
                        query ="UPDATE transaction_common SET pod=?, podbatch=? WHERE trackingid=?";
                    }
                    log.error("update query---"+query);
                    pstmt=conn.prepareStatement(query.toString());
                    pstmt.setString(1,pod);
                    pstmt.setString(2,podbatch);
                    pstmt.setString(3,trackingid);
                    int i= pstmt.executeUpdate();
                    if(i==1)
                    {
                        sSuccessMessage.append("Update Shipping Details for trackingid : "+icicitransid+"<BR>");
                        /*req.setAttribute("heading","Success Result");
                        req.setAttribute("Message",sSuccessMessage);
*/
                    }
                    else
                    {
                        sErrorMessage.append("Fail Shipping Details for Trackingid:- "+icicitransid+"<BR>");
                        /*req.setAttribute("heading","Success Result");
                        req.setAttribute("Message",sErrorMessage);*/
                    }
                    //
                    //log.debug(query);
                }
                catch (SQLException se)
                {
                   log.error("SQL Exception",se);
                }
                catch (SystemError systemError)
                {
                    log.error("System Error",systemError);
                }
                finally
                {
                    Database.closePreparedStatement(pstmt);
                    Database.closeConnection(conn);
                }
            }
            else
            {
                sErrorMessage.append("Invalid accountid for Trackingid:- "+icicitransid+"<BR>");
            }

        }
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append("<table align=\"center\" border=\"1\" bgcolor=\"CCE0FF\" cellpadding=\"2\" cellspacing=\"2\"><tr><td valign=\"middle\" bgcolor=\"#2379A5\"><font class=\"text\" color=\"#FFFFFF\" size=\"1\" face=\"Verdana, Arial\"> Success Result</font></td><td valign=\"middle\" bgcolor=\"#2379A5\"><font class=\"text\" color=\"#FFFFFF\" size=\"1\" face=\"Verdana, Arial\"> Fail Result</font></td></tr>");
        chargeBackMessage.append("<tr><td>");
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("</td><td>");
        chargeBackMessage.append(sErrorMessage.toString());
        chargeBackMessage.append("</td></tr></table>");
        req.setAttribute("sSuccessMessage", sSuccessMessage.toString());
        req.setAttribute("sErrorMessage",sErrorMessage.toString());
        log.debug("forwarding to member preference");
        RequestDispatcher rd = req.getRequestDispatcher("/servlet/EditShippingDetailList?ctoken="+user.getCSRFToken());
        rd.forward(req, res);

    }
}
