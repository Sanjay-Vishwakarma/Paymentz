package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.vo.ExchangeRatesVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Created by Jitendra on 02/03/2018.
 */
public class CurrencyExchangeDao
{
    private Logger logger=new Logger(CurrencyExchangeDao.class.getName());
    public boolean addExchangeRate(ExchangeRatesVO exchangeRatesVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean status =false;
        int k = 0;
        try{
            conn = Database.getConnection();
            String qry = "insert into currency_exchange_rates(from_currency,to_currency,exchange_rate,dtstamp) VALUES (?,?,?,unix_timestamp(now()))";
            pstmt = conn.prepareStatement(qry);
            pstmt.setString(1,exchangeRatesVO.getFromCurrency());
            pstmt.setString(2,exchangeRatesVO.getToCurrency());
            pstmt.setDouble(3, exchangeRatesVO.getExchangeValue());
            k = pstmt.executeUpdate();
            if(k>0)
            {
                status=true;
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(CurrencyExchangeDao.class.getName(), "addExchangeRate()", null, "Common", "SqlException while processing request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(CurrencyExchangeDao.class.getName(), "addExchangeRate()", null, "Common", "SystemError while processing request", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }
    public  ExchangeRatesVO getExchangeDetails(String mappingId) throws PZDBViolationException, ParseException
    {
        ExchangeRatesVO exchangeratesVO=null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT id,from_currency,to_currency,exchange_rate,FROM_UNIXTIME(dtstamp) AS dtstamp,TIMESTAMP FROM currency_exchange_rates WHERE id=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1,mappingId);
            rs = pstmt.executeQuery();
            if(rs.next())
            {
                exchangeratesVO = new ExchangeRatesVO();
                exchangeratesVO.setId(rs.getInt("id"));
                exchangeratesVO.setFromCurrency(rs.getString("from_currency"));
                exchangeratesVO.setToCurrency(rs.getString("to_currency"));
                exchangeratesVO.setExchangeValue(rs.getDouble("exchange_rate"));
                exchangeratesVO.setCreationTime(rs.getString("dtstamp"));
                exchangeratesVO.setTimestamp(rs.getString("timestamp"));
            }

        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(CurrencyExchangeDao.class.getName(), "getExchangeDetails()", null, "Common", "SqlException while processing request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(CurrencyExchangeDao.class.getName(), "getExchangeDetails()", null, "Common", "SystemError while processing request", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return exchangeratesVO;
    }
    public boolean updateExchangeDetails(String mappingId,String exchangeRate)throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt=null;
        boolean status=false;
        try
        {
            ExchangeRatesVO exchangeratesVO=null;
            connection = Database.getConnection();
            String query="update currency_exchange_rates set exchange_rate=? where id=? ";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, exchangeRate);
            pstmt.setString(2,mappingId);
            int k=pstmt.executeUpdate();
            if(k>0)
            {
                status=true;
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(CurrencyExchangeDao.class.getName(), "updateExchangeDetails()", null, "Common", "SqlException while processing request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(CurrencyExchangeDao.class.getName(), "updateExchangeDetails()", null, "Common", "SystemError while processing request", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return status;
    }
    public  boolean checkAvailability(ExchangeRatesVO exchangeRatesVO)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        boolean result=false;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT id FROM currency_exchange_rates WHERE from_currency=? and to_currency=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1,exchangeRatesVO.getFromCurrency());
            pstmt.setString(2,exchangeRatesVO.getToCurrency());
            rs = pstmt.executeQuery();
            if(rs.next()){
                result=true;
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(CurrencyExchangeDao.class.getName(), "checkAvailability()", null, "Common", "SqlException while processing request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(CurrencyExchangeDao.class.getName(), "checkAvailability()", null, "Common", "SystemError while processing request", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally{
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return result;
    }
}
