package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.vo.TerminalVO;
import com.manager.vo.payoutVOs.MerchantWireVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Sandip on 6/15/2017.
 */
public class WiresDAO
{
    private static Logger logger = new Logger(WiresDAO.class.getName());
    public MerchantWireVO getMerchantRecentWire(TerminalVO terminalVO)throws PZDBViolationException
    {
        MerchantWireVO merchantWireVO=null;
        String query ="SELECT settledate,firstdate,lastdate FROM merchant_wiremanager WHERE toid=? AND accountid=? and terminalid=? ORDER BY settledid DESC LIMIT 1";
        Connection conn=null;
        ResultSet rs=null;
        PreparedStatement pstmt=null;
        try
        {
            conn = Database.getConnection();
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, terminalVO.getMemberId());
            pstmt.setString(2, terminalVO.getAccountId());
            pstmt.setString(3, terminalVO.getTerminalId());
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                merchantWireVO=new MerchantWireVO();
                merchantWireVO.setSettleDate(rs.getString("settledate"));
                merchantWireVO.setSettlementStartDate(rs.getString("firstdate"));
                merchantWireVO.setSettlementEndDate(rs.getString("lastdate"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getMerchantRecentWire()", null, "Common", "SqlException while connecting to merchant_wiremanager table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getMerchantRecentWire()", null, "Common", "SystemError while connecting to merchant_wiremanager table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return merchantWireVO;
    }
}
