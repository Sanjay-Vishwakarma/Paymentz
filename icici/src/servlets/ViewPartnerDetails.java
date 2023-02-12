import com.directi.pg.Admin;
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
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 2/12/14
 * Time: 11:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class ViewPartnerDetails extends HttpServlet
{
    private static Logger log = new Logger(ViewPartnerDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String partnerid="";
        String action = "";
        String isReadOnly = "view";

        StringBuffer message = new StringBuffer();
        Hashtable hash = null;
        RequestDispatcher rd = req.getRequestDispatcher("/viewPartnerDetails.jsp?ctoken="+user.getCSRFToken());
        try
        {
            partnerid = ESAPI.validator().getValidInput("partnerid",req.getParameter("partnerid"),"Numbers",10,true);
            action = ESAPI.validator().getValidInput("action",req.getParameter("action"),"SafeString",30,true);
        }
        catch (ValidationException e)
        {
            log.error("Validation Exception while getting Partner Details",e);
            message.append("Internal error while processing your request");
        }
        try
        {
            hash = (Hashtable) req.getAttribute("partnerdetails1");
            if (hash == null)
            {
                Connection conn = null;
                PreparedStatement ps = null;
                ResultSet rs = null;
                try
                {
                    conn = Database.getRDBConnection();
                    String query = "SELECT partnerName,login,logoName,siteurl,contact_persons,contact_emails,companysupportmailid,companyadminid,supporturl,documentationurl,hosturl,salesemail,billingemail,notifyemail,companyfromemail,supportfromemail,smtp_host,smtp_port,smtp_user,smtp_password,sms_user,sms_password,from_sms,country,telno,isIpWhitelisted,fraudemailid,responseType,responseLength,isFlightPartner,isRefund,isTokenizationAllowed,isMerchantRequiredForCardRegistration,isAddressRequiredForTokenTransaction,isMerchantRequiredForCardholderRegistration,tokenValidDays,isCardEncryptionEnable,salescontactname,fraudcontactname,technicalemailid,technicalcontactname,chargebackemailid,chargebackcontactname,refundemailid,refundcontactname,billingcontactname,notifycontactname,is_rest_whitelisted,splitpayment,splitpaymenttype,addressvalidation,addressdetaildisplay,flightMode,template,address,reporting_currency,autoRedirect,ip_whitelist_invoice,address_validation_invoice,default_theme,domain,relayWithAuth,ispcilogo,binService,oldcheckout,emailSent,monthly_min_commission_module,support_email_for_transaction_mail,partner_short_name,profit_share_commission_module,termsUrl,privacyUrl,cookiesUrl,checkoutInvoice,bankCardLimit,contact_ccmailid,sales_ccemailid,billing_ccemailid,notify_ccemailid,fraud_ccemailid,chargeback_ccemailid,refund_ccemailid,technical_ccemailid, partnerOrgnizationForWL_Invoice,emi_configuration,exportTransactionCron,isMerchantKey,actionExecutorId,actionExecutorName,protocol,processor_partnerid,emailTemplateLang,sms_service,signupPrevent,logoheight,logowidth FROM partners WHERE partnerId=?";
                    ps = conn.prepareStatement(query);
                    ps.setString(1, partnerid);
                    rs = ps.executeQuery();
                    hash = Database.getHashFromResultSet(rs);
                }
                catch (SystemError systemError)
                {
                    log.error("Sql Exception while updating template fees:::::", systemError);
                    message.append("Internal error while processing your request");
                }
                catch (SQLException e)
                {
                    log.error("Sql Exception while updating template fees:::::", e);
                    message.append("Internal error while processing your request");
                }
                finally
                {
                    Database.closeResultSet(rs);
                    Database.closePreparedStatement(ps);
                    Database.closeConnection(conn);
                }
            }

            Connection con = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            String roles=null;
            Hashtable innerhash = (Hashtable) hash.get(1 + "");

            try
            {
                con = Database.getRDBConnection();
                String qry = "select roles from `user` where login=? and roles IN ('partner','superpartner')";
                //System.out.println("roles1");
                pstmt = con.prepareStatement(qry);
                pstmt.setString(1, String.valueOf(innerhash.get("login")));
                //System.out.println("roles2"+ String.valueOf(innerhash.get("login")));
                log.debug("user query::::" + pstmt);
                rs = pstmt.executeQuery();
                while (rs.next())
                {
                    roles = rs.getString("roles");
                    //System.out.println("roles3"+ rs.getString("roles"));
                }
            }
            catch (Exception e)
            {
                log.error("error", e);
            }
            finally
            {
                Database.closeResultSet(rs);
                Database.closePreparedStatement(pstmt);
                Database.closeConnection(con);
            }

            if (action.equalsIgnoreCase("modify"))
            {
                isReadOnly = "modify";
            }
            req.setAttribute("isreadonly", isReadOnly);
            req.setAttribute("partnerdetails", hash);
            req.setAttribute("partnerid", partnerid);
            req.setAttribute("action", action);
            req.setAttribute("roles", roles);
            rd.forward(req, res);
        }
        catch(Exception e)
        {
            log.debug("Exception:::"+e);
        }
    }
}