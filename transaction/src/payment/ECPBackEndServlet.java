package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.MerchantConfigManager;
import com.manager.TransactionManager;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.cupUPI.UnionPayInternationalErrorCode;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.nexi.NexiPaymentGateway;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Enumeration;

/**
 * Created by Balaji Sawant on 28-Sep-19.
 */
public class ECPBackEndServlet  extends HttpServlet
{
    TransactionLogger transactionLogger= new TransactionLogger(ECPBackEndServlet.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }
    protected void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        transactionLogger.error("Inside ECPBackEndServlet ---");
        Functions functions = new Functions();
        TransactionManager transactionManager = new TransactionManager();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommResponseVO transRespDetails = null;
        Connection con = null;
        ActionEntry entry = new ActionEntry();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();

        Enumeration enumeration = req.getParameterNames();
        boolean hasElements = enumeration.hasMoreElements();
        transactionLogger.error("hasElements ----" + hasElements);
        for (Object key : req.getParameterMap().keySet())
        {
            transactionLogger.error("----for loop ECPBackEndServlet-----" + key + "=" + req.getParameter((String) key) + "----------");
        }

        String trackingId = req.getParameter("trackingId");
        String transaction_id_Fromresponse = req.getParameter("transaction_id");
        String terminal_token = req.getParameter("terminal_token");
        String unique_id = req.getParameter("unique_id");
        String transaction_type = req.getParameter("transaction_type");
        String status_fromResponse = req.getParameter("status");
        String authorization_code = req.getParameter("authorization_code");
        String eci = req.getParameter("eci");
        String toid = "";
        String responseCode = "200";
        String accountId = "";
        String currency = "";
        String amount1 = "";
        String tmpl_amt = "";
        String tmpl_currency = "";
        String dbStatus = "";
        String notificationUrl = "";
        String billingDesc = "";
        String status = "";
        String message = "";
        String isService = "";
        String transactionId = "";
        String updatedStatus="";
        PrintWriter pWriter = res.getWriter();


        try
        {
            transactionLogger.error("trackingId ---------->" + trackingId);
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            if (transactionDetailsVO != null)
            {
                toid = transactionDetailsVO.getToid();
                accountId = transactionDetailsVO.getAccountId();
                currency = transactionDetailsVO.getCurrency();
                amount1 = transactionDetailsVO.getAmount();
                dbStatus = transactionDetailsVO.getStatus();
                transactionLogger.debug("dbStatus -----" + dbStatus);
                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                {
                    transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                }
                if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                {
                    transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));

                }
                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
                merchantDetailsVO = merchantConfigManager.getMerchantDetailFromToId(toid);
                StringBuffer dbBuffer = new StringBuffer();
                transactionId = unique_id;

                if (merchantDetailsVO != null)
                {
                    isService = merchantDetailsVO.getIsService();
                    transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
                }
                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toid);
                con = Database.getConnection();

                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && status_fromResponse.equalsIgnoreCase("approved") && isService.equalsIgnoreCase("Y"))
                {
                    notificationUrl = transactionDetailsVO.getNotificationUrl();
                    transactionLogger.error("Inside AUTH_STARTED ");
                    transRespDetails = new CommResponseVO();
                    billingDesc = gatewayAccount.getDisplayName();
                    status = "Success";
                  //  responseStatus = "Successful";
                    updatedStatus = "capturesuccess";
                    message = "Transaction Successful";
                   // transRespDetails.setDescription(ResponseMsg);
                    transRespDetails.setStatus(status);
                   // transRespDetails.setRemark(ResponseMsg);
                   // transRespDetails.setDescriptor(billingDesc);
                    transRespDetails.setTransactionId(unique_id);
                    transRespDetails.setTmpl_Amount(tmpl_amt);
                    transRespDetails.setTmpl_Currency(tmpl_currency);
                    transRespDetails.setCurrency(currency);
                    transRespDetails.setAuthCode(authorization_code);

                    dbBuffer.append("update transaction_common set paymentid='" + unique_id + "',status='capturesuccess',eci='" + eci + "',authorization_code='"+authorization_code+"' where trackingid = " + trackingId);
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                    entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                }
                else if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equalsIgnoreCase(dbStatus)  && status_fromResponse.equalsIgnoreCase("approved") )
                {
                    transactionLogger.debug("Inside CAPTURE_SUCCESS and approved");
                    status = "success";
                    message = "SYS: Transaction Successful";
                    updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                    dbBuffer.append("update transaction_common set paymentid='" + unique_id + "',eci='" + eci + "',authorization_code='"+authorization_code+"' where trackingid ='" + trackingId + "'");
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                }
                else if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && status_fromResponse.equalsIgnoreCase("approved") && isService.equalsIgnoreCase("N"))
                {
                    notificationUrl = transactionDetailsVO.getNotificationUrl();
                    transactionLogger.error("Inside AUTH_STARTED And IsService N ");
                    transRespDetails = new CommResponseVO();
                    billingDesc = gatewayAccount.getDisplayName();
                    status = "Success";
                   // responseStatus = "Successful";
                    updatedStatus = "authsuccessful";
                    message = "Transaction Successful";
                   // transRespDetails.setDescription(ResponseMsg);
                    transRespDetails.setStatus(status);
                   // transRespDetails.setRemark(ResponseMsg);
                    transRespDetails.setDescriptor(billingDesc);
                    transRespDetails.setTransactionId(transactionId);
                    transRespDetails.setTmpl_Amount(tmpl_amt);
                    transRespDetails.setTmpl_Currency(tmpl_currency);
                    transRespDetails.setCurrency(currency);
                    transRespDetails.setAuthCode(authorization_code);

                    dbBuffer.append("update transaction_common set paymentid='" + transactionId + "',remark='" + message + "',authorization_code="+authorization_code+",eci="+eci+" where trackingid = " + trackingId);
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                    entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authsuccessful");
                }
                else if (PZTransactionStatus.AUTH_SUCCESS.toString().equalsIgnoreCase(dbStatus))
                {
                    status = "success";
                    message = "SYS: Transaction Successful";

                    updatedStatus=PZTransactionStatus.AUTH_SUCCESS.toString();
                    dbBuffer.append("update transaction_common set paymentid='" + transactionId + "',status='authsuccessful' ,eci='" + eci +"',authorization_code="+authorization_code+"  where trackingid ='" + trackingId + "'");
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                }
                else if (PZTransactionStatus.REVERSED.toString().equalsIgnoreCase(dbStatus))
                {
                    status = "reversed";
                    message = "SYS: Transaction Reversed";
                    updatedStatus=PZTransactionStatus.REVERSED.toString();
                    dbBuffer.append("update transaction_common set paymentid='" + transactionId + "',status='reversed' ,eci='" + eci +"'  where trackingid ='" + trackingId + "'");
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                }
                else if (PZTransactionStatus.MARKED_FOR_REVERSAL.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.ACCEPTED_FOR_REVERSAL.toString().equalsIgnoreCase(dbStatus) )
                {
                    status = "markedforreversal";
                    message = "SYS: Transaction MarkedForReversal";
                    updatedStatus=PZTransactionStatus.MARKED_FOR_REVERSAL.toString();
                    dbBuffer.append("update transaction_common set paymentid='" + transactionId + "',status='markedforreversal' ,eci='" + eci +"'  where trackingid ='" + trackingId + "'");
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                }
                else if (PZTransactionStatus.AUTH_CANCELLED.toString().equalsIgnoreCase(dbStatus))
                {
                    status = "authcancelled";
                    message = "SYS: Transaction authcancelled";
                    updatedStatus=PZTransactionStatus.AUTH_CANCELLED.toString();
                    dbBuffer.append("update transaction_common set paymentid='" + transactionId + "',status='authcancelled' ,eci='" + eci +"'  where trackingid ='" + trackingId + "'");
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                }
                else if (PZTransactionStatus.CANCEL_STARTED.toString().equalsIgnoreCase(dbStatus))
                {
                    status = "cancelstarted";
                    message = "SYS: Transaction cancelstarted";
                    updatedStatus=PZTransactionStatus.CANCEL_STARTED.toString();
                    dbBuffer.append("update transaction_common set paymentid='" + transactionId + "',status='cancelstarted' ,eci='" + eci +"'  where trackingid ='" + trackingId + "'");
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                }
                else if (PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.FAILED.toString().equalsIgnoreCase(dbStatus))
                {
                    status = "authfailed";
                    message = "SYS: Transaction authfailed";
                    updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                    dbBuffer.append("update transaction_common set paymentid='" + transactionId + "',status='authfailed' ,eci='" + eci +"'  where trackingid ='" + trackingId + "'");
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                }

                else
                {

                    transactionLogger.error("Inside Else Of AUTH_STARTED");
                    transRespDetails = new CommResponseVO();
                    status = "fail";
                  //  responseStatus = "Failed";
                    message = "Transaction Failed";
                    updatedStatus = "authfailed";
                    transRespDetails.setStatus(status);
                    transRespDetails.setTransactionId(transactionId);

                    dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',remark='" + message + "' where trackingid = " + trackingId);
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                    entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                }
                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                AsynchronousSmsService smsService = new AsynchronousSmsService();
                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                if (functions.isValueNull(notificationUrl))
                {
                    transactionLogger.error("inside sending notification---" + notificationUrl);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    transactionDetailsVO.setBillingDesc(billingDesc);
                    asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, message,"");
                }

                String notificationXml=
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                                "<notification_echo>" +
                                "<unique_id>"+unique_id +"</unique_id>"+
                                "</notification_echo>";

                res.setContentType("application/xml");
                pWriter.println(notificationXml.toString());
                pWriter.flush();

            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException---->"+e);
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError---->"+se);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
