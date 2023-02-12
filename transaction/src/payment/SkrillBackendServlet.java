
package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.manager.BlacklistManager;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.skrill.SkrillResponseVO;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;

/**
 * Created by Trupti on 11/23/2017.
 */
public class SkrillBackendServlet extends PzServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(SkrillBackendServlet.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doService(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doService(request,response);
    }
    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.error("Entering doPost in SkrillBackendServlet::::" + request.getRemoteAddr());

        for (Object key : request.getParameterMap().keySet())
        {
            transactionLogger.error("----for loop SkrillBackEndServlet-----" + key + "=" + request.getParameter((String) key) + "--------------");
        }
        if (!request.getParameterMap().isEmpty())
        {

            TransactionManager transactionManager = new TransactionManager();
            StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
            ActionEntry entry = new ActionEntry();
            CommResponseVO commResponseVO = new CommResponseVO();
            SkrillResponseVO commResponseVO1 = new SkrillResponseVO();
            AuditTrailVO auditTrailVO = new AuditTrailVO();
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            //CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
            //GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
            //GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
            GenericRequestVO genericRequestVO = new GenericRequestVO();
            MerchantDetailsVO merchantDetailsVO = null;
            MerchantDAO merchantDAO = new MerchantDAO();
            Functions functions = new Functions();
            HttpSession session = request.getSession(true);
            Connection con = null;
            PaymentManager paymentManager = new PaymentManager();
            BlacklistManager blacklistManager=new BlacklistManager();
            BlacklistVO blacklistVO=new BlacklistVO();
            String toId = "";
            String isService = "";
            String accountId = "";
            String status = "";
            String amount = "";
            String responseAmount = "";
            String description = "";
            String redirectUrl = "";
            String clKey = "";
            String checksumNew = "";
            String autoRedirect = "";
            String logoName = "";
            String partnerName = "";
            String confirmStatus = "";
            String token = "";
            String orderDesc = "";
            String currency = "";
            String transactionId = "";
            String transactionStatus = "";
            String errorCode = "";
            String message = "";
            String billingDesc = "";
            String transType = "sale";
            String dbStatus = "";
            String tmpl_amt = "";
            String tmpl_currency = "";
            String paymodeid = "";
            String cardtypeid = "";
            String email = "";
            String phone = "";
            String cardHolderName = "";
            String customerEmail = "";
            String customerAmount = "";
            String customerCurrency = "";
            String pay_from_email = "";
            int notificationCount = 0;
            String trackingId = "";

            auditTrailVO.setActionExecutorName("AcquirerBackEnd");
            Transaction transaction = new Transaction();

            if (functions.isValueNull(request.getParameter("transaction_id")))
            {
                if (!functions.isNumericVal(request.getParameter("transaction_id")))
                    trackingId = "";
                else
                    trackingId = ESAPI.encoder().encodeForSQL(me, request.getParameter("transaction_id"));

                trackingId = request.getParameter("transaction_id");
                status = request.getParameter("status");
                pay_from_email = request.getParameter("pay_from_email");
                errorCode = request.getParameter("status");
                responseAmount = request.getParameter("amount");
                transactionLogger.debug("status----" + status);


                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

                toId = transactionDetailsVO.getToid();
                accountId = transactionDetailsVO.getAccountId();
                amount = transactionDetailsVO.getAmount();
                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                description = transactionDetailsVO.getDescription();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                if (functions.isValueNull(transactionDetailsVO.getOrderDescription()))
                    orderDesc = transactionDetailsVO.getOrderDescription();
                currency = transactionDetailsVO.getCurrency();
                paymodeid = transactionDetailsVO.getPaymodeId();
                cardtypeid = transactionDetailsVO.getCardTypeId();
                email = transactionDetailsVO.getEmailaddr();
                phone = transactionDetailsVO.getTelno();
                cardHolderName = transactionDetailsVO.getName();
                dbStatus = transactionDetailsVO.getStatus();
                commResponseVO1.setCurrency(currency);
                commResponseVO1.setAmount(amount);
                commResponseVO1.setDescription(description);
                commResponseVO1.setCustomerAmount(tmpl_amt);
                commResponseVO1.setCustomerCurrency(tmpl_currency);
                commResponseVO1.setStatus(dbStatus);
                commResponseVO1.setFromEmail(pay_from_email);
                transactionLogger.debug("dbStatus----" + dbStatus);

                try
                {
                    merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                    transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
                    if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                    {
                        StringBuffer dbBuffer = new StringBuffer();
                        con = Database.getConnection();
                        notificationCount = 1;
                        if(functions.isValueNull(responseAmount))
                        {
                            Double dbAmount=Double.parseDouble(amount);
                            Double resAmount=Double.parseDouble(responseAmount);
                            amount=responseAmount;
                            if (dbAmount!=resAmount && "2".equalsIgnoreCase(errorCode))
                            {
                                errorCode="9999";
                                commResponseVO.setStatus("failed");
                                message="Failed-IRA";
                                blacklistVO.setEmailAddress(email);
                                blacklistVO.setActionExecutorId(toId);
                                blacklistVO.setActionExecutorName("AcquirerBackEnd");
                                blacklistVO.setRemark("IncorrectAmount");
                                blacklistVO.setName(cardHolderName);
                                blacklistVO.setPhone(phone);
                                blacklistManager.commonBlackListing(blacklistVO);
                            }
                        }
                        if ("2".equals(errorCode))
                        {
                            status = "success";
                            message = "Transaction Successful";
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                            commResponseVO.setDescription(message);
                            commResponseVO.setStatus(status);
                            commResponseVO.setRemark(message);
                            commResponseVO.setDescriptor(billingDesc);

                            confirmStatus = "Y";
                            commResponseVO1.setStatus(dbStatus);
                            dbStatus = "capturesuccess";
                            dbBuffer.append("update transaction_common set notificationCount='" + notificationCount + "',captureamount='" + amount + "',paymentid='" + transactionId + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "',status='capturesuccess'");
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                            paymentManager.updateSkrillNetellerDetailEntry(commResponseVO1, trackingId);
                        }
                        else
                        {
                            confirmStatus = "N";
                            status = "fail";
                            if(!functions.isValueNull(message))
                                message = "Transaction Failed";
                            commResponseVO.setStatus(status);
                            commResponseVO.setDescription(message);
                            commResponseVO.setRemark(message);
                            commResponseVO1.setStatus(dbStatus);
                            dbStatus = "authfailed";
                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "'");
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                            paymentManager.updateSkrillNetellerDetailEntry(commResponseVO1, trackingId);
                        }
                        dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                        transactionLogger.debug("Update Query---" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                    }
                    if (functions.isValueNull(transactionDetailsVO.getNotificationUrl()))
                    {
                        transactionLogger.error("inside sending notification skrill---" + transactionDetailsVO.getNotificationUrl());

                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, status, message, "SKRILL");
                    }
                }
                catch (PZDBViolationException tve)
                {
                    transactionLogger.error("PZDBViolationException:::::", tve);
                }
                catch (SystemError e)
                {
                    transactionLogger.error("SystemError----", e);
                }
                finally
                {
                    Database.closeConnection(con);
                }
            }
        }
    }
}
