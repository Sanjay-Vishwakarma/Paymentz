package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Oculus.OculusUtils;
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
import java.io.PrintWriter;
import java.sql.Connection;

/**
 * Created by Admin on 7/28/2021.
 */
public class OculusBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(OculusBackEndServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.error("Inside OculusBackEndServlet ---");
        TransactionManager transactionManager=new TransactionManager();
        CommResponseVO transRespDetails = new CommResponseVO();
        Functions functions=new Functions();
        PrintWriter pWriter = response.getWriter();
        String responseCode = "200";
        String responseStatus = "OK";
        AuditTrailVO auditTrailVO= new AuditTrailVO();
        ActionEntry actionEntry = new ActionEntry();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        MerchantDAO merchantDAO=new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO=null;
        OculusUtils oculusUtils = new OculusUtils();

        transactionLogger.error("-----inside OculusBackEndServlet-----");
        for (Object key : request.getParameterMap().keySet())
        {
            transactionLogger.debug("----for loop OculusBackEndServlet-----" + key + "=" + request.getParameter((String) key) + "----------");
        }

        Connection con=null;
        String trackingId = "";
        String toId="";
        String sKey="";
        String accountId = "";
        String fromType = "";
        String message="";
        String dbStatus="";
        String billingDesc="";
        String notificationUrl="";
        String updatedStatus="";
        String cardType="";
        String cardTypeName="";
        String MCSTransactionID = "";
        String ApprovalCode ="";
        String ProcessorTransactionID ="";
        String ReferenceNumber ="";
        String Amount ="";
        String TransactionDate ="";
        String PaymentStatus ="";
        String CurrencyCode ="";
        String Result="";
        String AuthType="";
        String ResultDetail="";
        String Remark="";
        String AdditionalAuthType="";
        String transaction_Mode="";
        String resultMessage = "";
        String convertedCurrency = "";

        StringBuilder responseMsg=new StringBuilder();
        BufferedReader br = request.getReader();
        String str;
        while ((str = br.readLine()) != null)
        {
            responseMsg.append(str);
        }

        transactionLogger.error("-----Request JSON for OculusBackEndServlet-----" + responseMsg);
        try
        {
            if (functions.isValueNull(responseMsg.toString()) && responseMsg.toString().contains("{"))
            {
                JSONObject jsonObject = new JSONObject(responseMsg.toString());
                transactionLogger.error("OculusBackEndServlet --------->"+jsonObject.toString());

                if (jsonObject != null)
                {
                    if (jsonObject.has("MCSTransactionID"))
                    {
                        MCSTransactionID = jsonObject.getString("MCSTransactionID");
                    }

                    if (jsonObject.has("CountryCode"))
                    {
                        convertedCurrency = jsonObject.getString("CountryCode");
                    }

                    if (jsonObject.has("ResultMessage"))
                    {
                        resultMessage = jsonObject.getString("ResultMessage");
//                        resultMessage = "Pending_3DS_Challenge";
                    }

                    if (jsonObject.has("AdditionalAuthType"))
                    {
                        AdditionalAuthType = jsonObject.getString("AdditionalAuthType");
                        if (functions.isValueNull(AuthType) && AuthType.equals("2: Challenge3DS"))
                        {
                            transaction_Mode="3Dv2";
                        }
                        else if (functions.isValueNull(AuthType) && AuthType.equals("1: Frictionless3DS"))
                        {
                            transaction_Mode="3Dv2";
                        }
                        else
                        {
                            transaction_Mode="Non-3D";
                        }
                    }

                    if (jsonObject.has("TransactionDate"))
                    {

                        TransactionDate = jsonObject.getString("TransactionDate");
                    }

                    if (jsonObject.has("TicketNumber"))
                    {

                        trackingId = jsonObject.getString("TicketNumber");
                        transactionLogger.error("-----trackingId in response -----" + trackingId);
                    }

                    if (jsonObject.has("ResultDetail"))
                    {

                        ResultDetail = jsonObject.getString("ResultDetail");
                        Remark=oculusUtils.getErrorMessage(ResultDetail);

                    }

                    if (jsonObject.has("Result"))
                    {

                        Result = jsonObject.getString("Result");
                        if(Result.equals("0")){
                            message = "Approved";
                        }else if(Result.equals("1")){
                            message = "Declined";
                        }else if(Result.equals("2")){
                            message = "Error";
                        }else if(Result.equals("4")){
                            message = resultMessage;   // pending 3ds
                        }
                    }

                    if (jsonObject.has("ProcessorTransactionID"))
                    {

                        ProcessorTransactionID = jsonObject.getString("ProcessorTransactionID");

                    }

                    if (jsonObject.has("ApprovalCode"))
                    {

                        ApprovalCode = jsonObject.getString("ApprovalCode");

                    }

                    if (jsonObject.has("Currency"))
                    {

                        CurrencyCode = jsonObject.getString("Currency");

                    }

                    if (jsonObject.has("Amount"))
                    {
                        String TotalAmount = jsonObject.getString("Amount");
                        Double amountD=Double.parseDouble(TotalAmount);
                        Amount = String.format("%.2f", amountD);
                    }

                    if (jsonObject.has("ReferenceNumber"))
                    {

                        ReferenceNumber = jsonObject.getString("ReferenceNumber");

                    }

                    if (jsonObject.has("AuthType"))
                    {

                        AuthType = jsonObject.getString("AuthType");

                    }


                }

                transactionLogger.error("resultMessage ===== " + resultMessage);
                transactionLogger.error("message ===== " + message);
                transactionLogger.error("converted amount ===== " + Amount);
                transactionLogger.error("convertedCurrency ===== " + convertedCurrency);



                Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                if (transactionDetailsVO != null)
                {
                    accountId = transactionDetailsVO.getAccountId();
                    GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                    cardType=transactionDetailsVO.getCardtype();
                    cardTypeName = GatewayAccountService.getCardType(cardType);
                    transactionLogger.debug("---cardType---"+cardType);
                    transactionLogger.debug("---cardTypeName---"+cardTypeName);
                    toId=transactionDetailsVO.getToid();
                    dbStatus=transactionDetailsVO.getStatus();
                    merchantDetailsVO=merchantDAO.getMemberDetails(toId);
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
                    CurrencyCode = transactionDetailsVO.getCurrency();
                    transactionLogger.error("dbStatus-----" + dbStatus);

                    transactionLogger.debug("dbStatus------" + dbStatus);

                    transRespDetails.setTransactionId(MCSTransactionID);
                    transRespDetails.setStatus(message);
                    transRespDetails.setRemark(Remark);
                    transRespDetails.setDescriptor(billingDesc);
                    transRespDetails.setAmount(Amount);
                    transRespDetails.setCurrency(CurrencyCode);

                    transactionLogger.error("Oculusbackend dbStatus ----- " + trackingId + " resultMessage === " + resultMessage + " message ===== " + message);

                    if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus) || (PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(dbStatus)))
                    {
                        transactionLogger.debug("transactionStatus-----" + message);
                        transactionLogger.debug("respAmount-----" + Amount);
                        StringBuffer dbBuffer = new StringBuffer();
                        PaymentManager paymentManager = new PaymentManager();
                        if (functions.isValueNull(message) && message.equalsIgnoreCase("Approved"))
                        {
                            notificationUrl=transactionDetailsVO.getNotificationUrl();
                            billingDesc=gatewayAccount.getDisplayName();
                            dbBuffer.append("update transaction_common set captureamount='" + Amount + "',paymentid='" + MCSTransactionID + "',status='capturesuccess' ,remark='" + ESAPI.encoder().encodeForSQL(me, Remark) +"' ,walletAmount='" + Amount +"' ,walletCurrency='" + convertedCurrency + "' where trackingid = " + trackingId);
                            transactionLogger.error("-----dbBuffer-----" + dbBuffer);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                            actionEntry.actionEntryForCommon(trackingId, Amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                            if(!functions.isValueNull(transactionDetailsVO.getTransactionMode())){
                                transactionLogger.error("-----Updating Transaction Mode-----" + dbBuffer);
                                paymentManager.updatePaymentTransactionModeforCommon(transaction_Mode,trackingId);
                            }
                            updatedStatus = "capturesuccess";
                            transactionDetailsVO.setBillingDesc(billingDesc);
                        }
                        else if(functions.isValueNull(message) && ("Declined".equalsIgnoreCase(message) || "Error".equalsIgnoreCase(message)))
                        {
                            notificationUrl=transactionDetailsVO.getNotificationUrl();
                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + MCSTransactionID + "',remark='" + ESAPI.encoder().encodeForSQL(me, Remark) + "' where trackingid = " + trackingId);
                            transactionLogger.error("-----dbBuffer-----" + dbBuffer);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                            actionEntry.actionEntryForCommon(trackingId, Amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                            updatedStatus = "authfailed";
                        }
                        else
                        {
                            updatedStatus = "authstarted";
                        }
                       /* AsynchronousMailService asynchronousMailService = AsynchronousMailService.getInstance();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), message, Remark, billingDesc);

                        AsynchronousSmsService smsService = AsynchronousSmsService.getInstance();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), message, Remark, billingDesc);
*/
                        if (functions.isValueNull(notificationUrl))
                        {
                            transactionLogger.error("inside sending notification---" + notificationUrl);
                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, Remark,"");
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
        finally
        {
            Database.closeConnection(con);
        }
    }
}
