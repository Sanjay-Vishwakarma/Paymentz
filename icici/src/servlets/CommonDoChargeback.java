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
import com.payment.Mail.*;
import com.payment.PaymentProcessFactory;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.request.PZChargebackRequest;
import com.payment.response.PZChargebackResponse;
import com.payment.response.PZResponseStatus;
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
 * User: admin
 * Date: 4/10/13
 * Time: 1:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommonDoChargeback extends HttpServlet
{
    private static Logger logger    = new Logger(CommonDoChargeback.class.getName());
    ResourceBundle rb               = LoadProperties.getProperty("com.directi.pg.chargebackFraud");
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user           =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   logger.debug("invalid user");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String action                       = req.getParameter("submitbutton");
        StringBuilder sErrorMessage         = new StringBuilder();
        StringBuilder sSuccessMessage       = new StringBuilder();

        sSuccessMessage.append("<center><table border=2>");
        sSuccessMessage.append("<tr colspan=8 class=texthead>");
        sSuccessMessage.append("<td colspan=2 align=center>Tracking Id </td>");
        sSuccessMessage.append("<td colspan=2 align=center>Status</td>");
        sSuccessMessage.append("<td colspan=4 align=center>Description</td>");
        sSuccessMessage.append("<td colspan=4 align=center>Action</td>");
        sSuccessMessage.append("</tr>");

        String cbamount     = null;
        String cbreason     = null;
        String toid         = null;
        String accountId    = null;
        String cbDate       = null;
        String mailStatus           = "Fail";
        String[] icicitransidStr    = null;
        String isFraudRequest       =  "";
        String captureAmount        = null;
        String refundAmount         = null;
        String notificationUrl      = null;

        HashMap<String,List<Hashtable>> membersMap  = new HashMap<>();
        List<Hashtable> listOfRefunds               = null;
        MerchantDetailsVO merchantDetailsVO         = null;

        PZChargebackRequest cbRequest   = new PZChargebackRequest();
        PZChargebackResponse response   = null;
        MerchantDAO merchantDAO         = new MerchantDAO();
        String ipaddress                = req.getRemoteAddr();

        if (req.getParameterValues("trackingid")!= null)
        {
            icicitransidStr = req.getParameterValues("trackingid");
        }
        else
        {
            sSuccessMessage.append("Invalid TransactionID.");
            req.setAttribute("cbmessage", sSuccessMessage.toString());
            logger.debug("forwarding to CommonDoChargeback");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/CommonChargebackList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        if (Functions.checkArrayNull(icicitransidStr) == null)
        {
            sSuccessMessage.append("Select at least one transaction.");
            req.setAttribute("cbmessage", sSuccessMessage.toString());
            logger.debug("forwarding to CommonDoChargeback");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/CommonChargebackList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        if("RetrivalRequest Selected".equalsIgnoreCase(action))
        {
            TransactionDAO transactionDAO   = new TransactionDAO();
            for (String icicitransid : icicitransidStr)
            {
                String retrivalRequestFlag  = req.getParameter("isRetrivalRequest_"+icicitransid);
                boolean status              = transactionDAO.updateTransactionRetrialRequest(icicitransid,retrivalRequestFlag);
                if(status)
                {
                    sSuccessMessage.append(getMessage(icicitransid, "Success", "Record Updated Successfully.","Retrival Request"));
                }
                else
                {
                    sSuccessMessage.append(getMessage(icicitransid, "Success", "Failed During Updation.","Retrival Request"));
                }
            }
        }
        else
        {
            Hashtable refundDetails     = null;
            Functions functions         = new Functions();
            for (String icicitransid : icicitransidStr)
            {
                if (req.getParameter("toid_" + icicitransid) != null && !req.getParameter("toid_" + icicitransid).equals(""))
                {
                    toid = req.getParameter("toid_" + icicitransid);
                }
                else
                {
                    sSuccessMessage.append(getMessage(icicitransid, "Failed", "ToId is missing.","Chargeback"));
                    continue;
                }

                try
                {
                    merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                }
                catch (PZDBViolationException e)
                {
                    sSuccessMessage.append(getMessage(icicitransid, "Failed", "Error while fetching member details","Chargeback"));
                    continue;
                }

                try
                {
                    cbamount = ESAPI.validator().getValidInput("cbamount_" + icicitransid, req.getParameter("cbamount_" + icicitransid), "AmountStr", 20, false);
                }
                catch (ValidationException e)
                {
                    PZExceptionHandler.raiseAndHandleGenericViolationException("CommonDoChargeback.java", "doPost()", null, "admin", sSuccessMessage.toString(), null, e.getMessage(), e.getCause(), toid, PZOperations.ADMIN_CHARGEBACK);
                    sSuccessMessage.append(getMessage(icicitransid, "Failed", "chargeback amount should not be empty or invalid","Chargeback"));
                    continue;
                }
                if(cbamount.equals("0.00")){
                    sSuccessMessage.append(getMessage(icicitransid, "Failed", "chargeback amount should be greater then 0.00","Chargeback"));
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

                if (req.getParameter("accountid_" + icicitransid) != null && !req.getParameter("accountid_" + icicitransid).equals(""))
                {
                    accountId = req.getParameter("accountid_" + icicitransid);
                }
                else
                {
                    sSuccessMessage.append(getMessage(icicitransid, "Failed", "AccountId is missing.","Chargeback"));
                    continue;
                }
                if (req.getParameter("cbdate_" + icicitransid) != null && !req.getParameter("cbdate_" + icicitransid).equals(""))
                {
                    cbDate = req.getParameter("cbdate_" + icicitransid)+" 00:00:00";
                }
                else
                {
                    sSuccessMessage.append(getMessage(icicitransid, "Failed", "Chargeback Date is missing","Chargeback"));
                    continue;
                }

                if (req.getParameter("chargebackreason_" + icicitransid) != null && !req.getParameter("chargebackreason_" + icicitransid).equals(""))
                {
                    cbreason = req.getParameter("chargebackreason_" + icicitransid);
                }
                else
                {
                    sSuccessMessage.append(getMessage(icicitransid, "Failed", "Chargeback reason not provided","Chargeback"));
                    continue;
                }
                if (merchantDetailsVO != null && functions.isValueNull(merchantDetailsVO.getMemberId()))
                {
                    Hashtable transDetails          = TransactionEntry.getTransDetails(icicitransid);
                    String transactionDate          = (String) transDetails.get("transactiondate");
                    SimpleDateFormat targetFormat   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long d                          = Functions.DATEDIFF(transactionDate, targetFormat.format(new Date()));
                    int chargebackAllowedDays       = Integer.parseInt(merchantDetailsVO.getChargebackAllowedDays());
                    if (d > chargebackAllowedDays)
                    {
                        sSuccessMessage.append(getMessage(icicitransid, "Failed", "Transaction has exceeds chargeback expiry","Chargeback"));
                        continue;
                    }
                }
                //Process Chargeback
                AbstractPaymentProcess process = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(icicitransid), Integer.parseInt(accountId));
                cbRequest.setMemberId(Integer.valueOf(toid));
                cbRequest.setAccountId(Integer.parseInt(accountId));
                cbRequest.setTrackingId(Integer.parseInt(icicitransid));
                cbRequest.setCbAmount(cbamount);
                cbRequest.setCbReason(cbreason);
                cbRequest.setCaptureAmount(captureAmount);
                cbRequest.setRefundAmount(refundAmount);
                cbRequest.setAdmin(true);
                cbRequest.setChargebackDate(cbDate);

                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorId("0");
                auditTrailVO.setActionExecutorName("Admin"+ "-"+user.getAccountName());
                cbRequest.setAuditTrailVO(auditTrailVO);
                response = process.chargeback(cbRequest);
                if (response != null && (response.getStatus()).equals(PZResponseStatus.SUCCESS))
                {
                    refundDetails = new Hashtable();
                    refundDetails.put("icicitransid", response.getTrackingId());
                    mailStatus = "Successful";
                    //refundDetails.put("description", response.getResponseDesceiption());

                    sSuccessMessage.append(getMessage(response.getTrackingId(), response.getStatus().toString(), response.getResponseDesceiption(),"Chargeback"));
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
                    sSuccessMessage.append(getMessage(icicitransid, "Failed", response.getResponseDesceiption(),"Chargeback"));
                    continue;
                }

                try
                {
                    TransactionManager transactionManager   = new TransactionManager();
                    merchantDetailsVO                       = merchantDAO.getMemberDetails(toid);
                     notificationUrl                        = req.getParameter("notificationURL_" + icicitransid);
                    logger.error("##Notification Sending to---"+notificationUrl+"---"+icicitransid);
                    String notification_status = "chargeback";
                    logger.error("ChrgebackNotification flag for ---" + toid + "---" + merchantDetailsVO.getChargebackNotification());
                    if (functions.isValueNull(notificationUrl) && "Y".equals(merchantDetailsVO.getChargebackNotification()))
                    {
                        logger.error("inside sending notification---" + notificationUrl);
                        AsyncNotificationService asyncNotificationService   = AsyncNotificationService.getInstance();
                        TransactionDetailsVO transactionDetailsVO           = transactionManager.getTransDetailFromCommon(icicitransid);
                        transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
                        logger.error("chargeback test key" + merchantDetailsVO.getKey());
                        asyncNotificationService.sendNotification(transactionDetailsVO, icicitransid, notification_status, response.getResponseDesceiption());
                    }

                    if ("Y".equalsIgnoreCase(merchantDetailsVO.getChargebackMailsend()))
                    {
                        SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();
                        sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.CHARGEBACK_TRANSACTION, icicitransid, mailStatus, cbreason, null);
                    }

                }catch(Exception e){
                    logger.error("PZDBViolationException--->", e);
                }

                logger.error("dfghjklghjkl--->");
                //String reason = rb.getString("reason");
                //String reason1[] = reason.split("\\|");
                BlacklistManager blacklistManager           = new BlacklistManager();
                WhiteListManager whiteListManager           = new WhiteListManager();
                TransactionManager transactionManager       = new TransactionManager();
                TransactionDetailsVO transactionDetailsVO   = transactionManager.getTransDetailFromCommon(icicitransid);
                logger.error("BlacklistDetails::::"+req.getParameter("isBlacklistDetails_"+icicitransid));
                logger.error("RefundDetails::::"+req.getParameter("isRefundDetails_"+icicitransid));
                if (req.getParameter("isBlacklistDetails_"+icicitransid).equals("Current"))
                {
                    try
                    {
                        String blacklistReason = (cbreason +" (Chargeback by Admin) "+"(" + icicitransid + ")");
                        whiteListManager.whiteListEntities(icicitransid);
                        //Set<String> cardNum = new HashSet<>();
                        //Set<String> emailAddr = new HashSet<>();
                        //transactionManager.addCustomerName(transactionDetailsVO.getName(), cardNum, emailAddr);
                       /* if(functions.isValueNull(transactionDetailsVO.getName()))
                        {
                            blacklistManager.addCustomerNameBatch(transactionDetailsVO.getName(), blacklistReason, auditTrailVO);
                        }*/
                        if(functions.isValueNull(transactionDetailsVO.getCcnum()))
                        {
                            blacklistManager.addCustomerCardBatch(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()), blacklistReason, BlacklistEnum.Chargeback_Received.toString(), auditTrailVO);
                        }
                        if(functions.isValueNull(transactionDetailsVO.getEmailaddr()))
                        {
                            blacklistManager.addCustomerEmailBatch(transactionDetailsVO.getEmailaddr(), blacklistReason,auditTrailVO);
                        }
                        /*if(functions.isValueNull(transactionDetailsVO.getTelno()))
                            blacklistManager.addCustomerPhone(transactionDetailsVO.getTelno(), blacklistReason,auditTrailVO);
                       */ logger.error("transactionDetailsVO.getCardtype()--->"+transactionDetailsVO.getCardtype());
                        if("UPI".equalsIgnoreCase(transactionDetailsVO.getCardtype()) && functions.isValueNull(transactionDetailsVO.getCustomerId()))
                        {
                            //vpa Address store in customerId field while transaction
                            blacklistManager.addCustomerVPAAddressBatch(transactionDetailsVO.getCustomerId(),cbreason,auditTrailVO);
                        }
                        //blacklistManager.addCustomerEmailBatch(emailAddr, blacklistReason);
                        //blacklistManager.addCustomerCardBatch(cardNum, blacklistReason, BlacklistEnum.Chargeback_Received.toString());
                    }
                    catch (PZDBViolationException e)
                    {
                        logger.error("Duplicate Entry:::::" , e);
                    }
                }
                if (req.getParameter("isBlacklistDetails_"+icicitransid).equals("All"))
                {
                    try
                    {
                        String blacklistReason = (cbreason +" (Chargeback by Admin) "+"(" + icicitransid + ")");
                        whiteListManager.whiteListEntities(icicitransid);
                        if("UPI".equalsIgnoreCase(transactionDetailsVO.getCardtype()) && functions.isValueNull(transactionDetailsVO.getCustomerId()))
                        {
                            //vpa Address store in customerId field while transaction
                            blacklistManager.addCustomerVPAAddressBatch(transactionDetailsVO.getCustomerId(),cbreason,auditTrailVO);
                        }
                        Set<String> cardNum     = new HashSet<>();
                        Set<String> emailAddr   = new HashSet<>();
                        Set<String> phone       = new HashSet<>();
                        /*if(functions.isValueNull(transactionDetailsVO.getName()))
                            transactionManager.addCustomerName(transactionDetailsVO.getName(), cardNum, emailAddr,phone);*/
                        /*if(functions.isValueNull(transactionDetailsVO.getName())){
                            blacklistManager.addCustomerNameBatch(transactionDetailsVO.getName(), blacklistReason,auditTrailVO);
                        }*/
                        logger.error("emailAddr size---->"+emailAddr.size());
                        logger.error("cardNum size---->"+cardNum.size());

                        try
                        {
                            if (emailAddr.size() > 0)
                                blacklistManager.addCustomerEmailBatch(emailAddr, blacklistReason,auditTrailVO);
                        }catch (PZDBViolationException e){
                            logger.error("Duplicate Entry:::" , e);
                        }
                        try
                        {
                            if (cardNum.size() > 0)
                                blacklistManager.addCustomerCardBatch(cardNum, blacklistReason, BlacklistEnum.Chargeback_Received.toString(),auditTrailVO);
                        }catch (PZDBViolationException e){
                            logger.error("Duplicate Entry:::" , e);
                        }
                        /*try
                        {
                            if(phone.size()>0)
                                blacklistManager.addCustomerPhoneBatch(phone, blacklistReason,auditTrailVO);
                        }catch (PZDBViolationException e){
                            logger.error("Duplicate Entry:::" , e);
                        }*/
                    }
                    catch (PZDBViolationException e)
                    {
                        logger.error("Duplicate Entry:::::" , e);
                    }
                }
            }
            sSuccessMessage.append("</table>");
        }
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("cbmessage", chargeBackMessage.toString());

        if(membersMap!=null){
            Set<String> stringSet                                           = membersMap.keySet();
            List<TransactionDetailsVO> transactionDetailsVOList             = null;
            HashMap<String,List<TransactionDetailsVO>> memberTransactionMap = null;
            String refundreasonForMail  = "Current transaction have been identified as fraudulent by the system. Auto refunded by the system to avoid any future chargebacks.";
            String refundreason         = "Auto Refunded by due to Chargeback received from same Customer.";
            logger.error("stringSet--->"+stringSet);
            for(String key:stringSet)
            {
                List<Hashtable> refundList  = membersMap.get(key);
                transactionDetailsVOList    = new ArrayList<>();
                memberTransactionMap        = new HashMap<String,List<TransactionDetailsVO>>();
                for (Hashtable hashtable : refundList)
                {
                    String trackingId = (String) hashtable.get("icicitransid");
                    if ("Y".equals(req.getParameter("isRefundDetails_" + trackingId)))
                    {
                        /*sErrorMessage.append("<center><BR/>");
                        sErrorMessage.append("<center><BR/>");
                        sErrorMessage.append("<center><table border=2>");
                        sErrorMessage.append("<tr colspan=10 class=texthead>");
                        sErrorMessage.append("<td colspan=2 align=center>Tracking Id </td>");
                        sErrorMessage.append("<td colspan=2 align=center>Status</td>");
                        sErrorMessage.append("<td colspan=4 align=center>Description</td>");
                        sErrorMessage.append("<td colspan=2 align=center>Action</td>");
                        sErrorMessage.append("</tr>");
                        */
                        //Process refund
                        TransactionManager transactionManager       = new TransactionManager();
                        TransactionDetailsVO transactionDetailsVO   = transactionManager.getTransDetailFromCommon(trackingId);
                        try
                        {
                            AsyncMultipleRefund asyncMultipleRefund = AsyncMultipleRefund.getInstance();
                            asyncMultipleRefund.asyncRefund(transactionDetailsVO, "0", user.getAccountName(), refundreason, refundreasonForMail,"reversed");
                        }
                        catch (Exception e)
                        {
                            logger.error("Exception while refund::::",e);
                            sErrorMessage.append(getMessageForRefund(trackingId, "Failed", "Exception while refund","Refund"));
                            continue;
                        }
                    }
                }
                if(memberTransactionMap != null)
                {
                    Set<String> keySet = memberTransactionMap.keySet();
                    for (String memberId:keySet)
                    {
                        transactionDetailsVOList    = memberTransactionMap.get(memberId);
                        if (transactionDetailsVOList != null && transactionDetailsVOList.size() > 0)
                        {
                            logger.error("before AsynchronousMailService:::");
                            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                            HashMap rfMail          = new HashMap();
                            MailService mailService = new MailService();
                            rfMail.put(MailPlaceHolder.TOID, memberId);
                            rfMail.put(MailPlaceHolder.STATUS, mailStatus);
                            rfMail.put(MailPlaceHolder.REASON, refundreasonForMail);
                            rfMail.put(MailPlaceHolder.MULTIPALTRANSACTION, mailService.getMultipleRefundTransaction(transactionDetailsVOList, refundreasonForMail));
                            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.REFUND_TRANSACTION, rfMail);


                        }
                        logger.error("after AsynchronousMailService:::");

                    }
                }

            }
        }
        sErrorMessage.append("</table>");
        StringBuilder refundMessage = new StringBuilder();
        refundMessage.append("<BR/>");
        refundMessage.append(sSuccessMessage.toString());
        refundMessage.append(sErrorMessage.toString());
        req.setAttribute("cbmessage", refundMessage.toString());

        RequestDispatcher rd = req.getRequestDispatcher("/commonchargeback.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
    public String getMessage(String trackingId,String status,String description,String action)
    {
        StringBuffer stringBuffer   = new StringBuffer();
        stringBuffer.append("<tr colspan=10>");
        stringBuffer.append("<td colspan=2>"+trackingId+"</td>");
        stringBuffer.append("<td colspan=2>"+status+"</td>");
        stringBuffer.append("<td colspan=4>"+description+"</td>");
        stringBuffer.append("<td colspan=2>"+action+"</td>");
        stringBuffer.append("</tr>");
        return stringBuffer.toString();
    }

    public String getMessageForRefund(String trackingId,String status,String description,String action)
    {
        StringBuffer stringBuffer   = new StringBuffer();
        stringBuffer.append("<tr colspan=10>");
        stringBuffer.append("<td colspan=2>"+trackingId+"</td>");
        stringBuffer.append("<td colspan=2>"+status+"</td>");
        stringBuffer.append("<td colspan=4>"+description+"</td>");
        stringBuffer.append("<td colspan=2>"+action+"</td>");
        stringBuffer.append("</tr>");
        return stringBuffer.toString();
    }
}