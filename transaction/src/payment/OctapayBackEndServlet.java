package payment;

/**
 * Created by Admin on 12/19/2020.
 */

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.statussync.StatusSyncDAO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
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


public class OctapayBackEndServlet extends HttpServlet
{
    TransactionLogger transactionLogger= new TransactionLogger(OctapayBackEndServlet.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        transactionLogger.error("---- Inside OctapayBackEndServlet ---");
        Connection con = null;
        Functions functions = new Functions();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        TransactionManager transactionManager = new TransactionManager();
        MerchantDAO merchantDAO = new MerchantDAO();
        ActionEntry entry = new ActionEntry();
        CommResponseVO transRespDetails = new CommResponseVO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        PrintWriter pWriter = response.getWriter();
        String trackingId =request.getParameter("trackingId");
        StringBuilder responseMsg = new StringBuilder();

        MySQLCodec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        try
        {

            Enumeration enumeration = request.getParameterNames();
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

                String remark = "";
                String transactionId = "";
                String tradeStatus = "";
                String reason = "";
                String email = "";
                String test = "";
                String transaction_date = "";
                if (jsonObject != null)
                {

                    if (jsonObject.has("order_id"))
                    {
                        transactionId = jsonObject.getString("order_id");
                    }
                    if (jsonObject.has("customer_order_id") && !functions.isValueNull(trackingId))
                    {
                        trackingId = jsonObject.getString("customer_order_id");
                    }
                    if (jsonObject.has("transaction_status"))
                    {
                        tradeStatus = jsonObject.getString("transaction_status");
                    }
                    if (jsonObject.has("reason"))
                    {
                        remark = jsonObject.getString("reason");
                    }
                    if (jsonObject.has("email"))
                    {
                        email = jsonObject.getString("email");
                    }
                    if (jsonObject.has("test"))
                    {
                        test = jsonObject.getString("test");
                    }
                    if (jsonObject.has("transaction_date"))
                    {
                        transaction_date = jsonObject.getString("transaction_date");
                    }


                    String currency = "";
                    String status = "";
                    String message = "";
                    String toId = "";
                    String accountId = "";
                    String dbStatus = "";
                    String billingDesc = "";
                    String notificationUrl = "";
                    String description = "";
                    String amount = "";
                    String tmpl_amt = "";
                    String tmpl_currency = "";
                    transactionLogger.error("trackingId-->" + trackingId);
                    transactionLogger.error("transactionId-->" + transactionId);
                    transactionLogger.error("status-->" + status);
                    transactionLogger.error("tradeStatus-->" + tradeStatus);
                    transactionLogger.error("remark-->" + remark);

                    if (functions.isValueNull(trackingId))
                    {

                        TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                        if (transactionDetailsVO != null)
                        {
                            toId = transactionDetailsVO.getToid();
                            accountId = transactionDetailsVO.getAccountId();
                            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                            dbStatus = transactionDetailsVO.getStatus();
                            amount = transactionDetailsVO.getAmount();
                            currency = transactionDetailsVO.getCurrency();
                            tmpl_amt = transactionDetailsVO.getTemplateamount();
                            tmpl_currency = transactionDetailsVO.getTemplatecurrency();

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
                            if ((PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus)))
                            {
                                auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                                auditTrailVO.setActionExecutorId(toId);
                                StringBuffer dbBuffer = new StringBuffer();
                                transRespDetails = new CommResponseVO();
                                transRespDetails.setTransactionId(transactionId);
                                transRespDetails.setTmpl_Amount(tmpl_amt);
                                transRespDetails.setTmpl_Currency(tmpl_currency);
                                transRespDetails.setCurrency(currency);

                                if (tradeStatus.equalsIgnoreCase("success"))
                                {
                                    notificationUrl = transactionDetailsVO.getNotificationUrl();
                                    transactionLogger.error("Inside AUTH_STARTED ");

                                    billingDesc = gatewayAccount.getDisplayName();
                                    status = "success";
                                    if (functions.isValueNull(remark))
                                    {
                                        message = remark;
                                    }
                                    else
                                    {
                                        message = "Transaction Successful";
                                    }

                                    dbStatus = "capturesuccess";
                                    transRespDetails.setStatus(status);
                                    transRespDetails.setRemark(message);
                                    transRespDetails.setDescriptor(billingDesc);
                                    transRespDetails.setDescription(message);
                                    transRespDetails.setBankTransactionDate(transaction_date);


                                    dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess',remark='" + message + "' where trackingid = " + trackingId);
                                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                                }
                                else
                                {
                                    notificationUrl = transactionDetailsVO.getNotificationUrl();
                                    transactionLogger.error("Inside AUTH_STARTED ");

                                    status = "failed";

                                    if (functions.isValueNull(remark))
                                    {
                                        message = remark;
                                    }
                                    else
                                    {
                                        message = "Transaction failed";
                                    }

                                    dbStatus = "authfailed";
                                    transRespDetails.setStatus(status);
                                    transRespDetails.setRemark(message);
                                    transRespDetails.setDescription(message);
                                    transRespDetails.setBankTransactionDate(transaction_date);


                                    dbBuffer.append("update transaction_common set paymentid='" + transactionId + "',status='authfailed',remark='" + message + "' where trackingid = " + trackingId);
                                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                                }


                            }

                            if (functions.isValueNull(notificationUrl))
                            {
                                transactionLogger.error("Inside sending notification---" + notificationUrl);
                                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                transactionDetailsVO.setBillingDesc(billingDesc);
                                asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, dbStatus, message, "");
                            }
                        }


                        String responseCode = "200";
                        String responseStatus = "success";
                        response.setStatus(200);
                        pWriter.println(responseStatus);
                        pWriter.flush();
                        return;
                    }

                }
            }
        }
            catch (PZDBViolationException e)
            {
                transactionLogger.error("PZDBViolationException -----" + trackingId + "-----", e);
            }
            catch (JSONException e)
            {

                transactionLogger.error("JSONException -----" + trackingId + "-----", e);
            }
            catch (SystemError systemError)
            {

                transactionLogger.error("systemError -----" + trackingId + "-----", systemError);
            }


        finally
        {
            Database.closeConnection(con);
        }
    }
    }
