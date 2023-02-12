package com.directi.pg;

import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.ReitumuBank.core.ReitumuBankSMSPaymentGateway;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.statussync.StatusSyncDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created by ThinkPadT410 on 8/19/2016.
 */
public class ReitumuMarkedForReversalCron
{
    private static Logger logger = new Logger(ReitumuMarkedForReversalCron.class.getName());

    public static void main(String args[]) throws SystemError
    {
        ReitumuMarkedForReversalCron reitumuMarkedForReversalCron = new ReitumuMarkedForReversalCron();
        String sResponse = reitumuMarkedForReversalCron.reitumuMarkedForReversalCron(new Hashtable());
        //System.out.println("sResponse =" + sResponse);
    }

    public String reitumuMarkedForReversalCron(Hashtable ht) throws SystemError
    {
        StringBuffer success = new StringBuffer();
        success.append("Marked For Reversal Transaction List"+"<br>");

        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        Connection connection = null;
        ResultSet resultSet=null;
        PreparedStatement ps=null;

        String trackingId = null;
        String fromtype = null;
        String accountId = null;
        String toid = null;
        String captureAmount = null;
        String fromid = null;
        String sDescription = null;
        String updatequery = null;
        String selectquery = null;

        try
        {
            connection = Database.getConnection();
            selectquery = "SELECT trackingid, toid, status, amount, description, remark, accountid, fromid, fromtype FROM transaction_common WHERE status='markedforreversal' AND (fromtype='rbm' OR fromtype='rbg') AND (TIMESTAMPDIFF(MINUTE, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) > 15";
            //selectquery = "SELECT trackingid, toid, status, amount, description, remark, accountid, fromid, fromtype FROM transaction_common WHERE status='markedforreversal' AND (fromtype='rbm' OR fromtype='rbg') AND trackingid='15910'";
            ps = connection.prepareStatement(selectquery);
            resultSet = ps.executeQuery();
            logger.debug("Select Query ReitumuMarkedForReversalCron----->"+ps);
            while(resultSet.next())
            {
                trackingId = resultSet.getString("trackingid");
                accountId = resultSet.getString("accountid");
                captureAmount = resultSet.getString("amount");
                fromid = resultSet.getString("fromid");
                toid = resultSet.getString("toid");
                fromtype = resultSet.getString("fromtype");
                sDescription = resultSet.getString("description");
                logger.debug("ResultSet---"+trackingId);
                AuditTrailVO auditTrailVO = new AuditTrailVO();
                CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();

                transDetailsVO.setPreviousTransactionId(trackingId);
                auditTrailVO.setActionExecutorId(toid);
                auditTrailVO.setActionExecutorName("ReitumuMarkedForReversalCron");
                logger.debug("fromid--" + fromid + "description--" + sDescription + "captureAmt--" + captureAmount + "accountid--" + accountId + "trackingid--" + trackingId + "fromtype--" + fromtype);

                CommRequestVO requestVO = new CommRequestVO();
                CommResponseVO responseVO = null;

                requestVO.setTransDetailsVO(transDetailsVO);

                ReitumuBankSMSPaymentGateway rsPg = new ReitumuBankSMSPaymentGateway(accountId);

                responseVO = (CommResponseVO) rsPg.processInquiry(requestVO);

                if (responseVO != null)
                {
                    if(responseVO.getErrorCode()!=null || !responseVO.getErrorCode().equals("null"))
                    {
                        if(responseVO.getErrorCode().equals("000")|| responseVO.getErrorCode().equals("001") || responseVO.getErrorCode().equals("002") || responseVO.getErrorCode().equals("003") || responseVO.getErrorCode().equals("004") || responseVO.getErrorCode().equals("005") || responseVO.getErrorCode().equals("006") || responseVO.getErrorCode().equals("007"))
                        {
                            updatequery = "UPDATE transaction_common SET status='reversed', captureamount=?, remark='Transaction Reversed', paymentid=? WHERE trackingid=?";
                            logger.debug("Update Query ReitumuMarkedForReversalCron----->" + updatequery);
                            ps = connection.prepareStatement(updatequery);
                            ps.setString(1, captureAmount);
                           // ps.setString(2, responseVO.getRemark());
                            ps.setString(2, responseVO.getResponseHashInfo());
                            ps.setString(3, trackingId);
                            logger.debug("update query in success---" + ps);
                            int rs = ps.executeUpdate();
                            logger.debug("updated records in success---" + rs);

                            statusSyncDAO.updateReconciliationTransactionCronFlag(trackingId, "reversed", connection);

                            if (rs == 1)
                            {
                                ActionEntry entry = new ActionEntry();
                                responseVO.setTransactionType("Refund");
                                int actionEntry = entry.actionEntryForCommon(trackingId, captureAmount, ActionEntry.ACTION_REVERSAL_SUCCESSFUL, ActionEntry.STATUS_REVERSAL_SUCCESSFUL, responseVO, auditTrailVO, null);
                                entry.closeConnection();

                                if (actionEntry == 1)
                                {
                                    success.append("Success  Treatment(Status:- markedforreversal to reversed) for trackingid : " + trackingId);
                                    success.append("<br>");
                                }
                                else
                                {
                                    success.append("Success Treatment in Primary Table. Failed  treatment in Detail Table(Status:- markedforreversal to reversed) for trackingid : " + trackingId);
                                    success.append("<br>");
                                }
                            }
                        }
                        else if (!(responseVO.getErrorCode().equals("000") || responseVO.getErrorCode().equals("001") || responseVO.getErrorCode().equals("002") || responseVO.getErrorCode().equals("003") || responseVO.getErrorCode().equals("004") ||
                                responseVO.getErrorCode().equals("005") || responseVO.getErrorCode().equals("006") || responseVO.getErrorCode().equals("007"))) //Transaction declined.
                        {
                            updatequery = "UPDATE transaction_common SET status='capturesuccess',paymentid=? remark='Transaction Succeeded' WHERE trackingid=?";
                            ps = connection.prepareStatement(updatequery);
                            ps.setString(1, responseVO.getResponseHashInfo());
                           //ps.setString(2, responseVO.getRemark());
                            ps.setString(2, trackingId);
                            logger.debug("update query in error---" + ps);
                            int i = ps.executeUpdate(updatequery);
                            logger.debug("update records in error---" + i);

                            statusSyncDAO.updateReconciliationTransactionCronFlag(trackingId, "capturesuccess", connection);

                            if (i == 1)
                            {
                                ActionEntry entry = new ActionEntry();
                                int actionEntry = entry.actionEntryForCommon(trackingId, captureAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVO, null);

                                entry.closeConnection();

                                if (actionEntry == 1)
                                {
                                    success.append("Success  Treatment(Status:- markedforreversal to capturesuccess) for trackingid : " + trackingId);
                                    success.append("<br>");
                                }
                                else
                                {
                                    success.append("Success Treatment in Primary Table. Failed  treatment in Detail Table(Status:- markedforreversal to capturesuccess) for trackingid : " + trackingId);
                                    success.append("<br>");
                                }
                            }
                        }
                    }
                }
                else
                {
                    {
                        success.append("Failed  treatment as NULL Response from BANK (Status:- markedforreversal) for trackingid : "+ trackingId);
                        success.append("<br>");
                    }
                }
            }
        }
        catch (SQLException e)
        {
            success.append("Failed  treatment as Error  (Status:- markedforreversal) for trackingid : " + trackingId);
            success.append("<br>");
            logger.error("SQLException",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(ReitumuMarkedForReversalCron.class.getName(), "reitumuMarkedForReversalCron()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), toid, "Reitumu Marked For Reversak cron");
        }
        catch (PZTechnicalViolationException pzt)
        {
            success.append("Failed  treatment as Error (Status:- markedforreversal) for trackingid : "+ trackingId);
            success.append("<br>");
            logger.error("PZTechnicalViolationException exception",pzt);
            PZExceptionHandler.handleTechicalCVEException(pzt, toid, "Reitumu Marked For Reversal cron");
        }
        catch (PZDBViolationException pzd)
        {
            success.append("Failed  treatment as Error  (Status:- markedforreversal) for trackingid : "+ trackingId);
            success.append("<br>");
            logger.error("PZDBViolationException",pzd);
            PZExceptionHandler.handleDBCVEException(pzd, toid, "Reitumu Marked For Reversal cron");
        }
        catch (SystemError systemError)
        {
            success.append("Failed  treatment as Error  (Status:- markedforreversal) for trackingid : " + trackingId);
            success.append("<br>");
            logger.error("SystemError",systemError);
            PZExceptionHandler.raiseAndHandleDBViolationException(ReitumuMarkedForReversalCron.class.getName(),"reitumuMarkedForReversalCron()",null,"common","DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),toid,"Reitumu Marked For Reversal cron");
        }
        finally
        {
            Database.closeConnection(connection);
        }

        SendTransactionEventMailUtil sendTransactionEventMailUtil=new SendTransactionEventMailUtil();
        sendTransactionEventMailUtil.sendTransactionEventMail(MailEventEnum.ADMIN_MARKEDFORREVERSAL_CRON_REPORT, "", "", success.toString(),null);

        return success.toString();
    }
}
