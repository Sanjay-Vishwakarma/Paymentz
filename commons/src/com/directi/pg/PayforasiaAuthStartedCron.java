package com.directi.pg;

import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.payforasia.core.PayforasiaPaymentGateway;
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
 * Created by Nikita on 12/17/2015.
 */
public class PayforasiaAuthStartedCron
{
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.KotakServlet");
    private static Logger logger = new Logger(PayforasiaAuthStartedCron.class.getName());

    public static void main(String args[]) throws SystemError
    {
        PayforasiaAuthStartedCron payforasiaAuthStartedCron = new PayforasiaAuthStartedCron();
        String sResponse = payforasiaAuthStartedCron.payforasiaAuthStartedCron(new Hashtable());
    }

    public String payforasiaAuthStartedCron(Hashtable hashtable)
    {
        logger.debug("Inside payforasiaAuthStartedCron method :::");

        StringBuffer success = new StringBuffer();
        StringBuffer nTransaction = new StringBuffer();
        StringBuffer yTransaction = new StringBuffer();
        StringBuffer sHeader = new StringBuffer();
        success.append("<u>Transaction Report <b>Payforasia</b>" + "</u><br>");
        success.append("<br>");
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        AbstractPaymentGateway abstractPaymentGateway = null;
        Connection connection = null;
        ResultSet resultSet=null;
        PreparedStatement ps=null;

        String trackingid = null;
        String captureAmount = null;
        String accountid = null;
        String fromid = null;
        String sDescription = null;
        String toid = null;
        String fromtype = null;
        String currency = null;
        String status = null;
        //String isStatusUpdate = "";

        ActionEntry entry = new ActionEntry();

        int authstartedCounter = 0;
        int authFailedCounter = 0;
        int captureSuccesCounter = 0;
        int failedCounter = 0;
        int notProcessedCounter = 0;

        try
        {
            connection = Database.getConnection();
            String wCond = "FROM transaction_common WHERE STATUS in ('authstarted','capturestarted') AND fromtype='payforasia' AND (TIMESTAMPDIFF(HOUR, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) > 12";
            //String selectQuery = "SELECT trackingid, toid, status, amount, description, remark, accountid, fromid, fromtype FROM transaction_common WHERE STATUS='authstarted' AND fromtype='payforasia' AND trackingid = '14754' AND (TIMESTAMPDIFF(MINUTE, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) > 15";
            String selectQuery = "SELECT trackingid, toid, status, amount, description, remark, accountid, fromid, fromtype, currency "+wCond;
            String cQuery = "SELECT count(*) "+wCond;
            ps = connection.prepareStatement(selectQuery);
            resultSet = ps.executeQuery();

            ResultSet rs = Database.executeQuery(cQuery.toString(), connection);
            rs.next();
            authstartedCounter = rs.getInt(1);

            logger.debug("Select Query Payforasia---"+ps);
            logger.debug("Count Query Payforasia---"+cQuery);
            while (resultSet.next())
            {
                trackingid = resultSet.getString("trackingid");
                captureAmount = resultSet.getString("amount");
                accountid = resultSet.getString("accountid");
                toid = resultSet.getString("toid");
                sDescription = resultSet.getString("description");
                status = resultSet.getString("status");
                currency = resultSet.getString("currency");

                logger.debug("Result set---" + trackingid+accountid);

                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorId(toid);
                auditTrailVO.setActionExecutorName("PayforasiaAuthStartedCron");

                logger.debug("AuditTrailVO---");
                CommRequestVO requestVO = new CommRequestVO();
                CommResponseVO responseVO = null;
                CommTransactionDetailsVO transactionDetailsVO=new CommTransactionDetailsVO();
                logger.debug("before inquiry call---");

                transactionDetailsVO.setOrderId(trackingid);
                requestVO.setTransDetailsVO(transactionDetailsVO);

                PayforasiaPaymentGateway payforasiaPaymentGateway = new PayforasiaPaymentGateway(accountid);
                responseVO = (CommResponseVO) payforasiaPaymentGateway.processInquiry(requestVO);
                logger.debug("after inquiry call---");
                if (responseVO.getErrorCode().equals("0")) //Transaction Declined.
                {
                    logger.debug("If status is authstarted to authfailed---");
                    responseVO.setDescription("Transaction Failed");
                    responseVO.setRemark("Transaction Failed");
                    insertMainTableEntry("authfailed",responseVO.getDescription(),responseVO.getTransactionId(),trackingid);

                    statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid, "authfailed", connection);

                    entry.actionEntryForCommon(trackingid, captureAmount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, responseVO, auditTrailVO, null);

                    authFailedCounter++;
                }
                else if (responseVO.getErrorCode().equals("1")) //Transaction Approved.
                {
                    logger.debug("Else if status is authstarted to capturesuccess---");
                    responseVO.setDescription("Transaction Successful");
                    responseVO.setRemark("Transaction Successful");
                    insertMainTableEntry("capturesuccess",responseVO.getDescription(),responseVO.getTransactionId(),trackingid);

                    statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid, "capturesuccess", connection);

                    entry.actionEntryForCommon(trackingid, captureAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVO, null);

                    captureSuccesCounter++;
                }
                else if(responseVO.getErrorCode().equals("2"))  //Transaction not found.
                {
                    logger.debug("Else status is authstarted to 1---");
                    responseVO.setDescription("Transaction not found");
                    responseVO.setRemark("Transaction not found");
                    insertMainTableEntry("failed",responseVO.getDescription(),responseVO.getTransactionId(),trackingid);

                    statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid, "failed", connection);

                    entry.actionEntryForCommon(trackingid, captureAmount, ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, responseVO, auditTrailVO, null);

                    failedCounter++;

                    yTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,captureAmount,"failed"));
                }
                else
                {
                    logger.debug("Else status is authstarted to 0---");

                    notProcessedCounter++;

                    nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,captureAmount,status));
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(PayforasiaAuthStartedCron.class.getName(), "payforasiaAuthStartedCron()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), toid, "Payforasia Auth started cron");
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,captureAmount,status));
        }
        catch (PZTechnicalViolationException pzt)
        {
            logger.error("PZTechnicalViolationException exception",pzt);
            PZExceptionHandler.handleTechicalCVEException(pzt, toid, "PayforasiaAuthStartedCron");
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,captureAmount,status));
        }
        catch (PZDBViolationException pzd)
        {
            logger.error("PZDBViolationException",pzd);
            PZExceptionHandler.handleDBCVEException(pzd, toid, "PayforasiaAuthStartedCron");
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,captureAmount,status));
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError",systemError);
            PZExceptionHandler.raiseAndHandleDBViolationException(PayforasiaAuthStartedCron.class.getName(),"payforasiaAuthStartedCron()",null,"common","DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),toid,"Payforasia Auth started cron");
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,captureAmount,status));
        }
        finally
        {
            Database.closeConnection(connection);
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        success.append("<b>Date and Time : </b>"+String.valueOf(dateFormat.format(date))+"<br>");
        success.append("<b>Total Transactions in Authstarted/Capturestarted : </b>"+authstartedCounter+"<br>");
        success.append("<br>");
        success.append("<b>Capture Successful : </b>"+captureSuccesCounter+"<br>");
        success.append("<b>Authorization Failed : </b>"+authFailedCounter+"<br>");
        success.append("<b>Not Processed Transactions : </b>"+notProcessedCounter+"<br>");
        success.append("<b>Total Failed Transactions(Order Not found) : </b>"+failedCounter+"<br>");
        int tAdd = authFailedCounter+captureSuccesCounter+failedCounter+notProcessedCounter;
        success.append("<b>Total Transactions Processed : </b>"+tAdd+"<br><br>");

        //Auth/Capture started transaction table
        if(notProcessedCounter>0)
        {
            sHeader.append("<br>");
            sHeader.append("<b><u>Transactions in AuthStarted/Capture Started</b></u><br>");
            success.append("<br>");
            sHeader.append(getTableHeader());
            success.append("<br>");
            sHeader.append("<br>");
            sHeader.append(nTransaction);
            success.append("<br>");
            sHeader.append("</table>");
        }
        //Failed Transactions Table
        if(failedCounter>0)
        {
            sHeader.append("<br>");
            sHeader.append("<b><u>Transaction status changed from AuthStarted/CaptureStarted to Failed(Orders not found)</b></u><br>");
            success.append("<br>");
            sHeader.append(getTableHeader());
            success.append("<br>");
            sHeader.append("<br>");
            sHeader.append(yTransaction);
            success.append("<br>");
            sHeader.append("</table>");
        }
        success.append(sHeader);

        logger.debug("total count---"+success);
        SendTransactionEventMailUtil sendTransactionEventMailUtil = new SendTransactionEventMailUtil();
        sendTransactionEventMailUtil.sendTransactionEventMail(MailEventEnum.ADMIN_AUTHSTARTED_CRON_REPORT, "", "", success.toString(),null);

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
            logger.debug("Update query Payforasia---"+ps2);
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