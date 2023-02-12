package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.BlacklistManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.statussync.StatusSyncDAO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

/**
 * Created by Admin on 25-Feb-22.
 */
public class EBBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionlogger = new TransactionLogger(EBBackEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req, res);
        }
        catch (PZDBViolationException e)
        {
            transactionlogger.error("Entering EBBackEndServlet ......", e);
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req, res);
        }
        catch (PZDBViolationException e)
        {
            transactionlogger.error("Entering EBBackEndServlet ......", e);
        }
    }

    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, PZDBViolationException
    {
        transactionlogger.error("Entering EBBackEndServlet ......");
        TransactionManager transactionManager = new TransactionManager();
        Functions functions = new Functions();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommResponseVO commResponseVO = new CommResponseVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        ActionEntry entry = new ActionEntry();
        StatusSyncDAO statusSyncDAO =new StatusSyncDAO();
        Connection con = null;
        String trackingId = "";
        TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
        String toId = "";
        String accountId = "";
        String fromType = "";
        String paymentId = "";
        String respAmount = "";
        String amount = "";
        String currency = "";
        String errorCode = "";
        String description = "";
        String descriptor = "";
        String transactionStatus = "";
        String tmpl_currency = "";
        String tmpl_amt = "";
        String timestamp = "";
        String eci = "";
        String transactionId = "";
        String isService = "";
        String transType = "";
        String status = "";
        String message = "";
        String dbStatus = "";
        String errorName = "";
        String confirmStatus = "";
        String billingDesc = "";
        String merchantTransactionId = "";
        String notificationUrl = "";
        String updatedStatus = "";
        String cardType = "";
        String cardTypeName = "";
        String bankTransactionDate = "";
        String txnStatus = "";
        String txnFinalStatus = "";
        String email = "";
        String custId = "";
        String restatus = "";

        String ipnevent = "";
        String postdata = "";
        //String status;
        //  String message;
        String authcode = "";
        String code = "";
        String RESPONSE_CODE = "";
        String orderid = "";
        String transactionid = "";
        String custIp = "";

        try
        {
            if (transactionDetailsVO != null)
            {
                accountId = transactionDetailsVO.getAccountId();
                cardType = transactionDetailsVO.getCardtype();
                toId = transactionDetailsVO.getToid();
                dbStatus = transactionDetailsVO.getStatus();
                notificationUrl = transactionDetailsVO.getNotificationUrl();
                amount = transactionDetailsVO.getAmount();
                currency = transactionDetailsVO.getCurrency();
                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                custId = transactionDetailsVO.getCustomerId();
                custIp = transactionDetailsVO.getCustomerIp();
                email = transactionDetailsVO.getEmailaddr();

                if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                    transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                    transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));

                auditTrailVO.setActionExecutorName("EBBackEndServlet");
                auditTrailVO.setActionExecutorId(toId);
                merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());

                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                transactionlogger.error("DbStatus------>" + dbStatus);
                transactionlogger.error("PZTransactionStatus.AUTH_STARTED------>" + PZTransactionStatus.AUTH_STARTED.toString());
                BlacklistManager blacklistManager = new BlacklistManager();
                BlacklistVO blacklistVO = new BlacklistVO();

                if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(dbStatus))
                {
                    transactionlogger.error("inside if ------");
                    con = Database.getConnection();

                    commResponseVO.setCurrency(currency);
                    commResponseVO.setTmpl_Amount(tmpl_amt);
                    commResponseVO.setTmpl_Currency(transactionDetailsVO.getCurrency());
                    if (functions.isValueNull(respAmount))
                    {
                        Double compRsAmount = Double.valueOf(respAmount);
                        Double compDbAmount = Double.valueOf(amount);

                        transactionlogger.error("Easebuzz backend response amount --->" + compRsAmount);
                        transactionlogger.error("Easebuzz backend DB Amount--->" + compDbAmount);
                        if (!compDbAmount.equals(compRsAmount))
                        {
                            txnStatus= "TXN_FAILED";
                            message="Failed-IRA";
                            transactionlogger.error("inside else Amount incorrect--->"+respAmount);
                           // RESPONSE_CODE="11111";
                            amount=respAmount;
                            blacklistVO.setVpaAddress(custId);
                            blacklistVO.setIpAddress(custIp);
                            blacklistVO.setEmailAddress(email);
                            blacklistVO.setActionExecutorId(toId);
                            blacklistVO.setActionExecutorName("EBBackEndServlet");
                            blacklistVO.setRemark("IncorrectAmount");
                            blacklistManager.commonBlackListing(blacklistVO);
                        }

                    }

                    StringBuffer dbBuffer = new StringBuffer();
                    if ("true".equalsIgnoreCase(txnStatus))
                    {
                        restatus="success";
                        billingDesc=gatewayAccount.getDisplayName();
                        // message = "Transaction Successful";
                        commResponseVO.setTransactionId(transactionid);
                        commResponseVO.setResponseHashInfo(authcode);
                        commResponseVO.setDescription(message);
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescriptor(billingDesc);
                        // dbStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        dbBuffer.append("update transaction_common set captureamount='" + respAmount + "',currency='" + currency + "' ,paymentid='" + authcode + "',status='capturesuccess' ,remark='" + message + "' where trackingid = " + trackingId);
                        transactionlogger.error("dbBuffer->" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, respAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                        updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }
                    else if ("fail".equalsIgnoreCase(txnStatus) || "false".equalsIgnoreCase(txnStatus) || txnStatus.contains("FAIL") || txnStatus.contains("fail"))
                    {
                        restatus="Failed";
                        //   message = txnStatus;
                        commResponseVO.setTransactionId(transactionid);
                        commResponseVO.setResponseHashInfo(authcode);
                        commResponseVO.setDescription(message);
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark(message);
                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + authcode + "' ,remark='" +  message + "'");

                        dbBuffer.append(" where trackingid = " + trackingId);

                        transactionlogger.error("dbBuffer->" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                        updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }
                    if (functions.isValueNull(updatedStatus) && functions.isValueNull(notificationUrl))
                    {
                        transactionlogger.error("Inside sending notification---" + notificationUrl);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        transactionDetailsVO.setBillingDesc(billingDesc);
                        asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, restatus, message, "");
                    }

                }
            }
        }
        catch (SystemError systemError)
        {
            systemError.printStackTrace();
        }

    }
}
