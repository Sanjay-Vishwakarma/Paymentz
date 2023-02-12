package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.*;
import com.manager.vo.gatewayVOs.GatewayAccountVO;
import com.manager.vo.gatewayVOs.GatewayTypeVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 9/2/14
 * Time: 1:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class BankDao
{
    Logger logger = new Logger(BankDao.class.getName());
    //functions instance
    Functions functions = new Functions();
    //commonFunctionUtil instance
    CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();

    public List<BankRecievedSettlementCycleVO> getBankSettlementCycleList(InputDateVO inputDateVO,PaginationVO paginationVO,GatewayAccountVO gatewayAccountVO,GatewayTypeVO gatewayTypeVO)
    {
        Connection con = null;
        int count = 3;
        ResultSet rsBankSettlementList= null;
        PreparedStatement psSettlementCycleList=null;
        List<BankRecievedSettlementCycleVO> bankSettlementList = new ArrayList<BankRecievedSettlementCycleVO>();
        try
        {
            con= Database.getRDBConnection();
            StringBuilder query = new StringBuilder("Select * from bank_settlement_received_master where expected_startdate >=? and expected_startdate <=?");
            if(functions.isValueNull(String.valueOf(gatewayAccountVO.getAccountId())))
            {
                query.append(" and accountid = ?");
            }
            if(functions.isValueNull(gatewayTypeVO.getPgTYypeId()))
            {
                query.append(" and pgtypeid = (select pgtypeid from gateway_type where gateway = ?)");
            }
            query.append(" order by expected_startdate ");

            String countQuery="Select Count(*) from ("+query.toString()+") as temp";
            logger.debug("countQuery:-"+countQuery);
            query.append("  limit "+paginationVO.getStart()+","+paginationVO.getEnd());
            logger.debug("Query:-"+query);

            psSettlementCycleList = con.prepareStatement(query.toString());
            psSettlementCycleList.setString(1,inputDateVO.getsMinTransactionDate());
            psSettlementCycleList.setString(2,inputDateVO.getsMaxTransactionDate());

            if(functions.isValueNull(String.valueOf(gatewayAccountVO.getAccountId())))
            {
                psSettlementCycleList.setString(count, gatewayAccountVO.getAccountId());
                count++;
            }
            if(functions.isValueNull(gatewayTypeVO.getPgTYypeId()))
            {
                psSettlementCycleList.setString(count,gatewayTypeVO.getPgTYypeId());
            }
            logger.debug("countQuery:-"+psSettlementCycleList);
            rsBankSettlementList=psSettlementCycleList.executeQuery();
            while(rsBankSettlementList.next())
            {
                BankRecievedSettlementCycleVO bankSettlementCycleVO = new BankRecievedSettlementCycleVO();
                bankSettlementCycleVO.setBankSettlementReceivedId(rsBankSettlementList.getString("cycleid"));
                bankSettlementCycleVO.setAccountId(rsBankSettlementList.getString("accountid"));
                bankSettlementCycleVO.setPgTypeId(rsBankSettlementList.getString("pgtypeid"));
                bankSettlementCycleVO.setMerchantId(rsBankSettlementList.getString("merchantid"));
                bankSettlementCycleVO.setSettlementDate(Functions.checkStringNull(rsBankSettlementList.getString("settlementdate")) == null ? "" : rsBankSettlementList.getString("settlementdate"));
                bankSettlementCycleVO.setExpected_startDate(rsBankSettlementList.getString("expected_startdate"));
                bankSettlementCycleVO.setExpected_endDate(rsBankSettlementList.getString("expected_enddate"));
                bankSettlementCycleVO.setActual_startDate(Functions.checkStringNull(rsBankSettlementList.getString("actual_startdate")) == null ? "" : rsBankSettlementList.getString("actual_startdate"));
                bankSettlementCycleVO.setActual_endDate(Functions.checkStringNull(rsBankSettlementList.getString("actual_enddate")) == null ? "" : rsBankSettlementList.getString("actual_enddate"));
                bankSettlementCycleVO.setBank_settlementId(rsBankSettlementList.getString("bank_settlementid"));
                bankSettlementCycleVO.setSettlementCronExecuted(rsBankSettlementList.getString("issettlementcronexcecuted"));
                bankSettlementCycleVO.setPayoutCronExecuted(rsBankSettlementList.getString("ispayoutcronexecuted"));
                bankSettlementList.add(bankSettlementCycleVO);
            }
            //total records query
            count=3;
            psSettlementCycleList=con.prepareStatement(countQuery);
            psSettlementCycleList.setString(1,inputDateVO.getsMinTransactionDate());
            psSettlementCycleList.setString(2,inputDateVO.getsMaxTransactionDate());

            if(functions.isValueNull(String.valueOf(gatewayAccountVO.getAccountId())))
            {
                psSettlementCycleList.setString(count, gatewayAccountVO.getAccountId());
                count++;
            }
            if(functions.isValueNull(gatewayTypeVO.getPgTYypeId()))
            {
                psSettlementCycleList.setString(count,gatewayTypeVO.getPgTYypeId());
            }
            rsBankSettlementList=psSettlementCycleList.executeQuery();
            if(rsBankSettlementList.next())
            {
                paginationVO.setTotalRecords(rsBankSettlementList.getInt(1));
            }
        }
        catch (SQLException e)
        {
            logger.error(" bank settlement Cycle Sql Exception::",e);
        }
        catch (SystemError systemError)
        {
            logger.error(" bank settlement Cycle System error ::",systemError);
        }
        finally {
            Database.closeResultSet(rsBankSettlementList);
            Database.closePreparedStatement(psSettlementCycleList);
            Database.closeConnection(con);
        }
        return bankSettlementList;
    }

    public BankRecievedSettlementCycleVO getSingleBankSettlementCycleActionSpecific(ActionVO actionVO)
    {
        Connection con = null;
        ResultSet rsSingleBankSettlementCycle = null;
        PreparedStatement psSingleBankSettlementCycle=null;
        BankRecievedSettlementCycleVO bankSettlementCycleVO = null;
        try
        {
            con = Database.getRDBConnection();
            String query = "Select * from bank_settlement_received_master where cycleid =?";
            psSingleBankSettlementCycle = con.prepareStatement(query.toString());
            psSingleBankSettlementCycle.setString(1, actionVO.getActionCriteria());
            rsSingleBankSettlementCycle = psSingleBankSettlementCycle.executeQuery();
            if (rsSingleBankSettlementCycle.next())
            {
                bankSettlementCycleVO = new BankRecievedSettlementCycleVO();
                bankSettlementCycleVO.setBankSettlementReceivedId(rsSingleBankSettlementCycle.getString("cycleid"));
                bankSettlementCycleVO.setAccountId(rsSingleBankSettlementCycle.getString("accountid"));
                bankSettlementCycleVO.setPgTypeId(rsSingleBankSettlementCycle.getString("pgtypeid"));
                bankSettlementCycleVO.setMerchantId(rsSingleBankSettlementCycle.getString("merchantid"));
                bankSettlementCycleVO.setSettlementDate(commonFunctionUtil.convertTimestampToDatepicker(Functions.checkStringNull(rsSingleBankSettlementCycle.getString("settlementdate"))==null?"0000-00-00 00:00:00":rsSingleBankSettlementCycle.getString("settlementdate")));
                bankSettlementCycleVO.setExpected_startDate(commonFunctionUtil.convertTimestampToDatepicker(rsSingleBankSettlementCycle.getString("expected_startdate")));
                bankSettlementCycleVO.setExpected_endDate(commonFunctionUtil.convertTimestampToDatepicker(rsSingleBankSettlementCycle.getString("expected_enddate")));
                bankSettlementCycleVO.setActual_startDate(commonFunctionUtil.convertTimestampToDatepicker(Functions.checkStringNull(rsSingleBankSettlementCycle.getString("actual_startdate"))==null?"0000-00-00 00:00:00":rsSingleBankSettlementCycle.getString("actual_startdate")));
                bankSettlementCycleVO.setActual_endDate(commonFunctionUtil.convertTimestampToDatepicker(Functions.checkStringNull(rsSingleBankSettlementCycle.getString("actual_enddate"))==null?"0000-00-00 00:00:00":rsSingleBankSettlementCycle.getString("actual_enddate")));
                bankSettlementCycleVO.setBank_settlementId(rsSingleBankSettlementCycle.getString("bank_settlementid"));
                bankSettlementCycleVO.setSettlementCronExecuted(rsSingleBankSettlementCycle.getString("issettlementcronexcecuted"));
                bankSettlementCycleVO.setPayoutCronExecuted(rsSingleBankSettlementCycle.getString("ispayoutcronexecuted"));
            }

        }
        catch (SystemError systemError)
        {
            logger.error("System error for getSingleBankSettlementCycleActionSpecific::", systemError);
        }
        catch (SQLException e)
        {
            logger.error(" SQL Exception for getSingleBankSettlementCycleActionSpecific::", e);
        }
        finally {
            Database.closeResultSet(rsSingleBankSettlementCycle);
            Database.closePreparedStatement(psSingleBankSettlementCycle);
            Database.closeConnection(con);
        }
        return bankSettlementCycleVO;
    }
    //updating bank received Settlement cycle
    public boolean updateBankReceivedSettlementCycle(BankRecievedSettlementCycleVO bankRecievedSettlementCycleVO)
    {
        Connection con = null;
        PreparedStatement psupdateBankReceivedSettlementCycle=null;
        try
        {
            con=Database.getConnection();
            String update="update bank_settlement_received_master set accountid =?, pgtypeid =?,merchantid =?,settlementdate =?,expected_startdate =?,expected_enddate =?,actual_startdate =?,actual_enddate =?,issettlementcronexcecuted =?,ispayoutcronexecuted =? where cycleid =?";
            psupdateBankReceivedSettlementCycle = con.prepareStatement(update.toString());
            psupdateBankReceivedSettlementCycle.setString(1,bankRecievedSettlementCycleVO.getAccountId());
            psupdateBankReceivedSettlementCycle.setString(2,bankRecievedSettlementCycleVO.getPgTypeId());
            psupdateBankReceivedSettlementCycle.setString(3, bankRecievedSettlementCycleVO.getMerchantId());
            if(!functions.isValueNull(bankRecievedSettlementCycleVO.getSettlementDate()) ||("0000-00-00 00:00:00").equals(bankRecievedSettlementCycleVO.getSettlementDate()))
            {
                psupdateBankReceivedSettlementCycle.setNull(4, Types.TIMESTAMP);
            }
            else
            {
                psupdateBankReceivedSettlementCycle.setString(4,bankRecievedSettlementCycleVO.getSettlementDate());
            }
            psupdateBankReceivedSettlementCycle.setString(5, bankRecievedSettlementCycleVO.getExpected_startDate());
            psupdateBankReceivedSettlementCycle.setString(6, bankRecievedSettlementCycleVO.getExpected_endDate());
            if(!functions.isValueNull(bankRecievedSettlementCycleVO.getActual_startDate()) || ("0000-00-00 00:00:00").equals(bankRecievedSettlementCycleVO.getActual_startDate()))
            {
                psupdateBankReceivedSettlementCycle.setNull(7,Types.TIMESTAMP);
            }
            else
            {
                psupdateBankReceivedSettlementCycle.setString(7,bankRecievedSettlementCycleVO.getActual_startDate());
            }
            if(!functions.isValueNull(bankRecievedSettlementCycleVO.getActual_endDate()) || ("0000-00-00 00:00:00").equals(bankRecievedSettlementCycleVO.getActual_endDate()))
            {
                psupdateBankReceivedSettlementCycle.setNull(8,Types.TIMESTAMP);
            }
            else
            {
                psupdateBankReceivedSettlementCycle.setString(8,bankRecievedSettlementCycleVO.getActual_endDate());
            }
            psupdateBankReceivedSettlementCycle.setString(9,bankRecievedSettlementCycleVO.getSettlementCronExecuted());
            psupdateBankReceivedSettlementCycle.setString(10,bankRecievedSettlementCycleVO.getPayoutCronExecuted());
            psupdateBankReceivedSettlementCycle.setString(11, bankRecievedSettlementCycleVO.getBankSettlementReceivedId());

            logger.debug(" update query::"+update);
            logger.debug(" accountId::" + bankRecievedSettlementCycleVO.getAccountId() + " pgtypeId::" + bankRecievedSettlementCycleVO.getPgTypeId() + " MerchantId::" + bankRecievedSettlementCycleVO.getMerchantId() + " settlementDate::" + bankRecievedSettlementCycleVO.getSettlementDate() + " excpectedDate::" + bankRecievedSettlementCycleVO.getExpected_startDate() + " expectedEnddate::" + bankRecievedSettlementCycleVO.getExpected_endDate() + " actual satrtDt::" + bankRecievedSettlementCycleVO.getActual_startDate() + " Actual end date::" + bankRecievedSettlementCycleVO.getActual_endDate() + " bankReceivedId::" + bankRecievedSettlementCycleVO.getBankSettlementReceivedId());
            int rss=psupdateBankReceivedSettlementCycle.executeUpdate();
            if(rss>0)
            {
                return true;
            }

        }
        catch (SystemError systemError)
        {
            logger.error(" System error for updateBankReceivedSettlementCycle::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL exception for updateBankReceivedSettlementCycle::",e);
        }
        finally {
            Database.closePreparedStatement(psupdateBankReceivedSettlementCycle);
            Database.closeConnection(con);
        }
        return false;
    }
    //inserting to new Bank received Settlement Cycle
    public boolean insertNewBankReceivedSettlementCycle(BankRecievedSettlementCycleVO bankRecievedSettlementCycleVO) throws SystemError
    {
        Connection con = null;
        PreparedStatement psInsertBankReceivedSettlementCycle=null;
        ResultSet rsCondition=null;
        boolean flag=false;
        try
        {
            con=Database.getRDBConnection();
            String condition = "select * from bank_settlement_received_master where accountid=? ";
            psInsertBankReceivedSettlementCycle=con.prepareStatement(condition);
            psInsertBankReceivedSettlementCycle.setString(1,bankRecievedSettlementCycleVO.getAccountId());
            rsCondition=psInsertBankReceivedSettlementCycle.executeQuery();
            while(rsCondition.next())
            {
                if(rsCondition.getString("issettlementcronexcecuted").equals("N") || rsCondition.getString("issettlementcronexcecuted").equals("N"))
                {
                    throw new SystemError("ADDING FAILED FOR ACCOUNTID");
                }
                if(rsCondition.getString("bank_settlementid").equals(bankRecievedSettlementCycleVO.getBank_settlementId()))
                {
                    flag=true;
                }
            }
            if(flag)
            {
                throw new SystemError("UNIQUE BANK_SETTLEMENTID");
            }
            String insert = "insert into bank_settlement_received_master(accountid,pgtypeid,merchantid,settlementdate,expected_startdate,expected_enddate,actual_startdate,actual_enddate,bank_settlementid) values (?,?,?,?,?,?,?,?,?)";
            psInsertBankReceivedSettlementCycle = con.prepareStatement(insert.toString());
            psInsertBankReceivedSettlementCycle.setString(1, bankRecievedSettlementCycleVO.getAccountId());
            psInsertBankReceivedSettlementCycle.setString(2, bankRecievedSettlementCycleVO.getPgTypeId());
            psInsertBankReceivedSettlementCycle.setString(3, bankRecievedSettlementCycleVO.getMerchantId());
            if(!functions.isValueNull(bankRecievedSettlementCycleVO.getSettlementDate())||("0000-00-00 00:00:00").equals(bankRecievedSettlementCycleVO.getSettlementDate()))
            {
                psInsertBankReceivedSettlementCycle.setNull(4, Types.TIMESTAMP);
            }
            else
            {
                psInsertBankReceivedSettlementCycle.setString(4,bankRecievedSettlementCycleVO.getSettlementDate());
            }
            psInsertBankReceivedSettlementCycle.setString(5,bankRecievedSettlementCycleVO.getExpected_startDate());
            psInsertBankReceivedSettlementCycle.setString(6, bankRecievedSettlementCycleVO.getExpected_endDate());
            if(!functions.isValueNull(bankRecievedSettlementCycleVO.getActual_startDate()) || ("0000-00-00 00:00:00").equals(bankRecievedSettlementCycleVO.getActual_startDate()))
            {
                psInsertBankReceivedSettlementCycle.setNull(7,Types.TIMESTAMP);
            }
            else
            {
                psInsertBankReceivedSettlementCycle.setString(7,bankRecievedSettlementCycleVO.getActual_startDate());
            }
            if(!functions.isValueNull(bankRecievedSettlementCycleVO.getActual_endDate()) || ("0000-00-00 00:00:00").equals(bankRecievedSettlementCycleVO.getActual_endDate()))
            {
                psInsertBankReceivedSettlementCycle.setNull(8,Types.TIMESTAMP);
            }
            else
            {
                psInsertBankReceivedSettlementCycle.setString(8,bankRecievedSettlementCycleVO.getActual_endDate());
            }
            psInsertBankReceivedSettlementCycle.setString(9,bankRecievedSettlementCycleVO.getBank_settlementId());
            int rss=psInsertBankReceivedSettlementCycle.executeUpdate();
            if(rss>0)
            {
                return true;
            }
        }
        catch (SystemError systemError)
        {
            if(systemError.getMessage().toLowerCase().contains("adding failed"))
            {
                throw new SystemError("ADDING FAILED FOR ACCOUNTID");
            }
            if(systemError.getMessage().toLowerCase().contains("unique"))
            {
                throw new SystemError("UNIQUE BANK_SETTLEMENTID");
            }
            logger.error(" System Error for insertNewBankReceivedSettlementCycle ::",systemError);
        }
        catch (SQLException e)
        {
            logger.error(" Sql exception for insertNewBankReceivedSettlementCycle ::",e);
        }
        finally {
            Database.closeResultSet(rsCondition);
            Database.closePreparedStatement(psInsertBankReceivedSettlementCycle);
            Database.closeConnection(con);
        }
        return false;
    }
    //getting list of banking rolling reserve VO as per Date and accountId
    public List<BankRollingReserveVO> getBankRollingReserveList(InputDateVO inputDateVO, PaginationVO paginationVO, GatewayAccountVO gatewayAccountVO)throws SystemError,SQLException
    {
        Connection con = null;
        ResultSet rsBankRollingReserveList= null;
        PreparedStatement psBankRollingReserveList=null;
        List<BankRollingReserveVO> bankSettlementList = new ArrayList<BankRollingReserveVO>();
        try
        {
            con= Database.getRDBConnection();
            StringBuilder query = new StringBuilder("Select * from `bank_rollingreserve_master` where rollingreservereleaseupto >='"+inputDateVO.getsMinTransactionDate()+"' AND rollingreservereleaseupto <='"+inputDateVO.getsMaxTransactionDate()+"' ");
            if(functions.isValueNull(String.valueOf(gatewayAccountVO.getAccountId())))
            {
                query.append(" and accountid in (" + gatewayAccountVO.getAccountId() + ")");
            }
            query.append(" order by rollingreservereleaseupto ");
            String countQuery="Select Count(*) from ("+query.toString()+") as temp";
            logger.debug("countQuery:-" + countQuery);
            query.append("  limit " + paginationVO.getStart() + "," + paginationVO.getEnd());
            logger.debug("Query:-" + query);
            psBankRollingReserveList = con.prepareStatement(query.toString());
            rsBankRollingReserveList=psBankRollingReserveList.executeQuery();
            logger.debug("query for rolling reserve----"+psBankRollingReserveList);

            while(rsBankRollingReserveList.next())
            {
                BankRollingReserveVO bankRollingReserveVO = new BankRollingReserveVO();
                bankRollingReserveVO.setBankRollingReserveId(rsBankRollingReserveList.getString("id"));
                bankRollingReserveVO.setAccountId(rsBankRollingReserveList.getString("accountid"));
                bankRollingReserveVO.setRollingReserveDateUpTo(rsBankRollingReserveList.getString("rollingreservereleaseupto"));
                bankSettlementList.add(bankRollingReserveVO);
            }
            psBankRollingReserveList=con.prepareStatement(countQuery);
            rsBankRollingReserveList=psBankRollingReserveList.executeQuery();
            if(rsBankRollingReserveList.next())
            {
                paginationVO.setTotalRecords(rsBankRollingReserveList.getInt(1));
            }
        }
        finally
        {
            Database.closeResultSet(rsBankRollingReserveList);
            Database.closePreparedStatement(psBankRollingReserveList);
            Database.closeConnection(con);
        }
        return bankSettlementList;
    }
    //getting single BankRollingReserveVO
    public BankRollingReserveVO getSingleBankRollingReserveActionSpecific(ActionVO actionVO)
    {
        Connection con = null;
        ResultSet rsSingleBankRollingReserve = null;
        PreparedStatement psSingleBankRollingReserve=null;
        BankRollingReserveVO bankRollingReserveVO = null;
        try
        {
            con = Database.getRDBConnection();
            String query = "Select * from `bank_rollingreserve_master` where id =?";
            psSingleBankRollingReserve = con.prepareStatement(query.toString());
            psSingleBankRollingReserve.setString(1, actionVO.getActionCriteria());
            rsSingleBankRollingReserve = psSingleBankRollingReserve.executeQuery();
            if (rsSingleBankRollingReserve.next())
            {
                bankRollingReserveVO = new BankRollingReserveVO();
                bankRollingReserveVO.setBankRollingReserveId(rsSingleBankRollingReserve.getString("id"));
                bankRollingReserveVO.setAccountId(rsSingleBankRollingReserve.getString("accountid"));
                bankRollingReserveVO.setRollingReserveDateUpTo(rsSingleBankRollingReserve.getString("rollingreservereleaseupto"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System error for getSingleBankRollingReserveActionSpecific::", systemError);
        }
        catch (SQLException e)
        {
            logger.error(" SQL Exception for getSingleBankRollingReserveActionSpecific::", e);
        }
        finally {
            Database.closeResultSet(rsSingleBankRollingReserve);
            Database.closePreparedStatement(psSingleBankRollingReserve);
            Database.closeConnection(con);
        }
        return bankRollingReserveVO;
    }
    //updating Bank rolling reserve
    public boolean updateBankRollingReserve(BankRollingReserveVO bankRollingReserveVO)
    {
        Connection con = null;
        PreparedStatement psupdateBankRollingReserve=null;
        try
        {
            con=Database.getConnection();
            String update="update `bank_rollingreserve_master` set accountid =?,rollingreservereleaseupto =? where id =?";
            psupdateBankRollingReserve = con.prepareStatement(update.toString());
            psupdateBankRollingReserve.setString(1,bankRollingReserveVO.getAccountId());
            psupdateBankRollingReserve.setString(2,bankRollingReserveVO.getRollingReserveDateUpTo());
            psupdateBankRollingReserve.setString(3,bankRollingReserveVO.getBankRollingReserveId());
            logger.debug(" update query::" + update);
            int rss=psupdateBankRollingReserve.executeUpdate();
            if(rss>0)
            {
                return true;
            }
        }
        catch (SystemError systemError)
        {
            logger.error(" System error for updateBankRollingReserve::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL exception for updateBankRollingReserve::",e);
        }
        finally {
            Database.closePreparedStatement(psupdateBankRollingReserve);
            Database.closeConnection(con);
        }
        return false;
    }
    public boolean updateBankRollingReserveNew(BankRollingReserveVO bankRollingReserveVO)throws SystemError,SQLException//sandip
    {
        Connection con = null;
        PreparedStatement psupdateBankRollingReserve=null;
        try
        {
            con=Database.getConnection();
            String update="update bank_rollingreserve_master set rollingreservereleaseupto =? where accountid =?";
            psupdateBankRollingReserve = con.prepareStatement(update.toString());
            psupdateBankRollingReserve.setString(1,bankRollingReserveVO.getBankRollingReserveDateTime());
            psupdateBankRollingReserve.setString(2,bankRollingReserveVO.getAccountId());
            int k=psupdateBankRollingReserve.executeUpdate();
            if(k>0)
            {
                String insertQuery="insert into bank_rollingreserve_history(bank_rrhistoryid,accountid,merchantid,rollingreservereleaseupto) values(null,?,?,?)";
                psupdateBankRollingReserve=con.prepareStatement(insertQuery);
                psupdateBankRollingReserve.setString(1,bankRollingReserveVO.getAccountId());
                psupdateBankRollingReserve.setString(2,bankRollingReserveVO.getMerchantId());
                psupdateBankRollingReserve.setString(3,bankRollingReserveVO.getBankRollingReserveDateTime());
                k=psupdateBankRollingReserve.executeUpdate();
                if(k>0)
                {
                    return true;
                }
            }
        }
        finally
        {
            Database.closePreparedStatement(psupdateBankRollingReserve);
            Database.closeConnection(con);
        }
        return false;
    }
    //adding new Bank rolling reserve
    public boolean insertNewBankRollingReserve(BankRollingReserveVO bankRollingReserveVO)
    {
        Connection con = null;
        PreparedStatement psInsertBankRollingReserve=null;
        try
        {
            con=Database.getConnection();
            String insert = "insert into `bank_rollingreserve_master`(accountid,rollingreservereleaseupto) values (?,?)";
            psInsertBankRollingReserve = con.prepareStatement(insert.toString());
            psInsertBankRollingReserve.setString(1, bankRollingReserveVO.getAccountId());
            psInsertBankRollingReserve.setString(2,bankRollingReserveVO.getRollingReserveDateUpTo());
            int rss=psInsertBankRollingReserve.executeUpdate();
            if(rss>0)
            {
                return true;
            }
        }
        catch (SystemError systemError)
        {
            logger.error(" System Error for insertNewBankRollingReserve ::",systemError);
        }
        catch (SQLException e)
        {
            logger.error(" Sql exception for insertNewBankRollingReserve ::",e);
        }
        finally {
            Database.closePreparedStatement(psInsertBankRollingReserve);
            Database.closeConnection(con);
        }
        return false;
    }
    public boolean insertNewBankRollingReserveNew(BankRollingReserveVO bankRollingReserveVO)throws SystemError,SQLException//sandip
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        try
        {
            con=Database.getConnection();
            String insert = "insert into bank_rollingreserve_master(accountid,rollingreservereleaseupto) values (?,?)";
            pstmt = con.prepareStatement(insert.toString());
            pstmt.setString(1, bankRollingReserveVO.getAccountId());
            pstmt.setString(2, bankRollingReserveVO.getRollingReserveDateUpTo());
            int k=pstmt.executeUpdate();
            if(k>0)
            {
                String insertQuery="insert into bank_rollingreserve_history(bank_rrhistoryid,accountid,merchantid,rollingreservereleaseupto) values(null,?,?,?)";
                pstmt=con.prepareStatement(insertQuery);
                pstmt.setString(1,bankRollingReserveVO.getAccountId());
                pstmt.setString(2,bankRollingReserveVO.getMerchantId());
                pstmt.setString(3,bankRollingReserveVO.getRollingReserveDateUpTo());
                k=pstmt.executeUpdate();
                if(k>0)
                {
                    return true;
                }
            }
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return false;
    }
    public List<BankRecievedSettlementCycleVO> getBankReceivedSettlementByCycleId(int cycleId) //sandip
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet rs= null;
        StringBuilder query=null;
        List<BankRecievedSettlementCycleVO> bankSettlementList=null;
        BankRecievedSettlementCycleVO bankSettlementCycleVO=null;
        try
        {
            con= Database.getRDBConnection();
            bankSettlementList = new ArrayList<BankRecievedSettlementCycleVO>();

            query = new StringBuilder("SELECT * FROM bank_settlement_received_master WHERE id > ?");
            pstmt = con.prepareStatement(query.toString());
            pstmt.setInt(1,cycleId);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                bankSettlementCycleVO = new BankRecievedSettlementCycleVO();
                bankSettlementCycleVO.setBankSettlementReceivedId(rs.getString("cycleid"));
                bankSettlementCycleVO.setAccountId(rs.getString("accountid"));
                bankSettlementCycleVO.setPgTypeId(rs.getString("pgtypeid"));
                bankSettlementCycleVO.setMerchantId(rs.getString("merchantid"));
                bankSettlementCycleVO.setSettlementDate(rs.getString("settlementdate"));
                bankSettlementCycleVO.setExpected_startDate(rs.getString("expected_startdate"));
                bankSettlementCycleVO.setExpected_endDate(rs.getString("expected_enddate"));
                bankSettlementCycleVO.setActual_startDate(rs.getString("actual_startdate"));
                bankSettlementCycleVO.setActual_endDate(rs.getString("actual_enddate"));
                bankSettlementCycleVO.setBank_settlementId(rs.getString("bank_settlementid"));
                bankSettlementList.add(bankSettlementCycleVO);
            }

        }
        catch (SQLException e)
        {
            logger.error(" bank settlement Cycle Sql Exception::",e);
        }
        catch (SystemError systemError)
        {
            logger.error(" bank settlement Cycle System error ::",systemError);
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return bankSettlementList;
    }
    //getting list of bankMerchant settlement
    public List<BankMerchantSettlementVO> getBankMerchantSettlementList(InputDateVO inputDateVO, PaginationVO paginationVO, GatewayAccountVO gatewayAccountVO, MerchantDetailsVO merchantDetailsVO,GatewayTypeVO gatewayTypeVO)
    {
        Connection con = null;
        int count = 1;
        ResultSet rsBankMerchantSettlementList= null;
        PreparedStatement psMerchantSettlementList=null;
        List<BankMerchantSettlementVO> bankMerchantSettlementVOList = new ArrayList<BankMerchantSettlementVO>();
        try
        {
            con= Database.getRDBConnection();
            StringBuilder query = new StringBuilder("SELECT  bm.accountid,bm.memberid,bm.ispaid,bm.id,bm.timestamp,bm.cycleid ,gtype.pgtypeid FROM bank_merchant_settlement_master AS bm JOIN gateway_accounts AS gaccount ON bm.accountid=gaccount.accountid JOIN gateway_type AS gtype ON gaccount.pgtypeid=gtype.pgtypeid where bm.id>0 ");
            if(functions.isValueNull(String.valueOf(gatewayAccountVO.getAccountId())))
            {
                query.append(" and bm.accountid = ?");
            }
            if (functions.isValueNull(merchantDetailsVO.getMemberId()))
            {
                query.append(" and bm.memberid = ?");

            }
            if (functions.isValueNull(gatewayTypeVO.getPgTYypeId()))
            {
                query.append(" and gtype.pgtypeid = ?");
            }

            query.append(" order by bm.ispaid ");

            String countQuery="Select Count(*) from ("+query.toString()+") as temp";
            logger.debug("countQuery:-"+countQuery);
            query.append("  limit "+paginationVO.getStart()+","+paginationVO.getEnd());
            logger.debug("Query:-"+query);

            psMerchantSettlementList = con.prepareStatement(query.toString());
            if(functions.isValueNull(String.valueOf(gatewayAccountVO.getAccountId())))
            {
                psMerchantSettlementList.setString(count, gatewayAccountVO.getAccountId());
                count++;
            }
            if(functions.isValueNull(merchantDetailsVO.getMemberId()))
            {
                psMerchantSettlementList.setString(count,merchantDetailsVO.getMemberId());
                count++;
            }
            if (functions.isValueNull(gatewayTypeVO.getPgTYypeId()))
            {
                psMerchantSettlementList.setString(count,gatewayTypeVO.getPgTYypeId());
            }
            rsBankMerchantSettlementList=psMerchantSettlementList.executeQuery();
            while(rsBankMerchantSettlementList.next())
            {
                BankMerchantSettlementVO bankMerchantSettlementVO = new BankMerchantSettlementVO();
                bankMerchantSettlementVO.setBankMerchantSettlementId(rsBankMerchantSettlementList.getString("id"));
                bankMerchantSettlementVO.setAccountId(rsBankMerchantSettlementList.getString("accountid"));
                bankMerchantSettlementVO.setMemberId(rsBankMerchantSettlementList.getString("memberid"));
                bankMerchantSettlementVO.setBankReceivedSettlementId(rsBankMerchantSettlementList.getString("cycleid"));
                bankMerchantSettlementVO.setPaid(rsBankMerchantSettlementList.getString("ispaid"));
                bankMerchantSettlementVO.setPgTypeId(rsBankMerchantSettlementList.getString("pgtypeid"));
                bankMerchantSettlementVOList.add(bankMerchantSettlementVO);
            }
            //total records query
            count=1;
            psMerchantSettlementList=con.prepareStatement(countQuery);
            if(functions.isValueNull(String.valueOf(gatewayAccountVO.getAccountId())))
            {
                psMerchantSettlementList.setString(count, gatewayAccountVO.getAccountId());
                count++;
            }
            if(functions.isValueNull(merchantDetailsVO.getMemberId()))
            {
                psMerchantSettlementList.setString(count,merchantDetailsVO.getMemberId());
                count++;
            }
            if (functions.isValueNull(gatewayTypeVO.getPgTYypeId()))
            {
                psMerchantSettlementList.setString(count,gatewayTypeVO.getPgTYypeId());
            }
            rsBankMerchantSettlementList=psMerchantSettlementList.executeQuery();
            if(rsBankMerchantSettlementList.next())
            {
                paginationVO.setTotalRecords(rsBankMerchantSettlementList.getInt(1));
            }
            logger.debug("query for bank merchant settlement list----"+psMerchantSettlementList);
        }
        catch (SQLException e)
        {
            logger.error(" bank merchant settlement  Sql Exception::",e);
        }
        catch (SystemError systemError)
        {
            logger.error(" bank merchant settlement  System error ::",systemError);
        }
        finally {
            Database.closeResultSet(rsBankMerchantSettlementList);
            Database.closePreparedStatement(psMerchantSettlementList);
            Database.closeConnection(con);
        }
        return bankMerchantSettlementVOList;
    }
    //single Bank merchant according to the action specified
    public BankMerchantSettlementVO getSingleBankMerchantSettlementActionSpecific(ActionVO actionVO)
    {
        Connection con = null;
        ResultSet rsSingleBankMerchantSettlement = null;
        PreparedStatement psSingleBankMerchantSettlement=null;
        BankMerchantSettlementVO bankMerchantSettlementVO = null;
        try
        {
            con = Database.getRDBConnection();
            String query = "Select * from `bank_merchant_settlement_master` where id =?";
            psSingleBankMerchantSettlement = con.prepareStatement(query.toString());
            psSingleBankMerchantSettlement.setString(1, actionVO.getActionCriteria());
            rsSingleBankMerchantSettlement = psSingleBankMerchantSettlement.executeQuery();
            if (rsSingleBankMerchantSettlement.next())
            {
                bankMerchantSettlementVO = new BankMerchantSettlementVO();
                bankMerchantSettlementVO.setBankMerchantSettlementId(rsSingleBankMerchantSettlement.getString("id"));
                bankMerchantSettlementVO.setAccountId(rsSingleBankMerchantSettlement.getString("accountid"));
                bankMerchantSettlementVO.setMemberId(rsSingleBankMerchantSettlement.getString("memberid"));
                bankMerchantSettlementVO.setBankReceivedSettlementId(rsSingleBankMerchantSettlement.getString("cycleid"));
                bankMerchantSettlementVO.setPaid(rsSingleBankMerchantSettlement.getString("ispaid"));
            }

        }
        catch (SystemError systemError)
        {
            logger.error("System error for getSingleBankMerchantSettlementActionSpecific::", systemError);
        }
        catch (SQLException e)
        {
            logger.error(" SQL Exception for getSingleBankMerchantSettlementActionSpecific::", e);
        }
        finally {
            Database.closeResultSet(rsSingleBankMerchantSettlement);
            Database.closePreparedStatement(psSingleBankMerchantSettlement);
            Database.closeConnection(con);
        }
        return bankMerchantSettlementVO;
    }
    //updating Bank merchant settlement
    public boolean updateBankMerchantSettlement(BankMerchantSettlementVO bankMerchantSettlementVO)
    {
        Connection con = null;
        PreparedStatement psupdateBankMerchantSettlement=null;
        try
        {
            con=Database.getConnection();
            String update="update `bank_merchant_settlement_master` set ispaid =? where id =?";
            psupdateBankMerchantSettlement = con.prepareStatement(update.toString());
            psupdateBankMerchantSettlement.setString(1,bankMerchantSettlementVO.getPaid());
            psupdateBankMerchantSettlement.setString(2,bankMerchantSettlementVO.getBankMerchantSettlementId());
            logger.debug(" update query::"+update);
            int rss=psupdateBankMerchantSettlement.executeUpdate();
            if(rss>0)
            {
                return true;
            }

        }
        catch (SystemError systemError)
        {
            logger.error(" System error for updateBankMerchantSettlement",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL exception for updateBankMerchantSettlement",e);
        }
        finally {
            Database.closePreparedStatement(psupdateBankMerchantSettlement);
            Database.closeConnection(con);
        }
        return false;
    }
    //adding new Bank merchant settlement
    public boolean insertNewBankMerchantSettlement(BankMerchantSettlementVO bankMerchantSettlementVO)
    {
        Connection con = null;
        PreparedStatement psInsertBankMerchantSettlement=null;
        try
        {
            con=Database.getConnection();
            String insert = "insert into `bank_merchant_settlement_master` (accountid,memberid,cycleid,ispaid) values (?,?,?,?)";
            psInsertBankMerchantSettlement = con.prepareStatement(insert.toString());
            psInsertBankMerchantSettlement.setString(1,bankMerchantSettlementVO.getAccountId());
            psInsertBankMerchantSettlement.setString(2,bankMerchantSettlementVO.getMemberId());
            psInsertBankMerchantSettlement.setString(3,bankMerchantSettlementVO.getBankReceivedSettlementId());
            psInsertBankMerchantSettlement.setString(4,bankMerchantSettlementVO.getPaid());
            int rss=psInsertBankMerchantSettlement.executeUpdate();
            if(rss>0)
            {
                return true;
            }
        }
        catch (SystemError systemError)
        {
            logger.error(" System Error for insertNewBankMerchantSettlement",systemError);
        }
        catch (SQLException e)
        {
            logger.error(" Sql exception for insertNewBankMerchantSettlement",e);
        }
        finally {
            Database.closePreparedStatement(psInsertBankMerchantSettlement);
            Database.closeConnection(con);
        }
        return false;
    }
    //getting bank wire manager list
    public List<BankWireManagerVO> getBankWireManagerList(InputDateVO inputDateVO, PaginationVO paginationVO, GatewayAccountVO gatewayAccountVO, GatewayTypeVO gatewayTypeVO)
    {
        Connection con = null;
        int count = 3;
        ResultSet rsBankWireManagerList= null;
        PreparedStatement psBankWireManagerList=null;
        List<BankWireManagerVO> bankWireManagerVOs = new ArrayList<BankWireManagerVO>();
        try
        {
            con= Database.getRDBConnection();
            StringBuilder query = new StringBuilder("Select * from bank_wiremanager where server_start_date >=? and server_start_date <=?");
            if(functions.isValueNull(String.valueOf(gatewayAccountVO.getAccountId())))
            {
                query.append(" and accountId = ?");
            }
            if(functions.isValueNull(gatewayTypeVO.getPgTYypeId()))
            {
                query.append(" and pgtypeId = ?");
            }
            query.append(" order by server_start_date ");

            String countQuery="Select Count(*) from ("+query.toString()+") as temp";
            logger.debug("countQuery:-"+countQuery);
            query.append("  limit "+paginationVO.getStart()+","+paginationVO.getEnd());
            logger.debug("Query:-"+query);

            psBankWireManagerList = con.prepareStatement(query.toString());
            psBankWireManagerList.setString(1,inputDateVO.getsMinTransactionDate());
            psBankWireManagerList.setString(2,inputDateVO.getsMaxTransactionDate());

            if(functions.isValueNull(String.valueOf(gatewayAccountVO.getAccountId())))
            {
                psBankWireManagerList.setString(count, gatewayAccountVO.getAccountId());
                count++;
            }
            if(functions.isValueNull(gatewayTypeVO.getPgTYypeId()))
            {
                psBankWireManagerList.setString(count,gatewayTypeVO.getPgTYypeId());
            }
            rsBankWireManagerList=psBankWireManagerList.executeQuery();
            while(rsBankWireManagerList.next())
            {
                BankWireManagerVO bankWireManagerVO = new BankWireManagerVO();

                bankWireManagerVO.setBankwiremanagerId(rsBankWireManagerList.getString("bankwiremanagerId"));
                bankWireManagerVO.setSettleddate(Functions.checkStringNull(rsBankWireManagerList.getString("settleddate"))==null?"":rsBankWireManagerList.getString("settleddate"));
                bankWireManagerVO.setPgtypeId(rsBankWireManagerList.getString("pgtypeId"));
                bankWireManagerVO.setAccountId(rsBankWireManagerList.getString("accountId"));
                bankWireManagerVO.setMid(rsBankWireManagerList.getString("mid"));
                bankWireManagerVO.setBank_start_date(Functions.checkStringNull(rsBankWireManagerList.getString("bank_start_date"))==null?"":rsBankWireManagerList.getString("bank_start_date"));
                bankWireManagerVO.setBank_end_date(Functions.checkStringNull(rsBankWireManagerList.getString("bank_end_date"))==null?"":rsBankWireManagerList.getString("bank_end_date"));
                bankWireManagerVO.setServer_start_date(Functions.checkStringNull(rsBankWireManagerList.getString("server_start_date"))==null?"":rsBankWireManagerList.getString("server_start_date"));
                bankWireManagerVO.setServer_end_date(Functions.checkStringNull(rsBankWireManagerList.getString("server_end_date"))==null?"":rsBankWireManagerList.getString("server_end_date"));
                bankWireManagerVO.setProcessing_amount(rsBankWireManagerList.getString("processing_amount"));
                bankWireManagerVO.setGrossAmount(rsBankWireManagerList.getString("grossAmount"));
                bankWireManagerVO.setNetfinal_amount(rsBankWireManagerList.getString("netfinal_amount"));
                bankWireManagerVO.setUnpaid_amount(rsBankWireManagerList.getString("unpaid_amount"));
                bankWireManagerVO.setIsrollingreservereleasewire(rsBankWireManagerList.getString("isrollingreservereleasewire"));
                bankWireManagerVO.setRollingreservereleasedateupto(Functions.checkStringNull(rsBankWireManagerList.getString("rollingreservereleasedateupto"))==null?"":rsBankWireManagerList.getString("rollingreservereleasedateupto"));
                bankWireManagerVO.setDeclinedcoveredupto(Functions.checkStringNull(rsBankWireManagerList.getString("declinedcoveredupto"))==null?"":rsBankWireManagerList.getString("declinedcoveredupto"));
                bankWireManagerVO.setChargebackcoveredupto(Functions.checkStringNull(rsBankWireManagerList.getString("chargebackcoveredupto")) == null ? "" : rsBankWireManagerList.getString("chargebackcoveredupto"));
                bankWireManagerVO.setReversedCoveredUpto(Functions.checkStringNull(rsBankWireManagerList.getString("reversedCoveredUpto"))==null?"":rsBankWireManagerList.getString("reversedCoveredUpto"));
                bankWireManagerVO.setBanksettlement_report_file(Functions.checkStringNull(rsBankWireManagerList.getString("banksettlement_report_file"))==null?"":rsBankWireManagerList.getString("banksettlement_report_file"));
                bankWireManagerVO.setBanksettlement_transaction_file(Functions.checkStringNull(rsBankWireManagerList.getString("banksettlement_transaction_file"))==null?"":rsBankWireManagerList.getString("banksettlement_transaction_file"));
                bankWireManagerVO.setSettlementCronExceuted(rsBankWireManagerList.getString("isSettlementCronExceuted"));
                bankWireManagerVO.setPayoutCronExcuted(rsBankWireManagerList.getString("isPayoutCronExcuted"));
                bankWireManagerVO.setIspaid(rsBankWireManagerList.getString("ispaid"));
                bankWireManagerVOs.add(bankWireManagerVO);
            }
            //total records query
            count=3;
            psBankWireManagerList=con.prepareStatement(countQuery);
            psBankWireManagerList.setString(1,inputDateVO.getsMinTransactionDate());
            psBankWireManagerList.setString(2,inputDateVO.getsMaxTransactionDate());

            if(functions.isValueNull(String.valueOf(gatewayAccountVO.getAccountId())))
            {
                psBankWireManagerList.setString(count, gatewayAccountVO.getAccountId());
                count++;
            }
            if(functions.isValueNull(gatewayTypeVO.getPgTYypeId()))
            {
                psBankWireManagerList.setString(count,gatewayTypeVO.getPgTYypeId());
            }
            rsBankWireManagerList=psBankWireManagerList.executeQuery();
            if(rsBankWireManagerList.next())
            {
                paginationVO.setTotalRecords(rsBankWireManagerList.getInt(1));
            }
        }
        catch (SQLException e)
        {
            logger.error(" bank wire list  Sql Exception::",e);
        }
        catch (SystemError systemError)
        {
            logger.error(" bank wire list  System error ::",systemError);
        }
        finally {
            Database.closeResultSet(rsBankWireManagerList);
            Database.closePreparedStatement(psBankWireManagerList);
            Database.closeConnection(con);
        }
        return bankWireManagerVOs;
    }
    public List<BankWireManagerVO> getBankWireManagerListNew(InputDateVO inputDateVO, PaginationVO paginationVO, String accountid, String pgtypeid, String bankwireid, String parent_bankwireId/*GatewayAccountVO gatewayAccountVO, GatewayTypeVO gatewayTypeVO*/)//sandip
    {
        Connection con = null;
        ResultSet rs= null;
        PreparedStatement preparedStatement=null;
        List<BankWireManagerVO> bankWireManagerVOs = new ArrayList<BankWireManagerVO>();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        try
        {
            con= Database.getRDBConnection();
            StringBuilder query = new StringBuilder("select * from bank_wiremanager where bankwiremanagerid>0 ");
            StringBuilder countQuery=new StringBuilder("select count(*) from bank_wiremanager where bankwiremanagerid>0 ");
            if(functions.isValueNull(inputDateVO.getsMinTransactionDate()))
            {
                query.append(" and server_start_date >= '" + ESAPI.encoder().encodeForSQL(me,inputDateVO.getsMinTransactionDate()) + "'");
                countQuery.append(" and server_start_date >= '" + ESAPI.encoder().encodeForSQL(me,inputDateVO.getsMinTransactionDate()) + "'");
            }
            if(functions.isValueNull(inputDateVO.getsMinTransactionDate()))
            {
                query.append(" and server_end_date <= '" + ESAPI.encoder().encodeForSQL(me,inputDateVO.getsMaxTransactionDate()) + "'");
                countQuery.append(" and server_end_date <= '" + ESAPI.encoder().encodeForSQL(me,inputDateVO.getsMaxTransactionDate()) + "'");
            }
            if(functions.isValueNull(String.valueOf(accountid)) && !accountid.equals("0"))
            {
                query.append(" and accountId ="+ESAPI.encoder().encodeForSQL(me,accountid));
                countQuery.append(" and accountId ="+ESAPI.encoder().encodeForSQL(me,accountid));
            }
            if(functions.isValueNull(String.valueOf(pgtypeid)) && !pgtypeid.equals("0"))
            {
                query.append(" and pgtypeId ="+ESAPI.encoder().encodeForSQL(me,pgtypeid));
                countQuery.append(" and pgtypeId ="+ESAPI.encoder().encodeForSQL(me,pgtypeid));
            }
            if (functions.isValueNull(String.valueOf(bankwireid)) && !bankwireid.equals("0"))
            {
                query.append(" and bankwiremanagerId ="+ESAPI.encoder().encodeForSQL(me,bankwireid));
                countQuery.append(" and bankwiremanagerId ="+ESAPI.encoder().encodeForSQL(me,bankwireid));
            }
            if (functions.isValueNull(String.valueOf(parent_bankwireId)) && !parent_bankwireId.equals("0") )
            {
                query.append(" and parent_bankwireid ="+ESAPI.encoder().encodeForSQL(me,parent_bankwireId));
                countQuery.append(" and parent_bankwireid ="+ESAPI.encoder().encodeForSQL(me,parent_bankwireId));
            }
            query.append(" order by bankwiremanagerId DESC limit "+ paginationVO.getStart() + "," + paginationVO.getEnd());
            logger.debug("countQuery:-"+countQuery);
            logger.debug("Query:-"+query);

            preparedStatement=con.prepareStatement(query.toString());
            logger.error("QUERY FOR BANKWIREMANAGER LIST::: "+preparedStatement);
            rs=preparedStatement.executeQuery();
            while(rs.next())
            {
                BankWireManagerVO bankWireManagerVO = new BankWireManagerVO();

                bankWireManagerVO.setBankwiremanagerId(rs.getString("bankwiremanagerId"));
                bankWireManagerVO.setSettleddate(Functions.checkStringNull(rs.getString("settleddate")) == null ? "" : rs.getString("settleddate"));
                bankWireManagerVO.setPgtypeId(rs.getString("pgtypeId"));
                bankWireManagerVO.setAccountId(rs.getString("accountId"));
                bankWireManagerVO.setMid(rs.getString("mid"));
                bankWireManagerVO.setBank_start_date(rs.getString("bank_start_date"));
                bankWireManagerVO.setBank_end_date(rs.getString("bank_end_date"));
                bankWireManagerVO.setServer_start_date(rs.getString("server_start_date"));
                bankWireManagerVO.setServer_end_date(rs.getString("server_end_date"));
                bankWireManagerVO.setProcessing_amount(rs.getString("processing_amount"));
                bankWireManagerVO.setGrossAmount(rs.getString("grossAmount"));
                bankWireManagerVO.setNetfinal_amount(rs.getString("netfinal_amount"));
                bankWireManagerVO.setUnpaid_amount(rs.getString("unpaid_amount"));
                bankWireManagerVO.setIsrollingreservereleasewire(rs.getString("isrollingreservereleasewire"));
                bankWireManagerVO.setRollingreservereleasedateupto(rs.getString("rollingreservereleasedateupto"));
                bankWireManagerVO.setDeclinedcoveredupto(rs.getString("declinedcoveredupto"));
                bankWireManagerVO.setChargebackcoveredupto(rs.getString("chargebackcoveredupto"));
                bankWireManagerVO.setReversedCoveredUpto(rs.getString("reversedCoveredUpto"));
                bankWireManagerVO.setBanksettlement_report_file(rs.getString("banksettlement_report_file"));
                bankWireManagerVO.setBanksettlement_transaction_file(rs.getString("banksettlement_transaction_file"));
                bankWireManagerVO.setSettlementCronExceuted(rs.getString("isSettlementCronExceuted"));
                bankWireManagerVO.setPayoutCronExcuted(rs.getString("isPayoutCronExcuted"));
                bankWireManagerVO.setIspaid(rs.getString("ispaid"));
                bankWireManagerVO.setParent_bankwireid(rs.getString("parent_bankwireid"));

                bankWireManagerVO.setDeclinedcoveredStartdate(rs.getString("declinedCoveredStartdate") == null ? "0000-00-00 00:00:00" : rs.getString("declinedCoveredStartdate"));
                bankWireManagerVO.setChargebackcoveredStartdate(rs.getString("chargebackCoveredStartdate") == null ? "0000-00-00 00:00:00" : rs.getString("chargebackCoveredStartdate"));
                bankWireManagerVO.setReversedCoveredStartdate(rs.getString("reversedCoveredStartdate") == null ? "0000-00-00 00:00:00" : rs.getString("reversedCoveredStartdate"));
                bankWireManagerVO.setRollingreservereleaseStartdate(rs.getString("rollingreserveStartdate") == null ? "0000-00-00 00:00:00" : rs.getString("rollingreserveStartdate"));

                bankWireManagerVOs.add(bankWireManagerVO);
            }
            //total records query
            preparedStatement=con.prepareStatement(countQuery.toString());
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                paginationVO.setTotalRecords(rs.getInt(1));
            }
        }
        catch (SQLException e)
        {
            logger.error(" bank wire list  Sql Exception::",e);
        }
        catch (SystemError systemError)
        {
            logger.error(" bank wire list  System error ::",systemError);
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return bankWireManagerVOs;
    }

    public List<BankWireManagerVO> getBankWireManagerListNew1(InputDateVO inputDateVO, PaginationVO paginationVO, String accountid, String bankwireId,String listOfAccountId)//Mahima
    {
        Connection con = null;
        ResultSet rs= null;
        PreparedStatement preparedStatement=null;
        List<BankWireManagerVO> bankWireManagerVOs = new ArrayList<BankWireManagerVO>();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        try
        {
            con= Database.getRDBConnection();
            StringBuilder query = new StringBuilder("select * from bank_wiremanager where bankwiremanagerid>0 AND accountId IN ("+listOfAccountId+")");
            StringBuilder countQuery=new StringBuilder("select count(*) from bank_wiremanager where bankwiremanagerid>0 AND accountId IN ("+listOfAccountId+")");
            if(functions.isValueNull(inputDateVO.getsMinTransactionDate()))
            {
                query.append(" and server_start_date >= '" + ESAPI.encoder().encodeForSQL(me,inputDateVO.getsMinTransactionDate()) + "'");
                countQuery.append(" and server_start_date >= '" + ESAPI.encoder().encodeForSQL(me,inputDateVO.getsMinTransactionDate()) + "'");
            }
            if(functions.isValueNull(inputDateVO.getsMinTransactionDate()))
            {
                query.append(" and server_end_date <= '" + ESAPI.encoder().encodeForSQL(me,inputDateVO.getsMaxTransactionDate()) + "'");
                countQuery.append(" and server_end_date <= '" + ESAPI.encoder().encodeForSQL(me,inputDateVO.getsMaxTransactionDate()) + "'");
            }
            if(functions.isValueNull(String.valueOf(accountid)) && !accountid.equals("0"))
            {
                query.append(" and accountId ="+ESAPI.encoder().encodeForSQL(me,accountid));
                countQuery.append(" and accountId ="+ESAPI.encoder().encodeForSQL(me,accountid));
            }
            if(functions.isValueNull(String.valueOf(bankwireId)) && !bankwireId.equals("0"))
            {
                query.append(" and bankwiremanagerId ="+ESAPI.encoder().encodeForSQL(me,bankwireId));
                countQuery.append(" and bankwiremanagerId ="+ESAPI.encoder().encodeForSQL(me,bankwireId));
            }

            query.append(" order by server_start_date DESC limit "+ paginationVO.getStart() + "," + paginationVO.getEnd());
            logger.debug("countQuery:-"+countQuery);
            logger.debug("Query:-"+query);

            preparedStatement=con.prepareStatement(query.toString());
            rs=preparedStatement.executeQuery();
            while(rs.next())
            {
                BankWireManagerVO bankWireManagerVO = new BankWireManagerVO();

                bankWireManagerVO.setBankwiremanagerId(rs.getString("bankwiremanagerId"));
                bankWireManagerVO.setSettleddate(Functions.checkStringNull(rs.getString("settleddate")) == null ? "" : rs.getString("settleddate"));
                bankWireManagerVO.setPgtypeId(rs.getString("pgtypeId"));
                bankWireManagerVO.setAccountId(rs.getString("accountId"));
                bankWireManagerVO.setMid(rs.getString("mid"));
                bankWireManagerVO.setBank_start_date(rs.getString("bank_start_date"));
                bankWireManagerVO.setBank_end_date(rs.getString("bank_end_date"));
                bankWireManagerVO.setServer_start_date(rs.getString("server_start_date"));
                bankWireManagerVO.setServer_end_date(rs.getString("server_end_date"));
                bankWireManagerVO.setProcessing_amount(rs.getString("processing_amount"));
                bankWireManagerVO.setGrossAmount(rs.getString("grossAmount"));
                bankWireManagerVO.setNetfinal_amount(rs.getString("netfinal_amount"));
                bankWireManagerVO.setUnpaid_amount(rs.getString("unpaid_amount"));
                bankWireManagerVO.setIsrollingreservereleasewire(rs.getString("isrollingreservereleasewire"));
                bankWireManagerVO.setRollingreservereleasedateupto(rs.getString("rollingreservereleasedateupto"));
                bankWireManagerVO.setDeclinedcoveredupto(rs.getString("declinedcoveredupto"));
                bankWireManagerVO.setChargebackcoveredupto(rs.getString("chargebackcoveredupto"));
                bankWireManagerVO.setReversedCoveredUpto(rs.getString("reversedCoveredUpto"));
                bankWireManagerVO.setBanksettlement_report_file(rs.getString("banksettlement_report_file"));
                bankWireManagerVO.setBanksettlement_transaction_file(rs.getString("banksettlement_transaction_file"));
                bankWireManagerVO.setSettlementCronExceuted(rs.getString("isSettlementCronExceuted"));
                bankWireManagerVO.setPayoutCronExcuted(rs.getString("isPayoutCronExcuted"));
                bankWireManagerVO.setIspaid(rs.getString("ispaid"));

                bankWireManagerVO.setDeclinedcoveredStartdate(rs.getString("declinedCoveredStartdate") == null ? "0000-00-00 00:00:00" : rs.getString("declinedCoveredStartdate"));
                bankWireManagerVO.setChargebackcoveredStartdate(rs.getString("chargebackCoveredStartdate") == null ? "0000-00-00 00:00:00" : rs.getString("chargebackCoveredStartdate"));
                bankWireManagerVO.setReversedCoveredStartdate(rs.getString("reversedCoveredStartdate") == null ? "0000-00-00 00:00:00" : rs.getString("reversedCoveredStartdate"));
                bankWireManagerVO.setRollingreservereleaseStartdate(rs.getString("rollingreserveStartdate") == null ? "0000-00-00 00:00:00" : rs.getString("rollingreserveStartdate"));

                bankWireManagerVOs.add(bankWireManagerVO);
            }
            //total records query
            preparedStatement=con.prepareStatement(countQuery.toString());
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                paginationVO.setTotalRecords(rs.getInt(1));
            }
        }
        catch (SQLException e)
        {
            logger.error(" bank wire list  Sql Exception::",e);
        }
        catch (SystemError systemError)
        {
            logger.error(" bank wire list  System error ::",systemError);
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return bankWireManagerVOs;
    }
    //getting single bank wire manager according to the action specified
    public BankWireManagerVO getSingleBankWireManagerActionSpecific(ActionVO actionVO)
    {
        Connection con = null;
        ResultSet rsSingleBankWireManager = null;
        PreparedStatement psSingleBankWireManager=null;
        BankWireManagerVO bankWireManagerVO = null;
        try
        {
            con = Database.getRDBConnection();
            String query = "Select * from `bank_wiremanager` where bankwiremanagerId =?";
            psSingleBankWireManager = con.prepareStatement(query.toString());
            psSingleBankWireManager.setString(1, actionVO.getActionCriteria());
            logger.error("Query:::"+psSingleBankWireManager);
            rsSingleBankWireManager = psSingleBankWireManager.executeQuery();
            if (rsSingleBankWireManager.next())
            {
                bankWireManagerVO = new BankWireManagerVO();
                bankWireManagerVO.setBankwiremanagerId(rsSingleBankWireManager.getString("bankwiremanagerId"));
                bankWireManagerVO.setSettleddate(Functions.checkStringNull(rsSingleBankWireManager.getString("settleddate")) == null ? "" : rsSingleBankWireManager.getString("settleddate"));
                bankWireManagerVO.setPgtypeId(rsSingleBankWireManager.getString("pgtypeId"));
                bankWireManagerVO.setAccountId(rsSingleBankWireManager.getString("accountId"));
                bankWireManagerVO.setMid(rsSingleBankWireManager.getString("mid"));
                bankWireManagerVO.setBank_start_date(Functions.checkStringNull(rsSingleBankWireManager.getString("bank_start_date")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("bank_start_date"));
                bankWireManagerVO.setBank_end_date(Functions.checkStringNull(rsSingleBankWireManager.getString("bank_end_date")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("bank_end_date"));
                bankWireManagerVO.setServer_start_date(Functions.checkStringNull(rsSingleBankWireManager.getString("server_start_date")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("server_start_date"));
                bankWireManagerVO.setServer_end_date(Functions.checkStringNull(rsSingleBankWireManager.getString("server_end_date")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("server_end_date"));
                bankWireManagerVO.setProcessing_amount(rsSingleBankWireManager.getString("processing_amount"));
                bankWireManagerVO.setGrossAmount(rsSingleBankWireManager.getString("grossAmount"));
                bankWireManagerVO.setNetfinal_amount(rsSingleBankWireManager.getString("netfinal_amount"));
                bankWireManagerVO.setUnpaid_amount(rsSingleBankWireManager.getString("unpaid_amount"));
                bankWireManagerVO.setCurrency(rsSingleBankWireManager.getString("currency"));
                bankWireManagerVO.setIsrollingreservereleasewire(rsSingleBankWireManager.getString("isrollingreservereleasewire"));
                bankWireManagerVO.setRollingreservereleasedateupto(Functions.checkValue(rsSingleBankWireManager.getString("rollingreservereleasedateupto")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("rollingreservereleasedateupto"));
                bankWireManagerVO.setDeclinedcoveredupto(Functions.checkStringNull(rsSingleBankWireManager.getString("declinedcoveredupto")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("declinedcoveredupto"));
                bankWireManagerVO.setChargebackcoveredupto(Functions.checkStringNull(rsSingleBankWireManager.getString("chargebackcoveredupto")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("chargebackcoveredupto"));
                bankWireManagerVO.setReversedCoveredUpto(Functions.checkStringNull(rsSingleBankWireManager.getString("reversedCoveredUpto")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("reversedCoveredUpto"));
                bankWireManagerVO.setBanksettlement_report_file(Functions.checkStringNull(rsSingleBankWireManager.getString("banksettlement_report_file")) == null ? "" : rsSingleBankWireManager.getString("banksettlement_report_file"));
                bankWireManagerVO.setBanksettlement_transaction_file(Functions.checkStringNull(rsSingleBankWireManager.getString("banksettlement_transaction_file")) == null ? "" : rsSingleBankWireManager.getString("banksettlement_transaction_file"));
                bankWireManagerVO.setSettlementCronExceuted(rsSingleBankWireManager.getString("isSettlementCronExceuted"));
                bankWireManagerVO.setPayoutCronExcuted(rsSingleBankWireManager.getString("isPayoutCronExcuted"));
                bankWireManagerVO.setIsPartnerCommCronExecuted(rsSingleBankWireManager.getString("isPartnerCommCronExecuted"));
                bankWireManagerVO.setIsAgentCommCronExecuted(rsSingleBankWireManager.getString("isAgentCommCronExecuted"));
                bankWireManagerVO.setIspaid(rsSingleBankWireManager.getString("ispaid"));

                bankWireManagerVO.setDeclinedcoveredStartdate(Functions.checkStringNull(rsSingleBankWireManager.getString("declinedCoveredStartdate")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("declinedCoveredStartdate"));
                bankWireManagerVO.setChargebackcoveredStartdate(Functions.checkStringNull(rsSingleBankWireManager.getString("chargebackCoveredStartdate")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("chargebackCoveredStartdate"));
                bankWireManagerVO.setReversedCoveredStartdate(Functions.checkStringNull(rsSingleBankWireManager.getString("reversedCoveredStartdate")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("reversedCoveredStartdate"));
                bankWireManagerVO.setRollingreservereleaseStartdate(Functions.checkStringNull(rsSingleBankWireManager.getString("rollingreserveStartdate")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("rollingreserveStartdate"));
                bankWireManagerVO.setParent_bankwireid(rsSingleBankWireManager.getString("parent_bankwireid"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System error for getSingleBankSettlementCycleActionSpecific::", systemError);
        }
        catch (SQLException e)
        {
            logger.error(" SQL Exception for getSingleBankSettlementCycleActionSpecific::", e);
        }
        finally {
            Database.closeResultSet(rsSingleBankWireManager);
            Database.closePreparedStatement(psSingleBankWireManager);
            Database.closeConnection(con);
        }
        return bankWireManagerVO;
    }

    public boolean updateBankWireManager(BankWireManagerVO bankWireManagerVO)
    {
        Connection con = null;
        PreparedStatement psupdateBankWireManager=null;

        StringBuffer update1 = null;
        try
        {
            con=Database.getConnection();
//            String update="update `bank_wiremanager` set settleddate =?,pgtypeId =?,accountId =?,mid =?,bank_start_date =?,bank_end_date =?,server_start_date =?,server_end_date =?,processing_amount =?,grossAmount =?,netfinal_amount =?,unpaid_amount=?,isrollingreservereleasewire =?,rollingreservereleasedateupto =?,declinedcoveredupto =?,chargebackcoveredupto =?,reversedCoveredUpto =?,banksettlement_report_file =?,banksettlement_transaction_file=?,isSettlementCronExceuted =?,isPayoutCronExcuted =?,isPartnerCommCronExecuted=?,isAgentCommCronExecuted=?,ispaid =?,declinedCoveredStartdate=?,chargebackCoveredStartdate=?,reversedCoveredStartdate=?,rollingreserveStartdate=?, parent_bankwireid=? where bankwiremanagerId =?";
            update1 = new StringBuffer("update `bank_wiremanager` set settleddate =?,bank_start_date =?,bank_end_date =?,server_start_date =?,server_end_date =?,processing_amount =?,grossAmount =?,netfinal_amount =?,unpaid_amount=?,isrollingreservereleasewire =?,rollingreservereleasedateupto =?,declinedcoveredupto =?,chargebackcoveredupto =?,reversedCoveredUpto =?,banksettlement_report_file =?,banksettlement_transaction_file=?,isSettlementCronExceuted =?,isPayoutCronExcuted =?,isPartnerCommCronExecuted=?,isAgentCommCronExecuted=?,ispaid =?,declinedCoveredStartdate=?,chargebackCoveredStartdate=?,reversedCoveredStartdate=?,rollingreserveStartdate=?, pgtypeId =?,accountId =?,mid =? where bankwiremanagerId =? ");

            psupdateBankWireManager = con.prepareStatement(update1.toString());
            psupdateBankWireManager.setString(1,bankWireManagerVO.getSettleddate());
            psupdateBankWireManager.setString(2,bankWireManagerVO.getBank_start_date());
            psupdateBankWireManager.setString(3,bankWireManagerVO.getBank_end_date());
            psupdateBankWireManager.setString(4,bankWireManagerVO.getServer_start_date());
            psupdateBankWireManager.setString(5,bankWireManagerVO.getServer_end_date());
            psupdateBankWireManager.setString(6,bankWireManagerVO.getProcessing_amount());
            psupdateBankWireManager.setString(7,bankWireManagerVO.getGrossAmount());
            psupdateBankWireManager.setString(8,bankWireManagerVO.getNetfinal_amount());
            psupdateBankWireManager.setString(9,bankWireManagerVO.getUnpaid_amount());
            psupdateBankWireManager.setString(10,bankWireManagerVO.getIsrollingreservereleasewire());
            psupdateBankWireManager.setString(11,bankWireManagerVO.getRollingreservereleasedateupto());
            psupdateBankWireManager.setString(12,bankWireManagerVO.getDeclinedcoveredupto());
            psupdateBankWireManager.setString(13,bankWireManagerVO.getChargebackcoveredupto());
            psupdateBankWireManager.setString(14,bankWireManagerVO.getReversedCoveredUpto());
            psupdateBankWireManager.setString(15,bankWireManagerVO.getBanksettlement_report_file());
            psupdateBankWireManager.setString(16,bankWireManagerVO.getBanksettlement_transaction_file());
            psupdateBankWireManager.setString(17,bankWireManagerVO.getSettlementCronExceuted());
            psupdateBankWireManager.setString(18,bankWireManagerVO.getPayoutCronExcuted());
            psupdateBankWireManager.setString(19,bankWireManagerVO.getIsPartnerCommCronExecuted());
            psupdateBankWireManager.setString(20,bankWireManagerVO.getIsAgentCommCronExecuted());
            psupdateBankWireManager.setString(21,bankWireManagerVO.getIspaid());
            psupdateBankWireManager.setString(22,bankWireManagerVO.getDeclinedcoveredStartdate());
            psupdateBankWireManager.setString(23, bankWireManagerVO.getChargebackcoveredStartdate());
            psupdateBankWireManager.setString(24, bankWireManagerVO.getReversedCoveredStartdate());
            psupdateBankWireManager.setString(25,bankWireManagerVO.getRollingreservereleaseStartdate());
            psupdateBankWireManager.setString(26,bankWireManagerVO.getPgtypeId());
            psupdateBankWireManager.setString(27,bankWireManagerVO.getAccountId());
            psupdateBankWireManager.setString(28,bankWireManagerVO.getMid());
            psupdateBankWireManager.setString(29, bankWireManagerVO.getBankwiremanagerId());

            int rss=psupdateBankWireManager.executeUpdate();
            logger.info("query====>"+psupdateBankWireManager);
            if(rss>0)
            {
                String qry = "Select bankwiremanagerId from bank_wiremanager where parent_bankwireid=?";
                PreparedStatement pstm = con.prepareStatement(qry);
                pstm.setInt(1, Integer.parseInt(bankWireManagerVO.getBankwiremanagerId()));
                ResultSet rs = pstm.executeQuery();
                if (rs.next()){

                    update1 = new StringBuffer("update `bank_wiremanager` set settleddate =?,bank_start_date =?,bank_end_date =?,server_start_date =?,server_end_date =?,processing_amount =?,grossAmount =?,netfinal_amount =?,unpaid_amount=?,isrollingreservereleasewire =?,rollingreservereleasedateupto =?,declinedcoveredupto =?,chargebackcoveredupto =?,reversedCoveredUpto =?,banksettlement_report_file =?,banksettlement_transaction_file=?,isSettlementCronExceuted =?,isPayoutCronExcuted =?,isPartnerCommCronExecuted=?,isAgentCommCronExecuted=?,ispaid =?,declinedCoveredStartdate=?,chargebackCoveredStartdate=?,reversedCoveredStartdate=?,rollingreserveStartdate=? where parent_bankwireid =?");

                    psupdateBankWireManager = con.prepareStatement(update1.toString());
                    psupdateBankWireManager.setString(1,bankWireManagerVO.getSettleddate());
                    psupdateBankWireManager.setString(2,bankWireManagerVO.getBank_start_date());
                    psupdateBankWireManager.setString(3,bankWireManagerVO.getBank_end_date());
                    psupdateBankWireManager.setString(4,bankWireManagerVO.getServer_start_date());
                    psupdateBankWireManager.setString(5,bankWireManagerVO.getServer_end_date());
                    psupdateBankWireManager.setString(6,bankWireManagerVO.getProcessing_amount());
                    psupdateBankWireManager.setString(7,bankWireManagerVO.getGrossAmount());
                    psupdateBankWireManager.setString(8,bankWireManagerVO.getNetfinal_amount());
                    psupdateBankWireManager.setString(9,bankWireManagerVO.getUnpaid_amount());
                    psupdateBankWireManager.setString(10,bankWireManagerVO.getIsrollingreservereleasewire());
                    psupdateBankWireManager.setString(11,bankWireManagerVO.getRollingreservereleasedateupto());
                    psupdateBankWireManager.setString(12,bankWireManagerVO.getDeclinedcoveredupto());
                    psupdateBankWireManager.setString(13,bankWireManagerVO.getChargebackcoveredupto());
                    psupdateBankWireManager.setString(14,bankWireManagerVO.getReversedCoveredUpto());
                    psupdateBankWireManager.setString(15,bankWireManagerVO.getBanksettlement_report_file());
                    psupdateBankWireManager.setString(16,bankWireManagerVO.getBanksettlement_transaction_file());
                    psupdateBankWireManager.setString(17,bankWireManagerVO.getSettlementCronExceuted());
                    psupdateBankWireManager.setString(18,bankWireManagerVO.getPayoutCronExcuted());
                    psupdateBankWireManager.setString(19,bankWireManagerVO.getIsPartnerCommCronExecuted());
                    psupdateBankWireManager.setString(20,bankWireManagerVO.getIsAgentCommCronExecuted());
                    psupdateBankWireManager.setString(21,bankWireManagerVO.getIspaid());
                    psupdateBankWireManager.setString(22,bankWireManagerVO.getDeclinedcoveredStartdate());
                    psupdateBankWireManager.setString(23, bankWireManagerVO.getChargebackcoveredStartdate());
                    psupdateBankWireManager.setString(24, bankWireManagerVO.getReversedCoveredStartdate());
                    psupdateBankWireManager.setString(25,bankWireManagerVO.getRollingreservereleaseStartdate());
                    psupdateBankWireManager.setString(26, bankWireManagerVO.getBankwiremanagerId());

                    int rss1=psupdateBankWireManager.executeUpdate();
                    logger.info("query====>"+psupdateBankWireManager);
                    if(rss1>0)
                    {
                        return true;
                    }
                }
                return true;
            }

        }
        catch (SystemError systemError)
        {
            logger.error(" System error for updateBankMerchantSettlement",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL exception for updateBankMerchantSettlement",e);
        }
        finally
        {
            Database.closePreparedStatement(psupdateBankWireManager);
            Database.closeConnection(con);
        }
        return false;
    }

    public boolean insertNewBankWireManager(BankWireManagerVO bankWireManagerVO)
    {
        Connection con = null;
        PreparedStatement psInsertBankWireManager=null;
        try
        {
            con=Database.getConnection();
            String insert = "insert into `bank_wiremanager` (settleddate,pgtypeId,accountId,mid,bank_start_date,bank_end_date,server_start_date,server_end_date,processing_amount,grossAmount,netfinal_amount,unpaid_amount,currency,isrollingreservereleasewire,rollingreservereleasedateupto,declinedcoveredupto,chargebackcoveredupto,reversedCoveredUpto,banksettlement_report_file,banksettlement_transaction_file,ispaid,declinedCoveredStartdate,chargebackCoveredStartdate,reversedCoveredStartdate,rollingreserveStartdate,parent_bankwireid) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            psInsertBankWireManager = con.prepareStatement(insert.toString());
            psInsertBankWireManager.setString(1,bankWireManagerVO.getSettleddate());
            psInsertBankWireManager.setString(2,bankWireManagerVO.getPgtypeId());
            psInsertBankWireManager.setString(3,bankWireManagerVO.getAccountId());
            psInsertBankWireManager.setString(4,bankWireManagerVO.getMid());
            psInsertBankWireManager.setString(5,bankWireManagerVO.getBank_start_date());
            psInsertBankWireManager.setString(6,bankWireManagerVO.getBank_end_date());
            psInsertBankWireManager.setString(7,bankWireManagerVO.getServer_start_date());
            psInsertBankWireManager.setString(8,bankWireManagerVO.getServer_end_date());
            psInsertBankWireManager.setString(9,bankWireManagerVO.getProcessing_amount());
            psInsertBankWireManager.setString(10,bankWireManagerVO.getGrossAmount());
            psInsertBankWireManager.setString(11,bankWireManagerVO.getNetfinal_amount());
            psInsertBankWireManager.setString(12,bankWireManagerVO.getUnpaid_amount());
            psInsertBankWireManager.setString(13,bankWireManagerVO.getCurrency());
            psInsertBankWireManager.setString(14,bankWireManagerVO.getIsrollingreservereleasewire());
            psInsertBankWireManager.setString(15,bankWireManagerVO.getRollingreservereleasedateupto());
            psInsertBankWireManager.setString(16,bankWireManagerVO.getDeclinedcoveredupto());
            psInsertBankWireManager.setString(17,bankWireManagerVO.getChargebackcoveredupto());
            psInsertBankWireManager.setString(18,bankWireManagerVO.getReversedCoveredUpto());
            psInsertBankWireManager.setString(19,bankWireManagerVO.getBanksettlement_report_file());
            psInsertBankWireManager.setString(20,bankWireManagerVO.getBanksettlement_transaction_file());
            psInsertBankWireManager.setString(21,bankWireManagerVO.getIspaid());
            psInsertBankWireManager.setString(22,bankWireManagerVO.getDeclinedcoveredStartdate());
            psInsertBankWireManager.setString(23,bankWireManagerVO.getChargebackcoveredStartdate());
            psInsertBankWireManager.setString(24,bankWireManagerVO.getReversedCoveredStartdate());
            psInsertBankWireManager.setString(25,bankWireManagerVO.getRollingreservereleaseStartdate());
            if (functions.isValueNull(bankWireManagerVO.getParent_bankwireid())){
                psInsertBankWireManager.setString(26,bankWireManagerVO.getParent_bankwireid());}
            else{
                psInsertBankWireManager.setString(26,bankWireManagerVO.getBankwiremanagerId());
            }
            logger.error("query----"+psInsertBankWireManager);
            int rss=psInsertBankWireManager.executeUpdate();
            logger.error("rss----"+rss);
            if(rss>0)
            {
                return true;
            }
        }
        catch (SystemError systemError)
        {
            logger.error(" System Error for insertNewBankMerchantSettlement",systemError);
        }
        catch (SQLException e)
        {
            logger.error(" Sql exception for insertNewBankMerchantSettlement",e);
        }
        finally
        {
            Database.closePreparedStatement(psInsertBankWireManager);
            Database.closeConnection(con);
        }
        return false;
    }

    public int insertNewBankWireManagerNew(BankWireManagerVO bankWireManagerVO)
    {
        Connection con = null;
        int bankwireId = 0;
        PreparedStatement psInsertBankWireManager=null;
        try
        {
            con = Database.getConnection();
            String insert = "insert into `bank_wiremanager` (settleddate,pgtypeId,accountId,mid,bank_start_date,bank_end_date,server_start_date,server_end_date,processing_amount,grossAmount,netfinal_amount,unpaid_amount,currency,isrollingreservereleasewire,rollingreservereleasedateupto,declinedcoveredupto,chargebackcoveredupto,reversedCoveredUpto,banksettlement_report_file,banksettlement_transaction_file,ispaid,settlement_cycle_id) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            psInsertBankWireManager = con.prepareStatement(insert.toString());
            psInsertBankWireManager.setString(1, bankWireManagerVO.getSettleddate());
            psInsertBankWireManager.setString(2, bankWireManagerVO.getPgtypeId());
            psInsertBankWireManager.setString(3, bankWireManagerVO.getAccountId());
            psInsertBankWireManager.setString(4, bankWireManagerVO.getMid());
            psInsertBankWireManager.setString(5, bankWireManagerVO.getBank_start_date());
            psInsertBankWireManager.setString(6, bankWireManagerVO.getBank_end_date());
            psInsertBankWireManager.setString(7, bankWireManagerVO.getServer_start_date());
            psInsertBankWireManager.setString(8, bankWireManagerVO.getServer_end_date());
            psInsertBankWireManager.setString(9, bankWireManagerVO.getProcessing_amount());
            psInsertBankWireManager.setString(10, bankWireManagerVO.getGrossAmount());
            psInsertBankWireManager.setString(11, bankWireManagerVO.getNetfinal_amount());
            psInsertBankWireManager.setString(12, bankWireManagerVO.getUnpaid_amount());
            psInsertBankWireManager.setString(13, bankWireManagerVO.getCurrency());
            psInsertBankWireManager.setString(14, bankWireManagerVO.getIsrollingreservereleasewire());
            psInsertBankWireManager.setString(15, bankWireManagerVO.getRollingreservereleasedateupto());
            psInsertBankWireManager.setString(16, bankWireManagerVO.getDeclinedcoveredupto());
            psInsertBankWireManager.setString(17, bankWireManagerVO.getChargebackcoveredupto());
            psInsertBankWireManager.setString(18, bankWireManagerVO.getReversedCoveredUpto());
            psInsertBankWireManager.setString(19, bankWireManagerVO.getBanksettlement_report_file());
            psInsertBankWireManager.setString(20, bankWireManagerVO.getBanksettlement_transaction_file());
            psInsertBankWireManager.setString(21, bankWireManagerVO.getIspaid());
            psInsertBankWireManager.setString(22, bankWireManagerVO.getSettlementCycleId());
            int rss = psInsertBankWireManager.executeUpdate();
            if (rss > 0)
            {
                ResultSet rs = psInsertBankWireManager.getGeneratedKeys();
                if (rs.next())
                {
                    bankwireId = rs.getInt(1);
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error(" System Error for insertNewBankMerchantSettlement", systemError);
        }
        catch (SQLException e)
        {
            logger.error(" Sql exception for insertNewBankMerchantSettlement", e);
        }
        finally
        {
            Database.closePreparedStatement(psInsertBankWireManager);
            Database.closeConnection(con);
        }
        return bankwireId;
    }
    public boolean isRollingReserveAvailable(String accountId) throws SystemError,SQLException
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet resultSet=null;
        boolean status=false;
        try
        {
            String query = "select accountid from bank_rollingreserve_master where accountid=?";
            con=Database.getRDBConnection();
            pstmt=con.prepareStatement(query);
            pstmt.setString(1,accountId);
            resultSet=pstmt.executeQuery();
            if(resultSet.next())
            {
                status=true;
            }
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return status;
    }
    public BankRollingReserveVO getBankRollingReserveForAction(String mappingId)throws SystemError,SQLException
    {
        Connection con = null;
        ResultSet resultSet = null;
        PreparedStatement psSingleBankRollingReserve=null;
        BankRollingReserveVO bankRollingReserveVO = null;
        try
        {
            con = Database.getRDBConnection();
            String query = "select * from `bank_rollingreserve_master` where id =?";
            psSingleBankRollingReserve = con.prepareStatement(query.toString());
            psSingleBankRollingReserve.setString(1,mappingId);
            resultSet = psSingleBankRollingReserve.executeQuery();
            if (resultSet.next())
            {
                bankRollingReserveVO = new BankRollingReserveVO();
                bankRollingReserveVO.setBankRollingReserveId(resultSet.getString("id"));
                bankRollingReserveVO.setAccountId(resultSet.getString("accountid"));
                bankRollingReserveVO.setRollingReserveDateUpTo(resultSet.getString("rollingreservereleaseupto"));
            }
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(psSingleBankRollingReserve);
            Database.closeConnection(con);
        }
        return bankRollingReserveVO;
    }

    public List<BankRollingReserveVO> getBankRollingReserveHistory(String accountId)throws SystemError,SQLException
    {
        Connection con = null;
        ResultSet resultSet = null;
        PreparedStatement psSingleBankRollingReserve=null;
        List<BankRollingReserveVO> bankRollingReserveVOs=new ArrayList<BankRollingReserveVO>();
        BankRollingReserveVO bankRollingReserveVO = null;
        try
        {
            con = Database.getRDBConnection();
            String query = "select bank_rrhistoryid,accountid,merchantid,rollingreservereleaseupto from bank_rollingreserve_history where accountid=?";
            psSingleBankRollingReserve = con.prepareStatement(query.toString());
            psSingleBankRollingReserve.setString(1,accountId);
            resultSet = psSingleBankRollingReserve.executeQuery();
            while (resultSet.next())
            {
                bankRollingReserveVO = new BankRollingReserveVO();
                bankRollingReserveVO.setBankRollingReserveId(resultSet.getString("bank_rrhistoryid"));
                bankRollingReserveVO.setAccountId(resultSet.getString("accountid"));
                bankRollingReserveVO.setMerchantId(resultSet.getString("merchantid"));
                bankRollingReserveVO.setBankRollingReserveDateTime(resultSet.getString("rollingreservereleaseupto"));
                bankRollingReserveVOs.add(bankRollingReserveVO);
            }
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(psSingleBankRollingReserve);
            Database.closeConnection(con);
        }
        return bankRollingReserveVOs;
    }
    public TreeMap<String, BankWireManagerVO> getBankWiresForRandomCharges()
    {
        Connection conn = null;
        TreeMap<String,BankWireManagerVO> bankWireMap= new TreeMap();
        BankWireManagerVO bankWireManagerVO=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            String query ="SELECT bankwiremanagerId,accountid,MID FROM bank_wiremanager WHERE isSettlementCronExceuted='Y' and isPayoutCronExcuted='N' ORDER BY bankwiremanagerId DESC";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                bankWireManagerVO=new BankWireManagerVO();
                bankWireManagerVO.setBankwiremanagerId(rs.getString("bankwiremanagerId"));
                bankWireManagerVO.setAccountId(rs.getString("accountid"));
                bankWireManagerVO.setMid(rs.getString("MID"));
                bankWireMap.put(rs.getString("bankwiremanagerId"),bankWireManagerVO);
            }
        }
        catch (SystemError e)
        {
            logger.error("SystemError:::::",e);
        }
        catch (SQLException e)
        {
            logger.error("SystemError:::::",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return bankWireMap;
    }

    public int updateSettlementCronExecutedFlag(String bankWireId)
    {
        int result = 0;
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        try
        {
            String query = "update bank_wiremanager set isSettlementCronExceuted='Y' where bankwiremanagerId=?";
            conn = Database.getConnection();
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, bankWireId);
            result = preparedStatement.executeUpdate();
        }
        catch (Exception e){
            logger.error("Exception:::::"+e);
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return result;
    }

    public int updateTheStatus(String settlementCycleId, String status)
    {
        int result = 0;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try
        {
            String query = "update merchant_settlement_cycle_master set status=? where id=?";
            conn = Database.getConnection();
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, status);
            preparedStatement.setString(2, settlementCycleId);
            result = preparedStatement.executeUpdate();
        }
        catch (Exception e)
        {
            logger.error("Exception:::::" + e);
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return result;
    }
    public int updateTheStatusDelete(String settlementCycleId, String status)
    {
        int result = 0;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try
        {
            String query = "update merchant_settlement_cycle_master set status=? where cycleId=?";
            conn = Database.getConnection();
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, status);
            preparedStatement.setString(2, settlementCycleId);
            result = preparedStatement.executeUpdate();
        }
        catch (Exception e)
        {
            logger.error("Exception:::::" + e);
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return result;
    }
    public int updateTheStatusBankWire(String settlementCycleId,String bankWireId, String status)
    {
        int result = 0;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try
        {
            String query = "update merchant_settlement_cycle_master set status=?,cycleId=? where id=?";
            conn = Database.getConnection();
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, status);
            preparedStatement.setString(2, bankWireId);
            preparedStatement.setString(3, settlementCycleId);
            result = preparedStatement.executeUpdate();
        }
        catch (Exception e)
        {
            logger.error("Exception:::::" + e);
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return result;
    }

    public int removeTheBankWire(String bankWireId)
    {
        int result = 0;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try
        {
            String query = "delete from bank_wiremanager where bankwiremanagerId=?";
            conn = Database.getConnection();
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, bankWireId);
            result = preparedStatement.executeUpdate();
        }
        catch (Exception e)
        {
            logger.error("Exception:::::" + e);
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return result;
    }

    //getting single bank wire manager according to the action specified
    public BankWireManagerVO getSingleBankWireManagerActionSpecificBasedOnSettlementCycle(String settlementCycleId)
    {
        Connection con = null;
        ResultSet rsSingleBankWireManager = null;
        PreparedStatement psSingleBankWireManager=null;
        BankWireManagerVO bankWireManagerVO = null;
        try
        {
            con = Database.getRDBConnection();
            String query = "Select * from `bank_wiremanager` where settlement_cycle_id =?";
            psSingleBankWireManager = con.prepareStatement(query.toString());
            psSingleBankWireManager.setString(1, settlementCycleId);
            rsSingleBankWireManager = psSingleBankWireManager.executeQuery();
            if (rsSingleBankWireManager.next())
            {
                bankWireManagerVO = new BankWireManagerVO();
                bankWireManagerVO.setBankwiremanagerId(rsSingleBankWireManager.getString("bankwiremanagerId"));
                bankWireManagerVO.setSettleddate(Functions.checkStringNull(rsSingleBankWireManager.getString("settleddate")) == null ? "" : rsSingleBankWireManager.getString("settleddate"));
                bankWireManagerVO.setPgtypeId(rsSingleBankWireManager.getString("pgtypeId"));
                bankWireManagerVO.setAccountId(rsSingleBankWireManager.getString("accountId"));
                bankWireManagerVO.setMid(rsSingleBankWireManager.getString("mid"));
                bankWireManagerVO.setBank_start_date(Functions.checkStringNull(rsSingleBankWireManager.getString("bank_start_date")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("bank_start_date"));
                bankWireManagerVO.setBank_end_date(Functions.checkStringNull(rsSingleBankWireManager.getString("bank_end_date")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("bank_end_date"));
                bankWireManagerVO.setServer_start_date(Functions.checkStringNull(rsSingleBankWireManager.getString("server_start_date")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("server_start_date"));
                bankWireManagerVO.setServer_end_date(Functions.checkStringNull(rsSingleBankWireManager.getString("server_end_date")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("server_end_date"));
                bankWireManagerVO.setProcessing_amount(rsSingleBankWireManager.getString("processing_amount"));
                bankWireManagerVO.setGrossAmount(rsSingleBankWireManager.getString("grossAmount"));
                bankWireManagerVO.setNetfinal_amount(rsSingleBankWireManager.getString("netfinal_amount"));
                bankWireManagerVO.setUnpaid_amount(rsSingleBankWireManager.getString("unpaid_amount"));
                bankWireManagerVO.setCurrency(rsSingleBankWireManager.getString("currency"));
                bankWireManagerVO.setIsrollingreservereleasewire(rsSingleBankWireManager.getString("isrollingreservereleasewire"));
                bankWireManagerVO.setRollingreservereleasedateupto(Functions.checkValue(rsSingleBankWireManager.getString("rollingreservereleasedateupto")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("rollingreservereleasedateupto"));
                bankWireManagerVO.setDeclinedcoveredupto(Functions.checkStringNull(rsSingleBankWireManager.getString("declinedcoveredupto")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("declinedcoveredupto"));
                bankWireManagerVO.setChargebackcoveredupto(Functions.checkStringNull(rsSingleBankWireManager.getString("chargebackcoveredupto")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("chargebackcoveredupto"));
                bankWireManagerVO.setReversedCoveredUpto(Functions.checkStringNull(rsSingleBankWireManager.getString("reversedCoveredUpto")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("reversedCoveredUpto"));
                bankWireManagerVO.setBanksettlement_report_file(Functions.checkStringNull(rsSingleBankWireManager.getString("banksettlement_report_file")) == null ? "" : rsSingleBankWireManager.getString("banksettlement_report_file"));
                bankWireManagerVO.setBanksettlement_transaction_file(Functions.checkStringNull(rsSingleBankWireManager.getString("banksettlement_transaction_file")) == null ? "" : rsSingleBankWireManager.getString("banksettlement_transaction_file"));
                bankWireManagerVO.setSettlementCronExceuted(rsSingleBankWireManager.getString("isSettlementCronExceuted"));
                bankWireManagerVO.setPayoutCronExcuted(rsSingleBankWireManager.getString("isPayoutCronExcuted"));
                bankWireManagerVO.setIsPartnerCommCronExecuted(rsSingleBankWireManager.getString("isPartnerCommCronExecuted"));
                bankWireManagerVO.setIsAgentCommCronExecuted(rsSingleBankWireManager.getString("isAgentCommCronExecuted"));
                bankWireManagerVO.setIspaid(rsSingleBankWireManager.getString("ispaid"));

                bankWireManagerVO.setRollingreservereleaseStartdate(Functions.checkValue(rsSingleBankWireManager.getString("rollingreserveStartdate")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("rollingreserveStartdate"));
                bankWireManagerVO.setDeclinedcoveredStartdate(Functions.checkStringNull(rsSingleBankWireManager.getString("declinedCoveredStartdate")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("declinedCoveredStartdate"));
                bankWireManagerVO.setChargebackcoveredStartdate(Functions.checkStringNull(rsSingleBankWireManager.getString("chargebackCoveredStartdate")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("chargebackCoveredStartdate"));
                bankWireManagerVO.setReversedCoveredStartdate(Functions.checkStringNull(rsSingleBankWireManager.getString("reversedCoveredStartdate")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("reversedCoveredStartdate"));

            }

        }
        catch (SystemError systemError)
        {
            logger.error("System error for getSingleBankSettlementCycleActionSpecific::", systemError);
        }
        catch (SQLException e)
        {
            logger.error(" SQL Exception for getSingleBankSettlementCycleActionSpecific::", e);
        }
        finally
        {
            Database.closeResultSet(rsSingleBankWireManager);
            Database.closePreparedStatement(psSingleBankWireManager);
            Database.closeConnection(con);
        }
        return bankWireManagerVO;
    }

    public BankWireManagerVO getPreviousWireDetails(String accountId)
    {
        Connection con = null;
        ResultSet rsSingleBankWireManager = null;
        PreparedStatement psSingleBankWireManager=null;
        BankWireManagerVO bankWireManagerVO = null;
        try
        {
            con = Database.getRDBConnection();
//            String query = "SELECT bankwiremanagerId,bank_end_date,rollingreservereleasedateupto,declinedcoveredupto,chargebackcoveredupto,reversedCoveredUpto, parent_bankwireid, currency FROM bank_wiremanager  WHERE accountid IN ( '"+accountId+"') ORDER BY bankwiremanagerId DESC limit 1";
            String query = "SELECT bm.bankwiremanagerId,bm.bank_end_date,bm.rollingreservereleasedateupto,bm.declinedcoveredupto,bm.chargebackcoveredupto,bm.reversedCoveredUpto, \n" +
                    "bm.parent_bankwireid, gt.currency FROM bank_wiremanager AS bm JOIN gateway_type AS gt WHERE bm.pgTypeId = gt.pgTypeId AND bm.pgTypeId = (SELECT pgtypeid FROM gateway_accounts WHERE bm.accountId = accountId AND accountid IN ('"+accountId+"') ) ORDER BY bm.bankwiremanagerId DESC LIMIT 1";
            psSingleBankWireManager = con.prepareStatement(query.toString());
//            psSingleBankWireManager.setString(1, accountId);
            rsSingleBankWireManager = psSingleBankWireManager.executeQuery();
            logger.error("query----"+psSingleBankWireManager);
            if (rsSingleBankWireManager.next())
            {
                bankWireManagerVO = new BankWireManagerVO();
                bankWireManagerVO.setBankwiremanagerId(rsSingleBankWireManager.getString("bm.bankwiremanagerId"));
                bankWireManagerVO.setBank_end_date(Functions.checkStringNull(rsSingleBankWireManager.getString("bm.bank_end_date")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("bm.bank_end_date"));
                bankWireManagerVO.setRollingreservereleasedateupto(Functions.checkStringNull(rsSingleBankWireManager.getString("bm.rollingreservereleasedateupto")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("bm.rollingreservereleasedateupto"));
                bankWireManagerVO.setDeclinedcoveredupto(Functions.checkStringNull(rsSingleBankWireManager.getString("bm.declinedcoveredupto")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("bm.declinedcoveredupto"));
                bankWireManagerVO.setChargebackcoveredupto(Functions.checkStringNull(rsSingleBankWireManager.getString("bm.chargebackcoveredupto")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("bm.chargebackcoveredupto"));
                bankWireManagerVO.setReversedCoveredUpto(Functions.checkStringNull(rsSingleBankWireManager.getString("bm.reversedCoveredUpto")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("bm.reversedCoveredUpto"));
                bankWireManagerVO.setParent_bankwireid(Functions.checkStringNull(rsSingleBankWireManager.getString("bm.parent_bankwireid")) == null ? "" : rsSingleBankWireManager.getString("bm.parent_bankwireid"));
                bankWireManagerVO.setCurrency(Functions.checkStringNull(rsSingleBankWireManager.getString("gt.currency")) == null ? "" : rsSingleBankWireManager.getString("gt.currency"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System error for getSingleBankSettlementCycleActionSpecific::", systemError);
        }
        catch (SQLException e)
        {
            logger.error(" SQL Exception for getSingleBankSettlementCycleActionSpecific::", e);
        }
        finally
        {
            Database.closeResultSet(rsSingleBankWireManager);
            Database.closePreparedStatement(psSingleBankWireManager);
            Database.closeConnection(con);
        }
        return bankWireManagerVO;
    }

    public String getFirstTransactionDate(String accountId)
    {
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement preparedStatement = null;
        String firstTransactionDate = null;
        try
        {
            String query = "SELECT MIN(trackingid),FROM_UNIXTIME(dtstamp) AS firsttranssactiondate FROM transaction_common WHERE accountid=?";
            conn = Database.getConnection();
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, accountId);
            result = preparedStatement.executeQuery();
            if (result.next())
            {
                firstTransactionDate = result.getString("firsttranssactiondate");
            }
        }
        catch (Exception e)
        {
            logger.error("Exception:::::" + e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return firstTransactionDate;
    }

    public TreeMap<String, BankWireManagerVO> getBankWiresForAgent(String accountId)
    {
        Connection conn = null;
        TreeMap<String,BankWireManagerVO> bankWireMap= new TreeMap();
        BankWireManagerVO bankWireManagerVO=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        StringBuffer stringBuffer=new StringBuffer();
        try
        {
            conn = Database.getRDBConnection();
            stringBuffer.append("SELECT bankwiremanagerId,accountid,MID FROM bank_wiremanager WHERE isSettlementCronExceuted='Y' and isPayoutCronExcuted='Y' and isAgentCommCronExecuted='N'");
            if(functions.isValueNull(accountId)){
                stringBuffer.append("and accountid="+accountId);
            }
            stringBuffer.append(" ORDER BY bankwiremanagerId DESC");
            pstmt = conn.prepareStatement(stringBuffer.toString());
            logger.error("query::::"+pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                bankWireManagerVO=new BankWireManagerVO();
                bankWireManagerVO.setBankwiremanagerId(rs.getString("bankwiremanagerId"));
                bankWireManagerVO.setAccountId(rs.getString("accountid"));
                bankWireManagerVO.setMid(rs.getString("MID"));
                bankWireMap.put(rs.getString("bankwiremanagerId"),bankWireManagerVO);
            }
        }
        catch (SystemError e)
        {
            logger.error("SystemError:::::",e);
        }
        catch (SQLException e)
        {
            logger.error("SystemError:::::",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return bankWireMap;
    }

    public List<TerminalVO> getPartnerWireDetails(String partnerId)
    {
        List<TerminalVO> terminalVOList=new ArrayList<>();
        TerminalVO terminalVO=null;
        Connection con=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        try {
            con=Database.getRDBConnection();
            StringBuffer query=new StringBuffer("SELECT DISTINCT bw.bankwiremanagerId,bw.accountId,bw.mid FROM bank_wiremanager AS bw JOIN gateway_account_partner_mapping AS gt WHERE gt.accountid=bw.accountId AND bw.isSettlementCronExceuted='Y' AND bw.isPartnerCommCronExecuted='N' AND gt.partnerid IN("+partnerId+")");
            preparedStatement=con.prepareStatement(query.toString());
            resultSet=preparedStatement.executeQuery();
            logger.error("Query::::"+preparedStatement);
            while (resultSet.next()){
                terminalVO=new TerminalVO();
                terminalVO.setWireId(resultSet.getString("bankwiremanagerId"));
                terminalVO.setAccountId(resultSet.getString("accountId"));
                terminalVO.setMemberId(resultSet.getString("mid"));
                terminalVOList.add(terminalVO);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError",systemError);
        }
        catch (SQLException se){
            logger.error("SQLException",se);
        }
        return terminalVOList;
    }
    public String getListOfAccountIdMappedWithPartner(String partnerId)
    {
        Connection conn=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String accountList="0";
        try
        {
            conn = Database.getRDBConnection();
            StringBuilder query = new StringBuilder("SELECT gt.pgtypeid,gt.gateway,gt.currency,gt.name,ga.accountid as accountid,ga.merchantid,gapm.partnerid FROM gateway_type AS gt JOIN gateway_accounts AS ga JOIN gateway_account_partner_mapping AS gapm WHERE gt.pgtypeid=ga.pgtypeid AND ga.accountid=gapm.accountid AND gapm.partnerid IN("+partnerId+")");
            pstmt=conn.prepareStatement(query.toString());
            logger.error("GetListOfAccountId::::"+pstmt);
            rs=pstmt.executeQuery();
            while (rs.next())
            {
                accountList+=","+rs.getString("accountid");
            }
        }catch (Exception e){
            logger.error("Exception::::",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return accountList;
    }
    public BankWireManagerVO getBankWireDetailsWithParentBankID(String parentbankwireid)
    {
        Connection con = null;
        ResultSet rsSingleBankWireManager = null;
        PreparedStatement psSingleBankWireManager=null;
        BankWireManagerVO bankWireManagerVO = null;
        String id ="";
        try
        {
            con = Database.getRDBConnection();

            if (functions.isValueNull(parentbankwireid))
            {
                String query = "SELECT bankwiremanagerId  FROM bank_wiremanager  WHERE parent_bankwireid=? ORDER BY bankwiremanagerId DESC limit 1";
                psSingleBankWireManager = con.prepareStatement(query.toString());
                psSingleBankWireManager.setString(1, parentbankwireid);
                rsSingleBankWireManager = psSingleBankWireManager.executeQuery();
                logger.error("pstm======>"+psSingleBankWireManager);
                if (rsSingleBankWireManager.next())
                {
                    id = rsSingleBankWireManager.getString("bankwiremanagerId");
                }
                else if (!functions.isValueNull(id)){
                  id = parentbankwireid;
                }
            }
            if (functions.isValueNull(id) && !"0".equalsIgnoreCase(id))
            {
                String query = "SELECT * FROM bank_wiremanager  WHERE bankwiremanagerId=? ORDER BY bankwiremanagerId DESC limit 1";
                psSingleBankWireManager = con.prepareStatement(query.toString());
                psSingleBankWireManager.setString(1, parentbankwireid);
                rsSingleBankWireManager = psSingleBankWireManager.executeQuery();
                logger.error("query for getBankWireDetailsWithParentBankID method pstmt::: ----" + psSingleBankWireManager);
                if (rsSingleBankWireManager.next())
                {
                    bankWireManagerVO = new BankWireManagerVO();
                    bankWireManagerVO.setBankwiremanagerId(rsSingleBankWireManager.getString("bankwiremanagerId"));
                    bankWireManagerVO.setBank_start_date(Functions.checkStringNull(rsSingleBankWireManager.getString("bank_start_date")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("bank_start_date"));
                    bankWireManagerVO.setBank_end_date(Functions.checkStringNull(rsSingleBankWireManager.getString("bank_end_date")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("bank_end_date"));
                    bankWireManagerVO.setServer_start_date(Functions.checkStringNull(rsSingleBankWireManager.getString("server_start_date")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("server_start_date"));
                    bankWireManagerVO.setServer_end_date(Functions.checkStringNull(rsSingleBankWireManager.getString("server_end_date")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("server_end_date"));
                    bankWireManagerVO.setRollingreservereleaseStartdate(Functions.checkStringNull(rsSingleBankWireManager.getString("rollingreserveStartdate")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("rollingreserveStartdate"));
                    bankWireManagerVO.setRollingreservereleasedateupto(Functions.checkStringNull(rsSingleBankWireManager.getString("rollingreservereleasedateupto")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("rollingreservereleasedateupto"));
                    bankWireManagerVO.setDeclinedcoveredStartdate(Functions.checkStringNull(rsSingleBankWireManager.getString("declinedCoveredStartdate")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("declinedCoveredStartdate"));
                    bankWireManagerVO.setDeclinedcoveredupto(Functions.checkStringNull(rsSingleBankWireManager.getString("declinedcoveredupto")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("declinedcoveredupto"));
                    bankWireManagerVO.setChargebackcoveredStartdate(Functions.checkStringNull(rsSingleBankWireManager.getString("chargebackCoveredStartdate")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("chargebackCoveredStartdate"));
                    bankWireManagerVO.setChargebackcoveredupto(Functions.checkStringNull(rsSingleBankWireManager.getString("chargebackcoveredupto")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("chargebackcoveredupto"));
                    bankWireManagerVO.setReversedCoveredStartdate(Functions.checkStringNull(rsSingleBankWireManager.getString("reversedCoveredStartdate")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("reversedCoveredStartdate"));
                    bankWireManagerVO.setReversedCoveredUpto(Functions.checkStringNull(rsSingleBankWireManager.getString("reversedCoveredUpto")) == null ? "0000-00-00 00:00:00" : rsSingleBankWireManager.getString("reversedCoveredUpto"));
                    bankWireManagerVO.setProcessing_amount(Functions.checkStringNull(rsSingleBankWireManager.getString("processing_amount")) == null ? "" : rsSingleBankWireManager.getString("processing_amount"));
                    bankWireManagerVO.setGrossAmount(Functions.checkStringNull(rsSingleBankWireManager.getString("grossAmount")) == null ? "" : rsSingleBankWireManager.getString("grossAmount"));
                    bankWireManagerVO.setNetfinal_amount(Functions.checkStringNull(rsSingleBankWireManager.getString("netfinal_amount")) == null ? "" : rsSingleBankWireManager.getString("netfinal_amount"));
                    bankWireManagerVO.setUnpaid_amount(Functions.checkStringNull(rsSingleBankWireManager.getString("unpaid_amount")) == null ? "" : rsSingleBankWireManager.getString("unpaid_amount"));
                    bankWireManagerVO.setIsrollingreservereleasewire(Functions.checkStringNull(rsSingleBankWireManager.getString("isrollingreservereleasewire")) == null ? "" : rsSingleBankWireManager.getString("isrollingreservereleasewire"));
                    bankWireManagerVO.setIspaid(Functions.checkStringNull(rsSingleBankWireManager.getString("ispaid")) == null ? "" : rsSingleBankWireManager.getString("ispaid"));
                    bankWireManagerVO.setParent_bankwireid(Functions.checkStringNull(rsSingleBankWireManager.getString("parent_bankwireid")) == null ? "" : rsSingleBankWireManager.getString("parent_bankwireid"));
                    bankWireManagerVO.setCurrency(Functions.checkStringNull(rsSingleBankWireManager.getString("currency")) == null ? "" : rsSingleBankWireManager.getString("currency"));
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System error for getSingleBankSettlementCycleActionSpecific::", systemError);
        }
        catch (SQLException e)
        {
            logger.error(" SQL Exception for getSingleBankSettlementCycleActionSpecific::", e);
        }
        finally
        {
            Database.closeResultSet(rsSingleBankWireManager);
            Database.closePreparedStatement(psSingleBankWireManager);
            Database.closeConnection(con);
        }
        return bankWireManagerVO;
    }
    public List<String> getparentBankwireIdForRandomCharges()
    {
        Connection conn = null;
        List<String> list= new ArrayList<>();
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            String query ="SELECT DISTINCT bankwiremanagerId AS parent_bankwireid FROM bank_wiremanager WHERE bankwiremanagerId IN ((SELECT DISTINCT parent_bankwireid FROM bank_wiremanager WHERE parent_bankwireid>0 ORDER BY parent_bankwireid DESC))  AND isSettlementCronExceuted='Y' AND isPayoutCronExcuted='N' ORDER BY bankwiremanagerId DESC";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            logger.error("pstm === > "+pstmt);
            while (rs.next())
            {
                String parentBankID  = rs.getString("parent_bankwireid");
                list.add(parentBankID);
            }
        }
        catch (SystemError e)
        {
            logger.error("SystemError:::::",e);
        }
        catch (SQLException e)
        {
            logger.error("SystemError:::::",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return list;
    }
    public List<String> getCardtypeIdByAccountId(String accountId)
    {
        Connection conn = null;
        List<String> list= new ArrayList<>();
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            String query ="SELECT DISTINCT ct.cardtypeid, ct.cardType FROM member_account_mapping AS ma join card_type AS ct WHERE ct.cardtypeid = ma.cardtypeid AND ma.accountid IN ("+accountId+") AND ct.cardtypeid>0 ORDER BY ct.cardtypeid ASC";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            logger.error("pstm === > "+pstmt);
            while (rs.next())
            {
                String cardtypeid  = rs.getString("ct.cardtypeid") + " - "+rs.getString("ct.cardType");
                list.add(cardtypeid);
            }
        }
        catch (SystemError e)
        {
            logger.error("SystemError:::::",e);
        }
        catch (SQLException e)
        {
            logger.error("SystemError:::::",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return list;
    }
    public List<String> getPaymodeIdByAccountId(String accountId)
    {
        Connection conn = null;
        List<String> list = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getRDBConnection();
            String query ="SELECT DISTINCT pa.paymodeid, pa.paymentType FROM member_account_mapping AS ma JOIN `payment_type` AS pa WHERE ma.paymodeid=pa.paymodeid AND accountid IN ("+accountId+") AND pa.paymodeid>0 ORDER BY pa.paymodeid ASC";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            logger.error("pstm === > "+pstmt);
            while (rs.next())
            {
                String paymodeid  = rs.getString("pa.paymodeid") + " - "+ rs.getString("pa.paymentType");
                list.add(paymodeid);
            }
        }
        catch (SystemError e)
        {
            logger.error("SystemError:::::",e);
        }
        catch (SQLException e)
        {
            logger.error("SystemError:::::",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return list;
    }
    public String getparentSettleDate (String bankwireId){
        Connection connection = null;
        String settleddate ="";
        try
        {
            connection = Database.getConnection();
            String qry  = "SELECT settleddate FROM bank_wiremanager WHERE bankwiremanagerId=?";
            PreparedStatement pstm = connection.prepareStatement(qry);
            pstm.setInt(1, Integer.parseInt(bankwireId));
            ResultSet rs = pstm.executeQuery();
            logger.error("pstm============"+pstm);
            if (rs.next()){
                settleddate = rs.getString("settleddate");
            }

        }catch (Exception e){

        }finally
        {
            Database.closeConnection(connection);
        }
        return settleddate;
    }
}