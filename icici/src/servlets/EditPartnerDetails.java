import com.directi.pg.*;
import com.manager.enums.ActivityLogParameters;
import com.manager.vo.ActivityTrackerVOs;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 2/14/14
 * Time: 3:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class EditPartnerDetails extends HttpServlet
{
    static Logger log = new Logger(EditPartnerDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in List EditPartnerDetails");
        PrintWriter out = res.getWriter();
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Hashtable hash= new Hashtable<>();
        Hashtable details=new Hashtable();
        Functions functions=new Functions();

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        boolean flag = true;
        String EOL = "<BR>";
        String errormsg = "<center><font class=\"text\" face=\"arial\"><b>"+"Following information are incorrect:-"+EOL+"</b></font></center>";
        ActivityTrackerVOs activityTrackerVOs = new ActivityTrackerVOs();

        String partnerid="";
        String name ="";
        String emailid ="";
        String country ="";
        String phno ="";
        String partnerName = "";
        String siteurl = "";
        String domain = "";
        String relayWithAuth = "";
        String protocol="";
        String processor_partnerid="";
        String com_support_id = "";
        String admin_mail = "";
        String support_url = "";
        String documentation_url = "";
        String host_url = "";
        String sales_email = "";
        String billing_email = "";
        String notify_email = "";
        String com_from = "";
        //String supp_from = "";
        String smtp_host = "";
        String smtp_port = "";
        String smtp_user = "";
        String smtp_password = "";
        String sms_user = "";
        String sms_password = "";
        String from_sms = "";
        String error = "";
        String error1 = "";
        String fraudemailid="";
        String responseType="";
        String responseLength="";
        String isflightPartner="";
        String isipwhitelist = req.getParameter("isipwhitelisted");
        String isCardEncryptionEnable = req.getParameter("iscardencryptionenable");
        String isRefund="";
        String isTokenizationAllowed="";
        String isMerchantRequiredForCardRegistration = "";
        String isAddressRequiredForTokenTransaction= "";
        String isMerchantRequiredForCardholderRegistration = "";
        String tokenValidDays = "";
        String logoName="";
        String partnerTheme="";
        String salesContactName="";
        String fraudContactName="";
        String billingContactName="";
        String notifyContactName="";
        String technicalEmailId="";
        String technicalContactName="";
        String chargebackEmailId="";
        String chargebackContactName="";
        String refundEmailId="";
        String refundContactName="";
        String isIpWhitelistForAPIs="";
        String splitpayment="";
        String splitpaymenttype="";
        String addressvalidation="";
        String addressdetaildisplay="";
        String autoRedirect="";
        String flightMode="";
        String template="";
        String checkoutInvoice="";
        String address = "";
        String currency = "";
        String ip_whitelist_invoice = "";
        String address_validation_invoice = "";
        String ispcilogo = "";
        String binService = "";
        String oldcheckout="";
        /*String emailSent = "";*/
        String isMerchantKey = "";
        String monthlyMinCommissionModule = "";
        String supportEmailForTransactionMail = "";
        String partnerShortName = "";
        String profitShareCommissionModule = "";
        String termsurl="";
        String privacyurl="";
        String cookiesurl="";

        String contact_ccEmail ="";
        String sale_ccEmail ="";
        String billing_ccEmail ="";
        String notify_ccEmail ="";
        String fraud_ccEmail ="";
        String chargeback_ccEmail ="";
        String refund_ccEmail ="";
        String technical_ccEmail ="";
        String partnerNameForWLInvoice ="";
        String bankCardLimit ="";
        String emiConfiguration ="";
        String exportTransactionCron ="";
        String emailTemplateLang= "";
        String smsService="";
        String signupPrevent="";
        String Login=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String onchangedValues = req.getParameter("onchangedvalue");
        String logoHeight = req.getParameter("logoHeight");
        String logoWidth = req.getParameter("logoWidth");

        error = validateMandatoryParameters(req);
        errormsg = errormsg + error;
        if(!error.equals(""))
            flag = false;

        error1 = validateOptionalParameters(req);
        try
        {
            if (functions.isValueNull(req.getParameter("processor_partnerid")))
            {
                if (!isProcessorPartnerIDExisted(req.getParameter("processor_partnerid")))
                {
                    error1 = error1 + "Non-existing Processor Partner ID. Please Enter a Valid Value." + EOL;
                }

                else if (!functions.isValueNull(logofound(req.getParameter("processor_partnerid"))))
                {
                    error1 = error1 + "No Logo found for Processor Partner ID. Please Upload the Logo and try again." + EOL;
                }
            }
        }
        catch (SystemError se)
        {
            log.error("System Error:::",se);
        }
        errormsg = errormsg + error1;
        if(!error1.equals(""))
            flag = false;
        else
        {
            partnerNameForWLInvoice = req.getParameter("partnerNameForWLInvoice");
            name = req.getParameter("contact_persons");
            emailid = req.getParameter("contact_emails");
            siteurl = req.getParameter("sitename");
            domain = req.getParameter("domain");
            relayWithAuth = req.getParameter("relaywithauth");
            protocol=req.getParameter("protocol");
            com_support_id = req.getParameter("supportmailid");
            admin_mail = req.getParameter("adminmailid");
            support_url = req.getParameter("supporturl");
            documentation_url = req.getParameter("documentationurl");
            host_url = req.getParameter("hosturl");
            sales_email = req.getParameter("salesemail");
            billing_email = req.getParameter("billingemail");
            notify_email = req.getParameter("notifyemail");
            com_from = req.getParameter("companyfromaddress");
            //supp_from = req.getParameter("supportfromaddress");
            smtp_host = req.getParameter("smtp_host");
            smtp_port = req.getParameter("smtp_port");
            smtp_user = req.getParameter("smtp_user");
            smtp_password = req.getParameter("smtp_password");
            sms_user = req.getParameter("sms_user");
            sms_password = req.getParameter("sms_password");
            from_sms = req.getParameter("from_sms");
            country = req.getParameter("country");
            phno = req.getParameter("telno");
            partnerid = req.getParameter("partnerid");
            //action = req.getParameter("action");
            fraudemailid=req.getParameter("fraudemail");
            responseType=req.getParameter("responsetype");
            responseLength=req.getParameter("responselength");
            isflightPartner=req.getParameter("isflightPartner");
            isRefund=req.getParameter("isRefund");
            isTokenizationAllowed=req.getParameter("isTokenizationAllowed");
            isMerchantRequiredForCardRegistration = req.getParameter("isMerchantRequiredForCardRegistration");
            isAddressRequiredForTokenTransaction = req.getParameter("isAddressRequiredForTokenTransaction");
            isMerchantRequiredForCardholderRegistration = req.getParameter("isMerchantRequiredForCardholderRegistration");
            tokenValidDays = req.getParameter("tokenValidDays");
            logoName=req.getParameter("logoName");
            partnerTheme=req.getParameter("defaulttemplatetheme");
            ispcilogo=req.getParameter("ispcilogo");
            binService=req.getParameter("binService");
            oldcheckout=req.getParameter("oldcheckout");
            /*emailSent=req.getParameter("emailSent");*/
            processor_partnerid=req.getParameter("processor_partnerid");

            isMerchantKey=req.getParameter("isMerchantKey");


            salesContactName=req.getParameter("sales_contactperson");
            fraudContactName=req.getParameter("fraud_contactperson");
            technicalEmailId=req.getParameter("technicalcontact_mailid");
            technicalContactName=req.getParameter("technicalcontact_name");
            chargebackEmailId=req.getParameter("cbcontact_mailid");
            chargebackContactName=req.getParameter("cbcontact_name");
            refundEmailId=req.getParameter("refundcontact_mailid");
            refundContactName=req.getParameter("refundcontact_name");
            billingContactName=req.getParameter("billing_contactperson");
            notifyContactName=req.getParameter("notify_contactperson");
            isIpWhitelistForAPIs = req.getParameter("isipwhitelistedforAPIs");
            splitpayment = req.getParameter("splitpayment");
            splitpaymenttype = req.getParameter("splitpaymenttype");
            addressvalidation = req.getParameter("addressvalidation");
            addressdetaildisplay = req.getParameter("addressdetaildisplay");
            autoRedirect = req.getParameter("autoRedirect");
            flightMode = req.getParameter("flightMode");
            template = req.getParameter("template");
            checkoutInvoice = req.getParameter("checkoutInvoice");
            address = req.getParameter("address");
            currency = req.getParameter("currency");
            ip_whitelist_invoice = req.getParameter("ip_whitelist_invoice");
            address_validation_invoice = req.getParameter("address_validation_invoice");
            monthlyMinCommissionModule = req.getParameter("monthly_min_commission_module");
            supportEmailForTransactionMail = req.getParameter("supportemailfortransactionmail");
            partnerShortName = req.getParameter("partner_Short_Name");
            profitShareCommissionModule = req.getParameter("profitShareCommissionModule");
            privacyurl=req.getParameter("privacyUrl");
            cookiesurl=req.getParameter("cookiesUrl");
            termsurl=req.getParameter("termsUrl");

            contact_ccEmail=req.getParameter("contact_ccmailid");
            sale_ccEmail=req.getParameter("sales_ccemailid");
            billing_ccEmail=req.getParameter("billing_ccemailid");
            notify_ccEmail=req.getParameter("notify_ccemailid");
            fraud_ccEmail=req.getParameter("fraud_ccemailid");
            chargeback_ccEmail=req.getParameter("chargeback_ccemailid");
            refund_ccEmail=req.getParameter("refund_ccemailid");
            technical_ccEmail=req.getParameter("technical_ccemailid");
            bankCardLimit =req.getParameter("bankCardLimit");
            emiConfiguration =req.getParameter("emiConfiguration");
            exportTransactionCron =req.getParameter("exportTransactionCron");
            emailTemplateLang=req.getParameter("emailTemplateLang");
            smsService=req.getParameter("sms_service");
            signupPrevent=req.getParameter("signupPrevent");

        }
        if (!ESAPI.validator().isValidInput("termsUrl", req.getParameter("termsUrl"), "DomainURL", 100, true))
        {
            flag = false;
            errormsg = errormsg + "Invalid Terms URL." + EOL;
        }
        if (!ESAPI.validator().isValidInput("privacyUrl", req.getParameter("privacyUrl"), "DomainURL", 100, true))
        {
            flag        = false;
            errormsg    = errormsg + "Invalid privacyUrl URL. " + EOL;
        }
        if (!ESAPI.validator().isValidInput("cookiesUrl", req.getParameter("cookiesUrl"), "DomainURL", 100, true))
        {
            flag        = false;
            errormsg    = errormsg + "Invalid cookiesUrl URL. " + EOL;
        }
        if (!ESAPI.validator().isValidInput("logoName", req.getParameter("logoName"), "Description", 100, true))
        {
            flag        = false;
            errormsg    = errormsg + "Invalid Logo Name. " + EOL;
        }
        if (!ESAPI.validator().isValidInput("address", req.getParameter("address"), "Description", 50, true))
        {
            flag        = false;
            errormsg    = errormsg + "Invalid Address." + EOL;
        }if (!ESAPI.validator().isValidInput("tokenValidDays", req.getParameter("tokenValidDays"), "Numbers", 50, true))
        {
        flag        = false;
        errormsg    = errormsg + "Invalid Token Valid Days." + EOL;
        }
        if (!ESAPI.validator().isValidInput("sms_user", req.getParameter("sms_user"), "SafeString", 100, true) || functions.hasHTMLTags(req.getParameter("sms_user")))
        {
            flag        = false;
            errormsg    = errormsg + "Invalid SMS User." + EOL;
        }

        if (!ESAPI.validator().isValidInput("sms_password", req.getParameter("sms_password"), "SafeString", 100, true) || functions.hasHTMLTags(req.getParameter("sms_password")))
        {
            flag        = false;
            errormsg    = errormsg + "Invalid SMS Password." + EOL;
        }
        if (!ESAPI.validator().isValidInput("from_sms", req.getParameter("from_sms"), "SafeString", 100, true) || functions.hasHTMLTags(req.getParameter("from_sms")))
        {
            flag        = false;
            errormsg    = errormsg + "Invalid From SMS." + EOL;
        }
        if (!ESAPI.validator().isValidInput("domain", req.getParameter("domain"), "DomainURL", 5000, true))
        {
            flag        = false;
            errormsg    = errormsg + "Invalid Domain URL. " + EOL;
        }
        if (!ESAPI.validator().isValidInput("smtp_host", req.getParameter("smtp_host"), "smtphost", 100, false))
        {
            flag = false;
            errormsg = errormsg + "Invalid SMTP Host." + EOL;
        }

        if (!ESAPI.validator().isValidInput("smtp_port", req.getParameter("smtp_port"), "PortNo", 5, false))
        {
            flag        = false;
            errormsg    = errormsg + "Invalid SMTP Port." + EOL;
        }
        if (!ESAPI.validator().isValidInput("documentationurl", req.getParameter("documentationurl"), "DomainURL", 500, false))
        {
            flag = false;
            errormsg = errormsg + "Invalid Documentation URL." + EOL;
        }
        if (!ESAPI.validator().isValidInput("logoHeight", req.getParameter("logoHeight"), "Numbers", 3, true))
        {
            flag = false;
            errormsg = errormsg + "Invalid Logo Height." + EOL;
        }
        if (!ESAPI.validator().isValidInput("logoWidth", req.getParameter("logoWidth"), "Numbers", 3, true))
        {
            flag = false;
            errormsg = errormsg + "Invalid Logo Width " + EOL;
        }

        if (flag)
        {   StringBuffer errorMessage = new StringBuffer();
            Connection conn = null;
            //String query = "UPDATE partners SET siteurl=?,contact_emails=?,contact_persons=?,companysupportmailid=?,companyadminid=?,supporturl=?,salesemail=?,billingemail=?,notifyemail=?,companyfromemail=?,supportfromemail=?,smtp_host=?,smtp_port=?,smtp_user=?,smtp_password=?,country=?,telno=?,isIpWhitelisted=?,fraudemailid=?,hosturl=?,responseType=?,responseLength=?,isFlightPartner=?,isRefund=?,isTokenizationAllowed=?,isMerchantRequiredForCardRegistration=?,isAddressRequiredForTokenTransaction=?,isMerchantRequiredForCardholderRegistration=?,tokenValidDays=?,logoName=?,isCardEncryptionEnable=?,salescontactname=?,fraudcontactname=?,technicalemailid=?,technicalcontactname=?,chargebackemailid=?,chargebackcontactname=?,refundemailid=?,refundcontactname=?,billingcontactname=?,notifycontactname=?,is_rest_whitelisted=?, splitpayment=?,splitpaymenttype=?,addressvalidation=?,addressdetaildisplay=?,flightMode=?,template=?,address=?,reporting_currency=?,autoRedirect=?,sms_user=?,sms_password=?,from_sms=?,ip_whitelist_invoice=?,address_validation_invoice=?,default_theme=?,domain=?,relayWithAuth=?,ispcilogo=?,binService=?,monthly_min_commission_module=?,support_email_for_transaction_mail=?,partner_short_name=?,profit_share_commission_module=?,termsUrl=?,privacyUrl=?,contact_ccmailid=?,sales_ccemailid=?,billing_ccemailid=?,notify_ccemailid=?,fraud_ccemailid=?,chargeback_ccemailid=?,refund_ccemailid=?,technical_ccemailid=?, partnerOrgnizationForWL_Invoice=?,checkoutInvoice=?,bankCardLimit=?,emi_configuration=?,exportTransactionCron=?,isMerchantKey=? WHERE partnerId=?";
            String query = "UPDATE partners SET siteurl=?,contact_emails=?,contact_persons=?,companysupportmailid=?,companyadminid=?,supporturl=?,documentationurl=?,salesemail=?,billingemail=?,notifyemail=?,companyfromemail=?,smtp_host=?,smtp_port=?,smtp_user=?,smtp_password=?,country=?,telno=?,isIpWhitelisted=?,fraudemailid=?,hosturl=?,responseType=?,responseLength=?,isFlightPartner=?,isRefund=?,isTokenizationAllowed=?,isMerchantRequiredForCardRegistration=?,isAddressRequiredForTokenTransaction=?,isMerchantRequiredForCardholderRegistration=?,tokenValidDays=?,logoName=?,isCardEncryptionEnable=?,salescontactname=?,fraudcontactname=?,technicalemailid=?,technicalcontactname=?,chargebackemailid=?,chargebackcontactname=?,refundemailid=?,refundcontactname=?,billingcontactname=?,notifycontactname=?,is_rest_whitelisted=?, splitpayment=?,splitpaymenttype=?,addressvalidation=?,addressdetaildisplay=?,flightMode=?,template=?,address=?,reporting_currency=?,autoRedirect=?,sms_user=?,sms_password=?,from_sms=?,ip_whitelist_invoice=?,address_validation_invoice=?,default_theme=?,domain=?,relayWithAuth=?,ispcilogo=?,binService=?,monthly_min_commission_module=?,support_email_for_transaction_mail=?,partner_short_name=?,profit_share_commission_module=?,termsUrl=?,privacyUrl=?,contact_ccmailid=?,sales_ccemailid=?,billing_ccemailid=?,notify_ccemailid=?,fraud_ccemailid=?,chargeback_ccemailid=?,refund_ccemailid=?,technical_ccemailid=?, partnerOrgnizationForWL_Invoice=?,checkoutInvoice=?,bankCardLimit=?,exportTransactionCron=?,isMerchantKey=?,cookiesUrl=?,oldcheckout=?,protocol=?,processor_partnerid=?,emailTemplateLang=?,sms_service=?,signupPrevent=?,logoheight=?,logowidth=? WHERE partnerId=?";
            PreparedStatement ps = null;
            try
            {
                conn = Database.getConnection();
                ps = conn.prepareStatement(query);
                //ps.setString(1,partnerName);
                ps.setString(1,siteurl);
                ps.setString(2,emailid);
                ps.setString(3,name);
                ps.setString(4,com_support_id);
                ps.setString(5,admin_mail);
                ps.setString(6,support_url);
                ps.setString(7,documentation_url);
                ps.setString(8,sales_email);
                ps.setString(9,billing_email);
                ps.setString(10,notify_email);
                ps.setString(11,com_from);
                //ps.setString(11,supp_from);
                ps.setString(12,smtp_host);
                ps.setString(13,smtp_port);
                ps.setString(14,smtp_user);
                ps.setString(15,smtp_password);
                ps.setString(16,country);
                ps.setString(17,phno);
                ps.setString(18,isipwhitelist);
                ps.setString(19,fraudemailid);
                ps.setString(20,host_url);
                ps.setString(21,responseType);
                ps.setString(22,responseLength);
                ps.setString(23,isflightPartner);
                ps.setString(24,isRefund);
                ps.setString(25,isTokenizationAllowed);
                ps.setString(26,isMerchantRequiredForCardRegistration);
                ps.setString(27,isAddressRequiredForTokenTransaction);
                ps.setString(28,isMerchantRequiredForCardholderRegistration);
                ps.setString(29,tokenValidDays);
                ps.setString(30,logoName);
                ps.setString(31,isCardEncryptionEnable);
                ps.setString(32,salesContactName);
                ps.setString(33,fraudContactName);
                ps.setString(34,technicalEmailId);
                ps.setString(35,technicalContactName);
                ps.setString(36,chargebackEmailId);
                ps.setString(37,chargebackContactName);
                ps.setString(38,refundEmailId);
                ps.setString(39,refundContactName);
                ps.setString(40,billingContactName);
                ps.setString(41,notifyContactName);
                ps.setString(42,isIpWhitelistForAPIs);
                ps.setString(43,splitpayment);
                ps.setString(44,splitpaymenttype);
                ps.setString(45,addressvalidation);
                ps.setString(46,addressdetaildisplay);
                ps.setString(47,flightMode);
                ps.setString(48,template);
                ps.setString(49, address);
                ps.setString(50, currency);
                ps.setString(51, autoRedirect);
                ps.setString(52, sms_user);
                ps.setString(53, sms_password);
                ps.setString(54, from_sms);
                ps.setString(55, ip_whitelist_invoice);
                ps.setString(56, address_validation_invoice);
                ps.setString(57,partnerTheme);
                ps.setString(58,domain);
                ps.setString(59,relayWithAuth);
                ps.setString(60,ispcilogo);
                ps.setString(61,binService);
                ps.setString(62, monthlyMinCommissionModule);
                ps.setString(63, supportEmailForTransactionMail);
                ps.setString(64, partnerShortName);
                ps.setString(65, profitShareCommissionModule);
                ps.setString(66, termsurl);
                ps.setString(67, privacyurl);
                ps.setString(68, contact_ccEmail);
                ps.setString(69, sale_ccEmail);
                ps.setString(70, billing_ccEmail);
                ps.setString(71, notify_ccEmail);
                ps.setString(72, fraud_ccEmail);
                ps.setString(73, chargeback_ccEmail);
                ps.setString(74, refund_ccEmail);
                ps.setString(75, technical_ccEmail);
                ps.setString(76, partnerNameForWLInvoice);
                ps.setString(77, checkoutInvoice);
                ps.setString(78, bankCardLimit);
                //ps.setString(78, emiConfiguration);
                ps.setString(79, exportTransactionCron);
                ps.setString(80, isMerchantKey);
                ps.setString(81, cookiesurl);
                /*ps.setString(81, emailSent);*/
                ps.setString(82, oldcheckout);
                ps.setString(83, protocol);
                ps.setString(84, processor_partnerid);
                ps.setString(85, emailTemplateLang);
                ps.setString(86, smsService);
                ps.setString(87, signupPrevent);
                ps.setString(88, logoHeight);
                ps.setString(89, logoWidth);
                ps.setString(90, partnerid);

                log.debug("Creating Activity for Partner Master");
                String remoteAddr = Functions.getIpAddress(req);
                int serverPort = req.getServerPort();
                String servletPath = req.getServletPath();
                String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
                if(functions.isValueNull(onchangedValues))
                {
                    activityTrackerVOs.setInterface(ActivityLogParameters.ADMIN.toString());
                    activityTrackerVOs.setUser_name(Login + "-" + actionExecutorId);
                    activityTrackerVOs.setRole(ActivityLogParameters.ADMIN.toString());
                    activityTrackerVOs.setAction(ActivityLogParameters.EDIT.toString());
                    activityTrackerVOs.setModule_name(ActivityLogParameters.PARTNER_MASTER.toString());
                    activityTrackerVOs.setLable_values(onchangedValues);
                    activityTrackerVOs.setDescription(ActivityLogParameters.PARTNERID.toString() + "-" + partnerid);
                    activityTrackerVOs.setIp(remoteAddr);
                    activityTrackerVOs.setHeader(header);
                    try
                    {
                        AsyncActivityTracker asyncActivityTracker = AsyncActivityTracker.getInstance();
                        asyncActivityTracker.asyncActivity(activityTrackerVOs);
                    }
                    catch (Exception e)
                    {
                        log.error("Exception while AsyncActivityLog::::", e);
                    }
                }
                int i =ps.executeUpdate();
                if(i>=0)
                {
                    Merchants.refresh();
                    errorMessage.append("<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Record Updated Successful"+"</b>"+"</center>"+"</font>"+"<BR><BR>");
                }
                else
                {
                    errorMessage.append("<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Update Failed"+"</b>"+"</center>"+"</font>"+"<BR><BR>");
                }
            }
            catch (SystemError systemError)
            {
                log.error("Sql Exception :::::",systemError);
                errorMessage.append("Internal error while processing your request");
            }
            catch (SQLException e)
            {
                log.error("Exception while updating template fees : " + e.getMessage());
                errorMessage.append("Internal error while processing your request");
            }
            finally
            {
                Database.closePreparedStatement(ps);
                Database.closeConnection(conn);
            }
            req.setAttribute("message",errorMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/partnerInterface.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        else
        {
            try
            {
                hash.put("partnerName", req.getParameter("company_name"));
                hash.put("login", req.getParameter("user_name"));
                hash.put("partnerOrgnizationForWL_Invoice", req.getParameter("partnerNameForWLInvoice"));
                hash.put("contact_persons", req.getParameter("contact_persons"));
                hash.put("contact_emails", req.getParameter("contact_emails"));
                hash.put("siteurl", req.getParameter("sitename"));
                hash.put("domain", req.getParameter("domain"));
                hash.put("relayWithAuth", req.getParameter("relaywithauth"));
                hash.put("companysupportmailid", req.getParameter("supportmailid"));
                hash.put("companyadminid", req.getParameter("adminmailid"));
                hash.put("supporturl", req.getParameter("supporturl"));
                hash.put("documentationurl", req.getParameter("documentationurl"));
                hash.put("hosturl", req.getParameter("hosturl"));
                hash.put("salesemail", req.getParameter("salesemail"));
                hash.put("billingemail", req.getParameter("billingemail"));
                hash.put("notifyemail", req.getParameter("notifyemail"));
                hash.put("companyfromemail", req.getParameter("companyfromaddress"));
               // hash.put("supportfromemail", req.getParameter("supportfromaddress"));
                hash.put("smtp_host", req.getParameter("smtp_host"));
                hash.put("smtp_port", req.getParameter("smtp_port"));
                hash.put("smtp_user", req.getParameter("smtp_user"));
                hash.put("smtp_password", req.getParameter("smtp_password"));
                hash.put("sms_user", req.getParameter("sms_user"));
                hash.put("sms_password", req.getParameter("sms_password"));
                hash.put("from_sms", req.getParameter("from_sms"));
                hash.put("country", req.getParameter("country"));
                hash.put("telno", req.getParameter("telno"));
                hash.put("partnerid", req.getParameter("partnerid"));
                hash.put("isIpWhitelisted", isipwhitelist);
                hash.put("isCardEncryptionEnable", isCardEncryptionEnable);
                //action = req.getParameter("action");
                hash.put("fraudemailid", req.getParameter("fraudemail"));
                hash.put("responseType", req.getParameter("responsetype"));
                hash.put("responseLength", req.getParameter("responselength"));
                hash.put("isFlightPartner", req.getParameter("isflightPartner"));
                hash.put("isRefund", req.getParameter("isRefund"));
                hash.put("isTokenizationAllowed", req.getParameter("isTokenizationAllowed"));
                hash.put("isMerchantRequiredForCardRegistration", req.getParameter("isMerchantRequiredForCardRegistration"));
                hash.put("isAddressRequiredForTokenTransaction", req.getParameter("isAddressRequiredForTokenTransaction"));
                hash.put("isMerchantRequiredForCardholderRegistration", req.getParameter("isMerchantRequiredForCardholderRegistration"));
                hash.put("tokenValidDays", req.getParameter("tokenValidDays"));
                hash.put("logoName", req.getParameter("logoName"));
                hash.put("default_theme", req.getParameter("defaulttemplatetheme"));
                hash.put("ispcilogo", req.getParameter("ispcilogo"));
                hash.put("binService", req.getParameter("binService"));
                hash.put("oldcheckout", req.getParameter("oldcheckout"));
                hash.put("protocol", req.getParameter("protocol"));
                hash.put("processor_partnerid", req.getParameter("processor_partnerid"));

                /*hash.put("emailSent", req.getParameter("emailSent"));*/

                hash.put("isMerchantKey", req.getParameter("isMerchantKey"));


                hash.put("salescontactname", req.getParameter("sales_contactperson"));
                hash.put("fraudcontactname", req.getParameter("fraud_contactperson"));
                hash.put("technicalemailid", req.getParameter("technicalcontact_mailid"));
                hash.put("technicalcontactname", req.getParameter("technicalcontact_name"));
                hash.put("chargebackemailid", req.getParameter("cbcontact_mailid"));
                hash.put("chargebackcontactname", req.getParameter("cbcontact_name"));
                hash.put("refundemailid", req.getParameter("refundcontact_mailid"));
                hash.put("refundcontactname", req.getParameter("refundcontact_name"));
                hash.put("billingcontactname", req.getParameter("billing_contactperson"));
                hash.put("notifycontactname", req.getParameter("notify_contactperson"));
                hash.put("is_rest_whitelisted", req.getParameter("isipwhitelistedforAPIs"));
                hash.put("splitpayment", req.getParameter("splitpayment"));
                hash.put("splitpaymenttype", req.getParameter("splitpaymenttype"));
                hash.put("addressvalidation", req.getParameter("addressvalidation"));
                hash.put("addressdetaildisplay", req.getParameter("addressdetaildisplay"));
                hash.put("autoRedirect", req.getParameter("autoRedirect"));
                hash.put("flightMode", req.getParameter("flightMode"));
                hash.put("template", req.getParameter("template"));
                hash.put("checkoutInvoice", req.getParameter("checkoutInvoice"));
                hash.put("address", req.getParameter("address"));
                hash.put("reporting_currency", req.getParameter("currency"));
                hash.put("ip_whitelist_invoice", req.getParameter("ip_whitelist_invoice"));
                hash.put("address_validation_invoice", req.getParameter("address_validation_invoice"));
                hash.put("monthly_min_commission_module", req.getParameter("monthly_min_commission_module"));
                hash.put("support_email_for_transaction_mail", req.getParameter("supportemailfortransactionmail"));
                hash.put("partner_Short_Name", req.getParameter("partner_Short_Name"));
                hash.put("profit_share_commission_module", req.getParameter("profitShareCommissionModule"));
                hash.put("privacyUrl", req.getParameter("privacyUrl"));
                hash.put("cookiesUrl", req.getParameter("cookiesUrl"));
                hash.put("termsUrl", req.getParameter("termsUrl"));
                hash.put("partnerName",req.getParameter("company_name"));

                hash.put("contact_ccmailid", req.getParameter("contact_ccmailid"));
                hash.put("sales_ccemailid", req.getParameter("sales_ccemailid"));
                hash.put("billing_ccemailid", req.getParameter("billing_ccemailid"));
                hash.put("notify_ccemailid", req.getParameter("notify_ccemailid"));
                hash.put("fraud_ccemailid", req.getParameter("fraud_ccemailid"));
                hash.put("chargeback_ccemailid", req.getParameter("chargeback_ccemailid"));
                hash.put("refund_ccemailid", req.getParameter("refund_ccemailid"));
                hash.put("technical_ccemailid", req.getParameter("technical_ccemailid"));
                hash.put("bankCardLimit", req.getParameter("bankCardLimit"));
                hash.put("emailTemplateLang", req.getParameter("emailTemplateLang"));
                hash.put("smsService", req.getParameter("sms_service"));
                hash.put("signupPrevent", req.getParameter("signupPrevent"));
                hash.put("logoHeight", req.getParameter("logoHeight"));
                hash.put("logoWidth", req.getParameter("logoWidth"));
                details.put("1",hash);
            }
            catch (Exception e)
            {
                log.error("Catch Exception..",e);
            }
                req.setAttribute("partnerdetails1", details);
                req.setAttribute("message", errormsg);
                RequestDispatcher rd = req.getRequestDispatcher("/servlet/ViewPartnerDetails?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
        }
    }
    public boolean logoValidation(String logoname)
    {
        boolean isValidLogoType=false;
        String filename="";
        int pos= logoname.lastIndexOf(".");

        if (pos != -1)
        {
            filename = logoname.substring(pos + 1);
        }
        else
        {
            filename = logoname;
        }
        if (filename.equalsIgnoreCase("jpg") || filename.equalsIgnoreCase("jpeg"))
        {
            return true;
        }
        return isValidLogoType;
    }
    public boolean isProcessorPartnerIDExisted(String partnerid) throws SystemError
    {
        Functions functions = new Functions();
        Connection conn = null;
        if (functions.isValueNull(partnerid))
        {
            try
            {
                conn = Database.getConnection();
                StringBuffer query = new StringBuffer("SELECT partnerId FROM partners");
                PreparedStatement pstmt = conn.prepareStatement(query.toString());
                ResultSet rs = pstmt.executeQuery();
                while (rs.next())
                {
                    if (partnerid.equalsIgnoreCase(rs.getString("partnerId")))
                        return true;
                }
            }
            catch (SystemError systemError)
            {
                log.error("SystemError while getting Processor Partner ID", systemError);
            }
            catch (SQLException e)
            {
                log.error(" Sql Exception", e);
            }
            finally
            {
                Database.closeConnection(conn);
            }
        }
        return false;
    }

    public String logofound(String partnerId)
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String logofound="";
        try
        {

            con = Database.getRDBConnection();
            String qry = "select logoName from partners where partnerId=?";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, partnerId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                logofound = rs.getString("logoName");
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
        return logofound;
    }

    private String validateMandatoryParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.CONTACT_PERSON);
        inputFieldsListOptional.add(InputFields.CONTACT_EMAIL);
        inputFieldsListOptional.add(InputFields.COUNTRY);
        inputFieldsListOptional.add(InputFields.TELNO);
        inputFieldsListOptional.add(InputFields.SITENAME);
        inputFieldsListOptional.add(InputFields.SUPPORT_MAIL_ID);
        inputFieldsListOptional.add(InputFields.ADMIN_MAIL_ID);
        inputFieldsListOptional.add(InputFields.SUPPORT_URL);
        //inputFieldsListOptional.add(InputFields.DOCUMENTATION_URL);
        //inputFieldsListOptional.add(InputFields.HOST_URL);
        inputFieldsListOptional.add(InputFields.SALES_EMAIL);
        inputFieldsListOptional.add(InputFields.BILLING_EMAIL);
        inputFieldsListOptional.add(InputFields.NOTIFY_EMAIL);
        inputFieldsListOptional.add(InputFields.COMPANY_FROM_ADDRESS);
        //inputFieldsListOptional.add(InputFields.SUPPORT_FROM_ADDRESS);
        //inputFieldsListOptional.add(InputFields.SMTP_HOST);
        //inputFieldsListOptional.add(InputFields.SMTP_PORT);
        inputFieldsListOptional.add(InputFields.SMTP_USER);
        inputFieldsListOptional.add(InputFields.PARTNERID);
        inputFieldsListOptional.add(InputFields.ACTION);
        inputFieldsListOptional.add(InputFields.FRAUDEMAIL);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,false);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }

    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.SALES_CONTACTPERSON);
        inputFieldsListOptional.add(InputFields.BILLING_CONTACTPERSON);
        inputFieldsListOptional.add(InputFields.NOTIFY_CONTACTPERSON);
        inputFieldsListOptional.add(InputFields.FRAUD_CONTACTPERSON);
        inputFieldsListOptional.add(InputFields.TECHCONTACT_NAME);
        inputFieldsListOptional.add(InputFields.TECHCONTACT_MAIL);
        inputFieldsListOptional.add(InputFields.CBCONTACT_NAME);
        inputFieldsListOptional.add(InputFields.CBCONTACT_MAIL);
        inputFieldsListOptional.add(InputFields.REFUNDCONTACT_NAME);
        inputFieldsListOptional.add(InputFields.REFUNDCONTACT_MAIL);
        //inputFieldsListOptional.add(InputFields.SMS_USER);
        //inputFieldsListOptional.add(InputFields.SMS_PASSWORD);
        //inputFieldsListOptional.add(InputFields.FROM_SMS);
        //inputFieldsListOptional.add(InputFields.ADDRESS);
        inputFieldsListOptional.add(InputFields.CONTACT_CCEMAIL);
        inputFieldsListOptional.add(InputFields.SALES_CCEMAIL);
        inputFieldsListOptional.add(InputFields.BILLING_CCEMAIL);
        inputFieldsListOptional.add(InputFields.NOTIFY_CCEMAIL);
        inputFieldsListOptional.add(InputFields.FRAUD_CCEMAIL);
        inputFieldsListOptional.add(InputFields.CB_CCEMAIL);
        inputFieldsListOptional.add(InputFields.REFUND_CCEMAIL);
        inputFieldsListOptional.add(InputFields.TECH_CCEMAIL);
        inputFieldsListOptional.add(InputFields.PRIVACY_URL);
        inputFieldsListOptional.add(InputFields.WL_INVOICE_COMPANY_NAME);
        //inputFieldsListOptional.add(InputFields.PROCESSOR_PARTNERID);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }
}