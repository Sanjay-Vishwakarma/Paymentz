package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.vo.ManualReconVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.statussync.StatusSyncDAO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created by Jitendra on 4/4/2018.
 */
public class ReconDao
{
    Logger logger = new Logger(ReconDao.class.getName());
    Functions functions=new Functions();

    public Hashtable getReconListTransaction(String accountId,String partnerId, String fromDate1, String toDate1, String memberId, StringBuffer trackingId,String paymentId,String orderId, String status, int start, int end)throws  PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        Hashtable hash = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        int counter = 1;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            Functions functions = new Functions();
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            StringBuilder query = new StringBuilder("select trackingid,toid,description,fromid,amount,chargebackamount,status,timestamp,t.notificationUrl,t.accountid,paymentid,refundamount,captureamount,partnerid,fromtype from transaction_common as t JOIN members as m ON t.toid=m.memberid where (status IN('authstarted','markedforreversal','capturestarted','authstarted_3D','authfailed','capturesuccess','failed') OR (STATUS IN('chargeback','chargebackreversed')AND chargebackamount>0.00))");
            StringBuilder countQuery = new StringBuilder("select count(*) from transaction_common as t JOIN members as m ON t.toid=m.memberid where (status IN('authstarted','markedforreversal','capturestarted','authstarted_3D','authfailed','capturesuccess','failed') OR (STATUS IN('chargeback','chargebackreversed')AND chargebackamount>0.00))");
            if (functions.isValueNull(partnerId))
            {
                query.append(" and m.partnerid IN("+partnerId+")");
                countQuery.append(" and m.partnerid IN("+partnerId+")");
            }
            if (functions.isValueNull(fromDate1))
            {
                query.append(" and FROM_UNIXTIME(t.dtstamp) >= ?");
                countQuery.append(" and FROM_UNIXTIME(t.dtstamp) >=  ?");
            }
            if (functions.isValueNull(toDate1))
            {
                query.append(" and FROM_UNIXTIME(t.dtstamp) <= ? ");
                countQuery.append(" and FROM_UNIXTIME(t.dtstamp) <= ?");
            }
            if (functions.isValueNull(accountId))
            {
                query.append(" and t.accountid=?");
                countQuery.append(" and t.accountid= ? ");
            }
            if (functions.isValueNull(memberId))
            {
                query.append(" and toid= ? ");
                countQuery.append(" and toid= ? ");
            }
            if (functions.isValueNull(trackingId.toString()))
            {
                query.append(" and trackingid IN("+trackingId.toString()+")");
                countQuery.append(" and trackingid IN("+trackingId.toString()+")");
            }
            if (functions.isValueNull(paymentId))
            {
                query.append(" and paymentid= ? ");
                countQuery.append(" and paymentid= ? ");
            }
            if (functions.isValueNull(orderId))
            {
                query.append(" and description= ? ");
                countQuery.append(" and description= ? ");
            }

            if (functions.isValueNull(status) && !status.equalsIgnoreCase("all"))
            {
                query.append(" and status =  '"+status+"' ");
                countQuery.append(" and status = '"+status+"' ");
            }

            query.append(" order by trackingid desc LIMIT ? , ? ");

            pstmt = conn.prepareStatement(query.toString());
            pstmt1 = conn.prepareStatement(countQuery.toString());

            /*if (functions.isValueNull(partnerId))
            {
                pstmt.setString(counter, partnerId);
                pstmt1.setString(counter, partnerId);
                counter++;
            }*/
            if (functions.isValueNull(fromDate1))
            {
                pstmt.setString(counter, fromDate1);
                pstmt1.setString(counter, fromDate1);
                counter++;
            }
            if (functions.isValueNull(toDate1))
            {
                pstmt.setString(counter, toDate1);
                pstmt1.setString(counter, toDate1);
                counter++;
            }
            if (functions.isValueNull(accountId))
            {
                pstmt.setString(counter, accountId);
                pstmt1.setString(counter, accountId);
                counter++;
            }
            if (functions.isValueNull(memberId))
            {
                pstmt.setString(counter, memberId);
                pstmt1.setString(counter, memberId);
                counter++;
            }
            /*if (functions.isValueNull(trackingId.toString()))
            {
                pstmt.setString(counter, trackingId.toString());
                pstmt1.setString(counter, trackingId.toString());
                counter++;
            }*/
            if (functions.isValueNull(paymentId))
            {
                pstmt.setString(counter, paymentId);
                pstmt1.setString(counter, paymentId);
                counter++;
            }
            if (functions.isValueNull(orderId))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, orderId));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, orderId));
                counter++;
            }

            pstmt.setInt(counter, start);
            counter++;
            pstmt.setInt(counter, end);

            logger.debug("query pstmt ::::" + pstmt);
            System.out.println("query pstmt ::::" + pstmt);
            logger.debug("countQuery pstmt1::::"+pstmt1);

            hash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());
            rs = pstmt1.executeQuery();
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException e){
            logger.error("SQLException::::: ",e);
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(),"getReconListTransaction()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se){
            logger.error("SystemError:::::",se);
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(),"getReconListTransaction()", null, "Common", "SystemError Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(conn);
        }
        return hash;
    }
    public boolean giveFailedTreatment(ManualReconVO manualReconVO)throws PZDBViolationException
    {
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        Connection conn = null;
        boolean isTreatmentGiven=false;
        try
        {
            logger.debug("at line 115");
            conn=Database.getConnection();
            PreparedStatement pstmt=null;
            StringBuffer query=new StringBuffer("update transaction_common set status='failed',remark=? where trackingid=?");
            pstmt=conn.prepareStatement(query.toString());
            logger.debug("line 120 in dao");
            pstmt.setString(1,manualReconVO.getRemark());
            pstmt.setString(2,manualReconVO.getTrackingId());
            int i= pstmt.executeUpdate();
            logger.debug("i in dao:::::"+i);
            if(i==1)
            {
                query=new StringBuffer("INSERT INTO transaction_common_details (trackingid,action,status,responsetime,amount,actionexecutorid,actionexecutorname) VALUES (?,?,?,?,?,?,?)");
                pstmt=conn.prepareStatement(query.toString());
                pstmt.setString(1,manualReconVO.getTrackingId());
                pstmt.setString(2,"Validation Failed");
                pstmt.setString(3,"failed");
                pstmt.setString(4,manualReconVO.getTime());
                pstmt.setString(5,manualReconVO.getAmount());
                pstmt.setString(6,manualReconVO.getActionExecutorId());
                pstmt.setString(7,manualReconVO.getActionExecutorName());
                int j= pstmt.executeUpdate();
                if(j==1)
                {
                    isTreatmentGiven=true;
                }
                statusSyncDAO.updateReconciliationTransactionCronFlag(manualReconVO.getTrackingId(),"failed",conn);
            }
        }
        catch (SQLException e){
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(), "giveFailedTreatment()", null, "Common", "SqlException while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(), "giveFailedTreatment()", null, "Common", "SystemError while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return isTreatmentGiven;
    }
    public boolean giveAuthFailedTreatment(ManualReconVO manualReconVO)throws PZDBViolationException
    {
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        Connection conn = null;
        boolean isTreatmentGiven=false;
        try
        {
            conn=Database.getConnection();
            PreparedStatement pstmt=null;
            StringBuffer query=new StringBuffer("update transaction_common set status='authfailed', paymentid=?, remark=? where description=? and trackingid=?");
            pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1,manualReconVO.getPaymentOrderNumber());
            pstmt.setString(2,manualReconVO.getRemark());
            pstmt.setString(3,manualReconVO.getDesc());
            pstmt.setString(4,manualReconVO.getTrackingId());
            int i= pstmt.executeUpdate();
            if(i==1)
            {
                query=new StringBuffer("INSERT INTO transaction_common_details (trackingid,action,status,responsetime,amount,remark,responsetransactionid,actionexecutorid,actionexecutorname) VALUES (?,?,?,?,?,?,?,?,?)");
                pstmt=conn.prepareStatement(query.toString());
                pstmt.setString(1,manualReconVO.getTrackingId());
                pstmt.setString(2,"Authorisation Failed");
                pstmt.setString(3,"authfailed");
                pstmt.setString(4,manualReconVO.getTime());
                pstmt.setString(5,manualReconVO.getAmount());
                pstmt.setString(6,manualReconVO.getRemark());
                pstmt.setString(7,manualReconVO.getDbPaymentNumber());
                pstmt.setString(8,manualReconVO.getActionExecutorId());
                pstmt.setString(9,manualReconVO.getActionExecutorName());
                int j= pstmt.executeUpdate();
                if(j==1)
                {
                    isTreatmentGiven=true;
                }
                statusSyncDAO.updateReconciliationTransactionCronFlag(manualReconVO.getTrackingId(),"authfailed",conn);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(), "giveAuthFailedTreatment()", null, "Common", "SqlException while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {

            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(), "giveAuthFailedTreatment()", null, "Common", "SystemError while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isTreatmentGiven;
    }
    public boolean giveAuthSuccessTreatment(ManualReconVO manualReconVO)throws PZDBViolationException
    {
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        Connection conn = null;
        boolean isTreatmentGiven=false;
        try
        {
            conn=Database.getConnection();
            PreparedStatement pstmt=null;
            StringBuffer query=new StringBuffer("update transaction_common set status='authsuccessful', paymentid=?, remark=? where description=? and trackingid=?");
            pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1,manualReconVO.getPaymentOrderNumber());
            pstmt.setString(2,manualReconVO.getRemark());
            pstmt.setString(3,manualReconVO.getDesc());
            pstmt.setString(4,manualReconVO.getTrackingId());
            int i= pstmt.executeUpdate();
            if(i==1)
            {
                query=new StringBuffer("INSERT INTO transaction_common_details (trackingid,action,status,responsetime,amount,remark,responsetransactionid,actionexecutorid,actionexecutorname) VALUES (?,?,?,?,?,?,?,?,?)");
                pstmt=conn.prepareStatement(query.toString());
                pstmt.setString(1,manualReconVO.getTrackingId());
                pstmt.setString(2,"Auth Successful");
                pstmt.setString(3,"authsuccessful");
                pstmt.setString(4,manualReconVO.getTime());
                pstmt.setString(5,manualReconVO.getAmount());
                pstmt.setString(6,manualReconVO.getRemark());
                pstmt.setString(7,manualReconVO.getDbPaymentNumber());
                pstmt.setString(8,manualReconVO.getActionExecutorId());
                pstmt.setString(9,manualReconVO.getActionExecutorName());
                int j= pstmt.executeUpdate();
                if(j==1)
                {
                    isTreatmentGiven=true;
                }
                statusSyncDAO.updateReconciliationTransactionCronFlag(manualReconVO.getTrackingId(),"authsuccessful",conn);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(), "giveAuthFailedTreatment()", null, "Common", "SqlException while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {

            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(), "giveAuthFailedTreatment()", null, "Common", "SystemError while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isTreatmentGiven;
    }
    public boolean giveAuthStartedTreatment(ManualReconVO manualReconVO)throws PZDBViolationException
    {
        StatusSyncDAO statusSyncDAO =new StatusSyncDAO();
        Connection conn = null;
        boolean isTreatmentGiven=false;
        try
        {
            conn=Database.getConnection();
            PreparedStatement pstmt=null;
            StringBuffer query=new StringBuffer("update transaction_common set status='authstarted',paymentid=? where trackingid=?");
            pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1,manualReconVO.getPaymentOrderNumber());
            pstmt.setString(2,manualReconVO.getTrackingId());
            int i= pstmt.executeUpdate();
            if(i==1)
            {
                query=new StringBuffer("INSERT INTO transaction_common_details (trackingid,ACTION,STATUS,responsetime,amount,actionexecutorid,actionexecutorname) VALUES (?,?,?,?,?,?,?)");
                pstmt=conn.prepareStatement(query.toString());
                pstmt.setString(1,manualReconVO.getTrackingId());
                pstmt.setString(2,"Auth Started");
                pstmt.setString(3,"authstarted");
                pstmt.setString(4,manualReconVO.getTime());
                pstmt.setString(5,manualReconVO.getAmount());
                pstmt.setString(6,manualReconVO.getActionExecutorId());
                pstmt.setString(7,manualReconVO.getActionExecutorName());
                int j= pstmt.executeUpdate();
                if(j==1)
                {
                    isTreatmentGiven=true;
                }
                statusSyncDAO.updateReconciliationTransactionCronFlag(manualReconVO.getTrackingId(),"authstarted",conn);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(), "giveAuthStartedTreatment()", null, "Common", "SqlException while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {

            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(), "giveAuthStartedTreatment()", null, "Common", "SystemError while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isTreatmentGiven;
    }

    public boolean giveAuthStarted3DTreatment(ManualReconVO manualReconVO)throws PZDBViolationException
    {
        StatusSyncDAO statusSyncDAO =new StatusSyncDAO();
        Connection conn = null;
        boolean isTreatmentGiven=false;
        try
        {
            conn=Database.getConnection();
            PreparedStatement pstmt=null;
            StringBuffer query=new StringBuffer("update transaction_common set status='authstarted_3D',paymentid=? where trackingid=?");
            pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1,manualReconVO.getPaymentOrderNumber());
            pstmt.setString(2,manualReconVO.getTrackingId());
            int i= pstmt.executeUpdate();
            if(i==1)
            {
                query=new StringBuffer("INSERT INTO transaction_common_details (trackingid,ACTION,STATUS,responsetime,amount,actionexecutorid,actionexecutorname) VALUES (?,?,?,?,?,?,?)");
                pstmt=conn.prepareStatement(query.toString());
                pstmt.setString(1,manualReconVO.getTrackingId());
                pstmt.setString(2,"Auth Started 3D");
                pstmt.setString(3,"authstarted_3D");
                pstmt.setString(4,manualReconVO.getTime());
                pstmt.setString(5,manualReconVO.getAmount());
                pstmt.setString(6,manualReconVO.getActionExecutorId());
                pstmt.setString(7,manualReconVO.getActionExecutorName());
                int j= pstmt.executeUpdate();
                if(j==1)
                {
                    isTreatmentGiven=true;
                }
                statusSyncDAO.updateReconciliationTransactionCronFlag(manualReconVO.getTrackingId(),"authstarted_3D",conn);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(), "giveAuthStartedTreatment()", null, "Common", "SqlException while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {

            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(), "giveAuthStartedTreatment()", null, "Common", "SystemError while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isTreatmentGiven;
    }

    public boolean giveCaptureSuccessTreatment(ManualReconVO manualReconVO)throws PZDBViolationException
    {
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        Connection conn = null;
        boolean isTreatmentGiven=false;
        try
        {
            conn=Database.getConnection();
            PreparedStatement pstmt=null;
            StringBuffer query=new StringBuffer("update transaction_common set remark='Payment Success',status='capturesuccess',captureamount=?,paymentid=? where description=? and trackingid=?");
            pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1,manualReconVO.getCaptureAmount());
            pstmt.setString(2, manualReconVO.getPaymentOrderNumber());
            pstmt.setString(3,manualReconVO.getDesc());
            pstmt.setString(4,manualReconVO.getTrackingId());
            int i= pstmt.executeUpdate();
            if(i==1)
            {
                query=new StringBuffer("INSERT INTO transaction_common_details (trackingid,action,status,amount,responsetime,responsetransactionid,responsetransactionstatus,responsedescriptor,actionexecutorid,actionexecutorname) VALUES (?,?,?,?,?,?,?,?,?,?)");
                pstmt=conn.prepareStatement(query.toString());
                pstmt.setString(1,manualReconVO.getTrackingId());
                pstmt.setString(2,"Capture Successful");
                pstmt.setString(3,"capturesuccess");
                pstmt.setString(4,manualReconVO.getCaptureAmount());
                pstmt.setString(5,manualReconVO.getTime());
                pstmt.setString(6,manualReconVO.getPaymentOrderNumber());
                pstmt.setString(7,"Payment Success");
                pstmt.setString(8,GatewayAccountService.getGatewayAccount(manualReconVO.getAccountId()).getDisplayName().toString());
                pstmt.setString(9,manualReconVO.getActionExecutorId());
                pstmt.setString(10,manualReconVO.getActionExecutorName());
                int j= pstmt.executeUpdate();
                if(j==1){
                    isTreatmentGiven=true;
                }
                statusSyncDAO.updateReconciliationTransactionCronFlag(manualReconVO.getTrackingId(),"capturesuccess",conn);
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(), "giveCaptureSuccessTreatment()", null, "Common", "SqlException while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(), "giveCaptureSuccessTreatment()", null, "Common", "SystemError while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return isTreatmentGiven;
    }
    public boolean giveCaptureFailedTreatment(ManualReconVO manualReconVO)throws PZDBViolationException
    {
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        Connection conn = null;
        boolean isTreatmentGiven=false;
        try
        {
            conn=Database.getConnection();
            PreparedStatement pstmt=null;
            StringBuffer query=new StringBuffer("update transaction_common set status='capturefailed',paymentid=?,remark=? where description=? and trackingid=?");
            pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1,manualReconVO.getPaymentOrderNumber());
            pstmt.setString(2,manualReconVO.getRemark());
            pstmt.setString(3,manualReconVO.getDesc());
            pstmt.setString(4,manualReconVO.getTrackingId());
            int i= pstmt.executeUpdate();
            if(i==1)
            {
                query=new StringBuffer("INSERT INTO transaction_common_details (trackingid,ACTION,STATUS,responsetime,amount,remark,responsetransactionid,actionexecutorid,actionexecutorname) VALUES (?,?,?,?,?,?,?,?,?)");
                pstmt=conn.prepareStatement(query.toString());
                pstmt.setString(1,manualReconVO.getTrackingId());
                pstmt.setString(2,"Capture Failed");
                pstmt.setString(3,"capturefailed");
                pstmt.setString(4,manualReconVO.getTime());
                pstmt.setString(5,manualReconVO.getAmount());
                pstmt.setString(6,manualReconVO.getRemark());
                pstmt.setString(7,manualReconVO.getDbPaymentNumber());
                pstmt.setString(8,manualReconVO.getActionExecutorId());
                pstmt.setString(9,manualReconVO.getActionExecutorName());
                int j= pstmt.executeUpdate();
                if(j==1)
                {
                    isTreatmentGiven=true;
                }
                statusSyncDAO.updateReconciliationTransactionCronFlag(manualReconVO.getTrackingId(),"capturefailed",conn);
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(), "giveCaptureFailedTreatment()", null, "Common", "SqlException while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(), "giveCaptureFailedTreatment()", null, "Common", "SystemError while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return isTreatmentGiven;
    }

    public boolean giveReversedTreatment(ManualReconVO manualReconVO)throws PZDBViolationException
    {
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        Connection conn = null;
        boolean isTreatmentGiven=false;
        try
        {
            conn=Database.getConnection();
            PreparedStatement pstmt=null;
            StringBuffer query=new StringBuffer("update transaction_common set status='reversed',refundinfo='Partner treatment by reconciliation',refundamount=?,paymentid=? where trackingid=?");
            pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1,manualReconVO.getRefundAmount());
            pstmt.setString(2,manualReconVO.getPaymentOrderNumber());
            pstmt.setString(3, manualReconVO.getTrackingId());
            int i= pstmt.executeUpdate();

            if(i==1)
            {
                query=new StringBuffer("INSERT INTO transaction_common_details(trackingid,amount,ACTION,STATUS,responsetime,responsetransactionid,actionexecutorid,actionexecutorname) VALUES (?,?,?,?,?,?,?,?)");
                pstmt=conn.prepareStatement(query.toString());
                pstmt.setString(1,manualReconVO.getTrackingId());
                pstmt.setString(2,manualReconVO.getRefundAmount());
                pstmt.setString(3,"Reversal Successful");
                pstmt.setString(4,"reversed");
                pstmt.setString(5,manualReconVO.getTime());
                pstmt.setString(6,manualReconVO.getDbPaymentNumber());
                pstmt.setString(7,manualReconVO.getActionExecutorId());
                pstmt.setString(8,manualReconVO.getActionExecutorName());
                int j= pstmt.executeUpdate();
                if(j==1)
                {
                    isTreatmentGiven=true;
                }
                statusSyncDAO.updateReconciliationTransactionCronFlag(manualReconVO.getTrackingId(),"reversed",conn);
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(), "giveRefundTreatment()", null, "Common", "SqlException while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(), "giveRefundTreatment()", null, "Common", "SystemError while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return isTreatmentGiven;
    }
    public boolean giveChargebackReversedTreatment(ManualReconVO manualReconVO)throws PZDBViolationException
    {
        Connection conn = null;
        boolean isTreatmentGiven=false;
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        try
        {
            conn=Database.getConnection();
            PreparedStatement pstmt=null;
            StringBuffer query=new StringBuffer("update transaction_common set status='chargebackreversed', chargebackamount=?, paymentid=?, remark='Chargeback Reversed' where trackingid=? ");
            pstmt=conn.prepareStatement(query.toString());
            pstmt.setDouble(1,manualReconVO.getChargebackAmount());
            pstmt.setString(2,manualReconVO.getPaymentOrderNumber());
            pstmt.setString(3,manualReconVO.getTrackingId());
            int i= pstmt.executeUpdate();

            if(i==1)
            {
                query=new StringBuffer("INSERT INTO transaction_common_details(trackingid,amount,ACTION,STATUS,responsetime,responsetransactionid,responsedescription,actionexecutorid,actionexecutorname,remark) VALUES (?,?,?,?,?,?,?,?,?,?)");
                pstmt=conn.prepareStatement(query.toString());
                pstmt.setString(1,manualReconVO.getTrackingId());
                pstmt.setString(2,manualReconVO.getAmount());
                pstmt.setString(3,"Chargeback Reversed");
                pstmt.setString(4,"chargebackreversed");
                pstmt.setString(5,manualReconVO.getTime());
                pstmt.setString(6,manualReconVO.getDbPaymentNumber());
                pstmt.setString(7,"Reversal of Chargeback");
                pstmt.setString(8,manualReconVO.getActionExecutorId());
                pstmt.setString(9,manualReconVO.getActionExecutorName());
                pstmt.setString(10,manualReconVO.getRemark());
                int j= pstmt.executeUpdate();
                if(j==1)
                {
                    isTreatmentGiven=true;
                }
                statusSyncDAO.updateReconciliationTransactionCronFlag(manualReconVO.getTrackingId(),"chargebackreversed",conn);
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(), "giveRefundTreatment()", null, "Common", "SqlException while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(), "giveRefundTreatment()", null, "Common", "SystemError while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return isTreatmentGiven;
    }
    public boolean getCaseFilingRecord(String trackingId,String status)throws PZDBViolationException
    {
        Connection conn = null;
        boolean isRecordFound=false;
        try
        {
            conn=Database.getConnection();
            PreparedStatement pstmt=null;
            StringBuffer query=new StringBuffer("select * from transaction_common_details where trackingid=? AND status=?");
            pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1,trackingId);
            pstmt.setString(2,status);
            ResultSet rs=pstmt.executeQuery();
            if(rs.next())
            {
                isRecordFound=true;
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(), "giveRefundTreatment()", null, "Common", "SqlException while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(ReconDao.class.getName(), "giveRefundTreatment()", null, "Common", "SystemError while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return isRecordFound;
    }
}