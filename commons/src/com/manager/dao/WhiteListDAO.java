package com.manager.dao;

import com.directi.pg.*;
import com.manager.vo.PaginationVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/*Created with IntelliJ IDEA.
 * User: Mahima
 * Date: 25/03/18
 * Time: 2:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class WhiteListDAO
{
    private static Logger logger = new Logger(WhiteListDAO.class.getName());
    public Hashtable getWhiteListEmailDetails(String memberId, String accountId, String emailAddr, String firstSix, String lastFour, int start, int end,String isTemp,String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        Hashtable hash = null;
        Connection conn = null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Functions functions = new Functions();
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer stringBuffer = new StringBuffer("SELECT id,firstsix,lastfour,memberid,accountid,emailAddr,isTemp,isApproved,actionExecutorId,actionExecutorName FROM whitelist_details WHERE isApproved='Y'");//AND isTemp='Y'
            StringBuffer count = new StringBuffer("SELECT COUNT(*) FROM whitelist_details WHERE isApproved='Y'");//AND isTemp='Y'
            if (functions.isValueNull(isTemp))
            {
                stringBuffer.append(" and isTemp='" + ESAPI.encoder().encodeForSQL(me, isTemp)+"'");
                count.append(" and isTemp='" + ESAPI.encoder().encodeForSQL(me, isTemp)+"'");
            }
            if (functions.isValueNull(memberId))
            {
                stringBuffer.append(" and memberid=" + ESAPI.encoder().encodeForSQL(me, memberId));
                count.append(" and memberid=" + ESAPI.encoder().encodeForSQL(me, memberId));
            }
            if (functions.isValueNull(accountId))
            {
                stringBuffer.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me, accountId));
                count.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me, accountId));
            }
            if (functions.isValueNull(emailAddr))
            {
                stringBuffer.append(" and emailAddr='" + emailAddr + "'");
                count.append(" and emailAddr='" + emailAddr + "'");
            }
            if (functions.isValueNull(firstSix))
            {
                stringBuffer.append(" and firstsix='" + ESAPI.encoder().encodeForSQL(me, firstSix)+"'");
                count.append(" and firstsix='" + ESAPI.encoder().encodeForSQL(me, firstSix)+"'");
            }
            if (functions.isValueNull(lastFour))
            {
                stringBuffer.append(" and lastfour='" + ESAPI.encoder().encodeForSQL(me, lastFour)+"'");
                count.append(" and lastfour='" + ESAPI.encoder().encodeForSQL(me, lastFour)+"'");
            }
           /* if (functions.isValueNull(actionExecutorId ))
            {
                stringBuffer.append(" and actionExecutorId =" + ESAPI.encoder().encodeForSQL(me, actionExecutorId ));
                count.append(" and actionExecutorId =" + ESAPI.encoder().encodeForSQL(me, actionExecutorId ));
            }
            if (functions.isValueNull(memberId))
            {
                stringBuffer.append(" and actionExecutorName =" + ESAPI.encoder().encodeForSQL(me, actionExecutorName ));
                count.append(" and actionExecutorName =" + ESAPI.encoder().encodeForSQL(me, actionExecutorName ));
            }
           */ stringBuffer.append(" order by id asc LIMIT " + start + "," + end);
            logger.debug(stringBuffer.toString());
            hash = Database.getHashFromResultSet(Database.executeQuery(stringBuffer.toString(), conn));
            rs = Database.executeQuery(count.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "getWhiteListEmailDetails()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "getWhiteListEmailDetails()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return hash;
    }

    public Hashtable getWhiteListEmailDetailsForExport(String memberId, String accountId, String emailAddr, String firstSix, String lastFour,String actionExecutorId,String actionExecutorName,String isTemp)throws PZDBViolationException
    {
        Hashtable hash      = null;
        Connection conn     = null;
        Codec me            = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Functions functions = new Functions();
        ResultSet rs        = null;
        try
        {
            conn                        = Database.getRDBConnection();
            //StringBuffer stringBuffer   = new StringBuffer("SELECT id,firstsix,lastfour,memberid,accountid,emailAddr,isTemp,actionExecutorId,actionExecutorName isApproved FROM whitelist_details WHERE isApproved='Y' AND isTemp='Y' ");
            //StringBuffer count          = new StringBuffer("SELECT COUNT(*) FROM whitelist_details WHERE isApproved='Y' AND isTemp='Y' ");

            StringBuffer stringBuffer   = new StringBuffer("SELECT id,firstsix,lastfour,memberid,accountid,emailAddr,isTemp,actionExecutorId,actionExecutorName,isApproved FROM whitelist_details WHERE isApproved='Y' ");
            StringBuffer count          = new StringBuffer("SELECT COUNT(*) FROM whitelist_details WHERE isApproved='Y' ");
            if (functions.isValueNull(memberId))
            {
                stringBuffer.append(" and memberid=" + ESAPI.encoder().encodeForSQL(me, memberId));
                count.append(" and memberid=" + ESAPI.encoder().encodeForSQL(me, memberId));
            }
            if (functions.isValueNull(accountId))
            {
                stringBuffer.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me, accountId));
                count.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me, accountId));
            }
            if (functions.isValueNull(emailAddr))
            {
                stringBuffer.append(" and emailAddr='" + emailAddr + "'");
            }
            if (functions.isValueNull(firstSix))
            {
                stringBuffer.append(" and firstsix='" + ESAPI.encoder().encodeForSQL(me, firstSix)+"'");
                count.append(" and firstsix='" + ESAPI.encoder().encodeForSQL(me, firstSix)+"'");
            }
            if (functions.isValueNull(lastFour))
            {
                stringBuffer.append(" and lastfour='" + ESAPI.encoder().encodeForSQL(me, lastFour)+"'");
                count.append(" and lastfour='" + ESAPI.encoder().encodeForSQL(me, lastFour)+"'");
            }
            if (functions.isValueNull(isTemp))
            {
                stringBuffer.append(" and isTemp='" + ESAPI.encoder().encodeForSQL(me, isTemp)+"'");
                count.append(" and isTemp='" + ESAPI.encoder().encodeForSQL(me, isTemp)+"'");
            }
           /* if (functions.isValueNull(actionExecutorId))
            {
                stringBuffer.append(" and actionExecutorId=" + ESAPI.encoder().encodeForSQL(me, actionExecutorId));
            }
            if (functions.isValueNull(actionExecutorName))
            {
                stringBuffer.append(" and actionExecutorName=" + ESAPI.encoder().encodeForSQL(me, actionExecutorName));
           }
           */ stringBuffer.append(" order by id asc ");
            logger.error("EmailDetailsForExport " + stringBuffer.toString());
            logger.error("EmailDetailsForExport "+count.toString());
            hash    = Database.getHashFromResultSet(Database.executeQuery(stringBuffer.toString(), conn));
            rs      = Database.executeQuery(count.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "getWhiteListEmailDetailsForExport()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "getWhiteListEmailDetailsForExport()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return hash;
    }
    public Hashtable getCommonWhiteList(String isTemp, String mappingId)throws PZDBViolationException
    {
        Hashtable hash = null;
        Connection conn = null;
        String query = null;
        int updRecs = 0;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Functions functions = new Functions();
        PreparedStatement pstmt=null;
        try
        {
            conn = Database.getConnection();
            query = "Update whitelist_details set isTemp=? where id=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, isTemp);
            pstmt.setString(2, mappingId);

            logger.debug("result " + pstmt);
            int result = pstmt.executeUpdate();
            logger.debug("result " + query);
            if (result > 0)
            {
                updRecs++;
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "getCommonWhiteList()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "getCommonWhiteList()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return hash;
    }


    public boolean isRecordAvailableForMember(String memberId, String accountId, String firstSix, String lastFour, String emailAddress,String cardHolderName,String ipAddress,String expiryDate) throws PZDBViolationException
    {
        boolean isRecordAvailable = false;
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet result=null;
        Functions functions=new Functions();
        int pos=6;
        try
        {
            conn=Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT id FROM whitelist_details WHERE memberid=? and accountid=? and firstsix=? and lastfour=? and (emailAddr=? or emailAddr!=null)");
            if(functions.isValueNull(cardHolderName))
                query.append(" and name=?");
            if(functions.isValueNull(ipAddress))
                query.append(" and ipAddress=?");
            if(functions.isValueNull(expiryDate))
                query.append(" and expiryDate=?");
            query.append(" order by id");
            preparedStatement = conn.prepareStatement(query.toString());
            preparedStatement.setString(1,memberId);
            preparedStatement.setString(2,accountId);
            preparedStatement.setString(3,firstSix);
            preparedStatement.setString(4,lastFour);
            preparedStatement.setString(5,emailAddress);
            if(functions.isValueNull(cardHolderName))
            {
                preparedStatement.setString(pos, cardHolderName);
                pos++;
            }
            if(functions.isValueNull(ipAddress))
            {
                preparedStatement.setString(pos, ipAddress);
                pos++;
            }
            if(functions.isValueNull(expiryDate))
            {
                preparedStatement.setString(pos, expiryDate);
            }
            result = preparedStatement.executeQuery();
            if(result.next()){
                isRecordAvailable = true;
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return isRecordAvailable;
    }

    public String isRecordAvailableForPerMember(String memberId, String accountId, String firstSix, String lastFour, String emailAddress,String cardHolderName,String ipAddress,String expiryDate) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet result=null;
        Functions functions=new Functions();
        int pos=6;
        String isTemp="";
        try
        {
            conn=Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT id,isTemp FROM whitelist_details WHERE memberid=? and accountid=? and firstsix=? and lastfour=? and (emailAddr=? or emailAddr!=null)");
            if(functions.isValueNull(cardHolderName))
                query.append(" and name=?");
            if(functions.isValueNull(ipAddress))
                query.append(" and ipAddress=?");
            if(functions.isValueNull(expiryDate))
                query.append(" and expiryDate=?");
            query.append(" order by id");
            preparedStatement = conn.prepareStatement(query.toString());
            preparedStatement.setString(1,memberId);
            preparedStatement.setString(2,accountId);
            preparedStatement.setString(3,firstSix);
            preparedStatement.setString(4,lastFour);
            preparedStatement.setString(5,emailAddress);
            if(functions.isValueNull(cardHolderName))
            {
                preparedStatement.setString(pos, cardHolderName);
                pos++;
            }
            if(functions.isValueNull(ipAddress))
            {
                preparedStatement.setString(pos, ipAddress);
                pos++;
            }
            if(functions.isValueNull(expiryDate))
            {
                preparedStatement.setString(pos, expiryDate);
            }
            result = preparedStatement.executeQuery();
            if(result.next()){
                isTemp=result.getString("isTemp");
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return isTemp;
    }

    public boolean isRecordAvailableForMemberWithoutEmailid(String memberId, String accountId, String firstSix, String lastFour,String cardHolderName,String ipAddress,String expiryDate,String whitelistLevel) throws PZDBViolationException
    {
        boolean isRecordAvailable = false;
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet result=null;
        try
        {
            conn=Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT id FROM whitelist_details WHERE memberid=? and firstsix=? and lastfour=? and name=? and ipAddress=?");
            if("Account".equalsIgnoreCase(whitelistLevel))
                query.append(" and accountid='"+accountId+"'");
                query.append(" order by id");

            preparedStatement = conn.prepareStatement(query.toString());
            preparedStatement.setString(1,memberId);
            preparedStatement.setString(2,firstSix);
            preparedStatement.setString(3,lastFour);
            preparedStatement.setString(4,cardHolderName);
            preparedStatement.setString(5,ipAddress);
            //preparedStatement.setString(6,expiryDate);
            logger.error("SELECT preparedStatement--->"+preparedStatement);
            result = preparedStatement.executeQuery();
            if(result.next()){
                isRecordAvailable = true;
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableForMemberWithoutEmailid()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableForMemberWithoutEmailid()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return isRecordAvailable;
    }
    public boolean deleteWhitelistBin(String id) throws PZDBViolationException
    {
        logger.debug("inside deleteWhitelistBin:::");
        boolean isRecordAvailable = false;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        Functions functions = new Functions();
        int counter = 1;
        try
        {
            conn = Database.getRDBConnection();
            StringBuilder query = new StringBuilder("DELETE from whitelist_bins");

            if (functions.isValueNull(id))
            {
                query.append(" WHERE id=?");
            }
            preparedStatement = conn.prepareStatement(query.toString());

            if (functions.isValueNull(id))
            {
                preparedStatement.setString(counter, id);
                counter++;
            }

            logger.debug("delete query:::::" + preparedStatement);
            int i = preparedStatement.executeUpdate();
            if (i > 0)
            {
                logger.debug("inside if true");
                isRecordAvailable = true;
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(BlacklistDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to blacklist_country table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(BlacklistDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to blacklist_country table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        logger.debug("return:::::" + isRecordAvailable);
        return isRecordAvailable;
    }
    public boolean deleteWhitelistCard(String id) throws PZDBViolationException
    {
        logger.debug("inside deleteWhitelistCard:::");
        boolean isRecordAvailable = false;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        Functions functions = new Functions();
        int counter = 1;
        try
        {
            conn = Database.getRDBConnection();
            StringBuilder query = new StringBuilder("DELETE from whitelist_details");

            if (functions.isValueNull(id))
            {
                query.append(" WHERE id=?");
            }
            preparedStatement = conn.prepareStatement(query.toString());

            if (functions.isValueNull(id))
            {
                preparedStatement.setString(counter, id);
                counter++;
            }

            logger.debug("delete query:::::" + preparedStatement);
            int i = preparedStatement.executeUpdate();
            if (i > 0)
            {
                logger.debug("inside if true");
                isRecordAvailable = true;
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(BlacklistDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to blacklist_country table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(BlacklistDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to blacklist_country table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        logger.debug("return:::::" + isRecordAvailable);
        return isRecordAvailable;
    }
    public boolean deleteWhitelistEmail(String id) throws PZDBViolationException
    {
        logger.debug("inside deleteWhitelistCard:::");
        boolean isRecordAvailable = false;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        Functions functions = new Functions();
        int counter = 1;
        try
        {
            conn = Database.getRDBConnection();
            StringBuilder query = new StringBuilder("DELETE from whitelist_details");

            if (functions.isValueNull(id))
            {
                query.append(" WHERE id=?");
            }
            preparedStatement = conn.prepareStatement(query.toString());

            if (functions.isValueNull(id))
            {
                preparedStatement.setString(counter, id);
                counter++;
            }

            logger.debug("delete query:::::" + preparedStatement);
            int i = preparedStatement.executeUpdate();
            if (i > 0)
            {
                logger.debug("inside if true");
                isRecordAvailable = true;
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(BlacklistDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to blacklist_country table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(BlacklistDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to blacklist_country table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        logger.debug("return:::::" + isRecordAvailable);
        return isRecordAvailable;
    }
    public boolean isRecordAvailableForMember(String memberId, String accountId, String firstSix, String lastFour,String cardHolderName,String ipAddress,String expiryDate) throws PZDBViolationException
    {
        boolean isRecordAvailable = false;
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet result=null;
        Functions functions=new Functions();
        int pos=5;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT id FROM whitelist_details WHERE memberid=? and accountid=? and firstsix=? and lastfour=?");
            if(functions.isValueNull(cardHolderName))
                query.append(" and name=?");
            if(functions.isValueNull(ipAddress))
                query.append(" and ipAddress=?");
            if(functions.isValueNull(expiryDate))
                query.append(" and expiryDate=?");
            query.append(" order by id");
            preparedStatement = conn.prepareStatement(query.toString());
            preparedStatement.setString(1,memberId);
            preparedStatement.setString(2,accountId);
            preparedStatement.setString(3,firstSix);
            preparedStatement.setString(4,lastFour);
            if(functions.isValueNull(cardHolderName))
            {
                preparedStatement.setString(pos, cardHolderName);
                pos++;
            }
            if(functions.isValueNull(ipAddress))
            {
                preparedStatement.setString(pos, ipAddress);
                pos++;
            }
            if(functions.isValueNull(expiryDate))
            {
                preparedStatement.setString(pos, expiryDate);
            }
            logger.error("isRecordAvailableForMember ps---->"+preparedStatement);
            result = preparedStatement.executeQuery();
            if (result.next())
            {
                isRecordAvailable = true;
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return isRecordAvailable;
    }
    public String isRecordAvailableForPerMember(String memberId, String accountId, String firstSix, String lastFour,String cardHolderName,String ipAddress,String expiryDate) throws PZDBViolationException
    {
        boolean isRecordAvailable = false;
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet result=null;
        Functions functions=new Functions();
        int pos=5;
        String isTemp="";
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT id,isTemp FROM whitelist_details WHERE memberid=? and accountid=? and firstsix=? and lastfour=?");
            if(functions.isValueNull(cardHolderName))
                query.append(" and name=?");
            if(functions.isValueNull(ipAddress))
                query.append(" and ipAddress=?");
            if(functions.isValueNull(expiryDate))
                query.append(" and expiryDate=?");
            query.append(" order by id");
            preparedStatement = conn.prepareStatement(query.toString());
            preparedStatement.setString(1,memberId);
            preparedStatement.setString(2,accountId);
            preparedStatement.setString(3,firstSix);
            preparedStatement.setString(4,lastFour);
            if(functions.isValueNull(cardHolderName))
            {
                preparedStatement.setString(pos, cardHolderName);
                pos++;
            }
            if(functions.isValueNull(ipAddress))
            {
                preparedStatement.setString(pos, ipAddress);
                pos++;
            }
            if(functions.isValueNull(expiryDate))
            {
                preparedStatement.setString(pos, expiryDate);
            }
            logger.error("isRecordAvailableForMember ps---->"+preparedStatement);
            result = preparedStatement.executeQuery();
            if (result.next())
            {
                isRecordAvailable = true;
                isTemp=result.getString("isTemp");
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return isTemp;
    }
    public boolean isRecordAvailableForMembers(String memberId, String accountId, String firstSix, String lastFour) throws PZDBViolationException
    {
        boolean isRecordAvailable = false;
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet result=null;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT firstsix,lastfour FROM whitelist_details WHERE memberid=? and accountid=? order by firstsix");
            preparedStatement = conn.prepareStatement(query.toString());
            preparedStatement.setString(1,memberId);
            preparedStatement.setString(2,accountId);
            result = preparedStatement.executeQuery();
            while (result.next())
            {
                if (result.getString("firstsix").equals(firstSix) && result.getString("lastfour").equals(lastFour))
                {
                    isRecordAvailable = true;
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return isRecordAvailable;
    }



    public List<WhitelistingDetailsVO> getWhiteListCardForPartner(String firstSix,String lastFour,String emailAddr,String name,String ipAddress,String expiryDate,String accountId,String memberId,int records, int pageno,String actionExecutorId,String actionExecutorName, HttpServletRequest req) throws PZDBViolationException
    {
        int start = 0;
        int end = 0;
        Connection conn = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        PreparedStatement ps=null;
        PreparedStatement pstmt=null;
        WhitelistingDetailsVO whitelistingDetailsVO = null;
        List<WhitelistingDetailsVO> list= new ArrayList();
        StringBuffer stringBuffer = new StringBuffer();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        String isTemp=req.getParameter("isTemp");

        start = (pageno - 1) * records;
        end = records;
        try
        {
            conn = Database.getConnection();
            stringBuffer.append("SELECT id,memberid,accountid,firstsix,lastfour,emailAddr,name,ipAddress, expiryDate, isTemp,actionExecutorId,actionExecutorName FROM whitelist_details WHERE id>0  AND firstsix IS NOT NULL AND firstsix!=\"\" AND lastfour IS NOT NULL AND lastfour!=\"\" ");
            StringBuffer countQuery =new StringBuffer("SELECT COUNT(*) FROM whitelist_details WHERE id>0 AND firstsix IS NOT NULL AND firstsix!=\"\" AND lastfour IS NOT NULL AND lastfour!=\"\" ");
            if (isTemp != null && !isTemp.equals(""))
            {
                stringBuffer.append(" and isTemp='" + ESAPI.encoder().encodeForSQL(me,isTemp)+"'");
                countQuery.append(" and isTemp='" + ESAPI.encoder().encodeForSQL(me,isTemp)+"'");
            }
            if (firstSix != null && !firstSix.equals(""))
            {
                stringBuffer.append(" and firstsix=" + ESAPI.encoder().encodeForSQL(me,firstSix));
                countQuery.append(" and firstsix=" + ESAPI.encoder().encodeForSQL(me,firstSix));
            }
            if (firstSix != null && !firstSix.equals(""))
            {
                stringBuffer.append(" and firstsix=" + ESAPI.encoder().encodeForSQL(me,firstSix));
                countQuery.append(" and firstsix=" + ESAPI.encoder().encodeForSQL(me,firstSix));
            }
            if (lastFour != null && !lastFour.equals(""))
            {
                stringBuffer.append(" and lastfour=" + ESAPI.encoder().encodeForSQL(me,lastFour));
                countQuery.append(" and lastfour=" + ESAPI.encoder().encodeForSQL(me,lastFour));
            }
            if (emailAddr != null && !emailAddr.equals(""))
            {
                stringBuffer.append(" and emailAddr='" + emailAddr + "'");
                countQuery.append(" and emailAddr='" + emailAddr + "'");
            }
            if (name != null && !name.equals(""))
            {
                stringBuffer.append(" and name='" + name + "'");
                countQuery.append(" and name='" + name + "'");
            }
            if (ipAddress != null && !ipAddress.equals(""))
            {
                stringBuffer.append(" and ipAddress='" + ipAddress + "'");
                countQuery.append(" and ipAddress='" + ipAddress + "'");
            }
            if (expiryDate != null && !expiryDate.equals(""))
            {
                stringBuffer.append(" and expiryDate='" + expiryDate + "'");
                countQuery.append(" and expiryDate='" + expiryDate + "'");
            }
            if (accountId != null && !accountId.equals(""))
            {
                stringBuffer.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me,accountId));
                countQuery.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me,accountId));
            }
            if (memberId != null && !memberId.equals(""))
            {
                stringBuffer.append(" and memberid=" + ESAPI.encoder().encodeForSQL(me,memberId));
                countQuery.append(" and memberid=" + ESAPI.encoder().encodeForSQL(me,memberId));
            }
            stringBuffer.append(" ORDER BY id DESC ");
            stringBuffer.append(" limit  " + start + "," + end);
            ps = conn.prepareStatement(stringBuffer.toString());
            rs = ps.executeQuery();
            while (rs.next())
            {
                whitelistingDetailsVO = new WhitelistingDetailsVO();
                whitelistingDetailsVO.setId((rs.getString("id")));
                whitelistingDetailsVO.setFirstsix(rs.getString("firstsix"));
                whitelistingDetailsVO.setLastfour(rs.getString("lastfour"));
                whitelistingDetailsVO.setEmail(rs.getString("emailAddr"));
                whitelistingDetailsVO.setAccountid(Integer.parseInt(rs.getString("accountid")));
                whitelistingDetailsVO.setMemberid(rs.getString("memberid"));
                whitelistingDetailsVO.setName(rs.getString("name"));
                whitelistingDetailsVO.setIpAddress(rs.getString("ipAddress"));
                whitelistingDetailsVO.setExpiryDate(rs.getString("expiryDate"));
                whitelistingDetailsVO.setIsTemp(rs.getString("isTemp"));
                whitelistingDetailsVO.setActionExecutorId(rs.getString("actionExecutorId"));
                whitelistingDetailsVO.setActionExecutorName(rs.getString("actionExecutorName"));


                list.add(whitelistingDetailsVO);
            }

            pstmt = conn.prepareStatement(countQuery.toString());
            rs1 = pstmt.executeQuery();

            int totalrecords = 0;
            if (rs1.next())
                totalrecords = rs1.getInt(1);

            req.setAttribute("totalrecords", totalrecords);
            req.setAttribute("records", "0");
            if (totalrecords > 0)
            {
                req.setAttribute("records", list.size());
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","getWhiteListBin()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException-->",e);
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","getWhiteListBin()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return list;
    }


    public boolean isRecordAvailableOnOtherGroup(String firstSix, String lastFour, String gateway, String companyName) throws PZDBViolationException
    {
        boolean isRecordAvailable = false;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT wd.id FROM whitelist_details AS wd JOIN `members` AS m ON wd.memberid=m.memberid JOIN `gateway_accounts` AS ga ON wd.accountid=ga.accountid JOIN `gateway_type` AS gt ON ga.pgtypeid=gt.pgtypeid WHERE wd.firstsix=? AND wd.lastfour=? AND gt.gateway=? AND m.company_name !=?");
            preparedStatement = conn.prepareStatement(query.toString());
            preparedStatement.setString(1, firstSix);
            preparedStatement.setString(2, lastFour);
            preparedStatement.setString(3, gateway);
            preparedStatement.setString(4, companyName);
            result = preparedStatement.executeQuery();
            if (result.next())
            {
                isRecordAvailable = true;
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableOnOtherGroup()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableOnOtherGroup()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return isRecordAvailable;
    }

    public boolean isRecordAvailableOnOtherGroup(String firstSix, String lastFour, String emailAddress, String gateway, String companyName) throws PZDBViolationException
    {
        boolean isRecordAvailable = false;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT wd.id FROM whitelist_details AS wd JOIN `members` AS m ON wd.memberid=m.memberid JOIN `gateway_accounts` AS ga ON wd.accountid=ga.accountid JOIN `gateway_type` AS gt ON ga.pgtypeid=gt.pgtypeid WHERE wd.firstsix=? AND wd.lastfour=? AND wd.emailAddr=? AND gt.gateway=? AND m.company_name !=?");
            preparedStatement = conn.prepareStatement(query.toString());
            preparedStatement.setString(1, firstSix);
            preparedStatement.setString(2, lastFour);
            preparedStatement.setString(3, emailAddress);
            preparedStatement.setString(4, gateway);
            preparedStatement.setString(5, companyName);
            result = preparedStatement.executeQuery();
            if (result.next())
            {
                isRecordAvailable = true;
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableOnOtherGroup()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableOnOtherGroup()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return isRecordAvailable;
    }
    public boolean isRecordAvailableInSystem(String memberId,String firstSix, String lastFour, String emailAddress)throws PZDBViolationException
    {
        Connection conn = null;
        boolean isRecordAuthentic=false;
        PreparedStatement preparedStatement=null;
        ResultSet result=null;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query =new StringBuffer("SELECT id FROM whitelist_details WHERE firstsix=? AND lastfour=? AND emailAddr=? AND memberid=? order by id limit 1");
            preparedStatement=conn.prepareStatement(query.toString());
            preparedStatement.setString(1,firstSix);
            preparedStatement.setString(2,lastFour);
            preparedStatement.setString(3,emailAddress);
            preparedStatement.setString(4,memberId);
            result = preparedStatement.executeQuery();
            if(result.next()){
                isRecordAuthentic =true;
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableInSystem()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableInSystem()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return isRecordAuthentic;
    }
    public boolean addCard(String firstSix,String lastFour,String emailAddress,String accountId,String memberId,String cardHolderName,String ipAddress,String expiryDate,String actionExecutorId,String actionExecutorName)throws PZDBViolationException
    {
        Connection connection=null;
        Functions functions=new Functions();
        PreparedStatement preparedStatement=null;
        boolean result=false;
        if(functions.isValueNull(accountId)){
            accountId=accountId;
        }else {
            accountId="0";
        }
        try
        {
            connection=Database.getConnection();
            StringBuffer query=new StringBuffer("INSERT INTO whitelist_details (firstsix,lastfour,emailAddr,accountid,memberid,isApproved,name,ipAddress,expiryDate,actionExecutorId,actionExecutorName) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            preparedStatement=connection.prepareStatement(query.toString());
            preparedStatement.setString(1,firstSix);
            preparedStatement.setString(2,lastFour);
            preparedStatement.setString(3,emailAddress);
            preparedStatement.setString(4,accountId);
            preparedStatement.setString(5,memberId);
            preparedStatement.setString(6,"Y");
            preparedStatement.setString(7,cardHolderName);
            preparedStatement.setString(8,ipAddress);
            preparedStatement.setString(9,expiryDate);
            preparedStatement.setString(10,actionExecutorId);
            preparedStatement.setString(11,actionExecutorName);
            int k=preparedStatement.executeUpdate();

            logger.error("INSERT preparedStatement--->"+preparedStatement);
            if(k>0){
                result=true;
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "addCard()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "addCard()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return result;
    }
    public boolean updateCard(String firstSix,String  lastFour,String emailAddress,String accountId,String memberId,String cardHolderName,String ipAddress,String expiryDate,String actionExecutorId,String actionExecutorName )throws PZDBViolationException
    {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        boolean result=false;
        try
        {
            connection=Database.getConnection();
            StringBuffer query=new StringBuffer("UPDATE whitelist_details SET emailAddr=?,isApproved=?,name=?,ipAddress=?,expiryDate=?,isTemp='N' where accountid=? and memberid=? and firstsix=? and lastfour=? and actionExecutor=? and actionExecutorName=?");
            preparedStatement=connection.prepareStatement(query.toString());
            preparedStatement.setString(1,emailAddress);
            preparedStatement.setString(2,"Y");
            preparedStatement.setString(3,cardHolderName);
            preparedStatement.setString(4,ipAddress);
            preparedStatement.setString(5,expiryDate);
            preparedStatement.setString(6,accountId);
            preparedStatement.setString(7,memberId);
            preparedStatement.setString(8,firstSix);
            preparedStatement.setString(9,lastFour);
            preparedStatement.setString(10,actionExecutorId);
            preparedStatement.setString(11,actionExecutorName);

            logger.error("UPDATE preparedStatement ----->"+preparedStatement);
            int k=preparedStatement.executeUpdate();

            if(k>0){
                result=true;
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "addCard()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "addCard()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return result;
    }
    public boolean removeCardEmailEntry(String isTemp,String mappingId)throws PZDBViolationException
    {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        boolean result=false;
        try
        {
            connection=Database.getConnection();
            StringBuffer query=new StringBuffer("update whitelist_details set isTemp=? WHERE id=?");
            preparedStatement=connection.prepareStatement(query.toString());
            preparedStatement.setString(1,isTemp);
            preparedStatement.setString(2,mappingId);
            int k=preparedStatement.executeUpdate();
            if(k>0){
                result=true;
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "removeCardEmailEntry()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "removeCardEmailEntry()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return result;
    }
    public boolean updateCardEmailEntry(String mappingId)throws PZDBViolationException
    {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        boolean result=false;
        try
        {
            connection=Database.getConnection();
            StringBuffer query=new StringBuffer("update whitelist_details SET istemp='Y' WHERE id=?");
            preparedStatement=connection.prepareStatement(query.toString());
            preparedStatement.setString(1,mappingId);
            //System.out.println("updateCardEmailEntry---->"+preparedStatement);
            int k=preparedStatement.executeUpdate();
            if(k>0){
                result=true;
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "removeCardEmailEntry()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "removeCardEmailEntry()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return result;
    }
    public boolean updateCardEmail(String cardNumber,String eMail)throws PZDBViolationException
    {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        boolean result=false;
        try
        {
            connection=Database.getConnection();
            String firstSix=Functions.getFirstSix(cardNumber);
            String lastFour=Functions.getLastFour(cardNumber);

            StringBuffer query=new StringBuffer("update whitelist_details set istemp='Y' WHERE (firstsix=? AND lastfour=?) or emailAddr=?");
            preparedStatement=connection.prepareStatement(query.toString());
            preparedStatement.setString(1,firstSix);
            preparedStatement.setString(2,lastFour);
            preparedStatement.setString(3,eMail);
            int k=preparedStatement.executeUpdate();
            if(k>0){
                result=true;
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "removeCardEmail()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "removeCardEmail()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return result;
    }
    public boolean removeCardEmail(String cardNumber,String eMail)throws PZDBViolationException
    {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        boolean result=false;
        try
        {
            connection=Database.getConnection();
            String firstSix=Functions.getFirstSix(cardNumber);
            String lastFour=Functions.getLastFour(cardNumber);

            StringBuffer query=new StringBuffer("delete from whitelist_details WHERE (firstsix=? AND lastfour=?) or emailAddr=?");
            preparedStatement=connection.prepareStatement(query.toString());
            preparedStatement.setString(1,firstSix);
            preparedStatement.setString(2,lastFour);
            preparedStatement.setString(3,eMail);
            logger.error("removeCardEmail----->"+preparedStatement);
            int k=preparedStatement.executeUpdate();
            if(k>0){
                result=true;
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "removeCardEmail()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "removeCardEmail()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return result;
    }
    public void removeCardEmailEntry(Set<String> emailList,Set<String> cardList)throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try
        {
            connection = Database.getConnection();
            StringBuffer query = new StringBuffer("delete from whitelist_details WHERE (firstsix=? AND lastfour=?) or emailAddr=?");
            preparedStatement = connection.prepareStatement(query.toString());
            for (String cardNumber : cardList)
            {
                String firstSix = Functions.getFirstSix(cardNumber);
                String lastFour = Functions.getLastFour(cardNumber);
                preparedStatement.setString(1, firstSix);
                preparedStatement.setString(2, lastFour);
                for(String emailAddr:emailList)
                {
                    preparedStatement.setString(3,emailAddr);
                    preparedStatement.addBatch();
                }
            }
            int k[] = preparedStatement.executeBatch();
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "removeCardEmail()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "removeCardEmail()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
    }
    public void updateCardEmailEntry(Set<String> emailList,Set<String> cardList)throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Date d1=new Date();
        System.out.println("start time-->"+d1.getTime());
        try
        {
            connection = Database.getConnection();
            StringBuffer query = new StringBuffer("update whitelist_details SET isTemp='Y' WHERE (firstsix=? AND lastfour=?) or emailAddr=?");
            preparedStatement = connection.prepareStatement(query.toString());
            for (String cardNumber : cardList)
            {
                String firstSix = Functions.getFirstSix(cardNumber);
                String lastFour = Functions.getLastFour(cardNumber);
                preparedStatement.setString(1, firstSix);
                preparedStatement.setString(2, lastFour);
                for(String emailAddr:emailList)
                {
                    preparedStatement.setString(3,emailAddr);
                    preparedStatement.addBatch();
                }
            }
            int k[] = preparedStatement.executeBatch();

        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "removeCardEmail()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "removeCardEmail()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
    }

    public int uploadCards(List<String> queryBatch) throws PZDBViolationException
    {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        int count = 0;
        try
        {
            connection=Database.getConnection();
            for(String batch:queryBatch){
                if (batch != null && batch.length() > 0 && batch.charAt(batch.length() - 1) == ','){
                    batch = batch.substring(0, batch.length() - 1);
                }
                preparedStatement =connection.prepareStatement(batch.toString());
                logger.error("uploadCards preparedStatement:::::"+ preparedStatement);
                count=count+preparedStatement.executeUpdate();
            }
        }
        catch (SystemError systemError){
            logger.error("uploadCards SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java", "uploadCards()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException sql){
            logger.error(" uploadCards SQLException:::::", sql);

            PZExceptionHandler.raiseDBViolationException("WhiteListDao.java", "uploadCards()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, sql.getMessage(), sql.getCause());
        }
        finally{
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return count;
    }

    public List<WhitelistingDetailsVO> getWhiteListBin(String startBin,String endBin,String accountId,String memberId) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps=null;
        WhitelistingDetailsVO whitelistingDetailsVO = null;
        List<WhitelistingDetailsVO> list= new ArrayList();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        try
        {
            conn = Database.getConnection();
            sb.append("select startBin,endBin,accountId,memberId,actionExecutorId,actionExecutorName from whitelist_bins where id>0 ");
            StringBuffer countQuery =new StringBuffer("select count(*) whitelist_bins");

            if(function.isValueNull(startBin)&&function.isValueNull(endBin))
            {
                sb.append("And startBin <='"+startBin+"'"+"AND endBin >='"+endBin+"'");
                countQuery.append("And startBin <='"+startBin+"'"+"AND endBin >='"+endBin+"'");
            }

            if(function.isValueNull(accountId))
            {
                sb.append(" and accountId='"+accountId+"'");
                countQuery.append(" where accountId='"+accountId+"'");
            }
            if(function.isValueNull(memberId))
            {
                sb.append(" and memberId='"+memberId+"'");
                countQuery.append(" where memberId='"+memberId+"'");
            }
            ps = conn.prepareStatement(sb.toString());

            rs = ps.executeQuery();
            while (rs.next())
            {
                whitelistingDetailsVO = new WhitelistingDetailsVO();
                whitelistingDetailsVO.setAccountid(Integer.parseInt(rs.getString("accountId")));
                whitelistingDetailsVO.setMemberid(rs.getString("memberId"));
                whitelistingDetailsVO.setStartBin(rs.getString("startBin"));
                whitelistingDetailsVO.setEndBin(rs.getString("endBin"));
                whitelistingDetailsVO.setActionExecutorId(rs.getString("actionExecutorId"));
                whitelistingDetailsVO.setActionExecutorName(rs.getString("actionExecutorName"));
                list.add(whitelistingDetailsVO);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","getWhiteListBin()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","getWhiteListBin()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return list;
    }
    public List<WhitelistingDetailsVO> getWhiteListBinForPartner(String startBin,String endBin,String startCard,String endCard,String accountId,String memberId, int records, int pageno, HttpServletRequest req) throws PZDBViolationException
    {
        int start = 0;
        int end = 0;
        Connection conn = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        PreparedStatement ps=null;
        PreparedStatement pstmt=null;
        WhitelistingDetailsVO whitelistingDetailsVO = null;
        List<WhitelistingDetailsVO> list= new ArrayList();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();

        start = (pageno - 1) * records;
        end = records;

        try
        {
            conn = Database.getConnection();
            sb.append("select id,startBin,endBin,startCard,endCard,accountId,memberId from whitelist_bins where id>0 ");
            StringBuffer countQuery =new StringBuffer("select count(*) from whitelist_bins");

            if(function.isValueNull(memberId))
            {
                sb.append(" and memberId='"+memberId+"'");
                countQuery.append(" where memberId='"+memberId+"'");
            }

            if(function.isValueNull(startBin)&&function.isValueNull(endBin))
            {
                sb.append("And startBin >='"+startBin+"'"+"AND endBin <='"+endBin+"'");
                countQuery.append("And startBin >='"+startBin+"'"+"AND endBin <='"+endBin+"'");
            }

            if(function.isValueNull(startCard)&&function.isValueNull(endCard))
            {
                sb.append("And startCard >='"+startCard+"'"+"AND endCard <='"+endCard+"'");
                countQuery.append("And startCard >='"+startCard+"'"+"AND endCard <='"+endCard+"'");
            }

            if(function.isValueNull(accountId))
            {
                sb.append(" and accountId='"+accountId+"'");
                countQuery.append(" and accountId='"+accountId+"'");
            }
            sb.append(" ORDER BY id DESC ");
            sb.append(" limit "+start+","+end);

            ps = conn.prepareStatement(sb.toString());
            rs = ps.executeQuery();
            while (rs.next())
            {
                whitelistingDetailsVO = new WhitelistingDetailsVO();
                whitelistingDetailsVO.setId(rs.getString("id"));
                whitelistingDetailsVO.setAccountid(Integer.parseInt(rs.getString("accountId")));
                whitelistingDetailsVO.setMemberid(rs.getString("memberId"));
                whitelistingDetailsVO.setStartBin(rs.getString("startBin"));
                whitelistingDetailsVO.setEndBin(rs.getString("endBin"));
                whitelistingDetailsVO.setStartCard(rs.getString("startCard"));
                whitelistingDetailsVO.setEndCard(rs.getString("endCard"));
                list.add(whitelistingDetailsVO);
            }
            pstmt = conn.prepareStatement(countQuery.toString());
            rs1 = pstmt.executeQuery();

            int totalrecords = 0;
            if (rs1.next())
                totalrecords = rs1.getInt(1);

            req.setAttribute("totalrecords", totalrecords);
            req.setAttribute("records", "0");
            if (totalrecords > 0)
            {
                req.setAttribute("records", list.size());
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","getWhiteListBin()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","getWhiteListBin()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return list;
    }
    public List<WhitelistingDetailsVO> getWhiteListBinPage (String startBin, String endBin,String startCard, String endCard, String accountId, String memberId, String actionExecutorId,String actionExecutorName,PaginationVO paginationVO)throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        WhitelistingDetailsVO whitelistingDetailsVO = null;
        List<WhitelistingDetailsVO> list = new ArrayList();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        int totalreords = 0;

        try
        {
            conn = Database.getConnection();
            sb.append("select * from whitelist_bins where id>0 ");
            StringBuffer countQuery = new StringBuffer("select count(*) from whitelist_bins where id>0 ");

            if (function.isValueNull(startBin) && function.isValueNull(endBin))
            {
                sb.append("And startBin <='" + startBin + "'" + "AND endBin >='" + endBin + "'");
                countQuery.append("And startBin <='" + startBin + "'" + "AND endBin >='" + endBin + "'");
            }
            if (function.isValueNull(startCard) && function.isValueNull(endCard))
            {
                sb.append("And startCard <='" + startCard + "'" + "AND endCard >='" + endCard + "'");
                countQuery.append("And startCard <='" + startCard + "'" + "AND endCard >='" + endCard + "'");
            }

            if (function.isValueNull(accountId))
            {
                sb.append(" and accountId='" + accountId + "'");
                countQuery.append("And accountId='" + accountId + "'");
            }
            if (function.isValueNull(memberId))
            {
                sb.append(" and memberId='" + memberId + "'");
                countQuery.append(" And memberId='" + memberId + "'");
            }
            if (!function.isValueNull(startBin) && !function.isValueNull(endBin))
            {
                sb.append(" order by id desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            }
          /*  if (function.isValueNull(actionExecutorId))
            {
                sb.append(" and actionExecutorId='" + actionExecutorId + "'");

            }

            if (function.isValueNull(actionExecutorName))
            {
                sb.append(" and actionExecutorName='" + actionExecutorName + "'");

            }
*/
            ps = conn.prepareStatement(sb.toString());
            logger.error("Ps----->"+ps);
            System.out.println("Ps----->"+ps);


            rs = ps.executeQuery();


            while (rs.next())
            {
                whitelistingDetailsVO = new WhitelistingDetailsVO();
                whitelistingDetailsVO.setId(rs.getString("id"));
                whitelistingDetailsVO.setAccountid(Integer.parseInt(rs.getString("accountId")));
                whitelistingDetailsVO.setMemberid(rs.getString("memberId"));
                whitelistingDetailsVO.setStartBin(rs.getString("startBin"));
                whitelistingDetailsVO.setEndBin(rs.getString("endBin"));
                whitelistingDetailsVO.setStartCard(rs.getString("startCard"));
                whitelistingDetailsVO.setEndCard(rs.getString("endCard"));
                whitelistingDetailsVO.setActionExecutorId(rs.getString("actionExecutorId"));
                whitelistingDetailsVO.setActionExecutorName(rs.getString("actionExecutorName"));
                list.add(whitelistingDetailsVO);
            }
            ps = conn.prepareStatement(countQuery.toString());

            rs = ps.executeQuery();
            if(rs.next())
            {
                totalreords=rs.getInt(1);
            }
            if(totalreords!=0)
            {
                paginationVO.setTotalRecords(totalreords);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java", "getWhiteListBin()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java", "getWhiteListBin()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return list;
    }
    public int addBin(String startBin,String endBin,String startCard,String endCard,String accountId,String memberId,String actionExecutorId,String actionExecutorName)throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        int count=0;
        try
        {
            String query = "insert into whitelist_bins(startBin,endBin,startCard,endCard,accountId,memberId,actionExecutorId,actionExecutorName)values (?,?,?,?,?,?,?,?)";
            connection = Database.getConnection();
            preparedStatement=connection.prepareStatement(query);
            preparedStatement.setString(1,startBin);
            preparedStatement.setString(2,endBin);
            preparedStatement.setString(3,startCard);
            preparedStatement.setString(4,endCard);
            preparedStatement.setString(5,accountId);
            preparedStatement.setString(6,memberId);
            preparedStatement.setString(7,actionExecutorId);
            preparedStatement.setString(8,actionExecutorName);

            logger.error("INSERT preparedStatement---->"+preparedStatement);
            count=preparedStatement.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","addBin()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","addBin()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return count;
    }
    public void unblockBin(String startBin,String endBin,String accountId,String memberId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement ps=null;
        try
        {
            conn = Database.getConnection();
            String dQuery = "delete from whitelist_bins where startBin=? AND endBin=? AND accountId=? AND memberId=?";
            ps = conn.prepareStatement(dQuery);
            ps.setString(1,startBin);
            ps.setString(2,endBin);
            ps.setString(3,accountId);
            ps.setString(4, memberId);
            ps.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","unblockBin()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","unblockBin()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
    }
    public List<WhitelistingDetailsVO> getWhiteListCardForMerchant(String firstSix,String lastFour,String emailAddr,String name,String ipAddress,String memberId,PaginationVO paginationVO,String accountId) throws PZDBViolationException
    {
        int totalrecords        = 0;
        Connection conn         = null;
        ResultSet rs            = null;
        PreparedStatement ps    = null;
        WhitelistingDetailsVO whitelistingDetailsVO = null;
        List<WhitelistingDetailsVO> list            = new ArrayList();
        StringBuffer stringBuffer                   = new StringBuffer();
        Codec me            = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Functions functions =  new Functions();
        try
        {
            conn                    = Database.getConnection();
            stringBuffer.append("SELECT id,firstsix,lastfour,emailAddr,accountid,name,ipAddress FROM whitelist_details WHERE memberid=? AND istemp='N' AND firstsix IS NOT NULL AND firstsix!=\"\" AND lastfour IS NOT NULL AND lastfour!=\"\" ");
            StringBuffer countQuery = new StringBuffer("SELECT COUNT(*) FROM whitelist_details WHERE id>0 AND istemp='N'  AND memberid=? AND firstsix IS NOT NULL AND firstsix!=\"\" AND lastfour IS NOT NULL AND lastfour!=\"\" ");

            if (functions.isValueNull(firstSix))
            {
                stringBuffer.append(" and firstsix=" + ESAPI.encoder().encodeForSQL(me,firstSix));
                countQuery.append(" and firstsix=" + ESAPI.encoder().encodeForSQL(me,firstSix));
            }
            if (functions.isValueNull(lastFour))
            {
                stringBuffer.append(" and lastfour=" + ESAPI.encoder().encodeForSQL(me,lastFour));
                countQuery.append(" and lastfour=" + ESAPI.encoder().encodeForSQL(me,lastFour));
            }
            if (functions.isValueNull(emailAddr))
            {
                stringBuffer.append(" and emailAddr='" + emailAddr + "'");
                countQuery.append(" and emailAddr='" + emailAddr + "'");
            }
            if (functions.isValueNull(name))
            {
                stringBuffer.append(" and name='" +name+"'");
                countQuery.append(" and name='" + name+"'");
            }
            if (functions.isValueNull(ipAddress))
            {
                stringBuffer.append(" and ipAddress='" + ipAddress+"'");
                countQuery.append(" and ipAddress='" + ipAddress+"'");
            }
            if (functions.isValueNull(accountId))
            {
                stringBuffer.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me, accountId));
                countQuery.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me, accountId));
            }
/*
            if (actionExecutorId !=null && actionExecutorId.equals(""))
            {
                stringBuffer.append(" and actionExecutorId=" + ESAPI.encoder().encodeForSQL(me, actionExecutorId));
            }
            if (actionExecutorName  !=null && actionExecutorName .equals(""))
            {
                stringBuffer.append(" and actionExecutorName =" + ESAPI.encoder().encodeForSQL(me, actionExecutorName));
            }
*/
                stringBuffer.append(" order by id desc limit "+paginationVO.getStart()+","+paginationVO.getEnd());

            ps = conn.prepareStatement(stringBuffer.toString());
            ps.setString(1, memberId);
            logger.debug("PS:::::"+ps);
            rs = ps.executeQuery();
            while (rs.next())
            {
                whitelistingDetailsVO = new WhitelistingDetailsVO();
                whitelistingDetailsVO.setId(rs.getString("id"));
                whitelistingDetailsVO.setFirstsix(rs.getString("firstsix"));
                whitelistingDetailsVO.setLastfour(rs.getString("lastfour"));
                whitelistingDetailsVO.setEmail(rs.getString("emailAddr"));
                whitelistingDetailsVO.setAccountid(Integer.parseInt(rs.getString("accountid")));
                whitelistingDetailsVO.setName(rs.getString("name"));
                whitelistingDetailsVO.setIpAddress(rs.getString("ipAddress"));
                list.add(whitelistingDetailsVO);
            }
            ps  = conn.prepareStatement(countQuery.toString());
            ps.setString(1, memberId);
            rs  = ps.executeQuery();
            if (rs.next())
            {
                totalrecords = rs.getInt(1);
            }
            if (totalrecords != 0)
            {
                paginationVO.setTotalRecords(totalrecords);
            }

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","getWhiteListBin()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","getWhiteListBin()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return list;
    }

    public List<WhitelistingDetailsVO> getWhiteListEmailDetailsForMerchant(String emailAddress,String name, String ipAddress,String expiryDate,String accountId,String memberId) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps=null;
        WhitelistingDetailsVO whitelistingDetailsVO = null;
        List<WhitelistingDetailsVO> list= new ArrayList();
        StringBuffer stringBuffer = new StringBuffer();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        try
        {
            conn = Database.getConnection();
            stringBuffer.append("SELECT emailAddr,accountid,name,ipAddress,expiryDate FROM whitelist_details WHERE id>0 AND istemp='N' AND firstsix IS NULL OR firstsix=\"\" AND lastfour IS NULL OR lastfour=\"\" AND emailAddr IS NOT NULL AND emailAddr!=\"\" AND emailAddr!=\"NULL\" AND memberid=?");
            StringBuffer countQuery =new StringBuffer("SELECT COUNT(*) FROM whitelist_details WHERE id>0 AND istemp='N'");

            if (emailAddress != null && !emailAddress.equals(""))
            {
                stringBuffer.append(" and emailAddr='" + emailAddress + "'");
                countQuery.append(" and emailAddr='" + emailAddress + "'");
            }
            if (name != null && !name.equals(""))
            {
                stringBuffer.append(" and name='" + name + "'");
                countQuery.append(" and name='" + name + "'");
            }
            if (ipAddress != null && !ipAddress.equals(""))
            {
                stringBuffer.append(" and ipAddress='" + ipAddress + "'");
                countQuery.append(" and ipAddress='" + ipAddress + "'");
            }
            if (expiryDate != null && !expiryDate.equals(""))
            {
                stringBuffer.append(" and expiryDate='" + expiryDate + "'");
                countQuery.append(" and expiryDate='" + expiryDate + "'");

            }
            if (accountId != null && !accountId.equals(""))
            {
                stringBuffer.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me,accountId));
                countQuery.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me,accountId));
            }

            ps = conn.prepareStatement(stringBuffer.toString());
            ps.setString(1,memberId);

            rs = ps.executeQuery();
            while (rs.next())
            {
                whitelistingDetailsVO = new WhitelistingDetailsVO();
                whitelistingDetailsVO.setEmail(rs.getString("emailAddr"));
                whitelistingDetailsVO.setAccountid(Integer.parseInt(rs.getString("accountid")));
                list.add(whitelistingDetailsVO);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","getWhiteListBin()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","getWhiteListBin()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return list;
    }
    public List<WhitelistingDetailsVO> getWhiteListEmailDetailsForMerchantPage(String emailAddress,String name,String ipAddress,String memberId,PaginationVO paginationVO) throws PZDBViolationException
    {
        int totalrecords=0;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps=null;
        WhitelistingDetailsVO whitelistingDetailsVO = null;
        List<WhitelistingDetailsVO> list= new ArrayList();
        StringBuffer stringBuffer = new StringBuffer();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        try
        {
            conn = Database.getConnection();
            stringBuffer.append("SELECT id,emailAddr,name,ipAddress,accountid FROM whitelist_details WHERE id>0 AND firstsix IS NULL OR firstsix=\"\" AND lastfour IS NULL OR lastfour=\"\" AND emailAddr IS NOT NULL AND emailAddr!=\"\" AND emailAddr!=\"NULL\" AND memberid=?");
            StringBuffer countQuery =new StringBuffer("SELECT COUNT(*) FROM whitelist_details WHERE id>0  AND firstsix IS NULL OR firstsix=\"\" AND lastfour IS NULL OR lastfour=\"\" AND emailAddr IS NOT NULL AND emailAddr!=\"\" AND emailAddr!=\"NULL\" AND memberid=?");

            if (emailAddress != null && !emailAddress.equals(""))
            {
                stringBuffer.append(" and emailAddr='" + emailAddress + "'");
                countQuery.append(" and emailAddr='" + emailAddress + "'");
            }
            if (name != null && !name.equals(""))
            {
                stringBuffer.append(" and name='" + name + "'");
                countQuery.append(" and name='" + name + "'");
            }
            if (ipAddress != null && !ipAddress.equals(""))
            {
                stringBuffer.append(" and ipAddress='" + ipAddress + "'");
                countQuery.append(" and ipAddress='" + ipAddress + "'");
            }
            stringBuffer.append(" order by id desc limit "+paginationVO.getStart()+","+paginationVO.getEnd());
            ps = conn.prepareStatement(stringBuffer.toString());
            ps.setString(1,memberId);
            rs = ps.executeQuery();
            while (rs.next())
            {
                whitelistingDetailsVO = new WhitelistingDetailsVO();
                whitelistingDetailsVO.setId(rs.getString("id"));
                whitelistingDetailsVO.setEmail(rs.getString("emailAddr"));
                whitelistingDetailsVO.setName(rs.getString("name"));
                whitelistingDetailsVO.setIpAddress(rs.getString("ipAddress"));
                whitelistingDetailsVO.setAccountid(Integer.parseInt(rs.getString("accountid")));
                list.add(whitelistingDetailsVO);
            }
            ps=conn.prepareStatement(countQuery.toString());
            ps.setString(1, memberId);
            rs=ps.executeQuery();
            if (rs.next())
            {
                totalrecords = rs.getInt(1);
            }
            if (totalrecords != 0)
            {
                paginationVO.setTotalRecords(totalrecords);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","getWhiteListBin()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","getWhiteListBin()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return list;
    }
    public List<WhitelistingDetailsVO> getWhiteListEmailDetailsForPartner(String emailAddress,String name,String ipAddress,String expiryDate,String accountId,String memberId, int records, int pageno,HttpServletRequest req) throws PZDBViolationException
    {
        logger.debug("inside getWhiteListEmailDetailsForPartner memberid:::"+memberId);
        int start = 0;
        int end = 0;
        Connection conn = null;
        Functions functions = new Functions();
        ResultSet rs = null;
        ResultSet rs1 = null;
        PreparedStatement ps=null;
        PreparedStatement pstmt=null;
        WhitelistingDetailsVO whitelistingDetailsVO = null;
        List<WhitelistingDetailsVO> list= new ArrayList();
        StringBuffer stringBuffer = new StringBuffer();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        String isTemp=req.getParameter("isTemp");

        start = (pageno - 1) * records;
        end = records;
        try
        {
            conn = Database.getConnection();
            stringBuffer.append("SELECT id,memberid,emailAddr,accountid,name,ipAddress,expiryDate,isTemp,actionExecutorId,actionExecutorName FROM whitelist_details WHERE id>0 AND firstsix=\"\" AND  lastfour=\"\" AND emailAddr!=\"NULL\" ");
            StringBuffer countQuery =new StringBuffer("SELECT COUNT(*) FROM whitelist_details WHERE id>0 AND firstsix=\"\" AND  lastfour=\"\" AND emailAddr!=\"NULL\" ");
            if (functions.isValueNull(isTemp))
            {
                stringBuffer.append(" AND isTemp ='" + ESAPI.encoder().encodeForSQL(me,isTemp)+"'");
                countQuery.append(" AND isTemp ='" + ESAPI.encoder().encodeForSQL(me,isTemp)+"'");
            }
            if (functions.isValueNull(memberId))
            {
                stringBuffer.append(" AND memberid =" + ESAPI.encoder().encodeForSQL(me,memberId));
                countQuery.append(" AND memberid =" + ESAPI.encoder().encodeForSQL(me,memberId));
            }
            if (emailAddress != null && !emailAddress.equals(""))
            {
                stringBuffer.append(" and emailAddr='" + emailAddress + "'");
                countQuery.append(" and emailAddr='" + emailAddress + "'");
            }
            if (name != null && !name.equals(""))
            {
                stringBuffer.append(" and name='" + name + "'");
                countQuery.append(" and name='" + name + "'");
            }
            if (ipAddress != null && !ipAddress.equals(""))
            {
                stringBuffer.append(" and ipAddress='" + ipAddress + "'");
                countQuery.append(" and ipAddress='" + ipAddress + "'");
            }
            if (accountId != null && !accountId.equals(""))
            {
                stringBuffer.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me,accountId));
                countQuery.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me,accountId));
            }
            if (expiryDate != null && !expiryDate.equals(""))
            {
                stringBuffer.append(" and expiryDate='" + expiryDate + "'");
                countQuery.append(" and expiryDate='" + expiryDate + "'");
            }
           /* if (actionExecutorId != null && !actionExecutorId.equals(""))
            {
                stringBuffer.append(" and actionExecutorId='" + actionExecutorId + "'");
             }
            if (actionExecutorName != null && !actionExecutorName.equals(""))
            {
                stringBuffer.append(" and actionExecutorName='" + actionExecutorName + "'");
            }*/
            stringBuffer.append(" ORDER BY id DESC ");
            stringBuffer.append(" limit "+start+","+end);

            ps = conn.prepareStatement(stringBuffer.toString());
            logger.debug("email query::::"+ps);
            rs = ps.executeQuery();
            while (rs.next())
            {
                whitelistingDetailsVO = new WhitelistingDetailsVO();
                whitelistingDetailsVO.setId(rs.getString("id"));
                whitelistingDetailsVO.setMemberid(rs.getString("memberid"));
                whitelistingDetailsVO.setEmail(rs.getString("emailAddr"));
                whitelistingDetailsVO.setName(rs.getString("name"));
                whitelistingDetailsVO.setIpAddress(rs.getString("ipAddress"));
                whitelistingDetailsVO.setExpiryDate(rs.getString("expiryDate"));
                whitelistingDetailsVO.setAccountid(Integer.parseInt(rs.getString("accountid")));
                whitelistingDetailsVO.setIsTemp(rs.getString("isTemp"));
                whitelistingDetailsVO.setActionExecutorId(rs.getString("actionExecutorId"));
                whitelistingDetailsVO.setActionExecutorName(rs.getString("actionExecutorName"));
                list.add(whitelistingDetailsVO);
            }
            pstmt = conn.prepareStatement(countQuery.toString());
            logger.debug("country pstmt::::"+pstmt);
            rs1 = pstmt.executeQuery();

            int totalrecords = 0;
            if (rs1.next())
                totalrecords = rs1.getInt(1);

            req.setAttribute("totalrecords", totalrecords);
            req.setAttribute("records", "0");
            if (totalrecords > 0)
            {
                req.setAttribute("records", list.size());
            }

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","getWhiteListBin()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","getWhiteListBin()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return list;
    }
    public boolean getMemberidAccountid(String memberId,String accountID)throws PZDBViolationException
    {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        boolean flag=false;
        try
        {
            connection=Database.getRDBConnection();
            StringBuffer stringBuffer=new StringBuffer("select memberid,accountid from member_account_mapping where memberid=? and accountid=?");
            preparedStatement=connection.prepareStatement(stringBuffer.toString());
            preparedStatement.setString(1,memberId);
            preparedStatement.setString(2,accountID);
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                flag=true;
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","getMemberidAccountid()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","getMemberidAccountid()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return flag;
    }
    public int uploadBins(List<String> queryBatch) throws PZDBViolationException
    {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        int count = 0;
        try
        {
            connection=Database.getConnection();
            for(String batch:queryBatch){
                if (batch != null && batch.length() > 0 && batch.charAt(batch.length() - 1) == ','){
                    batch = batch.substring(0, batch.length() - 1);
                }
                preparedStatement =connection.prepareStatement(batch.toString());
                count=count+preparedStatement.executeUpdate();
            }
        }
        catch (SystemError systemError){
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java", "uploadBins()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException sql){
            PZExceptionHandler.raiseDBViolationException("WhiteListDao.java", "uploadBins()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, sql.getMessage(), sql.getCause());
        }
        finally{
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return count;
    }
    public boolean removeWhitelistBinEntry(String Id)throws PZDBViolationException
    {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        boolean result=false;
        try
        {
            connection=Database.getConnection();
            StringBuffer query=new StringBuffer("delete from whitelist_bins WHERE id=?");
            preparedStatement=connection.prepareStatement(query.toString());
            preparedStatement.setString(1,Id);
            int k=preparedStatement.executeUpdate();
            if(k>0){
                result=true;
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "removeCardEmailEntry()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "removeCardEmailEntry()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return result;
    }
    public boolean updateEmildId(String emailAddress,String firstSix,String  lastFour)throws PZDBViolationException
    {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        boolean result=false;
        try
        {
            connection=Database.getConnection();
            StringBuffer query=new StringBuffer("UPDATE whitelist_details SET Emailaddr ='"+emailAddress+"' Where firstsix = "+firstSix+" AND lastfour = "+lastFour);
            preparedStatement=connection.prepareStatement(query.toString());
            int k=preparedStatement.executeUpdate();

            if(k>0){
                result=true;
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "updateEmildId()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "updateEmildId()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return result;
    }
    public boolean insertBinCountryRoutingDetails(String memberId,String accountId,List<String> countryList,String actionExecutorId,String actionExecutorName)
    {
        Connection con=null;
        PreparedStatement preparedStatement=null;
        boolean isRecordInserted=false;
        try
        {
            con=Database.getConnection();
            con.setAutoCommit(false);
            String query="INSERT INTO whitelist_bin_country (accountid,memberid,actionExecutorId,actionExecutorName,country) VALUES (?,?,?,?,?)";
            preparedStatement=con.prepareStatement(query);
            preparedStatement.setString(1,accountId);
            preparedStatement.setString(2,memberId);
            preparedStatement.setString(3,actionExecutorId);
            preparedStatement.setString(4,actionExecutorName);

            for (String country : countryList)
            {
                preparedStatement.setString(5, country);
                preparedStatement.addBatch();
            }
            logger.error("INSERT query of Whitelist Bin Country--->"+preparedStatement);
           int[] i=preparedStatement.executeBatch();
            con.commit();
            isRecordInserted=true;
        }
        catch (SystemError se)
        {
            logger.error("SystemError ---->",se);
        }
        catch (SQLException e)
        {
            logger.error("SQLException ---->", e);
        }
        return  isRecordInserted;
    }
    public List<String> getWhiteListBinCountry(String country,String accountId,String memberId,String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps=null;
        List<String> list= new ArrayList();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        try
        {
            conn = Database.getConnection();
            sb.append("select country,accountId,memberId,actionExecutorId,actionExecutorName from whitelist_bin_country where id>0 ");

            if(function.isValueNull(country))
            {
                sb.append(" And country in ("+country+")");
            }

            if(function.isValueNull(accountId))
            {
                sb.append(" and accountId='"+accountId+"'");
            }
            if(function.isValueNull(memberId))
            {
                sb.append(" and memberId='"+memberId+"'");
            }

            ps = conn.prepareStatement(sb.toString());
            logger.error("Select query of whitelist bin Country--->"+ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                list.add(rs.getString("country"));
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError ---->",se);
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","getWhiteListBin()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException ---->",e);
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java","getWhiteListBin()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return list;
    }
    public boolean deleteBinCountry(String id )
    {
        Connection connection=null;
        try
        {
            connection = Database.getConnection();
            String deleteQuery = "DELETE FROM whitelist_bin_country WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setString(1,id);
            int i = preparedStatement.executeUpdate();
            if(i>0)
            {
                return true;
            }
        }
        catch (SystemError se)
        {
            logger.error("Exception----------" , se);
        }
        catch (SQLException e)
        {
            logger.error("Exception-------" , e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return false;
    }
    public List<WhitelistingDetailsVO> getWhiteListBinCountryPage (String country, String accountId, String memberId,String actionExecutorId,String actionExecutorName, PaginationVO paginationVO)throws PZDBViolationException
    {
        Functions functions=new Functions();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        WhitelistingDetailsVO whitelistingDetailsVO = null;
        List<WhitelistingDetailsVO> list = new ArrayList();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        int totalreords = 0;

        try
        {
            conn = Database.getConnection();
            sb = new StringBuffer("select id,country,accountid,memberid,actionExecutorId,actionExecutorName from whitelist_bin_country where id>0 ");
            StringBuffer countquery = new StringBuffer("select count(*) from whitelist_bin_country where id>0 ");

            if (functions.isValueNull(memberId))
            {
                sb.append("and  memberid = ?");
                countquery.append("and  memberid = ?");
            }
            if (functions.isValueNull(accountId))
            {
                sb.append(" and accountid = ?");
                countquery.append(" and accountid = ?");
            }
            if (functions.isValueNull(country))
            {
                sb.append(" and country in ("+country+")");
                countquery.append(" and country in ("+country+")");
            }
 /*           if (functions.isValueNull(actionExecutorId))
            {
                sb.append(" and actionExecutorId  in ("+actionExecutorId+")");
            }
            if (functions.isValueNull(actionExecutorName))
            {
                sb.append(" and actionExecutorName  in ("+actionExecutorName+")");
             }
*/
            sb.append(" order by id desc LIMIT ? , ?");
            int counter = 1;
            ps = conn.prepareStatement(sb.toString());
            ps1 = conn.prepareStatement(countquery.toString());
            if (function.isValueNull(memberId))
            {
                ps.setString(counter,memberId);
                ps1.setString(counter,memberId);
                counter++;
            }
            if (function.isValueNull(accountId))
            {

                ps.setString(counter,accountId);
                ps1.setString(counter,accountId);
                counter++;
            }


            ps.setInt(counter, paginationVO.getStart());
            counter++;
            ps.setInt(counter, paginationVO.getEnd());

            logger.error("ps--->"+ps);
            rs = ps.executeQuery();

            while (rs.next())
            {
                whitelistingDetailsVO = new WhitelistingDetailsVO();
                whitelistingDetailsVO.setId(rs.getString("id"));
                whitelistingDetailsVO.setAccountid(Integer.parseInt(rs.getString("accountId")));
                whitelistingDetailsVO.setMemberid(rs.getString("memberId"));
                whitelistingDetailsVO.setCountry(rs.getString("country"));
                whitelistingDetailsVO.setActionExecutorId(rs.getString("actionExecutorId"));
                whitelistingDetailsVO.setActionExecutorName(rs.getString("actionExecutorName"));


                list.add(whitelistingDetailsVO);
            }


            rs = ps1.executeQuery();
            if(rs.next())
            {
                totalreords=rs.getInt(1);
            }
            if(totalreords!=0)
            {
                paginationVO.setTotalRecords(totalreords);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java", "getWhiteListBin()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java", "getWhiteListBin()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return list;
    }
    public String getWhitelistingLevel(String memberId)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet result=null;
        String whitelistingLevel="";
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query =new StringBuffer("SELECT card_whitelist_level FROM members WHERE memberid=?");
            preparedStatement=conn.prepareStatement(query.toString());
            preparedStatement.setString(1,memberId);
            result = preparedStatement.executeQuery();
            if(result.next()){
                whitelistingLevel=result.getString("card_whitelist_level");
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableInSystem()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableInSystem()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return whitelistingLevel;
    }

    public boolean isRecordAvailableInSystemCustomerLevel(String firstSix, String lastFour, String emailAddress,String customerId,String memberId,String accountId)throws PZDBViolationException
    {
        Connection conn = null;
        boolean isRecordAuthentic=false;
        PreparedStatement preparedStatement=null;
        ResultSet result=null;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query =new StringBuffer("SELECT id FROM whitelist_details WHERE firstsix=? AND lastfour=? AND emailAddr=? AND customerId=? AND memberid=? AND accountid=? order by id limit 1");
            preparedStatement=conn.prepareStatement(query.toString());
            preparedStatement.setString(1,firstSix);
            preparedStatement.setString(2,lastFour);
            preparedStatement.setString(3,emailAddress);
            preparedStatement.setString(4,customerId);
            preparedStatement.setString(5,memberId);
            preparedStatement.setString(6,accountId);
            logger.error("Query::::"+preparedStatement);
            result = preparedStatement.executeQuery();
            if(result.next()){
                isRecordAuthentic =true;
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableInSystem()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "isRecordAvailableInSystem()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return isRecordAuthentic;
    }
    public boolean addCardForCustomer(String firstSix,String lastFour,String emailAddress,String accountId,String memberId,String customerId)throws PZDBViolationException
    {
        Connection connection=null;
        Functions functions=new Functions();
        PreparedStatement preparedStatement=null;
        boolean result=false;
        if(functions.isValueNull(accountId)){
            accountId=accountId;
        }else {
            accountId="0";
        }
        try
        {
            connection=Database.getConnection();
            StringBuffer query=new StringBuffer("INSERT INTO whitelist_details (firstsix,lastfour,emailAddr,accountid,memberid,customerId,isApproved) VALUES (?,?,?,?,?,?,?)");
            preparedStatement=connection.prepareStatement(query.toString());
            preparedStatement.setString(1,firstSix);
            preparedStatement.setString(2,lastFour);
            preparedStatement.setString(3,emailAddress);
            preparedStatement.setString(4,accountId);
            preparedStatement.setString(5,memberId);
            preparedStatement.setString(6,customerId);
            preparedStatement.setString(7,"Y");
            int k=preparedStatement.executeUpdate();

            logger.error("INSERT preparedStatement--->"+preparedStatement);
            if(k>0){
                result=true;
            }
        }
        catch (SQLException e){
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "addCard()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(WhiteListDAO.class.getName(), "addCard()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return result;
    }
}