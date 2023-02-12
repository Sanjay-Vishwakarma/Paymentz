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
import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Date;
import java.util.Enumeration;

/**
 * Created by Admin on 1/11/2022.
 */
public class PC21BackendServlet extends HttpServlet
{
    TransactionLogger transactionLogger= new TransactionLogger(PC21BackendServlet.class.getName());

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
        transactionLogger.error("Inside PC21BackendServlet");
        Date date4 = new Date();
        transactionLogger.debug("DirectTransactionRESTIMPL processDirectTransaction start #########" + date4.getTime());
        HttpSession session                     = req.getSession();
        Functions functions                     = new Functions();
        TransactionManager transactionManager   = new TransactionManager();
        StatusSyncDAO statusSyncDAO             = new StatusSyncDAO();
        AuditTrailVO auditTrailVO               = new AuditTrailVO();
        CommResponseVO commResponseVO           = new CommResponseVO();
        CommRequestVO commRequestVO             = new CommRequestVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = new CommTransactionDetailsVO();
        CommonValidatorVO commonValidatorVO                 = new CommonValidatorVO();
        GenericAddressDetailsVO addressDetailsVO            = new GenericAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO         = new GenericTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO                  = new GenericCardDetailsVO();
        TransactionUtility transactionUtility               = new TransactionUtility();

        Connection con = null;


        ActionEntry entry = new ActionEntry();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();

        PrintWriter pWriter = res.getWriter();
        String responseCode = "200";
        String returnResStatus = "OK";
        Enumeration enumeration = req.getParameterNames();

        while (enumeration.hasMoreElements())
        {
            String key      = (String) enumeration.nextElement();
            String value    = req.getParameter(key);
            transactionLogger.error("---Name===>" + key + " ,Value===>" + value);
        }
        // transactionLogger.error("Key ---" + key + "---value ---" + value);

        String trackingId               = req.getParameter("trackingId");
        String statusFromResponse       = req.getParameter("ccn_four");
        String orderidFromResponse      = req.getParameter("orderid");
        String errorcodeFromResponse    = req.getParameter("errorcode");
        String binFromResponse          = req.getParameter("bin");
        String errormessage             = req.getParameter("errormessage");
        String cardholderFromResponse               = req.getParameter("cardholder");
        String transactionidFromResponse            = req.getParameter("transactionid");
        String ProtocolVersion3dFromResponse        = req.getParameter("3dProtocolVersion");
        String timestampFromResponse                = req.getParameter("timestamp");
        String typeFromResponse                     = req.getParameter("type");
        String authentication_status3dFromResponse  = req.getParameter("3dauthentication_status");

        String toid             = "";
        String description      = "";
        String redirectUrl      = "";
        String accountId        = "";
        String orderDesc        = "";
        String currency         = "";
        String amount           = "";
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
        try
        {
            transactionLogger.error("trackingId ---------->" + trackingId);
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            BlacklistManager blacklistManager           = new BlacklistManager();
            BlacklistVO blacklistVO                     = new BlacklistVO();
            if (transactionDetailsVO != null)
            {
                accountId                       = transactionDetailsVO.getAccountId();
                GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);
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
                custId          = transactionDetailsVO.getCustomerId();
                paymentid       = transactionDetailsVO.getPaymentId();
                dbStatus        = transactionDetailsVO.getStatus();
                terminalId      = transactionDetailsVO.getTerminalId();
                tmpl_amt        = transactionDetailsVO.getTemplateamount();
                tmpl_currency   = transactionDetailsVO.getTemplatecurrency();
                ipAddress       = transactionDetailsVO.getIpAddress();
                version         = transactionDetailsVO.getVersion();
                ccnum           = transactionDetailsVO.getCcnum();
                if (functions.isValueNull(ccnum))
                {
                    ccnum    = PzEncryptor.decryptPAN(ccnum);
                    firstSix = functions.getFirstSix(ccnum);
                    lastFour = functions.getLastFour(ccnum);
                }
                if(functions.isValueNull(transactionDetailsVO.getName())){
                    cardHolderName = transactionDetailsVO.getName();
                }
                if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                {
                    ccnum = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                }
                if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                {
                    expDate         = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
                    String temp[]   = expDate.split("/");

                    if (functions.isValueNull(temp[0]))
                    {
                        expMonth = temp[0];
                    }
                    if (functions.isValueNull(temp[1]))
                    {
                        expYear = temp[1];
                    }
                }
                eci                     = transactionDetailsVO.getEci();
                MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
                merchantDetailsVO       = merchantConfigManager.getMerchantDetailFromToId(toid);
                StringBuffer dbBuffer   = new StringBuffer();
                transactionId           = paymentid;


                if (merchantDetailsVO != null)
                {
                    clKey               = merchantDetailsVO.getKey();
                    autoredirect        = merchantDetailsVO.getAutoRedirect();
                    logoName            = merchantDetailsVO.getLogoName();
                    partnerName         = merchantDetailsVO.getPartnerName();
                    isService           = merchantDetailsVO.getIsService();
                }
                auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                auditTrailVO.setActionExecutorId(toid);
                con         = Database.getConnection();

                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                transactionLogger.debug("dbStatus -----> " + trackingId + " " + dbStatus);
                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                {
                    transactionLogger.error("errorcodeFromResponse ----> "+trackingId +" "+errorcodeFromResponse);
                    transactionLogger.error("isService ----> "+trackingId +" "+isService);
                    transactionLogger.error("TransactionType ----> "+trackingId +" "+transactionDetailsVO.getTransactionType());

                    if("0".equalsIgnoreCase(errorcodeFromResponse))
                    {

                        if ("N".equalsIgnoreCase(isService) || "PA".equalsIgnoreCase(transactionDetailsVO.getTransactionType())) // AUTH
                        {
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            // commResponseVO = new CommResponseVO();
                            billingDesc     = gatewayAccount.getDisplayName();
                            status          = "success";
                            responseStatus  = "Successful";
                            message         = "Transaction Successful";
                            dbStatus        = "authsuccessful";
                            commResponseVO.setDescription(responseStatus);
                            commResponseVO.setStatus(status);
                            commResponseVO.setRemark("Transaction Successful");
                            commResponseVO.setDescriptor(billingDesc);
                            commResponseVO.setTransactionId(transactionidFromResponse);
                            commResponseVO.setTmpl_Amount(tmpl_amt);
                            commResponseVO.setTmpl_Currency(tmpl_currency);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setAuthCode(errorcodeFromResponse);
                            commResponseVO.setErrorCode(errorcodeFromResponse);
                            //commResponseVO.setResponseTime(creation_date);


                            dbBuffer.append("update transaction_common set paymentid='" + transactionidFromResponse + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "" + "',status='authsuccessful'" + " ,successtimestamp='" + functions.getTimestamp());
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
                            commResponseVO.setDescription(responseStatus);
                            commResponseVO.setStatus(status);
                            commResponseVO.setRemark("Transaction Successful");
                            commResponseVO.setDescriptor(billingDesc);
                            commResponseVO.setTransactionId(transactionidFromResponse);
                            commResponseVO.setTmpl_Amount(tmpl_amt);
                            commResponseVO.setTmpl_Currency(tmpl_currency);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setAuthCode(errorcodeFromResponse);
                            commResponseVO.setErrorCode(errorcodeFromResponse);
                            //commResponseVO.setResponseTime(creation_date);


                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionidFromResponse + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "" + "',status='capturesuccess'" + " ,successtimestamp='" + functions.getTimestamp());
                            dbBuffer.append("',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "' ,remark='" + responseStatus + "' where trackingid = " + trackingId);
                            transactionLogger.error("Update Query---" + dbBuffer);
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);

                        }
                   }
                   /* else if("Failed".equalsIgnoreCase(errorcodeFromResponse))
                    {
                        transactionLogger.error("inside powercash21 failed --->");

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
                    }*/
                    else {
                        //pending
                        dbStatus    = "authstarted";
                        status      = "Pending";
                        message     = "Pending";
                        transactionLogger.error("inside powercash21 pending condition --->"+STATUS+"--"+message);
                    }
                }
                else if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                {
                    billingDesc = gatewayAccount.getDisplayName();
                    status = "success";
                    responseStatus = "Successful";
                    dbStatus = "capturesuccess";

                    dbBuffer.append("update transaction_common set customerIp='"+ipAddress+"',customerIpCountry='"+functions.getIPCountryShort(ipAddress)+"' where trackingid ='" + trackingId+"'");
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                }
                else if (PZTransactionStatus.AUTH_SUCCESS.toString().equalsIgnoreCase(dbStatus))
                {
                    billingDesc     = gatewayAccount.getDisplayName();
                    status          = "success";
                    responseStatus  = "Successful";
                    dbStatus        = "authsuccessful";
                    message         = "Transaction Successful";

                    dbBuffer.append("update transaction_common set customerIp='"+ipAddress+"',customerIpCountry='"+functions.getIPCountryShort(ipAddress)+"' where trackingid ='" + trackingId + "'");
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                }
                else if (PZTransactionStatus.AUTH_CANCELLED.toString().equalsIgnoreCase(dbStatus))
                {
                    status          = "authcancelled";
                    responseStatus  = "AuthCancelled";
                    dbStatus        = "authcancelled";
                    message         = "Transaction AuthCancelled";

                    dbBuffer.append("update transaction_common set status='authcancelled' where trackingid ='" + trackingId + "'");
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                }
                else if (PZTransactionStatus.CANCEL_STARTED.toString().equalsIgnoreCase(dbStatus))
                {
                    status          = "cancelstarted";
                    responseStatus  = "CancelStarted";
                    dbStatus        = "cancelstarted";
                    message         = "Transaction CancelStarted";

                    dbBuffer.append("update transaction_common set status='cancelstarted',customerIp='"+ipAddress+"',customerIpCountry='"+functions.getIPCountryShort(ipAddress)+"' where trackingid ='" + trackingId + "'");
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                }
                else if (PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.FAILED.toString().equalsIgnoreCase(dbStatus) )
                {
                    status          = "authfailed";
                    responseStatus  = "authfailed";
                    dbStatus        = "authfailed";
                    message         = "Transaction authfailed";

                    dbBuffer.append("update transaction_common set status='authfailed',customerIp='"+ipAddress+"',customerIpCountry='"+functions.getIPCountryShort(ipAddress)+"' where trackingid ='" + trackingId + "'");
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                }

                else
                {
                    transactionLogger.error("Inside Else Of AUTH_STARTED");
                    status          = "fail";
                    responseStatus  = "Failed";
                    message         = "Transaction Failed";
                    dbStatus        = "authfailed";

                    dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',remark='" + message + "',customerIp='"+ipAddress+"',customerIpCountry='"+functions.getIPCountryShort(ipAddress)+"' where trackingid = " + trackingId);
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
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
                // genericTransDetailsVO.setRedirectMethod(redirectMethod);
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
                    addressDetailsVO.setLastname(lastName);}
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

            }
            JSONObject jsonResObject = new JSONObject();
            jsonResObject.put("responseCode", responseCode);
            jsonResObject.put("responseStatus", returnResStatus);
            res.setContentType("application/json");
            res.setStatus(200);
            pWriter.println(jsonResObject.toString());
            pWriter.flush();
            return;
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException---->"+e);
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError---->"+se);
        }

        catch (JSONException e)
        {
            transactionLogger.error("JSONException---->" + e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}