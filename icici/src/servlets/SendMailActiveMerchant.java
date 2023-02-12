//package servlets;

import com.directi.pg.*;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Mail.MailService;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.ValidationException;
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
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: amit.j
 * Date: Jun 8, 2006
 * Time: 5:55:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class SendMailActiveMerchant extends HttpServlet
{  private static Logger Log = new Logger(SendMailActiveMerchant.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        Log.debug("enter in send mail");
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   Log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String redirectpage = "/SendMail.jsp?ctoken="+user.getCSRFToken();
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        Log.debug("Entering in send mail to merchant");
        Connection con = null;
        String message=null;
        String mailSubject = null;
        try
        {
            validateOptionalParameter(request);
        }
        catch(ValidationException e)
        {
            Log.error("Enter valid type",e);
        }

        final ResourceBundle RBTemplate = LoadProperties.getProperty("com.directi.pg.template");
        String attachment = request.getParameter("attachment");
        String templateImagePath = RBTemplate.getString("IMGPATHFORMAIL");
        String toid=request.getParameter("toid");
        String accountId= request.getParameter("accountid");
        mailSubject = request.getParameter("mailSubject");
        message = request.getParameter("message");

        String toEmailId = null;
        String fromEmailId = null;
        String host = null;
        String port = null;
        String userName = null;
        String pwd = null;
        String relayWithAuth = null;
        String protocol=null;

        if (mailSubject == null || message == null)
        {
            sErrorMessage.append("Mail subject Or Mail message should not be empty.");
            StringBuilder msg = new StringBuilder();
            msg.append(sSuccessMessage.toString());
            msg.append("<BR/>");
            msg.append(sErrorMessage.toString());

            request.setAttribute("cbmessage", msg.toString());
            RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
            rd.forward(request, res);
            return;
        }
        ResultSet rsMrchnt = null;
        try
        {
            Functions functions = new Functions();
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            con= Database.getConnection();
            StringBuffer query = new StringBuffer("SELECT ma.accountid,ma.memberid,m.contact_emails,p.companyfromemail,p.smtp_host,smtp_port,smtp_user,smtp_password,relayWithAuth,protocol, p.siteurl, p.logoName, partnerName, siteurl,supporturl,companysupportmailid, m.contact_persons, m.company_name, m.sitename FROM member_account_mapping AS ma, members AS m, partners AS p WHERE ma.memberid=m.memberid AND isActive='Y' AND isTest='N' AND p.partnerId= m.partnerId");

            if (functions.isValueNull(toid))
            {
                query.append(" and ma.memberid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
            }
            if (functions.isValueNull(accountId))
            {
                query.append(" and ma.accountid='" + ESAPI.encoder().encodeForSQL(me,accountId) + "'");
            }

            Log.debug("Query:-" + query);

            rsMrchnt =Database.executeQuery( query.toString(),con);
            MailService mailService=new MailService();
            HashMap <MailPlaceHolder, String> mailContant = new HashMap<MailPlaceHolder, String>() ;
            mailContant.put(MailPlaceHolder.SUBJECT, mailSubject);

            while (rsMrchnt.next())
            {
                mailContant.put(MailPlaceHolder.NAME, rsMrchnt.getString("contact_persons"));
                mailContant.put(MailPlaceHolder.LOGO,"<table style=\"margin:1%;width:200px;background-color:white\" width=\"200px\" height=\"60\" bgcolor=\"white\"><tr><td><img width=\"200px\" height=\"60\"  src="+(String)rsMrchnt.getString("siteurl")+templateImagePath+rsMrchnt.getString("logoName")+"></td></tr></table>");
                mailContant.put(MailPlaceHolder.EmailFormAddress, rsMrchnt.getString("companyfromemail"));
                mailContant.put(MailPlaceHolder.COMPANYNAME, rsMrchnt.getString("partnerName"));
                mailContant.put(MailPlaceHolder.LIVEURL, rsMrchnt.getString("siteurl"));
                mailContant.put(MailPlaceHolder.SUPPORTLINK, rsMrchnt.getString("supporturl"));
                mailContant.put(MailPlaceHolder.SUPPORTEMAIL, rsMrchnt.getString("companysupportmailid"));
                mailContant.put(MailPlaceHolder.MERCHANTCOMPANYNAME, rsMrchnt.getString("company_name"));
                mailContant.put(MailPlaceHolder.MCOMNAME, rsMrchnt.getString("sitename"));
                mailContant.put(MailPlaceHolder.RELAY_WITH_AUTH, rsMrchnt.getString("relayWithAuth"));
                mailContant.put(MailPlaceHolder.PROTOCOL, rsMrchnt.getString("protocol"));

                String mailMessage = Functions.replaceTagForCustomTemplate(message, mailContant);

                toEmailId = rsMrchnt.getString("contact_emails");
                fromEmailId = rsMrchnt.getString("companyfromemail");
                host = rsMrchnt.getString("smtp_host");
                port = rsMrchnt.getString("smtp_port");
                userName = rsMrchnt.getString("smtp_user");
                pwd = rsMrchnt.getString("smtp_password");
                relayWithAuth = rsMrchnt.getString("relayWithAuth");
                protocol=rsMrchnt.getString("protocol");

                mailService.sendHtmlMail_WhiteLabel(toEmailId,fromEmailId,null,null,mailSubject,mailMessage,host,port,userName,pwd,relayWithAuth,protocol);
                sSuccessMessage.append("Mail sent to Active Memberid:- " + rsMrchnt.getString("memberid") + "<BR>");
            }
        }
        catch (SQLException sqe)
        {
            Log.error("SQL Exception in send mail",sqe);
            sErrorMessage.append("Internal System Error Send Mail \r\n");
        }
        catch (SystemError cnf)
        {   Log.error("SYSTEM error is occure in send mail",cnf);
            sErrorMessage.append("Internal System Error Send Mail \r\n");
        }
        catch (Exception e)
        {   Log.error("Exception occure in send mail",e);
            sErrorMessage.append("Internal System Error Send Mail \r\n");
        }
        finally
        {
            Database.closeResultSet(rsMrchnt);
            Database.closeConnection(con);
        }

        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");

        chargeBackMessage.append(sErrorMessage.toString());

        request.setAttribute("cbmessage", chargeBackMessage.toString());
        RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
        rd.forward(request, res);
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}
