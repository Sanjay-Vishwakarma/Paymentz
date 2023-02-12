package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
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
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/15/15
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class SetReservesMailSetting extends HttpServlet
{
    private static Logger logger = new Logger(SetReservesMailSetting.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        logger.debug("Entering in SetReserves");
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner=new PartnerFunctions();
        logger.debug("success");

        String errorMsg = "";
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        if(!partner.isEmptyOrNull(errorMsg))
        {

            String redirectpage = "/merchantmailaccess.jsp?ctoken="+user.getCSRFToken();
            req.setAttribute("error", errorMsg);
            RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
            rd.forward(req, res);
            return;
        }
        else
        {
            String memberId=req.getParameter("memberid");
            String accountId = req.getParameter("accountids");
            String isValidateEmail = req.getParameter("isValidateEmail");
            /*String customerReminderMail = req.getParameter("custremindermail");*/
            /*String isEmailSent = req.getParameter("emailSent");*/
            /*String isrefundmailsent = req.getParameter("isRefundEmailSent");*/
            /*String chargebackEmail  = req.getParameter("chargebackEmail");*/
            String merchantRegistrationMail= req.getParameter("merchantRegistrationMail");
            String merchantChangePassword=req.getParameter("merchantChangePassword");
            String merchantChangeProfile=req.getParameter("merchantChangeProfile");
            String transactionSuccessfulMail=req.getParameter("transactionSuccessfulMail");
            String transactionFailMail=req.getParameter("transactionFailMail");
            String transactionCapture=req.getParameter("transactionCapture");
            String transactionPayoutSuccess=req.getParameter("transactionPayoutSuccess");
            String transactionPayoutFail=req.getParameter("transactionPayoutFail");
            String refundMail=req.getParameter("refundMail");
            String chargebackMail=req.getParameter("chargebackMail");
            String transactionInvoice=req.getParameter("transactionInvoice");
            String cardRegistration=req.getParameter("cardRegistration");
            String payoutReport=req.getParameter("payoutReport");
            String highRiskRefunds=req.getParameter("highRiskRefunds");
            String fraudFailedTxn=req.getParameter("fraudFailedTxn");
            String dailyFraudReport=req.getParameter("dailyFraudReport");
            //Customer Notification Mail
            String customerTransactionSuccessfulMail=req.getParameter("customerTransactionSuccessfulMail");
            String customerTransactionFailMail=req.getParameter("customerTransactionFailMail");
            String customerTransactionPayoutSuccess=req.getParameter("customerTransactionPayoutSuccess");
            String customerTransactionPayoutFail=req.getParameter("customerTransactionPayoutFail");
            String customerRefundMail=req.getParameter("customerRefundMail");
            String customerTokenizationMail=req.getParameter("customerTokenizationMail");

            StringBuilder sSuccessMessage = new StringBuilder();
            StringBuilder sErrorMessage = new StringBuilder();

            StringBuilder query = new StringBuilder();

            res.setContentType("text/html");
            //PrintWriter out = res.getWriter();
            int updRecs = 0;
            Connection cn=null;
            PreparedStatement pstmt = null;
            if (memberId != null)
            { //process only if there is at least one record to be updated
                try
                {
                    //int size = reserves.length;

                        //GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                        //account.
                        //String reserve =  "", accountID = "";
                        String accountID = "";

                        String isValidEmail="N";
                        /*String custrememail="N";*/
                        /*String emailSent="N";*/
                        /*String isRefundEmailSent="N";*/
                        /*String chargebackEmailSent="N";*/

                        isValidEmail = isValidateEmail;
                        /*custrememail = customerReminderMail;*/
                        /*emailSent = isEmailSent;*/
                        /*isRefundEmailSent=isrefundmailsent;*/
                        /*chargebackEmailSent=chargebackEmail;*/
                        accountID =  Functions.checkStringNull(accountId);

                        if(isValidEmail==null)
                        {
                            isValidEmail="N";
                        }
                        /*if(custrememail==null)
                        {
                            custrememail="N";
                        }*/
                        /*if(emailSent==null)
                        {
                            emailSent="N";
                        }*/

                    /*if(isRefundEmailSent==null)
                    {
                        isRefundEmailSent="N";
                    }*/

                    /*if(chargebackEmailSent==null)
                    {
                        chargebackEmailSent="N";
                    }*/
                    if (merchantRegistrationMail == null)
                        merchantRegistrationMail = "N";

                    if (merchantChangePassword == null)
                        merchantChangePassword = "N";

                    if (merchantChangeProfile == null)
                        merchantChangeProfile = "N";

                    if (transactionSuccessfulMail == null)
                        transactionSuccessfulMail = "N";

                    if (transactionFailMail == null)
                        transactionFailMail = "N";

                    if (transactionCapture == null)
                        transactionCapture = "N";

                    if (transactionPayoutSuccess == null)
                        transactionPayoutSuccess = "N";

                    if (transactionPayoutFail == null)
                        transactionPayoutFail = "N";

                    if (refundMail == null)
                        refundMail = "N";

                    if (chargebackMail == null)
                        chargebackMail = "N";

                    if (transactionInvoice == null)
                        transactionInvoice = "N";

                    if (cardRegistration == null)
                        cardRegistration = "N";

                    if (payoutReport == null)
                        payoutReport = "N";

                    if (highRiskRefunds == null)
                        highRiskRefunds = "N";

                    if (fraudFailedTxn == null)
                        fraudFailedTxn = "N";

                    if (dailyFraudReport == null)
                        dailyFraudReport = "N";

                    if (customerTransactionSuccessfulMail == null)
                        customerTransactionSuccessfulMail = "N";

                    if (customerTransactionFailMail == null)
                        customerTransactionFailMail = "N";

                    if (customerTransactionPayoutSuccess == null)
                        customerTransactionPayoutSuccess = "N";

                    if (customerTransactionPayoutFail == null)
                        customerTransactionPayoutFail = "N";

                    if (customerRefundMail == null)
                        customerRefundMail = "N";

                    if (customerTokenizationMail == null)
                        customerTokenizationMail = "N";

                    try
                    {
                        cn= Database.getConnection();
                        query.append("update members m JOIN merchant_configuration mc ON m.memberid=mc.memberid set accountid=?,isValidateEmail=?,merchantRegistrationMail=?,merchantChangePassword=?,merchantChangeProfile=?,transactionSuccessfulMail=?,transactionFailMail=?,transactionCapture=?,transactionPayoutSuccess=?,transactionPayoutFail=?,refundMail=?,chargebackMail=?,transactionInvoice=?,cardRegistration=?,payoutReport=?,highRiskRefunds=?,fraudFailedTxn=?,dailyFraudReport=?,customerTransactionSuccessfulMail=?,customerTransactionFailMail=?,customerTransactionPayoutSuccess=?,customerTransactionPayoutFail=?,customerRefundMail=?,customerTokenizationMail=? WHERE m.memberid=?");
                        pstmt= cn.prepareStatement(query.toString());

                        pstmt.setString(1,accountID);
                        pstmt.setString(2,isValidEmail);
                        /*pstmt.setString(3,custrememail);*/
                        /*pstmt.setString(4,emailSent);*/
                        /*pstmt.setString(4,isRefundEmailSent);*/
                        /*pstmt.setString(5,chargebackEmailSent);*/
                        pstmt.setString(3, merchantRegistrationMail);
                        pstmt.setString(4, merchantChangePassword);
                        pstmt.setString(5, merchantChangeProfile);
                        pstmt.setString(6, transactionSuccessfulMail);
                        pstmt.setString(7, transactionFailMail);
                        pstmt.setString(8, transactionCapture);
                        pstmt.setString(9, transactionPayoutSuccess);
                        pstmt.setString(10, transactionPayoutFail);
                        pstmt.setString(11, refundMail);
                        pstmt.setString(12, chargebackMail);
                        pstmt.setString(13, transactionInvoice);
                        pstmt.setString(14, cardRegistration);
                        pstmt.setString(15, payoutReport);
                        pstmt.setString(16, highRiskRefunds);
                        pstmt.setString(17, fraudFailedTxn);
                        pstmt.setString(18, dailyFraudReport);
                        pstmt.setString(19, customerTransactionSuccessfulMail);
                        pstmt.setString(20, customerTransactionFailMail);
                        pstmt.setString(21, customerTransactionPayoutSuccess);
                        pstmt.setString(22, customerTransactionPayoutFail);
                        pstmt.setString(23, customerRefundMail);
                        pstmt.setString(24, customerTokenizationMail);
                        pstmt.setString(25,memberId);

                        logger.debug("result " + pstmt);
                        int result = pstmt.executeUpdate();


                        if (result > 0)
                        {
                            updRecs++;
                        }
                        logger.debug(updRecs);
                    }
                    catch (SQLException se)
                    {
                       // se.printStackTrace();
                        logger.debug("SQLException::::"+se);
                    }
                    //end for

                }
                catch (Exception e)
                {
                    //e.printStackTrace();
                    logger.error("Error while set reserves :",e);

                    //sErrorMessage.append(errorMsg);
                    req.setAttribute("error",errorMsg);


                }//try catch ends
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

            String redirectpage = "/merchantmailaccess.jsp?ctoken="+user.getCSRFToken();
            req.setAttribute("cbmessage", chargeBackMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
            rd.forward(req, res);
        }//post ends
    }

}
