package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.MerchantConfigManager;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.transactium.psp.ps.v1003.TransactiumPaymentGateway;
import com.payment.transactium.psp.ps.v1003.TransactiumPaymentProcess;
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
 * Created by Admin on 4/3/18.
 */
public class TRANSCTMFrontEndServlet extends HttpServlet
{
    //TransactiumLogger transactionLogger= new TransactiumLogger(TRANSCTMFrontEndServlet.class.getName());
    TransactionLogger transactionLogger= new TransactionLogger(TRANSCTMFrontEndServlet.class.getName());

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
        transactionLogger.error("Inside Transactium Frontend Servlet---"+request.getParameter("trackingId"));
        HttpSession session = request.getSession(true);

        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO= new GenericCardDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();


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
        String firstName="";
        String lastName="";
        String tmpl_Amount="";
        String tmpl_Currency="";
        String ccnum="";
        String customerId = "";
        String notificationUrl="";
        String version="";
        String terminalid="";
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

        Functions functions = new Functions();

        try
        {

            Enumeration enumeration = request.getParameterNames();
            while (enumeration.hasMoreElements())
            {
                String keyName = (String) enumeration.nextElement();
                String keyValue = request.getParameter(keyName);
                transactionLogger.error(keyName + ":" + keyValue);
            }

            PARes = request.getParameter("PaRes");
            String trackingId = request.getParameter("trackingId");
            String MD=request.getParameter("MD");

            TransactiumPaymentProcess paymentProcess = new TransactiumPaymentProcess();
            CommRequestVO commRequestVO = new CommRequestVO();

            paymentProcess.setTransactiumRequestVO(commRequestVO, trackingId);

            CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
            description = transactionDetailsVO.getOrderId();
            orderDescription = transactionDetailsVO.getOrderDesc();
            amount = transactionDetailsVO.getAmount();
            currency = transactionDetailsVO.getCurrency();
            toId = transactionDetailsVO.getToId();
            payModeId = transactionDetailsVO.getPaymentType();
            cardTypeId = transactionDetailsVO.getCardType();
            redirectUrl = transactionDetailsVO.getRedirectUrl();
            dbStatus=transactionDetailsVO.getPrevTransactionStatus();
            customerId = transactionDetailsVO.getCustomerId();
            notificationUrl=transactionDetailsVO.getNotificationUrl();
            version=transactionDetailsVO.getVersion();
            terminalid=transactionDetailsVO.getTerminalId();


            CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
            email = commAddressDetailsVO.getEmail();
            if (functions.isValueNull(commAddressDetailsVO.getFirstname()))
                firstName=commAddressDetailsVO.getFirstname();
            if(functions.isValueNull(commAddressDetailsVO.getLastname()))
                lastName=commAddressDetailsVO.getLastname();
            tmpl_Amount=commAddressDetailsVO.getTmpl_amount();
            tmpl_Currency=commAddressDetailsVO.getTmpl_currency();

            CommCardDetailsVO commCardDetailsVO= commRequestVO.getCardDetailsVO();
            ccnum=commCardDetailsVO.getCardNum();
            expMonth=commCardDetailsVO.getExpMonth();
            expYear=commCardDetailsVO.getExpYear();

            transactionLogger.debug("tmpl_Amount-----"+tmpl_Amount);
            transactionLogger.debug("tmpl_Currency-----"+tmpl_Currency);
            transactionLogger.debug("ccnum-----"+ccnum);


            CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
            accountId = commMerchantVO.getAccountId();

            CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
            commTransactionDetailsVO.setPreviousTransactionId(trackingId);
            commRequestVO.setTransDetailsVO(commTransactionDetailsVO);

            TransactiumPaymentGateway transactiumPaymentGateway = new TransactiumPaymentGateway(accountId);
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
            merchantDetailsVO = merchantConfigManager.getMerchantDetailFromToId(toId);

            auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
            auditTrailVO.setActionExecutorId(toId);

            if (merchantDetailsVO != null)
            {
                clKey = merchantDetailsVO.getKey();
                autoRedirect = merchantDetailsVO.getAutoRedirect();
                logoName = merchantDetailsVO.getLogoName();
                partnerName = merchantDetailsVO.getPartnerName();
                isService = merchantDetailsVO.getIsService();
            }

            commMerchantVO.setIsService(isService);
            String transType = "Sale";
            CommResponseVO transRespDetails = null;
            transactionLogger.error("dbStatus-----" + dbStatus + "--" + trackingId);
            try
            {
                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                {
                    if ("N".equals(isService))
                    {
                        entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, commRequestVO, auditTrailVO, null);
                        transRespDetails = (CommResponseVO) transactiumPaymentGateway.process3DConfirmation(commRequestVO,PARes, MD);
                        transType = "Auth";
                    }
                    else
                    {
                        entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, commRequestVO, auditTrailVO, null);
                        transRespDetails = (CommResponseVO) transactiumPaymentGateway.process3DConfirmation(commRequestVO, PARes,MD);
                    }
                    transactionLogger.error("after gateway confirmation call ------"+transRespDetails.getStatus());
                    transactionLogger.error("Remark------" + transRespDetails.getDescription());

                    if (transRespDetails != null)
                    {
                        transactionStatus = transRespDetails.getStatus();
                        transactionId = transRespDetails.getTransactionId();
                        message = transRespDetails.getDescription();
                        eci=transRespDetails.getEci();
                        transactionLogger.error("eci--------"+eci);

                    }


                    StringBuffer dbBuffer = new StringBuffer();
                    if ("success".equals(transactionStatus))
                    {
                        status = "success";
                        confirmStatus = "Y";
                        responceStatus = "Successful";
                        billingDesc = gatewayAccount.getDisplayName();
                        if ("Sale".equalsIgnoreCase(transType))
                        {
                            transRespDetails.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess' ,eci='"+eci+"'");
                            paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails,null,auditTrailVO);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId,"capturesuccess");
                        }
                        else
                        {
                            transRespDetails.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                            dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful' ,eci='"+eci+"'");
                            paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId,"authsuccessful");
                        }
                    }
                    else
                    {
                        confirmStatus = "N";
                        status = "fail";
                        responceStatus = "Failed(" + message + ")";
                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',eci='"+eci+"'");
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                    }
                    dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                    con = Database.getConnection();
                    Database.executeUpdate(dbBuffer.toString(), con);
                    transactionLogger.error("-----dbBuffer-----" + dbBuffer);

                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                }
                else
                {
                    if(PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status = "success";
                        message = "Transaction Successful";
                        responceStatus = "Successful";
                    }
                    else
                    {
                        status = "fail";
                        message = "Transaction Declined";
                        responceStatus = "Failed";
                    }
                }
                transactionLogger.debug("------1-------");
                commonValidatorVO.setTrackingid(trackingId);
                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setOrderDesc(orderDescription);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                genericTransDetailsVO.setNotificationUrl(notificationUrl);

                transactionLogger.debug("------2-------");
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
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

                transactionLogger.debug("------3-------");
                if (functions.isValueNull(lastName))
                    addressDetailsVO.setLastname(lastName);
                addressDetailsVO.setTmpl_amount(tmpl_Amount);
                addressDetailsVO.setTmpl_currency(tmpl_Currency);
                cardDetailsVO.setCardNum(ccnum);
                cardDetailsVO.setExpMonth(expMonth);
                cardDetailsVO.setExpYear(expYear);

                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setEci(eci);
                commonValidatorVO.setCustomerId(customerId);
                commonValidatorVO.setTerminalId(terminalid);

                transactionUtility.setToken(commonValidatorVO,responceStatus);

                if(functions.isValueNull(notificationUrl))
                {
                    transactionLogger.error("inside sending notification---"+notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1=transactionUtility.getTransactionDetails(commonValidatorVO);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1,trackingId,status,message,"");
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
                PZExceptionHandler.raiseAndHandleDBViolationException("TRANSCTMFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
            }
            finally
            {
                Database.closeConnection(con);
            }

        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("TRANSCTMFrontEndServlet.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause(), toId, null);
        }

        catch (PZDBViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("TRANSCTMFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }
    }
}
