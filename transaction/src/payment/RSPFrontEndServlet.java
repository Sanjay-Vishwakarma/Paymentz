package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TerminalManager;
import com.manager.TokenManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TokenRequestVO;
import com.manager.vo.TokenResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommCardDetailsVO;
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
 * Created by Jitendra on 5/21/2018.
 */
public class RSPFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(RSPFrontEndServlet.class.getName());
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
        String clKey = "";
        String checksumNew = "";
        String autoRedirect = "";
        String logoName = "";
        String partnerName = "";
        String confirmStatus = "";
        String isPowerBy = "";
        String dbStatus = "";
        String firstName = "";
        String lastName = "";
        String templateAmount = "";
        String templateCurrency = "";
        String cardNumber = "";
        String expiryYear = "";
        String expiryMonth = "";
        String currency = "";
        String billingDesc = "";
        String message = "";
        String email = "";
        String eci = "";
        String errorName = "";
        String transactionId = "";
        String isTokenAllowed = "N";
        String terminalId = "";
        String customerId="";


        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String keyName = (String) enumeration.nextElement();
            String keyValue = request.getParameter(keyName);
            transactionLogger.error(keyName + ":" + keyValue);
        }

        String trackingId = request.getParameter("trackingId");
        String resType = request.getParameter("res_type");
        String resMid = request.getParameter("res_mid");
        String resAccountId = request.getParameter("res_accountid");
        String resTrackId = request.getParameter("res_trackid");
        String resReferenceId = request.getParameter("res_referenceid");
        String resCode = request.getParameter("res_code");
        String resMessage = request.getParameter("res_message");
        String resDescription = request.getParameter("res_description");
        String resAmount = request.getParameter("res_amount");
        String resAuthCode = request.getParameter("res_authcode");
        String resErrorLevel = request.getParameter("res_errorlevel");
        String resRedirectUrl = request.getParameter("res_redirecturl");
        String resRemarks = request.getParameter("res_remarks");
        String resDatetime = request.getParameter("res_datetime");
        String resSignature = request.getParameter("res_signature");

        transactionId = resReferenceId;

        try
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
            terminalId = transactionDetailsVO.getTerminalId();
            customerId=transactionDetailsVO.getCustomerId();
            if (functions.isValueNull(transactionDetailsVO.getCcnum()))
            {
                cardNumber = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
            }
            if (functions.isValueNull(transactionDetailsVO.getExpdate()))
            {
                String expDate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
                String expDateArr[] = expDate.split("/");
                expiryMonth = expDateArr[0];
                expiryYear = expDateArr[1];
            }

            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            MerchantDAO merchantDAO= new MerchantDAO();
            merchantDetailsVO = merchantDAO.getMemberDetails(toId);

            auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
            auditTrailVO.setActionExecutorId(toId);

            if (merchantDetailsVO != null)
            {
                autoRedirect = merchantDetailsVO.getAutoRedirect();
                logoName = merchantDetailsVO.getLogoName();
                partnerName = merchantDetailsVO.getPartnerName();
                isService = merchantDetailsVO.getService();
                isTokenAllowed = merchantDetailsVO.getIsTokenizationAllowed();
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
                    commResponseVO.setTransactionId(transactionId);
                    commResponseVO.setTransactionType(transType);

                    message = resMessage;
                    StringBuffer dbBuffer = new StringBuffer();
                    if ("0".equals(resCode) && "Approved".equals(resMessage))
                    {
                        status = "success";
                        confirmStatus = "Y";
                        responseStatus = "Successful";
                        billingDesc = gatewayAccount.getDisplayName();
                        if ("sale".equals(transType))
                        {
                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess',eci='" + eci + "'");
                            commResponseVO.setTransactionStatus(status);
                            commResponseVO.setDescription(responseStatus);
                            commResponseVO.setStatus(responseStatus);

                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                        }
                        else
                        {
                            dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful',eci='" + eci + "'");
                            commResponseVO.setTransactionStatus(status);
                            commResponseVO.setDescription(responseStatus);
                            commResponseVO.setStatus(responseStatus);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authsuccessful");
                        }
                        if ("Y".equals(isTokenAllowed))
                        {
                            TerminalManager terminalManager = new TerminalManager();
                            if (terminalManager.isTokenizationActiveOnTerminal(toId, terminalId))
                            {
                                TokenManager tokenManager = new TokenManager();
                                String strToken = tokenManager.isCardAvailable(toId, cardNumber);
                                if (functions.isValueNull(strToken))
                                {
                                    String token = strToken;
                                    commonValidatorVO.setToken(token);
                                }
                                else
                                {
                                    TokenRequestVO tokenRequestVO = new TokenRequestVO();
                                    TokenResponseVO tokenResponseVO = null;
                                    CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();

                                    commCardDetailsVO.setCardNum(cardNumber);
                                    commCardDetailsVO.setExpMonth(expiryMonth);
                                    commCardDetailsVO.setExpYear(expiryYear);

                                    tokenRequestVO.setMemberId(toId);
                                    tokenRequestVO.setGeneratedBy(toId);
                                    tokenRequestVO.setTrackingId(trackingId);
                                    tokenRequestVO.setDescription(description);
                                    tokenRequestVO.setCommCardDetailsVO(commCardDetailsVO);
                                    tokenRequestVO.setAddressDetailsVO(addressDetailsVO);
                                    tokenResponseVO = tokenManager.createToken(tokenRequestVO);
                                    if ("success".equals(tokenResponseVO.getStatus()))
                                    {
                                        String token = tokenResponseVO.getToken();
                                        commonValidatorVO.setToken(token);
                                        transactionLogger.error("Token::" + token);
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        confirmStatus = "N";
                        status = "fail";
                        responseStatus = "Failed(" + message + ")";
                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',eci='" + eci + "'");
                        commResponseVO.setTransactionStatus(status);
                        commResponseVO.setDescription(responseStatus);
                        commResponseVO.setStatus(responseStatus);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
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
                    if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals("capturesuccess") || PZTransactionStatus.AUTH_SUCCESS.toString().equals("authsuccessful"))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status = "success";
                        message = "Transaction Successful";
                    }
                    else
                    {
                        status = "fail";
                        message = "Transaction Declined";
                        //responceStatus = "Failed";
                    }
                }
                commonValidatorVO.setTrackingid(trackingId);
                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setOrderDesc(orderDescription);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setPaymentType(payModeId);
                commonValidatorVO.setCardType(cardTypeId);
                commonValidatorVO.setErrorName(errorName);
                addressDetailsVO.setTmpl_amount(templateAmount);
                addressDetailsVO.setTmpl_currency(templateCurrency);
                cardDetailsVO.setCardNum(cardNumber);
                commonValidatorVO.setCustomerId(customerId);
                if (functions.isValueNull(email))
                    addressDetailsVO.setEmail(email);
                if (functions.isValueNull(firstName))
                    addressDetailsVO.setFirstname(firstName);

                if (functions.isValueNull(lastName))
                    addressDetailsVO.setLastname(lastName);


                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setEci(eci);
                commonValidatorVO.setCardDetailsVO(cardDetailsVO);

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
                    String version = (String)session.getAttribute("version");
                    if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    else
                        confirmationPage = "/confirmationpage.jsp?ctoken=";

                    RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(request, response);
                    session.invalidate();
                }
            }
            catch (SystemError se)
            {
                transactionLogger.error("SystemError::::::", se);
                PZExceptionHandler.raiseAndHandleDBViolationException("RSPFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
            }
            finally
            {
                Database.closeConnection(con);
            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("RSPFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }
    }
}
