package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.statussync.StatusSyncDAO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

/**
 * Created by Admin on 10/1/18.
 */
public class JetonBackEndServlet extends HttpServlet
{
    TransactionLogger transactionLogger = new TransactionLogger(JetonBackEndServlet.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.error("-----inside JetonBackEndServlet-----");
        Connection con = null;
        Functions functions = new Functions();
        StringBuilder responseMsg = new StringBuilder();
        PaymentManager paymentManager = new PaymentManager();
        try
        {
            BufferedReader br = request.getReader();
            String str;
            while((str = br.readLine()) != null ){
                responseMsg.append(str);
            }

            transactionLogger.error("-----responseMsg-----"+responseMsg.toString());

            if(responseMsg!=null)
            {
                JSONObject jsonObject = new JSONObject(responseMsg.toString());
                String trackingId = "";
                String transactionId = "";
                String type = "";
                String customer = "";
                String paymentAmount = "";
                String currecny = "";
                String status = "";
                String message = "";
                String toId = "";
                String accountId = "";
                String dbStatus = "";
                String billingDesc = "";
                String confirmStatus = "";
                String notificationUrl = "";
                String updatedStatus = "";

                if (jsonObject != null)
                {

                    if (jsonObject.has("orderId"))
                        trackingId = jsonObject.getString("orderId");
                    if (jsonObject.has("paymentId"))
                        transactionId = jsonObject.getString("paymentId");
                    if (jsonObject.has("type"))
                        type = jsonObject.getString("type");
                    if (jsonObject.has("customer"))
                        customer = jsonObject.getString("customer");
                    if (jsonObject.has("amount"))
                        paymentAmount = jsonObject.getString("amount");
                    if (jsonObject.has("currency"))
                        currecny = jsonObject.getString("currency");
                    if (jsonObject.has("status"))
                        status = jsonObject.getString("status");
                    if (jsonObject.has("message"))
                        message = jsonObject.getString("message");

                    if (functions.isValueNull(type))
                    {
                        if (type.equalsIgnoreCase("PAY"))
                        {
                            type = PZProcessType.SALE.toString();
                        }
                        else
                        {
                            type = PZProcessType.PAYOUT.toString();
                        }
                    }

                    if (functions.isValueNull(trackingId))
                    {
                        TransactionManager transactionManager = new TransactionManager();
                        ActionEntry entry = new ActionEntry();
                        CommResponseVO commResponseVO = new CommResponseVO();
                        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
                        AuditTrailVO auditTrailVO = new AuditTrailVO();
                        PrintWriter pWriter = response.getWriter();
                        TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                        if (transactionDetailsVO != null)
                        {
                            toId = transactionDetailsVO.getToid();
                            accountId = transactionDetailsVO.getAccountId();
                            dbStatus = transactionDetailsVO.getStatus();
                            notificationUrl = transactionDetailsVO.getNotificationUrl();

                            if(functions.isValueNull(transactionDetailsVO.getCcnum()))
                                transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                            if(functions.isValueNull(transactionDetailsVO.getExpdate()))
                            {
                                transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));
                            }


                            String tmpl_Amount = "";
                            if (!transactionDetailsVO.getAmount().equalsIgnoreCase(paymentAmount))
                            {
                                tmpl_Amount = String.valueOf((Double.parseDouble(transactionDetailsVO.getTemplateamount()) * Double.parseDouble(paymentAmount)) / Double.parseDouble(transactionDetailsVO.getAmount()));
                            }
                            else
                            {
                                tmpl_Amount = transactionDetailsVO.getTemplateamount();
                            }

                            transactionLogger.error("tmpl_Amount------" + tmpl_Amount);
                            transactionLogger.error("dbStatus------" + dbStatus);
                            if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.PAYOUT_STARTED.toString().equalsIgnoreCase(dbStatus))
                            {

                                if (functions.isValueNull(status))
                                {
                                    auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                                    auditTrailVO.setActionExecutorId(toId);
                                    StringBuffer dbBuffer = new StringBuffer();
                                    con = Database.getConnection();

                                    if (type.equalsIgnoreCase("sale"))
                                    {
                                        if ("success".equalsIgnoreCase(status))
                                        {
                                            status = "success";
                                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                                            commResponseVO.setDescription(message);
                                            commResponseVO.setStatus(status);
                                            commResponseVO.setRemark(message);
                                            commResponseVO.setDescriptor(billingDesc);
                                            commResponseVO.setCurrency(currecny);
                                            commResponseVO.setTmpl_Amount(tmpl_Amount);
                                            commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());
                                            commResponseVO.setTransactionType(type);

                                            confirmStatus = "Y";
                                            dbStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();


                                            dbBuffer.append("update transaction_common set captureamount='" + paymentAmount + "',currency='" + currecny + "' ,paymentid='" + transactionId + "',status='capturesuccess'");
                                            entry.actionEntryForCommon(trackingId, paymentAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                            updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                        }
                                        else
                                        {
                                            confirmStatus = "N";
                                            status = "fail";
                                            commResponseVO.setStatus(status);
                                            commResponseVO.setDescription(message);
                                            commResponseVO.setRemark(message);
                                            commResponseVO.setCurrency(currecny);
                                            commResponseVO.setTmpl_Amount(tmpl_Amount);
                                            commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());
                                            commResponseVO.setTransactionType(type);
                                            dbStatus = PZTransactionStatus.AUTH_FAILED.toString();
                                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "'");
                                            entry.actionEntryForCommon(trackingId, paymentAmount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                                            updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                                        }
                                    }
                                    else if (type.equalsIgnoreCase("payout"))
                                    {
                                        if ("success".equalsIgnoreCase(status))
                                        {
                                            status = "success";
                                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                                            commResponseVO.setDescription(message);
                                            commResponseVO.setStatus(status);
                                            commResponseVO.setRemark(message);
                                            commResponseVO.setDescriptor(billingDesc);
                                            commResponseVO.setCurrency(currecny);
                                            commResponseVO.setTmpl_Amount(tmpl_Amount);
                                            commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());
                                            commResponseVO.setTransactionType(type);

                                            confirmStatus = "Y";
                                            dbStatus = PZTransactionStatus.PAYOUT_SUCCESS.toString();
                                            dbBuffer.append("update transaction_common set payoutamount='" + paymentAmount + "',currency='" + currecny + "' ,paymentid='" + transactionId + "',status='payoutsuccessful' ");
                                            entry.actionEntryForCommon(trackingId, paymentAmount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                            updatedStatus = PZTransactionStatus.PAYOUT_SUCCESS.toString();
                                        }
                                        else
                                        {
                                            confirmStatus = "N";
                                            status = "fail";
                                            commResponseVO.setStatus(status);
                                            commResponseVO.setDescription(message);
                                            commResponseVO.setRemark(message);
                                            commResponseVO.setCurrency(currecny);
                                            commResponseVO.setTmpl_Amount(tmpl_Amount);
                                            commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());
                                            commResponseVO.setTransactionType(type);
                                            dbStatus = PZTransactionStatus.PAYOUT_FAILED.toString();
                                            dbBuffer.append("update transaction_common set status='payoutfailed',paymentid='" + transactionId + "'");
                                            entry.actionEntryForCommon(trackingId, paymentAmount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                                            updatedStatus = PZTransactionStatus.PAYOUT_FAILED.toString();
                                        }
                                    }
                                    dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                                    paymentManager.insertJetonDetails(trackingId, status, transactionId, customer);
                                }
                            }
                            if (!functions.isValueNull(updatedStatus))
                            {
                                updatedStatus = dbStatus;
                            }
                            if (functions.isValueNull(notificationUrl))
                            {
                                transactionLogger.error("inside sending notification---" + notificationUrl);
                                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, message, "");
                            }
                            response.setStatus(200);
                            pWriter.flush();
                            return;
                        }
                    }
                }
            }
        }catch (JSONException e)
        {
            transactionLogger.error("SystemError in JetonBackEndServlet---", e);
        }catch (SystemError e)
        {
            transactionLogger.error("SystemError in JetonBackEndServlet---", e);
        }catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException in JetonBackEndServlet---",e);
        }catch (IOException e)
        {
            transactionLogger.error("PZDBViolationException in JetonBackEndServlet---",e);
        } finally
        {
            Database.closeConnection(con);
        }
    }
}
