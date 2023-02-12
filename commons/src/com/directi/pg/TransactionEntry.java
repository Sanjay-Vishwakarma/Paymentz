package com.directi.pg;

import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayTypeService;
import com.directi.pg.core.paymentgateway.SBMPaymentGateway;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.logicboxes.util.ApplicationProperties;
import com.manager.vo.InputDateVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.TransactionDetailsVO;
import com.manager.vo.payoutVOs.WireVO;
import com.payment.b4payment.vos.Result;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.math.BigDecimal;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
public class TransactionEntry
{
    public static final String CREDIT = "credit";
    public static final String REVERSAL = "reversal";
    public static final String WITHDRAWAL = "withdrawal";
    public static final String CHARGEBACK = "chargeback";
    public static final String AUTHFAIL = "authfail";
    public static final String INTERNAL_TRANSFER = "internaltransfer";
    public static final String CREDIT_CHARGES = CREDIT + "charges";
    public static final String REVERSAL_CHARGES = "chargesfor" + REVERSAL;
    public static final String WITHDRAWAL_CHARGES = WITHDRAWAL + "charges";
    public static final String CHARGEBACK_CHARGES = CHARGEBACK + "charges";
    public static final String AUTHFAILED_CHARGES = AUTHFAIL + "charges";
    public static final String OTHER_CHARGES = "chargebackcharges";
    public static final String TAX_CREDIT_CHARGES = "taxon" + CREDIT_CHARGES;
    public static final String TAX_REVERSAL_CHARGES = "taxon" + REVERSAL_CHARGES;
    public static final String TAX_WITHDRAWAL_CHARGES = "taxon" + WITHDRAWAL_CHARGES;
    public static final String TAX_CHARGEBACK_CHARGES = "taxon" + CHARGEBACK_CHARGES;
    public static final String REVERSAL_OF_CREDIT_CHARGES = "reversalofcharges";// means reversal of "credit" charges
    public static final String REVERSAL_OF_TAX_CREDIT_CHARGES = "reversaloftaxoncharges";// means reversal of tax on "credit" charges
    public static final String REVERSAL_OF_CHARGEBACK = "chargebackreversal";
    public static final String REFUND_OF_REVERSAL_CHARGES = "refundofchargesforreversal";// means cancellation of "REVERSAL" charges
    public static final String REFUND_OF_TAX_REVERSAL_CHARGES = "refundoftaxonchargesforreversal";// means cancellation of tax on "REVERSAL" charges
    public static final String CANCELLATION_OF_REVERSAL = "cancellationof" + REVERSAL ;    // means cancellation of "REVERSAL"
    private static final String TYPE_PZ = "pz";
    private static final String TYPE_ICICICREDIT = "icicicredit";
    private static final String TYPE_WITHDRAWAL = "withdrawal";
    //  static Hashtable statushash = new Hashtable();
    static SortedMap sortedMap = new TreeMap();
    static Hashtable globalhash = new Hashtable();
    private static Logger logger = new Logger(TransactionEntry.class.getName());
    private static Functions functions = new Functions();
    boolean isLogEnabled = Boolean.parseBoolean(ApplicationProperties.getProperty("IS_LOG_ENABLED"));
    AuditTrailVO auditTrailVO=new AuditTrailVO();
    static
    {
        sortedMap.put("begun", "Begun Processing");
        sortedMap.put("authstarted", "Auth Started");
        sortedMap.put("payoutstarted", "Payout Started");
        sortedMap.put("proofrequired", "Proof Required");
        sortedMap.put("authsuccessful", "Auth Successful");
        sortedMap.put("payoutsuccessful", "Payout Successful");
        sortedMap.put("authfailed", "Auth Failed");
        sortedMap.put("payoutfailed", "Payout Failed");
        sortedMap.put("capturestarted", "Capture Started");
        sortedMap.put("capturesuccess", "Capture Successful");
        sortedMap.put("capturefailed", "Capture Failed");
        sortedMap.put("podsent", "POD Sent");
        sortedMap.put("settled", "Settled");
        sortedMap.put("reversed", "Reversed");
        sortedMap.put("chargeback", "Chargeback");
        sortedMap.put("chargebackreversed", "Chargeback Reversed");
        sortedMap.put("failed", "Failed");
        sortedMap.put("cancelstarted","Cancel Initiated");
        sortedMap.put("cancelled", "Cancelled Transactions");
        sortedMap.put("authcancelled", "Authorisation Cancelled");
        sortedMap.put("markedforreversal","Reversal Request Sent");
        sortedMap.put("payoutsuccessful","Payout Successful");
        sortedMap.put("payoutstarted","Payout Started");
        sortedMap.put("partialrefund","Partially Reversed");
        sortedMap.put("payoutcancelstarted", "Payout Cancel Started");
        sortedMap.put("payoutcancelsuccessful", "Payout Cancel Successful");
        sortedMap.put("payoutcancelfailed", "Payout Cancel Failed");
        sortedMap.put("authstarted_3D", "Auth Started 3D");
        sortedMap.put("smsstarted", "SMS Started");
        sortedMap.put("enrollmentstarted", "Enrollment Started");
        //statushash.put("authsuccess", "Auth Successful");
        try
        {
            ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.tc");
            Enumeration enu = rb.getKeys();
            while (enu.hasMoreElements())
            {
                String key = (String) enu.nextElement();
                globalhash.put(key, rb.getString(key));
            }
            logger.debug("Global Variables set ");
        }
        catch (MissingResourceException mre)
        {
            logger.error("exception for resourse not found", mre);
        }
    }
    //static Category cat = Category.getInstance(Merchants.class.getName());
    int merchantid = -9999;
    public TransactionEntry(int id)
    {
        logger.debug("Entering Constructor");
        this.merchantid = id;
        logger.debug("leaving Constructor");
    }
    public TransactionEntry()
    {
    }
    public static boolean isTransactionLock(String fromid) throws SystemError
    {
        logger.debug("Entering isTransactionlock");
        //boolean locked=false;
        Connection conn=null;
        try
        {
            conn = Database.getConnection();
            String sql = "select * from transaction_lock where fromid = ? ";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,fromid);
            ResultSet rs = pstmt.executeQuery();
            logger.debug("leaving isTransactionlock");
            return rs.next();
        }
        catch(SQLException se)
        {   logger.error("SQL Exception in isTransactionLock",se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    //ToDo - Create a method only with trackingid - getTransDetail(trackingid)
    /*public static Hashtable getTransDetails(String accountId,String trackingId)
    {
        Hashtable  transDetails =null;
        if(accountId == null || accountId.equals("0") || accountId.equals(""))
        {
            accountId   = getAccountID(trackingId);       //get AccountId from bin-details table
        }
        if(accountId == null || accountId.equals("0") || accountId.equals(""))
        {
            transDetails = searchTransDetailsFromALLTables(trackingId);      //get transDetails without account id after searching all tables
        }
        else
        {
            transDetails = searchTransDetails(trackingId,GatewayAccountService.getGatewayAccount(accountId).getGateway());
        }
        return transDetails;
    }*/

    public static Hashtable getTransDetails(String trackingId)
    {
        Hashtable transDetails = null;
        Connection conn = null;
        ResultSet rs=null;
        try
        {
            conn = Database.getConnection();
            String s="";
            s="select FROM_UNIXTIME(dtstamp) as transactiondate,accountid,toid,paymodeid,cardtypeid,fromid,amount,description,captureamount,refundamount,transid,status,name,emailaddr,currency,notificationUrl from transaction_common where trackingid = ?";

            PreparedStatement p=conn.prepareStatement(s);
            p.setString(1,trackingId);
            rs = p.executeQuery();
            logger.debug("getTransDetails query-----"+p);
            if (rs.next())
            {
                transDetails = new Hashtable();
                transDetails.put("accountid", rs.getString("accountid"));
                transDetails.put("toid", rs.getString("toid"));
                transDetails.put("paymodeid", rs.getString("paymodeid"));
                transDetails.put("cardtypeid", rs.getString("cardtypeid"));
                transDetails.put("fromid", rs.getString("fromid"));
                transDetails.put("amount", rs.getString("amount"));
                transDetails.put("description", rs.getString("description"));
                transDetails.put("captureamount", rs.getString("captureamount"));
                transDetails.put("refundamount", rs.getString("refundamount"));
                transDetails.put("transid", rs.getString("transid"));
                transDetails.put("status", rs.getString("status"));
                transDetails.put("cName",rs.getString("name"));
                transDetails.put("cEmail",rs.getString("emailaddr"));
                transDetails.put("transactiondate", rs.getString("transactiondate"));
                transDetails.put("currency", rs.getString("currency"));
                if(rs.getString("notificationUrl") !=null){
                    transDetails.put("notificationUrl", rs.getString("notificationUrl"));
                }else{
                    transDetails.put("notificationUrl", "");
                }
                return transDetails;

            }
        }
        catch (SystemError systemError)
        {
            logger.error("Error",systemError);
        }
        catch (SQLException e)
        {
            logger.error("Error" ,e);
        }
        catch (Exception ex)
        {
            logger.error("Error" ,ex);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transDetails;

    }

    public static String getAccountID(String trackingId)
    {
        String accountID = null;
        Connection conn = null;
        ResultSet rs;
        try
        {
            conn = Database.getConnection();
            String s="select accountid from bin_details where icicitransid = ?";
            PreparedStatement p=conn.prepareStatement(s);
            p.setString(1,trackingId);
            rs = p.executeQuery();
            if (rs.next())
                accountID = rs.getString("accountid");
        }
        catch (SystemError systemError)
        {
            logger.error("Error",systemError);
        }
        catch (SQLException e)
        {
            logger.error("Error" ,e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return accountID;
    }
    public static Hashtable searchTransDetailsFromALLTables(String trackingId)
    {
        logger.debug("InSide searchTransDetailsFromALLTables");
        Hashtable transDetails = null;
        Set<String> gatewaySet = new HashSet<String>();
        gatewaySet.addAll(GatewayTypeService.getGateways());
        Iterator i = gatewaySet.iterator();
        Connection conn = null;
        String tablename = "";
        ResultSet rs =null;
        while(i.hasNext())
        {
            tablename = Database.getTableName((String)i.next());
            logger.debug("Table Name ; "+ tablename);
            try
            {

                conn = Database.getConnection();
                String s="";
                if(tablename.equals("transaction_icicicredit"))
                    s="select FROM_UNIXTIME(dtstamp) as transactiondate,accountid,toid,paymodeid,cardtypeid,fromid,amount,description,captureamount,refundamount,transid,status,name,emailaddr from transaction_icicicredit where icicitransid = ?";
                else
                    s="select FROM_UNIXTIME(dtstamp) as transactiondate,accountid,toid,paymodeid,cardtypeid,fromid,amount,description,captureamount,refundamount,transid,status,name,emailaddr from "+tablename+" where trackingid = ?";

                PreparedStatement p=conn.prepareStatement(s);
                p.setString(1,trackingId);

                rs = p.executeQuery();
                if (rs.next())
                {
                    transDetails = new Hashtable();

                    transDetails.put("accountid", rs.getString("accountid"));
                    transDetails.put("toid", rs.getString("toid"));
                    transDetails.put("paymodeid", rs.getString("paymodeid"));
                    transDetails.put("cardtypeid", rs.getString("cardtypeid"));
                    transDetails.put("fromid", rs.getString("fromid"));
                    transDetails.put("amount", rs.getString("amount"));
                    transDetails.put("description", rs.getString("description"));
                    transDetails.put("captureamount", rs.getString("captureamount"));
                    transDetails.put("refundamount", rs.getString("refundamount"));
                    transDetails.put("transid", rs.getString("transid"));
                    transDetails.put("status", rs.getString("status"));
                    transDetails.put("cName",rs.getString("name"));
                    transDetails.put("cEmail",rs.getString("emailaddr"));
                    transDetails.put("transactiondate",rs.getString("transactiondate"));
                    logger.debug("transdetails found");
                    return transDetails;
                }
                logger.debug("transdetails not found");
            }
            catch (SystemError systemError)
            {
                logger.error("Error",systemError);
            }
            catch (SQLException e)
            {
                logger.error("Error" ,e);
            }
            catch (Exception ex)
            {
                logger.error("Error" ,ex);
            }
            finally
            {
                Database.closeConnection(conn);
            }
        }
        return transDetails;
    }
    public static Hashtable searchTransDetails(String trackingId,String gatewayType)
    {
        logger.debug("InSide searchTransDetails");
        Hashtable transDetails = null;
        Connection conn = null;
        String tablename =Database.getTableName(gatewayType) ;
        ResultSet rs=null;
        try
        {
            conn = Database.getConnection();
            String s="";
            if(tablename.equals("transaction_icicicredit"))
                s="select FROM_UNIXTIME(dtstamp) as transactiondate,accountid,toid,paymodeid,cardtypeid,fromid,amount,description,captureamount,refundamount,transid,status,name,emailaddr from "+tablename+" where icicitransid = ?";
            else
                s="select FROM_UNIXTIME(dtstamp) as transactiondate,accountid,toid,paymodeid,cardtypeid,fromid,amount,description,captureamount,refundamount,transid,status,name,emailaddr from "+tablename+" where trackingid = ?";

            PreparedStatement p=conn.prepareStatement(s);
            p.setString(1,trackingId);
            rs = p.executeQuery();
            if (rs.next())
            {
                transDetails = new Hashtable();

                transDetails.put("accountid", rs.getString("accountid"));
                transDetails.put("toid", rs.getString("toid"));
                transDetails.put("paymodeid", rs.getString("paymodeid"));
                transDetails.put("cardtypeid", rs.getString("cardtypeid"));
                transDetails.put("fromid", rs.getString("fromid"));
                transDetails.put("amount", rs.getString("amount"));
                transDetails.put("description", rs.getString("description"));
                transDetails.put("captureamount", rs.getString("captureamount"));
                transDetails.put("refundamount", rs.getString("refundamount"));
                transDetails.put("transid", rs.getString("transid"));
                transDetails.put("status", rs.getString("status"));
                transDetails.put("cName",rs.getString("name"));
                transDetails.put("cEmail",rs.getString("emailaddr"));
                transDetails.put("transactiondate",rs.getString("transactiondate"));
                logger.debug("transdetails found");
                return transDetails;
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Error",systemError);
        }
        catch (SQLException e)
        {
            logger.error("Error" ,e);
        }
        catch (Exception ex)
        {
            logger.error("Error" ,ex);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transDetails;
    }
    public static Hashtable getChargesFromMappingTable(String memberid,String accountId,String paymodeId, String cardTypeid)
    {
        Hashtable chargeDetails = null;
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            String s = "select chargePercentage,fixApprovalCharge,fixDeclinedCharge,taxper,reversalcharge,withdrawalcharge,chargebackcharge,reservePercentage,fraudVerificationCharge,annualCharge,setupCharge,fxClearanceChargePercentage,monthlyGatewayCharge,monthlyAccountMntCharge,reportCharge,fraudulentCharge,autoRepresentationCharge,interchangePlusCharge from member_account_mapping where memberid = ? and accountid=? and paymodeid=? and cardtypeid=?";
            PreparedStatement p=conn.prepareStatement(s);
            p.setString(1,memberid);
            p.setString(2,accountId);
            p.setString(3,paymodeId);
            p.setString(4,cardTypeid);
            rs = p.executeQuery();
            if (rs == null)
            {
                throw new SQLException("Empty ResultSet in getHashFromResultSet as parameter");
            }
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int count = rsMetaData.getColumnCount();
            int i = 0;
            if (rs.next())
            {
                chargeDetails = new Hashtable();
                for (i = 1; i <= count; i++)
                {
                    if (rs.getString(i) != null)
                    {

                        chargeDetails.put(rsMetaData.getColumnLabel(i), rs.getString(i));

                    }
                }
                setChargesFromMemberAccountChargesMapping(memberid,accountId,paymodeId,cardTypeid,chargeDetails);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Error",systemError);
        }
        catch (SQLException e)
        {
            logger.error("Error" ,e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return chargeDetails;
    }
    public static void main(String[] args)
    {
        logger.debug(new BigDecimal("25.67").compareTo(new BigDecimal("10.00")));
    }
    //for taking charges from charge master and memberAccountChargeMapping
    private static void setChargesFromMemberAccountChargesMapping(String memberid, String accountId, String paymodeId, String cardTypeid, Hashtable chargeDetails)
    {
        Connection con =null;
        PreparedStatement psMemberAccountCharges=null;
        ResultSet rsMemberAccountCharges=null;
        try
        {
            logger.debug("inside setChargesFromMemberAccountChargesMapping()");
            con=Database.getConnection();
            String query="select CM.keyname as charge_key_name,MACM.chargevalue as chargevalue from member_accounts_charges_mapping as MACM join charge_master as CM on MACM.chargeid=CM.chargeid where MACM.memberid = ? and MACM.accountid=? and MACM.paymodeid=? and MACM.cardtypeid=?";
            psMemberAccountCharges=con.prepareStatement(query);
            psMemberAccountCharges.setString(1,memberid);
            psMemberAccountCharges.setString(2,accountId);
            psMemberAccountCharges.setString(3,paymodeId);
            psMemberAccountCharges.setString(4,cardTypeid);
            rsMemberAccountCharges=psMemberAccountCharges.executeQuery();
            while(rsMemberAccountCharges.next())
            {
                if(functions.isValueNull(rsMemberAccountCharges.getString("charge_key_name")))
                    chargeDetails.put(rsMemberAccountCharges.getString("charge_key_name"),rsMemberAccountCharges.getString("chargevalue"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System exception while getting charges from member account charges table::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL exception while h=getting charges from member account charges table::",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }

    public Hashtable listAccounts(String desc, String tdtstamp, String fdtstamp, int records, int pageno) throws SystemError
    {
        logger.debug("Entering listAccounts");
        Hashtable hash = null;
        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp = ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp = ESAPI.encoder().encodeForSQL(me,tdtstamp);
        // desc = ESAPI.encoder().encodeForSQL(me,desc);
        Connection conn=null;
        try
        {
            conn=Database.getConnection();
            StringBuffer query = new StringBuffer("select transid,toid,totype,fromid,fromtype,amount,description,dtstamp from transactions where");
            if (desc != null)
            {
                query.append(" description like '%" + desc + "%' ");
            }
            else
            {
                query.append(" description like '%%' ");
            }
            query.append(" and (toid ='" + merchantid + "'");
            query.append(" or fromid ='" + merchantid + "')");

            if (fdtstamp != null)
                query.append(" and dtstamp >= " + fdtstamp);
            if (tdtstamp != null)
                query.append(" and dtstamp <= " + tdtstamp);
            query.append(" order by dtstamp ASC,transid ASC ");
            query.append(" limit " + start + "," + end);
            logger.debug("account query..."+query);
            logger.debug("Fatch record for transaction");
            StringBuffer countquery = new StringBuffer("select count(*) from transactions where");
            countquery.append(" (toid ='" + merchantid + "'");
            countquery.append(" or fromid ='" + merchantid + "')");
            if (fdtstamp != null)
                countquery.append(" and dtstamp >= " + fdtstamp);
            if (tdtstamp != null)
                countquery.append(" and dtstamp <= " + tdtstamp);
            if (desc != null)
                countquery.append(" and description like '%" + desc + "%'");
            logger.debug("Count record");
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));
            ResultSet rs = Database.executeQuery(countquery.toString(), conn);
            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);
            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");
            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException se)
        {
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        logger.debug("Leaving listAccounts");
        return hash;
    }
    public String getSpecialBalance(String dtstamp, int transid) throws SystemError
    {
        logger.debug("Execute Select query for getSpecialBalance");
        BigDecimal depositedamount = null;
        BigDecimal withdrawedamount = null;
        Connection conn=null;
        try
        {
            if (transid == 0)
            {
                conn=Database.getConnection();
                String sql = "select max(transid) as transid from transactions where toid =? or fromid =?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1,merchantid);
                pstmt.setInt(2,merchantid);
                ResultSet rs = pstmt.executeQuery();
                rs.next();
                transid = rs.getInt("transid");
                transid++;
            }
            StringBuffer sql1 = new StringBuffer("select sum(amount) as amount from transactions where toid = ? " );
            sql1.append(" and totype = 'pz'");
            sql1.append(" and transid < ?" );

            PreparedStatement pstmt = conn.prepareStatement(sql1.toString());
            pstmt.setInt(1,merchantid);
            pstmt.setInt(2,transid);

            ResultSet rs1 = pstmt.executeQuery();

            rs1.next();
            String tempamt = rs1.getString("amount");

            if (tempamt != null)
                depositedamount = new BigDecimal(tempamt);
            else
                depositedamount = new BigDecimal("0");


            StringBuffer sql2 = new StringBuffer("select sum(amount) as amount from transactions where fromid=?");
            sql2.append(" and fromtype = 'pz'");

            //if(dtstamp!=null)
            //sql2.append(" and dtstamp <="+ dtstamp);
            //else
            sql2.append(" and transid < ?");


            PreparedStatement p=conn.prepareStatement(sql2.toString());
            p.setInt(1,merchantid);
            p.setInt(2,transid);
            ResultSet rs2 = p.executeQuery();
            rs2.next();
            tempamt = rs2.getString("amount");

            if (tempamt != null)
                withdrawedamount = new BigDecimal(tempamt);
            else
                withdrawedamount = new BigDecimal("0");

            logger.debug("leaving getSpecialBalance");
            return (depositedamount.subtract(withdrawedamount)).toString();
        }
        catch (SQLException se)
        {
            logger.error("SQLException is occure",se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
    public String getBalance() throws SystemError
    {
        String amount = null;
        logger.debug("Entering getBalance");
        amount = getbalance(-1, -1);
        logger.debug("Leaving getBalance with amount ");
        return amount;
    }
    public float getBalance(int merchantid) throws SystemError
    {
        logger.debug("Entering getBalance(mid) for merchant " + merchantid);
        float amt = 0;
        BigDecimal depositedamount = null, withdrawedamount = null;
        Connection conn=null;
        try
        {
            conn=Database.getConnection();
            String sql1 = "select sum(amount) as amount from transactions where toid =?";
            PreparedStatement p1=conn.prepareStatement(sql1);
            p1.setInt(1,merchantid);
            ResultSet rs1 = p1.executeQuery();
            rs1.next();
            String tempamt = rs1.getString("amount");
            if (tempamt != null)
                depositedamount = new BigDecimal(tempamt);
            else
                depositedamount = new BigDecimal("0");

            String sql2 = "select sum(amount) as amount from transactions where fromid=?";
            PreparedStatement p2=conn.prepareStatement(sql2);
            p2.setInt(1,merchantid);
            ResultSet rs2 = p2.executeQuery();
            rs2.next();
            tempamt = rs2.getString("amount");
            if (tempamt != null)
                withdrawedamount = new BigDecimal(tempamt);
            else
                withdrawedamount = new BigDecimal("0");

            amt = (depositedamount.subtract(withdrawedamount)).floatValue();
            logger.debug("leaving getBalance(mid) for merchant " + merchantid + " with amount " + amt);

            return amt;
        }
        catch (SQLException se)
        {    logger.error("SQLException getBalance",se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
    public String getbalance(int fdtstamp, int tdtstamp) throws SystemError
    {
        logger.debug("Entering getBalance");
        BigDecimal depositedamount = null;
        BigDecimal withdrawedamount = null;
        Connection conn= null;
        try
        {
            conn=Database.getConnection();
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            StringBuffer sql1 = new StringBuffer("select sum(amount) as amount from transactions where toid =" + merchantid);
            if (fdtstamp > 0 && tdtstamp > 0)
                sql1.append(" and dtstamp between " + fdtstamp + " and " + tdtstamp);

            logger.info(sql1);

            ResultSet rs1 = Database.executeQuery(sql1.toString(), conn);

            rs1.next();
            String tempamt = rs1.getString("amount");

            if (tempamt != null)
                depositedamount = new BigDecimal(tempamt);
            else
                depositedamount = new BigDecimal("0");


            StringBuffer sql2 = new StringBuffer("select sum(amount) as amount from transactions where fromid=" + merchantid);
            if (fdtstamp > 0 && tdtstamp > 0)
                sql2.append(" and dtstamp between " + fdtstamp + " and " + tdtstamp);

            logger.info(sql2);

            ResultSet rs2 = Database.executeQuery(sql2.toString(), conn);
            rs2.next();
            tempamt = rs2.getString("amount");

            if (tempamt != null)
                withdrawedamount = new BigDecimal(tempamt);
            else
                withdrawedamount = new BigDecimal("0");

            logger.debug("leaving getBalance");
            return (depositedamount.subtract(withdrawedamount)).toString();
        }
        catch (SQLException se)
        {   logger.error("SQLException is occure in getBalance",se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
    public int transactionEntry(String toid, String fromid, String toType, String fromType, String amount, String description, String type) throws SystemError
    {
        logger.debug("Entering transactionEntry");
        Connection conn=null;
        String dtstamp = "" + (new java.util.Date()).getTime() / 1000;
        try
        {
            conn=Database.getConnection();
            String sql = "insert into transactions(toid,totype,fromid,fromtype,amount,description,dtstamp,type) values (?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt=conn.prepareStatement(sql);
            pstmt.setString(1, toid);
            pstmt.setString(2, toType);
            pstmt.setString(3,fromid);
            pstmt.setString(4,fromType);
            pstmt.setString(5, amount);
            pstmt.setString(6, description);
            pstmt.setString(7, dtstamp);
            pstmt.setString(8,type);
            int result =pstmt.executeUpdate();
            logger.debug("+++++++++++++insert query++++++++++:"+result);
            return result;
        }
        catch(SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ",se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    public String withDraw(String fromid, String amount, String description, String charge, String toid, String calculatedTax, String taxPercentage, String tax_toid) throws SystemError
    {
        logger.debug("Entering in withDraw");
        Connection conn=null;
        String transid = null;
        try
        {
            conn=Database.getConnection();
            Database.setAutoCommit(conn, false);
            transactionEntry(fromid, fromid, TYPE_WITHDRAWAL, TYPE_PZ, amount, description, WITHDRAWAL);

            StringBuffer sql = new StringBuffer("select max(transid) from transactions where");
            sql.append(" toid= ?");
            sql.append(" and amount= ?");
            sql.append(" and description= ?");
            PreparedStatement pstmt= conn.prepareStatement(sql.toString());
            pstmt.setString(1,fromid);
            pstmt.setString(2,amount);
            pstmt.setString(3,description);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                transid = rs.getString(1);
            }
            description = "Charges for withdrawal of (" + description + ")";

            transactionEntry(toid, fromid, TYPE_PZ, TYPE_PZ, charge, "" + description, WITHDRAWAL_CHARGES);
            taxEntry(fromid, calculatedTax.toString(), "Service Tax on Withdrawal charges @" + taxPercentage + "%(transid - " + transid + ")", tax_toid, TAX_WITHDRAWAL_CHARGES);

            Database.commit(conn);
            logger.debug("Leaving in withDraw ");
            return transid;
        }
        catch (SQLException se)
        {
            Database.rollback(conn);
            logger.error("SQL Exception in withDraw",se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.setAutoCommit(conn, true);
            Database.closeConnection(conn);
        }
    }
    public void taxEntry(String fromid, String calculatedTax, String description, String tax_toid, String tax_type) throws SystemError
    {
        if (Double.parseDouble(calculatedTax) > 0.0)
        {
            transactionEntry(tax_toid, fromid, TYPE_PZ, TYPE_PZ, calculatedTax, description, tax_type);
        }
    }
    public void setTransactionLock(String fromid) throws SystemError
    {
        logger.debug("Entering setTransactionLock");
        Connection conn=null;
        String dtstamp = "" + (new java.util.Date()).getTime() / 1000;
        try
        {
            conn=Database.getConnection();
            String sql ="insert into transaction_lock(fromid ,dtstamp) values (?,?)";
            PreparedStatement pa=conn.prepareStatement(sql);
            pa.setString(1,fromid);
            pa.setString(2,dtstamp);
            pa.executeUpdate();
            logger.debug("leaving setTransactionLock");
        }
        catch(SQLException se)
        {
            logger.error("SQL Exception leaving setTransactionLock" ,se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    public void deleteTransactionLock(String fromid) throws SystemError
    {
        Connection conn=null;
        try
        {
            conn=Database.getConnection();
            logger.debug("Entering deleteTransactionLock");
            String sql = "delete from transaction_lock where fromid =?";
            PreparedStatement p=conn.prepareStatement(sql);
            p.setString(1,fromid);
            p.executeUpdate();
            logger.debug("leaving deleteTransactionLock");
        }
        catch(SQLException se)
        {
            logger.error("SQL Exception leaving deleteTransactionLock" ,se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
    public void insertFinanceActivities(String transid, String desc) throws SystemError
    {
        Connection conn=null;
        try
        {
            conn=Database.getConnection();
            logger.debug("Entering insertFinanceActivities");
            String dtstamp = "" + (new java.util.Date()).getTime() / 1000;
            //String sql = "insert into finance_activities (transid ,description ,dtstamp )  values ('" + transid + "','" + desc + "','" + dtstamp + "')";
            String sql = "insert into finance_activities (transid ,description ,dtstamp )  values (?,?,?)";
            PreparedStatement p=conn.prepareStatement(sql);
            p.setString(1,transid);
            p.setString(2,desc);
            p.setString(3,dtstamp);
            p.executeUpdate();
            logger.debug("leaving insertFinanceActivities");
        }
        catch(SQLException se)
        {
            logger.error("SQL Exception leaving insertFinanceActivities" ,se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
    public BigDecimal getWithdrawBalance(String chargeAmount, String calculatedTax) throws SystemError
    {
        Merchants merchants = new Merchants();
        logger.debug("getting withdraw balance");
        BigDecimal bdConst = new BigDecimal("0.01");
        BigDecimal bdCharge = new BigDecimal(chargeAmount);
        BigDecimal balance = new BigDecimal(getBalance());
        BigDecimal calcTax = new BigDecimal(calculatedTax);
        BigDecimal reserves = new BigDecimal(merchants.getColumn("reserves", "" + merchantid));
        reserves = reserves.multiply(bdConst);
        return balance.subtract(reserves).subtract(bdCharge).subtract(calcTax).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    public BigDecimal calculateTax(String chargeAmount, String taxPercentage)
    {
        logger.debug("Entering calculateTax method ");
        BigDecimal bdConst = new BigDecimal("0.01");
        logger.debug("chargeAmount : " + chargeAmount);
        BigDecimal bdCharge = new BigDecimal(chargeAmount);
        BigDecimal taxpercent = new BigDecimal(taxPercentage);
        taxpercent = taxpercent.multiply(bdConst);
        return bdCharge.multiply(taxpercent).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    public void closeConnection()
    {
        //Database.closeConnection(cn);
    }


    public SortedMap getSortedMap()
    {
        return sortedMap;
    }


    public Hashtable getBankHash(String memberid)
    {
        Hashtable bankhash = new Hashtable();
        Connection connection=null;
        PreparedStatement pstmt2=null;
        ResultSet rs2=null;
        try
        {

            //connection=Database.getConnection();
            connection=Database.getRDBConnection();
            String query= "select accountid from member_account_mapping where memberid=?";
            pstmt2= connection.prepareStatement(query);
            pstmt2.setString(1,memberid);
            rs2 = pstmt2.executeQuery();
            while (rs2.next())
            {

                String displayname = GatewayAccountService.getGatewayAccount(rs2.getString("accountid")).getDisplayName();
                bankhash.put(rs2.getString("accountid"),displayname);
            }

        }
        catch (SQLException se)
        {
            logger.error("SQLException is occure",se);

        }
        catch (SystemError systemError)
        {
            logger.error("SQLException is occure",systemError);
        }
        finally
        {
            Database.closeResultSet(rs2);
            Database.closePreparedStatement(pstmt2);
            Database.closeConnection(connection);
        }
        return bankhash;
    }
    public Hashtable getGatewayHash(String memberid,String accountid)
    {
        Hashtable gatewayhash = new Hashtable();
        Connection connection=null;
        PreparedStatement pstmt2=null;
        ResultSet rs2=null;
        if(accountid==null || accountid.equals("") || accountid.equals("null"))
        {
            try
            {
                //connection= Database.getConnection();
                connection= Database.getRDBConnection();
                String query = "select accountid from member_account_mapping where accountid>0 AND memberid=?";
                pstmt2= connection.prepareStatement(query);
                pstmt2.setString(1,memberid);
                rs2 = pstmt2.executeQuery();
                logger.debug("query---"+pstmt2);
                while (rs2.next())
                {
                    String gateway = GatewayAccountService.getGatewayAccount(rs2.getString("accountid")).getGateway();
                    String displayname = GatewayAccountService.getGatewayAccount(rs2.getString("accountid")).getDisplayName();
                    gatewayhash.put(gateway,displayname);
                }
            }
            catch (SQLException se)
            {
                logger.error("SQLException is occure",se);

            }
            catch (SystemError systemError)
            {
                logger.error("SQLException is occure", systemError);
            }
            finally
            {
                Database.closeResultSet(rs2);
                Database.closePreparedStatement(pstmt2);
                Database.closeConnection(connection);
            }
        }
        else
        {
            String gateway = GatewayAccountService.getGatewayAccount(accountid).getGateway();
            String displayname = GatewayAccountService.getGatewayAccount(accountid).getDisplayName();
            gatewayhash.put(gateway,displayname);
        }
        return gatewayhash;
    }


    public Hashtable getGatewayHashPartner(String memberid,String accountid,String pgtypeId)
    {
        Hashtable gatewayhash = new Hashtable();
        Connection connection=null;
        if(accountid==null || accountid.equals("") || accountid.equals("null"))
        {
            try
            {
                //connection= Database.getConnection();
                connection= Database.getRDBConnection();
                String query = "select accountid from member_account_mapping where accountid>0 AND memberid=?";
                PreparedStatement pstmt2= connection.prepareStatement(query);
                pstmt2.setString(1,memberid);
                ResultSet rs2 = pstmt2.executeQuery();
                logger.debug("query---"+pstmt2);
                while (rs2.next())
                {
                    String gateway = GatewayAccountService.getGatewayAccount(rs2.getString("accountid")).getGateway();
                    String displayname = GatewayAccountService.getGatewayAccount(rs2.getString("accountid")).getDisplayName();
                    logger.debug("gateway:::::"+gateway);
                    logger.debug("displayname:::::"+displayname);
                    gatewayhash.put(gateway,displayname);
                }
            }
            catch (SQLException se)
            {
                logger.error("SQLException is occure",se);

            }
            catch (SystemError systemError)
            {
                logger.error("SQLException is occure", systemError);
            }
            finally
            {
                Database.closeConnection(connection);
            }
        }
        else
        {
            String gateway = GatewayAccountService.getGatewayAccount(accountid).getGateway();
            String displayname = GatewayAccountService.getGatewayAccount(accountid).getDisplayName();
            gatewayhash.put(gateway,displayname);
        }
        return gatewayhash;
    }
    public Hashtable getGatewayHashByPartnerMembers(String memberList,String accountId)
    {
        Hashtable gatewayhash = new Hashtable();
        Connection connection=null;
        PreparedStatement pstmt2= null;
        ResultSet rs2 = null;
        if(accountId==null || accountId.equals("") || accountId.equals("null"))
        {
            try
            {
                //connection= Database.getConnection();
                connection= Database.getRDBConnection();
                StringBuilder query= new StringBuilder("select accountid from member_account_mapping where memberid in ( ");
                query.append(memberList);
                query.append(" )");

                pstmt2= connection.prepareStatement(query.toString());
                rs2 = pstmt2.executeQuery();
                while (rs2.next())
                {
                    String gateway = GatewayAccountService.getGatewayAccount(rs2.getString("accountid")).getGateway();
                    String displayname = GatewayAccountService.getGatewayAccount(rs2.getString("accountid")).getDisplayName();
                    gatewayhash.put(gateway,displayname);
                }
            }
            catch (SQLException se)
            {
                logger.error("SQLException is occure",se);

            }
            catch (SystemError systemError)
            {
                logger.error("SQLException is occure", systemError);
            }
            finally
            {
                Database.closeResultSet(rs2);
                Database.closePreparedStatement(pstmt2);
                Database.closeConnection(connection);
            }
        }
        else
        {
            String gateway = GatewayAccountService.getGatewayAccount(accountId).getGateway();
            String displayname = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
            gatewayhash.put(gateway,displayname);
        }
        return gatewayhash;
    }

    public Hashtable getGatewayHashFromAgent(String memberid,String agentid) throws SQLException,SystemError
    {
        Hashtable gatewayhash = new Hashtable();
        Connection connection=null;
        PreparedStatement pstmt2= null;
        ResultSet rs = null;
        /*if(accountid==null || accountid.equals("") || accountid.equals("null"))
        {*/
        try
        {
            //connection= Database.getConnection();
            connection= Database.getRDBConnection();
            String query= "SELECT accountid FROM member_account_mapping AS macm JOIN merchant_agent_mapping AS mam ON macm.memberid=mam.memberid WHERE macm.memberid=? AND mam.agentid=?";
            pstmt2= connection.prepareStatement(query);
            pstmt2.setString(1,memberid);
            pstmt2.setString(2,agentid);
            rs = pstmt2.executeQuery();
            while (rs.next())
            {
                String gateway = GatewayAccountService.getGatewayAccount(rs.getString("accountid")).getGateway();
                String displayname = GatewayAccountService.getGatewayAccount(rs.getString("accountid")).getDisplayName();
                gatewayhash.put(gateway,displayname);
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt2);
            Database.closeConnection(connection);
        }
        /*}
        else
        {
            String gateway = GatewayAccountService.getGatewayAccount(accountid).getGateway();
            String displayname = GatewayAccountService.getGatewayAccount(accountid).getDisplayName();
            gatewayhash.put(gateway,displayname);
        }*/
        return gatewayhash;
    }

    public Hashtable listTransactionsNew(String desc, String tdtstamp, String fdtstamp,String trackingid, String status,String timestamp, int records, int pageno, boolean archive, Set<String> gatewayTypeSet, String firstName, String lastName, String emailAddress,String dateType,String customerId, String terminalid, String statusflag, String issuingBank,String firstSix,String lastFour,String transactionMode,String successtimestamp,String failuretimestamp,String refundtimestamp,String payouttimestamp,String chargebacktimestamp) throws SystemError
    {
        logger.debug("Entering listTransactionsNew");

        if(!Functions.isValidSQL(trackingid) || !Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");

        }
        Connection connection   = null;
        Hashtable hash          = null;
        String tablename        = "";
        String fields           = "";
        String pRefund          = "false";
        StringBuffer query      = new StringBuffer();
        //Encoding for SQL Injection check

        Codec me    = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp    = ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp    = ESAPI.encoder().encodeForSQL(me,tdtstamp);
        desc        = ESAPI.encoder().encodeForSQL(me,desc);
        trackingid  = ESAPI.encoder().encodeForSQL(me,trackingid);

        int start   = 0; // start index
        int end     = 0; // end index
        start       = (pageno - 1) * records;
        end         = records;

        if(gatewayTypeSet.size()==0)
        {
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }

        try
        {
            Iterator i  = gatewayTypeSet.iterator();
            connection  = Database.getConnection();
            tablename   = "transaction_common";
            if (archive)
            {
                //tablename += "_archive";        //ToDO will be used once all the archive tables will be avialbale.
                tablename = "transaction_common_archive";
            }
            fields = "t.trackingid as transid,t.status,t.name,t.amount,t.captureamount,t.refundamount,t.payoutamount,t.walletAmount,t.walletCurrency,t.description,t.orderdescription,t.dtstamp,t.timestamp,t.paymodeid,t.cardtype,t.cardtypeid,t.accountid,t.currency,t.remark,t.successtimestamp,t.failuretimestamp,t.refundtimestamp,t.payouttimestamp,t.chargebacktimestamp,t.ccnum,t.paymentid,t.customerId,t.toid,t.emailaddr,t.terminalid,t.chargebackinfo,b.issuing_bank,t.transaction_mode, t.processingbank ,b.first_six,b.last_four,t.arn, t.rrn";
           // query.append("select " + fields + " from " + tablename + " as t LEFT JOIN bin_details as b on t.trackingid = b.icicitransid where t.trackingid>0 ");
            query.append("select " + fields + " from " + tablename + " as t LEFT JOIN bin_details as b on t.trackingid = b.icicitransid where ");
            query.append(" t.toid ='" + merchantid + "'");
            if (status != null)
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status = "reversed";
                    pRefund = "true";
                }
                query.append(" and t.status='" + status + "'");
            }
            if (!"all".equalsIgnoreCase(statusflag))
            {
                query.append(" and b."+statusflag+"='Y'");
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" and t.timestamp >= '" + startDate + "'");
            }
            else
            {
                query.append(" and t.dtstamp >= " + fdtstamp);
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and t.timestamp <= '" + endDate + "'");
            }
            else
            {
                query.append(" and t.dtstamp <= " + tdtstamp);
            }
            if (desc != null)
            {
                query.append(" and t.description like '%" + desc + "%'");
            }
            if (trackingid != null && !trackingid.equalsIgnoreCase(""))
            {
                query.append(" and t.trackingid=" + trackingid);
            }
          /*  if (functions.isValueNull(gateway_name))
            {
                query.append(" and t.fromtype= '"+gateway_name+"'");
            }*/
            if (functions.isValueNull(issuingBank))
            {
                query.append(" and b.issuing_bank= '"+issuingBank+"'");
            }
            if (functions.isValueNull(firstSix))
            {
                query.append(" and b.first_six= '"+firstSix+"'");
            }
            if (functions.isValueNull(lastFour))
            {
                query.append(" and b.last_four= '"+lastFour+"'");
            }
            if(functions.isValueNull(firstName))
            {
                query.append(" and t.firstname= '" + firstName+"'");
            }
            if(functions.isValueNull(lastName))
            {
                query.append(" and t.lastname= '"+lastName+"'");
            }
            if(functions.isValueNull(emailAddress))
            {
                query.append(" and t.emailaddr= '"+emailAddress+"'");
            }
            if(functions.isValueNull(customerId))
            {
                query.append(" and t.customerId= '"+customerId+"'");
            }
            //if(!terminalid.equals("all"))
            if(!terminalid.equals("all") && !terminalid.equals("NoTerminals") )
            {
                query.append(" and t.terminalid =" + terminalid);
            }
            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and t.captureamount > t.refundamount");
            }
            if (functions.isValueNull(transactionMode))
            {
                query.append(" and t.transaction_mode ='"+transactionMode.trim()+"'");
            }
            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");
            query.append(" order by transid desc");
           // query.append(" limit " + start + "," + end);
            logger.error("transaction query inside exporttoexcel..."+query);
            hash                = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), connection));
            ResultSet rs        = Database.executeQuery(countquery.toString(), connection);

            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException se)
        {
            logger.error("SQL Exception leaving listTransactions" ,se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        logger.debug("Leaving listTransactions");
        return hash;
    }

    //New Method For Export to Excel in Partner
    public Hashtable  listForPartnerTransactions(String tdtstamp, String fdtstamp, String status, String desc, String toid, boolean archive, String trackingid, String currency, int records, int pageno, Set<String> gatewayTypeSet, String accountid, String accountid1, String gate_name, String currency1, String dateType, String firstName, String lastName, String emailAddr, String customerId, String startTime, String endTime, String statusflag, String issuingbank, String cardtype, String terminalid,String partnerName,String transactionMode,String successtimestamp,String failuretimestamp,String refundtimestamp,String payouttimestamp,String chargebacktimestamp) throws SystemError
    {
        logger.debug("inside listForPartnerTransactions");
        //System.out.println("trackingid"+trackingid);
        if(!Functions.isValidSQL(trackingid) || !Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");
        }
        Connection connection       = null;
        PreparedStatement pstms     = null;
        PreparedStatement pstms1    = null;
        ResultSet rs                = null;
        Hashtable hash              = null;
        String tablename            = "";
        String fields               = "";
        int counter                 = 1;
        String pRefund              = "false";
        StringBuilder query         = new StringBuilder();

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        //status = ESAPI.encoder().encodeForSQL(me,status);
        fdtstamp    = ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp    = ESAPI.encoder().encodeForSQL(me,tdtstamp);
        //desc        = ESAPI.encoder().encodeForSQL(me,desc);
        /*trackingid=ESAPI.encoder().encodeForSQL(me,trackingid);*/

       /* int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;*/

        if(gatewayTypeSet.size() == 0)
        {
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }

        try
        {
            Iterator i  = gatewayTypeSet.iterator();
            connection  = Database.getConnection();
            tablename   = "transaction_common";
            if (archive)
            {
                tablename = "transaction_common_archive";
            }
            fields = "t.toid,t.trackingid as transid,t.status,t.name,t.currency,t.country,t.amount,t.captureamount,t.walletAmount,t.walletCurrency," +
                    "t.refundamount,t.chargebackamount,t.payoutamount,t.description,t.dtstamp,t.paymodeid,t.cardtype,t.accountid,t.remark,t.successtimestamp,t.failuretimestamp,t.refundtimestamp,t.payouttimestamp,t.chargebacktimestamp,t.ccnum," +
                    "t.timestamp,t.emailaddr,t.orderdescription,t.customerId,t.terminalid,t.paymentid,t.fromtype,m.company_name,m.partnerId,m.memberid," +
                    "t.chargebackinfo,t.rrn,t.arn,t.authorization_code,t.transaction_mode,t.processingbank ";

            /*if (!"all".equalsIgnoreCase(statusflag) || functions.isValueNull(issuingbank))
            {*/

                fields = fields +",bd.issuing_bank,bd.bin_card_category,bd.bin_card_type,bd.bin_sub_brand,bd.country_name,bd.subcard_type,bd.first_six,bd.last_four ";
                // query.append("LEFT JOIN bin_details as b on t.trackingid = b.icicitransid ");
            //}
            query.append("select " + fields + " from " + tablename + " AS t  ");
            /*if (!"all".equalsIgnoreCase(statusflag) || functions.isValueNull(issuingbank))
            {*/
                query.append(" LEFT JOIN bin_details AS  bd ON t.trackingid=bd.icicitransid ");
            //}
            query.append(" JOIN members AS m ON t.toid = m.memberid ");
            //query.append("select ");
            //query.append(fields);
            //query.append(" from ");
            //query.append(tablename);
            //query.append(" AS t,bin_details AS bd, members AS m where t.trackingid=bd.icicitransid and t.toid = m.memberid");
            //query.append(" AS t, members AS m, bin_details AS b where fieldst.trackingid = b.icicitransid and t.toid = m.memberid");
            //query.append(" AS t LEFT JOIN bin_details AS b ON t.trackingid = b.icicitransid JOIN members AS m ON t.toid = m.memberid WHERE memberid>0");

            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds       = Long.parseLong(fdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" where t.timestamp >= '"+startDate+"'");
            }
            else
            {
                query.append("where t.dtstamp >="+fdtstamp);
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {


                long milliSeconds       = Long.parseLong(tdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and t.timestamp <= '"+endDate+"'");
            }
            else
            {
                query.append(" and t.dtstamp <="+tdtstamp);
            }

            if (functions.isValueNull(toid))
            {
                query.append(" and t.toid in (" );
                query.append(toid);
                query.append(" )");
            }
            if (functions.isValueNull(status))
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status      = "reversed";
                    pRefund     = "true";
                }
                query.append(" and t.status= ? ");
            }
            if (!"all".equalsIgnoreCase(statusflag) )
            {
                query.append(" and bd."+statusflag+"='Y'");
            }
            /*if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                query.append(" and t.timestamp >= ? ");
            }
            else
            {
                query.append(" and t.dtstamp >= ? ");
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                query.append(" and t.timestamp <= ? ");
            }
            else
            {
                query.append(" and t.dtstamp <= ? ");
            }*/
            if (functions.isValueNull(desc))
            {
               // query.append(" and t.description like % ? %");
                query.append(" and t.description= ? ");
            }
            /*if (functions.isValueNull(trackingid))
            {
                query.append(" and t.trackingid= ? ");
            }*/
            if (functions.isValueNull(trackingid.toString()))
            {
                query.append(" and t.trackingid IN("+trackingid.toString()+")");
            }

            if(functions.isValueNull(issuingbank))
            {
                query.append(" and bd.issuing_bank= ? ");
            }

            if(functions.isValueNull(gate_name))
            {
                query.append(" and t.fromtype= ? ");
            }

            if(functions.isValueNull(currency1))
            {
                query.append(" and t.currency= ? ");
            }

            if(functions.isValueNull(accountid1))
            {
                query.append(" and t.accountid= ? ");
            }

            if(functions.isValueNull(firstName))
            {
                query.append(" and t.firstname= ? ");
            }
            if (functions.isValueNull(lastName))
            {
                query.append(" and t.lastname= ? ");
            }
            if(functions.isValueNull(emailAddr))
            {
                query.append(" and t.emailAddr= ? ");
            }
            if (functions.isValueNull(cardtype))
            {
                query.append(" and t.cardtype= ? ");
            }
            if (terminalid != null && !terminalid.equals("") && !terminalid.equals("null"))
            {
                query.append(" and t.terminalid= ? ");
            }
            if(functions.isValueNull(customerId))
            {
                query.append(" and t.customerId= ? ");
            }
            if (functions.isValueNull(partnerName))
            {
                query.append(" and t.totype in("+partnerName+")");
            }
            if(functions.isValueNull(transactionMode))
            {
                query.append(" and t.transaction_mode= ? ");
            }
            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and captureamount > refundamount");
            }

            StringBuilder countquery = new StringBuilder("select count(*) from ( " );
            countquery.append(query);
            countquery.append(" ) as temp ");

            query.append(" order by transid desc");
            // query.append(" limit ? , ? ");

            pstms   = connection.prepareStatement(query.toString());
            pstms1  = connection.prepareStatement(countquery.toString());

            if (functions.isValueNull(status))
            {
                pstms.setString(counter, status);
                pstms1.setString(counter, status);
                counter++;
            }
            /*if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds       = Long.parseLong(fdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                pstms.setString(counter, startDate);
                pstms1.setString(counter, startDate);
                counter++;
            }
            else
            {
                pstms.setString(counter, fdtstamp);
                pstms1.setString(counter, fdtstamp);
                counter++;
            }*/
            /*if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds       = Long.parseLong(tdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                pstms.setString(counter, endDate);
                pstms1.setString(counter, endDate);
                counter++;
            }
            else
            {
                pstms.setString(counter, tdtstamp);
                pstms1.setString(counter, tdtstamp);
                counter++;
            }*/
            if (functions.isValueNull(desc))
            {
                pstms.setString(counter, desc);
                pstms1.setString(counter, desc);
                counter++;
            }
           /* if (functions.isValueNull(desc))
            {
                pstms.setString(counter, desc);
                pstms1.setString(counter, desc);
                counter++;
            } */
            /*if (functions.isValueNull(trackingid))
            {
                pstms.setString(counter, trackingid);
                pstms1.setString(counter, trackingid);
                counter++;
            }*/

            if (functions.isValueNull(issuingbank))
            {
                pstms.setString(counter, issuingbank);
                pstms1.setString(counter, issuingbank);
                counter++;
            }

            if(functions.isValueNull(gate_name))
            {
                pstms.setString(counter, gate_name);
                pstms1.setString(counter, gate_name);
                counter++;
            }

            if(functions.isValueNull(currency1))
            {
                pstms.setString(counter, currency1);
                pstms1.setString(counter, currency1);
                counter++;
            }

            if(functions.isValueNull(accountid1))
            {
                pstms.setString(counter, accountid1);
                pstms1.setString(counter, accountid1);
                counter++;
            }

            if(functions.isValueNull(firstName))
            {
                pstms.setString(counter, firstName);
                pstms1.setString(counter, firstName);
                counter++;
            }
            if (functions.isValueNull(lastName))
            {
                pstms.setString(counter, lastName);
                pstms1.setString(counter, lastName);
                counter++;
            }
            if(functions.isValueNull(emailAddr))
            {
                pstms.setString(counter, emailAddr);
                pstms1.setString(counter, emailAddr);
                counter++;
            }
            if (functions.isValueNull(cardtype))
            {
                pstms.setString(counter, cardtype);
                pstms1.setString(counter,cardtype);
                counter++;
            }
            if (functions.isValueNull(terminalid))
            {
                pstms.setString(counter, terminalid);
                pstms1.setString(counter, terminalid);
                counter++;
            }
            if(functions.isValueNull(customerId))
            {
                pstms.setString(counter, customerId);
                pstms1.setString(counter, customerId);
                counter++;
            }
            if(functions.isValueNull(transactionMode))
            {
                pstms.setString(counter, transactionMode);
                pstms1.setString(counter, transactionMode);
                counter++;
            }
           /* pstms.setInt(counter, start);
            counter++;
            pstms.setInt(counter, end);*/

            logger.error("export pstmt:::::"+pstms);
            logger.error("export pstmt1:::::"+pstms1);

            hash    = Database.getHashFromResultSetForTransactionEntry(pstms.executeQuery());
            rs      = pstms1.executeQuery();
            //System.out.println("pstms1"+pstms);

            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException se)
        {
            logger.error("SQL Exception leaving listTransactions" ,se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstms);
            Database.closePreparedStatement(pstms1);
            Database.closeConnection(connection);
        }
        logger.debug("Leaving listTransactions"+hash);
        return hash;
    }

    //New Method For Export to Excel in Partner
    public Hashtable  listForPartnerCardPresentTransactions(String tdtstamp, String fdtstamp, String status, String desc, String toid, boolean archive, String trackingid, String currency, int records, int pageno, Set<String> gatewayTypeSet, String accountid, String accountid1, String gate_name, String currency1, String dateType, String firstName, String lastName, String emailAddr, String customerId, String startTime, String endTime, String statusflag, String issuingbank, String cardtype, String terminalid,String partnerName,String successtimestamp,String failuretimestamp,String refundtimestamp,String payouttimestamp,String chargebacktimestamp) throws SystemError
    {
        logger.debug("inside listForPartnerTransactions");
        if(!Functions.isValidSQL(trackingid) || !Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");
        }
        Connection connection       = null;
        PreparedStatement pstms     = null;
        PreparedStatement pstms1    = null;
        ResultSet rs                = null;
        Hashtable hash              = null;
        String tablename            = "";
        String fields               = "";
        int counter                 = 1;
        String pRefund              = "false";
        StringBuilder query         = new StringBuilder();

        Codec me    = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        //status = ESAPI.encoder().encodeForSQL(me,status);
        fdtstamp    = ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp    = ESAPI.encoder().encodeForSQL(me,tdtstamp);
        desc        = ESAPI.encoder().encodeForSQL(me,desc);
        trackingid  = ESAPI.encoder().encodeForSQL(me,trackingid);

       /* int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;*/

        if(gatewayTypeSet.size()==0)
        {
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }

        try
        {
            Iterator i  = gatewayTypeSet.iterator();
            connection  = Database.getConnection();
            tablename   = "transaction_card_present";;
            if (archive)
            {
                tablename = "transaction_common_archive";
            }
            fields = "t.toid,t.trackingid as transid,t.status,t.name,t.currency,t.country,t.amount,t.captureamount,t.walletAmount,t.walletCurrency," +
                    "t.refundamount,t.chargebackamount,t.payoutamount,t.description,t.dtstamp,t.transactionTime, t.paymodeid,t.cardtype,t.accountid," +
                    "t.remark,t.successtimestamp,t.failuretimestamp,t.refundtimestamp,t.payouttimestamp,t.chargebacktimestamp,t.ccnum,t.timestamp,t.emailaddr,t.orderdescription,t.customerId,t.terminalid,t.paymentid,t.fromtype,m.company_name," +
                    "m.partnerId,m.memberid,t.chargebackinfo,t.rrn,t.arn,t.authorization_code";
            //query.append("select " + fields + " from " + tablename + " AS t,bin_details AS bd, members AS m where t.trackingid=bd.icicitransid and t.toid = m.memberid");
            if (!"all".equalsIgnoreCase(statusflag) || functions.isValueNull(issuingbank))
            {
                fields = fields +",bd.bin_card_category,bd.bin_card_type,bd.first_six,bd.last_four ";
            }
            query.append("select ");
            query.append(fields);
            query.append(" from ");
            query.append(tablename);
            //query.append(" AS t,bin_details AS bd, members AS m where t.trackingid=bd.icicitransid and t.toid = m.memberid");
            //query.append(" AS t, members AS m, bin_details AS b where t.trackingid = b.icicitransid and t.toid = m.memberid");
/*
            query.append(" AS t LEFT JOIN bin_details AS b ON t.trackingid = b.icicitransid JOIN members AS m ON t.toid = m.memberid WHERE memberid>0");
    */
            query.append(" AS t  JOIN members AS m ON t.toid = m.memberid  ");

            if (!"all".equalsIgnoreCase(statusflag) || functions.isValueNull(issuingbank))
            {
                query.append(" LEFT JOIN bin_details as bd on bd.icicitransid = t.trackingid ");
            }

            query.append("WHERE memberid > 0 ");

            if (functions.isValueNull(partnerName))
            {
                query.append(" and t.totype in("+partnerName+")");
            }
            if (functions.isValueNull(toid))
            {
                query.append(" and t.toid in (" );
                query.append(toid);
                query.append(" )");
            }
            if (functions.isValueNull(status))
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status  = "reversed";
                    pRefund = "true";
                }
                query.append(" and t.status= ? ");
            }
            /*if (!"all".equalsIgnoreCase(statusflag))
            {
                query.append(" and b."+statusflag+"='Y'");
            }*/
            if ( functions.isValueNull(fdtstamp))
            {
                query.append(" and t.transactionTime >= ? ");
            }

            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and t.transactionTime <= ? ");
            }

            if (functions.isValueNull(desc))
            {
              //  query.append(" and t.description like % ? %");
                query.append(" and t.description= ? ");
            }
            if (functions.isValueNull(trackingid))
            {
                query.append(" and t.trackingid= ? ");
            }

            /*if(functions.isValueNull(issuingbank))
            {
                query.append(" and b.issuing_bank= ? ");
            }
*/
            if(functions.isValueNull(gate_name))
            {
                query.append(" and t.fromtype= ? ");
            }

            if(functions.isValueNull(currency1))
            {

                query.append(" and t.currency= ? ");
            }

            if(functions.isValueNull(accountid1))
            {
                query.append(" and t.accountid= ? ");
            }

            if(functions.isValueNull(firstName))
            {
                query.append(" and t.firstname= ? ");
            }
            if (functions.isValueNull(lastName))
            {
                query.append(" and t.lastname= ? ");
            }
            if(functions.isValueNull(emailAddr))
            {
                query.append(" and t.emailAddr= ? ");
            }
            if (functions.isValueNull(cardtype))
            {
                query.append(" and t.cardtype= ? ");
            }
            if (terminalid != null && !terminalid.equals("") && !terminalid.equals("null"))
            {
                query.append(" and t.terminalid= ? ");
            }
            if(functions.isValueNull(customerId))
            {
                query.append(" and t.customerId= ? ");
            }

            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and captureamount > refundamount");
            }

            StringBuilder countquery = new StringBuilder("select count(*) from ( " );
            countquery.append(query);
            countquery.append(" ) as temp ");

            query.append(" order by transid desc");
            // query.append(" limit ? , ? ");

            pstms       = connection.prepareStatement(query.toString());
            pstms1      = connection.prepareStatement(countquery.toString());

            if (functions.isValueNull(status))
            {
                pstms.setString(counter, status);
                pstms1.setString(counter, status);
                counter++;
            }
            if ( functions.isValueNull(fdtstamp))
            {
                long milliSeconds           = Long.parseLong(fdtstamp + "000");
                Calendar calendar           = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1       = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate            = formatter1.format(calendar.getTime());
                pstms.setString(counter, startDate);
                pstms1.setString(counter, startDate);
                counter++;
            }

            if (functions.isValueNull(tdtstamp))
            {
                long milliSeconds       = Long.parseLong(tdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                pstms.setString(counter, endDate);
                pstms1.setString(counter, endDate);
                counter++;
            }

            if (functions.isValueNull(desc))
            {
                pstms.setString(counter, desc);
                pstms1.setString(counter, desc);
                counter++;
            }
            if (functions.isValueNull(desc))
            {
                pstms.setString(counter, desc);
                pstms1.setString(counter, desc);
                counter++;
            }
            if (functions.isValueNull(trackingid))
            {
                pstms.setString(counter, trackingid);
                pstms1.setString(counter, trackingid);
                counter++;
            }

            /*if (functions.isValueNull(issuingbank))
            {
                pstms.setString(counter, issuingbank);
                pstms1.setString(counter, issuingbank);
                counter++;
            }*/

            if(functions.isValueNull(gate_name))
            {
                pstms.setString(counter, gate_name);
                pstms1.setString(counter, gate_name);
                counter++;
            }

            if(functions.isValueNull(currency1))
            {
                pstms.setString(counter, currency1);
                pstms1.setString(counter, currency1);
                counter++;
            }

            if(functions.isValueNull(accountid1))
            {
                pstms.setString(counter, accountid1);
                pstms1.setString(counter, accountid1);
                counter++;
            }

            if(functions.isValueNull(firstName))
            {
                pstms.setString(counter, firstName);
                pstms1.setString(counter, firstName);
                counter++;
            }
            if (functions.isValueNull(lastName))
            {
                pstms.setString(counter, lastName);
                pstms1.setString(counter, lastName);
                counter++;
            }
            if(functions.isValueNull(emailAddr))
            {
                pstms.setString(counter, emailAddr);
                pstms1.setString(counter, emailAddr);
                counter++;
            }
            if (functions.isValueNull(cardtype))
            {
                pstms.setString(counter, cardtype);
                pstms1.setString(counter, cardtype);
                counter++;
            }
            if (functions.isValueNull(terminalid))
            {
                pstms.setString(counter, terminalid);
                pstms1.setString(counter, terminalid);
                counter++;
            }
            if(functions.isValueNull(customerId))
            {
                pstms.setString(counter, customerId);
                pstms1.setString(counter, customerId);
                counter++;
            }

           /* pstms.setInt(counter, start);
            counter++;
            pstms.setInt(counter, end);*/
logger.error("Query---"+query);
            logger.error("export pstmt:::::"+pstms);
            logger.error("export pstmt1:::::"+pstms1);

            hash    = Database.getHashFromResultSetForTransactionEntry(pstms.executeQuery());
            rs      = pstms1.executeQuery();

            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException se)
        {
            logger.error("SQL Exception leaving listTransactions" ,se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstms);
            Database.closePreparedStatement(pstms1);
            Database.closeConnection(connection);
        }
        logger.debug("Leaving listTransactions"+hash);
        return hash;
    }

    // New Method for Export Transactions(Rejected List)
    public Hashtable listTransactionsForRejected(String fdtstamp, String tdtstamp, String rejectreason, String amount, String email,String toid,String name,String desc, int records, int pageno, Set<String> gatewayTypeSet,String accountid,String terminalid,String firstsix,String lastfour) throws SystemError
    {
        logger.debug("Entering listTransactionsForExport");

        Connection connection   = null;
        ResultSet rs            = null;
        Hashtable hash          = null;
        StringBuffer query1     = new StringBuffer();
        StringBuffer countquery = new StringBuffer();

        Codec me    = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp    = ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp    = ESAPI.encoder().encodeForSQL(me,tdtstamp);
        desc        = ESAPI.encoder().encodeForSQL(me,desc);
        int start   = 0; // start index
        int end     = 0; // end index
        start       = (pageno - 1) * records;
        end         = records;
        try
        {
            connection = Database.getConnection();
           // query1.append("SELECT id,toid,totype,dtstamp,description,rejectreason,remark,cardnumber,expirydate,amount,email,firstname,lastname,terminalid,cardtypeid,paymenttypeid,currency,firstsix,lastfour,requestedip from transaction_fail_log where id>0 ");
            query1.append("SELECT * from transaction_fail_log where id>0 ");
            countquery.append("select count(*) from transaction_fail_log as temp where id>0  ");
            if (toid != null && toid != "" && toid != "null")
            {
                query1.append(" and toid in (" + toid + ")");
                countquery.append(" and toid in (" + toid + ")");
            }
            if (fdtstamp != null && fdtstamp != "" && fdtstamp != null)
            {
                query1.append(" and dtstamp >= " + fdtstamp);
                countquery.append(" and dtstamp >= " + fdtstamp);
            }
            if (tdtstamp != null && tdtstamp != "" && tdtstamp != null)
            {
                query1.append(" and dtstamp <= " + tdtstamp);
                countquery.append(" and dtstamp <= " + tdtstamp);
            }
            if (desc != null && desc != "" && desc != "null")
            {
               /* query1.append(" and description like '%" + desc + "%'");
                countquery.append(" and description like '%" + desc + "%'"); */
                query1.append(" and description='" + desc + "'");
                countquery.append(" and description='" + desc + "'");
            }
            if (rejectreason != null && rejectreason != "" && rejectreason != null)
            {
                query1.append(" and rejectreason='" + rejectreason + "'");
                countquery.append(" and rejectreason='" + rejectreason + "'");
            }
            if (amount != null && amount != "" && amount != null)
            {
                query1.append(" and amount='" + amount + "'");
                countquery.append(" and amount='" + amount + "'");
            }
            if (email != null && email != "" && email != null)
            {
                query1.append(" and email='" + email + "'");
                countquery.append(" and email='" + email + "'");
            }
            if (firstsix != null && firstsix !="" && firstsix!="null")
            {
                query1.append(" and firstsix='" + firstsix + "'");
                countquery.append(" and firstsix='" + firstsix + "'");
            }
            if (lastfour != null && lastfour!="" && lastfour!="null")
            {
                query1.append(" and lastfour='" + lastfour + "'");
                countquery.append(" and lastfour='" + lastfour + "'");
            }
            if (functions.isValueNull(name))
            {
                String arr[]=name.split(" ");
                String firstName="";
                String lastName="";
                try
                {
                    firstName=arr[0];
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    firstName="";
                }
                try
                {
                    lastName=arr[1];
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    lastName="";
                }
                if(functions.isValueNull(firstName))
                {
                    query1.append(" and firstname = '" + firstName+"'");
                    query1.append(" and lastname = '" + lastName+"'");
                }
                if(functions.isValueNull(lastName))
                {
                    countquery.append(" and firstname = '" + firstName+"'");
                    countquery.append(" and lastname = '" + lastName+"'");
                }
               /* if (firstname != null && firstname != "" && firstname != null)
                {
                    query1.append(" and firstname='" + firstname + "'");
                    countquery.append(" and firstname='" + firstname + "'");
                }
                if (lastname != null && lastname != "" && lastname != null)
                {
                    query1.append(" and lastname='" + lastname + "'");
                    countquery.append(" and lastname='" + lastname + "'");
                }*/
            }

            if (terminalid != null && terminalid != "" && terminalid != null)
            {
                query1.append(" and terminalid='" + terminalid + "'");
                countquery.append(" and terminalid='" + terminalid + "'");
            }

            query1.append(" order by id desc limit " + start + "," + end);
            logger.debug("transaction query..."+query1.toString());
            logger.debug("Count query =="+countquery.toString());

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query1.toString(), connection));
            rs = Database.executeQuery(countquery.toString(), connection);
            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);
            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");
            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException se)
        {
            logger.error("SQL Exception leaving listTransactionsForExport" ,se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(connection);
        }
        return hash;
    }

    public Hashtable listTransactions(String desc, String tdtstamp, String fdtstamp, String trackingid, String status, int records, int pageno, boolean archive, String statusflag, String issuingBank) throws SystemError
    {
        logger.debug("Entering listTransactions");
        if(!Functions.isValidSQL(trackingid) || !Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");
        }
        // logger.debug("trackingid===1===//=in transactionENTRY"+trackingid);
        Hashtable hash = null;
        String tablename = "";
        String fields = "";
        String orderby = "";

        //Encoding for SQL Injection check

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        //status = ESAPI.encoder().encodeForSQL(me,status);
        fdtstamp= ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp=ESAPI.encoder().encodeForSQL(me,tdtstamp);
        desc=ESAPI.encoder().encodeForSQL(me,desc);
        trackingid=ESAPI.encoder().encodeForSQL(me,trackingid);

        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;

        Connection conn=null;
        try
        {
            conn=Database.getConnection();
            tablename = "transaction_icicicredit";
            fields = "icicitransid as transid,status,name,amount,captureamount,refundamount,description,dtstamp";
            orderby = " order by dtstamp ,icicitransid ASC";

            if (archive)
            {
                tablename += "_archive";
            }
            StringBuffer query = new StringBuffer("select " + fields + " from " + tablename + " where");
            query.append(" toid ='" + merchantid + "'");
            if (status != null)
                query.append(" and status='" + status + "'");
            if (fdtstamp != null)
                query.append(" and dtstamp >= " + fdtstamp);
            if (tdtstamp != null)
                query.append(" and dtstamp <= " + tdtstamp);
            if (desc != null)
                query.append(" and description like '%" + desc + "%'");
            if (trackingid != null)
                query.append(" and icicitransid=" + trackingid);

            query.append(orderby);
            query.append(" limit " + start + "," + end);
            logger.debug("Fatch records from transaction ");

            StringBuffer countquery = new StringBuffer("select count(*) from " + tablename + " where");
            countquery.append(" toid ='" + merchantid + "'");
            if (status != null)
                countquery.append(" and status='" + status + "'");
            if (fdtstamp != null)
                countquery.append(" and dtstamp >= " + fdtstamp);
            if (tdtstamp != null)
                countquery.append(" and dtstamp <= " + tdtstamp);
            if (desc != null)
                countquery.append(" and description like '%" + desc + "%'");
            if (trackingid != null)
                countquery.append(" and icicitransid=" + trackingid);

            logger.debug("Count query");

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            //logger.debug("hash===1===//=in transactionENTRY"+hash);
            ResultSet rs = Database.executeQuery(countquery.toString(), conn);

            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException se)
        {   logger.error("SQL Exception leaving listTransactions" ,se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        logger.debug("Leaving listTransactions");
        return hash;
    }

    public Hashtable listPresentCardTransactions(String desc, String tdtstamp, String fdtstamp,String trackingid, String status,String timestamp, int records, int pageno, boolean archive, Set<String> gatewayTypeSet, String firstName, String lastName, String emailAddress,String dateType,String customerId, String terminalid, String statusflag, String issuingBank,String firstSix,String lastFour,String successtimestamp,String failuretimestamp,String refundtimestamp,String payouttimestamp,String chargebacktimestamp) throws SystemError
    {
        logger.debug("Entering listPresentCardTransactions");

        if(!Functions.isValidSQL(trackingid) || !Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");

        }
        Connection connection=null;
        Hashtable hash = null;
        String tablename = "";
        String fields = "";
        String pRefund = "false";
        StringBuffer query = new StringBuffer();
        //Encoding for SQL Injection check

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp= ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp=ESAPI.encoder().encodeForSQL(me,tdtstamp);
        desc=ESAPI.encoder().encodeForSQL(me,desc);
        trackingid=ESAPI.encoder().encodeForSQL(me,trackingid);

        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;

        if(gatewayTypeSet.size()==0)
        {
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }

        try
        {
            Iterator i = gatewayTypeSet.iterator();
            connection=Database.getConnection();
            tablename = "transaction_card_present";
            if (archive)
            {
                //tablename += "_archive";        //ToDO will be used once all the archive tables will be avialbale.
                tablename = "transaction_common_archive";
            }
            fields = "t.trackingid as transid,t.status,t.name,t.amount,t.captureamount,t.refundamount,t.payoutamount,t.walletAmount,t.walletCurrency,t.description,t.orderdescription,t.dtstamp,t.timestamp,t.paymodeid,t.cardtype,t.cardtypeid,t.accountid,t.currency,t.remark,t.successtimestamp,t.failuretimestamp,t.refundtimestamp,t.payouttimestamp,t.chargebacktimestamp,t.ccnum,t.paymentid,t.customerId,t.toid,t.emailaddr,t.terminalid,t.chargebackinfo,t.arn,t.rrn";
            //query.append("select " + fields + " from " + tablename + " as t LEFT JOIN bin_details as b on t.trackingid = b.icicitransid where t.trackingid>0 ");
            //query.append("select " + fields + " from " + tablename + " as t where t.trackingid>0 ");
            query.append("select " + fields + " from " + tablename + " as t where ");
            query.append(" t.toid ='" + merchantid + "'");
            if (status != null)
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status = "reversed";
                    pRefund = "true";
                }
                query.append(" and t.status='" + status + "'");
            }
           /* if (!"all".equalsIgnoreCase(statusflag))
            {
                query.append(" and b."+statusflag+"='Y'");
            }*/
            if ( functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                query.append(" and t.transactionTime >= '" + startDate + "'");
            }

            if ( functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter2.format(calendar.getTime());
                query.append(" and t.transactionTime <= '" + endDate + "'");
            }

            if (desc != null)
            {
                query.append(" and t.description like '%" + desc + "%'");
            }
            if (trackingid != null && !trackingid.equalsIgnoreCase(""))
            {
                query.append(" and t.trackingid=" + trackingid);
            }
          /*  if (functions.isValueNull(gateway_name))
            {
                query.append(" and t.fromtype= '"+gateway_name+"'");
            }*/
            /*if (functions.isValueNull(issuingBank))
            {
                query.append(" and b.issuing_bank= '"+issuingBank+"'");
            }
            if (functions.isValueNull(firstSix))
            {
                query.append(" and b.first_six= '"+firstSix+"'");
            }
            if (functions.isValueNull(lastFour))
            {
                query.append(" and b.last_four= '"+lastFour+"'");
            }*/
            if(functions.isValueNull(firstName))
            {
                query.append(" and t.firstname= '" + firstName+"'");
            }
            if(functions.isValueNull(lastName))
            {
                query.append(" and t.lastname= '"+lastName+"'");
            }
            if(functions.isValueNull(emailAddress))
            {
                query.append(" and t.emailaddr= '"+emailAddress+"'");
            }
            if(functions.isValueNull(customerId))
            {
                query.append(" and t.customerId= '"+customerId+"'");
            }
            if(!terminalid.equals("all"))
            {
                query.append(" and t.terminalid =" + terminalid);
            }
            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and t.captureamount > t.refundamount");
            }
            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");
            query.append(" order by transid desc");
            //query.append(" limit " + start + "," + end);
            logger.error("transaction query inside exporttoexcel..."+query);
            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), connection));
            ResultSet rs = Database.executeQuery(countquery.toString(), connection);

            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException se)
        {
            logger.error("SQL Exception leaving listPresentCardTransactions" ,se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        logger.debug("Leaving listPresentCardTransactions");
        return hash;
    }

    public Hashtable listCardTransactions(String desc, String tdtstamp, String fdtstamp, String trackingid, String status, int records, int pageno, boolean archive,Set<String> gatewayTypeSet,String accountid,TerminalVO terminalVO,String firstName,String lastName,String emailAddress,String paymentId, String customerId,String dateType,String statusflag,String issuingBank , String firstsix, String lastfour ) throws SystemError
    {
        logger.debug("Entering listCardTransactions");
        if(!Functions.isValidSQL(trackingid) || !Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");

        }
        Connection connection   =null;
        Hashtable hash          = null;
        String tablename        = "";
        String fields           = "";
        String orderby          = "";
        String pRefund          = "false";
        StringBuffer query      = new StringBuffer();
        //Encoding for SQL Injection check
        Codec me    = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp    = ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp    = ESAPI.encoder().encodeForSQL(me,tdtstamp);
        desc        = ESAPI.encoder().encodeForSQL(me,desc);
        trackingid  = ESAPI.encoder().encodeForSQL(me,trackingid);

        int start   = 0; // start index
        int end     = 0; // end index
        start       = (pageno - 1) * records;
        end         = records;

        if(gatewayTypeSet.size()==0)
        {
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }

        try
        {

            connection  =Database.getConnection();
            tablename   = "transaction_card_present";
            if (archive)
            {
                //ToDO will be used once all the archive tables will be avialbale.
                tablename = "transaction_common_archive";
            }
            fields = "t.trackingid as transid,t.status,t.name,t.amount,t.captureamount,t.refundamount,t.payoutamount,t.description,t.orderdescription," +
                    "t.dtstamp,t.paymodeid,t.customerId,t.cardtype,t.cardtypeid,t.accountid,t.terminalid,t.currency,t.remark,t.emailaddr,t.timestamp," +
                    "t.transactionTime,t.transaction_mode";
            //query.append("select " + fields + " from " + tablename + " as t where t.trackingid>0 and");
            query.append("select " + fields + " from " + tablename + " as t ");
            query.append("where t.toid ='" + merchantid + "'");
            if (status != null)
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status  = "reversed";
                    pRefund = "true";
                }
                query.append(" and t.status='" + status + "'");
            }
            if (!"all".equalsIgnoreCase(statusflag))
            {
                query.append(" and b."+statusflag+"='Y'");
            }
            if (functions.isValueNull(desc))
                query.append(" and t.description like '" + desc + "%'");

            if (functions.isValueNull(terminalVO.getTerminalId()))
                query.append(" and t.terminalid in " + terminalVO.getTerminalId());

            if (trackingid != null && !trackingid.equalsIgnoreCase(""))
            {
                query.append(" and t.trackingid=" + trackingid);
            }
            if ( functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" and t.transactionTime >= '" + startDate + "'");
            }

            if (functions.isValueNull(tdtstamp))
            {

                long milliSeconds   = Long.parseLong(tdtstamp + "000");
                Calendar calendar   = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and t.transactionTime <= '" + endDate + "'");
            }

            if(functions.isValueNull(paymentId))
            {
                query.append(" and t.paymentid='" + paymentId+"'");
            }

            if(functions.isValueNull(firstName))
            {
                query.append(" and t.firstname= '" + firstName+"'");
            }
            if (functions.isValueNull(lastName))
            {
                query.append(" and t.lastname= '"+lastName+"'");
            }
            if(functions.isValueNull(emailAddress))
            {
                query.append(" and t.emailaddr= '"+emailAddress+"'");
            }
            if(functions.isValueNull(customerId))
            {
                query.append(" and t.customerId= '"+customerId+"'");
            }
            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and t.captureamount > t.refundamount");
            }

            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");

            //query.append(" order by dtstamp ,transid desc");
            query.append(" order by transid desc");

            query.append(" limit " + start + "," + end);
            logger.debug("Fatch records from transaction issuing bank:::"+issuingBank);
            logger.debug("transaction query..."+query);


            logger.debug("Count query =="+countquery.toString());

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), connection));
            //logger.debug("hash===1===//=in transactionENTRY"+hash);
            ResultSet rs = Database.executeQuery(countquery.toString(), connection);

            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException se)
        {   logger.error("SQL Exception leaving listCardTransactions" ,se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        logger.debug("Leaving listCardTransactions");
        return hash;
    }

    public Hashtable listTransactions(String desc, String tdtstamp, String fdtstamp, String trackingid, String status, int records, int pageno, boolean archive,Set<String> gatewayTypeSet,String accountid,TerminalVO terminalVO,String firstName,String lastName,String emailAddress,String paymentId, String customerId,String dateType,String statusflag,String issuingBank , String firstsix, String lastfour ,String transactionMode ) throws SystemError
    {
        logger.debug("Entering listTransactionsNew");
        if(!Functions.isValidSQL(trackingid) || !Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");

        }
        Connection connection=null;
        // logger.debug("trackingid===1===//=in transactionENTRY"+trackingid);
        Hashtable hash      = null;
        String tablename    = "";
        String fields       = "";
        String orderby      = "";
        String pRefund      = "false";
        StringBuffer query  = new StringBuffer();
        //Encoding for SQL Injection check

        Codec me    = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        //status = ESAPI.encoder().encodeForSQL(me,status);
        fdtstamp    = ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp    = ESAPI.encoder().encodeForSQL(me,tdtstamp);
        desc        = ESAPI.encoder().encodeForSQL(me,desc);
        trackingid  = ESAPI.encoder().encodeForSQL(me,trackingid);

        int start   = 0; // start index
        int end     = 0; // end index
        start       = (pageno - 1) * records;
        end         = records;

        if(gatewayTypeSet.size() == 0)
        {
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }

        try
        {
            //Iterator i = gatewayTypeSet.iterator();
            connection  = Database.getConnection();
            //connection=Database.getRDBConnection();
            /*while(i.hasNext())
            {*/
            //tablename = "transaction_icicicredit";

            tablename   = "transaction_common";
            if (archive)
            {
                //tablename += "_archive";        //ToDO will be used once all the archive tables will be avialbale.
                tablename = "transaction_common_archive";
            }
                /*if(tablename.equals("transaction_icicicredit")||tablename.equals("transaction_icicicredit_archive"))
                {
                    fields = "icicitransid as transid,status,name,amount,captureamount,refundamount,payoutamount,description,orderdescription,dtstamp,cardtype,cardtypeid,accountid";
                }
                else
                {*/
            fields = "t.trackingid as transid,t.status,t.name,t.amount,t.captureamount,t.refundamount,t.payoutamount,t.description,t.orderdescription," +
                    "t.dtstamp,t.paymodeid,t.customerId,t.cardtype,t.cardtypeid,t.accountid,t.terminalid,t.currency,t.remark,t.emailaddr,t.timestamp," +
                    "b.issuing_bank,t.transaction_mode";
            // }

            query.append("select " + fields + " from " + tablename + " as t LEFT JOIN bin_details as b on t.trackingid = b.icicitransid ");
            //query.append("select " + fields + " from " + tablename + " as t LEFT JOIN bin_details as b on t.trackingid = b.icicitransid where t.trackingid>0 and");
            query.append(" where t.toid ='" + merchantid + "'");
            if (status != null)
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status  = "reversed";
                    pRefund = "true";
                }
                query.append(" and t.status='" + status + "'");
            }
            if (!"all".equalsIgnoreCase(statusflag))
            {
                query.append(" and b."+statusflag+"='Y'");
            }
            if (functions.isValueNull(desc))
                query.append(" and t.description like '" + desc + "%'");
                /*if (functions.isValueNull(terminalVO.getTerminalId()))
                    query.append(" and terminalid in " + terminalVO.getTerminalId());*/
                /*if (accountid!=null && !accountid.equals("") && !accountid.equals("null"))
                    query.append(" and accountid =" + accountid);*/

            if (functions.isValueNull(terminalVO.getTerminalId()))
                query.append(" and t.terminalid in " + terminalVO.getTerminalId());

            if (trackingid != null && !trackingid.equalsIgnoreCase(""))
            {
                    /*if(tablename.equals("transaction_icicicredit")||tablename.equals("transaction_icicicredit_archive"))
                    {
                        query.append(" and icicitransid=" + trackingid);
                    }
                    else
                    {*/
                query.append(" and t.trackingid=" + trackingid);
                //}
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds       = Long.parseLong(fdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" and t.timestamp >= '" + startDate + "'");
            }
            else if (functions.isValueNull(fdtstamp))
            {
                query.append(" and t.dtstamp >= " + fdtstamp);
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {

                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and t.timestamp <= '" + endDate + "'");
            }
            else if(functions.isValueNull(tdtstamp))
            {
                query.append(" and t.dtstamp <= " + tdtstamp);
            }
            if(functions.isValueNull(paymentId))
            {
                   /* if(tablename.equals("transaction_icicicredit"))
                    {
                        query.append(" and paymentid='" + paymentId+"'");
                    }
                    else if(tablename.equals("transaction_qwipi"))
                    {
                        query.append(" and qwipiPaymentOrderNumber='" + paymentId+"'");
                    }
                    else if(tablename.equals("transaction_ecore"))
                    {
                        query.append(" and ecorePaymentOrderNumber='" + paymentId+"'");
                    }
                    else
                    {*/
                query.append(" and t.paymentid='" + paymentId+"'");
                //}
            }

            if(functions.isValueNull(firstName))
            {
                query.append(" and t.firstname= '" + firstName+"'");
            }
            if (functions.isValueNull(lastName))
            {
                query.append(" and t.lastname= '"+lastName+"'");
            }
            if(functions.isValueNull(emailAddress))
            {
                query.append(" and t.emailaddr= '"+emailAddress+"'");
            }
            if(functions.isValueNull(customerId))
            {
                query.append(" and t.customerId= '"+customerId+"'");
            }
            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and t.captureamount > t.refundamount");
            }

            if (functions.isValueNull(issuingBank))
            {
                query.append(" and b.issuing_bank= '"+issuingBank+"'");
            }

            if (functions.isValueNull(firstsix))
            {
                query.append(" and b.first_six= '"+firstsix+"'");
            }

            if (functions.isValueNull(lastfour))
            {
                query.append(" and b.last_four= '"+lastfour+"'");
            }
            if (functions.isValueNull(transactionMode))
            {
                query.append(" and t.transaction_mode= '"+transactionMode.trim()+"'");
            }
            //appand terminalid list if desc+trackingid+paymentid not null
                /*if(!functions.isValueNull(desc) && !functions.isValueNull(trackingid) && !functions.isValueNull(paymentId))
                    query.append(" and terminalid IN "+terminalVO.getTerminalId());*/

                /*if(i.hasNext())
                    query.append(" UNION ");

            }*/


            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");

            //query.append(" order by dtstamp ,transid desc");
            query.append(" order by transid desc");

            query.append(" limit " + start + "," + end);
            logger.debug("Fatch records from transaction issuing bank:::"+issuingBank);
            logger.debug("transaction query....."+query);


            logger.debug("Count query =="+countquery.toString());

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), connection));
            //logger.debug("hash===1===//=in transactionENTRY"+hash);
            ResultSet rs = Database.executeQuery(countquery.toString(), connection);

            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException se)
        {   logger.error("SQL Exception leaving listTransactions" ,se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        logger.debug("Leaving listTransactions");
        return hash;
    }
    public int creditTransaction(int toid, String merchantid, String description, BigDecimal amt, BigDecimal chargeper, String transfixfee, GatewayAccount account, String taxper, int trackingId) throws SystemError
    {
        Connection conn=null;
        try
        {
            String query;
            int result;
            BigDecimal peramt;
            int paymentTransId;

            conn=Database.getConnection();
            Database.setAutoCommit(conn, false);
            transactionEntry(String.valueOf(toid), merchantid, TYPE_PZ, TYPE_ICICICREDIT, String.valueOf(amt.doubleValue()), description, CREDIT);
            //result = stmt.executeUpdate(query);

            peramt = amt.multiply(chargeper.multiply(new BigDecimal(1 / 10000.00)));
            peramt = peramt.add(new BigDecimal(transfixfee));
            peramt = peramt.setScale(2, BigDecimal.ROUND_HALF_UP);


            String tax_toid = String.valueOf(account.getTaxAccount());
            String charges_toid = String.valueOf(account.getChargesAccount());

            transactionEntry(charges_toid, String.valueOf(toid), TYPE_PZ, TYPE_PZ, String.valueOf(peramt.doubleValue()), "Charges for (" + description + ")", CREDIT_CHARGES);
            //result = stmt.executeUpdate(query);

            query = "select max(transid) as transid from transactions";
            ResultSet rsChargesTransid = Database.executeQuery(query, conn);
            String chargesTransId = null;
            if (rsChargesTransid.next())
            {
                chargesTransId = rsChargesTransid.getString("transid");
            }

            BigDecimal bdConst = new BigDecimal("0.01");
            BigDecimal taxPercentage = new BigDecimal(taxper).multiply(bdConst);
            BigDecimal calculatedTax = calculateTax(peramt.toString(), taxPercentage.toString());

            taxEntry(toid + "", calculatedTax.toString(), "Service Tax on Charges @" + taxPercentage + "% (transid - " + chargesTransId + ")", tax_toid, TAX_CREDIT_CHARGES);

            query = "select transid from transactions where toid=? and fromid=? and description=?";

            logger.debug("Select Query from creditTransaction : " );
            PreparedStatement p=conn.prepareStatement(query);
            p.setInt(1,toid);
            p.setString(2,merchantid);
            p.setString(3,description);
            ResultSet rs1 = p.executeQuery();

            if (rs1.next())
            {
                paymentTransId = rs1.getInt(1);
                query = "update transaction_icicicredit set transid=?,status='settled',chargeper=? where icicitransid=?";
                PreparedStatement ps=conn.prepareStatement(query);
                ps.setInt(1,paymentTransId);
                ps.setInt(2,chargeper.intValue());
                ps.setInt(3,trackingId);
                result = ps.executeUpdate();
                logger.debug("Number of records updated: ");
            }
            else
            {
                throw new SystemError("Error while executing query");
            }
            Database.commit(conn);
            //Start : Added for Action and Status Entry in Action History table
            ActionEntry entry = new ActionEntry();
            int actionEntry = entry.actionEntry(String.valueOf(trackingId),String.valueOf(amt.doubleValue()),ActionEntry.ACTION_CREDIT,ActionEntry.STATUS_CREDIT);
            entry.closeConnection();
            // End : Added for Action and Status Entry in Action History table
            return paymentTransId;
        }
        catch (SQLException e)
        {
            Database.rollback(conn);
            throw new SystemError("Error while executing query");
        }
        finally
        {
            //Database.rollback(conn);
            Database.setAutoCommit(conn, true);
            Database.closeConnection(conn);
        }
    }
    public int newGenericCreditTransaction(String trackingId,BigDecimal amt,String accountId,GenericResponseVO settlementDetails,AuditTrailVO auditTrailVO) throws SystemError
    {
        Connection conn=null;
        try
        {
            conn=Database.getConnection();
            logger.debug("Inside newGenericCreditTransaction");
            //getting Transaction Details
            Hashtable transDetails = getTransDetails(trackingId);
            if(transDetails==null)
            {
                throw new SystemError("Transaction Not found for given tracking Id"+trackingId);
            }
            logger.debug("TransDetails found");
            accountId = (String)transDetails.get("accountid");
            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            String memberid = (String)transDetails.get("toid");
            String merchantid = account.getMerchantId();
            String merchanttype = account.getDisplayName();
            String description = (String)transDetails.get("description");
            String paymodeid =(String)transDetails.get("paymodeid");
            String cardtypeid =(String)transDetails.get("cardtypeid");
            String amount =null;
            if(amt !=null)
            {
                amount = String.valueOf(amt.doubleValue());
            }
            else
            {
                amount =(String)transDetails.get("captureamount");
                amt = new BigDecimal(amount);
            }

            //Getting charges from Mapping Table
            Hashtable chargeDetails = getChargesFromMappingTable(memberid,accountId,paymodeid,cardtypeid);
            if(chargeDetails==null)
            {
                throw new SystemError("Charges Not found in mapping table ");
            }

            logger.debug("Charges found in mapping table");

            String chargePercentage = (String)chargeDetails.get("chargePercentage");
            String fixApprovalCharge = (String)chargeDetails.get("fixApprovalCharge");
            //String fixDeclinedCharge = (String)chargeDetails.get("fixDeclinedCharge");
            String taxper = (String)chargeDetails.get("taxper");
            //String reversalcharge = (String)chargeDetails.get("reversalcharge");
            //String withdrawalcharge = (String)chargeDetails.get("withdrawalcharge");
            //String chargebackcharge = (String)chargeDetails.get("chargebackcharge");
            //String reservePercentage = (String)chargeDetails.get("reservePercentage");
            String fraudVerificationCharge = (String)chargeDetails.get("fraudVerificationCharge");
            //String fraudulentCharge = (String)chargeDetails.get("fraudulentCharge");

            logger.debug(" chargePercentage::"+chargePercentage+" fixApprovalCharge::"+fixApprovalCharge+" taxper::"+taxper+" fraudVerificationCharge::"+fraudVerificationCharge);
            String query;
            int result;
            BigDecimal peramt;
            int paymentTransId;
            String table_name = Database.getTableName(account.getGateway());
            Database.setAutoCommit(conn, false);
            // Inserting a transaction in the transactions table, transfering  amount from pz account to member account.
            transactionEntry(memberid, merchantid, TYPE_PZ, merchanttype, amount, description, CREDIT);
            logger.debug("transfering  amount from pz account to member account");
            // Inserting a transaction in the transactions table, transfering  charges from member account to pz account.
            peramt = amt.multiply(new BigDecimal(chargePercentage).multiply(new BigDecimal(1 / 10000.00)));
            peramt = peramt.add(new BigDecimal(fixApprovalCharge));
            peramt = peramt.add(new BigDecimal(fraudVerificationCharge));
            //peramt = peramt.add(amt.multiply(new BigDecimal(fxClearanceChargePercentage).multiply(new BigDecimal(1 / 100.00))));
            peramt = peramt.setScale(2, BigDecimal.ROUND_HALF_UP);

            String tax_toid = String.valueOf(account.getTaxAccount());
            String charges_toid = String.valueOf(account.getChargesAccount());

            transactionEntry(charges_toid, memberid, TYPE_PZ, TYPE_PZ, String.valueOf(peramt.doubleValue()), "Charges for (" + description + ")", CREDIT_CHARGES);

            logger.debug("transfering  charges from member account to pz account");

            query = "select max(transid) as transid from transactions";
            ResultSet rsChargesTransid = Database.executeQuery(query, conn);
            String chargesTransId = null;
            if (rsChargesTransid.next())
            {
                chargesTransId = rsChargesTransid.getString("transid");
            }

            BigDecimal bdConst = new BigDecimal("0.01");
            BigDecimal taxPercentage = new BigDecimal(taxper).multiply(bdConst);
            BigDecimal calculatedTax = calculateTax(peramt.toString(), taxPercentage.toString());

            taxEntry(memberid, calculatedTax.toString(), "Service Tax on Charges @" + taxPercentage + "% (transid - " + chargesTransId + ")", tax_toid, TAX_CREDIT_CHARGES);

            logger.debug("transfering  taxes from member account to pz account");


            query = "select transid from transactions where toid=? and fromid=? and description=?";

            logger.debug("Select Query from creditTransaction : " );
            PreparedStatement p=conn.prepareStatement(query);
            p.setString(1,memberid);
            p.setString(2,merchantid);
            p.setString(3,description);
            ResultSet rs1 = p.executeQuery();

            if (rs1.next())
            {
                paymentTransId = rs1.getInt(1);
                if(table_name.equals("transaction_icicicredit"))
                {
                    query = "update transaction_icicicredit set transid=?,status='settled' where icicitransid=?";
                }
                else
                {
                    query = "update "+table_name+" set transid=?,status='settled' where trackingid=?";
                }
                PreparedStatement ps=conn.prepareStatement(query);
                ps.setInt(1,paymentTransId);
                //ps.setInt(2,new Integer(chargePercentage).intValue());
                ps.setInt(2,new Integer(trackingId).intValue());
                result = ps.executeUpdate();
                logger.debug("Number of records updated: ");
            }
            else
            {
                throw new SystemError("Error while executing query");
            }
            Database.commit(conn);
            //Start : Added for Action and Status Entry in Action History table
            ActionEntry entry = new ActionEntry();
            //System.out.println("action name transactuion entry---"+auditTrailVO.getActionExecutorName());
            int actionEntry = entry.genericActionEntry(trackingId,amount,ActionEntry.ACTION_CREDIT,ActionEntry.STATUS_CREDIT,null,account.getGateway(),settlementDetails,auditTrailVO);
            logger.debug("ActionExecutorId 1---------"+auditTrailVO.getActionExecutorId());
            entry.closeConnection();
            logger.debug("Action Entry done");
            // End : Added for Action and Status Entry in Action History table
            return paymentTransId;
        }
        catch (SQLException e)
        {
            Database.rollback(conn);
            logger.error("SQLException---",e);
            throw new SystemError("Error while executing query"+e.getMessage());
        }
               /*catch (Exception e)
               {
                   Database.rollback(cn);
                   throw new SystemError("Error while executing query"+e.getMessage());
               }*/
        finally
        {
            //Database.rollback(cn);
            Database.setAutoCommit(conn, true);
            Database.closeConnection(conn);
        }
    }
    public void newGenericFailedTransaction(String trackingId,String accountId,GenericResponseVO settlementDetails)throws SystemError
    {
        Connection conn=null;
        try
        {
            conn=Database.getConnection();
            logger.debug("Inside newGenericFailedTransaction");
            //getting Transaction Details
            Hashtable transDetails = getTransDetails(trackingId);
            if(transDetails==null)
            {
                throw new SystemError("Transaction Not found for given tracking Id"+trackingId);
            }
            logger.debug("TransDetails found");
            accountId = (String)transDetails.get("accountid");
            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            String memberid = (String)transDetails.get("toid");
            String merchantid = account.getMerchantId();
            String merchanttype = account.getDisplayName();
            String description = (String)transDetails.get("description");
            String paymodeid =(String)transDetails.get("paymodeid");
            String cardtypeid =(String)transDetails.get("cardtypeid");

            //Getting charges from Mapping Table
            Hashtable chargeDetails = getChargesFromMappingTable(memberid,accountId,paymodeid,cardtypeid);
            if(chargeDetails==null)
            {
                throw new SystemError("Charges Not found in mapping table ");
            }

            logger.debug("Charges found in mapping table");


            String fixDeclinedCharge = (String)chargeDetails.get("fixDeclinedCharge");
            String taxper = (String)chargeDetails.get("taxper");
            String fraudVerificationCharge = (String)chargeDetails.get("fraudVerificationCharge");


            String query;
            int result;
            BigDecimal peramt;
            int paymentTransId;
            String table_name = Database.getTableName(account.getGateway());
            Database.setAutoCommit(conn, false);
            // Inserting a transaction in the transactions table, transfering  charges from member account to pz account.
            peramt = new BigDecimal(fixDeclinedCharge);
            peramt = peramt.add(new BigDecimal(fraudVerificationCharge));
            peramt = peramt.setScale(2, BigDecimal.ROUND_HALF_UP);
            String tax_toid = String.valueOf(account.getTaxAccount());
            String charges_toid = String.valueOf(account.getChargesAccount());
            transactionEntry(charges_toid, memberid, TYPE_PZ, TYPE_PZ, String.valueOf(peramt.doubleValue()), "Failed Transaction  charges of (" + description + ")", AUTHFAILED_CHARGES);
            logger.debug("transfering  charges from member account to pz account");
            query = "select max(transid) as transid from transactions";
            ResultSet rsChargesTransid = Database.executeQuery(query, conn);
            String chargesTransId = null;
            if (rsChargesTransid.next())
            {
                chargesTransId = rsChargesTransid.getString("transid");
            }

            BigDecimal bdConst = new BigDecimal("0.01");
            BigDecimal taxPercentage = new BigDecimal(taxper).multiply(bdConst);
            BigDecimal calculatedTax = calculateTax(peramt.toString(), taxPercentage.toString());
            taxEntry(memberid, calculatedTax.toString(), "Service Tax on Charges @" + taxPercentage + "% (transid - " + chargesTransId + ")", tax_toid, TAX_CREDIT_CHARGES);
            logger.debug("transfering  taxes from member account to pz account");
            //Updating transid for failed transactions
            if(table_name.equals("transaction_icicicredit"))
            {
                query = "update transaction_icicicredit set transid=? where icicitransid=?";
            }
            else
            {
                query = "update "+table_name+" set transid=? where trackingid=?";
            }
            PreparedStatement ps=conn.prepareStatement(query);
            ps.setInt(1,Integer.parseInt(chargesTransId));
            ps.setInt(2,Integer.parseInt(trackingId));
            result = ps.executeUpdate();
            logger.debug("Number of records updated: ");
            Database.commit(conn);
            logger.debug("Leaving newGnericFailedTransaction");
        }
        catch(SQLException e)
        {
            Database.rollback(conn);
            throw new SystemError("Error while executing query"+e.getMessage());
        }
        catch(Exception e)
        {
            Database.rollback(conn);
            throw new SystemError("Error while executing query"+e.getMessage());
        }
        finally
        {
            //Database.rollback(cn);
            Database.setAutoCommit(conn, true);
            Database.closeConnection(conn);
        }
    }
    public void reverseTransaction(String refundamount, String icicitransid, String merchantid, String icicimerchantid, String description, int chargeper, GatewayAccount account, String rsdescription, String transid, int currtaxper, BigDecimal bdConst, int transactiontaxper, String reversalCharges, String memberId)throws SystemError
    {
        Connection conn=null;
        try
        {
            conn=Database.getConnection();
            Database.setAutoCommit(conn, false);
            logger.debug("update record for reverse transection in reverseTransaction ");
            String query = "update transaction_icicicredit set status='markedforreversal',refundamount=? where icicitransid=? and status='settled' and toid=? and captureamount>=?";

            PreparedStatement p=conn.prepareStatement(query);
            p.setString(1,refundamount);
            p.setString(2,icicitransid );
            p.setString(3,merchantid);
            p.setString(4, refundamount);
            int result = p.executeUpdate();
            logger.debug("No of Rows updated : " + result + "<br>");
            transactionEntry(icicimerchantid, String.valueOf(memberId), TYPE_ICICICREDIT, TYPE_PZ, refundamount, description, REVERSAL);
            logger.debug("description and amount is entered in select query");
            query = "select transid from transactions where fromid=? and description=? and amount=?";
            PreparedStatement ps=conn.prepareStatement(query);
            ps.setString(1,memberId);
            ps.setString(2,description);
            ps.setString(3,refundamount);
            ResultSet rs1 = ps.executeQuery();
            rs1.next();
            int newtransid = rs1.getInt(1);

            BigDecimal peramt = new BigDecimal(refundamount);
            peramt = peramt.multiply(new BigDecimal(chargeper / 10000.00)).setScale(2, BigDecimal.ROUND_HALF_EVEN);

            String tax_toid = String.valueOf(account.getTaxAccount());
            String charges_toid = String.valueOf(account.getChargesAccount());

            transactionEntry(merchantid, charges_toid, TYPE_PZ, TYPE_PZ, peramt.toString(), "Reversal of charges of " + newtransid + "(" + rsdescription + ")", REVERSAL_OF_CREDIT_CHARGES);

            int transidForTaxEntry = Integer.parseInt(transid) + 2;
            BigDecimal currentTaxPercentage = new BigDecimal(currtaxper).multiply(bdConst);
            BigDecimal transactionTaxPercentage = new BigDecimal(transactiontaxper).multiply(bdConst);
            BigDecimal taxToBeReversed = calculateTax(peramt.toString(), transactionTaxPercentage.toString());
            BigDecimal calculatedTax = calculateTax(reversalCharges, currentTaxPercentage.toString());

            // The reverse Tax entry should only be made if tax was charged at the time of transaction.
            query = "select * from transactions where type = 'taxoncreditcharges' and transid =?";
            PreparedStatement pst=conn.prepareStatement(query);
            pst.setInt(1,transidForTaxEntry);
            ResultSet rstaxEntry = pst.executeQuery();
            if (rstaxEntry.next())
            {
                // Since this a reversal of tax the tax_toid will be the fromid
                taxEntry(tax_toid, taxToBeReversed.toString(), "Reversal of Service Tax on Charges @" + transactionTaxPercentage + "% (transid - " + transidForTaxEntry + " ) ( " + rsdescription + " ) ", memberId, REVERSAL_OF_TAX_CREDIT_CHARGES);
            }

            transactionEntry(charges_toid, merchantid, TYPE_PZ, TYPE_PZ, reversalCharges, "charges for reversal of " + newtransid + " (" + rsdescription + ")", REVERSAL_CHARGES);

            query = "select max(transid) as transid from transactions";

            ResultSet rsChargesTransid = Database.executeQuery(query, conn);
            String chargesTransId = null;
            if (rsChargesTransid.next())
            {
                chargesTransId = rsChargesTransid.getString("transid");
            }

            taxEntry(memberId + "", calculatedTax.toString(), "Service Tax on ReversalCharges @" + currentTaxPercentage + "% (transid - " + chargesTransId + " )", tax_toid, TAX_REVERSAL_CHARGES);
            Database.commit(conn);

            if(description.contains("Fraudulent Transaction"))
            {

                ActionEntry entry = new ActionEntry();
                int actionEntry = entry.actionEntry(icicitransid,refundamount,ActionEntry.ACTION_REVERSAL_REQUEST_SENT_FRAUD,ActionEntry.STATUS_REVERSAL_REQUEST_SENT_FRAUD);
                entry.closeConnection();

            }
            else
            {
                ActionEntry entry = new ActionEntry();
                int actionEntry = entry.actionEntry(icicitransid,refundamount,ActionEntry.ACTION_REVERSAL_REQUEST_SENT,ActionEntry.STATUS_REVERSAL_REQUEST_SENT);
                entry.closeConnection();

            }
        }
        catch (SQLException e)
        {
            Database.rollback(conn);
            logger.error("SQL Exception occure in reverseTransaction",e);
            throw new SystemError("Error while executing query..");
        }
        finally
        {
            //Database.rollback(conn);
            Database.setAutoCommit(conn,true);
            Database.closeConnection(conn);
        }
    }
    public void newGenericRefundTransaction(String trackingId,BigDecimal refundamount,String accountId,String rfdescription,GenericResponseVO refundDetails,AuditTrailVO auditTrailVO) throws SystemError
    {
        Connection conn=null;
        try
        {
            conn=Database.getConnection();
            Database.setAutoCommit(conn, false);
            logger.debug("Inside newGenericRefundTransaction");
            //getting Transaction Details
            Hashtable transDetails = getTransDetails(trackingId);
            if(transDetails==null)
            {
                throw new SystemError("Transaction Not found for given tracking Id"+trackingId);
            }
            logger.debug("TransDetails found");
            accountId = (String)transDetails.get("accountid");
            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            String currencyCode=account.getCurrency();
            String memberid = (String)transDetails.get("toid");
            String merchantid = account.getMerchantId();
            String merchanttype = account.getDisplayName();
            String description =(String)transDetails.get("description");
            String paymodeid =(String)transDetails.get("paymodeid");
            String cardtypeid =(String)transDetails.get("cardtypeid");
            String cpamount=(String)transDetails.get("captureamount");
            String transid=(String)transDetails.get("transid");
            String status=(String)transDetails.get("status");
            String rfamount =null;
            if(refundamount !=null)
            {
                rfamount = String.valueOf(refundamount.doubleValue());
            }
            else
            {
                rfamount =cpamount;
                refundamount = new BigDecimal(rfamount);
            }

            BigDecimal captureamount = new BigDecimal(cpamount);
            refundamount=refundamount.setScale(2,BigDecimal.ROUND_HALF_EVEN);
            if(captureamount.compareTo(refundamount) ==-1)
            {
                throw new SystemError("Cannot Refund Transaction as Refund Amount is greater than Capture Amount for Tracking Id"+trackingId);
            }

            if(status.equals("capturesuccess"))
            {
                newGenericCreditTransaction(trackingId,null,accountId,null,auditTrailVO);
                logger.debug("getting transid for settled transaction");
                String query = "select transid from transactions where fromid=? and description=? and amount=?";
                PreparedStatement ps=conn.prepareStatement(query);
                ps.setString(1,merchantid);
                ps.setString(2,description);
                ps.setString(3,rfamount);
                ResultSet rs1 = ps.executeQuery();
                if(rs1.next())
                {
                    transid = rs1.getString("transid");
                }
            }

            //Getting charges from Mapping Table
            Hashtable chargeDetails = getChargesFromMappingTable(memberid,accountId,paymodeid,cardtypeid);
            if(chargeDetails==null)
            {
                throw new SystemError("Charges Not found in mapping table ");
            }

            logger.debug("Charges found in mapping table");
            String taxper = (String)chargeDetails.get("taxper");
            String reversalcharge = (String)chargeDetails.get("reversalcharge");
            //Database.setAutoCommit(conn, false);
            String table_name = Database.getTableName(account.getGateway());
            logger.debug("update record for reverse transection in reverseTransaction ");
            String query;
            if(table_name.equals("transaction_icicicredit"))
                query = "update "+table_name+" set status='markedforreversal',refundamount=? where icicitransid=? and status IN ('settled','capturesuccess') and toid=? and captureamount>=?";
            else
                query = "update "+table_name+" set status='markedforreversal',refundamount=? where trackingid=? and status IN ('settled','capturesuccess') and toid=? and captureamount>=?";
            PreparedStatement p=conn.prepareStatement(query);
            p.setString(1,rfamount);
            p.setString(2,trackingId );
            p.setString(3,memberid);
            p.setString(4, rfamount);
            logger.debug("Query: " + query);
            int result = p.executeUpdate();
            logger.debug("No of Rows updated : " + result + "<br>");
            if(rfdescription==null ||rfdescription.equals("") ||rfdescription.equals("null"))
            {
                rfdescription = "Refund of " + transid ;
            }
            // Inserting a transaction in the transactions table, transfering  amount from member account to pz account.
            transactionEntry(merchantid,memberid, merchanttype, TYPE_PZ, rfamount, rfdescription, REVERSAL);
            String tax_toid = String.valueOf(account.getTaxAccount());
            String charges_toid = String.valueOf(account.getChargesAccount());

            // Inserting a transaction in the transactions table, transfering  charges from member account to pz account.
            BigDecimal peramt = new BigDecimal(reversalcharge);
            peramt = peramt.setScale(2, BigDecimal.ROUND_HALF_UP);

            transactionEntry(charges_toid, memberid, TYPE_PZ, TYPE_PZ, String.valueOf(peramt.doubleValue()), "charges for reversal of "+trackingId+"(" + rfdescription + ")", REVERSAL_CHARGES);
            query = "select max(transid) as transid from transactions";

            ResultSet rsChargesTransid = Database.executeQuery(query, conn);
            String chargesTransId = null;
            if (rsChargesTransid.next())
            {
                chargesTransId = rsChargesTransid.getString("transid");
            }

            BigDecimal bdConst = new BigDecimal("0.01");
            BigDecimal taxPercentage = new BigDecimal(taxper).multiply(bdConst);
            BigDecimal calculatedTax = calculateTax(peramt.toString(), taxPercentage.toString());

            taxEntry(memberid + "", calculatedTax.toString(), "Service Tax on ReversalCharges @" + taxper + "% (transid - " + chargesTransId + " )", tax_toid, TAX_REVERSAL_CHARGES);
            Database.commit(conn);
            if(rfdescription.contains("Fraudulent Transaction"))
            {
                ActionEntry entry = new ActionEntry();
                int actionEntry = entry.genericActionEntry(trackingId,rfamount,ActionEntry.ACTION_REVERSAL_REQUEST_SENT_FRAUD,ActionEntry.STATUS_REVERSAL_REQUEST_SENT_FRAUD,null,account.getGateway(),auditTrailVO);
                logger.debug("ActionExecutorId 2---------"+auditTrailVO.getActionExecutorId());
                entry.closeConnection();
            }
            else
            {
                ActionEntry entry = new ActionEntry();
                int actionEntry = entry.genericActionEntry(trackingId,rfamount,ActionEntry.ACTION_REVERSAL_REQUEST_SENT,ActionEntry.STATUS_REVERSAL_REQUEST_SENT,null,account.getGateway(),auditTrailVO);
                logger.debug("ActionExecutorId 3---------"+auditTrailVO.getActionExecutorId());
                entry.closeConnection();

            }
        }
        catch (SQLException e)
        {
            Database.rollback(conn);
            throw new SystemError(e.getMessage());
        }
        finally
        {
            //Database.rollback(cn);
            Database.setAutoCommit(conn, true);
            Database.closeConnection(conn);
        }
    }
    public void newGenericRefundTransactionforCommon(String trackingId,BigDecimal refundamount,String accountId,String rfdescription,GenericResponseVO refundDetails,AuditTrailVO auditTrailVO) throws PZConstraintViolationException,SystemError
    {
        Connection conn = null;
        try
        {
            conn    = Database.getConnection();
            Database.setAutoCommit(conn, false);
            //getting Transaction Details
            Hashtable transDetails = getTransDetails(trackingId);
            if(transDetails == null)
            {
                throw new SystemError("Transaction Not found for given tracking Id"+trackingId);
            }
            accountId               = (String)transDetails.get("accountid");
            GatewayAccount account  = GatewayAccountService.getGatewayAccount(accountId);
            String memberid         = (String)transDetails.get("toid");
            String cpamount         = (String)transDetails.get("captureamount");
            String rfamount         = null;
            if(refundamount != null)
            {
                rfamount = String.valueOf(refundamount.doubleValue());
            }
            else
            {
                rfamount        = cpamount;
                refundamount    = new BigDecimal(rfamount);
            }

            BigDecimal captureamount    = new BigDecimal(cpamount);
            refundamount                = refundamount.setScale(2,BigDecimal.ROUND_HALF_EVEN);
            if(captureamount.compareTo(refundamount) ==-1)
            {
                String error = "Cannot Refund Transaction as Refund Amount is greater than Capture Amount for Tracking Id "+trackingId;
                PZExceptionHandler.raiseConstraintViolationException("TransactionEntry.java","newGenericRefundTransaction()",null,"common",error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
            }

            String query;
            query = "update transaction_common set status='markedforreversal' where trackingid=? and status IN ('settled','capturesuccess','reversed') and toid=? and captureamount>=?";

            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1,trackingId );
            p.setString(2,memberid);
            p.setString(3, rfamount);
            if (isLogEnabled)
                logger.debug("Query: " + query);
            int result = p.executeUpdate();
            if (isLogEnabled)
                logger.debug("No of Rows updated : " + result + "<br>");

            if(rfdescription==null ||rfdescription.equals("") ||rfdescription.equals("null"))
            {
                rfdescription = "Refund of " + trackingId ;
            }
            if(rfdescription.contains("Fraudulent Transaction"))
            {
                ActionEntry entry = new ActionEntry();
                entry.genericActionEntry(trackingId,rfamount,ActionEntry.ACTION_REVERSAL_REQUEST_SENT_FRAUD,ActionEntry.STATUS_REVERSAL_REQUEST_SENT_FRAUD,null,account.getGateway(),auditTrailVO);
                entry.closeConnection();
            }
            else
            {
                ActionEntry entry = new ActionEntry();
                entry.genericActionEntry(trackingId,rfamount,ActionEntry.ACTION_REVERSAL_REQUEST_SENT,ActionEntry.STATUS_REVERSAL_REQUEST_SENT,null,account.getGateway(),auditTrailVO);
                entry.closeConnection();
            }
        }
        catch (SQLException e)
        {
            Database.rollback(conn);
            throw new SystemError(e.getMessage());
        }
        finally
        {
            Database.setAutoCommit(conn, true);
            Database.closeConnection(conn);
        }
    }
    public void refundTransactionForQwipi(String icicitransid, String memberid,String refundamount,String captureamount,String merchantid,String description, int chargeper, int currtaxper,int transactiontaxper, GatewayAccount account, String reversalCharges,BigDecimal bdConst,String transid,String merchanttype,String status)throws SystemError
    {
        Connection conn=null;
        try
        {
            conn=Database.getConnection();
            Database.setAutoCommit(conn, false);
            logger.debug("update record for reverse transection in reverseTransaction ");
            String query = "update transaction_qwipi set status='markedforreversal',refundamount=? where trackingid=? and status IN ('settled','capturesuccess') and toid=? and captureamount>=?";
            PreparedStatement p=conn.prepareStatement(query);
            p.setString(1,refundamount);
            p.setString(2,icicitransid );
            p.setString(3,merchantid);
            p.setString(4, refundamount);
            int result = p.executeUpdate();
            logger.debug("No of Rows updated : " + result + "<br>");
            transactionEntry(merchantid,memberid, merchanttype, TYPE_PZ, refundamount, description, REVERSAL);
            logger.debug("description and amount is entered in select query");
            query = "select transid from transactions where fromid=? and description=? and amount=?";
            PreparedStatement ps=conn.prepareStatement(query);
            ps.setString(1,merchantid);
            ps.setString(2,description);
            ps.setString(3,refundamount);
            ResultSet rs1 = ps.executeQuery();
            rs1.next();
            // int newtransid = rs1.getInt(1);
            //logger.debug("newtransid="+newtransid);
            BigDecimal peramt = new BigDecimal(refundamount);
            peramt = peramt.multiply(new BigDecimal(chargeper / 10000.00)).setScale(2, BigDecimal.ROUND_HALF_EVEN);

            String tax_toid = String.valueOf(account.getTaxAccount());
            String charges_toid = String.valueOf(account.getChargesAccount());
            if(status.equals("settled"))
            {
                //transactionEntry(memberid, charges_toid, TYPE_PZ, TYPE_PZ, peramt.toString(), "Reversal of charges of "+icicitransid+" (" + description + ")", REVERSAL_OF_CREDIT_CHARGES);
            }
            int transidForTaxEntry = Integer.parseInt(transid) + 2;
            BigDecimal currentTaxPercentage = new BigDecimal(currtaxper).multiply(bdConst);
            BigDecimal transactionTaxPercentage = new BigDecimal(transactiontaxper).multiply(bdConst);
            BigDecimal taxToBeReversed = calculateTax(peramt.toString(), transactionTaxPercentage.toString());
            BigDecimal calculatedTax = calculateTax(reversalCharges, currentTaxPercentage.toString());

            // The reverse Tax entry should only be made if tax was charged at the time of transaction.
            query = "select * from transactions where type = 'taxoncreditcharges' and transid =?";
            PreparedStatement pst=conn.prepareStatement(query);
            pst.setInt(1,transidForTaxEntry);
            ResultSet rstaxEntry = pst.executeQuery();
            if (rstaxEntry.next())
            {
                // Since this a reversal of tax the tax_toid will be the fromid
                //taxEntry(tax_toid, taxToBeReversed.toString(), "Reversal of Service Tax on Charges @" + transactionTaxPercentage + "% (transid - " + transidForTaxEntry + " ) ( " + description + " ) ", merchantid, REVERSAL_OF_TAX_CREDIT_CHARGES);
            }

            transactionEntry(charges_toid, memberid, TYPE_PZ, TYPE_PZ, reversalCharges, "charges for reversal of "+icicitransid+"(" + description + ")", REVERSAL_CHARGES);
            query = "select max(transid) as transid from transactions";
            ResultSet rsChargesTransid = Database.executeQuery(query, conn);
            String chargesTransId = null;
            if (rsChargesTransid.next())
            {
                chargesTransId = rsChargesTransid.getString("transid");
            }

            taxEntry(memberid + "", calculatedTax.toString(), "Service Tax on ReversalCharges @" + currentTaxPercentage + "% (transid - " + chargesTransId + " )", tax_toid, TAX_REVERSAL_CHARGES);
            Database.commit(conn);
            if(description.contains("Fraudulent Transaction"))
            {
                ActionEntry entry = new ActionEntry();
                int actionEntry = entry.actionEntryForQwipi(icicitransid,refundamount,ActionEntry.ACTION_REVERSAL_REQUEST_SENT_FRAUD,ActionEntry.STATUS_REVERSAL_REQUEST_SENT_FRAUD,null,null,auditTrailVO);
                entry.closeConnection();
            }
            else
            {
                ActionEntry entry = new ActionEntry();
                int actionEntry = entry.actionEntryForQwipi(icicitransid,refundamount,ActionEntry.ACTION_REVERSAL_REQUEST_SENT,ActionEntry.STATUS_REVERSAL_REQUEST_SENT,null,null,auditTrailVO);
                entry.closeConnection();
            }
        }
        catch (SQLException e)
        {
            Database.rollback(conn);
            logger.error("SQL Exception occure in reverseTransaction",e);
            throw new SystemError("Error while executing query..");
        }
        catch (PZDBViolationException e)
        {
            Database.rollback(conn);
            logger.error("SQL Exception occure in reverseTransaction",e);
            throw new SystemError("Error while executing query..");
        }
        finally
        {
            //Database.rollback(conn);
            Database.setAutoCommit(conn,true);
            Database.closeConnection(conn);
        }
    }
    public void genericRefundTransaction(String trackingid, String memberid,String refundamount,String captureamount,String merchantid,String description, int chargeper, int currtaxper,int transactiontaxper, GatewayAccount account, String reversalCharges,BigDecimal bdConst,String transid,String merchanttype,String status)throws SystemError
    {
        Connection conn=null;
        try
        {
            conn=Database.getConnection();
            Database.setAutoCommit(conn,false);
            logger.debug("===gateway ===="+account.getGateway());
            String table_name = Database.getTableName(account.getGateway());
            logger.debug("update record for reverse transection in reverseTransaction ");
            String query = "update "+table_name+" set status='markedforreversal',refundamount=? where trackingid=? and status IN ('settled','capturesuccess') and toid=? and captureamount>=?";
            PreparedStatement p=conn.prepareStatement(query);
            p.setString(1,refundamount);
            p.setString(2,trackingid );
            p.setString(3,memberid);
            p.setString(4, refundamount);
            int result = p.executeUpdate();
            logger.debug("No of Rows updated : " + result + "<br>");
            transactionEntry(merchantid,memberid, merchanttype, TYPE_PZ, refundamount, description, REVERSAL);
            String tax_toid = String.valueOf(account.getTaxAccount());
            String charges_toid = String.valueOf(account.getChargesAccount());
            logger.debug("description and amount is entered in select query");
            query = "select transid from transactions where fromid=? and description=? and amount=?";
            PreparedStatement ps=conn.prepareStatement(query);
            ps.setString(1,merchantid);
            ps.setString(2,description);
            ps.setString(3,refundamount);
            ResultSet rs1 = ps.executeQuery();
            rs1.next();
            // int newtransid = rs1.getInt(1);
            //logger.debug("newtransid="+newtransid);
            BigDecimal peramt = new BigDecimal(refundamount);
            peramt = peramt.multiply(new BigDecimal(chargeper / 10000.00)).setScale(2, BigDecimal.ROUND_HALF_EVEN);
            if(status.equals("settled"))
            {
                //transactionEntry(memberid, charges_toid, TYPE_PZ, TYPE_PZ, peramt.toString(), "Reversal of charges of "+icicitransid+" (" + description + ")", REVERSAL_OF_CREDIT_CHARGES);
            }
            int transidForTaxEntry = Integer.parseInt(transid) + 2;
            BigDecimal currentTaxPercentage = new BigDecimal(currtaxper).multiply(bdConst);
            BigDecimal transactionTaxPercentage = new BigDecimal(transactiontaxper).multiply(bdConst);
            BigDecimal taxToBeReversed = calculateTax(peramt.toString(), transactionTaxPercentage.toString());
            BigDecimal calculatedTax = calculateTax(reversalCharges, currentTaxPercentage.toString());

            // The reverse Tax entry should only be made if tax was charged at the time of transaction.
            query = "select * from transactions where type = 'taxoncreditcharges' and transid =?";
            PreparedStatement pst=conn.prepareStatement(query);
            pst.setInt(1,transidForTaxEntry);
            ResultSet rstaxEntry = pst.executeQuery();
            if (rstaxEntry.next())
            {
                // Since this a reversal of tax the tax_toid will be the fromid
                //taxEntry(tax_toid, taxToBeReversed.toString(), "Reversal of Service Tax on Charges @" + transactionTaxPercentage + "% (transid - " + transidForTaxEntry + " ) ( " + description + " ) ", merchantid, REVERSAL_OF_TAX_CREDIT_CHARGES);
            }
            transactionEntry(charges_toid, memberid, TYPE_PZ, TYPE_PZ, reversalCharges, "charges for reversal of "+trackingid+"(" + description + ")", REVERSAL_CHARGES);
            query = "select max(transid) as transid from transactions";
            ResultSet rsChargesTransid = Database.executeQuery(query, conn);
            String chargesTransId = null;
            if (rsChargesTransid.next())
            {
                chargesTransId = rsChargesTransid.getString("transid");
            }
            taxEntry(memberid + "", calculatedTax.toString(), "Service Tax on ReversalCharges @" + currentTaxPercentage + "% (transid - " + chargesTransId + " )", tax_toid, TAX_REVERSAL_CHARGES);
            Database.commit(conn);
            if(description.contains("Fraudulent Transaction"))
            {
                ActionEntry entry = new ActionEntry();
                int actionEntry = entry.genericActionEntry(trackingid,refundamount,ActionEntry.ACTION_REVERSAL_REQUEST_SENT_FRAUD,ActionEntry.STATUS_REVERSAL_REQUEST_SENT_FRAUD,null,account.getGateway(),auditTrailVO);
                logger.debug("ActionExecutorId 4---------"+auditTrailVO.getActionExecutorId());
                entry.closeConnection();
            }
            else
            {
                ActionEntry entry = new ActionEntry();
                int actionEntry = entry.genericActionEntry(trackingid,refundamount,ActionEntry.ACTION_REVERSAL_REQUEST_SENT,ActionEntry.STATUS_REVERSAL_REQUEST_SENT,null,account.getGateway(),auditTrailVO);
                logger.debug("ActionExecutorId 5---------"+auditTrailVO.getActionExecutorId());
                entry.closeConnection();
            }
        }
        catch (SQLException e)
        {
            Database.rollback(conn);
            logger.error("SQL Exception occure in reverseTransaction",e);
            throw new SystemError("Error while executing query..");
        }
        finally
        {
            //Database.rollback(conn);
            Database.setAutoCommit(conn,true);
            Database.closeConnection(conn);
        }
    }
    public int chargebackTransaction(String cbrefnumber, String cbreason, String cbamount, String icicitransid, String memberId, String description, int chargebackCharge, GatewayAccount account, String taxPer) throws SystemError
    {
        Connection conn=null;
        int chargebackEntry = 0;
        try
        {
            conn=Database.getConnection();
            Database.setAutoCommit(conn, false);
            logger.debug(" Update query in chargeback Transection ");
            String query = "update transaction_icicicredit set status='chargeback',cbrefnumber=?,cbreason=?,chargebackamount=? where icicitransid=? and ( status='settled' or ( status='reversed' and captureamount>refundamount))";
            PreparedStatement pst=conn.prepareStatement(query);
            pst.setString(1,cbrefnumber);
            pst.setString(2,cbreason);
            pst.setDouble(3,Double.parseDouble(cbamount));
            pst.setString(4,icicitransid);
            int result = pst.executeUpdate();
            logger.debug("No of Rows updated : " + result + "<br>");
            if (result != 1)
            {
                throw new SystemError("Chargedback for this transaction cannot be processed.");
            }
            String merchantId = account.getMerchantId();
            chargebackEntry = transactionEntry(merchantId, memberId, TYPE_ICICICREDIT, TYPE_PZ, cbamount, description, CHARGEBACK);
            if (chargebackCharge > 0)
            {
                String charges_toid = String.valueOf(account.getChargesAccount());
                String tax_toid = String.valueOf(account.getTaxAccount());
                String chargesDescription = "Charges for " + description;
                String taxOnChargesDescription = " Tax on " + chargesDescription;
                BigDecimal taxPercentage = new BigDecimal(taxPer).multiply(new BigDecimal("0.01"));
                BigDecimal calculatedax = calculateTax(String.valueOf(chargebackCharge), taxPercentage.toString());
                transactionEntry(charges_toid, memberId, TYPE_PZ, TYPE_PZ, String.valueOf(chargebackCharge), chargesDescription, CHARGEBACK_CHARGES);
                taxEntry(memberId, calculatedax.toString(), taxOnChargesDescription, tax_toid, TAX_CHARGEBACK_CHARGES);
            }
            Database.commit(conn);
            // Start : Added for Action and Status Entry in Action History table
            ActionEntry entry = new ActionEntry();
            int actionEntry = entry.actionEntry(icicitransid,cbamount,ActionEntry.ACTION_CHARGEBACK_RACEIVED,ActionEntry.STATUS_CHARGEBACK_RACEIVED);
            entry.closeConnection();
            // End : Added for Action and Status Entry in Action History table
        }
        catch (SystemError systemError)
        {
            Database.rollback(conn);
            throw new SystemError("Error while executing query..");
        }
        catch (Exception Error)
        {
            Database.rollback(conn);
            logger.error("Exception Information in chargebackTransection ",Error );
        }
        finally
        {
            //Database.rollback(conn);
            Database.setAutoCommit(conn,true);
            Database.closeConnection(conn);
        }
        return chargebackEntry;
    }
    public void cancelReverseTransaction(String refundamount,String cancelledAmount, String icicitransid, String merchantid, String icicimerchantid, String description, GatewayAccount account, String rsdescription, String transid, int currtaxper, BigDecimal bdConst, String reversalCharges, String memberId)throws SystemError
    {
        Connection conn=null;
        try
        {
            conn=Database.getConnection();
            Database.setAutoCommit(conn, false);
            String  newrefundamount=String.valueOf(Double.parseDouble(refundamount) - Double.parseDouble(cancelledAmount));
            String query = "update transaction_icicicredit set refundamount=" + newrefundamount + " where icicitransid=" + icicitransid + " and status='reversed' and toid=" + merchantid + " and captureamount>=" + refundamount;
            logger.info(query + " <br> ");
            int result = Database.executeUpdate(query, conn);
            logger.info("No of Rows updated : " + result + "<br>");
            transactionEntry(String.valueOf(memberId), icicimerchantid, TYPE_PZ, TYPE_ICICICREDIT, cancelledAmount, description, CANCELLATION_OF_REVERSAL);
            query = "select transid from transactions where toid=" + memberId + " and description='" + description + "' and amount=" + cancelledAmount;
            logger.info(query);
            ResultSet rs1 = Database.executeQuery(query, conn);
            rs1.next();
            int newtransid = rs1.getInt(1);
            String tax_toid = String.valueOf(account.getTaxAccount());
            String charges_toid = String.valueOf(account.getChargesAccount());
            transactionEntry(merchantid, charges_toid, TYPE_PZ, TYPE_PZ, reversalCharges, "Reversal of charges of " + newtransid + "(" + rsdescription + ")", REFUND_OF_REVERSAL_CHARGES);
            BigDecimal currentTaxPercentage = new BigDecimal(currtaxper).multiply(bdConst);
            BigDecimal taxToBeReversed = calculateTax(reversalCharges, currentTaxPercentage.toString());
            query = "select max(transid) as transid from transactions";
            ResultSet rsChargesTransid = Database.executeQuery(query, conn);
            String chargesTransId = null;
            if (rsChargesTransid.next())
            {
                chargesTransId = rsChargesTransid.getString("transid");
            }
            // Since this a reversal of tax the tax_toid will be the fromid
            taxEntry(tax_toid, taxToBeReversed.toString(), "Reversal of Service Tax on Reversal Charges @" + currentTaxPercentage + "% (transid - " + chargesTransId + " ) ", memberId, REFUND_OF_TAX_REVERSAL_CHARGES);
            Database.commit(conn);
        }
        catch (SQLException e)
        {
            Database.rollback(conn);
            throw new SystemError("Error while executing query..");
        }
        finally
        {
            //Database.rollback(conn);
            Database.setAutoCommit(conn,true);
            Database.closeConnection(conn);
        }
    }
    public void newGenericCancelReverseTransaction(String trackingId,String cancelledAmount,String refundamount, String accountId,GenericResponseVO refundCancelDetails)throws SystemError
    {
        Connection conn=null;
        try
        {
            conn=Database.getConnection();
            logger.debug("Inside newGenericCancelReverseTransaction");
            //getting Transaction Details
            Hashtable transDetails = getTransDetails(trackingId);
            if(transDetails==null)
            {
                throw new SystemError("Transaction Not found for given tracking Id"+trackingId);
            }
            logger.debug("TransDetails found");
            accountId = (String)transDetails.get("accountid");
            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            String memberid = (String)transDetails.get("toid");
            String merchantid = account.getMerchantId();
            String merchanttype = account.getDisplayName();
            String description =(String)transDetails.get("description");
            String paymodeid =(String)transDetails.get("paymodeid");
            String cardtypeid =(String)transDetails.get("cardtypeid");
            String cpamount=(String)transDetails.get("captureamount");
            String transid=(String)transDetails.get("transid");
            String status=(String)transDetails.get("status");
            BigDecimal rfamount =null;
            if(refundamount !=null)
            {
                rfamount = new BigDecimal(refundamount);
            }
            else
            {
                refundamount = (String)transDetails.get("refundamount");
                rfamount = new BigDecimal(refundamount);
            }
            if(!status.equals("reversed") && !status.equals("markedforreversal"))
            {
                throw new SystemError("Cannot Cancel Refund  as Status of Transaction is not Reversed or markedForrevresal for trackingid "+trackingId);
            }
            //Getting charges from Mapping Table
           /* Hashtable chargeDetails = getChargesFromMappingTable(memberid,accountId,paymodeid,cardtypeid);
            if(chargeDetails==null)
            {
                throw new SystemError("Charges Not found in mapping table ");
            }
            logger.debug("Charges found in mapping table");
            String taxper = (String)chargeDetails.get("taxper");
            String reversalcharge = (String)chargeDetails.get("reversalcharge");*/
            Database.setAutoCommit(conn, false);
            String tablename = Database.getTableName(account.getGateway());
            String  newrefundamount=String.valueOf(Double.parseDouble(refundamount) - Double.parseDouble(cancelledAmount));
            String query ="";
            if(tablename.equals("transaction_icicicredit"))
                query = "update transaction_icicicredit set refundamount=" + newrefundamount + " where icicitransid=" + trackingId + " and status IN ('reversed','markedforreversal') and toid=" + memberid + " and captureamount>=" + refundamount;
            else
                query = "update "+tablename+" set refundamount=" + newrefundamount + " where trackingid=" + trackingId + " and status IN ('reversed','markedforreversal') and toid=" + memberid + " and captureamount>=" + refundamount;
            logger.info(query + " <br> ");
            int result = Database.executeUpdate(query, conn);
            logger.info("No of Rows updated : " + result + "<br>");
            String cancelDescription = "Cancellation of Reversal of " + transid + " (  To Auto adjust Chargeback Received ) ";
            transactionEntry(memberid, merchantid, TYPE_PZ, merchanttype, Functions.round(Double.valueOf(cancelledAmount), 2), cancelDescription, CANCELLATION_OF_REVERSAL);
            query = "select transid from transactions where toid=" + memberid + " and description='" + cancelDescription + "' and amount=" + Functions.roundDBL(Double.valueOf(cancelledAmount), 2);
            logger.info(query);
            ResultSet rs1 = Database.executeQuery(query, conn);
            rs1.next();
            int newtransid = rs1.getInt(1);
            String tax_toid = String.valueOf(account.getTaxAccount());
            String charges_toid = String.valueOf(account.getChargesAccount());
            //transactionEntry(memberid, charges_toid, TYPE_PZ, TYPE_PZ, reversalcharge, "Reversal of charges of " + newtransid + "(" + cancelDescription + ")", REFUND_OF_REVERSAL_CHARGES);
            BigDecimal bdConst = new BigDecimal("0.01");
            //BigDecimal taxPercentage = new BigDecimal(taxper).multiply(bdConst);
            // BigDecimal taxToBeReversed = calculateTax(reversalcharge, taxPercentage.toString());
            query = "select max(transid) as transid from transactions";
            ResultSet rsChargesTransid = Database.executeQuery(query, conn);
            String chargesTransId = null;
            if (rsChargesTransid.next())
            {
                chargesTransId = rsChargesTransid.getString("transid");
            }
            // Since this a reversal of tax the tax_toid will be the fromid
            //taxEntry(tax_toid, taxToBeReversed.toString(), "Reversal of Service Tax on Reversal Charges @" + taxPercentage + "% (transid - " + chargesTransId + " ) ", memberid, REFUND_OF_TAX_REVERSAL_CHARGES);
            Database.commit(conn);
        }
        catch (SQLException e)
        {
            //e.printStackTrace();
            Database.rollback(conn);
            throw new SystemError("Error while executing query"+e.getMessage());
        }
        /*catch (Exception e)
        {
            //e.printStackTrace();
            Database.rollback(conn);
            throw new SystemError("Error while executing query"+e.getMessage());
        }*/
        finally
        {
            //Database.rollback(cn);
            Database.setAutoCommit(conn,true);
            Database.closeConnection(conn);
        }
    }

    public void newGenericCancelReverseTransactionNew(String trackingId,String cancelledAmount,String refundamount,String status, String accountId,GenericResponseVO refundCancelDetails)throws SystemError
    {
        Connection conn=null;
        try
        {
            conn=Database.getConnection();
            logger.debug("Inside newGenericCancelReverseTransactionNew");
            if(trackingId==null)
            {
                throw new SystemError("Tracking Id should not be empty"+trackingId);
            }
            logger.debug("TransDetails found");
            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            if(!status.equals("reversed") && !status.equals("markedforreversal"))
            {
                throw new SystemError("Cannot Cancel Refund  as Status of Transaction is not Reversed or markedForrevresal for trackingid "+trackingId);
            }
            String tablename = Database.getTableName(account.getGateway());
            double  newrefundamount=Double.parseDouble(refundamount) - Double.parseDouble(cancelledAmount);
            logger.debug("NewRefundAmount:::"+newrefundamount);
            String query = "update "+tablename+" set refundamount=" + newrefundamount + " where trackingid=" + trackingId + " and status IN ('reversed','markedforreversal') and captureamount>=" + refundamount;
            logger.error(query + " <br> ");
            int result = Database.executeUpdate(query, conn);
            logger.error("No of Rows updated : " + result + "<br>");
            String cancelDescription = "Cancellation of Reversal of " + trackingId + " (  To Auto adjust Chargeback Received ) ";
            auditTrailVO.setCbReason(cancelDescription);
            ActionEntry entry = new ActionEntry();
            int actionEntry = entry.genericActionEntry(trackingId, cancelledAmount,ActionEntry.ACTION_REVERSAL_CANCELATION,ActionEntry.STATUS_REVERSAL_CANCELED,null,account.getGateway(),refundCancelDetails,auditTrailVO);

            //TODO-Action history should update..
        }
        catch (Exception e)
        {
            throw new SystemError("Error while executing query"+e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
    public int newGenericChargebackTransaction(String cbrefnumber, String cbreason, String cbamount, String trackingId, String accountId,String cbdescription,GenericResponseVO chargebackDetails,AuditTrailVO auditTrailVOLocal) throws SystemError
    {
        Connection conn=null;
        try
        {
            conn=Database.getConnection();
            Database.setAutoCommit(conn, false);
            logger.debug("Inside newGenericChargebackTransaction");
            //getting Transaction Details
            Hashtable transDetails = getTransDetails(trackingId);
            if(transDetails==null)
            {
                throw new SystemError("Transaction Not found for given tracking Id"+trackingId);
            }
            logger.debug("TransDetails found");
            accountId = (String)transDetails.get("accountid");
            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            String memberid = (String)transDetails.get("toid");
            String merchantid = account.getMerchantId();
            String merchanttype = account.getDisplayName();
            String description =(String)transDetails.get("description");
            String paymodeid =(String)transDetails.get("paymodeid");
            String cardtypeid =(String)transDetails.get("cardtypeid");
            String cpamount=(String)transDetails.get("captureamount");
            String transid=(String)transDetails.get("transid");
            String status=(String)transDetails.get("status");
            BigDecimal chargebackamount =null;
            if(cbamount !=null)
            {
                chargebackamount = new BigDecimal(cbamount);
            }
            else
            {
                cbamount = (String)transDetails.get("refundamount");
                chargebackamount = new BigDecimal(cbamount);
            }
            if (Functions.checkStringNull(cbrefnumber) == null)
            {
                cbrefnumber = "Not Available for TrackingId_"+trackingId;
            }
            if (Functions.checkStringNull(cbreason) == null)
            {
                cbreason = "Chargeback received";
            }

            //Getting charges from Mapping Table
            /*Hashtable chargeDetails = getChargesFromMappingTable(memberid,accountId,paymodeid,cardtypeid);
            if(chargeDetails==null)
            {
                throw new SystemError("Charges Not found in mapping table ");
            }
            logger.debug("Charges found in mapping table");
            String taxper = (String)chargeDetails.get("taxper");
            String chargebackcharge = (String)chargeDetails.get("chargebackcharge");*/

            int chargebackEntry = 0;
            String tablename = Database.getTableName(account.getGateway());
            String query ="";
            if(tablename.equals("transaction_icicicredit"))
            {
                query = "update transaction_icicicredit set status='chargeback',cbrefnumber='" + cbrefnumber + "',cbreason='" + cbreason + "',chargebackamount=" + Double.parseDouble(cbamount) + " where icicitransid=" + trackingId + " and ( status='settled' or ( status='reversed' and captureamount>refundamount))";
            }
            else if (tablename.equals("transaction_common"))
            {
                query = "update "+tablename+" set status='chargeback',chargebackinfo='" + cbreason + "',chargebackamount=" + Double.parseDouble(cbamount) + ",chargebacktimestamp='" + functions.getTimestamp() + "'" + " where trackingid=" + trackingId + " and ( status='settled' or ( status='reversed' and captureamount>refundamount))";
            }
            else
            {
                query = "update "+tablename+" set status='chargeback',cbreason='" + cbreason + "',chargebackamount=" + Double.parseDouble(cbamount) + " where trackingid=" + trackingId + " and ( status='settled' or ( status='reversed' and captureamount>refundamount))";
            }
            logger.info(query + " <br> ");
            int result = Database.executeUpdate(query, conn);
            logger.info("No of Rows updated : " + result + "<br>");
            if (result != 1)
            {
                throw new SystemError("Chargedback for this transaction cannot be processed.");
            }
            if(Functions.checkStringNull(cbdescription) == null)
            {
                cbdescription = "Reversal of " + transid + " ( Chargeback of orderid : " + description + " for " + cbreason + ")";
            }
            chargebackEntry = transactionEntry(merchantid, memberid, merchanttype, TYPE_PZ, cbamount, cbdescription, CHARGEBACK);
            String charges_toid = String.valueOf(account.getChargesAccount());
            String tax_toid = String.valueOf(account.getTaxAccount());
            String chargesDescription = "Charges for " + cbdescription;
            String taxOnChargesDescription = " Tax on " + chargesDescription;
            //BigDecimal taxPercentage = new BigDecimal(taxper).multiply(new BigDecimal("0.01"));
            //BigDecimal calculatedax = calculateTax(chargebackcharge, taxPercentage.toString());
            //transactionEntry(charges_toid, memberid, TYPE_PZ, TYPE_PZ, chargebackcharge, chargesDescription, CHARGEBACK_CHARGES);
            //taxEntry(memberid, calculatedax.toString(), taxOnChargesDescription, tax_toid, TAX_CHARGEBACK_CHARGES);
            //Start : Added for Action and Status Entry in Action History/ Details table
            //auditTrailVO.setActionExecutorId(memberid);
            //auditTrailVO.setActionExecutorName("Admin");
            auditTrailVOLocal.setCbReason(cbreason);
            ActionEntry entry = new ActionEntry();
            int actionEntry = entry.genericActionEntry(trackingId,cbamount,ActionEntry.ACTION_CHARGEBACK_RACEIVED,ActionEntry.STATUS_CHARGEBACK_RACEIVED,null,account.getGateway(),chargebackDetails,auditTrailVOLocal);
            entry.closeConnection();
            // End : Added for Action and Status Entry in Action History/Details table
            Database.commit(conn);
            return chargebackEntry;
        }

        finally
        {
            Database.setAutoCommit(conn,true);
            Database.closeConnection(conn);
        }
    }
    public int newGenericChargebackTransactionNew(String cbrefnumber, String cbreason, String cbamount, String trackingId,String orderId, String accountId,String cbdescription,GenericResponseVO chargebackDetails,AuditTrailVO auditTrailVOLocal) throws SystemError
    {
        Connection conn=null;
        try
        {
            conn    = Database.getConnection();
            logger.debug("Inside newGenericChargebackTransactionNew");
            //getting Transaction Details
            if(trackingId==null)
            {
                throw new SystemError("TrackingId Not found for given tracking Id"+trackingId);
            }
            logger.debug("TransDetails found");
            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            if (Functions.checkStringNull(cbreason) == null)
            {
                cbreason = "Chargeback received";
            }
            int chargebackEntry = 0;
            String tablename    = Database.getTableName(account.getGateway());
            String query        = "";
            query = "update "+tablename+" set status='chargeback',chargebackinfo='" + cbreason + "',chargebackamount=chargebackamount+'" + Double.parseDouble(cbamount)  + "',chargebacktimestamp='" + functions.getTimestamp() +  "'  where trackingid=" + trackingId + " AND ( STATUS='settled' OR (STATUS='chargeback' AND captureamount>chargebackamount) OR ( STATUS='reversed' AND captureamount>=refundamount))";
            logger.info(query + " <br> ");
            int result = Database.executeUpdate(query, conn);
            logger.info("No of Rows updated : " + result + "<br>");
            if (result != 1)
            {
                throw new SystemError("Chargedback for this transaction cannot be processed.");
            }
            if(Functions.checkStringNull(cbdescription) == null)
            {
                cbdescription = "Reversal of " + trackingId + " ( Chargeback of orderid : " + orderId + " for " + cbreason + ")";
            }
            auditTrailVOLocal.setCbReason(cbreason);
            ActionEntry entry   = new ActionEntry();
            int actionEntry     = entry.genericActionEntry(trackingId,cbamount,ActionEntry.ACTION_CHARGEBACK_RACEIVED,ActionEntry.STATUS_CHARGEBACK_RACEIVED,null,account.getGateway(),chargebackDetails,auditTrailVOLocal);
            entry.closeConnection();
            return chargebackEntry;
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
    public int newGenericChargebackTransactionForChargebackReversed(String cbrefnumber, String cbreason, String cbamount, String trackingId,String orderId, String accountId,String cbdescription,GenericResponseVO chargebackDetails,AuditTrailVO auditTrailVOLocal) throws SystemError
    {
        Connection conn=null;
        try
        {
            conn=Database.getConnection();
            logger.debug("Inside newGenericChargebackTransactionForChargebackReversed");
            //getting Transaction Details
            if(trackingId==null)
            {
                throw new SystemError("TrackingId Not found for given tracking Id"+trackingId);
            }
            logger.debug("TransDetails found");
            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            if (Functions.checkStringNull(cbreason) == null)
            {
                cbreason = "Chargeback received";
            }
            int chargebackEntry = 0;
            String tablename = Database.getTableName(account.getGateway());
            String query ="";
            query = "update "+tablename+" set status='chargeback',chargebackinfo='" + cbreason + "',chargebackamount=chargebackamount+'" + Double.parseDouble(cbamount)+ "',remark='Pre-arbitration' where trackingid=" + trackingId + " AND (STATUS='chargebackreversed' AND captureamount>chargebackamount)";
            logger.info(query + " <br> ");
            int result = Database.executeUpdate(query, conn);
            logger.info("No of Rows updated : " + result + "<br>");
            if (result != 1)
            {
                throw new SystemError("Chargedback for this transaction cannot be processed.");
            }
            if(Functions.checkStringNull(cbdescription) == null)
            {
                cbdescription = "Reversal of " + trackingId + " ( Chargeback of orderid : " + orderId + " for " + cbreason + ")";
            }
            auditTrailVOLocal.setCbReason(cbreason);
            CommResponseVO responseVO=new CommResponseVO();
            responseVO.setDescription("Pre-arbitration");
            ActionEntry entry = new ActionEntry();
            int actionEntry = entry.genericActionEntry(trackingId,cbamount,ActionEntry.ACTION_CHARGEBACK_RACEIVED,ActionEntry.STATUS_CHARGEBACK_RACEIVED,null,account.getGateway(),responseVO,auditTrailVOLocal);
            entry.closeConnection();
            return chargebackEntry;
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
    public List<TransactionDetailsVO>  exportlistForPartnerTransactions(String fdtstamp,String tdtstamp,String toType,String dateType) throws SystemError
    {
        logger.error("inside exportlistForPartnerTransactions");
        Connection connection=null;
        PreparedStatement pstms = null;
        PreparedStatement pstms1 =null;
        ResultSet rs = null;
        TransactionDetailsVO transactionDetailsVO=new TransactionDetailsVO();
        List<TransactionDetailsVO> transactionDetailsVOList=new ArrayList<>();
        String tablename = "";
        String fields = "";
        int counter = 1;
        String pRefund = "false";
        StringBuilder query = new StringBuilder();

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        //status = ESAPI.encoder().encodeForSQL(me,status);
        fdtstamp= ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp=ESAPI.encoder().encodeForSQL(me,tdtstamp);

        try
        {
            connection=Database.getConnection();
            tablename = "transaction_common";
            fields = "t.toid,t.trackingid as transid,t.status,t.name,t.currency,t.country,t.amount,t.captureamount,t.refundamount,t.payoutamount,t.description,t.dtstamp,t.paymodeid,t.cardtype,t.accountid,t.remark,t.ccnum,t.timestamp,t.emailaddr,t.orderdescription,t.customerId,t.terminalid,t.paymentid,t.fromtype,m.company_name,t.chargebackinfo, b.issuing_bank";
            //query.append("select " + fields + " from " + tablename + " AS t,bin_details AS bd, members AS m where t.trackingid=bd.icicitransid and t.toid = m.memberid");
            query.append("select ");
            query.append(fields);
            query.append(" from ");
            query.append(tablename);
            query.append(" AS t LEFT JOIN bin_details AS b ON t.trackingid = b.icicitransid JOIN members AS m ON t.toid = m.memberid WHERE memberid>0 and t.toType=?");

            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                query.append(" and t.timestamp >= ? ");
            }
            else
            {
                query.append(" and t.dtstamp >= ? ");
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                query.append(" and t.timestamp <= ? ");
            }
            else
            {
                query.append(" and t.dtstamp <= ? ");
            }

            pstms = connection.prepareStatement(query.toString());
            pstms.setString(counter,toType);
            counter++;
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                pstms.setString(counter, startDate);
                counter++;
            }
            else
            {
                pstms.setString(counter, fdtstamp);
                counter++;
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter2.format(calendar.getTime());
                pstms.setString(counter, endDate);
                counter++;
            }
            else
            {
                pstms.setString(counter, tdtstamp);
                counter++;
            }

            logger.error("export pstmt:::::"+pstms);
            rs=pstms.executeQuery();
            while (rs.next())
            {
                transactionDetailsVO=new TransactionDetailsVO();
                transactionDetailsVO.setToid(rs.getString("toid"));
                transactionDetailsVO.setTrackingid(rs.getString("transid"));
                transactionDetailsVO.setStatus(rs.getString("status"));
                transactionDetailsVO.setName(rs.getString("name"));
                transactionDetailsVO.setCurrency(rs.getString("currency"));
                transactionDetailsVO.setCountry(rs.getString("country"));
                transactionDetailsVO.setAmount(rs.getString("amount"));
                transactionDetailsVO.setCaptureAmount(rs.getString("captureamount"));
                transactionDetailsVO.setRefundAmount(rs.getString("refundamount"));
                transactionDetailsVO.setPayoutamount(rs.getString("payoutamount"));
                transactionDetailsVO.setDescription(rs.getString("description"));
                transactionDetailsVO.setPaymodeId(rs.getString("paymodeid"));
                transactionDetailsVO.setCardtype(rs.getString("cardtype"));
                transactionDetailsVO.setAccountId(rs.getString("accountid"));
                if(!"null".equalsIgnoreCase(rs.getString("remark")))
                    transactionDetailsVO.setRemark(rs.getString("remark"));
                if(functions.isValueNull(rs.getString("ccnum")))
                    transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(rs.getString("ccnum")));
                else
                    transactionDetailsVO.setCcnum("");
                transactionDetailsVO.setTransactionTime(rs.getString("timestamp"));
                transactionDetailsVO.setEmailaddr(rs.getString("emailaddr"));
                transactionDetailsVO.setOrderDescription(rs.getString("orderdescription"));
                transactionDetailsVO.setCustomerId(rs.getString("customerId"));
                transactionDetailsVO.setTerminalId(rs.getString("terminalid"));
                transactionDetailsVO.setPaymentId(rs.getString("paymentid"));
                transactionDetailsVO.setFromtype(rs.getString("fromtype"));
                transactionDetailsVO.setCompanyName(rs.getString("company_name"));
                transactionDetailsVO.setChargeBackInfo(rs.getString("chargebackinfo"));
                transactionDetailsVO.setIssuingBank(rs.getString("issuing_bank"));
                transactionDetailsVO.setDtstamp(rs.getString("dtstamp"));
                transactionDetailsVOList.add(transactionDetailsVO);
            }
        }
        catch (SQLException se)
        {
            logger.error("SQL Exception leaving listTransactions" ,se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstms);
            Database.closePreparedStatement(pstms1);
            Database.closeConnection(connection);
        }
        return transactionDetailsVOList;
    }
    public List<TransactionDetailsVO> getActionHistoryByTrackingIdAndGateway(String trackingId) throws SystemError
    {
        logger.debug("Entering getActionHistoryByTrackingId");
        TransactionDetailsVO transactionDetailsVO=new TransactionDetailsVO();
        List<TransactionDetailsVO> transactionDetailsVOList=new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        StringBuilder count = new StringBuilder();
        Connection cn = null;
        PreparedStatement pstmt = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<String> trackingIdList=new ArrayList<>();
        try
        {
            sql.append("select tcd.trackingid as icicitransid,tcd.amount,tcd.currency,tcd.templatecurrency,tcd.templateamount,tcd.action,tcd.status,tcd.timestamp,tcd.actionexecutorid,tcd.actionexecutorname,tcd.responsedescriptor as responsedescriptor,tcd.responsecode as responsecode,tcd.remark,tcd.responsedescription,tcd.responsehashinfo,tcd.arn,tcd.responsetransactionid,t.cardtype,t.cardtypeid from ");
            sql.append("transaction_common");
            sql.append("_details AS tcd,");
            sql.append("transaction_common AS t");
            sql.append(" where tcd.trackingid=t.trackingid and tcd.trackingid IN ("+trackingId+")  ORDER BY tcd.trackingid DESC,detailid DESC");
            //cn = Database.getConnection();
            cn = Database.getRDBConnection();
            pstmt = cn.prepareStatement(sql.toString());
            rs=pstmt.executeQuery();
            logger.debug("action history query----"+pstmt);
            int i=1;
            while (rs.next())
            {
                transactionDetailsVO=new TransactionDetailsVO();
                if(!trackingIdList.contains(rs.getString("icicitransid")))
                {
                    i=1;
                    trackingIdList.add(rs.getString("icicitransid"));
                    transactionDetailsVO.setCount(String.valueOf(i));
                    i++;
                }
                else
                {
                    transactionDetailsVO.setCount(String.valueOf(i));
                    i++;
                }
                transactionDetailsVO.setTrackingid(rs.getString("icicitransid"));
                transactionDetailsVO.setAmount(rs.getString("amount"));
                transactionDetailsVO.setCurrency(rs.getString("currency"));
                transactionDetailsVO.setTemplatecurrency(rs.getString("templatecurrency"));
                transactionDetailsVO.setTemplateamount(rs.getString("templateamount"));
                transactionDetailsVO.setAction(rs.getString("action"));
                transactionDetailsVO.setStatus(rs.getString("status"));
                transactionDetailsVO.setTransactionTime(rs.getString("timestamp"));
                transactionDetailsVO.setTransactionTime(rs.getString("timestamp"));
                transactionDetailsVO.setActionExecutorName(rs.getString("actionexecutorname"));
                transactionDetailsVO.setReponseDescriptor(rs.getString("responsedescriptor"));
                transactionDetailsVO.setResponseCode(rs.getString("responsecode"));
                transactionDetailsVO.setRemark(rs.getString("remark"));
                transactionDetailsVO.setDescription(rs.getString("responsedescription"));
                transactionDetailsVO.setResponseHashInfo(rs.getString("responsehashinfo"));
                transactionDetailsVO.setArn(rs.getString("arn"));
                transactionDetailsVO.setPaymentId(rs.getString("responsetransactionid"));
                transactionDetailsVO.setCardtype(rs.getString("cardtype"));
                transactionDetailsVO.setCardTypeId(rs.getString("cardtypeid"));
                transactionDetailsVOList.add(transactionDetailsVO);
            }

            return transactionDetailsVOList;

        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
            throw new SystemError(se.toString());

        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(ps);
            Database.closeConnection(cn);
        }

    }

    //New Method For Export to Excel in Agent
    public Hashtable  listForAgentCardPresentTransactions(String tdtstamp, String fdtstamp, String status, String desc, String toid, boolean archive, String trackingid, String currency, int records, int pageno, Set<String> gatewayTypeSet, String accountid, String gate_name, String currency1) throws SystemError
    {
        logger.debug("inside listForAgentCardPresentTransactions");
        if(!Functions.isValidSQL(trackingid) || !Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");
        }
        Connection connection       = null;
        PreparedStatement pstms     = null;
        PreparedStatement pstms1    = null;
        ResultSet rs                = null;
        Hashtable hash              = null;
        String tablename            = "";
        String fields               = "";
        int counter                 = 1;
        String pRefund              = "false";
        StringBuilder query         = new StringBuilder();

        Codec me    = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp    = ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp    = ESAPI.encoder().encodeForSQL(me,tdtstamp);
        desc        = ESAPI.encoder().encodeForSQL(me,desc);
        trackingid  = ESAPI.encoder().encodeForSQL(me,trackingid);

        if(gatewayTypeSet.size()==0)
        {
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }

        try
        {
            Iterator i  = gatewayTypeSet.iterator();
            connection  = Database.getConnection();
            tablename   = "transaction_card_present";;
            if (archive)
            {
                tablename = "transaction_common_archive";
            }
            fields = "t.toid,t.trackingid as transid,t.status,t.name,t.currency,t.country,t.amount,t.captureamount,t.walletAmount,t.walletCurrency," +
                    "t.refundamount,t.chargebackamount,t.payoutamount,t.description,t.dtstamp,t.transactionTime, t.paymodeid,t.cardtype,t.accountid," +
                    "t.remark,t.timestamp,t.emailaddr,t.orderdescription,t.customerId,t.terminalid,t.paymentid,t.fromtype,m.company_name," +
                    "m.partnerId,m.memberid,t.chargebackinfo,t.rrn,t.arn,t.authorization_code";


            fields = fields +",bd.bin_card_category,bd.bin_card_type,bd.first_six,bd.last_four ";

            query.append("select ");
            query.append(fields);
            query.append(" from ");
            query.append(tablename);
            query.append(" AS t  JOIN members AS m ON t.toid = m.memberid  ");

            query.append(" LEFT JOIN bin_details as bd on bd.icicitransid = t.trackingid ");

            query.append("WHERE memberid > 0 ");

            if (functions.isValueNull(toid))
            {
                query.append(" and t.toid in (" );
                query.append(toid);
                query.append(" )");
            }
            if (functions.isValueNull(status))
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status  = "reversed";
                    pRefund = "true";
                }
                query.append(" and t.status= ? ");
            }

            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and t.transactionTime >= ? ");
            }

            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and t.transactionTime <= ? ");
            }

            if (functions.isValueNull(desc))
            {
                query.append(" and t.description like '%" + desc + "%'");
            }
            if (functions.isValueNull(trackingid))
            {
                query.append(" and t.trackingid= ? ");
            }

            if(functions.isValueNull(gate_name))
            {
                query.append(" and t.fromtype= ? ");
            }

            if(functions.isValueNull(currency1))
            {

                query.append(" and t.currency= ? ");
            }

            if(functions.isValueNull(accountid))
            {
                query.append(" and t.accountid= ? ");
            }

            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and captureamount > refundamount");
            }

            StringBuilder countquery = new StringBuilder("select count(*) from ( " );
            countquery.append(query);
            countquery.append(" ) as temp ");

            query.append(" order by transid DESC");

            pstms       = connection.prepareStatement(query.toString());
            pstms1      = connection.prepareStatement(countquery.toString());

            if (functions.isValueNull(status))
            {
                pstms.setString(counter, status);
                pstms1.setString(counter, status);
                counter++;
            }
            if ( functions.isValueNull(fdtstamp))
            {
                long milliSeconds           = Long.parseLong(fdtstamp + "000");
                Calendar calendar           = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1       = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate            = formatter1.format(calendar.getTime());
                pstms.setString(counter, startDate);
                pstms1.setString(counter, startDate);
                counter++;
            }

            if (functions.isValueNull(tdtstamp))
            {
                long milliSeconds       = Long.parseLong(tdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                pstms.setString(counter, endDate);
                pstms1.setString(counter, endDate);
                counter++;
            }

            if (functions.isValueNull(desc))
            {
                pstms.setString(counter, desc);
                pstms1.setString(counter, desc);
                counter++;
            }
           /* if (functions.isValueNull(desc))
            {
                pstms.setString(counter, desc);
                pstms1.setString(counter, desc);
                counter++;
            }*/
            if (functions.isValueNull(trackingid))
            {
                pstms.setString(counter, trackingid);
                pstms1.setString(counter, trackingid);
                counter++;
            }

            if(functions.isValueNull(gate_name))
            {
                pstms.setString(counter, gate_name);
                pstms1.setString(counter, gate_name);
                counter++;
            }

            if(functions.isValueNull(currency1))
            {
                pstms.setString(counter, currency1);
                pstms1.setString(counter, currency1);
                counter++;
            }

            if(functions.isValueNull(accountid))
            {
                pstms.setString(counter, accountid);
                pstms1.setString(counter, accountid);
                counter++;
            }

            logger.error("export pstmt:::::"+pstms);
            logger.error("export pstmt1:::::"+pstms1);

            hash    = Database.getHashFromResultSetForTransactionEntry(pstms.executeQuery());
            rs      = pstms1.executeQuery();

            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException se)
        {
            logger.error("SQL Exception leaving listForAgentCardPresentTransactions" ,se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstms);
            Database.closePreparedStatement(pstms1);
            Database.closeConnection(connection);
        }
        logger.debug("Leaving listForAgentCardPresentTransactions"+hash);
        return hash;
    }

    public Hashtable  listForAgentTransactions(String tdtstamp, String fdtstamp, String status, String desc, String toid, boolean archive, String trackingid, String currency, int records, int pageno, Set<String> gatewayTypeSet, String accountid, String gate_name, String currency1) throws SystemError
    {
        logger.debug("inside listForAgentTransactions");
        if(!Functions.isValidSQL(trackingid) || !Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");
        }
        Connection connection       = null;
        PreparedStatement pstms     = null;
        PreparedStatement pstms1    = null;
        ResultSet rs                = null;
        Hashtable hash              = null;
        String tablename            = "";
        String fields               = "";
        int counter                 = 1;
        String pRefund              = "false";
        StringBuilder query         = new StringBuilder();

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp    = ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp    = ESAPI.encoder().encodeForSQL(me,tdtstamp);
        desc        = ESAPI.encoder().encodeForSQL(me,desc);

        if(gatewayTypeSet.size() == 0)
        {
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }

        try
        {
            Iterator i  = gatewayTypeSet.iterator();
            connection  = Database.getConnection();
            tablename   = "transaction_common";
            if (archive)
            {
                tablename = "transaction_common_archive";
            }
            fields = "t.toid,t.trackingid as transid,t.status,t.name,t.currency,t.country,t.amount,t.captureamount,t.walletAmount,t.walletCurrency," +
                    "t.refundamount,t.chargebackamount,t.payoutamount,t.description,t.dtstamp,t.paymodeid,t.cardtype,t.accountid,t.remark," +
                    "t.timestamp,t.emailaddr,t.orderdescription,t.customerId,t.terminalid,t.paymentid,t.fromtype,m.company_name,m.partnerId,m.memberid," +
                    "t.chargebackinfo,t.rrn,t.arn,t.authorization_code,t.transaction_mode ";

            fields = fields +",bd.issuing_bank,bd.bin_card_category,bd.bin_card_type,bd.bin_sub_brand,bd.country_name,bd.subcard_type,bd.first_six,bd.last_four ";

            query.append("select " + fields + " from " + tablename + " AS t  ");

            query.append(" LEFT JOIN bin_details AS  bd ON t.trackingid=bd.icicitransid ");

            query.append(" JOIN members AS m ON t.toid = m.memberid ");

            if (functions.isValueNull(toid))
            {
                query.append(" and t.toid in (" );
                query.append(toid);
                query.append(" )");
            }
            if (functions.isValueNull(status))
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status      = "reversed";
                    pRefund     = "true";
                }
                query.append(" and t.status= ? ");
            }

            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and t.dtstamp >= ? ");
            }
            if ( functions.isValueNull(tdtstamp))
            {
                query.append(" and t.dtstamp <= ? ");
            }
            if (functions.isValueNull(desc))
            {
                query.append(" and t.description like '%" + desc + "%'");
            }

            if (functions.isValueNull(trackingid.toString()))
            {
                query.append(" and t.trackingid IN("+trackingid.toString()+")");
            }

            if(functions.isValueNull(gate_name))
            {
                query.append(" and t.fromtype= ? ");
            }

            if(functions.isValueNull(currency1))
            {
                query.append(" and t.currency= ? ");
            }

            if(functions.isValueNull(accountid))
            {
                query.append(" and t.accountid= ? ");
            }

            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and captureamount > refundamount");
            }

            StringBuilder countquery = new StringBuilder("select count(*) from ( " );
            countquery.append(query);
            countquery.append(" ) as temp ");

            query.append(" order by transid DESC");

            pstms   = connection.prepareStatement(query.toString());
            pstms1  = connection.prepareStatement(countquery.toString());

            if (functions.isValueNull(status))
            {
                pstms.setString(counter, status);
                pstms1.setString(counter, status);
                counter++;
            }
            if (functions.isValueNull(fdtstamp))
            {
                pstms.setString(counter, fdtstamp);
                pstms1.setString(counter, fdtstamp);
                counter++;
            }
            if ( functions.isValueNull(tdtstamp))
            {
                pstms.setString(counter, tdtstamp);
                pstms1.setString(counter, tdtstamp);
                counter++;
            }
            /*if (functions.isValueNull(desc))
            {
                pstms.setString(counter, desc);
                pstms1.setString(counter, desc);
                counter++;
            }*/

            if(functions.isValueNull(gate_name))
            {
                pstms.setString(counter, gate_name);
                pstms1.setString(counter, gate_name);
                counter++;
            }

            if(functions.isValueNull(currency1))
            {
                pstms.setString(counter, currency1);
                pstms1.setString(counter, currency1);
                counter++;
            }

            if(functions.isValueNull(accountid))
            {
                pstms.setString(counter, accountid);
                pstms1.setString(counter, accountid);
                counter++;
            }

            logger.error("export pstmt:::::"+pstms);
            logger.error("export pstmt1:::::"+pstms1);

            hash    = Database.getHashFromResultSetForTransactionEntry(pstms.executeQuery());
            rs      = pstms1.executeQuery();

            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException se)
        {
            logger.error("SQL Exception leaving listForAgentTransactions" ,se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstms);
            Database.closePreparedStatement(pstms1);
            Database.closeConnection(connection);
        }
        logger.debug("Leaving listForAgentTransactions"+hash);
        return hash;
    }

    public HashMap getTrackingIdList(String desc, String tdtstamp, String fdtstamp, String trackingid, String status, int records, int pageno, boolean archive,Set<String> gatewayTypeSet,String accountid,TerminalVO terminalVO,String firstName,String lastName,String emailAddress,String paymentId, String customerId,String dateType,String statusflag,String issuingBank , String firstsix, String lastfour ,String transactionMode ) throws SystemError
    {
        logger.debug("Entering transaction entry getTrackingIdList +++++");
        if(!Functions.isValidSQL(trackingid) || !Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");

        }
        Connection connection=null;
        // logger.debug("trackingid===1===//=in transactionENTRY"+trackingid);
        HashMap hash      = null;
        String tablename    = "";
        String fields       = "";
        String orderby      = "";
        String pRefund      = "false";
        StringBuffer query  = new StringBuffer();
        //Encoding for SQL Injection check

        Codec me    = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        //status = ESAPI.encoder().encodeForSQL(me,status);
        fdtstamp    = ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp    = ESAPI.encoder().encodeForSQL(me,tdtstamp);
        desc        = ESAPI.encoder().encodeForSQL(me,desc);
        trackingid  = ESAPI.encoder().encodeForSQL(me,trackingid);

        int start   = 0; // start index
        int end     = 0; // end index
        start       = (pageno - 1) * records;
        end         = records;

        if(gatewayTypeSet.size() == 0)
        {
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }

        try
        {
            connection  = Database.getConnection();
            tablename   = "transaction_common";
            if (archive)
            {
                tablename = "transaction_common_archive";
            }

            fields = "t.trackingid as transid";


            //query.append("select " + fields + " from " + tablename + " as t LEFT JOIN bin_details as b on t.trackingid = b.icicitransid where t.trackingid>0 and");
            query.append("select " + fields + " from " + tablename + " as t LEFT JOIN bin_details as b on t.trackingid = b.icicitransid ");
            query.append(" where t.toid ='" + merchantid + "'");
            if (status != null)
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status  = "reversed";
                    pRefund = "true";
                }
                query.append(" and t.status='" + status + "'");
            }
            if (!"all".equalsIgnoreCase(statusflag))
            {
                query.append(" and b."+statusflag+"='Y'");
            }
            if (functions.isValueNull(desc))
                query.append(" and t.description like '" + desc + "%'");


            if (functions.isValueNull(terminalVO.getTerminalId()))
                query.append(" and t.terminalid in " + terminalVO.getTerminalId());

            if (trackingid != null && !trackingid.equalsIgnoreCase(""))
            {

                query.append(" and t.trackingid=" + trackingid);

            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds       = Long.parseLong(fdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" and t.timestamp >= '" + startDate + "'");
            }
            else if (functions.isValueNull(fdtstamp))
            {
                query.append(" and t.dtstamp >= " + fdtstamp);
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {

                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and t.timestamp <= '" + endDate + "'");
            }
            else if(functions.isValueNull(tdtstamp))
            {
                query.append(" and t.dtstamp <= " + tdtstamp);
            }
            if(functions.isValueNull(paymentId))
            {

                query.append(" and t.paymentid='" + paymentId+"'");

            }

            if(functions.isValueNull(firstName))
            {
                query.append(" and t.firstname= '" + firstName+"'");
            }
            if (functions.isValueNull(lastName))
            {
                query.append(" and t.lastname= '"+lastName+"'");
            }
            if(functions.isValueNull(emailAddress))
            {
                query.append(" and t.emailaddr= '"+emailAddress+"'");
            }
            if(functions.isValueNull(customerId))
            {
                query.append(" and t.customerId= '"+customerId+"'");
            }
            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and t.captureamount > t.refundamount");
            }

            if (functions.isValueNull(issuingBank))
            {
                query.append(" and b.issuing_bank= '"+issuingBank+"'");
            }

            if (functions.isValueNull(firstsix))
            {
                query.append(" and b.first_six= '"+firstsix+"'");
            }

            if (functions.isValueNull(lastfour))
            {
                query.append(" and b.last_four= '"+lastfour+"'");
            }
            if (functions.isValueNull(transactionMode))
            {
                query.append(" and t.transaction_mode= '"+transactionMode.trim()+"'");
            }

            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");

            //query.append(" order by dtstamp ,transid desc");
            query.append(" order by transid desc");

           // query.append(" limit " + start + "," + end);
            logger.debug("Fatch records from transaction issuing bank:::"+issuingBank);
            logger.debug("transaction query....."+query);


            //logger.debug("Count query =="+countquery.toString());

            hash = Database.getHashMapFromResultSet(Database.executeQuery(query.toString(), connection));
            /*ResultSet rs = Database.executeQuery(countquery.toString(), connection);

            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));*/
        }
        catch (SQLException se)
        {   logger.error("SQL Exception leaving listTransactions" ,se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        logger.debug("Leaving listTransactions");
        return hash;
    }
    public HashMap getTrackingIdLists(String desc, String tdtstamp, String fdtstamp, String trackingid, String status, int records, int pageno, boolean archive,Set<String> gatewayTypeSet,String accountid,TerminalVO terminalVO,String firstName,String lastName,String emailAddress,String paymentId, String customerId,String dateType,String statusflag,String issuingBank , String firstsix, String lastfour ,String transactionMode ) throws SystemError
    {
        logger.debug("Entering listTransactionsNew");
        if(!Functions.isValidSQL(trackingid) || !Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");

        }
        Connection connection=null;
        // logger.debug("trackingid===1===//=in transactionENTRY"+trackingid);
        HashMap hash      = null;
        String tablename    = "";
        String fields       = "";
        String orderby      = "";
        String pRefund      = "false";
        StringBuffer query  = new StringBuffer();
        //Encoding for SQL Injection check

        Codec me    = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        //status = ESAPI.encoder().encodeForSQL(me,status);
        fdtstamp    = ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp    = ESAPI.encoder().encodeForSQL(me,tdtstamp);
        desc        = ESAPI.encoder().encodeForSQL(me,desc);
        trackingid  = ESAPI.encoder().encodeForSQL(me,trackingid);

        int start   = 0; // start index
        int end     = 0; // end index
        start       = (pageno - 1) * records;
        end         = records;

        if(gatewayTypeSet.size() == 0)
        {
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }

        try
        {
            //Iterator i = gatewayTypeSet.iterator();
            connection  = Database.getConnection();
            //connection=Database.getRDBConnection();
            /*while(i.hasNext())
            {*/
            //tablename = "transaction_icicicredit";

            tablename   = "transaction_common";
            if (archive)
            {
                //tablename += "_archive";        //ToDO will be used once all the archive tables will be avialbale.
                tablename = "transaction_common_archive";
            }
                /*if(tablename.equals("transaction_icicicredit")||tablename.equals("transaction_icicicredit_archive"))
                {
                    fields = "icicitransid as transid,status,name,amount,captureamount,refundamount,payoutamount,description,orderdescription,dtstamp,cardtype,cardtypeid,accountid";
                }
                else
                {*/
            fields = "t.trackingid as transid";
            // }

            query.append("select " + fields + " from " + tablename + " as t LEFT JOIN bin_details as b on t.trackingid = b.icicitransid where t.trackingid>0 and");
            query.append(" t.toid ='" + merchantid + "'");
            if (status != null)
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status  = "reversed";
                    pRefund = "true";
                }
                query.append(" and t.status='" + status + "'");
            }
            if (!"all".equalsIgnoreCase(statusflag))
            {
                query.append(" and b."+statusflag+"='Y'");
            }
            if (functions.isValueNull(desc))
                query.append(" and t.description like '" + desc + "%'");
                /*if (functions.isValueNull(terminalVO.getTerminalId()))
                    query.append(" and terminalid in " + terminalVO.getTerminalId());*/
                /*if (accountid!=null && !accountid.equals("") && !accountid.equals("null"))
                    query.append(" and accountid =" + accountid);*/

            if (functions.isValueNull(terminalVO.getTerminalId()))
                query.append(" and t.terminalid in " + terminalVO.getTerminalId());

            if (trackingid != null && !trackingid.equalsIgnoreCase(""))
            {
                    /*if(tablename.equals("transaction_icicicredit")||tablename.equals("transaction_icicicredit_archive"))
                    {
                        query.append(" and icicitransid=" + trackingid);
                    }
                    else
                    {*/
                query.append(" and t.trackingid=" + trackingid);
                //}
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds       = Long.parseLong(fdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" and t.timestamp >= '" + startDate + "'");
            }
            else if (functions.isValueNull(fdtstamp))
            {
                query.append(" and t.dtstamp >= " + fdtstamp);
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {

                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and t.timestamp <= '" + endDate + "'");
            }
            else if(functions.isValueNull(tdtstamp))
            {
                query.append(" and t.dtstamp <= " + tdtstamp);
            }
            if(functions.isValueNull(paymentId))
            {
                   /* if(tablename.equals("transaction_icicicredit"))
                    {
                        query.append(" and paymentid='" + paymentId+"'");
                    }
                    else if(tablename.equals("transaction_qwipi"))
                    {
                        query.append(" and qwipiPaymentOrderNumber='" + paymentId+"'");
                    }
                    else if(tablename.equals("transaction_ecore"))
                    {
                        query.append(" and ecorePaymentOrderNumber='" + paymentId+"'");
                    }
                    else
                    {*/
                query.append(" and t.paymentid='" + paymentId+"'");
                //}
            }

            if(functions.isValueNull(firstName))
            {
                query.append(" and t.firstname= '" + firstName+"'");
            }
            if (functions.isValueNull(lastName))
            {
                query.append(" and t.lastname= '"+lastName+"'");
            }
            if(functions.isValueNull(emailAddress))
            {
                query.append(" and t.emailaddr= '"+emailAddress+"'");
            }
            if(functions.isValueNull(customerId))
            {
                query.append(" and t.customerId= '"+customerId+"'");
            }
            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and t.captureamount > t.refundamount");
            }

            if (functions.isValueNull(issuingBank))
            {
                query.append(" and b.issuing_bank= '"+issuingBank+"'");
            }

            if (functions.isValueNull(firstsix))
            {
                query.append(" and b.first_six= '"+firstsix+"'");
            }

            if (functions.isValueNull(lastfour))
            {
                query.append(" and b.last_four= '"+lastfour+"'");
            }
            if (functions.isValueNull(transactionMode))
            {
                query.append(" and t.transaction_mode= '"+transactionMode.trim()+"'");
            }
            //appand terminalid list if desc+trackingid+paymentid not null
                /*if(!functions.isValueNull(desc) && !functions.isValueNull(trackingid) && !functions.isValueNull(paymentId))
                    query.append(" and terminalid IN "+terminalVO.getTerminalId());*/

                /*if(i.hasNext())
                    query.append(" UNION ");

            }*/


            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");

            //query.append(" order by dtstamp ,transid desc");
            query.append(" order by transid desc");

            //query.append(" limit " + start + "," + end);
            logger.debug("Fatch records from transaction issuing bank:::"+issuingBank);
            logger.debug("transaction query....."+query);


            logger.debug("Count query =="+countquery.toString());

            hash = Database.getHashMapFromResultSet(Database.executeQuery(query.toString(), connection));
            //logger.debug("hash===1===//=in transactionENTRY"+hash);
            ResultSet rs = Database.executeQuery(countquery.toString(), connection);

            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException se)
        {   logger.error("SQL Exception leaving listTransactions" ,se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        logger.debug("Leaving listTransactions");
        return hash;
    }
    // new method for export payout transaction
    public Hashtable listForPayoutTransaction(String desc, String toid,String tdtstamp, String fdtstamp, String trackingid, String status,int records,int pageno,String accountid,String emailAddr ,String amount,String bankaccount ,String dateType, String partnerid) throws SystemError
    {
        logger.debug("inside the listForPayoutTransaction for excel === for partner");
        if(!Functions.isValidSQL(trackingid) || !Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");
        }
        Connection connection       = null;
        PreparedStatement pstms     = null;
        PreparedStatement pstms1    = null;
        ResultSet rs                = null;
        Hashtable hash              = null;
        String tablename            = "";
        String fields               = "";
        int counter                 = 1;
        StringBuilder query        = new StringBuilder();

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        //status = ESAPI.encoder().encodeForSQL(me,status);
        fdtstamp    = ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp    = ESAPI.encoder().encodeForSQL(me,tdtstamp);
        desc        = ESAPI.encoder().encodeForSQL(me,desc);
        try
        {
            connection  = Database.getConnection();
            tablename   = "transaction_common";

            fields=tablename+".trackingid as transid,"+tablename+".toid,"+tablename+".description,"+tablename+".amount,"+tablename+".dtstamp,"+tablename+".emailaddr,"+tablename+".accountid,"+tablename+".currency,"+tablename+".timestamp,"+tablename+".status,"+tablename+".remark,"+tablename+".terminalid,"+tablename+".totype,ts.bankaccount,ts.ifsc,ts.fullname,m.partnerId";

            query.append(" SELECT DISTINCT "+ fields + " From "+ tablename +" join transaction_safexpay_details as ts on ts.trackingid = "+tablename+".trackingid JOIN members AS m ON transaction_common.toid=m.memberid where "+tablename+".trackingid>0 ");


            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                query.append(" and "+tablename+".timestamp >= ? ");
            }
            else
            {
                query.append(" and "+tablename+".dtstamp >= ? ");
            }

            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                query.append(" and "+tablename+".timestamp <= ? ");
            }
            else
            {
                query.append(" and "+tablename+".dtstamp <= ? ");
            }

            if (functions.isValueNull(toid))
            {
                query.append(" and "+tablename+".toid in (" );
                query.append(toid);
                query.append(" )");
            }

            if (functions.isValueNull(desc))
            {
                query.append(" and "+tablename+".description= ? ");
            }

            if(functions.isValueNull(status))
            {
                System.out.println("inside status if contion");
                query.append(" and " + tablename + ".status=?");

            }else
            {
                System.out.println("inside status else contion");
                query.append(" and "+tablename+".status in ('payoutsuccessful','payoutfailed','payoutstarted')");
            }
            if(functions.isValueNull(emailAddr))
            {
                query.append(" and "+tablename+".emailAddr=?");
            }

            if(functions.isValueNull(bankaccount))
            {
                query.append(" and ts.bankaccount="+bankaccount);
            }

            if (functions.isValueNull(trackingid.toString()))
            {
                query.append(" and "+tablename+".trackingid IN("+trackingid.toString()+")");
            }

            if(functions.isValueNull(accountid))
            {
                query.append(" and "+tablename+".accountid=?");
            }

            if(functions.isValueNull(amount))
            {
                query.append(" and "+tablename+".amount="+amount);
            }

            if (functions.isValueNull(partnerid))
            {
                query.append(" and m.partnerid="+partnerid);
            }

            StringBuilder countquery = new StringBuilder("select count(*) from ( " );
            countquery.append(query);
            countquery.append(" ) as temp ");

            query.append(" order by transid desc");
            // query.append(" limit ? , ? ");

            pstms   = connection.prepareStatement(query.toString());
            pstms1  = connection.prepareStatement(countquery.toString());


            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds       = Long.parseLong(fdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                pstms.setString(counter, startDate);
                pstms1.setString(counter, startDate);
                counter++;
            }
            else
            {
                pstms.setString(counter, fdtstamp);
                pstms1.setString(counter, fdtstamp);
                counter++;
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds       = Long.parseLong(tdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                pstms.setString(counter, endDate);
                pstms1.setString(counter, endDate);
                counter++;
            }
            else
            {
                pstms.setString(counter, tdtstamp);
                pstms1.setString(counter, tdtstamp);
                counter++;
            }
            if (functions.isValueNull(desc))
            {
                pstms.setString(counter, desc);
                pstms1.setString(counter, desc);
                counter++;
            }
            if (functions.isValueNull(status))
            {
                pstms.setString(counter, status);
                pstms1.setString(counter, status);
                counter++;
            }
            if(functions.isValueNull(emailAddr))
            {
                pstms.setString(counter, emailAddr);
                pstms1.setString(counter, emailAddr);
                counter++;
            }
            if (functions.isValueNull(accountid))
            {
                pstms1.setString(counter, accountid);
                pstms.setString(counter, accountid);
                counter++;
            }
            if (functions.isValueNull(partnerid))
            {
                pstms1.setString(counter, partnerid);
                pstms.setString(counter, partnerid);
                counter++;
            }
            logger.error("export pstmt-listForPayoutTransaction:::::"+pstms);
            logger.error("export pstmt1--listForPayoutTransaction:::::"+pstms1);

            hash    = Database.getHashFromResultSetForTransactionEntry(pstms.executeQuery());
            rs      = pstms1.executeQuery();
            System.out.println("pstms1"+pstms);
            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }catch(Exception se){
            logger.error("SQL exception leaving list transaction",se);
            se.printStackTrace();
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstms);
            Database.closePreparedStatement(pstms1);
            Database.closeConnection(connection);

        }
        logger.debug("Leaving listForPayoutTransactions"+hash);

        return  hash;
    }

    public Hashtable payoutTransactionLists(String desc,String status, String tdtstamp, String fdtstamp, String trackingid, String accountid,String emailAddress,String dateType,TerminalVO terminalVO, int pageno,int records, String bankaccount,String fullname,String ifsc, String amount,String currency, String remark,String partnerName,String partnerid,String merchantid ) throws  SystemError
    {
        logger.error("Entering payoutTransactionLists for merchant");
        if(!Functions.isValidSQL(trackingid) || !Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");

        }
        Connection connection   = null;
        Hashtable hash          = null;
        String fields           = "";
        StringBuffer query      = new StringBuffer();
        //Encoding for SQL Injection check

        Codec me    = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp    = ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp    = ESAPI.encoder().encodeForSQL(me,tdtstamp);
        desc        = ESAPI.encoder().encodeForSQL(me,desc);
        trackingid  = ESAPI.encoder().encodeForSQL(me,trackingid);

        int start   = 0; // start index
        int end     = 0; // end index
        start       = (pageno - 1) * records;
        end         = records;

        try
        {

            connection  = Database.getConnection();

            fields = "t.trackingid as transid,t.status,t.amount,t.description,t.timestamp,t.dtstamp,t.accountid,t.terminalid,t.totype,t.toid,m.partnerid,t.currency,t.remark,t.emailaddr,ts.bankaccount,ts.fullname,ts.ifsc";

            //query.append("SELECT DISTINCT " + fields + " from transaction_common as t LEFT JOIN transaction_safexpay_details as ts on t.trackingid = ts.trackingid JOIN members AS m ON t.toid=m.memberid where t.trackingid>0 and");
            query.append("SELECT DISTINCT " + fields + " from transaction_common as t LEFT JOIN transaction_safexpay_details as ts on t.trackingid = ts.trackingid JOIN members AS m ON t.toid=m.memberid where ");
            query.append(" t.toid ='" + merchantid + "'");

            if(functions.isValueNull(status) && "0".equalsIgnoreCase(status))
            {
                query.append(" and t.status IN ('payoutsuccessful','payoutfailed','payoutstarted')");

            }else
            {
                query.append(" and t.status= '"+status+"'");
            }
            if(functions.isValueNull(amount)){

                query.append(" and t.amount= '"+amount+"'");
            }

            if (functions.isValueNull(desc))
                query.append(" and t.description like '" + desc + "%'");

            if (functions.isValueNull(terminalVO.getTerminalId()))
                query.append(" and t.terminalid in " + terminalVO.getTerminalId());

            if (trackingid != null && !trackingid.equalsIgnoreCase(""))
            {
                query.append(" and t.trackingid=" + trackingid);

            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds       = Long.parseLong(fdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" and t.timestamp >= '" + startDate + "'");
            }
            else if (functions.isValueNull(fdtstamp))
            {
                query.append(" and t.dtstamp >= " + fdtstamp);
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {

                long milliSeconds       = Long.parseLong(tdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and t.timestamp <= '" + endDate + "'");
            }
            else if(functions.isValueNull(tdtstamp))
            {
                query.append(" and t.dtstamp <= " + tdtstamp);
            }

            if(functions.isValueNull(accountid))
            {
                query.append(" and t.accountid= '" + accountid+"'");
            }

            if(functions.isValueNull(emailAddress))
            {
                query.append(" and t.emailaddr= '"+emailAddress+"'");
            }


            if (functions.isValueNull(bankaccount))
            {
                query.append(" and ts.bankaccount= '"+bankaccount+"'");
            }

            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");

            query.append(" order by transid desc");

            query.append(" limit " + start + "," + end);
            logger.debug("transaction query payoutTransactionLists===="+query);


            logger.debug("Count query =="+countquery.toString());

            hash            = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), connection));
            ResultSet rs    = Database.executeQuery(countquery.toString(), connection);

            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException se)
        {   logger.error("SQL Exception leaving payoutTransactionLists" ,se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        logger.debug("Leaving payoutTransactionLists");
        return hash;
    }
    public Hashtable exportreports(TerminalVO terminalVO,InputDateVO inputDateVO,int pageno,int records)throws SystemError
    {
        logger.debug("Enter in records");
        Connection con = null;
        ResultSet rs = null;
        Hashtable hash = null;
        StringBuilder query = new StringBuilder();
        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;
        try
        {
            con = Database.getConnection();
            query.append("SELECT * FROM merchant_wiremanager WHERE markedfordeletion='N' ");
            if (terminalVO.getTerminalId() != null && terminalVO.getTerminalId() != "" && terminalVO.getTerminalId() != "null")
            {
                query.append(" and terminalid in (" + terminalVO.getTerminalId() + ")");
            }
            logger.debug("exporttoexcel"+query.toString());
            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), con));
            rs = Database.executeQuery(query.toString(), con);
            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);
            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");
            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException se)
        {
            logger.error("SQL Exception leaving listTransactionsForExport", se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
        return hash;
    }
    public Hashtable getWhiteListCardForMerchant(String firstSix,String lastFour,String emailAddr,String name,String ipAddress,String memberid,String accountId) throws PZDBViolationException
    {
        System.out.println(" enter in getWhiteListCardForMerchant123------");
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Hashtable hash = null;
        StringBuffer query1     = new StringBuffer();
        Connection connection=null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Functions functions = new Functions();
        try
        {
            connection = Database.getConnection();
            query1.append("SELECT id,firstsix,lastfour,emailAddr,accountid,name,ipAddress FROM whitelist_details WHERE id>0 AND istemp='N'");
            StringBuffer countQuery = new StringBuffer("SELECT COUNT(*) FROM whitelist_details WHERE id>0 AND istemp='N' ");

            if (functions.isValueNull(memberid))
            {
                query1.append(" and memberid='"  + memberid+"'");
                countQuery.append(" and memberid='" + memberid+"'");
            }
            if(firstSix!=null && firstSix.equals(" "))
            {
                query1.append(" and firstsix='"  + firstSix+"'");
                countQuery.append(" and firstsix='" + firstSix+"'");
            }
            if(lastFour!=null && lastFour.equals(" "))
            {
                query1.append(" and lastfour='"+ lastFour+"'");
                countQuery.append(" and lastfour='" + lastFour+"'");
            }

            if (functions.isValueNull(emailAddr))
            {
                query1.append(" and emailAddr='" + emailAddr + "'");
                countQuery.append(" and emailAddr='" + emailAddr + "'");
            }
            if (functions.isValueNull(name))
            {
                query1.append(" and name='" + name + "'");
                countQuery.append(" and name='" + name + "'");
            }
            if (functions.isValueNull(ipAddress))
            {
                query1.append(" and ipAddress='" + ipAddress + "'");
                countQuery.append(" and ipAddress='" + ipAddress + "'");
            }
            if (functions.isValueNull(accountId))
            {
                query1.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me, accountId));
                countQuery.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me, accountId));
            }
            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query1.toString(), connection));
            rs = Database.executeQuery(countQuery.toString(), connection);
            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);
            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");
            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }


        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException(TransactionEntry.class.getName(), "getWhiteListCardForMerchant()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(TransactionEntry.class.getName(), "getWhiteListCardForMerchant()", null, "Common", "Sql exception while connecting to whitelist_details table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return hash;
    }

}
