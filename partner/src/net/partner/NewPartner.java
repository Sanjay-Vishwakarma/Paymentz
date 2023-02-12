package net.partner;

import com.directi.pg.*;
import com.logicboxes.util.ApplicationProperties;
import com.manager.enums.Currency;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.MultiplePartnerUtill;
import javacryption.aes.AesCtr;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.AuthenticationAccountsException;

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
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by Ajit.k on 13/09/2019.
 */



public class NewPartner extends HttpServlet
{
    static final String ROLE = "partner";
    private static Logger Log = new Logger(NewPartner.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Log.debug("Entering NewPartner");
        PartnerFunctions partnerFunctions = new PartnerFunctions();
        HttpSession session = Functions.getNewSession(request);
        if (!partnerFunctions.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        response.setContentType("text/html");
        MultiplePartnerUtill multiplePartnerUtill = new MultiplePartnerUtill();
        Functions functions = new Functions();
        String redirectpage = null;
        String errormsg = "";
        String msg = "F";
        int idx1, idx2;
        String EOL = "<BR>";
        String passwd = null;
        String conpasswd = null;
        String tpasswd = null;
        String contpasswd = null;
        Currency[] currency = Currency.values();
        String sendEmailNotification = "N";

        Log.debug("currency:::"+request.getParameter("country"));

        if(request.getParameter("sendEmailNotification") != null){
            sendEmailNotification = (String)request.getParameter("sendEmailNotification");
        }
        Log.debug("sendEmailNotification:::"+sendEmailNotification);

        if (request.getParameter("step").equals("1"))
        {
            Hashtable detailhash = new Hashtable();
            Log.debug("In newPartner.java");
            try
            {
                boolean flag = true;
                if (!ESAPI.validator().isValidInput("username", request.getParameter("username"), "UserName", 50, false))
                {
                    errormsg = errormsg + " Invalid Username." + EOL;
                    detailhash.put("login", request.getParameter("username"));
                    flag = false;
                }
                else
                {
                    detailhash.put("login", request.getParameter("username"));
                }

                /*passwd = request.getParameter("passwd");
                conpasswd = request.getParameter("conpasswd");*/

                if (request.getParameter("passwd") != null)
                {
                    Log.debug("Password decrypt::::"+request.getParameter("passwd") );
                    passwd = AesCtr.decrypt(request.getParameter("passwd"), request.getParameter("ctoken"), 256);
                }

                if (request.getParameter("conpasswd") != null)
                { Log.debug("conpasswd decrypt::::"+conpasswd);
                    conpasswd = AesCtr.decrypt(request.getParameter("conpasswd"), request.getParameter("ctoken"), 256);
                }

                if ((!ESAPI.validator().isValidInput("passwd", passwd, "NewPassword", 20, false)))
                {
                    flag = false;
                    Log.debug("Wrong Password");
                    Log.debug("Password::::"+passwd);
                    Log.debug("conpasswd::::"+conpasswd);
                    errormsg = errormsg + "Invalid Password." + EOL;
                }
                else if ((!(passwd).equals(conpasswd)))
                {
                    flag = false;
                    errormsg = errormsg + "Partner Password Mismatch." + EOL;

                }
                else
                {
                    detailhash.put("passwd", passwd);
                }

                if (!ESAPI.validator().isValidInput("partnerName ", request.getParameter("partnerName"), "organizationName", 100, false))
                {
                    errormsg = errormsg + "Invalid Partner Organisation Name." + EOL;
                    flag = false;
                    detailhash.put("partnerName", request.getParameter("partnerName"));
                }
                else
                {
                    detailhash.put("partnerName", request.getParameter("partnerName"));
                    if (isPartnerUnique(request.getParameter("partnerName")))
                    {
                        flag = false;
                        errormsg = errormsg + "Organization Name already exist." + EOL;
                    }
                }

                if (!ESAPI.validator().isValidInput("partnerNameForWLInvoice ", request.getParameter("partnerNameForWLInvoice"), "companyName", 100, true))
                {
                    errormsg = errormsg + "Invalid Organisation Name  For WL Invoice." + EOL;
                    flag = false;
                    detailhash.put("partnerNameForWLInvoice", request.getParameter("partnerNameForWLInvoice"));
                }
                else
                {
                    detailhash.put("partnerNameForWLInvoice", request.getParameter("partnerNameForWLInvoice"));
                    if (isPartnerNameForWLInvoiceUnique(request.getParameter("partnerNameForWLInvoice")))
                    {
                        flag = false;
                        errormsg = errormsg + "Organisation Name  For WL Invoice already exist." + EOL;
                    }
                }

                if (!ESAPI.validator().isValidInput("contact_persons", request.getParameter("contact_persons"), "contactName", 100, false))
                {
                    errormsg = errormsg + "Invalid Main Contact Name." + EOL;
                    flag = false;
                    detailhash.put("contact_persons", request.getParameter("contact_persons"));
                }
                else
                {
                    detailhash.put("contact_persons", request.getParameter("contact_persons"));
                }
                if (!ESAPI.validator().isValidInput("contact_emails", request.getParameter("contact_emails"), "Email", 100, false))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Main Contact Email ID." + EOL;
                    detailhash.put("contact_emails", request.getParameter("contact_emails"));
                }
                else
                {
                    detailhash.put("contact_emails", request.getParameter("contact_emails"));
                }
                if (!ESAPI.validator().isValidInput("country", request.getParameter("country"), "Description", 50, false))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Country." + EOL;
                    detailhash.put("country", request.getParameter("country"));
                }
                else
                {
                    detailhash.put("country", request.getParameter("country"));
                }
                if (!ESAPI.validator().isValidInput("address", request.getParameter("address"), "Description", 50, true))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Address." + EOL;
                    detailhash.put("address", request.getParameter("address"));
                }
                else
                {
                    detailhash.put("address", request.getParameter("address"));
                }
                if (!ESAPI.validator().isValidInput("telno", request.getParameter("telno"), "SignupPhone", 20, false))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Contact Telephone Number." + EOL;
                    detailhash.put("telno", request.getParameter("telno"));
                }
                else
                {
                    detailhash.put("telno", request.getParameter("telno"));
                }
                if (!ESAPI.validator().isValidInput("sitename", request.getParameter("sitename"), "DomainURL", 100, false))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Site URL. " + EOL;
                    detailhash.put("sitename", request.getParameter("sitename"));
                }
                else
                {
                    detailhash.put("sitename", request.getParameter("sitename"));
                }
                if (!ESAPI.validator().isValidInput("privacyUrl", request.getParameter("privacyUrl"), "DomainURL", 100, true))

                {
                    flag = false;
                    errormsg = errormsg + "Invalid privacyUrl URL. " + EOL;
                    detailhash.put("privacyUrl", request.getParameter("privacyUrl"));
                }
                else
                {
                    detailhash.put("privacyUrl", request.getParameter("privacyUrl"));
                }
                if (!ESAPI.validator().isValidInput("domain", request.getParameter("domain"), "DomainURL", 5000, true))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Domain URL. " + EOL;
                    detailhash.put("domain", request.getParameter("domain"));
                }
                else
                {
                    detailhash.put("domain", request.getParameter("domain"));
                }
                if (!ESAPI.validator().isValidInput("companysupportmailid", request.getParameter("companysupportmailid"), "Email", 100, false))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Support Mail ID." + EOL;
                    detailhash.put("companysupportmailid", request.getParameter("companysupportmailid"));
                }
                else
                {
                    detailhash.put("companysupportmailid", request.getParameter("companysupportmailid"));
                }
                if (!ESAPI.validator().isValidInput("adminmailid", request.getParameter("adminmailid"), "Email", 100, false))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Admin Mail ID." + EOL;
                    detailhash.put("adminmailid", request.getParameter("adminmailid"));
                }
                else
                {
                    detailhash.put("adminmailid", request.getParameter("adminmailid"));
                }
                if (!ESAPI.validator().isValidInput("supporturl", request.getParameter("supporturl"), "URL", 100, false) || functions.hasHTMLTags(request.getParameter("supporturl")))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Support URL." + EOL;
                    detailhash.put("supporturl", request.getParameter("supporturl"));
                }
                else
                {
                    detailhash.put("supporturl", request.getParameter("supporturl"));
                }
                if (!ESAPI.validator().isValidInput("documentationurl", request.getParameter("documentationurl"), "URL", 100, false) || functions.hasHTMLTags(request.getParameter("documentationurl")))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Documentation URL." + EOL;
                    detailhash.put("documentationurl", request.getParameter("documentationurl"));
                }
                else
                {
                    detailhash.put("documentationurl", request.getParameter("documentationurl"));
                }
                if (!ESAPI.validator().isValidInput("salesemail", request.getParameter("salesemail"), "Email", 100, false))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Sales Email ID." + EOL;
                    detailhash.put("salesemail", request.getParameter("salesemail"));
                }
                else
                {
                    detailhash.put("salesemail", request.getParameter("salesemail"));
                }
                if (!ESAPI.validator().isValidInput("billingemail", request.getParameter("billingemail"), "Email", 100, false))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Billing Email ID." + EOL;
                    detailhash.put("billingemail", request.getParameter("billingemail"));
                }
                else
                {
                    detailhash.put("billingemail", request.getParameter("billingemail"));
                }
                if (!ESAPI.validator().isValidInput("notifyemail", request.getParameter("notifyemail"), "Email", 100, false))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Notify Email ID." + EOL;
                    detailhash.put("notifyemail", request.getParameter("notifyemail"));
                }
                else
                {
                    detailhash.put("notifyemail", request.getParameter("notifyemail"));
                }
                if (!ESAPI.validator().isValidInput("companyfromaddress", request.getParameter("companyfromaddress"), "Email", 100, false))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Company from address. " + EOL;
                    detailhash.put("companyfromaddress", request.getParameter("companyfromaddress"));
                }
                else
                {
                    detailhash.put("companyfromaddress", request.getParameter("companyfromaddress"));
                }
                /*if (!ESAPI.validator().isValidInput("supportfromaddress", request.getParameter("supportfromaddress"), "Email", 100, false))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Support from address." + EOL;
                    detailhash.put("supportfromaddress", request.getParameter("supportfromaddress"));
                }
                else
                {
                    detailhash.put("supportfromaddress", request.getParameter("supportfromaddress"));
                }*/
                if (!ESAPI.validator().isValidInput("fraudemail", request.getParameter("fraudemail"), "Email", 100, false))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Fraud Email ID." + EOL;
                    detailhash.put("fraudemail", request.getParameter("fraudemail"));
                }
                else
                {
                    detailhash.put("fraudemail", request.getParameter("fraudemail"));
                }
                if (!ESAPI.validator().isValidInput("fraudcontactname", request.getParameter("fraudcontactname"), "contactName", 100, false))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Fraud Contact Name." + EOL;
                    detailhash.put("fraudcontactname", request.getParameter("fraudcontactname"));
                }
                else
                {
                    detailhash.put("fraudcontactname", request.getParameter("fraudcontactname"));
                }

                if (!ESAPI.validator().isValidInput("salescontactname", request.getParameter("salescontactname"), "contactName", 100, false))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Sales Contact Name." + EOL;
                    detailhash.put("salescontactname", request.getParameter("salescontactname"));
                }
                else
                {
                    detailhash.put("salescontactname", request.getParameter("salescontactname"));
                }

                if (!ESAPI.validator().isValidInput("notifycontactname", request.getParameter("notifycontactname"), "contactName", 100, false))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Notify Contact Name." + EOL;
                    detailhash.put("notifycontactname", request.getParameter("notifycontactname"));
                }
                else
                {
                    detailhash.put("notifycontactname", request.getParameter("notifycontactname"));
                }

                if (!ESAPI.validator().isValidInput("billingcontactname", request.getParameter("billingcontactname"), "contactName", 100, false))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Billing Contact Name." + EOL;
                    detailhash.put("billingcontactname", request.getParameter("billingcontactname"));
                }
                else
                {
                    detailhash.put("billingcontactname", request.getParameter("billingcontactname"));
                }

                if (!ESAPI.validator().isValidInput("technicalcontactname", request.getParameter("technicalcontactname"), "contactName", 100, true))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Technical Contact Name." + EOL;
                    detailhash.put("technicalcontactname", request.getParameter("technicalcontactname"));
                }
                else
                {
                    detailhash.put("technicalcontactname", request.getParameter("technicalcontactname"));
                }

                if (!ESAPI.validator().isValidInput("chargebackcontactname", request.getParameter("chargebackcontactname"), "contactName", 100, true))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Chargeback Contact Name." + EOL;
                    detailhash.put("chargebackcontactname", request.getParameter("chargebackcontactname"));
                }
                else
                {
                    detailhash.put("chargebackcontactname", request.getParameter("chargebackcontactname"));
                }

                if (!ESAPI.validator().isValidInput("refundcontactname", request.getParameter("refundcontactname"), "contactName", 100, true))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Refund Contact Name." + EOL;
                    detailhash.put("refundcontactname", request.getParameter("refundcontactname"));
                }
                else
                {
                    detailhash.put("refundcontactname", request.getParameter("refundcontactname"));
                }

                if (!ESAPI.validator().isValidInput("technicalemailid", request.getParameter("technicalemailid"), "Email", 100, true))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Technical Email ID." + EOL;
                    detailhash.put("technicalemailid", request.getParameter("technicalemailid"));
                }
                else
                {
                    detailhash.put("technicalemailid", request.getParameter("technicalemailid"));
                }

                if (!ESAPI.validator().isValidInput("chargebackemailid", request.getParameter("chargebackemailid"), "Email", 100, true))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Chargeback Email ID." + EOL;
                    detailhash.put("chargebackemailid", request.getParameter("chargebackemailid"));
                }
                else
                {
                    detailhash.put("chargebackemailid", request.getParameter("chargebackemailid"));
                }

                if (!ESAPI.validator().isValidInput("refundemailid", request.getParameter("refundemailid"), "Email", 100, true))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Refund Email ID." + EOL;
                    detailhash.put("refundemailid", request.getParameter("refundemailid"));
                }
                else
                {
                    detailhash.put("refundemailid", request.getParameter("refundemailid"));
                }


                if (!ESAPI.validator().isValidInput("contact_ccmailid", request.getParameter("contact_ccmailid"), "Email", 100, true))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Main Contact Cc Email ID." + EOL;
                    detailhash.put("contact_ccmailid", request.getParameter("contact_ccmailid"));
                }
                else
                {
                    detailhash.put("contact_ccmailid", request.getParameter("contact_ccmailid"));
                }

                if (!ESAPI.validator().isValidInput("sales_ccemailid", request.getParameter("sales_ccemailid"), "Email", 100, true))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Sales Cc Email ID." + EOL;
                    detailhash.put("sales_ccemailid", request.getParameter("sales_ccemailid"));
                }
                else
                {
                    detailhash.put("sales_ccemailid", request.getParameter("sales_ccemailid"));
                }


                if (!ESAPI.validator().isValidInput("billing_ccemailid", request.getParameter("billing_ccemailid"), "Email", 100, true))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Billing Cc Email ID." + EOL;
                    detailhash.put("billing_ccemailid", request.getParameter("billing_ccemailid"));
                }
                else
                {
                    detailhash.put("billing_ccemailid", request.getParameter("billing_ccemailid"));
                }


                if (!ESAPI.validator().isValidInput("notify_ccemailid", request.getParameter("notify_ccemailid"), "Email", 100, true))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Notify Cc Email ID." + EOL;
                    detailhash.put("notify_ccemailid", request.getParameter("notify_ccemailid"));
                }
                else
                {
                    detailhash.put("notify_ccemailid", request.getParameter("notify_ccemailid"));
                }


                if (!ESAPI.validator().isValidInput("fraud_ccemailid", request.getParameter("fraud_ccemailid"), "Email", 100, true))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Fraud Cc Email ID." + EOL;
                    detailhash.put("fraud_ccemailid", request.getParameter("fraud_ccemailid"));
                }
                else
                {
                    detailhash.put("fraud_ccemailid", request.getParameter("fraud_ccemailid"));
                }

                if (!ESAPI.validator().isValidInput("chargeback_ccemailid", request.getParameter("chargeback_ccemailid"), "Email", 100, true))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Chargeback Cc Email ID." + EOL;
                    detailhash.put("chargeback_ccemailid", request.getParameter("chargeback_ccemailid"));
                }
                else
                {
                    detailhash.put("chargeback_ccemailid", request.getParameter("chargeback_ccemailid"));
                }


                if (!ESAPI.validator().isValidInput("refund_ccemailid", request.getParameter("refund_ccemailid"), "Email", 100, true))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Refund Cc Email ID." + EOL;
                    detailhash.put("refund_ccemailid", request.getParameter("refund_ccemailid"));
                }
                else
                {
                    detailhash.put("refund_ccemailid", request.getParameter("refund_ccemailid"));
                }

                if (!ESAPI.validator().isValidInput("technical_ccemailid", request.getParameter("technical_ccemailid"), "Email", 100, true))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Technical Cc Email ID." + EOL;
                    detailhash.put("technical_ccemailid", request.getParameter("technical_ccemailid"));
                }
                else
                {
                    detailhash.put("technical_ccemailid", request.getParameter("technical_ccemailid"));
                }

                if (!ESAPI.validator().isValidInput("smtp_host", request.getParameter("smtp_host"), "smtphost", 100, false))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid SMTP Host." + EOL;
                    detailhash.put("smtp_host", request.getParameter("smtp_host"));
                }
                else
                    detailhash.put("smtp_host", request.getParameter("smtp_host"));

                if (!ESAPI.validator().isValidInput("smtp_port", request.getParameter("smtp_port"), "PortNo", 5, false))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid SMTP Port." + EOL;
                    detailhash.put("smtp_port", request.getParameter("smtp_port"));
                }
                else
                    detailhash.put("smtp_port", request.getParameter("smtp_port"));

                if (!ESAPI.validator().isValidInput("smtp_user", request.getParameter("smtp_user"), "Email", 100, false))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid SMTP User." + EOL;
                    detailhash.put("smtp_user", request.getParameter("smtp_user"));
                }
                else
                    detailhash.put("smtp_user", request.getParameter("smtp_user"));

                if (!ESAPI.validator().isValidInput("smtp_password", request.getParameter("smtp_password"), "SafeString", 100, true) || functions.hasHTMLTags(request.getParameter("smtp_password")))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid  SMTP Password." + EOL;
                    detailhash.put("smtp_password", request.getParameter("smtp_password"));
                }
                else
                    detailhash.put("smtp_password", request.getParameter("smtp_password"));

                if (!ESAPI.validator().isValidInput("sms_user", request.getParameter("sms_user"), "SafeString", 100, true) || functions.hasHTMLTags(request.getParameter("sms_user")))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid SMS User." + EOL;
                    detailhash.put("sms_user", request.getParameter("sms_user"));
                }
                else
                    detailhash.put("sms_user", request.getParameter("sms_user"));

                if (!ESAPI.validator().isValidInput("sms_password", request.getParameter("sms_password"), "SafeString", 100, true) || functions.hasHTMLTags(request.getParameter("sms_password")))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid SMS Password." + EOL;
                    detailhash.put("sms_password", request.getParameter("sms_password"));
                }
                else
                    detailhash.put("sms_password", request.getParameter("sms_password"));


                if (!ESAPI.validator().isValidInput("from_sms", request.getParameter("from_sms"), "SafeString", 100, true) || functions.hasHTMLTags(request.getParameter("from_sms")))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid From SMS." + EOL;
                    detailhash.put("from_sms", request.getParameter("from_sms"));
                }
                else
                    detailhash.put("from_sms", request.getParameter("from_sms"));

                if (!ESAPI.validator().isValidInput("address", request.getParameter("address"), "SafeString", 255, true) || functions.hasHTMLTags(request.getParameter("address")))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Address." + EOL;
                    detailhash.put("address", request.getParameter("address"));
                }
                else
                    detailhash.put("address", request.getParameter("address"));


                Log.debug("from_sms -------");
                if (!ESAPI.validator().isValidInput("hosturl", request.getParameter("hosturl"), "HostURL", 255, false))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Host URL." + EOL;
                    detailhash.put("hosturl", request.getParameter("hosturl"));
                }
                else
                {
                    detailhash.put("hosturl", request.getParameter("hosturl"));
                }
                Log.debug("hosturl -------");
                detailhash.put("isipwhitelisted", request.getParameter("isipwhitelisted"));
                detailhash.put("iscardencryptionenable", request.getParameter("iscardencryptionenable"));
                detailhash.put("isFlightPartner", request.getParameter("isflightPartner"));
                detailhash.put("splitpayment", request.getParameter("splitpayment"));
                detailhash.put("splitpaymenttype", request.getParameter("splitpaymenttype"));
                detailhash.put("addressvalidation", request.getParameter("addressvalidation"));
                detailhash.put("addressdetaildisplay", request.getParameter("addressdetaildisplay"));
                detailhash.put("autoRedirect", request.getParameter("autoRedirect"));
                detailhash.put("flightMode", request.getParameter("flightMode"));
                detailhash.put("template", request.getParameter("template"));
                detailhash.put("reportingcurrency", request.getParameter("currency"));
                detailhash.put("ip_whitelist_invoice", request.getParameter("ip_whitelist_invoice"));
                detailhash.put("address_validation_invoice", request.getParameter("address_validation_invoice"));
                detailhash.put("defaulttemplatetheme", request.getParameter("defaulttemplatetheme"));
                detailhash.put("relaywithauth", request.getParameter("relaywithauth"));
                detailhash.put("ispcilogo", request.getParameter("ispcilogo"));
                detailhash.put("binService", request.getParameter("binService"));
//                detailhash.put("oldcheckout", request.getParameter("oldcheckout"));
                detailhash.put("sendEmailNotification", sendEmailNotification);
                detailhash.put("sms_service", request.getParameter("sms_service"));

                /*detailhash.put("emailSent", request.getParameter("emailSent"));*/
                if (!ESAPI.validator().isValidInput("termsUrl", request.getParameter("termsUrl"), "DomainURL", 255, true))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Term URL." + EOL;
                    detailhash.put("termsUrl", request.getParameter("termsUrl"));
                }
                else
                {
                    detailhash.put("termsUrl", request.getParameter("termsUrl"));
                }
                if (!ESAPI.validator().isValidInput("privacyUrl", request.getParameter("privacyUrl"), "DomainURL", 255, true))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Privacy URL." + EOL;
                    detailhash.put("privacyUrl", request.getParameter("privacyUrl"));
                }
                else
                {
                    detailhash.put("privacyUrl", request.getParameter("privacyUrl"));
                }
                if (!ESAPI.validator().isValidInput("cookiesUrl", request.getParameter("cookiesUrl"), "DomainURL", 255, true))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid CookiesUrl URL." + EOL;
                    detailhash.put("cookiesUrl", request.getParameter("cookiesUrl"));
                }
                else
                {
                    detailhash.put("cookiesUrl", request.getParameter("cookiesUrl"));
                }

                detailhash.put("checkoutInvoice", request.getParameter("checkoutInvoice"));
                detailhash.put("bankCardLimit", request.getParameter("bankCardLimit"));
                detailhash.put("supportemailfortransactionmail", request.getParameter("supportemailfortransactionmail"));
                detailhash.put("emi_configuration",request.getParameter("emi_configuration"));
                detailhash.put("exportTransactionCron",request.getParameter("exportTransactionCron"));
                detailhash.put("currenttemplatetheme", "null");
                detailhash.put("partner_id", request.getParameter("partner_id"));
                detailhash.put("partner_Name", request.getParameter("partner_Name"));
                detailhash.put("protocol", request.getParameter("protocol"));

                Log.debug("partner_Name:::::::::"+request.getParameter("partner_Name"));

                Log.debug("currenttemplatetheme -------");

                if (flag == true)
                {
                    Log.debug("Valid user data");
                    if (isPartnerLoginNameUnique((String) detailhash.get("login")) || multiplePartnerUtill.isUniqueChildMember((String) detailhash.get("login")))
                    {
                        Log.debug("isPartnerLoginNameUnique -------");
                        Log.debug("redirect to NEWLOGIN");
                        redirectpage = "/uniquepartnerloginname.jsp?ctoken=" + user.getCSRFToken();
                        session.setAttribute("newmember", detailhash);
                        request.setAttribute("username", detailhash.get("login"));
                        request.setAttribute("sendEmailNotification", sendEmailNotification);
                        Log.debug("isPartnerLoginNameUnique redirectpage -------"+redirectpage);
                    }
                    else
                    {
                        request.setAttribute("role", detailhash.get("role"));
                        addMerchant(detailhash);
                        request.setAttribute("username", detailhash.get("login"));
                        request.setAttribute("partner_Name", detailhash.get("partner_Name"));
                        Log.debug("partner_Name inside flag:::"+detailhash.get("partner_Name"));
                        Log.debug("partner_Name inside flag:::"+request.getAttribute("partner_Name"));
                        redirectpage = "/successPartner.jsp?ctoken=" + user.getCSRFToken();
                        Log.info("THANK YOU for signup  " + redirectpage);
                    }
                }
                else
                {
                    Log.debug("ENTER VALID DATA");
                    redirectpage = "/partnersignup.jsp?MES=" + msg + "&ctoken=" + user.getCSRFToken();
                    request.setAttribute("details", detailhash);
                    request.setAttribute("error", errormsg);
                    Log.debug("isPartnerLoginNameUnique else redirectpage -------"+redirectpage);
                }
            }
            catch (Exception e)
            {
                if (e.getMessage().contains("Duplicate"))
                {
                    Log.debug("redirect to NEWLOGIN");
                    redirectpage = "/uniqueloginname.jsp?ctoken=" + user.getCSRFToken();
                    session.setAttribute("newmember", detailhash);
                    request.setAttribute("username", detailhash.get("login"));
                    Log.debug("Exception if Duplicate redirectpage -------"+redirectpage);
                }
                else
                {
                    redirectpage = "/partnersignup.jsp?MES=" + msg + "&ctoken=" + user.getCSRFToken();
                    request.setAttribute("details", detailhash);
                    request.setAttribute("error", "Internal System Error while add new partner.");
                    Log.debug("Exception else Duplicate redirectpage -------"+redirectpage);
                }
            }
            Log.debug("redirectpage  " + redirectpage);
        }
        else if (request.getParameter("step").equals("2"))
        {
            String pathtoraw = null;
            String pathtolog = null;
            pathtoraw = ApplicationProperties.getProperty("PARTNER_LOGO_PATH");
            pathtolog = ApplicationProperties.getProperty("LOG_STORE");
            FileUploadBean fub = new FileUploadBean();
            fub.setSavePath(pathtoraw);
            fub.setLogpath(pathtolog);
            try
            {
                fub.doUpload(request);
            }
            catch (SystemError sys)
            {
                Log.debug("PLS enter valid logo ");
                errormsg = errormsg + sys.getMessage() + EOL;
                redirectpage = "/partnerLogo.jsp?MES=" + msg + "&ctoken=" + user.getCSRFToken();
                request.setAttribute("error", errormsg);
            }
            String uploadedFileName = fub.getFilename();
            String partnerid = fub.getFieldValue("partnerid");
            if (!ESAPI.validator().isValidInput("partnerid", request.getParameter("partnerid"), "Numbers", 3, true))
            {
                errormsg = errormsg + "Invalid or Empty Partner ID." + EOL;
                Log.debug("Invalid Partner Id......");
                redirectpage = "/partnerLogo.jsp?MES=" + msg + "&ctoken=" + user.getCSRFToken();
                request.setAttribute("error", errormsg);
            }
            else
            {

            }
        }
        else if (request.getParameter("step").equals("3"))
        {
            Log.info("inside step 3");
            Hashtable detailhash = (Hashtable) session.getAttribute("newmember");
            try
            {
                if (!ESAPI.validator().isValidInput("username", request.getParameter("username"), "alphanum", 100, false) || isPartnerLoginNameUnique(request.getParameter("username")) || multiplePartnerUtill.isUniqueChildMember(request.getParameter("username")))
                {
                    request.setAttribute("username", request.getParameter("username"));
                    errormsg = errormsg + "Please Enter Valid Username.";
                    request.setAttribute("error", errormsg);
                    redirectpage = "/uniquepartnerloginname.jsp?ctoken=" + user.getCSRFToken();
                    Log.debug("step 3 if redirectpage -------"+redirectpage);
                    RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                    rd.forward(request, response);
                    return;
                }
                else
                {
                    Log.debug("Change username");
                    Log.debug(request.getParameter("username"));
                    detailhash.put("login", request.getParameter("username"));
                    request.setAttribute("role", "partner");
                    detailhash.put("sendEmailNotification",sendEmailNotification);
                    addMerchant(detailhash);
                    Log.debug(request.getParameter("username"));
                    request.setAttribute("username", detailhash.get("login"));
                    request.setAttribute("partner_Name", detailhash.get("partner_Name"));
                    Log.debug("partner_Name:::"+detailhash.get("partner_Name"));
                    Log.debug("partner_Name:::"+request.getAttribute("partner_Name"));
                    redirectpage = "/successPartner.jsp?ctoken=" + user.getCSRFToken();
                    Log.debug("step 3 else redirectpage -------"+redirectpage);

                }
                Log.debug("redirectpage  " + redirectpage);
            }
            catch (SystemError se)
            {
                Log.error("Leaving partner (Step 3) throwing SystemError : ", se);

                if (se.getMessage().contains("Duplicate"))
                {
                    Log.debug("redirect to NEWLOGIN");
                    redirectpage = "/newpartnerloginname.jsp?ctoken=" + user.getCSRFToken();
                    session.setAttribute("newmember", detailhash);
                    request.setAttribute("username", detailhash.get("login"));
                    Log.debug("SystemError step 3 if redirectpage -------"+redirectpage);
                }
                else
                {
                    redirectpage = "/partnersignup.jsp?MES=" + msg + "&ctoken=" + user.getCSRFToken();
                    request.setAttribute("details", detailhash);
                    request.setAttribute("error", "Internal error while adding new partner.");
                    request.setAttribute("currency", currency);
                    Log.debug("SystemError step 3 else redirectpage -------"+redirectpage);
                }
            }
            catch (Exception e)
            {
                Log.error("Leaving partner (Step 3) throwing Exception : ", e);
                redirectpage = "/partnersignup.jsp?MES=" + msg + "&ctoken=" + user.getCSRFToken();
                request.setAttribute("details", detailhash);
                request.setAttribute("error", "Internal error while adding new partner.");
                Log.debug("SystemError Exception step 3 if redirectpage -------"+redirectpage);
            }
        }
        RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
        rd.forward(request, response);
    }

    public void addMerchant(Hashtable details) throws SystemError
    {
        PartnerAuthenticate mem         = null;
        HashMap mailvalue               = new HashMap();
        String sendEmailNotification    = "N";
        try
        {
            User user   = ESAPI.authenticator().createUser((String) details.get("login"), (String) details.get("passwd"), "partner");
            mem         = addPartner_new(user.getAccountId(), details);
        }
        catch (Exception e)
        {
            Log.error("Add user throwing Authentication Exception ", e);
            if (e instanceof AuthenticationAccountsException)
            {
                String message = ((AuthenticationAccountsException) e).getLogMessage();
                if (message.contains("Duplicate"))
                {
                    throw new SystemError("Error: " + message);
                }
            }
            try
            {
                Partner.DeleteBoth((String) details.get("login"));
            }
            catch (Exception e1)
            {
                Log.error("Exception while deletion of Details::", e1);
            }
            throw new SystemError("Error: " + e.getMessage());
        }
        String toid             = String.valueOf(mem.partnerid);
        String partnerId        = String.valueOf(mem.partnerid);
        sendEmailNotification   = (String) details.get("sendEmailNotification");
        mailvalue.put(MailPlaceHolder.PARTNERORGNAME, details.get("partnerName"));
        mailvalue.put(MailPlaceHolder.USERNAME, details.get("login"));
        mailvalue.put(MailPlaceHolder.TOID, toid);
        mailvalue.put(MailPlaceHolder.ADMINEMAIL, details.get("adminmailid"));
        mailvalue.put(MailPlaceHolder.COMPANYSUPPORTMAIL, details.get("companysupportmailid"));
        mailvalue.put(MailPlaceHolder.BILLINGEMAIL, details.get("billingemail"));
        mailvalue.put(MailPlaceHolder.TECHEMAIL, details.get("technicalemailid"));
        mailvalue.put(MailPlaceHolder.COMPANYFROMADDRESS, details.get("companyfromaddress"));
        // mailvalue.put(MailPlaceHolder.IPADDRESS,"127.0.0.1");
        mailvalue.put(MailPlaceHolder.SUPPORTURL, details.get("supporturl"));
        mailvalue.put(MailPlaceHolder.CONTECTEMAIL, details.get("contact_emails"));
        mailvalue.put(MailPlaceHolder.PARTNERID, partnerId);

        Log.debug("send mail to new method"+sendEmailNotification);
        //MailService mailService=new MailService();
        if(sendEmailNotification != null && "Y".equalsIgnoreCase(sendEmailNotification)){
            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
            asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNER_REGISTRATION, mailvalue);
        }
    }

    public boolean isPartnerLoginNameUnique(String login) throws SystemError
    {
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            Log.debug("check isMember method");
            String selquery = "select accountid from `user` where login = ? and roles=?";

            PreparedStatement pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, login);
            pstmt.setString(2, ROLE);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                return true;
            }
        }
        catch (SystemError se)
        {
            Log.error(" SystemError in isMember method: ", se);

            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            Log.error("Exception in isMember method: ", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return false;
    }

    public boolean isPartnerUnique(String partnerName) throws SystemError
    {
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            String query = "SELECT partnerid FROM partners where partnerName=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, partnerName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                return true;
            }
        }
        catch (SystemError systemError)
        {
            Log.error("SystemError while getting Partner is Unique", systemError);
        }
        catch (SQLException e)
        {
            Log.error(" Sql Exception", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return false;
    }

    public boolean isPartnerNameForWLInvoiceUnique(String partnerNameForWLInvoice) throws SystemError
    {
        Functions functions = new Functions();
        Connection conn = null;
        if (functions.isValueNull(partnerNameForWLInvoice))
        {
            try
            {
                conn = Database.getConnection();
                StringBuffer query = new StringBuffer("SELECT partnerOrgnizationForWL_Invoice FROM partners");
                PreparedStatement pstmt = conn.prepareStatement(query.toString());
                ResultSet rs = pstmt.executeQuery();
                while (rs.next())
                {
                    if (partnerNameForWLInvoice.equalsIgnoreCase(rs.getString("partnerOrgnizationForWL_Invoice")))
                        return true;
                }
            }
            catch (SystemError systemError)
            {
                Log.error("SystemError while getting Partner is Unique", systemError);
            }
            catch (SQLException e)
            {
                Log.error(" Sql Exception", e);
            }
            finally
            {
                Database.closeConnection(conn);
            }
        }
        return false;
    }

    public PartnerAuthenticate addPartner_new(long accid, Hashtable details) throws SystemError
    {
        Connection conn = null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer sb = new StringBuffer("insert into partners " +
                "(accid,login,clkey," +
                "partnerName, partnerOrgnizationForWL_Invoice,contact_persons,contact_emails,country," +
                "telno,logoName,siteurl,domain,relayWithAuth,supporturl,documentationurl,hosturl,companyadminid,companysupportmailid,salesemail,billingemail,companyfromemail,supportfromemail,notifyemail,smtp_host,smtp_port,smtp_user,smtp_password,sms_user,sms_password,from_sms,superadminid,isIpWhitelisted,iscardencryptionenable,fraudemailid,isFlightPartner,salescontactname,fraudcontactname,technicalemailid,technicalcontactname,chargebackemailid,chargebackcontactname,refundemailid,refundcontactname,billingcontactname,notifycontactname,splitpayment,splitpaymenttype,addressvalidation,addressdetaildisplay,flightMode,template,address,reporting_currency,default_theme,current_theme,autoRedirect,dtstamp,ip_whitelist_invoice,address_validation_invoice,ispcilogo,binService,support_email_for_transaction_mail,termsUrl,privacyUrl,cookiesUrl,checkoutInvoice,bankCardLimit,contact_ccmailid,sales_ccemailid,billing_ccemailid,notify_ccemailid,fraud_ccemailid,chargeback_ccemailid,refund_ccemailid,technical_ccemailid,emi_configuration,exportTransactionCron,protocol,sms_service) values (");

        sb.append("" + ESAPI.encoder().encodeForSQL(me, String.valueOf(accid)) + "");
        sb.append(",'" + details.get("login") + "'");
        sb.append(",'" + Merchants.generateKey() + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("partnerName")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("partnerNameForWLInvoice")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("contact_persons")) + "'");
        sb.append(",'" + details.get("contact_emails") + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("country")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("telno")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("logoName")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("sitename")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("domain")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("relaywithauth")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("supporturl")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("documentationurl")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("hosturl")) + "'");
        sb.append(",'" + details.get("adminmailid") + "'");
        sb.append(",'" + details.get("companysupportmailid") + "'");

        sb.append(",'" + details.get("salesemail") + "'");
        sb.append(",'" + details.get("billingemail") + "'");
        sb.append(",'" + details.get("companyfromaddress") + "'");
        sb.append(",'" + details.get("supportfromaddress") + "'");
        sb.append(",'" + details.get("notifyemail") + "'");

        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("smtp_host")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("smtp_port")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("smtp_user")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("smtp_password")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("sms_user")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("sms_password")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("from_sms")) + "'");
        sb.append(",'" + details.get("partner_id") + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("isipwhitelisted")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("iscardencryptionenable")) + "'");
        sb.append(",'" + details.get("fraudemail") + "'");
        sb.append(",'" + details.get("isFlightPartner") + "'");
        sb.append(",'" + details.get("salescontactname") + "'");
        sb.append(",'" + details.get("fraudcontactname") + "'");
        sb.append(",'" + details.get("technicalemailid") + "'");
        sb.append(",'" + details.get("technicalcontactname") + "'");
        sb.append(",'" + details.get("chargebackemailid") + "'");
        sb.append(",'" + details.get("chargebackcontactname") + "'");
        sb.append(",'" + details.get("refundemailid") + "'");
        sb.append(",'" + details.get("refundcontactname") + "'");
        sb.append(",'" + details.get("billingcontactname") + "'");
        sb.append(",'" + details.get("notifycontactname") + "'");
        sb.append(",'" + details.get("splitpayment") + "'");
        sb.append(",'" + details.get("splitpaymenttype") + "'");
        sb.append(",'" + details.get("addressvalidation") + "'");
        sb.append(",'" + details.get("addressdetaildisplay") + "'");
        sb.append(",'" + details.get("flightMode") + "'");
        sb.append(",'" + details.get("template") + "'");
        sb.append(",'" + details.get("address") + "'");
        sb.append(",'" + details.get("reportingcurrency") + "'");
        sb.append(",'" + details.get("defaulttemplatetheme") + "'");
        sb.append(",'" + details.get("currenttemplatetheme") + "'");
        sb.append(",'" + details.get("autoRedirect") + "'");
        sb.append(",unix_timestamp(now())");
        sb.append(",'" + details.get("ip_whitelist_invoice") + "'");
        sb.append(",'" + details.get("address_validation_invoice") + "'");
        sb.append(",'" + details.get("ispcilogo") + "'");
        sb.append(",'" + details.get("binService") + "'");
//        sb.append(",'" + details.get("oldcheckout") + "'");

        /*sb.append(",'" + details.get("emailSent") + "'");*/

        sb.append(",'" + details.get("supportemailfortransactionmail") + "'");
        sb.append(",'" + details.get("termsUrl") + "'");
        sb.append(",'" + details.get("privacyUrl") + "'");
        sb.append(",'" + details.get("cookiesUrl") + "'");

        sb.append(",'" + details.get("checkoutInvoice") + "'");
        sb.append(",'" + details.get("bankCardLimit") + "'");
        sb.append(",'" + details.get("contact_ccmailid") + "'");
        sb.append(",'" + details.get("sales_ccemailid") + "'");
        sb.append(",'" + details.get("billing_ccemailid") + "'");
        sb.append(",'" + details.get("notify_ccemailid") + "'");
        sb.append(",'" + details.get("fraud_ccemailid") + "'");
        sb.append(",'" + details.get("chargeback_ccemailid") + "'");
        sb.append(",'" + details.get("refund_ccemailid") + "'");
        sb.append(",'" + details.get("technical_ccemailid") + "'");
        sb.append(",'" + details.get("emi_configuration") + "'");
        sb.append(",'" + details.get("exportTransactionCron") + "'");
        sb.append(",'" + details.get("protocol") + "'");
        sb.append(",'" + details.get("sms_service") + "')");



        //sb.append(",'" + details.get("login") + "'");
        Log.debug("Add newmerchant");
        Log.debug(sb.toString());
        System.out.println(sb.toString());
        PartnerAuthenticate mem = new PartnerAuthenticate();
        try
        {
            conn = Database.getConnection();
            Database.executeUpdate(sb.toString(), conn);
            Log.debug("query excuted");
            Merchants.refresh();
            String selquery = "select partnerid from partners where login=? and accid=?";
            PreparedStatement pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, (String) details.get("login"));
            pstmt.setLong(2, accid);
            Log.debug("pstmt----------->"+pstmt);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                mem.partnerid = rs.getInt("partnerid");
                mem.telno = (String) details.get("telno");
                mem.contactemails = (String) details.get("contact_emails");
                mem.isservice = false;
            }

            Functions functions = new Functions();
            if (functions.isValueNull(String.valueOf(mem.partnerid)))
            {
                String query1 = ("INSERT INTO partner_default_configuration(partnerid) VALUES (?)");
                pstmt = conn.prepareStatement(query1);
                pstmt.setInt(1, mem.partnerid);
                Log.debug("partner_default_configuration query:::"+pstmt);
                pstmt.executeUpdate();

                String query2 = ("INSERT INTO partner_configuration(partnerId) VALUES (?)");
                pstmt = conn.prepareStatement(query2);
                pstmt.setInt(1, mem.partnerid);
                Log.debug("partner_configuration query:::"+pstmt);
                pstmt.executeUpdate();
            }
        }
        catch (SystemError se)
        {
            Log.error("System error", se);
            throw new SystemError(se.getMessage());
        }
        catch (Exception e)
        {
            Log.error("Error!", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return mem;
    }
}
