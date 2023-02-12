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

import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

/**
 * Created by Rihen on 10-Aug-19.
 */
public class OnlinePayBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(OnlinePayBackEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.error("Inside OnlinePayBackEndServlet ---");

        TransactionManager transactionManager=new TransactionManager();
        CommResponseVO transRespDetails = new CommResponseVO();
        Functions functions=new Functions();
        PrintWriter pWriter = response.getWriter();
        String responseCode = "200";
        String responseStatus = "OK";
        AuditTrailVO auditTrailVO= new AuditTrailVO();
        ActionEntry actionEntry = new ActionEntry();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        Connection con=null;
        String query = "";
        String action = "";
        String trackingId = "";
        String toId="";
        String accountId = "";
        String transactionId = "";
        String status="";
        String message="";
        String dbStatus="";
        String billingDesc="";
        String notificationUrl="";
        String updatedStatus="";
        StringBuilder responseMsg=new StringBuilder();
        BufferedReader br = request.getReader();
        String str;
        while ((str = br.readLine()) != null)
        {
            responseMsg.append(str);
        }

        transactionLogger.error("-----Notification JSON-----" + responseMsg);
        try
        {
            JSONObject jsonObject = new JSONObject(responseMsg.toString());
            transactionLogger.error("in OnlinePayBackEndServlet --------->"+jsonObject.toString());

            if (jsonObject != null)
            {
                String ip = "";
                String resp_status = "";
                String amount = "";
                String currency = "";
                String decline_reason = "";
                String decline_code = "";

                if (jsonObject.has("payment_data"))
                {
                    JSONObject jsonPaymentData = jsonObject.getJSONObject("payment_data");

                    if (jsonPaymentData != null)
                    {
                        if (jsonPaymentData.has("id"))
                        {
                            transactionId = jsonPaymentData.getString("id");
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

                        if (jsonPaymentData.has("decline_reason"))
                        {
                            decline_reason = jsonPaymentData.getString("decline_reason");
                        }

                        if (jsonPaymentData.has("decline_code"))
                        {
                            decline_code = jsonPaymentData.getString("decline_code");
                        }

                    }
                }

                if (jsonObject.has("merchant_order"))
                {
                    JSONObject jsonMerchantOrder = jsonObject.getJSONObject("merchant_order");
                    if (jsonMerchantOrder != null)
                    {
                        if (jsonMerchantOrder.has("id"))
                        {
                            trackingId = jsonMerchantOrder.getString("id");
                        }
                    }
                }

                if (jsonObject.has("customer"))
                {
                    JSONObject jsonCustomer = jsonObject.getJSONObject("customer");
                    if (jsonCustomer != null)
                    {
                        if (jsonCustomer.has("ip"))
                        {
                            ip = jsonCustomer.getString("ip");
                        }
                    }
                }

                transactionLogger.error("------ OnlinePayBackEndServlet trackingID -------" + trackingId);
                transactionLogger.error("------ OnlinePayBackEndServlet resp_status -------" + resp_status);

                if(!"AUTHORIZED".equalsIgnoreCase(resp_status))
                {
                    TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                    if (transactionDetailsVO != null)
                    {
                        accountId = transactionDetailsVO.getAccountId();
                        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                        billingDesc = gatewayAccount.getDisplayName();

                        toId = transactionDetailsVO.getToid();
                        MerchantDetailsVO merchantDetailsVO = null;
                        MerchantDAO merchantDAO = new MerchantDAO();
                        merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                        dbStatus = transactionDetailsVO.getStatus();
                        notificationUrl = transactionDetailsVO.getNotificationUrl();

                        transactionLogger.debug("accountId----" + accountId);
                        transactionLogger.debug("toId----" + toId);
                        transactionLogger.debug("dbStatus----" + dbStatus);

                        auditTrailVO.setActionExecutorName("AcquirerCommonBackEnd");
                        auditTrailVO.setActionExecutorId(toId);
                        transRespDetails.setTransactionId(transactionId);
                        transRespDetails.setIpaddress(ip);
                        transRespDetails.setAmount(amount);
                        transRespDetails.setCurrency(currency);
                        if(functions.isValueNull(transactionDetailsVO.getTemplateamount())) {
                            transRespDetails.setTmpl_Amount(transactionDetailsVO.getTemplateamount());
                        }
                        else{
                            transRespDetails.setTmpl_Amount(amount);
                        }
                        if(functions.isValueNull(transactionDetailsVO.getTemplatecurrency())) {
                            transRespDetails.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());
                        }
                        else {
                            transRespDetails.setTmpl_Currency(currency);
                        }


                        if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && resp_status.equalsIgnoreCase("COMPLETED"))
                        {
                            transactionLogger.debug("-----inside if status -----" + resp_status);
                            status = "success";
                            transRespDetails.setStatus("success");
                            transRespDetails.setDescriptor(gatewayAccount.getDisplayName());
                            message = "Transaction Successful";
                            transRespDetails.setRemark(message);
                            transRespDetails.setDescription(message);
                            updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                            action = ActionEntry.ACTION_CAPTURE_SUCCESSFUL.toString();
                            query = "update transaction_common set status='" + updatedStatus + "',remark='" + message + "',paymentid='" + transactionId + "',captureamount='" + amount + "' where trackingid='" + trackingId + "'";
                            transactionLogger.error("-----query-----" + query);
                            con = Database.getConnection();
                            Database.executeUpdate(query.toString(), con);
                            actionEntry.actionEntryForCommon(trackingId, amount, action, updatedStatus, transRespDetails, auditTrailVO, ip);
                        }
                        else if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && resp_status.equalsIgnoreCase("DECLINED"))
                        {
                            transactionLogger.debug("-----inside else if status -----" + resp_status);
                            status = "fail";
                            transRespDetails.setStatus("failed");
                            message = "Transaction Declined";
                            transRespDetails.setRemark(message);
                            transRespDetails.setDescription(decline_reason);
                            transRespDetails.setErrorCode(decline_code);
                            updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                            action = ActionEntry.ACTION_AUTHORISTION_FAILED.toString();
                            query = "update transaction_common set status='" + updatedStatus + "',remark='" + message + "',paymentid='" + transactionId +"' where trackingid='" + trackingId + "'";
                            transactionLogger.error("-----query-----" + query);
                            con = Database.getConnection();
                            Database.executeUpdate(query.toString(), con);
                            actionEntry.actionEntryForCommon(trackingId, amount, action, updatedStatus, transRespDetails, auditTrailVO, ip);
                        }
                        else if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                        {
                            status = "success";
                            message = "Transaction Successful";
                            updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        }
                        else
                        {
                            status = "fail";
                            message = "Transaction Declined";
                            updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                        }

                        Database.closeConnection(con);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);


                        AsynchronousMailService asynchronousMailService = AsynchronousMailService.getInstance();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                        AsynchronousSmsService smsService = AsynchronousSmsService.getInstance();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);


                        if (functions.isValueNull(notificationUrl))
                        {
                            transactionLogger.error("inside sending notification---" + notificationUrl);
                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
                            asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, message, "");
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
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError In OnlinePayBackEndServlet---" , systemError);
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException In OnlinePayBackEndServlet---" , e);
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException In OnlinePayBackEndServlet---" , e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}

