package com.directi.pg;

import com.directi.pg.core.paymentgateway.SafeChargePaymentGateway;
import com.directi.pg.core.valueObjects.SafeChargeResponseVO;
import com.logicboxes.util.ApplicationProperties;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.statussync.StatusSyncDAO;
import com.payment.unicredit.UnicreditPaymentGateway;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

/**
 * Created by Admin on 10/25/2017.
 */
public class UnicreditAuthStartedCron
{
    private static Logger logger = new Logger(UnicreditAuthStartedCron.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(UnicreditAuthStartedCron.class.getName());
    boolean isLogEnabled= Boolean.parseBoolean(ApplicationProperties.getProperty("IS_LOG_ENABLED"));

    public static void main(String args[]) throws SystemError
    {
        UnicreditAuthStartedCron uc = new UnicreditAuthStartedCron();
        String sResponse = uc.unicreditAuthStartedCron(new Hashtable());
    }

    public String unicreditAuthStartedCron(Hashtable hashtable)
    {
        if (isLogEnabled)
        logger.debug("::: Inside UnicreditAuthStartedCron method :::");

        StringBuffer success = new StringBuffer();
        StringBuffer nTransaction = new StringBuffer();
        StringBuffer yTransaction = new StringBuffer();
        StringBuffer sHeader = new StringBuffer();
        success.append("<u>Transaction Report <b>Unicredit</b>" + "</u><br>");
        success.append("<br>");
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();

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

        ActionEntry entry = new ActionEntry();
        int actionEntry = 0;

        int authstartedCounter = 0;
        //int authsucessCounter = 0;
        int authFailedCounter = 0;
        int captureSuccesCounter = 0;
        //int cancelledCounter = 0;
        int refundCounter = 0;
        int payoutCounter = 0;
        int failedCounter = 0;
        int notProcessedCounter = 0;

        String transStatus = "";
        String paymentid = "";
        String remark = "";

        try
        {
            connection = Database.getConnection();
            String wCond = "FROM transaction_common as t WHERE t.STATUS in ('authstarted','capturestarted') AND fromtype='unicredit' AND (TIMESTAMPDIFF(HOUR, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) > 0";

            String selectQuery = "SELECT t.trackingid, t.toid, t.status, t.amount,t.timestamp, t.description, t.remark, t.accountid, t.fromid, t.fromtype, t.currency " + wCond;

            String cQuery = "SELECT count(*) " + wCond;
            ps = connection.prepareStatement(selectQuery);
            resultSet = ps.executeQuery();

            ResultSet rs = Database.executeQuery(cQuery.toString(), connection);
            rs.next();
            authstartedCounter = rs.getInt(1);

            if (isLogEnabled)
            transactionLogger.debug("Select Query SC---" + ps+"---authstartedCounter---"+authstartedCounter);

            while (resultSet.next())
            {
                trackingid = resultSet.getString("trackingid");
                amount = resultSet.getString("amount");
                accountid = resultSet.getString("accountid");
                toid = resultSet.getString("toid");
                sDescription = resultSet.getString("description");
                status = resultSet.getString("status");
                currency = resultSet.getString("currency");

                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorId(toid);
                auditTrailVO.setActionExecutorName("UnicreditAuthStartedCron");

                CommRequestVO requestVO = new CommRequestVO();
                CommResponseVO responseVO = null;
                CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();


                commTransactionDetailsVO.setOrderId(trackingid);
                commTransactionDetailsVO.setOrderDesc(sDescription);
                commTransactionDetailsVO.setCurrency(currency);
                commTransactionDetailsVO.setAmount(amount);
                commTransactionDetailsVO.setTransactionType("10");

                requestVO.setTransDetailsVO(commTransactionDetailsVO);

                if (isLogEnabled)
                    transactionLogger.debug("Select Query SC-------"+trackingid);

                UnicreditPaymentGateway uc = new UnicreditPaymentGateway(accountid);

                responseVO = (CommResponseVO)uc.processInquiry(requestVO);

                transactionLogger.error("error code---"+responseVO.getErrorCode());

                if(responseVO!=null)
                {
                    if(responseVO.getStatus().equalsIgnoreCase("success"))
                    {
                        if (isLogEnabled)
                        transactionLogger.debug("------ inside success-------");
                        if(responseVO.getRemark()==null ||responseVO.getRemark()==""){
                            responseVO.setRemark("Transaction Successful");
                        }

                        if (responseVO.getTransactionType().equalsIgnoreCase("10"))
                        {
                            if (isLogEnabled)
                            transactionLogger.debug("------ inside capturesuccess-------"+responseVO.getTransactionType());
                            //Capture Success
                            transStatus = "capturesuccess";

                            insertMainTableEntry(transStatus,responseVO.getRemark(),paymentid,trackingid);
                            //Detail Table Entry
                            entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVO, null);
                            captureSuccesCounter++;
                        }
                        if (responseVO.getTransactionType().contains("11"))
                        {
                            if (isLogEnabled)
                            transactionLogger.debug("------ inside payoutsuccess-------"+responseVO.getTransactionType());
                            //Payout/Refund
                            transStatus = "payoutsuccess";

                            insertMainTableEntry(transStatus,responseVO.getRemark(),paymentid,trackingid);
                            //Detail Table Entry for payout started
                            entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_PAYOUT_STARTED, ActionEntry.STATUS_PAYOUT_STARTED, responseVO, auditTrailVO, null);

                            //Detail Table Entry for payout success
                            entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_SUCCESSFUL, responseVO, auditTrailVO, null);
                            payoutCounter++;
                        }
                        if (responseVO.getTransactionType().contains("40"))
                        {
                            if (isLogEnabled)
                            transactionLogger.debug("------ inside reversed-------"+responseVO.getTransactionType());

                            //Payout/Refund
                            transStatus = "reversed";

                            insertMainTableEntry(transStatus,responseVO.getRemark(),paymentid,trackingid);
                            //Detail Table Entry for reversal started
                            entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_REVERSAL_REQUEST_SENT, ActionEntry.STATUS_REVERSAL_REQUEST_SENT, responseVO, auditTrailVO, null);

                            //Detail Table Entry for reversal success
                            entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_REVERSAL_SUCCESSFUL, ActionEntry.STATUS_REVERSAL_SUCCESSFUL, responseVO, auditTrailVO, null);
                            refundCounter++;
                        }
                    }
                    else if(responseVO.getErrorCode().equals("98"))
                    {
                        if (isLogEnabled)
                            transactionLogger.debug("-----inside failed-----");
                        //Not found transaction
                        responseVO.setRemark("Transaction Not Found");
                        responseVO.setDescription("Transaction Not Found");
                        transStatus = "failed";
                        paymentid = "";
                        insertMainTableEntry(transStatus,responseVO.getRemark(),paymentid,trackingid);
                        //Detail Table Entry
                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, responseVO, auditTrailVO, null);
                        failedCounter++;
                    }
                    else
                    {
                        if (isLogEnabled)
                        transactionLogger.debug("-----inside authfailed-----");

                        responseVO.setRemark("Transaction Declined by Admin");
                        responseVO.setDescription("Transaction Declined by Admin");
                        //Declined transaction
                        transStatus = "authfailed";
                        paymentid = responseVO.getTransactionId();
                        insertMainTableEntry(transStatus,responseVO.getRemark(),paymentid,trackingid);
                        //Detail Table Entry
                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, responseVO, auditTrailVO, null);
                        authFailedCounter++;
                    }
                }
                else
                {
                    if (isLogEnabled)
                    transactionLogger.debug("-----inside failed-----");
                    //Not found transaction
                    responseVO.setRemark("Transaction Not Found");
                    responseVO.setDescription("Transaction Not Found");
                    transStatus = "failed";
                    paymentid = "";
                    insertMainTableEntry(transStatus,responseVO.getRemark(),paymentid,trackingid);
                    //Detail Table Entry
                    entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, responseVO, auditTrailVO, null);
                    failedCounter++;
                }
                //update status flags
                statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,transStatus);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(UnicreditAuthStartedCron.class.getName(), "UnicreditAuthStartedCron()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), toid, "Unicredit Auth started cron");
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,status));
        }

        catch (PZDBViolationException pzd)
        {
            logger.error("PZDBViolationException",pzd);
            PZExceptionHandler.handleDBCVEException(pzd, toid, "UnicreditAuthStartedCron");
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,status));
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError",systemError);
            PZExceptionHandler.raiseAndHandleDBViolationException(UnicreditAuthStartedCron.class.getName(),"UnicreditAuthStartedCron()",null,"common","DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),toid,"Unicredit Auth started cron");
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,status));

        }catch(PZConstraintViolationException e){
            logger.error("PZConstraintViolationException",e);
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,status));
        }
        catch(PZGenericConstraintViolationException ge){
            logger.error("PZGenericConstraintViolationException",ge);
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,status));
        }
        finally
        {
            Database.closeConnection(connection);
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        success.append("<b>Date and Time : </b>"+String.valueOf(dateFormat.format(date))+"<br>");
        success.append("<b>Total Transactions in Authstarted : </b>"+authstartedCounter+"<br>");
        success.append("<br>");
        //success.append("<b>Authorization Successful : </b>"+authsucessCounter+"<br>");
        success.append("<b>Authorization Failed : </b>"+authFailedCounter+"<br>");
        success.append("<b>Capture Successful : </b>"+captureSuccesCounter+"<br>");
        success.append("<b>Refunded : </b>"+refundCounter+"<br>");
        success.append("<b>Payout Successful : </b>"+payoutCounter+"<br>");
        //success.append("<b>Cancelled  : </b>"+cancelledCounter+"<br>");
        success.append("<b>Failed Transactions(Order Not found) : </b>"+failedCounter+"<br>");
        success.append("<b>Not Processed Transactions : </b>"+notProcessedCounter+"<br>");
        int tAdd = authFailedCounter+captureSuccesCounter+refundCounter+payoutCounter+failedCounter+notProcessedCounter;
        success.append("<b>Total Transactions Processed : </b>"+tAdd+"<br>");

        //Auth started transaction table
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

        if (isLogEnabled)
        transactionLogger.debug("total count---"+success);

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
