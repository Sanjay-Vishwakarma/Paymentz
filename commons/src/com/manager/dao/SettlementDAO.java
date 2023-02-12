package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccount;
import com.manager.vo.SettlementDateVO;
import com.manager.vo.TransactionSummaryVO;
import com.manager.vo.morrisBarVOs.Data;
import com.manager.vo.payoutVOs.SettlementCycleVO;
import com.payment.PZTransactionStatus;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.response.PZSettlementRecord;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sandip on 4/19/2017.
 */

public class SettlementDAO
{
    Logger logger = new Logger(SettlementDAO.class.getName());

    public String settleTheTransaction(String trackingId) throws PZDBViolationException
    {
        String status = "";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        try
        {
            con = Database.getConnection();
            String Query = "update transaction_common set status='" + PZTransactionStatus.SETTLED.toString() + "' where trackingid=? ";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, trackingId);
            int k = preparedStatement.executeUpdate();
            if (k > 0)
            {
                status = "success";
            }
            else
            {
                status = "failed";
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(SettlementDAO.class.getName(), "settleTheTransaction", null, "Common", "SQLException while connecting to transaction table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(SettlementDAO.class.getName(), "settleTheTransaction()", null, "Common", "SystemError while connecting to transaction table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }

    public String settleTheTransactionBasedOnTable(String trackingId,String tablename) throws PZDBViolationException
    {
        String status = "";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        try
        {
            con = Database.getConnection();
            String Query = "update "+tablename+" set status='" + PZTransactionStatus.SETTLED.toString() + "' where trackingid=? ";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, trackingId);
            int k = preparedStatement.executeUpdate();
            if (k > 0)
            {
                status = "success";
            }
            else
            {
                status = "failed";
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(SettlementDAO.class.getName(), "settleTheTransaction", null, "Common", "SQLException while connecting to transaction table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(SettlementDAO.class.getName(), "settleTheTransaction()", null, "Common", "SystemError while connecting to transaction table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }

    public String captureTheTransaction(String trackingId, String paymentId, String captureAmount) throws PZDBViolationException
    {
        String status = "";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        try
        {
            con = Database.getConnection();
            String Query = "update transaction_common set paymentid=?,captureamount=?,status='" + PZTransactionStatus.CAPTURE_SUCCESS + "' where trackingid=?";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, paymentId);
            preparedStatement.setString(2, captureAmount);
            preparedStatement.setString(3, trackingId);
            int k = preparedStatement.executeUpdate();
            if (k > 0)
            {
                status = "success";
            }
            else
            {
                status = "failed";
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(SettlementDAO.class.getName(), "captureTheTransaction", null, "Common", "SQLException while connecting to transaction table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(SettlementDAO.class.getName(), "captureTheTransaction()", null, "Common", "SystemError while connecting to transaction table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }

    public String declineTheTransaction(String trackingId, String paymentId) throws PZDBViolationException
    {
        String status = "";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        try
        {
            con = Database.getConnection();
            String Query = "update transaction_common set paymentid=?,captureamount='0.00', status='" + PZTransactionStatus.AUTH_FAILED + "' where trackingid=?";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, paymentId);
            preparedStatement.setString(2, trackingId);
            int k = preparedStatement.executeUpdate();
            if (k > 0)
            {
                status = "success";
            }
            else
            {
                status = "failed";
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(SettlementDAO.class.getName(), "declineTheTransaction", null, "Common", "SQLException while connecting to transaction table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(SettlementDAO.class.getName(), "declineTheTransaction()", null, "Common", "SystemError while connecting to transaction table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }

    public int addSettlementCycleStep1(String accountId, String startDate, String endDate, String partnerId, String isTransactionFileAvailable) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        int settlementCycleId = 0;
        try
        {
            con = Database.getConnection();
            String query = "insert into merchant_settlement_cycle_master(id,accountid,startdate,enddate,partnerid,status,istransactionfileavailable,dtstamp)values(null,?,?,?,?,?,?,UNIX_TIMESTAMP(NOW()))";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, accountId);
            preparedStatement.setString(2, startDate);
            preparedStatement.setString(3, endDate);
            preparedStatement.setString(4, partnerId);
            preparedStatement.setString(5, "Initiated");
            preparedStatement.setString(6, isTransactionFileAvailable);
            int k = preparedStatement.executeUpdate();
            if (k > 0)
            {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next())
                {
                    settlementCycleId = resultSet.getInt(1);
                }

            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(SettlementDAO.class.getName(), "addSettlementCycleStep1()", null, "Common", "SQLException while connecting to merchant_settlement_cycle_master", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(SettlementDAO.class.getName(), "addSettlementCycleStep1()", null, "Common", "SystemError while connecting to merchant_settlement_cycle_master", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return settlementCycleId;
    }

    public boolean insertDataIntoTemp(PZSettlementRecord pzSettlementRecord, String settlementCycleId) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        boolean result = false;
        try
        {
            con = Database.getConnection();
            String query = "insert into temp_settlement_upload(id,trackingid,banktransactionid,status,amount,successfulamount,refundamount,chargebackamount,transactiontime,settlementcycleid,dtstamp)values(null,?,?,?,?,?,?,?,?,?,UNIX_TIMESTAMP(NOW()))";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, pzSettlementRecord.getMerchantTXNid());
            preparedStatement.setString(2, pzSettlementRecord.getPaymentid());
            preparedStatement.setString(3, pzSettlementRecord.getStatusDetail());
            preparedStatement.setString(4, pzSettlementRecord.getAmount());
            preparedStatement.setString(5, pzSettlementRecord.getSuccessfulAmount());
            preparedStatement.setString(6, pzSettlementRecord.getRefundAmount());
            preparedStatement.setString(7, pzSettlementRecord.getChargebackAmount());
            preparedStatement.setString(8, pzSettlementRecord.getTransactionTime());
            preparedStatement.setString(9, settlementCycleId);
            int k = preparedStatement.executeUpdate();
            if (k > 0)
            {
                result = true;
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(SettlementDAO.class.getName(), "insertDataIntoTemp()", null, "Common", "SQLException while connecting to temp_settlement_upload", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(SettlementDAO.class.getName(), "insertDataIntoTemp()", null, "Common", "SystemError while connecting to temp_settlement_upload", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return result;
    }

    public SettlementDateVO getSettlementPeriodFromExcel(String settlementCycleId) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        SettlementDateVO settlementDateVO = null;
        try
        {
            con = Database.getConnection();
            String query = "SELECT MIN(transactiontime) as transactionstarttime ,MAX(transactiontime) as transactionendtime FROM temp_settlement_upload where settlementcycleid=?";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, settlementCycleId);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next())
            {
                settlementDateVO = new SettlementDateVO();
                settlementDateVO.setSettlementStartDate(result.getString("transactionstarttime"));
                settlementDateVO.setSettlementEndDate(result.getString("transactionendtime"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(SettlementDAO.class.getName(), "getSettlementPeriodFromExcel()", null, "Common", "SQLException while connecting to temp_settlement_upload", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(SettlementDAO.class.getName(), "getSettlementPeriodFromExcel()", null, "Common", "SystemError while connecting to temp_settlement_upload", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return settlementDateVO;
    }

    public Hashtable getGatewGayAccountProcessingAmount(GatewayAccount gatewayAccount, String startDate, String endDate, String tableName, String partnerId)
    {
        Connection con = null;
        try
        {
            con = Database.getConnection();
            Functions functions = new Functions();
            StringBuilder query = new StringBuilder("SELECT STATUS,COUNT(*) as count,SUM(amount) AS amount,SUM(captureamount) AS captureamount,SUM(refundamount) AS refundamount,SUM(chargebackamount) AS chargebackamount FROM " + tableName + " AS t JOIN members AS m ON t.toid=m.memberid where m.partnerid IN(" + partnerId + ") ");
            if (functions.isValueNull(startDate))
            {
                query.append(" AND FROM_UNIXTIME(t.dtstamp)>='" + startDate + "'");
            }
            if (functions.isValueNull(endDate))
            {
                query.append(" AND FROM_UNIXTIME(t.dtstamp)<='" + endDate + "'");
            }
            if (gatewayAccount != null && functions.isValueNull(String.valueOf(gatewayAccount.getAccountId())))
            {
                query.append(" AND t.accountid='" + gatewayAccount.getAccountId() + "'");
            }
            query.append(" GROUP BY STATUS");
            logger.debug("query:" + query);
            Hashtable hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), con));
            return hash;
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return null;
    }

    public Hashtable getGatewayAccountProcessingAmount(String startDate, String endDate, String settlementCycleId)
    {
        Connection con = null;
        try
        {
            con = Database.getConnection();
            Functions functions = new Functions();
            StringBuilder query = new StringBuilder("SELECT STATUS,COUNT(*) as count,SUM(amount) AS amount,SUM(captureamount) AS captureamount,SUM(refundamount) AS refundamount,SUM(chargebackamount) AS chargebackamount FROM transaction_common  where trackingid>0 ");
            if (functions.isValueNull(startDate))
            {
                query.append(" AND FROM_UNIXTIME(dtstamp)>='" + startDate + "'");
                //logger.debug("startDate:"+startDate);
            }
            if (functions.isValueNull(endDate))
            {
                query.append(" AND FROM_UNIXTIME(dtstamp)<='" + endDate + "'");
                //logger.debug("endDate:"+endDate);
            }
            query.append(" GROUP BY STATUS");
            logger.debug("query:" + query);
            Hashtable hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), con));
            return hash;
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return null;
    }

    public Hashtable getGatewayAccountProcessingAmountFromTempTable(String startDate, String endDate, String settlementCycleId)
    {
        Connection con = null;
        try
        {
            con = Database.getConnection();
            StringBuilder query = new StringBuilder("SELECT STATUS,COUNT(*) as COUNT,SUM(amount) as amount,SUM(successfulamount) as captureamount,SUM(refundamount) as refundamount,SUM(chargebackamount) as chargebackamount FROM temp_settlement_upload WHERE settlementcycleid=" + settlementCycleId + " GROUP BY STATUS");
            logger.debug("query:::::" + query.toString());
            Hashtable hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), con));
            return hash;
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return null;
    }

    public Hashtable getSettlementTransactions1(GatewayAccount gatewayAccount, String startDate, String endDate, String tableName, String partnerId)
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Hashtable hash = null;
        try
        {
            connection = Database.getConnection();
            StringBuffer query = new StringBuffer("SELECT STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount,SUM(captureamount) AS captureamount,SUM(refundamount) AS refundamount,SUM(chargebackamount) AS chargebackamount FROM " + tableName + " as t JOIN members as m ON t.toid=m.memberid WHERE m.partnerid=" + partnerId + " AND FROM_UNIXTIME(t.dtstamp)>=? AND FROM_UNIXTIME(t.dtstamp)<=? AND t.accountid=? GROUP BY STATUS");
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setString(1, startDate);
            preparedStatement.setString(2, endDate);
            preparedStatement.setInt(3, gatewayAccount.getAccountId());
            resultSet = preparedStatement.executeQuery();
            hash = Database.getHashFromResultSet(resultSet);
            return hash;
        }
        catch (SQLException se)
        {
            logger.error("SQLException--->",se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError--->", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return hash;
    }

    public TransactionSummaryVO getGatewayAccountProcessingAmountFromTempTable(String settlementCycleId)
    {
        Connection con = null;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        try
        {
            con = Database.getConnection();
            StringBuilder query = new StringBuilder("SELECT STATUS,COUNT(*) as count,SUM(amount) as amount,SUM(successfulamount) as successfulamount,SUM(refundamount) as refundamount,SUM(chargebackamount) as chargebackamount FROM temp_settlement_upload WHERE settlementcycleid=? GROUP BY STATUS");
            preparedStatement = con.prepareStatement(query.toString());
            preparedStatement.setString(1, settlementCycleId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                if (PZTransactionStatus.AUTH_FAILED.toString().equals(resultSet.getString("STATUS")))
                {
                    transactionSummaryVO.setAuthfailedAmount(resultSet.getDouble("amount"));
                }
                else if (PZTransactionStatus.SETTLED.toString().equals(resultSet.getString("STATUS")))
                {
                    transactionSummaryVO.setSettledAmount(resultSet.getDouble("successfulamount"));
                }
                else if (PZTransactionStatus.REVERSED.toString().equals(resultSet.getString("STATUS")))
                {
                    transactionSummaryVO.setReversedAmount(resultSet.getDouble("refundamount"));
                }
                else if (PZTransactionStatus.CHARGEBACK.toString().equals(resultSet.getString("STATUS")))
                {
                    transactionSummaryVO.setChargebackAmount(resultSet.getDouble("chargebackamount"));
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionSummaryVO;
    }

    public int removeTempTransactions(String settlementCycleId)
    {
        int result = 0;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try
        {
            String query = "delete from temp_settlement_upload where settlementcycleid=?";
            conn = Database.getConnection();
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, settlementCycleId);
            result = preparedStatement.executeUpdate();
        }
        catch (Exception e)
        {
            logger.error("Exception:::::" + e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return result;
    }

    public int removeSettlementCycle(String settlementCycleId)
    {
        int result = 0;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try
        {
            String query = "delete from merchant_settlement_cycle_master where id=?";
            conn = Database.getConnection();
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, settlementCycleId);
            result = preparedStatement.executeUpdate();
        }
        catch (Exception e)
        {
            logger.error("Exception:::::" + e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return result;
    }

    public SettlementCycleVO getSettlementCycleInfo(String settlementCycleId, String status)
    {
        SettlementCycleVO settlementCycleVO = null;
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement preparedStatement = null;
        try
        {
            String query = "select * from merchant_settlement_cycle_master where status=? and id=? ";
            conn = Database.getConnection();
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, status);
            preparedStatement.setString(2, settlementCycleId);
            result = preparedStatement.executeQuery();
            if (result.next())
            {
                settlementCycleVO = new SettlementCycleVO();
                settlementCycleVO.setStartDate(result.getString("startdate"));
                settlementCycleVO.setEndDate(result.getString("enddate"));
                settlementCycleVO.setAccountId(result.getString("accountid"));
                settlementCycleVO.setPartnerId(result.getString("partnerid"));
                settlementCycleVO.setStatus(result.getString("status"));
                settlementCycleVO.setIsTransactionFileAvailable(result.getString("istransactionfileavailable"));
            }
        }
        catch (Exception e)
        {
            logger.error("Exception:::::" + e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return settlementCycleVO;
    }

    public boolean checkForPendingSettlementCycle(String accountId, String partnerId)
    {
        boolean isPendingSettlementCycle = false;
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement preparedStatement = null;
        try
        {
            String query = "select * from merchant_settlement_cycle_master where status in('Initiated','BankWireGenerated','SettlementUploaded','MerchantWireGenerated') and accountid=? and partnerid=? ";
            conn = Database.getConnection();
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, accountId);
            preparedStatement.setString(2, partnerId);
            result = preparedStatement.executeQuery();
            if (result.next())
            {
                isPendingSettlementCycle = true;
            }
        }
        catch (Exception e)
        {
            logger.error("Exception:::::" + e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isPendingSettlementCycle;
    }

    public SettlementCycleVO getRecentSettlementInfo(String accountId, String partnerId)
    {
        SettlementCycleVO settlementCycleVO = null;
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement preparedStatement = null;
        try
        {
            String query = "select * from merchant_settlement_cycle_master where accountid=? and partnerid=? order by id desc limit 1";
            conn = Database.getConnection();
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, accountId);
            preparedStatement.setString(2, partnerId);
            result = preparedStatement.executeQuery();
            if (result.next())
            {
                settlementCycleVO = new SettlementCycleVO();
                settlementCycleVO.setStartDate(result.getString("startdate"));
                settlementCycleVO.setEndDate(result.getString("enddate"));
                settlementCycleVO.setAccountId(result.getString("accountid"));
                settlementCycleVO.setPartnerId(result.getString("partnerid"));
                settlementCycleVO.setStatus(result.getString("status"));
                settlementCycleVO.setNextCycleDays("10");
            }
        }
        catch (Exception e)
        {
            logger.error("Exception:::::" + e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return settlementCycleVO;
    }

    public boolean checkSettlementDateRange(String accountId, String partnerId, String startDate, String endDate)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        boolean isInvalidSettlementDate = false;
        try
        {
            con = Database.getConnection();
            String Query = "SELECT id FROM merchant_settlement_cycle_master WHERE accountid=? AND partnerid=? AND (((?>=startdate) AND (?<=enddate)) OR ((?>=startdate) AND (?<=enddate)))";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, accountId);
            preparedStatement.setString(2, partnerId);
            preparedStatement.setString(3, startDate);
            preparedStatement.setString(4, startDate);
            preparedStatement.setString(5, endDate);
            preparedStatement.setString(6, endDate);
            rs = preparedStatement.executeQuery();
            logger.error("Query:::::"+preparedStatement);
            if (rs.next())
            {
                isInvalidSettlementDate = true;
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException:::::", se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError::::::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return isInvalidSettlementDate;
    }

    public boolean isFirstSettlementCycle(String accountId, String partnerId)
    {
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement preparedStatement = null;
        boolean isFirstSettlementCycle = true;
        try
        {
            String query = "select id from merchant_settlement_cycle_master where accountid=? and partnerid=?";
            conn = Database.getConnection();
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, accountId);
            preparedStatement.setString(2, partnerId);
            result = preparedStatement.executeQuery();
            if (result.next())
            {
                isFirstSettlementCycle = false;
            }
        }
        catch (Exception e)
        {
            logger.error("Exception:::::" + e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isFirstSettlementCycle;
    }

    public boolean checkForPendingTransactions(String accountId, String partnerId, String startDate, String endDate)
    {
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement preparedStatement = null;
        boolean checkForPendingTransactions = false;
        try
        {
            String query = "SELECT COUNT(*) AS 'count' FROM transaction_common AS t JOIN `members` AS m ON t.toid=m.memberid WHERE m.partnerid=? AND t.accountid=? AND FROM_UNIXTIME(t.dtstamp)>? AND FROM_UNIXTIME(t.dtstamp)<? and t.status in('authstarted','authsuccessful','authfailed','capturestarted','capturesuccess','capturefailed','settled','markedforreversal','reversed','chargeback')";
            conn = Database.getConnection();
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, partnerId);
            preparedStatement.setString(2, accountId);
            preparedStatement.setString(3, startDate);
            preparedStatement.setString(4, endDate);
            result = preparedStatement.executeQuery();
            if (result.next())
            {
                long count = result.getLong("count");
                if (count > 0)
                {
                    checkForPendingTransactions = true;
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Exception:::::" , e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return checkForPendingTransactions;
    }

    public boolean checkForCoverTransactions(String accountId, String partnerId, String startDate, String endDate)
    {
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement preparedStatement = null;
        boolean checkForPendingTransactions = false;
        try
        {
            String query = "SELECT COUNT(*) AS 'count' FROM transaction_common AS t JOIN `members` AS m ON t.toid=m.memberid WHERE m.partnerid=? AND t.accountid=? AND FROM_UNIXTIME(t.dtstamp)>? AND FROM_UNIXTIME(t.dtstamp)<? and t.status in('authstarted','authsuccessful','capturestarted','capturesuccess')";
            conn = Database.getConnection();
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, partnerId);
            preparedStatement.setString(2, accountId);
            preparedStatement.setString(3, startDate);
            preparedStatement.setString(4, endDate);
            result = preparedStatement.executeQuery();
            if (result.next())
            {
                long count = result.getLong("count");
                if (count > 0)
                {
                    checkForPendingTransactions = true;
                }
            }
            logger.debug("checkForCoverTransactions.Query:" + query);
        }
        catch (Exception e)
        {
            logger.error("Exception:::::" + e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return checkForPendingTransactions;
    }

    public String getFirstTransactionDate(String accountId, String toType)
    {
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement preparedStatement = null;
        String firstTransactionDate = null;
        try
        {
            String query = "SELECT MIN(trackingid),FROM_UNIXTIME(dtstamp) AS firsttranssactiondate FROM transaction_common WHERE totype=? AND accountid=?";
            conn = Database.getConnection();
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, toType);
            preparedStatement.setString(2, accountId);
            result = preparedStatement.executeQuery();
            if (result.next())
            {
                firstTransactionDate = result.getString("firsttranssactiondate");
            }
        }
        catch (Exception e)
        {
            logger.error("Exception:::::" + e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return firstTransactionDate;
    }

    public TransactionSummaryVO getSettlementTransactions2(GatewayAccount gatewayAccount, String startDate, String endDate, String tableName)
    {
        Connection con = null;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        try
        {
            con = Database.getConnection();
            StringBuilder query = new StringBuilder("SELECT STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount,SUM(captureamount) AS captureamount,SUM(refundamount) AS refundamount,SUM(chargebackamount) AS chargebackamount FROM " + tableName + " WHERE FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? AND accountid=? GROUP BY STATUS");
            preparedStatement = con.prepareStatement(query.toString());
            preparedStatement.setString(1, startDate);
            preparedStatement.setString(2, endDate);
            preparedStatement.setInt(3, gatewayAccount.getAccountId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                if (PZTransactionStatus.AUTH_FAILED.toString().equals(resultSet.getString("STATUS")))
                {
                    transactionSummaryVO.setAuthfailedAmount(resultSet.getDouble("amount"));
                }
                else if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(resultSet.getString("STATUS")))
                {
                    transactionSummaryVO.setSettledAmount(resultSet.getDouble("captureamount"));
                }
                else if (PZTransactionStatus.REVERSED.toString().equals(resultSet.getString("STATUS")))
                {
                    transactionSummaryVO.setReversedAmount(resultSet.getDouble("refundamount"));
                }
                else if (PZTransactionStatus.CHARGEBACK.toString().equals(resultSet.getString("STATUS")))
                {
                    transactionSummaryVO.setChargebackAmount(resultSet.getDouble("chargebackamount"));
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionSummaryVO;
    }

    public List<PZSettlementRecord> getSettlementTransactions3(GatewayAccount gatewayAccount, String startDate, String endDate, String tableName)
    {
        PZSettlementRecord loadTransResponse = null;
        List<PZSettlementRecord> vList = new ArrayList();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try
        {
            connection = Database.getConnection();
            StringBuffer query = new StringBuffer("SELECT trackingid,paymentid,amount,captureamount,refundamount,chargebackamount,STATUS FROM " + tableName + " WHERE FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? AND accountid=?");
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setString(1, startDate);
            preparedStatement.setString(2, endDate);
            preparedStatement.setInt(3, gatewayAccount.getAccountId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                loadTransResponse = new PZSettlementRecord();

                String paymentId = resultSet.getString("paymentid");
                String strMerchantOrderId = resultSet.getString("trackingid");

                String strTxnAmount = resultSet.getString("amount");
                String strSuccessfulTxnAmount = resultSet.getString("captureamount");
                String strRefundedTxnAmount = resultSet.getString("refundamount");
                String strChargebackTxnAmount = resultSet.getString("chargebackamount");

                String strStatus = resultSet.getString("status");

                if (strStatus.equalsIgnoreCase("capturesuccess"))
                {
                    loadTransResponse.setStatusDetail(PZTransactionStatus.SETTLED.toString());

                }
                else if (strStatus.equalsIgnoreCase("authfailed"))
                {
                    loadTransResponse.setStatusDetail(PZTransactionStatus.AUTH_FAILED.toString());

                }
                else if (strStatus.equalsIgnoreCase("reversed"))
                {
                    loadTransResponse.setStatusDetail(PZTransactionStatus.REVERSED.toString());

                }
                else if (strStatus.equalsIgnoreCase("chargeback"))
                {
                    loadTransResponse.setStatusDetail(PZTransactionStatus.CHARGEBACK.toString());
                }

                loadTransResponse.setAmount(strTxnAmount);
                loadTransResponse.setMerchantTXNid(strMerchantOrderId);
                loadTransResponse.setPaymentid(paymentId);
                loadTransResponse.setAmount(strTxnAmount);
                loadTransResponse.setSuccessfulAmount(strSuccessfulTxnAmount);
                loadTransResponse.setRefundAmount(strRefundedTxnAmount);
                loadTransResponse.setChargebackAmount(strChargebackTxnAmount);

                if (loadTransResponse != null)
                {
                    vList.add(loadTransResponse);
                }
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException--->", se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError--->", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return vList;
    }
}
