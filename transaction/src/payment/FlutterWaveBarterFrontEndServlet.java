package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.flwBarter.FlutterWaveBarterPaymentGateway;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Enumeration;

/**
 * Created by Rihen on 5-Jan-20.
 */
public class FlutterWaveBarterFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(FlutterWaveBarterFrontEndServlet.class.getName());

    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    @Override
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
        transactionLogger.error("Inside FlutterWaveBarterFrontEndServlet -----");
        TransactionManager transactionManager = new TransactionManager();
        ActionEntry entry = new ActionEntry();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        MerchantDetailsVO merchantDetailsVO = null;
        HttpSession session = request.getSession(true);
        MerchantDAO merchantDAO = new MerchantDAO();
        PaymentManager paymentManager = new PaymentManager();
        Functions functions = new Functions();
        CommRequestVO commRequestVO = new CommRequestVO();
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();

        String toId = "",accountId = "", dbStatus = "", status = "", amount = "", orderDesc = "",orderId="", redirectUrl = "", logoName = "",
                partnerName = "",  responseStatus = "", currency = "", billingDesc = "", custEmail = "", tmpl_amt = "", tmpl_currency = "",
                payModeId = "", cardTypeId = "",  firstName = "", lastName = "", trackingId = "", customerId = "", version = "", notificationUrl = "", terminalId = "",
                autoRedirect = "", message = "", updatedStatus = "", transactionStatus="",transactionId="",confirmStatus="",ccnum="",expMonth="",expYear="",expDate="",
                paymentId="";

        String requestIp=Functions.getIpAddress(request);
        Connection con = null;


        Enumeration enumeration = request.getParameterNames();
        boolean hasElements = enumeration.hasMoreElements();
        transactionLogger.debug("hasElements ----" + hasElements);
        String value="";
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            value = request.getParameter(key);
            transactionLogger.error("FlutterWaveBarterFrontEndServlet Key-----" + key + "----FlutterWaveBarterFrontEndServlet value----" + value);
        }

        StringBuilder responseMsg = new StringBuilder();
        BufferedReader br = request.getReader();
        String str;
        while ((str = br.readLine()) != null)
        {
            responseMsg.append(str);
        }

        transactionLogger.error("-----Notification JSON-----" + value);
        transactionLogger.error("tracking id ========= "+request.getParameter("trackingId"));

        try
        {
            if (functions.isValueNull(request.getParameter("trackingId")))
            {
                trackingId = request.getParameter("trackingId");
            }

            if (functions.isValueNull(trackingId))
            {
                FlutterWaveBarterPaymentGateway flutterWaveBarterPaymentGateway = null;
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                if (transactionDetailsVO != null)
                {
                    toId = transactionDetailsVO.getToid();
                    accountId = transactionDetailsVO.getAccountId();
                    amount = transactionDetailsVO.getAmount();
                    transactionLogger.debug("amount -------" + amount);
                    if (functions.isValueNull(transactionDetailsVO.getOrderDescription()))
                    {
                        orderDesc = transactionDetailsVO.getOrderDescription();
                    }
                    else{
                        orderDesc = transactionDetailsVO.getDescription();
                    }
                    orderId = transactionDetailsVO.getDescription();
                    redirectUrl = transactionDetailsVO.getRedirectURL();
                    tmpl_amt = transactionDetailsVO.getTemplateamount();
                    tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                    dbStatus = transactionDetailsVO.getStatus();
                    payModeId = transactionDetailsVO.getPaymodeId();
                    cardTypeId = transactionDetailsVO.getCardTypeId();
                    custEmail = transactionDetailsVO.getEmailaddr();
                    customerId = transactionDetailsVO.getCustomerId();
                    version = transactionDetailsVO.getVersion();
                    notificationUrl = transactionDetailsVO.getNotificationUrl();
                    terminalId = transactionDetailsVO.getTerminalId();
                    firstName = transactionDetailsVO.getFirstName();
                    lastName = transactionDetailsVO.getLastName();
                    paymentId = transactionDetailsVO.getPaymentId();
                    currency = transactionDetailsVO.getCurrency();

                    if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                    {
                        ccnum = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                    }
                    if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                    {
                        expDate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
                        String temp[] = expDate.split("/");

                        if (functions.isValueNull(temp[0]))
                        {
                            expMonth = temp[0];
                        }
                        if (functions.isValueNull(temp[1]))
                        {
                            expYear = temp[1];
                        }
                    }

                    merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                    if (merchantDetailsVO != null)
                    {
                        autoRedirect = merchantDetailsVO.getAutoRedirect();
                        logoName = merchantDetailsVO.getLogoName();
                        partnerName = merchantDetailsVO.getPartnerName();
                    }
                    auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                    auditTrailVO.setActionExecutorId(toId);


                    transactionLogger.error("dbStatus-----" + dbStatus);

                    flutterWaveBarterPaymentGateway = new FlutterWaveBarterPaymentGateway(accountId);
                    commTransactionDetailsVO.setPreviousTransactionId(paymentId);
                    commTransactionDetailsVO.setAmount(amount);
                    commTransactionDetailsVO.setCurrency(currency);
                    commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                    CommResponseVO commResponseVO = (CommResponseVO) flutterWaveBarterPaymentGateway.processInquiry(commRequestVO);

                    transactionLogger.error("------- below inquiry call in front end ------");
                    transactionLogger.error("------- commResponseVO ------" + commResponseVO.getStatus());

                    StringBuffer dbBuffer = new StringBuffer();

                    if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equals(dbStatus))
                    {
                        try
                        {
                            if (commResponseVO != null)
                            {
                                transactionLogger.error("status -----" + commResponseVO.getStatus());
                                transactionLogger.debug("getDescriptor -----" + commResponseVO.getDescriptor());
                                transactionLogger.debug("getDescription -----" + commResponseVO.getDescription());
                                transactionLogger.debug("getAmount -----" + commResponseVO.getAmount());
                                transactionLogger.debug("getRemark -----" + commResponseVO.getRemark());
                                transactionLogger.debug("getCurrency -----" + commResponseVO.getCurrency());
                                transactionLogger.debug("getBankTransactionDate -----" + commResponseVO.getBankTransactionDate());
                                transactionLogger.debug("getEci -----" + commResponseVO.getEci());
                                transactionLogger.debug("getTransactionId -----" + commResponseVO.getTransactionId());
                                transactionLogger.error("getErrorCode -----" + commResponseVO.getErrorCode());
                                transactionLogger.debug("getTransactionStatus -----" + commResponseVO.getTransactionStatus());

                                if (functions.isValueNull(commResponseVO.getStatus()))
                                {
                                    transactionStatus = commResponseVO.getStatus();
                                }
                                if (functions.isValueNull(commResponseVO.getTransactionId()))
                                {
                                    transactionId = commResponseVO.getTransactionId();
                                }
                                if (functions.isValueNull(commResponseVO.getDescription()))
                                {
                                    message = commResponseVO.getDescription();
                                }
                                if (functions.isValueNull(commResponseVO.getCurrency()))
                                {
                                    currency = commResponseVO.getCurrency();
                                }

                                commResponseVO.setTmpl_Amount(tmpl_amt);
                                commResponseVO.setTmpl_Currency(tmpl_currency);
                                commResponseVO.setIpaddress(requestIp);

                                if ("success".equalsIgnoreCase(transactionStatus))
                                {
                                    status = "Successful";
                                    billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                                    commResponseVO.setDescription(message);
                                    commResponseVO.setStatus(status);
                                    commResponseVO.setRemark(message);
                                    commResponseVO.setDescriptor(billingDesc);

                                    confirmStatus = "Y";
                                    dbStatus = "capturesuccess";
                                    updatedStatus = "capturesuccess";
                                    dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess'");
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);
                                }
                                else
                                {
                                    confirmStatus = "N";
                                    status = "fail";
                                    commResponseVO.setStatus(status);
                                    commResponseVO.setDescription(message);
                                    commResponseVO.setRemark(message);
                                    dbStatus = "authfailed";
                                    updatedStatus = "authfailed";
                                    dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "'");
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, requestIp);
                                }
                                dbBuffer.append(",customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "',remark='" + message + "' where trackingid = " + trackingId);
                                con = Database.getConnection();
                                Database.executeUpdate(dbBuffer.toString(), con);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);

                                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                                AsynchronousSmsService smsService = new AsynchronousSmsService();
                                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                            }
                            else
                            {
                                transactionLogger.debug("-----inside pending-----");
                                status = "pending";
                                message = "FE:Transaction is pending";
                                updatedStatus = "pending";
                            }
                        }
                        catch (PZGenericConstraintViolationException e)
                        {
                            transactionLogger.error("PZGenericConstraintViolationException :::::", e);
                        }
                    }
                    else
                    {
                        if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                        {
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            status = "success";
                            message = "Transaction Successful";
                            updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();

                        }
                        else if (PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                        {
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            status = "success";
                            message = "Transaction Successful";
                            updatedStatus = PZTransactionStatus.AUTH_SUCCESS.toString();
                        }
                        else if (PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                        {
                            status = "fail";
                            message = "Transaction Failed";
                            updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();

                        }
                        else
                        {
                            status = "fail";
                            message = "Transaction Declined";
                            updatedStatus = PZTransactionStatus.FAILED.toString();

                        }
                    }

                    genericTransDetailsVO.setOrderId(orderId);
                    genericTransDetailsVO.setAmount(amount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setOrderDesc(orderDesc);
                    genericTransDetailsVO.setRedirectUrl(redirectUrl);
                    genericTransDetailsVO.setNotificationUrl(notificationUrl);
                    genericTransDetailsVO.setBillingDiscriptor(billingDesc);

                    addressDetailsVO.setEmail(custEmail);
                    addressDetailsVO.setTmpl_amount(tmpl_amt);
                    addressDetailsVO.setTmpl_currency(tmpl_currency);
                    addressDetailsVO.setFirstname(firstName);
                    addressDetailsVO.setLastname(lastName);
                    cardDetailsVO.setCardNum(ccnum);
                    cardDetailsVO.setExpMonth(expMonth);
                    cardDetailsVO.setExpYear(expYear);
                    commonValidatorVO.setLogoName(logoName);
                    commonValidatorVO.setPartnerName(partnerName);
                    commonValidatorVO.setPaymentType(payModeId);
                    commonValidatorVO.setCardType(cardTypeId);
                    commonValidatorVO.setTrackingid(trackingId);

                    commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    commonValidatorVO.setCustomerId(customerId);
                    commonValidatorVO.setTerminalId(terminalId);
                    commonValidatorVO.setActionType("AcquirerFrontEnd");

                    if (functions.isValueNull(notificationUrl))
                    {
                        transactionLogger.error("inside sending notification---" + notificationUrl);
                        TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, updatedStatus, message, "");
                    }

                    if ("Y".equalsIgnoreCase(autoRedirect))
                    {
                        transactionUtility.doAutoRedirect(commonValidatorVO, response, status, billingDesc);
                    }
                    else
                    {
                        transactionLogger.debug("-----inside confirmation page-----");
                        session.setAttribute("ctoken", ctoken);
                        request.setAttribute("transDetail", commonValidatorVO);
                        request.setAttribute("responceStatus", status);
                        request.setAttribute("remark", message);
                        request.setAttribute("displayName", GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                        String confirmationPage = "";

                        if (functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                            confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                        else
                            confirmationPage = "/confirmationpage.jsp?ctoken=";

                        RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                        rd.forward(request, response);
                    }
                }
            }
        }
        catch(SystemError se)
        {
            transactionLogger.error("SystemError::::::", se);
            PZExceptionHandler.raiseAndHandleDBViolationException("FlutterWaveBarterFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
        }
        catch (PZDBViolationException tve)
        {
            transactionLogger.error("PZDBViolationException:::::", tve);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception:::::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
