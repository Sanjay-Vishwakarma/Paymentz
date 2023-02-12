package com.manager.dao;

import com.directi.pg.*;
import com.directi.pg.Base64;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.GatewayManager;
import com.manager.TransactionManager;
import com.manager.utils.AccountUtil;
import com.manager.vo.*;
import com.manager.vo.merchantmonitoring.DateVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.common.core.TransactionDetailsVOFactory;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 8/18/14
 * Time: 12:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionDAO
{
    Logger log              = new Logger(TransactionDAO.class.getName());
    Logger logger           = new Logger(TransactionDAO.class.getName());
    Functions functions     = new Functions();
    AccountUtil accountUtil = new AccountUtil();

    public LinkedHashMap<String, TransactionVO> getMerchantTransactionReports(InputDateVO inputDateVO, TerminalVO terminalVO, Set<String> sTableNames, LinkedHashMap<LinkedHashMap<String,String>, String> monthAndStatus) throws PZDBViolationException
    {
        Connection con = null;
        int count=1;
        PreparedStatement psMerchantTransactionReport = null;
        ResultSet rsMerchantTransactionReport = null;
        //TransactionVO initialization
        LinkedHashMap<String,TransactionVO> transactionVOHashMap = new LinkedHashMap<String, TransactionVO>();
        try
        {
            //con = Database.getConnection();
            con = Database.getRDBConnection();
            StringBuffer query = new StringBuffer();
            for(String sTablename : sTableNames)
            {
                if (query.length() != 0) {
                    query.append(" UNION ");
                }
                /*query.append(" Select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount,date_format(from_unixtime(dtstamp),'%b-%y') as month,sum(captureamount) as captureamount,sum(refundamount) as refundamount,sum(chargebackamount) as chargebackamount,max(dtstamp) as dtstamp from " + sTablename + "  where dtstamp >= ? and dtstamp <= ?");*/
                query.append(" select STATUS,COUNT(trackingid) AS COUNT,SUM(amount) AS amount,sum(captureamount) as captureamount,sum(refundamount) as refundamount,sum(chargebackamount) as chargebackamount,date_format(from_unixtime(dtstamp),'%b-%y') as month,max(dtstamp) as dtstamp from " + sTablename + "  where dtstamp >= ? and dtstamp <= ?");
                if(functions.isValueNull(terminalVO.getMemberId()))
                {
                    query.append(" and toid =?");
                }
                if(functions.isValueNull(terminalVO.getAccountId()))
                {
                    query.append(" and accountid =?");
                }
                if(functions.isValueNull(terminalVO.getPaymodeId()))
                {
                    query.append(" and paymodeid =?");
                }
                if(functions.isValueNull(terminalVO.getCardTypeId()))
                {
                    query.append(" and cardtypeid =?");
                }
                query.append(" group by status,month ");
            }
            query.append(" ) as temp group by status,month ORDER BY dt ;");
            /*query.insert(0, "Select STATUS,SUM(COUNT) AS COUNT,SUM(amount) AS amount,month,sum(captureamount) as captureamount,SUM(refundamount) AS refundamount,SUM(chargebackamount) as chargebackamount,max(dtstamp) as dt from (");*/
            query.insert(0, "select STATUS,SUM(COUNT) AS COUNT,SUM(amount) AS amount,month,sum(captureamount) as captureamount,SUM(refundamount) AS refundamount,SUM(chargebackamount) as chargebackamount,max(dtstamp) as dt from (");
            logger.debug("Transaction Report query::"+query.toString());

            psMerchantTransactionReport = con.prepareStatement(query.toString());
            for(String sTablename: sTableNames)
            {
                psMerchantTransactionReport.setString(count,inputDateVO.getFdtstamp());
                logger.debug("psParameters::" + count + "," + inputDateVO.getFdtstamp());
                count++;
                psMerchantTransactionReport.setString(count,inputDateVO.getTdtstamp());
                logger.debug("psParameters::"+count+","+inputDateVO.getTdtstamp());
                count++;
                if(functions.isValueNull(terminalVO.getMemberId()))
                {
                    psMerchantTransactionReport.setString(count,terminalVO.getMemberId());
                    logger.debug("psParameters::"+count+","+terminalVO.getMemberId());
                    count++;
                }
                if(functions.isValueNull(terminalVO.getAccountId()))
                {
                    psMerchantTransactionReport.setString(count,terminalVO.getAccountId());
                    logger.debug("psParameters::"+count+","+terminalVO.getAccountId());
                    count++;
                }
                if(functions.isValueNull(terminalVO.getPaymodeId()))
                {
                    psMerchantTransactionReport.setString(count,terminalVO.getPaymodeId());
                    logger.debug("psParameters::"+count+","+terminalVO.getPaymodeId());
                    count++;
                }
                if(functions.isValueNull(terminalVO.getCardTypeId()))
                {
                    psMerchantTransactionReport.setString(count,terminalVO.getCardTypeId());
                    logger.debug("psParameters::"+count+","+terminalVO.getCardTypeId());
                    count++;
                }
            }
            rsMerchantTransactionReport=psMerchantTransactionReport.executeQuery();
            while(rsMerchantTransactionReport.next())
            {
                LinkedHashMap<String,String> innerMonthAndStatus = new LinkedHashMap<String, String>();
                innerMonthAndStatus.put(rsMerchantTransactionReport.getString("month"),rsMerchantTransactionReport.getString("STATUS"));
                TransactionVO transactionVO = new TransactionVO();
                transactionVO.setStatus(rsMerchantTransactionReport.getString("STATUS"));
                transactionVO.setCount(rsMerchantTransactionReport.getLong("COUNT"));

                if(PZTransactionStatus.REVERSED.toString().toString().equals(rsMerchantTransactionReport.getString("STATUS")) )
                {
                    transactionVO.setAmount(rsMerchantTransactionReport.getString("amount"));
                    transactionVO.setCaptureAmount(rsMerchantTransactionReport.getDouble("refundamount"));
                }
                else if(PZTransactionStatus.CAPTURE_SUCCESS.toString().toString().equals(rsMerchantTransactionReport.getString("STATUS")))
                {
                    transactionVO.setAmount(rsMerchantTransactionReport.getString("amount"));
                    transactionVO.setCaptureAmount(rsMerchantTransactionReport.getDouble("captureamount"));
                }
                else if(PZTransactionStatus.SETTLED.toString().toString().equals(rsMerchantTransactionReport.getString("STATUS")))
                {
                    transactionVO.setAmount(rsMerchantTransactionReport.getString("amount"));
                    transactionVO.setCaptureAmount(rsMerchantTransactionReport.getDouble("captureamount"));
                }
                else if(PZTransactionStatus.MARKED_FOR_REVERSAL.toString().toString().equals(rsMerchantTransactionReport.getString("STATUS")))
                {
                    transactionVO.setAmount(rsMerchantTransactionReport.getString("amount"));
                    transactionVO.setCaptureAmount(rsMerchantTransactionReport.getDouble("captureamount"));
                }
                else if(PZTransactionStatus.CHARGEBACK.toString().toString().equals(rsMerchantTransactionReport.getString("STATUS")))
                {
                    transactionVO.setAmount(rsMerchantTransactionReport.getString("amount"));
                    transactionVO.setCaptureAmount(rsMerchantTransactionReport.getDouble("chargebackamount"));
                }
                else
                {
                    transactionVO.setAmount(rsMerchantTransactionReport.getString("amount"));
                    transactionVO.setCaptureAmount(rsMerchantTransactionReport.getDouble("amount"));
                }
                monthAndStatus.put(innerMonthAndStatus,rsMerchantTransactionReport.getString("STATUS"));
                transactionVOHashMap.put(rsMerchantTransactionReport.getString("month")+","+rsMerchantTransactionReport.getString("STATUS"),transactionVO);
            }
            logger.debug("successfully loaded hashMap");
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while transaction summary report",systemError);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(),"getMerchantTransactionReports",null,"Common","Sql exception while connecting to transaction table", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while transaction summary report",e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(),"getMerchantTransactionReports",null,"Common","In correct Sql query for getting transaction::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        logger.debug("transactionVOHashMap::"+transactionVOHashMap);
        return transactionVOHashMap;
    }

    public HashMap<String, String> getbincountrysuccessful( String toid ,String terminalid ,String country, String tdtstamp, String fdtstamp)
    {
        logger.debug("Entering getbincountrysuccessful:::::");
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        StringBuilder query = new StringBuilder();
        HashMap<String, String> hashMapbincountrysuccessful = new HashMap<>();

        try
        {
            connection = Database.getRDBConnection();
            query.append("SELECT bd.`bin_country_code_A2`,COUNT(trackingId) AS COUNT  FROM transaction_common tc,bin_details bd WHERE tc.`trackingid`=bd.`icicitransid` AND STATUS IN ('capturesuccess' ,'settled','authsuccessful','reversed','markedforreversal','chargeback')  AND toid = ? AND terminalid = ? ");

            if (functions.isValueNull(country))
            {
                query.append("AND country = ? ");
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
            int counter = 3;
            //pstmt.setString(1, totype);
            pstmt.setString(1, toid);
            pstmt.setString(2, terminalid);

            if (functions.isValueNull(country))
            {
                pstmt.setString(counter, country);
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
            logger.debug("QUERY for getbincountrysuccessful " +pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                hashMapbincountrysuccessful.put(rs.getString("bin_country_code_A2"), rs.getString("count"));
            }
        }
        catch (Exception e)
        {
            logger.error("Exception while getting getbincountrysuccessful data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }

        return hashMapbincountrysuccessful;
    }

    public HashMap<String, String> getbincountryfailed( String toid , String terminalid, String country, String tdtstamp, String fdtstamp)
    {
        logger.debug("Entering getbincountryfailed:::::");
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        StringBuilder query = new StringBuilder();
        HashMap<String, String> hashMapbincountryfailed = new HashMap<>();
        //System.out.println("Hashmap:::::::::getbincountryfailed"+hashMapbincountryfailed);

        try
        {
            connection = Database.getRDBConnection();
            query.append("SELECT bd.`bin_country_code_A2`,COUNT(trackingId) AS COUNT FROM transaction_common tc,bin_details bd WHERE tc.`trackingid`=bd.`icicitransid` AND STATUS IN ('failed','authfailed') AND toid = ? AND terminalid = ? ");

            if (functions.isValueNull(country))
            {
                query.append("AND country = ? ");
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
            int counter = 3;
            //pstmt.setString(1, totype);
            pstmt.setString(1, toid);
            pstmt.setString(2, terminalid);

            if (functions.isValueNull(country))
            {
                pstmt.setString(counter, country);
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
            logger.debug("QUERY for getbincountryfailed " +pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                hashMapbincountryfailed.put(rs.getString("bin_country_code_A2"), rs.getString("count"));
            }
        }
        catch (Exception e)
        {
            logger.error("Exception while getting getbincountryfailed data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }

        return hashMapbincountryfailed;
    }

    public HashMap<String, String> getipcountrysuccessful( String toid ,String terminalid , String tdtstamp, String fdtstamp)
    {
        logger.debug("Entering getipcountrysuccessful:::::");
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        StringBuilder query = new StringBuilder();
        HashMap<String, String> hashMapipcountrysuccessful = new HashMap<>();
        List<String> countryList=new ArrayList<>();

        try
        {
            connection = Database.getRDBConnection();
            //query.append("SELECT tcd.ipaddress, COUNT(tc.trackingId) AS COUNT FROM transaction_common AS tc , transaction_common_details AS tcd WHERE tc.`trackingid`=tcd.`trackingid` AND tc.STATUS IN ('capturesuccess' ,'settled','authsuccessful') AND tcd.ipaddress IS NOT NULL AND tcd.`ipaddress`!=''  AND toid = ? AND terminalid = ? ");
            query.append("SELECT tc.customerIpCountry, COUNT(tc.trackingId) AS COUNT FROM transaction_common AS tc WHERE tc.STATUS IN ('capturesuccess' ,'settled','authsuccessful','reversed','markedforreversal','chargeback') AND tc.customerIpCountry IS NOT NULL AND tc.`customerIpCountry`!='-' AND tc.`customerIpCountry`!='' AND toid = ? AND terminalid = ?");

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
            int counter = 3;
            //pstmt.setString(1, totype);
            pstmt.setString(1, toid);
            pstmt.setString(2, terminalid);

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
            logger.debug("QUERY for getipcountrysuccessful " +pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                hashMapipcountrysuccessful.put(rs.getString("customerIpCountry"), rs.getString("count"));
                /*String countryRs=functions.getIPCountryShort(rs.getString("ipAddress"));
                if(countryList.contains(countryRs))
                {
                    String trackingIdCount=String.valueOf(Integer.parseInt(hashMapipcountrysuccessful.get(countryRs))+rs.getInt("count"));
                    hashMapipcountrysuccessful.put(countryRs, trackingIdCount);
                }else
                {
                    hashMapipcountrysuccessful.put(countryRs, rs.getString("count"));
                    countryList.add(countryRs);
                    //System.out.println("countryList"+countryRs);
                }*/
            }
        }
        catch (Exception e)
        {
            logger.error("Exception while getting getipcountrysuccessful data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }

        return hashMapipcountrysuccessful;
    }

    public HashMap<String, String> getipcountryfailed( String toid,String terminalid ,String tdtstamp, String fdtstamp)
    {
        logger.debug("Entering getipcountryfailed:::::");
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        StringBuilder query = new StringBuilder();
        HashMap<String, String> hashMapipcountryfailed = new HashMap<>();
        List<String> countryList=new ArrayList<>();

        try
        {
            connection = Database.getRDBConnection();
            //query.append("SELECT tcd.ipaddress, COUNT(tc.trackingId) AS COUNT FROM transaction_common AS tc,transaction_common_details AS tcd WHERE tc.`trackingid`=tcd.`trackingid` AND tc.STATUS IN ('failed','authfailed') AND tcd.ipaddress IS NOT NULL AND tcd.`ipaddress`!=''  AND toid = ? AND terminalid = ? ");
            query.append("SELECT tc.customerIpCountry, COUNT(tc.trackingId) AS COUNT FROM transaction_common AS tc WHERE tc.STATUS IN ('failed','authfailed') AND tc.customerIpCountry IS NOT NULL AND tc.`customerIpCountry`!='-' AND tc.`customerIpCountry`!='' AND toid = ? AND terminalid = ?");

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
            int counter = 3;
            //pstmt.setString(1, totype);
            pstmt.setString(1, toid);
            pstmt.setString(2, terminalid);

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
            logger.debug("QUERY for getipcountryfailed " +pstmt);
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
            logger.error("Exception while getting getipcountryfailed data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }

        return hashMapipcountryfailed;
    }

    public boolean  setSingleTransactionAccordingToTheStatus(TerminalVO terminalVO,TransactionVO transactionVO)
    {
        Connection con = null;
        ResultSet rsSingleTransaction = null;
        try
        {
            con = Database.getConnection();
            String query = "select T.amount as amount,TD.responsetransactionid as responsetransactionid,TD.detailid as detailid  from transaction_common as T JOIN members as M on T.toid=M.memberid join transaction_common_details as TD on TD.trackingid=T.trackingid  where T.trackingid=? and T.accountid = ? and T.toid = ?  and T.status IN(?) and TD.status IN(?) GROUP BY TD.trackingid HAVING MAX(TD.timestamp)";
            PreparedStatement psSingleTransaction = con.prepareStatement(query.toString());
            psSingleTransaction.setString(1, transactionVO.getTrackingId());
            psSingleTransaction.setString(2, terminalVO.getAccountId());
            psSingleTransaction.setString(3, terminalVO.getMemberId());
            psSingleTransaction.setString(4,transactionVO.getStatus());
            psSingleTransaction.setString(5,transactionVO.getStatus());
            rsSingleTransaction= psSingleTransaction.executeQuery();
            if(rsSingleTransaction.next())
            {
                transactionVO.setAmount(rsSingleTransaction.getString("amount"));
                transactionVO.setDetailId(rsSingleTransaction.getString("detailid"));
                transactionVO.setResponseTransactionId(rsSingleTransaction.getString("responsetransactionid"));
                return true;
            }
        }
        catch(SystemError systemError)
        {
            logger.error("system error in setSingleTransactionAccordingToTheStatus ::", systemError);
        }
        catch(SQLException e)
        {
            logger.error("SQL excption in setSingleTransactionAccordingToTheStatus ::", e);
        }
        finally {
            Database.closeConnection(con);
        }
        return false;
    }

    public boolean updateTransactionStatusfromCommon(TransactionVO transactionVO)
    {
        Connection con = null;
        ResultSet rsUpdateTransaction = null;
        try
        {
            con=Database.getConnection();

            String update = "update transaction_common set status =? where  status =? and trackingid =?";
            PreparedStatement psUpdateTransaction =con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,transactionVO.getUpdateStatusTo());
            psUpdateTransaction.setString(2,transactionVO.getStatus());
            psUpdateTransaction.setString(3,transactionVO.getTrackingId());
            if(psUpdateTransaction.executeUpdate()==1)
            {
                return true;
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System error in updateTransactionfromCommon:;",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL exception in updateTransactionfromCommon::",e);
        }
        finally {
            Database.closeConnection(con);
        }
        return false;
    }
    public boolean updateTransactionRetrialRequest(String trackingId,String retrivalRequest)
    {
        Connection con = null;
        boolean status=false;
        try
        {
            con=Database.getConnection();
            String update = "UPDATE bin_details SET isRetrivalRequest=? WHERE icicitransid=?";
            PreparedStatement psUpdateTransaction =con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,retrivalRequest);
            psUpdateTransaction.setString(2,trackingId);
            int k=psUpdateTransaction.executeUpdate();
            if(k==1)
            {
                status=true;
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }
    public TransactionDetailsVO getDetailFromCommon(String trackingId)
    {
        Connection con                          = null;
        ResultSet resultSet                     = null;
        PreparedStatement psUpdateTransaction   = null;
        boolean status                          = false;
        TransactionDetailsVO transactionDetailsVO   = new TransactionDetailsVO();
        try
        {
            con                 = Database.getConnection();
            String update       = "select trackingid,accountid,paymodeid,cardtypeid,customerIp,toid,totype,fromid,fromtype,description,orderdescription,templateamount,amount,currency,redirecturl,notificationUrl,status,firstname,lastname,name,ccnum,expdate,cardtype,emailaddr,ipaddress,country,state,city,street,zip,telnocc,telno,httpheader,paymentid,templatecurrency,emailaddr,customerId,eci,terminalid,version,captureamount,refundamount,emiCount,timestamp,walletAmount,walletCurrency,transaction_type,transaction_mode,authorization_code,remark, podbatch,hrcode from transaction_common where trackingid=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,trackingId);
            resultSet           = psUpdateTransaction.executeQuery();
            if(resultSet.next())
            {
                transactionDetailsVO.setTrackingid(resultSet.getString("trackingid"));
                transactionDetailsVO.setAccountId(resultSet.getString("accountid"));
                transactionDetailsVO.setPaymodeId(resultSet.getString("paymodeid"));
                transactionDetailsVO.setCardTypeId(resultSet.getString("cardtypeid"));
                transactionDetailsVO.setCustomerIp(resultSet.getString("customerIp"));
                transactionDetailsVO.setToid(resultSet.getString("toid"));
                transactionDetailsVO.setTotype(resultSet.getString("totype"));//totype
                transactionDetailsVO.setFromid(resultSet.getString("fromid"));//fromid
                transactionDetailsVO.setFromtype(resultSet.getString("fromtype"));//fromtype
                transactionDetailsVO.setDescription(resultSet.getString("description"));
                transactionDetailsVO.setOrderDescription(resultSet.getString("orderdescription"));
                transactionDetailsVO.setTemplateamount(resultSet.getString("templateamount"));
                transactionDetailsVO.setAmount(resultSet.getString("amount"));
                transactionDetailsVO.setCurrency(resultSet.getString("currency"));
                transactionDetailsVO.setRedirectURL(resultSet.getString("redirecturl"));
                transactionDetailsVO.setNotificationUrl(resultSet.getString("notificationUrl"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setFirstName(resultSet.getString("firstname"));//fn
                transactionDetailsVO.setLastName(resultSet.getString("lastname"));//ln
                transactionDetailsVO.setName(resultSet.getString("name"));//name
                transactionDetailsVO.setCcnum(resultSet.getString("ccnum"));//ccnum
                transactionDetailsVO.setExpdate(resultSet.getString("expdate"));//expdt
                transactionDetailsVO.setCardtype(resultSet.getString("cardtype"));//cardtype
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setIpAddress(resultSet.getString("ipaddress"));
                transactionDetailsVO.setCountry(resultSet.getString("country"));//country
                transactionDetailsVO.setState(resultSet.getString("state"));//state
                transactionDetailsVO.setCity(resultSet.getString("city"));//city
                transactionDetailsVO.setStreet(resultSet.getString("street"));//street
                transactionDetailsVO.setZip(resultSet.getString("zip"));//zip
                transactionDetailsVO.setTelcc(resultSet.getString("telnocc"));//telcc
                transactionDetailsVO.setTelno(resultSet.getString("telno"));//telno
                transactionDetailsVO.setHttpHeader(resultSet.getString("httpheader"));//httpheadet
                transactionDetailsVO.setPaymentId(resultSet.getString("paymentid"));
                transactionDetailsVO.setTemplatecurrency(resultSet.getString("templatecurrency"));
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setCustomerId(resultSet.getString("customerId"));
                transactionDetailsVO.setEci(resultSet.getString("eci"));
                transactionDetailsVO.setTerminalId(resultSet.getString("terminalid"));
                transactionDetailsVO.setVersion(resultSet.getString("version"));
                transactionDetailsVO.setCaptureAmount(resultSet.getString("captureamount"));
                transactionDetailsVO.setRefundAmount(resultSet.getString("refundamount"));
                transactionDetailsVO.setEmiCount(resultSet.getString("emiCount"));
                transactionDetailsVO.setTransactionTime(resultSet.getString("timestamp"));
                transactionDetailsVO.setWalletAmount(resultSet.getString("walletAmount"));
                transactionDetailsVO.setWalletCurrency(resultSet.getString("walletCurrency"));
                transactionDetailsVO.setTransactionType(resultSet.getString("transaction_type"));
                transactionDetailsVO.setTransactionMode(resultSet.getString("transaction_mode"));
                transactionDetailsVO.setAuthorization_code(resultSet.getString("authorization_code"));
                transactionDetailsVO.setBankReferenceId(resultSet.getString("paymentid"));
                transactionDetailsVO.setRemark(resultSet.getString("remark"));
                transactionDetailsVO.setPodBatch(resultSet.getString("podbatch"));
                transactionDetailsVO.setRedirectMethod(resultSet.getString("hrcode"));
            }
            logger.debug("transaction common query----"+psUpdateTransaction);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeResultSet(resultSet);
            Database.closeConnection(con);
        }
        return transactionDetailsVO;
    }
    public TransactionDetailsVO getTransDetailsFromCommonBypaymentid(String paymentid)
    {
        Connection con                          = null;
        ResultSet resultSet                     = null;
        PreparedStatement psUpdateTransaction   = null;
        boolean status                          = false;
        TransactionDetailsVO transactionDetailsVO   =null;
        try
        {
            con                 = Database.getConnection();
            String update       = "select * from transaction_common where paymentid=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,paymentid);
            resultSet           = psUpdateTransaction.executeQuery();
            if(resultSet.next())
            {
                transactionDetailsVO=new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(resultSet.getString("trackingid"));
                transactionDetailsVO.setAccountId(resultSet.getString("accountid"));
                transactionDetailsVO.setPaymodeId(resultSet.getString("paymodeid"));
                transactionDetailsVO.setCardTypeId(resultSet.getString("cardtypeid"));
                transactionDetailsVO.setToid(resultSet.getString("toid"));
                transactionDetailsVO.setTotype(resultSet.getString("totype"));//totype
                transactionDetailsVO.setFromid(resultSet.getString("fromid"));//fromid
                transactionDetailsVO.setFromtype(resultSet.getString("fromtype"));//fromtype
                transactionDetailsVO.setDescription(resultSet.getString("description"));
                transactionDetailsVO.setOrderDescription(resultSet.getString("orderdescription"));
                transactionDetailsVO.setTemplateamount(resultSet.getString("templateamount"));
                transactionDetailsVO.setAmount(resultSet.getString("amount"));
                transactionDetailsVO.setCurrency(resultSet.getString("currency"));
                transactionDetailsVO.setRedirectURL(resultSet.getString("redirecturl"));
                transactionDetailsVO.setNotificationUrl(resultSet.getString("notificationUrl"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setFirstName(resultSet.getString("firstname"));//fn
                transactionDetailsVO.setLastName(resultSet.getString("lastname"));//ln
                transactionDetailsVO.setName(resultSet.getString("name"));//name
                transactionDetailsVO.setCcnum(resultSet.getString("ccnum"));//ccnum
                transactionDetailsVO.setExpdate(resultSet.getString("expdate"));//expdt
                transactionDetailsVO.setCardtype(resultSet.getString("cardtype"));//cardtype
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setIpAddress(resultSet.getString("ipaddress"));
                transactionDetailsVO.setCountry(resultSet.getString("country"));//country
                transactionDetailsVO.setState(resultSet.getString("state"));//state
                transactionDetailsVO.setCity(resultSet.getString("city"));//city
                transactionDetailsVO.setStreet(resultSet.getString("street"));//street
                transactionDetailsVO.setZip(resultSet.getString("zip"));//zip
                transactionDetailsVO.setTelcc(resultSet.getString("telnocc"));//telcc
                transactionDetailsVO.setTelno(resultSet.getString("telno"));//telno
                transactionDetailsVO.setHttpHeader(resultSet.getString("httpheader"));//httpheadet
                transactionDetailsVO.setPaymentId(resultSet.getString("paymentid"));
                transactionDetailsVO.setTemplatecurrency(resultSet.getString("templatecurrency"));
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setCustomerId(resultSet.getString("customerId"));
                transactionDetailsVO.setEci(resultSet.getString("eci"));
                transactionDetailsVO.setTerminalId(resultSet.getString("terminalid"));
                transactionDetailsVO.setVersion(resultSet.getString("version"));
                transactionDetailsVO.setCaptureAmount(resultSet.getString("captureamount"));
                transactionDetailsVO.setRefundAmount(resultSet.getString("refundamount"));
                transactionDetailsVO.setEmiCount(resultSet.getString("emiCount"));
                transactionDetailsVO.setTransactionTime(resultSet.getString("timestamp"));
                transactionDetailsVO.setWalletAmount(resultSet.getString("walletAmount"));
                transactionDetailsVO.setWalletCurrency(resultSet.getString("walletCurrency"));
                transactionDetailsVO.setTransactionType(resultSet.getString("transaction_type"));
                transactionDetailsVO.setTransactionMode(resultSet.getString("transaction_mode"));
                transactionDetailsVO.setAuthorization_code(resultSet.getString("authorization_code"));
            }
            logger.debug("transaction common query----"+psUpdateTransaction);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeResultSet(resultSet);
            Database.closeConnection(con);
        }
        return transactionDetailsVO;
    }

    public List<TransactionDetailsVO> getTransactionforStatusAPI(String tableName,String trackingId, String orderId, String memberId)
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet rs = null;
        List<TransactionDetailsVO> transactionDetailsVOList = new ArrayList<TransactionDetailsVO>();
        TransactionDetailsVO transactionDetailsVO = new TransactionDetailsVO();
        try
        {
            con = Database.getConnection();
            StringBuffer sb = new StringBuffer("select trackingid,accountid,toid,description,orderdescription,amount,currency,redirecturl,status,captureamount,refundamount,payoutamount from "+tableName+" where toid = "+memberId);

            if (functions.isValueNull(trackingId) )
            {
                sb.append(" and trackingid =" + trackingId );
            }

            if (!functions.isValueNull(trackingId) && functions.isValueNull(orderId))
            {
                sb.append(" and description like '" + orderId + "%' order by trackingid desc limit 1");
            }

            pstmt = con.prepareStatement(sb.toString());
            logger.debug("query=====from "+tableName+" ==" + sb.toString());

            rs = pstmt.executeQuery();
            if (rs.next())
            {
                //transactionDetailsVO = new TransactionDetailsVO();
                if(functions.isValueNull(rs.getString("accountid")))
                {
                    transactionDetailsVO = TransactionDetailsVOFactory.getTransactionDetailsVOInstance(Integer.parseInt(rs.getString("accountid")));
                }
                else
                {
                    transactionDetailsVO = new TransactionDetailsVO();
                }
                transactionDetailsVO.setTrackingid(rs.getString("trackingid"));
                transactionDetailsVO.setAccountId(rs.getString("accountid"));
                transactionDetailsVO.setToid(rs.getString("toid"));
                transactionDetailsVO.setDescription(rs.getString("description"));
                transactionDetailsVO.setOrderDescription(rs.getString("orderdescription"));
                transactionDetailsVO.setAmount(rs.getString("amount"));
                transactionDetailsVO.setCurrency(rs.getString("currency"));
                transactionDetailsVO.setRedirectURL(rs.getString("redirecturl"));
                transactionDetailsVO.setStatus(rs.getString("status"));
                transactionDetailsVO.setCaptureAmount(rs.getString("captureamount"));
                transactionDetailsVO.setAccountId(rs.getString("accountid"));
                transactionDetailsVO.setRefundAmount(rs.getString("refundamount"));
                transactionDetailsVO.setPayoutamount(rs.getString("payoutamount"));
                transactionDetailsVOList.add(transactionDetailsVO);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionDetailsVOList;
    }

    public TreeMap<String, String> getCardTypeLists()
    {
        TreeMap<String, String> treeMap = new TreeMap<>();
        Connection con = null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "SELECT bin_brand  FROM bin_details WHERE bin_brand!='null' GROUP BY bin_brand";
            PreparedStatement pstmt = con.prepareStatement(qry);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                treeMap.put(rs.getString("bin_brand"), rs.getString("bin_brand"));
            }
        }
        catch (Exception e)
        {
            logger.error("error", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return treeMap;
    }

    public TreeMap<String, String> getIssuing_BankList()
    {
        TreeMap<String, String> treeMap = new TreeMap<>();
        Connection con = null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "SELECT issuing_bank  FROM bin_details WHERE issuing_bank!='null'";
            PreparedStatement pstmt = con.prepareStatement(qry);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                treeMap.put(rs.getString("issuing_bank"), rs.getString("issuing_bank"));
            }
        }
        catch (Exception e)
        {
            logger.error("error", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return treeMap;
    }

    public List<TransactionDetailsVO> getCommonTransactionDetails(TransactionDetailsVO transactionDetailsVO,String trackingId,String description)
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet rs = null;
        List<TransactionDetailsVO> transactionDetailsVOList = new ArrayList<TransactionDetailsVO>();
        try
        {
            con=Database.getConnection();
            StringBuffer sb =new StringBuffer("select * from transaction_common ");
            if(functions.isValueNull(trackingId) && functions.isValueNull(description))
            {
                sb.append(" where trackingid ="+trackingId+" AND description ='"+description+"'");
            }
            else if(!functions.isValueNull(trackingId) && functions.isValueNull(description))
            {
                sb.append(" where description like '%"+description+"%'");
            }
            else if(functions.isValueNull(trackingId) && !functions.isValueNull(description))
            {
                sb.append(" where trackingid ="+trackingId);
            }
            pstmt=con.prepareStatement(sb.toString());
            logger.debug("query=====from common=="+sb.toString());
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                transactionDetailsVO = new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(rs.getString("trackingid"));
                transactionDetailsVO.setAccountId(rs.getString("accountid"));
                transactionDetailsVO.setPaymodeId(rs.getString("paymodeid"));
                transactionDetailsVO.setCardTypeId(rs.getString("cardtypeid"));
                transactionDetailsVO.setToid(rs.getString("toid"));
                transactionDetailsVO.setTotype(rs.getString("totype"));//totype
                transactionDetailsVO.setFromid(rs.getString("fromid"));//fromid
                transactionDetailsVO.setFromtype(rs.getString("fromtype"));//fromtype
                transactionDetailsVO.setDescription(rs.getString("description"));
                transactionDetailsVO.setOrderDescription(rs.getString("orderdescription"));
                transactionDetailsVO.setAmount(rs.getString("amount"));
                transactionDetailsVO.setCurrency(rs.getString("currency"));
                transactionDetailsVO.setRedirectURL(rs.getString("redirecturl"));
                transactionDetailsVO.setStatus(rs.getString("status"));
                transactionDetailsVO.setFirstName(rs.getString("firstname"));//fn
                transactionDetailsVO.setLastName(rs.getString("lastname"));//ln
                transactionDetailsVO.setName(rs.getString("name"));//name
                transactionDetailsVO.setCcnum(rs.getString("ccnum"));//ccnum
                transactionDetailsVO.setExpdate(rs.getString("expdate"));//expdt
                transactionDetailsVO.setCardtype(rs.getString("cardtype"));//cardtype
                transactionDetailsVO.setEmailaddr(rs.getString("emailaddr"));
                transactionDetailsVO.setCountry(rs.getString("country"));//country
                transactionDetailsVO.setState(rs.getString("state"));//state
                transactionDetailsVO.setCity(rs.getString("city"));//city
                transactionDetailsVO.setStreet(rs.getString("street"));//street
                transactionDetailsVO.setZip(rs.getString("zip"));//zip
                transactionDetailsVO.setTelcc(rs.getString("telnocc"));//telcc
                transactionDetailsVO.setTelno(rs.getString("telno"));//telno
                transactionDetailsVO.setHttpHeader(rs.getString("httpheader"));//httpheadet
                transactionDetailsVO.setCaptureAmount(rs.getString("captureamount"));



                transactionDetailsVOList.add(transactionDetailsVO);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionDetailsVOList;
    }
    public TransactionDetailsVO getTransactionQwipiDetails(String trackingId,String description)
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        TransactionDetailsVO transactionDetailsVO=new TransactionDetailsVO();
        try
        {
            con=Database.getConnection();
            StringBuffer sb =new StringBuffer("select * from transaction_qwipi ");
            if(functions.isValueNull(trackingId) && functions.isValueNull(description))
            {
                sb.append(" where trackingid ="+trackingId+" AND description ='"+description+"'");
            }
            else if(!functions.isValueNull(trackingId) && functions.isValueNull(description))
            {
                sb.append(" where description ='"+description+"' order by trackingid desc limit 1");
            }
            else if(functions.isValueNull(trackingId) && !functions.isValueNull(description))
            {
                sb.append(" where trackingid ="+trackingId);
            }
            pstmt=con.prepareStatement(sb.toString());
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                transactionDetailsVO.setTrackingid(rs.getString("trackingid"));
                transactionDetailsVO.setAccountId(rs.getString("accountid"));
                transactionDetailsVO.setPaymodeId(rs.getString("paymodeid"));
                transactionDetailsVO.setCardTypeId(rs.getString("cardtypeid"));
                transactionDetailsVO.setToid(rs.getString("toid"));
                transactionDetailsVO.setTotype(rs.getString("totype"));
                transactionDetailsVO.setFromid(rs.getString("fromid"));
                transactionDetailsVO.setFromtype(rs.getString("fromtype"));
                transactionDetailsVO.setDescription(rs.getString("description"));
                transactionDetailsVO.setOrderDescription(rs.getString("orderdescription"));
                transactionDetailsVO.setAmount(rs.getString("amount"));
                transactionDetailsVO.setCurrency(rs.getString("currency"));
                transactionDetailsVO.setRedirectURL(rs.getString("redirecturl"));
                transactionDetailsVO.setStatus(rs.getString("status"));
                transactionDetailsVO.setFirstName(rs.getString("firstname"));
                transactionDetailsVO.setLastName(rs.getString("lastname"));
                transactionDetailsVO.setName(rs.getString("name"));
                transactionDetailsVO.setCcnum(rs.getString("ccnum"));
                transactionDetailsVO.setExpdate(rs.getString("expdate"));
                transactionDetailsVO.setCardtype(rs.getString("cardtype"));
                transactionDetailsVO.setEmailaddr(rs.getString("emailaddr"));
                transactionDetailsVO.setCountry(rs.getString("country"));
                transactionDetailsVO.setState(rs.getString("state"));
                transactionDetailsVO.setCity(rs.getString("city"));
                transactionDetailsVO.setStreet(rs.getString("street"));
                transactionDetailsVO.setZip(rs.getString("zip"));
                transactionDetailsVO.setTelcc(rs.getString("telnocc"));
                transactionDetailsVO.setTelno(rs.getString("telno"));
                transactionDetailsVO.setHttpHeader(rs.getString("httpheader"));
                transactionDetailsVO.setCaptureAmount(rs.getString("captureamount"));

            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionDetailsVO;
    }
    public TransactionDetailsVO getTransactionEcoreDetails(String trackingId,String description)
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        TransactionDetailsVO transactionDetailsVO=new TransactionDetailsVO();
        try
        {
            con=Database.getConnection();
            StringBuffer sb =new StringBuffer("select * from transaction_ecore ");
            if(functions.isValueNull(trackingId) && functions.isValueNull(description))
            {
                sb.append(" where trackingid ="+trackingId+" AND description ='"+description+"'");
            }
            else if(!functions.isValueNull(trackingId) && functions.isValueNull(description))
            {
                sb.append(" where description ='"+description+"' order by trackingid desc limit 1");
            }
            else if(functions.isValueNull(trackingId) && !functions.isValueNull(description))
            {
                sb.append(" where trackingid ="+trackingId);
            }
            pstmt=con.prepareStatement(sb.toString());
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                transactionDetailsVO.setTrackingid(rs.getString("trackingid"));
                transactionDetailsVO.setAccountId(rs.getString("accountid"));
                transactionDetailsVO.setPaymodeId(rs.getString("paymodeid"));
                transactionDetailsVO.setCardTypeId(rs.getString("cardtypeid"));
                transactionDetailsVO.setToid(rs.getString("toid"));
                transactionDetailsVO.setTotype(rs.getString("totype"));
                transactionDetailsVO.setFromid(rs.getString("fromid"));
                transactionDetailsVO.setFromtype(rs.getString("fromtype"));
                transactionDetailsVO.setDescription(rs.getString("description"));
                transactionDetailsVO.setOrderDescription(rs.getString("orderdescription"));
                transactionDetailsVO.setAmount(rs.getString("amount"));
                transactionDetailsVO.setCurrency(rs.getString("currency"));
                transactionDetailsVO.setRedirectURL(rs.getString("redirecturl"));
                transactionDetailsVO.setStatus(rs.getString("status"));
                transactionDetailsVO.setFirstName(rs.getString("firstname"));
                transactionDetailsVO.setLastName(rs.getString("lastname"));
                transactionDetailsVO.setName(rs.getString("name"));
                transactionDetailsVO.setCcnum(rs.getString("ccnum"));
                transactionDetailsVO.setExpdate(rs.getString("expdate"));
                transactionDetailsVO.setCardtype(rs.getString("cardtype"));
                transactionDetailsVO.setEmailaddr(rs.getString("emailaddr"));
                transactionDetailsVO.setCountry(rs.getString("country"));
                transactionDetailsVO.setState(rs.getString("state"));
                transactionDetailsVO.setCity(rs.getString("city"));
                transactionDetailsVO.setStreet(rs.getString("street"));
                transactionDetailsVO.setZip(rs.getString("zip"));
                transactionDetailsVO.setTelcc(rs.getString("telnocc"));
                transactionDetailsVO.setTelno(rs.getString("telno"));
                transactionDetailsVO.setHttpHeader(rs.getString("httpheader"));
                transactionDetailsVO.setCaptureAmount(rs.getString("captureamount"));

            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionDetailsVO;
    }
    public BinDetailsVO getBinDetails(String trackingid) throws PZDBViolationException
    {
        Connection conn = null;
        BinDetailsVO binDetailsVO = new BinDetailsVO();

        try
        {
            conn = Database.getConnection();
            String query = "select * from bin_details where icicitransid=?";

            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1,trackingid);
            ResultSet resultSet = p.executeQuery();

            if(resultSet.next())
            {
                binDetailsVO.setFirst_six(resultSet.getString("first_six"));
                binDetailsVO.setLast_four(resultSet.getString("last_four"));
                binDetailsVO.setBin_brand(resultSet.getString("bin_brand"));
                binDetailsVO.setBin_transaction_type(resultSet.getString("bin_trans_type"));
                binDetailsVO.setBin_card_type(resultSet.getString("bin_card_type"));
                binDetailsVO.setBin_card_category(resultSet.getString("bin_card_category"));
                binDetailsVO.setBin_usage_type(resultSet.getString("bin_usage_type"));
            }

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionDAO.java", "getBinDetails()", null, "Common", "Error in connection:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionDAO.java", "getBinDetails()", null, "Common", "Error in connection:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return binDetailsVO;
    }



    public void updateBinDetailsSuccessful(int trackingid) throws PZDBViolationException
    {
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String binUpdateQuery = "update bin_details set isSuccessful='Y' where icicitransid=?";
            PreparedStatement pPStmt = connection.prepareStatement(binUpdateQuery);
            pPStmt.setString(1,String.valueOf(trackingid));
            pPStmt.executeUpdate();
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::",e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "updateBinDetailsSuccessful()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ",se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "updateBinDetailsSuccessful()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }

    }

    public List<TransactionDetailsVO> getAllStuckTransactionList(String status) throws PZDBViolationException
    {
        List<TransactionDetailsVO> transactionDetailsVOs=new ArrayList();
        String tableName = "";
        String fields = "";
        StringBuffer query = new StringBuffer();
        Connection conn = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            Set<String> gatewaySet =GatewayTypeService.getGateways() ;
            Set<String> tables = Database.getTableSet(gatewaySet);
            Iterator i = tables.iterator();
            while(i.hasNext())
            {
                tableName = (String)i.next();
                if(tableName.equals("transaction_icicicredit")||tableName.equals("transaction_icicicredit_archive"))
                {
                    fields = tableName+".icicitransid as transid,toid,status,amount,description";
                }
                else
                {
                    fields = "trackingid as transid,toid,status,amount,description";
                }

                if(tableName.equals("transaction_icicicredit")||tableName.equals("transaction_icicicredit_archive"))
                {
                    query.append("select " + fields + " from " + tableName + " where STATUS='" + status + "' ");
                }
                else
                {
                    query.append("select " + fields + " from " + tableName + " where STATUS='" + status + "' ");
                }
                if(i.hasNext())
                    query.append(" UNION ");

            }

            logger.debug("authstarted query======"+query.toString());
            //query.append(" order by  dtstamp DESC,transid ");
            ResultSet rs=Database.executeQuery(query.toString(), conn);
            TransactionDetailsVO transactionDetailsVO=null;
            while (rs.next())
            {
                transactionDetailsVO=new TransactionDetailsVO();
                transactionDetailsVO.setToid(rs.getString("toid"));
                transactionDetailsVO.setTrackingid(rs.getString("transid"));
                transactionDetailsVO.setDescription(rs.getString("description"));
                transactionDetailsVO.setAmount(rs.getString("amount"));
                transactionDetailsVO.setStatus(rs.getString("status"));
                transactionDetailsVOs.add(transactionDetailsVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::",e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getAllStuckTransactionList()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ",se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getAllStuckTransactionList()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transactionDetailsVOs;
    }

    public String getMerchantLastTransactionDate(String toId)throws PZDBViolationException
    {
        StringBuffer query = new StringBuffer();
        String lastTransactionDate=null;
        Set<String> gatewayTypeSet=GatewayTypeService.getGateways();
        Set<String> tableNames = Database.getTableSet(gatewayTypeSet);
        for(String tableName:tableNames)
        {
            lastTransactionDate=getMerchantLastTransactionDate(toId,tableName);
            if(lastTransactionDate!=null)
            {
                break;
            }
        }
        return lastTransactionDate;
    }
    public String getMerchantLastTransactionDate(String memberId,String tableName)throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String lastTransactionDate=null;
        try
        {
            connection = Database.getConnection();
            String query= "SELECT trackingid,FROM_UNIXTIME(dtstamp) AS lasttransactiondate FROM "+tableName+" WHERE trackingid=(SELECT MIN(trackingid) FROM transaction_common WHERE toid=?)";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, memberId);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                lastTransactionDate=rs.getString("lasttransactiondate");
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::",e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getMerchantLastTransactionDate()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ",se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(),"getMerchantLastTransactionDate()",null,"Common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return lastTransactionDate;
    }

    public CommonValidatorVO getDetailFromTransCommon(CommonValidatorVO commonValidatorVO,TokenDetailsVO tokenDetailsVO)
    {
        Connection con = null;
        boolean status=false;
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        TerminalVO terminalVO = commonValidatorVO.getTerminalVO();

        try
        {
            con=Database.getConnection();
            String update = "select * from transaction_common where trackingid=?";
            PreparedStatement psUpdateTransaction =con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,tokenDetailsVO.getTrackingId());
            logger.error("psUpdateTransaction----->"+psUpdateTransaction);
            ResultSet resultSet=psUpdateTransaction.executeQuery();
            if(resultSet.next())
            {
                genericTransDetailsVO.setFromid(resultSet.getString("fromid"));//fromid
                genericTransDetailsVO.setFromtype(resultSet.getString("fromtype"));//fromtype
                genericTransDetailsVO.setOrderDesc(resultSet.getString("orderdescription"));
                genericTransDetailsVO.setCurrency(resultSet.getString("currency"));
                genericTransDetailsVO.setHeader(resultSet.getString("httpheader"));//httpheadet
                if (addressDetailsVO != null && ((!functions.isValueNull(addressDetailsVO.getCity())) || (!functions.isValueNull(addressDetailsVO.getCountry())) || (!functions.isValueNull(addressDetailsVO.getZipCode())) || (!functions.isValueNull(addressDetailsVO.getState())) || (!functions.isValueNull(addressDetailsVO.getStreet())) || (!functions.isValueNull(addressDetailsVO.getPhone())) || !functions.isValueNull(addressDetailsVO.getTelnocc()) || !functions.isValueNull(addressDetailsVO.getEmail())))
                {
                    addressDetailsVO.setFirstname(resultSet.getString("firstname"));//fn
                    addressDetailsVO.setLastname(resultSet.getString("lastname"));//ln
                    addressDetailsVO.setEmail(resultSet.getString("emailaddr"));
                    addressDetailsVO.setCountry(resultSet.getString("country"));//country
                    addressDetailsVO.setState(resultSet.getString("state"));//state
                    addressDetailsVO.setCity(resultSet.getString("city"));//city
                    addressDetailsVO.setStreet(resultSet.getString("street"));//street
                    addressDetailsVO.setZipCode(resultSet.getString("zip"));//zip
                    addressDetailsVO.setTelnocc(resultSet.getString("telnocc"));//telcc
                    addressDetailsVO.setPhone(resultSet.getString("telno"));//telno
                }

                commonValidatorVO.setPaymentType(resultSet.getString("paymodeid"));
                commonValidatorVO.setCardType(resultSet.getString("cardtypeid"));
                commonValidatorVO.setTerminalId(resultSet.getString("terminalid"));
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setTerminalVO(terminalVO);
            }
            if(tokenDetailsVO.getAddressDetailsVO()!=null)
            {
                commonValidatorVO.setAddressDetailsVO(tokenDetailsVO.getAddressDetailsVO());
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return commonValidatorVO;
    }

    public List<TransactionDetailsVO> getListOfBinTransaction(TerminalVO terminalVO, DateVO dateVO, String bibStr,String lastFour)
    {
        List<TransactionDetailsVO> transactionDetailsVOs = new ArrayList();
        String tableName = "";
        String fields = "";
        StringBuffer query = new StringBuffer();
        Connection conn = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            AccountUtil accountUtil = new AccountUtil();
            tableName = accountUtil.getTableNameFromAccountId(terminalVO.getAccountId());
            if (tableName.equals("transaction_icicicredit") || tableName.equals("transaction_icicicredit_archive"))
            {
                fields = tableName + ".icicitransid as transid,toid,status,amount,description,firstname,lastname,from_unixtime(dtstamp) as transactiontime,currency,first_six,last_four";
            }
            else
            {
                fields = "trackingid as transid,toid,status,amount,description,firstname,lastname,from_unixtime(dtstamp) as transactiontime,currency,first_six,last_four";
            }

            if (tableName.equals("transaction_icicicredit") || tableName.equals("transaction_icicicredit_archive"))
            {
                query.append("select " + fields + " from " + tableName + " as t join bin_details as bd where on t.icicitransid=bd.icicitransid where t.icicitransid>0 and t.paymodeid=" + terminalVO.getPaymodeId() + " and t.cardtypeid=" + terminalVO.getCardTypeId() + " and t.accountid=" + terminalVO.getAccountId() + " and t.toid=" + terminalVO.getMemberId() + " and FROM_UNIXTIME(t.dtstamp)>='" + dateVO.getStartDate() + "' AND FROM_UNIXTIME(t.dtstamp)<='" + dateVO.getEndDate() + "' and bd.first_six in(" + bibStr + ") and bd.last_four in ("+lastFour+")");
            }
            else
            {
                query.append("select " + fields + " from " + tableName + " as t JOIN bin_details AS bd ON t.trackingid=bd.icicitransid where t.trackingid>0 and t.paymodeid=" + terminalVO.getPaymodeId() + " and t.cardtypeid=" + terminalVO.getCardTypeId() + " and t.accountid=" + terminalVO.getAccountId() + " and t.toid=" + terminalVO.getMemberId() + " and FROM_UNIXTIME(t.dtstamp)>='" + dateVO.getStartDate() + "' AND FROM_UNIXTIME(t.dtstamp)<='" + dateVO.getEndDate() + "' and bd.first_six in(" + bibStr + ") and bd.last_four in("+lastFour+")");
            }

            logger.debug("query======" + query.toString());
            ResultSet rs = Database.executeQuery(query.toString(), conn);
            TransactionDetailsVO transactionDetailsVO = null;
            while (rs.next())
            {
                transactionDetailsVO = new TransactionDetailsVO();
                transactionDetailsVO.setToid(rs.getString("toid"));
                transactionDetailsVO.setTrackingid(rs.getString("transid"));
                transactionDetailsVO.setDescription(rs.getString("description"));
                transactionDetailsVO.setAmount(rs.getString("amount"));
                transactionDetailsVO.setStatus(rs.getString("status"));
                transactionDetailsVO.setFirstName(rs.getString("firstname"));
                transactionDetailsVO.setLastName(rs.getString("lastname"));
                transactionDetailsVO.setTransactionTime(rs.getString("transactiontime"));
                transactionDetailsVO.setCurrency(rs.getString("currency"));
                transactionDetailsVO.setFirstSix(rs.getString("first_six"));
                transactionDetailsVO.setLastFour(rs.getString("last_four"));
                transactionDetailsVOs.add(transactionDetailsVO);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transactionDetailsVOs;
    }
    public List<TransactionDetailsVO> getListOfBinTransactionBYAmount(TerminalVO terminalVO, DateVO dateVO, String bibStr,String lastFour,String amount)
    {
        List<TransactionDetailsVO> transactionDetailsVOs = new ArrayList();
        String tableName = "";
        String fields = "";
        StringBuffer query = new StringBuffer();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            AccountUtil accountUtil = new AccountUtil();
            tableName = accountUtil.getTableNameFromAccountId(terminalVO.getAccountId());
            if (tableName.equals("transaction_icicicredit") || tableName.equals("transaction_icicicredit_archive"))
            {
                fields = tableName + ".icicitransid as transid,toid,status,amount,description,firstname,lastname,from_unixtime(dtstamp) as transactiontime,currency,first_six,last_four";
            }
            else
            {
                fields = "trackingid as transid,toid,status,amount,description,firstname,lastname,from_unixtime(dtstamp) as transactiontime,currency,first_six,last_four";
            }

            if (tableName.equals("transaction_icicicredit") || tableName.equals("transaction_icicicredit_archive"))
            {
                query.append("select " + fields + " from " + tableName + " as t join bin_details as bd where on t.icicitransid=bd.icicitransid where t.icicitransid>0 and t.paymodeid=" + terminalVO.getPaymodeId() + " and t.cardtypeid=" + terminalVO.getCardTypeId() + " and t.accountid=" + terminalVO.getAccountId() + " and t.toid=" + terminalVO.getMemberId() + " and FROM_UNIXTIME(t.dtstamp)>='" + dateVO.getStartDate() + "' AND FROM_UNIXTIME(t.dtstamp)<='" + dateVO.getEndDate() + "' and bd.first_six in(" + bibStr + ") and bd.last_four in ("+lastFour+") and t.amount='"+amount+"'");
            }
            else
            {
                query.append("select " + fields + " from " + tableName + " as t JOIN bin_details AS bd ON t.trackingid=bd.icicitransid where t.trackingid>0 and t.paymodeid=" + terminalVO.getPaymodeId() + " and t.cardtypeid=" + terminalVO.getCardTypeId() + " and t.accountid=" + terminalVO.getAccountId() + " and t.toid=" + terminalVO.getMemberId() + " and FROM_UNIXTIME(t.dtstamp)>='" + dateVO.getStartDate() + "' AND FROM_UNIXTIME(t.dtstamp)<='" + dateVO.getEndDate() + "' and bd.first_six in(" + bibStr + ") and bd.last_four in("+lastFour+") and t.amount='"+amount+"'");
            }

            logger.debug("query======" + query.toString());
            ResultSet rs = Database.executeQuery(query.toString(), conn);
            TransactionDetailsVO transactionDetailsVO = null;
            while (rs.next())
            {
                transactionDetailsVO = new TransactionDetailsVO();
                transactionDetailsVO.setToid(rs.getString("toid"));
                transactionDetailsVO.setTrackingid(rs.getString("transid"));
                transactionDetailsVO.setDescription(rs.getString("description"));
                transactionDetailsVO.setAmount(rs.getString("amount"));
                transactionDetailsVO.setStatus(rs.getString("status"));
                transactionDetailsVO.setFirstName(rs.getString("firstname"));
                transactionDetailsVO.setLastName(rs.getString("lastname"));
                transactionDetailsVO.setTransactionTime(rs.getString("transactiontime"));
                transactionDetailsVO.setCurrency(rs.getString("currency"));
                transactionDetailsVO.setFirstSix(rs.getString("first_six"));
                transactionDetailsVO.setLastFour(rs.getString("last_four"));
                transactionDetailsVOs.add(transactionDetailsVO);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transactionDetailsVOs;
    }
    public Map<String,TransactionDetailsVO> getListOfBinTransactionBYAmountMap(TerminalVO terminalVO, DateVO dateVO, String bibStr,String lastFour,String amount)
    {
        Map<String,TransactionDetailsVO> stringListMap=new TreeMap();
        //List<TransactionDetailsVO> transactionDetailsVOs = new ArrayList();
        String tableName = "";
        String fields = "";
        StringBuffer query = new StringBuffer();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            AccountUtil accountUtil = new AccountUtil();
            tableName = accountUtil.getTableNameFromAccountId(terminalVO.getAccountId());
            if (tableName.equals("transaction_icicicredit") || tableName.equals("transaction_icicicredit_archive"))
            {
                fields = tableName + ".icicitransid as transid,toid,status,amount,description,firstname,lastname,from_unixtime(dtstamp) as transactiontime,currency,first_six,last_four";
            }
            else
            {
                fields = "trackingid as transid,toid,status,amount,description,firstname,lastname,from_unixtime(dtstamp) as transactiontime,currency,first_six,last_four";
            }

            if (tableName.equals("transaction_icicicredit") || tableName.equals("transaction_icicicredit_archive"))
            {
                query.append("select " + fields + " from " + tableName + " as t join bin_details as bd where on t.icicitransid=bd.icicitransid where t.icicitransid>0 and t.paymodeid=" + terminalVO.getPaymodeId() + " and t.cardtypeid=" + terminalVO.getCardTypeId() + " and t.accountid=" + terminalVO.getAccountId() + " and t.toid=" + terminalVO.getMemberId() + " and FROM_UNIXTIME(t.dtstamp)>='" + dateVO.getStartDate() + "' AND FROM_UNIXTIME(t.dtstamp)<='" + dateVO.getEndDate() + "' and bd.first_six in(" + bibStr + ") and bd.last_four in ("+lastFour+") and t.amount='"+amount+"'");
            }
            else
            {
                query.append("select " + fields + " from " + tableName + " as t JOIN bin_details AS bd ON t.trackingid=bd.icicitransid where t.trackingid>0 and t.paymodeid=" + terminalVO.getPaymodeId() + " and t.cardtypeid=" + terminalVO.getCardTypeId() + " and t.accountid=" + terminalVO.getAccountId() + " and t.toid=" + terminalVO.getMemberId() + " and FROM_UNIXTIME(t.dtstamp)>='" + dateVO.getStartDate() + "' AND FROM_UNIXTIME(t.dtstamp)<='" + dateVO.getEndDate() + "' and bd.first_six in(" + bibStr + ") and bd.last_four in("+lastFour+") and t.amount='"+amount+"'");
            }

            logger.debug("query======" + query.toString());
            ResultSet rs = Database.executeQuery(query.toString(), conn);
            TransactionDetailsVO transactionDetailsVO = null;
            while (rs.next())
            {
                transactionDetailsVO = new TransactionDetailsVO();
                transactionDetailsVO.setToid(rs.getString("toid"));
                transactionDetailsVO.setTrackingid(rs.getString("transid"));
                transactionDetailsVO.setDescription(rs.getString("description"));
                transactionDetailsVO.setAmount(rs.getString("amount"));
                transactionDetailsVO.setStatus(rs.getString("status"));
                transactionDetailsVO.setFirstName(rs.getString("firstname"));
                transactionDetailsVO.setLastName(rs.getString("lastname"));
                transactionDetailsVO.setTransactionTime(rs.getString("transactiontime"));
                transactionDetailsVO.setCurrency(rs.getString("currency"));
                transactionDetailsVO.setFirstSix(rs.getString("first_six"));
                transactionDetailsVO.setLastFour(rs.getString("last_four"));
                /*transactionDetailsVOs.add(transactionDetailsVO);*/

                stringListMap.put(rs.getString("transid"), transactionDetailsVO);

            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return stringListMap;
    }
    public List<TransactionDetailsVO> getListHighRiskAmountTransaction(TerminalVO terminalVO, DateVO dateVO, String amount)
    {
        List<TransactionDetailsVO> transactionDetailsVOs = new ArrayList();
        String tableName = "";
        String fields = "";
        StringBuffer query = new StringBuffer();
        Connection conn = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            AccountUtil accountUtil = new AccountUtil();
            tableName = accountUtil.getTableNameFromAccountId(terminalVO.getAccountId());
            if (tableName.equals("transaction_icicicredit") || tableName.equals("transaction_icicicredit_archive"))
            {
                fields = tableName + ".icicitransid as transid,toid,status,amount,description,firstname,lastname,from_unixtime(dtstamp) as transactiontime,currency";
            }
            else
            {
                fields = "trackingid as transid,toid,status,amount,description,firstname,lastname,from_unixtime(dtstamp) as transactiontime,currency";
            }

            if (tableName.equals("transaction_icicicredit") || tableName.equals("transaction_icicicredit_archive"))
            {
                query.append("select " + fields + " from " + tableName + " as t where t.icicitransid>0 and t.paymodeid=" + terminalVO.getPaymodeId() + " and t.cardtypeid=" + terminalVO.getCardTypeId() + " and t.accountid=" + terminalVO.getAccountId() + " and t.toid=" + terminalVO.getMemberId() + " and FROM_UNIXTIME(t.dtstamp)>='" + dateVO.getStartDate() + "' AND FROM_UNIXTIME(t.dtstamp)<='" + dateVO.getEndDate() + "' and t.amount>'" + amount + "'");
            }
            else
            {
                query.append("select " + fields + " from " + tableName + " as t where t.trackingid>0 and t.paymodeid=" + terminalVO.getPaymodeId() + " and t.cardtypeid=" + terminalVO.getCardTypeId() + " and t.accountid=" + terminalVO.getAccountId() + " and t.toid=" + terminalVO.getMemberId() + " and FROM_UNIXTIME(t.dtstamp)>='" + dateVO.getStartDate() + "' AND FROM_UNIXTIME(t.dtstamp)<='" + dateVO.getEndDate() + "' and t.amount>'" + amount + "'");
            }

            logger.debug("query======" + query.toString());
            ResultSet rs = Database.executeQuery(query.toString(), conn);
            TransactionDetailsVO transactionDetailsVO = null;
            while (rs.next())
            {
                transactionDetailsVO = new TransactionDetailsVO();
                transactionDetailsVO.setToid(rs.getString("toid"));
                transactionDetailsVO.setTrackingid(rs.getString("transid"));
                transactionDetailsVO.setDescription(rs.getString("description"));
                transactionDetailsVO.setAmount(rs.getString("amount"));
                transactionDetailsVO.setStatus(rs.getString("status"));
                transactionDetailsVO.setFirstName(rs.getString("firstname"));
                transactionDetailsVO.setLastName(rs.getString("lastname"));
                transactionDetailsVO.setTransactionTime(rs.getString("transactiontime"));
                transactionDetailsVO.setCurrency(rs.getString("currency"));

                transactionDetailsVOs.add(transactionDetailsVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::", e);
            //PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getListHighRiskAmountTransaction()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            //PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getListHighRiskAmountTransaction()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transactionDetailsVOs;
    }
    public List<TransactionDetailsVO> getTransactionListByRejectReason(TerminalVO terminalVO, DateVO dateVO,String rejectReason)
    {
        List<TransactionDetailsVO> transactionDetailsVOs = new ArrayList();
        StringBuffer query = new StringBuffer();
        Connection conn = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            query.append("select *,from_unixtime(dtstamp) as transactiontime from transaction_fail_log where id> 0 and toid=" + terminalVO.getMemberId() + " and terminalid=" + terminalVO.getTerminalId() + " and FROM_UNIXTIME(dtstamp)>='" + dateVO.getStartDate() + "' AND FROM_UNIXTIME(dtstamp)<='" + dateVO.getEndDate() + "' and rejectreason='" + rejectReason + "'");
            ResultSet rs = Database.executeQuery(query.toString(), conn);
            TransactionDetailsVO transactionDetailsVO = null;
            while (rs.next())
            {
                transactionDetailsVO = new TransactionDetailsVO();
                transactionDetailsVO.setToid(rs.getString("toid"));
                transactionDetailsVO.setTrackingid(rs.getString("id"));
                transactionDetailsVO.setDescription(rs.getString("description"));
                transactionDetailsVO.setAmount(rs.getString("amount"));
                transactionDetailsVO.setFirstName(rs.getString("firstname"));
                transactionDetailsVO.setLastName(rs.getString("lastname"));
                transactionDetailsVO.setTransactionTime(rs.getString("transactiontime"));
                transactionDetailsVOs.add(transactionDetailsVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::", e);
            //PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getListHighRiskAmountTransaction()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            //PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getListHighRiskAmountTransaction()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transactionDetailsVOs;
    }
    public List<TransactionDetailsVO> getListPreAuthTransactionList(TerminalVO terminalVO,DateVO dateVO)
    {
        List<TransactionDetailsVO> transactionDetailsVOs = new ArrayList();
        String tableName = "";
        String fields = "";
        StringBuffer query = new StringBuffer();
        Connection conn = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            AccountUtil accountUtil = new AccountUtil();
            tableName = accountUtil.getTableNameFromAccountId(terminalVO.getAccountId());
            if (tableName.equals("transaction_icicicredit") || tableName.equals("transaction_icicicredit_archive"))
            {
                fields = tableName + ".icicitransid as transid,toid,status,amount,description,firstname,lastname,from_unixtime(dtstamp) as transactiontime,currency";
            }
            else
            {
                fields = "trackingid as transid,toid,status,amount,description,firstname,lastname,from_unixtime(dtstamp) as transactiontime,currency";
            }
            if (tableName.equals("transaction_icicicredit") || tableName.equals("transaction_icicicredit_archive"))
            {
                query.append("select " + fields + " from " + tableName + " as t where t.icicitransid>0 and t.paymodeid=" + terminalVO.getPaymodeId() + " and t.cardtypeid=" + terminalVO.getCardTypeId() + " and t.accountid=" + terminalVO.getAccountId() + " and t.toid=" + terminalVO.getMemberId() + " AND FROM_UNIXTIME(t.dtstamp)<='" + dateVO.getEndDate() + "' and status='authsuccessful' ");
            }
            else
            {
                query.append("select " + fields + " from " + tableName + " as t where t.trackingid>0 and t.paymodeid=" + terminalVO.getPaymodeId() + " and t.cardtypeid=" + terminalVO.getCardTypeId() + " and t.accountid=" + terminalVO.getAccountId() + " and t.toid=" + terminalVO.getMemberId() + " AND FROM_UNIXTIME(t.dtstamp)<='" + dateVO.getEndDate() + "' and status='authsuccessful' ");
            }

            logger.debug("query======" + query.toString());
            ResultSet rs = Database.executeQuery(query.toString(), conn);
            TransactionDetailsVO transactionDetailsVO = null;
            while (rs.next())
            {
                transactionDetailsVO = new TransactionDetailsVO();
                transactionDetailsVO.setToid(rs.getString("toid"));
                transactionDetailsVO.setTrackingid(rs.getString("transid"));
                transactionDetailsVO.setDescription(rs.getString("description"));
                transactionDetailsVO.setAmount(rs.getString("amount"));
                transactionDetailsVO.setStatus(rs.getString("status"));
                transactionDetailsVO.setFirstName(rs.getString("firstname"));
                transactionDetailsVO.setLastName(rs.getString("lastname"));
                transactionDetailsVO.setTransactionTime(rs.getString("transactiontime"));
                transactionDetailsVO.setCurrency(rs.getString("currency"));

                transactionDetailsVOs.add(transactionDetailsVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::", e);
            //PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getListPreAuthTransactionList()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            //PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getListPreAuthTransactionList()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transactionDetailsVOs;
    }
    public TransactionSummaryVO getGatewayAccountProcessingDetails(GatewayAccount gatewayAccount, String sTableName) throws PZDBViolationException
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getConnection();
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
            Database.closeConnection(con);
        }
        return transactionSummaryVO;

    }
    public TransactionSummaryVO getGatewayAccountProcessingDetails(GatewayAccount gatewayAccount,DateVO dateVO,String tableName) throws PZDBViolationException
    {
        long  authfailedCount=0;
        long  settledCount = 0;
        long  reversedCount = 0;
        long  chargebackCount = 0;
        long  totalSuccessCount = 0;

        double authfailedAmount=0.00;
        double settledAmount=0.0;
        double reversedAmount = 0.0;
        double chargebackAmount =0.0;
        double totalSuccessAmount =0.0;

        TransactionSummaryVO grossSummaryVO=new TransactionSummaryVO();
        TransactionSummaryVO summaryVO;

        summaryVO=getGatewayAccountProcessingAmount(gatewayAccount, dateVO.getStartDate(), dateVO.getEndDate(), tableName);

        totalSuccessAmount=summaryVO.getTotalProcessingAmount();
        totalSuccessCount=summaryVO.getTotalProcessingCount();

        summaryVO=getAuthFailCountAmountByDtstamp(gatewayAccount, dateVO.getStartDate(), dateVO.getEndDate(), tableName);
        authfailedCount=summaryVO.getCountOfAuthfailed();
        authfailedAmount=summaryVO.getAuthfailedAmount();

        summaryVO=getChargebackCountAmountByTimestamp(gatewayAccount, dateVO.getStartDate(), dateVO.getEndDate(), tableName);
        chargebackCount=summaryVO.getCountOfChargeback();
        chargebackAmount=summaryVO.getChargebackAmount();

        summaryVO=getReversalCountAmountByTimestamp(gatewayAccount, dateVO.getStartDate(), dateVO.getEndDate(), tableName);
        reversedCount=summaryVO.getCountOfReversed();
        reversedAmount=summaryVO.getReversedAmount();


        grossSummaryVO.setCountOfAuthfailed(authfailedCount);
        grossSummaryVO.setAuthfailedAmount(authfailedAmount);

        grossSummaryVO.setCountOfSettled(settledCount);
        grossSummaryVO.setSettledAmount(settledAmount);

        grossSummaryVO.setCountOfReversed(reversedCount);
        grossSummaryVO.setReversedAmount(reversedAmount);

        grossSummaryVO.setCountOfChargeback(chargebackCount);
        grossSummaryVO.setChargebackAmount(chargebackAmount);

        grossSummaryVO.setTotalProcessingAmount(totalSuccessAmount);
        grossSummaryVO.setTotalProcessingCount(totalSuccessCount);

        return grossSummaryVO;
    }

    public TransactionSummaryVO getGatewayTypeProcessingDetails(GatewayType gatewayType, DateVO dateVO, String tableName) throws PZDBViolationException
    {
        long authfailedCount = 0;
        long authstartedCount = 0;
        long settledCount = 0;
        long reversedCount = 0;
        long chargebackCount = 0;
        long totalSuccessCount = 0;

        double authfailedAmount = 0.00;
        double authstartedAmount = 0.00;
        double settledAmount = 0.0;
        double reversedAmount = 0.0;
        double chargebackAmount = 0.0;
        double totalSuccessAmount = 0.0;


        TransactionSummaryVO grossSummaryVO = new TransactionSummaryVO();
        TransactionSummaryVO summaryVO;

        summaryVO = getGatewayTypeProcessingAmount(gatewayType, dateVO.getStartDate(), dateVO.getEndDate(), tableName);

        totalSuccessAmount = summaryVO.getTotalProcessingAmount();
        totalSuccessCount = summaryVO.getTotalProcessingCount();

        summaryVO = getAuthFailCountAmountByDtstamp(gatewayType, dateVO.getStartDate(), dateVO.getEndDate(), tableName);
        authfailedCount = summaryVO.getCountOfAuthfailed();
        authfailedAmount = summaryVO.getAuthfailedAmount();

        summaryVO = getAuthStartedCountAmountByDtstamp(gatewayType, dateVO.getStartDate(), dateVO.getEndDate(), tableName);
        authstartedCount = summaryVO.getAuthstartedCount();
        authstartedAmount = summaryVO.getAuthstartedAmount();

        summaryVO = getChargebackCountAmountByTimestamp(gatewayType, dateVO.getStartDate(), dateVO.getEndDate(), tableName);
        chargebackCount = summaryVO.getCountOfChargeback();
        chargebackAmount = summaryVO.getChargebackAmount();

        summaryVO = getReversalCountAmountByTimestamp(gatewayType, dateVO.getStartDate(), dateVO.getEndDate(), tableName);
        reversedCount = summaryVO.getCountOfReversed();
        reversedAmount = summaryVO.getReversedAmount();

        grossSummaryVO.setCountOfAuthfailed(authfailedCount);
        grossSummaryVO.setAuthfailedAmount(authfailedAmount);

        grossSummaryVO.setCountOfSettled(settledCount);
        grossSummaryVO.setSettledAmount(settledAmount);

        grossSummaryVO.setCountOfReversed(reversedCount);
        grossSummaryVO.setReversedAmount(reversedAmount);

        grossSummaryVO.setCountOfChargeback(chargebackCount);
        grossSummaryVO.setChargebackAmount(chargebackAmount);

        grossSummaryVO.setTotalProcessingAmount(totalSuccessAmount);
        grossSummaryVO.setTotalProcessingCount(totalSuccessCount);

        grossSummaryVO.setAuthstartedCount(authstartedCount);
        grossSummaryVO.setAuthstartedAmount(authstartedAmount);


        return grossSummaryVO;
    }
    public TransactionSummaryVO getGatewayAccountProcessingAmount(GatewayAccount gatewayAccount,String startDate, String endDate,String tableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getConnection();
            StringBuilder strQry = new StringBuilder("select count(*) as 'count',sum(captureamount) as 'amount', from " + tableName + " where accountid=? and status in('settled','reversed','chargeback') and FROM_UNIXTIME(dtstamp) >=? and FROM_UNIXTIME(dtstamp) <=? ");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, gatewayAccount.getAccountId() );
            preparedStatement.setString(2,startDate);
            preparedStatement.setString(3,endDate);
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
            Database.closeConnection(con);
        }
        return transactionSummaryVO;
    }

    public TransactionSummaryVO getGatewayTypeProcessingAmount(GatewayType gatewayType, String startDate, String endDate, String tableName) throws PZDBViolationException
    {
/*
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            startDate=String.valueOf(dateformat.parse(startDate).getTime()/1000);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        try
        {
            endDate= String.valueOf(dateformat.parse(endDate).getTime()/1000);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }*/
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getConnection();
            StringBuilder strQry = new StringBuilder("select count(*) as 'count',sum(amount) as 'amount' from " + tableName + " where fromtype=? and currency=? and totype=? and status in('settled','reversed','chargeback','capturesuccess','authsuccessful','markedforreversal','capturestarted') and FROM_UNIXTIME(dtstamp) >=? and FROM_UNIXTIME(dtstamp) <=? ");
            //StringBuilder strQry = new StringBuilder("select count(*) as 'count',sum(amount) as 'amount' from " + tableName + " where fromtype=? and currency=? and totype=? and status in('settled','reversed','chargeback','capturesuccess','authsuccessful','markedforreversal','capturestarted') and timestamp >=? and timestamp <=? ");
            // StringBuilder strQry = new StringBuilder("select count(*) as 'count',sum(amount) as 'amount' from " + tableName + " where fromtype=? and currency=? and totype=? and status in('settled','reversed','chargeback','capturesuccess','authsuccessful','markedforreversal','capturestarted') and dtstamp >=? and dtstamp <=? ");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setString(1, gatewayType.getGateway());
            preparedStatement.setString(2, gatewayType.getCurrency());
            preparedStatement.setString(3, gatewayType.getPartnerId());
            preparedStatement.setString(4, startDate);
            preparedStatement.setString(5, endDate);
            rsPayReport = preparedStatement.executeQuery();
            if (rsPayReport.next())
            {
                transactionSummaryVO.setTotalProcessingAmount(rsPayReport.getDouble("amount"));
                transactionSummaryVO.setTotalProcessingCount(rsPayReport.getLong("count"));
            }

            logger.error("Query-->"+strQry);
            log.error("Query-->"+preparedStatement);
            log.error("startDate ->"+startDate);
            log.error("endDate -> "+endDate);


        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(PayoutDAO.class.getName(), "getGatewayTypeProcessingAmount", null, "Common", "SystemError While Getting TransactionSummary", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid", e);
            PZExceptionHandler.raiseDBViolationException(PayoutDAO.class.getName(), "getGatewayTypeProcessingAmount", null, "Common", "Sql Exception due to incorrect query", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionSummaryVO;
    }
    //New Method Added During Weekly Payout Cron Final Implementation


    public TransactionSummaryVO getAuthFailCountAmountByDtstamp(GatewayAccount gatewayAccount,String authfailedTransStartDate, String authfailedTransEndDate,String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getConnection();
            StringBuilder strQry = new StringBuilder("select status,count(*) as 'count',sum(amount) as 'amount' from " + sTableName + " where accountid=? and status=? and FROM_UNIXTIME(dtstamp) >=? and FROM_UNIXTIME(dtstamp) <=? ");
            strQry.append(" group by status");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, gatewayAccount.getAccountId());
            preparedStatement.setString(2,"authfailed");
            preparedStatement.setString(3,authfailedTransStartDate);
            preparedStatement.setString(4,authfailedTransEndDate);
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
            Database.closeConnection(con);
        }
        return transactionSummaryVO;

    }

    public TransactionSummaryVO getAuthFailCountAmountByDtstamp(GatewayType gatewayType, String authfailedTransStartDate, String authfailedTransEndDate, String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getConnection();
            StringBuilder strQry = new StringBuilder("select status,count(*) as 'count',sum(amount) as 'amount' from " + sTableName + " where fromtype=? and status=? and FROM_UNIXTIME(dtstamp) >=? and FROM_UNIXTIME(dtstamp) <=? and currency=? and totype=? ");
            strQry.append(" group by status");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setString(1, gatewayType.getGateway());
            preparedStatement.setString(2, "authfailed");
            preparedStatement.setString(3, authfailedTransStartDate);
            preparedStatement.setString(4, authfailedTransEndDate);
            preparedStatement.setString(5, gatewayType.getCurrency());
            preparedStatement.setString(6, gatewayType.getPartnerId());
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
            Database.closeConnection(con);
        }
        return transactionSummaryVO;

    }

    public TransactionSummaryVO getAuthStartedCountAmountByDtstamp(GatewayType gatewayType, String startDate, String endDate, String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getConnection();
            StringBuilder strQry = new StringBuilder("select status,count(*) as 'count',sum(amount) as 'amount' from " + sTableName + " where fromtype=? and status=? and FROM_UNIXTIME(dtstamp) >=? and FROM_UNIXTIME(dtstamp) <=? and currency=? and totype=? ");
            strQry.append(" group by status");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setString(1, gatewayType.getGateway());
            preparedStatement.setString(2, "authstarted");
            preparedStatement.setString(3, startDate);
            preparedStatement.setString(4, endDate);
            preparedStatement.setString(5, gatewayType.getCurrency());
            preparedStatement.setString(6, gatewayType.getPartnerId());
            rsPayReport = preparedStatement.executeQuery();
            if (rsPayReport.next())
            {
                transactionSummaryVO.setAuthstartedAmount(rsPayReport.getDouble("amount"));
                transactionSummaryVO.setAuthstartedCount(rsPayReport.getLong("count"));
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
            Database.closeConnection(con);
        }
        return transactionSummaryVO;
    }



    public TransactionSummaryVO getChargebackCountAmountByTimestamp(GatewayAccount gatewayAccount,String chargebackTransStartDate, String chargebackTransEndDate,String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getConnection();
            StringBuilder strQry = new StringBuilder("select status,count(*) as 'count',sum(chargebackamount) as 'chargebackamount' from " + sTableName + " where accountid=? and status=? and TIMESTAMP >=? and TIMESTAMP <=? ");
            strQry.append(" group by status");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, gatewayAccount.getAccountId());
            preparedStatement.setString(2,"chargeback");
            preparedStatement.setString(3,chargebackTransStartDate);
            preparedStatement.setString(4,chargebackTransEndDate);
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
            Database.closeConnection(con);
        }
        return transactionSummaryVO;

    }

    public TransactionSummaryVO getChargebackCountAmountByTimestamp(GatewayType gatewayType, String chargebackTransStartDate, String chargebackTransEndDate, String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getConnection();
            StringBuilder strQry = new StringBuilder("select status,count(*) as 'count',sum(chargebackamount) as 'chargebackamount' from " + sTableName + " where fromtype=? and status=? and TIMESTAMP >=? and TIMESTAMP <=? and currency=? and totype=? ");
            strQry.append(" group by status");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setString(1, gatewayType.getGateway());
            preparedStatement.setString(2, "chargeback");
            preparedStatement.setString(3, chargebackTransStartDate);
            preparedStatement.setString(4, chargebackTransEndDate);
            preparedStatement.setString(5, gatewayType.getCurrency());
            preparedStatement.setString(6, gatewayType.getPartnerId());
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
            Database.closeConnection(con);
        }
        return transactionSummaryVO;

    }
    public TransactionSummaryVO getReversalCountAmountByTimestamp(GatewayAccount gatewayAccount,String reversedTransStartDate, String reversedTransEndDate,String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getConnection();
            StringBuilder strQry = new StringBuilder("select status,count(*) as 'count',sum(refundamount) as 'refundamount' from " + sTableName + " where accountid=? and status=? and TIMESTAMP >=? and TIMESTAMP <=?  ");
            strQry.append(" group by status");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setInt(1, gatewayAccount.getAccountId());
            preparedStatement.setString(2, "reversed");
            preparedStatement.setString(3, reversedTransStartDate);
            preparedStatement.setString(4, reversedTransEndDate);
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
            Database.closeConnection(con);
        }
        return transactionSummaryVO;

    }

    public TransactionSummaryVO getReversalCountAmountByTimestamp(GatewayType gatewayType, String reversedTransStartDate, String reversedTransEndDate, String sTableName)
    {
        TransactionSummaryVO transactionSummaryVO = new TransactionSummaryVO();
        PreparedStatement preparedStatement = null;
        Connection con = null;
        ResultSet rsPayReport = null;
        try
        {
            con = Database.getConnection();
            StringBuilder strQry = new StringBuilder("select status,count(*) as 'count',sum(refundamount) as 'refundamount' from " + sTableName + " where fromtype=? and status=? and TIMESTAMP >=? and TIMESTAMP <=? and currency=? and totype=? ");
            strQry.append(" group by status");
            preparedStatement = con.prepareStatement(strQry.toString());
            preparedStatement.setString(1, gatewayType.getGateway());
            preparedStatement.setString(2, "reversed");
            preparedStatement.setString(3, reversedTransStartDate);
            preparedStatement.setString(4, reversedTransEndDate);
            preparedStatement.setString(5, gatewayType.getCurrency());
            preparedStatement.setString(6, gatewayType.getPartnerId());
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
            Database.closeConnection(con);
        }
        return transactionSummaryVO;

    }
    public long getRetrivalRequestOnGatewayAccount(GatewayAccount gatewayAccount, String tableName,DateVO dateVO)
    {
        long retrivalRequestCount = 0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getConnection();
            String Query = "SELECT COUNT(*) AS 'RetrivalRequestCount' FROM " + tableName + " AS T JOIN bin_details as b ON T.trackingid = b.icicitransid  WHERE T.accountid=? AND b.isRetrivalRequest=? and FROM_UNIXTIME(T.dtstamp) >=? and FROM_UNIXTIME(T.dtstamp) <=?";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setInt(1, gatewayAccount.getAccountId());
            preparedStatement.setString(2,"Y");
            preparedStatement.setString(3,dateVO.getStartDate());
            preparedStatement.setString(4,dateVO.getEndDate());
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
            Database.closeConnection(con);
        }
        return retrivalRequestCount;

    }
    public String getMemberFirstTransactionDateOnGatewayAccount(GatewayAccount gatewayAccount)throws PZDBViolationException
    {
        AccountUtil util = new AccountUtil();
        String tableName = util.getTableNameFromAccountId(String.valueOf(gatewayAccount.getAccountId()));
        String strQuery = null;
        String memberFirstTransactionDate = null;
        ResultSet rsPayout = null;
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            strQuery = "SELECT MIN(trackingid),FROM_UNIXTIME(dtstamp) AS 'FirstTransDate' FROM " + tableName + " WHERE accountid=?";
            PreparedStatement preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setInt(1, gatewayAccount.getAccountId());
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                memberFirstTransactionDate = rsPayout.getString("FirstTransDate");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(PayoutDAO.class.getName(), "getMemberFirstTransactionDateOnGatewayAccount", null, "Common", "SystemError while getting first transaction date on main transaction tables", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect gateway account first transaction date", e);
            PZExceptionHandler.raiseDBViolationException(PayoutDAO.class.getName(), "getMemberFirstTransactionDateOnGatewayAccount", null, "Common", "SqlException due to incorrect query on main transaction tables", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return memberFirstTransactionDate;
    }

    public String getPartnersFirstTransactionOnGatewayType(GatewayType gatewayType, String partnerName) throws PZDBViolationException
    {
        String tableName = Database.getTableName(gatewayType.getGateway());
        String strQuery = null;
        String firstTransactionDate = null;
        ResultSet rsPayout = null;
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            strQuery = "SELECT MIN(trackingid),FROM_UNIXTIME(dtstamp) AS 'firsttransdate' FROM " + tableName + " WHERE fromtype=? and totype=?";
            PreparedStatement preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, gatewayType.getGateway());
            preparedStatement.setString(2, partnerName);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                firstTransactionDate = rsPayout.getString("firsttransdate");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while collecting gateway partner first transaction date on gateway type ", systemError);
            PZExceptionHandler.raiseDBViolationException(PayoutDAO.class.getName(), "getPartnersFirstTransactionOnGatewayType()", null, "Common", "SystemError while getting first transaction date on main transaction tables", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException while collecting gateway partner first transaction date on gateway type", e);
            PZExceptionHandler.raiseDBViolationException(PayoutDAO.class.getName(), "getPartnersFirstTransactionOnGatewayType()", null, "Common", "SqlException due to incorrect query on main transaction tables", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return firstTransactionDate;
    }

    public String getPartnersFirstTransactionDate(String partnerName) throws PZDBViolationException
    {
        //String tableName = Database.getTableName();
        String strQuery = null;
        String firstTransactionDate = null;
        ResultSet rsPayout = null;
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            strQuery = "SELECT MIN(trackingid),FROM_UNIXTIME(dtstamp) AS 'firsttransdate' FROM transaction_common WHERE totype=?";
            PreparedStatement preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, partnerName);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                firstTransactionDate = rsPayout.getString("firsttransdate");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while collecting gateway partner first transaction date on gateway type ", systemError);
            PZExceptionHandler.raiseDBViolationException(PayoutDAO.class.getName(), "getPartnersFirstTransactionDate()", null, "Common", "SystemError while getting first transaction date on main transaction tables", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException while collecting gateway partner first transaction date on gateway type", e);
            PZExceptionHandler.raiseDBViolationException(PayoutDAO.class.getName(), "getPartnersFirstTransactionDate()", null, "Common", "SqlException due to incorrect query on main transaction tables", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return firstTransactionDate;
    }

    public TransactionDetailsVO getTransDetailsFromCommon(String orderId, String memberId)
    {
        Connection con = null;
        TransactionDetailsVO transactionDetailsVO = null;
        try
        {
            con=Database.getConnection();
            StringBuffer qry = new StringBuffer("SELECT * FROM transaction_common WHERE description=? AND toid=?");

            PreparedStatement psUpdateTransaction =con.prepareStatement(qry.toString());
            psUpdateTransaction.setString(1,orderId);
            psUpdateTransaction.setString(2,memberId);
            ResultSet resultSet=psUpdateTransaction.executeQuery();
            if(resultSet.next())
            {
                transactionDetailsVO = new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(resultSet.getString("trackingid"));
                transactionDetailsVO.setAccountId(resultSet.getString("accountid"));
                transactionDetailsVO.setPaymodeId(resultSet.getString("paymodeid"));
                transactionDetailsVO.setCardTypeId(resultSet.getString("cardtypeid"));
                transactionDetailsVO.setToid(resultSet.getString("toid"));
                transactionDetailsVO.setTotype(resultSet.getString("totype"));//totype
                transactionDetailsVO.setFromid(resultSet.getString("fromid"));//fromid
                transactionDetailsVO.setFromtype(resultSet.getString("fromtype"));//fromtype
                transactionDetailsVO.setDescription(resultSet.getString("description"));
                transactionDetailsVO.setOrderDescription(resultSet.getString("orderdescription"));
                transactionDetailsVO.setAmount(resultSet.getString("amount"));
                transactionDetailsVO.setRefundAmount(resultSet.getString("refundamount"));
                transactionDetailsVO.setCurrency(resultSet.getString("currency"));
                transactionDetailsVO.setRedirectURL(resultSet.getString("redirecturl"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setFirstName(resultSet.getString("firstname"));//fn
                transactionDetailsVO.setLastName(resultSet.getString("lastname"));//ln
                transactionDetailsVO.setName(resultSet.getString("name"));//name
                transactionDetailsVO.setCcnum(resultSet.getString("ccnum"));//ccnum
                transactionDetailsVO.setExpdate(resultSet.getString("expdate"));//expdt
                transactionDetailsVO.setCardtype(resultSet.getString("cardtype"));//cardtype
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setCountry(resultSet.getString("country"));//country
                transactionDetailsVO.setState(resultSet.getString("state"));//state
                transactionDetailsVO.setCity(resultSet.getString("city"));//city
                transactionDetailsVO.setStreet(resultSet.getString("street"));//street
                transactionDetailsVO.setZip(resultSet.getString("zip"));//zip
                transactionDetailsVO.setTelcc(resultSet.getString("telnocc"));//telcc
                transactionDetailsVO.setTelno(resultSet.getString("telno"));//telno
                transactionDetailsVO.setHttpHeader(resultSet.getString("httpheader"));//httpheadet
            }
            logger.debug("transaction common query----"+psUpdateTransaction);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionDetailsVO;
    }

    public Hashtable getTransactionListForCommonInquiry(TransactionVO transactionVO, String fdtstamp, String tdtstamp, String start, String end)
    {
        Hashtable hash      = null;
        Codec me            = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        //Iterator i = gatewayTypeSet.iterator();
        StringBuffer sb     = null;
        StringBuffer count  = null;
        Connection conn     = null;
        try
        {
            conn = Database.getConnection();
            /*while(i.hasNext())
            {*/
            sb      = new StringBuffer("select trackingid,accountid,toid,description,amount,status,paymentid,remark,timestamp from transaction_common");
            count   = new StringBuffer("select count(*) from transaction_common");
            if (functions.isValueNull(fdtstamp))
            {
                sb.append(" where dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
                count.append(" where dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
            }

            if (functions.isValueNull(tdtstamp))
            {
                sb.append(" and dtstamp <= " +ESAPI.encoder().encodeForSQL(me, tdtstamp));
                count.append(" and dtstamp <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
            }
            if (functions.isValueNull(transactionVO.getStatus()))
            {
                sb.append(" and status='" + ESAPI.encoder().encodeForSQL(me,transactionVO.getStatus()) + "'");
                count.append(" and status='" + ESAPI.encoder().encodeForSQL(me,transactionVO.getStatus()) + "'");
            }
            if (functions.isValueNull(transactionVO.getToid()))
            {
                sb.append(" and toid=" + ESAPI.encoder().encodeForSQL(me,transactionVO.getToid()));
                count.append(" and toid=" + ESAPI.encoder().encodeForSQL(me,transactionVO.getToid()));
            }
            if (functions.isValueNull(transactionVO.getPgtypeid()))
            {
                sb.append(" and fromtype= '" + ESAPI.encoder().encodeForSQL(me,transactionVO.getPgtypeid())+"'");
                count.append(" and fromtype= '" + ESAPI.encoder().encodeForSQL(me,transactionVO.getPgtypeid())+"'");
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                sb.append(" and currency= '" + ESAPI.encoder().encodeForSQL(me,transactionVO.getCurrency())+"'");
                count.append(" and currency= '" + ESAPI.encoder().encodeForSQL(me,transactionVO.getCurrency())+"'");
            }
            if (functions.isValueNull(transactionVO.getTrackingId()))
            {
                sb.append(" and trackingid=" + ESAPI.encoder().encodeForSQL(me,transactionVO.getTrackingId()));
                count.append(" and trackingid=" + ESAPI.encoder().encodeForSQL(me,transactionVO.getTrackingId()));
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                sb.append(" and paymentid= '" + ESAPI.encoder().encodeForSQL(me,transactionVO.getPaymentId())+"'");
                count.append(" and paymentid= '" + ESAPI.encoder().encodeForSQL(me,transactionVO.getPaymentId())+"'");
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                sb.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me,transactionVO.getAccountId()));
                count.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me,transactionVO.getAccountId()));
            }
            if (functions.isValueNull(transactionVO.getOrderDesc()))
            {
                sb.append(" and description like '%" +ESAPI.encoder().encodeForSQL(me, transactionVO.getOrderDesc()) + "%'");
                count.append(" and description like '%" +ESAPI.encoder().encodeForSQL(me, transactionVO.getOrderDesc()) + "%'");
            }
            System.out.println("queryorder"+transactionVO.getOrderDesc());
            sb.append(" order by trackingid desc LIMIT " + start + "," + end);

            logger.debug("query for common inquiry----"+sb);
            logger.debug("count query for common inquiry----"+count);
            //}

            hash            = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(sb.toString(), conn));
            ResultSet rs    = Database.executeQuery(count.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            logger.debug("query to get inquiry transaction details----"+sb);
        }
        catch(SystemError s)
        {
            logger.error("SystemError", s);
            //error= error+"System Error while listing records.<br>";
        }
        catch (SQLException e)
        {
            logger.error("SQLError",e);

        }
        finally
        {
            Database.closeConnection(conn);
        }
        return  hash;
    }

    public Hashtable getTransactionListForCommonActionHistory(TransactionVO transactionVO,String start, String end)
    {
        Hashtable hash = null;
        Connection conn = null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer sb=null;
        StringBuffer count=null;
        try
        {
            conn = Database.getConnection();

            sb=new StringBuffer("select D.trackingid,D.action,D.status,D.timestamp,D.amount,D.remark,D.responsetransactionid,D.responsetransactionstatus,D.responsecode,D.responsedescription,D.actionexecutorid,D.actionexecutorname,D.ipaddress from transaction_common_details as D,transaction_common as T where T.trackingid=D.trackingid and detailid>0");
            count=new StringBuffer("select count(*) from transaction_common_details as D,transaction_common as T where T.trackingid=D.trackingid and detailid>0");

            if (functions.isValueNull(transactionVO.getStatus()))
            {
                sb.append(" and D.status='" + ESAPI.encoder().encodeForSQL(me,transactionVO.getStatus()) + "'");
                count.append(" and D.status='" + ESAPI.encoder().encodeForSQL(me,transactionVO.getStatus()) + "'");
            }

            if (functions.isValueNull(transactionVO.getTrackingId()))
            {
                sb.append(" and D.trackingid=" + ESAPI.encoder().encodeForSQL(me,transactionVO.getTrackingId()));
                count.append(" and D.trackingid=" + ESAPI.encoder().encodeForSQL(me,transactionVO.getTrackingId()));
            }

            if (functions.isValueNull(transactionVO.getToid()))
            {
                sb.append(" and T.toid=" + ESAPI.encoder().encodeForSQL(me,transactionVO.getToid()));
                count.append(" and T.toid=" + ESAPI.encoder().encodeForSQL(me,transactionVO.getToid()));
            }

            if (functions.isValueNull(transactionVO.getPgtypeid()))
            {
                sb.append(" and T.fromtype= '" + ESAPI.encoder().encodeForSQL(me,transactionVO.getPgtypeid())+"' and T.currency= '" + ESAPI.encoder().encodeForSQL(me,transactionVO.getCurrency())+"'");
                count.append(" and T.fromtype= '" + ESAPI.encoder().encodeForSQL(me,transactionVO.getPgtypeid())+"' and T.currency= '"+ ESAPI.encoder().encodeForSQL(me,transactionVO.getCurrency())+"'");
            }
            /*if (functions.isValueNull(transactionVO.getCurrency()))
            {
                sb.append(" and T.currency= '" + ESAPI.encoder().encodeForSQL(me,transactionVO.getCurrency())+"'");
                count.append(" and T.currency= '" + ESAPI.encoder().encodeForSQL(me,transactionVO.getCurrency())+"'");
            }*/
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                sb.append(" and T.accountid=" + ESAPI.encoder().encodeForSQL(me,transactionVO.getAccountId()));
                count.append(" and T.accountid=" + ESAPI.encoder().encodeForSQL(me,transactionVO.getAccountId()));
            }

            sb.append(" order by D.trackingid desc LIMIT " + start + "," + end);
            logger.debug("query---"+sb.toString());
            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(sb.toString(), conn));
            ResultSet rs = Database.executeQuery(count.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

        }
        catch(SystemError s)
        {
            logger.error("SystemError",s);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");

        }
        catch (SQLException e)
        {
            logger.error("SQLError",e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return  hash;
    }

    public Hashtable getTransactionList(String trackingid, String name, String desc, String orderdesc, String tdtstamp, String fdtstamp, String status, int records, int pageno, String perfectmatch,Set<String> gatewayTypeSet)
    {
        Functions functions = new Functions();
        Hashtable hash = null;
        String tablename = "";
        String fields = "";
        StringBuffer query = new StringBuffer();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;

        Connection conn = null;

        try
        {
            Set<String> tables = Database.getTableSet(gatewayTypeSet);
            Iterator i = tables.iterator();
            while (i.hasNext())
            {
                tablename = (String) i.next();
                if (tablename.equals("transaction_icicicredit"))
                {
                    fields = "T.icicitransid as transid,toid,fromid,description,amount,redirecturl,status,dtstamp,name,T.emailaddr,httpheader,ipaddress,country,city,state,zip,orderdescription,telno,telnocc,captureamount,refundamount,hrcode,cardtype,templateamount,templatecurrency,paymodeid,cardtypeid,T.accountid,from_unixtime(dtstamp) as \"Transaction Date\",concat(B.first_six,'******',B.last_four)as \"card number\"" + " from " + tablename + " as T left join bin_details as B on T.icicitransid = B.icicitransid ";
                }
                else if (tablename.equals("transaction_ecore"))
                {
                    fields = "trackingid as transid,toid,fromid,description,amount,redirecturl,status,dtstamp,name,T.emailaddr,httpheader,ipaddress,country,city,state,zip,orderdescription,telno,telnocc,captureamount,refundamount,hrcode,cardtype,templateamount,templatecurrency,paymodeid,cardtypeid,T.accountid,from_unixtime(dtstamp) as \"Transaction Date\",concat(B.first_six,'******',B.last_four)as \"card number\", bin_brand,bin_card_type,bin_card_category,bin_usage_type,bin_trans_type " + " from " + tablename + " as T left join bin_details as B on T.trackingid = B.icicitransid ";
                }
                else if (tablename.equals("transaction_qwipi"))
                {
                    fields = "trackingid as transid,toid,fromid,description,amount,redirecturl,status,dtstamp,name,T.emailaddr,httpheader,ipaddress,country,city,state,zip,orderdescription,telno,telnocc,captureamount,refundamount,hrcode,cardtype,templateamount,templatecurrency,paymodeid,cardtypeid,T.accountid,from_unixtime(dtstamp) as \"Transaction Date\",concat(B.first_six,'******',B.last_four)as \"card number\", bin_brand,bin_card_type,bin_card_category,bin_usage_type,bin_trans_type " + " from " + tablename + " as T left join bin_details as B on T.trackingid = B.icicitransid ";
                }
                else if (tablename.equals("transaction_common"))
                {
                    fields = "trackingid as transid,toid,fromid,description,amount,redirecturl,status,dtstamp,name,T.emailaddr,httpheader,ipaddress,country,city,state,zip,orderdescription,telno,telnocc,captureamount,refundamount,hrcode,cardtype,templateamount,templatecurrency,paymodeid,cardtypeid,T.accountid,from_unixtime(dtstamp) as \"Transaction Date\",concat(B.first_six,'******',B.last_four)as \"card number\", bin_brand,bin_card_type,bin_card_category,bin_usage_type,bin_trans_type " + " from " + tablename + " as T left join bin_details as B on T.trackingid = B.icicitransid ";
                }

                query.append("select " + fields + "where 1=1");


                if (functions.isValueNull(status))
                {
                    query.append(" and status='" +status+ "'");

                }

                if (functions.isValueNull(name))
                {
                    if (perfectmatch == null)
                    {
                        query.append(" and name like '%" + ESAPI.encoder().encodeForSQL(me, name) + "%'");
                    }
                    else
                    {
                        query.append(" and name='" + ESAPI.encoder().encodeForSQL(me, name) + "'");
                    }
                }

                if (functions.isValueNull(fdtstamp))
                {
                    query.append(" and dtstamp >= " + ESAPI.encoder().encodeForSQL(me, fdtstamp));
                }

                if (functions.isValueNull(tdtstamp))
                {
                    query.append(" and dtstamp <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
                }

                if (functions.isValueNull(desc))
                {
                    if (perfectmatch == null)
                    {
                        query.append(" and description like '%" + ESAPI.encoder().encodeForSQL(me, desc) + "%'");
                    }
                    else
                    {
                        query.append(" and description='" + ESAPI.encoder().encodeForSQL(me, desc) + "'");
                    }
                }

                if (functions.isValueNull(orderdesc))
                {
                    if (perfectmatch == null)
                    {
                        query.append(" and orderdescription like '%" + ESAPI.encoder().encodeForSQL(me, orderdesc) + "%'");
                    }
                    else
                    {
                        query.append(" and orderdescription='" + ESAPI.encoder().encodeForSQL(me, orderdesc) + "'");
                    }
                }

                if (functions.isValueNull(trackingid))
                {
                    if (tablename.equals("transaction_icicicredit"))
                    {
                        query.append(" and T.icicitransid='" + ESAPI.encoder().encodeForSQL(me, trackingid) + "'");
                    }
                    else
                    {
                        query.append(" and trackingid=" + ESAPI.encoder().encodeForSQL(me, trackingid));
                    }
                }

                if (i.hasNext())
                    query.append(" UNION ");

            }

            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");

            query.append(" order by dtstamp DESC,transid ");

            query.append("  limit " + start + "," + end);

            logger.debug("trans query-----"+query);

            conn = Database.getRDBConnection();
            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));

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
            logger.error("SQL Exception:::::", se);
        }
        catch (SystemError se)
        {
            logger.error("SQL Exception:::::", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        logger.debug("Leaving listTransactions");
        return hash;
    }

    public Hashtable listTransactions(String toid, String trackingid, String name, String desc, String orderdesc, String amount, String refundamount, String firstfourofccnum, String lastfourofccnum, String emailaddr, String tdtstamp, String fdtstamp, String status, int records, int pageno, String perfectmatch, boolean archive, Set<String> gatewayTypeSet, String remark, String cardtype, String issuing_bank, String statusFlag, String gateway_name, String currency, String accountid,String paymentid, String dateType ,String arn,String rrn,String authcode,String customerId,String transactionMode, String telno,String telnocc,String totype,String bankaccount)
    {
        Codec me            = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        String tablename    = "";
        String fields       = "";
        String pRefund      = "false";
        StringBuffer query  = new StringBuffer();
        Hashtable hash      = null;
        int start           = 0; // start index
        int end             = 0; // end index
        start               = (pageno - 1) * records;
        end                 = records;
        Connection conn     = null;
        Functions functions = new Functions();
        try
        {
            conn = Database.getRDBConnection();
            //  Set<String> tables = Database.getTableSet(gatewayTypeSet);
            /*Iterator i = tables.iterator();
            while(i.hasNext())
            {*/
            tablename = "transaction_common";
            if (archive)
            {
                //ToDO will be used once all the archive tables will be avialbale.
                //tablename = "transaction_icicicredit_archive";
                tablename   = "transaction_common_archive";
            }

               /* if(tablename.equals("transaction_icicicredit")||tablename.equals("transaction_icicicredit_archive"))
                {
                    fields = tablename+".icicitransid as trackingid,status,name,amount,refundamount,description,orderdescription,from_unixtime("+tablename + ".dtstamp, '%Y-%m-%d %T') as dtstamp,hrcode," + tablename + ".accountid,"+tablename+".status as remark";
                }
                else
                {*/
            //fields = "trackingid as trackingid,status,firstname,amount,refundamount,description,orderdescription,from_unixtime("+tablename + ".dtstamp, '%Y-%m-%d %T') as dtstamp,hrcode," + tablename + ".accountid,"+tablename+".remark,currency";
            fields  = tablename+".trackingid,"+tablename+".status,name,"+tablename+".amount,refundamount,description,orderdescription, totype,from_unixtime("+tablename + ".dtstamp, '%Y-%m-%d %T') as dtstamp,hrcode," + tablename + ".accountid,"+tablename+".remark,currency,cardtype,toid,fromtype,customerId,telnocc,telno,transaction_mode,machineid";
            if (functions.isValueNull(bankaccount))
                fields = fields + ",ts.bankaccount";
            if(!"begun".equalsIgnoreCase(status))
                fields  = fields+",bin_brand";


                /*if(tablename.equals("transaction_icicicredit")||tablename.equals("transaction_icicicredit_archive"))
                {
                    query.append("select " + fields + " from " + tablename + " JOIN bin_details on "+tablename+".icicitransid = bin_details.icicitransid where trackingid>0");
                }
                else
                {*/
            query.append("select " + fields + " from " + tablename );
            if(!"begun".equalsIgnoreCase(status))
                query.append(" Left JOIN bin_details on "+tablename+".trackingid = bin_details.icicitransid");
            if (functions.isValueNull(bankaccount))
                query.append(" left join transaction_safexpay_details as ts on "+tablename+".trackingid = ts.trackingid ");
            query.append(" where ");
            // query.append(" where "+tablename+".trackingid > 0 ");
            // }

            //dateconversion
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" " + tablename + ".timestamp >= '" + startDate + "'");
            }
            else if(functions.isValueNull(fdtstamp))
            {
                query.append(" " + tablename+ ".dtstamp >= " + fdtstamp);
            }else if(!functions.isValueNull(fdtstamp) && functions.isValueNull(trackingid)){
                //on Inquiry
                query.append(" " + tablename+ ".trackingid = " + trackingid);
            }

            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {

                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and " + tablename + ".timestamp <= '" + endDate + "'");
            }
            else if (functions.isValueNull(tdtstamp))
            {
                query.append(" and " + tablename+ ".dtstamp <= " + tdtstamp);
            }

            if (functions.isValueNull(toid))
            {
                query.append(" and toid='" +ESAPI.encoder().encodeForSQL(me, toid) + "'");
            }
            if (functions.isValueNull(totype))
            {
                query.append(" and totype='" +ESAPI.encoder().encodeForSQL(me, totype) + "'");
            }
            if (functions.isValueNull(status))
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status  = "reversed";
                    pRefund = "true";
                }
                query.append(" and "+tablename+".status='" + status + "'");

            }
            if (!"all".equalsIgnoreCase(statusFlag) && !"begun".equalsIgnoreCase(status))
            {
                query.append(" and bin_details."+statusFlag+"='Y'");
            }
            if (functions.isValueNull(amount))
            {
                //query.append(" and amount='" +ESAPI.encoder().encodeForSQL(me, amount) + "'");
                query.append(" and "+tablename +".amount='" +ESAPI.encoder().encodeForSQL(me, amount) + "'");
            }
            if (functions.isValueNull(gateway_name))
            {
                query.append(" and fromtype='" +ESAPI.encoder().encodeForSQL(me, gateway_name) + "'");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and currency='" +ESAPI.encoder().encodeForSQL(me, currency) + "'");
            }
            if (functions.isValueNull(accountid))
            {
                query.append(" and "+tablename+".accountid='" +ESAPI.encoder().encodeForSQL(me, accountid) + "'");
            }
            if (functions.isValueNull(refundamount))
            {
                query.append(" and refundamount='" +ESAPI.encoder().encodeForSQL(me, refundamount) + "'");
            }
            if (functions.isValueNull(emailaddr))
            {
                query.append(" and "+tablename+".emailaddr='"+ emailaddr + "'");
            }
            if (functions.isValueNull(telnocc))
            {
                query.append(" and "+tablename+".telnocc='"+ telnocc + "'");
            }
            if (functions.isValueNull(telno))
            {
                query.append(" and "+tablename+".telno='"+  telno + "'");
            }
            if(functions.isValueNull(firstfourofccnum) && !"begun".equalsIgnoreCase(status))
            {
                query.append(" and bin_details.first_six ='" +ESAPI.encoder().encodeForSQL(me, firstfourofccnum) + "'");
            }
            if (functions.isValueNull(lastfourofccnum) && !"begun".equalsIgnoreCase(status))
            {
                query.append(" and bin_details.last_four ='" +ESAPI.encoder().encodeForSQL(me, lastfourofccnum )+ "'");
            }
           /* if (functions.isValueNull(cardtype) && !"begun".equalsIgnoreCase(status))
            {
                query.append(" and bin_brand ='" +ESAPI.encoder().encodeForSQL(me, cardtype )+ "'");
            }*/
            if (functions.isValueNull(cardtype) && !"begun".equalsIgnoreCase(status))
            {
                logger.error("===inside if ===" + cardtype);
                query.append(" and (bin_brand ='" +ESAPI.encoder().encodeForSQL(me, cardtype )+ "' OR   cardtype='"+ESAPI.encoder().encodeForSQL(me, cardtype )+ "')" );
            }
            if (functions.isValueNull(issuing_bank) && !"begun".equalsIgnoreCase(status))
            {
                query.append(" and issuing_bank ='" +ESAPI.encoder().encodeForSQL(me, issuing_bank )+ "'");
            }
            if (functions.isValueNull(paymentid))
            {
                query.append(" and paymentid ='" +paymentid.trim()+ "'");
            }
            if (functions.isValueNull(arn))
            {
                query.append(" and arn ='" +arn+ "'");
            }
            if (functions.isValueNull(rrn))
            {
                query.append(" and rrn ='" +rrn+ "'");
            }
            if (functions.isValueNull(authcode))
            {
                query.append(" and authorization_code ='" +authcode+ "'");
            }
            if (functions.isValueNull(bankaccount))
                query.append(" and ts.bankaccount='" +bankaccount+ "' AND bankaccount IS NOT NULL");
            if (functions.isValueNull(name))
            {
                if (perfectmatch == null)
                {
                    query.append(" and name like '%" + ESAPI.encoder().encodeForSQL(me,name) + "%'");
                }
                else
                {
                    query.append(" and name='" +ESAPI.encoder().encodeForSQL(me, name) + "'");
                }
            }
            /*//dateconversion
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" and " + tablename + ".timestamp >= '" + startDate + "'");
            }

            else if(functions.isValueNull(fdtstamp))
            {
                query.append(" and " + tablename+ ".dtstamp >= " + fdtstamp);
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {

                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and " + tablename + ".timestamp <= '" + endDate + "'");
            }
            else if (functions.isValueNull(tdtstamp))
            {
                query.append(" and " + tablename+ ".dtstamp <= " + tdtstamp);
            }*/
            if (functions.isValueNull(desc))
            {
                if (perfectmatch == null)
                {
                    if(desc.contains(",")){
                        query.append(" and description IN (" + desc + ")");
                    }else{
                        query.append(" and description like '" + ESAPI.encoder().encodeForSQL(me,desc) + "%'");
                    }

                }
                else
                {
                    query.append(" and description='" + ESAPI.encoder().encodeForSQL(me,desc) + "'");
                }
            }
            if (functions.isValueNull(orderdesc))
            {
                if (perfectmatch == null)
                {
                    query.append(" and orderdescription like '" +ESAPI.encoder().encodeForSQL(me, orderdesc) + "%'");
                }
                else
                {
                    query.append(" and orderdescription='" + ESAPI.encoder().encodeForSQL(me,orderdesc) + "'");
                }
            }
            if (functions.isValueNull(trackingid))
            {
                    /*if(tablename.equals("transaction_icicicredit")||tablename.equals("transaction_icicicredit_archive"))
                    {
                        query.append(" and " + tablename + ".icicitransid IN (" + trackingid + ")");
                    }
                    else
                    {*/
                query.append(" and " + tablename + ".trackingid IN (" + trackingid + ")");
                //}
            }
            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and captureamount > refundamount and STATUS='reversed'");
            }
            if (functions.isValueNull(remark))
            {
                    /*if(tablename.equals("transaction_icicicredit")||tablename.equals("transaction_icicicredit_archive"))
                    {

                    }
                    else
                    {*/
                if (perfectmatch == null)
                {
                    query.append(" and remark like '%" + ESAPI.encoder().encodeForSQL(me,remark) + "%'");
                }
                else
                {
                    query.append(" and remark='" +ESAPI.encoder().encodeForSQL(me, remark) + "'");
                }
                //}
            }
            if (functions.isValueNull(customerId))
            {
                query.append(" and customerId ='" +customerId+ "'");
            }
            if (functions.isValueNull(transactionMode))
            {
                query.append(" and transaction_mode ='" +transactionMode.trim()+ "'");
            }
                /*if(i.hasNext())
                    query.append(" UNION ");*/
            //}

            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");
            query.append(" order by  dtstamp DESC,trackingid ");
            query.append(" limit " + start + "," + end);

            logger.error("===count query===" + countquery);
            logger.error("===query===" + query);

            hash            = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            ResultSet rs    = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

        }
        catch (SQLException se)
        {
            logger.error("SQLException----",se);
        }
        catch (SystemError se)
        {
            logger.error("SystemError----",se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public Hashtable listTransactionsBasedOnDetailStatus(String toid, String trackingid, String name, String desc, String orderdesc, String amount, String refundamount, String firstfourofccnum, String lastfourofccnum, String emailaddr, String tdtstamp, String fdtstamp, String status, int records, int pageno, String perfectmatch, boolean archive, Set<String> gatewayTypeSet, String remark, String cardtype, String issuing_bank, String statusFlag, String gateway_name, String currency, String accountid,String paymentid, String dateType ,String arn,String rrn,String authcode,String detailStatus,String customerId,String transactionMode,String telno,String telnocc,String totype)
    {
        Codec me            = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        String tablename    = "";
        String fields       = "";
        String pRefund      = "false";
        StringBuffer query  = new StringBuffer();
        Hashtable hash      = null;
        int start           = 0; // start index
        int end             = 0; // end index
        start               = (pageno - 1) * records;
        end                 = records;
        Connection conn     = null;
        Functions functions = new Functions();
        try
        {
            conn        = Database.getRDBConnection();
            tablename   = "transaction_common";
            query       = new StringBuffer("SELECT from_unixtime(t.dtstamp, '%Y-%m-%d %T') as dtstamp,t.timestamp,tcd.trackingid,t.totype,t.description,t.orderdescription,t.name,t.cardtype,t.amount,t.refundamount,t.status,t.telnocc,t.telno,tcd.status AS detailStatus,tcd.amount AS detailAmount,tcd.`timestamp` AS detailTimestamp,tcd.remark,t.currency,t.accountid,t.rrn,t.arn,t.authorization_code,customerId,t.toid,t.fromtype,t.transaction_mode FROM transaction_common AS t JOIN transaction_common_details AS tcd");

            if(!"begun".equalsIgnoreCase(status))
                query.append(" LEFT JOIN bin_details as bd");
            query.append(" WHERE t.trackingid=tcd.trackingid");
            if(!"begun".equalsIgnoreCase(status))
                query.append(" AND tcd.trackingid=bd.icicitransid ");
            if (functions.isValueNull(toid))
            {
                query.append(" and t.toid='" +ESAPI.encoder().encodeForSQL(me, toid) + "'");
            }
            if (functions.isValueNull(totype))
            {
                query.append(" and t.totype='" +ESAPI.encoder().encodeForSQL(me, totype) + "'");
            }
            if (functions.isValueNull(status))
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status  = "reversed";
                    pRefund = "true";
                }
                query.append(" and tcd.status='" + status + "'");

            }

            if (functions.isValueNull(amount))
            {
                query.append(" and t.amount='" +ESAPI.encoder().encodeForSQL(me, amount) + "'");
            }
            if (functions.isValueNull(gateway_name))
            {
                query.append(" and t.fromtype='" +ESAPI.encoder().encodeForSQL(me, gateway_name) + "'");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and t.currency='" +ESAPI.encoder().encodeForSQL(me, currency) + "'");
            }
            if (functions.isValueNull(accountid))
            {
                query.append(" and t.accountid='" +ESAPI.encoder().encodeForSQL(me, accountid) + "'");
            }
            if (functions.isValueNull(refundamount))
            {
                query.append(" and t.refundamount='" +ESAPI.encoder().encodeForSQL(me, refundamount) + "'");
            }
            if (functions.isValueNull(emailaddr))
            {
                query.append(" and t.emailaddr='"+ emailaddr + "'");
            }
            if (functions.isValueNull(telnocc))
            {
                query.append(" and t.telnocc='" + telnocc + "'");
            }
            if (functions.isValueNull(telno))
            {
                query.append(" and t.telno='"+ telno + "'");
            }
            if(functions.isValueNull(firstfourofccnum) && !"begun".equalsIgnoreCase(status))
            {
                query.append(" and bd.first_six ='" +ESAPI.encoder().encodeForSQL(me, firstfourofccnum) + "'");
            }
            if (functions.isValueNull(lastfourofccnum) && !"begun".equalsIgnoreCase(status))
            {
                query.append(" and bd.last_four ='" +ESAPI.encoder().encodeForSQL(me, lastfourofccnum )+ "'");
            }
            if (functions.isValueNull(cardtype))
            {
                query.append(" and t.bin_brand ='" +ESAPI.encoder().encodeForSQL(me, cardtype )+ "'");
            }
            if (functions.isValueNull(issuing_bank))
            {
                query.append(" and t.issuing_bank ='" +ESAPI.encoder().encodeForSQL(me, issuing_bank )+ "'");
            }
            if (functions.isValueNull(paymentid))
            {
                query.append(" and t.paymentid ='" +paymentid.trim()+ "'");
            }
            if (functions.isValueNull(arn))
            {
                query.append(" and t.arn ='" +arn+ "'");
            }
            if (functions.isValueNull(rrn))
            {
                query.append(" and t.rrn ='" +rrn+ "'");
            }
            if (functions.isValueNull(authcode))
            {
                query.append(" and t.authorization_code ='" +authcode+ "'");
            }
            if (functions.isValueNull(name))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.name like '%" + ESAPI.encoder().encodeForSQL(me,name) + "%'");
                }
                else
                {
                    query.append(" and t.name='" +ESAPI.encoder().encodeForSQL(me, name) + "'");
                }
            }

            long milliSeconds = Long.parseLong(fdtstamp + "000");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
            DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startDate        = formatter1.format(calendar.getTime());

            query.append(" and tcd.timestamp >= '" + startDate + "'");

            long milliSeconds1 = Long.parseLong(tdtstamp + "000");
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(milliSeconds1);
            DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String endDate          = formatter2.format(calendar1.getTime());
            query.append(" and tcd.timestamp <= '" + endDate + "'");

            //dateconversion
           /* if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                query.append(" and tcd.timestamp >= '" + startDate + "'");
            }

            else if(functions.isValueNull(fdtstamp))
            {
                query.append(" and tcd.timestamp >= " + fdtstamp);
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {

                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter2.format(calendar.getTime());
                query.append(" and tcd.timestamp <= '" + endDate + "'");
            }
            else if (functions.isValueNull(tdtstamp))
            {
                query.append(" and tcd.timestamp <= " + tdtstamp);
            }*/
            if (functions.isValueNull(desc))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.description like '" + ESAPI.encoder().encodeForSQL(me,desc) + "%'");
                }
                else
                {
                    query.append(" and t.description='" + ESAPI.encoder().encodeForSQL(me,desc) + "'");
                }
            }
            if (functions.isValueNull(orderdesc))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.orderdescription like '" +ESAPI.encoder().encodeForSQL(me, orderdesc) + "%'");
                }
                else
                {
                    query.append(" and t.orderdescription='" + ESAPI.encoder().encodeForSQL(me,orderdesc) + "'");
                }
            }
            if (functions.isValueNull(trackingid))
            {
                query.append(" and t.trackingid IN (" + trackingid + ")");
            }
            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and t.captureamount > t.refundamount and t.STATUS='reversed'");
            }
            if (functions.isValueNull(remark))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.remark like '%" + ESAPI.encoder().encodeForSQL(me,remark) + "%'");
                }
                else
                {
                    query.append(" and t.remark='" +ESAPI.encoder().encodeForSQL(me, remark) + "'");
                }
            }
            if (functions.isValueNull(customerId))
            {
                query.append(" and customerId ='" +customerId+ "'");
            }
            if (functions.isValueNull(transactionMode))
            {
                query.append(" and transaction_mode ='" +transactionMode.trim()+ "'");
            }
            /*if (functions.isValueNull(detailStatus))
            {
                query.append(" and tcd.status='" +ESAPI.encoder().encodeForSQL(me, detailStatus) + "'");
            }*/
            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");
            query.append(" order by  t.dtstamp DESC,t.trackingid ");
            query.append(" limit " + start + "," + end);

            logger.error("===count query===" + countquery);
            logger.error("===query===" + query);

            hash            = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            ResultSet rs    = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

        }
        catch (SQLException se)
        {
            logger.error("SQLException----",se);
        }
        catch (SystemError se)
        {
            logger.error("SystemError----",se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public Hashtable fraudTransactionList(String toid, String mid,String trackingid,String terminalId,String transactionType,String amount,String refundamount, String firstfourofccnum, String lastfourofccnum, String tdtstamp, String fdtstamp, int records, int pageno,String currency, String accountid,String dataType,String paymentid,String isRefund,String rrn,String authCode)
    {
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer query = null;
        Hashtable hash = null;
        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;
        Connection conn = null;
        Functions functions = new Functions();
        try
        {
            conn = Database.getRDBConnection();
            query=new StringBuffer("SELECT id,trackingId,paymentid,accountId,terminalId,personalAccountNumber,amount,refund_amount,currency,transactionType,transactionDate,merchant_id,authorization_code,rrn,arn,isRecordFound,isRefunded,TIMESTAMP,dtstamp,toid,call_type,cardType,partnerid FROM fraud_defender_details WHERE 1>0 AND isRecordFound='Y'");
            if (functions.isValueNull(toid))
            {
                query.append(" and toid='" +ESAPI.encoder().encodeForSQL(me, toid) + "'");
            }
            if (functions.isValueNull(paymentid))
            {
                query.append(" and paymentid='" +ESAPI.encoder().encodeForSQL(me, paymentid) + "'");
            }
            if (functions.isValueNull(terminalId))
            {
                query.append(" and terminalId='" +ESAPI.encoder().encodeForSQL(me, terminalId) + "'");
            }
            if (functions.isValueNull(mid))
            {
                query.append(" and merchant_id='" +ESAPI.encoder().encodeForSQL(me, mid) + "'");
            }
            if (functions.isValueNull(rrn))
            {
                query.append(" and rrn='" +ESAPI.encoder().encodeForSQL(me, rrn) + "'");
            }
            if (functions.isValueNull(isRefund))
            {
                query.append(" and isRefunded='" +ESAPI.encoder().encodeForSQL(me, isRefund) + "'");
            }
            if (functions.isValueNull(amount))
            {
                query.append(" and amount='" +ESAPI.encoder().encodeForSQL(me, amount) + "'");
            }
            if (functions.isValueNull(refundamount))
            {
                query.append(" and refund_amount='" +ESAPI.encoder().encodeForSQL(me, refundamount) + "'");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and currency='" +ESAPI.encoder().encodeForSQL(me, currency) + "'");
            }
            if (functions.isValueNull(transactionType))
            {
                query.append(" and transactionType='" +ESAPI.encoder().encodeForSQL(me, transactionType) + "'");
            }
            if (functions.isValueNull(firstfourofccnum) && functions.isValueNull(lastfourofccnum))
            {
                query.append(" and personalAccountNumber='" +ESAPI.encoder().encodeForSQL(me, firstfourofccnum) +"******"+ESAPI.encoder().encodeForSQL(me, lastfourofccnum)+ "'");
            }
            if (functions.isValueNull(accountid))
            {
                query.append(" and accountId='" +ESAPI.encoder().encodeForSQL(me, accountid) + "'");
            }
            if (functions.isValueNull(terminalId))
            {
                query.append(" and terminalId='" +ESAPI.encoder().encodeForSQL(me, accountid) + "'");
            }
            if (functions.isValueNull(refundamount))
            {
                query.append(" and refund_amount='" +ESAPI.encoder().encodeForSQL(me, refundamount) + "'");
            }
            if (functions.isValueNull(paymentid))
            {
                query.append(" and paymentid ='" +paymentid.trim()+ "'");
            }
            if ("TIMESTAMP".equals(dataType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                query.append(" and timestamp >= '" + startDate + "'");
            }
            else
            {
                query.append(" and dtstamp >= " + fdtstamp);
            }
            if ("TIMESTAMP".equals(dataType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter1.format(calendar.getTime());
                query.append(" and timestamp <= '" + endDate + "'");
            }
            else
            {
                query.append(" and dtstamp <= " + tdtstamp);
            }

            if (functions.isValueNull(trackingid))
            {
                query.append(" and trackingId IN (" + trackingid + ")");
            }

            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");
            query.append(" order by id DESC");
            query.append(" limit " + start + "," + end);

            logger.debug("===count query===" + countquery);
            logger.debug("===query===" + query);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            ResultSet rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

        }
        catch (SQLException se)
        {
            logger.error("SQLException----",se);
        }
        catch (SystemError se)
        {
            logger.error("SystemError----",se);
        }
        catch (Exception se)
        {
            logger.error("Exception----",se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public Hashtable listTransactionsFraudAlertExportInExcel(String toid, String mid,String trackingid,String terminalId,String transactionType, String amount, String refundamount, String firstfourofccnum, String lastfourofccnum, String tdtstamp, String fdtstamp,String currency, String accountid,String paymentid, String dateType,String isRefund,String rrn,String authCode)
    {
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer query = null;
        Hashtable hash = null;
        Connection conn = null;
        Functions functions = new Functions();
        try
        {
            conn = Database.getRDBConnection();
            query=new StringBuffer("SELECT id,trackingId,paymentid,accountId,terminalId,personalAccountNumber,amount,refund_amount,currency,transactionType,transactionDate,merchant_id,authorization_code,rrn,arn,isRecordFound,isRefunded,TIMESTAMP,dtstamp,toid,call_type,cardType,partnerid  FROM fraud_defender_details WHERE 1>0 AND isRecordFound='Y'");
            if (functions.isValueNull(toid))
            {
                query.append(" and toid='" +ESAPI.encoder().encodeForSQL(me, toid) + "'");
            }
            if (functions.isValueNull(paymentid))
            {
                query.append(" and paymentid='" +ESAPI.encoder().encodeForSQL(me, paymentid) + "'");
            }
            if (functions.isValueNull(terminalId))
            {
                query.append(" and terminalId='" +ESAPI.encoder().encodeForSQL(me, terminalId) + "'");
            }
            if (functions.isValueNull(mid))
            {
                query.append(" and merchant_id='" +ESAPI.encoder().encodeForSQL(me, mid) + "'");
            }
            if (functions.isValueNull(rrn))
            {
                query.append(" and rrn='" +ESAPI.encoder().encodeForSQL(me, rrn) + "'");
            }
            if (functions.isValueNull(isRefund))
            {
                query.append(" and isRefunded='" +ESAPI.encoder().encodeForSQL(me, isRefund) + "'");
            }
            if (functions.isValueNull(amount))
            {
                query.append(" and amount='" +ESAPI.encoder().encodeForSQL(me, amount) + "'");
            }
            if (functions.isValueNull(refundamount))
            {
                query.append(" and refund_amount='" +ESAPI.encoder().encodeForSQL(me, refundamount) + "'");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and currency='" +ESAPI.encoder().encodeForSQL(me, currency) + "'");
            }
            if (functions.isValueNull(transactionType))
            {
                query.append(" and transactionType='" +ESAPI.encoder().encodeForSQL(me, transactionType) + "'");
            }
            if (functions.isValueNull(firstfourofccnum) && functions.isValueNull(lastfourofccnum))
            {
                query.append(" and personalAccountNumber='" +ESAPI.encoder().encodeForSQL(me, firstfourofccnum) +"******"+ESAPI.encoder().encodeForSQL(me, lastfourofccnum)+ "'");
            }
            if (functions.isValueNull(accountid))
            {
                query.append(" and accountId='" +ESAPI.encoder().encodeForSQL(me, accountid) + "'");
            }
            if (functions.isValueNull(terminalId))
            {
                query.append(" and terminalId='" +ESAPI.encoder().encodeForSQL(me, accountid) + "'");
            }
            if (functions.isValueNull(refundamount))
            {
                query.append(" and refund_amount='" +ESAPI.encoder().encodeForSQL(me, refundamount) + "'");
            }
            if (functions.isValueNull(paymentid))
            {
                query.append(" and paymentid ='" +paymentid.trim()+ "'");
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                query.append(" and timestamp >= '" + startDate + "'");
            }
            else
            {
                query.append(" and dtstamp >= " + fdtstamp);
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter1.format(calendar.getTime());
                query.append(" and timestamp <= '" + endDate + "'");
            }
            else
            {
                query.append(" and dtstamp <= " + tdtstamp);
            }

            if (functions.isValueNull(trackingid))
            {
                query.append(" and trackingId IN (" + trackingid + ")");
            }

            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");
            query.append(" order by id DESC");

            logger.debug("===count query EXCEL===" + countquery);
            logger.debug("===query for EXCEL==="+query);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            ResultSet rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

        }
        catch (SQLException se)
        {
            logger.error("SQLException----",se);
        }
        catch (SystemError se)
        {
            logger.error("SystemError----",se);
        }
        catch (Exception se)
        {
            logger.error("Exception----",se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public Hashtable cardPresentlistTransactions(String toid, String trackingid, String name, String desc, String orderdesc, String amount, String refundamount, String firstfourofccnum, String lastfourofccnum, String emailaddr, String tdtstamp, String fdtstamp, String status, int records, int pageno, String perfectmatch, boolean archive, Set<String> gatewayTypeSet, String remark, String cardtype, String issuing_bank, String statusFlag, String gateway_name, String currency, String accountid,String paymentid, String dateType ,String customerid,String telno,String telnocc,String totype)
    {
        logger.debug("Entering cardPresentlistTransactions ");
        Codec me            = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        String tablename    = "";
        String fields       = "";
        String pRefund      = "false";
        StringBuffer query  = new StringBuffer();
        Hashtable hash      = null;
        int start           = 0; // start index
        int end             = 0; // end index
        start               = (pageno - 1) * records;
        end                 = records;
        Connection conn     = null;
        Functions functions = new Functions();
        try
        {
            conn                = Database.getRDBConnection();
            Set<String> tables  = Database.getTableSet(gatewayTypeSet);
            tablename           = "transaction_card_present";
            if (archive)
            {
                //ToDO will be used once all the archive tables will be avialbale.
                tablename = "transaction_common_archive";
            }

            fields = "trackingid as trackingid,status,name,amount,refundamount,description,orderdescription,transactionTime, totype,from_unixtime("+tablename + ".dtstamp, '%Y-%m-%d %T') as dtstamp,hrcode," + tablename + ".accountid,"+tablename+".remark,currency,cardtype,customerId,toid,fromtype";

            //query.append("select " + fields + " from " + tablename + " where trackingid>0 ");
            query.append("select " + fields + " from " + tablename + " where ");

            if ("TIMESTAMP".equals(dateType) &&  functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" " + tablename + ".transactionTime >= '" + startDate + "'");
            }else if(functions.isValueNull(fdtstamp)){
                query.append(" " + tablename + ".dtstamp >= "+fdtstamp);
            }else if(!functions.isValueNull(fdtstamp) && functions.isValueNull(trackingid)){
                //ON Inquiry
                query.append(" " + tablename + ".trackingid = "+trackingid);
            }

            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {

                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());

                query.append(" and " + tablename + ".transactionTime <= '" + endDate + "'");
            }else if(functions.isValueNull(tdtstamp)){

                query.append(" and" + tablename + ".dtstamp <= "+tdtstamp);
            }

            if (functions.isValueNull(toid))
            {
                query.append(" and toid='" +ESAPI.encoder().encodeForSQL(me, toid) + "'");
            }
            if (functions.isValueNull(totype))
            {
                query.append(" and totype='" +ESAPI.encoder().encodeForSQL(me, totype) + "'");
            }
            if (functions.isValueNull(status))
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status = "reversed";
                    pRefund = "true";
                }
                query.append(" and status='" + status + "'");

            }
            /*if (!"all".equalsIgnoreCase(statusFlag))
            {
                query.append(" and bin_details."+statusFlag+"='Y'");
            }*/
            if (functions.isValueNull(amount))
            {
                query.append(" and amount='" +ESAPI.encoder().encodeForSQL(me, amount) + "'");
            }
            if (functions.isValueNull(gateway_name))
            {
                query.append(" and fromtype='" +ESAPI.encoder().encodeForSQL(me, gateway_name) + "'");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and currency='" +ESAPI.encoder().encodeForSQL(me, currency) + "'");
            }
            if (functions.isValueNull(accountid))
            {
                query.append(" and "+tablename+".accountid='" +ESAPI.encoder().encodeForSQL(me, accountid) + "'");
            }
            if (functions.isValueNull(refundamount))
            {
                query.append(" and refundamount='" +ESAPI.encoder().encodeForSQL(me, refundamount) + "'");
            }
            if (functions.isValueNull(emailaddr))
            {
                query.append(" and "+tablename+".emailaddr='"+ emailaddr + "'");
            }
            if (functions.isValueNull(telnocc))
            {
                query.append(" and "+tablename+".telnocc='"+ telnocc + "'");
            }
            if (functions.isValueNull(telno))
            {
                query.append(" and "+tablename+".telno='"+ telno + "'");
            }
            /*if(functions.isValueNull(firstfourofccnum))
            {
                query.append(" and bin_details.first_six ='" +ESAPI.encoder().encodeForSQL(me, firstfourofccnum) + "'");
            }
            if (functions.isValueNull(lastfourofccnum))
            {
                query.append(" and bin_details.last_four ='" +ESAPI.encoder().encodeForSQL(me, lastfourofccnum )+ "'");
            }*/
           /* if (functions.isValueNull(cardtype))
            {
                query.append(" and bin_brand ='" +ESAPI.encoder().encodeForSQL(me, cardtype )+ "'");
            }*/
            if (functions.isValueNull(issuing_bank))
            {
                query.append(" and issuing_bank ='" +ESAPI.encoder().encodeForSQL(me, issuing_bank )+ "'");
            }
            if (functions.isValueNull(paymentid))
            {
                query.append(" and paymentid ='" +paymentid.trim()+ "'");
            }

            if (functions.isValueNull(name))
            {
                if (perfectmatch == null)
                {
                    query.append(" and name like '%" + ESAPI.encoder().encodeForSQL(me,name) + "%'");
                }
                else
                {
                    query.append(" and name='" +ESAPI.encoder().encodeForSQL(me, name) + "'");
                }
            }
            /*if ( functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" and " + tablename + ".transactionTime >= '" + startDate + "'");
            }

            if (functions.isValueNull(tdtstamp))
            {

                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and " + tablename + ".transactionTime <= '" + endDate + "'");
            }*/

            if (functions.isValueNull(desc))
            {
                if (perfectmatch == null)
                {
                    query.append(" and description like '%" + ESAPI.encoder().encodeForSQL(me,desc) + "%'");
                }
                else
                {
                    query.append(" and description='" + ESAPI.encoder().encodeForSQL(me,desc) + "'");
                }
            }
            if (functions.isValueNull(orderdesc))
            {
                if (perfectmatch == null)
                {
                    query.append(" and orderdescription like '%" +ESAPI.encoder().encodeForSQL(me, orderdesc) + "%'");
                }
                else
                {
                    query.append(" and orderdescription='" + ESAPI.encoder().encodeForSQL(me,orderdesc) + "'");
                }
            }
            if (functions.isValueNull(trackingid))
            {
                query.append(" and " + tablename + ".trackingid IN (" + trackingid + ")");
            }
            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and captureamount > refundamount and STATUS='reversed'");
            }
            if (functions.isValueNull(remark))
            {
                if (perfectmatch == null)
                {
                    query.append(" and remark like '%" + ESAPI.encoder().encodeForSQL(me,remark) + "%'");
                }
                else
                {
                    query.append(" and remark='" +ESAPI.encoder().encodeForSQL(me, remark) + "'");
                }
            }
            if (functions.isValueNull(customerid))
            {
                query.append(" and customerId ='" +customerid.trim()+ "'");
            }
            if (functions.isValueNull(totype))
            {
                query.append(" and totype='" +ESAPI.encoder().encodeForSQL(me, totype) + "'");
            }

            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");
            query.append(" order by  dtstamp DESC,trackingid ");
            query.append(" limit " + start + "," + end);

            logger.debug("===count query===" + countquery);
            logger.debug("===query==="+query);

            hash            = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            ResultSet rs    = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

        }
        catch (SQLException se)
        {
            logger.error("SQLException----",se);
        }
        catch (SystemError se)
        {
            logger.error("SystemError----",se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        logger.debug("Leaving cardPresentlistTransactions");
        return hash;
    }


    public Hashtable getTransactionDetails(String icicitransId, boolean archive,String accountid)
    {
        logger.debug("fatch record 1");
        String tablename    = "";
        String tablename2    = "";
        StringBuffer query  = null;
        Hashtable hash      = null;
        Connection conn     = null;
        String gateway      = null;
        if(accountid != null && !accountid.equals(""))
        {
            gateway = GatewayAccountService.getGatewayAccount(accountid).getGateway();
            tablename = Database.getTableName(gateway);
            tablename2= "bin_details";
        }
        else
        {
            tablename = "transaction_common";
            tablename2= "bin_details";
        }


        if (archive)
        {
            //tablename += "_archive";        //ToDO will be used once all the archive tables will be avialbale.
            tablename = "transaction_common_archive";
            tablename2= "bin_details_archive";
        }

        try
        {
            conn = Database.getRDBConnection();
            if(tablename.equals("transaction_icicicredit")||tablename.equals("transaction_icicicredit_archive"))
            {
                query = new StringBuffer("select T.icicitransid as \"Tracking ID\",status as \"Status\",authqsiresponsecode as Remark,name as \"Cardholder_Name\",M.memberid as memberid, concat(B.first_six,'******',B.last_four) as \"ccnumber\",expdate as \"Expiry_date\",amount as \"Transaction Amount\",description as Description,orderdescription as \"Order Description\",from_unixtime(T.dtstamp) as \"Date of transaction\",T.timestamp as \"Last update\",T.emailaddr as \"Customer's Emailaddress\",T.city as City,street as Street, T.state as State,T.country as Country,company_name as \"Name of Merchant\",contact_persons as \"Contact person\",contact_emails as \"Merchant's Emailaddress\",sitename as \"Site URL\",M.telno as \"Merchants telephone Number\",authqsiresponsedesc as \"Auth Response Description\",captureamount as \"Captured Amount\",refundamount as \"Refund Amount\",chargebackamount as \"Chargeback Amount\", T.hrcode as HRCode,T.accountid,T.fromtype,T.paymentid,B.isSuccessful,B.isSettled,B.isRefund,B.isChargeback,B.isFraud,B.isRollingReserveKept,B.isRollingReserveReleased,T.ipaddress,B.subcard_type,T.telno as customerPhoneNo,T.telnocc as telephoneCode from members as M, "+tablename+" as T left join bin_details as B on T.icicitransid = B.icicitransid where T.toid=M.memberid  and T.icicitransid=?");
            }
            else if(tablename.equals("transaction_qwipi"))
            {
                query = new StringBuffer("select T.trackingid as \"Tracking ID\",T.status as \"Status\",T.remark as Remark,T.qwipiPaymentOrderNumber as paymentid,T.name as \"Cardholder_Name\",M.memberid as memberid, concat(B.first_six,'******',B.last_four) as \"ccnumber\",T.expdate as \"Expiry_date\",T.amount as \"Transaction Amount\",T.description as Description,T.orderdescription as \"Order Description\",from_unixtime(T.dtstamp) as \"Date of transaction\",T.timestamp as \"Last update\",T.emailaddr as \"Customer's Emailaddress\",T.city as City,T.street as Street, T.state as State,T.country as Country,M.company_name as \"Name of Merchant\",M.contact_persons as \"Contact person\",M.contact_emails as \"Merchant's Emailaddress\",M.sitename as \"Site URL\",M.telno as \"Merchants telephone Number\",T.captureamount as \"Captured Amount\",T.refundamount as \"Refund Amount\",T.chargebackamount as \"Chargeback Amount\", T.hrcode as HRCode,T.accountid,T.fromtype,B.isSuccessful,B.isSettled,B.isRefund,B.isChargeback,B.isFraud,B.isRollingReserveKept,B.isRollingReserveReleased,T.ipaddress,D.parentid as HistoryTrackingId,D.action as HistoryAction,D.status as HistoryStatus,D.timestamp as HistoryTimeStamp,D.amount as HistoryAmount,D.responseRemark as HistoryRemark,D.qwipiPaymentOrderNumber as HistoryResponseTransId,D.status as HistoryResponseStatus,D.responseResultCode as HistoryResponseCode,D.responseMD5Info as HistoryResponseDesc,D.actionexecutorid as HistoryExecutorId,D.actionexecutorname as HistoryExecutorName,D.ipaddress as HistoryIpAddress,D.responseBillingDescription as Historyresponsedescriptor,B.subcard_type,B.bin_brand,B.bin_trans_type,B.bin_card_type,B.bin_card_category,B.bin_usage_type,D.responseDateTime as ResponseTime,T.telno as customerPhoneNo,T.telnocc as telephoneCode from members as M, "+tablename+" as T left join "+tablename+"_details as D on T.trackingid = D.parentid left join bin_details as B on T.trackingid = B.icicitransid where T.toid=M.memberid  and T.trackingid=?");
            }
            else
            {
                String fkName       = null;
                String fRemark      = null;
                String fResponseId  = "";
                String fResponseCode    = "";
                String fResponseStatus  = "";
                String fResponseDesc    = "";
                String fBillingDes      = "";
                String fPaymentId       = "";
                if(tablename.equals("transaction_ecore"))
                {
                    fkName          = "parentid";
                    fRemark         = "responseRemark";
                    fResponseId     = "ecorePaymentOrderNumber";
                    fResponseCode   = "responseResultCode";
                    fResponseStatus = "status";
                    fResponseDesc   = "responseRemark";
                    fBillingDes     = "responseBillingDescription";
                    fPaymentId      = "ecorePaymentOrderNumber";
                }
                else
                {
                    fkName          = "trackingid";
                    fRemark         = "remark";
                    fResponseId     = "responsetransactionid";
                    fResponseCode   = "responsecode";
                    fResponseStatus = "responsetransactionstatus";
                    fResponseDesc   = "responsedescription";
                    fBillingDes     = "responsedescriptor";
                    fPaymentId      = "paymentid";
                }

                query = new StringBuffer("select T.trackingid as \"Tracking ID\",T.currency,T.terminalid,T.status as \"Status\",T.name as \"Cardholder_Name\",M.memberid as memberid, concat(B.first_six,'******',B.last_four) as \"ccnumber\",T.expdate as \"Expiry_date\",T.amount as \"Transaction Amount\",T.templateamount as\"Template Amount\",T.templatecurrency,D.errorName as\"Error Name\", T.description as Description,T.orderdescription as \"Order Description\",from_unixtime(T.dtstamp) as \"Date of transaction\",T.timestamp as \"Last update\",T.emailaddr as \"Customer's Emailaddress\",T.eci as \"ECI\",T.customerId as \"CustomerId\",T.cardtypeid as \"CardType\",T.city as City,T.notificationUrl,T.street as Street, T.state as State,T.country as Country,M.company_name as \"Name of Merchant\",M.contact_persons as \"Contact person\",M.contact_emails as \"Merchant's Emailaddress\",M.sitename as \"Site URL\",M.telno as \"Merchants telephone Number\",T.captureamount as \"Captured Amount\",T.payoutamount as \"Payout Amount\",T.refundamount as \"Refund Amount\",T.chargebackamount as \"Chargeback Amount\", T.hrcode as HRCode,T.emiCount as emi,T.ewalletid as walletId,T.accountid,T.fromtype,B.isSuccessful,B.isSettled,B.isRefund,B.isChargeback,B.isFraud,B.isRollingReserveKept,B.isRollingReserveReleased,T.ipaddress,T."+fPaymentId+" as paymentid,D.trackingid as HistoryTrackingId,D.action as HistoryAction,D.status as HistoryStatus,D.timestamp as HistoryTimeStamp,D.amount as HistoryAmount,D.currency as HistoryCurrency,D.templateamount as Historytemplateamount,D.templatecurrency as Historytemplatecurrency,D."+fRemark+" as HistoryRemark,D."+fResponseId+" as HistoryResponseTransId,D."+fResponseStatus+" as HistoryResponseStatus,D."+fResponseCode+" as HistoryResponseCode,D."+fResponseDesc+" as HistoryResponseDesc,D."+fBillingDes+" as Historyresponsedescriptor,D.actionexecutorid as HistoryExecutorId,D.actionexecutorname as HistoryExecutorName,D.ipaddress as HistoryIpAddress,D.responsehashinfo,D.arn,D.walletAmount,D.walletCurrency,D.transactionReceiptImg,B.subcard_type, B.bin_brand,B.bin_trans_type,B.bin_card_type,B.bin_card_category,B.bin_usage_type,B.bin_sub_brand,B.issuing_bank,B.country_name,T.arn,T.rrn,T.authorization_code,T.chargebackinfo,D.responsetime as ResponseTime,T.telno as customerPhoneNo,T.telnocc as telephoneCode,T.machineid from members as M, "+tablename+" as T left join "+tablename+"_details as D on T.trackingid = D."+fkName+" left join "+tablename2+" as B on T.trackingid = B.icicitransid where T.toid=M.memberid  and T.trackingid=? ORDER BY D.detailid");
            }
            logger.error("QUERY for Admin interface "+query.toString());
            PreparedStatement pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1,icicitransId);

            hash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());
            logger.debug("query for transaction list-----" + pstmt);
        }
        catch (SQLException se)
        {
            logger.error("SQLException----",se);
        }
        catch (SystemError se)
        {
            logger.error("SystemError----",se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        logger.debug("Leaving getTransactionDetails");
        return hash;
    }

    public Hashtable getCardPresentTransactionDetails(String icicitransId, boolean archive,String accountid)
    {
        logger.debug("Entering getCardPresentTransactionDetails ");
        String tablename    = "";
        StringBuffer query  = null;
        Hashtable hash      = null;
        Connection conn     = null;
        String gateway      = null;
        if(accountid != null && !accountid.equals(""))
        {
            gateway = GatewayAccountService.getGatewayAccount(accountid).getGateway();
            tablename = Database.getTableName(gateway);
        }
        else
        {
            tablename = "transaction_card_present";
        }


        if (archive)
        {
            //tablename += "_archive";        //ToDO will be used once all the archive tables will be avialbale.
            tablename = "transaction_icicicredit_archive";
        }

        try
        {
            conn = Database.getRDBConnection();
            if(tablename.equals("transaction_icicicredit")||tablename.equals("transaction_icicicredit_archive"))
            {
                query = new StringBuffer("select T.icicitransid as \"Tracking ID\",status as \"Status\",authqsiresponsecode as Remark,name as \"Cardholder_Name\",M.memberid as memberid,expdate as \"Expiry_date\",amount as \"Transaction Amount\",description as Description,orderdescription as \"Order Description\",from_unixtime(T.dtstamp) as \"Date of transaction\",T.timestamp as \"Last update\",T.emailaddr as \"Customer's Emailaddress\",T.city as City,street as Street, T.state as State,T.country as Country,company_name as \"Name of Merchant\",contact_persons as \"Contact person\",contact_emails as \"Merchant's Emailaddress\",sitename as \"Site URL\",M.telno as \"Merchants telephone Number\",authqsiresponsedesc as \"Auth Response Description\",captureamount as \"Captured Amount\",refundamount as \"Refund Amount\",chargebackamount as \"Chargeback Amount\", T.hrcode as HRCode,T.accountid,T.fromtype,T.paymentid,T.ipaddress,T.transactionTime,T.telno as customerPhoneNo,T.telnocc as telephoneCode from members as M, "+tablename+" as T where T.toid=M.memberid  and T.icicitransid=?");
            }
            else if(tablename.equals("transaction_qwipi"))
            {
                query = new StringBuffer("select T.trackingid as \"Tracking ID\",T.status as \"Status\",T.remark as Remark,T.qwipiPaymentOrderNumber as paymentid,T.name as \"Cardholder_Name\",M.memberid as memberid, T.expdate as \"Expiry_date\",T.amount as \"Transaction Amount\",T.description as Description,T.orderdescription as \"Order Description\",from_unixtime(T.dtstamp) as \"Date of transaction\",T.timestamp as \"Last update\",T.emailaddr as \"Customer's Emailaddress\",T.city as City,T.street as Street, T.state as State,T.country as Country,M.company_name as \"Name of Merchant\",M.contact_persons as \"Contact person\",M.contact_emails as \"Merchant's Emailaddress\",M.sitename as \"Site URL\",M.telno as \"Merchants telephone Number\",T.captureamount as \"Captured Amount\",T.refundamount as \"Refund Amount\",T.chargebackamount as \"Chargeback Amount\", T.hrcode as HRCode,T.accountid,T.fromtype,T.ipaddress,D.parentid as HistoryTrackingId,D.action as HistoryAction,D.status as HistoryStatus,D.timestamp as HistoryTimeStamp,D.amount as HistoryAmount,D.responseRemark as HistoryRemark,D.qwipiPaymentOrderNumber as HistoryResponseTransId,D.status as HistoryResponseStatus,D.responseResultCode as HistoryResponseCode,D.responseMD5Info as HistoryResponseDesc,D.actionexecutorid as HistoryExecutorId,D.actionexecutorname as HistoryExecutorName,D.ipaddress as HistoryIpAddress,D.responseBillingDescription as Historyresponsedescriptor,T.transactionTime,D.responsetime as ResponseTime,T.telno as customerPhoneNo,T.telnocc as telephoneCode from members as M, "+tablename+" as T left join transaction_common_details_card_present as D on T.trackingid = D.parentid where T.toid=M.memberid  and T.trackingid=?");
            }
            else
            {
                String fkName       = null;
                String fRemark      = null;
                String fResponseId  = "";
                String fResponseCode    = "";
                String fResponseStatus  = "";
                String fResponseDesc    = "";
                String fBillingDes      = "";
                String fPaymentId       = "";
                if(tablename.equals("transaction_ecore"))
                {
                    fkName          = "parentid";
                    fRemark         = "responseRemark";
                    fResponseId     = "ecorePaymentOrderNumber";
                    fResponseCode   = "responseResultCode";
                    fResponseStatus = "status";
                    fResponseDesc   = "responseRemark";
                    fBillingDes     = "responseBillingDescription";
                    fPaymentId      = "ecorePaymentOrderNumber";
                }
                else
                {
                    fkName      = "trackingid";
                    fRemark     = "remark";
                    fResponseId = "responsetransactionid";
                    fResponseCode   = "responsecode";
                    fResponseStatus = "responsetransactionstatus";
                    fResponseDesc   = "responsedescription";
                    fBillingDes     = "responsedescriptor";
                    fPaymentId      = "paymentid";
                }

                query = new StringBuffer("select T.trackingid as \"Tracking ID\",T.currency,T.terminalid,T.status as \"Status\",T.name as \"Cardholder_Name\",M.memberid as memberid,T.expdate as \"Expiry_date\",T.amount as \"Transaction Amount\",T.templateamount as\"Template Amount\",T.templatecurrency,D.errorName as\"Error Name\", T.description as Description,T.orderdescription as \"Order Description\",from_unixtime(T.dtstamp) as \"Date of transaction\",T.timestamp as \"Last update\",T.emailaddr as \"Customer's Emailaddress\",T.eci as \"ECI\",T.customerId as \"CustomerId\",T.cardtypeid as \"CardType\",T.city as City,T.notificationUrl,T.street as Street, T.state as State,T.country as Country,M.company_name as \"Name of Merchant\",M.contact_persons as \"Contact person\",M.contact_emails as \"Merchant's Emailaddress\",M.sitename as \"Site URL\",M.telno as \"Merchants telephone Number\",T.captureamount as \"Captured Amount\",T.payoutamount as \"Payout Amount\",T.refundamount as \"Refund Amount\",T.chargebackamount as \"Chargeback Amount\", T.hrcode as HRCode,T.emiCount as emi,T.ewalletid as walletId,T.accountid,T.fromtype,T.ipaddress,T."+fPaymentId+" as paymentid,D.trackingid as HistoryTrackingId,D.action as HistoryAction,D.status as HistoryStatus,D.timestamp as HistoryTimeStamp,D.amount as HistoryAmount,D.currency as HistoryCurrency,D.templateamount as Historytemplateamount,D.templatecurrency as Historytemplatecurrency,D."+fRemark+" as HistoryRemark,D."+fResponseId+" as HistoryResponseTransId,D."+fResponseStatus+" as HistoryResponseStatus,D."+fResponseCode+" as HistoryResponseCode,D."+fResponseDesc+" as HistoryResponseDesc,D."+fBillingDes+" as Historyresponsedescriptor,D.actionexecutorid as HistoryExecutorId,D.actionexecutorname as HistoryExecutorName,D.ipaddress as HistoryIpAddress,D.responsehashinfo,D.arn,D.walletAmount,D.walletCurrency ,T.transactionTime,D.responsetime as ResponseTime,T.telno as customerPhoneNo,T.telnocc as telephoneCode,T.machineid from members as M, transaction_card_present as T left join transaction_common_details_card_present as D on T.trackingid = D."+fkName+" where T.toid=M.memberid  and T.trackingid=?");
            }

            PreparedStatement pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1,icicitransId);

            hash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());
            logger.debug("query for transaction list getCardPresentTransactionDetails-----" + pstmt);
        }
        catch (SQLException se)
        {
            logger.error("SQLException----",se);
        }
        catch (SystemError se)
        {
            logger.error("SystemError----",se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        logger.debug("Leaving getCardPresentTransactionDetails");
        return hash;
    }



    public TransactionDetailsVO getTransactionDetailsToProcessCommonSettlement(String trackingId,String tablename) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        TransactionDetailsVO transactionDetailsVO = null;
        try
        {
            con = Database.getConnection();
            StringBuffer sb = new StringBuffer("select toid,description,trackingid,amount,captureamount,status,accountid from "+tablename+" where trackingid=?");
            pstmt = con.prepareStatement(sb.toString());
            pstmt.setString(1, trackingId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                transactionDetailsVO = new TransactionDetailsVO();
                transactionDetailsVO.setToid(rs.getString("toid"));
                transactionDetailsVO.setDescription(rs.getString("description"));
                transactionDetailsVO.setTrackingid(rs.getString("trackingid"));
                transactionDetailsVO.setAmount(rs.getString("amount"));
                transactionDetailsVO.setCaptureAmount(rs.getString("captureamount"));
                transactionDetailsVO.setStatus(rs.getString("status"));
                transactionDetailsVO.setAccountId(rs.getString("accountid"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getTransactionDetailsToProcessCommonSettlement", null, "Common", "SQLException while connecting to transaction common  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getTransactionDetailsToProcessCommonSettlement()", null, "Common", "SystemError while connecting to transaction common  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionDetailsVO;
    }

    public Hashtable listTransactionsForExportInExcel(String toid, String pgTypeId, String trackingid, String name, String desc, String orderdesc, String amount, String firstfourofccnum, String lastfourofccnum, String emailaddr, String tdtstamp, String fdtstamp, String status, String remark,String paymentId, String perfectmatch, boolean archive, Set<String> gatewayTypeSet, String gateway_name, String accountid, String dateType, String currency, String cardtype, String issuing_bank,String arn , String rrn , String auth,String transactionMode, String customerid, String statusflag,String telno,String telnocc,String totype,String bankname, String bankaccount,String ifsc ,String fromid) throws PZDBViolationException
    {
        String pRefund      = "false";
        String tableName    = "";
        String fields       = "";
        StringBuffer query  = new StringBuffer();
        Hashtable hash      = null;
        Codec me            = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Connection conn     = null;
        try
        {
            conn                    = Database.getRDBConnection();
            Set<String> tableSet    = Database.getTableSet(gatewayTypeSet);
            /*Iterator iterator = tableSet.iterator();
            while (iterator.hasNext())
            {*/
            tableName = "transaction_common";
            if (archive)
            {
                //tablename += "_archive";        //ToDO will be used once all the archive tables will be avialbale.
                tableName = "transaction_common_archive";
            }
                /*if (tableName.equals("transaction_icicicredit") || tableName.equals("transaction_icicicredit_archive"))
                {
                    fields = "icicitransid as trackingid,toid as memberid,name,amount,captureamount,templateamount,refundamount,paymodeid,cardtype,status,remark,description,orderdescription,t.dtstamp,hrcode,t.accountid";
                }
                else
                {*/
            fields = "t.trackingid as trackingid,t.status,t.remark,t.toid as memberid,t.paymentid, t.country, t.customerId, bd.issuing_bank, " +
                    "bd.bin_card_category, bd.bin_sub_brand, bd.subcard_type, bd.bin_card_type,bd.country_name as bin_country_name,t.name,t.amount," +
                    "t.captureamount,t.templateamount,t.refundamount,t.chargebackamount,t.payoutamount,t.paymodeid,t.cardtype,t.chargebackinfo," +
                    "t.description,t.orderdescription,t.dtstamp,t.timestamp,t.hrcode,t.accountid,t.terminalid,t.emailaddr,t.currency,t.ccnum," +
                    "m.company_name,t.totype,t.rrn,t.arn,t.authorization_code,t.transaction_mode,t.telnocc,t.telno,t.fromid ,t.processingbank,t.ipAddress,t.customerIp,bd.first_six,bd.last_four ";
            //}
                /*if (tableName.equals("transaction_icicicredit") || tableName.equals("transaction_icicicredit_archive"))
                {
                    query.append("select " + fields + " from " + tableName + " as t LEFT OUTER join bin_details as bn on t.icicitransid = bn.icicitransid where trackingid>0 ");
                }
                else
                {*/
            //query.append("select " + fields + " from " + tableName + " as t  LEFT join bin_details as bd on t.trackingid = bd.icicitransid join members AS m on m.memberid=t.toid  where t.trackingid>0");
            query.append("select " + fields + " from " + tableName + " as t  LEFT join bin_details as bd on t.trackingid = bd.icicitransid join members AS m on m.memberid=t.toid  where ");
            // }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                query.append(" t.timestamp >= '" + startDate + "'");
            }
            else
            {
                query.append(" t.dtstamp >= " + fdtstamp);
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter2.format(calendar.getTime());
                query.append(" and t.timestamp <= '" + endDate + "'");
            }
            else
            {
                query.append(" and t.dtstamp <= " + tdtstamp);
            }
            if (functions.isValueNull(toid))
            {
                query.append(" and t.toid=" + ESAPI.encoder().encodeForSQL(me, toid));
            }
            if (functions.isValueNull(totype))
            {
                query.append(" and t.totype ='" + ESAPI.encoder().encodeForSQL(me,totype)+ "'");
            }
            /*if (functions.isValueNull(status))
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status = "reversed";
                }
                query.append(" and status='" + status + "' and captureamount > refundamount ");*/

            if (functions.isValueNull(status))
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status = "reversed";
                    pRefund = "true";
                }
                query.append(" and t.status='" + status + "'");
            }
            if (!"all".equalsIgnoreCase(statusflag) && !"begun".equalsIgnoreCase(status))
            {
                query.append(" and bd."+statusflag+"='Y'");
            }
            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and captureamount > refundamount and STATUS='reversed'");
            }

            if (functions.isValueNull(remark))
            {
                query.append(" and t.remark='" + remark + "'");
            }
            if (functions.isValueNull(amount))
            {
                query.append(" and t.amount='" + ESAPI.encoder().encodeForSQL(me,amount) + "'");
            }
            if (functions.isValueNull(emailaddr))
            {
                query.append(" and t.emailaddr='" + emailaddr + "'");
            }
            if(functions.isValueNull(telnocc))
            {
                query.append(" and t.telnocc='" + telnocc +"'");
            }
            if (functions.isValueNull(telno))
            {
                query.append(" and t.telno='" + telno + "'");
            }

            if (functions.isValueNull(firstfourofccnum))
            {
                query.append(" and bd.first_six ='" +ESAPI.encoder().encodeForSQL(me, firstfourofccnum) + "'");
            }
            if (functions.isValueNull(lastfourofccnum))
            {
                query.append(" and bd.last_four ='" + ESAPI.encoder().encodeForSQL(me,lastfourofccnum )+ "'");
            }
            if (functions.isValueNull(cardtype))
            {
                query.append(" and (bd.bin_brand ='" +ESAPI.encoder().encodeForSQL(me, cardtype )+ "' OR t.cardtype='"+ESAPI.encoder().encodeForSQL(me, cardtype )+ "')");
            }
            if (functions.isValueNull(issuing_bank))
            {
                query.append(" and bd.issuing_bank ='" +ESAPI.encoder().encodeForSQL(me, issuing_bank )+ "'");
            }
            if (functions.isValueNull(gateway_name))
            {
                query.append(" and t.fromtype ='" + ESAPI.encoder().encodeForSQL(me,gateway_name )+ "'");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and t.currency ='" + ESAPI.encoder().encodeForSQL(me,currency)+ "'");
            }
            if (functions.isValueNull(arn))
            {
                query.append(" and t.arn ='" + ESAPI.encoder().encodeForSQL(me,arn)+ "'");
            }
            if (functions.isValueNull(rrn))
            {
                query.append(" and t.rrn ='" + ESAPI.encoder().encodeForSQL(me,rrn)+ "'");
            }
            if (functions.isValueNull(auth))
            {
                query.append(" and t.authorization_code ='" + ESAPI.encoder().encodeForSQL(me,auth)+ "'");
            }
            if (functions.isValueNull(paymentId))
            {
                query.append(" and t.paymentid ='"+ ESAPI.encoder().encodeForSQL(me,paymentId)+ "'");
            }
            if (functions.isValueNull(customerid))
            {
                query.append(" and t.customerId = '" + customerid + "'");
            }
          /*  if (functions.isValueNull(bankname))
            {
                query.append(" and ts.fullname ='"+ESAPI.encoder().encodeForSQL(me,bankname)+"'");
            }
            if (functions.isValueNull(bankaccount))
            {
                query.append(" and ts.bankaccount ='"+ESAPI.encoder().encodeForSQL(me,bankaccount)+ "'");
            }
            if (functions.isValueNull(ifsc))
            {
                query.append(" and ts.ifsc ='"+ESAPI.encoder().encodeForSQL(me,ifsc)+ "'");
            }*/
            logger.debug("accountid--"+accountid);
            logger.debug("accountid Is Null--"+functions.isValueNull(accountid));
            if (functions.isValueNull(accountid))
            {
                query.append(" and t.accountid ='" + ESAPI.encoder().encodeForSQL(me,accountid )+ "'");
            }
            if (functions.isValueNull(name))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.name like '%" +ESAPI.encoder().encodeForSQL(me, name) + "%'");
                }
                else
                {
                    query.append(" and t.name='" +ESAPI.encoder().encodeForSQL(me, name) + "'");
                }
            }

            /*if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
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
                DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter2.format(calendar.getTime());
                query.append(" and t.timestamp <= '" + endDate + "'");
            }
            else
            {
                query.append(" and t.dtstamp <= " + tdtstamp);
            }*/

            if (functions.isValueNull(desc))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.description like '%" + ESAPI.encoder().encodeForSQL(me, desc) + "%'");
                }
                else
                {
                    query.append(" and t.description='" +ESAPI.encoder().encodeForSQL(me, desc) + "'");
                }
            }
            if (functions.isValueNull(orderdesc))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.orderdescription like '%" +ESAPI.encoder().encodeForSQL(me, orderdesc )+ "%'");
                }
                else
                {
                    query.append(" and t.orderdescription='" +ESAPI.encoder().encodeForSQL(me, orderdesc) + "'");
                }
            }
            if (functions.isValueNull(trackingid))
            {
                    /*if (tableName.equals("transaction_icicicredit") || tableName.equals("transaction_icicicredit_archive"))
                    {
                        query.append(" and t.icicitransid=" +ESAPI.encoder().encodeForSQL(me, trackingid));
                    }
                    else
                    {*/
                //query.append(" and t.trackingid=" +ESAPI.encoder().encodeForSQL(me, trackingid));
                query.append(" and t.trackingid IN (" + trackingid + ")");
                //}
            }
            if (functions.isValueNull(transactionMode))
            {

                query.append(" and t.transaction_mode ='" + transactionMode.trim() + "'");
            }

            if(functions.isValueNull(fromid)){
                query.append("and t.fromid='"+ ESAPI.encoder().encodeForSQL(me, fromid)+ "'");
            }
            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");
            query.append(" order by dtstamp DESC,trackingid ");
            logger.debug("export to excel in admin:::::"+query.toString());
            hash            = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            ResultSet rs    = Database.executeQuery(countquery.toString(), conn);
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
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getTransactionDetailsToProcessCommonSettlement", null, "Common", "SQLException while connecting to transaction  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getTransactionDetailsToProcessCommonSettlement()", null, "Common", "SystemError while connecting to transaction  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }
    public Hashtable listTransactionsForExportInExcelForDetailStatus(String toid, String pgTypeId, String trackingid, String name, String desc, String orderdesc, String amount, String firstfourofccnum, String lastfourofccnum, String emailaddr, String tdtstamp, String fdtstamp, String status, String remark,String paymentId, String perfectmatch, boolean archive, Set<String> gatewayTypeSet, String gateway_name, String accountid, String dateType, String currency, String cardtype, String issuing_bank,String arn , String rrn , String auth,String detailStatus,String transactionMode, String customerid, String statusflag,String telno,String telnocc,String totype,String fromid) throws PZDBViolationException
    {
        String pRefund      = "false";
        String tableName    = "";
        String fields       = "";
        StringBuffer query  = new StringBuffer();
        Hashtable hash      = null;
        Codec me            = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Connection conn     = null;
        try
        {
            conn                    = Database.getRDBConnection();
            Set<String> tableSet    = Database.getTableSet(gatewayTypeSet);
            tableName               = "transaction_common";
            if (archive)
            {
                tableName = "transaction_common_archive";
            }
            query.append("SELECT t.trackingid as trackingid,t.status,t.remark,t.toid as memberid,t.paymentid, t.country,tcd.status AS detailStatus," +
                    " tcd.amount AS detailAmount,tcd.`timestamp` AS detailTimestamp, t.customerId, bd.issuing_bank, bd.bin_card_category, " +
                    " bd.bin_sub_brand, bd.subcard_type, bd.bin_card_type,bd.country_name as bin_country_name,t.name,t.amount,t.captureamount," +
                    " t.templateamount,t.refundamount,t.chargebackamount,t.payoutamount,t.paymodeid,t.cardtype,t.chargebackinfo,t.description," +
                    " t.orderdescription,t.dtstamp,t.timestamp,t.hrcode,t.accountid,t.terminalid,t.emailaddr,t.currency,t.ccnum,m.company_name," +
                    " t.totype,t.rrn,t.arn,t.authorization_code,t.transaction_mode,t.telnocc,t.telno,t.fromid,t.ipAddress,t.customerIp,bd.first_six,bd.last_four FROM members as m JOIN transaction_common AS t " +
                    " JOIN transaction_common_details AS tcd JOIN bin_details as bd WHERE t.trackingid=tcd.trackingid AND " +
                    " tcd.trackingid=bd.icicitransid AND m.memberid=t.toid");
            //fields = "t.trackingid as trackingid,t.status,t.remark,t.toid as memberid,t.paymentid, t.country, t.customerId, bd.issuing_bank, bd.bin_card_category, bd.bin_sub_brand, bd.subcard_type, bd.bin_card_type,bd.country_name as bin_country_name,t.name,t.amount,t.captureamount,t.templateamount,t.refundamount,t.chargebackamount,t.payoutamount,t.paymodeid,t.cardtype,t.chargebackinfo,t.description,t.orderdescription,t.dtstamp,t.timestamp,t.hrcode,t.accountid,t.terminalid,t.emailaddr,t.currency,t.ccnum,m.company_name,t.totype,t.rrn,t.arn,t.authorization_code";
            //query.append("select " + fields + " from " + tableName + " as t join bin_details as bd on t.trackingid = bd.icicitransid join members AS m on m.memberid=t.toid where trackingid>0");
            if (functions.isValueNull(toid))
            {
                query.append(" and t.toid=" + ESAPI.encoder().encodeForSQL(me, toid));
            }
            if (functions.isValueNull(totype))
            {
                query.append(" and t.totype ='" + ESAPI.encoder().encodeForSQL(me,totype)+ "'");
            }
            if (functions.isValueNull(status))
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status = "reversed";
                    pRefund = "true";
                }
                query.append(" and tcd.status='" + status + "'");
            }
            if (!"all".equalsIgnoreCase(statusflag) && !"begun".equalsIgnoreCase(status))
            {
                query.append(" and bd."+statusflag+"='Y'");
            }
            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and t.captureamount > t.refundamount and t.STATUS='reversed'");
            }

            if (functions.isValueNull(remark))
            {
                query.append(" and t.remark='" + remark + "'");
            }
            if (functions.isValueNull(amount))
            {
                query.append(" and t.amount='" + ESAPI.encoder().encodeForSQL(me,amount) + "'");
            }
            if (functions.isValueNull(emailaddr))
            {
                query.append(" and t.emailaddr='" + emailaddr + "'");
            }
            if (functions.isValueNull(telnocc))
            {
                query.append(" and t.telnocc='" + telnocc + "'");
            }
            if (functions.isValueNull(telno))
            {
                query.append(" and t.telno='" + telno + "'");
            }
            if (functions.isValueNull(firstfourofccnum))
            {
                query.append(" and bd.first_six ='" +ESAPI.encoder().encodeForSQL(me, firstfourofccnum) + "'");
            }
            if (functions.isValueNull(lastfourofccnum))
            {
                query.append(" and bd.last_four ='" + ESAPI.encoder().encodeForSQL(me,lastfourofccnum )+ "'");
            }
            if (functions.isValueNull(cardtype))
            {
                query.append(" and (bd.bin_brand ='" +ESAPI.encoder().encodeForSQL(me, cardtype )+ "'  OR  cardtype='"+ESAPI.encoder().encodeForSQL(me, cardtype )+ "')");
            }
            if (functions.isValueNull(issuing_bank))
            {
                query.append(" and bd.issuing_bank ='" +ESAPI.encoder().encodeForSQL(me, issuing_bank )+ "'");
            }
            if (functions.isValueNull(gateway_name))
            {
                query.append(" and t.fromtype ='" + ESAPI.encoder().encodeForSQL(me,gateway_name )+ "'");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and t.currency ='" + ESAPI.encoder().encodeForSQL(me,currency)+ "'");
            }
            if (functions.isValueNull(arn))
            {
                query.append(" and t.arn ='" + ESAPI.encoder().encodeForSQL(me,arn)+ "'");
            }
            if (functions.isValueNull(rrn))
            {
                query.append(" and t.rrn ='" + ESAPI.encoder().encodeForSQL(me,rrn)+ "'");
            }
            if (functions.isValueNull(auth))
            {
                query.append(" and t.authorization_code ='" + ESAPI.encoder().encodeForSQL(me,auth)+ "'");
            }
            if (functions.isValueNull(paymentId))
            {
                query.append(" and t.paymentid ='" + ESAPI.encoder().encodeForSQL(me,paymentId)+ "'");
            }
            if (functions.isValueNull(customerid))
            {
                query.append(" and t.customerId = '" + customerid + "'");
            }
            if(functions.isValueNull(fromid)){
                query.append("and t.fromid='"+ESAPI.encoder().encodeForSQL(me,fromid)+"'");
            }
            logger.debug("accountid--"+accountid);
            logger.debug("accountid Is Null--"+functions.isValueNull(accountid));
            if (functions.isValueNull(accountid))
            {
                query.append(" and t.accountid ='" + ESAPI.encoder().encodeForSQL(me,accountid )+ "'");
            }
            if (functions.isValueNull(name))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.name like '%" +ESAPI.encoder().encodeForSQL(me, name) + "%'");
                }
                else
                {
                    query.append(" and t.name='" +ESAPI.encoder().encodeForSQL(me, name) + "'");
                }
            }
            long milliSeconds = Long.parseLong(fdtstamp + "000");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
            DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startDate = formatter1.format(calendar.getTime());

            query.append(" and tcd.timestamp >= '" + startDate + "'");

            long milliSeconds1 = Long.parseLong(tdtstamp + "000");
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(milliSeconds1);
            DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String endDate = formatter2.format(calendar1.getTime());
            query.append(" and tcd.timestamp <= '" + endDate + "'");

            /*if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                query.append(" and tcd.timestamp >= '" + startDate + "'");
            }
            else
            {
                query.append(" and tcd.dtstamp >= " + fdtstamp);
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter2.format(calendar.getTime());
                query.append(" and tcd.timestamp <= '" + endDate + "'");
            }
            else
            {
                query.append(" and tcd.dtstamp <= " + tdtstamp);
            }*/

            if (functions.isValueNull(desc))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.description like '%" + ESAPI.encoder().encodeForSQL(me, desc) + "%'");
                }
                else
                {
                    query.append(" and t.description='" +ESAPI.encoder().encodeForSQL(me, desc) + "'");
                }
            }
            if (functions.isValueNull(orderdesc))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.orderdescription like '%" +ESAPI.encoder().encodeForSQL(me, orderdesc )+ "%'");
                }
                else
                {
                    query.append(" and t.orderdescription='" +ESAPI.encoder().encodeForSQL(me, orderdesc) + "'");
                }
            }
            if (functions.isValueNull(trackingid))
            {
                query.append(" and t.trackingid IN (" + trackingid + ")");
            }
            if (functions.isValueNull(transactionMode))
            {
                query.append(" and t.transaction_mode ='" + transactionMode.trim() + "'");
            }

            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");
            query.append(" order by t.dtstamp DESC,t.trackingid ");
            logger.debug("export to excel in admin for details status:::::"+query.toString());

            hash            = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            ResultSet rs    = Database.executeQuery(countquery.toString(), conn);

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
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getTransactionDetailsToProcessCommonSettlement", null, "Common", "SQLException while connecting to transaction  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getTransactionDetailsToProcessCommonSettlement()", null, "Common", "SystemError while connecting to transaction  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public Hashtable listCPTransactionsForExportInExcel(String toid, String pgTypeId, String trackingid, String name, String desc, String orderdesc, String amount, String firstfourofccnum, String lastfourofccnum, String emailaddr, String tdtstamp, String fdtstamp, String status, String remark,String paymentId, String perfectmatch, boolean archive, Set<String> gatewayTypeSet, String gateway_name, String accountid, String dateType, String currency, String cardtype, String issuing_bank, String customerid,String fromid) throws PZDBViolationException
    {
        String tableName    = "";
        String fields       = "";
        StringBuffer query  = new StringBuffer();
        Hashtable hash      = null;
        Codec me            = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Connection conn     = null;
        try
        {
            conn                    = Database.getRDBConnection();
            Set<String> tableSet    = Database.getTableSet(gatewayTypeSet);
            /*Iterator iterator = tableSet.iterator();
            while (iterator.hasNext())
            {*/
            tableName = "transaction_card_present";
            if (archive)
            {
                //tablename += "_archive";        //ToDO will be used once all the archive tables will be avialbale.
                tableName = "transaction_common_archive";
            }
                /*if (tableName.equals("transaction_icicicredit") || tableName.equals("transaction_icicicredit_archive"))
                {
                    fields = "icicitransid as trackingid,toid as memberid,name,amount,captureamount,templateamount,refundamount,paymodeid,cardtype,status,remark,description,orderdescription,t.dtstamp,hrcode,t.accountid";
                }
                else
                {*/
            fields = "t.trackingid as trackingid, t.status, t.remark, t.toid as memberid, t.paymentid, t.country, t.customerId, t.name, t.amount,t.captureamount, t.templateamount, t.refundamount, t.chargebackamount, t.payoutamount, t.paymodeid,t.cardtype,t.chargebackinfo,t.description,t.orderdescription,t.dtstamp,t.timestamp,t.hrcode,t.accountid,t.terminalid,t.emailaddr,t.currency,t.ccnum,m.company_name,t.totype,t.rrn,t.arn,t.authorization_code,t.fromid";
            //}
                /*if (tableName.equals("transaction_icicicredit") || tableName.equals("transaction_icicicredit_archive"))
                {
                    query.append("select " + fields + " from " + tableName + " as t LEFT OUTER join bin_details as bn on t.icicitransid = bn.icicitransid where trackingid>0 ");
                }
                else
                {*/
           // query.append("select " + fields + " from " + tableName + " as t  join members AS m on m.memberid=t.toid where trackingid>0");
            query.append("select " + fields + " from " + tableName + " as t  join members AS m on m.memberid=t.toid where ");
            // }
            if ("TIMESTAMP".equals(dateType) &&  functions.isValueNull(fdtstamp))
            {
                long milliSeconds       = Long.parseLong(fdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" t.transactionTime >= '" + startDate + "'");
            }else if(functions.isValueNull(fdtstamp)){
                query.append(" t.dtstamp >= "+fdtstamp);
            }

            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and t.transactionTime <= '" + endDate + "'");
            }else if(functions.isValueNull(tdtstamp)){
                query.append(" and t.dtstamp <= " + tdtstamp );
            }

            if (functions.isValueNull(toid))
            {
                query.append(" and t.toid=" + ESAPI.encoder().encodeForSQL(me, toid));
            }
            if (functions.isValueNull(status))
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status = "reversed";
                }
                query.append(" and status='" + status + "' and captureamount > refundamount ");

            }
            if (functions.isValueNull(remark))
            {
                query.append(" and t.remark='" + remark + "'");
            }
            if (functions.isValueNull(amount))
            {
                query.append(" and t.amount='" + ESAPI.encoder().encodeForSQL(me,amount) + "'");
            }
            if (functions.isValueNull(emailaddr))
            {
                query.append(" and t.emailaddr='" + emailaddr + "'");
            }
            if (functions.isValueNull(firstfourofccnum))
            {
                query.append(" and bd.first_six ='" +ESAPI.encoder().encodeForSQL(me, firstfourofccnum) + "'");
            }
            if (functions.isValueNull(lastfourofccnum))
            {
                query.append(" and bd.last_four ='" + ESAPI.encoder().encodeForSQL(me,lastfourofccnum )+ "'");
            }
            if (functions.isValueNull(cardtype))
            {
                query.append(" and bd.bin_brand ='" +ESAPI.encoder().encodeForSQL(me, cardtype )+ "'");
            }
            if (functions.isValueNull(issuing_bank))
            {
                query.append(" and bd.issuing_bank ='" +ESAPI.encoder().encodeForSQL(me, issuing_bank )+ "'");
            }
            if (functions.isValueNull(gateway_name))
            {
                query.append(" and t.fromtype ='" + ESAPI.encoder().encodeForSQL(me,gateway_name )+ "'");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and t.currency ='" + ESAPI.encoder().encodeForSQL(me,currency)+ "'");
            }
            if (functions.isValueNull(paymentId))
            {
                query.append(" and t.paymentid ='" +ESAPI.encoder().encodeForSQL(me,paymentId)+ "'");
            }
            if (functions.isValueNull(customerid))
            {
                query.append(" and t.customerId = '" + customerid + "'");
            }
            logger.debug("accountid--"+accountid);
            logger.debug("accountid Is Null--"+functions.isValueNull(accountid));
            if (functions.isValueNull(accountid))
            {
                query.append(" and t.accountid ='" + ESAPI.encoder().encodeForSQL(me,accountid )+ "'");
            }
            if (functions.isValueNull(fromid))
            {
                query.append(" and t.fromid ='" + ESAPI.encoder().encodeForSQL(me,accountid )+ "'");
            }
            if (functions.isValueNull(name))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.name like '%" +ESAPI.encoder().encodeForSQL(me, name) + "%'");
                }
                else
                {
                    query.append(" and t.name='" +ESAPI.encoder().encodeForSQL(me, name) + "'");
                }
            }
               /* if (functions.isValueNull(fdtstamp)){
                    query.append(" and t.dtstamp >= " + fdtstamp);
                }
                if (functions.isValueNull(tdtstamp)){
                    query.append(" and t.dtstamp <= " + tdtstamp);
                }*/
            if ( functions.isValueNull(fdtstamp))
            {
                long milliSeconds       = Long.parseLong(fdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" and t.transactionTime >= '" + startDate + "'");
            }
            if ( functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and t.transactionTime <= '" + endDate + "'");
            }
            if (functions.isValueNull(desc))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.description like '%" + ESAPI.encoder().encodeForSQL(me, desc) + "%'");
                }
                else
                {
                    query.append(" and t.description='" +ESAPI.encoder().encodeForSQL(me, desc) + "'");
                }
            }
            if (functions.isValueNull(orderdesc))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.orderdescription like '%" +ESAPI.encoder().encodeForSQL(me, orderdesc )+ "%'");
                }
                else
                {
                    query.append(" and t.orderdescription='" +ESAPI.encoder().encodeForSQL(me, orderdesc) + "'");
                }
            }
            if (functions.isValueNull(trackingid))
            {
                    /*if (tableName.equals("transaction_icicicredit") || tableName.equals("transaction_icicicredit_archive"))
                    {
                        query.append(" and t.icicitransid=" +ESAPI.encoder().encodeForSQL(me, trackingid));
                    }
                    else
                    {*/
                //query.append(" and t.trackingid=" +ESAPI.encoder().encodeForSQL(me, trackingid));
                query.append(" and t.trackingid IN (" + trackingid + ")");
                //}
            }
               /* if (iterator.hasNext())
                    query.append(" UNION ");
            }*/
            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");
            query.append(" order by dtstamp DESC,trackingid ");
            logger.debug("export to excel in admin:::::"+query.toString());
            hash            = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            ResultSet rs    = Database.executeQuery(countquery.toString(), conn);
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
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getTransactionDetailsToProcessCommonSettlement", null, "Common", "SQLException while connecting to transaction  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getTransactionDetailsToProcessCommonSettlement()", null, "Common", "SystemError while connecting to transaction  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }
    public Hashtable getListOfTransactionForCompliance(String gateway, String accountid, String currency, String firstsix, String lastfour, int start, int end) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Hashtable hash = null;
        StringBuffer sb=null;
        StringBuffer count=null;
        String tableName = Database.getTableName(gateway);

        try
        {
            con=Database.getConnection();
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            if(tableName.equalsIgnoreCase("transaction_icicicredit"))
            {
                sb=new StringBuffer("select t.icicitransid,t.toid ,b.first_six,b.last_four ,t.accountid,t.status,t.amount,t.emailaddr from "+tableName+" t , bin_details b where t.accountid = b.accountid and t.icicitransid = b.icicitransid and b.first_six = '"+ESAPI.encoder().encodeForSQL(me,firstsix)+"' and b.last_four = '"+ESAPI.encoder().encodeForSQL(me,lastfour)+"' ");
                count=new StringBuffer("select count(*) from "+tableName+" t , bin_details b where t.accountid = b.accountid and t.icicitransid = b.icicitransid and b.first_six = '"+ESAPI.encoder().encodeForSQL(me,firstsix)+"' and b.last_four = '"+ESAPI.encoder().encodeForSQL(me,lastfour)+"' ");
            }
            else
            {
                sb=new StringBuffer("select t.trackingid,t.toid ,b.first_six,b.last_four ,t.accountid,t.status,t.amount,t.emailaddr from "+tableName+" t , bin_details b where t.accountid = b.accountid and t.trackingid = b.icicitransid and b.first_six = '"+ESAPI.encoder().encodeForSQL(me,firstsix)+"' and b.last_four = '"+ESAPI.encoder().encodeForSQL(me,lastfour)+"' ");
                count=new StringBuffer("select count(*) from "+tableName+" t , bin_details b where t.accountid = b.accountid and t.trackingid = b.icicitransid and b.first_six = '"+ESAPI.encoder().encodeForSQL(me,firstsix)+"' and b.last_four = '"+ESAPI.encoder().encodeForSQL(me,lastfour)+"' ");
            }

            if(functions.isValueNull(accountid) && !"0".equals(accountid))
            {
                sb.append(" and b.accountid='"+ESAPI.encoder().encodeForSQL(me,accountid)+"'");
                count.append(" and b.accountid='"+ESAPI.encoder().encodeForSQL(me,accountid)+"'");
            }
            if(functions.isValueNull(gateway))
            {
                sb.append(" and t.fromtype='"+gateway+"'");
                count.append(" and t.fromtype='"+gateway+"'");
            }
            if(functions.isValueNull(currency))
            {
                sb.append(" and t.currency='"+ESAPI.encoder().encodeForSQL(me,currency)+"'");
                count.append(" and t.currency='"+ESAPI.encoder().encodeForSQL(me,currency)+"'");
            }
            sb.append(" order by b.icicitransid desc LIMIT " + start + "," + end);
            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(sb.toString(), con));
            rs = Database.executeQuery(count.toString(), con);
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
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getTransactionDetailsToProcessCommonSettlement", null, "Common", "SQLException while connecting to transaction common  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getTransactionDetailsToProcessCommonSettlement()", null, "Common", "SystemError while connecting to transaction common  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return hash;
    }

    public Hashtable listTransaction(String accountid, String gateway, String currency, String fromDate, String toDate, String startTime, String endTime, String view, int pageno, int records) throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Hashtable<String,String> hash= new Hashtable<String,String>();
        Functions functions = new Functions();
        String tableName = Database.getTableName(gateway);

        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;
        try
        {
            conn = Database.getConnection();
            StringBuffer query = new StringBuffer();
            query.append("SELECT t.trackingid,description,t.accountid, amount, b.RollingReserveAmountKept FROM "+tableName+" t INNER JOIN bin_details b ON t.trackingid=b.icicitransid WHERE STATUS IN ('settled', 'rejected', 'chargeback') AND t.timestamp>'"+fromDate+" "+startTime+"' AND t.timestamp<'"+toDate+" "+endTime+"'  ");
            if(functions.isValueNull(gateway) && !"0".equals(gateway))
            {
                query.append(" AND t.fromtype='"+gateway+"' AND t.currency='"+currency+"'");
            }
            if(functions.isValueNull(accountid) && !"0".equals(accountid))
            {
                query.append(" AND t.accountid='"+accountid+"'");
            }

            StringBuffer query1 = new StringBuffer(query);
            if(("View").equals(view))
                query.append("LIMIT " + start + "," + end);
            ps = conn.prepareStatement(query.toString());
            rs = ps.executeQuery();
            hash = Database.getHashFromResultSetForTransactionEntry(rs);

            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query1 + ") as temp order by  trackingid");
            ps = conn.prepareStatement(countquery.toString());
            rs = ps.executeQuery();
            //rs = Database.executeQuery(countquery.toString(), conn);
            logger.debug("query====="+countquery);

            rs.next();
            int totalrecords = rs.getInt(1);
            hash.put("totalrecords", String.valueOf(totalrecords));
            //hash.put("totalrecords",  totalrecords+"");

            hash.put("records", "0");
            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            logger.debug("query=hash===="+hash);
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "listTransaction", null, "Common", "SQLException while connecting to transaction common  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "listTransaction()", null, "Common", "SystemError while connecting to transaction common  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public Set<String> getAllGatewaysAssociatedWithMerchant(String memberid) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Set<String> gatewaySet = new HashSet<String>();

        try
        {
            con = Database.getConnection();
            String query = "SELECT DISTINCT gateway, currency FROM gateway_accounts AS ga, gateway_type AS gt, member_account_mapping AS ma WHERE ga.accountid=ma.accountid AND ga.pgtypeid=gt.pgtypeid AND memberid='"+memberid+"'";

            ps = con.prepareStatement(query);
            rs = Database.executeQuery(query, con);
            while (rs.next())
            {
                gatewaySet.add(rs.getString("gateway") +"-"+ rs.getString("currency"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getAllGatewaysAssociatedWithMerchant", null, "Common", "SQLException while connecting to transaction common  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getAllGatewaysAssociatedWithMerchant()", null, "Common", "SystemError while connecting to transaction common  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return gatewaySet;
    }

    public Set<String> getAllGatewaysAssociatedWithPartner(String partnerid) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Set<String> gatewaySet = new HashSet<String>();

        try
        {
            con = Database.getConnection();
            String query = "SELECT DISTINCT gateway, gt.currency FROM gateway_accounts AS ga, gateway_type AS gt, member_account_mapping AS ma, members AS m, partners AS p WHERE ga.accountid=ma.accountid AND ga.pgtypeid=gt.pgtypeid AND m.memberid=ma.memberid AND m.partnerId=p.partnerId AND m.partnerId='"+partnerid+"'";

            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next())
            {
                gatewaySet.add(rs.getString("gateway") +"-"+ rs.getString("currency"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getAllGatewaysAssociatedWithMerchant", null, "Common", "SQLException while connecting to transaction common  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getAllGatewaysAssociatedWithMerchant()", null, "Common", "SystemError while connecting to transaction common  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return gatewaySet;
    }

    public Map<String, List<TransactionVO>> getGatewayWiseMerchantTransactionStatus(TransactionVO transactionVO, Set<String> gatewaySet) throws PZDBViolationException
    {
        Connection con = null;
        ResultSet qryResultSet = null;
        ResultSet cQryResultSet = null;
        String totalCount = "";
        String gatewayName[] = null;
        TransactionVO transactionVOData = null;
        List<TransactionVO> transactionVOs = null;
        Map<String, List<TransactionVO>> transactionMap = new HashMap();
        try
        {
            con = Database.getConnection();
            for (String gateway : gatewaySet)
            {
                logger.debug("gateway inside for:::::"+gateway);
                transactionVOs = new ArrayList();
                transactionVO.setGatewayName(functions.getGatewayName(gateway));
                transactionVO.setCurrency(functions.getCurrency(gateway));

                StringBuffer query = new StringBuffer("SELECT STATUS,COUNT(*) AS trans_count,SUM(amount) AS amount, SUM(captureamount) AS captureamount,SUM(refundamount) AS refundamount,SUM(chargebackamount) AS chargebackamount FROM transaction_common WHERE trackingid>0 ");
                StringBuffer countquery = new StringBuffer("SELECT COUNT(*) AS total_count FROM transaction_common WHERE trackingid>0 ");
                if (functions.isValueNull(transactionVO.getStartDate()))
                {
                    query.append(" AND FROM_UNIXTIME(dtstamp) >= '" + transactionVO.getStartDate() + "'");
                    countquery.append(" AND FROM_UNIXTIME(dtstamp) >= '" + transactionVO.getStartDate() + "'");
                }
                if (functions.isValueNull(transactionVO.getEndDate()))
                {
                    query.append(" and FROM_UNIXTIME(dtstamp) <='" + transactionVO.getEndDate() + "'");
                    countquery.append(" and FROM_UNIXTIME(dtstamp) <='" + transactionVO.getEndDate() + "' ");
                }
                if (functions.isValueNull(transactionVO.getToType()))
                {
                    query.append(" and totype ='" + transactionVO.getToType() + "'");
                    countquery.append(" and totype ='" + transactionVO.getToType() + "'");
                }
                if (functions.isValueNull(transactionVO.getAccountId()) && !transactionVO.getAccountId().equals("0"))
                {
                    query.append(" and accountid =" + transactionVO.getAccountId());
                    countquery.append(" and accountid =" + transactionVO.getAccountId());
                }
                if (functions.isValueNull(transactionVO.getGatewayName()))
                {
                    query.append(" and fromtype='" + transactionVO.getGatewayName() + "'");
                    countquery.append(" and fromtype='" + transactionVO.getGatewayName() + "'");
                }
                if (functions.isValueNull(transactionVO.getCurrency()))
                {
                    query.append("  and currency='" + transactionVO.getCurrency() + "'");
                    countquery.append("  and currency='" + transactionVO.getCurrency() + "'");
                }
                if (functions.isValueNull(transactionVO.getStatus()))
                {
                    query.append("  and status='" + transactionVO.getStatus() + "'");
                    countquery.append("  and status='" + transactionVO.getStatus() + "'");
                }
                if (functions.isValueNull(transactionVO.getTerminalId()))
                {
                    query.append("  and terminalid='" + transactionVO.getTerminalId() + "'");
                    countquery.append("  and terminalid='" + transactionVO.getTerminalId() + "'");
                }
                if (functions.isValueNull(transactionVO.getCardTypeId()))
                {
                    query.append("  and cardtypeid='" + transactionVO.getCardTypeId() + "'");
                    countquery.append("  and cardtypeid='" + transactionVO.getCardTypeId() + "'");
                }
                if (functions.isValueNull(transactionVO.getPaymodeid()))
                {
                    query.append("  and paymodeid='" + transactionVO.getPaymodeid() + "'");
                    countquery.append("  and paymodeid='" + transactionVO.getPaymodeid() + "'");
                }
                if (functions.isValueNull(transactionVO.getMemberId()))
                {
                    query.append(" and toid =" + transactionVO.getMemberId());
                    countquery.append(" and toid =" + transactionVO.getMemberId());
                }
                query.append(" GROUP BY STATUS");

                logger.debug("Transaction query====." + query);
                logger.debug("Transaction count query====." + countquery);

                cQryResultSet = Database.executeQuery(countquery.toString(), con);
                if (cQryResultSet.next())
                {
                    totalCount = cQryResultSet.getString("total_count");
                }

                qryResultSet = Database.executeQuery(query.toString(), con);
                while (qryResultSet.next())
                {
                    transactionVOData = new TransactionVO();
                    transactionVOData.setStatus(qryResultSet.getString("STATUS"));
                    transactionVOData.setAmount(qryResultSet.getString("amount"));
                    transactionVOData.setCaptureAmount(qryResultSet.getDouble("captureamount"));
                    transactionVOData.setRefundAmount(qryResultSet.getString("refundamount"));
                    transactionVOData.setChargebackAmount(qryResultSet.getString("chargebackamount"));
                    transactionVOData.setCount(qryResultSet.getInt("trans_count"));
                    transactionVOData.setTotalTransCount(totalCount);
                    transactionVOs.add(transactionVOData);
                }
                if(transactionVOs.size()>0)
                    transactionMap.put(gateway, transactionVOs);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getPartnerTransactionStatus", null, "Common", "SQLException while connecting to transaction common  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getPartnerTransactionStatus()", null, "Common", "SystemError while connecting to transaction common  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionMap;
    }

    public Map<String, List<TransactionVO>> getBankTransactionStatus(TransactionVO transactionVO, Set<String> gatewaySet) throws PZDBViolationException
    {
        Connection con = null;
        ResultSet qryResultSet = null;
        ResultSet cQryResultSet = null;
        String totalCount = "";
        ResultSet rs=null;
        ResultSet rs2=null;
        String total = "";
        String refundtotal = "";
        String gatewayName[] = null;
        TransactionVO transactionVOData = null;
        List<TransactionVO> transactionVOs = new ArrayList<>();
        Map<String, List<TransactionVO>> transactionMap = new HashMap();
        try
        {
            con = Database.getConnection();
            /*for (String gateway : gatewaySet)
            {*/
                /*logger.debug("gateway inside for:::::"+gateway);
                transactionVOs = new ArrayList();
                transactionVO.setGatewayName(functions.getGatewayName(gateway));
                transactionVO.setCurrency(functions.getCurrency(gateway));*/

            StringBuffer query = new StringBuffer("SELECT STATUS,COUNT(*) AS trans_count,SUM(amount) AS amount, SUM(captureamount) AS captureamount,SUM(refundamount) AS refundamount,SUM(chargebackamount) AS chargebackamount FROM transaction_common WHERE trackingid>0 ");
            StringBuffer countquery = new StringBuffer("SELECT COUNT(*) AS total_count FROM transaction_common WHERE trackingid>0 ");
            if (functions.isValueNull(transactionVO.getStartDate()))
            {
                query.append(" AND FROM_UNIXTIME(dtstamp) >= '" + transactionVO.getStartDate() + "'");
                countquery.append(" AND FROM_UNIXTIME(dtstamp) >= '" + transactionVO.getStartDate() + "'");
            }
            if (functions.isValueNull(transactionVO.getEndDate()))
            {
                query.append(" and FROM_UNIXTIME(dtstamp) <='" + transactionVO.getEndDate() + "'");
                countquery.append(" and FROM_UNIXTIME(dtstamp) <='" + transactionVO.getEndDate() + "' ");
            }
            if (functions.isValueNull(transactionVO.getToType()))
            {
                query.append(" and totype ='" + transactionVO.getToType() + "'");
                countquery.append(" and totype ='" + transactionVO.getToType() + "'");
            }
            if (functions.isValueNull(transactionVO.getAccountId()) && !transactionVO.getAccountId().equals("0"))
            {
                query.append(" and accountid =" + transactionVO.getAccountId());
                countquery.append(" and accountid =" + transactionVO.getAccountId());
            }
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                query.append(" and fromtype='" + transactionVO.getGatewayName() + "'");
                countquery.append(" and fromtype='" + transactionVO.getGatewayName() + "'");
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                query.append("  and currency='" + transactionVO.getCurrency() + "'");
                countquery.append("  and currency='" + transactionVO.getCurrency() + "'");
            }
            if (functions.isValueNull(transactionVO.getStatus()))
            {
                query.append("  and status='" + transactionVO.getStatus() + "'");
                countquery.append("  and status='" + transactionVO.getStatus() + "'");
            }
            if (functions.isValueNull(transactionVO.getTerminalId()))
            {
                query.append("  and terminalid='" + transactionVO.getTerminalId() + "'");
                countquery.append("  and terminalid='" + transactionVO.getTerminalId() + "'");
            }
            if (functions.isValueNull(transactionVO.getCardTypeId()))
            {
                query.append("  and cardtypeid='" + transactionVO.getCardTypeId() + "'");
                countquery.append("  and cardtypeid='" + transactionVO.getCardTypeId() + "'");
            }
            if (functions.isValueNull(transactionVO.getPaymodeid()))
            {
                query.append("  and paymodeid='" + transactionVO.getPaymodeid() + "'");
                countquery.append("  and paymodeid='" + transactionVO.getPaymodeid() + "'");
            }
            if (functions.isValueNull(transactionVO.getMemberId()))
            {
                query.append(" and toid =" + transactionVO.getMemberId());
                countquery.append(" and toid =" + transactionVO.getMemberId());
            }
            query.append(" GROUP BY STATUS");

            logger.error("Transaction query====" + query);

            cQryResultSet = Database.executeQuery(countquery.toString(), con);
            if (cQryResultSet.next())
            {
                totalCount = cQryResultSet.getString("total_count");
            }

            qryResultSet = Database.executeQuery(query.toString(), con);
            while (qryResultSet.next())
            {
                transactionVOData = new TransactionVO();
                transactionVOData.setStatus(qryResultSet.getString("STATUS"));
                transactionVOData.setAmount(qryResultSet.getString("amount"));
                transactionVOData.setCaptureAmount(qryResultSet.getDouble("captureamount"));
                transactionVOData.setRefundAmount(qryResultSet.getString("refundamount"));
                transactionVOData.setChargebackAmount(qryResultSet.getString("chargebackamount"));
                transactionVOData.setCount(qryResultSet.getInt("trans_count"));
                transactionVOData.setTotalTransCount(totalCount);

                transactionVOs.add(transactionVOData);
            }
            if(transactionVOs.size()>0)
                transactionMap.put("transactionStatus", transactionVOs);
            System.out.println("transactionMap:"+transactionMap);
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getPartnerTransactionStatus", null, "Common", "SQLException while connecting to transaction common  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getPartnerTransactionStatus()", null, "Common", "SystemError while connecting to transaction common  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionMap;
    }

    public List<TransactionVO> getAccountWiseTransactionDetails(TransactionVO transactionVO) throws PZDBViolationException
    {
        Connection con = null;
        ResultSet qryResultSet = null;
        ResultSet cQryResultSet = null;
        ResultSet cbRefundQryResultSet = null;
        String totalCount = "";
        ResultSet rs=null;
        ResultSet rs2=null;
        String total = "";
        String refundtotal = "";
        String gatewayName[] = null;
        List<String> accountList=new ArrayList<>();
        HashMap<String,HashMap<String,String>> hashMap=new HashMap<>();
        TransactionVO transactionVOData = null;
        List<TransactionVO> transactionVOs = new ArrayList<>();
        double totalSalesAmount=0.00;
        double approveCount=0;
        double approveAmount=0.00;
        double refundCount=0;
        double refundAmount=0.00;
        double markforreversalCount=0;
        double markforreversalAmount=0.00;
        double chargebackCount=0;
        double chargebackAmount=0.00;

        double authstartedCount=0;
        double authstartedAmount=0;
        double authfailedCount=0;
        double authfailedAmount=0;
        double failedCount=0;
        double failedAmount=0.00;
        double payoutCount=0;
        double payoutAmount=0.00;
        double begunCount=0;
        double begunAmount=0.00;
        try
        {
            con = Database.getConnection();
            StringBuffer query = new StringBuffer("SELECT accountid,cardtype,STATUS,currency,COUNT(*) AS trans_count,SUM(amount) AS amount, SUM(captureamount) AS captureamount,SUM(refundamount) AS refundamount,SUM(chargebackamount) AS chargebackamount FROM transaction_common WHERE trackingid>0 ");
            StringBuffer chargebackRefundQuery = new StringBuffer("SELECT accountid,cardtype,STATUS,currency,COUNT(*) AS trans_count,SUM(refundamount) AS refundamount,SUM(chargebackamount) AS chargebackamount FROM transaction_common WHERE trackingid>0 ");
            StringBuffer countquery = new StringBuffer("SELECT accountid,cardtype,COUNT(*) AS total_count FROM transaction_common WHERE trackingid>0 ");
            if (functions.isValueNull(transactionVO.getStartDate()))
            {
                query.append(" AND FROM_UNIXTIME(dtstamp) >= '" + transactionVO.getStartDate() + "'");
                countquery.append(" AND FROM_UNIXTIME(dtstamp) >= '" + transactionVO.getStartDate() + "'");
                chargebackRefundQuery.append(" AND timestamp >= '" + transactionVO.getStartDate() + "'");
            }
            if (functions.isValueNull(transactionVO.getEndDate()))
            {
                query.append(" and FROM_UNIXTIME(dtstamp) <='" + transactionVO.getEndDate() + "'");
                countquery.append(" and FROM_UNIXTIME(dtstamp) <='" + transactionVO.getEndDate() + "' ");
                chargebackRefundQuery.append(" and timestamp <='" + transactionVO.getEndDate() + "' ");
            }
            if (functions.isValueNull(transactionVO.getToType()))
            {
                query.append(" and totype ='" + transactionVO.getToType() + "'");
                countquery.append(" and totype ='" + transactionVO.getToType() + "'");
                chargebackRefundQuery.append(" and totype ='" + transactionVO.getToType() + "'");
            }
            if (functions.isValueNull(transactionVO.getAccountId()) && !transactionVO.getAccountId().equals("0"))
            {
                query.append(" and accountid =" + transactionVO.getAccountId());
                countquery.append(" and accountid =" + transactionVO.getAccountId());
                chargebackRefundQuery.append(" and accountid =" + transactionVO.getAccountId());
            }
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                query.append(" and fromtype='" + transactionVO.getGatewayName() + "'");
                countquery.append(" and fromtype='" + transactionVO.getGatewayName() + "'");
                chargebackRefundQuery.append(" and fromtype='" + transactionVO.getGatewayName() + "'");
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                query.append("  and currency='" + transactionVO.getCurrency() + "'");
                countquery.append("  and currency='" + transactionVO.getCurrency() + "'");
                chargebackRefundQuery.append("  and currency='" + transactionVO.getCurrency() + "'");
            }
            if (functions.isValueNull(transactionVO.getStatus()))
            {
                query.append("  and status='" + transactionVO.getStatus() + "'");
                countquery.append("  and status='" + transactionVO.getStatus() + "'");
                chargebackRefundQuery.append("  and status='" + transactionVO.getStatus() + "'");
            }
            if (functions.isValueNull(transactionVO.getTerminalId()))
            {
                query.append("  and terminalid='" + transactionVO.getTerminalId() + "'");
                countquery.append("  and terminalid='" + transactionVO.getTerminalId() + "'");
                chargebackRefundQuery.append("  and terminalid='" + transactionVO.getTerminalId() + "'");
            }
            if (functions.isValueNull(transactionVO.getMemberId()))
            {
                query.append(" and toid =" + transactionVO.getMemberId());
                countquery.append(" and toid =" + transactionVO.getMemberId());
                chargebackRefundQuery.append(" and toid =" + transactionVO.getMemberId());
            }
            chargebackRefundQuery.append(" and status in('reversed','chargeback','markedforreversal','chargebackreversed')");
            query.append(" GROUP BY accountid,cardtype,STATUS");
            countquery.append(" GROUP BY accountid,cardtype");
            chargebackRefundQuery.append(" GROUP BY accountid,cardtype,STATUS");

            logger.error("getAccountWiseTransactionDetails query====" + query);

            /*cQryResultSet = Database.executeQuery(countquery.toString(), con);
            if (cQryResultSet.next())
            {
                totalCount = cQryResultSet.getString("total_count");
            }*/

            cQryResultSet = Database.executeQuery(countquery.toString(), con);
            while (cQryResultSet.next())
            {
                logger.error("cardtype 1 while"+cQryResultSet.getString("cardtype"));
                if(!accountList.contains(cQryResultSet.getString("accountid")+"-"+cQryResultSet.getString("cardtype")))
                {
                    totalCount="";
                    accountList.add(cQryResultSet.getString("accountid")+"-"+cQryResultSet.getString("cardtype"));
                }
                totalCount = cQryResultSet.getString("total_count");
                HashMap<String,String> innerMap=new HashMap<>();
                innerMap.put("totalCount", totalCount);
                hashMap.put(cQryResultSet.getString("accountid")+"-"+cQryResultSet.getString("cardtype"), innerMap);
            }

            qryResultSet = Database.executeQuery(query.toString(), con);
            cbRefundQryResultSet=Database.executeQuery(chargebackRefundQuery.toString(),con);
            accountList=new ArrayList<>();
            while (qryResultSet.next())
            {
                if(!accountList.contains(qryResultSet.getString("accountid")+"-"+qryResultSet.getString("cardtype")))
                {
                    approveCount = 0.00;
                    approveAmount = 0.00;
                    failedCount = 0.00;
                    failedAmount = 0.00;
                    totalSalesAmount = 0.00;
                    authstartedCount=0.00;
                    authstartedAmount=0.00;
                    authfailedCount=0.00;
                    authfailedAmount=0.00;
                    payoutCount=0.00;
                    payoutAmount=0.00;
                    begunCount=0.00;
                    begunAmount=0.00;
                    accountList.add(qryResultSet.getString("accountid")+"-"+qryResultSet.getString("cardtype"));
                }
                HashMap<String,String> innerMap=hashMap.get(qryResultSet.getString("accountid")+"-"+qryResultSet.getString("cardtype"));
                if(innerMap==null)
                    innerMap=new HashMap<>();
                if(innerMap.containsKey("totalSalesAmount")){
                    totalSalesAmount= Double.parseDouble(innerMap.get("totalSalesAmount"));
                }
                if(innerMap.containsKey("approveCount")){
                    approveCount= Double.parseDouble(innerMap.get("approveCount"));
                }else
                    approveCount=0.0;
                if(innerMap.containsKey("approveAmount")){
                    approveAmount= Double.parseDouble(innerMap.get("approveAmount"));
                }else
                    approveAmount=0.0;
                if(innerMap.containsKey("authstartedCount")){
                    authstartedCount= Double.parseDouble(innerMap.get("authstartedCount"));
                }else
                    authstartedCount=0.0;
                if(innerMap.containsKey("authstartedAmount")){
                    authstartedAmount= Double.parseDouble(innerMap.get("authstartedAmount"));
                }else
                    authstartedAmount=0.0;
                if(innerMap.containsKey("authfailedCount")){
                    authfailedCount= Double.parseDouble(innerMap.get("authfailedCount"));
                }else
                    authfailedCount=0.0;
                if(innerMap.containsKey("authfailedAmount")){
                    authfailedAmount= Double.parseDouble(innerMap.get("authfailedAmount"));
                }else
                    authfailedAmount=0.0;
                if(innerMap.containsKey("payoutCount")){
                    payoutCount= Double.parseDouble(innerMap.get("payoutCount"));
                }else
                    payoutCount=0.0;
                if(innerMap.containsKey("payoutAmount")){
                    payoutAmount= Double.parseDouble(innerMap.get("payoutAmount"));
                }else
                    payoutAmount=0.0;

                if(innerMap.containsKey("begunCount")){
                    begunCount= Double.parseDouble(innerMap.get("begunCount"));
                }else
                    begunCount=0.0;
                if(innerMap.containsKey("begunAmount")){
                    begunAmount= Double.parseDouble(innerMap.get("begunAmount"));
                }else
                    begunAmount=0.0;
                if(innerMap.containsKey("failedCount")){
                    failedCount= Double.parseDouble(innerMap.get("failedCount"));
                }else
                    failedCount=0.0;
                if(innerMap.containsKey("failedAmount")){
                    failedAmount= Double.parseDouble(innerMap.get("failedAmount"));
                }else
                    failedAmount=0.0;
                transactionVO.setAccountId(qryResultSet.getString("accountid"));
                transactionVO.setCurrency(qryResultSet.getString("currency"));
                if(functions.isValueNull(qryResultSet.getString("STATUS")))
                {
                    if(qryResultSet.getString("STATUS").equals("capturesuccess") || qryResultSet.getString("STATUS").equals("reversed") || qryResultSet.getString("STATUS").equals("chargeback") || qryResultSet.getString("STATUS").equals("settled") || qryResultSet.getString("STATUS").equals("authsuccessful"))
                    {
                        approveCount=approveCount+qryResultSet.getInt("trans_count");
                        approveAmount=approveAmount+qryResultSet.getDouble("amount");
                        totalSalesAmount= totalSalesAmount+Double.parseDouble(qryResultSet.getString("amount"));
                    }
                    else if(qryResultSet.getString("STATUS").equals("authstarted")) {
                        authstartedCount=qryResultSet.getInt("trans_count");
                        authstartedAmount=qryResultSet.getDouble("amount");
                        totalSalesAmount= totalSalesAmount+Double.parseDouble(qryResultSet.getString("amount"));
                    }
                    else if(qryResultSet.getString("STATUS").equals("authfailed")) {
                        authfailedCount=qryResultSet.getInt("trans_count");
                        authfailedAmount=qryResultSet.getDouble("amount");
                        totalSalesAmount= totalSalesAmount+Double.parseDouble(qryResultSet.getString("amount"));
                    }
                    else if(qryResultSet.getString("STATUS").equals("payoutsuccessful")) {
                        payoutCount=qryResultSet.getInt("trans_count");
                        payoutAmount=qryResultSet.getDouble("amount");
                        totalSalesAmount= totalSalesAmount+Double.parseDouble(qryResultSet.getString("amount"));
                    }
                    else if(qryResultSet.getString("STATUS").equals("begun")) {
                        begunCount=qryResultSet.getInt("trans_count");
                        begunAmount=qryResultSet.getDouble("amount");
                        totalSalesAmount= totalSalesAmount+Double.parseDouble(qryResultSet.getString("amount"));
                    }
                    else if(qryResultSet.getString("STATUS").equals("failed")) {
                        failedCount=qryResultSet.getInt("trans_count");
                        failedAmount=qryResultSet.getDouble("amount");
                        totalSalesAmount= totalSalesAmount+Double.parseDouble(qryResultSet.getString("amount"));
                    }
                    else {
                        totalSalesAmount= totalSalesAmount+Double.parseDouble(qryResultSet.getString("amount"));
                    }
                }
                innerMap.put("approveCount",String.valueOf(approveCount));
                innerMap.put("approveAmount",String.valueOf(approveAmount));
                innerMap.put("authstartedCount",String.valueOf(authstartedCount));
                innerMap.put("authstartedAmount",String.valueOf(authstartedAmount));
                innerMap.put("authfailedCount",String.valueOf(authfailedCount));
                innerMap.put("authfailedAmount",String.valueOf(authfailedAmount));
                innerMap.put("payoutCount",String.valueOf(payoutCount));
                innerMap.put("payoutAmount",String.valueOf(payoutAmount));
                innerMap.put("begunCount",String.valueOf(begunCount));
                innerMap.put("begunAmount",String.valueOf(begunAmount));
                innerMap.put("failedCount",String.valueOf(failedCount));
                innerMap.put("failedAmount",String.valueOf(failedAmount));
                innerMap.put("totalSalesAmount", String.valueOf(totalSalesAmount));
                logger.error("cardtype 2 while"+qryResultSet.getString("cardtype"));
                hashMap.put(qryResultSet.getString("accountid")+"-"+qryResultSet.getString("cardtype"),innerMap);
            }
            accountList=new ArrayList<>();
            while (cbRefundQryResultSet.next()){
                if(!accountList.contains(cbRefundQryResultSet.getString("accountid")+"-"+cbRefundQryResultSet.getString("cardtype")))
                {
                    refundCount = 0.00;
                    refundAmount = 0.00;
                    chargebackCount = 0.00;
                    chargebackAmount = 0.00;
                    markforreversalCount=0.00;
                    markforreversalAmount=0.00;
                    accountList.add(cbRefundQryResultSet.getString("accountid")+"-"+cbRefundQryResultSet.getString("cardtype"));
                }
                HashMap<String,String> innerMap=hashMap.get(cbRefundQryResultSet.getString("accountid")+"-"+cbRefundQryResultSet.getString("cardtype"));
                if(innerMap==null)
                    innerMap=new HashMap<>();
                if(innerMap.containsKey("refundCount")){
                    refundCount= Double.parseDouble(innerMap.get("refundCount"));
                }else
                    refundCount=0.0;
                if(innerMap.containsKey("refundAmount")){
                    refundAmount= Double.parseDouble(innerMap.get("refundAmount"));
                }else
                    refundAmount=0.0;
                if(innerMap.containsKey("chargebackCount")){
                    chargebackCount= Double.parseDouble(innerMap.get("chargebackCount"));
                }else
                    chargebackCount=0.0;
                if(innerMap.containsKey("chargebackAmount")){
                    chargebackAmount= Double.parseDouble(innerMap.get("chargebackAmount"));
                }else
                    chargebackAmount=0.0;
                if(innerMap.containsKey("markforreversalCount")){
                    markforreversalCount= Double.parseDouble(innerMap.get("markforreversalCount"));
                }else
                    markforreversalCount=0.0;
                if(innerMap.containsKey("markforreversalAmount")){
                    markforreversalAmount= Double.parseDouble(innerMap.get("markforreversalAmount"));
                }else
                    markforreversalAmount=0.0;
                if(cbRefundQryResultSet.getString("STATUS").equals("reversed"))
                {
                    refundCount=/*refundCount+*/cbRefundQryResultSet.getInt("trans_count");
                    refundAmount= /*refundAmount+*/Double.parseDouble(cbRefundQryResultSet.getString("refundamount"));
                }
                if(cbRefundQryResultSet.getString("STATUS").equals("markedforreversal"))
                {
                    markforreversalCount=/*markforreversalCount+*/cbRefundQryResultSet.getInt("trans_count");
                    markforreversalAmount= /*markforreversalAmount+*/Double.parseDouble(cbRefundQryResultSet.getString("refundamount"));
                }
                if(cbRefundQryResultSet.getString("STATUS").equals("chargeback"))
                {
                    chargebackCount=/*chargebackCount+*/cbRefundQryResultSet.getInt("trans_count");
                    chargebackAmount= /*chargebackAmount+*/Double.parseDouble(cbRefundQryResultSet.getString("chargebackamount"));
                }
                innerMap.put("refundCount",String.valueOf(refundCount));
                innerMap.put("refundAmount",String.valueOf(refundAmount));
                innerMap.put("chargebackCount",String.valueOf(chargebackCount));
                innerMap.put("chargebackAmount",String.valueOf(chargebackAmount));
                innerMap.put("markforreversalCount",String.valueOf(markforreversalCount));
                innerMap.put("markforreversalAmount", String.valueOf(markforreversalAmount));
                logger.error("cardtype 3 while"+cbRefundQryResultSet.getString("cardtype"));
                hashMap.put(cbRefundQryResultSet.getString("accountid")+"-"+cbRefundQryResultSet.getString("cardtype"),innerMap);
            }
            Set<String> keySet=hashMap.keySet();
            logger.error("hashMap---->"+hashMap);
            for(String key:keySet)
            {
                HashMap<String,String> innerMap=hashMap.get(key);
                String accountId=key.split("-")[0];
                String cardType=key.split("-")[1];
                // TransactionVO transactionVO1=getListOfAccountId.get(accountId);
                transactionVOData=new TransactionVO();
                if(functions.isValueNull(innerMap.get("totalCount")))
                    transactionVOData.setTotalTransCount(innerMap.get("totalCount"));
                else
                    transactionVOData.setTotalTransCount("0");
                transactionVOData.setAccountId(accountId);
                transactionVOData.setCardTypeId(cardType);
                transactionVOData.setCurrency(transactionVO.getCurrency());
                if(functions.isValueNull(innerMap.get("totalSalesAmount")))
                    transactionVOData.setAmount(innerMap.get("totalSalesAmount"));
                else
                    transactionVOData.setAmount("0.0");

                if(functions.isValueNull(innerMap.get("approveCount")))
                    transactionVOData.setCaptureCount(innerMap.get("approveCount"));
                else
                    transactionVOData.setCaptureCount("0.0");

                if(functions.isValueNull(innerMap.get("approveAmount")))
                    transactionVOData.setCaptureAmount(Double.parseDouble(innerMap.get("approveAmount")));
                else
                    transactionVOData.setCaptureAmount(0.0);

                if(functions.isValueNull(innerMap.get("refundCount")))
                    transactionVOData.setRefundcount(innerMap.get("refundCount"));
                else
                    transactionVOData.setRefundcount("0.0");

                if(functions.isValueNull(innerMap.get("refundAmount")))
                    transactionVOData.setReverseAmount(innerMap.get("refundAmount"));
                else
                    transactionVOData.setReverseAmount("0.0");

                if(functions.isValueNull(innerMap.get("chargebackCount")))
                    transactionVOData.setChargebackCount(innerMap.get("chargebackCount"));
                else
                    transactionVOData.setChargebackCount("0.0");

                if(functions.isValueNull(innerMap.get("chargebackAmount")))
                    transactionVOData.setChargebackAmount(innerMap.get("chargebackAmount"));
                else
                    transactionVOData.setChargebackAmount("0.0");

                if(functions.isValueNull(innerMap.get("authfailedCount")))
                    transactionVOData.setDeclineCount(innerMap.get("authfailedCount"));
                else
                    transactionVOData.setDeclineCount("0.0");

                if(functions.isValueNull(innerMap.get("authfailedAmount")))
                    transactionVOData.setDeclineAmount(innerMap.get("authfailedAmount"));
                else
                    transactionVOData.setDeclineAmount("0.0");

                if(functions.isValueNull(innerMap.get("authstartedCount")))
                    transactionVOData.setAuthstartedCount(innerMap.get("authstartedCount"));
                else
                    transactionVOData.setAuthstartedCount("0.0");

                if(functions.isValueNull(innerMap.get("authstartedAmount")))
                    transactionVOData.setAuthstartedAmount(innerMap.get("authstartedAmount"));
                else
                    transactionVOData.setAuthstartedAmount("0.0");

                if(functions.isValueNull(innerMap.get("payoutCount")))
                    transactionVOData.setPayoutCount(innerMap.get("payoutCount"));
                else
                    transactionVOData.setPayoutCount("0.0");

                if(functions.isValueNull(innerMap.get("payoutAmount")))
                    transactionVOData.setPayoutAmount(innerMap.get("payoutAmount"));
                else
                    transactionVOData.setPayoutAmount("0.0");

                if(functions.isValueNull(innerMap.get("begunCount")))
                    transactionVOData.setBegunCount(innerMap.get("begunCount"));
                else
                    transactionVOData.setBegunCount("0.0");

                if(functions.isValueNull(innerMap.get("begunAmount")))
                    transactionVOData.setBegunAmount(innerMap.get("begunAmount"));
                else
                    transactionVOData.setBegunAmount("0.0");

                if(functions.isValueNull(innerMap.get("failedCount")))
                    transactionVOData.setFailedCount(innerMap.get("failedCount"));
                else
                    transactionVOData.setFailedCount("0.0");

                if(functions.isValueNull(innerMap.get("failedAmount")))
                    transactionVOData.setFailedAmount(innerMap.get("failedAmount"));
                else
                    transactionVOData.setFailedAmount("0.0");

                if(functions.isValueNull(innerMap.get("markforreversalCount")))
                    transactionVOData.setMarkforreversalCount(innerMap.get("markforreversalCount"));
                else
                    transactionVOData.setMarkforreversalCount("0.0");

                if(functions.isValueNull(innerMap.get("markforreversalAmount")))
                    transactionVOData.setMarkforreversalAmount(innerMap.get("markforreversalAmount"));
                else
                    transactionVOData.setMarkforreversalAmount("0.0");

                transactionVOs.add(transactionVOData);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getPartnerTransactionStatus", null, "Common", "SQLException while connecting to transaction common  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getPartnerTransactionStatus()", null, "Common", "SystemError while connecting to transaction common  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionVOs;
    }

    public List<TransactionVO> getSalesReportMap(TransactionVO transactionVO,HashMap<String,TransactionVO> getListOfMemberId) throws PZDBViolationException
    {
        Connection con = null;
        ResultSet qryResultSet = null;
        ResultSet cQryResultSet = null;
        ResultSet cbRefundQryResultSet = null;
        String totalCount = "";
        TransactionVO transactionVOData = new TransactionVO();
        List<TransactionVO> transactionVOs = new ArrayList<>();
        HashMap<String,HashMap<String,String>> hashMap=new HashMap<>();
        List<String> memberList=new ArrayList<>();
        String memberIdList="";
        double totalSalesAmount=0.00;
        double approveCount=0;
        double approveAmount=0.00;
        double refundCount=0;
        double refundAmount=0.00;
        double markforreversalCount=0;
        double markforreversalAmount=0.00;
        double chargebackCount=0;
        double chargebackAmount=0.00;

        double authstartedCount=0;
        double authstartedAmount=0;
        double authfailedCount=0;
        double authfailedAmount=0;
        double failedCount=0;
        double failedAmount=0.00;
        double payoutCount=0;
        double payoutAmount=0.00;
        double begunCount=0;
        double begunAmount=0.00;

        try
        {
            Set<String> set=getListOfMemberId.keySet();
            logger.error("Set---"+set);
            int i=0;
            for (String memberId:set)
            {
                if(i==(set.size()-1))
                    memberIdList=memberIdList+memberId;
                else
                    memberIdList=memberIdList+memberId+",";
                i++;
            }
            con = Database.getConnection();
            StringBuffer query = new StringBuffer("SELECT STATUS,toid,currency,ct.cardType,COUNT(*) AS trans_count,SUM(amount) AS amount,SUM(captureamount) AS captureamount,SUM(refundamount) AS refundamount,SUM(chargebackamount) AS chargebackamount FROM transaction_common JOIN card_type AS ct WHERE ct.cardtypeid=transaction_common.`cardtypeid` AND trackingid>0");
            StringBuffer chargebackRefundQuery = new StringBuffer("SELECT STATUS,toid,currency,ct.cardType,COUNT(*) AS trans_count,SUM(refundamount) AS refundamount,SUM(chargebackamount) AS chargebackamount FROM transaction_common JOIN card_type AS ct WHERE ct.cardtypeid=transaction_common.`cardtypeid` AND trackingid>0 ");
            StringBuffer countquery = new StringBuffer("SELECT toid,COUNT(*) AS total_count,ct.cardType FROM transaction_common JOIN card_type AS ct WHERE ct.cardtypeid=transaction_common.`cardtypeid` AND trackingid>0 ");
            if (functions.isValueNull(transactionVO.getStartDate()))
            {
                query.append(" AND FROM_UNIXTIME(dtstamp) >= '" + transactionVO.getStartDate() + "'");
                countquery.append(" AND FROM_UNIXTIME(dtstamp) >= '" + transactionVO.getStartDate() + "'");
                chargebackRefundQuery.append(" AND timestamp >= '" + transactionVO.getStartDate() + "'");
            }
            if (functions.isValueNull(transactionVO.getEndDate()))
            {
                query.append(" and FROM_UNIXTIME(dtstamp) <='" + transactionVO.getEndDate() + "'");
                countquery.append(" and FROM_UNIXTIME(dtstamp) <='" + transactionVO.getEndDate() + "' ");
                chargebackRefundQuery.append(" and timestamp <='" + transactionVO.getEndDate() + "' ");
            }
            if (functions.isValueNull(transactionVO.getToType()))
            {
                query.append(" and totype ='" + transactionVO.getToType() + "'");
                countquery.append(" and totype ='" + transactionVO.getToType() + "'");
                chargebackRefundQuery.append(" and totype ='" + transactionVO.getToType() + "'");
            }
            if (functions.isValueNull(transactionVO.getAccountId()) && !transactionVO.getAccountId().equals("0"))
            {
                query.append(" and accountid =" + transactionVO.getAccountId());
                countquery.append(" and accountid =" + transactionVO.getAccountId());
                chargebackRefundQuery.append(" and accountid =" + transactionVO.getAccountId());
            }
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                query.append(" and fromtype='" + transactionVO.getGatewayName() + "'");
                countquery.append(" and fromtype='" + transactionVO.getGatewayName() + "'");
                chargebackRefundQuery.append(" and fromtype='" + transactionVO.getGatewayName() + "'");
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                query.append("  and currency='" + transactionVO.getCurrency() + "'");
                countquery.append("  and currency='" + transactionVO.getCurrency() + "'");
                chargebackRefundQuery.append("  and currency='" + transactionVO.getCurrency() + "'");
            }
            if (functions.isValueNull(transactionVO.getStatus()))
            {
                query.append("  and status='" + transactionVO.getStatus() + "'");
                countquery.append("  and status='" + transactionVO.getStatus() + "'");
                chargebackRefundQuery.append("  and status='" + transactionVO.getStatus() + "'");
            }
            if (functions.isValueNull(transactionVO.getTerminalId()))
            {
                query.append("  and terminalid='" + transactionVO.getTerminalId() + "'");
                countquery.append("  and terminalid='" + transactionVO.getTerminalId() + "'");
                chargebackRefundQuery.append("  and terminalid='" + transactionVO.getTerminalId() + "'");
            }
            if (functions.isValueNull(transactionVO.getCardTypeId()))
            {
                query.append("  and cardtypeid='" + transactionVO.getCardTypeId() + "'");
                countquery.append("  and cardtypeid='" + transactionVO.getCardTypeId() + "'");
                chargebackRefundQuery.append("  and cardtypeid='" + transactionVO.getCardTypeId() + "'");
            }
            if (functions.isValueNull(transactionVO.getPaymodeid()))
            {
                query.append("  and paymodeid='" + transactionVO.getPaymodeid() + "'");
                countquery.append("  and paymodeid='" + transactionVO.getPaymodeid() + "'");
                chargebackRefundQuery.append("  and paymodeid='" + transactionVO.getPaymodeid() + "'");
            }
            if (functions.isValueNull(memberIdList))
            {
                query.append(" and toid in (" + memberIdList+")");
                countquery.append(" and toid in (" + memberIdList+")");
                chargebackRefundQuery.append(" and toid in (" + memberIdList+")");
            }
            chargebackRefundQuery.append(" and status in('reversed','chargeback','markedforreversal','chargebackreversed')");
            query.append(" GROUP BY ct.cardType,toid,STATUS ORDER BY toid");
            countquery.append(" GROUP BY ct.cardType,toid ORDER BY toid");
            chargebackRefundQuery.append(" GROUP BY ct.cardType,toid,STATUS ORDER BY toid");

            logger.error("Sales Transaction query====" + query);
            logger.error("Sales Transaction countquery====" + countquery);
            logger.error("Sales Transaction chargebackRefundQuery====" + chargebackRefundQuery);

            cQryResultSet = Database.executeQuery(countquery.toString(), con);
            while (cQryResultSet.next())
            {
                if(!memberList.contains(cQryResultSet.getString("toid")+"-"+cQryResultSet.getString("cardType")))
                {
                    totalCount="";
                    memberList.add(cQryResultSet.getString("toid")+"-"+cQryResultSet.getString("cardType"));
                }
                totalCount = cQryResultSet.getString("total_count");
                HashMap<String,String> innerMap=new HashMap<>();
                innerMap.put("totalCount",totalCount);
                hashMap.put(cQryResultSet.getString("toid")+"-"+cQryResultSet.getString("cardType"),innerMap);
            }

            qryResultSet = Database.executeQuery(query.toString(), con);
            cbRefundQryResultSet=Database.executeQuery(chargebackRefundQuery.toString(),con);
            memberList=new ArrayList<>();
            while (qryResultSet.next())
            {
                if(!memberList.contains(qryResultSet.getString("toid")+"-"+qryResultSet.getString("cardType")))
                {
                    approveCount = 0.00;
                    approveAmount = 0.00;
                    failedCount = 0.00;
                    failedAmount = 0.00;
                    totalSalesAmount = 0.00;
                    authstartedCount=0.00;
                    authstartedAmount=0.00;
                    authfailedCount=0.00;
                    authfailedAmount=0.00;
                    payoutCount=0.00;
                    payoutAmount=0.00;
                    begunCount=0.00;
                    begunAmount=0.00;
                    memberList.add(qryResultSet.getString("toid")+"-"+qryResultSet.getString("cardType"));
                }
                HashMap<String,String> innerMap=hashMap.get(qryResultSet.getString("toid")+"-"+qryResultSet.getString("cardType"));
                if(innerMap==null)
                    innerMap=new HashMap<>();
                if(innerMap.containsKey("totalSalesAmount")){
                    totalSalesAmount= Double.parseDouble(innerMap.get("totalSalesAmount"));
                }
                if(innerMap.containsKey("approveCount")){
                    approveCount= Double.parseDouble(innerMap.get("approveCount"));
                }else
                    approveCount=0.0;
                if(innerMap.containsKey("approveAmount")){
                    approveAmount= Double.parseDouble(innerMap.get("approveAmount"));
                }else
                    approveAmount=0.0;
                if(innerMap.containsKey("authstartedCount")){
                    authstartedCount= Double.parseDouble(innerMap.get("authstartedCount"));
                }else
                    authstartedCount=0.0;
                if(innerMap.containsKey("authstartedAmount")){
                    authstartedAmount= Double.parseDouble(innerMap.get("authstartedAmount"));
                }else
                    authstartedAmount=0.0;
                if(innerMap.containsKey("authfailedCount")){
                    authfailedCount= Double.parseDouble(innerMap.get("authfailedCount"));
                }else
                    authfailedCount=0.0;
                if(innerMap.containsKey("authfailedAmount")){
                    authfailedAmount= Double.parseDouble(innerMap.get("authfailedAmount"));
                }else
                    authfailedAmount=0.0;
                if(innerMap.containsKey("payoutCount")){
                    payoutCount= Double.parseDouble(innerMap.get("payoutCount"));
                }else
                    payoutCount=0.0;
                if(innerMap.containsKey("payoutAmount")){
                    payoutAmount= Double.parseDouble(innerMap.get("payoutAmount"));
                }else
                    payoutAmount=0.0;

                if(innerMap.containsKey("begunCount")){
                    begunCount= Double.parseDouble(innerMap.get("begunCount"));
                }else
                    begunCount=0.0;
                if(innerMap.containsKey("begunAmount")){
                    begunAmount= Double.parseDouble(innerMap.get("begunAmount"));
                }else
                    begunAmount=0.0;
                if(innerMap.containsKey("failedCount")){
                    failedCount= Double.parseDouble(innerMap.get("failedCount"));
                }else
                    failedCount=0.0;
                if(innerMap.containsKey("failedAmount")){
                    failedAmount= Double.parseDouble(innerMap.get("failedAmount"));
                }else
                    failedAmount=0.0;
                //transactionVOData = new TransactionVO();
                transactionVO.setToid(qryResultSet.getString("toid"));
                transactionVO.setCurrency(qryResultSet.getString("currency"));
                transactionVO.setCardTypeId(qryResultSet.getString("cardType"));
                if(functions.isValueNull(qryResultSet.getString("STATUS")))
                {
                    if(qryResultSet.getString("STATUS").equals("capturesuccess") || qryResultSet.getString("STATUS").equals("reversed") || qryResultSet.getString("STATUS").equals("chargeback") || qryResultSet.getString("STATUS").equals("settled") || qryResultSet.getString("STATUS").equals("authsuccessful") || qryResultSet.getString("STATUS").equals("markedforreversal") || qryResultSet.getString("STATUS").equals("chargebackreversed"))
                    {
                        approveCount=approveCount+qryResultSet.getInt("trans_count");
                        approveAmount=approveAmount+qryResultSet.getDouble("amount");
                        totalSalesAmount= totalSalesAmount+Double.parseDouble(qryResultSet.getString("amount"));
                    }
                    else if(qryResultSet.getString("STATUS").equals("authstarted")) {
                        authstartedCount=/*authstartedCount+*/qryResultSet.getInt("trans_count");
                        authstartedAmount=/*authstartedAmount+*/qryResultSet.getDouble("amount");
                        totalSalesAmount= totalSalesAmount+Double.parseDouble(qryResultSet.getString("amount"));
                    }
                    else if(qryResultSet.getString("STATUS").equals("authfailed")) {
                        authfailedCount=/*authfailedCount+*/qryResultSet.getInt("trans_count");
                        authfailedAmount=/*authfailedAmount+*/qryResultSet.getDouble("amount");
                        totalSalesAmount= totalSalesAmount+Double.parseDouble(qryResultSet.getString("amount"));
                    }
                    else if(qryResultSet.getString("STATUS").equals("payoutsuccessful")) {
                        payoutCount=/*payoutCount+*/qryResultSet.getInt("trans_count");
                        payoutAmount=/*payoutAmount+*/qryResultSet.getDouble("amount");
                        totalSalesAmount= totalSalesAmount+Double.parseDouble(qryResultSet.getString("amount"));
                    }
                    else if(qryResultSet.getString("STATUS").equals("begun")) {
                        begunCount=/*begunCount*/+qryResultSet.getInt("trans_count");
                        begunAmount=/*begunAmount*/+qryResultSet.getDouble("amount");
                        totalSalesAmount= totalSalesAmount+Double.parseDouble(qryResultSet.getString("amount"));
                    }
                    else if(qryResultSet.getString("STATUS").equals("failed")) {
                        failedCount=/*failedCount+*/qryResultSet.getInt("trans_count");
                        failedAmount=/*failedAmount+*/qryResultSet.getDouble("amount");
                        totalSalesAmount= totalSalesAmount+Double.parseDouble(qryResultSet.getString("amount"));
                    }
                    else {
                        totalSalesAmount= totalSalesAmount+Double.parseDouble(qryResultSet.getString("amount"));
                    }
                }
                innerMap.put("approveCount",String.valueOf(approveCount));
                innerMap.put("approveAmount",String.valueOf(approveAmount));
                innerMap.put("authstartedCount",String.valueOf(authstartedCount));
                innerMap.put("authstartedAmount",String.valueOf(authstartedAmount));
                innerMap.put("authfailedCount",String.valueOf(authfailedCount));
                innerMap.put("authfailedAmount",String.valueOf(authfailedAmount));
                innerMap.put("payoutCount",String.valueOf(payoutCount));
                innerMap.put("payoutAmount",String.valueOf(payoutAmount));
                innerMap.put("begunCount",String.valueOf(begunCount));
                innerMap.put("begunAmount",String.valueOf(begunAmount));
                innerMap.put("failedCount",String.valueOf(failedCount));
                innerMap.put("failedAmount",String.valueOf(failedAmount));
                innerMap.put("totalSalesAmount", String.valueOf(totalSalesAmount));
                hashMap.put(qryResultSet.getString("toid")+"-"+qryResultSet.getString("cardType"),innerMap);
            }
            memberList=new ArrayList<>();
            while (cbRefundQryResultSet.next()){
                if(!memberList.contains(cbRefundQryResultSet.getString("toid")+"-"+cbRefundQryResultSet.getString("cardType")))
                {
                    refundCount = 0.00;
                    refundAmount = 0.00;
                    chargebackCount = 0.00;
                    chargebackAmount = 0.00;
                    markforreversalCount=0.00;
                    markforreversalAmount=0.00;
                    memberList.add(cbRefundQryResultSet.getString("toid")+"-"+cbRefundQryResultSet.getString("cardType"));
                }
                HashMap<String,String> innerMap=hashMap.get(cbRefundQryResultSet.getString("toid")+"-"+cbRefundQryResultSet.getString("cardType"));
                if(innerMap==null)
                    innerMap=new HashMap<>();
                if(innerMap.containsKey("refundCount")){
                    refundCount= Double.parseDouble(innerMap.get("refundCount"));
                }else
                    refundCount=0.0;
                if(innerMap.containsKey("refundAmount")){
                    refundAmount= Double.parseDouble(innerMap.get("refundAmount"));
                }else
                    refundAmount=0.0;
                if(innerMap.containsKey("chargebackCount")){
                    chargebackCount= Double.parseDouble(innerMap.get("chargebackCount"));
                }else
                    chargebackCount=0.0;
                if(innerMap.containsKey("chargebackAmount")){
                    chargebackAmount= Double.parseDouble(innerMap.get("chargebackAmount"));
                }else
                    chargebackAmount=0.0;
                if(innerMap.containsKey("markforreversalCount")){
                    markforreversalCount= Double.parseDouble(innerMap.get("markforreversalCount"));
                }else
                    markforreversalCount=0.0;
                if(innerMap.containsKey("markforreversalAmount")){
                    markforreversalAmount= Double.parseDouble(innerMap.get("markforreversalAmount"));
                }else
                    markforreversalAmount=0.0;
                if(cbRefundQryResultSet.getString("STATUS").equals("reversed"))
                {
                    refundCount=/*refundCount+*/cbRefundQryResultSet.getInt("trans_count");
                    refundAmount= /*refundAmount+*/Double.parseDouble(cbRefundQryResultSet.getString("refundamount"));
                }
                if(cbRefundQryResultSet.getString("STATUS").equals("markedforreversal"))
                {
                    markforreversalCount=/*markforreversalCount+*/cbRefundQryResultSet.getInt("trans_count");
                    markforreversalAmount= /*markforreversalAmount+*/Double.parseDouble(cbRefundQryResultSet.getString("refundamount"));
                }
                if(cbRefundQryResultSet.getString("STATUS").equals("chargeback"))
                {
                    chargebackCount=/*chargebackCount+*/cbRefundQryResultSet.getInt("trans_count");
                    chargebackAmount= /*chargebackAmount+*/Double.parseDouble(cbRefundQryResultSet.getString("chargebackamount"));
                }
                innerMap.put("refundCount",String.valueOf(refundCount));
                innerMap.put("refundAmount",String.valueOf(refundAmount));
                innerMap.put("chargebackCount",String.valueOf(chargebackCount));
                innerMap.put("chargebackAmount",String.valueOf(chargebackAmount));
                innerMap.put("markforreversalCount",String.valueOf(markforreversalCount));
                innerMap.put("markforreversalAmount", String.valueOf(markforreversalAmount));
                hashMap.put(cbRefundQryResultSet.getString("toid")+"-"+cbRefundQryResultSet.getString("cardType"),innerMap);
            }
            Set<String> keySet=hashMap.keySet();
            logger.error("hashMap---->"+hashMap);
            for(String key:keySet)
            {
                HashMap<String,String> innerMap=hashMap.get(key);
                String memberId=key.split("-")[0];
                String cardType=key.split("-")[1];
                TransactionVO transactionVO1=getListOfMemberId.get(memberId);
                transactionVOData=new TransactionVO();
                if(functions.isValueNull(innerMap.get("totalCount")))
                    transactionVOData.setTotalTransCount(innerMap.get("totalCount"));
                else
                    transactionVOData.setTotalTransCount("0");
                transactionVOData.setToType(transactionVO1.getToType());
                transactionVOData.setMemberId(transactionVO1.getMemberId());
                transactionVOData.setCurrency(transactionVO.getCurrency());
                transactionVOData.setName(transactionVO1.getName());
                transactionVOData.setCardTypeId(cardType);
                if(functions.isValueNull(innerMap.get("totalSalesAmount")))
                    transactionVOData.setAmount(innerMap.get("totalSalesAmount"));
                else
                    transactionVOData.setAmount("0.0");

                if(functions.isValueNull(innerMap.get("approveCount")))
                    transactionVOData.setCaptureCount(innerMap.get("approveCount"));
                else
                    transactionVOData.setCaptureCount("0.0");

                if(functions.isValueNull(innerMap.get("approveAmount")))
                    transactionVOData.setCaptureAmount(Double.parseDouble(innerMap.get("approveAmount")));
                else
                    transactionVOData.setCaptureAmount(0.0);

                if(functions.isValueNull(innerMap.get("refundCount")))
                    transactionVOData.setRefundcount(innerMap.get("refundCount"));
                else
                    transactionVOData.setRefundcount("0.0");

                if(functions.isValueNull(innerMap.get("refundAmount")))
                    transactionVOData.setReverseAmount(innerMap.get("refundAmount"));
                else
                    transactionVOData.setReverseAmount("0.0");

                if(functions.isValueNull(innerMap.get("chargebackCount")))
                    transactionVOData.setChargebackCount(innerMap.get("chargebackCount"));
                else
                    transactionVOData.setChargebackCount("0.0");

                if(functions.isValueNull(innerMap.get("chargebackAmount")))
                    transactionVOData.setChargebackAmount(innerMap.get("chargebackAmount"));
                else
                    transactionVOData.setChargebackAmount("0.0");

                if(functions.isValueNull(innerMap.get("authfailedCount")))
                    transactionVOData.setDeclineCount(innerMap.get("authfailedCount"));
                else
                    transactionVOData.setDeclineCount("0.0");

                if(functions.isValueNull(innerMap.get("authfailedAmount")))
                    transactionVOData.setDeclineAmount(innerMap.get("authfailedAmount"));
                else
                    transactionVOData.setDeclineAmount("0.0");

                if(functions.isValueNull(innerMap.get("authstartedCount")))
                    transactionVOData.setAuthstartedCount(innerMap.get("authstartedCount"));
                else
                    transactionVOData.setAuthstartedCount("0.0");

                if(functions.isValueNull(innerMap.get("authstartedAmount")))
                    transactionVOData.setAuthstartedAmount(innerMap.get("authstartedAmount"));
                else
                    transactionVOData.setAuthstartedAmount("0.0");

                if(functions.isValueNull(innerMap.get("payoutCount")))
                    transactionVOData.setPayoutCount(innerMap.get("payoutCount"));
                else
                    transactionVOData.setPayoutCount("0.0");

                if(functions.isValueNull(innerMap.get("payoutAmount")))
                    transactionVOData.setPayoutAmount(innerMap.get("payoutAmount"));
                else
                    transactionVOData.setPayoutAmount("0.0");

                if(functions.isValueNull(innerMap.get("begunCount")))
                    transactionVOData.setBegunCount(innerMap.get("begunCount"));
                else
                    transactionVOData.setBegunCount("0.0");

                if(functions.isValueNull(innerMap.get("begunAmount")))
                    transactionVOData.setBegunAmount(innerMap.get("begunAmount"));
                else
                    transactionVOData.setBegunAmount("0.0");

                if(functions.isValueNull(innerMap.get("failedCount")))
                    transactionVOData.setFailedCount(innerMap.get("failedCount"));
                else
                    transactionVOData.setFailedCount("0.0");

                if(functions.isValueNull(innerMap.get("failedAmount")))
                    transactionVOData.setFailedAmount(innerMap.get("failedAmount"));
                else
                    transactionVOData.setFailedAmount("0.0");

                if(functions.isValueNull(innerMap.get("markforreversalCount")))
                    transactionVOData.setMarkforreversalCount(innerMap.get("markforreversalCount"));
                else
                    transactionVOData.setMarkforreversalCount("0.0");

                if(functions.isValueNull(innerMap.get("markforreversalAmount")))
                    transactionVOData.setMarkforreversalAmount(innerMap.get("markforreversalAmount"));
                else
                    transactionVOData.setMarkforreversalAmount("0.0");

                transactionVOs.add(transactionVOData);
            }

        }
        catch (SQLException e)
        {
            logger.error("SQLException::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getPartnerTransactionStatus", null, "Common", "SQLException while connecting to transaction common  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getPartnerTransactionStatus()", null, "Common", "SystemError while connecting to transaction common  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionVOs;
    }

    public Map<String, List<TransactionVO>> getBankTransactionRefundStatus(TransactionVO transactionVO, Set<String> gatewaySet) throws PZDBViolationException
    {
        Connection con = null;
        ResultSet qryResultSet = null;
        ResultSet cQryResultSet = null;
        String totalCount = "";
        ResultSet rs=null;
        ResultSet rs1=null;
        ResultSet rs2=null;
        String total = "";
        String refundtotal = "";
        String gatewayName[] = null;
        TransactionVO transactionVOData = null;
        List<TransactionVO> transactionVOs = new ArrayList<>();
        String tablename = "";
        Map<String, List<TransactionVO>> transactionMap = new HashMap();
        Hashtable hash = new Hashtable();
        Hashtable hashRefund = new Hashtable();
        try
        {
            con = Database.getConnection();
            /*for (String gateway : gatewaySet)
            {*/
            transactionVOs = new ArrayList();

            StringBuffer queryRefund = new StringBuffer("SELECT STATUS,COUNT(*) AS trans_count, SUM(amount) AS amount,SUM(refundamount) AS refundamount,SUM(chargebackamount) AS chargebackamount FROM transaction_common WHERE trackingid>0 ");
            StringBuffer refundcountquery = new StringBuffer("SELECT COUNT(*) AS total_count FROM transaction_common WHERE trackingid>0 ");
            if (functions.isValueNull(transactionVO.getStartDate()))
            {
                queryRefund.append(" AND TIMESTAMP>= '" + transactionVO.getStartDate() + "'");
                refundcountquery.append(" AND TIMESTAMP>='" + transactionVO.getStartDate() + "'");
            }
            if (functions.isValueNull(transactionVO.getEndDate()))
            {
                queryRefund.append(" and TIMESTAMP <='" + transactionVO.getEndDate() + "' and STATUS IN('reversed','chargeback','markedforreversal','chargebackreversed')");
                refundcountquery.append(" and TIMESTAMP <='" + transactionVO.getEndDate() + "' and STATUS IN('reversed','chargeback','markedforreversal','chargebackreversed')");
            }
            if (functions.isValueNull(transactionVO.getToType()))
            {
                queryRefund.append(" and totype ='" + transactionVO.getToType() + "'");
                refundcountquery.append(" and totype ='" + transactionVO.getToType() + "'");
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                queryRefund.append(" and accountid =" + transactionVO.getAccountId());
                refundcountquery.append(" and accountid =" + transactionVO.getAccountId());
            }
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                queryRefund.append(" and fromtype='" + transactionVO.getGatewayName() + "'");
                refundcountquery.append(" and fromtype='" + transactionVO.getGatewayName() + "'");
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                queryRefund.append("  and currency='" + transactionVO.getCurrency() + "'");
                refundcountquery.append("  and currency='" + transactionVO.getCurrency() + "'");
            }
            if (functions.isValueNull(transactionVO.getStatus()))
            {
                queryRefund.append("  and status='" + transactionVO.getStatus() + "'");
                refundcountquery.append("  and status='" + transactionVO.getStatus() + "'");
            }
            if (functions.isValueNull(transactionVO.getTerminalId()))
            {
                queryRefund.append("  and terminalid='" + transactionVO.getTerminalId() + "'");
                refundcountquery.append("  and terminalid='" + transactionVO.getTerminalId() + "'");
            }
            if (functions.isValueNull(transactionVO.getCardTypeId()))
            {
                queryRefund.append("  and cardtypeid='" + transactionVO.getCardTypeId() + "'");
                refundcountquery.append("  and cardtypeid='" + transactionVO.getCardTypeId() + "'");
            }
            if (functions.isValueNull(transactionVO.getPaymodeid()))
            {
                queryRefund.append("  and paymodeid='" + transactionVO.getPaymodeid() + "'");
                refundcountquery.append("  and paymodeid='" + transactionVO.getPaymodeid() + "'");
            }
            if (functions.isValueNull(transactionVO.getMemberId()))
            {
                queryRefund.append(" and toid =" + transactionVO.getMemberId());
                refundcountquery.append(" and toid =" + transactionVO.getMemberId());
            }
            // queryRefund.append(" AND STATUS IN('reversed','chargeback')");
            //refundcountquery.append(" AND STATUS IN('reversed','chargeback')");
            queryRefund.append(" GROUP BY STATUS");

            logger.error("refund query..::::::::." + queryRefund);
            logger.error("refund count query.:::::.." + refundcountquery);

            cQryResultSet = Database.executeQuery(refundcountquery.toString(), con);
            if (cQryResultSet.next())
            {
                totalCount = cQryResultSet.getString("total_count");
            }

            qryResultSet = Database.executeQuery(queryRefund.toString(), con);
            while (qryResultSet.next())
            {
                transactionVOData = new TransactionVO();
                transactionVOData.setStatus(qryResultSet.getString("STATUS"));
                transactionVOData.setAmount(qryResultSet.getString("amount"));
                transactionVOData.setRefundAmount(String.valueOf(qryResultSet.getDouble("refundamount")));
                transactionVOData.setChargebackAmount(String.valueOf(qryResultSet.getDouble("chargebackamount")));
                transactionVOData.setCount(qryResultSet.getInt("trans_count"));
                transactionVOData.setTotalTransCount(totalCount);
                transactionVOs.add(transactionVOData);
            }
            if(transactionVOs.size()>0)
                transactionMap.put("transactionRefundStatus", transactionVOs);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getPartnerRefundStatus", null, "Common", "SQLException while connecting to transaction common  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getPartnerRefundStatus()", null, "Common", "SystemError while connecting to transaction common  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionMap;
    }

    public Map<String, List<TransactionVO>> getGatewayWiseMerchantRefundStatus(TransactionVO transactionVO, Set<String> gatewaySet) throws PZDBViolationException
    {
        Connection con = null;
        ResultSet qryResultSet = null;
        ResultSet cQryResultSet = null;
        String totalCount = "";
        String[] gatewayName = null;
        List<TransactionVO> transactionVOs = null;
        TransactionVO transactionVOData = null;
        Map<String, List<TransactionVO>> transactionVOMap = new HashMap<String, List<TransactionVO>>();

        try
        {
            con = Database.getConnection();
            for (String gateway : gatewaySet)
            {
                transactionVOs = new ArrayList();

                StringBuffer queryRefund = new StringBuffer("SELECT STATUS,COUNT(*) AS trans_count, SUM(amount) AS amount,SUM(refundamount) AS refundamount,SUM(chargebackamount) AS chargebackamount FROM transaction_common WHERE trackingid>0 ");
                StringBuffer refundcountquery = new StringBuffer("SELECT COUNT(*) AS total_count FROM transaction_common WHERE trackingid>0 ");
                if (functions.isValueNull(transactionVO.getStartDate()))
                {
                    queryRefund.append(" AND TIMESTAMP>= '" + transactionVO.getStartDate() + "'");
                    refundcountquery.append(" AND TIMESTAMP>='" + transactionVO.getStartDate() + "'");
                }
                if (functions.isValueNull(transactionVO.getEndDate()))
                {
                    queryRefund.append(" and TIMESTAMP <='" + transactionVO.getEndDate() + "'");
                    refundcountquery.append(" and TIMESTAMP <='" + transactionVO.getEndDate() + "'");
                }
                if (functions.isValueNull(transactionVO.getToType()))
                {
                    queryRefund.append(" and totype ='" + transactionVO.getToType() + "'");
                    refundcountquery.append(" and totype ='" + transactionVO.getToType() + "'");
                }
                /*if (functions.isValueNull(transactionVO.getAccountId()) && !transactionVO.getAccountId().equals("0"))*/
                if (functions.isValueNull(transactionVO.getAccountId()))
                {
                    queryRefund.append(" and accountid =" + transactionVO.getAccountId());
                    refundcountquery.append(" and accountid =" + transactionVO.getAccountId());
                }
                if (functions.isValueNull(transactionVO.getGatewayName()))
                {
                    queryRefund.append(" and fromtype='" + transactionVO.getGatewayName() + "'");
                    refundcountquery.append(" and fromtype='" + transactionVO.getGatewayName() + "'");
                }
                if (functions.isValueNull(transactionVO.getCurrency()))
                {
                    queryRefund.append("  and currency='" + transactionVO.getCurrency() + "'");
                    refundcountquery.append("  and currency='" + transactionVO.getCurrency() + "'");
                }
                if (functions.isValueNull(transactionVO.getStatus()))
                {
                    queryRefund.append("  and status='" + transactionVO.getStatus() + "'");
                    refundcountquery.append("  and status='" + transactionVO.getStatus() + "'");
                }
                if (functions.isDoubleValue(transactionVO.getTerminalId()))
                {
                    queryRefund.append("  and terminalid='" + transactionVO.getTerminalId() + "'");
                    refundcountquery.append("  and terminalid='" + transactionVO.getTerminalId() + "'");
                }
                if (functions.isValueNull(transactionVO.getCardTypeId()))
                {
                    queryRefund.append("  and cardtypeid='" + transactionVO.getCardTypeId() + "'");
                    refundcountquery.append("  and cardtypeid='" + transactionVO.getCardTypeId() + "'");
                }
                if (functions.isValueNull(transactionVO.getPaymodeid()))
                {
                    queryRefund.append("  and paymodeid='" + transactionVO.getPaymodeid() + "'");
                    refundcountquery.append("  and paymodeid='" + transactionVO.getPaymodeid() + "'");
                }
                if (functions.isValueNull(transactionVO.getMemberId()))
                {
                    queryRefund.append(" and toid =" + transactionVO.getMemberId());
                    refundcountquery.append(" and toid =" + transactionVO.getMemberId());
                }
                // queryRefund.append(" AND STATUS IN('reversed','chargeback')");
                //refundcountquery.append(" AND STATUS IN('reversed','chargeback')");
                queryRefund.append(" GROUP BY STATUS");

                logger.debug("refund query..." + queryRefund);
                logger.debug("refund count query..." + refundcountquery);

                cQryResultSet = Database.executeQuery(refundcountquery.toString(), con);
                if (cQryResultSet.next())
                {
                    totalCount = cQryResultSet.getString("total_count");
                }

                qryResultSet = Database.executeQuery(queryRefund.toString(), con);
                while (qryResultSet.next())
                {
                    transactionVOData = new TransactionVO();
                    transactionVOData.setStatus(qryResultSet.getString("STATUS"));
                    transactionVOData.setAmount(qryResultSet.getString("amount"));
                    transactionVOData.setRefundAmount(String.valueOf(qryResultSet.getDouble("refundamount")));
                    transactionVOData.setChargebackAmount(String.valueOf(qryResultSet.getDouble("chargebackamount")));
                    transactionVOData.setCount(qryResultSet.getInt("trans_count"));
                    transactionVOData.setTotalTransCount(totalCount);
                    transactionVOs.add(transactionVOData);
                }
                if(transactionVOs.size()>0)
                    transactionVOMap.put(gateway, transactionVOs);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getPartnerRefundStatus", null, "Common", "SQLException while connecting to transaction common  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getPartnerRefundStatus()", null, "Common", "SystemError while connecting to transaction common  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionVOMap;
    }

    public TransactionDetailsVO getCommonTransactionDetailsForChargeBack(String trackingId) throws PZDBViolationException
    {
        Connection con = null;
        TransactionDetailsVO transactionDetailsVO = null;
        try
        {
            con = Database.getConnection();
            String update = "select trackingid,accountid,paymodeid,cardtypeid,toid,totype,fromid,fromtype,description,orderdescription,templateamount,amount,currency,redirecturl,notificationUrl,status,firstname,lastname,name,ccnum,expdate,cardtype,emailaddr,ipaddress,country,state,city,street,zip,telnocc,telno,httpheader,paymentid,templatecurrency,emailaddr,customerId,eci,terminalid,version,captureamount,refundamount,emiCount,timestamp,walletAmount,walletCurrency from transaction_common where trackingid=? AND status IN('capturesuccess','settled','reversed','chargebackreversed','chargeback')";
            PreparedStatement psUpdateTransaction = con.prepareStatement(update);
            psUpdateTransaction.setString(1, trackingId);
            ResultSet resultSet = psUpdateTransaction.executeQuery();
            if (resultSet.next())
            {
                transactionDetailsVO = new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(resultSet.getString("trackingid"));
                transactionDetailsVO.setAccountId(resultSet.getString("accountid"));
                transactionDetailsVO.setPaymodeId(resultSet.getString("paymodeid"));
                transactionDetailsVO.setCardTypeId(resultSet.getString("cardtypeid"));
                transactionDetailsVO.setToid(resultSet.getString("toid"));
                transactionDetailsVO.setTotype(resultSet.getString("totype"));//totype
                transactionDetailsVO.setFromid(resultSet.getString("fromid"));//fromid
                transactionDetailsVO.setFromtype(resultSet.getString("fromtype"));//fromtype
                transactionDetailsVO.setDescription(resultSet.getString("description"));
                transactionDetailsVO.setOrderDescription(resultSet.getString("orderdescription"));
                transactionDetailsVO.setTemplateamount(resultSet.getString("templateamount"));
                transactionDetailsVO.setAmount(resultSet.getString("amount"));
                transactionDetailsVO.setCurrency(resultSet.getString("currency"));
                transactionDetailsVO.setRedirectURL(resultSet.getString("redirecturl"));
                transactionDetailsVO.setNotificationUrl(resultSet.getString("notificationUrl"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setFirstName(resultSet.getString("firstname"));//fn
                transactionDetailsVO.setLastName(resultSet.getString("lastname"));//ln
                transactionDetailsVO.setName(resultSet.getString("name"));//name
                transactionDetailsVO.setCcnum(resultSet.getString("ccnum"));//ccnum
                transactionDetailsVO.setExpdate(resultSet.getString("expdate"));//expdt
                transactionDetailsVO.setCardtype(resultSet.getString("cardtype"));//cardtype
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setIpAddress(resultSet.getString("ipaddress"));
                transactionDetailsVO.setCountry(resultSet.getString("country"));//country
                transactionDetailsVO.setState(resultSet.getString("state"));//state
                transactionDetailsVO.setCity(resultSet.getString("city"));//city
                transactionDetailsVO.setStreet(resultSet.getString("street"));//street
                transactionDetailsVO.setZip(resultSet.getString("zip"));//zip
                transactionDetailsVO.setTelcc(resultSet.getString("telnocc"));//telcc
                transactionDetailsVO.setTelno(resultSet.getString("telno"));//telno
                transactionDetailsVO.setHttpHeader(resultSet.getString("httpheader"));//httpheadet
                transactionDetailsVO.setPaymentId(resultSet.getString("paymentid"));
                transactionDetailsVO.setTemplatecurrency(resultSet.getString("templatecurrency"));
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setCustomerId(resultSet.getString("customerId"));
                transactionDetailsVO.setEci(resultSet.getString("eci"));
                transactionDetailsVO.setTerminalId(resultSet.getString("terminalid"));
                transactionDetailsVO.setVersion(resultSet.getString("version"));
                transactionDetailsVO.setCaptureAmount(resultSet.getString("captureamount"));
                transactionDetailsVO.setRefundAmount(resultSet.getString("refundamount"));
                transactionDetailsVO.setEmiCount(resultSet.getString("emiCount"));
                transactionDetailsVO.setTransactionTime(resultSet.getString("timestamp"));
                transactionDetailsVO.setWalletAmount(resultSet.getString("walletAmount"));
                transactionDetailsVO.setWalletCurrency(resultSet.getString("walletCurrency"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getCommonTransactionDetailsForChargeBack()", null, "Common", "SQLException while connecting to transaction common  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getCommonTransactionDetailsForChargeBack()", null, "Common", "SystemError while connecting to transaction common  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionDetailsVO;
    }

    public TransactionDetailsVO getBorgunCommonTransactionDetailsForChargeBack(String RRN) throws PZDBViolationException
    {
        Connection conn = null;
        TransactionDetailsVO transactionDetailsVO = null;
        try
        {
            String query = "SELECT tc.toid,tc.status,tc.trackingid,tc.amount,tc.accountid FROM transaction_common AS tc JOIN transaction_borgun_details AS tbd WHERE tc.trackingid=tbd.trackingId AND tbd.rrn=?";
            conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, RRN);
            ResultSet res = pstmt.executeQuery();
            if (res.next())
            {
                transactionDetailsVO = new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(res.getString("trackingid"));
                transactionDetailsVO.setAccountId(res.getString("accountid"));
                transactionDetailsVO.setToid(res.getString("toid"));
                transactionDetailsVO.setAmount(res.getString("amount"));
                transactionDetailsVO.setStatus(res.getString("status"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getBorgunCommonTransactionDetailsForChargeBack()", null, "Common", "SQLException while connecting to transaction common  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getBorgunCommonTransactionDetailsForChargeBack()", null, "Common", "SystemError while connecting to transaction common  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transactionDetailsVO;
    }

    public TransactionDetailsVO getCommonTransactionDetailsForChargeBackByBankId(String paymentId) throws PZDBViolationException
    {
        Connection con = null;
        TransactionDetailsVO transactionDetailsVO = null;
        try
        {
            con = Database.getConnection();
            String update = "SELECT trackingid,accountid,paymodeid,cardtypeid,toid,totype,fromid,fromtype,description,orderdescription,templateamount,amount,currency,redirecturl,notificationUrl,status,firstname,lastname,name,ccnum,expdate,cardtype,emailaddr,ipaddress,country,state,city,street,zip,telnocc,telno,httpheader,paymentid,templatecurrency,emailaddr,customerId,eci,terminalid,version,captureamount,refundamount,emiCount,timestamp,walletAmount,walletCurrency FROM transaction_common WHERE paymentid=? AND status IN('capturesuccess','settled','reversed','chargebackreversed','chargeback')";
            PreparedStatement psUpdateTransaction = con.prepareStatement(update);
            psUpdateTransaction.setString(1, paymentId);
            ResultSet resultSet = psUpdateTransaction.executeQuery();
            if (resultSet.next())
            {
                transactionDetailsVO = new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(resultSet.getString("trackingid"));
                transactionDetailsVO.setAccountId(resultSet.getString("accountid"));
                transactionDetailsVO.setPaymodeId(resultSet.getString("paymodeid"));
                transactionDetailsVO.setCardTypeId(resultSet.getString("cardtypeid"));
                transactionDetailsVO.setToid(resultSet.getString("toid"));
                transactionDetailsVO.setTotype(resultSet.getString("totype"));//totype
                transactionDetailsVO.setFromid(resultSet.getString("fromid"));//fromid
                transactionDetailsVO.setFromtype(resultSet.getString("fromtype"));//fromtype
                transactionDetailsVO.setDescription(resultSet.getString("description"));
                transactionDetailsVO.setOrderDescription(resultSet.getString("orderdescription"));
                transactionDetailsVO.setTemplateamount(resultSet.getString("templateamount"));
                transactionDetailsVO.setAmount(resultSet.getString("amount"));
                transactionDetailsVO.setCurrency(resultSet.getString("currency"));
                transactionDetailsVO.setRedirectURL(resultSet.getString("redirecturl"));
                transactionDetailsVO.setNotificationUrl(resultSet.getString("notificationUrl"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setFirstName(resultSet.getString("firstname"));//fn
                transactionDetailsVO.setLastName(resultSet.getString("lastname"));//ln
                transactionDetailsVO.setName(resultSet.getString("name"));//name
                transactionDetailsVO.setCcnum(resultSet.getString("ccnum"));//ccnum
                transactionDetailsVO.setExpdate(resultSet.getString("expdate"));//expdt
                transactionDetailsVO.setCardtype(resultSet.getString("cardtype"));//cardtype
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setIpAddress(resultSet.getString("ipaddress"));
                transactionDetailsVO.setCountry(resultSet.getString("country"));//country
                transactionDetailsVO.setState(resultSet.getString("state"));//state
                transactionDetailsVO.setCity(resultSet.getString("city"));//city
                transactionDetailsVO.setStreet(resultSet.getString("street"));//street
                transactionDetailsVO.setZip(resultSet.getString("zip"));//zip
                transactionDetailsVO.setTelcc(resultSet.getString("telnocc"));//telcc
                transactionDetailsVO.setTelno(resultSet.getString("telno"));//telno
                transactionDetailsVO.setHttpHeader(resultSet.getString("httpheader"));//httpheadet
                transactionDetailsVO.setPaymentId(resultSet.getString("paymentid"));
                transactionDetailsVO.setTemplatecurrency(resultSet.getString("templatecurrency"));
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setCustomerId(resultSet.getString("customerId"));
                transactionDetailsVO.setEci(resultSet.getString("eci"));
                transactionDetailsVO.setTerminalId(resultSet.getString("terminalid"));
                transactionDetailsVO.setVersion(resultSet.getString("version"));
                transactionDetailsVO.setCaptureAmount(resultSet.getString("captureamount"));
                transactionDetailsVO.setRefundAmount(resultSet.getString("refundamount"));
                transactionDetailsVO.setEmiCount(resultSet.getString("emiCount"));
                transactionDetailsVO.setTransactionTime(resultSet.getString("timestamp"));
                transactionDetailsVO.setWalletAmount(resultSet.getString("walletAmount"));
                transactionDetailsVO.setWalletCurrency(resultSet.getString("walletCurrency"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getCommonTransactionDetailsForChargeBackByBankId()", null, "Common", "SQLException while connecting to transaction common  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getCommonTransactionDetailsForChargeBackByBankId()", null, "Common", "SystemError while connecting to transaction common  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionDetailsVO;
    }


    public List<String> getListOfCurrencies(String toid)
    {
        List<String> currencyList = new ArrayList();
        Connection connection = null;

        try
        {
            {
                connection = Database.getConnection();
                String query = "SELECT DISTINCT gt.currency FROM gateway_type AS gt, member_account_mapping AS mam, gateway_accounts AS ga WHERE mam.memberid = ? AND mam.accountid = ga.accountid AND ga.pgtypeid = gt.pgtypeid";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setString(1,toid);
                ResultSet resultSet = pstmt.executeQuery();
                while (resultSet.next())
                {
                    String currency = "";
                    currency = resultSet.getString("currency");
                    currencyList.add(currency);
                }
            }
        }
        catch (SystemError e)
        {
            logger.error("SystemError----", e);
        }
        catch (SQLException e)
        {
            logger.error("SQLException----", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return currencyList;
    }
    public List<String> getListOfCurrenciesForSubMerchant(String toid,String login)
    {
        List<String> currencyList = new ArrayList();
        Connection connection = null;

        try
        {
            {
                connection = Database.getConnection();
                String query = "SELECT DISTINCT gt.currency FROM gateway_type AS gt, gateway_accounts AS ga,member_users AS mu,member_user_account_mapping AS muam WHERE mu.memberid = ? AND mu.login=? AND mu.userid=muam.userid AND muam.accountid = ga.accountid AND ga.pgtypeid = gt.pgtypeid";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setString(1,toid);
                pstmt.setString(2,login);
                ResultSet resultSet = pstmt.executeQuery();
                while (resultSet.next())
                {
                    String currency = "";
                    currency = resultSet.getString("currency");
                    currencyList.add(currency);
                }
            }
        }
        catch (SystemError e)
        {
            logger.error("SystemError----", e);
        }
        catch (SQLException e)
        {
            logger.error("SQLException----", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return currencyList;
    }

    public Hashtable listTransactions(String toid,String startDate, String endDate, String status, int records, int pageno, Set<String> gatewayTypeSet,String payModeId,String cardTypeId,String accountId) throws SystemError
    {
        logger.debug("Entering listTransactions in MerchantTransMailList");
        Functions functions = new Functions();
        Hashtable hash = null;
        String tablename = "";
        String fields = "";
        String orderby = "";
        StringBuffer query = new StringBuffer();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Connection conn = null;
        Set<String> accountids=null;
        try
        {
            if(gatewayTypeSet.size()==1)
            {
                GatewayManager gatewayManager=new GatewayManager();
                String gateway = gatewayTypeSet.iterator().next();
                accountids =gatewayManager.getGatewayAccounts(gateway);
            }
            Iterator i = gatewayTypeSet.iterator();
            while(i.hasNext())
            {
                tablename = Database.getTableName((String) i.next());
                if(tablename.equals("transaction_icicicredit")||tablename.equals("transaction_icicicredit_archive"))
                {
                    fields = tablename+".icicitransid as transid,toid,status,name,amount,description," + tablename + ".authqsiresponsecode as ordernumber,"+tablename+".authqsiresponsedesc as remark,orderdescription,members.company_name,members.contact_emails,members.contact_persons, " + tablename + ".dtstamp," + tablename + ".accountid ";
                }
                else if(tablename.equals("transaction_qwipi"))
                {
                    fields = "trackingid as transid,toid,status,name,amount,description,orderdescription," + tablename + ".qwipiPaymentOrderNumber as ordernumber,"+tablename+".remark as remark,members.company_name,members.contact_emails,members.contact_persons, " + tablename + ".dtstamp," + tablename + ".accountid ";
                }
                else if(tablename.equals("transaction_ecore"))
                {
                    fields = "trackingid as transid,toid,status,name,amount,description,orderdescription," + tablename + ".ecorePaymentOrderNumber as ordernumber,"+tablename+".remark as remark,members.company_name,members.contact_emails,members.contact_persons, " + tablename + ".dtstamp," + tablename + ".accountid ";
                }
                else if(tablename.equals("transaction_common"))
                {
                    fields = "trackingid as transid,toid,status,name,amount,description,orderdescription," + tablename + ".paymentid as ordernumber,"+tablename+".captureinfo as remark,members.company_name,members.contact_emails,members.contact_persons, " + tablename + ".dtstamp," + tablename + ".accountid ";
                }
                else
                {
                    fields = "trackingid as transid,toid,status,name,amount,description,orderdescription," + tablename + ".voucherid as ordernumber,"+tablename+".capturedata as remark,members.company_name,members.contact_emails,members.contact_persons, " + tablename + ".dtstamp," + tablename + ".accountid ";
                }

                if(tablename.equals("transaction_icicicredit")||tablename.equals("transaction_icicicredit_archive"))
                {
                    query.append("select " + fields + " from " + tablename + ",members  where " + tablename+ ".toid=members.memberid ");
                }
                else
                {
                    query.append("select " + fields + " from " + tablename + ",members  where " + tablename+ ".toid=members.memberid  ");
                }

                if (functions.isValueNull(toid))
                {
                    query.append(" and toid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
                }
                if (functions.isValueNull(startDate))
                {
                    query.append(" and FROM_UNIXTIME("+tablename+".dtstamp)>= '"+startDate+"'");
                }
                if (functions.isValueNull(endDate))
                {
                    query.append(" and  FROM_UNIXTIME("+tablename+".dtstamp)<='" +endDate+"'");
                }
                if(functions.isValueNull(payModeId))
                {
                    query.append(" and paymodeid="+ESAPI.encoder().encodeForSQL(me,payModeId));
                }
                if(functions.isValueNull(cardTypeId))
                {
                    query.append(" and cardtypeid="+ESAPI.encoder().encodeForSQL(me,cardTypeId));
                }
                if(functions.isValueNull(accountId))
                {
                    query.append(" and "+tablename+".accountid="+ESAPI.encoder().encodeForSQL(me,accountId));
                }
                if (functions.isValueNull(status))
                {
                    query.append(" and status = '" + ESAPI.encoder().encodeForSQL(me,status)+"'");
                }
                else
                {
                    query.append(" and status = 'authfailed' ");
                }
                if(accountids != null && !accountids.equals("null") && !accountids.equals(""))
                {
                    query.append(" and "+tablename+".accountid IN ( ");
                    Iterator<String> accounts =  accountids.iterator();
                    while(accounts.hasNext())
                    {
                        String account = accounts.next();
                        query.append(account);
                        if(accounts.hasNext())
                        {
                            query.append(" , ");
                        }
                        else
                        {
                            query.append(" )");
                        }
                    }
                }

                if(i.hasNext())
                    query.append(" UNION ");
            }

            StringBuffer countquery = new StringBuffer("select count(*),SUM(amount) as grandtotal from ( " + query + ") as temp ");
            query.append(" order by dtstamp DESC,transid ");
            //query.append("  limit " + start + "," + end);
            logger.debug("query======"+query.toString());

            conn = Database.getConnection();
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));
            ResultSet rs = Database.executeQuery(countquery.toString(), conn);
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
        {   logger.error("SQL Exception:::::",se);

            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public Hashtable getReport(String toid, String terminalId, String startDate, String endDate,Set<String> gatewayTypeSet,String payModeId,String cardTypeId,String accountId) throws SystemError
    {
        Hashtable hash = null;
        String tablename = "";
        String fields = "";
        Connection conn = null;
        StringBuffer query = new StringBuffer();

        TransactionEntry transactionentry = new TransactionEntry();
        Iterator j = gatewayTypeSet.iterator();
        conn=Database.getConnection();
        Functions functions = new Functions();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Set<String>  accountids = null;
        if(gatewayTypeSet.size()==1)
        {
            GatewayManager gatewayManager=new GatewayManager();
            String gateway = gatewayTypeSet.iterator().next();
            accountids = gatewayManager.getGatewayAccounts(gateway);
        }
        while(j.hasNext())
        {
            tablename = Database.getTableName((String)j.next());
            fields = "status,count(*) as count,SUM(amount) as amount,SUM(captureamount) as captureamount,SUM(refundamount) as refundamount,SUM(chargebackamount) as chargebackamount ";
            query.append("select " + fields + " from " + tablename + " where  FROM_UNIXTIME(dtstamp) >='" + startDate + "' and  FROM_UNIXTIME(dtstamp)<='" + endDate + "'");

            if(functions.isValueNull(toid))
            {
                query.append(" and toid ='" +ESAPI.encoder().encodeForSQL(me, toid) + "'");
            }
            if(functions.isValueNull(terminalId))
            {
                query.append(" and terminalid="+ESAPI.encoder().encodeForSQL(me, terminalId));
            }
            if(functions.isValueNull(payModeId))
            {
                query.append(" and paymodeid="+ESAPI.encoder().encodeForSQL(me, payModeId));
            }
            if(functions.isValueNull(cardTypeId))
            {
                query.append(" and cardtypeid="+ESAPI.encoder().encodeForSQL(me, cardTypeId));
            }

            if(functions.isValueNull(accountId))
            {
                query.append(" and "+tablename+".accountid="+ESAPI.encoder().encodeForSQL(me,accountId));
            }
            if(accountids != null && !accountids.equals("null") && !accountids.equals(""))
            {
                query.append(" and "+tablename+".accountid IN ( ");
                Iterator<String> accounts =  accountids.iterator();
                while(accounts.hasNext())
                {
                    String account=accounts.next();
                    query.append(account);
                    if(accounts.hasNext())
                    {
                        query.append(" , ");
                    }
                    else
                    {
                        query.append(" )");
                    }
                }
            }

            query.append(" group by status");

            if(j.hasNext())
            {
                query.append(" UNION ");
            }
            logger.debug("query===="+query);
        }

        StringBuffer reportquery = new StringBuffer("select status, SUM(count) as count,SUM(amount) as amount,SUM(captureamount) as captureamount,SUM(refundamount) as refundamount,SUM(chargebackamount) as chargebackamount from ( " + query + ")as temp  group by status ");
        StringBuffer countquery = new StringBuffer("select SUM(count) as count,SUM(amount) as grandtotal,SUM(captureamount) as captureamount,SUM(refundamount) as refundamount,SUM(chargebackamount) as chargebackamount from ( " + query + ")as temp  ");
        Hashtable statusreport= new Hashtable();
        try
        {
            statusreport=Database.getHashFromResultSet(Database.executeQuery(reportquery.toString(), conn));
            ResultSet rs = Database.executeQuery(countquery.toString(), conn);
            logger.debug(reportquery);
            logger.debug(countquery);
            int total = 0;
            String totalamount="";
            if (rs.next())
            {
                total = rs.getInt("count");
                totalamount=rs.getString("grandtotal");
            }
            statusreport.put("grandtotal",functions.isValueNull(totalamount)?totalamount:0);
            statusreport.put("totalrecords", "" + total);
            statusreport.put("records", "0");

            if (total > 0)
                statusreport.put("records", "" + (statusreport.size() - 2));
        }
        catch (SQLException e)
        {
            logger.error("sql error while execute status report",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        logger.debug(statusreport);
        return statusreport;
    }

    public Hashtable getRefundChargebackReport(String toid, String startDate, String endDate,Set<String> gatewayTypeSet,String payModeId,String cardTypeId,String accountId) throws SystemError
    {
        String tablename = "";
        String fields = "";
        Connection conn = null;

        StringBuffer refundcbk = new StringBuffer();
        Iterator j = gatewayTypeSet.iterator();
        conn=Database.getConnection();
        Functions functions = new Functions();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Set<String>  accountids = null;
        if(gatewayTypeSet.size()==1)
        {
            GatewayManager gatewayManager=new GatewayManager();
            String gateway=gatewayTypeSet.iterator().next();
            accountids =gatewayManager.getGatewayAccounts(gateway);
        }
        while(j.hasNext())
        {
            tablename = Database.getTableName((String)j.next());
            fields = "status,count(*) as count,SUM(amount) as amount,SUM(refundamount) as refundamount,SUM(chargebackamount) as chargebackamount ";
            refundcbk.append("select " + fields + " from " + tablename + " where status in('reversed','chargeback') and timestamp >='"+startDate+"' and timestamp <='"+endDate+"'");
            if(functions.isValueNull(toid))
            {
                refundcbk.append(" and toid ='" + ESAPI.encoder().encodeForSQL(me, toid) + "'");
            }
            if(functions.isValueNull(accountId))
            {
                refundcbk.append(" and "+tablename+".accountid="+ESAPI.encoder().encodeForSQL(me, accountId));
            }
            if(functions.isValueNull(payModeId))
            {
                refundcbk.append(" and paymodeid="+ESAPI.encoder().encodeForSQL(me, payModeId));
            }
            if(functions.isValueNull(cardTypeId))
            {
                refundcbk.append(" and cardtypeid="+ESAPI.encoder().encodeForSQL(me, cardTypeId));
            }
            if(accountids != null && !accountids.equals("null") && !accountids.equals(""))
            {
                refundcbk.append(" and "+tablename+".accountid IN ( ");
                Iterator<String> accounts =  accountids.iterator();
                while(accounts.hasNext())
                {
                    String account=accounts.next();
                    refundcbk.append(account);
                    if(accounts.hasNext())
                    {
                        refundcbk.append(" , ");
                    }
                    else
                    {
                        refundcbk.append(" )");
                    }
                }
            }

            refundcbk.append(" group by status");
            if(j.hasNext())
            {
                refundcbk.append(" UNION ");
            }
        }

        StringBuffer refcbk = new StringBuffer("select status, SUM(count) as count,SUM(amount) as amount,SUM(refundamount) as refundamount,SUM(chargebackamount) as chargebackamount from ( " + refundcbk + ")as temp  group by status ");
        StringBuffer countquery = new StringBuffer("select SUM(count) as count,SUM(amount) as grandtotal,SUM(refundamount) as refundamount,SUM(chargebackamount) as chargebackamount from ( " + refundcbk + ")as temp  ");
        Hashtable refundcbk1=new Hashtable();
        try
        {
            refundcbk1 = Database.getHashFromResultSet(Database.executeQuery(refcbk.toString(), conn));
            ResultSet rs = Database.executeQuery(countquery.toString(), conn);

            logger.debug(countquery);
            int total = 0;
            String totalamount="";
            if (rs.next())
            {
                total = rs.getInt("count");
                totalamount=rs.getString("grandtotal");
            }
            refundcbk1.put("grandtotal",functions.isValueNull(totalamount)?totalamount:0);
            refundcbk1.put("totalrecords", "" + total);
            refundcbk1.put("records", "0");

            if (total > 0)
                refundcbk1.put("records", "" + (refundcbk1.size() - 2));

        }
        catch (SQLException e)
        {
            logger.error("sql error while execute status report",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        logger.debug(refundcbk1);
        return refundcbk1;
    }

    //new
    public List<TransactionVO> transactionDetailsForAPI(TransactionVO transactionVO,CommonValidatorVO commonValidatorVO, List<TransactionVO> transDetailVOList,String fdtstamp,String tdtstamp)
    {
        logger.debug("Entering listInvoices");

        Functions functions = new Functions();
        GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = new GenericCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();

        //  List<TransactionVO> transDetailVOLists = new ArrayList<TransactionVO>();

        StringBuffer query = new StringBuffer();
        Connection conn=null;

        try
        {
            conn=Database.getRDBConnection();
            query.append("SELECT COUNT(*) AS COUNT ,SUM(amount)AS AMOUNT ,SUM(captureamount)AS CAMOUNT,SUM(refundamount)AS RAMOUNT,SUM(chargebackamount) AS CBAMOUNT,STATUS,toid,currency,timestamp,dtstamp FROM transaction_common WHERE ");

            if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId()));
            {
                query.append("toid ='" + commonValidatorVO.getMerchantDetailsVO().getMemberId()+ "'");
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and dtstamp >= " + fdtstamp);

            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and dtstamp <= " + tdtstamp);

            }
            if (functions.isValueNull(genericTransDetailsVO.getCurrency()))
            {
                query.append(" and currency = '" + genericTransDetailsVO.getCurrency() + "'");
            }

            query.append(" GROUP BY currency,STATUS ");


            logger.debug("query..." + query);

            logger.debug("Fatch records from transaction ");
            ResultSet queryRs = Database.executeQuery(query.toString(),conn);
            while (queryRs.next())
            {
                TransactionVO transactionVO1 = new TransactionVO();
                transactionVO1.setMemberId(queryRs.getString("toid"));
                transactionVO1.setAmount(queryRs.getString("AMOUNT"));
                transactionVO1.setCapAmount(queryRs.getString("CAMOUNT"));
                transactionVO1.setRefundAmount(queryRs.getString("RAMOUNT"));
                transactionVO1.setChargebackAmount(queryRs.getString("CBAMOUNT"));
                transactionVO1.setCurrency(queryRs.getString("currency"));
                transactionVO1.setTransactionStatus(queryRs.getString("STATUS"));
                transactionVO1.setCounts(queryRs.getString("count"));
                transactionVO1.setTimestamp(queryRs.getString("timestamp"));
                transactionVO1.setDtStamp(queryRs.getString("dtstamp"));

                transDetailVOList.add(transactionVO1);
            }
        }
        catch (SystemError e)
        {
            logger.error("SystemError Exception leaving listTransactions", e);
        }
        catch (SQLException se)
        {
            logger.error("SQL Exception leaving listTransactions", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        logger.debug("Leaving listTransactions  "+query.toString());
        return transDetailVOList;
    }
    public boolean checkPendingTransaction(GatewayAccount gatewayAccount,DateVO dateVO)throws PZDBViolationException{
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        boolean status=false;
        try{
            con=Database.getConnection();
            String tableName=Database.getTableNameForSettlement(gatewayAccount.getGateway());
            String update = "select trackingid from "+tableName+" where accountid=? and  FROM_UNIXTIME(dtstamp) >=? and FROM_UNIXTIME(dtstamp) <=? and status in('authstarted','capturesuccess','capturestarted','markedforreversal')";
            pstmt=con.prepareStatement(update);
            pstmt.setInt(1,gatewayAccount.getAccountId());
            pstmt.setString(2, dateVO.getStartDate());
            pstmt.setString(3, dateVO.getEndDate());
            rs = pstmt.executeQuery();
            if (rs.next()){
                status=true;
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "checkPendingTransaction()", null, "Common", "SQLException while connecting to transaction  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "checkPendingTransaction()", null, "Common", "SystemError while connecting to transaction  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }
    public boolean checkPendingTransactionOfMerchant(GatewayAccount gatewayAccount,String memberId,String terminalId,DateVO dateVO)throws PZDBViolationException{
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        boolean status=false;
        try{
            con=Database.getConnection();
            String tableName=Database.getTableNameForSettlement(gatewayAccount.getGateway());
            String update = "select trackingid,status from "+tableName+" where accountid=? AND toid=? AND terminalid=? and FROM_UNIXTIME(dtstamp) >=? and FROM_UNIXTIME(dtstamp) <=? and status in('authstarted','capturesuccess','capturestarted','markedforreversal','payoutstarted')";
            pstmt=con.prepareStatement(update);
            pstmt.setInt(1, gatewayAccount.getAccountId());
            pstmt.setString(2, memberId);
            pstmt.setString(3, terminalId);
            pstmt.setString(4, dateVO.getStartDate());
            pstmt.setString(5, dateVO.getEndDate());
            logger.error("pstmt------------>"+pstmt);
            rs = pstmt.executeQuery();
            if (rs.next()){
               /* if("settled".equalsIgnoreCase(rs.getString("status")))
                {
                    status = false;*/
                status=true;
                /*}*/
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::", e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "checkPendingTransaction()", null, "Common", "SQLException while connecting to transaction  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "checkPendingTransaction()", null, "Common", "SystemError while connecting to transaction  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }
    public void getCommonTransactionDetailsNew(TransactionDetailsVO transactionDetailsVO,String trackingId,String description)
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet rs = null;
        try
        {
            con=Database.getConnection();
            StringBuffer sb =new StringBuffer("select * from transaction_common where trackingid>0 ");
            if(functions.isValueNull(trackingId))
            {
                sb.append(" and trackingid ="+trackingId+"");
            }
            else if(functions.isValueNull(description))
            {
                sb.append(" and description like '%"+description+"%'");
            }
            pstmt=con.prepareStatement(sb.toString());
            logger.debug("query=====from common=="+sb.toString());
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                transactionDetailsVO.setTrackingid(rs.getString("trackingid"));
                transactionDetailsVO.setAccountId(rs.getString("accountid"));
                transactionDetailsVO.setPaymodeId(rs.getString("paymodeid"));
                transactionDetailsVO.setCardTypeId(rs.getString("cardtypeid"));
                transactionDetailsVO.setToid(rs.getString("toid"));
                transactionDetailsVO.setTotype(rs.getString("totype"));//totype
                transactionDetailsVO.setFromid(rs.getString("fromid"));//fromid
                transactionDetailsVO.setFromtype(rs.getString("fromtype"));//fromtype
                transactionDetailsVO.setDescription(rs.getString("description"));
                transactionDetailsVO.setOrderDescription(rs.getString("orderdescription"));
                transactionDetailsVO.setAmount(rs.getString("amount"));
                transactionDetailsVO.setCurrency(rs.getString("currency"));
                transactionDetailsVO.setRedirectURL(rs.getString("redirecturl"));
                transactionDetailsVO.setStatus(rs.getString("status"));
                transactionDetailsVO.setFirstName(rs.getString("firstname"));//fn
                transactionDetailsVO.setLastName(rs.getString("lastname"));//ln
                transactionDetailsVO.setName(rs.getString("name"));//name
                transactionDetailsVO.setCcnum(rs.getString("ccnum"));//ccnum
                transactionDetailsVO.setExpdate(rs.getString("expdate"));//expdt
                transactionDetailsVO.setCardtype(rs.getString("cardtype"));//cardtype
                transactionDetailsVO.setEmailaddr(rs.getString("emailaddr"));
                transactionDetailsVO.setCountry(rs.getString("country"));//country
                transactionDetailsVO.setState(rs.getString("state"));//state
                transactionDetailsVO.setCity(rs.getString("city"));//city
                transactionDetailsVO.setStreet(rs.getString("street"));//street
                transactionDetailsVO.setZip(rs.getString("zip"));//zip
                transactionDetailsVO.setTelcc(rs.getString("telnocc"));//telcc
                transactionDetailsVO.setTelno(rs.getString("telno"));//telno
                transactionDetailsVO.setHttpHeader(rs.getString("httpheader"));//httpheadet
                transactionDetailsVO.setCaptureAmount(rs.getString("captureamount"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
    public boolean updateIsFraudRequest(String trackingId,String remark,String fraudReason)
    {
        Connection con = null;
        boolean status=false;
        try
        {
            con=Database.getConnection();
            String update = "UPDATE bin_details SET isFraud='Y',reason=?,fraudreason=? WHERE icicitransid=?";
            PreparedStatement psUpdateTransaction =con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,remark);
            psUpdateTransaction.setString(2,fraudReason);
            psUpdateTransaction.setString(3,trackingId);
            int k=psUpdateTransaction.executeUpdate();
            if(k==1)
            {
                status=true;
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }
    public boolean  getUploadBulkFraudFile(String trackingId)
    {
        Connection con = null;
        boolean status=false;
        try
        {
            con = Database.getConnection();
            StringBuffer update =new StringBuffer("UPDATE bin_details SET isFraud='Y' WHERE icicitransid IN ("+trackingId+")");
            PreparedStatement psUpdateTransaction =con.prepareStatement(update.toString());
            int k=psUpdateTransaction.executeUpdate();
            if(k>0)
            {
                status=true;
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }

    public boolean isTransactionFraud (String trackingId)
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet Result = null;
        String status=null;
        try
        {
            String query = "SELECT 'X' FROM bin_details WHERE isFraud='Y' AND icicitransid=?";
            connection = Database.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, trackingId);
            Result = preparedStatement.executeQuery();
            while (Result.next())
            {
                status = Result.getString(1);
                if(status.equals("X")){
                    return true;
                }
            }

        }
        catch (Exception e)
        {
            logger.error("Exception:::::", e);
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return false;
    }

    public boolean getUploadBulkFraudReason(String trackingId, String fraudreason)
    {
        Connection con = null;
        boolean status=false;
        try
        {
            con = Database.getConnection();
            StringBuffer update =new StringBuffer("UPDATE bin_details SET isFraud='Y',fraudreason=? WHERE icicitransid IN ("+trackingId+")");
            PreparedStatement psUpdateTransaction =con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,fraudreason);
            int k=psUpdateTransaction.executeUpdate();
            if(k>0)
            {
                status=true;
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }
    public String getTrackingId(String paymentId)
    {
        Connection con = null;
        String trackingid=null;
        ResultSet rs=null;
        try
        {
            con=Database.getConnection();
            String update = "SELECT trackingid from transaction_common WHERE paymentid=?";
            PreparedStatement ps =con.prepareStatement(update);
            ps.setString(1,paymentId);
            rs=ps.executeQuery();
            if(rs.next())
            {
                trackingid=rs.getString("trackingid");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return trackingid;
    }

    public void addCustomerName(String customerName, Set<String> cardNum, Set<String> emailAddr,Set<String> phone)throws PZDBViolationException
    {
        Connection connection               = null;
        PreparedStatement preparedStatement = null;
        try{
            String query        = "SELECT DISTINCT Ccnum,emailaddr,telno FROM transaction_common WHERE NAME=? AND STATUS IN('capturesuccess','settled','chargeback','reversed')";
            connection          = Database.getConnection();
            preparedStatement   = connection.prepareStatement(query);
            preparedStatement.setString(1,customerName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next())
            {
                if(functions.isValueNull(resultSet.getString("Ccnum")) && !cardNum.contains(PzEncryptor.decryptPAN(resultSet.getString("Ccnum"))))
                    cardNum.add(PzEncryptor.decryptPAN(resultSet.getString("Ccnum")));
                if(functions.isValueNull(resultSet.getString("emailaddr")) && !emailAddr.contains(resultSet.getString("emailaddr")) && !" ".equalsIgnoreCase(resultSet.getString("emailaddr")))
                    emailAddr.add(resultSet.getString("emailaddr"));
                if(functions.isValueNull(resultSet.getString("telno")) && !phone.contains(resultSet.getString("telno")) && !" ".equalsIgnoreCase(resultSet.getString("telno")))
                    phone.add(resultSet.getString("telno"));
            }
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionDao.java","addCustomerNameBatch()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException sql)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionDao.java","addCustomerNameBatch()",null,"Common","DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY,null,sql.getMessage(),sql.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
    }
    public List<TransactionDetailsVO> getTransactionDetails(List<String> trackingId)throws PZDBViolationException
    {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        List<TransactionDetailsVO> transactionDetailsVOList=new ArrayList<>();
        TransactionDetailsVO transactionDetailsVO=null;
        try
        {
            String trackingIds = String.join(",", trackingId);
            String query="SELECT trackingid,name,amount,currency,status,toid FROM transaction_common WHERE trackingid in ("+trackingIds+")";
            connection=Database.getConnection();
            preparedStatement=connection.prepareStatement(query);
            ResultSet resultSet=preparedStatement.executeQuery();
            while(resultSet.next())
            {
                transactionDetailsVO=new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(resultSet.getString("trackingid"));
                transactionDetailsVO.setName(resultSet.getString("name"));
                transactionDetailsVO.setAmount(resultSet.getString("amount"));
                transactionDetailsVO.setCurrency(resultSet.getString("currency"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setToid(resultSet.getString("toid"));
                transactionDetailsVOList.add(transactionDetailsVO);
            }
        }
        catch (SystemError systemError){
            PZExceptionHandler.raiseDBViolationException("TransactionDao.java", "getTransactionDetails()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException sql){
            PZExceptionHandler.raiseDBViolationException("TransactionDao.java", "getTransactionDetails()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, sql.getMessage(), sql.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return transactionDetailsVOList;
    }
    public List<TransactionDetailsVO> getTransactionIdFromBin(String firstSix,String lastFour,String cardholderName)throws PZDBViolationException
    {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        List<TransactionDetailsVO> transactionDetailsVOList=new ArrayList<>();
        TransactionDetailsVO transactionDetailsVO=null;
        try
        {
            String query="SELECT t.trackingid,t.accountid,t.toid,t.amount,t.refundamount,t.captureamount,t.status,t.currency,t.name,t.emailAddr,t.description FROM transaction_common AS t,bin_details AS bd WHERE bd.first_six=? AND bd.last_four=? AND t.name=? AND t.status IN('capturesuccess','settled') AND t.trackingid=bd.icicitransid AND FROM_UNIXTIME(dtstamp) >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)";
            //String query="SELECT t.trackingid,t.accountid,t.toid,t.refundamount,t.captureamount,t.status FROM transaction_common AS t,bin_details AS bd WHERE bd.first_six=? AND bd.last_four=? AND t.name=? AND t.status IN('capturesuccess','settled') AND t.trackingid=bd.icicitransid /*AND FROM_UNIXTIME(dtstamp) >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)*/";
            connection=Database.getConnection();
            preparedStatement=connection.prepareStatement(query);
            preparedStatement.setString(1,firstSix);
            preparedStatement.setString(2,lastFour);
            preparedStatement.setString(3, cardholderName);
            logger.error("Query for getTransactionIdFromBin:::"+preparedStatement);
            ResultSet resultSet=preparedStatement.executeQuery();
            while(resultSet.next())
            {
                transactionDetailsVO=new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(resultSet.getString("trackingid"));
                transactionDetailsVO.setAccountId(resultSet.getString("accountid"));
                transactionDetailsVO.setToid(resultSet.getString("toid"));
                transactionDetailsVO.setAmount(resultSet.getString("amount"));
                transactionDetailsVO.setRefundAmount(resultSet.getString("refundamount"));
                transactionDetailsVO.setCaptureAmount(resultSet.getString("captureamount"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setCurrency(resultSet.getString("currency"));
                transactionDetailsVO.setName(resultSet.getString("name"));
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailAddr"));
                transactionDetailsVO.setDescription(resultSet.getString("description"));
                transactionDetailsVOList.add(transactionDetailsVO);
            }
        }
        catch (SystemError systemError){
            PZExceptionHandler.raiseDBViolationException("TransactionDao.java", "getTransactionDetails()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException sql){
            PZExceptionHandler.raiseDBViolationException("TransactionDao.java", "getTransactionDetails()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, sql.getMessage(), sql.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return transactionDetailsVOList;
    }

    public List<TransactionDetailsVO> getTransactionDetailsForCommon(List<String> trackingId)throws PZDBViolationException
    {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        List<TransactionDetailsVO> transactionDetailsVOList=new ArrayList<>();
        TransactionDetailsVO transactionDetailsVO=null;
        try
        {
            String trackingIds = String.join(",", trackingId);
            String query="SELECT trackingid,toid,name,amount,captureamount,currency,status,accountid,ipaddress,refundamount FROM transaction_common WHERE trackingid in ("+trackingIds+") AND STATUS IN('capturesuccess','settled','partialrefund','reversed')";
            connection=Database.getConnection();
            preparedStatement=connection.prepareStatement(query);
            ResultSet resultSet=preparedStatement.executeQuery();
            while(resultSet.next())
            {
                transactionDetailsVO=new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(resultSet.getString("trackingid"));
                transactionDetailsVO.setToid(resultSet.getString("toid"));
                transactionDetailsVO.setName(resultSet.getString("name"));
                transactionDetailsVO.setAmount(resultSet.getString("amount"));
                transactionDetailsVO.setCaptureAmount(resultSet.getString("captureamount"));
                transactionDetailsVO.setCurrency(resultSet.getString("currency"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setAccountId(resultSet.getString("accountid"));
                transactionDetailsVO.setIpAddress(resultSet.getString("ipaddress"));
                transactionDetailsVO.setRefundAmount(resultSet.getString("refundamount"));
                transactionDetailsVOList.add(transactionDetailsVO);
            }
        }
        catch (SystemError systemError){
            PZExceptionHandler.raiseDBViolationException("TransactionDao.java", "getTransactionDetails()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException sql){
            PZExceptionHandler.raiseDBViolationException("TransactionDao.java", "getTransactionDetails()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, sql.getMessage(), sql.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return transactionDetailsVOList;
    }

    public HashMap<String,TransactionVO> getMemberListFromTransaction(String memberid,String partnerName,String gateway,String currency,String startDate,String endDate)throws PZDBViolationException
    {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        //List<TransactionVO> transactionDetailsVOList=new ArrayList<>();
        HashMap<String,TransactionVO> getMemberHash=new HashMap<>();
        TransactionVO transactionDetailsVO=null;
        try
        {
            StringBuffer query=new StringBuffer("SELECT  DISTINCT t.toid as toid ,m.login as login,t.totype AS totype  FROM transaction_common AS t JOIN members AS m WHERE t.fromtype=? AND t.currency=? and FROM_UNIXTIME(t.dtstamp) >= ? AND FROM_UNIXTIME(t.dtstamp) <=? AND t.toid=m.memberid");
            connection=Database.getConnection();
            if(functions.isValueNull(partnerName)){
                query.append(" AND t.totype='"+partnerName+"'");
            }
            if(functions.isValueNull(memberid)){
                query.append(" AND m.memberid='"+memberid+"'");
            }
            preparedStatement=connection.prepareStatement(query.toString());
            preparedStatement.setString(1,gateway);
            preparedStatement.setString(2,currency);
            preparedStatement.setString(3,startDate);
            preparedStatement.setString(4,endDate);
            logger.error("Query1:::::"+preparedStatement);
            ResultSet resultSet=preparedStatement.executeQuery();
            while(resultSet.next())
            {
                transactionDetailsVO=new TransactionVO();
                transactionDetailsVO.setMemberId(resultSet.getString("toid"));
                transactionDetailsVO.setName(resultSet.getString("login"));
                transactionDetailsVO.setToType(resultSet.getString("totype"));
                //transactionDetailsVOList.add(transactionDetailsVO);
                getMemberHash.put(transactionDetailsVO.getMemberId(),transactionDetailsVO);
            }
            StringBuffer query2=new StringBuffer("SELECT  DISTINCT t.toid as toid ,m.login as login,t.totype AS totype  FROM transaction_common AS t JOIN members AS m WHERE t.fromtype=? AND t.currency=? and t.timestamp >= ? AND t.timestamp <=? AND t.toid=m.memberid");
            connection=Database.getConnection();
            if(functions.isValueNull(partnerName)){
                query2.append(" AND t.totype='"+partnerName+"'");
            }
            if(functions.isValueNull(memberid)){
                query2.append(" AND m.memberid='"+memberid+"'");
            }
            preparedStatement=connection.prepareStatement(query2.toString());
            preparedStatement.setString(1,gateway);
            preparedStatement.setString(2,currency);
            preparedStatement.setString(3,startDate);
            preparedStatement.setString(4,endDate);
            logger.error("Query2:::::"+preparedStatement);
            ResultSet resultSet2=preparedStatement.executeQuery();
            while(resultSet2.next())
            {
                transactionDetailsVO=new TransactionVO();
                transactionDetailsVO.setMemberId(resultSet2.getString("toid"));
                transactionDetailsVO.setName(resultSet2.getString("login"));
                transactionDetailsVO.setToType(resultSet2.getString("totype"));
                getMemberHash.put(transactionDetailsVO.getMemberId(),transactionDetailsVO);
                //transactionDetailsVOList.add(transactionDetailsVO);
            }
        }
        catch (SystemError systemError){
            PZExceptionHandler.raiseDBViolationException("TransactionDao.java", "getTransactionDetails()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException sql){
            PZExceptionHandler.raiseDBViolationException("TransactionDao.java", "getTransactionDetails()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, sql.getMessage(), sql.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return getMemberHash;
    }
    public HashMap<String,TransactionVO> getAccountIdListFromTransaction(String gateway,String currency,String startDate,String endDate)throws PZDBViolationException
    {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        HashMap<String,TransactionVO> getMemberHash=new HashMap<>();
        TransactionVO transactionDetailsVO=null;
        try
        {
            StringBuffer query=new StringBuffer("SELECT  DISTINCT accountid as accountid  FROM transaction_common  WHERE fromtype=? AND currency=? and FROM_UNIXTIME(dtstamp) >= ? AND FROM_UNIXTIME(dtstamp) <=?");
            connection=Database.getConnection();
            preparedStatement=connection.prepareStatement(query.toString());
            preparedStatement.setString(1,gateway);
            preparedStatement.setString(2,currency);
            preparedStatement.setString(3,startDate);
            preparedStatement.setString(4,endDate);
            logger.error("Query1:::::" + preparedStatement);
            ResultSet resultSet=preparedStatement.executeQuery();
            while(resultSet.next())
            {
                transactionDetailsVO=new TransactionVO();
                transactionDetailsVO.setAccountId(resultSet.getString("accountid"));
                getMemberHash.put(transactionDetailsVO.getAccountId(),transactionDetailsVO);
            }
            StringBuffer query2=new StringBuffer("SELECT  DISTINCT accountid FROM transaction_common WHERE fromtype=? AND currency=? and timestamp >= ? AND timestamp <=?");
            connection=Database.getConnection();
            preparedStatement=connection.prepareStatement(query2.toString());
            preparedStatement.setString(1,gateway);
            preparedStatement.setString(2,currency);
            preparedStatement.setString(3,startDate);
            preparedStatement.setString(4,endDate);
            logger.error("Query2:::::"+preparedStatement);
            ResultSet resultSet2=preparedStatement.executeQuery();
            while(resultSet2.next())
            {
                transactionDetailsVO=new TransactionVO();
                transactionDetailsVO.setAccountId(resultSet2.getString("accountid"));
                getMemberHash.put(transactionDetailsVO.getMemberId(), transactionDetailsVO);
            }
        }
        catch (SystemError systemError){
            PZExceptionHandler.raiseDBViolationException("TransactionDao.java", "getTransactionDetails()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException sql){
            PZExceptionHandler.raiseDBViolationException("TransactionDao.java", "getTransactionDetails()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, sql.getMessage(), sql.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return getMemberHash;
    }
    public boolean isValid(String trackingId, String paymentId)throws PZDBViolationException
    {
        Connection conn = null;
        boolean isValid =false;
        PreparedStatement preparedStatement=null;
        ResultSet result=null;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query =new StringBuffer("SELECT trackingid,paymentid FROM transaction_common WHERE trackingid=? AND paymentid=?");
            preparedStatement=conn.prepareStatement(query.toString());
            preparedStatement.setString(1,trackingId);
            preparedStatement.setString(2,paymentId);
            result = preparedStatement.executeQuery();
            if(result.next()){
                isValid  =true;
            }
        }
        catch (SQLException sql){
            logger.error("SQLException:::::", sql);
            PZExceptionHandler.raiseDBViolationException("TransactionDao.java", "isValid()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, sql.getMessage(), sql.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException("TransactionDao.java", "isValid()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return isValid ;
    }


    public TransactionDetailsVO getDetailFromCommonForAuthStarted(String trackingId)
    {
        Connection con = null;
        ResultSet resultSet = null;
        PreparedStatement psUpdateTransaction = null;
        boolean status=false;
        TransactionDetailsVO transactionDetailsVO=new TransactionDetailsVO();
        try
        {
            con=Database.getConnection();
            String update = "select * from transaction_common where trackingid=? AND STATUS='begun' ";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,trackingId);
            resultSet=psUpdateTransaction.executeQuery();
            if(resultSet.next())
            {
                transactionDetailsVO.setTrackingid(resultSet.getString("trackingid"));
                transactionDetailsVO.setAccountId(resultSet.getString("accountid"));
                transactionDetailsVO.setPaymodeId(resultSet.getString("paymodeid"));
                transactionDetailsVO.setCardTypeId(resultSet.getString("cardtypeid"));
                transactionDetailsVO.setToid(resultSet.getString("toid"));
                transactionDetailsVO.setTotype(resultSet.getString("totype"));//totype
                transactionDetailsVO.setFromid(resultSet.getString("fromid"));//fromid
                transactionDetailsVO.setFromtype(resultSet.getString("fromtype"));//fromtype
                transactionDetailsVO.setDescription(resultSet.getString("description"));
                transactionDetailsVO.setOrderDescription(resultSet.getString("orderdescription"));
                transactionDetailsVO.setTemplateamount(resultSet.getString("templateamount"));
                transactionDetailsVO.setAmount(resultSet.getString("amount"));
                transactionDetailsVO.setCurrency(resultSet.getString("currency"));
                transactionDetailsVO.setRedirectURL(resultSet.getString("redirecturl"));
                transactionDetailsVO.setNotificationUrl(resultSet.getString("notificationUrl"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setFirstName(resultSet.getString("firstname"));//fn
                transactionDetailsVO.setLastName(resultSet.getString("lastname"));//ln
                transactionDetailsVO.setName(resultSet.getString("name"));//name
                transactionDetailsVO.setCcnum(resultSet.getString("ccnum"));//ccnum
                transactionDetailsVO.setExpdate(resultSet.getString("expdate"));//expdt
                transactionDetailsVO.setCardtype(resultSet.getString("cardtype"));//cardtype
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setIpAddress(resultSet.getString("ipaddress"));
                transactionDetailsVO.setCountry(resultSet.getString("country"));//country
                transactionDetailsVO.setState(resultSet.getString("state"));//state
                transactionDetailsVO.setCity(resultSet.getString("city"));//city
                transactionDetailsVO.setStreet(resultSet.getString("street"));//street
                transactionDetailsVO.setZip(resultSet.getString("zip"));//zip
                transactionDetailsVO.setTelcc(resultSet.getString("telnocc"));//telcc
                transactionDetailsVO.setTelno(resultSet.getString("telno"));//telno
                transactionDetailsVO.setHttpHeader(resultSet.getString("httpheader"));//httpheadet
                transactionDetailsVO.setPaymentId(resultSet.getString("paymentid"));
                transactionDetailsVO.setTemplatecurrency(resultSet.getString("templatecurrency"));
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setCustomerId(resultSet.getString("customerId"));
                transactionDetailsVO.setEci(resultSet.getString("eci"));
                transactionDetailsVO.setTerminalId(resultSet.getString("terminalid"));
                transactionDetailsVO.setVersion(resultSet.getString("version"));
                transactionDetailsVO.setCaptureAmount(resultSet.getString("captureamount"));
                transactionDetailsVO.setEmiCount(resultSet.getString("emiCount"));
                transactionDetailsVO.setTransactionTime(resultSet.getString("timestamp"));
            }
            logger.debug("transaction common query----" + psUpdateTransaction);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeResultSet(resultSet);
            Database.closeConnection(con);
        }
        return transactionDetailsVO;
    }
    public List<MarketPlaceVO> getChildDetailsByParentTrackingid(String trackingId)
    {
        Connection con      = null;
        ResultSet resultSet = null;
        PreparedStatement psUpdateTransaction   = null;
        List<MarketPlaceVO> vendorsTracking     = new ArrayList<>();
        MarketPlaceVO marketPlaceVO             = new MarketPlaceVO();
        try
        {
            con                 = Database.getConnection();
            String update       = "select trackingid,amount,description,toid from transaction_common where parentTrackingid IS NOT NULL AND parentTrackingid!='' AND parentTrackingid=? ";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,trackingId);
            resultSet   = psUpdateTransaction.executeQuery();
            while(resultSet.next())
            {
                marketPlaceVO   = new MarketPlaceVO();
                marketPlaceVO.setTrackingid(resultSet.getString("trackingid"));
                marketPlaceVO.setAmount(resultSet.getString("amount"));
                marketPlaceVO.setOrderid(resultSet.getString("description"));
                marketPlaceVO.setMemberid(resultSet.getString("toid"));
                vendorsTracking.add(marketPlaceVO);
            }
            logger.error("transaction common query----" + psUpdateTransaction);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeResultSet(resultSet);
            Database.closeConnection(con);
        }
        return vendorsTracking;
    }

    public MarketPlaceVO getParentDetailsByChildTrackingid(String trackingId)
    {
        Connection con = null;
        ResultSet resultSet = null;
        PreparedStatement psUpdateTransaction = null;
        MarketPlaceVO marketPlaceVO=null;
        try
        {
            con=Database.getConnection();
            String update = "select trackingid,toid,captureamount,refundamount,status from transaction_common where trackingid=(select parentTrackingid from transaction_common where trackingid=?) ";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,trackingId);
            resultSet=psUpdateTransaction.executeQuery();
            if(resultSet.next())
            {
                marketPlaceVO=new MarketPlaceVO();
                marketPlaceVO.setTrackingid(resultSet.getString("trackingid"));
                marketPlaceVO.setCapturedAmount(resultSet.getString("captureamount"));
                marketPlaceVO.setReversedAmount(resultSet.getString("refundamount"));
                marketPlaceVO.setMemberid(resultSet.getString("toid"));
                marketPlaceVO.setStatus(resultSet.getString("status"));
            }
            logger.error("transaction common query----" + psUpdateTransaction);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeResultSet(resultSet);
            Database.closeConnection(con);
        }
        return marketPlaceVO;
    }
    public MarketPlaceVO getChildDetailsByChildTrackingid(String trackingId)
    {
        Connection con = null;
        ResultSet resultSet = null;
        PreparedStatement psUpdateTransaction = null;
        MarketPlaceVO marketPlaceVO=null;
        try
        {
            con=Database.getConnection();
            String update = "select trackingid,toid,captureamount,refundamount,status,parentTrackingid from transaction_common where trackingid=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,trackingId);
            resultSet=psUpdateTransaction.executeQuery();
            if(resultSet.next())
            {
                marketPlaceVO=new MarketPlaceVO();
                marketPlaceVO.setTrackingid(resultSet.getString("trackingid"));
                marketPlaceVO.setParentTrackingid(resultSet.getString("parentTrackingid"));
                marketPlaceVO.setCapturedAmount(resultSet.getString("captureamount"));
                marketPlaceVO.setReversedAmount(resultSet.getString("refundamount"));
                marketPlaceVO.setMemberid(resultSet.getString("toid"));
                marketPlaceVO.setStatus(resultSet.getString("status"));
            }
            logger.error("getChildDetailsByChildTrackingid----" + psUpdateTransaction);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::", e);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeResultSet(resultSet);
            Database.closeConnection(con);
        }
        return marketPlaceVO;
    }

    public CommTransactionDetailsVO getTransactionDetailFromCommonForQR(String trackingId)
    {
        logger.error("in getTransactionDetailFromCommonForQR");
        Connection con = null;
        ResultSet resultSet = null;
        PreparedStatement psUpdateTransaction = null;
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();

        try
        {
            con=Database.getConnection();
            String update = "select walletAmount, walletCurrency from transaction_common_details where trackingid=? AND status='authstarted' ";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,trackingId);
            resultSet=psUpdateTransaction.executeQuery();
            if(resultSet.next())
            {
                commTransactionDetailsVO.setWalletAmount(resultSet.getString("walletAmount"));
                commTransactionDetailsVO.setWalletCurrency(resultSet.getString("walletCurrency"));
            }
            logger.error("transaction common query----"+psUpdateTransaction);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::", e);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeResultSet(resultSet);
            Database.closeConnection(con);
        }
        return commTransactionDetailsVO;
    }
    public String getTransactionDetailIdFromTrackingId(String trackingId) throws PZDBViolationException
    {
        logger.error("in getTransactionDetaildFromTrackingId");
        Connection con = null;
        ResultSet resultSet = null;
        PreparedStatement psUpdateTransaction = null;
        String detailId="";

        try
        {
            con=Database.getConnection();
            String query = "select detailid from transaction_common_details where trackingid=? AND status in ('capturesuccess','authsuccessful') ORDER BY detailid desc LIMIT 1";
            psUpdateTransaction = con.prepareStatement(query.toString());
            psUpdateTransaction.setString(1,trackingId);
            resultSet=psUpdateTransaction.executeQuery();
            if(resultSet.next())
            {
                detailId=resultSet.getString("detailid");
            }
            logger.error("transaction common query----"+psUpdateTransaction);
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::",e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTransactionDetaildFromTrackingId()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ",se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTransactionDetaildFromTrackingId()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeResultSet(resultSet);
            Database.closeConnection(con);
        }
        return detailId;
    }
    public boolean updateTransactionReceiptByDetailId(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        logger.error("in getTransactionDetaildFromTrackingId");
        Connection con = null;
        PreparedStatement psUpdateTransaction = null;
        boolean isUpdate=false;
        try
        {
            byte[] receiptByte= Base64.decode(commonValidatorVO.getTransDetailsVO().getTransactionReceipt());
            con=Database.getConnection();
            String update = "update transaction_common_details set transactionReceiptImg = ? where detailId=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setBytes(1, receiptByte);
            psUpdateTransaction.setString(2,commonValidatorVO.getTransDetailsVO().getDetailId());
            logger.error("transaction common query----"+psUpdateTransaction);
            int i=psUpdateTransaction.executeUpdate();
            if(i>0)
            {
                isUpdate=true;
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::",e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTransactionDetaildFromTrackingId()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ",se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTransactionDetaildFromTrackingId()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeConnection(con);
        }
        return isUpdate;
    }

    public List<TransactionVO> getTransactionListForVirtualCheckout(CommonValidatorVO commonValidatorVO,String fdtstamp,String tdtstamp) throws PZDBViolationException
    {
        logger.debug("Entering getTransactionListForVirtualCheckout");

        Functions functions                         = new Functions();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        List<TransactionVO> transDetailVOList       = new ArrayList<TransactionVO>();
        StringBuffer query          = new StringBuffer();
        Connection conn             = null;
        List<String> trackingIdList = new ArrayList();
        PreparedStatement ps        = null;
        ResultSet queryRs           = null;
        Codec me                    = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        try
        {
            if (commonValidatorVO.getTimeZone() != null)
            {
                conn = applyTimeZone(commonValidatorVO.getTimeZone());
            }
            else
            {
                conn = Database.getConnection();
            }
            query.append("SELECT tc.toid,tc.amount,tc.trackingId,tc.status,tc.currency,tc.captureamount,tc.refundamount,tc.remark,tc.emailaddr,tc.timestamp,tc.firstname,tc.lastname,tc.country,tc.city,tc.state,tc.street,tc.zip,tc.telno,tc.telnocc,tc.payoutamount,tc.chargebackamount,first_six,last_four,tc.description,tc.terminalid, tc.paymentid, from_unixtime(tc.dtstamp) as transactionDate,tc.customerId,tc.paymodeid,tc.cardtype FROM transaction_common as tc left join bin_details as b on tc.trackingid=b.icicitransid WHERE tc.toid=?");

            if("chargeback".equalsIgnoreCase(commonValidatorVO.getStatus())|| ((commonValidatorVO.getTimeZone()!=null) || ((commonValidatorVO.getPaginationVO().getStartdate()!=null) && (commonValidatorVO.getPaginationVO().getEnddate()!=null))))
            {
                if (functions.isValueNull(fdtstamp))
                {
                    query.append(" and tc.timestamp >= '" + fdtstamp+"'");
                }
                if (functions.isValueNull(tdtstamp))
                {
                    query.append(" and tc.timestamp <= '" + tdtstamp+"'");
                }
            }else
            {
                if (functions.isValueNull(fdtstamp))
                {
                    query.append(" and tc.dtstamp >= " + fdtstamp);
                }
                if (functions.isValueNull(tdtstamp))
                {
                    query.append(" and tc.dtstamp <= " + tdtstamp);
                }
            }
            if (functions.isValueNull(genericTransDetailsVO.getCurrency()))
            {
                query.append(" and tc.currency = '" + genericTransDetailsVO.getCurrency() + "'");
            }
            if(functions.isValueNull(commonValidatorVO.getTrackingid()))
            {
                query.append(" and tc.trackingId = '"+commonValidatorVO.getTrackingid()+ "'");
            }
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail()))
            {
                query.append(" and tc.emailaddr = '"+commonValidatorVO.getAddressDetailsVO().getEmail()+ "'");
            }
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname()))
            {
                query.append(" and tc.firstname = '"+ ESAPI.encoder().encodeForSQL(me, commonValidatorVO.getAddressDetailsVO().getFirstname())+"'");
            }
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname()))
            {
                query.append(" and tc.lastname = '"+ESAPI.encoder().encodeForSQL(me,commonValidatorVO.getAddressDetailsVO().getLastname())+"'");
            }

            if(functions.isValueNull(commonValidatorVO.getStatus()))
            {
                query.append(" and tc.status = '"+commonValidatorVO.getStatus()+ "'");
            }

            if(functions.isValueNull(commonValidatorVO.getCardDetailsVO().getFirstSix()))
            {
                query.append(" and b.first_six = '"+commonValidatorVO.getCardDetailsVO().getFirstSix()+ "'");
            }

            if(functions.isValueNull(commonValidatorVO.getCardDetailsVO().getLastFour()))
            {
                query.append(" and b.last_four = '"+commonValidatorVO.getCardDetailsVO().getLastFour()+ "'");
            }
            query.append(" order by tc.trackingId desc");
            if(commonValidatorVO.getPaginationVO().getEnd()!=0)
            {
                query.append(" limit " + commonValidatorVO.getPaginationVO().getStart() + "," + commonValidatorVO.getPaginationVO().getEnd());
            }
            ps=conn.prepareStatement(query.toString());
            ps.setString(1, commonValidatorVO.getMerchantDetailsVO().getMemberId());
            logger.error("Transaction List ..." + ps);
            queryRs=ps.executeQuery();
            while (queryRs.next())
            {
                if(!trackingIdList.contains(queryRs.getString("trackingId")))
                {
                    trackingIdList.add(queryRs.getString("trackingId"));
                    TransactionVO transactionVO1 = new TransactionVO();
                    transactionVO1.setMemberId(queryRs.getString("toid"));
                    transactionVO1.setPaymentId(queryRs.getString("trackingId"));
                    transactionVO1.setOrderDesc(queryRs.getString("description"));
                    transactionVO1.setAmount(queryRs.getString("amount"));
                    transactionVO1.setTransactionStatus(queryRs.getString("status"));
                    transactionVO1.setCurrency(queryRs.getString("currency"));
                    transactionVO1.setCapAmount(queryRs.getString("captureamount"));
                    transactionVO1.setRefundAmount(queryRs.getString("refundamount"));
                    transactionVO1.setFirstName(queryRs.getString("firstname"));
                    transactionVO1.setLastName(queryRs.getString("lastname"));
                    transactionVO1.setTimestamp(queryRs.getString("timestamp"));
                    transactionVO1.setDtStamp(queryRs.getString("transactionDate"));
                    transactionVO1.setEmailAddr(queryRs.getString("emailaddr"));
                    transactionVO1.setRemark(queryRs.getString("remark"));
                    transactionVO1.setCountry(queryRs.getString("country"));
                    transactionVO1.setCity(queryRs.getString("city"));
                    transactionVO1.setState(queryRs.getString("state"));
                    transactionVO1.setStreet(queryRs.getString("street"));
                    transactionVO1.setZip(queryRs.getString("zip"));
                    transactionVO1.setTelnocc(queryRs.getString("telnocc"));
                    transactionVO1.setTelno(queryRs.getString("telno"));
                    transactionVO1.setFirstSix(queryRs.getString("first_six"));
                    transactionVO1.setLastFour(queryRs.getString("last_four"));
                    transactionVO1.setPayoutAmount(queryRs.getString("payoutamount"));
                    transactionVO1.setChargebackAmount(queryRs.getString("chargebackamount"));
                    transactionVO1.setBankReferenceId(queryRs.getString("tc.paymentid"));
                    transactionVO1.setTerminalId(queryRs.getString("tc.terminalid"));
                    transactionVO1.setCustomerId(queryRs.getString("tc.customerId"));
                    transactionVO1.setPaymodeid(queryRs.getString("tc.paymodeid"));
                    transactionVO1.setPaymentBrand(queryRs.getString("tc.cardtype"));

                    String queryDetail  = "select transactionReceiptImg from transaction_common_details where trackingId=? and transactionReceiptImg is not null and transactionReceiptImg!=''";
                    ps                  = conn.prepareStatement(queryDetail.toString());
                    ps.setString(1, queryRs.getString("trackingId"));
                    ResultSet rs    = ps.executeQuery();
                    if (rs.next())
                        transactionVO1.setTransactionReceiptImg(Base64.encode(rs.getBytes("transactionReceiptImg")));
                    else
                        transactionVO1.setTransactionReceiptImg("");
                    transDetailVOList.add(transactionVO1);
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::",e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTransactionListForVirtualCheckout()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ",se);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "getTransactionListForVirtualCheckout()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transDetailVOList;
    }

    public List<TransactionVO> transactionDetailsForAPIMemberUser(TransactionVO transactionVO,CommonValidatorVO commonValidatorVO, List<TransactionVO> transDetailVOList,String fdtstamp,String tdtstamp)
    {
        logger.debug("Entering listInvoices");

        Functions functions = new Functions();
        GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = new GenericCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        //  List<TransactionVO> transDetailVOLists = new ArrayList<TransactionVO>();

        StringBuffer query = new StringBuffer();
        Connection conn=null;

        try
        {
            conn=Database.getRDBConnection();
            query.append("SELECT COUNT(*) AS COUNT ,SUM(tc.amount)AS AMOUNT ,SUM(tc.captureamount)AS CAMOUNT,SUM(tc.refundamount)AS RAMOUNT,SUM(tc.chargebackamount) AS CBAMOUNT,tc.STATUS,tc.toid,tc.currency,tc.timestamp,tc.dtstamp FROM transaction_common AS tc,invoice AS iv WHERE tc.trackingid=iv.trackingid ");

            if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId()));
            {
                query.append("and tc.toid ='" + commonValidatorVO.getMerchantDetailsVO().getMemberId()+ "'");
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and tc.dtstamp >= " + fdtstamp);

            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and tc.dtstamp <= " + tdtstamp);

            }
            if (functions.isValueNull(genericTransDetailsVO.getCurrency()))
            {
                query.append(" and tc.currency = '" + genericTransDetailsVO.getCurrency() + "'");
            }

            if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getLogin()))
            {
                query.append("and raisedBy = '" +commonValidatorVO.getMerchantDetailsVO().getLogin() + "'");
            }

            query.append(" GROUP BY tc.currency,tc.STATUS ");


            logger.debug("query..." + query);

            logger.debug("Fetch records from transaction ");
            ResultSet queryRs = Database.executeQuery(query.toString(),conn);
            while (queryRs.next())
            {
                TransactionVO transactionVO1 = new TransactionVO();
                transactionVO1.setMemberId(queryRs.getString("toid"));
                transactionVO1.setAmount(queryRs.getString("AMOUNT"));
                transactionVO1.setCapAmount(queryRs.getString("CAMOUNT"));
                transactionVO1.setRefundAmount(queryRs.getString("RAMOUNT"));
                transactionVO1.setChargebackAmount(queryRs.getString("CBAMOUNT"));
                transactionVO1.setCurrency(queryRs.getString("currency"));
                transactionVO1.setTransactionStatus(queryRs.getString("STATUS"));
                transactionVO1.setCounts(queryRs.getString("count"));
                transactionVO1.setTimestamp(queryRs.getString("timestamp"));
                transactionVO1.setDtStamp(queryRs.getString("dtstamp"));

                transDetailVOList.add(transactionVO1);
            }
        }
        catch (SystemError e)
        {
            logger.error("SystemError Exception leaving listTransactions", e);
        }
        catch (SQLException se)
        {
            logger.error("SQL Exception leaving listTransactions", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        logger.debug("Leaving listTransactions  "+query.toString());
        return transDetailVOList;
    }

    public TransactionDetailsVO getDetailFraudDefender(CommonValidatorVO commonValidatorVO)
    {
        log.error("inside  getDetailFraudDefender------------------> ");
        Connection con = null;
        ResultSet resultSet = null;
        PreparedStatement psUpdateTransaction = null;
        boolean status=false;
        TransactionDetailsVO transactionDetailsVO=null ;

        Functions functions1 =new Functions();
        try
        {
            con=Database.getConnection();
            StringBuffer  update=new StringBuffer("select t.trackingid,t.accountid,t.terminalid,t.toid,t.totype,t.fromid,t.fromtype,t.description,t.orderdescription,t.amount,t.currency,t.status,t.firstname,t.lastname,t.name,t.emailaddr,t.country,t.state,t.city,street,t.zip,t.telnocc,t.telno,t.paymentid,t.ccnum,t.customerId,t.captureamount,t.refundamount,t.chargebackamount,t.timestamp,t.customerIp,bd.first_six,bd.last_four,t.cardtype from transaction_common as t, bin_details as bd where trackingid>0");
            update.append(" and t.trackingid = bd.icicitransid ");
            if(functions1.isValueNull(commonValidatorVO.getTransDetailsVO().getPurchase_identifier())){

                update.append(" and t.paymentid='"+commonValidatorVO.getTransDetailsVO().getPurchase_identifier()+"'");

            }
            if(functions1.isValueNull(commonValidatorVO.getTransDetailsVO().getMerchant_id())){

                update.append(" and t.fromid='"+commonValidatorVO.getTransDetailsVO().getMerchant_id()+"'");

            }
            if(functions1.isValueNull(commonValidatorVO.getTransDetailsVO().getAmount())){

                update.append(" and t.amount='"+commonValidatorVO.getTransDetailsVO().getAmount()+"'");

            }
            if(functions1.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency())){

                update.append(" and t.currency='"+commonValidatorVO.getTransDetailsVO().getCurrency()+"'");

            }

            if(functions1.isValueNull(commonValidatorVO.getTransDetailsVO().getTransactionDate())){

                SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d=dateFormat.parse(commonValidatorVO.getTransDetailsVO().getTransactionDate());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(d);
                calendar.add(Calendar.HOUR_OF_DAY, -24);
                long startDate=calendar.getTime().getTime()/1000;
                calendar.add(Calendar.HOUR_OF_DAY, 48);
                long endDate=calendar.getTime().getTime()/1000;

                update.append(" and t.dtstamp  >'"+startDate+"' and t.dtstamp<'"+endDate+"'");

            }

            if(functions1.isValueNull(commonValidatorVO.getTransDetailsVO().getTransactionType())){
                String Ttype=commonValidatorVO.getTransDetailsVO().getTransactionType();
                String transactionType=functions.getTransactionType(Ttype);
                update.append(" and t.status in ("+transactionType+")");
            }

            if(functions1.isValueNull(commonValidatorVO.getTransDetailsVO().getPersonalAccountNumber())){
                String firstsix=functions.getFirstSix(commonValidatorVO.getTransDetailsVO().getPersonalAccountNumber());
                String lastFour=functions.getLastFour(commonValidatorVO.getTransDetailsVO().getPersonalAccountNumber());

                update.append(" and bd.first_six='"+firstsix+"'");

                update.append(" and bd.last_four='"+lastFour+"'");
            }
            if(functions1.isValueNull(commonValidatorVO.getTransDetailsVO().getRrn())){
                update.append(" and (t.rrn = '"+commonValidatorVO.getTransDetailsVO().getRrn()+"'");
                update.append(" or t.rrn is null or t.rrn='')");
            }
            if(functions1.isValueNull(commonValidatorVO.getTransDetailsVO().getAuthorization_code())){
                update.append(" and t.authorization_code = '"+commonValidatorVO.getTransDetailsVO().getAuthorization_code()+"'");
            }
            update.append(" order by t.timestamp desc");

            psUpdateTransaction = con.prepareStatement(update.toString());
            log.error("datebase query------------------------------------->"+ psUpdateTransaction);
            resultSet=psUpdateTransaction.executeQuery();

            while(resultSet.next())
            {
                transactionDetailsVO =new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(resultSet.getString("trackingid"));
                transactionDetailsVO.setAccountId(resultSet.getString("accountid"));
                transactionDetailsVO.setTerminalId(resultSet.getString("terminalid"));
                transactionDetailsVO.setToid(resultSet.getString("toid"));
                transactionDetailsVO.setTotype(resultSet.getString("totype"));//totype
                transactionDetailsVO.setFromid(resultSet.getString("fromid"));//fromid
                transactionDetailsVO.setFromtype(resultSet.getString("fromtype"));//fromtype
                transactionDetailsVO.setDescription(resultSet.getString("description"));
                transactionDetailsVO.setOrderDescription(resultSet.getString("orderdescription"));
                transactionDetailsVO.setAmount(resultSet.getString("amount"));
                transactionDetailsVO.setCurrency(resultSet.getString("currency"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setFirstName(resultSet.getString("firstname"));//fn
                transactionDetailsVO.setLastName(resultSet.getString("lastname"));//ln
                transactionDetailsVO.setName(resultSet.getString("name"));//name
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setCountry(resultSet.getString("country"));//country
                transactionDetailsVO.setState(resultSet.getString("state"));//state
                transactionDetailsVO.setCity(resultSet.getString("city"));//city
                transactionDetailsVO.setStreet(resultSet.getString("street"));//street
                transactionDetailsVO.setZip(resultSet.getString("zip"));//zip
                transactionDetailsVO.setTelcc(resultSet.getString("telnocc"));//telcc
                transactionDetailsVO.setTelno(resultSet.getString("telno"));//telno
                transactionDetailsVO.setPaymentId(resultSet.getString("paymentid"));
                transactionDetailsVO.setCustomerId(resultSet.getString("customerId"));
                transactionDetailsVO.setCaptureAmount(resultSet.getString("captureamount"));
                transactionDetailsVO.setRefundAmount(resultSet.getString("refundamount"));
                transactionDetailsVO.setChargebackAmount(resultSet.getString("chargebackamount"));
                transactionDetailsVO.setTransactionTime(resultSet.getString("timestamp"));
                transactionDetailsVO.setIpAddress(resultSet.getString("customerIp"));
                transactionDetailsVO.setFirstSix(resultSet.getString("first_six"));
                transactionDetailsVO.setLastFour(resultSet.getString("last_four"));
                if(functions.isValueNull(resultSet.getString("ccnum")))
                    transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(resultSet.getString("ccnum")));
                else
                    transactionDetailsVO.setCcnum("");
                transactionDetailsVO.setCardtype(resultSet.getString("cardtype"));

            }
            logger.debug("transaction common query----"+psUpdateTransaction);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
            log.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
            log.error("SQLException:::::::",e);
        }
        catch (ParseException e)
        {
            logger.error("ParseException:::::::",e);
            log.error("ParseException:::::::", e);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeResultSet(resultSet);
            Database.closeConnection(con);
        }
        return transactionDetailsVO;
    }

    public TransactionDetailsVO getDetailFraudDefenderForICard(CommonValidatorVO commonValidatorVO)
    {
        log.error("inside  getDetailFraudDefenderForICard------------------> ");
        Connection con = null;
        ResultSet resultSet = null;
        PreparedStatement psUpdateTransaction = null;
        boolean status=false;
        TransactionDetailsVO transactionDetailsVO=null ;
        String trackingId="";
        Date transactionDate=null;
        Date transactionDate2=null;
        Functions functions1 =new Functions();
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getTransactionDate()))
            {
                try
                {
                    transactionDate = dateFormat.parse(commonValidatorVO.getTransDetailsVO().getTransactionDate());
                    transactionDate2 = dateFormat.parse("2020-05-27");
                }
                catch (ParseException e)
                {
                    log.error("ParseException e-->",e);
                }
            }
            con=Database.getConnection();
            log.error("transactionDate----->"+transactionDate);
            if(transactionDate !=null && transactionDate.after(transactionDate2) && (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getAuthorization_code()) || functions.isValueNull(commonValidatorVO.getTransDetailsVO().getRrn())))
            {
                StringBuffer icardQuery = new StringBuffer("SELECT `trackingid` FROM `transaction_icard_details` WHERE trackingId>0");
                if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getRrn()))
                    icardQuery.append(" AND`rrn`='"+commonValidatorVO.getTransDetailsVO().getRrn()+"'");
                if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getAuthorization_code()))
                {
                    icardQuery.append(" AND `approval`='" + commonValidatorVO.getTransDetailsVO().getAuthorization_code() + "'");
                }
                psUpdateTransaction=con.prepareStatement(icardQuery.toString());
                log.error("ICard detail table query --->"+psUpdateTransaction);
                resultSet=psUpdateTransaction.executeQuery();
                if(resultSet.next())
                {
                    trackingId=resultSet.getString("trackingid");
                }
            }
            psUpdateTransaction=null;
            resultSet=null;
            StringBuffer  query=new StringBuffer("SELECT t.trackingid,t.accountid,t.terminalid,t.toid,t.totype,t.fromid,t.fromtype,t.description,t.orderdescription,t.amount,t.currency,t.status,t.firstname,t.lastname,t.name,t.emailaddr,t.country,t.state,t.city,street,t.zip,t.telnocc,t.telno,t.ccnum,t.paymentid,t.customerId,t.captureamount,t.refundamount,t.chargebackamount,t.timestamp,t.customerIp,bd.first_six,bd.last_four FROM transaction_common AS t, transaction_common_details AS td,bin_details AS bd WHERE t.trackingid=td.trackingid AND t.trackingid = bd.icicitransid AND  t.amount=? AND t.currency=?");
            if(functions1.isValueNull(commonValidatorVO.getTransDetailsVO().getPurchase_identifier())){

                query.append(" and t.paymentid='"+commonValidatorVO.getTransDetailsVO().getPurchase_identifier()+"'");

            }
            if(functions1.isValueNull(trackingId)){

                query.append(" and t.trackingId='"+trackingId+"'");

            }
            if(functions1.isValueNull(commonValidatorVO.getTransDetailsVO().getAuthorization_code())){

                query.append(" and td.responsehashinfo='"+commonValidatorVO.getTransDetailsVO().getAuthorization_code()+"'");

            }

            if(functions1.isValueNull(commonValidatorVO.getTransDetailsVO().getTransactionDate())){

                query.append(" and td.timestamp like '"+commonValidatorVO.getTransDetailsVO().getTransactionDate()+"%'");

            }

            if(functions1.isValueNull(commonValidatorVO.getTransDetailsVO().getTransactionType())){
                String transStatus=functions.getTransactionType(commonValidatorVO.getTransDetailsVO().getTransactionType());


                query.append(" and td.status='"+transStatus+"'");

            }

            if(functions1.isValueNull(commonValidatorVO.getTransDetailsVO().getPersonalAccountNumber())){
                String firstsix=functions.getFirstSix(commonValidatorVO.getTransDetailsVO().getPersonalAccountNumber());
                String lastFour=functions.getLastFour(commonValidatorVO.getTransDetailsVO().getPersonalAccountNumber());

                query.append(" and bd.first_six='"+firstsix+"'");

                query.append(" and bd.last_four='"+lastFour+"'");



            }

            psUpdateTransaction = con.prepareStatement(query.toString());
            psUpdateTransaction.setString(1,commonValidatorVO.getTransDetailsVO().getAmount());
            psUpdateTransaction.setString(2,commonValidatorVO.getTransDetailsVO().getCurrency());
            log.error("datebase query--getDetailFraudDefenderForICard------->"+ psUpdateTransaction);
            resultSet=psUpdateTransaction.executeQuery();
            while(resultSet.next())
            {
                transactionDetailsVO =new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(resultSet.getString("trackingid"));
                transactionDetailsVO.setAccountId(resultSet.getString("accountid"));
                transactionDetailsVO.setTerminalId(resultSet.getString("terminalid"));
                transactionDetailsVO.setToid(resultSet.getString("toid"));
                transactionDetailsVO.setTotype(resultSet.getString("totype"));//totype
                transactionDetailsVO.setFromid(resultSet.getString("fromid"));//fromid
                transactionDetailsVO.setFromtype(resultSet.getString("fromtype"));//fromtype
                transactionDetailsVO.setDescription(resultSet.getString("description"));
                transactionDetailsVO.setOrderDescription(resultSet.getString("orderdescription"));
                transactionDetailsVO.setAmount(resultSet.getString("amount"));
                transactionDetailsVO.setCurrency(resultSet.getString("currency"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setFirstName(resultSet.getString("firstname"));//fn
                transactionDetailsVO.setLastName(resultSet.getString("lastname"));//ln
                transactionDetailsVO.setName(resultSet.getString("name"));//name
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setCountry(resultSet.getString("country"));//country
                transactionDetailsVO.setState(resultSet.getString("state"));//state
                transactionDetailsVO.setCity(resultSet.getString("city"));//city
                transactionDetailsVO.setStreet(resultSet.getString("street"));//street
                transactionDetailsVO.setZip(resultSet.getString("zip"));//zip
                transactionDetailsVO.setTelcc(resultSet.getString("telnocc"));//telcc
                transactionDetailsVO.setTelno(resultSet.getString("telno"));//telno
                transactionDetailsVO.setPaymentId(resultSet.getString("paymentid"));
                transactionDetailsVO.setCustomerId(resultSet.getString("customerId"));
                transactionDetailsVO.setCaptureAmount(resultSet.getString("captureamount"));
                transactionDetailsVO.setRefundAmount(resultSet.getString("refundamount"));
                transactionDetailsVO.setChargebackAmount(resultSet.getString("chargebackamount"));
                transactionDetailsVO.setTransactionTime(resultSet.getString("timestamp"));
                transactionDetailsVO.setIpAddress(resultSet.getString("customerIp"));
                transactionDetailsVO.setFirstSix(resultSet.getString("first_six"));
                transactionDetailsVO.setLastFour(resultSet.getString("last_four"));
                if(functions.isValueNull(resultSet.getString("ccnum")))
                    transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(resultSet.getString("ccnum")));
                else
                    transactionDetailsVO.setCcnum("");

            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
            log.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
            log.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeResultSet(resultSet);
            Database.closeConnection(con);
        }
        return transactionDetailsVO;
    }
    public String insertFraudDefenderDetails(CommonValidatorVO commonValidatorVO)
    {
        String fraudId="";
        Connection con=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        try
        {
            con=Database.getConnection();
            String query="INSERT INTO `fraud_defender_details` (`personalAccountNumber`,`amount`,`currency`,`transactionType`,`transactionDate`,`merchant_id`,`authorization_code`,`rrn`,`arn`,`dtstamp`,`call_type`) VALUES(?,?,?,?,?,?,?,?,?,UNIX_TIMESTAMP(NOW()),?)";
            ps=con.prepareStatement(query);
            ps.setString(1,commonValidatorVO.getTransDetailsVO().getPersonalAccountNumber());
            ps.setString(2,commonValidatorVO.getTransDetailsVO().getAmount());
            ps.setString(3,commonValidatorVO.getTransDetailsVO().getCurrency());
            ps.setString(4, commonValidatorVO.getTransDetailsVO().getTransactionType());
            ps.setString(5,commonValidatorVO.getTransDetailsVO().getTransactionDate());
            ps.setString(6,commonValidatorVO.getTransDetailsVO().getMerchant_id());
            ps.setString(7,commonValidatorVO.getTransDetailsVO().getAuthorization_code());
            ps.setString(8,commonValidatorVO.getTransDetailsVO().getRrn());
            ps.setString(9,commonValidatorVO.getTransDetailsVO().getArn());
            ps.setString(10,commonValidatorVO.getTransDetailsVO().getCall_type());
            log.error("insertFraudDefenderDetails--->"+ps);
            int i=ps.executeUpdate();
            rs=ps.getGeneratedKeys();
            if(rs.next())
            {
                fraudId=rs.getString(1);
            }

        }
        catch (SystemError systemError)
        {
            log.error("SystemError -->",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException -->",e);
        }
        return fraudId;
    }
    public boolean updateFraudTransactionDetails(CommonValidatorVO commonValidatorVO,TransactionDetailsVO transactionDetailsVO)
    {
        Connection con=null;
        PreparedStatement ps=null;
        boolean isUpdate=false;
        try
        {
            con=Database.getConnection();
            String updateQuery="UPDATE `fraud_defender_details` SET `trackingId`=?,`paymentid`=?,`accountId`=?,`terminalId`=?,`isRecordFound`=?,`toid`=?,`cardType`=?,`partnerid`=? WHERE `id`=?";
            ps=con.prepareStatement(updateQuery);
            ps.setString(1,transactionDetailsVO.getTrackingid());
            ps.setString(2,transactionDetailsVO.getPaymentId());
            ps.setString(3,transactionDetailsVO.getAccountId());
            ps.setString(4,transactionDetailsVO.getTerminalId());
            if(functions.isValueNull(transactionDetailsVO.getTrackingid()))
                ps.setString(5,"Y");
            else
                ps.setString(5,"N");
            ps.setString(6,transactionDetailsVO.getToid());
            ps.setString(7,transactionDetailsVO.getCardtype());
            ps.setString(8,commonValidatorVO.getParetnerId());
            ps.setString(9,commonValidatorVO.getFraudId());
            log.error("updateFraudTransactionDetails--->"+ps);
            int i=ps.executeUpdate();
            if(i>0)
                isUpdate=true;

        }
        catch (SystemError systemError)
        {
            log.error("SystemError------>",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException------>",e);
        }
        return isUpdate;
    }
    public boolean updateRefundFraudTransactionDetails(String trackingId,String isRefund,String refundAmount)
    {
        Connection con=null;
        PreparedStatement ps=null;
        boolean isUpdate=false;
        try
        {
            con=Database.getConnection();
            String updateQuery="UPDATE `fraud_defender_details` SET `isRefunded`=?,`refund_amount`=? WHERE `trackingId`=?";
            ps=con.prepareStatement(updateQuery);
            ps.setString(1,isRefund);
            ps.setString(2,refundAmount);
            ps.setString(3,trackingId);
            log.error("updateRefundFraudTransactionDetails--->" + ps);
            int i=ps.executeUpdate();
            if(i>0)
                isUpdate=true;

        }
        catch (SystemError systemError)
        {
            log.error("SystemError------>", systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException------>",e);
        }
        return isUpdate;
    }
    public boolean updateQueryRefundFraudTransactionDetails(String fraudId,String isRefund,String refundAmount)
    {
        Connection con=null;
        PreparedStatement ps=null;
        boolean isUpdate=false;
        try
        {
            con=Database.getConnection();
            String updateQuery="UPDATE `fraud_defender_details` SET `isRefunded`=?,`refund_amount`=? WHERE `id`=?";
            ps=con.prepareStatement(updateQuery);
            ps.setString(1,isRefund);
            ps.setString(2,refundAmount);
            ps.setString(3,fraudId);
            log.error("updateQueryRefundFraudTransactionDetails--->" + ps);
            int i=ps.executeUpdate();
            if(i>0)
                isUpdate=true;

        }
        catch (SystemError systemError)
        {
            log.error("SystemError------>",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException------>",e);
        }
        return isUpdate;
    }

    public boolean isTransactionDetailInserted(String trackingId, String status,String amount,String actionexecutorid,String actionexecutorname)
    {
        Connection cn = null;
        PreparedStatement pstmt = null;
        String actionStatus="";
        String role="Admin";
        boolean inserted =false;
        if(status.equals("capturesuccess")){
            actionStatus = ActionEntry.ACTION_CAPTURE_SUCCESSFUL;
        }
        else if(status.equals("settled")){
            actionStatus = ActionEntry.ACTION_CREDIT;
        }
        else if(status.equals("capturestarted")){
            actionStatus = ActionEntry.ACTION_CAPTURE_STARTED;
        }else if(status.equals("authcancelled")){
            actionStatus = ActionEntry.ACTION_AUTHORISTION_CANCLLED;
        }
        else if(status.equals("failed")){
            actionStatus = ActionEntry.ACTION_FAILED;
        }else if(status.equals("authfailed")){
            actionStatus = ActionEntry.ACTION_AUTHORISTION_FAILED;
        }else if(status.equals("authsuccessful")){
            actionStatus = ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL;
        }else if(status.equals("authstarted")){
            actionStatus = ActionEntry.ACTION_AUTHORISTION_STARTED;
        }else if(status.equals("reversed")){
            actionStatus = ActionEntry.ACTION_REVERSAL_SUCCESSFUL;
        }else if(status.equals("markedforreversal")){
            actionStatus = ActionEntry.ACTION_REVERSAL_REQUEST_SENT;
        }else
        {
            actionStatus = status;
        }

        try
        {
            cn = Database.getConnection();
            String sql = "insert into transaction_common_details(trackingid,amount,action,status,actionexecutorid,actionexecutorname) values (?,?,?,?,?,?)";
            pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            pstmt.setString(2, amount);
            pstmt.setString(3, actionStatus);
            pstmt.setString(4, status);
            pstmt.setString(5, actionexecutorid);
            pstmt.setString(6, actionexecutorname);
            int k=pstmt.executeUpdate();
            if(k>0)
            {
                inserted = true;
            }
            /*rs = pstmt.getGeneratedKeys();
            if (rs.next())
            {
                newDetailId = rs.getInt(1);
            }*/
        }
        catch (Exception e)
        {
            logger.error("Exception:::::", e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(cn);
        }
        return inserted;
    }

    public boolean getUpdateStatus(String trackingId, String paymentId, String remark, String status)
    {
        Connection con = null;
        boolean Update=false;
        try
        {
            if(!functions.isValueNull(paymentId))
            {
                paymentId = "";
            }
            con = Database.getConnection();
            StringBuffer update =new StringBuffer("UPDATE transaction_common SET status=? ,paymentid=? ,remark=? where trackingid=? ");
            PreparedStatement psUpdateTransaction =con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1, status);
            psUpdateTransaction.setString(2, paymentId);
            psUpdateTransaction.setString(3, remark);
            psUpdateTransaction.setString(4, trackingId);

            int k=psUpdateTransaction.executeUpdate();
            if(k>0)
            {
                Update=true;
            }
        }
        catch (SystemError systemError)
        {

            logger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return Update;
    }

    public boolean isAuthstarted(String trackingId)throws PZDBViolationException
    {
        Connection conn = null;
        boolean isAuthstarted =false;
        PreparedStatement preparedStatement=null;
        ResultSet result=null;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query =new StringBuffer("SELECT status FROM transaction_common WHERE trackingid=? AND (STATUS='authstarted' OR STATUS='authstarted_3D')");
            preparedStatement=conn.prepareStatement(query.toString());
            preparedStatement.setString(1,trackingId);
            result = preparedStatement.executeQuery();
            if(result.next()){
                isAuthstarted  =true;
            }
        }
        catch (SQLException sql){
            logger.error("SQLException:::::", sql);
            PZExceptionHandler.raiseDBViolationException("TransactionDao.java", "isAuthstarted()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, sql.getMessage(), sql.getCause());
        }
        catch (SystemError systemError){
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException("TransactionDao.java", "isAuthstarted()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return isAuthstarted ;
    }

    public int getCountfraudTransactionListNew(String toid, String partnerid,String mid,String trackingid,String terminalId,String transactionType,String amount,String refundamount, String firstsix,String lastfour,  String tdtstamp, String fdtstamp, int records, int pageno,String currency, String accountid,String paymentid,String dataType,String isRefund,String rrn,String authCode)
    {
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer query = null;
        int totalrecords=0;
        Connection conn = null;
        Functions functions = new Functions();
        try
        {
            conn = Database.getRDBConnection();
            query=new StringBuffer("SELECT id,trackingId,paymentid,accountId,terminalId,personalAccountNumber,amount,refund_amount,currency,transactionType,transactionDate,merchant_id,authorization_code,rrn,arn,isRecordFound,isRefunded,TIMESTAMP,dtstamp,toid,call_type,cardType,partnerid FROM fraud_defender_details WHERE 1>0 AND isRecordFound='Y'");
            if (functions.isValueNull(toid))
            {
                query.append(" and toid='" +ESAPI.encoder().encodeForSQL(me, toid) + "'");
            }
            if (functions.isValueNull(partnerid))
            {
                query.append(" and partnerid='" +ESAPI.encoder().encodeForSQL(me, partnerid) + "'");
            }
            if (functions.isValueNull(paymentid))
            {
                query.append(" and paymentid='" +ESAPI.encoder().encodeForSQL(me, paymentid) + "'");
            }
            if (functions.isValueNull(terminalId))
            {
                query.append(" and terminalId='" +ESAPI.encoder().encodeForSQL(me, terminalId) + "'");
            }
            if (functions.isValueNull(mid))
            {
                query.append(" and merchant_id='" +ESAPI.encoder().encodeForSQL(me, mid) + "'");
            }
            if (functions.isValueNull(rrn))
            {
                query.append(" and rrn='" +ESAPI.encoder().encodeForSQL(me, rrn) + "'");
            }
            if (functions.isValueNull(isRefund))
            {
                query.append(" and isRefunded='" +ESAPI.encoder().encodeForSQL(me, isRefund) + "'");
            }
            if (functions.isValueNull(amount))
            {
                query.append(" and amount='" +ESAPI.encoder().encodeForSQL(me, amount) + "'");
            }
            if (functions.isValueNull(refundamount))
            {
                query.append(" and refund_amount='" +ESAPI.encoder().encodeForSQL(me, refundamount) + "'");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and currency='" +ESAPI.encoder().encodeForSQL(me, currency) + "'");
            }
            if (functions.isValueNull(transactionType))
            {
                query.append(" and transactionType='" +ESAPI.encoder().encodeForSQL(me, transactionType) + "'");
            }
            if (functions.isValueNull(firstsix) && functions.isValueNull(lastfour))
            {
                query.append(" and personalAccountNumber='" +ESAPI.encoder().encodeForSQL(me, firstsix) +"xxxxxx"+ESAPI.encoder().encodeForSQL(me, lastfour)+ "'");
            }
            if (functions.isValueNull(accountid))
            {
                query.append(" and accountId='" +ESAPI.encoder().encodeForSQL(me, accountid) + "'");
            }
            if (functions.isValueNull(refundamount))
            {
                query.append(" and refund_amount='" +ESAPI.encoder().encodeForSQL(me, refundamount) + "'");
            }
            if (functions.isValueNull(authCode))
            {
                query.append(" and authorization_code='" +ESAPI.encoder().encodeForSQL(me, authCode) + "'");
            }

            if ("TIMESTAMP".equals(dataType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                query.append(" and timestamp >= '" + startDate + "'");
            }
            else
            {
                query.append(" and dtstamp >= " + fdtstamp);
            }
            if ("TIMESTAMP".equals(dataType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter1.format(calendar.getTime());
                query.append(" and timestamp <= '" + endDate + "'");
            }
            else
            {
                query.append(" and dtstamp <= " + tdtstamp);
            }

            if (functions.isValueNull(trackingid))
            {
                query.append(" and trackingId IN (" + trackingid + ")");
            }
            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");
            Date date5= new Date();
            logger.error("fraudTransactionListPartner count query starts######## "+date5.getTime());
            ResultSet rs = Database.executeQuery(countquery.toString(), conn);
            logger.error("fraudTransactionListPartner count query ends######## "+new Date().getTime());
            logger.error("fraudTransactionListPartner count query diff######## "+(new Date().getTime()-date5.getTime()));
            rs.next();
            totalrecords = rs.getInt(1);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError::::: ",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::: ",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return totalrecords;
    }
    public Hashtable fraudTransactionListPartner(String toid, String partnerid,String mid,String trackingid,String terminalId,String transactionType,String amount,String refundamount, String firstsix,String lastfour,  String tdtstamp, String fdtstamp, int records, int pageno,String currency, String accountid,String paymentid,String dataType,String isRefund,String rrn,String authCode,int totalrecords)
    {
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer query = null;
        Hashtable hash = null;
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
        Connection conn = null;
        Functions functions = new Functions();
        try
        {
            conn = Database.getRDBConnection();
            query=new StringBuffer("SELECT id,trackingId,paymentid,accountId,terminalId,personalAccountNumber,amount,refund_amount,currency,transactionType,transactionDate,merchant_id,authorization_code,rrn,arn,isRecordFound,isRefunded,TIMESTAMP,dtstamp,toid,call_type,cardType,partnerid FROM fraud_defender_details WHERE 1>0 AND isRecordFound='Y'");
            if (functions.isValueNull(toid))
            {
                query.append(" and toid='" +ESAPI.encoder().encodeForSQL(me, toid) + "'");
            }
            if (functions.isValueNull(partnerid))
            {
                query.append(" and partnerid='" +ESAPI.encoder().encodeForSQL(me, partnerid) + "'");
            }
            if (functions.isValueNull(paymentid))
            {
                query.append(" and paymentid='" +ESAPI.encoder().encodeForSQL(me, paymentid) + "'");
            }
            if (functions.isValueNull(terminalId))
            {
                query.append(" and terminalId='" +ESAPI.encoder().encodeForSQL(me, terminalId) + "'");
            }
            if (functions.isValueNull(mid))
            {
                query.append(" and merchant_id='" +ESAPI.encoder().encodeForSQL(me, mid) + "'");
            }
            if (functions.isValueNull(rrn))
            {
                query.append(" and rrn='" +ESAPI.encoder().encodeForSQL(me, rrn) + "'");
            }
            if (functions.isValueNull(isRefund))
            {
                query.append(" and isRefunded='" +ESAPI.encoder().encodeForSQL(me, isRefund) + "'");
            }
            if (functions.isValueNull(amount))
            {
                query.append(" and amount='" +ESAPI.encoder().encodeForSQL(me, amount) + "'");
            }
            if (functions.isValueNull(refundamount))
            {
                query.append(" and refund_amount='" +ESAPI.encoder().encodeForSQL(me, refundamount) + "'");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and currency='" +ESAPI.encoder().encodeForSQL(me, currency) + "'");
            }
            if (functions.isValueNull(transactionType))
            {
                query.append(" and transactionType='" +ESAPI.encoder().encodeForSQL(me, transactionType) + "'");
            }
            if (functions.isValueNull(firstsix) && functions.isValueNull(lastfour))
            {
                query.append(" and personalAccountNumber='" +ESAPI.encoder().encodeForSQL(me, firstsix) +"xxxxxx"+ESAPI.encoder().encodeForSQL(me, lastfour)+ "'");
            }
            if (functions.isValueNull(accountid))
            {
                query.append(" and accountId='" +ESAPI.encoder().encodeForSQL(me, accountid) + "'");
            }
            if (functions.isValueNull(refundamount))
            {
                query.append(" and refund_amount='" +ESAPI.encoder().encodeForSQL(me, refundamount) + "'");
            }
            if (functions.isValueNull(authCode))
            {
                query.append(" and authorization_code='" +ESAPI.encoder().encodeForSQL(me, authCode) + "'");
            }

            if ("TIMESTAMP".equals(dataType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                query.append(" and timestamp >= '" + startDate + "'");
            }
            else
            {
                query.append(" and dtstamp >= " + fdtstamp);
            }
            if ("TIMESTAMP".equals(dataType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter1.format(calendar.getTime());
                query.append(" and timestamp <= '" + endDate + "'");
            }
            else
            {
                query.append(" and dtstamp <= " + tdtstamp);
            }

            if (functions.isValueNull(trackingid))
            {
                query.append(" and trackingId IN (" + trackingid + ")");
            }

            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");
            query.append(" order by id DESC");
            query.append(" limit " + start + "," + end);

            logger.debug("===count query===" + countquery);
            logger.debug("===query===" + query);

            Date date4= new Date();
            logger.error("fraudTransactionListPartner query starts########  "+date4.getTime());
            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            logger.error("fraudTransactionListPartner query ends########  "+new Date().getTime());
            logger.error("fraudTransactionListPartner query diff########  "+(new Date().getTime()-date4.getTime()));
           /* ResultSet rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);*/

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

        }
        catch (SQLException se)
        {
            logger.error("SQLException----",se);
        }
        catch (SystemError se)
        {
            logger.error("SystemError----",se);
        }
        catch (Exception se)
        {
            logger.error("Exception----",se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }
    public Hashtable listTransactionsFraudAlertPartnerExportInExcel(String toid,String partnerid, String mid,String trackingid,String terminalId,String transactionType, String amount, String refundamount, String firstfourofccnum, String lastfourofccnum, String tdtstamp, String fdtstamp,String currency, String accountid,String paymentid, String dateType,String isRefund,String rrn,String authCode)
    {
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer query = null;
        Hashtable hash = null;
        Connection conn = null;
        Functions functions = new Functions();
        try
        {
            conn = Database.getRDBConnection();
            query=new StringBuffer("SELECT id,trackingId,paymentid,accountId,terminalId,personalAccountNumber,amount,refund_amount,currency,transactionType,transactionDate,merchant_id,authorization_code,rrn,arn,isRecordFound,isRefunded,TIMESTAMP,dtstamp,toid,call_type,cardType,partnerid  FROM fraud_defender_details WHERE 1>0 AND isRecordFound='Y'");
            if (functions.isValueNull(toid))
            {
                query.append(" and toid='" +ESAPI.encoder().encodeForSQL(me, toid) + "'");
            }
            if (functions.isValueNull(partnerid))
            {
                query.append(" and partnerid='" +ESAPI.encoder().encodeForSQL(me, partnerid) + "'");
            }
            if (functions.isValueNull(paymentid))
            {
                query.append(" and paymentid='" +ESAPI.encoder().encodeForSQL(me, paymentid) + "'");
            }
            if (functions.isValueNull(terminalId))
            {
                query.append(" and terminalId='" +ESAPI.encoder().encodeForSQL(me, terminalId) + "'");
            }
            if (functions.isValueNull(mid))
            {
                query.append(" and merchant_id='" +ESAPI.encoder().encodeForSQL(me, mid) + "'");
            }
            if (functions.isValueNull(rrn))
            {
                query.append(" and rrn='" +ESAPI.encoder().encodeForSQL(me, rrn) + "'");
            }
            if (functions.isValueNull(isRefund))
            {
                query.append(" and isRefunded='" +ESAPI.encoder().encodeForSQL(me, isRefund) + "'");
            }
            if (functions.isValueNull(amount))
            {
                query.append(" and amount='" +ESAPI.encoder().encodeForSQL(me, amount) + "'");
            }
            if (functions.isValueNull(refundamount))
            {
                query.append(" and refund_amount='" +ESAPI.encoder().encodeForSQL(me, refundamount) + "'");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and currency='" +ESAPI.encoder().encodeForSQL(me, currency) + "'");
            }
            if (functions.isValueNull(transactionType))
            {
                query.append(" and transactionType='" +ESAPI.encoder().encodeForSQL(me, transactionType) + "'");
            }
            if (functions.isValueNull(firstfourofccnum) && functions.isValueNull(lastfourofccnum))
            {
                query.append(" and personalAccountNumber='" +ESAPI.encoder().encodeForSQL(me, firstfourofccnum) +"xxxxxx"+ESAPI.encoder().encodeForSQL(me, lastfourofccnum)+ "'");
            }
            if (functions.isValueNull(accountid))
            {
                query.append(" and accountId='" +ESAPI.encoder().encodeForSQL(me, accountid) + "'");
            }
            if (functions.isValueNull(terminalId))
            {
                query.append(" and terminalId='" +ESAPI.encoder().encodeForSQL(me, terminalId) + "'");
            }
            if (functions.isValueNull(refundamount))
            {
                query.append(" and refund_amount='" +ESAPI.encoder().encodeForSQL(me, refundamount) + "'");
            }
            if (functions.isValueNull(paymentid))
            {
                query.append(" and paymentid ='" +paymentid.trim()+ "'");
            }
            if (functions.isValueNull(authCode))
            {
                query.append(" and authorization_code='" +ESAPI.encoder().encodeForSQL(me, authCode) + "'");
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                query.append(" and timestamp >= '" + startDate + "'");
            }
            else
            {
                query.append(" and dtstamp >= " + fdtstamp);
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter1.format(calendar.getTime());
                query.append(" and timestamp <= '" + endDate + "'");
            }
            else
            {
                query.append(" and dtstamp <= " + tdtstamp);
            }

            if (functions.isValueNull(trackingid))
            {
                query.append(" and trackingId IN (" + trackingid + ")");
            }

            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");
            query.append(" order by id DESC");

            logger.debug("===count query EXCEL===" + countquery);
            logger.debug("===query for EXCEL==="+query);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            ResultSet rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

        }
        catch (SQLException se)
        {
            logger.error("SQLException----",se);
        }
        catch (SystemError se)
        {
            logger.error("SystemError----",se);
        }
        catch (Exception se)
        {
            logger.error("Exception----",se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public String getTerminalId(String accountid, String memberid)
    {
        Connection con = null;
        String terminalid=null;
        ResultSet rs=null;
        try
        {
            con=Database.getConnection();
            String update = "SELECT terminalid from member_account_mapping WHERE accountid=? and memberid=?";
            PreparedStatement ps =con.prepareStatement(update);
            ps.setString(1,accountid);
            ps.setString(2,memberid);
            rs=ps.executeQuery();
            if(rs.next())
            {
                terminalid=rs.getString("terminalid");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return terminalid;
    }

    public HashMap cardPresentlistTrackingIds(String toid, String trackingid, String name, String desc, String orderdesc, String amount, String refundamount, String firstfourofccnum, String lastfourofccnum, String emailaddr, String tdtstamp, String fdtstamp, String status, int records, int pageno, String perfectmatch, boolean archive, Set<String> gatewayTypeSet, String remark, String cardtype, String issuing_bank, String statusFlag, String gateway_name, String currency, String accountid,String paymentid, String dateType,String customerid,String telno, String telnocc,String totype)
    {
        logger.debug("Entering cardPresentlistTransactions ");
        Codec me            = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        String tablename    = "";
        String fields       = "";
        String pRefund      = "false";
        StringBuffer query  = new StringBuffer();
        HashMap hash        = null;
        int start           = 0; // start index
        int end             = 0; // end index
        start               = (pageno - 1) * records;
        end                 = records;
        Connection conn     = null;
        Functions functions = new Functions();
        try
        {
            conn                = Database.getRDBConnection();
            Set<String> tables  = Database.getTableSet(gatewayTypeSet);
            tablename           = "transaction_card_present";
            if (archive)
            {
                tablename = "transaction_common_archive";
            }

            fields      = "trackingid as trackingid";

            query.append("select " + fields + " from " + tablename + " where ");
            //query.append("select " + fields + " from " + tablename + " where trackingid>0 ");

            if ( "TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" " + tablename + ".transactionTime >= '" + startDate + "'");
            }else if(functions.isValueNull(fdtstamp)){
                query.append(" " + tablename + ".dtstamp >=" + fdtstamp);
            }else if(!functions.isValueNull(fdtstamp) &&  functions.isValueNull(trackingid)){
                query.append(" " + tablename + ".trackingid =" + trackingid);
            }

            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {

                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and " + tablename + ".transactionTime <= '" + endDate + "'");
            }else if(functions.isValueNull(fdtstamp)){
                query.append("and " + tablename + ".dtstamp <=" + tdtstamp);
            }

            if (functions.isValueNull(toid))
            {
                query.append(" and toid='" +ESAPI.encoder().encodeForSQL(me, toid) + "'");
            }
            if (functions.isValueNull(totype))
            {
                query.append(" and totype='" +ESAPI.encoder().encodeForSQL(me, totype) + "'");
            }
            if (functions.isValueNull(status))
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status  = "reversed";
                    pRefund = "true";
                }
                query.append(" and status='" + status + "'");

            }
            if (functions.isValueNull(amount))
            {
                query.append(" and amount='" +ESAPI.encoder().encodeForSQL(me, amount) + "'");
            }
            if (functions.isValueNull(gateway_name))
            {
                query.append(" and fromtype='" +ESAPI.encoder().encodeForSQL(me, gateway_name) + "'");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and currency='" +ESAPI.encoder().encodeForSQL(me, currency) + "'");
            }
            if (functions.isValueNull(accountid))
            {
                query.append(" and "+tablename+".accountid='" +ESAPI.encoder().encodeForSQL(me, accountid) + "'");
            }
            if (functions.isValueNull(refundamount))
            {
                query.append(" and refundamount='" +ESAPI.encoder().encodeForSQL(me, refundamount) + "'");
            }
            if (functions.isValueNull(emailaddr))
            {
                query.append(" and "+tablename+".emailaddr='"+ emailaddr + "'");
            }
            if (functions.isValueNull(telnocc))
            {
                query.append(" and "+tablename+".telnocc='" + telnocc + "'");
            }
            if (functions.isValueNull(telno))
            {
                query.append(" and "+tablename+".telno='"+ telno + "'");
            }
            if (functions.isValueNull(issuing_bank))
            {
                query.append(" and issuing_bank ='" +ESAPI.encoder().encodeForSQL(me, issuing_bank )+ "'");
            }
            if (functions.isValueNull(paymentid))
            {
                query.append(" and paymentid ='" +paymentid.trim()+ "'");
            }
            if (functions.isValueNull(name))
            {
                if (perfectmatch == null)
                {
                    query.append(" and name like '%" + ESAPI.encoder().encodeForSQL(me,name) + "%'");
                }
                else
                {
                    query.append(" and name='" +ESAPI.encoder().encodeForSQL(me, name) + "'");
                }
            }
            /*if ( functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" and " + tablename + ".transactionTime >= '" + startDate + "'");
            }

            if (functions.isValueNull(tdtstamp))
            {

                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and " + tablename + ".transactionTime <= '" + endDate + "'");
            }*/

            if (functions.isValueNull(desc))
            {
                if (perfectmatch == null)
                {
                    query.append(" and description like '%" + ESAPI.encoder().encodeForSQL(me,desc) + "%'");
                }
                else
                {
                    query.append(" and description='" + ESAPI.encoder().encodeForSQL(me,desc) + "'");
                }
            }
            if (functions.isValueNull(orderdesc))
            {
                if (perfectmatch == null)
                {
                    query.append(" and orderdescription like '%" +ESAPI.encoder().encodeForSQL(me, orderdesc) + "%'");
                }
                else
                {
                    query.append(" and orderdescription='" + ESAPI.encoder().encodeForSQL(me,orderdesc) + "'");
                }
            }
            if (functions.isValueNull(trackingid))
            {
                query.append(" and " + tablename + ".trackingid IN (" + trackingid + ")");
            }
            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and captureamount > refundamount and STATUS='reversed'");
            }
            if (functions.isValueNull(remark))
            {
                if (perfectmatch == null)
                {
                    query.append(" and remark like '%" + ESAPI.encoder().encodeForSQL(me,remark) + "%'");
                }
                else
                {
                    query.append(" and remark='" +ESAPI.encoder().encodeForSQL(me, remark) + "'");
                }
            }
            if (functions.isValueNull(customerid))
            {
                query.append(" and customerId ='" +customerid.trim()+ "'");
            }

            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");
            query.append(" order by  dtstamp DESC,trackingid ");
            //query.append(" limit " + start + "," + end);

            //logger.debug("===count query===" + countquery);
            logger.debug("===query==="+query);

            hash            = Database.getHashMapFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
           /* ResultSet rs    = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));*/

        }
        catch (SQLException se)
        {
            logger.error("SQLException----",se);
        }
        catch (SystemError se)
        {
            logger.error("SystemError----",se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        logger.debug("Leaving cardPresentlistTransactionIds");
        return hash;
    }

    public HashMap listTrackingIds(String toid, String trackingid, String name, String desc, String orderdesc, String amount, String refundamount, String firstfourofccnum, String lastfourofccnum, String emailaddr, String tdtstamp, String fdtstamp, String status, int records, int pageno, String perfectmatch, boolean archive, Set<String> gatewayTypeSet, String remark, String cardtype, String issuing_bank, String statusFlag, String gateway_name, String currency, String accountid,String paymentid, String dateType ,String arn,String rrn,String authcode,String customerId,String transactionMode,String telno, String telnocc,String totype)
    {
        Codec me            = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        String tablename    = "";
        String fields       = "";
        String pRefund      = "false";
        StringBuffer query  = new StringBuffer();
        HashMap hash        = null;
        int start           = 0; // start index
        int end             = 0; // end index
        start               = (pageno - 1) * records;
        end                 = records;
        Connection conn     = null;
        Functions functions = new Functions();
        try
        {
            conn = Database.getRDBConnection();
            //  Set<String> tables = Database.getTableSet(gatewayTypeSet);
            /*Iterator i = tables.iterator();
            while(i.hasNext())
            {*/
            tablename = "transaction_common";
            if (archive)
            {
                //tablename = "transaction_icicicredit_archive";
                tablename = "transaction_common_archive";
            }

            fields = "trackingid as trackingid";

            query.append("select " + fields + " from " + tablename);
            if(!"begun".equalsIgnoreCase(status) && (functions.isValueNull(firstfourofccnum) || functions.isValueNull(lastfourofccnum) || functions.isValueNull(cardtype) || functions.isValueNull(issuing_bank)))
            {
                logger.error("inside if condition of bin_details..");
                query.append(" JOIN bin_details on " + tablename + ".trackingid = bin_details.icicitransid");
            }
            query.append(" where ");
            //query.append(" where trackingid>0 ");
            // }
            //dateconversion
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds       = Long.parseLong(fdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" " + tablename + ".timestamp >= '" + startDate + "'");
            }
            else if(functions.isValueNull(fdtstamp))
            {
                query.append(" " + tablename+ ".dtstamp >= " + fdtstamp);
            } else if(!functions.isValueNull(fdtstamp) && functions.isValueNull(trackingid))
            {
                //on Inquiry
                query.append(" " + tablename+ ".trackingid = " + trackingid);
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and " + tablename + ".timestamp <= '" + endDate + "'");
            }
            else if (functions.isValueNull(tdtstamp))
            {
                query.append(" and " + tablename+ ".dtstamp <= " + tdtstamp);
            }
            if (functions.isValueNull(toid))
            {
                query.append(" and toid='" +ESAPI.encoder().encodeForSQL(me, toid) + "'");
            }
            if (functions.isValueNull(totype))
            {
                query.append(" and totype='" +ESAPI.encoder().encodeForSQL(me, totype) + "'");
            }
            if (functions.isValueNull(status))
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status  = "reversed";
                    pRefund = "true";
                }
                query.append(" and status='" + status + "'");

            }
            if (!"all".equalsIgnoreCase(statusFlag) && !"begun".equalsIgnoreCase(status))
            {
                query.append(" and bin_details."+statusFlag+"='Y'");
            }
            if (functions.isValueNull(amount))
            {
                query.append(" and amount='" +ESAPI.encoder().encodeForSQL(me, amount) + "'");
            }
            if (functions.isValueNull(gateway_name))
            {
                query.append(" and fromtype='" +ESAPI.encoder().encodeForSQL(me, gateway_name) + "'");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and currency='" +ESAPI.encoder().encodeForSQL(me, currency) + "'");
            }
            if (functions.isValueNull(accountid))
            {
                query.append(" and "+tablename+".accountid='" +ESAPI.encoder().encodeForSQL(me, accountid) + "'");
            }
            if (functions.isValueNull(refundamount))
            {
                query.append(" and refundamount='" +ESAPI.encoder().encodeForSQL(me, refundamount) + "'");
            }
            if (functions.isValueNull(emailaddr))
            {
                query.append(" and "+tablename+".emailaddr='"+ emailaddr + "'");
            }
            if (functions.isValueNull(telnocc))
            {
                query.append(" and "+tablename+".telnocc='"+ telnocc + "'");
            }
            if (functions.isValueNull(telno))
            {
                query.append(" and "+tablename+".telno='"+ telno + "'");
            }
            if(functions.isValueNull(firstfourofccnum) && !"begun".equalsIgnoreCase(status))
            {
                query.append(" and bin_details.first_six ='" +ESAPI.encoder().encodeForSQL(me, firstfourofccnum) + "'");
            }
            if (functions.isValueNull(lastfourofccnum) && !"begun".equalsIgnoreCase(status))
            {
                query.append(" and bin_details.last_four ='" +ESAPI.encoder().encodeForSQL(me, lastfourofccnum )+ "'");
            }
            if (functions.isValueNull(cardtype) && !"begun".equalsIgnoreCase(status))
            {
                query.append(" and ( bin_brand ='" +ESAPI.encoder().encodeForSQL(me, cardtype )+ "'  OR   cardtype='"+ESAPI.encoder().encodeForSQL(me, cardtype )+"')");
            }
            if (functions.isValueNull(issuing_bank) && !"begun".equalsIgnoreCase(status))
            {
                query.append(" and issuing_bank ='" +ESAPI.encoder().encodeForSQL(me, issuing_bank )+ "'");
            }
            if (functions.isValueNull(paymentid))
            {
                query.append(" and paymentid ='" +paymentid.trim()+ "'");
            }
            if (functions.isValueNull(arn))
            {
                query.append(" and arn ='" +arn+ "'");
            }
            if (functions.isValueNull(rrn))
            {
                query.append(" and rrn ='" +rrn+ "'");
            }
            if (functions.isValueNull(authcode))
            {
                query.append(" and authorization_code ='" +authcode+ "'");
            }
            if (functions.isValueNull(name))
            {
                if (perfectmatch == null)
                {
                    query.append(" and name like '%" + ESAPI.encoder().encodeForSQL(me,name) + "%'");
                }
                else
                {
                    query.append(" and name='" +ESAPI.encoder().encodeForSQL(me, name) + "'");
                }
            }
            /*//dateconversion
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" and " + tablename + ".timestamp >= '" + startDate + "'");
            }

            else if(functions.isValueNull(fdtstamp))
            {
                query.append(" and " + tablename+ ".dtstamp >= " + fdtstamp);
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {

                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and " + tablename + ".timestamp <= '" + endDate + "'");
            }
            else if (functions.isValueNull(tdtstamp))
            {
                query.append(" and " + tablename+ ".dtstamp <= " + tdtstamp);
            }*/
            if (functions.isValueNull(desc))
            {
                if (perfectmatch == null)
                {
                    if(desc.contains(",")){
                        query.append(" and description IN (" + desc + ")");
                    }else{
                        query.append(" and description like '" + ESAPI.encoder().encodeForSQL(me,desc) + "%'");
                    }

                }
                else
                {
                    query.append(" and description='" + ESAPI.encoder().encodeForSQL(me,desc) + "'");
                }
            }
            if (functions.isValueNull(orderdesc))
            {
                if (perfectmatch == null)
                {
                    query.append(" and orderdescription like '" +ESAPI.encoder().encodeForSQL(me, orderdesc) + "%'");
                }
                else
                {
                    query.append(" and orderdescription='" + ESAPI.encoder().encodeForSQL(me,orderdesc) + "'");
                }
            }
            if (functions.isValueNull(trackingid))
            {
                query.append(" and " + tablename + ".trackingid IN (" + trackingid + ")");

            }
            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and captureamount > refundamount and STATUS='reversed'");
            }
            if (functions.isValueNull(remark))
            {
                if (perfectmatch == null)
                {
                    query.append(" and remark like '%" + ESAPI.encoder().encodeForSQL(me,remark) + "%'");
                }
                else
                {
                    query.append(" and remark='" +ESAPI.encoder().encodeForSQL(me, remark) + "'");
                }
            }
            if (functions.isValueNull(customerId))
            {
                query.append(" and customerId ='" +customerId+ "'");
            }
            if (functions.isValueNull(transactionMode))
            {
                query.append(" and transaction_mode ='" +transactionMode.trim()+ "'");
            }

            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");
            query.append(" order by  dtstamp DESC,trackingid ");
            //query.append(" limit " + start + "," + end);

           // logger.error("===count query===" + countquery);
            logger.error("===query===" + query);
            logger.error("QUERY FOR listTrackingIds method++++++ "+query);
            hash = Database.getHashMapFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            /*ResultSet rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));*/

        }
        catch (SQLException se)
        {
            logger.error("SQLException----",se);
        }
        catch (SystemError se)
        {
            logger.error("SystemError----",se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public HashMap listTrackingidsBasedOnDetailStatus(String toid, String trackingid, String name, String desc, String orderdesc, String amount, String refundamount, String firstfourofccnum, String lastfourofccnum, String emailaddr, String tdtstamp, String fdtstamp, String status, int records, int pageno, String perfectmatch, boolean archive, Set<String> gatewayTypeSet, String remark, String cardtype, String issuing_bank, String statusFlag, String gateway_name, String currency, String accountid,String paymentid, String dateType ,String arn,String rrn,String authcode,String detailStatus,String customerId,String transactionMode, String telno,String telnocc,String totype)
    {
        Codec me            = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        String tablename    = "";
        String fields       = "";
        String pRefund      = "false";
        StringBuffer query  = new StringBuffer();
        HashMap hash        = null;
        int start           = 0; // start index
        int end             = 0; // end index
        start               = (pageno - 1) * records;
        end                 = records;
        Connection conn     = null;
        Functions functions = new Functions();
        try
        {
            conn        = Database.getRDBConnection();
            tablename   = "transaction_common";
            query       = new StringBuffer("SELECT tcd.trackingid FROM transaction_common AS t JOIN transaction_common_details AS tcd");

            if(!"begun".equalsIgnoreCase(status))
                query.append(" JOIN bin_details as bd");
            query.append(" WHERE t.trackingid=tcd.trackingid");
            if(!"begun".equalsIgnoreCase(status))
                query.append(" AND tcd.trackingid=bd.icicitransid ");
            if (functions.isValueNull(toid))
            {
                query.append(" and t.toid='" +ESAPI.encoder().encodeForSQL(me, toid) + "'");
            }
            if (functions.isValueNull(totype))
            {
                query.append(" and t.totype='" +ESAPI.encoder().encodeForSQL(me, totype) + "'");
            }
            if (functions.isValueNull(status))
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status = "reversed";
                    pRefund = "true";
                }
                query.append(" and t.status='" + status + "'");

            }

            if (functions.isValueNull(amount))
            {
                query.append(" and t.amount='" +ESAPI.encoder().encodeForSQL(me, amount) + "'");
            }
            if (functions.isValueNull(gateway_name))
            {
                query.append(" and t.fromtype='" +ESAPI.encoder().encodeForSQL(me, gateway_name) + "'");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and t.currency='" +ESAPI.encoder().encodeForSQL(me, currency) + "'");
            }
            if (functions.isValueNull(accountid))
            {
                query.append(" and t.accountid='" +ESAPI.encoder().encodeForSQL(me, accountid) + "'");
            }
            if (functions.isValueNull(refundamount))
            {
                query.append(" and t.refundamount='" +ESAPI.encoder().encodeForSQL(me, refundamount) + "'");
            }
            if (functions.isValueNull(emailaddr))
            {
                query.append(" and t.emailaddr='"+ emailaddr + "'");
            }
            if (functions.isValueNull(telnocc))
            {
                query.append(" and t.telnocc='" + telnocc + "'");
            }
            if (functions.isValueNull(telno))
            {
                query.append(" and t.telno='" + telno + "'");
            }
            if(functions.isValueNull(firstfourofccnum) && !"begun".equalsIgnoreCase(status))
            {
                query.append(" and bd.first_six ='" +ESAPI.encoder().encodeForSQL(me, firstfourofccnum) + "'");
            }
            if (functions.isValueNull(lastfourofccnum) && !"begun".equalsIgnoreCase(status))
            {
                query.append(" and bd.last_four ='" +ESAPI.encoder().encodeForSQL(me, lastfourofccnum )+ "'");
            }
            if (functions.isValueNull(cardtype))
            {
                query.append(" and (t.bin_brand ='" +ESAPI.encoder().encodeForSQL(me, cardtype )+ "'  OR  cardtype='"+ESAPI.encoder().encodeForSQL(me, cardtype )+ "')");
            }
            if (functions.isValueNull(issuing_bank))
            {
                query.append(" and t.issuing_bank ='" +ESAPI.encoder().encodeForSQL(me, issuing_bank )+ "'");
            }
            if (functions.isValueNull(paymentid))
            {
                query.append(" and t.paymentid ='" +paymentid.trim()+ "'");
            }
            if (functions.isValueNull(arn))
            {
                query.append(" and t.arn ='" +arn+ "'");
            }
            if (functions.isValueNull(rrn))
            {
                query.append(" and t.rrn ='" +rrn+ "'");
            }
            if (functions.isValueNull(authcode))
            {
                query.append(" and t.authorization_code ='" +authcode+ "'");
            }
            if (functions.isValueNull(name))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.name like '%" + ESAPI.encoder().encodeForSQL(me,name) + "%'");
                }
                else
                {
                    query.append(" and t.name='" +ESAPI.encoder().encodeForSQL(me, name) + "'");
                }
            }
            //dateconversion
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds       = Long.parseLong(fdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" and t.timestamp >= '" + startDate + "'");
            }

            else if(functions.isValueNull(fdtstamp))
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
            else if (functions.isValueNull(tdtstamp))
            {
                query.append(" and t.dtstamp <= " + tdtstamp);
            }
            if (functions.isValueNull(desc))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.description like '" + ESAPI.encoder().encodeForSQL(me,desc) + "%'");
                }
                else
                {
                    query.append(" and t.description='" + ESAPI.encoder().encodeForSQL(me,desc) + "'");
                }
            }
            if (functions.isValueNull(orderdesc))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.orderdescription like '" +ESAPI.encoder().encodeForSQL(me, orderdesc) + "%'");
                }
                else
                {
                    query.append(" and t.orderdescription='" + ESAPI.encoder().encodeForSQL(me,orderdesc) + "'");
                }
            }
            if (functions.isValueNull(trackingid))
            {
                query.append(" and t.trackingid IN (" + trackingid + ")");
            }
            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and t.captureamount > t.refundamount and t.STATUS='reversed'");
            }
            if (functions.isValueNull(remark))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.remark like '%" + ESAPI.encoder().encodeForSQL(me,remark) + "%'");
                }
                else
                {
                    query.append(" and t.remark='" +ESAPI.encoder().encodeForSQL(me, remark) + "'");
                }
            }
            if (functions.isValueNull(detailStatus))
            {
                query.append(" and tcd.status='" +ESAPI.encoder().encodeForSQL(me, detailStatus) + "'");
            }
            if (functions.isValueNull(customerId))
            {
                query.append(" and t.customerId ='" +customerId+ "'");
            }
            if (functions.isValueNull(transactionMode))
            {
                query.append(" and t.transaction_mode ='" +transactionMode+ "'");
            }
            query.append(" order by  t.dtstamp DESC,t.trackingid ");
            //query.append(" limit " + start + "," + end);
            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");


            //logger.error("===count query===" + countquery);
            logger.error("===query===" + query);

            hash            = Database.getHashMapFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
           /* ResultSet rs    = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));*/

        }
        catch (SQLException se)
        {
            logger.error("SQLException----",se);
        }
        catch (SystemError se)
        {
            logger.error("SystemError----",se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }
    public TransactionDetailsVO getDetailFromCommonForJPBank(String customerId)
    {
        Connection con = null;
        ResultSet resultSet = null;
        PreparedStatement psUpdateTransaction = null;
        boolean status=false;
        TransactionDetailsVO transactionDetailsVO=null;
        try
        {
            con=Database.getConnection();
            String update = "select * from transaction_common where customerId=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,customerId);
            logger.error("getDetailFromCommonForJPBank transaction common query----"+psUpdateTransaction);
            resultSet=psUpdateTransaction.executeQuery();
            if(resultSet.next())
            {
                transactionDetailsVO=new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(resultSet.getString("trackingid"));
                transactionDetailsVO.setAccountId(resultSet.getString("accountid"));
                transactionDetailsVO.setPaymodeId(resultSet.getString("paymodeid"));
                transactionDetailsVO.setCardTypeId(resultSet.getString("cardtypeid"));
                transactionDetailsVO.setToid(resultSet.getString("toid"));
                transactionDetailsVO.setTotype(resultSet.getString("totype"));//totype
                transactionDetailsVO.setFromid(resultSet.getString("fromid"));//fromid
                transactionDetailsVO.setFromtype(resultSet.getString("fromtype"));//fromtype
                transactionDetailsVO.setDescription(resultSet.getString("description"));
                transactionDetailsVO.setOrderDescription(resultSet.getString("orderdescription"));
                transactionDetailsVO.setTemplateamount(resultSet.getString("templateamount"));
                transactionDetailsVO.setAmount(resultSet.getString("amount"));
                transactionDetailsVO.setCurrency(resultSet.getString("currency"));
                transactionDetailsVO.setRedirectURL(resultSet.getString("redirecturl"));
                transactionDetailsVO.setNotificationUrl(resultSet.getString("notificationUrl"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setFirstName(resultSet.getString("firstname"));//fn
                transactionDetailsVO.setLastName(resultSet.getString("lastname"));//ln
                transactionDetailsVO.setName(resultSet.getString("name"));//name
                transactionDetailsVO.setCcnum(resultSet.getString("ccnum"));//ccnum
                transactionDetailsVO.setExpdate(resultSet.getString("expdate"));//expdt
                transactionDetailsVO.setCardtype(resultSet.getString("cardtype"));//cardtype
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setIpAddress(resultSet.getString("ipaddress"));
                transactionDetailsVO.setCountry(resultSet.getString("country"));//country
                transactionDetailsVO.setState(resultSet.getString("state"));//state
                transactionDetailsVO.setCity(resultSet.getString("city"));//city
                transactionDetailsVO.setStreet(resultSet.getString("street"));//street
                transactionDetailsVO.setZip(resultSet.getString("zip"));//zip
                transactionDetailsVO.setTelcc(resultSet.getString("telnocc"));//telcc
                transactionDetailsVO.setTelno(resultSet.getString("telno"));//telno
                transactionDetailsVO.setHttpHeader(resultSet.getString("httpheader"));//httpheadet
                transactionDetailsVO.setPaymentId(resultSet.getString("paymentid"));
                transactionDetailsVO.setTemplatecurrency(resultSet.getString("templatecurrency"));
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setCustomerId(resultSet.getString("customerId"));
                transactionDetailsVO.setEci(resultSet.getString("eci"));
                transactionDetailsVO.setTerminalId(resultSet.getString("terminalid"));
                transactionDetailsVO.setVersion(resultSet.getString("version"));
                transactionDetailsVO.setCaptureAmount(resultSet.getString("captureamount"));
                transactionDetailsVO.setRefundAmount(resultSet.getString("refundamount"));
                transactionDetailsVO.setEmiCount(resultSet.getString("emiCount"));
                transactionDetailsVO.setTransactionTime(resultSet.getString("timestamp"));
                transactionDetailsVO.setWalletAmount(resultSet.getString("walletAmount"));
                transactionDetailsVO.setWalletCurrency(resultSet.getString("walletCurrency"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeResultSet(resultSet);
            Database.closeConnection(con);
        }
        return transactionDetailsVO;
    }

    public TransactionDetailsVO getTransDetailsForQuickpayments(String arn,String toid)
    {
        Connection con = null;
        ResultSet resultSet = null;
        PreparedStatement psUpdateTransaction = null;
        boolean status=false;
        TransactionDetailsVO transactionDetailsVO=null;
        try
        {
            con=Database.getConnection();
            String update = "select * from transaction_common where arn=? AND toid=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,arn);
            psUpdateTransaction.setString(2,toid);
            logger.error("getTransDetailsForQuickpayments transaction common query----"+psUpdateTransaction);
            resultSet=psUpdateTransaction.executeQuery();
            if(resultSet.next())
            {
                transactionDetailsVO=new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(resultSet.getString("trackingid"));
                transactionDetailsVO.setAccountId(resultSet.getString("accountid"));
                transactionDetailsVO.setPaymodeId(resultSet.getString("paymodeid"));
                transactionDetailsVO.setCardTypeId(resultSet.getString("cardtypeid"));
                transactionDetailsVO.setToid(resultSet.getString("toid"));
                transactionDetailsVO.setTotype(resultSet.getString("totype"));//totype
                transactionDetailsVO.setFromid(resultSet.getString("fromid"));//fromid
                transactionDetailsVO.setFromtype(resultSet.getString("fromtype"));//fromtype
                transactionDetailsVO.setDescription(resultSet.getString("description"));
                transactionDetailsVO.setOrderDescription(resultSet.getString("orderdescription"));
                transactionDetailsVO.setTemplateamount(resultSet.getString("templateamount"));
                transactionDetailsVO.setAmount(resultSet.getString("amount"));
                transactionDetailsVO.setCurrency(resultSet.getString("currency"));
                transactionDetailsVO.setRedirectURL(resultSet.getString("redirecturl"));
                transactionDetailsVO.setNotificationUrl(resultSet.getString("notificationUrl"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setFirstName(resultSet.getString("firstname"));//fn
                transactionDetailsVO.setLastName(resultSet.getString("lastname"));//ln
                transactionDetailsVO.setName(resultSet.getString("name"));//name
                transactionDetailsVO.setCcnum(resultSet.getString("ccnum"));//ccnum
                transactionDetailsVO.setExpdate(resultSet.getString("expdate"));//expdt
                transactionDetailsVO.setCardtype(resultSet.getString("cardtype"));//cardtype
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setIpAddress(resultSet.getString("ipaddress"));
                transactionDetailsVO.setCountry(resultSet.getString("country"));//country
                transactionDetailsVO.setState(resultSet.getString("state"));//state
                transactionDetailsVO.setCity(resultSet.getString("city"));//city
                transactionDetailsVO.setStreet(resultSet.getString("street"));//street
                transactionDetailsVO.setZip(resultSet.getString("zip"));//zip
                transactionDetailsVO.setTelcc(resultSet.getString("telnocc"));//telcc
                transactionDetailsVO.setTelno(resultSet.getString("telno"));//telno
                transactionDetailsVO.setHttpHeader(resultSet.getString("httpheader"));//httpheadet
                transactionDetailsVO.setPaymentId(resultSet.getString("paymentid"));
                transactionDetailsVO.setTemplatecurrency(resultSet.getString("templatecurrency"));
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setCustomerId(resultSet.getString("customerId"));
                transactionDetailsVO.setEci(resultSet.getString("eci"));
                transactionDetailsVO.setTerminalId(resultSet.getString("terminalid"));
                transactionDetailsVO.setVersion(resultSet.getString("version"));
                transactionDetailsVO.setCaptureAmount(resultSet.getString("captureamount"));
                transactionDetailsVO.setRefundAmount(resultSet.getString("refundamount"));
                transactionDetailsVO.setEmiCount(resultSet.getString("emiCount"));
                transactionDetailsVO.setTransactionTime(resultSet.getString("timestamp"));
                transactionDetailsVO.setWalletAmount(resultSet.getString("walletAmount"));
                transactionDetailsVO.setWalletCurrency(resultSet.getString("walletCurrency"));
                transactionDetailsVO.setArn(resultSet.getString("arn"));

            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeResultSet(resultSet);
            Database.closeConnection(con);
        }
        return transactionDetailsVO;
    }
    public TerminalVO getGatewayAccountsDetails(String accountId)
    {
        Connection con = null;
        ResultSet resultSet = null;
        PreparedStatement psUpdateTransaction = null;
        boolean status=false;
        TerminalVO transactionDetailsVO=null;
        try
        {
            con=Database.getConnection();
            String update = "SELECT accountid,merchantid,aliasname,displayname,3dSupportAccount FROM gateway_accounts WHERE accountid=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,accountId);
            logger.error("getGatewayAccountsDetails----"+psUpdateTransaction);
            resultSet=psUpdateTransaction.executeQuery();
            if(resultSet.next())
            {
                transactionDetailsVO=new TerminalVO();
                transactionDetailsVO.setAccountId(resultSet.getString("accountid"));
                transactionDetailsVO.setMemberId(resultSet.getString("merchantid"));
                transactionDetailsVO.setGateway_name(resultSet.getString("aliasname"));
                transactionDetailsVO.setDisplayName(resultSet.getString("displayname"));
                transactionDetailsVO.setIs3DSupport(resultSet.getString("3dSupportAccount"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeResultSet(resultSet);
            Database.closeConnection(con);
        }
        return transactionDetailsVO;
    }
    public boolean isTransactionDetailInserted(String trackingId, String status,String amount,String actionexecutorid,String actionexecutorname,String remark)
    {
        Connection cn = null;
        PreparedStatement pstmt = null;
        String actionStatus="";
        String role="Admin";
        boolean inserted =false;
        if(status.equals("capturesuccess")){
            actionStatus = ActionEntry.ACTION_CAPTURE_SUCCESSFUL;
        }
        else if(status.equals("settled")){
            actionStatus = ActionEntry.ACTION_CREDIT;
        }
        else if(status.equals("capturestarted")){
            actionStatus = ActionEntry.ACTION_CAPTURE_STARTED;
        }else if(status.equals("authcancelled")){
            actionStatus = ActionEntry.ACTION_AUTHORISTION_CANCLLED;
        }
        else if(status.equals("failed")){
            actionStatus = ActionEntry.ACTION_FAILED;
        }else if(status.equals("authfailed")){
            actionStatus = ActionEntry.ACTION_AUTHORISTION_FAILED;
        }else if(status.equals("authsuccessful")){
            actionStatus = ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL;
        }else if(status.equals("authstarted")){
            actionStatus = ActionEntry.ACTION_AUTHORISTION_STARTED;
        }else if(status.equals("reversed")){
            actionStatus = ActionEntry.ACTION_REVERSAL_SUCCESSFUL;
        }else if(status.equals("markedforreversal")){
            actionStatus = ActionEntry.ACTION_REVERSAL_REQUEST_SENT;
        }else
        {
            actionStatus = status;
        }

        try
        {
            cn = Database.getConnection();
            String sql = "insert into transaction_common_details(trackingid,amount,action,status,actionexecutorid,actionexecutorname,remark) values (?,?,?,?,?,?,?)";
            pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            pstmt.setString(2, amount);
            pstmt.setString(3, actionStatus);
            pstmt.setString(4, status);
            pstmt.setString(5, actionexecutorid);
            pstmt.setString(6, actionexecutorname);
            pstmt.setString(7, remark);
            int k=pstmt.executeUpdate();
            if(k>0)
            {
                inserted = true;
            }
        }
        catch (Exception e)
        {
            logger.error("Exception:::::", e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(cn);
        }
        return inserted;
    }

    public TransactionDetailsVO getDetailFromCommonForJPBank(String customerId,String amount,String transactionId) throws PZDBViolationException
    {
        Connection con = null;
        ResultSet resultSet = null;
        PreparedStatement ps = null;
        boolean status=false;
        TransactionDetailsVO transactionDetailsVO=null;
        try
        {
            con=Database.getConnection();
            StringBuffer query = new StringBuffer("select * from transaction_common where customerId=?");
            if(functions.isValueNull(amount))
                query.append(" and amount=?");
            if(functions.isValueNull(transactionId))
                query.append(" and paymentid REGEXP ?");
            query.append(" order by trackingId desc");
            ps = con.prepareStatement(query.toString());
            ps.setString(1,customerId);
            int counter=2;
            if(functions.isValueNull(amount))
            {
                ps.setString(counter, amount);
                counter++;
            }
            if(functions.isValueNull(transactionId))
            {
                ps.setString(counter, transactionId);
                counter++;
            }
            logger.error("getDetailFromCommonForJPBank transaction common query----"+ps);
            resultSet=ps.executeQuery();
            if(resultSet.next())
            {
                transactionDetailsVO=new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(resultSet.getString("trackingid"));
                transactionDetailsVO.setAccountId(resultSet.getString("accountid"));
                transactionDetailsVO.setPaymodeId(resultSet.getString("paymodeid"));
                transactionDetailsVO.setCardTypeId(resultSet.getString("cardtypeid"));
                transactionDetailsVO.setToid(resultSet.getString("toid"));
                transactionDetailsVO.setTotype(resultSet.getString("totype"));//totype
                transactionDetailsVO.setFromid(resultSet.getString("fromid"));//fromid
                transactionDetailsVO.setFromtype(resultSet.getString("fromtype"));//fromtype
                transactionDetailsVO.setDescription(resultSet.getString("description"));
                transactionDetailsVO.setOrderDescription(resultSet.getString("orderdescription"));
                transactionDetailsVO.setTemplateamount(resultSet.getString("templateamount"));
                transactionDetailsVO.setAmount(resultSet.getString("amount"));
                transactionDetailsVO.setCurrency(resultSet.getString("currency"));
                transactionDetailsVO.setRedirectURL(resultSet.getString("redirecturl"));
                transactionDetailsVO.setNotificationUrl(resultSet.getString("notificationUrl"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setFirstName(resultSet.getString("firstname"));//fn
                transactionDetailsVO.setLastName(resultSet.getString("lastname"));//ln
                transactionDetailsVO.setName(resultSet.getString("name"));//name
                transactionDetailsVO.setCcnum(resultSet.getString("ccnum"));//ccnum
                transactionDetailsVO.setExpdate(resultSet.getString("expdate"));//expdt
                transactionDetailsVO.setCardtype(resultSet.getString("cardtype"));//cardtype
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setIpAddress(resultSet.getString("ipaddress"));
                transactionDetailsVO.setCountry(resultSet.getString("country"));//country
                transactionDetailsVO.setState(resultSet.getString("state"));//state
                transactionDetailsVO.setCity(resultSet.getString("city"));//city
                transactionDetailsVO.setStreet(resultSet.getString("street"));//street
                transactionDetailsVO.setZip(resultSet.getString("zip"));//zip
                transactionDetailsVO.setTelcc(resultSet.getString("telnocc"));//telcc
                transactionDetailsVO.setTelno(resultSet.getString("telno"));//telno
                transactionDetailsVO.setHttpHeader(resultSet.getString("httpheader"));//httpheadet
                transactionDetailsVO.setPaymentId(resultSet.getString("paymentid"));
                transactionDetailsVO.setTemplatecurrency(resultSet.getString("templatecurrency"));
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setCustomerId(resultSet.getString("customerId"));
                transactionDetailsVO.setEci(resultSet.getString("eci"));
                transactionDetailsVO.setTerminalId(resultSet.getString("terminalid"));
                transactionDetailsVO.setVersion(resultSet.getString("version"));
                transactionDetailsVO.setCaptureAmount(resultSet.getString("captureamount"));
                transactionDetailsVO.setRefundAmount(resultSet.getString("refundamount"));
                transactionDetailsVO.setEmiCount(resultSet.getString("emiCount"));
                transactionDetailsVO.setTransactionTime(resultSet.getString("timestamp"));
                transactionDetailsVO.setWalletAmount(resultSet.getString("walletAmount"));
                transactionDetailsVO.setWalletCurrency(resultSet.getString("walletCurrency"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(),"getMerchantTransactionReports",null,"Common","Sql exception while connecting to transaction table", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
            PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(),"getMerchantTransactionReports",null,"Common","Sql exception while connecting to transaction table", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeResultSet(resultSet);
            Database.closeConnection(con);
        }
        return transactionDetailsVO;
    }


    public Hashtable getPayoutAmountLimitDetails(String gatewayname,int pageno ,int pagerecords)
    {
        Hashtable payoutAmountLimitDetails =  null;
        PreparedStatement ps    = null;
        ResultSet rs            = null;
        Connection conn         = null;
        int start = (pageno - 1) * pagerecords;
        int end = pagerecords;

        try
        {
            payoutAmountLimitDetails = new Hashtable<>();
            conn                     = Database.getRDBConnection();
            StringBuffer query       = new StringBuffer("SELECT g.aliasname,p.accountid,g.pgtypeid,gt.gateway,p.currentPayoutAmount,p.addedPayoutAmount FROM payout_amount_limit AS p JOIN `gateway_accounts` AS g ON p.accountid = g.accountid JOIN `gateway_type` AS gt ON g.pgtypeid = gt.pgtypeid where p.accountid > 0 ");
            StringBuffer countquery  = new StringBuffer("select count(*) from payout_amount_limit AS p JOIN `gateway_accounts` AS g ON p.accountid = g.accountid JOIN `gateway_type` AS gt ON g.pgtypeid = gt.pgtypeid where p.accountid > 0 ");

            if (functions.isValueNull(gatewayname))
            {
                query.append(" AND gt.gateway = '" + gatewayname + "'");
                countquery.append(" AND gt.gateway = '" + gatewayname + "'");
            }
            query.append(" order by gt.gateway LIMIT " + start + "," + end);

            logger.error("getPayoutAmountLimitDetails query:: "+query);
            logger.error("getPayoutAmountLimitDetails countQuery:: "+countquery);

            payoutAmountLimitDetails = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            rs                       = Database.executeQuery(countquery.toString(), conn);

            rs.next();
            int totalrecords = rs.getInt(1);

            payoutAmountLimitDetails.put("totalrecords", "" + totalrecords);
            payoutAmountLimitDetails.put("records", "0");

            if (totalrecords > 0)
                payoutAmountLimitDetails.put("records", "" + (payoutAmountLimitDetails.size() - 2));

        }
        catch (SystemError | SQLException e) {
            logger.error("Exception while getPayoutAmountLimitDetails:: "+e.toString());
        }
        finally {
            Database.closePreparedStatement(ps);
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return payoutAmountLimitDetails;
    }

    public Hashtable<String,String> getPayoutLimitAmount(String accountId)
    {
        Connection con          = null;
        ResultSet resultSet     = null;
        PreparedStatement ps    = null;
        Hashtable hash          = null;
        try
        {
            con     = Database.getConnection();
            StringBuffer query = new StringBuffer("select * from payout_amount_limit where accountid=?");

            ps = con.prepareStatement(query.toString());
            ps.setString(1,accountId);

            logger.error("getPayoutLimitAmount---"+ps);
            resultSet   = ps.executeQuery();
            hash        = new Hashtable<String,String>();
            if(resultSet.next())
            {

                hash.put("accountid",resultSet.getInt("accountid")+"");
                hash.put("currentPayoutAmount",resultSet.getString("currentPayoutAmount"));
                hash.put("addedPayoutAmount",resultSet.getString("addedPayoutAmount"));

            }

        }
        catch (SQLException se)
        {
            logger.error("SQLException----", se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError----", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return hash;
    }



    public String insertPayoutAmountLimit(String accountid,String currentPayoutAmount,String addedPayoutAmount)
    {
        Connection connection                  = null;
        PreparedStatement preparedStatement    = null;
        ResultSet resultSet                    = null;
        int i                                  = 0;
        String id                                 = "";
        try
        {
            connection          = Database.getConnection();
            String query        = "INSERT INTO `payout_amount_limit` (accountid,currentPayoutAmount,addedPayoutAmount) VALUES(?,?,?)";
            preparedStatement   = connection.prepareStatement(query);

            preparedStatement.setString(1,accountid);
            preparedStatement.setString(2,currentPayoutAmount);
            preparedStatement.setString(3, addedPayoutAmount);

            log.error("insertPayoutAmountLimit--->"+preparedStatement);
            i   = preparedStatement.executeUpdate();
            resultSet      = preparedStatement.getGeneratedKeys();
            if(resultSet.next())
            {
                id = resultSet.getString(1);
            }

        }
        catch (SystemError systemError)
        {
            log.error("SystemError -->",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException -->",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return id;
    }
    public boolean updatePayoutAmountLimit(String accountId, String currentPayoutAmount,String addedPayoutAmount)
    {
        Connection connection                  = null;
        PreparedStatement preparedStatement    = null;
        ResultSet resultSet                    = null;
        int i                                  = 0;
        String id                                 = "";
        boolean isUpdate                         = false;
        try
        {
            connection          = Database.getConnection();
            String query        = "update payout_amount_limit set currentPayoutAmount=?,addedPayoutAmount=? where accountId=?";
            preparedStatement   = connection.prepareStatement(query);

            preparedStatement.setString(1,currentPayoutAmount);
            preparedStatement.setString(2, addedPayoutAmount);
            preparedStatement.setString(3, accountId);

            log.error("updatePayoutAmountLimit--->"+preparedStatement);
            i  = preparedStatement.executeUpdate();
            if(i > 0){
                isUpdate = true;
            }
        }
        catch (SystemError systemError)
        {
            log.error("SystemError -->",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException -->",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return isUpdate;
    }
    public boolean updateNotificationStatusCode(String trackingId,String responseCode,CommResponseVO commResponseVO, AuditTrailVO auditTrailVO)
    {

        Connection connection                  = null;
        PreparedStatement preparedStatement    = null;
        ActionEntry entry = new ActionEntry();
        int i                                  = 0;
        String requestIp                       = (functions.isValueNull(commResponseVO.getIpaddress()))?commResponseVO.getIpaddress():"";
        String amount                          = (functions.isValueNull(commResponseVO.getAmount()))?commResponseVO.getAmount():"0.00";
        boolean isUpdate                         = false;
        try
        {
            connection          = Database.getConnection();
            String query        = "update transaction_common set machineid=? where trackingId=?";
            preparedStatement   = connection.prepareStatement(query);

            preparedStatement.setString(1,responseCode);
            preparedStatement.setString(2, trackingId);

            log.error("updateNotificationStatusCode--->"+preparedStatement);
            i  = preparedStatement.executeUpdate();
            if(i > 0 && !amount.equalsIgnoreCase("0.00")){
                isUpdate = true;
                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_NOTIFICATION_SENT, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);
            }
        }
        catch (SystemError systemError)
        {
            log.error("SystemError -->",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException -->",e);
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException -->", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return isUpdate;
    }

    public String getStatusFromCommon(String trackingId,String toid)
    {
        Connection con                          = null;
        ResultSet resultSet                     = null;
        PreparedStatement selectStatus   = null;
        String status = null;
        try
        {
            con                 = Database.getConnection();
            String update       = "SELECT status from transaction_common WHERE trackingid=? and toid=?";
            selectStatus = con.prepareStatement(update);
            selectStatus.setString(1,trackingId);
            selectStatus.setString(2,toid);
            resultSet           = selectStatus.executeQuery();
            if(resultSet.next())
            {
                status=resultSet.getString("status");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closePreparedStatement(selectStatus);
            Database.closeResultSet(resultSet);
            Database.closeConnection(con);
        }
        return status;
    }


    public Hashtable getPayoutStartDetails(String trackingid)
    {
        logger.debug("fetch record 1");
        StringBuffer query  = null;
        Hashtable hash      = null;
        Connection conn     = null;

        try
        {
            conn = Database.getRDBConnection();

            query = new StringBuffer("select distinct TS.trackingid, TS.fullname,TS.bankaccount,TS.ifsc, pu.transferType from transaction_safexpay_details as TS Left JOIN bulk_payout_upload as pu on TS.trackingid=pu.trackingid  where TS.trackingid=?  ");

            PreparedStatement pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1, trackingid);
            System.out.println("QUERY:::::: "+pstmt);
            hash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());
            logger.debug("query for payoutstarted transaction list-----" + pstmt);
        }
        catch (SQLException se)
        {
            logger.error("SQLException----",se);
        }
        catch (SystemError se)
        {
            logger.error("SystemError----",se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        logger.debug("Leaving getPayoutSuccessTransactionDetails");
        return hash;
    }
    public Map<String, Map<String, Map<String, Object>>> getDailySalesReport(CommonValidatorVO commonValidatorVO, List<Date> dateRange)
    {
        Map<String, Map<String, Map<String, Object>>> hashMap = new HashMap<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            con = Database.getConnection();
            String query = "SELECT DISTINCT tc.currency FROM transaction_common as tc  WHERE tc.toid=? AND tc.status=?";
            ps = con.prepareStatement(query);
            ps.setString(1, commonValidatorVO.getMerchantDetailsVO().getMemberId());
            ps.setString(2, commonValidatorVO.getStatus());
            logger.error("getDailySalesReport------->" + ps);
            rs = ps.executeQuery();
            while (rs.next())
            {
                Map<String, Map<String, Object>> data = getValueByDate(rs.getString("currency"), dateRange, commonValidatorVO.getTimeZone(),commonValidatorVO.getStatus(),commonValidatorVO.getMerchantDetailsVO().getMemberId());
                if (data != null && !data.isEmpty())
                {
                    hashMap.put(rs.getString("currency"), data);
                }
            }
        }
        catch (SystemError systemError)
        {
            systemError.printStackTrace();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            Database.closeConnection(con);
        }
        return hashMap;
    }


    private Map<String, Map<String, Object>> getValueByDate(String currency, List<Date> dateRange, String timeZone,String status,String id)
    {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fdtStamp, tdtStamp;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, Map<String, Object>> valueByDate = new LinkedHashMap<>();
        try
        {
            if (timeZone != null)
            {
                con = applyTimeZone(timeZone);
            }
            else
            {
                con = Database.getConnection();
            }
            for (Date d : dateRange)
            {
                String f = sdf.format(d);

                fdtStamp = sdf2.format(sdf.parse(f)) + " 00:00:00";
                tdtStamp = sdf2.format(sdf.parse(f)) + " 23:59:59";
                String query = "SELECT COUNT(*) records, SUM(transaction_common.amount) total FROM transaction_common WHERE transaction_common.status=? AND transaction_common.currency=? AND transaction_common.toid=? AND transaction_common.timestamp >=? AND transaction_common.timestamp <=?";
                ps = con.prepareStatement(query);
                ps.setString(1, status);
                ps.setString(2, currency);
                ps.setString(3, id);
                ps.setString(4, fdtStamp);
                ps.setString(5, tdtStamp);

                rs = ps.executeQuery();
                while (rs.next())
                {
                    //System.out.println(rs.getString("timestamp"));
                    Map<String, Object> value = new HashMap<>();
                    value.put("Amount", rs.getDouble("total"));
                    value.put("No_Of_Records", rs.getInt("records"));
                    valueByDate.put(f, value);
                }
            }
        }
        catch (SystemError systemError)
        {
            systemError.printStackTrace();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        finally
        {
            Database.closeConnection(con);
        }
        return valueByDate;
    }
    private Connection applyTimeZone(String timeZone) throws SystemError, SQLException
    {
        log.info("time::->"+timeZone);
        StringBuffer query = new StringBuffer();
        PreparedStatement ps = null;
        Connection conn = null;
        conn = Database.getConnection();
        query.append("SET SESSION time_zone='").append(timeZone).append("' ");
        ps = conn.prepareStatement(query.toString());
        int isChanged = ps.executeUpdate();
        if (isChanged > 0) {
            System.out.println("success");
        } else {
            System.out.println("stuck somewhere");
        }
        return conn;
    }

    public Hashtable payoutTransactionList(String toid, String trackingIds, String terminalId, String amount,String email, String description, String accountid, String tdtstamp, String fdtstamp, int records, int pageno, String totype,String dateType, String bankaccount, String status)
    {
        Codec me                = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer query      = null;
        Hashtable hash          = null;
        int start   = 0; // start index
        int end     = 0; // end index
        start       = (pageno - 1) * records;
        end                     = records;
        Connection conn         = null;
        Functions functions     = new Functions();
        try
        {
            conn    = Database.getRDBConnection();
            query   = new StringBuffer("SELECT ts.trackingid,totype,description,accountid,toid,tc.amount,from_unixtime(tc.dtstamp, '%Y-%m-%d %T') as dtstamp,emailaddr,currency,terminalId,ts.status,remark,fullname,bankaccount,ifsc FROM transaction_safexpay_details AS ts LEFT JOIN transaction_common AS tc ON ts.trackingid = tc.trackingid  WHERE  ts.status IN('payoutstarted','payoutfailed','payoutsuccessful')");

            if (functions.isValueNull(toid))
            {
                query.append(" and toid='" +ESAPI.encoder().encodeForSQL(me, toid) + "'");
            }
            if (functions.isValueNull(trackingIds))
            {
                query.append(" and tc.trackingid='" +ESAPI.encoder().encodeForSQL(me, trackingIds) + "'");
            }
            if (functions.isValueNull(terminalId))
            {
                query.append(" and terminalid='" +ESAPI.encoder().encodeForSQL(me, terminalId) + "'");
            }
            if (functions.isValueNull(amount))
            {
                query.append(" and tc.amount='" +ESAPI.encoder().encodeForSQL(me, amount) + "'");
            }
            if (functions.isValueNull(email))
            {
                query.append(" and tc.emailaddr='" +ESAPI.encoder().encodeForSQL(me, email) + "'");
            }
            if (functions.isValueNull(description))
            {
                query.append(" and description='" +ESAPI.encoder().encodeForSQL(me, description) + "'");
            }
            if (functions.isValueNull(accountid))
            {
                query.append(" and accountid='" +ESAPI.encoder().encodeForSQL(me, accountid) + "'");
            }
            if (functions.isValueNull(totype))
            {
                query.append(" and totype='" +ESAPI.encoder().encodeForSQL(me, totype) + "'");
            }
            if (functions.isValueNull(bankaccount))
            {
                query.append(" and ts.bankaccount='" + bankaccount + "' AND bankaccount IS NOT NULL");
            }
            if (functions.isValueNull(status))
            {
                query.append(" and ts.status='" + status + "'");

            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" and tc.timestamp >= '" + startDate + "'");
            }

            else if(functions.isValueNull(fdtstamp))
            {
                query.append(" and tc.dtstamp >= " + fdtstamp);
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {

                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and tc.timestamp <= '" + endDate + "'");
            }
            else if (functions.isValueNull(tdtstamp))
            {
                query.append(" and tc.dtstamp <= " + tdtstamp);
            }

            if (functions.isValueNull(trackingIds))
            {
                query.append(" and tc.trackingId IN (" + trackingIds + ")");
            }

            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");
            query.append(" group by tc.trackingId order by tc.trackingId DESC");
            query.append(" limit " + start + "," + end);

            logger.debug("===count query===" + countquery);
            logger.debug("===query===" + query);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            ResultSet rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

        }
        catch (SQLException se)
        {
            logger.error("SQLException----",se);
        }
        catch (SystemError se)
        {
            logger.error("SystemError----",se);
        }
        catch (Exception se)
        {
            logger.error("Exception----",se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public Hashtable listPayoutTransactionsForExportInExcel(String toid, String pgTypeId, String trackingid, String desc, String amount, String emailaddr, String tdtstamp, String fdtstamp, String status, String remark,String paymentId, String perfectmatch, boolean archive, Set<String> gatewayTypeSet, String gateway_name, String accountid, String dateType, String currency, String auth,String transactionMode, String customerid, String statusflag ,String totype,String bankname, String bankaccount,String ifsc ) throws PZDBViolationException
    {
        String tableName    = "";
        String fields       = "";
        StringBuffer query  = new StringBuffer();
        Hashtable hash      = null;
        Codec me            = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Connection conn     = null;
        try
        {
            conn                    = Database.getRDBConnection();
            Set<String> tableSet    = Database.getTableSet(gatewayTypeSet);

            tableName = "transaction_common";
            fields = "t.trackingid as trackingid,t.status,t.remark,t.toid as memberid,t.paymentid, t.country, t.customerId, bd.issuing_bank,t.name,t.amount," +
                    "t.payoutamount,t.description,t.orderdescription,t.dtstamp,t.timestamp,t.hrcode,t.accountid,t.terminalid,t.emailaddr,t.currency,t.ccnum," +
                    "t.totype,t.rrn,t.arn,t.authorization_code,t.transaction_mode,t.telnocc,t.telno,t.fromid ,t.processingbank";

            query.append("select " + fields + " from " + tableName + " as t  LEFT join bin_details as bd on t.trackingid = bd.icicitransid join members AS m on m.memberid=t.toid  where t.trackingid>0");
            // }
            if (functions.isValueNull(toid))
            {
                query.append(" and t.toid=" + ESAPI.encoder().encodeForSQL(me, toid));
            }
            if (functions.isValueNull(totype))
            {
                query.append(" and t.totype ='" + ESAPI.encoder().encodeForSQL(me,totype)+ "'");
            }

            if (functions.isValueNull(status))
            {
                query.append(" and t.status='" + status + "'");
            }

            if (functions.isValueNull(remark))
            {
                query.append(" and t.remark='" + remark + "'");
            }
            if (functions.isValueNull(amount))
            {
                query.append(" and t.amount='" + ESAPI.encoder().encodeForSQL(me,amount) + "'");
            }
            if (functions.isValueNull(emailaddr))
            {
                query.append(" and t.emailaddr='" + emailaddr + "'");
            }
            if (functions.isValueNull(gateway_name))
            {
                query.append(" and t.fromtype ='" + ESAPI.encoder().encodeForSQL(me,gateway_name )+ "'");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and t.currency ='" + ESAPI.encoder().encodeForSQL(me,currency)+ "'");
            }
            if (functions.isValueNull(auth))
            {
                query.append(" and t.authorization_code ='" + ESAPI.encoder().encodeForSQL(me,auth)+ "'");
            }
            if (functions.isValueNull(bankname))
            {
                query.append(" and ts.fullname ='"+ESAPI.encoder().encodeForSQL(me,bankname)+"'");
            }
            if (functions.isValueNull(bankaccount))
            {
                query.append(" and ts.bankaccount ='"+ESAPI.encoder().encodeForSQL(me,bankaccount)+ "'");
            }
            if (functions.isValueNull(ifsc))
            {
                query.append(" and ts.ifsc ='"+ESAPI.encoder().encodeForSQL(me,ifsc)+ "'");
            }
            logger.debug("accountid--"+accountid);
            logger.debug("accountid Is Null--"+functions.isValueNull(accountid));
            if (functions.isValueNull(accountid))
            {
                query.append(" and t.accountid ='" + ESAPI.encoder().encodeForSQL(me,accountid )+ "'");
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
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
                DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter2.format(calendar.getTime());
                query.append(" and t.timestamp <= '" + endDate + "'");
            }
            else
            {
                query.append(" and t.dtstamp <= " + tdtstamp);
            }

            if (functions.isValueNull(desc))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.description like '%" + ESAPI.encoder().encodeForSQL(me, desc) + "%'");
                }
                else
                {
                    query.append(" and t.description='" +ESAPI.encoder().encodeForSQL(me, desc) + "'");
                }
            }
           /* if (functions.isValueNull(orderdesc))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.orderdescription like '%" +ESAPI.encoder().encodeForSQL(me, orderdesc )+ "%'");
                }
                else
                {
                    query.append(" and t.orderdescription='" +ESAPI.encoder().encodeForSQL(me, orderdesc) + "'");
                }
            }*/
            if (functions.isValueNull(trackingid))
            {
                query.append(" and t.trackingid IN (" + trackingid + ")");
            }
           /* if (functions.isValueNull(transactionMode))
            {

                query.append(" and t.transaction_mode ='" + transactionMode.trim() + "'");
            }

            if(functions.isValueNull(fromid)){
                query.append("and t.fromid='"+ ESAPI.encoder().encodeForSQL(me, fromid)+ "'");
            }*/
            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");
            query.append(" order by dtstamp DESC,trackingid ");
            logger.debug("export to excel in admin:::::"+query.toString());
            hash            = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            ResultSet rs    = Database.executeQuery(countquery.toString(), conn);
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
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getTransactionDetailsToProcessCommonSettlement", null, "Common", "SQLException while connecting to transaction  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getTransactionDetailsToProcessCommonSettlement()", null, "Common", "SystemError while connecting to transaction  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public TransactionDetailsVO getDetailFromCommonBasedOnPaymentId(String paymentid)
    {
        Connection con                          = null;
        ResultSet resultSet                     = null;
        PreparedStatement psUpdateTransaction   = null;
        boolean status                          = false;
        TransactionDetailsVO transactionDetailsVO   = new TransactionDetailsVO();
        try
        {
            con                 = Database.getConnection();
            String update       = "select trackingid,accountid,paymodeid,cardtypeid,customerIp,toid,totype,fromid,fromtype,description,orderdescription,templateamount,amount,currency,redirecturl,notificationUrl,status,firstname,lastname,name,ccnum,expdate,cardtype,emailaddr,ipaddress,country,state,city,street,zip,telnocc,telno,httpheader,paymentid,templatecurrency,emailaddr,customerId,eci,terminalid,version,captureamount,refundamount,emiCount,timestamp,walletAmount,walletCurrency,transaction_type,transaction_mode,authorization_code,remark from transaction_common where paymentid=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,paymentid);
            resultSet           = psUpdateTransaction.executeQuery();
            if(resultSet.next())
            {
                transactionDetailsVO.setTrackingid(resultSet.getString("trackingid"));
                transactionDetailsVO.setAccountId(resultSet.getString("accountid"));
                transactionDetailsVO.setPaymodeId(resultSet.getString("paymodeid"));
                transactionDetailsVO.setCardTypeId(resultSet.getString("cardtypeid"));
                transactionDetailsVO.setCustomerIp(resultSet.getString("customerIp"));
                transactionDetailsVO.setToid(resultSet.getString("toid"));
                transactionDetailsVO.setTotype(resultSet.getString("totype"));//totype
                transactionDetailsVO.setFromid(resultSet.getString("fromid"));//fromid
                transactionDetailsVO.setFromtype(resultSet.getString("fromtype"));//fromtype
                transactionDetailsVO.setDescription(resultSet.getString("description"));
                transactionDetailsVO.setOrderDescription(resultSet.getString("orderdescription"));
                transactionDetailsVO.setTemplateamount(resultSet.getString("templateamount"));
                transactionDetailsVO.setAmount(resultSet.getString("amount"));
                transactionDetailsVO.setCurrency(resultSet.getString("currency"));
                transactionDetailsVO.setRedirectURL(resultSet.getString("redirecturl"));
                transactionDetailsVO.setNotificationUrl(resultSet.getString("notificationUrl"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setFirstName(resultSet.getString("firstname"));//fn
                transactionDetailsVO.setLastName(resultSet.getString("lastname"));//ln
                transactionDetailsVO.setName(resultSet.getString("name"));//name
                transactionDetailsVO.setCcnum(resultSet.getString("ccnum"));//ccnum
                transactionDetailsVO.setExpdate(resultSet.getString("expdate"));//expdt
                transactionDetailsVO.setCardtype(resultSet.getString("cardtype"));//cardtype
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setIpAddress(resultSet.getString("ipaddress"));
                transactionDetailsVO.setCountry(resultSet.getString("country"));//country
                transactionDetailsVO.setState(resultSet.getString("state"));//state
                transactionDetailsVO.setCity(resultSet.getString("city"));//city
                transactionDetailsVO.setStreet(resultSet.getString("street"));//street
                transactionDetailsVO.setZip(resultSet.getString("zip"));//zip
                transactionDetailsVO.setTelcc(resultSet.getString("telnocc"));//telcc
                transactionDetailsVO.setTelno(resultSet.getString("telno"));//telno
                transactionDetailsVO.setHttpHeader(resultSet.getString("httpheader"));//httpheadet
                transactionDetailsVO.setPaymentId(resultSet.getString("paymentid"));
                transactionDetailsVO.setTemplatecurrency(resultSet.getString("templatecurrency"));
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setCustomerId(resultSet.getString("customerId"));
                transactionDetailsVO.setEci(resultSet.getString("eci"));
                transactionDetailsVO.setTerminalId(resultSet.getString("terminalid"));
                transactionDetailsVO.setVersion(resultSet.getString("version"));
                transactionDetailsVO.setCaptureAmount(resultSet.getString("captureamount"));
                transactionDetailsVO.setRefundAmount(resultSet.getString("refundamount"));
                transactionDetailsVO.setEmiCount(resultSet.getString("emiCount"));
                transactionDetailsVO.setTransactionTime(resultSet.getString("timestamp"));
                transactionDetailsVO.setWalletAmount(resultSet.getString("walletAmount"));
                transactionDetailsVO.setWalletCurrency(resultSet.getString("walletCurrency"));
                transactionDetailsVO.setTransactionType(resultSet.getString("transaction_type"));
                transactionDetailsVO.setTransactionMode(resultSet.getString("transaction_mode"));
                transactionDetailsVO.setAuthorization_code(resultSet.getString("authorization_code"));
                transactionDetailsVO.setBankReferenceId(resultSet.getString("paymentid"));
                transactionDetailsVO.setRemark(resultSet.getString("remark"));
            }
            logger.debug("transaction common query----"+psUpdateTransaction);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeResultSet(resultSet);
            Database.closeConnection(con);
        }
        return transactionDetailsVO;
    }

    public boolean updateUUID(String uuid, String trackingId)
    {
        Connection con = null;
        ResultSet rsUpdateTransaction = null;
        try
        {
            con=Database.getConnection();

            String update = "update transaction_common set podbatch = ? where trackingid = ?";
            PreparedStatement psUpdateTransaction =con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,uuid);
            psUpdateTransaction.setString(2,trackingId);
            if(psUpdateTransaction.executeUpdate()==1)
            {
                return true;
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System error in updateTransactionfromCommon:;",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL exception in updateTransactionfromCommon::",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return false;
    }

    public List<TransactionVO> getTransactionListForInquiry(TransactionVO transactionVO, String fdtstamp, String tdtstamp, PaginationVO paginationVO)
    {
        List<TransactionVO> inquiryList      = new ArrayList();
        Codec me            = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer query     = null;
        StringBuffer count  = null;
        int counter =1;
        Connection conn     = null;

        try
        {
            conn = Database.getConnection();

            query = new StringBuffer("select trackingid,accountid,toid,description,amount,status,paymentid,remark,timestamp from transaction_common");

            if (functions.isValueNull(fdtstamp))
            {
                query.append(" where dtstamp >=?");
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and dtstamp <=?");
            }
            if (functions.isValueNull(transactionVO.getStatus()))
            {
                query.append(" and status=?");
            }
            if (functions.isValueNull(transactionVO.getToid()))
            {
                query.append(" and toid=?");
            }
            if (functions.isValueNull(transactionVO.getPgtypeid()))
            {
                query.append(" and fromtype=?");
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                query.append(" and currency=?");
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                query.append(" and paymentid=?");
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                query.append(" and accountid=?");
            }
            if (functions.isValueNull(transactionVO.getOrderDesc()))
            {
                if (transactionVO.getOrderDesc().contains(","))
                {
                    query.append(" and description IN (" + transactionVO.getOrderDesc() + ")");
                }
                else{
                    query.append(" and description= " + transactionVO.getOrderDesc() + "");
                }
            }
            if (functions.isValueNull(transactionVO.getTrackingId()))
            {
                query.append(" and trackingid IN ("+transactionVO.getTrackingId()+")");
            }
            String countQuery = "Select count(*) from ("+query.toString()+") as temp";
            query.append(" order by trackingid desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());

            PreparedStatement pstmt= conn.prepareStatement(query.toString());
            if (functions.isValueNull(fdtstamp))
            {
                pstmt.setString(counter, fdtstamp);
                counter++;
            }
            if (functions.isValueNull(tdtstamp))
            {
                pstmt.setString(counter, tdtstamp);
                counter++;
            }
            if (functions.isValueNull(transactionVO.getStatus()))
            {
                pstmt.setString(counter, transactionVO.getStatus());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getToid()))
            {
                pstmt.setString(counter, transactionVO.getToid());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getPgtypeid()))
            {
                pstmt.setString(counter, transactionVO.getPgtypeid());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                pstmt.setString(counter, transactionVO.getCurrency());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                pstmt.setString(counter, transactionVO.getPaymentId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                pstmt.setString(counter, transactionVO.getAccountId());
                counter++;
            }
           /* if (functions.isValueNull(transactionVO.getOrderDesc()))
            {
                pstmt.setString(counter, transactionVO.getOrderDesc());
                counter++;
            }*/

            logger.error("query for common inquiry----" + pstmt);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next())
            {
                TransactionVO transactionVO1 = new TransactionVO();
                transactionVO1.setTrackingId(rs.getString("trackingid"));
                transactionVO1.setAccountId(rs.getString("accountid"));
                transactionVO1.setMemberId(rs.getString("toid"));
                transactionVO1.setOrderDesc(rs.getString("description"));
                transactionVO1.setAmount(rs.getString("amount"));
                transactionVO1.setStatus(rs.getString("status"));
                transactionVO1.setPaymentId(rs.getString("paymentid"));
                transactionVO1.setRemark(rs.getString("remark"));
                transactionVO1.setTimestamp(rs.getString("timestamp"));

                inquiryList.add(transactionVO1);
            }

            logger.error("count query for common inquiry----" + countQuery);

            counter=1;

            PreparedStatement psCount= conn.prepareStatement(countQuery.toString());
            if (functions.isValueNull(fdtstamp))
            {
                psCount.setString(counter, fdtstamp);
                counter++;
            }
            if (functions.isValueNull(tdtstamp))
            {
                psCount.setString(counter, tdtstamp);
                counter++;
            }
            if (functions.isValueNull(transactionVO.getStatus()))
            {
                psCount.setString(counter, transactionVO.getStatus());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getToid()))
            {
                psCount.setString(counter, transactionVO.getToid());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getPgtypeid()))
            {
                psCount.setString(counter, transactionVO.getPgtypeid());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                psCount.setString(counter, transactionVO.getCurrency());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                psCount.setString(counter, transactionVO.getPaymentId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                psCount.setString(counter, transactionVO.getAccountId());
                counter++;
            }
           /* if (functions.isValueNull(transactionVO.getOrderDesc()))
            {
                psCount.setString(counter, transactionVO.getOrderDesc());
                counter++;
            }*/
            ResultSet rsCount =psCount.executeQuery();
            if(rsCount.next())
            {
                paginationVO.setTotalRecords(rsCount.getInt(1));
            }
            logger.debug("countQuery::"+countQuery);

        }
        catch(SystemError s)
        {
            logger.error("SystemError", s);
        }
        catch (SQLException e)
        {
            logger.error("SQLError",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return  inquiryList;
    }

    public TransactionDetailsVO getInquiryDetailsbyTrackingid(String trackingid)
    {
        Connection con = null;
        TransactionDetailsVO transactionDetailsVO=new TransactionDetailsVO();
        ResultSet rs=null;
        try
        {
            con=Database.getConnection();
            String update = "SELECT accountId,TrackingId,toid FROM transaction_common WHERE TrackingId=?";
            PreparedStatement ps =con.prepareStatement(update);
            ps.setString(1,trackingid);
            rs=ps.executeQuery();
            if(rs.next())
            {
                transactionDetailsVO.setAccountId(rs.getString("accountId"));
                transactionDetailsVO.setTrackingid(rs.getString("TrackingId"));
                transactionDetailsVO.setToid(rs.getString("toid"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionDetailsVO;
    }


    /*public Hashtable listTransactionsForExportInExcelNew(String toid, String pgTypeId, String trackingid, String name, String desc, String orderdesc, String amount, String firstfourofccnum, String lastfourofccnum, String emailaddr, String tdtstamp, String fdtstamp, String status, String remark,String paymentId, String perfectmatch, boolean archive, Set<String> gatewayTypeSet, String gateway_name, String accountid, String dateType, String currency, String cardtype, String issuing_bank,String arn , String rrn , String auth,String transactionMode, String customerid, String statusflag,String telno,String telnocc,String totype,String bankname, String bankaccount,String ifsc ,String fromid,String fileName) throws PZDBViolationException
    {
        String pRefund      = "false";
        String tableName    = "";
        String fields       = "";
        StringBuffer query  = new StringBuffer();
        Hashtable hash      = null;
        Codec me            = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Connection conn     = null;
        try
        {
            conn                    = Database.getRDBConnection();
            tableName               = "transaction_common";
            if (archive)
            {
                tableName = "transaction_common_archive";
            }
            String columnName="";
            columnName = "select 'Transaction Date(MM/DD/YYYY)','Member ID','Merchant Company Name','Tracking ID','Partner Name','Payment ID','Order ID','Order Description','Customer ID','Bank Account ID'" +
                         ",'Terminal ID','Payment Mode','Payment Brand','Bin Card Category','Bin Sub Brand','Sub Card Type','Issuing Bank Name','ISO Country','Currency','Card Holder Name'"+
                         ",'Customer Email','PhoneCC','Phone Number','Transaction Country','Auth Amount','Captured Amount','Refund Amount','Chargeback Amount'" +
                         ",'Payout Amount','First Six','Last four','RRN','ARN','Authorization Code','Status','Reason','Remark','MID','Processing BankName','Merchant IP','Customer IP','Last Update Date(MM/DD/YYYY)' UNION ALL";

            fields = "t.dtstamp,t.toid as memberid,m.company_name,t.trackingid as trackingid,t.totype,t.paymentid,t.description,t.orderdescription,t.customerId,t.accountid,t.terminalid,t.paymodeid,t.cardtype," +
                    "bd.bin_card_category,bd.bin_sub_brand,bd.subcard_type,bd.issuing_bank,bd.country_name as bin_country_name,t.currency,t.name,t.emailaddr,t.telnocc,t.telno,t.country,t.amount," +
                    "t.captureamount,t.refundamount,t.chargebackamount,t.payoutamount,bd.first_six,bd.last_four,t.rrn,t.arn,t.authorization_code,t.status,t.chargebackinfo,t.remark,t.fromid,t.processingbank," +
                    "t.ipAddress,t.customerIp,t.timestamp";
            //fields = fields +"t.templateamount,bd.bin_card_type,t.hrcode,t.transaction_mode";


            query.append(columnName+" "+" select " + fields + " from " + tableName + " as t  LEFT join bin_details as bd on t.trackingid = bd.icicitransid join members AS m on m.memberid=t.toid  where ");


            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                query.append(" t.timestamp >= '" + startDate + "'");
            }
            else
            {
                query.append(" t.dtstamp >= " + fdtstamp);
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter2.format(calendar.getTime());
                query.append(" and t.timestamp <= '" + endDate + "'");
            }
            else
            {
                query.append(" and t.dtstamp <= " + tdtstamp);
            }
            if (functions.isValueNull(toid))
            {
                query.append(" and t.toid=" + ESAPI.encoder().encodeForSQL(me, toid));
            }
            if (functions.isValueNull(totype))
            {
                query.append(" and t.totype ='" + ESAPI.encoder().encodeForSQL(me,totype)+ "'");
            }
            *//*if (functions.isValueNull(status))
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status = "reversed";
                }
                query.append(" and status='" + status + "' and captureamount > refundamount ");*//*

            if (functions.isValueNull(status))
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status = "reversed";
                    pRefund = "true";
                }
                query.append(" and t.status='" + status + "'");
            }
            if (!"all".equalsIgnoreCase(statusflag) && !"begun".equalsIgnoreCase(status))
            {
                query.append(" and bd."+statusflag+"='Y'");
            }
            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and captureamount > refundamount and STATUS='reversed'");
            }

            if (functions.isValueNull(remark))
            {
                query.append(" and t.remark='" + remark + "'");
            }
            if (functions.isValueNull(amount))
            {
                query.append(" and t.amount='" + ESAPI.encoder().encodeForSQL(me,amount) + "'");
            }
            if (functions.isValueNull(emailaddr))
            {
                query.append(" and t.emailaddr='" + emailaddr + "'");
            }
            if(functions.isValueNull(telnocc))
            {
                query.append(" and t.telnocc='" + telnocc +"'");
            }
            if (functions.isValueNull(telno))
            {
                query.append(" and t.telno='" + telno + "'");
            }

            if (functions.isValueNull(firstfourofccnum))
            {
                query.append(" and bd.first_six ='" +ESAPI.encoder().encodeForSQL(me, firstfourofccnum) + "'");
            }
            if (functions.isValueNull(lastfourofccnum))
            {
                query.append(" and bd.last_four ='" + ESAPI.encoder().encodeForSQL(me,lastfourofccnum )+ "'");
            }
            if (functions.isValueNull(cardtype))
            {
                query.append(" and (bd.bin_brand ='" +ESAPI.encoder().encodeForSQL(me, cardtype )+ "' OR t.cardtype='"+ESAPI.encoder().encodeForSQL(me, cardtype )+ "')");
            }
            if (functions.isValueNull(issuing_bank))
            {
                query.append(" and bd.issuing_bank ='" +ESAPI.encoder().encodeForSQL(me, issuing_bank )+ "'");
            }
            if (functions.isValueNull(gateway_name))
            {
                query.append(" and t.fromtype ='" + ESAPI.encoder().encodeForSQL(me,gateway_name )+ "'");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and t.currency ='" + ESAPI.encoder().encodeForSQL(me,currency)+ "'");
            }
            if (functions.isValueNull(arn))
            {
                query.append(" and t.arn ='" + ESAPI.encoder().encodeForSQL(me,arn)+ "'");
            }
            if (functions.isValueNull(rrn))
            {
                query.append(" and t.rrn ='" + ESAPI.encoder().encodeForSQL(me,rrn)+ "'");
            }
            if (functions.isValueNull(auth))
            {
                query.append(" and t.authorization_code ='" + ESAPI.encoder().encodeForSQL(me,auth)+ "'");
            }
            if (functions.isValueNull(paymentId))
            {
                query.append(" and t.paymentid ='"+ ESAPI.encoder().encodeForSQL(me,paymentId)+ "'");
            }
            if (functions.isValueNull(customerid))
            {
                query.append(" and t.customerId = '" + customerid + "'");
            }
            logger.debug("accountid--"+accountid);
            logger.debug("accountid Is Null--"+functions.isValueNull(accountid));
            if (functions.isValueNull(accountid))
            {
                query.append(" and t.accountid ='" + ESAPI.encoder().encodeForSQL(me,accountid )+ "'");
            }
            if (functions.isValueNull(name))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.name like '%" +ESAPI.encoder().encodeForSQL(me, name) + "%'");
                }
                else
                {
                    query.append(" and t.name='" +ESAPI.encoder().encodeForSQL(me, name) + "'");
                }
            }


            if (functions.isValueNull(desc))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.description like '%" + ESAPI.encoder().encodeForSQL(me, desc) + "%'");
                }
                else
                {
                    query.append(" and t.description='" +ESAPI.encoder().encodeForSQL(me, desc) + "'");
                }
            }
            if (functions.isValueNull(orderdesc))
            {
                if (perfectmatch == null)
                {
                    query.append(" and t.orderdescription like '%" +ESAPI.encoder().encodeForSQL(me, orderdesc )+ "%'");
                }
                else
                {
                    query.append(" and t.orderdescription='" +ESAPI.encoder().encodeForSQL(me, orderdesc) + "'");
                }
            }
            if (functions.isValueNull(trackingid))
            {
                    *//*if (tableName.equals("transaction_icicicredit") || tableName.equals("transaction_icicicredit_archive"))
                    {
                        query.append(" and t.icicitransid=" +ESAPI.encoder().encodeForSQL(me, trackingid));
                    }
                    else
                    {*//*
                //query.append(" and t.trackingid=" +ESAPI.encoder().encodeForSQL(me, trackingid));
                query.append(" and t.trackingid IN (" + trackingid + ")");
                //}
            }
            if (functions.isValueNull(transactionMode))
            {

                query.append(" and t.transaction_mode ='" + transactionMode.trim() + "'");
            }

            if(functions.isValueNull(fromid)){
                query.append("and t.fromid='"+ ESAPI.encoder().encodeForSQL(me, fromid)+ "'");
            }

            query.append(" INTO OUTFILE '"+fileName+"' ");
            query.append(" FIELDS TERMINATED BY ','   ENCLOSED BY '\"'   LINES TERMINATED BY '\\n' ");

            logger.error("queryExcel ::::>>>>>>>>>>>>>>>> "+query.toString());

            Database.executeQuery(query.toString(), conn);

        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getTransactionDetailsToProcessCommonSettlement()", null, "Common", "SystemError while connecting to transaction  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        catch (Exception e)
        {
            logger.error("SQLException::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getTransactionDetailsToProcessCommonSettlement", null, "Common", "SQLException while connecting to transaction  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }*/

    public boolean updateUpiTransactionDetails(CommonValidatorVO commonValidatorVO)
    {
        TransactionDetailsVO  transactionDetailsVO  = commonValidatorVO.getTransactionDetailsVO();
        MerchantDetailsVO merchantDetailsVO         = commonValidatorVO.getMerchantDetailsVO();
        Connection conn         = null;
        PreparedStatement ps    = null;
        boolean res             = false;

        try
        {
            conn = Database.getConnection();
            String query = "INSERT INTO  upi_transaction_details (upiReferenceId,amount,status,transactionDate,dtstamp,merchantid,vpaAddress) VALUES (?,?,?,?,unix_timestamp(now()),?,?)";
            ps           = conn.prepareStatement(query);
            ps.setString(1, transactionDetailsVO.getPaymentId());
            ps.setString(2, transactionDetailsVO.getAmount());
            ps.setString(3, transactionDetailsVO.getStatus());
            ps.setString(4, transactionDetailsVO.getTransactionTime());
            ps.setString(5, merchantDetailsVO.getMemberId());
            ps.setString(6, transactionDetailsVO.getCustomerId());
            int k = ps.executeUpdate();
            if (k==1)
                res= true;

            logger.error("res:"+res);
            logger.error("SqlQuery updateUpiTransactTrionDetails-----" + ps);
        }
        catch(Exception e)
        {
            logger.error("Exception updateUpiTransactTrionDetails  --->",e);
        }
        finally
        {
            Database.closeConnection(conn);
            Database.closePreparedStatement(ps);
        }
        return res;
    }

}