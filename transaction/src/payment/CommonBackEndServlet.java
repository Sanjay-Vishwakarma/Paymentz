package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.bitcoinpayget.BitcoinPaygateResponseVO;
import com.payment.bitcoinpayget.BitcoinPaygateUtils;
import com.payment.common.core.*;
import com.payment.curo.CuroUtils;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.phoneix.PhoneixUtils;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.tojika.TojikaUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.*;

/**
 * Created by Admin on 8/24/18.
 */
public class CommonBackEndServlet extends HttpServlet
{
    private static  TransactionLogger transactionLogger= new TransactionLogger(CommonBackEndServlet.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doService(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doService(request,response);
    }

    public void doService(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
    {
        Functions functions                     = new Functions();
        TransactionManager transactionManager   = new TransactionManager();
        CommonPaymentProcess paymentProcess     = new CommonPaymentProcess();
        AuditTrailVO auditTrailVO               = new AuditTrailVO();
        Comm3DResponseVO transRespDetails       = null;
        Connection con                          = null;
        ActionEntry actionEntry                 = new ActionEntry();
        StatusSyncDAO statusSyncDAO             = new StatusSyncDAO();
        transactionLogger.error("-----inside CommonBackEndServlet-----");
        for (Object key : request.getParameterMap().keySet())
        {
            transactionLogger.debug("----for loop CommonBackEndServlet-----" + key + "=" + request.getParameter((String) key) + "----------");
        }


        PrintWriter pWriter         = response.getWriter();
        String responseCode         = "200";
        String responseStatus       = "OK";
        StringBuilder responseMsg   = new StringBuilder();
        BufferedReader br           = request.getReader();
        String str;
        while ((str = br.readLine()) != null)
        {
            responseMsg.append(str);
        }

        transactionLogger.error("-----Response JSON-----" + responseMsg);
        try
        {

            String trackingId   = "";
            String toId         = "";
            String accountId    = "";
            String fromType     = "";
            String paymentId    = "";
            String respAmount   = "";
            String respCurrency = "";
            String errorCode    = "";
            String description  = "";
            String descriptor   = "";
            String transactionStatus    = "";
            String tmpl_currency        = "";
            String tmpl_amt             = "";
            String timestamp            = "";
            String eci                  = "";
            String transactionId        = "";
            String isService            = "";
            String transType            = "";
            String status               = "";
            String message              = "";
            String dbStatus             = "";
            String errorName            = "";
            String confirmStatus        = "";
            String billingDesc          = "";
            String merchantTransactionId= "";
            String notificationUrl      = "";
            String updatedStatus        = "";
            String cardType             = "";
            String cardTypeName         = "";

            if (functions.isValueNull(request.getParameter("trackingId")))
            {
                trackingId = request.getParameter("trackingId");
            }
            // transaction_id
            else if (functions.isValueNull(request.getParameter("transaction_id")))
            {
                trackingId  = request.getParameter("transaction_id");
                transactionLogger.debug("tracking id is -----------"+trackingId);
            }
            else if (functions.isValueNull(request.getParameter("txRef")))  // txRef is Currently for FlutterWave
            {
                trackingId  = request.getParameter("txRef");
                transactionLogger.debug("tracking id is -----------"+trackingId);
            }
            else if(functions.isValueNull(request.getParameter("reference")))    // for NinjaWallet
            {
                trackingId  = request.getParameter("reference");
                transactionLogger.error("Tracking ID....."+trackingId);
            }
            else
            {
                trackingId = merchantTransactionId;
            }
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            if (responseMsg != null)
            {
                if (transactionDetailsVO != null)
                {
                    fromType            = transactionDetailsVO.getFromtype();
                    accountId           = transactionDetailsVO.getAccountId();
                    String secretKey    = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
                    transactionLogger.debug("secretKey ---"+secretKey);
                    cardType            = transactionDetailsVO.getCardtype();
                    cardTypeName        = GatewayAccountService.getCardType(cardType);
                    transactionLogger.debug("---cardType---"+cardType);
                    transactionLogger.debug("---cardTypeName---"+cardTypeName);
                    toId                = transactionDetailsVO.getToid();
                    dbStatus            = transactionDetailsVO.getStatus();
                    notificationUrl     = transactionDetailsVO.getNotificationUrl();

                    if(functions.isValueNull(transactionDetailsVO.getCcnum())){
                        transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                    }
                    if (functions.isValueNull(transactionDetailsVO.getExpdate())){
                        transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));
                    }

                    MerchantDAO merchantDAO            = new MerchantDAO();
                    MerchantDetailsVO merchantDetailsVO2 = merchantDAO.getMemberDetails(toId);

                    transactionLogger.debug("fromType----"+fromType);
                    transactionLogger.debug("accountId----"+accountId);
                    transactionLogger.debug("toId----"+toId);
                    transactionLogger.debug("dbStatus----"+dbStatus);

                    auditTrailVO.setActionExecutorName("AcquirerCommonBackEnd");
                    auditTrailVO.setActionExecutorId(toId);

                    GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                    if ("agnipay".equalsIgnoreCase(fromType) || "trnsactWLD".equalsIgnoreCase(fromType) || "shimotomo".equalsIgnoreCase(fromType))
                    {
                        JSONObject jsonObject = new JSONObject(responseMsg.toString());
                        if (jsonObject != null)
                        {
                            if (jsonObject.has("paymentId"))
                            {
                                transactionId = jsonObject.getString("paymentId");
                            }
                            if (jsonObject.has("merchantTransactionId"))
                            {
                                merchantTransactionId = jsonObject.getString("merchantTransactionId");
                            }
                            if (jsonObject.has("amount"))
                            {
                                respAmount = jsonObject.getString("amount");
                            }
                            if (jsonObject.has("currency"))
                            {
                                respCurrency = jsonObject.getString("currency");
                            }
                            if (jsonObject.has("descriptor"))
                            {
                                descriptor = jsonObject.getString("descriptor");
                            }
                            if (jsonObject.has("result"))
                            {
                                if (jsonObject.getJSONObject("result").has("code"))
                                {
                                    errorCode = jsonObject.getJSONObject("result").getString("code");
                                }
                                if (jsonObject.getJSONObject("result").has("description"))
                                {
                                    description = jsonObject.getJSONObject("result").getString("description");
                                }
                            }
                            if (jsonObject.has("transactionStatus"))
                            {
                                transactionStatus = jsonObject.getString("transactionStatus");
                            }
                            if (jsonObject.has("tmpl_currency"))
                            {
                                tmpl_currency = jsonObject.getString("tmpl_currency");
                            }
                            if (jsonObject.has("tmpl_amount"))
                            {
                                tmpl_amt = jsonObject.getString("tmpl_amount");
                            }
                            if (jsonObject.has("timestamp"))
                            {
                                timestamp = jsonObject.getString("timestamp");
                            }
                            if (jsonObject.has("eci"))
                            {
                                eci = jsonObject.getString("eci");
                            }

                            transactionLogger.debug("trackingId----"+trackingId);
                            transactionLogger.debug("paymentId----" + paymentId);
                            transactionLogger.debug("transactionStatus----" + transactionStatus);
                            transactionLogger.debug("-----inside agnipay-----");
                            transRespDetails = new Comm3DResponseVO();
                            if ("Y".equalsIgnoreCase(transactionStatus))
                            {
                                transRespDetails.setStatus("success");
                                transRespDetails.setDescriptor(gatewayAccount.getDisplayName());
                            }
                            else
                            {
                                transRespDetails.setStatus("fail");
                            }
                            transRespDetails.setAmount(respAmount);
                            transRespDetails.setCurrency(respCurrency);
                            transRespDetails.setTmpl_Amount(tmpl_amt);
                            transRespDetails.setTmpl_Currency(tmpl_currency);
                            transRespDetails.setBankTransactionDate(timestamp);
                            transRespDetails.setErrorCode(errorCode);
                            transRespDetails.setDescription(description);
                            transRespDetails.setRemark(description);
                            transRespDetails.setTransactionId(transactionId);
                            transRespDetails.setEci(eci);
                            //MerchantDAO merchantDAO = new MerchantDAO();
                            MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(toId);

                            if (merchantDetailsVO != null)
                            {
                                isService = merchantDetailsVO.getIsService();
                            }

                            transactionLogger.debug("dbStatus------" + dbStatus);
                            if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus))
                            {
                                if (transRespDetails != null)
                                {
                                    transactionLogger.debug("---inside transRespDetails---" + transRespDetails);
                                    transactionStatus = transRespDetails.getStatus();
                                    transactionId = transRespDetails.getTransactionId();
                                    message = transRespDetails.getDescription();
                                    eci = transRespDetails.getEci();
                                    errorName = transRespDetails.getErrorName();

                                    if (!functions.isValueNull(transRespDetails.getCurrency()))
                                        transRespDetails.setCurrency(respCurrency);
                                    if (!functions.isValueNull(transRespDetails.getTmpl_Amount()))
                                        transRespDetails.setTmpl_Amount(tmpl_amt);
                                    if (!functions.isValueNull(transRespDetails.getTmpl_Currency()))
                                        transRespDetails.setTmpl_Currency(tmpl_currency);
                                    if (functions.isValueNull(transRespDetails.getAmount()))
                                        respAmount = transRespDetails.getAmount();

                                    transactionLogger.debug("transactionStatus-----" + transactionStatus);
                                    transactionLogger.debug("respAmount-----" + respAmount);
                                    StringBuffer dbBuffer = new StringBuffer();
                                    if ("success".equals(transactionStatus))
                                    {
                                        status = "success";
                                        confirmStatus = "Y";
                                        responseStatus = "Successful";
                                        billingDesc = gatewayAccount.getDisplayName();
                                        if ("Y".equalsIgnoreCase(isService) && (!functions.isValueNull(transactionDetailsVO.getTransactionType()) || "DB".equalsIgnoreCase(transactionDetailsVO.getTransactionType())))
                                        {
                                            //transRespDetails.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                                            dbBuffer.append("update transaction_common set captureamount='" + respAmount + "',paymentid='" + transactionId + "',status='capturesuccess' ,eci='" + eci + "'"  + ",successtimestamp='" + functions.getTimestamp() + "'");
                                            paymentProcess.actionEntry(trackingId, respAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                                            updatedStatus="capturesuccess";
                                        }
                                        else
                                        {
                                            //  transRespDetails.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                                            dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful' ,eci='" + eci + "'" + ",successtimestamp='" + functions.getTimestamp() + "'");
                                            paymentProcess.actionEntry(trackingId, respAmount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authsuccessful");
                                            updatedStatus="authsuccessful";
                                        }
                                    }
                                    else
                                    {
                                        confirmStatus = "N";
                                        status = "fail";
                                        if (!functions.isValueNull(message))
                                        {
                                            message = "fail";
                                        }
                                        responseStatus = "Failed(" + message + ")";
                                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',eci='" + eci + "'" + ",failuretimestamp='" + functions.getTimestamp() + "'");
                                        actionEntry.actionEntryForCommon(trackingId, respAmount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                                        updatedStatus="authfailed";
                                    }
                                    dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                                    con = Database.getConnection();
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    transactionLogger.debug("-----dbBuffer-----" + dbBuffer);
                                    /*AsynchronousMailService asynchronousMailService = AsynchronousMailService.getInstance();
                                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                                    AsynchronousSmsService smsService = AsynchronousSmsService.getInstance();
                                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                                    */
                                }

                            }
                        }
                    }
                    else if (fromType.equalsIgnoreCase("DusPay") && cardTypeName.equalsIgnoreCase("eCheck"))
                    {
                        String transaction_id="";
                        String order_id="";
                        String price="";
                        String currency="";
                        String respStatus="";
                        String status_nm="";
                        JSONObject jsonObject = new JSONObject(responseMsg.toString());
                        if (jsonObject != null)
                        {
                            if (jsonObject.has("status_nm"))
                            {
                                status_nm = jsonObject.getString("status_nm");
                                transactionLogger.error("---status_nm---" + status_nm);
                            }
                            if (jsonObject.has("transaction_id"))
                            {
                                transaction_id = jsonObject.getString("transaction_id");
                                transactionLogger.error("---transaction_id---" + transaction_id);
                            }
                            if (jsonObject.has("order_id"))
                            {
                                order_id = jsonObject.getString("order_id");
                                transactionLogger.error("---order_id---" + order_id);
                            }
                            if (jsonObject.has("price"))
                            {
                                price = jsonObject.getString("price");
                                transactionLogger.error("---price---" + price);
                            }
                            if (jsonObject.has("currency"))
                            {
                                currency = jsonObject.getString("currency");
                                transactionLogger.error("---currency---" + currency);
                            }
                            if (jsonObject.has("status"))
                            {
                                respStatus = jsonObject.getString("status");
                                transactionLogger.error("---respStatus---" + respStatus);
                            }
                            //  final List<String> respValues= Arrays.asList("Cancelled","Refunded","Returned");
                            //  final List<String> respStatusnm= Arrays.asList("2","3","6");
                            transRespDetails = new Comm3DResponseVO();
                            String action="";
                            if(status_nm.equals("3") || status_nm.equals("6")  || "Returned".equalsIgnoreCase(respStatus) || "Refunded".equalsIgnoreCase(respStatus))
                            {
                                transRespDetails.setStatus("failed");
                                updatedStatus=PZTransactionStatus.CAPTURE_FAILED.toString();
                                message="SYS: Transaction Failed";
                                action=ActionEntry.ACTION_CAPTURE_FAILED.toString();
                            }
                            else if(status_nm.equals("2") || respStatus.equalsIgnoreCase("Cancelled"))
                            {
                                transRespDetails.setStatus("failed");
                                updatedStatus=PZTransactionStatus.AUTH_CANCELLED.toString();
                                message="SYS: Transaction Failed";
                                action=ActionEntry.ACTION_AUTHORISTION_CANCLLED.toString();
                            }
                            else if((respStatus.equalsIgnoreCase("Completed") || respStatus.equalsIgnoreCase("Pending") || status_nm.equals("1") || status_nm.equals("0")) && (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus)))
                            {
                                transRespDetails.setStatus("success");
                                updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                message="SYS: Transaction Successful";
                                action=ActionEntry.ACTION_CAPTURE_SUCCESSFUL.toString();
                            }

                           /* if((respStatus.equalsIgnoreCase("Cancelled") || respStatus.equalsIgnoreCase("Refunded") || respStatus.equalsIgnoreCase("Returned") || status_nm.equals("2") || status_nm.equals("3") || status_nm.equals("6")) && (dbStatus.equalsIgnoreCase(PZTransactionStatus.AUTH_STARTED.toString()) || dbStatus.equalsIgnoreCase(PZTransactionStatus.CAPTURE_SUCCESS.toString())))
                            {
                                transRespDetails.setStatus("failed");
                                updatedStatus=PZTransactionStatus.CAPTURE_FAILED.toString();
                                message="SYS: Transaction Failed";
                                action=ActionEntry.ACTION_CAPTURE_FAILED.toString();
                            }*/

                            transRespDetails.setRemark(respStatus);
                            transRespDetails.setDescription(responseStatus);
                            transRespDetails.setTransactionId(transaction_id);
                            transRespDetails.setAmount(price);
                            transRespDetails.setCurrency(currency);

                            String query="update transaction_common set status='"+updatedStatus+"',paymentid='" + transactionId +"',remark='"+respStatus+"',amount='"+price+"' where trackingid='"+trackingId+"'";
                            transactionLogger.error("-----query-----" + query);
                            con = Database.getConnection();
                            Database.executeUpdate(query.toString(), con);

                            actionEntry.actionEntryForCommon(trackingId, price, action, updatedStatus, transRespDetails, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);

                        }

                    }
                    else if (fromType.equalsIgnoreCase("Tojika"))
                    {
                        transactionLogger.error("Inside Tojika-----------------");

                        String amount="",application_id="",consumermessage="",currency="",istest="",method="",tp_transaction_id="",transaction_id="",transaction_time="",user_id="",jwt="";

                        if (functions.isValueNull(request.getParameter("amount")))
                        {
                            amount=request.getParameter("amount");
                        }
                        if (functions.isValueNull(request.getParameter("application_id")))
                        {
                            application_id = request.getParameter("application_id");
                        }
                        if (functions.isValueNull(request.getParameter("currency")))
                        {
                            currency = request.getParameter("currency");
                        }
                        if (functions.isValueNull(request.getParameter("consumermessage")))
                        {
                            consumermessage = request.getParameter("consumermessage");
                        }
                        if (functions.isValueNull(request.getParameter("description")))
                        {
                            description = request.getParameter("description");
                        }
                        if (functions.isValueNull(request.getParameter("istest")))
                        {
                            istest = request.getParameter("istest");
                        }
                        if (functions.isValueNull(request.getParameter("method")))
                        {
                            method = request.getParameter("method");
                        }
                        if (functions.isValueNull(request.getParameter("transaction_id")))
                        {
                            transaction_id = request.getParameter("transaction_id");
                        }
                        if (functions.isValueNull(request.getParameter("tp_transaction_id")))
                        {
                            tp_transaction_id = request.getParameter("tp_transaction_id");
                        }
                        if (functions.isValueNull(request.getParameter("transaction_time")))
                        {
                            transaction_time = request.getParameter("transaction_time");
                        }
                        if (functions.isValueNull(request.getParameter("user_id")))
                        {
                            user_id = request.getParameter("user_id");
                        }
                        if (functions.isValueNull(request.getParameter("status")))
                        {
                            status = request.getParameter("status");
                        }
                        if (functions.isValueNull(request.getParameter("jwt")))
                        {
                            jwt = request.getParameter("jwt");
                        }

                        transactionLogger.debug("amount"+amount);
                        transactionLogger.debug("application_id"+application_id);
                        transactionLogger.debug("currency"+currency);
                        transactionLogger.debug("consumermessage"+consumermessage);
                        transactionLogger.debug("description"+description);
                        transactionLogger.debug("istest"+istest);
                        transactionLogger.debug("method"+method);
                        transactionLogger.debug("transaction_id"+transaction_id);
                        transactionLogger.debug("tp_transaction_id"+tp_transaction_id);
                        transactionLogger.debug("transaction_time"+transaction_time);
                        transactionLogger.debug("user_id"+user_id);
                        transactionLogger.error("status" + status);
                        transactionLogger.debug("jwt"+jwt);

                        if(functions.isValueNull(jwt))
                        {
                            TojikaUtils tojikaUtils= new TojikaUtils();
                            boolean verifyTokem=tojikaUtils.verifyToken(jwt,secretKey);
                            transactionLogger.debug("verifyTokem in CommonBackEndServlet------"+verifyTokem);
                        }
                        transRespDetails = new Comm3DResponseVO();
                        String action="";
                        if (status.equalsIgnoreCase("SUCCESS"))
                        {
                            transactionLogger.error("inside Success-----------------");
                            transRespDetails.setStatus("success");
                            transRespDetails.setDescriptor(gatewayAccount.getDisplayName());
                            updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                            message="SYS: Transaction Successful";
                            action=ActionEntry.ACTION_CAPTURE_SUCCESSFUL.toString();
                        }
                        else
                        {

                            transactionLogger.error("inside failed -----------------");
                            transRespDetails.setStatus("failed");
                            updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                            message="SYS: Transaction Failed";
                            action=ActionEntry.ACTION_AUTHORISTION_FAILED.toString();
                        }
                        if(!functions.isValueNull(amount))
                        {
                            amount=transactionDetailsVO.getAmount();
                        }
                       /* if(functions.isValueNull(consumermessage) && !consumermessage.contains("na"))
                        {
                            message=consumermessage;
                        }*/
                        transRespDetails.setAmount(amount);
                        transRespDetails.setCurrency(currency);
                        transRespDetails.setDescription(description);
                        transRespDetails.setTransactionId(tp_transaction_id);
                        transRespDetails.setResponseTime(transaction_time);
                        transRespDetails.setRemark(description);

                        String query="update transaction_common set status='"+updatedStatus+"',paymentid='" + tp_transaction_id +"',remark='"+message+"',amount='"+amount+"' where trackingid='"+trackingId+"'";
                        transactionLogger.error("-----query-----" + query);
                        con = Database.getConnection();
                        Database.executeUpdate(query.toString(), con);

                        actionEntry.actionEntryForCommon(trackingId, amount, action, updatedStatus, transRespDetails, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }

                    else if(fromType.equalsIgnoreCase("Flutter"))
                    {
                        transactionLogger.error("Inside Flutter ---");

                        String id="",txRef="",flwRef="",createdAt="",amount="",currency="",eventtype="";

                        JSONObject jsonObject = new JSONObject(responseMsg.toString());
                        if (jsonObject != null)
                        {
                            if (jsonObject.has("id"))
                            {
                                id = jsonObject.getString("id");
                                transactionLogger.debug("id ---"+id);
                            }
                            if (jsonObject.has("txRef"))
                            {
                                txRef = jsonObject.getString("txRef");
                                transactionLogger.debug("txRef ---"+txRef);
                            }
                            if (jsonObject.has("flwRef"))
                            {
                                flwRef = jsonObject.getString("flwRef");
                                transactionLogger.debug("flwRef ---"+flwRef);
                            }
                            if (jsonObject.has("createdAt"))
                            {
                                createdAt = jsonObject.getString("createdAt");
                                transactionLogger.debug("createdAt ---"+createdAt);
                            }
                            if (jsonObject.has("amount"))
                            {
                                amount = jsonObject.getString("amount");
                                transactionLogger.debug("amount ---"+amount);
                            }
                            if (jsonObject.has("status"))
                            {
                                status = jsonObject.getString("status");
                                transactionLogger.debug("status ---"+status);
                            }
                            if (jsonObject.has("currency"))
                            {
                                currency = jsonObject.getString("currency");
                                transactionLogger.debug("currency ---"+currency);
                            }
                            if (jsonObject.has("event.type"))
                            {
                                eventtype = jsonObject.getString("event.type");
                                transactionLogger.debug("event.type ---"+eventtype);
                            }
                            transRespDetails = new Comm3DResponseVO();
                            if ("successful".equalsIgnoreCase(status))
                            {
                                transRespDetails.setStatus("success");
                                transRespDetails.setDescriptor(gatewayAccount.getDisplayName());
                            }
                            else
                            {
                                transRespDetails.setStatus("fail");
                            }

                            transRespDetails.setAmount(amount);
                            transRespDetails.setCurrency(currency);
                            transRespDetails.setDescription(status);
                            transRespDetails.setTransactionId(flwRef);
                            transRespDetails.setBankTransactionDate(createdAt);
                            // transRespDetails.setTmpl_Amount(tmpl_amt);
                            // transRespDetails.setTmpl_Currency(tmpl_currency);
                            // transRespDetails.setErrorCode(errorCode);
                            transRespDetails.setRemark(status);
                            // transRespDetails.setEci(eci);

                            transactionLogger.debug("dbStatus------" + dbStatus);

                            if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus))
                            {
                                if (transRespDetails != null)
                                {
                                    transactionLogger.debug("---inside transRespDetails Flutter---" + transRespDetails);
                                    transactionStatus = transRespDetails.getStatus();
                                    transactionId = transRespDetails.getTransactionId();
                                    message = transRespDetails.getDescription();
                                    transactionLogger.debug("message --------"+message);
                                    // eci = transRespDetails.getEci();
                                    // errorName = transRespDetails.getErrorName();
                                    if (functions.isValueNull(transRespDetails.getAmount()))
                                        respAmount = transRespDetails.getAmount();

                                    transactionLogger.debug("transactionStatus-----" + transactionStatus);
                                    transactionLogger.debug("respAmount-----" + amount);
                                    StringBuffer dbBuffer = new StringBuffer();
                                    if ("success".equals(transactionStatus))
                                    {
                                        status = "success";
                                        confirmStatus = "Y";
                                        responseStatus = "Successful";
                                        billingDesc = gatewayAccount.getDisplayName();

                                        //transRespDetails.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                                        dbBuffer.append("update transaction_common set captureamount='" + respAmount + "',paymentid='" + transactionId + "',status='capturesuccess' ,eci='" + eci + "'" + ",successtimestamp='" + functions.getTimestamp() + "'");
                                        paymentProcess.actionEntry(trackingId, respAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                                        updatedStatus="capturesuccess";
                                    }
                                    else
                                    {
                                        confirmStatus = "N";
                                        status = "fail";
                                        if (!functions.isValueNull(message))
                                        {
                                            message = "fail";
                                        }
                                        responseStatus = "Failed(" + message + ")";
                                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',eci='" + eci + "'" + ",failuretimestamp='" + functions.getTimestamp() + "'");
                                        actionEntry.actionEntryForCommon(trackingId, respAmount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                                        updatedStatus="authfailed";
                                    }
                                    dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                                    con = Database.getConnection();
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    transactionLogger.debug("-----dbBuffer-----" + dbBuffer);

                                    /*AsynchronousMailService asynchronousMailService = AsynchronousMailService.getInstance();
                                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                                    AsynchronousSmsService smsService = AsynchronousSmsService.getInstance();
                                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);*/

                                }
                            }
                        }
                    }
                    //
                    else if(fromType.equalsIgnoreCase("bitcoinpg"))
                    {
                        BitcoinPaygateResponseVO bitcoinPaygateResponseVO   = new BitcoinPaygateResponseVO();
                        transactionLogger.error("Inside GageWay Of  ---> " + trackingId + " " +fromType);

                        String payout_btcAmountNew  ="",amountBtc="",amountBtcNew="",paidBtcNew="",remainingToPayBtcNew="",paid="",payout_fiatAmountNew="",paidBtc="",remainingToPay="",remainingToPayBtc="",currency="",sale_currency="",exchange_rate="",payout_btcAmount="";
                        String payout_address       ="",payout_approvalTime="",notification_PaymentId="",payout_currency="",payoutId="",payout_status="",payout_fiatAmount="",sale_status="",sale_amount="";

                        JSONObject resJson      = new JSONObject(responseMsg.toString());
                        JSONObject jsonObject   = null;
                        transactionLogger.error("BackEnd Response ---> " + trackingId + " " + resJson.toString());
                        if(resJson.has("body")){
                            jsonObject  = resJson.getJSONObject("body");
                        }

                        if (jsonObject != null)
                        {
                            if (jsonObject.has("priceAmount"))
                            {
                                sale_amount = jsonObject.getString("priceAmount");
                                transactionLogger.debug("priceAmount ---"+sale_amount);
                            }
                            if (jsonObject.has("transferAmount"))
                            {
                                amountBtc = jsonObject.getString("transferAmount");
                                transactionLogger.debug("transferAmount ---"+amountBtc);
                            }
                            if (jsonObject.has("paidPriceAmount"))
                            {
                                paid = jsonObject.getString("paidPriceAmount");
                                transactionLogger.debug("paidPriceAmount ---"+paid);
                            }
                            if (jsonObject.has("paidTransferAmount"))
                            {
                                paidBtc = jsonObject.getString("paidTransferAmount");
                                transactionLogger.debug("paidTransferAmount ---"+paidBtc);
                            }
                            if (jsonObject.has("pendingPaidPriceAmount"))
                            {
                                remainingToPay = jsonObject.getString("pendingPaidPriceAmount");
                                transactionLogger.debug("pendingPaidPriceAmount ---"+remainingToPay);
                            }
                            if (jsonObject.has("pendingPaidTransferAmount"))
                            {
                                remainingToPayBtc = jsonObject.getString("pendingPaidTransferAmount");
                                transactionLogger.debug("pendingPaidTransferAmount ---"+remainingToPayBtc);
                            }
                            if (jsonObject.has("status"))
                            {
                                sale_status = jsonObject.getString("status");
                                transactionLogger.error("sale_status ---" + sale_status);
                            }
                            if (jsonObject.has("transactionId"))
                            {
                                transactionId = jsonObject.getString("transactionId");
                                transactionLogger.debug("transactionId ---"+transactionId);
                            }
                            if (jsonObject.has("paymentId"))
                            {
                                notification_PaymentId = jsonObject.getString("paymentId");
                                transactionLogger.debug("notification_PaymentId ---"+notification_PaymentId);
                            }
                            if (jsonObject.has("priceCurrency"))
                            {
                                sale_currency = jsonObject.getString("priceCurrency");
                                transactionLogger.debug("sale_currency ---"+sale_currency);
                            }
                            if (jsonObject.has("exchangeRate"))
                            {
                                exchange_rate = jsonObject.getString("exchangeRate");
                                transactionLogger.debug("exchange_rate ---"+exchange_rate);
                            }
                            if (jsonObject.has("address"))
                            {
                                payout_address = jsonObject.getString("address");
                                transactionLogger.debug("payout_address ---"+payout_address);
                            }
                            if (jsonObject.has("approvalTime"))
                            {
                                payout_approvalTime = jsonObject.getString("approvalTime");
                                transactionLogger.debug("payout_approvalTime ---"+payout_approvalTime);
                            }
                            if (jsonObject.has("priceCurrency"))
                            {
                                payout_currency = jsonObject.getString("priceCurrency");
                                transactionLogger.debug("payout_currency ---"+payout_currency);
                            }
                            if (jsonObject.has("payoutId"))
                            {
                                payoutId = jsonObject.getString("payoutId");
                                transactionLogger.debug("payoutId ---"+payoutId);
                            }
                            if (jsonObject.has("status"))
                            {
                                payout_status = jsonObject.getString("status");
                                transactionLogger.error("payout_status ---" + payout_status);
                            }
                            if (jsonObject.has("priceAmount"))
                            {
                                payout_fiatAmount = jsonObject.getString("priceAmount");
                                transactionLogger.debug("payout_fiatAmount ---"+payout_fiatAmount);
                            }
                            if (jsonObject.has("transferAmount")) // for payout
                            {
                                payout_btcAmount = jsonObject.getString("transferAmount");
                                transactionLogger.debug("payout_btcAmount ---"+payout_btcAmount);
                            }
                            if (functions.isValueNull(payout_btcAmount))
                            {
                                payout_btcAmountNew = new BigDecimal(payout_btcAmount).toPlainString();
                            }

                            transRespDetails = new Comm3DResponseVO();

                            if (PZTransactionStatus.PAYOUT_STARTED.toString().equalsIgnoreCase(dbStatus))
                            {
                                if ("SENT".equalsIgnoreCase(payout_status))
                                {
                                    transRespDetails.setStatus("success");
                                    transRespDetails.setDescriptor(gatewayAccount.getDisplayName());
                                }
                                else
                                {
                                    transRespDetails.setStatus("fail");
                                }
                                con = Database.getConnection();
                                transRespDetails.setAmount(payout_fiatAmount);
                                transRespDetails.setDescription(payout_status);
                                transRespDetails.setTransactionId(payoutId);
                                transRespDetails.setWalletAmount(payout_btcAmountNew);
                                transRespDetails.setWalletCurrecny("BTC");
                                transRespDetails.setCurrency(payout_currency);
                                StringBuffer dbBuffer = new StringBuffer();
                                if (payout_status.equalsIgnoreCase("SENT"))
                                {
                                    status              = "success";
                                    confirmStatus    = "Y";
                                    responseStatus  = "Successful";
                                    billingDesc     = gatewayAccount.getDisplayName();
                                    transRespDetails.setTransactionType(PZProcessType.PAYOUT.toString());
                                    dbBuffer.append("update transaction_common set payoutamount='" + payout_fiatAmount + "',paymentid='" + payoutId + "',walletAmount='"+payout_btcAmountNew+"',walletCurrency='BTC',status='payoutsuccessful',remark='" + payout_status+ "',payouttimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    transactionLogger.debug("-----dbBuffer-----" + dbBuffer);
                                    paymentProcess.actionEntry(trackingId, payout_fiatAmount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "payoutsuccessful");
                                    updatedStatus="payoutsuccessful";

                                }
                                else
                                {
                                    confirmStatus = "N";
                                    status = "fail";
                                    responseStatus = "Failed";
                                    dbBuffer.append("update transaction_common set status='payoutfailed',paymentid='" + payoutId + "',walletAmount='"+payout_btcAmountNew+"',walletCurrency='BTC',payoutamount='" + payout_fiatAmount + "',remark='" + payout_status + "' where trackingid = " + trackingId);
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    transactionLogger.debug("-----dbBuffer-----" + dbBuffer);
                                    actionEntry.actionEntryForCommon(trackingId, payout_fiatAmount, ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.ACTION_PAYOUT_FAILED, transRespDetails, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "payoutfailed");
                                    updatedStatus="payoutfailed";
                                }
                            }

                            else
                            {
                                if (functions.isValueNull(amountBtc))
                                    amountBtcNew = new BigDecimal(amountBtc).toPlainString();

                                if (functions.isValueNull(sale_amount))
                                    transRespDetails.setAmount(sale_amount);
                                if (functions.isValueNull(amountBtcNew))
                                    transRespDetails.setWalletAmount(amountBtcNew);

                                if (functions.isValueNull(sale_currency))
                                {
                                    transRespDetails.setCurrency(sale_currency);
                                    transRespDetails.setWalletCurrecny("BTC");
                                }
                                if (functions.isValueNull(sale_status))
                                    transRespDetails.setDescription(sale_status);
                                if (functions.isValueNull(notification_PaymentId))
                                    transRespDetails.setTransactionId(notification_PaymentId);
                                    transactionLogger.error("notification_PaymentId ----"+notification_PaymentId);

                                if (functions.isValueNull(sale_status))
                                    transRespDetails.setRemark(sale_status);
                                if (functions.isValueNull(sale_currency))
                                    transRespDetails.setCurrency(sale_currency);
                                transRespDetails.setResponseHashInfo(payout_address);

                                if (transRespDetails != null)
                                {
                                    transactionLogger.debug("---inside transRespDetails Bitcoin---" + transRespDetails);
                                    transactionId = transRespDetails.getTransactionId();
                                    message = transRespDetails.getDescription();
                                    transactionLogger.debug("message --------"+message);
                                    con = Database.getConnection();
                                    StringBuffer dbBuffer = new StringBuffer();
                                    if ("CONFIRMED".equals(sale_status))
                                    {
                                        transactionLogger.error("Inside CONFIRMED");
                                        status = "success";
                                        confirmStatus = "Y";
                                        responseStatus = "Successful";
                                        billingDesc = gatewayAccount.getDisplayName();
                                        transRespDetails.setDescriptor(billingDesc);
                                        transRespDetails.setStatus(status);
                                        transRespDetails.setTransactionType(PZProcessType.SALE.toString());
                                        dbBuffer.append("update transaction_common set captureamount='" + paid + "',paymentid='" + transactionId + "',walletAmount='"+amountBtcNew+"',walletCurrency='BTC',status='capturesuccess' ,eci='" + eci + "',remark='" + sale_status + "',successtimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                                        Database.executeUpdate(dbBuffer.toString(), con);
                                        transactionLogger.debug("-----dbBuffer-for Bitcoin----" + dbBuffer);
                                        paymentProcess.actionEntry(trackingId, sale_amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                                        updatedStatus="capturesuccess";
                                    }
                                    else if ("UNDERPAID".equalsIgnoreCase(sale_status) || "INVALID".equalsIgnoreCase(sale_status))
                                    {
                                        transactionLogger.error("inside UNDERPAID or INVALID");
                                        status = "success";
                                        confirmStatus = "Y";
                                        responseStatus = "Successful";
                                        billingDesc = gatewayAccount.getDisplayName();
                                        transRespDetails.setDescriptor(billingDesc);
                                        transRespDetails.setStatus(status);
                                        transRespDetails.setTransactionType(PZProcessType.SALE.toString());
                                        if ("UNDERPAID".equalsIgnoreCase(sale_status))
                                            transRespDetails.setDescription("remaining to pay: "+remainingToPay);
                                        if("INVALID".equalsIgnoreCase(sale_status))
                                            transRespDetails.setDescription("overpaid amount: "+remainingToPay.substring(1)); // substring used to remove "-" sign
                                        dbBuffer.append("update transaction_common set captureamount='" + paid + "',amount='"+sale_amount+"',paymentid='" + transactionId + "',walletAmount='"+amountBtcNew+"',walletCurrency='BTC',status='capturesuccess' ,eci='" + eci + "',remark='" + sale_status + "',successtimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                                        Database.executeUpdate(dbBuffer.toString(), con);
                                        transactionLogger.debug("-----dbBuffer-for Bitcoin----" + dbBuffer);
                                        paymentProcess.actionEntry(trackingId, sale_amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                                        updatedStatus="capturesuccess";

                                    }
                                    else if ("UNCONFIRMED".equalsIgnoreCase(sale_status))
                                    {
                                        transactionLogger.error("inside UNCONFIRMED");
                                        status="pending";
                                        transRespDetails.setStatus(status);
                                        dbBuffer.append("update transaction_common set amount='" + sale_amount +"',paymentid='" + transactionId + "',walletAmount='"+amountBtcNew+"',walletCurrency='BTC',eci='" + eci + "',remark='" + sale_status + "' where trackingid = " + trackingId);
                                        Database.executeUpdate(dbBuffer.toString(), con);
                                        transactionLogger.debug("-----dbBuffer-for Bitcoin----" + dbBuffer);
                                        // paymentProcess.actionEntry(trackingId, respAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                        // statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                                        updatedStatus=dbStatus;
                                        transactionLogger.error("Query.....!!!" + dbBuffer);
                                    }
                                    else
                                    {
                                        transactionLogger.error("no status from response");
                                        confirmStatus = "N";
                                        status = "fail";
                                        responseStatus = "Failed";
                                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',walletAmount='"+amountBtcNew+"',walletCurrency='BTC',amount='" + sale_amount + "',remark='" + sale_status + "',failuretimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                                        Database.executeUpdate(dbBuffer.toString(), con);
                                        transactionLogger.debug("-----dbBuffer-for Bitcoin----" + dbBuffer);
                                        actionEntry.actionEntryForCommon(trackingId, sale_amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                                        updatedStatus="authfailed";
                                    }
                                }
                            }

                            if (functions.isValueNull(amountBtc))
                            {
                                amountBtcNew = new BigDecimal(amountBtc).toPlainString(); // Converung Scientif Notion amount to String
                                transactionLogger.error("amountBtcNew --"+amountBtcNew);
                            }
                            if (functions.isValueNull(paidBtc))
                            {
                                paidBtcNew = new BigDecimal(paidBtc).toPlainString();
                                transactionLogger.error("paidBtcNew --"+paidBtcNew);
                            }
                            if (functions.isValueNull(remainingToPayBtc))
                            {
                                remainingToPayBtcNew = new BigDecimal(remainingToPayBtc).toPlainString();
                                transactionLogger.error("remainingToPayBtcNew --"+remainingToPayBtcNew);
                            }
                            if (functions.isValueNull(payout_fiatAmount)) // For Payout
                            {
                                 payout_fiatAmountNew = new BigDecimal(payout_fiatAmount).toPlainString();
                                transactionLogger.error("payout_fiatAmountNew --" + payout_fiatAmountNew);
                            }

                            bitcoinPaygateResponseVO.setAmount(sale_amount);
                            if (payout_status.equalsIgnoreCase("SENT"))
                            {
                                bitcoinPaygateResponseVO.setAmount(payout_fiatAmountNew);
                                bitcoinPaygateResponseVO.setFiat_amount(payout_fiatAmountNew);
                            }
                            if (functions.isValueNull(amountBtcNew))
                                bitcoinPaygateResponseVO.setAmount_btc(amountBtcNew);
                            if (payout_status.equalsIgnoreCase("SENT"))
                                bitcoinPaygateResponseVO.setAmount_btc(payout_btcAmountNew);
                            if (functions.isValueNull(sale_currency))
                                bitcoinPaygateResponseVO.setCurrency(sale_currency);
                            bitcoinPaygateResponseVO.setCurrency_btc("BTC");
                            if (functions.isValueNull(exchange_rate))
                                bitcoinPaygateResponseVO.setExchange_rate(exchange_rate);
                            if (functions.isValueNull(paid))
                                bitcoinPaygateResponseVO.setPaid(paid);
                            if(functions.isValueNull(paidBtcNew))
                                bitcoinPaygateResponseVO.setPaid_btc(paidBtcNew);
                            if (functions.isValueNull(transactionId))
                                bitcoinPaygateResponseVO.setPayment_id(transactionId);
                            if (functions.isValueNull(remainingToPay))
                            bitcoinPaygateResponseVO.setRemaining_to_pay(remainingToPay);
                            if (functions.isValueNull(remainingToPayBtcNew))
                                bitcoinPaygateResponseVO.setRemaining_to_pay_btc(remainingToPayBtcNew);
                            bitcoinPaygateResponseVO.setResp_status(sale_status);
                           // if (functions.isValueNull(payout_fiatAmountNew))
                              //  bitcoinPaygateResponseVO.setFiat_amount(payout_fiatAmountNew);
                            if (functions.isValueNull(payoutId))
                                bitcoinPaygateResponseVO.setPayoutid(payoutId);
                            BitcoinPaygateUtils.insertDetailTableEntry(trackingId,bitcoinPaygateResponseVO);
                            transactionLogger.debug("After insertDetailTableEntry");

                            /*AsynchronousMailService asynchronousMailService = AsynchronousMailService.getInstance();
                            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                            AsynchronousSmsService smsService = AsynchronousSmsService.getInstance();
                            smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);*/

                        }
                    }else if(fromType.equalsIgnoreCase("bitclear")){
                        transactionLogger.error("Inside GageWay Of  ---> " + trackingId + " " +fromType);
                        String opt = "";
                        if(request.getParameter("opt") != null){
                            opt = request.getParameter("opt");
                        }
                        BitcoinPaygateResponseVO bitcoinPaygateResponseVO   = new BitcoinPaygateResponseVO();

                        String payout_btcAmountNew  ="",amountBtc="",amountBtcNew="",paidBtcNew="",remainingToPayBtcNew="",paid="",payout_fiatAmountNew="",paidBtc="",remainingToPay="",remainingToPayBtc="",currency="",sale_currency="",exchange_rate="",payout_btcAmount="";
                        String payout_address       ="",payout_approvalTime="",notification_PaymentId="",payout_currency="",payoutId="",payout_status="",payout_fiatAmount="",sale_status="",sale_amount="";

                        JSONObject resJson      = new JSONObject(responseMsg.toString());
                        JSONObject jsonObject   = null;

                        transactionLogger.error("Response ---> " + trackingId + " "+opt+" " + resJson.toString());

                        if(resJson.has("body")){
                            jsonObject  = resJson.getJSONObject("body");
                        }

                        if (jsonObject != null)
                        {
                            if (jsonObject.has("priceAmount"))
                            {
                                sale_amount = jsonObject.getString("priceAmount");
                                transactionLogger.debug("priceAmount ---"+sale_amount);
                            }
                            if (jsonObject.has("transferAmount"))
                            {
                                amountBtc = jsonObject.getString("transferAmount");
                                transactionLogger.debug("transferAmount ---"+amountBtc);
                            }
                            if (jsonObject.has("paidPriceAmount"))
                            {
                                paid = jsonObject.getString("paidPriceAmount");
                                transactionLogger.debug("paidPriceAmount ---"+paid);
                            }
                            if (jsonObject.has("paidTransferAmount"))
                            {
                                paidBtc = jsonObject.getString("paidTransferAmount");
                                transactionLogger.debug("paidTransferAmount ---"+paidBtc);
                            }
                            if (jsonObject.has("pendingPaidPriceAmount"))
                            {
                                remainingToPay = jsonObject.getString("pendingPaidPriceAmount");
                                transactionLogger.debug("pendingPaidPriceAmount ---"+remainingToPay);
                            }
                            if (jsonObject.has("pendingPaidTransferAmount"))
                            {
                                remainingToPayBtc = jsonObject.getString("pendingPaidTransferAmount");
                                transactionLogger.debug("pendingPaidTransferAmount ---"+remainingToPayBtc);
                            }
                            if (jsonObject.has("status"))
                            {
                                sale_status = jsonObject.getString("status");
                                transactionLogger.error("sale_status ---" + sale_status);
                            }
                            if (jsonObject.has("transactions") && jsonObject.getJSONArray("transactions") != null)
                            {
                                JSONArray jsonArray = jsonObject.getJSONArray("transactions");
                                if(jsonArray !=null && jsonArray.length() > 0){
                                    transactionId       = jsonArray.getJSONObject(0).getString("transactionId");
                                }
                                transactionLogger.debug("transactionId ---"+transactionId);
                            }
                            if (jsonObject.has("paymentId"))
                            {
                                notification_PaymentId = jsonObject.getString("paymentId");
                                transactionLogger.debug("notification_PaymentId ---"+notification_PaymentId);
                            }
                            if (jsonObject.has("priceCurrency"))
                            {
                                sale_currency = jsonObject.getString("priceCurrency");
                                transactionLogger.debug("sale_currency ---"+sale_currency);
                            }
                            if (jsonObject.has("exchangeRate"))
                            {
                                exchange_rate = jsonObject.getString("exchangeRate");
                                transactionLogger.debug("exchange_rate ---"+exchange_rate);
                            }
                            if (jsonObject.has("address"))
                            {
                                payout_address = jsonObject.getString("address");
                                transactionLogger.debug("payout_address ---"+payout_address);
                            }
                            if (jsonObject.has("approvalTime"))
                            {
                                payout_approvalTime = jsonObject.getString("approvalTime");
                                transactionLogger.debug("payout_approvalTime ---"+payout_approvalTime);
                            }
                            if (jsonObject.has("priceCurrency"))
                            {
                                payout_currency = jsonObject.getString("priceCurrency");
                                transactionLogger.debug("payout_currency ---"+payout_currency);
                            }
                            if (jsonObject.has("payoutId"))
                            {
                                payoutId = jsonObject.getString("payoutId");
                                transactionLogger.debug("payoutId ---"+payoutId);
                            }
                            if (jsonObject.has("status"))
                            {
                                payout_status = jsonObject.getString("status");
                                transactionLogger.error("payout_status ---" + payout_status);
                            }
                            if (jsonObject.has("priceAmount"))
                            {
                                payout_fiatAmount = jsonObject.getString("priceAmount");
                                transactionLogger.debug("payout_fiatAmount ---"+payout_fiatAmount);
                            }
                            if (jsonObject.has("transferAmount")) // for payout
                            {
                                payout_btcAmount = jsonObject.getString("transferAmount");
                                transactionLogger.debug("payout_btcAmount ---"+payout_btcAmount);
                            }
                            if (functions.isValueNull(payout_btcAmount))
                            {
                                payout_btcAmountNew = new BigDecimal(payout_btcAmount).toPlainString();
                            }

                            transRespDetails = new Comm3DResponseVO();


                            if(PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                            {
                                if (functions.isValueNull(amountBtc)){
                                    amountBtcNew = new BigDecimal(amountBtc).toPlainString();
                                }

                                if (functions.isValueNull(sale_amount)){
                                    transRespDetails.setAmount(sale_amount);
                                }

                                if (functions.isValueNull(amountBtcNew)){
                                    transRespDetails.setWalletAmount(amountBtcNew);
                                }

                                if (functions.isValueNull(sale_currency))
                                {
                                    transRespDetails.setCurrency(sale_currency);
                                    transRespDetails.setWalletCurrecny("BTC");
                                }

                                if (functions.isValueNull(sale_status)){
                                    transRespDetails.setDescription(sale_status);
                                }
                                if (functions.isValueNull(transactionId)){
                                    transRespDetails.setTransactionId(transactionId);
                                }
                                transactionLogger.error("notification_PaymentId ----"+notification_PaymentId);

                                if (functions.isValueNull(sale_status)){
                                    transRespDetails.setRemark(sale_status);
                                }
                                if (functions.isValueNull(sale_currency)){
                                    transRespDetails.setCurrency(sale_currency);
                                }
                                if (functions.isValueNull(payout_address)){
                                    transRespDetails.setResponseHashInfo(payout_address);
                                }

                                if(functions.isValueNull(transactionDetailsVO.getTemplatecurrency())){
                                    transRespDetails.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());
                                }else{
                                    transRespDetails.setTmpl_Currency(transactionDetailsVO.getCurrency());
                                }

                                if(functions.isValueNull(transactionDetailsVO.getTemplateamount())){
                                    transRespDetails.setTmpl_Amount(transactionDetailsVO.getTemplateamount());
                                }else{
                                    transRespDetails.setTmpl_Amount(transactionDetailsVO.getAmount());
                                }

                                if (transRespDetails != null)
                                {
                                    transactionLogger.debug("---inside transRespDetails Bitcoin---" + transRespDetails);
                                    //transactionId   = transRespDetails.getTransactionId();
                                    message         = transRespDetails.getDescription();
                                    transactionLogger.debug("message --------"+message);
                                    con                     = Database.getConnection();
                                    StringBuffer dbBuffer   = new StringBuffer();

                                    if ("CONFIRMED".equalsIgnoreCase(sale_status))
                                    {
                                        transactionLogger.error("Inside CONFIRMED");
                                        status          = "success";
                                        confirmStatus   = "Y";
                                        responseStatus  = "Successful";
                                        billingDesc     = gatewayAccount.getDisplayName();
                                        notificationUrl     = "";
                                        notificationUrl     = transactionDetailsVO.getNotificationUrl();

                                        transRespDetails.setDescriptor(billingDesc);
                                        transRespDetails.setStatus(status);
                                        transRespDetails.setTransactionType(PZProcessType.SALE.toString());

                                        dbBuffer.append("update transaction_common set captureamount='" + paid + "',paymentid='" + notification_PaymentId + "',walletAmount='"+amountBtcNew+"',walletCurrency='BTC',status='capturesuccess' ,eci='" + eci + "',remark='" + sale_status + "',successtimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                                        Database.executeUpdate(dbBuffer.toString(), con);
                                        transactionLogger.debug("-----dbBuffer-for Bitcoin----" + dbBuffer);

                                        paymentProcess.actionEntry(trackingId, sale_amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");

                                        updatedStatus   =  "capturesuccess";
                                    }
                                    else if ("UNDERPAID".equalsIgnoreCase(sale_status) || "INVALID".equalsIgnoreCase(sale_status))
                                    {
                                        transactionLogger.error("inside UNDERPAID or INVALID");
                                        status          = "success";
                                        confirmStatus   = "Y";
                                        responseStatus  = "Successful";
                                        billingDesc     = gatewayAccount.getDisplayName();
                                        notificationUrl     = "";
                                        notificationUrl     = transactionDetailsVO.getNotificationUrl();

                                        transRespDetails.setDescriptor(billingDesc);
                                        transRespDetails.setStatus(status);
                                        transRespDetails.setTransactionType(PZProcessType.SALE.toString());

                                        if ("UNDERPAID".equalsIgnoreCase(sale_status)){
                                            transRespDetails.setDescription("remaining to pay: "+remainingToPay);
                                        }
                                        if("INVALID".equalsIgnoreCase(sale_status))
                                        {
                                            transRespDetails.setDescription("overpaid amount: " + remainingToPay.substring(1)); // substring used to remove "-" sign
                                        }
                                        dbBuffer.append("update transaction_common set captureamount='" + paid + "',amount='"+sale_amount+"',paymentid='" + notification_PaymentId + "',walletAmount='"+amountBtcNew+"',walletCurrency='BTC',status='capturesuccess' ,eci='" + eci + "',remark='" + sale_status + "',successtimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                                        Database.executeUpdate(dbBuffer.toString(), con);
                                        transactionLogger.debug("-----dbBuffer-for Bitcoin----" + dbBuffer);

                                        paymentProcess.actionEntry(trackingId, sale_amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");

                                        updatedStatus="capturesuccess";

                                    }
                                    else if ("UNCONFIRMED".equalsIgnoreCase(sale_status))
                                    {
                                        transactionLogger.error("inside UNCONFIRMED");
                                        status      = "pending";
                                        transRespDetails.setStatus(status);
                                        dbBuffer.append("update transaction_common set amount='" + sale_amount +"',paymentid='" + notification_PaymentId + "',walletAmount='"+amountBtcNew+"',walletCurrency='BTC',eci='" + eci + "',remark='" + sale_status + "' where trackingid = " + trackingId);
                                        Database.executeUpdate(dbBuffer.toString(), con);
                                        transactionLogger.debug("-----dbBuffer-for Bitcoin----" + dbBuffer);
                                        // paymentProcess.actionEntry(trackingId, respAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                        // statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                                        updatedStatus   = dbStatus;
                                        transactionLogger.error("Query.....!!!" + dbBuffer);
                                    }
                                    else if("EXPIRED".equalsIgnoreCase(sale_status))
                                    {
                                        transactionLogger.error("no status from response");
                                        confirmStatus   = "N";
                                        status          = "fail";
                                        responseStatus  = "Failed";
                                        notificationUrl     = "";
                                        notificationUrl     = transactionDetailsVO.getNotificationUrl();

                                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + notification_PaymentId + "',walletAmount='"+amountBtcNew+"',walletCurrency='BTC',amount='" + sale_amount + "',remark='" + sale_status + "',failuretimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                                        Database.executeUpdate(dbBuffer.toString(), con);
                                        transactionLogger.debug("-----dbBuffer-for Bitcoin----" + dbBuffer);
                                        actionEntry.actionEntryForCommon(trackingId, sale_amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");

                                        updatedStatus   = "authfailed";
                                    }else
                                    {
                                        transactionLogger.error("no status from response");
                                        confirmStatus   = "P";
                                        status          = "pending";
                                        responseStatus  = "pending";
                                        updatedStatus   = "pending";
                                    }
                                }
                            }
                            else if (PZTransactionStatus.PAYOUT_STARTED.toString().equalsIgnoreCase(dbStatus))
                            {
                                if ("SENT".equalsIgnoreCase(payout_status))
                                {
                                    transRespDetails.setStatus("success");
                                    transRespDetails.setDescriptor(gatewayAccount.getDisplayName());
                                }
                                else if("EXPIRED".equalsIgnoreCase(payout_status) || "FAILED".equalsIgnoreCase(payout_status))
                                {
                                    transRespDetails.setStatus("fail");
                                }else{
                                    transRespDetails.setStatus("pending");
                                }

                                con = Database.getConnection();
                                transRespDetails.setAmount(payout_fiatAmount);
                                transRespDetails.setDescription(payout_status);
                                transRespDetails.setTransactionId(payoutId);
                                transRespDetails.setWalletAmount(payout_btcAmountNew);
                                transRespDetails.setWalletCurrecny("BTC");
                                transRespDetails.setCurrency(payout_currency);
                                StringBuffer dbBuffer = new StringBuffer();

                                if (payout_status.equalsIgnoreCase("SENT"))
                                {
                                    status          = "success";
                                    confirmStatus   = "Y";
                                    responseStatus  = "Successful";
                                    notificationUrl     = "";
                                    notificationUrl     = transactionDetailsVO.getNotificationUrl();

                                    billingDesc     = gatewayAccount.getDisplayName();
                                    transRespDetails.setTransactionType(PZProcessType.PAYOUT.toString());


                                    dbBuffer.append("update transaction_common set payoutamount='" + payout_fiatAmount + "',paymentid='" + payoutId + "',walletAmount='"+payout_btcAmountNew+"',walletCurrency='BTC',status='payoutsuccessful',remark='" + payout_status+ "',payouttimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    transactionLogger.debug("-----dbBuffer-----" + dbBuffer);

                                    paymentProcess.actionEntry(trackingId, payout_fiatAmount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "payoutsuccessful");

                                    updatedStatus   = "payoutsuccessful";

                                }
                                else if("EXPIRED".equalsIgnoreCase(payout_status) || "FAILED".equalsIgnoreCase(payout_status))
                                {
                                    confirmStatus   = "N";
                                    status          = "fail";
                                    responseStatus  = "Failed";
                                    notificationUrl     = "";
                                    notificationUrl     = transactionDetailsVO.getNotificationUrl();

                                    dbBuffer.append("update transaction_common set status='payoutfailed',paymentid='" + payoutId + "',walletAmount='"+payout_btcAmountNew+"',walletCurrency='BTC',payoutamount='" + payout_fiatAmount + "',remark='" + payout_status + "' where trackingid = " + trackingId);
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    transactionLogger.debug("-----dbBuffer-----" + dbBuffer);

                                    actionEntry.actionEntryForCommon(trackingId, payout_fiatAmount, ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.ACTION_PAYOUT_FAILED, transRespDetails, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "payoutfailed");

                                    updatedStatus="payoutfailed";
                                }else{
                                    status          = "pending";
                                    confirmStatus   = "P";
                                    responseStatus  = "pending";

                                }
                            }


                            if (functions.isValueNull(amountBtc))
                            {
                                amountBtcNew = new BigDecimal(amountBtc).toPlainString(); // Converung Scientif Notion amount to String
                                transactionLogger.error("amountBtcNew --"+amountBtcNew);
                            }
                            if (functions.isValueNull(paidBtc))
                            {
                                paidBtcNew = new BigDecimal(paidBtc).toPlainString();
                                transactionLogger.error("paidBtcNew --"+paidBtcNew);
                            }
                            if (functions.isValueNull(remainingToPayBtc))
                            {
                                remainingToPayBtcNew = new BigDecimal(remainingToPayBtc).toPlainString();
                                transactionLogger.error("remainingToPayBtcNew --"+remainingToPayBtcNew);
                            }
                            if (functions.isValueNull(payout_fiatAmount)) // For Payout
                            {
                                payout_fiatAmountNew = new BigDecimal(payout_fiatAmount).toPlainString();
                                transactionLogger.error("payout_fiatAmountNew --" + payout_fiatAmountNew);
                            }

                            bitcoinPaygateResponseVO.setAmount(sale_amount);
                            if (payout_status.equalsIgnoreCase("SENT"))
                            {
                                bitcoinPaygateResponseVO.setAmount(payout_fiatAmountNew);
                                bitcoinPaygateResponseVO.setFiat_amount(payout_fiatAmountNew);
                            }
                            if (functions.isValueNull(amountBtcNew))
                                bitcoinPaygateResponseVO.setAmount_btc(amountBtcNew);
                            if (payout_status.equalsIgnoreCase("SENT"))
                                bitcoinPaygateResponseVO.setAmount_btc(payout_btcAmountNew);
                            if (functions.isValueNull(sale_currency))
                                bitcoinPaygateResponseVO.setCurrency(sale_currency);
                            bitcoinPaygateResponseVO.setCurrency_btc("BTC");
                            if (functions.isValueNull(exchange_rate))
                                bitcoinPaygateResponseVO.setExchange_rate(exchange_rate);
                            if (functions.isValueNull(paid))
                                bitcoinPaygateResponseVO.setPaid(paid);
                            if(functions.isValueNull(paidBtcNew))
                                bitcoinPaygateResponseVO.setPaid_btc(paidBtcNew);
                            if (functions.isValueNull(transactionId))
                                bitcoinPaygateResponseVO.setPayment_id(transactionId);
                            if (functions.isValueNull(remainingToPay))
                                bitcoinPaygateResponseVO.setRemaining_to_pay(remainingToPay);
                            if (functions.isValueNull(remainingToPayBtcNew))
                                bitcoinPaygateResponseVO.setRemaining_to_pay_btc(remainingToPayBtcNew);
                            bitcoinPaygateResponseVO.setResp_status(sale_status);
                            // if (functions.isValueNull(payout_fiatAmountNew))
                            //  bitcoinPaygateResponseVO.setFiat_amount(payout_fiatAmountNew);
                            if (functions.isValueNull(payoutId)){
                                bitcoinPaygateResponseVO.setPayoutid(payoutId);
                            }

                            BitcoinPaygateUtils.insertDetailTableEntry(trackingId,bitcoinPaygateResponseVO);

                            /*transactionLogger.debug("After insertDetailTableEntry");
                            AsynchronousMailService asynchronousMailService = AsynchronousMailService.getInstance();
                            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                            AsynchronousSmsService smsService = AsynchronousSmsService.getInstance();
                            smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);*/
                        }
                    }

                    //
                    else if (fromType.equalsIgnoreCase("pswallet"))
                    {
                        transactionLogger.error("Inside Pay Send Wallet Back End-----------------");

                        String id="",reference_id="",order_id="",amount="",price="",currency="",req_status="",reason="";

                        if (functions.isValueNull(request.getParameter("id"))) {
                            id=request.getParameter("id");
                        }

                        if (functions.isValueNull(request.getParameter("reference_id"))) {
                            reference_id = request.getParameter("reference_id");
                        }

                        if (functions.isValueNull(request.getParameter("order_id"))) {
                            order_id=request.getParameter("order_id");
                        }

                        if (functions.isValueNull(request.getParameter("amount"))) {
                            amount = request.getParameter("amount");
                        }

                        if (functions.isValueNull(request.getParameter("price"))) {
                            price = request.getParameter("price");
                        }

                        if (functions.isValueNull(request.getParameter("currency"))) {
                            currency = request.getParameter("currency");
                        }

                        if (functions.isValueNull(request.getParameter("status"))) {
                            req_status = request.getParameter("status");
                        }

                        if (functions.isValueNull(request.getParameter("reason"))) {
                            reason = request.getParameter("reason");
                        }

                        transactionLogger.error("id = "+id);
                        transactionLogger.error("reference_id = " + reference_id);
                        transactionLogger.error("order_id = " + order_id);
                        transactionLogger.error("amount = " + amount);
                        transactionLogger.error("price = " + price);
                        transactionLogger.error("currency = " + currency);
                        transactionLogger.error("req_status = " + req_status);
                        transactionLogger.error("reason = " + reason);

/*
                        transRespDetails = new Comm3DResponseVO();
                        String action="";
                        if (status.equalsIgnoreCase("success"))
                        {
                            transactionLogger.error("inside PaySend Wallet Success-----------------");
                            transRespDetails.setStatus("success");
                            transRespDetails.setDescriptor(gatewayAccount.getDisplayName());
                            updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                            message="SYS: Transaction Successful";
                            action=ActionEntry.ACTION_CAPTURE_SUCCESSFUL.toString();
                        }
                        else
                        {
                            transactionLogger.error("inside PaySend Wallet Failed -----------------");
                            transRespDetails.setStatus("failed");
                            updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                            message="SYS: Transaction Failed";
                            action=ActionEntry.ACTION_AUTHORISTION_FAILED.toString();
                        }

                        transRespDetails.setAmount(amount);
                        transRespDetails.setCurrency(currency);
                        transRespDetails.setDescription(description);
                        transRespDetails.setTransactionId(order_id);
                        transRespDetails.setRemark(reason);

                        String query="update transaction_common set status='"+updatedStatus+"',paymentid='" + order_id +"',remark='"+message+"',amount='"+amount+"' where trackingid='"+trackingId+"'";
                        transactionLogger.error("-----query-----" + query);
                        con = Database.getConnection();
                        Database.executeUpdate(query.toString(), con);

                        actionEntry.actionEntryForCommon(trackingId, amount, action, updatedStatus, transRespDetails, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);*/
                    }
                    else if(fromType.equalsIgnoreCase("ninja"))
                    {
                        transactionLogger.error("Inside NinjaWalletBackEndServlet");

                        transactionLogger.debug("dbStatus------" + dbStatus);
                        transRespDetails= new Comm3DResponseVO();

                        String id="",  total="",  currency="",  reference="",  customer_email="", item_name="", item_description="",
                                status1="", reason="", descriptor1="", merchant_code="", created_at="";


                        JSONObject jsonObject1 = new JSONObject(responseMsg.toString());
                        if (jsonObject1!=null)
                        {
                            if (jsonObject1.has("data"))
                            {
                                JSONObject jsonObject = jsonObject1.getJSONObject("data");
                                if (jsonObject.has("id"))
                                {
                                    id = jsonObject.getString("id");
                                    transactionLogger.error("ID....." + id);
                                }
                                if (jsonObject.has("total"))
                                {
                                    total = jsonObject.getString("total");
                                    transactionLogger.error("Total....." + total);
                                }
                                if (jsonObject.has("currency"))
                                {
                                    currency = jsonObject.getString("currency");
                                    transactionLogger.error("Currency....." + currency);
                                }
                                if (jsonObject.has("reference"))
                                {
                                    reference = jsonObject.getString("reference");
                                    transactionLogger.error("Reference....." + reference);
                                }
                                if (jsonObject.has("customer_email"))
                                {
                                    customer_email = jsonObject.getString("customer_email");
                                    transactionLogger.error("Customer Email....." + customer_email);
                                }
                                if (jsonObject.has("item_name"))
                                {
                                    item_name = jsonObject.getString("item_name");
                                    transactionLogger.error("Item name....." + item_name);
                                }
                                if (jsonObject.has("item_description"))
                                {
                                    item_description = jsonObject.getString("item_description");
                                    transactionLogger.error("Item description....." + item_description);
                                }
                                if (jsonObject.has("status"))
                                {
                                    status1 = jsonObject.getString("status");
                                    transactionLogger.error("Status....." + status1);
                                }
                                if (jsonObject.has("reason"))
                                {
                                    reason = jsonObject.getString("reason");
                                    transactionLogger.error("Reason....." + reason);
                                }
                                if (jsonObject.has("descriptor"))
                                {
                                    descriptor1 = jsonObject.getString("descriptor");
                                    transactionLogger.error("Descriptor....." + descriptor1);
                                }
                                if (jsonObject.has("merchant_code"))
                                {
                                    merchant_code = jsonObject.getString("merchant_code");
                                    transactionLogger.error("Merchant Code....." + merchant_code);
                                }
                                if (jsonObject.has("created_at"))
                                {
                                    created_at = jsonObject.getString("created_at");
                                    transactionLogger.error("Created Time....." + created_at);
                                }

                                transRespDetails.setAmount(total);
                                transRespDetails.setTransactionId(id);
                                transRespDetails.setResponseTime(created_at);
                                transRespDetails.setCurrency(currency);

                                StringBuffer dbBuffer = new StringBuffer();

                                if ((PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus)) && status1.equalsIgnoreCase("COMPLETE"))
                                {
                                    //todo set status success and db status capturesuccess and update other details also
                                    transactionLogger.error("Inside DBStatus...."+dbStatus +"& status..."+status1);

                                    transRespDetails.setStatus("success");
                                    transRespDetails.setDescriptor(descriptor1);
                                    dbBuffer.append("update transaction_common set captureamount='" + total + "',paymentid='" + id + "',status='capturesuccess'" + ",successtimestamp='" + functions.getTimestamp() + "'");
                                    paymentProcess.actionEntry(trackingId, total, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                                    updatedStatus = "capturesuccess";
                                    transactionLogger.error("Query.....!!!" + dbBuffer);
                                }
                                else if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equalsIgnoreCase(dbStatus) && status1.equalsIgnoreCase("COMPLETE"))
                                {
                                    transactionLogger.error("Inside DBStatus...."+dbStatus +"& status..."+status1);
                                    dbBuffer.append("update transaction_common set paymentid='" + id + "'");
                                    updatedStatus="capturesuccess";
                                    transactionLogger.error("Query.....!!!" + dbBuffer);
                                }
                                else if((PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus)) && status1.equalsIgnoreCase("PENDING"))
                                {
                                    //todo set status pending
                                    transactionLogger.error("Inside DBStatus...."+dbStatus +"& status..."+status1);
                                    transRespDetails.setStatus("success");
                                    transRespDetails.setDescriptor(descriptor1);

                                    dbBuffer.append("update transaction_common set captureamount='" + total +"',paymentid='" + id + "',status='authstarted'");
                                    updatedStatus="authstarted";
                                    transactionLogger.error("Query.....!!!" + dbBuffer);
                                }
                                else if(PZTransactionStatus.REVERSED.toString().equalsIgnoreCase(dbStatus) ||PZTransactionStatus.MARKED_FOR_REVERSAL.toString().equalsIgnoreCase(dbStatus))
                                {
                                    transactionLogger.error("Inside DBStatus...." + dbStatus + "& status..." + status1);
                                    if (!status1.equalsIgnoreCase("REFUNDED"))
                                    {
                                        transRespDetails.setStatus("markedforreversal");
                                        dbBuffer.append("update transaction_common set status='markedforreversal',paymentid='" + id + "'");
                                        updatedStatus = "markedforreversal";
                                    }
                                    else
                                    {
                                        transRespDetails.setStatus("reversed");
                                        dbBuffer.append("update transaction_common set refundamount='" + total +"',status='reversed',paymentid='" + id + "'" + ",refundtimestamp='" + functions.getTimestamp() + "'");
                                        updatedStatus = "reversed";
                                    }
                                }
                                else
                                {
                                    //todo update status to fail
                                    transactionLogger.error("Inside DBStatus...."+dbStatus +"& status..."+status1);
                                    if (!functions.isValueNull(message))
                                    {
                                        message = "fail";
                                    }
                                    responseStatus = "Failed(" + message + ")";
                                    dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + id +"'" + ",failuretimestamp='" + functions.getTimestamp() + "'");
                                    actionEntry.actionEntryForCommon(trackingId, total, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                                    updatedStatus="authfailed";
                                    transactionLogger.error("Query.....!!!" + dbBuffer);
                                }
                                dbBuffer.append(" ,remark='" + updatedStatus + "' where trackingid = " + trackingId);
                                con = Database.getConnection();
                                Database.executeUpdate(dbBuffer.toString(), con);
                                transactionLogger.error("dbBuffer.....!!!" + dbBuffer);
                                transactionDetailsVO.setBillingDesc(descriptor1);
                            }
                        }
                    }
                    else if (fromType.equalsIgnoreCase("ONLINEPAY"))
                    {
                        JSONObject jsonObject = new JSONObject(responseMsg.toString());
                        transactionLogger.error("---- commonBackEnd OnlinePay response ---- "+jsonObject);

                        String tracking_id = "";
                        String ip = "";
                        String transaction_id = "";
                        String resp_status = "";
                        String amount = "";
                        String currency = "";
                        String decline_reason = "";
                        String decline_code = "";
                        String resp_remark = "";

                        if (jsonObject != null)
                        {
                            if(jsonObject.has("payment_data"))
                            {
                                JSONObject jsonPaymentData = jsonObject.getJSONObject("payment_data");

                                if(jsonPaymentData != null)
                                {
                                    if (jsonPaymentData.has("id"))
                                    {
                                        transaction_id = jsonPaymentData.getString("id");
                                    }

                                    if (jsonPaymentData.has("status"))
                                    {
                                        resp_status = jsonPaymentData.getString("status");
                                    }

                                    if (jsonPaymentData.has("amount"))
                                    {
                                        amount = jsonPaymentData.getString("amount");
                                    }

                                    if (jsonPaymentData.has("currency"))
                                    {
                                        currency = jsonPaymentData.getString("currency");
                                    }

                                    if(jsonPaymentData.has("decline_reason"))
                                    {
                                        decline_reason = jsonPaymentData.getString("decline_reason");
                                    }

                                    if(jsonPaymentData.has("decline_code"))
                                    {
                                        decline_code = jsonPaymentData.getString("decline_code");
                                    }

                                }
                            }

                            if(jsonObject.has("merchant_order"))
                            {
                                JSONObject jsonMerchantOrder = jsonObject.getJSONObject("merchant_order");

                                if(jsonMerchantOrder != null)
                                {
                                    if(jsonMerchantOrder.has("id"))
                                    {
                                        tracking_id = jsonMerchantOrder.getString("id");
                                    }
                                }
                            }

                            if(jsonObject.has("customer"))
                            {
                                JSONObject jsonCustomer = jsonObject.getJSONObject("customer");

                                if(jsonCustomer != null)
                                {
                                    if(jsonCustomer.has("ip"))
                                    {
                                        ip = jsonCustomer.getString("ip");
                                    }
                                }
                            }

                            transRespDetails = new Comm3DResponseVO();
                            String action="";
                            if(resp_status.equalsIgnoreCase("DECLINED") && (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus)))
                            {
                                resp_remark = decline_reason;
                                responseStatus ="Failed ("+decline_reason+")";
                                transRespDetails.setStatus("failed");
                                updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                                message="SYS: Transaction Failed";
                                action=ActionEntry.ACTION_AUTHORISTION_FAILED.toString();
                            }
                            else if((resp_status.equalsIgnoreCase("COMPLETED")) && (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus)))
                            {
                                resp_remark = "Transaction Successful";
                                responseStatus="Successful";
                                transRespDetails.setStatus("success");
                                updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                message="SYS: Transaction Successful";
                                action=ActionEntry.ACTION_CAPTURE_SUCCESSFUL.toString();
                            }
                            else
                            {
                                resp_remark = "Transaction Failed";
                                responseStatus ="Failed";
                                transRespDetails.setStatus("failed");
                                updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                                message="SYS: Transaction Failed";
                                action=ActionEntry.ACTION_AUTHORISTION_FAILED.toString();
                            }


                            transRespDetails.setDescription(resp_remark);
                            transRespDetails.setTransactionId(transaction_id);
                            transRespDetails.setAmount(amount);
                            transRespDetails.setCurrency(currency);
                            transRespDetails.setTmpl_Amount(amount);
                            transRespDetails.setTmpl_Currency(currency);
                            transRespDetails.setRemark(resp_remark);
                            transRespDetails.setErrorCode(decline_code);

                            String query="update transaction_common set status='"+updatedStatus+"',paymentid='" + transaction_id +"',remark='"+resp_remark+"',captureamount='" + amount + "',amount='"+amount+"' where trackingid='"+tracking_id+"'";
                            transactionLogger.error("-----query-----" + query);
                            con = Database.getConnection();
                            Database.executeUpdate(query.toString(), con);

                            actionEntry.actionEntryForCommon(tracking_id, amount, action, updatedStatus, transRespDetails, auditTrailVO, ip);
                            statusSyncDAO.updateAllTransactionFlowFlag(tracking_id, updatedStatus);

                        }

                    }else if (fromType.equalsIgnoreCase("phoneix"))
                    {
                        transRespDetails = new Comm3DResponseVO();
                        responseCode = request.getParameter("Reply");
                        String responseDesc=request.getParameter("ReplyDesc");
                        String transID=request.getParameter("TransID");
                        String remark = PhoneixUtils.getDescription(responseCode);
                        if(!functions.isValueNull(remark))
                            remark=responseDesc;
                        String amount = request.getParameter("Amount");
                        //MerchantDAO merchantDAO = new MerchantDAO();
                        MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(toId);

                        if (merchantDetailsVO != null)
                        {
                            isService = merchantDetailsVO.getIsService();
                        }
                        descriptor = gatewayAccount.getDisplayName();
                        transRespDetails.setRemark(remark);
                        transRespDetails.setDescription(remark);
                        transRespDetails.setErrorCode(responseCode);
                        transRespDetails.setTransactionId(transID);
                        StringBuffer dbBuffer = new StringBuffer();
                        transactionLogger.error("Inside DBStatus...." + dbStatus);
                        if ((PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus)))
                        {
                            //todo set status success and db status capturesuccess and update other details also
                            if ("000".equalsIgnoreCase(responseCode) && "SUCCESS".equalsIgnoreCase(responseDesc))
                            {
                                if ("N".equalsIgnoreCase(isService) || "PA".equalsIgnoreCase(transactionDetailsVO.getTransactionType()))
                                {
                                    transRespDetails.setStatus("success");
                                    transRespDetails.setDescriptor(descriptor);
                                    dbBuffer.append("update transaction_common set status='authsuccessful' , paymentid='"+transID+"'" + ",successtimestamp='" + functions.getTimestamp() + "'");
                                    paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                                    updatedStatus = "authsuccessful";
                                }
                                else
                                {
                                    transRespDetails.setStatus("success");
                                    transRespDetails.setDescriptor(descriptor);
                                    dbBuffer.append("update transaction_common set captureamount='" + amount + "',status='capturesuccess', paymentid='"+transID+"'" + ",successtimestamp='" + functions.getTimestamp() + "'");
                                    paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                                    updatedStatus = "capturesuccess";
                                }
                            }
                            else if ("553".equalsIgnoreCase(responseCode))
                            {
                                /*transRespDetails.setStatus("success");
                                transRespDetails.setDescriptor(descriptor);
                                dbBuffer.append("update transaction_common set status='pending3DConfirmation'");
                                paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);

                                updatedStatus = "pending3DConfirmation";*/
                            }
                            else
                            {
                                responseStatus = "Failed(" + message + ")";
                                updatedStatus = "authfailed";
                                dbBuffer.append("update transaction_common set status='authfailed'" + ",failuretimestamp='" + functions.getTimestamp() + "'");
                                actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                            }
                            transactionLogger.error("responseCode---->"+responseCode);
                            if (!"553".equalsIgnoreCase(responseCode)){
                                dbBuffer.append(" ,remark='" + remark + "' where trackingid = " + trackingId);
                                con = Database.getConnection();
                                Database.executeUpdate(dbBuffer.toString(), con);
                            }
                            transactionLogger.error("phoneix dbBuffer.....!!!" + dbBuffer);
                        }
                    }
                    else if (fromType.equalsIgnoreCase("curo"))
                    {
                        transRespDetails = new Comm3DResponseVO();
                        String resCode = request.getParameter("code");
                        status=request.getParameter("status");
                        String amount = request.getParameter("amount");
                        int amountInt= Integer.parseInt(amount);
                        amount=String.valueOf(amountInt/100)+".00";
                        String currency = request.getParameter("currency");
                        String responseTime = request.getParameter("book_date");
                        transactionId=request.getParameter("transaction");

                        descriptor = gatewayAccount.getDisplayName();
                        transRespDetails.setErrorCode(resCode);
                        transRespDetails.setCurrency(currency);
                        transRespDetails.setTransactionId(transactionId);
                        transRespDetails.setResponseTime(responseTime);
                        StringBuffer dbBuffer = new StringBuffer();
                        transactionLogger.error("Inside DBStatus...." + dbStatus);
                        if ((PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus)))
                        {
                            message= CuroUtils.getErrorMessage(resCode);
                            transRespDetails.setRemark(message);
                            transRespDetails.setDescription(message);
                            //todo set status success and db status capturesuccess and update other details also
                            if ("200".equalsIgnoreCase(resCode) && "success".equalsIgnoreCase(status))
                            {
                                message="Transaction Successful";
                                transRespDetails.setStatus("success");
                                transRespDetails.setDescriptor(descriptor);
                                dbBuffer.append("update transaction_common set captureamount='" + amount + "',status='capturesuccess'" + ",successtimestamp='" + functions.getTimestamp() + "'");
                                paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                                updatedStatus = "capturesuccess";
                            }
                            else if("0".equalsIgnoreCase(resCode) || "700".equalsIgnoreCase(resCode))
                            {
                                message="Transaction is pending";
                                transRespDetails.setStatus("pending");
                                dbBuffer.append("update transaction_common set status='authstarted'");
                                paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                updatedStatus = "authstarted";
                            }
                            else
                            {

                                responseStatus = "Failed(" + message + ")";
                                updatedStatus = "authfailed";
                                dbBuffer.append("update transaction_common set status='authfailed'" + ",failuretimestamp='" + functions.getTimestamp() + "'");
                                actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                            }
                            transactionLogger.error("responseCode---->"+responseCode);
                            dbBuffer.append(" ,paymentid='"+transactionId+"',remark='" + message + "' where trackingid = " + trackingId);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                            transactionLogger.error("curo dbBuffer.....!!!" + dbBuffer);
                        }
                    }
                    else if (fromType.equalsIgnoreCase("emerchant"))
                    {
                        transRespDetails = new Comm3DResponseVO();
                        status=request.getParameter("status");
                        eci = request.getParameter("eci");
                        String transaction_type = request.getParameter("transaction_type");
                        transactionId=request.getParameter("unique_id");

                        descriptor = gatewayAccount.getDisplayName();
                        transRespDetails.setTransactionId(transactionId);
                        transRespDetails.setAmount(transactionDetailsVO.getAmount());
                        transRespDetails.setCurrency(transactionDetailsVO.getCurrency());
                        StringBuffer dbBuffer = new StringBuffer();
                        transactionLogger.error("Inside DBStatus...." + dbStatus);
                        if ((PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus)))
                        {
                            transRespDetails.setRemark(status);
                            transRespDetails.setDescription(status);
                            //todo set status success and db status capturesuccess and update other details also
                            if ("approved".equalsIgnoreCase(status))
                            {
                                message="Transaction Successful";
                                transRespDetails.setStatus("success");
                                transRespDetails.setDescriptor(descriptor);
                                transactionDetailsVO.setBillingDesc(descriptor);
                                transRespDetails.setEci(eci);
                                transactionDetailsVO.setEci(eci);
                                if("sale3d".equalsIgnoreCase(transaction_type))
                                {
                                    dbBuffer.append("update transaction_common set paymentid='"+transactionId+"',captureamount='" + transactionDetailsVO.getAmount() + "',status='capturesuccess',eci='" + eci + "'" + ",successtimestamp='" + functions.getTimestamp() + "'");
                                    paymentProcess.actionEntry(trackingId, transactionDetailsVO.getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                                    updatedStatus = "capturesuccess";
                                }else
                                {
                                    dbBuffer.append("update transaction_common set paymentid='"+transactionId+"',status='authsuccessful',eci='" + eci + "'" + ",successtimestamp='" + functions.getTimestamp() + "'");
                                    paymentProcess.actionEntry(trackingId, transactionDetailsVO.getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authsuccessful");
                                    updatedStatus = "authsuccessful";
                                }
                            }
                            else
                            {
                                responseStatus = "Failed(" + status + ")";
                                updatedStatus = "authfailed";
                                dbBuffer.append("update transaction_common set status='authfailed'" + ",failuretimestamp='" + functions.getTimestamp() + "'");
                                actionEntry.actionEntryForCommon(trackingId, transactionDetailsVO.getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                            }
                            transactionLogger.error("responseCode---->"+responseCode);
                            dbBuffer.append(" ,paymentid='"+transactionId+"',remark='" + status + "' where trackingid = " + trackingId);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                            transactionLogger.error("curo dbBuffer.....!!!" + dbBuffer);
                        } else
                        {
                            if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                            {
                                transRespDetails.setDescriptor(descriptor);
                                transactionDetailsVO.setBillingDesc(descriptor);
                                message = "Transaction Successful";
                                responseStatus = "Transaction Successful";
                                updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                dbBuffer.append("update transaction_common set eci='" + eci + "'");

                            }else if(PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus)){
                                message = "Transaction Successful";
                                responseStatus = "Transaction Successful";
                                updatedStatus=PZTransactionStatus.AUTH_SUCCESS.toString();
                                dbBuffer.append("update transaction_common set eci='" + eci + "'");
                            }else if(PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus)){
                                message = "Transaction Declined";
                                responseStatus="Transaction Failed";
                                updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                                dbBuffer.append("update transaction_common set eci='" + eci + "'");
                            }
                            else
                            {
                                message = "Transaction Declined";
                                responseStatus = "Transaction Failed";
                                updatedStatus=PZTransactionStatus.FAILED.toString();
                            }
                            dbBuffer.append(" ,remark='" + status + "' where trackingid = " + trackingId);
                        }
                    }

                    if(!functions.isValueNull(notificationUrl) && functions.isValueNull(merchantDetailsVO2.getNotificationUrl())){
                        notificationUrl = merchantDetailsVO2.getNotificationUrl();
                    }

                    if (functions.isValueNull(notificationUrl))
                    {
                        if(transRespDetails != null && functions.isValueNull(transRespDetails.getRemark()))
                            message=transRespDetails.getRemark();
                        transactionLogger.error("inside sending notification---" + notificationUrl);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        if(functions.isValueNull(merchantDetailsVO2.getNotificationUrl())){
                            transactionDetailsVO.setMerchantNotificationUrl(merchantDetailsVO2.getNotificationUrl());
                        }else{
                            transactionDetailsVO.setMerchantNotificationUrl("");
                        }


                        asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, message,"");
                    }

                }


                JSONObject jsonResObject = new JSONObject();
                jsonResObject.put("responseCode", responseCode);
                jsonResObject.put("responseStatus", responseStatus);
                response.setContentType("application/json");
                response.setStatus(200);
                pWriter.println(jsonResObject.toString());
                pWriter.flush();
                return;

            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException---",e);
        }catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException----",e);
        }catch (SystemError e)
        {
            transactionLogger.error("SystemError----",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
