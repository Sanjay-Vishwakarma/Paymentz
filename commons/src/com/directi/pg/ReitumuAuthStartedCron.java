package com.directi.pg;

import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.ReitumuBank.core.ReitumuBankSMSPaymentGateway;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
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
 * Created by Pradeep on 17/11/2015.
 */
public class ReitumuAuthStartedCron
{
    private static Logger logger = new Logger(ReitumuAuthStartedCron.class.getName());

    public static void main(String args[]) throws SystemError
    {
        ReitumuAuthStartedCron reitumuAuthStartedCron = new ReitumuAuthStartedCron();
        String sResponse = reitumuAuthStartedCron.reitumuAuthStartedCron(new Hashtable());
        //System.out.println("sResponse =" + sResponse);
    }

    public String reitumuAuthStartedCron(Hashtable ht) throws SystemError
    {
        StringBuffer success = new StringBuffer();
        success.append("Authstarted Transaction List"+"<br>");

        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        AbstractPaymentGateway abstractPaymentGateway = null;
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
            selectquery = "SELECT trackingid, toid, status, amount, description, remark, accountid, fromid, fromtype FROM transaction_common WHERE status='authstarted' AND (fromtype='rbm' OR fromtype='rbg') AND (TIMESTAMPDIFF(MINUTE, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) > 15";
            ps = connection.prepareStatement(selectquery);
            resultSet = ps.executeQuery();
            logger.debug("Select Query ReitumuAuthStartedCron----->"+ps);
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
                auditTrailVO.setActionExecutorName("ReitumuAuthStartedCron");
                logger.debug("fromid--" + fromid + "description--" + sDescription + "captureAmt--" + captureAmount + "accountid--" + accountId + "trackingid--" + trackingId + "fromtype--" + fromtype);

                CommRequestVO requestVO = new CommRequestVO();
                CommResponseVO responseVO = null;

                requestVO.setTransDetailsVO(transDetailsVO);

                ReitumuBankSMSPaymentGateway rsPg = new ReitumuBankSMSPaymentGateway(accountId);

                responseVO = (CommResponseVO) rsPg.processInquiry(requestVO);

                if (responseVO != null)
                {
                    //System.out.println("remark---"+responseVO.getRemark());
                    if(responseVO.getRemark().contains("Unknown merchant transaction") )//Transaction not found.
                    {
                        logger.debug("trackingid--if-"+trackingId);

                        String updatequery1 = "UPDATE transaction_common SET status='failed',remark='Transaction not processed' WHERE trackingid="+trackingId;
                        int i = Database.executeUpdate(updatequery1.toString(), connection);

                        if(i == 1)
                        {
                            ActionEntry entry = new ActionEntry() ;
                            int actionEntry = entry.actionEntryForCommon(trackingId, captureAmount, ActionEntry.ACTION_FAILED,ActionEntry.STATUS_FAILED,responseVO,auditTrailVO,null);

                            entry.closeConnection();

                            if(actionEntry == 1)
                            {
                                success.append("Success Treatment(Status :- authstarted to failed) for trackingid : "+ trackingId);
                                success.append("<br>");
                            }
                            else
                            {
                                success.append("Success Treatment in Primary Table. Failed  Treatment in Detail Table(Status:- authstarted to failed) for trackingid : "+ trackingId);
                                success.append("<br>");
                            }
                        }
                    }
                    else if(responseVO.getErrorCode()!=null || !responseVO.getErrorCode().equals("null"))
                    {
                        if(responseVO.getErrorCode().equals("000")|| responseVO.getErrorCode().equals("001") || responseVO.getErrorCode().equals("002") || responseVO.getErrorCode().equals("003") || responseVO.getErrorCode().equals("004") || responseVO.getErrorCode().equals("005") || responseVO.getErrorCode().equals("006") || responseVO.getErrorCode().equals("007"))
                        {
                            updatequery = "UPDATE transaction_common SET status='capturesuccess', captureamount=?, remark=?, paymentid=? WHERE trackingid=?";
                            logger.debug("Update Query ReitumuAuthStartedCron----->" + updatequery);
                            ps = connection.prepareStatement(updatequery);
                            ps.setString(1, captureAmount);
                            ps.setString(2, responseVO.getRemark());
                            ps.setString(3, responseVO.getResponseHashInfo());
                            ps.setString(4, trackingId);
                            logger.debug("update query in success---" + ps);
                            int rs = ps.executeUpdate();
                            logger.debug("updated records in success---" + rs);

                            statusSyncDAO.updateReconciliationTransactionCronFlag(trackingId, "capturesuccess", connection);

                            if (rs == 1)
                            {
                                ActionEntry entry = new ActionEntry();
                                int actionEntry = entry.actionEntryForCommon(trackingId, captureAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVO, null);
                                entry.closeConnection();

                                if (actionEntry == 1)
                                {
                                    success.append("Success  Treatment(Status:- authstarted to capturesuccess) for trackingid : " + trackingId);
                                    success.append("<br>");
                                }
                                else
                                {
                                    success.append("Success Treatment in Primary Table. Failed  treatment in Detail Table(Status:- authstarted to capturesuccess) for trackingid : " + trackingId);
                                    success.append("<br>");
                                }
                            }
                        }
                        else if (!(responseVO.getErrorCode().equals("000") || responseVO.getErrorCode().equals("001") || responseVO.getErrorCode().equals("002") || responseVO.getErrorCode().equals("003") || responseVO.getErrorCode().equals("004") ||
                            responseVO.getErrorCode().equals("005") || responseVO.getErrorCode().equals("006") || responseVO.getErrorCode().equals("007"))) //Transaction declined.
                        {
                            updatequery = "UPDATE transaction_common SET status='authfailed',paymentid=? remark=? WHERE trackingid=?";
                            ps = connection.prepareStatement(updatequery);
                            ps.setString(1, responseVO.getResponseHashInfo());
                            ps.setString(2, responseVO.getRemark());
                            ps.setString(3, trackingId);
                            logger.debug("update query in error---" + ps);
                            int i = ps.executeUpdate(updatequery);
                            logger.debug("update records in error---" + i);

                            statusSyncDAO.updateReconciliationTransactionCronFlag(trackingId, "authfailed", connection);

                            if (i == 1)
                            {
                                ActionEntry entry = new ActionEntry();
                                int actionEntry = entry.actionEntryForCommon(trackingId, captureAmount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, responseVO, auditTrailVO, null);

                                entry.closeConnection();

                                if (actionEntry == 1)
                                {
                                    success.append("Success  Treatment(Status:- authstarted to authfailed) for trackingid : " + trackingId);
                                    success.append("<br>");
                                }
                                else
                                {
                                    success.append("Success Treatment in Primary Table. Failed  treatment in Detail Table(Status:- authstarted to authfailed) for trackingid : " + trackingId);
                                    success.append("<br>");
                                }
                            }
                        }
                    }
                }
                else
                {
                    {
                        success.append("Failed  treatment as NULL Response from BANK (Status:- authstarted) for trackingid : "+ trackingId);
                        success.append("<br>");
                    }
                }
            }
        }
        catch (SQLException e)
        {
            success.append("Failed  treatment as Error  (Status:- authstarted) for trackingid : " + trackingId);
            success.append("<br>");
            logger.error("SQLException",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(ReitumuAuthStartedCron.class.getName(), "reitumuAuthStartedCron()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), toid, "Reitumu Auth started cron");
        }
        catch (PZTechnicalViolationException pzt)
        {
            success.append("Failed  treatment as Error (Status:- authstarted) for trackingid : "+ trackingId);
            success.append("<br>");
            logger.error("PZTechnicalViolationException exception",pzt);
            PZExceptionHandler.handleTechicalCVEException(pzt, toid, "Reitumu Auth started cron");
        }
        catch (PZDBViolationException pzd)
        {
            success.append("Failed  treatment as Error  (Status:- authstarted) for trackingid : "+ trackingId);
            success.append("<br>");
            logger.error("PZDBViolationException",pzd);
            PZExceptionHandler.handleDBCVEException(pzd, toid, "Reitumu Auth started cron");
        }
        catch (SystemError systemError)
        {
            success.append("Failed  treatment as Error  (Status:- authstarted) for trackingid : " + trackingId);
            success.append("<br>");
            logger.error("SystemError",systemError);
            PZExceptionHandler.raiseAndHandleDBViolationException(ReitumuAuthStartedCron.class.getName(),"reitumuAuthStartedCron()",null,"common","DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),toid,"Reitumu Auth started cron");
        }
        finally
        {
            Database.closeConnection(connection);
        }

        SendTransactionEventMailUtil sendTransactionEventMailUtil=new SendTransactionEventMailUtil();
        sendTransactionEventMailUtil.sendTransactionEventMail(MailEventEnum.ADMIN_AUTHSTARTED_CRON_REPORT, "", "", success.toString(), null);

        return success.toString();
    }
}
