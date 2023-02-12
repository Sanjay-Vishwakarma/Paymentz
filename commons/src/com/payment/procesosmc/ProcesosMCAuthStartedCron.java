package com.payment.procesosmc;

import com.directi.pg.*;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.common.core.CommMerchantVO;
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
 * Created by IntelliJ IDEA.
 * User: Supriya
 * Date: 12/01/2017
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */

public class ProcesosMCAuthStartedCron
{
    private static Logger logger=new Logger(ProcesosMCAuthStartedCron.class.getName());
    public static void main(String[] args)
    {
        ProcesosMCAuthStartedCron procesosMCAuthStartedCron =new ProcesosMCAuthStartedCron();
        String sResponse= procesosMCAuthStartedCron.pmcAuthStartedCron(new Hashtable());
        //System.out.println("sResponse====="+sResponse);
    }
    public String pmcAuthStartedCron(Hashtable hashtable)
    {
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        CommTransactionDetailsVO commTransactionDetailsVO=null;
        AuditTrailVO auditTrailVO=null;
        CommRequestVO commRequestVO=null;
        CommMerchantVO commMerchantVO=null;
        StringBuffer msgBuffer=new StringBuffer();
        ActionEntry entry = new ActionEntry();
        Connection conn=null;
        ResultSet rs=null;
        PreparedStatement pstmt=null;
        String query="";
        String trackingId=null;
        String amount=null;
        String accountId=null;
        String description=null;
        String fromId=null;
        String toId=null;
        try
        {
            conn= Database.getConnection();
            query="SELECT trackingid,toid,status,amount,description,remark,accountid,fromid,fromtype FROM transaction_common WHERE STATUS='authstarted' AND fromtype='procesosmc' AND (TIMESTAMPDIFF(MINUTE, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) > 15 order by trackingid desc limit 5";
            pstmt=conn.prepareStatement(query);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                trackingId=rs.getString("trackingid");
                amount=rs.getString("amount");
                accountId = rs.getString("accountid");
                description=rs.getString("description");
                fromId = rs.getString("fromid");
                toId = rs.getString("toid");

                commTransactionDetailsVO=new CommTransactionDetailsVO();
                commTransactionDetailsVO.setAmount(amount);
                commTransactionDetailsVO.setOrderDesc(description);
                commTransactionDetailsVO.setFromid(fromId);
                commTransactionDetailsVO.setPreviousTransactionId(trackingId);

                auditTrailVO=new AuditTrailVO();
                auditTrailVO.setActionExecutorId(toId);
                auditTrailVO.setActionExecutorName("admin(pmc authstarted cron)");

                commMerchantVO=new CommMerchantVO();
                commMerchantVO.setAccountId(accountId);

                commRequestVO=new CommRequestVO();
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                commRequestVO.setCommMerchantVO(commMerchantVO);

                ProcesosMCPaymentGateway procesosMCPaymentGateway=new ProcesosMCPaymentGateway(accountId);
                CommResponseVO responseVO=null;
                try
                {
                    responseVO = (CommResponseVO)procesosMCPaymentGateway.processInquiry(commRequestVO);
                }
                catch (PZTechnicalViolationException e)
                {
                    logger.error("PZTechnicalViolationException:::::::"+e);
                    continue;
                }

                if(responseVO!=null)
                {
                    if("failed".equals(responseVO.getStatus())) // Transaction Declined
                    {
                        String updateQuery = "UPDATE transaction_common SET status='authfailed', remark='Transaction Failed',paymentid=? WHERE trackingid=?";
                        PreparedStatement preparedStatement1 = conn.prepareStatement(updateQuery);
                        preparedStatement1.setString(1,responseVO.getTransactionId());
                        preparedStatement1.setString(2,trackingId);
                        int rs1 = preparedStatement1.executeUpdate();
                        if (rs1 == 1)
                        {
                            statusSyncDAO.updateReconciliationTransactionCronFlag(trackingId, "authfailed",conn);
                            int actionEntry = entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, responseVO, auditTrailVO, "");
                            if (actionEntry == 1)
                            {
                                msgBuffer.append("Success treatment(Status :- authstarted to authfailed) for trackingid : " + trackingId);
                                msgBuffer.append("<br>");
                            }
                            else
                            {
                                msgBuffer.append("Success Treatment in Primary Table. Failed  treatment in Detail Table(Status:- authstarted to authfailed) for trackingid : " + trackingId);
                                msgBuffer.append("<br>");
                            }
                        }
                    }
                    else if("success".equals(responseVO.getStatus()))//Transaction Approved
                    {
                        query="UPDATE transaction_common SET status='authsuccessful',remark='"+responseVO.getDescription()+"',paymentid='"+responseVO.getTransactionId()+"' WHERE trackingid=?";
                        PreparedStatement ps2=conn.prepareStatement(query);
                        ps2.setString(1,trackingId);
                        int rs2 = ps2.executeUpdate();
                        if(rs2==1)
                        {
                            statusSyncDAO.updateReconciliationTransactionCronFlag(trackingId, "authsuccessful",conn);
                            int actionEntry = entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, responseVO, auditTrailVO, null);
                            if (actionEntry == 1)
                            {
                                msgBuffer.append("Success  Treatment(Status:- authstarted to authsuccessful) for trackingid : " + trackingId);
                                msgBuffer.append("<br>");
                            }
                            else
                            {
                                msgBuffer.append("Success Treatment in Primary Table. Failed  treatment in Detail Table(Status:- authstarted to authsuccessful) for trackingid : " + trackingId);
                                msgBuffer.append("<br>");
                            }
                        }
                    }
                    else//Transaction not found at bank side
                    {
                        query = "UPDATE transaction_common SET status='failed', remark='Transaction not found at bank' WHERE trackingid=?";
                        PreparedStatement ps3 = conn.prepareStatement(query);
                        ps3.setString(1, trackingId);
                        int rs3 = ps3.executeUpdate();
                        if (rs3 == 1)
                        {
                            statusSyncDAO.updateReconciliationTransactionCronFlag(trackingId, "failed", conn);
                            int actionEntry = entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, responseVO, auditTrailVO, null);
                            entry.closeConnection();
                            if (actionEntry == 1)
                            {
                                msgBuffer.append("Success treatment(Status :- authstarted to failed) for trackingid : " + trackingId);
                                msgBuffer.append("<br>");
                            }
                            else
                            {
                                msgBuffer.append("Success Treatment in Primary Table. Failed  treatment in Detail Table(Status:- authstarted to failed) for trackingid : " + trackingId);
                                msgBuffer.append("<br>");
                            }
                        }
                    }
                }
                else
                {
                    msgBuffer.append("Failed  treatment as NULL Response from BANK (Status:- authstarted) for trackingid : "+ trackingId);
                    msgBuffer.append("<br>");
                }
            }
        }
        catch (SQLException e)
        {
            msgBuffer.append("Failed  treatment as Error  (Status:- authstarted) for trackingid : " + trackingId);
            msgBuffer.append("<br>");
            logger.error("SQLException",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(ProcesosMCAuthStartedCron.class.getName(), "pmcAuthStartedCron()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), toId, "PMC Auth started cron");
        }
        catch (PZDBViolationException pzd)
        {
            msgBuffer.append("Failed  treatment as Error  (Status:- authstarted) for trackingid : " + trackingId);
            msgBuffer.append("<br>");
            logger.error("PZDBViolationException",pzd);
            PZExceptionHandler.handleDBCVEException(pzd, toId, "PMCAuthStartedCron");
        }
        catch (SystemError systemError)
        {
            msgBuffer.append("Failed  treatment as Error  (Status:- authstarted) for trackingid : " + trackingId);
            msgBuffer.append("<br>");
            logger.error("SystemError",systemError);
            PZExceptionHandler.raiseAndHandleDBViolationException(ProcesosMCAuthStartedCron.class.getName(),"pmcAuthStartedCron()",null,"common","DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),toId,"PMC Auth started cron");
        }
        finally
        {
            Database.closeConnection(conn);
        }
        SendTransactionEventMailUtil sendTransactionEventMailUtil=new SendTransactionEventMailUtil();
        sendTransactionEventMailUtil.sendTransactionEventMail(MailEventEnum.ADMIN_AUTHSTARTED_CRON_REPORT, "", "", msgBuffer.toString(), null);
        return  msgBuffer.toString();
    }
}