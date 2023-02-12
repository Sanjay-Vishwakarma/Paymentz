package dao;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.vo.MerchantDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Admin on 6/7/2017.
 */
public class TransactionAuthDAO
{
    private static Logger logger = new Logger(TransactionAuthDAO.class.getName());

    public MerchantDetailsVO getMerchantKey(String memberId) throws PZDBViolationException
    {
        Connection conn=null;
        PreparedStatement pstmt=null;
        ResultSet resultSet = null;
        String key = "";
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        try
        {
            conn = Database.getConnection();
            String query1 = "select clkey,login from members where memberid=? ";
            pstmt = conn.prepareStatement(query1);
            pstmt.setString(1,memberId);
            resultSet=pstmt.executeQuery();
            if (resultSet.next())
            {
                merchantDetailsVO.setKey(resultSet.getString("clkey"));
                merchantDetailsVO.setLogin(resultSet.getString("login"));
            }
            logger.debug("merchant key query----"+pstmt);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error in isTokenizationAllowed",systemError);
            PZExceptionHandler.raiseDBViolationException(TransactionAuthDAO.class.getName(), "getMerchantKey()", null, "Common", "System Error while connecting to the member_account_mapping table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception in isTokenizationAllowed",e);
            PZExceptionHandler.raiseDBViolationException(TransactionAuthDAO.class.getName(), "getMerchantKey()", null, "Common", "Sql exception due tyo incorrect query to the member_account_mapping table::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return merchantDetailsVO;
    }
}
