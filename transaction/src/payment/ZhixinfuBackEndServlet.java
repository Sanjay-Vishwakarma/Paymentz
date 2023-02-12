package payment;

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
import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.esapi.codecs.MySQLCodec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Enumeration;


/**
 * Created by Admin on 11/24/2020.
 */

public class ZhixinfuBackEndServlet extends HttpServlet
{
    TransactionLogger transactionLogger= new TransactionLogger(ZhixinfuBackEndServlet.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        transactionLogger.error("---- Inside ZhixinfuBackEndServlet ---");
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
        String trackingId = request.getParameter("merOrdId");
        MySQLCodec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        try
        {

            Enumeration enumeration = request.getParameterNames();
            while (enumeration.hasMoreElements())
            {
                String key = (String) enumeration.nextElement();
                String value = request.getParameter(key);

                transactionLogger.error("Name=" + key + "-----Value=" + value);
            }

            String tradeStatus = request.getParameter("tradeStatus");
            String remark=request.getParameter("remark");
            String code=request.getParameter("code");
            String transactionId=request.getParameter("sysOrdId");


                String currency = "";
                String status = "";
                String message = "";
                String toId = "";
                String accountId = "";
                String dbStatus = "";
                String billingDesc = "";
                String notificationUrl = "";
                String description = "";
                String amount="";
                String tmpl_amt="";
                String tmpl_currency="";
                transactionLogger.error("trackingId-->" + trackingId);
                transactionLogger.error("transactionId-->" + transactionId);
                transactionLogger.error("status-->" + status);

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
                                tmpl_amt=transactionDetailsVO.getTemplateamount();
                                tmpl_currency=transactionDetailsVO.getTemplatecurrency();

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
                                    if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                                    {
                                        transRespDetails = new CommResponseVO();
                                        transRespDetails.setTransactionId(transactionId);
                                        transRespDetails.setTmpl_Amount(tmpl_amt);
                                        transRespDetails.setTmpl_Currency(tmpl_currency);
                                        transRespDetails.setCurrency(currency);

                                        if (tradeStatus.equalsIgnoreCase("completed"))
                                        {
                                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                                            transactionLogger.error("Inside AUTH_STARTED ");

                                            billingDesc = gatewayAccount.getDisplayName();
                                            status = "success";
                                            if(functions.isValueNull(remark))
                                            {
                                                message=remark;
                                            }
                                            else
                                            {
                                                message = "Transaction Successful";
                                            }

                                            dbStatus = "capturesuccess";
                                            transRespDetails.setStatus(status);
                                            transRespDetails.setRemark(message);
                                            transRespDetails.setDescriptor(billingDesc);
                                            transRespDetails.setDescription(description);


                                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess',remark='" + message +  "' where trackingid = " + trackingId);
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

                                            if(functions.isValueNull(remark))
                                            {
                                                message=remark;
                                            }
                                            else
                                            {
                                                message = "Transaction failed";
                                            }

                                            dbStatus = "authfailed";
                                            transRespDetails.setStatus(status);
                                            transRespDetails.setRemark(message);
                                            transRespDetails.setDescription(description);


                                            dbBuffer.append("update transaction_common set paymentid='" + transactionId + "',status='authfailed',remark='" + message + "' where trackingid = " + trackingId);
                                            transactionLogger.error("update query------------>" + dbBuffer.toString());
                                            Database.executeUpdate(dbBuffer.toString(), con);
                                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                                        }
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
                            String responseStatus = "notifysuccess";
                            response.setStatus(200);
                            pWriter.println(responseStatus);
                            pWriter.flush();
                            return;

                            }
                        }
        catch (PZDBViolationException e1)
        {
            transactionLogger.error("PZDBViolationException -----" + trackingId + "-----", e1);
        }
        catch (SystemError systemError1)
        {
            transactionLogger.error("SystemError -----" + trackingId + "-----", systemError1);
        }

        finally
            {
                Database.closeConnection(con);
            }
        }
}