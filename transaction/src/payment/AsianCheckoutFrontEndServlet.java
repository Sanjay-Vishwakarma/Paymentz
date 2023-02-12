package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
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
import com.payment.asiancheckout.AsianCheckoutPaymentGateway;
import com.payment.asiancheckout.AsianCheckoutUtils;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.verve.VervePaymentGateway;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;


/**
 * Created by Admin on 10/16/2020.
 */
public class AsianCheckoutFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionlogger  = new TransactionLogger(AsianCheckoutFrontEndServlet.class.getName());
    String ctoken                                       = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {   doService(req,res);

    }

    public void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        doService(req,res);

    }


    public void doService(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        transactionlogger.error("Entering AsianCheckoutFrontEndServlet ......");


        HttpSession session                 = req.getSession();
        Functions functions                 = new Functions();
        ActionEntry entry                   = new ActionEntry();
        AuditTrailVO auditTrailVO           = new AuditTrailVO();
        TransactionManager transactionManager       = new TransactionManager();
        CommonValidatorVO commonValidatorVO         = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO         = new MerchantDetailsVO();
        MerchantDAO merchantDAO                     = new MerchantDAO();
        CommResponseVO commResponseVO               = new CommResponseVO();
        GenericAddressDetailsVO addressDetailsVO    = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO          = new GenericCardDetailsVO();
        StatusSyncDAO statusSyncDAO                 = new StatusSyncDAO();
        PaymentManager paymentManager               = new PaymentManager();
        CommRequestVO requestVO                     = new CommRequestVO();
        TransactionUtility transactionUtility       = new TransactionUtility();
        CommAddressDetailsVO commAddressDetailsVO   = new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = new CommTransactionDetailsVO();
        Connection con      = null;
        PreparedStatement p = null;
        ResultSet rs        = null;
        String toid         = "";
        String description  = "";
        String redirectUrl  = "";
        String accountId    = "";
        String orderDesc    = "";
        String currency     = "";
        String clkey        = "";
        String checksumAlgo = "";
        String checksum     = "";
        String autoredirect = "";
        String isService    = "";
        String displayName  = "";
        String isPowerBy    = "";
        String logoName     = "";
        String partnerName  = "";
        String amount       = "";
        String trackingId   = req.getParameter("trackingId");
        String status       = "";
        String remark       = "";

        String bankTransactionStatus    = "";
        String resultCode               = "";
        String email                    = "";

        String tmpl_amt         = "";
        String tmpl_currency    = "";
        String firstName        = "";
        String lastName         = "";
        String paymodeid        = "";
        String cardtypeid       = "";
        String custId           = "";
        String transactionStatus    = "";
        String confirmStatus        = "";
        String responseStatus       = "";
        String transactionId        = "";
        String message              = "";
        String billingDesc          = "";
        String dbStatus             = "";
        String eci                  = "";
        String paymentid            = "";
        String errorCode            = "";
        String name                 = "";
        String notificationUrl      = "";
        String ccnum                = "";
        String expMonth             = "";
        String expYear              = "";
        String requestIp            = "";
        String merchantKey          = "";
        String paymentId            = "";
        String autoRedirect         = "";
        String updatedStatus        = "";
        String responseAmount       = "";
        String RESPONSE_CODE        = "";


        StringBuilder responseMsg   = new StringBuilder();
        BufferedReader br           = req.getReader();
        Enumeration enumeration     = req.getParameterNames();
        String str                  = "";
        String cardtype             = "";
        String ENCDATA              = "";

        while (enumeration.hasMoreElements())
        {
            String key      = (String) enumeration.nextElement();
            String value    = req.getParameter(key);
            transactionlogger.error("---Key---" + key + "---Value---" + value);
        }


        try
        {

            if(req.getParameter("ENCDATA") != null){
                ENCDATA = req.getParameter("ENCDATA");
            }
           /* String INTERNAL_ACQUIRER_TYPE=req.getParameter("INTERNAL_ACQUIRER_TYPE");//for PhonePe
            transactionlogger.error("INTERNAL_ACQUIRER_TYPE---->"+INTERNAL_ACQUIRER_TYPE);*/


            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

            if (transactionDetailsVO != null)
            {
                // ccnum = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                toid            = transactionDetailsVO.getToid();
                description     = transactionDetailsVO.getDescription();
                redirectUrl     = transactionDetailsVO.getRedirectURL();
                accountId       = transactionDetailsVO.getAccountId();
                orderDesc       = transactionDetailsVO.getOrderDescription();
                currency        = transactionDetailsVO.getCurrency();
                amount          = transactionDetailsVO.getAmount();
                tmpl_amt        = transactionDetailsVO.getTemplateamount();

                requestIp       = Functions.getIpAddress(req);
                commAddressDetailsVO.setCardHolderIpAddress(requestIp);
                transactionlogger.error("requestIp --- >" + requestIp);
                //  transactionlogger.error("notificationUrl ---" + notificationUrl);
                if (functions.isValueNull(tmpl_amt))
                {
                    tmpl_amt = transactionDetailsVO.getTemplateamount();
                }

                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                if (functions.isValueNull(tmpl_currency))
                {
                    tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                }

                firstName   = transactionDetailsVO.getFirstName();
                lastName    = transactionDetailsVO.getLastName();
                name        = transactionDetailsVO.getFirstName() + " " + transactionDetailsVO.getLastName();
                custId          = transactionDetailsVO.getCustomerId();
                transactionId   = transactionDetailsVO.getPaymentId();
                transactionlogger.error("transactionId --->" + transactionDetailsVO.getPaymentId());
                dbStatus        = transactionDetailsVO.getStatus();
                transactionlogger.error("dbStatus ---" + dbStatus);

                merchantDetailsVO   = merchantDAO.getMemberDetails(toid);
                clkey               = merchantDetailsVO.getKey();
                checksumAlgo        = merchantDetailsVO.getChecksumAlgo();
                autoredirect        = merchantDetailsVO.getAutoRedirect();
                isPowerBy           = merchantDetailsVO.getIsPoweredBy();
                logoName            = merchantDetailsVO.getLogoName();
                isService           = merchantDetailsVO.getIsService();
                partnerName         = merchantDetailsVO.getPartnerName();
                email               = transactionDetailsVO.getEmailaddr();

                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toid);
                transactionlogger.error("accountid in side frontEnd------------>"+accountId);
                BlacklistManager blacklistManager   = new BlacklistManager();
                BlacklistVO blacklistVO             = new BlacklistVO();

                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

                String CryptoKey    = gatewayAccount.getFRAUD_FILE_SHORT_NAME();
                String PAY_ID       = gatewayAccount.getMerchantId();

                AsianCheckoutUtils asianCheckoutUtils   = new AsianCheckoutUtils();
                String decryptedData                    = asianCheckoutUtils.decrypt1(ENCDATA, CryptoKey,PAY_ID);
                transactionlogger.error("decryptedData  ---" + decryptedData);

                displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                {
                    AsianCheckoutPaymentGateway asianCheckoutPaymentGateway = new AsianCheckoutPaymentGateway(accountId);
                    StringBuffer dbBuffer                                   = new StringBuffer();

                    commTransactionDetailsVO.setPreviousTransactionId(transactionId);
                    commTransactionDetailsVO.setResponseHashInfo(transactionId);

                    commTransactionDetailsVO.setAmount(amount);
                    requestVO.setTransDetailsVO(commTransactionDetailsVO);
                    requestVO.setAddressDetailsVO(commAddressDetailsVO);
                    commResponseVO = (CommResponseVO) asianCheckoutPaymentGateway.processQuery(trackingId, requestVO);
                    transactionlogger.error("inside AUTH_STARTED---");

                    if (commResponseVO != null)
                    {
                        transactionlogger.error("status from inquiry -----" + commResponseVO.getStatus());
                        responseAmount      = commResponseVO.getAmount();
                        if(functions.isValueNull(responseAmount)){
                        Double compRsAmount = Double.valueOf(responseAmount)   ;
                        Double compDbAmount = Double.valueOf(amount);

                        transactionlogger.error("response amount --->" + compRsAmount);
                        transactionlogger.error(" DB Amount--->" + compDbAmount);

                        if (compDbAmount.equals(compRsAmount))
                        {
                            transactionStatus   = commResponseVO.getStatus();
                            message             = commResponseVO.getRemark();
                            amount              = responseAmount;
                        }
                        else
                        {
                            transactionStatus   = "authfailed";
                            message             = "Failed-IRA";
                            RESPONSE_CODE       = "11111";
                            amount              = responseAmount;
                            blacklistVO.setVpaAddress(custId);
                            blacklistVO.setIpAddress(requestIp);
                            blacklistVO.setEmailAddress(email);
                            blacklistVO.setActionExecutorId(toid);
                            blacklistVO.setActionExecutorName("ASCfrontendservlet");
                            blacklistVO.setRemark("IncorrectAmount");
                            blacklistManager.commonBlackListing(blacklistVO);
                            transactionlogger.error("inside else Amount incorrect--->" + responseAmount);
                        }
                    }
                            commResponseVO.setTmpl_Amount(tmpl_amt);
                            commResponseVO.setTmpl_Currency(tmpl_currency);
                            commResponseVO.setIpaddress(requestIp);
                            commResponseVO.setDescription(message);
                            commResponseVO.setRemark(message);
                            commResponseVO.setAmount(amount);
                            commResponseVO.setCurrency(currency);


                        if ("success".equalsIgnoreCase(transactionStatus))
                        {
                            transactionlogger.error("inside transactionStatus success");
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            status          = "success";
                            commResponseVO.setDescriptor(billingDesc);
                            responseStatus  = "Successful";
                            dbStatus        = "capturesuccess";
                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',status='capturesuccess',paymentid='" + commResponseVO.getTransactionId() + "',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "',remark='" + message + "' where trackingid = " + trackingId);
                            con     = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);
                        }
                        else if ("pending".equalsIgnoreCase(transactionStatus))
                        {
                            transactionlogger.error("inside transactionStatus pending");
                            status          = "pending";
                            dbStatus        = "authstarted";
                            responseStatus  = "pending";
                            billingDesc     = "";
                            displayName     = "";

                            dbBuffer.append("update transaction_common set customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "',remark='" + message + "' where trackingid = " + trackingId);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);

                        }
                        else if("Failed".equalsIgnoreCase(transactionStatus))
                        {
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            confirmStatus   = "N";
                            status          = "failed";
                            dbStatus        = "authfailed";
                            updatedStatus   = "authfailed";
                            billingDesc     = "";
                            displayName     = "";

                            dbBuffer.append("update transaction_common set paymentid='" + commResponseVO.getTransactionId() + "',status='authfailed',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "', remark = '" + message + "'");
                            dbBuffer.append(" where trackingid = " + trackingId);
                            con =    Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, requestIp);
                            responseStatus = "fail";

                        }
                        else {
                            //pending
                            dbStatus="authstarted";
                            status="Pending";
                            message="Pending";
                            transactionlogger.error("inside Asiancheckout pending condition --->"+message);
                        }

                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                    }
                }

                else
                {
                    if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                    {
                        transactionlogger.error("dbstatus before capture------------------->" + dbStatus);
                        billingDesc     = displayName;
                        status          = "success";
                        message         = "Transaction Successful";
                        responseStatus  = "Successful";
                        dbStatus        = "capturesuccess";
                        transactionlogger.error("dbstatus AFTER capture------------------>" + dbStatus);

                    }
                    else if(PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                    {
                        status          = "failed";
                        message         = "Transaction Declined";
                        responseStatus  = "Failed";
                        dbStatus        = "authfailed";
                    }  // set pending
                    else
                    {
                        status = "pending";
                        message = "Transaction pending";
                        dbStatus=PZTransactionStatus.AUTH_STARTED.toString();

                    }
                }

                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, responseStatus, billingDesc);

                AsynchronousSmsService smsService = new AsynchronousSmsService();
                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, responseStatus, billingDesc);

                commonValidatorVO.setStatus(dbStatus);
                commonValidatorVO.setTrackingid(trackingId);
                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setOrderDesc(orderDesc);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                genericTransDetailsVO.setNotificationUrl(notificationUrl);
                genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                addressDetailsVO.setTmpl_amount(tmpl_amt);
                addressDetailsVO.setTmpl_currency(tmpl_currency);
                addressDetailsVO.setFirstname(firstName);
                addressDetailsVO.setLastname(lastName);
                addressDetailsVO.setCardHolderIpAddress(requestIp);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setPaymentType(transactionDetailsVO.getPaymodeId());
                commonValidatorVO.setCardType(transactionDetailsVO.getCardTypeId());

                if (functions.isValueNull(email))
                    addressDetailsVO.setEmail(email);

                else
                    addressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());

                commonValidatorVO.setCustomerId(custId);
                commonValidatorVO.setCustomerBankId(custId);

                if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                {
                    transactionlogger.error("expDate --->" + transactionDetailsVO.getExpdate());

                    String expDate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
                    transactionlogger.error("expDate --->" + expDate);
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
                if (functions.isValueNull(ccnum))
                    cardDetailsVO.setCardNum(ccnum);

                cardDetailsVO.setExpMonth(expMonth);
                cardDetailsVO.setExpYear(expYear);
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setCardDetailsVO(cardDetailsVO);

                if (functions.isValueNull(notificationUrl))
                {
                    transactionlogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1          = transactionUtility.getTransactionDetails(commonValidatorVO);
                    AsyncNotificationService asyncNotificationService   = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, status, message, "");
                    transactionlogger.error("remark message----------------------" + message);
                }

                if ("Y".equalsIgnoreCase(autoredirect))
                {
                    transactionlogger.error("responseStatus in ---" + responseStatus);
                    transactionUtility.doAutoRedirect(commonValidatorVO, res, status, billingDesc);
                }
                else
                {
                    session.setAttribute("ctoken", ctoken);

                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", status);
                    req.setAttribute("displayName", displayName);
                    req.setAttribute("remark", message);

                    String confirmationPage = "";
                    String version          = (String) session.getAttribute("version");

                    if (functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                    {
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    }
                    else
                    {
                        confirmationPage = "/confirmationpage.jsp?ctoken=";
                    }

                    session.invalidate();
                    RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(req, res);
                }

            }
        }

        catch(NullPointerException e)
        {
            transactionlogger.error("NullPointerException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("AsianCheckoutFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }

        catch(Exception e)
        {
            transactionlogger.error("PZDBViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("AsianCheckoutFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}

