import com.directi.pg.*;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.ResourceBundle;

//import java.io.PrintWriter;


public class UpdateMerchant extends HttpServlet
{
    private static Logger Log = new Logger(UpdateMerchant.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        response.setContentType("text/html");

       // PrintWriter out = response.getWriter();
        StringBuffer errormsg   = new StringBuffer("");
        //String data = request.getParameter("data");
        String EOL              = "  ";
        String redirectpage     = null;
        Functions functions     = new Functions();
        Merchants merchants     = new Merchants();
        HttpSession session     = request.getSession();
        if (!merchants.isLoggedIn(session))
        {
            response.sendRedirect("/merchant/logout.jsp");
            return;
        }
        User user                       =  (User)session.getAttribute("ESAPIUserSessionKey");
        Hashtable detailhash            = new Hashtable();
        ResourceBundle rb1              = null;
        String language_property1       = (String)session.getAttribute("language_property");
        rb1                             = LoadProperties.getProperty(language_property1);
        String isSendEmailNotification  = "N";

        String UpdateMerchant_company_errormsg      = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_company_errormsg"))?rb1.getString("UpdateMerchant_company_errormsg"): "Invalid Company Name";
        String UpdateMerchant_email_errormsg        = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_email_errormsg"))?rb1.getString("UpdateMerchant_email_errormsg"): "Invalid Notification email address.";
        String UpdateMerchant_maincontact_errormsg  = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_maincontact_errormsg"))?rb1.getString("UpdateMerchant_maincontact_errormsg"): "Invalid Main Contact.";
        String UpdateMerchant_mainemail_errormsg    = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_mainemail_errormsg"))?rb1.getString("UpdateMerchant_mainemail_errormsg"): "Invalid Main Contact Email.";
        String UpdateMerchant_address_errormsg      = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_address_errormsg"))?rb1.getString("UpdateMerchant_address_errormsg"): "Invalid Address.";
        String UpdateMerchant_city_errormsg         = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_city_errormsg"))?rb1.getString("UpdateMerchant_city_errormsg"): "Invalid City.";
        String UpdateMerchant_state_errormsg        = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_state_errormsg"))?rb1.getString("UpdateMerchant_state_errormsg"): "Invalid State.";
        String UpdateMerchant_pincode_errormsg      = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_pincode_errormsg"))?rb1.getString("UpdateMerchant_pincode_errormsg"): "Invalid Pin Code.";
        String UpdateMerchant_validcountry_errormsg = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_validcountry_errormsg"))?rb1.getString("UpdateMerchant_validcountry_errormsg"): "Please enter Valid Country Name.";
        String UpdateMerchant_timezone_errormsg     = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_timezone_errormsg"))?rb1.getString("UpdateMerchant_timezone_errormsg"): "Please enter Valid timezone.";
        String UpdateMerchant_telephone_errormsg    = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_telephone_errormsg"))?rb1.getString("UpdateMerchant_telephone_errormsg"): "Please enter valid telephone number.";
        String UpdateMerchant_faxno_errormsg        = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_faxno_errormsg"))?rb1.getString("UpdateMerchant_faxno_errormsg"): "Invalid Contact Fax Number.";
        String UpdateMerchant_brand_errormsg        = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_brand_errormsg"))?rb1.getString("UpdateMerchant_brand_errormsg"): "Invalid Brand Name.";
        String UpdateMerchant_siteurl_errormsg      = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_siteurl_errormsg"))?rb1.getString("UpdateMerchant_siteurl_errormsg"): "Invalid Site URL.";
        String UpdateMerchant_fraud_errormsg        = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_fraud_errormsg"))?rb1.getString("UpdateMerchant_fraud_errormsg"): "Invalid Fraud Alert Score.";
        String UpdateMerchant_reversal_errormsg     = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_reversal_errormsg"))?rb1.getString("UpdateMerchant_reversal_errormsg"): "Invalid Fraud Auto Reversal Score.";
        String UpdateMerchant_contactcc_errormsg    = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_contactcc_errormsg"))?rb1.getString("UpdateMerchant_contactcc_errormsg"): "Invalid main contat cc mailid.";
        String UpdateMerchant_maincontact1_errormsg = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_maincontact1_errormsg"))?rb1.getString("UpdateMerchant_maincontact1_errormsg"): "Invalid Main Contact Phone.";
        String UpdateMerchant_support_errormsg      = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_support_errormsg"))?rb1.getString("UpdateMerchant_support_errormsg"): "Invalid Customer Support name.";
        String UpdateMerchant_supportemail_errormsg = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_supportemail_errormsg"))?rb1.getString("UpdateMerchant_supportemail_errormsg"): "Invalid Customer Support Email.";
        String UpdateMerchant_mailid_errormsg       = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_mailid_errormsg"))?rb1.getString("UpdateMerchant_mailid_errormsg"): "Invalid support contat cc mailid.";
        String UpdateMerchant_supportphone_errormsg = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_supportphone_errormsg"))?rb1.getString("UpdateMerchant_supportphone_errormsg"): "Invalid Support Phone.";
        String UpdateMerchant_chargeback_errormsg   = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_chargeback_errormsg"))?rb1.getString("UpdateMerchant_chargeback_errormsg"): "Invalid Chargeback Contact Name.";
        String UpdateMerchant_chargeemail_errormsg  = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_chargeemail_errormsg"))?rb1.getString("UpdateMerchant_chargeemail_errormsg"): "Invalid Chargeback Email .";
        String UpdateMerchant_chargebackcc_errormsg = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_chargebackcc_errormsg"))?rb1.getString("UpdateMerchant_chargebackcc_errormsg"): "Invalid Chrgeback Cc mailid .";
        String UpdateMerchant_contactphone_errormsg = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_contactphone_errormsg"))?rb1.getString("UpdateMerchant_contactphone_errormsg"): "Invalid Main Contact Phone.";
        String UpdateMerchant_refund_errormsg       = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_refund_errormsg"))?rb1.getString("UpdateMerchant_refund_errormsg"): "Invalid Refund Contact Name.";
        String UpdateMerchant_refundcontact_errormsg        = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_refundcontact_errormsg"))?rb1.getString("UpdateMerchant_refundcontact_errormsg"): "Invalid Refund Contact Email .";
        String UpdateMerchant_refundcc_errormsg             = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_refundcc_errormsg"))?rb1.getString("UpdateMerchant_refundcc_errormsg"): "Invalid Refund Contact Cc mailid .";
        String UpdateMerchant_refundcontactphone_errormsg   = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_refundcontactphone_errormsg"))?rb1.getString("UpdateMerchant_refundcontactphone_errormsg"): "Invalid Refund Contact Phone.";
        String UpdateMerchant_sales_errormsg                = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_sales_errormsg"))?rb1.getString("UpdateMerchant_sales_errormsg"): "Invalid Sales Contact Name.";
        String UpdateMerchant_salescontact_errormsg         = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_salescontact_errormsg"))?rb1.getString("UpdateMerchant_salescontact_errormsg"): "Invalid Sales Contact Email .";
        String UpdateMerchant_ccmailid_errormsg             = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_ccmailid_errormsg"))?rb1.getString("UpdateMerchant_ccmailid_errormsg"): "Invalid Sales Contact Cc mailid .";
        String UpdateMerchant_contactphone1_errormsg        = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_contactphone1_errormsg"))?rb1.getString("UpdateMerchant_contactphone1_errormsg"): "Invalid Sales Contact Phone.";
        String UpdateMerchant_fraudcontact_errormsg         = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_fraudcontact_errormsg"))?rb1.getString("UpdateMerchant_fraudcontact_errormsg"): "Invalid Sales Contact Phone.";
        String UpdateMerchant_fraudccmailid_errormsg        = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_fraudccmailid_errormsg"))?rb1.getString("UpdateMerchant_fraudccmailid_errormsg"): "Invalid Fraud Contact Cc mailid .";
        String UpdateMerchant_fraudphone_errormsg           = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_fraudphone_errormsg"))?rb1.getString("UpdateMerchant_fraudphone_errormsg"): "Invalid fraud contact phone.";
        String UpdateMerchant_technical_errormsg            = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_technical_errormsg"))?rb1.getString("UpdateMerchant_technical_errormsg"): "Invalid Technical Contact Name.";
        String UpdateMerchant_technicalemail_errormsg       = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_technicalemail_errormsg"))?rb1.getString("UpdateMerchant_technicalemail_errormsg"): "Invalid Technical Contact Email .";
        String UpdateMerchant_mailid1_errormsg              = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_mailid1_errormsg"))?rb1.getString("UpdateMerchant_mailid1_errormsg"): "Invalid Technical Contact Cc mailid .";
        String UpdateMerchant_technicalcontact_errormsg     = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_technicalcontact_errormsg"))?rb1.getString("UpdateMerchant_technicalcontact_errormsg"): "Invalid Technical Contact Phone.";
        String UpdateMerchant_billingcontact_errormsg       = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_billingcontact_errormsg"))?rb1.getString("UpdateMerchant_billingcontact_errormsg"): "Invalid Billing Contact Name.";
        String UpdateMerchant_billingemail_errormsg         = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_billingemail_errormsg"))?rb1.getString("UpdateMerchant_billingemail_errormsg"): "Invalid Billing Contact Email .";
        String UpdateMerchant_billingcc_errormsg            = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_billingcc_errormsg"))?rb1.getString("UpdateMerchant_billingcc_errormsg"): "Invalid Billing Contact Cc .";
        String UpdateMerchant_billingphone_errormsg         = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_billingphone_errormsg"))?rb1.getString("UpdateMerchant_billingphone_errormsg"): "Invalid Billing Contact Phone.";
        String UpdateMerchant_fraudemail_errormsg           = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_fraudemail_errormsg"))?rb1.getString("UpdateMerchant_fraudemail_errormsg"): "Invalid Fraud Contact Email .";
        String UpdateMerchant_update_errormsg               = !functions.isEmptyOrNull(rb1.getString("UpdateMerchant_update_errormsg"))?rb1.getString("UpdateMerchant_update_errormsg"): "Update Successful";

        try
        {

            boolean flag = true;


            detailhash.put("notifyemail", (String) request.getParameter("notifyemail"));
            detailhash.put("mitigationmail", (String) request.getParameter("mitigationmail"));
            detailhash.put("hralertproof", (String) request.getParameter("hralertproof"));
            detailhash.put("datamismatchproof", (String) request.getParameter("datamismatchproof"));

            if (!ESAPI.validator().isValidInput("Company Name", (String) request.getParameter("company_name"), "companyName", 100, false))
            {
                Log.error(UpdateMerchant_company_errormsg);
                flag    = false;
            }
            else
            {
                detailhash.put("company_name", (String) request.getParameter("company_name"));
            }
            if (!ESAPI.validator().isValidInput("notifyemail", (String) request.getParameter("notifyemail"), "Email", 100, true))
            {
                errormsg.append(UpdateMerchant_email_errormsg);
                errormsg.append(EOL);
                detailhash.put("notifyemail", "");
                flag    = false;
            }
            else
            {
                detailhash.put("notifyemail", (String) request.getParameter("notifyemail"));
            }
            if (!ESAPI.validator().isValidInput("Contact persons", (String) request.getParameter("contact_persons"), "contactName", 100, false))
            {

                errormsg.append(UpdateMerchant_maincontact_errormsg);
                errormsg.append(EOL);
                detailhash.put("contact_persons", "");
                flag    = false;
            }
            else
            {
                detailhash.put("contact_persons", (String) request.getParameter("contact_persons"));
            }
            if (!ESAPI.validator().isValidInput("contact_emails", (String) request.getParameter("contact_emails"), "Email", 100, false))
            {
                errormsg.append(UpdateMerchant_mainemail_errormsg);
                errormsg.append(EOL);
                detailhash.put("contact_emails", "");
                flag    = false;
            }
            else
            {
                detailhash.put("contact_emails", (String) request.getParameter("contact_emails"));
            }
            if (!ESAPI.validator().isValidInput("address", (String) request.getParameter("address"), "Description", 175, true))
            {
                errormsg.append(UpdateMerchant_address_errormsg);
                errormsg.append(EOL);
                detailhash.put("address", "");
                flag    = false;
            }
            else
            {
                detailhash.put("address", (String) request.getParameter("address"));
            }
            if (!ESAPI.validator().isValidInput("city", (String) request.getParameter("city"), "City", 50, true))
            {
                errormsg.append(UpdateMerchant_city_errormsg);
                errormsg.append(EOL);
                detailhash.put("city", "");
                flag    = false;
            }
            else
            {
                detailhash.put("city", (String) request.getParameter("city"));
            }
            if (!ESAPI.validator().isValidInput("state", (String) request.getParameter("state"), "Description", 50, true))
            {
                errormsg.append(UpdateMerchant_state_errormsg);
                errormsg.append(EOL);
                detailhash.put("state", "");
                //flag = false;
            }
            else
            {
                detailhash.put("state", (String) request.getParameter("state"));
            }
            if (!ESAPI.validator().isValidInput("zip", (String) request.getParameter("zip"), "Zip", 30, true))
            {
                errormsg.append(UpdateMerchant_pincode_errormsg);
                errormsg.append(EOL);
                detailhash.put("zip", "");
                flag = false;
            }
            else
            {
                detailhash.put("zip", (String) request.getParameter("zip"));
            }
          /* if (!ESAPI.validator().isValidInput("Country",(String) request.getParameter("country"),"SafeString",50,false))
            {   errormsg.append("Invalid Country."+EOL);
                detailhash.put("country", "");
                flag = false;
            }
            else
                detailhash.put("country", (String) request.getParameter("country"));*/

           /* if (!ESAPI.validator().isValidInput("Telno",(String) request.getParameter("telno"),"Phone",30,false))
            {   errormsg.append( "Invalid Contact Telephone Number."+EOL);
                detailhash.put("telno", "");
                flag = false;
            }
            else
                detailhash.put("telno", (String) request.getParameter("telno"));*/


            String country = "";
            if (functions.isValueNull(request.getParameter("country")))
            {
                String[] splitValue = request.getParameter("country").split("\\|");
                country             = splitValue[0];
            }
            if (!ESAPI.validator().isValidInput("country", country, "SafeString", 50, false))
            {
                errormsg.append(errormsg);
                errormsg.append(UpdateMerchant_validcountry_errormsg);
                errormsg.append(EOL);
                flag = false;
                detailhash.put("country", country);

            }
            else
            {
                detailhash.put("country", country);
            }

            String timezone = request.getParameter("timezone");

            if (!ESAPI.validator().isValidInput("timezone",timezone, "SafeString", 50, true))
            {
                errormsg.append(errormsg);
                errormsg.append(UpdateMerchant_timezone_errormsg);
                errormsg.append(EOL);
                flag = false;
                detailhash.put("timezone", timezone);

            }
            else
            {
                detailhash.put("timezone", timezone);
            }

            String telno    = request.getParameter("telno");
            String phonecc  = request.getParameter("phonecc");
            String phoneNo  = phonecc + "-" + telno;

            if (!ESAPI.validator().isValidInput("telno", (String) telno, "SignupPhone", 20, false))
            {
                errormsg.append(errormsg);
                errormsg.append(UpdateMerchant_telephone_errormsg);
                errormsg.append(EOL);
                flag = false;
                detailhash.put("telno", "");
            }
            else
            {
                detailhash.put("telno", phoneNo);
            }
            if (!ESAPI.validator().isValidInput("Faxno", (String) request.getParameter("faxno"), "Numbers", 30, true))
            {
                errormsg.append(UpdateMerchant_faxno_errormsg);
                errormsg.append(EOL);
                detailhash.put("faxno", "");
                flag = false;
            }
            else
            {
                detailhash.put("faxno", (String) request.getParameter("faxno"));
            }
            if (!ESAPI.validator().isValidInput("brandname", (String) request.getParameter("brandname"), "companyName", 100, true))
            {
                errormsg.append(UpdateMerchant_brand_errormsg);
                errormsg.append(EOL);
                detailhash.put("brandname", "");
                flag = false;
            }
            else
            {
                detailhash.put("brandname", (String) request.getParameter("brandname"));
            }
            if (!ESAPI.validator().isValidInput("sitename", (String) request.getParameter("sitename"), "URL", 100, false))
            {
                errormsg.append(UpdateMerchant_siteurl_errormsg);
                errormsg.append(EOL);
                detailhash.put("sitename", "");
                flag = false;
            }
            else
            {
                detailhash.put("sitename", (String) request.getParameter("sitename"));
            }
            if (!ESAPI.validator().isValidInput("maxscoreallowed", (String) request.getParameter("maxscoreallowed"), "Numbers", 100, false))
            {
                errormsg.append(UpdateMerchant_fraud_errormsg);
                errormsg.append(EOL);
                detailhash.put("maxscoreallowed", "");
                flag = false;
            }
            else
            {
                detailhash.put("maxscoreallowed", (String) request.getParameter("maxscoreallowed"));
            }
            if (!ESAPI.validator().isValidInput("maxscoreautoreversal", (String) request.getParameter("maxscoreautoreversal"), "Numbers", 100, false))
            {
                errormsg.append(UpdateMerchant_reversal_errormsg);
                errormsg.append(EOL);
                detailhash.put("maxscoreautoreversal", "");
                flag = false;
            }
            else
            {
                detailhash.put("maxscoreautoreversal", (String) request.getParameter("maxscoreautoreversal"));
            }
            if (!ESAPI.validator().isValidInput("maincontact_ccmailid", (String) request.getParameter("maincontact_ccmailid"), "Email", 100, true))
            {

                errormsg.append(UpdateMerchant_contactcc_errormsg);
                errormsg.append(EOL);
                detailhash.put("maincontact_ccmailid", "");
                flag = false;
            }
            else
            {
                detailhash.put("maincontact_ccmailid", (String) request.getParameter("maincontact_ccmailid"));
            }
            if (!ESAPI.validator().isValidInput("maincontact_phone", (String) request.getParameter("maincontact_phone"), "Numbers", 100, true))
            {
                errormsg.append(UpdateMerchant_maincontact1_errormsg);
                errormsg.append(EOL);
                detailhash.put("maincontact_phone", "");
                flag = false;
            }
            else
            {
                detailhash.put("maincontact_phone", (String) request.getParameter("maincontact_phone"));
            }
            if (!ESAPI.validator().isValidInput("Contact persons", (String) request.getParameter("support_persons"), "contactName", 100, false))
            {

                errormsg.append(UpdateMerchant_support_errormsg);
                errormsg.append(EOL);
                detailhash.put("support_persons", "");
                flag = false;
            }
            else
            {
                detailhash.put("support_persons", (String) request.getParameter("support_persons"));
            }
            if (!ESAPI.validator().isValidInput("support_emails", (String) request.getParameter("support_emails"), "Email", 100, false))
            {
                errormsg.append(UpdateMerchant_supportemail_errormsg);
                errormsg.append(EOL);
                detailhash.put("support_emails", "");
                flag = false;
            }
            else
            {
                detailhash.put("support_emails", (String) request.getParameter("support_emails"));
            }
            if (!ESAPI.validator().isValidInput("support_ccmailid", (String) request.getParameter("support_ccmailid"), "Email", 100, true))
            {

                errormsg.append(UpdateMerchant_mailid_errormsg);
                errormsg.append(EOL);
                detailhash.put("support_ccmailid", "");
                flag = false;
            }
            else
            {
                detailhash.put("support_ccmailid", (String) request.getParameter("support_ccmailid"));
            }
            if (!ESAPI.validator().isValidInput("support_phone", (String) request.getParameter("support_phone"), "Numbers", 100, true))
            {
                errormsg.append(UpdateMerchant_supportphone_errormsg);
                errormsg.append(EOL);
                detailhash.put("support_phone", "");
                flag = false;
            }
            else
            {
                detailhash.put("support_phone", (String) request.getParameter("support_phone"));
            }


            if (!ESAPI.validator().isValidInput("cbcontact_name", (String) request.getParameter("cbcontact_name"), "contactName", 100, true))
            {

                errormsg.append(UpdateMerchant_chargeback_errormsg);
                errormsg.append(EOL);
                detailhash.put("cbcontact_name", "");
                flag = false;
            }
            else
            {
                detailhash.put("cbcontact_name", (String) request.getParameter("cbcontact_name"));
            }
            if (!ESAPI.validator().isValidInput("cbcontact_mailid", (String) request.getParameter("cbcontact_mailid"), "Email", 100, true))
            {
                errormsg.append(UpdateMerchant_chargeemail_errormsg);
                errormsg.append(EOL);
                detailhash.put("cbcontact_mailid", "");
                flag = false;
            }
            else
            {
                detailhash.put("cbcontact_mailid", (String) request.getParameter("cbcontact_mailid"));
            }
            if (!ESAPI.validator().isValidInput("cbcontact_ccmailid", (String) request.getParameter("cbcontact_ccmailid"), "Email", 100, true))
            {
                errormsg.append(UpdateMerchant_chargebackcc_errormsg);
                errormsg.append(EOL);
                detailhash.put("cbcontact_ccmailid", "");
                flag = false;
            }
            else
            {
                detailhash.put("cbcontact_ccmailid", (String) request.getParameter("cbcontact_ccmailid"));
            }
            if (!ESAPI.validator().isValidInput("cbcontact_phone", (String) request.getParameter("cbcontact_phone"), "Numbers", 100, true))
            {
                errormsg.append(UpdateMerchant_contactphone_errormsg);
                errormsg.append(EOL);
                detailhash.put("cbcontact_phone", "");
                flag = false;
            }
            else
            {
                detailhash.put("cbcontact_phone", (String) request.getParameter("cbcontact_phone"));
            }
            if (!ESAPI.validator().isValidInput("refundcontact_name", (String) request.getParameter("refundcontact_name"), "contactName", 100, true))
            {

                errormsg.append(UpdateMerchant_refund_errormsg);
                errormsg.append(EOL);
                detailhash.put("refundcontact_name", "");
                flag = false;
            }
            else
            {
                detailhash.put("refundcontact_name", (String) request.getParameter("refundcontact_name"));
            }

            if (!ESAPI.validator().isValidInput("refundcontact_mailid", (String) request.getParameter("refundcontact_mailid"), "Email", 100, true))
            {
                errormsg.append(UpdateMerchant_refundcontact_errormsg);
                errormsg.append(EOL);
                detailhash.put("refundcontact_mailid", "");
                flag = false;
            }
            else
            {
                detailhash.put("refundcontact_mailid", (String) request.getParameter("refundcontact_mailid"));
            }
            if (!ESAPI.validator().isValidInput("refundcontact_ccmailid", (String) request.getParameter("refundcontact_ccmailid"), "Email", 100, true))
            {
                errormsg.append(UpdateMerchant_refundcc_errormsg);
                errormsg.append(EOL);
                detailhash.put("refundcontact_ccmailid", "");
                flag = false;
            }
            else
            {
                detailhash.put("refundcontact_ccmailid", (String) request.getParameter("refundcontact_ccmailid"));
            }
            if (!ESAPI.validator().isValidInput("refundcontact_phone", (String) request.getParameter("refundcontact_phone"), "Numbers", 100, true))
            {
                errormsg.append(UpdateMerchant_refundcontactphone_errormsg);
                errormsg.append(EOL);
                detailhash.put("refundcontact_phone", "");
                flag = false;
            }
            else
            {
                detailhash.put("refundcontact_phone", (String) request.getParameter("refundcontact_phone"));
            }

            if (!ESAPI.validator().isValidInput("salescontact_name", (String) request.getParameter("salescontact_name"), "contactName", 100, true))
            {

                errormsg.append(UpdateMerchant_sales_errormsg);
                errormsg.append(EOL);
                detailhash.put("salescontact_name", "");
                flag = false;
            }
            else
            {
                detailhash.put("salescontact_name", (String) request.getParameter("salescontact_name"));
            }
            if (!ESAPI.validator().isValidInput("salescontact_mailid", (String) request.getParameter("salescontact_mailid"), "Email", 100, true))
            {
                errormsg.append(UpdateMerchant_salescontact_errormsg);
                errormsg.append(EOL);
                detailhash.put("salescontact_mailid", "");
                flag = false;
            }
            else
            {
                detailhash.put("salescontact_mailid", (String) request.getParameter("salescontact_mailid"));
            }
            if (!ESAPI.validator().isValidInput("salescontact_ccmailid", (String) request.getParameter("salescontact_ccmailid"), "Email", 100, true))
            {
                errormsg.append(UpdateMerchant_ccmailid_errormsg);
                errormsg.append(EOL);
                detailhash.put("salescontact_ccmailid", "");
                flag = false;
            }
            else
            {
                detailhash.put("salescontact_ccmailid", (String) request.getParameter("salescontact_ccmailid"));
            }
            if (!ESAPI.validator().isValidInput("salescontact_phone", (String) request.getParameter("salescontact_phone"), "Numbers", 100, true))
            {
                errormsg.append(UpdateMerchant_contactphone1_errormsg);
                errormsg.append(EOL);
                detailhash.put("salescontact_phone", "");
                flag = false;
            }
            else
            {
                detailhash.put("salescontact_phone", (String) request.getParameter("salescontact_phone"));
            }

            if (!ESAPI.validator().isValidInput("fraudcontact_name", (String) request.getParameter("fraudcontact_name"), "contactName", 100, true))
            {

                errormsg.append(UpdateMerchant_fraudcontact_errormsg);
                errormsg.append(EOL);
                detailhash.put("fraudcontact_name", "");
                flag = false;
            }
            else
            {
                detailhash.put("fraudcontact_name", (String) request.getParameter("fraudcontact_name"));
            }
            if (!ESAPI.validator().isValidInput("fraudcontact_mailid", (String) request.getParameter("fraudcontact_mailid"), "Email", 100, true))
            {
                errormsg.append(UpdateMerchant_fraudemail_errormsg);
                errormsg.append(EOL);
                detailhash.put("fraudcontact_mailid", "");
                flag = false;
            }
            else
            {
                detailhash.put("fraudcontact_mailid", (String) request.getParameter("fraudcontact_mailid"));
            }
            if (!ESAPI.validator().isValidInput("fraudcontact_ccmailid", (String) request.getParameter("fraudcontact_ccmailid"), "Email", 100, true))
            {
                errormsg.append(UpdateMerchant_fraudccmailid_errormsg);
                errormsg.append(EOL);
                detailhash.put("fraudcontact_ccmailid", "");
                flag = false;
            }
            else
            {
                detailhash.put("fraudcontact_ccmailid", (String) request.getParameter("fraudcontact_ccmailid"));
            }
            if (!ESAPI.validator().isValidInput("fraudcontact_phone", (String) request.getParameter("fraudcontact_phone"), "Numbers", 100, true))
            {
                errormsg.append(UpdateMerchant_fraudphone_errormsg);
                errormsg.append(EOL);
                detailhash.put("fraudcontact_phone", "");
                flag = false;
            }
            else
            {
                detailhash.put("fraudcontact_phone", (String) request.getParameter("fraudcontact_phone"));
            }

            if (!ESAPI.validator().isValidInput("technicalcontact_name", (String) request.getParameter("technicalcontact_name"), "contactName", 100, true))
            {

                errormsg.append(UpdateMerchant_technical_errormsg);
                errormsg.append(EOL);
                detailhash.put("technicalcontact_name", "");
                flag = false;
            }
            else
            {
                detailhash.put("technicalcontact_name", (String) request.getParameter("technicalcontact_name"));
            }
            if (!ESAPI.validator().isValidInput("technicalcontact_mailid", (String) request.getParameter("technicalcontact_mailid"), "Email", 100, true))
            {
                errormsg.append(UpdateMerchant_technicalemail_errormsg);
                errormsg.append(EOL);
                detailhash.put("technicalcontact_mailid", "");
                flag = false;
            }
            else
            {
                detailhash.put("technicalcontact_mailid", (String) request.getParameter("technicalcontact_mailid"));
            }
            if (!ESAPI.validator().isValidInput("technicalcontact_ccmailid", (String) request.getParameter("technicalcontact_ccmailid"), "Email", 100, true))
            {
                errormsg.append(UpdateMerchant_mailid1_errormsg);
                errormsg.append(EOL);
                detailhash.put("technicalcontact_ccmailid", "");
                flag = false;
            }
            else
            {
                detailhash.put("technicalcontact_ccmailid", (String) request.getParameter("technicalcontact_ccmailid"));
            }
            if (!ESAPI.validator().isValidInput("technicalcontact_phone", (String) request.getParameter("technicalcontact_phone"), "Numbers", 100, true))
            {
                errormsg.append(UpdateMerchant_technicalcontact_errormsg);
                errormsg.append(EOL);
                detailhash.put("technicalcontact_phone", "");
                flag = false;
            }
            else
            {
                detailhash.put("technicalcontact_phone", (String) request.getParameter("technicalcontact_phone"));
            }

            if (!ESAPI.validator().isValidInput("billingcontact_name", (String) request.getParameter("billingcontact_name"), "contactName", 100, true))
            {

                errormsg.append(UpdateMerchant_billingcontact_errormsg);
                errormsg.append(EOL);
                detailhash.put("billingcontact_name", "");
                flag = false;
            }
            else
            {
                detailhash.put("billingcontact_name", (String) request.getParameter("billingcontact_name"));
            }

            if (!ESAPI.validator().isValidInput("billingcontact_mailid", (String) request.getParameter("billingcontact_mailid"), "Email", 100, true))
            {
                errormsg.append(UpdateMerchant_billingemail_errormsg);
                errormsg.append(EOL);
                detailhash.put("billingcontact_mailid", "");
                flag = false;
            }
            else
            {
                detailhash.put("billingcontact_mailid", (String) request.getParameter("billingcontact_mailid"));
            }

            if (!ESAPI.validator().isValidInput("billingcontact_ccmailid",(String) request.getParameter("billingcontact_ccmailid"),"Email",100,true))
            {   errormsg.append( UpdateMerchant_billingcc_errormsg);
                errormsg.append(EOL);
                detailhash.put("billingcontact_ccmailid", "");
                flag = false;
            }
            else
            {
                detailhash.put("billingcontact_ccmailid", (String) request.getParameter("billingcontact_ccmailid"));
            }
            if (!ESAPI.validator().isValidInput("billingcontact_phone",(String) request.getParameter("billingcontact_phone"),"Numbers",100,true))
            {   errormsg.append( UpdateMerchant_billingphone_errormsg);
                errormsg.append(EOL);
                detailhash.put("billingcontact_phone", "");
                flag = false;
            }
            else
            {
                detailhash.put("billingcontact_phone", (String) request.getParameter("billingcontact_phone"));
            }

            if(request.getParameter("sendEmailNotification") != null){
                isSendEmailNotification = request.getParameter("sendEmailNotification");
            }

            Log.error("Merchant isSendEmailNotification --------> "+isSendEmailNotification);
            if (flag)
            {
                Log.debug("Update the record of Merchant ");
                boolean isUpdate        = false;
                isUpdate                = merchants.updateProfile(detailhash, (String) session.getAttribute("merchantid"));
                //MailService mailService=new MailService();
                if(isSendEmailNotification.equals("Y")){
                    AsynchronousMailService asynchronousMailService =   new AsynchronousMailService();
                    HashMap mailValues                              = new HashMap();
                    mailValues.put(MailPlaceHolder.TOID,(String) session.getAttribute("merchantid"));
                //mailService.sendMail(MailEventEnum.PARTNERS_MERCHANT_CHANGE_IN_PROFILE_DETAILS,mailValues);
                    asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNERS_MERCHANT_CHANGE_IN_PROFILE_DETAILS,mailValues);
                }
                request.setAttribute("MES", UpdateMerchant_update_errormsg);
                redirectpage = "/updatedprofile.jsp?ctoken="+user.getCSRFToken();

            }
            else
            {
                request.setAttribute("error",errormsg.toString());
                redirectpage = "/merchprofile.jsp?MES=F&ctoken="+user.getCSRFToken();
            }
        }
        catch (SystemError se)
        {
            Log.error("Leaving Merchants throwing SQL Exception as System Error : ",se);
            redirectpage = "/error.jsp";
        }
        catch (Exception e)
        {
            Log.error("Leaving Merchants throwing Exception : ",e);
            redirectpage = "/error.jsp";
        }
        Log.info("Update the record of Merchant and redirect to :::: updatedprofile.jsp");
        request.setAttribute("details", detailhash);
        Log.debug("Leaving UpdateMerchant");
        Log.debug("redirectpage  " + redirectpage);
        RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
        rd.forward(request, response);

    }
}
