package net.partner;

import com.directi.pg.AsyncActivityTracker;
import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.enums.ActivityLogParameters;
import com.manager.vo.ActivityTrackerVOs;
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
import java.util.Arrays;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/14/15
 * Time: 8:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class SetReservesBackOffice extends HttpServlet
{
    private static Logger logger = new Logger(SetReservesBackOffice.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner=new PartnerFunctions();
        Functions functions = new Functions();
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

        String memberIds[] = req.getParameterValues("memberid");
        String backOfficeAccess=req.getParameter("icici");

        String dashboardAccess =req.getParameter("dashboard_access");
        String accountingAccess =req.getParameter("accounting_access");
        String settingAccess =req.getParameter("setting_access");
        String transactionsAccess =req.getParameter("transactions_access");
        String invoicingAccess =req.getParameter("invoicing_access");
        String virtualTerminalAccess =req.getParameter("virtualterminal_access");
        String merchantMgtAccess =req.getParameter("merchantmgt_access");
        String isCardRegistrationAllowed=req.getParameter("iscardregistrationallowed");
        String isRecurring = req.getParameter("is_recurring");
        String isAppManagerActivate=req.getParameter("isappmanageractivate");

        String accountsAccountSummaryAccess=req.getParameter("accounts_account_summary_access");
        String accountsChargesSummaryAccess=req.getParameter("accounts_charges_summary_access");
        String accountsTransactionSummaryAccess=req.getParameter("accounts_transaction_summary_access");
        String accountsReportSummaryAccess=req.getParameter("accounts_reports_summary_access");

        String settingsMerchantProfileAccess=req.getParameter("settings_merchant_profile_access");
        String settingsOrganisationProfileAccess=req.getParameter("settings_organisation_profile_access");
        String settingsGenerateKeyAccess=req.getParameter("settings_generate_key_access");
        String settingsInvoiceConfigAccess=req.getParameter("settings_invoice_config_access");
        String settingsMerchantConfigAccess=req.getParameter("settings_merchant_config_access");
        String settingsFraudRuleConfigAccess=req.getParameter("settings_fraudrule_config_access");
        String settingsWhitelistDetails=req.getParameter("settings_whitelist_details");
        String settingsBlacklistDetails=req.getParameter("settings_blacklist_details");

        String transMgtTransactionAccess=req.getParameter("transmgt_transaction_access");
        String transMgtCaptureAccess=req.getParameter("transmgt_capture_access");
        String transMgtReversalAccess=req.getParameter("transmgt_reversal_access");
        String transMgtPayoutAccess=req.getParameter("transmgt_payout_access");

        String invoiceGenerateAccess=req.getParameter("invoice_generate_access");
        String invoiceHistoryAccess=req.getParameter("invoice_history_access");

        String tokenMgtRegistrationHistoryAccess=req.getParameter("tokenmgt_registration_history_access");
        String tokenMgtRegisterCardAccess=req.getParameter("tokenmgt_register_card_access");
        String rejected_transaction=req.getParameter("rejected_transaction");
        String virtual_checkout=req.getParameter("virtual_checkout");
        String onchangedValues = req.getParameter("onchangedvalue");
        String paybylink= req.getParameter("paybylink");
        String merchant_verify_otp= req.getParameter("merchant_verify_otp");
        String generateview= req.getParameter("generateview");


        String query = null;

        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();

        res.setContentType("text/html");
        //PrintWriter out = res.getWriter();
        int updRecs = 0;
        Connection cn=null;
        PreparedStatement pstmt = null;
        if (memberIds != null)
        { //process only if there is at least one record to be updated
            try
            {
                //int size = reserves.length;
                for (int i = 0; i < 1; i++)
                {
                    if(backOfficeAccess==null)
                    {
                        backOfficeAccess="N";
                    }

                    cn= Database.getConnection();
                    query ="update members m JOIN merchant_configuration mc ON m.memberid=mc.memberid set icici=?,dashboard_access =?,accounting_access =?,setting_access =?,transactions_access =?,invoicing_access =?,virtualterminal_access =?,merchantmgt_access =?,settings_fraudrule_config_access =?,settings_merchant_config_access =?,isappmanageractivate=?,iscardregistrationallowed=?,is_recurring=?,accounts_account_summary_access=?,accounts_charges_summary_access=?,accounts_transaction_summary_access=?,accounts_reports_summary_access=?,settings_merchant_profile_access=?,settings_organisation_profile_access=?, settings_checkout_page_access=?,settings_generate_key_access=?,settings_invoice_config_access=?,transmgt_transaction_access=?,transmgt_capture_access=?,transmgt_reversal_access=?,transmgt_payout_access=?,invoice_generate_access=?,invoice_history_access=?,tokenmgt_registration_history_access=?,tokenmgt_register_card_access=?,settings_whitelist_details=?,settings_blacklist_details=?,rejected_transaction=?,virtual_checkout=?,paybylink=?,mc.merchant_verify_otp=?,mc.generateview=? where m.memberid=?";
                    pstmt= cn.prepareStatement(query);
                    pstmt.setString(1,backOfficeAccess);
                    pstmt.setString(2,dashboardAccess);
                    pstmt.setString(3,accountingAccess);
                    pstmt.setString(4,settingAccess);
                    pstmt.setString(5,transactionsAccess);
                    pstmt.setString(6,invoicingAccess);
                    pstmt.setString(7,virtualTerminalAccess);
                    pstmt.setString(8,merchantMgtAccess);
                    pstmt.setString(9,settingsFraudRuleConfigAccess);
                    pstmt.setString(10,settingsMerchantConfigAccess);
                    pstmt.setString(11,isAppManagerActivate);
                    pstmt.setString(12,isCardRegistrationAllowed);
                    pstmt.setString(13,isRecurring);
                    pstmt.setString(14,accountsAccountSummaryAccess);
                    pstmt.setString(15,accountsChargesSummaryAccess);
                    pstmt.setString(16,accountsTransactionSummaryAccess);
                    pstmt.setString(17,accountsReportSummaryAccess);
                    pstmt.setString(18,settingsMerchantProfileAccess);
                    pstmt.setString(19,settingsOrganisationProfileAccess);
                    pstmt.setString(20,"N");
                    pstmt.setString(21,settingsGenerateKeyAccess);
                    pstmt.setString(22,settingsInvoiceConfigAccess);
                    pstmt.setString(23,transMgtTransactionAccess);
                    pstmt.setString(24,transMgtCaptureAccess);
                    pstmt.setString(25,transMgtReversalAccess);
                    pstmt.setString(26,transMgtPayoutAccess);
                    pstmt.setString(27,invoiceGenerateAccess);
                    pstmt.setString(28,invoiceHistoryAccess);
                    pstmt.setString(29,tokenMgtRegistrationHistoryAccess);
                    pstmt.setString(30,tokenMgtRegisterCardAccess);
                    pstmt.setString(31,settingsWhitelistDetails);
                    pstmt.setString(32,settingsBlacklistDetails);
                    pstmt.setString(33,rejected_transaction);
                    pstmt.setString(34,virtual_checkout);
                    pstmt.setString(35,paybylink);
                    pstmt.setString(36,merchant_verify_otp);
                    pstmt.setString(37,generateview);
                    pstmt.setString(38,memberIds[i]);

                    logger.debug("result " + query);
                    int result = pstmt.executeUpdate();
                    logger.debug("result update is :::"+result);
                    logger.debug("Creating Activity for edit Merchant Backoffice Access");
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
                        activityTrackerVOs.setModule_name(ActivityLogParameters.MERCHANT_BACKOFFICE_ACCESS.toString());
                        activityTrackerVOs.setLable_values(onchangedValues);
                        activityTrackerVOs.setDescription(ActivityLogParameters.MEMBERID.toString() + "-" + memberIds[i]);
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
                    if (result > 0)
                    {
                        updRecs++;
                    }
                }//end for

            }
            catch (Exception e)
            {
                logger.error("Exception:::::",e);
                req.setAttribute("error","Internal error while processing your request");
            }
            finally {
                Database.closePreparedStatement(pstmt);
                Database.closeConnection(cn);
            }
        }
        sSuccessMessage.append(updRecs).append(" Records Updated");
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());

        String redirectPage = "/backofficeaccess.jsp?ctoken="+user.getCSRFToken();
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        req.setAttribute("sSuccessMessage", sSuccessMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher(redirectPage);
        rd.forward(req, res);

    }
}
