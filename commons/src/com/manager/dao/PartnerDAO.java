package com.manager.dao;

import com.directi.pg.*;
import com.manager.DateManager;
import com.manager.enums.PartnerTemplatePreference;
import com.manager.enums.TemplatePreference;
import com.manager.vo.*;
import com.manager.vo.merchantmonitoring.DateVO;
import com.manager.vo.partnerMerchantVOs.TokenVo;
import com.manager.vo.payoutVOs.PartnerWireVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/11/15
 * Time: 1:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerDAO
{
    private static Logger log                           = new Logger(PartnerDAO.class.getName());
    private static TransactionLogger transactionLogger  = new TransactionLogger(PartnerDAO.class.getName());
    private static Functions functions                  = new Functions();
    DateManager dateManager= new DateManager();
   // Functions functions= new Functions();

    public static Hashtable getMemberTemplateDetails(String merchantid) throws SystemError
    {
        log.debug(" Entering getMemberTemplatDetails");
        Hashtable detailhash    = null;
        Connection con          = null;
        PreparedStatement pstmt = null;
        try
        {
            con             = Database.getRDBConnection();
            String query    = "select name,value from template_preferences where memberid= ? ";
            pstmt           = con.prepareStatement(query);
            pstmt.setString(1, merchantid);
            detailhash = Functions.getDetailedHashFromResultSet(pstmt.executeQuery());
        }
        catch (Exception e)
        {
            log.error("Leaving Merchants throwing SQL Exception as System Error : ", e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        if (detailhash != null)
        {
            return detailhash;
        }
        else
        {
            return new Hashtable();
        }
    }
    public static void ChangeTemplate(Hashtable details, String merchantid) throws SystemError
    {
        log.debug("entering inside ChangeTemplate");
        Hashtable merchantTemplateHash = getMemberTemplateDetails(merchantid);
        StringBuffer query;
        Enumeration detailsEnum = details.keys();
        Connection con          = Database.getRDBConnection();
        PreparedStatement p1    = null;
        try
        {
            while (detailsEnum.hasMoreElements())
            {
                String name = (String) detailsEnum.nextElement();
                if (merchantTemplateHash != null)
                {
                    if (merchantTemplateHash.get(name) != null)
                    {
                        query = new StringBuffer("update template_preferences set ");
                        query.append("value=?  ");
                        query.append("where memberid =? and name=?");

                        p1 = con.prepareStatement(query.toString());
                        p1.setString(1, (String) details.get(name));
                        p1.setString(2, merchantid);
                        p1.setString(3, name);
                    }
                    else
                    {
                        query = new StringBuffer("insert into template_preferences(memberid,name,value) values (?,?,?)");

                        p1 = con.prepareStatement(query.toString());
                        p1.setString(1, merchantid);
                        p1.setString(2, name);
                        p1.setObject(3, details.get(name));
                    }
                }
                else
                {
                    query = new StringBuffer("insert into template_preferences(memberid,name,value) values ( ?,?,?)");

                    p1 = con.prepareStatement(query.toString());
                    p1.setString(1, merchantid);
                    p1.setString(2, name);
                    p1.setObject(3, details.get(name));
                }
                p1.executeUpdate();
            }
        }
        catch (SQLException se)
        {
            log.error("SQLException occure", se);
        }
        catch (Exception e)
        {
            log.error("Exception ::::", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closePreparedStatement(p1);
            Database.closeConnection(con);
        }
        log.debug("leaving ChangeTemplate");
    }

    public static Hashtable getPartnerWireList(String toid, String accountid, String terminalid, String partnerId, String gateway, String is_paid, String fdtstamp, String tdtstamp, int pageno, int records)
    {
        Functions functions     = new Functions();
        Hashtable hash          = new Hashtable();
        Connection connection   = null;
        ResultSet rs            = null;
        Codec me                = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        int start   = 0; // start index
        int end     = 0; // end index
        start       = (pageno - 1) * records;
        end         = records;

        try
        {

            connection              = Database.getRDBConnection();
            StringBuffer query      = new StringBuffer("SELECT settledid,settlementstartdate,settlementenddate,partnerchargeamount,partnerunpaidamount,partnertotalfundedamount,currency,status,settlementreportfilename,markedfordeletion,partnerid,TIMESTAMP,toid,terminalid,accountid,paymodeid,cardtypeid,declinedcoverdateupto,reversedcoverdateupto,chargebackcoverdateupto,FROM_UNIXTIME(wirecreationtime) AS 'wirecreationtime'  FROM partner_wiremanager WHERE markedfordeletion='N'");
            StringBuffer countquery = new StringBuffer("SELECT count(*) FROM partner_wiremanager WHERE markedfordeletion='N'");
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and wirecreationtime  >= " + ESAPI.encoder().encodeForSQL(me, fdtstamp));
                countquery.append(" and wirecreationtime >= " + ESAPI.encoder().encodeForSQL(me, fdtstamp));
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and wirecreationtime <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
                countquery.append(" and wirecreationtime <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
            }
            if (functions.isValueNull(partnerId) && !partnerId.equals("0"))
            {
                query.append(" and partnerid='" + ESAPI.encoder().encodeForSQL(me, partnerId) + "'");
                countquery.append(" and partnerid='" + ESAPI.encoder().encodeForSQL(me, partnerId) + "'");
            }
            if (functions.isValueNull(toid) && !toid.equals("0"))
            {
                query.append(" and toid='" + ESAPI.encoder().encodeForSQL(me, toid) + "'");
                countquery.append(" and toid='" + ESAPI.encoder().encodeForSQL(me, toid) + "'");
            }
            if (functions.isValueNull(accountid) && !accountid.equals("0"))
            {
                query.append(" and accountid in (" + accountid + ")");
                countquery.append(" and accountid in (" + accountid + ")");
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
            query.append(" ORDER BY wirecreationtime,toid DESC limit " + start + "," + end);
            log.debug("Query:-" + query);
            log.debug("CountQuery:-" + countquery);

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
            {
                hash.put("records", "" + (hash.size() - 2));
            }

            log.debug("wire hash---" + hash);
        }
        catch (SystemError systemError)
        {
            log.error("SystemError----" + systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException---" + e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(connection);
        }
        return hash;
    }

    /*public static Hashtable getMemberMapping(String accountid, String memberid, String partnerid)
    {
        Hashtable hash = null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer query = new StringBuffer("SELECT m.memberid,m.accountid,m.paymodeid,m.cardtypeid,m.monthly_amount_limit,m.daily_amount_limit,m.daily_card_limit,m.weekly_card_limit,m.monthly_card_limit,m.daily_card_amount_limit,m.weekly_card_amount_limit,m.monthly_card_amount_limit,m.min_transaction_amount,m.max_transaction_amount,m.isActive,m.priority,m.isTest,m.isPSTTerminal,m.isCardEncryptionEnable,m.isTokenizationActive,m.terminalid,m.weekly_amount_limit,m.daily_avg_ticket,m.weekly_avg_ticket,m.monthly_avg_ticket,m.addressDetails,m.addressValidation,m.riskruleactivation,m.min_payout_amount,m.settlement_currency,m.binRouting,mem.personal_info_display,mem.personal_info_validation,mem.hosted_payment_page,mem.vbvLogo,mem.masterSecureLogo,m.isCardWhitelisted,m.isEmailWhitelisted,m.emi_support,m.whitelisting_details,m.cardLimitCheck,m.cardAmountLimitCheck,m.amountLimitCheck FROM member_account_mapping AS m JOIN members AS mem ON m.memberid=mem.memberid AND mem.partnerid=" + partnerid);
        StringBuffer countquery = new StringBuffer("SELECT count(*) FROM member_account_mapping AS m JOIN members AS mem ON m.memberid=mem.memberid AND mem.partnerid=" + partnerid);
        if (functions.isValueNull(memberid) && !functions.isValueNull(accountid))
        {
            query.append(" where m.memberid=" + ESAPI.encoder().encodeForSQL(me, memberid));
            countquery.append(" where m.memberid=" + ESAPI.encoder().encodeForSQL(me, memberid));
        }
        else if (functions.isValueNull(accountid) && !functions.isValueNull(memberid))
        {
            query.append(" where m.accountid=" + ESAPI.encoder().encodeForSQL(me, accountid));
            countquery.append(" where m.accountid=" + ESAPI.encoder().encodeForSQL(me, accountid));
        }
        else if (functions.isValueNull(accountid) && functions.isValueNull(memberid))
        {
            query.append(" where m.accountid=" + ESAPI.encoder().encodeForSQL(me, accountid));
            query.append(" and m.memberid=" + ESAPI.encoder().encodeForSQL(me, memberid));
            countquery.append(" where m.accountid=" + ESAPI.encoder().encodeForSQL(me, accountid));
            countquery.append(" and m.memberid=" + ESAPI.encoder().encodeForSQL(me, memberid));
        }
        query.append(" order by m.memberid ");
        log.debug("---query---" + query);


        Connection con = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            if (true)
            {
                hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), con));
                rs = Database.executeQuery(countquery.toString(), con);
                rs.next();
                int totalrecords = rs.getInt(1);
                hash.put("totalrecords", "" + totalrecords);
                hash.put("records", "0");
                if (totalrecords > 0)
                {
                    hash.put("records", "" + (hash.size() - 2));
                }
            }
            else
            {
                hash = new Hashtable();
                hash.put("records", "0");
                hash.put("totalrecords", "0");
            }
        }
        catch (SystemError se)
        {
            log.error("System Error::::", se);
        }
        catch (Exception e)
        {
            log.error("Exception::::", e);
        }
        finally
        {
            try
            {
                con.close();
            }
            catch (SQLException e)
            {
                log.error("SQL Exception::::", e);
            }
            finally
            {
                Database.closeResultSet(rs);
                Database.closeConnection(con);
            }
            return hash;
        }
    }*/

    //CHANGED QUERY AS PER SUPERPARTNER MODIFICATION.
    public static Hashtable getMemberMapping(String accountid, String memberid, String partnerid,String paymodeid, String cardtypeid, String ActiveOrInactive, String ispayoutactive)
    {
        Hashtable hash          = null;
        Codec me                = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        //CHANGED THE QUERY TO FETCH MEMBERS AS PER PARTNER AND SUPERPARTNER.
        StringBuffer query      = new StringBuffer("SELECT m.memberid,m.accountid,m.paymodeid,m.cardtypeid,m.monthly_amount_limit,m.daily_amount_limit,m.daily_card_limit,m.weekly_card_limit," +
                "m.monthly_card_limit,m.daily_card_amount_limit,m.weekly_card_amount_limit,m.monthly_card_amount_limit,m.min_transaction_amount,m.max_transaction_amount,m.isActive,m.priority," +
                "m.isTest,m.isPSTTerminal,m.isCardEncryptionEnable,m.isTokenizationActive,m.terminalid,m.weekly_amount_limit,m.daily_avg_ticket,m.weekly_avg_ticket,m.monthly_avg_ticket," +
                "m.addressDetails,m.addressValidation,m.riskruleactivation,m.min_payout_amount,m.settlement_currency,m.binRouting,mem.personal_info_display,mem.personal_info_validation," +
                "mem.hosted_payment_page,mem.vbvLogo,mem.masterSecureLogo,m.isCardWhitelisted,m.isEmailWhitelisted,m.emi_support,m.whitelisting_details,m.cardLimitCheck,m.cardAmountLimitCheck," +
                "m.amountLimitCheck,m.is_recurring,m.isRestrictedTicketActive,m.isManualRecurring,m.cardDetailRequired,m.payoutActivation,m.autoRedirectRequest,m.currency_conversion," +
                "m.conversion_currency,m.isCardEncryptionEnable,m.actionExecutorId,m.actionExecutorName,m.payout_priority,m.daily_amount_limit_check,m.weekly_amount_limit_check,m.monthly_amount_limit_check," +
                "m.daily_card_limit_check,m.weekly_card_limit_check,m.monthly_card_limit_check,m.daily_card_amount_limit_check,m.weekly_card_amount_limit_check,m.monthly_card_amount_limit_check " +
                " FROM member_account_mapping AS m JOIN members AS mem ON m.memberid=mem.memberid JOIN partners p ON mem.partnerId=p.partnerId AND (p.superadminid=" + partnerid+" OR mem.partnerid=" + partnerid+")");
        StringBuffer countquery = new StringBuffer("SELECT count(*) FROM member_account_mapping AS m JOIN members AS mem ON m.memberid=mem.memberid JOIN partners p ON mem.partnerId=p.partnerId AND (p.superadminid=" + partnerid+" OR mem.partnerid=" + partnerid+")");

        if (functions.isValueNull(memberid) && !functions.isValueNull(accountid) && !functions.isValueNull(paymodeid) && !functions.isValueNull(cardtypeid))
        {
            query.append(" where m.memberid=" + ESAPI.encoder().encodeForSQL(me, memberid));
            countquery.append(" where m.memberid=" + ESAPI.encoder().encodeForSQL(me, memberid));
        }
         if (functions.isValueNull(accountid) && !functions.isValueNull(memberid) && !functions.isValueNull(paymodeid) && !functions.isValueNull(cardtypeid))
        {
            query.append(" where m.accountid=" + ESAPI.encoder().encodeForSQL(me, accountid));
            countquery.append(" where m.accountid=" + ESAPI.encoder().encodeForSQL(me, accountid));
        }
         if (functions.isValueNull(memberid) && !functions.isValueNull(accountid) && (functions.isValueNull(paymodeid) || functions.isValueNull(cardtypeid)))
        {
            query.append(" where m.memberid=" + ESAPI.encoder().encodeForSQL(me, memberid));
            countquery.append(" where m.memberid=" + ESAPI.encoder().encodeForSQL(me, memberid));

            if (functions.isValueNull(paymodeid))
            {
                query.append(" and m.paymodeid=" + ESAPI.encoder().encodeForSQL(me, paymodeid));
                countquery.append(" and m.paymodeid=" + ESAPI.encoder().encodeForSQL(me, paymodeid));
            }
            if (functions.isValueNull(cardtypeid))
            {
                query.append(" and m.cardtypeid=" +ESAPI.encoder().encodeForSQL(me, cardtypeid));
                countquery.append(" and m.cardtypeid=" + ESAPI.encoder().encodeForSQL(me, cardtypeid));
            }
        }
         if (functions.isValueNull(accountid) && !functions.isValueNull(memberid) && (functions.isValueNull(paymodeid) || functions.isValueNull(cardtypeid)))
        {
            query.append(" where m.accountid=" + ESAPI.encoder().encodeForSQL(me, accountid));
            countquery.append(" where m.accountid=" + ESAPI.encoder().encodeForSQL(me, accountid));

            if (functions.isValueNull(paymodeid))
            {
                query.append(" and m.paymodeid=" + ESAPI.encoder().encodeForSQL(me, paymodeid));
                countquery.append(" and m.paymodeid=" + ESAPI.encoder().encodeForSQL(me, paymodeid));
            }
            if (functions.isValueNull(cardtypeid))
            {
                query.append(" and m.cardtypeid=" + ESAPI.encoder().encodeForSQL(me, cardtypeid));
                countquery.append(" and m.cardtypeid=" + ESAPI.encoder().encodeForSQL(me, cardtypeid));
            }
        }
        if(functions.isValueNull(memberid) && functions.isValueNull(accountid) && !functions.isValueNull(paymodeid) && !functions.isValueNull(cardtypeid))
        {
            query.append(" where m.memberid=" + ESAPI.encoder().encodeForSQL(me, memberid));
            query.append(" and m.accountid=" + ESAPI.encoder().encodeForSQL(me, accountid));
            countquery.append(" where m.memberid=" + ESAPI.encoder().encodeForSQL(me, memberid));
            countquery.append(" and m.accountid=" + ESAPI.encoder().encodeForSQL(me, accountid));
        }
        if (functions.isValueNull(memberid) && functions.isValueNull(accountid) && functions.isValueNull(cardtypeid) && !functions.isValueNull(paymodeid))
        {
            query.append(" where m.memberid=" + ESAPI.encoder().encodeForSQL(me, memberid));
            query.append(" and m.accountid=" + ESAPI.encoder().encodeForSQL(me, accountid));
            countquery.append(" where m.memberid=" + ESAPI.encoder().encodeForSQL(me, memberid));
            countquery.append(" and m.accountid=" + ESAPI.encoder().encodeForSQL(me, accountid));
            query.append(" and m.cardtypeid=" + ESAPI.encoder().encodeForSQL(me, cardtypeid));
            countquery.append(" and m.cardtypeid=" + ESAPI.encoder().encodeForSQL(me, cardtypeid));
        }
        if (functions.isValueNull(memberid) && functions.isValueNull(accountid) && functions.isValueNull(paymodeid) && !functions.isValueNull(cardtypeid))
        {
            query.append(" where m.memberid=" + ESAPI.encoder().encodeForSQL(me, memberid));
            query.append(" and m.accountid=" + ESAPI.encoder().encodeForSQL(me, accountid));
            countquery.append(" where m.memberid=" + ESAPI.encoder().encodeForSQL(me, memberid));
            countquery.append(" and m.accountid=" + ESAPI.encoder().encodeForSQL(me, accountid));
            query.append(" and m.paymodeid=" + ESAPI.encoder().encodeForSQL(me, paymodeid));
            countquery.append(" and m.paymodeid=" + ESAPI.encoder().encodeForSQL(me, paymodeid));
        }
         if (functions.isValueNull(memberid) && functions.isValueNull(accountid) && functions.isValueNull(paymodeid) && functions.isValueNull(cardtypeid))
        {
            query.append(" where m.memberid=" + ESAPI.encoder().encodeForSQL(me, memberid));
            query.append(" and m.accountid=" + ESAPI.encoder().encodeForSQL(me, accountid));
            query.append(" and m.paymodeid=" +ESAPI.encoder().encodeForSQL(me, paymodeid));
            query.append(" and m.cardtypeid=" +ESAPI.encoder().encodeForSQL(me, cardtypeid));
            countquery.append(" where m.memberid=" + ESAPI.encoder().encodeForSQL(me, memberid));
            countquery.append(" and m.accountid=" + ESAPI.encoder().encodeForSQL(me, accountid));
            countquery.append(" and m.paymodeid=" + ESAPI.encoder().encodeForSQL(me, paymodeid));
            countquery.append(" and m.cardtypeid=" + ESAPI.encoder().encodeForSQL(me, cardtypeid));
        }
        if (functions.isValueNull(ActiveOrInactive))
        {
            query.append(" and m.isActive='" + ESAPI.encoder().encodeForSQL(me, ActiveOrInactive) +"'");
            countquery.append(" and m.isActive='" + ESAPI.encoder().encodeForSQL(me, ActiveOrInactive)+"'");
        }
        if (functions.isValueNull(ispayoutactive))
        {
            query.append(" and m.payoutActivation='" +ESAPI.encoder().encodeForSQL(me,ispayoutactive)+"'");
            countquery.append(" and m.payoutActivation='" +ESAPI.encoder().encodeForSQL(me,ispayoutactive)+"'");
        }
        query.append(" order by m.memberid ");
        log.error("---query for  member mapping---" + query);


        Connection con  = null;
        ResultSet rs    = null;
        try
        {
            con = Database.getRDBConnection();
            if (true)
            {
                hash    = Database.getHashFromResultSet(Database.executeQuery(query.toString(), con));
                rs      = Database.executeQuery(countquery.toString(), con);
                rs.next();
                int totalrecords = rs.getInt(1);
                hash.put("totalrecords", "" + totalrecords);
                hash.put("records", "0");
                if (totalrecords > 0)
                {
                    hash.put("records", "" + (hash.size() - 2));
                }
            }
            else
            {
                hash = new Hashtable();
                hash.put("records", "0");
                hash.put("totalrecords", "0");
            }
        }
        catch (SystemError se)
        {
            log.error("System Error::::", se);
        }
        catch (Exception e)
        {
            log.error("Exception::::", e);
        }
        finally
        {
            try
            {
                con.close();
            }
            catch (SQLException e)
            {
                log.error("SQL Exception::::", e);
            }
            finally
            {
                Database.closeResultSet(rs);
                Database.closeConnection(con);
            }
            return hash;
        }
    }

    public static List<MerchantDetailsVO> getMemberListForFraud(String partnerId)
    {
        MerchantDetailsVO merchantDetailsVO = null;
        List<MerchantDetailsVO> memberList = new ArrayList();
        Connection conn             = null;
        PreparedStatement pstmt     = null;
        ResultSet rs                = null;
        try
        {
            conn            = Database.getRDBConnection();
            String query    = "select memberid, company_name,activation from members where partnerId =? ORDER BY memberid ASC";
            pstmt           = conn.prepareStatement(query);
            pstmt.setString(1, partnerId);
            rs              = pstmt.executeQuery();
            while (rs.next())
            {
                merchantDetailsVO = new MerchantDetailsVO();
                merchantDetailsVO.setMemberId(rs.getString("memberid"));
                merchantDetailsVO.setCompany_name(rs.getString("company_name"));
                merchantDetailsVO.setActivation(rs.getString("activation"));
                memberList.add(merchantDetailsVO);
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception while loading MemberList", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return memberList;
    }

    public static TreeMap<String, String> getAccountsDetailsMemberid( String gatewayid, String accountid, String partnerid)
    {
        Connection con              = null;
        PreparedStatement psmt      = null;
        Functions functions         = new Functions();
        TreeMap<String, String> map = new TreeMap();
        int counter                 = 1;
        //StringBuilder query = new StringBuilder("SELECT mam.memberid, ga.accountid,ga.merchantid,gt.currency FROM member_account_mapping AS mam, gateway_accounts AS ga, gateway_type AS gt, gateway_account_partner_mapping AS gacpm  WHERE mam.accountid = ga.accountid AND ga.pgtypeid = gt.pgtypeid AND ga.accountid=gacpm.accountid");
        StringBuilder query = new StringBuilder("SELECT mam.memberid,m.company_name FROM member_account_mapping AS mam, members AS m, gateway_accounts AS ga, gateway_type AS gt, gateway_account_partner_mapping AS gacpm  WHERE mam.memberid = m.memberid and mam.accountid = ga.accountid AND ga.pgtypeid = gt.pgtypeid AND ga.accountid=gacpm.accountid AND gacpm.partnerid = m.partnerId");
        try
        {
            con = Database.getConnection();
            if (functions.isValueNull(gatewayid))
            {
                query.append(" AND ga.pgtypeid = ?");
            }
            if (functions.isValueNull(accountid))
            {
                query.append(" AND mam.accountid = ?");
            }
            if (functions.isValueNull(partnerid))
            {
                query.append(" AND gacpm.partnerid = ?");
            }
            psmt = con.prepareStatement(query.toString());

            if (functions.isValueNull(gatewayid))
            {
                psmt.setString(counter, gatewayid);
                counter++;
            }
            if (functions.isValueNull(accountid))
            {
                psmt.setString(counter, accountid);
                counter++;
            }
            if (functions.isValueNull(partnerid))
            {
                psmt.setString(counter, partnerid);
            }
            ResultSet rs = psmt.executeQuery();
            log.debug("Query memberid:::::::::::::::" + psmt.toString());
            while (rs.next())
            {
                map.put(rs.getString("memberid"),rs.getString("memberid")+"-"+rs.getString("company_name"));
            }

        }
        catch (SystemError s)
        {
            log.error("System error while performing select query",s);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (SQLException e)
        {
            log.error("SQL error",e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeConnection(con);
        }
        return map;
    }


    public static TreeMap<String, String> getAccountsDetailsMemberid_superpartner( String gatewayid, String accountid, String partnerid)
    {
        Connection con              = null;
        PreparedStatement psmt      = null;
        Functions functions         = new Functions();
        TreeMap<String, String> map = new TreeMap();
        int counter = 1;
        //StringBuilder query = new StringBuilder("SELECT mam.memberid, ga.accountid,ga.merchantid,gt.currency FROM member_account_mapping AS mam, gateway_accounts AS ga, gateway_type AS gt, gateway_account_partner_mapping AS gacpm  WHERE mam.accountid = ga.accountid AND ga.pgtypeid = gt.pgtypeid AND ga.accountid=gacpm.accountid");
        StringBuilder query = new StringBuilder("SELECT mam.memberid,m.company_name FROM member_account_mapping AS mam, members AS m, gateway_accounts AS ga, gateway_type AS gt, gateway_account_partner_mapping AS gacpm ,partners AS p WHERE mam.memberid = m.memberid and gacpm.partnerid = p.partnerId and mam.accountid = ga.accountid AND ga.pgtypeid = gt.pgtypeid AND ga.accountid=gacpm.accountid AND gacpm.partnerid = m.partnerId");
        try
        {
            con = Database.getConnection();
            if (functions.isValueNull(gatewayid))
            {
                query.append(" AND ga.pgtypeid = ?");
            }
            if (functions.isValueNull(accountid))
            {
                query.append(" AND mam.accountid = ?");
            }
            if (functions.isValueNull(partnerid))
            {
                query.append(" AND (gacpm.partnerid = ? OR p.superadminid=?)");
            }
            psmt = con.prepareStatement(query.toString());

            if (functions.isValueNull(gatewayid))
            {
                psmt.setString(counter, gatewayid);
                counter++;
            }
            if (functions.isValueNull(accountid))
            {
                psmt.setString(counter, accountid);
                counter++;
            }
            if (functions.isValueNull(partnerid))
            {
                psmt.setString(counter, partnerid);
                counter++;
                psmt.setString(counter, partnerid);
            }
            ResultSet rs = psmt.executeQuery();
            log.debug("Query memberid:::::::::::::::"+psmt.toString());
            while (rs.next())
            {
                map.put(rs.getString("memberid"),rs.getString("memberid")+"-"+rs.getString("company_name"));
            }

        }
        catch (SystemError s)
        {
            log.error("System error while performing select query",s);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (SQLException e)
        {
            log.error("SQL error",e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeConnection(con);
        }
        return map;
    }

    public CommonValidatorVO getPartnerDetails(String toid, CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        Connection conn         = null;
        PreparedStatement pstmt = null;
        ResultSet res           = null;
        try
        {
            //conn = Database.getConnection();
            conn        = Database.getRDBConnection();
            log.debug("Entering getMember Details");
            String query    = "SELECT partnerId,logoName,partnerName FROM partners WHERE partnerid IN (SELECT partnerId FROM members WHERE memberid=?)";
            pstmt           = conn.prepareStatement(query);
            pstmt.setString(1, toid);
            res = pstmt.executeQuery();
            if (res.next())
            {
                commonValidatorVO.setParetnerId(res.getString("partnerId"));
                commonValidatorVO.setPartnerName(res.getString("partnerName"));
                commonValidatorVO.setLogoName(res.getString("logoName"));
            }
        }
        catch (SystemError se)
        {
            log.error(":::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TransactionUtility.java", "getPartnerDetails()", null, "Transaction", "SQL Exception Thrown:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            log.error(":::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TransactionUtility.class", "getPartnerDetails()", null, "Transaction", "SQL Exception Thrown:::::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return commonValidatorVO;
    }

    public PartnerDetailsVO getPartnerDetails(String partnerId) throws PZDBViolationException
    {
        Connection conn         = null;
        PreparedStatement pstmt = null;
        ResultSet rs            = null;
        String query            = null;
        PartnerDetailsVO partnerDetailsVO = null;
        try
        {
            conn    = Database.getRDBConnection();
            query   = "SELECT partnerId,partnerName,partnerOrgnizationForWL_Invoice,contact_persons,logoName,clkey,isTokenizationAllowed,isMerchantRequiredForCardRegistration,isAddressRequiredForTokenTransaction,isMerchantRequiredForCardholderRegistration,isCardEncryptionEnable,responseType,responseLength,hosturl,address,city,state,zip,country,telno,faxno,reporting_currency,ip_whitelist_invoice,address_validation_invoice,addressvalidation,is_rest_whitelisted,reportfile_bgcolor,companysupportmailid,processor_partnerid,monthly_min_commission_module,partner_short_name,profit_share_commission_module,supporturl,documentationurl FROM partners WHERE partnerId=?";
            pstmt   = conn.prepareStatement(query);
            pstmt.setString(1, partnerId);
            transactionLogger.error("query--------->"+pstmt);
            rs      = pstmt.executeQuery();
            if (rs.next())
            {
                partnerDetailsVO = new PartnerDetailsVO();
                partnerDetailsVO.setPartnerId(rs.getString("partnerId"));
                partnerDetailsVO.setOrganizationName(rs.getString("partnerOrgnizationForWL_Invoice"));
                partnerDetailsVO.setCompanyName(rs.getString("partnerName"));
                partnerDetailsVO.setContactPerson(rs.getString("contact_persons"));
                partnerDetailsVO.setLogoName(rs.getString("logoName"));
                partnerDetailsVO.setPartnerKey(rs.getString("clkey"));
                partnerDetailsVO.setIsTokenizationAllowed(rs.getString("isTokenizationAllowed"));
                partnerDetailsVO.setIsMerchantRequiredForCardRegistration(rs.getString("isMerchantRequiredForCardRegistration"));
                partnerDetailsVO.setIsAddressRequiredForTokenTransaction(rs.getString("isAddressRequiredForTokenTransaction"));
                partnerDetailsVO.setIsMerchantRequiredForCardholderRegistration(rs.getString("isMerchantRequiredForCardholderRegistration"));
                partnerDetailsVO.setIsCardEncryptionEnable(rs.getString("isCardEncryptionEnable"));
                partnerDetailsVO.setResponseType(rs.getString("responseType"));
                partnerDetailsVO.setResponseLength(rs.getString("responseLength"));
                partnerDetailsVO.setHostUrl(rs.getString("hosturl"));
                partnerDetailsVO.setAddress(rs.getString("address"));
                partnerDetailsVO.setCity(rs.getString("city"));
                partnerDetailsVO.setState(rs.getString("state"));
                partnerDetailsVO.setZip(rs.getString("zip"));
                partnerDetailsVO.setTelno(rs.getString("telno"));
                partnerDetailsVO.setFaxNo(rs.getString("faxno"));
                partnerDetailsVO.setReportingCurrency(rs.getString("reporting_currency"));
                partnerDetailsVO.setIpWhitelistInvoice(rs.getString("ip_whitelist_invoice"));
                partnerDetailsVO.setAddressValidationInvoice(rs.getString("address_validation_invoice"));
                partnerDetailsVO.setIsIpWhiteListedCheckForAPIs(rs.getString("is_rest_whitelisted"));
                partnerDetailsVO.setAddressvalidation(rs.getString("addressvalidation"));
                partnerDetailsVO.setReportFileBGColor(rs.getString("reportfile_bgcolor"));
                partnerDetailsVO.setSupportMailId(rs.getString("companysupportmailid"));
                partnerDetailsVO.setProcessorPartnerId(rs.getString("processor_partnerid"));
                partnerDetailsVO.setMonthlyMinCommissionModule(rs.getString("monthly_min_commission_module"));
                partnerDetailsVO.setPartnerShortName(rs.getString("partner_short_name"));
                partnerDetailsVO.setProfitShareCommissionModule(rs.getString("profit_share_commission_module"));
                partnerDetailsVO.setSupporturl(rs.getString("supporturl"));
                partnerDetailsVO.setDocumentationurl(rs.getString("documentationurl"));
            }
        }
        catch (SQLException e)
        {
            log.error("Leaving Merchants throwing SQL Exception as System Error :::: ", e);
            PZExceptionHandler.raiseDBViolationException("PartnerDAO", "getPartnerDetails()", null, "Common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            log.error("Leaving Merchants throwing SQL Exception as System Error :::: ", se);
            PZExceptionHandler.raiseDBViolationException("PartnerDAO", "getPartnerDetails()", null, "Common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());

        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        log.debug("Leaving getMemberDetails");

        return partnerDetailsVO;
    }

    public PartnerWireVO getSinglePartnerWire(String wireId) throws SystemError, SQLException
    {
        Connection connection                   = null;
        PartnerWireVO partnerWireVO             = null;
        PreparedStatement preparedStatement     = null;
        ResultSet rs                            = null;
        try
        {
            connection          = Database.getRDBConnection();
            String qry          = "SELECT settledid,settledate,settlementstartdate,settlementenddate,partnerchargeamount,partnerunpaidamount,partnertotalfundedamount,currency,STATUS,settlementreportfilename,markedfordeletion,partnerid,TIMESTAMP,toid,terminalid,accountid,paymodeid,cardtypeid,declinedcoverdateupto,reversedcoverdateupto,chargebackcoverdateupto,FROM_UNIXTIME(wirecreationtime) AS 'wirecreationdate',partnertype FROM partner_wiremanager WHERE settledid=?";
            preparedStatement   = connection.prepareStatement(qry);
            preparedStatement.setString(1, wireId);
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                partnerWireVO = new PartnerWireVO();
                partnerWireVO.setSettledId(rs.getString("settledid"));
                partnerWireVO.setSettleDate(rs.getString("settledate"));
                partnerWireVO.setSettlementStartDate(rs.getString("settlementstartdate"));
                partnerWireVO.setSettlementEndDate(rs.getString("settlementenddate"));
                partnerWireVO.setPartnerChargeAmount(rs.getDouble("partnerchargeamount"));
                partnerWireVO.setPartnerUnpaidAmount(rs.getDouble("partnerunpaidamount"));
                partnerWireVO.setPartnerTotalFundedAmount(rs.getDouble("partnertotalfundedamount"));
                partnerWireVO.setCurrency(rs.getString("currency"));
                partnerWireVO.setStatus(rs.getString("status"));
                partnerWireVO.setSettlementReportFileName(rs.getString("settlementreportfilename"));
                partnerWireVO.setMarkedForDeletion(rs.getString("markedfordeletion"));
                partnerWireVO.setPartnerId(rs.getString("partnerid"));
                partnerWireVO.setTimestamp(rs.getString("timestamp"));
                partnerWireVO.setMemberId(rs.getString("toid"));
                partnerWireVO.setTerminalId(rs.getString("terminalid"));
                partnerWireVO.setAccountId(rs.getString("accountid"));
                partnerWireVO.setPayModeId(rs.getString("paymodeid"));
                partnerWireVO.setCardTypeId(rs.getString("cardtypeid"));
                partnerWireVO.setDeclinedCoverDateUpTo(rs.getString("declinedcoverdateupto"));
                partnerWireVO.setReversedCoverDateUpTo(rs.getString("reversedcoverdateupto"));
                partnerWireVO.setChargebackCoverDateUpTo(rs.getString("chargebackcoverdateupto"));
                partnerWireVO.setWireCreationDate(rs.getString("wirecreationdate"));
                partnerWireVO.setPartnerType(rs.getString("partnertype"));
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return partnerWireVO;
    }

    public boolean setPartnerWireMarkForDelete(String wireId) throws SystemError, SQLException
    {
        boolean flag                        = false;
        Connection connection               = null;
        PreparedStatement preparedStatement = null;
        try
        {
            connection          = Database.getConnection();
            String qry          = "UPDATE partner_wiremanager SET markedfordeletion='Y' WHERE settledid=?";
            preparedStatement   = connection.prepareStatement(qry);
            preparedStatement.setString(1, wireId);
            int i = preparedStatement.executeUpdate();
            if (i == 1)
            {
                flag = true;
            }
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return flag;
    }

    public String getPartnerWireReportFileName(String wireId) throws SystemError, SQLException
    {
        Connection connection               = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs                        = null;
        String settlementReportFileName     = null;
        try
        {
            connection          = Database.getRDBConnection();
            String qry          = "SELECT settlementreportfilename FROM partner_wiremanager WHERE settledid=?";
            preparedStatement   = connection.prepareStatement(qry);
            preparedStatement.setString(1, wireId);
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                settlementReportFileName = rs.getString("settlementreportfilename");
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

    public void updatePartnerWire(PartnerWireVO partnerWireVO)
    {
        Connection conn         = null;
        PreparedStatement pstmt = null;
        try
        {
            conn            = Database.getConnection();
            String query1   = "update partner_wiremanager set settledate=? ,partnerchargeamount=? ,partnerunpaidamount=? ,partnertotalfundedamount=?,status=? where settledid=? ";
            pstmt           = conn.prepareStatement(query1);
            pstmt.setString(1, partnerWireVO.getSettleDate());
            pstmt.setDouble(2, partnerWireVO.getPartnerChargeAmount());
            pstmt.setDouble(3, partnerWireVO.getPartnerUnpaidAmount());
            pstmt.setDouble(4, partnerWireVO.getPartnerTotalFundedAmount());
            pstmt.setString(5, partnerWireVO.getStatus());
            pstmt.setString(6, partnerWireVO.getSettledId());
            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                partnerWireVO.setUpdated(true);
            }
            else
            {
                partnerWireVO.setUpdated(false);
            }
        }
        catch (SystemError systemError)
        {
            log.error("SystemError::::" + systemError.getMessage());
        }
        catch (SQLException se)
        {
            log.error("SQLException:::::" + se.getMessage());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
    }

    public PartnerDetailsVO getselfPartnerDetails(String partnerid) throws PZDBViolationException
    {
        Connection conn         = null;
        PreparedStatement pstmt = null;
        ResultSet res           = null;
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        try
        {
            conn            = Database.getRDBConnection();
            log.debug("Entering getMember Details");
            String query    = "select * from partners where partnerid=? ";
            pstmt           = conn.prepareStatement(query);
            pstmt.setString(1, partnerid);
            res             = pstmt.executeQuery();
            log.debug("Old partner Details is loading ");
            if (res.next())
            {
                partnerDetailsVO.setPartnerId(res.getString("partnerId"));
                partnerDetailsVO.setClkey(res.getString("clkey"));
                partnerDetailsVO.setContactPerson(res.getString("contact_persons"));
                partnerDetailsVO.setActivation(res.getString("activation"));
                partnerDetailsVO.setTemplate(res.getString("template"));
                partnerDetailsVO.setPartnerName(res.getString("partnerName"));
                partnerDetailsVO.setContact_emails(res.getString("contact_emails"));
                partnerDetailsVO.setAddress(res.getString("address"));
                partnerDetailsVO.setCountry(res.getString("country"));
                partnerDetailsVO.setTelno(res.getString("telno"));
                partnerDetailsVO.setPharma(res.getString("isPharma"));
                partnerDetailsVO.setValidateEmail(res.getString("isValidateEmail"));
                partnerDetailsVO.setDaily_amount_limit(res.getString("daily_amount_limit"));
                partnerDetailsVO.setMonthly_amount_limit(res.getString("monthly_amount_limit"));
                partnerDetailsVO.setDaily_card_limit(res.getString("daily_card_limit"));
                partnerDetailsVO.setWeekly_card_limit(res.getString("weekly_card_limit"));
                partnerDetailsVO.setMonthly_card_limit(res.getString("monthly_card_limit"));
                partnerDetailsVO.setCheck_limit(res.getString("check_limit"));
                partnerDetailsVO.setFraudemailid(res.getString("fraudemailid"));
                partnerDetailsVO.setIpWhitelisted(res.getString("isIpWhitelisted"));
                partnerDetailsVO.setModify_merchant_details(res.getString("modify_merchant_details"));
                partnerDetailsVO.setModify_company_details(res.getString("modify_company_details"));
                partnerDetailsVO.setActivation(res.getString("activation"));
                partnerDetailsVO.setResponseType(res.getString("responseType"));
                partnerDetailsVO.setResponseLength(res.getString("responseLength"));
                partnerDetailsVO.setSupportMailId(res.getString("companysupportmailid"));
                partnerDetailsVO.setAdminMailId(res.getString("companyadminid"));
                partnerDetailsVO.setSalesMailId(res.getString("salesemail"));
                partnerDetailsVO.setBillingMailId(res.getString("billingemail"));
                partnerDetailsVO.setNotifyEmailId(res.getString("notifyemail"));
                partnerDetailsVO.setLogoName(res.getString("logoName"));
                partnerDetailsVO.setSalesContactName(res.getString("salescontactname"));
                partnerDetailsVO.setBillingContactName(res.getString("billingcontactname"));
                partnerDetailsVO.setNotifyContactName(res.getString("notifycontactname"));
                partnerDetailsVO.setFraudContactName(res.getString("fraudcontactname"));
                partnerDetailsVO.setChargebackContactName(res.getString("chargebackcontactname"));
                partnerDetailsVO.setRefundContactName(res.getString("refundcontactname"));
                partnerDetailsVO.setTechContactName(res.getString("technicalcontactname"));
                partnerDetailsVO.setChargebackMailId(res.getString("chargebackemailid"));
                partnerDetailsVO.setRefundMailId(res.getString("refundemailid"));
                partnerDetailsVO.setTechMailId(res.getString("technicalemailid"));
                partnerDetailsVO.setDefaultTheme(res.getString("default_theme"));
                partnerDetailsVO.setCurrentTheme(res.getString("current_theme"));
            }
            log.debug("merchant Details loaded");
        }
        catch (SystemError systemError)
        {
            log.error("Leaving Merchants throwing System Exception as System Error :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getselfPartnerDetails()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("Leaving Merchants throwing SQL Exception as System Error :::: ", e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getselfPartnerDetails()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        log.debug("Leaving getselfPartnerDetails");
        return partnerDetailsVO;
    }

    public boolean isMemberMappedWithPartner(String partnerId, String memberId) throws SQLException, SystemError
    {
        Connection con          = null;
        PreparedStatement pstmt = null;
        ResultSet rs            = null;
        boolean b               = false;
        try
        {
            con             = Database.getRDBConnection();
            StringBuffer sb = new StringBuffer("select memberid from members where partnerId=? and memberid=?");
            pstmt           = con.prepareStatement(sb.toString());
            pstmt.setString(1, partnerId);
            pstmt.setString(2, memberId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                b = true;
            }
        }
        finally
        {   Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return b;
    }

    public Map<String, Object> getSavedMemberTemplateDetails(String memberId) throws PZDBViolationException
    {
        Connection con          = null;
        ResultSet resultSet     = null;
        PreparedStatement pstmt = null;
        Map<String, Object> merchantDetailsVOMap = new HashMap<String, Object>();
        log.debug("Inside saved Template Details:::");
        try
        {
            con             = Database.getRDBConnection();
            String query    = "select name,value from template_preferences where memberid= ? ";
            pstmt           = con.prepareStatement(query);
            pstmt.setString(1, memberId);
            resultSet = pstmt.executeQuery();
            while (resultSet.next())
            {
                if (TemplatePreference.getEnum(resultSet.getString("name")) != null && !TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.name().equals(TemplatePreference.getEnum(resultSet.getString("name")).name()))
                {
                    log.debug("inside First Condition:::" + resultSet.getString("name") + " enum" + TemplatePreference.getEnum(resultSet.getString("name")).toString());
                    merchantDetailsVOMap.put(TemplatePreference.getEnum(resultSet.getString("name")).toString(), resultSet.getString("value"));
                }
                else if (TemplatePreference.getEnum(resultSet.getString("name")) != null && TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.name().equals(TemplatePreference.getEnum(resultSet.getString("name")).name()))
                {
                    log.debug("inside 2nd Condition:::" + resultSet.getString("name") + " enum" + TemplatePreference.getEnum(resultSet.getString("name")).toString());
                    merchantDetailsVOMap.put(TemplatePreference.getEnum(resultSet.getString("name")).toString(), resultSet.getBytes("value"));
                }
            }
        }
        catch (SQLException e)
        {
            log.error("Leaving Merchants throwing SQL Exception as System Error : ", e);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "getMemberTemplateDetails()", null, "Common", "Exception while getting template information", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            log.error("Leaving Merchants throwing SQL Exception as System Error : ", systemError);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "getMemberTemplateDetails()", null, "Common", "Exception while getting template information", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {   Database.closeResultSet(resultSet);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return merchantDetailsVOMap;
    }

    public Map<String, Object> getPartnerSavedMemberTemplateDetails(String partnerId) throws PZDBViolationException
    {
        Connection con          = null;
        ResultSet resultSet     = null;
        PreparedStatement pstmt = null;

        Map<String, Object> partnerDetailsVOMap = new HashMap<String, Object>();
        log.debug("Inside saved Template Detailssssss:::");
        try
        {
            con             = Database.getRDBConnection();
            String query    = "select name,value from partner_template_preference where partnerid= ? ";
            pstmt           = con.prepareStatement(query);
            pstmt.setString(1, partnerId);
            resultSet = pstmt.executeQuery();
            while (resultSet.next())
            {
                log.debug(resultSet.getString("name")+"---"+resultSet.getString("name"));
                if (PartnerTemplatePreference.getEnum(resultSet.getString("name")) != null && !PartnerTemplatePreference.ATRANSACTIONPAGEMERCHANTLOGO.name().equals(PartnerTemplatePreference.getEnum(resultSet.getString("name")).name()))
                {
                    log.debug("inside First Condition:::" + resultSet.getString("name") + " enum" + PartnerTemplatePreference.getEnum(resultSet.getString("name")).toString());
                    partnerDetailsVOMap.put(PartnerTemplatePreference.getEnum(resultSet.getString("name")).toString(), resultSet.getString("value"));
                }
                else if (PartnerTemplatePreference.getEnum(resultSet.getString("name")) != null && PartnerTemplatePreference.ATRANSACTIONPAGEMERCHANTLOGO.name().equals(PartnerTemplatePreference.getEnum(resultSet.getString("name")).name()))
                {
                    log.debug("inside 2nd Condition:::" + resultSet.getString("name") + " enum" + PartnerTemplatePreference.getEnum(resultSet.getString("name")).toString());
                    partnerDetailsVOMap.put(PartnerTemplatePreference.getEnum(resultSet.getString("name")).toString(), resultSet.getBytes("value"));
                }
            }
        }
        catch (SQLException e)
        {
            log.error("Leaving Merchants throwing SQL Exception as System Error : ", e);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "getPartnerMemberTemplateDetails()", null, "Common", "Exception while getting template information", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            log.error("Leaving Merchants throwing SQL Exception as System Error : ", systemError);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "getPartnerMemberTemplateDetails()", null, "Common", "Exception while getting template information", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return partnerDetailsVOMap;
    }

    /*public Map<String, Object> getPartnerSavedTemplateDetails(String partnerId) throws PZDBViolationException
    {
        Connection con = null;
        ResultSet resultSet = null;
        Map<String, Object> partnerDetailsVOMap = new HashMap<String, Object>();
        log.debug("Inside saved Template Detailssssss:::");
        try
        {
            con = Database.getConnection();
            String query = "select name,value from partner_template_preference where partnerid= ? ";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, partnerId);
            resultSet = pstmt.executeQuery();
            System.out.println("template---"+pstmt);
            while (resultSet.next())
            {
                partnerDetailsVOMap.put(resultSet.getString("name").toString(), resultSet.getString("value"));
            }
        }
        catch (SQLException e)
        {
            log.error("Leaving Merchants throwing SQL Exception as System Error : ", e);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "getPartnerMemberTemplateDetails()", null, "Common", "Exception while getting template information", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            log.error("Leaving Merchants throwing SQL Exception as System Error : ", systemError);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "getPartnerMemberTemplateDetails()", null, "Common", "Exception while getting template information", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return partnerDetailsVOMap;
    }
*/
    public boolean insertPartnerTemplatePreferences(Map<String, Object> partnerTemplateInformation, String partnerId) throws PZDBViolationException
    {
        Connection conn         = null;
        PreparedStatement pstmt = null;
        String query            = null;
        try
        {
            conn    = Database.getConnection();
            query   = "insert into partner_template_preference(partnerid,name,value) values (?,?,?)";
            pstmt   = conn.prepareStatement(query);
            if (partnerTemplateInformation != null && partnerTemplateInformation.size() > 0)
            {
                for (Map.Entry<String, Object> partnerTemplateNameKeyPair : partnerTemplateInformation.entrySet())
                {
                    if (partnerTemplateNameKeyPair.getValue() != null && partnerTemplateNameKeyPair.getKey() != null)
                    {
                        pstmt.setString(1, partnerId);
                        pstmt.setString(2, partnerTemplateNameKeyPair.getKey());
                        if (PartnerTemplatePreference.ATRANSACTIONPAGEMERCHANTLOGO.name().equals(partnerTemplateNameKeyPair.getKey()))
                        {
                            pstmt.setBytes(3, (byte[]) partnerTemplateNameKeyPair.getValue());
                        }
                        else
                        {
                            pstmt.setString(3, partnerTemplateNameKeyPair.getValue().toString());
                        }
                        pstmt.addBatch();
                    }
                }
                int[] k = pstmt.executeBatch();
                if (k.length > 0)
                {
                    return true;
                }
            }
        }
        catch (SystemError systemError)
        {
            log.error("Leaving insertPartnerTemplatePreferences throwing SQL Exception as System Error :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "insertPartnerTemplatePreferences()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("Leaving insertPartnerTemplatePreferences throwing SQL Exception as System Error :::: ", e);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "insertPartnerTemplatePreferences()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

        return false;
    }

    public boolean deletePartnerTemplatePreferences(Map<String, Object> partnerTemplateInformation, String partnerId) throws PZDBViolationException
    {
        Connection conn         = null;
        PreparedStatement pstmt = null;
        String query            = null;
        try
        {
            conn    = Database.getConnection();
            query   = "DELETE FROM partner_template_preference WHERE name=? AND partnerid=?";
            pstmt   = conn.prepareStatement(query);
            if (partnerTemplateInformation != null && partnerTemplateInformation.size() >= 0)
            {
                for (Map.Entry<String, Object> merchantTemplateNameKeyPair : partnerTemplateInformation.entrySet())
                {
                    if (merchantTemplateNameKeyPair.getValue() != null && merchantTemplateNameKeyPair.getKey() != null)
                    {
                        pstmt.setString(1, merchantTemplateNameKeyPair.getKey());
                        pstmt.setString(2, partnerId);
                        pstmt.addBatch();
                    }
                }
                int[] k = pstmt.executeBatch();
                if (k.length > 0)
                {
                    return true;
                }
            }
        }
        catch (SystemError systemError)
        {
            log.error("Leaving insertPartnerTemplatePreferences throwing SQL Exception as System Error :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "insertPartnerTemplatePreferences()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("Leaving insertPartnerTemplatePreferences throwing SQL Exception as System Error :::: ", e);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "insertPartnerTemplatePreferences()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return false;
    }

    public boolean updatePartnerTemplateDetails(Map<String, Object> partnerTemplateInformation, String partnerId) throws PZDBViolationException
    {
        Connection conn         = null;
        PreparedStatement pstmt = null;
        String query            = null;
        try
        {
            conn = Database.getConnection();
            query = "update partner_template_preference set value=? where name=? and partnerid=?";
            pstmt = conn.prepareStatement(query);

            if (partnerTemplateInformation != null && partnerTemplateInformation.size() >= 0)
            {
                for (Map.Entry<String, Object> merchantTemplateNameKeyPair : partnerTemplateInformation.entrySet())
                {
                    if (merchantTemplateNameKeyPair.getValue() != null && merchantTemplateNameKeyPair.getKey() != null)
                    {
                        if (PartnerTemplatePreference.ATRANSACTIONPAGEMERCHANTLOGO.name().equals(merchantTemplateNameKeyPair.getKey()))
                        {
                            pstmt.setBytes(1, (byte[]) merchantTemplateNameKeyPair.getValue());
                        }
                        else
                        {
                            pstmt.setString(1, merchantTemplateNameKeyPair.getValue().toString());
                        }
                        pstmt.setString(2, merchantTemplateNameKeyPair.getKey());
                        pstmt.setString(3, partnerId);
                        pstmt.addBatch();
                    }
                }
                int[] k = pstmt.executeBatch();
                if (k.length > 0)
                {
                    return true;
                }
            }
        }
        catch (SystemError systemError)
        {
            log.error("Leaving insertPartnerTemplatePreferences throwing SQL Exception as System Error :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "insertPartnerTemplatePreferences()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("Leaving insertPartnerTemplatePreferences throwing SQL Exception as System Error :::: ", e);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "insertPartnerTemplatePreferences()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return false;
    }

    public boolean insertTemplatePreferences(Map<String, Object> merchantTemplateInformation, String memberId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        try
        {
            conn = Database.getConnection();
            query = "insert into template_preferences(memberid,name,value) values (?,?,?)";
            pstmt = conn.prepareStatement(query);
            if (merchantTemplateInformation != null && merchantTemplateInformation.size() > 0)
            {
                for (Map.Entry<String, Object> merchantTemplateNameKeyPair : merchantTemplateInformation.entrySet())
                {
                    if (merchantTemplateNameKeyPair.getValue() != null && merchantTemplateNameKeyPair.getKey() != null)
                    {
                        pstmt.setString(1, memberId);
                        pstmt.setString(2, merchantTemplateNameKeyPair.getKey());
                        if (TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.name().equals(merchantTemplateNameKeyPair.getKey()))
                        {
                            pstmt.setBytes(3, (byte[]) merchantTemplateNameKeyPair.getValue());
                        }
                        else
                        {
                            pstmt.setString(3, merchantTemplateNameKeyPair.getValue().toString());
                        }
                        pstmt.addBatch();
                    }
                }
                int[] k = pstmt.executeBatch();
                if (k.length > 0)
                {
                    return true;
                }
            }
        }
        catch (SystemError systemError)
        {
            log.error("Leaving insertMemberTemplate throwing SQL Exception as System Error :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "insertTemplatePreferences()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("Leaving insertMemberTemplate throwing SQL Exception as System Error :::: ", e);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "insertTemplatePreferences()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return false;
    }

    public boolean updateTemplatePreferences(Map<String, Object> merchantTemplateInformation, String memberId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        try
        {
            conn = Database.getConnection();
            query = "update template_preferences set value=? where name=? and memberid=?";
            pstmt = conn.prepareStatement(query);
            if (merchantTemplateInformation != null && merchantTemplateInformation.size() >= 0)
            {
                for (Map.Entry<String, Object> merchantTemplateNameKeyPair : merchantTemplateInformation.entrySet())
                {
                    if (merchantTemplateNameKeyPair.getValue() != null && merchantTemplateNameKeyPair.getKey() != null)
                    {
                        if (TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.name().equals(merchantTemplateNameKeyPair.getKey()))
                        {
                            pstmt.setBytes(1, (byte[]) merchantTemplateNameKeyPair.getValue());
                        }
                        else
                        {
                            pstmt.setString(1, merchantTemplateNameKeyPair.getValue().toString());
                        }
                        pstmt.setString(2, merchantTemplateNameKeyPair.getKey());
                        pstmt.setString(3, memberId);
                        pstmt.addBatch();
                    }
                }
                int[] k = pstmt.executeBatch();
                if (k.length > 0)
                {
                    return true;
                }
            }
        }
        catch (SystemError systemError)
        {
            log.error("Leaving insertMemberTemplate throwing SQL Exception as System Error :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "insertTemplatePreferences()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("Leaving insertMemberTemplate throwing SQL Exception as System Error :::: ", e);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "insertTemplatePreferences()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return false;
    }

    public boolean deleteMemberTemplateDetails(Map<String, Object> merchantTemplateInformation, String memberId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        try
        {
            conn = Database.getConnection();
            query = "DELETE FROM template_preferences WHERE name=? AND memberid=?";
            pstmt = conn.prepareStatement(query);
            if (merchantTemplateInformation != null && merchantTemplateInformation.size() >= 0)
            {
                for (Map.Entry<String, Object> merchantTemplateNameKeyPair : merchantTemplateInformation.entrySet())
                {
                    if (merchantTemplateNameKeyPair.getValue() != null && merchantTemplateNameKeyPair.getKey() != null)
                    {
                        pstmt.setString(1, merchantTemplateNameKeyPair.getKey());
                        pstmt.setString(2, memberId);
                        pstmt.addBatch();
                    }
                }
                int[] k = pstmt.executeBatch();
                if (k.length > 0)
                {
                    return true;
                }
            }
        }
        catch (SystemError systemError)
        {
            log.error("Leaving insertMemberTemplate throwing SQL Exception as System Error :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "insertTemplatePreferences()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("Leaving insertMemberTemplate throwing SQL Exception as System Error :::: ", e);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "insertTemplatePreferences()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return false;
    }

    public PartnerDetailsVO getPartnerDetailsForIFE(String partnerID) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt =null;
        ResultSet rs = null;
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT clkey,splitpayment,flightMode,isCardEncryptionEnable FROM partners WHERE partnerId=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, partnerID);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                partnerDetailsVO.setPartnerKey(rs.getString("clkey"));
                partnerDetailsVO.setSplitPaymentAllowed(rs.getString("splitpayment"));
                partnerDetailsVO.setFlightMode(rs.getString("flightMode"));
                partnerDetailsVO.setIsCardEncryptionEnable(rs.getString("isCardEncryptionEnable"));
            }
            transactionLogger.debug("query to get partner key----" + pstmt);
        }
        catch (SystemError systemError)
        {
            log.error("System Error :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMemberDetails()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("System Error :::: ", e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMemberDetails()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return partnerDetailsVO;
    }

    public TreeMap<String, String> getpartnerDetails()
    {
        TreeMap<String, String> partners = new TreeMap<String, String>();
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "SELECT partnerId,partnerName FROM partners WHERE activation='T' ORDER BY partnerId ASC";
            pstmt = con.prepareStatement(qry);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                partners.put(rs.getString("partnerId"), rs.getString("partnerId") + "-" + rs.getString("partnerName"));
            }
        }
        catch (Exception e)
        {
            log.error("error", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return partners;
    }

    public LinkedHashMap<String, String> getpartnerDetail()
    {
        LinkedHashMap<String, String> partners = new LinkedHashMap<String, String>();
        Connection con = null;
        PreparedStatement pstmt=null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "SELECT partnerId,partnerName FROM partners WHERE activation='T' ORDER BY partnerId ASC";
            pstmt = con.prepareStatement(qry);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                partners.put(rs.getString("partnerId"), rs.getString("partnerId") + "-" + rs.getString("partnerName"));
            }
        }
        catch (Exception e)
        {
            log.error("error", e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return partners;
    }

    public TreeMap<Integer, String> loadGatewayAccounts(String partnerid, String accountid)
    {
        TreeMap<Integer, String> gatewayaccounts = new TreeMap<Integer, String>();
        Connection conn = null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            rs = Database.executeQuery("select g.accountid,g.merchantid,g.pgtypeid,t.currency,t.name from gateway_accounts as g JOIN gateway_type as t ON t.pgtypeid=g.pgtypeid JOIN gateway_account_partner_mapping as gapm ON g.accountid=gapm.accountid and gapm.isActive='Y' and gapm.partnerid=" + partnerid + " order by g.accountid", conn);
            while (rs.next())
            {
                gatewayaccounts.put(rs.getInt("accountid"), rs.getString("merchantid") + "-" + rs.getString("name") + "-" + rs.getString("currency"));
            }
        }
        catch (Exception e)
        {
            log.error("Exception while loading partner managed bank account", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return gatewayaccounts;

    }

    //to filter account id on merchant id basic
    public TreeMap<Integer, String> loadMerchantsGatewayAccounts(String partnerid, String memberid)
    {
        TreeMap<Integer, String> gatewayaccounts = new TreeMap<Integer, String>();
        Connection conn = null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            rs = Database.executeQuery("select g.accountid,g.merchantid,g.pgtypeid,t.currency,t.name from gateway_accounts as g JOIN gateway_type as t ON t.pgtypeid=g.pgtypeid JOIN gateway_account_partner_mapping as gapm ON g.accountid=gapm.accountid and gapm.isActive='Y' and gapm.partnerid=" + partnerid + " and g.accountid IN (SELECT accountid FROM member_account_mapping WHERE memberid="+memberid+") order by g.accountid asc ", conn);
            while (rs.next())
            {
                gatewayaccounts.put(rs.getInt("accountid"), rs.getString("merchantid") + "-" + rs.getString("name") + "-" + rs.getString("currency"));
            }
        }
        catch (Exception e)
        {
            log.error("Exception while loading partner managed bank account", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return gatewayaccounts;

    }
    public TreeMap<Integer, String> loadGatewayAccountForPartner(String partnerId)
    {
        TreeMap<Integer, String> gatewayaccounts = new TreeMap<Integer, String>();
        Connection conn = null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            rs = Database.executeQuery("select g.accountid,g.merchantid,g.pgtypeid,t.currency,t.name ,g.aliasname from gateway_accounts as g JOIN gateway_type as t ON t.pgtypeid=g.pgtypeid JOIN gateway_account_partner_mapping as gapm ON g.accountid=gapm.accountid and gapm.isActive='Y' and gapm.partnerid=" + partnerId + " order by g.accountid asc ", conn);
            while (rs.next())
            {
                gatewayaccounts.put(rs.getInt("accountid"), rs.getString("currency") + "-" + rs.getString("merchantid") + "-" + rs.getString("name")+ "-" + rs.getString("aliasname"));
            }
        }
        catch (Exception e)
        {
            log.error("Exception while loading partner managed bank account", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return gatewayaccounts;
    }
    public TreeMap<Integer, String> loadGatewayAccount(String partnerId)
    {
        TreeMap<Integer, String> gatewayaccounts = new TreeMap<Integer, String>();
        Connection conn = null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            rs = Database.executeQuery("select g.accountid,g.merchantid,g.pgtypeid,t.currency,t.name from gateway_accounts as g JOIN gateway_type as t ON t.pgtypeid=g.pgtypeid JOIN gateway_account_partner_mapping as gapm ON g.accountid=gapm.accountid and gapm.isActive='Y' and gapm.partnerid=" + partnerId + " order by g.accountid asc ", conn);
            while (rs.next())
            {
                gatewayaccounts.put(rs.getInt("accountid"), rs.getString("merchantid") + "-" + rs.getString("name") + "-" + rs.getString("currency"));
            }
        }
        catch (Exception e)
        {
            log.error("Exception while loading partner managed bank account", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return gatewayaccounts;
    }

    public boolean isTerminalUnique(String memberId, String accountId, String payModeId, String cardTypeId) throws Exception
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean status = true;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer sb = new StringBuffer("select terminalid from member_account_mapping where memberid=? and accountid=? and paymodeid=? and cardtypeid=?");
            pstmt = conn.prepareStatement(sb.toString());
            pstmt.setString(1, memberId);
            pstmt.setString(2, accountId);
            pstmt.setString(3, payModeId);
            pstmt.setString(4, cardTypeId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                status = false;
            }
        }
        finally
        {   Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public boolean isMasterCardSupported(String accountId) throws Exception
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean flag = false;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer sb = new StringBuffer("select ismastercardsupported from gateway_accounts where accountid=?");
            pstmt = conn.prepareStatement(sb.toString());
            pstmt.setString(1, accountId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                if ("1".equals(rs.getString("ismastercardsupported")))
                {
                    flag = true;
                }
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return flag;
    }

    public boolean isChackForAccountId(String partnerid,String accountIdAfterFillter) throws Exception
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean flag = false;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer sb = new StringBuffer("SELECT g.accountid,g.merchantid,g.pgtypeid,t.currency,t.name FROM gateway_accounts AS g JOIN gateway_type AS t ON t.pgtypeid=g.pgtypeid JOIN gateway_account_partner_mapping AS gapm ON g.accountid=gapm.accountid AND gapm.isActive='Y' AND gapm.partnerid= ? AND g.accountid = ? ");
            pstmt = conn.prepareStatement(sb.toString());
            pstmt.setString(1, partnerid);
            pstmt.setString(2, accountIdAfterFillter);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                    flag = true;
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return flag;
    }

    public List<PartnerDetailsVO> getAllWhitelabelPartners()
    {
        PartnerDetailsVO partnerDetailsVO = null;
        List<PartnerDetailsVO> partnerList = new ArrayList();
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT partnerId,partnerName FROM partners WHERE activation='T' ORDER BY partnerId ASC";
            pstmt = conn.prepareStatement(query);
            log.debug("partner list---" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                partnerDetailsVO = new PartnerDetailsVO();
                partnerDetailsVO.setCompanyName(rs.getString("partnerName"));
                partnerDetailsVO.setPartnerId(rs.getString("partnerId"));
                partnerList.add(partnerDetailsVO);
            }
        }
        catch (Exception e)
        {
            log.error("Exception::::::", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return partnerList;
    }

    public List<TokenVo> getpartnerTokenDetail()
    {
        TokenVo tokenVo = null;
        List<TokenVo> tokenVoList = new ArrayList<TokenVo>();
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "SELECT p.partnerId,p.partnerName,m.memberid,m.company_name FROM partners AS p JOIN members AS m ON p.partnerId = m.partnerid  WHERE  p.isTokenizationAllowed='Y' AND p.is_rest_whitelisted ='Y' AND m.isTokenizationAllowed='Y' AND m.is_rest_whitelisted ='Y' ORDER BY partnerId ASC";
            pstmt = con.prepareStatement(qry);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                tokenVo = new TokenVo();
                tokenVo.setPartnerid(rs.getString("partnerId"));
                tokenVo.setPartnername(rs.getString("partnerName"));
                tokenVo.setMemberid(rs.getString("memberid"));
                tokenVo.setCompanyname(rs.getString("company_name"));
                tokenVoList.add(tokenVo);
            }
        }
        catch (Exception e)
        {
            log.error("error", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return tokenVoList;
    }

    public List<TokenVo> getAllPartnerid()
    {
        TokenVo tokenVo = null;
        List<TokenVo> tokenVoList = new ArrayList<TokenVo>();
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "SELECT partnerId,partnerName FROM partners WHERE activation='T' ORDER BY partnerId ASC";
            pstmt = con.prepareStatement(qry);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                tokenVo = new TokenVo();
                tokenVo.setPartnerid(rs.getString("partnerId"));
                tokenVo.setPartnername(rs.getString("partnerName"));
                tokenVoList.add(tokenVo);
            }
        }
        catch (Exception e)
        {
            log.error("error", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return tokenVoList;
    }

    public String getPartnerName(String partnerid)
    {
        String partnername="";
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "SELECT partnerName FROM partners WHERE partnerid=?";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, partnerid);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                partnername = partnername+rs.getString("partnerName");
            }
        }
        catch (Exception e)
        {
            log.error("error", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return partnername;
    }

    public List<TokenVo> getpartnerTokenDetailByMemberid(String partnerid)
    {
        TokenVo tokenVo = null;
        List<TokenVo> tokenVoList = new ArrayList<TokenVo>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        try
        {
            con = Database.getRDBConnection();

            String qry = "";
            if (partnerid.equals(null) || partnerid.equals(""))
            {
                qry = "SELECT p.partnerId,p.partnerName,m.memberid,m.company_name FROM partners AS p JOIN members AS m ON p.partnerId = m.partnerid  WHERE  p.isTokenizationAllowed='Y' AND p.is_rest_whitelisted ='Y' AND m.isTokenizationAllowed='Y' AND m.is_rest_whitelisted ='Y' ORDER BY memberid ASC";
                pstmt = con.prepareStatement(qry);
            }
            else
            {
                qry = "SELECT p.partnerId,p.partnerName,m.memberid,m.company_name FROM partners AS p JOIN members AS m ON p.partnerId = m.partnerid  WHERE  p.isTokenizationAllowed='Y' AND p.is_rest_whitelisted ='Y' AND m.isTokenizationAllowed='Y' AND m.is_rest_whitelisted ='Y' AND p.partnerId = ? ORDER BY memberid ASC";
                pstmt = con.prepareStatement(qry);
                pstmt.setInt(1, Integer.parseInt(partnerid));
            }
            log.debug("query:::::"+pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                tokenVo = new TokenVo();
                tokenVo.setPartnerid(rs.getString("partnerId"));
                tokenVo.setPartnername(rs.getString("partnerName"));
                tokenVo.setMemberid(rs.getString("memberid"));
                tokenVo.setCompanyname(rs.getString("company_name"));
                tokenVoList.add(tokenVo);
            }
        }
        catch (Exception e)
        {
            log.error("error", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return tokenVoList;
    }
    public List<TokenVo> getpartnerTokenDetailByMemberidPartner(String partnerid)
    {
        TokenVo tokenVo = null;
        List<TokenVo> tokenVoList = new ArrayList<TokenVo>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        try
        {
            con = Database.getRDBConnection();

            String qry = "";
            if (partnerid.equals(null) || partnerid.equals(""))
            {
                qry = "SELECT m.memberid,p.partnerName,p.partnerId FROM partners AS p JOIN members AS m  ON p.partnerId = m.partnerid ORDER BY m.memberid ASC";
                pstmt = con.prepareStatement(qry);
            }
            else
            {
                qry = "SELECT m.memberid,p.partnerName,p.partnerId FROM partners AS p JOIN members AS m  ON p.partnerId = m.partnerid WHERE p.partnerId =? ORDER BY m.memberid ASC";
                pstmt = con.prepareStatement(qry);
                pstmt.setInt(1, Integer.parseInt(partnerid));
            }
            log.debug("query:::::"+pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                tokenVo = new TokenVo();
                tokenVo.setPartnerid(rs.getString("partnerId"));
                tokenVo.setPartnername(rs.getString("partnerName"));
                tokenVo.setMemberid(rs.getString("memberid"));
                tokenVoList.add(tokenVo);
            }
        }
        catch (Exception e)
        {
            log.error("error", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return tokenVoList;
    }

    public List<TokenVo> getpartnerTokenDetails()
    {
        TokenVo tokenVo = null;
        List<TokenVo> tokenVoList = new ArrayList<TokenVo>();
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "SELECT p.partnerId,p.partnerName,m.memberid,m.company_name FROM partners AS p JOIN members AS m ON p.partnerId = m.partnerid WHERE  p.isTokenizationAllowed='Y' AND m.isTokenizationAllowed='Y' ORDER BY partnerId ASC";
            pstmt = con.prepareStatement(qry);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                tokenVo = new TokenVo();
                tokenVo.setPartnerid(rs.getString("partnerId"));
                tokenVo.setPartnername(rs.getString("partnerName"));
                tokenVo.setMemberid(rs.getString("memberid"));
                tokenVo.setCompanyname(rs.getString("company_name"));
                tokenVoList.add(tokenVo);
            }
        }
        catch (Exception e)
        {
            log.error("error", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return tokenVoList;
    }

    public boolean isUniqueTheme(DefaultThemeVO defaultThemeVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        boolean status = true;
        String query = null;
        try
        {
            conn = Database.getRDBConnection();
            query = "select theme_id from default_theme where default_themeName=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, defaultThemeVO.getDefaultThemeName());
            resultSet = pstmt.executeQuery();
            if (resultSet.next())
            {
                status = false;
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PartnerDAO.java", "isUniqueTheme()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PartnerDAO.java", "isUniqueTheme()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
            log.debug("Connection Closed");
        }
        return status;
    }

    public boolean isUniqueCurrentTheme(CurrentThemeVO currentThemeVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        boolean status = true;
        String query = null;
        try
        {
            conn = Database.getRDBConnection();
            query = "select theme_id from theme where current_theme=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, currentThemeVO.getCurrentThemeName());
            resultSet = pstmt.executeQuery();
            if (resultSet.next())
            {
                status = false;
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PartnerDAO.java", "isUniqueTheme()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PartnerDAO.java", "isUniqueTheme()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
            log.debug("Connection Closed");
        }
        return status;
    }

    public String addNewDefaultTheme(DefaultThemeVO defaultThemeVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String status = "";
        String query = null;
        try
        {
            conn = Database.getConnection();
            query = "insert into default_theme(theme_id,default_themeName,themeCreationtime)values(null,?,unix_timestamp(now()))";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, defaultThemeVO.getDefaultThemeName());
            int k = pstmt.executeUpdate();
            if (k > 0)
            {
                status = "success";
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PartnerDAO.java", "addNewDefaultTheme()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PartnerDAO.java", "addNewDefaultTheme()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {

            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public String addNewCurrentTheme(CurrentThemeVO currentThemeVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String status = "";
        String query = null;
        try
        {
            conn = Database.getConnection();
            query = "insert into theme(theme_id,current_theme,theme_type)values(null,?,?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, currentThemeVO.getCurrentThemeName());
            pstmt.setString(2, currentThemeVO.getThemeType());
            int k = pstmt.executeUpdate();
            if (k > 0)
            {
                status = "success";
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PartnerDAO.java", "addNewDefaultTheme()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PartnerDAO.java", "addNewDefaultTheme()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public LinkedHashMap<String, Integer> listCurrenttheme()
    {
        LinkedHashMap<String, Integer> themeMap = new LinkedHashMap<String, Integer>();
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT current_theme,theme_id FROM theme WHERE theme_type='current' ORDER BY theme_id DESC";
            pstmt = conn.prepareStatement(query);
            res = pstmt.executeQuery();
            while (res.next())
            {
                themeMap.put(res.getString("current_theme"), res.getInt("theme_id"));
            }
        }
        catch (SystemError systemError)
        {
            log.error("Leaving MerchantDAO throwing System Exception as SystemError :::: ", systemError);
        }
        catch (SQLException e)
        {
            log.error("Leaving MerchantDAO throwing SQL Exception as SystemError :::: ", e);
        }
        catch (Exception e)
        {
            log.error("Leaving MerchantDAO throwing SQL Exception as Exception :::: ", e);
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return themeMap;
    }

    public String insertBinUpload(String bin,String accountid,String memberid) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String status = "";
        String query = null;
        try
        {
            conn = Database.getConnection();
            query = "insert into whitelist_bins(bin,accountid,memberid) values(?,?,?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, bin);
            pstmt.setString(2, accountid);
            pstmt.setString(3, memberid);
            int k = pstmt.executeUpdate();
            if (k > 0)
            {
                status = "success";
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PartnerDAO.java", "addNewDefaultTheme()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PartnerDAO.java", "addNewDefaultTheme()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public LinkedHashMap<String, Integer> listDefaulttheme()
    {
        LinkedHashMap<String, Integer> themeMap = new LinkedHashMap<String, Integer>();
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT current_theme,theme_id FROM theme WHERE theme_type='default' ORDER BY theme_id DESC";
            pstmt = conn.prepareStatement(query);
            res = pstmt.executeQuery();
            while (res.next())
            {
                themeMap.put(res.getString("current_theme"), res.getInt("theme_id"));
            }
        }
        catch (SystemError systemError)
        {
            log.error("Leaving MerchantDAO throwing System Exception as SystemError :::: ", systemError);
        }
        catch (SQLException e)
        {
            log.error("Leaving MerchantDAO throwing SQL Exception as SystemError :::: ", e);
        }
        catch (Exception e)
        {
            log.error("Leaving MerchantDAO throwing SQL Exception as Exception :::: ", e);
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return themeMap;
    }

    public PartnerDefaultConfigVO getPartnerDefaultConfig(String partnerId) throws PZDBViolationException
    {
        Connection conn                                 = null;
        ResultSet rs                                    = null;
        PreparedStatement preparedStatement             = null;
        PartnerDefaultConfigVO partnerDefaultConfigVO   = null;
        try
        {
            conn                = Database.getRDBConnection();
            String qry          = "SELECT * from partner_default_configuration where partnerid=?";
            preparedStatement   = conn.prepareStatement(qry);
            preparedStatement.setString(1, partnerId);
            rs                  = preparedStatement.executeQuery();
            if (rs.next())
            {
                partnerDefaultConfigVO  = new PartnerDefaultConfigVO();

                partnerDefaultConfigVO.setActivation(rs.getString("activation"));
                partnerDefaultConfigVO.setMerchantInterfaceAccess(rs.getString("merchant_interface_access"));
                partnerDefaultConfigVO.setIsExcessCaptureAllowed(rs.getString("is_excesscapture_allowed"));
                partnerDefaultConfigVO.setFlightMode(rs.getString("flight_mode"));
                partnerDefaultConfigVO.setIsService(rs.getString("isservice"));
                partnerDefaultConfigVO.setAutoRedirect(rs.getString("auto_redirect"));
                partnerDefaultConfigVO.setMastercardSupported(rs.getString("mastercard_supported"));
                partnerDefaultConfigVO.setBinService(rs.getString("bin_service"));
                partnerDefaultConfigVO.setDashboardAccess(rs.getString("dashboard_access"));
                partnerDefaultConfigVO.setAccountingAccess(rs.getString("accounting_access"));

                partnerDefaultConfigVO.setSettingAccess(rs.getString("setting_access"));
                partnerDefaultConfigVO.setTransactionsAccess(rs.getString("transactions_access"));
                partnerDefaultConfigVO.setInvoicingAccess(rs.getString("invoicing_access"));
                partnerDefaultConfigVO.setVirtualTerminalAccess(rs.getString("virtualterminal_access"));
                partnerDefaultConfigVO.setMerchantMgtAccess(rs.getString("merchantmgt_access"));
                partnerDefaultConfigVO.setIsAppManagerActivate(rs.getString("is_appmanager_activate"));
                partnerDefaultConfigVO.setTemplate(rs.getString("template"));
                partnerDefaultConfigVO.setHasPaid(rs.getString("haspaid"));
                partnerDefaultConfigVO.setHrAlertProof(rs.getString("hralertproof"));
                partnerDefaultConfigVO.setDataMismatchProof(rs.getString("data_mismatch_proof"));

                partnerDefaultConfigVO.setVbv(rs.getString("vbv"));
                partnerDefaultConfigVO.setCustReminderMail(rs.getString("cust_reminder_mail"));
                partnerDefaultConfigVO.setModifyMerchantDetails(rs.getString("modify_merchant_details"));
                partnerDefaultConfigVO.setModifyCompanyDetails(rs.getString("modify_company_details"));
                partnerDefaultConfigVO.setHrparameterised(rs.getString("hrparameterised"));
                partnerDefaultConfigVO.setIsPharma(rs.getString("is_pharma"));
                partnerDefaultConfigVO.setIsValidateEmail(rs.getString("is_validate_email"));
                partnerDefaultConfigVO.setDailyAmountLimit(rs.getString("daily_amount_limit"));
                partnerDefaultConfigVO.setMonthlyAmountLimit(rs.getString("monthly_amount_limit"));
                partnerDefaultConfigVO.setDailyCardLimit(rs.getString("daily_card_limit"));

                partnerDefaultConfigVO.setWeeklyCardLimit(rs.getString("weekly_card_limit"));
                partnerDefaultConfigVO.setMonthlyCardLimit(rs.getString("monthly_card_limit"));
                partnerDefaultConfigVO.setCheckLimit(rs.getString("check_limit"));
                partnerDefaultConfigVO.setIsPoweredBy(rs.getString("is_powered_by"));
                partnerDefaultConfigVO.setInvoiceTemplate(rs.getString("invoice_template"));
                partnerDefaultConfigVO.setIsWhiteListed(rs.getString("iswhitelisted"));
                partnerDefaultConfigVO.setIsMerchantLogo(rs.getString("is_merchant_logo"));
                partnerDefaultConfigVO.setIsRefund(rs.getString("is_refund"));
                partnerDefaultConfigVO.setIsPodRequired(rs.getString("is_pod_required"));
                partnerDefaultConfigVO.setIsIpWhiteListed(rs.getString("is_ip_whitelisted"));

                partnerDefaultConfigVO.setAutoSelectTerminal(rs.getString("auto_select_terminal"));
                partnerDefaultConfigVO.setWeeklyAmountLimit(rs.getString("weekly_amount_limit"));
                partnerDefaultConfigVO.setIsPciLogo(rs.getString("is_pci_logo"));
                partnerDefaultConfigVO.setIsCardRegistrationAllowed(rs.getString("is_card_registration_allowed"));
                partnerDefaultConfigVO.setIsRecurring(rs.getString("is_recurring"));
                partnerDefaultConfigVO.setIsRestrictedTicket(rs.getString("is_restricted_ticket"));
                partnerDefaultConfigVO.setIsTokenizationAllowed(rs.getString("is_tokenization_allowed"));
                partnerDefaultConfigVO.setTokenValidDays(rs.getString("token_valid_days"));
                partnerDefaultConfigVO.setIsAddressDetailsRequired(rs.getString("is_address_details_required"));
                partnerDefaultConfigVO.setBlacklistTransaction(rs.getString("blacklist_transaction"));

                partnerDefaultConfigVO.setOnlineFraudCheck(rs.getString("online_fraud_check"));
                partnerDefaultConfigVO.setMerchantEmailSent(rs.getString("merchant_email_sent"));
                partnerDefaultConfigVO.setIsSplitPayment(rs.getString("is_split_payment"));
                partnerDefaultConfigVO.setSplitPaymentType(rs.getString("split_payment_type"));
                partnerDefaultConfigVO.setRefundAllowedDays(rs.getString("refundallowed_days"));
                partnerDefaultConfigVO.setChargebackAllowedDays(rs.getString("chargeback_allowed_days"));
                partnerDefaultConfigVO.setIsCardEncryptionEnable(rs.getString("is_card_encryption_enable"));
                partnerDefaultConfigVO.setIsRestWhiteListed(rs.getString("is_rest_whitelisted"));
                partnerDefaultConfigVO.setMerchantSmsActivation(rs.getString("merchant_sms_activation"));
                partnerDefaultConfigVO.setCustomerSmsActivation(rs.getString("customer_sms_activation"));

                partnerDefaultConfigVO.setEmailLimitEnabled(rs.getString("email_limit_enabled"));
                partnerDefaultConfigVO.setIpWhiteListInvoice(rs.getString("ip_whitelist_invoice"));
                partnerDefaultConfigVO.setAddressValidationInvoice(rs.getString("address_validation_invoice"));
                partnerDefaultConfigVO.setExpDateOffset(rs.getString("exp_date_offset"));
                partnerDefaultConfigVO.setIsEmailVerified(rs.getString("is_email_verified"));
                partnerDefaultConfigVO.setSupportSection(rs.getString("support_section"));
                partnerDefaultConfigVO.setSupportNoNeeded(rs.getString("supportNoNeeded"));
                partnerDefaultConfigVO.setCardWhiteListLevel(rs.getString("card_whitelist_level"));
                partnerDefaultConfigVO.setSettingsFraudRuleConfigAccess(rs.getString("settings_fraudrule_config_access"));
                partnerDefaultConfigVO.setSettingsMerchantConfigAccess(rs.getString("settings_merchant_config_access"));
                partnerDefaultConfigVO.setAccountsAccountSummaryAccess(rs.getString("accounts_account_summary_access"));

                partnerDefaultConfigVO.setAccountsChargesSummaryAccess(rs.getString("accounts_charges_summary_access"));
                partnerDefaultConfigVO.setAccountsTransactionSummaryAccess(rs.getString("accounts_transaction_summary_access"));
                partnerDefaultConfigVO.setAccountsReportsSummaryAccess(rs.getString("accounts_reports_summary_access"));
                partnerDefaultConfigVO.setSettingsMerchantProfileAccess(rs.getString("settings_merchant_profile_access"));
                partnerDefaultConfigVO.setSettingsOrganisationProfileAccess(rs.getString("settings_organisation_profile_access"));
                partnerDefaultConfigVO.setSettingsCheckoutPageAccess(rs.getString("settings_checkout_page_access"));
                partnerDefaultConfigVO.setSettingsGenerateKeyAccess(rs.getString("settings_generate_key_access"));
                partnerDefaultConfigVO.setSettingsInvoiceConfigAccess(rs.getString("settings_invoice_config_access"));
                partnerDefaultConfigVO.setTransMgtTransactionAccess(rs.getString("transmgt_transaction_access"));
                partnerDefaultConfigVO.setTransMgtCaptureAccess(rs.getString("transmgt_capture_access"));

                partnerDefaultConfigVO.setTransMgtReversalAccess(rs.getString("transmgt_reversal_access"));
                partnerDefaultConfigVO.setTransMgtPayoutAccess(rs.getString("transmgt_payout_access"));
                partnerDefaultConfigVO.setInvoiceGenerateAccess(rs.getString("invoice_generate_access"));
                partnerDefaultConfigVO.setInvoiceHistoryAccess(rs.getString("invoice_history_access"));
                partnerDefaultConfigVO.setTokenMgtRegisterCardAccess(rs.getString("tokenmgt_register_card_access"));
                partnerDefaultConfigVO.setTokenMgtRegistrationHistoryAccess(rs.getString("tokenmgt_registration_history_access"));
                partnerDefaultConfigVO.setMerchantMgtUserManagementAccess(rs.getString("merchantmgt_user_management_access"));
                partnerDefaultConfigVO.setCardCheckLimit(rs.getString("card_check_limit"));
                partnerDefaultConfigVO.setCardTransactionLimit(rs.getString("card_transaction_limit"));
                partnerDefaultConfigVO.setDailyCardAmountLimit(rs.getString("daily_card_amount_limit"));

                partnerDefaultConfigVO.setMonthlyCardAmountLimit(rs.getString("monthly_card_amount_limit"));
                partnerDefaultConfigVO.setMultiCurrencySupport(rs.getString("multi_Currency_support"));
                partnerDefaultConfigVO.setRefundDailyLimit(rs.getString("refund_daily_limit"));
                partnerDefaultConfigVO.setMaxScoreAllowed(rs.getString("max_score_allowed"));
                partnerDefaultConfigVO.setMaxScoreAutoReversal(rs.getString("max_score_auto_reversal"));
                partnerDefaultConfigVO.setWeeklyCardAmountLimit(rs.getString("weekly_card_amount_limit"));
                partnerDefaultConfigVO.setIpValidationRequired(rs.getString("ip_validation_required"));
                partnerDefaultConfigVO.setIsPartnerLogo(rs.getString("is_partner_logo"));
                partnerDefaultConfigVO.setBinRouting(rs.getString("binRouting"));
                partnerDefaultConfigVO.setPersonalInfoDisplay(rs.getString("personal_info_display"));
                partnerDefaultConfigVO.setPersonalInfoValidation(rs.getString("personal_info_validation"));
                partnerDefaultConfigVO.setHostedPaymentPage(rs.getString("hosted_payment_page"));
                partnerDefaultConfigVO.setVbvLogo(rs.getString("vbvLogo"));
                partnerDefaultConfigVO.setMasterSecureLogo(rs.getString("masterSecureLogo"));
                partnerDefaultConfigVO.setChargebackMailSend(rs.getString("chargebackEmail"));
                partnerDefaultConfigVO.setSettingWhiteListDetails(rs.getString("settings_whitelist_details"));
                partnerDefaultConfigVO.setSettingBlacklistDetails(rs.getString("settings_blacklist_details"));
                partnerDefaultConfigVO.setEmiConfiguration(rs.getString("emi_configuration"));
                partnerDefaultConfigVO.setEmiSupport(rs.getString("emiSupport"));
                partnerDefaultConfigVO.setInternalFraudCheck(rs.getString("internalFraudCheck"));
                partnerDefaultConfigVO.setLimitRouting(rs.getString("limitRouting"));
                partnerDefaultConfigVO.setCheckoutTimer(rs.getString("checkoutTimer"));
                partnerDefaultConfigVO.setCheckoutTimerTime(rs.getString("checkoutTimerTime"));
                partnerDefaultConfigVO.setMarketplace(rs.getString("marketplace"));
                partnerDefaultConfigVO.setCardVelocityCheck(rs.getString("card_velocity_check"));
                partnerDefaultConfigVO.setMerchantOrderDetails(rs.getString("merchant_order_details"));
                partnerDefaultConfigVO.setIsSecurityLogo(rs.getString("isSecurityLogo"));
                partnerDefaultConfigVO.setRejectedTransaction(rs.getString("rejected_transaction"));
                partnerDefaultConfigVO.setVirtualCheckOut(rs.getString("virtual_checkout"));
                partnerDefaultConfigVO.setIsVirtualCheckoutAllowed(rs.getString("isVirtualCheckoutAllowed"));
                partnerDefaultConfigVO.setIsMobileAllowedForVC(rs.getString("isMobileAllowedForVC"));
                partnerDefaultConfigVO.setIsEmailAllowedForVC(rs.getString("isEmailAllowedForVC"));
                partnerDefaultConfigVO.setIsCvvStore(rs.getString("isCvvStore"));
                partnerDefaultConfigVO.setIsMultipleRefund(rs.getString("isMultipleRefund"));
                partnerDefaultConfigVO.setIsPartialRefund(rs.getString("isPartialRefund"));
                partnerDefaultConfigVO.setReconciliationNotification(rs.getString("reconciliationNotification"));
                partnerDefaultConfigVO.setTransactionNotification(rs.getString("transactionNotification"));
                partnerDefaultConfigVO.setRefundNotification(rs.getString("refundNotification"));
                partnerDefaultConfigVO.setChargebackNotification(rs.getString("chargebackNotification"));

                partnerDefaultConfigVO.setMerchantRegistrationMail(rs.getString("merchantRegistrationMail"));
                partnerDefaultConfigVO.setMerchantChangePassword(rs.getString("merchantChangePassword"));
                partnerDefaultConfigVO.setMerchantChangeProfile(rs.getString("merchantChangeProfile"));
                partnerDefaultConfigVO.setTransactionSuccessfulMail(rs.getString("transactionSuccessfulMail"));
                partnerDefaultConfigVO.setTransactionFailMail(rs.getString("transactionFailMail"));
                partnerDefaultConfigVO.setTransactionCapture(rs.getString("transactionCapture"));
                partnerDefaultConfigVO.setTransactionPayoutSuccess(rs.getString("transactionPayoutSuccess"));
                partnerDefaultConfigVO.setTransactionPayoutFail(rs.getString("transactionPayoutFail"));
                partnerDefaultConfigVO.setRefundMail(rs.getString("refundMail"));
                partnerDefaultConfigVO.setChargebackMail(rs.getString("chargebackMail"));
                partnerDefaultConfigVO.setTransactionInvoice(rs.getString("transactionInvoice"));
                partnerDefaultConfigVO.setCardRegistration(rs.getString("cardRegistration"));
                partnerDefaultConfigVO.setPayoutReport(rs.getString("payoutReport"));
                partnerDefaultConfigVO.setMonitoringAlertMail(rs.getString("monitoringAlertMail"));
                partnerDefaultConfigVO.setMonitoringSuspensionMail(rs.getString("monitoringSuspensionMail"));
                partnerDefaultConfigVO.setHighRiskRefunds(rs.getString("highRiskRefunds"));
                partnerDefaultConfigVO.setFraudFailedTxn(rs.getString("fraudFailedTxn"));
                partnerDefaultConfigVO.setDailyFraudReport(rs.getString("dailyFraudReport"));
                partnerDefaultConfigVO.setCustomerTransactionSuccessfulMail(rs.getString("customerTransactionSuccessfulMail"));
                partnerDefaultConfigVO.setCustomerTransactionFailMail(rs.getString("customerTransactionFailMail"));
                partnerDefaultConfigVO.setCustomerTransactionPayoutSuccess(rs.getString("customerTransactionPayoutSuccess"));
                partnerDefaultConfigVO.setCustomerTransactionPayoutFail(rs.getString("customerTransactionPayoutFail"));
                partnerDefaultConfigVO.setCustomerRefundMail(rs.getString("customerRefundMail"));
                partnerDefaultConfigVO.setCustomerTokenizationMail(rs.getString("customerTokenizationMail"));
                partnerDefaultConfigVO.setIsUniqueOrderIdRequired(rs.getString("isUniqueOrderIdRequired"));
                partnerDefaultConfigVO.setEmailTemplateLang(rs.getString("emailTemplateLang"));
                partnerDefaultConfigVO.setSuccessReconMail(rs.getString("successReconMail"));
                partnerDefaultConfigVO.setRefundReconMail(rs.getString("refundReconMail"));
                partnerDefaultConfigVO.setChargebackReconMail(rs.getString("chargebackReconMail"));
                partnerDefaultConfigVO.setPayoutReconMail(rs.getString("payoutReconMail"));
                partnerDefaultConfigVO.setIsMerchantLogoBO(rs.getString("isMerchantLogoBO"));
                partnerDefaultConfigVO.setCardExpiryDateCheck(rs.getString("cardExpiryDateCheck"));
                partnerDefaultConfigVO.setPayoutNotification(rs.getString("payoutNotification"));
                partnerDefaultConfigVO.setInquiryNotification(rs.getString("inquiryNotification"));
                partnerDefaultConfigVO.setVpaAddressLimitCheck(rs.getString("vpaAddressLimitCheck"));
                partnerDefaultConfigVO.setVpaAddressDailyCount(rs.getString("vpaAddressDailyCount"));
                partnerDefaultConfigVO.setVpaAddressAmountLimitCheck(rs.getString("vpaAddressAmountLimitCheck"));
                partnerDefaultConfigVO.setVpaAddressDailyAmountLimit(rs.getString("vpaAddressDailyAmountLimit"));
                partnerDefaultConfigVO.setPayoutBankAccountNoLimitCheck(rs.getString("payoutBankAccountNoLimitCheck"));
                partnerDefaultConfigVO.setBankAccountNoDailyCount(rs.getString("bankAccountNoDailyCount"));
                partnerDefaultConfigVO.setPayoutBankAccountNoAmountLimitCheck(rs.getString("payoutBankAccountNoAmountLimitCheck"));
                partnerDefaultConfigVO.setBankAccountNoDailyAmountLimit(rs.getString("bankAccountNoDailyAmountLimit"));
                partnerDefaultConfigVO.setIsShareAllowed(rs.getString("isShareAllowed"));
                partnerDefaultConfigVO.setIsSignatureAllowed(rs.getString("isSignatureAllowed"));
                partnerDefaultConfigVO.setIsSaveReceiptAllowed(rs.getString("isSaveReceiptAllowed"));
                partnerDefaultConfigVO.setDefaultLanguage(rs.getString("defaultLanguage"));
                partnerDefaultConfigVO.setIsDomainWhitelisted(rs.getString("isDomainWhitelisted"));
                partnerDefaultConfigVO.setCustomerIpLimitCheck(rs.getString("customerIpLimitCheck"));
                partnerDefaultConfigVO.setCustomerIpDailyCount(rs.getString("customerIpDailyCount"));
                partnerDefaultConfigVO.setCustomerIpAmountLimitCheck(rs.getString("customerIpAmountLimitCheck"));
                partnerDefaultConfigVO.setCustomerIpDailyAmountLimit(rs.getString("customerIpDailyAmountLimit"));
                partnerDefaultConfigVO.setCustomerNameLimitCheck(rs.getString("customerNameLimitCheck"));
                partnerDefaultConfigVO.setCustomerNameDailyCount(rs.getString("customerNameDailyCount"));
                partnerDefaultConfigVO.setCustomerNameAmountLimitCheck(rs.getString("customerNameAmountLimitCheck"));
                partnerDefaultConfigVO.setCustomerNameDailyAmountLimit(rs.getString("customerNameDailyAmountLimit"));
                partnerDefaultConfigVO.setCustomerEmailLimitCheck(rs.getString("customerEmailLimitCheck"));
                partnerDefaultConfigVO.setCustomerEmailDailyCount(rs.getString("customerEmailDailyCount"));
                partnerDefaultConfigVO.setCustomerEmailAmountLimitCheck(rs.getString("customerEmailAmountLimitCheck"));
                partnerDefaultConfigVO.setCustomerEmailDailyAmountLimit(rs.getString("customerEmailDailyAmountLimit"));
                partnerDefaultConfigVO.setCustomerPhoneLimitCheck(rs.getString("customerPhoneLimitCheck"));
                partnerDefaultConfigVO.setCustomerPhoneDailyCount(rs.getString("customerPhoneDailyCount"));
                partnerDefaultConfigVO.setCustomerPhoneAmountLimitCheck(rs.getString("customerPhoneAmountLimitCheck"));
                partnerDefaultConfigVO.setCustomerPhoneDailyAmountLimit(rs.getString("customerPhoneDailyAmountLimit"));
                partnerDefaultConfigVO.setPaybylink(rs.getString("paybylink"));
                partnerDefaultConfigVO.setTransMgtPayoutTransaction(rs.getString("transmgt_payout_transactions"));
                partnerDefaultConfigVO.setIsOTPRequired(rs.getString("isOTPRequired"));
                partnerDefaultConfigVO.setIsCardStorageRequired(rs.getString("isCardStorageRequired"));
                partnerDefaultConfigVO.setVpaAddressMonthlyCount(rs.getString("vpaAddressMonthlyCount"));
                partnerDefaultConfigVO.setVpaAddressMonthlyAmountLimit(rs.getString("vpaAddressMonthlyAmountLimit"));
                partnerDefaultConfigVO.setCustomerEmailMonthlyCount(rs.getString("customerEmailMonthlyCount"));
                partnerDefaultConfigVO.setCustomerEmailMonthlyAmountLimit(rs.getString("customerEmailMonthlyAmountLimit"));
                partnerDefaultConfigVO.setCustomerPhoneMonthlyCount(rs.getString("customerPhoneMonthlyCount"));
                partnerDefaultConfigVO.setCustomerPhoneMonthlyAmountLimit(rs.getString("customerPhoneMonthlyAmountLimit"));
                partnerDefaultConfigVO.setBankAccountNoMonthlyCount(rs.getString("bankAccountNoMonthlyCount"));
                partnerDefaultConfigVO.setBankAccountNoMonthlyAmountLimit(rs.getString("bankAccountNoMonthlyAmountLimit"));
                partnerDefaultConfigVO.setCustomerIpMonthlyCount(rs.getString("customerIpMonthlyCount"));
                partnerDefaultConfigVO.setCustomerIpMonthlyAmountLimit(rs.getString("customerIpMonthlyAmountLimit"));
                partnerDefaultConfigVO.setCustomerNameMonthlyCount(rs.getString("customerNameMonthlyCount"));
                partnerDefaultConfigVO.setCustomerNameMonthlyAmountLimit(rs.getString("customerNameMonthlyAmountLimit"));
                partnerDefaultConfigVO.setMerchant_verify_otp(rs.getString("merchant_verify_otp"));


            }
        }
        catch (SQLException e)
        {
            log.error("Leaving Merchants throwing SQL Exception as System Error :::: ", e);
            PZExceptionHandler.raiseDBViolationException("PartnerDAO", "getPartnerDefaultConfig()", null, "Common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            log.error("Leaving Merchants throwing SQL Exception as System Error :::: ", se);
            PZExceptionHandler.raiseDBViolationException("PartnerDAO", "getPartnerDefaultConfig()", null, "Common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        log.debug("Leaving getMemberDetails");
        return partnerDefaultConfigVO;
    }

    public String getPartnerKey(String partnerId)
    {
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        String partnerKey = "";
        try
        {
            conn = Database.getRDBConnection();
            String query = "select clkey from partners where partnerid = "+partnerId;
            preparedStatement = conn.prepareStatement(query);
            rs = preparedStatement.executeQuery();

            if (rs.next())
            {
                partnerKey = rs.getString("clkey");
            }
        }
        catch (SQLException e)
        {
            log.error("SQLException----",e);
        }
        catch (SystemError e)
        {
            log.error("SystemError----",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return partnerKey;
    }

    public PartnerDetailsVO geturl(String partnerID) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT termsUrl,privacyUrl,partnerName,logoname,default_theme,current_theme,hosturl,logoHeight,logoWidth FROM partners WHERE partnerId=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, partnerID);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                partnerDetailsVO.setTermsurl(rs.getString("termsUrl"));
                partnerDetailsVO.setPrivacyurl(rs.getString("privacyUrl"));
                partnerDetailsVO.setPartnerName(rs.getString("partnerName"));
                partnerDetailsVO.setLogoName(rs.getString("logoname"));
                partnerDetailsVO.setDefaultTheme(rs.getString("default_theme"));
                partnerDetailsVO.setCurrentTheme(rs.getString("current_theme"));
                partnerDetailsVO.setHostUrl(rs.getString("hosturl"));
                partnerDetailsVO.setLogoHeight(rs.getString("logoHeight"));
                partnerDetailsVO.setLogoWidth(rs.getString("logoWidth"));



            }
            transactionLogger.debug("query to get url----" + pstmt);
        }
        catch (SystemError systemError)
        {
            log.error("System Error :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMemberDetails()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("System Error :::: ", e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMemberDetails()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return partnerDetailsVO;
    }

    //Start of PartnerDashboard METHODS
   /* public String getTotalSalesAmount(String partnerName, String currency, String payBrand, String payMode)
    {
        String amount = "";
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        int counter = 1;
        StringBuilder query = new StringBuilder();
        String tableName = "transaction_common";

        if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName = "transaction_card_present";
        try
        {
            connection = Database.getRDBConnection();
            query.append("SELECT SUM(amount) AS sumAmount FROM "+tableName+" AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid WHERE tc.Status IN ('markedforreversal' ,'capturesuccess','authsuccessful','chargeback','settled','reversed') AND totype ='" + partnerName + "'");
            if (functions.isValueNull(currency))
            {
                query.append("AND currency = ? ");
            }
            if (functions.isValueNull(payBrand))
            {
                query.append("AND ct.cardtypeid = ? ");
            }
            if (functions.isValueNull(payMode))
            {
                query.append("AND pt.paymodeid = ? ");
            }
            pstmt = connection.prepareStatement(query.toString());

            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, currency);
                counter++;
            }
            if (functions.isValueNull(payBrand))
            {
                pstmt.setString(counter, payBrand);
                counter++;
            }
            if (functions.isValueNull(payMode))
            {
                pstmt.setString(counter, payMode);
                counter++;
            }
            log.debug("QUERY for getTotalSalesAmount " +pstmt.toString());
            Date date5= new Date();
            log.error("start time for getTotalSalesAmount query#### "+date5.getTime());
            rs = pstmt.executeQuery();
            log.error("end time for getTotalSalesAmount query#### "+new Date().getTime());
            log.error("diff time for getTotalSalesAmount query#### "+(new Date().getTime()-date5.getTime()));
            log.error("query for getTotalSalesAmount ####"+pstmt);
            rs.next();
            String amt = rs.getString("sumAmount");
            //double amt = rs.getInt(1);
            amount = getValidValue(amt);
            log.debug("amount in getTotalSalesAmount ===> " + amount);

        }
        catch (Exception e)
        {
            log.error("Exception while getting getTotalSalesAmount info BOX data ", e);        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return amount;
    }
*/
    public Hashtable<String,String> getTotalSalesAmount(String partnerName, String currency, String payBrand, String payMode,DateVO dateVO,String dashboard_value,String status)
    {
        String amount = "";
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        int counter = 1;
        String salescount="";
        Hashtable<String, String> hashtableforSales = new Hashtable<>();
        StringBuilder query = new StringBuilder();
        String tableName = "transaction_common";

        if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName = "transaction_card_present";
        try
        {
            connection = Database.getRDBConnection();
            DateVO lastweek= dateManager.getDateRangeNew("Last seven days");

            String lastweekstartdtstmp= functions.getLastWeekStartdtStamp(lastweek);
            String lastweekenddtstmp= functions.getLastWeekEnddtStamp(lastweek);

            query.append("SELECT SUM(amount) AS sumAmount,COUNT(amount) as salesCount FROM "+tableName+" AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid WHERE tc.Status IN ('markedforreversal' ,'capturesuccess','authsuccessful','chargeback','settled','reversed') AND totype ='" + partnerName + "'");
            if (functions.isValueNull(currency))
            {
                query.append("AND currency = ? ");
            }
            if (functions.isValueNull(payBrand))
            {
                query.append("AND ct.cardtypeid = ? ");
            }
            if (functions.isValueNull(payMode))
            {
                query.append("AND pt.paymodeid = ? ");
            }
            if (functions.isValueNull(dashboard_value))
            {
                query.append(" AND dtstamp BETWEEN '"+lastweekstartdtstmp+"' AND '"+lastweekenddtstmp+"'");
            }
            else if (functions.isValueNull(status) && functions.isValueNull(dateVO.getDateLabel()))
            {

                String datestartdtstmp= functions.getStartDtStamp(dateVO);
                String dateenddtstmp =functions.getEndDtStamp(dateVO);
                query.append(" AND dtstamp BETWEEN '" + datestartdtstmp + "' AND '" + dateenddtstmp + "'");
            }
            else if (functions.isValueNull(status))
            {
                query.append(" AND dtstamp BETWEEN '"+lastweekstartdtstmp+"' AND '"+lastweekenddtstmp+"'");
            }
            else
            {
                String datestartdtstmp= functions.getStartDtStamp(dateVO);
                String dateenddtstmp =functions.getEndDtStamp(dateVO);
                query.append(" AND dtstamp BETWEEN '" + datestartdtstmp + "' AND '" + dateenddtstmp + "'");
            }
            pstmt = connection.prepareStatement(query.toString());

            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, currency);
                counter++;
            }
            if (functions.isValueNull(payBrand))
            {
                pstmt.setString(counter, payBrand);
                counter++;
            }
            if (functions.isValueNull(payMode))
            {
                pstmt.setString(counter, payMode);
                counter++;
            }
            log.debug("QUERY for getTotalSalesAmount " +pstmt.toString());
            Date date5= new Date();
            log.error("start time for getTotalSalesAmount query#### "+date5.getTime());
            rs = pstmt.executeQuery();
            log.error("end time for getTotalSalesAmount query#### "+new Date().getTime());
            log.error("diff time for getTotalSalesAmount query#### "+(new Date().getTime()-date5.getTime()));
            log.error("query for getTotalSalesAmount ####" + pstmt);
            while(rs.next())
            {
                String amt = rs.getString("sumAmount");
                //double amt = rs.getInt(1);
                amount = getValidValue(amt);
                log.debug("amount in getTotalSalesAmount ===> " + amount);
                salescount= rs.getString("salesCount");
                if (functions.isValueNull(amount) || functions.isValueNull(salescount))
                {
                    hashtableforSales.put(amount, salescount);
                    log.error("hashtableforSales ::::: "+hashtableforSales);
                }
            }
        }
        catch (Exception e)
        {
            log.error("Exception while getting getTotalSalesAmount info BOX data ", e);        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashtableforSales;
    }

    public HashMap<String,String> getTotalAmountandCount(String partnerName, String currency, String payBrand, String payMode,DateVO dateVO,String dashboard_value,String status)
    {
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        int counter = 1;
        int refundCount=0;
        int settledcount=0;
        int chargebackcount=0;
        int capturecount=0;
        int payoutsuccesscount=0;
        int payoutfailedcount=0;
        int declinedCount=0;
        int sumfordecaCount=0;
        double sumdeclineamount= 0.0d;
        HashMap<String,String> hashforStatus= new HashMap<>();
        StringBuilder query = new StringBuilder();
        String tableName = "transaction_common";

        if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName = "transaction_card_present";
        try
        {
            connection = Database.getRDBConnection();
            DateVO lastweek= dateManager.getDateRangeNew("Last seven days");
            String lastweekstartdtstmp= functions.getLastWeekStartdtStamp(lastweek);
            String lastweekenddtstmp= functions.getLastWeekEnddtStamp(lastweek);

            query.append("Select SUM(CASE WHEN STATUS='reversed' THEN refundamount END) AS revSum,COUNT(CASE WHEN STATUS ='reversed' THEN refundamount END) AS ref, ");
            query.append("SUM(CASE WHEN STATUS='Settled' THEN amount END) AS settledSum,COUNT(CASE WHEN STATUS='Settled' THEN amount END) AS settledCount, ");
            query.append("SUM(CASE WHEN STATUS='chargeback' THEN amount END) AS chargebackSum,COUNT(CASE WHEN STATUS='chargeback' THEN amount END) AS chargebackCount, ");
            query.append("SUM(CASE WHEN STATUS='capturesuccess' THEN amount END) AS captureSum,COUNT(CASE WHEN STATUS='capturesuccess' THEN amount END) as captureCount, ");
            query.append("SUM(CASE WHEN STATUS='payoutsuccessful' THEN payoutamount END) AS payoutSuccessSum,COUNT(CASE WHEN STATUS='payoutsuccessful' THEN payoutamount END) AS payoutSuccessCount, ");
            query.append("SUM(CASE WHEN STATUS='payoutfailed' THEN payoutamount END) AS payoutFailedSum, COUNT(CASE WHEN STATUS='payoutfailed' THEN payoutamount END) AS payoutFailedCount, ");
            query.append("SUM(CASE WHEN STATUS IN('authfailed','failed') THEN amount END)AS declinedAmount, COUNT(CASE WHEN STATUS IN('authfailed','failed') THEN amount END)AS declinedCount ");

            query.append("from "+tableName+" as tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid WHERE totype ='" + partnerName+ "'");
            if (functions.isValueNull(currency))
            {
                query.append("AND currency = ? ");
            }
            if (functions.isValueNull(payBrand))
            {
                query.append("AND ct.cardtypeid = ? ");
            }
            if (functions.isValueNull(payMode))
            {
                query.append("AND pt.paymodeid = ? ");
            }

            if (functions.isValueNull(dashboard_value))
            {
                query.append(" AND dtstamp BETWEEN '"+lastweekstartdtstmp+"' AND '"+lastweekenddtstmp+"' GROUP BY STATUS");
            }
            else if (functions.isValueNull(status) && functions.isValueNull(dateVO.getDateLabel()))
            {

                String datestartdtstmp= functions.getStartDtStamp(dateVO);
                String dateenddtstmp= functions.getEndDtStamp(dateVO);
                query.append(" AND dtstamp BETWEEN '" + datestartdtstmp + "' AND '" + dateenddtstmp + "' GROUP BY STATUS");
            }
            else if (functions.isValueNull(status))
            {
                query.append(" AND dtstamp BETWEEN '"+lastweekstartdtstmp+"' AND '"+lastweekenddtstmp+"' GROUP BY STATUS");
            }
            else
            {

                String datestartdtstmp= functions.getStartDtStamp(dateVO);
                String dateenddtstmp= functions.getEndDtStamp(dateVO);
                query.append(" AND dtstamp BETWEEN '" + datestartdtstmp + "' AND '" + dateenddtstmp + "' GROUP BY STATUS");
            }

            pstmt = connection.prepareStatement(query.toString());
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, currency);
                counter++;
            }
            if (functions.isValueNull(payBrand))
            {
                pstmt.setString(counter, payBrand);
                counter++;
            }
            if (functions.isValueNull(payMode))
            {
                pstmt.setString(counter, payMode);
                counter++;
            }
            Date date5= new Date();
            log.error("getTotalAmountAndCount  query starts######## "+date5.getTime());
            rs=pstmt.executeQuery();
            log.error("getTotalAmountAndCount  query ends######## "+new Date().getTime());
            log.error("getTotalAmountAndCount query difference######### "+(new Date().getTime()-date5.getTime()));
            log.error("getTotalAmountAndCount query ######## "+pstmt);
            while(rs.next())
            {
                String refundAmount = rs.getString("revSum");
                refundCount = rs.getInt("ref");
                if (functions.isValueNull(refundAmount) && refundCount!=0)
                {
                    hashforStatus.put("reversed", refundAmount + "_" + Integer.toString(refundCount));
                }
                String settledAmount= rs.getString("settledSum");
                settledcount= rs.getInt("settledCount");
                if (functions.isValueNull(settledAmount) && settledcount!=0)
                {
                    hashforStatus.put("Settled", settledAmount+"_"+ Integer.toString(settledcount));
                }
                String chargebackamount= rs.getString("chargebackSum");
                chargebackcount= rs.getInt("chargebackCount");
                if (functions.isValueNull(chargebackamount) && chargebackcount!=0)
                {
                    hashforStatus.put("chargeback",chargebackamount+"_"+ Integer.toString(chargebackcount));
                }
                String captureSum= rs.getString("captureSum");
                capturecount= rs.getInt("captureCount");
                if (functions.isValueNull(captureSum) && capturecount!= 0)
                {
                    hashforStatus.put("capturesuccess",captureSum+"_"+Integer.toString(capturecount));
                }
                String payoutSuccessSum= rs.getString("payoutSuccessSum");
                payoutsuccesscount= rs.getInt("payoutSuccessCount");
                if (functions.isValueNull(payoutSuccessSum) && payoutsuccesscount!= 0)
                {
                    hashforStatus.put("payoutsuccessful",payoutSuccessSum+"_"+Integer.toString(payoutsuccesscount));
                }
                String payoutFailedSum= rs.getString("payoutFailedSum");
                payoutfailedcount= rs.getInt("payoutFailedCount");
                if (functions.isValueNull(payoutFailedSum) && payoutfailedcount!= 0)
                {
                    hashforStatus.put("payoutfailed",payoutFailedSum+"_"+Integer.toString(payoutfailedcount));
                }
                double declinedAmount= rs.getDouble("declinedAmount");
                declinedCount= rs.getInt("declinedCount");
                sumfordecaCount= sumfordecaCount + declinedCount;
                sumdeclineamount= sumdeclineamount+ declinedAmount;
            }
            if (sumfordecaCount!= 0 || sumdeclineamount!= 0.0d)
            {
                hashforStatus.put("declined", String.valueOf(sumdeclineamount)+"_"+Integer.toString(sumfordecaCount));
            }
            log.error("hashforStatus from partner dashboard+++ "+hashforStatus);
        }
        catch (Exception e)
        {
            log.error("Exception while getting getTotalSettledAmount info BOX data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashforStatus;
    }
    public String getTotalSettledAmount(String partnerName, String currency, String payBrand, String payMode)
    {
        String amount = "";
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        int counter = 1;
        StringBuilder query = new StringBuilder();
        String tableName = "transaction_common";

        if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName = "transaction_card_present";
        try
        {
            connection = Database.getRDBConnection();
            //String query = "SELECT SUM(amount) AS settledAmount FROM transaction_common AS tc, payment_type AS pt , card_type AS ct WHERE tc.paymodeid=pt.paymodeid AND ct.cardtypeid=tc.cardtypeid AND tc.Status ='Settled' AND totype ='" + partnerName + "'";
            query.append("SELECT SUM(amount) AS settledAmount FROM "+tableName+" AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid WHERE tc.Status ='Settled' AND totype ='" + partnerName + "'");
            if (functions.isValueNull(currency))
            {
                query.append("AND currency = ? ");
            }
            if (functions.isValueNull(payBrand))
            {
                query.append("AND ct.cardtypeid = ? ");
            }
            if (functions.isValueNull(payMode))
            {
                query.append("AND pt.paymodeid = ? ");
            }
            pstmt = connection.prepareStatement(query.toString());
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, currency);
                counter++;
            }
            if (functions.isValueNull(payBrand))
            {
                pstmt.setString(counter, payBrand);
                counter++;
            }
            if (functions.isValueNull(payMode))
            {
                pstmt.setString(counter, payMode);
                counter++;
            }
            log.debug("QUERY for getTotalSettledAmount " +pstmt.toString());
            Date date5= new Date();
            log.error("start time for getTotalSettledAmount##### "+date5.getTime());
            rs = pstmt.executeQuery();
            log.error("end time for getTotalSettledAmount##### "+new Date().getTime());
            log.error("diff time for getTotalSettledAmount##### "+(new Date().getTime()-date5.getTime()));
            log.error("query for getTotalSettledAmount##### "+pstmt);

            rs.next();
            String amt = rs.getString("settledAmount");
            //double amt = rs.getInt(1);
            amount = getValidValue(amt);
            log.debug("amount in getTotalSettledAmount ===> " + amount);

        }
        catch (Exception e)
        {
            log.error("Exception while getting getTotalSettledAmount info BOX data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return amount;
    }

    public String getTotalTotalRefundAmount(String partnerName, String currency, String payBrand, String payMode)
    {
        String amount = "";
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        int counter = 1;
        StringBuilder query = new StringBuilder();
        String tableName = "transaction_common";

        if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName = "transaction_card_present";
        try
        {
            connection = Database.getRDBConnection();
            //String query = "SELECT SUM(amount) AS refundAmount FROM transaction_common AS tc, payment_type AS pt , card_type AS ct  WHERE tc.paymodeid=pt.paymodeid AND ct.cardtypeid=tc.cardtypeid  AND tc.status='reversed' AND totype ='" + partnerName + "'";
            query.append("SELECT SUM(refundamount) AS refundAmount FROM "+tableName+" AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid WHERE tc.Status IN ('reversed' ,'partialrefund') AND totype ='" + partnerName + "'");
            if (functions.isValueNull(currency))
            {
                query.append("AND currency = ? ");
            }
            if (functions.isValueNull(payBrand))
            {
                query.append("AND ct.cardtypeid = ? ");
            }
            if (functions.isValueNull(payMode))
            {
                query.append("AND pt.paymodeid = ? ");
            }
            pstmt = connection.prepareStatement(query.toString());
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, currency);
                counter++;
            }
            if (functions.isValueNull(payBrand))
            {
                pstmt.setString(counter, payBrand);
                counter++;
            }
            if (functions.isValueNull(payMode))
            {
                pstmt.setString(counter, payMode);
                counter++;
            }
            log.debug("QUERY for getTotalTotalRefundAmount " +pstmt.toString());
            Date date5= new Date();
            log.error("start time for getTotalTotalRefundAmount##### "+date5.getTime());
            rs = pstmt.executeQuery();
            log.error("end time for getTotalTotalRefundAmount##### "+new Date().getTime());
            log.error("diff time for getTotalTotalRefundAmount##### "+(new Date().getTime()-date5.getTime()));
            log.error("query for getTotalTotalRefundAmount##### "+pstmt);

            rs.next();
            //double amt = rs.getInt(1);
            String amt = rs.getString("refundAmount");
            amount = getValidValue(amt);
            log.debug("amount in getTotalTotalRefundAmount ===> " + amount);

        }
        catch (Exception e)
        {
            log.error("Exception while getting getTotalTotalRefundAmount info BOX data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return amount;
    }

    public String getTotalChargebackAmount(String partnerName, String currency, String payBrand, String payMode)
    {
        String amount = "";
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        int counter = 1;
        StringBuilder query = new StringBuilder();
        String tableName = "transaction_common";

        if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName = "transaction_card_present";
        try
        {
            connection = Database.getRDBConnection();
            query.append("SELECT SUM(amount) AS chargebackAmount FROM "+tableName+" AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid WHERE tc.Status ='chargeback' AND totype ='" + partnerName + "'");
            if (functions.isValueNull(currency))
            {
                query.append("AND currency = ? ");
            }
            if (functions.isValueNull(payBrand))
            {
                query.append("AND ct.cardtypeid = ? ");
            }
            if (functions.isValueNull(payMode))
            {
                query.append("AND pt.paymodeid = ? ");
            }
            pstmt = connection.prepareStatement(query.toString());
            log.debug("QUERY for getTotalChargebackAmount " + pstmt.toString());

            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, currency);
                counter++;
            }
            if (functions.isValueNull(payBrand))
            {
                pstmt.setString(counter, payBrand);
                counter++;
            }
            if (functions.isValueNull(payMode))
            {
                pstmt.setString(counter, payMode);
                counter++;
            }
            log.debug("QUERY for getTotalChargebackAmount " +pstmt.toString());
            Date date5= new Date();
            log.error("start time for getTotalChargebackAmount query#### "+date5.getTime());
            rs = pstmt.executeQuery();
            log.error("end time for getTotalChargebackAmount query#### " + new Date().getTime());
            log.error("diff time for getTotalChargebackAmount query#### "+(new Date().getTime()-date5.getTime()));
            log.error("query for getTotalChargebackAmount#### "+pstmt);
            rs.next();
            //double amt = rs.getInt(1);
            String amt = rs.getString("chargebackAmount");
            amount = getValidValue(amt);
            log.debug("amount in getTotalChargebackAmount ===> " + amount);

        }
        catch (Exception e)
        {
            log.error("Exception while getting getTotalChargebackAmount info BOX data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return amount;
    }

    /* public String getTotalDeclinedAmount(String partnerName, String currency, String payBrand, String payMode)
     {
         String amount = "";
         Connection connection = null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;
         int counter = 1;
         StringBuilder query = new StringBuilder();
         String tableName = "transaction_common";

         if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
             tableName = "transaction_card_present";
         try
         {
             connection = Database.getRDBConnection();
             query.append("SELECT SUM(amount) AS declinedAmount FROM "+tableName+" AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid WHERE tc.status IN ('authfailed','failed') AND totype ='" + partnerName + "'");
             if (functions.isValueNull(currency))
             {
                 query.append("AND currency = ? ");
             }
             if (functions.isValueNull(payBrand))
             {
                 query.append("AND ct.cardtypeid = ? ");
             }
             if (functions.isValueNull(payMode))
             {
                 query.append("AND pt.paymodeid = ? ");
             }
             pstmt = connection.prepareStatement(query.toString());
             log.debug("QUERY for getTotalDeclinedAmount " +pstmt.toString());

             if (functions.isValueNull(currency))
             {
                 pstmt.setString(counter, currency);
                 counter++;
             }
             if (functions.isValueNull(payBrand))
             {
                 pstmt.setString(counter, payBrand);
                 counter++;
             }
             if (functions.isValueNull(payMode))
             {
                 pstmt.setString(counter, payMode);
                 counter++;
             }
             Date date5= new Date();
             log.error("start time for getTotalDeclinedAmount query#### "+date5.getTime());
             rs = pstmt.executeQuery();
             log.error("end time for getTotalDeclinedAmount query#### "+new Date().getTime());
             log.error("diff time for getTotalDeclinedAmount query#### "+(new Date().getTime()-date5.getTime()));
             log.error("query for getTotalDeclinedAmount#### "+pstmt);

             rs.next();
             //double amt = rs.getInt(1);
             String amt = rs.getString("declinedAmount");
             amount = getValidValue(amt);
             log.debug("amount in getTotalDeclinedAmount ===> " +amount);
         }
         catch (Exception e)
         {
             log.error("Exception while getting getTotalDeclinedAmount info BOX data ", e);
         }
         finally
         {
             Database.closeResultSet(rs);
             Database.closePreparedStatement(pstmt);
             Database.closeConnection(connection);
         }
         return amount;
     }
 */
    /*public Hashtable<String,String> getTotalDeclinedAmount(String partnerName, String currency, String payBrand, String payMode,DateVO dateVO,String dashboard_value,String status)
    {
        System.out.println("inside getTotalDeclinedAmount method....");
        String amount = "";
        String count="";
        Hashtable<String,String> declinedhashtable= new Hashtable<>();
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        int counter = 1;
        StringBuilder query = new StringBuilder();
        String tableName = "transaction_common";

        if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName = "transaction_card_present";
        try
        {
            connection = Database.getRDBConnection();
            DateVO lastweek= dateManager.getDateRangeNew("last_seven_days");
            System.out.println("Last week start date for getTotalDeclinedAmount +++++++ "+lastweek.getStartDate());
            System.out.println("Last week end date for getTotalDeclinedAmount +++++++ "+lastweek.getEndDate());

            String lastweekstartdtstmp= Functions.getLastWeekStartdtStamp(lastweek);
            System.out.println(("lastweekstartdtstmp getTotalDeclinedAmount +++ "+lastweekstartdtstmp));

            String lastweekenddtstmp= Functions.getLastWeekEnddtStamp(lastweek);
            System.out.println(("lastweekenddtstmp getTotalDeclinedAmount+++ " + lastweekenddtstmp));

            query.append("SELECT SUM(amount) AS declinedAmount,COUNT(amount) as declinedCount FROM "+tableName+" AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid WHERE tc.status IN ('authfailed','failed') AND totype ='" + partnerName + "'");
            if (functions.isValueNull(currency))
            {
                query.append("AND currency = ? ");
            }
            if (functions.isValueNull(payBrand))
            {
                query.append("AND ct.cardtypeid = ? ");
            }
            if (functions.isValueNull(payMode))
            {
                query.append("AND pt.paymodeid = ? ");
            }
            if (functions.isValueNull(dashboard_value))
            {
                System.out.println("inside the value case 2: " + dashboard_value);
                query.append(" AND dtstamp BETWEEN '"+lastweekstartdtstmp+"' AND '"+lastweekenddtstmp+"'");

            }
            else if (functions.isValueNull(status) && functions.isValueNull(dateVO.getDateLabel()))
            {
                String datestartdtstmp= Functions.getStartDtStamp(dateVO);
                String dateenddtstmp= Functions.getEndDtStamp(dateVO);

                System.out.println("datestartdtstmp for getTotalDeclinedAmount "+datestartdtstmp);
                System.out.println("dateenddtstmp for getTotalDeclinedAmount "+dateenddtstmp);
                query.append(" AND dtstamp BETWEEN '" + datestartdtstmp + "' AND '" + dateenddtstmp + "'");
            }
            else if (functions.isValueNull(status))
            {
                query.append(" AND dtstamp BETWEEN '"+lastweekstartdtstmp+"' AND '"+lastweekenddtstmp+"'");
            }
            else
            {
                String datestartdtstmp= Functions.getStartDtStamp(dateVO);
                String dateenddtstmp= Functions.getEndDtStamp(dateVO);

                System.out.println("datestartdtstmp for getTotalDeclinedAmount "+datestartdtstmp);
                System.out.println("dateenddtstmp for getTotalDeclinedAmount "+dateenddtstmp);
                query.append(" AND dtstamp BETWEEN '" + datestartdtstmp + "' AND '" + dateenddtstmp + "'");
            }
            pstmt = connection.prepareStatement(query.toString());
            log.debug("QUERY for getTotalDeclinedAmount " +pstmt.toString());

            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, currency);
                counter++;
            }
            if (functions.isValueNull(payBrand))
            {
                pstmt.setString(counter, payBrand);
                counter++;
            }
            if (functions.isValueNull(payMode))
            {
                pstmt.setString(counter, payMode);
                counter++;
            }
            Date date5= new Date();
            log.error("start time for getTotalDeclinedAmount query#### "+date5.getTime());
            rs = pstmt.executeQuery();
            log.error("end time for getTotalDeclinedAmount query#### "+new Date().getTime());
            log.error("diff time for getTotalDeclinedAmount query#### "+(new Date().getTime()-date5.getTime()));
            log.error("query for getTotalDeclinedAmount#### "+pstmt);

            while(rs.next())
            {
                String amt = rs.getString("declinedAmount");
                amount = getValidValue(amt);
                log.debug("amount in getTotalDeclinedAmount ===> " +amount);
                count= rs.getString("declinedCount");
                if (functions.isValueNull(amount) || functions.isValueNull(count))
                {
                    declinedhashtable.put(amount,count);
                }
            }
            //double amt = rs.getInt(1);

        }
        catch (Exception e)
        {
            log.error("Exception while getting getTotalDeclinedAmount info BOX data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return declinedhashtable;
    }
*/
    public List<String> getValidCurrencyListForPartner(String partnerName)
    {
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        List<String> currencyList = new ArrayList();
        //int counter = 1;
        StringBuilder query = new StringBuilder();
        try
        {
            connection = Database.getRDBConnection();
            query.append("SELECT DISTINCT (UPPER(currency)) AS currencyName FROM transaction_common WHERE STATUS IN ('markedforreversal' ,'chargeback','capturesuccess' ,'settled','reversed','authsuccessful') AND totype =?");
            query.append(" and currency!= '' GROUP BY currency ");
            pstmt = connection.prepareStatement(query.toString());
            log.debug("QUERY for getValidCurrencyListForPartner " +pstmt.toString());
            pstmt.setString(1, partnerName);
            Date date5= new Date();
            log.error("start time for getValidCurrencyListForPartner query##### "+date5.getTime());
            rs = pstmt.executeQuery();
            log.error("end time for getValidCurrencyListForPartner query##### "+new Date().getTime());
            log.error("differ time for getValidCurrencyListForPartner query##### "+(new Date().getTime()-date5.getTime()));
            log.error("Query for getValidCurrencyListForPartner query##### "+pstmt);

            while (rs.next())
            {
                currencyList.add(rs.getString("currencyName"));
            }
        }
        catch (Exception e)
        {
            log.error("Exception while getting getValidCurrencyListForPartner data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return currencyList;
    }

    public HashMap<String, String> getValidPayBrandListForPartner(String partnerName,String bandList)//QUERY TAKING TIME
    {

        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        //int counter = 1;
        Functions functions1 = new Functions();
        StringBuilder query = new StringBuilder();
        HashMap<String, String> hashMapPayBrandDetails = new HashMap<>();
        String tableName = "transaction_common";
        if(functions1.isValueNull(bandList) && bandList.equals("32")){
            tableName = "transaction_card_present";
        }
        try
        {
            //SELECT DISTINCT (ct.cardtypeid) AS cardTypeId ,ct.cardType AS cardType FROM card_type AS ct JOIN transaction_common AS tc ON tc.cardtypeid=ct.cardtypeid WHERE totype=? ORDER BY cardType ASC
            connection = Database.getRDBConnection();
            query.append("SELECT DISTINCT (ct.cardtypeid) AS cardTypeId ,ct.cardType AS cardType FROM card_type AS ct JOIN "+tableName+" AS tc ON tc.cardtypeid=ct.cardtypeid WHERE totype=? AND paymodeid =? ORDER BY cardType ASC");   //TIME TAKING
            pstmt = connection.prepareStatement(query.toString());
            pstmt.setString(1, partnerName);
            pstmt.setString(2, bandList);
            log.debug("QUERY for getValidPayBrandListForPartner " +pstmt.toString());
            Date date5= new Date();
            log.error("start time for getValidPayBrandListForPartner query##### "+date5.getTime());
            rs = pstmt.executeQuery();
            log.error("end time for getValidPayBrandListForPartner query##### "+new Date().getTime());
            log.error("differ time for getValidPayBrandListForPartner query##### "+(new Date().getTime()-date5.getTime()));
            log.error("Query for getValidPayBrandListForPartner query##### "+pstmt);
            while (rs.next())
            {
                hashMapPayBrandDetails.put(rs.getString("cardTypeId"), rs.getString("cardType"));
            }
        }
        catch (Exception e)
        {
            log.error("Exception while getting getValidPayBrandListForPartner data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashMapPayBrandDetails;
    }


    public HashMap<String, String> getValidPayModeListForPartner(String partnerName)//TIME TAKING
    {
        Connection connection = null;
        PreparedStatement pstmt=null,pstmt1=null;
        ResultSet rs=null,rs1=null;
        StringBuilder query = new StringBuilder();
        StringBuilder query1 = new StringBuilder();
        HashMap<String, String> hashMapPayModeDetails = new HashMap<>();
        try
        {
            connection = Database.getRDBConnection();
            query.append("SELECT DISTINCT(pt.paymodeid) AS payModeId, pt.paymentType AS paymentType FROM payment_type AS pt JOIN transaction_common AS tc ON tc.paymodeid=pt.paymodeid WHERE totype=? ORDER BY paymentType ASC");//TIME TAKING
            pstmt = connection.prepareStatement(query.toString());
            pstmt.setString(1, partnerName);
            Date date5= new Date();
            log.error("start time for getValidPayModeListForPartner query##### "+date5.getTime());
            rs = pstmt.executeQuery();
            log.error("end time for getValidPayModeListForPartner query##### "+new Date().getTime());
            log.error("differ time for getValidPayModeListForPartner query##### "+(new Date().getTime()-date5.getTime()));
            log.error("Query for getValidPayModeListForPartner query##### "+pstmt);
            while (rs.next())
            {
                hashMapPayModeDetails.put(rs.getString("payModeId"), rs.getString("paymentType"));
            }

            query1.append("SELECT DISTINCT(pt.paymodeid) AS payModeId, pt.paymentType AS paymentType FROM payment_type AS pt JOIN transaction_card_present AS tc ON tc.paymodeid=pt.paymodeid WHERE totype=? ORDER BY paymentType ASC");//TIME TAKING
            pstmt1 = connection.prepareStatement(query1.toString());
            pstmt1.setString(1, partnerName);
            Date date6= new Date();
            log.error("start time for getValidPayModeListForPartner query 1##### "+date6.getTime());
            rs1 = pstmt1.executeQuery();
            log.error("end time for getValidPayModeListForPartner query 1 ##### "+new Date().getTime());
            log.error("differ time for getValidPayModeListForPartner query 1 ##### "+(new Date().getTime()-date6.getTime()));
            log.error("Query for getValidPayModeListForPartner query 1 ##### "+pstmt);
            while (rs1.next())
            {
                hashMapPayModeDetails.put(rs1.getString("payModeId"), rs1.getString("paymentType"));
            }
        }
        catch (Exception e)
        {
            log.error("Exception while getting getValidPayModeListForPartner data ", e);
        }
        finally

        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashMapPayModeDetails;
    }

    public HashMap<String, String> getSalesPerCurrencyChartForPartner(String partnerName, String currency, String payBrand, String payMode,DateVO dateVO,String dashboard_value,String status)
    {
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        int counter = 1;
        StringBuilder query = new StringBuilder();
        HashMap<String, String> hashMapSalesPerCurrencyChart = new HashMap<>();
        String tableName = "transaction_common";

        if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName = "transaction_card_present";
        try
        {
            connection = Database.getRDBConnection();
            DateVO lastweek= dateManager.getDateRangeNew("Last seven days");

            String lastweekstartdtstmp= functions.getLastWeekStartdtStamp(lastweek);
            String lastweekenddtstmp= functions.getLastWeekEnddtStamp(lastweek);

            if (functions.isValueNull(dashboard_value))
            {
                query.append("SELECT (UPPER(currency)) AS currencyName ,SUM(amount) AS sumAmount FROM "+tableName+" AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid  WHERE tc.Status IN ('markedforreversal' ,'chargeback','capturesuccess' ,'settled','reversed','authsuccessful') AND totype ='" + partnerName + "'");
            }
            else if (functions.isValueNull(status) && "sales".equalsIgnoreCase(status))
            {
                query.append("SELECT (UPPER(currency)) AS currencyName ,SUM(amount) AS sumAmount FROM "+tableName+" AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid WHERE tc.Status IN ('markedforreversal' ,'chargeback','capturesuccess' ,'settled','reversed','authsuccessful') AND totype ='" + partnerName + "'");
            }
            else if (functions.isValueNull(status) && "payout".equalsIgnoreCase(status))
            {
                query.append("SELECT (UPPER(currency)) AS currencyName ,SUM(payoutamount) AS payoutAmount FROM "+tableName+" AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid WHERE tc.Status ='payoutsuccessful' AND totype ='" + partnerName + "'");
            }

            if (functions.isValueNull(currency))
            {
                query.append("AND currency = ? ");
            }
            if (functions.isValueNull(payBrand))
            {
                query.append("AND ct.cardtypeid = ? ");
            }
            if (functions.isValueNull(payMode))
            {
                query.append("AND pt.paymodeid = ? ");
            }
            if (functions.isValueNull(dashboard_value))
            {
                query.append(" AND dtstamp BETWEEN '"+lastweekstartdtstmp+"' AND '"+lastweekenddtstmp+"'");
            }
            else if (functions.isValueNull(dateVO.getDateLabel()) && (!functions.isValueNull(status) || functions.isValueNull(status)))
            {
                String datestartdtstmp= functions.getStartDtStamp(dateVO);
                String dateenddtstmp= functions.getEndDtStamp(dateVO);
                query.append(" AND dtstamp BETWEEN '" + datestartdtstmp + "' AND '" + dateenddtstmp + "'");
            }
            else if (functions.isValueNull(status))
            {
                query.append(" AND dtstamp BETWEEN '"+lastweekstartdtstmp+"' AND '"+lastweekenddtstmp+"'");
            }

            query.append(" GROUP BY currency ");
            pstmt = connection.prepareStatement(query.toString());
            log.debug("QUERY for getSalesPerCurrencyChartForPartner " +pstmt.toString());
            //pstmt.setString(1, partnerName);
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, currency);
                counter++;
            }
            if (functions.isValueNull(payBrand))
            {
                pstmt.setString(counter, payBrand);
                counter++;
            }
            if (functions.isValueNull(payMode))
            {
                pstmt.setString(counter, payMode);
                counter++;
            }
            Date date5= new Date();
            log.error("start time for getSalesPerCurrencyChartForPartner #####  "+date5.getTime());
            rs = pstmt.executeQuery();
            log.error("end time for getSalesPerCurrencyChartForPartner #####  "+new Date().getTime());
            log.error("diff time for getSalesPerCurrencyChartForPartner #####  "+(new Date().getTime()-date5.getTime()));
            log.error("query for getSalesPerCurrencyChartForPartner #####  "+pstmt);
            while (rs.next())
            {
                Float PayoutAmount = 0.0f;

                if (functions.isValueNull(status) && "payout".equalsIgnoreCase(status))
                {
                    PayoutAmount = rs.getFloat("PayoutAmount");
                    hashMapSalesPerCurrencyChart.put(rs.getString("currencyName"), String.valueOf(PayoutAmount));
                }
                else
                {
                    hashMapSalesPerCurrencyChart.put(rs.getString("currencyName"), rs.getString("sumAmount"));
                }

                //  hashMapSalesPerCurrencyChart.put(rs.getString("currencyName"), rs.getString("sumAmount"));
            }
        }
        catch (Exception e)
        {
            log.error("Exception while getting getSalesPerCurrencyChartForPartner data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashMapSalesPerCurrencyChart;
    }

    public HashMap<String, String> getbincountrysuccessful(String totype ,String toid ,String country, String tdtstamp, String fdtstamp ,String accountid, String currency)
    {
        log.debug("Entering getbincountrysuccessful:::::");
        //System.out.println("Entering getbincountrysuccessful:::::");
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        StringBuilder query = new StringBuilder();
        HashMap<String, String> hashMapbincountrysuccessful = new HashMap<>();
        //System.out.println("Hashmap"+hashMapbincountrysuccessful);

        try
        {
            connection = Database.getRDBConnection();
            query.append("SELECT bd.`bin_country_code_A2`,COUNT(trackingId) AS COUNT FROM transaction_common tc,bin_details bd WHERE tc.`trackingid`=bd.`icicitransid` AND STATUS IN ('capturesuccess' ,'settled','authsuccessful','reversed','markedforreversal','chargeback') AND toid = ? ");

            if (functions.isValueNull(accountid))
            {
                query.append(" AND tc.accountid = ? ");
            }
            if (functions.isValueNull(country))
            {
                query.append("AND country = ? ");
            }
            if (functions.isValueNull(currency))
            {
                query.append("AND currency = ? ");
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" AND dtstamp<= ?");
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" AND dtstamp>= ?");
            }
            query.append(" AND bd.`bin_country_code_A2` IS NOT NULL  GROUP BY bd.bin_country_code_A2 ");
            pstmt = connection.prepareStatement(query.toString());
            int counter = 2;
            //pstmt.setString(1, totype);
            pstmt.setString(1, toid);

            if (functions.isValueNull(accountid))
            {
                pstmt.setString(counter, accountid);
                counter++;
            }
            if (functions.isValueNull(country))
            {
                pstmt.setString(counter, country);
                counter++;
            }
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, currency);
                counter++;
            }
            if (functions.isValueNull(tdtstamp))
            {
                pstmt.setString(counter, tdtstamp);
                counter++;
            }

            if (functions.isValueNull(fdtstamp))
            {
                pstmt.setString(counter, fdtstamp);
                counter++;
            }
            log.debug("QUERY for getbincountrysuccessful " +pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                //hashMapSalesPerCurrencyChart.put(rs.getString("country_name"), rs.getString("sumAmount"));
                hashMapbincountrysuccessful.put(rs.getString("bin_country_code_A2"), rs.getString("count"));
            }
        }
        catch (Exception e)
        {
            log.error("Exception while getting getbincountrysuccessful data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashMapbincountrysuccessful;
    }

    public HashMap<String, String> getbincountryfailed(String totype,String toid ,String country, String tdtstamp, String fdtstamp, String accountid, String currency )
    {
        log.debug("Entering getbincountryfailed:::::");
        //System.out.println("Entering getbincountryfailed:::::");
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        StringBuilder query = new StringBuilder();
        HashMap<String, String> hashMapbincountryfailed = new HashMap<>();
        //System.out.println("Hashmap"+hashMapbincountryfailed);

        try
        {
            connection = Database.getRDBConnection();
            query.append("SELECT bd.`bin_country_code_A2`,COUNT(trackingId) AS COUNT FROM transaction_common tc,bin_details bd WHERE tc.`trackingid`=bd.`icicitransid` AND STATUS IN ('failed','authfailed') AND toid = ? ");

            if (functions.isValueNull(accountid))
            {
                query.append(" AND tc.accountid = ? ");
            }
            if (functions.isValueNull(country))
            {
                query.append("AND country = ? ");
            }
            if (functions.isValueNull(currency))
            {
                query.append("AND currency = ? ");
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" AND dtstamp<= ?");
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" AND dtstamp>= ?");
            }
            query.append(" AND bd.`bin_country_code_A2` IS NOT NULL  GROUP BY bd.bin_country_code_A2 ");
            pstmt = connection.prepareStatement(query.toString());
            int counter = 2;
            //pstmt.setString(1, totype);
            pstmt.setString(1, toid);

            if (functions.isValueNull(accountid))
            {
                pstmt.setString(counter, accountid);
                counter++;
            }
            if (functions.isValueNull(country))
            {
                pstmt.setString(counter, country);
                counter++;
            }
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, currency);
                counter++;
            }
            if (functions.isValueNull(tdtstamp))
            {
                pstmt.setString(counter, tdtstamp);
                counter++;
            }

            if (functions.isValueNull(fdtstamp))
            {
                pstmt.setString(counter, fdtstamp);
                counter++;
            }
            log.debug("QUERY for getbincountryfailed " +pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                hashMapbincountryfailed.put(rs.getString("bin_country_code_A2"), rs.getString("count"));
            }
        }
        catch (Exception e)
        {
            log.error("Exception while getting getbincountryfailed data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashMapbincountryfailed;
    }

    public HashMap<String, String> getipcountrysuccessful(String totype ,String toid ,String tdtstamp, String fdtstamp, String accountid, String currency)
    {
        log.debug("Entering getipcountrysuccessful:::::");
        //System.out.println("Entering getipcountrysuccessful:::::");
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        StringBuilder query = new StringBuilder();
        HashMap<String, String> hashMapipcountrysuccessful = new HashMap<>();
        //List<String> countryList=new ArrayList<>();
        //System.out.println("Hashmap"+hashMapipcountrysuccessful);

        try
        {
            connection = Database.getRDBConnection();
            //query.append("SELECT tcd.ipaddress, COUNT(tc.trackingId) AS COUNT FROM transaction_common AS tc,transaction_common_details AS tcd WHERE tc.`trackingid`=tcd.`trackingid` AND tc.STATUS IN ('capturesuccess' ,'settled','authsuccessful') AND tcd.ipaddress IS NOT NULL AND tcd.`ipaddress`!=''  AND totype = ? AND toid = ?");
            query.append("SELECT tc.customerIpCountry, COUNT(tc.trackingId) AS COUNT FROM transaction_common AS tc WHERE tc.STATUS IN ('capturesuccess' ,'settled','authsuccessful','reversed','markedforreversal','chargeback') AND tc.customerIpCountry IS NOT NULL AND tc.`customerIpCountry`!='-' AND tc.`customerIpCountry`!='' AND toid = ?");

            if (functions.isValueNull(accountid))
            {
                query.append(" AND accountid= ?");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" AND currency= ?");
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" AND dtstamp<= ?");
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" AND dtstamp>= ?");
            }
            query.append(" GROUP BY tc.customerIpCountry ");
            pstmt = connection.prepareStatement(query.toString());
            int counter = 2;
            //pstmt.setString(1, totype);
            pstmt.setString(1, toid);

            if (functions.isValueNull(accountid))
            {
                pstmt.setString(counter, accountid);
                counter++;
            }
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, currency);
                counter++;
            }

            if (functions.isValueNull(tdtstamp))
            {
                pstmt.setString(counter, tdtstamp);
                counter++;
            }

            if (functions.isValueNull(fdtstamp))
            {
                pstmt.setString(counter, fdtstamp);
                counter++;
            }
            log.debug("QUERY for getipcountrysuccessful " +pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                hashMapipcountrysuccessful.put(rs.getString("customerIpCountry"), rs.getString("count"));

                /*String countryRs=functions.getIPCountryShort(rs.getString("ipAddress"));
                if(countryList.contains(countryRs))
                {
                    String trackingIdCount=String.valueOf(Integer.parseInt(hashMapipcountrysuccessful.get(countryRs))+rs.getInt("count"));
                    hashMapipcountrysuccessful.put(countryRs, trackingIdCount);
                }
                else
                {
                    hashMapipcountrysuccessful.put(rs.getString("countryList"), rs.getString("count"));
                    countryList.add(countryRs);
                }*/
            }
        }
        catch (Exception e)
        {
            log.error("Exception while getting getipcountrysuccessful data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashMapipcountrysuccessful;
    }

    public HashMap<String, String> getipcountryfailed(String totype ,String toid,String tdtstamp, String fdtstamp, String accountid, String currency)
    {
        log.debug("Entering getipcountryfailed:::::");
        //System.out.println("Entering getipcountryfailed:::::");
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        StringBuilder query = new StringBuilder();
        HashMap<String, String> hashMapipcountryfailed = new HashMap<>();
        //List<String> countryList=new ArrayList<>();
        //System.out.println("Hashmap"+hashMapipcountryfailed);

        try
        {
            connection = Database.getRDBConnection();
            //query.append("SELECT tcd.ipaddress, COUNT(tc.trackingId) AS COUNT FROM transaction_common AS tc,transaction_common_details AS tcd WHERE tc.`trackingid`=tcd.`trackingid` AND tc.STATUS IN ('failed','authfailed') AND tcd.ipaddress IS NOT NULL AND tcd.`ipaddress`!=''  AND totype = ? AND toid = ?");
            query.append("SELECT tc.customerIpCountry, COUNT(tc.trackingId) AS COUNT FROM transaction_common AS tc WHERE tc.STATUS IN ('failed','authfailed') AND tc.customerIpCountry IS NOT NULL AND tc.`customerIpCountry`!='-' AND tc.`customerIpCountry`!='' AND toid = ?");

            if (functions.isValueNull(accountid))
            {
                query.append(" AND accountid= ?");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" AND currency= ?");
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" AND dtstamp<= ?");
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" AND dtstamp>= ?");
            }
            query.append(" GROUP BY tc.customerIpCountry ");
            pstmt = connection.prepareStatement(query.toString());
            int counter = 2;
            //pstmt.setString(1, totype);
            pstmt.setString(1, toid);

            if (functions.isValueNull(accountid))
            {
                pstmt.setString(counter, accountid);
                counter++;
            }
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, currency);
                counter++;
            }

            if (functions.isValueNull(tdtstamp))
            {
                pstmt.setString(counter, tdtstamp);
                counter++;
            }

            if (functions.isValueNull(fdtstamp))
            {
                pstmt.setString(counter, fdtstamp);
                counter++;
            }
            log.debug("QUERY for getipcountryfailed " +pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                hashMapipcountryfailed.put(rs.getString("customerIpCountry"), rs.getString("count"));

                /*String countryRs=functions.getIPCountryShort(rs.getString("ipAddress"));
                if(countryList.contains(countryRs))
                {
                    String trackingIdCount=String.valueOf(Integer.parseInt(hashMapipcountryfailed.get(countryRs))+rs.getInt("count"));
                    hashMapipcountryfailed.put(countryRs, trackingIdCount);
                }else
                {
                    hashMapipcountryfailed.put(countryRs, rs.getString("count"));
                    countryList.add(countryRs);
                }*/
            }
        }
        catch (Exception e)
        {
            log.error("Exception while getting getipcountryfailed data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashMapipcountryfailed;
    }

    public HashMap<String, String> getValidStatusChartForPartner(String partnerName, String currency, String payBrand, String payMode,DateVO dateVO,String dashboard_value,String status)
    {
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        HashMap<String, String> hashMapValidStatusChart = new HashMap<>();
        int counter = 1;
        StringBuilder query = new StringBuilder();
        String tableName = "transaction_common";

        if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName = "transaction_card_present";
        try
        {
            connection = Database.getRDBConnection();
            DateVO lastweek= dateManager.getDateRangeNew("Last seven days");
            String lastweekstartdtstmp= functions.getLastWeekStartdtStamp(lastweek);
            String lastweekenddtstmp= functions.getLastWeekEnddtStamp(lastweek);

            query.append("SELECT STATUS AS statusName ,COUNT(STATUS) AS countStatus FROM "+tableName+" AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid WHERE totype ='" + partnerName + "'");
            if (functions.isValueNull(currency))
            {
                query.append("AND currency = ? ");
            }
            if (functions.isValueNull(payBrand))
            {
                query.append("AND ct.cardtypeid = ? ");
            }
            if (functions.isValueNull(payMode))
            {
                query.append("AND pt.paymodeid = ? ");
            }
            if (functions.isValueNull(dashboard_value))
            {
                query.append(" AND dtstamp BETWEEN '"+lastweekstartdtstmp+"' AND '"+lastweekenddtstmp+"' GROUP BY STATUS ");
            }
            else if (functions.isValueNull(status) && functions.isValueNull(dateVO.getDateLabel()))
            {
                String datestartdtstmp= functions.getStartDtStamp(dateVO);
                String dateenddtstmp= functions.getEndDtStamp(dateVO);
                query.append(" AND dtstamp BETWEEN '"+datestartdtstmp+"' AND '"+dateenddtstmp+"' GROUP BY STATUS ");
            }
            else if (functions.isValueNull(status))
            {
                query.append(" AND dtstamp BETWEEN '"+lastweekstartdtstmp+"' AND '"+lastweekenddtstmp+"' GROUP BY STATUS ");
            }
            else
            {
                String datestartdtstmp= functions.getStartDtStamp(dateVO);
                String dateenddtstmp= functions.getEndDtStamp(dateVO);
                query.append(" AND dtstamp BETWEEN '"+datestartdtstmp+"' AND '"+dateenddtstmp+"' GROUP BY STATUS ");
            }
            pstmt = connection.prepareStatement(query.toString());
            log.debug("QUERY for getValidStatusChartForPartner " +pstmt.toString());
            //pstmt.setString(1, partnerName);
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, currency);
                counter++;
            }
            if (functions.isValueNull(payBrand))
            {
                pstmt.setString(counter, payBrand);
                counter++;
            }
            if (functions.isValueNull(payMode))
            {
                pstmt.setString(counter, payMode);
                counter++;
            }
            Date date5= new Date();
            log.error("start time for getValidStatusChartForPartner query##### "+date5.getTime());
            rs = pstmt.executeQuery();
            log.error("end time for getValidStatusChartForPartner query##### "+new Date().getTime());
            log.error("diff time for getValidStatusChartForPartner query##### "+(new Date().getTime()-date5.getTime()));
            log.error("query for getValidStatusChartForPartner query##### "+pstmt);

            while (rs.next())
            {
                hashMapValidStatusChart.put(rs.getString("statusName"), rs.getString("countStatus"));
            }
        }
        catch (Exception e)
        {
            log.error("Exception while getting getValidStatusChartForPartner data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashMapValidStatusChart;
    }

    public  String getMonthlyTransactionDetailsForPartner(String partnerName, DateVO dateVO, String currency, String payBrand, String payMode)//PENDING METHOD FOR SALES CHART
    {
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;

        JSONObject obj = new JSONObject();
        String tableName = "transaction_common";

        if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName = "transaction_card_present";

        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT (UPPER(currency)) AS currencyName, SUM(amount) AS transamount FROM "+tableName+" AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid WHERE tc.Status IN ('markedforreversal' ,'chargeback','capturesuccess' ,'settled','reversed','authsuccessful') AND  totype=?";
            query = query + " AND FROM_UNIXTIME(dtstamp) BETWEEN '" + dateVO.getStartDate().split(" ")[0] + "' AND '" + dateVO.getEndDate().split(" ")[0] + "'";

            if (functions.isValueNull(currency))
            {
                query = query + " AND currency = '" + currency + "'";
            }
            if (functions.isValueNull(payBrand))
            {
                query = query + " AND ct.cardtypeid = '" + payBrand + "'";
            }
            if (functions.isValueNull(payMode))
            {
                query = query + " AND pt.paymodeid = '" + payMode + "'";
            }
            query = query + " GROUP BY currency";

            pstmt = connection.prepareStatement(query);
            log.debug("QUERY for getMonthlyTransactionDetailsForPartner " +pstmt.toString());
            pstmt.setString(1, partnerName);
            Date date5= new Date();
            log.error("start time for getMonthlyTransactionDetailsForPartner #### "+date5.getTime());
            rs = pstmt.executeQuery();
            log.error("end time for getMonthlyTransactionDetailsForPartner #### "+new Date().getTime());
            log.error("diff time for getMonthlyTransactionDetailsForPartner #### "+(new Date().getTime()-date5.getTime()));
            log.error("query for getMonthlyTransactionDetailsForPartner #### "+pstmt);

            while (rs.next())
            {
                obj.put(rs.getString("currencyName"), rs.getString("transamount"));
            }
        }
        catch (Exception e)
        {
            log.error("Exception while getting getMonthlyTransactionDetailsForPartner data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return obj.toString().replaceAll("\\}", "").replaceAll("\\{", "");
    }

    public StringBuffer getMonthlyTransactionDetailsForPartnerNew(String partnerName, DateVO dateVO, String currency, String payBrand, String payMode,String dashboard_value,String status)
    {
        log.error("Entering getMonthlyTransactionDetailsForPartnerNew method....");
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        JSONObject obj = new JSONObject();
        StringBuffer jsonBuffer = new StringBuffer();
        String query2="";
        String lcurrency = "";
        String finalValue ="";

        String tableName = "transaction_common";

        if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName = "transaction_card_present";
        try
        {
            connection= Database.getRDBConnection();
            DateVO lastweek= dateManager.getDateRangeNew("Last seven days");
            String lastweekstartdtstmp= functions.getLastWeekStartdtStamp(lastweek);
            String lastweekenddtstmp= functions.getLastWeekEnddtStamp(lastweek);
            if (functions.isValueNull(dashboard_value) || functions.isValueNull(dateVO.getDateLabel()) && "today".equalsIgnoreCase(dateVO.getDateLabel()) || "Current month".equalsIgnoreCase(dateVO.getDateLabel()) || "Last month".equalsIgnoreCase(dateVO.getDateLabel()) || "Last seven days".equalsIgnoreCase(dateVO.getDateLabel()))
            {
                query2= "SELECT DATE(from_unixtime(dtstamp)) as lDate,";
            }
            else if(functions.isValueNull(dateVO.getDateLabel()) && "Last six months".equalsIgnoreCase(dateVO.getDateLabel()))
            {
                query2= "SELECT  DATE_FORMAT(from_unixtime(dtstamp),'%Y-%m') as lDate,";
            }
            else
            {
                query2= "SELECT DATE(from_unixtime(dtstamp)) as lDate,";
            }
            if (functions.isValueNull(dashboard_value) || (functions.isValueNull(dateVO.getDateLabel()) || "today".equalsIgnoreCase(dateVO.getDateLabel()) || "Current month".equalsIgnoreCase(dateVO.getDateLabel()) || "Last month".equalsIgnoreCase(dateVO.getDateLabel()) || "Last seven days".equalsIgnoreCase(dateVO.getDateLabel()) || "Last six months".equalsIgnoreCase(dateVO.getDateLabel())) && !functions.isValueNull(status))
            {
                 query2 = query2+" (UPPER(currency)) AS currencyName, SUM(amount) AS transamount FROM "+tableName+" AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid";
                 query2= query2+ " WHERE tc.Status IN ('markedforreversal' ,'chargeback','capturesuccess' ,'settled','reversed','authsuccessful') AND  totype=?";
                if (functions.isValueNull(dashboard_value))
                {
                    query2= query2+" AND dtstamp between '"+lastweekstartdtstmp+" ' AND '"+lastweekenddtstmp+"'";
                }
                else
                {
                    String datestartdtstmp= functions.getStartDtStamp(dateVO);
                    String dateenddtstmp= functions.getEndDtStamp(dateVO);
                    query2= query2+" AND dtstamp between '"+datestartdtstmp+" ' AND '"+dateenddtstmp+"'";
                }
            }
            else if (functions.isValueNull(status) && "sales".equalsIgnoreCase(status))
            {
                query2 = query2+" (UPPER(currency)) AS currencyName, SUM(amount) AS transamount FROM "+tableName+" AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid";
                query2= query2+ " WHERE tc.Status IN ('markedforreversal' ,'chargeback','capturesuccess' ,'settled','reversed','authsuccessful') AND  totype=?";
                if (functions.isValueNull(dateVO.getDateLabel()))
                {
                    String datestartdtstmp= functions.getStartDtStamp(dateVO);
                    String dateenddtstmp= functions.getEndDtStamp(dateVO);
                    query2= query2+" AND dtstamp between '"+datestartdtstmp+" ' AND '"+dateenddtstmp+"'";
                }
                else
                {
                    query2= query2+" AND dtstamp between '"+lastweekstartdtstmp+" ' AND '"+lastweekenddtstmp+"'";
                }
            }
            else if (functions.isValueNull(status) && "payout".equalsIgnoreCase(status))
            {
                query2 = query2+" (UPPER(currency)) AS currencyName, SUM(payoutamount) AS payoutAmount FROM "+tableName+" AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid";
                query2= query2+ " WHERE tc.Status='payoutsuccessful' AND  totype=?";

                if (functions.isValueNull(dateVO.getDateLabel()))
                {
                    String datestartdtstmp= functions.getStartDtStamp(dateVO);
                    String dateenddtstmp= functions.getEndDtStamp(dateVO);
                    query2= query2+" AND dtstamp between '"+datestartdtstmp+" ' AND '"+dateenddtstmp+"'";
                }
                else
                {
                    query2= query2+" AND dtstamp between '"+lastweekstartdtstmp+" ' AND '"+lastweekenddtstmp+"'";
                }
            }
            else
            {
                query2 = query2 + " (UPPER(currency)) AS currencyName, SUM(CASE WHEN tc.Status  = 'authsuccessful' THEN amount ELSE 0 END ) AS Amount,SUM(CASE WHEN tc.Status IN ('markedforreversal' ,'chargeback','capturesuccess' ,'settled','reversed') THEN captureamount ELSE 0 END) AS Captureamount ";
                query2= query2+ " FROM " + tableName + " AS tc, payment_type AS pt , card_type AS ct  WHERE tc.paymodeid=pt.paymodeid AND ct.cardtypeid=tc.cardtypeid AND toid =? ";
                query2 = query2 + " AND dtstamp BETWEEN '" + lastweekstartdtstmp + "' AND '" + lastweekenddtstmp + "'";
            }

            if (functions.isValueNull(currency))
            {
                query2 = query2 + " AND currency = '" + currency + "'";
            }
            if (functions.isValueNull(payBrand))
            {
                query2 = query2 + " AND ct.cardtypeid = '" + payBrand + "'";
            }
            if (functions.isValueNull(payMode))
            {
                query2 = query2 + " AND pt.paymodeid = '" + payMode + "'";
            }
            if ((functions.isValueNull(dashboard_value)) || (functions.isValueNull(dateVO.getDateLabel()) && ("today".equalsIgnoreCase(dateVO.getDateLabel()) || "Last seven days".equalsIgnoreCase(dateVO.getDateLabel()) || "Current month".equalsIgnoreCase(dateVO.getDateLabel()) || "Last month".equalsIgnoreCase(dateVO.getDateLabel())))  && (functions.isValueNull(status)  || !functions.isValueNull(status)))
            {
                query2 = query2 + " GROUP BY currency,DATE(from_unixtime(dtstamp))";
            }
            else if ( (functions.isValueNull(dateVO.getDateLabel()) && ("Last six months".equalsIgnoreCase(dateVO.getDateLabel()))) && (functions.isValueNull(status) || !functions.isValueNull(status)))
            {
                query2 = query2 + " GROUP BY currency,MONTH (FROM_UNIXTIME(dtstamp))";
            }
            else if ((functions.isValueNull(status)))
            {
                query2 = query2 + " GROUP BY currency,DATE(from_unixtime(dtstamp))";
            }
            else
            {
                query2= query2 +" GROUP BY currency,DATE(from_unixtime(dtstamp))";
            }
            pstmt = connection.prepareStatement(query2.toString());
            pstmt.setString(1, partnerName);
            Date date5= new Date();
            log.error("start time for getMonthlyTransactionDetailsForPartnerNew #### "+date5.getTime());
            rs = pstmt.executeQuery();
            log.error("end time for getMonthlyTransactionDetailsForPartnerNew #### "+new Date().getTime());
            log.error("diff time for getMonthlyTransactionDetailsForPartnerNew #### "+(new Date().getTime()-date5.getTime()));
            log.error("query for getMonthlyTransactionDetailsForPartnerNew #### "+pstmt);

            while(rs.next())
            {
                Float amount = 0.0f;
                Float Payoutamount = 0.0f;
                if (functions.isValueNull(status) && "payout".equalsIgnoreCase(status))
                {
                    Payoutamount = rs.getFloat("payoutAmount");
                    obj.put(rs.getString("lDate") + "_" + rs.getString("currencyName"), Float.toString(Payoutamount));
                }
                else
                {
                    amount = rs.getFloat("transamount");
                    obj.put(rs.getString("lDate") + "_" + rs.getString("currencyName"), Float.toString(amount));
                }
            }
            log.error("JSONObject +++ "+obj);
            JSONObject jsonObject= new JSONObject(obj.toString());
            Iterator<String> keys= jsonObject.sortedKeys();

            while (keys.hasNext())
            {
                String key= keys.next();
                String label="";
                if (key.contains("_"))
                {
                    String sArray[]=key.split("_");
                    label= sArray[0];
                    lcurrency= sArray[1];
                }
                Month month= Month.of(Integer.parseInt(label.split("-")[1])) ;
                String monthname= month.toString().substring(0,3);
                String xlabels= monthname+"-"+label.split("-")[0];
                if ((functions.isValueNull(dashboard_value)) || "today".equalsIgnoreCase(dateVO.getDateLabel()) || "Last seven days".equalsIgnoreCase(dateVO.getDateLabel()) || "Current month".equalsIgnoreCase(dateVO.getDateLabel()) || "Last month".equalsIgnoreCase(dateVO.getDateLabel()) && (functions.isValueNull(status) || !functions.isValueNull(status)))
                {
                    finalValue = "'" + lcurrency + "'" + ":'" + jsonObject.getString(label + "_" + lcurrency) + "'";
                    jsonBuffer.append("{x:'" + label + "'," + finalValue + "},");
                }
                else if ("Last six months".equalsIgnoreCase(dateVO.getDateLabel()) && (functions.isValueNull(status) || !functions.isValueNull(status)) )
                {
                    finalValue = "'" + lcurrency + "'" + ":'" + jsonObject.getString(label + "_" + lcurrency) + "'";
                    jsonBuffer.append("{x:'" + xlabels + "'," + finalValue + "},");
                }
                else if (functions.isValueNull(status))
                {
                    finalValue = "'" + lcurrency + "'" + ":'" + jsonObject.getString(label + "_" + lcurrency) + "'";
                    jsonBuffer.append("{x:'" + label + "'," + finalValue + "},");
                }
            }
            if (jsonBuffer.length()==0)
            {
                finalValue = "'" + lcurrency + "'" + ":'" + " " + "'";
                jsonBuffer.append("{x:'" + " " + "'," + finalValue + "},");
            }
        }
        catch (Exception e)
        {
            log.error("Exception while getMonthlyTransactionDetailsForPartnerNew++ ",e);
        }
        return  jsonBuffer;
    }
    public String getValidValue(String Value)
    {
        if (Value == null)
        {
            return "0.00";
        }
        else
        {
            return Value;
        }
    }

    public PartnerDetailsVO getPartnerDetailsByLogin(String login) throws PZDBViolationException
    {


        // MerchantDetailsVO merchantDetailsVO= new MerchantDetailsVO();
        PartnerDetailsVO partnerDetailsVO=new PartnerDetailsVO();
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            log.debug("Entering get Partner Details");

            transactionLogger.debug("PartnerDetails.GetPartnerDetails getting partner from  ############  DB Call");
            //String query = "select * from members where memberid =?";
            //String query = "SELECT m.*,p.partnerName,p.logoName,p.partnerId FROM members AS m,partners AS p WHERE m.login=? AND m.partnerId=p.partnerId";
            String query = "SELECT * FROM partners WHERE login=?";

            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,login);
            rs = pstmt.executeQuery();
            log.debug(" partner Details is loading ");
            if (rs.next())
            {

                partnerDetailsVO = new PartnerDetailsVO();
                partnerDetailsVO.setPartnerName(rs.getString("login"));
                partnerDetailsVO.setPartnerId(rs.getString("partnerId"));
                partnerDetailsVO.setCompanyName(rs.getString("partnerName"));
                partnerDetailsVO.setContactPerson(rs.getString("contact_persons"));
                partnerDetailsVO.setLogoName(rs.getString("logoName"));
                partnerDetailsVO.setPartnerKey(rs.getString("clkey"));
                partnerDetailsVO.setIsTokenizationAllowed(rs.getString("isTokenizationAllowed"));
                partnerDetailsVO.setIsMerchantRequiredForCardRegistration(rs.getString("isMerchantRequiredForCardRegistration"));
                partnerDetailsVO.setIsAddressRequiredForTokenTransaction(rs.getString("isAddressRequiredForTokenTransaction"));
                partnerDetailsVO.setIsMerchantRequiredForCardholderRegistration(rs.getString("isMerchantRequiredForCardholderRegistration"));
                partnerDetailsVO.setIsCardEncryptionEnable(rs.getString("isCardEncryptionEnable"));
                partnerDetailsVO.setResponseType(rs.getString("responseType"));
                partnerDetailsVO.setResponseLength(rs.getString("responseLength"));
                partnerDetailsVO.setHostUrl(rs.getString("hosturl"));
                partnerDetailsVO.setAddress(rs.getString("address"));
                partnerDetailsVO.setCity(rs.getString("city"));
                partnerDetailsVO.setState(rs.getString("state"));
                partnerDetailsVO.setZip(rs.getString("zip"));
                partnerDetailsVO.setTelno(rs.getString("telno"));
                partnerDetailsVO.setFaxNo(rs.getString("faxno"));
                partnerDetailsVO.setReportingCurrency(rs.getString("reporting_currency"));
                partnerDetailsVO.setIpWhitelistInvoice(rs.getString("ip_whitelist_invoice"));
                partnerDetailsVO.setAddressValidationInvoice(rs.getString("address_validation_invoice"));
                partnerDetailsVO.setIsIpWhiteListedCheckForAPIs(rs.getString("is_rest_whitelisted"));
                partnerDetailsVO.setAddressvalidation(rs.getString("addressvalidation"));
                partnerDetailsVO.setReportFileBGColor(rs.getString("reportfile_bgcolor"));
                partnerDetailsVO.setSupportMailId(rs.getString("companysupportmailid"));
                partnerDetailsVO.setProcessorPartnerId(rs.getString("processor_partnerid"));
                partnerDetailsVO.setMonthlyMinCommissionModule(rs.getString("monthly_min_commission_module"));
                partnerDetailsVO.setPartnerShortName(rs.getString("partner_short_name"));
                partnerDetailsVO.setProfitShareCommissionModule(rs.getString("profit_share_commission_module"));

            }

            log.debug("partner Details loaded");
        }
        catch(SystemError systemError)
        {
            log.error("Leaving partner throwing System Exception as System Error :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(),"getPartnerDetails()",null,"Common","System Error while connecting to the partner table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("Leaving partner throwing SQL Exception as System Error :::: ",e);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(),"getPartnerDetails()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        log.debug("Leaving getPartnerDetails");

        return partnerDetailsVO;
    }

    public boolean authenticatePartnerViaKey(String loginName,String key, String partnerid) throws PZDBViolationException
    {
        Connection con = null;
        ResultSet resultSet =null;
        PreparedStatement preparedStatement = null;
        try
        {
            con =Database.getRDBConnection();
            String query="Select login,clkey from partners where login=? and BINARY clkey=? and partnerid=?" ;

            preparedStatement=con.prepareStatement(query);

            preparedStatement.setString(1,loginName);
            preparedStatement.setString(2,key);
            preparedStatement.setString(3,partnerid);

            resultSet=preparedStatement.executeQuery();
            log.debug("clkey query----"+preparedStatement);

            if(resultSet.next())
            {
                return true;
            }
        }
        catch (SystemError systemError)
        {
            log.error("Leaving Partners throwing System Exception as System Error :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "authenticateMemberViaKey()", null, "Common", "System Error while connecting to the partners table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("Leaving Partners throwing System Exception as System Error :::: ",e);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "authenticateMemberViaKey()", null, "Common", "SQLException while connecting to the partners table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }

        return false;
    }

    public int getPartnerId(String loginName) throws PZDBViolationException
    {
        Connection con = null;
        ResultSet resultSet =null;
        PreparedStatement preparedStatement = null;
        int id=0;
        try
        {
            con =Database.getRDBConnection();
            String query="Select login,clkey,partnerid from partners where login=?" ;

            preparedStatement=con.prepareStatement(query);

            preparedStatement.setString(1,loginName);
            resultSet=preparedStatement.executeQuery();
            log.debug("partnerId query----"+preparedStatement);

            if(resultSet.next())
            {
                id=resultSet.getInt("partnerId");
            }
        }
        catch (SystemError systemError)
        {
            log.error("Leaving Partners throwing System Exception as System Error :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "getPartnerId()", null, "Common", "System Error while connecting to the partners table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("Leaving Partners throwing System Exception as System Error :::: ",e);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "getPartnerId()", null, "Common", "SQLException while connecting to the partners table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }

        return id;
    }

    public HashMap getReport(String toid,String partnerId,String fdtstamp,String tdtstamp, String currency,String accountid) throws SystemError
    {
        Connection conn = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        //Functions partner=new PartnerFunctions();
        conn = Database.getRDBConnection();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        HashMap statusreport= new HashMap();
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        int counter = 1;
        int total = 0;
        String totalamount="";

        try
        {
            //common
            query.append("select STATUS,COUNT(trackingid) AS COUNT,SUM(amount) AS amount from transaction_common where ");
            if (functions.isValueNull(toid))
            {
                query.append(" toid = ?");
            }
            if (functions.isValueNull(accountid))
            {
                query.append(" AND  accountid = ?");
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" AND dtstamp>= ?");
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" AND dtstamp<= ?");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" AND currency IN ( ? )");
            }
            /*else

            {
                query.append(" AND currency IN ( ? ) ");
            }*/
            //query.append(" AND currency= '" + ESAPI.encoder().encodeForSQL(me, currency) + "'");
            query.append(" group by status ");

            StringBuilder reportquery = new StringBuilder("select status, SUM(count) as count,SUM(amount) as amount from ( ");
            reportquery.append(query);
            reportquery.append(" )as temp group by status");

            StringBuilder countquery = new StringBuilder("select SUM(count) as count,SUM(amount) as grandtotal from ( ");
            countquery.append(query);
            countquery.append(" ) as temp");

            log.debug("query volume::::"+reportquery.toString());
            // Date date1 = new Date();
            log.debug("reportquery:::::"+reportquery.toString());
            log.debug("countquery:::::"+countquery.toString());

            pstmt = conn.prepareStatement(reportquery.toString());
            pstmt1 = conn.prepareStatement(countquery.toString());

            if (functions.isValueNull(toid))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, toid));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, toid));
                counter++;
            }
            if (functions.isValueNull(accountid))
            {
                pstmt.setString(counter,ESAPI.encoder().encodeForSQL(me,accountid));
                pstmt1.setString(counter,ESAPI.encoder().encodeForSQL(me,accountid));
                counter++;
            }
            if (functions.isValueNull(fdtstamp))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me,fdtstamp));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, fdtstamp));
                counter++;
            }
            if (functions.isValueNull(tdtstamp))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, tdtstamp));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, tdtstamp));
                counter++;
            }
            /*if (partner.isEmptyOrNull(currency))
            {
                String allcurrency = partner.allCurrency(toid);
                log.debug("allcurrency::::::"+allcurrency);
               // pstmt.setString(counter, allcurrency);
                pstmt.setInt(counter, Integer.valueOf(allcurrency));
                pstmt1.setInt(counter, Integer.valueOf(allcurrency));
                counter++;
            }*/
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, currency));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, currency));
                counter++;
            }

            log.debug("pstmt::::"+pstmt);
            log.debug("pstmt1::::"+pstmt1);

            if (isPartnerMemberMapped(toid, partnerId))
            {
                //statusreport=Database.getHashFromResultSet(pstmt.executeQuery());
                statusreport=Database.getHashMapFromResultSetForTransactionEntry(pstmt.executeQuery());
                //rs = Database.executeQuery(countquery.toString(), conn);
                rs = pstmt1.executeQuery();
                if (rs.next())
                {
                    total = rs.getInt("count");
                    totalamount=rs.getString("grandtotal");
                }
            }
            if(totalamount!=null)
            {
                statusreport.put("grandtotal",totalamount);
                statusreport.put("totalrecords", "" + total);
            }
            else
            {
                statusreport.put("grandtotal","00");
                statusreport.put("totalrecords", "00");
            }
            statusreport.put("records", "0");

            if (total > 0)
            {
                statusreport.put("records", "" + (statusreport.size() - 2));
            }
        }
        catch (SQLException e)
        {
            log.error("sql error while execute status report",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(conn);
        }
        return statusreport;

    }

    public HashMap getReportpercentage(String toid,String partnerId,String fdtstamp,String tdtstamp, String currency,String accountid) throws SystemError
    {
        Connection conn = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        //Functions partner=new PartnerFunctions();
        conn = Database.getRDBConnection();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        HashMap statusreport= new HashMap();
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        int counter = 1;
        int total = 0;
        String totalamount="";

        try
        {
            //common
            query.append("select STATUS,COUNT(trackingid) AS COUNT,SUM(amount) AS amount from transaction_common where STATUS NOT IN ('begun','authstarted','failed','cancelled','payoutstarted','payoutsuccessful','payoutfailed')  AND  ");
            if (functions.isValueNull(toid))
            {
                query.append(" toid = ?");
            }
            if (functions.isValueNull(accountid))
            {
                query.append(" AND accountid = ?");
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" AND dtstamp>= ?");
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" AND dtstamp<= ?");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" AND currency IN ( ? )");
            }
            /*else

            {
                query.append(" AND currency IN ( ? ) ");
            }*/
            //query.append(" AND currency= '" + ESAPI.encoder().encodeForSQL(me, currency) + "'");
            query.append(" group by status ");

            StringBuilder reportquery = new StringBuilder("select status, SUM(count) as count,SUM(amount) as amount from ( ");
            reportquery.append(query);
            reportquery.append(" )as temp group by status");

            StringBuilder countquery = new StringBuilder("select SUM(count) as count,SUM(amount) as grandtotal from ( ");
            countquery.append(query);
            countquery.append(" ) as temp");

            log.debug("query volume::::"+reportquery.toString());
            // Date date1 = new Date();
            log.debug("reportquery:::::"+reportquery.toString());
            log.debug("countquery:::::"+countquery.toString());

            pstmt = conn.prepareStatement(reportquery.toString());
            pstmt1 = conn.prepareStatement(countquery.toString());

            if (functions.isValueNull(toid))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, toid));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, toid));
                counter++;
            }
            if (functions.isValueNull(accountid))
            {
                pstmt.setString(counter,ESAPI.encoder().encodeForSQL(me,accountid));
                pstmt1.setString(counter,ESAPI.encoder().encodeForSQL(me,accountid));
                counter++;
            }
            if (functions.isValueNull(fdtstamp))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me,fdtstamp));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, fdtstamp));
                counter++;
            }
            if (functions.isValueNull(tdtstamp))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, tdtstamp));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, tdtstamp));
                counter++;
            }
            /*if (partner.isEmptyOrNull(currency))
            {
                String allcurrency = partner.allCurrency(toid);
                log.debug("allcurrency::::::"+allcurrency);
               // pstmt.setString(counter, allcurrency);
                pstmt.setInt(counter, Integer.valueOf(allcurrency));
                pstmt1.setInt(counter, Integer.valueOf(allcurrency));
                counter++;
            }*/
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, currency));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, currency));
                counter++;
            }

            log.debug("pstmt::::"+pstmt);
            log.debug("pstmt1::::"+pstmt1);

            if (isPartnerMemberMapped(toid, partnerId))
            {
                //statusreport=Database.getHashFromResultSet(pstmt.executeQuery());
                statusreport=Database.getHashMapFromResultSetForTransactionEntry(pstmt.executeQuery());
                //rs = Database.executeQuery(countquery.toString(), conn);
                rs = pstmt1.executeQuery();
                if (rs.next())
                {
                    total = rs.getInt("count");
                    totalamount=rs.getString("grandtotal");
                }
            }
            if(totalamount!=null)
            {
                statusreport.put("grandtotal",totalamount);
                statusreport.put("totalrecords", "" + total);
            }
            else
            {
                statusreport.put("grandtotal","00");
                statusreport.put("totalrecords", "00");
            }
            statusreport.put("records", "0");

            if (total > 0)
            {
                statusreport.put("records", "" + (statusreport.size() - 2));
            }
        }
        catch (SQLException e)
        {
            log.error("sql error while execute status report",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(conn);
        }
        return statusreport;

    }


    //MODIFIED THE QUERY TO CHECK MERCHANT IS MAPPED WITH PARTNER OR SUPERPARNER.
    public boolean isPartnerMemberMapped(String memberid, String partnerid) throws SystemError
    {
        Connection conn = null;
        PreparedStatement pstmt= null;
        ResultSet rs = null;
        String status = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            log.debug("check isMember method");
            String selquery = "SELECT 'X' FROM members m, partners p WHERE p.partnerId = m.partnerId AND m.memberid = ? AND (p.superadminid =? OR m.partnerId=?)";

            pstmt= conn.prepareStatement(selquery);
            pstmt.setString(1,memberid);
            pstmt.setString(2,partnerid);
            pstmt.setString(3, partnerid);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                status = rs.getString(1) ;
                if(status.equals("X")){
                    return true;
                }
            }

        }
        catch (SystemError se)
        {
            log.error(" SystemError in isMemberUser method: ",se);

            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            log.error("Exception in isMemberUser method: ",e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return false;
    }

    /*public boolean isPartnerMemberMapped(String memberid, String partnerid) throws SystemError
    {
        Connection conn = null;
        PreparedStatement pstmt= null;
        ResultSet rs = null;
        String mappedPartnerId = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            log.debug("check isMember method");
            String selquery = "SELECT partnerid FROM members WHERE memberid = ? ";

            pstmt= conn.prepareStatement(selquery);
            pstmt.setString(1,memberid);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                mappedPartnerId = rs.getString("partnerid");
            }

        }
        catch (SystemError se)
        {
            log.error(" SystemError in isMemberUser method: ",se);

            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            log.error("Exception in isMemberUser method: ",e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        if(functions.isValueNull(mappedPartnerId)&& mappedPartnerId.equals(partnerid))
        {
            return true;
        }
        return false;
    }*/
    public int getRefundListCountNew(String description,String accountid,String trakingid,String memberId, String partnerId,int records,int pageno) throws SystemError
    {
        log.error("Entering into getRefundListCountNew method..");
        int totalrecords= 0;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuilder query = new StringBuilder();

        String tablename="transaction_common";
        query.append("select T.toid,T.status,T.trackingid as trackingid,T.transid,T.description,T.dtstamp,T.amount,T.captureamount,T.refundamount,T.currency,T.paymodeid,T.accountid from ");
        query.append(tablename);
        query.append(" as T where trackingid>1");
        if (description != null && !description.equalsIgnoreCase(""))
        {
            query.append(" and description like '%");
            query.append(description);
            query.append("%'");
        }
        if (accountid!=null && !accountid.equals("") && !accountid.equals("null"))
        {
            query.append(" and T.accountid =");
            query.append(accountid);
        }
        if (memberId!=null && !memberId.equals("") && !memberId.equals("null"))
        {
            query.append(" and toid IN(").append(memberId).append(")");
        }

        if (trakingid != null && !trakingid.equalsIgnoreCase(""))
        {
            if(tablename.equals("transaction_icicicredit"))
            {
                query.append(" and icicitransid=").append(ESAPI.encoder().encodeForSQL(me,trakingid));
            }
            else
            {
                query.append(" and trackingid=").append(ESAPI.encoder().encodeForSQL(me,trakingid));
            }

        }
        if(tablename.equals("transaction_qwipi"))
        {
            query.append(" and status IN ('settled','capturesuccess','partialrefund') ");
        }
        else if(tablename.equals("transaction_ecore"))
        {
            query.append(" and status IN ('settled','capturesuccess','partialrefund') ");
        }
        else
        {
            query.append(" and status IN ('settled','capturesuccess','reversed') and T.captureamount>T.refundamount");
        }
        StringBuilder countquery = new StringBuilder("select count(*) from ( ").append(query.toString()).append(") as temp ");
        log.debug("count query::::"+countquery.toString());
        Connection cn = null;
        ResultSet rs = null;
        try
        {
            if (isPartnerMemberMapped(memberId, partnerId))
            {
                cn= Database.getRDBConnection();
                log.debug("calling database to fetch count of transaction details for reverse");

                Date date5= new Date();
                log.error("getRefundListcountNew starts countquery###########  "+date5.getTime());
                rs = Database.executeQuery(countquery.toString(), cn);
                log.error("getRefundListcountNew ends countquery###########  "+new Date().getTime());
                log.error("getRefundListcountNew diff countquery###########  "+(new Date().getTime()-date5.getTime()));

                rs.next();
                totalrecords = rs.getInt(1);
                log.error("getRefundListcountNew countquery######  "+countquery.toString()+" "+totalrecords);
            }
        }
        catch (SystemError se)
        {
            log.error("System error",se);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (Exception e)
        {
            log.error("Exception Occur while fetching refund record", e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(cn);
        }
        log.error("Leaving getRefundListcountNew...");
        return totalrecords;
    }

    public HashMap getPartnerMerchantRefundList(String description,String accountid,String trakingid,String memberId, String partnerId,int records,int pageno,int totalrecords) throws SystemError
    {
        HashMap hash = new HashMap();

        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;
        if (totalrecords>records)
        {
            end= totalrecords-start;
            if (end >records)
            {
                end= records;
            }
        }
        else
        {
            end= totalrecords;
        }
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuilder query = new StringBuilder();

        String tablename="transaction_common";
        query.append("select T.toid,T.status,T.trackingid as trackingid,T.transid,T.description,T.dtstamp,T.amount,T.captureamount,T.refundamount,T.currency,T.paymodeid,T.accountid from ");
        query.append(tablename);
        query.append(" as T where trackingid>1");

        if (description != null && !description.equalsIgnoreCase(""))
        {
            //query.append(" and description like '% ").append(ESAPI.encoder().encodeForSQL(me,description)).append(" %'");
            query.append(" and description like '%");
            query.append(description);
            query.append("%'");

        }
        if (accountid!=null && !accountid.equals("") && !accountid.equals("null"))
        {
            query.append(" and T.accountid =");
            query.append(accountid);
        }
        if (memberId!=null && !memberId.equals("") && !memberId.equals("null"))
        {
            query.append(" and toid IN(").append(memberId).append(")");

        }

        if (trakingid != null && !trakingid.equalsIgnoreCase(""))
        {
            if(tablename.equals("transaction_icicicredit"))
            {
                query.append(" and icicitransid=").append(ESAPI.encoder().encodeForSQL(me,trakingid));
            }
            else
            {
                query.append(" and trackingid=").append(ESAPI.encoder().encodeForSQL(me,trakingid));
            }

        }
        if(tablename.equals("transaction_qwipi"))
        {
            query.append(" and status IN ('settled','capturesuccess','partialrefund') ");
        }
        else if(tablename.equals("transaction_ecore"))
        {
            query.append(" and status IN ('settled','capturesuccess','partialrefund') ");
        }
        else
        {
            query.append(" and status IN ('settled','capturesuccess','reversed') and T.captureamount>T.refundamount");
        }
        StringBuilder countquery = new StringBuilder("select count(*) from ( ").append(query.toString()).append(") as temp ");
        query.append(" order by trackingid desc LIMIT ").append(start).append(",").append(end);
        log.debug(" query =="+query.toString());
        log.debug("count query::::"+countquery.toString());
        Connection cn = null;
        ResultSet rs = null;
        try
        {
            if (isPartnerMemberMapped(memberId, partnerId))
            {
                //cn = Database.getConnection();
                cn = Database.getRDBConnection();
                log.debug("calling database to fetch transaction details for reverse");
                Date date4 = new Date();
                log.error("getPartnerMerchantRefundList starts query###########  "+date4.getTime());
                hash = Database.getHashMapFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), cn));
                log.error("getPartnerMerchantRefundList ends query###########  "+new Date().getTime());
                log.error("getPartnerMerchantRefundList diff query###########  "+(new Date().getTime()- date4.getTime()));
                log.error("getPartnerMerchantRefundList Query##########  "+query.toString());

                log.debug("calling database to fetch count of transaction details for reverse");

                hash.put("totalrecords", "" + totalrecords);
                hash.put("records", "0");

                if (totalrecords > 0)
                {
                    hash.put("records", "" + (hash.size() - 2));
                }
            }
        }
        catch (SystemError se)
        {
            log.error("System error",se);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (Exception e)
        {
            log.error("Exception Occur while fetching refund record", e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(cn);
        }

        return hash;
    }

    /*public HashMap getPartnerMerchantfraudSettingList(String memberId, String partnerId)
    {
        HashMap hash = new HashMap();

        StringBuffer query = new StringBuffer("select memberid,maxScoreAllowed,maxScoreAutoReversal,onlineFraudCheck,internalFraudCheck from members where memberid=? and partnerid=?");
        StringBuffer countQuery = new StringBuffer("select count(*) from members where memberid=? and partnerid=?");

        Connection con = null;
        PreparedStatement p = null,p1 = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            if (functions.isValueNull(memberId) && functions.isValueNull(partnerId))
            {
                p = con.prepareStatement(query.toString());
                p.setString(1, memberId);
                p.setString(2, partnerId);
                log.debug("query inside fraud Memberssa table inside dao::::" + p);

                hash = Database.getHashMapFromResultSet(p.executeQuery());
                p1 = con.prepareStatement(countQuery.toString());
                p1.setString(1, memberId);
                p1.setString(2, partnerId);
                log.debug("count query inside fraud Members table inside dao::::" + p1);
                rs = p1.executeQuery();
                rs.next();
                int totalrecords = rs.getInt(1);

                hash.put("totalrecords", "" + totalrecords);
                hash.put("records", "0");
                if (totalrecords > 0)
                {
                    hash.put("records", "" + (hash.size() - 2));
                }
            }
            else
            {
                hash = new HashMap();
                hash.put("records", "0");
                hash.put("totalrecords", "0");
            }
        }

        catch (SystemError se){
            log.error("System Error::::",se);
        }
        catch (Exception e){
            log.error("Exception::::",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closePreparedStatement(p1);
            Database.closeConnection(con);
        }

        return hash;
    }*/

    //MODIFICATION IN QUERY AS PER SUPERPARTNER ACCESS.
    public HashMap getPartnerMerchantfraudSettingList(String memberId, String partnerId)
    {
        HashMap hash = new HashMap();
        //
        StringBuffer query = new StringBuffer("select m.memberid,m.maxScoreAllowed,m.maxScoreAutoReversal,m.onlineFraudCheck,m.internalFraudCheck from members m , partners p where  m.partnerId = p.partnerId and  m.memberid=? and (m.partnerid=? or p.superadminid=?) ");
        StringBuffer countQuery = new StringBuffer("select count(*) from members m , partners p where m.partnerId = p.partnerId and  m.memberid=? and (m.partnerId=? or p.superadminid=?)");

        Connection con = null;
        PreparedStatement p = null,p1 = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            if (functions.isValueNull(memberId) && functions.isValueNull(partnerId))
            {
                p = con.prepareStatement(query.toString());
                p.setString(1, memberId);
                p.setString(2, partnerId);
                p.setString(3, partnerId);
                log.debug("query inside fraud Memberssa table inside dao::::" + p);
                hash = Database.getHashMapFromResultSet(p.executeQuery());
                p1 = con.prepareStatement(countQuery.toString());
                p1.setString(1, memberId);
                p1.setString(2, partnerId);
                p1.setString(3, partnerId);
                log.debug("count query inside fraud Members table inside dao::::" + p1);
                rs = p1.executeQuery();
                rs.next();
                int totalrecords = rs.getInt(1);

                hash.put("totalrecords", "" + totalrecords);
                hash.put("records", "0");
                if (totalrecords > 0)
                {
                    hash.put("records", "" + (hash.size() - 2));
                }
            }
            else
            {
                hash = new HashMap();
                hash.put("records", "0");
                hash.put("totalrecords", "0");
            }
        }

        catch (SystemError se){
            log.error("System Error::::",se);
        }
        catch (Exception e){
            log.error("Exception::::",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closePreparedStatement(p1);
            Database.closeConnection(con);
        }

        return hash;
    }

    /*public HashMap getPartnerBackOfficeAccessList(String memberId, String partnerid, String month, String year)
    {
        HashMap hash = new HashMap();
        StringBuffer query =new StringBuffer("SELECT company_name,m.memberid,icici,isappmanageractivate,dashboard_access,accounting_access,setting_access,transactions_access,invoicing_access,virtualterminal_access,merchantmgt_access,settings_fraudrule_config_access,settings_merchant_config_access,iscardregistrationallowed,is_recurring,accounts_account_summary_access,accounts_charges_summary_access,accounts_transaction_summary_access,accounts_reports_summary_access,settings_merchant_profile_access,settings_organisation_profile_access, settings_checkout_page_access,settings_generate_key_access,settings_invoice_config_access,transmgt_transaction_access,transmgt_capture_access,transmgt_reversal_access,transmgt_payout_access,invoice_generate_access,invoice_history_access,tokenmgt_registration_history_access,tokenmgt_register_card_access,settings_whitelist_details,settings_blacklist_details,mc.rejected_transaction FROM members AS m JOIN merchant_configuration AS mc ON m.`memberid`=mc.`memberid` WHERE m.memberid=? and m.partnerid=?");
        StringBuffer countquery = new StringBuffer("select count(*) from members where memberid=? and partnerid=?");
        Connection con = null;
        PreparedStatement p=null,p1=null;
        ResultSet rs=null;
        try
        {
            Functions f = new Functions();
            con = Database.getRDBConnection();
            if (f.isValueNull(memberId) && f.isValueNull(partnerid))
            {
                p=con.prepareStatement(query.toString());
                p.setString(1,memberId);
                p.setString(2,partnerid);
                log.debug("query inside BackOfficeAccess:::::"+p);

                hash = Database.getHashMapFromResultSet(p.executeQuery());
                p1=con.prepareStatement(countquery.toString());
                p1.setString(1,memberId);
                p1.setString(2,partnerid);
                log.debug("count query inside BackOfficeAccess:::::"+p1);
                rs = p1.executeQuery();
                rs.next();
                int totalrecords = rs.getInt(1);

                hash.put("totalrecords", "" + totalrecords);
                hash.put("records", "0");
                if (totalrecords > 0)
                {
                    hash.put("records", "" + (hash.size() - 2));
                }
            }
            else
            {
                hash = new HashMap();
                hash.put("records", "0");
                hash.put("totalrecords", "0");
            }

            hash.put("month", "" + month);
            hash.put("year", "" + year);
        }
        catch (SystemError se)
        {
            log.error("System Error::::",se);
        }
        catch (Exception e)
        {
            log.error("Exception::::",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closePreparedStatement(p1);
            Database.closeConnection(con);
        }
        return hash;
    }*/

    //Modification in query as per superpartner access.
    public HashMap getPartnerBackOfficeAccessList(String memberId, String partnerid, String month, String year)
    {
        HashMap hash = new HashMap();
        StringBuffer query =new StringBuffer("SELECT company_name,m.memberid,m.icici,isappmanageractivate,dashboard_access,accounting_access,setting_access,transactions_access,invoicing_access,virtualterminal_access,merchantmgt_access,settings_fraudrule_config_access,settings_merchant_config_access,iscardregistrationallowed,is_recurring,accounts_account_summary_access,accounts_charges_summary_access,accounts_transaction_summary_access,accounts_reports_summary_access,settings_merchant_profile_access,settings_organisation_profile_access, settings_checkout_page_access,settings_generate_key_access,settings_invoice_config_access,transmgt_transaction_access,transmgt_capture_access,transmgt_reversal_access,transmgt_payout_access,invoice_generate_access,invoice_history_access,tokenmgt_registration_history_access,tokenmgt_register_card_access,settings_whitelist_details,settings_blacklist_details,mc.rejected_transaction,mc.virtual_checkout,mc.paybylink,mc.merchant_verify_otp,mc.generateview FROM members AS m JOIN merchant_configuration AS mc ON m.`memberid`=mc.`memberid` JOIN partners p ON m.partnerId= p.partnerId WHERE m.memberid=? AND (m.partnerid=? OR p.superadminid=?)");
        StringBuffer countquery = new StringBuffer("select count(*) from members m JOIN partners p ON m.partnerId= p.partnerId WHERE m.memberid=? AND (m.partnerid=? OR p.superadminid=?)");
        Connection con = null;
        PreparedStatement p=null,p1=null;
        ResultSet rs=null;
        try
        {
            Functions f = new Functions();
            con = Database.getRDBConnection();
            if (f.isValueNull(memberId) && f.isValueNull(partnerid))
            {
                p=con.prepareStatement(query.toString());
                p.setString(1,memberId);
                p.setString(2,partnerid);
                p.setString(3,partnerid);
                log.debug("query inside BackOfficeAccess:::::"+p);

                hash = Database.getHashMapFromResultSet(p.executeQuery());
                p1=con.prepareStatement(countquery.toString());
                p1.setString(1,memberId);
                p1.setString(2,partnerid);
                p1.setString(3,partnerid);
                log.debug("count query inside BackOfficeAccess:::::"+p1);
                rs = p1.executeQuery();
                rs.next();
                int totalrecords = rs.getInt(1);

                hash.put("totalrecords", "" + totalrecords);
                hash.put("records", "0");
                if (totalrecords > 0)
                {
                    hash.put("records", "" + (hash.size() - 2));
                }
            }
            else
            {
                hash = new HashMap();
                hash.put("records", "0");
                hash.put("totalrecords", "0");
            }

            hash.put("month", "" + month);
            hash.put("year", "" + year);
        }
        catch (SystemError se)
        {
            log.error("System Error::::",se);
        }
        catch (Exception e)
        {
            log.error("Exception::::",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closePreparedStatement(p1);
            Database.closeConnection(con);
        }
        return hash;
    }

    /*public HashMap getBlockedMerchantList(String partnerid, int pageno, int records)
    {
        HashMap merchanthash = null;
        int start = (pageno-1)*records;
        int end = records;
        StringBuilder query = new StringBuilder("SELECT m.memberid,m.login,m.contact_emails FROM members AS m ,user AS u WHERE u.unblocked='locked' AND u.faillogincount>='3' AND m.login=u.login AND m.partnerid = ? ORDER BY m.memberid ASC LIMIT ? , ?");
        StringBuilder countquery = new StringBuilder("SELECT COUNT(*) FROM members AS m ,user AS u WHERE u.unblocked='locked' AND u.faillogincount>='3' AND m.login=u.login AND m.partnerid = ? ");

        Connection cn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs=null;
        int counter = 1;
        try
        {
            cn = Database.getRDBConnection();
            pstmt = cn.prepareStatement(query.toString());
            pstmt1 = cn.prepareStatement(countquery.toString());
            if(functions.isValueNull(partnerid))
            {
                pstmt.setString(counter, partnerid);
                pstmt1.setString(counter, partnerid);
                counter++;
            }
            pstmt.setInt(counter, start);
            counter++;
            pstmt.setInt(counter, end);

            log.debug("pstmt inside getBlockedMerchantList:::::::"+pstmt);
            merchanthash = Database.getHashMapFromResultSet(pstmt.executeQuery());

            rs = pstmt1.executeQuery();
            rs.next();
            int totalemailrecords = rs.getInt(1);

            merchanthash.put("totalrecords", "" + totalemailrecords);
            merchanthash.put("records", "0");

            if (totalemailrecords > 0)
            {
                merchanthash.put("records", "" + (merchanthash.size() - 2));
            }
        }
        catch (SystemError se)
        {   log.error("SystemError in BlockedEmailList::::",se);

        }
        catch (Exception e)
        {
            log.error("Exception in BlockedEmailList",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(cn);
        }
        return merchanthash;
    }*/

    //MODIFICATION IN QUERY FOR SUPERPARTNER ACCESS.
    public HashMap getBlockedMerchantList(String partnerid, int pageno, int records)
    {
        HashMap merchanthash = null;
        int start = (pageno-1)*records;
        int end = records;
        StringBuilder query = new StringBuilder("SELECT m.memberid,m.login,m.contact_emails FROM members AS m ,user AS u, partners AS p WHERE u.unblocked='locked' AND u.faillogincount>='3' AND m.login=u.login AND m.partnerid=p.partnerid AND (m.partnerid = ? or p.superadminid=?) ORDER BY m.memberid ASC LIMIT ? , ?");
        StringBuilder countquery = new StringBuilder("SELECT COUNT(*) FROM members AS m ,user AS u,partners AS p WHERE u.unblocked='locked' AND u.faillogincount>='3' AND m.login=u.login AND m.partnerid = p.partnerid AND (m.partnerid = ? or p.superadminid=?)");

        Connection cn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs=null;
        int counter = 1;
        try
        {
            cn = Database.getRDBConnection();
            pstmt = cn.prepareStatement(query.toString());
            pstmt1 = cn.prepareStatement(countquery.toString());
            if(functions.isValueNull(partnerid))
            {
                pstmt.setString(counter, partnerid);
                pstmt1.setString(counter, partnerid);
                counter++;
                pstmt.setString(counter, partnerid);
                pstmt1.setString(counter, partnerid);
                counter++;
            }
            pstmt.setInt(counter, start);
            counter++;
            pstmt.setInt(counter, end);

            log.debug("pstmt inside getBlockedMerchantList:::::::" + pstmt);
            merchanthash = Database.getHashMapFromResultSet(pstmt.executeQuery());

            rs = pstmt1.executeQuery();
            rs.next();
            int totalemailrecords = rs.getInt(1);

            merchanthash.put("totalrecords", "" + totalemailrecords);
            merchanthash.put("records", "0");

            if (totalemailrecords > 0)
            {
                merchanthash.put("records", "" + (merchanthash.size() - 2));
            }
        }
        catch (SystemError se)
        {   log.error("SystemError in BlockedEmailList::::",se);

        }
        catch (Exception e)
        {
            log.error("Exception in BlockedEmailList",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(cn);
        }
        return merchanthash;
    }


    public boolean getUnBlockedAccount(String login)
    {
        boolean flag = false;
        StringBuffer query = new StringBuffer("update user set unblocked='unlocked' ,faillogincount='0' where unblocked='locked' and faillogincount>='3' and login=? and roles='merchant' OR roles='agent'");

        Connection con = null;
        PreparedStatement pstmt = null;
        try
        {
            con = Database.getConnection();
            pstmt= con.prepareStatement(query.toString());
            pstmt.setString(1,login);
            log.error("update query:::: "+pstmt);
            int i = pstmt.executeUpdate();
            if (i > 0)
            {
                flag = true;
            }
        }
        catch (SystemError se)
        {
            log.error("SystemError in UnblockEmail::::::",se);
        }
        catch (Exception e)
        {
            log.error("SystemError in UnblockEmail::::::",e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return flag;
    }
    //MODIFIED QUERY FOR SUPERPARTNER ACCESS.
    public HashMap getMerchantUserUnblockedAccountList(String partnerid, int pageno, int records)
    {
        HashMap merchanthash = null;
        int start = (pageno-1)*records;
        int end = records;
        StringBuilder query = new StringBuilder("SELECT u.login,u.roles,mu.userid,mu.contact_emails,m.memberid FROM user AS u LEFT JOIN member_users AS mu ON u.accountid = mu.accid LEFT JOIN members AS m ON mu.memberid = m.memberid JOIN partners p ON m.partnerid = p.partnerid WHERE unblocked = 'locked' AND faillogincount= '5' AND (m.partnerId = ? or p.superadminid=?) ORDER BY m.memberid ASC LIMIT ? , ? ");
        StringBuilder countquery = new StringBuilder("SELECT COUNT(*) FROM user AS u LEFT JOIN member_users AS mu ON u.accountid = mu.accid LEFT JOIN members AS m ON mu.memberid = m.memberid JOIN partners AS p ON p.partnerid=m.partnerid WHERE unblocked = 'locked' AND faillogincount= '5' AND (m.partnerId = ?  OR p.superadminid = ?)");
        Connection cn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        int counter = 1;
        try
        {
            cn = Database.getRDBConnection();
            pstmt = cn.prepareStatement(query.toString());
            pstmt1 = cn.prepareStatement(countquery.toString());
            if(functions.isValueNull(partnerid))
            {
                pstmt.setString(counter, partnerid);
                pstmt1.setString(counter, partnerid);
                counter++;
                pstmt.setString(counter, partnerid);
                pstmt1.setString(counter, partnerid);
                counter++;
            }
            pstmt.setInt(counter, start);
            counter++;
            pstmt.setInt(counter, end);

            log.debug("pstmt inside getMerchantUserUnblockedAccountList:::::::"+pstmt);
            merchanthash = Database.getHashMapFromResultSet(pstmt.executeQuery());

            rs = pstmt1.executeQuery();
            rs.next();
            int totalemailrecords = rs.getInt(1);

            merchanthash.put("totalrecords", "" + totalemailrecords);
            merchanthash.put("records", "0");

            if (totalemailrecords > 0)
            {
                merchanthash.put("records", "" + (merchanthash.size() - 2));
            }

        }
        catch (SystemError se)
        {
            log.error("SystemError in BlockedEmailList::::",se);
        }
        catch (Exception e)
        {
            log.error("Exception in BlockedEmailList",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(cn);
        }
        return merchanthash;
    }


    /*public HashMap getMerchantUserUnblockedAccountList(String partnerid, int pageno, int records)
    {
        HashMap merchanthash = null;
        int start = (pageno-1)*records;
        int end = records;
        StringBuilder query = new StringBuilder("SELECT u.login,u.roles,mu.userid,mu.contact_emails,m.memberid FROM user AS u LEFT JOIN member_users AS mu ON u.accountid = mu.accid LEFT JOIN members AS m ON mu.memberid = m.memberid WHERE unblocked = 'locked'AND faillogincount= '5' AND m.partnerId = ? ORDER BY m.memberid ASC LIMIT ? , ? ");
        StringBuilder countquery = new StringBuilder("SELECT COUNT(*) FROM user AS u LEFT JOIN member_users AS mu ON u.accountid = mu.accid LEFT JOIN members AS m ON mu.memberid = m.memberid WHERE unblocked = 'locked'AND faillogincount= '5' AND m.partnerId = ? ");
        Connection cn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        int counter = 1;
        try
        {
            cn = Database.getRDBConnection();
            pstmt = cn.prepareStatement(query.toString());
            pstmt1 = cn.prepareStatement(countquery.toString());
            if(functions.isValueNull(partnerid))
            {
                pstmt.setString(counter, partnerid);
                pstmt1.setString(counter, partnerid);
                counter++;
            }
            pstmt.setInt(counter, start);
            counter++;
            pstmt.setInt(counter, end);

            log.debug("pstmt inside getMerchantUserUnblockedAccountList:::::::"+pstmt);
            merchanthash = Database.getHashMapFromResultSet(pstmt.executeQuery());

            rs = pstmt1.executeQuery();
            rs.next();
            int totalemailrecords = rs.getInt(1);

            merchanthash.put("totalrecords", "" + totalemailrecords);
            merchanthash.put("records", "0");

            if (totalemailrecords > 0)
            {
                merchanthash.put("records", "" + (merchanthash.size() - 2));
            }

        }
        catch (SystemError se)
        {
            log.error("SystemError in BlockedEmailList::::",se);
        }
        catch (Exception e)
        {
            log.error("Exception in BlockedEmailList",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(cn);
        }
        return merchanthash;
    }*/



    public boolean getUnBlockedUserAccount(String login)
    {
        boolean flag = false;
        StringBuilder query = new StringBuilder("UPDATE user SET unblocked='unlocked' ,faillogincount='0' WHERE unblocked='locked' AND faillogincount>='3' AND login= ? AND roles='submerchant'");

        Connection con = null;
        PreparedStatement pstmt = null;
        try
        {
            con = Database.getConnection();
            pstmt= con.prepareStatement(query.toString());
            pstmt.setString(1,login);
            log.debug("update query::::"+pstmt);
            int i = pstmt.executeUpdate();
            if (i > 0)
            {
                flag = true;
            }
        }
        catch (SystemError se)
        {
            log.error("SystemError in UnblockEmail::::::",se);
        }
        catch (Exception e)
        {
            log.error("SystemError in UnblockEmail::::::",e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return flag;
    }
    public static TreeMap<String, String> getAccountDetailsMemberid( String memberid, String partnerid)
    {
        Connection con = null;
        PreparedStatement psmt = null;
        Functions functions = new Functions();
        TreeMap<String, String> map = new TreeMap();
        int counter = 1;
        StringBuilder query = new StringBuilder("SELECT ga.accountid,ga.merchantid,gt.currency FROM member_account_mapping AS mam, gateway_accounts AS ga, gateway_type AS gt, gateway_account_partner_mapping AS gacpm  WHERE mam.accountid = ga.accountid AND ga.pgtypeid = gt.pgtypeid AND ga.accountid=gacpm.accountid");
        try
        {
            con = Database.getConnection();
            if (functions.isValueNull(memberid))
            {
                query.append(" AND mam.memberid = ?");
            }
            if (functions.isValueNull(partnerid))
            {
                query.append(" AND gacpm.partnerid = ?");
            }
            psmt = con.prepareStatement(query.toString());

            if (functions.isValueNull(memberid))
            {
                psmt.setString(counter, memberid);
                counter++;
            }
            if (functions.isValueNull(partnerid))
            {
                psmt.setString(counter, partnerid);
            }
            ResultSet rs = psmt.executeQuery();
            log.debug("Query: psmt memberid ::::::"+psmt.toString());
            while (rs.next())
            {
                map.put(rs.getString("accountid"),rs.getString("accountid")+"-"+rs.getString("merchantid")+"-"+rs.getString("currency"));
            }

        }
        catch (SystemError s)
        {
            log.error("System error while performing select query",s);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (SQLException e)
        {
            log.error("SQL error",e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeConnection(con);
        }
        return map;
    }
    public List<PartnerDetailsVO> getPartnerDetailsForExportTransactionCron() throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet res = null;
        List<PartnerDetailsVO> partnerList=new ArrayList<>();
        PartnerDetailsVO partnerDetailsVO=null;
        try
        {
            //conn = Database.getConnection();
            conn=Database.getRDBConnection();
            log.debug("Entering getPartnerDetailsForExportTransactionCron Details");
            String query = "SELECT login,exportTransactionCron FROM partners WHERE exportTransactionCron != 'N'";
            pstmt = conn.prepareStatement(query);
            log.error("getPartnerDetailsForExportTransactionCron----------->"+pstmt);
            res = pstmt.executeQuery();
            while (res.next())
            {
                partnerDetailsVO=new PartnerDetailsVO();
                partnerDetailsVO.setLogin(res.getString("login"));
                partnerDetailsVO.setExportTransactionCron(res.getString("exportTransactionCron"));
                partnerList.add(partnerDetailsVO);
            }
        }
        catch (SystemError se)
        {
            log.error(":::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TransactionUtility.java", "getPartnerDetailsForExportTransactionCron()", null, "Transaction", "SQL Exception Thrown:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            log.error(":::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TransactionUtility.class", "getPartnerDetailsForExportTransactionCron()", null, "Transaction", "SQL Exception Thrown:::::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return partnerList;
    }

    public boolean checkMemberAccountConfiguration(String memberId,String accountId)
    {
        boolean check=false;
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try
        {
            connection=Database.getConnection();
            String query="SELECT memberid,accountid FROM member_account_mapping WHERE memberid=? AND accountid=?";
            preparedStatement=connection.prepareStatement(query);
            preparedStatement.setString(1,memberId);
            preparedStatement.setString(2,accountId);
            log.error("Query checkMemberAccountConfiguration:::"+preparedStatement);
            rs=preparedStatement.executeQuery();
            if (rs.next())
            {
                check=true;
            }
        }catch (Exception e)
        {
            log.error("Exception::::",e);
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeResultSet(rs);
            Database.closeConnection(connection);
        }
        return check;
    }


    public String getFormattedStatus(String status)
    {
        String s = "";
        if("begun".equals(status))
        {
            s = "Begun Processing";
        }
        else if("authstarted".equals(status))
        {
            s = "Auth Started";
        }
        else if ("payoutstarted".equals(status)){
            s = "Payout Started";
        }
        else if ("proofrequired".equals(status)){
            s = "Proof Required";
        }
        else if ("authsuccessful".equals(status)){
            s = "Auth Successful";
        } else if ("payoutsuccessful".equals(status)){
            s = "Payout Successful";
        }
        else if ("authfailed".equals(status)){
            s = "Auth Failed";
        } else if ("proofrequired".equals(status)){
            s = "Proof Required";
        }
        else if ("payoutfailed".equals(status)){
            s = "Payout Failed";
        }
        else if ("capturestarted".equals(status)){
            s = "Capture Started";
        } else if ("capturesuccess".equals(status)){
            s = "Capture Successful";
        } else if ("capturefailed".equals(status)){
            s = "Capture Failed";
        }
        else if ("podsent".equals(status)){
            s = "POD Sent";
        }
        else if ("settled".equals(status)){
            s = "Settled";
        }
        else if ("reversed".equals(status)){
            s = "Reversed";
        }
        else if ("chargeback".equals(status)){
            s = "Chargeback";
        }
        else if ("failed".equals(status)){
            s = "Failed";
        }
        else if ("cancelstarted".equals(status)){
            s = "Cancel Initiated";
        }
        else if ("cancelled".equals(status)){
            s = "Cancelled Transactions";
        }
        else if ("authcancelled".equals(status)){
            s = "Authorisation Cancelled";
        }
        else if ("markedforreversal".equals(status)){
            s = "Reversal Request Sent";
        }
        else if ("payoutsuccessful".equals(status)){
            s = "Payout Successful";
        }
        else if ("payoutstarted".equals(status)){
            s = "Payout Started";
        }
        else if ("partialrefund".equals(status)){
            s = "Partially Reversed";
        }
        else if ("payoutcancelstarted".equals(status)){
            s = "Payout Cancel Started";
        }
        else if ("payoutcancelsuccessful".equals(status)){
            s = "Payout Cancel Successful";
        }
        else if ("payoutcancelfailed".equals(status)){
            s = "Payout Cancel Failed";
        }
        else if ("authstarted_3D".equals(status)){
            s = "Auth Started 3D";
        }

        return s;

    }

    public String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    public PartnerEmailNotificationVO getEmailNotificationDetails(String partnerId)throws SQLException
    {
        PartnerEmailNotificationVO partnerEmailNotificationVO=null;
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        if(functions.isValueNull(partnerId) && "0".equalsIgnoreCase(partnerId))
            partnerId="1";
        try{
            connection=Database.getConnection();
            String query="Select * from partner_configuration where partnerId=?";
            preparedStatement=connection.prepareStatement(query);
            preparedStatement.setString(1,partnerId);
            log.error("preparedStatement--->"+preparedStatement);
            resultSet=preparedStatement.executeQuery();
            if(resultSet.next()){
                partnerEmailNotificationVO=new PartnerEmailNotificationVO();
                partnerEmailNotificationVO.setSalesBillingDescriptor(resultSet.getString("salesBillingDescriptor"));
                partnerEmailNotificationVO.setMerchantSalesTransaction(resultSet.getString("merchantSalesTransaction"));
                partnerEmailNotificationVO.setSalesAdminFailedTransaction(resultSet.getString("salesAdminFailedTransaction"));
                partnerEmailNotificationVO.setSalesPartnerCardRegistration(resultSet.getString("salesPartnerCardRegistration"));
                partnerEmailNotificationVO.setSalesMerchantCardRegistration(resultSet.getString("salesMerchantCardRegistration"));
                partnerEmailNotificationVO.setSalesPayoutTransaction(resultSet.getString("salesPayoutTransaction"));
                partnerEmailNotificationVO.setFraudFailedTransaction(resultSet.getString("fraudFailedTransaction"));
                partnerEmailNotificationVO.setChargebackTransaction(resultSet.getString("chargebackTransaction"));
                partnerEmailNotificationVO.setRefundTransaction(resultSet.getString("refundTransaction"));
            }
        }
        catch (Exception e){
            log.error("Exception--->",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return partnerEmailNotificationVO;
    }

    public PartnerDefaultConfigVO getEmailNotificationDetailsForMerchant(String memberId)throws SQLException
    {
        PartnerDefaultConfigVO partnerDefaultConfigVO=null;
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        try{
            connection=Database.getConnection();
            String query="Select * from merchant_configuration where partnerId=?";
            preparedStatement=connection.prepareStatement(query);
            preparedStatement.setString(1,memberId);
            resultSet=preparedStatement.executeQuery();
            if(resultSet.next()){
                partnerDefaultConfigVO=new PartnerDefaultConfigVO();
                partnerDefaultConfigVO.setMerchantRegistrationMail(resultSet.getString("merchantRegistrationMail"));
                partnerDefaultConfigVO.setMerchantChangePassword(resultSet.getString("merchantChangePassword"));
                partnerDefaultConfigVO.setMerchantChangeProfile(resultSet.getString("merchantChangeProfile"));
                partnerDefaultConfigVO.setTransactionSuccessfulMail(resultSet.getString("transactionSuccessfulMail"));
                partnerDefaultConfigVO.setTransactionFailMail(resultSet.getString("transactionFailMail"));
                partnerDefaultConfigVO.setTransactionCapture(resultSet.getString("transactionCapture"));
                partnerDefaultConfigVO.setTransactionPayoutSuccess(resultSet.getString("transactionPayoutSuccess"));
                partnerDefaultConfigVO.setTransactionPayoutFail(resultSet.getString("transactionPayoutFail"));
                partnerDefaultConfigVO.setRefundMail(resultSet.getString("refundMail"));
                partnerDefaultConfigVO.setChargebackMail(resultSet.getString("chargebackMail"));
                partnerDefaultConfigVO.setTransactionInvoice(resultSet.getString("transactionInvoice"));
                partnerDefaultConfigVO.setCardRegistration(resultSet.getString("cardRegistration"));
                partnerDefaultConfigVO.setPayoutReport(resultSet.getString("payoutReport"));
                partnerDefaultConfigVO.setMonitoringAlertMail(resultSet.getString("monitoringAlertMail"));
                partnerDefaultConfigVO.setMonitoringSuspensionMail(resultSet.getString("monitoringSuspensionMail"));
                partnerDefaultConfigVO.setHighRiskRefunds(resultSet.getString("highRiskRefunds"));
                partnerDefaultConfigVO.setFraudFailedTxn(resultSet.getString("fraudFailedTxn"));
                partnerDefaultConfigVO.setDailyFraudReport(resultSet.getString("dailyFraudReport"));
                partnerDefaultConfigVO.setCustomerTransactionSuccessfulMail(resultSet.getString("customerTransactionSuccessfulMail"));
                partnerDefaultConfigVO.setCustomerTransactionFailMail(resultSet.getString("customerTransactionFailMail"));
                partnerDefaultConfigVO.setCustomerTransactionPayoutSuccess(resultSet.getString("customerTransactionPayoutSuccess"));
                partnerDefaultConfigVO.setCustomerTransactionPayoutFail(resultSet.getString("customerTransactionPayoutFail"));
                partnerDefaultConfigVO.setCustomerRefundMail(resultSet.getString("customerRefundMail"));
                partnerDefaultConfigVO.setCustomerTokenizationMail(resultSet.getString("customerTokenizationMail"));
            }
        }
        catch (Exception e){
            log.error("Exception--->",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return partnerDefaultConfigVO;
    }
    public String getPartnerIdfromMemberid(String memberId)
    {
        Connection con=null;
        String partnerId="";

        try {

            con=Database.getConnection();
            String query="SELECT partnerId FROM members WHERE memberid=?";
            PreparedStatement ps=con.prepareStatement(query);
            ps.setString(1,memberId);

            ResultSet rs=ps.executeQuery();
            if (rs.next())
            {
                partnerId=rs.getString("partnerId");
                log.error("Generated ID-------"+partnerId);
            }

        }
        catch(Exception e)
        {
            log.error("Exception in questionDetails-----",e);
        }
        finally {
            Database.closeConnection(con);
        }

        return partnerId;
    }
    public String getSuperAdminId(String partnerId)
    {
        Connection con=null;
        String superAdminId="";

        try {

            con=Database.getConnection();
            String query="SELECT superadminid FROM partners WHERE partnerId=?";
            PreparedStatement ps=con.prepareStatement(query);
            ps.setString(1,partnerId);

            ResultSet rs=ps.executeQuery();
            if (rs.next())
            {
                superAdminId=rs.getString("superadminid");
                log.error("Generated ID-------"+superAdminId);
            }

        }
        catch(Exception e)
        {
            log.error("Exception in questionDetails-----",e);
        }
        finally {
            Database.closeConnection(con);
        }

        return superAdminId;
    }

    public HashMap getPartnerMerchantCallbackSettingList(String memberId, String partnerId)
    {
        HashMap hash = new HashMap();

        StringBuffer query = new StringBuffer("select m.memberid, mc.reconciliationNotification, mc.transactionNotification, mc.refundNotification,mc.chargebackNotification,mc.payoutNotification, mc.inquiryNotification from members AS m JOIN merchant_configuration AS mc ON m.memberid=mc.memberid JOIN partners p where m.partnerId=p.partnerId and m.memberid=? and (m.partnerid=? or p.superadminid=?) ");
        StringBuffer countQuery = new StringBuffer("select count(*) from members m , partners p,merchant_configuration mc where m.partnerId = p.partnerId and  m.memberid=? and (m.partnerid=? or p.superadminid=?)");

        Connection con = null;
        PreparedStatement p = null,p1 = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            if (functions.isValueNull(memberId) && functions.isValueNull(partnerId))
            {
                p = con.prepareStatement(query.toString());
                p.setString(1, memberId);
                p.setString(2, partnerId);
                p.setString(3, partnerId);
                log.debug("query inside callback Memberssa table inside dao::::" + p);
                hash = Database.getHashMapFromResultSet(p.executeQuery());
                p1 = con.prepareStatement(countQuery.toString());
                p1.setString(1, memberId);
                p1.setString(2, partnerId);
                p1.setString(3, partnerId);
                log.debug("count query inside callback Members table inside dao::::" + p1);
                rs = p1.executeQuery();
                rs.next();
                int totalrecords = rs.getInt(1);

                hash.put("totalrecords", "" + totalrecords);
                hash.put("records", "0");
                if (totalrecords > 0)
                {
                    hash.put("records", "" + (hash.size() - 2));
                }
                log.debug("insert ++++"+query);
            }
            else
            {
                hash = new HashMap();
                hash.put("records", "0");
                hash.put("totalrecords", "0");
            }
        }

        catch (SystemError se){
            log.error("System Error::::",se);
        }
        catch (Exception e){
            log.error("Exception::::",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closePreparedStatement(p1);
            Database.closeConnection(con);
        }
        return hash;
    }

    public HashMap getReportSucccessCount(String toid,String partnerId,String fdtstamp,String tdtstamp, String currency,String accountid) throws SystemError
    {
        Connection conn = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        conn = Database.getRDBConnection();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        String SuccessCount=null;
        String SuccessAmount=null;
        HashMap hash=new HashMap();
        PreparedStatement pstmt = null;
        int counter = 1;


        try
        {
            //common
            query.append("select COUNT(trackingid) AS COUNT,SUM(amount) AS amount from transaction_common where status IN('markedforreversal' ,'capturesuccess','authsuccessful','chargeback','settled','reversed')");
            if (functions.isValueNull(toid))
            {
                query.append("AND toid = ?");
            }
            if (functions.isValueNull(accountid))
            {
                query.append(" AND  accountid = ?");
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" AND dtstamp>= ?");
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" AND dtstamp<= ?");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" AND currency IN ( ? )");
            }


            pstmt = conn.prepareStatement(query.toString());


            if (functions.isValueNull(toid))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, toid));
                counter++;
            }
            if (functions.isValueNull(accountid))
            {
                pstmt.setString(counter,ESAPI.encoder().encodeForSQL(me,accountid));
                counter++;
            }
            if (functions.isValueNull(fdtstamp))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me,fdtstamp));
                counter++;
            }
            if (functions.isValueNull(tdtstamp))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, tdtstamp));
                counter++;
            }
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, currency));
                counter++;
            }

            log.debug("pstmt::::"+pstmt);

            if (isPartnerMemberMapped(toid, partnerId))
            {
                System.out.println("pstmt::::"+pstmt);
                rs = pstmt.executeQuery();
                if (rs.next())
                {
                    hash.put("count",rs.getString("count"));
                    hash.put("amount",rs.getString("amount"));
                }
            }


        }
        catch (SQLException e)
        {
            log.error("sql error while execute status report",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return hash;
    }

    public HashMap getReportpercentageSuccessCount(String toid,String partnerId,String fdtstamp,String tdtstamp, String currency,String accountid) throws SystemError
    {
        Connection conn = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        conn = Database.getRDBConnection();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        String SuccessCount=null;
        String SuccessAmount=null;
        HashMap hash=new HashMap();
        PreparedStatement pstmt = null;
        int counter = 1;

        try
        {
            //common
            query.append("select COUNT(trackingid) AS COUNT,SUM(amount) AS amount from transaction_common where STATUS NOT IN ('begun','authstarted','failed','cancelled','payoutstarted','payoutsuccessful','payoutfailed') AND STATUS IN('markedforreversal' ,'capturesuccess','authsuccessful','chargeback','settled','reversed') AND");
            if (functions.isValueNull(toid))
            {
                query.append(" toid = ?");
            }
            if (functions.isValueNull(accountid))
            {
                query.append(" AND accountid = ?");
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" AND dtstamp>= ?");
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" AND dtstamp<= ?");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" AND currency IN ( ? )");
            }

            StringBuilder countquery = new StringBuilder("select SUM(count) as count from ( ");
            countquery.append(query.toString());

            pstmt = conn.prepareStatement(query.toString());

            if (functions.isValueNull(toid))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, toid));
                counter++;
            }
            if (functions.isValueNull(accountid))
            {
                pstmt.setString(counter,ESAPI.encoder().encodeForSQL(me,accountid));
                counter++;
            }
            if (functions.isValueNull(fdtstamp))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me,fdtstamp));
                counter++;
            }
            if (functions.isValueNull(tdtstamp))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, tdtstamp));
                counter++;
            }
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, currency));
                counter++;
            }

            log.debug("pstmt::::"+pstmt);

            if (isPartnerMemberMapped(toid, partnerId))
            {
                System.out.println("pstmt::::"+pstmt);
                rs = pstmt.executeQuery();
                if (rs.next())
                {
                    hash.put("count",rs.getString("count"));
                    hash.put("amount",rs.getString("amount"));
                }
            }
        }
        catch (SQLException e)
        {
            log.error("sql error while execute status report",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return hash;

    }
    public String getCookiesURL(String partnerId){
        String URL="";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getRDBConnection();
            String selquery = "SELECT cookiesUrl FROM partners WHERE partnerId = ?";
            pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, partnerId);
            rs = pstmt.executeQuery();

            if (rs.next())
            {
                URL = rs.getString("cookiesUrl");
            }
        }

        catch (Exception e)
        {
            log.error("Exception in isMemberUser method: ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

        return URL;
    }
// Get merchant Contact phone
    public String getMemberContactNumber(String memberid){
        String contactNumber="";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getRDBConnection();
            String selquery = "SELECT maincontact_phone FROM merchant_configuration WHERE memberid = ?";
            pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, memberid);
            rs = pstmt.executeQuery();

            if (rs.next())
            {
                contactNumber = rs.getString("maincontact_phone");
            }
        }

        catch (Exception e)
        {
            log.error("Exception in isMemberUser method: ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

        return contactNumber;
    }


    // Get merchant Contact phone
    public String getMerchantUserContactNumber(String userId){
        String contactNumber="";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getRDBConnection();
            String selquery = "SELECT telno,telcc FROM member_users WHERE userid = ?";
            pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next())
            {
                contactNumber = rs.getString("telcc")+"-"+rs.getString("telno");
            }
        }

        catch (Exception e)
        {
            log.error("Exception in isMemberUser method: ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

        return contactNumber;
    }
}