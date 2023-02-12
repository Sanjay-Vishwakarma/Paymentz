package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.PaymentManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.paynetics.core.PayneticsGateway;
import com.payment.paynetics.core.PayneticsPaymentProcess;
import com.payment.paynetics.core.PayneticsRequestVO;
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

/**
 * Created by Sandip on 12/6/2017.
 */
public class PyncsFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger  = new TransactionLogger(PyncsFrontEndServlet.class.getName());
    String ctoken                                       = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req,res);
    }

    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session                         = request.getSession(true);
        ActionEntry entry                           = new ActionEntry();
        AuditTrailVO auditTrailVO                   = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO         = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO    = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO          = new GenericCardDetailsVO();
        TransactionUtility transactionUtility       = new TransactionUtility();
        StatusSyncDAO statusSyncDAO                 = new StatusSyncDAO();
        MerchantDetailsVO merchantDetailsVO         = null;
        Functions functions                         = new Functions();
        PaymentManager paymentManager               = new PaymentManager();

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

        String isPowerBy = "";
        String dbStatus="";
        String firstName="";
        String lastName="";

        String tmpl_Amount="";
        String tmpl_Currency="";
        String ccnum="";
        String expMonth="";
        String expYear="";

        String currency = "";
        String billingDesc = "";
        String message="";
        String email="";
        String eci="";
        String errorName = "";
        String cvv = "";

        String transactionId="";
        String transactionStatus="";
        String customerId="";
        String notificationUrl="";
        String terminalId="";
        String version="";

        String redirectMethod="";
        //String clKey = "";
        //String checksumNew = "";
        //String confirmStatus = "";
        //String token = "";

        String PARes                = request.getParameter("PaRes");
        String trackingId           = request.getParameter("trackingId");
        String MD                   = request.getParameter("MD");
        String cres                 = request.getParameter("cres");
        String threeDSSessionData   = request.getParameter("threeDSSessionData");

        if (functions.isValueNull(MD))
        {
            cvv = PzEncryptor.decryptCVV(MD);
        }
        try
        {
            PayneticsPaymentProcess payneticsPaymentProcess     = new PayneticsPaymentProcess();
            PayneticsRequestVO commRequestVO                    = new PayneticsRequestVO();

            payneticsPaymentProcess.setPayneticsRequestVO(commRequestVO, trackingId, cvv);

            CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
            transactionDetailsVO.setCres(cres);
            commRequestVO.setTransDetailsVO(transactionDetailsVO);
            description         = transactionDetailsVO.getOrderId();
            orderDescription    = transactionDetailsVO.getOrderDesc();
            amount              = transactionDetailsVO.getAmount();
            currency            = transactionDetailsVO.getCurrency();
            toId                = transactionDetailsVO.getToId();
            payModeId           = transactionDetailsVO.getPaymentType();
            cardTypeId          = transactionDetailsVO.getCardType();
            redirectUrl         = transactionDetailsVO.getRedirectUrl();
            dbStatus            = transactionDetailsVO.getPrevTransactionStatus();
            customerId          = transactionDetailsVO.getCustomerId();
            terminalId          = transactionDetailsVO.getTerminalId();
            version             = transactionDetailsVO.getVersion();
            redirectMethod      = transactionDetailsVO.getRedirectMethod();

            CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
            ccnum       = commCardDetailsVO.getCardNum();
            expMonth    = commCardDetailsVO.getExpMonth();
            expYear     = commCardDetailsVO.getExpYear();

            CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
            email           = commAddressDetailsVO.getEmail();
            tmpl_Amount     = commAddressDetailsVO.getTmpl_amount();
            tmpl_Currency   = commAddressDetailsVO.getTmpl_currency();

            if(functions.isValueNull(commAddressDetailsVO.getFirstname()))
                firstName   = commAddressDetailsVO.getFirstname();
            if(functions.isValueNull(commAddressDetailsVO.getLastname()))
                lastName    = commAddressDetailsVO.getLastname();

            CommMerchantVO commMerchantVO   = commRequestVO.getCommMerchantVO();
            accountId                       = commMerchantVO.getAccountId();

            commRequestVO.setPARes(PARes);

            GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);
            MerchantDAO merchantDAO         = new MerchantDAO();
            merchantDetailsVO               = merchantDAO.getMemberDetails(toId);

            auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
            auditTrailVO.setActionExecutorId(toId);
            if(merchantDetailsVO != null){
                autoRedirect    = merchantDetailsVO.getAutoRedirect();
                logoName        = merchantDetailsVO.getLogoName();
                partnerName     = merchantDetailsVO.getPartnerName();
                isService       = merchantDetailsVO.getService();
            }

            String transType = "sale";
            if("N".equals(isService)){
                transType = "auth";
            }

            try
            {
                transactionLogger.debug("dbStatus::::" + dbStatus);
                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equals(dbStatus))
                {
                    /*int k = paymentManager.mark3DTransaction(trackingId);
                    transactionLogger.error("paymentManager.mark3DTransaction().k=" + k);
                    if (k == 1)
                    {*/
                        Comm3DResponseVO transRespDetails = null;
                        PayneticsGateway payneticsGateway = new PayneticsGateway(accountId);

                        entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, commRequestVO, auditTrailVO, null);
                        transRespDetails = (Comm3DResponseVO) payneticsGateway.process3DSaleConfirmation(trackingId, commRequestVO, transType);
                        if (transRespDetails != null)
                        {
                            notificationUrl     = transactionDetailsVO.getNotificationUrl();
                            transactionStatus   = transRespDetails.getStatus();
                            transactionId       = transRespDetails.getTransactionId();
                            message             = transRespDetails.getRemark();
                            eci                 = transRespDetails.getEci();
                            if (functions.isValueNull(transRespDetails.getErrorName()))
                                errorName = transRespDetails.getErrorName();

                            StringBuffer dbBuffer = new StringBuffer();
                            if ("success".equals(transactionStatus))
                            {
                                status = "success";
                                //confirmStatus = "Y";
                                responseStatus = "Successful";
                                billingDesc = gatewayAccount.getDisplayName();
                                if ("sale".equals(transType))
                                {
                                    dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess',eci='" + eci + "',remark='" + message + "'" + ",successtimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                                    con = Database.getConnection();
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                                }
                                else
                                {
                                    dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful',eci='" + eci + "',remark='" + message + "'" + ",successtimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                                    con = Database.getConnection();
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authsuccessful");
                                }
                            }
                            else
                            {
                                status = "failed";
                                //confirmStatus = "N";
                                responseStatus = "Failed(" + message + ")";
                                dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',eci='" + eci + "',remark='" + message + "'" + ",failuretimestamp='" + functions.getTimestamp()  + "' where trackingid = " + trackingId);
                                con = Database.getConnection();
                                Database.executeUpdate(dbBuffer.toString(), con);
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                            }

                            /*AsynchronousMailService asynchronousMailService = AsynchronousMailService.getInstance();
                            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                            AsynchronousSmsService smsService = AsynchronousSmsService.getInstance();
                            smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);*/
                        }
                        else
                        {
                            status = "pending";
                            //confirmStatus = "P";
                            responseStatus = "Pending";
                            message = "Transaction is pending";
                        }
                    /*}
                    else
                    {
                        status = "pending";
                        //confirmStatus = "P";
                        responseStatus = "Pending";
                        message = "Transaction is in progress";
                    }*/
                }
                else
                {
                    if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                    {
                        status = "success";
                        //confirmStatus = "Y";
                        responseStatus = "Successful";
                        message = "Transaction is successful";
                        billingDesc = gatewayAccount.getDisplayName();
                    }
                    else if (PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                    {
                        status = "failed";
                        //confirmStatus = "N";
                        responseStatus = "Failed";
                        message = "Failed";
                    }
                    else if (PZTransactionStatus.AUTHSTARTED_3D.toString().equals(dbStatus))
                    {
                        status = "pending";
                        //confirmStatus = "P";
                        responseStatus = "Pending";
                        message = "Transaction is in progress";
                    }
                    else
                    {
                        status = "fail";
                        //confirmStatus = "N";
                        responseStatus = "Failed(Transaction not found in correct status)";
                        message = "Failed(Transaction not found in correct status)";
                    }
                }
                commonValidatorVO.setTrackingid(trackingId);
                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setOrderDesc(orderDescription);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                genericTransDetailsVO.setNotificationUrl(notificationUrl);
                genericTransDetailsVO.setRedirectMethod(redirectMethod);

                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setPaymentType(payModeId);
                commonValidatorVO.setCardType(cardTypeId);
                commonValidatorVO.setCustomerId(customerId);
                commonValidatorVO.setErrorName(errorName);
                addressDetailsVO.setTmpl_amount(tmpl_Amount);
                addressDetailsVO.setTmpl_currency(tmpl_Currency);
                cardDetailsVO.setCardNum(ccnum);
                cardDetailsVO.setExpMonth(expMonth);
                cardDetailsVO.setExpYear(expYear);

                if (functions.isValueNull(email))
                    addressDetailsVO.setEmail(email);
                if (functions.isValueNull(firstName))
                    addressDetailsVO.setFirstname(firstName);

                if (functions.isValueNull(lastName))
                    addressDetailsVO.setLastname(lastName);


                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setEci(eci);
                commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setTerminalId(terminalId);

                transactionUtility.setToken(commonValidatorVO,status);

                transactionLogger.error("TransactionNotification flag for ---"+toId+"---"+merchantDetailsVO.getTransactionNotification());
                if(functions.isValueNull(notificationUrl))
                {
                    transactionLogger.error("inside sending notification---"+notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1=transactionUtility.getTransactionDetails(commonValidatorVO);
                    transactionDetailsVO1.setTransactionMode("3D");
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1,trackingId,status,message,"");
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

                    if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
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
                PZExceptionHandler.raiseAndHandleDBViolationException("PyncsFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
            }
            finally
            {
                Database.closeConnection(con);
            }
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("PyncsFrontEndServlet.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (PZConstraintViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleConstraintViolationException("PyncsFrontEndServlet.java", "doService()", null, "Transaction", null, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("PyncsFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }
    }
}
