package com.manager.dao;

import com.directi.pg.*;
import com.manager.vo.BlacklistVO;
import com.manager.vo.PaginationVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Jinesh on 4/30/2015.
 */
public class BlacklistDAO
{
    private static Logger log = new Logger(BlacklistDAO.class.getName());

    public int insertBlackListCard(String firstSix, String lastFour,String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement ps = null;
        int count = 0;
        try
        {
            con = Database.getConnection();
            String insertQuery = "INSERT INTO blacklist_cards (first_six,last_four,actionExecutorId,actionExecutorName) VALUES (?,?,?,?)";
            ps = con.prepareStatement(insertQuery);
            ps.setString(1, firstSix);
            ps.setString(2, lastFour);
            ps.setString(3, actionExecutorId);
            ps.setString(4, actionExecutorName);
            count = ps.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlackListCard()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlackListCard()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return count;
    }

    public int insertBlackListCard(String firstSix, String lastFour,String actionExecutorId,String actionExecutorName,String reason,String remark) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement ps = null;
        int count = 0;
        try
        {
            con = Database.getConnection();
            String insertQuery = "INSERT INTO blacklist_cards (first_six,last_four,reason,actionExecutorId,actionExecutorName,remark) VALUES (?,?,?,?,?,?)";
            ps = con.prepareStatement(insertQuery);
            ps.setString(1, firstSix);
            ps.setString(2, lastFour);
            ps.setString(3, reason);
            ps.setString(4, actionExecutorId);
            ps.setString(5, actionExecutorName);
            ps.setString(6, remark);
            log.error("insert blacklist_cards query-->" + ps);
            count = ps.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlackListCard()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlackListCard()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return count;
    }
    public List<BlacklistVO> getBlackListedCards(String firstSix, String lastFour) throws PZDBViolationException
    {
        Connection con = null;
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer();
        List<BlacklistVO> listOfCard = new ArrayList<BlacklistVO>();
        Functions functions = new Functions();
        try
        {
            con = Database.getRDBConnection();
            sb.append("select * from blacklist_cards");

            if (functions.isValueNull(firstSix) && !functions.isValueNull(lastFour))
            {
                sb.append(" where first_six=" + firstSix);
            }
            if (functions.isValueNull(lastFour) && !functions.isValueNull(firstSix))
            {
                sb.append(" where last_four=" + lastFour);
            }
            if (functions.isValueNull(firstSix) && functions.isValueNull(lastFour))
            {
                sb.append(" where first_six=" + firstSix + " and last_four=" + lastFour);
            }
            /*if (functions.isValueNull(remark))
            {
                sb.append(" where remark=" + remark);
            }*/
            PreparedStatement ps = con.prepareStatement(sb.toString());

            rs = ps.executeQuery();
            log.error("select query - "+rs);
            while (rs.next())
            {
                BlacklistVO blacklistVO = new BlacklistVO();
                blacklistVO.setFirstSix(rs.getString("first_six"));
                blacklistVO.setLastFour(rs.getString("last_four"));
                blacklistVO.setLastFour(rs.getString("remark"));
               // blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                //blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));
                listOfCard.add(blacklistVO);

            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlackListedCards()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlackListedCards()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
        return listOfCard;
    }

    public List<BlacklistVO> getBlackListedCardsPage(String firstSix, String lastFour,String actionExecutorId,String actionExecutorName,String remark,PaginationVO paginationVO) throws PZDBViolationException
    {
        int totalreords = 0;
        Connection con = null;
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer();
        List<BlacklistVO> listOfCard = new ArrayList<BlacklistVO>();
        Functions functions = new Functions();
        try
        {
            con = Database.getRDBConnection();
            sb.append("select * from blacklist_cards where id>0");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_cards where id>0");

            if (functions.isValueNull(firstSix) && !functions.isValueNull(lastFour))
            {
                sb.append(" and first_six=" + firstSix);
                countQuery.append(" and first_six=" + firstSix);
            }
            if (functions.isValueNull(lastFour) && !functions.isValueNull(firstSix))
            {
                sb.append(" and last_four=" + lastFour);
                countQuery.append(" and last_four=" + lastFour);
            }
            if (functions.isValueNull(firstSix) && functions.isValueNull(lastFour))
            {
                sb.append(" and first_six=" + firstSix + " and last_four=" + lastFour);
                countQuery.append(" and first_six=" + firstSix + " and last_four=" + lastFour);
            }
            if (functions.isValueNull(remark))
            {
                sb.append(" and remark='" + remark+"'");
                countQuery.append(" and remark='" + remark+"'");
            }

            sb.append(" order by id DESC LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            PreparedStatement ps = con.prepareStatement(sb.toString());
                        rs = ps.executeQuery();
            log.error("select  query---" + ps);

            while (rs.next())
            {
                BlacklistVO blacklistVO = new BlacklistVO();
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.setFirstSix(rs.getString("first_six"));
                blacklistVO.setLastFour(rs.getString("last_four"));
                blacklistVO.setBlacklistCardReason(rs.getString("reason"));
                blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));
                blacklistVO.setRemark(rs.getString("remark"));

                listOfCard.add(blacklistVO);

            }
            ps = con.prepareStatement(countQuery.toString());
            log.error("select  query---" + ps);
            rs = ps.executeQuery();
            log.error("rs query---" + rs);

            log.error("countquery ---" + countQuery);

            if (rs.next())
            {
                totalreords = rs.getInt(1);
            }
            if (totalreords != 0)
            {
                paginationVO.setTotalRecords(totalreords);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlackListedCardsPage()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlackListedCardsPage()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
        return listOfCard;
    }

    public void deleteBlockedCard(String id) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        try
        {
            conn = Database.getConnection();
            String dQuery = "DELETE FROM blacklist_cards WHERE id=?";
            ps = conn.prepareStatement(dQuery);
            ps.setString(1, id);
            ps.executeUpdate();
            log.debug("delete card---" + dQuery);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "deleteBlockedCard()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "deleteBlockedCard()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
    }

    public List<BlacklistVO> getBlockedIp(String ip) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        BlacklistVO blacklistVO = null;
        List<BlacklistVO> listOfIp = new ArrayList<BlacklistVO>();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        try
        {
            conn = Database.getRDBConnection();
            sb.append("select * from blacklist_ip");
            String query = "select count(*) blacklist_ip";
            if (function.isValueNull(ip))
            {
                sb.append(" where ipaddress='" + ip + "'");
            }

            ps = conn.prepareStatement(sb.toString());
            rs = ps.executeQuery();
            while (rs.next())
            {
                blacklistVO = new BlacklistVO();
                blacklistVO.setIpAddress(rs.getString("ipaddress"));
               // blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
               // blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));
                listOfIp.add(blacklistVO);
            }
            log.debug("ip query---" + sb.toString());
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedIp()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedIp()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return listOfIp;
    }

    public int insertBlockedIp(String ipAddress) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        int count = 0;
        try
        {
            conn = Database.getConnection();
            String insertQuery = "insert into blacklist_ip (ipaddress) values (?)";
            ps = conn.prepareStatement(insertQuery);
            ps.setString(1, ipAddress);
         //   ps.setString(2, actionExecutorId);
           // ps.setString(3, actionExecutorName);
            log.error("insert blacklist_ip query-->"+ps);
            count = ps.executeUpdate();

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedIp()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedIp()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return count;
    }

    public void unblockIpAddress(String ipAddress) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        BlacklistVO blacklistVO = new BlacklistVO();
        try
        {
            conn = Database.getConnection();
            String dQuery = "delete from blacklist_ip where ipaddress=?";
            ps = conn.prepareStatement(dQuery);
            ps.setString(1, ipAddress);
            ps.executeUpdate();
            log.debug("unblock query---" + dQuery);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "unblockIpAddress()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "unblockIpAddress()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
    }
    //BlockEmail

    public List<BlacklistVO> getBlockedEmailAddress(String email,String reason,String remark, PaginationVO paginationVO) throws PZDBViolationException
    {
        int totalreords = 0;
        Connection conn = null;
        ResultSet rs = null;
        BlacklistVO blacklistVO = null;
        List<BlacklistVO> listOfEmail = new ArrayList<BlacklistVO>();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        try
        {
            conn = Database.getConnection();
            sb.append("select * from blacklist_email where id>0");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_email where id>0 ");

            if (function.isValueNull(email))
            {
                sb.append(" and emailAddress='" + email + "'");
                countQuery.append("  and emailAddress='" + email + "'");
            }
            if (function.isValueNull(reason))
            {
                sb.append(" and reason='" + reason + "'");
                countQuery.append(" and  reason='" + reason + "'");
            }
            if (function.isValueNull(remark))
            {
                sb.append(" and remark='" + remark + "'");
                countQuery.append(" and remark='" + remark + "'");
            }
            sb.append(" order by TIMESTAMP desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            PreparedStatement ps = conn.prepareStatement(sb.toString());
            log.debug("search email query---" + ps);
            rs = ps.executeQuery();
            while (rs.next())
            {
                blacklistVO = new BlacklistVO();
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.setEmailAddress(rs.getString("emailAddress"));
                blacklistVO.setBlacklistReason(rs.getString("reason"));
                blacklistVO.setTimestamp(rs.getString("timestamp"));
                blacklistVO.setRemark(rs.getString("remark"));
                blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));
                listOfEmail.add(blacklistVO);
            }
            log.debug("email query---" + sb.toString());
            ps = conn.prepareStatement(countQuery.toString());
            rs = ps.executeQuery();
            if (rs.next())
            {
                totalreords = rs.getInt(1);
            }
            if (totalreords != 0)
            {
                paginationVO.setTotalRecords(totalreords);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedEmailAddress()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedEmailAddress()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return listOfEmail;
    }

    public int insertBlockedEmail(String emailAddress,String reason,String remark, String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        int count = 0;
        try
        {
            conn = Database.getConnection();
            String insertQuery = "insert into blacklist_email (emailAddress,reason,remark,actionExecutorId,actionExecutorName) values (?,?,?,?,?)";
            ps = conn.prepareStatement(insertQuery);
            ps.setString(1, emailAddress);
            ps.setString(2, reason);
            ps.setString(3, remark);
            ps.setString(4,actionExecutorId);
            ps.setString(5, actionExecutorName);

            log.error("insert blacklist_email query---" + ps);
            count = ps.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlockedEmail()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlockedEmail()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return count;
    }
    public int insertBlockedvpaAddress(String vpaAddress,String reason,String remark, String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        int count = 0;
        try
        {
            conn = Database.getConnection();
            String insertQuery = "insert into blacklist_vpaaddress (vpaAddress,reason,remark,actionExecutorId,actionExecutorName) values (?,?,?,?,?)";
            ps = conn.prepareStatement(insertQuery);
            ps.setString(1, vpaAddress);
            ps.setString(2, reason);
            ps.setString(3, remark);
            ps.setString(4,actionExecutorId);
            ps.setString(5, actionExecutorName);

            log.error("insert blacklist_email query---" + ps);
            count = ps.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlockedEmail()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlockedEmail()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return count;
    }

    public void unblockEmailAddress(String id) throws PZDBViolationException
    {
        Connection conn = null;
        BlacklistVO blacklistVO = new BlacklistVO();
        PreparedStatement ps = null;
        try
        {
            conn = Database.getConnection();
            String dQuery = "delete from blacklist_email where id=?";
            ps = conn.prepareStatement(dQuery);
            ps.setString(1, id);
            ps.executeUpdate();
            log.debug("unblock query---" + dQuery);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "unblockEmailAddress()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "unblockEmailAddress()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
    }

    //BlockNames
    public List<BlacklistVO> getBlockedName(String name, String reason,String remark,PaginationVO paginationVO) throws PZDBViolationException
    {
        int totalreords = 0;
        Connection conn = null;
        ResultSet rs = null;
        BlacklistVO blacklistVO = null;
        List<BlacklistVO> listOfName = new ArrayList<BlacklistVO>();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        try
        {
            conn = Database.getConnection();
            sb.append("select * from blacklist_name where id>0");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_name where id>0");

            if (function.isValueNull(name))
            {
                sb.append(" and name='" + name + "'");
                countQuery.append(" and name='" + name + "'");
            }
            if ( function.isValueNull(reason))
            {
                sb.append(" and  reason='" + reason + "'");
                countQuery.append(" and  reason='" + reason + "'");
            }
            if (function.isValueNull(remark))
            {
                sb.append(" and remark='" + remark + "'");
                countQuery.append(" and remark='" +remark + "'");
            }
            sb.append(" order by TIMESTAMP desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            PreparedStatement ps = conn.prepareStatement(sb.toString());

            rs = ps.executeQuery();
            while (rs.next())
            {
                blacklistVO = new BlacklistVO();
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.setName(rs.getString("name"));
                blacklistVO.setBlacklistReason(rs.getString("reason"));
                blacklistVO.setTimestamp(rs.getString("timestamp"));
                blacklistVO.setRemark(rs.getString("remark"));
                blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));
                listOfName.add(blacklistVO);
            }
            log.debug("name query---" + sb.toString());
            ps = conn.prepareStatement(countQuery.toString());
            rs = ps.executeQuery();
            if (rs.next())
            {
                totalreords = rs.getInt(1);
            }
            if (totalreords != 0)
            {
                paginationVO.setTotalRecords(totalreords);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedName()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedName()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return listOfName;
    }

    public int insertBlockedName(String name,String reason,String remark, String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        int count = 0;
        try
        {
            conn = Database.getConnection();
            String insertQuery = "insert into blacklist_name (name,reason,remark,actionExecutorId,actionExecutorName) values (?,?,?,?,?)";
            ps = conn.prepareStatement(insertQuery);
            ps.setString(1, name);
            ps.setString(2, reason);
            ps.setString(3, remark);
            ps.setString(4,actionExecutorId);
            ps.setString(5,actionExecutorName);
            count = ps.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlockedName()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlockedName()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return count;
    }

    public void unblockName(String id) throws PZDBViolationException
    {
        Connection conn = null;
        BlacklistVO blacklistVO = new BlacklistVO();
        PreparedStatement ps = null;
        try
        {
            conn = Database.getConnection();
            String dQuery = "delete from blacklist_name where id=?";
            ps = conn.prepareStatement(dQuery);
            ps.setString(1, id);
            ps.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "unblockName()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "unblockName()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
    }

    //BlockCountry
    public List<BlacklistVO> getBlockedCountry(String country, String accountId, String memberId) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        BlacklistVO blacklistVO = null;
        List<BlacklistVO> listOfCountry = new ArrayList();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        try
        {
            conn = Database.getConnection();
            sb.append("select * from blacklist_country where id>0");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_country where  id>0");

            if (function.isValueNull(country))
            {
                sb.append(" and country='" + country + "'");
                countQuery.append(" and country='" + country + "'");
            }
            if (function.isValueNull(accountId))
            {
                sb.append(" and accountid='" + accountId + "'");
                countQuery.append(" and accountid='" + accountId + "'");
            }
            if (function.isValueNull(memberId))
            {
                sb.append(" and memberid='" + memberId + "'");
                countQuery.append(" and memberid='" + memberId + "'");
            }
            ps = conn.prepareStatement(sb.toString());
            rs = ps.executeQuery();
            while (rs.next())
            {
                blacklistVO = new BlacklistVO();
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.setCountry(rs.getString("country"));
                blacklistVO.setTelnocc(rs.getString("telnocc"));
                blacklistVO.setCountryCode(rs.getString("country_code"));
                blacklistVO.setThreeDigitCountryCode(rs.getString("three_digit_country_code"));
                blacklistVO.setAccountId(rs.getString("accountid"));
                blacklistVO.setMemberId(rs.getString("memberid"));
                listOfCountry.add(blacklistVO);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return listOfCountry;
    }

    public List<BlacklistVO> getBlockedCountryPage(String country, String accountId, String memberId, PaginationVO paginationVO) throws PZDBViolationException
    {
        int totalreords = 0;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        BlacklistVO blacklistVO = null;
        List<BlacklistVO> listOfCountry = new ArrayList();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        try
        {
            conn = Database.getConnection();
            sb.append("select * from blacklist_country where id>0");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_country where  id>0");

            if (function.isValueNull(country))
            {
                sb.append(" and country='" + country + "'");
                countQuery.append(" and country='" + country + "'");
            }
            if (function.isValueNull(accountId))
            {
                sb.append(" and accountid='" + accountId + "'");
                countQuery.append(" and accountid='" + accountId + "'");
            }
            if (function.isValueNull(memberId))
            {
                sb.append(" and memberid='" + memberId + "'");
                countQuery.append(" and memberid='" + memberId + "'");
            }
            sb.append(" order by TIMESTAMP desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            ps = conn.prepareStatement(sb.toString());
            rs = ps.executeQuery();
            while (rs.next())
            {
                blacklistVO = new BlacklistVO();
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.setCountry(rs.getString("country"));
                blacklistVO.setTelnocc(rs.getString("telnocc"));
                blacklistVO.setCountryCode(rs.getString("country_code"));
                blacklistVO.setThreeDigitCountryCode(rs.getString("three_digit_country_code"));
                blacklistVO.setAccountId(rs.getString("accountid"));
                blacklistVO.setMemberId(rs.getString("memberid"));
                blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));
                listOfCountry.add(blacklistVO);
            }
            ps = conn.prepareStatement(countQuery.toString());
            rs = ps.executeQuery();
            if (rs.next())
            {
                totalreords = rs.getInt(1);
            }
            if (totalreords != 0)
            {
                paginationVO.setTotalRecords(totalreords);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedCountryPage()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedCountryPage()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return listOfCountry;
    }

    public List<BlacklistVO> getBlockedCountryPartner(String country, String accountId, String memberId, int records, int pageno, HttpServletRequest req) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        PreparedStatement ps = null;
        PreparedStatement pstmt = null;
        BlacklistVO blacklistVO = null;
        List<BlacklistVO> listOfCountry = new ArrayList();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        int start = 0;
        int end = 0;

        start = (pageno - 1) * records;
        end = records;
        try
        {
            conn = Database.getConnection();
            sb.append("select * from blacklist_country where id>0");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_country where id>0");

            if (function.isValueNull(memberId))
            {
                sb.append(" and memberid='" + memberId + "'");
                countQuery.append(" and memberid='" + memberId + "'");
            }
            if (function.isValueNull(country))
            {
                sb.append(" and country='" + country + "'");
                countQuery.append(" and country='" + country + "'");
            }
            if (function.isValueNull(accountId))
            {
                sb.append(" and accountid='" + accountId + "'");
                countQuery.append(" and accountid='" + accountId + "'");
            }
            sb.append(" ORDER BY TIMESTAMP DESC ");
            sb.append(" limit  " + start + "," + end);
            ps = conn.prepareStatement(sb.toString());
            log.debug("inside country query:::::" + ps);

            rs = ps.executeQuery();
            while (rs.next())
            {
                blacklistVO = new BlacklistVO();
                blacklistVO.setCountry(rs.getString("country"));
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.setTelnocc(rs.getString("telnocc"));
                blacklistVO.setCountryCode(rs.getString("country_code"));
                blacklistVO.setThreeDigitCountryCode(rs.getString("three_digit_country_code"));
                blacklistVO.setAccountId(rs.getString("accountid"));
                blacklistVO.setMemberId(rs.getString("memberid"));
                blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));
                listOfCountry.add(blacklistVO);
            }
            pstmt = conn.prepareStatement(countQuery.toString());
            log.debug("count query::::" + pstmt);
            rs1 = pstmt.executeQuery();
            int totalrecords = 0;
            if (rs1.next())
                totalrecords = rs1.getInt(1);

            req.setAttribute("totalrecords", totalrecords);
            req.setAttribute("records", "0");
            if (totalrecords > 0)
            {
                req.setAttribute("records", listOfCountry.size());
            }
            log.debug("totalrecords::::" + totalrecords);
            log.debug("listOfCountry size::::" + listOfCountry.size());
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return listOfCountry;
    }

    public int insertBlockedCountry(String country, String code, String telCc, String three_digit_country_code, String accountId, String memberId,String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        int count = 0;
        try
        {
            conn = Database.getConnection();
            String insertQuery = "insert into blacklist_country (country,country_code,telnocc,accountid,memberid,three_digit_country_code,actionExecutorId,actionExecutorName) values (?,?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(insertQuery);
            ps.setString(1, country);
            ps.setString(2, code);
            ps.setString(3, telCc);
            ps.setString(4, accountId);
            ps.setString(5, memberId);
            ps.setString(6, three_digit_country_code);
            ps.setString(7, actionExecutorId);
            ps.setString(8, actionExecutorName);
            count = ps.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlockedCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlockedCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return count;
    }

    public void unblockCountry(String Id) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        BlacklistVO blacklistVO = new BlacklistVO();
        try
        {
            conn = Database.getConnection();
            String dQuery = "delete from blacklist_country where id=?";
            ps = conn.prepareStatement(dQuery);
            ps.setString(1, Id);
            log.debug("PS::::::::" + ps);
            ps.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "unblockCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "unblockCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
    }

    public void addCustomerNameBatch(Set<String> nameList, String reason) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try
        {
            String query = "insert into blacklist_name(name,reason) value(?,?)";
            connection = Database.getConnection();
            preparedStatement = connection.prepareStatement(query);
            for (String customerName : nameList)
            {
                preparedStatement.setString(1, customerName);
                preparedStatement.setString(2, reason);
                preparedStatement.addBatch();
            }
            int k[] = preparedStatement.executeBatch();
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "addCustomerNameBatch()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException sql)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "addCustomerNameBatch()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, sql.getMessage(), sql.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
    }

    public void addCustomerCardBatch(Set<String> cardList, String reason, String remark,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Functions functions=new Functions();
        try
        {
            connection = Database.getConnection();
            String query = "insert into blacklist_cards(first_six,last_four,reason,remark,actionExecutorId,actionExecutorName)values(?,?,?,?,?,?)";
            preparedStatement = connection.prepareStatement(query);
            for (String cardNumber : cardList)
            {
                if(functions.isValueNull(cardNumber))
                {
                    String firstSix = Functions.getFirstSix(cardNumber);
                    String lastFour = Functions.getLastFour(cardNumber);
                    preparedStatement.setString(1, firstSix);
                    preparedStatement.setString(2, lastFour);
                    preparedStatement.setString(3, reason);
                    preparedStatement.setString(4, remark);
                    preparedStatement.setString(5, auditTrailVO.getActionExecutorId());
                    preparedStatement.setString(6, auditTrailVO.getActionExecutorName());
                    preparedStatement.addBatch();
                }
            }
            int k[] = preparedStatement.executeBatch();
            System.out.println(Arrays.toString(k));
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "addCustomerCardBatch()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException sql)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "addCustomerCardBatch()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, sql.getMessage(), sql.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
    }

    public void addCustomerEmailBatch(Set<String> emailList, String reason,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        try
        {
            String query = "insert into blacklist_email (emailAddress,reason,actionExecutorId,actionExecutorName) values (?,?,?,?)";
            conn = Database.getConnection();
            ps = conn.prepareStatement(query);
            for (String emailAddress : emailList)
            {
                ps.setString(1, emailAddress);
                ps.setString(2, reason);
                ps.setString(3, auditTrailVO.getActionExecutorId());
                ps.setString(4, auditTrailVO.getActionExecutorName());
                ps.addBatch();
            }
            int k[] = ps.executeBatch();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "addCustomerEmailBatch()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "addCustomerEmailBatch()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
    }

    //TO CHECK IF CARDHOLDER NAME IS ALREADY EXIST
    public boolean isCardNameBlacklist(String customerName) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet Result = null;
        String status=null;
        try
        {
            String query = "SELECT 'X' FROM blacklist_name WHERE name=?";
            connection = Database.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, customerName);
            Result = preparedStatement.executeQuery();
            while (Result.next())
            {
                status = Result.getString(1);
                if(status.equals("X")){
                    return true;
                }
            }

        }
        catch (Exception e)
        {
            log.error("Exception--->",e);
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return false;
    }

    public void addCustomerNameBatch(String customerName, String reason,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean status = isCardNameBlacklist(customerName);
        if(status == false)
        {
            try
            {
                String query = "insert into blacklist_name(name,reason,actionExecutorId,actionExecutorName) value(?,?,?,?)";
                connection = Database.getConnection();
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, customerName);
                preparedStatement.setString(2, reason);
                preparedStatement.setString(3, auditTrailVO.getActionExecutorId());
                preparedStatement.setString(4, auditTrailVO.getActionExecutorName());
                int k = preparedStatement.executeUpdate();
            }
            catch (SystemError systemError)
            {
                PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "addCustomerNameBatch()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
            }
            catch (SQLException sql)
            {
                PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "addCustomerNameBatch()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, sql.getMessage(), sql.getCause());
            }
            finally
            {
                Database.closePreparedStatement(preparedStatement);
                Database.closeConnection(connection);
            }
        }
        return;
    }

    public void addCustomerCardBatch(String cardNumber, String reason,String remark,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try
        {
            String firstSix = Functions.getFirstSix(cardNumber);
            String lastFour = Functions.getLastFour(cardNumber);
            connection = Database.getConnection();
            String query = "insert into blacklist_cards(first_six,last_four,reason,remark,actionExecutorId,actionExecutorName)value(?,?,?,?,?,?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, firstSix);
            preparedStatement.setString(2, lastFour);
            preparedStatement.setString(3, reason);
            preparedStatement.setString(4, remark);
            preparedStatement.setString(5, auditTrailVO.getActionExecutorId());
            preparedStatement.setString(6, auditTrailVO.getActionExecutorName());
            int k = preparedStatement.executeUpdate();
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "addCustomerCardBatch()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException sql)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "addCustomerCardBatch()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, sql.getMessage(), sql.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
    }

    public void addCustomerEmailBatch(String cardHolderEmail, String reason,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try
        {
            String query = "insert into blacklist_email (emailAddress,reason,actionExecutorId,actionExecutorName) values (?,?,?,?)";
            connection = Database.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cardHolderEmail);
            preparedStatement.setString(2, reason);
            preparedStatement.setString(3, auditTrailVO.getActionExecutorId());
            preparedStatement.setString(4, auditTrailVO.getActionExecutorName());
            int k = preparedStatement.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "addCustomerEmailBatch()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "addCustomerEmailBatch()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
    }
    public void addCustomerVPAAddressBatch(String vpaAddress, String reason,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Functions functions = new Functions();
        try
        {
            String vpaPrefix = functions.getVpaAddressPrefix(vpaAddress);
            String query = "insert into blacklist_vpaAddress (vpaAddress,reason,actionExecutorId,actionExecutorName, vpaprefix) values (?,?,?,?, ?)";
            connection = Database.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, vpaAddress);
            preparedStatement.setString(2, reason);
            preparedStatement.setString(3, auditTrailVO.getActionExecutorId());
            preparedStatement.setString(4, auditTrailVO.getActionExecutorName());
            preparedStatement.setString(5, vpaPrefix);
            int k = preparedStatement.executeUpdate();
        }
        catch (SystemError se)
        {
            log.error("System Error Exception--->",se);
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "addCustomerVPAAddressBatch()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQLException Exception--->",e);
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "addCustomerVPAAddressBatch()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
    }

    public List<BlacklistVO> getBlockedBin(String startBin, String endBin, String accountId, String memberId, String reason,String remark, String role_interface) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        BlacklistVO blacklistVO = null;
        List<BlacklistVO> list = new ArrayList();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        try
        {
            conn = Database.getConnection();
            sb.append("select * from blacklist_bin where id>0 ");
            StringBuffer countQuery = new StringBuffer("select count(*) blacklist_bin");

            if (function.isValueNull(startBin) && function.isValueNull(endBin))
            {
                sb.append(" And startBin <='" + startBin + "'" + "AND endBin >='" + endBin + "'");
                countQuery.append(" And startBin <='" + startBin + "'" + "AND endBin >='" + endBin + "'");
            }

            if (function.isValueNull(accountId))
            {
                sb.append(" and accountid='" + accountId + "'");
                countQuery.append(" where accountid='" + accountId + "'");
            }
            if (function.isValueNull(memberId))
            {
                sb.append(" and memberid='" + memberId + "'");
                countQuery.append(" where memberid='" + memberId + "'");
            }
            if (function.isValueNull(reason))
            {
                sb.append(" and reason='" +reason+ "'");
                countQuery.append(" where reason='" +reason +"'");
            }
            if (function.isValueNull(remark))
            {
                sb.append(" and remark='" +remark+ "'");
                countQuery.append(" where remark='" +remark +"'");
            }
            sb.append(" ORDER BY TIMESTAMP DESC ");
            ps = conn.prepareStatement(sb.toString());

            rs = ps.executeQuery();
            while (rs.next())
            {
                blacklistVO = new BlacklistVO();
                blacklistVO.setAccountId(rs.getString("accountid"));
                blacklistVO.setMemberId(rs.getString("memberid"));
                blacklistVO.setBinStart(rs.getString("startBin"));
                blacklistVO.setBinEnd(rs.getString("endBin"));
                blacklistVO.setBlacklistReason(rs.getString("reason"));
                blacklistVO.setTimestamp(rs.getString("timestamp"));
                blacklistVO.setRemark(rs.getString("remark"));
                blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));
                list.add(blacklistVO);
            }
            log.debug("BIN query---" + sb.toString());
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return list;
    }

    public List<BlacklistVO> getBlockedBinPage(String startBin, String endBin, String accountId, String memberId,String reason,String remark, PaginationVO paginationVO, String role_interface) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        BlacklistVO blacklistVO = null;
        List<BlacklistVO> list = new ArrayList();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        int totalreords = 0;
        try
        {
            conn = Database.getConnection();
            sb.append("select id,accountid,memberid,startBin,endBin,timestamp,actionExecutorId,actionExecutorName ");
            if(role_interface.equalsIgnoreCase("admin") )
            {
                sb.append(",reason,remark ");
            }
            sb.append("from blacklist_bin where id>0");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_bin where id>0 ");

            if (function.isValueNull(startBin) && function.isValueNull(endBin))
            {
                sb.append(" And startBin <='" + startBin + "'" + "AND endBin >='" + endBin + "'");
                countQuery.append(" And startBin <='" + startBin + "'" + "AND endBin >='" + endBin + "'");
            }

            if (function.isValueNull(accountId))
            {
                sb.append(" and accountid='" + accountId + "'");
                countQuery.append(" AND accountid='" + accountId + "'");
            }
            if (function.isValueNull(memberId))
            {
                sb.append(" and memberid='" + memberId + "'");
                countQuery.append(" AND memberid='" + memberId + "'");
            }
            if (role_interface.equalsIgnoreCase("admin") && function.isValueNull(reason))
            {
                sb.append(" and reason='" +reason +"'");
                countQuery.append(" AND reason='" +reason+ "'");
            }
            if (role_interface.equalsIgnoreCase("admin") && function.isValueNull(remark))
            {
                sb.append(" and remark='" +remark +"'");
                countQuery.append(" AND remark='" +remark+ "'");
            }
            if (!function.isValueNull(startBin) && !function.isValueNull(endBin))
            {
                sb.append(" order by TIMESTAMP desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            }
            ps = conn.prepareStatement(sb.toString());
            log.error("select query for blockbinGetPage:: "+ps);
            rs = ps.executeQuery();
            while (rs.next())
            {
                blacklistVO = new BlacklistVO();
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.setAccountId(rs.getString("accountid"));
                blacklistVO.setMemberId(rs.getString("memberid"));
                blacklistVO.setBinStart(rs.getString("startBin"));
                blacklistVO.setBinEnd(rs.getString("endBin"));
                blacklistVO.setTimestamp(rs.getString("timestamp"));
                if (role_interface.equalsIgnoreCase("admin"))
                {
                    blacklistVO.setBlacklistReason(rs.getString("reason"));
                    blacklistVO.setRemark(rs.getString("remark"));
                }
                blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));
                list.add(blacklistVO);
            }
            ps = conn.prepareStatement(countQuery.toString());

            rs = ps.executeQuery();
            if (rs.next())
            {
                totalreords = rs.getInt(1);
            }
            if (totalreords != 0)
            {
                paginationVO.setTotalRecords(totalreords);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedBinPage()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedBinPage()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
          //  Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return list;
    }

    public int addBin(String startBin, String endBin, String accountId, String memberId,String reason,String remark,String actionExecutorId,String actionExecutorName,String role_interface) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int count = 0;
        try
        {
            String query = "insert into blacklist_bin (startBin,endBin,accountid,memberid,reason,remark,actionExecutorId,actionExecutorName) values (?,?,?,?,?,?,?,?)";
            connection = Database.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, startBin);
            preparedStatement.setString(2, endBin);
            preparedStatement.setString(3, accountId);
            preparedStatement.setString(4, memberId);
            preparedStatement.setString(5, reason);
            preparedStatement.setString(6, remark);
            preparedStatement.setString(7, actionExecutorId);
            preparedStatement.setString(8, actionExecutorName);
            log.error("ADDBIN Query:: " + preparedStatement);
            count = preparedStatement.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "addCustomerEmailBatch()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "addCustomerEmailBatch()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return count;
    }

    public List<BlacklistVO> getBlackListBinForPartner(String startBin, String endBin, String accountId, String memberId, int records, int pageno, String actionExecutorId,String actionExecutorName, HttpServletRequest req) throws PZDBViolationException
    {
        int start = 0;
        int end = 0;
        Connection conn = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        PreparedStatement ps = null;
        PreparedStatement pstmt = null;
        BlacklistVO BlacklistVO = null;
        List<BlacklistVO> list = new ArrayList();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();

        start = (pageno - 1) * records;
        end = records;

        try
        {
            conn = Database.getConnection();
            sb.append("select id, startBin,endBin,accountId,memberId, actionExecutorId,actionExecutorName from blacklist_bin where id>0 ");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_bin");

            if (function.isValueNull(memberId))
            {
                sb.append(" and memberId='" + memberId + "'");
                countQuery.append(" where memberId='" + memberId + "'");
            }

            if (function.isValueNull(startBin) && function.isValueNull(endBin))
            {
                sb.append("And startBin >='" + startBin + "'" + "AND endBin <='" + endBin + "'");
                countQuery.append("And startBin >='" + startBin + "'" + "AND endBin <='" + endBin + "'");
            }

            if (function.isValueNull(accountId))
            {
                sb.append(" and accountId='" + accountId + "'");
                countQuery.append(" and accountId='" + accountId + "'");
            }
            sb.append(" ORDER BY TIMESTAMP DESC ");
            sb.append(" limit  " + start + "," + end);
            ps = conn.prepareStatement(sb.toString());
            rs = ps.executeQuery();
            while (rs.next())
            {
                BlacklistVO = new BlacklistVO();
                BlacklistVO.setId(rs.getString("id"));
                BlacklistVO.setAccountId(rs.getString("accountid"));
                BlacklistVO.setMemberId(rs.getString("memberId"));
                BlacklistVO.setBinStart(rs.getString("startBin"));
                BlacklistVO.setBinEnd(rs.getString("endBin"));
                BlacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                BlacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));
                list.add(BlacklistVO);
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
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getWhiteListBin()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getWhiteListBin()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return list;
    }

    public void unblockBin(String Id) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        try
        {
            conn = Database.getConnection();
            String dQuery = "delete from blacklist_bin where id=?";
            ps = conn.prepareStatement(dQuery);
            ps.setString(1, Id);
            ps.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "unblockCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "unblockCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
    }

    public int uploadBins(List<String> queryBatch) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int count = 0;
        try
        {
            connection = Database.getConnection();
            for (String batch : queryBatch)
            {
                if (batch != null && batch.length() > 0 && batch.charAt(batch.length() - 1) == ',')
                {
                    batch = batch.substring(0, batch.length() - 1);
                }
                preparedStatement = connection.prepareStatement(batch.toString());
                count = count + preparedStatement.executeUpdate();
            }
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "uploadBins()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException sql)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "uploadBins()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, sql.getMessage(), sql.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return count;
    }

    public boolean deleteBlacklistCard(String id) throws PZDBViolationException
    {
        log.debug("inside deleteBlacklistCard:::");
        boolean isRecordAvailable = false;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        Functions functions = new Functions();
        int counter = 1;
        try
        {
            conn = Database.getRDBConnection();
            StringBuilder query = new StringBuilder("DELETE from blacklist_country");

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

            log.debug("delete query:::::" + preparedStatement);
            int i = preparedStatement.executeUpdate();
            if (i > 0)
            {
                log.debug("inside if true");
                isRecordAvailable = true;
            }
        }
        catch (SQLException e)
        {
            log.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(BlacklistDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to blacklist_country table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            log.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(BlacklistDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to blacklist_country table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        log.debug("return:::::" + isRecordAvailable);
        return isRecordAvailable;
    }

    public boolean deleteBlacklistBin(String id) throws PZDBViolationException
    {
        log.debug("inside deleteBlacklistCard:::");
        boolean isRecordAvailable = false;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        Functions functions = new Functions();
        int counter = 1;
        try
        {
            conn = Database.getRDBConnection();
            StringBuilder query = new StringBuilder("DELETE from blacklist_bin");

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
            log.debug("delete query:::::" + preparedStatement);
            int i = preparedStatement.executeUpdate();
            if (i > 0)
            {
                log.debug("inside if true");
                isRecordAvailable = true;
            }
        }
        catch (SQLException e)
        {
            log.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(BlacklistDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to blacklist_country table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            log.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(BlacklistDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to blacklist_country table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        log.debug("return:::::" + isRecordAvailable);
        return isRecordAvailable;
    }


    public boolean chackmemberId(String memberId) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        boolean flag = false;

        try
        {
            connection = Database.getRDBConnection();
            StringBuffer stringBuffer = new StringBuffer("SELECT memberid FROM members where memberid ='" + memberId + "'");
            preparedStatement = connection.prepareStatement(stringBuffer.toString());
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                flag = true;
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "chackmemberId()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "chackmemberId()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return flag;
    }


    //1 dec 2018
    public List<BlacklistVO> getBlockedAllIP(String AllIp, String memberId, String selectIpVersion, PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        ResultSet rsCommonIPList = null;
        PreparedStatement ps = null;
        BlacklistVO blacklistVO = null;
        PreparedStatement psCountOfCommonIpList = null;
        int counter = 1;
        List<BlacklistVO> list = new ArrayList<BlacklistVO>();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        try
        {
            conn = Database.getRDBConnection();
            sb.append("SELECT * FROM blacklist_ip ");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_ip");

            sb.append(" where id>0 ");
            countQuery.append(" where id>0 ");

            if (function.isValueNull(AllIp))
            {
                sb.append("  AND ipaddress = '" + AllIp + "'");
                countQuery.append("  AND ipaddress = '" + AllIp + "'");
            }
            if (function.isValueNull(memberId))
            {
                sb.append("  AND memberId = '" + memberId + "'");
                countQuery.append(" AND memberId = '" + memberId + "'");
            }
            if (function.isValueNull(selectIpVersion))
            {
                sb.append("  AND TYPE = '" + selectIpVersion + "'");
                countQuery.append("  AND TYPE = '" + selectIpVersion + "'");
            }
            sb.append(" ORDER BY ID DESC LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            ps = conn.prepareStatement(sb.toString());
            rs = ps.executeQuery();
            while (rs.next())
            {
                blacklistVO = new BlacklistVO();
                blacklistVO.setMemberId(rs.getString("memberId"));
                blacklistVO.setStartIpv4(rs.getString("ipaddress"));
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.selectIpVersion(rs.getString("type"));
                list.add(blacklistVO);
            }
            counter = 1;
            psCountOfCommonIpList = conn.prepareStatement(countQuery.toString());
            rsCommonIPList = psCountOfCommonIpList.executeQuery();
            if (rsCommonIPList.next())
            {
                paginationVO.setTotalRecords(rsCommonIPList.getInt(1));
            }

        }
        catch (SystemError se)
        {
            log.error("Internal Error", se);
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedAllIP()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            log.error("Internal Error", e);
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedAllIP()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {

            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return list;
    }

    public List<BlacklistVO> getBlockedip(String memberId, String AllIp, String selectIpVersion,PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        ResultSet rsCommonIPList = null;
        PreparedStatement ps = null;
        BlacklistVO blacklistVO = null;
        PreparedStatement psCountOfCommonIpList = null;
        int counter = 1;
        List<BlacklistVO> list = new ArrayList<BlacklistVO>();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        try
        {
            conn = Database.getRDBConnection();
            sb.append("SELECT memberId,ipaddress,id, actionExecutorId,actionExecutorName type FROM blacklist_ip where id>0 ");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_ip where id>0");

            if (function.isValueNull(memberId))
            {
                sb.append("  AND memberId = '" + memberId + "'");
                countQuery.append(" AND memberId = '" + memberId + "'");
            }
            if (function.isValueNull(AllIp))
            {
                sb.append("  AND ipaddress = '" + AllIp + "'");
                countQuery.append("  AND ipaddress = '" + AllIp + "'");
            }
            if (function.isValueNull(selectIpVersion))
            {
                sb.append("  AND type = '" + selectIpVersion + "'");
                countQuery.append("  AND type = '" + selectIpVersion + "'");
            }
            sb.append(" ORDER BY id DESC LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            ps = conn.prepareStatement(sb.toString());
            rs = ps.executeQuery();

            while (rs.next())
            {
                blacklistVO = new BlacklistVO();
                blacklistVO.setMemberId(rs.getString("memberId"));
                blacklistVO.setStartIpv4(rs.getString("ipaddress"));
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.selectIpVersion(rs.getString("type"));
               blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
              blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));
                list.add(blacklistVO);
            }
            counter = 1;
            psCountOfCommonIpList = conn.prepareStatement(countQuery.toString());
            rsCommonIPList = psCountOfCommonIpList.executeQuery();
            if (rsCommonIPList.next())
            {
                paginationVO.setTotalRecords(rsCommonIPList.getInt(1));
            }

        }
        catch (SystemError se)
        {
            log.error("Internal Error", se);
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedip()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            log.error("Internal Error", e);
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedip()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {

            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return list;
    }

    public List<BlacklistVO> getBlockedipOne(String memberId, String AllIp, String selectIpVersion, PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        ResultSet rsCommonIPList = null;
        PreparedStatement ps = null;
        BlacklistVO blacklistVO = null;
        PreparedStatement psCountOfCommonIpList = null;
        int counter = 1;
        List<BlacklistVO> list = new ArrayList<BlacklistVO>();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        try
        {
            conn = Database.getRDBConnection();
            sb.append("SELECT memberId,ipaddress,id,type,actionExecutorId,actionExecutorName FROM blacklist_ip where id>0 ");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_ip where id>0");

            if (function.isValueNull(memberId))
            {
                sb.append("  AND memberId = '" + memberId + "'");
                countQuery.append(" AND memberId = '" + memberId + "'");
            }
            if (function.isValueNull(AllIp))
            {
                sb.append("  AND ipaddress = '" + AllIp + "'");
                countQuery.append("  AND ipaddress = '" + AllIp + "'");
            }
            if (function.isValueNull(selectIpVersion))
            {
                sb.append("  AND type = '" + selectIpVersion + "'");
                countQuery.append("  AND type = '" + selectIpVersion + "'");
            }
            sb.append(" ORDER BY id DESC LIMIT 0,1 ");
            ps = conn.prepareStatement(sb.toString());
            rs = ps.executeQuery();

            while (rs.next())
            {
                blacklistVO = new BlacklistVO();
                blacklistVO.setMemberId(rs.getString("memberId"));
                blacklistVO.setStartIpv4(rs.getString("ipaddress"));
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.selectIpVersion(rs.getString("type"));
               blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
               blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));
                list.add(blacklistVO);
            }
            counter = 1;
            psCountOfCommonIpList = conn.prepareStatement(countQuery.toString());
            rsCommonIPList = psCountOfCommonIpList.executeQuery();
            if (rsCommonIPList.next())
            {
                paginationVO.setTotalRecords(rsCommonIPList.getInt(1));
            }

        }
        catch (SystemError se)
        {
            log.error("Internal Error", se);
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedipOne()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            log.error("Internal Error", e);
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedipOne()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {

            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return list;
    }

    public List<BlacklistVO> getBlockedipForPartner(String memberId, String allIP, String selectIpVersion,String actionExecutorId,String actionExecutorName, PaginationVO paginationVO) throws PZDBViolationException
    {
        log.debug("inside getBlockedipForPartner:::");
        Connection conn = null;
        Functions functions = new Functions();
        ResultSet rs = null;
        ResultSet rsCommonIPList = null;
        PreparedStatement ps = null;
        BlacklistVO blacklistVO = null;
        PreparedStatement psCountOfCommonIpList = null;
        int counter = 1;
        List<BlacklistVO> list = new ArrayList<BlacklistVO>();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        try
        {
            conn = Database.getRDBConnection();
            sb.append("SELECT id, memberId,ipaddress,type, actionExecutorId,actionExecutorName FROM blacklist_ip where id>0 ");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_ip where id>0");

            if (function.isValueNull(memberId))
            {
                sb.append(" AND memberId = '" + memberId + "'");
                countQuery.append(" AND memberId = '" + memberId + "'");
            }
            if (function.isValueNull(allIP))
            {
                sb.append("  AND ipaddress = '" + allIP + "'");
                countQuery.append("  AND ipaddress = '" + allIP + "'");
            }
            if (function.isValueNull(selectIpVersion))
            {
                sb.append("  AND type = '" + selectIpVersion + "'");
                countQuery.append("  AND type = '" + selectIpVersion + "'");
            }
            sb.append(" ORDER BY id DESC LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            ps = conn.prepareStatement(sb.toString());
            rs = ps.executeQuery();
            while (rs.next())
            {
                blacklistVO = new BlacklistVO();
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.setMemberId(rs.getString("memberId"));
                blacklistVO.setStartIpv4(rs.getString("ipaddress"));
                blacklistVO.selectIpVersion(rs.getString("type"));
                blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));
                list.add(blacklistVO);
            }
            counter = 1;
            psCountOfCommonIpList = conn.prepareStatement(countQuery.toString());
            rsCommonIPList = psCountOfCommonIpList.executeQuery();
            if (rsCommonIPList.next())
            {
                paginationVO.setTotalRecords(rsCommonIPList.getInt(1));
            }

        }
        catch (SystemError se)
        {
            log.error("Internal Error", se);
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedipForPartner()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            log.error("Internal Error", e);
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedipForPartner()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {

            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return list;
    }

    public Hashtable retrievIPForMerchant(String memberId, String allIP, String selectIpVersion, PaginationVO paginationVO, boolean isLimit)
    {
        Hashtable recordHash = new Hashtable();
        Connection conn = null;
        Functions functions = new Functions();
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT id, ipAddress,type FROM blacklist_ip WHERE memberId=?");
            StringBuffer count = new StringBuffer("SELECT COUNT(*) FROM ipwhitelist WHERE memberId=?");
            if (functions.isValueNull(allIP) && !"".equals(allIP))
            {
                query.append(" AND ipAddress='" + allIP + "'");
                count.append(" AND ipAddress='" + allIP + "'");
            }
            if (functions.isValueNull(selectIpVersion))
            {
                query.append(" AND type='" + selectIpVersion + "'");
                count.append(" AND type='" + selectIpVersion + "'");
            }
            if (isLimit == true)
            {
                query.append(" order by id desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            }

            PreparedStatement p = conn.prepareStatement(query.toString());
            p.setString(1, memberId);
            PreparedStatement p1 = conn.prepareStatement(count.toString());
            p1.setString(1, memberId);
            recordHash = Database.getHashFromResultSet(p.executeQuery());
            ResultSet rs = p1.executeQuery();
            rs.next();
            int totalRecords = rs.getInt(1);
            recordHash.put("totalrecords", "" + totalRecords);
            recordHash.put("records", "0");

            if (totalRecords > 0)
                recordHash.put("records", "" + (recordHash.size() - 2));

            /*while (rs.next())
            {
                recordHash.put(memberId,rs.getString("ipAddress"));
            }*/

        }
        catch (SystemError systemError)
        {
            log.error("System Error..." + systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL Exception..." + e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return recordHash;
    }

    public boolean insertIPForMerchant(String memberId, String allIP, String selectIpVersion, String actionExecutorId,String actionExecutorName)//String endIpAddress
    {
        Connection conn = null;
        boolean result = false;
        Functions functions = new Functions();
        int counter = 1;
        try
        {
            conn = Database.getConnection();
            StringBuilder query = new StringBuilder("INSERT INTO blacklist_ip (memberId,ipAddress,type,actionExecutorId,actionExecutorName) VALUES (?,?,?,?,?)");
            PreparedStatement p = conn.prepareStatement(query.toString());
            if (functions.isValueNull(memberId))
            {
                p.setString(1, memberId);
            }
            else
            {
                p.setInt(1, Integer.parseInt(""));
            }
            p.setString(2, allIP);
            p.setString(3, selectIpVersion);
            p.setString(4, actionExecutorId);
            p.setString(5, actionExecutorName);
            int k = p.executeUpdate();
            if (k > 0)
            {
                result = true;
            }
            log.debug("inserted..." + p);
        }
        catch (SystemError systemError)
        {
            log.error("System Error..." , systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL Exception..." , e);

        }
        finally
        {
            Database.closeConnection(conn);
        }
        return result;
    }

    public boolean insertIPForMerchant(String allIP, String selectIpVersion ,String actionExecutorId,String actionExecutorName)//String endIpAddress
    {
        Connection conn = null;
        boolean result = false;
        Functions functions = new Functions();
        int counter = 1;
        try
        {
            conn = Database.getConnection();
            StringBuilder query = new StringBuilder("INSERT INTO blacklist_ip (ipAddress,type,actionExecutorId,actionExecutorName) VALUES (?,?,?,?)");
            PreparedStatement p = conn.prepareStatement(query.toString());
            p.setString(1, allIP);
            p.setString(2, selectIpVersion);
            p.setString(3,actionExecutorId);
            p.setString(4,actionExecutorName);
            int k = p.executeUpdate();
            if (k > 0)
            {
                result = true;
            }
            log.debug("inserted..." + p);
        }
        catch (SystemError systemError)
        {
            log.error("System Error..." , systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL Exception..." , e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return result;
    }

    public boolean deleteIPForMerchant(String id)
    {
        Connection conn = null;
        boolean result = false;
        try
        {
            conn = Database.getConnection();
            String query = "DELETE FROM blacklist_ip WHERE id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, id);
            int k = pstmt.executeUpdate();
            if (k > 0)
            {
                result = true;
            }
        }
        catch (SystemError systemError)
        {
            log.error("System Error..." + systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL Exception..." + e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return result;
    }

    public boolean checkForGlobal(String allIP) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        boolean flag = false;

        try
        {
            connection = Database.getRDBConnection();
            StringBuffer stringBuffer = new StringBuffer("SELECT * FROM blacklist_ip where id>0 And ipaddress ='" + allIP + "' AND memberId IS NULL ");
            preparedStatement = connection.prepareStatement(stringBuffer.toString());
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                flag = true;
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "CheckForGlobal()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "CheckForGlobal()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return flag;
    }

    public List<BlacklistVO> getBlackListedVpaAddress(String vpaAddress,String reason, PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection con = null;
        int totalreords = 0;
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer();
        PreparedStatement ps = null;
        List<BlacklistVO> listOfCustomerIds = new ArrayList<BlacklistVO>();
        Functions functions = new Functions();
        try
        {
            con = Database.getRDBConnection();
            sb.append("select * from blacklist_vpaAddress where id>0");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_vpaAddress where  id>0");

            if (functions.isValueNull(vpaAddress))
            {
                sb.append(" and vpaAddress='" + vpaAddress+"'");
                countQuery.append(" and vpaAddress='" + vpaAddress+"'" );
            }
            if (functions.isValueNull(reason))
            {
                sb.append(" and reason='" + reason +"'");
                countQuery.append(" and reason='" + reason +"'");
            }
            sb.append(" order by TIMESTAMP desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            ps = con.prepareStatement(sb.toString());

            rs = ps.executeQuery();
           // log.error("select query - ");
           // log.error("select query :::::"+sb.);
            while (rs.next())
            {
                BlacklistVO blacklistVO = new BlacklistVO();
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.setVpaAddress(rs.getString("vpaAddress"));
                blacklistVO.setBlacklistReason(rs.getString("reason"));
                blacklistVO.setTimestamp(rs.getString("timestamp"));
                blacklistVO.setRemark(rs.getString("remark"));
                blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));

                listOfCustomerIds.add(blacklistVO);

            }
            ps = con.prepareStatement(countQuery.toString());
            rs = ps.executeQuery();
            if (rs.next())
            {
                totalreords = rs.getInt(1);
            }
            if (totalreords != 0)
            {
                paginationVO.setTotalRecords(totalreords);
            }

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlackListedCustomerIds", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlackListedCustomerIds", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
        return listOfCustomerIds;
    }

    public int insertBlockedVpaAddress(String vpaAddress, String reason, String actionExecutorId, String actionExecutorName) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        int count = 0;
        Functions functions = new Functions();
        try
        {
            String vpaPrefix = functions.getVpaAddressPrefix(vpaAddress);
            conn = Database.getConnection();
            String insertQuery = "insert into blacklist_vpaAddress (vpaAddress,reason,actionExecutorId,actionExecutorName, vpaprefix) values (?,?,?,?,?)";
            ps = conn.prepareStatement(insertQuery);
            ps.setString(1, vpaAddress);
            ps.setString(2, reason);
            ps.setString(3, actionExecutorId);
            ps.setString(4, actionExecutorName);
            ps.setString(5, vpaPrefix);
            log.error("insert blacklist_vpaAddress Query--"+ps);
            count = ps.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlockedCustomerId()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlockedCustomerId()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return count;
    }

    public void unblockVpaAddress(String Id) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        BlacklistVO blacklistVO = new BlacklistVO();
        try
        {
            conn = Database.getConnection();
            String dQuery = "delete from blacklist_vpaAddress where id=?";
            ps = conn.prepareStatement(dQuery);
            ps.setString(1, Id);
            log.debug("PS::::::::" + ps);
            ps.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "unblockCustomerId()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "unblockCustomerId()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
    }
    public void addCustomerPhone(String phone, String reason,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try
        {
            String query = "insert into blacklist_phone (phone,reason,actionExecutorId,actionExecutorName) values (?,?,?,?)";
            connection = Database.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, phone);
            preparedStatement.setString(2, reason);
            preparedStatement.setString(3, auditTrailVO.getActionExecutorId());
            preparedStatement.setString(4, auditTrailVO.getActionExecutorName());
            int k = preparedStatement.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "addCustomerPhone()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "addCustomerPhone()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
    }
    public void addCustomerPhoneBatch(Set<String> phoneList, String reason,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        try
        {
            String query = "insert into blacklist_phone (phone,reason,actionExecutorId,actionExecutorName) values (?,?,?,?)";
            conn = Database.getConnection();
            ps = conn.prepareStatement(query);
            for (String phone : phoneList)
            {
                ps.setString(1, phone);
                ps.setString(2, reason);
                ps.setString(3, auditTrailVO.getActionExecutorId());
                ps.setString(4, auditTrailVO.getActionExecutorName());
                ps.addBatch();
            }
            int k[] = ps.executeBatch();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "addCustomerEmailBatch()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "addCustomerEmailBatch()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
    }

    public List<BlacklistVO> getBlackListedPhoneNo(String phone,String reason, PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection con = null;
        int totalreords = 0;
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer();
        PreparedStatement ps = null;
        List<BlacklistVO> listOfphones = new ArrayList<BlacklistVO>();
        Functions functions = new Functions();
        try
        {
            con = Database.getRDBConnection();
            sb.append("select * from blacklist_phone where id>0");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_phone where  id>0");

            if (functions.isValueNull(phone))
            {
                sb.append(" and phone='" + phone+"'");
                countQuery.append(" and phone='" + phone+"'" );
            }
            if (functions.isValueNull(reason))
            {
                sb.append(" and reason='" + reason +"'");
                countQuery.append(" and reason='" + reason +"'");
            }
            System.out.println("pagestart"+paginationVO.getStart());
            System.out.println("pagestart"+paginationVO.getEnd());
            sb.append(" order by TIMESTAMP desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());

            ps = con.prepareStatement(sb.toString());

            rs = ps.executeQuery();
            // log.error("select query - ");
            // log.error("select query :::::"+sb.);
            while (rs.next())
            {
                BlacklistVO blacklistVO = new BlacklistVO();
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.setPhone(rs.getString("phone"));
                blacklistVO.setBlacklistReason(rs.getString("reason"));
                blacklistVO.setTimestamp(rs.getString("timestamp"));
                blacklistVO.setRemark(rs.getString("remark"));
                blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));

                listOfphones.add(blacklistVO);

            }
            ps = con.prepareStatement(countQuery.toString());
            rs = ps.executeQuery();
            if (rs.next())
            {
                totalreords = rs.getInt(1);
            }
            if (totalreords != 0)
            {
                paginationVO.setTotalRecords(totalreords);
            }

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlackListedPhoneNo", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlackListedPhoneNo", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
        return listOfphones;
    }

    public void unblockPhoneNo(String Id) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        try
        {
            conn = Database.getConnection();
            String dQuery = "delete from blacklist_phone where id=?";
            ps = conn.prepareStatement(dQuery);
            ps.setString(1, Id);
            log.debug("PS::::::::" + ps);
            ps.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "unblockPhoneNo()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "unblockPhoneNo()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
    }
    public int insertBlockedPhone(String phone, String reason, String actionExecutorId, String actionExecutorName) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        int count = 0;
        try
        {
            conn = Database.getConnection();
            String insertQuery = "insert into blacklist_phone (phone,reason,actionExecutorId,actionExecutorName) values (?,?,?,?)";
            ps = conn.prepareStatement(insertQuery);
            ps.setString(1, phone);
            ps.setString(2, reason);
            ps.setString(3, actionExecutorId);
            ps.setString(4, actionExecutorName);
            log.error("insert blacklist_phone Query--"+ps);
            count = ps.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlockedPhone()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlockedPhone()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return count;
    }
    public int insertBlacklistIp(String memberId, String allIP, String selectIpVersion, String actionExecutorId,String actionExecutorName)//String endIpAddress
    {
        System.out.println("insertBlacklistIp");
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        int count = 0;
        try
        {
            conn = Database.getConnection();
            String insertQuery = "INSERT INTO blacklist_ip (memberId,ipAddress,type,actionExecutorId,actionExecutorName) VALUES (?,?,?,?,?)";
            ps = conn.prepareStatement(insertQuery);
            ps.setString(1, memberId);
            ps.setString(2, allIP);
            ps.setString(3, selectIpVersion);
            ps.setString(4, actionExecutorId);
            ps.setString(5, actionExecutorName);
            log.error("insert blacklist_ip Query--" + ps);
            count = ps.executeUpdate();
        }
        catch (SystemError se)
        {
            try
            {
                PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlacklistIp()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
            }
            catch (PZDBViolationException e)
            {
                e.printStackTrace();
            }
        }
        catch (SQLException e)
        {
            try
            {
                PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlacklistIp()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
            }
            catch (PZDBViolationException e1)
            {
                e1.printStackTrace();
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return count;
    }
    public List<BlacklistVO> getBlockedCountryPages(String country, String accountId, String memberId,String reason,String remark, PaginationVO paginationVO) throws PZDBViolationException
    {
        int totalreords = 0;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        BlacklistVO blacklistVO = null;
        List<BlacklistVO> listOfCountry = new ArrayList();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        try
        {
            conn = Database.getConnection();
            sb.append("select * from blacklist_country where id>0");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_country where  id>0");

            if (function.isValueNull(country))
            {
                sb.append(" and country='" + country + "'");
                countQuery.append(" and country='" + country + "'");
            }
            if (function.isValueNull(accountId))
            {
                sb.append(" and accountid='" + accountId + "'");
                countQuery.append(" and accountid='" + accountId + "'");
            }
            if (function.isValueNull(memberId))
            {
                sb.append(" and memberid='" + memberId + "'");
                countQuery.append(" and memberid='" + memberId + "'");
            }if (function.isValueNull(reason))
            {
                sb.append(" and reason='" + reason + "'");
                countQuery.append(" and reason='" + reason + "'");
            }if (function.isValueNull(remark))
            {
                sb.append(" and remark='" + remark + "'");
                countQuery.append(" and remark='" + remark + "'");
            }
            sb.append(" order by TIMESTAMP desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            ps = conn.prepareStatement(sb.toString());
            rs = ps.executeQuery();
            while (rs.next())
            {
                blacklistVO = new BlacklistVO();
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.setCountry(rs.getString("country"));
                blacklistVO.setTelnocc(rs.getString("telnocc"));
                blacklistVO.setCountryCode(rs.getString("country_code"));
                blacklistVO.setThreeDigitCountryCode(rs.getString("three_digit_country_code"));
                blacklistVO.setAccountId(rs.getString("accountid"));
                blacklistVO.setMemberId(rs.getString("memberid"));
                blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));
                blacklistVO.setBlacklistReason(rs.getString("reason"));
                blacklistVO.setRemark(rs.getString("remark"));
                blacklistVO.setTimestamp(rs.getString("timestamp"));
                listOfCountry.add(blacklistVO);
            }
            ps = conn.prepareStatement(countQuery.toString());
            rs = ps.executeQuery();
            if (rs.next())
            {
                totalreords = rs.getInt(1);
            }
            if (totalreords != 0)
            {
                paginationVO.setTotalRecords(totalreords);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedCountryPage()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedCountryPage()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return listOfCountry;
    }
    public List<BlacklistVO> getBlockedCountrys(String country, String accountId, String memberId,String reason,String remark) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        BlacklistVO blacklistVO = null;
        List<BlacklistVO> listOfCountry = new ArrayList();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        try
        {
            conn = Database.getConnection();
            sb.append("select * from blacklist_country where id>0");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_country where  id>0");

            if (function.isValueNull(country))
            {
                sb.append(" and country='" + country + "'");
                countQuery.append(" and country='" + country + "'");
            }
            if (function.isValueNull(accountId))
            {
                sb.append(" and accountid='" + accountId + "'");
                countQuery.append(" and accountid='" + accountId + "'");
            }
            if (function.isValueNull(memberId))
            {
                sb.append(" and memberid='" + memberId + "'");
                countQuery.append(" and memberid='" + memberId + "'");
            }if (function.isValueNull(reason))
            {
                sb.append(" and reason='" + reason + "'");
                countQuery.append(" and reason='" + reason + "'");
            }if (function.isValueNull(remark))
            {
                sb.append(" and remark='" + remark + "'");
                countQuery.append(" and remark='" + remark + "'");
            }
            ps = conn.prepareStatement(sb.toString());
            rs = ps.executeQuery();
            while (rs.next())
            {
                blacklistVO = new BlacklistVO();
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.setCountry(rs.getString("country"));
                blacklistVO.setTelnocc(rs.getString("telnocc"));
                blacklistVO.setCountryCode(rs.getString("country_code"));
                blacklistVO.setThreeDigitCountryCode(rs.getString("three_digit_country_code"));
                blacklistVO.setAccountId(rs.getString("accountid"));
                blacklistVO.setMemberId(rs.getString("memberid"));
                blacklistVO.setBlacklistReason(rs.getString("reason"));
                blacklistVO.setRemark(rs.getString("remark"));
                listOfCountry.add(blacklistVO);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return listOfCountry;
    }
    public int insertBlockedCountrys(String country, String code, String telCc, String three_digit_country_code, String accountId, String memberId,String actionExecutorId,String actionExecutorName,String reason,String remark) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        int count = 0;
        try
        {
            conn = Database.getConnection();
            String insertQuery = "insert into blacklist_country (country,country_code,telnocc,accountid,memberid,three_digit_country_code,actionExecutorId,actionExecutorName,reason,remark) values (?,?,?,?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(insertQuery);
            ps.setString(1, country);
            ps.setString(2, code);
            ps.setString(3, telCc);
            ps.setString(4, accountId);
            ps.setString(5, memberId);
            ps.setString(6, three_digit_country_code);
            ps.setString(7, actionExecutorId);
            ps.setString(8, actionExecutorName);
            ps.setString(9, reason);
            ps.setString(10, remark);
            count = ps.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlockedCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlockedCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return count;
    }
    public List<BlacklistVO> getBlackListedCardsPages(String firstSix, String lastFour,String actionExecutorId,String actionExecutorName,String remark,PaginationVO paginationVO,String reason) throws PZDBViolationException
    {
        int totalreords = 0;
        Connection con = null;
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer();
        List<BlacklistVO> listOfCard = new ArrayList<BlacklistVO>();
        Functions functions = new Functions();
        try
        {
            con = Database.getRDBConnection();
            sb.append("select * from blacklist_cards where id>0");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_cards where id>0");

            if (functions.isValueNull(firstSix) && !functions.isValueNull(lastFour))
            {
                sb.append(" and first_six=" + firstSix);
                countQuery.append(" and first_six=" + firstSix);
            }
            if (functions.isValueNull(lastFour) && !functions.isValueNull(firstSix))
            {
                sb.append(" and last_four=" + lastFour);
                countQuery.append(" and last_four=" + lastFour);
            }
            if (functions.isValueNull(firstSix) && functions.isValueNull(lastFour))
            {
                sb.append(" and first_six=" + firstSix + " and last_four=" + lastFour);
                countQuery.append(" and first_six=" + firstSix + " and last_four=" + lastFour);
            }
            if (functions.isValueNull(remark))
            {
                sb.append(" and remark='" + remark+"'");
                countQuery.append(" and remark='" + remark+"'");
            }
            if (functions.isValueNull(reason))
            {
                sb.append(" and reason='" +reason+ "'");
                countQuery.append(" and reason='" +reason+ "'");
            }
            sb.append(" order by id DESC LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            PreparedStatement ps = con.prepareStatement(sb.toString());
            rs = ps.executeQuery();
            log.error("select blacklistCard query---" + ps);

            while (rs.next())
            {
                BlacklistVO blacklistVO = new BlacklistVO();
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.setFirstSix(rs.getString("first_six"));
                blacklistVO.setLastFour(rs.getString("last_four"));
                blacklistVO.setBlacklistCardReason(rs.getString("reason"));
                blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));
                blacklistVO.setRemark(rs.getString("remark"));
                blacklistVO.setTimestamp(rs.getString("timestamp"));

                listOfCard.add(blacklistVO);

            }
            ps = con.prepareStatement(countQuery.toString());
            log.error("select  query---" + ps);
            rs = ps.executeQuery();
            log.error("rs query---" + rs);

            log.error("countquery ---" + countQuery);

            if (rs.next())
            {
                totalreords = rs.getInt(1);
            }
            if (totalreords != 0)
            {
                paginationVO.setTotalRecords(totalreords);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlackListedCardsPage()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlackListedCardsPage()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
        return listOfCard;
    }
    public List<BlacklistVO> getBlockedipForAdmin(String memberId, String allIP, String selectIpVersion,String actionExecutorId,String actionExecutorName,String reason,String remark, PaginationVO paginationVO) throws PZDBViolationException
    {
        log.debug("inside getBlockedipForAdmin:::");
        Connection conn = null;
        Functions functions = new Functions();
        ResultSet rs = null;
        ResultSet rsCommonIPList = null;
        PreparedStatement ps = null;
        BlacklistVO blacklistVO = null;
        PreparedStatement psCountOfCommonIpList = null;
        int counter = 1;
        List<BlacklistVO> list = new ArrayList<BlacklistVO>();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        try
        {
            conn = Database.getRDBConnection();
            sb.append("SELECT id, memberId,ipaddress,type, actionExecutorId,actionExecutorName,reason,remark,timestamp FROM blacklist_ip where id>0 ");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_ip where id>0");

            if (function.isValueNull(memberId))
            {
                sb.append(" AND memberId = '" + memberId + "'");
                countQuery.append(" AND memberId = '" + memberId + "'");
            }
            if (function.isValueNull(allIP))
            {
                sb.append("  AND ipaddress = '" + allIP + "'");
                countQuery.append("  AND ipaddress = '" + allIP + "'");
            }
            if (function.isValueNull(selectIpVersion))
            {
                sb.append("  AND type = '" + selectIpVersion + "'");
                countQuery.append("  AND type = '" + selectIpVersion + "'");
            }if (function.isValueNull(reason))
            {
                sb.append("  AND reason = '" + reason + "'");
                countQuery.append("  AND reason = '" + reason + "'");
            }if (function.isValueNull(remark))
            {
                sb.append("  AND remark = '" + remark + "'");
                countQuery.append("  AND remark = '" + remark + "'");
            }
            sb.append(" ORDER BY id DESC LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            ps = conn.prepareStatement(sb.toString());
            rs = ps.executeQuery();
            while (rs.next())
            {
                blacklistVO = new BlacklistVO();
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.setMemberId(rs.getString("memberId"));
                blacklistVO.setStartIpv4(rs.getString("ipaddress"));
                blacklistVO.selectIpVersion(rs.getString("type"));
                blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));
                blacklistVO.setBlacklistReason(rs.getString("reason"));
                blacklistVO.setRemark(rs.getString("remark"));
                blacklistVO.setTimestamp(rs.getString("timestamp"));
                list.add(blacklistVO);
            }
            counter = 1;
            psCountOfCommonIpList = conn.prepareStatement(countQuery.toString());
            rsCommonIPList = psCountOfCommonIpList.executeQuery();
            if (rsCommonIPList.next())
            {
                paginationVO.setTotalRecords(rsCommonIPList.getInt(1));
            }

        }
        catch (SystemError se)
        {
            log.error("Internal Error", se);
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedipForAdmin()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            log.error("Internal Error", e);
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedipForAdmin()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {

            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return list;
    }
    public boolean insertIPForAdmin(String allIP, String selectIpVersion ,String actionExecutorId,String actionExecutorName,String reason,String remark)//String endIpAddress
    {
        Connection conn = null;
        boolean result = false;
        Functions functions = new Functions();
        int counter = 1;
        try
        {
            conn = Database.getConnection();
            StringBuilder query = new StringBuilder("INSERT INTO blacklist_ip (ipAddress,type,actionExecutorId,actionExecutorName,reason,remark) VALUES (?,?,?,?,?,?)");
            PreparedStatement p = conn.prepareStatement(query.toString());
            p.setString(1, allIP);
            p.setString(2, selectIpVersion);
            p.setString(3,actionExecutorId);
            p.setString(4,actionExecutorName);
            p.setString(5,reason);
            p.setString(6,remark);
            int k = p.executeUpdate();
            if (k > 0)
            {
                result = true;
            }
            log.debug("inserted..." + p);
        }
        catch (SystemError systemError)
        {
            log.error("System Error...", systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL Exception...", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return result;
    }
    public boolean insertIPForAdmin(String memberId, String allIP, String selectIpVersion, String actionExecutorId,String actionExecutorName,String reason,String remark)//String endIpAddress
    {
        Connection conn = null;
        boolean result = false;
        Functions functions = new Functions();
        int counter = 1;
        try
        {
            conn = Database.getConnection();
            StringBuilder query = new StringBuilder("INSERT INTO blacklist_ip (memberId,ipAddress,type,actionExecutorId,actionExecutorName,reason,remark) VALUES (?,?,?,?,?,?,?)");
            PreparedStatement p = conn.prepareStatement(query.toString());
            if (functions.isValueNull(memberId))
            {
                p.setString(1, memberId);
            }
            else
            {
                p.setInt(1, Integer.parseInt(""));
            }
            p.setString(2, allIP);
            p.setString(3, selectIpVersion);
            p.setString(4, actionExecutorId);
            p.setString(5, actionExecutorName);
            p.setString(6, reason);
            p.setString(7, remark);
            int k = p.executeUpdate();
            if (k > 0)
            {
                result = true;
            }
            log.debug("inserted..." + p);
        }
        catch (SystemError systemError)
        {
            log.error("System Error..." , systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL Exception..." , e);

        }
        finally
        {
            Database.closeConnection(conn);
        }
        return result;
    }
    public List<BlacklistVO> getBlockedipOnes(String memberId, String AllIp, String selectIpVersion,String reason,String remark, PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        ResultSet rsCommonIPList = null;
        PreparedStatement ps = null;
        BlacklistVO blacklistVO = null;
        PreparedStatement psCountOfCommonIpList = null;
        int counter = 1;
        List<BlacklistVO> list = new ArrayList<BlacklistVO>();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        try
        {
            conn = Database.getRDBConnection();
            sb.append("SELECT memberId,ipaddress,id,type,actionExecutorId,actionExecutorName,reason,remark,timestamp FROM blacklist_ip where id>0 ");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_ip where id>0");

            if (function.isValueNull(memberId))
            {
                sb.append("  AND memberId = '" + memberId + "'");
                countQuery.append(" AND memberId = '" + memberId + "'");
            }
            if (function.isValueNull(AllIp))
            {
                sb.append("  AND ipaddress = '" + AllIp + "'");
                countQuery.append("  AND ipaddress = '" + AllIp + "'");
            }
            if (function.isValueNull(selectIpVersion))
            {
                sb.append("  AND type = '" + selectIpVersion + "'");
                countQuery.append("  AND type = '" + selectIpVersion + "'");
            } if (function.isValueNull(reason))
            {
                sb.append("  AND reason = '" + reason + "'");
                countQuery.append("  AND reason = '" + reason + "'");
            } if (function.isValueNull(remark))
            {
                sb.append("  AND remark = '" + remark + "'");
                countQuery.append("  AND remark = '" + remark + "'");
            }
            sb.append(" ORDER BY id DESC LIMIT 0,1 ");
            ps = conn.prepareStatement(sb.toString());
            rs = ps.executeQuery();

            while (rs.next())
            {
                blacklistVO = new BlacklistVO();
                blacklistVO.setMemberId(rs.getString("memberId"));
                blacklistVO.setStartIpv4(rs.getString("ipaddress"));
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.selectIpVersion(rs.getString("type"));
                blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));
                blacklistVO.setBlacklistReason(rs.getString("reason"));
                blacklistVO.setRemark(rs.getString("remark"));
                blacklistVO.setTimestamp(rs.getString("timestamp"));
                list.add(blacklistVO);
            }
            counter = 1;
            psCountOfCommonIpList = conn.prepareStatement(countQuery.toString());
            rsCommonIPList = psCountOfCommonIpList.executeQuery();
            if (rsCommonIPList.next())
            {
                paginationVO.setTotalRecords(rsCommonIPList.getInt(1));
            }

        }
        catch (SystemError se)
        {
            log.error("Internal Error", se);
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedipOnes()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            log.error("Internal Error", e);
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedipOnes()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {

            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return list;
    }
    public List<BlacklistVO> getBlockedips(String memberId, String AllIp, String selectIpVersion,String reason,String remark,PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        ResultSet rsCommonIPList = null;
        PreparedStatement ps = null;
        BlacklistVO blacklistVO = null;
        PreparedStatement psCountOfCommonIpList = null;
        int counter = 1;
        List<BlacklistVO> list = new ArrayList<BlacklistVO>();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        try
        {
            conn = Database.getRDBConnection();
            sb.append("SELECT memberId,ipaddress,id, actionExecutorId,actionExecutorName,type,reason,remark FROM blacklist_ip where id>0 ");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_ip where id>0");

            if (function.isValueNull(memberId))
            {
                sb.append("  AND memberId = '" + memberId + "'");
                countQuery.append(" AND memberId = '" + memberId + "'");
            }
            if (function.isValueNull(AllIp))
            {
                sb.append("  AND ipaddress = '" + AllIp + "'");
                countQuery.append("  AND ipaddress = '" + AllIp + "'");
            }
            if (function.isValueNull(selectIpVersion))
            {
                sb.append("  AND type = '" + selectIpVersion + "'");
                countQuery.append("  AND type = '" + selectIpVersion + "'");
            }if (function.isValueNull(reason))
            {
                sb.append("  AND reason = '" + reason + "'");
                countQuery.append("  AND reason = '" + reason + "'");
            }if (function.isValueNull(remark))
            {
                sb.append("  AND remark = '" + remark + "'");
                countQuery.append("  AND remark = '" + remark + "'");
            }
            sb.append(" ORDER BY id DESC LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            ps = conn.prepareStatement(sb.toString());
            rs = ps.executeQuery();

            while (rs.next())
            {
                blacklistVO = new BlacklistVO();
                blacklistVO.setMemberId(rs.getString("memberId"));
                blacklistVO.setStartIpv4(rs.getString("ipaddress"));
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.selectIpVersion(rs.getString("type"));
                blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));
                blacklistVO.setBlacklistReason(rs.getString("reason"));
                blacklistVO.setRemark(rs.getString("remark"));
                blacklistVO.setTimestamp(rs.getString("timestamp"));
                list.add(blacklistVO);
            }
            counter = 1;
            psCountOfCommonIpList = conn.prepareStatement(countQuery.toString());
            rsCommonIPList = psCountOfCommonIpList.executeQuery();
            if (rsCommonIPList.next())
            {
                paginationVO.setTotalRecords(rsCommonIPList.getInt(1));
            }

        }
        catch (SystemError se)
        {
            log.error("Internal Error", se);
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedip()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            log.error("Internal Error", e);
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedip()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {

            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return list;
    }

    public int insertBlockedBankAccNo(String bankAccountno, String reason, String actionExecutorId, String actionExecutorName) throws PZDBViolationException
    {
        System.out.println("inside insertBlockedBankAccNo--");
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        int count = 0;
        try
        {
            System.out.println("inside try insertBlockedBankAccNo--");

            conn = Database.getConnection();
            String insertQuery = "insert into blacklist_bankAccountno (bankAccountno,reason,actionExecutorId,actionExecutorName) values (?,?,?,?)";
            ps = conn.prepareStatement(insertQuery);
            ps.setString(1, bankAccountno);
            ps.setString(2, reason);
            ps.setString(3, actionExecutorId);
            ps.setString(4, actionExecutorName);
            log.error("insert blacklist_bankAccountno Query--"+ps);
            count = ps.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlockedCustomerId()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "insertBlockedCustomerId()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return count;
    }


    public List<BlacklistVO> getBlackListedBankAccNo(String bankAccountno,String reason, PaginationVO paginationVO) throws PZDBViolationException
    {
        System.out.println("inside getBlackListedBankAccNo-- ");
        Connection con = null;
        int totalreords = 0;
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer();
        PreparedStatement ps = null;
        List<BlacklistVO> listOfCustomerIds = new ArrayList<BlacklistVO>();
        Functions functions = new Functions();
        try
        {
            System.out.println("inside try insertBlockedBankAccNo--");

            con = Database.getRDBConnection();
            sb.append("select * from blacklist_bankAccountno where id>0");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_bankAccountno where  id>0");

            if (functions.isValueNull(bankAccountno))
            {
                sb.append(" and bankAccountno='" + bankAccountno+"'");
                countQuery.append(" and bankAccountno='" + bankAccountno+"'" );
            }
            if (functions.isValueNull(reason))
            {
                sb.append(" and reason='" + reason +"'");
                countQuery.append(" and reason='" + reason +"'");
            }
            sb.append(" order by TIMESTAMP desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            ps = con.prepareStatement(sb.toString());

            rs = ps.executeQuery();
            // log.error("select query - ");
            // log.error("select query :::::"+sb.);
            while (rs.next())
            {
                BlacklistVO blacklistVO = new BlacklistVO();
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.setBlacklistBankAccountNo(rs.getString("bankAccountno"));
                blacklistVO.setBlacklistReason(rs.getString("reason"));
                blacklistVO.setTimestamp(rs.getString("timestamp"));
                blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));

                listOfCustomerIds.add(blacklistVO);

            }
            ps = con.prepareStatement(countQuery.toString());
            rs = ps.executeQuery();
            if (rs.next())
            {
                totalreords = rs.getInt(1);
            }
            if (totalreords != 0)
            {
                paginationVO.setTotalRecords(totalreords);
            }

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlackListedCustomerIds", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlackListedCustomerIds", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
        return listOfCustomerIds;
    }

    public void unblockBankAccountNo(String Id) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        BlacklistVO blacklistVO = new BlacklistVO();
        try
        {
            conn = Database.getConnection();
            String dQuery = "delete from blacklist_bankAccountno where id=?";
            ps = conn.prepareStatement(dQuery);
            ps.setString(1, Id);
            log.debug("PS::::::::" + ps);
            ps.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "unblockCustomerId()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "unblockCustomerId()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
    }
    public List<BlacklistVO> getBlockEmail(String email,String reason,String remark,int records,int pageno,HttpServletRequest req) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        PreparedStatement ps = null;
        PreparedStatement pstmt = null;
        BlacklistVO blacklistVO = null;
        List<BlacklistVO> listOfVPAAdress = new ArrayList();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        int start = 0;
        int end = 0;

        start = (pageno - 1) * records;
        end = records;
        try
        {
            conn = Database.getConnection();
            sb.append("select * from blacklist_email where id>0");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_email where id>0");

            if (function.isValueNull(email))
            {
                sb.append(" and emailAddress='" + email + "'");
                countQuery.append(" and emailAddress='" + email + "'");
            }
            if (function.isValueNull(reason))
            {
                sb.append(" and reason='" + reason + "'");
                countQuery.append(" and reason='" + reason + "'");
            }if (function.isValueNull(remark))
        {
            sb.append(" and remark='" + remark + "'");
            countQuery.append(" and remark='" + remark + "'");
        }
            sb.append(" ORDER BY TIMESTAMP DESC ");
            sb.append(" limit  " + start + "," + end);
            ps = conn.prepareStatement(sb.toString());
            log.debug("inside country query:::::" + ps);

            rs = ps.executeQuery();
            while (rs.next())
            {
                blacklistVO = new BlacklistVO();
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.setEmailAddress(rs.getString("emailAddress"));
                blacklistVO.setBlacklistReason(rs.getString("reason"));
                blacklistVO.setTimestamp(rs.getString("timestamp"));
                blacklistVO.setRemark(rs.getString("remark"));
                blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));
                listOfVPAAdress.add(blacklistVO);
            }
            pstmt = conn.prepareStatement(countQuery.toString());
            log.debug("count query::::" + pstmt);
            rs1 = pstmt.executeQuery();
            int totalrecords = 0;
            if (rs1.next())
                totalrecords = rs1.getInt(1);

            req.setAttribute("totalrecords", totalrecords);
            req.setAttribute("records", "0");
            if (totalrecords > 0)
            {
                req.setAttribute("records", listOfVPAAdress.size());
            }
            log.debug("totalrecords::::" + totalrecords);
            log.debug("listOfCountry size::::" + listOfVPAAdress.size());
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return listOfVPAAdress;
    }
    public boolean unblockVpaAddressPartner(String id) throws PZDBViolationException
    {
        log.debug("inside deleteBlacklistvpaaddress:::");
        System.out.println("inside deleteblacklistvpaaddress");
        boolean isRecordAvailable = false;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        Functions functions = new Functions();
        int counter = 1;
        try
        {
            conn = Database.getRDBConnection();
            StringBuilder query = new StringBuilder("DELETE from blacklist_vpaAddress");

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

            log.debug("delete query:::::" + preparedStatement);
            int i = preparedStatement.executeUpdate();
            if (i > 0)
            {
                log.debug("inside if true");
                isRecordAvailable = true;
            }
        }
        catch (SQLException e)
        {
            log.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(BlacklistDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to blacklist_country table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            log.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(BlacklistDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to blacklist_country table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        log.debug("return:::::" + isRecordAvailable);
        return isRecordAvailable;
    }
    public List<BlacklistVO> getBlockVPAAddress(String vpaAddress,String reason,int records,int pageno,HttpServletRequest req) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        PreparedStatement ps = null;
        PreparedStatement pstmt = null;
        BlacklistVO blacklistVO = null;
        List<BlacklistVO> listOfVPAAdress = new ArrayList();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        int start = 0;
        int end = 0;


        start = (pageno - 1) * records;
        end = records;
        try
        {
            conn = Database.getConnection();
            sb.append("select * from blacklist_vpaAddress where id>0");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_vpaAddress where id>0");

            if (function.isValueNull(vpaAddress))
            {
                sb.append(" and vpaAddress='" + vpaAddress + "'");
                countQuery.append(" and vpaAddress='" + vpaAddress + "'");
            }
            if (function.isValueNull(reason))
            {
                sb.append(" and reason='" + reason + "'");
                countQuery.append(" and reason='" + reason + "'");
            }
            sb.append(" ORDER BY TIMESTAMP DESC ");
            sb.append(" limit  " + start + "," + end);
            ps = conn.prepareStatement(sb.toString());
            log.debug("inside country query:::::" + ps);

            rs = ps.executeQuery();
            while (rs.next())
            {
                blacklistVO = new BlacklistVO();
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.setVpaAddress(rs.getString("vpaAddress"));
                blacklistVO.setBlacklistReason(rs.getString("reason"));
                blacklistVO.setTimestamp(rs.getString("timestamp"));
                blacklistVO.setRemark(rs.getString("remark"));
                blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));
                listOfVPAAdress.add(blacklistVO);
            }
            pstmt = conn.prepareStatement(countQuery.toString());
            log.debug("count query::::" + pstmt);
            rs1 = pstmt.executeQuery();
            int totalrecords = 0;
            if (rs1.next())
                totalrecords = rs1.getInt(1);

            req.setAttribute("totalrecords", totalrecords);
            req.setAttribute("records", "0");
            if (totalrecords > 0)
            {
                req.setAttribute("records", listOfVPAAdress.size());
            }
            log.debug("totalrecords::::" + totalrecords);
            log.debug("listOfCountry size::::" + listOfVPAAdress.size());
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return listOfVPAAdress;
    }
    public boolean deleteBlacklistEmail(String id) throws PZDBViolationException
    {
        log.debug("inside deleteBlacklistEmail:::");
        boolean isRecordAvailable = false;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        Functions functions = new Functions();
        int counter = 1;
        try
        {
            conn = Database.getRDBConnection();
            StringBuilder query = new StringBuilder("DELETE from blacklist_email");

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

            log.debug("delete query:::::" + preparedStatement);
            int i = preparedStatement.executeUpdate();
            if (i > 0)
            {
                log.debug("inside if true");
                isRecordAvailable = true;
            }
        }
        catch (SQLException e)
        {
            log.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(BlacklistDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to blacklist_country table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            log.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(BlacklistDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to blacklist_country table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        log.debug("return:::::" + isRecordAvailable);
        return isRecordAvailable;
    }
    public List<BlacklistVO> getBlockAccountNumber(String bankaccountno,String reason,int records,int pageno,HttpServletRequest req) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        PreparedStatement ps = null;
        PreparedStatement pstmt = null;
        BlacklistVO blacklistVO = null;
        List<BlacklistVO> listOfVPAAdress = new ArrayList();
        StringBuffer sb = new StringBuffer();
        Functions function = new Functions();
        int start = 0;
        int end = 0;

        start = (pageno - 1) * records;
        end = records;
        try
        {
            conn = Database.getConnection();
            sb.append("select * from blacklist_bankAccountno where id>0");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_bankAccountno where id>0");

            if (function.isValueNull(bankaccountno))
            {
                sb.append(" and bankAccountno='" + bankaccountno + "'");
                countQuery.append(" and bankAccountno='" + bankaccountno + "'");
            }
            if (function.isValueNull(reason))
            {
                sb.append(" and reason='" + reason + "'");
                countQuery.append(" and reason='" + reason + "'");
            }
            sb.append(" ORDER BY TIMESTAMP DESC ");
            sb.append(" limit  " + start + "," + end);
            ps = conn.prepareStatement(sb.toString());
            log.debug("inside country query:::::" + ps);

            rs = ps.executeQuery();
            while (rs.next())
            {
                blacklistVO = new BlacklistVO();
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.setBlacklistBankAccountNo(rs.getString("bankAccountno"));
                blacklistVO.setBlacklistReason(rs.getString("reason"));
                blacklistVO.setTimestamp(rs.getString("timestamp"));
                blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));
                listOfVPAAdress.add(blacklistVO);
            }
            pstmt = conn.prepareStatement(countQuery.toString());
            log.debug("count query::::" + pstmt);
            rs1 = pstmt.executeQuery();
            int totalrecords = 0;
            if (rs1.next())
                totalrecords = rs1.getInt(1);

            req.setAttribute("totalrecords", totalrecords);
            req.setAttribute("records", "0");
            if (totalrecords > 0)
            {
                req.setAttribute("records", listOfVPAAdress.size());
            }
            log.debug("totalrecords::::" + totalrecords);
            log.debug("listOfCountry size::::" + listOfVPAAdress.size());
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlockedCountry()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return listOfVPAAdress;
    }
    public boolean deleteBankAcccountNumber(String id) throws PZDBViolationException
    {
        log.debug("inside deleteBankAcccountNumber:::");
        boolean isRecordAvailable = false;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        Functions functions = new Functions();
        int counter = 1;
        try
        {
            conn = Database.getRDBConnection();
            StringBuilder query = new StringBuilder("DELETE from blacklist_bankAccountno");

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

            log.debug("delete query:::::" + preparedStatement);
            int i = preparedStatement.executeUpdate();
            if (i > 0)
            {
                log.debug("inside if true");
                isRecordAvailable = true;
            }
        }
        catch (SQLException e)
        {
            log.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(BlacklistDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to blacklist_bankAccountno table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            log.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(BlacklistDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to blacklist_bankAccountno table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        log.debug("return:::::" + isRecordAvailable);
        return isRecordAvailable;
    }
    public boolean deleteBlacklistPhone(String id) throws PZDBViolationException
    {
        log.debug("inside deleteBlacklistPhone:::");
        boolean isRecordAvailable = false;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        Functions functions = new Functions();
        int counter = 1;
        try
        {
            conn = Database.getRDBConnection();
            StringBuilder query = new StringBuilder("DELETE from blacklist_phone");

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

            log.debug("delete query:::::" + preparedStatement);
            int i = preparedStatement.executeUpdate();
            if (i > 0)
            {
                log.debug("inside if true");
                isRecordAvailable = true;
            }
        }
        catch (SQLException e)
        {
            log.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(BlacklistDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to blacklist_country table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            log.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(BlacklistDAO.class.getName(), "isRecordAvailableForMember()", null, "Common", "Sql exception while connecting to blacklist_country table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        log.debug("return:::::" + isRecordAvailable);
        return isRecordAvailable;
    }
    public List<BlacklistVO> getBlacklistedvpaaddress(String vpaAddress,String reason, PaginationVO paginationVO) throws PZDBViolationException
    {
        System.out.println("methodgetBlacklistedvpaaddress");
        Connection con = null;
        int totalreords = 0;
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer();
        PreparedStatement ps = null;
        List<BlacklistVO> listOfphones = new ArrayList<BlacklistVO>();
        Functions functions = new Functions();
        try
        {
            con = Database.getRDBConnection();
            sb.append("select * from blacklist_vpaAddress where id>0");
            StringBuffer countQuery = new StringBuffer("select count(*) from blacklist_vpaAddress where  id>0");

            if (functions.isValueNull(vpaAddress))
            {
                sb.append(" and vpaAddress='" + vpaAddress+"'");
                countQuery.append(" and vpaAddress='" + vpaAddress+"'" );
            }
            if (functions.isValueNull(reason))
            {
                sb.append(" and reason='" + reason +"'");
                countQuery.append(" and reason='" + reason +"'");
            }
            System.out.println("pagestart"+paginationVO.getStart());
            System.out.println("pagestart"+paginationVO.getEnd());
            sb.append(" order by TIMESTAMP desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());

            ps = con.prepareStatement(sb.toString());

            rs = ps.executeQuery();
            // log.error("select query - ");
            // log.error("select query :::::"+sb.);
            while (rs.next())
            {
                BlacklistVO blacklistVO = new BlacklistVO();
                blacklistVO.setId(rs.getString("id"));
                blacklistVO.setVpaAddress(rs.getString("vpaAddress"));
                blacklistVO.setBlacklistReason(rs.getString("reason"));
                blacklistVO.setTimestamp(rs.getString("timestamp"));
                blacklistVO.setRemark(rs.getString("remark"));
                blacklistVO.setActionExecutorId(rs.getString("actionExecutorId"));
                blacklistVO.setActionExecutorName(rs.getString("actionExecutorName"));

                listOfphones.add(blacklistVO);

            }
            ps = con.prepareStatement(countQuery.toString());
            rs = ps.executeQuery();
            if (rs.next())
            {
                totalreords = rs.getInt(1);
            }
            if (totalreords != 0)
            {
                paginationVO.setTotalRecords(totalreords);
            }

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlackListedPhoneNo", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BlacklistDAO.java", "getBlackListedPhoneNo", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
        return listOfphones;
    }

}