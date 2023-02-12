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
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

/**
 * Created by Admin on 9/21/2020.
 */
public class SafeChargeV2BackEndServlet extends HttpServlet
{
    TransactionLogger transactionLogger= new TransactionLogger(SafeChargeV2FrontEndServlet.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        transactionLogger.error("---- Inside SafeChargeV2BackEndServlet ---");
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
        for (Object key : request.getParameterMap().keySet())
        {
            transactionLogger.error("----for loop-----" + key + " = " + request.getParameter((String) key) + "----------");
        }
        String trackingId=request.getParameter("clientUniqueId");
        String transactionStatus=request.getParameter("Status");
        String message=request.getParameter("Reason");
        String errorCode=request.getParameter("ErrCode");
        String transactionID=request.getParameter("TransactionID");
        String transactionType=request.getParameter("transactionType");
        String eci=request.getParameter("eci");
        String authCode=request.getParameter("AuthCode");
        String userPaymentOptionId=request.getParameter("userPaymentOptionId");
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
                    if ("Sale".equalsIgnoreCase(transactionType) || "Auth".equalsIgnoreCase(transactionType))
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
                        transactionLogger.error("dbStatus-----" + dbStatus);

                        transactionLogger.debug("dbStatus------" + dbStatus);

                        transRespDetails.setTransactionId(transactionID);
                        transRespDetails.setRemark(message);
                        transRespDetails.setDescriptor(billingDesc);
                        transRespDetails.setAmount(amount);
                        transRespDetails.setCurrency(currency);
                        transRespDetails.setEci(eci);
                        transRespDetails.setAuthCode(authCode);
                        transRespDetails.setErrorCode(errorCode);
                        transRespDetails.setResponseHashInfo(userPaymentOptionId);
                        if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus))
                        {
                            con=Database.getConnection();
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            StringBuffer dbBuffer = new StringBuffer();
                            if("APPROVED".equalsIgnoreCase(transactionStatus))
                            {
                                status="success";
                                transRespDetails.setDescriptor(billingDesc);
                                transactionDetailsVO.setBillingDesc(billingDesc);
                                transactionDetailsVO.setStatus(status);
                                if("Sale".equalsIgnoreCase(transactionType))
                                {
                                    updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                    dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionID + "',status='capturesuccess' ,eci='" + eci + "',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "',authorization_code='" + authCode + "' where trackingid = " + trackingId);
                                    transactionLogger.error("dbBuffer--->"+dbBuffer);
                                    Database.executeUpdate(dbBuffer.toString(),con);
                                    actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                                }else if("Auth".equalsIgnoreCase(transactionType))
                                {
                                    updatedStatus = PZTransactionStatus.AUTH_SUCCESS.toString();
                                    dbBuffer.append("update transaction_common set paymentid='" + transactionID + "',status='authsuccessful' ,eci='" + eci + "',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "',authorization_code='" + authCode + "' where trackingid = " + trackingId);
                                    transactionLogger.error("dbBuffer--->"+dbBuffer);
                                    Database.executeUpdate(dbBuffer.toString(),con);
                                    actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                                }
                            }else
                            {
                                status="failed";
                                updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                                dbBuffer.append("update transaction_common set status='authfailed,");
                                if(functions.isValueNull(transactionID))
                                    dbBuffer.append("paymentid='" + transactionID + "',");
                                dbBuffer.append("eci='" + eci + "',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "',authorization_code='" + authCode + "' where trackingid = " + trackingId);
                                transactionLogger.error("dbBuffer--->"+dbBuffer);
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
