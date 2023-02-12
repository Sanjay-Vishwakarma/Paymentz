package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.vo.AgentCommissionVO;
import com.manager.vo.AgentDetailsVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.payoutVOs.AgentWireVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 12/10/14
 * Time: 6:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class AgentDAO
{
    private static Logger logger = new Logger(AgentDAO.class.getName());
    Functions functions = new Functions();
    public static Hashtable getAgentWireList(String toid,String terminalid,String agentId, String is_paid, String fdtstamp, String tdtstamp, int pageno, int records)
    {
        Functions functions = new Functions();
        Hashtable hash = new Hashtable();
        Connection connection = null;
        ResultSet rs=null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;
        try
        {
            connection = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT settledid,settlementstartdate,settlementenddate,agentchargeamount,agentunpaidamount,agenttotalfundedamount,currency,status,settlementreportfilename,markedfordeletion,agentid,TIMESTAMP,toid,terminalid,accountid,paymodeid,cardtypeid,declinedcoverdateupto,reversedcoverdateupto,chargebackcoverdateupto,FROM_UNIXTIME(wirecreationtime) AS 'wirecreationtime'  FROM agent_wiremanager WHERE markedfordeletion='N'");
            StringBuffer countquery = new StringBuffer("SELECT count(*) FROM agent_wiremanager WHERE markedfordeletion='N'");
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and wirecreationtime >= " + ESAPI.encoder().encodeForSQL(me, fdtstamp));
                countquery.append(" and wirecreationtime >= " + ESAPI.encoder().encodeForSQL(me, fdtstamp));
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and wirecreationtime <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
                countquery.append(" and wirecreationtime <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
            }
            if (functions.isValueNull(agentId))
            {
                query.append(" and agentid='" + ESAPI.encoder().encodeForSQL(me, agentId) + "'");
                countquery.append(" and agentid='" + ESAPI.encoder().encodeForSQL(me, agentId) + "'");
            }
            if (functions.isValueNull(toid))
            {
                query.append(" and toid='" + ESAPI.encoder().encodeForSQL(me, toid) + "'");
                countquery.append(" and toid='" + ESAPI.encoder().encodeForSQL(me, toid) + "'");
            }
            if (functions.isValueNull(terminalid))
            {
                query.append(" and terminalid='" + ESAPI.encoder().encodeForSQL(me, terminalid) + "'");
                countquery.append(" and terminalid='" + ESAPI.encoder().encodeForSQL(me, terminalid) + "'");
            }
            if (functions.isValueNull(is_paid))
            {
                if (is_paid.equalsIgnoreCase("Y"))
                {
                    query.append(" and status='paid'");
                    countquery.append(" and status='paid'");
                }
                else
                {
                    query.append(" and status='unpaid'");
                    countquery.append(" and status='unpaid'");
                }
            }
            query.append(" order by toid desc LIMIT " + start + "," + end);
            logger.debug("Query:-" + query);
            logger.debug("CountQuery:-" + countquery);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), connection));
            rs = Database.executeQuery(countquery.toString(), connection);
            int totalrecords = 0;
            if (rs.next())
            {
                totalrecords = rs.getInt(1);
            }
            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::" + systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::" + e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(connection);
        }
        return hash;
    }

    public AgentDetailsVO getAgentDetails(String agentId) throws SystemError, Exception
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String query=null;
        AgentDetailsVO agentDetailsVO=null;
        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT agentid,agentname,contact_persons FROM agents WHERE agentid=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,agentId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                agentDetailsVO=new AgentDetailsVO();
                agentDetailsVO.setAgentId(rs.getString("agentid"));
                agentDetailsVO.setAgentName(rs.getString("agentName"));
                agentDetailsVO.setContactPerson(rs.getString("contact_persons"));
            }
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error :::: ",e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        logger.debug("Leaving getMemberDetails");
        return agentDetailsVO;
    }

    public int getAgentPartner(String username) throws SystemError, Exception
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String query=null;
        int partner= 0;
        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT partnerId FROM agents WHERE login=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,username);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
               partner=rs.getInt("partnerId");
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException :::: ",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return partner;
    }
    //new
    public List <AgentDetailsVO> getAgentDetails () throws SystemError, Exception
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String query=null;
        List<AgentDetailsVO> agentDetailsVOList=new ArrayList();
        AgentDetailsVO agentDetailsVO=null;
        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT agentid,agentName FROM agents WHERE activation='T' ORDER BY agentid ASC";
            pstmt= conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                agentDetailsVO=new AgentDetailsVO();
                agentDetailsVO.setAgentId(rs.getString("agentid"));
                agentDetailsVO.setAgentName(rs.getString("agentName"));
                agentDetailsVOList.add(agentDetailsVO);
            }
        }
        catch (SystemError e)
        {
            logger.error("SystemError::::: ",e);
            throw new SystemError("Error : " + e.getMessage());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::: ",e);
            throw new SQLException("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return agentDetailsVOList;
    }

    public List<AgentDetailsVO> getMerchantAgents(String memberId) throws Exception
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String query=null;
        List<AgentDetailsVO> agentDetailsVOList=new ArrayList<AgentDetailsVO>();
        AgentDetailsVO agentDetailsVO=null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            query = "SELECT mam.mappingid,mam.memberid,a.agentid,a.agentname,a.contact_persons FROM  merchant_agent_mapping as mam join agents as a on mam.agentid=a.agentid and mam.memberid=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,memberId);
            rs = pstmt.executeQuery();
            while(rs.next())
            {
                agentDetailsVO=new AgentDetailsVO();
                agentDetailsVO=new AgentDetailsVO();
                agentDetailsVO.setAgentId(rs.getString("agentid"));
                agentDetailsVO.setAgentName(rs.getString("agentName"));
                agentDetailsVO.setContactPerson(rs.getString("contact_persons"));
                agentDetailsVOList.add(agentDetailsVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException while getting merchant agent mapping:::: ",e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return agentDetailsVOList;
    }

    public boolean setMarkForDelete(String wireId)throws SystemError,SQLException
    {
        boolean flag=false;
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        try
        {
            connection= Database.getConnection();
            String qry="UPDATE agent_wiremanager SET markedfordeletion='Y' WHERE settledid=?";
            preparedStatement=connection.prepareStatement(qry);
            preparedStatement.setString(1,wireId);
            int i=preparedStatement.executeUpdate();
            if(i==1)
            {
                flag=true;
            }
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return flag;
    }
    public boolean setDeleteAgentWire(String wireId)throws SystemError,SQLException
    {
        boolean flag=false;
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        try
        {
            connection= Database.getConnection();
            String qry="DELETE FROM agent_wiremanager WHERE settledid=?";
            preparedStatement=connection.prepareStatement(qry);
            preparedStatement.setString(1,wireId);
            int i=preparedStatement.executeUpdate();
            if(i==1)
            {
                flag=true;
            }
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return flag;
    }

    public AgentWireVO getSingleAgentWire(String wireId)throws SystemError,SQLException
    {
        Connection connection=null;
        AgentWireVO agentWireVO=null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try
        {
            connection= Database.getRDBConnection();
            String qry="SELECT settledid,settledate,settlementstartdate,settlementenddate,agentchargeamount,agentunpaidamount,agenttotalfundedamount,currency,STATUS,settlementreportfilename,markedfordeletion,agentid,TIMESTAMP,toid,terminalid,accountid,paymodeid,cardtypeid,declinedcoverdateupto,reversedcoverdateupto,chargebackcoverdateupto,FROM_UNIXTIME(wirecreationtime) AS 'wirecreationdate' FROM agent_wiremanager WHERE settledid=?";
            preparedStatement=connection.prepareStatement(qry);
            preparedStatement.setString(1,wireId);
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                agentWireVO=new AgentWireVO();
                agentWireVO.setSettledId(rs.getString("settledid"));
                agentWireVO.setSettleDate(rs.getString("settledate"));
                agentWireVO.setSettlementStartDate(rs.getString("settlementstartdate"));
                agentWireVO.setSettlementEndDate(rs.getString("settlementenddate"));
                agentWireVO.setAgentChargeAmount(rs.getDouble("agentchargeamount"));
                agentWireVO.setAgentUnpaidAmount(rs.getDouble("agentunpaidamount"));
                agentWireVO.setAgentTotalFundedAmount(rs.getDouble("agenttotalfundedamount"));
                agentWireVO.setCurrency(rs.getString("currency"));
                agentWireVO.setStatus(rs.getString("status"));
                agentWireVO.setSettlementReportFileName(rs.getString("settlementreportfilename"));
                agentWireVO.setMarkedForDeletion(rs.getString("markedfordeletion"));
                agentWireVO.setAgentId(rs.getString("agentid"));
                agentWireVO.setTimestamp(rs.getString("timestamp"));
                agentWireVO.setMemberId(rs.getString("toid"));
                agentWireVO.setTerminalId(rs.getString("terminalid"));
                agentWireVO.setAccountId(rs.getString("accountid"));
                agentWireVO.setPayModeId(rs.getString("paymodeid"));
                agentWireVO.setCardTypeId(rs.getString("cardtypeid"));
                agentWireVO.setDeclinedCoverDateUpTo(rs.getString("declinedcoverdateupto"));
                agentWireVO.setReversedCoverDateUpTo(rs.getString("reversedcoverdateupto"));
                agentWireVO.setChargebackCoverDateUpTo(rs.getString("chargebackcoverdateupto"));
                agentWireVO.setWireCreationDate(rs.getString("wirecreationdate"));
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return agentWireVO;
    }

    public String getAgentWireReportFileName(String wireId)throws SystemError,SQLException
    {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        String settlementReportFileName=null;
        try
        {
            connection= Database.getRDBConnection();
            String qry="SELECT settlementreportfilename FROM agent_wiremanager WHERE settledid=?";
            preparedStatement=connection.prepareStatement(qry);
            preparedStatement.setString(1,wireId);
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                settlementReportFileName=rs.getString("settlementreportfilename");
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return settlementReportFileName;
    }

    public boolean isMemberMappedWithAgent(String memberId,String agentId)throws SQLException,SystemError
    {
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        boolean b=false;
        try
        {
            con=Database.getRDBConnection();
            StringBuffer sb=new StringBuffer("select memberid from merchant_agent_mapping where agentid=? and memberid=?");
            pstmt=con.prepareStatement(sb.toString());
            pstmt.setString(1,agentId);
            pstmt.setString(2,memberId);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                b=true;
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return b;
    }

    public String getAgentMemberList(String agentid)
    {
        ResultSet rs=null;
        PreparedStatement pstmt=null;
        Connection con=null;
        StringBuffer sMemberList = new StringBuffer();
        String memberList="";
        try
        {
            con=Database.getRDBConnection();
            StringBuffer qry= new StringBuffer("select memberid from merchant_agent_mapping where agentId=?");
            pstmt= con.prepareStatement(qry.toString());


            pstmt.setString(1,agentid);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                sMemberList.append(rs.getString("memberid")+",");
            }
            if(sMemberList.length() > 0)
            {
                memberList = sMemberList.toString().substring(0, sMemberList.length() - 1);
            }
        }
        catch(Exception e)
        {
            logger.error("error",e);
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return memberList;
    }

    public List<MerchantDetailsVO> getAgentMemberListFromMapping()
    {
        ResultSet rs=null;
        Connection con=null;
        PreparedStatement pstmt=null;
        MerchantDetailsVO merchantDetailsVO=null;
        List<MerchantDetailsVO> merchantDetailsVOList=new LinkedList();
        try
        {
            con=Database.getRDBConnection();
            StringBuffer qry= new StringBuffer("SELECT mam.memberid,mam.agentid,m.company_name FROM merchant_agent_mapping AS mam JOIN members AS m ON mam.`memberid`=m.`memberid` ORDER BY m.memberid ASC");
            pstmt= con.prepareStatement(qry.toString());
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                merchantDetailsVO=new MerchantDetailsVO();
                merchantDetailsVO.setMemberId(rs.getString("memberid"));
                merchantDetailsVO.setAgentId(rs.getString("agentid"));
                merchantDetailsVO.setCompany_name(rs.getString("company_name"));
                merchantDetailsVOList.add(merchantDetailsVO);
            }

        }
        catch(Exception e)
        {
            logger.error("Exception::::",e);
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return merchantDetailsVOList;
    }


    public List<MerchantDetailsVO> getAgentMemberListFromMapped(String agentid)
    {
        ResultSet rs=null;
        Connection con=null;
        PreparedStatement pstmt=null;
        MerchantDetailsVO merchantDetailsVO=null;
        List<MerchantDetailsVO> merchantDetailsVOList=new LinkedList();
        try
        {
            con=Database.getRDBConnection();
            StringBuffer qry= new StringBuffer("SELECT mam.memberid,mam.agentid,m.company_name FROM merchant_agent_mapping AS mam JOIN members AS m ON mam.`memberid`=m.`memberid`");
            if (functions.isValueNull(agentid))
            {
                qry.append(" where mam.agentid = "+agentid);
            }
            qry.append(" ORDER BY m.memberid ASC");
            pstmt= con.prepareStatement(qry.toString());
            logger.debug("agentid query::::::"+pstmt);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                merchantDetailsVO=new MerchantDetailsVO();
                merchantDetailsVO.setMemberId(rs.getString("memberid"));
                merchantDetailsVO.setAgentId(rs.getString("agentid"));
                merchantDetailsVO.setCompany_name(rs.getString("company_name"));
                merchantDetailsVOList.add(merchantDetailsVO);
            }
        }
        catch(Exception e)
        {
            logger.error("Exception::::",e);
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return merchantDetailsVOList;
    }

    public List<MerchantDetailsVO> getAgentMemberAccountListFromMapped(String memberId,String agentId)
    {
        ResultSet rs=null;
        Connection con=null;
        PreparedStatement pstmt=null;
        MerchantDetailsVO merchantDetailsVO=null;
        List<MerchantDetailsVO> merchantDetailsVOList=new LinkedList();
        try
        {
            con=Database.getRDBConnection();
            StringBuffer qry= new StringBuffer("SELECT DISTINCT g.accountid,g.merchantid,m.`memberid` FROM gateway_accounts AS g JOIN member_account_mapping AS m JOIN merchant_agent_mapping AS mg WHERE m.memberid=mg.memberid  AND m.`accountid`=g.`accountid`");
            if (functions.isValueNull(memberId))
            {
                qry.append(" and m.memberid = "+memberId);
            }
            if (functions.isValueNull(agentId))
            {
                qry.append(" and mg.agentid = "+agentId);
            }
            qry.append(" ORDER BY m.accountid ASC");
            pstmt= con.prepareStatement(qry.toString());
            logger.debug("agentid query::::::"+pstmt);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                merchantDetailsVO=new MerchantDetailsVO();
                merchantDetailsVO.setAccountId(rs.getString("accountid"));
                merchantDetailsVO.setMemberId(rs.getString("merchantid"));
                merchantDetailsVOList.add(merchantDetailsVO);
            }
        }
        catch(Exception e)
        {
            logger.error("Exception::::",e);
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return merchantDetailsVOList;
    }

    //new
    public void updateAgentWire(AgentWireVO agentWireVO)
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        try
        {
            conn = Database.getConnection();
            String query1 = "update agent_wiremanager set settledate=? ,agentchargeamount=? ,agentunpaidamount=? ,agenttotalfundedamount=?,status=? where settledid=?" ;
            pstmt = conn.prepareStatement(query1);
            pstmt.setString(1, agentWireVO.getSettleDate());
            pstmt.setDouble(2, agentWireVO.getAgentChargeAmount());
            pstmt.setDouble(3, agentWireVO.getAgentUnpaidAmount());
            pstmt.setDouble(4, agentWireVO.getAgentTotalFundedAmount());
            pstmt.setString(5, agentWireVO.getStatus());
            pstmt.setString(6, agentWireVO.getSettledId());
            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                agentWireVO.setUpdated(true);
            }
            else
            {
                agentWireVO.setUpdated(false);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError::::" + systemError.getMessage());
        }
        catch (SQLException se)
        {
            logger.error("SQLException:::::" + se.getMessage());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

    }

    public boolean isChargeAppliedOnAgent(AgentCommissionVO agentCommissionVO) throws Exception
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        boolean checkAvailability = true;
        int k = 0;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT * FROM agent_commission WHERE memberid=? and agentid=? and terminalid=? and chargeid=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, agentCommissionVO.getMemberId());
            pstmt.setString(2, agentCommissionVO.getAgentId());
            pstmt.setString(3, agentCommissionVO.getTerminalId());
            pstmt.setString(4, agentCommissionVO.getChargeId());
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                checkAvailability = false;
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return checkAvailability;
    }

    public boolean isCheckMemberMappedWithAgent(String memberId,String agentId)throws SQLException,SystemError
    {
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        boolean b=true;
        try
        {
            con=Database.getRDBConnection();
            StringBuffer sb=new StringBuffer("select memberid from merchant_agent_mapping where agentid=? and memberid=?");
            pstmt=con.prepareStatement(sb.toString());
            pstmt.setString(1,agentId);
            pstmt.setString(2,memberId);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                b=false;
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return b;
    }

}
