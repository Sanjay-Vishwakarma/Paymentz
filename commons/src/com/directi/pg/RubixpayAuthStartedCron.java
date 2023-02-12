package com.directi.pg;

import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.FlutterWave.FlutterWavePaymentGateway;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Rubixpay.RubixpayPaymentGateway;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.statussync.StatusSyncDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.ResourceBundle;

/**
 * Created by Admin on 6/7/2020.
 */
public class RubixpayAuthStartedCron
{

    private static Logger logger = new Logger(RubixpayAuthStartedCron.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(RubixpayAuthStartedCron.class.getName());

    public String rubixpayAuthStartedCron(Hashtable hashtable)
    {
         logger.error("::: Inside RubixpayAuthStartedCron method :::");

        StringBuffer success = new StringBuffer();
        StringBuffer nTransaction = new StringBuffer();
        StringBuffer yTransaction = new StringBuffer();
        StringBuffer sHeader = new StringBuffer();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        Functions functions=new Functions();

        Connection connection = null;
        ResultSet resultSet=null;
        PreparedStatement ps=null;

        String trackingid = null;
        String amount = null;
        String accountid = null;
        String toid = null;
        String currency = null;
        String status = null;
        String sDescription = null;
        String timestamp=null;
        String notificationUrl = "";

        ActionEntry entry = new ActionEntry();
        int actionEntry = 0;

        int authstartedCounter = 0;
        //int authsucessCounter = 0;
        int authFailedCounter = 0;
        int captureSuccesCounter = 0;
        int pendingCounter = 0;
        int refundCounter = 0;
        int failedCounter = 0;
        int notProcessedCounter = 0;

        String transStatus = "";
        String paymentid = "";
        String remark = "";
        //  String memberIdforCron = RB.getString("MEMBERID_FOR_CRON");
        // logger.error("Memberid for Authstarted Cron---"+memberIdforCron);
        try
        {

            connection = Database.getConnection();
            // String wCond = "FROM transaction_common as t WHERE t.STATUS in ('authstarted','capturestarted') AND fromtype='Flutter' AND toid in ("+memberIdforCron+") AND (TIMESTAMPDIFF(HOUR, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) >0";
            String wCond = "FROM transaction_common as t WHERE t.STATUS in ('authstarted')  AND fromtype='rubixpay' AND (TIMESTAMPDIFF(HOUR, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) >0" ;
           // String wCond = "FROM transaction_common as t WHERE t.STATUS in ('authstarted')  AND fromtype='rubixpay' and from_unixtime(dtstamp)>'2020-06-08 00:00:00' and from_unixtime(dtstamp)<'2020-06-08 14:00:00' AND (TIMESTAMPDIFF(HOUR, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) >0" ;

            String selectQuery = "SELECT t.trackingid, t.toid, t.paymentid, t.status, t.notificationUrl, t.amount,t.timestamp, t.description, t.remark, t.accountid, t.fromid, t.fromtype, t.currency " + wCond;
            transactionLogger.error("selectQuery -----------" + selectQuery);
            String cQuery = "SELECT count(*) " + wCond;

            ResultSet rset = Database.executeQuery(cQuery.toString(), connection);
            if(rset.next())
                authstartedCounter = rset.getInt(1);
            // Database.closeResultSet(rset);

            ps = connection.prepareStatement(selectQuery);
            resultSet = ps.executeQuery();



            transactionLogger.error("Select Query rubixpay---" + ps+ "authstartedCounter---"+authstartedCounter);

            while (resultSet.next())
            {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                trackingid = resultSet.getString("trackingid");
                amount = resultSet.getString("amount");
                accountid = resultSet.getString("accountid");
                toid = resultSet.getString("toid");
                paymentid = resultSet.getString("paymentid");
                sDescription = resultSet.getString("description");
                status = resultSet.getString("status");
                currency = resultSet.getString("currency");
                timestamp = resultSet.getString("timestamp");
                notificationUrl = resultSet.getString("notificationUrl");

                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorId(toid);
                auditTrailVO.setActionExecutorName("RubixpayAuthStartedCron");

                CommRequestVO requestVO = new CommRequestVO();
                CommResponseVO responseVO = null;
                CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
                CommAddressDetailsVO commAddressDetailsVO =new CommAddressDetailsVO();

                commTransactionDetailsVO.setOrderId(trackingid);
                commTransactionDetailsVO.setPreviousTransactionId(paymentid);
                commTransactionDetailsVO.setResponseHashInfo(paymentid);
                commTransactionDetailsVO.setResponsetime(String.valueOf(dateFormat.format(date)));

                requestVO.setTransDetailsVO(commTransactionDetailsVO);
                requestVO.setAddressDetailsVO(commAddressDetailsVO);
                //requestVO.getCommMerchantVO().setMerchantId("108");
                transactionLogger.error("Select Query rubixpay-------" + trackingid);

                // SkrillPaymentGateway skrillPaymentGateway = new SkrillPaymentGateway(accountid);
                RubixpayPaymentGateway rubixpayPaymentGateway=new RubixpayPaymentGateway(accountid);
              //  entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVO, null);
                entry.actionEntryFor3DCommon(trackingid, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, null, requestVO, auditTrailVO, null);
                // responseVO = (CommResponseVO)skrillPaymentGateway.processInquiry(trackingid, requestVO);
                responseVO= (CommResponseVO) rubixpayPaymentGateway.process3DSaleConfirmation(trackingid,requestVO);

                if(responseVO!=null)
                {
                    if(responseVO.getStatus().equalsIgnoreCase("success"))
                    {
                        transactionLogger.error("------ inside success-------" + responseVO.getTransactionId());
                        transactionLogger.error("------ inside success-------" + responseVO.getDescription());
                        transactionLogger.error("------ inside success-------" + responseVO.getRemark());
                        transactionLogger.error("------ inside success-------" + responseVO.getStatus());
                        // responseVO.setRemark("Transaction Successful");

                            //Auth Success
                            transStatus = "capturesuccess";
                            paymentid = responseVO.getTransactionId();
                            insertMainTableEntry(transStatus, responseVO.getRemark(), paymentid, trackingid);
                            //Detail Table Entry
                            entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVO, null);
                            captureSuccesCounter++;

                    }

                    else if(responseVO.getStatus().equalsIgnoreCase("pending"))
                    {
                        transStatus="pending";

                        transactionLogger.error("-----inside pending-----"+trackingid);
                        transactionLogger.error("-----transaction id in pending case-----"+responseVO.getTransactionId());
                        pendingCounter++;
                    }

                    else
                    {
                        transactionLogger.error("-----inside failed-----");
                        transactionLogger.error("-----transaction id in fail case-----"+responseVO.getTransactionId());
                        //Not found transaction
                        transStatus = "authfailed";
                        if(functions.isValueNull(responseVO.getTransactionId()))
                            paymentid = responseVO.getTransactionId();
                        else
                            paymentid = "";
                        insertMainTableEntry(transStatus,responseVO.getRemark(),paymentid,trackingid);
                        //Detail Table Entry
                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, responseVO, auditTrailVO, null);
                        authFailedCounter++;
                    }
                }
                else
                {
                    transactionLogger.error("-----inside failed-----");
                    //Not found transaction
                    transStatus = "failed";
                    paymentid = "";
                    insertMainTableEntry(transStatus,responseVO.getRemark(),paymentid,trackingid);
                    //Detail Table Entry
                    entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, responseVO, auditTrailVO, null);
                    failedCounter++;
                   
                }
                //update status flags
                statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,transStatus);

                //Sending Notification on NotificationURL
                logger.error("Notificatin Sending to---"+notificationUrl+"---"+trackingid);
                if(functions.isValueNull(notificationUrl)&&!"pending".equalsIgnoreCase(transStatus))
                {
                    TransactionManager transactionManager = new TransactionManager();
                    MerchantDAO merchantDAO = new MerchantDAO();
                    MerchantDetailsVO merchantDetailsVO =  merchantDAO.getMemberDetails(toid);
                    logger.error("inside sending notification---"+notificationUrl);

                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);
                    transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
                    transactionDetailsVO.setBillingDesc(GatewayAccountService.getGatewayAccount(accountid).getDisplayName().toString());
                    asyncNotificationService.sendNotification(transactionDetailsVO,trackingid,transStatus,responseVO.getRemark());
                }
            }

            transactionLogger.error("authstartedCounter------------------>"+authstartedCounter);
            transactionLogger.error("captureSuccesCounter------------------>"+captureSuccesCounter);
            transactionLogger.error("pendingCounter------------------>"+pendingCounter);
            transactionLogger.error("authFailedCounter------------------>"+authFailedCounter);
            transactionLogger.error("FailedCounter------------------>"+failedCounter);
        }
        catch (SQLException e)
        {
            logger.error("SQLException",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(FlutterWaveAuthStartedCron.class.getName(), "rubixpayAuthStartedCron()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), toid, "RubixpayAuthStartedCron");
            notProcessedCounter++;

        }

        catch (PZDBViolationException pzd)
        {
            logger.error("PZDBViolationException",pzd);
            PZExceptionHandler.handleDBCVEException(pzd, toid, "RubixpayAuthStartedCron");
            notProcessedCounter++;

        }
        catch (SystemError systemError)
        {
            logger.error("SystemError", systemError);
            PZExceptionHandler.raiseAndHandleDBViolationException(RubixpayAuthStartedCron.class.getName(), "rubixpayAuthStartedCron()", null, "common", "DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause(), toid, "RubixpayAuthStartedCron");
            notProcessedCounter++;


        }
        catch(PZGenericConstraintViolationException ge){
            logger.error("PZGenericConstraintViolationException",ge);
            notProcessedCounter++;

        }
        finally
        {
            if(connection!=null)
                Database.closeConnection(connection);

            //Database.closeResultSet(resultSet);
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        success.append("<b>Date and Time : </b>"+String.valueOf(dateFormat.format(date))+"<br>");
        success.append("<b>Total Transactions in Authstarted/Capturestarted : </b>"+authstartedCounter+"<br>");
        success.append("<br>");
        success.append("<b>Authorization Failed : </b>"+authFailedCounter+"<br>");
        success.append("<b>Capture Successful : </b>"+captureSuccesCounter+"<br>");
        success.append("<b>Refunded : </b>"+refundCounter+"<br>");
        success.append("<b>Failed Transactions(Order Not found) : </b>"+failedCounter+"<br>");
        success.append("<b>Not Processed Transactions : </b>"+notProcessedCounter+"<br>");
        int tAdd = authFailedCounter+captureSuccesCounter+refundCounter+failedCounter+notProcessedCounter;
        success.append("<b>Total Transactions Processed : </b>"+tAdd+"<br>");

        //Auth/Capture started transaction table

        transactionLogger.error("total count---"+success);

      /*  AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
        asynchronousMailService.sendEmail(MailEventEnum.ADMIN_AUTHSTARTED_CRON_REPORT, "", "", success.toString(), null);*/
        return success.toString();

    }

    public void insertMainTableEntry(String transStatus, String remark, String paymentId, String trackingid)
    {
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String updateQuery1 = "UPDATE transaction_common SET status=?,remark=?,paymentid=? WHERE trackingid=?";
            PreparedStatement ps2 = connection.prepareStatement(updateQuery1);
            ps2.setString(1, transStatus);
            ps2.setString(2, remark);
            ps2.setString(3, paymentId);
            ps2.setString(4, trackingid);
            ps2.executeUpdate();
        }
        catch (SQLException se)
        {
            logger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            logger.error("SQLException---", s);
        }
        finally
        {
            if(connection!=null)
                Database.closeConnection(connection);
        }
    }



}
