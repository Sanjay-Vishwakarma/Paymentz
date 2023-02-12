package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.statussync.StatusSyncDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by uday on 11/2/17.
 */
public class EpayBackEndServlet extends PzServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(EpayBackEndServlet.class.getName());

    public static Map<String, String> getDecoded(String query)
    {
        String[] params = query.split(":");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String[] p = param.split("=");
            String name = p[0];
            if (p.length > 1)
            {
                String value = p[1];
                map.put(name, value);
                transactionLogger.debug(name + ":::" + value);
            }
        }
        return map;
    }

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

        transactionLogger.error("Entering doPost in EpayBackEndServlet::::" + request.getRemoteAddr());

        for (Object key : request.getParameterMap().keySet())
        {
            transactionLogger.error("----for loop EpayBackEndServlet-----" + key + "=" + request.getParameter((String) key) + "--------------");
        }

        if (!request.getParameterMap().isEmpty())
        {

            String encoded = request.getParameter("encoded");
            String checksum = request.getParameter("checksum");

            String decoded = new String(com.directi.pg.Base64.decode(encoded));
            transactionLogger.error("decoded:::::" + decoded);

            CommResponseVO commResponseVO = new CommResponseVO();
            TransactionManager transactionManager = new TransactionManager();
            Functions functions = new Functions();
            StringBuffer dbBuffer = new StringBuffer();
            ActionEntry entry = new ActionEntry();
            PaymentManager paymentManager = new PaymentManager();
            StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
            AuditTrailVO auditTrailVO = new AuditTrailVO();
            auditTrailVO.setActionExecutorName("AcquirerBackEnd");
            Connection con = null;
            int notificationCount = 0;

            Map<String, String> responseMap = EpayBackEndServlet.getDecoded(decoded);
            try
            {
                if (responseMap != null)
                {
                    String trackingId = responseMap.get("INVOICE");
                    String status = responseMap.get("STATUS");
                    String payTime = responseMap.get("PAY_TIME");
                    String stan = responseMap.get("STAN");
                    String bcode = responseMap.get("BCODE");

                    con = Database.getConnection();
                    TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

                    if (transactionDetailsVO != null)
                    {
                        String dbStatus = transactionDetailsVO.getStatus();
                        String amount = transactionDetailsVO.getAmount();
                        String accountId = transactionDetailsVO.getAccountId();

                        String updateStatus = "";
                        String remark = "";
                        String notificationStatus = "";

                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        Date date = new Date();

                        if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) || PZTransactionStatus.PAYOUT_STARTED.toString().equals(dbStatus))
                        {
                            transactionLogger.debug("-------inside authstarted------" + status);
                            dbBuffer.append("update transaction_common set ");
                            //if ("DENIED".contains(status))
                            if (status.contains("DENIED"))
                            {
                                transactionLogger.debug("inside---" + status);
                                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                                {
                                    remark = "Transaction Fail";
                                    updateStatus = "authfailed";
                                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                                    commResponseVO.setTransactionId(stan);
                                    commResponseVO.setErrorCode(bcode);
                                    commResponseVO.setTransactionType("sale");
                                    commResponseVO.setDescription(status);
                                    commResponseVO.setRemark(remark);
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.AUTH_FAILED.toString());
                                }
                                if (PZTransactionStatus.PAYOUT_STARTED.toString().equals(dbStatus))
                                {
                                    remark = "Payout Fail";
                                    updateStatus = "payoutfailed";

                                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                                    commResponseVO.setTransactionId(stan);
                                    commResponseVO.setErrorCode(bcode);
                                    commResponseVO.setTransactionType("payout");
                                    commResponseVO.setDescription(status);
                                    commResponseVO.setRemark(remark);
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.STATUS_PAYOUT_FAILED, commResponseVO, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.PAYOUT_FAILED.toString());
                                }
                                transactionLogger.debug("updatStatus:::::" + updateStatus);
                                status = "NO";
                                dbBuffer.append("status='" + updateStatus + "',paymentid='" + stan + "',remark='" + remark + "'");
                            }
                            if ("PAID".equals(status))
                            {
                                transactionLogger.debug("inside---" + status);
                                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                                {
                                    remark = "Transaction Successful";
                                    updateStatus = "capturesuccess";

                                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                                    commResponseVO.setTransactionId(stan);
                                    commResponseVO.setErrorCode(bcode);
                                    commResponseVO.setTransactionType("sale");
                                    commResponseVO.setDescription(status);
                                    commResponseVO.setRemark(remark);
                                    commResponseVO.setStatus("success");
                                    commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.CAPTURE_SUCCESS.toString());
                                }
                                if (PZTransactionStatus.PAYOUT_STARTED.toString().equals(dbStatus))
                                {
                                    remark = "Payout Successful";
                                    updateStatus = "payoutsuccessful";

                                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                                    commResponseVO.setTransactionId(stan);
                                    commResponseVO.setErrorCode(bcode);
                                    commResponseVO.setTransactionType("payout");
                                    commResponseVO.setDescription(status);
                                    commResponseVO.setRemark(remark);
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.PAYOUT_SUCCESS.toString());
                                }
                                transactionLogger.debug("updatStatus:::::" + updateStatus);

                                status = "OK";
                                dbBuffer.append("notificationCount=1,captureamount='" + amount + "', status='" + updateStatus + "', paymentid='" + stan + "',    remark='" + remark + "'");
                            }

                            if ("EXPIRED".equalsIgnoreCase(status))
                            {
                                transactionLogger.debug("inside---" + status);
                                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                                {
                                    updateStatus = "authfailed";

                                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                                    commResponseVO.setTransactionId(stan);
                                    commResponseVO.setErrorCode(bcode);
                                    commResponseVO.setTransactionType("sale");
                                    commResponseVO.setDescription(remark);
                                    commResponseVO.setRemark(remark);
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.AUTH_FAILED.toString());
                                }
                                if (PZTransactionStatus.PAYOUT_STARTED.toString().equals(dbStatus))
                                {
                                    updateStatus = "payoutfailed";

                                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                                    commResponseVO.setTransactionId(stan);
                                    commResponseVO.setErrorCode(bcode);
                                    commResponseVO.setDescription(status);
                                    commResponseVO.setTransactionType("payout");
                                    commResponseVO.setRemark("Transaction Expired");
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.STATUS_PAYOUT_FAILED, commResponseVO, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.PAYOUT_FAILED.toString());
                                }
                                transactionLogger.debug("updatStatus:::::" + updateStatus);
                                status = "ERR";
                                dbBuffer.append("status='" + updateStatus + "',remark='Transaction Expired'");
                            }
                            dbBuffer.append(" where trackingid=" + trackingId + "");

                            int result = Database.executeUpdate(dbBuffer.toString(), con);

                            transactionLogger.debug("is result---" + result);
                        }
                        if (functions.isValueNull(transactionDetailsVO.getNotificationUrl()))
                        {

                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            asyncNotificationService.sendNotification(transactionDetailsVO,trackingId,updateStatus,remark,"EPAY");
                        }
                        transactionLogger.debug("-----dbBuffer-----" + dbBuffer);

                        paymentManager.updateExtensionforEpay(trackingId, status, payTime, stan, bcode);

                        PrintWriter pWriter = response.getWriter();
                        pWriter.println("INVOICE=" + trackingId + ":STATUS=" + status + "");
                        pWriter.flush();
                        return;
                    }
                }
            }
            catch (PZDBViolationException e)
            {
                transactionLogger.error("PZDBViolationException::::::", e);
            }
            catch (SystemError systemError)
            {
                transactionLogger.error("SystemError:::::", systemError);
            }
            finally
            {
                Database.closeConnection(con);
            }
        }
    }
}