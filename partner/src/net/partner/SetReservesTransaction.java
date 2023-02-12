package net.partner;

import com.directi.pg.AsyncActivityTracker;
import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.PartnerManager;
import com.manager.enums.ActivityLogParameters;
import com.manager.enums.TemplatePreference;
import com.manager.vo.ActivityTrackerVOs;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/15/15
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class SetReservesTransaction extends HttpServlet
{
    private static Logger logger = new Logger(SetReservesTransaction.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        User user           = (User) session.getAttribute("ESAPIUserSessionKey");

        PartnerFunctions partner                = new PartnerFunctions();
        Functions functions                     = new Functions();
        ActivityTrackerVOs activityTrackerVOs   = new ActivityTrackerVOs();

        String actionExecutorId =(String) session.getAttribute("merchantid");

        /*String activityrole     = "";
        String Roles            = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
        List<String> rolelist   = Arrays.asList(Roles.split("\\s*,\\s*"));
        if(rolelist.contains("subpartner"))
        {
            activityrole    = ActivityLogParameters.SUBPARTNER.toString();
        }
        else if(rolelist.contains("superpartner"))
        {
            activityrole    = ActivityLogParameters.SUPERPARTNER.toString();
        }
        else if(rolelist.contains("childsuperpartner"))
        {
            activityrole    = ActivityLogParameters.CHILEDSUPERPARTNER.toString();
        }

        else if(rolelist.contains("partner")){
            activityrole    = ActivityLogParameters.PARTNER.toString();
        }*/
        String Login    = user.getAccountName();


        String errorMsg = "";
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        String EOL = "<br>";
        String memberids                = req.getParameter("memberid");
        String HEADPANELFONT_COLOR      = "";
        String BODYPANELFONT_COLOR      = "";
        String PANELHEADING_COLOR       = "";
        String PANELBODY_COLOR          = "";
        String MAINBACKGROUNDCOLOR      = "";
       /* String TRANSACTIONPAGEMERCHANTLOGO = req.getParameter(TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.toString());
        String BODY_BG_COLOR = "";
        String BODY_FG_COLOR = "";
        String NAV_FONT_COLOR = "";*/
        String TEXTBOX_COLOR = "";
        //String ICON_VECTOR = "";
        String MAILBACKGROUNDCOLOR      = "";
        String MAIL_PANELHEADING_COLOR  = "";
        String MAIL_HEADPANELFONT_COLOR = "";
        String MAIL_BODYPANELFONT_COLOR = "";
        String BODY_BACKGROUND_COLOR    = "";
        String BODY_FOREGROUND_COLOR    = "";
        String NAVIGATION_FONT_COLOR    = "";
        String ICON_VECTOR_COLOR        = "";

        String CHECKOUTBODYNFOOTER_COLOR        = "";
        String CHECKOUTHEADERBACKGROUND_COLOR   = "";
        String CHECKOUTNAVIGATIONBAR_COLOR      = "";
        String CHECKOUTBUTTON_FONT_COLOR        = "";
        String CHECKOUTHEADER_FONT_COLOR        = "";
        String CHECKOUTFULLBACKGROUND_COLOR     = "";
        String CHECKOUTLABEL_FONT_COLOR         = "";
        String CHECKOUTNAVIGATIONBAR_FONT_COLOR = "";
        String CHECKOUTBUTTON_COLOR             = "";
        String CHECKOUTICON_COLOR               = "";
        String CHECKOUTTIMER_COLOR              = "";
        String CHECKOUTBOX_SHADOW               = "";


        StringBuilder sSuccessMessage   = new StringBuilder();
        StringBuilder sErrorMessage     = new StringBuilder();
        String active                   = req.getParameter("activation");
        String isservice                = req.getParameter("isservice");
        String personalInfoDisplay      = req.getParameter("personal_info_display");
        String personalInfoValidation   = req.getParameter("personal_info_validation");
        String hostedPaymentPage        = req.getParameter("hosted_payment_page");
        /*String consent = req.getParameter("consent");*/
        String masterCardSupported      = req.getParameter("masterCardSupported");
        /*String isPharma = req.getParameter("isPharma");
        String template = req.getParameter("template");
        String ismerchantlogo = req.getParameter("ismerchantlogo");
        String ispcilogo = req.getParameter("ispcilogo");*/
        String isCardEncryptionEnable       = req.getParameter("isCardEncryptionEnable");
        String isTokenizationAllowed        = req.getParameter("isTokenizationAllowed");
        String flightMode                   = req.getParameter("flightMode");
        String expDateOffset                = req.getParameter("expDateOffset");
        String multiCurrencySupport         = req.getParameter("multiCurrencySupport");
        String isRefundAllowed              = req.getParameter("isRefundAllowed");
        String isPODRequired                = req.getParameter("isPODRequired");
        String isSplitPayment               = req.getParameter("isSplitPayment");
        String splitPaymentType             = req.getParameter("splitPaymentType");
        String smsActivation                = req.getParameter("smsactivation");
        String customerSmsActivation        = req.getParameter("customersmsactivation");
        String isAddressDetailsRequired     = req.getParameter("isAddrDetailsRequired");
        String autoRedirect                 = req.getParameter("autoredirect");

        String isExcessCaptureAllowed   = req.getParameter("isExcessCaptureAllowed");
        String notificationUrl          = "";//req.getParameter("notificationurl");
        String termsUrl                 = "";//req.getParameter("termsurl");
        String ipValidationRequired     = req.getParameter("ip_validation_required");
       /* String isPartnerLogo = req.getParameter("ispartnerlogo");*/
        String dailyRefundLimit         = req.getParameter("dailyRefundLimit");
        System.out.println("dailyRefundLimit=="+dailyRefundLimit);
        String refundAllowedDays    = req.getParameter("refundAllowedDays");
        String binRouting           = req.getParameter("binRouting");
       /* String vbvLogo = req.getParameter("vbvLogo");
        String masterSecureLogo = req.getParameter("masterSecureLogo");*/
       /* String isSecurityLogo = req.getParameter("isSecurityLogo");*/
        String isMultipleRefund     = req.getParameter("isMultipleRefund");
        String privacyPolicyUrl     = req.getParameter("privacyPolicyUrl");
        String isPartialRefund      = req.getParameter("isPartialRefund");
        String isIpWhitelisted      = req.getParameter("isIpWhitelisted");
        String blacklistTransaction = req.getParameter("blacklistTransaction");
        String emiSupport           = req.getParameter("emiSupport");
        String binservice           = req.getParameter("binservice");
        String merchant_order_details   = req.getParameter("merchant_order_details");
        /*String checkoutTimer = req.getParameter("checkoutTimer");
        String checkoutTimerTime = req.getParameter("checkoutTimerTime");*/

        String marketPlace              = req.getParameter("marketplace");
        String isVirtualCheckoutAllowed = req.getParameter("isVirtualCheckoutAllowed");
        String isMobileAllowedForVC     = req.getParameter("isMobileAllowedForVC");
        String isEmailAllowedForVC      = req.getParameter("isEmailAllowedForVC");
        //String isCvvStore = req.getParameter("isCvvStore");
        String isIpWhitelistedAPI       = req.getParameter("isIpWhitelistedAPI");
        String isUniqueOrderIdRequired  = req.getParameter("isUniqueOrderIdRequired");
        String autoSelectTerminal       = req.getParameter("autoSelectTerminal");
        String onchangedValues          = req.getParameter("onchangedvalue");
       String isMerchantLogoBO          = req.getParameter("isMerchantLogoBO");
        String ip_whitelist_invoice     = req.getParameter("ip_whitelist_invoice");

        String isShareAllowed           = req.getParameter("isShareAllowed");
        String isSignatureAllowed       = req.getParameter("isSignatureAllowed");
        String isSaveReceiptAllowed     = req.getParameter("isSaveReceiptAllowed");
        String defaultLanguage          = req.getParameter("defaultLanguage");
        String isDomainWhitelisted      = req.getParameter("isDomainWhitelisted");
        String isOTPRequired            = req.getParameter("isOTPRequired");
        String isCardStorageRequired        = req.getParameter("isCardStorageRequired");
        String payoutPersonalInfoValidation = req.getParameter("payoutPersonalInfoValidation");


        if (!ESAPI.validator().isValidInput("HEADPANELFONT_COLOR ", req.getParameter(TemplatePreference.HEADPANELFONT_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Label Font Color" + EOL;
        }
        else
        {
            HEADPANELFONT_COLOR = req.getParameter(TemplatePreference.HEADPANELFONT_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("BODYPANELFONT_COLOR ", req.getParameter(TemplatePreference.BODYPANELFONT_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Hading Label Font Color" + EOL;
        }
        else
        {
            BODYPANELFONT_COLOR = req.getParameter(TemplatePreference.BODYPANELFONT_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("PANELHEADING_COLOR ", req.getParameter(TemplatePreference.PANELHEADING_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Panel Heading Color" + EOL;
        }
        else
        {
            PANELHEADING_COLOR = req.getParameter(TemplatePreference.PANELHEADING_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("MAINBACKGROUNDCOLOR ", req.getParameter(TemplatePreference.MAINBACKGROUNDCOLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Template Background Color" + EOL;
        }
        else
        {
            MAINBACKGROUNDCOLOR = req.getParameter(TemplatePreference.MAINBACKGROUNDCOLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("PANELBODY_COLOR ", req.getParameter(TemplatePreference.PANELBODY_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Left Navigation Color" + EOL;
        }
        else
        {
            PANELBODY_COLOR = req.getParameter(TemplatePreference.PANELBODY_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("BODY_BACKGROUND_COLOR ", req.getParameter(TemplatePreference.BODY_BACKGROUND_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Body BackGround Color" + EOL;
        }
        else
        {
            BODY_BACKGROUND_COLOR = req.getParameter(TemplatePreference.BODY_BACKGROUND_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("BODY_FOREGROUND_COLOR ", req.getParameter(TemplatePreference.BODY_FOREGROUND_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Body ForeGround Color" + EOL;
        }
        else
        {
            BODY_FOREGROUND_COLOR = req.getParameter(TemplatePreference.BODY_FOREGROUND_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("TEXTBOX_COLOR ", req.getParameter(TemplatePreference.TEXTBOX_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Textbox Color" + EOL;
        }
        else
        {
            TEXTBOX_COLOR = req.getParameter(TemplatePreference.TEXTBOX_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("NAVIGATION_FONT_COLOR ", req.getParameter(TemplatePreference.NAVIGATION_FONT_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Navigation Font Color" + EOL;
        }
        else
        {
            NAVIGATION_FONT_COLOR = req.getParameter(TemplatePreference.NAVIGATION_FONT_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("ICON_VECTOR_COLOR ", req.getParameter(TemplatePreference.ICON_VECTOR_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Icon Vector Color" + EOL;
        }
        else
        {
            ICON_VECTOR_COLOR = req.getParameter(TemplatePreference.ICON_VECTOR_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("MAILBACKGROUNDCOLOR ", req.getParameter(TemplatePreference.MAILBACKGROUNDCOLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Mail Template BackGround Color" + EOL;
        }
        else
        {
            MAILBACKGROUNDCOLOR = req.getParameter(TemplatePreference.MAILBACKGROUNDCOLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("MAIL_HEADPANELFONT_COLOR ", req.getParameter(TemplatePreference.MAIL_HEADPANELFONT_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Mail Label Font Color" + EOL;
        }
        else
        {
            MAIL_HEADPANELFONT_COLOR = req.getParameter(TemplatePreference.MAIL_HEADPANELFONT_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("MAIL_PANELHEADING_COLOR ", req.getParameter(TemplatePreference.MAIL_PANELHEADING_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Mail Panel Heading Color" + EOL;
        }
        else
        {
            MAIL_PANELHEADING_COLOR = req.getParameter(TemplatePreference.MAIL_PANELHEADING_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("MAIL_BODYPANELFONT_COLOR ", req.getParameter(TemplatePreference.MAIL_BODYPANELFONT_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Mail Table Color" + EOL;
        }
        else
        {
            MAIL_BODYPANELFONT_COLOR = req.getParameter(TemplatePreference.MAIL_BODYPANELFONT_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("notificationurl ", req.getParameter("notificationurl"), "URL", 255, true))
        {
            errorMsg = errorMsg + "Invalid Notification Url." + EOL;
        }
        else
        {
            notificationUrl = req.getParameter("notificationurl");
        }

        if (!ESAPI.validator().isValidInput("termsurl ",req.getParameter("termsurl"), "URL", 255, true))
        {
            errorMsg = errorMsg + "Invalid Terms Url." + EOL;
        }
        else
        {
            termsUrl = req.getParameter("termsurl");
        }
        if (!ESAPI.validator().isValidInput("privacyPolicyUrl ",req.getParameter("privacyPolicyUrl"), "URL", 255, true))
        {
            errorMsg = errorMsg + "Invalid Privacy Policy Url." + EOL;
        }
        else
        {
            privacyPolicyUrl = req.getParameter("privacyPolicyUrl");
        }
        /*if (!ESAPI.validator().isValidInput("CHECKOUTBODYNFOOTER_COLOR ", req.getParameter(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Body & Footer Color" + EOL;
        }
        else
        {
            CHECKOUTBODYNFOOTER_COLOR = req.getParameter(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("CHECKOUTHEADERBACKGROUND_COLOR ", req.getParameter(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Header Background Color\t" + EOL;
        }
        else
        {
            CHECKOUTHEADERBACKGROUND_COLOR = req.getParameter(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("CHECKOUTNAVIGATIONBAR_COLOR ", req.getParameter(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Navigation Bar" + EOL;
        }
        else
        {
            CHECKOUTNAVIGATIONBAR_COLOR = req.getParameter(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("CHECKOUTBUTTON_FONT_COLOR ", req.getParameter(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Button Font Color" + EOL;
        }
        else
        {
            CHECKOUTBUTTON_FONT_COLOR = req.getParameter(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("CHECKOUTHEADER_FONT_COLOR ", req.getParameter(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Header Font Color" + EOL;
        }
        else
        {
            CHECKOUTHEADER_FONT_COLOR = req.getParameter(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("CHECKOUTFULLBACKGROUND_COLOR ", req.getParameter(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Full Background Color" + EOL;
        }
        else
        {
            CHECKOUTFULLBACKGROUND_COLOR = req.getParameter(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("CHECKOUTLABEL_FONT_COLOR ", req.getParameter(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Label Font Color" + EOL;
        }
        else
        {
            CHECKOUTLABEL_FONT_COLOR = req.getParameter(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("CHECKOUTNAVIGATIONBAR_FONT_COLOR ", req.getParameter(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Navigation Bar Font Color" + EOL;
        }
        else
        {
            CHECKOUTNAVIGATIONBAR_FONT_COLOR = req.getParameter(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("CHECKOUTBUTTON_COLOR ", req.getParameter(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Button Color" + EOL;
        }
        else
        {
            CHECKOUTBUTTON_COLOR = req.getParameter(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("CHECKOUTICON_COLOR ", req.getParameter(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Icon Color" + EOL;
        }
        else
        {
            CHECKOUTICON_COLOR = req.getParameter(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString());
        }

        if (!ESAPI.validator().isValidInput("CHECKOUTTIMER_COLOR", req.getParameter(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()), "colorForTemplate", 10, true))
        {
            errorMsg = errorMsg + "Invalid Timer Color" + EOL;
        }
        else
        {
            CHECKOUTTIMER_COLOR = req.getParameter(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString());
        }
        if (!ESAPI.validator().isValidInput("CHECKOUTBOX_SHADOW ", req.getParameter(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()), "SafeString", 50, true))
        {
            errorMsg = errorMsg + "Invalid Box Shadow" + EOL;
        }
        else
        {
            CHECKOUTBOX_SHADOW = req.getParameter(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString());
        }
        if (!ESAPI.validator().isValidInput("checkoutTimerTime", req.getParameter("checkoutTimerTime"), "checkoutTimer", 5, false)||req.getParameter("checkoutTimerTime").equals("0:00")||req.getParameter("checkoutTimerTime").equals("00:00")||req.getParameter("checkoutTimerTime").equals("00:0"))
        {
            errorMsg = errorMsg + "Invalid Checkout Timer Time,Accepts only in [mm:ss]" + EOL;
        }
        else
        {
            checkoutTimerTime = req.getParameter("checkoutTimerTime");
        }*/

        /*if (!ESAPI.validator().isValidInput("dailyRefundLimit ",req.getParameter("dailyRefundLimit"), "URL", 100, true)){
            errorMsg = errorMsg + "Invalid Daily Refund Limit." + EOL;
        }
        else*/
            dailyRefundLimit    = req.getParameter("dailyRefundLimit");

        if(!partner.isEmptyOrNull(errorMsg))
        {
            String redirectpage     = "/transactionsprocess.jsp?ctoken="+user.getCSRFToken();
            req.setAttribute("error", errorMsg);
            RequestDispatcher rd    = req.getRequestDispatcher(redirectpage);
            rd.forward(req, res);
            return;
        }

        StringBuilder query     = new StringBuilder();
        res.setContentType("text/html");
        int updRecs             = 0;
        Connection cn           = null;
        PreparedStatement pstmt = null;
        if (memberids != null)
        { //process only if there is at least one record to be updated
            try
            {
                //int size = reserves.length;
                for (int i = 0; i < 1; i++)
                {
                    Map<String,Object> merchantTemplateInformationUpdate    = new HashMap<String, Object>();
                    Map<String,Object> merchantTemplateInformationInsert    = new HashMap<String, Object>();
                    Map<String,Object> merchantTemplateInformationDelete    = new HashMap<String, Object>();
                    DateFormat dateFormat   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date               = new Date();
                    String d                = dateFormat.format(date);
                    String activation       = functions.getActivation(memberids);
                    cn                      = Database.getConnection();
                    query.append("update members m JOIN merchant_configuration mc ON m.memberid=mc.memberid  set activation=?,isservice=?,masterCardSupported=?,isCardEncryptionEnable=?,isTokenizationAllowed=?,flightMode=?, expDateOffset = ?,multiCurrencySupport=?,isrefund=?,isPODRequired=?,isSplitPayment=?,splitPaymentType=?,smsactivation=?,customersmsactivation=?,addressDetails=?,autoredirect=?,isExcessCaptureAllowed=?,notificationUrl=?,termsUrl=?,ip_validation_required=?,refunddailylimit=?,refundallowed_days=?,binRouting=?,personal_info_display=?,personal_info_validation=?,hosted_payment_page=?,isMultipleRefund=?,privacyPolicyUrl=?,isPartialRefund=?,isIpWhitelisted=?,blacklistTransaction=?,emiSupport=?,binservice=?,merchant_order_details=?,marketplace=?,isVirtualCheckoutAllowed=?,isMobileAllowedForVC=?,isEmailAllowedForVC=?,is_rest_whitelisted=?,isUniqueOrderIdRequired=?,autoSelectTerminal=?,m.ip_whitelist_invoice=?,isShareAllowed=?,isMerchantLogoBO=?,isSignatureAllowed=?,isSaveReceiptAllowed=?,defaultLanguage=?,isDomainWhitelisted=?,isOTPRequired=?,isCardStorageRequired=?,payoutPersonalInfoValidation=?");
                    if (!activation.equals("Y") && active.equals("Y"))
                    {
                        query.append(" ,activation_date = ");
                        query.append("UNIX_TIMESTAMP(NOW())");
                    }
                    query.append(" where m.memberid=?");
                    pstmt   = cn.prepareStatement(query.toString());
                    pstmt.setString(1,active);
                    pstmt.setString(2,isservice);
                    pstmt.setString(3,masterCardSupported);
                    pstmt.setString(4,isCardEncryptionEnable);
                    pstmt.setString(5,isTokenizationAllowed);
                    pstmt.setString(6,flightMode);
                    pstmt.setString(7,expDateOffset);
                    pstmt.setString(8,multiCurrencySupport);
                    pstmt.setString(9,isRefundAllowed);
                    pstmt.setString(10,isPODRequired);
                    pstmt.setString(11,isSplitPayment);
                    pstmt.setString(12,splitPaymentType);
                    pstmt.setString(13,smsActivation);
                    pstmt.setString(14,customerSmsActivation);
                    pstmt.setString(15,isAddressDetailsRequired);
                    pstmt.setString(16,autoRedirect);
                    pstmt.setString(17,isExcessCaptureAllowed);
                    pstmt.setString(18,notificationUrl);
                    pstmt.setString(19,termsUrl);
                    pstmt.setString(20,ipValidationRequired);
                    pstmt.setString(21,dailyRefundLimit);
                    pstmt.setString(22,refundAllowedDays);
                    pstmt.setString(23,binRouting);
                    pstmt.setString(24,personalInfoDisplay);
                    pstmt.setString(25,personalInfoValidation);
                    pstmt.setString(26,hostedPaymentPage);
                   /* pstmt.setString(27,consent);*/
                    pstmt.setString(27,isMultipleRefund);
                    pstmt.setString(28,privacyPolicyUrl);
                    pstmt.setString(29,isPartialRefund);
                    pstmt.setString(30,isIpWhitelisted);
                    pstmt.setString(31,blacklistTransaction);
                    pstmt.setString(32,emiSupport);
                    pstmt.setString(33,binservice);
                    pstmt.setString(34,merchant_order_details);
//                    pstmt.setString(35,supportSection);
                    pstmt.setString(35,marketPlace);
                    pstmt.setString(36,isVirtualCheckoutAllowed);
                    pstmt.setString(37,isMobileAllowedForVC);
                    pstmt.setString(38,isEmailAllowedForVC);
                   // pstmt.setString(51,isCvvStore);
                    pstmt.setString(39,isIpWhitelistedAPI);
                    pstmt.setString(40,isUniqueOrderIdRequired);
                    pstmt.setString(41,autoSelectTerminal);
                    pstmt.setString(42,ip_whitelist_invoice);
                    pstmt.setString(43,isShareAllowed);
                    pstmt.setString(44,isMerchantLogoBO);
                    pstmt.setString(45,isSignatureAllowed);
                    pstmt.setString(46,isSaveReceiptAllowed);
                    pstmt.setString(47,defaultLanguage);
                    pstmt.setString(48,isDomainWhitelisted);
                    pstmt.setString(49,isOTPRequired);
                    pstmt.setString(50,isCardStorageRequired);
                    pstmt.setString(51,payoutPersonalInfoValidation);
                    pstmt.setString(52,memberids);
                    int result = pstmt.executeUpdate();

                    logger.debug("Creating Activity for edit Merchant Backoffice Access");
                    String remoteAddr   = Functions.getIpAddress(req);
                    int serverPort      = req.getServerPort();
                    String servletPath  = req.getServletPath();
                    String header       = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
                    if(functions.isValueNull(onchangedValues))
                    {
                        activityTrackerVOs.setInterface(ActivityLogParameters.PARTNER.toString());
                        activityTrackerVOs.setUser_name(Login + "-" + actionExecutorId);
                        //activityTrackerVOs.setRole(activityrole);
                        activityTrackerVOs.setRole(partner.getUserRole(user));
                        activityTrackerVOs.setAction(ActivityLogParameters.EDIT.toString());
                        activityTrackerVOs.setModule_name(ActivityLogParameters.MERCHANT_TRANSACTION_SETTING.toString());
                        activityTrackerVOs.setLable_values(onchangedValues);
                        activityTrackerVOs.setDescription(ActivityLogParameters.MEMBERID.toString() + "-" + memberids);
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
                            logger.error("Exception while AsyncActivityLog::::", e);
                        }
                    }
                    PartnerManager partnerManager = new PartnerManager();

                    Map<String,Object> presentTemplateDetails=partnerManager.getSavedMemberTemplateDetails(memberids);

                    if(!functions.isValueNull(HEADPANELFONT_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.HEADPANELFONT_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.HEADPANELFONT_COLOR.name(),HEADPANELFONT_COLOR);
                    }
                    else if(functions.isValueNull(HEADPANELFONT_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.HEADPANELFONT_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.HEADPANELFONT_COLOR.name(),HEADPANELFONT_COLOR);
                    }
                    else if(functions.isValueNull(HEADPANELFONT_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.HEADPANELFONT_COLOR.name(),HEADPANELFONT_COLOR);
                    }

                    if(!functions.isValueNull(BODYPANELFONT_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.BODYPANELFONT_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.BODYPANELFONT_COLOR.name(),BODYPANELFONT_COLOR);
                    }
                    else if(functions.isValueNull(BODYPANELFONT_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.BODYPANELFONT_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.BODYPANELFONT_COLOR.name(),BODYPANELFONT_COLOR);
                    }
                    else if(functions.isValueNull(BODYPANELFONT_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.BODYPANELFONT_COLOR.name(),BODYPANELFONT_COLOR);
                    }

                    if(!functions.isValueNull(PANELHEADING_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.PANELHEADING_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.PANELHEADING_COLOR.name(),PANELHEADING_COLOR);
                    }
                    else if(functions.isValueNull(PANELHEADING_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.PANELHEADING_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.PANELHEADING_COLOR.name(),PANELHEADING_COLOR);
                    }
                    else if(functions.isValueNull(PANELHEADING_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.PANELHEADING_COLOR.name(),PANELHEADING_COLOR);
                    }

                    if(!functions.isValueNull(PANELBODY_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.PANELBODY_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.PANELBODY_COLOR.name(),PANELBODY_COLOR);
                    }
                    else if(functions.isValueNull(PANELBODY_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.PANELBODY_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.PANELBODY_COLOR.name(),PANELBODY_COLOR);
                    }
                    else if(functions.isValueNull(PANELBODY_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.PANELBODY_COLOR.name(),PANELBODY_COLOR);
                    }

                    if(!functions.isValueNull(MAINBACKGROUNDCOLOR) && presentTemplateDetails.containsKey(TemplatePreference.MAINBACKGROUNDCOLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.MAINBACKGROUNDCOLOR.name(),MAINBACKGROUNDCOLOR);
                    }
                    else if(functions.isValueNull(MAINBACKGROUNDCOLOR) && presentTemplateDetails.containsKey(TemplatePreference.MAINBACKGROUNDCOLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.MAINBACKGROUNDCOLOR.name(),MAINBACKGROUNDCOLOR);
                    }
                    else if(functions.isValueNull(MAINBACKGROUNDCOLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.MAINBACKGROUNDCOLOR.name(),MAINBACKGROUNDCOLOR);
                    }

                    if(!functions.isValueNull(BODY_BACKGROUND_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.BODY_BACKGROUND_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.BODY_BACKGROUND_COLOR.name(),BODY_BACKGROUND_COLOR);
                    }
                    else if(functions.isValueNull(BODY_BACKGROUND_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.BODY_BACKGROUND_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.BODY_BACKGROUND_COLOR.name(),BODY_BACKGROUND_COLOR);
                    }
                    else if(functions.isValueNull(BODY_BACKGROUND_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.BODY_BACKGROUND_COLOR.name(),BODY_BACKGROUND_COLOR);
                    }

                    if(!functions.isValueNull(BODY_FOREGROUND_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.BODY_FOREGROUND_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.BODY_FOREGROUND_COLOR.name(),BODY_FOREGROUND_COLOR);
                    }
                    else if(functions.isValueNull(BODY_FOREGROUND_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.BODY_FOREGROUND_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.BODY_FOREGROUND_COLOR.name(),BODY_FOREGROUND_COLOR);
                    }
                    else if(functions.isValueNull(BODY_FOREGROUND_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.BODY_FOREGROUND_COLOR.name(),BODY_FOREGROUND_COLOR);
                    }

                    if(!functions.isValueNull(NAVIGATION_FONT_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NAVIGATION_FONT_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.NAVIGATION_FONT_COLOR.name(),NAVIGATION_FONT_COLOR);
                    }
                    else if(functions.isValueNull(NAVIGATION_FONT_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NAVIGATION_FONT_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.NAVIGATION_FONT_COLOR.name(),NAVIGATION_FONT_COLOR);
                    }
                    else if(functions.isValueNull(NAVIGATION_FONT_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.NAVIGATION_FONT_COLOR.name(),NAVIGATION_FONT_COLOR);
                    }

                    if(!functions.isValueNull(TEXTBOX_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.TEXTBOX_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.TEXTBOX_COLOR.name(),TEXTBOX_COLOR);
                    }
                    else if(functions.isValueNull(TEXTBOX_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.TEXTBOX_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.TEXTBOX_COLOR.name(),TEXTBOX_COLOR);
                    }
                    else if(functions.isValueNull(TEXTBOX_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.TEXTBOX_COLOR.name(),TEXTBOX_COLOR);
                    }

                    if(!functions.isValueNull(ICON_VECTOR_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.ICON_VECTOR_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.ICON_VECTOR_COLOR.name(),ICON_VECTOR_COLOR);
                    }
                    else if(functions.isValueNull(ICON_VECTOR_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.ICON_VECTOR_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.ICON_VECTOR_COLOR.name(),ICON_VECTOR_COLOR);
                    }
                    else if(functions.isValueNull(ICON_VECTOR_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.ICON_VECTOR_COLOR.name(),ICON_VECTOR_COLOR);
                    }

                    if(!functions.isValueNull(MAILBACKGROUNDCOLOR) && presentTemplateDetails.containsKey(TemplatePreference.MAILBACKGROUNDCOLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.MAILBACKGROUNDCOLOR.name(),MAILBACKGROUNDCOLOR);
                    }
                    else if(functions.isValueNull(MAILBACKGROUNDCOLOR) && presentTemplateDetails.containsKey(TemplatePreference.MAILBACKGROUNDCOLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.MAILBACKGROUNDCOLOR.name(),MAILBACKGROUNDCOLOR);
                    }
                    else if(functions.isValueNull(MAILBACKGROUNDCOLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.MAILBACKGROUNDCOLOR.name(),MAILBACKGROUNDCOLOR);
                    }

                    if(!functions.isValueNull(MAIL_PANELHEADING_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.MAIL_PANELHEADING_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.MAIL_PANELHEADING_COLOR.name(),MAIL_PANELHEADING_COLOR);
                    }
                    else if(functions.isValueNull(MAIL_PANELHEADING_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.MAIL_PANELHEADING_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.MAIL_PANELHEADING_COLOR.name(),MAIL_PANELHEADING_COLOR);
                    }
                    else if(functions.isValueNull(MAIL_PANELHEADING_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.MAIL_PANELHEADING_COLOR.name(),MAIL_PANELHEADING_COLOR);
                    }

                    if(!functions.isValueNull(MAIL_HEADPANELFONT_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.MAIL_HEADPANELFONT_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.MAIL_HEADPANELFONT_COLOR.name(),MAIL_HEADPANELFONT_COLOR);
                    }
                    else if(functions.isValueNull(MAIL_HEADPANELFONT_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.MAIL_HEADPANELFONT_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.MAIL_HEADPANELFONT_COLOR.name(),MAIL_HEADPANELFONT_COLOR);
                    }
                    else if(functions.isValueNull(MAIL_HEADPANELFONT_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.MAIL_HEADPANELFONT_COLOR.name(),MAIL_HEADPANELFONT_COLOR);
                    }

                    if(!functions.isValueNull(MAIL_BODYPANELFONT_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.MAIL_BODYPANELFONT_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.MAIL_BODYPANELFONT_COLOR.name(),MAIL_BODYPANELFONT_COLOR);
                    }
                    else if(functions.isValueNull(MAIL_BODYPANELFONT_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.MAIL_BODYPANELFONT_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.MAIL_BODYPANELFONT_COLOR.name(),MAIL_BODYPANELFONT_COLOR);
                    }
                    else if(functions.isValueNull(MAIL_BODYPANELFONT_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.MAIL_BODYPANELFONT_COLOR.name(),MAIL_BODYPANELFONT_COLOR);
                    }



                    /*if(!functions.isValueNull(CHECKOUTBODYNFOOTER_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.name(),CHECKOUTBODYNFOOTER_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTBODYNFOOTER_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.name(),CHECKOUTBODYNFOOTER_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTBODYNFOOTER_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.name(),CHECKOUTBODYNFOOTER_COLOR);
                    }

                    if(!functions.isValueNull(CHECKOUTHEADERBACKGROUND_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.name(),CHECKOUTHEADERBACKGROUND_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTHEADERBACKGROUND_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.name(),CHECKOUTHEADERBACKGROUND_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTHEADERBACKGROUND_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.name(),CHECKOUTHEADERBACKGROUND_COLOR);
                    }


                    if(!functions.isValueNull(CHECKOUTNAVIGATIONBAR_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.name(),CHECKOUTNAVIGATIONBAR_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTNAVIGATIONBAR_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.name(),CHECKOUTNAVIGATIONBAR_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTNAVIGATIONBAR_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.name(),CHECKOUTNAVIGATIONBAR_COLOR);
                    }


                    if(!functions.isValueNull(CHECKOUTBUTTON_FONT_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.name(),CHECKOUTBUTTON_FONT_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTBUTTON_FONT_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.name(),CHECKOUTBUTTON_FONT_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTBUTTON_FONT_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.name(),CHECKOUTBUTTON_FONT_COLOR);
                    }



                    if(!functions.isValueNull(CHECKOUTHEADER_FONT_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.name(),CHECKOUTHEADER_FONT_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTHEADER_FONT_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.name(),CHECKOUTHEADER_FONT_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTHEADER_FONT_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.name(),CHECKOUTHEADER_FONT_COLOR);
                    }


                    if(!functions.isValueNull(CHECKOUTFULLBACKGROUND_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.name(),CHECKOUTFULLBACKGROUND_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTFULLBACKGROUND_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.name(),CHECKOUTFULLBACKGROUND_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTFULLBACKGROUND_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.name(),CHECKOUTFULLBACKGROUND_COLOR);
                    }


                    if(!functions.isValueNull(CHECKOUTLABEL_FONT_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.name(),CHECKOUTLABEL_FONT_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTLABEL_FONT_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.name(),CHECKOUTLABEL_FONT_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTLABEL_FONT_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.name(),CHECKOUTLABEL_FONT_COLOR);
                    }



                    if(!functions.isValueNull(CHECKOUTNAVIGATIONBAR_FONT_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name(),CHECKOUTNAVIGATIONBAR_FONT_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTNAVIGATIONBAR_FONT_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name(),CHECKOUTNAVIGATIONBAR_FONT_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTNAVIGATIONBAR_FONT_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name(),CHECKOUTNAVIGATIONBAR_FONT_COLOR);
                    }


                    if(!functions.isValueNull(CHECKOUTBUTTON_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.name(),CHECKOUTBUTTON_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTBUTTON_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.name(),CHECKOUTBUTTON_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTBUTTON_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.name(),CHECKOUTBUTTON_COLOR);
                    }


                    if(!functions.isValueNull(CHECKOUTICON_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.name(),CHECKOUTICON_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTICON_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.name(),CHECKOUTICON_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTICON_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.name(),CHECKOUTICON_COLOR);
                    }

                    if(!functions.isValueNull(CHECKOUTTIMER_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.name(),CHECKOUTTIMER_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTTIMER_COLOR) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.name(),CHECKOUTTIMER_COLOR);
                    }
                    else if(functions.isValueNull(CHECKOUTTIMER_COLOR))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.name(),CHECKOUTTIMER_COLOR);
                    }

                    if(!functions.isValueNull(CHECKOUTBOX_SHADOW) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.name()))
                    {
                        merchantTemplateInformationDelete.put(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.name(),CHECKOUTBOX_SHADOW);
                    }
                    else if(functions.isValueNull(CHECKOUTBOX_SHADOW) && presentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.name()))
                    {
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.name(),CHECKOUTBOX_SHADOW);
                    }
                    else if(functions.isValueNull(CHECKOUTBOX_SHADOW))
                    {
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.name(), CHECKOUTBOX_SHADOW);

                    }*/
                        if (result > 0)
                    {
                        updRecs++;
                    }

                    boolean isSaved=partnerManager.insertMemberTemplateDetails(merchantTemplateInformationInsert,memberids);
                    boolean isUpdated=partnerManager.updateMemberTemplateDetails(merchantTemplateInformationUpdate,memberids);
                    boolean isDeleted = partnerManager.deleteMemberTemplateDetails(merchantTemplateInformationDelete,memberids);
                    logger.debug("IsSaved::"+isSaved+" isUpdated::"+isUpdated+"isDeleted::"+isDeleted);

                }

            }
            catch (Exception e)
            {
                logger.error("Error while set reserves :",e);
                req.setAttribute("error",errorMsg);
                String redirectpage = "/transactionsprocess.jsp?ctoken="+user.getCSRFToken();
                req.setAttribute("cbmessage", sSuccessMessage.toString());
                RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
                rd.forward(req, res);
                return;
            }
            finally
            {
                Database.closePreparedStatement(pstmt);
                Database.closeConnection(cn);
            }
        }
        sSuccessMessage.append(updRecs).append(" Records Updated");
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());

        req.setAttribute("errormessage",errorMsg);
        String redirectpage = "/transactionsprocess.jsp?ctoken="+user.getCSRFToken();
        req.setAttribute("cbmessage", sSuccessMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);

        /*}*/
    }
}
