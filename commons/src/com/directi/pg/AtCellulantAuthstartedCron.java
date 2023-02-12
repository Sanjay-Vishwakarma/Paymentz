package com.directi.pg;

import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.FlutterWave.FlutterWavePaymentGateway;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.cellulant.AtCellulantPaymentGateway;
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

/**
 * Created by Admin on 3/1/2021.
 */
public class AtCellulantAuthstartedCron
{
    private static TransactionLogger transactionLogger=new TransactionLogger(AtCellulantAuthstartedCron.class.getName());
    public String atCellulantAuthstartedCron(Hashtable hashtable)
    {
        transactionLogger.error("::: Inside AtCellulantAuthstartedCron method :::");

        StringBuffer success = new StringBuffer();
        StringBuffer nTransaction = new StringBuffer();
        StringBuffer yTransaction = new StringBuffer();
        StringBuffer sHeader = new StringBuffer();
        success.append("<u>Transaction Report <b>AtCellulant</b>" + "</u><br>");
        success.append("<br>");
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
        int authFailedCounter = 0;
        int captureSuccesCounter = 0;
        int failedCounter = 0;
        int notProcessedCounter = 0;
        int pendingCounter = 0;

        String transStatus = "";
        String paymentid = "";
        String reqPaymentid = "";
        String remark = "";
        try
        {

            connection = Database.getConnection();
            String wCond = "FROM transaction_common as t WHERE t.STATUS in ('authstarted','capturestarted') AND fromtype='atcellulant' AND FROM_UNIXTIME(dtstamp)<DATE_ADD(CURRENT_TIMESTAMP,INTERVAL -20 MINUTE)";//AND FROM_UNIXTIME(dtstamp)>DATE_ADD(CURRENT_TIMESTAMP,INTERVAL -80 MINUTE)

            String selectQuery = "SELECT t.trackingid,t.paymentid, t.toid, t.status, t.notificationUrl, t.amount,t.timestamp, t.description, t.remark, t.accountid, t.fromid, t.fromtype, t.currency " + wCond;
            transactionLogger.error("selectQuery -----------" + selectQuery);
            String cQuery = "SELECT count(*) " + wCond;

            ResultSet rset = Database.executeQuery(cQuery.toString(), connection);
            if(rset.next())
                authstartedCounter = rset.getInt(1);

            ps = connection.prepareStatement(selectQuery);
            resultSet = ps.executeQuery();

            transactionLogger.error("Select Query AtCellulant---" + ps+ "--authstartedCounter---"+authstartedCounter);
            while (resultSet.next())
            {
                notificationUrl="";
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                trackingid = resultSet.getString("trackingid");
                amount = resultSet.getString("amount");
                accountid = resultSet.getString("accountid");
                toid = resultSet.getString("toid");
                sDescription = resultSet.getString("description");
                status = resultSet.getString("status");
                currency = resultSet.getString("currency");
                timestamp = resultSet.getString("timestamp");
                reqPaymentid = resultSet.getString("paymentid");

                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorId(toid);
                auditTrailVO.setActionExecutorName("AtCellulantAuthstartedCron");

                CommRequestVO requestVO = new CommRequestVO();
                CommResponseVO responseVO = null;
                CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();

                commTransactionDetailsVO.setOrderId(trackingid);
                commTransactionDetailsVO.setResponsetime(String.valueOf(dateFormat.format(date)));
                commTransactionDetailsVO.setPreviousTransactionId(reqPaymentid);

                requestVO.setTransDetailsVO(commTransactionDetailsVO);
                transactionLogger.error("Select Query AtCellulant-------" +trackingid);
                AtCellulantPaymentGateway atCellulantPaymentGateway=new AtCellulantPaymentGateway(accountid);
                responseVO= (CommResponseVO) atCellulantPaymentGateway.processQuery(trackingid, requestVO);

                if(responseVO!=null)
                {
                    if("success".equalsIgnoreCase(responseVO.getTransactionStatus()))
                    {
                        // responseVO.setRemark("Transaction Successful");
                        amount=responseVO.getAmount();
                        paymentid = responseVO.getTransactionId();
                        if (status.equalsIgnoreCase("authstarted"))
                        {
                            notificationUrl = resultSet.getString("notificationUrl");
                            //Auth Success
                            transStatus = "capturesuccess";
                            insertMainTableEntryForCapture(transStatus, responseVO.getRemark(), paymentid, amount, trackingid);
                            //Detail Table Entry
                            entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVO, null);
                            captureSuccesCounter++;
                            //update status flags
                            statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,transStatus);
                        }
                        else if (status.equalsIgnoreCase("capturestarted"))
                        {
                            notificationUrl = resultSet.getString("notificationUrl");
                            //Capture Success
                            transStatus = "capturesuccess";
                            paymentid = responseVO.getTransactionId();
                            insertMainTableEntryForCapture(transStatus, responseVO.getRemark(), paymentid,amount, trackingid);
                            //Detail Table Entry
                            entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVO, null);
                            captureSuccesCounter++;
                            //update status flags
                            statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,transStatus);
                        }
                    }else if("pending".equalsIgnoreCase(responseVO.getTransactionStatus()))
                    {
                        transStatus = "pending";
                        pendingCounter++;
                    }
                    else if("failed".equalsIgnoreCase(responseVO.getTransactionStatus()))
                    {
                        notificationUrl = resultSet.getString("notificationUrl");
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
                        //update status flags
                        statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,transStatus);
                    }
                }

                //Sending Notification on NotificationURL
                transactionLogger.error("Notification Sending to---"+notificationUrl+"---"+trackingid);
                if(functions.isValueNull(notificationUrl) && !"pending".equalsIgnoreCase(transStatus))
                {
                    TransactionManager transactionManager = new TransactionManager();
                    MerchantDAO merchantDAO = new MerchantDAO();
                    MerchantDetailsVO merchantDetailsVO =  merchantDAO.getMemberDetails(toid);
                    transactionLogger.error("inside sending notification---"+notificationUrl);

                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);
                    transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
                    if(functions.isValueNull(transactionDetailsVO.getCcnum()))
                        transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                    if(functions.isValueNull(transactionDetailsVO.getExpdate()))
                        transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));
                    transactionDetailsVO.setBillingDesc(GatewayAccountService.getGatewayAccount(accountid).getDisplayName().toString());
                    asyncNotificationService.sendNotification(transactionDetailsVO,trackingid,transStatus,responseVO.getRemark());
                }
            }
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException--->",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(FlutterWaveAuthStartedCron.class.getName(), "flutterWaveAuthStartedCron()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), toid, "FlutterWaveAuthStartedCron");
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid, accountid, toid, sDescription, currency, amount,status));
        }

        catch (PZDBViolationException pzd)
        {
            transactionLogger.error("PZDBViolationException---->",pzd);
            PZExceptionHandler.handleDBCVEException(pzd, toid, "FlutterWaveAuthStartedCron");
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid, accountid, toid, sDescription, currency, amount,status));
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError--->", systemError);
            PZExceptionHandler.raiseAndHandleDBViolationException(FlutterWaveAuthStartedCron.class.getName(), "flutterWaveAuthStartedCron()", null, "common", "DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause(), toid, "FlutterWaveAuthStartedCron");
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid, accountid, toid, sDescription, currency, amount, status));

        }
        catch(PZGenericConstraintViolationException ge){
            transactionLogger.error("PZGenericConstraintViolationException--->",ge);
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid, accountid, toid, sDescription, currency, amount,status));
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
        success.append("<b>Auth Started : </b>"+pendingCounter+"<br>");
        success.append("<b>Failed Transactions(Order Not found) : </b>"+failedCounter+"<br>");
        success.append("<b>Not Processed Transactions : </b>"+notProcessedCounter+"<br>");
        int tAdd = authFailedCounter+captureSuccesCounter+failedCounter+notProcessedCounter;
        success.append("<b>Total Transactions Processed : </b>"+tAdd+"<br>");

        //Auth/Capture started transaction table
        if(notProcessedCounter>0)
        {
            sHeader.append("<br>");
            sHeader.append("<b><u>Transactions in AuthStarted / CaptureStarted</b></u><br>");
            sHeader.append("<br>");
            sHeader.append(getTableHeader());
            sHeader.append("<br>");
            success.append("<br>");
            sHeader.append(nTransaction);
            sHeader.append("<br>");
            sHeader.append("</table>");
        }
        //Failed Transactions Table
        if(failedCounter>0)
        {
            sHeader.append("<br>");
            sHeader.append("<b><u>Transaction status changed from AuthStarted/Capture Started to Failed(Orders not found)</b></u><br>");
            sHeader.append("<br>");
            sHeader.append(getTableHeader());
            sHeader.append("<br>");
            success.append("<br>");
            sHeader.append(yTransaction);
            sHeader.append("<br>");
            sHeader.append("</table>");
        }
        success.append(sHeader);

        transactionLogger.error("total count---"+success);

        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
        asynchronousMailService.sendEmail(MailEventEnum.ADMIN_AUTHSTARTED_CRON_REPORT, "", "", success.toString(), null);
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
            transactionLogger.error("insertMainTableEntry---->"+ps2);
            ps2.executeUpdate();
        }
        catch (SQLException se)
        {
            transactionLogger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            transactionLogger.error("SQLException---", s);
        }
        finally
        {
            if(connection!=null)
                Database.closeConnection(connection);
        }
    }
    public void insertMainTableEntryForCapture(String transStatus, String remark, String paymentId,String amount, String trackingid)
    {
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String updateQuery1 = "UPDATE transaction_common SET status=?,remark=?,paymentid=?,captureamount=? WHERE trackingid=?";
            PreparedStatement ps2 = connection.prepareStatement(updateQuery1);
            ps2.setString(1, transStatus);
            ps2.setString(2, remark);
            ps2.setString(3, paymentId);
            ps2.setString(4, amount);
            ps2.setString(5, trackingid);
            transactionLogger.error("insertMainTableEntryForCapture---->"+ps2);
            ps2.executeUpdate();
        }
        catch (SQLException se)
        {
            transactionLogger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            transactionLogger.error("SQLException---", s);
        }
        finally
        {
            if(connection!=null)
                Database.closeConnection(connection);
        }
    }
    public StringBuffer getTableHeader()
    {
        StringBuffer sHeader = new StringBuffer();

        sHeader.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\" >");
        sHeader.append("<TR>");
        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#0f8c93\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">TrackingID</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#0f8c93\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">AccountID</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#0f8c93\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">MerchantID</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#0f8c93\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">OrderID</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#0f8c93\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Amount</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#0f8c93\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Status</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("</TR>");
        return sHeader;
    }

    public StringBuffer setTableData(String trackingid, String accountid, String toid, String sDescription, String currency, String captureAmount, String status)
    {
        StringBuffer nTransaction = new StringBuffer();

        nTransaction.append("<TR>");
        nTransaction.append("<TD>");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+trackingid+"</p>");
        nTransaction.append("</TD>");
        nTransaction.append("<TD>");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+accountid+"</p>");
        nTransaction.append("</TD>");
        nTransaction.append("<TD>");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+toid+"</p>");
        nTransaction.append("</TD>");
        nTransaction.append("<TD>");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+sDescription+"</p>");
        nTransaction.append("</TD>");
        nTransaction.append("<TD>");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+currency+" "+captureAmount+"</p>");
        nTransaction.append("</TD>");
        nTransaction.append("<TD>");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+status+"</p>");
        nTransaction.append("</TD>");

        nTransaction.append("</TR>");
        return nTransaction;
    }
}
