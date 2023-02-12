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
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.response.PZResponseStatus;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.json.JSONObject;
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
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.Enumeration;

/**
 * Created by Admin on 9/13/2021.
 */

public class ESBackendServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(ESBackendServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        TransactionManager transactionManager = new TransactionManager();
        ActionEntry entry = new ActionEntry();
        CommResponseVO commResponseVO                       = new CommResponseVO();
        AuditTrailVO auditTrailVO                           = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO                 = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO         = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO            = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO                  = new GenericCardDetailsVO();
        MerchantDetailsVO merchantDetailsVO                 = null;
        Functions functions                                 = new Functions();
        CommRequestVO requestVO                             = new CommRequestVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = new CommTransactionDetailsVO();
        MerchantDAO merchantDAO=new MerchantDAO();
        StatusSyncDAO statusSyncDAO                 = new StatusSyncDAO();
        PrintWriter pWriter         = res.getWriter();
        String responseCode         = "200";
        String returnResStatus      = "OK";

        Connection con = null;
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
        String trackingId= "";


        String standing_order_id="";
        String event_timestamp="";
        String event_id="";
        String event_value="";
        String record_type="";
        String record_id="";

        StringBuilder responseMsg=new StringBuilder();

        BufferedReader br=req.getReader();
        String str;
        while ((str=br.readLine())!=null)
        {
            responseMsg.append(str);
        }
        transactionLogger.error("ESBackendServlet Notification JSON-----" + responseMsg);
        try
        {

            JSONObject jsonObject = new JSONObject(responseMsg.toString());

            transactionLogger.error(" ESBackendServlet postdataJSON" + jsonObject);

            if (jsonObject.has("event_timestamp"))
            {
                event_timestamp = jsonObject.getString("event_timestamp");
            }

            if (jsonObject.has("event_id"))
            {
                event_id = jsonObject.getString("event_id");
            }

            if (jsonObject.has("event_value"))
            {
                txnStatus = jsonObject.getString("event_value");
            }

            if (jsonObject.has("record_type"))
            {
                record_type = jsonObject.getString("record_type");
            }
            if (jsonObject.has("record_id"))
            {
                record_id = jsonObject.getString("record_id");
            }

            if (functions.isValueNull(record_type)&&"PaylinkPayment".equalsIgnoreCase(record_type))
            {
                record_id = event_id;
            }


            TransactionDetailsVO transactionDetailsVO = transactionManager.getDetailFromCommonBasedOnPaymentId(record_id);

            if (transactionDetailsVO != null)
            {
                trackingId=transactionDetailsVO.getTrackingid();
                transactionLogger.error("ESBackendServlet trackingid------>" + transactionDetailsVO.getTrackingid());

                accountId       = transactionDetailsVO.getAccountId();
                cardType        = transactionDetailsVO.getCardtype();
                toId            = transactionDetailsVO.getToid();
                dbStatus        = transactionDetailsVO.getStatus();
                amount          = transactionDetailsVO.getAmount();
                currency        = transactionDetailsVO.getCurrency();
                tmpl_amt        = transactionDetailsVO.getTemplateamount();
                tmpl_currency   = transactionDetailsVO.getTemplatecurrency();
                custId          = transactionDetailsVO.getCustomerId();
                email           = transactionDetailsVO.getEmailaddr();
                if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                    transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                    transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));

                auditTrailVO.setActionExecutorName("ESBackendServlet");
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
                    if (functions.isValueNull(tmpl_amt))
                    {
                        commResponseVO.setTmpl_Amount(tmpl_amt);
                    }
                    else
                    {
                        commResponseVO.setTmpl_Amount(transactionDetailsVO.getAmount());
                    }
                    commResponseVO.setTmpl_Currency(transactionDetailsVO.getCurrency());
                   /* if(functions.isValueNull(respAmount))
                    {
                        Double compRsAmount= Double.valueOf(respAmount);
                        Double compDbAmount= Double.valueOf(amount);

                        transactionLogger.error("ESBackendServlet backend response amount --->"+compRsAmount);
                        transactionLogger.error("ESBackendServlet backend DB Amount--->"+compDbAmount);
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
                    }*/
                    //requestVO.setAddressDetailsVO(commAddressDetailsVO);
                    //commResponseVO.setBankTransactionDate(bankTransactionDate);
                    // commResponseVO.setErrorCode(status);
                    StringBuffer dbBuffer = new StringBuffer();
                    if ("Completed".equalsIgnoreCase(txnStatus))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        restatus        ="success";
                        billingDesc=gatewayAccount.getDisplayName();
                        // message = "Transaction Successful";
                        commResponseVO.setTransactionId(record_id);
                        commResponseVO.setResponseHashInfo(record_id);
                        commResponseVO.setDescription("success");
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark(txnStatus);
                        commResponseVO.setDescriptor(billingDesc);
                        // dbStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        dbBuffer.append("update transaction_common set captureamount='" + amount + "',currency='" + currency +  "',status='capturesuccess' ,remark='" + message + "' where trackingid = " + trackingId);
                        transactionLogger.error("dbBuffer->" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                        updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }
                    else if ("Failed".equalsIgnoreCase(txnStatus) || "Abandoned".equalsIgnoreCase(txnStatus)||"Rejected".equalsIgnoreCase(txnStatus)|| "Canceled".equalsIgnoreCase(txnStatus))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        restatus="Failed";
                        //   message = txnStatus;
                        commResponseVO.setTransactionId(record_id);
                        commResponseVO.setResponseHashInfo(record_id);
                        commResponseVO.setDescription(message);
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark(message);
                        dbBuffer.append("update transaction_common set status='authfailed' ,remark='" +  message + "'");

                        dbBuffer.append(" where trackingid = " + trackingId);

                        transactionLogger.error("dbBuffer->" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                        updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }

                    if(!functions.isValueNull(notificationUrl) && functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                        notificationUrl = merchantDetailsVO.getNotificationUrl();
                    }
                    if (functions.isValueNull(notificationUrl))
                    {
                        transactionLogger.error("Inside sending notification---" + notificationUrl);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        transactionDetailsVO.setBillingDesc(billingDesc);
                        if(functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                            transactionDetailsVO.setMerchantNotificationUrl(merchantDetailsVO.getNotificationUrl());
                        }else{
                            transactionDetailsVO.setMerchantNotificationUrl("");
                        }
                        asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, message, "");
                    }
                }
            }

            res.setStatus(200);
            return;
        }


        catch (Exception ex)
        {
            transactionLogger.error("Exception---->", ex);
        }
        finally
        {
            Database.closeConnection(con);
        }

    }

}
