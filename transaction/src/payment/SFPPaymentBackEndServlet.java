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
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.response.PZResponseStatus;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONArray;
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
 * Created by Admin on 9/2/2021.
 */
public class SFPPaymentBackEndServlet extends HttpServlet
{

    private static TransactionLogger transactionlogger  = new TransactionLogger(SFPPaymentBackEndServlet.class.getName());
    String ctoken                                       = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req, res);
        }
        catch (PZDBViolationException e)
        {
            transactionlogger.error("Entering SFPPaymentBackEndServlet ......", e);
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req, res);
        }
        catch (PZDBViolationException e)
        {
            transactionlogger.error("Entering SFPPaymentBackEndServlet ......", e);
        }
    }

    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, PZDBViolationException
    {
        transactionlogger.error("Entering SFPPaymentBackEndServlet ......");


        HttpSession session     = req.getSession();
        Functions functions     = new Functions();
        ActionEntry entry                       = new ActionEntry();
        AuditTrailVO auditTrailVO               = new AuditTrailVO();
        TransactionManager transactionManager   = new TransactionManager();
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

        Connection con      = null;
        PreparedStatement p = null;
        ResultSet rs        = null;
        String toid         = "";
        String description = "";
        String redirectUrl = "";
        String accountId = "";
        String orderDesc = "";
        String currency = "";
        String clkey = "";
        String checksumAlgo = "";
        String checksum = "";
        String autoredirect = "";
        String isService = "";
        String displayName = "";
        String isPowerBy = "";
        String logoName = "";
        String partnerName = "";

        String amount = "";
        String trackingId = "";


        String bankTransactionStatus = "";
        String resultCode = "";
        String email = "";

        String tmpl_amt = "";
        String tmpl_currency = "";

        String custId = "";


        String billingDesc = "";
        String dbStatus = "";
        String eci = "";
        String paymentid = "";
        String errorCode = "";
        String name = "";
        String notificationUrl = "";
        String RESPONSE_DATE_TIME = "";
        String TXNTYPE = "";
        String CURRENCY_CODE = "";
        String STATUS = "";
        String CLIENT_ID = "";
        String CUST_EMAIL = "";
        String CUST_NAME = "";
        String cardType = "";
        String restatus = "";
        String responseAmount = "";
        String toId = "";
        String custIp = "";
        String updatedStatus = "";
        String txtStatus="";
        String str;
        String amountRes="";

        String txnStatus="";
        Enumeration enumeration = req.getParameterNames();
        StringBuilder responseMsg = new StringBuilder();

        String PaymentStatus    = "";
        String transactionId    = "";
        String OrderAmount      = "";
        String dateTime         = "";
        String PaymentResponseCode  = "";
        String bankcode             = "";
        String paymentResponseStatus  = "";
        String RESPONSE_MESSAGE="";
        String RESPONSE_CODE    ="";
        String payout_status    ="";
        String payment_status    ="";
        String responseStatus    ="";
        String remark    ="";
        String type    ="";

       /*while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = req.getParameter(key);
            transactionlogger.error("---PayGBackEndServlet Key---" + key + "---Value---" + value);
        }*/

        BufferedReader br = req.getReader();
        String str1;
        while ((str1=br.readLine())!=null)
        {
            responseMsg.append(str1);
        }
        transactionlogger.error("-----SFPPaymentBackEndServlet JSON-----" + responseMsg);

        try
        {
            
            JSONObject responseJOSN = new JSONObject(responseMsg.toString());
            JSONObject dataJSON     = new JSONObject();
            JSONArray dataArrayJSON = null;

            if(responseJOSN.has("data")){
                dataArrayJSON = responseJOSN.getJSONArray("data");
            }

            if(dataArrayJSON != null && dataArrayJSON.length() > 0){
                dataJSON = (JSONObject) dataArrayJSON.get(0);
            }


            if (dataJSON.has("transaction_id") && functions.isValueNull(dataJSON.getString("transaction_id")))
            {
                trackingId = dataJSON.getString("transaction_id");
            }
            if (dataJSON.has("id") && functions.isValueNull(dataJSON.getString("id")))
            {
                transactionId = dataJSON.getString("id");
            }

            if (dataJSON.has("amount") && functions.isValueNull(dataJSON.getString("amount")))
            {
                OrderAmount = dataJSON.getString("amount");
            }

            if (dataJSON.has("status") && functions.isValueNull(dataJSON.getString("status")))
            {
                paymentResponseStatus = dataJSON.getString("status");
            }
            if (dataJSON.has("payment_status") && functions.isValueNull(dataJSON.getString("payment_status")))
            {
                payment_status = dataJSON.getString("payment_status");
            }
            if (dataJSON.has("payout_status") && functions.isValueNull(dataJSON.getString("payout_status")))
            {
                payment_status = dataJSON.getString("payout_status");
            }
            if (dataJSON.has("type") && functions.isValueNull(dataJSON.getString("type")))
            {
                type = dataJSON.getString("type");
            }


            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            if (transactionDetailsVO != null)
            {
                accountId       = transactionDetailsVO.getAccountId();
                cardType        = transactionDetailsVO.getCardtype();
                toId            = transactionDetailsVO.getToid();
                dbStatus        = transactionDetailsVO.getStatus();
                amount          = transactionDetailsVO.getAmount();
                currency        = transactionDetailsVO.getCurrency();
                tmpl_amt        = transactionDetailsVO.getTemplateamount();
                tmpl_currency   = transactionDetailsVO.getTemplatecurrency();
                custId          = transactionDetailsVO.getCustomerId();
                custIp          = transactionDetailsVO.getCustomerIp();
                email           = transactionDetailsVO.getEmailaddr();
                if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                    transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                    transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));

                auditTrailVO.setActionExecutorName("SFPBackEndServlet");
                auditTrailVO.setActionExecutorId(toId);
                merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());


                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                transactionlogger.error("DbStatus------>" + dbStatus);
                transactionlogger.error("PZTransactionStatus.AUTH_STARTED------>" + PZTransactionStatus.AUTH_STARTED.toString());
                BlacklistManager blacklistManager   = new BlacklistManager();
                BlacklistVO blacklistVO             = new BlacklistVO();

                if ("payment".equalsIgnoreCase(type) && (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(dbStatus)))
                {
                    amountRes   = OrderAmount;
                    transactionlogger.error(" formated Response amount--"+trackingId+"-->"+amountRes);
                    transactionlogger.error("inside if ------" );
                    con = Database.getConnection();

                    commResponseVO.setCurrency(currency);
                    commResponseVO.setTmpl_Amount(tmpl_amt);
                    commResponseVO.setTmpl_Currency(transactionDetailsVO.getCurrency());
                    if(functions.isValueNull(amountRes))
                    {
                        Double compRsAmount = Double.valueOf(amountRes);
                        Double compDbAmount = Double.valueOf(amount);

                        transactionlogger.error("payg backend response amount --->"+compRsAmount);
                        transactionlogger.error("payg backend DB Amount--->"+compDbAmount);

                        if(!compDbAmount.equals(compRsAmount)){
                            transactionlogger.error("inside else Amount incorrect--->"+amountRes);
                            paymentResponseStatus         = "Failed";
                            RESPONSE_MESSAGE            = "Failed-IRA";
                            RESPONSE_CODE               = "11111";
                            amount                      = amountRes;
                            blacklistVO.setVpaAddress(custId);
                            blacklistVO.setIpAddress(custIp);
                            blacklistVO.setEmailAddress(email);
                            blacklistVO.setActionExecutorId(toId);
                            blacklistVO.setActionExecutorName("PGBackEndServlet");
                            blacklistVO.setRemark("IncorrectAmount");
                            blacklistManager.commonBlackListing(blacklistVO);
                        }
                    }

                    StringBuffer dbBuffer = new StringBuffer();
                    if ("paid".equalsIgnoreCase(paymentResponseStatus) && "paid".equalsIgnoreCase(payment_status))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        restatus        = "success";
                        billingDesc     = gatewayAccount.getDisplayName();

                        commResponseVO.setStatus("success");
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setResponseHashInfo(transactionId);
                        commResponseVO.setDescription(paymentResponseStatus);
                        commResponseVO.setRemark(paymentResponseStatus);
                        commResponseVO.setDescriptor(billingDesc);
                        commResponseVO.setErrorCode(PaymentResponseCode);

                        dbBuffer.append("update transaction_common set captureamount='" + amountRes + "',currency='" + currency + "' ,paymentid='" + transactionId + "',status='capturesuccess' ,remark='" + paymentResponseStatus + "' ,successtimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                        transactionlogger.error("dbBuffer->" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);

                        entry.actionEntryForCommon(trackingId, amountRes, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                        updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }
                    else if ("canceled".equalsIgnoreCase(paymentResponseStatus) || "denied".equalsIgnoreCase(payment_status))
                    {
                        restatus        = "Failed";

                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        commResponseVO.setStatus("failed");
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setResponseHashInfo(transactionId);
                        commResponseVO.setDescription(paymentResponseStatus);
                        commResponseVO.setRemark(paymentResponseStatus);
                        commResponseVO.setErrorCode(PaymentResponseCode);

                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "' ,remark='" +  paymentResponseStatus +" "+ RESPONSE_MESSAGE+ "'" + " ,failuretimestamp='" + functions.getTimestamp());
                        dbBuffer.append("' where trackingid = " + trackingId);
                        transactionlogger.error("dbBuffer->" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);

                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                        updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }else{
                            restatus    = "pending";
                            commResponseVO.setStatus("pending");
                            commResponseVO.setTransactionStatus("pending");
                    }
                    if (functions.isValueNull(notificationUrl))
                    {
                        transactionlogger.error("Inside sending notification---" + notificationUrl);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        transactionDetailsVO.setBillingDesc(billingDesc);
                        asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, restatus, paymentResponseStatus, "");
                    }
                }else if("payout".equalsIgnoreCase(type) && (PZTransactionStatus.PAYOUT_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.PAYOUT_FAILED.toString().equalsIgnoreCase(dbStatus))){
                    if ("paid".equalsIgnoreCase(paymentResponseStatus) && "success".equalsIgnoreCase(payment_status))
                    {

                        responseStatus   = "payoutsuccessful";
                        remark           = payment_status;
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
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setResponseHashInfo(transactionId);

                        sb.append("update transaction_common set ");
                        sb.append(" payoutamount='" + OrderAmount + "'");
                        sb.append(", status='payoutsuccessful'");
                        sb.append(" ,remark='" + remark + "' ,paymentid='" + transactionId +"',payouttimestamp='" + functions.getTimestamp() + "' where trackingid =" + trackingId + "");
                        transactionlogger.error("payoutquery "+ trackingId + " "+sb.toString());
                        con     = Database.getConnection();
                        int result  = Database.executeUpdate(sb.toString(), con);
                        transactionlogger.error("payoutquery "+ trackingId + " "+result);

                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.PAYOUT_SUCCESS.toString());

                        if (result != 1)
                        {
                            Database.rollback(con);
                        }

                    }else if("canceled".equalsIgnoreCase(paymentResponseStatus) || "failed".equalsIgnoreCase(payment_status)){
                        responseStatus   = "payoutfailed";
                        remark           = payment_status;
                        updatedStatus    = PZTransactionStatus.PAYOUT_FAILED.toString();
                        StringBuffer sb  = new StringBuffer();

                        notificationUrl = transactionDetailsVO.getNotificationUrl();

                        transactionDetailsVO.setBillingDesc(displayName);
                        commResponseVO.setDescriptor(displayName);
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark(remark);
                        commResponseVO.setDescription("Payout failed");
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setTmpl_Amount(tmpl_amt);
                        commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());

                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setResponseHashInfo(transactionId);

                        sb.append("update transaction_common set ");
                       // sb.append(" payoutamount='" + OrderAmount + "'");
                        sb.append(" status='payoutfailed'");
                        sb.append(" ,remark='" + remark +"' ,paymentid='" + transactionId + "',payouttimestamp='" + functions.getTimestamp() + "' where trackingid =" + trackingId + "");
                        transactionlogger.error("payoutquery "+ trackingId + " "+sb.toString());
                        con     = Database.getConnection();
                        int result  = Database.executeUpdate(sb.toString(), con);
                        transactionlogger.error("payoutquery "+ trackingId + " "+result);

                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.STATUS_PAYOUT_FAILED, commResponseVO, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.PAYOUT_FAILED.toString());

                        if (result != 1)
                        {
                            Database.rollback(con);
                        }
                    }else{

                        responseStatus = PZResponseStatus.PENDING.toString();
                        updatedStatus  = "pending";
                        remark         = payout_status;
                    }


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

            PrintWriter pWriter         = res.getWriter();
            String responseCode         = "0";
            String returnResStatus      = "OK";
            JSONObject jsonResObject = new JSONObject();
            jsonResObject.put("responseCode", responseCode);
            jsonResObject.put("responseStatus", returnResStatus);

            res.setStatus(200);
            pWriter.println(jsonResObject.toString());
            pWriter.flush();
            return;
        }
        catch (Exception e)
        {
            transactionlogger.error("PZDBViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("SFPPaymentBackEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}

