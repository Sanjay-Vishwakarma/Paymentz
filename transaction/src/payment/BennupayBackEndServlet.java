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
import com.payment.bennupay.BennupayUtils;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

/**
 * Created by Admin on 6/2/2021.
 */
public class BennupayBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(BennupayBackEndServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.error("Inside BennupayBackEndServlet ---");
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
        Connection con=null;
        String trackingId = "";
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
        String cardTypeName="";
        StringBuilder responseMsg=new StringBuilder();
        String confirmStatus="";
        BufferedReader br = request.getReader();
        BennupayUtils bennupayUtils = new BennupayUtils();
        String str;
        while ((str = br.readLine()) != null)
        {
            responseMsg.append(str);
        }

        transactionLogger.error("-----Request JSON for BennupayBackEndServlet-----" + responseMsg);
        try
        {
            if (functions.isValueNull(responseMsg.toString()) && responseMsg.toString().contains("{"))
            {
            JSONObject jsonObject = new JSONObject(responseMsg.toString());
            transactionLogger.error("BennupayBackEndServlet --------->"+jsonObject.toString());
            String ConfirmationNumber = "";
            String TransactionId ="";
            String Code ="";
            String Description ="";
            String TotalAmount ="";
            String TxnTime ="";
            String PaymentStatus ="";
            String CurrencyCode ="";
            String SecurePage="";
            String amount="";
                if (jsonObject != null)
                {
                    if (jsonObject.has("ConfirmationNumber"))
                    {

                        ConfirmationNumber = jsonObject.getString("ConfirmationNumber");

                    }

                    if (jsonObject.has("TransactionId"))
                    {

                        TransactionId = jsonObject.getString("TransactionId");

                    }

                    if (jsonObject.has("PaymentStatus"))
                    {

                        PaymentStatus = jsonObject.getString("PaymentStatus");

                    }

                    if (jsonObject.has("Code"))
                    {

                        Code = jsonObject.getString("Code");

                    }

                    if (jsonObject.has("Description"))
                    {

                        Description = jsonObject.getString("Description");

                    }

                    if (jsonObject.has("TxnTime"))
                    {

                        TxnTime = jsonObject.getString("TxnTime");

                    }

                    if (jsonObject.has("SecurePage"))
                    {

                        SecurePage = jsonObject.getString("SecurePage");

                    }

                    if (jsonObject.has("CurrencyCode"))
                    {

                        CurrencyCode = jsonObject.getString("CurrencyCode");

                    }

                    if (jsonObject.has("TotalAmount"))
                    {

                        TotalAmount = jsonObject.getString("TotalAmount");
                        if ("JPY".equalsIgnoreCase(CurrencyCode))
                            amount = TotalAmount+".00";
                        else if ("KWD".equalsIgnoreCase(CurrencyCode))
                        {
                            Double amountD=Double.parseDouble(TotalAmount);
                            amount = String.format("%.2f", amountD / 1000);
                        }
                        else
                        {
                            Double amountD=Double.parseDouble(TotalAmount);
                            amount = String.format("%.2f", amountD / 100);
                        }
                    }

                }
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

                    transRespDetails.setTransactionId(ConfirmationNumber);
                    transRespDetails.setStatus(PaymentStatus);
                    transRespDetails.setRemark(Description);
                    transRespDetails.setDescriptor(billingDesc);
                    transRespDetails.setAmount(amount);
                    transRespDetails.setCurrency(CurrencyCode);

                    if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus) || (PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(dbStatus) && "successful".equalsIgnoreCase(PaymentStatus)))
                    {
                        notificationUrl=transactionDetailsVO.getNotificationUrl();
                        transactionLogger.debug("transactionStatus-----" + PaymentStatus);
                        transactionLogger.debug("respAmount-----" + amount);
                        StringBuffer dbBuffer = new StringBuffer();
                        if (functions.isValueNull(PaymentStatus) && PaymentStatus.equalsIgnoreCase("APPROVED"))
                        {
                            billingDesc=gatewayAccount.getDisplayName();
                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + ConfirmationNumber + "',status='capturesuccess' ,remark='" + ESAPI.encoder().encodeForSQL(me, Description) + "' where trackingid = " + trackingId);
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
                            FlutterWavePaymentGateway flutterWavePaymentGateway=new FlutterWavePaymentGateway(accountId);
                            CommRequestVO commRequestVO=new CommRequestVO();
                            CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
                            commTransactionDetailsVO.setOrderId(trackingId);
                            commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                            CommResponseVO commResponseVO= (CommResponseVO) flutterWavePaymentGateway.processInquiry(commRequestVO);

                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + ConfirmationNumber + "',remark='" + ESAPI.encoder().encodeForSQL(me, Description) + "' where trackingid = " + trackingId);
                            transactionLogger.error("-----dbBuffer-----" + dbBuffer);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                            actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                            updatedStatus = "authfailed";
                        }
                        AsynchronousMailService asynchronousMailService = AsynchronousMailService.getInstance();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), PaymentStatus, Description, billingDesc);

                        AsynchronousSmsService smsService = AsynchronousSmsService.getInstance();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), PaymentStatus, Description, billingDesc);

                        if (functions.isValueNull(notificationUrl))
                        {
                            transactionLogger.error("inside sending notification---" + notificationUrl);
                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, PaymentStatus,"");
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
