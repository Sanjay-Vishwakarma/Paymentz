package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.TWDTaiwan.TWDTaiwanUtils;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Enumeration;

/**
 * Created by Admin on 12/24/2020.
 */
public class TWDTaiwanBackEndServlet extends HttpServlet
{
    TransactionLogger transactionLogger=new TransactionLogger(TWDTaiwanBackEndServlet.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        transactionLogger.error("--- Inside TWDTaiwanBackEndServlet ---");
        Functions functions = new Functions();
        TransactionManager transactionManager=new TransactionManager();
        MerchantDAO merchantDAO=new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        CommResponseVO transRespDetails = new CommResponseVO();
        ActionEntry actionEntry=new ActionEntry();
        StatusSyncDAO statusSyncDAO=new StatusSyncDAO();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        PrintWriter pWriter=response.getWriter();
        StringBuffer resposeMsg=new StringBuffer();
        Enumeration enumeration=request.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = request.getParameter(key);

            transactionLogger.error("Name=" + key + "-----Value=" + value);
        }

        String trackingId=request.getParameter("trackingId");
        String resStatus=request.getParameter("status");
        String txid=request.getParameter("txid");
        String transactionID=request.getParameter("parent_txid");
        String comment=request.getParameter("comment");
        String descriptor=request.getParameter("descriptor");
        String error=request.getParameter("error");
        String error_type=request.getParameter("error_type");
        String error_sys=request.getParameter("error_sys");
        String error_msg=request.getParameter("error_msg");
        String error_info=request.getParameter("error_info");
        String error_code=request.getParameter("error_code");
        String tx_action=request.getParameter("tx_action");
        String reject_code="";
        if(functions.isValueNull(error_msg) && error_msg.contains("-"))
            reject_code=error_msg.split("-")[0];
        if(functions.isValueNull(error_msg))
        {
            String code=error_msg;
            String errorMsg = TWDTaiwanUtils.getErrorMessage(code);
            if(functions.isValueNull(errorMsg))
            {
                error_msg=errorMsg;
                error_code = code;
            }
        }
        if(!functions.isValueNull(error_code) && functions.isValueNull(reject_code))
        {
            error_code = reject_code;
            error_msg= TWDTaiwanUtils.getErrorMessage(error_code);
        }

        String accountId="";
        String billingDesc="";
        String cardType="";
        String cardTypeName="";
        String toId="";
        String dbStatus="";
        String sKey="";
        String currency="";
        String amount="";
        String status="";
        String notificationUrl="";
        String updatedStatus="";
        String res_cd="";
        String res_msg="";
        String message="";
        String app_time="";
        String app_no="";
        String buyr_name="";
        String firstname="";
        String lastname="";
        String buyr_mail="";
        String card_name="";
        String responseCode = "200";
        String responseStatus = "OK";
        Connection con=null;

        try
        {

        if (functions.isValueNull(trackingId))
        {
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            if (transactionDetailsVO != null && functions.isValueNull(transactionDetailsVO.getTrackingid()))
            {
                accountId = transactionDetailsVO.getAccountId();
                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                billingDesc = gatewayAccount.getDisplayName();
                String secretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
                transactionLogger.debug("secretKey ---" + secretKey);
                cardType = transactionDetailsVO.getCardtype();
                cardTypeName = GatewayAccountService.getCardType(cardType);
                transactionLogger.debug("---cardType---" + cardType);
                transactionLogger.debug("---cardTypeName---" + cardTypeName);
                toId = transactionDetailsVO.getToid();
                dbStatus = transactionDetailsVO.getStatus();
                merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                if (merchantDetailsVO != null)
                    sKey = merchantDetailsVO.getKey();
                transactionDetailsVO.setSecretKey(sKey);

                if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                {
                    transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                }
                if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                {
                    transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));

                }

                transactionLogger.debug("accountId----" + accountId);
                transactionLogger.debug("toId----" + toId);
                transactionLogger.debug("dbStatus----" + dbStatus);

                auditTrailVO.setActionExecutorName("AcquirerCommonBackEnd");
                auditTrailVO.setActionExecutorId(toId);
                currency = transactionDetailsVO.getCurrency();
                amount = transactionDetailsVO.getAmount();

                transactionLogger.debug("dbStatus------" + dbStatus);

                transRespDetails.setTransactionId(transactionID);
                transRespDetails.setDescriptor(billingDesc);
                transRespDetails.setAmount(amount);
                transRespDetails.setCurrency(currency);
                transRespDetails.setErrorCode(res_cd);
                transRespDetails.setBankTransactionDate(app_time);
                transRespDetails.setAuthCode(app_no);
                transRespDetails.setResponseHashInfo(card_name);
                StringBuffer dbBuffer = new StringBuffer();
                con= Database.getConnection();
                if(functions.isValueNull(comment))
                    message=comment;
                else if(functions.isValueNull(error))
                    message=error;
                else if(functions.isValueNull(error_info))
                    message=error_info;
                else if(functions.isValueNull(error_msg) && error_msg.length()>3)
                    message=error_msg;
                if ((PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus)) && "SETTLEMENT".equalsIgnoreCase(tx_action))
                {
                    notificationUrl = transactionDetailsVO.getNotificationUrl();
                    if("OK".equalsIgnoreCase(resStatus))
                    {
                        if(!functions.isValueNull(message))
                            message="Transaction Successful";
                        transRespDetails.setRemark(message);
                        status="success";
                        if(functions.isValueNull(descriptor))
                            transactionDetailsVO.setBillingDesc(descriptor);
                        else
                            transactionDetailsVO.setBillingDesc(billingDesc);
                        transactionDetailsVO.setStatus(status);
                        updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionID + "',status='capturesuccess',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "',authorization_code='"+app_no+"' where trackingid = " + trackingId);
                        transactionLogger.error("dbBuffer--->" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(),con);
                        actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }else
                    {
                        if(!functions.isValueNull(message))
                            message="Transaction failed";
                        transRespDetails.setRemark(message);
                        transRespDetails.setErrorCode(error_code);
                        status="failed";
                        updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                        dbBuffer.append("update transaction_common set status='authfailed',");
                        if(functions.isValueNull(transactionID))
                            dbBuffer.append("paymentid='" + transactionID + "',");
                        dbBuffer.append("remark='" + ESAPI.encoder().encodeForSQL(me, message) + "',authorization_code='"+app_no+"' where trackingid = " + trackingId);
                        transactionLogger.error("dbBuffer--->" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(),con);
                        actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }
                    AsynchronousMailService asynchronousMailService = AsynchronousMailService.getInstance();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                    AsynchronousSmsService smsService = AsynchronousSmsService.getInstance();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                    if (functions.isValueNull(notificationUrl))
                    {
                        transactionLogger.error("inside sending notification---" + notificationUrl);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, message,"");
                    }
                }else if("PREAUTH".equalsIgnoreCase(tx_action))
                {
                    if("OK".equalsIgnoreCase(resStatus)){
                        transRespDetails.setStatus("pending");
                    dbBuffer.append("update transaction_common set paymentid='" + transactionID + "',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "',authorization_code='"+app_no+"' where trackingid = " + trackingId);
                    transactionLogger.error("dbBuffer--->" + dbBuffer);
                    Database.executeUpdate(dbBuffer.toString(),con);
                    actionEntry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, null, auditTrailVO, null);
                    }else
                    {
                        if(!functions.isValueNull(message))
                            message="Transaction failed";
                        transRespDetails.setRemark(message);
                        transRespDetails.setErrorCode(error_code);
                        status="failed";
                        updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                        dbBuffer.append("update transaction_common set status='authfailed',");
                        if(functions.isValueNull(transactionID))
                            dbBuffer.append("paymentid='" + transactionID + "',");
                        dbBuffer.append("remark='" + ESAPI.encoder().encodeForSQL(me, message) + "',authorization_code='"+app_no+"' where trackingid = " + trackingId);
                        transactionLogger.error("dbBuffer--->" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(),con);
                        actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }

                }

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
    catch (PZDBViolationException e)
    {
        transactionLogger.error("PZDBViolationException -----"+trackingId+"-----",e);
    }
    catch (SystemError systemError)
    {
        transactionLogger.error("SystemError -----" + trackingId + "-----", systemError);
    }
    catch (JSONException e)
    {
        transactionLogger.error("JSONException -----" + trackingId + "-----", e);
    }
    finally
    {
        Database.closeConnection(con);
    }
    }
}
