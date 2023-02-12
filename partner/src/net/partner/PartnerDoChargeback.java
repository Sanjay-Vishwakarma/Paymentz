package net.partner;

import com.directi.pg.*;
import com.manager.BlacklistManager;
import com.manager.TransactionManager;
import com.manager.WhiteListManager;
import com.manager.dao.MerchantDAO;
import com.manager.dao.TransactionDAO;
import com.manager.enums.BlacklistEnum;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.PaymentProcessFactory;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.request.PZChargebackRequest;
import com.payment.response.PZChargebackResponse;
import com.payment.response.PZResponseStatus;
import org.apache.commons.lang.ArrayUtils;
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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Mahima Rai.
 * Date: 17/05/18
 * Time: 1:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerDoChargeback extends HttpServlet
{
    private static Logger logger = new Logger(PartnerDoChargeback.class.getName());
    ResourceBundle rb=LoadProperties.getProperty("com.directi.pg.chargebackFraud");
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   logger.debug("invalid user");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        //String action=req.getParameter("submitbutton");
        StringBuilder sErrorMessage = new StringBuilder();

        String cbamount = null;
        String cbreason=null;
        String toid = null;
        String accountId = null;
        String mailStatus="Fail";
        String[] icicitransidStr =null;
        String captureAmount=null;
        String refundAmount=null;
        String notificationUrl =null;
        String partnerId=(String)session.getAttribute("merchantid");


        PZChargebackRequest cbRequest = new PZChargebackRequest();
        PZChargebackResponse response = null;
        MerchantDAO merchantDAO = new MerchantDAO();

        if (req.getParameterValues("trackingid")!= null)
        {
            icicitransidStr = req.getParameterValues("trackingid");
        }
        else
        {
            sErrorMessage.append("Invalid TransactionID.");
            req.setAttribute("message", sErrorMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/PartnerMerchantChargebackList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        if (Functions.checkArrayNull(icicitransidStr) == null)
        {
            sErrorMessage.append("Select at least one transaction.");
            req.setAttribute("message", sErrorMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/PartnerMerchantChargebackList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        HashMap<String,Collection> membersMap = new HashMap<String,Collection>();
        Collection<Hashtable> listOfRefunds = null;
        Hashtable refundDetails = null;
        Functions functions = new Functions();
        for (String icicitransid : icicitransidStr)
        {
            MerchantDetailsVO merchantDetailsVO = null;
            if (req.getParameter("accountid_" + icicitransid) != null && !req.getParameter("accountid_" + icicitransid).equals(""))
            {
                accountId = req.getParameter("accountid_" + icicitransid);
            }
            else
            {
                sErrorMessage.append("<tr><td>" + icicitransid + "</td><td>" + toid + "</td><td>" + accountId + "</td><td>" + "Failed" + "</td><td>AccountId is missing</td></tr>");
                continue;
            }
            if (req.getParameter("toid_" + icicitransid) != null && !req.getParameter("toid_" + icicitransid).equals(""))
            {
                toid = req.getParameter("toid_" + icicitransid);
            }
            else
            {
                sErrorMessage.append("<tr><td>" + icicitransid + "</td><td>" + toid + "</td><td>" + accountId + "</td><td>" + "Failed" + "</td><td>ToId is missing.</td></tr>");
                continue;
            }
            if (req.getParameter("captureamount_" + icicitransid) != null && !req.getParameter("captureamount_" + icicitransid).equals(""))
            {
                captureAmount = req.getParameter("captureamount_" + icicitransid);
            }
            if (req.getParameter("refundamount_" + icicitransid) != null && !req.getParameter("refundamount_" + icicitransid).equals(""))
            {
                refundAmount = req.getParameter("refundamount_" + icicitransid);
            }
            try
            {
                merchantDetailsVO = merchantDAO.getMemberDetails(toid);
            }
            catch (PZDBViolationException e)
            {
                sErrorMessage.append("<tr><td>" + icicitransid + "</td><td>" + toid + "</td><td>" + accountId + "</td><td>" + "Failed" + "</td><td>Error while fetching member details</td></tr>");
                continue;
            }
            try
            {
                cbamount = ESAPI.validator().getValidInput("cbamount_" + icicitransid, req.getParameter("cbamount_" + icicitransid), "AmountStr", 20, false);
            }
            catch (ValidationException e)
            {
                PZExceptionHandler.raiseAndHandleGenericViolationException("PartnerDoChargeback.java", "doPost()", null, "admin", sErrorMessage.toString(), null, e.getMessage(), e.getCause(), toid, PZOperations.PARTNER_CHARGEBACK);
                sErrorMessage.append("<tr><td>" + icicitransid + "</td><td>" + toid + "</td><td>" + accountId + "</td><td>" + "Failed" + "</td><td>" + "chargeback amount should not be empty" + "</td></tr>");
                continue;
            }
            if(cbamount.equals("0.00")){
                sErrorMessage.append("<tr><td>" + icicitransid + "</td><td>" + toid + "</td><td>" + accountId + "</td><td>" + "Failed" + "</td><td>" + "chargeback amount should be greater then 0.00" + "</td></tr>");
                continue;
            }
            if (req.getParameter("chargebackreason_" + icicitransid) != null && !req.getParameter("chargebackreason_" + icicitransid).equals("") && !"0".equals(req.getParameter("chargebackreason_" + icicitransid)))
            {
                cbreason = req.getParameter("chargebackreason_" + icicitransid);
            }
            else
            {
                sErrorMessage.append("<tr><td>" + icicitransid + "</td><td>" + toid + "</td><td>" + accountId + "</td><td>" + "Failed" + "</td><td>Chargeback reason not provided</td></tr>");
                continue;
            }

            if (merchantDetailsVO != null && functions.isValueNull(merchantDetailsVO.getMemberId()))
            {
                Hashtable transDetails = TransactionEntry.getTransDetails(icicitransid);
                String transactionDate = (String) transDetails.get("transactiondate");
                SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                long d = Functions.DATEDIFF(transactionDate, targetFormat.format(new Date()));
                int chargebackAllowedDays = Integer.parseInt(merchantDetailsVO.getChargebackAllowedDays());
                if (d > chargebackAllowedDays)
                {
                    sErrorMessage.append("<tr><td>" + icicitransid + "</td><td>" + toid + "</td><td>" + accountId + "</td><td>" + "Failed" + "</td><td>Transaction has exceeds refund expiry</td></tr>");
                    continue;
                }
            }
            AbstractPaymentProcess process = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(icicitransid), Integer.parseInt(accountId));
            cbRequest.setMemberId(Integer.valueOf(toid));
            cbRequest.setAccountId(Integer.parseInt(accountId));
            cbRequest.setTrackingId(Integer.parseInt(icicitransid));
            cbRequest.setCbAmount(cbamount);
            cbRequest.setCbReason(cbreason);
            cbRequest.setCaptureAmount(captureAmount);
            cbRequest.setRefundAmount(refundAmount);
            cbRequest.setAdmin(true);

            AuditTrailVO auditTrailVO = new AuditTrailVO();
            auditTrailVO.setActionExecutorId(partnerId);
            String role = "";
            for (String s : user.getRoles())
            {
                role = role.concat(s);
            }
            auditTrailVO.setActionExecutorName(role + "-" + session.getAttribute("username").toString());
            cbRequest.setAuditTrailVO(auditTrailVO);
            response = process.chargeback(cbRequest);
            if (response != null && (response.getStatus()).equals(PZResponseStatus.SUCCESS))
            {
                refundDetails = new Hashtable();
                refundDetails.put("icicitransid", response.getTrackingId());
                mailStatus = "Successful";
                refundDetails.put("description", response.getResponseDesceiption());
                sErrorMessage.append("<tr><td>" + response.getTrackingId() + "</td><td>" + toid + "</td><td>" + accountId + "</td><td>" + "Chargeback Successful" + "</td><td>" + cbRequest.getCbReason() + "</td></tr>");
                if (membersMap.get(toid) == null)
                {
                    listOfRefunds = new ArrayList<Hashtable>();
                    listOfRefunds.add(refundDetails);
                    membersMap.put(toid, listOfRefunds);
                }
                else
                {
                    listOfRefunds = membersMap.get(toid);
                    listOfRefunds.add(refundDetails);
                    membersMap.put(toid, listOfRefunds);
                }
            }
            else
            {
                sErrorMessage.append("<tr><td>" + icicitransid + "</td><td>" + toid + "</td><td>" + accountId + "</td><td>" + "Failed" + "</td><td>" + response.getResponseDesceiption() + "</td></tr>");
                continue;
            }

            if ("Y".equalsIgnoreCase(merchantDetailsVO.getChargebackMailsend()))
            {
                SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();
                sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.CHARGEBACK_TRANSACTION, icicitransid, mailStatus, cbreason, null);

            }
            try
            {
                TransactionManager transactionManager = new TransactionManager();
                merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                notificationUrl = req.getParameter("notificationURL_" + icicitransid);
                String notification_status ="chargeback";
                logger.error("Notification Sending to---"+notificationUrl+"---"+icicitransid + " status "+ notification_status + response.getResponseDesceiption());

                logger.error("ChargebackNotification flag for ---" + toid + "---" + merchantDetailsVO.getChargebackNotification());
                if (functions.isValueNull(notificationUrl) && "Y".equals(merchantDetailsVO.getChargebackNotification()))
                {
                    logger.error("inside sending notification---" + notificationUrl);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(icicitransid);
                    asyncNotificationService.sendNotification(transactionDetailsVO, icicitransid, notification_status, response.getResponseDesceiption());
                }
            }catch(Exception e){
                logger.error("PZDBViolationException--->", e);
            }
            String reason = rb.getString("reason");
            String reason1[] = reason.split("\\|");
            TransactionDAO transactionDAO = new TransactionDAO();
            if (ArrayUtils.contains(reason1, cbreason))
            {
                boolean status = transactionDAO.getUploadBulkFraudFile(icicitransid);
                BlacklistManager blacklistManager = new BlacklistManager();
                WhiteListManager whiteListManager = new WhiteListManager();
                TransactionManager transactionManager = new TransactionManager();
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(icicitransid);
                String blacklistReason = (cbreason + " (Chargeback by Partner) " + "(" + icicitransid + ")");
                try
                {
                    blacklistManager.blacklistEntities(icicitransid, blacklistReason,BlacklistEnum.Chargeback_Received.toString(),auditTrailVO);
                    whiteListManager.whiteListEntities(icicitransid);
                    if("UPI".equalsIgnoreCase(transactionDetailsVO.getCardtype()) && functions.isValueNull(transactionDetailsVO.getCustomerId()))
                    {
                        //vpa Address store in customerId field while transaction
                        blacklistManager.addCustomerVPAAddressBatch(transactionDetailsVO.getCustomerId(),blacklistReason,auditTrailVO);
                    }
                    Set<String> cardNum = new HashSet<>();
                    Set<String> emailAddr = new HashSet<>();
                    Set<String> phone = new HashSet<>();
                    /*if(functions.isValueNull(transactionDetailsVO.getName()))
                        transactionManager.addCustomerName(transactionDetailsVO.getName(), cardNum, emailAddr,phone);*/
                    logger.error("emailAddr size---->"+emailAddr.size());
                    logger.error("cardNum size---->"+cardNum.size());
                    try
                    {
                        if(emailAddr.size()>0)
                        blacklistManager.addCustomerEmailBatch(emailAddr, blacklistReason,auditTrailVO);
                    }catch (PZDBViolationException e){
                        logger.error("Duplicate Entry:::", e);
                    }
                    /*try
                    {
                        if(phone.size()>0)
                            blacklistManager.addCustomerPhoneBatch(phone, blacklistReason,auditTrailVO);
                    }catch (PZDBViolationException e){
                        logger.error("Duplicate Entry:::" , e);
                    }*/

                    try{
                        if(cardNum.size()>0)
                        blacklistManager.addCustomerCardBatch(cardNum, blacklistReason, BlacklistEnum.Chargeback_Received.toString(),auditTrailVO);
                    }
                    catch (PZDBViolationException e){
                        logger.error("Duplicate Entry:::" , e);
                    }
                    try{
                        whiteListManager.updateCardEmailEntry(emailAddr, cardNum);
                    }
                    catch (PZDBViolationException e){
                        logger.info("Duplicate Entry:::" + e);
                    }
                }
                catch (PZDBViolationException e)
                {
                    logger.info("Duplicate Entry:::::" + e);
                }
            }
        }

        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append("<div><table align=center width=\"40%\" class=\"display table table table-striped table-bordered table-hover dataTable\" style=\"font-family:Open Sans;font-size: 13px;font-weight: 600;width: 70%;\"><tr><td valign=\"middle\" style=\"background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;><font class=\"texthead\" color=\"#FFFFFF\" size=\"4\" >Tracking Id</font></td>" +
                "<td td valign=\"middle\" style=\"background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;><font class=\"texthead\" color=\"#FFFFFF\" size=\"4\">Member ID</font></td>" +
                "<td td valign=\"middle\" style=\"background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;><font class=\"texthead\" color=\"#FFFFFF\" size=\"4\">Account ID</font></td>" +
                "<td td valign=\"middle\" style=\"background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;><font class=\"texthead\" color=\"#FFFFFF\" size=\"4\">Status</font></td>" +
                "<td td valign=\"middle\" style=\"background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;><font class=\"texthead\" color=\"#FFFFFF\" size=\"4\">Remark</font></td></tr>");
        chargeBackMessage.append(sErrorMessage.toString());
        chargeBackMessage.append("</table></div>");
        req.setAttribute("message", chargeBackMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher("/partnerMerchantChargebackList.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
}
