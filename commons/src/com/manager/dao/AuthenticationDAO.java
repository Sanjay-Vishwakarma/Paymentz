package com.manager.dao;

import com.directi.pg.*;
import com.manager.vo.PaginationVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.AuthenticationException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**This is only for the Security purpose
 * Created by NIKET on 11-06-2016.
 */
public class AuthenticationDAO
{
    private final String LOCKED="locked";
    private final String UNLOCKED="unlocked";
    private Logger logger = new Logger(AuthenticationDAO.class.getName());
    private Functions functions = new Functions();

    /**
     * Getting all locked user.
     * @param paginationVO
     * @return
     * @throws PZDBViolationException
     */
    public List<DefaultUser> getAllLockedUser(PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection con=null;
        ResultSet rsGetAllLockedUser=null;
        ResultSet rsGetAllLockedUserCount=null;
        List<DefaultUser> defaultUsers = new ArrayList<DefaultUser>();
        try
        {
            con= Database.getRDBConnection();
            StringBuffer query=new StringBuffer("select accountid,login,roles from user where unblocked=? order by roles ");
            String countQuery="select count(*) from ("+query.toString()+") as temp";
            query.append("  limit "+paginationVO.getStart()+","+paginationVO.getEnd());

            PreparedStatement psGetAllLockedUser =con.prepareStatement(query.toString());

            psGetAllLockedUser.setString(1,LOCKED);

            rsGetAllLockedUser=psGetAllLockedUser.executeQuery();

            while(rsGetAllLockedUser.next())
            {
                DefaultUser defaultUser = new DefaultUser(rsGetAllLockedUser.getString("login"));
                defaultUser.setAccountName(rsGetAllLockedUser.getString("login"));
                defaultUser.setAccountId(Long.valueOf(rsGetAllLockedUser.getString("accountid")));
                defaultUser.addRole(rsGetAllLockedUser.getString("roles"));

                defaultUsers.add(defaultUser);
            }

            if(defaultUsers.size()>0)
            {
                PreparedStatement psGetAllLockedUserCount =con.prepareStatement(countQuery);
                psGetAllLockedUserCount.setString(1,LOCKED);
                rsGetAllLockedUserCount=psGetAllLockedUserCount.executeQuery();
                if(rsGetAllLockedUserCount.next())
                {
                    paginationVO.setTotalRecords(rsGetAllLockedUserCount.getInt(1));
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while connecting to the database",systemError);
            PZExceptionHandler.raiseDBViolationException(AuthenticationDAO.class.getName(), "getAllLockedUser()", null, "Common", "Sql Exception while connecting to the database", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while connecting to the database",e);
            PZExceptionHandler.raiseDBViolationException(AuthenticationDAO.class.getName(), "getAllLockedUser()", null, "Common", "Sql Exception Incorrect query while fetching the user from the table", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        catch (AuthenticationException e)
        {
            logger.error("System Error while connecting to the database",e);
            PZExceptionHandler.raiseDBViolationException(AuthenticationDAO.class.getName(), "getAllLockedUser()", null, "Common", "Sql Exception Incorrect query while fetching the user from the table", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return defaultUsers;
    }
    public boolean unlockUser(String accountId) throws PZDBViolationException
    {
        Connection con=null;
        int unlockedCount=0;
        PreparedStatement psUpdateUnLockedUser=null;
        try
        {
            con= Database.getConnection();
            StringBuffer query=new StringBuffer("update user set unblocked=?,faillogincount=0 where accountid=? ");
            psUpdateUnLockedUser =con.prepareStatement(query.toString());
            psUpdateUnLockedUser.setString(1,UNLOCKED);
            psUpdateUnLockedUser.setString(2,accountId);
            unlockedCount=psUpdateUnLockedUser.executeUpdate();
            if(unlockedCount>0)
            {
                ((PzAuthenticator)ESAPI.authenticator()).setLastChecked(0);
                return true;
            }

        }
        catch (SystemError systemError)
        {
            logger.error("System Error while connecting to the database",systemError);
            PZExceptionHandler.raiseDBViolationException(AuthenticationDAO.class.getName(), "getAllLockedUser()", null, "Common", "Sql Exception while connecting to the database", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while connecting to the database",e);
            PZExceptionHandler.raiseDBViolationException(AuthenticationDAO.class.getName(), "getAllLockedUser()", null, "Common", "Sql Exception Incorrect query while fetching the user from the table", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(psUpdateUnLockedUser);
            Database.closeConnection(con);
        }

        return false;
    }

    public boolean getUnBlockedAccount(String login)
    {
        boolean flag = false;
        StringBuffer query = new StringBuffer("update user set unblocked='unlocked' ,faillogincount='0' where unblocked='locked' and faillogincount>='3' and login=?");

        Connection con = null;
        PreparedStatement pstmt = null;
        try
        {
            con = Database.getConnection();
            pstmt= con.prepareStatement(query.toString());
            pstmt.setString(1,login);
            logger.debug("update query::::"+pstmt);
            int i = pstmt.executeUpdate();
            if (i > 0)
            {
                flag = true;
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError in Unblock User::::::",se);
        }
        catch (Exception e)
        {
            logger.error("SystemError in Unblock User::::::",e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return flag;
    }
}
