package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.Enumeration;

/**
 * Created by Sandip on 5/21/2018.
 */
public class PaySecFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(PaySecFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession(true);

        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO = new AuditTrailVO();

        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        MerchantDAO merchantDAO= new MerchantDAO();

        TransactionUtility transactionUtility = new TransactionUtility();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        Functions functions = new Functions();
        CommResponseVO commResponseVO = new CommResponseVO();

        MerchantDetailsVO merchantDetailsVO = null;
        Connection con = null;

        String toId = "";
        String payModeId = "";
        String cardTypeId = "";
        String isService = "";
        String accountId = "";
        String status = "";
        String responseStatus = "";
        String amount = "";
        String description = "";
        String orderDescription = "";
        String redirectUrl = "";
        String autoRedirect = "";
        String logoName = "";
        String partnerName = "";
        String dbStatus = "";
        String firstName = "";
        String lastName = "";
        String templateAmount = "";
        String templateCurrency = "";
        String cardNumber = "";
        String currency = "";
        String billingDesc = "";
        String message = "";
        String email = "";
        String eci = "";
        String errorName = "";
        String transactionId = "";
        String customerId="";
        String version="";
        String notificationUrl="";
        String updatedStatus="";

        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String keyName = (String) enumeration.nextElement();
            String keyValue = request.getParameter(keyName);
            transactionLogger.error(keyName + ":" + keyValue);
        }
        String bankStatus = request.getParameter("status");
        String transactionReference = request.getParameter("transactionReference");
        String trackingId = request.getParameter("cartId");
        String orderAmount = request.getParameter("orderAmount");
         currency = request.getParameter("currency");

        transactionId = transactionReference;


        try
        {
            if (functions.isValueNull(trackingId))
            {
                TransactionManager transactionManager = new TransactionManager();
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                description = transactionDetailsVO.getDescription();
                orderDescription = transactionDetailsVO.getOrderDescription();
                amount = transactionDetailsVO.getAmount();
                currency = transactionDetailsVO.getCurrency();
                toId = transactionDetailsVO.getToid();
                payModeId = transactionDetailsVO.getPaymodeId();
                cardTypeId = transactionDetailsVO.getCardTypeId();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                dbStatus = transactionDetailsVO.getStatus();
                templateAmount = transactionDetailsVO.getTemplateamount();
                templateCurrency = transactionDetailsVO.getTemplatecurrency();
                email = transactionDetailsVO.getEmailaddr();
                firstName = transactionDetailsVO.getFirstName();
                lastName = transactionDetailsVO.getLastName();
                accountId = transactionDetailsVO.getAccountId();
                customerId = transactionDetailsVO.getCustomerId();
                version = transactionDetailsVO.getVersion();
                notificationUrl = transactionDetailsVO.getNotificationUrl();

                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

                merchantDetailsVO = merchantDAO.getMemberDetails(toId);

                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toId);
                if (merchantDetailsVO != null)
                {
                    autoRedirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();
                    isService = merchantDetailsVO.getService();
                }

                String transType = "sale";
                if ("N".equals(isService))
                {
                    transType = "auth";
                }

                try
                {
                    if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                    {
                        message = bankStatus;
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setTransactionType(transType);

                        StringBuffer dbBuffer = new StringBuffer();
                        if ("SUCCESS".equals(bankStatus))
                        {
                            status = "success";
                            responseStatus = "Successful";

                            billingDesc = gatewayAccount.getDisplayName();
                            if ("sale".equals(transType))
                            {
                                dbBuffer.append("update transaction_common set captureamount='" + orderAmount + "',paymentid='" + transactionId + "',status='capturesuccess',eci='" + eci + "'");
                                commResponseVO.setTransactionStatus(status);
                                commResponseVO.setDescription(responseStatus);
                                commResponseVO.setStatus(responseStatus);

                                entry.actionEntryForCommon(trackingId, orderAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                                updatedStatus = "capturesuccess";
                            }
                            else
                            {
                                dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful',eci='" + eci + "'");
                                commResponseVO.setTransactionStatus(status);
                                commResponseVO.setDescription(responseStatus);
                                commResponseVO.setStatus(responseStatus);
                                entry.actionEntryForCommon(trackingId, orderAmount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authsuccessful");
                                updatedStatus = "authsuccessful";
                            }
                        }
                        else
                        {
                            status = "fail";
                            responseStatus = "Failed(" + message + ")";
                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',eci='" + eci + "'");
                            commResponseVO.setTransactionStatus(status);
                            commResponseVO.setDescription(responseStatus);
                            commResponseVO.setStatus(responseStatus);
                            entry.actionEntryForCommon(trackingId, orderAmount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                            updatedStatus = "authfailed";
                        }
                        dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                        con = Database.getConnection();
                        Database.executeUpdate(dbBuffer.toString(), con);

                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                    }
                    else
                    {
                        if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                        {
                            status = "success";
                            responseStatus = "Successful";
                            message = "Transaction is successful";
                            billingDesc = gatewayAccount.getDisplayName();
                            updatedStatus = "capturesuccess";
                        }
                        else if (PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                        {
                            status = "success";
                            responseStatus = "Successful";
                            message = "Transaction is successful";
                            billingDesc = gatewayAccount.getDisplayName();
                            updatedStatus = "authsuccessful";
                        }
                        else if (PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                        {
                            status = "fail";
                            responseStatus = "Failed";
                            message = "Failed";
                            updatedStatus = "authfailed";
                        }
                        else
                        {
                            status = "fail";
                            responseStatus = "Failed(Transaction not found in correct status)";
                            message = "Failed(Transaction not found in correct status)";
                            updatedStatus = "failed";
                        }
                    }
                    commonValidatorVO.setTrackingid(trackingId);
                    genericTransDetailsVO.setOrderId(description);
                    genericTransDetailsVO.setOrderDesc(orderDescription);
                    genericTransDetailsVO.setNotificationUrl(notificationUrl);
                    genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                    genericTransDetailsVO.setAmount(orderAmount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setRedirectUrl(redirectUrl);
                    commonValidatorVO.setLogoName(logoName);
                    commonValidatorVO.setPartnerName(partnerName);
                    commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    commonValidatorVO.setPaymentType(payModeId);
                    commonValidatorVO.setCardType(cardTypeId);
                    commonValidatorVO.setCustomerId(customerId);
                    commonValidatorVO.setErrorName(errorName);
                    addressDetailsVO.setTmpl_amount(templateAmount);
                    addressDetailsVO.setTmpl_currency(templateCurrency);
                    cardDetailsVO.setCardNum(cardNumber);
                    if (functions.isValueNull(email))
                        addressDetailsVO.setEmail(email);
                    if (functions.isValueNull(firstName))
                        addressDetailsVO.setFirstname(firstName);

                    if (functions.isValueNull(lastName))
                        addressDetailsVO.setLastname(lastName);


                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setEci(eci);
                    commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                    if (functions.isValueNull(notificationUrl))
                    {
                        transactionLogger.error("inside sending notification---" + notificationUrl);
                        TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, updatedStatus, message, "");
                    }


                    if ("Y".equalsIgnoreCase(autoRedirect))
                    {
                        transactionUtility.doAutoRedirect(commonValidatorVO, response, responseStatus, billingDesc);
                    }
                    else
                    {
                        request.setAttribute("responceStatus", responseStatus);
                        request.setAttribute("displayName", billingDesc);
                        request.setAttribute("remark", message);
                        request.setAttribute("errorName", errorName);
                        request.setAttribute("transDetail", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);

                        String confirmationPage = "";
                        if (functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                            confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                        else
                            confirmationPage = "/confirmationpage.jsp?ctoken=";
                        RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                        rd.forward(request, response);
                    }
                }
                catch (SystemError se)
                {
                    transactionLogger.error("SystemError::::::", se);
                    PZExceptionHandler.raiseAndHandleDBViolationException("PaySecFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
                }
                finally
                {
                    Database.closeConnection(con);
                }
            }
        }catch(PZDBViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("PaySecFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }
    }
}
