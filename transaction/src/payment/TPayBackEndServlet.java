package payment;

import com.directi.pg.*;
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
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.response.PZResponseStatus;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.log4j.LogManager;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;


/**
 * Created by Admin on 10/16/2020.
 */
public class TPayBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionlogger = new TransactionLogger(TPayBackEndServlet.class.getName());
    String ctoken                                       = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
    org.apache.log4j.Logger facileroLogger              = LogManager.getLogger("facilerolog");
    public void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {   doService(req, res);

    }

    public void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        doService(req,res);

    }


    public void doService(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        transactionlogger.error("Entering TigerPayBackEndServlet ......");


        HttpSession session         = req.getSession();
        Functions functions         = new Functions();
        ActionEntry entry           = new ActionEntry();
        AuditTrailVO auditTrailVO   = new AuditTrailVO();
        TransactionManager transactionManager       = new TransactionManager();
        CommonValidatorVO commonValidatorVO         = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO         = new MerchantDetailsVO();
        MerchantDAO merchantDAO         = new MerchantDAO();
        CommResponseVO commResponseVO   = new CommResponseVO();
        GenericAddressDetailsVO addressDetailsVO    = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO          = new GenericCardDetailsVO();
        StatusSyncDAO statusSyncDAO     = new StatusSyncDAO();
        PaymentManager paymentManager   = new PaymentManager();
        CommRequestVO requestVO         = new CommRequestVO();
        TransactionUtility transactionUtility       = new TransactionUtility();
        CommAddressDetailsVO commAddressDetailsVO   = new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
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
        String trackingId   = "";
        String status = "";
        String remark = "";

        String bankTransactionStatus = "";
        String resultCode = "";
        String email = "";

        String tmpl_amt         = "";
        String tmpl_currency    = "";
        String firstName    = "";
        String lastName     = "";
        String paymodeid    = "";
        String cardtypeid   = "";
        String custId       = "";
        String transactionStatus    = "";
        String confirmStatus        = "";
        String responseStatus   = "";
        String transactionId    = "";
        String message          = "";
        String billingDesc      = "";
        String dbStatus         = "";
        String eci              = "";
        String paymentid        = "";
        String errorCode        = "";
        String name             = "";
        String notificationUrl  = "";
        String ccnum        = "";
        String expMonth     = "";
        String expYear      = "";
        String requestIp    = "";
        String merchantKey  = "";
        String paymentId    = "";
        String autoRedirect     = "";
        String updatedStatus    = "";
        String responseAmount   = "";
        String RESPONSE_CODE    = "";
        PrintWriter pWriter = res.getWriter();
        String responseCode = "0";
        String returnResStatus = "OK";


        StringBuilder responseMsg   = new StringBuilder();
        BufferedReader br           = req.getReader();
        Enumeration enumeration     = req.getParameterNames();
        String str      = "";
        String cardtype = "";
        String RequestDetailsStr = "";
        String resPaymentId = "";
        String branch_name  = "";
        String bank_name    = "";
        String branch_code  = "";
        String account_type  = "";
        String account_number  = "";
        String account_name  = "";
        String reference_number  = "";
        String amount_requested  = "";

        String date_deposited  = "";
        String resStatus  = "";

        String confirmation_number  = "";
        String system_transaction_id  = "";
        String amount_deposited     = "";
        String type                 = "";
        StringBuilder responseBackend   = new StringBuilder();

        while (enumeration.hasMoreElements())
        {
            String key      = (String) enumeration.nextElement();
            String value    = req.getParameter(key);
            transactionlogger.error("---Key---" + key + "---Value---" + value);
            responseBackend.append(key+"="+value+"&");
        }

        BufferedReader bufferedReader = req.getReader();
        String string;
        while ((string = bufferedReader.readLine()) != null)
        {
            responseMsg.append(string);
        }

        transactionlogger.error("-----TigerPayBackEndServlet   response-----" + responseMsg);
        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();

        try
        {

           if(req.getParameter("transaction_id") !=null){
               trackingId = req.getParameter("transaction_id");
           }
           transactionlogger.error("-----TigerPayBackEndServlet ----- " + trackingId);

           if(req.getParameter("system_transaction_id") !=null){
               system_transaction_id = req.getParameter("system_transaction_id");
           }
           if(req.getParameter("reference_number") !=null){
               confirmation_number = req.getParameter("reference_number");
           }
            if(req.getParameter("date_deposited") !=null){
                date_deposited = req.getParameter("date_deposited");
           }
            if(req.getParameter("type") !=null){
                type = req.getParameter("type");
           }

           if(req.getParameter("amount_deposited") !=null){
               amount_deposited = req.getParameter("amount_deposited");
               amount_deposited = String.format("%.2f", Double.parseDouble(amount_deposited));
               transactionlogger.error("amount_deposited >>>  " + trackingId +" "+amount_deposited);

           }
            if(req.getParameter("amount_requested") !=null){
                amount_requested = req.getParameter("amount_requested");
                amount_requested = String.format("%.2f", Double.parseDouble(amount_requested));
                transactionlogger.error("amount_requested >>>  " + trackingId +" "+amount_requested);

            }
            if(req.getParameter("amount") !=null){
                amount_deposited = req.getParameter("amount");
                amount_deposited = String.format("%.2f", Double.parseDouble(amount_deposited));
                transactionlogger.error("amount >>>  " + trackingId +" "+amount_deposited);

            }
           if(req.getParameter("status") !=null){
               resStatus = req.getParameter("status");
           }


            if(functions.isValueNull(trackingId)){

                TransactionDetailsVO transactionDetailsVO   = transactionManager.getTransDetailFromCommon(trackingId);

                if (transactionDetailsVO != null)
                {
                    trackingId  = transactionDetailsVO.getTrackingid();
                    toid        = transactionDetailsVO.getToid();
                    description = transactionDetailsVO.getDescription();
                    redirectUrl = transactionDetailsVO.getRedirectURL();
                    accountId   = transactionDetailsVO.getAccountId();
                    orderDesc   = transactionDetailsVO.getOrderDescription();
                    currency    = transactionDetailsVO.getCurrency();
                    amount      = transactionDetailsVO.getAmount();
                    tmpl_amt    = transactionDetailsVO.getTemplateamount();
                    if("Facilero".equalsIgnoreCase(transactionDetailsVO.getTotype())){
                        facileroLogger.error("TigerGateWay Response Backend ----> "+trackingId+" "+responseBackend.toString());
                    }

                    requestIp = Functions.getIpAddress(req);
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


                    paymodeid       = transactionDetailsVO.getPaymodeId();
                    cardtypeid      = transactionDetailsVO.getCardTypeId();
                    custId          = transactionDetailsVO.getCustomerId();
                    transactionId   = transactionDetailsVO.getPaymentId();
                    transactionlogger.error("trackingId-> "+trackingId+" transactionId ----->" + transactionDetailsVO.getPaymentId());
                    dbStatus        = transactionDetailsVO.getStatus();
                    transactionlogger.error("dbStatus--> "+trackingId +" " + dbStatus);

                    merchantDetailsVO   = merchantDAO.getMemberDetails(toid);
                    clkey               = merchantDetailsVO.getKey();
                    checksumAlgo        = merchantDetailsVO.getChecksumAlgo();
                    autoredirect        = merchantDetailsVO.getAutoRedirect();
                    isPowerBy           = merchantDetailsVO.getIsPoweredBy();
                    logoName            = merchantDetailsVO.getLogoName();
                    isService           = merchantDetailsVO.getIsService();
                    partnerName         = merchantDetailsVO.getPartnerName();
                    email               = transactionDetailsVO.getEmailaddr();

                    auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                    auditTrailVO.setActionExecutorId(toid);
                    BlacklistManager blacklistManager   = new BlacklistManager();
                    BlacklistVO blacklistVO             = new BlacklistVO();

                    transactionlogger.error("accountid "+trackingId+" " + accountId);
                    transactionlogger.error("type "+trackingId+" " + type);
                    displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    if(type.equalsIgnoreCase("deposit")){
                        amount = amount_deposited;
                        if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                        {
                            transactionlogger.error("inside AUTH_STARTED---");
                            StringBuffer dbBuffer = new StringBuffer();

                            /*if (commResponseVO != null)
                            {*/
                                transactionlogger.error("status from response -----" + resStatus);
                                responseAmount      = amount_deposited;
                                transactionStatus   = resStatus;
                                message             = resStatus;
                                transactionlogger.error("response Amount -----" + responseAmount);
                                transactionlogger.error("DB Amount -----" + responseAmount);
                                //amount miss match code remove
                                /*if(functions.isValueNull(responseAmount))
                                {
                                    Double compRsAmount = Double.valueOf(responseAmount);
                                    Double compDbAmount = Double.valueOf(amount);
                                    transactionlogger.error("Inside if response amount --->" + compRsAmount);
                                    transactionlogger.error(" DB Amount--->" + compDbAmount);
                                    *//*if (compDbAmount.equals(compRsAmount))
                                    {
                                        transactionStatus   = resStatus;
                                        message             = resStatus;
                                        amount              = responseAmount;
                                    }
                                    else
                                    {
                                        transactionStatus   = "failed";
                                        message             = "Failed-IRA";
                                        RESPONSE_CODE       = "11111";
                                        amount              = responseAmount;
                                        transactionlogger.error("inside else RESPONSE_CODE--->" + RESPONSE_CODE);
                                        transactionlogger.error("inside else Amount incorrect--->" + responseAmount);

                                        blacklistVO.setVpaAddress(custId);
                                        blacklistVO.setIpAddress(requestIp);
                                        blacklistVO.setEmailAddress(email);
                                        blacklistVO.setActionExecutorId(toid);
                                        blacklistVO.setActionExecutorName("TPayBackEndServlet");
                                        blacklistVO.setRemark("IncorrectAmount");

                                        blacklistManager.commonBlackListing(blacklistVO);
                                    }*//*
                                }*/

                                commResponseVO.setTmpl_Amount(tmpl_amt);
                                commResponseVO.setTmpl_Currency(tmpl_currency);
                                commResponseVO.setIpaddress(requestIp);
                                commResponseVO.setDescription(message);
                                commResponseVO.setRemark(message);
                                commResponseVO.setAmount(amount);
                                commResponseVO.setCurrency(currency);
                                commResponseVO.setErrorCode(confirmation_number);
                                commResponseVO.setResponseTime(date_deposited);

                                transactionlogger.error("inside transactionStatus "+transactionStatus);
                                if ("completed".equalsIgnoreCase(transactionStatus))
                                {
                                    notificationUrl = transactionDetailsVO.getNotificationUrl();
                                    billingDesc     = displayName;
                                    status          = "success";
                                    commResponseVO.setDescriptor(billingDesc);
                                    commResponseVO.setStatus(status);
                                    confirmStatus   = "Y";
                                    responseStatus  = "Successful";
                                    dbStatus        = "capturesuccess";
                                    updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                    //dbBuffer.append("update transaction_common set captureamount='" + amount + "',status='capturesuccess',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "',remark='" + message + "' where trackingid = " + trackingId);
                                    dbBuffer.append("update transaction_common set captureamount='" + responseAmount + "',status='capturesuccess',paymentid='" + system_transaction_id + "',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "',remark='" + message + "' where trackingid = " + trackingId);
                                    con = Database.getConnection();
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);

                                }
                                else if ("pending".equalsIgnoreCase(transactionStatus))
                                {
                                    status          = "pending";
                                    dbStatus        = "pending";
                                    responseStatus  = "pending";
                                    billingDesc     = "";
                                    displayName     = "";

                                    dbBuffer.append("update transaction_common set customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "',remark='" + message + "' where trackingid = " + trackingId);
                                    con         = Database.getConnection();
                                    Database.executeUpdate(dbBuffer.toString(), con);

                                }
                                else if("failed".equalsIgnoreCase(transactionStatus))
                                {
                                    notificationUrl = transactionDetailsVO.getNotificationUrl();
                                    confirmStatus   = "N";
                                    status          = "failed";
                                    dbStatus        = "authfailed";
                                    updatedStatus   = PZTransactionStatus.AUTH_FAILED.toString();
                                    billingDesc     = "";
                                    displayName     = "";
                                    //dbBuffer.append("update transaction_common set paymentid='" + commResponseVO.getResponseHashInfo() + "',status='authfailed',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "', remark = '" + message + "'");
                                    dbBuffer.append("update transaction_common set status='authfailed',paymentid='"+system_transaction_id+"',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "', remark = '" + message + "'");
                                  /*  if ("11111".equalsIgnoreCase(RESPONSE_CODE))
                                    {
                                        dbBuffer.append(",captureamount='" + amount + "'");
                                    }*/
                                    dbBuffer.append(" where trackingid = " + trackingId);
                                    con = Database.getConnection();
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, requestIp);
                                    responseStatus = "fail";

                                }
                                else{
                                    status      = "pending";
                                    dbStatus    = "pending";
                                    message     = "Transaction pending";

                                }
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                            /*}*/
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
                                updatedStatus   = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                transactionlogger.error("dbstatus AFTER capture------------------>" + dbStatus);

                            }
                            else if(PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                            {
                                status          = "failed";
                                message         = "Transaction Declined";
                                responseStatus  = "Failed";
                                dbStatus        = "authfailed";
                                updatedStatus   = PZTransactionStatus.AUTH_FAILED.toString();
                            }  // set pending
                            else{
                                status          = "pending";
                                message         = "pending";
                                responseStatus  = "pending";
                                dbStatus        = "authstarted";
                            }
                        }


                        /*asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, responseStatus, billingDesc);

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
                        if (functions.isValueNull(ccnum)){
                            cardDetailsVO.setCardNum(ccnum);
                        }
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
                            transactionlogger.error("remark message----------------------" + message);
                        }
                }else if(type.equalsIgnoreCase("withdrawal")){
                   if (PZTransactionStatus.PAYOUT_STARTED.toString().equalsIgnoreCase(dbStatus))
                   {
                       transactionStatus = resStatus;

                       if ("completed".equalsIgnoreCase(transactionStatus))
                       {

                           responseStatus   = "payoutsuccessful";
                           remark           = transactionStatus;
                           updatedStatus    = PZTransactionStatus.PAYOUT_SUCCESS.toString();
                           StringBuffer sb  = new StringBuffer();

                           notificationUrl = transactionDetailsVO.getNotificationUrl();

                           transactionDetailsVO.setBillingDesc(displayName);
                           commResponseVO.setDescriptor(displayName);
                           commResponseVO.setStatus("success");
                           commResponseVO.setRemark(remark);
                           commResponseVO.setDescription("payout Successful");
                           commResponseVO.setCurrency(currency);
                           commResponseVO.setTmpl_Amount(tmpl_amt);
                           commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());
                           //commResponseVO.setTransactionId(pspid);
                           //commResponseVO.setErrorCode(authCode);

                           sb.append("update transaction_common set ");
                           sb.append(" payoutamount='" + amount_deposited + "'");
                           sb.append(", status='payoutsuccessful'");
                           //sb.append(" ,remark='" + remark + "',paymentid='" + pspid + "',firstname='" + firstname + "',lastname='" + lastname + "',Name='" + CardHName + "',ccnum='" + CardNum + "',payouttimestamp='" + functions.getTimestamp() + "' where trackingid =" + trackingId + "");
                           sb.append(" ,remark='" + remark + "',payouttimestamp='" + functions.getTimestamp() + "' where trackingid =" + trackingId + "");
                           transactionlogger.error("payoutquery "+ trackingId + " "+sb.toString());
                           con     = Database.getConnection();
                           int result  = Database.executeUpdate(sb.toString(), con);
                           transactionlogger.error("payoutquery "+ trackingId + " "+result);

                           entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                           statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.PAYOUT_SUCCESS.toString());

                           if (result != 1)
                           {
                               Database.rollback(con);
                              // asynchronousMailService.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                           }

                       }else{

                           responseStatus = PZResponseStatus.PENDING.toString();
                           remark         = transactionStatus;
                       }

                       //asynchronousMailService.sendEmail(MailEventEnum.PAYOUT_TRANSACTION, String.valueOf(trackingId), responseStatus, remark, null);

                       if (functions.isValueNull(notificationUrl))
                       {
                           transactionlogger.error("inside sending notification---" + notificationUrl);
                           AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                           transactionDetailsVO.setBillingDesc(displayName);
                           transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
                           asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, remark, "");
                       }
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
            }
        }

        catch(NullPointerException e)
        {
            transactionlogger.error("NullPointerException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("TPayBackEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }

        catch(Exception e)
        {
            transactionlogger.error("PZDBViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("TPayBackEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }


}
