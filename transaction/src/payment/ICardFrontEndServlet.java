package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.MerchantConfigManager;
import com.manager.TransactionManager;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.icard.ICardPaymentGateway;
import com.payment.icard.ICardPaymentProcess;
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
import java.util.Arrays;
import java.util.Enumeration;


/**
 * Created by Admin on 5/6/2019.
 */
public class ICardFrontEndServlet extends HttpServlet
{
    private static ICardLogger transactionLogger = new ICardLogger(ICardFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        doService(req, res);
    }

    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.error("Inside ICardFrontEnd Servlet------");
        HttpSession session = request.getSession(true);

            ActionEntry entry                       = new ActionEntry();
        AuditTrailVO auditTrailVO                   = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO         = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO    = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO          = new GenericCardDetailsVO();
        TransactionUtility transactionUtility       = new TransactionUtility();
        StatusSyncDAO statusSyncDAO                 = new StatusSyncDAO();
        TransactionManager transactionManager       = new TransactionManager();
        MerchantDetailsVO merchantDetailsVO         = null;
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
        String notificationUrl = "";
        String clKey = "";
        String checksumNew = "";
        String autoRedirect = "";
        String logoName = "";
        String partnerName = "";
        String confirmStatus = "";
        String isPowerBy = "";
        String firstName="";
        String lastName="";
        String tmpl_Amount="";
        String tmpl_Currency="";
        String ccnum="";
        String expMonth="";
        String expYear="";
        String token = "";
        String currency = "";
        String billingDesc = "";
        String message = "";
        String email = "";
        String dbStatus="";
        String transactionId = "";
        String transactionStatus = "";
        String PARes ="";
        String eci="";
        String rrn="";
        String authCode="";
        String customerId="";
        String RedirectMethod   = "";
        String requestIp        = Functions.getIpAddress(request);
        Functions functions     = new Functions();

        try
        {
            Enumeration enumeration = request.getParameterNames();
            while (enumeration.hasMoreElements())
            {
                String keyName  = (String) enumeration.nextElement();
                String keyValue = request.getParameter(keyName);
                transactionLogger.error(keyName + ":" + keyValue);
            }

            PARes               = request.getParameter("PaRes");
            String trackingId   = request.getParameter("trackingId");
            String MD           = request.getParameter("MD");
            String cres         = request.getParameter("cres");
            String threeDSSessionData = request.getParameter("threeDSSessionData");

            String cvv  = PzEncryptor.decryptCVV(MD);


            ICardPaymentProcess paymentProcess  = new ICardPaymentProcess();
            CommRequestVO commRequestVO         = new CommRequestVO();

            paymentProcess.setICardRequestVO(commRequestVO, trackingId, cvv);

            CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
            transactionDetailsVO.setCres(cres);
            description             = transactionDetailsVO.getOrderId();
            orderDescription        = transactionDetailsVO.getOrderDesc();
            amount                  = transactionDetailsVO.getAmount();
            currency                = transactionDetailsVO.getCurrency();
            toId                    = transactionDetailsVO.getToId();
            payModeId               = transactionDetailsVO.getPaymentType();
            cardTypeId              = transactionDetailsVO.getCardType();
            redirectUrl             = transactionDetailsVO.getRedirectUrl();
            notificationUrl         = transactionDetailsVO.getNotificationUrl();
            dbStatus                = transactionDetailsVO.getPrevTransactionStatus();
            RedirectMethod          = transactionDetailsVO.getRedirectMethod();

            CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
            email = commAddressDetailsVO.getEmail();
            if (functions.isValueNull(commAddressDetailsVO.getFirstname()))
                firstName=commAddressDetailsVO.getFirstname();
            if(functions.isValueNull(commAddressDetailsVO.getLastname()))
                lastName=commAddressDetailsVO.getLastname();
            tmpl_Amount=commAddressDetailsVO.getTmpl_amount();
            tmpl_Currency=commAddressDetailsVO.getTmpl_currency();
            customerId=commAddressDetailsVO.getCustomerid();

            commAddressDetailsVO.setCardHolderIpAddress(requestIp);

            CommCardDetailsVO commCardDetailsVO= commRequestVO.getCardDetailsVO();
            ccnum       = commCardDetailsVO.getCardNum();
            expMonth    = commCardDetailsVO.getExpMonth();
            expYear     = commCardDetailsVO.getExpYear();

            transactionLogger.error("tmpl_Amount-----" + tmpl_Amount);
            transactionLogger.error("tmpl_Currency-----" + tmpl_Currency);
            //transactionLogger.error("ccnum-----" + ccnum);

            CommMerchantVO commMerchantVO   = commRequestVO.getCommMerchantVO();
            accountId                       = commMerchantVO.getAccountId();

            transactionLogger.error("accountId-----"+accountId);

            CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
            commTransactionDetailsVO.setPreviousTransactionId(trackingId);
            commRequestVO.setTransDetailsVO(commTransactionDetailsVO);

            ICardPaymentGateway iCardPaymentGateway     = new ICardPaymentGateway(accountId);
            GatewayAccount gatewayAccount               = GatewayAccountService.getGatewayAccount(accountId);
            MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
            merchantDetailsVO                           = merchantConfigManager.getMerchantDetailFromToId(toId);

            auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
            auditTrailVO.setActionExecutorId(toId);

            if (merchantDetailsVO != null)
            {
                clKey           = merchantDetailsVO.getKey();
                autoRedirect    = merchantDetailsVO.getAutoRedirect();
                logoName        = merchantDetailsVO.getLogoName();
                partnerName     = merchantDetailsVO.getPartnerName();
                isService       = merchantDetailsVO.getIsService();
            }

            String transType = "Sale";
            Comm3DResponseVO transRespDetails = null;
            transactionLogger.error("dbStatus-----" + dbStatus);
            try
            {
                TransactionDetailsVO transactionDetailsVO2  = transactionManager.getTransDetailFromCommon(trackingId);
                dbStatus                                    = transactionDetailsVO2.getStatus();
                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                {
                    if ("N".equals(isService))
                    {
                        entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, commRequestVO, auditTrailVO, requestIp);
                        transRespDetails = (Comm3DResponseVO) iCardPaymentGateway.processCommon3DAuthConfirmation(trackingId, commRequestVO, PARes);
                        transType = "Auth";
                    }
                    else
                    {
                        entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, commRequestVO, auditTrailVO, requestIp);
                        transRespDetails = (Comm3DResponseVO) iCardPaymentGateway.processCommon3DSaleConfirmation(trackingId, commRequestVO, PARes);
                    }

                    if (transRespDetails != null)
                    {
                        transactionStatus = transRespDetails.getStatus();
                        transactionId = transRespDetails.getTransactionId();
                        message = transRespDetails.getRemark();
                        eci=transRespDetails.getEci();
                        rrn=transRespDetails.getRrn();
                        authCode=transRespDetails.getAuthCode();
                        transactionLogger.error("eci--------"+eci);

                    }
                    transactionLogger.error("Remark------" + transRespDetails.getRemark());

                    StringBuffer dbBuffer = new StringBuffer();
                    if ("success".equals(transactionStatus))
                    {
                        status = "success";
                        confirmStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        responseStatus = "Successful";
                        billingDesc = gatewayAccount.getDisplayName();
                        if ("Sale".equalsIgnoreCase(transType))
                        {
                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess' ,eci='"+eci+"',rrn='"+rrn+"',authorization_code='"+authCode+"',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "',remark='" + message + "' where trackingid = " + trackingId);
                            con = Database.getConnection();
                            transactionLogger.error("-----dbBuffer-----" + dbBuffer);
                            Database.executeUpdate(dbBuffer.toString(), con);
                            paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails,null,auditTrailVO);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId,"capturesuccess");
                        }
                        else
                        {
                            dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful' ,eci='"+eci+"',rrn='"+rrn+"',authorization_code='"+authCode+"',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "',remark='" + message + "' where trackingid = " + trackingId);
                            con = Database.getConnection();
                            transactionLogger.error("-----dbBuffer-----" + dbBuffer);
                            Database.executeUpdate(dbBuffer.toString(), con);
                            paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId,"authsuccessful");
                        }
                    }
                    else
                    {
                        confirmStatus = PZTransactionStatus.AUTH_FAILED.toString();
                        status = "fail";
                        responseStatus = "Failed(" + message + ")";
                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',eci='"+eci+"',rrn='"+rrn+"',authorization_code='"+authCode+"',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "',remark='" + message + "' where trackingid = " + trackingId);
                        con = Database.getConnection();
                        transactionLogger.error("-----dbBuffer-----" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                    }
                    //dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);

                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                }else
                {
                    if(PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status = "success";
                        message = "Transaction Successful";
                        responseStatus = "Successful";
                        confirmStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                    }
                    else
                    {
                        status = "failed";
                        message = "Transaction Declined";
                        responseStatus = "Failed";
                        confirmStatus = PZTransactionStatus.AUTH_FAILED.toString();
                    }
                }
                commonValidatorVO.setTrackingid(trackingId);
                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setOrderDesc(orderDescription);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                genericTransDetailsVO.setRedirectMethod(RedirectMethod);

                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                genericTransDetailsVO.setNotificationUrl(notificationUrl);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                merchantDetailsVO.setPoweredBy(isPowerBy);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setPaymentType(payModeId);
                commonValidatorVO.setCardType(cardTypeId);
                if (functions.isValueNull(email)){
                    addressDetailsVO.setEmail(email);}
                if (functions.isValueNull(firstName)){
                    addressDetailsVO.setFirstname(firstName);}

                if (functions.isValueNull(lastName)){
                    addressDetailsVO.setLastname(lastName);}
                addressDetailsVO.setTmpl_amount(tmpl_Amount);
                addressDetailsVO.setTmpl_currency(tmpl_Currency);
                cardDetailsVO.setCardNum(ccnum);
                cardDetailsVO.setExpMonth(expMonth);
                cardDetailsVO.setExpYear(expYear);

                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setEci(eci);
                commonValidatorVO.setReason(message);
                commonValidatorVO.setCustomerId(customerId);

                transactionUtility.setToken(commonValidatorVO, confirmStatus);

                transactionLogger.error("TransactionNotification flag for ---"+toId+"---"+merchantDetailsVO.getTransactionNotification());
                if(functions.isValueNull(notificationUrl))
                {
                    transactionLogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                    transactionDetailsVO1.setTransactionMode("3D");
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, confirmStatus, message, "");
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
                    request.setAttribute("transDetail", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);

                    String confirmationPage = "";
                    String version = (String)session.getAttribute("version");
                    if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                    {
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    } else{
                        confirmationPage = "/confirmationpage.jsp?ctoken=";}
                    RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(request, response);
                }
            }
            catch (SystemError se)
            {
                transactionLogger.error("SystemError::::::", se);
                PZExceptionHandler.raiseAndHandleDBViolationException("ICardFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
            }
            finally
            {
                Database.closeConnection(con);
            }

        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("ICardFrontEndServlet.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause(), toId, null);
        }

        catch (PZDBViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("ICardFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }
    }
}
