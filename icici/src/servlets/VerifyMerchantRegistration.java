package servlets;

import com.directi.pg.*;
import com.invoice.dao.InvoiceEntry;
import com.logicboxes.util.ApplicationProperties;
import com.manager.dao.MerchantDAO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Mail.MailService;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Balaji on 27-Mar-20.
 */
public class VerifyMerchantRegistration extends HttpServlet
{

    private static Logger log = new Logger(VerifyMerchantRegistration.class.getName());
    Functions functions = new Functions();
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req,res);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        String errormsg="";
        String mailresult="fail";
        PrintWriter out = res.getWriter();
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        Functions functions = new Functions();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt=null;
        String partnerId=null;
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        log.error("inside VerifyMerchantRegistration  servlet--------");
        String login = req.getParameter("login")!=null?req.getParameter("login"):"";
        String contact_persons = req.getParameter("contact_persons")!=null?req.getParameter("contact_persons"):"";
        String memberid = req.getParameter("memberid")!=null?req.getParameter("memberid"):"";
        String emailtoken = req.getParameter("emailtoken")!=null?req.getParameter("emailtoken"):"";

        if(functions.isValueNull(memberid))
        {
            try
            {
                conn = Database.getConnection();
                log.error("Get partnerid----");
                String query = "SELECT partnerId FROM members where memberid=?";
                pstmt=conn.prepareStatement(query.toString());
                pstmt.setString(1,memberid);
                rs = pstmt.executeQuery();
                while(rs.next())
                {
                    partnerId= rs.getString("partnerId");
                }
            }
            catch (SQLException se)
            {
                log.error("SQL Exception ",se);
            }
            catch (SystemError systemError)
            {
                log.error("SystemError ",systemError);
            }
            finally
            {
                Database.closeResultSet(rs);
                Database.closePreparedStatement(pstmt);
                Database.closeConnection(conn);
            }
        }
        log.error("login--------"+login);
        log.error("contact_persons--------"+contact_persons);
        log.error("memberid--------"+memberid);
        log.error("emailtoken--------"+emailtoken);
        log.error("partnerid--------"+partnerId);


        try
        {
            //client mail
            String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
            InvoiceEntry invoiceEntry = new InvoiceEntry();
            //merchants.updateFlag(Integer.parseInt(String.valueOf(mem.memberid)));
            MailService mailService = new MailService();
            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
            HashMap merchantSignupMail = new HashMap();
        /*======================*/
            merchantSignupMail.put(MailPlaceHolder.USERNAME, login);
            merchantSignupMail.put(MailPlaceHolder.NAME, contact_persons);
            merchantSignupMail.put(MailPlaceHolder.TOID, memberid);
            merchantSignupMail.put(MailPlaceHolder.CTOKEN, emailtoken);
            merchantSignupMail.put(MailPlaceHolder.PARTNER_URL, liveUrl);
            merchantSignupMail.put(MailPlaceHolder.PARTNERID, partnerId);
            log.error("merchantSignupMail--"+merchantSignupMail.get((MailPlaceHolder.PARTNERID)));
            asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNERS_MERCHANT_REGISTRATION, merchantSignupMail);
            invoiceEntry.insertInvoiceConfigDetails(String.valueOf(memberid));
            mailresult="success";
        }catch(Exception e){
            log.error("Exception while sending mail-----",e);
        }

       /* merchantSignupMail.put(MailPlaceHolder.USERNAME, details.get("login"));
        merchantSignupMail.put(MailPlaceHolder.NAME, details.get("contact_persons"));
        merchantSignupMail.put(MailPlaceHolder.TOID, String.valueOf(mem.memberid));
        merchantSignupMail.put(MailPlaceHolder.CTOKEN, details.get("emailtoken"));
        merchantSignupMail.put(MailPlaceHolder.PARTNER_URL, liveUrl);
        merchantSignupMail.put(MailPlaceHolder.PARTNER_URL, liveUrl);`
        merchantSignupMail.put(MailPlaceHolder.PARTNERID,("1"));
        asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNERS_MERCHANT_REGISTRATION, merchantSignupMail);
        invoiceEntry.insertInvoiceConfigDetails(String.valueOf(mem.memberid));*/
        req.setAttribute("mailresult",mailresult);
        RequestDispatcher rd = req.getRequestDispatcher("MerchantDetails?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
       /*return;*/
    }
}
