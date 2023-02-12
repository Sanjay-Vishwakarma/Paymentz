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
import java.sql.Connection;
import java.util.Enumeration;

/**
 * Created by Jitendra on 18-Jul-19.
 */
public class UnionPayInternationalBackEndServlet extends HttpServlet
{
    private static UnionPayInternationalLogger transactionLogger = new UnionPayInternationalLogger(UnionPayInternationalBackEndServlet.class.getName());

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
        transactionLogger.error("Inside UnionPayInternationalBackEndServlet --->");
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
            transactionLogger.error("----for loop UnionPayInternationalBackEndServlet-----" + key + "=" + req.getParameter((String) key) + "----------");
        }
        StringBuilder responseMsg = new StringBuilder();
        BufferedReader br = req.getReader();
        String str;
        while ((str = br.readLine()) != null)
        {
            responseMsg.append(str);
        }

        String trackingId = req.getParameter("trackingId");
        String queryId = req.getParameter("queryId");
        String respCode = req.getParameter("respCode");
        String respMsg = req.getParameter("respMsg");
        String settleAmt = req.getParameter("settleAmt");
        String txnAmt = req.getParameter("txnAmt");
        String toid = "";
        String responseCode = "200";
        String description = "";
        String redirectUrl = "";
        String accountId = "";
        String orderDesc = "";
        String currency = "";
        String amount = "";
        String amount1 = "";
        String tmpl_amt = "";
        String tmpl_currency = "";
        String firstName = "";
        String lastName = "";
        String paymodeid = "";
        String cardtypeid = "";
        String custId = "";
        String paymentid = "";
        String dbStatus = "";
        String notificationUrl = "";
        String clKey = "";
        String logoName = "";
        String partnerName = "";
        String autoRedirect = "";
        String version = "";
        String confirmationPage = "";
        String billingDesc = "";
        String status = "";
        String message = "";
        String isService = "";
        String transactionStatus = "";
        String transactionId = "";
        String confirmStatus = "";
        String responseStatus = "";
        String responseString = "";
        String custEmail = "";
        String terminalId = "";
        String ccnum = "";
        String eci = "";
        String updatedStatus="";


        try
        {
            transactionLogger.error("trackingId ---------->" + trackingId);
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            if (transactionDetailsVO != null)
            {
                toid = transactionDetailsVO.getToid();
                description = transactionDetailsVO.getDescription();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                accountId = transactionDetailsVO.getAccountId();
                orderDesc = transactionDetailsVO.getOrderDescription();
                currency = transactionDetailsVO.getCurrency();
                // if(transactionDetailsVO.getAmount().contains("."))
                // amount = transactionDetailsVO.getAmount().replace(".","");
                amount1 = transactionDetailsVO.getAmount();
                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                firstName = transactionDetailsVO.getFirstName();
                lastName = transactionDetailsVO.getLastName();
                paymodeid = transactionDetailsVO.getPaymodeId();
                cardtypeid = transactionDetailsVO.getCardTypeId();
                custEmail = transactionDetailsVO.getEmailaddr();
                custId = transactionDetailsVO.getCustomerId();
                paymentid = transactionDetailsVO.getPaymentId();
                dbStatus = transactionDetailsVO.getStatus();
                transactionLogger.debug("dbStatus -----" + dbStatus);
                notificationUrl = transactionDetailsVO.getNotificationUrl();
                terminalId = transactionDetailsVO.getTerminalId();
                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                version = transactionDetailsVO.getVersion();
                ccnum = transactionDetailsVO.getCcnum();
                eci = transactionDetailsVO.getEci();
                NexiPaymentGateway nexiPaymentGateway = new NexiPaymentGateway(accountId);
                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
                merchantDetailsVO = merchantConfigManager.getMerchantDetailFromToId(toid);
                //isService=merchantDetailsVO.getIsService();
                StringBuffer dbBuffer = new StringBuffer();
                transactionId = queryId;

                if (merchantDetailsVO != null)
                {
                    clKey = merchantDetailsVO.getKey();
                    autoRedirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();
                    isService = merchantDetailsVO.getIsService();
                }
                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toid);
                con = Database.getConnection();
                String ResponseMsg= UnionPayInternationalErrorCode.getDescription(respCode);
                transactionLogger.error("ResponseMsg  --->"+ResponseMsg);
                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && respCode.equalsIgnoreCase("00") && isService.equalsIgnoreCase("Y"))
                {
                    transactionLogger.error("Inside AUTH_STARTED ");
                    transRespDetails = new CommResponseVO();
                    billingDesc = gatewayAccount.getDisplayName();
                    status = "Success";
                    responseStatus = "Successful";
                    updatedStatus = "capturesuccess";
                    message = "Transaction Successful";
                    transRespDetails.setDescription(ResponseMsg);
                    transRespDetails.setStatus(status);
                    transRespDetails.setRemark(ResponseMsg);
                    transRespDetails.setDescriptor(billingDesc);
                    transRespDetails.setTransactionId(transactionId);

                    dbBuffer.append("update transaction_common set captureamount='" + amount1 + "',paymentid='" + transactionId + "',status='capturesuccess',remark='" + ResponseMsg + "' where trackingid = " + trackingId);
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                    entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                }
                else if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equalsIgnoreCase(dbStatus)  )
                {
                    transRespDetails = new CommResponseVO();
                    billingDesc = gatewayAccount.getDisplayName();
                    transRespDetails.setDescriptor(billingDesc);
                    status = "success";
                    message = "SYS: Transaction Successful";
                    updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                    dbBuffer.append("update transaction_common set status='capturesuccess' where trackingid ='" + trackingId + "'");
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                }
                else if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && respCode.equalsIgnoreCase("00") && isService.equalsIgnoreCase("N"))
                {
                    transactionLogger.error("Inside AUTH_STARTED And IsService N ");
                    transRespDetails = new CommResponseVO();
                    billingDesc = gatewayAccount.getDisplayName();
                    status = "Success";
                    responseStatus = "Successful";
                    updatedStatus = "authsuccessful";
                    message = "Transaction Successful";
                    transRespDetails.setDescription(ResponseMsg);
                    transRespDetails.setStatus(status);
                    transRespDetails.setRemark(ResponseMsg);
                    transRespDetails.setDescriptor(billingDesc);
                    transRespDetails.setTransactionId(transactionId);

                    dbBuffer.append("update transaction_common set paymentid='" + transactionId + "',status='authsuccessful',remark='" + message + "' where trackingid = " + trackingId);
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                    entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authsuccessful");
                }
                else if (PZTransactionStatus.AUTH_SUCCESS.toString().equalsIgnoreCase(dbStatus))
                {
                    transRespDetails = new CommResponseVO();
                    billingDesc = gatewayAccount.getDisplayName();
                    transRespDetails.setDescriptor(billingDesc);
                    status = "success";
                    message = "SYS: Transaction Successful";
                    updatedStatus=PZTransactionStatus.AUTH_SUCCESS.toString();
                    dbBuffer.append("update transaction_common set status='authsuccessful' where trackingid ='" + trackingId + "'");
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                }
                else if (PZTransactionStatus.AUTH_SUCCESS.toString().equalsIgnoreCase(dbStatus) && !respCode.equalsIgnoreCase("00"))
                {
                    transactionLogger.error("Inside AUTH_STARTED and respCode 00");
                    status = "fail";
                    responseStatus = "Failed";
                    message = "Transaction Failed";
                    updatedStatus = "authfailed";
                    if (functions.isValueNull(ResponseMsg))
                    {
                        transRespDetails.setDescription(ResponseMsg);
                        transRespDetails.setRemark(ResponseMsg);
                    }
                    transRespDetails.setStatus(status);
                    transRespDetails.setTransactionId(transactionId);

                    dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',remark='" + message + "' where trackingid = " + trackingId);
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                    entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                }
                else if (PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(dbStatus))
                {
                    transactionLogger.error("Inside Else Of AUTH_STARTED");
                    status = "fail";
                    responseStatus = "Failed";
                    message = "Transaction Failed";
                    updatedStatus = "authfailed";
                    transRespDetails.setStatus(status);
                    if (functions.isValueNull(ResponseMsg))
                    {
                        transRespDetails.setDescription(ResponseMsg);
                        transRespDetails.setRemark(ResponseMsg);
                    }
                    transRespDetails.setTransactionId(transactionId);

                    dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',remark='" + message + "' where trackingid = " + trackingId);
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                    entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                }
                transactionLogger.error("billingDesc--->"+billingDesc);
                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                AsynchronousSmsService smsService = new AsynchronousSmsService();
                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                if (functions.isValueNull(notificationUrl))
                {
                    transactionLogger.error("inside sending notification---" + notificationUrl);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, message,"");
                }
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
