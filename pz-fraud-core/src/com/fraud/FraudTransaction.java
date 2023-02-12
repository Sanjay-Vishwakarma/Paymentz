package com.fraud;
import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.fraud.at.ATResponseVO;
import com.fraud.fourstop.FourStopRequestVO;
import com.fraud.fourstop.FourStopResponseVO;
import com.fraud.vo.PZFraudRequestVO;
import com.manager.dao.FraudTransactionDAO;
import com.payment.AbstractPaymentProcess;
import com.payment.PaymentProcessFactory;
import com.payment.ecore.core.EcorePaymentProcess;
import com.payment.ecore.core.request.EcoreRefundRequest;
import com.payment.request.PZRefundRequest;
import com.payment.response.PZRefundResponse;
import com.payment.response.PZResponseStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 9/15/14
 * Time: 7:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class FraudTransaction
{
    static Logger logger = new Logger(FraudTransaction.class.getName());
    static TransactionLogger transactionLogger = new TransactionLogger(FraudTransaction.class.getName());
    Functions functions=new Functions();

    public int fraudTransactionEntry(PZFraudRequestVO pzFraudRequestVO, String fsid)
    {
        int fraudTransId = 0;
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String query="insert into fraud_transaction(trackingid,memberid,accountid,fsid,member_transid,firstname,lastname,emailaddr,address1,city,state,countrycode,zip,phone,ipaddrs,firstsix,lastfour,dailycardminlimit,dailycardlimit,weeklycardlimit,monthlycardlimit,paymenttype,partnerid,website,username,usernumber,currency,amount,fraud_trans_status,attempts,dtstamp) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,1,unix_timestamp(now()))";
            PreparedStatement p=con.prepareStatement(query);
            // p.setString(1,pzFraudRequestVO.getTrackingid());
            if(functions.isValueNull(pzFraudRequestVO.getTrackingid()))
            {
                p.setString(1,pzFraudRequestVO.getTrackingid());
            }
            else{
                p.setNull(1, Types.VARCHAR);

            }
            p.setString(2,pzFraudRequestVO.getMemberid());
            p.setString(3, pzFraudRequestVO.getAccountid());
           /* if (functions.isValueNull(pzFraudRequestVO.getAccountid()))
            {
                p.setString(3, pzFraudRequestVO.getAccountid());
            }
            else {
                p.setNull(3, Types.VARCHAR);
            }*/
            p.setString(4,fsid);
            p.setString(5,pzFraudRequestVO.getDescription());
            p.setString(6,pzFraudRequestVO.getFirstname());
            p.setString(7,pzFraudRequestVO.getLastname());
            p.setString(8,pzFraudRequestVO.getEmailaddr());
            p.setString(9,pzFraudRequestVO.getAddress1());
            p.setString(10,pzFraudRequestVO.getCity());
            p.setString(11,pzFraudRequestVO.getState());
            p.setString(12,pzFraudRequestVO.getCountrycode());
            p.setString(13,pzFraudRequestVO.getZip());
            p.setString(14,pzFraudRequestVO.getPhone());
            p.setString(15,pzFraudRequestVO.getIpaddrs());
            p.setString(16,pzFraudRequestVO.getFirstsix());
            p.setString(17,pzFraudRequestVO.getLastfour());
            p.setString(18,pzFraudRequestVO.getDailycardminlimit());
            p.setString(19,pzFraudRequestVO.getDailycardlimit());
            p.setString(20,pzFraudRequestVO.getWeeklycardlimit());
            p.setString(21,pzFraudRequestVO.getMonthlycardlimit());
            p.setString(22,pzFraudRequestVO.getPaymenttype());
            p.setString(23,pzFraudRequestVO.getPartnerid());
            p.setString(24,pzFraudRequestVO.getWebsite());
            p.setString(25,pzFraudRequestVO.getUsername());
            p.setString(26,pzFraudRequestVO.getUsernumber());
            p.setString(27,pzFraudRequestVO.getCurrency());
            p.setString(28,pzFraudRequestVO.getAmount());
            p.setString(29,"Pending");
            int num = p.executeUpdate();
            transactionLogger.debug("Pending fraudTransactionEntry---"+p);
            if (num ==1)
            {
                ResultSet rs = p.getGeneratedKeys();
                if(rs.next())
                {
                    fraudTransId = rs.getInt(1);
                }
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving FraudTransaction", se);
        }
        catch(SystemError e)
        {
            logger.error("SystemError ::::::: Leaving FraudTransaction", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return fraudTransId;
    }
    public int fraudActionEntryforInternalFraud(String fraudTransId)
    {
        FraudTransactionDAO transactionDAO=new FraudTransactionDAO();

        Connection con = null;
        PreparedStatement preparedStatement = null;
        int k=0;
        try
        {
            con= Database.getConnection();
            StringBuffer query=new StringBuffer("update fraud_transaction set fraud_trans_status='Process Successfully',isAlertSent='N/A',status='N/A' where fraud_transaction_id=?");

            preparedStatement=con.prepareStatement(query.toString());
            preparedStatement.setString(1,fraudTransId);
            k=preparedStatement.executeUpdate();
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving FraudTransaction", se);
        }
        catch(SystemError e)
        {
            logger.error("SystemError ::::::: Leaving FraudTransaction", e);
        }
        catch(Exception e)
        {
            logger.error("SystemError ::::::: Leaving FraudTransaction", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return k;
    }


    public int fraudActionEntry(ATResponseVO atResponseVO, String fraudTransId, String trackingId, String amount, String memberId)
    {
        FraudTransactionDAO transactionDAO=new FraudTransactionDAO();
        String fsTransId=atResponseVO.getInternal_trans_id();
        Double score=atResponseVO.getScore();
        String fsResponseCode=atResponseVO.getStatus();
        String fsResponseDesc=atResponseVO.getDescription();
        String fsResponseRec=atResponseVO.getRec();
        JSONObject jsonObject=atResponseVO.getJsonObject();
        Connection con = null;
        PreparedStatement preparedStatement = null;
        int k=0;
        try
        {
            con= Database.getConnection();
            StringBuffer query=new StringBuffer("update fraud_transaction set ");
            if("0".equals(fsResponseCode))
            {
                query.append(" fstransid="+fsTransId);
                query.append(",fraud_trans_status='Process Successfully'");
                if(Double.valueOf(score).intValue()>Integer.parseInt(transactionDAO.getMerchantMaxScoreAllowed(memberId)) && Double.valueOf(score).intValue()<Integer.parseInt(transactionDAO.getMerchantAutoReversalScore(memberId)))
                {
                    query.append(" ,isAlertSent='N',status='I'");
                }
                else if(Double.valueOf(score).intValue()>Integer.parseInt(transactionDAO.getMerchantAutoReversalScore(memberId)))
                {
                    query.append(" ,isAlertSent='N',status='R'");
                }
                else
                {
                    query.append(" ,isAlertSent='N/A',status='N/A'");
                }

            }
            else
            {
                query.append(" ,fraud_trans_status='Process Failed',isAlertSent='N/A',status='N/A',isReversed='N/A'");
            }
            query.append(" where fraud_transaction_id=?");
            preparedStatement=con.prepareStatement(query.toString());
            preparedStatement.setString(1,fraudTransId);
            k=preparedStatement.executeUpdate();
            if(k>0)
            {
                if("0".equals(fsResponseCode))
                {
                    String sql = "insert into at_fraud_trans_details(fraud_trans_id,trackingid,fs_transid,score,fs_responsecode,fs_responsedesc,fs_responserec,amount,fraud_trans_status) values (?,?,?,?,?,?,?,?,?)";
                    preparedStatement= con.prepareStatement(sql);
                    preparedStatement.setString(1,fraudTransId);
                    preparedStatement.setString(2,trackingId);
                    preparedStatement.setString(3,fsTransId);
                    preparedStatement.setDouble(4,score);
                    preparedStatement.setString(5,fsResponseCode);
                    preparedStatement.setString(6,fsResponseDesc);
                    preparedStatement.setString(7,fsResponseRec);
                    preparedStatement.setString(8,amount);
                    preparedStatement.setString(9,"Process Successfully");
                    /*else
                    {
                        preparedStatement.setString(9,"Process Failed");
                    }*/
                }
                else
                {
                    String sql = "insert into at_fraud_trans_details(fraud_trans_id,trackingid,score,fs_responsecode,fs_responsedesc,amount,fraud_trans_status) values (?,?,?,?,?,?,?)";
                    preparedStatement= con.prepareStatement(sql);
                    preparedStatement.setString(1,fraudTransId);
                    preparedStatement.setString(2,trackingId);
                    preparedStatement.setString(3,String.valueOf(score));
                    preparedStatement.setString(4,fsResponseCode);
                    preparedStatement.setString(5,fsResponseDesc);
                    preparedStatement.setString(6,amount);
                    preparedStatement.setString(7,"Process Failed");
                }
                int results = preparedStatement.executeUpdate();
                if(results>0)
                {
                    logger.debug("Fraud Transaction Action Entry Done");
                    if(jsonObject!=null && jsonObject.has("rules_triggered"))
                    {
                        JSONArray arr =jsonObject.getJSONArray("rules_triggered");
                        for (int j = 0; j < arr.length(); j++)
                        {
                            String ruleName = arr.getJSONObject(j).getString("name");
                            String ruleScore = arr.getJSONObject(j).getString("score");
                            String sql = "INSERT INTO fraudtransaction_rules_triggered(id,rulename,rulescore,fraud_transid)VALUES(NULL,?,?,?);";
                            preparedStatement= con.prepareStatement(sql);
                            preparedStatement.setString(1,ruleName);
                            preparedStatement.setString(2,ruleScore);
                            preparedStatement.setString(3,fraudTransId);
                            int triggeredRule=preparedStatement.executeUpdate();

                        }
                    }
                }
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving FraudTransaction", se);
        }
        catch(SystemError e)
        {
            logger.error("SystemError ::::::: Leaving FraudTransaction", e);
        }
        catch(Exception e)
        {
            logger.error("SystemError ::::::: Leaving FraudTransaction", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return k;
    }
    public int fraudActionEntryNew(ATResponseVO atResponseVO, String fraudTransId, String trackingId, String amount, String memberId)
    {
        FraudTransactionDAO transactionDAO=new FraudTransactionDAO();
        String fsTransId=atResponseVO.getInternal_trans_id();
        Double score=atResponseVO.getScore();
        String fsResponseCode=atResponseVO.getStatus();
        String fsResponseDesc=atResponseVO.getDescription();
        String fsResponseRec=atResponseVO.getRec();
        JSONObject jsonObject=atResponseVO.getJsonObject();
        Connection con = null;
        PreparedStatement preparedStatement = null;
        int k=0;
        try
        {
            con= Database.getConnection();
            StringBuffer query=new StringBuffer("update fraud_transaction set maxScore="+transactionDAO.getMerchantMaxScoreAllowed(memberId)+",autoReversalScore="+transactionDAO.getMerchantAutoReversalScore(memberId));
            if("0".equals(fsResponseCode))
            {
                query.append(" ,fstransid="+fsTransId);
                query.append(",fraud_trans_status='Process Successfully'");

                if(Double.valueOf(score)>Double.valueOf(transactionDAO.getMerchantMaxScoreAllowed(memberId)) && Double.valueOf(score)<Double.valueOf(transactionDAO.getMerchantAutoReversalScore(memberId)))
                {
                    logger.debug("inside alert intimation");
                    query.append(" ,isAlertSent='N',status='I'");

                }
                else if(Double.valueOf(score)>Double.valueOf(transactionDAO.getMerchantAutoReversalScore(memberId)))
                {
                    logger.debug("inside refund intimation");
                    query.append(" ,isAlertSent='N',status='R'");
                }
                else
                {
                    logger.debug("inside all well section");
                    query.append(" ,isAlertSent='N/A',status='N/A'");
                }
            }
            else
            {
                query.append(" ,fraud_trans_status='Process Failed',isAlertSent='N/A',status='N/A',isReversed='N/A'");
            }
            query.append(" where fraud_transaction_id=?");
            preparedStatement=con.prepareStatement(query.toString());
            preparedStatement.setString(1,fraudTransId);
            k=preparedStatement.executeUpdate();
            if(k>0)
            {
                if("0".equals(fsResponseCode))
                {
                    String sql = "insert into at_fraud_trans_details(fraud_trans_id,trackingid,fs_transid,score,fs_responsecode,fs_responsedesc,fs_responserec,amount,fraud_trans_status) values (?,?,?,?,?,?,?,?,?)";
                    preparedStatement= con.prepareStatement(sql);
                    preparedStatement.setString(1,fraudTransId);
                    preparedStatement.setString(2,trackingId);
                    preparedStatement.setString(3,fsTransId);
                    preparedStatement.setDouble(4,score);
                    preparedStatement.setString(5,fsResponseCode);
                    preparedStatement.setString(6,fsResponseDesc);
                    preparedStatement.setString(7,fsResponseRec);
                    preparedStatement.setString(8,amount);
                    preparedStatement.setString(9,"Process Successfully");
                    /*else
                    {
                        preparedStatement.setString(9,"Process Failed");
                    }*/
                }
                else
                {
                    String sql = "insert into at_fraud_trans_details(fraud_trans_id,trackingid,score,fs_responsecode,fs_responsedesc,amount,fraud_trans_status) values (?,?,?,?,?,?,?)";
                    preparedStatement= con.prepareStatement(sql);
                    preparedStatement.setString(1,fraudTransId);
                    preparedStatement.setString(2,trackingId);
                    preparedStatement.setString(3,String.valueOf(score));
                    preparedStatement.setString(4,fsResponseCode);
                    preparedStatement.setString(5,fsResponseDesc);
                    preparedStatement.setString(6,amount);
                    preparedStatement.setString(7,"Process Failed");
                }
                int results = preparedStatement.executeUpdate();
                if(results>0)
                {
                    logger.debug("Fraud Transaction Action Entry Done");
                    if(jsonObject!=null && jsonObject.has("rules_triggered"))
                    {
                        JSONArray arr =jsonObject.getJSONArray("rules_triggered");
                        for (int j = 0; j < arr.length(); j++)
                        {
                            String ruleName = arr.getJSONObject(j).getString("name");
                            String ruleScore = arr.getJSONObject(j).getString("score");
                            String sql = "INSERT INTO fraudtransaction_rules_triggered(id,rulename,rulescore,fraud_transid)VALUES(NULL,?,?,?);";
                            preparedStatement= con.prepareStatement(sql);
                            preparedStatement.setString(1,ruleName);
                            preparedStatement.setString(2,ruleScore);
                            preparedStatement.setString(3,fraudTransId);
                            int triggeredRule=preparedStatement.executeUpdate();

                        }
                    }
                }
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving FraudTransaction", se);
        }
        catch(SystemError e)
        {
            logger.error("SystemError ::::::: Leaving FraudTransaction", e);
        }
        catch(Exception e)
        {
            logger.error("SystemError ::::::: Leaving FraudTransaction", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return k;
    }

    public int fraudActionEntryNewForFourStop(FourStopRequestVO fourStopRequestVO,FourStopResponseVO fourStopResponseVO, String fraudTransId, String trackingId, String amount, String memberId)
    {
        FraudTransactionDAO transactionDAO=new FraudTransactionDAO();
        String fsTransId=fourStopResponseVO.getInternal_trans_id();
        Double score=fourStopResponseVO.getScore();
        String fsResponseCode=fourStopResponseVO.getStatus();
        String fsResponseDesc=fourStopResponseVO.getDescription();
        String fsResponseRec=fourStopResponseVO.getRec();
        JSONObject jsonObject=fourStopResponseVO.getJsonObject();
        Connection con = null;
        PreparedStatement preparedStatement = null;
        int k=0;
        try
        {
            con= Database.getConnection();
            StringBuffer query=new StringBuffer("update fraud_transaction set maxScore="+transactionDAO.getMerchantMaxScoreAllowed(memberId)+",autoReversalScore="+transactionDAO.getMerchantAutoReversalScore(memberId));
            if("0".equals(fsResponseCode))
            {
                query.append(" ,fstransid="+fsTransId);
                query.append(",fraud_trans_status='Process Successfully'");

                if(Double.valueOf(score)>Double.valueOf(transactionDAO.getMerchantMaxScoreAllowed(memberId)) && Double.valueOf(score)<Double.valueOf(transactionDAO.getMerchantAutoReversalScore(memberId)))
                {
                    logger.debug("inside alert intimation");
                    query.append(" ,isAlertSent='N',status='I'");

                }
                else if(Double.valueOf(score)>Double.valueOf(transactionDAO.getMerchantAutoReversalScore(memberId)))
                {
                    logger.debug("inside refund intimation");
                    query.append(" ,isAlertSent='N',status='R'");
                }
                else
                {
                    logger.debug("inside all well section");
                    query.append(" ,isAlertSent='N/A',status='N/A'");
                }
            }
            else
            {
                query.append(" ,fraud_trans_status='Process Failed',isAlertSent='N/A',status='N/A',isReversed='N/A'");
            }
            query.append(" where fraud_transaction_id=?");
            preparedStatement=con.prepareStatement(query.toString());
            preparedStatement.setString(1,fraudTransId);
            k=preparedStatement.executeUpdate();
            if(k>0)
            {
                if("0".equals(fsResponseCode))
                {
                    String sql = "insert into at_fraud_trans_details(fraud_trans_id,trackingid,fs_transid,score,fs_responsecode,fs_responsedesc,fs_responserec,amount,fraud_trans_status) values (?,?,?,?,?,?,?,?,?)";
                    preparedStatement= con.prepareStatement(sql);
                    preparedStatement.setString(1,fraudTransId);
                    preparedStatement.setString(2,trackingId);
                    preparedStatement.setString(3,fsTransId);
                    preparedStatement.setDouble(4,score);
                    preparedStatement.setString(5,fsResponseCode);
                    preparedStatement.setString(6,fsResponseDesc);
                    preparedStatement.setString(7,fsResponseRec);
                    preparedStatement.setString(8,amount);
                    preparedStatement.setString(9,"Process Successfully");
                    /*else
                    {
                        preparedStatement.setString(9,"Process Failed");
                    }*/
                }
                else
                {
                    String sql = "insert into at_fraud_trans_details(fraud_trans_id,trackingid,score,fs_responsecode,fs_responsedesc,amount,fraud_trans_status) values (?,?,?,?,?,?,?)";
                    preparedStatement= con.prepareStatement(sql);
                    preparedStatement.setString(1,fraudTransId);
                    preparedStatement.setString(2,trackingId);
                    preparedStatement.setString(3,String.valueOf(score));
                    preparedStatement.setString(4,fsResponseCode);
                    preparedStatement.setString(5,fsResponseDesc);
                    preparedStatement.setString(6,amount);
                    preparedStatement.setString(7,"Process Failed");
                }
                int results = preparedStatement.executeUpdate();
                if(results>0)
                {
                    logger.debug("Fraud Transaction Action Entry Done");
                    if(jsonObject!=null)
                    {
                        if(jsonObject.has("rules_triggered"))
                        {
                            JSONArray arr =jsonObject.getJSONArray("rules_triggered");
                            for (int j = 0; j < arr.length(); j++)
                            {
                                String ruleName = arr.getJSONObject(j).getString("name");
                                String ruleScore = arr.getJSONObject(j).getString("score");
                                String sql = "INSERT INTO fraudtransaction_rules_triggered(id,rulename,rulescore,fraud_transid)VALUES(NULL,?,?,?);";
                                preparedStatement= con.prepareStatement(sql);
                                preparedStatement.setString(1,ruleName);
                                preparedStatement.setString(2,ruleScore);
                                preparedStatement.setString(3,fraudTransId);
                                int triggeredRule=preparedStatement.executeUpdate();
                            }
                        }
                        if(jsonObject.has("bin_information"))
                        {
                            //JSONArray arr =jsonObject.getJSONArray("bin_information");
                            JSONObject  object=jsonObject.getJSONObject("bin_information");

                            String strQuery = "select id from fraud_bin_base WHERE first_six=? AND last_four=?";
                            preparedStatement=con.prepareStatement(strQuery);
                            preparedStatement.setString(1, fourStopRequestVO.getPayment_method_bin());
                            preparedStatement.setString(2, fourStopRequestVO.getPayment_method_last_digits());
                            ResultSet rs=preparedStatement.executeQuery();
                            if(!rs.next())
                            {
                                String bankName = object.getString("bank_name");
                                String bankLocation =object.getString("bank_location");
                                String cardType = object.getString("card_type");
                                String cardLevel = object.getString("card_level");
                                String isoCardCountry =object.getString("iso_card_country");

                                String sql = "INSERT INTO fraud_bin_base(id,first_six,last_four,cardtype,bank_name,bank_location,card_level,card_country,first_name,last_name,emailid)VALUES(NULL,?,?,?,?,?,?,?,?,?,?);";
                                preparedStatement= con.prepareStatement(sql);
                                preparedStatement.setString(1,fourStopRequestVO.getPayment_method_bin());
                                preparedStatement.setString(2,fourStopRequestVO.getPayment_method_last_digits());
                                preparedStatement.setString(3,cardType);
                                preparedStatement.setString(4,bankName);
                                preparedStatement.setString(5,bankLocation);
                                preparedStatement.setString(6,cardLevel);
                                preparedStatement.setString(7,isoCardCountry);
                                preparedStatement.setString(8,fourStopRequestVO.getCustomer_information_first_name());
                                preparedStatement.setString(9,fourStopRequestVO.getCustomer_information_last_name());
                                preparedStatement.setString(10,fourStopRequestVO.getCustomer_information_email());
                                int kl=preparedStatement.executeUpdate();
                            }
                        }
                    }
                }
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving FraudTransaction", se);
        }
        catch(SystemError e)
        {
            logger.error("SystemError ::::::: Leaving FraudTransaction", e);
        }
        catch(Exception e)
        {
            logger.error("SystemError ::::::: Leaving FraudTransaction", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return k;
    }

    public void updateFraudTransactionEntry(String memberId,String trackingId,String orderId)
    {
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String query="update fraud_transaction set fraud_trans_status=?,attempts=attempts+1 where memberid=? and trackingid=? or member_transid=?";
            PreparedStatement preparedStatement=con.prepareStatement(query);
            preparedStatement.setString(1,"Pending");
            preparedStatement.setString(2,memberId);
            preparedStatement.setString(3,trackingId);
            preparedStatement.setString(4,orderId);
            int num = preparedStatement.executeUpdate();
            if (num>0)
            {
                //System.out.println("Member Record In Fraud Transaction in Updated");
                logger.debug("Member Record In Fraud Transaction in Updated");
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving FraudTransaction", se);
        }
        catch(SystemError e)
        {
            logger.error("SystemError ::::::: Leaving FraudTransaction", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
    public int checkForTransExits(String memberId,String trackingId,String orderId)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        int fraudTransId=0;
        try
        {
            con= Database.getConnection();
            String Query = "select fraud_transaction_id from fraud_transaction where memberid=? and trackingid=? or member_transid=?";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,memberId);
            preparedStatement.setString(2,trackingId);
            preparedStatement.setString(3,orderId);
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                fraudTransId=rs.getInt("fraud_transaction_id");
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
            Database.closeConnection(con);
        }
        return fraudTransId;
    }

    public int updateFraudActionEntry(String status,ATResponseVO atResponseVO)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        int k=0;
        try
        {
            con=Database.getConnection();
            StringBuffer updateQyery=new StringBuffer("update at_fraud_trans_details set");
            if("chargeback".equals(status))
            {
                updateQyery.append(" updatechargebackstatus=? ,updatechargebackdesc=?");
            }
            else if("reversed".equals(status))
            {
                updateQyery.append(" updatereversstatus=? ,updatereversdesc=?");
            }
            updateQyery.append(" where fs_transid=?");

            logger.debug("updateQyery========"+updateQyery.toString());
            //logger.debug("update qyery..."+updateQyery);
            preparedStatement=con.prepareStatement(updateQyery.toString());
            preparedStatement.setString(1,(atResponseVO.getStatus().toString()));
            preparedStatement.setString(2,atResponseVO.getDescription());
            preparedStatement.setString(3,atResponseVO.getInternal_trans_id());
            k=preparedStatement.executeUpdate();
            if(k>0)
            {
                //System.out.println(k+" Transaction is updated in frauddetails table");
                logger.debug(k+" Transaction is updated in frauddetails table");
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
        return k;
    }

    public int updateFraudActionEntry(String status, String updateStatus, String updateDescription, String internalTransId)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        int k = 0;
        try
        {
            con = Database.getConnection();
            StringBuffer updateQyery = new StringBuffer("update at_fraud_trans_details set");
            if ("chargeback".equals(status))
            {
                updateQyery.append(" updatechargebackstatus=? ,updatechargebackdesc=?");
            }
            else if ("reversed".equals(status))
            {
                updateQyery.append(" updatereversstatus=? ,updatereversdesc=?");
            }
            updateQyery.append(" where fs_transid=?");

            logger.debug("updateQyery========" + updateQyery.toString());
            //logger.debug("update qyery..."+updateQyery);
            preparedStatement = con.prepareStatement(updateQyery.toString());
            preparedStatement.setString(1, updateStatus);
            preparedStatement.setString(2, updateDescription);
            preparedStatement.setString(3, internalTransId);
            k = preparedStatement.executeUpdate();
            if (k > 0)
            {
                //System.out.println(k + " Transaction is updated in frauddetails table");
                logger.debug(k + " Transaction is updated in frauddetails table");
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
        return k;
    }
    public Hashtable getFraudTransactionDetails(String fsTransId,String fsid,String score)throws SystemError
    {
        logger.debug("fatch record 1");
        PreparedStatement pstmt;
        StringBuffer query =null;
        Hashtable hash = null;
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            query = new StringBuffer("SELECT ft.fraud_transaction_id AS fraudtransid,ft.trackingid,ft.memberid,ft.firstname,ft.lastname,ft.emailaddr,ft.address1,ft.city,ft.state,ft.countrycode,ft.zip,ft.phone,ft.ipaddrs,ft.firstsix,ft.lastfour,ft.dailycardminlimit,ft.dailycardlimit,ft.weeklycardlimit,ft.monthlycardlimit,ft.paymenttype,ft.partnerid,ft.website,ft.username,ft.usernumber,ft.currency,ft.amount,ft.fsid,ft.fstransid,ft.fraud_trans_status AS fraudtransstatus,ft.isAlertSent,ft.status AS checkstatus,ft.isReversed,FROM_UNIXTIME(ft.dtstamp) AS dtstamp ,ft.member_transid,aftd.score,aftd.amount,aftd.fs_responsecode,aftd.fs_responsedesc,aftd.fs_responserec FROM fraud_transaction ft JOIN at_fraud_trans_details AS aftd ON ft.fraud_transaction_id=aftd.fraud_trans_id WHERE ft.fraud_transaction_id>0 AND ft.fraud_transaction_id=? AND ft.fsid=? AND aftd.score=?");
            pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1,fsTransId);
            pstmt.setString(2,fsid);
            pstmt.setString(3,score);
            logger.debug(query);
            hash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());

            Hashtable fraudRuleHash = new Hashtable();
            StringBuffer fetchRule = new StringBuffer("SELECT rulename,rulescore FROM fraudtransaction_rules_triggered where fraud_transid=?");
            pstmt = conn.prepareStatement(fetchRule.toString());
            pstmt.setString(1,fsTransId);
            ResultSet rs=pstmt.executeQuery();
            while(rs.next())
            {
                String ruleName = rs.getString("rulename");
                String ruleScore = rs.getString("rulescore");
                fraudRuleHash.put(ruleName,ruleScore);
            }
            hash.put("fraudRule",fraudRuleHash);
        }
        catch (SQLException se)
        {
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        logger.debug("Leaving getFraudTransactionDetails");
        return hash;
    }
    public Hashtable listFraudTransactions(String toid, String trackingid, String fstransid, String fsid,String tdtstamp, String fdtstamp, String fstransstatus,int records, int pageno) throws SystemError
    {
        Hashtable hash = null;
        Functions functions = new Functions();
        StringBuffer query = new StringBuffer();
        StringBuffer count =new StringBuffer();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;
        Connection conn = null;
        try
        {
            //query.append("SELECT DISTINCT ft.fraud_transaction_id AS fraudtransid,ft.trackingid,ft.memberid,ft.accountid,ft.fsid,ft.fstransid,ft.fraud_trans_status AS fraudtransstatus,ft.isAlertSent,ft.isReversed,FROM_UNIXTIME(ft.dtstamp) AS dtstamp,aftd.score,aftd.amount,bd.isFraud,bd.isRefund,ft.maxScore,ft.autoReversalScore FROM fraud_transaction ft JOIN at_fraud_trans_details AS aftd ON ft.fraud_transaction_id=aftd.fraud_trans_id JOIN bin_details AS bd  ON ft.trackingid=bd.icicitransid WHERE ft.fraud_transaction_id>0");
            query.append("SELECT DISTINCT ft.fraud_transaction_id AS fraudtransid,ft.trackingid,ft.memberid,ft.accountid,ft.fsid,ft.fstransid,ft.fraud_trans_status AS fraudtransstatus,ft.isAlertSent,ft.isReversed,FROM_UNIXTIME(ft.dtstamp) AS dtstamp,aftd.score,aftd.amount,bd.isFraud,bd.isRefund,ft.maxScore,ft.autoReversalScore FROM fraud_transaction ft JOIN at_fraud_trans_details AS aftd ON ft.fraud_transaction_id=aftd.fraud_trans_id JOIN bin_details AS bd  ON ft.trackingid=bd.icicitransid WHERE ft.fraud_transaction_id>0");
            count.append("SELECT COUNT(DISTINCT ft.fraud_transaction_id) FROM fraud_transaction AS ft JOIN at_fraud_trans_details AS aftd ON ft.fraud_transaction_id=aftd.fraud_trans_id  JOIN bin_details AS bd  ON ft.trackingid=bd.icicitransid WHERE ft.fraud_transaction_id>0");
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and ft.dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
                count.append(" and ft.dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and ft.dtstamp <= " +ESAPI.encoder().encodeForSQL(me, tdtstamp));
                count.append(" and ft.dtstamp <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
            }
            if (functions.isValueNull(toid))
            {
                query.append(" AND ft.memberid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
                count.append(" AND ft.memberid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
            }
            if (functions.isValueNull(trackingid))
            {
                query.append(" AND ft.trackingid='" + ESAPI.encoder().encodeForSQL(me,trackingid) + "'");
                count.append(" AND ft.trackingid='" + ESAPI.encoder().encodeForSQL(me,trackingid) + "'");
            }
            if (functions.isValueNull(fstransid))
            {
                query.append(" AND ft.fraud_transaction_id='" + ESAPI.encoder().encodeForSQL(me, fstransid) + "'");
                count.append(" AND ft.fraud_transaction_id='" + ESAPI.encoder().encodeForSQL(me, fstransid) + "'");
            }
            if (functions.isValueNull(fsid))
            {
                query.append(" AND ft.fsid='" + ESAPI.encoder().encodeForSQL(me,fsid) + "'");
                count.append(" AND ft.fsid='" + ESAPI.encoder().encodeForSQL(me,fsid) + "'");
            }
            if (functions.isValueNull(fstransstatus))
            {
                query.append(" AND ft.fraud_trans_status='" + ESAPI.encoder().encodeForSQL(me,fstransstatus) + "'");
                count.append(" AND ft.fraud_trans_status='" + ESAPI.encoder().encodeForSQL(me,fstransstatus) + "'");
            }
            query.append("  limit " + start + "," + end);
            conn = Database.getConnection();
            logger.debug("Fraud Transaction Details====="+query.toString());
            logger.debug("Fraud Transaction Count====="+count.toString());
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));
            ResultSet rs = Database.executeQuery(count.toString(), conn);

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

        catch (SQLException se)
        {   logger.error("SQL Exception:::::", se);

            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public Hashtable listPartnerFraudTransactions(String partnerId, String toid, String trackingid, String fstransid, String fsid,String tdtstamp, String fdtstamp, String fstransstatus,int records, int pageno) throws SystemError
    {
        Hashtable hash = null;
        Functions functions = new Functions();
        StringBuffer query = new StringBuffer();
        StringBuffer count =new StringBuffer();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;
        Connection conn = null;
        ResultSet rs=null;
        try
        {
            query.append("SELECT DISTINCT ft.fraud_transaction_id AS fraudtransid,ft.trackingid,ft.memberid,ft.accountid,ft.fsid,ft.fstransid,ft.fraud_trans_status AS fraudtransstatus,ft.isAlertSent,ft.isAlertSent,ft.isReversed,FROM_UNIXTIME(ft.dtstamp) AS dtstamp,aftd.score,aftd.amount,bd.isFraud,bd.isRefund,ft.maxScore,ft.autoReversalScore FROM fraud_transaction ft JOIN at_fraud_trans_details AS aftd ON ft.fraud_transaction_id=aftd.fraud_trans_id JOIN bin_details AS bd  ON ft.trackingid=bd.icicitransid WHERE ft.fraud_transaction_id>0");
            count.append("SELECT COUNT(DISTINCT ft.fraud_transaction_id) FROM fraud_transaction AS ft JOIN at_fraud_trans_details AS aftd ON ft.fraud_transaction_id=aftd.fraud_trans_id  JOIN bin_details AS bd  ON ft.trackingid=bd.icicitransid WHERE ft.fraud_transaction_id>0 ");
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and ft.dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
                count.append(" and ft.dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and ft.dtstamp <= " +ESAPI.encoder().encodeForSQL(me, tdtstamp));
                count.append(" and ft.dtstamp <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
            }

            /*if (functions.isValueNull(toid))
            {
                query.append(" AND ft.memberid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
                count.append(" AND ft.memberid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
            }*/
            if(functions.isValueNull(toid))
            {
                query.append(" AND ft.memberid in (" + toid + ")");
                count.append(" AND ft.memberid in (" + toid + ")");
            }
            if (functions.isValueNull(trackingid))
            {
                query.append(" AND ft.trackingid='" + ESAPI.encoder().encodeForSQL(me,trackingid) + "'");
                count.append(" AND ft.trackingid='" + ESAPI.encoder().encodeForSQL(me,trackingid) + "'");
            }
            if (functions.isValueNull(fstransid))
            {
                query.append(" AND ft.fraud_transaction_id='" + ESAPI.encoder().encodeForSQL(me, fstransid) + "'");
                count.append(" AND ft.fraud_transaction_id='" + ESAPI.encoder().encodeForSQL(me, fstransid) + "'");
            }
            if (functions.isValueNull(fsid))
            {
                query.append(" AND ft.fsid='" + ESAPI.encoder().encodeForSQL(me,fsid) + "'");
                count.append(" AND ft.fsid='" + ESAPI.encoder().encodeForSQL(me,fsid) + "'");
            }
            if (functions.isValueNull(fstransstatus))
            {
                query.append(" AND ft.fraud_trans_status='" + ESAPI.encoder().encodeForSQL(me,fstransstatus) + "'");
                count.append(" AND ft.fraud_trans_status='" + ESAPI.encoder().encodeForSQL(me,fstransstatus) + "'");
            }
            query.append("  limit " + start + "," + end);
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            logger.debug("Fraud Transaction Details====="+query.toString());
            logger.debug("Fraud Transaction Count====="+count.toString());
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));
            rs = Database.executeQuery(count.toString(), conn);

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

        catch (SQLException se)
        {   logger.error("SQL Exception:::::", se);

            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return hash;
    }

    public Hashtable reverseForCommon(String trackingid,String toid,String accountId,String refundamount,String reversedAmount,String refundreason,String transStatus)  throws SystemError
    {
        PZRefundRequest refundRequest = new PZRefundRequest();
        PZRefundResponse response = new PZRefundResponse();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        Hashtable refundDetails = null;
        try
        {
            AbstractPaymentProcess process = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(trackingid), Integer.parseInt(accountId));
            InetAddress ip=null;
            try
            {
                ip = InetAddress.getLocalHost();
                refundRequest.setIpAddress(ip.getHostAddress());
            }catch (UnknownHostException e)
            {
                logger.error("UnknownHostException--->",e);
            }
            refundRequest.setMemberId(Integer.valueOf(toid));
            refundRequest.setAccountId(Integer.parseInt(accountId));
            refundRequest.setTrackingId(Integer.parseInt(trackingid));
            refundRequest.setRefundAmount(refundamount);
            refundRequest.setCaptureAmount(refundamount);
            refundRequest.setRefundReason(refundreason);
            refundRequest.setReversedAmount(reversedAmount);
            refundRequest.setTransactionStatus(transStatus);
            refundRequest.setAdmin(true);
            refundRequest.setFraud(true);
            auditTrailVO.setActionExecutorId(toid);
            auditTrailVO.setActionExecutorName("Admin Refund");
            refundRequest.setAuditTrailVO(auditTrailVO);
            //getting responce
            response = process.refund(refundRequest);
            PZResponseStatus status = response.getStatus();
            logger.debug("Response Status Common====="+status);
            /*System.out.println("Payment Gateway Response======:"+response.getResponseDesceiption());*/

            if (PZResponseStatus.ERROR.equals(status))
            {
                throw new SystemError();
            }
            else if (PZResponseStatus.FAILED.equals(status))
            {
                throw new SystemError();
            }
            if (response != null && (response.getStatus()).equals(PZResponseStatus.SUCCESS))
            {
                refundDetails = new Hashtable();
                refundDetails.put("trackingid",trackingid);
                refundDetails.put("description",response.getResponseDesceiption());
            }
            else
            {
                throw new SystemError();
            }
        }
        catch(Exception e)
        {
            logger.error("Exception--->",e);
            throw new SystemError();
        }
        return  refundDetails;
    }

    public Hashtable reverseForEcore(String toid,String accountId,String trackingId,String refundAmount,String refundReason)  throws SystemError
    {
        Hashtable refundDetails=null;
        try
        {
            EcoreRefundRequest refundRequest= new EcoreRefundRequest();
            AbstractPaymentProcess payment  = new EcorePaymentProcess();

            refundRequest.setMemberId(Integer.valueOf(toid));
            refundRequest.setAccountId(Integer.parseInt(accountId));
            refundRequest.setTrackingId(Integer.parseInt(trackingId));
            refundRequest.setRefundAmount(refundAmount);
            refundRequest.setRefundReason(refundReason);
            refundRequest.setFraud(true);
            refundRequest.setAdmin(false);
            PZRefundResponse response = payment.refund(refundRequest);
            PZResponseStatus status = response.getStatus();
            logger.debug("Response Status Score ====="+status);

            if(PZResponseStatus.ERROR.equals(status))
            {
                throw new SystemError();
            }
            else if(PZResponseStatus.FAILED.equals(status))
            {
                throw new SystemError();
            }
            else if (response != null && (response.getStatus()).equals(PZResponseStatus.SUCCESS) )
            {
                refundDetails = new Hashtable();
                refundDetails.put("trackingid",trackingId);
                refundDetails.put("description",response.getResponseDesceiption());

            }
            else
            {
                throw new SystemError();
            }

        }
        catch (Exception e)
        {
            logger.error("Exception:::::"+e);
            throw new SystemError();
        }
        return refundDetails;
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
                AuditTrailVO auditTrailVO=new AuditTrailVO();
                Hashtable qwipiKsnFlag=transaction.getMidKeyForQwipi(accountId);
                transRespDetails.setRemark(occure);
                //add charges and change status to markforreverse
                transactionEntry.newGenericRefundTransaction(icicitransid,rfamt,accountId,description,transRespDetails,auditTrailVO);

                auditTrailVO.setActionExecutorId(icicimerchantid);
                auditTrailVO.setActionExecutorName("Admin");

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
                    logger.debug("Response Status For QWIPI ====="+transRespDetails.getStatus());

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
                finally
                {
                    Database.closeConnection(conn);
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

        return refundDetails;

    }

    public String getPartnerFsDetails(String partnerId){
        Connection connection = null;
        String fsAccountId="";
        try
        {
            connection = Database.getConnection();
            String query = "select fsaccountid from partner_fsaccounts_mapping where partnerid=?";
            PreparedStatement psmt=connection.prepareStatement(query);
            psmt.setString(1,partnerId);
            ResultSet rs = psmt.executeQuery();
            if(rs.next()){
                fsAccountId=rs.getString("fsaccountid");
            }
        }
        catch (SystemError e)
        {
            logger.error("SystemError---->",e);
        }
        catch (SQLException e)
        {
            logger.error("SQLException---->", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return fsAccountId;
    }

    //*******fraud kyc method ********//

    public Hashtable listPartnerFraudKycTransactions(String partnerId, String custRegId,String tdtstamp, String fdtstamp, String fskycstatus,int records, int pageno) throws SystemError
    {
        Hashtable hash = null;
        Functions functions = new Functions();
        StringBuffer query = new StringBuffer();
        StringBuffer count =new StringBuffer();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;
        Connection conn = null;
        ResultSet rs=null;
        try
        {
            query.append("SELECT id, cust_request_id, customer_registration_id, fsid, firstName, lastName, emailId, partnerId, reg_status, score, ACTION, FROM_UNIXTIME(dtstamp) as dtstamp, TIMESTAMP FROM customer_registration WHERE 1>0");
            count.append("SELECT COUNT(DISTINCT id) FROM customer_registration WHERE 1>0");
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
                count.append(" and dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and dtstamp <= " +ESAPI.encoder().encodeForSQL(me, tdtstamp));
                count.append(" and dtstamp <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
            }

            if (functions.isValueNull(partnerId))
            {
                query.append(" AND partnerId='" + ESAPI.encoder().encodeForSQL(me,partnerId) + "'");
                count.append(" AND partnerId='" + ESAPI.encoder().encodeForSQL(me,partnerId) + "'");
            }
            if (functions.isValueNull(custRegId))
            {
                query.append(" AND id='" + ESAPI.encoder().encodeForSQL(me, custRegId) + "'");
                count.append(" AND id='" + ESAPI.encoder().encodeForSQL(me, custRegId) + "'");
            }
            /*if (functions.isValueNull(fsid))
            {
                query.append(" AND ft.fsid='" + ESAPI.encoder().encodeForSQL(me,fsid) + "'");
                count.append(" AND ft.fsid='" + ESAPI.encoder().encodeForSQL(me,fsid) + "'");
            }*/
            if (functions.isValueNull(fskycstatus))
            {
                query.append(" AND reg_status='" + ESAPI.encoder().encodeForSQL(me,fskycstatus) + "'");
                count.append(" AND reg_status='" + ESAPI.encoder().encodeForSQL(me,fskycstatus) + "'");
            }
            query.append("  limit " + start + "," + end);
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            logger.debug("Fraud Kyc Details====="+query.toString());
            logger.debug("Fraud Kyc Count====="+count.toString());
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));
            rs = Database.executeQuery(count.toString(), conn);

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

        catch (SQLException se)
        {   logger.error("SQL Exception:::::", se);

            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return hash;
    }

    public Hashtable getFraudKycDetails(String fsCustRegId,String fsid)throws SystemError
    {
        logger.debug("fatch record 1");
        PreparedStatement pstmt = null;
        StringBuffer query =null;
        Hashtable hash = null;
        Connection conn = null;
        ResultSet rs=null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            query = new StringBuffer("SELECT id, cust_request_id, customer_registration_id, fsid, firstName, lastName, emailId, countryCode, phone, reg_date, customerIpAddress, partnerId, website, reg_status, score, recommendation,description, confidence_level, ACTION, FROM_UNIXTIME(dtstamp) as dtstamp, TIMESTAMP FROM customer_registration WHERE 1>0 AND id= ? AND fsid =?");
            pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1,fsCustRegId);
            pstmt.setString(2,fsid);
            logger.debug(query);
            hash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());

            Hashtable fraudRuleHash = new Hashtable();
            StringBuffer fetchRule = new StringBuffer("SELECT rulename,rulescore FROM fraud_custreg_rules_triggered where fraud_custregid=?");
            pstmt = conn.prepareStatement(fetchRule.toString());
            pstmt.setString(1, fsCustRegId);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                String ruleName = rs.getString("rulename");
                String ruleScore = rs.getString("rulescore");
                fraudRuleHash.put(ruleName,ruleScore);
            }
            hash.put("fraudRule",fraudRuleHash);
        }
        catch (SQLException se)
        {
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        logger.debug("Leaving getFraudTransactionDetails");
        return hash;
    }

    public Hashtable listFraudTransactions1(String toid, String trackingid, String fstransid, String fsid,String tdtstamp, String fdtstamp, String fstransstatus,int records, int pageno) throws SystemError
    {
        Hashtable hash = null;
        Functions functions = new Functions();
        StringBuffer query = new StringBuffer();
        StringBuffer count =new StringBuffer();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;
        Connection conn = null;
        try
        {
            query.append("SELECT DISTINCT ft.fraud_transaction_id AS fraudtransid,ft.trackingid,ft.memberid,ft.accountid,ft.fsid,ft.fstransid,ft.fraud_trans_status AS fraudtransstatus,ft.isAlertSent,ft.isReversed,FROM_UNIXTIME(ft.dtstamp) AS dtstamp,aftd.score,aftd.amount,ft.maxScore,ft.autoReversalScore FROM fraud_transaction ft JOIN at_fraud_trans_details AS aftd ON ft.fraud_transaction_id=aftd.fraud_trans_id WHERE ft.fraud_transaction_id>0");
            count.append("SELECT COUNT(DISTINCT ft.fraud_transaction_id) FROM fraud_transaction AS ft JOIN at_fraud_trans_details AS aftd ON ft.fraud_transaction_id=aftd.fraud_trans_id  WHERE ft.fraud_transaction_id>0");
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and ft.dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
                count.append(" and ft.dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and ft.dtstamp <= " +ESAPI.encoder().encodeForSQL(me, tdtstamp));
                count.append(" and ft.dtstamp <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
            }
            if (functions.isValueNull(toid))
            {
                query.append(" AND ft.memberid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
                count.append(" AND ft.memberid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
            }
            if (functions.isValueNull(trackingid))
            {
                query.append(" AND ft.trackingid='" + ESAPI.encoder().encodeForSQL(me,trackingid) + "'");
                count.append(" AND ft.trackingid='" + ESAPI.encoder().encodeForSQL(me,trackingid) + "'");
            }
            if (functions.isValueNull(fstransid))
            {
                query.append(" AND ft.fraud_transaction_id='" + ESAPI.encoder().encodeForSQL(me, fstransid) + "'");
                count.append(" AND ft.fraud_transaction_id='" + ESAPI.encoder().encodeForSQL(me, fstransid) + "'");
            }
            if (functions.isValueNull(fsid))
            {
                query.append(" AND ft.fsid='" + ESAPI.encoder().encodeForSQL(me,fsid) + "'");
                count.append(" AND ft.fsid='" + ESAPI.encoder().encodeForSQL(me,fsid) + "'");
            }
            if (functions.isValueNull(fstransstatus))
            {
                query.append(" AND ft.fraud_trans_status='" + ESAPI.encoder().encodeForSQL(me,fstransstatus) + "'");
                count.append(" AND ft.fraud_trans_status='" + ESAPI.encoder().encodeForSQL(me,fstransstatus) + "'");
            }
            query.append("  limit " + start + "," + end);
            conn = Database.getConnection();
            logger.debug("Fraud Transaction Details====="+query.toString());
            logger.debug("Fraud Transaction Count====="+count.toString());
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));
            ResultSet rs = Database.executeQuery(count.toString(), conn);

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

        catch (SQLException se)
        {   logger.error("SQL Exception:::::", se);

            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public Hashtable listPartnerFraudTransactions1(String partnerId, String toid, String trackingid, String fstransid, String fsid,String tdtstamp, String fdtstamp, String fstransstatus,int records, int pageno) throws SystemError
    {
        Hashtable hash = null;
        Functions functions = new Functions();
        StringBuffer query = new StringBuffer();
        StringBuffer count =new StringBuffer();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;
        Connection conn = null;
        ResultSet rs=null;
        try
        {
            query.append("SELECT DISTINCT ft.fraud_transaction_id AS fraudtransid,ft.trackingid,ft.memberid,ft.accountid,ft.fsid,ft.fstransid,ft.fraud_trans_status AS fraudtransstatus,ft.isAlertSent,ft.isAlertSent,ft.isReversed,FROM_UNIXTIME(ft.dtstamp) AS dtstamp,aftd.score,aftd.amount,ft.maxScore,ft.autoReversalScore FROM fraud_transaction ft JOIN at_fraud_trans_details AS aftd ON ft.fraud_transaction_id=aftd.fraud_trans_id WHERE ft.fraud_transaction_id>0 ");
            count.append("SELECT COUNT(DISTINCT ft.fraud_transaction_id) FROM fraud_transaction AS ft JOIN at_fraud_trans_details AS aftd ON ft.fraud_transaction_id=aftd.fraud_trans_id  JOIN bin_details AS bd  ON ft.trackingid=bd.icicitransid WHERE ft.fraud_transaction_id>0 ");
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and ft.dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
                count.append(" and ft.dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and ft.dtstamp <= " +ESAPI.encoder().encodeForSQL(me, tdtstamp));
                count.append(" and ft.dtstamp <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
            }

            /*if (functions.isValueNull(toid))
            {
                query.append(" AND ft.memberid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
                count.append(" AND ft.memberid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
            }*/
            if(functions.isValueNull(toid))
            {
                query.append(" AND ft.memberid in (" + toid + ")");
                count.append(" AND ft.memberid in (" + toid + ")");
            }
            if (functions.isValueNull(trackingid))
            {
                query.append(" AND ft.trackingid='" + ESAPI.encoder().encodeForSQL(me,trackingid) + "'");
                count.append(" AND ft.trackingid='" + ESAPI.encoder().encodeForSQL(me,trackingid) + "'");
            }
            if (functions.isValueNull(fstransid))
            {
                query.append(" AND ft.fraud_transaction_id='" + ESAPI.encoder().encodeForSQL(me, fstransid) + "'");
                count.append(" AND ft.fraud_transaction_id='" + ESAPI.encoder().encodeForSQL(me, fstransid) + "'");
            }
            if (functions.isValueNull(fsid))
            {
                query.append(" AND ft.fsid='" + ESAPI.encoder().encodeForSQL(me,fsid) + "'");
                count.append(" AND ft.fsid='" + ESAPI.encoder().encodeForSQL(me,fsid) + "'");
            }
            if (functions.isValueNull(fstransstatus))
            {
                query.append(" AND ft.fraud_trans_status='" + ESAPI.encoder().encodeForSQL(me,fstransstatus) + "'");
                count.append(" AND ft.fraud_trans_status='" + ESAPI.encoder().encodeForSQL(me,fstransstatus) + "'");
            }
            query.append("  limit " + start + "," + end);
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            logger.debug("Fraud Transaction Details====="+query.toString());
            logger.debug("Fraud Transaction Count====="+count.toString());
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));
            rs = Database.executeQuery(count.toString(), conn);

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

        catch (SQLException se)
        {   logger.error("SQL Exception:::::", se);

            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return hash;
    }


}