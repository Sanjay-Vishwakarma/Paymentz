package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.utils.AccountUtil;
import com.manager.vo.*;
import com.manager.vo.payoutVOs.AgentWireVO;
import com.manager.vo.payoutVOs.WireVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 26/7/14
 * Time: 7:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class AccountDAO
{
    Logger logger=new Logger(AccountDAO.class.getName());
    Functions functions = new Functions();
    public ResultSet getCountOfTransactionAsPerDateAndTerminal(String sTableName,InputDateVO inputDateVO,TerminalVO terminalVO) throws PZDBViolationException
    {

        Connection con=null;
        int counter=4;
        StringBuilder strQueryApender = new StringBuilder("select count(*) as 'count',min(FROM_UNIXTIME(dtstamp)) as 'startDate',max(FROM_UNIXTIME(dtstamp)) as 'endDate' from "+ sTableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=?");
        if(inputDateVO.getFdtstamp()!=null)
        {
            strQueryApender.append(" and dtstamp >= ?");

        }
        if(inputDateVO.getTdtstamp()!=null)
        {
            strQueryApender.append(" and dtstamp <= ?");

        }

        ResultSet rsCountOfTransaction = null;
        try
        {
            con=Database.getConnection();
            PreparedStatement pCountTransactionList = con.prepareStatement(strQueryApender.toString());
            pCountTransactionList.setString(1,terminalVO.getMemberId());
            pCountTransactionList.setString(2,terminalVO.getAccountId());
            pCountTransactionList.setString(3,terminalVO.getPaymodeId());
            pCountTransactionList.setString(4,terminalVO.getCardTypeId());
            if(Functions.checkStringNull(inputDateVO.getFdtstamp())!=null)
            {
                counter+=1;
                pCountTransactionList.setString(counter,inputDateVO.getFdtstamp());

            }
            if(Functions.checkStringNull(inputDateVO.getTdtstamp())!=null)
            {
                counter+=1;
                pCountTransactionList.setString(counter,inputDateVO.getTdtstamp());

            }
            rsCountOfTransaction=pCountTransactionList.executeQuery();
        }
        catch(SystemError systemError)
        {
            logger.error("System Error::",systemError);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getCountOfTransactionAsPerDateAndTerminal()",null,"Common","System error while connecting to "+sTableName+" table ",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception",e);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getCountOfTransactionAsPerDateAndTerminal()",null,"Common","Sql Exception due incorrect query to "+sTableName+" table ",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return rsCountOfTransaction;
    }

    //getting Transaction Summary for Different Status
    public TransactionSummaryVO getTransactionSummary(String sTableName,InputDateVO inputDateVO,TerminalVO terminalVO) throws PZDBViolationException
    {
        Connection con = null;
        ResultSet rsSumOfAmounts=null;
        TransactionSummaryVO amountVO= new TransactionSummaryVO();
        String strQuery = "select status,count(*) as 'count',sum(amount) as 'amount',sum(captureamount) as 'captureamount',sum(refundamount) as 'refundamount',sum(chargebackamount) as 'chargebackamount' from "+sTableName+" where toid=? and accountid=? and paymodeid=? and cardtypeid=? and  dtstamp >=? and dtstamp <= ?   group by status";
        try
        {
            con=Database.getRDBConnection();
            PreparedStatement psSumofAmount=con.prepareStatement(strQuery);
            psSumofAmount.setString(1,terminalVO.getMemberId());
            psSumofAmount.setString(2,terminalVO.getAccountId());
            psSumofAmount.setString(3,terminalVO.getPaymodeId());
            psSumofAmount.setString(4,terminalVO.getCardTypeId());
            psSumofAmount.setString(5,inputDateVO.getFdtstamp());
            psSumofAmount.setString(6,inputDateVO.getTdtstamp());
            rsSumOfAmounts=psSumofAmount.executeQuery();
            while(rsSumOfAmounts.next())
            {
                if(rsSumOfAmounts.getString("status").equals("authfailed"))
                {
                    amountVO.setAuthfailedAmount(rsSumOfAmounts.getDouble("amount"));
                    amountVO.setCountOfAuthfailed(rsSumOfAmounts.getLong("count"));
                }
                if(rsSumOfAmounts.getString("status").equals("settled"))
                {
                    amountVO.setSettledAmount(rsSumOfAmounts.getDouble("captureamount"));
                    amountVO.setCountOfSettled(rsSumOfAmounts.getLong("count"));
                }
                if(rsSumOfAmounts.getString("status").equals("reversed"))
                {
                    amountVO.setReversedAmount(rsSumOfAmounts.getDouble("refundamount"));
                    amountVO.setCountOfReversed(rsSumOfAmounts.getLong("count"));
                }
                if(rsSumOfAmounts.getString("status").equals("chargeback"))
                {
                    amountVO.setChargebackAmount(rsSumOfAmounts.getDouble("chargebackamount"));
                    amountVO.setCountOfChargeback(rsSumOfAmounts.getLong("count"));
                }
                if(rsSumOfAmounts.getString("status").equals("capturesuccess"))
                {
                    amountVO.setCaptureSuccessAmount(rsSumOfAmounts.getDouble("captureamount"));
                    amountVO.setCaptureSuccessCount(rsSumOfAmounts.getLong("count"));
                }
            }
            amountVO.setReserveGeneratedAmount(amountVO.getChargebackAmount()+amountVO.getReversedAmount()+amountVO.getSettledAmount()+amountVO.getCaptureSuccessAmount());
            amountVO.setCountOfReserveGenerated(amountVO.getCountOfChargeback()+amountVO.getCountOfReversed()+amountVO.getCountOfSettled()+amountVO.getCaptureSuccessCount());
            amountVO.setTotalOfChargeBackAndReversal(amountVO.getChargebackAmount()+amountVO.getReversedAmount());


        }
        catch (SystemError systemError)
        {
            logger.error("System Error::",systemError);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getTransactionSummary()",null,"Common","System error while connecting to "+sTableName+" table ",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::",e);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getTransactionSummary()",null,"Common","Sql exception due to incorrect query  to "+sTableName+" table ",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return amountVO;
    }
    //getting Rolling Reserve Refund Amount and Rolling Reserve Refund Count
    public TransactionSummaryVO getReservedRefundedSummary(RollingReserveDateVO rollingReserveDateVO, TerminalVO terminalVO,String sTableName) throws PZDBViolationException
    {
        int counter=5;
        StringBuffer strQuery=new StringBuffer("SELECT SUM(captureamount) AS 'RollingReserveRefundAmount',COUNT(*) AS 'RollingReserveRefundCount' FROM "+ sTableName +"  WHERE toid=? AND accountid=? AND paymodeid=? AND cardtypeid=?  AND STATUS IN ('settled','reversed','chargeback') ");
        if(functions.isValueNull(rollingReserveDateVO.getRollingReserveStartDate()))
        {
            strQuery.append("AND FROM_UNIXTIME(dtstamp)>=?");
        }
        if(functions.isValueNull(rollingReserveDateVO.getRollingReserveEndDate()))
        {
            strQuery.append("And FROM_UNIXTIME(dtstamp)<=?");
        }
        TransactionSummaryVO reservedRefundedSummary = new TransactionSummaryVO();
        Connection con=null;
        ResultSet rsReservedRefundSummary=null;
        try
        {
            //con = Database.getConnection();
            con = Database.getRDBConnection();
            PreparedStatement psReservedRefundedSummary = con.prepareStatement(strQuery.toString());
            psReservedRefundedSummary.setString(1, terminalVO.getMemberId()) ;
            psReservedRefundedSummary.setString(2,terminalVO.getAccountId()) ;
            psReservedRefundedSummary.setString(3,terminalVO.getPaymodeId()) ;
            psReservedRefundedSummary.setString(4, terminalVO.getCardTypeId()) ;
            if(functions.isValueNull(rollingReserveDateVO.getRollingReserveStartDate()))
            {
                psReservedRefundedSummary.setString(counter,rollingReserveDateVO.getRollingReserveStartDate()) ;
                counter++;
            }
            if(functions.isValueNull(rollingReserveDateVO.getRollingReserveEndDate()))
            {
                psReservedRefundedSummary.setString(counter,rollingReserveDateVO.getRollingReserveEndDate());
            }
            rsReservedRefundSummary= psReservedRefundedSummary.executeQuery();
            if(rsReservedRefundSummary.next())
            {
                reservedRefundedSummary.setReserveRefundAmount(rsReservedRefundSummary.getDouble("RollingReserveRefundAmount"));
                reservedRefundedSummary.setCountOfreserveRefund(rsReservedRefundSummary.getLong("RollingReserveRefundCount"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::",systemError);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getReservedRefundedSummary()",null,"Common","System error while connecting to "+sTableName+"::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ::",e);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getReservedRefundedSummary()",null,"Common","Sql exception due to incorrect query to "+sTableName+"::",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return reservedRefundedSummary;
    }

    //Getting sum of unpaidamount column
    public void setUnpaidBalanceAmount(TerminalVO terminalVO,WireAmountVO wireAmountVO) throws PZDBViolationException
    {
        String  strQuery = "SELECT SUM(unpaidamount) AS 'UnpaidBalanceAmount' FROM wiremanager WHERE toid=? AND accountid=? AND paymodeid=? AND cardtypeid=?";
        Connection con=null;
        ResultSet rsWireBalnceAmount;
        try
        {
            //con=Database.getConnection();
            con=Database.getRDBConnection();
            PreparedStatement psWireBalaceAmount=con.prepareStatement(strQuery);
            psWireBalaceAmount.setString(1,terminalVO.getMemberId());
            psWireBalaceAmount.setString(2,terminalVO.getAccountId());
            psWireBalaceAmount.setString(3,terminalVO.getPaymodeId());
            psWireBalaceAmount.setString(4,terminalVO.getCardTypeId());
            rsWireBalnceAmount=psWireBalaceAmount.executeQuery();
            if(rsWireBalnceAmount.next())
            {
                wireAmountVO.setUnpaidBalanceAmount(rsWireBalnceAmount.getDouble("UnpaidBalanceAmount"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while setting unpaid Balance amount::",systemError);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"setUnpaidBalanceAmount()",null,"Common","System error while connecting to wiremanager::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while setting unpaid balance amount ::",e);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"setUnpaidBalanceAmount()",null,"Common","SQL exception due to incorrect query to wiremanger",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

    }


    public void setWireAmount(TerminalVO terminalVO,WireAmountVO wireAmountVO) throws PZDBViolationException
    {
        Connection con=null;
        ResultSet rsWireAmount=null;
        long counter=1;
        long wireCount =1;
        String strQuery = "SELECT COUNT(*) AS 'WireCount', SUM(netfinalamount) AS 'WireAmount',status FROM wiremanager WHERE toid=? AND accountid=? AND paymodeid=? AND cardtypeid=? Group by status";

        try
        {
            //con=Database.getConnection();
            con=Database.getRDBConnection();
            PreparedStatement psWireBalanceAmount=con.prepareStatement(strQuery);
            psWireBalanceAmount.setString(1,terminalVO.getMemberId());
            psWireBalanceAmount.setString(2,terminalVO.getAccountId());
            psWireBalanceAmount.setString(3,terminalVO.getPaymodeId());
            psWireBalanceAmount.setString(4,terminalVO.getCardTypeId());

            rsWireAmount=psWireBalanceAmount.executeQuery();

            while(rsWireAmount.next())
            {

                if("paid".equals(rsWireAmount.getString("status")))
                {
                    wireAmountVO.setPaidAmount(rsWireAmount.getDouble("WireAmount"));

                }
                else if("unpaid".equals(rsWireAmount.getString("status")))
                {
                    wireAmountVO.setUnpaidAmount(rsWireAmount.getDouble("WireAmount"));
                }
                //wireCount=rsWireAmount.getLong("WireCount")+counter;

            }

            wireAmountVO.setWireCount(counter);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error",systemError);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"setWireAmount()",null,"Common","System error while connecting to wiremanager::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::",e);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"setWireAmount()",null,"Common","Sql Exception due incorrect query to wiremanager::",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

    }

    //Getting all the charges of member based on paymodeid,cardtypeid,accountid
    public List<ChargeVO> getChargesAsPerTerminal(TerminalVO terminalVO) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement psCharges=null;
        ResultSet rsCharges = null;
        List<ChargeVO> chargeVOList = new ArrayList<ChargeVO>();
        String strQuery = "select M.mappingid,M.memberid,M.chargeid,M.chargevalue,M.valuetype,C.chargename,C.isinputrequired,M.keyword,M.sequencenum,M.frequency,M.category,M.subkeyword,M.agentchargevalue,M.partnerchargevalue from member_accounts_charges_mapping as M, charge_master as C  where M.chargeid=C.chargeid and M.memberid=? and M.paymodeid=? and M.cardtypeid=? and M.accountid=? ORDER BY sequencenum;";

        if (terminalVO == null)
        {
        }
        else
        {
            try
            {
                //con=Database.getConnection();
                con = Database.getRDBConnection();
                //change the name
                psCharges = con.prepareStatement(strQuery);
                psCharges.setString(1, terminalVO.getMemberId());
                psCharges.setString(2, terminalVO.getPaymodeId());
                psCharges.setString(3, terminalVO.getCardTypeId());
                psCharges.setString(4, terminalVO.getAccountId());
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
                    chargeVO.setAgentChargeValue(rsCharges.getString(13));
                    chargeVO.setPartnerChargeValue(rsCharges.getString(14));
                    chargeVOList.add(chargeVO);
                }
            }
            catch (SQLException e)
            {
                logger.error("SQL Exception while getting charges list", e);
                PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(), "getChargesAsPerTerminal()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
            }
            catch (SystemError systemError)
            {
                logger.error("System Error while getting charges list::", systemError);
                PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(), "getChargesAsPerTerminal()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
            }
            finally
            {
                Database.closeResultSet(rsCharges);
                Database.closePreparedStatement(psCharges);
                Database.closeConnection(con);
            }
        }
        return chargeVOList;
    }

    //to get count of Verified Order transaction count
    public TransactionSummaryVO getCountOfVerifyOrder(InputDateVO inputDateVO,TerminalVO terminalVO) throws PZDBViolationException
    {
        Connection con= null;
        int counter=6;
        StringBuffer strQuery=new StringBuffer("SELECT COUNT(*) AS 'VerifyOrderCount' FROM transaction_qwipi AS T JOIN bin_details ON T.trackingid = bin_details.icicitransid  WHERE T.toid=? AND T.accountid=? AND T.paymodeid=? AND T.cardtypeid=? AND bin_details.isVerifyOrder=?  ");
        if(functions.isValueNull(inputDateVO.getFdtstamp()))
        {
            strQuery.append(" AND dtstamp >=?");
        }
        if(functions.isValueNull(inputDateVO.getTdtstamp()))
        {
            strQuery.append(" AND dtstamp <=?");
        }
        TransactionSummaryVO transactionSummaryVO= null;
        ResultSet rsCountOfVerifyOrder= null;
        try
        {
            //con=Database.getConnection();
            con=Database.getRDBConnection();
            PreparedStatement psCountOfVerifyOrder=con.prepareStatement(strQuery.toString());
            psCountOfVerifyOrder.setString(1,terminalVO.getMemberId());
            psCountOfVerifyOrder.setString(2,terminalVO.getAccountId());
            psCountOfVerifyOrder.setString(3,terminalVO.getPaymodeId());
            psCountOfVerifyOrder.setString(4,terminalVO.getCardTypeId());
            psCountOfVerifyOrder.setString(5,"Y");
            if(functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psCountOfVerifyOrder.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if(functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psCountOfVerifyOrder.setString(counter,inputDateVO.getTdtstamp());
            }
            rsCountOfVerifyOrder=psCountOfVerifyOrder.executeQuery();
            if(rsCountOfVerifyOrder.next())
            {
                transactionSummaryVO=new TransactionSummaryVO();
                transactionSummaryVO.setCountOfVerifiedOrder(rsCountOfVerifyOrder.getLong("VerifyOrderCount"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::",systemError);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getCountOfVerifyOrder()",null,"Common","System error while connecting to Transaction_Qwipi::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL exception ::",e);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getCountOfVerifyOrder()",null,"Common","Sql Exception due to incorrect query to Transaction_Qwipi::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionSummaryVO;
    }

    //to get count of Refund Alert transaction count
    public TransactionSummaryVO getCountOfRefundAlert(InputDateVO inputDateVO,TerminalVO terminalVO) throws PZDBViolationException
    {
        Connection con= null;
        int counter=6;
        StringBuffer strQuery=new StringBuffer("SELECT COUNT(*) AS 'RefundAlertCount' FROM transaction_qwipi AS T JOIN bin_details ON T.trackingid = bin_details.icicitransid  WHERE T.toid=? AND T.accountid=? AND T.paymodeid=? AND T.cardtypeid=? AND bin_details.isRefundAlert=? ");
        if(functions.isValueNull(inputDateVO.getFdtstamp()))
        {
            strQuery.append(" AND  dtstamp >=?");
        }
        if(functions.isValueNull(inputDateVO.getTdtstamp()))
        {
            strQuery.append(" AND dtstamp <=? ");
        }
        TransactionSummaryVO transactionSummaryVO= null;
        ResultSet rsCountOfVerifyOrder= null;
        try
        {
            //con=Database.getConnection();
            con=Database.getRDBConnection();
            PreparedStatement psCountOfVerifyOrder=con.prepareStatement(strQuery.toString());
            psCountOfVerifyOrder.setString(1,terminalVO.getMemberId());
            psCountOfVerifyOrder.setString(2,terminalVO.getAccountId());
            psCountOfVerifyOrder.setString(3,terminalVO.getPaymodeId());
            psCountOfVerifyOrder.setString(4,terminalVO.getCardTypeId());
            psCountOfVerifyOrder.setString(5,"Y");
            if(functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psCountOfVerifyOrder.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if(functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psCountOfVerifyOrder.setString(counter,inputDateVO.getTdtstamp());
            }
            rsCountOfVerifyOrder=psCountOfVerifyOrder.executeQuery();
            if(rsCountOfVerifyOrder.next())
            {
                transactionSummaryVO=new TransactionSummaryVO();
                transactionSummaryVO.setCountOfRefundAlert(rsCountOfVerifyOrder.getLong("RefundAlertCount"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::",systemError);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getCountOfRefundAlert()",null,"Common","System error while connecting to Transaction_Qwipi::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL exception ::",e);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getCountOfRefundAlert()",null,"Common","Sql Exception due to incorrect query to Transaction_Qwipi::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionSummaryVO;
    }

    //to get Reserve Release Date
    public RollingReserveDateVO getReserveReleaseDate(String memberId) throws PZDBViolationException
    {
        String strQuery=null;
        ResultSet rsPayout=null;
        Connection conn=null;
        PreparedStatement preparedStatement = null;
        RollingReserveDateVO rollingReserveDateVO=new RollingReserveDateVO();
        try
        {
            conn= Database.getRDBConnection();
            strQuery="SELECT WM.rollingreservereleasedateupto AS 'rollingreservestartdate',BRRM.rollingreservereleaseupto AS 'rollingreserveenddate' FROM wiremanager AS WM JOIN bank_rollingreserve_master AS BRRM ON WM.accountid=BRRM.accountid where toid=? order by WM.settledid desc limit 1";
            preparedStatement=conn.prepareStatement(strQuery);
            preparedStatement.setString(1,memberId);
            rsPayout=preparedStatement.executeQuery();
            if(rsPayout.next())
            {
                rollingReserveDateVO.setRollingReserveStartDate(rsPayout.getString("rollingreservestartdate"));
                rollingReserveDateVO.setRollingReserveEndDate(rsPayout.getString("rollingreserveenddate"));
            }
            else
            {
                logger.debug("ResultSet is empty...");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect ",systemError);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getReserveReleaseDate()",null,"Common","System error while connecting to wiremanager table ",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect ",e);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getReserveReleaseDate()",null,"Common","Sql Exception due to incorrect query  to wiremanager table ",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }

        return rollingReserveDateVO;
    }
    //to new Rolling release date(new one if required)
    public RollingReserveDateVO getReserveReleaseDate(TerminalVO terminalVO) throws PZDBViolationException
    {
        String strQuery=null;
        ResultSet rsPayout=null;
        Connection conn=null;
        RollingReserveDateVO rollingReserveDateVO=new RollingReserveDateVO();
        try
        {
            //conn= Database.getConnection();
            conn= Database.getRDBConnection();
            strQuery="SELECT MAX(WM.rollingreservereleasedateupto) AS 'rollingreservestartdate',MAX(BRRM.rollingreservereleaseupto) AS 'rollingreserveenddate' FROM (SELECT accountid,rollingreservereleasedateupto,toid FROM `wiremanager` WHERE toid=?) AS WM RIGHT JOIN (SELECT accountid,rollingreservereleaseupto FROM `bank_rollingreserve_master` WHERE accountid=?) AS BRRM ON WM.accountid=BRRM.accountid";
            PreparedStatement preparedStatement=conn.prepareStatement(strQuery);
            preparedStatement.setString(1,terminalVO.getMemberId());
            preparedStatement.setString(2,terminalVO.getAccountId());
            rsPayout=preparedStatement.executeQuery();
            if(rsPayout.next())
            {
                rollingReserveDateVO.setRollingReserveStartDate(rsPayout.getString("rollingreservestartdate"));
                rollingReserveDateVO.setRollingReserveEndDate(rsPayout.getString("rollingreserveenddate"));
            }
            else
            {
                logger.debug("ResultSet is empty...");

            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect ",systemError);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getReserveReleaseDate()",null,"Common","System error while connecting to wiremanger::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect ",e);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getReserveReleaseDate()",null,"Common","Sql Exception due to incorrect query to wiremanger::",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return rollingReserveDateVO;
    }
    //getting list of WIre Reports
    public List<WireVO> getListOfWireReports(InputDateVO inputDateVO,TerminalVO terminalVO,PaginationVO paginationVO,String isPaid) throws PZDBViolationException
    {
        StringBuffer query=null;
        String countQuery=null;
        Connection con=null;
        ResultSet rsWireReports= null;
        int counter = 4;
        List<WireVO> wireVOs= new ArrayList<WireVO>();
        try
        {

            con= Database.getRDBConnection();
            query= new StringBuffer("SELECT * FROM wiremanager WHERE markedfordeletion='N'  and toid =? and settledate >=? and settledate <=? ");
            if(functions.isValueNull(terminalVO.getTerminalId()))
            {
                query.append(" and terminalid =?");
            }
            if (functions.isValueNull(isPaid) && !"all".equals(isPaid))
            {
                query.append(" and status= ?");
            }
            query.append(" ORDER BY timestamp DESC");
            countQuery="Select Count(*) from ("+query.toString()+") as temp";
            logger.debug("countQuery:-"+countQuery);
            query.append("  limit "+paginationVO.getStart()+","+paginationVO.getEnd());
            logger.debug("Query:-"+query);

            PreparedStatement psWireReports = con.prepareStatement(query.toString());
            psWireReports.setString(1,terminalVO.getMemberId());
            psWireReports.setString(2,inputDateVO.getsMinTransactionDate());
            psWireReports.setString(3,inputDateVO.getsMaxTransactionDate());
            if(functions.isValueNull(terminalVO.getTerminalId()))
            {
                psWireReports.setString(counter,terminalVO.getTerminalId());
                counter++;
            }
            if (functions.isValueNull(isPaid) && !"all".equals(isPaid))
            {
                psWireReports.setString(counter,isPaid);
            }
            rsWireReports=psWireReports.executeQuery();
            while(rsWireReports.next())
            {
                WireVO wireVO = new WireVO();
                wireVO.setSettleId(rsWireReports.getString("settledid"));
                wireVO.setTerminalVO(terminalVO);
                wireVO.setCurrency(rsWireReports.getString("currency"));
                wireVO.setSettleDate(rsWireReports.getString("settledate"));
                wireVO.setFirstDate(rsWireReports.getString("firstdate"));
                wireVO.setLastDate(rsWireReports.getString("lastdate"));
                wireVO.setNetFinalAmount(rsWireReports.getDouble("netfinalamount"));
                wireVO.setUnpaidAmount(rsWireReports.getDouble("unpaidamount"));
                wireVO.setSettlementReportFilePath(rsWireReports.getString("settlementreportfilepath"));
                wireVO.setSettledTransactionFilePath(rsWireReports.getString("settledtransactionfilepath"));
                wireVO.setStatus(rsWireReports.getString("status"));
                wireVOs.add(wireVO);
            }

            //Total records query
            counter=4;
            PreparedStatement psCountOfWireReports= con.prepareStatement(countQuery);
            psCountOfWireReports.setString(1,terminalVO.getMemberId());
            psCountOfWireReports.setString(2,inputDateVO.getsMinTransactionDate());
            psCountOfWireReports.setString(3,inputDateVO.getsMaxTransactionDate());
            logger.debug("terminalId::"+terminalVO.getTerminalId());
            if(functions.isValueNull(terminalVO.getTerminalId()))
            {
                psCountOfWireReports.setString(counter,terminalVO.getTerminalId());
                counter++;
            }
            if (functions.isValueNull(isPaid) && !"all".equals(isPaid))
            {
                psCountOfWireReports.setString(counter,isPaid);
            }
            rsWireReports=psCountOfWireReports.executeQuery();
            if(rsWireReports.next())
            {
                paginationVO.setTotalRecords(rsWireReports.getInt(1));
            }

        }
        catch (SystemError systemError)
        {
            logger.error("error while fetching record from wire manager",systemError);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getListOfWireReports()",null,"Common","DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Error while fetching record from wire manager",e);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getListOfWireReports()",null,"Common","SQL Query Exception",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return wireVOs;
    }
    //get transactionVo by dtStamp
    public TransactionSummaryVO getTransactionSummaryByDtstamp(String sTableName,InputDateVO inputDateVO,TerminalVO terminalVO) throws PZDBViolationException
    {
        TransactionSummaryVO transactionSummaryVO=new TransactionSummaryVO();
        Connection con = null;
        ResultSet rsSumOfAmounts=null;

        int counter=5;

        StringBuilder strQuery = new StringBuilder("select count(*) as 'count',status,sum(amount) as 'amount',sum(captureamount) as 'captureamount',sum(refundamount) as 'refundamount',sum(chargebackamount) as 'chargebackamount',MIN(from_unixtime(dtstamp)) as 'mindate',MAX(from_unixtime(dtstamp)) as 'maxdate' from "+sTableName+" where toid=? and accountid=? and paymodeid=? and cardtypeid=?");
        if(functions.isValueNull(inputDateVO.getFdtstamp()))
        {
            strQuery.append(" and dtstamp >=?");
        }
        if(functions.isValueNull(inputDateVO.getTdtstamp()))
        {
            strQuery.append(" and dtstamp <=?");
        }
        strQuery.append(" group by status");
        try
        {
            //con=Database.getConnection();
            con=Database.getRDBConnection();
            PreparedStatement preparedStatement=con.prepareStatement(strQuery.toString());
            preparedStatement.setString(1,terminalVO.getMemberId());
            preparedStatement.setString(2,terminalVO.getAccountId());
            preparedStatement.setString(3,terminalVO.getPaymodeId());
            preparedStatement.setString(4,terminalVO.getCardTypeId());
            if(functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                preparedStatement.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if(functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                preparedStatement.setString(counter,inputDateVO.getTdtstamp());
            }
            logger.error("Query for getTransactionSummaryByDtstamp::::"+preparedStatement);
            rsSumOfAmounts=preparedStatement.executeQuery();
            while(rsSumOfAmounts.next())
            {
                if(rsSumOfAmounts.getString("status").equals("authfailed"))
                {
                    transactionSummaryVO.setAuthfailedAmount(rsSumOfAmounts.getDouble("amount"));
                    transactionSummaryVO.setCountOfAuthfailed(rsSumOfAmounts.getLong("count"));
                    //min and max dates for authFailed
                    transactionSummaryVO.setMinAuthFailedDate(rsSumOfAmounts.getString("mindate"));
                    transactionSummaryVO.setMaxAuthFailedDate(rsSumOfAmounts.getString("maxdate"));
                }
                if(rsSumOfAmounts.getString("status").equals("settled"))
                {
                    transactionSummaryVO.setSettledAmount(rsSumOfAmounts.getDouble("captureamount"));
                    transactionSummaryVO.setCountOfSettled(rsSumOfAmounts.getLong("count"));
                    //min and max dates for settled
                    transactionSummaryVO.setMinSettledDate(rsSumOfAmounts.getString("mindate"));
                    transactionSummaryVO.setMaxSettledDate(rsSumOfAmounts.getString("maxdate"));
                }
                if(rsSumOfAmounts.getString("status").equals("reversed"))
                {
                    transactionSummaryVO.setReversedAmount(rsSumOfAmounts.getDouble("refundamount"));
                    transactionSummaryVO.setCountOfReversed(rsSumOfAmounts.getLong("count"));
                    //min and max dates for reversed
                    transactionSummaryVO.setMinReversedDate(rsSumOfAmounts.getString("mindate"));
                    transactionSummaryVO.setMaxReversedDate(rsSumOfAmounts.getString("maxdate"));
                }
                if(rsSumOfAmounts.getString("status").equals("chargeback"))
                {
                    transactionSummaryVO.setChargebackAmount(rsSumOfAmounts.getDouble("chargebackamount"));
                    transactionSummaryVO.setCountOfChargeback(rsSumOfAmounts.getLong("count"));
                    //min and max dates for chargeBack
                    transactionSummaryVO.setMinChargeBackDate(rsSumOfAmounts.getString("mindate"));
                    transactionSummaryVO.setMaxChargeBackDate(rsSumOfAmounts.getString("maxdate"));
                }
                if(rsSumOfAmounts.getString("status").equals("capturesuccess"))
                {
                    transactionSummaryVO.setCaptureSuccessAmount(rsSumOfAmounts.getDouble("captureamount"));
                    transactionSummaryVO.setCaptureSuccessCount(rsSumOfAmounts.getLong("count"));
                }

                logger.debug("status::"+rsSumOfAmounts.getString("status"));
                logger.debug("min date::"+rsSumOfAmounts.getString("mindate"));
                logger.debug("max date::"+rsSumOfAmounts.getString("maxdate"));
                transactionSummaryVO.setMinOfAllStatusDate(rsSumOfAmounts.getString("mindate"));
                transactionSummaryVO.setMaxOfAllStatusDate(rsSumOfAmounts.getString("maxdate"));
            }
            logger.debug("min of all status date::"+transactionSummaryVO.getMinOfAllStatusDate()+" Max date of all status::"+transactionSummaryVO.getMaxOfAllStatusDate());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::",systemError);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getTransactionSummaryByDtstamp()",null,"Common","System error while connecting to "+sTableName+"::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::",e);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getTransactionSummaryByDtstamp()",null,"Common","SQL Excpetion due to incorrect query to "+sTableName+"::",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

        return transactionSummaryVO;

    }
    // get transactionVo by timeStamp
    public TransactionSummaryVO getTransactionSummaryByTimestamp(String sTableName,InputDateVO inputDateVO,TerminalVO terminalVO) throws PZDBViolationException
    {
        TransactionSummaryVO transactionSummaryVO=new TransactionSummaryVO();
        Connection con = null;
        ResultSet rsSumOfAmounts=null;

        int counter=5;
        StringBuilder strQuery = new StringBuilder("select status,count(*) as 'count',sum(amount) as 'amount',sum(captureamount) as 'captureamount',sum(refundamount) as 'refundamount',sum(chargebackamount) as 'chargebackamount',MIN(timestamp) as 'mindate',MAX(timestamp) as 'maxdate' from " + sTableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=?");
        if(functions.isValueNull(inputDateVO.getsMinTransactionDate()))
        {
            strQuery.append(" and timestamp >=?");
        }
        if(functions.isValueNull(inputDateVO.getsMaxTransactionDate()))
        {
            strQuery.append(" and timestamp <=?");
        }
        strQuery.append(" group by status");
        try
        {
            //con=Database.getConnection();
            con=Database.getRDBConnection();
            PreparedStatement preparedStatement=con.prepareStatement(strQuery.toString());
            preparedStatement.setString(1,terminalVO.getMemberId());
            preparedStatement.setString(2,terminalVO.getAccountId());
            preparedStatement.setString(3,terminalVO.getPaymodeId());
            preparedStatement.setString(4,terminalVO.getCardTypeId());
            if(functions.isValueNull(inputDateVO.getsMinTransactionDate()))
            {
                preparedStatement.setString(counter,inputDateVO.getsMinTransactionDate());
                counter++;
            }
            if(functions.isValueNull(inputDateVO.getsMaxTransactionDate()))
            {
                preparedStatement.setString(counter,inputDateVO.getsMaxTransactionDate());
            }
            rsSumOfAmounts=preparedStatement.executeQuery();
            while(rsSumOfAmounts.next())
            {
                if(rsSumOfAmounts.getString("status").equals("authfailed"))
                {
                    transactionSummaryVO.setAuthfailedAmount(rsSumOfAmounts.getDouble("amount"));
                    transactionSummaryVO.setCountOfAuthfailed(rsSumOfAmounts.getLong("count"));
                    //min and max dates for authFailed
                    transactionSummaryVO.setMinAuthFailedDate(rsSumOfAmounts.getString("mindate"));
                    transactionSummaryVO.setMaxAuthFailedDate(rsSumOfAmounts.getString("maxdate"));
                }
                if(rsSumOfAmounts.getString("status").equals("settled"))
                {
                    transactionSummaryVO.setSettledAmount(rsSumOfAmounts.getDouble("captureamount"));
                    transactionSummaryVO.setCountOfSettled(rsSumOfAmounts.getLong("count"));
                    //min and max dates for Settled
                    transactionSummaryVO.setMinSettledDate(rsSumOfAmounts.getString("mindate"));
                    transactionSummaryVO.setMaxSettledDate(rsSumOfAmounts.getString("maxdate"));
                }
                if(rsSumOfAmounts.getString("status").equals("reversed"))
                {
                    transactionSummaryVO.setReversedAmount(rsSumOfAmounts.getDouble("refundamount"));
                    transactionSummaryVO.setCountOfReversed(rsSumOfAmounts.getLong("count"));
                    //min and max dates for reversed
                    transactionSummaryVO.setMinReversedDate(rsSumOfAmounts.getString("mindate"));
                    transactionSummaryVO.setMaxReversedDate(rsSumOfAmounts.getString("maxdate"));
                }
                if(rsSumOfAmounts.getString("status").equals("chargeback"))
                {
                    transactionSummaryVO.setChargebackAmount(rsSumOfAmounts.getDouble("chargebackamount"));
                    transactionSummaryVO.setCountOfChargeback(rsSumOfAmounts.getLong("count"));
                    //min and max dates for chargeBack
                    transactionSummaryVO.setMinChargeBackDate(rsSumOfAmounts.getString("mindate"));
                    transactionSummaryVO.setMaxChargeBackDate(rsSumOfAmounts.getString("maxdate"));
                }
                transactionSummaryVO.setMinOfAllStatusDate(rsSumOfAmounts.getString("mindate"));
                transactionSummaryVO.setMaxOfAllStatusDate(rsSumOfAmounts.getString("maxdate"));
            }
            transactionSummaryVO.setTotalOfChargeBackAndReversal(transactionSummaryVO.getChargebackAmount()+transactionSummaryVO.getReversedAmount());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::",systemError);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getTransactionSummaryByTimestamp()",null,"Common","System error while connecting to "+sTableName+"::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::",e);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getTransactionSummaryByTimestamp()",null,"Common","Sql Exception due to incorrect query to "+sTableName+"::",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

        return transactionSummaryVO;

    }

    public String getMemberFirstTransactionDate(String memberId, String accountId)//sandip
    {
        AccountUtil util=new AccountUtil();
        String tableName=util.getTableNameFromAccountId(accountId);
        String strQuery=null;
        String memberFirstTransactionDate=null;
        ResultSet rsPayout=null;
        Connection conn=null;
        try
        {
            conn= Database.getRDBConnection();
            strQuery="SELECT FROM_UNIXTIME(dtstamp) AS 'FirstTransDate' FROM "+  tableName + " WHERE trackingid IN(SELECT MIN(trackingid) FROM " + tableName + " WHERE toid=?)";
            PreparedStatement preparedStatement=conn.prepareStatement(strQuery);
            preparedStatement.setString(1,memberId);
            rsPayout=preparedStatement.executeQuery();
            if(rsPayout.next())
            {
                memberFirstTransactionDate=rsPayout.getString("FirstTransDate");
            }
            else
            {
                logger.debug("ResultSet is empty...");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect ",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect ",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return memberFirstTransactionDate;
    }

    public String getReserveReleaseStartDate(String memberId)//sandip
    {
        String strQuery=null;
        String rollingreserveStartDate=null;
        ResultSet rsPayout=null;
        Connection conn=null;
        try
        {
            conn= Database.getRDBConnection();
            strQuery="SELECT WM.rollingreservereleasedateupto AS 'rollingreservestartdate' FROM wiremanager AS WM WHERE toid=? ORDER BY WM.settledid DESC LIMIT 1";
            PreparedStatement preparedStatement=conn.prepareStatement(strQuery);
            preparedStatement.setString(1,memberId);
            rsPayout=preparedStatement.executeQuery();
            if(rsPayout.next())
            {
                rollingreserveStartDate=rsPayout.getString("rollingreservestartdate");
            }
            else
            {
                logger.debug("ResultSet is empty...");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect ",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect ",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return rollingreserveStartDate;
    }
    public String getReserveReleaseEndDate(String accountId) //sandip
    {
        String strQuery=null;
        String rollingreserveEndDate=null;
        ResultSet rsPayout=null;
        Connection conn=null;
        try
        {
            conn= Database.getRDBConnection();
            strQuery="SELECT BRRM.rollingreservereleaseupto AS 'rollingreserveenddate' FROM bank_rollingreserve_master AS BRRM WHERE BRRM.accountId=? ORDER BY BRRM.id DESC LIMIT 1";
            PreparedStatement preparedStatement=conn.prepareStatement(strQuery);
            preparedStatement.setString(1,accountId);
            rsPayout=preparedStatement.executeQuery();
            if(rsPayout.next())
            {
                rollingreserveEndDate=rsPayout.getString("rollingreserveenddate");
            }
            else
            {
                logger.debug("ResultSet is empty...");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect ",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect ",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return rollingreserveEndDate;
    }

    public long getVerifyOrderCount(TerminalVO terminalVO)//sandip
    {
        long verifyOrderCount=0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout=null;
        try
        {
            con= Database.getRDBConnection();
            String Query = "SELECT COUNT(*) AS 'VerifyOrderCount' FROM transaction_qwipi AS T JOIN bin_details ON T.trackingid = bin_details.icicitransid  WHERE T.toid=? AND T.accountid=? AND T.paymodeid=? AND T.cardtypeid=? AND bin_details.isVerifyOrder=?";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,terminalVO.getMemberId());
            preparedStatement.setString(2,terminalVO.getAccountId());
            preparedStatement.setString(3,terminalVO.getPaymodeId());
            preparedStatement.setString(4,terminalVO.getCardTypeId());
            preparedStatement.setString(5,"Y");
            rsPayout=preparedStatement.executeQuery();
            if(rsPayout.next())
            {
                verifyOrderCount=rsPayout.getLong("VerifyOrderCount");
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
            Database.closeConnection(con);
        }
        return verifyOrderCount;

    }
    public long getRefundAlertCount(TerminalVO terminalVO)//sandip
    {
        long refundAlertCount=0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout=null;
        try
        {
            con= Database.getRDBConnection();
            String Query = "SELECT COUNT(*) AS 'RefundAlertCount' FROM transaction_qwipi AS T JOIN bin_details ON T.trackingid = bin_details.icicitransid  WHERE T.toid=? AND T.accountid=? AND T.paymodeid=? AND T.cardtypeid=? AND bin_details.isRefundAlert=?";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,terminalVO.getMemberId());
            preparedStatement.setString(2,terminalVO.getAccountId());
            preparedStatement.setString(3,terminalVO.getPaymodeId());
            preparedStatement.setString(4,terminalVO.getCardTypeId());
            preparedStatement.setString(5,"Y");
            rsPayout=preparedStatement.executeQuery();
            if(rsPayout.next())
            {
                refundAlertCount=rsPayout.getLong("RefundAlertCount");
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
            Database.closeConnection(con);
        }
        return refundAlertCount;

    }
    public long getPaidVerifyOrderCount(TerminalVO terminalVO)//sandip
    {
        long paidVerifyOderCount=0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout=null;
        try
        {
            con= Database.getRDBConnection();
            String Query = "SELECT SUM(verifyorder_count) AS 'verifyOrderPaidCount' FROM member_settlementcycle_details WHERE memberid=?";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,terminalVO.getMemberId());
            rsPayout=preparedStatement.executeQuery();
            if(rsPayout.next())
            {
                paidVerifyOderCount=rsPayout.getLong("verifyOrderPaidCount");
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
            Database.closeConnection(con);
        }
        return paidVerifyOderCount;


    }
    public long getPaidRefundAlertCount(TerminalVO terminalVO)//sandip
    {
        long paidRefundAlertCount=0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout=null;
        try
        {
            con= Database.getRDBConnection();
            String Query = "SELECT SUM(refundalert_count) AS 'refundAlertPaidCount' FROM member_settlementcycle_details WHERE memberid=?";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,terminalVO.getMemberId());
            rsPayout=preparedStatement.executeQuery();
            if(rsPayout.next())
            {
                paidRefundAlertCount=rsPayout.getLong("refundAlertPaidCount");
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
            Database.closeConnection(con);
        }
        return paidRefundAlertCount;


    }
    public String updateMemberCycleDetails(String memberId,String terminalId,long verifyOrderCount,long refundAlertCount,String latestSetupFeeDate)//sandip
    {
        String  status="failure";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout=null;
        try
        {
            con= Database.getConnection();
            String Query = "INSERT INTO member_settlementcycle_details(memberid,terminalid,verifyorder_count,refundalert_count,lastsetupfeedate) VALUES(?,?,?,?,?)";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,memberId);
            preparedStatement.setString(2,terminalId);
            preparedStatement.setLong(3,verifyOrderCount);
            preparedStatement.setLong(4,refundAlertCount);
            preparedStatement.setString(5,latestSetupFeeDate);
            int k=preparedStatement.executeUpdate();
            if(k==1)
            {
                status="success";
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
            Database.closeConnection(con);
        }
        return status;
    }
    public String getLastSetupFeeDate(String memberId,String terminalId) //sandip
    {
        String lastSetupFeeDate=null;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout=null;
        try
        {
            con= Database.getRDBConnection();
            String Query = "SELECT lastsetupfeedate FROM member_settlementcycle_details WHERE memberid=? AND terminalid=? ORDER BY id DESC LIMIT 1";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,memberId);
            preparedStatement.setString(2,terminalId);
            rsPayout=preparedStatement.executeQuery();
            if(rsPayout.next())
            {

                lastSetupFeeDate=rsPayout.getString("lastsetupfeedate");
            }

        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving ", se);
        }
        catch(SystemError e)
        {
            logger.error("SystemError ::::::: Leaving  ", e);
        }
        finally
        {
            Database.closeConnection(con);
        }

        return lastSetupFeeDate;


    }
    //getting list Of Merchant Wire Reports
    public List<WireVO> getListOfMerchantWireReports(InputDateVO inputDateVO,TerminalVO terminalVO,PaginationVO paginationVO,String isPaid) throws PZDBViolationException
    {
        StringBuilder query=null;
        String countQuery=null;
        Connection con=null;
        PreparedStatement psWireReports=null;
        ResultSet rsWireReports= null;
        int counter = 4;
        List<WireVO> wireVOs= new ArrayList<WireVO>();
        try
        {

            //con= Database.getConnection();
            con= Database.getRDBConnection();
            query= new StringBuilder("SELECT * FROM merchant_wiremanager WHERE markedfordeletion='N'  and toid =? and wirecreationtime >=? and wirecreationtime <=? ");
            if(functions.isValueNull(terminalVO.getTerminalId()))
            {
                query.append(" and terminalid =?");
            }
            if (functions.isValueNull(isPaid) && !"all".equals(isPaid))
            {
                query.append(" and status= ?");
            }
            query.append(" ORDER BY timestamp DESC");
            countQuery="Select Count(*) from ("+query.toString()+") as temp";
            logger.debug("countQuery:-"+countQuery);
            query.append("  limit ").append(paginationVO.getStart()).append(",").append(paginationVO.getEnd());
            logger.debug("Query:-"+query);

            psWireReports = con.prepareStatement(query.toString());
            psWireReports.setString(1,terminalVO.getMemberId());
            psWireReports.setString(2,inputDateVO.getFdtstamp());
            psWireReports.setString(3,inputDateVO.getTdtstamp());
            if(functions.isValueNull(terminalVO.getTerminalId()))
            {
                psWireReports.setString(counter,terminalVO.getTerminalId());
                counter++;
            }
            if (functions.isValueNull(isPaid) && !"all".equals(isPaid))
            {
                psWireReports.setString(counter,isPaid);
            }
            rsWireReports=psWireReports.executeQuery();
            while(rsWireReports.next())
            {
                WireVO wireVO = new WireVO();
                wireVO.setSettleId(rsWireReports.getString("settledid"));
                wireVO.setTerminalVO(terminalVO);
                wireVO.setCurrency(rsWireReports.getString("currency"));
                wireVO.setSettleDate(rsWireReports.getString("settledate"));
                wireVO.setFirstDate(rsWireReports.getString("firstdate"));
                wireVO.setLastDate(rsWireReports.getString("lastdate"));
                wireVO.setNetFinalAmount(rsWireReports.getDouble("netfinalamount"));
                wireVO.setUnpaidAmount(rsWireReports.getDouble("unpaidamount"));
                wireVO.setSettlementReportFilePath(rsWireReports.getString("settlementreportfilepath"));
                wireVO.setSettledTransactionFilePath(rsWireReports.getString("settledtransactionfilepath"));
                wireVO.setStatus(rsWireReports.getString("status"));
                wireVO.setSettlementCycleNo(rsWireReports.getString("settlementcycle_no"));
                wireVO.setMemberId(rsWireReports.getString("toid"));
                wireVO.setTerminalId(rsWireReports.getString("terminalid"));
                wireVOs.add(wireVO);
            }

            //Total records query
            counter=4;
            PreparedStatement psCountOfWireReports= con.prepareStatement(countQuery);
            psCountOfWireReports.setString(1,terminalVO.getMemberId());
            psCountOfWireReports.setString(2,inputDateVO.getFdtstamp());
            psCountOfWireReports.setString(3,inputDateVO.getTdtstamp());
            logger.debug("terminalId::"+terminalVO.getTerminalId());
            if(functions.isValueNull(terminalVO.getTerminalId()))
            {
                psCountOfWireReports.setString(counter,terminalVO.getTerminalId());
                counter++;
            }
            if (functions.isValueNull(isPaid) && !"all".equals(isPaid))
            {
                psCountOfWireReports.setString(counter,isPaid);
            }
            rsWireReports=psCountOfWireReports.executeQuery();
            if(rsWireReports.next())
            {
                paginationVO.setTotalRecords(rsWireReports.getInt(1));
            }

        }
        catch (SystemError systemError)
        {
            logger.error("error while fetching record from wire manager",systemError);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getListOfMerchantWireReports()",null,"Common","DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Error while fetching record from wire manager",e);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getListOfMerchantWireReports()",null,"Common","SQL Query Exception",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(rsWireReports);
            Database.closePreparedStatement(psWireReports);
            Database.closeConnection(con);
        }
        return wireVOs;
    }
    //getting retrievalRequest count
    public TransactionSummaryVO getRetrievalRequestInfo(TerminalVO terminalVO,String tableName) throws PZDBViolationException
    {
        TransactionSummaryVO transactionSummaryVO=null;
        Connection con = null;
        ResultSet rsRetrievalRequestInfo = null;
        try
        {
            //con= Database.getConnection();
            con= Database.getRDBConnection();
            String Query = "SELECT COUNT(*) AS 'RetrivalRequestCount' FROM "+tableName+" AS T JOIN bin_details ON T.trackingid = bin_details.icicitransid  WHERE T.toid=? AND T.accountid=? AND T.paymodeid=? AND T.cardtypeid=? AND bin_details.isRetrivalRequest=?";
            PreparedStatement psRetrivalRequestInfo=con.prepareStatement(Query);
            psRetrivalRequestInfo.setString(1,terminalVO.getMemberId());
            psRetrivalRequestInfo.setString(2,terminalVO.getAccountId());
            psRetrivalRequestInfo.setString(3,terminalVO.getPaymodeId());
            psRetrivalRequestInfo.setString(4,terminalVO.getCardTypeId());
            psRetrivalRequestInfo.setString(5,"Y");
            rsRetrievalRequestInfo=psRetrivalRequestInfo.executeQuery();
            if(rsRetrievalRequestInfo.next())
            {
                transactionSummaryVO = new TransactionSummaryVO();
                transactionSummaryVO.setCountOfRetrievalRequest(rsRetrievalRequestInfo.getLong("RetrivalRequestCount"));
            }

        }
        catch (SQLException se)
        {
            logger.error("error while fetching record from wire manager",se);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(), "getRetrievalRequestInfo()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch(SystemError e)
        {
            logger.error("SQL Error while fetching record from wire manager",e);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(), "getRetrievalRequestInfo()", null, "Common", "SQL Query Exception", PZDBExceptionEnum.INCORRECT_QUERY,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionSummaryVO;
    }

    //getting list Of wireReports for Agent
    public List<AgentWireVO> getListOfAgentWireReports(InputDateVO inputDateVO,TerminalVO terminalVO,PaginationVO paginationVO,String isPaid) throws PZDBViolationException
    {
        StringBuffer query=null;
        String countQuery=null;
        Connection con=null;
        ResultSet rsWireReports= null;
        int counter = 3;
        List<AgentWireVO> wireVOs= new ArrayList<AgentWireVO>();
        try
        {

            con= Database.getRDBConnection();
            query= new StringBuffer("SELECT * FROM agent_wiremanager WHERE markedfordeletion='N'  and wirecreationtime >=? and wirecreationtime <=? ");
            if(functions.isValueNull(terminalVO.getTerminalId()))
            {
                query.append(" and terminalid =?");
            }
            if (functions.isValueNull(isPaid) && !"all".equals(isPaid))
            {
                query.append(" and status= ?");
            }
            query.append(" ORDER BY timestamp DESC");
            countQuery="Select Count(*) from ("+query.toString()+") as temp";
            logger.debug("countQuery:-"+countQuery);
            query.append("  limit "+paginationVO.getStart()+","+paginationVO.getEnd());
            logger.debug("Query:-"+query);

            PreparedStatement psWireReports = con.prepareStatement(query.toString());
            psWireReports.setString(1,inputDateVO.getFdtstamp());
            psWireReports.setString(2,inputDateVO.getTdtstamp());
            if(functions.isValueNull(terminalVO.getTerminalId()))
            {
                psWireReports.setString(counter,terminalVO.getTerminalId());
                counter++;
            }
            if (functions.isValueNull(isPaid) && !"all".equals(isPaid))
            {
                psWireReports.setString(counter,isPaid);
            }
            rsWireReports=psWireReports.executeQuery();
            while(rsWireReports.next())
            {
                AgentWireVO agentWireVO = new AgentWireVO();

                agentWireVO.setSettledId(rsWireReports.getString("settledid"));
                agentWireVO.setSettleDate(rsWireReports.getString("settledate"));
                agentWireVO.setSettlementStartDate(rsWireReports.getString("settlementstartdate"));
                agentWireVO.setSettlementEndDate(rsWireReports.getString("settlementenddate"));
                agentWireVO.setAgentChargeAmount(rsWireReports.getDouble("agentchargeamount"));
                agentWireVO.setAgentUnpaidAmount(rsWireReports.getDouble("agentunpaidamount"));
                agentWireVO.setAgentTotalFundedAmount(rsWireReports.getDouble("agenttotalfundedamount"));
                agentWireVO.setCurrency(rsWireReports.getString("currency"));
                agentWireVO.setStatus(rsWireReports.getString("status"));
                agentWireVO.setSettlementReportFileName(rsWireReports.getString("settlementreportfilename"));
                agentWireVO.setMarkedForDeletion(rsWireReports.getString("markedfordeletion"));
                agentWireVO.setTimestamp(rsWireReports.getString("timestamp"));
                agentWireVO.setAgentId(rsWireReports.getString("agentid"));
                agentWireVO.setMemberId(rsWireReports.getString("toid"));
                agentWireVO.setTerminalId(rsWireReports.getString("terminalid"));
                agentWireVO.setAccountId(rsWireReports.getString("accountid"));
                agentWireVO.setPayModeId(rsWireReports.getString("paymodeid"));
                agentWireVO.setCardTypeId(rsWireReports.getString("cardtypeid"));
                agentWireVO.setDeclinedCoverDateUpTo(rsWireReports.getString("declinedcoverdateupto"));
                agentWireVO.setReversedCoverDateUpTo(rsWireReports.getString("reversedcoverdateupto"));
                agentWireVO.setChargebackCoverDateUpTo(rsWireReports.getString("chargebackcoverdateupto"));
                agentWireVO.setWireCreationDate(rsWireReports.getString("wirecreationtime"));

                wireVOs.add(agentWireVO);
            }

            //Total records query
            counter=3;
            PreparedStatement psCountOfWireReports= con.prepareStatement(countQuery);
            psCountOfWireReports.setString(1,inputDateVO.getFdtstamp());
            psCountOfWireReports.setString(2,inputDateVO.getTdtstamp());
            logger.debug("terminalId::"+terminalVO.getTerminalId());
            if(functions.isValueNull(terminalVO.getTerminalId()))
            {
                psCountOfWireReports.setString(counter,terminalVO.getTerminalId());
                counter++;
            }
            if (functions.isValueNull(isPaid) && !"all".equals(isPaid))
            {
                psCountOfWireReports.setString(counter,isPaid);
            }
            rsWireReports=psCountOfWireReports.executeQuery();
            if(rsWireReports.next())
            {
                paginationVO.setTotalRecords(rsWireReports.getInt(1));
            }

        }
        catch (SystemError systemError)
        {
            logger.error("error while fetching record from wire manager",systemError);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getListOfAgentWireReports()",null,"Common","DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Error while fetching record from wire manager",e);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getListOfMerchantWireReports()",null,"Common","SQL Query Exception",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return wireVOs;
    }

    public List<AgentWireVO> getListOfAgentWireReportByTerminal(InputDateVO inputDateVO,String agentid,String terminalId,PaginationVO paginationVO,String isPaid) throws PZDBViolationException
    {
        StringBuffer query=null;
        String countQuery=null;
        Connection con=null;
        PreparedStatement psWireReports = null;
        ResultSet rsWireReports= null;
        int counter = 3;
        List<AgentWireVO> wireVOs= new ArrayList();
        try
        {
            //con= Database.getConnection();
            con= Database.getRDBConnection();
            query= new StringBuffer("SELECT * FROM agent_wiremanager WHERE agentid='"+agentid+"' AND markedfordeletion='N' and wirecreationtime >=? and wirecreationtime <=? ");
            if(functions.isValueNull(terminalId))
            {
                query.append(" and terminalid IN ("+terminalId+")");
            }
            if (functions.isValueNull(isPaid) && !"all".equals(isPaid))
            {
                query.append(" and status= ?");
            }
            query.append(" ORDER BY timestamp DESC");
            countQuery="Select Count(*) from ("+query.toString()+") as temp";
            query.append("  limit "+paginationVO.getStart()+","+paginationVO.getEnd());
            logger.debug("Query:-" + query);

            psWireReports = con.prepareStatement(query.toString());
            psWireReports.setString(1,inputDateVO.getFdtstamp());
            psWireReports.setString(2,inputDateVO.getTdtstamp());
            /*if(functions.isValueNull(terminalId))
            {
                psWireReports.setString(counter,terminalId);
                counter++;
            }*/
            if (functions.isValueNull(isPaid) && !"all".equals(isPaid))
            {
                psWireReports.setString(counter,isPaid);
            }
            rsWireReports=psWireReports.executeQuery();
            while(rsWireReports.next())
            {
                AgentWireVO agentWireVO = new AgentWireVO();

                agentWireVO.setSettledId(rsWireReports.getString("settledid"));
                agentWireVO.setSettleDate(rsWireReports.getString("settledate"));
                agentWireVO.setSettlementStartDate(rsWireReports.getString("settlementstartdate"));
                agentWireVO.setSettlementEndDate(rsWireReports.getString("settlementenddate"));
                agentWireVO.setAgentChargeAmount(rsWireReports.getDouble("agentchargeamount"));
                agentWireVO.setAgentUnpaidAmount(rsWireReports.getDouble("agentunpaidamount"));
                agentWireVO.setAgentTotalFundedAmount(rsWireReports.getDouble("agenttotalfundedamount"));
                agentWireVO.setCurrency(rsWireReports.getString("currency"));
                agentWireVO.setStatus(rsWireReports.getString("status"));
                agentWireVO.setSettlementReportFileName(rsWireReports.getString("settlementreportfilename"));
                agentWireVO.setMarkedForDeletion(rsWireReports.getString("markedfordeletion"));
                agentWireVO.setTimestamp(rsWireReports.getString("timestamp"));
                agentWireVO.setAgentId(rsWireReports.getString("agentid"));
                agentWireVO.setMemberId(rsWireReports.getString("toid"));
                agentWireVO.setTerminalId(rsWireReports.getString("terminalid"));
                agentWireVO.setAccountId(rsWireReports.getString("accountid"));
                agentWireVO.setPayModeId(rsWireReports.getString("paymodeid"));
                agentWireVO.setCardTypeId(rsWireReports.getString("cardtypeid"));
                agentWireVO.setDeclinedCoverDateUpTo(rsWireReports.getString("declinedcoverdateupto"));
                agentWireVO.setReversedCoverDateUpTo(rsWireReports.getString("reversedcoverdateupto"));
                agentWireVO.setChargebackCoverDateUpTo(rsWireReports.getString("chargebackcoverdateupto"));
                agentWireVO.setWireCreationDate(rsWireReports.getString("wirecreationtime"));

                wireVOs.add(agentWireVO);
            }

            //Total records query
            counter=3;
            PreparedStatement psCountOfWireReports= con.prepareStatement(countQuery);
            psCountOfWireReports.setString(1,inputDateVO.getFdtstamp());
            psCountOfWireReports.setString(2,inputDateVO.getTdtstamp());
            logger.debug("terminalId::"+terminalId);
            /*if(functions.isValueNull(terminalId))
            {
                psCountOfWireReports.setString(counter,terminalId);
                counter++;
            }*/
            if (functions.isValueNull(isPaid) && !"all".equals(isPaid))
            {
                psCountOfWireReports.setString(counter,isPaid);
            }
            rsWireReports=psCountOfWireReports.executeQuery();
            if(rsWireReports.next())
            {
                paginationVO.setTotalRecords(rsWireReports.getInt(1));
            }

        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while fetching record from wire manager",systemError);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getListOfAgentWireReportByTerminal()",null,"Common","DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException while fetching record from wire manager",e);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getListOfAgentWireReportByTerminal()",null,"Common","SQL Query Exception",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(rsWireReports);
            Database.closePreparedStatement(psWireReports);
            Database.closeConnection(con);
        }
        return wireVOs;
    }

    public List<WireVO> getListOfMerchantWireReportsForPartner(InputDateVO inputDateVO,TerminalVO terminalVO,PaginationVO paginationVO,String isPaid,String toIdList) throws PZDBViolationException
    {
        StringBuffer query=null;
        String countQuery=null;
        Connection con=null;
        PreparedStatement psWireReports = null;
        ResultSet rsWireReports= null;
        int counter = 3;
        List<WireVO> wireVOs= new ArrayList<WireVO>();
        try
        {

            //con= Database.getConnection();
            con= Database.getRDBConnection();
            query= new StringBuffer("SELECT * FROM merchant_wiremanager WHERE markedfordeletion='N'   and wirecreationtime >=? and wirecreationtime <=? ");
            if(functions.isValueNull(terminalVO.getTerminalId()))
            {
                query.append(" and terminalid =?");
            }
            if (functions.isValueNull(isPaid) && !"all".equals(isPaid))
            {
                query.append(" and status= ?");
            }
            if (functions.isValueNull(terminalVO.getMemberId()))
            {
                query.append(" and toid =?");
            }
            else
            {
                query.append(" and toid IN("+toIdList+")");
            }
            query.append(" ORDER BY timestamp DESC");
            countQuery="Select Count(*) from ("+query.toString()+") as temp";
            query.append("  limit " + paginationVO.getStart() + "," + paginationVO.getEnd());

            psWireReports = con.prepareStatement(query.toString());
            psWireReports.setString(1, inputDateVO.getFdtstamp());
            psWireReports.setString(2,inputDateVO.getTdtstamp());
            if(functions.isValueNull(terminalVO.getTerminalId()))
            {
                psWireReports.setString(counter,terminalVO.getTerminalId());
                counter++;
            }
            if (functions.isValueNull(isPaid) && !"all".equals(isPaid))
            {
                psWireReports.setString(counter,isPaid);
                counter++;
            }
            if (functions.isValueNull(terminalVO.getMemberId()) )
            {
                psWireReports.setString(counter,terminalVO.getMemberId());
            }
            rsWireReports=psWireReports.executeQuery();
            logger.debug("psWireReports::::"+psWireReports);
            while(rsWireReports.next())
            {
                WireVO wireVO = new WireVO();
                TerminalVO terminalVO1=new TerminalVO();
                terminalVO1.setTerminalId(rsWireReports.getString("terminalid"));
                terminalVO1.setMemberId(rsWireReports.getString("toid"));
                terminalVO1.setAccountId(rsWireReports.getString("accountid"));
                terminalVO1.setPaymodeId(rsWireReports.getString("paymodeid"));
                terminalVO1.setCardTypeId(rsWireReports.getString("cardtypeid"));
                wireVO.setSettleId(rsWireReports.getString("settledid"));
                wireVO.setTerminalVO(terminalVO1);
                wireVO.setCurrency(rsWireReports.getString("currency"));
                wireVO.setSettleDate(rsWireReports.getString("settledate"));
                wireVO.setFirstDate(rsWireReports.getString("firstdate"));
                wireVO.setLastDate(rsWireReports.getString("lastdate"));
                wireVO.setNetFinalAmount(rsWireReports.getDouble("netfinalamount"));
                wireVO.setUnpaidAmount(rsWireReports.getDouble("unpaidamount"));
                wireVO.setSettlementReportFilePath(rsWireReports.getString("settlementreportfilepath"));
                wireVO.setSettledTransactionFilePath(rsWireReports.getString("settledtransactionfilepath"));
                wireVO.setStatus(rsWireReports.getString("status"));
                wireVO.setMemberId(rsWireReports.getString("toid"));
                wireVO.setTerminalId(rsWireReports.getString("terminalid"));
                wireVO.setSettlementCycleNo(rsWireReports.getString("settlementcycle_no"));
                wireVO.setDateOfReportGeneration(rsWireReports.getString("wirecreationtime"));
                wireVOs.add(wireVO);
            }

            //Total records query
            counter=3;
            PreparedStatement psCountOfWireReports= con.prepareStatement(countQuery);
            psCountOfWireReports.setString(1,inputDateVO.getFdtstamp());
            psCountOfWireReports.setString(2,inputDateVO.getTdtstamp());
            logger.debug("terminalId::"+terminalVO.getTerminalId());
            if(functions.isValueNull(terminalVO.getTerminalId()))
            {
                psCountOfWireReports.setString(counter,terminalVO.getTerminalId());
                counter++;
            }
            if (functions.isValueNull(isPaid) && !"all".equals(isPaid))
            {
                psCountOfWireReports.setString(counter,isPaid);
                counter++;
            }
            if (functions.isValueNull(terminalVO.getMemberId()) )
            {
                psCountOfWireReports.setString(counter,terminalVO.getMemberId());
            }
            rsWireReports=psCountOfWireReports.executeQuery();
            if(rsWireReports.next())
            {
                paginationVO.setTotalRecords(rsWireReports.getInt(1));
            }

        }
        catch (SystemError systemError)
        {
            logger.error("error while fetching record from wire manager",systemError);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getListOfMerchantWireReportsForPartner()",null,"Common","DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Error while fetching record from wire manager",e);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getListOfMerchantWireReportsForPartner()",null,"Common","SQL Query Exception",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(rsWireReports);
            Database.closePreparedStatement(psWireReports);
            Database.closeConnection(con);
        }
        return wireVOs;

    }

    public List<WireVO> getCycleId(String cycleId) throws PZDBViolationException
    {
        StringBuffer query=null;
        Connection con=null;
        PreparedStatement psWireReports = null;
        ResultSet rsWireReports= null;
        List<WireVO> wireVOs= new ArrayList<WireVO>();
        try
        {
            con= Database.getRDBConnection();
            query= new StringBuffer("SELECT * FROM merchant_wiremanager WHERE settlementcycle_no=? ");
            psWireReports = con.prepareStatement(query.toString());
            psWireReports.setString(1, cycleId);
            rsWireReports=psWireReports.executeQuery();
            logger.debug("psWireReports::::"+psWireReports);
            while(rsWireReports.next())
            {
                WireVO wireVO = new WireVO();
                wireVO.setSettlementCycleNo(rsWireReports.getString("settlementcycle_no"));
                wireVOs.add(wireVO);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("error while fetching record from wire manager",systemError);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getListOfMerchantWireReportsForPartner()",null,"Common","DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Error while fetching record from wire manager",e);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getListOfMerchantWireReportsForPartner()",null,"Common","SQL Query Exception",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(rsWireReports);
            Database.closePreparedStatement(psWireReports);
            Database.closeConnection(con);
        }
        return wireVOs;
    }

    public List<WireVO> getListOfPartnerWireReports(InputDateVO inputDateVO,PaginationVO paginationVO,String isPaid,String partnerId) throws PZDBViolationException
    {
        StringBuffer query=null;
        String countQuery=null;
        Connection con=null;
        PreparedStatement psWireReports = null;
        ResultSet rsWireReports= null;
        int counter = 3;
        List<WireVO> wireVOs= new ArrayList<WireVO>();
        try
        {

            //con= Database.getConnection();
            con= Database.getRDBConnection();
            query= new StringBuffer("SELECT * FROM partner_wiremanager WHERE markedfordeletion='N'   and wirecreationtime >=? and wirecreationtime <=? ");
            if(functions.isValueNull(partnerId))
            {
                query.append(" and partnerid IN("+partnerId+")");
                counter++;
            }
            if (functions.isValueNull(isPaid) && !"all".equals(isPaid))
            {
                query.append(" and status= ?");
            }
            query.append(" ORDER BY timestamp DESC");
            countQuery="Select Count(*) from ("+query.toString()+") as temp";
            logger.debug("countQuery:-"+countQuery);
            query.append("  limit "+paginationVO.getStart()+","+paginationVO.getEnd());
            logger.debug("Query:-"+query);

            psWireReports = con.prepareStatement(query.toString());
            psWireReports.setString(1,inputDateVO.getFdtstamp());
            psWireReports.setString(2,inputDateVO.getTdtstamp());
            /*if(functions.isValueNull(partnerId))
            {
                psWireReports.setString(counter,partnerId);
                counter++;
            }*/
            if (functions.isValueNull(isPaid) && !"all".equals(isPaid))
            {
                psWireReports.setString(counter,isPaid);
            }
            rsWireReports=psWireReports.executeQuery();
            while(rsWireReports.next())
            {
                WireVO wireVO = new WireVO();
                TerminalVO terminalVO1=new TerminalVO();
                terminalVO1.setTerminalId(rsWireReports.getString("terminalid"));
                terminalVO1.setMemberId(rsWireReports.getString("toid"));
                terminalVO1.setAccountId(rsWireReports.getString("accountid"));
                terminalVO1.setPaymodeId(rsWireReports.getString("paymodeid"));
                terminalVO1.setCardTypeId(rsWireReports.getString("cardtypeid"));
                wireVO.setSettleId(rsWireReports.getString("settledid"));
                wireVO.setTerminalVO(terminalVO1);
                wireVO.setCurrency(rsWireReports.getString("currency"));
                wireVO.setSettleDate(rsWireReports.getString("settledate"));
                wireVO.setFirstDate(rsWireReports.getString("settlementstartdate"));
                wireVO.setLastDate(rsWireReports.getString("settlementenddate"));
                wireVO.setNetFinalAmount(rsWireReports.getDouble("partnertotalfundedamount"));
                wireVO.setUnpaidAmount(rsWireReports.getDouble("partnerunpaidamount"));
                wireVO.setSettlementReportFilePath(rsWireReports.getString("settlementreportfilename"));
                wireVO.setStatus(rsWireReports.getString("status"));
                wireVOs.add(wireVO);
            }

            //Total records query
            counter=3;
            PreparedStatement psCountOfWireReports= con.prepareStatement(countQuery);
            psCountOfWireReports.setString(1,inputDateVO.getFdtstamp());
            psCountOfWireReports.setString(2,inputDateVO.getTdtstamp());
            if(functions.isValueNull(partnerId))
            {
               // psCountOfWireReports.setString(counter,partnerId);
                counter++;
            }
            if (functions.isValueNull(isPaid) && !"all".equals(isPaid))
            {
                psCountOfWireReports.setString(counter,isPaid);
            }
            rsWireReports=psCountOfWireReports.executeQuery();
            if(rsWireReports.next())
            {
                paginationVO.setTotalRecords(rsWireReports.getInt(1));
            }

        }
        catch (SystemError systemError)
        {
            logger.error("error while fetching record from wire manager",systemError);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getListOfPartnerWireReports()",null,"Common","DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Error while fetching record from wire manager",e);
            PZExceptionHandler.raiseDBViolationException(AccountDAO.class.getName(),"getListOfPartnerWireReports()",null,"Common","SQL Query Exception",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(rsWireReports);
            Database.closePreparedStatement(psWireReports);
            Database.closeConnection(con);
        }
        return wireVOs;

    }
    public String getProcessingCurrency(String accountId)
    {
        String processingCurrency="";
        Connection connection=null;
        ResultSet rs=null;
        PreparedStatement pstmt=null;
        try{
            connection=Database.getConnection();
            String query="SELECT gt.currency FROM gateway_type AS gt JOIN gateway_accounts AS ga WHERE ga.pgtypeid=gt.pgtypeid AND ga.accountid=?";
            pstmt=connection.prepareStatement(query);
            pstmt.setString(1, accountId);
            rs=pstmt.executeQuery();
            if(rs.next()){
                processingCurrency=rs.getString("currency");
            }
        }catch (Exception e){
            logger.error("Exception::::",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return processingCurrency;
    }
}
