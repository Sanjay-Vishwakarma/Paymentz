package com.payment.fraudAPI;
import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayTypeService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.logicboxes.util.Util;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 5/20/14
 * Time: 9:04 PM
 * To change this template use File | Settings | File Templates.
 */

public class FraudTransactionDBA
{
    static Logger logger = new Logger(FraudTransactionDBA.class.getName());

    private static Hashtable<String, String> MerchantMaxScore;

    private static Hashtable<String, String> MerchantMaxScoreReversal;
    AuditTrailVO auditTrailVO=new AuditTrailVO();
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

    public int getMaxTrackingidForFraud()
    {
        int sMaxTrackingId=0;
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String query = "SELECT MAX(tracking_id) as maxTrackingId FROM frauddetails";
            PreparedStatement p1=con.prepareStatement(query);
            ResultSet rs = p1.executeQuery();

            if(rs.next())
            {
                sMaxTrackingId= rs.getInt("maxTrackingId");
            }
            return sMaxTrackingId;
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

        return sMaxTrackingId;
    }

    public ArrayList<HashMap> getTransactionDetailsForFraudCheck(String tablename, int trackingId)
    {
        ArrayList<HashMap> listOfTransactions = new ArrayList<HashMap>();
        HashMap transaDetails=null;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        try
        {
            con=Database.getConnection();
            //String Query = "SELECT trackingid,toid,transid,first_six,last_four,amount,T.emailaddr,description,orderdescription,dtstamp,STATUS,firstname,lastname,NAME,T.accountId,currency,ipaddress,country,city,state,street,zip,telnocc,telno, timestamp, MAM.daily_card_amount_limit, MAM.weekly_card_amount_limit, MAM.monthly_card_amount_limit, MAM.min_transaction_amount FROM "+  tablename + " AS T LEFT JOIN bin_details ON T.trackingid = bin_details.icicitransid  LEFT JOIN member_account_mapping AS MAM ON T.accountid=MAM.accountid AND T.toid=MAM.memberid AND T.paymentid=MAM.paymodeid AND T.cardtypeid = MAM.cardtypeid  WHERE  STATUS IN ('capturesuccess','settled', 'authfailed') AND trackingid >? AND first_six IS NOT NULL AND firstname IS NOT NULL AND lastname IS NOT NULL";
            String Query = "SELECT DISTINCT trackingid,toid,first_six,last_four,amount,T.emailaddr,description,orderdescription,dtstamp,STATUS,firstname,lastname,NAME,T.accountId,currency,ipaddress,country,city,state,street,zip,telnocc,telno, TIMESTAMP, MAM.daily_card_amount_limit , MAM.weekly_card_amount_limit, MAM.monthly_card_amount_limit, MAM.min_transaction_amount FROM "+  tablename + " AS T JOIN bin_details ON T.trackingid = bin_details.icicitransid  LEFT JOIN member_account_mapping AS MAM ON T.accountid=MAM.accountid AND T.toid=MAM.memberid AND T.paymodeid=MAM.paymodeid AND T.cardtypeid = MAM.cardtypeid  WHERE  STATUS IN ('capturesuccess','settled') AND trackingid >? AND T.firstname != \"\" AND T.`firstname` IS NOT NULL AND T.`lastname`!=\"\" AND T.lastname IS NOT NULL AND first_six IS NOT NULL limit 5";//306 is accountid of CredoraxPaymentGateway which currnt not available
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setInt(1,trackingId);
            rs=preparedStatement.executeQuery();
            while (rs.next())
            {
                transaDetails = new HashMap();
                //Mandatory Fields
                transaDetails.put("memberid",String.valueOf(rs.getInt("toid")));
                transaDetails.put("trackingid",String.valueOf(rs.getInt("trackingid")));
                transaDetails.put("bin",rs.getString("first_six"));
                transaDetails.put("ip",rs.getString("ipaddress"));
                transaDetails.put("last_4",rs.getString("last_four"));
                transaDetails.put("amount",rs.getString("amount"));
                transaDetails.put("trans_id",String.valueOf(rs.getInt("trackingid")));
                transaDetails.put("status",rs.getString("status"));
                transaDetails.put("accountId",rs.getString("accountId"));
                transaDetails.put("currency",rs.getString("currency"));
                transaDetails.put("time","2014-05-23 23:53:16");
                transaDetails.put("device_id","");

                transaDetails.put("deposit_limits[pay_method_type]","CC");
                transaDetails.put("deposit_limits[dl_min]", String.valueOf(rs.getInt("min_transaction_amount")));
                transaDetails.put("deposit_limits[dl_daily]",String.valueOf(rs.getInt("daily_card_amount_limit")));
                transaDetails.put("deposit_limits[dl_weekly]",String.valueOf(rs.getInt("weekly_card_amount_limit")));
                transaDetails.put("deposit_limits[dl_monthly]",String.valueOf(rs.getInt("monthly_card_amount_limit")));

                transaDetails.put("customer_information[country]",rs.getString("country"));
                transaDetails.put("customer_information[city]",rs.getString("city"));
                transaDetails.put("customer_information[address1]",rs.getString("street"));
                transaDetails.put("customer_information[postal_code]",rs.getString("zip"));
                transaDetails.put("customer_information[email]",rs.getString("emailaddr"));
                transaDetails.put("customer_information[first_name]",rs.getString("firstname"));
                transaDetails.put("customer_information[last_name]",rs.getString("lastname"));

                transaDetails.put("payment_method[bin]",rs.getString("first_six"));
                transaDetails.put("payment_method[last_digits]",rs.getString("last_four"));

                //Optional Fields
                transaDetails.put("email",rs.getString("emailaddr"));
                transaDetails.put("description",rs.getString("description"));
                transaDetails.put("orderdescription",rs.getString("orderdescription"));
                transaDetails.put("name",rs.getString("name"));
                transaDetails.put("state",rs.getString("state"));
                transaDetails.put("telnocc",rs.getString("telnocc"));
                transaDetails.put("telno",rs.getString("telno"));

                listOfTransactions.add(transaDetails);
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
            Database.closeConnection(con);
        }
        return listOfTransactions;
    }

    public ArrayList<HashMap> getTransactionDetailsForFraudCheckUpdate(String tableName)
    {
        ArrayList<HashMap> listOfTransactions = new ArrayList<HashMap>();
        HashMap transaDetails=null;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        try
        {
            con= Database.getConnection();
            String Query = "SELECT fd.tracking_id,fd.at_trans_id,T.status  FROM frauddetails as fd  JOIN "+  tableName + " as T ON fd.tracking_id=T.trackingid WHERE (T.status IN ('chargeback') AND fd.updatechargebackdesc IS NULL AND fd.updatechargebackstatus IS NULL) OR (T.status IN ('reversed') AND fd.updatereversdesc IS NULL AND fd.updatereversstatus IS NULL)";
            preparedStatement=con.prepareStatement(Query);
            rs=preparedStatement.executeQuery();
            while (rs.next())
            {
                transaDetails = new HashMap();
                transaDetails.put("trans_id",String.valueOf(rs.getInt("tracking_id")));
                transaDetails.put("internal_trans_id",rs.getString("at_trans_id"));
                transaDetails.put("status",rs.getString("status"));
                listOfTransactions.add(transaDetails);
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
            Database.closeConnection(con);
        }
        return listOfTransactions;

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
            String Query = "SELECT * from frauddetails where isAlertSent=?";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,"N");
            rs=preparedStatement.executeQuery();
            while (rs.next())
            {
                if("R".equals(rs.getString("status")) && "T".equals(rs.getString("isReversed")))
                {
                    transaDetails = new LinkedHashMap();
                    transaDetails.put("TrackingId",String.valueOf(rs.getInt("tracking_id")));
                    transaDetails.put("MemberId",String.valueOf(rs.getInt("memberid")));
                    transaDetails.put("Score",rs.getString("new_score"));
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
                else if("I".equals(rs.getString("status")))
                {
                    transaDetails = new LinkedHashMap();
                    transaDetails.put("TrackingId",String.valueOf(rs.getInt("tracking_id")));
                    transaDetails.put("MemberId",String.valueOf(rs.getInt("memberid")));
                    transaDetails.put("Score",rs.getString("new_score"));
                    transaDetails.put("Amount",rs.getString("amount"));
                    transaDetails.put("Recommendation","Transaction must be reversed.");

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
                if("R".equals(rs.getString("status")))
                {
                    transaDetails = new LinkedHashMap();
                    transaDetails.put("TrackingId",String.valueOf(rs.getInt("tracking_id")));
                    transaDetails.put("MemberId",String.valueOf(rs.getInt("memberid")));
                    transaDetails.put("Score",rs.getString("new_score"));
                    transaDetails.put("Amount",rs.getString("amount"));
                    if("T".equals(rs.getString("isReversed")))
                        transaDetails.put("Description","Transaction is reversed successfully by cron");
                    else
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

    public HashMap getTransactionDetailsForBinCheck(String trackingid)
    {
        HashMap transaDetails = new HashMap();
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        try
        {
            con= Database.getConnection();
            String Query = "SELECT icicitransid,first_six,last_four, FROM bin_details where icicitransid ="+trackingid ;
            preparedStatement=con.prepareStatement(Query);
            rs=preparedStatement.executeQuery();
            if (rs.next())
            {
                transaDetails.put("trackingid",String.valueOf(rs.getInt("icicitransid")));
                transaDetails.put("bin",rs.getString("first_six"));
                transaDetails.put("last_4",rs.getString("last_four"));
            }
            return transaDetails;

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
        return transaDetails;
    }

    public HashMap getTransactionDetailsForEmail(String tablename, int trackingId)
    {

        HashMap transaDetails = new HashMap();
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        try
        {
            con= Database.getConnection();
            String Query = "SELECT trackingid,T.emailaddr,FROM "+  tablename + " trackingid=?";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setInt(1, trackingId);
            rs=preparedStatement.executeQuery();
            if (rs.next())
            {
                transaDetails.put("trackingid",String.valueOf(rs.getInt("trackingid")));
                transaDetails.put("email",rs.getString("emailaddr"));

            }

            return transaDetails;

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
        return transaDetails;
    }

    public boolean updateForNewTransaction(NewTransactionResponseVO responseVO, String trackingid, String memberid,String amount)
    {
        boolean isAutoRevesed=false;
        logger.debug("Inserting data in the  Fraud Table!");
        Connection con = null;
        PreparedStatement preparedStatement;
        HashMap hs=responseVO.getHashMap();
        try
        {
            java.util.Date date = new java.util.Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            con=Database.getConnection();
            String query = "INSERT INTO frauddetails (tracking_id, at_trans_id, new_status, new_description, new_recommendation, new_score, new_third_party,memberid,timestamp,isAlertSent,isReversed,status,amount) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1,trackingid);
            preparedStatement.setInt(2, new Integer(responseVO.getInternal_trans_id()).intValue());
            preparedStatement.setString(3, responseVO.getStatus().toString());
            preparedStatement.setString(4, responseVO.getDescription());
            preparedStatement.setString(5, responseVO.getRec());
            preparedStatement.setDouble(6, responseVO.getScore());
            preparedStatement.setString(7, responseVO.getThird_party());
            preparedStatement.setString(8, memberid);
            preparedStatement.setTimestamp(9, timestamp);
            if(responseVO.getScore().intValue()>Double.valueOf(MerchantMaxScore.get(memberid)).intValue() && responseVO.getScore().intValue()<Double.valueOf(MerchantMaxScoreReversal.get(memberid)).intValue()/*Double.valueOf(MerchantMaxScore.get(memberid)) && responseVO.getScore()<Double.valueOf(MerchantMaxScoreReversal.get(memberid))*/)
            {
                preparedStatement.setString(10,"N");
                preparedStatement.setString(12,"I");
                isAutoRevesed=false;
            }
            else if(responseVO.getScore().intValue()>Double.valueOf(MerchantMaxScoreReversal.get(memberid)).intValue())
            {
                preparedStatement.setString(10,"N");
                preparedStatement.setString(12,"R");
                isAutoRevesed=true;
            }
            else
            {
                preparedStatement.setString(10,"N/A");
                preparedStatement.setString(12,"N/A");
            }

            preparedStatement.setString(11,"F");
            preparedStatement.setDouble(13,Double.valueOf(amount));
            preparedStatement.executeUpdate();

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
        return isAutoRevesed;
    }
    public void updateForUpdateTransaction(String status, UpdateTransactionResponseVO responseVO)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        try
        {
            con=Database.getConnection();
            StringBuffer updateQyery=new StringBuffer("update frauddetails set");
            if("chargeback".equals(status))
            {
                updateQyery.append(" updatechargebackstatus=? ,updatechargebackdesc=?");
            }
            else if("reversed".equals(status))
            {
                updateQyery.append(" updatereversstatus=? ,updatereversdesc=?");
            }
            updateQyery.append(" where at_trans_id=?");

            logger.debug("updateQyery========"+updateQyery.toString());
            //logger.debug("update qyery..."+updateQyery);
            preparedStatement=con.prepareStatement(updateQyery.toString());
            preparedStatement.setString(1,(responseVO.getStatus().toString()));
            preparedStatement.setString(2,responseVO.getDescription());
            preparedStatement.setString(3,responseVO.getInternal_trans_id());
            int k=preparedStatement.executeUpdate();

            logger.debug(k+" Transaction is updated in frauddetails table");

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

    }


    public  void updateForBinCheck(BinCheckResponseVO responseVO, String trackingid, String bin)
    {
        logger.debug("Inserting data in the  Bin Check Table!");
        Connection con = null;
        PreparedStatement preparedStatement;
        int rs ;
        HashMap hm =responseVO.getHashMap();

        try
        {
            con=Database.getConnection();
            String query = "INSERT INTO bincheckdetails (tracking_id, first_six_digits, bin_check_id, bin_status,  bin_description, bin_bank_name,bin_country_code,bin_country,bin_category,bin_type) VALUES(?,?,?,?,?,?,?,?,?,?);";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1,trackingid);
            preparedStatement.setString(2, bin);
            preparedStatement.setString(3, responseVO.getCheck_id());
            preparedStatement.setString(4, responseVO.getStatus().toString());
            preparedStatement.setString(5,responseVO.getDescription());
            preparedStatement.setString(6, responseVO.getBan());
            preparedStatement.setDouble(7, Double.parseDouble(responseVO.getIso()));
            preparedStatement.setString(8, responseVO.getCou());
            preparedStatement.setString(9, responseVO.getCat());
            preparedStatement.setString(10, responseVO.getTyp());

            rs=preparedStatement.executeUpdate();

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

    }


    public  void updateForEmailCheck(EmailCheckResponseVO responseVO, String trackingid, String email)
    {
        logger.debug("Inserting data in the  Email Check Table!");
        Connection con = null;
        PreparedStatement preparedStatement;
        int rs ;
        HashMap hm =responseVO.getHashMap();

        try
        {
            con=Database.getConnection();
            String query = "INSERT INTO emailcheckdetails (tracking_id, email_id, email_check_id, email_status, email_description, email_date_of_first_transaction,email_date_of_last_transaction,email_no_of_chargeback,email_no_of_refund,email_blacklisted) VALUES(?,?,?,?,?,?,?,?,?,?);";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1,trackingid);
            preparedStatement.setString(2,email);
            preparedStatement.setString(3, responseVO.getCheck_id());
            preparedStatement.setString(4, responseVO.getStatus().toString());
            preparedStatement.setString(5,responseVO.getDescription());
            preparedStatement.setString(6, responseVO.getDas());
            preparedStatement.setString(7, responseVO.getDae());
            preparedStatement.setString(8, responseVO.getCbn());
            preparedStatement.setString(9, responseVO.getRen());
            preparedStatement.setString(10, responseVO.getBla());

            rs=preparedStatement.executeUpdate();

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
    }

    public  void updateFraudForCardCheck(CardNumberCheckResponseVO responseVO, String trackingid,String firstSix, String lastFour)
    {
        logger.debug("Inserting data in the  Card Check Table!");
        Connection con = null;
        PreparedStatement preparedStatement;
        int rs ;
        HashMap hm =responseVO.getHashMap();

        try
        {
            con=Database.getConnection();
            String query = "INSERT INTO cardcheckdetails (tracking_id, first_six_digits, last_four_digits, card_check_id,  card_status, card_description,card_date_of_first_transaction,card_date_of_last_transaction,card_no_of_chargeback,card_no_of_refund,card_blacklisted) VALUES(?,?,?,?,?,?,?,?,?,?,?);";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1,trackingid);
            preparedStatement.setString(2, firstSix);
            preparedStatement.setString(3, lastFour);
            preparedStatement.setString(4, responseVO.getCheck_id());
            preparedStatement.setString(5, responseVO.getStatus().toString());
            preparedStatement.setString(6,responseVO.getDescription());
            preparedStatement.setString(7, responseVO.getDas());
            preparedStatement.setString(8, responseVO.getDae());
            preparedStatement.setString(9, responseVO.getCbn());
            preparedStatement.setString(10, responseVO.getRen());
            preparedStatement.setString(11, responseVO.getBla());
            rs=preparedStatement.executeUpdate();

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
    }
    public void updateIsAlertSentStatus(Set trackingIds)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
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
        query.append("update frauddetails set isAlertSent='Y' where tracking_id IN ");
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
    public void updateIsReversedStatus(Set trackingIds)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
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
        query.append("update frauddetails set isReversed='T' where tracking_id IN ");
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

    public Hashtable reverseQwipiTransaction(String icicitransid, String refundAmount)throws SystemError
    {
        Connection conn = null;
        TransactionEntry transactionEntry = null;
        Transaction transaction = new Transaction();
        conn = Database.getConnection();
        transactionEntry = new TransactionEntry();
        String query = null;
        String icicimerchantid = null;
        String refundamount = null;
        String authamount = null;
        String captureamount = null;
        String description = null;
        String authid = null;
        String captureid = null;
        String toid = null;
        String company_name = null;
        String contact_emails = null;
        String accountId = null;
        String cardholdername = null;
        String paymentorder=null;
        String billno=null;
        BigDecimal bdConst = new BigDecimal("0.01");
        QwipiRequestVO RequestDetail=null;
        QwipiResponseVO transRespDetails=new QwipiResponseVO();
        QwipiTransDetailsVO TransDetail = new QwipiTransDetailsVO();
        GenericCardDetailsVO cardDetail= new GenericCardDetailsVO();
        QwipiAddressDetailsVO AddressDetail= new QwipiAddressDetailsVO();
        String[] icicitransidStr =null;
        String fromid=null;
        String fixamt=null;
        BigDecimal rfamt=new BigDecimal(refundAmount);
        String fraud="Y";
        String occure="Refund BY Cron FRAUD Transaction";
        Hashtable refundDetails=null;
        boolean refunded = false;
        auditTrailVO.setActionExecutorId(icicimerchantid);
        auditTrailVO.setActionExecutorName("Admin");
        try
        {
            query = "select trackingid,toid,fromid,description,amount,transid,transaction_qwipi.accountid,status,timestamp,name,qwipiPaymentOrderNumber,members.contact_emails,members.company_name from transaction_qwipi,members where status IN ('settled','capturesuccess') and transaction_qwipi.toid=members.memberid and trackingid=?";
            PreparedStatement p= conn.prepareStatement(query);
            p.setString(1,icicitransid);
            ResultSet rs = p.executeQuery();

            if (rs.next())
            {
                icicitransid = rs.getString("trackingid");
                fromid=rs.getString("fromid");
                toid = rs.getString("toid");
                billno=rs.getString("description");
                company_name = rs.getString("company_name");
                contact_emails = rs.getString("contact_emails");
                paymentorder=rs.getString("qwipiPaymentOrderNumber");
                //toid = rs.getString("toid");
                authamount = rs.getString("amount");
                captureamount = rs.getString("amount");
                accountId = rs.getString("accountid");
                cardholdername = rs.getString("name");
                String transid= rs.getString("transid");
                GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                icicimerchantid = account.getMerchantId();
                String binUpdateQuery = "update bin_details set isFraud=?, isRefund=? where icicitransid=?";
                PreparedStatement pPStmt = conn.prepareStatement(binUpdateQuery);
                description = "Refund of " + transid +"  Fraudulent Transaction";
                pPStmt.setString(1,"Y");
                pPStmt.setString(2,"Y");
                pPStmt.setString(3,icicitransid);
                pPStmt.executeUpdate();

                Hashtable qwipiKsnFlag=transaction.getMidKeyForQwipi(accountId);
                transRespDetails.setRemark(occure);
                //add charges and change status to markforreverse
                transactionEntry.newGenericRefundTransaction(icicitransid,rfamt,accountId,description,transRespDetails,auditTrailVO);

                // set perameter to call gateway
                TransDetail.setOperation("02");
                TransDetail.setPaymentOrderNo(paymentorder);
                TransDetail.setBillNo(billno);
                TransDetail.setAmount(captureamount);
                TransDetail.setRefundAmount(refundamount);
                //Now Reverse transaction on the gateway

                AbstractPaymentGateway paymentGateway = AbstractPaymentGateway.getGateway(accountId);
                RequestDetail = new QwipiRequestVO(cardDetail,AddressDetail,TransDetail);
                try
                {
                    RequestDetail.setKsnUrlFlag((String)qwipiKsnFlag.get("isksnurl"));
                    transRespDetails = (QwipiResponseVO) paymentGateway.processRefund(icicitransid, RequestDetail);


                    if (transRespDetails != null && (transRespDetails.getResultCode()).equals("0"))
                    {
                        Codec MY = new MySQLCodec(MySQLCodec.Mode.STANDARD);
                        StringBuffer sb = new StringBuffer();
                        sb.append("update transaction_qwipi set status='reversed'");
                        sb.append(",refundamount='"+ ESAPI.encoder().encodeForSQL(MY,transRespDetails.getRefundAmount())+"'");
                        sb.append(",refundcode='"+ESAPI.encoder().encodeForSQL(MY,transRespDetails.getResultCode())+"'");

                        sb.append(" where trackingid=" +ESAPI.encoder().encodeForSQL(MY,icicitransid)+ " and status = 'markedforreversal'");

                        int rows =new Database().executeUpdate(sb.toString(), conn);
                        logger.debug("No of Rows updated : " + rows + "<br>");

                        if (rows == 1)
                        {
                            refunded = true;
                        }
                        refundDetails = new Hashtable();
                        refundDetails.put("icicitransid",icicitransid);
                        refundDetails.put("captureamount",captureamount);
                        refundDetails.put("refundamount",transRespDetails.getRefundAmount());
                        refundDetails.put("description",transRespDetails.getPaymentOrderNo());
                        refundDetails.put("accountid",accountId);
                        refundDetails.put("cardholdername",cardholdername);

                        ActionEntry entry = new ActionEntry();
                        int actionEntry = entry.actionEntryForQwipi(icicitransid,refundamount,ActionEntry.ACTION_REVERSAL_SUCCESSFUL,ActionEntry.STATUS_REVERSAL_SUCCESSFUL,null,transRespDetails,auditTrailVO);
                        entry.closeConnection();
                    }
                    else
                    {
                        throw new SystemError();
                    }

                }
                catch (SystemError se)
                {
                    throw new SystemError();
                }
            }
        }
        catch (SystemError se)
        {
            throw new SystemError();
        }
        catch (Exception e)
        {
            throw new SystemError();
        }//try catch ends
        finally
        {
            Database.closeConnection(conn);
        }

        return refundDetails;

    }

    public static void loadFraudTransactionThresholdForMerchant() throws Exception
    {
        MerchantMaxScore = new Hashtable<String, String>();
        MerchantMaxScoreReversal = new Hashtable<String, String>();

        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("SELECT memberId,maxScoreAllowed,maxScoreAutoReversal,clkey FROM members",conn);
            while (rs.next())
            {
                MerchantMaxScore.put(rs.getString("memberId"),rs.getString("maxScoreAllowed"));
                MerchantMaxScoreReversal.put(rs.getString("memberId"),rs.getString("maxScoreAutoReversal"));

            }
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

}
