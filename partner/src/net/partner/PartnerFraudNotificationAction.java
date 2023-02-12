package net.partner;

import com.directi.pg.AuditTrailVO;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Mail.MailService;
import com.payment.PaymentProcessFactory;
import com.payment.request.PZRefundRequest;
import com.payment.response.PZRefundResponse;
import com.payment.response.PZResponseStatus;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ajit on 28/11/2018.
 */
public class PartnerFraudNotificationAction extends HttpServlet
{
    private static Logger logger = new Logger(PartnerFraudNotificationAction.class.getName());
    Connection conn=null;
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partnerFunctions = new PartnerFunctions();
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        int count = 1;
        String responseMessage = "";

        if (!partnerFunctions.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/logout.jsp");
            return;
        }
        String action = req.getParameter("submitbutton");
        String toId = req.getParameter("toid");
        String toid = "";
        String reason=null;
        String[] trackingIds =null;

        if (req.getParameterValues("trackingid") != null)
        {
            trackingIds = req.getParameterValues("trackingid");
        }
        else
        {
            sErrorMessage.append("Invalid TransactionID.");
            req.setAttribute("error", sErrorMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/partner/net/PartnerFraudNotification?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        if (Functions.checkArrayNull(trackingIds) == null)
        {
            sErrorMessage.append("Select at least one transaction.");
            req.setAttribute("error", sErrorMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/partner/net/PartnerFraudNotification?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        sErrorMessage.append("<center><table border=1 style=\"width: 50%; margin: auto;\">");
        sErrorMessage.append("<tr style=\"background-color: #7eccad\">");
        sErrorMessage.append("<td align=center> Sr. No.  </td>");
        sErrorMessage.append("<td align=center> MemberID  </td>");
        sErrorMessage.append("<td align=center> TrackingID </td>");
        sErrorMessage.append("<td align=center> Status </td>");
        sErrorMessage.append("<td align=center> Remark </td>");
        sErrorMessage.append("</tr>");

        MailService mailService = new MailService();
        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
        TransactionManager transactionManager = new TransactionManager();
        List<String> trackingId= Arrays.asList(trackingIds);
        List<TransactionDetailsVO> list = null;
        String trackingid = "";

        if("Fraud Intimation".equalsIgnoreCase(action))
        {
            try
            {
                list = transactionManager.getTransactionDetails(trackingId);
                HashMap rfMail = new HashMap();
                for(TransactionDetailsVO transactionDetailsVO :list)
                {
                    toid = transactionDetailsVO.getToid();
                    if (req.getParameter("reason_" +transactionDetailsVO.getTrackingid() ) != null && !req.getParameter("reason_" + transactionDetailsVO.getTrackingid()).equals("0"))
                    {
                        reason = req.getParameter("reason_" + transactionDetailsVO.getTrackingid());
                    }
                    sErrorMessage.append(getMessage(count, toid, transactionDetailsVO.getTrackingid(), "Successful", "Mail Send Successful"));
                    count++;
                }

                rfMail.put(MailPlaceHolder.TOID, toid);
                rfMail.put(MailPlaceHolder.MULTIPALTRANSACTION, mailService.getDetailTableForMultipleTrans(list, action, reason));
                asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.FRAUD_NOTIFICATION, rfMail);
            }
            catch (Exception e)
            {
                sErrorMessage.append(getMessage(count, toId, String.valueOf(trackingId), "Failed", "Mail Send Failed."));
            }
        }
        else if("Fraud Reversal & Intimation".equalsIgnoreCase(action))
        {
            PZRefundRequest refundRequest =null;
            PZRefundResponse response =null;
            AuditTrailVO auditTrailVO =null;
            Functions functions = new Functions();
            try
            {
                list = transactionManager.getTransactionDetailsForCommon(trackingId);
                for(TransactionDetailsVO transactionDetailsVO1:list)
                {
                    try
                    {

                        trackingid = transactionDetailsVO1.getTrackingid();
                        toid = transactionDetailsVO1.getToid();
                        if (req.getParameter("reason_" + transactionDetailsVO1.getTrackingid()) != null && !req.getParameter("reason_" + transactionDetailsVO1.getTrackingid()).equals("0"))
                        {
                            reason = req.getParameter("reason_" + transactionDetailsVO1.getTrackingid());
                        }
                        refundRequest = new PZRefundRequest();

                        String sCaptureAmount = transactionDetailsVO1.getCaptureAmount();

                        AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(transactionDetailsVO1.getAccountId()));

                        if (functions.isValueNull(transactionDetailsVO1.getCaptureAmount()) && functions.isValueNull(transactionDetailsVO1.getRefundAmount()))
                            sCaptureAmount = String.valueOf((Double.parseDouble(transactionDetailsVO1.getCaptureAmount()) - Double.parseDouble(transactionDetailsVO1.getRefundAmount())));


                        refundRequest.setAccountId(Integer.valueOf(transactionDetailsVO1.getAccountId()));
                        refundRequest.setTrackingId(Integer.valueOf(transactionDetailsVO1.getTrackingid()));
                        refundRequest.setMemberId(Integer.parseInt(transactionDetailsVO1.getToid()));
                        refundRequest.setRefundAmount(sCaptureAmount);
                        refundRequest.setRefundReason(reason);
                        refundRequest.setIpAddress(transactionDetailsVO1.getIpAddress());
                        refundRequest.setCaptureAmount(transactionDetailsVO1.getCaptureAmount());
                        refundRequest.setCurrency(transactionDetailsVO1.getCurrency());

                        auditTrailVO = new AuditTrailVO();
                        auditTrailVO.setActionExecutorId(transactionDetailsVO1.getToid());
                        auditTrailVO.setActionExecutorName(transactionDetailsVO1.getName());
                        refundRequest.setAuditTrailVO(auditTrailVO);
                        refundRequest.setTransactionStatus(transactionDetailsVO1.getStatus());
                        refundRequest.setReversedAmount(transactionDetailsVO1.getRefundAmount());

                        //auditTrailVO.setActionExecutorName(user.getRoles().toString());
                        PZRefundResponse refundResponse = null;
                        PZResponseStatus responseStatus = null;
                        refundRequest.setAuditTrailVO(auditTrailVO);
                        refundRequest.setFraud(true);

                        refundResponse = paymentProcess.refund(refundRequest);
                        responseStatus = refundResponse.getStatus();

                        sErrorMessage.append(getMessage(count, transactionDetailsVO1.getToid(), transactionDetailsVO1.getTrackingid(), "Successful", "Refund Successful"));
                    }
                    catch (Exception e)
                    {
                        sErrorMessage.append(getMessage(count, toid,trackingid,"Failed","Refund Failed"));
                    }
                    count++;
                }
                HashMap rfMail = new HashMap();
                rfMail.put(MailPlaceHolder.TOID, toid);
                rfMail.put(MailPlaceHolder.MULTIPALTRANSACTION, mailService.getDetailTableForMultipleTrans(list, action, reason));
                asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.FRAUD_NOTIFICATION, rfMail);
            }
            catch (Exception e)
            {
                logger.debug("exception::::"+e);
            }
        }
        sErrorMessage.append("</table>");
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("successmsg", chargeBackMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher("/fraudNotification.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(req, res);
    }
    public String getMessage(int count, String memberId,String trackingId, String status, String statusMsg)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<tr>");
        sb.append("<td align=center>" + count + "</td>");
        sb.append("<td align=center>" + memberId + "</td>");
        sb.append("<td align=center>" + trackingId + "</td>");
        sb.append("<td align=center>" + status + "</td>");
        sb.append("<td align=center>" + statusMsg + "</td>");
        sb.append("</tr>");
        return sb.toString();
    }
}

