package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Enumeration;

/**
 * Created by Admin on 5/26/2021.
 */
public class PayEasyWorldBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(PayEasyWorldBackEndServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.error("---Inside PayEasyWorldBackEndServlet---");
        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = request.getParameter(key);

            transactionLogger.error("Name=" + key + "-----Value=" + value);
        }
        TransactionManager transactionManager = new TransactionManager();
        CommResponseVO transRespDetails = new CommResponseVO();
        Functions functions = new Functions();
        PrintWriter pWriter = response.getWriter();
        String responseCode = "200";
        String responseStatus = "OK";
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        ActionEntry actionEntry = new ActionEntry();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        MerchantDAO merchantDAO = new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO = null;
        Connection con = null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        String trackingId = request.getParameter("trackingId");
        String acquirer = request.getParameter("acquirer");
        String orderAmount = request.getParameter("orderAmount");
        String orderSucceed = request.getParameter("orderSucceed");
        String tradeNo = request.getParameter("tradeNo");
        String message = request.getParameter("orderResult");
        String toId = "";
        String sKey = "";
        String accountId = "";
        String fromType = "";
        String transactionStatus = "";
        String eci = "";
        String transactionId = "";
        String status = "";
        String dbStatus = "";
        String billingDesc = "";
        String notificationUrl = "";
        String updatedStatus = "";
        String cardType = "";
        String cardTypeName = "";
        String currency = "";
        String amount = "";
        try
        {
            if(functions.isValueNull(orderAmount))
                 orderAmount=String.format("%.2f",orderAmount);
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            if (transactionDetailsVO != null)
            {
                accountId = transactionDetailsVO.getAccountId();
                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                billingDesc = gatewayAccount.getDisplayName();
                String secretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
                transactionLogger.debug("secretKey ---" + secretKey);
                cardType = transactionDetailsVO.getCardtype();
                cardTypeName = GatewayAccountService.getCardType(cardType);
                transactionLogger.debug("---cardType---" + cardType);
                transactionLogger.debug("---cardTypeName---" + cardTypeName);
                toId = transactionDetailsVO.getToid();
                dbStatus = transactionDetailsVO.getStatus();
                currency = transactionDetailsVO.getCurrency();
                merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                if (merchantDetailsVO != null)
                    sKey = merchantDetailsVO.getKey();
                transactionDetailsVO.setSecretKey(sKey);

                if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                {
                    transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                }
                if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                {
                    transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));

                }

                transactionLogger.debug("fromType----" + fromType);
                transactionLogger.debug("accountId----" + accountId);
                transactionLogger.debug("toId----" + toId);
                transactionLogger.debug("dbStatus----" + dbStatus);

                auditTrailVO.setActionExecutorName("AcquirerCommonBackEnd");
                auditTrailVO.setActionExecutorId(toId);
                currency = transactionDetailsVO.getCurrency();
                transactionLogger.error("dbStatus-----" + dbStatus);

                transactionLogger.debug("dbStatus------" + dbStatus);

                transRespDetails.setTransactionId(tradeNo);
                transRespDetails.setAmount(orderAmount);
                transRespDetails.setCurrency(currency);
                transactionId = tradeNo;

                if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus) || (PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(dbStatus) && "successful".equalsIgnoreCase(status)))
                {
                    transactionLogger.debug("transactionStatus-----" + transactionStatus);
                    transactionLogger.debug("respAmount-----" + amount);
                    StringBuffer dbBuffer = new StringBuffer();
                    if ("1".equalsIgnoreCase(orderSucceed))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        status="success";
                        transRespDetails.setStatus(status);
                        transRespDetails.setRemark(status);
                        transRespDetails.setDescriptor(billingDesc);
                        dbBuffer.append("update transaction_common set captureamount='" + orderAmount + "',paymentid='" + transactionId + "',status='capturesuccess',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                        transactionLogger.error("-----dbBuffer-----" + dbBuffer);
                        con = Database.getConnection();
                        Database.executeUpdate(dbBuffer.toString(), con);
                        actionEntry.actionEntryForCommon(trackingId, orderAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                        updatedStatus = "capturesuccess";
                        transactionDetailsVO.setBillingDesc(billingDesc);
                    }
                    else if("0".equalsIgnoreCase(orderSucceed))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        status = "failed";
                        transRespDetails.setStatus(status);
                        if (!functions.isValueNull(message))
                        {
                            message = "fail";
                        }
                        transRespDetails.setRemark(message);
                        transRespDetails.setDescription(message);
                        dbBuffer.append("update transaction_common set status='authfailed',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                        transactionLogger.error("-----dbBuffer-----" + dbBuffer);
                        con = Database.getConnection();
                        Database.executeUpdate(dbBuffer.toString(), con);
                        actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                        updatedStatus = "authfailed";
                    }
                    if(functions.isValueNull(status))
                    {
                        AsynchronousMailService asynchronousMailService = AsynchronousMailService.getInstance();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                        AsynchronousSmsService smsService = AsynchronousSmsService.getInstance();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                    }

                    if (functions.isValueNull(notificationUrl))
                    {
                        transactionLogger.error("inside sending notification---" + notificationUrl);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, status, "");
                    }
                }

                JSONObject jsonResObject = new JSONObject();
                jsonResObject.put("responseCode", responseCode);
                jsonResObject.put("responseStatus", responseStatus);
                response.setContentType("application/json");
                response.setStatus(200);
                pWriter.println(jsonResObject.toString());
                pWriter.flush();
                return;
            }
        }
        catch(SystemError systemError)
        {
            transactionLogger.error("SystemError--->" + trackingId + "-->", systemError);
        }
        catch(JSONException e)
        {
            transactionLogger.error("SystemError---->", e);
        }
        catch(PZDBViolationException e)
        {
            transactionLogger.error("SystemError--->" + trackingId + "-->", e);
        }
        finally
        {
            if(con!=null)
                Database.closeConnection(con);
        }


    }
}
