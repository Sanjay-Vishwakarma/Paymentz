package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.statussync.StatusSyncDAO;
import com.payment.trustly.api.NotificationHandler;
import com.payment.trustly.api.commons.Method;
import com.payment.trustly.api.commons.ResponseStatus;
import com.payment.trustly.api.data.notification.Notification;
import com.payment.trustly.api.data.notification.notificationdata.*;
import com.payment.trustly.api.data.response.Response;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Uday on 11/24/17.
 */
public class TrustlyBackEndServlet extends PzServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(TrustlyBackEndServlet.class.getName());

    public  int   dbUpdate(String tracking,String dbstatus,String paymentid,String remark,String captureamount,int notificationCount) throws PZDBViolationException{
        Connection conn=null;
        PreparedStatement ps=null;
        int i = 0;
        try{
            conn=Database.getConnection();
            String updateQuery="Update transaction_common Set status=?,paymentid=?,remark=?,captureamount=?,notificationCount=? where trackingid=?";
            ps=conn.prepareStatement(updateQuery);
            ps.setString(1,dbstatus);
            ps.setString(2,paymentid);
            ps.setString(3,remark);
            ps.setString(4,captureamount);
            ps.setInt(5, notificationCount);
            ps.setString(6,tracking);
            i = ps.executeUpdate();

        }catch (SystemError systemError){
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "insertTrustlyBankDetails()", null, "Common", "SystemError Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }catch(SQLException e){
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java","insertTrustlyBankDetails()",null,"Common","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }finally
        {
            Database.closeConnection(conn);
        }
        return i;
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
        CommResponseVO commResponseVO = new CommResponseVO();
        TransactionManager transactionManager = new TransactionManager();
        StringBuffer dbBuffer = new StringBuffer();
        ActionEntry entry = new ActionEntry();
        PaymentManager paymentManager = new PaymentManager();
        Functions functions = new Functions();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        auditTrailVO.setActionExecutorName("AcquirerBackEnd");
        Connection con = null;
        int notificationCount = 0;


        transactionLogger.error("Entering doPost in TrustlyBackEndServlet::::" + request.getRemoteAddr());

        for (Object key : request.getParameterMap().keySet())
        {
            transactionLogger.error("----for loop TrustlyBackEndServlet-----" + key + "=" + request.getParameter((String) key) + "--------------");
        }


        StringBuilder responseMsg = new StringBuilder();
        BufferedReader br = request.getReader();
        String str = "";
        while ((str = br.readLine()) != null)
        {
            responseMsg.append(str);
        }

        transactionLogger.error("-----payment confirmation response-----" + responseMsg);

        if (responseMsg != null)
        {

            Notification notification = new Notification();
            NotificationHandler notificationHandler = new NotificationHandler();

            notification = notificationHandler.handleNotification(responseMsg.toString());
            AccountNotificationData accountNotificationData = null;
            PendingNotificationData pendingNotificationData = null;
            CreditData creditData = null;
            DebitNotificationData debitNotificationData = null;
            CancelNotificationData cancelNotificationData = null;


            transactionLogger.error("-----inside-----notificationHandler-----");


            String trackingId = notification.getParams().getData().getMessageId();

            int result = 0;
            try
            {
                con = Database.getConnection();
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                String dbStatus = transactionDetailsVO.getStatus();
                String amount = transactionDetailsVO.getAmount();
                String accountId = transactionDetailsVO.getAccountId();

                transactionLogger.debug("trackingid----" + trackingId);
                transactionLogger.debug("dbStatus------" + dbStatus);
                transactionLogger.debug("amount------" + amount);

                String updateStatus = "";
                String remark = "";

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date = new Date();

                if (notification.getMethod() != null)
                {


                    //dbBuffer.append("update transaction_common set ");

                    if (notification.getMethod().equals(Method.ACCOUNT))
                    {
                        if (notification.getParams().getData() instanceof AccountNotificationData)
                        {
                            transactionLogger.error("inside instence of AccountNotificationData---");
                            accountNotificationData = (AccountNotificationData) notification.getParams().getData();
                            transactionLogger.error("amount acc---" + accountNotificationData.getVerified());
                        }

                        transactionLogger.debug("inside---" + notification.getMethod());
                        transactionLogger.debug("Verified---" + accountNotificationData.getVerified());
                        boolean verified = accountNotificationData.getVerified();
                        if (verified)
                        {
                            transactionLogger.debug("-----Account Verified-----" + accountNotificationData.getAttributes());

                            String queryResult = insertTrustlyBankDetails(accountNotificationData);
                            transactionLogger.error("Query Execution Account-----" + queryResult);

                            if (queryResult.equalsIgnoreCase("success"))
                            {
                                Response response1 = notificationHandler.prepareNotificationResponse(notification.getMethod(), notification.getUUID(), ResponseStatus.OK);

                                transactionLogger.debug("-----after getting ResponseVO-------");

                                String jsonNotificationResponse = notificationHandler.toJson(response1);

                                transactionLogger.error("jsonNotificationResponse account--" + trackingId + "---" + jsonNotificationResponse);

                                PrintWriter printWriter = response.getWriter();
                                printWriter.println(jsonNotificationResponse);
                                printWriter.flush();
                            }
                        }
                    }


                    if (notification.getMethod().equals(Method.PENDING))
                    {
                        transactionLogger.debug("inside---" + notification.getMethod());

                        if (notification.getParams().getData() instanceof PendingNotificationData)
                        {
                            transactionLogger.error("inside instence of PendingNotificationData---");
                            pendingNotificationData = (PendingNotificationData) notification.getParams().getData();
                            transactionLogger.error("amount pending---" + pendingNotificationData.getAmount());
                        }


                        if (pendingNotificationData.getAmount().equals(amount))
                        {
                            transactionLogger.debug("-----inside--" + dbStatus + "------");
                            int i = 0;
                            if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                            {
                                remark = "Transaction Pending";
                                updateStatus = "capturestarted";
                                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                                commResponseVO.setTransactionId(notification.getParams().getData().getOrderId());
                                commResponseVO.setTransactionType("sale");
                                commResponseVO.setDescription(Method.PENDING.toString());
                                commResponseVO.setRemark(remark);

                                transactionLogger.debug("updatStatus:::::" + updateStatus);
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_STARTED, ActionEntry.STATUS_CAPTURE_STARTED, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.CAPTURE_STARTED.toString());
                                i = dbUpdate(trackingId, updateStatus, notification.getParams().getData().getOrderId(), remark, amount, notificationCount);
                            }
                            if (PZTransactionStatus.MARKED_FOR_REVERSAL.toString().equals(dbStatus))
                            {
                                transactionLogger.debug("-----inside--" + dbStatus + "------");
                                remark = "Transaction Pending";
                                updateStatus = "markedforreversal";
                                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                                commResponseVO.setTransactionId(notification.getParams().getData().getOrderId());
                                commResponseVO.setTransactionType("refund");
                                commResponseVO.setDescription(Method.PENDING.toString());
                                commResponseVO.setRemark(remark);
                                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_REVERSAL_REQUEST_ACCEPTED, ActionEntry.STATUS_REVERSAL_REQUEST_ACCEPTED, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.ACCEPTED_FOR_REVERSAL.toString());
                                i = dbUpdate(trackingId, updateStatus, notification.getParams().getData().getOrderId(), remark, amount, notificationCount);
                            }
                            if (PZTransactionStatus.PAYOUT_STARTED.toString().equals(dbStatus))
                            {
                                transactionLogger.debug("-----inside--" + dbStatus + "------");
                                remark = "Payout Pending";
                                updateStatus = "payoutstarted";

                                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                                commResponseVO.setTransactionId(notification.getParams().getData().getOrderId());
                                commResponseVO.setTransactionType("payout");
                                commResponseVO.setDescription(Method.PENDING.toString());
                                commResponseVO.setRemark(remark);
                                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_PAYOUT_STARTED, ActionEntry.STATUS_PAYOUT_STARTED, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.PAYOUT_STARTED.toString());
                                i = dbUpdate(trackingId, updateStatus, notification.getParams().getData().getOrderId(), remark, amount, notificationCount);
                            }
                            transactionLogger.error("is DB update for pending---" + i);
                            if (i == 1)
                            {
                                Response response1 = notificationHandler.prepareNotificationResponse(notification.getMethod(), notification.getUUID(), ResponseStatus.OK);

                                String jsonNotificationResponse = notificationHandler.toJson(response1);

                                transactionLogger.error("jsonNotificationResponse-----" + jsonNotificationResponse);

                                PrintWriter printWriter = response.getWriter();
                                printWriter.println(jsonNotificationResponse);
                                printWriter.flush();
                                return;
                            }
                        }
                    }
                    if (notification.getMethod().equals(Method.CREDIT))
                    {
                        String status = "capturesuccess";
                        remark = "Transaction Successful";
                        transactionLogger.debug("inside---" + notification.getMethod());
                        if (functions.isValueNull(transactionDetailsVO.getNotificationUrl()))
                        {
                            transactionLogger.error("---inside sending notification for trustly ----" + transactionDetailsVO.getNotificationUrl());
                            HashMap hashMap = paymentManager.getExtnDetailsForTrustly(trackingId);
                            hashMap.put("customerEmail", transactionDetailsVO.getEmailaddr());

                            TransactionUtility transactionUtility = new TransactionUtility();
                            transactionUtility.setMerchantNotification(hashMap, transactionDetailsVO, trackingId, status, remark);
                            notificationCount = 1;
                            transactionLogger.error("notificationCount------" + notificationCount);
                        }

                        int i = 0;

                        if (notification.getParams().getData() instanceof CreditData)
                        {
                            transactionLogger.error("inside instence of CreditData---");
                            creditData = (CreditData) notification.getParams().getData();
                            transactionLogger.error("amount credit---" + creditData.getAmount());
                        }


                        if (creditData.getAmount().equals(amount))
                        {
                            transactionLogger.debug("-----inside--" + dbStatus + "------");

                            if (PZTransactionStatus.CAPTURE_STARTED.toString().equals(dbStatus))
                            {
                                transactionLogger.debug("-----inside--" + dbStatus + "------");
                                remark = "Transaction Successful";
                                updateStatus = "capturesuccess";
                                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                                commResponseVO.setTransactionId(notification.getParams().getData().getOrderId());
                                commResponseVO.setTransactionType("sale");
                                commResponseVO.setDescription(Method.CREDIT.toString());
                                commResponseVO.setStatus("success");
                                commResponseVO.setRemark(remark);
                                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.CAPTURE_SUCCESS.toString());
                                i = dbUpdate(trackingId, updateStatus, notification.getParams().getData().getOrderId(), remark, amount, notificationCount);
                            }
                            if (PZTransactionStatus.PAYOUT_STARTED.toString().equals(dbStatus))
                            {
                                transactionLogger.debug("-----inside--" + dbStatus + "------");
                                remark = "Payout Fail";
                                updateStatus = "payoutfailed";

                                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                                commResponseVO.setTransactionId(notification.getParams().getData().getOrderId());
                                commResponseVO.setTransactionType("payout");
                                commResponseVO.setDescription(remark);
                                commResponseVO.setRemark(remark);
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.STATUS_PAYOUT_FAILED, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.PAYOUT_FAILED.toString());
                                i = dbUpdate(trackingId, updateStatus, notification.getParams().getData().getOrderId(), remark, amount, notificationCount);
                            }

                            transactionLogger.error("is DB update for credit---" + i);

                            if (i == 1)
                            {
                                Response response1 = notificationHandler.prepareNotificationResponse(notification.getMethod(), notification.getUUID(), ResponseStatus.OK);

                                String jsonNotificationResponse = notificationHandler.toJson(response1);

                                transactionLogger.error("jsonNotificationResponse-----" + jsonNotificationResponse);

                                PrintWriter printWriter = response.getWriter();
                                printWriter.println(jsonNotificationResponse);
                                printWriter.flush();
                                return;
                            }
                        }
                    }
                    if (notification.getMethod().equals(Method.DEBIT))
                    {
                        transactionLogger.debug("inside---" + notification.getMethod());
                        transactionLogger.debug("amout-----" + debitNotificationData.getAmount());
                        int i = 0;
                        if (!debitNotificationData.getAmount().equals(amount))
                        {
                            if (notification.getParams().getData() instanceof DebitNotificationData)
                            {
                                transactionLogger.error("inside instence of DebitNotificationData---");
                                debitNotificationData = (DebitNotificationData) notification.getParams().getData();
                                transactionLogger.error("amount debit---" + debitNotificationData.getAmount());
                            }

                            if (PZTransactionStatus.MARKED_FOR_REVERSAL.toString().equals(dbStatus))
                            {
                                transactionLogger.debug("-----inside--" + dbStatus + "------");
                                remark = "Transaction Successful";
                                updateStatus = "reversed";
                                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                                commResponseVO.setTransactionId(notification.getParams().getData().getOrderId());
                                commResponseVO.setTransactionType("refund");
                                commResponseVO.setDescription(Method.DEBIT.toString());
                                commResponseVO.setRemark(remark);
                                entry.actionEntryForCommon(trackingId, debitNotificationData.getAmount(), ActionEntry.ACTION_REVERSAL_SUCCESSFUL, ActionEntry.STATUS_REVERSAL_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.REVERSED.toString());
                                i = dbUpdate(trackingId, updateStatus, notification.getParams().getData().getOrderId(), remark, amount, notificationCount);
                            }
                            if (PZTransactionStatus.PAYOUT_STARTED.toString().equals(dbStatus))
                            {
                                transactionLogger.debug("-----inside--" + dbStatus + "------");
                                remark = "payoutsuccessful";
                                updateStatus = "payoutstarted";

                                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                                commResponseVO.setTransactionId(notification.getParams().getData().getOrderId());
                                commResponseVO.setTransactionType("payout");
                                commResponseVO.setDescription(Method.DEBIT.toString());
                                commResponseVO.setRemark(remark);
                                entry.actionEntryForCommon(trackingId, debitNotificationData.getAmount(), ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.PAYOUT_SUCCESS.toString());
                                i = dbUpdate(trackingId, updateStatus, notification.getParams().getData().getOrderId(), remark, amount, notificationCount);
                            }
                            transactionLogger.error("is DB update for debit---" + i);
                            if (i == 1)
                            {
                                Response response1 = notificationHandler.prepareNotificationResponse(notification.getMethod(), notification.getUUID(), ResponseStatus.OK);

                                String jsonNotificationResponse = notificationHandler.toJson(response1);

                                transactionLogger.error("jsonNotificationResponse-----" + jsonNotificationResponse);

                                PrintWriter printWriter = response.getWriter();
                                printWriter.println(jsonNotificationResponse);
                                printWriter.flush();
                                return;
                            }
                        }
                    }

                    if (notification.getMethod().equals(Method.CANCEL))
                    {
                        String status = "authfailed";
                        String notificationRemark = "Transaction Fail";
                        if (functions.isValueNull(transactionDetailsVO.getNotificationUrl()))
                        {
                            transactionLogger.error("---inside sending notification for trustly ----" + transactionDetailsVO.getNotificationUrl());
                            HashMap hashMap = paymentManager.getExtnDetailsForTrustly(trackingId);
                            hashMap.put("customerEmail", transactionDetailsVO.getEmailaddr());

                            TransactionUtility transactionUtility = new TransactionUtility();
                            transactionUtility.setMerchantNotification(hashMap, transactionDetailsVO, trackingId, status, notificationRemark);
                            notificationCount = 1;
                            transactionLogger.error("notificationCount------" + notificationCount);
                        }
                        transactionLogger.debug("inside---" + notification.getMethod());
                        int i = 0;
                        if (notification.getParams().getData() instanceof CancelNotificationData)
                        {
                            transactionLogger.error("inside instence of CancelNotificationData---");
                            cancelNotificationData = (CancelNotificationData) notification.getParams().getData();
                        }
                        if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                        {
                            transactionLogger.debug("-----inside--" + dbStatus + "------");
                            remark = "Transaction Fail";
                            updateStatus = "authfailed";
                            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                            commResponseVO.setTransactionId(notification.getParams().getData().getOrderId());
                            commResponseVO.setTransactionType("sale");
                            commResponseVO.setDescription(remark);
                            commResponseVO.setRemark(remark);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_CANCLLED, ActionEntry.STATUS_AUTHORISTION_CANCLLED, commResponseVO, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.AUTH_CANCELLED.toString());
                            i = dbUpdate(trackingId, updateStatus, notification.getParams().getData().getOrderId(), remark, amount, notificationCount);
                        }
                        if (PZTransactionStatus.MARKED_FOR_REVERSAL.toString().equals(dbStatus))
                        {
                            transactionLogger.debug("-----inside--" + dbStatus + "------");
                            remark = "Transaction Successful";
                            updateStatus = "markedforreversal";
                            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                            commResponseVO.setTransactionId(notification.getParams().getData().getOrderId());
                            commResponseVO.setTransactionType("refund");
                            commResponseVO.setDescription(Method.CANCEL.toString());
                            commResponseVO.setRemark(remark);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_REVERSAL_CONSOLIDATED, ActionEntry.STATUS_REVERSAL_CONSOLIDATED, commResponseVO, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.CONSOLIDATED_FOR_REVERSAL.toString());
                            i = dbUpdate(trackingId, updateStatus, notification.getParams().getData().getOrderId(), remark, amount, notificationCount);
                        }
                        if (PZTransactionStatus.PAYOUT_STARTED.toString().equals(dbStatus))
                        {
                            transactionLogger.debug("-----inside--" + dbStatus + "------");
                            remark = "Payout Fail";
                            updateStatus = "payoutfailed";

                            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                            commResponseVO.setTransactionId(notification.getParams().getData().getOrderId());
                            commResponseVO.setTransactionType("payout");
                            commResponseVO.setDescription(remark);
                            commResponseVO.setRemark(remark);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.STATUS_PAYOUT_FAILED, commResponseVO, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.PAYOUT_FAILED.toString());
                            i = dbUpdate(trackingId, updateStatus, notification.getParams().getData().getOrderId(), remark, amount, notificationCount);
                        }

                        transactionLogger.error("is DB update for cancel---" + i);
                        if (i == 1)
                        {
                            Response response1 = notificationHandler.prepareNotificationResponse(notification.getMethod(), notification.getUUID(), ResponseStatus.OK);

                            String jsonNotificationResponse = notificationHandler.toJson(response1);

                            transactionLogger.error("jsonNotificationResponse-----" + jsonNotificationResponse);

                            PrintWriter printWriter = response.getWriter();
                            printWriter.println(jsonNotificationResponse);
                            printWriter.flush();
                            return;
                        }
                    }

                }
            }
            catch (PZDBViolationException e)
            {
                transactionLogger.error("PZDBViolationException in PerfectMoneyBackEndServlet---", e);
            }
            catch (SystemError e)
            {
                transactionLogger.error("SystemError in PerfectMoneyBackEndServlet---", e);
            }
            finally
            {
                Database.closeConnection(con);
            }
        }
    }

    public String insertTrustlyBankDetails(AccountNotificationData accountNotificationData) throws PZDBViolationException
    {
        String queryResult="";
        Connection conn=null;
        PreparedStatement ps=null;
        try
        {
            conn=Database.getConnection();
            String query="INSERT INTO transaction_trustly_details(trackingid,bank_order_id,customer_account_id,clearinghouse,bank,descriptor,lastdigits,personid,name,address,zipcode,city) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
            ps=conn.prepareStatement(query);
            ps.setString(1,accountNotificationData.getMessageId());
            ps.setString(2,accountNotificationData.getOrderId());
            ps.setString(3,accountNotificationData.getAccountId());
            if(accountNotificationData.getAttributes().get("clearinghouse")!=null)
                ps.setString(4,accountNotificationData.getAttributes().get("clearinghouse").toString());
            else
                ps.setString(4,"");
            if(accountNotificationData.getAttributes().get("bank")!=null)
                ps.setString(5,accountNotificationData.getAttributes().get("bank").toString());
            else
                ps.setString(5,"");
            if(accountNotificationData.getAttributes().get("descriptor")!=null)
                ps.setString(6,accountNotificationData.getAttributes().get("descriptor").toString());
            else
                ps.setString(6,"");
            if(accountNotificationData.getAttributes().get("lastdigits")!=null)
                ps.setString(7,accountNotificationData.getAttributes().get("lastdigits").toString());
            else
                ps.setString(7,"");
            if(accountNotificationData.getAttributes().get("personid")!=null)
                ps.setString(8,accountNotificationData.getAttributes().get("personid").toString());
            else
                ps.setString(8,"");
            if(accountNotificationData.getAttributes().get("name")!=null)
                ps.setString(9,accountNotificationData.getAttributes().get("name").toString());
            else
                ps.setString(9,"");
            if(accountNotificationData.getAttributes().get("address")!=null)
                ps.setString(10,accountNotificationData.getAttributes().get("address").toString());
            else
                ps.setString(10,"");
            if(accountNotificationData.getAttributes().get("zipcode")!=null)
                ps.setString(11,accountNotificationData.getAttributes().get("zipcode").toString());
            else
                ps.setString(11,"");
            if(accountNotificationData.getAttributes().get("city")!=null)
                ps.setString(12,accountNotificationData.getAttributes().get("city").toString());
            else
                ps.setString(12,"");
            int num=ps.executeUpdate();

            if(num==1){
                queryResult="success";
            }else{
                queryResult="failed";
            }

        }catch (SystemError systemError){
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "insertTrustlyBankDetails()", null, "Common", "SystemError Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }catch (SQLException e){
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java","insertTrustlyBankDetails()",null,"Common","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }finally
        {
            Database.closeConnection(conn);
        }
        return queryResult;
    }
}
