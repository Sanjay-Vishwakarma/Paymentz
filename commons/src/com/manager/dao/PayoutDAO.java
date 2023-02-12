package com.manager.dao;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.manager.utils.AccountUtil;
import com.manager.vo.*;
import com.manager.vo.payoutVOs.*;
import com.payment.PZTransactionStatus;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.PZPayoutRequest;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.io.File;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 8/1/14
 * Time: 1:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayoutDAO
{
    private static Logger logger = new Logger(PayoutDAO.class.getName());
    public static Hashtable getMerchantWireList(String toid, String accountid, String terminalid, String cycleid, String parentcycleid, String gateway, String is_paid, String fdtstamp, String tdtstamp, int pageno, int records,String reportid)
    {
        Functions functions = new Functions();
        Hashtable hash = new Hashtable();
        Connection connection = null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;

        try
        {
            connection = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT settledid,settledate,firstdate,lastdate,amount,balanceamount,netfinalamount,unpaidamount,mw.currency,mw.STATUS,settlementreportfilepath,settledtransactionfilepath,markedfordeletion,mw.TIMESTAMP,mw.toid,mw.terminalid,mw.accountid,mw.paymodeid,mw.cardtypeid,isrollingreserveincluded,rollingreservereleasedateupto,settlementcycle_no,declinedcoverdateupto,reversedcoverdateupto,chargebackcoverdateupto,FROM_UNIXTIME(wirecreationtime) AS 'wirecreationdate',payoutid ,mw.parentsettlementcycle_no FROM merchant_wiremanager AS mw, gateway_accounts AS ga, gateway_type AS gt WHERE mw.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND markedfordeletion='N'");
            StringBuffer countquery = new StringBuffer("SELECT count(*) FROM merchant_wiremanager AS mw, gateway_accounts AS ga, gateway_type AS gt WHERE mw.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND markedfordeletion='N'");

            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and wirecreationtime >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
                countquery.append(" and wirecreationtime >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
            }

            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and wirecreationtime <= " +ESAPI.encoder().encodeForSQL(me, tdtstamp));
                countquery.append(" and wirecreationtime <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
            }

            if (functions.isValueNull(toid) && !toid.equals("0"))
            {
                query.append(" and mw.toid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
                countquery.append(" and mw.toid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
            }

            if (functions.isValueNull(accountid) && !accountid.equals("0"))
            {
                query.append(" and mw.accountid='" + ESAPI.encoder().encodeForSQL(me,accountid) + "'");
                countquery.append(" and mw.accountid='" + ESAPI.encoder().encodeForSQL(me,accountid) + "'");
            }
            if (functions.isValueNull(terminalid))
            {
                query.append(" and mw.terminalid='" + ESAPI.encoder().encodeForSQL(me,terminalid) + "'");
                countquery.append(" and mw.terminalid='" + ESAPI.encoder().encodeForSQL(me,terminalid) + "'");
            }
            if (functions.isValueNull(cycleid))
            {
                query.append(" and mw.settlementcycle_no='" + ESAPI.encoder().encodeForSQL(me,cycleid) + "'");
                countquery.append(" and mw.settlementcycle_no='" + ESAPI.encoder().encodeForSQL(me,cycleid) + "'");
            }
            if (functions.isValueNull(parentcycleid))
            {
                query.append(" and mw.parentsettlementcycle_no='" + ESAPI.encoder().encodeForSQL(me,parentcycleid) + "'");
                countquery.append(" and mw.parentsettlementcycle_no='" + ESAPI.encoder().encodeForSQL(me,parentcycleid) + "'");
            }
            if (functions.isValueNull(reportid))
            {
                query.append(" and mw.reportid='" + ESAPI.encoder().encodeForSQL(me,reportid) + "'");
                countquery.append(" and mw.reportid='" + ESAPI.encoder().encodeForSQL(me,reportid) + "'");
            }
            if (functions.isValueNull(gateway) && !gateway.equals("0"))
            {
                query.append(" and gt.pgtypeid='" + ESAPI.encoder().encodeForSQL(me,gateway) + "'");
                countquery.append(" and gt.pgtypeid='" + ESAPI.encoder().encodeForSQL(me,gateway) + "'");
            }

            if (functions.isValueNull(is_paid))
            {

                query.append(" and status='"+ ESAPI.encoder().encodeForSQL(me,is_paid) +"'");
                countquery.append(" and status='"+ ESAPI.encoder().encodeForSQL(me,is_paid) +"'");

            }
            query.append(" ORDER BY settledid DESC limit "+start + "," +end);
            logger.debug("Query in getMerchantWireList() Method:-"+query);
            logger.debug("CountQuery in getMerchantWireList() Method:-"+countquery);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), connection));
            ResultSet rs = Database.executeQuery(countquery.toString(), connection);
            int totalrecords=0;
            if(rs.next())
            {
                totalrecords = rs.getInt(1);
            }
            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

            logger.debug("wire hash---"+hash);

        }
        catch (SystemError systemError)
        {
            logger.error("SystemError----" + systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException---"+e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return hash;
    }

    // Insert PayOut Method Data
    public static String getMerchantpayOutHash(PayoutDetailsVO payoutDetailsVO)
    {
        String status = null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        int payoutid=0;
        //SimpleDateFormat targetFormat = null;
        try
        {
            //targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            conn = Database.getConnection();
            String query = "INSERT INTO merchant_payout_details(settledid, payout_date, payout_currency, conversion_rate, payout_amount, beneficiary_bank_details, remitter_bank_details, remarks, swift_message,swift_upload, payment_receipt_date,payment_receipt_confirmation ) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1,payoutDetailsVO.getSettledId());
            pstmt.setString(2,payoutDetailsVO.getPayoutDate());
            pstmt.setString(3,payoutDetailsVO.getPayoutCurrency());
            pstmt.setString(4,payoutDetailsVO.getConversionRate());
            pstmt.setString(5, payoutDetailsVO.getPayoutAmount());
            pstmt.setString(6, payoutDetailsVO.getBeneficiaryBankDetails());
            pstmt.setString(7, payoutDetailsVO.getRemitterBankDetails());
            pstmt.setString(8,payoutDetailsVO.getRemarks());
            pstmt.setString(9, payoutDetailsVO.getSwiftMessage());
            pstmt.setString(10, payoutDetailsVO.getSwiftUpload());
            pstmt.setString(11, payoutDetailsVO.getPaymentReceiptDate());
            pstmt.setString(12, payoutDetailsVO.getPaymentReceiptConfirmation());

            logger.debug("pstmt query::::"+pstmt);
           /* int reportid = Integer.valueOf(payoutDetailsVO.getCycleid());
            Functions function = new Functions();
            if(function.isValueNull(payoutDetailsVO.getCycleid())){
                reportid=UpdateOLDReportid(payoutDetailsVO.getSettledId());
            }
*/
            int num = pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                payoutid= rs.getInt(1);
                String queryUpdate = "UPDATE merchant_wiremanager SET payoutid="+payoutid+" WHERE reportid="+payoutDetailsVO.getCycleid();
                pstmt = conn.prepareStatement(queryUpdate);
                pstmt.executeUpdate();
            }
            if (num == 1)
            {
                logger.debug("New payout added successfully.");
                status = "Payout Added Successfully for Payout Id="+payoutid;
            }
            else
            {
                logger.debug("payout Check The JDBC Code For Add Payout");
                status = "Failure";
            }
        }
        catch (SystemError error)
        {
            status = "Failure";
            logger.error(error);
        }
        catch (SQLException e)
        {
            status = "Failure";
            logger.error(e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public static int UpdateOLDReportid(String settledid)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        int reportid=getreportid();
        try
        {
            con = Database.getConnection();
            String query = "UPDATE merchant_wiremanager SET reportid="+reportid+" WHERE settledid="+settledid;
            preparedStatement = con.prepareStatement(query);
            int k = preparedStatement.executeUpdate();
            if (k == 1)
            {
                return  reportid;
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
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return reportid;
    }

    //End


    // Insert PayOut Method Data
    public static String merchantpayOutUpdate(PayoutDetailsVO payoutDetailsVO)
    {
        String status = null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        Functions functions = new Functions();
        int counter = 11;
        //SimpleDateFormat targetFormat = null;
        try
        {
            //targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            conn = Database.getConnection();
            StringBuffer query = new StringBuffer("UPDATE merchant_payout_details set payout_date =?, payout_currency =?, conversion_rate = ?, payout_amount =?, beneficiary_bank_details = ?, remitter_bank_details =?, remarks = ?, swift_message = ?, payment_receipt_date = ? , payment_receipt_confirmation = ?");
            if (functions.isValueNull(payoutDetailsVO.getSwiftUpload()) )
                query.append(", swift_upload = ?  ");
            query.append(" WHERE  payout_id=?");
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1,payoutDetailsVO.getPayoutDate());
            pstmt.setString(2,payoutDetailsVO.getPayoutCurrency());
            pstmt.setString(3, payoutDetailsVO.getConversionRate());
            pstmt.setString(4, payoutDetailsVO.getPayoutAmount());
            pstmt.setString(5, payoutDetailsVO.getBeneficiaryBankDetails());
            pstmt.setString(6,payoutDetailsVO.getRemitterBankDetails());
            pstmt.setString(7,payoutDetailsVO.getRemarks());
            pstmt.setString(8, payoutDetailsVO.getSwiftMessage());
            pstmt.setString(9, payoutDetailsVO.getPaymentReceiptDate());
            pstmt.setString(10, payoutDetailsVO.getPaymentReceiptConfirmation());
            if (functions.isValueNull(payoutDetailsVO.getSwiftUpload()))
            {
                pstmt.setString(counter, payoutDetailsVO.getSwiftUpload());
                counter++;
            }

            pstmt.setString(counter, payoutDetailsVO.getPayoutId());


            int num = pstmt.executeUpdate();
            if (num == 1)
            {
                logger.debug("New Payout Updated successfully.");
                status = "Payout Updated Successfully";
            }
            else
            {
                logger.debug("payout Check The JDBC Code For Generate Wire");
                status = "Failure";
            }
        }
        catch (SystemError error)
        {
            status = "Failure";
            logger.error(error);
        }
        catch (SQLException e)
        {
            status = "Failure";
            logger.error(e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public static Hashtable getISOWireReport(String accountid, String gateway, String isPaid, String fdtstamp, String tdtstamp, int pageno, int records)
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
            StringBuffer query = new StringBuffer("SELECT iso_comm_id,icw.accountid,startdate,enddate,settleddate,amount,netfinalamount,unpaidamount,icw.currency,icw.status,reportfilepath,transactionfilepath,FROM_UNIXTIME(creationdate) AS wirecreationtime FROM iso_commission_wire_manager AS icw, gateway_accounts AS ga, gateway_type AS gt WHERE icw.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND icw.iso_comm_id>0 ");
            StringBuffer countquery = new StringBuffer("SELECT count(*) FROM iso_commission_wire_manager AS icw, gateway_accounts AS ga, gateway_type AS gt WHERE icw.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND iso_comm_id>0");
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and creationdate >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
                countquery.append(" and creationdate >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and creationdate <= " +ESAPI.encoder().encodeForSQL(me, tdtstamp));
                countquery.append(" and creationdate <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
            }
            if (functions.isValueNull(accountid) && !accountid.equals("0"))
            {
                query.append(" and icw.accountid='" + ESAPI.encoder().encodeForSQL(me,accountid) + "'");
                countquery.append(" and icw.accountid='" + ESAPI.encoder().encodeForSQL(me,accountid) + "'");
            }
            if (functions.isValueNull(gateway) && !gateway.equals("0"))
            {
                query.append(" and gt.pgtypeid='" + ESAPI.encoder().encodeForSQL(me,gateway) + "'");
                countquery.append(" and gt.pgtypeid='" + ESAPI.encoder().encodeForSQL(me,gateway) + "'");
            }
            if (functions.isValueNull(isPaid))
            {
                if(isPaid.equalsIgnoreCase("Y"))
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
            query.append(" ORDER BY creationdate DESC limit "+start + "," +end);

            logger.debug("Query:-" + query);
            logger.debug("CountQuery:-"+countquery);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), connection));

            rs = Database.executeQuery(countquery.toString(), connection);
            int totalrecords=0;
            if(rs.next())
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
            logger.error("SystemError----" + systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException---"+e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(connection);
        }
        return hash;
    }
    public static List<TerminalVO> getTerminalForMerchantWireList()
    {
        TerminalVO terminalVO=null;
        List<TerminalVO> terminalVOList=new ArrayList();
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT DISTINCT terminalid,mam.isActive,paymodeid,cardtypeid,gt.currency FROM member_account_mapping AS mam JOIN gateway_accounts AS ga JOIN gateway_type AS gt WHERE mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid ORDER BY terminalid ASC";
            pstmt= conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                terminalVO=new TerminalVO();
                terminalVO.setTerminalId(rs.getString("terminalid"));
                terminalVO.setIsActive(rs.getString("isActive"));
                terminalVO.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVO.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVO.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                terminalVO.setPaymodeId(rs.getString("paymodeid"));
                terminalVO.setCurrency((rs.getString("currency")));
                terminalVOList.add(terminalVO);
            }
        }
        catch (SQLException e)
        {
            logger.debug("SQLException---"+e);
        }
        catch (SystemError systemError)
        {
            logger.debug("SystemError---"+systemError);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return terminalVOList;
    }

    public static List<TerminalVO> loadcardtypeids()
    {
        WireVO wireVO = new WireVO();
        TerminalVO terminalVO = wireVO.getTerminalVO();
        List<TerminalVO> cardtypeList = new ArrayList();
        Connection conn=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT * FROM card_type ORDER BY cardtype ASC";
            pstmt = conn.prepareStatement(query);
            logger.debug("query loadcardtypeids----" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                terminalVO = new TerminalVO();
                terminalVO.setCardType(rs.getString("cardtype"));
                terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                cardtypeList.add(terminalVO);
            }
        }
        catch(Exception e)
        {
            logger.error("Exception while loading paymodeids",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return cardtypeList;
    }

    public static List<TerminalVO> loadPaymodeids()
    {
        WireVO wireVO = new WireVO();
        TerminalVO terminalVO = wireVO.getTerminalVO();
        List<TerminalVO> paymodeList = new ArrayList();
        Connection conn=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT * FROM payment_type ORDER BY paymentType ASC";
            pstmt = conn.prepareStatement(query);
            logger.debug("query loadPaymodeids----" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                terminalVO = new TerminalVO();
                terminalVO.setPaymentTypeName(rs.getString("paymentType"));
                terminalVO.setPaymodeId(rs.getString("paymodeid"));
                terminalVO.setPaymentName(rs.getString("paymode"));
                paymodeList.add(terminalVO);

            }
        }
        catch(Exception e)
        {
            logger.error("Exception while loading paymodeids",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return paymodeList;
    }

    public static List<ChargeVO> loadchargename()
    {
        ChargeVO chargeVO = null;
        List<ChargeVO> chargeNameList = new ArrayList();
        Connection conn=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "Select chargeid, chargename, valuetype,keyword from charge_master";
            pstmt = conn.prepareStatement(query);
            logger.debug("query loadcardtypeids----" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                chargeVO = new ChargeVO();
                chargeVO.setChargename(rs.getString("chargename"));
                chargeVO.setChargeid(rs.getString("chargeid"));
                chargeVO.setValuetype(rs.getString("valuetype"));
                chargeVO.setKeyword(rs.getString("keyword"));
                chargeNameList.add(chargeVO);
            }
        }
        catch(Exception e)
        {
            logger.error("Exception while loading paymodeids",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return chargeNameList;
    }

    public String getMemberFirstTransactionDateOnTerminal(TerminalVO terminalVO)
    {
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameSettlement(terminalVO.getAccountId());
        String strQuery = null;
        String memberFirstTransactionDate = null;
        ResultSet rsPayout = null;
        PreparedStatement preparedStatement=null;
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT MIN(trackingid),FROM_UNIXTIME(dtstamp) AS 'FirstTransDate' FROM " + tableName + " WHERE toid=? and accountid=? and paymodeid=? and cardtypeid=?";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getPaymodeId());
            preparedStatement.setString(4, terminalVO.getCardTypeId());
            logger.error("query----"+preparedStatement);

            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                memberFirstTransactionDate = rsPayout.getString("FirstTransDate");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return memberFirstTransactionDate;
    }

    public String getMemberFirstTransactionOnTerminal(TerminalVO terminalVO)
    {
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameSettlement(terminalVO.getAccountId());
        String strQuery = null;
        String memberFirstTransactionDate = null;
        ResultSet rsPayout = null;
        PreparedStatement preparedStatement=null;
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT MIN(trackingid),FROM_UNIXTIME(dtstamp) AS 'FirstTransDate' FROM " + tableName + " WHERE toid=? and accountid=? and terminalid=?";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getTerminalId());
            logger.error("query----"+preparedStatement);

            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                memberFirstTransactionDate = rsPayout.getString("FirstTransDate");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return memberFirstTransactionDate;
    }


    public String getMemberFirstTransactionDateOnTerminalId(TerminalVO terminalVO)
    {
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameSettlement(terminalVO.getAccountId());
        String strQuery = null;
        String memberFirstTransactionDate = null;
        ResultSet rsPayout = null;
        PreparedStatement preparedStatement=null;
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT MIN(trackingid),FROM_UNIXTIME(dtstamp) AS 'FirstTransDate' FROM " + tableName + " WHERE toid=? and accountid=? and paymodeid=? and cardtypeid=? and terminalId=? GROUP BY terminalId";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getPaymodeId());
            preparedStatement.setString(4, terminalVO.getCardTypeId());
            preparedStatement.setString(5, terminalVO.getTerminalId());
            rsPayout = preparedStatement.executeQuery();
            logger.error("ps-----"+preparedStatement);
            if (rsPayout.next())
            {
                memberFirstTransactionDate = rsPayout.getString("FirstTransDate");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return memberFirstTransactionDate;
    }

    public String getMemberFirstTransactionDate(String memberId, String accountId)
    {
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameSettlement(accountId);
        String strQuery = null;
        String memberFirstTransactionDate = null;
        ResultSet rsPayout = null;
        PreparedStatement preparedStatement=null;
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT MIN(trackingid),FROM_UNIXTIME(dtstamp) AS 'FirstTransDate' FROM " + tableName + " WHERE toid=? and accountid=?";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, memberId);
            preparedStatement.setString(2, accountId);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                memberFirstTransactionDate = rsPayout.getString("FirstTransDate");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return memberFirstTransactionDate;
    }

    public String getMemberFirstTransactionDateOnAccountId(String memberId, String accountId)
    {
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameSettlement(accountId);
        String strQuery = null;
        String memberFirstTransactionDate = null;
        ResultSet rsPayout = null;
        PreparedStatement preparedStatement=null;
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            //strQuery="SELECT FROM_UNIXTIME(dtstamp) AS 'FirstTransDate' FROM "+  tableName + " WHERE trackingid IN(SELECT MIN(trackingid) FROM " + tableName + " WHERE toid=? and accountid=?)";
            strQuery = "SELECT MIN(trackingid),FROM_UNIXTIME(dtstamp) AS 'FirstTransDate' FROM " + tableName + " WHERE toid=? and accountid=?";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, memberId);
            preparedStatement.setString(2, accountId);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                memberFirstTransactionDate = rsPayout.getString("FirstTransDate");
            }
            else
                logger.debug("ResultSet is empty...");
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return memberFirstTransactionDate;
    }
    public String getMemberFirstTransactionDateOnAccountId(String memberId, String accountId,String terminalId)
    {
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameSettlement(accountId);
        String strQuery = null;
        String memberFirstTransactionDate = null;
        ResultSet rsPayout = null;
        PreparedStatement preparedStatement=null;
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            //strQuery="SELECT FROM_UNIXTIME(dtstamp) AS 'FirstTransDate' FROM "+  tableName + " WHERE trackingid IN(SELECT MIN(trackingid) FROM " + tableName + " WHERE toid=? and accountid=?)";
            strQuery = "SELECT MIN(trackingid),FROM_UNIXTIME(dtstamp) AS 'FirstTransDate' FROM " + tableName + " WHERE toid=? and accountid=? and terminalid=? GROUP BY terminalid";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, memberId);
            preparedStatement.setString(2, accountId);
            preparedStatement.setString(3, terminalId);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                memberFirstTransactionDate = rsPayout.getString("FirstTransDate");
            }
            else
                logger.debug("ResultSet is empty...");
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return memberFirstTransactionDate;
    }

    public String getReserveReleaseStartDate(String memberId)
    {
        String strQuery = null;
        String rollingreserveStartDate = null;
        ResultSet rsPayout = null;
        PreparedStatement preparedStatement=null;
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT WM.rollingreservereleasedateupto AS 'rollingreservestartdate' FROM wiremanager AS WM WHERE toid=? ORDER BY WM.settledid DESC LIMIT 1";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, memberId);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                rollingreserveStartDate = rsPayout.getString("rollingreservestartdate");
            }
            else
                logger.debug("ResultSet is empty...");
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return rollingreserveStartDate;
    }

    public String getReserveReleaseStartDateOnAccount(String memberId, String accountId)
    {
        String strQuery = null;
        String rollingreserveStartDate = null;
        ResultSet rsPayout = null;
        PreparedStatement preparedStatement=null;
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT WM.rollingreservereleasedateupto AS 'rollingreservestartdate' FROM wiremanager AS WM WHERE toid=? and accountid=? ORDER BY WM.settledid DESC LIMIT 1";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, memberId);
            preparedStatement.setString(2, accountId);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                rollingreserveStartDate = rsPayout.getString("rollingreservestartdate");
            }
            else
                logger.debug("ResultSet is empty...");
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return rollingreserveStartDate;
    }

    public String getRollingReserveStartDateMerchantPayout(String memberId, String accountId)
    {
        String strQuery = null;
        String rollingreserveStartDate = null;
        ResultSet rsPayout = null;
        PreparedStatement preparedStatement=null;
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT WM.rollingreservereleasedateupto AS 'rollingreservestartdate' FROM merchant_wiremanager AS WM WHERE toid=? and accountid=? ORDER BY WM.settledid DESC LIMIT 1";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, memberId);
            preparedStatement.setString(2, accountId);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                rollingreserveStartDate = rsPayout.getString("rollingreservestartdate");
            }
            else
                logger.debug("ResultSet is empty...");
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return rollingreserveStartDate;
    }

    public String getRollingReserveStartDateMerchantPayout(String memberId, String accountId,String terminalId)
    {
        String strQuery = null;
        String rollingreserveStartDate = null;
        ResultSet rsPayout = null;
        PreparedStatement preparedStatement=null;
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT WM.rollingreservereleasedateupto AS 'rollingreservestartdate' FROM merchant_wiremanager AS WM WHERE toid=? and accountid=? and terminalid=? ORDER BY WM.settledid DESC LIMIT 1";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, memberId);
            preparedStatement.setString(2, accountId);
            preparedStatement.setString(3, terminalId);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                rollingreserveStartDate = rsPayout.getString("rollingreservestartdate");
            }
            else
                logger.debug("ResultSet is empty...");
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return rollingreserveStartDate;
    }

    public String getRRDateFromBankRollingReserveManager(String accountId)
    {
        String strQuery = null;
        String rollingreserveEndDate = null;
        PreparedStatement preparedStatement=null;
        ResultSet rsPayout = null;
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT BRRM.rollingreservereleaseupto AS 'rollingreserveenddate' FROM bank_rollingreserve_master AS BRRM WHERE BRRM.accountId=? ORDER BY BRRM.id DESC LIMIT 1";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, accountId);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                rollingreserveEndDate = rsPayout.getString("rollingreserveenddate");
            }
            else
                logger.debug("ResultSet is empty...");
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return rollingreserveEndDate;
    }

    public List<TransactionVO> getTransactionDetailsByDtstamp(String tableName, String settleStartDate, String settleEndDate, TerminalVO terminalVO, PZTransactionStatus status)
    {
        List<TransactionVO> transactionVOs = new LinkedList<TransactionVO>();
        Connection con = null;
        PreparedStatement preparedStatement = null;
        TransactionVO detailsVO = null;
        ResultSet rsPayout = null;
        try
        {
            String query = null;
            con = Database.getRDBConnection();
            if ("transaction_ecore".equalsIgnoreCase(tableName))
            {
                query = "SELECT trackingid,description,FROM_UNIXTIME(dtstamp) as transactiondate,amount,captureamount,currency,STATUS,firstname,lastname FROM transaction_ecore where toid=? and accountid=? and paymodeid=? and cardtypeid=? and FROM_UNIXTIME(dtstamp) >=? and FROM_UNIXTIME(dtstamp) <=? and status=?";
            }
            else if ("transaction_qwipi".equalsIgnoreCase(tableName))
            {
                query = "SELECT trackingid,description,FROM_UNIXTIME(dtstamp) as transactiondate,amount,captureamount,currency,STATUS,firstname,lastname FROM transaction_qwipi where toid=? and accountid=? and paymodeid=? and cardtypeid=? and FROM_UNIXTIME(dtstamp) >=? and FROM_UNIXTIME(dtstamp) <=? and status=?";
            }
            else
            {
                //query = "SELECT trackingid,description,FROM_UNIXTIME(dtstamp) as transactiondate,amount,captureamount,currency,STATUS,firstname,lastname,walletAmount,walletCurrency FROM transaction_common AS tc, transaction_common_details AS tcd where toid=? and accountid=? and paymodeid=? and cardtypeid=? and FROM_UNIXTIME(dtstamp) >=? and FROM_UNIXTIME(dtstamp) <=? and status=?";
                query = "SELECT trackingid,description,FROM_UNIXTIME(dtstamp) AS transactiondate,amount,captureamount,currency,STATUS,firstname,lastname FROM transaction_common WHERE toid=? AND accountid=? AND paymodeid=? AND cardtypeid=? AND FROM_UNIXTIME(dtstamp) >=? AND FROM_UNIXTIME(dtstamp) <=? AND STATUS=?";
            }

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getPaymodeId());
            preparedStatement.setString(4, terminalVO.getCardTypeId());
            preparedStatement.setString(5, settleStartDate);
            preparedStatement.setString(6, settleEndDate);
            preparedStatement.setString(7, status.toString());
            // preparedStatement.setString(8, detailsVO.getWalletAmount());
            // preparedStatement.setString(9, detailsVO.getWalletCurrency());
            rsPayout = preparedStatement.executeQuery();
            logger.debug("Query:::::::::::;" + query);
            logger.debug("preparedStatement:::::::::::;"+preparedStatement);
            logger.debug("rsPayout:::::::::::;"+rsPayout);
            while (rsPayout.next())
            {
                detailsVO = new TransactionVO();
                detailsVO.setTrackingId(rsPayout.getString("trackingid"));
                detailsVO.setTransactionDate(rsPayout.getString("transactiondate"));
                detailsVO.setCustFirstName(rsPayout.getString("firstname"));
                detailsVO.setCustLastName(rsPayout.getString("lastname"));
                detailsVO.setOrderId(rsPayout.getString("description"));
                detailsVO.setStatus(rsPayout.getString("STATUS"));
                detailsVO.setCurrency(rsPayout.getString("currency"));
                detailsVO.setAmount(rsPayout.getString("amount"));
                detailsVO.setCaptureAmount(rsPayout.getDouble("captureamount"));
                detailsVO.setWalletAmount("");
                detailsVO.setWalletCurrency("");
                //detailsVO.setCaptureAmount(rsPayout.getDouble("captureamount"));
                transactionVOs.add(detailsVO);
                logger.debug("Query:::::::::::;" + query);
                logger.debug("preparedStatement:::::::::::;"+preparedStatement);
                logger.debug("rsPayout:::::::::::;"+rsPayout);
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionVOs;
    }
    public List<TransactionVO> getRollingReserveTransaction(String rollingReserveStartDate, String rollingReserveEndDate, TerminalVO terminalVO)
    {
        List<TransactionVO> transactionVOs = new LinkedList<TransactionVO>();
        Connection con = null;
        PreparedStatement preparedStatement = null;
        TransactionVO detailsVO = null;
        ResultSet rsPayout = null;
        try
        {
            String query = null;
            con = Database.getRDBConnection();
            query = "SELECT t.trackingid,t.description,FROM_UNIXTIME(dtstamp) as transactiondate,t.amount,t.captureamount,t.currency,t.STATUS,t.firstname,t.lastname,bd.isRollingReserveReleased FROM transaction_common as t JOIN bin_details as bd where t.toid=? and t.accountid=? and t.paymodeid=? and t.cardtypeid=? and FROM_UNIXTIME(dtstamp) >=? and FROM_UNIXTIME(dtstamp) <=? and t.terminalid=? and bd.isRollingReserveReleased=? and bd.icicitransid=t.trackingid";

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getPaymodeId());
            preparedStatement.setString(4, terminalVO.getCardTypeId());
            preparedStatement.setString(5, rollingReserveStartDate);
            preparedStatement.setString(6, rollingReserveEndDate);
            preparedStatement.setString(7, terminalVO.getTerminalId());
            preparedStatement.setString(8, "Y");
            logger.error("Query For RollingReverse::::"+preparedStatement);

            rsPayout = preparedStatement.executeQuery();
            while (rsPayout.next())
            {
                detailsVO = new TransactionVO();
                detailsVO.setTrackingId(rsPayout.getString("trackingid"));
                detailsVO.setTransactionDate(rsPayout.getString("transactiondate"));
                detailsVO.setCustFirstName(rsPayout.getString("firstname"));
                detailsVO.setCustLastName(rsPayout.getString("lastname"));
                detailsVO.setOrderId(rsPayout.getString("description"));
                detailsVO.setStatus(rsPayout.getString("STATUS"));
                detailsVO.setCurrency(rsPayout.getString("currency"));
                detailsVO.setAmount(rsPayout.getString("amount"));
                detailsVO.setCaptureAmount(rsPayout.getDouble("captureamount"));
                detailsVO.setIsRollingReserve(rsPayout.getString("isRollingReserveReleased"));
                transactionVOs.add(detailsVO);
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionVOs;
    }

    public List<TransactionVO> getTransactionDetailsByTimestamp(String tableName, String settleStartDate, String settleEndDate, TerminalVO terminalVO, PZTransactionStatus status)
    {
        List<TransactionVO> transactionVOs = new LinkedList<TransactionVO>();
        Connection con = null;
        PreparedStatement preparedStatement = null;
        TransactionVO detailsVO = null;
        ResultSet rsPayout = null;
        String statusIn=null;
        logger.error("status::::"+status.toString());
        if(status.toString().equals("reversed")){
            statusIn="'partialrefund','reversed'";
        }
        try
        {
            String query = null;
            int count = 0;
            con = Database.getRDBConnection();
            if ("transaction_ecore".equalsIgnoreCase(tableName))
            {
                query = "SELECT trackingid,description,FROM_UNIXTIME(dtstamp) as transactiondate,chargebackamount,refundamount,captureamount,currency,STATUS,firstname,lastname FROM transaction_ecore where toid=? and accountid=? and paymodeid=? and cardtypeid=? and timestamp >=? and timestamp<=? and status=?";
            }
            else if ("transaction_qwipi".equalsIgnoreCase(tableName))
            {
                query = "SELECT trackingid,description,FROM_UNIXTIME(dtstamp) as transactiondate,chargebackamount,refundamount,captureamount,currency,STATUS,firstname,lastname FROM transaction_qwipi where toid=? and accountid=? and paymodeid=? and cardtypeid=? and timestamp >=? and timestamp<=? and status=?";
            }
            else
            {
                //query = "SELECT trackingid,description,FROM_UNIXTIME(dtstamp) as transactiondate,chargebackamount,refundamount,captureamount,currency,STATUS,firstname,lastname FROM transaction_common where toid=? and accountid=? and paymodeid=? and cardtypeid=? and timestamp >=? and timestamp<=? and status=?";
                query = "SELECT tc.trackingid,tc.description,FROM_UNIXTIME(dtstamp) as transactiondate,tc.chargebackamount,tc.refundamount,tc.captureamount,tc.currency,tc.STATUS,tc.firstname,tc.lastname,tcd.walletAmount,tcd.walletCurrency,tcd.amount,tcd.timestamp FROM transaction_common AS tc, transaction_common_details AS tcd where toid=? and accountid=? and paymodeid=? and cardtypeid=? and tcd.timestamp >=? and tcd.timestamp<=? AND tcd.status IN("+statusIn+") AND tc.trackingid=tcd.trackingid";

            }
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getPaymodeId());
            preparedStatement.setString(4, terminalVO.getCardTypeId());
            preparedStatement.setString(5, settleStartDate);
            preparedStatement.setString(6, settleEndDate);
            // preparedStatement.setString(7, Status);
            logger.error("Query for reversed:::::"+preparedStatement);
            rsPayout = preparedStatement.executeQuery();
            while (rsPayout.next())
            {
                count = count + 1;
                detailsVO = new TransactionVO();
                detailsVO.setTrackingId(rsPayout.getString("trackingid"));
                detailsVO.setTransactionDate(rsPayout.getString("timestamp"));
                detailsVO.setCustFirstName(rsPayout.getString("firstname"));
                detailsVO.setCustLastName(rsPayout.getString("lastname"));
                detailsVO.setOrderId(rsPayout.getString("description"));
                detailsVO.setStatus(rsPayout.getString("STATUS"));
                detailsVO.setCurrency(rsPayout.getString("currency"));
                detailsVO.setChargebackAmount(rsPayout.getString("chargebackamount"));
                detailsVO.setReverseAmount(rsPayout.getString("refundamount"));
                detailsVO.setWalletAmount(rsPayout.getString("walletAmount"));
                detailsVO.setWalletCurrency(rsPayout.getString("walletCurrency"));
                detailsVO.setRefundAmount(rsPayout.getString("amount"));
                transactionVOs.add(detailsVO);
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionVOs;
    }

    public List<TransactionVO> getTransactionDetailsByTimestampForChargeback(String tableName, String settleStartDate, String settleEndDate, TerminalVO terminalVO, PZTransactionStatus status)
    {
        List<TransactionVO> transactionVOs = new LinkedList<TransactionVO>();
        Connection con = null;
        PreparedStatement preparedStatement = null;
        TransactionVO detailsVO = null;
        ResultSet rsPayout = null;
        try
        {
            String query = null;
            int count = 0;
            con = Database.getRDBConnection();
            query = "SELECT tcd.trackingid,tc.description,FROM_UNIXTIME(dtstamp) as transactiondate,tc.currency,tc.STATUS,tc.firstname,tc.lastname,tcd.walletAmount,tcd.walletCurrency,tcd.amount,tcd.timestamp FROM transaction_common AS tc, transaction_common_details AS tcd where toid=? and accountid=? and paymodeid=? and cardtypeid=? and tcd.timestamp >=? and tcd.timestamp<=? and tcd.status='chargeback' AND tc.trackingid=tcd.trackingid";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getPaymodeId());
            preparedStatement.setString(4, terminalVO.getCardTypeId());
            preparedStatement.setString(5, settleStartDate);
            preparedStatement.setString(6, settleEndDate);
            logger.error("Query for chargeback:::::"+preparedStatement);
            rsPayout = preparedStatement.executeQuery();
            while (rsPayout.next())
            {
                count = count + 1;
                detailsVO = new TransactionVO();
                detailsVO.setTrackingId(rsPayout.getString("trackingid"));
                detailsVO.setTransactionDate(rsPayout.getString("timestamp"));
                detailsVO.setCustFirstName(rsPayout.getString("firstname"));
                detailsVO.setCustLastName(rsPayout.getString("lastname"));
                detailsVO.setOrderId(rsPayout.getString("description"));
                detailsVO.setStatus(rsPayout.getString("STATUS"));
                detailsVO.setCurrency(rsPayout.getString("currency"));
                detailsVO.setChargebackAmount(rsPayout.getString("amount"));
                detailsVO.setWalletAmount(rsPayout.getString("walletAmount"));
                detailsVO.setWalletCurrency(rsPayout.getString("walletCurrency"));
                transactionVOs.add(detailsVO);
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionVOs;
    }

    public List<TransactionVO> getPayoutTransactionDetailsByTimestamp(String tableName, String settleStartDate, String settleEndDate, TerminalVO terminalVO, PZTransactionStatus status)
    {
        List<TransactionVO> transactionVOs = new LinkedList<TransactionVO>();
        Connection con = null;
        PreparedStatement preparedStatement = null;
        TransactionVO detailsVO = null;
        ResultSet rsPayout = null;
        try
        {
            String query = null;
            int count = 0;
            con = Database.getRDBConnection();
            query = "SELECT trackingid,payoutamount,timestamp,description,currency,STATUS,firstname,lastname FROM transaction_common WHERE toid=? AND accountid=? AND terminalid=? AND FROM_UNIXTIME(dtstamp) >=? AND FROM_UNIXTIME(dtstamp)<=? AND STATUS=?";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getTerminalId());
            preparedStatement.setString(4, settleStartDate);
            preparedStatement.setString(5, settleEndDate);
            preparedStatement.setString(6, status.toString());
            logger.error("Query for Payout Excel:::::"+preparedStatement);
            rsPayout = preparedStatement.executeQuery();
            while (rsPayout.next())
            {
                count = count + 1;
                detailsVO = new TransactionVO();
                detailsVO.setTrackingId(rsPayout.getString("trackingid"));
                detailsVO.setTransactionDate(rsPayout.getString("timestamp"));
                detailsVO.setCustFirstName(rsPayout.getString("firstname"));
                detailsVO.setCustLastName(rsPayout.getString("lastname"));
                detailsVO.setOrderId(rsPayout.getString("description"));
                detailsVO.setStatus(rsPayout.getString("STATUS"));
                detailsVO.setCurrency(rsPayout.getString("currency"));
                detailsVO.setPayoutAmount(rsPayout.getString("payoutamount"));
                detailsVO.setWalletAmount("");
                detailsVO.setWalletCurrency("");
                transactionVOs.add(detailsVO);
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionVOs;
    }
    public List<TransactionVO> getRefundReverseTransactionDetailsByTimestamp(String tableName, String settleStartDate, String settleEndDate, TerminalVO terminalVO, PZTransactionStatus status)
    {
        List<TransactionVO> transactionVOs = new LinkedList<TransactionVO>();
        Connection con = null;
        PreparedStatement preparedStatement = null;
        TransactionVO detailsVO = null;
        ResultSet rsPayout = null;
        try
        {
            String query = null;
            int count = 0;
            con = Database.getRDBConnection();
            query = "SELECT tc.trackingid,tc.description,FROM_UNIXTIME(dtstamp) as transactiondate,tcd.amount,tc.currency,tc.STATUS,tc.firstname,tc.lastname,tcd.walletAmount,tcd.walletCurrency,tcd.timestamp FROM transaction_common AS tc, transaction_common_details AS tcd where toid=? and accountid=? and terminalid=? and tcd.status='refundreversed' and tcd.timestamp >=? and tcd.timestamp<=?  AND tc.trackingid=tcd.trackingid";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getTerminalId());
            preparedStatement.setString(4, settleStartDate);
            preparedStatement.setString(5, settleEndDate);
            logger.error("Query for Refund Reverse Excel:::::"+preparedStatement);
            rsPayout = preparedStatement.executeQuery();
            while (rsPayout.next())
            {
                count = count + 1;
                detailsVO = new TransactionVO();
                detailsVO.setTrackingId(rsPayout.getString("trackingid"));
                detailsVO.setTransactionDate(rsPayout.getString("timestamp"));
                detailsVO.setCustFirstName(rsPayout.getString("firstname"));
                detailsVO.setCustLastName(rsPayout.getString("lastname"));
                detailsVO.setOrderId(rsPayout.getString("description"));
                detailsVO.setStatus(rsPayout.getString("STATUS"));
                detailsVO.setCurrency(rsPayout.getString("currency"));
                detailsVO.setAmount(rsPayout.getString("amount"));
                detailsVO.setWalletAmount("");
                detailsVO.setWalletCurrency("");
                transactionVOs.add(detailsVO);
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionVOs;
    }
    public List<TransactionVO> getCaseFilingTransactionDetailsByTimestamp(String tableName, String settleStartDate, String settleEndDate, TerminalVO terminalVO, PZTransactionStatus status)
    {
        List<TransactionVO> transactionVOs = new LinkedList<TransactionVO>();
        Connection con = null;
        PreparedStatement preparedStatement = null;
        TransactionVO detailsVO = null;
        ResultSet rsPayout = null;
        try
        {
            String query = null;
            int count = 0;
            con = Database.getRDBConnection();
            query = "SELECT tc.trackingid,tc.description,FROM_UNIXTIME(dtstamp) as transactiondate,tcd.amount,tc.currency,tc.STATUS,tc.firstname,tc.lastname,tcd.walletAmount,tcd.walletCurrency,tcd.timestamp FROM transaction_common AS tc, transaction_common_details AS tcd where toid=? and accountid=? and terminalid=? and tcd.status='casefiling' and tcd.timestamp >=? and tcd.timestamp<=?  AND tc.trackingid=tcd.trackingid";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getTerminalId());
            preparedStatement.setString(4, settleStartDate);
            preparedStatement.setString(5, settleEndDate);
            logger.error("Query for Case Filing Excel:::::"+preparedStatement);
            rsPayout = preparedStatement.executeQuery();
            while (rsPayout.next())
            {
                count = count + 1;
                detailsVO = new TransactionVO();
                detailsVO.setTrackingId(rsPayout.getString("trackingid"));
                detailsVO.setTransactionDate(rsPayout.getString("timestamp"));
                detailsVO.setCustFirstName(rsPayout.getString("firstname"));
                detailsVO.setCustLastName(rsPayout.getString("lastname"));
                detailsVO.setOrderId(rsPayout.getString("description"));
                detailsVO.setStatus(rsPayout.getString("STATUS"));
                detailsVO.setCurrency(rsPayout.getString("currency"));
                detailsVO.setAmount(rsPayout.getString("amount"));
                detailsVO.setWalletAmount("");
                detailsVO.setWalletCurrency("");
                transactionVOs.add(detailsVO);
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionVOs;
    }
    public List<TransactionVO> getTransactionDetailsForChargeback(String tableName, String settleStartDate, String settleEndDate, TerminalVO terminalVO, PZTransactionStatus status)
    {
        List<TransactionVO> transactionVOs = new LinkedList<TransactionVO>();
        Connection con = null;
        PreparedStatement preparedStatement = null;
        TransactionVO detailsVO = null;
        ResultSet rsPayout = null;
        try
        {
            String query = null;
            int count = 0;
            con = Database.getRDBConnection();
            if ("transaction_ecore".equalsIgnoreCase(tableName))
            {
                query = "SELECT trackingid,description,FROM_UNIXTIME(dtstamp) as transactiondate,chargebackamount,refundamount,captureamount,currency,STATUS,firstname,lastname FROM transaction_ecore where toid=? and accountid=? and paymodeid=? and cardtypeid=? and timestamp >=? and timestamp<=? and status=?";
            }
            else if ("transaction_qwipi".equalsIgnoreCase(tableName))
            {
                query = "SELECT trackingid,description,FROM_UNIXTIME(dtstamp) as transactiondate,chargebackamount,refundamount,captureamount,currency,STATUS,firstname,lastname FROM transaction_qwipi where toid=? and accountid=? and paymodeid=? and cardtypeid=? and timestamp >=? and timestamp<=? and status=?";
            }
            else
            {
                //query = "SELECT trackingid,description,FROM_UNIXTIME(dtstamp) as transactiondate,chargebackamount,refundamount,captureamount,currency,STATUS,firstname,lastname FROM transaction_common where toid=? and accountid=? and paymodeid=? and cardtypeid=? and timestamp >=? and timestamp<=? and status=?";
                query = " SELECT trackingid,description,FROM_UNIXTIME(dtstamp) AS transactiondate,timestamp,chargebackamount,captureamount,currency,STATUS,firstname,lastname,chargebackamount,walletAmount,walletCurrency FROM transaction_common WHERE toid=? AND accountid=? AND paymodeid=? AND cardtypeid=? AND timestamp >=? AND timestamp<=? AND status=?";
            }
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getPaymodeId());
            preparedStatement.setString(4, terminalVO.getCardTypeId());
            preparedStatement.setString(5, settleStartDate);
            preparedStatement.setString(6, settleEndDate);
            preparedStatement.setString(7, status.toString());
            logger.error("Query for Chargeback:::::"+preparedStatement);
            rsPayout = preparedStatement.executeQuery();
            while (rsPayout.next())
            {
                count = count + 1;
                detailsVO = new TransactionVO();
                detailsVO.setTrackingId(rsPayout.getString("trackingid"));
                detailsVO.setTransactionDate(rsPayout.getString("timestamp"));
                detailsVO.setCustFirstName(rsPayout.getString("firstname"));
                detailsVO.setCustLastName(rsPayout.getString("lastname"));
                detailsVO.setOrderId(rsPayout.getString("description"));
                detailsVO.setStatus(rsPayout.getString("STATUS"));
                detailsVO.setCurrency(rsPayout.getString("currency"));
                detailsVO.setChargebackAmount(rsPayout.getString("chargebackamount"));
                detailsVO.setWalletAmount(rsPayout.getString("walletAmount"));
                detailsVO.setWalletCurrency(rsPayout.getString("walletCurrency"));
                transactionVOs.add(detailsVO);
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionVOs;
    }

    public List<TransactionVO> geTransactionDetailsForChargebackReversed(String tableName, String settleStartDate, String settleEndDate, TerminalVO terminalVO, PZTransactionStatus status)
    {
        List<TransactionVO> transactionVOs = new LinkedList<TransactionVO>();
        Connection con = null;
        PreparedStatement preparedStatement = null;
        TransactionVO detailsVO = null;
        ResultSet rsPayout = null;
        try
        {
            String query = null;
            int count = 0;
            con = Database.getRDBConnection();
            query = "SELECT tc.trackingid,tc.description,FROM_UNIXTIME(dtstamp) as transactiondate,tcd.amount,tc.currency,tcd.STATUS,tc.firstname,tc.lastname,tcd.walletAmount,tcd.walletCurrency,tcd.timestamp FROM transaction_common AS tc, transaction_common_details AS tcd where toid=? and accountid=? and terminalid=? and tcd.status='chargebackreversed' and tcd.timestamp >=? and tcd.timestamp<=?  AND tc.trackingid=tcd.trackingid";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getTerminalId());
            preparedStatement.setString(4, settleStartDate);
            preparedStatement.setString(5, settleEndDate);
            logger.error("Query for ChargebackReversed:::::"+preparedStatement);
            rsPayout = preparedStatement.executeQuery();
            while (rsPayout.next())
            {
                count = count + 1;
                detailsVO = new TransactionVO();
                detailsVO.setTrackingId(rsPayout.getString("trackingid"));
                detailsVO.setTransactionDate(rsPayout.getString("timestamp"));
                detailsVO.setCustFirstName(rsPayout.getString("firstname"));
                detailsVO.setCustLastName(rsPayout.getString("lastname"));
                detailsVO.setOrderId(rsPayout.getString("description"));
                detailsVO.setStatus(rsPayout.getString("STATUS"));
                detailsVO.setCurrency(rsPayout.getString("currency"));
                detailsVO.setChargebackAmount(rsPayout.getString("amount"));
                detailsVO.setWalletAmount("");
                detailsVO.setWalletCurrency("");
                transactionVOs.add(detailsVO);
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionVOs;
    }

    public TransactionSummaryVO getReleaseCountAndAmount(RollingReserveDateVO rollingReserveDateVO, TerminalVO terminalVO, String tableName)
    {
        TransactionSummaryVO summaryVO = new TransactionSummaryVO();
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        StringBuilder qryBuilder = null;
        long totalReleaseCount = 0;
        double totalReleaseAmount = 0.00;
        Functions functions=new Functions();

        try
        {
            con = Database.getRDBConnection();
//            qryBuilder = new StringBuilder("SELECT COUNT(*) AS 'count',SUM(amount) AS 'amount' FROM " + tableName + "  WHERE toid=? AND accountid=? AND terminalid=? AND status IN ('settled','reversed','chargeback','chargebackreversed') ");
            qryBuilder = new StringBuilder("SELECT COUNT(*) AS 'count',SUM(amount) AS 'amount' FROM transaction_common AS t JOIN bin_details AS b  WHERE t.toid=? AND t.accountid=? AND t.terminalid=? AND STATUS IN ('settled','reversed','chargeback','chargebackreversed') AND b.icicitransid=t.trackingid AND b.isRollingReserveReleased='Y'");
            //String Query = "";
            if (functions.isValueNull(rollingReserveDateVO.getRollingReserveStartDate()))
            {
                qryBuilder.append(" AND FROM_UNIXTIME(dtstamp) >='" + rollingReserveDateVO.getRollingReserveStartDate() + "'");
            }
            if (functions.isValueNull(rollingReserveDateVO.getRollingReserveEndDate()))
            {
                qryBuilder.append(" AND FROM_UNIXTIME(dtstamp) <='" + rollingReserveDateVO.getRollingReserveEndDate() + "'");
            }
            qryBuilder.append(" GROUP BY toid");
            preparedStatement = con.prepareStatement(qryBuilder.toString());
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getTerminalId());
            // preparedStatement.setString(3, terminalVO.getPaymodeId());
            //preparedStatement.setString(4, terminalVO.getCardTypeId());
            logger.error("getReleaseCountAndAmount----"+preparedStatement);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                totalReleaseCount = rsPayout.getLong("count");
                totalReleaseAmount = rsPayout.getDouble("amount");
            }
            summaryVO.setCountOfreserveRefund(totalReleaseCount);
            summaryVO.setReserveRefundAmount(totalReleaseAmount);
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return summaryVO;
    }

    public long getVerifyOrderCount(TerminalVO terminalVO, SettlementDateVO settlementDateVO)
    {
        long verifyOrderCount = 0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT COUNT(*) AS 'VerifyOrderCount' FROM transaction_qwipi AS T JOIN bin_details ON T.trackingid = bin_details.icicitransid  WHERE T.toid=? AND T.accountid=? AND T.paymodeid=? AND T.cardtypeid=? AND bin_details.isVerifyOrder=?";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getPaymodeId());
            preparedStatement.setString(4, terminalVO.getCardTypeId());
            preparedStatement.setString(5, "Y");
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                verifyOrderCount = rsPayout.getLong("VerifyOrderCount");
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return verifyOrderCount;
    }

    public long getRefundAlertCount(TerminalVO terminalVO, SettlementDateVO settlementDateVO)
    {
        long refundAlertCount = 0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT COUNT(*) AS 'RefundAlertCount' FROM transaction_qwipi AS T JOIN bin_details ON T.trackingid = bin_details.icicitransid  WHERE T.toid=? AND T.accountid=? AND T.paymodeid=? AND T.cardtypeid=? AND bin_details.isRefundAlert=?";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getPaymodeId());
            preparedStatement.setString(4, terminalVO.getCardTypeId());
            preparedStatement.setString(5, "Y");
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                refundAlertCount = rsPayout.getLong("RefundAlertCount");
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return refundAlertCount;
    }

    public long getRetrivalRequestCount(TerminalVO terminalVO, String tableName)
    {
        long retrivalRequestCount = 0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT COUNT(*) AS 'RetrivalRequestCount' FROM " + tableName + " AS T JOIN bin_details ON T.trackingid = bin_details.icicitransid  WHERE T.toid=? AND T.accountid=? AND T.terminalid=? AND bin_details.isRetrivalRequest=?";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getTerminalId());
            //preparedStatement.setString(3, terminalVO.getPaymodeId());
            //preparedStatement.setString(4, terminalVO.getCardTypeId());
            preparedStatement.setString(4, "Y");
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                retrivalRequestCount = rsPayout.getLong("RetrivalRequestCount");
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return retrivalRequestCount;
    }

    public long getPaidVerifyOrderCount(TerminalVO terminalVO)
    {
        long paidVerifyOderCount = 0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT SUM(verifyorder_count) AS 'verifyOrderPaidCount' FROM member_settlementcycle_details WHERE memberid=?";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                paidVerifyOderCount = rsPayout.getLong("verifyOrderPaidCount");
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return paidVerifyOderCount;
    }

    public long getPaidVerifyOrderCountOnTerminal(TerminalVO terminalVO)
    {
        long paidVerifyOderCount = 0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT SUM(verifyorder_count) AS 'verifyOrderPaidCount' FROM member_settlementcycle_details WHERE memberid=? and terminalid=?";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getTerminalId());
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                paidVerifyOderCount = rsPayout.getLong("verifyOrderPaidCount");
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return paidVerifyOderCount;
    }

    public long getPaidFraudulentTransactionCountOnTerminal(TerminalVO terminalVO)
    {
        long paidFraudulentTransactionCount = 0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT SUM(fraudulent_transaction_count) AS 'FraudulentTransactionPaidCount' FROM member_settlementcycle_details WHERE memberid=? and terminalid=?";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getTerminalId());
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                paidFraudulentTransactionCount = rsPayout.getLong("FraudulentTransactionPaidCount");
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return paidFraudulentTransactionCount;
    }

    public long getVerifyOrderCountOnTerminal(TerminalVO terminalVO, SettlementDateVO settlementDateVO)
    {
        long verifyOrderCount = 0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT COUNT(*) AS 'VerifyOrderCount' FROM transaction_qwipi AS T JOIN bin_details ON T.trackingid = bin_details.icicitransid  WHERE T.toid=? AND T.accountid=? AND T.paymodeid=? AND T.cardtypeid=? AND bin_details.isVerifyOrder=?";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getPaymodeId());
            preparedStatement.setString(4, terminalVO.getCardTypeId());
            preparedStatement.setString(5, "Y");
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                verifyOrderCount = rsPayout.getLong("VerifyOrderCount");
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return verifyOrderCount;
    }

    public long getFraudulentTransactionCountOnTerminal(TerminalVO terminalVO)
    {
        long fraudulentTransactionCount = 0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
            GatewayType gatewayType=GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
            String tableName=Database.getTableName(gatewayType.getGateway());
            con = Database.getRDBConnection();
            String Query = "SELECT COUNT(*) AS 'FraudulentTransactionCount' FROM "+tableName+" AS T JOIN bin_details ON T.trackingid = bin_details.icicitransid  WHERE T.toid=? AND T.accountid=? AND T.terminalid=? AND bin_details.isFraud=?";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getTerminalId());
            //preparedStatement.setString(4, terminalVO.getCardTypeId());
            preparedStatement.setString(4, "Y");
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                fraudulentTransactionCount = rsPayout.getLong("FraudulentTransactionCount");
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return fraudulentTransactionCount;
    }
    public long getCaseFilingTransactionCount(TerminalVO terminalVO,SettlementDateVO settlementDateVO)
    {
        long CaseFilingTransactionCount = 0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuilder strQry = new StringBuilder(" SELECT COUNT(tcd.`trackingid`) as CaseFilingTransactionCount, SUM(tcd.`amount`) as amount FROM transaction_common_details AS tcd,transaction_common AS tc WHERE toid=? AND accountid=? AND terminalid=? AND tcd.status='casefiling' AND tc.trackingid=tcd.trackingid AND tcd.timestamp >=? AND tcd.timestamp<=?");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getTerminalId()));
            preparedStatement.setString(4, settlementDateVO.getChargebackStartDate());
            preparedStatement.setString(5, settlementDateVO.getChargebackEndDate());
            logger.error("Query for caseFillingCount:::" + preparedStatement);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                CaseFilingTransactionCount = rsPayout.getLong("CaseFilingTransactionCount");
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return CaseFilingTransactionCount;
    }

    public long getRefundAlertCountOnTerminal(TerminalVO terminalVO, SettlementDateVO settlementDateVO)
    {
        long refundAlertCount = 0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT COUNT(*) AS 'RefundAlertCount' FROM transaction_qwipi AS T JOIN bin_details ON T.trackingid = bin_details.icicitransid  WHERE T.toid=? AND T.accountid=? AND T.paymodeid=? AND T.cardtypeid=? AND bin_details.isRefundAlert=?";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getPaymodeId());
            preparedStatement.setString(4, terminalVO.getCardTypeId());
            preparedStatement.setString(5, "Y");
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                refundAlertCount = rsPayout.getLong("RefundAlertCount");
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return refundAlertCount;
    }

    public long getPaidRefundAlertCount(TerminalVO terminalVO)
    {
        long paidRefundAlertCount = 0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT SUM(refundalert_count) AS 'refundAlertPaidCount' FROM member_settlementcycle_details WHERE memberid=?";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                paidRefundAlertCount = rsPayout.getLong("refundAlertPaidCount");
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return paidRefundAlertCount;
    }

    public long getPaidRetrivalRequestCount(TerminalVO terminalVO)
    {
        long retrivalRequestPaidCount = 0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT SUM(retrivalrequest_count) AS 'retrivalRequestPaidCount' FROM member_settlementcycle_details WHERE memberid=? and terminalid=?";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getTerminalId());
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                retrivalRequestPaidCount = rsPayout.getLong("retrivalRequestPaidCount");
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return retrivalRequestPaidCount;
    }

    public long getPaidRefundAlertCountOnTerminal(TerminalVO terminalVO)
    {
        long paidRefundAlertCount = 0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT SUM(refundalert_count) AS 'refundAlertPaidCount' FROM member_settlementcycle_details WHERE memberid=? and terminalid=? ";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getTerminalId());
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                paidRefundAlertCount = rsPayout.getLong("refundAlertPaidCount");
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return paidRefundAlertCount;
    }


    public static int getreportid()
    {
        int id = 0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT MAX(reportid) FROM merchant_wiremanager";
            preparedStatement = con.prepareStatement(Query);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                id = rsPayout.getInt(1);

            }
            if (id == 0){
                id=1;
            }else{
                id=id+1;
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return id;
    }

    public String generateSettlementCycleWire(WireVO wireVO)
    {
        TerminalVO terminalVO = wireVO.getTerminalVO();
        String status = null;
        Connection conn = null;
        PreparedStatement pstmt1=null;
        SimpleDateFormat targetFormat = null;
        try
        {
            targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            conn = Database.getConnection();
            String query1 = "INSERT INTO merchant_wiremanager (firstdate,lastdate,amount,balanceamount,netfinalamount,currency,status,settlementreportfilepath,settledtransactionfilepath,toid,terminalid,accountid,paymodeid,cardtypeid,isrollingreserveincluded,rollingreservereleasedateupto,settlementcycle_no,declinedcoverdateupto,reversedcoverdateupto,chargebackcoverdateupto,rollingReserveFilePath,wirecreationtime,parentsettlementcycle_no,reportid) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?)";
            pstmt1 = conn.prepareStatement(query1);
            pstmt1.setString(1, wireVO.getFirstDate());
            pstmt1.setString(2, wireVO.getLastDate());
            pstmt1.setString(3, String.format("%.2f", wireVO.getWireAmount()));
            pstmt1.setString(4, String.format("%.2f", wireVO.getWireBalanceAmount()));
            pstmt1.setString(5, String.format("%.2f", wireVO.getNetFinalAmount()));
            pstmt1.setString(6, wireVO.getCurrency());
            pstmt1.setString(7, wireVO.getStatus());
            pstmt1.setString(8, wireVO.getSettlementReportFilePath());
            pstmt1.setString(9, wireVO.getSettledTransactionFilePath());
            pstmt1.setString(10, terminalVO.getMemberId());
            pstmt1.setString(11, terminalVO.getTerminalId());
            pstmt1.setString(12, terminalVO.getAccountId());
            pstmt1.setString(13, terminalVO.getPaymodeId());
            pstmt1.setString(14, terminalVO.getCardTypeId());
            pstmt1.setString(15, wireVO.getRollingReserveIncluded());
            pstmt1.setString(16, wireVO.getReserveReleasedUptoDate());
            pstmt1.setString(17, wireVO.getSettlementCycleNo());
            pstmt1.setString(18, wireVO.getDeclinedcoverdateupto());
            pstmt1.setString(19, wireVO.getReversedcoverdateupto());
            pstmt1.setString(20, wireVO.getChargebackcoverdateupto());
            pstmt1.setString(21, wireVO.getRollingReserveFilePath());
            pstmt1.setString(22, wireVO.getParent_settlementCycleNo());
            pstmt1.setInt(23, wireVO.getReportid());
            logger.error("insert Query::::"+pstmt1);

            int num = pstmt1.executeUpdate();
            if (num == 1)
            {
                logger.debug("New Wire added successfully.");
                status = "success";
            }
            else
            {
                logger.debug("Adding Wire Failure Check The JDBC Code For Generate Wire");
                status = "failure";
            }
        }
        catch (SystemError error)
        {
            status = "failure";
            logger.error(error);
        }
        catch (SQLException e)
        {
            status = "failure";
            logger.error(e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(conn);
        }
        return status;
    }

    public String generateSettlementCycleWireNew(WireVO wireVO)
    {
        TerminalVO terminalVO = wireVO.getTerminalVO();
        String status = null;
        Connection conn = null;
        PreparedStatement pstmt1=null;
        SimpleDateFormat targetFormat = null;
        try
        {
            targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            conn = Database.getConnection();
            String query1 = "INSERT INTO merchant_wiremanager (firstdate,lastdate,amount,balanceamount,netfinalamount,currency,status,settlementreportfilepath,settledtransactionfilepath,toid,terminalid,accountid,paymodeid,cardtypeid,isrollingreserveincluded,rollingreservereleasedateupto,settlementcycle_no,declinedcoverdateupto,reversedcoverdateupto,chargebackcoverdateupto,wirecreationtime,reportid) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?)";
            pstmt1 = conn.prepareStatement(query1);
            pstmt1.setString(1, wireVO.getFirstDate());
            pstmt1.setString(2, wireVO.getLastDate());
            pstmt1.setDouble(3, wireVO.getWireAmount());
            pstmt1.setDouble(4, wireVO.getWireBalanceAmount());
            pstmt1.setDouble(5, wireVO.getNetFinalAmount());
            pstmt1.setString(6, wireVO.getCurrency());
            pstmt1.setString(7, wireVO.getStatus());
            pstmt1.setString(8, wireVO.getSettlementReportFilePath());
            pstmt1.setString(9, wireVO.getSettledTransactionFilePath());
            pstmt1.setString(10, wireVO.getMemberId());
            pstmt1.setString(11, wireVO.getTerminalId());
            pstmt1.setString(12, terminalVO.getAccountId());
            pstmt1.setString(13, terminalVO.getPaymodeId());
            pstmt1.setString(14, terminalVO.getCardTypeId());
            pstmt1.setString(15, wireVO.getRollingReserveIncluded());
            pstmt1.setString(16, wireVO.getReserveReleasedUptoDate());
            pstmt1.setString(17, wireVO.getSettlementCycleNo());
            pstmt1.setString(18, wireVO.getDeclinedcoverdateupto());
            pstmt1.setString(19, wireVO.getReversedcoverdateupto());
            pstmt1.setString(20, wireVO.getChargebackcoverdateupto());
            pstmt1.setInt(21, getreportid());

            int num = pstmt1.executeUpdate();
            if (num == 1)
            {
                logger.debug("New Wire added successfully.");
                status = "success";
            }
            else
            {
                logger.debug("Adding Wire Failure Check The JDBC Code For Generate Wire");
                status = "failure";
            }
        }
        catch (SystemError error)
        {
            status = "failure";
            logger.error(error);
        }
        catch (SQLException e)
        {
            status = "failure";
            logger.error(e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(conn);
        }
        return status;
    }

    public String generateSettlementCycleWireForAllTerminal(WireVO wireVO,List<WireVO> wireVOList)
    {
        TerminalVO terminalVO = wireVO.getTerminalVO();
        String status = null;
        Connection conn = null;
        PreparedStatement pstmt1=null;
        int num=0;
        try
        {
            conn = Database.getConnection();
            String query1 = "INSERT INTO merchant_wiremanager (firstdate,lastdate,amount,balanceamount,netfinalamount,currency,status,settlementreportfilepath,settledtransactionfilepath,toid,terminalid,paymodeid,cardtypeid,accountid,isrollingreserveincluded,rollingreservereleasedateupto,settlementcycle_no,declinedcoverdateupto,reversedcoverdateupto,chargebackcoverdateupto,wirecreationtime,reportid) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?)";
            for(WireVO wireVO1:wireVOList)
            {
                pstmt1 = conn.prepareStatement(query1);
                pstmt1.setString(1, wireVO.getFirstDate());
                pstmt1.setString(2, wireVO.getLastDate());
                pstmt1.setDouble(3, wireVO.getWireAmount());
                pstmt1.setDouble(4, wireVO.getWireBalanceAmount());
                pstmt1.setDouble(5, wireVO.getNetFinalAmount());
                pstmt1.setString(6, wireVO1.getCurrency());
                pstmt1.setString(7, wireVO.getStatus());
                pstmt1.setString(8, wireVO.getSettlementReportFilePath());
                pstmt1.setString(9, wireVO.getSettledTransactionFilePath());
                pstmt1.setString(10, wireVO.getMemberId());
                pstmt1.setString(11, wireVO1.getTerminalId());
                pstmt1.setString(12, wireVO1.getPaymodeId());
                pstmt1.setString(13, wireVO1.getCardTypeId());
                pstmt1.setString(14, terminalVO.getAccountId());
                pstmt1.setString(15, wireVO.getRollingReserveIncluded());
                pstmt1.setString(16, wireVO.getReserveReleasedUptoDate());
                pstmt1.setString(17, wireVO.getSettlementCycleNo());
                pstmt1.setString(18, wireVO.getDeclinedcoverdateupto());
                pstmt1.setString(19, wireVO.getReversedcoverdateupto());
                pstmt1.setString(20, wireVO.getChargebackcoverdateupto());
                pstmt1.setInt(21, wireVO.getReportid());

                num = pstmt1.executeUpdate();
                num++;
            }
            if (num>0)
            {
                logger.debug("New Wire added successfully.");
                status = "success";
            }
            else
            {
                logger.debug("Adding Wire Failure Check The JDBC Code For Generate Wire");
                status = "failure";
            }
        }
        catch (SystemError error)
        {
            status = "failure";
            logger.error(error);
        }
        catch (SQLException e)
        {
            status = "failure";
            logger.error(e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(conn);
        }
        return status;
    }

    public String generateSettlementCycleWireForAllTerminalPartner(SettlementCycleVO settlementCycleVO,List<SettlementCycleVO> settlementCycleVOList)
    {
        String status = null;
        Connection conn = null;
        PreparedStatement pstmt1=null;
        int num=0;
        try
        {
            for(SettlementCycleVO settlementCycleVO1:settlementCycleVOList)
            {
                conn = Database.getConnection();
                String query1 = "INSERT INTO merchant_wiremanager (firstdate,lastdate,amount,balanceamount,netfinalamount,unpaidamount,status,settledtransactionfilepath,settlementreportfilepath,toid,terminalid,paymodeid,cardtypeid,accountid,rollingreservereleasedateupto,isrollingreserveincluded,markedfordeletion,currency,settledate,wirecreationtime,reportid) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?)";
                pstmt1 = conn.prepareStatement(query1);
                pstmt1.setString(1, settlementCycleVO.getStartDate());
                pstmt1.setString(2, settlementCycleVO.getEndDate());
                pstmt1.setDouble(3, Double.parseDouble(settlementCycleVO.getAmount()));
                pstmt1.setDouble(4, Double.parseDouble(settlementCycleVO.getBalanceAmount()));
                pstmt1.setDouble(5, Double.parseDouble(settlementCycleVO.getFinalAmount()));
                pstmt1.setDouble(6, Double.parseDouble(settlementCycleVO.getUnpaidAmount()));
                pstmt1.setString(7, settlementCycleVO.getStatus());
                pstmt1.setString(8, settlementCycleVO.getSettledTransactionFilePath());
                pstmt1.setString(9, settlementCycleVO.getSettlementReportFilePath());
                pstmt1.setString(10, settlementCycleVO.getMemberId());
                pstmt1.setString(11, settlementCycleVO1.getTerminalId());
                pstmt1.setString(12, settlementCycleVO1.getPaymodeId());
                pstmt1.setString(13, settlementCycleVO1.getCardTypeId());
                pstmt1.setString(14, settlementCycleVO.getAccountId());
                pstmt1.setString(15, settlementCycleVO.getRollingReserveDate());
                pstmt1.setString(16, settlementCycleVO.getRollingReserveIncluded());
                pstmt1.setString(17, settlementCycleVO.getMarkForDeletion());
                pstmt1.setString(18, settlementCycleVO1.getCurrency());
                pstmt1.setString(19, settlementCycleVO.getMoneyRecivedDate());
                pstmt1.setInt(20, getreportid());

                int k = pstmt1.executeUpdate();
                num++;
            }
            if (num>0)
            {
                logger.debug("New Wire added successfully.");
                status = "success";
            }
            else
            {
                logger.debug("Adding Wire Failure Check The JDBC Code For Generate Wire");
                status = "failure";
            }
        }
        catch (SystemError error)
        {
            status = "failure";
            logger.error(error);
        }
        catch (SQLException e)
        {
            status = "failure";
            logger.error(e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(conn);
        }
        return status;
    }
    public boolean updateMemberCycleDetails(String memberId, String terminalId, long verifyOrderCount, long refundAlertCount, long retrivalRequestCount, String latestSetupFeeDate)
    {
        boolean status = false;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getConnection();
            String Query = "INSERT INTO member_settlementcycle_details(memberid,terminalid,verifyorder_count,refundalert_count,retrivalrequest_count,lastsetupfeedate) VALUES(?,?,?,?,?,?)";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, memberId);
            preparedStatement.setString(2, terminalId);
            preparedStatement.setLong(3, verifyOrderCount);
            preparedStatement.setLong(4, refundAlertCount);
            preparedStatement.setLong(5, retrivalRequestCount);
            preparedStatement.setString(6, latestSetupFeeDate);
            int k = preparedStatement.executeUpdate();
            if (k == 1)
            {
                status = true;
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return status;
    }

    public boolean updateMemberCycleDetailsWithAccountId(String memberId, String accountId, String terminalId, long verifyOrderCount,long refundAlertCount, long retrivalRequestCount, String latestSetupFeeDate, String cycleId,long fraudulentTransaction, String parentcycleid)
    {
        boolean status = false;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getConnection();
            String Query = "INSERT INTO member_settlementcycle_details(memberid,accountid,terminalid,verifyorder_count,refundalert_count,retrivalrequest_count,lastsetupfeedate,cycleid,fraudulent_transaction_count,parentcycleid) VALUES(?,?,?,?,?,?,?,?,?,?)";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, memberId);
            preparedStatement.setString(2, accountId);
            preparedStatement.setString(3, terminalId);
            preparedStatement.setLong(4, verifyOrderCount);
            preparedStatement.setLong(5, refundAlertCount);
            preparedStatement.setLong(6, retrivalRequestCount);
            preparedStatement.setString(7, latestSetupFeeDate);
            preparedStatement.setString(8, cycleId);
            preparedStatement.setLong(9, fraudulentTransaction);
            preparedStatement.setString(10, parentcycleid);
            int k = preparedStatement.executeUpdate();
            if (k == 1)
            {
                status = true;
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return status;
    }

    public String getLastSetupFeeDate(String memberId, String accountId)
    {
        String lastSetupFeeDate = null;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT lastsetupfeedate FROM member_settlementcycle_details WHERE memberid=? AND terminalid=? ORDER BY id DESC LIMIT 1";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, memberId);
            preparedStatement.setString(2, accountId);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                lastSetupFeeDate = rsPayout.getString("lastsetupfeedate");
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving ", se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError ::::::: Leaving  ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return lastSetupFeeDate;
    }

    public String getLastSetupFeeDateByMemberId(String memberId, String accountId,String terminalId)
    {
        String lastSetupFeeDate = null;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT lastsetupfeedate FROM member_settlementcycle_details WHERE memberid=? and accountid=? and terminalid=? ORDER BY id DESC LIMIT 1";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, memberId);
            preparedStatement.setString(2, accountId);
            preparedStatement.setString(3, terminalId);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                lastSetupFeeDate = rsPayout.getString("lastsetupfeedate");
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving ", se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError ::::::: Leaving  ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return lastSetupFeeDate;
    }

    public String getMerchantWireCoveredDate(String memberId, String accountId, String terminalId, String cycleId)
    {
        String lastSetupFeeDate = null;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT lastsetupfeedate FROM member_settlementcycle_details WHERE memberid=? and accountid=? and terminalid=? and cycleid=? ORDER BY id DESC LIMIT 1";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, memberId);
            preparedStatement.setString(2, accountId);
            preparedStatement.setString(3, terminalId);
            preparedStatement.setString(4, cycleId);
            logger.error("lastsetupfeedate----"+preparedStatement);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                lastSetupFeeDate = rsPayout.getString("lastsetupfeedate");
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving ", se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError ::::::: Leaving  ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return lastSetupFeeDate;
    }

    public String getMerchantWireLastCoveredDate(String memberId, String accountId, String terminalId, String cycleId)
    {
        String lastSetupFeeDate = null;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT lastsetupfeedate FROM member_settlementcycle_details WHERE memberid=? AND accountid=? AND terminalid=? AND cycleid=(SELECT MAX(cycleid) FROM member_settlementcycle_details WHERE accountid=? and cycleid<?)";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1,memberId);
            preparedStatement.setString(2,accountId);
            preparedStatement.setString(3,terminalId);
            preparedStatement.setString(4,accountId);
            preparedStatement.setString(5,cycleId);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                lastSetupFeeDate = rsPayout.getString("lastsetupfeedate");
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving ", se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError ::::::: Leaving  ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return lastSetupFeeDate;
    }

    public TransactionSummaryVO getAuthFailCountAmountByDtstamp(String authfailedTransStartDate, TerminalVO terminalVO, String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuilder strQry = new StringBuilder("select status,count(*) as 'count',sum(amount) as 'amount' from " + sTableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and status=? ");
            if (authfailedTransStartDate != null)
            {
                strQry.append(" and FROM_UNIXTIME(dtstamp) >='" + authfailedTransStartDate + "' ");
            }
            strQry.append(" group by status");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getPaymodeId()));
            preparedStatement.setInt(4, Integer.parseInt(terminalVO.getCardTypeId()));
            preparedStatement.setString(5, "authfailed");
            rsPayReport = preparedStatement.executeQuery();
            if (rsPayReport.next())
            {
                transactionSummaryVO.setAuthfailedAmount(rsPayReport.getDouble("amount"));
                transactionSummaryVO.setCountOfAuthfailed(rsPayReport.getLong("count"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionSummaryVO;
    }

    public TransactionSummaryVO getSettledCountAmountByDtstamp(String settledTransStartDate, TerminalVO terminalVO, String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuilder strQry = new StringBuilder("select status,count(*) as 'count',sum(captureamount) as 'captureamount' from " + sTableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and status=? ");
            if (settledTransStartDate != null)
            {
                strQry.append(" and FROM_UNIXTIME(dtstamp) >='" + settledTransStartDate + "' ");
            }
            strQry.append(" group by status");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getPaymodeId()));
            preparedStatement.setInt(4, Integer.parseInt(terminalVO.getCardTypeId()));
            preparedStatement.setString(5, "settled");
            rsPayReport = preparedStatement.executeQuery();
            if (rsPayReport.next())
            {
                transactionSummaryVO.setSettledAmount(rsPayReport.getDouble("captureamount"));
                transactionSummaryVO.setCountOfSettled(rsPayReport.getLong("count"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionSummaryVO;
    }

    public TransactionSummaryVO getReversalCountAmountByTimestamp(String reversedTransStartDate, TerminalVO terminalVO, String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuilder strQry = new StringBuilder("select status,count(*) as 'count',sum(refundamount) as 'refundamount' from " + sTableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and status=? ");
            if (reversedTransStartDate != null)
            {
                strQry.append(" and TIMESTAMP >='" + reversedTransStartDate + "' ");
            }
            strQry.append(" group by status");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getPaymodeId()));
            preparedStatement.setInt(4, Integer.parseInt(terminalVO.getCardTypeId()));
            preparedStatement.setString(5, "reversed");
            rsPayReport = preparedStatement.executeQuery();
            if (rsPayReport.next())
            {
                transactionSummaryVO.setCountOfReversed(rsPayReport.getLong("count"));
                transactionSummaryVO.setReversedAmount(rsPayReport.getDouble("refundamount"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionSummaryVO;
    }

    public TransactionSummaryVO getChargebackCountAmountByTimestamp(String chargebackTransStartDate, TerminalVO terminalVO, String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuilder strQry = new StringBuilder("select status,count(*) as 'count',sum(chargebackamount) as 'chargebackamount' from " + sTableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and status=? ");
            if (chargebackTransStartDate != null)
            {
                strQry.append(" and TIMESTAMP >='" + chargebackTransStartDate + "' ");
            }
            strQry.append(" group by status");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getPaymodeId()));
            preparedStatement.setInt(4, Integer.parseInt(terminalVO.getCardTypeId()));
            preparedStatement.setString(5, "chargeback");
            rsPayReport = preparedStatement.executeQuery();
            if (rsPayReport.next())
            {
                transactionSummaryVO.setCountOfChargeback(rsPayReport.getLong("count"));
                transactionSummaryVO.setChargebackAmount(rsPayReport.getDouble("chargebackamount"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionSummaryVO;
    }

    public long getWireCount(TerminalVO terminalVO)
    {
        long wireCount = 0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT COUNT(*) AS 'WireCount' FROM wiremanager WHERE toid=? AND accountid=? AND paymodeid=? AND cardtypeid=? AND STATUS=?";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getPaymodeId());
            preparedStatement.setString(4, terminalVO.getCardTypeId());
            preparedStatement.setString(5, "paid");
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                wireCount = rsPayout.getLong("WireCount");
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return wireCount;
    }

    //Getting sum of unpaidamount column
    public void setUnpaidBalanceAmount(TerminalVO terminalVO, WireAmountVO wireAmountVO)
    {
        String strQuery = "SELECT SUM(unpaidamount) AS 'UnpaidBalanceAmount' FROM wiremanager WHERE toid=? AND accountid=? AND paymodeid=? AND cardtypeid=?";
        Connection con = null;
        PreparedStatement psWireBalaceAmount=null;
        ResultSet rsWireBalnceAmount=null;
        try
        {
            con = Database.getRDBConnection();
            psWireBalaceAmount = con.prepareStatement(strQuery);
            psWireBalaceAmount.setString(1, terminalVO.getMemberId());
            psWireBalaceAmount.setString(2, terminalVO.getAccountId());
            psWireBalaceAmount.setString(3, terminalVO.getPaymodeId());
            psWireBalaceAmount.setString(4, terminalVO.getCardTypeId());
            rsWireBalnceAmount = psWireBalaceAmount.executeQuery();
            if (rsWireBalnceAmount.next())
            {
                wireAmountVO.setUnpaidBalanceAmount(rsWireBalnceAmount.getDouble("UnpaidBalanceAmount"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ::", e);
        }
        finally
        {
            Database.closeResultSet(rsWireBalnceAmount);
            Database.closePreparedStatement(psWireBalaceAmount);
            Database.closeConnection(con);
        }
    }

    public void setWireAmount(TerminalVO terminalVO, WireAmountVO wireAmountVO)
    {
        Connection con = null;
        PreparedStatement psWireBalanceAmount=null;
        ResultSet rsWireAmount = null;
        long counter = 1;
        long wireCount = 1;
        String strQuery = "SELECT COUNT(*) AS 'WireCount', SUM(netfinalamount) AS 'WireAmount',status FROM wiremanager WHERE toid=? AND accountid=? AND paymodeid=? AND cardtypeid=? Group by status";

        try
        {
            con = Database.getRDBConnection();
            psWireBalanceAmount = con.prepareStatement(strQuery.toString());
            psWireBalanceAmount.setString(1, terminalVO.getMemberId());
            psWireBalanceAmount.setString(2, terminalVO.getAccountId());
            psWireBalanceAmount.setString(3, terminalVO.getPaymodeId());
            psWireBalanceAmount.setString(4, terminalVO.getCardTypeId());

            rsWireAmount = psWireBalanceAmount.executeQuery();

            while (rsWireAmount.next())
            {

                if ("paid".equals(rsWireAmount.getString("status")))
                {
                    wireAmountVO.setPaidAmount(rsWireAmount.getDouble("WireAmount"));

                }
                else if ("unpaid".equals(rsWireAmount.getString("status")))
                {
                    wireAmountVO.setUnpaidAmount(rsWireAmount.getDouble("WireAmount"));
                }
            }
            wireAmountVO.setWireCount(counter);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rsWireAmount);
            Database.closePreparedStatement(psWireBalanceAmount);
            Database.closeConnection(con);
        }
    }

    public List<WireVO> getMemberTerminalId(String memberId,String accountId) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps=null;
        WireVO wireVO=null;
        StringBuffer stringBuffer = new StringBuffer();
        List<WireVO> wireVOList=new ArrayList<>();
        try
        {
            conn=Database.getConnection();
            //stringBuffer.append("SELECT mam.terminalid,mam.paymodeid,mam.cardtypeid FROM member_account_mapping AS mam WHERE isActive='Y'AND memberid=? AND accountid=?");
            stringBuffer.append("SELECT mam.terminalid,mam.paymodeid,mam.cardtypeid,gt.currency FROM member_account_mapping AS mam,gateway_type AS gt,gateway_accounts AS gs WHERE mam.isActive='Y' AND mam.memberid=? AND gs.accountid=? AND gs.pgtypeid=gt.pgtypeid");
            ps = conn.prepareStatement(stringBuffer.toString());
            ps.setString(1,memberId);
            ps.setString(2,accountId);
            rs = ps.executeQuery();
            while (rs.next())
            {
                wireVO=new WireVO();
                wireVO.setTerminalId(rs.getString("terminalid"));
                wireVO.setPaymodeId(rs.getString("paymodeid"));
                wireVO.setCardTypeId(rs.getString("cardtypeid"));
                wireVO.setCurrency(rs.getString("currency"));
                wireVOList.add(wireVO);
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
        return wireVOList;
    }

    public List<SettlementCycleVO> getMemberTerminalIdPartner(String memberId,String accountId) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps=null;
        SettlementCycleVO settlementCycleVO=null;
        StringBuffer stringBuffer = new StringBuffer();
        List<SettlementCycleVO> settlementCycleVOList=new ArrayList<>();
        try
        {
            conn=Database.getConnection();
            //stringBuffer.append("SELECT mam.terminalid,mam.paymodeid,mam.cardtypeid FROM member_account_mapping AS mam WHERE isActive='Y' AND memberid=? AND accountid=?");
            stringBuffer.append("SELECT mam.terminalid,mam.paymodeid,mam.cardtypeid,gt.currency FROM member_account_mapping AS mam,gateway_type AS gt,gateway_accounts AS gs WHERE mam.isActive='Y' AND mam.memberid=? AND gs.accountid=? AND gs.pgtypeid=gt.pgtypeid");
            ps = conn.prepareStatement(stringBuffer.toString());
            ps.setString(1,memberId);
            ps.setString(2,accountId);
            rs = ps.executeQuery();
            while (rs.next())
            {
                settlementCycleVO=new SettlementCycleVO();
                settlementCycleVO.setTerminalId(rs.getString("terminalid"));
                settlementCycleVO.setPaymodeId(rs.getString("paymodeid"));
                settlementCycleVO.setCardTypeId(rs.getString("cardtypeid"));
                settlementCycleVO.setCurrency(rs.getString("currency"));
                settlementCycleVOList.add(settlementCycleVO);
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
        return settlementCycleVOList;
    }

    public String getMemberTerminalId(String memberId, String accountId, String payModeId, String cardTypeId)
    {
        String terminalId = null;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT terminalid FROM member_account_mapping WHERE memberId=? AND accountid=? And paymodeid=? And cardtypeid=?";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, memberId);
            preparedStatement.setString(2, accountId);
            preparedStatement.setString(3, payModeId);
            preparedStatement.setString(4, cardTypeId);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                terminalId = rsPayout.getString("terminalid");
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return terminalId;

    }

    public SettlementDateVO getWireReportSettlementDateVO(TerminalVO terminalVO, String sTableName) throws Exception
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SettlementDateVO settlementDateVO = new SettlementDateVO();
        PreparedStatement preparedStatementPayReport = null;
        ResultSet rsPayReport = null;
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            String qry = "SELECT lastdate,declinedcoverdateupto,reversedcoverdateupto,chargebackcoverdateupto FROM wiremanager WHERE toid=? AND accountid=? AND paymodeid=? AND cardtypeid=? ORDER BY settledid DESC LIMIT 1";
            preparedStatementPayReport = conn.prepareStatement(qry);
            preparedStatementPayReport.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatementPayReport.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatementPayReport.setInt(3, Integer.parseInt(terminalVO.getPaymodeId()));
            preparedStatementPayReport.setInt(4, Integer.parseInt(terminalVO.getCardTypeId()));
            rsPayReport = preparedStatementPayReport.executeQuery();
            if (rsPayReport.next())
            {
                Calendar cal = Calendar.getInstance();

                String settledTransStartDate = rsPayReport.getString("lastdate");
                String authfailedTransStartDate = rsPayReport.getString("declinedcoverdateupto");
                String reversedTransStartDate = rsPayReport.getString("reversedcoverdateupto");
                String chargebackTransStartDate = rsPayReport.getString("chargebackcoverdateupto");

                cal.setTime(targetFormat.parse(settledTransStartDate));
                cal.add(Calendar.SECOND, 1);
                settlementDateVO.setSettlementStartDate(targetFormat.format(cal.getTime()));

                cal.setTime(targetFormat.parse(authfailedTransStartDate));
                cal.add(Calendar.SECOND, 1);
                settlementDateVO.setDeclinedStartDate(targetFormat.format(cal.getTime()));

                cal.setTime(targetFormat.parse(reversedTransStartDate));
                cal.add(Calendar.SECOND, 1);
                settlementDateVO.setReversedStartDate(targetFormat.format(cal.getTime()));

                cal.setTime(targetFormat.parse(chargebackTransStartDate));
                cal.add(Calendar.SECOND, 1);
                settlementDateVO.setChargebackStartDate(targetFormat.format(cal.getTime()));
            }
            else
            {
                AccountDAO accountDAO = new AccountDAO();
                String firstTransDate = accountDAO.getMemberFirstTransactionDate(terminalVO.getMemberId(), terminalVO.getAccountId());

                settlementDateVO.setSettlementStartDate(firstTransDate);
                settlementDateVO.setDeclinedStartDate(firstTransDate);
                settlementDateVO.setReversedStartDate(firstTransDate);
                settlementDateVO.setChargebackStartDate(firstTransDate);
            }
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatementPayReport);
            Database.closeConnection(conn);
        }
        return settlementDateVO;
    }

    public SettlementDateVO getSettlementDateOnTerminal(TerminalVO terminalVO, String tableName)
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SettlementDateVO settlementDateVO = new SettlementDateVO();
        Functions functions = new Functions();
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        Connection conn = null;
        String currentDate = null;
        Date currDate = new Date();
        currentDate = targetFormat.format(currDate);
        String query = null;
        try
        {
            conn = Database.getRDBConnection();
            query = "select max(from_unixtime(dtstamp)) as 'endDate' from " + tableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and status=?";
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getPaymodeId()));
            preparedStatement.setInt(4, Integer.parseInt(terminalVO.getCardTypeId()));
            preparedStatement.setString(5, "settled");
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                if (functions.isValueNull(rs.getString("endDate")))
                {
                    settlementDateVO.setSettlementEndDate(rs.getString("endDate"));
                }
                else
                {
                    settlementDateVO.setSettlementEndDate(currentDate);
                }
            }
            else
            {
                settlementDateVO.setSettlementEndDate(currentDate);
            }

            preparedStatement.setString(5, "authfailed");
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                if (functions.isValueNull(rs.getString("endDate")))
                {
                    settlementDateVO.setDeclinedEndDate(rs.getString("endDate"));
                }
                else
                {
                    settlementDateVO.setDeclinedEndDate(currentDate);
                }
            }
            else
            {
                settlementDateVO.setDeclinedEndDate(currentDate);
            }
            query = "select max(timestamp) as 'endDate' from " + tableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and status=?";
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getPaymodeId()));
            preparedStatement.setInt(4, Integer.parseInt(terminalVO.getCardTypeId()));
            preparedStatement.setString(5, "reversed");
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                if (functions.isValueNull(rs.getString("endDate")))
                {
                    settlementDateVO.setReversedEndDate(rs.getString("endDate"));
                }
                else
                {
                    settlementDateVO.setReversedEndDate(currentDate);
                }
            }
            else
            {
                settlementDateVO.setReversedEndDate(currentDate);
            }
            preparedStatement.setString(5, "chargeback");
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                if (functions.isValueNull(rs.getString("endDate")))
                {
                    settlementDateVO.setChargebackEndDate(rs.getString("endDate"));
                }
                else
                {
                    settlementDateVO.setChargebackEndDate(currentDate);
                }
            }
            else
            {
                settlementDateVO.setChargebackEndDate(currentDate);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::" + systemError);
        }
        catch (SQLException se)
        {
            logger.error("SQLException::::" + se);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return settlementDateVO;
    }

    public SettlementDateVO getSettlementDateOnAccount(String memberId, String accountId, String tableName)
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SettlementDateVO settlementDateVO = new SettlementDateVO();
        Functions functions = new Functions();
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        Connection conn = null;
        String currentDate = null;
        Date currDate = new Date();
        currentDate = targetFormat.format(currDate);
        String query = null;
        try
        {
            conn = Database.getRDBConnection();
            query = "select max(from_unixtime(dtstamp)) as 'endDate' from " + tableName + " where toid=? and accountid=? and status=?";
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(memberId));
            preparedStatement.setInt(2, Integer.parseInt(accountId));
            preparedStatement.setString(3, "settled");
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                if (functions.isValueNull(rs.getString("endDate")))
                {
                    settlementDateVO.setSettlementEndDate(rs.getString("endDate"));
                }
                else
                {
                    settlementDateVO.setSettlementEndDate(currentDate);
                }
            }
            else
            {
                settlementDateVO.setSettlementEndDate(currentDate);
            }

            preparedStatement.setString(3, "authfailed");
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                if (functions.isValueNull(rs.getString("endDate")))
                {
                    settlementDateVO.setDeclinedEndDate(rs.getString("endDate"));
                }
                else
                {
                    settlementDateVO.setDeclinedEndDate(currentDate);
                }
            }
            else
            {
                settlementDateVO.setDeclinedEndDate(currentDate);
            }
            query = "select max(timestamp) as 'endDate' from " + tableName + " where toid=? and accountid=? and status=?";
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(memberId));
            preparedStatement.setInt(2, Integer.parseInt(accountId));
            preparedStatement.setString(3, "reversed");
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                if (functions.isValueNull(rs.getString("endDate")))
                {
                    settlementDateVO.setReversedEndDate(rs.getString("endDate"));
                }
                else
                {
                    settlementDateVO.setReversedEndDate(currentDate);
                }
            }
            else
            {
                settlementDateVO.setReversedEndDate(currentDate);
            }
            preparedStatement.setString(3, "chargeback");
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                if (functions.isValueNull(rs.getString("endDate")))
                {
                    settlementDateVO.setChargebackEndDate(rs.getString("endDate"));
                }
                else
                {
                    settlementDateVO.setChargebackEndDate(currentDate);
                }
            }
            else
            {
                settlementDateVO.setChargebackEndDate(currentDate);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::" + systemError);
        }
        catch (SQLException se)
        {
            logger.error("SQLException::::" + se);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return settlementDateVO;
    }

    public SettlementDateVO getSettlementDateOnGatewayAccount(GatewayAccount gatewayAccount, String tableName)
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SettlementDateVO settlementDateVO = new SettlementDateVO();
        Functions functions = new Functions();
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        Connection conn = null;
        String currentDate = null;
        Date currDate = new Date();
        currentDate = targetFormat.format(currDate);
        String query = null;
        try
        {
            conn = Database.getRDBConnection();
            query = "select max(from_unixtime(dtstamp)) as 'endDate' from " + tableName + " where fromid=? and accountid=? and status=?";
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, gatewayAccount.getMerchantId());
            preparedStatement.setString(2, String.valueOf(gatewayAccount.getAccountId()));
            preparedStatement.setString(3, "settled");
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                if (functions.isValueNull(rs.getString("endDate")))
                {
                    settlementDateVO.setSettlementEndDate(rs.getString("endDate"));
                }
                else
                {
                    settlementDateVO.setSettlementEndDate(currentDate);
                }
            }
            else
            {
                settlementDateVO.setSettlementEndDate(currentDate);
            }

            preparedStatement.setString(3, "authfailed");
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                if (functions.isValueNull(rs.getString("endDate")))
                {
                    settlementDateVO.setDeclinedEndDate(rs.getString("endDate"));
                }
                else
                {
                    settlementDateVO.setDeclinedEndDate(currentDate);
                }
            }
            else
            {
                settlementDateVO.setDeclinedEndDate(currentDate);
            }
            query = "select max(timestamp) as 'endDate' from " + tableName + " where fromid=? and accountid=? and status=?";
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, gatewayAccount.getMerchantId());
            preparedStatement.setString(2, String.valueOf(gatewayAccount.getAccountId()));
            preparedStatement.setString(3, "reversed");
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                if (functions.isValueNull(rs.getString("endDate")))
                {
                    settlementDateVO.setReversedEndDate(rs.getString("endDate"));
                }
                else
                {
                    settlementDateVO.setReversedEndDate(currentDate);
                }
            }
            else
            {
                settlementDateVO.setReversedEndDate(currentDate);
            }
            preparedStatement.setString(3, "chargeback");
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                if (functions.isValueNull(rs.getString("endDate")))
                {
                    settlementDateVO.setChargebackEndDate(rs.getString("endDate"));
                }
                else
                {
                    settlementDateVO.setChargebackEndDate(currentDate);
                }
            }
            else
            {
                settlementDateVO.setChargebackEndDate(currentDate);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::" + systemError);
        }
        catch (SQLException se)
        {
            logger.error("SQLException::::" + se);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return settlementDateVO;
    }

    public HashMap getPartnerDetails(String sMemberId)
    {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String strQuery;
        Functions functions=new Functions();
        HashMap partnerDetails = null;
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT partnerid FROM members WHERE memberid=?";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, sMemberId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                String partnerid = resultSet.getString("partnerid");
                strQuery = "SELECT processor_partnerid FROM partners WHERE partnerId=?";
                preparedStatement = conn.prepareStatement(strQuery);
                preparedStatement.setString(1,partnerid);
                resultSet=preparedStatement.executeQuery();
                if (resultSet.next()){
                    String processorPartnerId=resultSet.getString("processor_partnerid");
                    if(functions.isValueNull(processorPartnerId)){
                        partnerid=processorPartnerId;
                    }
                }

                strQuery = "SELECT partnername,logoName,address,city,state,zip,country,telno,faxno,companysupportmailid,reportfile_bgcolor FROM partners WHERE partnerId=?";
                preparedStatement = conn.prepareStatement(strQuery);
                preparedStatement.setString(1, partnerid);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next())
                {
                    partnerDetails = new HashMap();
                    partnerDetails.put("partnerid", partnerid);
                    partnerDetails.put("partnername", resultSet.getString("partnername"));
                    partnerDetails.put("logoName", resultSet.getString("logoName"));
                    partnerDetails.put("address", resultSet.getString("address"));
                    partnerDetails.put("telno", resultSet.getString("telno"));
                    partnerDetails.put("companysupportmailid", resultSet.getString("companysupportmailid"));
                    partnerDetails.put("reportfile_bgcolor",resultSet.getString("reportfile_bgcolor"));
                }
            }
        }
        catch (Exception e)
        {
            logger.error(e);
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return partnerDetails;
    }
    public HashMap getPartnerDetailsFromTerminal(String sMemberId,String terminalId)
    {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String strQuery;
        Functions functions=new Functions();
        HashMap partnerDetails = null;
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT processor_partnerid FROM member_account_mapping WHERE terminalid=? AND memberid=?";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, terminalId);
            preparedStatement.setString(2, sMemberId);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
            {
                String proccessorPartnerId = resultSet.getString("processor_partnerid");
                logger.error("ProccessorPartnerId from terminal:::" + proccessorPartnerId);
                if (functions.isValueNull(proccessorPartnerId))
                {
                    strQuery = "SELECT partnername,logoName,address,city,state,zip,country,telno,faxno,companysupportmailid,reportfile_bgcolor FROM partners WHERE partnerId=?";
                    preparedStatement = conn.prepareStatement(strQuery);
                    preparedStatement.setString(1, proccessorPartnerId);
                    resultSet = preparedStatement.executeQuery();
                    if (resultSet.next())
                    {
                        partnerDetails = new HashMap();
                        partnerDetails.put("partnerid", proccessorPartnerId);
                        partnerDetails.put("partnername", resultSet.getString("partnername"));
                        partnerDetails.put("logoName", resultSet.getString("logoName"));
                        partnerDetails.put("address", resultSet.getString("address"));
                        partnerDetails.put("telno", resultSet.getString("telno"));
                        partnerDetails.put("companysupportmailid", resultSet.getString("companysupportmailid"));
                        partnerDetails.put("reportfile_bgcolor", resultSet.getString("reportfile_bgcolor"));
                    }
                }
                else
                {
                    logger.error("inside out--");
                    strQuery = "SELECT partnerid FROM members WHERE memberid=?";
                    preparedStatement = conn.prepareStatement(strQuery);
                    preparedStatement.setString(1, sMemberId);
                    resultSet = preparedStatement.executeQuery();
                    if (resultSet.next())
                    {
                        String partnerid = resultSet.getString("partnerid");
                        strQuery = "SELECT processor_partnerid FROM partners WHERE partnerId=?";
                        preparedStatement = conn.prepareStatement(strQuery);
                        preparedStatement.setString(1, partnerid);
                        resultSet = preparedStatement.executeQuery();
                        if (resultSet.next())
                        {
                            String processorPartnerId = resultSet.getString("processor_partnerid");
                            if (functions.isValueNull(processorPartnerId))
                            {
                                partnerid = processorPartnerId;
                            }
                        }

                        strQuery = "SELECT partnername,logoName,address,city,state,zip,country,telno,faxno,companysupportmailid,reportfile_bgcolor FROM partners WHERE partnerId=?";
                        preparedStatement = conn.prepareStatement(strQuery);
                        preparedStatement.setString(1, partnerid);
                        resultSet = preparedStatement.executeQuery();
                        if (resultSet.next())
                        {
                            partnerDetails = new HashMap();
                            partnerDetails.put("partnerid", partnerid);
                            partnerDetails.put("partnername", resultSet.getString("partnername"));
                            partnerDetails.put("logoName", resultSet.getString("logoName"));
                            partnerDetails.put("address", resultSet.getString("address"));
                            partnerDetails.put("telno", resultSet.getString("telno"));
                            partnerDetails.put("companysupportmailid", resultSet.getString("companysupportmailid"));
                            partnerDetails.put("reportfile_bgcolor", resultSet.getString("reportfile_bgcolor"));
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error(e);
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return partnerDetails;
    }

    public Set getActiveMembersOfAccount(String accountId)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        Set<String> set = null;
        try
        {
            set = new HashSet();
            con = Database.getRDBConnection();
            String Query = "SELECT memberid FROM member_account_mapping WHERE accountid=? AND payoutActivation='Y'";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, accountId);
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                do
                {
                    set.add(rs.getString("memberid"));
                }
                while (rs.next());
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving PayoutDAO ", se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError ::::::: Leaving PayoutDAO ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return set;
    }

    public boolean merchantSettlementMasterEntry(String memberId, String accountId,String terminalId, String cycleId, String ispaid, String parentcycleid)
    {
        boolean status = false;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        try
        {
            con = Database.getConnection();
            String query = "insert into bank_merchant_settlement_master(memberid,accountid,terminalId,cycleid,ispaid,parentcycleid)values(?,?,?,?,?,?)";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, memberId);
            preparedStatement.setString(2, accountId);
            preparedStatement.setString(3, terminalId);
            preparedStatement.setString(4, cycleId);
            preparedStatement.setString(5, ispaid);
            preparedStatement.setString(6, parentcycleid);
            logger.error("merchantSettlementMasterEntry--------------"+preparedStatement);
            int k = preparedStatement.executeUpdate();
            if (k == 1)
            {
                status = true;
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
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return status;
    }

    public boolean updatePayoutCronExecutedFlag(String accountId, String cycleId)
    {
        boolean status = false;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        try
        {
            con = Database.getConnection();
            String query = "update bank_wiremanager set ispayoutcronexcuted=? where accountid=? and bankwiremanagerid=?";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, "Y");
            preparedStatement.setString(2, accountId);
            preparedStatement.setString(3, cycleId);
            int k = preparedStatement.executeUpdate();
            logger.error("updatePayoutCronExecutedFlag-------------"+preparedStatement);
            if (k == 1)
            {
                status = true;
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
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return status;
    }
    public List<ChargeVO> getChargesAsPerAccount(String memberId, String accountId)
    {
        Connection con = null;
        ResultSet rsCharges = null;
        PreparedStatement psCharges=null;
        List<ChargeVO> chargeVOList = new ArrayList<ChargeVO>();
        String strQuery = "select M.mappingid,M.memberid,M.chargeid,M.chargevalue,M.valuetype,C.chargename,C.isinputrequired,M.keyword,M.sequencenum,M.frequency,M.category,M.subkeyword,M.terminalid,M.paymodeid,M.cardtypeid,M.accountid from member_accounts_charges_mapping as M, charge_master as C  where M.chargeid=C.chargeid and M.memberid=? and M.accountid=?  ORDER BY sequencenum";

        try
        {
            con = Database.getRDBConnection();
            psCharges = con.prepareStatement(strQuery);
            psCharges.setString(1, memberId);
            psCharges.setString(2, accountId);
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
            logger.error("SQL Exception", e);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        finally
        {
            Database.closeResultSet(rsCharges);
            Database.closePreparedStatement(psCharges);
            Database.closeConnection(con);
        }
        return chargeVOList;
    }

    public List<ChargeVO> getChargesAsPerTerminal(TerminalVO terminalVO)
    {
        Connection con = null;
        PreparedStatement psCharges=null;
        ResultSet rsCharges = null;
        List<ChargeVO> chargeVOList = new ArrayList<ChargeVO>();
        String strQuery = "select M.mappingid,M.memberid,M.chargeid,M.chargevalue,M.valuetype,C.chargename,M.isinput_required,M.keyword,M.sequencenum,M.frequency,M.category,M.subkeyword,M.terminalid,M.paymodeid,M.cardtypeid,M.accountid,M.negativebalance from member_accounts_charges_mapping as M, charge_master as C  where M.chargeid=C.chargeid and M.memberid=? and M.accountid=? and terminalid=?  ORDER BY sequencenum";

        try
        {
            con = Database.getRDBConnection();
            psCharges = con.prepareStatement(strQuery);
            psCharges.setString(1, terminalVO.getMemberId());
            psCharges.setString(2, terminalVO.getAccountId());
            psCharges.setString(3, terminalVO.getTerminalId());
            rsCharges = psCharges.executeQuery();
            logger.error("getChargesAsPerTerminal---------------------"+psCharges);
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
                chargeVO.setNegativebalance(rsCharges.getString(17));
                chargeVOList.add(chargeVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception", e);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        finally
        {
            Database.closeResultSet(rsCharges);
            Database.closePreparedStatement(psCharges);
            Database.closeConnection(con);
        }
        return chargeVOList;
    }

    public List<BankWireManagerVO> getBankReceivedWireList(String issettlementcronexceuted, String ispayoutcronexcuted)
    {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        BankWireManagerVO bankWireManagerVO = null;
        List<BankWireManagerVO> bankWireManagerVOs = new ArrayList<BankWireManagerVO>();
        try
        {
            con = Database.getRDBConnection();
            String strQuery = "SELECT bankwiremanagerid,settleddate,pgtypeid,accountid,MID,bank_start_date,bank_end_date,server_start_date,server_end_date,processing_amount,grossamount,netfinal_amount,unpaid_amount,isrollingreservereleasewire,rollingreservereleasedateupto,declinedcoveredupto,chargebackcoveredupto,reversedcoveredupto,banksettlement_report_file,banksettlement_transaction_file,issettlementcronexceuted,ispayoutcronexcuted,ispaid FROM bank_wiremanager WHERE issettlementcronexceuted=? and  ispayoutcronexcuted=? order by settleddate asc ";
            pstmt = con.prepareStatement(strQuery);
            pstmt.setString(1, issettlementcronexceuted);
            pstmt.setString(2, ispayoutcronexcuted);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                do
                {
                    bankWireManagerVO = new BankWireManagerVO();
                    bankWireManagerVO.setBankwiremanagerId(rs.getString("bankwiremanagerid"));
                    bankWireManagerVO.setSettleddate(Functions.checkStringNull(rs.getString("settleddate")) == null ? "" : rs.getString("settleddate"));
                    bankWireManagerVO.setPgtypeId(rs.getString("pgtypeid"));
                    bankWireManagerVO.setAccountId(rs.getString("accountid"));
                    bankWireManagerVO.setMid(rs.getString("MID"));
                    bankWireManagerVO.setBank_start_date(rs.getString("bank_start_date"));
                    bankWireManagerVO.setBank_end_date(rs.getString("bank_end_date"));
                    bankWireManagerVO.setServer_start_date(rs.getString("server_start_date"));
                    bankWireManagerVO.setServer_end_date(rs.getString("server_end_date"));
                    bankWireManagerVO.setProcessing_amount(rs.getString("processing_amount"));
                    bankWireManagerVO.setGrossAmount(rs.getString("grossamount"));
                    bankWireManagerVO.setNetfinal_amount(rs.getString("netfinal_amount"));
                    bankWireManagerVO.setUnpaid_amount(rs.getString("unpaid_amount"));
                    bankWireManagerVO.setIsrollingreservereleasewire(rs.getString("isrollingreservereleasewire"));
                    bankWireManagerVO.setRollingreservereleasedateupto(Functions.checkStringNull(rs.getString("rollingreservereleasedateupto")) == null ? "" : rs.getString("rollingreservereleasedateupto"));
                    bankWireManagerVO.setDeclinedcoveredupto(rs.getString("declinedcoveredupto"));
                    bankWireManagerVO.setChargebackcoveredupto(rs.getString("chargebackcoveredupto"));
                    bankWireManagerVO.setReversedCoveredUpto(rs.getString("reversedcoveredupto"));
                    bankWireManagerVO.setBanksettlement_report_file(rs.getString("banksettlement_report_file"));
                    bankWireManagerVO.setBanksettlement_transaction_file(rs.getString("banksettlement_transaction_file"));
                    bankWireManagerVO.setSettlementCronExceuted(rs.getString("issettlementcronexceuted"));
                    bankWireManagerVO.setPayoutCronExcuted(rs.getString("ispayoutcronexcuted"));
                    bankWireManagerVO.setIspaid(rs.getString("ispaid"));
                    bankWireManagerVOs.add(bankWireManagerVO);
                }
                while (rs.next());
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception", e);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return bankWireManagerVOs;
    }

    public BankWireManagerVO getBankWireDetails(String bankWireId,String issettlementcronexceuted, String ispayoutcronexcuted)
    {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        BankWireManagerVO bankWireManagerVO = null;
        try
        {
            con = Database.getRDBConnection();
            String strQuery = "SELECT bankwiremanagerid,settleddate,pgtypeid,accountid,MID,bank_start_date,bank_end_date,server_start_date,server_end_date,processing_amount,grossamount,netfinal_amount,unpaid_amount,isrollingreservereleasewire,rollingreservereleasedateupto,declinedcoveredupto,chargebackcoveredupto,reversedcoveredupto,banksettlement_report_file,banksettlement_transaction_file,issettlementcronexceuted,ispayoutcronexcuted,ispaid,declinedCoveredStartdate,chargebackCoveredStartdate,reversedCoveredStartdate,rollingreserveStartdate FROM bank_wiremanager WHERE issettlementcronexceuted=? and  ispayoutcronexcuted=? and bankwiremanagerId=?";
            pstmt = con.prepareStatement(strQuery);
            pstmt.setString(1, issettlementcronexceuted);
            pstmt.setString(2, ispayoutcronexcuted);
            pstmt.setString(3, bankWireId);
            rs = pstmt.executeQuery();
            logger.error("getBankWireDetails--------------------------"+pstmt);
            if (rs.next())
            {
                bankWireManagerVO = new BankWireManagerVO();
                bankWireManagerVO.setBankwiremanagerId(rs.getString("bankwiremanagerid"));
                bankWireManagerVO.setSettleddate(Functions.checkStringNull(rs.getString("settleddate")) == null ? "" : rs.getString("settleddate"));
                bankWireManagerVO.setPgtypeId(rs.getString("pgtypeid"));
                bankWireManagerVO.setAccountId(rs.getString("accountid"));
                bankWireManagerVO.setMid(rs.getString("MID"));
                bankWireManagerVO.setBank_start_date(rs.getString("bank_start_date"));
                bankWireManagerVO.setBank_end_date(rs.getString("bank_end_date"));
                bankWireManagerVO.setServer_start_date(rs.getString("server_start_date"));
                bankWireManagerVO.setServer_end_date(rs.getString("server_end_date"));
                bankWireManagerVO.setProcessing_amount(rs.getString("processing_amount"));
                bankWireManagerVO.setGrossAmount(rs.getString("grossamount"));
                bankWireManagerVO.setNetfinal_amount(rs.getString("netfinal_amount"));
                bankWireManagerVO.setUnpaid_amount(rs.getString("unpaid_amount"));
                bankWireManagerVO.setIsrollingreservereleasewire(rs.getString("isrollingreservereleasewire"));
                bankWireManagerVO.setRollingreservereleasedateupto(Functions.checkStringNull(rs.getString("rollingreservereleasedateupto")) == null ? "" : rs.getString("rollingreservereleasedateupto"));
                bankWireManagerVO.setDeclinedcoveredupto(rs.getString("declinedcoveredupto"));
                bankWireManagerVO.setChargebackcoveredupto(rs.getString("chargebackcoveredupto"));
                bankWireManagerVO.setReversedCoveredUpto(rs.getString("reversedcoveredupto"));
                bankWireManagerVO.setBanksettlement_report_file(rs.getString("banksettlement_report_file"));
                bankWireManagerVO.setBanksettlement_transaction_file(rs.getString("banksettlement_transaction_file"));
                bankWireManagerVO.setSettlementCronExceuted(rs.getString("issettlementcronexceuted"));
                bankWireManagerVO.setPayoutCronExcuted(rs.getString("ispayoutcronexcuted"));
                bankWireManagerVO.setIspaid(rs.getString("ispaid"));

                bankWireManagerVO.setDeclinedcoveredStartdate(Functions.checkStringNull(rs.getString("declinedCoveredStartdate")) == null ? "0000-00-00 00:00:00" : rs.getString("declinedCoveredStartdate"));
                bankWireManagerVO.setChargebackcoveredStartdate(Functions.checkStringNull(rs.getString("chargebackCoveredStartdate")) == null ? "0000-00-00 00:00:00" : rs.getString("chargebackCoveredStartdate"));
                bankWireManagerVO.setReversedCoveredStartdate(Functions.checkStringNull(rs.getString("reversedCoveredStartdate")) == null ? "0000-00-00 00:00:00" : rs.getString("reversedCoveredStartdate"));
                bankWireManagerVO.setRollingreservereleaseStartdate(Functions.checkStringNull(rs.getString("rollingreserveStartdate")) == null ? "0000-00-00 00:00:00" : rs.getString("rollingreserveStartdate"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception", e);
        }
        catch(SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return bankWireManagerVO;
    }

    public BankWireManagerVO getBankWireDetailsForPartnerCommission(String issettlementcronexceuted, String partnerCommissionCron,String bankWireId)
    {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        BankWireManagerVO bankWireManagerVO = null;
        try
        {
            con = Database.getRDBConnection();
            String strQuery = "SELECT bankwiremanagerid,settleddate,pgtypeid,accountid,MID,bank_start_date,bank_end_date,server_start_date,server_end_date,processing_amount,grossamount,netfinal_amount,unpaid_amount,isrollingreservereleasewire,rollingreservereleasedateupto,declinedcoveredupto,chargebackcoveredupto,reversedcoveredupto,banksettlement_report_file,banksettlement_transaction_file,issettlementcronexceuted,ispayoutcronexcuted,ispaid,declinedCoveredStartdate,chargebackCoveredStartdate,reversedCoveredStartdate,rollingreserveStartdate FROM bank_wiremanager WHERE issettlementcronexceuted=? and  isPartnerCommCronExecuted=? and bankwiremanagerId=?";
            pstmt = con.prepareStatement(strQuery);
            pstmt.setString(1, issettlementcronexceuted);
            pstmt.setString(2, partnerCommissionCron);
            pstmt.setString(3, bankWireId);
            logger.error("Query for bankWire::::"+pstmt);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                bankWireManagerVO = new BankWireManagerVO();
                bankWireManagerVO.setBankwiremanagerId(rs.getString("bankwiremanagerid"));
                bankWireManagerVO.setSettleddate(Functions.checkStringNull(rs.getString("settleddate")) == null ? "" : rs.getString("settleddate"));
                bankWireManagerVO.setPgtypeId(rs.getString("pgtypeid"));
                bankWireManagerVO.setAccountId(rs.getString("accountid"));
                bankWireManagerVO.setMid(rs.getString("MID"));
                bankWireManagerVO.setBank_start_date(rs.getString("bank_start_date"));
                bankWireManagerVO.setBank_end_date(rs.getString("bank_end_date"));
                bankWireManagerVO.setServer_start_date(rs.getString("server_start_date"));
                bankWireManagerVO.setServer_end_date(rs.getString("server_end_date"));
                bankWireManagerVO.setProcessing_amount(rs.getString("processing_amount"));
                bankWireManagerVO.setGrossAmount(rs.getString("grossamount"));
                bankWireManagerVO.setNetfinal_amount(rs.getString("netfinal_amount"));
                bankWireManagerVO.setUnpaid_amount(rs.getString("unpaid_amount"));
                bankWireManagerVO.setIsrollingreservereleasewire(rs.getString("isrollingreservereleasewire"));
                bankWireManagerVO.setRollingreservereleasedateupto(Functions.checkStringNull(rs.getString("rollingreservereleasedateupto")) == null ? "" : rs.getString("rollingreservereleasedateupto"));
                bankWireManagerVO.setDeclinedcoveredupto(rs.getString("declinedcoveredupto"));
                bankWireManagerVO.setChargebackcoveredupto(rs.getString("chargebackcoveredupto"));
                bankWireManagerVO.setReversedCoveredUpto(rs.getString("reversedcoveredupto"));
                bankWireManagerVO.setBanksettlement_report_file(rs.getString("banksettlement_report_file"));
                bankWireManagerVO.setBanksettlement_transaction_file(rs.getString("banksettlement_transaction_file"));
                bankWireManagerVO.setSettlementCronExceuted(rs.getString("issettlementcronexceuted"));
                bankWireManagerVO.setPayoutCronExcuted(rs.getString("ispayoutcronexcuted"));
                bankWireManagerVO.setIspaid(rs.getString("ispaid"));

                bankWireManagerVO.setDeclinedcoveredStartdate(Functions.checkStringNull(rs.getString("declinedCoveredStartdate")) == null ? "0000-00-00 00:00:00" : rs.getString("declinedCoveredStartdate"));
                bankWireManagerVO.setChargebackcoveredStartdate(Functions.checkStringNull(rs.getString("chargebackCoveredStartdate")) == null ? "0000-00-00 00:00:00" : rs.getString("chargebackCoveredStartdate"));
                bankWireManagerVO.setReversedCoveredStartdate(Functions.checkStringNull(rs.getString("reversedCoveredStartdate")) == null ? "0000-00-00 00:00:00" : rs.getString("reversedCoveredStartdate"));
                bankWireManagerVO.setRollingreservereleaseStartdate(Functions.checkStringNull(rs.getString("rollingreserveStartdate")) == null ? "0000-00-00 00:00:00" : rs.getString("rollingreserveStartdate"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception", e);
        }
        catch(SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return bankWireManagerVO;
    }

    public List<BankWireManagerVO> getBankWireListForPartnerCommissionCron(String isSettlementCronExceuted, String isPartnerCommissionCronExcuted)
    {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        BankWireManagerVO bankWireManagerVO = null;
        List<BankWireManagerVO> bankWireManagerVOs = new ArrayList<BankWireManagerVO>();
        try
        {
            con = Database.getRDBConnection();
            String strQuery = "SELECT bankwiremanagerid,accountid,server_start_date,server_end_date,declinedcoveredupto,chargebackcoveredupto,reversedcoveredupto FROM bank_wiremanager WHERE issettlementcronexceuted=? and isPartnerCommCronExecuted=? order by settleddate asc ";
            pstmt = con.prepareStatement(strQuery);
            pstmt.setString(1, isSettlementCronExceuted);
            pstmt.setString(2, isPartnerCommissionCronExcuted);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                bankWireManagerVO = new BankWireManagerVO();
                bankWireManagerVO.setBankwiremanagerId(rs.getString("bankwiremanagerid"));
                bankWireManagerVO.setAccountId(rs.getString("accountid"));
                bankWireManagerVO.setServer_start_date(rs.getString("server_start_date"));
                bankWireManagerVO.setServer_end_date(rs.getString("server_end_date"));
                bankWireManagerVO.setDeclinedcoveredupto(rs.getString("declinedcoveredupto"));
                bankWireManagerVO.setChargebackcoveredupto(rs.getString("chargebackcoveredupto"));
                bankWireManagerVO.setReversedCoveredUpto(rs.getString("reversedcoveredupto"));
                bankWireManagerVOs.add(bankWireManagerVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception", e);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return bankWireManagerVOs;
    }

    public boolean isChargesAppliedOnTerminal(TerminalVO terminalVO)
    {
        String strQuery = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        Connection conn = null;
        boolean isChargesApplyOnTerminal = false;
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT * FROM member_accounts_charges_mapping where memberid=? and accountid=? and terminalid=?";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getTerminalId());
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                isChargesApplyOnTerminal = true;
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return isChargesApplyOnTerminal;
    }

    //New Method Added During Weekly Payout Cron Final Implementation
    public TransactionSummaryVO getAuthFailCountAmountByDtstamp(String authfailedTransStartDate, String authfailedTransEndDate, TerminalVO terminalVO, String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;

        if (authfailedTransStartDate.endsWith(".0") || authfailedTransStartDate.contains(".0")){
            authfailedTransStartDate = authfailedTransStartDate.substring(0, authfailedTransStartDate.indexOf('.')); }
        String datetimeArray[] = authfailedTransStartDate.split(" ");
        String mm = String.valueOf(Integer.parseInt(datetimeArray[0].split("-")[1]) - 1);
        String startdtstmp = Functions.converttomillisec(mm,datetimeArray[0].split("-")[2],datetimeArray[0].split("-")[0],datetimeArray[1].split(":")[0],datetimeArray[1].split(":")[1],datetimeArray[1].split(":")[2]);

        if (authfailedTransEndDate.endsWith(".0") || authfailedTransEndDate.contains(".0")){
            authfailedTransEndDate=authfailedTransEndDate.substring(0, authfailedTransEndDate.indexOf('.')); }
        String datetimeArray1[] = authfailedTransEndDate.split(" ");
        String mm1 = String.valueOf(Integer.parseInt(datetimeArray1[0].split("-")[1]) - 1);
        String enddtstmp = Functions.converttomillisec(mm1,datetimeArray1[0].split("-")[2],datetimeArray1[0].split("-")[0],datetimeArray1[1].split(":")[0],datetimeArray1[1].split(":")[1],datetimeArray1[1].split(":")[2]);

        try
        {
            con = Database.getRDBConnection();
            StringBuilder strQry = new StringBuilder("select status,count(*) as 'count',sum(amount) as 'amount' from " + sTableName + " where toid=? and accountid=? and terminalid=? and status=? and dtstamp >=? and dtstamp <=? ");
            strQry.append(" group by status");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getTerminalId()));
            // preparedStatement.setInt(3, Integer.parseInt(terminalVO.getPaymodeId()));
            //preparedStatement.setInt(4, Integer.parseInt(terminalVO.getCardTypeId()));
            preparedStatement.setString(4, "authfailed");
            preparedStatement.setString(5, startdtstmp);
            preparedStatement.setString(6, enddtstmp);
            rsPayReport = preparedStatement.executeQuery();
            if (rsPayReport.next())
            {
                transactionSummaryVO.setAuthfailedAmount(rsPayReport.getDouble("amount"));
                transactionSummaryVO.setCountOfAuthfailed(rsPayReport.getLong("count"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionSummaryVO;
    }

    public TransactionSummaryVO getSettledCountAmountByDtstamp(String settledTransStartDate, String settledTransEndDate, TerminalVO terminalVO, String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuilder strQry = new StringBuilder("select status,count(*) as 'count',sum(captureamount) as 'captureamount' from " + sTableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and status=? and FROM_UNIXTIME(dtstamp) >=? and FROM_UNIXTIME(dtstamp) <=? ");
            strQry.append(" group by status");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getPaymodeId()));
            preparedStatement.setInt(4, Integer.parseInt(terminalVO.getCardTypeId()));
            preparedStatement.setString(5, "settled");
            preparedStatement.setString(6, settledTransStartDate);
            preparedStatement.setString(7, settledTransEndDate);
            rsPayReport = preparedStatement.executeQuery();
            if (rsPayReport.next())
            {
                transactionSummaryVO.setSettledAmount(rsPayReport.getDouble("captureamount"));
                transactionSummaryVO.setCountOfSettled(rsPayReport.getLong("count"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionSummaryVO;

    }
    //New method added for changing data from wiremanager to merchant_wiremanager

    /*
       @programmer:sandip
        added new methods for getting total processing count and amount for certain time period
     */
    public TransactionSummaryVO getProcessingCountAmountDetails(String settledTransStartDate, String settledTransEndDate, TerminalVO terminalVO, String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuilder strQry = new StringBuilder("select count(*) as 'count',sum(captureamount) as 'amount' from " + sTableName + " where toid=? and accountid=? and terminalid=? and status in('settled','reversed','chargeback','chargebackreversed') and FROM_UNIXTIME(dtstamp) >=? and FROM_UNIXTIME(dtstamp) <=? ");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getTerminalId()));
            //preparedStatement.setInt(3, Integer.parseInt(terminalVO.getPaymodeId()));
            //preparedStatement.setInt(4, Integer.parseInt(terminalVO.getCardTypeId()));
            preparedStatement.setString(4, settledTransStartDate);
            preparedStatement.setString(5, settledTransEndDate);
            rsPayReport = preparedStatement.executeQuery();
            logger.error("getProcessingCountAmountDetails--------------------" + preparedStatement);
            if (rsPayReport.next())
            {
                transactionSummaryVO.setTotalProcessingAmount(rsPayReport.getDouble("amount"));
                transactionSummaryVO.setTotalProcessingCount(rsPayReport.getLong("count"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionSummaryVO;
    }

    public TransactionSummaryVO getReversalCountAmountByTimestampOld(String reversedTransStartDate, String reversedTransEndDate, TerminalVO terminalVO, String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuilder strQry = new StringBuilder("select status,count(*) as 'count',sum(refundamount) as 'refundamount' from " + sTableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and status=? and TIMESTAMP >=? and TIMESTAMP <=?  ");
            strQry.append(" group by status");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getPaymodeId()));
            preparedStatement.setInt(4, Integer.parseInt(terminalVO.getCardTypeId()));
            preparedStatement.setString(5, "reversed");
            preparedStatement.setString(6, reversedTransStartDate);
            preparedStatement.setString(7, reversedTransEndDate);
            rsPayReport = preparedStatement.executeQuery();
            if (rsPayReport.next())
            {
                transactionSummaryVO.setCountOfReversed(rsPayReport.getLong("count"));
                transactionSummaryVO.setReversedAmount(rsPayReport.getDouble("refundamount"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionSummaryVO;

    }

    public TransactionSummaryVO getReversalCountAmountByTimestampOld1(String reversedTransStartDate, String reversedTransEndDate, TerminalVO terminalVO, String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        Functions functions=new Functions();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        ResultSet countResult=null;
        String trackingIds=null;
        try
        {
            con = Database.getRDBConnection();
            StringBuilder strQry = new StringBuilder("select trackingid,status from " + sTableName + " where toid=? and accountid=? and paymodeid=? and cardtypeid=? and status=? and TIMESTAMP >=? and TIMESTAMP <=?  ");
            StringBuilder countQuery = new StringBuilder("select COUNT(*) as count from " + sTableName + " where toid="+terminalVO.getMemberId()+" and accountid="+terminalVO.getAccountId()+" and paymodeid="+terminalVO.getPaymodeId()+" and cardtypeid="+terminalVO.getCardTypeId()+" and status='reversed' and TIMESTAMP >='"+reversedTransStartDate+"' and TIMESTAMP <='"+reversedTransEndDate+"'");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getPaymodeId()));
            preparedStatement.setInt(4, Integer.parseInt(terminalVO.getCardTypeId()));
            preparedStatement.setString(5, "reversed");
            preparedStatement.setString(6, reversedTransStartDate);
            preparedStatement.setString(7, reversedTransEndDate);
            logger.error("Query for reversed transaction:::" + preparedStatement);
            logger.error("Count Query::::::"+countQuery);
            countResult = Database.executeQuery(countQuery.toString(), con);
            if (countResult.next())
            {
                transactionSummaryVO.setCountOfReversed(countResult.getLong("count"));
            }
            rsPayReport = preparedStatement.executeQuery();
            while(rsPayReport.next())
            {
                trackingIds+=","+rsPayReport.getString("trackingid");
            }
            logger.error("List OF tracking id:::"+trackingIds);
            if(trackingIds!=null){
                StringBuilder query=new StringBuilder("SELECT SUM(amount) as amount FROM transaction_common_details WHERE trackingid IN ("+trackingIds+") AND status IN('reversed','partialrefund') AND TIMESTAMP >=? AND TIMESTAMP <=?");
                preparedStatement = con.prepareStatement(query.toString());
                preparedStatement.setString(1, reversedTransStartDate);
                preparedStatement.setString(2, reversedTransEndDate);
                logger.error("Reverse Amount:::"+preparedStatement);
                ResultSet rs=preparedStatement.executeQuery();
                if(rs.next()){
                    transactionSummaryVO.setReversedAmount(rs.getDouble("amount"));
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionSummaryVO;

    }

    public TransactionSummaryVO getReversalCountAmountByTimestamp(String reversedTransStartDate, String reversedTransEndDate, TerminalVO terminalVO, String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuilder strQry = new StringBuilder(" SELECT COUNT(tcd.`trackingid`) as count, SUM(tcd.`amount`) as amount FROM transaction_common_details AS tcd,transaction_common AS tc WHERE toid=? AND accountid=? AND terminalid=? AND tcd.status IN('partialrefund','reversed') AND tc.trackingid=tcd.trackingid AND tcd.timestamp >=? AND tcd.timestamp<=?");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getTerminalId()));
            preparedStatement.setString(4, reversedTransStartDate);
            preparedStatement.setString(5, reversedTransEndDate);
            logger.error("Query for reversed transaction:::" + preparedStatement);
            rsPayReport = preparedStatement.executeQuery();
            if(rsPayReport.next()){
                transactionSummaryVO.setCountOfReversed(rsPayReport.getLong("count"));
                transactionSummaryVO.setReversedAmount(rsPayReport.getDouble("amount"));
            }

        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionSummaryVO;

    }
    public TransactionSummaryVO getChargebackReversedCountAmountByTerminal(String chargebackReversedSatrtDate, String chargebackReversedEndDate, TerminalVO terminalVO, String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuilder strQry = new StringBuilder(" SELECT COUNT(tcd.`trackingid`) as count, SUM(tcd.`amount`) as amount FROM transaction_common_details AS tcd,transaction_common AS tc WHERE toid=? AND accountid=? AND terminalid=? AND tcd.status='chargebackreversed' AND tc.trackingid=tcd.trackingid AND tcd.timestamp >=? AND tcd.timestamp<=?");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getTerminalId()));
            preparedStatement.setString(4, chargebackReversedSatrtDate);
            preparedStatement.setString(5, chargebackReversedEndDate);
            logger.error("Query for getChargebackReversedCountAmountByTerminal:::" + preparedStatement);
            rsPayReport = preparedStatement.executeQuery();
            if(rsPayReport.next()){
                transactionSummaryVO.setCountOfChargebackReversed(rsPayReport.getLong("count"));
                transactionSummaryVO.setChargebackReversedAmount(rsPayReport.getDouble("amount"));
            }

        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionSummaryVO;

    }


    public TransactionSummaryVO getPayoutCountAmountByTerminal(String reversedTransStartDate, String reversedTransEndDate, TerminalVO terminalVO, String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;


//        ----------------- payout timestamp code
        logger.error("reversedTransStartDate+++   "+reversedTransStartDate);
        logger.error("reversedTransEndDate+++   "+reversedTransEndDate);

        if (reversedTransStartDate.endsWith(".0") || reversedTransStartDate.contains(".0")){
            reversedTransStartDate = reversedTransStartDate.substring(0, reversedTransStartDate.indexOf('.'));
        }
        String payoutDatetimeArray[] = reversedTransStartDate.split(" ");
        String payoutmm = String.valueOf(Integer.parseInt(payoutDatetimeArray[0].split("-")[1]) - 1);
        String payoutStartdtstmp = Functions.converttomillisec(payoutmm,payoutDatetimeArray[0].split("-")[2],payoutDatetimeArray[0].split("-")[0],payoutDatetimeArray[1].split(":")[0],payoutDatetimeArray[1].split(":")[1],payoutDatetimeArray[1].split(":")[2]);
        logger.error("payoutmm+++ "+payoutmm+" ======= payoutStartdtstmp+++ "+payoutStartdtstmp);

        if (reversedTransEndDate.endsWith(".0") || reversedTransEndDate.contains(".0")){
            reversedTransEndDate=reversedTransEndDate.substring(0, reversedTransEndDate.indexOf('.'));
        }
        String payoutdatetimeArray1[] = reversedTransEndDate.split(" ");
        String payoutmm1 = String.valueOf(Integer.parseInt(payoutdatetimeArray1[0].split("-")[1]) - 1);
        String payoutEnddtstmp = Functions.converttomillisec(payoutmm1,payoutdatetimeArray1[0].split("-")[2],payoutdatetimeArray1[0].split("-")[0],payoutdatetimeArray1[1].split(":")[0],payoutdatetimeArray1[1].split(":")[1],payoutdatetimeArray1[1].split(":")[2]);
        logger.error("payoutmm1+++ "+payoutmm1+" ======= payoutEnddtstmp+++ "+payoutEnddtstmp);
        try
        {
            con = Database.getRDBConnection();
            //StringBuilder strQry = new StringBuilder(" SELECT COUNT(tcd.`trackingid`) as count, SUM(tcd.`amount`) as amount FROM transaction_common_details AS tcd,transaction_common AS tc WHERE toid=? AND accountid=? AND terminalid=? AND tc.status='payoutsuccessful' AND tcd.status='payoutsuccessful' AND tc.trackingid=tcd.trackingid AND tcd.timestamp >=? AND tcd.timestamp<=?");
            //StringBuilder strQry = new StringBuilder("select count(*) as 'count',sum(captureamount) as 'amount' from " + sTableName + " where toid=? and accountid=? and terminalid=? and status in('settled','reversed','chargeback','chargebackreversed') and FROM_UNIXTIME(dtstamp) >=? and FROM_UNIXTIME(dtstamp) <=? ");
//            StringBuilder strQry = new StringBuilder(" SELECT COUNT(trackingid) as count, SUM(payoutamount) as amount FROM transaction_common WHERE toid=? AND accountid=? AND terminalid=? AND status='payoutsuccessful' AND  FROM_UNIXTIME(dtstamp) >=? and FROM_UNIXTIME(dtstamp) <=?");
            StringBuilder strQry = new StringBuilder("SELECT COUNT(CASE WHEN STATUS  IN('payoutsuccessful') THEN 1 END) AS count, SUM(CASE WHEN STATUS IN ('payoutsuccessful')  THEN payoutamount ELSE 0 END) AS amount FROM transaction_common WHERE toid=? AND accountid=? AND terminalid=? AND status='payoutsuccessful' AND  dtstamp >=? and dtstamp <=?");

            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getTerminalId()));
            preparedStatement.setString(4, payoutStartdtstmp);
            preparedStatement.setString(5, payoutEnddtstmp);
            logger.error("Query for getPayoutCountAmountByTerminal:::" + preparedStatement);
            rsPayReport = preparedStatement.executeQuery();
            if(rsPayReport.next()){
                transactionSummaryVO.setCountOfPayout(rsPayReport.getLong("count"));
                transactionSummaryVO.setPayoutAmount(rsPayReport.getDouble("amount"));
            }

        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionSummaryVO;

    }
    public TransactionSummaryVO getRefundReverseCountAmountByTerminal(String reversedTransStartDate, String reversedTransEndDate, TerminalVO terminalVO, String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuilder strQry = new StringBuilder(" SELECT COUNT(tcd.`trackingid`) as count, SUM(tcd.`amount`) as amount FROM transaction_common_details AS tcd,transaction_common AS tc WHERE toid=? AND accountid=? AND terminalid=? AND tcd.status='refundreversed' AND tc.trackingid=tcd.trackingid AND tcd.timestamp >=? AND tcd.timestamp<=?");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getTerminalId()));
            preparedStatement.setString(4, reversedTransStartDate);
            preparedStatement.setString(5, reversedTransEndDate);
            logger.error("Query for getRefundReverseCountAmountByTerminal:::" + preparedStatement);
            rsPayReport = preparedStatement.executeQuery();
            if(rsPayReport.next()){
                transactionSummaryVO.setRefundReverseCount(rsPayReport.getLong("count"));
                transactionSummaryVO.setRefundReverseAmount(rsPayReport.getDouble("amount"));
            }

        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionSummaryVO;

    }

    public TransactionSummaryVO getChargebackCountAmountByTimestamp(String chargebackTransStartDate, String chargebackTransEndDate, TerminalVO terminalVO, String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuilder strQry = new StringBuilder(" SELECT COUNT(tcd.`trackingid`) as count, SUM(tcd.`amount`) as amount FROM transaction_common_details AS tcd,transaction_common AS tc WHERE toid=? AND accountid=? AND terminalid=? AND tcd.status=? AND tc.trackingid=tcd.trackingid AND tcd.timestamp >=? AND tcd.timestamp<=?");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getTerminalId()));
            preparedStatement.setString(4, "chargeback");
            preparedStatement.setString(5, chargebackTransStartDate);
            preparedStatement.setString(6, chargebackTransEndDate);
            rsPayReport = preparedStatement.executeQuery();
            logger.error("getChargebackCountAmountByTimestamp---------------------"+preparedStatement);
            if (rsPayReport.next())
            {
                transactionSummaryVO.setCountOfChargeback(rsPayReport.getLong("count"));
                transactionSummaryVO.setChargebackAmount(rsPayReport.getDouble("amount"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionSummaryVO;

    }

    public void setUnpaidBalanceAmountMWMOnTerminal(TerminalVO terminalVO, WireAmountVO wireAmountVO)
    {
        String strQuery = "SELECT unpaidamount AS 'UnpaidBalanceAmount' FROM merchant_wiremanager WHERE toid=? AND accountid=? and terminalid=? ORDER BY settledid DESC LIMIT 1";
        Connection con = null;
        PreparedStatement psWireBalaceAmount=null;
        ResultSet rsWireBalnceAmount=null;
        try
        {
            con = Database.getRDBConnection();
            psWireBalaceAmount = con.prepareStatement(strQuery);
            psWireBalaceAmount.setString(1, terminalVO.getMemberId());
            psWireBalaceAmount.setString(2, terminalVO.getAccountId());
            psWireBalaceAmount.setString(3, terminalVO.getTerminalId());
            rsWireBalnceAmount = psWireBalaceAmount.executeQuery();
            if (rsWireBalnceAmount.next())
            {
                wireAmountVO.setUnpaidBalanceAmount(rsWireBalnceAmount.getDouble("UnpaidBalanceAmount"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ::", e);
        }
        finally
        {
            Database.closeResultSet(rsWireBalnceAmount);
            Database.closePreparedStatement(psWireBalaceAmount);
            Database.closeConnection(con);
        }
    }

    public double getPartnerUnpaidBalanceAmountOnTerminal(TerminalVO terminalVO)
    {
        String strQuery = "SELECT partnerunpaidamount AS 'PartnerUnpaidBalanceAmount' FROM partner_wiremanager WHERE toid=? AND accountid=? and terminalid=? ORDER BY settledid DESC LIMIT 1";
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet rs = null;
        double partnerunpaidamount = 0.00;
        try
        {
            con = Database.getRDBConnection();
            pstmt = con.prepareStatement(strQuery);
            pstmt.setString(1, terminalVO.getMemberId());
            pstmt.setString(2, terminalVO.getAccountId());
            pstmt.setString(3, terminalVO.getTerminalId());
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                partnerunpaidamount = rs.getDouble("PartnerUnpaidBalanceAmount");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ::", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return partnerunpaidamount;
    }

    public double getAgentUnpaidBalanceAmountOnTerminal(TerminalVO terminalVO)
    {
        String strQuery = "SELECT agentunpaidamount AS 'AgentUnpaidBalanceAmount' FROM agent_wiremanager WHERE toid=? AND accountid=? and terminalid=? ORDER BY settledid DESC LIMIT 1";
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet rs = null;
        double agentunpaidamount = 0.00;
        try
        {
            con = Database.getRDBConnection();
            pstmt = con.prepareStatement(strQuery);
            pstmt.setString(1, terminalVO.getMemberId());
            pstmt.setString(2, terminalVO.getAccountId());
            pstmt.setString(3, terminalVO.getTerminalId());
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                agentunpaidamount = rs.getDouble("AgentUnpaidBalanceAmount");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ::", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return agentunpaidamount;
    }

    public String getSettledStartDateToMerchantPayout(TerminalVO terminalVO)
    {
        String strQuery = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        Connection conn = null;
        String settleCoveredUptoDate = null;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT lastdate FROM merchant_wiremanager WHERE toid=? AND accountid=? and terminalid=? ORDER BY settledid DESC LIMIT 1";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getTerminalId());
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                try
                {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(targetFormat.parse(rsPayout.getString("lastdate")));
                    cal.add(Calendar.SECOND, 1);
                    settleCoveredUptoDate = targetFormat.format(cal.getTime());
                }
                catch (ParseException e)
                {
                    logger.error("Exception :::", e);
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return settleCoveredUptoDate;
    }

    public String getPartnerCommissionSettledStartDate(TerminalVO terminalVO)
    {
        String strQuery = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        Connection conn = null;
        String settleCoveredUptoDate = null;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT settlementenddate FROM partner_wiremanager WHERE toid=? AND accountid=? and terminalid=? ORDER BY settledid DESC LIMIT 1";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getTerminalId());
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                try
                {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(targetFormat.parse(rsPayout.getString("settlementenddate")));
                    cal.add(Calendar.SECOND, 1);
                    settleCoveredUptoDate = targetFormat.format(cal.getTime());
                }
                catch (ParseException e)
                {
                    logger.error("Exception :::", e);
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return settleCoveredUptoDate;
    }

    public String getDeclineStartDateToMerchantPayout(TerminalVO terminalVO)
    {
        String strQuery = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        Connection conn = null;
        String declineCoveredUptoDate = null;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT declinedcoverdateupto FROM merchant_wiremanager WHERE toid=? AND accountid=? and terminalid=? ORDER BY settledid DESC LIMIT 1";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getTerminalId());
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                try
                {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(targetFormat.parse(rsPayout.getString("declinedcoverdateupto")));
                    cal.add(Calendar.SECOND, 1);
                    declineCoveredUptoDate = targetFormat.format(cal.getTime());
                }
                catch (ParseException e)
                {
                    logger.error("Exception :::", e);
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        //System.out.println("return----"+declineCoveredUptoDate);
        return declineCoveredUptoDate;
    }

    public String getPartnerCommissionDeclinedStartDate(TerminalVO terminalVO)
    {
        StringBuffer strQuery = new StringBuffer();
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        Connection conn = null;
        String declineCoveredUptoDate = null;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            conn = Database.getRDBConnection();
            strQuery.append("SELECT declinedcoverdateupto FROM partner_wiremanager WHERE toid=? AND accountid=? and terminalid=? ORDER BY settledid DESC LIMIT 1");
            preparedStatement = conn.prepareStatement(strQuery.toString());
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getTerminalId());
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                try
                {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(targetFormat.parse(rsPayout.getString("declinedcoverdateupto")));
                    cal.add(Calendar.SECOND, 1);
                    declineCoveredUptoDate = targetFormat.format(cal.getTime());
                }
                catch (ParseException e)
                {
                    logger.error("Exception :::", e);
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return declineCoveredUptoDate;
    }

    public String getReverseStartDateToMerchantPayout(TerminalVO terminalVO)
    {
        String strQuery = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        Connection conn = null;
        String reversedCoveredUptoDate = null;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT reversedcoverdateupto FROM merchant_wiremanager WHERE toid=? AND accountid=? and terminalid=? ORDER BY settledid DESC LIMIT 1";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getTerminalId());
            rsPayout = preparedStatement.executeQuery();

            if (rsPayout.next())
            {
                Calendar cal = Calendar.getInstance();
                cal.setTime(targetFormat.parse(rsPayout.getString("reversedcoverdateupto")));
                cal.add(Calendar.SECOND, 1);
                reversedCoveredUptoDate = targetFormat.format(cal.getTime());
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ", e);
        }
        catch (ParseException e)
        {
            logger.error("Exception :::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return reversedCoveredUptoDate;
    }

    public String getPartnerCommissionReversedStartDate(TerminalVO terminalVO)
    {
        String strQuery = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        Connection conn = null;
        String reversedCoveredUptoDate = null;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT reversedcoverdateupto FROM partner_wiremanager WHERE toid=? AND accountid=? and terminalid=? ORDER BY settledid DESC LIMIT 1";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getTerminalId());
            rsPayout = preparedStatement.executeQuery();

            if (rsPayout.next())
            {
                Calendar cal = Calendar.getInstance();
                cal.setTime(targetFormat.parse(rsPayout.getString("reversedcoverdateupto")));
                cal.add(Calendar.SECOND, 1);
                reversedCoveredUptoDate = targetFormat.format(cal.getTime());
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ", e);
        }
        catch (ParseException e)
        {
            logger.error("Exception :::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return reversedCoveredUptoDate;
    }

    public String getChargebackCoveredUpDateFromMWM(String memberId, String accountId)
    {
        String strQuery = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        Connection conn = null;
        String chargebackCoveredUptoDate = null;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT chargebackcoverdateupto FROM merchant_wiremanager WHERE toid=? AND accountid=? ORDER BY settledid DESC LIMIT 1";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, memberId);
            preparedStatement.setString(2, accountId);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                Calendar cal = Calendar.getInstance();
                cal.setTime(targetFormat.parse(rsPayout.getString("chargebackcoverdateupto")));
                cal.add(Calendar.SECOND, 1);
                chargebackCoveredUptoDate = targetFormat.format(cal.getTime());
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ", e);
        }
        catch (ParseException e)
        {
            logger.error("Exception :::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return chargebackCoveredUptoDate;
    }

    public String getChargebackStartDateToMerchant(TerminalVO terminalVO)
    {
        String strQuery = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        Connection conn = null;
        String chargebackCoveredUptoDate = null;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT chargebackcoverdateupto FROM merchant_wiremanager WHERE toid=? AND accountid=? and terminalid=? ORDER BY settledid DESC LIMIT 1";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getTerminalId());
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                Calendar cal = Calendar.getInstance();
                cal.setTime(targetFormat.parse(rsPayout.getString("chargebackcoverdateupto")));
                cal.add(Calendar.SECOND, 1);
                chargebackCoveredUptoDate = targetFormat.format(cal.getTime());
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ", e);
        }
        catch (ParseException e)
        {
            logger.error("Exception :::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return chargebackCoveredUptoDate;
    }

    public String getPartnerCommissionChargebackStartDate(TerminalVO terminalVO)
    {
        String strQuery = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        Connection conn = null;
        String chargebackCoveredUptoDate = null;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT chargebackcoverdateupto FROM partner_wiremanager WHERE toid=? AND accountid=? and terminalid=? ORDER BY settledid DESC LIMIT 1";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getTerminalId());
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                Calendar cal = Calendar.getInstance();
                cal.setTime(targetFormat.parse(rsPayout.getString("chargebackcoverdateupto")));
                cal.add(Calendar.SECOND, 1);
                chargebackCoveredUptoDate = targetFormat.format(cal.getTime());
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ", e);
        }
        catch (ParseException e)
        {
            logger.error("Exception :::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return chargebackCoveredUptoDate;
    }

    public void setUnpaidBalanceAmountFromMWM(TerminalVO terminalVO, WireAmountVO wireAmountVO)
    {
        String strQuery = "SELECT SUM(unpaidamount) AS 'UnpaidBalanceAmount' FROM merchant_wiremanager WHERE toid=? AND accountid=? ";
        Connection con = null;
        PreparedStatement psWireBalaceAmount=null;
        ResultSet rsWireBalnceAmount=null;
        try
        {
            con = Database.getRDBConnection();
            psWireBalaceAmount = con.prepareStatement(strQuery);
            psWireBalaceAmount.setString(1, terminalVO.getMemberId());
            psWireBalaceAmount.setString(2, terminalVO.getAccountId());
            rsWireBalnceAmount = psWireBalaceAmount.executeQuery();
            if (rsWireBalnceAmount.next())
            {
                wireAmountVO.setUnpaidBalanceAmount(rsWireBalnceAmount.getDouble("UnpaidBalanceAmount"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ::", e);
        }
        finally
        {
            Database.closeResultSet(rsWireBalnceAmount);
            Database.closePreparedStatement(psWireBalaceAmount);
            Database.closeConnection(con);
        }
    }

    public boolean isBankWireValidToProcess(String bankwireId, String accountId)
    {
        boolean status = false;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT bankwiremanagerid,MIN(settleddate) FROM bank_wiremanager WHERE accountid=? AND issettlementcronexceuted='Y' AND ispayoutcronexcuted='N' GROUP BY accountid";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, accountId);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                int rsbankwireid = rsPayout.getInt("bankwiremanagerid");
                int outbankwireid = Integer.parseInt(bankwireId);
                if (rsbankwireid == outbankwireid)
                {
                    status = true;
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return status;
    }

    public void setAgentWireUnpaidAmount(AgentDetailsVO agentDetailsVO, TerminalVO terminalVO, WireAmountVO wireAmountVO)
    {
        String strQuery = "SELECT SUM(agentunpaidamount) as 'agentunpaidamount'  FROM agent_wiremanager WHERE agentid=? AND toid=? AND accountid=? AND paymodeid=? AND cardtypeid=?";
        Connection con = null;
        PreparedStatement psWireBalaceAmount=null;
        ResultSet rsWireBalnceAmount=null;
        try
        {
            con = Database.getRDBConnection();
            psWireBalaceAmount = con.prepareStatement(strQuery);
            psWireBalaceAmount.setString(1, agentDetailsVO.getAgentId());
            psWireBalaceAmount.setString(2, terminalVO.getMemberId());
            psWireBalaceAmount.setString(3, terminalVO.getAccountId());
            psWireBalaceAmount.setString(4, terminalVO.getPaymodeId());
            psWireBalaceAmount.setString(5, terminalVO.getCardTypeId());
            rsWireBalnceAmount = psWireBalaceAmount.executeQuery();
            if (rsWireBalnceAmount.next())
            {
                wireAmountVO.setUnpaidBalanceAmount(rsWireBalnceAmount.getDouble("agentunpaidamount"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ::", e);
        }
        finally
        {
            Database.closeResultSet(rsWireBalnceAmount);
            Database.closePreparedStatement(psWireBalaceAmount);
            Database.closeConnection(con);
        }
    }

    public void setBankAgentWireUnpaidAmount(String agentId, GatewayAccount gatewayAccount, WireAmountVO wireAmountVO)
    {
        String strQuery = "SELECT SUM(agentunpaidamount) as 'agentunpaidamount'  FROM agent_wiremanager WHERE agentid=? AND toid=? AND accountid=? ";
        Connection con = null;
        ResultSet rsWireBalnceAmount=null;
        PreparedStatement psWireBalaceAmount=null;
        try
        {
            con = Database.getRDBConnection();
            psWireBalaceAmount = con.prepareStatement(strQuery);
            psWireBalaceAmount.setString(1, agentId);
            psWireBalaceAmount.setString(2, gatewayAccount.getMerchantId());
            psWireBalaceAmount.setInt(3, gatewayAccount.getAccountId());
            rsWireBalnceAmount = psWireBalaceAmount.executeQuery();
            if (rsWireBalnceAmount.next())
            {
                wireAmountVO.setUnpaidBalanceAmount(rsWireBalnceAmount.getDouble("agentunpaidamount"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ::", e);
        }
        finally
        {
            Database.closeResultSet(rsWireBalnceAmount);
            Database.closePreparedStatement(psWireBalaceAmount);
            Database.closeConnection(con);
        }
    }

    public void setBankPartnerWireUnpaidAmount(String partnerId, GatewayAccount gatewayAccount, WireAmountVO wireAmountVO)
    {
        String strQuery = "SELECT SUM(partnerunpaidamount) as 'unpaidamount'  FROM partner_wiremanager WHERE partnerid=? AND toid=? AND accountid=? ";
        Connection con = null;
        PreparedStatement psWireBalaceAmount=null;
        ResultSet rsWireBalnceAmount=null;
        try
        {
            con = Database.getRDBConnection();
            psWireBalaceAmount = con.prepareStatement(strQuery);
            psWireBalaceAmount.setString(1, partnerId);
            psWireBalaceAmount.setString(2, gatewayAccount.getMerchantId());
            psWireBalaceAmount.setInt(3, gatewayAccount.getAccountId());
            rsWireBalnceAmount = psWireBalaceAmount.executeQuery();
            if (rsWireBalnceAmount.next())
            {
                wireAmountVO.setUnpaidBalanceAmount(rsWireBalnceAmount.getDouble("unpaidamount"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ::", e);
        }
        finally
        {
            Database.closeResultSet(rsWireBalnceAmount);
            Database.closePreparedStatement(psWireBalaceAmount);
            Database.closeConnection(con);
        }
    }

    public void setPartnerWireUnpaidAmount(PartnerDetailsVO partnerDetailsVO, TerminalVO terminalVO, WireAmountVO wireAmountVO)
    {
        String strQuery = "SELECT SUM(partnerunpaidamount) as 'partnerunpaidamount'  FROM partner_wiremanager WHERE partnerid=? AND toid=? AND accountid=? AND paymodeid=? AND cardtypeid=?";
        Connection con = null;
        ResultSet rsWireBalnceAmount=null;
        PreparedStatement psWireBalaceAmount=null;
        try
        {
            con = Database.getRDBConnection();
            psWireBalaceAmount = con.prepareStatement(strQuery);
            psWireBalaceAmount.setString(1, partnerDetailsVO.getPartnerId());
            psWireBalaceAmount.setString(2, terminalVO.getMemberId());
            psWireBalaceAmount.setString(3, terminalVO.getAccountId());
            psWireBalaceAmount.setString(4, terminalVO.getPaymodeId());
            psWireBalaceAmount.setString(5, terminalVO.getCardTypeId());
            rsWireBalnceAmount = psWireBalaceAmount.executeQuery();
            if (rsWireBalnceAmount.next())
            {
                wireAmountVO.setUnpaidBalanceAmount(rsWireBalnceAmount.getDouble("partnerunpaidamount"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ::", e);
        }
        finally
        {
            Database.closeResultSet(rsWireBalnceAmount);
            Database.closePreparedStatement(psWireBalaceAmount);
            Database.closeConnection(con);
        }
    }

    public void setAgentWireAmountVO(AgentDetailsVO agentDetailsVO, TerminalVO terminalVO, WireAmountVO wireAmountVO)
    {
        Connection con = null;
        PreparedStatement psWireBalanceAmount=null;
        ResultSet rs = null;
        long wireCount = 1;

        try
        {
            con = Database.getRDBConnection();
            String strQuery = "SELECT COUNT(*) AS 'WireCount', SUM(agenttotalfundedamount) AS 'WireAmount',status FROM agent_wiremanager WHERE agentid=? AND toid=? AND accountid=? AND paymodeid=? AND cardtypeid=? Group by status";
            psWireBalanceAmount = con.prepareStatement(strQuery.toString());
            psWireBalanceAmount.setString(1, agentDetailsVO.getAgentId());
            psWireBalanceAmount.setString(2, terminalVO.getMemberId());
            psWireBalanceAmount.setString(3, terminalVO.getAccountId());
            psWireBalanceAmount.setString(4, terminalVO.getPaymodeId());
            psWireBalanceAmount.setString(5, terminalVO.getCardTypeId());
            rs = psWireBalanceAmount.executeQuery();
            while (rs.next())
            {
                if ("paid".equals(rs.getString("status")))
                {
                    wireAmountVO.setPaidAmount(rs.getDouble("WireAmount"));
                    wireCount = wireCount + rs.getLong("WireCount");
                }
                else if ("unpaid".equals(rs.getString("status")))
                {
                    wireAmountVO.setUnpaidAmount(rs.getDouble("WireAmount"));
                }
            }
            wireAmountVO.setWireCount(wireCount);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(psWireBalanceAmount);
            Database.closeConnection(con);
        }
    }

    public void setBankAgentWireAmountVO(String agentId, GatewayAccount gatewayAccount, WireAmountVO wireAmountVO)
    {
        Connection con = null;
        PreparedStatement psWireBalanceAmount=null;
        ResultSet rs = null;
        long wireCount = 1;

        try
        {
            con = Database.getRDBConnection();
            String strQuery = "SELECT COUNT(*) AS 'WireCount', SUM(agenttotalfundedamount) AS 'WireAmount',status FROM agent_wiremanager WHERE agentid=? AND toid=? AND accountid=? Group by status";
            psWireBalanceAmount = con.prepareStatement(strQuery.toString());
            psWireBalanceAmount.setString(1, agentId);
            psWireBalanceAmount.setString(2, gatewayAccount.getMerchantId());
            psWireBalanceAmount.setInt(3, gatewayAccount.getAccountId());
            rs = psWireBalanceAmount.executeQuery();
            while (rs.next())
            {
                if ("paid".equals(rs.getString("status")))
                {
                    wireAmountVO.setPaidAmount(rs.getDouble("WireAmount"));
                    wireCount = wireCount + rs.getLong("WireCount");
                }
                else if ("unpaid".equals(rs.getString("status")))
                {
                    wireAmountVO.setUnpaidAmount(rs.getDouble("WireAmount"));
                }
            }
            wireAmountVO.setWireCount(wireCount);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(psWireBalanceAmount);
            Database.closeConnection(con);
        }
    }

    public void setBankPartnerWireAmountVO(String partnerId, GatewayAccount gatewayAccount, WireAmountVO wireAmountVO)
    {
        Connection con = null;
        PreparedStatement psWireBalanceAmount=null;
        ResultSet rs = null;
        long wireCount = 1;
        try
        {
            con = Database.getRDBConnection();
            String strQuery = "SELECT COUNT(*) AS 'WireCount', SUM(partnertotalfundedamount) AS 'WireAmount',status FROM partner_wiremanager WHERE partnerid=? AND toid=? AND accountid=? Group by status";
            psWireBalanceAmount = con.prepareStatement(strQuery.toString());
            psWireBalanceAmount.setString(1, partnerId);
            psWireBalanceAmount.setString(2, gatewayAccount.getMerchantId());
            psWireBalanceAmount.setInt(3, gatewayAccount.getAccountId());
            rs = psWireBalanceAmount.executeQuery();
            while (rs.next())
            {
                if ("paid".equals(rs.getString("status")))
                {
                    wireAmountVO.setPaidAmount(rs.getDouble("WireAmount"));
                    wireCount = wireCount + rs.getLong("WireCount");
                }
                else if ("unpaid".equals(rs.getString("status")))
                {
                    wireAmountVO.setUnpaidAmount(rs.getDouble("WireAmount"));
                }
            }
            wireAmountVO.setWireCount(wireCount);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(psWireBalanceAmount);
            Database.closeConnection(con);
        }
    }

    public void setPartnerWireAmountVO(PartnerDetailsVO partnerDetailsVO, TerminalVO terminalVO, WireAmountVO wireAmountVO)
    {
        Connection con = null;
        PreparedStatement psWireBalanceAmount=null;
        ResultSet rs = null;
        long wireCount = 1;
        try
        {
            con = Database.getRDBConnection();
            String strQuery = "SELECT COUNT(*) AS 'WireCount', SUM(partnertotalfundedamount) AS 'WireAmount',status FROM partner_wiremanager WHERE partnerid=? AND toid=? AND accountid=? AND paymodeid=? AND cardtypeid=? Group by status";
            psWireBalanceAmount = con.prepareStatement(strQuery.toString());
            psWireBalanceAmount.setString(1, partnerDetailsVO.getPartnerId());
            psWireBalanceAmount.setString(2, terminalVO.getMemberId());
            psWireBalanceAmount.setString(3, terminalVO.getAccountId());
            psWireBalanceAmount.setString(4, terminalVO.getPaymodeId());
            psWireBalanceAmount.setString(5, terminalVO.getCardTypeId());
            rs = psWireBalanceAmount.executeQuery();
            while (rs.next())
            {
                if ("paid".equals(rs.getString("status")))
                {
                    wireAmountVO.setPaidAmount(rs.getDouble("WireAmount"));
                    wireCount = wireCount + rs.getLong("WireCount");
                }
                else if ("unpaid".equals(rs.getString("status")))
                {
                    wireAmountVO.setUnpaidAmount(rs.getDouble("WireAmount"));
                }
            }
            wireAmountVO.setWireCount(wireCount);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(psWireBalanceAmount);
            Database.closeConnection(con);
        }
    }

    public String createMerchantWire(MerchantWireVO merchantWireVO)
    {
        String status = null;
        Connection conn = null;
        PreparedStatement pstmt1=null;
        try
        {
            conn = Database.getConnection();
            String query1 = "INSERT INTO wiremanager (firstdate,lastdate,amount,balanceamount,netfinalamount,currency,status,settlementreportfilepath,settledtransactionfilepath,toid,terminalid,accountid,paymodeid,cardtypeid,isrollingreserveincluded,rollingreservereleasedateupto,settlementcycle_no,declinedcoverdateupto,reversedcoverdateupto,chargebackcoverdateupto) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt1 = conn.prepareStatement(query1);
            pstmt1.setString(1, merchantWireVO.getSettlementStartDate());
            pstmt1.setString(2, merchantWireVO.getSettlementEndDate());
            pstmt1.setDouble(3, merchantWireVO.getAmount());
            pstmt1.setDouble(4, merchantWireVO.getBalanceAmount());
            pstmt1.setDouble(5, merchantWireVO.getNetFinalAmount());
            pstmt1.setString(6, merchantWireVO.getCurrency());
            pstmt1.setString(7, merchantWireVO.getStatus());
            pstmt1.setString(8, merchantWireVO.getReportFileName());
            pstmt1.setString(9, merchantWireVO.getTransactionFileName());
            pstmt1.setString(10, merchantWireVO.getMemberId());
            pstmt1.setString(11, merchantWireVO.getTerminalId());
            pstmt1.setString(12, merchantWireVO.getAccountId());
            pstmt1.setString(13, merchantWireVO.getPayModeId());
            pstmt1.setString(14, merchantWireVO.getCardTypeId());
            pstmt1.setString(15, merchantWireVO.getRollingReserveIncluded());
            pstmt1.setString(16, merchantWireVO.getRollingReserveReleaseDateUpTo());
            pstmt1.setString(17, merchantWireVO.getSettlementCycleNO());
            pstmt1.setString(18, merchantWireVO.getDeclinedCoverDateUpTo());
            pstmt1.setString(19, merchantWireVO.getReversedCoverDateUpTo());
            pstmt1.setString(20, merchantWireVO.getChargebackCoverDateUpTo());

            int num = pstmt1.executeUpdate();
            if (num == 1)
            {
                logger.debug("New Wire added successfully.");
                status = "success";
            }
            else
            {
                logger.debug("Adding Wire Failed");
                status = "failure";
            }
        }
        catch (SystemError error)
        {
            status = "failure";
            logger.error(error);
        }
        catch (SQLException e)
        {
            status = "failure";
            logger.error(e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(conn);
        }
        return status;
    }

    public String createAgentWire(AgentWireVO agentWireVO) throws Exception
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        String status = "failed";
        try
        {
            conn = Database.getConnection();
            query = "INSERT INTO agent_wiremanager(settledid,settlementstartdate,settlementenddate,agentchargeamount,agentunpaidamount,agenttotalfundedamount,currency,STATUS,settlementreportfilename,markedfordeletion,agentid,toid,accountid,terminalid,paymodeid,cardtypeid,declinedcoverdateupto,reversedcoverdateupto,chargebackcoverdateupto,wirecreationtime,agenttype,settlementcycle_no)VALUES(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, agentWireVO.getSettlementStartDate());
            pstmt.setString(2, agentWireVO.getSettlementEndDate());
            pstmt.setDouble(3, agentWireVO.getAgentChargeAmount());
            pstmt.setDouble(4, agentWireVO.getAgentUnpaidAmount());
            pstmt.setDouble(5, agentWireVO.getAgentTotalFundedAmount());
            pstmt.setString(6, agentWireVO.getCurrency());
            pstmt.setString(7, agentWireVO.getStatus());
            pstmt.setString(8, agentWireVO.getSettlementReportFileName());
            pstmt.setString(9, agentWireVO.getMarkedForDeletion());
            pstmt.setString(10, agentWireVO.getAgentId());
            pstmt.setString(11, agentWireVO.getMemberId());
            pstmt.setString(12, agentWireVO.getAccountId());
            pstmt.setString(13, agentWireVO.getTerminalId());
            pstmt.setString(14, agentWireVO.getPayModeId());
            pstmt.setString(15, agentWireVO.getCardTypeId());
            pstmt.setString(16, agentWireVO.getDeclinedCoverDateUpTo());
            pstmt.setString(17, agentWireVO.getReversedCoverDateUpTo());
            pstmt.setString(18, agentWireVO.getChargebackCoverDateUpTo());
            pstmt.setString(19, agentWireVO.getAgentType());
            pstmt.setString(20, agentWireVO.getSettlementCycleNo());
            int k = pstmt.executeUpdate();
            if (k > 0)
            {
                status = "success";
            }
        }
        catch (SQLException e)
        {
            logger.error("Leaving AgentDAO throwing SQL Exception as System Error :::: ", e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        logger.debug("Leaving createAgentWire");

        return status;
    }

    public String createBankAgentWire(BankAgentWireVO agentBankWireVO)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        String status = "failed";
        try
        {
            conn = Database.getConnection();
            logger.debug("Entering into payout dao");
            query = "INSERT INTO agent_wiremanager(settledid,settlementstartdate,settlementenddate,agentchargeamount,agentunpaidamount,agenttotalfundedamount,currency,STATUS,settlementreportfilename,markedfordeletion,agentid,toid,accountid,terminalid,paymodeid,cardtypeid,declinedcoverdateupto,reversedcoverdateupto,chargebackcoverdateupto,wirecreationtime,agenttype)VALUES(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, agentBankWireVO.getSettlementStartDate());
            pstmt.setString(2, agentBankWireVO.getSettlementEndDate());
            pstmt.setDouble(3, agentBankWireVO.getAgentChargeAmount());
            pstmt.setDouble(4, agentBankWireVO.getAgentUnpaidAmount());
            pstmt.setDouble(5, agentBankWireVO.getAgentTotalFundedAmount());
            pstmt.setString(6, agentBankWireVO.getCurrency());
            pstmt.setString(7, agentBankWireVO.getStatus());
            pstmt.setString(8, agentBankWireVO.getSettlementReportFileName());
            pstmt.setString(9, agentBankWireVO.getMarkedForDeletion());
            pstmt.setString(10, agentBankWireVO.getAgentId());
            pstmt.setString(11, agentBankWireVO.getMid());
            pstmt.setString(12, agentBankWireVO.getAccountId());
            pstmt.setString(13, agentBankWireVO.getTerminalId());
            pstmt.setString(14, agentBankWireVO.getPayModeId());
            pstmt.setString(15, agentBankWireVO.getCardTypeId());
            pstmt.setString(16, agentBankWireVO.getDeclinedCoverDateUpTo());
            pstmt.setString(17, agentBankWireVO.getReversedCoverDateUpTo());
            pstmt.setString(18, agentBankWireVO.getChargebackCoverDateUpTo());
            pstmt.setString(19, agentBankWireVO.getAgentType());
            int k = pstmt.executeUpdate();
            if (k > 0)
            {
                status = "success";
            }

        }
        catch (SystemError systemError)
        {
            logger.error(systemError);
        }
        catch (SQLException e)
        {
            logger.error("Leaving PayoutDAO throwing SQL Exception as System Error :::: ", e);
        }
        catch (Exception rumtimeError)
        {
            logger.error(rumtimeError);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public String createBankPartnerWire(BankPartnerWireVO bankPartnerWireVO)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        String status = "failed";
        try
        {
            conn = Database.getConnection();
            query = "INSERT INTO partner_wiremanager(settledid,settlementstartdate,settlementenddate,partnerchargeamount,partnerunpaidamount,partnertotalfundedamount,currency,STATUS,settlementreportfilename,markedfordeletion,partnerid,toid,accountid,terminalid,paymodeid,cardtypeid,declinedcoverdateupto,reversedcoverdateupto,chargebackcoverdateupto,wirecreationtime,partnertype)VALUES(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, bankPartnerWireVO.getSettlementStartDate());
            pstmt.setString(2, bankPartnerWireVO.getSettlementEndDate());
            pstmt.setDouble(3, bankPartnerWireVO.getPartnerChargeAmount());
            pstmt.setDouble(4, bankPartnerWireVO.getPartnerUnpaidAmount());
            pstmt.setDouble(5, bankPartnerWireVO.getPartnerTotalFundedAmount());
            pstmt.setString(6, bankPartnerWireVO.getCurrency());
            pstmt.setString(7, bankPartnerWireVO.getStatus());
            pstmt.setString(8, bankPartnerWireVO.getSettlementReportFileName());
            pstmt.setString(9, bankPartnerWireVO.getMarkedForDeletion());
            pstmt.setString(10, bankPartnerWireVO.getPartnerId());
            pstmt.setString(11, bankPartnerWireVO.getMid());
            pstmt.setString(12, bankPartnerWireVO.getAccountId());
            pstmt.setString(13, bankPartnerWireVO.getTerminalId());
            pstmt.setString(14, bankPartnerWireVO.getPayModeId());
            pstmt.setString(15, bankPartnerWireVO.getCardTypeId());
            pstmt.setString(16, bankPartnerWireVO.getDeclinedCoverDateUpTo());
            pstmt.setString(17, bankPartnerWireVO.getReversedCoverDateUpTo());
            pstmt.setString(18, bankPartnerWireVO.getChargebackCoverDateUpTo());
            pstmt.setString(19, bankPartnerWireVO.getPartnerType());
            int k = pstmt.executeUpdate();
            if (k > 0)
            {
                status = "success";
            }

        }
        catch (SystemError systemError)
        {
            logger.error(systemError);
        }
        catch (SQLException e)
        {
            logger.error("Leaving PayoutDAO throwing SQL Exception as System Error :::: ", e);
        }
        catch (Exception rumtimeError)
        {
            logger.error(rumtimeError);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public String createPartnerWire(PartnerWireVO partnerWireVO) throws SystemError, SQLException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        String status = "failed";
        try
        {
            conn = Database.getConnection();
            query = "INSERT INTO partner_wiremanager(settledid,settlementstartdate,settlementenddate,partnerchargeamount,partnerunpaidamount,partnertotalfundedamount,currency,STATUS,settlementreportfilename,markedfordeletion,partnerid,toid,accountid,terminalid,paymodeid,cardtypeid,declinedcoverdateupto,reversedcoverdateupto,chargebackcoverdateupto,wirecreationtime,partnertype)VALUES(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, partnerWireVO.getSettlementStartDate());
            pstmt.setString(2, partnerWireVO.getSettlementEndDate());
            pstmt.setDouble(3, partnerWireVO.getPartnerChargeAmount());
            pstmt.setDouble(4, partnerWireVO.getPartnerUnpaidAmount());
            pstmt.setDouble(5, partnerWireVO.getPartnerTotalFundedAmount());
            pstmt.setString(6, partnerWireVO.getCurrency());
            pstmt.setString(7, partnerWireVO.getStatus());
            pstmt.setString(8, partnerWireVO.getSettlementReportFileName());
            pstmt.setString(9, partnerWireVO.getMarkedForDeletion());
            pstmt.setString(10, partnerWireVO.getPartnerId());
            pstmt.setString(11, partnerWireVO.getMemberId());
            pstmt.setString(12, partnerWireVO.getAccountId());
            pstmt.setString(13, partnerWireVO.getTerminalId());
            pstmt.setString(14, partnerWireVO.getPayModeId());
            pstmt.setString(15, partnerWireVO.getCardTypeId());
            pstmt.setString(16, partnerWireVO.getDeclinedCoverDateUpTo());
            pstmt.setString(17, partnerWireVO.getReversedCoverDateUpTo());
            pstmt.setString(18, partnerWireVO.getChargebackCoverDateUpTo());
            pstmt.setString(19, partnerWireVO.getPartnerType());

            int k = pstmt.executeUpdate();
            if (k > 0)
            {
                status = "success";
            }

        }
        catch (SQLException e)
        {
            logger.error("Leaving AgentDAO throwing SQL Exception as System Error :::: ", e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        logger.debug("Leaving createPartnerWire");

        return status;
    }

    public TransactionSummaryVO getGatewayAccountProcessedTrans(GatewayAccount gatewayAccount, String sTableName) throws PZDBViolationException
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getRDBConnection();
            String query = "select status,count(*) as 'count',sum(amount) as 'amount',sum(captureamount) as 'captureamount',sum(refundamount) as 'refundamount',sum(chargebackamount) as 'chargebackamount' from " + sTableName + " where accountid=? and fromid=? AND STATUS IN('authfailed','settled','reversed','chargeback')  group by status ";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setInt(1, gatewayAccount.getAccountId());
            preparedStatement.setString(2, gatewayAccount.getMerchantId());
            rsPayReport = preparedStatement.executeQuery();
            while (rsPayReport.next())
            {
                if ("authfailed".equals(rsPayReport.getString("status")))
                {
                    transactionSummaryVO.setAuthfailedAmount(rsPayReport.getDouble("amount"));
                    transactionSummaryVO.setCountOfAuthfailed(rsPayReport.getLong("count"));
                }
                if ("settled".equals(rsPayReport.getString("status")))
                {
                    transactionSummaryVO.setCountOfSettled(rsPayReport.getLong("count"));
                    transactionSummaryVO.setSettledAmount(rsPayReport.getDouble("captureamount"));
                }
                if ("reversed".equals(rsPayReport.getString("status")))
                {
                    transactionSummaryVO.setCountOfReversed(rsPayReport.getLong("count"));
                    transactionSummaryVO.setReversedAmount(rsPayReport.getDouble("refundamount"));
                }
                if ("chargeback".equals(rsPayReport.getString("status")))
                {
                    transactionSummaryVO.setCountOfChargeback(rsPayReport.getLong("count"));
                    transactionSummaryVO.setChargebackAmount(rsPayReport.getDouble("chargebackamount"));
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(PayoutDAO.class.getName(), "getGatewayAccountProcessedTrans", null, "Common", "SystemError While Getting TransactionSummary For GateAccount", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid", e);
            PZExceptionHandler.raiseDBViolationException(PayoutDAO.class.getName(), "getGatewayAccountProcessedTrans", null, "Common", "Sql Exception due to incorrect query to member_account_mapping", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionSummaryVO;

    }

    public boolean updateIsPartnerCommCronExecutedFlag(String accountId, String cycleId)
    {
        boolean status = false;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        try
        {
            con = Database.getConnection();
            String query = "update bank_wiremanager set isPartnerCommCronExecuted=? where accountid=? and bankwiremanagerid=?";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, "Y");
            preparedStatement.setString(2, accountId);
            preparedStatement.setString(3, cycleId);
            int k = preparedStatement.executeUpdate();
            if (k == 1)
            {
                status = true;
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException while updating isPartnerCommCronExecuted flag", se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError while updating isPartnerCommCronExecuted flag", e);
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return status;
    }

    public boolean updateIsAgentCommCronExecutedFlag(String accountId, String cycleId)
    {
        boolean status = false;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        try
        {
            con = Database.getConnection();
            String query = "update bank_wiremanager set isAgentCommCronExecuted=? where accountid=? and bankwiremanagerid=?";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, "Y");
            preparedStatement.setString(2, accountId);
            preparedStatement.setString(3, cycleId);
            int k = preparedStatement.executeUpdate();
            if (k == 1)
            {
                status = true;
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException while updating isAgentCommCronExecutedFlag", se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError while updating isAgentCommCronExecutedFlag ", e);
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return status;
    }

    public BankWireManagerVO getBankWireListForAgentCommissionCron(String bankWireId, String isSettlementCronExecuted, String isAgentCommissionCronExecuted,String isPayoutCronExcuted)
    {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        BankWireManagerVO bankWireManagerVO = null;
        List<BankWireManagerVO> bankWireManagerVOs = new ArrayList<BankWireManagerVO>();

        try
        {
            con = Database.getRDBConnection();
            String strQuery = "SELECT bankwiremanagerid,accountid,server_start_date,server_end_date,declinedcoveredupto,chargebackcoveredupto,reversedcoveredupto,declinedCoveredStartdate,chargebackCoveredStartdate,reversedCoveredStartdate,rollingreserveStartdate,rollingreservereleasedateupto FROM bank_wiremanager WHERE issettlementcronexceuted=? and isAgentCommCronExecuted=? and isPayoutCronExcuted=? and bankwiremanagerId=?";
            pstmt = con.prepareStatement(strQuery);
            pstmt.setString(1, isSettlementCronExecuted);
            pstmt.setString(2, isAgentCommissionCronExecuted);
            pstmt.setString(3, isPayoutCronExcuted);
            pstmt.setString(4, bankWireId);
            logger.error("Query:::::"+pstmt);
            rs = pstmt.executeQuery();
            if(rs.next())
            {
                bankWireManagerVO = new BankWireManagerVO();
                bankWireManagerVO.setBankwiremanagerId(rs.getString("bankwiremanagerid"));
                bankWireManagerVO.setAccountId(rs.getString("accountid"));
                bankWireManagerVO.setServer_start_date(rs.getString("server_start_date"));
                bankWireManagerVO.setServer_end_date(rs.getString("server_end_date"));
                bankWireManagerVO.setDeclinedcoveredupto(rs.getString("declinedcoveredupto"));
                bankWireManagerVO.setChargebackcoveredupto(rs.getString("chargebackcoveredupto"));
                bankWireManagerVO.setReversedCoveredUpto(rs.getString("reversedcoveredupto"));
                bankWireManagerVO.setDeclinedcoveredStartdate(rs.getString("declinedCoveredStartdate"));
                bankWireManagerVO.setChargebackcoveredStartdate(rs.getString("chargebackCoveredStartdate"));
                bankWireManagerVO.setReversedCoveredStartdate(rs.getString("reversedCoveredStartdate"));
                bankWireManagerVO.setRollingreservereleaseStartdate(rs.getString("rollingreserveStartdate"));
                bankWireManagerVO.setRollingreservereleasedateupto(rs.getString("rollingreservereleasedateupto"));
                bankWireManagerVOs.add(bankWireManagerVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception", e);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return bankWireManagerVO;
    }

    public SettlementDateVO getAgentCommissionReportStartDate(TerminalVO terminalVO)
    {
        String strQuery = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        Connection conn = null;
        SettlementDateVO settlementDateVO = null;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT settlementenddate,declinedcoverdateupto,reversedcoverdateupto,chargebackcoverdateupto FROM agent_wiremanager WHERE toid=? AND accountid=? and terminalid=? ORDER BY settledid DESC LIMIT 1";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getTerminalId());
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {

                settlementDateVO = new SettlementDateVO();
                Calendar cal = Calendar.getInstance();
                cal.setTime(targetFormat.parse(rsPayout.getString("settlementenddate")));
                cal.add(Calendar.SECOND, 1);
                settlementDateVO.setSettlementStartDate(targetFormat.format(cal.getTime()));

                cal.setTime(targetFormat.parse(rsPayout.getString("declinedcoverdateupto")));
                cal.add(Calendar.SECOND, 1);
                settlementDateVO.setDeclinedStartDate(targetFormat.format(cal.getTime()));

                cal.setTime(targetFormat.parse(rsPayout.getString("reversedcoverdateupto")));
                cal.add(Calendar.SECOND, 1);
                settlementDateVO.setReversedStartDate(targetFormat.format(cal.getTime()));

                cal.setTime(targetFormat.parse(rsPayout.getString("chargebackcoverdateupto")));
                cal.add(Calendar.SECOND, 1);
                settlementDateVO.setChargebackStartDate(targetFormat.format(cal.getTime()));

            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ", e);
        }
        catch (ParseException e)
        {
            logger.error("ParseException ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return settlementDateVO;
    }

    public SettlementDateVO getPartnerCommissionReportStartDate(TerminalVO terminalVO)
    {
        String strQuery = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        Connection conn = null;
        SettlementDateVO settlementDateVO = null;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            conn = Database.getRDBConnection();
            strQuery = "SELECT settlementenddate,declinedcoverdateupto,reversedcoverdateupto,chargebackcoverdateupto FROM partner_wiremanager WHERE toid=? AND accountid=? and terminalid=? ORDER BY settledid DESC LIMIT 1";
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getTerminalId());
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {

                settlementDateVO = new SettlementDateVO();
                Calendar cal = Calendar.getInstance();
                cal.setTime(targetFormat.parse(rsPayout.getString("settlementenddate")));
                cal.add(Calendar.SECOND, 1);
                settlementDateVO.setSettlementStartDate(targetFormat.format(cal.getTime()));

                cal.setTime(targetFormat.parse(rsPayout.getString("declinedcoverdateupto")));
                cal.add(Calendar.SECOND, 1);
                settlementDateVO.setDeclinedStartDate(targetFormat.format(cal.getTime()));

                cal.setTime(targetFormat.parse(rsPayout.getString("reversedcoverdateupto")));
                cal.add(Calendar.SECOND, 1);
                settlementDateVO.setReversedStartDate(targetFormat.format(cal.getTime()));

                cal.setTime(targetFormat.parse(rsPayout.getString("chargebackcoverdateupto")));
                cal.add(Calendar.SECOND, 1);
                settlementDateVO.setChargebackStartDate(targetFormat.format(cal.getTime()));

            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ", e);
        }
        catch (ParseException e)
        {
            logger.error("ParseException ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return settlementDateVO;
    }

    public long getMerchantPaidWireCountForCommissionCalculation(TerminalVO terminalVO, SettlementDateVO settlementDateVO)
    {
        long wireCount = 0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT COUNT(*) AS 'WireCount' FROM merchant_wiremanager WHERE toid=? AND accountid=? AND paymodeid=? AND cardtypeid=? AND status='Paid' AND settledate BETWEEN ? AND ? ";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getPaymodeId());
            preparedStatement.setString(4, terminalVO.getCardTypeId());
            preparedStatement.setString(5, settlementDateVO.getSettlementStartDate());
            preparedStatement.setString(6, settlementDateVO.getSettlementEndDate());
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                wireCount = rs.getLong("WireCount");
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
        return wireCount;
    }
    public double getMerchantTotalAmountFunded(TerminalVO terminalVO, SettlementDateVO settlementDateVO)
    {
        double netFinalAmount = 0.00;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT SUM(netfinalamount) AS netfinalamount FROM merchant_wiremanager WHERE toid=? AND accountid=? AND paymodeid=? AND cardtypeid=? AND firstdate>=? AND  lastdate<=? ";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getPaymodeId());
            preparedStatement.setString(4, terminalVO.getCardTypeId());
            preparedStatement.setString(5, settlementDateVO.getSettlementStartDate());
            preparedStatement.setString(6, settlementDateVO.getSettlementEndDate());
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                netFinalAmount = rs.getDouble("netfinalamount");
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
        return netFinalAmount;
    }

    public ChargeVO getMerchantWireCharge(TerminalVO terminalVO)
    {
        Connection con = null;
        PreparedStatement preparedStatement =null;
        ResultSet rs = null;
        ChargeVO chargeVO = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT mc.mappingid,mc.memberid,mc.accountid,mc.paymodeid,mc.cardtypeid,mc.chargeid,mc.chargevalue,mc.valuetype,mc.keyword,mc.sequencenum,mc.frequency,mc.category,mc.subkeyword,mc.terminalid,mc.agentchargevalue,mc.partnerchargevalue,cm.chargename FROM member_accounts_charges_mapping AS mc JOIN charge_master AS cm ON mc.chargeid=cm.chargeid AND memberid=? AND accountid=? AND paymodeid=? AND cardtypeid=? AND mc.keyword='Wire'";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getAccountId());
            preparedStatement.setString(3, terminalVO.getPaymodeId());
            preparedStatement.setString(4, terminalVO.getCardTypeId());
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                chargeVO=new ChargeVO();
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
        return chargeVO;
    }

    public boolean updateMerchantWireFile(String imageName,String settledId) throws SystemError
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        boolean status=false;
        try
        {
            conn = Database.getConnection();
            String query = "update merchant_wiremanager set wiretransfer_confirmation_image =? where settledid = ? ";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,imageName);
            pstmt.setString(2,settledId);
            int i = pstmt.executeUpdate();
            if(i>0)
            {
                status=true;
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError:::::",se);
            throw new SystemError("Error:" + se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Exception:::::",e);
            throw new SystemError("Error:" + e.getMessage());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public Hashtable getPayOutDetailsUpdate(String payoutid)
    {
        Hashtable payoutDetails1=new Hashtable();
        Connection connection=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            connection= Database.getRDBConnection();
            String qry="SELECT payout_id, settledid, DATE_FORMAT(payout_date,'%d/%m/%Y') payout_date, payout_currency, conversion_rate, payout_amount, beneficiary_bank_details, remitter_bank_details, remarks, swift_message,swift_upload ,DATE_FORMAT(payment_receipt_date,'%d/%m/%Y') payment_receipt_date,payment_receipt_confirmation FROM merchant_payout_details WHERE payout_id = ? ";
            pstmt=connection.prepareStatement(qry);
            pstmt.setString(1, payoutid);
            logger.debug("cc" + pstmt);
            payoutDetails1=Database.getHashFromResultSet(pstmt.executeQuery());

        }
        catch (SystemError systemError)
        {
            logger.error("Error while fetching data from merchant_payout_details",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL error while fetching data from merchant_payout_details", e);
        }
        finally {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return payoutDetails1;
    }


    public Hashtable getPayOutDetailsUpdatesettleid(String settledid)
    {
        Hashtable payoutDetails1=new Hashtable();
        Connection connection=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            connection= Database.getRDBConnection();
            String qry="SELECT payout_id, settledid, DATE_FORMAT(payout_date,'%d/%m/%Y') payout_date, payout_currency, conversion_rate, payout_amount, beneficiary_bank_details, remitter_bank_details, remarks, swift_message,swift_upload ,DATE_FORMAT(payment_receipt_date,'%d/%m/%Y') payment_receipt_date,payment_receipt_confirmation FROM merchant_payout_details WHERE settledid = ? ";
            pstmt=connection.prepareStatement(qry);
            pstmt.setString(1, settledid);
            logger.debug("cc" + pstmt);
            payoutDetails1=Database.getHashFromResultSet(pstmt.executeQuery());

        }
        catch (SystemError systemError)
        {
            logger.error("Error while fetching data from merchant_payout_details",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL error while fetching data from merchant_payout_details", e);
        }
        finally {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return payoutDetails1;
    }
    public TransactionSummaryVO getFraudDefenderCountAmountByDtstamp(String startDate, String endDate, TerminalVO terminalVO)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuilder strQry = new StringBuilder("SELECT COUNT(*) AS 'count',SUM(amount) AS 'amount' FROM (SELECT DISTINCT trackingId,amount FROM fraud_defender_details  WHERE toid=? AND accountid=? AND terminalId=? AND isRecordFound=? AND FROM_UNIXTIME(dtstamp) >=? AND FROM_UNIXTIME(dtstamp) <=?) AS fs");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getTerminalId()));
            preparedStatement.setString(4, "Y");
            preparedStatement.setString(5, startDate);
            preparedStatement.setString(6, endDate);
            logger.error("Query For FraudDefender::::"+preparedStatement);
            rsPayReport = preparedStatement.executeQuery();
            if (rsPayReport.next())
            {
                transactionSummaryVO.setTotalProcessingCount(rsPayReport.getLong("count"));
                transactionSummaryVO.setTotalProcessingAmount(rsPayReport.getLong("amount"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionSummaryVO;
    }
    public TransactionSummaryVO getDomesticTotalAmountByTerminalForEU(String startDate, String endDate, TerminalVO terminalVO)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuilder strQry = new StringBuilder("SELECT COUNT(*) AS 'count',SUM(tc.captureamount) AS 'amount' FROM bin_details AS bd JOIN transaction_common AS tc\n" +
                    "WHERE tc.trackingid=bd.icicitransid AND tc.toid=? AND tc.accountid=? AND tc.terminalid=? AND\n" +
                    "tc.STATUS IN('settled','reversed','chargeback','chargebackreversed') AND \n" +
                    "FROM_UNIXTIME(tc.dtstamp) >=? AND FROM_UNIXTIME(tc.dtstamp) <=? AND bd.bin_country_code_A3 \n" +
                    "IN('AUT','BEL','BGR','HRV','CYP','CZE','DNK','EST','FIN','FRA','DEU','GRC','HUN','IRL','ITA','LVA','LTU','LUX','MLT','NLD','POL','PRT','ROU','SVK','SVN','ESP','SWE')\n");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getTerminalId()));
            preparedStatement.setString(4, startDate);
            preparedStatement.setString(5, endDate);
            rsPayReport = preparedStatement.executeQuery();
            if (rsPayReport.next())
            {
                transactionSummaryVO.setTotalProcessingAmount(rsPayReport.getDouble("amount"));
                transactionSummaryVO.setTotalProcessingCount(rsPayReport.getLong("count"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionSummaryVO;
    }
    public TransactionSummaryVO getDomesticTotalAmountByTerminalForNonEU(String startDate, String endDate, TerminalVO terminalVO,String country)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuilder strQry = new StringBuilder("SELECT COUNT(*) AS 'count',SUM(tc.captureamount) AS 'amount' FROM bin_details AS bd JOIN transaction_common AS tc\n" +
                    "WHERE tc.trackingid=bd.icicitransid AND tc.toid=? AND tc.accountid=? AND tc.terminalid=? AND\n" +
                    "tc.STATUS IN('settled','reversed','chargeback','chargebackreversed') AND \n" +
                    "FROM_UNIXTIME(tc.dtstamp) >=? AND FROM_UNIXTIME(tc.dtstamp) <=? AND bd.bin_country_code_A3=?");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getTerminalId()));
            preparedStatement.setString(4, startDate);
            preparedStatement.setString(5, endDate);
            preparedStatement.setString(6, country);
            rsPayReport = preparedStatement.executeQuery();
            if (rsPayReport.next())
            {
                transactionSummaryVO.setTotalProcessingAmount(rsPayReport.getDouble("amount"));
                transactionSummaryVO.setTotalProcessingCount(rsPayReport.getLong("count"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionSummaryVO;
    }

    public TransactionSummaryVO getInternationalTotalAmountByTerminalForEU(String startDate, String endDate, TerminalVO terminalVO)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuilder strQry = new StringBuilder("SELECT COUNT(*) AS 'count',SUM(tc.captureamount) AS 'amount' FROM bin_details AS bd JOIN transaction_common AS tc\n" +
                    "WHERE tc.trackingid=bd.icicitransid AND tc.toid=? AND tc.accountid=? AND tc.terminalid=? AND\n" +
                    "tc.STATUS IN('settled','reversed','chargeback','chargebackreversed') AND \n" +
                    "FROM_UNIXTIME(tc.dtstamp) >=? AND FROM_UNIXTIME(tc.dtstamp) <=? AND bd.bin_country_code_A3 NOT\n" +
                    "IN('AUT','BEL','BGR','HRV','CYP','CZE','DNK','EST','FIN','FRA','DEU','GRC','HUN','IRL','ITA','LVA','LTU','LUX','MLT','NLD','POL','PRT','ROU','SVK','SVN','ESP','SWE')\n");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getTerminalId()));
            preparedStatement.setString(4, startDate);
            preparedStatement.setString(5, endDate);
            rsPayReport = preparedStatement.executeQuery();
            if (rsPayReport.next())
            {
                transactionSummaryVO.setTotalProcessingAmount(rsPayReport.getDouble("amount"));
                transactionSummaryVO.setTotalProcessingCount(rsPayReport.getLong("count"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionSummaryVO;
    }
    public TransactionSummaryVO getInternationalTotalAmountByTerminalForNonEU(String startDate, String endDate, TerminalVO terminalVO,String country)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuilder strQry = new StringBuilder("SELECT COUNT(*) AS 'count',SUM(tc.captureamount) AS 'amount' FROM bin_details AS bd JOIN transaction_common AS tc\n" +
                    "WHERE tc.trackingid=bd.icicitransid AND tc.toid=? AND tc.accountid=? AND tc.terminalid=? AND\n" +
                    "tc.STATUS IN('settled','reversed','chargeback','chargebackreversed') AND \n" +
                    "FROM_UNIXTIME(tc.dtstamp) >=? AND FROM_UNIXTIME(tc.dtstamp) <=? AND bd.bin_country_code_A3!=?");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getTerminalId()));
            preparedStatement.setString(4, startDate);
            preparedStatement.setString(5, endDate);
            preparedStatement.setString(6, country);
            rsPayReport = preparedStatement.executeQuery();
            if (rsPayReport.next())
            {
                transactionSummaryVO.setTotalProcessingAmount(rsPayReport.getDouble("amount"));
                transactionSummaryVO.setTotalProcessingCount(rsPayReport.getLong("count"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionSummaryVO;
    }

    public String getGatewayCountryFromAccountId(String accountId)
    {
        String country=null;
        Connection connection=null;
        ResultSet rs=null;
        PreparedStatement pstmt=null;
        try
        {
            connection=Database.getConnection();
            String query="SELECT country FROM gateway_type WHERE pgtypeid=(SELECT pgtypeid FROM gateway_accounts WHERE accountid=?)";
            pstmt=connection.prepareStatement(query);
            pstmt.setString(1,accountId);
            rs=pstmt.executeQuery();
            if(rs.next()){
               country=rs.getString("country");
            }
        }catch (Exception e){
            logger.error("Exception While fatch the country:::",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return country;
    }

    public static List<AgentCommissionVO> loadchargenameAgent()
    {
        AgentCommissionVO agentCommissionVO = null;
        List<AgentCommissionVO> chargeNameList = new ArrayList();
        Connection conn=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "Select chargeid, chargename from charge_master";
            pstmt = conn.prepareStatement(query);
            logger.debug("query loadcardtypeids----" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                AgentCommissionVO agentCommissionVO1 = new AgentCommissionVO();
                agentCommissionVO1.setChargeName(rs.getString("chargename"));
                agentCommissionVO1.setChargeId(rs.getString("chargeid"));
                chargeNameList.add(agentCommissionVO1);
            }
        }
        catch(Exception e)
        {
            logger.error("Exception while loading paymodeids",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return chargeNameList;
    }
    public String addBulkPayout(List<PZPayoutRequest> pzPayoutRequestList,String fileName,String memberId) throws PZDBViolationException
    {
        Connection connection               = null;
        PreparedStatement preparedStatement = null;
        Functions functions                 = new Functions();
        String discription                  = "";
        try
        {
            connection          = Database.getRDBConnection();
            String query        = "insert into bulk_payout_upload(merchantId,terminalId,bankname,bankaccount,ifsc,discription,amount,transferType,status,filename)values(?,?,?,?,?,?,?,?,?,?)";
            preparedStatement   = connection.prepareStatement(query);

            for (PZPayoutRequest pZPayoutRequest : pzPayoutRequestList)
            {
                   //String discription  = "PY-"+ Math.random()+"-"+memberId;
                    if(!functions.isValueNull(pZPayoutRequest.getOrderId())){
                        discription  = "PY-"+ getRandomnumber()+"-"+memberId;
                        pZPayoutRequest.setOrderId(discription);
                    }

                    preparedStatement.setInt(1, Integer.parseInt(memberId));
                    /*String []res        = pZPayoutRequest.getTerminalId().split("\\.");
                    String terminalId   = res[0];*/

                   if(!functions.isValueNull(pZPayoutRequest.getTerminalId())){
                        preparedStatement.setInt(2, 0);
                   }

                    if(functions.isValueNull(pZPayoutRequest.getCustomerBankAccountName())){
                        preparedStatement.setString(3, pZPayoutRequest.getCustomerBankAccountName());
                    }else{
                        preparedStatement.setString(3, "");
                    }

                    if(functions.isValueNull(pZPayoutRequest.getBankAccountNo())){
                        preparedStatement.setString(4, pZPayoutRequest.getBankAccountNo());
                    }else{
                        preparedStatement.setString(4, "");
                    }

                    if(functions.isValueNull(pZPayoutRequest.getBankIfsc())){
                        preparedStatement.setString(5, pZPayoutRequest.getBankIfsc());
                    }else{
                        preparedStatement.setString(5, "");
                    }

                    if(functions.isValueNull(pZPayoutRequest.getOrderId())){
                        preparedStatement.setString(6, pZPayoutRequest.getOrderId());
                    }else{
                        preparedStatement.setString(6, pZPayoutRequest.getOrderId());
                    }

                    if(functions.isValueNull(pZPayoutRequest.getPayoutAmount())){
                        preparedStatement.setDouble(7, Double.parseDouble(pZPayoutRequest.getPayoutAmount()));
                    }else{
                        preparedStatement.setDouble(7, 0.00);
                    }

                    if(functions.isValueNull(pZPayoutRequest.getBankTransferType())){
                        preparedStatement.setString(8, pZPayoutRequest.getBankTransferType());
                    }else{
                        preparedStatement.setString(8, "");
                    }
                    preparedStatement.setString(9, "UPLOADED");
                    preparedStatement.setString(10, fileName);
                logger.error("preparedStatement ---->>>>> "+preparedStatement);
                preparedStatement.executeUpdate();
                  //  preparedStatement.addBatch();

            }
           int k[] = preparedStatement.executeBatch();

          logger.error("No Of Record--->:"+pzPayoutRequestList.size()+" Uploaded Record--->:"+Arrays.toString(k));
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError ---->>>>> ",systemError);
            PZExceptionHandler.raiseDBViolationException("PayoutDAO.java", "addCustomerCardBatch()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException sql)
        {
            logger.error("SQLException ---->>>>> ",sql);
            PZExceptionHandler.raiseDBViolationException("PayoutDAO.java", "addCustomerCardBatch()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, sql.getMessage(), sql.getCause());
        }
        catch (Exception sql)
        {
            logger.error("Exception ---->>>>> ",sql);
            PZExceptionHandler.raiseDBViolationException("PayoutDAO.java", "addCustomerCardBatch()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, sql.getMessage(), sql.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return "PayOut Uploaded Successfully";
    }

    public List<PZPayoutRequest>  getPendingPayoutByMerchantId(String merchantId)
    {

        Connection connection                           = null;
        ResultSet resultSet                                    = null;
        PreparedStatement preparedStatement             = null;
        List<PZPayoutRequest> pzPayoutRequestList       = new ArrayList();
        Functions functions                             = new Functions();
        try
        {
            connection              = Database.getRDBConnection();
            String query            = "SELECT * FROM bulk_payout_upload WHERE status =? and merchantid =? ";
            preparedStatement       = connection.prepareStatement(query);
            preparedStatement.setString(1,"UPLOADED");
            preparedStatement.setString(2,merchantId);
            resultSet       = preparedStatement.executeQuery();

            while(resultSet.next()){
                PZPayoutRequest pzPayoutRequest = new PZPayoutRequest();

                pzPayoutRequest.setOrderId(resultSet.getInt("id")+"");
                pzPayoutRequest.setCustomerBankAccountName(resultSet.getString("bankname"));
                pzPayoutRequest.setBankAccountNo(resultSet.getString("bankaccount"));
                pzPayoutRequest.setBankIfsc(resultSet.getString("ifsc"));
                pzPayoutRequest.setOrderDescription(resultSet.getString("discription"));
                pzPayoutRequest.setPayoutAmount(resultSet.getDouble("amount") + "");
                pzPayoutRequest.setBankTransferType(resultSet.getString("transferType"));
                pzPayoutRequest.setMemberId(resultSet.getInt("merchantId"));
                pzPayoutRequest.setTerminalId(resultSet.getInt("terminalId") + "");
                pzPayoutRequest.setStatus(resultSet.getString("status"));


                if(functions.isValueNull(pzPayoutRequest.getPayoutAmount()) ){
                    try{
                        String payoutAmount =   String.format("%.2f", Double.parseDouble(pzPayoutRequest.getPayoutAmount()));
                        pzPayoutRequest.setPayoutAmount(payoutAmount);
                    }catch (NumberFormatException e){
                        logger.debug("BulkPayout Amount NumberFormatException---");
                    }
               }


                pzPayoutRequestList.add(pzPayoutRequest);
            }
        }catch (Exception e){
            logger.error("Exception While fatch the country:::",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return pzPayoutRequestList;
    }

    public int getRandomnumber(){

        Random rand = new Random();
        return rand.nextInt(1000);
    }

    public static List<WireVO> getMerchantWirelistBySettledid(String settledid)
    {
        Functions functions = new Functions();
        Connection connection = null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<WireVO> wireVOs = new ArrayList<WireVO>();

        try
        {
            connection = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT terminalId,settlementcycle_no,settledid FROM merchant_wiremanager WHERE settledid="+settledid+"");

            pstmt=connection.prepareStatement(query.toString());
            logger.debug("query=====from common==" + query.toString());
            rs=pstmt.executeQuery();

            if (rs.next())
            {
                WireVO wireVO = new WireVO();
                wireVO.setTerminalId(rs.getString("terminalId"));
                wireVO.setSettlementCycleNo(rs.getString("settlementcycle_no"));
                wireVO.setSettleId(rs.getString("settledid"));
                wireVOs.add(wireVO);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError----" + systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException---"+e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return wireVOs;
    }
    public String checkOrderUniquenessStatus(String toid, StringBuilder orderids )
    {
        logger.debug("checkorderuniqueness---");
        String str = "";
        Connection con = null;
        try
        {

            con = Database.getConnection();
            String transaction_table = "transaction_common";
            logger.debug("InputValidatorUtils.OrderUniqueness ::: DB Call" + transaction_table);
            String query2 = "select trackingid from transaction_common where toid = ? and description in("+orderids+") order by dtstamp desc";
            PreparedStatement pstmt1 = con.prepareStatement(query2);
            pstmt1.setString(1, toid);
            logger.error("order unique query---" + pstmt1);

            ResultSet rs1 = pstmt1.executeQuery();
            if (rs1.next())
            {
                str = "Duplicate OrderId Found in Upload List. Kindly try to place transaction with unique orderId.";
            }
        }
        catch (Exception e)
        {
            logger.error("Exception occur", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return str;
    }

    public String checkOrderUniqueness(String toid, String orderids )
    {
        logger.debug("check order uniqueness---");
        String str = "";
        Connection con = null;
        try
        {

            con = Database.getConnection();
            String transaction_table = "transaction_common";
            logger.debug("InputValidator Utils.OrderUniqueness ::: DB Call" + transaction_table);
            String query2 = "select trackingid from transaction_common where toid = ? and description= '"+orderids+"' order by dtstamp desc";
            PreparedStatement pstmt1 = con.prepareStatement(query2);
            pstmt1.setString(1, toid);
            logger.error("checkOrderUniqueness query---" + pstmt1);

            ResultSet rs1 = pstmt1.executeQuery();
            if (rs1.next())
            {
                str = "Transaction Already Exist With Ths Order.";
                logger.error("inside if payoutdao record found ---"+str);
            }
        }
        catch (Exception e)
        {
            logger.error("Exception occur", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return str;
    }

    public String checkOrderUniquenessInUploadList(String toid, String orderids )
    {
        logger.debug("check order uniqueness---");
        String str = "";
        Connection con = null;
        try
        {

            con = Database.getConnection();
            String transaction_table = "transaction_common";
            logger.debug("InputValidator Utils.OrderUniqueness ::: DB Call" + transaction_table);
            String query2 = "select discription from bulk_payout_upload where merchantid = ? and discription= '"+orderids+"' ";
            PreparedStatement pstmt1 = con.prepareStatement(query2);
            pstmt1.setString(1, toid);
            logger.error("checkOrderUniqueness query---" + pstmt1);

            ResultSet rs1 = pstmt1.executeQuery();
            if (rs1.next())
            {
                str = "Transaction Already Exist In Upload List. Orderid ";
                logger.error("inside if payoutdao record found ---"+str);
            }
        }
        catch (Exception e)
        {
            logger.error("Exception occur", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return str;
    }
    public List<BankWireManagerVO> getBankWireDetailsList (String parent_bankwireid,String issettlementcronexceuted, String ispayoutcronexcuted)
    {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        BankWireManagerVO bankWireManagerVO = null;
        List<BankWireManagerVO> voList = new ArrayList<>();
        try
        {
            con = Database.getRDBConnection();
            String strQuery = "SELECT bankwiremanagerid,settleddate,pgtypeid,accountid,MID,bank_start_date,bank_end_date,server_start_date,server_end_date,processing_amount,grossamount,netfinal_amount,unpaid_amount,isrollingreservereleasewire,rollingreservereleasedateupto,declinedcoveredupto,chargebackcoveredupto,reversedcoveredupto,banksettlement_report_file,banksettlement_transaction_file,issettlementcronexceuted,ispayoutcronexcuted,ispaid,declinedCoveredStartdate,chargebackCoveredStartdate,reversedCoveredStartdate,rollingreserveStartdate,parent_bankwireid FROM bank_wiremanager WHERE issettlementcronexceuted=? and  ispayoutcronexcuted=? and parent_bankwireid=? or bankwiremanagerid=?";
            pstmt = con.prepareStatement(strQuery);
            pstmt.setString(1, issettlementcronexceuted);
            pstmt.setString(2, ispayoutcronexcuted);
            pstmt.setString(3, parent_bankwireid);
            pstmt.setString(4, parent_bankwireid);
            rs = pstmt.executeQuery();
            logger.error("strQuery----------->"+pstmt);
            while (rs.next())
            {
                bankWireManagerVO = new BankWireManagerVO();
                bankWireManagerVO.setBankwiremanagerId(rs.getString("bankwiremanagerid"));
                bankWireManagerVO.setSettleddate(Functions.checkStringNull(rs.getString("settleddate")) == null ? "" : rs.getString("settleddate"));
                bankWireManagerVO.setPgtypeId(rs.getString("pgtypeid"));
                bankWireManagerVO.setAccountId(rs.getString("accountid"));
                bankWireManagerVO.setMid(rs.getString("MID"));
                bankWireManagerVO.setBank_start_date(rs.getString("bank_start_date"));
                bankWireManagerVO.setBank_end_date(rs.getString("bank_end_date"));
                bankWireManagerVO.setServer_start_date(rs.getString("server_start_date"));
                bankWireManagerVO.setServer_end_date(rs.getString("server_end_date"));
                bankWireManagerVO.setProcessing_amount(rs.getString("processing_amount"));
                bankWireManagerVO.setGrossAmount(rs.getString("grossamount"));
                bankWireManagerVO.setNetfinal_amount(rs.getString("netfinal_amount"));
                bankWireManagerVO.setUnpaid_amount(rs.getString("unpaid_amount"));
                bankWireManagerVO.setIsrollingreservereleasewire(rs.getString("isrollingreservereleasewire"));
                bankWireManagerVO.setRollingreservereleasedateupto(Functions.checkStringNull(rs.getString("rollingreservereleasedateupto")) == null ? "" : rs.getString("rollingreservereleasedateupto"));
                bankWireManagerVO.setDeclinedcoveredupto(rs.getString("declinedcoveredupto"));
                bankWireManagerVO.setChargebackcoveredupto(rs.getString("chargebackcoveredupto"));
                bankWireManagerVO.setReversedCoveredUpto(rs.getString("reversedcoveredupto"));
                bankWireManagerVO.setBanksettlement_report_file(rs.getString("banksettlement_report_file"));
                bankWireManagerVO.setBanksettlement_transaction_file(rs.getString("banksettlement_transaction_file"));
                bankWireManagerVO.setSettlementCronExceuted(rs.getString("issettlementcronexceuted"));
                bankWireManagerVO.setPayoutCronExcuted(rs.getString("ispayoutcronexcuted"));
                bankWireManagerVO.setIspaid(rs.getString("ispaid"));

                bankWireManagerVO.setDeclinedcoveredStartdate(Functions.checkStringNull(rs.getString("declinedCoveredStartdate")) == null ? "0000-00-00 00:00:00" : rs.getString("declinedCoveredStartdate"));
                bankWireManagerVO.setChargebackcoveredStartdate(Functions.checkStringNull(rs.getString("chargebackCoveredStartdate")) == null ? "0000-00-00 00:00:00" : rs.getString("chargebackCoveredStartdate"));
                bankWireManagerVO.setReversedCoveredStartdate(Functions.checkStringNull(rs.getString("reversedCoveredStartdate")) == null ? "0000-00-00 00:00:00" : rs.getString("reversedCoveredStartdate"));
                bankWireManagerVO.setRollingreservereleaseStartdate(Functions.checkStringNull(rs.getString("rollingreserveStartdate")) == null ? "0000-00-00 00:00:00" : rs.getString("rollingreserveStartdate"));
                bankWireManagerVO.setParent_bankwireid(Functions.checkStringNull(rs.getString("parent_bankwireid")) == null ? "" : rs.getString("parent_bankwireid"));
                voList.add(bankWireManagerVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception", e);
        }
        catch(SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return voList;
    }

    public Hashtable getPayoutBatchALlDetails(String fdtstamp,String tdtstamp)
    {
        System.out.println("inside getPayoutBatch");
        Hashtable hash = new Hashtable();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            connection = Database.getRDBConnection();
            String query = "select sysBatchid,sysBatchdate,sysCount,sysAmount,bankAmount,walletName,bankBatchid,bankCount,sysStatus,bankStatus,bankDate from payout_batch_details WHERE dtstamp >= ? AND dtstamp <= ? ORDER BY sysBatchid DESC ";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,fdtstamp);
            pstmt.setString(2,tdtstamp);
            rs=pstmt.executeQuery();
            logger.debug("qury for getPayoutBatchALlDetails---" + pstmt);
            hash = getHashFromResultSet(rs);

        }
        catch (SystemError systemError)
        {
            logger.error("Error while fetching data from ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL error while fetching data from ", e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hash;
    }

    public boolean updateSystemBatchId( String sysBatchid,String podbatch,String trackingid)
    {
        Connection connection = null;
        ResultSet rs = null;
        boolean result= false;
        try
        {
            connection = Database.getConnection();
            String updateQuery1 = "UPDATE transaction_common SET pod=? ,customerId=? WHERE trackingid IN (" + trackingid + ")";
            PreparedStatement ps2 = connection.prepareStatement(updateQuery1);
            ps2.setString(1, sysBatchid);
            ps2.setString(2, podbatch);
//            ps2.setString(3, trackingid);
            logger.error("updateSystemBatchId queryy...."+ps2);

            int a = ps2.executeUpdate();
            if(a>0)
            {
                result=true;
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException updateSystemBatchId---", se);
        }
        catch (SystemError s)
        {
            logger.error("SystemError updateSystemBatchId---", s);
        }
        finally
        {
            if (connection != null)
                Database.closeConnection(connection);
        }

        return result;
    }


    public int deleteSystemBatchId(String sysBatchid)
    {
        System.out.println("inside deleteSystemBatchId");
        int result = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try
        {
            String query = "Delete from payout_batch_details where sysBatchid =? and sysStatus='downloaded' ";

            conn = Database.getConnection();
            pstmt = conn.prepareStatement(query);
            logger.error("queryy for deleteSystemBatchId--"+pstmt);
            pstmt.setString(1, sysBatchid);
            result = pstmt.executeUpdate();
        }
        catch (Exception e)
        {
            logger.error("Exception:::::" + e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return result;
    }


    public List<TransactionDetailsVO> getPayoutDetailsExcel( String fdtstamp, String tdtstamp,String walletname,String toid, String accountid )
    {
        System.out.println("inside getPayoutDetailsExcel");
        Connection connection   = null;
        PreparedStatement pstmt = null;
        Functions functions    = new Functions();
        TransactionDetailsVO transactionDetailsVO ;
        List<TransactionDetailsVO> list= new ArrayList();

        try
        {
            connection = Database.getRDBConnection();
            StringBuffer queryString  = new StringBuffer("select tc.toid, tc.status, tc.telno, tc.amount, tc.trackingid, tpd.walletname, tpd.bankaccount from transaction_common as tc , transaction_payout_details as tpd  WHERE tc.trackingid=tpd.trackingid and (tc.pod='' OR tc.pod IS NULL) and tc.status='payoutstarted'" );
            if(functions.isValueNull(toid)){
                queryString.append(" and tc.toid='"+toid+"'");
            }
            if(functions.isValueNull(accountid)){
                queryString.append(" and tc.accountid ='"+accountid+"'");
            }
            if(functions.isValueNull(walletname)){
                queryString.append(" and tpd.walletname ='"+walletname+"'");
            }
            if(functions.isValueNull(fdtstamp))
            {
                queryString.append(" and tc.dtstamp >= " + fdtstamp);
            }

            if (functions.isValueNull(tdtstamp))
            {
                queryString.append(" and tc.dtstamp <= " + tdtstamp);
            }

            pstmt = connection.prepareStatement(queryString.toString());
            logger.error("querrry for excel "+queryString);

            ResultSet rs = pstmt.executeQuery();
            logger.error("querrry for ResultSet "+rs);

            while (rs.next())
            {
                logger.error("querrry inside while loop "+rs.getString("trackingid"));

                transactionDetailsVO = new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(rs.getString("trackingid"));

                transactionDetailsVO.setTelno(rs.getString("bankaccount"));
                transactionDetailsVO.setAmount(rs.getString("amount"));
                transactionDetailsVO.setName(rs.getString("walletname"));
                transactionDetailsVO.setStatus(rs.getString("status"));
                list.add(transactionDetailsVO);
            }

        }
        catch (SystemError systemError)
        {
            logger.error("Error while fetching data from ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL error while fetching data from ", e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return list;
    }
    public int getLastBatchId()
    {
        System.out.println("inside getLastBatchId");
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int result=0;
        try
        {
            connection = Database.getRDBConnection();
//            String query = " select id from transaction_payout_details  ORDER BY id DESC limit 0,1 ";
            String query = " select id from payout_batch_details  ORDER BY id DESC limit 0,1 ";
            pstmt = connection.prepareStatement(query);

            logger.debug("qury for getLastBatchId---" + pstmt);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                result=rs.getInt("id");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Error while fetching data from ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL error while fetching data from ", e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return result;
    }

    //batchId,sysCount,sysAmount,sysStatus,naration,filePath,bankBatchid,bankCount,bankStatus,actionExecutorId,actionExecutorName
    public String insertIntoPayoutBatch(String sysBatchid,int sysCount,String sysAmount, String sysStatus,String walletName,String filePath,String bankBatchid,int bankCount, String bankStatus,String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String result = "";
        String query = null;
        try
        {
            conn = Database.getConnection();
            query = "insert into payout_batch_details (sysBatchid,sysCount,sysAmount,sysStatus,walletName,filePath,bankBatchid,bankCount,bankStatus,actionExecutorId,actionExecutorName,dtstamp) values(?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()))";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, sysBatchid);
            pstmt.setInt(2, sysCount);
            pstmt.setString(3, sysAmount);
            pstmt.setString(4, sysStatus);
            pstmt.setString(5, walletName);
            pstmt.setString(6, filePath);
            pstmt.setString(7, bankBatchid);
            pstmt.setInt(8, bankCount);
            pstmt.setString(9, bankStatus);
            pstmt.setString(10, actionExecutorId);
            pstmt.setString(11, actionExecutorName);
            logger.error("querrry for insertIntoPayoutBatch---" + pstmt);

            int k = pstmt.executeUpdate();
            if (k > 0)
            {
                result = "success";
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PartnerDAO.java", "addNewDefaultTheme()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
            logger.error("sql SystemError ---",se);

        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PartnerDAO.java", "addNewDefaultTheme()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
            logger.error("sql SQLException ---",e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return result;
    }




    public boolean updateWalletNameAndBatchIdForDelete( String sysBatchid,String customerId,String status)
    {
        Connection connection = null;
        boolean result= false;
        try
        {
            connection = Database.getConnection();
            String updateQuery1 = "UPDATE transaction_common SET pod='' ,podbatch='',status=? WHERE pod=? and customerId=?";
            PreparedStatement ps2 = connection.prepareStatement(updateQuery1);
            ps2.setString(1, status);
            ps2.setString(2, sysBatchid);
            ps2.setString(3, customerId);
            logger.error("updateWalletNameAndBatchIdForDelete queryy...."+ps2);

            int a = ps2.executeUpdate();
            if(a>0)
            {
                result=true;
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException updateSystemBatchId---", se);
        }
        catch (SystemError s)
        {
            logger.error("SystemError updateSystemBatchId---", s);
        }
        finally
        {
            if (connection != null)
                Database.closeConnection(connection);
        }
        return result;
    }


    public boolean bdPayoutDetailsForUpdate(String  bankBatchid ,int bankCount, String bankStatus, String sysBatchid, String bankAmount)
    {
        Connection conn=null;
        PreparedStatement pstmt=null;
        boolean result=false;
        Functions functions = new Functions();
        String timeStamp= functions.getTimestamp();
//        System.out.println("timestamp=="+timeStamp);
        try
        {
            conn = Database.getConnection();
            String query1 = "update payout_batch_details set bankBatchid=? ,bankCount=? ,bankStatus=? , bankDate=? ,bankAmount=?,sysStatus='uploaded' where sysBatchid=? ";
            pstmt = conn.prepareStatement(query1);
            pstmt.setString(1, bankBatchid);
            pstmt.setInt(2, bankCount);
            pstmt.setString(3, bankStatus);
            pstmt.setString(4, timeStamp);
            pstmt.setString(5, bankAmount);
            pstmt.setString(6, sysBatchid);

            logger.error("pstmt Update bdPayoutDetailsForUpdate------->"+pstmt);
            int k=pstmt.executeUpdate();
            if(k>0)
            {
                result=true;
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
        return result;
    }


    public boolean updateTransactionCommonBankBatchId( String sysBatchid, String bankBatchId, String status)
    {
        Connection connection = null;
        ResultSet rs = null;
        boolean result= false;
        try
        {
            connection = Database.getConnection();
            String updateQuery1 = "UPDATE transaction_common SET podbatch=?,status=? WHERE pod=? ";
            PreparedStatement ps2 = connection.prepareStatement(updateQuery1);
            ps2.setString(1, bankBatchId);
            ps2.setString(2, status);
            ps2.setString(3, sysBatchid);
            logger.error("updateTransactionCommonBankBatchId queryy...."+ps2);

            int a = ps2.executeUpdate();
            if(a>0)
            {
                result=true;
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException updateTransactionCommonBankBatchId---", se);
        }
        catch (SystemError s)
        {
            logger.error("SystemError updateTransactionCommonBankBatchId---", s);
        }
        finally
        {
            if (connection != null)
                Database.closeConnection(connection);
        }

        return result;
    }


    public static Hashtable getHashFromResultSet(ResultSet rs) throws SQLException
    {
        int j = 0;
        if (rs == null)
            throw new SQLException("Empty ResultSet in getHashFromResultSet as parameter");

        Hashtable outerHash = new Hashtable();
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int count = rsMetaData.getColumnCount();

        int i = 0;
        while (rs.next())
        {
            Hashtable innerHash = new Hashtable();
            for (i = 1; i <= count; i++)
            {
                if (rs.getString(i) != null)
                {
                        if(rsMetaData.getColumnLabel(i).equalsIgnoreCase("bankDate"))
                        {
                            innerHash.put(rsMetaData.getColumnLabel(i), rs.getTimestamp(i)+"");

                        }
                    else{
                            innerHash.put(rsMetaData.getColumnLabel(i), rs.getString(i));

                        }

                }
                //cat.error("refund "+rsMetaData.getColumnLabel(i)+"="+ rs.getString(i));
            }
            j++;
            outerHash.put("" + j, innerHash);
        }
        return outerHash;
    }

    public ArrayList<TransactionDetailsVO> getTransactionCommonBySysBatchId( String sysBatchid)
    {
        Connection connection = null;
        boolean result          = false;
        ArrayList<TransactionDetailsVO> arrayList =  new ArrayList();
        try
        {
            connection = Database.getConnection();
            String queryString = "select trackingId,amount,currency from transaction_common  WHERE pod=? ";
            PreparedStatement ps2 = connection.prepareStatement(queryString);
            ps2.setString(1, sysBatchid);
            logger.error("getTransactionCommonBySysBatchId "+ps2);

            ResultSet resultSet  = ps2.executeQuery();
            while (resultSet.next())
            {
                TransactionDetailsVO transactionDetailsVO = new TransactionDetailsVO();

                transactionDetailsVO.setTrackingid(resultSet.getString("trackingId"));
                transactionDetailsVO.setAmount(resultSet.getString("amount"));
                transactionDetailsVO.setCurrency(resultSet.getString("currency"));

                arrayList.add(transactionDetailsVO);

            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException getTransactionCommonBySysBatchId---", se);
        }
        catch (SystemError s)
        {
            logger.error("SystemError getTransactionCommonBySysBatchId---", s);
        }
        finally
        {
            if (connection != null)
                Database.closeConnection(connection);
        }

        return arrayList;
    }

    public void  updateTransactionCommonAndDetails(ArrayList<TransactionDetailsVO> transactionDetailsLit,AuditTrailVO auditTrailVO,String status,String action){
        StringBuffer insertQuery = new StringBuffer();
        Connection connection                = null;
        PreparedStatement insetStatement     = null;

        try{
            connection       = Database.getConnection();
            connection.setAutoCommit(false);

            insertQuery.append("insert into transaction_common_details(trackingid,amount,status,action,actionexecutorid,actionexecutorname,currency,templatecurrency,templateamount) values (?,?,?,?,?,?,?,?,?)");

            insetStatement = connection.prepareStatement(insertQuery.toString());

            for (TransactionDetailsVO transactionDetailsVO : transactionDetailsLit)
            {

                insetStatement.setString(1,transactionDetailsVO.getTrackingid());
                insetStatement.setString(2,transactionDetailsVO.getAmount());
                insetStatement.setString(3,status);
                insetStatement.setString(4,action);
                insetStatement.setString(5,auditTrailVO.getActionExecutorId());
                insetStatement.setString(6,auditTrailVO.getActionExecutorName());
                insetStatement.setString(7,transactionDetailsVO.getCurrency());
                insetStatement.setString(8,transactionDetailsVO.getCurrency());
                insetStatement.setString(9,transactionDetailsVO.getAmount());

                insetStatement.addBatch();

            }

            int i[] = insetStatement.executeBatch();
            connection.commit();

            logger.error("No Of Record--->: "+transactionDetailsLit.size()+", Inserted Record--->: "+Arrays.toString(i) );

        }catch (Exception e){
            logger.error("updateTransactionCommonAndDetails +++++++"+e);
        }finally
        {
            Database.closePreparedStatement(insetStatement);
            Database.closeConnection(connection);
        }
    }

    public TransactionSummaryVO getProcessingAllCountAmountDetails(String payoutStartDate,String payoutEndDate,String settledTransStartDate, String settledTransEndDate, TerminalVO terminalVO, String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        logger.error("settledTransStartDate+++   "+settledTransStartDate);
        logger.error("settledTransEndDate+++   "+settledTransEndDate);

        if (settledTransStartDate.endsWith(".0") || settledTransStartDate.contains(".0")){
            settledTransStartDate = settledTransStartDate.substring(0, settledTransStartDate.indexOf('.')); }
        String datetimeArray[] = settledTransStartDate.split(" ");
        String mm = String.valueOf(Integer.parseInt(datetimeArray[0].split("-")[1]) - 1);
        String startdtstmp = Functions.converttomillisec(mm,datetimeArray[0].split("-")[2],datetimeArray[0].split("-")[0],datetimeArray[1].split(":")[0],datetimeArray[1].split(":")[1],datetimeArray[1].split(":")[2]);
        logger.error("mm+++ "+mm+" ======= startdtstmp+++ "+startdtstmp);

        if (settledTransEndDate.endsWith(".0") || settledTransEndDate.contains(".0")){
            settledTransEndDate=settledTransEndDate.substring(0, settledTransEndDate.indexOf('.')); }
        String datetimeArray1[] = settledTransEndDate.split(" ");
        String mm1 = String.valueOf(Integer.parseInt(datetimeArray1[0].split("-")[1]) - 1);
        String enddtstmp = Functions.converttomillisec(mm1,datetimeArray1[0].split("-")[2],datetimeArray1[0].split("-")[0],datetimeArray1[1].split(":")[0],datetimeArray1[1].split(":")[1],datetimeArray1[1].split(":")[2]);
        logger.error("mm1+++ "+mm1+" ======= enddtstmp+++ "+enddtstmp);

        try
        {
            con = Database.getConnection();
//            StringBuilder strQuery = new StringBuilder("SELECT COUNT(CASE WHEN STATUS  IN('authfailed') THEN 1 END) AS authfailcount, SUM(CASE WHEN STATUS  IN('authfailed') THEN amount ELSE 0 END ) AS Amount,");
            StringBuilder strQuery = new StringBuilder("SELECT COUNT(CASE WHEN STATUS  IN('markedforreversal' ,'chargeback','chargebackreversed','capturesuccess' ,'settled','reversed') THEN 1 END) AS successcount, SUM(CASE WHEN STATUS IN ('markedforreversal' ,'chargeback','chargebackreversed','capturesuccess' ,'settled','reversed') THEN captureamount ELSE 0 END) AS Captureamount");
//            strQuery.append("COUNT(CASE WHEN STATUS  IN('payoutsuccessful') THEN 1 END) AS payoutcount, SUM(CASE WHEN STATUS IN ('payoutsuccessful') AND  dtstamp >=? and dtstamp <=? THEN payoutamount ELSE 0 END) AS payoutamount");
            strQuery.append(" FROM " + sTableName + " WHERE STATUS IN ('settled','reversed','chargeback','chargebackreversed','payoutsuccessful','authfailed') AND toid=? AND accountid=? AND terminalid=? AND  dtstamp >=? and dtstamp <=?");
            preparedStatement= con.prepareStatement(strQuery.toString());
            preparedStatement.setInt(1, Integer.parseInt(terminalVO.getMemberId()));
            preparedStatement.setInt(2, Integer.parseInt(terminalVO.getAccountId()));
            preparedStatement.setInt(3, Integer.parseInt(terminalVO.getTerminalId()));
            preparedStatement.setString(4, startdtstmp);
            preparedStatement.setString(5, enddtstmp);
            logger.error("getProcessingAllCountAmountDetails +++  "+preparedStatement);
            Date date5= new Date();
            logger.error("getProcessingAllCountAmountDetails  query starts######## "+date5.getTime());
            rsPayReport= preparedStatement.executeQuery();
            logger.error("getProcessingAllCountAmountDetails  query ends######## "+new Date().getTime());
            logger.error("getProcessingAllCountAmountDetails query difference######### "+(new Date().getTime()-date5.getTime()));

            if (rsPayReport.next())
            {
//                transactionSummaryVO.setAuthfailedAmount(rsPayReport.getDouble("Amount"));
//                transactionSummaryVO.setCountOfAuthfailed(rsPayReport.getLong("authfailcount"));

                transactionSummaryVO.setTotalProcessingAmount(rsPayReport.getDouble("Captureamount"));
                transactionSummaryVO.setTotalProcessingCount(rsPayReport.getLong("successcount"));
/*
                transactionSummaryVO.setPayoutAmount(rsPayReport.getDouble("payoutamount"));
                transactionSummaryVO.setCountOfPayout(rsPayReport.getLong("payoutcount"));*/
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (Exception e)
        {
            logger.error("SQL Exception::", e);
        }
        finally
        {
            Database.closeResultSet(rsPayReport);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return transactionSummaryVO;
    }

}