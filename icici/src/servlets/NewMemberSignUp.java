import com.directi.pg.*;
import com.invoice.dao.InvoiceEntry;
import com.logicboxes.util.ApplicationProperties;
import com.manager.PartnerManager;
import com.manager.vo.PartnerDefaultConfigVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Mail.MailService;
import com.payment.MultipleMemberUtill;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import javacryption.aes.AesCtr;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.AuthenticationAccountsException;
import servlets.ChargesUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 4/19/14
 * Time: 12:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewMemberSignUp extends HttpServlet
{
    private static Logger log = new Logger(NewMemberSignUp.class.getName());
    Functions functions = new Functions();
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();

        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String errormsg = "";
        String passwd = null;
        String conpasswd = null;

        Hashtable detailhash = new Hashtable();
        Merchants merchants = new Merchants();

        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;




        String redirectPage = "";
        String EOL = "<BR>";
        boolean flag = true;
        boolean isUpdate=false;
        String remoteAddr = Functions.getIpAddress(request);
        int serverPort = request.getServerPort();
        String servletPath = request.getServletPath();
        String userAgent = request.getHeader("User-Agent");
        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath + ",User-Agent="+userAgent;
        String url=request.getHeader("referer");



        passwd=request.getParameter("passwd");
        conpasswd=request.getParameter("conpasswd");


        if (request.getParameter("passwd") != null)
        {
            passwd = AesCtr.decrypt(request.getParameter("passwd"), request.getParameter("ctoken"), 256);
            request.setAttribute("passwd", passwd);
        }
        if (request.getParameter("conpasswd") != null)
        {
            conpasswd = AesCtr.decrypt(request.getParameter("conpasswd"), request.getParameter("ctoken"), 256);
            request.setAttribute("conpasswd", conpasswd);
        }
        if (request.getParameter("step").equals("1"))
        {
            if(!ESAPI.validator().isValidInput("username",request.getParameter("username"),"username",50,false))
            {
                flag=false;
                errormsg=errormsg + "Invalid Username or Email Address." + EOL;
                detailhash.put("login", request.getParameter("username"));
            }
            else
            {
                detailhash.put("login",request.getParameter("username"));
            }
            if ((!ESAPI.validator().isValidInput("passwd",passwd,"NewPassword",20,false)))
            {
                flag=false;
                errormsg = errormsg + "Invalid password."+EOL;
            }
            else
            {
                detailhash.put("passwd", passwd);
            }
            if ((!ESAPI.validator().isValidInput("conpasswd",conpasswd,"NewPassword",20,false)) || (!(passwd).equals(conpasswd)))
            {
                flag=false;
                errormsg = errormsg + "Invalid password & confirm password."+EOL;
            }
            else
            {
                detailhash.put("conpasswd", request.getParameter("conpasswd"));
            }
            if (!ESAPI.validator().isValidInput("company_name ", request.getParameter("company_name"), "companyName", 100, false))
            {
                flag = false;
                errormsg = errormsg + "Invalid organisation name." + EOL;
                detailhash.put("company_name", request.getParameter("company_name"));
            }
            else
            {
                detailhash.put("company_name", request.getParameter("company_name"));
            }
            String country = "";
            if(functions.isValueNull(request.getParameter("country")))
            {
                String [] splitValue = request.getParameter("country").split("\\|");
                country = splitValue[0];
            }
            if (!ESAPI.validator().isValidInput("country",country,"StrictString",25,false))
            {
                errormsg = errormsg + "Please enter Valid Country Name."+EOL;
                flag = false;
                detailhash.put("country", country);
            }
            else
                detailhash.put("country", country);
            String telno = request.getParameter("telno");
            String phonecc = request.getParameter("phonecc");
            String phoneNo = phonecc+"-"+telno;

            detailhash.put("phone",telno);
            if (!ESAPI.validator().isValidInput("telno",(String) telno,"SignupPhone",20,false))
            {   log.debug("PLS enter valid phone number ");
                errormsg = errormsg + "Please enter valid telephone number."+EOL;
                flag = false;
                detailhash.put("telno", (String) phoneNo);
            }
            else
            {
                detailhash.put("telno", (String) phoneNo);
            }
            if (!ESAPI.validator().isValidInput("sitename", request.getParameter("sitename"), "URL", 100, false) || functions.hasHTMLTags(request.getParameter("sitename")))
            {
                flag = false;
                errormsg = errormsg + "Invalid site URL. " + EOL;
                detailhash.put("sitename", request.getParameter("sitename"));
            }
            else
            {
                detailhash.put("sitename", request.getParameter("sitename"));
            }
            if (functions.hasHTMLTags(phonecc))
            {
                flag = false;
                errormsg = errormsg + "Invalid Phone no CC. " + EOL;
            }
            if (!ESAPI.validator().isValidInput("contact_persons", request.getParameter("contact_persons"), "contactName", 100, false))
            {
                flag = false;
                errormsg = errormsg + "Invalid main contact name." + EOL;
                detailhash.put("contact_persons", request.getParameter("contact_persons"));
            }
            else
            {
                detailhash.put("contact_persons", request.getParameter("contact_persons"));
            }
            if (!ESAPI.validator().isValidInput("contact_emails", request.getParameter("contact_emails"), "Email", 250, false))
            {
                flag = false;
                errormsg = errormsg + "Invalid main contact mailid." + EOL;
                detailhash.put("contact_emails", request.getParameter("contact_emails"));
            }
            else
            {
                detailhash.put("contact_emails", request.getParameter("contact_emails"));
            }
            if(functions.isValueNull(request.getParameter("maincontact_ccmailid"))){
                String columnName = request.getParameter("maincontact_ccmailid");
                String[] columnNameArray = columnName.split(";");
                for (int i = 0; i < columnNameArray.length; i++)
                {
                    String tempColumn = "";
                    tempColumn = columnNameArray[i];
                    System.out.println("tempColumn"+ tempColumn);
                        if (!ESAPI.validator().isValidInput("maincontact_ccmailid", tempColumn, "Email", 250, true))
                        {
                            flag = false;
                            errormsg = errormsg + "Invalid contact cc mailid "+tempColumn+"." + EOL;
                        }
                }
                detailhash.put("maincontact_ccmailid", request.getParameter("maincontact_ccmailid"));
               }else{
                detailhash.put("maincontact_ccmailid", request.getParameter("maincontact_ccmailid"));
               }

            if(functions.isValueNull(request.getParameter("maincontact_bccmailid"))){
                String columnName = request.getParameter("maincontact_bccmailid");
                String[] columnNameArray = columnName.split(";");

                for (int i = 0; i < columnNameArray.length; i++)
                {
                    String tempColumn = "";
                    tempColumn = columnNameArray[i];
                    if (!ESAPI.validator().isValidInput("maincontact_bccmailid", tempColumn, "Email", 250, true))
                    {
                        flag = false;
                        errormsg = errormsg + "Invalid contact bcc mailid "+tempColumn+"." + EOL;
                    }
                }
                detailhash.put("maincontact_bccmailid", request.getParameter("maincontact_bccmailid"));
            }else{
                detailhash.put("maincontact_bccmailid", request.getParameter("maincontact_bccmailid"));
            }

            if (!ESAPI.validator().isValidInput("maincontact_phone", request.getParameter("maincontact_phone"), "SignupPhone", 20, true))
            {
                flag = false;
                errormsg = errormsg + "Invalid main contact phone." + EOL;
                detailhash.put("maincontact_phone", request.getParameter("maincontact_phone"));
            }
            else
            {
                detailhash.put("maincontact_phone", request.getParameter("maincontact_phone"));
            }


            if (!ESAPI.validator().isValidInput("support_persons", request.getParameter("support_persons"), "contactName", 100, false))
            {
                flag = false;
                errormsg = errormsg + "Invalid Customer Support Contact name." + EOL;
                detailhash.put("support_persons", request.getParameter("support_persons"));
            }
            else
            {
                detailhash.put("support_persons", request.getParameter("support_persons"));
            }
            if (!ESAPI.validator().isValidInput("support_emails", request.getParameter("support_emails"), "Email", 250, false))
            {
                flag = false;
                errormsg = errormsg + "Invalid Customer support mailid." + EOL;
                detailhash.put("support_emails", request.getParameter("support_emails"));
            }
            else
            {
                detailhash.put("support_emails", request.getParameter("support_emails"));
            }
            if(functions.isValueNull(request.getParameter("support_ccmailid"))){
                String columnName = request.getParameter("support_ccmailid");
                String[] columnNameArray = columnName.split(";");

                for (int i = 0; i < columnNameArray.length; i++)
                {
                    String tempColumn = "";
                    tempColumn = columnNameArray[i];
                    if (!ESAPI.validator().isValidInput("support_ccmailid", tempColumn, "Email", 250, true))
                    {
                        flag = false;
                        errormsg = errormsg + "Invalid Customer support cc mailid "+tempColumn+"." + EOL;
                    }
                }
                detailhash.put("support_ccmailid", request.getParameter("support_ccmailid"));
            }else{
                detailhash.put("support_ccmailid", request.getParameter("support_ccmailid"));
            }
            if(functions.isValueNull(request.getParameter("support_bccmailid"))){
                String columnName = request.getParameter("support_bccmailid");
                String[] columnNameArray = columnName.split(";");

                for (int i = 0; i < columnNameArray.length; i++)
                {
                    String tempColumn = "";
                    tempColumn = columnNameArray[i];
                    if (!ESAPI.validator().isValidInput("support_bccmailid", tempColumn, "Email", 250, true))
                    {
                        flag = false;
                        errormsg = errormsg + "Invalid Customer support bcc mailid "+tempColumn+"." + EOL;
                    }
                }
                detailhash.put("support_bccmailid", request.getParameter("support_bccmailid"));
            }
            else
            {
                detailhash.put("support_bccmailid", request.getParameter("support_bccmailid"));
            }
            if (!ESAPI.validator().isValidInput("support_phone", request.getParameter("support_phone"), "SignupPhone", 20, true))
            {
                flag = false;
                errormsg = errormsg + "Invalid Customer support phone number." + EOL;
                detailhash.put("support_phone", request.getParameter("support_phone"));
            }
            else
            {
                detailhash.put("support_phone", request.getParameter("support_phone"));
            }


            if (!ESAPI.validator().isValidInput("salescontact_name", request.getParameter("salescontact_name"), "contactName", 100, true))
            {
                flag = false;
                errormsg = errormsg + "Invalid sales contact name." + EOL;
                detailhash.put("salescontact_name", request.getParameter("salescontact_name"));
            }
            else
            {
                detailhash.put("salescontact_name", request.getParameter("salescontact_name"));
            }
            if (!ESAPI.validator().isValidInput("salescontact_mailid", request.getParameter("salescontact_mailid"), "Email", 250, true))
            {
                flag = false;
                errormsg = errormsg + "Invalid sales contact mailid." + EOL;
                detailhash.put("salescontact_mailid", request.getParameter("salescontact_mailid"));
            }
            else
            {
                detailhash.put("salescontact_mailid", request.getParameter("salescontact_mailid"));
            }
            if(functions.isValueNull(request.getParameter("salescontact_ccmailid"))){
                String columnName = request.getParameter("salescontact_ccmailid");
                String[] columnNameArray = columnName.split(";");

                for (int i = 0; i < columnNameArray.length; i++)
                {
                    String tempColumn = "";
                    tempColumn = columnNameArray[i];
                    if (!ESAPI.validator().isValidInput("salescontact_ccmailid", tempColumn, "Email", 250, true))
                    {
                        flag = false;
                        errormsg = errormsg + "Invalid sales cc mailid "+tempColumn+"." + EOL;
                    }
                }
                detailhash.put("salescontact_ccmailid", request.getParameter("salescontact_ccmailid"));
            }
            else
            {
                detailhash.put("salescontact_ccmailid", request.getParameter("salescontact_ccmailid"));
            }
            if(functions.isValueNull(request.getParameter("salescontact_bccmailid"))){
                String columnName = request.getParameter("salescontact_bccmailid");
                String[] columnNameArray = columnName.split(";");

                for (int i = 0; i < columnNameArray.length; i++)
                {
                    String tempColumn = "";
                    tempColumn = columnNameArray[i];
                    if (!ESAPI.validator().isValidInput("salescontact_bccmailid", tempColumn, "Email", 250, true))
                    {
                        flag = false;
                        errormsg = errormsg + "Invalid sales bcc mailid"+tempColumn+"." + EOL;
                    }
                }
                detailhash.put("salescontact_bccmailid", request.getParameter("salescontact_bccmailid"));
            }
            else
            {
                detailhash.put("salescontact_bccmailid", request.getParameter("salescontact_bccmailid"));
            }

            if (!ESAPI.validator().isValidInput("salescontact_phone", request.getParameter("salescontact_phone"), "SignupPhone", 20, true))
            {
                flag = false;
                errormsg = errormsg + "Invalid sales contact phone." + EOL;
                detailhash.put("salescontact_phone", request.getParameter("salescontact_phone"));
            }
            else
            {
                detailhash.put("salescontact_phone", request.getParameter("salescontact_phone"));
            }
            if (!ESAPI.validator().isValidInput("billingcontact_name", request.getParameter("billingcontact_name"), "contactName", 100, true))
            {
                flag = false;
                errormsg = errormsg + "Invalid billing contact name." + EOL;
                detailhash.put("billingcontact_name", request.getParameter("billingcontact_name"));
            }
            else
            {
                detailhash.put("billingcontact_name", request.getParameter("billingcontact_name"));
            }
            if (!ESAPI.validator().isValidInput("billingcontact_mailid", request.getParameter("billingcontact_mailid"), "Email", 250, true))
            {
                flag = false;
                errormsg = errormsg + "Invalid billing mailid." + EOL;
                detailhash.put("billingcontact_mailid", request.getParameter("billingcontact_mailid"));
            }
            else
            {
                detailhash.put("billingcontact_mailid", request.getParameter("billingcontact_mailid"));
            }
            if(functions.isValueNull(request.getParameter("billingcontact_ccmailid"))){
                String columnName = request.getParameter("billingcontact_ccmailid");
                String[] columnNameArray = columnName.split(";");

                for (int i = 0; i < columnNameArray.length; i++)
                {
                    String tempColumn = "";
                    tempColumn = columnNameArray[i];
                    if (!ESAPI.validator().isValidInput("billingcontact_ccmailid", tempColumn, "Email", 250, true))
                    {
                        flag = false;
                        errormsg = errormsg + "Invalid billing cc mailid "+tempColumn+"." + EOL;
                    }
                }
                detailhash.put("billingcontact_ccmailid", request.getParameter("billingcontact_ccmailid"));
            }
            else
            {
                detailhash.put("billingcontact_ccmailid", request.getParameter("billingcontact_ccmailid"));
            }
            if(functions.isValueNull(request.getParameter("billingcontact_bccmailid"))){
                String columnName = request.getParameter("billingcontact_bccmailid");
                String[] columnNameArray = columnName.split(";");

                for (int i = 0; i < columnNameArray.length; i++)
                {
                    String tempColumn = "";
                    tempColumn = columnNameArray[i];
                    if (!ESAPI.validator().isValidInput("billingcontact_bccmailid", tempColumn, "Email", 250, true))
                    {
                        flag = false;
                        errormsg = errormsg + "Invalid billing  bcc mailid "+tempColumn+"." + EOL;
                    }
                }
                detailhash.put("billingcontact_bccmailid", request.getParameter("billingcontact_bccmailid"));
            }
            else
            {
                detailhash.put("billingcontact_bccmailid", request.getParameter("billingcontact_bccmailid"));
            }
            if (!ESAPI.validator().isValidInput("billingcontact_phone", request.getParameter("billingcontact_phone"), "SignupPhone", 20, true))
            {
                flag = false;
                errormsg = errormsg + "Invalid billing phone." + EOL;
                detailhash.put("billingcontact_phone", request.getParameter("billingcontact_phone"));
            }
            else
            {
                detailhash.put("billingcontact_phone", request.getParameter("billingcontact_phone"));
            }
            if(!ESAPI.validator().isValidInput("fraudcontact_name", request.getParameter("fraudcontact_name"),"contactName", 100,true))
            {
                flag=false;
                errormsg=errormsg + "Invalid fraud contact_name." + EOL;
                detailhash.put("fraudcontact_name",request.getParameter("fraudcontact_name"));
            }
            else
            {
                detailhash.put("fraudcontact_name",request.getParameter("fraudcontact_name"));
            }
            if(!ESAPI.validator().isValidInput("fraudcontact_mailid",request.getParameter("fraudcontact_mailid"),"Email", 250, true))
            {
                flag=false;
                errormsg=errormsg + "Invalid fraud contact mailid." + EOL;
                detailhash.put("fraudcontact_mailid", request.getParameter("fraudcontact_mailid"));
            }
            else
            {
                detailhash.put("fraudcontact_mailid",request.getParameter("fraudcontact_mailid"));
            }
            if(functions.isValueNull(request.getParameter("fraudcontact_ccmailid"))){
                String columnName = request.getParameter("fraudcontact_ccmailid");
                String[] columnNameArray = columnName.split(";");

                for (int i = 0; i < columnNameArray.length; i++)
                {
                    String tempColumn = "";
                    tempColumn = columnNameArray[i];
                    if(!ESAPI.validator().isValidInput("fraudcontact_ccmailid",tempColumn,"Email", 250, true))
                    {
                        flag=false;
                        errormsg=errormsg + "Invalid fraud contact cc mailid "+tempColumn+"." + EOL;
                    }
                }
                detailhash.put("fraudcontact_ccmailid", request.getParameter("fraudcontact_ccmailid"));
            }
            else
            {
                detailhash.put("fraudcontact_ccmailid",request.getParameter("fraudcontact_ccmailid"));
            }
            if(functions.isValueNull(request.getParameter("fraudcontact_bccmailid"))){
                String columnName = request.getParameter("fraudcontact_bccmailid");
                String[] columnNameArray = columnName.split(";");

                for (int i = 0; i < columnNameArray.length; i++)
                {
                    String tempColumn = "";
                    tempColumn = columnNameArray[i];
                    if(!ESAPI.validator().isValidInput("fraudcontact_bccmailid",tempColumn,"Email", 250, true))
                    {
                        flag=false;
                        errormsg=errormsg + "Invalid fraud contact bcc mailid "+tempColumn+"." + EOL;
                    }
                }
                detailhash.put("fraudcontact_bccmailid", request.getParameter("fraudcontact_bccmailid"));
            }
            else
            {
                detailhash.put("fraudcontact_bccmailid",request.getParameter("fraudcontact_bccmailid"));
            }
            if(!ESAPI.validator().isValidInput("fraudcontact_phone",request.getParameter("fraudcontact_phone"),"SignupPhone", 20, true))
            {
                flag=false;
                errormsg=errormsg + "Invalid fraud contact phone." + EOL;
                detailhash.put("fraudcontact_phone", request.getParameter("fraudcontact_phone"));
            }
            else
            {
                detailhash.put("fraudcontact_phone",request.getParameter("fraudcontact_phone"));
            }
            if(!ESAPI.validator().isValidInput("refundcontact_name", request.getParameter("refundcontact_name"),"contactName", 100,true))
            {
                flag=false;
                errormsg=errormsg + "Invalid refund contact name." + EOL;
                detailhash.put("refundcontact_name",request.getParameter("refundcontact_name"));
            }
            else
            {
                detailhash.put("refundcontact_name",request.getParameter("refundcontact_name"));
            }
            if(!ESAPI.validator().isValidInput("refundcontact_mailid",request.getParameter("refundcontact_mailid"),"Email", 250, true))
            {
                flag=false;
                errormsg=errormsg + "Invalid refund contact mailid." + EOL;
                detailhash.put("refundcontact_mailid", request.getParameter("refundcontact_mailid"));
            }
            else
            {
                detailhash.put("refundcontact_mailid",request.getParameter("refundcontact_mailid"));
            }
            if(functions.isValueNull(request.getParameter("refundcontact_ccmailid"))){
                String columnName = request.getParameter("refundcontact_ccmailid");
                String[] columnNameArray = columnName.split(";");

                for (int i = 0; i < columnNameArray.length; i++)
                {
                    String tempColumn = "";
                    tempColumn = columnNameArray[i];
                    if(!ESAPI.validator().isValidInput("refundcontact_ccmailid",tempColumn,"Email", 250, true))
                    {
                        flag=false;
                        errormsg=errormsg + "Invalid refund contact cc mailid "+tempColumn+"." + EOL;
                    }
                }
                detailhash.put("refundcontact_ccmailid", request.getParameter("refundcontact_ccmailid"));
            }
            else
            {
                detailhash.put("refundcontact_ccmailid",request.getParameter("refundcontact_ccmailid"));
            }
            if(functions.isValueNull(request.getParameter("refundcontact_bccmailid"))){
                String columnName = request.getParameter("refundcontact_bccmailid");
                String[] columnNameArray = columnName.split(";");

                for (int i = 0; i < columnNameArray.length; i++)
                {
                    String tempColumn = "";
                    tempColumn = columnNameArray[i];
                    if(!ESAPI.validator().isValidInput("refundcontact_bccmailid",tempColumn,"Email", 250, true))
                    {
                        flag=false;
                        errormsg=errormsg + "Invalid refund contact bcc mailid "+tempColumn+"." + EOL;
                    }
                }
                detailhash.put("refundcontact_bccmailid", request.getParameter("refundcontact_bccmailid"));
            }
            else
            {
                detailhash.put("refundcontact_bccmailid",request.getParameter("refundcontact_bccmailid"));
            }
            if(!ESAPI.validator().isValidInput("refundcontact_phone",request.getParameter("refundcontact_phone"),"SignupPhone", 20, true))
            {
                flag=false;
                errormsg=errormsg + "Invalid refund contact phone." + EOL;
                detailhash.put("refundcontact_phone", request.getParameter("refundcontact_phone"));
            }
            else
            {
                detailhash.put("refundcontact_phone",request.getParameter("refundcontact_phone"));
            }
            if(!ESAPI.validator().isValidInput("cbcontact_name", request.getParameter("cbcontact_name"),"contactName", 100,true))
            {
                flag=false;
                errormsg=errormsg + "Invalid chargeback contact person." + EOL;
                detailhash.put("cbcontact_name",request.getParameter("cbcontact_name"));
            }
            else
            {
                detailhash.put("cbcontact_name",request.getParameter("cbcontact_name"));
            }
            if(!ESAPI.validator().isValidInput("cbcontact_mailid",request.getParameter("cbcontact_mailid"),"Email", 250, true))
            {
                flag=false;
                errormsg=errormsg + "Invalid chargeback mailid." + EOL;
                detailhash.put("cbcontact_mailid", request.getParameter("cbcontact_mailid"));
            }
            else
            {
                detailhash.put("cbcontact_mailid",request.getParameter("cbcontact_mailid"));
            }
            if(functions.isValueNull(request.getParameter("cbcontact_ccmailid"))){
                String columnName = request.getParameter("cbcontact_ccmailid");
                String[] columnNameArray = columnName.split(";");

                for (int i = 0; i < columnNameArray.length; i++)
                {
                    String tempColumn = "";
                    tempColumn = columnNameArray[i];
                    if(!ESAPI.validator().isValidInput("cbcontact_ccmailid",tempColumn,"Email", 250, true))
                    {
                        flag=false;
                        errormsg=errormsg + "Invalid chargeback cc mailid "+tempColumn+"." + EOL;
                    }
                }
                detailhash.put("cbcontact_ccmailid", request.getParameter("cbcontact_ccmailid"));
            }
            else
            {
                detailhash.put("cbcontact_ccmailid",request.getParameter("cbcontact_ccmailid"));
            }
            if(functions.isValueNull(request.getParameter("cbcontact_bccmailid"))){
                String columnName = request.getParameter("cbcontact_bccmailid");
                String[] columnNameArray = columnName.split(";");

                for (int i = 0; i < columnNameArray.length; i++)
                {
                    String tempColumn = "";
                    tempColumn = columnNameArray[i];
                    if(!ESAPI.validator().isValidInput("cbcontact_bccmailid",tempColumn,"Email", 250, true))
                    {
                        flag=false;
                        errormsg=errormsg + "Invalid chargeback bcc mailid "+tempColumn+"." + EOL;
                    }
                }
                detailhash.put("cbcontact_bccmailid", request.getParameter("cbcontact_bccmailid"));
            }
            else
            {
                detailhash.put("cbcontact_bccmailid",request.getParameter("cbcontact_bccmailid"));
            }
            if(!ESAPI.validator().isValidInput("cbcontact_phone",request.getParameter("cbcontact_phone"),"SignupPhone", 20, true))
            {
                flag=false;
                errormsg=errormsg + "Invalid chargeback contact mailid." + EOL;
                detailhash.put("cbcontact_phone", request.getParameter("cbcontact_phone"));
            }
            else
            {
                detailhash.put("cbcontact_phone",request.getParameter("cbcontact_phone"));
            }
            if(!ESAPI.validator().isValidInput("technicalcontact_name", request.getParameter("technicalcontact_name"),"contactName", 100,true))
            {
                flag=false;
                errormsg=errormsg + "Invalid technical contact name." + EOL;
                detailhash.put("technicalcontact_name",request.getParameter("technicalcontact_name"));
            }
            else
            {
                detailhash.put("technicalcontact_name",request.getParameter("technicalcontact_name"));
            }
            if(!ESAPI.validator().isValidInput("technicalcontact_mailid",request.getParameter("technicalcontact_mailid"),"Email", 250, true))
            {
                flag=false;
                errormsg=errormsg + "Invalid technical contact mailid." + EOL;
                detailhash.put("technicalcontact_mailid", request.getParameter("technicalcontact_mailid"));
            }
            else
            {
                detailhash.put("technicalcontact_mailid",request.getParameter("technicalcontact_mailid"));
            }
            if(functions.isValueNull(request.getParameter("technicalcontact_ccmailid"))){
                String columnName = request.getParameter("technicalcontact_ccmailid");
                String[] columnNameArray = columnName.split(";");

                for (int i = 0; i < columnNameArray.length; i++)
                {
                    String tempColumn = "";
                    tempColumn = columnNameArray[i];
                    if(!ESAPI.validator().isValidInput("technicalcontact_ccmailid",tempColumn,"Email", 250, true))
                    {
                        flag=false;
                        errormsg=errormsg + "Invalid technical  contact cc mailid "+tempColumn+"." + EOL;
                    }
                }
                detailhash.put("technicalcontact_ccmailid", request.getParameter("technicalcontact_ccmailid"));
            }
            else
            {
                detailhash.put("technicalcontact_ccmailid",request.getParameter("technicalcontact_ccmailid"));
            }
            if(functions.isValueNull(request.getParameter("technicalcontact_bccmailid"))){
                String columnName = request.getParameter("technicalcontact_bccmailid");
                String[] columnNameArray = columnName.split(";");

                for (int i = 0; i < columnNameArray.length; i++)
                {
                    String tempColumn = "";
                    tempColumn = columnNameArray[i];
                    if(!ESAPI.validator().isValidInput("technicalcontact_bccmailid",tempColumn,"Email", 250, true))
                    {
                        flag=false;
                        errormsg=errormsg + "Invalid technical contact bcc mailid"+tempColumn+"." + EOL;
                    }
                }
                detailhash.put("technicalcontact_bccmailid", request.getParameter("technicalcontact_bccmailid"));
            }
            else
            {
                detailhash.put("technicalcontact_bccmailid",request.getParameter("technicalcontact_bccmailid"));
            }
            if(!ESAPI.validator().isValidInput("technicalcontact_phone",request.getParameter("technicalcontact_phone"),"SignupPhone", 20, true))
            {
                flag=false;
                errormsg=errormsg + "Invalid technical contact phone." + EOL;
                detailhash.put("technicalcontact_phone", request.getParameter("technicalcontact_phone"));
            }
            else
            {
                detailhash.put("technicalcontact_phone",request.getParameter("technicalcontact_phone"));
            }
            if (!ESAPI.validator().isValidInput("domain", request.getParameter("domain"), "DomainURL", 5000, true))
            {
                flag = false;
                errormsg = errormsg + "Invalid domain URL. " + EOL;
                detailhash.put("domain", request.getParameter("domain"));
            }
            else
            {
                detailhash.put("domain", request.getParameter("domain"));
            }
            detailhash.put("emailtoken", (String) request.getParameter("emailtoken"));
            detailhash.put("url", url);
            detailhash.put("ipAddress", remoteAddr);
            detailhash.put("httpheader", header);
            detailhash.put("actionExecutorId",actionExecutorId);
            detailhash.put("actionExecutorName",role+"-"+username);

            log.error("actionExecutorId-----"+actionExecutorId);
            log.error("actionExecutorName-----"+actionExecutorName);




            if (flag == false)
            {
                request.setAttribute("error", errormsg);
                request.setAttribute("details",detailhash);
                RequestDispatcher requestDispatcher = request.getRequestDispatcher("/membersignup.jsp");
                requestDispatcher.forward(request, res);
            }
            else
            {
                if (request.getParameter("step").equals("1"))
                {
                    try
                    {
                        PartnerManager partnerManager=new PartnerManager();
                        PartnerDefaultConfigVO partnerDefaultConfigVO=partnerManager.getPartnerDefaultConfig("1");
                        if(partnerDefaultConfigVO == null){
                            errormsg="partner default configuration not found";
                            request.setAttribute("details", detailhash);
                            redirectPage="/membersignup.jsp?ctoken=";
                        }
                        else
                        {
                            if (merchants.isMember((String) detailhash.get("login")) || multipleMemberUtill.isUniqueChildMember((String) detailhash.get("login")))
                            {
                                log.debug("redirect to NEWLOGIN");
                                session.setAttribute("newmember", detailhash);
                                request.setAttribute("username", detailhash.get("login"));
                                errormsg = detailhash.get("login") + " is already exist in records kindly provide unique username.";
                                redirectPage = "/newloginname.jsp?ctoken=";
                            }
                            else
                            {
                                request.setAttribute("role","merchant");
                                addMerchant(detailhash,partnerDefaultConfigVO);
                                request.setAttribute("username", detailhash.get("login"));
                                errormsg = detailhash.get("login") + " Registration successful";
                                redirectPage = "/memberlist.jsp?ctoken=";
                                isUpdate=true;
                            }
                        }
                        request.setAttribute("error", errormsg);
                        RequestDispatcher rd = request.getRequestDispatcher(redirectPage + user.getCSRFToken());
                        rd.forward(request, res);
                    }
                    catch (SystemError systemError)
                    {
                        log.error("System Error while collecting member details", systemError);
                        if(systemError.getMessage().contains("Duplicate"))
                        {
                            log.debug("redirect to NEWLOGIN");
                            session.setAttribute("newmember", detailhash);
                            request.setAttribute("username", detailhash.get("login"));
                            errormsg = detailhash.get("login") + " is already exist in records kindly provide unique username.";
                            redirectPage = "/newloginname.jsp?ctoken=";
                            request.setAttribute("error", errormsg);
                            RequestDispatcher rd = request.getRequestDispatcher(redirectPage + user.getCSRFToken());
                            rd.forward(request, res);
                            return;
                        }
                    }
                    catch (PZDBViolationException e)
                    {
                        log.error("PZ DB Exception while getting uniqueness check from the members_users...",e);
                        PZExceptionHandler.handleDBCVEException(e,null,"UNIQUENESS CHECK from members_users table.");
                    }
                }
            }
        }
        else if (request.getParameter("step").equals("3"))
        {
            detailhash = (Hashtable) session.getAttribute("newmember");
            try
            {
                PartnerManager partnerManager=new PartnerManager();
                PartnerDefaultConfigVO partnerDefaultConfigVO=partnerManager.getPartnerDefaultConfig("1");
                if(partnerDefaultConfigVO == null){
                    errormsg="partner default configuration not found";
                    request.setAttribute("details", detailhash);
                    redirectPage="/membersignup.jsp?ctoken=";
                }
                else
                {
                    if (!ESAPI.validator().isValidInput("username", request.getParameter("username"), "UserName", 30, false) || merchants.isMember(request.getParameter("username")) || multipleMemberUtill.isUniqueChildMember(request.getParameter("username")))
                    {
                        request.setAttribute("username", request.getParameter("username"));
                        errormsg = errormsg + "Please Enter Valid Username.";
                        redirectPage = "/newloginname.jsp?ctoken=";
                    }
                    else
                    {
                        detailhash.put("login", request.getParameter("username"));
                        request.setAttribute("role","merchant");
                        addMerchant(detailhash,partnerDefaultConfigVO);
                        errormsg = detailhash.get("login") + " Registration successful";
                        redirectPage = "/memberlist.jsp?ctoken=";
                        isUpdate=true;
                    }
                }
                request.setAttribute("error", errormsg);
                RequestDispatcher rd = request.getRequestDispatcher(redirectPage + user.getCSRFToken());
                rd.forward(request, res);
            }
            catch (SystemError se)
            {
                log.error("Leaving Merchants (Step 3) throwing System Error : ", se);
                if(se.getMessage().contains("Duplicate"))
                {
                    log.debug("redirect to NEWLOGIN");
                    session.setAttribute("newmember", detailhash);
                    request.setAttribute("username", detailhash.get("login"));
                    errormsg = detailhash.get("login") + " is already exist in records kindly provide unique username.";
                    redirectPage = "/newloginname.jsp?ctoken=";
                    request.setAttribute("error", errormsg);
                    RequestDispatcher rd = request.getRequestDispatcher(redirectPage + user.getCSRFToken());
                    rd.forward(request, res);
                    return;
                }
            }
            catch (Exception e)
            {
                log.error("Leaving Merchants (Step 3) throwing Exception : ", e);
            }
        }
    }
    public void addMerchant(Hashtable details,PartnerDefaultConfigVO partnerDefaultConfigVO) throws SystemError
    {
        Member mem = null;
        Merchants merchants=new Merchants();
        try
        {
            User user = ESAPI.authenticator().createUser((String) details.get("login"), (String) details.get("passwd"), "merchant");
            mem = merchants.addMerchantNew(user.getAccountId(), details, partnerDefaultConfigVO);
            ChargesUtils.loadMembers();
        }
        catch (Exception e)
        {
            log.error("Add user throwing Authentication Exception ", e);

            if(e instanceof AuthenticationAccountsException )
            {
                String message=((AuthenticationAccountsException)e).getLogMessage();
                if(message.contains("Duplicate"))
                {
                    throw new SystemError("Error: " + message);
                }
            }
            try{
                merchants.DeleteBoth((String) details.get("login"));
            }
            catch(Exception e1)
            {
                log.error("Exception while deletion of Details::",e1);
            }
            throw new SystemError("Error: " + e.getMessage());
        }

        //client mail
        String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
        InvoiceEntry invoiceEntry = new InvoiceEntry();
        //merchants.updateFlag(Integer.parseInt(String.valueOf(mem.memberid)));
        MailService mailService = new MailService();
        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
        HashMap merchantSignupMail = new HashMap();
        merchantSignupMail.put(MailPlaceHolder.USERNAME, details.get("login"));
        merchantSignupMail.put(MailPlaceHolder.NAME, details.get("contact_persons"));
        merchantSignupMail.put(MailPlaceHolder.TOID, String.valueOf(mem.memberid));
        merchantSignupMail.put(MailPlaceHolder.CTOKEN, details.get("emailtoken"));
        merchantSignupMail.put(MailPlaceHolder.PARTNER_URL, liveUrl);
        merchantSignupMail.put(MailPlaceHolder.PARTNERID,("1"));
        asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNERS_MERCHANT_REGISTRATION, merchantSignupMail);
        invoiceEntry.insertInvoiceConfigDetails(String.valueOf(mem.memberid));
    }
}