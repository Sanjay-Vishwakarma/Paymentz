import com.directi.pg.*;
import com.manager.TransactionManager;
import com.manager.vo.MerchantDetailsVO;
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
import java.util.Hashtable;
import java.util.List;
/**
 * Created by Mahima on 1/10/2018
 */
public class FraudNotificationAction extends HttpServlet
{
    private static Logger logger = new Logger(FraudNotificationAction.class.getName());
    Connection conn=null;
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();

        if(!Admin.isLoggedIn(session)){
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String action = req.getParameter("submitbutton");
        String toId = req.getParameter("toid");
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
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/MarkFraudTransaction?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        if (Functions.checkArrayNull(trackingIds) == null)
        {
            sErrorMessage.append("Select at least one transaction.");
            req.setAttribute("error", sErrorMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/MarkFraudTransaction?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        sErrorMessage.append("<center><table border=1>");
        sErrorMessage.append("<tr>");
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
        MerchantDetailsVO merchantDetailsVO = null;

        if("Fraud Intimation".equalsIgnoreCase(action))
        {
            try
            {
                list = transactionManager.getTransactionDetails(trackingId);
                HashMap rfMail = new HashMap();
                rfMail.put(MailPlaceHolder.TOID, toId);
                rfMail.put(MailPlaceHolder.MULTIPALTRANSACTION, mailService.getDetailTableForMultipleTrans(list, action, reason));
                asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.FRAUD_NOTIFICATION, rfMail);

                for(TransactionDetailsVO transactionDetailsVO :list){
                    sErrorMessage.append(getMessage(toId, transactionDetailsVO.getTrackingid(), "Successful", "Mail Send Successful"));
                }
            }
            catch (Exception e)
            {
                sErrorMessage.append(getMessage(toId, String.valueOf(trackingId), "Failed", "Mail Send Failed."));
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
                    if (req.getParameter("reason_" +transactionDetailsVO1.getTrackingid() ) != null && !req.getParameter("reason_" + transactionDetailsVO1.getTrackingid()).equals("0"))
                    {
                        reason = req.getParameter("reason_" + transactionDetailsVO1.getTrackingid());
                    }
                    refundRequest=new PZRefundRequest();
                    AbstractPaymentProcess process = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(transactionDetailsVO1.getTrackingid()), Integer.parseInt(transactionDetailsVO1.getAccountId()));
                    refundRequest.setMemberId(Integer.valueOf(transactionDetailsVO1.getToid()));
                    refundRequest.setAccountId(Integer.parseInt(transactionDetailsVO1.getAccountId()));
                    refundRequest.setTrackingId(Integer.parseInt(transactionDetailsVO1.getTrackingid()));
                    String sCaptureAmount = transactionDetailsVO1.getCaptureAmount();

                    if(functions.isValueNull(transactionDetailsVO1.getCaptureAmount()) && functions.isValueNull(transactionDetailsVO1.getRefundAmount()))
                        sCaptureAmount = String.valueOf((Double.parseDouble(transactionDetailsVO1.getCaptureAmount())-Double.parseDouble(transactionDetailsVO1.getRefundAmount())));

                    refundRequest.setRefundAmount(sCaptureAmount);
                    refundRequest.setReversedAmount(transactionDetailsVO1.getRefundAmount());
                    refundRequest.setTransactionStatus(transactionDetailsVO1.getStatus());
                    refundRequest.setCaptureAmount(transactionDetailsVO1.getCaptureAmount());
                    refundRequest.setAdmin(true);
                    refundRequest.setIpAddress(transactionDetailsVO1.getIpAddress());
                    refundRequest.setFraud(true);
                    refundRequest.setRefundReason(reason);

                    auditTrailVO=new AuditTrailVO();
                    auditTrailVO.setActionExecutorId(transactionDetailsVO1.getToid());
                    auditTrailVO.setActionExecutorName("Admin Refund");
                    refundRequest.setAuditTrailVO(auditTrailVO);
                    response = process.refund(refundRequest);
                    PZResponseStatus status1 = response.getStatus();
                    sErrorMessage.append(getMessage(toId,transactionDetailsVO1.getTrackingid(),"Successful","Refund Successful"));
                }
                HashMap rfMail = new HashMap();
                rfMail.put(MailPlaceHolder.TOID, toId);
                rfMail.put(MailPlaceHolder.MULTIPALTRANSACTION, mailService.getDetailTableForMultipleTrans(list, action, reason));
                asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.FRAUD_NOTIFICATION, rfMail);
            }
            catch (Exception e){
                sErrorMessage.append(getMessage(toId,String.valueOf(trackingId),"Failed","Refund Failed"));
            }
        }
        sErrorMessage.append("</table>");
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("error", chargeBackMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher("/fraudNotification.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(req, res);
    }
    public String getMessage(String memberId,String trackingId, String status, String statusMsg)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<tr>");
        sb.append("<td>" + memberId + "</td>");
        sb.append("<td>" + trackingId + "</td>");
        sb.append("<td>" + status + "</td>");
        sb.append("<td>" + statusMsg + "</td>");
        sb.append("</tr>");
        return sb.toString();
    }
}
