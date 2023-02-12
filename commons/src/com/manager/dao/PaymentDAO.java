package com.manager.dao;

import com.directi.pg.*;
import com.directi.pg.core.CardUsageType;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.BinVerificationManager;
import com.manager.PaymentManager;
import com.manager.vo.*;
import com.payment.Ecospend.EcospendRequestVo;
import com.payment.Ecospend.EcospendResponseVO;
import com.payment.Enum.PZProcessType;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/11/15
 * Time: 1:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class PaymentDAO
{
    private static Logger log                               = new Logger(PaymentDAO.class.getName());
    private static TransactionLogger transactionLogger      = new TransactionLogger(PaymentDAO.class.getName());
    private static Functions functions                      = new Functions();

    public int insertBegunTransactionForQwipi(CommonValidatorVO commonValidatorVO, String httpHeader, String status) throws PZDBViolationException
    {
        int trackingId = 0;
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String query = "insert into transaction_qwipi(toid,totype,fromid,fromtype,description,orderdescription,amount,redirecturl,status,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,ipaddress,terminalid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?)";
            PreparedStatement p = con.prepareStatement(query);
            p.setString(1, commonValidatorVO.getMerchantDetailsVO().getMemberId());
            p.setString(2, commonValidatorVO.getTransDetailsVO().getTotype());
            p.setString(3, commonValidatorVO.getTransDetailsVO().getFromid());
            p.setString(4, commonValidatorVO.getTransDetailsVO().getFromtype());
            p.setString(5, commonValidatorVO.getTransDetailsVO().getOrderId());
            p.setString(6, commonValidatorVO.getTransDetailsVO().getOrderDesc());
            p.setString(7, commonValidatorVO.getTransDetailsVO().getAmount());
            p.setString(8, commonValidatorVO.getTransDetailsVO().getRedirectUrl());
            p.setString(9, status);
            p.setString(10, commonValidatorVO.getMerchantDetailsVO().getAccountId());
            p.setInt(11, Integer.parseInt(commonValidatorVO.getPaymentType()));
            p.setInt(12, Integer.parseInt(commonValidatorVO.getCardType()));
            p.setString(13, commonValidatorVO.getTransDetailsVO().getCurrency());
            p.setString(14, httpHeader);
            p.setString(15, commonValidatorVO.getAddressDetailsVO().getIp());
            p.setString(16, commonValidatorVO.getTerminalId());
            int num = p.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {
                        trackingId = rs.getInt(1);
                    }
                }
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "insertTranseQwipiException()", null, "common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException s)
        {
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "insertTranseQwipiException()", null, "common", "SQL Exception thrown:::", PZDBExceptionEnum.INCORRECT_QUERY, null, s.getMessage(), s.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return trackingId;
    }

    public int insertBegunTransactionForEcore(CommonValidatorVO commonValidatorVO, String httphadder, String status) throws PZDBViolationException
    {
        int trackingId = 0;
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String query = "insert into transaction_ecore(toid,totype,fromid,fromtype,description,orderdescription,amount,redirecturl,status,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,ipaddress,terminalid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?)";
            PreparedStatement p = con.prepareStatement(query);
            p.setString(1, commonValidatorVO.getMerchantDetailsVO().getMemberId());
            p.setString(2, commonValidatorVO.getTransDetailsVO().getTotype());
            p.setString(3, commonValidatorVO.getTransDetailsVO().getFromid());
            p.setString(4, commonValidatorVO.getTransDetailsVO().getFromtype());
            p.setString(5, commonValidatorVO.getTransDetailsVO().getOrderId());
            p.setString(6, commonValidatorVO.getTransDetailsVO().getOrderDesc());
            p.setString(7, commonValidatorVO.getTransDetailsVO().getAmount());
            p.setString(8, commonValidatorVO.getTransDetailsVO().getRedirectUrl());
            p.setString(9, status);
            p.setString(10, commonValidatorVO.getMerchantDetailsVO().getAccountId());
            p.setInt(11, Integer.parseInt(commonValidatorVO.getPaymentType()));
            p.setInt(12, Integer.parseInt(commonValidatorVO.getCardType()));
            p.setString(13, commonValidatorVO.getTransDetailsVO().getCurrency());
            p.setString(14, httphadder);
            p.setString(15, commonValidatorVO.getAddressDetailsVO().getIp());
            p.setString(16, commonValidatorVO.getTerminalId());
            int num = p.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {

                        trackingId = rs.getInt(1);
                    }
                }
            }
            log.debug("query ecore---" + p);
        }
        catch (SQLException s)
        {
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "insertBegunTransactionForEcore()", null, "common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, s.getMessage(), s.getCause());
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "insertBegunTransactionForEcore()", null, "common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }

        finally
        {
            Database.closeConnection(con);
        }
        return trackingId;
    }

    public void updateDetailsTablewithErrorName(String errorName, String trackingid) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try
        {
            connection = Database.getConnection();
            String updateRecord = "UPDATE transaction_common_details SET errorName=? WHERE trackingid=? ORDER BY `detailid` DESC LIMIT 1";
            preparedStatement = connection.prepareStatement(updateRecord);
            preparedStatement.setString(1, errorName);
            preparedStatement.setString(2, trackingid);
            int i = preparedStatement.executeUpdate();
            //System.out.println("Q---"+preparedStatement+"---"+i);

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updateDetailsTablewithErrorName()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updateDetailsTablewithErrorName()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    public int insertBegunTransactionForCommon(CommonValidatorVO commonValidatorVO, String httphadder, String status) throws PZDBViolationException
    {
        int trackingId = 0;
        Connection con = null;
        PreparedStatement p = null;
        ResultSet rs = null;

        try
        {
            con = Database.getConnection();
            String query = "insert into transaction_common(toid,totype,fromid,fromtype,description,orderdescription,amount,redirecturl,status,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,ipaddress,terminalid,templateamount,templatecurrency,notificationUrl,customerId,version,parentTrackingid,emailaddr,merchantIpCountry,customerIP,customerIpCountry,firstname,lastname,name) values(?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            //String query="insert into transaction_common(toid,totype,fromid,fromtype,description,orderdescription,amount,redirecturl,status,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,ipaddress,terminalid,templateamount,templatecurrency,notificationUrl,customerId) values(?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?,?,?,?)";
            p = con.prepareStatement(query);
            p.setString(1, commonValidatorVO.getMerchantDetailsVO().getMemberId());
            p.setString(2, commonValidatorVO.getTransDetailsVO().getTotype());
            p.setString(3, commonValidatorVO.getTransDetailsVO().getFromid());
            p.setString(4, commonValidatorVO.getTransDetailsVO().getFromtype());
            p.setString(5, commonValidatorVO.getTransDetailsVO().getOrderId());
            p.setString(6, commonValidatorVO.getTransDetailsVO().getOrderDesc());
            p.setString(7, commonValidatorVO.getTransDetailsVO().getAmount());
            p.setString(8, commonValidatorVO.getTransDetailsVO().getRedirectUrl());
            p.setString(9, status);
            p.setString(10, commonValidatorVO.getMerchantDetailsVO().getAccountId());

            if (functions.isValueNull(commonValidatorVO.getPaymentType()))
            {
                p.setInt(11, Integer.parseInt(commonValidatorVO.getPaymentType()));
            }
            else
            {
                p.setInt(11, 0);
            }
            if (functions.isValueNull(commonValidatorVO.getCardType()))
            {
                p.setInt(12, Integer.parseInt(commonValidatorVO.getCardType()));
            }
            else
            {
                p.setInt(12, 0);
            }
            if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency()))
            {
                p.setString(13, commonValidatorVO.getTransDetailsVO().getCurrency().toUpperCase());
            }
            else
            {
                p.setString(13, "");
            }
            p.setString(14, httphadder);
            p.setString(15, commonValidatorVO.getAddressDetailsVO().getIp());
            if (functions.isValueNull(commonValidatorVO.getTerminalId()))
            {
                p.setInt(16, Integer.parseInt(commonValidatorVO.getTerminalId()));
            }
            else
            {
                p.setInt(16, 0);
            }
            p.setString(17, commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency()))
            {
                p.setString(18, commonValidatorVO.getAddressDetailsVO().getTmpl_currency().toUpperCase());
            }
            else
            {
                p.setString(18, commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
            }
            p.setString(19, commonValidatorVO.getTransDetailsVO().getNotificationUrl());
            p.setString(20, commonValidatorVO.getCustomerId());
            if (functions.isValueNull(commonValidatorVO.getVersion()))
            {
                p.setString(21, commonValidatorVO.getVersion());
            }
            else
            {
                p.setString(21, "");
            }
            p.setString(22, commonValidatorVO.getTrackingid());

            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail()))
            {
                p.setString(23, commonValidatorVO.getAddressDetailsVO().getEmail());
            }
            else
            {
                p.setString(23, "");
            }
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getIp()))
                p.setString(24, functions.getIPCountryShort(commonValidatorVO.getAddressDetailsVO().getIp()));
            else
                p.setString(24, "");

            p.setString(25, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()))
                p.setString(26, functions.getIPCountryShort(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()));
            else
                p.setString(26, "");

            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname()))
            {
                p.setString(27,commonValidatorVO.getAddressDetailsVO().getFirstname());
            }
            else
            {
                p.setString(27,"");
            }
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname()))
            {
                p.setString(28,commonValidatorVO.getAddressDetailsVO().getLastname());
            }
            else
            {
                p.setString(28,"");
            }
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname())&&functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname()))
            {
                p.setString(29,commonValidatorVO.getAddressDetailsVO().getFirstname()+" "+(commonValidatorVO.getAddressDetailsVO().getLastname()));
            }
            else if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname())&&!functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname()))
            {
                p.setString(29,commonValidatorVO.getAddressDetailsVO().getFirstname()+" "+(commonValidatorVO.getAddressDetailsVO().getFirstname()));
            }
            else if (!functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname())&&functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname()))
            {
                p.setString(29,commonValidatorVO.getAddressDetailsVO().getLastname()+" "+(commonValidatorVO.getAddressDetailsVO().getLastname()));
            }

            else
            {
                p.setString(29, "");
            }

            transactionLogger.error("---- insertBegunTransactionForCommon query -----"+p);

            int num = p.executeUpdate();
            if (num == 1)
            {
                rs = p.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {
                        trackingId = rs.getInt(1);
                    }
                }
            }

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "insertBegunTransactionForCommon()", null, "common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException s)
        {
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "insertBegunTransactionForCommon()", null, "common", "SQL Exception thrown:::", PZDBExceptionEnum.INCORRECT_QUERY, null, s.getMessage(), s.getCause());
        }
        finally
        {
            Database.closeConnection(con);
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
        }
        return trackingId;
    }


    public int insertAuthstartedTransactionforAsync(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        Connection connection = null;
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        TerminalVO terminalVO = commonValidatorVO.getTerminalVO();
        int trackingId = 0;

        PreparedStatement preparedStatement = null;
        try
        {
            connection = Database.getConnection();
            String updateRecord = "INSERT INTO transaction_common (toid,totype,fromid,fromtype,description,orderdescription,amount,redirecturl,STATUS,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,ipaddress,terminalid,street,country,city,state,zip,telno,telnocc,emailaddr,templateamount,templatecurrency,notificationUrl,customerId,cardtype,firstname,lastname,name,merchantIpCountry,customerIP,customerIpCountry,hrcode) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,UNIX_TIMESTAMP(NOW()),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            preparedStatement = connection.prepareStatement(updateRecord);
            preparedStatement.setString(1, merchantDetailsVO.getMemberId());
            preparedStatement.setString(2, genericTransDetailsVO.getTotype());
            preparedStatement.setString(3, genericTransDetailsVO.getFromid());
            preparedStatement.setString(4, genericTransDetailsVO.getFromtype());
            preparedStatement.setString(5, genericTransDetailsVO.getOrderId());
            preparedStatement.setString(6, genericTransDetailsVO.getOrderDesc());
            preparedStatement.setString(7, genericTransDetailsVO.getAmount());
            preparedStatement.setString(8, genericTransDetailsVO.getRedirectUrl());
            preparedStatement.setString(9, PZTransactionStatus.AUTH_STARTED.toString());
            preparedStatement.setString(10, merchantDetailsVO.getAccountId());
            preparedStatement.setString(11, terminalVO.getPaymodeId());
            preparedStatement.setString(12, terminalVO.getCardTypeId());
            preparedStatement.setString(13, genericTransDetailsVO.getCurrency());
            preparedStatement.setString(14, genericTransDetailsVO.getHeader());
            preparedStatement.setString(15, genericAddressDetailsVO.getIp());
            preparedStatement.setString(16, terminalVO.getTerminalId());
            preparedStatement.setString(17, genericAddressDetailsVO.getStreet());
            preparedStatement.setString(18, genericAddressDetailsVO.getCountry());
            preparedStatement.setString(19, genericAddressDetailsVO.getCity());
            preparedStatement.setString(20, genericAddressDetailsVO.getState());
            preparedStatement.setString(21, genericAddressDetailsVO.getZipCode());
            preparedStatement.setString(22, genericAddressDetailsVO.getPhone());
            preparedStatement.setString(23, genericAddressDetailsVO.getTelnocc());
            preparedStatement.setString(24, genericAddressDetailsVO.getEmail());
            preparedStatement.setString(25, genericAddressDetailsVO.getTmpl_amount());
            preparedStatement.setString(26, genericAddressDetailsVO.getTmpl_currency());
            preparedStatement.setString(27, genericTransDetailsVO.getNotificationUrl());
            preparedStatement.setString(28, commonValidatorVO.getCustomerId());
            preparedStatement.setString(29, GatewayAccountService.getCardType(commonValidatorVO.getCardType()));
            preparedStatement.setString(30, commonValidatorVO.getAddressDetailsVO().getFirstname());
            preparedStatement.setString(31, commonValidatorVO.getAddressDetailsVO().getLastname());
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname()) && functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname()))
                preparedStatement.setString(32, commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname());
            else if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname()))
                preparedStatement.setString(32, commonValidatorVO.getAddressDetailsVO().getFirstname());
            else if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname()))
                preparedStatement.setString(32, commonValidatorVO.getAddressDetailsVO().getLastname());
            else
                preparedStatement.setString(32, "");

            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getIp()))
                preparedStatement.setString(33,functions.getIPCountryShort(commonValidatorVO.getAddressDetailsVO().getIp()));
            else
                preparedStatement.setString(33,"");

            preparedStatement.setString(34,commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()))
                preparedStatement.setString(35,functions.getIPCountryShort(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()));
            else
                preparedStatement.setString(35,"");

                preparedStatement.setString(36,genericTransDetailsVO.getRedirectMethod());

            int i = preparedStatement.executeUpdate();

            if (i == 1)
            {
                ResultSet rs = preparedStatement.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {
                        trackingId = rs.getInt(1);
                    }
                }
            }

            commonValidatorVO.setTrackingid(String.valueOf(trackingId));
            String qry = "insert into bin_details (icicitransid, accountid) values(?,?)";
            PreparedStatement pstmt = connection.prepareStatement(qry);
            pstmt.setString(1, commonValidatorVO.getTrackingid());
            pstmt.setString(2, commonValidatorVO.getMerchantDetailsVO().getAccountId());
            pstmt.executeUpdate();
            transactionLogger.error("in insertAuthstartedTransactionforAsync quer ------"+pstmt);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "updateAuthstartedTransactionforCupInPay()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "updateAuthstartedTransactionforCupInPay()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return trackingId;
    }

    public void updateTransactionforExchanger(CommonValidatorVO commonValidatorVO, String status) throws PZDBViolationException
    {
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String updateRecord = "UPDATE transaction_common SET status=?,description=?, successtimestamp = ? WHERE trackingid=?";
            preparedStatement = connection.prepareStatement(updateRecord);
            preparedStatement.setString(1, status);
            preparedStatement.setString(2, commonValidatorVO.getTransDetailsVO().getOrderId());
            preparedStatement.setString(3, functions.getTimestamp());
            preparedStatement.setString(4, commonValidatorVO.getTrackingid());
            preparedStatement.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updateAuthstartedTransactionforP4()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updateAuthstartedTransactionforP4()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    public void getTransactionDetails(CommonValidatorVO commonValidatorVO)
    {
        Connection connection = null;
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();

        try
        {
            connection = Database.getConnection();
            String query = "SELECT totype,description,orderdescription,amount,currency,redirecturl,notificationUrl,templateamount,templatecurrency,customerId from transaction_common WHERE trackingid=?";
            PreparedStatement p = connection.prepareStatement(query);
            p.setString(1, commonValidatorVO.getTrackingid());
            ResultSet rs = p.executeQuery();
            if (rs.next())
            {
                genericTransDetailsVO.setTotype(rs.getString("totype"));
                genericTransDetailsVO.setRedirectUrl(rs.getString("redirecturl"));
                genericTransDetailsVO.setNotificationUrl(rs.getString("notificationUrl"));
                if(!functions.isValueNull(genericTransDetailsVO.getAmount()))
                {
                    genericTransDetailsVO.setAmount(rs.getString("amount"));
                    genericAddressDetailsVO.setTmpl_amount(rs.getString("templateamount"));

                }
                genericTransDetailsVO.setOrderDesc(rs.getString("orderdescription"));
                genericTransDetailsVO.setOrderId(rs.getString("description"));
                if(!functions.isValueNull(genericTransDetailsVO.getCurrency()))
                {
                    genericTransDetailsVO.setCurrency(rs.getString("currency"));
                    genericAddressDetailsVO.setTmpl_currency(rs.getString("templatecurrency"));

                }
                commonValidatorVO.setPartnerName(rs.getString("totype"));
                if (functions.isValueNull(rs.getString("customerId")))
                {
                    commonValidatorVO.setCustomerId(rs.getString("customerId"));
                }

                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
            }
            transactionLogger.debug("getMerchantandTransactionDetails123---"+p);
        }
        catch (SystemError systemError)
        {
            log.error("System error", systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    public void getMerchantDetails(CommonValidatorVO commonValidatorVO)
    {
        Connection connection = null;

        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        try
        {
            connection = Database.getConnection();
            String query = "SELECT clkey,partnerId,isPoweredBy,onlineFraudCheck,multiCurrencySupport,merchantlogoname,supportSection,address,city,state,zip,country,telno,company_name from members WHERE memberid=?";
            PreparedStatement p = connection.prepareStatement(query);
            p.setString(1, merchantDetailsVO.getMemberId());
            ResultSet rs = p.executeQuery();
            if (rs.next())
            {
                merchantDetailsVO.setPoweredBy(rs.getString("isPoweredBy"));
                merchantDetailsVO.setOnlineFraudCheck(rs.getString("onlineFraudCheck"));
                merchantDetailsVO.setKey(rs.getString("clkey"));
                merchantDetailsVO.setMultiCurrencySupport(rs.getString("multiCurrencySupport"));
                merchantDetailsVO.setSupportSection("supportSection");
                merchantDetailsVO.setAddress("address");
                merchantDetailsVO.setCity("city");
                merchantDetailsVO.setState("state");
                merchantDetailsVO.setZip("zip");
                merchantDetailsVO.setCountry("country");
                merchantDetailsVO.setTelNo("telno");
                merchantDetailsVO.setCompany_name("company_name");

                commonValidatorVO.setParetnerId(rs.getString("partnerId"));
                commonValidatorVO.setLogoName(rs.getString("merchantlogoname"));
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            }
            transactionLogger.debug("getMerchantandTransactionDetails---" + p);
        }
        catch (SystemError systemError)
        {
            log.error("System error", systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    public void updateCancelTransactionforCommon(CommonValidatorVO commonValidatorVO, String trackingid) throws PZDBViolationException
    {

        log.debug("-----inside updateAuthstartedTransactionforCommon----");

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try
        {
            connection = Database.getConnection();
            String updateRecord = "UPDATE transaction_common SET STATUS='cancelled',remark='Cancelled By Customer' WHERE trackingid=?";
            preparedStatement = connection.prepareStatement(updateRecord);
            preparedStatement.setString(1, trackingid);
            int i = preparedStatement.executeUpdate();
        }
        catch (SystemError systemError)
        {
            log.error("System error", systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException", e);
        }
        finally
        {
            Database.closeConnection(connection);
            Database.closePreparedStatement(preparedStatement);
        }
    }

    public void updateAuthstartedTransactionforCommon(CommonValidatorVO commonValidatorVO, String trackingid) throws PZDBViolationException
    {
        log.debug("-----inside updateAuthstartedTransactionforCommon----");
        if (commonValidatorVO != null)
        {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            PreparedStatement pstmt = null;
            GenericAddressDetailsVO genericAddressDetailsVO = null;
            GenericTransDetailsVO genericTransDetailsVO = null;
            GenericCardDetailsVO genericCardDetailsVO = null;
            MerchantDetailsVO merchantDetailsVO = null;

            if (commonValidatorVO.getAddressDetailsVO() != null)
            {
                genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
            }
            else
            {
                genericAddressDetailsVO = new GenericAddressDetailsVO();
            }

            if (commonValidatorVO.getCardDetailsVO() != null)
            {
                genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
            }
            else
            {
                genericCardDetailsVO = new GenericCardDetailsVO();
            }

            if (commonValidatorVO.getTransDetailsVO() != null)
            {
                genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
            }
            else
            {
                genericTransDetailsVO = new GenericTransDetailsVO();
            }

            if (commonValidatorVO.getMerchantDetailsVO() != null)
            {
                merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
            }
            else
            {
                merchantDetailsVO = new MerchantDetailsVO();
            }

            BinVerificationManager binVerificationManager=new BinVerificationManager();
            BinResponseVO binResponseVO=null;


            try
            {
                connection = Database.getConnection();
                String updateRecord = "UPDATE transaction_common SET firstname=?,lastname=?,name=?,street=?,country =?,city =?,state =?,zip =?,telno =?,telnocc =?,emailaddr=?,cardtype=?,STATUS='authstarted',ipaddress=?,fromtype=?,fromid=?,paymodeid=?,cardtypeid=?,terminalid=?,accountid=?,customerId=?,ccnum=?,transaction_mode=? WHERE trackingid=?";
                preparedStatement = connection.prepareStatement(updateRecord);
                String cardNum="";
                String threeDVersion="3Dv1";
                String firstSix="";
                String lastFour="";
                if(merchantDetailsVO.getIsCardStorageRequired().equalsIgnoreCase("N") && functions.isValueNull(genericCardDetailsVO.getCardNum()))
                {
                    cardNum = "";
                }else{
                    if(genericCardDetailsVO.getCardNum() != null && functions.isValueNull(genericCardDetailsVO.getCardNum())){
                        cardNum     =   genericCardDetailsVO.getCardNum();
                        firstSix=cardNum.substring(0,6);
                        lastFour=cardNum.substring((cardNum.length() - 4), cardNum.length());
                        GatewayAccountService gatewayAccountService = new GatewayAccountService();

                        String gatewayCountry                       = gatewayAccountService.getGatewayCountry(commonValidatorVO.getTerminalVO().getAccountId());

                        cardNum     =  PzEncryptor.encryptPAN(genericCardDetailsVO.getCardNum());
                        binResponseVO   =functions.getBinDetails(firstSix,gatewayCountry);

                        if(binResponseVO != null){
                            genericCardDetailsVO.setBin_brand(binResponseVO.getBrand());
                            genericCardDetailsVO.setIssuingBank(binResponseVO.getBank());
                            genericCardDetailsVO.setBin_card_category(binResponseVO.getCardcategory());
                            genericCardDetailsVO.setBin_card_type(binResponseVO.getCardtype());
                            genericCardDetailsVO.setCountry_code_A2(binResponseVO.getCountrycodeA2());
                            genericCardDetailsVO.setCountry_code_A3(binResponseVO.getCountrycodeA3());
                            genericCardDetailsVO.setCountryName(binResponseVO.getCountryname());
                            genericCardDetailsVO.setTrans_type(binResponseVO.getTranstype());
                            genericCardDetailsVO.setBin_sub_brand(binResponseVO.getSubbrand());
                            genericCardDetailsVO.setBin_usage_type(binResponseVO.getUsagetype());
                        }
                    }
                }

                if (functions.isValueNull(genericAddressDetailsVO.getFirstname()))
                {
                    preparedStatement.setString(1, genericAddressDetailsVO.getFirstname());
                }
                else
                {
                    preparedStatement.setString(1, "");
                }

                if (functions.isValueNull(genericAddressDetailsVO.getLastname()))
                {
                    preparedStatement.setString(2, genericAddressDetailsVO.getLastname());
                }
                else
                {
                    preparedStatement.setString(2, "");
                }
                if (functions.isValueNull(genericAddressDetailsVO.getFirstname()) && functions.isValueNull(genericAddressDetailsVO.getLastname()))
                {
                    preparedStatement.setString(3, genericAddressDetailsVO.getFirstname() + " " + genericAddressDetailsVO.getLastname());
                }
                else if (functions.isValueNull(genericAddressDetailsVO.getFirstname()) && !functions.isValueNull(genericAddressDetailsVO.getLastname()))
                {
                    preparedStatement.setString(3, genericAddressDetailsVO.getFirstname());
                }
                else
                {
                    preparedStatement.setString(3, "");
                }
                if (functions.isValueNull(genericAddressDetailsVO.getStreet()))
                {
                    preparedStatement.setString(4, genericAddressDetailsVO.getStreet());
                }
                else
                {
                    preparedStatement.setString(4, "");
                }
                if (functions.isValueNull(genericAddressDetailsVO.getCountry()))
                {
                    preparedStatement.setString(5, genericAddressDetailsVO.getCountry());
                }
                else
                {
                    preparedStatement.setString(5, "");
                }
                if (functions.isValueNull(genericAddressDetailsVO.getCity()))
                {
                    preparedStatement.setString(6, genericAddressDetailsVO.getCity());
                }
                else
                {
                    preparedStatement.setString(6, "");
                }
                if (functions.isValueNull(genericAddressDetailsVO.getState()))
                {
                    preparedStatement.setString(7, genericAddressDetailsVO.getState());
                }
                else
                {
                    preparedStatement.setString(7, "");
                }
                if (functions.isValueNull(genericAddressDetailsVO.getZipCode()))
                {
                    preparedStatement.setString(8, genericAddressDetailsVO.getZipCode());
                }
                else
                {
                    preparedStatement.setString(8, "");
                }
                if (functions.isValueNull(genericAddressDetailsVO.getPhone()))
                {
                    preparedStatement.setString(9, genericAddressDetailsVO.getPhone());
                }
                else
                {
                    preparedStatement.setString(9, "");
                }
                if (functions.isValueNull(genericAddressDetailsVO.getTelnocc()))
                {
                    preparedStatement.setString(10, genericAddressDetailsVO.getTelnocc());
                }
                else
                {
                    preparedStatement.setString(10, "");
                }
                if (functions.isValueNull(genericAddressDetailsVO.getEmail()))
                {
                    preparedStatement.setString(11, genericAddressDetailsVO.getEmail());
                }
                else
                {
                    preparedStatement.setString(11, "");
                }
                if (functions.isValueNull(genericCardDetailsVO.getCardNum()))
                {
                    preparedStatement.setString(12, Functions.getCardType(genericCardDetailsVO.getCardNum()));
                }
                else
                {
                    preparedStatement.setString(12, GatewayAccountService.getCardType(commonValidatorVO.getCardType()));
                }
                if (functions.isValueNull(genericAddressDetailsVO.getIp()))
                {
                    preparedStatement.setString(13, genericAddressDetailsVO.getIp());
                }
                else
                {
                    preparedStatement.setString(13, "");
                }
                if (functions.isValueNull(genericTransDetailsVO.getFromtype()))
                {
                    preparedStatement.setString(14, genericTransDetailsVO.getFromtype());
                }
                else
                {
                    preparedStatement.setString(14, "");
                }
                if (functions.isValueNull(genericTransDetailsVO.getFromid()))
                {
                    preparedStatement.setString(15, genericTransDetailsVO.getFromid());
                }
                else
                {
                    preparedStatement.setString(15, "");
                }
                if (functions.isValueNull(commonValidatorVO.getPaymentType()))
                {
                    preparedStatement.setString(16, commonValidatorVO.getPaymentType());
                }
                else
                {
                    preparedStatement.setString(16, "");
                }
                if (functions.isValueNull(commonValidatorVO.getCardType()))
                {
                    preparedStatement.setString(17, commonValidatorVO.getCardType());
                }
                else
                {
                    preparedStatement.setString(17, "");
                }
                if (functions.isValueNull(commonValidatorVO.getTerminalId()))
                {
                    preparedStatement.setString(18, commonValidatorVO.getTerminalId());
                }
                else
                {
                    preparedStatement.setInt(18, 0);
                }
                if (functions.isValueNull(merchantDetailsVO.getAccountId()))
                {
                    preparedStatement.setString(19, merchantDetailsVO.getAccountId());
                }
                else
                {
                    preparedStatement.setString(19, "");
                }
                if (functions.isValueNull(commonValidatorVO.getCustomerId()))
                {
                    preparedStatement.setString(20, commonValidatorVO.getCustomerId());
                }
                else
                {
                    preparedStatement.setString(20, "");
                }

                if (functions.isValueNull(cardNum))
                {
                    preparedStatement.setString(21, cardNum);
                }
                else
                {
                    preparedStatement.setString(21, "");
                }
                preparedStatement.setString(22, threeDVersion);
               /* if (functions.isValueNull(commonValidatorVO.getProcessingbank()))
                {
                    preparedStatement.setString(21, commonValidatorVO.getProcessingbank());
                }
                else
                {
                    preparedStatement.setString(21, "");
                }*/

                preparedStatement.setString(23, trackingid);
                int i = preparedStatement.executeUpdate();
                transactionLogger.error("preparedStatement --"+preparedStatement);

                String qry = "insert into bin_details (icicitransid, accountid, bin_brand, bin_sub_brand, bin_card_type, bin_card_category,bin_usage_type,bin_country_code_A3,bin_trans_type,bin_country_code_A2,emailaddr,customer_phone,customer_id,merchant_id,customer_ip,customer_ipCountry,customer_country,trans_currency,trans_amount,customer_email,trans_status,first_six,last_four,country_name,issuing_bank,trans_dtstamp) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()))";
                pstmt = connection.prepareStatement(qry);
                if (functions.isValueNull(commonValidatorVO.getTrackingid()))
                {
                    pstmt.setString(1, commonValidatorVO.getTrackingid());
                }
                else
                {
                    pstmt.setString(1, "");
                }
                if (functions.isValueNull(merchantDetailsVO.getAccountId()))
                {
                    pstmt.setString(2, merchantDetailsVO.getAccountId());
                }
                else
                {
                    pstmt.setString(2, "");
                }
                if (functions.isValueNull(genericCardDetailsVO.getBin_brand()))
                {
                    pstmt.setString(3, genericCardDetailsVO.getBin_brand());
                }
                else
                {
                    pstmt.setString(3, "");
                }
                if (functions.isValueNull(genericCardDetailsVO.getBin_sub_brand()))
                {
                    pstmt.setString(4, genericCardDetailsVO.getBin_sub_brand());
                }
                else
                {
                    pstmt.setString(4, "");
                }
                if (functions.isValueNull(genericCardDetailsVO.getBin_card_type()))
                {
                    pstmt.setString(5, genericCardDetailsVO.getBin_card_type());
                }
                else
                {
                    pstmt.setString(5, "");
                }
                if (functions.isValueNull(genericCardDetailsVO.getBin_card_category()))
                {
                    pstmt.setString(6, genericCardDetailsVO.getBin_card_category());
                }
                else
                {
                    pstmt.setString(6, "");
                }
                if (functions.isValueNull(genericCardDetailsVO.getBin_usage_type()))
                {
                    pstmt.setString(7, genericCardDetailsVO.getBin_usage_type());
                }
                else
                {
                    pstmt.setString(7, "");
                }
                if (functions.isValueNull(genericCardDetailsVO.getCountry_code_A3()))
                {
                    pstmt.setString(8, genericCardDetailsVO.getCountry_code_A3());
                }
                else
                {
                    pstmt.setString(8, "");
                }
                if (functions.isValueNull(genericCardDetailsVO.getTrans_type()))
                {
                    pstmt.setString(9, genericCardDetailsVO.getTrans_type());
                }
                else
                {
                    pstmt.setString(9, "");
                }
                if (functions.isValueNull(genericCardDetailsVO.getCountry_code_A2()))
                {
                    pstmt.setString(10, genericCardDetailsVO.getCountry_code_A2());
                }
                else
                {
                    pstmt.setString(10, "");
                }
                if (functions.isValueNull(genericAddressDetailsVO.getEmail()))
                {
                    pstmt.setString(11, genericAddressDetailsVO.getEmail());
                }
                else
                {
                    pstmt.setString(11, "");
                }

                if (functions.isValueNull(genericAddressDetailsVO.getPhone()))
                {
                    pstmt.setString(12, genericAddressDetailsVO.getPhone());
                }
                else
                {
                    pstmt.setString(12, "");
                }

                if (functions.isValueNull(commonValidatorVO.getCustomerId()))
                {
                    pstmt.setString(13, commonValidatorVO.getCustomerId());
                }
                else
                {
                    pstmt.setString(13, "");
                }

                if (functions.isValueNull(merchantDetailsVO.getMemberId()))
                {
                    pstmt.setString(14, merchantDetailsVO.getMemberId());
                }
                else
                {
                    pstmt.setString(14, "");
                }

                if (functions.isValueNull(genericAddressDetailsVO.getIp()))
                {
                    pstmt.setString(15, genericAddressDetailsVO.getIp());
                }
                else
                {
                    pstmt.setString(15, "");
                }

                if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()))
                {
                    pstmt.setString(16, functions.getIPCountryShort(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()));
                }
                else
                {
                    pstmt.setString(16, "");
                }

                if (functions.isValueNull(genericAddressDetailsVO.getCountry()))
                {
                    pstmt.setString(17, genericAddressDetailsVO.getCountry());
                }
                else
                {
                    pstmt.setString(17, "");
                }

                if (functions.isValueNull(genericTransDetailsVO.getCurrency()))
                {
                    pstmt.setString(18, genericTransDetailsVO.getCurrency());
                }
                else
                {
                    pstmt.setString(18, "");
                }

                if (functions.isValueNull(genericTransDetailsVO.getAmount()))
                {
                    pstmt.setString(19, genericTransDetailsVO.getAmount());
                }
                else
                {
                    pstmt.setString(19, "");
                }

                if (functions.isValueNull(genericAddressDetailsVO.getEmail()))
                {
                    pstmt.setString(20, genericAddressDetailsVO.getEmail());
                }
                else
                {
                    pstmt.setString(20, "");
                }

                //status
                pstmt.setString(21, "authstarted");

                if (functions.isValueNull(cardNum))
                {

                    pstmt.setString(22, firstSix);
                    pstmt.setString(23, lastFour);

                }
                else
                {
                    pstmt.setString(22, "");
                    pstmt.setString(23, "");
                }
                if (functions.isValueNull(genericCardDetailsVO.getCountryName()))
                {
                    pstmt.setString(24, genericCardDetailsVO.getCountryName());
                }
                else
                {
                    pstmt.setString(24, "");
                }

                if (functions.isValueNull(genericCardDetailsVO.getIssuingBank()))
                {
                    pstmt.setString(25, genericCardDetailsVO.getIssuingBank());
                }
                else
                {
                    pstmt.setString(25, "");
                }

                transactionLogger.error("  insert into bin card-->"+pstmt);
                pstmt.executeUpdate();
            }
            catch (SystemError se)
            {
                PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updateAuthstartedTransactionforP4()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
            }
            catch (SQLException e)
            {
                PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updateAuthstartedTransactionforP4()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
            }
            finally
            {
                Database.closeConnection(connection);
                Database.closePreparedStatement(preparedStatement);
                Database.closePreparedStatement(pstmt);
            }
        }
    }

    public void updateSMSstartedTransactionforCupUPI(CommonValidatorVO commonValidatorVO, String trackingid) throws PZDBViolationException
    {
        log.debug("-----inside updateSMSstartedTransactionforCupUPI----");
        if (commonValidatorVO != null)
        {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            PreparedStatement pstmt = null;
            GenericAddressDetailsVO genericAddressDetailsVO = null;
            GenericCardDetailsVO genericCardDetailsVO = null;
            GenericTransDetailsVO genericTransDetailsVO = null;
            MerchantDetailsVO merchantDetailsVO = null;
            if (commonValidatorVO.getAddressDetailsVO() != null)
            {
                genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
            }
            else
            {
                genericAddressDetailsVO = new GenericAddressDetailsVO();
            }

            if (commonValidatorVO.getCardDetailsVO() != null)
            {
                genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
            }
            else
            {
                genericCardDetailsVO = new GenericCardDetailsVO();
            }

            if (commonValidatorVO.getTransDetailsVO() != null)
            {
                genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
            }
            else
            {
                genericTransDetailsVO = new GenericTransDetailsVO();
            }

            if (commonValidatorVO.getMerchantDetailsVO() != null)
            {
                merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
            }
            else
            {
                merchantDetailsVO = new MerchantDetailsVO();
            }


            try
            {
                connection = Database.getConnection();
                String updateRecord = "UPDATE transaction_common SET firstname=?,lastname=?,name=?,street=?,country =?,city =?,state =?,zip =?,telno =?,telnocc =?,emailaddr=?,cardtype=?,STATUS='smsstarted',ipaddress=?,fromtype=?,fromid=?,paymodeid=?,cardtypeid=?,terminalid=?,accountid=? WHERE trackingid=?";
                preparedStatement = connection.prepareStatement(updateRecord);
                if (functions.isValueNull(genericAddressDetailsVO.getFirstname()))
                {
                    preparedStatement.setString(1, genericAddressDetailsVO.getFirstname());
                }
                else
                {
                    preparedStatement.setString(1, "");
                }

                if (functions.isValueNull(genericAddressDetailsVO.getLastname()))
                {
                    preparedStatement.setString(2, genericAddressDetailsVO.getLastname());
                }
                else
                {
                    preparedStatement.setString(2, "");
                }
                if (functions.isValueNull(genericAddressDetailsVO.getFirstname()) && functions.isValueNull(genericAddressDetailsVO.getLastname()))
                {
                    preparedStatement.setString(3, genericAddressDetailsVO.getFirstname() + " " + genericAddressDetailsVO.getLastname());
                }
                else
                {
                    preparedStatement.setString(3, "");
                }
                if (functions.isValueNull(genericAddressDetailsVO.getStreet()))
                {
                    preparedStatement.setString(4, genericAddressDetailsVO.getStreet());
                }
                else
                {
                    preparedStatement.setString(4, "");
                }
                if (functions.isValueNull(genericAddressDetailsVO.getCountry()))
                {
                    preparedStatement.setString(5, genericAddressDetailsVO.getCountry());
                }
                else
                {
                    preparedStatement.setString(5, "");
                }
                if (functions.isValueNull(genericAddressDetailsVO.getCity()))
                {
                    preparedStatement.setString(6, genericAddressDetailsVO.getCity());
                }
                else
                {
                    preparedStatement.setString(6, "");
                }
                if (functions.isValueNull(genericAddressDetailsVO.getState()))
                {
                    preparedStatement.setString(7, genericAddressDetailsVO.getState());
                }
                else
                {
                    preparedStatement.setString(7, "");
                }
                if (functions.isValueNull(genericAddressDetailsVO.getZipCode()))
                {
                    preparedStatement.setString(8, genericAddressDetailsVO.getZipCode());
                }
                else
                {
                    preparedStatement.setString(8, "");
                }
                if (functions.isValueNull(genericAddressDetailsVO.getPhone()))
                {
                    preparedStatement.setString(9, genericAddressDetailsVO.getPhone());
                }
                else
                {
                    preparedStatement.setString(9, "");
                }
                if (functions.isValueNull(genericAddressDetailsVO.getTelnocc()))
                {
                    preparedStatement.setString(10, genericAddressDetailsVO.getTelnocc());
                }
                else
                {
                    preparedStatement.setString(10, "");
                }
                if (functions.isValueNull(genericAddressDetailsVO.getEmail()))
                {
                    preparedStatement.setString(11, genericAddressDetailsVO.getEmail());
                }
                else
                {
                    preparedStatement.setString(11, "");
                }
                if (functions.isValueNull(genericCardDetailsVO.getCardNum()))
                {
                    preparedStatement.setString(12, Functions.getCardType(genericCardDetailsVO.getCardNum()));
                }
                else
                {
                    preparedStatement.setString(12, "");

                    preparedStatement.setString(12, GatewayAccountService.getCardType(commonValidatorVO.getCardType()));

                }
                if (functions.isValueNull(genericAddressDetailsVO.getIp()))
                {
                    preparedStatement.setString(13, genericAddressDetailsVO.getIp());
                }
                else
                {
                    preparedStatement.setString(13, "");
                }
                if (functions.isValueNull(genericTransDetailsVO.getFromtype()))
                {
                    preparedStatement.setString(14, genericTransDetailsVO.getFromtype());
                }
                else
                {
                    preparedStatement.setString(14, "");
                }
                if (functions.isValueNull(genericTransDetailsVO.getFromid()))
                {
                    preparedStatement.setString(15, genericTransDetailsVO.getFromid());
                }
                else
                {
                    preparedStatement.setString(15, "");
                }
                if (functions.isValueNull(commonValidatorVO.getPaymentType()))
                {
                    preparedStatement.setString(16, commonValidatorVO.getPaymentType());
                }
                else
                {
                    preparedStatement.setString(16, "");
                }
                if (functions.isValueNull(commonValidatorVO.getCardType()))
                {
                    preparedStatement.setString(17, commonValidatorVO.getCardType());
                }
                else
                {
                    preparedStatement.setString(17, "");
                }
                if (functions.isValueNull(commonValidatorVO.getTerminalId()))
                {
                    preparedStatement.setString(18, commonValidatorVO.getTerminalId());
                }
                else
                {
                    preparedStatement.setInt(18, 0);
                }
                if (functions.isValueNull(merchantDetailsVO.getAccountId()))
                {
                    preparedStatement.setString(19, merchantDetailsVO.getAccountId());
                }
                else
                {
                    preparedStatement.setString(19, "");
                }
                preparedStatement.setString(20, trackingid);
                int i = preparedStatement.executeUpdate();
                transactionLogger.debug("query for sms started ---"+preparedStatement);
                String qry = "insert into bin_details (icicitransid, accountid, bin_brand, bin_sub_brand, bin_card_type, bin_card_category,bin_usage_type,bin_country_code_A3,bin_trans_type,bin_country_code_A2,emailaddr) " +
                        "values(?,?,?,?,?,?,?,?,?,?,?)";
                pstmt = connection.prepareStatement(qry);
                transactionLogger.debug("tracking id in payment dao-----------"+commonValidatorVO.getTrackingid());
                if (functions.isValueNull(commonValidatorVO.getTrackingid()))
                {
                    pstmt.setString(1, commonValidatorVO.getTrackingid());
                }
                else
                {
                    pstmt.setString(1, "");
                }
                if (functions.isValueNull(merchantDetailsVO.getAccountId()))
                {
                    pstmt.setString(2, merchantDetailsVO.getAccountId());
                }
                else
                {
                    pstmt.setString(2, "");
                }
                if (functions.isValueNull(genericCardDetailsVO.getBin_brand()))
                {
                    pstmt.setString(3, genericCardDetailsVO.getBin_brand());
                }
                else
                {
                    pstmt.setString(3, "");
                }
                if (functions.isValueNull(genericCardDetailsVO.getBin_sub_brand()))
                {
                    pstmt.setString(4, genericCardDetailsVO.getBin_sub_brand());
                }
                else
                {
                    pstmt.setString(4, "");
                }
                if (functions.isValueNull(genericCardDetailsVO.getBin_card_type()))
                {
                    pstmt.setString(5, genericCardDetailsVO.getBin_card_type());
                }
                else
                {
                    pstmt.setString(5, "");
                }
                if (functions.isValueNull(genericCardDetailsVO.getBin_card_category()))
                {
                    pstmt.setString(6, genericCardDetailsVO.getBin_card_category());
                }
                else
                {
                    pstmt.setString(6, "");
                }
                if (functions.isValueNull(genericCardDetailsVO.getBin_usage_type()))
                {
                    pstmt.setString(7, genericCardDetailsVO.getBin_usage_type());
                }
                else
                {
                    pstmt.setString(7, "");
                }
                if (functions.isValueNull(genericCardDetailsVO.getCountry_code_A3()))
                {
                    pstmt.setString(8, genericCardDetailsVO.getCountry_code_A3());
                }
                else
                {
                    pstmt.setString(8, "");
                }
                if (functions.isValueNull(genericCardDetailsVO.getTrans_type()))
                {
                    pstmt.setString(9, genericCardDetailsVO.getTrans_type());
                }
                else
                {
                    pstmt.setString(9, "");
                }
                if (functions.isValueNull(genericCardDetailsVO.getCountry_code_A2()))
                {
                    pstmt.setString(10, genericCardDetailsVO.getCountry_code_A2());
                }
                else
                {
                    pstmt.setString(10, "");
                }
                if (functions.isValueNull(genericAddressDetailsVO.getEmail()))
                {
                    pstmt.setString(11, genericAddressDetailsVO.getEmail());
                }
                else
                {
                    pstmt.setString(11, "");
                }

                pstmt.executeUpdate();
            }
            catch (SystemError se)
            {
                PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updateAuthstartedTransactionforP4()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
            }
            catch (SQLException e)
            {
                PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updateAuthstartedTransactionforP4()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
            }
            finally
            {
                Database.closeConnection(connection);
                Database.closePreparedStatement(preparedStatement);
                Database.closePreparedStatement(pstmt);
            }
        }
    }

    public void updateEnrollmentstartedTransactionforCupUPI(CommonValidatorVO commonValidatorVO, String trackingid,String status) throws PZDBViolationException
    {
        log.debug("-----inside updateEnrollmentstartedTransactionforCupUPI----");
        if (commonValidatorVO != null)
        {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            PreparedStatement pstmt = null;
            try
            {
                connection = Database.getConnection();
                String updateRecord = "UPDATE transaction_common SET STATUS=? WHERE trackingid=?";
                preparedStatement = connection.prepareStatement(updateRecord);
                preparedStatement.setString(1, status);
                preparedStatement.setString(2, trackingid);
                int i = preparedStatement.executeUpdate();
                transactionLogger.debug("query --------------------"+preparedStatement);
            }
            catch (SystemError se)
            {
                PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updateEnrollmentstartedTransactionforCupUPI()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
            }
            catch (SQLException e)
            {
                PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updateEnrollmentstartedTransactionforCupUPI()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
            }
            finally
            {
                Database.closeConnection(connection);
                Database.closePreparedStatement(preparedStatement);
                Database.closePreparedStatement(pstmt);
            }
        }
    }


    public void updateExtensionforEpay(String trackingid, String status, String pay_time, String stan, String bcode) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        try
        {
            conn = Database.getConnection();
            String updateQuery = "UPDATE transaction_epay_details SET status=?,pay_time=?,stan=?,bcode=? WHERE trackingid=?";
            ps = conn.prepareStatement(updateQuery);
            ps.setString(1, status);
            ps.setString(2, pay_time);
            ps.setString(3, stan);
            ps.setString(4, bcode);
            ps.setString(5, trackingid);
            int i = ps.executeUpdate();

            transactionLogger.debug("-------updateExtensionforEpay-------" + ps);

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "updateExtensionforEpay()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "updateExtensionforEpay()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }

    }

    public HashMap getExtnDetailsForEpay(String trackingId)
    {
        HashMap hashMap = new HashMap();
        Connection connection = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        try
        {
            connection = Database.getConnection();
            String query = "SELECT cemail,cin,customerId FROM transaction_epay_details WHERE trackingid=?";
            p = connection.prepareStatement(query);
            p.setString(1, trackingId);
            rs = p.executeQuery();
            if (rs.next())
            {
                if (functions.isValueNull(rs.getString("cemail")))
                {
                    hashMap.put("customerEmail", rs.getString("cemail"));
                }
                if (functions.isValueNull(rs.getString("cin")))
                {
                    hashMap.put("customerBankId", rs.getString("cin"));
                }
                if (functions.isValueNull(rs.getString("customerId")))
                {
                    hashMap.put("customerId", rs.getString("customerId"));
                }
            }
            transactionLogger.debug("getExtnDetailsForEpay---" + p);
        }
        catch (SystemError systemError)
        {
            log.error("System error", systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException", e);
        }
        finally
        {
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
            Database.closeConnection(connection);
        }
        return hashMap;
    }

    public HashMap getExtnDetailsForTrustly(String trackingId)
    {
        HashMap hashMap = new HashMap();
        Connection connection = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        try
        {
            connection = Database.getConnection();
            String query = "SELECT customer_account_id FROM transaction_trustly_details WHERE trackingid=?";
            p = connection.prepareStatement(query);
            p.setString(1, trackingId);
            rs = p.executeQuery();
            if (rs.next())
            {

                if (functions.isValueNull(rs.getString("customer_account_id")))
                {
                    hashMap.put("customerBankId", rs.getString("customer_account_id"));
                }

            }
            transactionLogger.debug("getExtnDetailsForTrustly---" + p);
        }
        catch (SystemError systemError)
        {
            log.error("System error", systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException", e);
        }
        finally
        {
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
            Database.closeConnection(connection);
        }
        return hashMap;
    }

    public HashMap getExtnDetailsForSkrill(String trackingId)
    {
        Connection connection = null;
        HashMap hashMap = new HashMap();

        try
        {
            connection = Database.getConnection();
            String query = "SELECT customerId,customerEmail,customerBankId FROM transaction_skrill_details WHERE trackingid=?";
            PreparedStatement p = connection.prepareStatement(query);
            p.setString(1, trackingId);
            ResultSet rs = p.executeQuery();
            if (rs.next())
            {
                if (functions.isValueNull(rs.getString("customerId")))
                {
                    hashMap.put("customerId", rs.getString("customerId"));
                }
                else
                {
                    hashMap.put("customerId", "");
                }
                if (functions.isValueNull(rs.getString("customerEmail")))
                {
                    hashMap.put("customerEmail", rs.getString("customerEmail"));
                }
                else
                {
                    hashMap.put("customerEmail", "");
                }
                if (functions.isValueNull(rs.getString("customerBankId")))
                {
                    hashMap.put("customerBankId", rs.getString("customerBankId"));
                }
                else
                {
                    hashMap.put("customerBankId", "");
                }
            }
            transactionLogger.debug("detail table for skrill---" + p);
        }
        catch (SystemError systemError)
        {
            log.error("System error", systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return hashMap;
    }

    public HashMap getExtnDetailsForPM(String trackingId)
    {
        Connection connection = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        HashMap hashMap = new HashMap();

        try
        {
            connection = Database.getConnection();
            String query = "SELECT payer_account,payment_batch_number FROM transaction_perfectmoney_details WHERE trackingid=?";
            p = connection.prepareStatement(query);
            p.setString(1, trackingId);
            rs = p.executeQuery();
            if (rs.next())
            {
                if (functions.isValueNull(rs.getString("payer_account")))
                {
                    hashMap.put("customerBankId", rs.getString("payer_account"));
                }
                if (functions.isValueNull(rs.getString("payment_batch_number")))
                {
                    hashMap.put("customerId", rs.getString("payment_batch_number"));
                }
            }
            transactionLogger.debug("detail table for PM---" + p);
        }
        catch (SystemError systemError)
        {
            log.error("System error", systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException", e);
        }
        finally
        {
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
            Database.closeConnection(connection);
        }
        return hashMap;
    }

    public HashMap getExtnDetailsForVM(String trackingId)
    {
        Connection connection = null;
        HashMap hashMap = new HashMap();
        PreparedStatement p = null;
        ResultSet rs = null;
        try
        {
            connection = Database.getConnection();
            String query = "SELECT customerId,customerEmail,customerBankId,merchantUsersCommission,merchantUserCommCurrency,commissionPaidToUser,commPaidToUserCurrency FROM transaction_vouchermoney_details WHERE trackingid=?";
            p = connection.prepareStatement(query);
            p.setString(1, trackingId);
            rs = p.executeQuery();
            if (rs.next())
            {
                if (functions.isValueNull(rs.getString("customerid")))
                {
                    hashMap.put("customerId", rs.getString("customerid"));
                }
                if (functions.isValueNull(rs.getString("customerEmail")))
                {
                    hashMap.put("customerEmail", rs.getString("customerEmail"));
                }
                if (functions.isValueNull(rs.getString("customerBankId")))
                {
                    hashMap.put("customerBankId", rs.getString("customerBankId"));
                }
                if (functions.isValueNull(rs.getString("merchantUsersCommission")))
                {
                    hashMap.put("merchantUsersCommission", rs.getString("merchantUsersCommission"));
                }
                if (functions.isValueNull(rs.getString("merchantUserCommCurrency")))
                {
                    hashMap.put("merchantUserCommCurrency", rs.getString("merchantUserCommCurrency"));
                }
                if (functions.isValueNull(rs.getString("commissionPaidToUser")))
                {
                    hashMap.put("commissionPaidToUser", rs.getString("commissionPaidToUser"));
                }
                if (functions.isValueNull(rs.getString("commPaidToUserCurrency")))
                {
                    hashMap.put("commPaidToUserCurrency", rs.getString("commPaidToUserCurrency"));
                }
            }
            transactionLogger.debug("detail table for VM---" + p);
        }
        catch (SystemError systemError)
        {
            log.error("System error", systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException", e);
        }
        finally
        {
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
            Database.closeConnection(connection);
        }
        return hashMap;
    }

    public void updatePaymentIdforCommon(CommResponseVO commResponseVO, String trackingid) throws PZDBViolationException
    {
        Connection connection = null;

        PreparedStatement preparedStatement = null;
        try
        {
            connection = Database.getConnection();
            String updateRecord = "UPDATE transaction_common SET paymentid=?,transaction_mode=? WHERE trackingid=?";
            preparedStatement = connection.prepareStatement(updateRecord);
            preparedStatement.setString(1, commResponseVO.getTransactionId());
            preparedStatement.setString(2, commResponseVO.getThreeDVersion());
            preparedStatement.setString(3, trackingid);
            int i = preparedStatement.executeUpdate();

            transactionLogger.debug("updated -------"+i);
            transactionLogger.debug("updated yess   ------------ -------");

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updatePaymentIdforCommon()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updatePaymentIdforCommon()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    public void updatePaymentRemarkforCommon(String remark, String trackingid) throws PZDBViolationException
    {
        Connection connection = null;

        PreparedStatement preparedStatement = null;
        try
        {
            connection = Database.getConnection();
            String updateRecord = "UPDATE transaction_common SET remark=? WHERE trackingid=?";
            preparedStatement = connection.prepareStatement(updateRecord);
            preparedStatement.setString(1, remark);
            preparedStatement.setString(2, trackingid);
            int i = preparedStatement.executeUpdate();

            transactionLogger.debug("Remark updated -------"+i);
            transactionLogger.debug("updated yess   ------------ -------");

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updatePaymentIdforCommon()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updatePaymentIdforCommon()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    public void updatePaymentTransactionModeforCommon(String mode, String trackingid) throws PZDBViolationException
    {
        Connection connection = null;

        PreparedStatement preparedStatement = null;
        try
        {
            connection = Database.getConnection();
            String updateRecord = "UPDATE transaction_common SET transaction_mode=? WHERE trackingid=?";
            preparedStatement = connection.prepareStatement(updateRecord);
            preparedStatement.setString(1, mode);
            preparedStatement.setString(2, trackingid);
            int i = preparedStatement.executeUpdate();

            transactionLogger.debug("Transaction Mode updated -------"+i);
            transactionLogger.debug("updated yess   ------------ -------");

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updatePaymentIdforCommon()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updatePaymentIdforCommon()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    public void updatePaymentIdforCommon(String paymentId, String trackingid, String status) throws PZDBViolationException
    {
        Connection connection = null;

        PreparedStatement preparedStatement = null;
        try
        {
            connection = Database.getConnection();
            String updateRecord = "UPDATE transaction_common SET paymentid=?,status=? WHERE trackingid=?";
            preparedStatement = connection.prepareStatement(updateRecord);
            preparedStatement.setString(1, paymentId);
            preparedStatement.setString(2, status);
            preparedStatement.setString(3, trackingid);
            int i = preparedStatement.executeUpdate();

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "updatePaymentIdforSofort()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "updatePaymentIdforSofort()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
    }

    public void updateCaptureforPaysafeCard(String paymentId, String trackingid, String status, String amount) throws PZDBViolationException
    {
        Connection connection = null;

        PreparedStatement preparedStatement = null;
        try
        {
            connection = Database.getConnection();
            String updateRecord = "UPDATE transaction_common SET paymentid=?,status=?,captureamount=? WHERE trackingid=?";
            preparedStatement = connection.prepareStatement(updateRecord);
            preparedStatement.setString(1, paymentId);
            preparedStatement.setString(2, status);
            preparedStatement.setString(3, amount);
            preparedStatement.setString(4, trackingid);
            int i = preparedStatement.executeUpdate();

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "updatePaymentIdforSofort()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "updatePaymentIdforSofort()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    public void updateAuthstartedTransactionForQwipiEcoreCommon(CommonValidatorVO commonValidatorVO, String trackingid, String tablename) throws PZDBViolationException
    {
        Connection connection               = null;
        Transaction transaction             = new Transaction();
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO       = commonValidatorVO.getCardDetailsVO();
        PreparedStatement preparedStatement             = null;
        CommResponseVO commResponseVO                   = new CommResponseVO();
        commResponseVO.setIpaddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        String transaction_mode     = null;

        if("N".equalsIgnoreCase(merchantDetailsVO.getIsService()))
            transaction_mode="PA";
        else if("Y".equalsIgnoreCase(merchantDetailsVO.getIsService()))
            transaction_mode="DB";

        String cardNum = "";

        if(merchantDetailsVO.getIsCardStorageRequired().equalsIgnoreCase("N") && functions.isValueNull(genericCardDetailsVO.getCardNum()))
        {
            cardNum = "";
        }else{
            cardNum =   PzEncryptor.encryptPAN(genericCardDetailsVO.getCardNum());
        }
        try
        {

            connection = Database.getConnection();
            if (tablename.equals("transaction_common"))
            {
                String updateRecord = "UPDATE " + tablename + " SET ccnum=?,firstname=?,lastname=?,name=?,street=?,country =?,city =?,state =?,zip =?,telno =?,telnocc =?,expdate=?,cardtype=?,emailaddr=?,STATUS='authstarted',ipaddress=?,fromtype=?,fromid=?,paymodeid=?,cardtypeid=?,terminalid=?,accountid=?,emiCount=?,amount=?,currency=?,templateamount=?,templatecurrency=?,transaction_type=? WHERE trackingid=?";
                preparedStatement   = connection.prepareStatement(updateRecord);
                preparedStatement.setString(1, cardNum);
                // preparedStatement.setString(1, PzEncryptor.encryptPAN(genericCardDetailsVO.getCardNum()));
                preparedStatement.setString(2, genericAddressDetailsVO.getFirstname());
                preparedStatement.setString(3, genericAddressDetailsVO.getLastname());
                preparedStatement.setString(4, genericAddressDetailsVO.getFirstname() + " " + genericAddressDetailsVO.getLastname());
                preparedStatement.setString(5, genericAddressDetailsVO.getStreet());
                preparedStatement.setString(6, genericAddressDetailsVO.getCountry());
                preparedStatement.setString(7, genericAddressDetailsVO.getCity());
                preparedStatement.setString(8, genericAddressDetailsVO.getState());
                preparedStatement.setString(9, genericAddressDetailsVO.getZipCode());
                preparedStatement.setString(10, genericAddressDetailsVO.getPhone());
                preparedStatement.setString(11, genericAddressDetailsVO.getTelnocc());
                preparedStatement.setString(12, PzEncryptor.encryptExpiryDate(genericCardDetailsVO.getExpMonth() + "/" + genericCardDetailsVO.getExpYear()));
                preparedStatement.setString(13, Functions.getCardType(genericCardDetailsVO.getCardNum()));
                preparedStatement.setString(14, genericAddressDetailsVO.getEmail());
                preparedStatement.setString(15, genericAddressDetailsVO.getIp());
                preparedStatement.setString(16, commonValidatorVO.getTransDetailsVO().getFromtype());
                preparedStatement.setString(17, commonValidatorVO.getTransDetailsVO().getFromid());
                preparedStatement.setString(18, commonValidatorVO.getPaymentType());
                preparedStatement.setString(19, commonValidatorVO.getCardType());
                preparedStatement.setString(20, commonValidatorVO.getTerminalId());
                preparedStatement.setString(21, merchantDetailsVO.getAccountId());
                preparedStatement.setString(22, commonValidatorVO.getTransDetailsVO().getEmiCount());
                preparedStatement.setString(23, commonValidatorVO.getTransDetailsVO().getAmount());
                preparedStatement.setString(24, commonValidatorVO.getTransDetailsVO().getCurrency());
                preparedStatement.setString(25, commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
                preparedStatement.setString(26, commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
                preparedStatement.setString(27, transaction_mode);
                preparedStatement.setString(28, trackingid);
            }
            else
            {
                String updateRecord = "UPDATE " + tablename + " SET ccnum=?, firstname=? , lastname=? ,name=?,street=?,country =?,city =?,state =?,zip =?,telno =?,birthdate =?,telnocc =?,LANGUAGE =?,expdate=?,cardtype=?,emailaddr=?,STATUS='authstarted',ipaddress=?,fromtype=?,fromid=?,paymodeid=?,cardtypeid=?,terminalid=?,accountid=? WHERE trackingid=?";
                preparedStatement = connection.prepareStatement(updateRecord);
                preparedStatement.setString(1, PzEncryptor.encryptPAN(genericCardDetailsVO.getCardNum()));
                preparedStatement.setString(2, genericAddressDetailsVO.getFirstname());
                preparedStatement.setString(3, genericAddressDetailsVO.getLastname());
                preparedStatement.setString(4, genericAddressDetailsVO.getFirstname() + " " + genericAddressDetailsVO.getLastname());
                preparedStatement.setString(5, genericAddressDetailsVO.getStreet());
                preparedStatement.setString(6, genericAddressDetailsVO.getCountry());
                preparedStatement.setString(7, genericAddressDetailsVO.getCity());
                preparedStatement.setString(8, genericAddressDetailsVO.getState());
                preparedStatement.setString(9, genericAddressDetailsVO.getZipCode());
                preparedStatement.setString(10, genericAddressDetailsVO.getPhone());
                preparedStatement.setString(11, genericAddressDetailsVO.getBirthdate());
                preparedStatement.setString(12, genericAddressDetailsVO.getTelnocc());
                preparedStatement.setString(13, genericAddressDetailsVO.getLanguage());
                preparedStatement.setString(14, PzEncryptor.encryptExpiryDate(genericCardDetailsVO.getExpMonth() + "/" + genericCardDetailsVO.getExpYear()));
                preparedStatement.setString(15, Functions.getCardType(genericCardDetailsVO.getCardNum()));
                preparedStatement.setString(16, genericAddressDetailsVO.getEmail());
                preparedStatement.setString(17, genericAddressDetailsVO.getIp());
                preparedStatement.setString(18, commonValidatorVO.getTransDetailsVO().getFromtype());
                preparedStatement.setString(19, commonValidatorVO.getTransDetailsVO().getFromid());
                preparedStatement.setString(20, commonValidatorVO.getPaymentType());
                preparedStatement.setString(21, commonValidatorVO.getCardType());
                preparedStatement.setString(22, commonValidatorVO.getTerminalId());
                preparedStatement.setString(23, merchantDetailsVO.getAccountId());
                preparedStatement.setString(24, trackingid);
                log.debug("update authstarted query---" + updateRecord);
            }
            //preparedStatement.executeQuery("SET NAMES 'UTF8'");
            //preparedStatement.executeQuery("SET CHARACTER SET 'UTF8'");
            int i = preparedStatement.executeUpdate();


            //Update Bin Details
            commonValidatorVO.setTrackingid(trackingid);
            transaction.updateBinDetails(commonValidatorVO);
            /*if ("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getIsCvvStore()))
            {
                insertCvv(commonValidatorVO);
            }*/
            statusSyncDAO.updateAllTransactionFlowFlag(trackingid, "authstarted");
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "updateAuthstartedTransactionForQwipiEcoreCommon()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "updateAuthstartedTransactionForQwipiEcoreCommon()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }
    public void updateAuthstartedTransactionForQwipiEcoreCommonForMarketPlace(CommonValidatorVO commonValidatorVO, String trackingid,String amount, String tablename) throws PZDBViolationException
    {
        Connection connection                   = null;
        Transaction transaction                 = new Transaction();
        MerchantDetailsVO merchantDetailsVO     = commonValidatorVO.getMerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO       = commonValidatorVO.getCardDetailsVO();
        PreparedStatement preparedStatement = null;
        CommResponseVO commResponseVO       = new CommResponseVO();
        commResponseVO.setIpaddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        String transaction_mode     = null;

        if("N".equalsIgnoreCase(merchantDetailsVO.getIsService()))
            transaction_mode="PA";
        else if("Y".equalsIgnoreCase(merchantDetailsVO.getIsService()))
            transaction_mode="DB";

        String cardNum = "";

        if(merchantDetailsVO.getIsCardStorageRequired().equalsIgnoreCase("N") && functions.isValueNull(genericCardDetailsVO.getCardNum()))
        {
            cardNum = "";
        }else{
            cardNum =   PzEncryptor.encryptPAN(genericCardDetailsVO.getCardNum());
        }
        try
        {

            connection = Database.getConnection();
            if (tablename.equals("transaction_common"))
            {
                String updateRecord = "UPDATE " + tablename + " SET ccnum=?,firstname=?,lastname=?,name=?,street=?,country =?,city =?,state =?,zip =?,telno =?,telnocc =?,expdate=?,cardtype=?,emailaddr=?,STATUS='authstarted',ipaddress=?,fromtype=?,fromid=?,paymodeid=?,cardtypeid=?,terminalid=?,accountid=?,emiCount=?,amount=?,currency=?,templateamount=?,templatecurrency=?,transaction_type=? WHERE trackingid=?";
                preparedStatement = connection.prepareStatement(updateRecord);
                //preparedStatement.setString(1, PzEncryptor.encryptPAN(genericCardDetailsVO.getCardNum()));
                preparedStatement.setString(1, cardNum);
                preparedStatement.setString(2, genericAddressDetailsVO.getFirstname());
                preparedStatement.setString(3, genericAddressDetailsVO.getLastname());
                preparedStatement.setString(4, genericAddressDetailsVO.getFirstname() + " " + genericAddressDetailsVO.getLastname());
                preparedStatement.setString(5, genericAddressDetailsVO.getStreet());
                preparedStatement.setString(6, genericAddressDetailsVO.getCountry());
                preparedStatement.setString(7, genericAddressDetailsVO.getCity());
                preparedStatement.setString(8, genericAddressDetailsVO.getState());
                preparedStatement.setString(9, genericAddressDetailsVO.getZipCode());
                preparedStatement.setString(10, genericAddressDetailsVO.getPhone());
                preparedStatement.setString(11, genericAddressDetailsVO.getTelnocc());
                preparedStatement.setString(12, PzEncryptor.encryptExpiryDate(genericCardDetailsVO.getExpMonth() + "/" + genericCardDetailsVO.getExpYear()));
                preparedStatement.setString(13, Functions.getCardType(genericCardDetailsVO.getCardNum()));
                preparedStatement.setString(14, genericAddressDetailsVO.getEmail());
                preparedStatement.setString(15, genericAddressDetailsVO.getIp());
                preparedStatement.setString(16, commonValidatorVO.getTransDetailsVO().getFromtype());
                preparedStatement.setString(17, commonValidatorVO.getTransDetailsVO().getFromid());
                preparedStatement.setString(18, commonValidatorVO.getPaymentType());
                preparedStatement.setString(19, commonValidatorVO.getCardType());
                preparedStatement.setString(20, commonValidatorVO.getTerminalId());
                preparedStatement.setString(21, merchantDetailsVO.getAccountId());
                preparedStatement.setString(22, commonValidatorVO.getTransDetailsVO().getEmiCount());
                preparedStatement.setString(23, amount);
                preparedStatement.setString(24, commonValidatorVO.getTransDetailsVO().getCurrency());
                preparedStatement.setString(25, commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
                preparedStatement.setString(26, commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
                preparedStatement.setString(27, transaction_mode);
                preparedStatement.setString(28, trackingid);
            }
            else
            {
                String updateRecord = "UPDATE " + tablename + " SET ccnum=?, firstname=? , lastname=? ,name=?,street=?,country =?,city =?,state =?,zip =?,telno =?,birthdate =?,telnocc =?,LANGUAGE =?,expdate=?,cardtype=?,emailaddr=?,STATUS='authstarted',ipaddress=?,fromtype=?,fromid=?,paymodeid=?,cardtypeid=?,terminalid=?,accountid=? WHERE trackingid=?";
                preparedStatement = connection.prepareStatement(updateRecord);
                preparedStatement.setString(1, PzEncryptor.encryptPAN(genericCardDetailsVO.getCardNum()));
                preparedStatement.setString(2, genericAddressDetailsVO.getFirstname());
                preparedStatement.setString(3, genericAddressDetailsVO.getLastname());
                preparedStatement.setString(4, genericAddressDetailsVO.getFirstname() + " " + genericAddressDetailsVO.getLastname());
                preparedStatement.setString(5, genericAddressDetailsVO.getStreet());
                preparedStatement.setString(6, genericAddressDetailsVO.getCountry());
                preparedStatement.setString(7, genericAddressDetailsVO.getCity());
                preparedStatement.setString(8, genericAddressDetailsVO.getState());
                preparedStatement.setString(9, genericAddressDetailsVO.getZipCode());
                preparedStatement.setString(10, genericAddressDetailsVO.getPhone());
                preparedStatement.setString(11, genericAddressDetailsVO.getBirthdate());
                preparedStatement.setString(12, genericAddressDetailsVO.getTelnocc());
                preparedStatement.setString(13, genericAddressDetailsVO.getLanguage());
                preparedStatement.setString(14, PzEncryptor.encryptExpiryDate(genericCardDetailsVO.getExpMonth() + "/" + genericCardDetailsVO.getExpYear()));
                preparedStatement.setString(15, Functions.getCardType(genericCardDetailsVO.getCardNum()));
                preparedStatement.setString(16, genericAddressDetailsVO.getEmail());
                preparedStatement.setString(17, genericAddressDetailsVO.getIp());
                preparedStatement.setString(18, commonValidatorVO.getTransDetailsVO().getFromtype());
                preparedStatement.setString(19, commonValidatorVO.getTransDetailsVO().getFromid());
                preparedStatement.setString(20, commonValidatorVO.getPaymentType());
                preparedStatement.setString(21, commonValidatorVO.getCardType());
                preparedStatement.setString(22, commonValidatorVO.getTerminalId());
                preparedStatement.setString(23, merchantDetailsVO.getAccountId());
                preparedStatement.setString(24, trackingid);
                log.debug("update authstarted query---" + updateRecord);
            }
            //preparedStatement.executeQuery("SET NAMES 'UTF8'");
            //preparedStatement.executeQuery("SET CHARACTER SET 'UTF8'");
            int i = preparedStatement.executeUpdate();


            //Update Bin Details
            commonValidatorVO.setTrackingid(trackingid);
            transaction.updateBinDetails(commonValidatorVO);
            /*if ("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getIsCvvStore()))
            {
                insertCvv(commonValidatorVO);
            }*/
            statusSyncDAO.updateAllTransactionFlowFlag(trackingid, "authstarted");
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "updateAuthstartedTransactionForQwipiEcoreCommon()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "updateAuthstartedTransactionForQwipiEcoreCommon()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    public void updateTransactionAfterResponse(String tablename, String status, String amount, String machineid, String paymentid, String remark, String dateTime, String trackingId) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try
        {
            if (tablename != null)
            {
                connection = Database.getConnection();
                String updateRecord = "";

                if (tablename.equals("transaction_common"))
                {
                    if (status.equals("capturesuccess"))
                    {
                        updateRecord = "update transaction_common set status=?,captureamount=?,machineid=?,paymentid=?,remark=?, successtimestamp = ? where trackingid =?";
                        preparedStatement = connection.prepareStatement(updateRecord);
                        preparedStatement.setString(1, status);
                        preparedStatement.setString(2, amount);
                        preparedStatement.setString(3, machineid);
                        preparedStatement.setString(4, paymentid);
                        preparedStatement.setString(5, remark);
                        preparedStatement.setString(6, functions.getTimestamp());
                        preparedStatement.setString(7, trackingId);
                        preparedStatement.executeUpdate();
                    }
                    else
                    {
                        updateRecord = "update transaction_common set status=?,machineid=?,paymentid=?,remark=?, failuretimestamp = ? where trackingid =?";
                        preparedStatement = connection.prepareStatement(updateRecord);
                        preparedStatement.setString(1, status);
                        preparedStatement.setString(2, machineid);
                        preparedStatement.setString(3, paymentid);
                        preparedStatement.setString(4, remark);
                        preparedStatement.setString(5, functions.getTimestamp());
                        preparedStatement.setString(6, trackingId);
                        preparedStatement.executeUpdate();
                    }
                }
                log.debug("updatre query---" + updateRecord + status + paymentid + trackingId);
            }

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "updateTransactionAfterResponse()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "updateTransactionAfterResponse()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
            Database.closePreparedStatement(preparedStatement);
        }
    }


    public void updatereversedTransactionAfterResponse(String tablename, String status, String amount, String machineid, String paymentid, String remark, String dateTime, String trackingId) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try
        {
            if (tablename != null)
            {
                connection = Database.getConnection();
                String updateRecord = "";

                updateRecord = "update transaction_common set status=?,refundamount=?,machineid=?,paymentid=?,remark=?, refundtimestamp=? where trackingid =?";
                preparedStatement = connection.prepareStatement(updateRecord);
                preparedStatement.setString(1, status);
                preparedStatement.setString(2, amount);
                preparedStatement.setString(3, machineid);
                preparedStatement.setString(4, paymentid);
                preparedStatement.setString(5, remark);
                preparedStatement.setString(6, functions.getTimestamp());
                preparedStatement.setString(7, trackingId);
                preparedStatement.executeUpdate();

                log.debug("updatre query---" + updateRecord + status + paymentid + trackingId);
            }

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "updateTransactionAfterResponse()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "updateTransactionAfterResponse()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
            Database.closePreparedStatement(preparedStatement);
        }
    }

    public void updateAuthFailTransactionForCommon(String status, String paymentid, String remark, String trackingId) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try
        {

            connection = Database.getConnection();
            String updateRecord = "";


            updateRecord = "update transaction_common set status=?,paymentid=?,remark=? where trackingid =?";
            preparedStatement = connection.prepareStatement(updateRecord);
            preparedStatement.setString(1, status);
            preparedStatement.setString(2, paymentid);
            preparedStatement.setString(3, remark);
            preparedStatement.setString(4, trackingId);


        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "updateAuthFailTransactionForCommon()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "updateAuthFailTransactionForCommon()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }


    public Hashtable getTransactionDetailsForCommon(String trackingId) throws PZDBViolationException
    {
        Hashtable transaDetails = new Hashtable();
        Connection con = null;
        PreparedStatement p1 = null;
        ResultSet rs = null;
        try
        {
            con = Database.getConnection();
            String sql = "select amount,paymentid,emailaddr,toid,fromid,description,orderdescription,dtstamp,status,httpheader,templatecurrency,templateamount,redirecturl,ccnum,name,accountId,currency,ipaddress,paymodeid,cardtypeid,customerId from transaction_common where trackingid =?";
            p1 = con.prepareStatement(sql);
            p1.setString(1, trackingId);
            rs = p1.executeQuery();

            if (rs.next())
            {
                transaDetails.put("toid", String.valueOf(rs.getInt("toid")));
                if (rs.getString("amount") == null)
                {
                    transaDetails.put("amount", "");
                }
                else
                {
                    transaDetails.put("amount", rs.getString("amount"));
                }
                transaDetails.put("accountid", rs.getString("accountid"));
                transaDetails.put("fromid", rs.getString("fromid"));
                if (rs.getString("paymentid") == null)
                {
                    transaDetails.put("paymentid", "");
                }
                else
                {
                    transaDetails.put("paymentid", rs.getString("paymentid"));
                }
                if (rs.getString("description") == null)
                {
                    transaDetails.put("description", "");
                }
                else
                {
                    transaDetails.put("description", rs.getString("description"));
                }
                if (rs.getString("orderdescription") == null)
                {
                    transaDetails.put("orderdescription", "");
                }
                else
                {
                    transaDetails.put("orderdescription", rs.getString("orderdescription"));

                    transaDetails.put("dtstamp", rs.getString("dtstamp"));
                    transaDetails.put("httpheader", rs.getString("httpheader"));
                }
                if (rs.getString("templatecurrency") == null)
                {
                    transaDetails.put("templatecurrency", "");
                }
                else
                {
                    transaDetails.put("templatecurrency", rs.getString("templatecurrency"));
                    transaDetails.put("templateamount", rs.getString("templateamount"));
                    transaDetails.put("status", rs.getString("status"));
                    transaDetails.put("emailaddr", rs.getString("emailaddr"));
                    transaDetails.put("paymodeid", rs.getString("paymodeid"));
                    transaDetails.put("cardtypeid", rs.getString("cardtypeid"));
                }
                if (rs.getString("name") == null)
                {
                    transaDetails.put("name", "");
                }
                else
                {
                    transaDetails.put("name", rs.getString("name"));
                }
                /*if(pzEncryptor.decryptPAN(rs.getString("ccnum")) == null)
                    transaDetails.put("ccnum","");
                else
                    transaDetails.put("ccnum",pzEncryptor.decryptPAN(rs.getString("ccnum")));*/
                if (rs.getString("currency") == null)
                {
                    transaDetails.put("currency", "");
                }
                else
                {
                    transaDetails.put("currency", rs.getString("currency"));
                    transaDetails.put("redirecturl", rs.getString("redirecturl"));
                }
                if (rs.getString("ipaddress") == null)
                {
                    transaDetails.put("ipaddress", "");
                }
                else
                {
                    transaDetails.put("ipaddress", rs.getString("ipaddress"));
                }

                if (rs.getString("customerId") == null)
                {
                    transaDetails.put("customerId", "");
                }
                else
                {
                    transaDetails.put("customerId", rs.getString("customerId"));
                }

            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "getTransactionDetailsForCommon()", null, "common", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java.java", "getTransactionDetailsForCommon()", null, "common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(p1);
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
        return transaDetails;
    }


    public Hashtable getAccountIdandPaymeIdForCommon(String trackingId) throws PZDBViolationException
    {
        String accountId = null;
        String paymentId = null;
        String toid = null;
        Connection con = null;
        PreparedStatement p1 = null;
        ResultSet rs = null;
        Hashtable details = new Hashtable();
        try
        {
            con = Database.getConnection();
            String sql = "select accountid,paymentid,toid,amount from transaction_common where trackingid =?";
            p1 = con.prepareStatement(sql);
            p1.setString(1, trackingId);
            rs = p1.executeQuery();

            if (rs.next())
            {

                accountId = rs.getString("accountid");
                paymentId = rs.getString("paymentid");
                toid = rs.getString("toid");
                details.put("accountid", accountId);
                details.put("paymentid", paymentId);
                details.put("toid", toid);
                details.put("amount", rs.getString("amount"));
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "getAccountIdForCommon()", null, "common", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java.java", "getAccountIdForCommon()", null, "common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(p1);
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
        return details;
    }

    public String loadReason(String rs, String gatewayName) throws PZDBViolationException
    {
        String failreason = "";
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String qry = "select * from rs_codes where gateway='" + gatewayName + "' and code='" + rs + "'";
            PreparedStatement p = connection.prepareStatement(qry);
            ResultSet result = p.executeQuery();
            if (result.next())
            {
                failreason = result.getString("reason");
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "loadReason()", null, "common", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException se)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "loadReason()", null, "common", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return failreason;
    }


    public int insertAuthStartedForRecurring(TransactionDetailsVO transactionDetailsVO) throws PZDBViolationException
    {
        int trackingId = 0;
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String query = "insert into transaction_common(toid,totype,fromid,fromtype,description,orderdescription,amount,redirecturl,status,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,ipaddress,firstname,lastname,name,ccnum,expdate,cardtype,emailaddr,country,state,city,street,zip,telnocc,telno) values(?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement p = con.prepareStatement(query);

            p.setString(1, transactionDetailsVO.getToid());
            p.setString(2, transactionDetailsVO.getTotype());
            p.setString(3, transactionDetailsVO.getFromid());
            p.setString(4, transactionDetailsVO.getFromtype());
            p.setString(5, transactionDetailsVO.getDescription());
            p.setString(6, transactionDetailsVO.getOrderDescription());
            p.setString(7, transactionDetailsVO.getAmount());
            p.setString(8, transactionDetailsVO.getRedirectURL());
            p.setString(9, ActionEntry.STATUS_AUTHORISTION_STARTED);
            p.setString(10, transactionDetailsVO.getAccountId());
            p.setString(11, transactionDetailsVO.getPaymodeId());
            p.setString(12, transactionDetailsVO.getCardTypeId());
            p.setString(13, transactionDetailsVO.getCurrency());
            p.setString(14, transactionDetailsVO.getHttpHeader());
            p.setString(15, transactionDetailsVO.getIpAddress());
            p.setString(16, transactionDetailsVO.getFirstName());
            p.setString(17, transactionDetailsVO.getLastName());
            p.setString(18, transactionDetailsVO.getFirstName() + " " + transactionDetailsVO.getLastName());
            p.setString(19, transactionDetailsVO.getCcnum());
            p.setString(20, transactionDetailsVO.getExpdate());
            p.setString(21, transactionDetailsVO.getCardtype());
            p.setString(22, transactionDetailsVO.getEmailaddr());
            p.setString(23, transactionDetailsVO.getCountry());
            p.setString(24, transactionDetailsVO.getState());
            p.setString(25, transactionDetailsVO.getCity());
            p.setString(26, transactionDetailsVO.getStreet());
            p.setString(27, transactionDetailsVO.getZip());
            p.setString(28, transactionDetailsVO.getTelcc());
            p.setString(29, transactionDetailsVO.getTelno());

            int num = p.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {
                        trackingId = rs.getInt(1);
                    }
                }
            }
            log.debug("Insert Query trans_common---" + query + "---generated Trackingid---" + trackingId);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "insertAuthStartedForRecurring()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "insertAuthStartedForRecurring()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return trackingId;
    }


    public int insertTransactionCommon(String toid, String totype, String fromid, String fromtype, String descreption, String orderdescription, String amount, String redirecturl, String accontid, int paymodeid, int cardtypeid, String currency, String httphadder, String ipaddress, String status, String terminalID) throws PZDBViolationException
    {
        int TRACKING_ID = 0;
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String query = "insert into transaction_common(toid,totype,fromid,fromtype,description,orderdescription,amount,redirecturl,status,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,ipaddress, terminalid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?)";
            PreparedStatement p = con.prepareStatement(query);
            p.setString(1, toid);
            p.setString(2, totype);
            p.setString(3, fromid);
            p.setString(4, fromtype);
            p.setString(5, descreption);
            p.setString(6, orderdescription);
            p.setString(7, amount);
            p.setString(8, redirecturl);

            p.setString(9, status);
            p.setString(10, accontid);
            p.setInt(11, paymodeid);
            p.setInt(12, cardtypeid);
            p.setString(13, currency);
            p.setString(14, httphadder);
            p.setString(15, ipaddress);
            p.setString(16, terminalID);
            int num = p.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {

                        TRACKING_ID = rs.getInt(1);
                    }
                }
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException(PaymentDAO.class.getName(), "insertTransactionCommon()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(PaymentDAO.class.getName(), "insertTransactionCommon()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

        return (TRACKING_ID);


    }

    public int insertTransactionCommonForPayout(String toid, String totype, String fromid, String fromtype, String descreption, String orderdescription, String amount, String redirecturl, String accontid, int paymodeid, int cardtypeid, String currency, String httphadder, String ipaddress, String status, String terminalID, String name, String ccnum, String firstname, String lastname, String street, String country, String city, String state, String zip, String telno, String telnocc, String expdate, String email, String tmpl_amount, String tmpl_currency, String customerid, String notificationUrl, String ewalletid,String cardTypeName) throws PZDBViolationException
    {
        int TRACKING_ID = 0;
        Connection con  = null;
        try
        {
            con                 = Database.getConnection();
            String query        = "insert into transaction_common(toid,totype,fromid,fromtype,description,orderdescription,amount,redirecturl,status,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,ipaddress, terminalid,name,ccnum,firstname,lastname,street,country,city,state,zip,telno,telnocc,expdate,emailaddr,templateamount,templatecurrency,customerId,notificationUrl,ewalletid,cardType) values(?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement p = con.prepareStatement(query);
            p.setString(1, toid);
            p.setString(2, totype);
            p.setString(3, fromid);
            p.setString(4, fromtype);
            p.setString(5, descreption);
            p.setString(6, orderdescription);
            p.setString(7, amount);
            p.setString(8, redirecturl);

            p.setString(9, status);
            p.setString(10, accontid);
            p.setInt(11, paymodeid);
            p.setInt(12, cardtypeid);
            p.setString(13, currency);
            p.setString(14, httphadder);
            p.setString(15, ipaddress);
            p.setString(16, terminalID);
            p.setString(17, name);
            p.setString(18, ccnum);
            p.setString(19, firstname);
            p.setString(20, lastname);
            p.setString(21, street);
            p.setString(22, country);
            p.setString(23, city);
            p.setString(24, state);
            p.setString(25, zip);
            p.setString(26, telno);
            p.setString(27, telnocc);
            p.setString(28, expdate);
            p.setString(29, email);
            p.setString(30, tmpl_amount);
            p.setString(31, tmpl_currency);
            p.setString(32, customerid);
            p.setString(33, notificationUrl);
            p.setString(34, ewalletid);
            p.setString(35, cardTypeName);
            int num = p.executeUpdate();
            transactionLogger.debug("authStarted query----" + p);
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {

                        TRACKING_ID = rs.getInt(1);
                    }
                }
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException(PaymentDAO.class.getName(), "insertTransactionCommon()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(PaymentDAO.class.getName(), "insertTransactionCommon()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

        return (TRACKING_ID);


    }

    public int insertTransactionCommonForPayoutSuccess(String toid, String totype, String fromid, String fromtype, String descreption, String orderdescription, String amount, String redirecturl, String accontid, int paymodeid, int cardtypeid, String currency, String httphadder, String ipaddress, String status, String terminalID, String name, String ccnum, String firstname, String lastname, String street, String country, String city, String state, String zip, String telno, String telnocc, String expdate, String email, String tmpl_amount, String tmpl_currency, String customerid, String notificationUrl, String ewalletid,String cardTypeName,String paymentid) throws PZDBViolationException
    {
        int TRACKING_ID = 0;
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String query = "insert into transaction_common(toid,totype,fromid,fromtype,description,orderdescription,amount,redirecturl,status,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,ipaddress, terminalid,name,ccnum,firstname,lastname,street,country,city,state,zip,telno,telnocc,expdate,emailaddr,templateamount,templatecurrency,customerId,notificationUrl,remark,cardType,payoutamount,paymentid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement p = con.prepareStatement(query);
            p.setString(1, toid);
            p.setString(2, totype);
            p.setString(3, fromid);
            p.setString(4, fromtype);
            p.setString(5, descreption);
            p.setString(6, orderdescription);
            p.setString(7, amount);
            p.setString(8, redirecturl);
            p.setString(9, status);
            p.setString(10, accontid);
            p.setInt(11, paymodeid);
            p.setInt(12, cardtypeid);
            p.setString(13, currency);
            p.setString(14, httphadder);
            p.setString(15, ipaddress);
            p.setString(16, terminalID);
            p.setString(17, name);
            p.setString(18, ccnum);
            p.setString(19, firstname);
            p.setString(20, lastname);
            p.setString(21, street);
            p.setString(22, country);
            p.setString(23, city);
            p.setString(24, state);
            p.setString(25, zip);
            p.setString(26, telno);
            p.setString(27, telnocc);
            p.setString(28, expdate);
            p.setString(29, email);
            p.setString(30, tmpl_amount);
            p.setString(31, tmpl_currency);
            p.setString(32, customerid);
            p.setString(33, notificationUrl);
            p.setString(34, ewalletid);
            p.setString(35, cardTypeName);
            p.setString(36, amount);
            p.setString(37, paymentid);

            int num = p.executeUpdate();
            transactionLogger.debug("authStarted query----" + p);
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {

                        TRACKING_ID = rs.getInt(1);
                    }
                }
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException(PaymentDAO.class.getName(), "insertTransactionCommonForPayoutSuccess()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(PaymentDAO.class.getName(), "insertTransactionCommonForPayoutSuccess()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

        return (TRACKING_ID);


    }

    public int actionEntryForCommon(String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO requestVO, AuditTrailVO auditTrailVO, String remark) throws PZDBViolationException
    {
        int newDetailId = 0;
        String responsecode = "";
        String dateTime = "";
        String responsetransactionid = "";
        String responsetransactionstatus = "";
        String responseTime = "";
        String responsedescription = "";
        String responsedescriptor = "";
        String responsehashinfo = "";
        String transType = "";
        String ipaddress = "";
        String actionExId = "0";
        String actionExname = "";
        String errorName = "";
        String arn = "";
        String rrn = "";
        String authCode = "";
        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        String walletAmount = "";
        String walletCurrency = "";

        Connection cn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        if (responseVO != null)
        {
            responsecode = responseVO.getErrorCode();
            responsetransactionid = String.valueOf(responseVO.getTransactionId());
            responsedescription = responseVO.getDescription();
            responseTime = responseVO.getResponseTime();
            responsetransactionstatus = responseVO.getStatus();
            if (functions.isValueNull(responseVO.getStatus()) && "success".equalsIgnoreCase(responseVO.getStatus().trim()))
            {
                responsedescriptor = responseVO.getDescriptor();
            }
            transType = responseVO.getTransactionType();
            if (functions.isValueNull(responseVO.getResponseHashInfo()))
            {
                responsehashinfo = responseVO.getResponseHashInfo();
                if (!functions.isValueNull(remark))
                    remark = responseVO.getRemark();
            }
            if (functions.isValueNull(responseVO.getErrorName()))
            {
                errorName = responseVO.getErrorName();
            }
            if (functions.isValueNull((responseVO.getArn())))
            {
                arn = responseVO.getArn();
            }
            if (functions.isValueNull((responseVO.getRrn())))
            {
                rrn = responseVO.getRrn();
            }
            if (functions.isValueNull((responseVO.getAuthCode())))
            {
                authCode = responseVO.getAuthCode();
            }
            if (functions.isValueNull(responseVO.getCurrency()))
            {
                currency = responseVO.getCurrency();
            }
            if (functions.isValueNull(responseVO.getTmpl_Amount()))
            {
                tmpl_amount = responseVO.getTmpl_Amount();
            }
            if (functions.isValueNull(responseVO.getTmpl_Currency()))
            {
                tmpl_currency = responseVO.getTmpl_Currency();
            }
            if (functions.isValueNull(responseVO.getWalletAmount()))
            {
                walletAmount = responseVO.getWalletAmount();
            }
            if (functions.isValueNull(responseVO.getWalletCurrecny()))
            {
                walletCurrency = responseVO.getWalletCurrecny();
            }
        }

        if (requestVO != null && responseVO == null)
        {
            if (requestVO.getAddressDetailsVO() != null)
            {
                ipaddress = requestVO.getAddressDetailsVO().getCardHolderIpAddress();
            }
            if (requestVO.getTransDetailsVO() != null)
            {
                CommTransactionDetailsVO commTransactionDetailsVO = requestVO.getTransDetailsVO();
                if (commTransactionDetailsVO != null)
                {
                    responsetransactionid = requestVO.getTransDetailsVO().getResponseHashInfo();

                    if (functions.isValueNull(commTransactionDetailsVO.getWalletAmount()))
                    {
                        walletAmount = commTransactionDetailsVO.getWalletAmount();
                    }
                    if (functions.isValueNull(commTransactionDetailsVO.getWalletCurrency()))
                    {
                        walletCurrency = commTransactionDetailsVO.getWalletCurrency();
                    }
                }
            }

            CommMerchantVO commMerchantVO = requestVO.getCommMerchantVO();
            if (commMerchantVO != null)
            {
                if ("N".equalsIgnoreCase(commMerchantVO.getIsService()))
                {
                    transType = PZProcessType.AUTH.toString();

                }
                else if ("Y".equalsIgnoreCase(commMerchantVO.getIsService()))
                {
                    transType = PZProcessType.SALE.toString();
                }
            }
        }


        if (auditTrailVO != null)
        {
            log.debug("auditTrailVO is not null");
            actionExId = auditTrailVO.getActionExecutorId();
            actionExname = auditTrailVO.getActionExecutorName();
        }

        try
        {
            cn = Database.getConnection();
            String sql = "insert into transaction_common_details(trackingid,amount,action,status,remark,responsetransactionid,responsetransactionstatus,responsecode,responseTime,responseDescription,responsedescriptor,responsehashinfo,transtype,ipaddress,actionexecutorid,actionexecutorname,errorName,arn,currency,templateamount,templatecurrency,walletAmount,walletCurrency,rrn,authorization_code) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            pstmt.setString(2, amount);
            pstmt.setString(3, action);
            pstmt.setString(4, status);
            pstmt.setString(5, remark);
            pstmt.setString(6, responsetransactionid);
            pstmt.setString(7, responsetransactionstatus);
            pstmt.setString(8, responsecode);
            pstmt.setString(9, responseTime);
            pstmt.setString(10, responsedescription);
            pstmt.setString(11, responsedescriptor);
            pstmt.setString(12, responsehashinfo);
            pstmt.setString(13, transType);
            pstmt.setString(14, ipaddress);
            pstmt.setString(15, actionExId);
            pstmt.setString(16, actionExname);
            pstmt.setString(17, errorName);
            pstmt.setString(18, arn);
            pstmt.setString(19, currency);
            pstmt.setString(20, tmpl_amount);
            pstmt.setString(21, tmpl_currency);
            pstmt.setString(22, walletAmount);
            pstmt.setString(23, walletCurrency);
            pstmt.setString(24, rrn);
            pstmt.setString(25, authCode);
            int results = pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            if (rs.next())
            {
                newDetailId = rs.getInt(1);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException(PaymentDAO.class.getName(), "insertTransactionCommonDetails()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(PaymentDAO.class.getName(), "insertTransactionCommonDetails()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeResultSet(rs);
            Database.closeConnection(cn);
        }
        return newDetailId;
    }

    public boolean updateDetailsOfTransactionForCommon(String ccnum, String firstname, String lastname, String street, String country, String city, String state, String zip, String telno, String telnocc, String expdate, String email, String trackingId) throws PZDBViolationException
    {
        Connection con = null;
        int counter = 14;
        int result = 0;
        try
        {
            con = Database.getConnection();
            StringBuffer updateQuery = new StringBuffer("update transaction_common set ccnum= ?, firstname= ?,lastname= ?,name= ?,street= ?,country = ?,city = ?,state = ?,zip = ?,telno = ?,telnocc = ?,expdate= ?,cardtype= ?");
            if (functions.isValueNull(email))
            {
                updateQuery.append(", emailaddr= ?");
            }
            updateQuery.append(" where trackingid= ?");

            PreparedStatement psUpdateTransactionDetails = con.prepareStatement(updateQuery.toString());

            psUpdateTransactionDetails.setString(1, PzEncryptor.encryptPAN(ccnum));
            psUpdateTransactionDetails.setString(2, firstname);
            psUpdateTransactionDetails.setString(3, lastname);
            psUpdateTransactionDetails.setString(4, firstname + " " + lastname);
            psUpdateTransactionDetails.setString(5, street);
            psUpdateTransactionDetails.setString(6, country);
            psUpdateTransactionDetails.setString(7, city);
            psUpdateTransactionDetails.setString(8, state);
            psUpdateTransactionDetails.setString(9, zip);
            psUpdateTransactionDetails.setString(10, telno);
            psUpdateTransactionDetails.setString(11, telnocc);
            psUpdateTransactionDetails.setString(12, PzEncryptor.encryptExpiryDate(expdate));
            psUpdateTransactionDetails.setString(13, Functions.getCardType(ccnum));
            if (functions.isValueNull(email))
            {
                psUpdateTransactionDetails.setString(counter, email);
                counter++;
            }
            psUpdateTransactionDetails.setString(counter, trackingId);
            result = psUpdateTransactionDetails.executeUpdate();
            if (result > 0)
            {
                return true;
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException(PaymentDAO.class.getName(), "updateDetailsOfTransactionForCommon()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(PaymentDAO.class.getName(), "updateDetailsOfTransactionForCommon()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return false;
    }
    public int insertSMSStartedEntryForCupRest(CommonValidatorVO commonValidatorVO, String tableName) throws PZDBViolationException
    {
        int trackingId = 0;
        Connection con = null;
        Transaction transaction = new Transaction();
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        try
        {
            con = Database.getConnection();
            String query = "insert into " + tableName + " (toid,totype,fromid,fromtype,description,orderdescription,amount,redirecturl,status,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,ipaddress,firstname,lastname,name,ccnum,expdate,cardtype,emailaddr,country,state,city,street,zip,telnocc,telno,terminalid,templateamount,templatecurrency,notificationUrl,customerId,emiCount) values(?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement p = con.prepareStatement(query);

            p.setString(1, commonValidatorVO.getMerchantDetailsVO().getMemberId());
            p.setString(2, commonValidatorVO.getTransDetailsVO().getTotype());
            p.setString(3, commonValidatorVO.getTransDetailsVO().getFromid());
            p.setString(4, commonValidatorVO.getTransDetailsVO().getFromtype());
            p.setString(5, commonValidatorVO.getTransDetailsVO().getOrderId());
            p.setString(6, commonValidatorVO.getTransDetailsVO().getOrderDesc());
            p.setString(7, commonValidatorVO.getTransDetailsVO().getAmount());
            p.setString(8, genericTransDetailsVO.getRedirectUrl());
            p.setString(9, "smsstarted");
            p.setString(10, commonValidatorVO.getMerchantDetailsVO().getAccountId());
            p.setString(11, commonValidatorVO.getPaymentType());
            p.setString(12, commonValidatorVO.getCardType());
            p.setString(13, commonValidatorVO.getTransDetailsVO().getCurrency());
            p.setString(14, commonValidatorVO.getTransDetailsVO().getHeader());
            p.setString(15, commonValidatorVO.getAddressDetailsVO().getIp());
            p.setString(16, commonValidatorVO.getAddressDetailsVO().getFirstname());
            p.setString(17, commonValidatorVO.getAddressDetailsVO().getLastname());
            p.setString(18, commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname());
            p.setString(19, PzEncryptor.encryptPAN(commonValidatorVO.getCardDetailsVO().getCardNum()));
            p.setString(20, PzEncryptor.encryptExpiryDate(commonValidatorVO.getCardDetailsVO().getExpMonth() + "/" + commonValidatorVO.getCardDetailsVO().getExpYear()));
            p.setString(21, Functions.getCardType(commonValidatorVO.getCardDetailsVO().getCardNum()));
            p.setString(22, commonValidatorVO.getAddressDetailsVO().getEmail());
            p.setString(23, commonValidatorVO.getAddressDetailsVO().getCountry());
            p.setString(24, commonValidatorVO.getAddressDetailsVO().getState());
            p.setString(25, commonValidatorVO.getAddressDetailsVO().getCity());
            p.setString(26, commonValidatorVO.getAddressDetailsVO().getStreet());
            p.setString(27, commonValidatorVO.getAddressDetailsVO().getZipCode());
            p.setString(28, commonValidatorVO.getAddressDetailsVO().getTelnocc());
            p.setString(29, commonValidatorVO.getAddressDetailsVO().getPhone());
            p.setString(30, commonValidatorVO.getTerminalId());
            p.setString(31, commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
            p.setString(32, commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
            p.setString(33, commonValidatorVO.getTransDetailsVO().getNotificationUrl());
            p.setString(34, commonValidatorVO.getCustomerId());
            p.setString(35, commonValidatorVO.getTransDetailsVO().getEmiCount());

            int num = p.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {
                        trackingId = rs.getInt(1);
                        commonValidatorVO.setTrackingid(String.valueOf(trackingId));
                    }
                }
            }
            transaction.updateBinDetailsforWebService(commonValidatorVO);
            log.debug("Insert Query trans_common---" + p + "---generated Trackingid---" + trackingId);
            if (commonValidatorVO.getTerminalId()!=null)
            {
                /*String isManualRecurring="";

                String query1 = "SELECT isManualRecurring FROM member_account_mapping WHERE terminalid=" + commonValidatorVO.getTerminalId() + "";
                p = con.prepareStatement(query1);
                ResultSet rs = p.executeQuery();
                if (rs.next())
                {
                    isManualRecurring = rs.getString("isManualRecurring");
                    transactionLogger.error("isManualRecurring flag-----"+isManualRecurring);
                }
                transactionLogger.error("Query1----"+p);*/

                /*if ("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getIsCvvStore()))
                {
                    insertCvv(commonValidatorVO);
                }*/
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "insertAuthStartedForRecurring()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "insertAuthStartedForRecurring()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return trackingId;
    }
    public int insertAuthStartedEntry(CommonValidatorVO commonValidatorVO, String tableName) throws PZDBViolationException
    {
        int trackingId                                  = 0;
        Connection con                                  = null;
        Transaction transaction                         = new Transaction();
        MerchantDetailsVO merchantDetailsVO             = commonValidatorVO.getMerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO       = commonValidatorVO.getCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO     = commonValidatorVO.getTransDetailsVO();
        String cardNumber                               = "";


        if(merchantDetailsVO.getIsCardStorageRequired().equalsIgnoreCase("N") && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()) ){
            cardNumber = "";
        }else{
            cardNumber = PzEncryptor.encryptPAN(commonValidatorVO.getCardDetailsVO().getCardNum());
        }
        try
        {
            con                 = Database.getConnection();
            String query        = "insert into " + tableName + " (toid,totype,fromid,fromtype,description,orderdescription,amount,redirecturl,status,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,ipaddress,firstname,lastname,name,ccnum,expdate,cardtype,emailaddr,country,state,city,street,zip,telnocc,telno,terminalid,templateamount,templatecurrency,notificationUrl,customerId,emiCount,merchantIpCountry,customerIP,customerIpCountry,transaction_type,hrcode) values(?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement p = con.prepareStatement(query);

            p.setString(1, commonValidatorVO.getMerchantDetailsVO().getMemberId());
            p.setString(2, commonValidatorVO.getTransDetailsVO().getTotype());
            p.setString(3, commonValidatorVO.getTransDetailsVO().getFromid());
            p.setString(4, commonValidatorVO.getTransDetailsVO().getFromtype());
            p.setString(5, commonValidatorVO.getTransDetailsVO().getOrderId());
            p.setString(6, commonValidatorVO.getTransDetailsVO().getOrderDesc());
            p.setString(7, commonValidatorVO.getTransDetailsVO().getAmount());
            p.setString(8, genericTransDetailsVO.getRedirectUrl());
            p.setString(9, ActionEntry.STATUS_AUTHORISTION_STARTED);
            p.setString(10, commonValidatorVO.getMerchantDetailsVO().getAccountId());
            p.setString(11, commonValidatorVO.getPaymentType());
            p.setString(12, commonValidatorVO.getCardType());
            p.setString(13, commonValidatorVO.getTransDetailsVO().getCurrency());
            p.setString(14, commonValidatorVO.getTransDetailsVO().getHeader());
            p.setString(15, commonValidatorVO.getAddressDetailsVO().getIp());
            p.setString(16, commonValidatorVO.getAddressDetailsVO().getFirstname());
            p.setString(17, commonValidatorVO.getAddressDetailsVO().getLastname());
            p.setString(18, commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname());
            // p.setString(19, PzEncryptor.encryptPAN(commonValidatorVO.getCardDetailsVO().getCardNum()));
            p.setString(19, cardNumber);
            p.setString(20, PzEncryptor.encryptExpiryDate(commonValidatorVO.getCardDetailsVO().getExpMonth() + "/" + commonValidatorVO.getCardDetailsVO().getExpYear()));
            p.setString(21, Functions.getCardType(commonValidatorVO.getCardDetailsVO().getCardNum()));
            p.setString(22, commonValidatorVO.getAddressDetailsVO().getEmail());
            p.setString(23, commonValidatorVO.getAddressDetailsVO().getCountry());
            p.setString(24, commonValidatorVO.getAddressDetailsVO().getState());
            p.setString(25, commonValidatorVO.getAddressDetailsVO().getCity());
            p.setString(26, commonValidatorVO.getAddressDetailsVO().getStreet());
            p.setString(27, commonValidatorVO.getAddressDetailsVO().getZipCode());
            p.setString(28, commonValidatorVO.getAddressDetailsVO().getTelnocc());
            p.setString(29, commonValidatorVO.getAddressDetailsVO().getPhone());
            p.setString(30, commonValidatorVO.getTerminalId());
            p.setString(31, commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
            p.setString(32, commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
            p.setString(33, commonValidatorVO.getTransDetailsVO().getNotificationUrl());
            p.setString(34, commonValidatorVO.getCustomerId());
            p.setString(35, commonValidatorVO.getTransDetailsVO().getEmiCount());
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getIp()))
                p.setString(36,functions.getIPCountryShort(commonValidatorVO.getAddressDetailsVO().getIp()));
            else
                p.setString(36,"");

            p.setString(37,commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()))
                p.setString(38,functions.getIPCountryShort(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()));
            else
                p.setString(38,"");
            p.setString(39,commonValidatorVO.getTransactionType());
            p.setString(40,genericTransDetailsVO.getRedirectMethod());
            int num = p.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {
                        trackingId = rs.getInt(1);
                        commonValidatorVO.setTrackingid(String.valueOf(trackingId));
                    }
                }
            }
            transaction.updateBinDetailsforWebService(commonValidatorVO);
            log.debug("Insert Query trans_common---" + p + "---generated Trackingid---" + trackingId);

            if (commonValidatorVO.getTerminalId()!=null)
            {
                /*String isManualRecurring="";

                String query1 = "SELECT isManualRecurring FROM member_account_mapping WHERE terminalid=" + commonValidatorVO.getTerminalId() + "";
                p = con.prepareStatement(query1);
                ResultSet rs = p.executeQuery();
                if (rs.next())
                {
                    isManualRecurring = rs.getString("isManualRecurring");
                    transactionLogger.error("isManualRecurring flag-----"+isManualRecurring);
                }
                transactionLogger.error("Query1----"+p);*/

                /*if ("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getIsCvvStore()))
                {
                    insertCvv(commonValidatorVO);
                }*/
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "insertAuthStartedForRecurring()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "insertAuthStartedForRecurring()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return trackingId;
    }

    public int insertAuthStartedEntryForAccount(CommonValidatorVO commonValidatorVO, String tableName) throws PZDBViolationException
    {
        int trackingId                                  = 0;
        Connection con                                  = null;
        MerchantDetailsVO merchantDetailsVO             = commonValidatorVO.getMerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO       = commonValidatorVO.getCardDetailsVO();
        try
        {
            con                 = Database.getConnection();
            transactionLogger.debug("PaymentDAO.insertAuthStartedEntry :::::::: DB Call");
            String query        = "insert into " + tableName + " (toid,totype,fromid,fromtype,description,orderdescription,amount,redirecturl,status,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,ipaddress,firstname,lastname,name,/*cardtype,*/emailaddr,country,state,city,street,zip,telnocc,telno,terminalid,templateamount,templatecurrency,notificationUrl,merchantIpCountry,customerIP,customerIpCountry,hrcode) values(?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement p = con.prepareStatement(query);

            p.setString(1, commonValidatorVO.getMerchantDetailsVO().getMemberId());
            p.setString(2, commonValidatorVO.getTransDetailsVO().getTotype());
            p.setString(3, commonValidatorVO.getTransDetailsVO().getFromid());
            p.setString(4, commonValidatorVO.getTransDetailsVO().getFromtype());
            p.setString(5, commonValidatorVO.getTransDetailsVO().getOrderId());
            p.setString(6, commonValidatorVO.getTransDetailsVO().getOrderDesc());
            p.setString(7, commonValidatorVO.getTransDetailsVO().getAmount());
            p.setString(8, commonValidatorVO.getTransDetailsVO().getRedirectUrl());
            p.setString(9, ActionEntry.STATUS_AUTHORISTION_STARTED);
            p.setString(10, commonValidatorVO.getMerchantDetailsVO().getAccountId());
            p.setString(11, commonValidatorVO.getPaymentType());
            p.setString(12, commonValidatorVO.getCardType());
            p.setString(13, commonValidatorVO.getTransDetailsVO().getCurrency());
            p.setString(14, commonValidatorVO.getTransDetailsVO().getHeader());
            p.setString(15, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            p.setString(16, commonValidatorVO.getAddressDetailsVO().getFirstname());
            p.setString(17, commonValidatorVO.getAddressDetailsVO().getLastname());
            p.setString(18, commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname());
            //p.setString(19,commonValidatorVO.getCardDetailsVO().getCardType());
            p.setString(19, commonValidatorVO.getAddressDetailsVO().getEmail());
            p.setString(20, commonValidatorVO.getAddressDetailsVO().getCountry());
            p.setString(21, commonValidatorVO.getAddressDetailsVO().getState());
            p.setString(22, commonValidatorVO.getAddressDetailsVO().getCity());
            p.setString(23, commonValidatorVO.getAddressDetailsVO().getStreet());
            p.setString(24, commonValidatorVO.getAddressDetailsVO().getZipCode());
            p.setString(25, commonValidatorVO.getAddressDetailsVO().getTelnocc());
            p.setString(26, commonValidatorVO.getAddressDetailsVO().getPhone());
            p.setString(27, commonValidatorVO.getTerminalId());
            p.setString(28, commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
            p.setString(29, commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
            p.setString(30, commonValidatorVO.getTransDetailsVO().getNotificationUrl());
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getIp()))
                p.setString(31,functions.getIPCountryShort(commonValidatorVO.getAddressDetailsVO().getIp()));
            else
                p.setString(31,"");

            p.setString(32,commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()))
                p.setString(33,functions.getIPCountryShort(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()));
            else
                p.setString(33,"");

            p.setString(34,commonValidatorVO.getTransDetailsVO().getRedirectMethod());
            int num = p.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {
                        trackingId = rs.getInt(1);
                    }
                }
            }

            commonValidatorVO.setTrackingid(String.valueOf(trackingId));
            updateAuthstartedTransactionForPayMitco(commonValidatorVO, commonValidatorVO.getTrackingid());
            log.debug("Insert Query trans_common---" + p + "---generated Trackingid---" + trackingId);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "insertAuthStartedEntryForAccount()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "insertAuthStartedEntryForAccount()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return trackingId;
    }

    public int insertAuthStartedForCommon(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        PaymentDAO paymentDao = new PaymentDAO();
        int trackingId = 0;
        Connection con = null;
        String cardType = "";
        String cardNum = "";
        try
        {
            con = Database.getConnection();

            String query = "INSERT INTO transaction_common(toid,totype,fromid,fromtype,description,orderdescription,amount,redirecturl,STATUS,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,ipaddress,ccnum,firstname,lastname,NAME,street,country,city,state,zip,telno,telnocc,expdate,cardtype,emailaddr,terminalid,merchantIpCountry,customerIP,customerIpCountry) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,UNIX_TIMESTAMP(NOW()),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement p = con.prepareStatement(query);
            p.setString(1, commonValidatorVO.getMerchantDetailsVO().getMemberId());
            p.setString(2, commonValidatorVO.getTransDetailsVO().getTotype());
            p.setString(3, commonValidatorVO.getTransDetailsVO().getFromid());
            p.setString(4, commonValidatorVO.getTransDetailsVO().getFromtype());
            p.setString(5, commonValidatorVO.getTransDetailsVO().getOrderId());
            p.setString(6, commonValidatorVO.getTransDetailsVO().getOrderDesc());
            p.setString(7, commonValidatorVO.getTransDetailsVO().getAmount());
            p.setString(8, commonValidatorVO.getTransDetailsVO().getRedirectUrl());
            p.setString(9, ActionEntry.STATUS_AUTHORISTION_STARTED);
            p.setString(10, commonValidatorVO.getMerchantDetailsVO().getAccountId());
            p.setString(11, commonValidatorVO.getPaymentType());
            p.setString(12, commonValidatorVO.getCardType());
            p.setString(13, commonValidatorVO.getMerchantDetailsVO().getCurrency());
            //p.setString(14,dtstamp);
            p.setString(14, commonValidatorVO.getTransDetailsVO().getHeader());
            p.setString(15, commonValidatorVO.getAddressDetailsVO().getIp());
            p.setString(16, PzEncryptor.encryptPAN(commonValidatorVO.getCardDetailsVO().getCardNum()));
            p.setString(17, commonValidatorVO.getAddressDetailsVO().getFirstname());
            p.setString(18, commonValidatorVO.getAddressDetailsVO().getLastname());
            p.setString(19, commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname());
            p.setString(20, commonValidatorVO.getAddressDetailsVO().getStreet());
            p.setString(21, commonValidatorVO.getAddressDetailsVO().getCountry());
            p.setString(22, commonValidatorVO.getAddressDetailsVO().getCity());
            p.setString(23, commonValidatorVO.getAddressDetailsVO().getState());
            p.setString(24, commonValidatorVO.getAddressDetailsVO().getZipCode());
            p.setString(25, commonValidatorVO.getAddressDetailsVO().getPhone());
            p.setString(26, commonValidatorVO.getAddressDetailsVO().getTelnocc());
            p.setString(27, PzEncryptor.encryptExpiryDate(commonValidatorVO.getCardDetailsVO().getExpMonth() + "/" + commonValidatorVO.getCardDetailsVO().getExpYear()));
            if (!commonValidatorVO.getCardDetailsVO().getCardNum().equals(""))
            {
                cardNum = commonValidatorVO.getCardDetailsVO().getCardNum();
            }
            p.setString(28, Functions.getCardType(cardNum));
            // p.setString(28,Functions.getCardType(cardType));
            p.setString(29, commonValidatorVO.getAddressDetailsVO().getEmail());
            p.setString(30, commonValidatorVO.getTerminalId());
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getIp()))
                p.setString(31,functions.getIPCountryShort(commonValidatorVO.getAddressDetailsVO().getIp()));
            else
                p.setString(31,"");

            p.setString(32,commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()))
                p.setString(33,functions.getIPCountryShort(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()));
            else
                p.setString(33,"");
            int num = p.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {
                        trackingId = rs.getInt(1);
                        commonValidatorVO.setTrackingid(String.valueOf(trackingId));
                    }
                }
            }
            paymentDao.updateBinDetails(commonValidatorVO);
            /*if ("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getIsCvvStore()))
            {
                insertCvv(commonValidatorVO);
            }*/
            log.debug("Insert Query trans_common---" + p + "---generated Trackingid---" + trackingId);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "insertAuthStartedForCommon()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "insertAuthStartedForCommon()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return trackingId;
    }

    public void updateBinDetails(CommonValidatorVO commonValidatorVO)
    {
        Functions functions = new Functions();
        Connection con = null;
        try
        {
            PaymentManager paymentManager = new PaymentManager();
            String subCardType = "Consumer Card";
            if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {
                subCardType = paymentManager.getSubCardType(commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6));
            }
            con = Database.getConnection();
            String qry = "insert into bin_details (icicitransid, first_six, last_four, accountid,emailaddr,boiledname,isSuccessful,isSettled,isRefund,isChargeback,isFraud,isRollingReserveKept,isRollingReserveReleased,subcard_type,bin_brand, bin_sub_brand, bin_card_type, bin_card_category,bin_usage_type,bin_country_code_A3,bin_trans_type,bin_country_code_A2, country_name, issuing_bank) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = con.prepareStatement(qry);
            pstmt.setString(1, commonValidatorVO.getTrackingid());
            if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {
                pstmt.setString(2, commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6));
                pstmt.setString(3, commonValidatorVO.getCardDetailsVO().getCardNum().substring(commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4));
            }
            else
            {
                pstmt.setString(2, "");
                pstmt.setString(3, "");
            }
            pstmt.setString(4, commonValidatorVO.getMerchantDetailsVO().getAccountId());
            pstmt.setString(5, commonValidatorVO.getAddressDetailsVO().getEmail());
            pstmt.setString(6, commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname());
            pstmt.setString(7, "N");
            pstmt.setString(8, "N");
            pstmt.setString(9, "N");
            pstmt.setString(10, "N");
            pstmt.setString(11, "N");
            pstmt.setString(12, "N");
            pstmt.setString(13, "Y");
            pstmt.setString(14, commonValidatorVO.getCardDetailsVO().getBin_usage_type());//Sub Card type
            pstmt.setString(15, commonValidatorVO.getCardDetailsVO().getBin_brand());
            pstmt.setString(16, commonValidatorVO.getCardDetailsVO().getBin_card_category());//Bin sub brand
            pstmt.setString(17, commonValidatorVO.getCardDetailsVO().getBin_card_type());
            pstmt.setString(18, commonValidatorVO.getCardDetailsVO().getBin_card_category());
            pstmt.setString(19, commonValidatorVO.getCardDetailsVO().getBin_usage_type());
            pstmt.setString(20, commonValidatorVO.getCardDetailsVO().getCountry_code_A3());
            pstmt.setString(21, commonValidatorVO.getCardDetailsVO().getTrans_type());
            pstmt.setString(22, commonValidatorVO.getCardDetailsVO().getCountry_code_A2());
            pstmt.setString(23, commonValidatorVO.getCardDetailsVO().getCountryName());
            pstmt.setString(24, commonValidatorVO.getCardDetailsVO().getIssuingBank());

            pstmt.executeUpdate();

        }
        catch (SQLException systemError)
        {
            log.error("Sql Exception :::::", systemError);

            //ToDO raise and handle DB Exception

        }
        catch (SystemError systemError)
        {
            log.error("System Error ", systemError);
            //ToDO raise and handle DB Exception
        }
        finally
        {
            Database.closeConnection(con);
        }
    }

    public void updateBinDetailsForRecurring(CommonValidatorVO commonValidatorVO)
    {
        Functions functions = new Functions();
        Connection con = null;
        try
        {
            PaymentManager paymentManager = new PaymentManager();
            String subCardType = "Consumer Card";
            if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {
                subCardType = paymentManager.getSubCardType(commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6));
            }
            con = Database.getConnection();
            String qry = "insert into bin_details (icicitransid, first_six, last_four, accountid,emailaddr,boiledname,isSuccessful,isSettled,isRefund,isChargeback,isFraud,isRollingReserveKept,isRollingReserveReleased,subcard_type,bin_brand, bin_sub_brand, bin_card_type, bin_card_category,bin_usage_type,bin_country_code_A3,bin_trans_type,bin_country_code_A2, country_name, issuing_bank) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = con.prepareStatement(qry);
            pstmt.setString(1, commonValidatorVO.getTrackingid());
            if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {
                String cardNum = PzEncryptor.decryptPAN(commonValidatorVO.getCardDetailsVO().getCardNum());
                pstmt.setString(2, cardNum.substring(0, 6));
                pstmt.setString(3, cardNum.substring(cardNum.length() - 4));

            }
            else
            {
                pstmt.setString(2, "");
                pstmt.setString(3, "");
            }
            pstmt.setString(4, commonValidatorVO.getMerchantDetailsVO().getAccountId());
            pstmt.setString(5, commonValidatorVO.getAddressDetailsVO().getEmail());
            pstmt.setString(6, commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname());
            pstmt.setString(7, "N");
            pstmt.setString(8, "N");
            pstmt.setString(9, "N");
            pstmt.setString(10, "N");
            pstmt.setString(11, "N");
            pstmt.setString(12, "N");
            pstmt.setString(13, "Y");
            pstmt.setString(14, commonValidatorVO.getCardDetailsVO().getBin_usage_type());//Sub Card type
            pstmt.setString(15, commonValidatorVO.getCardDetailsVO().getBin_brand());
            pstmt.setString(16, commonValidatorVO.getCardDetailsVO().getBin_card_category());//Bin sub brand
            pstmt.setString(17, commonValidatorVO.getCardDetailsVO().getBin_card_type());
            pstmt.setString(18, commonValidatorVO.getCardDetailsVO().getBin_card_category());
            pstmt.setString(19, commonValidatorVO.getCardDetailsVO().getBin_usage_type());
            pstmt.setString(20, commonValidatorVO.getCardDetailsVO().getCountry_code_A3());
            pstmt.setString(21, commonValidatorVO.getCardDetailsVO().getTrans_type());
            pstmt.setString(22, commonValidatorVO.getCardDetailsVO().getCountry_code_A2());
            pstmt.setString(23, commonValidatorVO.getCardDetailsVO().getCountryName());
            pstmt.setString(24, commonValidatorVO.getCardDetailsVO().getIssuingBank());

            pstmt.executeUpdate();

        }
        catch (SQLException systemError)
        {
            log.error("Sql Exception :::::", systemError);

            //ToDO raise and handle DB Exception

        }
        catch (SystemError systemError)
        {
            log.error("System Error ", systemError);
            //ToDO raise and handle DB Exception
        }
        finally
        {
            Database.closeConnection(con);
        }
    }

    public void updateTransactionStatusAfterResponse(String trackingId, String status, String captureAmount, String remark, String paymentId,String transaction_mode)
    {
        transactionLogger.debug("payoutStatus-----" + status);

        Connection con = null;
        try
        {
            con = Database.getConnection();
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            StringBuffer sb = new StringBuffer();

            sb.append("update transaction_common set");
            if ("capturesuccess".equalsIgnoreCase(status))
            {
                sb.append(" status= '" + status + "', captureAmount= '" + captureAmount + "', remark= '" + ESAPI.encoder().encodeForSQL(me,remark) + "'" + ", successtimestamp = '" + functions.getTimestamp() + "'");
            }
            else if ("authsuccessful".equalsIgnoreCase(status))
            {
                sb.append(" status = 'authsuccessful',remark = '" + ESAPI.encoder().encodeForSQL(me,remark) + "'" + ", successtimestamp = '" + functions.getTimestamp() + "'");
            }
            else if ("pending".equalsIgnoreCase(status))
            {
                sb.append(" remark = '" + ESAPI.encoder().encodeForSQL(me,remark) + "'");
            }
            else if ("payoutsuccessful".equalsIgnoreCase(status))
            {
                sb.append(" status = 'payoutsuccessful',payoutamount='" + captureAmount + "' ,remark = '" + ESAPI.encoder().encodeForSQL(me,remark) + "'" + ", payouttimestamp = '" + functions.getTimestamp() + "'");
            }
            else if ("payoutfailed".equalsIgnoreCase(status))
            {
                sb.append(" status = 'payoutfailed',remark = '" + ESAPI.encoder().encodeForSQL(me,remark) + "'");

            }
            else if ("payoutcancelfailed".equalsIgnoreCase(status))
            {

                sb.append(" status = 'payoutcancelfailed',remark = '" + ESAPI.encoder().encodeForSQL(me,remark) + "'");

            }
            else if ("payoutcancelsuccessful".equalsIgnoreCase(status))
            {

                sb.append(" status = 'payoutcancelsuccessful',remark = '" + ESAPI.encoder().encodeForSQL(me,remark) + "'");
            }
            else
            {
                sb.append(" status = 'authfailed',remark = '" + ESAPI.encoder().encodeForSQL(me,remark) + "'" + ", failuretimestamp = '" + functions.getTimestamp() + "'");
            }
            sb.append(",paymentid= '" + paymentId + "',transaction_mode=? where trackingid=?");

            PreparedStatement pstmt = con.prepareStatement(sb.toString());
            pstmt.setString(1, transaction_mode);
            pstmt.setString(2, trackingId);

            int i = pstmt.executeUpdate();

            log.debug("update success query---" + pstmt.toString() + "---" + i);

        }
        catch (SystemError systemError)
        {
            log.error("SystemError :::::", systemError);
        }
        catch (SQLException systemError)
        {
            log.error("Sql Exception :::::", systemError);

        }
        catch (Exception e)
        {
            transactionLogger.error(" paymentDAO Exception :::::", e);

        }
        finally
        {
            Database.closeConnection(con);
        }
    }

    public CommonValidatorVO getTransDetailsForCommon(String trackingId)
    {
        Connection connection = null;
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = new GenericCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        RecurringBillingVO recurringBillingVO = new RecurringBillingVO();

        try
        {
            connection = Database.getConnection();
            String query = "SELECT tc.*,rts.rbid FROM `transaction_common` AS tc,`recurring_transaction_subscription` AS rts WHERE tc.`trackingid`=? AND tc.trackingid=rts.`originatingTrackingid`";
            PreparedStatement p = connection.prepareStatement(query);
            p.setString(1, trackingId);
            ResultSet rs = p.executeQuery();
            if (rs.next())
            {
                merchantDetailsVO.setMemberId(rs.getString("toid"));
                //commonValidatorVO.getMerchantDetailsVO().setMemberId(rs.getString("toid"));
                genericTransDetailsVO.setTotype(rs.getString("totype"));
                genericTransDetailsVO.setFromid(rs.getString("fromid"));
                genericTransDetailsVO.setFromtype(rs.getString("fromtype"));
                genericTransDetailsVO.setAmount(rs.getString("amount"));
                genericTransDetailsVO.setCurrency(rs.getString("currency"));
                genericTransDetailsVO.setRedirectUrl(rs.getString("redirecturl"));
                genericAddressDetailsVO.setFirstname(rs.getString("firstname"));
                genericAddressDetailsVO.setLastname(rs.getString("lastname"));
                genericCardDetailsVO.setCardHolderName(rs.getString("name"));
                genericCardDetailsVO.setExpMonth(rs.getString("expdate"));
                genericCardDetailsVO.setCardNum(rs.getString("ccnum"));
                genericAddressDetailsVO.setEmail(rs.getString("emailaddr"));

                genericAddressDetailsVO.setIp(rs.getString("ipaddress"));
                genericAddressDetailsVO.setCountry(rs.getString("country"));
                genericAddressDetailsVO.setCity(rs.getString("city"));
                genericAddressDetailsVO.setState(rs.getString("state"));
                //genericAddressDetailsVO.setBirthdate(rs.getString("birthdate"));
                genericAddressDetailsVO.setZipCode(rs.getString("zip"));
                genericAddressDetailsVO.setTelnocc(rs.getString("telnocc"));
                genericAddressDetailsVO.setPhone(rs.getString("telno"));
                genericAddressDetailsVO.setStreet(rs.getString("street"));
                genericCardDetailsVO.setCardType(rs.getString("cardtype"));
                merchantDetailsVO.setAccountId(rs.getString("accountid"));
                commonValidatorVO.setPaymentType(rs.getString("paymodeid"));
                commonValidatorVO.setCardType(rs.getString("cardtypeid"));
                merchantDetailsVO.setCurrency(rs.getString("currency"));
                // genericAddressDetailsVO.setLanguage(rs.getString("language"));

                recurringBillingVO.setRbid(rs.getString("rbid"));

                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setCardDetailsVO(genericCardDetailsVO);
                commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setRecurringBillingVO(recurringBillingVO);
            }
        }
        catch (SystemError systemError)
        {
            log.error("System error", systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return commonValidatorVO;
    }

    public void updateAuthstartedTransactionForPayMitco(CommonValidatorVO commonValidatorVO, String trackingid) throws PZDBViolationException
    {
        Connection connection = null;
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        PreparedStatement preparedStatement = null;
        try
        {
            connection = Database.getConnection();
            String updateRecord = "UPDATE transaction_common SET firstname=?,lastname=?,name=?,street=?,country =?,city =?,state =?,zip =?,telno =?,telnocc =?,cardtype=?,emailaddr=?,STATUS='authstarted',ipaddress=?,fromtype=?,fromid=?,paymodeid=?,cardtypeid=?,terminalid=?,accountid=? WHERE trackingid=?";
            preparedStatement = connection.prepareStatement(updateRecord);
            preparedStatement.setString(1, commonValidatorVO.getAddressDetailsVO().getFirstname());
            preparedStatement.setString(2, commonValidatorVO.getAddressDetailsVO().getLastname());
            preparedStatement.setString(3, genericAddressDetailsVO.getFirstname() + " " + genericAddressDetailsVO.getLastname());
            preparedStatement.setString(4, genericAddressDetailsVO.getStreet());
            preparedStatement.setString(5, genericAddressDetailsVO.getCountry());
            preparedStatement.setString(6, genericAddressDetailsVO.getCity());
            preparedStatement.setString(7, genericAddressDetailsVO.getState());
            preparedStatement.setString(8, genericAddressDetailsVO.getZipCode());
            preparedStatement.setString(9, genericAddressDetailsVO.getPhone());
            preparedStatement.setString(10, genericAddressDetailsVO.getTelnocc());
            preparedStatement.setString(11, GatewayAccountService.getCardType(commonValidatorVO.getCardType()));
            preparedStatement.setString(12, genericAddressDetailsVO.getEmail());
            preparedStatement.setString(13, genericAddressDetailsVO.getIp());
            preparedStatement.setString(14, commonValidatorVO.getTransDetailsVO().getFromtype());
            preparedStatement.setString(15, commonValidatorVO.getTransDetailsVO().getFromid());
            preparedStatement.setString(16, commonValidatorVO.getPaymentType());
            preparedStatement.setString(17, commonValidatorVO.getCardType());
            preparedStatement.setString(18, commonValidatorVO.getTerminalId());
            preparedStatement.setString(19, commonValidatorVO.getMerchantDetailsVO().getAccountId());
            preparedStatement.setString(20, trackingid);
            int i = preparedStatement.executeUpdate();

            String qry = "insert into bin_details (icicitransid, accountid) values(?,?)";
            PreparedStatement pstmt = connection.prepareStatement(qry);
            pstmt.setString(1, commonValidatorVO.getTrackingid());
            pstmt.setString(2, commonValidatorVO.getMerchantDetailsVO().getAccountId());
            pstmt.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updateAuthstartedTransactionForPayMitco()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updateAuthstartedTransactionForPayMitco()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    public void updateFailedSessionoutTransaction(CommonValidatorVO commonValidatorVO, String trackingid) throws PZDBViolationException
    {
        log.debug("-----inside updateAuthstartedTransactionforCommon----");

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try
        {
            connection = Database.getConnection();
            String updateRecord = "UPDATE transaction_common SET STATUS='failed',remark='Payment session expired' WHERE trackingid=?";
            preparedStatement = connection.prepareStatement(updateRecord);
            preparedStatement.setString(1, trackingid);
            int i = preparedStatement.executeUpdate();
        }
        catch (SystemError systemError)
        {
            log.error("System error", systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException", e);
        }
        finally
        {
            Database.closeConnection(connection);
            Database.closePreparedStatement(preparedStatement);
        }
    }

    public String insertPerfectMoneyDetails(String trackingId, String transactionStatus, String paymentId, String paymentAmount, String paymentUnit, String payeeAccount, String payerAccount, String paymentBatchNumber, String timestampGmt, String email, String bankId) throws PZDBViolationException
    {
        String queryResult = "";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        try
        {
            con = Database.getConnection();
            String query = "INSERT INTO transaction_perfectmoney_details(detailid,trackingid,status,payment_id,payment_amount,payment_unit,payee_account,payer_account,payment_batch_number,timestampgmt,customerEmail,customerBankId) VALUES (null,?,?,?,?,?,?,?,?,?,?,?)";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, trackingId);
            preparedStatement.setString(2, transactionStatus);
            preparedStatement.setString(3, paymentId);
            preparedStatement.setString(4, paymentAmount);
            preparedStatement.setString(5, paymentUnit);
            preparedStatement.setString(6, payeeAccount);
            preparedStatement.setString(7, payerAccount);
            preparedStatement.setString(8, paymentBatchNumber);
            preparedStatement.setString(9, timestampGmt);
            preparedStatement.setString(10, email);
            preparedStatement.setString(11, bankId);
            int num = preparedStatement.executeUpdate();
            if (num == 1)
            {
                queryResult = "success";
            }
            else
            {
                queryResult = "failed";
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "insertPerfectMoneyDetails()", null, "Common", "SystemError Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "insertPerfectMoneyDetails()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return queryResult;
    }


    public void addBinDetailsEntry(String trackingId, String accountId, String firstSix, String lastFour, String bin_Brand, String bin_Transaction_Type, String bin_Card_Type, String bin_Card_Category, String bin_Usage_Type) throws PZDBViolationException
    {
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String qry = "insert into bin_details (icicitransid, accountid,first_six,last_four,bin_brand,bin_trans_type,bin_card_type,bin_card_category,bin_usage_type)values(?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = connection.prepareStatement(qry);
            pstmt.setString(1, trackingId);
            pstmt.setString(2, accountId);
            pstmt.setString(3, firstSix);
            pstmt.setString(4, lastFour);
            pstmt.setString(5, bin_Brand);
            pstmt.setString(6, bin_Transaction_Type);
            pstmt.setString(7, bin_Card_Type);
            pstmt.setString(8, bin_Card_Category);
            pstmt.setString(9, bin_Usage_Type);
            pstmt.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "addBinDetailsEntry()", null, "common", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "addBinDetailsEntry()", null, "common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    public String insertJetonDetails(String trackingid, String status, String paymentid, String customerNumber) throws PZDBViolationException
    {
        String queryResult = "";
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String query = "INSERT INTO transaction_jeton_details(detailid,trackingid,status,paymentid,customer_number,dtstamp) VALUES (null,?,?,?,?,unix_timestamp(now()))";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, trackingid);
            preparedStatement.setString(2, status);
            preparedStatement.setString(3, paymentid);
            preparedStatement.setString(4, customerNumber);
            int num = preparedStatement.executeUpdate();
            if (num == 1)
            {
                queryResult = "success";
            }
            else
            {
                queryResult = "failed";
            }
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "insertJetonDetails()", null, "Common", "SystemError Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "insertJetonDetails()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return queryResult;

    }


    public String getStatusOfTransaction(String trackingId)
    {
        Connection connection = null;
        String status = "";
        try
        {

            connection = Database.getConnection();
            String query = "select status from transaction_common where trackingid = ?";
            PreparedStatement p = connection.prepareStatement(query);
            p.setString(1, trackingId);
            ResultSet rs = p.executeQuery();
            if (rs.next())
            {
                status = rs.getString("status");
            }
        }
        catch (SQLException e)
        {
            log.error("SQLException---", e);
        }
        catch (SystemError e)
        {
            log.error("SystemError---", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return status;
    }

    public String getTerminalFromTrackingid(String trackingId)
    {
        Connection connection = null;
        String terminalid = "";
        try
        {

            connection = Database.getConnection();
            String query = "select terminalid from transaction_common where trackingid = ?";
            PreparedStatement p = connection.prepareStatement(query);
            p.setString(1, trackingId);
            ResultSet rs = p.executeQuery();
            if (rs.next())
            {
                terminalid = rs.getString("terminalid");
            }
            log.debug("query=-====" + p);
        }
        catch (SQLException e)
        {
            log.error("SQLException---", e);
        }
        catch (SystemError e)
        {
            log.error("SystemError---", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return terminalid;
    }

    public boolean isTransactionExist(String trackingId)
    {
        Connection conn = null;
        boolean transactionExist = false;
        try
        {
            conn = Database.getConnection();
            String query = "SELECT trackingid FROM transaction_vouchermoney_details WHERE trackingid = ?";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1, trackingId);
            ResultSet rs = p.executeQuery();
            if (rs.next())
            {
                transactionExist = true;
            }
            log.debug("query=-====" + p);
        }
        catch (SystemError e)
        {
            log.error("SystemError---", e);
        }
        catch (SQLException e)
        {
            log.error("SQLException---", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transactionExist;
    }

    public void updateSubscriptionAfterBankCall(String rbId, String first_six, String last_four, String trackingId, String bankTransactionId) throws PZDBViolationException
    {
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String query = "update recurring_transaction_subscription set first_six=?,last_four=?,rbid=?,original_banktransaction_id=? where originatingTrackingid=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, first_six);
            preparedStatement.setString(2, last_four);
            preparedStatement.setString(3, rbId);
            preparedStatement.setString(4, bankTransactionId);
            preparedStatement.setString(5, trackingId);
            preparedStatement.executeUpdate();
        }
        catch (SystemError e)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java", "updateSubscriptionAfterBankCall()", null, "Common", "Error in connection:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java", "updateSubscriptionAfterBankCall()", null, "Common", "Error in Query:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    public synchronized int mark3DTransaction(String trackingId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        try
        {
            conn = Database.getConnection();
            StringBuilder sb = new StringBuilder();
            sb.append("update transaction_common set status='authstarted_3D' WHERE trackingid =? and status='authstarted'");
            pstmt = conn.prepareStatement(sb.toString());
            pstmt.setString(1, trackingId);
            result = pstmt.executeUpdate();
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "markTheTransactionAs3D()", null, "Common", "SystemError Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "markTheTransactionAs3D()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return result;
    }

    /*public void insertCvv(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = Database.getConnection();
            String query = "insert into token_cvv (cvv,trackingid) values(?,?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, PzEncryptor.encryptCVV(commonValidatorVO.getCardDetailsVO().getcVV()));
            pstmt.setString(2, commonValidatorVO.getTrackingid());
            int num = pstmt.executeUpdate();
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "markTheTransactionAs3D()", null, "Common", "SystemError Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "markTheTransactionAs3D()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
    }*/

    /*public String getCvv(CommonValidatorVO commonValidatorVO)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String cvv = "";
        try
        {
            conn = Database.getConnection();
            String query = "select cvv from token_cvv where trackingid=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, commonValidatorVO.getTrackingid());
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                cvv = rs.getString("cvv");
            }

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError::::::", systemError);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException::::::", e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return cvv;
    }*/


    public void updateAuthSuccessfulForWallet(CommonValidatorVO commonValidatorVO, String trackingid) throws PZDBViolationException
    {
        log.debug("-----inside updateAuthSuccessfulForWallet----");
        if (commonValidatorVO != null)
        {
            Connection connection = null;
            PreparedStatement preparedStatement = null;

            try
            {
                connection = Database.getConnection();
                String updateRecord = "UPDATE transaction_common SET STATUS='authsuccessful',paymodeid=?,cardtypeid=?,terminalid=?,amount=?,currency=? WHERE trackingid=?";
                preparedStatement = connection.prepareStatement(updateRecord);

                if (functions.isValueNull(commonValidatorVO.getPaymentType())) {
                    preparedStatement.setString(1, commonValidatorVO.getPaymentType());
                }
                else {
                    preparedStatement.setString(1, "");
                }

                if (functions.isValueNull(commonValidatorVO.getCardType())) {
                    preparedStatement.setString(2, commonValidatorVO.getCardType());
                }
                else {
                    preparedStatement.setString(2, "");
                }

                if (functions.isValueNull(commonValidatorVO.getTerminalId())) {
                    preparedStatement.setString(3, commonValidatorVO.getTerminalId());
                }
                else {
                    preparedStatement.setInt(3, 0);
                }

                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getAmount())) {
                    preparedStatement.setString(4, commonValidatorVO.getTransDetailsVO().getAmount());
                }
                else {
                    preparedStatement.setInt(4, 0);
                }

                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency())) {
                    preparedStatement.setString(5, commonValidatorVO.getTransDetailsVO().getCurrency());
                }
                else {
                    preparedStatement.setInt(5, 0);
                }

                preparedStatement.setString(6, trackingid);
                int i = preparedStatement.executeUpdate();

            }
            catch (SystemError se)
            {
                PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updateAuthstartedTransactionforP4()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
            }
            catch (SQLException e)
            {
                PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updateAuthstartedTransactionforP4()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
            }
            finally
            {
                Database.closeConnection(connection);
                Database.closePreparedStatement(preparedStatement);
            }
        }
    }
    public CommonValidatorVO getTerminalBasedOnMarketPlaceSubmerchant(CommonValidatorVO commonValidatorVO,String terminalid)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        String cvv="";
        MarketPlaceVO marketPlaceVO=commonValidatorVO.getMarketPlaceVO();
        try
        {
            conn = Database.getConnection();
            String query = "SELECT mam.accountid,mam.terminalid FROM member_account_mapping mam,gateway_accounts ga,gateway_type gt WHERE mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND mam.memberid=? AND mam.accountid=? AND mam.paymodeid=? AND mam.cardtypeid=? AND gt.currency=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, marketPlaceVO.getMemberid());
            pstmt.setString(2, commonValidatorVO.getMerchantDetailsVO().getAccountId());
            pstmt.setString(3, commonValidatorVO.getPaymentType());
            pstmt.setString(4, commonValidatorVO.getCardType());
            pstmt.setString(5, commonValidatorVO.getTransDetailsVO().getCurrency());
            rs= pstmt.executeQuery();
            if (rs.next())
            {
                commonValidatorVO.getMerchantDetailsVO().setAccountId(rs.getString("accountid"));
                commonValidatorVO.setTerminalId(rs.getString("terminalid"));
            }
            else
            {
                commonValidatorVO.setTerminalId(terminalid);
            }

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError::::::", systemError);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException::::::", e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return commonValidatorVO;
    }

    public MarketPlaceVO getMarketPlaceDetailsByTrackingid(String trackingid)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        MarketPlaceVO marketPlaceVO=new MarketPlaceVO();
        try
        {
            conn = Database.getConnection();
            String query = "SELECT toid,amount,description,orderdescription FROM transaction_common WHERE trackingid=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, trackingid);
            transactionLogger.error("getMarketPlaceDetailsByTrackingid------>"+pstmt);
            rs= pstmt.executeQuery();
            if (rs.next())
            {
                marketPlaceVO.setMemberid(rs.getString("toid"));
                marketPlaceVO.setAmount(rs.getString("amount"));
                marketPlaceVO.setOrderid(rs.getString("description"));
                marketPlaceVO.setOrderDesc(rs.getString("orderdescription"));
                marketPlaceVO.setTrackingid(trackingid);
            }

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError::::::", systemError);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException::::::", e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return marketPlaceVO;
    }

    public int insertCaptureStartedTransactionforStaticQR(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        Connection connection = null;
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        TerminalVO terminalVO = commonValidatorVO.getTerminalVO();
        int trackingId = 0;

        PreparedStatement preparedStatement = null;
        try
        {
            connection = Database.getConnection();
            String updateRecord = "INSERT INTO transaction_common (toid,totype,fromid,fromtype,description,orderdescription,amount,redirecturl,STATUS,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,ipaddress,terminalid,street,country,city,state,zip,telno,telnocc,emailaddr,templateamount,templatecurrency,walletAmount,walletCurrency,notificationUrl,customerId,firstname,lastname,name) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,UNIX_TIMESTAMP(NOW()),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            preparedStatement = connection.prepareStatement(updateRecord);
            preparedStatement.setString(1, merchantDetailsVO.getMemberId());
            preparedStatement.setString(2, genericTransDetailsVO.getTotype());
            preparedStatement.setString(3, genericTransDetailsVO.getFromid());
            preparedStatement.setString(4, genericTransDetailsVO.getFromtype());
            preparedStatement.setString(5, genericTransDetailsVO.getOrderId());
            preparedStatement.setString(6, genericTransDetailsVO.getOrderDesc());
            preparedStatement.setString(7, genericTransDetailsVO.getAmount());
            preparedStatement.setString(8, genericTransDetailsVO.getRedirectUrl());
            preparedStatement.setString(9, PZTransactionStatus.CAPTURE_STARTED.toString());
            preparedStatement.setString(10, merchantDetailsVO.getAccountId());
            preparedStatement.setString(11, terminalVO.getPaymodeId());
            preparedStatement.setString(12, terminalVO.getCardTypeId());
            preparedStatement.setString(13, genericTransDetailsVO.getCurrency());
            preparedStatement.setString(14, genericTransDetailsVO.getHeader());
            preparedStatement.setString(15, genericAddressDetailsVO.getIp());
            preparedStatement.setString(16, terminalVO.getTerminalId());
            preparedStatement.setString(17, genericAddressDetailsVO.getStreet());
            preparedStatement.setString(18, genericAddressDetailsVO.getCountry());
            preparedStatement.setString(19, genericAddressDetailsVO.getCity());
            preparedStatement.setString(20, genericAddressDetailsVO.getState());
            preparedStatement.setString(21, genericAddressDetailsVO.getZipCode());
            preparedStatement.setString(22, genericAddressDetailsVO.getPhone());
            preparedStatement.setString(23, genericAddressDetailsVO.getTelnocc());
            preparedStatement.setString(24, genericAddressDetailsVO.getEmail());
            preparedStatement.setString(25, genericAddressDetailsVO.getTmpl_amount());
            preparedStatement.setString(26, genericAddressDetailsVO.getTmpl_currency());
            preparedStatement.setString(27, genericTransDetailsVO.getWalletAmount());
            preparedStatement.setString(28, genericTransDetailsVO.getWalletCurrency());
            preparedStatement.setString(29, genericTransDetailsVO.getNotificationUrl());
            preparedStatement.setString(30, commonValidatorVO.getCustomerId());
            preparedStatement.setString(31, genericAddressDetailsVO.getFirstname());
            preparedStatement.setString(32, genericAddressDetailsVO.getLastname());
            if (functions.isValueNull(genericAddressDetailsVO.getFirstname()) && functions.isValueNull(genericAddressDetailsVO.getLastname())) {
                preparedStatement.setString(31, genericAddressDetailsVO.getFirstname() + " " + genericAddressDetailsVO.getLastname());
            }
            else {
                preparedStatement.setString(31, "");
            }

            int i = preparedStatement.executeUpdate();

            if (i == 1)
            {
                ResultSet rs = preparedStatement.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {
                        trackingId = rs.getInt(1);
                    }
                }
            }

            transactionLogger.error("in insertAuthstartedTransactionforStaticQR insert query ------"+preparedStatement);

            commonValidatorVO.setTrackingid(String.valueOf(trackingId));
            String qry = "insert into bin_details (icicitransid, accountid) values(?,?)";
            PreparedStatement pstmt = connection.prepareStatement(qry);
            pstmt.setString(1, commonValidatorVO.getTrackingid());
            pstmt.setString(2, commonValidatorVO.getMerchantDetailsVO().getAccountId());
            pstmt.executeUpdate();
            transactionLogger.error("in insertAuthstartedTransactionforStaticQR query ------"+pstmt);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "insertAuthstartedTransactionforStaticQR()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "insertAuthstartedTransactionforStaticQR()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return trackingId;
    }

    public void updateAuthstartedTransactionforDynamicQR(CommonValidatorVO commonValidatorVO, String trackingid) throws PZDBViolationException
    {
        log.debug("-----inside updateAuthstartedTransactionforQRCommon----");
        if (commonValidatorVO != null)
        {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            GenericAddressDetailsVO genericAddressDetailsVO = null;
            GenericCardDetailsVO genericCardDetailsVO = null;
            GenericTransDetailsVO genericTransDetailsVO = null;
            MerchantDetailsVO merchantDetailsVO = null;

            if (commonValidatorVO.getAddressDetailsVO() != null) {
                genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
            }
            else {
                genericAddressDetailsVO = new GenericAddressDetailsVO();
            }

            if (commonValidatorVO.getCardDetailsVO() != null) {
                genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
            }
            else {
                genericCardDetailsVO = new GenericCardDetailsVO();
            }

            if (commonValidatorVO.getTransDetailsVO() != null) {
                genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
            }
            else {
                genericTransDetailsVO = new GenericTransDetailsVO();
            }

            if (commonValidatorVO.getMerchantDetailsVO() != null) {
                merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
            }
            else {
                merchantDetailsVO = new MerchantDetailsVO();
            }

            try
            {
                connection = Database.getConnection();
                String updateRecord = "UPDATE transaction_common SET firstname=?,lastname=?,name=?,street=?,country =?,city =?,state =?,zip =?,telno =?,telnocc =?,emailaddr=?,cardtype=?,STATUS='authstarted',ipaddress=?,fromtype=?,fromid=?,paymodeid=?,cardtypeid=?,terminalid=?,accountid=?,amount=?,currency=?,templateamount=?,templatecurrency=?,walletAmount=?,walletCurrency=? WHERE trackingid=?";
                preparedStatement = connection.prepareStatement(updateRecord);

                if (functions.isValueNull(genericAddressDetailsVO.getFirstname())) {
                    preparedStatement.setString(1, genericAddressDetailsVO.getFirstname());
                }
                else {
                    preparedStatement.setString(1, "");
                }

                if (functions.isValueNull(genericAddressDetailsVO.getLastname())) {
                    preparedStatement.setString(2, genericAddressDetailsVO.getLastname());
                }
                else {
                    preparedStatement.setString(2, "");
                }

                if (functions.isValueNull(genericAddressDetailsVO.getFirstname()) && functions.isValueNull(genericAddressDetailsVO.getLastname())) {
                    preparedStatement.setString(3, genericAddressDetailsVO.getFirstname() + " " + genericAddressDetailsVO.getLastname());
                }
                else {
                    preparedStatement.setString(3, "");
                }

                if (functions.isValueNull(genericAddressDetailsVO.getStreet())) {
                    preparedStatement.setString(4, genericAddressDetailsVO.getStreet());
                }
                else {
                    preparedStatement.setString(4, "");
                }

                if (functions.isValueNull(genericAddressDetailsVO.getCountry())) {
                    preparedStatement.setString(5, genericAddressDetailsVO.getCountry());
                }
                else {
                    preparedStatement.setString(5, "");
                }

                if (functions.isValueNull(genericAddressDetailsVO.getCity())) {
                    preparedStatement.setString(6, genericAddressDetailsVO.getCity());
                }
                else {
                    preparedStatement.setString(6, "");
                }

                if (functions.isValueNull(genericAddressDetailsVO.getState())) {
                    preparedStatement.setString(7, genericAddressDetailsVO.getState());
                }
                else {
                    preparedStatement.setString(7, "");
                }

                if (functions.isValueNull(genericAddressDetailsVO.getZipCode())) {
                    preparedStatement.setString(8, genericAddressDetailsVO.getZipCode());
                }
                else {
                    preparedStatement.setString(8, "");
                }

                if (functions.isValueNull(genericAddressDetailsVO.getPhone())) {
                    preparedStatement.setString(9, genericAddressDetailsVO.getPhone());
                }
                else {
                    preparedStatement.setString(9, "");
                }

                if (functions.isValueNull(genericAddressDetailsVO.getTelnocc())) {
                    preparedStatement.setString(10, genericAddressDetailsVO.getTelnocc());
                }
                else {
                    preparedStatement.setString(10, "");
                }

                if (functions.isValueNull(genericAddressDetailsVO.getEmail())) {
                    preparedStatement.setString(11, genericAddressDetailsVO.getEmail());
                }
                else {
                    preparedStatement.setString(11, "");
                }

                if (functions.isValueNull(genericCardDetailsVO.getCardNum())) {
                    preparedStatement.setString(12, Functions.getCardType(genericCardDetailsVO.getCardNum()));
                }
                else {
                    preparedStatement.setString(12, "");
                }

                if (functions.isValueNull(genericAddressDetailsVO.getIp())) {
                    preparedStatement.setString(13, genericAddressDetailsVO.getIp());
                }
                else {
                    preparedStatement.setString(13, "");
                }

                if (functions.isValueNull(genericTransDetailsVO.getFromtype())) {
                    preparedStatement.setString(14, genericTransDetailsVO.getFromtype());
                }
                else {
                    preparedStatement.setString(14, "");
                }

                if (functions.isValueNull(genericTransDetailsVO.getFromid())) {
                    preparedStatement.setString(15, genericTransDetailsVO.getFromid());
                }
                else {
                    preparedStatement.setString(15, "");
                }

                if (functions.isValueNull(commonValidatorVO.getPaymentType())) {
                    preparedStatement.setString(16, commonValidatorVO.getPaymentType());
                }
                else {
                    preparedStatement.setString(16, "");
                }

                if (functions.isValueNull(commonValidatorVO.getCardType())) {
                    preparedStatement.setString(17, commonValidatorVO.getCardType());
                }
                else {
                    preparedStatement.setString(17, "");
                }

                if (functions.isValueNull(commonValidatorVO.getTerminalId())) {
                    preparedStatement.setString(18, commonValidatorVO.getTerminalId());
                }
                else {
                    preparedStatement.setInt(18, 0);
                }

                if (functions.isValueNull(merchantDetailsVO.getAccountId())) {
                    preparedStatement.setString(19, merchantDetailsVO.getAccountId());
                }
                else {
                    preparedStatement.setString(19, "");
                }

                if (functions.isValueNull(genericTransDetailsVO.getAmount())) {
                    preparedStatement.setString(20, genericTransDetailsVO.getAmount());
                }
                else {
                    preparedStatement.setString(20, "");
                }

                if (functions.isValueNull(genericTransDetailsVO.getCurrency())) {
                    preparedStatement.setString(21, genericTransDetailsVO.getCurrency());
                }
                else {
                    preparedStatement.setString(21, "");
                }

                if (functions.isValueNull(genericAddressDetailsVO.getTmpl_amount())) {
                    preparedStatement.setString(22, genericAddressDetailsVO.getTmpl_amount());
                }
                else {
                    preparedStatement.setString(22, "");
                }

                if (functions.isValueNull(genericAddressDetailsVO.getTmpl_currency())) {
                    preparedStatement.setString(23, genericAddressDetailsVO.getTmpl_currency());
                }
                else {
                    preparedStatement.setString(23, "");
                }

                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getWalletAmount())) {
                    preparedStatement.setString(24, commonValidatorVO.getTransDetailsVO().getWalletAmount());
                }
                else {
                    preparedStatement.setString(24, "");
                }

                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getWalletCurrency())) {
                    preparedStatement.setString(25, commonValidatorVO.getTransDetailsVO().getWalletCurrency());
                }
                else {
                    preparedStatement.setString(25, "");
                }
                preparedStatement.setString(26, trackingid);
                int i = preparedStatement.executeUpdate();

                commonValidatorVO.setTrackingid(String.valueOf(trackingid));

                transactionLogger.error("updateAuthstartedTransactionforQRCommon ---update query ---" + preparedStatement);

                String checkQuery = "select icicitransid from  bin_details where icicitransid=?";
                PreparedStatement pstmt = connection.prepareStatement(checkQuery);
                pstmt.setString(1, commonValidatorVO.getTrackingid());
                ResultSet rs=pstmt.executeQuery();
                boolean isTrackingIdAvailable=false;
                while (rs.next())
                {
                    isTrackingIdAvailable = true;
                }

                transactionLogger.error("updateAuthstartedTransactionforQRCommon ---check query ---" + pstmt);
                transactionLogger.error("updateAuthstartedTransactionforQRCommon --- isTrackingIdAvailable ---" + isTrackingIdAvailable);

                if(!isTrackingIdAvailable)
                {
                    String qry = "insert into bin_details (icicitransid, accountid) values(?,?)";
                    pstmt = connection.prepareStatement(qry);
                    pstmt.setString(1, commonValidatorVO.getTrackingid());
                    pstmt.setString(2, commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    pstmt.executeUpdate();

                    transactionLogger.error("updateAuthstartedTransactionforQRCommon ---insert bin query ---" + pstmt);
                }
            }
            catch (SystemError se)
            {
                PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updateAuthstartedTransactionforQRCommon()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
            }
            catch (SQLException e)
            {
                PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updateAuthstartedTransactionforQRCommon()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
            }
            finally
            {
                Database.closeConnection(connection);
                Database.closePreparedStatement(preparedStatement);
            }
        }
    }


    public void updateStatusForQR(String transaction_status, String trackingid, String captureAmount, String walletAmount, String walletCurrency) throws PZDBViolationException
    {
        log.debug("-----inside updateStatusForQR----");
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String status = "";

        transactionLogger.error("status === "+transaction_status);
        transactionLogger.error("tracking id === "+trackingid);

        if (functions.isValueNull(transaction_status) && functions.isValueNull(trackingid))
        {
            status = transaction_status;

            try
            {
                connection = Database.getConnection();
                String updateRecord = "UPDATE transaction_common SET STATUS=?,captureamount=?,walletAmount=?,walletCurrency=? WHERE trackingid=?";
                preparedStatement = connection.prepareStatement(updateRecord);

                preparedStatement.setString(1, status);
                preparedStatement.setString(2, captureAmount);
                preparedStatement.setString(3, walletAmount);
                preparedStatement.setString(4, walletCurrency);
                preparedStatement.setString(5, trackingid);
                int i = preparedStatement.executeUpdate();

                transactionLogger.error("update query ==="+preparedStatement);
            }
            catch (SystemError se)
            {
                PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updateStatusForQR()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
            }
            catch (SQLException e)
            {
                PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updateStatusForQR()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
            }
            finally
            {
                Database.closeConnection(connection);
                Database.closePreparedStatement(preparedStatement);
            }
        }
    }

    public List<String> getCurrencyByMemberId(String memberId)
    {
        Connection con=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        List<String> currencyList=new ArrayList<>();
        try
        {
            con = Database.getConnection();
            String query="SELECT DISTINCT gt.currency FROM member_account_mapping AS m, gateway_accounts AS ga,gateway_type AS gt WHERE memberid =? AND ga.accountid=m.accountid AND ga.pgtypeid=gt.pgtypeid AND m.isactive='Y' AND gt.`currency`!='ALL';";
            ps=con.prepareStatement(query);
            ps.setString(1,memberId);
            rs=ps.executeQuery();
            while (rs.next())
            {
                currencyList.add(rs.getString("currency"));
            }
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError-->",systemError);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException-->", e);
        }finally
        {
            Database.closeConnection(con);
        }

        return currencyList;
    }

    public int insertInitAuthStartedEntry(CommonValidatorVO commonValidatorVO, String tableName) throws PZDBViolationException
    {
        int trackingId = 0;
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String query = "insert into " + tableName + " (toid,totype,fromid,fromtype,status,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,amount) values(?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?)";
            PreparedStatement p = con.prepareStatement(query);

            p.setString(1, commonValidatorVO.getMerchantDetailsVO().getMemberId());
            p.setString(2, commonValidatorVO.getTransDetailsVO().getTotype());
            p.setString(3, commonValidatorVO.getTransDetailsVO().getFromid());
            p.setString(4, commonValidatorVO.getTransDetailsVO().getFromtype());
            p.setString(5, ActionEntry.ACTION_3D2_INITIATED);
            p.setString(6, commonValidatorVO.getMerchantDetailsVO().getAccountId());
            p.setString(7, commonValidatorVO.getPaymentType());
            p.setString(8, commonValidatorVO.getCardType());
            p.setString(9, commonValidatorVO.getTransDetailsVO().getCurrency());
            p.setString(10, commonValidatorVO.getTransDetailsVO().getHeader());
            p.setString(11, commonValidatorVO.getTransDetailsVO().getAmount());

            int num = p.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {
                        trackingId = rs.getInt(1);
                        commonValidatorVO.setTrackingid(String.valueOf(trackingId));
                    }
                }
            }

            log.debug("Insert Query trans_common---" + p + "--- Trackingid---" + trackingId);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "insertAuthStartedForRecurring()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "insertAuthStartedForRecurring()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return trackingId;
    }
    public int insertBegunTransactionForCommonMarketPlace(CommonValidatorVO commonValidatorVO, String httphadder, String status,String toType) throws PZDBViolationException
    {
        int trackingId = 0;
        Connection con = null;
        PreparedStatement p = null;
        ResultSet rs = null;

        try
        {
            con = Database.getConnection();
            String query = "insert into transaction_common(toid,totype,fromid,fromtype,description,orderdescription,amount,redirecturl,status,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,ipaddress,terminalid,templateamount,templatecurrency,notificationUrl,customerId,version,parentTrackingid,emailaddr,merchantIpCountry,customerIP,customerIpCountry,firstname,lastname,name) values(?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            //String query="insert into transaction_common(toid,totype,fromid,fromtype,description,orderdescription,amount,redirecturl,status,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,ipaddress,terminalid,templateamount,templatecurrency,notificationUrl,customerId) values(?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?,?,?,?)";
            p = con.prepareStatement(query);
            p.setString(1, commonValidatorVO.getMerchantDetailsVO().getMemberId());
            p.setString(2, toType);
            p.setString(3, commonValidatorVO.getTransDetailsVO().getFromid());
            p.setString(4, commonValidatorVO.getTransDetailsVO().getFromtype());
            p.setString(5, commonValidatorVO.getTransDetailsVO().getOrderId());
            p.setString(6, commonValidatorVO.getTransDetailsVO().getOrderDesc());
            p.setString(7, commonValidatorVO.getTransDetailsVO().getAmount());
            p.setString(8, commonValidatorVO.getTransDetailsVO().getRedirectUrl());
            p.setString(9, status);
            p.setString(10, commonValidatorVO.getMerchantDetailsVO().getAccountId());

            if (functions.isValueNull(commonValidatorVO.getPaymentType()))
            {
                p.setInt(11, Integer.parseInt(commonValidatorVO.getPaymentType()));
            }
            else
            {
                p.setInt(11, 0);
            }
            if (functions.isValueNull(commonValidatorVO.getCardType()))
            {
                p.setInt(12, Integer.parseInt(commonValidatorVO.getCardType()));
            }
            else
            {
                p.setInt(12, 0);
            }
            if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency()))
            {
                p.setString(13, commonValidatorVO.getTransDetailsVO().getCurrency().toUpperCase());
            }
            else
            {
                p.setString(13, "");
            }
            p.setString(14, httphadder);
            p.setString(15, commonValidatorVO.getAddressDetailsVO().getIp());
            if (functions.isValueNull(commonValidatorVO.getTerminalId()))
            {
                p.setInt(16, Integer.parseInt(commonValidatorVO.getTerminalId()));
            }
            else
            {
                p.setInt(16, 0);
            }
            p.setString(17, commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency()))
            {
                p.setString(18, commonValidatorVO.getAddressDetailsVO().getTmpl_currency().toUpperCase());
            }
            else
            {
                p.setString(18, commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
            }
            p.setString(19, commonValidatorVO.getTransDetailsVO().getNotificationUrl());
            p.setString(20, commonValidatorVO.getCustomerId());
            if (functions.isValueNull(commonValidatorVO.getVersion()))
            {
                p.setString(21, commonValidatorVO.getVersion());
            }
            else
            {
                p.setString(21, "");
            }
            p.setString(22, commonValidatorVO.getTrackingid());

            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail()))
            {
                p.setString(23, commonValidatorVO.getAddressDetailsVO().getEmail());
            }
            else
            {
                p.setString(23, "");
            }
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getIp()))
                p.setString(24, functions.getIPCountryShort(commonValidatorVO.getAddressDetailsVO().getIp()));
            else
                p.setString(24, "");

            p.setString(25, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()))
                p.setString(26, functions.getIPCountryShort(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()));
            else
                p.setString(26, "");

            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname()))
            {
                p.setString(27,commonValidatorVO.getAddressDetailsVO().getFirstname());
            }
            else
            {
                p.setString(27,"");
            }
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname()))
            {
                p.setString(28,commonValidatorVO.getAddressDetailsVO().getLastname());
            }
            else
            {
                p.setString(28,"");
            }
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname())&&functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname()))
            {
                p.setString(29,commonValidatorVO.getAddressDetailsVO().getFirstname()+" "+(commonValidatorVO.getAddressDetailsVO().getLastname()));
            }
            else if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname())&&!functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname()))
            {
                p.setString(29,commonValidatorVO.getAddressDetailsVO().getFirstname()+" "+(commonValidatorVO.getAddressDetailsVO().getFirstname()));
            }
            else if (!functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname())&&functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname()))
            {
                p.setString(29,commonValidatorVO.getAddressDetailsVO().getLastname()+" "+(commonValidatorVO.getAddressDetailsVO().getLastname()));
            }

            else
            {
                p.setString(29, "");
            }

            transactionLogger.error("---- insertBegunTransactionForCommon query -----"+p);

            int num = p.executeUpdate();
            if (num == 1)
            {
                rs = p.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {
                        trackingId = rs.getInt(1);
                    }
                }
            }

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "insertBegunTransactionForCommon()", null, "common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException s)
        {
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "insertBegunTransactionForCommon()", null, "common", "SQL Exception thrown:::", PZDBExceptionEnum.INCORRECT_QUERY, null, s.getMessage(), s.getCause());
        }
        finally
        {
            Database.closeConnection(con);
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
        }
        return trackingId;
    }

    public void insertEcospendDetails(EcospendResponseVO ecospendResponseVO,String trackingid) throws PZDBViolationException
    {
        int trackingId = 0;
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String query = "insert into transaction_ecospend_detaills(trackingid,bank_id,description,reference,crtype,dbtype," +
                    "cridentification,dbidentification,crowner_name,dbowner_name,crcurrency,dbcurrency,crbic,dbbic,get_refund_info," +
                    "for_payout,scheduled_for,psu_id,payment_rails,first_payment_date,number_of_payments,period) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement p = con.prepareStatement(query);
            p.setString(1, trackingid);
            p.setString(2, ecospendResponseVO.getBank_id());
            p.setString(3, ecospendResponseVO.getDescription());
            p.setString(4, ecospendResponseVO.getReference());
            p.setString(5, ecospendResponseVO.getCreditortype());
            p.setString(6, ecospendResponseVO.getDebtortype());
            p.setString(7, ecospendResponseVO.getCreditorID());
            p.setString(8, ecospendResponseVO.getDebtorID());
            p.setString(9, ecospendResponseVO.getCreditorName());
            p.setString(10, ecospendResponseVO.getDebtorName());
            p.setString(11, ecospendResponseVO.getCreditorCurrency());
            p.setString(12, ecospendResponseVO.getDebtorCurrency());
            p.setString(13, ecospendResponseVO.getCreditorBic());
            p.setString(14, ecospendResponseVO.getDebtorBic());
            p.setString(15, ecospendResponseVO.getGetrefundinfo());
            p.setString(16, ecospendResponseVO.getFor_payout());
            p.setString(17, ecospendResponseVO.getScheduled_for());
            p.setString(18, ecospendResponseVO.getPsuid());
            p.setString(19, ecospendResponseVO.getPaymentrails());
            p.setString(20, ecospendResponseVO.getFirst_payment_date());
            p.setString(21, ecospendResponseVO.getNumber_of_payments());
            p.setString(22, ecospendResponseVO.getPeriod());

            p.executeUpdate();
            log.debug("Insert Query Ecospend details---" + query + "---for Trackingid---" + trackingId);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "insertEcospendDetails()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "insertEcospendDetails()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
    }

    public Hashtable getTransactionDetails(String trackingid)
    {
        Connection conn = null;
        PreparedStatement p1 = null;
        ResultSet rs = null;
        Hashtable details = new Hashtable();
        try
        {
            conn = Database.getConnection();
            String query = "SELECT bank_id,reference,crtype,cridentification,crowner_name,crcurrency,crbic from transaction_ecospend_detaills WHERE trackingid=?";
            p1 = conn.prepareStatement(query);
            p1.setString(1, trackingid);
            rs = p1.executeQuery();

            if (rs.next())
            {
                details.put("bank_id", rs.getString("bank_id"));
                details.put("reference", rs.getString("reference"));
                details.put("crtype", rs.getString("crtype"));
                details.put("cridentification", rs.getString("cridentification"));
                details.put("crowner_name", rs.getString("crowner_name"));
                details.put("crcurrency", rs.getString("crcurrency"));
                details.put("crbic", rs.getString("crbic"));
            }

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError::::::", systemError);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException::::::", e);
        }
        finally
        {
            Database.closePreparedStatement(p1);
            Database.closeConnection(conn);
        }
        return details;
    }

    public void updateAuthstartedTransactionforFlexepinVoucher(CommonValidatorVO commonValidatorVO, String trackingid) throws PZDBViolationException
    {
        log.error("-----inside updateAuthstartedTransactionforFlexepinVoucher----");
        if (commonValidatorVO != null)
        {
            Connection connection                           = null;
            PreparedStatement pstmt                         = null;
            GenericTransDetailsVO  genericTransDetailsVO    = null;
            GenericCardDetailsVO genericCardDetailsVO       = null;

            if (commonValidatorVO.getTransDetailsVO() != null)
                genericTransDetailsVO =  commonValidatorVO.getTransDetailsVO();
            else
                genericTransDetailsVO = new GenericTransDetailsVO();

            if (commonValidatorVO.getCardDetailsVO() != null)
                genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
            else
                genericCardDetailsVO = new GenericCardDetailsVO();

            try
            {
                connection          = Database.getConnection();
                String updateRecord = "insert into transaction_flexepin_details (voucher,status,timestamp,expiry,amount,currency,paymentid,ean,voucherStatus,serial,trackingid) values(?,?,?,?,?,?,?,?,?,?,?)";
                pstmt               = connection.prepareStatement(updateRecord);

                if (functions.isValueNull(genericCardDetailsVO.getVoucherNumber()))
                    pstmt.setString(1, genericCardDetailsVO.getVoucherNumber());
                else
                    pstmt.setString(1, "");

                pstmt.setString(2, "authstarted");
                pstmt.setString(3, functions.getTimestamp());
                pstmt.setString(4, "");

                if (functions.isValueNull(genericTransDetailsVO.getAmount()))
                    pstmt.setString(5, genericTransDetailsVO.getAmount());
                else
                    pstmt.setString(5, "0.00");

                pstmt.setString(6, "");
                pstmt.setString(7, "");
                pstmt.setString(8, "");
                pstmt.setString(9, "");
                pstmt.setString(10, "");
                pstmt.setString(11, trackingid);

                transactionLogger.error("update for flexepin details -->"+pstmt);
                pstmt.executeUpdate();
            }
            catch (SystemError se)
            {
                PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updateAuthstartedTransactionforFlexepinVoucher()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
            }
            catch (SQLException e)
            {
                PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "updateAuthstartedTransactionforFlexepinVoucher()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
            }
            finally
            {
                Database.closeConnection(connection);
                Database.closePreparedStatement(pstmt);
            }
        }
    }

    public int insertTransactionEntryForFlexepinVoucher(String trackingId,String payoutAmount,String currency, String status) throws PZDBViolationException
    {
        int id = 0;
        Connection connection   = null;
        PreparedStatement pstmt = null;
        try
        {
            connection = Database.getConnection();
            String updateRecord = "insert into transaction_flexepin_details (status,timestamp,amount,currency,trackingid) values(?,?,?,?,?)";
            pstmt = connection.prepareStatement(updateRecord);


            pstmt.setString(1,status);
            pstmt.setString(2, functions.getTimestamp());

            if(functions.isValueNull(payoutAmount))
                pstmt.setString(3, payoutAmount);
            else
                pstmt.setString(3,"0.00");

            if(functions.isValueNull(currency) && !currency.equalsIgnoreCase("ALL"))
                pstmt.setString(4, currency);
            else
                pstmt.setString(4,"");

            pstmt.setString(5,trackingId);

            int num = pstmt.executeUpdate();
            transactionLogger.error("insert for flexepin details for payout -->" + pstmt);
            if (num == 1)
            {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs != null)
                {
                    while (rs.next())
                    {
                        id = rs.getInt(1);
                    }
                }
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseDBViolationException(PaymentDAO.class.getName(), "insertTransactionCommon()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }

       return id;
    }

    public void updateCaptureforFlexepinVoucher(HashMap<String,String> hashMap,Comm3DResponseVO transRespDetails,String paymentId, String trackingid, String status, String amount) throws PZDBViolationException
    {
        Connection connection               = null;
        PreparedStatement preparedStatement = null;
        try
        {
            connection = Database.getConnection();
            String params = "";
            if (hashMap != null &&  hashMap.get("voucherNumber") != null)
                params = " ,voucher='"+hashMap.get("voucherNumber")+"'";

            String updateRecord = "UPDATE transaction_flexepin_details SET status=?,timestamp=?,expiry=?,amount=?,currency=?,paymentid=?,ean=?,voucherStatus=?,serial=? "+params+" WHERE trackingid=?";
            preparedStatement   = connection.prepareStatement(updateRecord);

            preparedStatement.setString(1,status);
            preparedStatement.setString(2, functions.getTimestamp());
            if (transRespDetails.getResponseTime() != null)
                preparedStatement.setString(3, transRespDetails.getResponseTime());
            else if (hashMap != null)
                preparedStatement.setString(3, hashMap.getOrDefault("expiry",""));

            preparedStatement.setString(4, amount);

            if (functions.isValueNull(transRespDetails.getCurrency()))
                preparedStatement.setString(5, transRespDetails.getCurrency());
            else if (hashMap != null)
                preparedStatement.setString(5, hashMap.getOrDefault("currency",""));

            if (functions.isValueNull(paymentId))
                preparedStatement.setString(6, paymentId);
            else if (hashMap != null)
                preparedStatement.setString(6, hashMap.getOrDefault("transaction_id",""));

            if (functions.isValueNull(transRespDetails.getAuthCode()))
                preparedStatement.setString(7, transRespDetails.getAuthCode());
            else if (hashMap != null)
                preparedStatement.setString(7, hashMap.getOrDefault("ean",""));

            if (hashMap != null)
                preparedStatement.setString(8, hashMap.getOrDefault("status",""));
            else
                preparedStatement.setString(8, "");

            if (hashMap != null)
                preparedStatement.setString(9, hashMap.getOrDefault("serial",""));
            else
                preparedStatement.setString(9, "");

            preparedStatement.setString(10, trackingid);

            preparedStatement.executeUpdate();

            transactionLogger.error("update FlexepinVoucher details query ------> "+preparedStatement);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "updatePaymentIdforSofort()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "updatePaymentIdforSofort()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

}