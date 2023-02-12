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
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

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
 * Created by Admin on 9/2/2021.
 */
public class LYPaymentBackEndServlet extends HttpServlet
{

    private static TransactionLogger transactionlogger = new TransactionLogger(LYPaymentBackEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req, res);
        }
        catch (PZDBViolationException e)
        {
            transactionlogger.error("Entering LYPaymentBackEndServlet ......", e);
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
            transactionlogger.error("Entering LYPaymentBackEndServlet ......", e);
        }
    }

    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, PZDBViolationException
    {
        transactionlogger.error("Entering LYPaymentBackEndServlet ......");


        HttpSession session                         = req.getSession();
        Functions functions                         = new Functions();
        ActionEntry entry                           = new ActionEntry();
        AuditTrailVO auditTrailVO                   = new AuditTrailVO();
        TransactionManager transactionManager       = new TransactionManager();
        CommonValidatorVO commonValidatorVO         = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO         = new MerchantDetailsVO();
        MerchantDAO merchantDAO                     = new MerchantDAO();
        CommResponseVO commResponseVO               = new CommResponseVO();
        GenericAddressDetailsVO addressDetailsVO    = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO      = new GenericCardDetailsVO();
        StatusSyncDAO statusSyncDAO             = new StatusSyncDAO();
        PaymentManager paymentManager           = new PaymentManager();
        TransactionUtility transactionUtility   = new TransactionUtility();

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
        String isService = "";
        String displayName = "";
        String isPowerBy = "";
        String logoName = "";
        String partnerName = "";

        String amount = "";


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
        String PaymentResponseText  = "";
        String RESPONSE_MESSAGE="";
        String RESPONSE_CODE    ="";

        String trackingId = req.getParameter("trackingId");
        BufferedReader br = req.getReader();
        String str1;
        while ((str1=br.readLine())!=null)
        {
            responseMsg.append(str1);
        }
        transactionlogger.error("-----LYPaymentBackEndServlet JSON-----" +trackingId +" "+ responseMsg);

        try
        {
            
            JSONObject Response = new JSONObject(responseMsg.toString());

            if (Response.has("uuid") && functions.isValueNull(Response.getString("uuid")))
            {
                transactionId = Response.getString("uuid");
            }

            if (Response.has("status") && functions.isValueNull(Response.getString("status")))
            {
                PaymentResponseText = Response.getString("status");
            }

            if (Response.has("amount") && functions.isValueNull(Response.getString("amount")))
            {
                OrderAmount = Response.getString("amount");
                OrderAmount = String.format("%.2f", Double.parseDouble(OrderAmount)/100);
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

                auditTrailVO.setActionExecutorName("LYBackEndServlet");
                auditTrailVO.setActionExecutorId(toId);
                merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());


                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                transactionlogger.error("DbStatus------>" + dbStatus);
                transactionlogger.error("PZTransactionStatus.AUTH_STARTED------>" + PZTransactionStatus.AUTH_STARTED.toString());
                BlacklistManager blacklistManager   = new BlacklistManager();
                BlacklistVO blacklistVO             = new BlacklistVO();
                if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(dbStatus))
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
                            PaymentResponseText         = "Failed";
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
                    if ("ACCEPTED".equalsIgnoreCase(PaymentResponseText))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        restatus        = "success";
                        billingDesc     = gatewayAccount.getDisplayName();

                        commResponseVO.setStatus("success");
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setResponseHashInfo(transactionId);
                        commResponseVO.setDescription(PaymentResponseText);
                        commResponseVO.setRemark(PaymentResponseText);
                        commResponseVO.setDescriptor(billingDesc);
                        commResponseVO.setErrorCode(PaymentResponseCode);

                        dbBuffer.append("update transaction_common set captureamount='" + amountRes + "',currency='" + currency + "' ,paymentid='" + transactionId + "',status='capturesuccess' ,remark='" + PaymentResponseText + "' ,successtimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                        transactionlogger.error("dbBuffer->" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);

                        entry.actionEntryForCommon(trackingId, amountRes, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                        updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }

                    else if ("DECLINED".equalsIgnoreCase(PaymentResponseText))
                    {
                        restatus        = "Failed";

                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        commResponseVO.setStatus("failed");
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setResponseHashInfo(transactionId);
                        commResponseVO.setDescription(PaymentResponseText);
                        commResponseVO.setRemark(PaymentResponseText);
                        commResponseVO.setErrorCode(PaymentResponseCode);

                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "' ,remark='" +  PaymentResponseText +" "+ RESPONSE_MESSAGE+ "'" + " ,failuretimestamp='" + functions.getTimestamp());
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
                        asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, restatus, PaymentResponseText, "");
                    }
                }
            }

            res.setStatus(200);
            return;
        }

        catch (JSONException e)
        {
            transactionlogger.error("JSONException---->",e);
        }

        catch (Exception ex)
        {
            transactionlogger.error("Exception---->",ex);
        }
        finally
        {
            Database.closeConnection(con);
        }



    }
}

