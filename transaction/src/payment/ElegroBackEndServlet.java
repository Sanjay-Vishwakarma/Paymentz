package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import org.codehaus.jettison.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

/**
 * Created by Admin on 1/19/2019.
 */
public class ElegroBackEndServlet extends HttpServlet
{
    //private static ElegroLogger transactionLogger= new ElegroLogger(ElegroBackEndServlet.class.getName());
    private static TransactionLogger transactionLogger= new TransactionLogger(ElegroBackEndServlet.class.getName());

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
        Functions functions = new Functions();
        TransactionManager transactionManager = new TransactionManager();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommResponseVO transRespDetails = null;
        Connection con = null;
        ActionEntry actionEntry = new ActionEntry();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        transactionLogger.error("-----inside ElegroBackEndServlet-----");
        for (Object key : request.getParameterMap().keySet())
        {
            transactionLogger.debug("----for loop ElegroBackEndServlet-----" + key + "=" + request.getParameter((String) key) + "----------");
        }


        PrintWriter pWriter = response.getWriter();
        String responseCode = "200";
        String responseStatus = "";
        StringBuilder responseMsg = new StringBuilder();
        BufferedReader br = request.getReader();
        String str;
        while ((str = br.readLine()) != null)
        {
            responseMsg.append(str);
        }

        String accountId="";
        String toId="";
        String dbStatus="";
        String notificationUrl="";
        String status="";
        String billingDesc="";
        String updatedStatus="";

        String trackingid="";
        String respStatus="";
        String respAmount="";
        String transactionId="";
        String email="";
        String message="";
        String coinType="";
        String tmpl_Amount="0.00";
        String tmpl_Currency="";
        String currency="";

        transactionLogger.error("-----Notification JSON-----" + responseMsg);
        try
        {
            if (functions.isValueNull(responseMsg.toString()) && responseMsg.toString().contains("{"))
            {

                JSONObject jsonObject= new JSONObject(responseMsg.toString());

                if(jsonObject!=null)
                {
                    if(jsonObject.has("orderId"))
                    {
                        trackingid=jsonObject.getString("orderId");
                    }
                    if(jsonObject.has("status"))
                    {
                        respStatus=jsonObject.getString("status");
                    }
                    if(jsonObject.has("amount"))
                    {
                        respAmount=jsonObject.getString("amount");
                    }
                    if(jsonObject.has("transactionId"))
                    {
                        transactionId=jsonObject.getString("transactionId");
                    }
                    if(jsonObject.has("email"))
                    {
                        email=jsonObject.getString("email");
                    }
                    if(jsonObject.has("error")){
                        message=jsonObject.getString("error");
                    }
                    if(jsonObject.has("coinType")){
                        coinType=jsonObject.getString("coinType");
                    }



                    TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);
                    if (transactionDetailsVO != null)
                    {
                        accountId = transactionDetailsVO.getAccountId();
                        toId = transactionDetailsVO.getToid();
                        dbStatus = transactionDetailsVO.getStatus();
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        tmpl_Amount=transactionDetailsVO.getTemplateamount();
                        tmpl_Currency=transactionDetailsVO.getTemplatecurrency();

                        if(functions.isValueNull(transactionDetailsVO.getCurrency())){
                            currency=transactionDetailsVO.getCurrency();
                        }

                        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

                        if (!functions.isValueNull(respAmount))
                        {
                            respAmount = transactionDetailsVO.getAmount();
                        }

                        if (!functions.isValueNull(message))
                        {
                            message = respStatus;
                        }
                        if (functions.isValueNull(respStatus))
                        {
                            transRespDetails = new CommResponseVO();

                            if (respStatus.equalsIgnoreCase("success"))
                            {
                                transRespDetails.setStatus("success");
                                transRespDetails.setDescriptor(gatewayAccount.getDisplayName());
                                transRespDetails.setDescription("SYS: Transaction Successful");
                            }
                            else
                            {
                                transRespDetails.setStatus("fail");
                                transRespDetails.setDescription("SYS: Transaction Failed");
                            }
                            transRespDetails.setWalletCurrecny(coinType);
                            transRespDetails.setRemark(message);
                            transRespDetails.setTransactionId(transactionId);
                            transRespDetails.setAmount(respAmount);
                            transRespDetails.setTmpl_Amount(tmpl_Amount);
                            transRespDetails.setTmpl_Currency(tmpl_Currency);
                            transRespDetails.setCurrency(currency);
                        }

                        auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                        auditTrailVO.setActionExecutorId(toId);
                        transactionLogger.debug("dbStatus------" + dbStatus);
                        if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus))
                        {
                            StringBuffer dbBuffer = new StringBuffer();
                            if ("success".equals(respStatus))
                            {
                                status = "success";
                                responseStatus = "Successful";
                                billingDesc = gatewayAccount.getDisplayName();

                                dbBuffer.append("update transaction_common set captureamount='" + respAmount + "',paymentid='" + transactionId + "',status='capturesuccess'");
                                actionEntry.actionEntryForCommon(trackingid, respAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);

                                statusSyncDAO.updateAllTransactionFlowFlag(trackingid, "capturesuccess");
                                updatedStatus = "capturesuccess";

                            }
                            else
                            {
                                status = "fail";
                                if (!functions.isValueNull(message))
                                {
                                    message = "fail";
                                }
                                responseStatus = "Failed(" + message + ")";
                                dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "'");
                                actionEntry.actionEntryForCommon(trackingid, respAmount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingid, "authfailed");
                                updatedStatus = "authfailed";
                            }
                            dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingid);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                            transactionLogger.debug("-----dbBuffer-----" + dbBuffer);
                            AsynchronousMailService asynchronousMailService = AsynchronousMailService.getInstance();
                            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), status, message, billingDesc);

                            AsynchronousSmsService smsService = AsynchronousSmsService.getInstance();
                            smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), status, message, billingDesc);

                            if (functions.isValueNull(notificationUrl))
                            {
                                transactionLogger.error("inside sending notification---" + notificationUrl);
                                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                asyncNotificationService.sendNotification(transactionDetailsVO, trackingid, updatedStatus, message, "");
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
        }
        catch (org.json.JSONException e)
        {
            transactionLogger.error("JSONException-----",e);
        }catch (PZDBViolationException e){
            PZExceptionHandler.raiseAndHandleDBViolationException("ElegroBackEndServlet.java", "doService()", null, "Transaction", null, null, null, e.getMessage(), e.getCause(), toId, null);
        } catch(SystemError se)
        {
            transactionLogger.error("SystemError::::::", se);
            PZExceptionHandler.raiseAndHandleDBViolationException("ElegroBackEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}