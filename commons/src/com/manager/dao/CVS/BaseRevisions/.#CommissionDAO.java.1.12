package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayType;
import com.manager.vo.CommissionVO;
import com.manager.vo.ISOCommReportVO;
import com.manager.vo.WLPartnerCommissionVO;
import com.manager.vo.WLPartnerInvoiceVO;
import com.manager.vo.payoutVOs.ChargeMasterVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 8/8/16
 * Time: 3:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommissionDAO
{
    Logger logger = new Logger(PayoutDAO.class.getName());
    public List<CommissionVO> getGatewayAccountCommission(String accountId)throws PZDBViolationException
    {
        List<CommissionVO> commissionVOsList=new LinkedList();
        CommissionVO commissionVO=null;
        ChargeMasterVO chargeMasterVO=null;
        Connection con=null;
        ResultSet rs=null;
        PreparedStatement pstmt=null;
        String strQuery = "SELECT M.mappingid,M.chargeid,M.chargevalue,C.valuetype,C.chargename,M.isinputrequired,C.keyword,M.sequencenum,C.frequency,C.category,C.subkeyword FROM gateway_accounts_charges_mapping AS M, charge_master AS C  WHERE M.chargeid=C.chargeid AND M.accountid=? ORDER BY M.sequencenum";
        try
        {
            con= Database.getRDBConnection();
            pstmt= con.prepareStatement(strQuery);
            pstmt.setString(1, accountId);
            rs = pstmt.executeQuery();
            while(rs.next())
            {
                chargeMasterVO=new ChargeMasterVO();
                commissionVO=new CommissionVO();

                commissionVO.setCommissionId(rs.getString("mappingid"));
                commissionVO.setChargeId(rs.getString("chargeid"));
                commissionVO.setCommissionValue(rs.getDouble("chargevalue"));
                commissionVO.setIsInputRequired(rs.getString("isinputrequired"));

                chargeMasterVO.setValueType(rs.getString("valuetype"));
                chargeMasterVO.setChargeName(rs.getString("chargename"));
                chargeMasterVO.setKeyword(rs.getString("keyword"));
                chargeMasterVO.setSequenceNumber(rs.getString("sequencenum"));
                chargeMasterVO.setFrequency(rs.getString("frequency"));
                chargeMasterVO.setCategory(rs.getString("category"));
                chargeMasterVO.setSubKeyword(rs.getString("subkeyword"));

                commissionVO.setChargeMasterVO(chargeMasterVO);
                commissionVOsList.add(commissionVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting commission list",e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayAccountCommission", null, "Common", "Sql exception while connecting to gateway account commission  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting commission list::",systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayAccountCommission()", null, "Common", "Sql exception while connecting to gateway account commission table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return commissionVOsList;
    }
    public List<CommissionVO> getGatewayAccountDynamicInputCommissions(String accountId)throws PZDBViolationException
    {
        List<CommissionVO> commissionVOsList=new LinkedList();
        CommissionVO commissionVO=null;
        ChargeMasterVO chargeMasterVO=null;
        Connection con=null;
        ResultSet rs=null;
        PreparedStatement pstmt=null;
        String strQuery = "SELECT M.mappingid,M.chargeid,M.chargevalue,C.valuetype,C.chargename,M.isinputrequired,C.keyword,M.sequencenum,C.frequency,C.category,C.subkeyword FROM gateway_accounts_charges_mapping AS M, charge_master AS C  WHERE M.isinputrequired='Y' and M.chargeid=C.chargeid AND M.accountid=? ORDER BY M.sequencenum";
        try
        {
            con= Database.getRDBConnection();
            pstmt= con.prepareStatement(strQuery);
            pstmt.setString(1, accountId);
            rs = pstmt.executeQuery();
            while(rs.next())
            {
                chargeMasterVO=new ChargeMasterVO();
                commissionVO=new CommissionVO();

                commissionVO.setCommissionId(rs.getString("mappingid"));
                commissionVO.setChargeId(rs.getString("chargeid"));
                commissionVO.setCommissionValue(rs.getDouble("chargevalue"));
                commissionVO.setIsInputRequired(rs.getString("isinputrequired"));

                chargeMasterVO.setValueType(rs.getString("valuetype"));
                chargeMasterVO.setChargeName(rs.getString("chargename"));
                chargeMasterVO.setKeyword(rs.getString("keyword"));
                chargeMasterVO.setSequenceNumber(rs.getString("sequencenum"));
                chargeMasterVO.setFrequency(rs.getString("frequency"));
                chargeMasterVO.setCategory(rs.getString("category"));
                chargeMasterVO.setSubKeyword(rs.getString("subkeyword"));

                commissionVO.setChargeMasterVO(chargeMasterVO);
                commissionVOsList.add(commissionVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting commission list",e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayAccountDynamicInputCommissions", null, "Common", "Sql exception while connecting to gateway account commission  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting commission list::",systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayAccountDynamicInputCommissions()", null, "Common", "Sql exception while connecting to gateway account commission table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return commissionVOsList;
    }
    public String getGatewayAccountSetupCoveredDate(GatewayAccount gatewayAccount)throws PZDBViolationException
    {
        String lastSetupFeeDate = null;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            String Query ="SELECT enddate as lastsetupfeedate FROM iso_commission_wire_manager WHERE accountid=? ORDER BY iso_comm_id DESC LIMIT 1";
            preparedStatement =con.prepareStatement(Query);
            preparedStatement.setInt(1, gatewayAccount.getAccountId());
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                lastSetupFeeDate = rsPayout.getString("lastsetupfeedate");
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting commission list",e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayAccountSetupCoveredDate", null, "Common", "Sql exception while connecting to iso wire manager  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting commission list::",systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayAccountSetupCoveredDate()", null, "Common", "Sql exception while connecting to iso wire manager table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return lastSetupFeeDate;
    }
    public ISOCommReportVO getISOCommWireReport(String isoCommId)throws SystemError,SQLException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        ResultSet rs=null;
        ISOCommReportVO isoCommReportVO=null;
        try
        {
            con= Database.getRDBConnection();
            StringBuffer sbQuery=new StringBuffer("select * from iso_commission_wire_manager where iso_comm_id=?");
            pstmt=con.prepareStatement(sbQuery.toString());
            pstmt.setString(1,isoCommId);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                isoCommReportVO=new ISOCommReportVO();
                isoCommReportVO.setIsoWireId(rs.getString("iso_comm_id"));
                isoCommReportVO.setAccountId(rs.getString("accountid"));
                isoCommReportVO.setCurrency(rs.getString("currency"));
                isoCommReportVO.setStartDate(rs.getString("startdate"));
                isoCommReportVO.setEndDate(rs.getString("enddate"));
                isoCommReportVO.setSettledDate(rs.getString("settleddate"));
                isoCommReportVO.setAmount(rs.getDouble("amount"));
                isoCommReportVO.setNetfinalamount(rs.getDouble("netfinalamount"));
                isoCommReportVO.setUnpaidAmount(rs.getDouble("unpaidamount"));
                isoCommReportVO.setStatus(rs.getString("status"));
                isoCommReportVO.setReportFilePath(rs.getString("reportfilepath"));
                isoCommReportVO.setTransactionFilePath(rs.getString("transactionfilepath"));
                isoCommReportVO.setActionExecutor(rs.getString("actionexecutor"));
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return isoCommReportVO;
    }
    public ISOCommReportVO getLastISOCommWireReport(GatewayAccount gatewayAccount)throws SystemError,SQLException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        ResultSet rs=null;
        ISOCommReportVO isoCommReportVO=null;
        try
        {
            con= Database.getRDBConnection();
            StringBuffer sbQuery=new StringBuffer("SELECT * FROM iso_commission_wire_manager WHERE accountid=? ORDER BY iso_comm_id DESC LIMIT 1");
            pstmt=con.prepareStatement(sbQuery.toString());
            pstmt.setInt(1, gatewayAccount.getAccountId());
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                isoCommReportVO=new ISOCommReportVO();
                isoCommReportVO.setIsoWireId(rs.getString("iso_comm_id"));
                isoCommReportVO.setAccountId(rs.getString("accountid"));
                isoCommReportVO.setCurrency(rs.getString("currency"));
                isoCommReportVO.setStartDate(rs.getString("startdate"));
                isoCommReportVO.setEndDate(rs.getString("enddate"));
                isoCommReportVO.setSettledDate(rs.getString("settleddate"));
                isoCommReportVO.setAmount(rs.getDouble("amount"));
                isoCommReportVO.setNetfinalamount(rs.getDouble("netfinalamount"));
                isoCommReportVO.setUnpaidAmount(rs.getDouble("unpaidamount"));
                isoCommReportVO.setStatus(rs.getString("status"));
                isoCommReportVO.setReportFilePath(rs.getString("reportfilepath"));
                isoCommReportVO.setTransactionFilePath(rs.getString("transactionfilepath"));
                isoCommReportVO.setActionExecutor(rs.getString("actionexecutor"));
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return isoCommReportVO;
    }
    public String getGatewayAccountChargeVersionRate(String gatewayAccountChargeMappingId,String reportingDate)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        String chargeVersionRate=null;
        try
        {
            con= Database.getRDBConnection();
            String Query = "SELECT merchantChargeValue FROM ChargeVersionMemberMaster WHERE member_accounts_charges_mapping_id=? AND (?>=effectiveStartDate) AND (?<=effectiveEndDate)";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,gatewayAccountChargeMappingId);
            preparedStatement.setString(2,reportingDate);
            preparedStatement.setString(3,reportingDate);
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                chargeVersionRate=rs.getString("merchantChargeValue");
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch(SystemError e)
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
    public long getGatewayAccountWireCount(GatewayAccount gatewayAccount)throws PZDBViolationException
    {
        String strQuery = "SELECT count(*) AS 'invoicecount' FROM iso_commission_wire_manager WHERE accountid=? ";
        Connection con = null;
        ResultSet rs=null;
        long wireCount=0;
        PreparedStatement psWireBalaceAmount=null;
        try
        {
            con = Database.getRDBConnection();
            psWireBalaceAmount = con.prepareStatement(strQuery);
            psWireBalaceAmount.setInt(1, gatewayAccount.getAccountId());
            rs = psWireBalaceAmount.executeQuery();
            if (rs.next())
            {
                wireCount=rs.getLong("invoicecount");
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting commission list",e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayAccountWireCount", null, "Common", "Sql exception while connecting to iso wire manager  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting commission list::",systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayAccountWireCount()", null, "Common", "Sql exception while connecting to iso wire manager table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(psWireBalaceAmount);
            Database.closeConnection(con);
        }
        return wireCount;
    }

    public long getWLPartnerWireCount(GatewayType gatewayType, String partnerId) throws PZDBViolationException
    {
        String strQuery = "SELECT count(*) AS 'invoicecount' FROM wl_invoice_manager WHERE partner_id=? and pgtype_id=?";
        Connection con = null;
        ResultSet rs=null;
        PreparedStatement psWireBalaceAmount=null;
        long wireCount = 0;
        try
        {
            con = Database.getRDBConnection();
            psWireBalaceAmount = con.prepareStatement(strQuery);
            psWireBalaceAmount.setString(1, partnerId);
            psWireBalaceAmount.setString(2, gatewayType.getPgTypeId());
            rs = psWireBalaceAmount.executeQuery();
            if (rs.next())
            {
                wireCount = rs.getLong("invoicecount");
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException while getting wl partner commission wire count", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getWLPartnerWireCount", null, "Common", "Sql exception while connecting to iso wire manager  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while getting wl partner commission wire count", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getWLPartnerWireCount()", null, "Common", "Sql exception while connecting to iso wire manager table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(psWireBalaceAmount);
            Database.closeConnection(con);
        }
        return wireCount;
    }

    public long getWLPartnerWireCount(String partnerId) throws PZDBViolationException
    {
        String strQuery = "SELECT count(*) AS 'invoicecount' FROM wl_invoice_manager WHERE partner_id=?";
        Connection con = null;
        ResultSet rs=null;
        PreparedStatement psWireBalaceAmount=null;
        long wireCount = 0;
        try
        {
            con = Database.getRDBConnection();
            psWireBalaceAmount = con.prepareStatement(strQuery);
            psWireBalaceAmount.setString(1, partnerId);
            rs = psWireBalaceAmount.executeQuery();
            if (rs.next())
            {
                wireCount = rs.getLong("invoicecount");
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException while getting wl partner commission wire count", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getWLPartnerWireCount()", null, "Common", "Sql exception while connecting to iso wire manager  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while getting wl partner commission wire count", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getWLPartnerWireCount()", null, "Common", "Sql exception while connecting to iso wire manager table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(psWireBalaceAmount);
            Database.closeConnection(con);
        }
        return wireCount;
    }
    public List<WLPartnerCommissionVO> getWLPartnerCommissionsList(String partnerId, String pgTypeId) throws PZDBViolationException
    {
        List<com.manager.vo.WLPartnerCommissionVO> wlPartnerCommissionVOs = new LinkedList();
        com.manager.vo.WLPartnerCommissionVO wlPartnerCommissionVO = null;
        ChargeMasterVO chargeMasterVO = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT wlpc.commission_id,wlpc.commission_value,C.chargeid,C.valuetype,C.chargename,wlpc.isinput_required,C.keyword,wlpc.sequence_no,C.frequency,C.category,C.subkeyword FROM wl_partner_commission_mapping AS wlpc, charge_master AS C WHERE wlpc.commission_id=C.chargeid AND wlpc.partner_id=? AND wlpc.pgtype_id=? ORDER BY wlpc.sequence_no";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, partnerId);
            pstmt.setString(2, pgTypeId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                chargeMasterVO = new ChargeMasterVO();
                chargeMasterVO.setChargeId(rs.getString("chargeid"));
                chargeMasterVO.setValueType(rs.getString("valuetype"));
                chargeMasterVO.setChargeName(rs.getString("chargename"));
                chargeMasterVO.setKeyword(rs.getString("keyword"));
                chargeMasterVO.setFrequency(rs.getString("frequency"));
                chargeMasterVO.setCategory(rs.getString("category"));
                chargeMasterVO.setSubKeyword(rs.getString("subkeyword"));

                wlPartnerCommissionVO = new com.manager.vo.WLPartnerCommissionVO();

                wlPartnerCommissionVO.setChargeId(rs.getString("commission_id"));
                wlPartnerCommissionVO.setCommissionValue(rs.getDouble("commission_value"));
                wlPartnerCommissionVO.setIsInputRequired(rs.getString("isinput_required"));
                wlPartnerCommissionVO.setSequenceNo(rs.getString("sequence_no"));
                wlPartnerCommissionVO.setChargeMasterVO(chargeMasterVO);
                wlPartnerCommissionVOs.add(wlPartnerCommissionVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting WL partner invoice commission list", e);
            PZExceptionHandler.raiseDBViolationException(CommissionDAO.class.getName(), "getWLPartnerCommissionsList", null, "Common", "Sql exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting WL partner invoice commission list::", systemError);
            PZExceptionHandler.raiseDBViolationException(CommissionDAO.class.getName(), "getWLPartnerCommissionsList()", null, "Common", "Sql exception", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return wlPartnerCommissionVOs;

    }

    public HashMap<String, List<WLPartnerCommissionVO>> getWLPartnerCommissionsList(String partnerId) throws PZDBViolationException
    {
        HashMap<String, List<WLPartnerCommissionVO>> listHashMap = new HashMap();
        List<WLPartnerCommissionVO> wlPartnerCommissionVOs = null;
        WLPartnerCommissionVO wlPartnerCommissionVO = null;
        ChargeMasterVO chargeMasterVO = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT wlpc.commission_id,wlpc.commission_value,wlpc.pgtype_id,c.chargeid,c.valuetype,c.chargename,wlpc.isinput_required,c.keyword,wlpc.sequence_no,c.frequency,c.category,c.subkeyword FROM wl_partner_commission_mapping AS wlpc, charge_master AS c WHERE wlpc.commission_id=c.chargeid AND wlpc.partner_id=? ORDER BY wlpc.sequence_no";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, partnerId);
            rs = pstmt.executeQuery();
            Functions fuction=new Functions();
            while (rs.next())
            {
                String pgTypeId = rs.getString("pgtype_id");
                if(!fuction.isValueNull(pgTypeId) || "0".equals(pgTypeId))
                {
                    pgTypeId="0";
                }

                wlPartnerCommissionVOs = listHashMap.get(pgTypeId);
                if(wlPartnerCommissionVOs == null)
                {
                    wlPartnerCommissionVOs = new LinkedList();
                }

                chargeMasterVO = new ChargeMasterVO();
                chargeMasterVO.setChargeId(rs.getString("chargeid"));
                chargeMasterVO.setValueType(rs.getString("valuetype"));
                chargeMasterVO.setChargeName(rs.getString("chargename"));
                chargeMasterVO.setKeyword(rs.getString("keyword"));
                chargeMasterVO.setFrequency(rs.getString("frequency"));
                chargeMasterVO.setCategory(rs.getString("category"));
                chargeMasterVO.setSubKeyword(rs.getString("subkeyword"));

                wlPartnerCommissionVO = new WLPartnerCommissionVO();

                wlPartnerCommissionVO.setChargeId(rs.getString("commission_id"));
                wlPartnerCommissionVO.setCommissionValue(rs.getDouble("commission_value"));
                wlPartnerCommissionVO.setIsInputRequired(rs.getString("isinput_required"));
                wlPartnerCommissionVO.setSequenceNo(rs.getString("sequence_no"));
                wlPartnerCommissionVO.setChargeMasterVO(chargeMasterVO);
                wlPartnerCommissionVOs.add(wlPartnerCommissionVO);

                listHashMap.put(pgTypeId, wlPartnerCommissionVOs);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting WL partner invoice commission list", e);
            PZExceptionHandler.raiseDBViolationException(CommissionDAO.class.getName(), "getWLPartnerCommissionsList", null, "Common", "Sql exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting WL partner invoice commission list::", systemError);
            PZExceptionHandler.raiseDBViolationException(CommissionDAO.class.getName(), "getWLPartnerCommissionsList()", null, "Common", "Sql exception", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return listHashMap;
    }
    public List<WLPartnerCommissionVO> getWLPartnerDynamicCommissions(String partnerId, String pgtypeId) throws PZDBViolationException
    {
        List<WLPartnerCommissionVO> wlPartnerCommissionVOs = new LinkedList();
        WLPartnerCommissionVO wlPartnerCommissionVO = null;
        ChargeMasterVO chargeMasterVO = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT wlpc.commission_id,wlpc.commission_value,C.valuetype,C.chargename,wlpc.isinput_required,C.keyword,wlpc.sequence_no,C.frequency,C.category,C.subkeyword FROM wl_partner_commission_mapping AS wlpc, charge_master AS C WHERE wlpc.isinput_required='Y' AND wlpc.commission_id=C.chargeid AND wlpc.isActive='Y' AND wlpc.partner_id=? AND wlpc.pgtype_id=? ORDER BY wlpc.sequence_no";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, partnerId);
            pstmt.setString(2, pgtypeId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                wlPartnerCommissionVO = new WLPartnerCommissionVO();

                wlPartnerCommissionVO.setChargeId(rs.getString("commission_id"));
                wlPartnerCommissionVO.setCommissionValue(rs.getDouble("commission_value"));
                wlPartnerCommissionVO.setIsInputRequired(rs.getString("isinput_required"));
                wlPartnerCommissionVO.setSequenceNo(rs.getString("sequence_no"));

                chargeMasterVO = new ChargeMasterVO();

                chargeMasterVO.setValueType(rs.getString("valuetype"));
                chargeMasterVO.setChargeName(rs.getString("chargename"));
                chargeMasterVO.setKeyword(rs.getString("keyword"));
                chargeMasterVO.setFrequency(rs.getString("frequency"));
                chargeMasterVO.setCategory(rs.getString("category"));
                chargeMasterVO.setSubKeyword(rs.getString("subkeyword"));

                wlPartnerCommissionVO.setChargeMasterVO(chargeMasterVO);
                wlPartnerCommissionVOs.add(wlPartnerCommissionVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting WL partner invoice commission list", e);
            PZExceptionHandler.raiseDBViolationException(CommissionDAO.class.getName(), "getWLPartnerDynamicCommissions", null, "Common", "Sql exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting WL partner invoice commission list::", systemError);
            PZExceptionHandler.raiseDBViolationException(CommissionDAO.class.getName(), "getWLPartnerDynamicCommissions()", null, "Common", "Sql exception", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return wlPartnerCommissionVOs;

    }

    public List<WLPartnerCommissionVO> getWLPartnerDynamicCommissions(String partnerId) throws PZDBViolationException
    {
        List<WLPartnerCommissionVO> wlPartnerCommissionVOs = new LinkedList();
        WLPartnerCommissionVO wlPartnerCommissionVO = null;
        ChargeMasterVO chargeMasterVO = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT wlpc.commission_id,wlpc.commission_value,C.valuetype,C.chargename,wlpc.isinput_required,C.keyword,wlpc.sequence_no,C.frequency,C.category,C.subkeyword FROM wl_partner_commission_mapping AS wlpc, charge_master AS C WHERE wlpc.isinput_required='Y' AND wlpc.commission_id=C.chargeid AND wlpc.isActive='Y' AND wlpc.partner_id=? ORDER BY wlpc.sequence_no";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, partnerId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                wlPartnerCommissionVO = new WLPartnerCommissionVO();

                wlPartnerCommissionVO.setChargeId(rs.getString("commission_id"));
                wlPartnerCommissionVO.setCommissionValue(rs.getDouble("commission_value"));
                wlPartnerCommissionVO.setIsInputRequired(rs.getString("isinput_required"));
                wlPartnerCommissionVO.setSequenceNo(rs.getString("sequence_no"));

                chargeMasterVO = new ChargeMasterVO();

                chargeMasterVO.setValueType(rs.getString("valuetype"));
                chargeMasterVO.setChargeName(rs.getString("chargename"));
                chargeMasterVO.setKeyword(rs.getString("keyword"));
                chargeMasterVO.setFrequency(rs.getString("frequency"));
                chargeMasterVO.setCategory(rs.getString("category"));
                chargeMasterVO.setSubKeyword(rs.getString("subkeyword"));

                wlPartnerCommissionVO.setChargeMasterVO(chargeMasterVO);
                wlPartnerCommissionVOs.add(wlPartnerCommissionVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException while getting WL partner invoice dynamic commission list", e);
            PZExceptionHandler.raiseDBViolationException(CommissionDAO.class.getName(), "getWLPartnerDynamicCommissions()", null, "Common", "Sql exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while getting WL partner invoice dynamic commission list::", systemError);
            PZExceptionHandler.raiseDBViolationException(CommissionDAO.class.getName(), "getWLPartnerDynamicCommissions()", null, "Common", "Sql exception", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return wlPartnerCommissionVOs;
    }
    public WLPartnerInvoiceVO getLastWLPartnerInvoiceDetails(String pgTypeId, String partnerId) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        WLPartnerInvoiceVO wlPartnerInvoiceVO = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuffer sbQuery = new StringBuffer("SELECT start_date,end_date FROM wl_invoice_manager WHERE pgtype_id=? and partner_id=? ORDER BY id DESC LIMIT 1");
            pstmt = con.prepareStatement(sbQuery.toString());
            pstmt.setString(1, pgTypeId);
            pstmt.setString(2, partnerId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                wlPartnerInvoiceVO = new WLPartnerInvoiceVO();
                wlPartnerInvoiceVO.setStartDate(rs.getString("start_date"));
                wlPartnerInvoiceVO.setEndDate(rs.getString("end_date"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException while getting WL partner recent invoice details", e);
            PZExceptionHandler.raiseDBViolationException(CommissionDAO.class.getName(), "getLastWLPartnerInvoiceDetails()", null, "Common", "Sql exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while getting WL partner recent invoice details::", systemError);
            PZExceptionHandler.raiseDBViolationException(CommissionDAO.class.getName(), "getLastWLPartnerInvoiceDetails()", null, "Common", "Sql exception", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return wlPartnerInvoiceVO;
    }

    public WLPartnerInvoiceVO getLastWLPartnerInvoiceDetails(String partnerId) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        WLPartnerInvoiceVO wlPartnerInvoiceVO = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuffer sbQuery = new StringBuffer("SELECT start_date,end_date FROM wl_invoice_manager where partner_id=? ORDER BY id DESC LIMIT 1");
            pstmt = con.prepareStatement(sbQuery.toString());
            pstmt.setString(1, partnerId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                wlPartnerInvoiceVO = new WLPartnerInvoiceVO();
                wlPartnerInvoiceVO.setStartDate(rs.getString("start_date"));
                wlPartnerInvoiceVO.setEndDate(rs.getString("end_date"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException while getting WL partner recent invoice details", e);
            PZExceptionHandler.raiseDBViolationException(CommissionDAO.class.getName(), "getLastWLPartnerInvoiceDetails()", null, "Common", "Sql exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while getting WL partner recent invoice details::", systemError);
            PZExceptionHandler.raiseDBViolationException(CommissionDAO.class.getName(), "getLastWLPartnerInvoiceDetails()", null, "Common", "Sql exception", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return wlPartnerInvoiceVO;
    }
}
