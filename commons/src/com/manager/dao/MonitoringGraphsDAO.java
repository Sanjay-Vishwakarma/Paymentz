package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.manager.DateManager;
import com.manager.MerchantMonitoringManager;
import com.manager.enums.TransReqRejectCheck;
import com.manager.utils.AccountUtil;
import com.manager.vo.TerminalVO;
import com.manager.vo.TransactionDetailsVO;
import com.manager.vo.TransactionSummaryVO;
import com.manager.vo.merchantmonitoring.DateVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by admin on 8/31/2016.
 */
public class MonitoringGraphsDAO
{
    private static Logger logger =new Logger(MonitoringGraphsDAO.class.getName());
    //Method 1:Get Data for Decline Authorization
    public TransactionSummaryVO getDataForDataForDeclineAuthorization(TerminalVO terminalVO, String startDate, String endDate)throws Exception
    {
        TransactionSummaryVO transactionSummaryVO =null;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(terminalVO.getAccountId());
        try
        {
            connection = Database.getRDBConnection();
            String query = "select count(*) as 'count', sum(amount) as amount, sum(captureamount) as captureamount, sum(refundamount) as refundamount, sum(chargebackamount) as chargebackamount,status FROM " + tableName + " where toid=? AND terminalid=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? group by STATUS";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2, terminalVO.getTerminalId());
            pstmt.setString(3, startDate);
            pstmt.setString(4, endDate);
            rs = pstmt.executeQuery();
            if(rs.next())
            {
                transactionSummaryVO=new TransactionSummaryVO();
                do
                {
                    if("authfailed".equals(rs.getString("status")))
                    {
                        transactionSummaryVO.setCountOfAuthfailed(rs.getLong("count"));
                        transactionSummaryVO.setAuthfailedAmount(rs.getDouble("amount"));
                    }
                    else if("authsuccessful".equals(rs.getString("status")))
                    {
                        transactionSummaryVO.setAuthSuccessCount(rs.getLong("count"));
                        transactionSummaryVO.setAuthSuccessAmount(rs.getDouble("amount"));
                    }
                    else if("capturesuccess".equals(rs.getString("status")))
                    {
                        transactionSummaryVO.setCaptureSuccessCount(rs.getLong("count"));
                        transactionSummaryVO.setCaptureSuccessAmount(rs.getDouble("amount"));
                    }
                    else if("chargeback".equals(rs.getString("status")))
                    {
                        transactionSummaryVO.setCountOfChargeback(rs.getLong("count"));
                        transactionSummaryVO.setChargebackAmount(rs.getLong("amount"));
                    }
                    else if("markedforreversal".equals(rs.getString("status")))
                    {
                        transactionSummaryVO.setMarkForReversalCount(rs.getLong("count"));
                        transactionSummaryVO.setMarkForReversalAmount(rs.getDouble("amount"));
                    }
                    else if("reversed".equals(rs.getString("status")))
                    {
                        transactionSummaryVO.setCountOfReversed(rs.getLong("count"));
                        transactionSummaryVO.setReversedAmount(rs.getLong("amount"));
                    }
                    else if("settled".equals(rs.getString("status")))
                    {
                        transactionSummaryVO.setCountOfSettled(rs.getLong("count"));
                        transactionSummaryVO.setSettledAmount(rs.getLong("amount"));
                    }
                }while (rs.next());
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::"+se.getMessage());
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getDataForDataForDeclineAuthorization()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::"+e.getMessage());
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getDataForDataForDeclineAuthorization()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionSummaryVO;
    }
    //Method 1:Get Data for Chargeback ratio
    public TransactionSummaryVO getDataForDataForChargebackRatio(TerminalVO terminalVO, String startDate, String endDate)throws PZDBViolationException
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        double totalProcessingAmount=0.00;
        long totalProcessingCount=0;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(terminalVO.getAccountId());
        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            String query = "SELECT STATUS, COUNT(*) AS 'count', SUM(amount)AS amount,sum(captureamount) as captureamount,SUM(refundamount)AS refundamount, SUM(chargebackamount)AS chargebackamount FROM " + tableName + " WHERE toid=? and terminalid=? AND STATUS IN ('authsuccessful','capturesuccess','settled','authfailed','chargeback','markedforreversal','reversed') AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? GROUP BY STATUS";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, terminalVO.getMemberId());
            pstmt.setString(2, terminalVO.getTerminalId());
            pstmt.setString(3, startDate);
            pstmt.setString(4, endDate);
            logger.debug("query:::::"+pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
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
                    totalProcessingAmount = totalProcessingAmount + rs.getDouble("captureamount");

                    transactionSummaryVO.setCountOfChargeback(rs.getInt("count"));
                    transactionSummaryVO.setChargebackAmount(rs.getDouble("chargebackamount"));
                }
                else if("markedforreversal".equals(rs.getString("status")))
                {
                    totalProcessingCount=totalProcessingCount+rs.getLong("count");
                    totalProcessingAmount = totalProcessingAmount + rs.getDouble("captureamount");

                    transactionSummaryVO.setMarkForReversalCount(rs.getLong("count"));
                    transactionSummaryVO.setMarkForReversalAmount(rs.getDouble("amount"));
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
        catch(SystemError se)
        {
            logger.error("SystemError::::::" , se);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getDataForDataForChargebackRatio()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch(SQLException e)
        {
            logger.error("SQLException::::::" , e);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getDataForDataForChargebackRatio()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionSummaryVO;
    }
    public TransactionSummaryVO getRefundAndChargebackDataByTimeStamp(TerminalVO terminalVO, String startDate, String endDate)throws PZDBViolationException
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        double totalProcessingAmount=0.00;
        long totalProcessingCount=0;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(terminalVO.getAccountId());
        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            String query = "SELECT STATUS, COUNT(*) AS 'count',SUM(refundamount)AS refundamount, SUM(chargebackamount)AS chargebackamount FROM " + tableName + " WHERE toid=? AND terminalid=? AND STATUS IN ('chargeback','reversed') AND TIMESTAMP>=? AND TIMESTAMP<=? GROUP BY STATUS";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, terminalVO.getMemberId());
            pstmt.setString(2, terminalVO.getTerminalId());
            pstmt.setString(3, startDate);
            pstmt.setString(4, endDate);
            logger.debug("query:::::"+pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                if("chargeback".equals(rs.getString("status")))
                {
                    transactionSummaryVO.setCountOfChargeback(rs.getInt("count"));
                    transactionSummaryVO.setChargebackAmount(rs.getDouble("chargebackamount"));
                }
                else if("reversed".equals(rs.getString("status")))
                {
                    transactionSummaryVO.setCountOfReversed(rs.getInt("count"));
                    transactionSummaryVO.setReversedAmount(rs.getDouble("refundamount"));
                }
            }
        }
        catch(SystemError se)
        {
            logger.error("SystemError::::::" , se);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getDataForDataForChargebackRatio()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch(SQLException e)
        {
            logger.error("SQLException::::::" , e);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getDataForDataForChargebackRatio()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return transactionSummaryVO;

    }
    //Method 2: High Foreign Sales
    public HashMap getDataForDataForForeignSales(TerminalVO terminalVO, String startDate, String endDate)throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(terminalVO.getAccountId());
        double totalAmount = 0.00;
        long totalCount = 0;
        Functions functions=new Functions();
        HashMap<String,Double> hashtable = new HashMap();

        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            String query = "SELECT country, COUNT(*) AS 'count', SUM(amount)AS amount FROM " + tableName + " WHERE toid=? AND accountid=? AND paymodeid=? AND cardtypeid=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? and status in ('authsuccessful','capturesuccess','chargeback','markedforreversal','reversed','settled') AND country!=' ' AND country IS NOT NULL GROUP BY country";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, terminalVO.getMemberId());
            pstmt.setString(2, terminalVO.getAccountId());
            pstmt.setString(3, terminalVO.getPaymodeId());
            pstmt.setString(4, terminalVO.getCardTypeId());
            pstmt.setString(5, startDate);
            pstmt.setString(6, endDate);
            rs = pstmt.executeQuery();
            logger.debug("query for high foreign sales::::" + query);
            while (rs.next())
            {
                totalAmount = totalAmount + rs.getDouble("amount");
                totalCount = totalCount + rs.getLong("count");
                if(functions.isValueNull(rs.getString("country")))
                {
                    String countryCode = rs.getString("country");
                    if (countryCode != null && countryCode != "" && countryCode.length() > 2)
                    {
                        countryCode = countryCode.substring(0, 2);
                    }
                    if (hashtable.containsKey(countryCode))
                    {
                        double localTotal =hashtable.get(countryCode);
                        localTotal = localTotal + rs.getDouble("amount");
                        hashtable.put(countryCode, localTotal);
                    }
                    else
                    {
                        hashtable.put(countryCode, rs.getDouble("amount"));
                    }
                }
                else
                {
                    if (hashtable.containsKey("Others"))
                    {
                        double localTotal = hashtable.get("Others");
                        localTotal = localTotal + rs.getDouble("amount");
                        hashtable.put("Others", localTotal);
                    }
                    else
                    {
                        hashtable.put("Others", rs.getDouble("amount"));
                    }

                }
            }
            hashtable.put("totalAmount",totalAmount);
            logger.debug("high foreign sales data hash:::::" + hashtable);
        }
        catch(SystemError se)
        {
            logger.error("SystemError::::::" , se);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getDataForDataForForeignSales()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch(SQLException e)
        {
            logger.error("SQLException::::::" , e);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getDataForDataForForeignSales()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashtable;
    }
    public long getMatureOperationTransactionsCount(TerminalVO terminalVO,DateVO dateVO,String status,int days) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        long count=0;
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
        GatewayType gatewayType = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
        String tableName = Database.getTableName(gatewayType.getGateway());
        DateManager dateManager=new DateManager();
        DateVO dateVO1 = dateManager.getPreviousDayDateRange(days);
        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            String query = "SELECT count(*) as 'count' FROM " + tableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and status=? and TIMESTAMP>=? and TIMESTAMP <=? and from_unixtime(dtstamp)<?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, terminalVO.getMemberId());
            pstmt.setString(2, terminalVO.getAccountId());
            pstmt.setString(3, terminalVO.getPaymodeId());
            pstmt.setString(4, terminalVO.getCardTypeId());
            pstmt.setString(5, status);
            pstmt.setString(6, dateVO.getStartDate());
            pstmt.setString(7, dateVO.getEndDate());
            pstmt.setString(8, dateVO1.getStartDate());
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                count=rs.getLong("count");
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getMatureOperationTransactionsCount()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getMatureOperationTransactionsCount()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return count;
    }
    //@Method 3: TransDetailsFromBlackListedCountry
    public long getTransDetailsFromBlackListedCountry(TerminalVO terminalVO,DateVO dateVO,String country)throws PZDBViolationException
    {
        //System.out.println("country::::::"+country);
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        long l = 0;
        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            String query = "SELECT COUNT(*) AS 'count' FROM transaction_fail_log WHERE toid=? AND terminalid=? AND COUNTRY=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? AND rejectreason=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,country);
            pstmt.setString(4,dateVO.getStartDate());
            pstmt.setString(5,dateVO.getEndDate());
            pstmt.setString(6,TransReqRejectCheck.CUSTOMER_BLACKLISTED_COUNTRY.toString());
            logger.error("Country Query::::"+pstmt);
            rs = pstmt.executeQuery();

            while (rs.next())
            {
                l=rs.getLong("count");
            }
        }
        catch(SystemError se)
        {
            logger.error("SystemError::::::" , se);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getTransDetailsFromBlackListedCountry()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch(SQLException e)
        {
            logger.error("SQLException::::::" , e);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getTransDetailsFromBlackListedCountry()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Exception::::::", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return l;
    }

    //Method 4:
    public List<String> getListOfBlacklistedCountry()throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<String> list = new ArrayList<String>();
        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            String query = "SELECT country_code FROM blacklist_country";
            pstmt = connection.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                list.add(rs.getString("country_code"));
            }
        }
        catch(SystemError se)
        {
            logger.error("SystemError::::::" , se);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getListofBlacklistedCountry()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch(SQLException e)
        {
            logger.error("SQLException::::::" , e);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getListofBlacklistedCountry()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return list;
    }

    //Method 5: Transaction from Blacklisted IP
    public long getTransDetailsFromBlackListedIP(TerminalVO terminalVO,DateVO dateVO, String cardholderIp)throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        long l = 0;
        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            String query = "SELECT COUNT(*) AS 'count' FROM transaction_fail_log WHERE toid=? AND terminalid=? AND cardholderip=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? and rejectreason=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,cardholderIp);
            pstmt.setString(4,dateVO.getStartDate());
            pstmt.setString(5,dateVO.getEndDate());
            pstmt.setString(6, TransReqRejectCheck.CUSTOMER_BLACKLISTED_IP.toString());
            logger.error("IP Query::::"+pstmt);
            rs = pstmt.executeQuery();

            while (rs.next())
            {
                l=rs.getLong("count");
            }
        }
        catch(SystemError se)
        {
            logger.error("SystemError::::::" , se);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getTransDetailsFromBlackListedIP()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch(SQLException e)
        {
            logger.error("SQLException::::::" , e);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getTransDetailsFromBlackListedIP()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            logger.error("SQLException::::::", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return l;
    }

    //Method 6:
    public List<String> getListOfBlacklistedIP()throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<String> list = new ArrayList<String>();
        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            String query = "SELECT ipaddress FROM blacklist_ip";
            pstmt = connection.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                list.add(rs.getString("ipaddress"));
            }
        }
        catch(SystemError se)
        {
            logger.error("SystemError::::::" , se);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getListOfBlacklistedIP()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch(SQLException e)
        {
            logger.error("SQLException::::::" , e);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getListOfBlacklistedIP()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return list;
    }

    //Method 7: Transaction from Blacklisted Card
    public long getTransDetailsFromBlackListedCard(TerminalVO terminalVO,DateVO dateVO, String first_six, String last_four)throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        long l = 0;
        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            String query = "SELECT COUNT(*) AS 'count' FROM transaction_fail_log WHERE toid=? AND terminalid=? AND firstsix=? AND lastfour=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? AND rejectreason=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,first_six);
            pstmt.setString(4,last_four);
            pstmt.setString(5,dateVO.getStartDate());
            pstmt.setString(6,dateVO.getEndDate());
            pstmt.setString(7,TransReqRejectCheck.CUSTOMER_BLACKLISTED_CARD.toString());
            logger.error("Card Query::::"+pstmt);
            rs = pstmt.executeQuery();

            if (rs.next())
            {
                l = rs.getLong("count");
            }
        }
        catch(SystemError se)
        {
            logger.error("SystemError::::::" , se);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getTransDetailsFromBlackListedCard()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch(SQLException e)
        {
            logger.error("SQLException::::::" , e);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getTransDetailsFromBlackListedCard()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Exception::::::", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return l;
    }

    //Method 8:
    public List<String> getListOfBlacklistedCard()throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<String> list = new ArrayList<String>();
        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            String query = "SELECT first_six, last_four FROM blacklist_cards";
            pstmt = connection.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                String first_six = rs.getString("first_six");
                String last_four = rs.getString("last_four");
                String cardnumber = first_six +":"+ last_four;
                list.add(cardnumber);
            }
        }
        catch(SystemError se)
        {
            logger.error("SystemError::::::" , se);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getListOfBlacklistedCard()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch(SQLException e)
        {
            logger.error("SQLException::::::" , e);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getListOfBlacklistedCard()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return list;
    }

    //Method 9: Transaction from Blacklisted Emails
    public long getTransDetailsFromBlackListedEmails(TerminalVO terminalVO,DateVO dateVO,String emailAddress)throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        long l = 0;
        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            String query = "SELECT COUNT(*) AS 'count' FROM transaction_fail_log WHERE toid=? AND terminalid=? AND email=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? AND rejectreason=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,emailAddress);
            pstmt.setString(4,dateVO.getStartDate());
            pstmt.setString(5,dateVO.getEndDate());
            pstmt.setString(6,TransReqRejectCheck.CUSTOMER_BLACKLISTED_EMAIL.toString());
            logger.error("Email Query::::"+pstmt);
            rs = pstmt.executeQuery();

            while (rs.next())
            {
                l = rs.getLong("count");
            }
        }
        catch(SystemError se)
        {
            logger.error("SystemError::::::" , se);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getTransDetailsFromBlackListedEmails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch(SQLException e)
        {
            logger.error("SQLException::::::" , e);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getTransDetailsFromBlackListedEmails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Exception::::::" , e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return l;
    }

    //Method 10:
    public List<String> getListOfBlacklistedEmails()throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<String> list = new ArrayList<String>();
        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            String query = "SELECT emailAddress FROM blacklist_email";
            pstmt = connection.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                list.add(rs.getString("emailAddress"));
            }
        }
        catch(SystemError se)
        {
            logger.error("SystemError::::::" , se);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getListOfBlacklistedEmails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch(SQLException e)
        {
            logger.error("SQLException::::::" , e);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getListOfBlacklistedEmails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return list;
    }

    //Method 11: Transaction from Blacklisted Names
    public long getTransDetailsFromBlackListedNames(TerminalVO terminalVO,DateVO dateVO,String firstName, String lastName)throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        long l = 0;
        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            String query = "SELECT COUNT(*) AS 'count' FROM transaction_fail_log WHERE toid=? AND terminalid=? AND firstname=? AND lastname=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? AND rejectreason=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,firstName.trim());
            pstmt.setString(4,lastName.trim());
            pstmt.setString(5,dateVO.getStartDate());
            pstmt.setString(6,dateVO.getEndDate());
            pstmt.setString(7,TransReqRejectCheck.CUSTOMER_BLACKLISTED_NAME.toString());
            logger.error("Name Query::::"+pstmt);
            rs = pstmt.executeQuery();

            while (rs.next())
            {
                l = rs.getLong("count");
            }
        }
        catch(SystemError se)
        {
            logger.error("SystemError::::::" , se);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getTransDetailsFromBlackListedNames()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch(SQLException e)
        {
            logger.error("SQLException::::::" , e);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getTransDetailsFromBlackListedNames()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Exception::::::" , e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return l;
    }
    //Method 12:
    public List<String> getListOfBlacklistedNames()throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<String> list = new ArrayList<String>();
        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            String query = "SELECT name FROM blacklist_name";
            pstmt = connection.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                list.add(rs.getString("name"));
            }
        }
        catch(SystemError se)
        {
            logger.error("SystemError::::::" , se);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getListOfBlacklistedNames()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch(SQLException e)
        {
            logger.error("SQLException::::::" , e);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getListOfBlacklistedNames()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return list;
    }
    //Method 12:
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
            //conn = Database.getConnection();
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
    public String getMerchantFirstSubmission(TerminalVO terminalVO)throws PZDBViolationException
    {
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(terminalVO.getAccountId());
        String strQuery = null;
        String memberFirstTransactionDate = null;
        ResultSet rsPayout = null;
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            strQuery = "SELECT FROM_UNIXTIME(dtstamp) AS 'firsttransdate' FROM " + tableName + " WHERE  trackingid=(select MIN(trackingid) from " +tableName+ " where toid=? and accountid=? and paymodeid=? and cardtypeid=?)";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getPaymodeId());
            preparedStatement.setString(4, terminalVO.getCardTypeId());
            logger.debug("query::::::::"+preparedStatement);
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
    public Hashtable getDataForPendingTransactions(TerminalVO terminalVO)throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Hashtable hashtable = new Hashtable();
        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            String query = "SELECT STATUS as 'transstatus',COUNT(*) as 'count' FROM `transaction_common` WHERE toid=? AND terminalid=? AND STATUS IN ('authstarted','capturestarted','markedforreversal','cancelstarted') GROUP BY STATUS";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            rs = pstmt.executeQuery();
            if(rs.next())
            {
                do
                {
                    if("authstarted".equals(rs.getString("transstatus")))
                    {
                        hashtable.put("authstarted",rs.getString("count"));
                    }
                    else if("capturestarted".equals(rs.getString("transstatus")))
                    {
                        hashtable.put("capturestarted",rs.getString("count"));
                    }
                    else if("markedforreversal".equals(rs.getString("transstatus")))
                    {
                        hashtable.put("markedforreversal",rs.getString("count"));
                    }
                    else if("cancelstarted".equals(rs.getString("transstatus")))
                    {
                        hashtable.put("cancelstarted",rs.getString("count"));
                    }
                }
                while (rs.next());
                if(!(hashtable.containsKey("authstarted")))
                {
                    hashtable.put("authstarted","0");
                }
                if(!(hashtable.containsKey("capturestarted")))
                {
                    hashtable.put("capturestarted","0");
                }
                if(!(hashtable.containsKey("markedforreversal")))
                {
                    hashtable.put("markedforreversal","0");
                }
                if(!(hashtable.containsKey("cancelstarted")))
                {
                    hashtable.put("cancelstarted","0");
                }
            }
            else
            {
                hashtable.put("authstarted","0");
                hashtable.put("capturestarted","0");
                hashtable.put("markedforreversal","0");
                hashtable.put("cancelstarted","0");
            }
        }
        catch(SystemError se)
        {
            logger.error("SystemError::::::" , se);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getDataForPendingTransactions()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch(SQLException e)
        {
            logger.error("SQLException::::::" , e);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getDataForPendingTransactions()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashtable;
    }

    public HashMap getTransactionCountBasedOnBinCategory(TerminalVO terminalVO, DateVO dateVO,int[] categoryArray1,int[] categoryArray2,int[] categoryArray3,int[] categoryArray4)throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap hashMap = new HashMap();

        long categoryCounter1 = 0;
        long categoryCounter2 = 0;
        long categoryCounter3 = 0;
        long categoryCounter4 = 0;

        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            String query = "SELECT first_six,last_four,COUNT(*) AS 'count' FROM bin_details AS bd JOIN transaction_common AS t ON bd.icicitransid=t.trackingid AND t.toid=? AND t.terminalid=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? GROUP BY first_six,last_four";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,dateVO.getStartDate());
            pstmt.setString(4,dateVO.getEndDate());
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                if (rs.getLong("count") > categoryArray1[0] && rs.getLong("count") <= categoryArray1[1])
                {
                    categoryCounter1 = categoryCounter1 + 1;
                }
                else if (rs.getLong("count") >= categoryArray2[0] && rs.getLong("count") <= categoryArray2[1])
                {
                    categoryCounter2 = categoryCounter2+1;
                }
                else if (rs.getLong("count") >= categoryArray3[0] && rs.getLong("count") <= categoryArray3[1])
                {
                    categoryCounter3 = categoryCounter3+1;
                }
                else if (rs.getLong("count") >= categoryArray4[0])
                {
                    categoryCounter4 = categoryCounter4+1;
                }
            }
            hashMap.put("categoryCounter1", categoryCounter1);
            hashMap.put("categoryCounter2", categoryCounter2);
            hashMap.put("categoryCounter3", categoryCounter3);
            hashMap.put("categoryCounter4", categoryCounter4);
        }
        catch (SQLException e)
        {
            logger.error("SqlException :::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTransactionCountBasedOnBinCategory()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTransactionCountBasedOnBinCategory()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashMap;
    }
    public HashMap getCardCountBasedOnBinAndAmountCategory(TerminalVO terminalVO, DateVO dateVO,int[] categoryArray1,int[] categoryArray2,int[] categoryArray3,int[] categoryArray4)throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap hashMap = new HashMap();

        long categoryCounter1 = 0;
        long categoryCounter2 = 0;
        long categoryCounter3 = 0;
        long categoryCounter4 = 0;
        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            //String query = "SELECT first_six,last_four,amount,COUNT(*) AS 'count' FROM bin_details AS bd JOIN transaction_common AS t ON bd.icicitransid=t.trackingid AND t.toid=? AND t.terminalid=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? GROUP BY first_six,last_four,amount";
            String query = "SELECT first_six,last_four,amount,COUNT(*) AS 'count' FROM bin_details AS bd JOIN transaction_common AS t ON bd.icicitransid=t.trackingid WHERE t.toid=? AND t.terminalid=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? AND bd.`last_four`!='' GROUP BY bd.first_six,bd.last_four,t.amount";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,dateVO.getStartDate());
            pstmt.setString(4,dateVO.getEndDate());
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                double catagory1 = categoryArray1[1] + 0.99;
                double catagory2 = categoryArray2[1] + 0.99;
                double catagory3 = categoryArray3[1] + 0.99;
                if (rs.getDouble("amount") > categoryArray1[0] && rs.getDouble("amount") <= catagory1)
                {
                    categoryCounter1 = categoryCounter1 + 1;
                }
                else if (rs.getDouble("amount") >= categoryArray2[0] && rs.getDouble("amount") <= catagory2)
                {
                    categoryCounter2 = categoryCounter2+1;
                }
                else if (rs.getDouble("amount") >= categoryArray3[0] && rs.getDouble("amount") <= catagory3)
                {
                    categoryCounter3 = categoryCounter3+1;
                }
                else if (rs.getDouble("amount") >= categoryArray4[0])
                {
                    categoryCounter4 = categoryCounter4+1;
                }
            }
            hashMap.put("categoryCounter1", categoryCounter1);
            hashMap.put("categoryCounter2", categoryCounter2);
            hashMap.put("categoryCounter3", categoryCounter3);
            hashMap.put("categoryCounter4", categoryCounter4);
        }
        catch (SQLException e)
        {
            logger.error("SqlException :::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getCardCountBasedOnBinAndAmountCategory()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getCardCountBasedOnBinAndAmountCategory()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashMap;
    }
    public HashMap getTransactionCountBasedOnAmountCategory(TerminalVO terminalVO, DateVO dateVO,int[] categoryArray1,int[] categoryArray2,int[] categoryArray3,int[] categoryArray4)throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap hashMap = new HashMap();

        long categoryCounter1 = 0;
        long categoryCounter2 = 0;
        long categoryCounter3 = 0;
        long categoryCounter4 = 0;

        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            String query = "SELECT amount,COUNT(*) AS 'count' FROM transaction_common AS t where  t.toid=? AND t.terminalid=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? GROUP BY amount";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,dateVO.getStartDate());
            pstmt.setString(4,dateVO.getEndDate());
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                double catagory1 = categoryArray1[1] + 0.99;
                double catagory2 = categoryArray2[1] + 0.99;
                double catagory3 = categoryArray3[1] + 0.99;
                if (rs.getDouble("amount") > categoryArray1[0] && rs.getDouble("amount") <= catagory1)
                {
                    categoryCounter1 = categoryCounter1 + rs.getLong("count");
                }
                else if (rs.getDouble("amount") >= categoryArray2[0] && rs.getDouble("amount") <= catagory2)
                {
                    categoryCounter2 = categoryCounter2 + rs.getLong("count");
                }
                else if (rs.getDouble("amount") >= categoryArray3[0] && rs.getDouble("amount") <= catagory3)
                {
                    categoryCounter3 = categoryCounter3 + rs.getLong("count");
                }
                else if (rs.getDouble("amount") >= categoryArray4[0])
                {
                    categoryCounter4 = categoryCounter4 + rs.getLong("count");
                }
            }
            hashMap.put("categoryCounter1", categoryCounter1);
            hashMap.put("categoryCounter2", categoryCounter2);
            hashMap.put("categoryCounter3", categoryCounter3);
            hashMap.put("categoryCounter4", categoryCounter4);
        }
        catch (SQLException e)
        {
            logger.error("SqlException :::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTransactionCountBasedOnBinCategory()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTransactionCountBasedOnBinCategory()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashMap;
    }

    public HashMap getTransactionCountBasedOnBinUsedDays(TerminalVO terminalVO,DateVO dateVO,int[] categoryArray1,int[] categoryArray2,int[] categoryArray3,int[] categoryArray4)throws PZDBViolationException
    {
        HashMap hashMap = new HashMap();

        long categoryCounter1 = 0;
        long categoryCounter2 = 0;
        long categoryCounter3 = 0;
        long categoryCounter4 = 0;

        DateManager dateManager = new DateManager();
        MerchantMonitoringManager merchantMonitoringManager = new MerchantMonitoringManager();
        Set<String> todayBinDetails = merchantMonitoringManager.getAllCardsGroupByBins(terminalVO, dateVO);
        //System.out.println("todayBinDetails======"+todayBinDetails);
        List<Set> sets = new ArrayList();
        int thresholdValue =categoryArray1[1];
        int days = thresholdValue;
        boolean actionToBeTaken = false;
        boolean isValidToProceed = true;
        int i = 1;
        while (days > i)
        {
            DateVO previousDayDateRange = dateManager.getPreviousDayDateRange(i);
            //System.out.println("previousDayDateRange start date==="+previousDayDateRange.getStartDate());
            //System.out.println("previousDayDateRange end date==="+previousDayDateRange.getEndDate());
            Set<String> set1 = merchantMonitoringManager.getAllCardsGroupByBins(terminalVO, previousDayDateRange);
            //System.out.println("set1=="+set1);
            i++;
            if (set1.size() > 0)
            {
                sets.add(set1);
            }
            else
            {
                isValidToProceed=false;
                break;
            }

        }
        if(isValidToProceed)
        {
            StringBuffer firstSix = new StringBuffer();
            StringBuffer lastFour = new StringBuffer();
            for (String bin : todayBinDetails)
            {
                boolean status = false;
                for (Set set2 : sets)
                {
                    if (set2.contains(bin))
                    {
                        status = true;
                    }
                    else
                    {
                        status = false;
                        break;
                    }
                }
                if (status)
                {
                    actionToBeTaken = true;
                    if (firstSix.length() > 0)
                    {
                        firstSix.append(",");
                        lastFour.append(",");
                    }
                    firstSix.append(bin.split(":")[0]);
                    lastFour.append(bin.split(":")[1]);
                }

                if (actionToBeTaken && firstSix.length()>0)
                {
                    TransactionDAO transactionDAO = new TransactionDAO();
                    dateVO.setStartDate(dateManager.getPreviousDayDateRange(days).getStartDate());
                    List<TransactionDetailsVO> transactionDetailsVOs = transactionDAO.getListOfBinTransaction(terminalVO, dateVO, firstSix.toString(),lastFour.toString());
                    //System.out.println("transactionDetailsVOs size "+transactionDetailsVOs.size());
                    if (transactionDetailsVOs != null)
                    {
                        categoryCounter1 = transactionDetailsVOs.size();
                    }
                }
            }
        }

        sets = new ArrayList();
        thresholdValue =categoryArray2[1];
        days = thresholdValue;
        actionToBeTaken = false;
        isValidToProceed=true;
        i = 0;
        while (days > i)
        {
            DateVO previousDayDateRange = dateManager.getPreviousDayDateRange(i);
            Set<String> set1 = merchantMonitoringManager.getAllCardsGroupByBins(terminalVO, previousDayDateRange);
            if (set1.size() > 0)
            {
                sets.add(set1);
            }
            else
            {
                isValidToProceed=false;
                break;
            }
            i++;
        }
        if(isValidToProceed)
        {
            StringBuffer firstSix = new StringBuffer();
            StringBuffer lastFour = new StringBuffer();
            for (String bin : todayBinDetails)
            {
                boolean status = false;
                for (Set set2 : sets)
                {
                    if (set2.contains(bin))
                    {
                        status = true;
                    }
                    else
                    {
                        status = false;
                        break;
                    }
                }
                if (status)
                {
                    actionToBeTaken = true;
                    if (firstSix.length() > 0)
                    {
                        firstSix.append(",");
                        lastFour.append(",");
                    }
                    firstSix.append(bin.split(":")[0]);
                    lastFour.append(bin.split(":")[1]);
                }

                if (actionToBeTaken && firstSix.length()>0)
                {
                    TransactionDAO transactionDAO = new TransactionDAO();
                    dateVO.setStartDate(dateManager.getPreviousDayDateRange(days).getStartDate());
                    List<TransactionDetailsVO> transactionDetailsVOs = transactionDAO.getListOfBinTransaction(terminalVO, dateVO, firstSix.toString(),lastFour.toString());
                    if (transactionDetailsVOs != null)
                    {
                        categoryCounter2 = transactionDetailsVOs.size();
                    }
                }
            }
        }

        sets = new ArrayList();
        thresholdValue =categoryArray3[1];
        days = thresholdValue;
        actionToBeTaken = false;
        isValidToProceed=true;
        i = 0;
        while (days > i)
        {
            DateVO previousDayDateRange = dateManager.getPreviousDayDateRange(i);
            Set<String> set1 = merchantMonitoringManager.getAllCardsGroupByBins(terminalVO, previousDayDateRange);
            if (set1.size() > 0)
            {
                sets.add(set1);
            }
            else
            {
                isValidToProceed=false;
                break;
            }
            i++;
        }
        if(isValidToProceed)
        {
            StringBuffer firstSix = new StringBuffer();
            StringBuffer lastFour = new StringBuffer();
            for (String bin : todayBinDetails)
            {
                boolean status = false;
                for (Set set2 : sets)
                {
                    if (set2.contains(bin))
                    {
                        status = true;
                    }
                    else
                    {
                        status = false;
                        break;
                    }
                }
                if (status)
                {
                    actionToBeTaken = true;
                    if (firstSix.length() > 0)
                    {
                        firstSix.append(",");
                        lastFour.append(",");
                    }
                    firstSix.append(bin.split(":")[0]);
                    lastFour.append(bin.split(":")[1]);
                }

                if (actionToBeTaken && firstSix.length()>0)
                {
                    TransactionDAO transactionDAO = new TransactionDAO();
                    dateVO.setStartDate(dateManager.getPreviousDayDateRange(days).getStartDate());
                    List<TransactionDetailsVO> transactionDetailsVOs = transactionDAO.getListOfBinTransaction(terminalVO, dateVO, firstSix.toString(),lastFour.toString());
                    if (transactionDetailsVOs != null)
                    {
                        categoryCounter3 = transactionDetailsVOs.size();
                    }
                }
            }
        }

        sets = new ArrayList();
        thresholdValue =categoryArray4[1];
        days = thresholdValue;
        actionToBeTaken = false;
        isValidToProceed=true;
        i = 0;
        while (days > i)
        {
            DateVO previousDayDateRange = dateManager.getPreviousDayDateRange(i);
            Set<String> set1 = merchantMonitoringManager.getAllCardsGroupByBins(terminalVO, previousDayDateRange);
            if (set1.size() > 0)
            {
                sets.add(set1);
            }
            else
            {
                isValidToProceed=false;
                break;
            }
            i++;
        }
        if(isValidToProceed)
        {
            StringBuffer firstSix = new StringBuffer();
            StringBuffer lastFour = new StringBuffer();
            for (String bin : todayBinDetails)
            {
                boolean status = false;
                for (Set set2 : sets)
                {
                    if (set2.contains(bin))
                    {
                        status = true;
                    }
                    else
                    {
                        status = false;
                        break;
                    }
                }
                if (status)
                {
                    actionToBeTaken = true;
                    if (firstSix.length() > 0)
                    {
                        firstSix.append(",");
                        lastFour.append(",");
                    }
                    firstSix.append(bin.split(":")[0]);
                    lastFour.append(bin.split(":")[1]);
                }

                if (actionToBeTaken && firstSix.length()>0)
                {
                    TransactionDAO transactionDAO = new TransactionDAO();
                    dateVO.setStartDate(dateManager.getPreviousDayDateRange(days).getStartDate());
                    List<TransactionDetailsVO> transactionDetailsVOs = transactionDAO.getListOfBinTransaction(terminalVO, dateVO, firstSix.toString(),lastFour.toString());
                    if (transactionDetailsVOs != null)
                    {
                        categoryCounter4 = transactionDetailsVOs.size();
                    }
                }
            }
        }

        hashMap.put("categoryCounter1", categoryCounter1);
        hashMap.put("categoryCounter2", categoryCounter2);
        hashMap.put("categoryCounter3", categoryCounter3);
        hashMap.put("categoryCounter4", categoryCounter4);
        /*Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap hashMap = new HashMap();

        long categoryCounter1 = 0;
        long categoryCounter2 = 0;
        long categoryCounter3 = 0;
        long categoryCounter4 = 0;
        try
        {
            //Fetch Data For Category-1
            connection = Database.getConnection();
            DateManager dateManager=new DateManager();
            DateVO category1StartDate=dateManager.getPreviousDayDateRange(categoryArray1[1]);
            DateVO dateVO2=dateManager.getPreviousDayDateRange(0);
            String query = "SELECT COUNT(*) AS 'count' FROM bin_details AS bd JOIN transaction_common AS t ON bd.icicitransid=t.trackingid AND t.toid=? AND t.terminalid=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,category1StartDate.getStartDate());
            pstmt.setString(4,dateVO2.getEndDate());
            rs = pstmt.executeQuery();
            if(rs.next())
            {
                if (rs.getLong("count") >0)
                {
                    categoryCounter1=categoryCounter1+rs.getLong("count");
                }
            }

            DateVO category2StartDate=dateManager.getPreviousDayDateRange(categoryArray2[1]);
            query = "SELECT COUNT(*) AS 'count' FROM bin_details AS bd JOIN transaction_common AS t ON bd.icicitransid=t.trackingid AND t.toid=? AND t.terminalid=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,category2StartDate.getStartDate());
            pstmt.setString(4,dateVO2.getEndDate());
            rs =pstmt.executeQuery();
            if(rs.next())
            {
                if (rs.getLong("count") >0)
                {
                    categoryCounter2=rs.getLong("count");
                }
            }
            DateVO category3StartDate=dateManager.getPreviousDayDateRange(categoryArray3[1]);
            query = "SELECT COUNT(*) AS 'count' FROM bin_details AS bd JOIN transaction_common AS t ON bd.icicitransid=t.trackingid AND t.toid=? AND t.terminalid=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,category3StartDate.getStartDate());
            pstmt.setString(4,dateVO2.getEndDate());
            rs = pstmt.executeQuery();
            if(rs.next())
            {
                if (rs.getLong("count") >0)
                {
                    categoryCounter3=rs.getLong("count");
                }
            }

            DateVO category4StartDate=dateManager.getPreviousDayDateRange(categoryArray4[1]);
            query = "SELECT COUNT(*) AS 'count' FROM bin_details AS bd JOIN transaction_common AS t ON bd.icicitransid=t.trackingid AND t.toid=? AND t.terminalid=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,category4StartDate.getStartDate());
            pstmt.setString(4,dateVO2.getEndDate());
            rs = pstmt.executeQuery();
            if(rs.next())
            {
                if (rs.getLong("count") >0)
                {
                    categoryCounter4=rs.getLong("count");
                }
            }
            hashMap.put("categoryCounter1", categoryCounter1);
            hashMap.put("categoryCounter2", categoryCounter2);
            hashMap.put("categoryCounter3", categoryCounter3);
            hashMap.put("categoryCounter4", categoryCounter4);
        }
        catch (SQLException e)
        {
            logger.error("SqlException :::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTransactionCountBasedOnBinUsedDays()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTransactionCountBasedOnBinUsedDays()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }*/
        return hashMap;
    }
    public double[] getMinMaxTransactionAmountByDate(TerminalVO terminalVO,DateVO dateVO)throws PZDBViolationException
    {
        String strQuery = null;
        double[] transactionAmountArr=new double[2];
        ResultSet rs = null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            strQuery = "SELECT MAX(amount) as 'maxamount',MIN(amount) as 'minamount' FROM bin_details AS bd JOIN transaction_common AS t ON bd.icicitransid=t.trackingid AND t.toid=? AND t.terminalid=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=?";
            pstmt = conn.prepareStatement(strQuery);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,dateVO.getStartDate());
            pstmt.setString(4,dateVO.getEndDate());
            rs = pstmt.executeQuery();
            if(rs.next())
            {

                double minTransactionAmount = rs.getDouble("minamount");
                double maxTransactionAmount = rs.getDouble("maxamount");
                transactionAmountArr[1] = maxTransactionAmount;
                if (minTransactionAmount == maxTransactionAmount)
                {
                    transactionAmountArr[0] = 1.00;
                }
                else
                {
                    transactionAmountArr[0] = minTransactionAmount;
                }
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::"+se.getMessage());
            PZExceptionHandler.raiseDBViolationException("MerchantMonitoringDAO.java", "getMinMaxTransactionAmountByDate()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::"+e.getMessage());
            PZExceptionHandler.raiseDBViolationException("MerchantMonitoringDAO.java", "getMinMaxTransactionAmountByDate()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transactionAmountArr;
    }

    public HashMap getBlacklistCountryCountFromTransaction(TerminalVO terminalVO, DateVO dateVO)throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, String> hashMap = new HashMap();

        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT country,COUNT(*) AS 'count' FROM transaction_fail_log WHERE toid=? AND terminalid=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? AND rejectreason=? GROUP BY country";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,dateVO.getStartDate());
            pstmt.setString(4,dateVO.getEndDate());
            pstmt.setString(5,TransReqRejectCheck.CUSTOMER_BLACKLISTED_COUNTRY.toString());
            logger.error("Country Query::::" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
               hashMap.put(rs.getString("country"), rs.getString("count"));
            }
        }
        catch(SystemError se)
        {
            logger.error("SystemError::::::" , se);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getDataForDataForForeignSales()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch(SQLException e)
        {
            logger.error("SQLException::::::" , e);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getDataForDataForForeignSales()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashMap;
    }

    public HashMap getBlacklistIPCountFromTransaction(TerminalVO terminalVO, DateVO dateVO)throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, String> hashMap = new HashMap();

        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT cardholderip,COUNT(*) AS 'count' FROM transaction_fail_log WHERE toid=? AND terminalid=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? AND rejectreason=? GROUP BY cardholderip";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,dateVO.getStartDate());
            pstmt.setString(4,dateVO.getEndDate());
            pstmt.setString(5,TransReqRejectCheck.CUSTOMER_BLACKLISTED_IP.toString());
            logger.error("IP Query::::" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                hashMap.put(rs.getString("cardholderip"), rs.getString("count"));
            }
        }
        catch(SystemError se)
        {
            logger.error("SystemError::::::" , se);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getDataForDataForForeignSales()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch(SQLException e)
        {
            logger.error("SQLException::::::" , e);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getDataForDataForForeignSales()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashMap;
    }

    public HashMap getBlacklistEmailCountFromTransaction(TerminalVO terminalVO, DateVO dateVO)throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, String> hashMap = new HashMap();

        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT email,COUNT(*) AS 'count' FROM transaction_fail_log WHERE toid=? AND terminalid=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? AND rejectreason=? GROUP BY email";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,dateVO.getStartDate());
            pstmt.setString(4,dateVO.getEndDate());
            pstmt.setString(5,TransReqRejectCheck.CUSTOMER_BLACKLISTED_EMAIL.toString());
            logger.error("Email Query::::" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                hashMap.put(rs.getString("email"), rs.getString("count"));
            }
        }
        catch(SystemError se)
        {
            logger.error("SystemError::::::" , se);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getDataForDataForForeignSales()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch(SQLException e)
        {
            logger.error("SQLException::::::" , e);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getDataForDataForForeignSales()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashMap;
    }

    public HashMap getBlacklistNameCountFromTransaction(TerminalVO terminalVO, DateVO dateVO)throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, String> hashMap = new HashMap();

        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT firstname,lastname,COUNT(*) AS 'count' FROM transaction_fail_log WHERE toid=? AND terminalid=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? AND rejectreason=? GROUP BY firstname,lastname";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,dateVO.getStartDate());
            pstmt.setString(4,dateVO.getEndDate());
            pstmt.setString(5,TransReqRejectCheck.CUSTOMER_BLACKLISTED_NAME.toString());
            logger.error("Name Query::::" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                hashMap.put(rs.getString("firstname")+" "+rs.getString("lastname"), rs.getString("count"));
            }
        }
        catch(SystemError se)
        {
            logger.error("SystemError::::::" , se);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getDataForDataForForeignSales()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch(SQLException e)
        {
            logger.error("SQLException::::::" , e);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getDataForDataForForeignSales()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashMap;
    }

    public HashMap getBlacklistCardCountFromTransaction(TerminalVO terminalVO, DateVO dateVO)throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, String> hashMap = new HashMap();

        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT firstsix,lastfour,COUNT(*) AS 'count' FROM transaction_fail_log WHERE toid=? AND terminalid=? AND FROM_UNIXTIME(dtstamp)>=? AND FROM_UNIXTIME(dtstamp)<=? AND rejectreason=? GROUP BY firstsix,lastfour";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,terminalVO.getMemberId());
            pstmt.setString(2,terminalVO.getTerminalId());
            pstmt.setString(3,dateVO.getStartDate());
            pstmt.setString(4,dateVO.getEndDate());
            pstmt.setString(5,TransReqRejectCheck.CUSTOMER_BLACKLISTED_CARD.toString());
            logger.error("Card Query::::" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                hashMap.put(rs.getString("firstsix")+":"+rs.getString("lastfour"), rs.getString("count"));
            }
        }
        catch(SystemError se)
        {
            logger.error("SystemError::::::" , se);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getDataForDataForForeignSales()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch(SQLException e)
        {
            logger.error("SQLException::::::" ,e);
            PZExceptionHandler.raiseDBViolationException("MonitoringGraphsDAO.java", "getDataForDataForForeignSales()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashMap;
    }
}
