package net.partner;

import com.directi.pg.AsyncActivityTracker;
import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.dao.FraudTransactionDAO;
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
 * Date: 4/15/15
 * Time: 3:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class SetReservesFraud extends HttpServlet
{
    private static Logger logger = new Logger(SetReservesFraud.class.getName());
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

        String errorMsg = "";
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        if(!partner.isEmptyOrNull(errorMsg))
        {

            String redirectPage = "/merchantfraudsetting.jsp?ctoken="+user.getCSRFToken();
            req.setAttribute("error", errorMsg);
            RequestDispatcher rd = req.getRequestDispatcher(redirectPage);
            rd.forward(req, res);
            return;
        }
        else
        {

            StringBuilder sSuccessMessage = new StringBuilder();
            StringBuilder sFailMessage = new StringBuilder();
            StringBuilder query = new StringBuilder();

            String memberIds[] = req.getParameterValues("memberid");

            String maxScoreAllowed = req.getParameter("maxscoreallowed");
            String maxScoreReversal = req.getParameter("maxscoreautoreversal");
            String onlineFraudCheck =req.getParameter("onlineFraudCheck");
            String internalFraudCheck =req.getParameter("internalFraudCheck");
            String onchangedValues = req.getParameter("onchangedvalue");

            int updRecs = 0;
            int updFail = 0;
            Connection cn=null;
            PreparedStatement pstmt = null;
            if (memberIds != null)
            { //process only if there is at least one record to be updated
                try
                {
                    //int size = reserves.length;
                    for (int i = 0; i < 1; i++)
                    {

                        if(functions.isNumericVal(maxScoreAllowed) && functions.isNumericVal(maxScoreReversal))
                        {
                            cn = Database.getConnection();
                            query.append("update members set maxScoreAllowed=?,maxScoreAutoReversal=?,onlineFraudCheck=?,internalFraudCheck=? where memberid=?");
                            pstmt = cn.prepareStatement(query.toString());
                            pstmt.setString(1, maxScoreAllowed);
                            pstmt.setString(2, maxScoreReversal);
                            pstmt.setString(3, onlineFraudCheck);
                            pstmt.setString(4, internalFraudCheck);
                            pstmt.setString(5, memberIds[i]);
                            logger.debug("query " + query.toString());
                            int result = pstmt.executeUpdate();
                            logger.debug("result update is :::"+result);
                            logger.debug("Creating Activity for edit Merchant Fraud Setting");
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
                                activityTrackerVOs.setModule_name(ActivityLogParameters.MERCHANT_FRAUD_SETTING.toString());
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
                            if (result > 0){
                                updRecs++;
                                FraudTransactionDAO.loadFraudTransactionThresholdForMerchant();
                            }
                            else
                            {
                                updFail++;
                            }
                        }
                        else
                        {
                            updFail++;
                        }
                    }//end for
                    if(updFail > 0){
                        sFailMessage.append("Invalid max score allowed/ max score reversal");
                    }
                    if(updRecs > 0){
                        sSuccessMessage.append(updRecs).append(" Records Updated");
                    }
                }
                catch (Exception e){
                    logger.error("Exception:::::", e);
                    req.setAttribute("error","Internal error while processing your request");

                }//try catch ends
                finally
                {
                    Database.closePreparedStatement(pstmt);
                    Database.closeConnection(cn);
                }
            }
            String redirectPage = "/merchantfraudsetting.jsp?ctoken="+user.getCSRFToken();
            req.setAttribute("failed",sFailMessage);
            req.setAttribute("success",sSuccessMessage);
            RequestDispatcher rd = req.getRequestDispatcher(redirectPage);
            rd.forward(req, res);
        }
    }

}
