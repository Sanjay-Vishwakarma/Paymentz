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
 * Created by Sanjay on 2/24/2022.
 */
public class CheckoutConfiguration extends HttpServlet
{

    private static Logger log = new Logger(CheckoutConfiguration.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException ,ServletException
    {
        doPost(req, res);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException ,ServletException
    {


        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        PartnerFunctions partner = new PartnerFunctions();
        Functions functions = new Functions();
        ActivityTrackerVOs activityTrackerVOs = new ActivityTrackerVOs();

        String actionExecutorId=(String) session.getAttribute("merchantid");

        /*String activityrole="";
        String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
        List<String> rolelist = Arrays.asList(Roles.split("\\s*,\\s*"));
        if(rolelist.contains("subpartner"))
        {
            activityrole= ActivityLogParameters.SUBPARTNER.toString();
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

        String errorMsg = "";
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }


        String EOL = "<br>";
        String memberids = req.getParameter("memberid");
        String NEW_CHECKOUT_BODYNFOOTER_COLOR = "";
        String NEW_CHECKOUT_HEADERBACKGROUND_COLOR = "";
        String NEW_CHECKOUT_NAVIGATIONBAR_COLOR = "";
        String NEW_CHECKOUT_BUTTON_FONT_COLOR = "";
        String NEW_CHECKOUT_HEADER_FONT_COLOR = "";
        String NEW_CHECKOUT_FULLBACKGROUND_COLOR = "";
        String NEW_CHECKOUT_LABEL_FONT_COLOR = "";
        String NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR = "";
        String NEW_CHECKOUT_BUTTON_COLOR = "";
        String NEW_CHECKOUT_ICON_COLOR = "";
        String NEW_CHECKOUT_TIMER_COLOR = "";
        String NEW_CHECKOUT_FOOTER_FONT_COLOR = "";
        String NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR = "";
        String NEW_CHECKOUT_BOX_SHADOW = "";

        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();

        String onchangedValues = req.getParameter("onchangedvalue");
        String isPharma = req.getParameter("isPharma");
        String isPowerdBy = req.getParameter("isPoweredBy");
        String template = req.getParameter("template");
        String ismerchantlogo = req.getParameter("ismerchantlogo");
        String ispcilogo = req.getParameter("ispcilogo");
        String isSecurityLogo = req.getParameter("isSecurityLogo");
        String checkoutTimer = req.getParameter("checkoutTimer");
        String checkoutTimerTime = req.getParameter("checkoutTimerTime");
        String vbvLogo = req.getParameter("vbvLogo");
        String isMerchantLogoBO= req.getParameter("isMerchantLogoBO");
        String masterSecureLogo = req.getParameter("masterSecureLogo");
        String consent = req.getParameter("consent");
        String isPartnerLogo = req.getParameter("ispartnerlogo");
        String isSupport = req.getParameter("ispartnerlogo");
        String supportSection = req.getParameter("supportSection");
        String supportNoNeeded= req.getParameter("supportNoNeeded");



        if (!ESAPI.validator().isValidInput("checkoutTimerTime", req.getParameter("checkoutTimerTime"), "checkoutTimer", 5, false)||req.getParameter("checkoutTimerTime").equals("0:00")||req.getParameter("checkoutTimerTime").equals("00:00")||req.getParameter("checkoutTimerTime").equals("00:0"))
        {
            errorMsg = errorMsg + "Invalid Checkout Timer Time,Accepts only in [mm:ss]" + EOL;
        }
        else
        {
            checkoutTimerTime = req.getParameter("checkoutTimerTime");
        }
        if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR)),req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR)),"colorcode", 8, false))
        {
            errorMsg = errorMsg + "Invalid Body & Footer Color" + EOL;
        }
        else
            NEW_CHECKOUT_BODYNFOOTER_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR));

        if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR)),req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR)),"colorcode", 8, false))
            errorMsg = errorMsg + "Invalid Header Background Color" + EOL;
        else
            NEW_CHECKOUT_HEADERBACKGROUND_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR));

        if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR)),req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR)),"colorcode", 8, false))
            errorMsg = errorMsg + "Invalid NavigationBar Color" + EOL;
        else
            NEW_CHECKOUT_NAVIGATIONBAR_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR));

        if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR)), "colorcode", 8, false))
            errorMsg = errorMsg + "Invalid Button Font Color" + EOL;
        else
            NEW_CHECKOUT_BUTTON_FONT_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR));

        if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR)), "colorcode", 8, false))
            errorMsg = errorMsg + "Invalid Header Font Color" + EOL;
        else
            NEW_CHECKOUT_HEADER_FONT_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR));

        if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR)), "colorcode", 8, false))
            errorMsg = errorMsg + "Invalid Full Background Color" + EOL;
        else
            NEW_CHECKOUT_FULLBACKGROUND_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR));

        if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR)), "colorcode", 8, false))
            errorMsg = errorMsg + "Invalid Label Font Color" + EOL;
        else
            NEW_CHECKOUT_LABEL_FONT_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR));

        if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR)), "colorcode", 8, false))
            errorMsg = errorMsg + "Invalid NavigationBar Font Color" + EOL;
        else
            NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR));

        if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR)), "colorcode", 8, false))
            errorMsg = errorMsg + "Invalid Botton Color" + EOL;
        else
            NEW_CHECKOUT_BUTTON_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR));

        if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_ICON_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_ICON_COLOR)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_ICON_COLOR)), "colorcode", 8, false))
            errorMsg = errorMsg + "Invalid Icon Color" + EOL;
        else
            NEW_CHECKOUT_ICON_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_ICON_COLOR));

        if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR)), "colorcode", 8, false))
            errorMsg = errorMsg + "Invalid Timer Color" + EOL;
        else
            NEW_CHECKOUT_TIMER_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR));

        if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR)), "colorcode", 8, false))
            errorMsg = errorMsg + "Invalid Footer Font Color" + EOL;
        else
            NEW_CHECKOUT_FOOTER_FONT_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR));

        if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR)), "colorcode", 8, false))
            errorMsg = errorMsg + "Invalid Footer Background Color" + EOL;
        else
            NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR));

        if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW)), "colorcode", 8, false))
            errorMsg = errorMsg + "Invalid Box Shadow" + EOL;
        else
            NEW_CHECKOUT_BOX_SHADOW = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW));


        if(!partner.isEmptyOrNull(errorMsg))
        {
            String redirectpage = "/checkoutConfiguration.jsp?ctoken="+user.getCSRFToken();
            req.setAttribute("error", errorMsg);
            RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
            rd.forward(req, res);
            return;
        }



        StringBuilder query = new StringBuilder();
        res.setContentType("text/html");
        int updRecs = 0;
        Connection cn=null;
        PreparedStatement pstmt = null;
        if (memberids != null)
        {
            try
            {

                for (int i = 0; i < 1; i++)
                {
                    Map<String,Object> merchantTemplateInformationUpdate=new HashMap<String, Object>();
                    Map<String,Object> merchantTemplateInformationInsert=new HashMap<String, Object>();
                    Map<String,Object> merchantTemplateInformationDelete=new HashMap<String, Object>();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    String d = dateFormat.format(date);
                    cn= Database.getConnection();
                    query.append("update members m JOIN merchant_configuration mc ON m.memberid=mc.memberid  set isPharma=?,template=?, ismerchantlogo=?, ispcilogo=?, isPartnerLogo=?,vbvLogo=?, masterSecureLogo=?, consent=?,isSecurityLogo=?,checkoutTimer=?,checkoutTimerTime=?,isPoweredBy=?,supportSection=?,supportNoNeeded=?");

                    query.append(" where m.memberid=?");
                    pstmt= cn.prepareStatement(query.toString());

                    pstmt.setString(1,isPharma);
                    pstmt.setString(2,template);
                    pstmt.setString(3,ismerchantlogo);
                    pstmt.setString(4,ispcilogo);
                    pstmt.setString(5,isPartnerLogo);
                    pstmt.setString(6,vbvLogo);
                    pstmt.setString(7,masterSecureLogo);
                    pstmt.setString(8,consent);
                    pstmt.setString(9,isSecurityLogo);
                    pstmt.setString(10,checkoutTimer);
                    pstmt.setString(11,checkoutTimerTime);
                    /*pstmt.setString(12,isMerchantLogoBO);*/
                    pstmt.setString(12,isPowerdBy);
                    pstmt.setString(13,supportSection);
                    pstmt.setString(14,supportNoNeeded);
                    pstmt.setString(15,memberids);

                    int result = pstmt.executeUpdate();

                    log.debug("Creating Activity for edit Merchant Backoffice Access");
                    String remoteAddr = Functions.getIpAddress(req);
                    int serverPort = req.getServerPort();
                    String servletPath = req.getServletPath();
                    String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
                    if(functions.isValueNull(onchangedValues))
                    {
                        activityTrackerVOs.setInterface(ActivityLogParameters.PARTNER.toString());
                        activityTrackerVOs.setUser_name(Login + "-" + actionExecutorId);
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
                            log.error("Exception while AsyncActivityLog::::", e);
                        }
                    }
                    PartnerManager partnerManager = new PartnerManager();

                    Map<String,Object> merchantPresentTemplateDetails=partnerManager.getSavedMemberTemplateDetails(memberids);

                    if (NEW_CHECKOUT_BODYNFOOTER_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.name()))
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.name(), NEW_CHECKOUT_BODYNFOOTER_COLOR);
                    else if (NEW_CHECKOUT_BODYNFOOTER_COLOR != null)
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.name(), NEW_CHECKOUT_BODYNFOOTER_COLOR);

                    if (NEW_CHECKOUT_HEADERBACKGROUND_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.name()))
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.name(), NEW_CHECKOUT_HEADERBACKGROUND_COLOR);
                    else if (functions.isValueNull(NEW_CHECKOUT_HEADERBACKGROUND_COLOR))
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.name(), NEW_CHECKOUT_HEADERBACKGROUND_COLOR);

                    if (NEW_CHECKOUT_NAVIGATIONBAR_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.name()))
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.name(), NEW_CHECKOUT_NAVIGATIONBAR_COLOR);
                    else if (NEW_CHECKOUT_NAVIGATIONBAR_COLOR != null)
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.name(), NEW_CHECKOUT_NAVIGATIONBAR_COLOR);

                    if (NEW_CHECKOUT_BUTTON_FONT_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.name()))
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.name(), NEW_CHECKOUT_BUTTON_FONT_COLOR);
                    else if (NEW_CHECKOUT_BUTTON_FONT_COLOR != null)
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.name(), NEW_CHECKOUT_BUTTON_FONT_COLOR);

                    if (NEW_CHECKOUT_HEADER_FONT_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.name()))
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.name(), NEW_CHECKOUT_HEADER_FONT_COLOR);
                    else if (NEW_CHECKOUT_HEADER_FONT_COLOR != null)
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.name(), NEW_CHECKOUT_HEADER_FONT_COLOR);

                    if (NEW_CHECKOUT_FULLBACKGROUND_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.name()))
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.name(), NEW_CHECKOUT_FULLBACKGROUND_COLOR);
                    else if (NEW_CHECKOUT_FULLBACKGROUND_COLOR != null)
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.name(), NEW_CHECKOUT_FULLBACKGROUND_COLOR);

                    if (NEW_CHECKOUT_LABEL_FONT_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.name()))
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.name(), NEW_CHECKOUT_LABEL_FONT_COLOR);
                    else if (NEW_CHECKOUT_LABEL_FONT_COLOR != null)
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.name(), NEW_CHECKOUT_LABEL_FONT_COLOR);

                    if (NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name()))
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name(), NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR);
                    else if (NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR != null)
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name(), NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR);

                    if (NEW_CHECKOUT_BUTTON_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.name()))
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.name(), NEW_CHECKOUT_BUTTON_COLOR);
                    else if (NEW_CHECKOUT_BUTTON_COLOR != null)
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.name(), NEW_CHECKOUT_BUTTON_COLOR);

                    if (NEW_CHECKOUT_ICON_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.name()))
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.name(), NEW_CHECKOUT_ICON_COLOR);
                    else if (NEW_CHECKOUT_ICON_COLOR != null)
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.name(), NEW_CHECKOUT_ICON_COLOR);

                    if (NEW_CHECKOUT_TIMER_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.name()))
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.name(), NEW_CHECKOUT_TIMER_COLOR);
                    else if (NEW_CHECKOUT_TIMER_COLOR != null)
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.name(), NEW_CHECKOUT_TIMER_COLOR);

                    if (NEW_CHECKOUT_FOOTER_FONT_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.name()))
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.name(), NEW_CHECKOUT_FOOTER_FONT_COLOR);
                    else if (NEW_CHECKOUT_FOOTER_FONT_COLOR != null)
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.name(), NEW_CHECKOUT_FOOTER_FONT_COLOR);

                    if (NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.name()))
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.name(), NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR);
                    else if (NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR != null)
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.name(), NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR);

                    if (NEW_CHECKOUT_BOX_SHADOW != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.name()))
                        merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.name(), NEW_CHECKOUT_BOX_SHADOW);
                    else if (NEW_CHECKOUT_BOX_SHADOW != null)
                        merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.name(), NEW_CHECKOUT_BOX_SHADOW);


                    if (result > 0)
                    {
                        updRecs++;
                    }

                    boolean isSaved=partnerManager.insertMemberTemplateDetails(merchantTemplateInformationInsert,memberids);
                    boolean isUpdated=partnerManager.updateMemberTemplateDetails(merchantTemplateInformationUpdate,memberids);
//                    boolean isDeleted = partnerManager.deleteMemberTemplateDetails(merchantTemplateInformationDelete,memberids);
                    log.debug("IsSaved::"+isSaved+" isUpdated::"+isUpdated);

                }

            }
            catch (Exception e)
            {
                log.error("Error while set reserves :",e);
                req.setAttribute("error",errorMsg);
                String redirectpage = "/checkoutConfiguration.jsp?ctoken="+user.getCSRFToken();
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

            sSuccessMessage.append(updRecs).append(" Records Updated");
            StringBuilder chargeBackMessage = new StringBuilder();
            chargeBackMessage.append(sSuccessMessage.toString());
            chargeBackMessage.append("<BR/>");
            chargeBackMessage.append(sErrorMessage.toString());

            req.setAttribute("errormessage",errorMsg);
            String redirectpage = "/checkoutConfiguration.jsp?ctoken="+user.getCSRFToken();
            req.setAttribute("cbmessage", sSuccessMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
            rd.forward(req, res);

        }
    }
}
