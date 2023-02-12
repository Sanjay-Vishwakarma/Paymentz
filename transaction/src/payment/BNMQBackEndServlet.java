package payment;

import com.directi.pg.*;
import com.directi.pg.AsyncNotificationService;
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
import com.payment.PZTransactionStatus;
import com.payment.bnmquick.BnmQuickPaymentGateway;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
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
 * Created by Admin on 7/9/2022.
 */
public class BNMQBackEndServlet extends HttpServlet
{

    private static TransactionLogger transactionlogger = new TransactionLogger(BNMQBackEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req,res);
        }
        catch (PZDBViolationException e)
        {
            transactionlogger.error("Entering BNMQBackEndServlet ......",e);
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
            transactionlogger.error("Entering BNMQBackEndServlet ......",e);
        }
    }

    public void doService(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException, PZDBViolationException
    {
        transactionlogger.error("....Entering BNMQBackEndServlet ......");


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

        Enumeration enumeration = req.getParameterNames();
        StringBuffer responseMsg=new StringBuffer();

       /* while (enumeration.hasMoreElements())
        {
            String key      = (String) enumeration.nextElement();
            String value    = req.getParameter(key);
            transactionlogger.error("---Key---" + key + "---Value---" + value);
        }*/

        BufferedReader br = req.getReader();
        String str1;
        while ((str1=br.readLine())!=null)
        {
            responseMsg.append(str1);
        }
        transactionlogger.error("-----BNMQBackEndServlet JSON-----" + responseMsg);

        String paymentStatus="";
        String txnId="";
        String resAmount="";
        try
        {

            JSONObject  dataObject = new JSONObject(responseMsg.toString());

            if(dataObject != null )
            {

                    if (dataObject.has("amount"))
                    {
                        responseAmount = dataObject.getString("amount");
                        responseAmount  = String.format("%.2f", Double.parseDouble(responseAmount));
                        transactionlogger.error("Ds_Amount response amount================>"+responseAmount);
                    }
                    if (dataObject.has("payment_status"))
                    {
                        paymentStatus = dataObject.getString("payment_status");
                    }
                    if (dataObject.has("txn_id"))
                    {
                        txnId = dataObject.getString("txn_id");
                    }
                    if (dataObject.has("order_id"))
                    {
                        trackingId = dataObject.getString("order_id");
                    }
            }
            TransactionDetailsVO transactionDetailsVO   = transactionManager.getTransDetailFromCommon(trackingId);
            CommRequestVO requestVO                     = new CommRequestVO();
            BlacklistManager blacklistManager           = new BlacklistManager();
            BlacklistVO blacklistVO                     = new BlacklistVO();


            transactionlogger.error("..... BNMQBackEndServlet ......"+trackingId+" mihpayid--> ");

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
                transactionlogger.error("requestIp --- >" + requestIp);
                transactionlogger.error("notificationUrl ---" + notificationUrl);
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
                    transactionlogger.error("inside else of tmpl_currency --->");
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

                auditTrailVO.setActionExecutorName("BNMQBackEnd");
                auditTrailVO.setActionExecutorId(toid);

                displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();


                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                {
                    transactionlogger.error("inside AUTH_STARTED---");
                    StringBuffer dbBuffer = new StringBuffer();

                    commTransactionDetailsVO.setAmount(amount);
                    commTransactionDetailsVO.setCurrency(currency);
                    commTransactionDetailsVO.setPreviousTransactionId(transactionId);

                    requestVO.setTransDetailsVO(commTransactionDetailsVO);

                    con             = Database.getConnection();
                    //commResponseVO  = (CommResponseVO) bnmQuickPaymentGateway.processQuery(trackingId, requestVO);


                    transactionlogger.error("inquiry trackingid--->"+trackingId);
                    transactionlogger.error("inquiry inStatus--->"+paymentStatus);
                    transactionlogger.error("inquiry amount--->"+resAmount);
                    transactionlogger.error("inquiry VendorOrderId--->"+txnId);

                    if(functions.isValueNull(responseAmount))
                    {
                        Double compRsAmount = Double.valueOf(responseAmount);
                        Double compDbAmount = Double.valueOf(amount);

                        transactionlogger.error("response amount --->" + compRsAmount);
                        transactionlogger.error(" DB Amount--->" + compDbAmount);

                        if (compDbAmount.equals(compRsAmount))
                        {
                            STATUS = paymentStatus;
                            amount = responseAmount;
                            remark = paymentStatus;
                        }

                        else if (!compDbAmount.equals(compRsAmount) && "success".equalsIgnoreCase(paymentStatus))
                        {
                            transactionlogger.error("inside else Amount incorrect--->" + resAmount);
                            remark = "Failed-IRA";
                            STATUS = "authfailed";
                            amount = responseAmount;

                            blacklistVO.setVpaAddress(custId);
                            blacklistVO.setIpAddress(requestIp);
                            blacklistVO.setEmailAddress(email);
                            blacklistVO.setActionExecutorId(toid);
                            blacklistVO.setActionExecutorName("AcquirerBackEnd");
                            blacklistVO.setRemark("IncorrectAmount");
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

                    if ("Success".equalsIgnoreCase(paymentStatus))
                    {
                        notificationUrl    = transactionDetailsVO.getNotificationUrl();
                        status              = "success";
                        responseStatus      = remark;
                        billingDesc         = displayName;
                        message             = remark;


                        commResponseVO.setStatus("success");
                        commResponseVO.setTransactionId(txnId);
                        commResponseVO.setResponseHashInfo(txnId);
                        commResponseVO.setDescription(paymentStatus);
                        commResponseVO.setRemark(paymentStatus);
                        commResponseVO.setDescriptor(billingDesc);


                        dbStatus = "capturesuccess";

                        dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + txnId + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency  +"',status='capturesuccess'" + " ,successtimestamp='" + functions.getTimestamp());
                        dbBuffer.append("',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + paymentStatus + "' where trackingid = " + trackingId);
                        transactionlogger.error("Update Query---" + dbBuffer);

                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);//+ ",rrn='" + rrn
                    }
                    else if("Failed".equalsIgnoreCase(paymentStatus))
                    {
                        transactionlogger.error("inside Imoney failed --->");

                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        status          = "Failed";

                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionId(txnId);
                        commResponseVO.setResponseHashInfo(txnId);
                        commResponseVO.setDescription(paymentStatus);
                        commResponseVO.setRemark(paymentStatus);
                        commResponseVO.setDescriptor(billingDesc);
                        dbStatus = "authfailed";
                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + txnId + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "'" + " ,failuretimestamp='" + functions.getTimestamp());

                        dbBuffer.append("',authorization_code='"+amount+"',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" +paymentStatus+ "' where trackingid = " + trackingId);
                        transactionlogger.error("Update Query---" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, requestIp);
                    }
                    else {
                        //pending
                        dbStatus    = "authstarted";
                        status      = "Pending";
                        message     = "Pending";

                        transactionlogger.error("inside Bnmq pending condition --->"+STATUS+"--"+message);
                    }
                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId,dbStatus);
                }
                else
                {
                    if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status      = "success";

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
                    }
                    else
                    {
                        status      = "pending";
                        message     = "Transaction pending";
                        dbStatus    = PZTransactionStatus.AUTH_STARTED.toString();
                    }
                }


              /*  AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
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
                    payment.AsyncNotificationService asyncNotificationService = payment.AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, status, message, "");
                    transactionlogger.error("remark message----------------------"+message);
                }

                res.setStatus(200);
                return;
            }
        }
        catch (JSONException je){
            transactionlogger.error("JSONException:::::", je);
        }
        catch (Exception e)
        {
            transactionlogger.error("PZDBViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("BNMQBackEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        finally
        {
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
    }
}