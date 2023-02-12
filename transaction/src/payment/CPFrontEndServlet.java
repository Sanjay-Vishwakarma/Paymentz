package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.MerchantConfigManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.cardpay.CardPayPaymentGateway;
import com.payment.cardpay.CardPayPaymentProcess;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
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
 * Created by Admin on 7/27/2018.
 */
public class CPFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(CPFrontEndServlet.class.getName());
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
        transactionLogger.error("-----Inside CPFrontEndServlet-----");

        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = request.getParameter(key);

            transactionLogger.error("Key-----" + key + "----value----" + value);
        }

        HttpSession session = request.getSession(true);

        ActionEntry entry                           = new ActionEntry();
        AuditTrailVO auditTrailVO                   = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO         = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO    = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO          = new GenericCardDetailsVO();
        TransactionUtility transactionUtility       = new TransactionUtility();
        StatusSyncDAO statusSyncDAO                 = new StatusSyncDAO();
        MerchantDAO merchantDAO                     = new MerchantDAO();

        MerchantDetailsVO merchantDetailsVO = null;
        Connection con = null;

        String toId = "";
        String payModeId = "";
        String cardTypeId = "";
        String isService = "";
        String accountId = "";
        String status = "";
        String responceStatus = "";
        String amount = "";
        String description = "";
        String orderDescription = "";
        String redirectUrl = "";
        String clKey = "";
        String checksumNew = "";
        String autoRedirect = "";
        String logoName = "";
        String partnerName = "";
        String confirmStatus = "";
        String isPowerBy = "";
        String firstName = "";
        String lastName = "";
        String tmpl_Amount = "";
        String tmpl_Currency = "";
        String ccnum = "";
        String expMonth="";
        String expYear="";

        String token = "";
        String currency = "";
        String billingDesc = "";
        String message = "";
        String email = "";
        String dbStatus = "";
        String updateStatus = "";

        String transactionId = "";
        String authorization_code = "";
        String transactionStatus = "";
        String customerId = "";
        String eci = "";
        String trackingId="";
        String transtype="";
        String approval_Code="";
        String notificationUrl="";
        String terminalId="";
        String PaymentId="";

        String PaRes = "";
        String MD = "";
        String requestIp = Functions.getIpAddress(request);
        Functions functions = new Functions();

        try
        {
            if(functions.isValueNull(request.getParameter("trackingId")))
                trackingId  = request.getParameter("trackingId");
            if(functions.isValueNull(request.getParameter("PaRes")))
                PaRes   = request.getParameter("PaRes");
            if(functions.isValueNull(request.getParameter("MD")))
                MD  = request.getParameter("MD");


            CardPayPaymentProcess paymentProcess = new CardPayPaymentProcess();
            CommRequestVO commRequestVO          = new CommRequestVO();

            Thread.sleep(5000);
            paymentProcess.setCPRequestVO(commRequestVO, trackingId);

            CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
            description         = transactionDetailsVO.getOrderId();
            orderDescription    = transactionDetailsVO.getOrderDesc();
            amount              = transactionDetailsVO.getAmount();
            currency            = transactionDetailsVO.getCurrency();
            toId                = transactionDetailsVO.getToId();
            payModeId           = transactionDetailsVO.getPaymentType();
            cardTypeId          = transactionDetailsVO.getCardType();
            redirectUrl         = transactionDetailsVO.getRedirectUrl();
            dbStatus            = transactionDetailsVO.getPrevTransactionStatus();
            notificationUrl     = transactionDetailsVO.getNotificationUrl();
            terminalId          = transactionDetailsVO.getTerminalId();
            PaymentId          = transactionDetailsVO.getPaymentId();

            CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
            ccnum       = commCardDetailsVO.getCardNum();
            expMonth    = commCardDetailsVO.getExpMonth();
            expYear     = commCardDetailsVO.getExpYear();

            transactionLogger.debug("NotifiactionUrl------"+notificationUrl);


            CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
            commRequestVO.getAddressDetailsVO().setCardHolderIpAddress(requestIp);
            email = commAddressDetailsVO.getEmail();
            if (functions.isValueNull(commAddressDetailsVO.getFirstname()))
                firstName = commAddressDetailsVO.getFirstname();
            if (functions.isValueNull(commAddressDetailsVO.getLastname()))
                lastName = commAddressDetailsVO.getLastname();
            tmpl_Amount = commAddressDetailsVO.getTmpl_amount();
            tmpl_Currency = commAddressDetailsVO.getTmpl_currency();
            if (functions.isValueNull(commAddressDetailsVO.getCustomerid()))
                customerId = commAddressDetailsVO.getCustomerid();
            CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
            accountId = commMerchantVO.getAccountId();

            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

            String threeDsVersion   = gatewayAccount.getThreeDsVersion();
            transactionLogger.debug("ThreeDVersion ------ " + threeDsVersion);

            CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
            commTransactionDetailsVO.setPreviousTransactionId(transactionId);
            commTransactionDetailsVO.setOrderId(trackingId);
            commRequestVO.setTransDetailsVO(commTransactionDetailsVO);


            merchantDetailsVO = merchantDAO.getMemberDetails(toId);

            CardPayPaymentGateway cardPayPaymentGateway = new CardPayPaymentGateway(accountId);
            auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
            auditTrailVO.setActionExecutorId(toId);

            if (merchantDetailsVO != null)
            {
                clKey = merchantDetailsVO.getKey();
                autoRedirect    = merchantDetailsVO.getAutoRedirect();
                logoName        = merchantDetailsVO.getLogoName();
                partnerName     = merchantDetailsVO.getPartnerName();
                isService       = merchantDetailsVO.getIsService();
            }

            commMerchantVO.setIsService(isService);
            commRequestVO.setCommMerchantVO(commMerchantVO);
            String transType = "Sale";

            transactionLogger.debug("dbStatus-----" + dbStatus);
            CommResponseVO transRespDetails = null;
            try
            {
                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                {
                    if("3Dsv2".equalsIgnoreCase(threeDsVersion)){

                        transactionLogger.debug("Inside Front end ThreeDVersion ------ "+threeDsVersion);
                        transactionLogger.debug("Inside Front end getPaymentId ------ "+transactionDetailsVO.getPaymentId());
                        commTransactionDetailsVO.setPreviousTransactionId(transactionDetailsVO.getPaymentId());
                        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);

                        transRespDetails = (CommResponseVO) cardPayPaymentGateway.processInquiry(commRequestVO);
                    }else{

                        if ("N".equals(isService))
                        {
                            entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, commRequestVO, auditTrailVO, requestIp);
                            transRespDetails = (CommResponseVO) cardPayPaymentGateway.process3DAuthConfirmation(PaRes,MD,commRequestVO);

                            transType = "Auth";
                        }
                        else
                        {
                            entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, commRequestVO, auditTrailVO, requestIp);
                            transRespDetails = (CommResponseVO) cardPayPaymentGateway.process3DSaleConfirmation(PaRes, MD,commRequestVO);
                        }
                    }

                    if (transRespDetails != null)
                    {
                        transactionStatus   = transRespDetails.getStatus();
                        transactionId       = transRespDetails.getTransactionId();
                        authorization_code  = transRespDetails.getAuthCode();
                        message             = transRespDetails.getDescription();
                        eci                 = transRespDetails.getEci();
                        transactionLogger.error("eci--------"+eci);

                    }
                    if(functions.isValueNull(transRespDetails.getAmount())) // for JPY Currency
                    {
                        double amt  = Double.parseDouble(transRespDetails.getAmount());
                        transRespDetails.setAmount(String.format("%.2f",amt));
                        transactionLogger.debug("Amount------" + transRespDetails.getAmount());
                    }
                    transactionLogger.debug("Remark------" + transRespDetails.getDescription());

                    StringBuffer dbBuffer = new StringBuffer();

                    if ("success".equals(transactionStatus))
                    {
                        status          = "success";
                        confirmStatus   = "Y";
                        responceStatus  = "Successful";
                        billingDesc      = gatewayAccount.getDisplayName();
                        if ("Sale".equalsIgnoreCase(transType))
                        {
                            updateStatus = "capturesuccess";
                            transRespDetails.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess' ,eci='" + eci + "',authorization_code='"+authorization_code+"'" + ",successtimestamp='" + functions.getTimestamp()+"'");
                            paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                        }
                        else
                        {
                            updateStatus = "authsuccessful";
                            transRespDetails.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                            dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful' ,eci='" + eci + "',authorization_code='"+authorization_code+"'" + ",successtimestamp='" + functions.getTimestamp() +"'");
                            paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authsuccessful");
                        }

                        dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                        con         = Database.getConnection();
                        Database.executeUpdate(dbBuffer.toString(), con);
                        transactionLogger.debug("-----dbBuffer-----" + dbBuffer);
                    }
                    else  if ("failed".equalsIgnoreCase(transactionStatus) || "fail".equalsIgnoreCase(transactionStatus))
                    {
                        updateStatus        = "authfailed";
                        confirmStatus       = "N";
                        status              = "fail";
                        responceStatus      = "Failed(" + message + ")";
                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',eci='" + eci + "'" + ",failuretimestamp='" + functions.getTimestamp()  +"'");
                        dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                        con         = Database.getConnection();
                        Database.executeUpdate(dbBuffer.toString(), con);
                        transactionLogger.debug("-----dbBuffer-----" + dbBuffer);

                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                    }else{
                        dbStatus    = "authstarted";
                        updateStatus= "Pending";
                        status      = "Pending";
                        message     = "Pending";
                    }

                    /*AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);*/
                }
                else
                {
                    if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc     = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status          = "success";
                        message         = "Transaction Successful";
                        responceStatus  = "Successful";
                    }else if(PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus)){
                        status          = "fail";
                        message         = "Transaction Declined";
                        responceStatus  = "Failed";
                    }
                    else
                    {
                        updateStatus= "Pending";
                        status      = "Pending";
                        message     = "Pending";
                    }
                }

                commonValidatorVO.setTrackingid(trackingId);
                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setOrderDesc(orderDescription);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                genericTransDetailsVO.setNotificationUrl(notificationUrl);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                genericTransDetailsVO.setRedirectMethod(transactionDetailsVO.getRedirectMethod());

                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setPaymentType(payModeId);
                commonValidatorVO.setCardType(cardTypeId);
                if (functions.isValueNull(email))
                    addressDetailsVO.setEmail(email);
                if (functions.isValueNull(firstName))
                    addressDetailsVO.setFirstname(firstName);

                if (functions.isValueNull(lastName))
                    addressDetailsVO.setLastname(lastName);
                addressDetailsVO.setTmpl_amount(tmpl_Amount);
                addressDetailsVO.setTmpl_currency(tmpl_Currency);
                if (session.getAttribute("language") !=null)
                {
                    addressDetailsVO.setLanguage(session.getAttribute("language").toString());
                }
                else
                {
                    addressDetailsVO.setLanguage("");
                }
                cardDetailsVO.setCardNum(ccnum);
                cardDetailsVO.setExpMonth(expMonth);
                cardDetailsVO.setExpYear(expYear);

                commonValidatorVO.setCustomerId(customerId);
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setEci(eci);
                commonValidatorVO.setTerminalId(terminalId);
                commonValidatorVO.setVersion(commTransactionDetailsVO.getVersion());

                transactionUtility.setToken(commonValidatorVO,responceStatus);

                if(functions.isValueNull(notificationUrl))
                {
                    transactionLogger.error("inside sending notification---"+notificationUrl+"--- for trackingid---"+trackingId);
                    TransactionDetailsVO transactionDetailsVO1=transactionUtility.getTransactionDetails(commonValidatorVO);
                    transactionDetailsVO1.setTransactionMode("3D");
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1,trackingId,updateStatus,message,"");
                }

                if ("Y".equalsIgnoreCase(autoRedirect))
                {
                    transactionUtility.doAutoRedirect(commonValidatorVO, response, responceStatus, billingDesc);
                }
                else
                {
                    request.setAttribute("responceStatus", responceStatus);
                    request.setAttribute("displayName", billingDesc);
                    request.setAttribute("remark", message);
                    request.setAttribute("transDetail", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);

                    String confirmationPage = "";
                    String version = commTransactionDetailsVO.getVersion();
                    if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    else
                        confirmationPage = "/confirmationpage.jsp?ctoken=";

                    transactionLogger.debug("Version value---"+version+"---"+confirmationPage);
                    RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(request, response);
                }
            }
            catch (SystemError se)
            {
                transactionLogger.error("SystemError::::::", se);
                PZExceptionHandler.raiseAndHandleDBViolationException("CPFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
            }
            catch (PZConstraintViolationException e)
            {
                transactionLogger.error("PZConstraintViolationException::::::", e);
                PZExceptionHandler.raiseAndHandleDBViolationException("CPFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause(), toId, null);
            }
            catch (PZGenericConstraintViolationException e)
            {
                transactionLogger.error("PZConstraintViolationException::::::", e);
                PZExceptionHandler.raiseAndHandleDBViolationException("CPFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause(), toId, null);
            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("CPFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (InterruptedException e)
        {
            transactionLogger.error("Exception : "+e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }


}
