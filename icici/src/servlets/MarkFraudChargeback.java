import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.BlacklistManager;
import com.manager.TransactionManager;
import com.manager.WhiteListManager;
import com.manager.dao.MerchantDAO;
import com.manager.dao.TransactionDAO;
import com.manager.enums.BlacklistEnum;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.Mail.*;
import com.payment.PZTransactionStatus;
import com.payment.PaymentProcessFactory;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZRefundRequest;
import com.payment.response.PZRefundResponse;
import com.payment.response.PZResponseStatus;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Mahima
 * Date: 4/10/18
 * Time: 1:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class MarkFraudChargeback extends HttpServlet
{
    private static Logger logger = new Logger(MarkFraudChargeback.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("invalid user");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String action = req.getParameter("submitbutton");
        StringBuilder sErrorMessage = new StringBuilder();
        StringBuilder sErrorMessageRefund = new StringBuilder();
        Functions functions=new Functions();
        String fraudReason=null;
        String remark=null;
        String accountId="";
        String toid="";
        String mailStatus="Fail";
        AuditTrailVO auditTrailVO1 = new AuditTrailVO();
        auditTrailVO1.setActionExecutorId("0");
        auditTrailVO1.setActionExecutorName("Admin"+ "-"+user.getAccountName());

        sErrorMessage.append("<center><table border=2>");
        sErrorMessage.append("<tr colspan=8 class=texthead>");
        sErrorMessage.append("<td colspan=2 align=center> Tracking Id </td>");
        sErrorMessage.append("<td colspan=2 align=center> Status </td>");
        sErrorMessage.append("<td colspan=4 align=center> Description </td>");
        sErrorMessage.append("</tr>");

        sErrorMessageRefund.append("<center><table border=2>");
        sErrorMessageRefund.append("<tr colspan=10 class=texthead>");
        sErrorMessageRefund.append("<td colspan=2 align=center> Tracking Id </td>");
        sErrorMessageRefund.append("<td colspan=2 align=center> Status </td>");
        sErrorMessageRefund.append("<td colspan=4 align=center> Description </td>");
        sErrorMessageRefund.append("<td colspan=2 align=center> Action </td>");
        sErrorMessageRefund.append("</tr>");

        String[] icicitransidStr = null;

        if (req.getParameterValues("trackingid") != null)
        {
            icicitransidStr = req.getParameterValues("trackingid");
        }
        else
        {
            sErrorMessage.append("Invalid TransactionID.");
            req.setAttribute("error", sErrorMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/MarkFraudTransaction?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        if (Functions.checkArrayNull(icicitransidStr) == null)
        {
            sErrorMessage.append("Select at least one transaction.");
            req.setAttribute("error", sErrorMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/MarkFraudTransaction?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        if ("Submit".equalsIgnoreCase(action))
        {
            TransactionDAO transactionDAO = new TransactionDAO();
            BlacklistManager blacklistManager = new BlacklistManager();
            WhiteListManager whiteListManager = new WhiteListManager();
            TransactionManager transactionManager = new TransactionManager();
            MerchantDetailsVO merchantDetailsVO = null;
            MerchantDAO merchantDAO = new MerchantDAO();

            for (String icicitransId : icicitransidStr)
            {
                if (req.getParameter("reason_" + icicitransId) != null && !req.getParameter("reason_" + icicitransId).equals(""))
                {
                    remark  = req.getParameter("reason_" + icicitransId);
                }
                if (req.getParameter("fraudreason_" + icicitransId) != null && !req.getParameter("fraudreason_" + icicitransId).equals(""))
                {
                    fraudReason = req.getParameter("fraudreason_" + icicitransId);
                }
                else{
                    sErrorMessage.append(getMessage(icicitransId, "Failed", "Fraud reason not provided"));
                    continue;
                }
                if(req.getParameter("accountid_"+icicitransId) !=null && !req.getParameter("accountid_"+icicitransId).equals("")){
                    accountId=req.getParameter("accountid_"+icicitransId);
                }
                if (req.getParameter("toid_" + icicitransId) != null && !req.getParameter("toid_" + icicitransId).equals(""))
                {
                    toid = req.getParameter("toid_" + icicitransId);
                }
                else
                {
                    sErrorMessage.append(getMessage(icicitransId, "Failed", "ToId is missing."));
                    continue;
                }

                try
                {
                    merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                }
                catch (PZDBViolationException e)
                {
                    sErrorMessage.append(getMessage(icicitransId, "Failed", "Error while fetching member details"));
                    continue;
                }
                boolean status = transactionDAO.updateIsFraudRequest(icicitransId,remark, fraudReason);
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(icicitransId);
                String reason = "Fraudulent Transaction Uploaded by Admin" + "(" + (icicitransId) + ")";
                try
                {
                    //blacklistManager.blacklistEntities(icicitransId, blacklistReason);
                    whiteListManager.whiteListEntities(icicitransId);
                    Set<String> cardNum = new HashSet<>();
                    Set<String> emailAddr = new HashSet<>();
                    Set<String> phone = new HashSet<>();
                  //  transactionManager.addCustomerName(transactionDetailsVO.getName(), cardNum, emailAddr,phone);
                    blacklistManager.addCustomerNameBatch(transactionDetailsVO.getName(), reason,auditTrailVO1);
                    blacklistManager.addCustomerEmailBatch(emailAddr, reason,auditTrailVO1);
                   // blacklistManager.addCustomerPhoneBatch(phone, reason, auditTrailVO1);
                    blacklistManager.addCustomerCardBatch(cardNum, reason, BlacklistEnum.Fraud_Received.toString(),auditTrailVO1);
                    //whiteListManager.removeCardEmailEntry(emailAddr, cardNum);
                    //whiteListManager.updateCardEmailEntry(emailAddr, cardNum);
                }
                catch (PZDBViolationException e)
                {
                    logger.info("Duplicate Entry:::" + e);
                }
                sErrorMessage.append(getMessage(icicitransId, "Success", "Transaction marked as Fraud Successfully."));

                logger.error("RefundDetails::::"+req.getParameter("isRefundDetails_"+icicitransId));
                if("All".equals(req.getParameter("isRefundDetails_"+icicitransId)))
                {
                    //Process refund
                    Connection conn = null;
                    try
                    {
                        AbstractPaymentProcess process = null;
                        TransactionDetailsVO transactionDetailsVO1 = transactionManager.getTransDetailFromCommon(icicitransId);
                        //TransactionDetailsVO transactionDetailsVO1 = transactionManager.getTransDetailFromCommon(transactionDetailsVO1.getTrackingid());
                        GatewayAccount account = GatewayAccountService.getGatewayAccount(transactionDetailsVO1.getAccountId());
                        String refundreasonForMail = "Current transaction have been identified as fraudulent by the system. Auto refunded by the system to avoid any future chargebacks.";
                        String refundreason = "Auto Refunded due to Fraud received from same Customer.";
                        List<String> refundFailedTransaction = new ArrayList<String>();
                        List successlist = new ArrayList();
                        String transactionStatus = "";
                        PZRefundRequest refundRequest = new PZRefundRequest();
                        String currency = account.getCurrency();
                        process = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(transactionDetailsVO1.getTrackingid()), Integer.parseInt(transactionDetailsVO1.getAccountId()));
                        logger.error("Toid:::::" + transactionDetailsVO1.getToid() + "----trackingId----" + transactionDetailsVO1.getTrackingid() + "------captureAmount----" + transactionDetailsVO1.getCaptureAmount()+"-------"+transactionDetailsVO1.getRefundAmount());
                        String remainingAmt=String.format("%.2f",Double.parseDouble(transactionDetailsVO1.getCaptureAmount())-Double.parseDouble(transactionDetailsVO1.getRefundAmount()));
                        logger.error("Balance Amount=" + Double.parseDouble(remainingAmt));

                        refundRequest.setMemberId(Integer.valueOf(transactionDetailsVO1.getToid()));
                        refundRequest.setAccountId(Integer.parseInt(transactionDetailsVO1.getAccountId()));
                        refundRequest.setTrackingId(Integer.parseInt(transactionDetailsVO1.getTrackingid()));
                        refundRequest.setRefundAmount(remainingAmt);
                        refundRequest.setRefundReason(refundreason);
                        refundRequest.setReversedAmount(transactionDetailsVO1.getRefundAmount());
                        refundRequest.setTransactionStatus(transactionDetailsVO1.getStatus());
                        refundRequest.setCaptureAmount(transactionDetailsVO1.getCaptureAmount());
                        refundRequest.setAdmin(true);
                        refundRequest.setIpAddress(null);
                        //refundRequest.setFraud("Y".equals(fraud));
                        currency = account.getCurrency();
                        //newly added
                        AuditTrailVO auditTrailVO = new AuditTrailVO();
                        auditTrailVO.setActionExecutorId("0");
                        auditTrailVO.setActionExecutorName("Admin Refund " + "-" + user.getAccountName());
                        auditTrailVO.setCbReason(refundreason);
                        refundRequest.setAuditTrailVO(auditTrailVO);

                        if ("N".equalsIgnoreCase(merchantDetailsVO.getMultipleRefund()) && transactionStatus.equalsIgnoreCase(PZTransactionStatus.REVERSED.toString()))
                        {
                            sErrorMessageRefund.append(getMessageForRefund(transactionDetailsVO1.getTrackingid(), "System Restrication", "Multiple Refund is not allowed.","Refund"));
                        }
                        else
                        {
                            PZRefundResponse refundResponse = process.refund(refundRequest);
                            logger.error("RefundResponse-------"+refundResponse);
                            PZResponseStatus refundResponseStatus = refundResponse.getStatus();
                            logger.error("status-------"+refundResponseStatus);
                            Hashtable refundDetails = null;
                            StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
                            conn=Database.getConnection();
                            if (refundResponse != null && PZResponseStatus.SUCCESS.equals(refundResponseStatus))
                            {
                                successlist.add(transactionDetailsVO1.getTrackingid() + "<BR>");
                                refundDetails = new Hashtable();
                                refundDetails.put("icicitransid", transactionDetailsVO1.getTrackingid());

                                refundDetails.put("description", refundResponse.getResponseDesceiption());
                                mailStatus = "successful";
                                sErrorMessageRefund.append(getMessageForRefund(transactionDetailsVO1.getTrackingid(), refundResponse.getStatus().toString(), refundResponse.getResponseDesceiption(), "Refund"));
                                logger.error("SuccessMsg:::"+sErrorMessage.toString());
                                statusSyncDAO.updateAllRefundTransactionFlowFlag(transactionDetailsVO1.getTrackingid(), "fraud", conn);
                                logger.error("after reversed:::");
                            }
                            else if (refundResponse != null && PZResponseStatus.PENDING.equals(refundResponseStatus))
                            {
                                mailStatus = refundResponse.getResponseDesceiption();
                                sErrorMessageRefund.append(getMessageForRefund(transactionDetailsVO1.getTrackingid(), refundResponse.getStatus().toString(), refundResponse.getResponseDesceiption(), "Refund"));
                            }
                            else
                            {
                                refundFailedTransaction.add(transactionDetailsVO1.getTrackingid());
                                sErrorMessageRefund.append(getMessageForRefund(transactionDetailsVO1.getTrackingid(), refundResponse.getStatus().toString(), refundResponse.getResponseDesceiption(), "Refund"));
                                continue;
                            }
                        }
                        logger.error("after AsynchronousMailService:::");
                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendEmail(MailEventEnum.REFUND_TRANSACTION, transactionDetailsVO1.getTrackingid(), mailStatus, refundreasonForMail, null);
                        logger.error("before AsynchronousMailService:::");

                        logger.error("after AsynchronousSmsService:::");
                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                        smsService.sendSMS(MailEventEnum.REFUND_TRANSACTION, transactionDetailsVO1.getTrackingid(), mailStatus, refundreasonForMail, null);
                        logger.error("after AsynchronousSmsService:::");
                        AsyncMultipleRefund asyncMultipleRefund=AsyncMultipleRefund.getInstance();
                        asyncMultipleRefund.asyncRefund(transactionDetailsVO,"0",user.getAccountName(),refundreason,refundreasonForMail,"fraud");

                    }
                    catch (Exception e)
                    {
                        logger.error("Exception while Refund the transaction",e);
                        sErrorMessageRefund.append(getMessageForRefund(icicitransId, "Failed", "Exception while Refund the transaction","Refund"));
                    }finally
                    {
                        Database.closeConnection(conn);
                    }
                }
                else if("Current".equals(req.getParameter("isRefundDetails_"+icicitransId)))
                {
                    //Process refund For Single Refund
                    Connection conn = null;
                    try
                    {
                        AbstractPaymentProcess process = null;
                        TransactionDetailsVO transactionDetailsVO1 = transactionManager.getTransDetailFromCommon(icicitransId);
                        GatewayAccount account = GatewayAccountService.getGatewayAccount(transactionDetailsVO1.getAccountId());
                        String refundreasonForMail = "Current transaction have been identified as fraudulent by the system. Auto refunded by the system to avoid any future chargebacks.";
                        String refundreason = "Auto Refunded due to Fraud received from same Customer("+icicitransId+")";
                        List<String> refundFailedTransaction = new ArrayList<String>();
                        List successlist = new ArrayList();
                        String transactionStatus = "";
                        PZRefundRequest refundRequest = new PZRefundRequest();
                        String currency = account.getCurrency();
                        process = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(transactionDetailsVO1.getTrackingid()), Integer.parseInt(transactionDetailsVO1.getAccountId()));
                        logger.error("Toid:::::" + transactionDetailsVO1.getToid() + "----trackingId----" + transactionDetailsVO1.getTrackingid() + "------captureAmount----" + transactionDetailsVO1.getCaptureAmount()+"-------"+transactionDetailsVO1.getRefundAmount());
                        String remainingAmt=String.format("%.2f",Double.parseDouble(transactionDetailsVO1.getCaptureAmount())-Double.parseDouble(transactionDetailsVO1.getRefundAmount()));
                        logger.error("Balance Amount=" + Double.parseDouble(remainingAmt));

                        refundRequest.setMemberId(Integer.valueOf(transactionDetailsVO1.getToid()));
                        refundRequest.setAccountId(Integer.parseInt(transactionDetailsVO1.getAccountId()));
                        refundRequest.setTrackingId(Integer.parseInt(transactionDetailsVO1.getTrackingid()));
                        refundRequest.setRefundAmount(remainingAmt);
                        refundRequest.setRefundReason(refundreason);
                        refundRequest.setReversedAmount(transactionDetailsVO1.getRefundAmount());
                        refundRequest.setTransactionStatus(transactionDetailsVO1.getStatus());
                        refundRequest.setCaptureAmount(transactionDetailsVO1.getCaptureAmount());
                        refundRequest.setAdmin(true);
                        refundRequest.setIpAddress(null);
                        refundRequest.setFraud(true);
                        currency = account.getCurrency();
                        //newly added
                        AuditTrailVO auditTrailVO = new AuditTrailVO();
                        auditTrailVO.setActionExecutorId("0");
                        auditTrailVO.setActionExecutorName("Admin Refund " + "-" + user.getAccountName());
                        auditTrailVO.setCbReason(refundreason);
                        refundRequest.setAuditTrailVO(auditTrailVO);

                        if ("N".equalsIgnoreCase(merchantDetailsVO.getMultipleRefund()) && transactionStatus.equalsIgnoreCase(PZTransactionStatus.REVERSED.toString()))
                        {
                            sErrorMessageRefund.append(getMessageForRefund(transactionDetailsVO1.getTrackingid(), "System Restrication", "Multiple Refund is not allowed.","Refund"));
                        }
                        else
                        {
                            PZRefundResponse refundResponse = process.refund(refundRequest);
                            logger.error("RefundResponse-------"+refundResponse);
                            PZResponseStatus refundResponseStatus = refundResponse.getStatus();
                            logger.error("status-------"+refundResponseStatus);
                            Hashtable refundDetails = null;
                            StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
                            conn=Database.getConnection();
                            if (refundResponse != null && PZResponseStatus.SUCCESS.equals(refundResponseStatus))
                            {
                                successlist.add(transactionDetailsVO1.getTrackingid() + "<BR>");
                                refundDetails = new Hashtable();
                                refundDetails.put("icicitransid", transactionDetailsVO1.getTrackingid());

                                refundDetails.put("description", refundResponse.getResponseDesceiption());
                                mailStatus = "successful";
                                sErrorMessageRefund.append(getMessageForRefund(transactionDetailsVO1.getTrackingid(), refundResponse.getStatus().toString(), refundResponse.getResponseDesceiption(), "Refund"));
                                logger.error("SuccessMsg:::"+sErrorMessageRefund.toString());
                                statusSyncDAO.updateAllRefundTransactionFlowFlag(transactionDetailsVO1.getTrackingid(), "fraud", conn);
                                logger.error("after reversed:::");
                            }
                            else if (refundResponse != null && PZResponseStatus.PENDING.equals(refundResponseStatus))
                            {
                                mailStatus = refundResponse.getResponseDesceiption();
                                sErrorMessageRefund.append(getMessageForRefund(transactionDetailsVO1.getTrackingid(), refundResponse.getStatus().toString(), refundResponse.getResponseDesceiption(), "Refund"));
                            }
                            else
                            {
                                refundFailedTransaction.add(transactionDetailsVO1.getTrackingid());
                                sErrorMessageRefund.append(getMessageForRefund(transactionDetailsVO1.getTrackingid(), refundResponse.getStatus().toString(), refundResponse.getResponseDesceiption(), "Refund"));
                                continue;
                            }
                        }
                        logger.error("after AsynchronousMailService:::");
                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendEmail(MailEventEnum.REFUND_TRANSACTION, transactionDetailsVO1.getTrackingid(), mailStatus, refundreasonForMail, null);
                        logger.error("before AsynchronousMailService:::");

                        logger.error("after AsynchronousSmsService:::");
                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                        smsService.sendSMS(MailEventEnum.REFUND_TRANSACTION, transactionDetailsVO1.getTrackingid(), mailStatus, refundreasonForMail, null);
                        logger.error("after AsynchronousSmsService:::");
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception while Refund the transaction",e);
                        sErrorMessageRefund.append(getMessageForRefund(icicitransId, "Failed", "Exception while Refund the transaction","Refund"));
                    }
                    finally
                    {
                        Database.closeConnection(conn);
                    }
                }
                else {
                    sErrorMessageRefund.append(getMessageForRefund(icicitransId, "-", "Transaction not marked as Refund","Refund"));
                }
            }
            sErrorMessageRefund.append("</table>");
            sErrorMessage.append("</table>");
        }
        sErrorMessage.append("</table>");
        sErrorMessageRefund.append("</table>");
        StringBuilder chargeBackMessage = new StringBuilder();
        StringBuilder RefundMessage = new StringBuilder();
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        RefundMessage.append(sErrorMessageRefund.toString());
        req.setAttribute("error", chargeBackMessage.toString());
        req.setAttribute("refundMsg", RefundMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher("/markFraudTransaction.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    public String getMessage(String trackingId,String status,String description)
    {
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("<tr colspan=8>");
        stringBuffer.append("<td colspan=2>"+trackingId+"</td>");
        stringBuffer.append("<td colspan=2>"+status+"</td>");
        stringBuffer.append("<td colspan=4>"+description+"</td>");
        stringBuffer.append("</tr>");
        return stringBuffer.toString();
    }
    public String getMessageForRefund(String trackingId,String status,String description,String action)
    {
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("<tr colspan=10>");
        stringBuffer.append("<td colspan=2>"+trackingId+"</td>");
        stringBuffer.append("<td colspan=2>"+status+"</td>");
        stringBuffer.append("<td colspan=4>"+description+"</td>");
        stringBuffer.append("<td colspan=2>"+action+"</td>");
        stringBuffer.append("</tr>");
        return stringBuffer.toString();
    }
}