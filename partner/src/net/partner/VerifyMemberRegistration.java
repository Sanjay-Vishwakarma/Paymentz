package net.partner;

import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.invoice.dao.InvoiceEntry;
import com.logicboxes.util.ApplicationProperties;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Created by Balaji on 01-Apr-20.
 */
public class VerifyMemberRegistration extends HttpServlet
{

    private static Logger log = new Logger(VerifyMemberRegistration.class.getName());
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

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        log.error("inside VerifyMerchantRegistration  servlet--------");
        String login = req.getParameter("login")!=null?req.getParameter("login"):"";
        String contact_persons = req.getParameter("contact_persons")!=null?req.getParameter("contact_persons"):"";
        String memberid = req.getParameter("memberid")!=null?req.getParameter("memberid"):"";
        String emailtoken = req.getParameter("emailtoken")!=null?req.getParameter("emailtoken"):"";
        String partnerId = req.getParameter("partnerId")!=null?req.getParameter("partnerId"):"";
        String pidName = req.getParameter("pidName")!=null?req.getParameter("pidName"):"";

        log.error("login--------"+login);
        log.error("contact_persons--------"+contact_persons);
        log.error("memberid--------"+memberid);
        log.error("emailtoken--------"+emailtoken);
        log.error("contact_persons--------"+contact_persons);
        log.error("pidName--------"+pidName);
        log.error("partnerId--------"+partnerId);


        try
        {
            //client mail
            String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            HashMap merchantSignupMail=new HashMap();
            merchantSignupMail.put(MailPlaceHolder.USERNAME,login);
            merchantSignupMail.put(MailPlaceHolder.NAME,contact_persons);
            merchantSignupMail.put(MailPlaceHolder.TOID,memberid);
            merchantSignupMail.put(MailPlaceHolder.PARTNERID,partnerId);
            merchantSignupMail.put(MailPlaceHolder.CTOKEN,emailtoken);
            merchantSignupMail.put(MailPlaceHolder.TOID,memberid);
            merchantSignupMail.put(MailPlaceHolder.FROM_TYPE,pidName);
            merchantSignupMail.put(MailPlaceHolder.PARTNER_URL,liveUrl);
            asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNERS_MERCHANT_REGISTRATION, merchantSignupMail);
            InvoiceEntry invoiceEntry = new InvoiceEntry();
            invoiceEntry.insertInvoiceConfigDetails(memberid);
            mailresult="success";
        }catch(Exception e){
            log.error("Exception while sending mail-----",e);
        }

       /* merchantSignupMail.put(MailPlaceHolder.USERNAME, details.get("login"));
        merchantSignupMail.put(MailPlaceHolder.NAME, details.get("contact_persons"));
        merchantSignupMail.put(MailPlaceHolder.TOID, String.valueOf(mem.memberid));
        merchantSignupMail.put(MailPlaceHolder.CTOKEN, details.get("emailtoken"));
        merchantSignupMail.put(MailPlaceHolder.PARTNER_URL, liveUrl);
        merchantSignupMail.put(MailPlaceHolder.PARTNER_URL, liveUrl);
        merchantSignupMail.put(MailPlaceHolder.PARTNERID,("1"));
        asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNERS_MERCHANT_REGISTRATION, merchantSignupMail);
        invoiceEntry.insertInvoiceConfigDetails(String.valueOf(mem.memberid));*/
        req.setAttribute("mailresult",mailresult);
        RequestDispatcher rd = req.getRequestDispatcher("MemberDetailList?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
       /*return;*/
    }
}
