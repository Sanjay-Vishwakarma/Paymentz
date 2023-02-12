package com.payment.checkers;

import com.directi.pg.*;
import com.manager.vo.CardDetailsVO;
import com.manager.vo.MerchantDetailsVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncryptionException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 3/29/14
 * Time: 5:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaymentChecker
{
    private static Logger log = new Logger(PaymentChecker.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PaymentChecker.class.getName());

    public boolean isCardTypeValid(String cardtype,String ccpan)
    {
        if(!cardtype.equals("ANY") && !Functions.getCardType(ccpan).equals(cardtype))
        {
            return false;
        }
        return true;
    }

    public boolean isCardTypeAndPayModeValid(String paymodeid,String cardtypeid,String memberid)
    {
        Connection conn = null;
        String query="select * from member_account_mapping where paymodeid=? and cardtypeid= ? and memberid=?" ;
        try
        {
            conn= Database.getConnection();
            PreparedStatement ps=conn.prepareStatement(query);
            ps.setString(1,paymodeid);
            ps.setString(2,cardtypeid);
            ps.setString(3,memberid);
            ResultSet rs=ps.executeQuery();
            if(!rs.next())
            {
                return false;
            }
        }
        catch(Exception e)
        {
            log.error("EXCEPTION OCCURED",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return true;
    }

    public boolean isWhitelistedCardnumber(String toid, String accountid, String ccnum)
    {
        Connection con = null;
        boolean isehitelisted = false;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        int len = ccnum.length();

        String firstsix = "";
        String lastfour = "";
        if (len == 16)
        {
            firstsix = ccnum.substring(ccnum.length() - 16, ccnum.length() - 10);
            lastfour = ccnum.substring(ccnum.length() - 4);
        }
        else if (len == 15)
        {
            firstsix = ccnum.substring(ccnum.length() - 15, ccnum.length() - 9);
            lastfour = ccnum.substring(ccnum.length() - 4);
        }
        else if (len == 14)
        {
            firstsix = ccnum.substring(ccnum.length() - 14, ccnum.length() - 8);
            lastfour = ccnum.substring(ccnum.length() - 4);
        }
        else if (len == 13)
        {
            firstsix = ccnum.substring(ccnum.length() - 13, ccnum.length() - 7);
            lastfour = ccnum.substring(ccnum.length() - 4);
        }
        else if (len == 12)
        {
            firstsix = ccnum.substring(ccnum.length() - 12, ccnum.length() - 6);
            lastfour = ccnum.substring(ccnum.length() - 4);
        }
        else if (len == 11)
        {
            firstsix = ccnum.substring(ccnum.length() - 11, ccnum.length() - 5);
            lastfour = ccnum.substring(ccnum.length() - 4);
        }
        else
        {
            isehitelisted = false;
        }
        try
        {
            con = Database.getConnection();
            String whitelist = "N";
            String qry = "SELECT iswhitelisted FROM members WHERE memberid=?";
            preparedStatement = con.prepareStatement(qry);
            preparedStatement.setString(1, toid);
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                whitelist = rs.getString("iswhitelisted");
            }
            int count1 = 0;
            if (whitelist.equalsIgnoreCase("Y"))
            {
                String qry2 = "select count(*) as count from whitelist_details where memberid=? and accountid=?  and isTemp='N'";
                preparedStatement = con.prepareStatement(qry2);
                preparedStatement.setString(1, toid);
                preparedStatement.setString(2, accountid);
                rs = preparedStatement.executeQuery();
                if (rs.next())
                {
                    count1 = rs.getInt("count");
                }
                if (count1 > 0)
                {
                    String qry1 = "select * from whitelist_details where firstsix=? and lastfour=? and memberid=? and accountid=?  and isTemp='N'";
                    preparedStatement = con.prepareStatement(qry1);
                    preparedStatement.setString(1, firstsix);
                    preparedStatement.setString(2, lastfour);
                    preparedStatement.setString(3, toid);
                    preparedStatement.setString(4, accountid);
                    rs = preparedStatement.executeQuery();
                    if (rs.next())
                    {
                        isehitelisted = true;
                    }
                    else
                    {
                        isehitelisted = false;
                    }
                }
                else
                {
                    isehitelisted = true;
                }
            }
            else
            {
                isehitelisted = true;
            }
        }
        catch (SystemError se)
        {
            log.error("SystemException----",se);
            transactionLogger.error("SystemException while getting whitelisted card number----",se);
            //PZExceptionHandler.raiseAndHandleDBViolationException("PaymentChecker.java","isWhitelistedCardnumber()",null,"common","SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,se.getMessage(),se.getCause(),toid,null);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQl exception while getting whitelisted card number----",e);
            //PZExceptionHandler.raiseAndHandleDBViolationException("PaymentChecker.java","isWhitelistedCardnumber()",null,"common","SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,e.getMessage(),e.getCause(),toid,null);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return isehitelisted;
    }

    public boolean isIpWhitelistedForMember(String memberId,String ipAddress)
    {
        Connection conn = null;
        //boolean isIpWhitelist = true;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try
        {
            conn = Database.getConnection();
            String isIpWhitelisted = "N";
            String query = "select isIpWhitelisted from members where memberid = ?";
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,memberId);
            rs = preparedStatement.executeQuery();
            if(rs.next())
            {
                isIpWhitelisted = rs.getString("isIpWhitelisted");
            }
            int count1 = 0;
            log.debug("isIpWhitelisted value...."+isIpWhitelisted);
            transactionLogger.debug("isIpWhitelisted value...."+isIpWhitelisted);
            if(isIpWhitelisted.equalsIgnoreCase("Y"))
            {
                String qry2 = "select count(id) as count from ipwhitelist where memberId=? and ipAddress=?";
                preparedStatement = conn.prepareStatement(qry2);
                preparedStatement.setString(1, memberId);
                preparedStatement.setString(2,ipAddress);
                rs = preparedStatement.executeQuery();
                log.debug("inside condition====YES");
                transactionLogger.debug("inside condition====YES");
                if (rs.next())
                {
                    count1 = rs.getInt("count");
                }
                if (count1 > 0)
                {
                    log.debug("found record...."+count1);
                    transactionLogger.debug("found record...."+count1);
                    return true;
                }
                else
                {
                    log.debug("not found record...");
                    transactionLogger.debug("not found record...");
                    return false;
                }
            }
            log.debug("outside if condition===YES");
            transactionLogger.debug("outside if condition===YES");
        }
        catch (SystemError systemError)
        {
            log.error("System Error"+systemError);
            transactionLogger.error("System Error"+systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL Exception"+e);
            transactionLogger.error("SQL Exception"+e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return true;
    }

    public boolean isIpWhitelistedForMember(MerchantDetailsVO merchantDetailsVO, String ipAddress)
    {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        int count1 = 0;

        try
        {
            if(merchantDetailsVO.getIsIpWhiteListed().equalsIgnoreCase("Y"))
            {
                String qry2 = "select count(id) as count from ipwhitelist where memberId=? and ipAddress=?";
                preparedStatement = conn.prepareStatement(qry2);
                preparedStatement.setString(1, merchantDetailsVO.getMemberId());
                preparedStatement.setString(2,ipAddress);
                rs = preparedStatement.executeQuery();
                if (rs.next())
                {
                    count1 = rs.getInt("count");
                }
                if (count1 > 0)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        catch (SQLException e)
        {
            log.error("SQL Exception",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return true;
    }


    public  boolean isIpWhitelistedForPartner(String partnerId,String ipAddress)
    {
        Connection conn = null;
        boolean isIpWhitelist = true;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try
        {
            conn = Database.getConnection();
            String isIpWhitelisted = "N";
            String query = "select isIpWhitelisted from partners where partnerid = ?";
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,partnerId);
            rs = preparedStatement.executeQuery();
            if(rs.next())
            {
                isIpWhitelisted = rs.getString("isIpWhitelisted");
            }
            int count1 = 0;

            if(isIpWhitelisted.equalsIgnoreCase("Y"))
            {
                String qry2 = "select count(id) as count from ipwhitelist where partnerId=? and ipAddress=?";
                preparedStatement = conn.prepareStatement(qry2);
                preparedStatement.setString(1, partnerId);
                preparedStatement.setString(2,ipAddress);
                rs = preparedStatement.executeQuery();
                if (rs.next())
                {
                    count1 = rs.getInt("count");
                }
                if (count1 > 0)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        catch (SystemError systemError)
        {
            log.error("System Error"+systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL Exception"+e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return true;
    }

    public  boolean isIpWhitelistedForAgent(String agentId,String ipAddress)
    {
        Connection conn = null;

        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try
        {
            conn = Database.getConnection();
            String isIpWhitelisted = "N";
            String query = "select isIpWhitelisted from agents where agentid = ?";
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,agentId);
            rs = preparedStatement.executeQuery();
            if(rs.next())
            {
                isIpWhitelisted = rs.getString("isIpWhitelisted");
            }
            int count1 = 0;

            if(isIpWhitelisted.equalsIgnoreCase("Y"))
            {
                String qry2 = "select count(id) as count from ipwhitelist where agentId=? and ipAddress=?";
                preparedStatement = conn.prepareStatement(qry2);
                preparedStatement.setString(1, agentId);
                preparedStatement.setString(2,ipAddress);
                rs = preparedStatement.executeQuery();
                if (rs.next())
                {
                    count1 = rs.getInt("count");
                }
                if (count1 > 0)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        catch (SystemError systemError)
        {
            log.error("System Error"+systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL Exception"+e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return true;
    }

    public boolean isPartnerexistindb(String partnerid,String ipAddress,String fromtype) throws SystemError
    {
        boolean existindb=false;
        Connection con=null;
        try
        {
            con= Database.getConnection();
            String query ="SELECT COUNT(*) AS cntPartner FROM partners WHERE partnerId=? AND partnerName=?";
            PreparedStatement p = con.prepareStatement(query);
            p.setString(1,partnerid);
            p.setString(2,fromtype);
            ResultSet rs = p.executeQuery();
            rs.next();
            if (rs.getInt("cntPartner") > 0)
            {
                existindb=true;
            }

        }
        catch (SQLException e)
        {
            log.error("SQL EXCEPTION: " + e.getMessage(), e );
        }
        finally
        {
            Database.closeConnection(con);
        }
        return existindb;
    }



    public boolean isTerminalIdValid(String terminalId, String memberId)
    {
        Connection dbConn = null;

        try
        {
            log.debug("terminal Id ");
            transactionLogger.debug("terminal Id ");

            dbConn = Database.getConnection();
            transactionLogger.debug("PaymentChecker.isTerminalValid from member_account_mapping ::: DB Call");
            String query = "select memberid from member_account_mapping where terminalid= ? and memberid=?";
            PreparedStatement preparedStatement = dbConn.prepareStatement(query);
            preparedStatement.setString(1,terminalId);
            preparedStatement.setString(2, memberId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                return true;
            }

            return false;

        }
        catch (Exception e)
        {
            log.error("Error while checking terminalid ", e);
            transactionLogger.error("Error while checking terminalid ", e);

        }
        finally
        {
            Database.closeConnection(dbConn);
        }

        return false;

    }


    public boolean isTerminalActive(String terminalId)
    {
        Connection dbConn = null;
        String isActive = "";

        try
        {
            log.debug("is terminal active ");
            transactionLogger.debug("is terminal active ");

            dbConn = Database.getConnection();
            transactionLogger.debug("PaymentChecker.isTerminalActive from member_account_mapping ::: DB Call");
            String query = "select isActive from member_account_mapping where terminalid= ? ";
            PreparedStatement preparedStatement = dbConn.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(terminalId));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                isActive = resultSet.getString("isActive");

                if(isActive.equals("Y"))
                    return true;
                else
                    return false;
            }
            else
            {
                return false;
            }

        }
        catch (Exception e)
        {
            log.error("Error while checking terminalid ", e);
            transactionLogger.error("Error while checking terminalid ", e);

        }
        finally
        {
            Database.closeConnection(dbConn);
        }

        return false;


    }

    public boolean isTerminalTest(String terminalId)
    {
        Connection dbConn = null;
        String isTest = "";

        try
        {
            log.debug("is terminal active ");

            dbConn = Database.getConnection();
            String query = "select isTest from member_account_mapping where terminalid= ? ";
            PreparedStatement preparedStatement = dbConn.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(terminalId));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                isTest = resultSet.getString("isTest");

                if(isTest.equals("Y"))
                    return true;
                else
                    return false;
            }
            else
            {
                return false;
            }

        }
        catch (Exception e)
        {
            log.error("Error while checking terminalid ", e);
        }
        finally
        {
            Database.closeConnection(dbConn);
        }

        return false;
    }

    public boolean isAmountValidForJPY(String currency,BigDecimal amount)
    {
        boolean isValid = false;
        BigInteger amountInt = amount.multiply(new BigDecimal(100)).toBigInteger();
        BigInteger amountRound = amount.toBigInteger().multiply(new BigInteger("100"));
        if(currency.equals("JPY"))
        {
            if(amountInt.subtract(amountRound).intValue()==0)
            {
               return true;
            }
        }
        return false;
    }

    public boolean isAmountValidForJPY(String currency,String amountStr)
    {
        boolean isValid = false;
        BigDecimal amount = new BigDecimal(amountStr);
        amount = amount.setScale(2, BigDecimal.ROUND_DOWN);
        BigInteger amountInt = amount.multiply(new BigDecimal(100)).toBigInteger();
        BigInteger amountRound = amount.toBigInteger().multiply(new BigInteger("100"));
        if(currency.equals("JPY"))
        {
            if(amountInt.subtract(amountRound).intValue()==0)
            {
                return true;
            }
        }
        return false;
    }

    public boolean isIpWhitelistedForTransaction(String memberId,String ipAddress)
    {
        Connection conn = null;
        //boolean isIpWhitelist = true;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try
        {
            conn = Database.getConnection();

            int count1 = 0;

            String qry2 = "select count(id) as count from ipwhitelist where memberId=? and ipAddress=?";
            preparedStatement = conn.prepareStatement(qry2);
            preparedStatement.setString(1, memberId);
            preparedStatement.setString(2,ipAddress);
            rs = preparedStatement.executeQuery();
            log.debug("inside condition====YES");
            transactionLogger.debug("inside condition====YES");
            if (rs.next())
            {
                count1 = rs.getInt("count");
            }
            if (count1 > 0)
            {
                log.debug("found record...."+count1);
                transactionLogger.debug("found record...."+count1);
                return true;
            }
            else
            {
                log.debug("not found record...");
                transactionLogger.debug("not found record...");
                return false;
            }
        }
        catch (SystemError systemError)
        {
            log.error("System Error"+systemError);
            transactionLogger.error("System Error" + systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL Exception"+e);
            transactionLogger.error("SQL Exception" + e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return true;
    }

    public boolean isIpWhitelistedForTransactionByPartner(String partnerId, String ipAddress)
    {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try
        {
            conn = Database.getConnection();

            int count1 = 0;

            String qry2 = "select count(id) as count from ipwhitelist where partnerId=? and ipAddress=?";
            preparedStatement = conn.prepareStatement(qry2);
            preparedStatement.setString(1, partnerId);
            preparedStatement.setString(2,ipAddress);
            rs = preparedStatement.executeQuery();
            log.debug("inside condition====YES");
            transactionLogger.debug("inside condition====YES");
            if (rs.next())
            {
                count1 = rs.getInt("count");
            }
            if (count1 > 0)
            {
                log.debug("found record...."+count1);
                transactionLogger.debug("found record...."+count1);
                return true;
            }
            else
            {
                log.debug("not found record...");
                transactionLogger.debug("not found record...");
                return false;
            }
        }
        catch (SystemError systemError)
        {
            log.error("System Error"+systemError);
            transactionLogger.error("System Error"+systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL Exception"+e);
            transactionLogger.error("SQL Exception"+e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return true;
    }

    public boolean isWhitelistedCardnumberforTransaction(String toid, String accountid, String ccnum,String cardWhitelistLevel)
    {
        Date d=new Date();
        transactionLogger.error("isWhitelistedCardnumberforTransaction Start Time------>"+d.getTime());
        Connection con = null;
        boolean isWhitelisted = false;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        int len = ccnum.length();

        String firstsix = "";
        String lastfour = "";
        switch (len)
        {
            case 16:
                firstsix = ccnum.substring(ccnum.length() - 16, ccnum.length() - 10);
                lastfour = ccnum.substring(ccnum.length() - 4);
                break;
            case 15:
                firstsix = ccnum.substring(ccnum.length() - 15, ccnum.length() - 9);
                lastfour = ccnum.substring(ccnum.length() - 4);
                break;
            case 14:
                firstsix = ccnum.substring(ccnum.length() - 14, ccnum.length() - 8);
                lastfour = ccnum.substring(ccnum.length() - 4);
                break;
            case 13:
                firstsix = ccnum.substring(ccnum.length() - 13, ccnum.length() - 7);
                lastfour = ccnum.substring(ccnum.length() - 4);
                break;
            case 12:
                firstsix = ccnum.substring(ccnum.length() - 12, ccnum.length() - 6);
                lastfour = ccnum.substring(ccnum.length() - 4);
                break;
            case 11:
                firstsix = ccnum.substring(ccnum.length() - 11, ccnum.length() - 5);
                lastfour = ccnum.substring(ccnum.length() - 4);
                break;
            default:
                isWhitelisted = false;
        }

        try
        {
            con = Database.getConnection();

            StringBuffer qry1 = new StringBuffer("select id from whitelist_details where firstsix=? and lastfour=? and memberid=? and isTemp='N'");
            if("Account".equalsIgnoreCase(cardWhitelistLevel))
                qry1.append(" and accountid=?");
            preparedStatement = con.prepareStatement(qry1.toString());
            preparedStatement.setString(1, firstsix);
            preparedStatement.setString(2, lastfour);
            preparedStatement.setString(3, toid);
            if("Account".equalsIgnoreCase(cardWhitelistLevel))
                preparedStatement.setString(4, accountid);
            transactionLogger.error("cardWhitelistLevel--->"+cardWhitelistLevel);
            transactionLogger.error("query1--->"+preparedStatement);
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                isWhitelisted = true;
            }
            else
            {
                isWhitelisted = false;
            }
        }
        catch (SystemError se)
        {
            log.error("SystemException----",se);
            transactionLogger.error("SystemException while getting whitelisted card number----",se);
            //PZExceptionHandler.raiseAndHandleDBViolationException("PaymentChecker.java","isWhitelistedCardnumber()",null,"common","SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,se.getMessage(),se.getCause(),toid,null);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQl exception while getting whitelisted card number----",e);
            //PZExceptionHandler.raiseAndHandleDBViolationException("PaymentChecker.java","isWhitelistedCardnumber()",null,"common","SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,e.getMessage(),e.getCause(),toid,null);
        }
        finally
        {
            Database.closeConnection(con);
        }
        transactionLogger.error("isWhitelistedCardnumberforTransaction End Time------>"+new Date().getTime());
        transactionLogger.error("isWhitelistedCardnumberforTransaction Diff Time------>"+(new Date().getTime()-d.getTime()));
        return isWhitelisted;
    }

    public boolean isWhitelistedEmailforTransaction(String toid, String accountid, String email, List<CardDetailsVO> listOfCards,String cardWhitelistLevel)
    {
        Connection con = null;
        boolean isEmailWhitelisted = false;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        CardDetailsVO cardDetailsVO = null;

                try
                {
                    con = Database.getConnection();
                    int count1 = 0;

                       StringBuffer qry1 = new StringBuffer("select id,firstsix,lastfour, isTemp from whitelist_details where emailAddr=? and memberid=? and isTemp='N'");
                    if("Account".equalsIgnoreCase(cardWhitelistLevel))
                        qry1.append(" and accountid=?");
                        preparedStatement = con.prepareStatement(qry1.toString());
                        preparedStatement.setString(1, email);
                        preparedStatement.setString(2, toid);
                    if("Account".equalsIgnoreCase(cardWhitelistLevel))
                        preparedStatement.setString(3, accountid);
                    transactionLogger.error("preparedStatement----->"+preparedStatement);
                        rs = preparedStatement.executeQuery();
                        while (rs.next())
                        {
                            cardDetailsVO = new CardDetailsVO();
                            //System.out.println("firstsix----"+rs.getString("firstsix")+"----lastfour----"+rs.getString("lastfour"));
                            cardDetailsVO.setFirstsix(rs.getString("firstsix"));
                            cardDetailsVO.setLastfour(rs.getString("lastfour"));
                            cardDetailsVO.setIsTemp(rs.getString("isTemp"));
                            listOfCards.add(cardDetailsVO);
                            isEmailWhitelisted = true;
                        }


                }
                catch (SystemError se)
                {
                    log.error("SystemException----", se);
                }
                catch (SQLException e)
                {
                    log.error("SQLException----", e); //To change body of catch statement use File | Settings | File Templates.
                }
                finally
                {
                    Database.closeConnection(con);
                }
                return isEmailWhitelisted;
        }

    public boolean isWhitelistedCardDetailsforTransaction(String toid, String accountid, String name,String ipAddress,String expiryDate)
    {
        Connection con = null;
        boolean isCardDetailsWhitelisted = false;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        Functions functions=new Functions();

        try
        {
            con = Database.getConnection();
            int count1 = 0;

            StringBuffer qry1 =new StringBuffer("select id from whitelist_details where memberid=? and accountid=?  and isTemp='N'");
            if(functions.isValueNull(name))
                qry1.append(" and name='"+name+"'");
            if(functions.isValueNull(ipAddress))
                qry1.append(" and ipAddress='"+ipAddress+"'");
            if(functions.isValueNull(expiryDate))
                qry1.append(" and expiryDate='"+ ESAPI.encryptor().hash(expiryDate,toid)+"'");
            preparedStatement = con.prepareStatement(qry1.toString());
            preparedStatement.setString(1, toid);
            preparedStatement.setString(2, accountid);
            transactionLogger.error("isWhitelistedCardDetailsforTransaction----->"+preparedStatement);
            rs = preparedStatement.executeQuery();
                if (rs.next())
                {
                    isCardDetailsWhitelisted = true;
                }



        }
        catch (SystemError se)
        {
            log.error("SystemException----", se);
        }
        catch (SQLException e)
        {
            log.error("SQLException----", e);  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (EncryptionException e)
        {
            log.error("EncryptionException----", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return isCardDetailsWhitelisted;
    }

    public void insertWhitelistedCardDetails(String toid, String accountid, String ccnum, String email)
    {
        Connection conn = null;
        ResultSet rs = null;
        int len = ccnum.length();

        String firstsix = "";
        String lastfour = "";
        switch (len)
        {
            case 16:
                firstsix = ccnum.substring(ccnum.length() - 16, ccnum.length() - 10);
                lastfour = ccnum.substring(ccnum.length() - 4);
                break;
            case 15:
                firstsix = ccnum.substring(ccnum.length() - 15, ccnum.length() - 9);
                lastfour = ccnum.substring(ccnum.length() - 4);
                break;
            case 14:
                firstsix = ccnum.substring(ccnum.length() - 14, ccnum.length() - 8);
                lastfour = ccnum.substring(ccnum.length() - 4);
                break;
            case 13:
                firstsix = ccnum.substring(ccnum.length() - 13, ccnum.length() - 7);
                lastfour = ccnum.substring(ccnum.length() - 4);
                break;
            case 12:
                firstsix = ccnum.substring(ccnum.length() - 12, ccnum.length() - 6);
                lastfour = ccnum.substring(ccnum.length() - 4);
                break;
            case 11:
                firstsix = ccnum.substring(ccnum.length() - 11, ccnum.length() - 5);
                lastfour = ccnum.substring(ccnum.length() - 4);
                break;

        }
        try
        {
            conn = Database.getConnection();
            String query = "INSERT INTO whitelist_details (firstsix,lastfour,accountid,memberid,emailAddr,isTemp, isApproved) VALUES (?,?,?,?,?,?,?)";
            PreparedStatement p=conn.prepareStatement(query);

            p.setString(1, firstsix);
            p.setString(2, lastfour);
            p.setString(3, accountid);
            p.setString(4, toid);
            p.setString(5, email);
            p.setString(6, "Y");
            p.setString(7, "N");
            int num = p.executeUpdate();
            transactionLogger.debug("insert query----"+p);
        }
        catch (SystemError e)
        {
            transactionLogger.error("SystemError----",e);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException----",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
}