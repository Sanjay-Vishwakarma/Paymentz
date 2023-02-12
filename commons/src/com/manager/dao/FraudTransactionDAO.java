package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.logicboxes.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 9/18/14
 * Time: 5:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class FraudTransactionDAO
{
    static Logger logger = new Logger(FraudTransactionDAO.class.getName());
    private static Hashtable<String, String> MerchantMaxScore;
    private static Hashtable<String, String> MerchantMaxScoreReversal;
    static
    {
        try
        {
            loadFraudTransactionThresholdForMerchant();
        }
        catch (Exception e)
        {
            logger.error("Exception in loading Metrchant Max Score or AutoReversal Score " + Util.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }
    public static void loadFraudTransactionThresholdForMerchant() throws Exception
    {
        MerchantMaxScore = new Hashtable<String, String>();
        MerchantMaxScoreReversal = new Hashtable<String, String>();

        Connection conn = null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            rs = Database.executeQuery("SELECT memberId,maxScoreAllowed,maxScoreAutoReversal FROM members",conn);
            while (rs.next())
            {
                MerchantMaxScore.put(rs.getString("memberId"),rs.getString("maxScoreAllowed"));
                MerchantMaxScoreReversal.put(rs.getString("memberId"),rs.getString("maxScoreAutoReversal"));
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
    }
    public String getMerchantAutoReversalScore(String memberId)
    {
        return MerchantMaxScoreReversal.get(memberId);
    }
    public String getMerchantMaxScoreAllowed(String memberId)
    {
        return MerchantMaxScore.get(memberId);
    }

    public Set getMembersForOfflineFraudCheck()
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        Set<String> set=null;
        try
        {
            set=new HashSet<String>();
            con= Database.getRDBConnection();
            String query = "SELECT mfsm.memberid FROM members  AS m JOIN member_fraudsystem_mapping AS mfsm ON m.memberid=mfsm.memberid WHERE m.activation=? AND mfsm.isactive=? AND mfsm.isonlinefraudcheck=?";
            preparedStatement=con.prepareStatement(query);
            preparedStatement.setString(1,"Y");
            preparedStatement.setString(2,"Y");
            preparedStatement.setString(3,"N");
            rs=preparedStatement.executeQuery();
            while(rs.next())
            {
                set.add(rs.getString("memberid"));
            }
            if(!(set.size()>0))
            {
              logger.debug("Member-Fraud System Not Found For Offline Fraud Check");
            }

        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving  getAllActiveMembers()", se);
        }
        catch(SystemError e)
        {
            logger.error("SystemError ::::::: Leaving getAllActiveMembers() ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return set;

    }
    public int getFraudSystemIdByMemberId(String memberId)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        int fsId=0;
        try
        {
            con= Database.getRDBConnection();
            String Query = "select fsid from member_fraudsystem_mapping where memberid=?";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,memberId);
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                fsId=rs.getInt("fsid");
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving", se);
        }
        catch(SystemError e)
        {
            logger.error("SystemError ::::::: Leaving", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return fsId;
    }
    public int getMaxTrackingIdByStatus(String memberId,String accountId,String fraudTransStatus)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        int trackingId=0;
        try
        {
            con= Database.getRDBConnection();
            String Query = "select max(trackingid) as 'trackingid'  from fraud_transaction where memberid=? and accountid=? and fraud_trans_status=?";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,memberId);
            preparedStatement.setString(2,accountId);
            preparedStatement.setString(3,fraudTransStatus);
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                trackingId=rs.getInt("trackingid")+1;
            }

        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving", se);
        }
        catch(SystemError e)
        {
            logger.error("SystemError ::::::: Leaving", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return trackingId;

    }
    public String getAccountIdByMemberId(String memberId)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        String accountId=null;
        try
        {
            con= Database.getRDBConnection();
            String Query = "SELECT accountid FROM member_account_mapping WHERE memberid=?";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,memberId);
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                accountId=rs.getString("accountid");
            }

        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving", se);
        }
        catch(SystemError e)
        {
            logger.error("SystemError ::::::: Leaving ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return accountId;
    }
    public int getMemberFirstTransaction(String memberId,String accountId,String tableName)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        int minTrackingId=0;
        try
        {
            con= Database.getRDBConnection();
            String Query = "SELECT MIN(trackingid) as 'minTrackingId' FROM "+tableName+" WHERE toid=? and accountid=?";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,memberId);
            preparedStatement.setString(2,accountId);
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                minTrackingId=rs.getInt("minTrackingId");
            }

        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving ", se);
        }
        catch(SystemError e)
        {
            logger.error("SystemError ::::::: Leaving ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return minTrackingId;
    }
    public ArrayList<HashMap> getTransDetailsForFraudCheckByMemberId(String tableName,int trackingId,String memberId,String accountId)
    {
        ArrayList<HashMap> listOfTransactions = new ArrayList<HashMap>();
        HashMap transDetails=null;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        try
        {
            con=Database.getRDBConnection();
            String Query = "SELECT DISTINCT trackingid,toid,first_six,last_four,amount,T.emailaddr,description,FROM_UNIXTIME(dtstamp) AS 'time',STATUS,firstname,lastname,NAME,T.accountId,currency,ipaddress,country,city,state,street,zip,TIMESTAMP, MAM.daily_card_amount_limit , MAM.weekly_card_amount_limit, MAM.monthly_card_amount_limit, MAM.min_transaction_amount FROM "+  tableName + " AS T JOIN bin_details ON T.trackingid = bin_details.icicitransid  LEFT JOIN member_account_mapping AS MAM ON T.accountid=MAM.accountid AND T.toid=MAM.memberid AND T.paymodeid=MAM.paymodeid AND T.cardtypeid = MAM.cardtypeid  WHERE  STATUS IN ('capturesuccess','settled','authsuccess') AND trackingid >=? AND T.toid=? AND T.accountid=? AND T.firstname != \"\" AND T.`firstname` IS NOT NULL AND T.`lastname`!=\"\" AND T.lastname IS NOT NULL AND first_six IS NOT NULL AND ipaddress IS NOT NULL AND ipaddress != \"\"";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setInt(1,trackingId);
            preparedStatement.setString(2, memberId);
            preparedStatement.setString(3, accountId);
            rs=preparedStatement.executeQuery();
            while (rs.next())
            {
                transDetails = new HashMap();
                //Mandatory Fields
                transDetails.put("memberid",String.valueOf(rs.getInt("toid")));
                transDetails.put("trackingid",String.valueOf(rs.getInt("trackingid")));
                transDetails.put("bin",rs.getString("first_six"));
                transDetails.put("ip",rs.getString("ipaddress"));
                transDetails.put("last_4",rs.getString("last_four"));
                transDetails.put("amount",rs.getString("amount"));
                transDetails.put("trans_id",String.valueOf(rs.getInt("trackingid")));
                transDetails.put("status",rs.getString("status"));
                transDetails.put("accountId",rs.getString("accountId"));
                transDetails.put("currency",rs.getString("currency"));
                transDetails.put("time",rs.getString("time"));
                transDetails.put("device_id","");//Optional

                transDetails.put("deposit_limits[pay_method_type]","CC");
                transDetails.put("deposit_limits[dl_min]", String.valueOf(rs.getInt("min_transaction_amount")));
                transDetails.put("deposit_limits[dl_daily]",String.valueOf(rs.getInt("daily_card_amount_limit")));
                transDetails.put("deposit_limits[dl_weekly]",String.valueOf(rs.getInt("weekly_card_amount_limit")));
                transDetails.put("deposit_limits[dl_monthly]",String.valueOf(rs.getInt("monthly_card_amount_limit")));

                transDetails.put("customer_information[country]",rs.getString("country"));
                transDetails.put("customer_information[city]",rs.getString("city"));
                transDetails.put("customer_information[address1]",rs.getString("street"));
                transDetails.put("customer_information[postal_code]",rs.getString("zip"));
                transDetails.put("customer_information[email]",rs.getString("emailaddr"));
                transDetails.put("customer_information[first_name]",rs.getString("firstname"));
                transDetails.put("customer_information[last_name]",rs.getString("lastname"));
                transDetails.put("description",rs.getString("description"));
                transDetails.put("state",rs.getString("state"));

                listOfTransactions.add(transDetails);
            }
            return listOfTransactions;

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
        return listOfTransactions;
    }
    public HashMap getTransactionDetailByTrackingId(String tableName, String trackingId)
    {
        HashMap transDetails=null;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        try
        {
            logger.debug("came To Pick Up The Failed Transaction");
            con=Database.getRDBConnection();
            String Query = "SELECT DISTINCT trackingid,toid,first_six,last_four,amount,T.emailaddr,description,orderdescription,FROM_UNIXTIME(dtstamp) AS 'time',STATUS,firstname,lastname,NAME,T.accountId,currency,ipaddress,country,city,state,street,zip,telnocc,telno, TIMESTAMP, MAM.daily_card_amount_limit , MAM.weekly_card_amount_limit, MAM.monthly_card_amount_limit, MAM.min_transaction_amount FROM "+  tableName + " AS T JOIN bin_details ON T.trackingid = bin_details.icicitransid  LEFT JOIN member_account_mapping AS MAM ON T.accountid=MAM.accountid AND T.toid=MAM.memberid AND T.paymodeid=MAM.paymodeid AND T.cardtypeid = MAM.cardtypeid  WHERE  STATUS IN ('capturesuccess','settled','authsuccess') AND trackingid =? AND T.firstname != \"\" AND T.`firstname` IS NOT NULL AND T.`lastname`!=\"\" AND T.lastname IS NOT NULL AND first_six IS NOT NULL AND ipaddress IS NOT NULL";
            logger.debug("Check Exceptional Query=="+Query);
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1, trackingId);
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                transDetails = new HashMap();
                transDetails.put("memberid",String.valueOf(rs.getInt("toid")));
                transDetails.put("trackingid",String.valueOf(rs.getInt("trackingid")));
                transDetails.put("bin",rs.getString("first_six"));
                transDetails.put("ip",rs.getString("ipaddress"));
                transDetails.put("last_4",rs.getString("last_four"));
                transDetails.put("amount",rs.getString("amount"));
                transDetails.put("trans_id",String.valueOf(rs.getInt("trackingid")));
                transDetails.put("status",rs.getString("status"));
                transDetails.put("currency",rs.getString("currency"));
                transDetails.put("time",rs.getString("time"));
                transDetails.put("deposit_limits[pay_method_type]","CC");
                transDetails.put("deposit_limits[dl_min]", String.valueOf(rs.getInt("min_transaction_amount")));
                transDetails.put("deposit_limits[dl_daily]",String.valueOf(rs.getInt("daily_card_amount_limit")));
                transDetails.put("deposit_limits[dl_weekly]",String.valueOf(rs.getInt("weekly_card_amount_limit")));
                transDetails.put("deposit_limits[dl_monthly]",String.valueOf(rs.getInt("monthly_card_amount_limit")));
                transDetails.put("customer_information[country]",rs.getString("country"));
                transDetails.put("customer_information[city]",rs.getString("city"));
                transDetails.put("customer_information[address1]",rs.getString("street"));
                transDetails.put("customer_information[postal_code]",rs.getString("zip"));
                transDetails.put("customer_information[email]",rs.getString("emailaddr"));
                transDetails.put("customer_information[first_name]",rs.getString("firstname"));
                transDetails.put("customer_information[last_name]",rs.getString("lastname"));
                transDetails.put("payment_method[last_digits]",rs.getString("last_four"));
                transDetails.put("email",rs.getString("emailaddr"));
                transDetails.put("description",rs.getString("description"));
                transDetails.put("state",rs.getString("state"));

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
        return transDetails;
    }
    public Set getFraudCheckFailedTrackingId(String memberId,String accountId,String status)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        Set<String> set=null;
        try
        {
            set=new HashSet<String>();
            con= Database.getRDBConnection();
            String Query = "select trackingId from fraud_transaction where memberid=?  and accountid=? and fraud_trans_status=? and attempts<3";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,memberId);
            preparedStatement.setString(2,accountId);
            preparedStatement.setString(3,status);
            rs=preparedStatement.executeQuery();
            while(rs.next())
            {
                set.add(rs.getString("trackingId"));
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
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return set;
    }
    public ArrayList<HashMap> getTransDetailsFraudUpdateToAT(String memberId,String tableName)
    {
        ArrayList<HashMap> listOfTransactions = new ArrayList<HashMap>();
        HashMap transDetails=null;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        try
        {
            con= Database.getRDBConnection();
            String Query = "SELECT DISTINCT fd.fraud_transaction_id,fd.fstransid,T.status,fd.trackingid FROM fraud_transaction AS fd JOIN "+tableName+" AS T ON fd.trackingid=T.trackingid JOIN at_fraud_trans_details AS aftd ON fd.fraud_transaction_id=aftd.fraud_trans_id WHERE (T.status IN ('chargeback') AND aftd.updatechargebackdesc IS NULL AND aftd.updatechargebackstatus IS NULL) OR (T.status IN ('reversed') AND aftd.updatereversdesc IS NULL AND aftd.updatereversstatus IS NULL) AND fd.memberid=?";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,memberId);
            rs=preparedStatement.executeQuery();
            while (rs.next())
            {
                transDetails = new HashMap();
                transDetails.put("fraudtransid",rs.getString("fraud_transaction_id"));
                transDetails.put("fstransid", rs.getString("fstransid"));
                transDetails.put("status", rs.getString("status"));
                listOfTransactions.add(transDetails);
            }

            return listOfTransactions;
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
        return listOfTransactions;

    }
    public boolean isFraudSystemWorkOffline(int fsId)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        boolean isWorkForOffline=false;
        try
        {
            con= Database.getRDBConnection();
            String query = "select fsid from fraudsystem_master where fsid=? and offline='Y'";
            preparedStatement=con.prepareStatement(query);
            preparedStatement.setInt(1,fsId);
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                isWorkForOffline=true;
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving", se);
        }
        catch(SystemError e)
        {
            logger.error("SystemError ::::::: Leaving", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return isWorkForOffline;
    }
    public Map<String,Map<String,Map<String,Map<String,String>>>> getHighRiskTransactionDetailsForAlertCron()
    {
        Map<String,Map<String,Map<String,Map<String,String>>>> mailList=new LinkedHashMap<String, Map<String, Map<String, Map<String, String>>>>() ;

        Map<String,Map<String,Map<String,String>>> merchantsRefundTransactions = new LinkedHashMap<String, Map<String, Map<String, String>>>();

        Map<String,Map<String,Map<String,String>>> merchantsFraudIntimationTransactions = new LinkedHashMap<String, Map<String, Map<String, String>>>();

        Map<String,Map<String,Map<String,String>>> adminFraudIntimationTransactions = new LinkedHashMap<String, Map<String, Map<String, String>>>();

        Map<String,Map<String,String>> listOfFraudIntimationTransactions = null;

        Map<String,Map<String,String>> listOfTransactionsForAdmin = null;

        Map<String,Map<String,String>> listOfRefundedTransactions = null;

        Map transaDetails=null;

        Connection con = null;

        PreparedStatement preparedStatement = null;

        ResultSet rs=null;

        try
        {
            con= Database.getConnection();
            String Query = "SELECT ft.trackingid,ft.memberid,ft.isReversed,ft.isAlertSent,ft.status,aftd.amount,aftd.score from fraud_transaction as ft join at_fraud_trans_details as aftd on ft.fraud_transaction_id=aftd.fraud_trans_id where isAlertSent=?";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,"N");
            rs=preparedStatement.executeQuery();
            while (rs.next())
            {
                if("R".equals(rs.getString("status")) && "T".equals(rs.getString("isReversed")))
                {
                    transaDetails = new LinkedHashMap();
                    transaDetails.put("TrackingId",String.valueOf(rs.getInt("trackingid")));
                    transaDetails.put("MemberId",String.valueOf(rs.getInt("memberid")));
                    transaDetails.put("Score",rs.getString("score"));
                    transaDetails.put("Amount",rs.getString("amount"));
                    transaDetails.put("Transaction Status","Reversed");
                    transaDetails.put("Refund Reason","High risk fraud transaction");

                    listOfRefundedTransactions =merchantsRefundTransactions.get(String.valueOf(rs.getInt("memberid")));
                    if(listOfRefundedTransactions==null)
                    {
                        listOfRefundedTransactions = new LinkedHashMap<String, Map<String, String>>();
                        listOfRefundedTransactions.put((String) transaDetails.get("TrackingId"), transaDetails);
                    }
                    else
                    {
                        listOfRefundedTransactions.put((String) transaDetails.get("TrackingId"), transaDetails);
                    }
                    merchantsRefundTransactions.put(String.valueOf(rs.getInt("memberid")),listOfRefundedTransactions);
                }
                else if("I".equals(rs.getString("status")) /*|| "R".equals(rs.getString("status"))*/)
                {
                    transaDetails = new LinkedHashMap();
                    transaDetails.put("TrackingId",String.valueOf(rs.getInt("trackingid")));
                    transaDetails.put("MemberId",String.valueOf(rs.getInt("memberid")));
                    transaDetails.put("Score",rs.getString("score"));
                    transaDetails.put("Amount",rs.getString("amount"));
                    transaDetails.put("Recommendation","Transaction is fraudulent take appropriate action.");

                    listOfFraudIntimationTransactions = merchantsFraudIntimationTransactions.get(String.valueOf(rs.getInt("memberid")));
                    if(listOfFraudIntimationTransactions==null)
                    {
                        listOfFraudIntimationTransactions = new LinkedHashMap<String,Map<String,String>>();
                        listOfFraudIntimationTransactions.put((String) transaDetails.get("TrackingId"), transaDetails);
                    }
                    else
                    {
                        listOfFraudIntimationTransactions.put((String) transaDetails.get("TrackingId"), transaDetails);
                    }
                    merchantsFraudIntimationTransactions.put(String.valueOf(rs.getInt("memberid")),listOfFraudIntimationTransactions);
                }
                if("R".equals(rs.getString("status"))&& "F".equals(rs.getString("isReversed")))
                {
                    transaDetails = new LinkedHashMap();
                    transaDetails.put("TrackingId",String.valueOf(rs.getInt("trackingid")));
                    transaDetails.put("MemberId",String.valueOf(rs.getInt("memberid")));
                    transaDetails.put("Score",rs.getString("score"));
                    transaDetails.put("Amount",rs.getString("amount"));
                    /*if("T".equals(rs.getString("isReversed")))
                        transaDetails.put("Description","Transaction is reversed successfully by cron");*/
                    transaDetails.put("Description","Transaction to be Reversed by Admin");



                    listOfTransactionsForAdmin = adminFraudIntimationTransactions.get(String.valueOf(rs.getInt("memberid")));
                    if(listOfTransactionsForAdmin==null)
                    {
                        listOfTransactionsForAdmin = new LinkedHashMap<String,Map<String,String>>();
                        listOfTransactionsForAdmin.put((String) transaDetails.get("TrackingId"), transaDetails);
                    }
                    else
                    {
                        listOfTransactionsForAdmin.put((String) transaDetails.get("TrackingId"), transaDetails);
                    }
                    adminFraudIntimationTransactions.put(String.valueOf(rs.getInt("memberid")), listOfTransactionsForAdmin);
                }

            }
        }
        catch (SQLException se)
        {
            logger.error(se);
        }
        catch(SystemError e)
        {
            logger.error(e);
        }
        finally
        {
            Database.closeConnection(con);
        }

        mailList.put("merchantsRefundTransactions",merchantsRefundTransactions);

        mailList.put("merchantsFraudIntimationTransactions",merchantsFraudIntimationTransactions);

        mailList.put("adminFraudIntimationTransactions",adminFraudIntimationTransactions);

        return mailList;
    }
    public void updateIsAlertSentStatus(Set trackingIds)
    {
        Connection con = null;
        StringBuffer query = new StringBuffer();
        StringBuffer sb = new StringBuffer();

        int i = 0;
        Iterator itr = trackingIds.iterator();
        while (itr.hasNext())
        {
            if(i!=0)
            {
                sb.append(",");

            }
            sb.append(itr.next());
            i++;
        }
        query.append("update fraud_transaction set isAlertSent='Y' where status='I' AND trackingid IN ");
        query.append("(");
        query.append(sb.toString());
        query.append(")");
        logger.debug("===" + query.toString());
        try
        {
            con=Database.getConnection();
            Database.executeUpdate(query.toString(),con);

        }
        catch(SystemError e)
        {
            logger.error("SystemError :::::::  ", e);
        }
        finally
        {
            Database.closeConnection(con);
        }

    }
    public void updateIsAlertSentForRefund(Set trackingIds)
    {
        Connection con = null;
        StringBuffer query = new StringBuffer();
        StringBuffer sb = new StringBuffer();

        int i = 0;
        Iterator itr = trackingIds.iterator();
        while (itr.hasNext())
        {
            if(i!=0)
            {
                sb.append(",");

            }
            sb.append(itr.next());
            i++;
        }
        query.append("update fraud_transaction set isAlertSent='Y' where trackingid IN ");
        query.append("(");
        query.append(sb.toString());
        query.append(")");
        logger.debug("===" + query.toString());
        try
        {
            con=Database.getConnection();
            Database.executeUpdate(query.toString(),con);

        }
        catch(SystemError e)
        {
            logger.error("SystemError :::::::  ", e);
        }
        finally
        {
            Database.closeConnection(con);
        }

    }
    public void updateIsReversedStatus(List trackingIds)
    {
        Connection con = null;
        StringBuffer query = new StringBuffer();
        StringBuffer sb = new StringBuffer();

        int i = 0;
        Iterator itr = trackingIds.iterator();
        while (itr.hasNext())
        {
            if(i!=0)
            {
                sb.append(",");

            }
            sb.append(itr.next());
            i++;
        }
        query.append("update fraud_transaction set isReversed='T' where trackingid IN ");
        query.append("(");
        query.append(sb.toString());
        query.append(")");
        logger.debug("===" + query.toString());
        try
        {
            con=Database.getConnection();
            int k=Database.executeUpdate(query.toString(),con);
            logger.debug(k+" :Recoreds Updated Successfully");

        }
        catch(SystemError e)
        {
            logger.error("SystemError :::::::  ", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
    public Set getAccountsIdByMemberId(String memberId)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        Set<String> accountIdSet=null;
        try
        {
            con= Database.getRDBConnection();
            String Query = "SELECT accountid FROM member_account_mapping WHERE memberid=?";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,memberId);
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                accountIdSet=new HashSet<String>();
                do
                {
                    accountIdSet.add(rs.getString("accountid"));
                }while(rs.next());
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving", se);
        }
        catch(SystemError e)
        {
            logger.error("SystemError ::::::: Leaving ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return accountIdSet;
    }
    /*public int getFraudTransactionDetails(String memberId,String pzFraudTransactionId,PZFraudStatus status)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        int fsTransId=0;
        try
        {
            con= Database.getConnection();
            String Query = "select fstransid from fraud_transaction where fraud_transaction_id=? and fraud_trans_status=? and memberid=?";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,pzFraudTransactionId);
            preparedStatement.setString(2,status.toString());
            preparedStatement.setString(3,memberId);
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
              fsTransId=rs.getInt("fstransid");
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving", se);
        }
        catch(SystemError e)
        {
            logger.error("SystemError ::::::: Leaving ", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return fsTransId;
    }*/

    public void updateStatusFlagForOnlineCheck(String isAlertSent,String isReversed,String status,String trackingId)
    {
        Connection con = null;
        PreparedStatement preparedStatement=null;
        String query=null;
        query="update fraud_transaction set isAlertSent=?,isReversed=?,status=? where trackingid=? ";
        try
        {
            con=Database.getConnection();
            preparedStatement=con.prepareStatement(query);
            preparedStatement.setString(1,isAlertSent);
            preparedStatement.setString(2,isReversed);
            preparedStatement.setString(3,status);
            preparedStatement.setString(4,trackingId);
            int k=preparedStatement.executeUpdate();
            logger.debug(k+" :Recoreds Updated Successfully");
        }
        catch(SystemError e)
        {
            logger.error("SystemError :::::::  ", e);
        }
        catch (SQLException se)
        {
            logger.error("SQLException::::"+se);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
    public Map<String,Map<String,String>> getHighRiskRefundTransactionDetailsForAlertPerTrackingId(List trackingIds)
    {

        Map<String,Map<String,String>> listOfRefundedTransactions = null;

        Map transaDetails=null;

        Connection con = null;

        PreparedStatement preparedStatement = null;

        ResultSet rs=null;
        StringBuffer sb = new StringBuffer();

        int i = 0;
        Iterator itr = trackingIds.iterator();
        while (itr.hasNext())
        {
            if(i!=0)
            {
                sb.append(",");

            }
            sb.append(itr.next());
            i++;
        }

        try
        {
            con= Database.getConnection();
            String Query = "SELECT ft.trackingid,ft.memberid,ft.isReversed,ft.isAlertSent,ft.status,aftd.amount,aftd.score from fraud_transaction as ft join at_fraud_trans_details as aftd on ft.fraud_transaction_id=aftd.fraud_trans_id where isAlertSent=? and ft.trackingid in("+sb.toString()+")";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,"N");
            logger.error("getHighRiskRefundTransactionDetailsForAlertPerTrackingId--->"+preparedStatement);
            rs=preparedStatement.executeQuery();
            while (rs.next())
            {
                if("T".equals(rs.getString("isReversed")))
                {
                    transaDetails = new LinkedHashMap();
                    transaDetails.put("TrackingId",String.valueOf(rs.getInt("trackingid")));
                    transaDetails.put("MemberId",String.valueOf(rs.getInt("memberid")));
                    transaDetails.put("Score",rs.getString("score"));
                    transaDetails.put("Amount",rs.getString("amount"));
                    transaDetails.put("Transaction Status","Reversed");
                    transaDetails.put("Refund Reason","High risk fraud transaction");

                    if(listOfRefundedTransactions==null)
                    {
                        listOfRefundedTransactions = new LinkedHashMap<String, Map<String, String>>();
                        listOfRefundedTransactions.put((String) transaDetails.get("TrackingId"), transaDetails);
                    }
                    else
                    {
                        listOfRefundedTransactions.put((String) transaDetails.get("TrackingId"), transaDetails);
                    }
                }
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException--->",se);
        }
        catch(SystemError e)
        {
            logger.error("SystemError--->",e);
        }
        finally
        {
            Database.closeConnection(con);
        }


        return listOfRefundedTransactions;
    }
}
