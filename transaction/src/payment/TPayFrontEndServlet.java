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
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.tigerpay.TigerPayPaymentGateway;
import com.payment.validators.vo.CommonValidatorVO;
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
 * Created by Admin on 6/23/2021.
 */
public class TPayFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionlogger = new TransactionLogger(TPayFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req,res);
        }
        catch (PZDBViolationException e)
        {
            transactionlogger.error("Entering TPayFrontEndServlet ......",e);
        }
    }

    public void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req,res);
        }
        catch (PZDBViolationException e)
        {
            transactionlogger.error("Entering TPayFrontEndServlet ......",e);
        }
    }

    public void doService(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException, PZDBViolationException
    {
        transactionlogger.error("....Entering TPayFrontEndServlet ......");
        HttpSession session = req.getSession();
        Functions functions = new Functions();
        ActionEntry entry                           = new ActionEntry();
        AuditTrailVO auditTrailVO                   = new AuditTrailVO();
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
        TransactionUtility transactionUtility       = new TransactionUtility();
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

        String bankTransactionStatus = "";
        String resultCode            = "";
        String email                 = "";
        String tmpl_amt             = "";
        String tmpl_currency        = "";
        String firstName            = "";
        String lastName             = "";
        String paymodeid            = "";
        String cardtypeid           = "";
        String custId               = "";
        String transactionStatus    = "";
        String confirmStatus        = "";
        String responseStatus       = "";
        String transactionId        = "";
        String message              = "";
        String billingDesc          = "";
        String updatedStatus        = "";
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
        String MOP_TYPE             = "";
        String txtdesc              = "";
        String RESPONSE_DATE_TIME   = "";
        String paymentOption        = "";
        String AMOUNT               = "";
        String MerchantorderId      = "";
        String VendorOrderId        = "";
        String txtDate              = "";
        String resStatus            = "";
        String signature            = "";
        String STATUS               = "";
        String RESPONSE_CODE        = "";
        String notificationAmount   = "";
        String responseAmount       = "";
        String firstSix       = "";
        String lastFour       = "";
        String cardHolderName       = "";
        String Authorization_code = "";

        Enumeration enumeration = req.getParameterNames();

        while (enumeration.hasMoreElements())
        {
            String key      = (String) enumeration.nextElement();
            String value    = req.getParameter(key);
            transactionlogger.error("---Key---" + key + "---Value---" + value);
        }

        BufferedReader bufferedReader = req.getReader();
        StringBuilder responseMsg   = new StringBuilder();
        String string;
        while ((string = bufferedReader.readLine()) != null)
        {
            responseMsg.append(string);
        }

        transactionlogger.error("-----TigerPayFrontEndServlet   response-----" + responseMsg);
        String ENCDATA          = "";
        String decryptedData    = "";
        try
        {
            transactionlogger.error("..... TPayFrontEndServlet ......"+trackingId);


            TransactionDetailsVO transactionDetailsVO   = transactionManager.getTransDetailFromCommon(trackingId);
            CommRequestVO requestVO                     = new CommRequestVO();
            BlacklistManager blacklistManager           = new BlacklistManager();
            BlacklistVO blacklistVO                     = new BlacklistVO();

            if (transactionDetailsVO != null)
            {
                ccnum        = transactionDetailsVO.getCcnum();
                if (functions.isValueNull(ccnum))
                {
                    ccnum    = PzEncryptor.decryptPAN(ccnum);
                    firstSix = functions.getFirstSix(ccnum);
                    lastFour = functions.getLastFour(ccnum);
                }
                toid        = transactionDetailsVO.getToid();
                description = transactionDetailsVO.getDescription();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                accountId   = transactionDetailsVO.getAccountId();
                orderDesc   = transactionDetailsVO.getOrderDescription();
                currency    = transactionDetailsVO.getCurrency();
                amount      = transactionDetailsVO.getAmount();
                tmpl_amt    = transactionDetailsVO.getTemplateamount();

                requestIp = Functions.getIpAddress(req);
                transactionlogger.debug("requestIp --- >" + requestIp);
                transactionlogger.debug("notificationUrl ---" + notificationUrl);
                if (functions.isValueNull(tmpl_amt))
                {
                    tmpl_amt = transactionDetailsVO.getTemplateamount();
                }

                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                if (functions.isValueNull(tmpl_currency))
                {
                    tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                }
                else
                {
                    transactionlogger.debug("inside else of tmpl_currency --->");
                    tmpl_currency = "";
                }
                firstName   = transactionDetailsVO.getFirstName();
                lastName    = transactionDetailsVO.getLastName();
                name        = transactionDetailsVO.getFirstName() + " " + transactionDetailsVO.getLastName();

                if(functions.isValueNull(transactionDetailsVO.getName())){
                    cardHolderName=transactionDetailsVO.getName();
                }
                paymodeid       = transactionDetailsVO.getPaymodeId();
                cardtypeid      = transactionDetailsVO.getCardTypeId();
                custId          = transactionDetailsVO.getCustomerId();
                transactionId   = transactionDetailsVO.getPaymentId();
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
                Authorization_code  = transactionDetailsVO.getAuthorization_code();

                auditTrailVO.setActionExecutorName("TPPayFrontEnd");
                auditTrailVO.setActionExecutorId(toid);

                TigerPayPaymentGateway tigerPayPaymentGateway     = new TigerPayPaymentGateway(accountId);
                GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);

                displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                checksum    = Checksum.generateChecksumV2(description, String.valueOf(amount), bankTransactionStatus, clkey, checksumAlgo);


                transactionlogger.error("decryptedData  ---" + decryptedData);

                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                {
                    transactionlogger.error("inside AUTH_STARTED---");
                    StringBuffer dbBuffer = new StringBuffer();

                    commTransactionDetailsVO.setAmount(amount);
                    commTransactionDetailsVO.setCurrency(currency);
                    commTransactionDetailsVO.setAuthorization_code(Authorization_code);
                    commTransactionDetailsVO.setTotype(transactionDetailsVO.getTotype());
                    requestVO.setTransDetailsVO(commTransactionDetailsVO);

                    con             = Database.getConnection();
                    commResponseVO  = (CommResponseVO) tigerPayPaymentGateway.processQuery(trackingId, requestVO);

                    String inStatus     = commResponseVO.getStatus();
                    String inRemark     = commResponseVO.getRemark();
                    String inCode       = commResponseVO.getAuthCode();
                    responseAmount      = commResponseVO.getAmount();
                    String  TXN_ID      = commResponseVO.getTransactionId();
                    String  RRN         = commResponseVO.getRrn();

                    transactionlogger.error("inquiry trackingid--->"+trackingId);
                    transactionlogger.error("inquiry inStatus--->"+inStatus);
                    transactionlogger.error("inquiry amount--->"+responseAmount);
                    transactionlogger.error("inquiry inCode--->"+inCode);
                    transactionlogger.error("inquiry inRemark--->"+inRemark);
                    transactionlogger.error("inquiry VendorOrderId--->"+TXN_ID);

                    if(functions.isValueNull(responseAmount))
                    {
                        Double compRsAmount = Double.valueOf(responseAmount);
                        Double compDbAmount = Double.valueOf(amount);

                        transactionlogger.error("response amount --->" + compRsAmount);
                        transactionlogger.error(" DB Amount--->" + compDbAmount);
                        transactionlogger.error(" DB inStatus--->" + inStatus);

                        if (compDbAmount.equals(compRsAmount))
                        {
                            STATUS = commResponseVO.getStatus();
                            amount = commResponseVO.getAmount();
                            remark = commResponseVO.getRemark();
                        }
                        else if (!compDbAmount.equals(compRsAmount) && "success".equalsIgnoreCase(inStatus))
                        {
                            transactionlogger.error("inside else Amount incorrect--->" + responseAmount);
                            remark = "Failed-IRA";
                            STATUS = "Failed";
                            amount = responseAmount;
                            blacklistVO.setVpaAddress(custId);
                            blacklistVO.setIpAddress(requestIp);
                            blacklistVO.setEmailAddress(email);
                            blacklistVO.setActionExecutorId(toid);
                            blacklistVO.setActionExecutorName("AcquirerFrontEnd");
                            blacklistVO.setRemark("IncorrectAmount Trackingid : "+trackingId);
                            blacklistVO.setFirstSix(firstSix);
                            blacklistVO.setLastFour(lastFour);
                            blacklistVO.setName(cardHolderName);
                            blacklistManager.commonBlackListing(blacklistVO);
                        }
                    }
                    //currency    = commResponseVO.getCurrency();
                    txtdesc     = commResponseVO.getDescription();
                    String bankTransactionDate  = commResponseVO.getBankTransactionDate();

                    commResponseVO.setTmpl_Amount(tmpl_amt);
                    commResponseVO.setTmpl_Currency(tmpl_currency);
                    commResponseVO.setIpaddress(requestIp);

                    transactionlogger.error("currency--->"+currency);
                    transactionlogger.error("inquiry txtdesc--->"+txtdesc);
                    transactionlogger.error("inquiry bankTransactionDate--->"+bankTransactionDate);
                    transactionlogger.error("remark--->"+remark);

                    if ("Success".equalsIgnoreCase(STATUS))
                    {
                        notificationUrl    = transactionDetailsVO.getNotificationUrl();
                        status              = "success";
                        responseStatus      = remark;
                        billingDesc         = displayName;
                        message             = remark;
                        updatedStatus       = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        commResponseVO.setDescriptor(billingDesc);

                        dbStatus = "capturesuccess";

                        dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + TXN_ID + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency +  "',rrn='" +RRN+ "" +"',status='capturesuccess'" + " ,successtimestamp='" + functions.getTimestamp());
                        dbBuffer.append("',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + responseStatus + "' where trackingid = " + trackingId);
                        transactionlogger.error("Update Query---" + dbBuffer);

                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);//+ ",rrn='" + rrn
                    }
                    else if("Failed".equalsIgnoreCase(STATUS))
                    {
                        transactionlogger.error("inside Imoney failed --->");

                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        status          = "Failed";
                        message         = remark;
                        responseStatus  = remark;
                        updatedStatus   = PZTransactionStatus.AUTH_FAILED.toString();

                        dbStatus = "authfailed";
                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + TXN_ID + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "'" + " ,failuretimestamp='" + functions.getTimestamp());

                        dbBuffer.append("',authorization_code='"+amount+"',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" +responseStatus+ "' where trackingid = " + trackingId);
                        transactionlogger.error("Update Query---" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, requestIp);
                    }
                    else {
                        //pending
                        dbStatus    = "authstarted";
                        status      = "Pending";
                        message     = "Pending";
                        transactionlogger.error("inside Imoney pending condition --->"+STATUS+"--"+message);
                    }
                }
                else
                {
                    if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status      = "success";
                        updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();

                        if(functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else
                            message = "Transaction Successful";
                        dbStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                    }else if(PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus)){
                        status  = "failed";

                        if(functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else if(!functions.isValueNull(message))
                            message = "Transaction Failed";
                        dbStatus=PZTransactionStatus.AUTH_FAILED.toString();
                        updatedStatus   = PZTransactionStatus.AUTH_FAILED.toString();
                    }
                    else
                    {
                        status      = "pending";
                        message     = "Transaction pending";
                        dbStatus    = PZTransactionStatus.AUTH_STARTED.toString();
                    }
                }
                statusSyncDAO.updateAllTransactionFlowFlag(trackingId,dbStatus);

                /*AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, responseStatus, billingDesc);

                AsynchronousSmsService smsService = new AsynchronousSmsService();
                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, responseStatus, billingDesc);
*/
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
                commonValidatorVO.setPaymentType(paymodeid);
                commonValidatorVO.setCardType(cardtypeid);

                commonValidatorVO.setCustomerId(custId);
                commonValidatorVO.setCustomerBankId(custId);

                if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                {

                    String expDate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());

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

                cardDetailsVO.setCardNum(ccnum);
                cardDetailsVO.setExpMonth(expMonth);
                cardDetailsVO.setExpYear(expYear);
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setCardDetailsVO(cardDetailsVO);

                if (functions.isValueNull(notificationUrl))
                {
                    transactionlogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, updatedStatus, message, "");
                    transactionlogger.debug("remark message----------------------" + message);
                }

                if ("Y".equalsIgnoreCase(autoredirect))
                {
                    transactionlogger.debug("responseStatus in ---" + dbStatus);
                    transactionUtility.doAutoRedirect(commonValidatorVO, res, status, billingDesc);
                }
                else
                {
                    session.setAttribute("ctoken", ctoken);

                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", status);
                    req.setAttribute("displayName",displayName);
                    req.setAttribute("remark", message);

                    String confirmationPage = "";
                    String version          = (String)session.getAttribute("version");

                    if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    else
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    session.invalidate();
                    RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(req, res);
                }
            }
        }
        catch (Exception e)
        {
            transactionlogger.error("PZDBViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("EPaymentzFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        finally
        {
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
    }
}
