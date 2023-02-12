package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.ChargeManager;
import com.manager.WiresManager;
import com.manager.enums.Charge_category;
import com.manager.enums.Charge_keyword;
import com.manager.enums.Charge_subKeyword;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.AgentCommissionVO;
import com.manager.vo.ChargeVO;
import com.manager.vo.PartnerCommissionVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.payoutVOs.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 1/9/15
 * Time: 9:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChargesDAO
{
    Logger logger = new Logger(ChargesDAO.class.getName());
    public String addNewBusinessCharge(ChargeMasterVO chargeMasterVO) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        String status = "failed";
        try
        {
            con = Database.getConnection();
            StringBuffer buffer = new StringBuffer("select chargename from charge_master where chargename=?");
            preparedStatement = con.prepareStatement(buffer.toString());
            preparedStatement.setString(1, chargeMasterVO.getChargeName());
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                logger.error("Charge is already added in charge Master ");
                status = "failed";
            }
            else
            {
                StringBuffer stringBuffer = new StringBuffer("insert into charge_master(chargename,isinputrequired,keyname,valuetype," +
                        "category,keyword,subkeyword,frequency,sequencenum,actionExecutorId,actionExecutorName) values(?,?,?,?,?,?,?,?,?,?,?)");
                preparedStatement = con.prepareStatement(stringBuffer.toString());
                preparedStatement.setString(1, chargeMasterVO.getChargeName());
                preparedStatement.setString(2, chargeMasterVO.getInInputRequired());
                preparedStatement.setString(3, chargeMasterVO.getKeyName());
                preparedStatement.setString(4, chargeMasterVO.getValueType());
                preparedStatement.setString(5, chargeMasterVO.getCategory());
                preparedStatement.setString(6, chargeMasterVO.getKeyword());
                preparedStatement.setString(7, chargeMasterVO.getSubKeyword());
                preparedStatement.setString(8, chargeMasterVO.getFrequency());
                preparedStatement.setString(9, chargeMasterVO.getSequenceNumber());
                preparedStatement.setString(10,chargeMasterVO.getActionExecutorId());
                preparedStatement.setString(11,chargeMasterVO.getActionExecutorName());
                int k = preparedStatement.executeUpdate();
                if (k > 0)
                {
                    status = "success";
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting charges list", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayAccountCharges", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting charges list::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayAccountCharges()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return status;
    }

    public String addMerchantRandomCharge(MerchantRandomChargesVO merchantRandomChargesVO) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        String status = "";
        StringBuffer sb = null;
        try
        {
            con = Database.getConnection();
            sb = new StringBuffer("insert into merchant_random_charges(merchantrdmchargeid,bankwireid,memberid,terminalid,chargename,chargerate,valuetype,chargecounter,chargeamount,chargevalue,chargeremark,chargetype,actionExecutorId,actionExecutorName) values(null,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            preparedStatement = con.prepareStatement(sb.toString());
            preparedStatement.setString(1, merchantRandomChargesVO.getBankWireId());
            preparedStatement.setString(2, merchantRandomChargesVO.getMemberId());
            preparedStatement.setString(3, merchantRandomChargesVO.getTerminalId());
            preparedStatement.setString(4, merchantRandomChargesVO.getChargeName());
            preparedStatement.setDouble(5, merchantRandomChargesVO.getChargeRate());
            preparedStatement.setString(6, merchantRandomChargesVO.getChargeValueType());
            preparedStatement.setInt(7, merchantRandomChargesVO.getChargeCounter());
            preparedStatement.setDouble(8, merchantRandomChargesVO.getChargeAmount());
            preparedStatement.setDouble(9, merchantRandomChargesVO.getChargeValue());
            preparedStatement.setString(10, merchantRandomChargesVO.getChargeRemark());
            preparedStatement.setString(11, merchantRandomChargesVO.getChargeType());
            preparedStatement.setString(12,merchantRandomChargesVO.getActionExecutorId());
            preparedStatement.setString(13,merchantRandomChargesVO.getActionExecutorName());
            int k = preparedStatement.executeUpdate();
            if (k > 0)
            {
                status = "success";
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting charges list", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "addmerchantRandomCharge", null, "Common", "Sql exception while connecting to merchant_random_charges table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting charges list::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "addmerchantRandomCharge()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }

        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return status;
    }

    public boolean updateBusinessCharge(ChargeMasterVO chargeMasterVO) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        int k = 0;
        boolean status = false;
        try
        {
            con = Database.getConnection();
            StringBuffer query = new StringBuffer("update charge_master set chargename=?,isinputrequired=?,keyname=?,valuetype=?" +
                    ",category=?,keyword=?,subkeyword=?,frequency=?,sequencenum=?  where chargeid= ?");
            pstmt = con.prepareStatement(query.toString());
            pstmt.setString(1, chargeMasterVO.getChargeName());
            pstmt.setString(2, chargeMasterVO.getInInputRequired());
            pstmt.setString(3, chargeMasterVO.getKeyName());
            pstmt.setString(4, chargeMasterVO.getValueType());
            pstmt.setString(5, chargeMasterVO.getCategory());
            pstmt.setString(6, chargeMasterVO.getKeyword());
            pstmt.setString(7, chargeMasterVO.getSubKeyword());
            pstmt.setString(8, chargeMasterVO.getFrequency());
            pstmt.setString(9, chargeMasterVO.getSequenceNumber());
            pstmt.setString(10, chargeMasterVO.getChargeId());
            k = pstmt.executeUpdate();
            if (k > 0)
            {

                StringBuffer query2 = new StringBuffer("update member_accounts_charges_mapping set category=?,keyword=?,subkeyword =?,frequency =?,valuetype =? where chargeid=?");
                pstmt = con.prepareStatement(query2.toString());
                pstmt.setString(1, chargeMasterVO.getCategory());
                pstmt.setString(2, chargeMasterVO.getKeyword());
                pstmt.setString(3, chargeMasterVO.getSubKeyword());
                pstmt.setString(4, chargeMasterVO.getFrequency());
                pstmt.setString(5, chargeMasterVO.getValueType());
                pstmt.setString(6, chargeMasterVO.getChargeId());
                pstmt.executeUpdate();
                status = true;
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while updateBusinessCharge charge ", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayAccountCharges", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while while updateBusinessCharge charge::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayAccountCharges()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return status;
    }


    public String getMerchantChargeVersionRate(String memberAccountChargeMappingId, String reportingDate)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        String chargeVersionRate = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT merchantChargeValue FROM ChargeVersionMemberMaster WHERE member_accounts_charges_mapping_id=? AND (?>=effectiveStartDate) AND (?<=effectiveEndDate)";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, memberAccountChargeMappingId);
            preparedStatement.setString(2, reportingDate);
            preparedStatement.setString(3, reportingDate);
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                chargeVersionRate = rs.getString("merchantChargeValue");
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError ::::::: Leaving transactionEntry ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return chargeVersionRate;
    }

    public String getAgentChargeVersionRate(String memberAccountChargeMappingId, String reportingDate)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        String chargeVersionRate = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT agentCommision FROM ChargeVersionMemberMaster WHERE member_accounts_charges_mapping_id=? AND (?>=effectiveStartDate) AND (?<=effectiveEndDate)";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, memberAccountChargeMappingId);
            preparedStatement.setString(2, reportingDate);
            preparedStatement.setString(3, reportingDate);
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                chargeVersionRate = rs.getString("agentCommision");
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError ::::::: Leaving transactionEntry ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return chargeVersionRate;
    }

    public String getBankAgentChargeVersionRate(String gatewayAccountChargeMappingId, String reportingDate)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        String chargeVersionRate = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT agentCommision FROM ChargeVersionGatewayMaster WHERE gateway_accounts_charges_mapping_id=? AND (?>=effectiveStartDate) AND (?<=effectiveEndDate)";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, gatewayAccountChargeMappingId);
            preparedStatement.setString(2, reportingDate);
            preparedStatement.setString(3, reportingDate);
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                Database.closeResultSet(rs);
                Database.closePreparedStatement(preparedStatement);
                chargeVersionRate = rs.getString("agentCommision");
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError ::::::: Leaving transactionEntry ", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return chargeVersionRate;
    }

    public String getBankPartnerChargeVersionRate(String gatewayAccountChargeMappingId, String reportingDate)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        String chargeVersionRate = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT partnerCommision FROM ChargeVersionGatewayMaster WHERE gateway_accounts_charges_mapping_id=? AND (?>=effectiveStartDate) AND (?<=effectiveEndDate)";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, gatewayAccountChargeMappingId);
            preparedStatement.setString(2, reportingDate);
            preparedStatement.setString(3, reportingDate);
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                chargeVersionRate = rs.getString("partnerCommision");
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving  ChargesDAO", se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError ::::::: Leaving  ChargesDAO", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return chargeVersionRate;
    }

    public String getPartnerChargeVersionRate(String memberAccountChargeMappingId, String reportingDate)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        String chargeVersionRate = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT partnerCommision FROM ChargeVersionMemberMaster WHERE member_accounts_charges_mapping_id=? AND (?>=effectiveStartDate) AND (?<=effectiveEndDate)";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, memberAccountChargeMappingId);
            preparedStatement.setString(2, reportingDate);
            preparedStatement.setString(3, reportingDate);
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                chargeVersionRate = rs.getString("partnerCommision");
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError ::::::: Leaving transactionEntry ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return chargeVersionRate;
    }

    public List<ChargeVO> getGatewayAccountCharges(GatewayAccount gatewayAccount) throws PZDBViolationException
    {
        List<ChargeVO> chargeVOList = new ArrayList<ChargeVO>();
        ChargeVO chargeVO = null;
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt=null;
        String strQuery = "SELECT M.mappingid,M.chargeid,M.chargevalue,M.agentchargevalue,M.partnerchargevalue,M.valuetype,C.chargename,C.isinputrequired,M.keyword,M.sequencenum,M.frequency,M.category,M.subkeyword FROM gateway_accounts_charges_mapping AS M, charge_master AS C  WHERE M.chargeid=C.chargeid AND M.accountid=? ORDER BY sequencenum";
        try
        {
            con = Database.getRDBConnection();
            pstmt = con.prepareStatement(strQuery);
            pstmt.setInt(1, gatewayAccount.getAccountId());
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                chargeVO = new ChargeVO();
                chargeVO.setMappingid(rs.getString("mappingid"));
                chargeVO.setChargeid(rs.getString("chargeid"));
                chargeVO.setChargevalue(rs.getString("chargevalue"));
                chargeVO.setAgentChargeValue(rs.getString("agentchargevalue"));
                chargeVO.setPartnerChargeValue(rs.getString("partnerchargevalue"));
                chargeVO.setValuetype(rs.getString("valuetype"));
                chargeVO.setChargename(rs.getString("chargename"));
                chargeVO.setKeyword(rs.getString("keyword"));
                chargeVO.setSequencenum(rs.getString("sequencenum"));
                chargeVO.setFrequency(rs.getString("frequency"));
                chargeVO.setCategory(rs.getString("category"));
                chargeVO.setSubkeyword(rs.getString("subkeyword"));
                chargeVOList.add(chargeVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting charges list", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayAccountCharges", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting charges list::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayAccountCharges()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return chargeVOList;
    }

    public boolean checkSequenceNoAvailabilityWLPartner(WLPartnerCommissionVO wlPartnerCommissionVO)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        boolean checkAvailability = true;
        int k = 0;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT * FROM wl_partner_commission_mapping WHERE sequence_no=? and partner_id=? and pgtype_id=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, wlPartnerCommissionVO.getSequenceNo());
            pstmt.setString(2, wlPartnerCommissionVO.getPartnerId());
            pstmt.setString(3, wlPartnerCommissionVO.getPgTypeId());
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                checkAvailability = false;
            }
        }
        catch (Exception e)
        {
            logger.error("Exception::::::::" + e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return checkAvailability;
    }

    public boolean checkCommissionAvailabilityWLPartner(WLPartnerCommissionVO wlPartnerCommissionVO)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        boolean checkAvailability = true;
        int k = 0;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT * FROM wl_partner_commission_mapping WHERE partner_id=? and pgtype_id=? and commission_id=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, wlPartnerCommissionVO.getPartnerId());
            pstmt.setString(2, wlPartnerCommissionVO.getPgTypeId());
            pstmt.setString(3, wlPartnerCommissionVO.getCommissionId());
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                checkAvailability = false;
            }
        }
        catch (Exception e)
        {
            logger.error("Exception::::::::" + e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return checkAvailability;
    }

    public boolean isChargeAppliedOnMerchantAccount(ChargeVO chargeVO) throws Exception
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        boolean checkAvailability = true;
        int k = 0;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT * FROM member_accounts_charges_mapping WHERE memberid=? and accountid=? and terminalid=? and chargeid=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, chargeVO.getMemberid());
            pstmt.setString(2, chargeVO.getAccountId());
            pstmt.setString(3, chargeVO.getTerminalid());
            pstmt.setString(4, chargeVO.getChargeid());
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
    public void deleteChargeDetails(ChargeVO chargeVO) throws Exception
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "DELETE FROM member_accounts_charges_mapping WHERE memberid=? and accountid=? and terminalid=? and chargeid=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, chargeVO.getMemberid());
            pstmt.setString(2, chargeVO.getAccountId());
            pstmt.setString(3, chargeVO.getTerminalid());
            pstmt.setString(4, chargeVO.getChargeid());
            int j = pstmt.executeUpdate();
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return;
    }
    public void deleteCommissionDetails(AgentCommissionVO chargeVO) throws Exception
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "DELETE FROM agent_commission WHERE memberid=? and agentid=? and chargeid=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, chargeVO.getMemberId());
            pstmt.setString(2, chargeVO.getAgentId());
            pstmt.setString(3, chargeVO.getChargeId());
            int j = pstmt.executeUpdate();
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return;
    }

    public boolean checkSequenceNoAvailability(ChargeVO chargeVO)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        boolean checkAvailability = true;
        int k = 0;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT * FROM gateway_accounts_charges_mapping WHERE accountid=? and sequencenum=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, chargeVO.getAccountId());
            pstmt.setString(2, chargeVO.getSequencenum());
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                checkAvailability = false;
            }
        }
        catch (Exception e)
        {
            logger.error("Exception::::::::" + e);

        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return checkAvailability;
    }

    public boolean checkMemberSequenceNoAvailability(ChargeVO chargeVO)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean checkAvailability = true;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT * FROM member_accounts_charges_mapping WHERE memberid=? and accountid=? and sequencenum=? and terminalid=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, chargeVO.getMemberid());
            pstmt.setString(2, chargeVO.getAccountId());
            pstmt.setString(3, chargeVO.getSequencenum());
            pstmt.setString(4, chargeVO.getTerminalid());
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                checkAvailability = false;
            }
        }
        catch (Exception e)
        {
            logger.error("Exception::::::::" + e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return checkAvailability;
    }

    public boolean checkChargeAvailability(ChargeVO chargeVO)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        boolean checkChargeAvailability = true;
        int k = 0;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT * FROM gateway_accounts_charges_mapping WHERE accountid=? and chargeid=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, chargeVO.getAccountId());
            pstmt.setString(2, chargeVO.getChargeid());
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                checkChargeAvailability = false;
            }
        }
        catch (Exception e)
        {
            logger.error("Exception::::::::" + e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return checkChargeAvailability;
    }

    public int addNewChargeOnGatewayAccount(ChargeVO chargeVO)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        int autoKey = 0;
        int k = 0;
        try
        {
            conn = Database.getConnection();
            String query = "insert into gateway_accounts_charges_mapping (accountid,isinputrequired,chargeid,chargevalue,sequencenum,actionExecutorId,actionExecutorName) VALUES(?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, chargeVO.getAccountId());
            pstmt.setString(2, chargeVO.getIsInputRequired());
            pstmt.setString(3, chargeVO.getChargeid());
            pstmt.setString(4, chargeVO.getChargevalue());
            pstmt.setString(5, chargeVO.getSequencenum());
            pstmt.setString(6, chargeVO.getActionExecutorId());
            pstmt.setString(7, chargeVO.getActionExecutorName());
            k = pstmt.executeUpdate();
            if (k > 0)
            {
                rs = pstmt.getGeneratedKeys();
                if (rs != null)
                {
                    while (rs.next())
                    {
                        autoKey = rs.getInt(1);
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Exception::::::::" ,e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return autoKey;
    }

    public String createChargeVersionOnGatewayAccount(ChargeVersionVO chargeVersionVO)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String status = "failed";
        int k = 0;
        try
        {
            conn = Database.getConnection();
            String qry = "insert into ChargeVersionGatewayMaster (gatewayChargeValue,agentCommision,partnerCommision,effectiveStartDate,effectiveEndDate,gateway_accounts_charges_mapping_id) VALUES (?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(qry);
            pstmt.setDouble(1, chargeVersionVO.getChargeValue());
            pstmt.setDouble(2, chargeVersionVO.getAgentCommision());
            pstmt.setDouble(3, chargeVersionVO.getPartnerCommision());
            pstmt.setString(4, chargeVersionVO.getEffectiveStartDate());
            pstmt.setString(5, chargeVersionVO.getEffectiveEndDate());
            pstmt.setInt(6, chargeVersionVO.getAccounts_charges_mapping_id());
            k = pstmt.executeUpdate();
            status = "success";
        }
        catch (Exception e)
        {
            logger.error("Exception:::::" + e.getMessage());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public String updateChargeVersionOnGatewayAccount(ChargeVersionVO chargeVersionVO)
    {
        String status = "failed";
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement preparedStatement=null;
        try
        {
            conn = Database.getConnection();
            SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
            Date newStartDate = sdf.parse(chargeVersionVO.getEffectiveStartDate());
            Date dateBefore = new Date(newStartDate.getTime() - 1 * 24 * 3600 * 1000);

            String q1 = "SELECT MAX(chargeversionId) AS chargeversionId FROM ChargeVersionGatewayMaster WHERE gateway_accounts_charges_mapping_id=?";
            preparedStatement = conn.prepareStatement(q1);
            preparedStatement.setInt(1, chargeVersionVO.getAccounts_charges_mapping_id());
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                String last_update_version = rs.getString("chargeversionId");
                String updatememberhistory = "UPDATE ChargeVersionGatewayMaster SET effectiveEndDate=? WHERE chargeversionId=? AND  gateway_accounts_charges_mapping_id=?";
                preparedStatement = conn.prepareStatement(updatememberhistory);
                preparedStatement.setString(1, sdf.format(dateBefore) + " 23:59:59");
                preparedStatement.setString(2, last_update_version);
                preparedStatement.setInt(3, chargeVersionVO.getAccounts_charges_mapping_id());
                int i = preparedStatement.executeUpdate();
                if (i == 1)
                {
                    String q2 = "UPDATE gateway_accounts_charges_mapping SET chargevalue=?,agentchargevalue=?,partnerchargevalue=? WHERE mappingid=?";
                    preparedStatement = conn.prepareStatement(q2);
                    preparedStatement.setDouble(1, chargeVersionVO.getChargeValue());
                    preparedStatement.setDouble(2, chargeVersionVO.getAgentCommision());
                    preparedStatement.setDouble(3, chargeVersionVO.getPartnerCommision());
                    preparedStatement.setInt(4, chargeVersionVO.getAccounts_charges_mapping_id());
                    int j = preparedStatement.executeUpdate();
                    if (j == 1)
                    {
                        status = createChargeVersionOnGatewayAccount(chargeVersionVO);
                    }
                }
                else
                {
                    status = "failed";
                }
            }
            else
            {
                status = "failed";
            }
        }
        catch (Exception e)
        {
            logger.error(e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return status;
    }

    public MerchantRandomChargesVO getMerchantRandomChargeDetails(String merchantrdmchargeid) throws PZDBViolationException
    {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        MerchantRandomChargesVO merchantRandomChargesVO = null;
        try
        {
            con = Database.getConnection();
            StringBuffer qry = new StringBuffer("select * from merchant_random_charges where merchantrdmchargeid=? ");
            pstmt = con.prepareStatement(qry.toString());
            pstmt.setString(1, merchantrdmchargeid);

            rs = pstmt.executeQuery();
            if (rs.next())
            {
                merchantRandomChargesVO = new MerchantRandomChargesVO();
                merchantRandomChargesVO.setMerchantRdmChargeId(rs.getString("merchantrdmchargeid"));
                merchantRandomChargesVO.setMemberId(rs.getString("memberid"));
                merchantRandomChargesVO.setTerminalId(rs.getString("terminalid"));
                merchantRandomChargesVO.setChargeName(rs.getString("chargename"));
                merchantRandomChargesVO.setChargeRate(rs.getDouble("chargerate"));
                merchantRandomChargesVO.setChargeValueType(rs.getString("valuetype"));
                merchantRandomChargesVO.setChargeCounter(rs.getInt("chargecounter"));
                merchantRandomChargesVO.setChargeAmount(rs.getDouble("chargeamount"));
                merchantRandomChargesVO.setChargeValue(rs.getDouble("chargevalue"));
                merchantRandomChargesVO.setChargeRemark(rs.getString("chargeremark"));

            }
        }
        catch (SQLException e)
        {

            logger.error("SQL Exception while adding New Random Charges for Merchant", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getMerchantRandomChargeDetails()", null, "Common", "Sql exception while connecting to merchant_random_charges table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {

            logger.error("System Error while adding New Random Charges for Merchant::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getMerchantRandomChargeDetails()", null, "Common", "Sql exception while connecting to merchant_random_charges", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return merchantRandomChargesVO;
    }

    public ChargeMasterVO getBusinessChargeDetails(String businessChargeId) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ChargeMasterVO chargeMasterVO = null;
        try
        {
            con = Database.getConnection();
            StringBuffer sbQuery = new StringBuffer("select chargeid,chargename,isinputrequired,keyname,valuetype,category," +
                    "keyword,subkeyword,frequency,sequencenum,actionExecutorId,actionExecutorName from charge_master where chargeid=?");
            pstmt = con.prepareStatement(sbQuery.toString());
            pstmt.setString(1, businessChargeId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                chargeMasterVO = new ChargeMasterVO();
                chargeMasterVO.setChargeId(rs.getString("chargeid"));
                chargeMasterVO.setChargeName(rs.getString("chargename"));
                chargeMasterVO.setInInputRequired(rs.getString("isinputrequired"));
                chargeMasterVO.setKeyName(rs.getString("keyname"));
                chargeMasterVO.setValueType(rs.getString("valuetype"));
                chargeMasterVO.setCategory(rs.getString("category"));
                chargeMasterVO.setKeyword(rs.getString("keyword"));
                chargeMasterVO.setSubKeyword(rs.getString("subkeyword"));
                chargeMasterVO.setFrequency(rs.getString("frequency"));
                chargeMasterVO.setSequenceNumber(rs.getString("sequencenum"));
                chargeMasterVO.setActionExecutorId(rs.getString("actionExecutorId"));
                chargeMasterVO.setActionExecutorName(rs.getString("actionExecutorName"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting business Charge Details", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getBusinessChargeDetails()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting business Charge Details::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getBusinessChargeDetails()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return chargeMasterVO;
    }

    public String updateMerchantRandomChargeDetails(MerchantRandomChargesVO merchantRandomChargesVO) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        int k = 0;
        String status = "fail";
        try
        {
            con = Database.getConnection();
            StringBuffer qry = new StringBuffer("update merchant_random_charges set chargename=?,chargerate=?,valuetype=?,chargecounter=?," +
                    "chargeamount=?,chargevalue=?,chargeremark=? where merchantrdmchargeid=? ");
            pstmt = con.prepareStatement(qry.toString());
            pstmt.setString(1, merchantRandomChargesVO.getChargeName());
            pstmt.setDouble(2, merchantRandomChargesVO.getChargeRate());
            pstmt.setString(3, merchantRandomChargesVO.getChargeValueType());
            pstmt.setInt(4, merchantRandomChargesVO.getChargeCounter());
            pstmt.setDouble(5, merchantRandomChargesVO.getChargeAmount());
            pstmt.setDouble(6, merchantRandomChargesVO.getChargeValue());
            pstmt.setString(7, merchantRandomChargesVO.getChargeRemark());
            pstmt.setString(8, merchantRandomChargesVO.getMerchantRdmChargeId());
            k = pstmt.executeUpdate();
            if (k > 0)
            {
                status = "success";
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while adding new Fraud Merchant account_rule_mapping", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "updateMerchantRandomChargeDetails()", null, "Common", "Sql exception while connecting to merchant_random_charges table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while  adding new Fraud Merchant account_rule_mapping::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "updateMerchantRandomChargeDetails()", null, "Common", "Sql exception while connecting to merchant_random_charges", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return status;
    }

    public List<MerchantRandomChargesVO> getMerchantRandomChargesList(String bankwireId, String memberId, String terminalId) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        StringBuffer sb = null;
        List<MerchantRandomChargesVO> merchantRandomChargesVOList = new ArrayList<MerchantRandomChargesVO>();
        MerchantRandomChargesVO merchantRandomChargesVO = null;
        try
        {
            con = Database.getConnection();
            sb = new StringBuffer("SELECT merchantrdmchargeid,chargename,chargerate,valuetype,chargecounter,chargeamount,chargevalue,chargetype,terminalid,chargeremark FROM merchant_random_charges where bankwireid=? and memberid=? and terminalid=? ");
            preparedStatement = con.prepareStatement(sb.toString());
            preparedStatement.setString(1, bankwireId);
            preparedStatement.setString(2, memberId);
            preparedStatement.setString(3, terminalId);
            rs = preparedStatement.executeQuery();
            while (rs.next())
            {
                merchantRandomChargesVO = new MerchantRandomChargesVO();
                merchantRandomChargesVO.setMerchantRdmChargeId(rs.getString("merchantrdmchargeid"));
                merchantRandomChargesVO.setChargeName(rs.getString("chargename"));
                merchantRandomChargesVO.setChargeRate(rs.getDouble("chargerate"));
                merchantRandomChargesVO.setChargeValueType(rs.getString("valuetype"));
                merchantRandomChargesVO.setChargeCounter(rs.getInt("chargecounter"));
                merchantRandomChargesVO.setChargeAmount(rs.getDouble("chargeamount"));
                merchantRandomChargesVO.setChargeValue(rs.getDouble("chargevalue"));
                merchantRandomChargesVO.setChargeType(rs.getString("chargetype"));
                merchantRandomChargesVO.setTerminalId(rs.getString("terminalid"));
                merchantRandomChargesVO.setChargeRemark(rs.getString("chargeremark"));
                merchantRandomChargesVOList.add(merchantRandomChargesVO);
            }

        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting random charges list", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getMerchantRandomCharges", null, "Common", "Sql exception while connecting to merchant_random_charges table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting  random charges list::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getMerchantRandomCharges()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return merchantRandomChargesVOList;
    }

    public HashMap<String,MerchantRandomChargesVO> getMerchantRandomChargesListVO(String bankwireId, String memberId, String terminalId) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        StringBuffer sb = null;
        HashMap<String,MerchantRandomChargesVO> merchantRandomChargesVOList = new HashMap<String,MerchantRandomChargesVO>();
        MerchantRandomChargesVO merchantRandomChargesVO = null;
        try
        {
            con = Database.getConnection();
            sb = new StringBuffer("SELECT merchantrdmchargeid,chargename,chargerate,valuetype,chargecounter,chargeamount,chargevalue,chargetype,terminalid,chargeremark FROM merchant_random_charges where bankwireid=? and memberid=? and terminalid=? ");
            preparedStatement = con.prepareStatement(sb.toString());
            preparedStatement.setString(1, bankwireId);
            preparedStatement.setString(2, memberId);
            preparedStatement.setString(3, terminalId);
            rs = preparedStatement.executeQuery();
            while (rs.next())
            {
                merchantRandomChargesVO = new MerchantRandomChargesVO();
                merchantRandomChargesVO.setMerchantRdmChargeId(rs.getString("merchantrdmchargeid"));
                merchantRandomChargesVO.setChargeName(rs.getString("chargename"));
                merchantRandomChargesVO.setChargeRate(rs.getDouble("chargerate"));
                merchantRandomChargesVO.setChargeValueType(rs.getString("valuetype"));
                merchantRandomChargesVO.setChargeCounter(rs.getInt("chargecounter"));
                merchantRandomChargesVO.setChargeAmount(rs.getDouble("chargeamount"));
                merchantRandomChargesVO.setChargeValue(rs.getDouble("chargevalue"));
                merchantRandomChargesVO.setChargeType(rs.getString("chargetype"));
                merchantRandomChargesVO.setTerminalId(rs.getString("terminalid"));
                merchantRandomChargesVO.setChargeRemark(rs.getString("chargeremark"));
                merchantRandomChargesVOList.put(merchantRandomChargesVO.getChargeName(),merchantRandomChargesVO);
            }

        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting random charges list", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getMerchantRandomCharges", null, "Common", "Sql exception while connecting to merchant_random_charges table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting  random charges list::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getMerchantRandomCharges()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return merchantRandomChargesVOList;
    }

    public String addNewPartnerCommission(PartnerCommissionVO partnerCommissionVO) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        String status = "";
        try
        {
            con = Database.getConnection();
            StringBuffer buffer = new StringBuffer("select * from partner_commission where chargeid=? and partnerid=? and memberid=? and terminalid=?");
            preparedStatement = con.prepareStatement(buffer.toString());
            preparedStatement.setString(1, partnerCommissionVO.getChargeId());
            preparedStatement.setString(2, partnerCommissionVO.getPartnerId());
            preparedStatement.setString(3, partnerCommissionVO.getMemberId());
            preparedStatement.setString(4, partnerCommissionVO.getTerminalId());
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                logger.error("This commission is already mapped");
                status = "This commission is already mapped";
                return status;
            }

            buffer = new StringBuffer("select * from partner_commission where partnerid=? and memberid=? and terminalid=? and sequence_no=?");
            preparedStatement = con.prepareStatement(buffer.toString());
            preparedStatement.setString(1, partnerCommissionVO.getPartnerId());
            preparedStatement.setString(2, partnerCommissionVO.getMemberId());
            preparedStatement.setString(3, partnerCommissionVO.getTerminalId());
            preparedStatement.setString(4, partnerCommissionVO.getSequenceNo());
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                logger.error("Sequence number is already allocated,please provide unique");
                status = "Sequence number is already allocated,please provide unique";
                return status;
            }

            StringBuffer stringBuffer = new StringBuffer("insert into partner_commission(id,partnerid,memberid,terminalid,chargeid,commission_value,startdate,enddate,sequence_no,actionExecutorId,actionExecutorName)" +
                    "value(null,?,?,?,?,?,?,?,?,?,?)");
            preparedStatement = con.prepareStatement(stringBuffer.toString());
            preparedStatement.setString(1, partnerCommissionVO.getPartnerId());
            preparedStatement.setString(2, partnerCommissionVO.getMemberId());
            preparedStatement.setString(3, partnerCommissionVO.getTerminalId());
            preparedStatement.setString(4, partnerCommissionVO.getChargeId());
            preparedStatement.setDouble(5, partnerCommissionVO.getCommissionValue());
            preparedStatement.setString(6, partnerCommissionVO.getStartDate());
            preparedStatement.setString(7, partnerCommissionVO.getEndDate());
            preparedStatement.setString(8, partnerCommissionVO.getSequenceNo());
            preparedStatement.setString(9, partnerCommissionVO.getActionExecutorId());
            preparedStatement.setString(10, partnerCommissionVO.getActionExecutorName());
            int k = preparedStatement.executeUpdate();
            if (k > 0)
            {
                status = "Commission Added Successfully";
            }

        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while adding partner commission", e);
            status = "Could not mapped commission,some internal exception";
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "addPartnerCommision", null, "Common", "Sql exception while connecting to partner commission table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while adding partner commission", systemError);
            status = "Could not mapped commission,some internal exception";
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayAccountCharges()", null, "Common", "Sql exception while connecting to partner commission table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return status;
    }

    public String applyCommissionOnWLPartner(WLPartnerCommissionVO wlPartnerCommissionVO) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        String status = "";
        try
        {
            con = Database.getConnection();
            StringBuffer stringBuffer = new StringBuffer("insert into wl_partner_commission_mapping(id,partner_id,pgtype_id,commission_id,commission_value,isinput_required,sequence_no,isActive,start_date,end_date,actionExecutorId,actionExecutorName,creation_on)" +
                    "value(null,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()))");
            preparedStatement = con.prepareStatement(stringBuffer.toString());
            preparedStatement.setString(1, wlPartnerCommissionVO.getPartnerId());
            preparedStatement.setString(2, wlPartnerCommissionVO.getPgTypeId());
            preparedStatement.setString(3, wlPartnerCommissionVO.getCommissionId());
            preparedStatement.setDouble(4, wlPartnerCommissionVO.getCommissionValue());
            preparedStatement.setString(5, wlPartnerCommissionVO.getIsInputRequired());
            preparedStatement.setString(6, wlPartnerCommissionVO.getSequenceNo());
            preparedStatement.setString(7, wlPartnerCommissionVO.getIsActive());
            preparedStatement.setString(8, wlPartnerCommissionVO.getStartDate());
            preparedStatement.setString(9, wlPartnerCommissionVO.getEndDate());
            preparedStatement.setString(10, wlPartnerCommissionVO.getActionExecutorId());
            preparedStatement.setString(11, wlPartnerCommissionVO.getActionExecutorName());

            int k = preparedStatement.executeUpdate();
            if (k > 0)
            {
                status = " Commission added successfully";
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::::::", e);
            status = "Internal error while processing your request.";
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "wlPartnerCommision", null, "Common", "Sql exception while connecting to wl_partner_commission_mapping table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::::::", systemError);
            status = "Internal error while processing your request.";
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayAccountCharges()", null, "Common", "Sql exception while connecting to wl_partner_commission_mapping table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return status;
    }

    public boolean updateWLPartnerCommissionValue(WLPartnerCommissionVO wlPartnerCommissionVO) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        int k = 0;
        boolean b = false;
        try
        {
            con = Database.getConnection();
            StringBuffer query = new StringBuffer("update wl_partner_commission_mapping set commission_value=?,start_date=?,end_date=?,isinput_required=?,isActive=? where id=?");
            pstmt = con.prepareStatement(query.toString());
            pstmt.setDouble(1, wlPartnerCommissionVO.getCommissionValue());
            pstmt.setString(2, wlPartnerCommissionVO.getStartDate());
            pstmt.setString(3, wlPartnerCommissionVO.getEndDate());
            pstmt.setString(4, wlPartnerCommissionVO.getIsInputRequired());
            pstmt.setString(5, wlPartnerCommissionVO.getIsActive());
            pstmt.setString(6, wlPartnerCommissionVO.getCommissionId());
            k = pstmt.executeUpdate();
            if (k > 0)
            {
                b = true;
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while update WL partner commission ", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "updateWLPartnerCommissionValue()", null, "Common", "Sql exception while connecting to wl_partner_commission_mapping table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while while WL partner commission ", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "updateWLPartnerCommissionValue()", null, "Common", "Sql exception while connecting to wl_partner_commission_mapping table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return b;
    }

    public WLPartnerCommissionVO getWLPartnerCommissionDetails(String commissionId) throws PZDBViolationException
    {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        WLPartnerCommissionVO wlPartnerCommissionVO = null;
        try
        {
            con = Database.getConnection();
            StringBuffer sbQuery = new StringBuffer("SELECT wlpc.id,wlpc.partner_id, p.partnerName, wlpc.pgtype_id, gt.gateway,gt.currency,wlpc.commission_id,cm.chargename,wlpc.commission_value, wlpc.start_date,wlpc.end_date, wlpc.isinput_required,wlpc.sequence_no,wlpc.isActive FROM wl_partner_commission_mapping AS wlpc JOIN partners p ON wlpc.partner_id=p.partnerid LEFT JOIN gateway_type gt ON wlpc.pgtype_id=gt.pgtypeid JOIN charge_master cm ON wlpc.commission_id=cm.chargeid where wlpc.id=?");
            pstmt = con.prepareStatement(sbQuery.toString());
            pstmt.setString(1, commissionId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                wlPartnerCommissionVO = new WLPartnerCommissionVO();
                wlPartnerCommissionVO.setCommissionId(rs.getString("id"));
                wlPartnerCommissionVO.setPartnerName(rs.getString("partnerName"));
                wlPartnerCommissionVO.setGateway(rs.getString("gateway"));
                wlPartnerCommissionVO.setCurrency(rs.getString("currency"));
                wlPartnerCommissionVO.setChargeName(rs.getString("chargename"));
                wlPartnerCommissionVO.setCommissionValue(Double.valueOf(rs.getString("commission_value")));
                wlPartnerCommissionVO.setIsInputRequired(rs.getString("isinput_required"));
                wlPartnerCommissionVO.setStartDate(rs.getString("start_date"));
                wlPartnerCommissionVO.setEndDate(rs.getString("end_date"));
                wlPartnerCommissionVO.setSequenceNo(rs.getString("sequence_no"));
                wlPartnerCommissionVO.setIsActive(rs.getString("isActive"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting WL partner commission Details", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getWLPartnerCommissionDetails()", null, "Common", "Sql exception while connecting to wl_partner_commission_mapping table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting WL partner commission Details::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getWLPartnerCommissionDetails()", null, "Common", "Sql exception while connecting to wl_partner_commission_mapping table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return wlPartnerCommissionVO;
    }

    public List<PartnerCommissionVO> getPartnerCommissionOnTerminal(String partnerId, String memberId, String terminalId) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        StringBuffer query = new StringBuffer("");
        List<PartnerCommissionVO> partnerCommissionVOList = new ArrayList<PartnerCommissionVO>();
        PartnerCommissionVO partnerCommissionVO = null;
        ChargeMasterVO chargeMasterVO = null;
        try
        {
            con = Database.getConnection();
            query.append("SELECT pc.id,pc.chargeid,pc.commission_value,pc.sequence_no,cm.valuetype,cm.category,cm.keyword,cm.subkeyword,cm.frequency,cm.chargename FROM partner_commission AS pc JOIN charge_master AS cm ON pc.chargeid=cm.chargeid and pc.partnerid=? and pc.memberid=? and pc.terminalid=?");
            preparedStatement = con.prepareStatement(query.toString());
            preparedStatement.setString(1, partnerId);
            preparedStatement.setString(2, memberId);
            preparedStatement.setString(3, terminalId);
            rs = preparedStatement.executeQuery();
            while (rs.next())
            {
                chargeMasterVO = new ChargeMasterVO();
                partnerCommissionVO = new PartnerCommissionVO();

                partnerCommissionVO.setCommissionId(rs.getString("id"));
                partnerCommissionVO.setChargeId(rs.getString("chargeid"));
                partnerCommissionVO.setCommissionValue(rs.getDouble("commission_value"));
                chargeMasterVO.setChargeId(rs.getString("chargeid"));
                chargeMasterVO.setChargeName(rs.getString("chargename"));
                chargeMasterVO.setValueType(rs.getString("valuetype"));
                chargeMasterVO.setCategory(rs.getString("category"));
                chargeMasterVO.setKeyword(rs.getString("keyword"));
                chargeMasterVO.setSubKeyword(rs.getString("subkeyword"));
                chargeMasterVO.setFrequency(rs.getString("frequency"));

                partnerCommissionVO.setChargeMasterVO(chargeMasterVO);
                partnerCommissionVOList.add(partnerCommissionVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException while adding partner commission", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getPartnerCommissionOnTerminal", null, "Common", "Sql exception while connecting to partner commission table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while adding partner commission", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getPartnerCommissionOnTerminal()", null, "Common", "Sql exception while connecting to partner commission table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return partnerCommissionVOList;
    }

    public List<AgentCommissionVO> getAgentCommissionOnTerminal(String agentId, String memberId, String terminalId) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        StringBuffer query = new StringBuffer("");
        List<AgentCommissionVO> agentCommissionVOList = new ArrayList<AgentCommissionVO>();
        AgentCommissionVO agentCommissionVO = null;
        ChargeMasterVO chargeMasterVO = null;
        try
        {
            con = Database.getConnection();
            query.append("SELECT pc.id,pc.chargeid,pc.commission_value,pc.sequence_no,cm.valuetype,cm.category,cm.keyword,cm.subkeyword,cm.frequency,cm.chargename FROM agent_commission AS pc JOIN charge_master AS cm ON pc.chargeid=cm.chargeid and pc.agentid=? and pc.memberid=? and pc.terminalid=?");
            preparedStatement = con.prepareStatement(query.toString());
            preparedStatement.setString(1, agentId);
            preparedStatement.setString(2, memberId);
            preparedStatement.setString(3, terminalId);
            logger.error("query for charges:::::"+preparedStatement);
            rs = preparedStatement.executeQuery();
            while (rs.next())
            {
                chargeMasterVO = new ChargeMasterVO();
                agentCommissionVO = new AgentCommissionVO();

                agentCommissionVO.setCommissionId(rs.getString("id"));
                agentCommissionVO.setChargeId(rs.getString("chargeid"));
                agentCommissionVO.setCommissionValue(rs.getDouble("commission_value"));
                chargeMasterVO.setChargeId(rs.getString("chargeid"));
                chargeMasterVO.setChargeName(rs.getString("chargename"));
                chargeMasterVO.setValueType(rs.getString("valuetype"));
                chargeMasterVO.setCategory(rs.getString("category"));
                chargeMasterVO.setKeyword(rs.getString("keyword"));
                chargeMasterVO.setSubKeyword(rs.getString("subkeyword"));
                chargeMasterVO.setFrequency(rs.getString("frequency"));

                agentCommissionVO.setChargeMasterVO(chargeMasterVO);
                agentCommissionVOList.add(agentCommissionVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException while getting  agent commission", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getAgentCommissionOnTerminal", null, "Common", "Sql exception while connecting to agent commission table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while getting  agent commission", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getAgentCommissionOnTerminal()", null, "Common", "Sql exception while connecting to agent commission table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return agentCommissionVOList;
    }

    public boolean updatePartnerCommissionValue(PartnerCommissionVO partnerCommissionVO) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        int k = 0;
        boolean b = false;
        try
        {
            con = Database.getConnection();
            StringBuffer query = new StringBuffer("update partner_commission set commission_value=? where id=?");
            pstmt = con.prepareStatement(query.toString());
            pstmt.setDouble(1, partnerCommissionVO.getCommissionValue());
            pstmt.setString(2, partnerCommissionVO.getCommissionId());
            k = pstmt.executeUpdate();
            if (k > 0)
            {
                b = true;
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while update partner commission ", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "updatePartnerCommissionValue()", null, "Common", "Sql exception while connecting to partner commission table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while while partner commission ", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "updatePartnerCommissionValue()", null, "Common", "Sql exception while connecting to partner commission table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return b;
    }

    public PartnerCommissionVO getPartnerCommissionDetails(String commissionId) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PartnerCommissionVO partnerCommissionVO = new PartnerCommissionVO();
        ChargeMasterVO chargeMasterVO = new ChargeMasterVO();
        try
        {
            con = Database.getConnection();
            StringBuffer sbQuery = new StringBuffer("select pc.id,pc.partnerid,pc.memberid,pc.terminalid,pc.chargeid,pc.commission_value,pc.startdate,pc.enddate,pc.sequence_no,cm.chargename from partner_commission as pc JOIN charge_master cm on pc.chargeid=cm.chargeid where pc.id=?");
            pstmt = con.prepareStatement(sbQuery.toString());
            pstmt.setString(1, commissionId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                partnerCommissionVO.setCommissionId(rs.getString("id"));
                partnerCommissionVO.setPartnerId(rs.getString("partnerid"));
                partnerCommissionVO.setMemberId(rs.getString("memberid"));
                partnerCommissionVO.setTerminalId(rs.getString("terminalid"));
                partnerCommissionVO.setChargeId(rs.getString("chargeid"));
                chargeMasterVO.setChargeName(rs.getString("chargename"));
                partnerCommissionVO.setChargeMasterVO(chargeMasterVO);
                partnerCommissionVO.setCommissionValue(Double.valueOf(rs.getString("commission_value")));
                partnerCommissionVO.setStartDate(rs.getString("startdate"));
                partnerCommissionVO.setEndDate(rs.getString("enddate"));
                partnerCommissionVO.setSequenceNo(rs.getString("sequence_no"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting partner commission Details", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getPartnerCommissionDetails()", null, "Common", "Sql exception while connecting to partner_commision table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting partner commission Details::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getPartnerCommissionDetails()", null, "Common", "Sql exception while connecting to partner_commision table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return partnerCommissionVO;
    }

    public String addNewAgentCommission(AgentCommissionVO agentCommissionVO) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        String status = "";
        try
        {
            con = Database.getConnection();
            StringBuffer buffer = new StringBuffer("select * from agent_commission where chargeid=? and agentid=? and memberid=? and terminalid=?");
            preparedStatement = con.prepareStatement(buffer.toString());
            preparedStatement.setString(1, agentCommissionVO.getChargeId());
            preparedStatement.setString(2, agentCommissionVO.getAgentId());
            preparedStatement.setString(3, agentCommissionVO.getMemberId());
            preparedStatement.setString(4, agentCommissionVO.getTerminalId());
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                logger.error("This commission is already mapped");
                status = "This commission is already mapped";
                return status;
            }

            buffer = new StringBuffer("select * from agent_commission where agentid=? and memberid=? and terminalid=? and sequence_no=?");
            preparedStatement = con.prepareStatement(buffer.toString());
            preparedStatement.setString(1, agentCommissionVO.getAgentId());
            preparedStatement.setString(2, agentCommissionVO.getMemberId());
            preparedStatement.setString(3, agentCommissionVO.getTerminalId());
            preparedStatement.setString(4, agentCommissionVO.getSequenceNo());
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                logger.error("Sequence number is already allocated on same terminal,please provide unique sequence number");
                status = "Sequence number is already allocated on same terminal,please provide unique";
                return status;
            }

            StringBuffer stringBuffer = new StringBuffer("insert into agent_commission(id,agentid,memberid,terminalid,chargeid,commission_value,startdate,enddate,sequence_no,actionExecutorId,actionExecutorName)" +
                    "value(null,?,?,?,?,?,?,?,?,?,?)");
            preparedStatement = con.prepareStatement(stringBuffer.toString());
            preparedStatement.setString(1, agentCommissionVO.getAgentId());
            preparedStatement.setString(2, agentCommissionVO.getMemberId());
            preparedStatement.setString(3, agentCommissionVO.getTerminalId());
            preparedStatement.setString(4, agentCommissionVO.getChargeId());
            preparedStatement.setDouble(5, agentCommissionVO.getCommissionValue());
            preparedStatement.setString(6, agentCommissionVO.getStartDate());
            preparedStatement.setString(7, agentCommissionVO.getEndDate());
            preparedStatement.setString(8, agentCommissionVO.getSequenceNo());
            preparedStatement.setString(9, agentCommissionVO.getActionExecutorId());
            preparedStatement.setString(10, agentCommissionVO.getActionExecutorName());
            int k = preparedStatement.executeUpdate();
            if (k > 0)
            {
                status = "Commission mapped successfully";
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException while adding agent commission", e);
            status = "Could not mapped commission,some internal exception";
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "addNewAgentCommission()", null, "Common", "Sql exception while connecting to agent commission table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while adding agent commission", systemError);
            status = "Could not mapped commission,some internal exception";
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "addNewAgentCommission()", null, "Common", "Sql exception while connecting to agent commission table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return status;
    }

    public boolean updateAgentCommissionValue(AgentCommissionVO agentCommissionVO) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        int k = 0;
        boolean b = false;
        try
        {
            con = Database.getConnection();
            StringBuffer query = new StringBuffer("update agent_commission set commission_value=?,startdate=?,enddate=? where id=?");
            pstmt = con.prepareStatement(query.toString());
            pstmt.setDouble(1, agentCommissionVO.getCommissionValue());
            pstmt.setString(2, agentCommissionVO.getStartDate());
            pstmt.setString(3, agentCommissionVO.getEndDate());
            pstmt.setString(4, agentCommissionVO.getCommissionId());
            k = pstmt.executeUpdate();
            if (k > 0)
            {
                b = true;
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException while update agent commission ", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "updateAgentCommissionValue()", null, "Common", "Sql exception while connecting to agent commission table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while while agent commission ", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "updateAgentCommissionValue()", null, "Common", "Sql exception while connecting to agent commission table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return b;
    }

    public AgentCommissionVO getAgentCommissionDetails(String commissionId) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        AgentCommissionVO agentCommissionVO = new AgentCommissionVO();
        ChargeMasterVO chargeMasterVO = new ChargeMasterVO();
        try
        {
            con = Database.getConnection();
            StringBuffer sbQuery = new StringBuffer("select ac.id,ac.agentid,ac.memberid,ac.terminalid,ac.chargeid,ac.commission_value,ac.startdate,ac.enddate,ac.sequence_no,cm.chargename from agent_commission as ac JOIN charge_master cm on ac.chargeid=cm.chargeid where ac.id=?");
            pstmt = con.prepareStatement(sbQuery.toString());
            pstmt.setString(1, commissionId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                agentCommissionVO.setCommissionId(rs.getString("id"));
                agentCommissionVO.setAgentId(rs.getString("agentid"));
                agentCommissionVO.setMemberId(rs.getString("memberid"));
                agentCommissionVO.setTerminalId(rs.getString("terminalid"));
                agentCommissionVO.setChargeId(rs.getString("chargeid"));
                chargeMasterVO.setChargeName(rs.getString("chargename"));
                agentCommissionVO.setChargeMasterVO(chargeMasterVO);
                agentCommissionVO.setCommissionValue(Double.valueOf(rs.getString("commission_value")));
                agentCommissionVO.setStartDate(rs.getString("startdate"));
                agentCommissionVO.setEndDate(rs.getString("enddate"));
                agentCommissionVO.setSequenceNo(rs.getString("sequence_no"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException while getting agent commission Details", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getAgentCommissionDetails()", null, "Common", "Sql exception while connecting to agent_commision table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while getting agent commission Details::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getAgentCommissionDetails()", null, "Common", "Sql exception while connecting to agent_commision table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return agentCommissionVO;
    }

    public List<ChargeVO> getTobeDebitedAgentCharges(TerminalVO terminalVO)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<ChargeVO> chargeVOList = new ArrayList();
        ChargeVO chargeVO = null;
        try
        {
            con = Database.getConnection();
            String Query = "SELECT mc.mappingid,mc.memberid,mc.accountid,mc.paymodeid,mc.cardtypeid,mc.chargeid,mc.chargevalue,cm.valuetype,cm.keyword,cm.sequencenum,cm.frequency,cm.category,cm.subkeyword,mc.terminalid,mc.agentchargevalue,mc.partnerchargevalue,cm.chargename FROM member_accounts_charges_mapping AS mc JOIN charge_master AS cm ON mc.chargeid=cm.chargeid AND agentchargevalue IS NOT NULL AND agentchargevalue>0.00 AND memberid=? AND accountid=? AND terminalid=?";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getTerminalId());
            rs = preparedStatement.executeQuery();
            while (rs.next())
            {
                chargeVO = new ChargeVO();
                chargeVO.setMappingid(rs.getString("mappingid"));
                chargeVO.setMemberid(rs.getString("memberid"));
                chargeVO.setAccountId(rs.getString("accountid"));
                chargeVO.setPaymentName(rs.getString("paymodeid"));
                chargeVO.setCardType(rs.getString("cardtypeid"));
                chargeVO.setChargeid(rs.getString("chargeid"));
                chargeVO.setChargename(rs.getString("chargename"));
                chargeVO.setChargevalue(rs.getString("chargevalue"));
                chargeVO.setValuetype(rs.getString("valuetype"));
                chargeVO.setKeyword(rs.getString("keyword"));
                chargeVO.setSequencenum(rs.getString("sequencenum"));
                chargeVO.setFrequency(rs.getString("frequency"));
                chargeVO.setCategory(rs.getString("category"));
                chargeVO.setSubkeyword(rs.getString("subkeyword"));
                chargeVO.setTerminalid(rs.getString("terminalid"));
                chargeVO.setAgentChargeValue(rs.getString("agentchargevalue"));
                chargeVO.setPartnerChargeValue(rs.getString("partnerchargevalue"));
                chargeVOList.add(chargeVO);
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException", se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return chargeVOList;
    }

    public List<ChargeVO> getTobeDebitedPartnerCharges(TerminalVO terminalVO)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<ChargeVO> chargeVOList = new ArrayList();
        ChargeVO chargeVO = null;
        try
        {
            con = Database.getConnection();
            String Query = "SELECT mc.mappingid,mc.memberid,mc.accountid,mc.paymodeid,mc.cardtypeid,mc.chargeid,mc.chargevalue,cm.valuetype,cm.keyword,cm.sequencenum,cm.frequency,cm.category,cm.subkeyword,mc.terminalid,mc.agentchargevalue,mc.partnerchargevalue,cm.chargename FROM member_accounts_charges_mapping AS mc JOIN charge_master AS cm ON mc.chargeid=cm.chargeid AND agentchargevalue IS NOT NULL /*AND agentchargevalue>0.00 */AND memberid=? AND accountid=? AND terminalid=?";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getTerminalId());
            rs = preparedStatement.executeQuery();
            while (rs.next())
            {
                chargeVO = new ChargeVO();
                chargeVO.setMappingid(rs.getString("mappingid"));
                chargeVO.setMemberid(rs.getString("memberid"));
                chargeVO.setAccountId(rs.getString("accountid"));
                chargeVO.setPaymentName(rs.getString("paymodeid"));
                chargeVO.setCardType(rs.getString("cardtypeid"));
                chargeVO.setChargeid(rs.getString("chargeid"));
                chargeVO.setChargename(rs.getString("chargename"));
                chargeVO.setChargevalue(rs.getString("chargevalue"));
                chargeVO.setValuetype(rs.getString("valuetype"));
                chargeVO.setKeyword(rs.getString("keyword"));
                chargeVO.setSequencenum(rs.getString("sequencenum"));
                chargeVO.setFrequency(rs.getString("frequency"));
                chargeVO.setCategory(rs.getString("category"));
                chargeVO.setSubkeyword(rs.getString("subkeyword"));
                chargeVO.setTerminalid(rs.getString("terminalid"));
                chargeVO.setAgentChargeValue(rs.getString("agentchargevalue"));
                chargeVO.setPartnerChargeValue(rs.getString("partnerchargevalue"));
                chargeVOList.add(chargeVO);
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException", se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return chargeVOList;
    }

    public List<ChargeVO> getListOfCharges() throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        ChargeVO chargeVO = null;
        List<ChargeVO> chargeVOList = new LinkedList();
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            query = "Select chargeid, chargename from charge_master ORDER BY chargeid ASC ";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                chargeVO = new ChargeVO();
                chargeVO.setChargeid(rs.getString("chargeid"));
                chargeVO.setChargename(rs.getString("chargename"));
                chargeVOList.add(chargeVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("Leaving ChargesDAO throwing SQL Exception as System Error :::: ", e);
            PZExceptionHandler.raiseDBViolationException("ChargesDAO", "getListOfCharges()", null, "Common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("Leaving ChargesDAO throwing SQL Exception as System Error :::: ", se);
            PZExceptionHandler.raiseDBViolationException("ChargesDAO", "getListOfCharges()", null, "Common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());

        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return chargeVOList;
    }

    public Hashtable getListOfGatewayAccountCharges(String pgtypeid, String currency, String accountId, String chargeId, String gateway, int start, int end, String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        Connection conn = null;
        Hashtable hash = null;
        ResultSet rs=null;
        try
        {
            conn = Database.getConnection();
            Functions functions = new Functions();

            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            StringBuffer query = new StringBuffer("SELECT ga.mappingid,ga.accountid,ga.isinputrequired,cm.chargename,ga.chargevalue,ga.sequencenum,ga.actionExecutorId,ga.actionExecutorName FROM gateway_accounts_charges_mapping AS ga JOIN charge_master AS cm ON cm.chargeid=ga.chargeid JOIN gateway_accounts AS g ON ga.accountid=g.accountid JOIN gateway_type AS gt ON gt.pgtypeid=g.pgtypeid WHERE mappingid>0");
            StringBuffer countquery = new StringBuffer("select count(*) from gateway_accounts_charges_mapping AS ga JOIN charge_master AS cm ON cm.chargeid=ga.chargeid JOIN gateway_accounts AS g ON ga.accountid=g.accountid JOIN gateway_type AS gt ON gt.pgtypeid=g.pgtypeid where mappingid>0");
            if (functions.isValueNull(accountId))
            {
                query.append(" and ga.accountid='" + ESAPI.encoder().encodeForSQL(me, accountId) + "'");
                countquery.append(" and ga.accountid='" + ESAPI.encoder().encodeForSQL(me, accountId) + "'");
            }
            if (functions.isValueNull(chargeId))
            {
                query.append(" and ga.chargeid=" + ESAPI.encoder().encodeForSQL(me, chargeId) + "");
                countquery.append(" and ga.chargeid=" + ESAPI.encoder().encodeForSQL(me, chargeId) + "");
            }
            if (functions.isValueNull(gateway) & !("0".equals(gateway)))
            {
                query.append(" and gt.gateway='" + ESAPI.encoder().encodeForSQL(me, gateway) + "'");
                countquery.append(" and gt.gateway='" + ESAPI.encoder().encodeForSQL(me, gateway) + "'");
            }
            if (functions.isValueNull(pgtypeid))
            {
                query.append(" and gt.pgtypeid='" + ESAPI.encoder().encodeForSQL(me, pgtypeid) + "'");
                countquery.append(" and gt.pgtypeid='" + ESAPI.encoder().encodeForSQL(me, pgtypeid) + "'");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and gt.currency='" + ESAPI.encoder().encodeForSQL(me, currency) + "'");
                countquery.append(" and gt.currency='" + ESAPI.encoder().encodeForSQL(me, currency) + "'");
            }

            query.append(" order by mappingid asc LIMIT " + start + "," + end);

            logger.debug("Query:-" + query);
            logger.debug("CountQuery:-" + countquery);
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));

            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException e)
        {
            logger.error("Leaving ChargesDAO throwing SQL Exception as System Error :::: ", e);
            PZExceptionHandler.raiseDBViolationException("ChargesDAO", "getListOfGatewayAccountCharges()", null, "Common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("Leaving ChargesDAO throwing SQL Exception as System Error :::: ", se);
            PZExceptionHandler.raiseDBViolationException("ChargesDAO", "getListOfGatewayAccountCharges()", null, "Common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return hash;
    }
    public HashMap getListOfGatewayAccountCharge(String mappingId) throws PZDBViolationException
    {
        Connection conn = null;
        Hashtable hash = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        HashMap rsDetails = new HashMap();
        try
        {
            conn = Database.getRDBConnection();
            Functions functions = new Functions();
            String qry = "SELECT m.terminalid,m.accountid,m.mappingid,h.* FROM ChargeVersionGatewayMaster AS h, gateway_accounts_charges_mapping AS m WHERE m.mappingid=h.gateway_accounts_charges_mapping_id AND m.mappingid=? ORDER BY chargeversionId DESC ";
            pstmt = conn.prepareStatement(qry);
            pstmt.setString(1, mappingId);
            rs = pstmt.executeQuery();
            int i = 1;
            while (rs.next())
            {
                HashMap record = new HashMap();
                record.put("terminalid", rs.getString("terminalid"));
                record.put("accountid", rs.getString("accountid"));
                record.put("mappingid", rs.getString("mappingid"));
                record.put("gatewayChargeValue", rs.getString("gatewayChargeValue"));
                record.put("agentCommision", rs.getString("agentCommision"));
                record.put("partnerCommision", rs.getString("partnerCommision"));
                record.put("effectiveStartDate", rs.getString("effectiveStartDate"));
                record.put("effectiveEndDate", rs.getString("effectiveEndDate"));
                rsDetails.put(i, record);
                i = i + 1;
            }
        }
        catch (SQLException e)
        {
            logger.error("Leaving ChargesDAO throwing SQL Exception as System Error :::: ", e);
            PZExceptionHandler.raiseDBViolationException("ChargesDAO", "getGatewayAccountCharge()", null, "Common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("Leaving ChargesDAO throwing SQL Exception as System Error :::: ", se);
            PZExceptionHandler.raiseDBViolationException("ChargesDAO", "getGatewayAccountCharge()", null, "Common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());

        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return rsDetails;

    }

    public HashMap getGatewayAccountCharges(String mappingId) throws PZDBViolationException
    {
        Connection conn = null;
        Hashtable hash = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        HashMap rsDetails = new HashMap();
        try
        {
            conn = Database.getRDBConnection();
            String qry = "select * from gateway_accounts_charges_mapping where mappingid=?";
            pstmt = conn.prepareStatement(qry);
            pstmt.setString(1, mappingId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                rsDetails.put("accountid", rs.getString("accountid"));
                rsDetails.put("paymodeid", rs.getString("paymodeid"));
                rsDetails.put("isinputrequired", rs.getString("isinputrequired"));
                rsDetails.put("chargeid", rs.getString("chargeid"));
                rsDetails.put("chargevalue", rs.getString("chargevalue"));
                rsDetails.put("sequencenum", rs.getString("sequencenum"));
            }
        }
        catch (SQLException e)
        {
            logger.error("Leaving ChargesDAO throwing SQL Exception as System Error :::: ", e);
            PZExceptionHandler.raiseDBViolationException("ChargesDAO", "getGatewayAccountCharges()", null, "Common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("Leaving ChargesDAO throwing SQL Exception as System Error :::: ", se);
            PZExceptionHandler.raiseDBViolationException("ChargesDAO", "getGatewayAccountCharges()", null, "Common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());

        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return rsDetails;
    }

    public HashMap getGatewayAccountChargeDate(String mappingId) throws PZDBViolationException
    {
        Connection conn = null;
        Hashtable hash = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        HashMap rsDetails = new HashMap();
        try
        {
            conn = Database.getRDBConnection();
            String qry = "SELECT effectiveStartDate,effectiveEndDate FROM ChargeVersionGatewayMaster WHERE chargeversionId=(SELECT MAX(chargeversionId) FROM ChargeVersionGatewayMaster WHERE gateway_accounts_charges_mapping_id=?)";
            pstmt = conn.prepareStatement(qry);
            pstmt.setString(1, mappingId);
            rs = pstmt.executeQuery();
            int i = 0;
            if (rs.next())
            {
                rsDetails.put("lastupdateDate", rs.getString("effectiveEndDate"));
                rsDetails.put("effectiveStartDate", rs.getString("effectiveStartDate"));
            }

        }
        catch (SQLException e)
        {
            logger.error("Leaving ChargesDAO throwing SQL Exception as System Error :::: ", e);
            PZExceptionHandler.raiseDBViolationException("ChargesDAO", "getGatewayAccountChargeDate()", null, "Common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("Leaving ChargesDAO throwing SQL Exception as System Error :::: ", se);
            PZExceptionHandler.raiseDBViolationException("ChargesDAO", "getGatewayAccountChargeDate()", null, "Common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return rsDetails;
    }

    public String insertMemberAccountCharges(ChargeVO chargeVO) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String status = "";
        try
        {
            con = Database.getConnection();
            String query = "insert into member_accounts_charges_mapping (memberid,accountid,paymodeid,cardtypeid,chargeid,chargevalue,valuetype, keyword, sequencenum, agentchargevalue, partnerchargevalue, subkeyword, frequency, category,terminalid,isinput_required,actionExecutorId,actionExecutorName,negativebalance) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, chargeVO.getMemberid());
            pstmt.setString(2, chargeVO.getAccountId());
            pstmt.setString(3, chargeVO.getPaymode());
            pstmt.setString(4, chargeVO.getCardType());
            pstmt.setString(5, chargeVO.getChargeid());
            pstmt.setString(6, chargeVO.getMchargevalue());
            pstmt.setString(7, chargeVO.getChargetype());
            pstmt.setString(8, chargeVO.getKeyword());
            pstmt.setString(9, chargeVO.getSequencenum());
            pstmt.setString(10, chargeVO.getAchargevalue());
            pstmt.setString(11, chargeVO.getPchargevalue());
            pstmt.setString(12, chargeVO.getSubKey());
            pstmt.setString(13, chargeVO.getFrequency());
            pstmt.setString(14, chargeVO.getCategory());
            pstmt.setString(15, chargeVO.getTerminalid());
            pstmt.setString(16, chargeVO.getIsInputRequired());
            pstmt.setString(17, chargeVO.getActionExecutorId());
            pstmt.setString(18, chargeVO.getActionExecutorName());
            pstmt.setString(19, chargeVO.getNegativebalance());
            int num = pstmt.executeUpdate();
            int memberAccountChargeMappingID = 0;
            if (num == 1)
            {
                rs = pstmt.getGeneratedKeys();
                if (rs != null)
                {
                    while (rs.next())
                    {
                        memberAccountChargeMappingID = rs.getInt(1);
                    }
                }
                if (memberAccountChargeMappingID != 0)
                {
                    String qry = "insert into ChargeVersionMemberMaster (merchantChargeValue,agentCommision,partnerCommision,effectiveStartDate,effectiveEndDate,member_accounts_charges_mapping_id) VALUES (?,?,?,?,?,?)";
                    pstmt = con.prepareStatement(qry);
                    pstmt.setString(1, chargeVO.getMchargevalue());
                    pstmt.setString(2, chargeVO.getAchargevalue());
                    pstmt.setString(3, chargeVO.getPchargevalue());
                    pstmt.setString(4, chargeVO.getStartdate());
                    pstmt.setString(5, chargeVO.getEnddate());
                    pstmt.setInt(6, memberAccountChargeMappingID);
                   // pstmt.setString(7,chargeVO.getActionExecutorId());
                    //pstmt.setString(8,chargeVO.getActionExecutorName());
                    pstmt.executeUpdate();
                }
                logger.debug("New Wire added successfully.");
                status = "success";
            }
            else
            {
                logger.debug("Adding Wire Failure Check The JDBC Code For Generate Wire");
                status = "failure";
            }
        }
        catch (SQLException e)
        {
            logger.error("Leaving ChargesDAO throwing SQL Exception as System Error :::: ", e);
            PZExceptionHandler.raiseDBViolationException("ChargesDAO", "insertMemberAccountCharges()", null, "Common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("Leaving ChargesDAO throwing SQL Exception as System Error :::: ", se);
            PZExceptionHandler.raiseDBViolationException("ChargesDAO", "insertMemberAccountCharges()", null, "Common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return status;
    }

    public Hashtable getMemberAccountCharges(String memberId, String accountId, String payModeId, String cardTypeId, String chargeId, String mChargeValue, String aChargeValue, String pChargeValue, String chargeType, String terminalid, String gateway, String currency, int start, int end,String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        Hashtable hash = null;
        Functions functions = new Functions();
        try
        {
            conn = Database.getRDBConnection();
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            StringBuffer query = new StringBuffer("SELECT mac.mappingid,mac.memberid,mac.terminalid,mac.accountid,mac.paymodeid,mac.cardtypeid,mac.chargeid,mac.chargevalue,mac.valuetype,mac.sequencenum,mac.keyword,mac.agentchargevalue,mac.partnerchargevalue, mac.subkeyword,mac.frequency,mac.category,cm.chargename,mac.actionExecutorId,mac.actionExecutorName,mac.negativebalance FROM member_accounts_charges_mapping AS mac JOIN charge_master AS cm ON mac.chargeid=cm.chargeid WHERE mappingid>0 ");
            StringBuffer countquery = new StringBuffer("select count(*) FROM member_accounts_charges_mapping AS mac JOIN charge_master AS cm ON mac.chargeid=cm.chargeid WHERE mappingid>0 ");
            if (functions.isValueNull(memberId))
            {
                query.append(" and mac.memberid='" + ESAPI.encoder().encodeForSQL(me, memberId) + "'");
                countquery.append(" and mac.memberid='" + ESAPI.encoder().encodeForSQL(me, memberId) + "'");
            }
            if (functions.isValueNull(accountId))
            {
                query.append(" and mac.accountid in (" + accountId + ")");
                countquery.append(" and mac.accountid in (" + accountId + ")");
            }
            if (functions.isValueNull(chargeId))
            {
                query.append(" and mac.chargeid='" + ESAPI.encoder().encodeForSQL(me, chargeId) + "'");
                countquery.append(" and mac.chargeid='" + ESAPI.encoder().encodeForSQL(me, chargeId) + "'");
            }
            if (functions.isValueNull(chargeType))
            {
                query.append(" and mac.valuetype='" + ESAPI.encoder().encodeForSQL(me, chargeType) + "'");
                countquery.append(" and mac.valuetype='" + ESAPI.encoder().encodeForSQL(me, chargeType) + "'");
            }
            if (functions.isValueNull(terminalid))
            {
                query.append(" and mac.terminalid='" + ESAPI.encoder().encodeForSQL(me, terminalid) + "'");
                countquery.append(" and mac.terminalid='" + ESAPI.encoder().encodeForSQL(me, terminalid) + "'");
            }
            query.append(" order by mac.mappingid desc LIMIT " + start + "," + end);

            logger.debug("Query:-" + query);
            logger.debug("CountQuery:-" + countquery);
            hash = Database.getHashFromResultSetForTransactionEntryNew(Database.executeQuery(query.toString(), conn));
            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getMemberAccountCharges", null, "Common", "SQLException while connecting to member_account_charges_mapping common  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getMemberAccountCharges()", null, "Common", "SystemError while connecting to member_account_charges_mapping  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }
    public Hashtable getMemberAccountChargesForExport(String memberId, String accountId, String chargeId, String chargeType, String terminalid) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        Hashtable hash = null;
        Functions functions = new Functions();
        try
        {
            conn = Database.getRDBConnection();
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            StringBuffer query = new StringBuffer("SELECT mac.mappingid,mac.memberid,mac.terminalid,mac.accountid,mac.paymodeid,mac.cardtypeid,mac.chargeid,mac.chargevalue,mac.valuetype,mac.sequencenum,mac.keyword,mac.agentchargevalue,mac.partnerchargevalue,mac.isinput_required, mac.subkeyword,mac.frequency,mac.category,cm.chargename,mac.actionExecutorId,mac.actionExecutorName,mac.negativebalance FROM member_accounts_charges_mapping AS mac JOIN charge_master AS cm ON mac.chargeid=cm.chargeid WHERE mappingid>0 ");
            StringBuffer countquery = new StringBuffer("select count(*) FROM member_accounts_charges_mapping AS mac JOIN charge_master AS cm ON mac.chargeid=cm.chargeid WHERE mappingid>0 ");
            if (functions.isValueNull(memberId))
            {
                query.append(" and mac.memberid='" + ESAPI.encoder().encodeForSQL(me, memberId) + "'");
                countquery.append(" and mac.memberid='" + ESAPI.encoder().encodeForSQL(me, memberId) + "'");
            }
            if (functions.isValueNull(accountId))
            {
                query.append(" and mac.accountid in (" + accountId + ")");
                countquery.append(" and mac.accountid in (" + accountId + ")");
            }
            if (functions.isValueNull(chargeId))
            {
                query.append(" and mac.chargeid='" + ESAPI.encoder().encodeForSQL(me, chargeId) + "'");
                countquery.append(" and mac.chargeid='" + ESAPI.encoder().encodeForSQL(me, chargeId) + "'");
            }
            if (functions.isValueNull(chargeType))
            {
                query.append(" and mac.valuetype='" + ESAPI.encoder().encodeForSQL(me, chargeType) + "'");
                countquery.append(" and mac.valuetype='" + ESAPI.encoder().encodeForSQL(me, chargeType) + "'");
            }
            if (functions.isValueNull(terminalid))
            {
                query.append(" and mac.terminalid='" + ESAPI.encoder().encodeForSQL(me, terminalid) + "'");
                countquery.append(" and mac.terminalid='" + ESAPI.encoder().encodeForSQL(me, terminalid) + "'");
            }
            query.append(" order by mac.mappingid desc ");

            logger.debug("Query:-" + query);
            logger.debug("CountQuery:-" + countquery);
            hash = Database.getHashFromResultSetForTransactionEntryNew(Database.executeQuery(query.toString(), conn));
            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getMemberAccountCharges", null, "Common", "SQLException while connecting to member_account_charges_mapping common  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getMemberAccountCharges()", null, "Common", "SystemError while connecting to member_account_charges_mapping  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public ChargeVO getMerchantChargeDetails(String mappingId) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ChargeVO chargeVO = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT terminalid,memberid,accountid,paymodeid,cardtypeid FROM member_accounts_charges_mapping WHERE mappingid=?");
            pstmt = con.prepareStatement(query.toString());
            pstmt.setString(1, mappingId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                chargeVO = new ChargeVO();
                chargeVO.setTerminalid(rs.getString("terminalid"));
                chargeVO.setMemberid(rs.getString("memberid"));
                chargeVO.setAccountId(rs.getString("accountid"));
                chargeVO.setPaymode(rs.getString("paymodeid"));
                chargeVO.setCardType(rs.getString("cardtypeid"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getMerchantChargeDetails()", null, "Common", "SqlException while connecting to member_accounts_charges_mapping table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getMerchantChargeDetails()", null, "Common", "SystemError while connecting to member_accounts_charges_mapping table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return chargeVO;
    }

    public boolean updateMerchantCharge(ChargeVO chargeVO) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        int k = 0;
        boolean status = false;
        try
        {
            con = Database.getConnection();
            StringBuffer query = new StringBuffer("update ChargeVersionMemberMaster set merchantChargeValue=?,agentCommision=?,partnerCommision=?,effectiveStartDate=?,effectiveEndDate=? where member_accounts_charges_mapping_id=?");
            pstmt = con.prepareStatement(query.toString());
            pstmt.setString(1, chargeVO.getChargevalue());
            pstmt.setString(2, chargeVO.getAgentChargeValue());
            pstmt.setString(3, chargeVO.getPartnerChargeValue());
            pstmt.setString(4, chargeVO.getStartdate());
            pstmt.setString(5, chargeVO.getEnddate());
            pstmt.setString(6, chargeVO.getMappingid());
            k = pstmt.executeUpdate();
            if (k > 0)
            {
                status = true;
            }
            StringBuffer query2 = new StringBuffer("update member_accounts_charges_mapping set chargevalue=?,agentchargevalue=?,partnerchargevalue =? where mappingid=?");
            pstmt = con.prepareStatement(query2.toString());
            pstmt.setString(1, chargeVO.getChargevalue());
            pstmt.setString(2, chargeVO.getAgentChargeValue());
            pstmt.setString(3, chargeVO.getPartnerChargeValue());
            pstmt.setString(4, chargeVO.getMappingid());
            k = pstmt.executeUpdate();
            if (k > 0)
            {
                status = true;
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "updateMerchantCharge()", null, "Common", "SqlException while connecting to member_accounts_charges_mapping table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while while updateBusinessCharge charge::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayAccountCharges()", null, "Common", "SqlException while connecting to member_accounts_charges_mapping table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return status;
    }

    public List<ChargeVO> getDynamicChargesAsPerTerminal(TerminalVO terminalVO) throws PZDBViolationException
    {
        Connection con = null;
        ResultSet rsCharges = null;
        PreparedStatement psCharges=null;
        List<ChargeVO> chargeVOList = new ArrayList();
        String strQuery = "select M.mappingid,M.memberid,M.chargeid,M.chargevalue,M.valuetype,C.chargename,C.isinputrequired,M.keyword,M.sequencenum,M.frequency,M.category,M.subkeyword,M.terminalid,M.paymodeid,M.cardtypeid,M.accountid from member_accounts_charges_mapping as M, charge_master as C  where M.chargeid=C.chargeid and M.memberid=? and M.accountid=? and terminalid=? and isinput_required='Y'  ORDER BY sequencenum";
        try
        {
            con = Database.getConnection();
            psCharges = con.prepareStatement(strQuery);
            psCharges.setString(1, terminalVO.getMemberId());
            psCharges.setString(2, terminalVO.getAccountId());
            psCharges.setString(3, terminalVO.getTerminalId());
            rsCharges = psCharges.executeQuery();
            while (rsCharges.next())
            {
                ChargeVO chargeVO = new ChargeVO();
                chargeVO.setMappingid(rsCharges.getString(1));
                chargeVO.setMemberid(rsCharges.getString(2));
                chargeVO.setChargeid(rsCharges.getString(3));
                chargeVO.setChargevalue(rsCharges.getString(4));
                chargeVO.setValuetype(rsCharges.getString(5));
                chargeVO.setChargename(rsCharges.getString(6));
                chargeVO.setIsinputrequired(rsCharges.getString(7));
                chargeVO.setKeyword(rsCharges.getString(8));
                chargeVO.setSequencenum(rsCharges.getString(9));
                chargeVO.setFrequency(rsCharges.getString(10));
                chargeVO.setCategory(rsCharges.getString(11));
                chargeVO.setSubkeyword(rsCharges.getString(12));
                chargeVO.setTerminalid(rsCharges.getString(13));
                chargeVO.setPaymentName(GatewayAccountService.getPaymentMode(rsCharges.getString(14)));
                chargeVO.setCardType(GatewayAccountService.getCardType(rsCharges.getString(15)));
                chargeVO.setAccountId(rsCharges.getString(16));
                chargeVOList.add(chargeVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getDynamicChargesAsPerTerminal()", null, "Common", "SqlException while connecting to member_accounts_charges_mapping table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError::::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getDynamicChargesAsPerTerminal()", null, "Common", "SqlException while connecting to member_accounts_charges_mapping table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rsCharges);
            Database.closePreparedStatement(psCharges);
            Database.closeConnection(con);
        }
        return chargeVOList;
    }

    public ChargeVO getChargeDetails(String mappingId) throws PZDBViolationException
    {
        Connection con = null;
        ResultSet rsCharges = null;
        PreparedStatement psCharges=null;
        ChargeVO chargeVO = null;
        String strQuery = "select M.mappingid,M.memberid,M.chargeid,M.chargevalue,M.valuetype,C.chargename,M.isinput_required,M.keyword,M.sequencenum,M.frequency,M.category,M.subkeyword,M.terminalid,M.paymodeid,M.cardtypeid,M.accountid from member_accounts_charges_mapping as M, charge_master as C  where M.chargeid=C.chargeid and M.mappingid=?";
        try
        {
            con = Database.getRDBConnection();
            psCharges = con.prepareStatement(strQuery);
            psCharges.setString(1, mappingId);
            rsCharges = psCharges.executeQuery();
            if (rsCharges.next())
            {
                chargeVO = new ChargeVO();
                chargeVO.setMappingid(rsCharges.getString(1));
                chargeVO.setMemberid(rsCharges.getString(2));
                chargeVO.setChargeid(rsCharges.getString(3));
                chargeVO.setChargevalue(rsCharges.getString(4));
                chargeVO.setValuetype(rsCharges.getString(5));
                chargeVO.setChargename(rsCharges.getString(6));
                chargeVO.setIsinputrequired(rsCharges.getString(7));
                chargeVO.setKeyword(rsCharges.getString(8));
                chargeVO.setSequencenum(rsCharges.getString(9));
                chargeVO.setFrequency(rsCharges.getString(10));
                chargeVO.setCategory(rsCharges.getString(11));
                chargeVO.setSubkeyword(rsCharges.getString(12));
                chargeVO.setTerminalid(rsCharges.getString(13));
                chargeVO.setPaymentName(GatewayAccountService.getPaymentMode(rsCharges.getString(14)));
                chargeVO.setCardType(GatewayAccountService.getCardType(rsCharges.getString(15)));
                chargeVO.setAccountId(rsCharges.getString(16));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getChargeDetails()", null, "Common", "SqlException while connecting to member_accounts_charges_mapping table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError::::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getChargeDetails()", null, "Common", "SqlException while connecting to member_accounts_charges_mapping table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rsCharges);
            Database.closePreparedStatement(psCharges);
            Database.closeConnection(con);
        }
        return chargeVO;
    }

    public HashMap getChargeInfo(String mappingId) throws PZDBViolationException
    {
        PreparedStatement pstmt = null;
        ResultSet rs2=null;
        String qry = "";
        Connection conn = null;
        HashMap rsDetails = new HashMap();
        try
        {
            conn = Database.getRDBConnection();
            qry = "SELECT cm.chargename,mc.memberid,mc.accountid,mc.terminalid,mc.chargevalue,mc.agentchargevalue,mc.partnerchargevalue,mc.sequencenum,mc.isinput_required FROM member_accounts_charges_mapping AS mc JOIN charge_master AS cm ON mc.chargeid=cm.chargeid WHERE mc.mappingid=?";
            pstmt = conn.prepareStatement(qry);
            pstmt.setString(1, mappingId);
            rs2 = pstmt.executeQuery();
            if (rs2.next())
            {
                rsDetails.put("memberid", rs2.getString("memberid"));
                rsDetails.put("accountid", rs2.getString("accountid"));
                rsDetails.put("terminalid", rs2.getString("terminalid"));
                rsDetails.put("chargevalue", rs2.getString("chargevalue"));//merchat rate
                rsDetails.put("agentchargevalue", rs2.getString("agentchargevalue"));
                rsDetails.put("partnerchargevalue", rs2.getString("partnerchargevalue"));
                rsDetails.put("sequencenum", rs2.getString("sequencenum"));
                rsDetails.put("isinput_required", rs2.getString("isinput_required"));
                rsDetails.put("chargename", rs2.getString("chargename"));
            }
            qry = "SELECT effectiveStartDate,effectiveEndDate FROM ChargeVersionMemberMaster WHERE chargeversionId=(SELECT MAX(chargeversionId) FROM ChargeVersionMemberMaster WHERE member_accounts_charges_mapping_id=?)";
            pstmt = conn.prepareStatement(qry);
            pstmt.setString(1, mappingId);
            rs2 = pstmt.executeQuery();
            int i = 0;
            if (rs2.next())
            {

                rsDetails.put("lastupdateDate", rs2.getString("effectiveEndDate"));
                rsDetails.put("effectiveStartDate", rs2.getString("effectiveStartDate"));
            }
            rsDetails.put("mappingid", mappingId);

            try
            {
                ChargeManager chargeManager = new ChargeManager();
                WiresManager wiresManager = new WiresManager();
                MerchantWireVO merchantWireVO = null;
                TerminalVO terminalVO = null;

                ChargeVO chargeVO = chargeManager.getMerchantChargeDetails(mappingId);
                if (chargeVO != null)
                {
                    terminalVO = new TerminalVO();
                    terminalVO.setMemberId(chargeVO.getMemberid());
                    terminalVO.setTerminalId(chargeVO.getTerminalid());
                    terminalVO.setAccountId(chargeVO.getAccountId());
                    merchantWireVO = wiresManager.getMerchantRecentWire(terminalVO);

                }
                if (merchantWireVO == null)
                {
                    rsDetails.put("version", "false");
                }
                else
                {
                    String endDate = merchantWireVO.getSettlementEndDate();
                    CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
                    String errorMsg = commonFunctionUtil.newValidateDateWithNewFormat(endDate, rs2.getString("effectiveEndDate"), null, null);
                    Functions functions = new Functions();
                    if (functions.isValueNull(errorMsg))
                    {
                        rsDetails.put("lastupdateDate", endDate);
                    }
                }
            }
            catch (PZDBViolationException e)
            {
                logger.error("PZDBViolationException::::::" + e);

            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getChargeInfo()", null, "Common", "SqlException while connecting to member_accounts_charges_mapping table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError::::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getChargeInfo()", null, "Common", "SqlException while connecting to member_accounts_charges_mapping table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs2);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return rsDetails;
    }

    public List<ChargeVO> getListOfTerminalCharges(String memberId,String terminalId) throws PZDBViolationException
    {
        Connection con = null;
        ResultSet rsCharges = null;
        PreparedStatement psCharges=null;
        List<ChargeVO> chargeVOList = new ArrayList();
        List<String> mappingId=new ArrayList<>();
        String strQuery = "SELECT macm.mappingid,macm.chargeid,macm.chargevalue,macm.valuetype,macm.keyword,macm.sequencenum," +
                "macm.agentchargevalue,macm.partnerchargevalue,cm.chargename,macm.isinput_required,cvmm.effectiveStartDate," +
                "cvmm.effectiveEndDate,macm.negativebalance FROM member_accounts_charges_mapping AS macm,charge_master AS cm,ChargeVersionMemberMaster AS cvmm  WHERE cm.chargeid=macm.chargeid AND macm.mappingid=cvmm.member_accounts_charges_mapping_id AND macm.memberid=? AND macm.terminalid=? ORDER BY sequencenum,chargeversionId DESC";
        try
        {
            con = Database.getConnection();
            psCharges = con.prepareStatement(strQuery);
            psCharges.setString(1, memberId);
            psCharges.setString(2, terminalId);
            rsCharges = psCharges.executeQuery();
            while (rsCharges.next())
            {
                if(!mappingId.contains(rsCharges.getString(1)))
                {
                    mappingId.add(rsCharges.getString(1));
                    ChargeVO chargeVO = new ChargeVO();
                    chargeVO.setMappingid(rsCharges.getString(1));
                    chargeVO.setChargeid(rsCharges.getString(2));
                    chargeVO.setChargevalue(rsCharges.getString(3));
                    chargeVO.setValuetype(rsCharges.getString(4));
                    chargeVO.setKeyword(rsCharges.getString(5));
                    chargeVO.setSequencenum(rsCharges.getString(6));
                    chargeVO.setAgentChargeValue(rsCharges.getString(7));
                    chargeVO.setPartnerChargeValue(rsCharges.getString(8));
                    chargeVO.setChargename(rsCharges.getString(9));
                    chargeVO.setIsInputRequired(rsCharges.getString(10));
                    chargeVO.setStartdate(rsCharges.getString(11));
                    chargeVO.setEnddate(rsCharges.getString(12));
                    chargeVO.setNegativebalance(rsCharges.getString(13));
                    chargeVOList.add(chargeVO);
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getDynamicChargesAsPerTerminal()", null, "Common", "SqlException while connecting to member_accounts_charges_mapping table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError::::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getDynamicChargesAsPerTerminal()", null, "Common", "SqlException while connecting to member_accounts_charges_mapping table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rsCharges);
            Database.closePreparedStatement(psCharges);
            Database.closeConnection(con);
        }
        return chargeVOList;
    }

    public boolean Negativebalanceshow(String chargeid) throws PZDBViolationException
    {
        Connection con = null;
        ResultSet rsCharges = null;
        PreparedStatement psCharges=null;
        ChargeVO chargeVO = new ChargeVO();
        boolean Negativebalanceshow = false;

        String strQuery = "SELECT keyword,category,subkeyword FROM member_accounts_charges_mapping  WHERE chargeid=?";
        try
        {
            con = Database.getConnection();
            psCharges = con.prepareStatement(strQuery);
            psCharges.setString(1, chargeid);
            rsCharges = psCharges.executeQuery();
            while (rsCharges.next())
            {
                  chargeVO.setKeyword(rsCharges.getString(1));
                  chargeVO.setCategory(rsCharges.getString(2));
                  chargeVO.setSubkeyword(rsCharges.getString(3));
                if (chargeVO.getCategory().equals(Charge_category.Success.toString()) && chargeVO.getKeyword().equals(Charge_keyword.NetFinalAmount.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
                {
                    Negativebalanceshow=true;
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getDynamicChargesAsPerTerminal()", null, "Common", "SqlException while connecting to member_accounts_charges_mapping table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError::::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getDynamicChargesAsPerTerminal()", null, "Common", "SqlException while connecting to member_accounts_charges_mapping table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rsCharges);
            Database.closePreparedStatement(psCharges);
            Database.closeConnection(con);
        }
        return Negativebalanceshow;
    }


    public boolean checkAgentSequenceNoAvailability(AgentCommissionVO agentCommissionVO)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean checkAvailability = true;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT * FROM agent_commission WHERE memberid=? and agentid=? and sequence_no=? and terminalid=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, agentCommissionVO.getMemberId());
            pstmt.setString(2, agentCommissionVO.getAgentId());
            pstmt.setString(3, agentCommissionVO.getSequenceNo());
            pstmt.setString(4, agentCommissionVO.getTerminalId());
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                checkAvailability = false;
            }
        }
        catch (Exception e)
        {
            logger.error("Exception::::::::" + e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return checkAvailability;
    }

    public List<AgentCommissionVO> getListOfTerminalChargesAgent(String memberId,String agentid,String terminalId) throws PZDBViolationException
    {
        Connection con = null;
        ResultSet rsCharges = null;
        PreparedStatement psCharges=null;
        List<AgentCommissionVO> commissionVOList = new ArrayList();
        List<String> mappingId=new ArrayList<>();
        String strQuery = "SELECT ac.id,ac.chargeid,ac.commission_value,ac.sequence_no,cm.chargename,ac.startdate,ac.enddate FROM agent_commission AS ac,charge_master AS cm WHERE cm.chargeid=ac.chargeid AND ac.memberid=? AND ac.agentid=? AND ac.terminalid=? ORDER BY sequence_no DESC";
        try
        {
            con = Database.getConnection();
            psCharges = con.prepareStatement(strQuery);
            psCharges.setString(1, memberId);
            psCharges.setString(2, agentid);
            psCharges.setString(3, terminalId);
            rsCharges = psCharges.executeQuery();
            while (rsCharges.next())
            {
                if(!mappingId.contains(rsCharges.getString(1)))
                {
                    mappingId.add(rsCharges.getString(1));
                    AgentCommissionVO agentCommissionVO = new AgentCommissionVO();
                    agentCommissionVO.setId(rsCharges.getString(1));
                    agentCommissionVO.setChargeId(rsCharges.getString(2));
                    agentCommissionVO.setChargeValue(rsCharges.getString(3));
                    agentCommissionVO.setSequenceNo(rsCharges.getString(4));
                    agentCommissionVO.setChargeName(rsCharges.getString(5));
                    agentCommissionVO.setStartDate(rsCharges.getString(6));
                    agentCommissionVO.setEndDate(rsCharges.getString(7));
                    commissionVOList.add(agentCommissionVO);
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getListOfTerminalChargesAgent()", null, "Common", "SqlException while connecting to member_accounts_charges_mapping table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError::::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getListOfTerminalChargesAgent()", null, "Common", "SqlException while connecting to member_accounts_charges_mapping table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rsCharges);
            Database.closePreparedStatement(psCharges);
            Database.closeConnection(con);
        }
        return commissionVOList;
    }

    public String insertNewAgentCommission(AgentCommissionVO commissionVO) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String status = "";
        try
        {
            con = Database.getConnection();
            String query = "insert into agent_commission (agentid,memberid,chargeid,commission_value, sequence_no,terminalid,startdate,enddate,actionExecutorId,actionExecutorName) VALUES(?,?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, commissionVO.getAgentId());
            pstmt.setString(2, commissionVO.getMemberId());
            pstmt.setString(3, commissionVO.getChargeId());
            pstmt.setString(4, commissionVO.getChargeValue());
            pstmt.setString(5, commissionVO.getSequenceNo());
            pstmt.setString(6, commissionVO.getTerminalId());
            pstmt.setString(7, commissionVO.getStartDate());
            pstmt.setString(8, commissionVO.getEndDate());
            pstmt.setString(9, commissionVO.getActionExecutorId());
            pstmt.setString(10, commissionVO.getActionExecutorName());
            int num = pstmt.executeUpdate();
            if (num > 0)
            {
                status = "success";
            }
            else
            {
                logger.debug("Commission mapping failed");
                status = "failure";
            }
        }
        catch (SQLException e)
        {
            logger.error("Leaving ChargesDAO throwing SQL Exception as System Error :::: ", e);
            PZExceptionHandler.raiseDBViolationException("ChargesDAO", "insertNewAgentCommission()", null, "Common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("Leaving ChargesDAO throwing SQL Exception as System Error :::: ", se);
            PZExceptionHandler.raiseDBViolationException("ChargesDAO", "insertNewAgentCommission()", null, "Common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return status;
    }

    public Boolean getMerchantChargeNegativeBalance(String memberid, String accountid, String terminalid, String chargeid)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        boolean chargeVersionRate = false;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT negativebalance FROM member_accounts_charges_mapping WHERE memberid=? AND accountid=? AND terminalid=? AND chargeid=?";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, memberid);
            preparedStatement.setString(2, accountid);
            preparedStatement.setString(3, terminalid);
            preparedStatement.setString(4, chargeid);
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                String value = rs.getString("negativebalance");
                if(value.equals("Y")){
                    chargeVersionRate = true;
                }
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError ::::::: Leaving transactionEntry ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return chargeVersionRate;
    }
}