package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.FlutterWave.FlutterWavePaymentGateway;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Enumeration;

/**
 * Created by Vivek on 6/4/2019.
 */
public class FlutterWaveBackEndServlet extends HttpServlet
{
   // private static TransactionLogger transactionLogger= new TransactionLogger(FlutterWaveBackEndServlet.class.getName());
    private static FlutterWaveLogger transactionLogger= new FlutterWaveLogger(FlutterWaveBackEndServlet.class.getName());
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request,response);
    }

    protected void doService(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
    {
        transactionLogger.error("Inside Flutter ---");
        TransactionManager transactionManager   = new TransactionManager();
        CommResponseVO transRespDetails         = new CommResponseVO();
        Functions functions         =new Functions();
        PrintWriter pWriter         = response.getWriter();
        String responseCode         = "200";
        String responseStatus       = "OK";
        AuditTrailVO auditTrailVO   = new AuditTrailVO();
        ActionEntry actionEntry     = new ActionEntry();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        MerchantDAO merchantDAO     = new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO = null;
        Connection con      = null;
        String trackingId   = "";
        String toId="";
        String sKey="";
        String accountId = "";
        String fromType = "";
        String transactionStatus = "";
        String eci = "";
        String transactionId = "";
        String status="";
        String message="";
        String dbStatus="";
        String billingDesc="";
        String notificationUrl="";
        String updatedStatus="";
        String cardType="";
        String toType="";
        String cardTypeName         = "";
        StringBuilder responseMsg   = new StringBuilder();
        String confirmStatus        = "";
        BufferedReader br           = request.getReader();
        String str;
        while ((str = br.readLine()) != null)
        {
            responseMsg.append(str);
        }

        transactionLogger.error("-----Request JSON-----" + responseMsg);
        try
        {
            JSONObject jsonObject = new JSONObject(responseMsg.toString());
            transactionLogger.error("FlutterWaveBackEndServlet --------->"+jsonObject.toString());
            if (jsonObject != null)
            {
                String id = "", txRef = "", flwRef = "", createdAt = "", amount = "", currency = "", eventtype = "";

                if (jsonObject.has("id"))
                {
                    id = jsonObject.getString("id");
                    transactionLogger.debug("id ---" + id);
                }
                if (jsonObject.has("txRef"))
                {
                    trackingId = jsonObject.getString("txRef");
                    transactionLogger.debug("txRef ---" + trackingId);
                }else if (jsonObject.has("tx_ref"))
                {
                    trackingId = jsonObject.getString("tx_ref");
                    transactionLogger.debug("txRef ---" + trackingId);
                }
                if (jsonObject.has("flwRef"))
                {
                    flwRef = jsonObject.getString("flwRef");
                    transactionLogger.debug("flwRef ---" + flwRef);
                }else if (jsonObject.has("flw_ref"))
                {
                    flwRef = jsonObject.getString("flw_ref");
                    transactionLogger.debug("flwRef ---" + flwRef);
                }
                if (jsonObject.has("createdAt"))
                {
                    createdAt = jsonObject.getString("createdAt");
                    transactionLogger.debug("createdAt ---" + createdAt);
                }else if (jsonObject.has("created_at"))
                {
                    createdAt = jsonObject.getString("created_at");
                    transactionLogger.debug("createdAt ---" + createdAt);
                }
                if (jsonObject.has("amount"))
                {
                    amount = jsonObject.getString("amount");
                    transactionLogger.debug("amount ---" + amount);
                }
                if (jsonObject.has("status"))
                {
                    status = jsonObject.getString("status");
                    transactionLogger.debug("status ---" + status);
                }
                if (jsonObject.has("currency"))
                {
                    currency = jsonObject.getString("currency");
                    transactionLogger.debug("currency ---" + currency);
                }
                if (jsonObject.has("event.type"))
                {
                    eventtype = jsonObject.getString("event.type");
                    transactionLogger.debug("event.type ---" + eventtype);
                }
                if(jsonObject.has("data") && !functions.isValueNull(trackingId))
                {
                    JSONObject data=jsonObject.getJSONObject("data");
                    if (data.has("id"))
                    {
                        id = data.getString("id");
                        transactionLogger.debug("id ---" + id);
                    }
                    if (data.has("txRef"))
                    {
                        trackingId = data.getString("txRef");
                        transactionLogger.debug("txRef ---" + trackingId);
                    }else if (data.has("tx_ref"))
                    {
                        trackingId = data.getString("tx_ref");
                        transactionLogger.debug("txRef ---" + trackingId);
                    }
                    if (data.has("flwRef"))
                    {
                        flwRef = data.getString("flwRef");
                        transactionLogger.debug("flwRef ---" + flwRef);
                    }else if (data.has("flw_ref"))
                    {
                        flwRef = data.getString("flw_ref");
                        transactionLogger.debug("flwRef ---" + flwRef);
                    }
                    if (data.has("createdAt"))
                    {
                        createdAt = data.getString("createdAt");
                        transactionLogger.debug("createdAt ---" + createdAt);
                    }else if (data.has("created_at"))
                    {
                        createdAt = data.getString("created_at");
                        transactionLogger.debug("createdAt ---" + createdAt);
                    }
                    if (data.has("amount"))
                    {
                        amount = data.getString("amount");
                        transactionLogger.debug("amount ---" + amount);
                    }
                    if (data.has("status"))
                    {
                        status = data.getString("status");
                        transactionLogger.debug("status ---" + status);
                    }
                    if (data.has("currency"))
                    {
                        currency = data.getString("currency");
                        transactionLogger.debug("currency ---" + currency);
                    }
                    if (data.has("event.type"))
                    {
                        eventtype = data.getString("event.type");
                        transactionLogger.debug("event.type ---" + eventtype);
                    }
                }
                Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                if (transactionDetailsVO != null)
                {
                    accountId                       = transactionDetailsVO.getAccountId();
                    GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);
                    billingDesc                     = gatewayAccount.getDisplayName();
                    String secretKey                = gatewayAccount.getFRAUD_FTP_PASSWORD();
                    toType                          = transactionDetailsVO.getTotype();
                    transactionLogger.debug("secretKey ---"+secretKey);
                    cardType        = transactionDetailsVO.getCardtype();
                    cardTypeName    = GatewayAccountService.getCardType(cardType);
                    transactionLogger.debug("---cardType---"+cardType);
                    transactionLogger.debug("---cardTypeName---"+cardTypeName);
                    toId                = transactionDetailsVO.getToid();
                    dbStatus            = transactionDetailsVO.getStatus();
                    merchantDetailsVO   = merchantDAO.getMemberDetails(toId);
                    if(merchantDetailsVO!=null)
                        sKey=merchantDetailsVO.getKey();
                    transactionDetailsVO.setSecretKey(sKey);

                    if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                    {
                        transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                    }
                    if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                    {
                        transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));

                    }

                    transactionLogger.debug("fromType----"+fromType);
                    transactionLogger.debug("accountId----"+accountId);
                    transactionLogger.debug("toId----"+toId);
                    transactionLogger.debug("dbStatus----"+dbStatus);

                    auditTrailVO.setActionExecutorName("AcquirerCommonBackEnd");
                    auditTrailVO.setActionExecutorId(toId);
                    currency = transactionDetailsVO.getCurrency();
                    transactionLogger.error("dbStatus-----" + dbStatus);

                    transactionLogger.debug("dbStatus------" + dbStatus);

                    transRespDetails.setTransactionId(flwRef);
                    transRespDetails.setStatus(status);
                    transRespDetails.setRemark(status);
                    transRespDetails.setDescriptor(billingDesc);
                    transRespDetails.setAmount(amount);
                    transRespDetails.setCurrency(currency);
                    transactionId   = flwRef;


                    if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus) || (PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(dbStatus) && "successful".equalsIgnoreCase(status)))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        transactionLogger.debug("transactionStatus-----" + transactionStatus);
                        transactionLogger.debug("respAmount-----" + amount);
                        StringBuffer dbBuffer   = new StringBuffer();
                        message                 = status;
                        if (functions.isValueNull(status) && status.equalsIgnoreCase("successful"))
                        {
                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess' ,eci='" + eci + "',remark='" + ESAPI.encoder().encodeForSQL(me, message)+ "',successtimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                            transactionLogger.error("-----dbBuffer-----" + dbBuffer);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                            actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                            updatedStatus = "capturesuccess";
                            transactionDetailsVO.setBillingDesc(billingDesc);
                        }
                        else
                        {
                            status = "fail";
                            FlutterWavePaymentGateway flutterWavePaymentGateway = new FlutterWavePaymentGateway(accountId);
                            CommRequestVO commRequestVO                         = new CommRequestVO();
                            CommTransactionDetailsVO commTransactionDetailsVO   = new CommTransactionDetailsVO();
                            commTransactionDetailsVO.setOrderId(trackingId);
                            commTransactionDetailsVO.setTotype(toType);

                            commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                            CommResponseVO commResponseVO   = (CommResponseVO) flutterWavePaymentGateway.processInquiry(commRequestVO);
                            if(functions.isValueNull(commResponseVO.getStatus()))
                            {
                                transactionLogger.error("inside query response if");
                                message=commResponseVO.getDescription();
                                if("N/A".equalsIgnoreCase(message))
                                {
                                    transRespDetails.setRemark("Transaction failed");
                                    transRespDetails.setDescription("Transaction failed");
                                }
                                else
                                {
                                    transRespDetails.setRemark(message);
                                    transRespDetails.setDescription(message);
                                }
                                transRespDetails.setErrorCode(
                                        commResponseVO.getAuthCode());
                            }
                            if (!functions.isValueNull(message))
                            {
                                message = "fail";
                            }
                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',eci='" + eci + "',remark='" + ESAPI.encoder().encodeForSQL(me, message)+ "',failuretimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                            transactionLogger.error("-----dbBuffer-----" + dbBuffer);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                            actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                            updatedStatus = "authfailed";
                        }
                        /*AsynchronousMailService asynchronousMailService = AsynchronousMailService.getInstance();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                        AsynchronousSmsService smsService = AsynchronousSmsService.getInstance();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);*/

                        if (functions.isValueNull(notificationUrl))
                        {
                            transactionLogger.error("inside sending notification---" + notificationUrl);
                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, status,"");
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
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError--->"+trackingId+"-->",systemError);
        }
        catch (JSONException e)
        {
            transactionLogger.error("SystemError---->", e);
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("SystemError--->" + trackingId + "-->", e);
        }
        catch (PZConstraintViolationException e)
        {
            transactionLogger.error("SystemError--->" + trackingId + "-->", e);
        }finally
        {
            Database.closeConnection(con);
        }
    }
}