package net.partner;

import com.directi.pg.*;
import com.manager.enums.ActivityLogParameters;
import com.manager.vo.ActivityTrackerVOs;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin Bari on 30-01-2020.
 */

public class UpdatePartnerDetails extends HttpServlet
{
    private static Logger log = new Logger(UpdateMemberDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        String partnername=(String)session.getAttribute("merchantid");

        PartnerFunctions partner=new PartnerFunctions();
        Functions functions=new Functions();
        ActivityTrackerVOs activityTrackerVOs = new ActivityTrackerVOs();

        String actionExecutorId=(String) session.getAttribute("merchantid");

        /*String activityrole="";
        String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
        List<String> rolelist = Arrays.asList(Roles.split("\\s*,\\s*"));
        if(rolelist.contains("subpartner"))
        {
            activityrole=ActivityLogParameters.SUBPARTNER.toString();
        }
        else if(rolelist.contains("superpartner"))
        {
            activityrole=ActivityLogParameters.SUPERPARTNER.toString();
        }
        else if(rolelist.contains("childsuperpartner"))
        {
            activityrole=ActivityLogParameters.CHILEDSUPERPARTNER.toString();
        }

        else if(rolelist.contains("partner")){
            activityrole=ActivityLogParameters.PARTNER.toString();
        }*/
        String Login=user.getAccountName();

        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        String partnerID="";
        String action = "";
        Connection conn = null;
        PreparedStatement preparedStatement1 = null, preparedStatement = null,preparedStatement2 = null;
        ResultSet rs = null;
        String partnerName=null;
        String partnerNameForWLInvoice=null;
        String sitename=null;
        String domain=null;
        String telno=null;
        String companysupportmailid=null;
        String adminmailid=null;
        String supporturl = null;
        String documentationurl = null;
        String hosturl = null;
        String companyfromaddress=null;
        String smtp_host=null;
        String smtp_port=null;
        String relaywithauth=null;
        String smtp_user=null;
        String smtp_password=null;
        String sms_user=null;
        String sms_password=null;
        String from_sms=null;
        String isipwhitelisted=null;
        String iscardencryptionenable=null;
        String splitpayment=null;
        String splitpaymenttype=null;
        String addressvalidation=null;
        String addressdetaildisplay=null;
        String autoRedirect=null;
        String flightMode=null;
        String template=null;
        String checkoutInvoice=null;
        String bankCardLimit=null;
        String emi_configuration=null;
        String exportTransactionCron=null;
        String country=null;
        String address=null;
        String responseType=null;
        String responseLength=null;
        String isflightPartner=null;
        String isTokenizationAllowed=null;
        String currency=null;
        String isRefund=null;
        String ip_whitelist_invoice=null;
        String address_validation_invoice=null;
        String defaulttemplatetheme=null;
        String ispcilogo=null;
        String binService=null;
        String oldcheckout=null;
        /*String emailSent=null;*/
        String supportemailfortransactionmail=null;
        String termsUrl=null;
        String privacyUrl=null;
        String cookiesUrl=null;
        String contact_persons=null;
        String contact_emails=null;
        String contact_ccmailid=null;
        String salescontactname=null;
        String salesemail=null;
        String sales_ccemailid=null;
        String billingemail=null;
        String billingContactName =null;
        String billingccEmail =null;
        String notifycontactname=null;
        String notifyemail=null;
        String notify_ccemailid=null;
        String fraudcontactname=null;
        String fraudemail=null;
        String fraud_ccemailid=null;
        String chargebackcontactname=null;
        String chargebackemailid=null;
        String chargeback_ccemailid=null;
        String refundcontactname=null;
        String refundemailid=null;
        String refund_ccemailid=null;
        String technicalcontactname=null;
        String technicalemailid=null;
        String technical_ccemailid=null;
        String errorList = "";
        String partnerid = "";
        String protocol ="";
        String onchangedValues ="";
        String logoheight ="";
        String logowidth ="";
        String sms_service ="";

        try
        {
            partnerID = ESAPI.validator().getValidInput("partnerID",req.getParameter("partnerID"),"Numbers",10,true);
            action = ESAPI.validator().getValidInput("action",req.getParameter("action"),"SafeString",30,true);
        }
        catch (ValidationException e)
        {
            log.error("Validation Exception",e);
        }
        try
        {
            HashMap partnerHash = null;
            String merchant_key1=null;
            conn= Database.getConnection();
            String qry="SELECT * FROM partners where partnerId=?";
            preparedStatement1= conn.prepareStatement(qry);
            preparedStatement1.setString(1,partnerID);
            partnerHash = Database.getHashMapFromResultSetForTransactionEntry(preparedStatement1.executeQuery());
            if(action!=null && !action.equals(""))
            {
                if(action.equalsIgnoreCase("view"))
                {
                    req.setAttribute("isreadonly","view");
                    req.setAttribute("partnerDetail",partnerHash);
                    RequestDispatcher rd = req.getRequestDispatcher("/updatePartnerDetails.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                else if(action.equalsIgnoreCase("modify"))
                {
                    req.setAttribute("partnerDetail",partnerHash);
                    req.setAttribute("isreadonly","modify");
                    if(req.getParameter("update")!=null)
                    {
                        req.setAttribute("partnerDetail",null);
                        if(functions.isValueNull(req.getParameter("country")))
                        {
                            String [] splitValue = req.getParameter("country").split("\\|");
                            country = splitValue[0];
                        }

                        errorList =errorList+validateUpdatePartnerParameters(req, country);
                        if (isPartnerNameForWLInvoiceUnique(req.getParameter("partnerNameForWLInvoice"),req.getParameter("partnerid")))
                        {
                            errorList = errorList + "Organisation Name  For WL Invoice already exist." + "</BR>";
                        }
                        if(errorList!=null && !errorList.equals(""))
                        {
                            req.setAttribute("error",errorList);
                            RequestDispatcher rd = req.getRequestDispatcher("/updatePartnerDetails.jsp?ctoken="+user.getCSRFToken());
                            rd.forward(req, res);
                            return;
                        }
                        else
                        {
                            partnerid = req.getParameter("partnerid");
                            partnerName=req.getParameter("partnerName");
                            partnerNameForWLInvoice=req.getParameter("partnerNameForWLInvoice");
                            sitename=req.getParameter("sitename");
                            domain=req.getParameter("domain");
                            telno=req.getParameter("telno");
                            companysupportmailid=req.getParameter("companysupportmailid");
                            adminmailid=req.getParameter("adminmailid");
                            supporturl = req.getParameter("supporturl");
                            documentationurl = req.getParameter("documentationurl");
                            hosturl = req.getParameter("hosturl");
                            companyfromaddress=req.getParameter("companyfromaddress");
                            smtp_host=req.getParameter("smtp_host");
                            smtp_port=req.getParameter("smtp_port");
                            relaywithauth=req.getParameter("relaywithauth");
                            smtp_user=req.getParameter("smtp_user");
                            smtp_password=req.getParameter("smtp_password");
                            sms_user=req.getParameter("sms_user");
                            sms_password=req.getParameter("sms_password");
                            from_sms=req.getParameter("from_sms");
                            isipwhitelisted=req.getParameter("isipwhitelisted");
                            iscardencryptionenable=req.getParameter("iscardencryptionenable");
                            splitpayment=req.getParameter("splitpayment");
                            splitpaymenttype=req.getParameter("splitpaymenttype");
                            addressvalidation=req.getParameter("addressvalidation");
                            addressdetaildisplay=req.getParameter("addressdetaildisplay");
                            autoRedirect=req.getParameter("autoRedirect");
                            flightMode=req.getParameter("flightMode");
                            template=req.getParameter("template");
                            checkoutInvoice=req.getParameter("checkoutInvoice");
                            bankCardLimit=req.getParameter("bankCardLimit");
                            emi_configuration=req.getParameter("emi_configuration");
                            exportTransactionCron=req.getParameter("exportTransactionCron");
                            country=req.getParameter("country");
                            address=req.getParameter("address");
                            responseType=req.getParameter("responsetype");
                            responseLength=req.getParameter("responselength");
                            isflightPartner=req.getParameter("isflightPartner");
                            isTokenizationAllowed=req.getParameter("isTokenizationAllowed");
                            isRefund=req.getParameter("isrefund");
                            ip_whitelist_invoice=req.getParameter("ip_whitelist_invoice");
                            address_validation_invoice=req.getParameter("address_validation_invoice");
                            defaulttemplatetheme=req.getParameter("defaulttemplatetheme");
                            ispcilogo=req.getParameter("ispcilogo");
                            binService=req.getParameter("binService");
                            oldcheckout=req.getParameter("oldcheckout");
                            /*emailSent=req.getParameter("emailSent");*/

                            supportemailfortransactionmail=req.getParameter("supportemailfortransactionmail");
                            termsUrl=req.getParameter("termsUrl");
                            privacyUrl=req.getParameter("privacyUrl");
                            cookiesUrl=req.getParameter("cookiesUrl");
                            contact_persons=req.getParameter("contact_persons");
                            contact_emails=req.getParameter("contact_emails");
                            contact_ccmailid=req.getParameter("contact_ccmailid");
                            salescontactname=req.getParameter("salescontactname");
                            salesemail=req.getParameter("salesemail");
                            sales_ccemailid=req.getParameter("sales_ccemailid");
                            billingContactName=req.getParameter("billingcontactname");
                            billingemail=req.getParameter("billingemail");
                            notifycontactname=req.getParameter("notifycontactname");
                            notifyemail=req.getParameter("notifyemail");
                            notify_ccemailid=req.getParameter("notify_ccemailid");
                            fraudcontactname=req.getParameter("fraudcontactname");
                            fraudemail=req.getParameter("fraudemail");
                            fraud_ccemailid=req.getParameter("fraud_ccemailid");
                            chargebackcontactname=req.getParameter("chargebackcontactname");
                            chargebackemailid=req.getParameter("chargebackemailid");
                            chargeback_ccemailid=req.getParameter("chargeback_ccemailid");
                            refundcontactname=req.getParameter("refundcontactname");
                            refundemailid=req.getParameter("refundemailid");
                            refund_ccemailid=req.getParameter("refund_ccemailid");
                            technicalcontactname=req.getParameter("technicalcontactname");
                            technicalemailid=req.getParameter("technicalemailid");
                            technical_ccemailid=req.getParameter("technical_ccemailid");
                            billingccEmail=req.getParameter("billing_ccemailid");
                            currency=req.getParameter("currency");
                            protocol=req.getParameter("protocol");
                            onchangedValues = req.getParameter("onchangedvalue");
                            logoheight = req.getParameter("logoheight");
                            logowidth = req.getParameter("logowidth");
                            sms_service = req.getParameter("sms_service");


                        }

                        String updateMemberSetails="UPDATE partners SET partnerName=?, partnerOrgnizationForWL_Invoice=?,contact_persons=?,contact_emails=?,country=?," +
                                "telno=?,siteurl=?,domain=?,relayWithAuth=?,supporturl=?,documentationurl=?,hosturl=?,companyadminid=?,companysupportmailid=?,salesemail=?," +
                                "billingemail=?,companyfromemail=?,supportfromemail=?,notifyemail=?,smtp_host=?,smtp_port=?,smtp_user=?," +
                                "smtp_password=?,sms_user=?,sms_password=?,from_sms=?,isIpWhitelisted=?,isCardEncryptionEnable=?,fraudemailid=?," +
                                "isFlightPartner=?,salescontactname=?,fraudcontactname=?,technicalemailid=?,technicalcontactname=?,chargebackemailid=?,chargebackcontactname=?," +
                                "refundemailid=?,refundcontactname=?,billingcontactname=?,notifycontactname=?,splitpayment=?,splitpaymenttype=?,addressvalidation=?," +
                                "flightMode=?,template=?,address=?,reporting_currency=?,default_theme=?,autoRedirect=?," +
                                "ip_whitelist_invoice=?,address_validation_invoice=?,ispcilogo=?,binService=?,support_email_for_transaction_mail=?,termsUrl=?,privacyUrl=?," +
                                "checkoutInvoice=?,bankCardLimit=?,contact_ccmailid=?,sales_ccemailid=?,billing_ccemailid=?,notify_ccemailid=?,fraud_ccemailid=?," +
                                "chargeback_ccemailid=?,refund_ccemailid=?,technical_ccemailid=?,emi_configuration=?,exportTransactionCron=?,addressdetaildisplay=?,cookiesUrl=?,responseType=?,responseLength=?,isRefund=?,oldcheckout=?,protocol=?,isTokenizationAllowed=?,logoheight=?,logowidth=?,sms_service=? where partnerId=?";
                        preparedStatement=conn.prepareStatement(updateMemberSetails);
                        preparedStatement.setString(1,partnerName);
                        preparedStatement.setString(2,partnerNameForWLInvoice);
                        preparedStatement.setString(3,contact_persons);
                        preparedStatement.setString(4,contact_emails);
                        preparedStatement.setString(5,country);
                        preparedStatement.setString(6,telno);
                        preparedStatement.setString(7,sitename);
                        preparedStatement.setString(8,domain);
                        preparedStatement.setString(9,relaywithauth);
                        preparedStatement.setString(10,supporturl);
                        preparedStatement.setString(11,documentationurl);
                        preparedStatement.setString(12,hosturl);
                        preparedStatement.setString(13,adminmailid);
                        preparedStatement.setString(14,companysupportmailid);
                        preparedStatement.setString(15,salesemail);
                        preparedStatement.setString(16,billingemail);
                        preparedStatement.setString(17,companyfromaddress);
                        preparedStatement.setString(18,companysupportmailid);
                        preparedStatement.setString(19,notifyemail);
                        preparedStatement.setString(20,smtp_host);
                        preparedStatement.setString(21,smtp_port);
                        preparedStatement.setString(22,smtp_user);
                        preparedStatement.setString(23,smtp_password);
                        preparedStatement.setString(24,sms_user);
                        preparedStatement.setString(25,sms_password);
                        preparedStatement.setString(26,from_sms);
                        preparedStatement.setString(27,isipwhitelisted);
                        preparedStatement.setString(28,iscardencryptionenable);
                        preparedStatement.setString(29,fraudemail);
                        preparedStatement.setString(30,isflightPartner);
                        preparedStatement.setString(31,salescontactname);
                        preparedStatement.setString(32,fraudcontactname);
                        preparedStatement.setString(33,technicalemailid);
                        preparedStatement.setString(34,technicalcontactname);
                        preparedStatement.setString(35,chargebackemailid);
                        preparedStatement.setString(36,chargebackcontactname);
                        preparedStatement.setString(37,refundemailid);
                        preparedStatement.setString(38,refundcontactname);
                        preparedStatement.setString(39,billingContactName);
                        preparedStatement.setString(40,notifycontactname);
                        preparedStatement.setString(41,splitpayment);
                        preparedStatement.setString(42,splitpaymenttype);
                        preparedStatement.setString(43,addressvalidation);
                        preparedStatement.setString(44,flightMode);
                        preparedStatement.setString(45,template);
                        preparedStatement.setString(46,address);
                        preparedStatement.setString(47,currency);
                        preparedStatement.setString(48,defaulttemplatetheme);
                        preparedStatement.setString(49,autoRedirect);
                        preparedStatement.setString(50,ip_whitelist_invoice);
                        preparedStatement.setString(51,address_validation_invoice);
                        preparedStatement.setString(52,ispcilogo);
                        preparedStatement.setString(53,binService);
                        preparedStatement.setString(54,supportemailfortransactionmail);
                        preparedStatement.setString(55,termsUrl);
                        preparedStatement.setString(56,privacyUrl);
                        preparedStatement.setString(57,checkoutInvoice);
                        preparedStatement.setString(58,bankCardLimit);
                        preparedStatement.setString(59,contact_ccmailid);
                        preparedStatement.setString(60,sales_ccemailid);
                        preparedStatement.setString(61,billingccEmail);
                        preparedStatement.setString(62,notify_ccemailid);
                        preparedStatement.setString(63,fraud_ccemailid);
                        preparedStatement.setString(64,chargeback_ccemailid);
                        preparedStatement.setString(65,refund_ccemailid);
                        preparedStatement.setString(66,technical_ccemailid);
                        preparedStatement.setString(67,emi_configuration);
                        preparedStatement.setString(68,exportTransactionCron);
                        preparedStatement.setString(69,addressdetaildisplay);
                        preparedStatement.setString(70,cookiesUrl);
                        preparedStatement.setString(71,responseType);
                        preparedStatement.setString(72,responseLength);
                        preparedStatement.setString(73,isRefund);
                        /*preparedStatement.setString(73,emailSent);*/
                        preparedStatement.setString(74,oldcheckout);
                        preparedStatement.setString(75,protocol);
                        preparedStatement.setString(76,isTokenizationAllowed);
                        preparedStatement.setString(77,logoheight);
                        preparedStatement.setString(78,logowidth);
                        preparedStatement.setString(79,sms_service);
                        preparedStatement.setString(80,partnerid);

                        int i=preparedStatement.executeUpdate();
                        log.debug("Creating Activity for edit Partner Master Edit");
                        String remoteAddr = Functions.getIpAddress(req);
                        int serverPort = req.getServerPort();
                        String servletPath = req.getServletPath();
                        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
                        if(functions.isValueNull(onchangedValues))
                        {
                            activityTrackerVOs.setInterface(ActivityLogParameters.PARTNER.toString());
                            activityTrackerVOs.setUser_name(Login + "-" + actionExecutorId);
                            //activityTrackerVOs.setRole(activityrole);
                            activityTrackerVOs.setRole(partner.getUserRole(user));
                            activityTrackerVOs.setAction(ActivityLogParameters.EDIT.toString());
                            activityTrackerVOs.setModule_name(ActivityLogParameters.PARTNER_MASTER.toString());
                            activityTrackerVOs.setLable_values(onchangedValues);
                            activityTrackerVOs.setDescription(ActivityLogParameters.PARTNERID.toString() + "-" + partnerid);
                            activityTrackerVOs.setIp(remoteAddr);
                            activityTrackerVOs.setHeader(header);
                            activityTrackerVOs.setPartnerId(actionExecutorId);
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

                         if(i==1)
                        {
                            req.setAttribute("message","Partner profile details has been updated successfully");
                        }
                        else
                        {
                            req.setAttribute("error","Partner profile updation has been failed");
                        }
                    }
                    RequestDispatcher rd = req.getRequestDispatcher("/updatePartnerDetails.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
            }
            else
            {
                req.setAttribute("partnerID",partnerID);
                req.setAttribute("error","Action is getting empty");
                RequestDispatcher rd = req.getRequestDispatcher("/updatePartnerDetails.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
        }
        catch (SystemError systemError)
        {

            log.error("SystemError ", systemError);
        }
        catch (SQLException e)
        {

            log.error("SQL Exception ", e);
        }
        catch (Exception e)
        {

            log.error("SQL Exception ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement1);
            Database.closeConnection(conn);
        }
    }
    public String validateUpdatePartnerParameters(HttpServletRequest request, String country)
    {
        String errormsg="";
        String EOL="<BR>";
        Functions functions = new Functions();
        /*if (!ESAPI.validator().isValidInput("username", request.getParameter("username"), "UserName", 50, false))
        {
            errormsg = errormsg + " Invalid Username." + EOL;
        }*/

        if (!ESAPI.validator().isValidInput("partnerName", request.getParameter("partnerName"), "organizationName", 50, false))
        {
            errormsg = errormsg + " Invalid Partner Organisation Name." + EOL;
        }

        if (!ESAPI.validator().isValidInput("partnerNameForWLInvoice ", request.getParameter("partnerNameForWLInvoice"), "companyName", 100, true))
        {
            errormsg = errormsg + "Invalid Organisation Name  For WL Invoice." + EOL;
        }

        if (!ESAPI.validator().isValidInput("contact_persons", request.getParameter("contact_persons"), "contactName", 100, false))
        {
            errormsg = errormsg + "Invalid Main Contact Name." + EOL;
        }
        if (!ESAPI.validator().isValidInput("contact_emails", request.getParameter("contact_emails"), "Email", 100, false))
        {
             errormsg = errormsg + "Invalid Main Contact Email ID." + EOL;
        }
        if (!ESAPI.validator().isValidInput("country", request.getParameter("country"), "Description", 50, false))
        {
            errormsg = errormsg + "Invalid Country." + EOL;
        }
       if (!ESAPI.validator().isValidInput("address", request.getParameter("address"), "Description", 50, true))
        {
            errormsg = errormsg + "Invalid Address." + EOL;
        }
        /*if (!ESAPI.validator().isValidInput("responseType", request.getParameter("responseType"), "ResponseType", 50, false))
        {
            errormsg = errormsg + "Invalid ResponseType." + EOL;
        }
        if (!ESAPI.validator().isValidInput("responseLength", request.getParameter("responseLength"), "ResponseLength", 50, false))
        {
            errormsg = errormsg + "Invalid ResponseLength." + EOL;
        }
        if (!ESAPI.validator().isValidInput("isRefund", request.getParameter("isRefund"), "Is Refund", 50, false))
        {
            errormsg = errormsg + "Invalid Is Refund." + EOL;
        }*/

        if (!ESAPI.validator().isValidInput("telno", request.getParameter("telno"), "SignupPhone", 20, false))
        {
            errormsg = errormsg + "Invalid Contact Telephone Number." + EOL;
        }
         if (!ESAPI.validator().isValidInput("sitename", request.getParameter("sitename"), "URL", 100, false) || functions.hasHTMLTags(request.getParameter("sitename")))
        {
            errormsg = errormsg + "Invalid Site URL. " + EOL;
        }
        if (!ESAPI.validator().isValidInput("domain", request.getParameter("domain"), "DomainURL", 5000, true))
        {
            errormsg = errormsg + "Invalid Domain URL. " + EOL;
        }
        if (!ESAPI.validator().isValidInput("companysupportmailid", request.getParameter("companysupportmailid"), "Email", 100, false))
        {
            errormsg = errormsg + "Invalid Support Mail ID." + EOL;
        }
        if (!ESAPI.validator().isValidInput("adminmailid", request.getParameter("adminmailid"), "Email", 100, false))
        {
            errormsg = errormsg + "Invalid Admin Mail ID." + EOL;
        }
        if (!ESAPI.validator().isValidInput("supporturl", request.getParameter("supporturl"), "DomainURL", 100, false))
        {
            errormsg = errormsg + "Invalid Support URL." + EOL;
        }
        if (!ESAPI.validator().isValidInput("documentationurl", request.getParameter("documentationurl"), "DomainURL", 100, false))
        {
            errormsg = errormsg + "Invalid Documentationurl URL." + EOL;
        }
        if (!ESAPI.validator().isValidInput("salesemail", request.getParameter("salesemail"), "Email", 100, false))
        {
            errormsg = errormsg + "Invalid Sales Email ID." + EOL;
        }
        if (!ESAPI.validator().isValidInput("billingemail", request.getParameter("billingemail"), "Email", 100, false))
        {
            errormsg = errormsg + "Invalid Billing Email ID." + EOL;
        }
        if (!ESAPI.validator().isValidInput("notifyemail", request.getParameter("notifyemail"), "Email", 100, false))
        {
            errormsg = errormsg + "Invalid Notify Email ID." + EOL;
        }
        if (!ESAPI.validator().isValidInput("companyfromaddress", request.getParameter("companyfromaddress"), "Email", 100, false))
        {
            errormsg = errormsg + "Invalid Company from address. " + EOL;
        }
        if (!ESAPI.validator().isValidInput("fraudemail", request.getParameter("fraudemail"), "Email", 100, false))
        {
            errormsg = errormsg + "Invalid Fraud Email ID." + EOL;
        }
        if (!ESAPI.validator().isValidInput("fraudcontactname", request.getParameter("fraudcontactname"), "contactName", 100, false))
        {
             errormsg = errormsg + "Invalid Fraud Contact Name." + EOL;

        }

        if (!ESAPI.validator().isValidInput("salescontactname", request.getParameter("salescontactname"), "contactName", 100, false))
        {
            errormsg = errormsg + "Invalid Sales Contact Name." + EOL;
        }

        if (!ESAPI.validator().isValidInput("notifycontactname", request.getParameter("notifycontactname"), "contactName", 100, false))
        {
            errormsg = errormsg + "Invalid Notify Contact Name." + EOL;
        }
        if (!ESAPI.validator().isValidInput("billingcontactname", request.getParameter("billingcontactname"), "contactName", 100, false))
        {
             errormsg = errormsg + "Invalid Billing Contact Name." + EOL;
        }
        if (!ESAPI.validator().isValidInput("technicalcontactname", request.getParameter("technicalcontactname"), "contactName", 100, true))
        {
             errormsg = errormsg + "Invalid Technical Contact Name." + EOL;
        }

        if (!ESAPI.validator().isValidInput("chargebackcontactname", request.getParameter("chargebackcontactname"), "contactName", 100, true))
        {
            errormsg = errormsg + "Invalid Chargeback Contact Name." + EOL;
        }
        if (!ESAPI.validator().isValidInput("refundcontactname", request.getParameter("refundcontactname"), "contactName", 100, true))
        {
            errormsg = errormsg + "Invalid Refund Contact Name." + EOL;
        }
        if (!ESAPI.validator().isValidInput("technicalemailid", request.getParameter("technicalemailid"), "Email", 100, true))
        {
            errormsg = errormsg + "Invalid Technical Email ID." + EOL;
        }
        if (!ESAPI.validator().isValidInput("chargebackemailid", request.getParameter("chargebackemailid"), "Email", 100, true))
        {
            errormsg = errormsg + "Invalid Chargeback Email ID." + EOL;
        }
        if (!ESAPI.validator().isValidInput("refundemailid", request.getParameter("refundemailid"), "Email", 100, true))
        {
            errormsg = errormsg + "Invalid Refund Email ID." + EOL;
        }
        if (!ESAPI.validator().isValidInput("contact_ccmailid", request.getParameter("contact_ccmailid"), "Email", 100, true))
        {
            errormsg = errormsg + "Invalid Main Contact Cc Email ID." + EOL;
        }

        if (!ESAPI.validator().isValidInput("sales_ccemailid", request.getParameter("sales_ccemailid"), "Email", 100, true))
        {
            errormsg = errormsg + "Invalid Sales Cc Email ID." + EOL;
        }

        if (!ESAPI.validator().isValidInput("billing_ccemailid", request.getParameter("billing_ccemailid"), "Email", 100, true))
        {
             errormsg = errormsg + "Invalid Billing Cc Email ID." + EOL;
        }

        if (!ESAPI.validator().isValidInput("notify_ccemailid", request.getParameter("notify_ccemailid"), "Email", 100, true))
        {
            errormsg = errormsg + "Invalid Notify Cc Email ID." + EOL;
        }

        if (!ESAPI.validator().isValidInput("fraud_ccemailid", request.getParameter("fraud_ccemailid"), "Email", 100, true))
        {
            errormsg = errormsg + "Invalid Fraud Cc Email ID." + EOL;
        }
        if (!ESAPI.validator().isValidInput("chargeback_ccemailid", request.getParameter("chargeback_ccemailid"), "Email", 100, true))
        {
            errormsg = errormsg + "Invalid Chargeback Cc Email ID." + EOL;
        }
        if (!ESAPI.validator().isValidInput("refund_ccemailid", request.getParameter("refund_ccemailid"), "Email", 100, true))
        {
            errormsg = errormsg + "Invalid Refund Cc Email ID." + EOL;

        }

        if (!ESAPI.validator().isValidInput("technical_ccemailid", request.getParameter("technical_ccemailid"), "Email", 100, true))
        {
            errormsg = errormsg + "Invalid Technical Cc Email ID." + EOL;
        }

        if (!ESAPI.validator().isValidInput("smtp_host", request.getParameter("smtp_host"), "smtphost", 100, false))
        {
            errormsg = errormsg + "Invalid SMTP Host." + EOL;
        }

        if (!ESAPI.validator().isValidInput("smtp_port", request.getParameter("smtp_port"), "PortNo", 5, false))
        {
            errormsg = errormsg + "Invalid SMTP Port." + EOL;
        }

        if (!ESAPI.validator().isValidInput("smtp_user", request.getParameter("smtp_user"), "SafeString", 100, false) || functions.hasHTMLTags(request.getParameter("smtp_user")))
        {
             errormsg = errormsg + "Invalid SMTP User." + EOL;
        }

        if(request.getParameter("relaywithauth").equals("Y"))
        {
            if (!ESAPI.validator().isValidInput("smtp_password", request.getParameter("smtp_password"), "SafeString", 100, false) || functions.hasHTMLTags(request.getParameter("smtp_user")))
            {
                errormsg = errormsg + "Invalid  SMTP Password." + EOL;
            }
        }else{
            if (!ESAPI.validator().isValidInput("smtp_password", request.getParameter("smtp_password"), "SafeString", 100, true) || functions.hasHTMLTags(request.getParameter("smtp_user")))
            {
                errormsg = errormsg + "Invalid  SMTP Password." + EOL;
            }
        }

        if (!ESAPI.validator().isValidInput("sms_user", request.getParameter("sms_user"), "SafeString", 100, true) || functions.hasHTMLTags(request.getParameter("smtp_user")))
        {
            errormsg = errormsg + "Invalid SMS User." + EOL;
        }

        if (!ESAPI.validator().isValidInput("sms_password", request.getParameter("sms_password"), "SafeString", 100, true) || functions.hasHTMLTags(request.getParameter("smtp_user")))
        {
            errormsg = errormsg + "Invalid SMS Password." + EOL;
        }

        if (!ESAPI.validator().isValidInput("from_sms", request.getParameter("from_sms"), "SafeString", 100, true) || functions.hasHTMLTags(request.getParameter("smtp_user")))
        {
            errormsg = errormsg + "Invalid From SMS." + EOL;
        }

        if (!ESAPI.validator().isValidInput("hosturl", request.getParameter("hosturl"), "HostURL", 255, false))
        {
             errormsg = errormsg + "Invalid Host URL." + EOL;
        }
        if (!ESAPI.validator().isValidInput("privacyUrl", request.getParameter("privacyUrl"), "DomainURL", 255, true))
        {
            errormsg = errormsg + "Invalid Privacy URL." + EOL;
        }
        if (!ESAPI.validator().isValidInput("cookiesUrl", request.getParameter("cookiesUrl"), "DomainURL", 255, true))
        {
            errormsg = errormsg + "Invalid CookiesUrl URL." + EOL;
        }
        if (!ESAPI.validator().isValidInput("TermsURL", request.getParameter("termsUrl"), "DomainURL", 255, true))
        {
            errormsg = errormsg + "Invalid TermsURL." + EOL;
        }

          return errormsg;
    }

    public boolean isPartnerNameForWLInvoiceUnique(String partnerNameForWLInvoice , String partnerid) throws SystemError
    {
        Functions functions = new Functions();
        Connection conn = null;
        if (functions.isValueNull(partnerNameForWLInvoice))
        {
            try
            {
                conn = Database.getConnection();
                StringBuffer query = new StringBuffer("SELECT partnerOrgnizationForWL_Invoice,partnerId FROM partners");
                PreparedStatement pstmt = conn.prepareStatement(query.toString());
                ResultSet rs = pstmt.executeQuery();
                while (rs.next())
                {
                    if (partnerNameForWLInvoice.equalsIgnoreCase(rs.getString("partnerOrgnizationForWL_Invoice")) && !partnerid.equals(rs.getString("partnerId")))
                        return true;
                }
            }
            catch (SystemError systemError)
            {
                log.error("SystemError while getting Partner is Unique", systemError);
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

}
