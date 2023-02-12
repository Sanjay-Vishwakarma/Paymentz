package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.BlacklistManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.statussync.StatusSyncDAO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.Enumeration;

/**
 * Created by Admin on 2/23/2021.
 */
public class vervepaybackendservlet  extends HttpServlet
{
    private static VervePayGatewayLogger transactionLogger = new VervePayGatewayLogger(vervepaybackendservlet.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.error("----Inside vervepaybackendservlet----");
        Functions functions = new Functions();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        StringBuilder responseMsg = new StringBuilder();
        TransactionManager transactionManager = new TransactionManager();
        CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();
        ActionEntry entry = new ActionEntry();
        MySQLCodec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        Enumeration enumeration = request.getParameterNames();
        BufferedReader br = request.getReader();
        String str;
        String trackingId = "";
        Connection con=null;
        while ((str = br.readLine()) != null)
        {
            responseMsg.append(str);
        }


        transactionLogger.error("-----vervepaybackendservlet   response-----" + responseMsg);

        String toId = "";
        String accountId = "";
        String fromType = "";
        String paymentId = "";
        String respAmount = "";
        String amount = "";
        String currency = "";
        String errorCode = "";
        String description = "";
        String descriptor = "";
        String transactionStatus = "";
        String tmpl_currency = "";
        String tmpl_amt = "";
        String timestamp = "";
        String eci = "";
        String transactionId = "";
        String isService = "";
        String transType = "";
        String status = "";
        String message = "";
        String dbStatus = "";
        String errorName = "";
        String confirmStatus = "";
        String billingDesc = "";
        String merchantTransactionId = "";
        String notificationUrl = "";
        String updatedStatus = "";
        String cardType = "";
        String cardTypeName = "";
        String bankTransactionDate = "";
        String txnStatus = "";
        String txnFinalStatus = "";
        String email = "";
        String custId = "";
        String restatus = "";

        String ipnevent= "";
        String postdata= "";
        //String status;
        //  String message;
        String authcode= "";
        String code= "";
        String RESPONSE_CODE="";
        String orderid = "";
        String transactionid= "";
        String custIp= "";
        try
        {

            JSONObject jsonObject = new JSONObject(responseMsg.toString());
            postdata=jsonObject.getString("postdata");
            JSONObject postdataJSON = new JSONObject(postdata);
            transactionLogger.error("postdataJSON" + jsonObject);

            if (jsonObject.has("ipnevent"))
            {
                ipnevent = jsonObject.getString("ipnevent");
                transactionLogger.error("ipnevent--->" + ipnevent);
            }
            if (postdataJSON.has("status"))
            {
                txnStatus = postdataJSON.getString("status");
                transactionLogger.error("status-->" + txnStatus);
            }
            if (postdataJSON.has("code"))
            {
                code = postdataJSON.getString("code");
                transactionLogger.error("code-->"+code);
            }
            if (postdataJSON.has("message"))
            {
                message = postdataJSON.getString("message");
                transactionLogger.error("message-->"+message);
            }
            if (postdataJSON.has("amount"))
            {
                respAmount = postdataJSON.getString("amount");
                transactionLogger.error("respAmount-->"+respAmount);
            }
            if (postdataJSON.has("authcode"))
            {
                authcode = postdataJSON.getString("authcode");
                transactionLogger.error("authcode-->"+authcode);
            }
            if (postdataJSON.has("orderid"))
            {
                trackingId = postdataJSON.getString("orderid");
                transactionLogger.error("trackingId-->"+trackingId);
            }
            if (postdataJSON.has("transactionid"))
            {
                transactionid = postdataJSON.getString("transactionid");
                transactionLogger.error("transactionid-->"+transactionid);
            }





            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            if (transactionDetailsVO != null)
            {
                accountId = transactionDetailsVO.getAccountId();
                cardType = transactionDetailsVO.getCardtype();
                toId = transactionDetailsVO.getToid();
                dbStatus = transactionDetailsVO.getStatus();
                notificationUrl = transactionDetailsVO.getNotificationUrl();
                amount = transactionDetailsVO.getAmount();
                currency = transactionDetailsVO.getCurrency();
                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                custId = transactionDetailsVO.getCustomerId();
                custIp = transactionDetailsVO.getCustomerIp();
                email = transactionDetailsVO.getEmailaddr();
                if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                    transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                    transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));

                auditTrailVO.setActionExecutorName("vervepaybackendservlet");
                auditTrailVO.setActionExecutorId(toId);
                merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());


                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                transactionLogger.error("DbStatus------>" + dbStatus);
                transactionLogger.error("PZTransactionStatus.AUTH_STARTED------>" +PZTransactionStatus.AUTH_STARTED.toString());
                BlacklistManager blacklistManager=new BlacklistManager();
                BlacklistVO blacklistVO=new BlacklistVO();
                if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(dbStatus))
                {

                    transactionLogger.error("inside if ------" );
                    con = Database.getConnection();

                    commResponseVO.setCurrency(currency);
                    commResponseVO.setTmpl_Amount(tmpl_amt);
                    commResponseVO.setTmpl_Currency(transactionDetailsVO.getCurrency());
                    if(functions.isValueNull(respAmount))
                    {
                    Double compRsAmount= Double.valueOf(respAmount);
                    Double compDbAmount= Double.valueOf(amount);

                    transactionLogger.error("vervepay backend response amount --->"+compRsAmount);
                    transactionLogger.error("vervepay backend DB Amount--->"+compDbAmount);
                    if(!compDbAmount.equals(compRsAmount)){
                        txnStatus= "TXN_FAILED";
                        message="Failed-IRA";
                        transactionLogger.error("inside else Amount incorrect--->"+respAmount);
                        RESPONSE_CODE="11111";
                        amount=respAmount;
                        blacklistVO.setVpaAddress(custId);
                        blacklistVO.setIpAddress(custIp);
                        blacklistVO.setEmailAddress(email);
                        blacklistVO.setActionExecutorId(toId);
                        blacklistVO.setActionExecutorName("vervepaybackendservlet");
                        blacklistVO.setRemark("IncorrectAmount");
                        blacklistManager.commonBlackListing(blacklistVO);
                    }
                    }
                    //requestVO.setAddressDetailsVO(commAddressDetailsVO);
                    //commResponseVO.setBankTransactionDate(bankTransactionDate);
                    // commResponseVO.setErrorCode(status);
                    StringBuffer dbBuffer = new StringBuffer();
                    if ("TXN_COMPLETE".equalsIgnoreCase(txnStatus))
                    {
                        restatus="success";
                        billingDesc=gatewayAccount.getDisplayName();
                        // message = "Transaction Successful";
                        commResponseVO.setTransactionId(transactionid);
                        commResponseVO.setResponseHashInfo(authcode);
                        commResponseVO.setDescription(message);
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescriptor(billingDesc);
                        // dbStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        dbBuffer.append("update transaction_common set captureamount='" + respAmount + "',currency='" + currency + "' ,paymentid='" + authcode + "',status='capturesuccess' ,remark='" + message + "' where trackingid = " + trackingId);
                        transactionLogger.error("dbBuffer->" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, respAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                        updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }
                    else if ("fail".equalsIgnoreCase(txnStatus) || "TXN_FAILED".equalsIgnoreCase(txnStatus)||txnStatus.contains("FAIL")||txnStatus.contains("fail"))
                    {
                        restatus="Failed";
                        //   message = txnStatus;
                        commResponseVO.setTransactionId(transactionid);
                        commResponseVO.setResponseHashInfo(authcode);
                        commResponseVO.setDescription(message);
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark(message);
                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + authcode + "' ,remark='" +  message + "'");

                        dbBuffer.append(" where trackingid = " + trackingId);

                        transactionLogger.error("dbBuffer->" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                        updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }
                    if (functions.isValueNull(updatedStatus) && functions.isValueNull(notificationUrl))
                    {
                        transactionLogger.error("Inside sending notification---" + notificationUrl);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        transactionDetailsVO.setBillingDesc(billingDesc);
                        asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, restatus, message, "");
                    }
                }
            }

            response.setStatus(200);
            return;
        }

        catch (JSONException e)
        {
            transactionLogger.error("JSONException---->",e);
        }

        catch (Exception ex)
        {
            transactionLogger.error("Exception---->",ex);
        }
        finally
        {
            Database.closeConnection(con);
        }

    }

}