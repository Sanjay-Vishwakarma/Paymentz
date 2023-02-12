package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.manager.utils.AccountUtil;
import com.manager.vo.TerminalVO;
import com.manager.vo.TransactionDetailsVO;
import com.manager.vo.TransactionSummaryVO;
import com.manager.vo.TransactionVO;
import com.manager.vo.merchantmonitoring.*;
import com.manager.vo.merchantmonitoring.enums.MonitoringFrequency;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: sandip1
 * Date: 3/9/16
 * Time: 10:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantMonitoringDAO
{
    Logger logger = new Logger(MerchantMonitoringDAO.class.getName());
    public String getMerchantFirstSubmission(TerminalVO terminalVO)throws PZDBViolationException
    {
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(terminalVO.getAccountId());
        String strQuery = null;
        String memberFirstTransactionDate = null;
        ResultSet rsPayout = null;
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT FROM_UNIXTIME(dtstamp) AS 'firsttransdate' FROM " + tableName + " WHERE  trackingid=(select MIN(trackingid) from " +tableName+ " where toid=? and accountid=? and paymodeid=? and cardtypeid=?)";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getPaymodeId());
            preparedStatement.setString(4, terminalVO.getCardTypeId());
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                memberFirstTransactionDate = rsPayout.getString("firsttransdate");
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::"+se.getMessage());
            PZExceptionHandler.raiseDBViolationException("MerchantMonitoringDAO.java", "getMerchantFirstSubmission()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::"+e.getMessage());
            PZExceptionHandler.raiseDBViolationException("MerchantMonitoringDAO.java", "getMerchantFirstSubmission()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return memberFirstTransactionDate;
    }

    public String getMerchantLastSubmission(TerminalVO terminalVO)throws PZDBViolationException
    {
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(terminalVO.getAccountId());
        String strQuery = null;
        String memberLastTransactionDate = null;
        ResultSet rs = null;
        PreparedStatement preparedStatement=null;
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT FROM_UNIXTIME(dtstamp) AS 'lasttransdate' FROM " + tableName + " WHERE  trackingid=(select MAX(trackingid) from " +tableName+ " where toid=? and accountid=? and paymodeid=? and cardtypeid=?)";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getPaymodeId());
            preparedStatement.setString(4, terminalVO.getCardTypeId());
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                memberLastTransactionDate = rs.getString("lasttransdate");
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::"+se.getMessage());
            PZExceptionHandler.raiseDBViolationException("MerchantMonitoringDAO.java", "getMerchantLastSubmission()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::"+e.getMessage());
            PZExceptionHandler.raiseDBViolationException("MerchantMonitoringDAO.java", "getMerchantLastSubmission()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return memberLastTransactionDate;
    }
    public TransactionSummaryVO getCurrentMonthSales(TerminalVO terminalVO,String currentMonthStartDate,String currentMonthEndDate)throws Exception
    {
        TransactionSummaryVO transactionSummaryVO=new TransactionSummaryVO();
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        double totalProcessingAmount=0.00;
        long totalProcessingCount=0;
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(terminalVO.getAccountId());
        try
        {
            connection = Database.getRDBConnection();
            String query= "select count(*) as 'count', sum(amount) as amount, sum(captureamount) as captureamount, sum(refundamount) as refundamount, sum(chargebackamount) as chargebackamount,status FROM " +tableName+ " where toid=? AND accountid=? AND paymodeid=? AND cardtypeid=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? group by STATUS";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getAccountId());
            pstmt.setString(3,terminalVO.getPaymodeId());
            pstmt.setString(4,terminalVO.getCardTypeId());
            pstmt.setString(5,currentMonthStartDate);
            pstmt.setString(6,currentMonthEndDate);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                if ("authfailed".equals(rs.getString("status")))
                {
                    transactionSummaryVO.setCountOfAuthfailed(rs.getInt("count"));
                    transactionSummaryVO.setAuthfailedAmount(rs.getDouble("amount"));
                }
                else if ("capturesuccess".equals(rs.getString("status")))
                {
                    totalProcessingCount=totalProcessingCount+rs.getLong("count");
                    totalProcessingAmount=totalProcessingAmount+rs.getDouble("captureamount");

                    transactionSummaryVO.setCaptureSuccessCount(rs.getLong("count"));
                    transactionSummaryVO.setCaptureSuccessAmount(rs.getDouble("captureamount"));
                }
                else if("settled".equals(rs.getString("status")))
                {
                    totalProcessingCount=totalProcessingCount+rs.getLong("count");
                    totalProcessingAmount=totalProcessingAmount+rs.getDouble("captureamount");

                    transactionSummaryVO.setCountOfSettled(rs.getInt("count"));
                    transactionSummaryVO.setSettledAmount(rs.getDouble("captureamount"));
                }
                else if("chargeback".equals(rs.getString("status")))
                {
                    totalProcessingCount=totalProcessingCount+rs.getLong("count");
                    totalProcessingAmount=totalProcessingAmount+rs.getDouble("captureamount");

                    transactionSummaryVO.setCountOfChargeback(rs.getInt("count"));
                    transactionSummaryVO.setChargebackAmount(rs.getDouble("chargebackamount"));
                }
                else if("markedforreversal".equals(rs.getString("status")))
                {
                    totalProcessingCount=totalProcessingCount+rs.getLong("count");
                    totalProcessingAmount=totalProcessingAmount+rs.getDouble("captureamount");

                    transactionSummaryVO.setMarkForReversalCount(rs.getLong("count"));
                    transactionSummaryVO.setMarkForReversalAmount(rs.getDouble("captureamount"));
                }
                else if("reversed".equals(rs.getString("status")))
                {
                    totalProcessingCount=totalProcessingCount+rs.getLong("count");
                    totalProcessingAmount=totalProcessingAmount+rs.getDouble("captureamount");

                    transactionSummaryVO.setCountOfReversed(rs.getInt("count"));
                    transactionSummaryVO.setReversedAmount(rs.getDouble("refundamount"));
                }
                else if("authsuccessful".equals(rs.getString("status")))
                {
                    totalProcessingCount=totalProcessingCount+rs.getLong("count");
                    totalProcessingAmount=totalProcessingAmount+rs.getDouble("amount");

                    transactionSummaryVO.setAuthSuccessCount(rs.getLong("count"));
                    transactionSummaryVO.setAuthSuccessAmount(rs.getDouble("amount"));
                }
            }
            transactionSummaryVO.setTotalProcessingCount(totalProcessingCount);
            transactionSummaryVO.setTotalProcessingAmount(totalProcessingAmount);
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::"+se.getMessage());
            PZExceptionHandler.raiseDBViolationException("MerchantMonitoringDAO.java", "getCurrentMonthSales()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::"+e.getMessage());
            PZExceptionHandler.raiseDBViolationException("MerchantMonitoringDAO.java", "getCurrentMonthSales()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionSummaryVO;
    }

    public TransactionSummaryVO getTerminalSalesDetails(TerminalVO terminalVO,String currentDayStartDate, String currentDayEndDate) throws Exception
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double totalProcessingAmount=0.00;
        long totalProcessingCount=0;
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(terminalVO.getAccountId());
        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            //String query = "select count(*) as 'count',sum(amount) as 'amount' from " + tableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and status in('authsuccessful','capturesuccess','reversed','chargeback','settled','markedforreversal') and FROM_UNIXTIME(dtstamp) >=? and FROM_UNIXTIME(dtstamp) <=? ";
            String query = "SELECT STATUS, COUNT(*) AS 'count', SUM(amount)AS amount,sum(captureamount) as captureamount,SUM(refundamount)AS refundamount, SUM(chargebackamount)AS chargebackamount FROM " + tableName + " WHERE toid=? AND accountid=? AND paymodeid=? AND cardtypeid=? AND STATUS IN ('capturesuccess','settled','authfailed','chargeback','markedforreversal','reversed') AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? GROUP BY STATUS";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, terminalVO.getMemberId());
            pstmt.setString(2, terminalVO.getAccountId());
            pstmt.setString(3, terminalVO.getPaymodeId());
            pstmt.setString(4, terminalVO.getCardTypeId());
            pstmt.setString(5, currentDayStartDate);
            pstmt.setString(6, currentDayEndDate);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                if ("authfailed".equals(rs.getString("status")))
                {
                    transactionSummaryVO.setCountOfAuthfailed(rs.getInt("count"));
                    transactionSummaryVO.setAuthfailedAmount(rs.getDouble("amount"));
                }
                else if ("capturesuccess".equals(rs.getString("status")))
                {
                    totalProcessingCount=totalProcessingCount+rs.getLong("count");
                    totalProcessingAmount=totalProcessingAmount+rs.getDouble("captureamount");

                    transactionSummaryVO.setCaptureSuccessCount(rs.getLong("count"));
                    transactionSummaryVO.setCaptureSuccessAmount(rs.getDouble("captureamount"));
                }
                else if("settled".equals(rs.getString("status")))
                {
                    totalProcessingCount=totalProcessingCount+rs.getLong("count");
                    totalProcessingAmount=totalProcessingAmount+rs.getDouble("captureamount");

                    transactionSummaryVO.setCountOfSettled(rs.getInt("count"));
                    transactionSummaryVO.setSettledAmount(rs.getDouble("captureamount"));
                }
                else if("chargeback".equals(rs.getString("status")))
                {
                    totalProcessingCount=totalProcessingCount+rs.getLong("count");
                    totalProcessingAmount=totalProcessingAmount+rs.getDouble("chargebackamount");

                    transactionSummaryVO.setCountOfChargeback(rs.getInt("count"));
                    transactionSummaryVO.setChargebackAmount(rs.getDouble("chargebackamount"));
                }
                else if("markedforreversal".equals(rs.getString("status")))
                {
                    totalProcessingCount=totalProcessingCount+rs.getLong("count");
                    totalProcessingAmount=totalProcessingAmount+rs.getDouble("amount");

                    transactionSummaryVO.setMarkForReversalCount(rs.getLong("count"));
                    transactionSummaryVO.setMarkForReversalAmount(rs.getDouble("amount"));
                }
                else if("reversed".equals(rs.getString("status")))
                {
                    totalProcessingCount=totalProcessingCount+rs.getLong("count");
                    totalProcessingAmount=totalProcessingAmount+rs.getDouble("captureamount");

                    transactionSummaryVO.setCountOfReversed(rs.getInt("count"));
                    transactionSummaryVO.setReversedAmount(rs.getDouble("captureamount"));
                }
                else if("authsuccessful".equals(rs.getString("status")))
                {
                    totalProcessingCount=totalProcessingCount+rs.getLong("count");
                    totalProcessingAmount=totalProcessingAmount+rs.getDouble("amount");

                    transactionSummaryVO.setAuthSuccessCount(rs.getLong("count"));
                    transactionSummaryVO.setAuthSuccessAmount(rs.getDouble("amount"));
                }
            }
            transactionSummaryVO.setTotalProcessingCount(totalProcessingCount);
            transactionSummaryVO.setTotalProcessingAmount(totalProcessingAmount);
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("MerchantMonitoringDAO.java", "getCurrentDaysSales()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("MerchantMonitoringDAO.java", "getCurrentDaysSales()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionSummaryVO;
    }

    public TransactionSummaryVO getPreAuthDetails(TerminalVO terminalVO, String currentMonthStartDate, String currentMonthEndDate) throws Exception
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(terminalVO.getAccountId());

        try
        {
            connection = Database.getRDBConnection();
            String query = "select count(*) as 'count',sum(amount) as 'amount' from " + tableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and status='authsuccessful' and FROM_UNIXTIME(dtstamp) >=? and FROM_UNIXTIME(dtstamp) <=? ";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, terminalVO.getMemberId());
            pstmt.setString(2, terminalVO.getAccountId());
            pstmt.setString(3, terminalVO.getPaymodeId());
            pstmt.setString(4, terminalVO.getCardTypeId());
            pstmt.setString(5, currentMonthStartDate);
            pstmt.setString(6, currentMonthEndDate);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                transactionSummaryVO.setAuthSuccessCount(rs.getInt("count"));
                transactionSummaryVO.setAuthSuccessAmount(rs.getDouble("amount"));
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getPreAuthDetails()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getPreAuthDetails()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionSummaryVO;
    }

    public TransactionSummaryVO getCurrentMonthDeclinedDetails(TerminalVO terminalVO,String currentMonthStartDate,String currentMonthEndDate)throws Exception
    {
        TransactionSummaryVO transactionSummaryVO=new TransactionSummaryVO();
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(terminalVO.getAccountId());
        try
        {
            connection = Database.getRDBConnection();
            String query= "select count(*) as 'count',sum(amount) as 'amount' from " + tableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and status='authfailed' and FROM_UNIXTIME(dtstamp) >=? and FROM_UNIXTIME(dtstamp) <=? ";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getAccountId());
            pstmt.setString(3,terminalVO.getPaymodeId());
            pstmt.setString(4,terminalVO.getCardTypeId());
            pstmt.setString(5,currentMonthStartDate);
            pstmt.setString(6,currentMonthEndDate);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                transactionSummaryVO.setCountOfAuthfailed(rs.getInt("count"));
                transactionSummaryVO.setAuthfailedAmount(rs.getDouble("amount"));
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::",e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getCurrentMonthDeclinedDetails()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ",se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(),"getCurrentMonthDeclinedDetails()",null,"Common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionSummaryVO;
    }

    public TransactionSummaryVO getCurrentDayDeclinedDetails(TerminalVO terminalVO, String currentDayStartDate, String currentDayEndDate) throws Exception
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(terminalVO.getAccountId());

        try
        {
            connection = Database.getRDBConnection();
            String query = "select count(*) as 'count',sum(amount) as 'amount' from " + tableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and status='authfailed' and FROM_UNIXTIME(dtstamp) >=? and FROM_UNIXTIME(dtstamp) <=? ";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, terminalVO.getMemberId());
            pstmt.setString(2, terminalVO.getAccountId());
            pstmt.setString(3, terminalVO.getPaymodeId());
            pstmt.setString(4, terminalVO.getCardTypeId());
            pstmt.setString(5, currentDayStartDate);
            pstmt.setString(6, currentDayEndDate);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                transactionSummaryVO.setCountOfAuthfailed(rs.getInt("count"));
                transactionSummaryVO.setAuthfailedAmount(rs.getDouble("amount"));
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getCurrentMonthDeclinedDetails()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getCurrentMonthDeclinedDetails()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionSummaryVO;
    }
    public TransactionSummaryVO getCurrentMonthChargebackDetails(TerminalVO terminalVO,String currentMonthStartDate,String currentMonthEndDate)throws Exception
    {
        TransactionSummaryVO transactionSummaryVO=new TransactionSummaryVO();

        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(terminalVO.getAccountId());

        try
        {
            connection = Database.getRDBConnection();
            String query= "select count(*) as 'count',sum(chargebackamount) as 'chargebackamount' from " + tableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and TIMESTAMP >=? and TIMESTAMP <=? and status ='chargeback'";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getAccountId());
            pstmt.setString(3,terminalVO.getPaymodeId());
            pstmt.setString(4,terminalVO.getCardTypeId());
            pstmt.setString(5,currentMonthStartDate);
            pstmt.setString(6,currentMonthEndDate);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                transactionSummaryVO.setCountOfChargeback(rs.getInt("count"));
                transactionSummaryVO.setChargebackAmount(rs.getDouble("chargebackamount"));
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::",e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getCurrentMonthChargebackDetails()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ",se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(),"getCurrentMonthChargebackDetails()",null,"Common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionSummaryVO;
    }

    public TransactionSummaryVO getCurrentDayChargebackDetails(TerminalVO terminalVO, String currentDayStartDate, String currentDayEndDate) throws Exception
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(terminalVO.getAccountId());
        try
        {
            connection = Database.getRDBConnection();
            String query = "select count(*) as 'count',sum(chargebackamount) as 'chargebackamount' from " + tableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and TIMESTAMP >=? and TIMESTAMP <=? and status ='chargeback'";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, terminalVO.getMemberId());
            pstmt.setString(2, terminalVO.getAccountId());
            pstmt.setString(3, terminalVO.getPaymodeId());
            pstmt.setString(4, terminalVO.getCardTypeId());
            pstmt.setString(5, currentDayStartDate);
            pstmt.setString(6, currentDayEndDate);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                transactionSummaryVO.setCountOfChargeback(rs.getInt("count"));
                transactionSummaryVO.setChargebackAmount(rs.getDouble("chargebackamount"));
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getCurrentMonthChargebackDetails()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getCurrentMonthChargebackDetails()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionSummaryVO;
    }
    public TransactionSummaryVO getCBDetailsByDtstamp(TerminalVO terminalVO, String currentDayStartDate, String currentDayEndDate) throws Exception
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(terminalVO.getAccountId());
        try
        {
            connection = Database.getRDBConnection();
            String query = "select count(*) as 'count',sum(chargebackamount) as 'chargebackamount' from " + tableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and FROM_UNIXTIME(dtstamp) >=? and FROM_UNIXTIME(dtstamp) <=? and status ='chargeback'";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, terminalVO.getMemberId());
            pstmt.setString(2, terminalVO.getAccountId());
            pstmt.setString(3, terminalVO.getPaymodeId());
            pstmt.setString(4, terminalVO.getCardTypeId());
            pstmt.setString(5, currentDayStartDate);
            pstmt.setString(6, currentDayEndDate);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                transactionSummaryVO.setCountOfChargeback(rs.getInt("count"));
                transactionSummaryVO.setChargebackAmount(rs.getDouble("chargebackamount"));
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getCurrentMonthChargebackDetails()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getCurrentMonthChargebackDetails()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionSummaryVO;
    }
    public TransactionSummaryVO getCurrentMonthRefundDetails(TerminalVO terminalVO,String currentMonthStartDate,String currentMonthEndDate)throws Exception
    {
        TransactionSummaryVO transactionSummaryVO=new TransactionSummaryVO();
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(terminalVO.getAccountId());
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;

        try
        {
            connection = Database.getRDBConnection();
            String query= "select count(*) as 'count',sum(refundamount) as 'refundamount' from " + tableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and TIMESTAMP >=? and TIMESTAMP <=?  and status='reversed' ";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getAccountId());
            pstmt.setString(3,terminalVO.getPaymodeId());
            pstmt.setString(4,terminalVO.getCardTypeId());
            pstmt.setString(5,currentMonthStartDate);
            pstmt.setString(6,currentMonthEndDate);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                transactionSummaryVO.setCountOfReversed(rs.getInt("count"));
                transactionSummaryVO.setReversedAmount(rs.getDouble("refundamount"));
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::",e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getCurrentMonthRefundDetails()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ",se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(),"getCurrentMonthRefundDetails()",null,"Common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionSummaryVO;
    }
    public TransactionSummaryVO getRFDetailsByDtstamp(TerminalVO terminalVO,String currentMonthStartDate,String currentMonthEndDate)throws Exception
    {
        TransactionSummaryVO transactionSummaryVO=new TransactionSummaryVO();
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(terminalVO.getAccountId());
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;

        try
        {
            connection = Database.getRDBConnection();
            String query= "select count(*) as 'count',sum(refundamount) as 'refundamount' from " + tableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and FROM_UNIXTIME(dtstamp) >=? and FROM_UNIXTIME(dtstamp) <=?  and status='reversed' ";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getAccountId());
            pstmt.setString(3,terminalVO.getPaymodeId());
            pstmt.setString(4,terminalVO.getCardTypeId());
            pstmt.setString(5,currentMonthStartDate);
            pstmt.setString(6,currentMonthEndDate);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                transactionSummaryVO.setCountOfReversed(rs.getInt("count"));
                transactionSummaryVO.setReversedAmount(rs.getDouble("refundamount"));
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::",e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getRFDetailsByDtstamp()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ",se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(),"getRFDetailsByDtstamp()",null,"Common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionSummaryVO;
    }
    public TransactionSummaryVO getCurrentDayRefundDetails(TerminalVO terminalVO, String currentDayStartDate, String currentDayEndDate) throws Exception
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(terminalVO.getAccountId());

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try
        {
            connection = Database.getRDBConnection();
            String query = "select count(*) as 'count',sum(refundamount) as 'refundamount' from " + tableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and TIMESTAMP >=? and TIMESTAMP <=?  and status='reversed' ";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, terminalVO.getMemberId());
            pstmt.setString(2, terminalVO.getAccountId());
            pstmt.setString(3, terminalVO.getPaymodeId());
            pstmt.setString(4, terminalVO.getCardTypeId());
            pstmt.setString(5, currentDayStartDate);
            pstmt.setString(6, currentDayEndDate);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                transactionSummaryVO.setCountOfReversed(rs.getInt("count"));
                transactionSummaryVO.setReversedAmount(rs.getDouble("refundamount"));
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getCurrentMonthRefundDetails()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getCurrentMonthRefundDetails()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionSummaryVO;
    }
    public TransactionSummaryVO getFirstMonthSalesDetails(TerminalVO terminalVO,String terminalActivationDate)throws Exception
    {
        TransactionSummaryVO transactionSummaryVO=new TransactionSummaryVO();
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(terminalVO.getAccountId());

        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;

        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String todayDate=targetFormat.format(new Date());

        Calendar cal = Calendar.getInstance();
        cal.setTime(targetFormat.parse(terminalActivationDate));
        cal.add(Calendar.DAY_OF_MONTH,30);
        String firstMonthEndDate = targetFormat.format(cal.getTime());
        try
        {
            connection = Database.getConnection();
            String query= "";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getAccountId());
            pstmt.setString(3,terminalVO.getPaymodeId());
            pstmt.setString(4,terminalVO.getCardTypeId());
            pstmt.setString(5,firstMonthEndDate);
            pstmt.setString(6,todayDate);
            rs=pstmt.executeQuery();
            if(rs.next())
            {

            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::",e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getFirstMonthSalesDetails()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ",se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(),"getFirstMonthSalesDetails()",null,"Common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionSummaryVO;
    }
    public TransactionSummaryVO getTotalSalesAmount(TerminalVO terminalVO)throws PZDBViolationException
    {
        TransactionSummaryVO transactionSummaryVO=new TransactionSummaryVO();
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(terminalVO.getAccountId());
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;

        try
        {
            connection = Database.getRDBConnection();
            String query= "select count(*) as 'count',sum(amount) as 'amount' from "+tableName+" where toid=? and accountid=? and paymodeid=? and cardtypeid=? and status in('authsuccessful','capturesuccess','reversed','chargeback','settled') ";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getAccountId());
            pstmt.setString(3,terminalVO.getPaymodeId());
            pstmt.setString(4,terminalVO.getCardTypeId());
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                transactionSummaryVO.setTotalProcessingCount(rs.getInt("count"));
                transactionSummaryVO.setTotalProcessingAmount(rs.getDouble("amount"));
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::",e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTotalSalesAmount()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ",se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTotalSalesAmount()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionSummaryVO;
    }

    public List<MerchantTerminalThresholdVO> getMerchantTerminalThresholdDetails(String memberId) throws PZDBViolationException
    {
        List<MerchantTerminalThresholdVO> merchantTerminalThresholdVOs = new ArrayList();
        MerchantTerminalThresholdVO merchantTerminalThresholdVO = null;
        TerminalLimitsVO terminalLimitsVO = null;
        TerminalVO terminalVO = null;
        TerminalThresholdsVO terminalThresholdsVO = null;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT mtt.*,mam.cardtypeid,mam.accountid,mam.paymodeid FROM member_terminal_threshold  AS mtt JOIN member_account_mapping AS mam ON mtt.terminalid=mam.terminalid AND mtt.memberid=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                merchantTerminalThresholdVO = new MerchantTerminalThresholdVO();
                terminalVO = new TerminalVO();
                terminalLimitsVO = new TerminalLimitsVO();
                terminalThresholdsVO = new TerminalThresholdsVO();

                terminalVO.setTerminalId(rs.getString("terminalid"));
                terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                terminalVO.setAccountId(rs.getString("accountid"));
                terminalVO.setPaymodeId(rs.getString("paymodeid"));
                terminalVO.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVO.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVO.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));

                terminalThresholdsVO.setDailyApprovalRatio(rs.getDouble("daily_approval_ratio"));
                terminalThresholdsVO.setWeeklyApprovalRatio(rs.getDouble("weekly_approval_ratio"));
                terminalThresholdsVO.setMonthlyApprovalRatio(rs.getDouble("monthly_approval_ratio"));

                terminalThresholdsVO.setDailyCBRatio(rs.getDouble("daily_cb_ratio"));
                terminalThresholdsVO.setWeeklyCBRatio(rs.getDouble("weekly_cb_ratio"));
                terminalThresholdsVO.setMonthlyCBRatio(rs.getDouble("monthly_cb_ratio"));

                terminalThresholdsVO.setDailyRFRatio(rs.getDouble("daily_rf_ratio"));
                terminalThresholdsVO.setWeeklyRFRatio(rs.getDouble("weekly_rf_ratio"));
                terminalThresholdsVO.setMonthlyRFRatio(rs.getDouble("monthly_rf_ratio"));

                terminalThresholdsVO.setInactivePeriodThreshold(rs.getInt("inactive_period_threshold"));
                terminalThresholdsVO.setFirstSubmissionThreshold(rs.getInt("first_submission_threshold"));

                terminalThresholdsVO.setMonthlyCBRatioAmount(rs.getDouble("monthly_cb_amount_ratio"));
                terminalThresholdsVO.setDailyCBRatioAmount(rs.getDouble("daily_cb_amount_ratio"));
                terminalThresholdsVO.setWeeklyCBRatioAmount(rs.getDouble("weekly_cb_amount_ratio"));

                terminalThresholdsVO.setSuspendCBCountThreshold(rs.getInt("suspend_cbcount_threshold"));
                terminalThresholdsVO.setAlertCBCountThreshold(rs.getInt("alert_cbcount_threshold"));
                terminalThresholdsVO.setPriorMonthSalesVsCurrentMonthRefund(rs.getDouble("priormonth_rf_vs_currentmonth_sales_threshold"));
                terminalThresholdsVO.setManualCaptureAlertThreshold(rs.getInt("manualcapture_alert_threshold"));

                terminalThresholdsVO.setDailyAvgTicketThreshold(rs.getDouble("daily_avgticket_threshold"));
                terminalThresholdsVO.setWeeklyAvgTicketThreshold(rs.getDouble("weekly_avgticket_threshold"));
                terminalThresholdsVO.setMonthlyAvgTicketThreshold(rs.getDouble("monthly_avgticket_threshold"));
                terminalThresholdsVO.setDailyVsQuarterlyAvgTicketThreshold(rs.getDouble("daily_vs_quarterly_avgticket_threshold"));

                terminalThresholdsVO.setDailyRFAmountRatio(rs.getDouble("daily_rf_amount_ratio"));
                terminalThresholdsVO.setWeeklyRFAmountRatio(rs.getDouble("weekly_rf_amount_ratio"));
                terminalThresholdsVO.setMonthlyRFAmountRatio(rs.getDouble("monthy_rf_amount_ratio"));

                terminalThresholdsVO.setSameCardSameAmountThreshold(rs.getInt("samecard_sameamount_threshold"));
                terminalThresholdsVO.setResumeProcessingAlert(rs.getInt("resume_processing_alert"));
                terminalThresholdsVO.setDailyAvgTicketPercentageThreshold(rs.getDouble("daily_avgticket_percentage_threshold"));
                terminalThresholdsVO.setDailyCBRatioSuspension(rs.getDouble("daily_cb_ratio_suspension"));
                terminalThresholdsVO.setDailyCBAmountRatioSuspension(rs.getDouble("daily_cb_amount_ratio_suspension"));
                terminalThresholdsVO.setSameCardSameAmountConsequenceThreshold(rs.getInt("samecard_sameamount_consequence_threshold"));
                terminalThresholdsVO.setSameCardConsequentlyThreshold(rs.getInt("samecard_consequently_threshold"));

                merchantTerminalThresholdVO.setTerminalLimitsVO(terminalLimitsVO);
                merchantTerminalThresholdVO.setTerminalVO(terminalVO);
                merchantTerminalThresholdVO.setTerminalThresholdsVO(terminalThresholdsVO);
                merchantTerminalThresholdVO.setMemberId(memberId);

                merchantTerminalThresholdVOs.add(merchantTerminalThresholdVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTotalSalesAmount()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTotalSalesAmount()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return merchantTerminalThresholdVOs;
    }

    public MerchantTerminalThresholdVO getTerminalThresholdDetails(TerminalVO requestTerminalVO) throws PZDBViolationException
    {
        MerchantTerminalThresholdVO terminalThresholdVO = new MerchantTerminalThresholdVO();
        TerminalLimitsVO terminalLimitsVO = null;
        TerminalVO terminalVO = null;
        TerminalThresholdsVO terminalThresholdsVO = null;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try
        {
            connection = Database.getRDBConnection();
            String query = "select mth.*,mam.from_unixtime(dtstamp) as activationtime,mam.cardtypeid,mam.paymodeid,mam.accountid,mam.monthly_amount_limit,mam.daily_amount_limit,mam.weekly_amount_limit from member_terminal_threshold as mth join member_account_mapping as mam on mth.terminalid=mam.terminalid and mth.terminalid=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, requestTerminalVO.getTerminalId());
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                terminalVO = new TerminalVO();
                terminalLimitsVO = new TerminalLimitsVO();
                terminalThresholdsVO = new TerminalThresholdsVO();

                terminalVO.setTerminalId(rs.getString("terminalid"));
                terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                terminalVO.setAccountId(rs.getString("accountid"));
                terminalVO.setPaymodeId(rs.getString("paymodeid"));
                terminalVO.setActivationDate(rs.getString("activationtime"));

                terminalLimitsVO.setMonthlyAmountLimit(rs.getDouble("monthly_amount_limit"));
                terminalLimitsVO.setDailyAmountLimit(rs.getDouble("daily_amount_limit"));
                terminalLimitsVO.setWeeklyAmountLimit(rs.getDouble("weekly_amount_limit"));

                terminalThresholdsVO.setDailyApprovalRatio(rs.getDouble("daily_approval_ratio"));
                terminalThresholdsVO.setWeeklyApprovalRatio(rs.getDouble("weekly_approval_ratio"));
                terminalThresholdsVO.setMonthlyApprovalRatio(rs.getDouble("monthly_approval_ratio"));

                terminalThresholdsVO.setDailyCBRatio(rs.getDouble("daily_cb_ratio"));
                terminalThresholdsVO.setWeeklyCBRatio(rs.getDouble("weekly_cb_ratio"));
                terminalThresholdsVO.setMonthlyCBRatio(rs.getDouble("monthly_cb_ratio"));

                terminalThresholdsVO.setDailyRFRatio(rs.getDouble("daily_rf_ratio"));
                terminalThresholdsVO.setWeeklyRFRatio(rs.getDouble("weekly_rf_ratio"));
                terminalThresholdsVO.setMonthlyRFRatio(rs.getDouble("monthly_rf_ratio"));

                terminalThresholdsVO.setInactivePeriodThreshold(rs.getInt("inactive_period_threshold"));
                terminalThresholdsVO.setFirstSubmissionThreshold(rs.getInt("first_submission_threshold"));

                terminalThresholdsVO.setMonthlyCBRatioAmount(rs.getDouble("monthly_cb_amount_ratio"));
                terminalThresholdsVO.setDailyCBRatioAmount(rs.getDouble("daily_cb_amount_ratio"));
                terminalThresholdsVO.setWeeklyCBRatioAmount(rs.getDouble("weekly_cb_amount_ratio"));

                terminalThresholdsVO.setSuspendCBCountThreshold(rs.getInt("suspend_cbcount_threshold"));
                terminalThresholdsVO.setAlertCBCountThreshold(rs.getInt("alert_cbcount_threshold"));
                terminalThresholdsVO.setPriorMonthSalesVsCurrentMonthRefund(rs.getDouble("priormonth_rf_vs_currentmonth_sales_threshold"));
                terminalThresholdsVO.setManualCaptureAlertThreshold(rs.getInt("manualcapture_alert_threshold"));

                terminalThresholdsVO.setDailyAvgTicketThreshold(rs.getDouble("daily_avgticket_threshold"));
                terminalThresholdsVO.setWeeklyAvgTicketThreshold(rs.getDouble("weekly_avgticket_threshold"));
                terminalThresholdsVO.setMonthlyAvgTicketThreshold(rs.getDouble("monthly_avgticket_threshold"));
                terminalThresholdsVO.setDailyVsQuarterlyAvgTicketThreshold(rs.getDouble("daily_vs_quarterly_avgticket_threshold"));

                terminalThresholdsVO.setDailyRFAmountRatio(rs.getDouble("daily_rf_amount_ratio"));
                terminalThresholdsVO.setWeeklyRFAmountRatio(rs.getDouble("weekly_rf_amount_ratio"));
                terminalThresholdsVO.setMonthlyRFAmountRatio(rs.getDouble("monthy_rf_amount_ratio"));

                terminalThresholdsVO.setSameCardSameAmountThreshold(rs.getInt("samecard_sameamount_threshold"));
                terminalThresholdsVO.setSameCardSameAmountConsequenceThreshold(rs.getInt("samecard_sameamount_consequence_threshold"));
                terminalThresholdsVO.setResumeProcessingAlert(rs.getInt("resume_processing_alert"));

                terminalThresholdsVO.setDailyAvgTicketPercentageThreshold(rs.getDouble("daily_avgticket_percentage_threshold"));
                terminalThresholdsVO.setDailyCBRatioSuspension(rs.getDouble("daily_cb_ratio_suspension"));
                terminalThresholdsVO.setDailyCBAmountRatioSuspension(rs.getDouble("daily_cb_amount_ratio_suspension"));
                terminalThresholdsVO.setSameCardSameAmountConsequenceThreshold(rs.getInt("samecard_sameamount_consequence_threshold"));
                terminalThresholdsVO.setSameCardConsequentlyThreshold(rs.getInt("samecard_consequently_threshold"));

                terminalThresholdVO.setTerminalLimitsVO(terminalLimitsVO);
                terminalThresholdVO.setTerminalVO(terminalVO);
                terminalThresholdVO.setTerminalThresholdsVO(terminalThresholdsVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTerminalThresholdDetails()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTerminalThresholdDetails()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return terminalThresholdVO;
    }
    public List<TransactionVO> getTransactionListByDtstamp(String tableName,TerminalVO terminalVO, String status)
    {
        List<TransactionVO> transactionVOs = new LinkedList<TransactionVO>();
        Connection con = null;
        PreparedStatement preparedStatement = null;
        TransactionVO detailsVO = null;
        ResultSet rsPayout = null;
        try
        {
            String query = null;
            con = Database.getRDBConnection();
            if ("transaction_ecore".equalsIgnoreCase(tableName))
            {
                query = "SELECT trackingid,description,FROM_UNIXTIME(dtstamp) as transactiondate,amount,captureamount,currency,STATUS,firstname,lastname FROM transaction_ecore where toid=? and accountid=? and paymodeid=? and cardtypeid=?  and status=?";
            }
            else if ("transaction_qwipi".equalsIgnoreCase(tableName))
            {
                query = "SELECT trackingid,description,FROM_UNIXTIME(dtstamp) as transactiondate,amount,captureamount,currency,STATUS,firstname,lastname FROM transaction_qwipi where toid=? and accountid=? and paymodeid=? and cardtypeid=? and status=?";
            }
            else
            {
                query = "SELECT trackingid,description,FROM_UNIXTIME(dtstamp) as transactiondate,amount,captureamount,currency,STATUS,firstname,lastname FROM transaction_common where toid=? and accountid=? and paymodeid=? and cardtypeid=?  and status=?";
            }

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getPaymodeId());
            preparedStatement.setString(4, terminalVO.getCardTypeId());
            preparedStatement.setString(5, status);
            rsPayout = preparedStatement.executeQuery();
            while (rsPayout.next())
            {
                detailsVO = new TransactionVO();
                detailsVO.setTrackingId(rsPayout.getString("trackingid"));
                detailsVO.setTransactionDate(rsPayout.getString("transactiondate"));
                detailsVO.setCustFirstName(rsPayout.getString("firstname"));
                detailsVO.setCustLastName(rsPayout.getString("lastname"));
                detailsVO.setOrderId(rsPayout.getString("description"));
                detailsVO.setStatus(rsPayout.getString("STATUS"));
                detailsVO.setCurrency(rsPayout.getString("currency"));
                detailsVO.setAmount(rsPayout.getString("amount"));

                transactionVOs.add(detailsVO);
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError ::::::: Leaving transactionEntry ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionVOs;

    }
    public List<BinAmountVO> getSameCardSameAmountDetail(TerminalVO terminalVO,DateVO dateVO) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        List<BinAmountVO> binAmountVOList=new ArrayList();
        BinAmountVO binAmountVO=null;
        ResultSet rs = null;
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
        GatewayType gatewayType = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
        String tableName = Database.getTableName(gatewayType.getGateway());
        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT COUNT(*) as transcount,t.amount,bd.first_six,bd.last_four,terminalid FROM bin_details AS bd JOIN "+tableName+" AS t ON bd.icicitransid=t.trackingid AND t.toid=? AND t.terminalid=?  AND FROM_UNIXTIME(t.dtstamp)>=? AND FROM_UNIXTIME(t.dtstamp)<=? GROUP BY t.amount,bd.first_six,bd.last_four";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,dateVO.getStartDate());
            pstmt.setString(4,dateVO.getEndDate());
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                binAmountVO=new BinAmountVO();
                binAmountVO.setCount(rs.getInt("transcount"));
                binAmountVO.setBinAmount(rs.getDouble("amount"));
                binAmountVO.setBinString(rs.getString("first_six"));
                binAmountVO.setLastFourString(rs.getString("last_four"));
                binAmountVOList.add(binAmountVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getSameCardSameAmountDetail()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getSameCardSameAmountDetail()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return binAmountVOList;
    }
    public List<BinAmountVO> getSameCardSameAmountConsequenceDetail(TerminalVO terminalVO,DateVO dateVO) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        List<BinAmountVO> binAmountVOList=new ArrayList();
        BinAmountVO binAmountVO=null;
        ResultSet rs = null;

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
        GatewayType gatewayType = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
        String tableName = Database.getTableName(gatewayType.getGateway());
        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT COUNT(*) as transcount,t.amount,bd.first_six,terminalid FROM bin_details AS bd JOIN "+tableName+" AS t ON bd.icicitransid=t.trackingid AND t.toid=? AND t.terminalid=?  AND FROM_UNIXTIME(t.dtstamp)>=? AND FROM_UNIXTIME(t.dtstamp)<=? GROUP BY t.amount,bd.first_six HAVING transcount>1";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,dateVO.getStartDate());
            pstmt.setString(4,dateVO.getEndDate());
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                binAmountVO=new BinAmountVO();
                binAmountVO.setCount(rs.getInt("transcount"));
                binAmountVO.setBinAmount(rs.getDouble("amount"));
                binAmountVO.setBinString(rs.getString("first_six"));
                binAmountVOList.add(binAmountVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getSameCardSameAmountDetail()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getSameCardSameAmountDetail()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return binAmountVOList;
    }

    public List<BinAmountVO> getSameCardConsequently(TerminalVO terminalVO,DateVO dateVO) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        List<BinAmountVO> binAmountVOList=new ArrayList();
        BinAmountVO binAmountVO=null;
        ResultSet rs = null;

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
        GatewayType gatewayType = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
        String tableName = Database.getTableName(gatewayType.getGateway());
        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT COUNT(*) as transcount,t.amount,bd.first_six,terminalid FROM bin_details AS bd JOIN "+tableName+" AS t ON bd.icicitransid=t.trackingid AND t.toid=? AND t.terminalid=?  AND FROM_UNIXTIME(t.dtstamp)>='1-04-2016 00:00:00' AND FROM_UNIXTIME(t.dtstamp)<='27-04-2016 23:59:59' GROUP BY t.amount,bd.first_six HAVING transcount>1";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                binAmountVO=new BinAmountVO();
                binAmountVO.setCount(rs.getInt("transcount"));
                binAmountVO.setBinAmount(rs.getDouble("amount"));
                binAmountVO.setBinString(rs.getString("first_six"));
                binAmountVOList.add(binAmountVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getSameCardSameAmountDetail()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getSameCardSameAmountDetail()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return binAmountVOList;
    }

    public List<TransactionVO> getCurrentDayChargebackTransactionDetails(TerminalVO terminalVO,DateVO dateVO) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        List<TransactionVO> transactionVOs = new ArrayList();
        TransactionVO transactionVO=null;
        ResultSet rs = null;

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
        GatewayType gatewayType = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
        String tableName = Database.getTableName(gatewayType.getGateway());
        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT trackingid,toid,FROM_UNIXTIME(dtstamp) AS transactiondate FROM "+tableName+" where toid=? and accountid=? and paymodeid=? and cardtypeid=? and status=? and TIMESTAMP>=? and TIMESTAMP <=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getAccountId());
            pstmt.setString(3,terminalVO.getPaymodeId());
            pstmt.setString(4,terminalVO.getCardTypeId());
            pstmt.setString(5,"chargeback");
            pstmt.setString(6,dateVO.getStartDate());
            pstmt.setString(7,dateVO.getEndDate());
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                transactionVO=new TransactionVO();
                transactionVO.setTrackingId(rs.getString("trackingid"));
                transactionVO.setTransactionDate(rs.getString("transactiondate"));
                transactionVO.setToid(rs.getString("toid"));
                transactionVOs.add(transactionVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getCurrentDayChargebackTransactionDetails()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getCurrentDayChargebackTransactionDetails()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionVOs;
    }

    public List<TransactionVO> getTransactionListByTimestamp(TerminalVO terminalVO, DateVO dateVO, String status) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        List<TransactionVO> transactionVOs = new ArrayList();
        TransactionVO transactionVO = null;
        ResultSet rs = null;

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
        GatewayType gatewayType = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
        String tableName = Database.getTableName(gatewayType.getGateway());
        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT trackingid,description,firstname,lastname,toid,FROM_UNIXTIME(dtstamp) AS transactiondate,status,currency,amount FROM " + tableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and status=? and TIMESTAMP>=? and TIMESTAMP <=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, terminalVO.getMemberId());
            pstmt.setString(2, terminalVO.getAccountId());
            pstmt.setString(3, terminalVO.getPaymodeId());
            pstmt.setString(4, terminalVO.getCardTypeId());
            pstmt.setString(5, status);
            pstmt.setString(6, dateVO.getStartDate());
            pstmt.setString(7, dateVO.getEndDate());
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                transactionVO = new TransactionVO();
                transactionVO.setTrackingId(rs.getString("trackingid"));
                transactionVO.setOrderId(rs.getString("description"));
                transactionVO.setCustFirstName(rs.getString("firstname"));
                transactionVO.setCustLastName(rs.getString("lastname"));
                transactionVO.setToid(rs.getString("toid"));
                transactionVO.setTransactionDate(rs.getString("transactiondate"));
                transactionVO.setStatus(rs.getString("status"));
                transactionVO.setCurrency(rs.getString("currency"));
                transactionVO.setAmount(rs.getString("amount"));
                transactionVOs.add(transactionVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTransactionListByTimestamp()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTransactionListByTimestamp()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionVOs;
    }

    public List<TransactionVO> getInCompleteTransactionListByTimestamp(TerminalVO terminalVO, DateVO dateVO) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        List<TransactionVO> transactionVOs = new ArrayList();
        TransactionVO transactionVO = null;
        ResultSet rs = null;

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
        GatewayType gatewayType = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
        String tableName = Database.getTableName(gatewayType.getGateway());
        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT trackingid,description,firstname,lastname,toid,FROM_UNIXTIME(dtstamp) AS transactiondate,status,currency,amount FROM " + tableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and status in ('authstarted','capturestarted','markedforreversal','cancelstarted') and TIMESTAMP>=? and TIMESTAMP <=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, terminalVO.getMemberId());
            pstmt.setString(2, terminalVO.getAccountId());
            pstmt.setString(3, terminalVO.getPaymodeId());
            pstmt.setString(4, terminalVO.getCardTypeId());
            pstmt.setString(5, dateVO.getStartDate());
            pstmt.setString(6, dateVO.getEndDate());
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                transactionVO = new TransactionVO();
                transactionVO.setTrackingId(rs.getString("trackingid"));
                transactionVO.setOrderId(rs.getString("description"));
                transactionVO.setCustFirstName(rs.getString("firstname"));
                transactionVO.setCustLastName(rs.getString("lastname"));
                transactionVO.setToid(rs.getString("toid"));
                transactionVO.setTransactionDate(rs.getString("transactiondate"));
                transactionVO.setStatus(rs.getString("status"));
                transactionVO.setCurrency(rs.getString("currency"));
                transactionVO.setAmount(rs.getString("amount"));
                transactionVOs.add(transactionVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getInCompleteTransactionListByTimestamp()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getInCompleteTransactionListByTimestamp()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionVOs;
    }

    public List<TransactionVO> getTransactionListByDTStamp(TerminalVO terminalVO, DateVO dateVO) throws PZDBViolationException
    {
        Connection connection= null;
        PreparedStatement pstmt = null;
        List<TransactionVO> transactionVOs = new ArrayList();
        TransactionVO transactionVO = null;
        ResultSet rs = null;

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
        GatewayType gatewayType = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
        String tableName = Database.getTableName(gatewayType.getGateway());
        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT trackingid,description,firstname,lastname,toid,FROM_UNIXTIME(dtstamp) AS transactiondate,status,currency,amount FROM " + tableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and FROM_UNIXTIME(dtstamp)>=? and FROM_UNIXTIME(dtstamp) <=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, terminalVO.getMemberId());
            pstmt.setString(2, terminalVO.getAccountId());
            pstmt.setString(3, terminalVO.getPaymodeId());
            pstmt.setString(4, terminalVO.getCardTypeId());
            pstmt.setString(5, dateVO.getStartDate());
            pstmt.setString(6, dateVO.getEndDate());
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                transactionVO = new TransactionVO();
                transactionVO.setTrackingId(rs.getString("trackingid"));
                transactionVO.setOrderId(rs.getString("description"));
                transactionVO.setCustFirstName(rs.getString("firstname"));
                transactionVO.setCustLastName(rs.getString("lastname"));
                transactionVO.setToid(rs.getString("toid"));
                transactionVO.setTransactionDate(rs.getString("transactiondate"));
                transactionVO.setStatus(rs.getString("status"));
                transactionVO.setCurrency(rs.getString("currency"));
                transactionVO.setAmount(rs.getString("amount"));
                transactionVOs.add(transactionVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTransactionListByDTStamp()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTransactionListByDTStamp()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionVOs;
    }

    public Set<String> getAllCardsGroupByBins(TerminalVO terminalVO, DateVO dateVO)
    {
        Connection connection=null;
        PreparedStatement pstmt=null;
        HashSet <String> binAmountVOSet = new HashSet();
        ResultSet rs = null;

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
        GatewayType gatewayType = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
        String tableName = Database.getTableName(gatewayType.getGateway());
        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT first_six,last_four FROM bin_details AS bd JOIN "+tableName+" AS t ON bd.icicitransid=t.trackingid AND t.toid=? AND t.terminalid=? AND FROM_UNIXTIME(t.dtstamp)>=? AND FROM_UNIXTIME(t.dtstamp)<=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,dateVO.getStartDate());
            pstmt.setString(4,dateVO.getEndDate());
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                String str=rs.getString("first_six")+":"+rs.getString("last_four");
                binAmountVOSet.add(str);
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::", e);
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return binAmountVOSet;
    }

    public String addNewMonitoringParameter(MonitoringParameterVO monitoringParameterVO)throws PZDBViolationException
    {
        Connection conn=null;
        PreparedStatement pstmt =null;
        String query="";
        String status="";

        try
        {
            conn = Database.getConnection();
            query = "insert into monitoring_parameter_master(monitoing_para_id,monitoing_para_name,monitoing_para_tech_name,monitoring_unit,monitoing_category,monitoring_deviation,monitoing_keyword,monitoing_subkeyword,monitoing_alert_category,monitoing_onchannel,default_alert_threshold,default_suspension_threshold,default_alert_activation,default_suspension_activation,default_isAlertToAdmin,default_isAlertToMerchant,default_isAlertToPartner,default_isAlertToAgent,default_alertMsg,isalerttoadmin_sales,isalerttoadmin_fraud,isalerttoadmin_rf,isalerttoadmin_cb,isalerttoadmin_tech,isalerttomerchant_sales,isalerttomerchant_fraud,isalerttomerchant_rf,isalerttomerchant_cb,isalerttomerchant_tech,isalerttopartner_sales,isalerttopartner_fraud,isalerttopartner_rf,isalerttopartner_cb,isalerttopartner_tech,isalerttoagent_sales,isalerttoagent_fraud,isalerttoagent_rf,isalerttoagent_cb,isalerttoagent_tech,isdailyexecution,isweeklyexecution,ismonthlyexecution,weekly_alert_threshold,weekly_suspension_threshold,monthly_alert_threshold,monthly_suspension_threshold,displayChartType)values(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,monitoringParameterVO.getMonitoringParameterName());
            pstmt.setString(2,monitoringParameterVO.getMonitoingParaTechName());
            pstmt.setString(3,monitoringParameterVO.getMonitoringUnit());
            pstmt.setString(4,monitoringParameterVO.getMonitoingCategory());
            pstmt.setString(5,monitoringParameterVO.getMonitoingDeviation());
            pstmt.setString(6,monitoringParameterVO.getMonitoingKeyword());
            pstmt.setString(7,monitoringParameterVO.getMonitoingSubKeyword());
            pstmt.setString(8,monitoringParameterVO.getMonitoingAlertCategory());
            pstmt.setString(9,monitoringParameterVO.getMonitoingOnChannel());
            pstmt.setDouble(10,monitoringParameterVO.getDefaultAlertThreshold());
            pstmt.setDouble(11,monitoringParameterVO.getDefaultSuspensionThreshold());
            pstmt.setString(12,monitoringParameterVO.getDefaultAlertActivation());
            pstmt.setString(13,monitoringParameterVO.getDefaultSuspensionActivation());
            pstmt.setString(14,monitoringParameterVO.getDefaultIsAlertToAdmin());
            pstmt.setString(15,monitoringParameterVO.getDefaultIsAlertToMerchant());
            pstmt.setString(16,monitoringParameterVO.getDefaultIsAlertToPartner());
            pstmt.setString(17,monitoringParameterVO.getDefaultIsAlertToAgent());
            pstmt.setString(18,monitoringParameterVO.getDefaultAlertMsg());
            pstmt.setString(19,monitoringParameterVO.getDefaultIsAlertToAdminSales());
            pstmt.setString(20,monitoringParameterVO.getDefaultIsAlertToAdminFraud());
            pstmt.setString(21,monitoringParameterVO.getDefaultIsAlertToAdminRF());
            pstmt.setString(22,monitoringParameterVO.getDefaultIsAlertToAdminCB());
            pstmt.setString(23,monitoringParameterVO.getDefaultIsAlertToAdminTech());
            pstmt.setString(24,monitoringParameterVO.getDefaultIsAlertToMerchantSales());
            pstmt.setString(25,monitoringParameterVO.getDefaultIsAlertToMerchantFraud());
            pstmt.setString(26,monitoringParameterVO.getDefaultIsAlertToMerchantRF());
            pstmt.setString(27,monitoringParameterVO.getDefaultIsAlertToMerchantCB());
            pstmt.setString(28,monitoringParameterVO.getDefaultIsAlertToMerchantTech());
            pstmt.setString(29,monitoringParameterVO.getDefaultIsAlertToPartnerSales());
            pstmt.setString(30,monitoringParameterVO.getDefaultIsAlertToPartnerFraud());
            pstmt.setString(31,monitoringParameterVO.getDefaultIsAlertToPartnerRF());
            pstmt.setString(32,monitoringParameterVO.getDefaultIsAlertToPartnerCB());
            pstmt.setString(33,monitoringParameterVO.getDefaultIsAlertToPartnerTech());
            pstmt.setString(34,monitoringParameterVO.getDefaultIsAlertToAgentSales());
            pstmt.setString(35,monitoringParameterVO.getDefaultIsAlertToAgentFraud());
            pstmt.setString(36,monitoringParameterVO.getDefaultIsAlertToAgentRF());
            pstmt.setString(37,monitoringParameterVO.getDefaultIsAlertToAgentCB());
            pstmt.setString(38,monitoringParameterVO.getDefaultIsAlertToAgentTech());
            pstmt.setString(39,monitoringParameterVO.getIsDailyExecution());
            pstmt.setString(40,monitoringParameterVO.getIsWeeklyExecution());
            pstmt.setString(41,monitoringParameterVO.getIsMonthlyExecution());
            pstmt.setDouble(42,monitoringParameterVO.getWeeklyAlertThreshold());
            pstmt.setDouble(43,monitoringParameterVO.getWeeklySuspensionThreshold());
            pstmt.setDouble(44,monitoringParameterVO.getMonthlyAlertThreshold());
            pstmt.setDouble(45,monitoringParameterVO.getMonthlySuspensionThreshold());
            pstmt.setString(46,monitoringParameterVO.getDisplayChartType());

            int k=pstmt.executeUpdate();
            if (k==1)
            {
                status="success";
            }
        }
        catch (SystemError se)
        {
            status="failed";
            logger.error("SystemError::::::"+se.getMessage());
            PZExceptionHandler.raiseDBViolationException("MerchantMonitoringDAO.java", "addNewMonitoringParameter()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            status="failed";
            logger.error("SQLException::::::"+e.getMessage());
            PZExceptionHandler.raiseDBViolationException("MerchantMonitoringDAO.java", "addNewMonitoringParameter()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public String monitoringParameterMapping(MonitoringParameterMappingVO monitoringParameterMappingVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = "";
        String status = "";

        try
        {
            conn = Database.getConnection();
            query = "insert into member_account_monitoringpara_mapping(mappingid,memberid,terminalid,alert_threshold,suspension_threshold,alert_activation,suspension_activation,isalerttoadmin,isalerttomerchant,isalerttopartner,isalerttoagent,alertMessage,monitoing_para_id,isalerttoadmin_sales,dtstamp,isalerttoadmin_fraud,isalerttoadmin_rf,isalerttoadmin_cb,isalerttoadmin_tech,isalerttomerchant_sales,isalerttomerchant_fraud,isalerttomerchant_rf,isalerttomerchant_cb,isalerttomerchant_tech,isalerttopartner_sales,isalerttopartner_fraud,isalerttopartner_rf,isalerttopartner_cb,isalerttopartner_tech,isalerttoagent_sales,isalerttoagent_fraud,isalerttoagent_rf,isalerttoagent_cb,isalerttoagent_tech,isdailyexecution,isweeklyexecution,ismonthlyexecution,weekly_alert_threshold,weekly_suspension_threshold,monthly_alert_threshold,monthly_suspension_threshold)values(NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1,monitoringParameterMappingVO.getMemberId());
            pstmt.setInt(2,monitoringParameterMappingVO.getTerminalId());
            pstmt.setDouble(3,monitoringParameterMappingVO.getAlertThreshold());
            pstmt.setDouble(4,monitoringParameterMappingVO.getSuspensionThreshold());
            pstmt.setString(5,monitoringParameterMappingVO.getAlertActivation());
            pstmt.setString(6,monitoringParameterMappingVO.getSuspensionActivation());
            pstmt.setString(7,monitoringParameterMappingVO.getIsAlertToAdmin());
            pstmt.setString(8,monitoringParameterMappingVO.getIsAlertToMerchant());
            pstmt.setString(9,monitoringParameterMappingVO.getIsAlertToPartner());
            pstmt.setString(10,monitoringParameterMappingVO.getIsAlertToAgent());
            pstmt.setString(11,monitoringParameterMappingVO.getAlertMessage());
            pstmt.setInt(12,monitoringParameterMappingVO.getMonitoringParameterId());
            pstmt.setString(13,monitoringParameterMappingVO.getIsAlertToAdminSales());
            pstmt.setString(14,monitoringParameterMappingVO.getIsAlertToAdminFraud());
            pstmt.setString(15,monitoringParameterMappingVO.getIsAlertToAdminRF());
            pstmt.setString(16,monitoringParameterMappingVO.getIsAlertToAdminCB());
            pstmt.setString(17,monitoringParameterMappingVO.getIsAlertToAdminTech());
            pstmt.setString(18,monitoringParameterMappingVO.getIsAlertToMerchantSales());
            pstmt.setString(19,monitoringParameterMappingVO.getIsAlertToMerchantFraud());
            pstmt.setString(20,monitoringParameterMappingVO.getIsAlertToMerchantRF());
            pstmt.setString(21,monitoringParameterMappingVO.getIsAlertToMerchantCB());
            pstmt.setString(22,monitoringParameterMappingVO.getIsAlertToMerchantTech());
            pstmt.setString(23,monitoringParameterMappingVO.getIsAlertToPartnerSales());
            pstmt.setString(24,monitoringParameterMappingVO.getIsAlertToPartnerFraud());
            pstmt.setString(25,monitoringParameterMappingVO.getIsAlertToPartnerRF());
            pstmt.setString(26,monitoringParameterMappingVO.getIsAlertToPartnerCB());
            pstmt.setString(27,monitoringParameterMappingVO.getIsAlertToPartnerTech());
            pstmt.setString(28,monitoringParameterMappingVO.getIsAlertToAgentSales());
            pstmt.setString(29,monitoringParameterMappingVO.getIsAlertToAgentFraud());
            pstmt.setString(30,monitoringParameterMappingVO.getIsAlertToAgentRF());
            pstmt.setString(31,monitoringParameterMappingVO.getIsAlertToAgentCB());
            pstmt.setString(32,monitoringParameterMappingVO.getIsAlertToAgentTech());
            pstmt.setString(33,monitoringParameterMappingVO.getIsDailyExecution());
            pstmt.setString(34,monitoringParameterMappingVO.getIsWeeklyExecution());
            pstmt.setString(35,monitoringParameterMappingVO.getIsMonthlyExecution());
            pstmt.setDouble(36,monitoringParameterMappingVO.getWeeklyAlertThreshold());
            pstmt.setDouble(37,monitoringParameterMappingVO.getWeeklySuspensionThreshold());
            pstmt.setDouble(38,monitoringParameterMappingVO.getMonthlyAlertThreshold());
            pstmt.setDouble(39,monitoringParameterMappingVO.getMonthlySuspensionThreshold());
            int k=pstmt.executeUpdate();
            if (k==1)
            {
                status="success";
            }
        }
        catch (SystemError se)
        {
            status="failed";
            logger.error("SystemError::::::"+se.getMessage());
            PZExceptionHandler.raiseDBViolationException("MerchantMonitoringDAO.java", "monitoringParameterMapping()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            status="failed";
            logger.error("SQLException::::::"+e.getMessage());
            PZExceptionHandler.raiseDBViolationException("MerchantMonitoringDAO.java", "monitoringParameterMapping()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }
    public String monitoringParameterMappingFromPartnerAccount(MonitoringParameterMappingVO monitoringParameterMappingVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = "";
        String status = "";

        try
        {
            conn = Database.getConnection();
            query = "insert into member_account_monitoringpara_mapping(mappingid,memberid,terminalid,alert_threshold,suspension_threshold,alert_activation,suspension_activation,isalerttomerchant,monitoing_para_id,isalerttomerchant_sales,isalerttomerchant_fraud,isalerttomerchant_rf,isalerttomerchant_cb,isalerttomerchant_tech,isalerttopartner,isalerttopartner_sales,isalerttopartner_fraud,isalerttopartner_rf,isalerttopartner_cb,isalerttopartner_tech,isdailyexecution,isweeklyexecution,ismonthlyexecution,weekly_alert_threshold,weekly_suspension_threshold,monthly_alert_threshold,monthly_suspension_threshold,alertMessage,isalerttoadmin,isalerttoadmin_sales,isalerttoadmin_rf,isalerttoadmin_cb,isalerttoadmin_fraud,isalerttoadmin_tech,isalerttoagent,isalerttoagent_sales,isalerttoagent_rf,isalerttoagent_cb,isalerttoagent_fraud,isalerttoagent_tech,dtstamp)values(NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()))";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1,monitoringParameterMappingVO.getMemberId());
            pstmt.setInt(2,monitoringParameterMappingVO.getTerminalId());
            pstmt.setDouble(3, monitoringParameterMappingVO.getAlertThreshold());
            pstmt.setDouble(4, monitoringParameterMappingVO.getSuspensionThreshold());
            pstmt.setString(5, monitoringParameterMappingVO.getAlertActivation());
            pstmt.setString(6, monitoringParameterMappingVO.getSuspensionActivation());
            pstmt.setString(7, monitoringParameterMappingVO.getIsAlertToMerchant());
            pstmt.setInt(8, monitoringParameterMappingVO.getMonitoringParameterId());
            pstmt.setString(9, monitoringParameterMappingVO.getIsAlertToMerchantSales());
            pstmt.setString(10, monitoringParameterMappingVO.getIsAlertToMerchantFraud());
            pstmt.setString(11, monitoringParameterMappingVO.getIsAlertToMerchantRF());
            pstmt.setString(12, monitoringParameterMappingVO.getIsAlertToMerchantCB());
            pstmt.setString(13, monitoringParameterMappingVO.getIsAlertToMerchantTech());
            pstmt.setString(14, monitoringParameterMappingVO.getIsAlertToPartner());
            pstmt.setString(15, monitoringParameterMappingVO.getIsAlertToPartnerSales());
            pstmt.setString(16, monitoringParameterMappingVO.getIsAlertToPartnerFraud());
            pstmt.setString(17, monitoringParameterMappingVO.getIsAlertToPartnerRF());
            pstmt.setString(18, monitoringParameterMappingVO.getIsAlertToPartnerCB());
            pstmt.setString(19, monitoringParameterMappingVO.getIsAlertToPartnerTech());
            pstmt.setString(20, monitoringParameterMappingVO.getIsDailyExecution());
            pstmt.setString(21,monitoringParameterMappingVO.getIsWeeklyExecution());
            pstmt.setString(22,monitoringParameterMappingVO.getIsMonthlyExecution());
            pstmt.setDouble(23, monitoringParameterMappingVO.getWeeklyAlertThreshold());
            pstmt.setDouble(24, monitoringParameterMappingVO.getWeeklySuspensionThreshold());
            pstmt.setDouble(25, monitoringParameterMappingVO.getMonthlyAlertThreshold());
            pstmt.setDouble(26, monitoringParameterMappingVO.getMonthlySuspensionThreshold());
            pstmt.setString(27, monitoringParameterMappingVO.getAlertMessage());
            pstmt.setString(28, monitoringParameterMappingVO.getIsAlertToAdmin());
            pstmt.setString(29, monitoringParameterMappingVO.getIsAlertToAdminSales());
            pstmt.setString(30, monitoringParameterMappingVO.getIsAlertToAdminRF());
            pstmt.setString(31, monitoringParameterMappingVO.getIsAlertToAdminCB());
            pstmt.setString(32, monitoringParameterMappingVO.getIsAlertToAdminFraud());
            pstmt.setString(33, monitoringParameterMappingVO.getIsAlertToAdminTech());
            pstmt.setString(34, monitoringParameterMappingVO.getIsAlertToAgent());
            pstmt.setString(35, monitoringParameterMappingVO.getIsAlertToAgentSales());
            pstmt.setString(36, monitoringParameterMappingVO.getIsAlertToAgentRF());
            pstmt.setString(37, monitoringParameterMappingVO.getIsAlertToAgentCB());
            pstmt.setString(38, monitoringParameterMappingVO.getIsAlertToAgentFraud());
            pstmt.setString(39,monitoringParameterMappingVO.getIsAlertToAgentTech());
            int k=pstmt.executeUpdate();
            if (k==1)
            {
                status="success";
            }
        }
        catch (SystemError se)
        {
            status="failed";
            logger.error("SystemError::::::"+se.getMessage());
            PZExceptionHandler.raiseDBViolationException("MerchantMonitoringDAO.java", "monitoringParameterMappingFromPartnerAccount()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            status="failed";
            logger.error("SQLException::::::"+e.getMessage());
            PZExceptionHandler.raiseDBViolationException("MerchantMonitoringDAO.java", "monitoringParameterMappingFromPartnerAccount()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public String monitoringParameterMappingLogDetails(MonitoringParameterMappingVO monitoringParameterMappingVO,String actionExecutor) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = "";
        String status = "";

        try
        {
            conn = Database.getConnection();
            query = "insert into monitoring_parameter_log_details(id,memberid,terminalid,alert_threshold,suspension_threshold,alert_activation,suspension_activation,isalerttoadmin,isalerttomerchant,isalerttopartner,isalerttoagent,alertMessage,monitoring_para_id,isalerttoadmin_sales,dtstamp,isalerttoadmin_fraud,isalerttoadmin_rf,isalerttoadmin_cb,isalerttoadmin_tech,isalerttomerchant_sales,isalerttomerchant_fraud,isalerttomerchant_rf,isalerttomerchant_cb,isalerttomerchant_tech,isalerttopartner_sales,isalerttopartner_fraud,isalerttopartner_rf,isalerttopartner_cb,isalerttopartner_tech,isalerttoagent_sales,isalerttoagent_fraud,isalerttoagent_rf,isalerttoagent_cb,isalerttoagent_tech,isdailyexecution,isweeklyexecution,ismonthlyexecution,weekly_alert_threshold,weekly_suspension_threshold,monthly_alert_threshold,monthly_suspension_threshold,actionExecutor)values(NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1,monitoringParameterMappingVO.getMemberId());
            pstmt.setInt(2,monitoringParameterMappingVO.getTerminalId());
            pstmt.setDouble(3,monitoringParameterMappingVO.getAlertThreshold());
            pstmt.setDouble(4,monitoringParameterMappingVO.getSuspensionThreshold());
            pstmt.setString(5,monitoringParameterMappingVO.getAlertActivation());
            pstmt.setString(6,monitoringParameterMappingVO.getSuspensionActivation());
            pstmt.setString(7,monitoringParameterMappingVO.getIsAlertToAdmin());
            pstmt.setString(8,monitoringParameterMappingVO.getIsAlertToMerchant());
            pstmt.setString(9,monitoringParameterMappingVO.getIsAlertToPartner());
            pstmt.setString(10,monitoringParameterMappingVO.getIsAlertToAgent());
            pstmt.setString(11,monitoringParameterMappingVO.getAlertMessage());
            pstmt.setInt(12,monitoringParameterMappingVO.getMonitoringParameterId());
            pstmt.setString(13,monitoringParameterMappingVO.getIsAlertToAdminSales());
            pstmt.setString(14,monitoringParameterMappingVO.getIsAlertToAdminFraud());
            pstmt.setString(15,monitoringParameterMappingVO.getIsAlertToAdminRF());
            pstmt.setString(16,monitoringParameterMappingVO.getIsAlertToAdminCB());
            pstmt.setString(17,monitoringParameterMappingVO.getIsAlertToAdminTech());
            pstmt.setString(18,monitoringParameterMappingVO.getIsAlertToMerchantSales());
            pstmt.setString(19,monitoringParameterMappingVO.getIsAlertToMerchantFraud());
            pstmt.setString(20,monitoringParameterMappingVO.getIsAlertToMerchantRF());
            pstmt.setString(21,monitoringParameterMappingVO.getIsAlertToMerchantCB());
            pstmt.setString(22,monitoringParameterMappingVO.getIsAlertToMerchantTech());
            pstmt.setString(23,monitoringParameterMappingVO.getIsAlertToPartnerSales());
            pstmt.setString(24,monitoringParameterMappingVO.getIsAlertToPartnerFraud());
            pstmt.setString(25,monitoringParameterMappingVO.getIsAlertToPartnerRF());
            pstmt.setString(26,monitoringParameterMappingVO.getIsAlertToPartnerCB());
            pstmt.setString(27,monitoringParameterMappingVO.getIsAlertToPartnerTech());
            pstmt.setString(28,monitoringParameterMappingVO.getIsAlertToAgentSales());
            pstmt.setString(29,monitoringParameterMappingVO.getIsAlertToAgentFraud());
            pstmt.setString(30,monitoringParameterMappingVO.getIsAlertToAgentRF());
            pstmt.setString(31,monitoringParameterMappingVO.getIsAlertToAgentCB());
            pstmt.setString(32,monitoringParameterMappingVO.getIsAlertToAgentTech());
            pstmt.setString(33,monitoringParameterMappingVO.getIsDailyExecution());
            pstmt.setString(34,monitoringParameterMappingVO.getIsWeeklyExecution());
            pstmt.setString(35,monitoringParameterMappingVO.getIsMonthlyExecution());
            pstmt.setDouble(36,monitoringParameterMappingVO.getWeeklyAlertThreshold());
            pstmt.setDouble(37,monitoringParameterMappingVO.getWeeklySuspensionThreshold());
            pstmt.setDouble(38,monitoringParameterMappingVO.getMonthlyAlertThreshold());
            pstmt.setDouble(39,monitoringParameterMappingVO.getMonthlySuspensionThreshold());
            pstmt.setString(40,actionExecutor);
            int k=pstmt.executeUpdate();
            if (k==1)
            {
                status="success";
            }
        }
        catch (SystemError se)
        {
            status="failed";
            logger.error("SystemError::::::"+se.getMessage());
            PZExceptionHandler.raiseDBViolationException("MerchantMonitoringDAO.java", "monitoringParameterMappingLogDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            status="failed";
            logger.error("SQLException::::::"+e.getMessage());
            PZExceptionHandler.raiseDBViolationException("MerchantMonitoringDAO.java", "monitoringParameterMappingLogDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public Map<String, Map<String, List<MonitoringParameterMappingVO>>> getMonitoringParameterGroupByMerchant(MonitoringFrequency monitoringFrequency) throws PZDBViolationException
    {
        Map<String, Map<String, List<MonitoringParameterMappingVO>>> stringListMapMerchantLevel = new HashMap();
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res1=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT memberid,COUNT(*) FROM member_account_monitoringpara_mapping GROUP BY memberid";
            pstmt = conn.prepareStatement(query);
            res1 = pstmt.executeQuery();
            while (res1.next())
            {
                Map<String, List<MonitoringParameterMappingVO>> stringListMap = new HashMap();
                List<MonitoringParameterMappingVO> monitoringParameterMappingVOs = null;
                MonitoringParameterMappingVO monitoringParameterMappingVO = null;
                MonitoringParameterVO monitoringParameterVO = null;
                TerminalVO terminalVO = null;

                String memberId = res1.getString("memberid");
                //TODO:please make join on member_account_mapping and get paymodeid,cardtypeid,accountid and terminalid in above query.
                query = "SELECT mpm.*,mamm.mappingid,mamm.memberid,mamm.terminalid,alert_threshold,suspension_threshold,alert_activation,suspension_activation,isalerttoadmin,isalerttomerchant,isalerttopartner,isalerttoagent,mpm.monitoing_para_id,mam.accountid,mam.paymodeid,mam.cardtypeid FROM member_account_monitoringpara_mapping AS mamm JOIN monitoring_parameter_master AS mpm ON mamm.monitoing_para_id=mpm.monitoing_para_id JOIN member_account_mapping AS mam ON mamm.terminalid=mam.terminalid WHERE mamm.memberid=? and mpm.monitoing_frequency=?";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, memberId);
                pstmt.setString(2, monitoringFrequency.toString());
                ResultSet res = pstmt.executeQuery();
                while (res.next())
                {
                    monitoringParameterMappingVO = new MonitoringParameterMappingVO();
                    monitoringParameterVO = new MonitoringParameterVO();

                    //TODO:please set those values in "terminalVO" file
                    terminalVO = new TerminalVO();
                    terminalVO.setMemberId(res.getString("memberid"));
                    terminalVO.setTerminalId(res.getString("terminalid"));
                    terminalVO.setAccountId(res.getString("accountid"));
                    terminalVO.setPaymodeId(res.getString("paymodeid"));
                    terminalVO.setCardTypeId(res.getString("cardtypeid"));

                    monitoringParameterVO.setMonitoringParameterName(res.getString("monitoing_para_name"));
                    monitoringParameterVO.setMonitoingParaTechName(res.getString("monitoing_para_tech_name"));
                    monitoringParameterVO.setMonitoingCategory(res.getString("monitoing_category"));
                    monitoringParameterVO.setMonitoingKeyword(res.getString("monitoing_keyword"));
                    monitoringParameterVO.setMonitoingSubKeyword(res.getString("monitoing_subkeyword"));
                    monitoringParameterVO.setMonitoingAlertCategory(res.getString("monitoing_alert_category"));
                    monitoringParameterVO.setMonitoingFrequency(res.getString("monitoing_frequency"));
                    monitoringParameterVO.setMonitoingOnChannel(res.getString("monitoing_onchannel"));


                    monitoringParameterMappingVO.setMappingId(res.getInt("mappingid"));
                    monitoringParameterMappingVO.setMemberId(res.getInt("memberid"));
                    monitoringParameterMappingVO.setTerminalId(res.getInt("terminalid"));
                    monitoringParameterMappingVO.setAlertThreshold(res.getDouble("alert_threshold"));
                    monitoringParameterMappingVO.setSuspensionThreshold(res.getDouble("suspension_threshold"));
                    monitoringParameterMappingVO.setAlertActivation(res.getString("alert_activation"));
                    monitoringParameterMappingVO.setSuspensionActivation(res.getString("suspension_activation"));
                    monitoringParameterMappingVO.setIsAlertToAdmin(res.getString("isalerttoadmin"));
                    monitoringParameterMappingVO.setIsAlertToMerchant(res.getString("isalerttomerchant"));
                    monitoringParameterMappingVO.setIsAlertToPartner(res.getString("isalerttopartner"));
                    monitoringParameterMappingVO.setIsAlertToAgent(res.getString("isalerttoagent"));
                    monitoringParameterMappingVO.setMonitoringParameterId(res.getInt("monitoing_para_id"));

                    monitoringParameterMappingVO.setMonitoringParameterVO(monitoringParameterVO);
                    monitoringParameterMappingVO.setTerminalVO(terminalVO);

                    //TODO:set "terminalVO" in  "MonitoringParameterMappingVO" file
                    monitoringParameterMappingVOs = stringListMap.get(res.getString("terminalid"));
                    if (monitoringParameterMappingVOs == null)
                    {
                        monitoringParameterMappingVOs = new ArrayList();
                        monitoringParameterMappingVOs.add(monitoringParameterMappingVO);
                    }
                    else
                    {
                        monitoringParameterMappingVOs.add(monitoringParameterMappingVO);
                    }
                    stringListMap.put(res.getString("terminalid"), monitoringParameterMappingVOs);
                }
                stringListMapMerchantLevel.put(memberId, stringListMap);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing System Exception as SystemError :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getMonitoringParameterGroupByMerchant()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing SQL Exception as SystemError :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getMonitoringParameterGroupByMerchant()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as Exception :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getMonitoringParameterGroupByMerchant()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(res1);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return stringListMapMerchantLevel;
    }
    public Map<String, List<MonitoringParameterMappingVO>> getDailyMonitoringParameterGroupByMerchantId(String memberId) throws PZDBViolationException
    {
        Map<String, List<MonitoringParameterMappingVO>> stringListMap = new HashMap();
        List<MonitoringParameterMappingVO> monitoringParameterMappingVOs = null;
        MonitoringParameterMappingVO monitoringParameterMappingVO = null;
        MonitoringParameterVO monitoringParameterVO = null;
        TerminalVO terminalVO = null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;
        try
        {
            conn = Database.getRDBConnection();
            //TODO:please make join on member_account_mapping and get paymodeid,cardtypeid,accountid and terminalid in above query.
            String query = "SELECT mpm.*,mamm.mappingid,mamm.memberid,mamm.terminalid,alert_threshold,suspension_threshold,alert_activation,suspension_activation,isalerttoadmin,isalerttomerchant,isalerttopartner,isalerttoagent,mpm.monitoing_para_id,mam.accountid,mam.paymodeid,mam.cardtypeid FROM member_account_monitoringpara_mapping AS mamm JOIN monitoring_parameter_master AS mpm ON mamm.monitoing_para_id=mpm.monitoing_para_id JOIN member_account_mapping AS mam ON mamm.terminalid=mam.terminalid WHERE mamm.memberid=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, memberId);
            res = pstmt.executeQuery();
            while (res.next())
            {
                monitoringParameterMappingVO = new MonitoringParameterMappingVO();
                monitoringParameterVO = new MonitoringParameterVO();

                //TODO:please set those values in "terminalVO" file
                terminalVO = new TerminalVO();
                terminalVO.setMemberId(res.getString("memberid"));
                terminalVO.setTerminalId(res.getString("terminalid"));
                terminalVO.setAccountId(res.getString("accountid"));
                terminalVO.setPaymodeId(res.getString("paymodeid"));
                terminalVO.setCardTypeId(res.getString("cardtypeid"));

                monitoringParameterVO.setMonitoringParameterId(res.getInt("monitoing_para_id"));
                monitoringParameterVO.setMonitoringParameterName(res.getString("monitoing_para_name"));
                monitoringParameterVO.setMonitoingParaTechName(res.getString("monitoing_para_tech_name"));
                monitoringParameterVO.setMonitoingKeyword(res.getString("monitoing_category"));
                monitoringParameterVO.setMonitoingKeyword(res.getString("monitoing_keyword"));
                monitoringParameterVO.setMonitoingSubKeyword(res.getString("monitoing_subkeyword"));
                monitoringParameterVO.setMonitoingAlertCategory(res.getString("monitoing_alert_category"));
                monitoringParameterVO.setMonitoingFrequency(res.getString("monitoing_frequency"));
                monitoringParameterVO.setMonitoingOnChannel(res.getString("monitoing_onchannel"));
                monitoringParameterVO.setMonitoringUnit(res.getString("monitoring_unit"));

                monitoringParameterMappingVO.setMappingId(res.getInt("mappingid"));
                monitoringParameterMappingVO.setMemberId(res.getInt("memberid"));
                monitoringParameterMappingVO.setTerminalId(res.getInt("terminalid"));
                monitoringParameterMappingVO.setAlertThreshold(res.getDouble("alert_threshold"));
                monitoringParameterMappingVO.setSuspensionThreshold(res.getDouble("suspension_threshold"));
                monitoringParameterMappingVO.setAlertActivation(res.getString("alert_activation"));
                monitoringParameterMappingVO.setSuspensionActivation(res.getString("suspension_activation"));
                monitoringParameterMappingVO.setIsAlertToAdmin(res.getString("isalerttoadmin"));
                monitoringParameterMappingVO.setIsAlertToMerchant(res.getString("isalerttomerchant"));
                monitoringParameterMappingVO.setIsAlertToPartner(res.getString("isalerttopartner"));
                monitoringParameterMappingVO.setIsAlertToAgent(res.getString("isalerttoagent"));
                monitoringParameterMappingVO.setMonitoringParameterId(res.getInt("monitoing_para_id"));

                monitoringParameterMappingVO.setMonitoringParameterVO(monitoringParameterVO);
                monitoringParameterMappingVO.setTerminalVO(terminalVO);

                //TODO:set "terminalVO" in  "MonitoringParameterMappingVO" file

                monitoringParameterMappingVOs = stringListMap.get(res.getString("terminalid"));
                if (monitoringParameterMappingVOs == null)
                {
                    monitoringParameterMappingVOs = new ArrayList();
                    monitoringParameterMappingVOs.add(monitoringParameterMappingVO);
                }
                else
                {
                    monitoringParameterMappingVOs.add(monitoringParameterMappingVO);
                }
                stringListMap.put(res.getString("terminalid"), monitoringParameterMappingVOs);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing System Exception as SystemError :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getDailyMonitoringParameterGroupByMerchantId()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing SQL Exception as SystemError :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getDailyMonitoringParameterGroupByMerchantId()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as Exception :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getDailyMonitoringParameterGroupByMerchantId()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return stringListMap;
    }

    public Map<String, List<MonitoringParameterMappingVO>> getDailyMonitoringParameterGroupByMerchantOld() throws PZDBViolationException
    {
        Map<String, List<MonitoringParameterMappingVO>> stringListMap = new HashMap();
        List<MonitoringParameterMappingVO> monitoringParameterMappingVOs = null;
        MonitoringParameterMappingVO monitoringParameterMappingVO = null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT mappingid,memberid,terminalid,alert_threshold,suspension_threshold,alert_activation,suspension_activation,isalerttoadmin,isalerttomerchant,isalerttopartner,isalerttoagent,monitoing_para_id FROM member_account_monitoringpara_mapping";
            pstmt = conn.prepareStatement(query);
            res = pstmt.executeQuery();
            while (res.next())
            {
                monitoringParameterMappingVO = new MonitoringParameterMappingVO();
                monitoringParameterMappingVO.setMappingId(res.getInt("mappingid"));
                monitoringParameterMappingVO.setMemberId(res.getInt("memberid"));
                monitoringParameterMappingVO.setTerminalId(res.getInt("terminalid"));
                monitoringParameterMappingVO.setAlertThreshold(res.getDouble("alert_threshold"));
                monitoringParameterMappingVO.setSuspensionThreshold(res.getDouble("suspension_threshold"));
                monitoringParameterMappingVO.setAlertActivation(res.getString("alert_activation"));
                monitoringParameterMappingVO.setSuspensionActivation(res.getString("suspension_activation"));
                monitoringParameterMappingVO.setIsAlertToAdmin(res.getString("isalerttoadmin"));
                monitoringParameterMappingVO.setIsAlertToMerchant(res.getString("isalerttomerchant"));
                monitoringParameterMappingVO.setIsAlertToPartner(res.getString("isalerttopartner"));
                monitoringParameterMappingVO.setIsAlertToAgent(res.getString("isalerttopartner"));
                monitoringParameterMappingVO.setMonitoringParameterId(res.getInt("monitoing_para_id"));

                monitoringParameterMappingVOs = stringListMap.get(res.getString("memberid"));
                if (monitoringParameterMappingVOs == null)
                {
                    monitoringParameterMappingVOs = new ArrayList();
                    monitoringParameterMappingVOs.add(monitoringParameterMappingVO);
                }
                stringListMap.put(res.getString("memberid"), monitoringParameterMappingVOs);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing System Exception as SystemError :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getDailyMonitoringParameterGroupByMerchantOld()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing SQL Exception as SystemError :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getDailyMonitoringParameterGroupByMerchantOld()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as Exception :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getDailyMonitoringParameterGroupByMerchantOld()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return stringListMap;
    }
    public boolean isMappingAvailable(String memberId, String terminalId, String parameterId)throws  PZDBViolationException
    {
        Connection con = null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        boolean status=false;
        try
        {
            con=Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select mappingid from member_account_monitoringpara_mapping where memberid=? and terminalid=? and monitoing_para_id=?");
            ps = con.prepareStatement(qry.toString());
            ps.setString(1,memberId);
            ps.setString(2, terminalId);
            ps.setString(3, parameterId);
            rs = ps.executeQuery();
            if(rs.next())
            {
                status=true;
            }
        }
        catch (SQLException e)
        {
            status=true;
            logger.error("SQL Exception while adding new Fraud Sub-account rule Mapping",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isMappingAvailable()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            status=true;
            logger.error("System Error while  adding new Fraud Sub-account rule Mapping::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isMappingAvailable()", null, "Common", "Sql exception while connecting to rule master", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return status;
    }
    public boolean isParameterAvailable(MonitoringParameterVO monitoringParameterVO)throws  PZDBViolationException
    {
        Connection con = null;
        ResultSet rs=null;
        PreparedStatement ps=null;
        boolean status=false;
        try
        {
            con=Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select monitoing_para_id from monitoring_parameter_master where monitoing_category=? and monitoring_deviation=? and monitoing_keyword=? and monitoing_subkeyword=? and monitoing_alert_category=? and monitoing_onchannel=?");
            ps = con.prepareStatement(qry.toString());
            ps.setString(1,monitoringParameterVO.getMonitoingCategory());
            ps.setString(2,monitoringParameterVO.getMonitoingDeviation());
            ps.setString(3,monitoringParameterVO.getMonitoingKeyword());
            ps.setString(4,monitoringParameterVO.getMonitoingSubKeyword());
            ps.setString(5,monitoringParameterVO.getMonitoingAlertCategory());
            ps.setString(6, monitoringParameterVO.getMonitoingOnChannel());
            rs = ps.executeQuery();
            if(rs.next())
            {
                status=true;
            }
        }
        catch (SQLException e)
        {
            status=true;
            logger.error("SQL Exception while adding new Fraud Sub-account rule Mapping",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isParameterAvailable()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            status=true;
            logger.error("System Error while  adding new Fraud Sub-account rule Mapping::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isParameterAvailable()", null, "Common", "Sql exception while connecting to rule master", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return status;
    }

    public boolean isParameterNameAvailable(MonitoringParameterVO monitoringParameterVO)throws  PZDBViolationException
    {
        Connection con = null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        boolean status=false;
        try
        {
            con=Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select monitoing_para_id from monitoring_parameter_master where monitoing_para_name=?");
            ps = con.prepareStatement(qry.toString());
            ps.setString(1,monitoringParameterVO.getMonitoringParameterName());
            rs = ps.executeQuery();
            if(rs.next())
            {
                status=true;
            }
        }
        catch (SQLException e)
        {
            status=true;
            logger.error("SQL Exception while adding new Parametr Name",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isParameterAvailable()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            status=true;
            logger.error("System Error while  adding new Parameter Name::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isParameterAvailable()", null, "Common", "Sql exception while connecting to rule master", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return status;
    }

    public MonitoringParameterVO getMonitoringParameterDetails(String monitoringParaId)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        ResultSet rs=null;
        MonitoringParameterVO monitoringParameterVO=null;
        try
        {
            con= Database.getRDBConnection();
            StringBuffer sbQuery=new StringBuffer("select monitoing_para_id,monitoing_para_name,monitoing_para_tech_name,monitoing_category,monitoring_deviation,monitoing_keyword,monitoing_subkeyword," +
                    "monitoing_alert_category,monitoing_onchannel,monitoing_frequency,monitoring_unit,default_alert_threshold,default_suspension_threshold,default_isAlertToAdmin,default_isAlertToMerchant,default_isAlertToPartner,default_isAlertToAgent,default_alert_activation,default_suspension_activation,default_alertMsg,isalerttoadmin_sales,isalerttoadmin_fraud,isalerttoadmin_rf,isalerttoadmin_cb,isalerttoadmin_tech,isalerttomerchant_sales,isalerttomerchant_fraud,isalerttomerchant_rf,isalerttomerchant_cb,isalerttomerchant_tech,isalerttopartner_sales,isalerttopartner_fraud,isalerttopartner_rf,isalerttopartner_cb,isalerttopartner_tech,isalerttoagent_sales,isalerttoagent_fraud,isalerttoagent_rf,isalerttoagent_cb,isalerttoagent_tech,isalerttomerchant_sales,isalerttomerchant_rf,isalerttomerchant_cb,isalerttomerchant_fraud,isalerttomerchant_tech,isalerttopartner_sales,isalerttopartner_rf,isalerttopartner_cb,isalerttopartner_fraud,isalerttopartner_tech,isalerttoagent_sales,isalerttoagent_rf,isalerttoagent_cb,isalerttoagent_fraud,isalerttoagent_tech,isdailyexecution,isweeklyexecution,ismonthlyexecution, weekly_alert_threshold, weekly_suspension_threshold, monthly_alert_threshold, monthly_suspension_threshold, displayChartType from monitoring_parameter_master where monitoing_para_id=?");
            pstmt=con.prepareStatement(sbQuery.toString());
            pstmt.setString(1,monitoringParaId);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                monitoringParameterVO=new MonitoringParameterVO();
                monitoringParameterVO.setMonitoringParameterId(rs.getInt("monitoing_para_id"));
                monitoringParameterVO.setMonitoringParameterName(rs.getString("monitoing_para_name"));
                monitoringParameterVO.setMonitoingParaTechName(rs.getString("monitoing_para_tech_name"));
                monitoringParameterVO.setMonitoingCategory(rs.getString("monitoing_category"));
                monitoringParameterVO.setMonitoingDeviation(rs.getString("monitoring_deviation"));
                monitoringParameterVO.setMonitoingKeyword(rs.getString("monitoing_keyword"));
                monitoringParameterVO.setMonitoingSubKeyword(rs.getString("monitoing_subkeyword"));
                monitoringParameterVO.setMonitoingAlertCategory(rs.getString("monitoing_alert_category"));
                monitoringParameterVO.setMonitoingOnChannel(rs.getString("monitoing_onchannel"));
                monitoringParameterVO.setMonitoringUnit(rs.getString("monitoring_unit"));
                monitoringParameterVO.setDefaultAlertThreshold(rs.getDouble("default_alert_threshold"));
                monitoringParameterVO.setDefaultSuspensionThreshold(rs.getDouble("default_suspension_threshold"));
                monitoringParameterVO.setDefaultIsAlertToAdmin(rs.getString("default_isAlertToAdmin"));
                monitoringParameterVO.setDefaultIsAlertToMerchant(rs.getString("default_isAlertToMerchant"));
                monitoringParameterVO.setDefaultIsAlertToPartner(rs.getString("default_isAlertToPartner"));
                monitoringParameterVO.setDefaultIsAlertToAgent(rs.getString("default_isAlertToAgent"));
                monitoringParameterVO.setDefaultAlertActivation(rs.getString("default_alert_activation"));
                monitoringParameterVO.setDefaultSuspensionActivation(rs.getString("default_suspension_activation"));
                monitoringParameterVO.setDefaultAlertMsg(rs.getString("default_alertMsg"));

                monitoringParameterVO.setDefaultIsAlertToAdminSales(rs.getString("isalerttoadmin_sales"));
                monitoringParameterVO.setDefaultIsAlertToAdminRF(rs.getString("isalerttoadmin_rf"));
                monitoringParameterVO.setDefaultIsAlertToAdminCB(rs.getString("isalerttoadmin_cb"));
                monitoringParameterVO.setDefaultIsAlertToAdminFraud(rs.getString("isalerttoadmin_fraud"));
                monitoringParameterVO.setDefaultIsAlertToAdminTech(rs.getString("isalerttoadmin_tech"));

                monitoringParameterVO.setDefaultIsAlertToMerchantSales(rs.getString("isalerttomerchant_sales"));
                monitoringParameterVO.setDefaultIsAlertToMerchantRF(rs.getString("isalerttomerchant_rf"));
                monitoringParameterVO.setDefaultIsAlertToMerchantCB(rs.getString("isalerttomerchant_cb"));
                monitoringParameterVO.setDefaultIsAlertToMerchantFraud(rs.getString("isalerttomerchant_fraud"));
                monitoringParameterVO.setDefaultIsAlertToMerchantTech(rs.getString("isalerttomerchant_tech"));

                monitoringParameterVO.setDefaultIsAlertToPartnerSales(rs.getString("isalerttopartner_sales"));
                monitoringParameterVO.setDefaultIsAlertToPartnerRF(rs.getString("isalerttopartner_rf"));
                monitoringParameterVO.setDefaultIsAlertToPartnerCB(rs.getString("isalerttopartner_cb"));
                monitoringParameterVO.setDefaultIsAlertToPartnerFraud(rs.getString("isalerttopartner_fraud"));
                monitoringParameterVO.setDefaultIsAlertToPartnerTech(rs.getString("isalerttopartner_tech"));

                monitoringParameterVO.setDefaultIsAlertToAgentSales(rs.getString("isalerttoagent_sales"));
                monitoringParameterVO.setDefaultIsAlertToAgentRF(rs.getString("isalerttoagent_rf"));
                monitoringParameterVO.setDefaultIsAlertToAgentCB(rs.getString("isalerttoagent_cb"));
                monitoringParameterVO.setDefaultIsAlertToAgentFraud(rs.getString("isalerttoagent_fraud"));
                monitoringParameterVO.setDefaultIsAlertToAgentTech(rs.getString("isalerttoagent_tech"));

                monitoringParameterVO.setIsDailyExecution(rs.getString("isdailyexecution"));
                monitoringParameterVO.setIsWeeklyExecution(rs.getString("isweeklyexecution"));
                monitoringParameterVO.setIsMonthlyExecution(rs.getString("ismonthlyexecution"));

                monitoringParameterVO.setWeeklyAlertThreshold(rs.getDouble("weekly_alert_threshold"));
                monitoringParameterVO.setWeeklySuspensionThreshold(rs.getDouble("weekly_suspension_threshold"));
                monitoringParameterVO.setMonthlyAlertThreshold(rs.getDouble("monthly_alert_threshold"));
                monitoringParameterVO.setMonthlySuspensionThreshold(rs.getDouble("monthly_suspension_threshold"));
                monitoringParameterVO.setDisplayChartType(rs.getString("displayChartType"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting monitoring parameter details",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMonitoringParameterDetails()", null, "Common", "Sql exception while connecting to Parameter's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting monitoring parameter details::",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMonitoringParameterDetails()", null, "Common", "Sql exception while connecting to Parameter's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return monitoringParameterVO;
    }
    public MonitoringParameterMappingVO getMonitoringParameterDetailsFromMapping(String monitoringParaId,String memberid,String terminalId)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        ResultSet res=null;
        MonitoringParameterMappingVO monitoringParameterMappingVO=null;
        try
        {
            //con= Database.getRDBConnection();
            con= Database.getRDBConnection();
            StringBuffer sbQuery=new StringBuffer("SELECT mamm.*,mpm.displayChartType FROM member_account_monitoringpara_mapping AS mamm JOIN monitoring_parameter_master AS mpm ON mpm.monitoing_para_id=mamm.monitoing_para_id WHERE  mamm.monitoing_para_id=? AND memberid=? AND terminalid=?");
            pstmt=con.prepareStatement(sbQuery.toString());
            pstmt.setString(1,monitoringParaId);
            pstmt.setString(2,memberid);
            pstmt.setString(3,terminalId);
            res=pstmt.executeQuery();
            if(res.next())
            {
                monitoringParameterMappingVO = new MonitoringParameterMappingVO();
                monitoringParameterMappingVO.setMonitoringParameterId(res.getInt("monitoing_para_id"));
                monitoringParameterMappingVO.setAlertThreshold(res.getDouble("alert_threshold"));
                monitoringParameterMappingVO.setSuspensionThreshold(res.getDouble("suspension_threshold"));
                monitoringParameterMappingVO.setWeeklyAlertThreshold(res.getDouble("weekly_alert_threshold"));
                monitoringParameterMappingVO.setWeeklySuspensionThreshold(res.getDouble("weekly_suspension_threshold"));
                monitoringParameterMappingVO.setMonthlyAlertThreshold(res.getDouble("monthly_alert_threshold"));
                monitoringParameterMappingVO.setMonthlySuspensionThreshold(res.getDouble("monthly_suspension_threshold"));
                monitoringParameterMappingVO.setDisplayChartType(res.getString("displayChartType"));
                monitoringParameterMappingVO.setIsDailyExecution(res.getString("isdailyexecution"));
                monitoringParameterMappingVO.setIsWeeklyExecution(res.getString("isweeklyexecution"));
                monitoringParameterMappingVO.setIsMonthlyExecution(res.getString("ismonthlyexecution"));

            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting monitoring parameter details",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMonitoringParameterDetailsFromMapping()", null, "Common", "Sql exception while connecting to Parameter's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting monitoring parameter details::",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMonitoringParameterDetailsFromMapping()", null, "Common", "Sql exception while connecting to Parameter's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return monitoringParameterMappingVO;
    }
    public MonitoringParameterMappingVO getMonitoringParameterFromMaster(String monitoringParaId)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        ResultSet res=null;
        MonitoringParameterMappingVO monitoringParameterMappingVO=null;
        try
        {
            con= Database.getRDBConnection();
            StringBuffer sbQuery=new StringBuffer("SELECT * from monitoring_parameter_master WHERE  monitoing_para_id=?");
            pstmt=con.prepareStatement(sbQuery.toString());
            pstmt.setString(1,monitoringParaId);
            res=pstmt.executeQuery();
            if(res.next())
            {
                monitoringParameterMappingVO = new MonitoringParameterMappingVO();
                monitoringParameterMappingVO.setMonitoringParameterId(res.getInt("monitoing_para_id"));
                monitoringParameterMappingVO.setAlertThreshold(res.getDouble("default_alert_threshold"));
                monitoringParameterMappingVO.setSuspensionThreshold(res.getDouble("default_suspension_threshold"));
                monitoringParameterMappingVO.setWeeklyAlertThreshold(res.getDouble("weekly_alert_threshold"));
                monitoringParameterMappingVO.setWeeklySuspensionThreshold(res.getDouble("weekly_suspension_threshold"));
                monitoringParameterMappingVO.setMonthlyAlertThreshold(res.getDouble("monthly_alert_threshold"));
                monitoringParameterMappingVO.setMonthlySuspensionThreshold(res.getDouble("monthly_suspension_threshold"));
                monitoringParameterMappingVO.setDisplayChartType(res.getString("displayChartType"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting monitoring parameter details",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMonitoringParameterFromMaster()", null, "Common", "Sql exception while connecting to Parameter's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting monitoring parameter details::",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMonitoringParameterFromMaster()", null, "Common", "Sql exception while connecting to Parameter's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return monitoringParameterMappingVO;
    }
    public List<MonitoringRuleLogVO> getMonitoringRuleLogDetails(String memberId,String terminalId) throws PZDBViolationException
    {
        List<MonitoringRuleLogVO> monitoringRuleLogVOs = new LinkedList();
        MonitoringRuleLogVO monitoringRuleLogVO=null;
        MonitoringParameterMappingVO monitoringParameterMappingVO;
        MonitoringParameterVO monitoringParameterVO=null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        try
        {
            con= Database.getRDBConnection();
            StringBuffer sbQuery = new StringBuffer("SELECT mpld.*,mpm.monitoing_para_name,FROM_UNIXTIME(dtstamp) AS modifiedon FROM monitoring_parameter_log_details AS mpld JOIN monitoring_parameter_master AS mpm ON mpld.monitoring_para_id=mpm.monitoing_para_id WHERE memberId =? AND terminalId =? order by dtstamp desc ");
            pstmt=con.prepareStatement(sbQuery.toString());
            pstmt.setString(1,memberId);
            pstmt.setString(2,terminalId);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                monitoringRuleLogVO=new MonitoringRuleLogVO();
                monitoringParameterMappingVO=new MonitoringParameterMappingVO();
                monitoringParameterVO=new MonitoringParameterVO();

                monitoringRuleLogVO.setActionExecutor(rs.getString("actionExecutor"));
                monitoringRuleLogVO.setModifiedOn(rs.getString("modifiedon"));
                monitoringParameterVO.setMonitoringParameterId(rs.getInt("monitoring_para_id"));
                monitoringParameterVO.setMonitoringParameterName(rs.getString("monitoing_para_name"));

                monitoringParameterMappingVO.setMonitoringParameterVO(monitoringParameterVO);
                monitoringRuleLogVO.setMonitoringParameterMappingVO(monitoringParameterMappingVO);

                monitoringRuleLogVO.setHistoryId(rs.getString("id"));
                monitoringRuleLogVOs.add(monitoringRuleLogVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting monitoring parameter details",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMonitoringRuleLogDetails()", null, "Common", "Sql exception while connecting to Parameter's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting monitoring parameter details::",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMonitoringRuleLogDetails()", null, "Common", "Sql exception while connecting to Parameter's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return monitoringRuleLogVOs;
    }
    public MonitoringRuleLogVO getRuleChangeHistoryDetails(String historyId) throws PZDBViolationException
    {
        Connection con = null;
        MonitoringRuleLogVO monitoringRuleLogVO=new  MonitoringRuleLogVO();
        MonitoringParameterMappingVO monitoringParameterMappingVO=null;
        MonitoringParameterVO monitoringParameterVO=null;
        PreparedStatement pstmt = null;
        ResultSet res=null;
        try
        {
            con= Database.getRDBConnection();
            StringBuffer sbQuery = new StringBuffer("SELECT mpld.*,mpm.monitoing_para_name,FROM_UNIXTIME(dtstamp) AS modifiedon FROM monitoring_parameter_log_details AS mpld JOIN monitoring_parameter_master AS mpm ON mpld.monitoring_para_id=mpm.monitoing_para_id WHERE mpld.id =?");
            pstmt=con.prepareStatement(sbQuery.toString());
            pstmt.setString(1,historyId);
            res=pstmt.executeQuery();
            if(res.next())
            {
                monitoringParameterMappingVO=new MonitoringParameterMappingVO();
                monitoringParameterVO=new MonitoringParameterVO();

                monitoringRuleLogVO.setActionExecutor(res.getString("actionExecutor"));
                monitoringRuleLogVO.setModifiedOn(res.getString("modifiedon"));
                monitoringParameterVO.setMonitoringParameterId(res.getInt("monitoring_para_id"));
                monitoringParameterVO.setMonitoringParameterName(res.getString("monitoing_para_name"));

                monitoringParameterMappingVO.setIsAlertToAdminSales(res.getString("isalerttoadmin_sales"));
                monitoringParameterMappingVO.setIsAlertToAdminRF(res.getString("isalerttoadmin_rf"));
                monitoringParameterMappingVO.setIsAlertToAdminCB(res.getString("isalerttoadmin_cb"));
                monitoringParameterMappingVO.setIsAlertToAdminFraud(res.getString("isalerttoadmin_fraud"));
                monitoringParameterMappingVO.setIsAlertToAdminTech(res.getString("isalerttoadmin_tech"));

                monitoringParameterMappingVO.setIsAlertToMerchantSales(res.getString("isalerttomerchant_sales"));
                monitoringParameterMappingVO.setIsAlertToMerchantRF(res.getString("isalerttomerchant_rf"));
                monitoringParameterMappingVO.setIsAlertToMerchantCB(res.getString("isalerttomerchant_cb"));
                monitoringParameterMappingVO.setIsAlertToMerchantFraud(res.getString("isalerttomerchant_fraud"));
                monitoringParameterMappingVO.setIsAlertToMerchantTech(res.getString("isalerttomerchant_tech"));

                monitoringParameterMappingVO.setIsAlertToPartnerSales(res.getString("isalerttopartner_sales"));
                monitoringParameterMappingVO.setIsAlertToPartnerRF(res.getString("isalerttopartner_rf"));
                monitoringParameterMappingVO.setIsAlertToPartnerCB(res.getString("isalerttopartner_cb"));
                monitoringParameterMappingVO.setIsAlertToPartnerFraud(res.getString("isalerttopartner_fraud"));
                monitoringParameterMappingVO.setIsAlertToPartnerTech(res.getString("isalerttopartner_tech"));

                monitoringParameterMappingVO.setIsAlertToAgentSales(res.getString("isalerttoagent_sales"));
                monitoringParameterMappingVO.setIsAlertToAgentRF(res.getString("isalerttoagent_rf"));
                monitoringParameterMappingVO.setIsAlertToAgentCB(res.getString("isalerttoagent_cb"));
                monitoringParameterMappingVO.setIsAlertToAgentFraud(res.getString("isalerttoagent_fraud"));
                monitoringParameterMappingVO.setIsAlertToAgentTech(res.getString("isalerttoagent_tech"));

                monitoringParameterMappingVO.setMappingId(res.getInt("id"));
                monitoringParameterMappingVO.setMemberId(res.getInt("memberid"));
                monitoringParameterMappingVO.setTerminalId(res.getInt("terminalid"));
                monitoringParameterMappingVO.setAlertThreshold(res.getDouble("alert_threshold"));
                monitoringParameterMappingVO.setSuspensionThreshold(res.getDouble("suspension_threshold"));
                monitoringParameterMappingVO.setAlertActivation(res.getString("alert_activation"));
                monitoringParameterMappingVO.setSuspensionActivation(res.getString("suspension_activation"));
                monitoringParameterMappingVO.setIsAlertToAdmin(res.getString("isalerttoadmin"));
                monitoringParameterMappingVO.setIsAlertToMerchant(res.getString("isalerttomerchant"));
                monitoringParameterMappingVO.setIsAlertToPartner(res.getString("isalerttopartner"));
                monitoringParameterMappingVO.setIsAlertToAgent(res.getString("isalerttoagent"));
                monitoringParameterMappingVO.setMonitoringParameterId(res.getInt("monitoring_para_id"));

                monitoringParameterMappingVO.setAlertMessage(res.getString("alertMessage"));

                monitoringParameterMappingVO.setIsDailyExecution(res.getString("isdailyexecution"));
                monitoringParameterMappingVO.setIsWeeklyExecution(res.getString("isweeklyexecution"));
                monitoringParameterMappingVO.setIsMonthlyExecution(res.getString("ismonthlyexecution"));

                monitoringParameterMappingVO.setWeeklyAlertThreshold(res.getDouble("weekly_alert_threshold"));
                monitoringParameterMappingVO.setMonthlyAlertThreshold(res.getDouble("monthly_alert_threshold"));
                monitoringParameterMappingVO.setWeeklySuspensionThreshold(res.getDouble("weekly_suspension_threshold"));
                monitoringParameterMappingVO.setMonthlySuspensionThreshold(res.getDouble("monthly_suspension_threshold"));

                monitoringParameterMappingVO.setMonitoringParameterVO(monitoringParameterVO);
                monitoringRuleLogVO.setMonitoringParameterMappingVO(monitoringParameterMappingVO);

                monitoringRuleLogVO.setHistoryId(res.getString("id"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting monitoring parameter details",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMonitoringRuleLogDetails()", null, "Common", "Sql exception while connecting to Parameter's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting monitoring parameter details::",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMonitoringRuleLogDetails()", null, "Common", "Sql exception while connecting to Parameter's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return monitoringRuleLogVO;
    }
    public MonitoringRuleLogVO getPreviousRuleChangeHistoryDetails(String ruleId,String historyId,String terminalId) throws PZDBViolationException
    {
        Connection con = null;
        MonitoringRuleLogVO monitoringRuleLogVO=new  MonitoringRuleLogVO();
        MonitoringParameterMappingVO monitoringParameterMappingVO=null;
        MonitoringParameterVO monitoringParameterVO=null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        try
        {
            con= Database.getRDBConnection();
            StringBuffer sbQuery = new StringBuffer("SELECT mpld.*,mpm.monitoing_para_name,FROM_UNIXTIME(dtstamp) AS modifiedon FROM monitoring_parameter_log_details AS mpld JOIN monitoring_parameter_master AS mpm ON mpld.monitoring_para_id=mpm.monitoing_para_id WHERE mpld.id =(SELECT MAX(id) FROM monitoring_parameter_log_details WHERE monitoring_para_id=? AND id<? and terminalid=?)");
            pstmt=con.prepareStatement(sbQuery.toString());
            pstmt.setString(1,ruleId);
            pstmt.setString(2,historyId);
            pstmt.setString(3,terminalId);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                monitoringParameterMappingVO=new MonitoringParameterMappingVO();
                monitoringParameterVO=new MonitoringParameterVO();

                monitoringRuleLogVO.setActionExecutor(rs.getString("actionExecutor"));
                monitoringRuleLogVO.setModifiedOn(rs.getString("modifiedon"));
                monitoringParameterVO.setMonitoringParameterId(rs.getInt("monitoring_para_id"));
                monitoringParameterVO.setMonitoringParameterName(rs.getString("monitoing_para_name"));

                monitoringParameterMappingVO.setAlertActivation(rs.getString("alert_activation"));
                monitoringParameterMappingVO.setSuspensionActivation(rs.getString("suspension_activation"));
                monitoringParameterMappingVO.setIsAlertToAdmin(rs.getString("isalerttoadmin"));
                monitoringParameterMappingVO.setIsAlertToMerchant(rs.getString("isalerttomerchant"));
                monitoringParameterMappingVO.setIsAlertToPartner(rs.getString("isalerttopartner"));
                monitoringParameterMappingVO.setIsAlertToAgent(rs.getString("isalerttoagent"));
                monitoringParameterMappingVO.setMonitoringParameterId(rs.getInt("monitoring_para_id"));

                monitoringParameterMappingVO.setIsAlertToAdminSales(rs.getString("isalerttoadmin_sales"));
                monitoringParameterMappingVO.setIsAlertToAdminRF(rs.getString("isalerttoadmin_rf"));
                monitoringParameterMappingVO.setIsAlertToAdminCB(rs.getString("isalerttoadmin_cb"));
                monitoringParameterMappingVO.setIsAlertToAdminFraud(rs.getString("isalerttoadmin_fraud"));
                monitoringParameterMappingVO.setIsAlertToAdminTech(rs.getString("isalerttoadmin_tech"));

                monitoringParameterMappingVO.setIsAlertToMerchantSales(rs.getString("isalerttomerchant_sales"));
                monitoringParameterMappingVO.setIsAlertToMerchantRF(rs.getString("isalerttomerchant_rf"));
                monitoringParameterMappingVO.setIsAlertToMerchantCB(rs.getString("isalerttomerchant_cb"));
                monitoringParameterMappingVO.setIsAlertToMerchantFraud(rs.getString("isalerttomerchant_fraud"));
                monitoringParameterMappingVO.setIsAlertToMerchantTech(rs.getString("isalerttomerchant_tech"));

                monitoringParameterMappingVO.setIsAlertToPartnerSales(rs.getString("isalerttopartner_sales"));
                monitoringParameterMappingVO.setIsAlertToPartnerRF(rs.getString("isalerttopartner_rf"));
                monitoringParameterMappingVO.setIsAlertToPartnerCB(rs.getString("isalerttopartner_cb"));
                monitoringParameterMappingVO.setIsAlertToPartnerFraud(rs.getString("isalerttopartner_fraud"));
                monitoringParameterMappingVO.setIsAlertToPartnerTech(rs.getString("isalerttopartner_tech"));

                monitoringParameterMappingVO.setIsAlertToAgentSales(rs.getString("isalerttoagent_sales"));
                monitoringParameterMappingVO.setIsAlertToAgentRF(rs.getString("isalerttoagent_rf"));
                monitoringParameterMappingVO.setIsAlertToAgentCB(rs.getString("isalerttoagent_cb"));
                monitoringParameterMappingVO.setIsAlertToAgentFraud(rs.getString("isalerttoagent_fraud"));
                monitoringParameterMappingVO.setIsAlertToAgentTech(rs.getString("isalerttoagent_tech"));

                monitoringParameterMappingVO.setMappingId(rs.getInt("id"));
                monitoringParameterMappingVO.setMemberId(rs.getInt("memberid"));
                monitoringParameterMappingVO.setTerminalId(rs.getInt("terminalid"));

                monitoringParameterMappingVO.setAlertThreshold(rs.getDouble("alert_threshold"));
                monitoringParameterMappingVO.setWeeklyAlertThreshold(rs.getDouble("weekly_alert_threshold"));
                monitoringParameterMappingVO.setMonthlyAlertThreshold(rs.getDouble("monthly_alert_threshold"));

                monitoringParameterMappingVO.setSuspensionThreshold(rs.getDouble("suspension_threshold"));
                monitoringParameterMappingVO.setWeeklySuspensionThreshold(rs.getDouble("weekly_suspension_threshold"));
                monitoringParameterMappingVO.setMonthlySuspensionThreshold(rs.getDouble("monthly_suspension_threshold"));
                monitoringParameterMappingVO.setAlertMessage(rs.getString("alertMessage"));

                monitoringParameterMappingVO.setIsDailyExecution(rs.getString("isdailyexecution"));
                monitoringParameterMappingVO.setIsWeeklyExecution(rs.getString("isweeklyexecution"));
                monitoringParameterMappingVO.setIsMonthlyExecution(rs.getString("ismonthlyexecution"));

                monitoringParameterMappingVO.setMonitoringParameterVO(monitoringParameterVO);
                monitoringRuleLogVO.setMonitoringParameterMappingVO(monitoringParameterMappingVO);

                monitoringRuleLogVO.setHistoryId(rs.getString("id"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting monitoring parameter details",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMonitoringRuleLogDetails()", null, "Common", "Sql exception while connecting to Parameter's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting monitoring parameter details::",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMonitoringRuleLogDetails()", null, "Common", "Sql exception while connecting to Parameter's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return monitoringRuleLogVO;
    }
    public MonitoringRuleLogVO getPreviousRuleChangeHistoryDetails(String ruleId) throws PZDBViolationException
    {
        MonitoringRuleLogVO monitoringRuleLogVO=new  MonitoringRuleLogVO();
        MonitoringParameterMappingVO monitoringParameterMappingVO = null;
        MonitoringParameterVO monitoringParameterVO = null;
        TerminalVO terminalVO = null;
        Connection conn = null;
        PreparedStatement pstmt2=null;
        ResultSet rs2=null;
        try
        {
            conn = Database.getRDBConnection();
            String query2 = "SELECT * from monitoring_parameter_master where monitoing_para_id=?";
            pstmt2 = conn.prepareStatement(query2);
            pstmt2.setString(1,ruleId);
            rs2 = pstmt2.executeQuery();
            if (rs2.next())
            {
                monitoringParameterMappingVO = new MonitoringParameterMappingVO();
                monitoringParameterVO = new MonitoringParameterVO();

                monitoringParameterVO.setMonitoringParameterName(rs2.getString("monitoing_para_name"));
                monitoringParameterVO.setMonitoingParaTechName(rs2.getString("monitoing_para_tech_name"));
                monitoringParameterVO.setMonitoingCategory(rs2.getString("monitoing_category"));
                monitoringParameterVO.setMonitoingKeyword(rs2.getString("monitoing_keyword"));
                monitoringParameterVO.setMonitoingSubKeyword(rs2.getString("monitoing_subkeyword"));
                monitoringParameterVO.setMonitoingAlertCategory(rs2.getString("monitoing_alert_category"));
                monitoringParameterVO.setMonitoingOnChannel(rs2.getString("monitoing_onchannel"));
                monitoringParameterVO.setMonitoringUnit(rs2.getString("monitoring_unit"));
                monitoringParameterVO.setMonitoringParameterId(Integer.parseInt(rs2.getString("monitoing_para_id")));
                monitoringParameterVO.setDefaultAlertMsg(rs2.getString("default_alertMsg").trim());

                monitoringParameterMappingVO.setAlertThreshold(rs2.getDouble("default_alert_threshold"));
                monitoringParameterMappingVO.setWeeklyAlertThreshold(rs2.getDouble("weekly_alert_threshold"));
                monitoringParameterMappingVO.setMonthlyAlertThreshold(rs2.getDouble("monthly_alert_threshold"));

                monitoringParameterMappingVO.setSuspensionThreshold(rs2.getDouble("default_suspension_threshold"));
                monitoringParameterMappingVO.setWeeklySuspensionThreshold(rs2.getDouble("weekly_suspension_threshold"));
                monitoringParameterMappingVO.setMonthlySuspensionThreshold(rs2.getDouble("monthly_suspension_threshold"));

                monitoringParameterMappingVO.setAlertActivation(rs2.getString("default_alert_activation"));
                monitoringParameterMappingVO.setSuspensionActivation(rs2.getString("default_suspension_activation"));
                monitoringParameterMappingVO.setIsAlertToAdmin(rs2.getString("default_isAlertToAdmin"));
                monitoringParameterMappingVO.setIsAlertToMerchant(rs2.getString("default_isAlertToMerchant"));
                monitoringParameterMappingVO.setIsAlertToPartner(rs2.getString("default_isAlertToPartner"));
                monitoringParameterMappingVO.setIsAlertToAgent(rs2.getString("default_isAlertToAgent"));
                monitoringParameterMappingVO.setMonitoringParameterId(rs2.getInt("monitoing_para_id"));

                monitoringParameterMappingVO.setIsAlertToAdminSales(rs2.getString("isalerttoadmin_sales"));
                monitoringParameterMappingVO.setIsAlertToAdminRF(rs2.getString("isalerttoadmin_rf"));
                monitoringParameterMappingVO.setIsAlertToAdminCB(rs2.getString("isalerttoadmin_cb"));
                monitoringParameterMappingVO.setIsAlertToAdminFraud(rs2.getString("isalerttoadmin_fraud"));
                monitoringParameterMappingVO.setIsAlertToAdminTech(rs2.getString("isalerttoadmin_tech"));

                monitoringParameterMappingVO.setIsAlertToMerchantSales(rs2.getString("isalerttomerchant_sales"));
                monitoringParameterMappingVO.setIsAlertToMerchantRF(rs2.getString("isalerttomerchant_rf"));
                monitoringParameterMappingVO.setIsAlertToMerchantCB(rs2.getString("isalerttomerchant_cb"));
                monitoringParameterMappingVO.setIsAlertToMerchantFraud(rs2.getString("isalerttomerchant_fraud"));
                monitoringParameterMappingVO.setIsAlertToMerchantTech(rs2.getString("isalerttomerchant_tech"));

                monitoringParameterMappingVO.setIsAlertToPartnerSales(rs2.getString("isalerttopartner_sales"));
                monitoringParameterMappingVO.setIsAlertToPartnerRF(rs2.getString("isalerttopartner_rf"));
                monitoringParameterMappingVO.setIsAlertToPartnerCB(rs2.getString("isalerttopartner_cb"));
                monitoringParameterMappingVO.setIsAlertToPartnerFraud(rs2.getString("isalerttopartner_fraud"));
                monitoringParameterMappingVO.setIsAlertToPartnerTech(rs2.getString("isalerttopartner_tech"));

                monitoringParameterMappingVO.setIsAlertToAgentSales(rs2.getString("isalerttoagent_sales"));
                monitoringParameterMappingVO.setIsAlertToAgentRF(rs2.getString("isalerttoagent_rf"));
                monitoringParameterMappingVO.setIsAlertToAgentCB(rs2.getString("isalerttoagent_cb"));
                monitoringParameterMappingVO.setIsAlertToAgentFraud(rs2.getString("isalerttoagent_fraud"));
                monitoringParameterMappingVO.setIsAlertToAgentTech(rs2.getString("isalerttoagent_tech"));

                monitoringParameterMappingVO.setIsDailyExecution(rs2.getString("isdailyexecution"));
                monitoringParameterMappingVO.setIsWeeklyExecution(rs2.getString("isweeklyexecution"));
                monitoringParameterMappingVO.setIsMonthlyExecution(rs2.getString("ismonthlyexecution"));

                monitoringParameterMappingVO.setMonitoringParameterVO(monitoringParameterVO);
                monitoringRuleLogVO.setMonitoringParameterMappingVO(monitoringParameterMappingVO);
            }
            else
            {
                logger.debug("Need to be pick rule from master");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing System Exception as SystemError :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getPreviousRuleChangeHistoryDetails()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing SQL Exception as SystemError :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getPreviousRuleChangeHistoryDetails()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as Exception :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getPreviousRuleChangeHistoryDetails()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs2);
            Database.closePreparedStatement(pstmt2);
            Database.closeConnection(conn);
        }
        return monitoringRuleLogVO;
    }

    public boolean updateMonitoringParameter(MonitoringParameterVO monitoringParameterVO)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        int k=0;
        boolean status=false;
        try
        {
            con = Database.getConnection();
            StringBuffer query = new StringBuffer("update monitoring_parameter_master set monitoing_para_name=?,monitoing_para_tech_name=?,monitoing_category=?,monitoring_deviation=?" +
                    ",monitoing_keyword=?,monitoing_subkeyword=?,monitoing_alert_category=?,monitoing_onchannel=?,monitoring_unit=?,default_alert_threshold=?,default_suspension_threshold=?,default_isAlertToAdmin=?,default_isAlertToMerchant=?,default_isAlertToPartner=?,default_isAlertToAgent=?,default_alert_activation=?,default_suspension_activation=?,default_alertMsg=?,isalerttoadmin_sales=?,isalerttoadmin_rf=?,isalerttoadmin_cb=?,isalerttoadmin_fraud=?,isalerttoadmin_tech=?,isalerttomerchant_sales=?,isalerttomerchant_rf=?,isalerttomerchant_cb=?,isalerttomerchant_fraud=?,isalerttomerchant_tech=?,isalerttopartner_sales=?,isalerttopartner_rf=?,isalerttopartner_cb=?,isalerttopartner_fraud=?,isalerttopartner_tech=?,isalerttoagent_sales=?,isalerttoagent_rf=?,isalerttoagent_cb=?,isalerttoagent_fraud=?,isalerttoagent_tech=?,isdailyexecution=?,isweeklyexecution=?,ismonthlyexecution=?, weekly_alert_threshold=?, weekly_suspension_threshold=?, monthly_alert_threshold=?, monthly_suspension_threshold=?, displayChartType=? where monitoing_para_id= ?");
            pstmt = con.prepareStatement(query.toString());
            pstmt.setString(1,monitoringParameterVO.getMonitoringParameterName());
            pstmt.setString(2,monitoringParameterVO.getMonitoingParaTechName());
            pstmt.setString(3,monitoringParameterVO.getMonitoingCategory());
            pstmt.setString(4,monitoringParameterVO.getMonitoingDeviation());
            pstmt.setString(5,monitoringParameterVO.getMonitoingKeyword());
            pstmt.setString(6,monitoringParameterVO.getMonitoingSubKeyword());
            pstmt.setString(7,monitoringParameterVO.getMonitoingAlertCategory());
            pstmt.setString(8,monitoringParameterVO.getMonitoingOnChannel());
            pstmt.setString(9,monitoringParameterVO.getMonitoringUnit());
            pstmt.setDouble(10, monitoringParameterVO.getDefaultAlertThreshold());
            pstmt.setDouble(11,monitoringParameterVO.getDefaultSuspensionThreshold());
            pstmt.setString(12, monitoringParameterVO.getDefaultIsAlertToAdmin());
            pstmt.setString(13,monitoringParameterVO.getDefaultIsAlertToMerchant());
            pstmt.setString(14,monitoringParameterVO.getDefaultIsAlertToPartner());
            pstmt.setString(15,monitoringParameterVO.getDefaultIsAlertToAgent());
            pstmt.setString(16,monitoringParameterVO.getDefaultAlertActivation());
            pstmt.setString(17,monitoringParameterVO.getDefaultSuspensionActivation());
            pstmt.setString(18,monitoringParameterVO.getDefaultAlertMsg());
            pstmt.setString(19,monitoringParameterVO.getDefaultIsAlertToAdminSales());
            pstmt.setString(20,monitoringParameterVO.getDefaultIsAlertToAdminRF());
            pstmt.setString(21,monitoringParameterVO.getDefaultIsAlertToAdminCB());
            pstmt.setString(22,monitoringParameterVO.getDefaultIsAlertToAdminFraud());
            pstmt.setString(23,monitoringParameterVO.getDefaultIsAlertToAdminTech());
            pstmt.setString(24,monitoringParameterVO.getDefaultIsAlertToMerchantSales());
            pstmt.setString(25,monitoringParameterVO.getDefaultIsAlertToMerchantRF());
            pstmt.setString(26,monitoringParameterVO.getDefaultIsAlertToMerchantCB());
            pstmt.setString(27,monitoringParameterVO.getDefaultIsAlertToMerchantFraud());
            pstmt.setString(28,monitoringParameterVO.getDefaultIsAlertToMerchantTech());
            pstmt.setString(29,monitoringParameterVO.getDefaultIsAlertToPartnerSales());
            pstmt.setString(30,monitoringParameterVO.getDefaultIsAlertToPartnerRF());
            pstmt.setString(31,monitoringParameterVO.getDefaultIsAlertToPartnerCB());
            pstmt.setString(32,monitoringParameterVO.getDefaultIsAlertToPartnerFraud());
            pstmt.setString(33,monitoringParameterVO.getDefaultIsAlertToPartnerTech());
            pstmt.setString(34,monitoringParameterVO.getDefaultIsAlertToAgentSales());
            pstmt.setString(35,monitoringParameterVO.getDefaultIsAlertToAgentRF());
            pstmt.setString(36,monitoringParameterVO.getDefaultIsAlertToAgentCB());
            pstmt.setString(37,monitoringParameterVO.getDefaultIsAlertToAgentFraud());
            pstmt.setString(38,monitoringParameterVO.getDefaultIsAlertToAgentTech());
            pstmt.setString(39,monitoringParameterVO.getIsDailyExecution());
            pstmt.setString(40,monitoringParameterVO.getIsWeeklyExecution());
            pstmt.setString(41,monitoringParameterVO.getIsMonthlyExecution());
            pstmt.setDouble(42, monitoringParameterVO.getWeeklyAlertThreshold());
            pstmt.setDouble(43,monitoringParameterVO.getWeeklySuspensionThreshold());
            pstmt.setDouble(44,monitoringParameterVO.getMonthlyAlertThreshold());
            pstmt.setDouble(45,monitoringParameterVO.getMonthlySuspensionThreshold());
            pstmt.setString(46,monitoringParameterVO.getDisplayChartType());
            pstmt.setInt(47,monitoringParameterVO.getMonitoringParameterId());

            k=pstmt.executeUpdate();
            if(k>0)
            {
                status=true;
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::::::",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "updateMonitoringParameter()", null, "Common", "Sql exception while connecting to Parameter's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::::::::::",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "updateMonitoringParameter()", null, "Common", "Sql exception while connecting to Parameter's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return status;
    }

    public Map<String, Map<String, List<MonitoringParameterMappingVO>>> getMonitoringParameterGroupByMerchantNew(MonitoringFrequency monitoringFrequency) throws PZDBViolationException
    {
        Map<String, Map<String, List<MonitoringParameterMappingVO>>> stringListMapMerchantLevel = new HashMap();
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT memberid,COUNT(*) FROM member_account_mapping  WHERE riskruleactivation='Y' and isActive='Y' GROUP BY memberid";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                String memberId = rs.getString("memberid");
                Map<String, List<MonitoringParameterMappingVO>> stringListMap = new HashMap();
                List<MonitoringParameterMappingVO> monitoringParameterMappingVOs = null;
                MonitoringParameterMappingVO monitoringParameterMappingVO = null;
                MonitoringParameterVO monitoringParameterVO = null;
                TerminalVO terminalVO = null;

                String query1 = "SELECT terminalid FROM member_account_mapping  WHERE riskruleactivation='Y' and isActive='Y' and memberid=?";
                PreparedStatement pstm1 = conn.prepareStatement(query1);
                pstm1.setString(1, memberId);
                ResultSet resNew = pstm1.executeQuery();
                while (resNew.next())
                {
                    String terminalId = resNew.getString("terminalid");
                    query = "SELECT mpm.monitoing_para_id,mpm.monitoing_para_name,mpm.monitoing_para_tech_name,mpm.monitoing_category,mpm.monitoing_keyword,mpm.monitoing_subkeyword,mpm.monitoing_alert_category,mpm.monitoing_alert_category,mpm.monitoing_frequency,mpm.monitoing_onchannel,mpm.monitoring_deviation,mpm.monitoring_unit,mamm.mappingid,mamm.memberid,mamm.terminalid,alert_threshold,suspension_threshold,alert_activation,suspension_activation,isalerttoadmin,isalerttomerchant,isalerttopartner,isalerttoagent,mpm.monitoing_para_id,mam.accountid,mam.paymodeid,mam.cardtypeid,mamm.alertMessage,mamm.isalerttoadmin_sales,mamm.isalerttoadmin_rf,mamm.isalerttoadmin_cb,mamm.isalerttoadmin_fraud,mamm.isalerttoadmin_tech,mamm.isalerttomerchant_sales,mamm.isalerttomerchant_rf,mamm.isalerttomerchant_cb,mamm.isalerttomerchant_fraud,mamm.isalerttomerchant_tech,mamm.isalerttopartner_sales,mamm.isalerttopartner_rf,mamm.isalerttopartner_cb,mamm.isalerttopartner_fraud,mamm.isalerttopartner_tech,mamm.isalerttoagent_sales,mamm.isalerttoagent_rf,mamm.isalerttoagent_cb,mamm.isalerttoagent_fraud,mamm.isalerttoagent_tech FROM member_account_monitoringpara_mapping AS mamm JOIN monitoring_parameter_master AS mpm ON mamm.monitoing_para_id=mpm.monitoing_para_id JOIN member_account_mapping AS mam ON mamm.terminalid=mam.terminalid WHERE mamm.terminalid=? and mamm.mapping_frequency=?";
                    pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, terminalId);
                    pstmt.setString(2, monitoringFrequency.toString());
                    ResultSet res = pstmt.executeQuery();
                    if (res.next())
                    {
                        monitoringParameterMappingVOs = new ArrayList();
                        do
                        {
                            monitoringParameterMappingVO = new MonitoringParameterMappingVO();
                            monitoringParameterVO = new MonitoringParameterVO();
                            terminalVO = new TerminalVO();

                            terminalVO.setMemberId(res.getString("memberid"));
                            terminalVO.setTerminalId(res.getString("terminalid"));
                            terminalVO.setAccountId(res.getString("accountid"));
                            terminalVO.setPaymodeId(res.getString("paymodeid"));
                            terminalVO.setCardTypeId(res.getString("cardtypeid"));

                            monitoringParameterVO.setMonitoringParameterName(res.getString("monitoing_para_name"));
                            monitoringParameterVO.setMonitoingParaTechName(res.getString("monitoing_para_tech_name"));
                            monitoringParameterVO.setMonitoingCategory(res.getString("monitoing_category"));
                            monitoringParameterVO.setMonitoingKeyword(res.getString("monitoing_keyword"));
                            monitoringParameterVO.setMonitoingSubKeyword(res.getString("monitoing_subkeyword"));
                            monitoringParameterVO.setMonitoingAlertCategory(res.getString("monitoing_alert_category"));
                            monitoringParameterVO.setMonitoingFrequency(res.getString("monitoing_frequency"));
                            monitoringParameterVO.setMonitoingOnChannel(res.getString("monitoing_onchannel"));
                            monitoringParameterVO.setMonitoingDeviation(res.getString("monitoring_deviation"));
                            monitoringParameterVO.setMonitoringUnit(res.getString("monitoring_unit"));

                            monitoringParameterMappingVO.setIsAlertToAdminSales(res.getString("isalerttoadmin_sales"));
                            monitoringParameterMappingVO.setIsAlertToAdminRF(res.getString("isalerttoadmin_rf"));
                            monitoringParameterMappingVO.setIsAlertToAdminCB(res.getString("isalerttoadmin_cb"));
                            monitoringParameterMappingVO.setIsAlertToAdminFraud(res.getString("isalerttoadmin_fraud"));
                            monitoringParameterMappingVO.setIsAlertToAdminTech(res.getString("isalerttoadmin_tech"));

                            monitoringParameterMappingVO.setIsAlertToMerchantSales(res.getString("isalerttomerchant_sales"));
                            monitoringParameterMappingVO.setIsAlertToMerchantRF(res.getString("isalerttomerchant_rf"));
                            monitoringParameterMappingVO.setIsAlertToMerchantCB(res.getString("isalerttomerchant_cb"));
                            monitoringParameterMappingVO.setIsAlertToMerchantFraud(res.getString("isalerttomerchant_fraud"));
                            monitoringParameterMappingVO.setIsAlertToMerchantTech(res.getString("isalerttomerchant_tech"));

                            monitoringParameterMappingVO.setIsAlertToPartnerSales(res.getString("isalerttopartner_sales"));
                            monitoringParameterMappingVO.setIsAlertToPartnerRF(res.getString("isalerttopartner_rf"));
                            monitoringParameterMappingVO.setIsAlertToPartnerCB(res.getString("isalerttopartner_cb"));
                            monitoringParameterMappingVO.setIsAlertToPartnerFraud(res.getString("isalerttopartner_fraud"));
                            monitoringParameterMappingVO.setIsAlertToPartnerTech(res.getString("isalerttopartner_tech"));

                            monitoringParameterMappingVO.setIsAlertToAgentSales(res.getString("isalerttoagent_sales"));
                            monitoringParameterMappingVO.setIsAlertToAgentRF(res.getString("isalerttoagent_rf"));
                            monitoringParameterMappingVO.setIsAlertToAgentCB(res.getString("isalerttoagent_cb"));
                            monitoringParameterMappingVO.setIsAlertToAgentFraud(res.getString("isalerttoagent_fraud"));
                            monitoringParameterMappingVO.setIsAlertToAgentTech(res.getString("isalerttoagent_tech"));

                            monitoringParameterMappingVO.setMappingId(res.getInt("mappingid"));
                            monitoringParameterMappingVO.setMemberId(res.getInt("memberid"));
                            monitoringParameterMappingVO.setTerminalId(res.getInt("terminalid"));
                            monitoringParameterMappingVO.setAlertThreshold(res.getDouble("alert_threshold"));
                            monitoringParameterMappingVO.setSuspensionThreshold(res.getDouble("suspension_threshold"));
                            monitoringParameterMappingVO.setAlertActivation(res.getString("alert_activation"));
                            monitoringParameterMappingVO.setSuspensionActivation(res.getString("suspension_activation"));
                            monitoringParameterMappingVO.setIsAlertToAdmin(res.getString("isalerttoadmin"));
                            monitoringParameterMappingVO.setIsAlertToMerchant(res.getString("isalerttomerchant"));
                            monitoringParameterMappingVO.setIsAlertToPartner(res.getString("isalerttopartner"));
                            monitoringParameterMappingVO.setIsAlertToAgent(res.getString("isalerttoagent"));
                            monitoringParameterMappingVO.setMonitoringParameterId(res.getInt("monitoing_para_id"));

                            monitoringParameterMappingVO.setAlertMessage(res.getString("alertMessage"));

                            monitoringParameterMappingVO.setMonitoringParameterVO(monitoringParameterVO);
                            monitoringParameterMappingVO.setTerminalVO(terminalVO);
                            monitoringParameterMappingVOs.add(monitoringParameterMappingVO);

                        }
                        while (res.next());
                        stringListMap.put(terminalId, monitoringParameterMappingVOs);
                    }
                    else
                    {
                        String query2 = "SELECT * from monitoring_parameter_master where  monitoing_frequency=?";
                        PreparedStatement pstmt2 = conn.prepareStatement(query2);
                        pstmt2.setString(1, monitoringFrequency.toString());
                        ResultSet rs2 = pstmt2.executeQuery();
                        if (rs2.next())
                        {
                            monitoringParameterMappingVOs = new ArrayList();
                            do
                            {
                                monitoringParameterMappingVO = new MonitoringParameterMappingVO();
                                monitoringParameterVO = new MonitoringParameterVO();
                                terminalVO = new TerminalVO();

                                terminalVO.setMemberId(memberId);
                                terminalVO.setTerminalId(terminalId);

                                monitoringParameterVO.setMonitoringParameterName(rs2.getString("monitoing_para_name"));
                                monitoringParameterVO.setMonitoingParaTechName(rs2.getString("monitoing_para_tech_name"));
                                monitoringParameterVO.setMonitoingCategory(rs2.getString("monitoing_category"));
                                monitoringParameterVO.setMonitoingKeyword(rs2.getString("monitoing_keyword"));
                                monitoringParameterVO.setMonitoingSubKeyword(rs2.getString("monitoing_subkeyword"));
                                monitoringParameterVO.setMonitoingAlertCategory(rs2.getString("monitoing_alert_category"));
                                monitoringParameterVO.setMonitoingFrequency(rs2.getString("monitoing_frequency"));
                                monitoringParameterVO.setMonitoingOnChannel(rs2.getString("monitoing_onchannel"));
                                monitoringParameterVO.setMonitoingDeviation(rs2.getString("monitoring_deviation"));
                                monitoringParameterVO.setMonitoringUnit(rs2.getString("monitoring_unit"));

                                monitoringParameterMappingVO.setIsAlertToAdminSales(rs2.getString("isalerttoadmin_sales"));
                                monitoringParameterMappingVO.setIsAlertToAdminRF(rs2.getString("isalerttoadmin_rf"));
                                monitoringParameterMappingVO.setIsAlertToAdminCB(rs2.getString("isalerttoadmin_cb"));
                                monitoringParameterMappingVO.setIsAlertToAdminFraud(rs2.getString("isalerttoadmin_fraud"));
                                monitoringParameterMappingVO.setIsAlertToAdminTech(rs2.getString("isalerttoadmin_tech"));

                                monitoringParameterMappingVO.setIsAlertToMerchantSales(rs2.getString("isalerttomerchant_sales"));
                                monitoringParameterMappingVO.setIsAlertToMerchantRF(rs2.getString("isalerttomerchant_rf"));
                                monitoringParameterMappingVO.setIsAlertToMerchantCB(rs2.getString("isalerttomerchant_cb"));
                                monitoringParameterMappingVO.setIsAlertToMerchantFraud(rs2.getString("isalerttomerchant_fraud"));
                                monitoringParameterMappingVO.setIsAlertToMerchantTech(rs2.getString("isalerttomerchant_tech"));

                                monitoringParameterMappingVO.setIsAlertToPartnerSales(rs2.getString("isalerttopartner_sales"));
                                monitoringParameterMappingVO.setIsAlertToPartnerRF(rs2.getString("isalerttopartner_rf"));
                                monitoringParameterMappingVO.setIsAlertToPartnerCB(rs2.getString("isalerttopartner_cb"));
                                monitoringParameterMappingVO.setIsAlertToPartnerFraud(rs2.getString("isalerttopartner_fraud"));
                                monitoringParameterMappingVO.setIsAlertToPartnerTech(rs2.getString("isalerttopartner_tech"));

                                monitoringParameterMappingVO.setIsAlertToAgentSales(rs2.getString("isalerttoagent_sales"));
                                monitoringParameterMappingVO.setIsAlertToAgentRF(rs2.getString("isalerttoagent_rf"));
                                monitoringParameterMappingVO.setIsAlertToAgentCB(rs2.getString("isalerttoagent_cb"));
                                monitoringParameterMappingVO.setIsAlertToAgentFraud(rs2.getString("isalerttoagent_fraud"));
                                monitoringParameterMappingVO.setIsAlertToAgentTech(rs2.getString("isalerttoagent_tech"));

                                monitoringParameterMappingVO.setMemberId(Integer.parseInt(memberId));
                                monitoringParameterMappingVO.setTerminalId(Integer.parseInt(terminalId));
                                monitoringParameterMappingVO.setAlertThreshold(rs2.getDouble("default_alert_threshold"));
                                monitoringParameterMappingVO.setSuspensionThreshold(rs2.getDouble("default_suspension_threshold"));
                                monitoringParameterMappingVO.setAlertActivation(rs2.getString("default_alert_activation"));
                                monitoringParameterMappingVO.setSuspensionActivation(rs2.getString("default_suspension_activation"));
                                monitoringParameterMappingVO.setIsAlertToAdmin(rs2.getString("default_isAlertToAdmin"));
                                monitoringParameterMappingVO.setIsAlertToMerchant(rs2.getString("default_isAlertToMerchant"));
                                monitoringParameterMappingVO.setIsAlertToPartner(rs2.getString("default_isAlertToPartner"));
                                monitoringParameterMappingVO.setIsAlertToAgent(rs2.getString("default_isAlertToAgent"));
                                monitoringParameterMappingVO.setMonitoringParameterId(rs2.getInt("monitoing_para_id"));
                                monitoringParameterMappingVO.setAlertMessage(rs2.getString("default_alertMsg"));

                                monitoringParameterMappingVO.setMonitoringParameterVO(monitoringParameterVO);
                                monitoringParameterMappingVO.setTerminalVO(terminalVO);
                                monitoringParameterMappingVOs.add(monitoringParameterMappingVO);
                            }
                            while (rs2.next());
                            stringListMap.put(terminalId, monitoringParameterMappingVOs);
                        }

                    }
                }
                stringListMapMerchantLevel.put(memberId, stringListMap);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing System Exception as SystemError :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getMonitoringParameterGroupByMerchantNew()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing SQL Exception as SystemError :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getMonitoringParameterGroupByMerchantNew()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as Exception :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getMonitoringParameterGroupByMerchantNew()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return stringListMapMerchantLevel;
    }

    public Map<String, Map<String, List<MonitoringParameterMappingVO>>> getMonitoringParameterGroupByMerchantDailyExecution() throws PZDBViolationException
    {
        Map<String, Map<String, List<MonitoringParameterMappingVO>>> stringListMapMerchantLevel = new HashMap();
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT memberid,COUNT(*) FROM member_account_mapping  WHERE riskruleactivation='Y' and isActive='Y' GROUP BY memberid";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                String memberId = rs.getString("memberid");
                Map<String, List<MonitoringParameterMappingVO>> stringListMap = new HashMap();
                List<MonitoringParameterMappingVO> monitoringParameterMappingVOs = null;
                MonitoringParameterMappingVO monitoringParameterMappingVO = null;
                MonitoringParameterVO monitoringParameterVO = null;
                TerminalVO terminalVO = null;

                String query1 = "SELECT terminalid FROM member_account_mapping  WHERE riskruleactivation='Y' and isActive='Y' and memberid=?";
                PreparedStatement pstm1 = conn.prepareStatement(query1);
                pstm1.setString(1, memberId);
                ResultSet resNew = pstm1.executeQuery();
                while (resNew.next())
                {
                    String terminalId = resNew.getString("terminalid");
                    query = "SELECT monitoing_para_id FROM member_account_monitoringpara_mapping WHERE terminalid=?";
                    pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, terminalId);
                    ResultSet res1 = pstmt.executeQuery();
                    if (res1.next())
                    {
                        query = "SELECT mpm.monitoing_para_id,mpm.monitoing_para_name,mpm.monitoing_para_tech_name,mpm.monitoing_category,mpm.monitoing_keyword,mpm.monitoing_subkeyword,mpm.monitoing_alert_category,mpm.monitoing_alert_category,mpm.monitoing_frequency,mpm.monitoing_onchannel,mpm.monitoring_deviation,mpm.monitoring_unit,mamm.mappingid,mamm.memberid,mamm.terminalid,alert_threshold,mamm.weekly_alert_threshold,mamm.monthly_alert_threshold,suspension_threshold,mamm.weekly_suspension_threshold,mamm.monthly_suspension_threshold,alert_activation,suspension_activation,isalerttoadmin,isalerttomerchant,isalerttopartner,isalerttoagent,mpm.monitoing_para_id,mam.accountid,mam.paymodeid,mam.cardtypeid,mamm.alertMessage,mamm.isalerttoadmin_sales,mamm.isalerttoadmin_rf,mamm.isalerttoadmin_cb,mamm.isalerttoadmin_fraud,mamm.isalerttoadmin_tech,mamm.isalerttomerchant_sales,mamm.isalerttomerchant_rf,mamm.isalerttomerchant_cb,mamm.isalerttomerchant_fraud,mamm.isalerttomerchant_tech,mamm.isalerttopartner_sales,mamm.isalerttopartner_rf,mamm.isalerttopartner_cb,mamm.isalerttopartner_fraud,mamm.isalerttopartner_tech,mamm.isalerttoagent_sales,mamm.isalerttoagent_rf,mamm.isalerttoagent_cb,mamm.isalerttoagent_fraud,mamm.isalerttoagent_tech FROM member_account_monitoringpara_mapping AS mamm JOIN monitoring_parameter_master AS mpm ON mamm.monitoing_para_id=mpm.monitoing_para_id JOIN member_account_mapping AS mam ON mamm.terminalid=mam.terminalid WHERE mamm.terminalid=? and mamm.isdailyexecution=?";
                        pstmt = conn.prepareStatement(query);
                        pstmt.setString(1, terminalId);
                        pstmt.setString(2, "Y");
                        ResultSet res = pstmt.executeQuery();
                        if (res.next())
                        {
                            monitoringParameterMappingVOs = new ArrayList();
                            do
                            {
                                monitoringParameterMappingVO = new MonitoringParameterMappingVO();
                                monitoringParameterVO = new MonitoringParameterVO();
                                terminalVO = new TerminalVO();

                                terminalVO.setMemberId(res.getString("memberid"));
                                terminalVO.setTerminalId(res.getString("terminalid"));
                                terminalVO.setAccountId(res.getString("accountid"));
                                terminalVO.setPaymodeId(res.getString("paymodeid"));
                                terminalVO.setCardTypeId(res.getString("cardtypeid"));

                                monitoringParameterVO.setMonitoringParameterName(res.getString("monitoing_para_name"));
                                monitoringParameterVO.setMonitoingParaTechName(res.getString("monitoing_para_tech_name"));
                                monitoringParameterVO.setMonitoingCategory(res.getString("monitoing_category"));
                                monitoringParameterVO.setMonitoingKeyword(res.getString("monitoing_keyword"));
                                monitoringParameterVO.setMonitoingSubKeyword(res.getString("monitoing_subkeyword"));
                                monitoringParameterVO.setMonitoingAlertCategory(res.getString("monitoing_alert_category"));
                                monitoringParameterVO.setMonitoingFrequency(res.getString("monitoing_frequency"));
                                monitoringParameterVO.setMonitoingOnChannel(res.getString("monitoing_onchannel"));
                                monitoringParameterVO.setMonitoingDeviation(res.getString("monitoring_deviation"));
                                monitoringParameterVO.setMonitoringUnit(res.getString("monitoring_unit"));

                                monitoringParameterMappingVO.setIsAlertToAdminSales(res.getString("isalerttoadmin_sales"));
                                monitoringParameterMappingVO.setIsAlertToAdminRF(res.getString("isalerttoadmin_rf"));
                                monitoringParameterMappingVO.setIsAlertToAdminCB(res.getString("isalerttoadmin_cb"));
                                monitoringParameterMappingVO.setIsAlertToAdminFraud(res.getString("isalerttoadmin_fraud"));
                                monitoringParameterMappingVO.setIsAlertToAdminTech(res.getString("isalerttoadmin_tech"));

                                monitoringParameterMappingVO.setIsAlertToMerchantSales(res.getString("isalerttomerchant_sales"));
                                monitoringParameterMappingVO.setIsAlertToMerchantRF(res.getString("isalerttomerchant_rf"));
                                monitoringParameterMappingVO.setIsAlertToMerchantCB(res.getString("isalerttomerchant_cb"));
                                monitoringParameterMappingVO.setIsAlertToMerchantFraud(res.getString("isalerttomerchant_fraud"));
                                monitoringParameterMappingVO.setIsAlertToMerchantTech(res.getString("isalerttomerchant_tech"));

                                monitoringParameterMappingVO.setIsAlertToPartnerSales(res.getString("isalerttopartner_sales"));
                                monitoringParameterMappingVO.setIsAlertToPartnerRF(res.getString("isalerttopartner_rf"));
                                monitoringParameterMappingVO.setIsAlertToPartnerCB(res.getString("isalerttopartner_cb"));
                                monitoringParameterMappingVO.setIsAlertToPartnerFraud(res.getString("isalerttopartner_fraud"));
                                monitoringParameterMappingVO.setIsAlertToPartnerTech(res.getString("isalerttopartner_tech"));

                                monitoringParameterMappingVO.setIsAlertToAgentSales(res.getString("isalerttoagent_sales"));
                                monitoringParameterMappingVO.setIsAlertToAgentRF(res.getString("isalerttoagent_rf"));
                                monitoringParameterMappingVO.setIsAlertToAgentCB(res.getString("isalerttoagent_cb"));
                                monitoringParameterMappingVO.setIsAlertToAgentFraud(res.getString("isalerttoagent_fraud"));
                                monitoringParameterMappingVO.setIsAlertToAgentTech(res.getString("isalerttoagent_tech"));

                                monitoringParameterMappingVO.setMappingId(res.getInt("mappingid"));
                                monitoringParameterMappingVO.setMemberId(res.getInt("memberid"));
                                monitoringParameterMappingVO.setTerminalId(res.getInt("terminalid"));

                                monitoringParameterMappingVO.setAlertThreshold(res.getDouble("alert_threshold"));
                                monitoringParameterMappingVO.setWeeklyAlertThreshold(res.getDouble("weekly_alert_threshold"));
                                monitoringParameterMappingVO.setMonthlyAlertThreshold(res.getDouble("monthly_alert_threshold"));

                                monitoringParameterMappingVO.setSuspensionThreshold(res.getDouble("suspension_threshold"));
                                monitoringParameterMappingVO.setWeeklySuspensionThreshold(res.getDouble("weekly_suspension_threshold"));
                                monitoringParameterMappingVO.setMonthlySuspensionThreshold(res.getDouble("monthly_suspension_threshold"));

                                monitoringParameterMappingVO.setAlertActivation(res.getString("alert_activation"));
                                monitoringParameterMappingVO.setSuspensionActivation(res.getString("suspension_activation"));
                                monitoringParameterMappingVO.setIsAlertToAdmin(res.getString("isalerttoadmin"));
                                monitoringParameterMappingVO.setIsAlertToMerchant(res.getString("isalerttomerchant"));
                                monitoringParameterMappingVO.setIsAlertToPartner(res.getString("isalerttopartner"));
                                monitoringParameterMappingVO.setIsAlertToAgent(res.getString("isalerttoagent"));
                                monitoringParameterMappingVO.setMonitoringParameterId(res.getInt("monitoing_para_id"));

                                monitoringParameterMappingVO.setAlertMessage(res.getString("alertMessage"));

                                monitoringParameterMappingVO.setMonitoringParameterVO(monitoringParameterVO);
                                monitoringParameterMappingVO.setTerminalVO(terminalVO);
                                monitoringParameterMappingVOs.add(monitoringParameterMappingVO);

                            }
                            while (res.next());
                            stringListMap.put(terminalId, monitoringParameterMappingVOs);
                        }
                    }
                    else
                    {
                        String query2 = "SELECT * from monitoring_parameter_master where  isdailyexecution=?";
                        PreparedStatement pstmt2 = conn.prepareStatement(query2);
                        pstmt2.setString(1, "Y");
                        ResultSet rs2 = pstmt2.executeQuery();
                        if (rs2.next())
                        {
                            monitoringParameterMappingVOs = new ArrayList();
                            do
                            {
                                monitoringParameterMappingVO = new MonitoringParameterMappingVO();
                                monitoringParameterVO = new MonitoringParameterVO();
                                terminalVO = new TerminalVO();

                                terminalVO.setMemberId(memberId);
                                terminalVO.setTerminalId(terminalId);

                                monitoringParameterVO.setMonitoringParameterName(rs2.getString("monitoing_para_name"));
                                monitoringParameterVO.setMonitoingParaTechName(rs2.getString("monitoing_para_tech_name"));
                                monitoringParameterVO.setMonitoingCategory(rs2.getString("monitoing_category"));
                                monitoringParameterVO.setMonitoingKeyword(rs2.getString("monitoing_keyword"));
                                monitoringParameterVO.setMonitoingSubKeyword(rs2.getString("monitoing_subkeyword"));
                                monitoringParameterVO.setMonitoingAlertCategory(rs2.getString("monitoing_alert_category"));
                                monitoringParameterVO.setMonitoingFrequency(rs2.getString("monitoing_frequency"));
                                monitoringParameterVO.setMonitoingOnChannel(rs2.getString("monitoing_onchannel"));
                                monitoringParameterVO.setMonitoingDeviation(rs2.getString("monitoring_deviation"));
                                monitoringParameterVO.setMonitoringUnit(rs2.getString("monitoring_unit"));

                                monitoringParameterMappingVO.setIsAlertToAdminSales(rs2.getString("isalerttoadmin_sales"));
                                monitoringParameterMappingVO.setIsAlertToAdminRF(rs2.getString("isalerttoadmin_rf"));
                                monitoringParameterMappingVO.setIsAlertToAdminCB(rs2.getString("isalerttoadmin_cb"));
                                monitoringParameterMappingVO.setIsAlertToAdminFraud(rs2.getString("isalerttoadmin_fraud"));
                                monitoringParameterMappingVO.setIsAlertToAdminTech(rs2.getString("isalerttoadmin_tech"));

                                monitoringParameterMappingVO.setIsAlertToMerchantSales(rs2.getString("isalerttomerchant_sales"));
                                monitoringParameterMappingVO.setIsAlertToMerchantRF(rs2.getString("isalerttomerchant_rf"));
                                monitoringParameterMappingVO.setIsAlertToMerchantCB(rs2.getString("isalerttomerchant_cb"));
                                monitoringParameterMappingVO.setIsAlertToMerchantFraud(rs2.getString("isalerttomerchant_fraud"));
                                monitoringParameterMappingVO.setIsAlertToMerchantTech(rs2.getString("isalerttomerchant_tech"));

                                monitoringParameterMappingVO.setIsAlertToPartnerSales(rs2.getString("isalerttopartner_sales"));
                                monitoringParameterMappingVO.setIsAlertToPartnerRF(rs2.getString("isalerttopartner_rf"));
                                monitoringParameterMappingVO.setIsAlertToPartnerCB(rs2.getString("isalerttopartner_cb"));
                                monitoringParameterMappingVO.setIsAlertToPartnerFraud(rs2.getString("isalerttopartner_fraud"));
                                monitoringParameterMappingVO.setIsAlertToPartnerTech(rs2.getString("isalerttopartner_tech"));

                                monitoringParameterMappingVO.setIsAlertToAgentSales(rs2.getString("isalerttoagent_sales"));
                                monitoringParameterMappingVO.setIsAlertToAgentRF(rs2.getString("isalerttoagent_rf"));
                                monitoringParameterMappingVO.setIsAlertToAgentCB(rs2.getString("isalerttoagent_cb"));
                                monitoringParameterMappingVO.setIsAlertToAgentFraud(rs2.getString("isalerttoagent_fraud"));
                                monitoringParameterMappingVO.setIsAlertToAgentTech(rs2.getString("isalerttoagent_tech"));

                                monitoringParameterMappingVO.setMemberId(Integer.parseInt(memberId));
                                monitoringParameterMappingVO.setTerminalId(Integer.parseInt(terminalId));

                                monitoringParameterMappingVO.setAlertThreshold(rs2.getDouble("default_alert_threshold"));
                                monitoringParameterMappingVO.setWeeklyAlertThreshold(rs2.getDouble("weekly_alert_threshold"));
                                monitoringParameterMappingVO.setMonthlyAlertThreshold(rs2.getDouble("monthly_alert_threshold"));

                                monitoringParameterMappingVO.setSuspensionThreshold(rs2.getDouble("default_suspension_threshold"));
                                monitoringParameterMappingVO.setWeeklySuspensionThreshold(rs2.getDouble("weekly_suspension_threshold"));
                                monitoringParameterMappingVO.setMonthlySuspensionThreshold(rs2.getDouble("monthly_suspension_threshold"));

                                monitoringParameterMappingVO.setAlertActivation(rs2.getString("default_alert_activation"));
                                monitoringParameterMappingVO.setSuspensionActivation(rs2.getString("default_suspension_activation"));
                                monitoringParameterMappingVO.setIsAlertToAdmin(rs2.getString("default_isAlertToAdmin"));
                                monitoringParameterMappingVO.setIsAlertToMerchant(rs2.getString("default_isAlertToMerchant"));
                                monitoringParameterMappingVO.setIsAlertToPartner(rs2.getString("default_isAlertToPartner"));
                                monitoringParameterMappingVO.setIsAlertToAgent(rs2.getString("default_isAlertToAgent"));
                                monitoringParameterMappingVO.setMonitoringParameterId(rs2.getInt("monitoing_para_id"));
                                monitoringParameterMappingVO.setAlertMessage(rs2.getString("default_alertMsg"));

                                monitoringParameterMappingVO.setMonitoringParameterVO(monitoringParameterVO);
                                monitoringParameterMappingVO.setTerminalVO(terminalVO);
                                monitoringParameterMappingVOs.add(monitoringParameterMappingVO);
                            }
                            while (rs2.next());
                            stringListMap.put(terminalId, monitoringParameterMappingVOs);
                        }

                    }
                }
                stringListMapMerchantLevel.put(memberId, stringListMap);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing System Exception as SystemError :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getMonitoringParameterGroupByMerchantDailyExecution()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing SQL Exception as SystemError :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getMonitoringParameterGroupByMerchantDailyExecution()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as Exception :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getMonitoringParameterGroupByMerchantDailyExecution()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return stringListMapMerchantLevel;
    }

    public Map<String, Map<String, List<MonitoringParameterMappingVO>>> getMonitoringParameterGroupByMerchantWeeklyExecution() throws PZDBViolationException
    {
        Map<String, Map<String, List<MonitoringParameterMappingVO>>> stringListMapMerchantLevel = new HashMap();
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT memberid,COUNT(*) FROM member_account_mapping  WHERE riskruleactivation='Y' and isActive='Y' GROUP BY memberid";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                String memberId = rs.getString("memberid");
                Map<String, List<MonitoringParameterMappingVO>> stringListMap = new HashMap();
                List<MonitoringParameterMappingVO> monitoringParameterMappingVOs = null;
                MonitoringParameterMappingVO monitoringParameterMappingVO = null;
                MonitoringParameterVO monitoringParameterVO = null;
                TerminalVO terminalVO = null;

                String query1 = "SELECT terminalid FROM member_account_mapping  WHERE riskruleactivation='Y' and isActive='Y' and memberid=?";
                PreparedStatement pstm1 = conn.prepareStatement(query1);
                pstm1.setString(1, memberId);
                ResultSet resNew = pstm1.executeQuery();
                while (resNew.next())
                {
                    String terminalId = resNew.getString("terminalid");
                    query = "SELECT monitoing_para_id FROM member_account_monitoringpara_mapping WHERE terminalid=?";
                    pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, terminalId);
                    ResultSet res1 = pstmt.executeQuery();
                    if (res1.next())
                    {
                        query = "SELECT mpm.monitoing_para_id,mpm.monitoing_para_name,mpm.monitoing_para_tech_name,mpm.monitoing_category,mpm.monitoing_keyword,mpm.monitoing_subkeyword,mpm.monitoing_alert_category,mpm.monitoing_alert_category,mpm.monitoing_frequency,mpm.monitoing_onchannel,mpm.monitoring_deviation,mpm.monitoring_unit,mamm.mappingid,mamm.memberid,mamm.terminalid,alert_threshold,mamm.weekly_alert_threshold,mamm.monthly_alert_threshold,suspension_threshold,mamm.weekly_suspension_threshold,mamm.monthly_suspension_threshold,alert_activation,suspension_activation,isalerttoadmin,isalerttomerchant,isalerttopartner,isalerttoagent,mpm.monitoing_para_id,mam.accountid,mam.paymodeid,mam.cardtypeid,mamm.alertMessage,mamm.isalerttoadmin_sales,mamm.isalerttoadmin_rf,mamm.isalerttoadmin_cb,mamm.isalerttoadmin_fraud,mamm.isalerttoadmin_tech,mamm.isalerttomerchant_sales,mamm.isalerttomerchant_rf,mamm.isalerttomerchant_cb,mamm.isalerttomerchant_fraud,mamm.isalerttomerchant_tech,mamm.isalerttopartner_sales,mamm.isalerttopartner_rf,mamm.isalerttopartner_cb,mamm.isalerttopartner_fraud,mamm.isalerttopartner_tech,mamm.isalerttoagent_sales,mamm.isalerttoagent_rf,mamm.isalerttoagent_cb,mamm.isalerttoagent_fraud,mamm.isalerttoagent_tech FROM member_account_monitoringpara_mapping AS mamm JOIN monitoring_parameter_master AS mpm ON mamm.monitoing_para_id=mpm.monitoing_para_id JOIN member_account_mapping AS mam ON mamm.terminalid=mam.terminalid WHERE mamm.terminalid=? and mamm.isweeklyexecution=?";
                        pstmt = conn.prepareStatement(query);
                        pstmt.setString(1, terminalId);
                        pstmt.setString(2, "Y");
                        ResultSet res = pstmt.executeQuery();
                        if (res.next())
                        {
                            monitoringParameterMappingVOs = new ArrayList();
                            do
                            {
                                monitoringParameterMappingVO = new MonitoringParameterMappingVO();
                                monitoringParameterVO = new MonitoringParameterVO();
                                terminalVO = new TerminalVO();

                                terminalVO.setMemberId(res.getString("memberid"));
                                terminalVO.setTerminalId(res.getString("terminalid"));
                                terminalVO.setAccountId(res.getString("accountid"));
                                terminalVO.setPaymodeId(res.getString("paymodeid"));
                                terminalVO.setCardTypeId(res.getString("cardtypeid"));

                                monitoringParameterVO.setMonitoringParameterName(res.getString("monitoing_para_name"));
                                monitoringParameterVO.setMonitoingParaTechName(res.getString("monitoing_para_tech_name"));
                                monitoringParameterVO.setMonitoingCategory(res.getString("monitoing_category"));
                                monitoringParameterVO.setMonitoingKeyword(res.getString("monitoing_keyword"));
                                monitoringParameterVO.setMonitoingSubKeyword(res.getString("monitoing_subkeyword"));
                                monitoringParameterVO.setMonitoingAlertCategory(res.getString("monitoing_alert_category"));
                                monitoringParameterVO.setMonitoingFrequency(res.getString("monitoing_frequency"));
                                monitoringParameterVO.setMonitoingOnChannel(res.getString("monitoing_onchannel"));
                                monitoringParameterVO.setMonitoingDeviation(res.getString("monitoring_deviation"));
                                monitoringParameterVO.setMonitoringUnit(res.getString("monitoring_unit"));

                                monitoringParameterMappingVO.setIsAlertToAdminSales(res.getString("isalerttoadmin_sales"));
                                monitoringParameterMappingVO.setIsAlertToAdminRF(res.getString("isalerttoadmin_rf"));
                                monitoringParameterMappingVO.setIsAlertToAdminCB(res.getString("isalerttoadmin_cb"));
                                monitoringParameterMappingVO.setIsAlertToAdminFraud(res.getString("isalerttoadmin_fraud"));
                                monitoringParameterMappingVO.setIsAlertToAdminTech(res.getString("isalerttoadmin_tech"));

                                monitoringParameterMappingVO.setIsAlertToMerchantSales(res.getString("isalerttomerchant_sales"));
                                monitoringParameterMappingVO.setIsAlertToMerchantRF(res.getString("isalerttomerchant_rf"));
                                monitoringParameterMappingVO.setIsAlertToMerchantCB(res.getString("isalerttomerchant_cb"));
                                monitoringParameterMappingVO.setIsAlertToMerchantFraud(res.getString("isalerttomerchant_fraud"));
                                monitoringParameterMappingVO.setIsAlertToMerchantTech(res.getString("isalerttomerchant_tech"));

                                monitoringParameterMappingVO.setIsAlertToPartnerSales(res.getString("isalerttopartner_sales"));
                                monitoringParameterMappingVO.setIsAlertToPartnerRF(res.getString("isalerttopartner_rf"));
                                monitoringParameterMappingVO.setIsAlertToPartnerCB(res.getString("isalerttopartner_cb"));
                                monitoringParameterMappingVO.setIsAlertToPartnerFraud(res.getString("isalerttopartner_fraud"));
                                monitoringParameterMappingVO.setIsAlertToPartnerTech(res.getString("isalerttopartner_tech"));

                                monitoringParameterMappingVO.setIsAlertToAgentSales(res.getString("isalerttoagent_sales"));
                                monitoringParameterMappingVO.setIsAlertToAgentRF(res.getString("isalerttoagent_rf"));
                                monitoringParameterMappingVO.setIsAlertToAgentCB(res.getString("isalerttoagent_cb"));
                                monitoringParameterMappingVO.setIsAlertToAgentFraud(res.getString("isalerttoagent_fraud"));
                                monitoringParameterMappingVO.setIsAlertToAgentTech(res.getString("isalerttoagent_tech"));

                                monitoringParameterMappingVO.setMappingId(res.getInt("mappingid"));
                                monitoringParameterMappingVO.setMemberId(res.getInt("memberid"));
                                monitoringParameterMappingVO.setTerminalId(res.getInt("terminalid"));

                                monitoringParameterMappingVO.setAlertThreshold(res.getDouble("alert_threshold"));
                                monitoringParameterMappingVO.setWeeklyAlertThreshold(res.getDouble("weekly_alert_threshold"));
                                monitoringParameterMappingVO.setMonthlyAlertThreshold(res.getDouble("monthly_alert_threshold"));

                                monitoringParameterMappingVO.setSuspensionThreshold(res.getDouble("suspension_threshold"));
                                monitoringParameterMappingVO.setWeeklySuspensionThreshold(res.getDouble("weekly_suspension_threshold"));
                                monitoringParameterMappingVO.setMonthlySuspensionThreshold(res.getDouble("monthly_suspension_threshold"));

                                monitoringParameterMappingVO.setAlertActivation(res.getString("alert_activation"));
                                monitoringParameterMappingVO.setSuspensionActivation(res.getString("suspension_activation"));
                                monitoringParameterMappingVO.setIsAlertToAdmin(res.getString("isalerttoadmin"));
                                monitoringParameterMappingVO.setIsAlertToMerchant(res.getString("isalerttomerchant"));
                                monitoringParameterMappingVO.setIsAlertToPartner(res.getString("isalerttopartner"));
                                monitoringParameterMappingVO.setIsAlertToAgent(res.getString("isalerttoagent"));
                                monitoringParameterMappingVO.setMonitoringParameterId(res.getInt("monitoing_para_id"));

                                monitoringParameterMappingVO.setAlertMessage(res.getString("alertMessage"));

                                monitoringParameterMappingVO.setMonitoringParameterVO(monitoringParameterVO);
                                monitoringParameterMappingVO.setTerminalVO(terminalVO);
                                monitoringParameterMappingVOs.add(monitoringParameterMappingVO);

                            }
                            while (res.next());
                            stringListMap.put(terminalId, monitoringParameterMappingVOs);
                        }
                    }
                    else
                    {
                        String query2 = "SELECT * from monitoring_parameter_master where  isweeklyexecution=?";
                        PreparedStatement pstmt2 = conn.prepareStatement(query2);
                        pstmt2.setString(1, "Y");
                        ResultSet rs2 = pstmt2.executeQuery();
                        if (rs2.next())
                        {
                            monitoringParameterMappingVOs = new ArrayList();
                            do
                            {
                                monitoringParameterMappingVO = new MonitoringParameterMappingVO();
                                monitoringParameterVO = new MonitoringParameterVO();
                                terminalVO = new TerminalVO();

                                terminalVO.setMemberId(memberId);
                                terminalVO.setTerminalId(terminalId);

                                monitoringParameterVO.setMonitoringParameterName(rs2.getString("monitoing_para_name"));
                                monitoringParameterVO.setMonitoingParaTechName(rs2.getString("monitoing_para_tech_name"));
                                monitoringParameterVO.setMonitoingCategory(rs2.getString("monitoing_category"));
                                monitoringParameterVO.setMonitoingKeyword(rs2.getString("monitoing_keyword"));
                                monitoringParameterVO.setMonitoingSubKeyword(rs2.getString("monitoing_subkeyword"));
                                monitoringParameterVO.setMonitoingAlertCategory(rs2.getString("monitoing_alert_category"));
                                monitoringParameterVO.setMonitoingFrequency(rs2.getString("monitoing_frequency"));
                                monitoringParameterVO.setMonitoingOnChannel(rs2.getString("monitoing_onchannel"));
                                monitoringParameterVO.setMonitoingDeviation(rs2.getString("monitoring_deviation"));
                                monitoringParameterVO.setMonitoringUnit(rs2.getString("monitoring_unit"));

                                monitoringParameterMappingVO.setIsAlertToAdminSales(rs2.getString("isalerttoadmin_sales"));
                                monitoringParameterMappingVO.setIsAlertToAdminRF(rs2.getString("isalerttoadmin_rf"));
                                monitoringParameterMappingVO.setIsAlertToAdminCB(rs2.getString("isalerttoadmin_cb"));
                                monitoringParameterMappingVO.setIsAlertToAdminFraud(rs2.getString("isalerttoadmin_fraud"));
                                monitoringParameterMappingVO.setIsAlertToAdminTech(rs2.getString("isalerttoadmin_tech"));

                                monitoringParameterMappingVO.setIsAlertToMerchantSales(rs2.getString("isalerttomerchant_sales"));
                                monitoringParameterMappingVO.setIsAlertToMerchantRF(rs2.getString("isalerttomerchant_rf"));
                                monitoringParameterMappingVO.setIsAlertToMerchantCB(rs2.getString("isalerttomerchant_cb"));
                                monitoringParameterMappingVO.setIsAlertToMerchantFraud(rs2.getString("isalerttomerchant_fraud"));
                                monitoringParameterMappingVO.setIsAlertToMerchantTech(rs2.getString("isalerttomerchant_tech"));

                                monitoringParameterMappingVO.setIsAlertToPartnerSales(rs2.getString("isalerttopartner_sales"));
                                monitoringParameterMappingVO.setIsAlertToPartnerRF(rs2.getString("isalerttopartner_rf"));
                                monitoringParameterMappingVO.setIsAlertToPartnerCB(rs2.getString("isalerttopartner_cb"));
                                monitoringParameterMappingVO.setIsAlertToPartnerFraud(rs2.getString("isalerttopartner_fraud"));
                                monitoringParameterMappingVO.setIsAlertToPartnerTech(rs2.getString("isalerttopartner_tech"));

                                monitoringParameterMappingVO.setIsAlertToAgentSales(rs2.getString("isalerttoagent_sales"));
                                monitoringParameterMappingVO.setIsAlertToAgentRF(rs2.getString("isalerttoagent_rf"));
                                monitoringParameterMappingVO.setIsAlertToAgentCB(rs2.getString("isalerttoagent_cb"));
                                monitoringParameterMappingVO.setIsAlertToAgentFraud(rs2.getString("isalerttoagent_fraud"));
                                monitoringParameterMappingVO.setIsAlertToAgentTech(rs2.getString("isalerttoagent_tech"));

                                monitoringParameterMappingVO.setMemberId(Integer.parseInt(memberId));
                                monitoringParameterMappingVO.setTerminalId(Integer.parseInt(terminalId));

                                monitoringParameterMappingVO.setAlertThreshold(rs2.getDouble("default_alert_threshold"));
                                monitoringParameterMappingVO.setWeeklyAlertThreshold(rs2.getDouble("weekly_alert_threshold"));
                                monitoringParameterMappingVO.setMonthlyAlertThreshold(rs2.getDouble("monthly_alert_threshold"));

                                monitoringParameterMappingVO.setSuspensionThreshold(rs2.getDouble("default_suspension_threshold"));
                                monitoringParameterMappingVO.setWeeklySuspensionThreshold(rs2.getDouble("weekly_suspension_threshold"));
                                monitoringParameterMappingVO.setMonthlySuspensionThreshold(rs2.getDouble("monthly_suspension_threshold"));

                                monitoringParameterMappingVO.setAlertActivation(rs2.getString("default_alert_activation"));
                                monitoringParameterMappingVO.setSuspensionActivation(rs2.getString("default_suspension_activation"));
                                monitoringParameterMappingVO.setIsAlertToAdmin(rs2.getString("default_isAlertToAdmin"));
                                monitoringParameterMappingVO.setIsAlertToMerchant(rs2.getString("default_isAlertToMerchant"));
                                monitoringParameterMappingVO.setIsAlertToPartner(rs2.getString("default_isAlertToPartner"));
                                monitoringParameterMappingVO.setIsAlertToAgent(rs2.getString("default_isAlertToAgent"));
                                monitoringParameterMappingVO.setMonitoringParameterId(rs2.getInt("monitoing_para_id"));
                                monitoringParameterMappingVO.setAlertMessage(rs2.getString("default_alertMsg"));

                                monitoringParameterMappingVO.setMonitoringParameterVO(monitoringParameterVO);
                                monitoringParameterMappingVO.setTerminalVO(terminalVO);
                                monitoringParameterMappingVOs.add(monitoringParameterMappingVO);
                            }
                            while (rs2.next());
                            stringListMap.put(terminalId, monitoringParameterMappingVOs);
                        }

                    }
                }
                stringListMapMerchantLevel.put(memberId, stringListMap);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing System Exception as SystemError :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getMonitoringParameterGroupByMerchantWeeklyExecution()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing SQL Exception as SystemError :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getMonitoringParameterGroupByMerchantWeeklyExecution()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as Exception :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getMonitoringParameterGroupByMerchantWeeklyExecution()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return stringListMapMerchantLevel;
    }

    public Map<String, Map<String, List<MonitoringParameterMappingVO>>> getMonitoringParameterGroupByMerchantMonthlyExecution() throws PZDBViolationException
    {
        Map<String, Map<String, List<MonitoringParameterMappingVO>>> stringListMapMerchantLevel = new HashMap();
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT memberid,COUNT(*) FROM member_account_mapping  WHERE riskruleactivation='Y' and isActive='Y' GROUP BY memberid";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                String memberId = rs.getString("memberid");
                Map<String, List<MonitoringParameterMappingVO>> stringListMap = new HashMap();
                List<MonitoringParameterMappingVO> monitoringParameterMappingVOs = null;
                MonitoringParameterMappingVO monitoringParameterMappingVO = null;
                MonitoringParameterVO monitoringParameterVO = null;
                TerminalVO terminalVO = null;

                String query1 = "SELECT terminalid FROM member_account_mapping  WHERE riskruleactivation='Y' and isActive='Y' and memberid=?";
                PreparedStatement pstm1 = conn.prepareStatement(query1);
                pstm1.setString(1, memberId);
                ResultSet resNew = pstm1.executeQuery();
                while (resNew.next())
                {
                    String terminalId = resNew.getString("terminalid");
                    query = "SELECT monitoing_para_id FROM member_account_monitoringpara_mapping WHERE terminalid=?";
                    pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, terminalId);
                    ResultSet res1 = pstmt.executeQuery();
                    if (res1.next())
                    {
                        query = "SELECT mpm.monitoing_para_id,mpm.monitoing_para_name,mpm.monitoing_para_tech_name,mpm.monitoing_category,mpm.monitoing_keyword,mpm.monitoing_subkeyword,mpm.monitoing_alert_category,mpm.monitoing_alert_category,mpm.monitoing_frequency,mpm.monitoing_onchannel,mpm.monitoring_deviation,mpm.monitoring_unit,mamm.mappingid,mamm.memberid,mamm.terminalid,alert_threshold,mamm.weekly_alert_threshold,mamm.monthly_alert_threshold,suspension_threshold,mamm.weekly_suspension_threshold,mamm.monthly_suspension_threshold,alert_activation,suspension_activation,isalerttoadmin,isalerttomerchant,isalerttopartner,isalerttoagent,mpm.monitoing_para_id,mam.accountid,mam.paymodeid,mam.cardtypeid,mamm.alertMessage,mamm.isalerttoadmin_sales,mamm.isalerttoadmin_rf,mamm.isalerttoadmin_cb,mamm.isalerttoadmin_fraud,mamm.isalerttoadmin_tech,mamm.isalerttomerchant_sales,mamm.isalerttomerchant_rf,mamm.isalerttomerchant_cb,mamm.isalerttomerchant_fraud,mamm.isalerttomerchant_tech,mamm.isalerttopartner_sales,mamm.isalerttopartner_rf,mamm.isalerttopartner_cb,mamm.isalerttopartner_fraud,mamm.isalerttopartner_tech,mamm.isalerttoagent_sales,mamm.isalerttoagent_rf,mamm.isalerttoagent_cb,mamm.isalerttoagent_fraud,mamm.isalerttoagent_tech FROM member_account_monitoringpara_mapping AS mamm JOIN monitoring_parameter_master AS mpm ON mamm.monitoing_para_id=mpm.monitoing_para_id JOIN member_account_mapping AS mam ON mamm.terminalid=mam.terminalid WHERE mamm.terminalid=? and mamm.ismonthlyexecution=?";
                        pstmt = conn.prepareStatement(query);
                        pstmt.setString(1, terminalId);
                        pstmt.setString(2, "Y");
                        ResultSet res = pstmt.executeQuery();
                        if(res.next())
                        {
                            monitoringParameterMappingVOs = new ArrayList();
                            do
                            {
                                monitoringParameterMappingVO = new MonitoringParameterMappingVO();
                                monitoringParameterVO = new MonitoringParameterVO();
                                terminalVO = new TerminalVO();

                                terminalVO.setMemberId(res.getString("memberid"));
                                terminalVO.setTerminalId(res.getString("terminalid"));
                                terminalVO.setAccountId(res.getString("accountid"));
                                terminalVO.setPaymodeId(res.getString("paymodeid"));
                                terminalVO.setCardTypeId(res.getString("cardtypeid"));

                                monitoringParameterVO.setMonitoringParameterName(res.getString("monitoing_para_name"));
                                monitoringParameterVO.setMonitoingParaTechName(res.getString("monitoing_para_tech_name"));
                                monitoringParameterVO.setMonitoingCategory(res.getString("monitoing_category"));
                                monitoringParameterVO.setMonitoingKeyword(res.getString("monitoing_keyword"));
                                monitoringParameterVO.setMonitoingSubKeyword(res.getString("monitoing_subkeyword"));
                                monitoringParameterVO.setMonitoingAlertCategory(res.getString("monitoing_alert_category"));
                                monitoringParameterVO.setMonitoingFrequency(res.getString("monitoing_frequency"));
                                monitoringParameterVO.setMonitoingOnChannel(res.getString("monitoing_onchannel"));
                                monitoringParameterVO.setMonitoingDeviation(res.getString("monitoring_deviation"));
                                monitoringParameterVO.setMonitoringUnit(res.getString("monitoring_unit"));

                                monitoringParameterMappingVO.setIsAlertToAdminSales(res.getString("isalerttoadmin_sales"));
                                monitoringParameterMappingVO.setIsAlertToAdminRF(res.getString("isalerttoadmin_rf"));
                                monitoringParameterMappingVO.setIsAlertToAdminCB(res.getString("isalerttoadmin_cb"));
                                monitoringParameterMappingVO.setIsAlertToAdminFraud(res.getString("isalerttoadmin_fraud"));
                                monitoringParameterMappingVO.setIsAlertToAdminTech(res.getString("isalerttoadmin_tech"));

                                monitoringParameterMappingVO.setIsAlertToMerchantSales(res.getString("isalerttomerchant_sales"));
                                monitoringParameterMappingVO.setIsAlertToMerchantRF(res.getString("isalerttomerchant_rf"));
                                monitoringParameterMappingVO.setIsAlertToMerchantCB(res.getString("isalerttomerchant_cb"));
                                monitoringParameterMappingVO.setIsAlertToMerchantFraud(res.getString("isalerttomerchant_fraud"));
                                monitoringParameterMappingVO.setIsAlertToMerchantTech(res.getString("isalerttomerchant_tech"));

                                monitoringParameterMappingVO.setIsAlertToPartnerSales(res.getString("isalerttopartner_sales"));
                                monitoringParameterMappingVO.setIsAlertToPartnerRF(res.getString("isalerttopartner_rf"));
                                monitoringParameterMappingVO.setIsAlertToPartnerCB(res.getString("isalerttopartner_cb"));
                                monitoringParameterMappingVO.setIsAlertToPartnerFraud(res.getString("isalerttopartner_fraud"));
                                monitoringParameterMappingVO.setIsAlertToPartnerTech(res.getString("isalerttopartner_tech"));

                                monitoringParameterMappingVO.setIsAlertToAgentSales(res.getString("isalerttoagent_sales"));
                                monitoringParameterMappingVO.setIsAlertToAgentRF(res.getString("isalerttoagent_rf"));
                                monitoringParameterMappingVO.setIsAlertToAgentCB(res.getString("isalerttoagent_cb"));
                                monitoringParameterMappingVO.setIsAlertToAgentFraud(res.getString("isalerttoagent_fraud"));
                                monitoringParameterMappingVO.setIsAlertToAgentTech(res.getString("isalerttoagent_tech"));

                                monitoringParameterMappingVO.setMappingId(res.getInt("mappingid"));
                                monitoringParameterMappingVO.setMemberId(res.getInt("memberid"));
                                monitoringParameterMappingVO.setTerminalId(res.getInt("terminalid"));

                                monitoringParameterMappingVO.setAlertThreshold(res.getDouble("alert_threshold"));
                                monitoringParameterMappingVO.setWeeklyAlertThreshold(res.getDouble("weekly_alert_threshold"));
                                monitoringParameterMappingVO.setMonthlyAlertThreshold(res.getDouble("monthly_alert_threshold"));

                                monitoringParameterMappingVO.setSuspensionThreshold(res.getDouble("suspension_threshold"));
                                monitoringParameterMappingVO.setWeeklySuspensionThreshold(res.getDouble("weekly_suspension_threshold"));
                                monitoringParameterMappingVO.setMonthlySuspensionThreshold(res.getDouble("monthly_suspension_threshold"));

                                monitoringParameterMappingVO.setAlertActivation(res.getString("alert_activation"));
                                monitoringParameterMappingVO.setSuspensionActivation(res.getString("suspension_activation"));
                                monitoringParameterMappingVO.setIsAlertToAdmin(res.getString("isalerttoadmin"));
                                monitoringParameterMappingVO.setIsAlertToMerchant(res.getString("isalerttomerchant"));
                                monitoringParameterMappingVO.setIsAlertToPartner(res.getString("isalerttopartner"));
                                monitoringParameterMappingVO.setIsAlertToAgent(res.getString("isalerttoagent"));
                                monitoringParameterMappingVO.setMonitoringParameterId(res.getInt("monitoing_para_id"));

                                monitoringParameterMappingVO.setAlertMessage(res.getString("alertMessage"));

                                monitoringParameterMappingVO.setMonitoringParameterVO(monitoringParameterVO);
                                monitoringParameterMappingVO.setTerminalVO(terminalVO);
                                monitoringParameterMappingVOs.add(monitoringParameterMappingVO);

                            }
                            while (res.next());
                            stringListMap.put(terminalId, monitoringParameterMappingVOs);
                        }
                    }
                    else
                    {
                        String query2 = "SELECT * from monitoring_parameter_master where ismonthlyexecution=?";
                        PreparedStatement pstmt2 = conn.prepareStatement(query2);
                        pstmt2.setString(1, "Y");
                        ResultSet rs2 = pstmt2.executeQuery();
                        if (rs2.next())
                        {
                            monitoringParameterMappingVOs = new ArrayList();
                            do
                            {
                                monitoringParameterMappingVO = new MonitoringParameterMappingVO();
                                monitoringParameterVO = new MonitoringParameterVO();
                                terminalVO = new TerminalVO();

                                terminalVO.setMemberId(memberId);
                                terminalVO.setTerminalId(terminalId);

                                monitoringParameterVO.setMonitoringParameterName(rs2.getString("monitoing_para_name"));
                                monitoringParameterVO.setMonitoingParaTechName(rs2.getString("monitoing_para_tech_name"));
                                monitoringParameterVO.setMonitoingCategory(rs2.getString("monitoing_category"));
                                monitoringParameterVO.setMonitoingKeyword(rs2.getString("monitoing_keyword"));
                                monitoringParameterVO.setMonitoingSubKeyword(rs2.getString("monitoing_subkeyword"));
                                monitoringParameterVO.setMonitoingAlertCategory(rs2.getString("monitoing_alert_category"));
                                monitoringParameterVO.setMonitoingFrequency(rs2.getString("monitoing_frequency"));
                                monitoringParameterVO.setMonitoingOnChannel(rs2.getString("monitoing_onchannel"));
                                monitoringParameterVO.setMonitoingDeviation(rs2.getString("monitoring_deviation"));
                                monitoringParameterVO.setMonitoringUnit(rs2.getString("monitoring_unit"));

                                monitoringParameterMappingVO.setIsAlertToAdminSales(rs2.getString("isalerttoadmin_sales"));
                                monitoringParameterMappingVO.setIsAlertToAdminRF(rs2.getString("isalerttoadmin_rf"));
                                monitoringParameterMappingVO.setIsAlertToAdminCB(rs2.getString("isalerttoadmin_cb"));
                                monitoringParameterMappingVO.setIsAlertToAdminFraud(rs2.getString("isalerttoadmin_fraud"));
                                monitoringParameterMappingVO.setIsAlertToAdminTech(rs2.getString("isalerttoadmin_tech"));

                                monitoringParameterMappingVO.setIsAlertToMerchantSales(rs2.getString("isalerttomerchant_sales"));
                                monitoringParameterMappingVO.setIsAlertToMerchantRF(rs2.getString("isalerttomerchant_rf"));
                                monitoringParameterMappingVO.setIsAlertToMerchantCB(rs2.getString("isalerttomerchant_cb"));
                                monitoringParameterMappingVO.setIsAlertToMerchantFraud(rs2.getString("isalerttomerchant_fraud"));
                                monitoringParameterMappingVO.setIsAlertToMerchantTech(rs2.getString("isalerttomerchant_tech"));

                                monitoringParameterMappingVO.setIsAlertToPartnerSales(rs2.getString("isalerttopartner_sales"));
                                monitoringParameterMappingVO.setIsAlertToPartnerRF(rs2.getString("isalerttopartner_rf"));
                                monitoringParameterMappingVO.setIsAlertToPartnerCB(rs2.getString("isalerttopartner_cb"));
                                monitoringParameterMappingVO.setIsAlertToPartnerFraud(rs2.getString("isalerttopartner_fraud"));
                                monitoringParameterMappingVO.setIsAlertToPartnerTech(rs2.getString("isalerttopartner_tech"));

                                monitoringParameterMappingVO.setIsAlertToAgentSales(rs2.getString("isalerttoagent_sales"));
                                monitoringParameterMappingVO.setIsAlertToAgentRF(rs2.getString("isalerttoagent_rf"));
                                monitoringParameterMappingVO.setIsAlertToAgentCB(rs2.getString("isalerttoagent_cb"));
                                monitoringParameterMappingVO.setIsAlertToAgentFraud(rs2.getString("isalerttoagent_fraud"));
                                monitoringParameterMappingVO.setIsAlertToAgentTech(rs2.getString("isalerttoagent_tech"));

                                monitoringParameterMappingVO.setMemberId(Integer.parseInt(memberId));
                                monitoringParameterMappingVO.setTerminalId(Integer.parseInt(terminalId));

                                monitoringParameterMappingVO.setAlertThreshold(rs2.getDouble("default_alert_threshold"));
                                monitoringParameterMappingVO.setWeeklyAlertThreshold(rs2.getDouble("weekly_alert_threshold"));
                                monitoringParameterMappingVO.setMonthlyAlertThreshold(rs2.getDouble("monthly_alert_threshold"));

                                monitoringParameterMappingVO.setSuspensionThreshold(rs2.getDouble("default_suspension_threshold"));
                                monitoringParameterMappingVO.setWeeklySuspensionThreshold(rs2.getDouble("weekly_suspension_threshold"));
                                monitoringParameterMappingVO.setMonthlySuspensionThreshold(rs2.getDouble("monthly_suspension_threshold"));

                                monitoringParameterMappingVO.setAlertActivation(rs2.getString("default_alert_activation"));
                                monitoringParameterMappingVO.setSuspensionActivation(rs2.getString("default_suspension_activation"));
                                monitoringParameterMappingVO.setIsAlertToAdmin(rs2.getString("default_isAlertToAdmin"));
                                monitoringParameterMappingVO.setIsAlertToMerchant(rs2.getString("default_isAlertToMerchant"));
                                monitoringParameterMappingVO.setIsAlertToPartner(rs2.getString("default_isAlertToPartner"));
                                monitoringParameterMappingVO.setIsAlertToAgent(rs2.getString("default_isAlertToAgent"));
                                monitoringParameterMappingVO.setMonitoringParameterId(rs2.getInt("monitoing_para_id"));
                                monitoringParameterMappingVO.setAlertMessage(rs2.getString("default_alertMsg"));

                                monitoringParameterMappingVO.setMonitoringParameterVO(monitoringParameterVO);
                                monitoringParameterMappingVO.setTerminalVO(terminalVO);
                                monitoringParameterMappingVOs.add(monitoringParameterMappingVO);
                            }
                            while (rs2.next());
                            stringListMap.put(terminalId, monitoringParameterMappingVOs);
                        }

                    }
                }
                stringListMapMerchantLevel.put(memberId, stringListMap);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing System Exception as SystemError :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getMonitoringParameterGroupByMerchantMonthlyExecution()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing SQL Exception as SystemError :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getMonitoringParameterGroupByMerchantMonthlyExecution()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as Exception :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getMonitoringParameterGroupByMerchantMonthlyExecution()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return stringListMapMerchantLevel;
    }
    public List<MonitoringParameterMappingVO> getRiskRuleOnTerminalFromMapping(String terminalId, String memberId) throws PZDBViolationException
    {
        List<MonitoringParameterMappingVO> monitoringParameterMappingVOs = null;
        MonitoringParameterMappingVO monitoringParameterMappingVO = null;
        MonitoringParameterVO monitoringParameterVO = null;
        TerminalVO terminalVO = null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT mpm.monitoing_para_id,mpm.monitoing_para_name,mpm.monitoing_para_tech_name,mpm.monitoing_category,mpm.monitoing_keyword,mpm.monitoing_subkeyword,mpm.monitoing_alert_category,mpm.monitoing_onchannel,mpm.monitoring_deviation,mpm.monitoring_unit,mamm.mappingid,mamm.memberid,mamm.terminalid,alert_threshold,mamm.weekly_alert_threshold,mamm.monthly_alert_threshold,mamm.suspension_threshold,mamm.weekly_suspension_threshold,mamm.monthly_suspension_threshold,alert_activation,suspension_activation,isalerttoadmin,isalerttomerchant,isalerttopartner,isalerttoagent,mpm.monitoing_para_id,mam.accountid,mam.paymodeid,mam.cardtypeid,alertMessage,mamm.isalerttoadmin_sales,mamm.isalerttoadmin_rf,mamm.isalerttoadmin_cb,mamm.isalerttoadmin_fraud,mamm.isalerttoadmin_tech,mamm.isalerttomerchant_sales,mamm.isalerttomerchant_rf,mamm.isalerttomerchant_cb,mamm.isalerttomerchant_fraud,mamm.isalerttomerchant_tech,mamm.isalerttopartner_sales,mamm.isalerttopartner_rf,mamm.isalerttopartner_cb,mamm.isalerttopartner_fraud,mamm.isalerttopartner_tech,mamm.isalerttoagent_sales,mamm.isalerttoagent_rf,mamm.isalerttoagent_cb,mamm.isalerttoagent_fraud,mamm.isalerttoagent_tech,mamm.isdailyexecution,mamm.isweeklyexecution,mamm.ismonthlyexecution FROM member_account_monitoringpara_mapping AS mamm JOIN monitoring_parameter_master AS mpm ON mamm.monitoing_para_id=mpm.monitoing_para_id JOIN member_account_mapping AS mam ON mamm.terminalid=mam.terminalid WHERE mamm.terminalid=? and mamm.memberid=? ORDER BY monitoing_para_id";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, terminalId);
            pstmt.setString(2, memberId);
            res = pstmt.executeQuery();
            if (res.next())
            {
                monitoringParameterMappingVOs = new ArrayList();
                do
                {
                    monitoringParameterMappingVO = new MonitoringParameterMappingVO();
                    monitoringParameterVO = new MonitoringParameterVO();

                    terminalVO = new TerminalVO();
                    terminalVO.setMemberId(res.getString("memberid"));
                    terminalVO.setTerminalId(res.getString("terminalid"));
                    terminalVO.setAccountId(res.getString("accountid"));
                    terminalVO.setPaymodeId(res.getString("paymodeid"));
                    terminalVO.setCardTypeId(res.getString("cardtypeid"));

                    monitoringParameterVO.setMonitoringParameterId(res.getInt("monitoing_para_id"));
                    monitoringParameterVO.setMonitoringParameterName(res.getString("monitoing_para_name"));
                    monitoringParameterVO.setMonitoingParaTechName(res.getString("monitoing_para_tech_name"));
                    monitoringParameterVO.setMonitoingKeyword(res.getString("monitoing_category"));
                    monitoringParameterVO.setMonitoingKeyword(res.getString("monitoing_keyword"));
                    monitoringParameterVO.setMonitoingSubKeyword(res.getString("monitoing_subkeyword"));
                    monitoringParameterVO.setMonitoingAlertCategory(res.getString("monitoing_alert_category"));
                    monitoringParameterVO.setMonitoingOnChannel(res.getString("monitoing_onchannel"));
                    monitoringParameterVO.setMonitoringUnit(res.getString("monitoring_unit"));
                    monitoringParameterVO.setDefaultAlertMsg(res.getString("alertMessage"));

                    monitoringParameterMappingVO.setMappingId(res.getInt("mappingid"));
                    monitoringParameterMappingVO.setMemberId(res.getInt("memberid"));
                    monitoringParameterMappingVO.setTerminalId(res.getInt("terminalid"));

                    monitoringParameterMappingVO.setAlertThreshold(res.getDouble("alert_threshold"));
                    monitoringParameterMappingVO.setWeeklyAlertThreshold(res.getDouble("weekly_alert_threshold"));
                    monitoringParameterMappingVO.setMonthlyAlertThreshold(res.getDouble("monthly_alert_threshold"));

                    monitoringParameterMappingVO.setSuspensionThreshold(res.getDouble("suspension_threshold"));
                    monitoringParameterMappingVO.setWeeklySuspensionThreshold(res.getDouble("weekly_suspension_threshold"));
                    monitoringParameterMappingVO.setMonthlySuspensionThreshold(res.getDouble("monthly_suspension_threshold"));

                    monitoringParameterMappingVO.setAlertActivation(res.getString("alert_activation"));
                    monitoringParameterMappingVO.setSuspensionActivation(res.getString("suspension_activation"));
                    monitoringParameterMappingVO.setIsAlertToAdmin(res.getString("isalerttoadmin"));
                    monitoringParameterMappingVO.setIsAlertToMerchant(res.getString("isalerttomerchant"));
                    monitoringParameterMappingVO.setIsAlertToPartner(res.getString("isalerttopartner"));
                    monitoringParameterMappingVO.setIsAlertToAgent(res.getString("isalerttoagent"));

                    monitoringParameterMappingVO.setIsAlertToAdminSales(res.getString("isalerttoadmin_sales"));
                    monitoringParameterMappingVO.setIsAlertToAdminRF(res.getString("isalerttoadmin_rf"));
                    monitoringParameterMappingVO.setIsAlertToAdminCB(res.getString("isalerttoadmin_cb"));
                    monitoringParameterMappingVO.setIsAlertToAdminFraud(res.getString("isalerttoadmin_fraud"));
                    monitoringParameterMappingVO.setIsAlertToAdminTech(res.getString("isalerttoadmin_tech"));

                    monitoringParameterMappingVO.setIsAlertToMerchantSales(res.getString("isalerttomerchant_sales"));
                    monitoringParameterMappingVO.setIsAlertToMerchantRF(res.getString("isalerttomerchant_rf"));
                    monitoringParameterMappingVO.setIsAlertToMerchantCB(res.getString("isalerttomerchant_cb"));
                    monitoringParameterMappingVO.setIsAlertToMerchantFraud(res.getString("isalerttomerchant_fraud"));
                    monitoringParameterMappingVO.setIsAlertToMerchantTech(res.getString("isalerttomerchant_tech"));

                    monitoringParameterMappingVO.setIsAlertToPartnerSales(res.getString("isalerttopartner_sales"));
                    monitoringParameterMappingVO.setIsAlertToPartnerRF(res.getString("isalerttopartner_rf"));
                    monitoringParameterMappingVO.setIsAlertToPartnerCB(res.getString("isalerttopartner_cb"));
                    monitoringParameterMappingVO.setIsAlertToPartnerFraud(res.getString("isalerttopartner_fraud"));
                    monitoringParameterMappingVO.setIsAlertToPartnerTech(res.getString("isalerttopartner_tech"));

                    monitoringParameterMappingVO.setIsAlertToAgentSales(res.getString("isalerttoagent_sales"));
                    monitoringParameterMappingVO.setIsAlertToAgentRF(res.getString("isalerttoagent_rf"));
                    monitoringParameterMappingVO.setIsAlertToAgentCB(res.getString("isalerttoagent_cb"));
                    monitoringParameterMappingVO.setIsAlertToAgentFraud(res.getString("isalerttoagent_fraud"));
                    monitoringParameterMappingVO.setIsAlertToAgentTech(res.getString("isalerttoagent_tech"));

                    monitoringParameterMappingVO.setIsDailyExecution(res.getString("isdailyexecution"));
                    monitoringParameterMappingVO.setIsWeeklyExecution(res.getString("isweeklyexecution"));
                    monitoringParameterMappingVO.setIsMonthlyExecution(res.getString("ismonthlyexecution"));

                    monitoringParameterMappingVO.setMonitoringParameterId(res.getInt("monitoing_para_id"));

                    monitoringParameterMappingVO.setMonitoringParameterVO(monitoringParameterVO);
                    monitoringParameterMappingVO.setTerminalVO(terminalVO);
                    monitoringParameterMappingVOs.add(monitoringParameterMappingVO);

                }
                while (res.next());
            }
            else
            {
                logger.debug(terminalId + "Need to be pick rule from master");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing System Exception as SystemError :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getDailyMonitoringParameterGroupByMerchantId()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing SQL Exception as SystemError :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getDailyMonitoringParameterGroupByMerchantId()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as Exception :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getDailyMonitoringParameterGroupByMerchantId()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return monitoringParameterMappingVOs;
    }
    public List<MonitoringParameterMappingVO> getRiskRuleOnTerminalFromMappingForPartner(String terminalId, String memberId) throws PZDBViolationException
    {
        List<MonitoringParameterMappingVO> monitoringParameterMappingVOs = null;
        MonitoringParameterMappingVO monitoringParameterMappingVO = null;
        MonitoringParameterVO monitoringParameterVO = null;
        TerminalVO terminalVO = null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            String query = "SELECT mpm.monitoing_para_id,mpm.monitoing_para_name,mpm.monitoing_para_tech_name,mpm.monitoing_category,mpm.monitoing_keyword,mpm.monitoing_subkeyword,mpm.monitoing_alert_category,mpm.monitoing_onchannel,mpm.monitoring_deviation,mpm.monitoring_unit,mamm.alertMessage,mamm.mappingid,mamm.memberid,mamm.terminalid,alert_threshold,mamm.weekly_alert_threshold,mamm.monthly_alert_threshold,mamm.suspension_threshold,mamm.weekly_suspension_threshold,mamm.monthly_suspension_threshold,alert_activation,suspension_activation,isalerttoadmin,isalerttomerchant,isalerttopartner,isalerttoagent,mpm.monitoing_para_id,mam.accountid,mam.paymodeid,mam.cardtypeid,alertMessage,mamm.isalerttoadmin_sales,mamm.isalerttoadmin_rf,mamm.isalerttoadmin_cb,mamm.isalerttoadmin_fraud,mamm.isalerttoadmin_tech,mamm.isalerttomerchant_sales,mamm.isalerttomerchant_rf,mamm.isalerttomerchant_cb,mamm.isalerttomerchant_fraud,mamm.isalerttomerchant_tech,mamm.isalerttopartner_sales,mamm.isalerttopartner_rf,mamm.isalerttopartner_cb,mamm.isalerttopartner_fraud,mamm.isalerttopartner_tech,mamm.isalerttoagent_sales,mamm.isalerttoagent_rf,mamm.isalerttoagent_cb,mamm.isalerttoagent_fraud,mamm.isalerttoagent_tech,mamm.isdailyexecution,mamm.isweeklyexecution,mamm.ismonthlyexecution FROM member_account_monitoringpara_mapping AS mamm JOIN monitoring_parameter_master AS mpm ON mamm.monitoing_para_id=mpm.monitoing_para_id JOIN member_account_mapping AS mam ON mamm.terminalid=mam.terminalid WHERE mamm.terminalid=? and mamm.memberid=? ORDER BY monitoing_para_id";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, terminalId);
            pstmt.setString(2, memberId);
            res = pstmt.executeQuery();
            if (res.next())
            {
                monitoringParameterMappingVOs = new ArrayList();
                do
                {
                    monitoringParameterMappingVO = new MonitoringParameterMappingVO();
                    monitoringParameterVO = new MonitoringParameterVO();
                    String alertMessage = res.getString("alertMessage").trim();

                    terminalVO = new TerminalVO();
                    terminalVO.setMemberId(res.getString("memberid"));
                    terminalVO.setTerminalId(res.getString("terminalid"));
                    terminalVO.setAccountId(res.getString("accountid"));
                    terminalVO.setPaymodeId(res.getString("paymodeid"));
                    terminalVO.setCardTypeId(res.getString("cardtypeid"));

                    monitoringParameterVO.setMonitoringParameterId(res.getInt("monitoing_para_id"));
                    monitoringParameterVO.setMonitoringParameterName(res.getString("monitoing_para_name"));
                    monitoringParameterVO.setMonitoingParaTechName(res.getString("monitoing_para_tech_name"));
                    monitoringParameterVO.setMonitoingKeyword(res.getString("monitoing_category"));
                    monitoringParameterVO.setMonitoingKeyword(res.getString("monitoing_keyword"));
                    monitoringParameterVO.setMonitoingSubKeyword(res.getString("monitoing_subkeyword"));
                    monitoringParameterVO.setMonitoingAlertCategory(res.getString("monitoing_alert_category"));
                    monitoringParameterVO.setMonitoingOnChannel(res.getString("monitoing_onchannel"));
                    monitoringParameterVO.setMonitoringUnit(res.getString("monitoring_unit"));
                    monitoringParameterVO.setDefaultAlertMsg(alertMessage);

                    monitoringParameterMappingVO.setMappingId(res.getInt("mappingid"));
                    monitoringParameterMappingVO.setMemberId(res.getInt("memberid"));
                    monitoringParameterMappingVO.setTerminalId(res.getInt("terminalid"));
                    monitoringParameterMappingVO.setAlertMessage(alertMessage);

                    monitoringParameterMappingVO.setAlertThreshold(res.getDouble("alert_threshold"));
                    monitoringParameterMappingVO.setWeeklyAlertThreshold(res.getDouble("weekly_alert_threshold"));
                    monitoringParameterMappingVO.setMonthlyAlertThreshold(res.getDouble("monthly_alert_threshold"));

                    monitoringParameterMappingVO.setSuspensionThreshold(res.getDouble("suspension_threshold"));
                    monitoringParameterMappingVO.setWeeklySuspensionThreshold(res.getDouble("weekly_suspension_threshold"));
                    monitoringParameterMappingVO.setMonthlySuspensionThreshold(res.getDouble("monthly_suspension_threshold"));

                    monitoringParameterMappingVO.setAlertActivation(res.getString("alert_activation"));
                    monitoringParameterMappingVO.setSuspensionActivation(res.getString("suspension_activation"));
                    monitoringParameterMappingVO.setIsAlertToAdmin(res.getString("isalerttoadmin"));
                    monitoringParameterMappingVO.setIsAlertToMerchant(res.getString("isalerttomerchant"));
                    monitoringParameterMappingVO.setIsAlertToPartner(res.getString("isalerttopartner"));
                    monitoringParameterMappingVO.setIsAlertToAgent(res.getString("isalerttoagent"));

                    monitoringParameterMappingVO.setIsAlertToAdminSales(res.getString("isalerttoadmin_sales"));
                    monitoringParameterMappingVO.setIsAlertToAdminRF(res.getString("isalerttoadmin_rf"));
                    monitoringParameterMappingVO.setIsAlertToAdminCB(res.getString("isalerttoadmin_cb"));
                    monitoringParameterMappingVO.setIsAlertToAdminFraud(res.getString("isalerttoadmin_fraud"));
                    monitoringParameterMappingVO.setIsAlertToAdminTech(res.getString("isalerttoadmin_tech"));

                    monitoringParameterMappingVO.setIsAlertToMerchantSales(res.getString("isalerttomerchant_sales"));
                    monitoringParameterMappingVO.setIsAlertToMerchantRF(res.getString("isalerttomerchant_rf"));
                    monitoringParameterMappingVO.setIsAlertToMerchantCB(res.getString("isalerttomerchant_cb"));
                    monitoringParameterMappingVO.setIsAlertToMerchantFraud(res.getString("isalerttomerchant_fraud"));
                    monitoringParameterMappingVO.setIsAlertToMerchantTech(res.getString("isalerttomerchant_tech"));

                    monitoringParameterMappingVO.setIsAlertToPartnerSales(res.getString("isalerttopartner_sales"));
                    monitoringParameterMappingVO.setIsAlertToPartnerRF(res.getString("isalerttopartner_rf"));
                    monitoringParameterMappingVO.setIsAlertToPartnerCB(res.getString("isalerttopartner_cb"));
                    monitoringParameterMappingVO.setIsAlertToPartnerFraud(res.getString("isalerttopartner_fraud"));
                    monitoringParameterMappingVO.setIsAlertToPartnerTech(res.getString("isalerttopartner_tech"));

                    monitoringParameterMappingVO.setIsAlertToAgentSales(res.getString("isalerttoagent_sales"));
                    monitoringParameterMappingVO.setIsAlertToAgentRF(res.getString("isalerttoagent_rf"));
                    monitoringParameterMappingVO.setIsAlertToAgentCB(res.getString("isalerttoagent_cb"));
                    monitoringParameterMappingVO.setIsAlertToAgentFraud(res.getString("isalerttoagent_fraud"));
                    monitoringParameterMappingVO.setIsAlertToAgentTech(res.getString("isalerttoagent_tech"));

                    monitoringParameterMappingVO.setIsDailyExecution(res.getString("isdailyexecution"));
                    monitoringParameterMappingVO.setIsWeeklyExecution(res.getString("isweeklyexecution"));
                    monitoringParameterMappingVO.setIsMonthlyExecution(res.getString("ismonthlyexecution"));

                    monitoringParameterMappingVO.setMonitoringParameterId(res.getInt("monitoing_para_id"));

                    monitoringParameterMappingVO.setMonitoringParameterVO(monitoringParameterVO);
                    monitoringParameterMappingVO.setTerminalVO(terminalVO);
                    monitoringParameterMappingVOs.add(monitoringParameterMappingVO);
                }
                while (res.next());
            }
            else
            {
                logger.debug(terminalId + "Need to be pick rule from master");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing System Exception as SystemError :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getRiskRuleOnTerminalFromMappingForPartner()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing SQL Exception as SystemError :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getRiskRuleOnTerminalFromMappingForPartner()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as Exception :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getRiskRuleOnTerminalFromMappingForPartner()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return monitoringParameterMappingVOs;
    }

    public List<MonitoringParameterMappingVO> getRiskRuleOnTerminalFromMaster() throws PZDBViolationException
    {
        List<MonitoringParameterMappingVO> monitoringParameterMappingVOs = null;
        MonitoringParameterMappingVO monitoringParameterMappingVO = null;
        MonitoringParameterVO monitoringParameterVO = null;
        TerminalVO terminalVO = null;
        Connection conn = null;
        PreparedStatement pstmt2=null;
        ResultSet rs2=null;
        try
        {
            conn = Database.getRDBConnection();
            String query2 = "SELECT * from monitoring_parameter_master";
            pstmt2 = conn.prepareStatement(query2);
            rs2 = pstmt2.executeQuery();
            if (rs2.next())
            {
                monitoringParameterMappingVOs = new ArrayList();
                do
                {
                    monitoringParameterMappingVO = new MonitoringParameterMappingVO();
                    monitoringParameterVO = new MonitoringParameterVO();
                    terminalVO = new TerminalVO();

                    monitoringParameterVO.setMonitoringParameterName(rs2.getString("monitoing_para_name"));
                    monitoringParameterVO.setMonitoingParaTechName(rs2.getString("monitoing_para_tech_name"));
                    monitoringParameterVO.setMonitoingCategory(rs2.getString("monitoing_category"));
                    monitoringParameterVO.setMonitoingKeyword(rs2.getString("monitoing_keyword"));
                    monitoringParameterVO.setMonitoingSubKeyword(rs2.getString("monitoing_subkeyword"));
                    monitoringParameterVO.setMonitoingAlertCategory(rs2.getString("monitoing_alert_category"));
                    monitoringParameterVO.setMonitoingOnChannel(rs2.getString("monitoing_onchannel"));
                    monitoringParameterVO.setMonitoringUnit(rs2.getString("monitoring_unit"));
                    monitoringParameterVO.setMonitoringParameterId(Integer.parseInt(rs2.getString("monitoing_para_id")));
                    monitoringParameterVO.setDefaultAlertMsg(rs2.getString("default_alertMsg").trim());

                    monitoringParameterMappingVO.setAlertThreshold(rs2.getDouble("default_alert_threshold"));
                    monitoringParameterMappingVO.setWeeklyAlertThreshold(rs2.getDouble("weekly_alert_threshold"));
                    monitoringParameterMappingVO.setMonthlyAlertThreshold(rs2.getDouble("monthly_alert_threshold"));
                    monitoringParameterMappingVO.setAlertMessage(rs2.getString("default_alertMsg").trim());

                    monitoringParameterMappingVO.setSuspensionThreshold(rs2.getDouble("default_suspension_threshold"));
                    monitoringParameterMappingVO.setWeeklySuspensionThreshold(rs2.getDouble("weekly_suspension_threshold"));
                    monitoringParameterMappingVO.setMonthlySuspensionThreshold(rs2.getDouble("monthly_suspension_threshold"));

                    monitoringParameterMappingVO.setAlertActivation(rs2.getString("default_alert_activation"));
                    monitoringParameterMappingVO.setSuspensionActivation(rs2.getString("default_suspension_activation"));
                    monitoringParameterMappingVO.setIsAlertToAdmin(rs2.getString("default_isAlertToAdmin"));
                    monitoringParameterMappingVO.setIsAlertToMerchant(rs2.getString("default_isAlertToMerchant"));
                    monitoringParameterMappingVO.setIsAlertToPartner(rs2.getString("default_isAlertToPartner"));
                    monitoringParameterMappingVO.setIsAlertToAgent(rs2.getString("default_isAlertToAgent"));
                    monitoringParameterMappingVO.setMonitoringParameterId(rs2.getInt("monitoing_para_id"));

                    monitoringParameterMappingVO.setIsAlertToAdminSales(rs2.getString("isalerttoadmin_sales"));
                    monitoringParameterMappingVO.setIsAlertToAdminRF(rs2.getString("isalerttoadmin_rf"));
                    monitoringParameterMappingVO.setIsAlertToAdminCB(rs2.getString("isalerttoadmin_cb"));
                    monitoringParameterMappingVO.setIsAlertToAdminFraud(rs2.getString("isalerttoadmin_fraud"));
                    monitoringParameterMappingVO.setIsAlertToAdminTech(rs2.getString("isalerttoadmin_tech"));

                    monitoringParameterMappingVO.setIsAlertToMerchantSales(rs2.getString("isalerttomerchant_sales"));
                    monitoringParameterMappingVO.setIsAlertToMerchantRF(rs2.getString("isalerttomerchant_rf"));
                    monitoringParameterMappingVO.setIsAlertToMerchantCB(rs2.getString("isalerttomerchant_cb"));
                    monitoringParameterMappingVO.setIsAlertToMerchantFraud(rs2.getString("isalerttomerchant_fraud"));
                    monitoringParameterMappingVO.setIsAlertToMerchantTech(rs2.getString("isalerttomerchant_tech"));

                    monitoringParameterMappingVO.setIsAlertToPartnerSales(rs2.getString("isalerttopartner_sales"));
                    monitoringParameterMappingVO.setIsAlertToPartnerRF(rs2.getString("isalerttopartner_rf"));
                    monitoringParameterMappingVO.setIsAlertToPartnerCB(rs2.getString("isalerttopartner_cb"));
                    monitoringParameterMappingVO.setIsAlertToPartnerFraud(rs2.getString("isalerttopartner_fraud"));
                    monitoringParameterMappingVO.setIsAlertToPartnerTech(rs2.getString("isalerttopartner_tech"));

                    monitoringParameterMappingVO.setIsAlertToAgentSales(rs2.getString("isalerttoagent_sales"));
                    monitoringParameterMappingVO.setIsAlertToAgentRF(rs2.getString("isalerttoagent_rf"));
                    monitoringParameterMappingVO.setIsAlertToAgentCB(rs2.getString("isalerttoagent_cb"));
                    monitoringParameterMappingVO.setIsAlertToAgentFraud(rs2.getString("isalerttoagent_fraud"));
                    monitoringParameterMappingVO.setIsAlertToAgentTech(rs2.getString("isalerttoagent_tech"));

                    monitoringParameterMappingVO.setIsDailyExecution(rs2.getString("isdailyexecution"));
                    monitoringParameterMappingVO.setIsWeeklyExecution(rs2.getString("isweeklyexecution"));
                    monitoringParameterMappingVO.setIsMonthlyExecution(rs2.getString("ismonthlyexecution"));

                    monitoringParameterMappingVO.setMonitoringParameterVO(monitoringParameterVO);
                    monitoringParameterMappingVO.setTerminalVO(terminalVO);

                    monitoringParameterMappingVOs.add(monitoringParameterMappingVO);
                }
                while (rs2.next());
            }
            else
            {
                logger.debug("Need to be pick rule from master");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing System Exception as SystemError :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getRiskRuleOnTerminalFromMaster()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving MerchantMonitoringDAO throwing SQL Exception as SystemError :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getRiskRuleOnTerminalFromMaster()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as Exception :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getRiskRuleOnTerminalFromMaster()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs2);
            Database.closePreparedStatement(pstmt2);
            Database.closeConnection(conn);
        }
        return monitoringParameterMappingVOs;
    }

    public boolean removeRiskRuleMapping(String memberId, String terminalId, String riskRuleId) throws PZDBViolationException
    {
        Connection con = null;
        boolean status = false;
        PreparedStatement ps=null;
        try
        {
            con = Database.getConnection();
            StringBuffer qry = new StringBuffer("delete from member_account_monitoringpara_mapping where memberid=? and terminalid=? and monitoing_para_id=?");
            ps = con.prepareStatement(qry.toString());
            ps.setString(1, memberId);
            ps.setString(2, terminalId);
            ps.setString(3, riskRuleId);
            int k = ps.executeUpdate();
            if (k > 0)
            {
                status = true;
            }
        }
        catch (SQLException e)
        {
            status = true;
            logger.error("SQL Exception while adding new Fraud Sub-account rule Mapping", e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "removeRiskRuleMapping()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            status = true;
            logger.error("System Error while  adding new Fraud Sub-account rule Mapping::", systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "removeRiskRuleMapping()", null, "Common", "Sql exception while connecting to rule master", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return status;
    }
    public boolean isParameterNameAvailableBasedOnId(MonitoringParameterVO monitoringParameterVO)throws  PZDBViolationException
    {
        Connection con = null;
        boolean status=false;
        PreparedStatement ps=null;
        ResultSet rs=null;
        try
        {
            con=Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select monitoing_para_id from monitoring_parameter_master where monitoing_para_id=? and monitoing_category=? and monitoing_keyword=? and monitoing_subkeyword=? and monitoing_alert_category=? and monitoing_onchannel=? and monitoring_deviation=? and monitoring_unit=?");
            ps = con.prepareStatement(qry.toString());
            ps.setInt(1,monitoringParameterVO.getMonitoringParameterId());
            ps.setString(2, monitoringParameterVO.getMonitoingCategory());
            ps.setString(3,monitoringParameterVO.getMonitoingKeyword());
            ps.setString(4,monitoringParameterVO.getMonitoingSubKeyword());
            ps.setString(5,monitoringParameterVO.getMonitoingAlertCategory());
            ps.setString(6,monitoringParameterVO.getMonitoingOnChannel());
            ps.setString(7,monitoringParameterVO.getMonitoingDeviation());
            ps.setString(8,monitoringParameterVO.getMonitoringUnit());
            rs = ps.executeQuery();
            if(rs.next())
            {
                status=true;
            }
        }
        catch (SQLException e)
        {
            status=true;
            logger.error("SQL Exception while adding new Parametr Name",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isParameterNameAvailableBasedOnId()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            status=true;
            logger.error("System Error while  adding new Parameter Name::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isParameterNameAvailableBasedOnId()", null, "Common", "Sql exception while connecting to rule master", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return status;
    }
    public List<TransactionDetailsVO> getConsequtiveSameCardSameAmountDetail(TerminalVO terminalVO, DateVO dateVO, int threshold) /*throws PZDBViolationException*/
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        List<TransactionDetailsVO> transactionDetailsVOs=new ArrayList();
        ResultSet rs = null;
        TransactionDAO transactionDAO=new TransactionDAO();
        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT first_six,last_four,amount,COUNT(*) AS total FROM bin_details AS bd JOIN transaction_common AS t ON bd.icicitransid=t.trackingid AND t.toid=? AND t.terminalid=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? GROUP BY first_six,last_four,amount HAVING total>?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,dateVO.getStartDate());
            pstmt.setString(4,dateVO.getEndDate());
            pstmt.setInt(5, threshold);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                Map<String,TransactionDetailsVO> stringTransactionDetailsVOMap=transactionDAO.getListOfBinTransactionBYAmountMap(terminalVO, dateVO, rs.getString("first_six"), rs.getString("last_four"), String.valueOf(rs.getDouble("amount")));
                Set set=stringTransactionDetailsVOMap.keySet();
                Iterator iterator=set.iterator();
                Map<String,String> stringListMap=new TreeMap();
                while(iterator.hasNext())
                {
                    String trackingId =(String)iterator.next();
                    String expectedNextTrackingId=String.valueOf(Integer.parseInt(trackingId)+1);
                    int count=0;
                    if(stringTransactionDetailsVOMap.containsKey(expectedNextTrackingId))
                    {
                        count=count+1;
                    }
                    stringListMap.put(trackingId,String.valueOf(count));
                }

                List<String> stringList=new ArrayList();
                Set set1=stringListMap.keySet();
                Iterator iterator1=set1.iterator();
                while (iterator1.hasNext())
                {
                    String trackingId=(String)iterator1.next();
                    if("1".equals(stringListMap.get(trackingId)))
                    {
                        stringList.add(trackingId);
                        transactionDetailsVOs.add(stringTransactionDetailsVOMap.get(trackingId));
                    }
                    else
                    {
                        String previousTrackingId=String.valueOf(Integer.parseInt(trackingId) - 1);
                        if("1".equals(stringListMap.get(previousTrackingId)))
                        {
                            transactionDetailsVOs.add(stringTransactionDetailsVOMap.get(trackingId));
                        }
                    }
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::", e);
            //PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getConsequtiveSameCardSameAmountDetail()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            //PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getConsequtiveSameCardSameAmountDetail()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionDetailsVOs;
    }
    public List<TransactionDetailsVO> getListHighRiskAmountRejectedTransaction(TerminalVO terminalVO,DateVO dateVO,double threshold,String rejectReason)
    {
        List<TransactionDetailsVO> transactionDetailsVOs=new ArrayList();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
        Connection conn=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        TransactionDetailsVO transactionDetailsVO=null;
        String status="Rejected";
        String currency=gatewayAccount.getCurrency();
        try
        {
            conn=Database.getRDBConnection();
            StringBuffer query=new StringBuffer("SELECT id,toid,totype,description,orderdescription,terminalid,firstname,lastname,amount,rejectreason,firstsix,lastfour,FROM_UNIXTIME(dtstamp) as transdate FROM transaction_fail_log WHERE toid=? AND terminalid=? AND rejectreason=? AND amount>? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=?");
            pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,rejectReason);
            pstmt.setDouble(4,threshold);
            pstmt.setString(5,dateVO.getStartDate());
            pstmt.setString(6,dateVO.getEndDate());
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                transactionDetailsVO=new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(rs.getString("id"));
                transactionDetailsVO.setDescription(rs.getString("description"));
                transactionDetailsVO.setTransactionTime(rs.getString("transdate"));
                transactionDetailsVO.setToid(rs.getString("toid"));
                transactionDetailsVO.setAmount(rs.getString("amount"));
                transactionDetailsVO.setFirstName(rs.getString("firstname"));
                transactionDetailsVO.setLastName(rs.getString("lastname"));
                transactionDetailsVO.setFirstSix(rs.getString("firstsix"));
                transactionDetailsVO.setLastFour(rs.getString("lastfour"));
                transactionDetailsVO.setStatus(status);
                transactionDetailsVO.setCurrency(currency);
                transactionDetailsVOs.add(transactionDetailsVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::::::::::::::",e);
            //PZExceptionHandler.raiseDBViolationException(MerchantMonitoringDAO.class.getName(), "getListHighRiskAmountRejectedTransaction()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("SystemError:::::::::::::::::",se);
            //PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getListHighRiskAmountRejectedTransaction()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());

        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return  transactionDetailsVOs;
    }

    public TransactionSummaryVO getRFDetailsByTimeStamp(TerminalVO terminalVO,String currentMonthStartDate,String currentMonthEndDate)throws Exception
    {
        TransactionSummaryVO transactionSummaryVO=new TransactionSummaryVO();
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(terminalVO.getAccountId());

        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;

        try
        {
            connection = Database.getRDBConnection();
            String query= "select count(*) as 'count',sum(refundamount) as 'refundamount' from " + tableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and TIMESTAMP >=? and TIMESTAMP <=?  and status='reversed' ";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getAccountId());
            pstmt.setString(3,terminalVO.getPaymodeId());
            pstmt.setString(4,terminalVO.getCardTypeId());
            pstmt.setString(5,currentMonthStartDate);
            pstmt.setString(6,currentMonthEndDate);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                transactionSummaryVO.setCountOfreserveRefund(rs.getInt("count"));
                transactionSummaryVO.setReversedAmount(rs.getDouble("refundamount"));
            }
        }
        catch (SQLException e){
            logger.error("SqlException:::::",e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getRFDetailsByTimeStamp()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (SystemError se){
            logger.error("SystemError:::::",se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(),"getRFDetailsByTimeStamp()",null,"Common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        finally{
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionSummaryVO;
    }
    public int logRuleDetails(String ruleName,String ruleMessage,String actualRatio,String alertThreshold,String suspensionThreshold,String monitroingStartDate,String moniotringEndDate,String alertDate,String terminalId,String alertId)throws Exception
    {
        Connection connection = null;
        PreparedStatement pstmt=null;
        int k=0;
        try
        {
            connection = Database.getConnection();
            String query= "insert into log_monitoring_alerts_details(rule_name,rule_message,actual_ratio,alert_threshold,suspension_threshold,monitroing_start_date,moniotring_end_date,alert_date,terminalid,alert_id,dtstamp)"+"values(?,?,?,?,?,now(),now(),now(),?,?,0)";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,ruleName);
            pstmt.setString(2,ruleMessage);
            pstmt.setString(3,actualRatio);
            pstmt.setString(4,alertThreshold);
            pstmt.setString(5,suspensionThreshold);
            pstmt.setString(6,terminalId);
            pstmt.setString(7,alertId);
            k=pstmt.executeUpdate();
        }
        catch (SQLException e){
            logger.error("SqlException :::::",e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "logRuleDetails()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (SystemError se){
            logger.error("System Error ",se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(),"logRuleDetails()",null,"Common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        finally{
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return k;
    }
    public int logAlert(String alertType,String alertTeam,String memberId,String terminalId,String report)throws Exception
    {
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        int k=0;
        try
        {
            connection = Database.getConnection();
            String query= "insert into log_monitoring_alerts(alert_type,alert_team,memberid,terminalid,report,dtstamp)"+"values(?,?,?,?,?,unix_timestamp(now()))";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,alertType);
            pstmt.setString(2,alertTeam);
            pstmt.setString(3,memberId);
            pstmt.setString(4,terminalId);
            pstmt.setString(5,report);
            k=pstmt.executeUpdate();
            if (k == 1)
            {
                rs = pstmt.getGeneratedKeys();
                if(rs!=null)
                {
                    while(rs.next())
                    {
                        k = rs.getInt(1);
                    }
                }
            }
        }
        catch (SQLException e){
            logger.error("SqlException :::::",e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "logAlert()", null, "Common", "SQLException thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (SystemError se){
            logger.error("System Error ",se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(),"logAlert()",null,"Common","SQLException thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return k;
    }
    public MonitoringAlertDetailVO getAlertDetail(String alertId) throws SystemError
    {
        Connection conn = null;
        ResultSet resultSet=null;
        PreparedStatement pstmt=null;
        StringBuffer query =null;
        MonitoringAlertDetailVO monitoringAlertDetailVO=new MonitoringAlertDetailVO();
        try {
            conn = Database.getRDBConnection();
            query = new StringBuffer("SELECT lma.*,lmad.* FROM log_monitoring_alerts AS lma LEFT JOIN log_monitoring_alerts_details AS lmad ON lma.id=lmad.alert_id WHERE alert_id=?");
            pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1,alertId);
            resultSet=pstmt.executeQuery();
            while(resultSet.next()){
                monitoringAlertDetailVO.setAlertId(resultSet.getInt("alert_id"));
                monitoringAlertDetailVO.setMonitoringAlertName(resultSet.getString("rule_name"));
                monitoringAlertDetailVO.setAlertMsg(resultSet.getString("rule_message"));
                monitoringAlertDetailVO.setAlertType(resultSet.getString("alert_type"));
                monitoringAlertDetailVO.setAlertTeam(resultSet.getString("alert_team"));
                monitoringAlertDetailVO.setMemberId(resultSet.getString("memberid"));
                monitoringAlertDetailVO.setTerminalId(resultSet.getString("terminalid"));
                monitoringAlertDetailVO.setReportFileName(resultSet.getString("report"));
                monitoringAlertDetailVO.setActualratio(Double.parseDouble(resultSet.getString("actual_ratio")));
                monitoringAlertDetailVO.setAlertThreshold(resultSet.getDouble("alert_threshold"));
                monitoringAlertDetailVO.setSuspensionThreshold(resultSet.getDouble("suspension_threshold"));
                monitoringAlertDetailVO.setMonitroingStartDate(resultSet.getDate("monitroing_start_date"));
                monitoringAlertDetailVO.setMonitroingEndDate(resultSet.getDate("moniotring_end_date"));
                monitoringAlertDetailVO.setAlertDate(resultSet.getDate("alert_date"));
                return monitoringAlertDetailVO;
            }
        }
        catch (SQLException se){
            throw new SystemError(se.toString());
        }
        finally{
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return monitoringAlertDetailVO;
    }

    public List<MonitoringAlertDetailVO> getRuleLogDetail(String alertId)
    {
        MonitoringAlertDetailVO monitoringAlertDetailVO=null;
        List<MonitoringAlertDetailVO> monitoringAlertDetailVOsList=new LinkedList();
        Connection connection = null;
        PreparedStatement p=null;
        ResultSet rs=null;
        try{
            connection = Database.getRDBConnection();
            String query = "SELECT id,rule_name,rule_message,actual_ratio,alert_threshold,suspension_threshold,monitroing_start_date,moniotring_end_date,alert_date,terminalid,alert_id FROM log_monitoring_alerts_details WHERE alert_id=?";
            p = connection.prepareStatement(query);
            p.setString(1, alertId);
             rs = p.executeQuery();
            while (rs.next()){
                monitoringAlertDetailVO=new MonitoringAlertDetailVO();
                monitoringAlertDetailVO.setId(rs.getInt("id"));
                monitoringAlertDetailVO.setMonitoringAlertName(rs.getString("rule_name"));
                monitoringAlertDetailVO.setAlertMsg(rs.getString("rule_message"));
                monitoringAlertDetailVO.setActualratio(rs.getDouble("actual_ratio"));
                monitoringAlertDetailVO.setAlertThreshold(rs.getDouble("alert_threshold"));
                monitoringAlertDetailVO.setSuspensionThreshold(rs.getDouble("suspension_threshold"));
                monitoringAlertDetailVO.setMonitroingStartDate(rs.getDate("monitroing_start_date"));
                monitoringAlertDetailVO.setMonitroingEndDate(rs.getDate("moniotring_end_date"));
                monitoringAlertDetailVO.setAlertDate(rs.getDate("alert_date"));
                monitoringAlertDetailVO.setTerminalId(rs.getString("terminalid"));
                monitoringAlertDetailVO.setAlertId(rs.getInt("alert_id"));
                monitoringAlertDetailVO.setMonitoringAlertPeriod("29 Jan,2018");
                monitoringAlertDetailVOsList.add(monitoringAlertDetailVO);
            }
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::", systemError);
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
        }
        finally{
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(connection);
        }
        return monitoringAlertDetailVOsList;
    }
    public Map<String,List<MonitoringAlertDetailVO>> getRuleLogDetailPerTerminal(String alertId)
    {
        MonitoringAlertDetailVO monitoringAlertDetailVO=null;
        Map<String,List<MonitoringAlertDetailVO>> stringListMap=new HashMap();
        List<MonitoringAlertDetailVO> monitoringAlertDetailVOsList=null;
        String terminalId="";

        Connection connection = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT id,rule_name,rule_message,actual_ratio,alert_threshold,suspension_threshold,monitroing_start_date,moniotring_end_date,alert_date,terminalid,alert_id FROM log_monitoring_alerts_details WHERE alert_id=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, alertId);
            rs = preparedStatement.executeQuery();
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (rs.next())
            {
                terminalId=rs.getString("terminalid");
                monitoringAlertDetailVO=new MonitoringAlertDetailVO();
                monitoringAlertDetailVO.setId(rs.getInt("id"));
                monitoringAlertDetailVO.setMonitoringAlertName(rs.getString("rule_name"));
                monitoringAlertDetailVO.setAlertMsg(rs.getString("rule_message"));
                monitoringAlertDetailVO.setActualratio(rs.getDouble("actual_ratio"));
                monitoringAlertDetailVO.setAlertThreshold(rs.getDouble("alert_threshold"));
                monitoringAlertDetailVO.setSuspensionThreshold(rs.getDouble("suspension_threshold"));
                monitoringAlertDetailVO.setMonitroingStartDate(targetFormat.parse(rs.getString("monitroing_start_date")));
                monitoringAlertDetailVO.setMonitroingEndDate(targetFormat.parse(rs.getString("moniotring_end_date")));
                monitoringAlertDetailVO.setAlertDate(targetFormat.parse(rs.getString("alert_date")));
                monitoringAlertDetailVO.setTerminalId(rs.getString("terminalid"));
                monitoringAlertDetailVO.setAlertId(rs.getInt("alert_id"));
                monitoringAlertDetailVO.setMonitoringAlertPeriod(targetFormat.format(targetFormat.parse(rs.getString("monitroing_start_date")))+"-"+targetFormat.format(targetFormat.parse(rs.getString("moniotring_end_date"))));

                monitoringAlertDetailVOsList=stringListMap.get(terminalId);
                if(monitoringAlertDetailVOsList==null){
                    monitoringAlertDetailVOsList=new ArrayList();
                }
                monitoringAlertDetailVOsList.add(monitoringAlertDetailVO);
                stringListMap.put(terminalId,monitoringAlertDetailVOsList);
            }
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::", systemError);
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
        }
        catch (Exception e){
            logger.error("Exception:::::", e);
        }
        finally{
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return stringListMap;
    }
    public Map<String,List<MonitoringAlertDetailVO>> getRuleLogDetailPerTerminalForResend(String alertId)
    {
        MonitoringAlertDetailVO monitoringAlertDetailVO=null;
        Map<String,List<MonitoringAlertDetailVO>> stringListMap=new HashMap();
        List<MonitoringAlertDetailVO> monitoringAlertDetailVOsList=null;
        String terminalId="";
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT id,rule_name,rule_message,actual_ratio,alert_threshold,suspension_threshold,monitroing_start_date,moniotring_end_date,alert_date,terminalid,alert_id FROM log_monitoring_alerts_details WHERE alert_id=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, alertId);
            rs = preparedStatement.executeQuery();
            while (rs.next())
            {
                terminalId=rs.getString("terminalid");

                monitoringAlertDetailVO=new MonitoringAlertDetailVO();
                monitoringAlertDetailVO.setId(rs.getInt("id"));
                monitoringAlertDetailVO.setMonitoringAlertName(rs.getString("rule_name"));
                monitoringAlertDetailVO.setAlertMsg(rs.getString("rule_message"));
                monitoringAlertDetailVO.setActualratio(rs.getDouble("actual_ratio"));
                monitoringAlertDetailVO.setAlertThreshold(rs.getDouble("alert_threshold"));
                monitoringAlertDetailVO.setSuspensionThreshold(rs.getDouble("suspension_threshold"));
                monitoringAlertDetailVO.setMonitroingStartDate(rs.getDate("monitroing_start_date"));
                monitoringAlertDetailVO.setMonitroingEndDate(rs.getDate("moniotring_end_date"));
                monitoringAlertDetailVO.setAlertDate(rs.getDate("alert_date"));
                monitoringAlertDetailVO.setTerminalId(rs.getString("terminalid"));
                monitoringAlertDetailVO.setAlertId(rs.getInt("alert_id"));
                monitoringAlertDetailVO.setMonitoringAlertPeriod(rs.getDate("monitroing_start_date")+"-"+rs.getDate("moniotring_end_date"));

                monitoringAlertDetailVOsList=stringListMap.get(terminalId);
                if(monitoringAlertDetailVOsList==null){
                    monitoringAlertDetailVOsList=new ArrayList();
                }
                monitoringAlertDetailVOsList.add(monitoringAlertDetailVO);
                stringListMap.put(terminalId,monitoringAlertDetailVOsList);
            }
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::", systemError);
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
        }
        finally{
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return stringListMap;
    }
    public MonitoringAlertDetailVO getLogAlertDetail(String alertId)
    {
        MonitoringAlertDetailVO monitoringAlertDetailVO=null;
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try{
            connection = Database.getRDBConnection();
            String query = "SELECT alert_team,memberid,report FROM log_monitoring_alerts WHERE id=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, alertId);
            rs = preparedStatement.executeQuery();
            if(rs.next()){
                monitoringAlertDetailVO=new MonitoringAlertDetailVO();
                monitoringAlertDetailVO.setMemberId(rs.getString("memberid"));
                monitoringAlertDetailVO.setAlertTeam(rs.getString("alert_team"));
                monitoringAlertDetailVO.setReportFileName(rs.getString("report"));
            }
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::", systemError);
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
        }
        finally{
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return monitoringAlertDetailVO;
    }
}