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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;

/**
 * Created by Admin on 10/28/2020.
 */
public class AlweaveBackEndServlet extends HttpServlet
{
    TransactionLogger transactionLogger= new TransactionLogger(AlweaveBackEndServlet.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        transactionLogger.error("---- Inside AlweaveBackEndServlet ---");
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
        BufferedReader io=request.getReader();
        String str="";
        while ((str=io.readLine())!=null)
        {
            resposeMsg.append(str);
        }
        transactionLogger.error("-----Notification JSON-----" + resposeMsg);
        String trackingId="";
        String accountId="";
        String billingDesc="";
        String cardType="";
        String cardTypeName="";
        String toId="";
        String dbStatus="";
        String sKey="";
        String supportName="";
        String currency="";
        String amount="";
        String status="";
        String notificationUrl="";
        String updatedStatus="";
        String transactionID="";
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
            if(functions.isValueNull(resposeMsg.toString()))
            {
                JSONObject responseJSON = new JSONObject(resposeMsg.toString());
                if(responseJSON.has("ordr_idxx"))
                    trackingId=responseJSON.getString("ordr_idxx");
                if(responseJSON.has("tno"))
                    transactionID=responseJSON.getString("tno");
                if(responseJSON.has("res_cd"))
                    res_cd=responseJSON.getString("res_cd");
                if(responseJSON.has("res_msg"))
                {
                    res_msg = responseJSON.getString("res_msg");
                }
                if(responseJSON.has("app_time"))
                    app_time=responseJSON.getString("app_time");
                if(responseJSON.has("app_no"))
                    app_no=responseJSON.getString("app_no");
                if(responseJSON.has("buyr_name"))
                {
                    buyr_name = responseJSON.getString("buyr_name");
                    if(buyr_name.contains(" "))
                    {
                        firstname=buyr_name.split(" ")[0];
                        lastname=buyr_name.split(" ")[1];
                    }else {
                        firstname=buyr_name;
                    }
                }
                if(responseJSON.has("buyr_mail"))
                    buyr_mail=responseJSON.getString("buyr_mail");
                if(responseJSON.has("card_name"))
                    card_name=responseJSON.getString("card_name");
            }

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
                        {
                            sKey = merchantDetailsVO.getKey();
                            if(functions.isValueNull(merchantDetailsVO.getMerchantLogo()) && merchantDetailsVO.getMerchantLogo().equalsIgnoreCase("Y"))
                                supportName=merchantDetailsVO.getCompany_name();
                            else if(functions.isValueNull(merchantDetailsVO.getPartnerLogoFlag()) && merchantDetailsVO.getPartnerLogoFlag().equalsIgnoreCase("Y"))
                                supportName=merchantDetailsVO.getPartnerName();
                        }
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
                        con=Database.getConnection();
                        if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus))
                        {
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            if("0000".equalsIgnoreCase(res_cd))
                            {
                                if(functions.isValueNull(supportName) && functions.isValueNull(res_msg) && (res_msg.contains("KCP") || res_msg.contains("kcp")))
                                    message=res_msg.replaceAll("kcp",supportName);
                                else if(functions.isValueNull(res_msg))
                                    message=res_msg;
                                else
                                    message="Transaction Successful";
                                transRespDetails.setRemark(message);
                                transRespDetails.setDescription(message);
                                status="success";
                                transRespDetails.setDescriptor(billingDesc);
                                transactionDetailsVO.setBillingDesc(billingDesc);
                                transactionDetailsVO.setStatus(status);
                                updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionID + "',status='capturesuccess',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "',authorization_code='"+app_no+"',name='"+ESAPI.encoder().encodeForSQL(me, buyr_name)+"',firstname='"+ESAPI.encoder().encodeForSQL(me, firstname)+"',lastname='"+ESAPI.encoder().encodeForSQL(me, lastname)+"',emailaddr='"+ESAPI.encoder().encodeForSQL(me, buyr_mail)+" where trackingid = " + trackingId);
                                transactionLogger.error("dbBuffer--->" + dbBuffer);
                                Database.executeUpdate(dbBuffer.toString(),con);
                                actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                            }else
                            {
                                if(functions.isValueNull(supportName) && functions.isValueNull(res_msg) && (res_msg.contains("KCP") || res_msg.contains("kcp")))
                                    message=res_msg.replaceAll("kcp",supportName);
                                else if(functions.isValueNull(res_msg))
                                    message=res_msg;
                                else
                                    message="Transaction failed";
                                transRespDetails.setRemark(message);
                                transRespDetails.setDescription(message);
                                status="failed";
                                updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                                dbBuffer.append("update transaction_common set status='authfailed,");
                                if(functions.isValueNull(transactionID))
                                    dbBuffer.append("paymentid='" + transactionID + "',");
                                dbBuffer.append("remark='" + ESAPI.encoder().encodeForSQL(me, message) + "',authorization_code='"+app_no+"',name='"+ESAPI.encoder().encodeForSQL(me, buyr_name)+"',firstname='"+ESAPI.encoder().encodeForSQL(me, firstname)+"',lastname='"+ESAPI.encoder().encodeForSQL(me, lastname)+"',emailaddr='"+ESAPI.encoder().encodeForSQL(me, buyr_mail)+"' where trackingid = " + trackingId);
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
                    }else
                        {
                            dbBuffer.append("update transaction_common set paymentid='" + transactionID + "',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "',authorization_code='"+app_no+"',name='"+ESAPI.encoder().encodeForSQL(me, buyr_name)+"',firstname='"+ESAPI.encoder().encodeForSQL(me, firstname)+"',lastname='"+ESAPI.encoder().encodeForSQL(me, lastname)+"',emailaddr='"+ESAPI.encoder().encodeForSQL(me, buyr_mail)+" where trackingid = " + trackingId);
                            transactionLogger.error("dbBuffer--->" + dbBuffer);
                            Database.executeUpdate(dbBuffer.toString(),con);
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
