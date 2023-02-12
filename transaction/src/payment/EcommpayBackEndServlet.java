package payment;

import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.manager.PaymentManager;
import com.manager.RecurringManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.dao.RecurringDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.RecurringBillingVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Ecommpay.EcommpayPaymentGateway;
import com.directi.pg.*;
import com.payment.Ecommpay.EcommpayUtils;
import com.payment.Enum.PZProcessType;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.response.PZResponseStatus;
import com.payment.statussync.StatusSyncDAO;
import org.codehaus.jettison.json.JSONArray;
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
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.TreeMap;


/**
 * Created by Sagar on 6/17/2020.
 */




public class EcommpayBackEndServlet extends HttpServlet
{
    TransactionLogger transactionLogger = new TransactionLogger(EcommpayBackEndServlet.class.getName());
    private static Logger logger = new Logger(EcommpayBackEndServlet.class.getName());


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.error("-----Inside EcommpayBackEndServlet-----");
        Connection con                  = null;
        Functions functions             = new Functions();
        StringBuilder responseMsg           = new StringBuilder();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        StatusSyncDAO statusSyncDAO         = new StatusSyncDAO();
        MySQLCodec me                       = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        PaymentManager paymentManager = new PaymentManager();
        try
        {
            BufferedReader br = request.getReader();
            String str;
            while ((str = br.readLine()) != null)
            {
                responseMsg.append(str);
            }

            transactionLogger.error("-----responseMsg-----" + responseMsg.toString());

            if (responseMsg != null)
            {
                JSONObject jsonObject = new JSONObject(responseMsg.toString());
                String trackingId = "";
                String transactionId = "";
                String type = "";
                String customer = "";
                String paymentAmount = "";
                String currency = "";
                String status = "";
                String date = "";
                String message = "";
                String toId = "";
                String accountId = "";
                String dbStatus = "";
                String billingDesc = "";
                String confirmStatus = "";
                String notificationUrl = "";
                String updatedStatus = "";
                String code = "";
                String eci = "";
                String field = "";
                String id="";
                String responseAmount="";
                String acs_url="";
                String pa_req="";
                String md="";
                String term_url="";


                if (jsonObject != null)
                {
                    if (jsonObject.has("payment"))
                    {
                        if (jsonObject.getJSONObject("payment").has("id"))
                        {
                            trackingId = jsonObject.getJSONObject("payment").getString("id");
                        }
                        if (jsonObject.getJSONObject("payment").has("status"))
                        {
                            status = jsonObject.getJSONObject("payment").getString("status");
                        }
                        if (jsonObject.getJSONObject("payment").has("date"))
                        {
                            date = jsonObject.getJSONObject("payment").getString("date");
                        }
                    }
                    if(jsonObject.has("recurring"))
                    {
                        if(jsonObject.getJSONObject("recurring").has("id"))
                        {
                            id=jsonObject.getJSONObject("recurring").getString("id");
                        }
                    }
                    if(jsonObject.has("redirect_data"))
                    {
                        JSONObject redirectDataJson=jsonObject.getJSONObject("redirect_data");
                        if(redirectDataJson.has("url"))
                        {
                            acs_url=redirectDataJson.getString("url");
                        }
                    }
                    if (jsonObject.has("acs"))
                    {
                        JSONObject acs = jsonObject.getJSONObject("acs");

                        if (acs.has("pa_req"))
                        {
                            pa_req = acs.getString("pa_req");
                        }
                        if (acs.has("acs_url"))
                        {
                            acs_url = acs.getString("acs_url");
                        }
                        if (acs.has("md"))
                        {
                            md = acs.getString("md");
                        }
                        if (acs.has("term_url"))
                        {
                            term_url = acs.getString("term_url");
                        }
                    }
                    if (jsonObject.has("operation"))
                    {
                        if (jsonObject.getJSONObject("operation").has("type"))
                        {
                            type = jsonObject.getJSONObject("operation").getString("type");
                        }
                        if (jsonObject.getJSONObject("operation").has("code"))
                        {
                            code = jsonObject.getJSONObject("operation").getString("code");
                        }
                        if (jsonObject.getJSONObject("operation").has("message"))
                        {
                            message = jsonObject.getJSONObject("operation").getString("message");
                        }
                        if (jsonObject.getJSONObject("operation").has("eci"))
                        {
                            eci = jsonObject.getJSONObject("operation").getString("eci");
                        }
                        if(jsonObject.getJSONObject("operation").has("provider"))
                        {
                            if (jsonObject.getJSONObject("operation").getJSONObject("provider").has("payment_id"))
                            {
                                transactionId = jsonObject.getJSONObject("operation").getJSONObject("provider").getString("payment_id");
                            }
                        }
                        if(jsonObject.getJSONObject("operation").has("sum_initial"))
                        {
                            if(jsonObject.getJSONObject("operation").getJSONObject("sum_initial").has("amount"))
                            {
                                responseAmount=jsonObject.getJSONObject("operation").getJSONObject("sum_initial").getString("amount");
                            }
                        }
                    }

                    if (jsonObject.has("errors"))
                    {
                        JSONArray jsonArray = jsonObject.getJSONArray("errors");
                        if (jsonArray != null)
                        {
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject errors = jsonArray.getJSONObject(i);
                                if (i != 0)
                                {
                                    message = message + "|" + errors.getString("field") + " " + errors.getString("message");
                                }
                                else
                                {
                                    message = errors.getString("field") + " " + errors.getString("message");
                                }
                            }
                        }
                    }
                    transactionLogger.error("trackingId-->" + trackingId);
                    transactionLogger.error("transactionId-->" + transactionId);
                    transactionLogger.error("status-->" + status);

                    if (functions.isValueNull(trackingId))
                    {
                        TransactionManager transactionManager   = new TransactionManager();
                        MerchantDAO merchantDAO                 = new MerchantDAO();
                        ActionEntry entry                       = new ActionEntry();
                        CommResponseVO commResponseVO           = new CommResponseVO();
                        AuditTrailVO auditTrailVO               = new AuditTrailVO();
                        PrintWriter pWriter                         = response.getWriter();
                        TransactionDetailsVO transactionDetailsVO   = transactionManager.getTransDetailFromCommon(trackingId);
                        if (transactionDetailsVO != null)
                        {
                            toId            = transactionDetailsVO.getToid();
                            accountId       = transactionDetailsVO.getAccountId();
                            dbStatus        = transactionDetailsVO.getStatus();
                            paymentAmount   = transactionDetailsVO.getAmount();
                            currency        = transactionDetailsVO.getCurrency();

                            if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                                transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                            if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                            {
                                transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));
                            }
                            merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                            transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());

                            transactionLogger.error("DbStatus------" + dbStatus);
                            con = Database.getConnection();
                            if ((PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus))&&!"recurring".equalsIgnoreCase(type))
                            {
                                if(("awaiting 3ds result".equalsIgnoreCase(status) ||"awaiting redirect result".equalsIgnoreCase(status)) && !"processing".equalsIgnoreCase(status))
                                {
                                    boolean is3DDataInserted= EcommpayUtils.insertThreeDsData(trackingId,acs_url,pa_req,md,term_url);
                                    transactionLogger.error("is3DDataInserted--"+trackingId+"-->"+is3DDataInserted);
                                }
                                else if (functions.isValueNull(status) && !"awaiting 3ds result".equalsIgnoreCase(status) && !"processing".equalsIgnoreCase(status) && !"awaiting redirect result".equalsIgnoreCase(status))
                                {
                                    auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                                    auditTrailVO.setActionExecutorId(toId);
                                    StringBuffer dbBuffer = new StringBuffer();


                                    if (type.equalsIgnoreCase("sale"))
                                    {
                                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                                        if ("success".equalsIgnoreCase(status))
                                        {
                                            status = "success";
                                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                                            commResponseVO.setDescription(message);
                                            commResponseVO.setStatus(status);
                                            commResponseVO.setRemark(message);
                                            commResponseVO.setDescriptor(billingDesc);
                                            commResponseVO.setCurrency(currency);
                                            commResponseVO.setTmpl_Amount(paymentAmount);
                                            commResponseVO.setTmpl_Currency(transactionDetailsVO.getCurrency());
                                            commResponseVO.setTransactionType(type);
                                            commResponseVO.setTransactionId(transactionId);
                                            commResponseVO.setEci(eci);
                                            commResponseVO.setBankTransactionDate(date);
                                            commResponseVO.setErrorCode(code);
                                            commResponseVO.setResponseHashInfo(id);

                                            confirmStatus = "Y";
                                            dbStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                            dbBuffer.append("update transaction_common set captureamount='" + paymentAmount + "',currency='" + currency + "' ,paymentid='" + transactionId + "',status='capturesuccess' ,remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                                            transactionLogger.error("dbBuffer->" + dbBuffer);
                                            Database.executeUpdate(dbBuffer.toString(), con);
                                            entry.actionEntryForCommon(trackingId, paymentAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                            updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                        }
                                        else
                                        {
                                            confirmStatus = "N";
                                            status = "failed";
                                            commResponseVO.setStatus(status);
                                            commResponseVO.setDescription(message);
                                            commResponseVO.setRemark(message);
                                            commResponseVO.setCurrency(currency);
                                            commResponseVO.setTmpl_Amount(paymentAmount);
                                            commResponseVO.setTmpl_Currency(transactionDetailsVO.getCurrency());
                                            commResponseVO.setTransactionType(type);
                                            commResponseVO.setTransactionId(transactionId);
                                            commResponseVO.setEci(eci);
                                            commResponseVO.setBankTransactionDate(date);
                                            commResponseVO.setErrorCode(code);


                                            dbStatus = PZTransactionStatus.AUTH_FAILED.toString();
                                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "' ,remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                                            transactionLogger.error("dbBuffer->" + dbBuffer);
                                            Database.executeUpdate(dbBuffer.toString(), con);
                                            entry.actionEntryForCommon(trackingId, paymentAmount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                                            updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                                        }
                                    }
                                    else if (type.equalsIgnoreCase("auth"))
                                    {
                                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                                        if ("awaiting capture".equalsIgnoreCase(status))
                                        {
                                            status = "success";
                                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                                            commResponseVO.setDescription(message);
                                            commResponseVO.setStatus(status);
                                            commResponseVO.setRemark(message);
                                            commResponseVO.setDescriptor(billingDesc);
                                            commResponseVO.setCurrency(currency);
                                            commResponseVO.setTmpl_Amount(paymentAmount);
                                            commResponseVO.setTmpl_Currency(transactionDetailsVO.getCurrency());
                                            commResponseVO.setTransactionType(type);
                                            commResponseVO.setTransactionId(transactionId);
                                            commResponseVO.setEci(eci);
                                            commResponseVO.setBankTransactionDate(date);
                                            commResponseVO.setErrorCode(code);
                                            commResponseVO.setResponseHashInfo(id);

                                            confirmStatus = "Y";
                                            dbStatus = PZTransactionStatus.AUTH_SUCCESS.toString();
                                            dbBuffer.append("update transaction_common set currency='" + currency + "' ,paymentid='" + transactionId + "',status='authsuccessful' ,remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                                            transactionLogger.error("dbBuffer->" + dbBuffer);
                                            Database.executeUpdate(dbBuffer.toString(), con);
                                            entry.actionEntryForCommon(trackingId, paymentAmount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                            updatedStatus = PZTransactionStatus.AUTH_SUCCESS.toString();
                                        }
                                        else
                                        {
                                            confirmStatus = "N";
                                            status = "failed";
                                            commResponseVO.setStatus(status);
                                            commResponseVO.setDescription(message);
                                            commResponseVO.setRemark(message);
                                            commResponseVO.setCurrency(currency);
                                            commResponseVO.setTmpl_Amount(paymentAmount);
                                            commResponseVO.setTmpl_Currency(transactionDetailsVO.getCurrency());
                                            commResponseVO.setTransactionType(type);
                                            commResponseVO.setTransactionId(transactionId);
                                            commResponseVO.setEci(eci);
                                            commResponseVO.setBankTransactionDate(date);
                                            commResponseVO.setErrorCode(code);

                                            dbStatus = PZTransactionStatus.AUTH_FAILED.toString();
                                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "' ,remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                                            transactionLogger.error("dbBuffer->" + dbBuffer);
                                            Database.executeUpdate(dbBuffer.toString(), con);
                                            entry.actionEntryForCommon(trackingId, paymentAmount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                                            updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                                        }
                                    }
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                                }
                                   if (!functions.isValueNull(updatedStatus))
                                    {
                                        updatedStatus = dbStatus;
                                    }
                                    if (functions.isValueNull(notificationUrl) || functions.isValueNull(merchantDetailsVO.getNotificationUrl()))
                                    {
                                        transactionLogger.error("Inside sending notification---" + notificationUrl);
                                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                        transactionDetailsVO.setBillingDesc(billingDesc);
                                        transactionDetailsVO.setBankReferenceId(transactionId);
                                        if(functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                                            transactionDetailsVO.setMerchantNotificationUrl(merchantDetailsVO.getNotificationUrl());;
                                        }else{
                                            transactionDetailsVO.setMerchantNotificationUrl("");
                                        }
                                        asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, message, "");
                                    }
                                }

                            else if (type.equalsIgnoreCase("refund"))
                            {
                                if (("partially refunded".equalsIgnoreCase(status) || "refunded".equalsIgnoreCase(status)) && PZTransactionStatus.MARKED_FOR_REVERSAL.toString().equalsIgnoreCase(dbStatus))
                                {
                                    status = "success";
                                    billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                                    auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                                    auditTrailVO.setActionExecutorId(toId);
                                    confirmStatus = "Y";
                                    dbStatus = PZTransactionStatus.REVERSED.toString();
                                    String refundstatus = "reversed";
                                    String refundedAmount = transactionDetailsVO.getRefundAmount();
                                    String captureAmount = transactionDetailsVO.getCaptureAmount();
                                    double refAmount = Double.parseDouble(refundedAmount) + Double.parseDouble(responseAmount);

                                    String actionEntryAction = ActionEntry.ACTION_REVERSAL_SUCCESSFUL;
                                    String actionEntryStatus = ActionEntry.STATUS_REVERSAL_SUCCESSFUL;
                                    String transactionStatus = PZResponseStatus.SUCCESS.toString();
                                    StringBuffer dbBuffer=new StringBuffer();


                                    commResponseVO.setStatus("success");
                                    commResponseVO.setDescription(message);
                                    commResponseVO.setRemark(message);
                                    commResponseVO.setDescriptor(billingDesc);
                                    commResponseVO.setTransactionStatus(status);
                                    commResponseVO.setTransactionId(transactionId);
                                    commResponseVO.setCurrency(currency);
                                    commResponseVO.setTmpl_Amount(paymentAmount);
                                    commResponseVO.setTmpl_Currency(transactionDetailsVO.getCurrency());
                                    commResponseVO.setTransactionType(type);
                                    commResponseVO.setEci(eci);
                                    commResponseVO.setBankTransactionDate(date);
                                    commResponseVO.setErrorCode(code);
                                    commResponseVO.setResponseHashInfo(id);

                                    if (Double.parseDouble(captureAmount) > Double.parseDouble(String.valueOf(refAmount)))
                                    {
                                        status = "reversed";
                                        dbStatus = "";
                                        actionEntryAction = ActionEntry.ACTION_PARTIAL_REFUND;
                                        actionEntryStatus = ActionEntry.STATUS_PARTIAL_REFUND;
                                        transactionStatus = PZResponseStatus.PARTIALREFUND.toString();
                                    }

                                    if (captureAmount.equals(String.format("%.2f", refAmount)))
                                    {
                                        status = "reversed";
                                        actionEntryAction = ActionEntry.ACTION_REVERSAL_SUCCESSFUL;
                                        actionEntryStatus = ActionEntry.STATUS_REVERSAL_SUCCESSFUL;
                                        transactionStatus = PZResponseStatus.SUCCESS.toString();
                                    }

                                    dbBuffer.append("update transaction_common set status='reversed',refundAmount='" + String.format("%.2f", refAmount) + "'where trackingid = " + trackingId);


                                    transactionLogger.error("dbBuffer->" + dbBuffer);
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    entry.actionEntryForCommon(trackingId, String.format("%.2f", refAmount), actionEntryAction, actionEntryStatus, commResponseVO, auditTrailVO, null);
                                    updatedStatus = PZTransactionStatus.REVERSED.toString();
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                                }
                            }
                            else if (type.equalsIgnoreCase("capture"))
                            {
                                notificationUrl = transactionDetailsVO.getNotificationUrl();
                                if (("partially capture".equalsIgnoreCase(status) || "capture".equalsIgnoreCase(status)) && PZTransactionStatus.CAPTURE_STARTED.toString().equalsIgnoreCase(dbStatus))
                                {
                                    status = "success";
                                    billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                    auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                                    auditTrailVO.setActionExecutorId(toId);

                                    confirmStatus = "Y";
                                    dbStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                    String capturestatus = "capturesuccess";
                                    double captureAmount = Double.parseDouble(responseAmount);

                                    String actionEntryAction = ActionEntry.ACTION_CAPTURE_SUCCESSFUL;
                                    String actionEntryStatus = ActionEntry.STATUS_CAPTURE_SUCCESSFUL;
                                    String transactionStatus = PZResponseStatus.SUCCESS.toString();
                                    StringBuffer dbBuffer=new StringBuffer();


                                    commResponseVO.setStatus("success");
                                    commResponseVO.setDescriptor(billingDesc);
                                    commResponseVO.setTransactionStatus(status);
                                    commResponseVO.setTransactionId(transactionId);
                                    commResponseVO.setCurrency(currency);
                                    commResponseVO.setTmpl_Amount(paymentAmount);
                                    commResponseVO.setTmpl_Currency(transactionDetailsVO.getCurrency());
                                    commResponseVO.setTransactionType(type);
                                    commResponseVO.setEci(eci);
                                    commResponseVO.setBankTransactionDate(date);
                                    commResponseVO.setErrorCode(code);
                                    commResponseVO.setResponseHashInfo(id);
                                    dbBuffer.append("update transaction_common set status='capturesuccess',captureAmount='" + String.format("%.2f", captureAmount) + "' where trackingid = " + trackingId);


                                    transactionLogger.error("dbBuffer->" + dbBuffer);
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    entry.actionEntryForCommon(trackingId, String.format("%.2f", captureAmount), actionEntryAction, actionEntryStatus, commResponseVO, auditTrailVO, null);
                                    updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);

                                    if (functions.isValueNull(notificationUrl) || functions.isValueNull(merchantDetailsVO.getNotificationUrl()))
                                    {
                                        transactionLogger.error("Inside sending notification---" + notificationUrl);
                                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                        transactionDetailsVO.setBillingDesc(billingDesc);
                                        if(functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                                            transactionDetailsVO.setMerchantNotificationUrl(merchantDetailsVO.getNotificationUrl());;
                                        }else{
                                            transactionDetailsVO.setMerchantNotificationUrl("");
                                        }
                                        asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, message, "");
                                    }

                                }
                            }
                            else if (type.equalsIgnoreCase("cancel"))
                            {
                                if (("canceled".equalsIgnoreCase(status)) && PZTransactionStatus.CANCEL_STARTED.toString().equalsIgnoreCase(dbStatus))
                                {
                                    status = "success";
                                    billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                    auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                                    auditTrailVO.setActionExecutorId(toId);

                                    confirmStatus = "Y";
                                    dbStatus = PZTransactionStatus.CANCEL_STARTED.toString();
                                    double cancelAmount = Double.parseDouble(responseAmount);

                                    String actionEntryAction = ActionEntry.ACTION_CANCEL_SUCCESSFUL;
                                    String actionEntryStatus = ActionEntry.STATUS_CANCEL_STARTED;
                                    String transactionStatus = PZResponseStatus.CANCELLED.toString();
                                    StringBuffer dbBuffer=new StringBuffer();


                                    commResponseVO.setStatus("success");
                                    commResponseVO.setDescriptor(billingDesc);
                                    commResponseVO.setTransactionStatus(status);
                                    commResponseVO.setTransactionId(transactionId);
                                    commResponseVO.setCurrency(currency);
                                    commResponseVO.setTmpl_Amount(paymentAmount);
                                    commResponseVO.setTmpl_Currency(transactionDetailsVO.getCurrency());
                                    commResponseVO.setTransactionType(type);
                                    commResponseVO.setEci(eci);
                                    commResponseVO.setBankTransactionDate(date);
                                    commResponseVO.setErrorCode(code);
                                    commResponseVO.setResponseHashInfo(id);
                                    dbBuffer.append("update transaction_common set status='authcancelled' where trackingid = " + trackingId);

                                    transactionLogger.error("dbBuffer->" + dbBuffer);
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    entry.actionEntryForCommon(trackingId, String.format("%.2f", cancelAmount), actionEntryAction, actionEntryStatus, commResponseVO, auditTrailVO, null);
                                    updatedStatus = PZTransactionStatus.CANCEL_STARTED.toString();
                                }
                            }
                            else if (type.equalsIgnoreCase("payout"))
                            {
                                String payoutAmount = String.format("%.2f", Double.parseDouble(responseAmount));
                                auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                                auditTrailVO.setActionExecutorId(toId);

                                if(PZTransactionStatus.PAYOUT_STARTED.toString().equalsIgnoreCase(dbStatus))
                                {
                                    if ("success".equalsIgnoreCase(status))
                                    {
                                        status = "success";
                                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                        dbStatus = PZTransactionStatus.PAYOUT_SUCCESS.toString();

                                        commResponseVO.setStatus("success");
                                        commResponseVO.setDescriptor(billingDesc);
                                        commResponseVO.setTransactionStatus(status);
                                        commResponseVO.setTransactionId(transactionId);
                                        commResponseVO.setCurrency(currency);
                                        commResponseVO.setTmpl_Amount(paymentAmount);
                                        commResponseVO.setTmpl_Currency(transactionDetailsVO.getCurrency());
                                        commResponseVO.setTransactionType(type);
                                        commResponseVO.setEci(eci);
                                        commResponseVO.setBankTransactionDate(date);
                                        commResponseVO.setErrorCode(code);
                                        commResponseVO.setResponseHashInfo(id);

                                        entry.actionEntryForCommon(trackingId, payoutAmount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                        paymentManager.updateTransactionStatusAfterResponse(String.valueOf(trackingId), ActionEntry.STATUS_PAYOUT_SUCCESSFUL, payoutAmount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, transactionId,null);
                                        updatedStatus = PZTransactionStatus.PAYOUT_SUCCESS.toString();

                                    }
                                    else if ("processing".equalsIgnoreCase(dbStatus))
                                    {

                                    }
                                    else
                                    {
                                        confirmStatus = "N";
                                        status = "failed";
                                        commResponseVO.setStatus(status);
                                        commResponseVO.setDescription(message);
                                        commResponseVO.setRemark(message);
                                        commResponseVO.setCurrency(currency);
                                        commResponseVO.setTmpl_Amount(paymentAmount);
                                        commResponseVO.setTmpl_Currency(transactionDetailsVO.getCurrency());
                                        commResponseVO.setTransactionType(type);
                                        commResponseVO.setTransactionId(transactionId);
                                        commResponseVO.setEci(eci);
                                        commResponseVO.setBankTransactionDate(date);
                                        commResponseVO.setErrorCode(code);

                                        entry.actionEntryForCommon(trackingId, payoutAmount, ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.STATUS_PAYOUT_FAILED, commResponseVO, auditTrailVO, null);
                                        paymentManager.updateTransactionStatusAfterResponse(String.valueOf(trackingId), ActionEntry.STATUS_PAYOUT_FAILED, payoutAmount, ActionEntry.ACTION_PAYOUT_FAILED, transactionId,null);
                                    }
                                }
                            }
                            if (type.equalsIgnoreCase("recurring"))
                            {
                                if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus))
                                {
                                    notificationUrl = transactionDetailsVO.getNotificationUrl();
                                    String billingDescriptor = "";
                                    CommResponseVO responseVO = new CommResponseVO();
                                    RecurringBillingVO recurringBillingVO = new RecurringBillingVO();
                                    RecurringManager recurringManager = new RecurringManager();
                                    RecurringDAO recurringDAO = new RecurringDAO();
                                    String recurringAmount = String.format("%.2f", Double.parseDouble(responseAmount));

                                    auditTrailVO.setActionExecutorName("Manual Rebill");
                                    auditTrailVO.setActionExecutorId(toId);

                                    if (responseVO.getStatus().equalsIgnoreCase("success"))
                                    {
                                        billingDescriptor = responseVO.getDescriptor();
                                        paymentManager.updateTransactionAfterResponseForCommon("capturesuccess", recurringAmount, "", transactionId, message, date, String.valueOf(trackingId));
                                        entry.actionEntryForCommon(String.valueOf(trackingId), recurringAmount, entry.STATUS_CAPTURE_SUCCESSFUL, entry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVO, null);
                                        recurringBillingVO = recurringDAO.getRecurringSubscriptionDetails(trackingId);

                                        recurringBillingVO.setRecurring_subscrition_id(recurringBillingVO.getRecurring_subscrition_id());
                                        recurringBillingVO.setParentBankTransactionID("");
                                        recurringBillingVO.setBankRecurringBillingID("");//197
                                        recurringBillingVO.setNewBankTransactionID(responseVO.getTransactionId());//R6
                                        recurringBillingVO.setParentPzTransactionID(trackingId);//R5
                                        recurringBillingVO.setAmount(recurringAmount);
                                        recurringBillingVO.setDescription(trackingId);//R3
                                        recurringBillingVO.setNewPzTransactionID(String.valueOf(trackingId));
                                        recurringBillingVO.setRecurringRunDate(String.valueOf(responseVO.getResponseTime()));
                                        recurringBillingVO.setTransactionStatus(status);

                                        recurringManager.insertRecurringDetailsEntry(recurringBillingVO);
                                        updatedStatus = "capturesuccess";
                                        if (functions.isValueNull(notificationUrl) || functions.isValueNull(merchantDetailsVO.getNotificationUrl()))
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
                                    else if("processing".equalsIgnoreCase(status))
                                    {
                                        updatedStatus="pending";
                                    }
                                    else
                                    {
                                        paymentManager.updateTransactionAfterResponseForCommon("authfailed", recurringAmount, "", transactionId, message, date, String.valueOf(trackingId));
                                        entry.actionEntryForCommon(String.valueOf(trackingId), recurringAmount, entry.STATUS_AUTHORISTION_FAILED, entry.STATUS_AUTHORISTION_FAILED, responseVO, auditTrailVO, null);
                                        updatedStatus = "authfailed";
                                        if (functions.isValueNull(notificationUrl) || functions.isValueNull(merchantDetailsVO.getNotificationUrl()))
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
                            }


                            response.setStatus(200);
                            return;
                        }
                    }
                }
            }

        }

        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException--", e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError--", systemError);
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException--" , e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}