package payment;

import com.directi.pg.*;
import com.manager.MerchantConfigManager;
import com.manager.TransactionManager;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.statussync.StatusSyncDAO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Enumeration;

/**
 * Created by Sandip on 13/1/2016.
 */
public class PaySecBackendNotification extends PzServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(PaySecBackendNotification.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException
    {
        doService(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException
    {
        doService(request, response);
    }
    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException
    {

        Functions functions= new Functions();
        HttpSession session = request.getSession(true);
        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO = new AuditTrailVO();

        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        CommResponseVO commResponseVO = new CommResponseVO();

        MerchantDetailsVO merchantDetailsVO = null;
        Connection con = null;

        String toId = "";
        String isService = "";
        String accountId = "";
        String status = "";
        String response_Status = "";
        String amount = "";
        String dbStatus = "";
        String message = "";
        String eci = "";
        String transactionId = "";
        String notificationUrl="";
        String updatedStatus="";

        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String keyName = (String) enumeration.nextElement();
            String keyValue = request.getParameter(keyName);
            transactionLogger.error(keyName + ":" + keyValue);
        }

        String bankStatus = request.getParameter("status");
        String transactionReference = request.getParameter("transactionReference");
        String trackingId = request.getParameter("cartId");
        String orderAmount = request.getParameter("orderAmount");
        String currency = request.getParameter("currency");

        //String signature = request.getParameter(" signature");
        //String completedTime = request.getParameter("completedTime");
        //String orderAmount = request.getParameter("orderAmount");
        //String orderTime = request.getParameter("orderTime");
        //String currency = request.getParameter("currency");
        //String version = request.getParameter("version");
        //String statusMessage = request.getParameter("statusMessage");

        transactionId = transactionReference;
        try
        {
            if(functions.isValueNull(trackingId))
            {
                TransactionManager transactionManager = new TransactionManager();
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                amount = transactionDetailsVO.getAmount();
                toId = transactionDetailsVO.getToid();
                dbStatus = transactionDetailsVO.getStatus();
                notificationUrl = transactionDetailsVO.getNotificationUrl();

                MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
                merchantDetailsVO = merchantConfigManager.getMerchantDetailFromToId(toId);

                auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                auditTrailVO.setActionExecutorId(toId);
                if (merchantDetailsVO != null)
                {
                    isService = merchantDetailsVO.getService();
                }

                String transType = "sale";
                if ("N".equals(isService))
                {
                    transType = "auth";
                }

                try
                {
                    transactionLogger.debug("dbStatus-----" + dbStatus);
                    if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                    {
                        message = bankStatus;
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setTransactionType(transType);

                        StringBuffer dbBuffer = new StringBuffer();
                        if ("SUCCESS".equals(bankStatus))
                        {
                            transactionLogger.debug("-----inside success-----");
                            status = "success";
                            response_Status = "Successful";

                            if ("sale".equals(transType))
                            {
                                dbBuffer.append("update transaction_common set captureamount='" + orderAmount + "',paymentid='" + transactionId + "',status='capturesuccess',eci='" + eci + "'");
                                commResponseVO.setTransactionStatus(status);
                                commResponseVO.setDescription(response_Status);
                                commResponseVO.setStatus(response_Status);

                                entry.actionEntryForCommon(trackingId, orderAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                                updatedStatus = "capturesuccess";
                            }
                            else
                            {
                                dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful',eci='" + eci + "'");
                                commResponseVO.setTransactionStatus(status);
                                commResponseVO.setDescription(response_Status);
                                commResponseVO.setStatus(response_Status);
                                entry.actionEntryForCommon(trackingId, orderAmount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authsuccessful");
                                updatedStatus = "authsuccessful";
                            }
                        }
                        else
                        {
                            transactionLogger.debug("-----inside fail-----");
                            status = "fail";
                            response_Status = "Failed(" + message + ")";
                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',eci='" + eci + "'");
                            commResponseVO.setTransactionStatus(status);
                            commResponseVO.setDescription(response_Status);
                            commResponseVO.setStatus(response_Status);
                            entry.actionEntryForCommon(trackingId, orderAmount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                            updatedStatus = "authfailed";
                        }
                        dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                        transactionLogger.debug("sqlQuery-----" + dbBuffer.toString());
                        con = Database.getConnection();
                        Database.executeUpdate(dbBuffer.toString(), con);
                        if (functions.isValueNull(notificationUrl))
                        {
                            transactionLogger.error("inside sending notification---" + notificationUrl);
                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, message, "");
                        }
                    }
                    transactionLogger.debug("-----Notification Sendind--1---");
                    PrintWriter writer = response.getWriter();
                    response.setContentType("text/plain");
                    response.setStatus(HttpServletResponse.SC_OK);
                    writer.println("OK");
                    writer.flush();
                    transactionLogger.debug("-----Notification Sent-----");
                    return;

                }
                catch (SystemError se)
                {
                    transactionLogger.error("SystemError::::::", se);
                    PZExceptionHandler.raiseAndHandleDBViolationException("PaySecBackendNotification.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
                }
                finally
                {
                    Database.closeConnection(con);
                }
            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("PaySecBackendNotification.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }catch (Exception e){
            transactionLogger.error("Exception::::", e);
        }
    }
}
