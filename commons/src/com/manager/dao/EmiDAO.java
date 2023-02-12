package com.manager.dao;
import com.directi.pg.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahima on 11/5/2018.
 */
public class EmiDAO
{
    private static Logger log = new Logger(EmiDAO.class.getName());
    Functions functions = new Functions();
    public List<EmiVO> listOfEmiForMerchant(String merchantId,String terminal)throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps=null;
        List<EmiVO> emiVOList=new ArrayList<>();
        EmiVO emiVO=null;
        try
        {
            conn=Database.getRDBConnection();
            StringBuffer query=new StringBuffer("SELECT id,FROM_UNIXTIME(startdate,\"%d/%m/%Y %H:%i:%s\") AS startDate, FROM_UNIXTIME(enddate,\"%d/%m/%Y %H:%i:%s\") AS endDate, emi_period,terminalId,enable FROM emi_configuration WHERE memberId=? AND terminalId=?");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1,merchantId);
            ps.setString(2,terminal);
            rs = ps.executeQuery();
            while (rs.next())
            {
                emiVO=new EmiVO();
                emiVO.setId(rs.getString("id"));
                if(functions.isValueNull(rs.getString("startDate")))
                {
                    if(rs.getString("startDate").contains(" "))
                    {
                        String sDT[] = rs.getString("startDate").split(" ");
                        emiVO.setStartDate(sDT[0]);
                        emiVO.setStartTime(sDT[1]);
                    }
                }
                if(functions.isValueNull(rs.getString("endDate")))
                {
                    if(rs.getString("endDate").contains(" "))
                    {
                        String eDT[] = rs.getString("endDate").split(" ");
                        emiVO.setEndDate(eDT[0]);
                        emiVO.setEndTime(eDT[1]);
                    }
                }
                emiVO.setEmiPeriod(rs.getString("emi_period"));
                emiVO.setIsActive(rs.getString("enable"));
                emiVOList.add(emiVO);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("EmiDAO.java", "listOfEmiForMerchant()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("EmiDAO.java", "listOfEmiForMerchant()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return  emiVOList;
    }

    public int addEmiConfig(String fdtstamp,String tdtstamp,String merchantId,String emiPeriod,String terminal,String active)throws PZDBViolationException
    {
        Connection connection=null;
        PreparedStatement pstmt=null;
        int count=0;
        try
        {
            String query="insert into emi_configuration(startdate,enddate,memberId,emi_period,terminalId,enable) values(?,?,?,?,?,?)";
            connection=Database.getConnection();
            pstmt=connection.prepareStatement(query);
            pstmt.setString(1,fdtstamp);
            pstmt.setString(2,tdtstamp);
            pstmt.setString(3,merchantId);
            pstmt.setString(4,emiPeriod);
            pstmt.setString(5,terminal);
            pstmt.setString(6,active);
            count=pstmt.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("EmiDAO.java", "addEmiConfig()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("EmiDAO.java", "addEmiConfig()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
            Database.closePreparedStatement(pstmt);
        }
        return count;
    }

    public int removeDetails(String terminal)throws PZDBViolationException
    {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        int count=0;
        try
        {
            connection=Database.getConnection();
            StringBuffer query=new StringBuffer("delete from emi_configuration WHERE terminalId=?");
            preparedStatement=connection.prepareStatement(query.toString());
            preparedStatement.setString(1,terminal);
            int k=preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            log.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(EmiDAO.class.getName(), "removeDetails()", null, "Common", "Sql exception while connecting to emi_configuration table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            log.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(EmiDAO.class.getName(), "removeDetails()", null, "Common", "Sql exception while connecting to emi_configuration table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return count;
    }
}
