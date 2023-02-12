package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.BlacklistManager;
import com.manager.MerchantConfigManager;
import com.manager.TransactionManager;
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
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.powercash21.PowerCash21PaymentGateway;
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
import java.util.Date;
import java.util.Enumeration;

/**
 * Created by Admin on 1/11/2022.
 */
public class PC21FrontendServlet extends HttpServlet
{
    TransactionLogger transactionLogger= new TransactionLogger(PC21FrontendServlet.class.getName());

    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }
    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        transactionLogger.error("Inside PC21FrontendServlet");
        Date date4              = new Date();
        HttpSession session     = req.getSession();
        Functions functions     = new Functions();
        TransactionManager transactionManager   = new TransactionManager();
        StatusSyncDAO statusSyncDAO             = new StatusSyncDAO();
        AuditTrailVO auditTrailVO       = new AuditTrailVO();
        CommResponseVO commResponseVO   = new CommResponseVO();
        CommRequestVO commRequestVO     = new CommRequestVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = new CommTransactionDetailsVO();
        CommonValidatorVO commonValidatorVO                 = new CommonValidatorVO();
        GenericAddressDetailsVO addressDetailsVO            = new GenericAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO         = new GenericTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO      = new GenericCardDetailsVO();
        TransactionUtility transactionUtility   = new TransactionUtility();

        Connection con = null;


        ActionEntry entry                   = new ActionEntry();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();


        Enumeration enumeration = req.getParameterNames();

        while (enumeration.hasMoreElements())
        {
            String key      = (String) enumeration.nextElement();
            String value    = req.getParameter(key);
            transactionLogger.error("Name====>" + key + " ,Value===> " + value);
        }

        String toid = "";
        String description = "";
        String redirectUrl = "";
        String accountId = "";
        String orderDesc = "";
        String currency = "";
        String amount = "";
        String tmpl_amt = "";
        String tmpl_currency = "";
        String firstName = "";
        String lastName = "";
        String paymodeid = "";
        String cardtypeid = "";
        String custId = "";
        String paymentid = "";
        String dbStatus = "";
        String notificationUrl = "";
        String clKey = "";
        String logoName = "";
        String partnerName = "";
        String autoredirect = "";
        String version = "";
        String confirmationPage = "";
        String billingDesc = "";
        String status = "";
        String message = "";
        String isService = "";
        String transactionStatus = "";
        String transactionId = "";
        String confirmStatus = "";
        String responseStatus = "";
        String responseString = "";
        String custEmail = "";
        String terminalId = "";
        String ccnum = "";
        String expDate="";
        String expMonth="";
        String expYear="";
        String eci = "";
        String ipAddress = "";
        String displayName="";
        String responseAmount="";
        String  TXN_ID="";
        String STATUS               = "";
        String remark               = "";
        String firstSix       = "";
        String lastFour       = "";
        String cardHolderName       = "";
        String RedirectMethod       = "";
        try
        {
            String trackingId               = req.getParameter("trackingId");
            String statusFromResponse       = req.getParameter("ccn_four");
            String orderidFromResponse      = req.getParameter("orderid");
            String errorcodeFromResponse    = req.getParameter("errorcode");
            String binFromResponse          = req.getParameter("bin");
            String errormessage             = req.getParameter("errormessage");
            String cardholderFromResponse           = req.getParameter("cardholder");
            String transactionidFromResponse        = req.getParameter("transactionid");
            String ProtocolVersion3dFromResponse    = req.getParameter("3dProtocolVersion");
            String timestampFromResponse            = req.getParameter("timestamp");
            String typeFromResponse                     = req.getParameter("type");
            String authentication_status3dFromResponse  = req.getParameter("3dauthentication_status");
            transactionLogger.error("trackingId ---------->" + trackingId);
            // pasuse for 5 second
            Thread.sleep(5000);
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            BlacklistManager blacklistManager           = new BlacklistManager();
            BlacklistVO blacklistVO                     = new BlacklistVO();
            if (transactionDetailsVO != null)
            {
                accountId       = transactionDetailsVO.getAccountId();
                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                toid            = transactionDetailsVO.getToid();
                description     = transactionDetailsVO.getDescription();
                redirectUrl     = transactionDetailsVO.getRedirectURL();
                orderDesc       = transactionDetailsVO.getOrderDescription();
                currency        = transactionDetailsVO.getCurrency();
                amount          = transactionDetailsVO.getAmount();
                firstName       = transactionDetailsVO.getFirstName();
                lastName        = transactionDetailsVO.getLastName();
                paymodeid       = transactionDetailsVO.getPaymodeId();
                cardtypeid      = transactionDetailsVO.getCardTypeId();
                custEmail       = transactionDetailsVO.getEmailaddr();
                custId      = transactionDetailsVO.getCustomerId();
                paymentid   = transactionDetailsVO.getPaymentId();
                dbStatus    = transactionDetailsVO.getStatus();
                ipAddress   = transactionDetailsVO.getIpAddress();
                terminalId  = transactionDetailsVO.getTerminalId();
                tmpl_amt        = transactionDetailsVO.getTemplateamount();
                tmpl_currency   = transactionDetailsVO.getTemplatecurrency();
                version         = transactionDetailsVO.getVersion();
                RedirectMethod         = transactionDetailsVO.getRedirectMethod();
                ccnum           = transactionDetailsVO.getCcnum();
                if (functions.isValueNull(ccnum))
                {
                    ccnum    = PzEncryptor.decryptPAN(ccnum);
                    firstSix = functions.getFirstSix(ccnum);
                    lastFour = functions.getLastFour(ccnum);
                }
                if(functions.isValueNull(transactionDetailsVO.getName())){
                    cardHolderName=transactionDetailsVO.getName();
                }
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
                eci             = transactionDetailsVO.getEci();
                MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
                merchantDetailsVO       = merchantConfigManager.getMerchantDetailFromToId(toid);
                StringBuffer dbBuffer   = new StringBuffer();
                transactionId       = paymentid;


                if (merchantDetailsVO != null)
                {
                    clKey        = merchantDetailsVO.getKey();
                    autoredirect = merchantDetailsVO.getAutoRedirect();
                    logoName     = merchantDetailsVO.getLogoName();
                    partnerName  = merchantDetailsVO.getPartnerName();
                    isService    = merchantDetailsVO.getIsService();
                }
                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toid);
                con = Database.getConnection();

                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                transactionLogger.error("dbStatus---> "+trackingId +" "+dbStatus);
                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                {

                    PowerCash21PaymentGateway powerCash21PaymentGateway=new PowerCash21PaymentGateway(accountId);
                    commResponseVO  = (CommResponseVO) powerCash21PaymentGateway.processQuery(trackingId,commRequestVO);

                    String inStatus      = commResponseVO.getStatus();
                    String inRemark      = commResponseVO.getRemark();
                    String inCode        = commResponseVO.getAuthCode();
                    responseAmount       = commResponseVO.getAmount();
                    TXN_ID               = commResponseVO.getTransactionId();
                    String resCurrency   = commResponseVO.getCurrency();
                    String error_code    = commResponseVO.getAuthCode();
                    String creation_date = commResponseVO.getResponseTime();
                    String error_message = commResponseVO.getDescription();


                    transactionLogger.error("inquiry inStatus---> "+trackingId +" "+inStatus);
                    transactionLogger.error("inquiry amount---> "+trackingId +" "+responseAmount);
                    transactionLogger.error("inquiry inCode--->"+trackingId +" "+inCode);
                    transactionLogger.error("inquiry inRemark--->"+trackingId +" "+inRemark);
                    transactionLogger.error("inquiry transactionid--->"+trackingId +" "+TXN_ID);


                    if(functions.isValueNull(responseAmount))
                    {
                        Double compRsAmount = Double.valueOf(responseAmount);
                        Double compDbAmount = Double.valueOf(amount);

                        transactionLogger.error("response amount --->" + compRsAmount);
                        transactionLogger.error(" DB Amount--->" + compDbAmount);

                        if (compDbAmount.equals(compRsAmount))
                        {
                            STATUS = commResponseVO.getStatus();
                            amount = commResponseVO.getAmount();
                            remark = commResponseVO.getRemark();
                        }

                        else if (!compDbAmount.equals(compRsAmount) && "success".equalsIgnoreCase(inStatus))
                        {
                            transactionLogger.error("inside else Amount incorrect--->" + responseAmount);
                            remark = "Failed-IRA";
                            STATUS = "Failed";
                            amount = responseAmount;
                            blacklistVO.setVpaAddress(custId);
                            blacklistVO.setIpAddress(ipAddress);
                            blacklistVO.setEmailAddress(custEmail);
                            blacklistVO.setActionExecutorId(toid);
                            blacklistVO.setActionExecutorName("AcquirerFrontEnd");
                            blacklistVO.setRemark("IncorrectAmount Trackingid : "+trackingId);
                            blacklistVO.setFirstSix(firstSix);
                            blacklistVO.setLastFour(lastFour);
                            blacklistVO.setName(cardHolderName);
                            blacklistManager.commonBlackListing(blacklistVO);
                        }
                    }

                    transactionLogger.error("RESPONSE STATUS--> "+trackingId+" "+STATUS);
                    if("success".equalsIgnoreCase(STATUS))
                    {

                        if ("N".equalsIgnoreCase(isService) || "PA".equalsIgnoreCase(transactionDetailsVO.getTransactionType())) // AUTH
                        {
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            billingDesc     = gatewayAccount.getDisplayName();
                            status          = "success";
                            responseStatus  = "Successful";
                            message         = "Transaction Successful";
                            dbStatus        = "authsuccessful";
                            commResponseVO.setDescription(error_message);
                            commResponseVO.setStatus(status);
                            commResponseVO.setRemark("Transaction Successful");
                            commResponseVO.setDescriptor(billingDesc);
                            commResponseVO.setTransactionId(TXN_ID);
                            commResponseVO.setTmpl_Amount(tmpl_amt);
                            commResponseVO.setTmpl_Currency(tmpl_currency);
                            commResponseVO.setCurrency(resCurrency);
                            commResponseVO.setAuthCode(error_code);
                            commResponseVO.setErrorCode(error_code);
                            commResponseVO.setResponseTime(creation_date);


                            dbBuffer.append("update transaction_common set  paymentid='" + TXN_ID + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "" + "',status='authsuccessful'" + " ,successtimestamp='" + functions.getTimestamp());
                            dbBuffer.append("',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "' ,remark='" + responseStatus + "' where trackingid = " + trackingId);
                            transactionLogger.error("Update Query---" + dbBuffer);
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                        }
                        else//sale
                        {
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            commResponseVO  = new CommResponseVO();
                            billingDesc     = gatewayAccount.getDisplayName();
                            status          = "success";
                            responseStatus  = "Successful";
                            message         = "Transaction Successful";
                            dbStatus        = "capturesuccess";
                            commResponseVO.setDescription(error_message);
                            commResponseVO.setStatus(status);
                            commResponseVO.setRemark("Transaction Successful");
                            commResponseVO.setDescriptor(billingDesc);
                            commResponseVO.setTransactionId(TXN_ID);
                            commResponseVO.setTmpl_Amount(tmpl_amt);
                            commResponseVO.setTmpl_Currency(tmpl_currency);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setCurrency(resCurrency);
                            commResponseVO.setAuthCode(error_code);
                            commResponseVO.setErrorCode(error_code);
                            commResponseVO.setResponseTime(creation_date);


                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + TXN_ID + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "" + "',status='capturesuccess'" + " ,successtimestamp='" + functions.getTimestamp());
                            dbBuffer.append("',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "' ,remark='" + responseStatus + "' where trackingid = " + trackingId);
                            transactionLogger.error("Update Query---" + dbBuffer);
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);

                        }
                    }
                    else if("Failed".equalsIgnoreCase(STATUS))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        status          = "Failed";
                        message         = remark;
                        responseStatus  = remark;

                        dbStatus = "authfailed";
                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + TXN_ID + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "'" + " ,failuretimestamp='" + functions.getTimestamp());

                        dbBuffer.append("',authorization_code='"+amount+"',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "' ,remark='" +responseStatus+ "' where trackingid = " + trackingId);
                        transactionLogger.error("Update Query---" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, ipAddress);
                    }
                    else {
                        //pending
                        dbStatus    = "authstarted";
                        status      = "Pending";
                        message     = "Pending";
                        transactionLogger.error("inside powercash21 pending condition --->"+STATUS+"--"+message);
                    }
                }
                else  if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                {
                    billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    status              = "success";
                    responseStatus      = "Successful";
                    if (functions.isValueNull(transactionDetailsVO.getRemark()))
                        message = transactionDetailsVO.getRemark();
                    else
                        message = "Transaction Successful";
                    dbStatus    = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                }
                else if(PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus)){
                    billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    status              = "success";
                    responseStatus      = "Successful";
                    if(functions.isValueNull(transactionDetailsVO.getRemark()))
                        message = transactionDetailsVO.getRemark();
                    else
                        message = "Transaction Successful";
                    dbStatus=PZTransactionStatus.AUTH_SUCCESS.toString();
                }
                else if (PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                {
                    status          = "fail";
                    responseStatus  = "failed";
                    if (functions.isValueNull(transactionDetailsVO.getRemark()))
                        message = transactionDetailsVO.getRemark();
                    else if (!functions.isValueNull(message))
                        message = "Transaction Failed";
                    dbStatus = PZTransactionStatus.AUTH_FAILED.toString();
                }
                else
                {
                    status              = "pending";
                    responseStatus      = "pending";
                    message     = "Transaction pending";
                    dbStatus    = PZTransactionStatus.AUTH_STARTED.toString();

                }

                statusSyncDAO.updateAllTransactionFlowFlag(trackingId,dbStatus);
                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                AsynchronousSmsService smsService = new AsynchronousSmsService();
                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setOrderDesc(orderDesc);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setNotificationUrl(notificationUrl);
                genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                genericTransDetailsVO.setRedirectMethod(RedirectMethod);

                addressDetailsVO.setEmail(custEmail);
                addressDetailsVO.setTmpl_amount(tmpl_amt);
                addressDetailsVO.setTmpl_currency(tmpl_currency);

                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setCustomerId(custId);
                commonValidatorVO.setTerminalId(terminalId);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setPaymentType(paymodeid);
                commonValidatorVO.setCardType(cardtypeid);
                commonValidatorVO.setTrackingid(trackingId);
                commonValidatorVO.setStatus(message);

                commonValidatorVO.setReason(message);

                if (functions.isValueNull(custEmail)){
                    addressDetailsVO.setEmail(custEmail);}
                if (functions.isValueNull(firstName)){
                    addressDetailsVO.setFirstname(firstName);}

                if (functions.isValueNull(lastName)){
                    addressDetailsVO.setLastname(lastName);
                }
                addressDetailsVO.setTmpl_amount(tmpl_amt);
                addressDetailsVO.setTmpl_currency(tmpl_currency);
                cardDetailsVO.setCardNum(ccnum);
                cardDetailsVO.setExpMonth(expMonth);
                cardDetailsVO.setExpYear(expYear);
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setEci(eci);
                transactionUtility.setToken(commonValidatorVO, dbStatus);
                if (functions.isValueNull(notificationUrl))
                {
                    transactionLogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                    transactionDetailsVO1.setBillingDesc(billingDesc);
                    transactionDetailsVO1.setTransactionMode("3D");
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, dbStatus, message, "");
                }
                transactionLogger.debug("autoredirect in ---" +trackingId +" "+ autoredirect);
                if ("Y".equalsIgnoreCase(autoredirect))
                {
                    transactionUtility.doAutoRedirect(commonValidatorVO, res, responseStatus, billingDesc);
                }
                else
                {
                    session.setAttribute("ctoken", ctoken);

                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", responseStatus);
                    req.setAttribute("displayName",billingDesc);
                    req.setAttribute("remark", message);

                    confirmationPage = "";
                    version          = (String)session.getAttribute("version");
                    transactionLogger.error("version===>" +trackingId+" "+ version);
                    if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                    {
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    }
                    else
                    {
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    }
                    session.invalidate();
                    RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(req, res);
                }
            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException---->"+e);
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError---->"+se);
        }
        catch (PZTechnicalViolationException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            transactionLogger.error("InterruptedException---->" + e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
