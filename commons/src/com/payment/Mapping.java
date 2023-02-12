package com.payment;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccountService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 6/19/14
 * Time: 12:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class Mapping
{
    static Logger logger = new Logger(Mapping.class.getName());

    public Hashtable getPaymentTypeForMembers(String toid) throws SystemError
    {
        Connection con =null;
        String paymodeid = "";
        Hashtable paymodeHash = new Hashtable();
        try
        {
            con = Database.getConnection();
            String query = "select distinct paymodeid from member_account_mapping where memberid=?";
            PreparedStatement p = con.prepareStatement(query);
            p.setString(1, toid);
            ResultSet rs = p.executeQuery();
            logger.debug("Query : " + query);
            while(rs.next())
            {
                paymodeid = rs.getString("paymodeid");
                paymodeHash.put(paymodeid, GatewayAccountService.getPaymentTypes(paymodeid));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException In Mapping", e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return paymodeHash;
    }

    public Hashtable getCardTypeForMembers(String toid) throws SystemError
    {
        Connection con =null;
        String cardid = "";
        Hashtable cardHash = new Hashtable();
        try
        {
            con = Database.getConnection();
            String query = "select distinct cardtypeid from member_account_mapping where memberid=?";
            PreparedStatement p = con.prepareStatement(query);
            p.setString(1, toid);
            ResultSet rs = p.executeQuery();
            logger.debug("Query : " + query);
            while(rs.next())
            {
                cardid = rs.getString("cardtypeid");
                cardHash.put(cardid, GatewayAccountService.getCardType(cardid));
            }
        }
        catch (SQLException s)
        {
            logger.error("Sqlerror", s);
            throw new SystemError("Error : " + s.getMessage());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return cardHash;
    }

    public Hashtable getTerminalforMember(String toid) throws SystemError
    {
        Connection conn = null;
        String terminal = "";
        String paymodeid = "";
        String cardtypeid = "";
        String terminalList = "";
        Hashtable terminalHash = new Hashtable();

        try
        {
            conn = Database.getConnection();
            String query = "select distinct terminalid,paymodeid,cardtypeid from member_account_mapping where memberid=?";
            PreparedStatement ps =conn.prepareStatement(query);
            ps.setString(1, toid);
            ResultSet rs = ps.executeQuery();
            logger.debug("Query : " + query);
            while(rs.next())
            {
                terminal = rs.getString("terminalid");
                paymodeid = rs.getString("paymodeid");
                cardtypeid = rs.getString("cardtypeid");
                terminalList = terminal+"--"+GatewayAccountService.getPaymentTypes(paymodeid)+"--"+GatewayAccountService.getCardType(cardtypeid);

                terminalHash.put(terminal,terminalList);
            }

        }
        catch (SQLException e)
        {
            logger.error("SQLException In Mapping  ::::",e);  //To change body of catch statement use File | Settings | File Templates.
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return terminalHash;

    }

    public Hashtable getKeyAlgoAutoSelectTerminal(String toid) throws SystemError
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs1=null;
        Hashtable dataHash = new Hashtable();

        try
        {
            //conn = Database.getConnection();
            conn=Database.getRDBConnection();
            String query1= "select clkey,checksumalgo,autoSelectTerminal from members where memberid=?";
            pstmt = conn.prepareStatement(query1);
            pstmt.setString(1,toid);
            rs1 = pstmt.executeQuery();

            if (rs1.next())
            {
                dataHash.put("key",rs1.getString("clkey"));
                dataHash.put("checksumAlgorithm",rs1.getString("checksumalgo"));
                dataHash.put("autoSelectTerminal",rs1.getString("autoSelectTerminal"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException ----",e);
        }
        finally
        {
            Database.closeResultSet(rs1);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

        return dataHash;
    }


    }
