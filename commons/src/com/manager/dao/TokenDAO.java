package com.manager.dao;

import com.directi.pg.*;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.manager.TokenManager;
import com.manager.vo.*;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.validators.vo.ReserveField2VO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.sql.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: sandip
 * Date: 4/3/15
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class TokenDAO
{
    private static Logger logger = new Logger(TokenDAO.class.getName());
    private Functions functions = new Functions();

    public boolean checkUniqueOderIdForToken(TokenRequestVO tokenRequestVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean status = true;
        String query = "";
        try
        {
            conn = Database.getRDBConnection();
            query = "select token,expiry_date,cnum from token_master where toid=? and merchant_orderid=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, tokenRequestVO.getMemberId());
            pstmt.setString(2, tokenRequestVO.getDescription());
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                status = false;
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isTokenUnique()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isTokenUnique()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public boolean isNewCard(String memberId, String cardNumber) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean status = true;
        String query = "";
        try
        {
            //conn = Database.getConnection();
            conn=Database.getRDBConnection();
            query = "select cnum from token_master where toid=? and isactive='Y'";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                if (cardNumber.equals(PzEncryptor.decryptPAN(rs.getString("cnum"))))
                {
                    status = false;
                    break;
                }
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isNewCard()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isNewCard()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public String isNewAccount(String memberId, TokenRequestVO tokenRequestVO) throws PZDBViolationException
    {
        GenericCardDetailsVO cardDetailsVO = tokenRequestVO.getCardDetailsVO();
        ReserveField2VO reserveField2VO = tokenRequestVO.getReserveField2VO();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String tokenId = "";
        StringBuffer query = new StringBuffer();
        try
        {
            conn = Database.getRDBConnection();
            query.append("SELECT tm.tokenid,tm.token,bad.bic,bad.iban,bad.accountNumber,bad.accountType,bad.routingNumber FROM token_master AS tm JOIN bankaccount_details AS bad WHERE tm.bank_accountid = bad.bank_accountid AND tm.toid = ? AND tm.isactive = 'Y'");
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                if (functions.isValueNull(rs.getString("bic")) && functions.isValueNull(rs.getString("iban")))
                {
                    if (cardDetailsVO.getBIC().equals(rs.getString("bic")) && cardDetailsVO.getIBAN().equals(rs.getString("iban")))
                    {
                        tokenId = rs.getString("tokenid");
                    }
                }
                else if (functions.isValueNull(rs.getString("accountNumber")) && functions.isValueNull(rs.getString("routingNumber")) && functions.isValueNull(rs.getString("accountType")))
                {
                    if (reserveField2VO.getAccountNumber().equals(rs.getString("accountNumber")) && reserveField2VO.getAccountType().equals(rs.getString("accountType")) && reserveField2VO.getRoutingNumber().equals(rs.getString("routingNumber")))
                    {
                        tokenId = rs.getString("tokenid");
                    }
                }
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isNewAccount()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isNewAccount()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenId;
    }

    public String isNewAccountPaymitco(String memberId, String accountNumber, String accountType, String routingNumber) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String token = "";
        String query = "";
        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT tm.token,bad.accountNumber,bad.accountType,bad.routingNumber FROM token_master AS tm JOIN bankaccount_details AS bad WHERE tm.bank_accountid = bad.bank_accountid AND tm.toid = ? AND tm.isActive = 'Y'";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                if (accountNumber.equals(rs.getString("accountNumber")) && accountType.equals(rs.getString("accountType")) && routingNumber.equals(rs.getString("routingNumber")))
                {
                    token = rs.getString("token");
                }
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isNewAccount()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isNewAccount()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return token;
    }


    public boolean isNewCardForPartner(String partnerId, String cardNumber) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean status = true;
        String query = "";
        try
        {
            conn = Database.getRDBConnection();
            query = "select cnum from token_master where partnerid=? and isactive='Y' and toid is NULL";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, partnerId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                if (cardNumber.equals(PzEncryptor.decryptPAN(rs.getString("cnum"))))
                {
                    status = false;
                    break;
                }
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isNewCardForPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isNewCardForPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public boolean isNewAccountForPartner(String partnerId, String bic) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean status = true;
        String query = "";
        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT b.bic FROM token_master AS tm JOIN bankaccount_details AS b WHERE tm.bank_accountid = b.bank_accountid AND tm.isactive='Y' AND tm.partnerid = ? AND tm.toid IS NULL";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, partnerId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                if (bic.equals(PzEncryptor.decryptPAN(rs.getString("bic"))))
                {
                    status = false;
                    break;
                }
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isNewAccountForPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isNewAccountForPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public String isTokenAvailable(String memberId, String cardNumber,String expDate) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";
        String tokenId = "";
        try
        {
            conn = Database.getRDBConnection();
            query = "select tokenid,cnum,expiry_date,token from token_master where toid=? and isactive='Y'";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                if (cardNumber.equals(PzEncryptor.decryptPAN(rs.getString("cnum"))) && expDate.equals(PzEncryptor.decryptExpiryDate(rs.getString("expiry_date"))))
                {
                    tokenId = rs.getString("tokenid");
                    break;
                }
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" , se);
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isNewCard()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::",e);
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isNewCard()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenId;
    }

    public String isCardAvailable(String memberId, String cardNumber) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";
        String token = "";
        try
        {
            conn = Database.getRDBConnection();
            query = "select tokenid,cnum,token from token_master where toid=? and isactive='Y'";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                if (cardNumber.equals(PzEncryptor.decryptPAN(rs.getString("cnum"))))
                {
                    token = rs.getString("token");
                    break;
                }
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isNewCard()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isNewCard()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return token;
    }

    public String isAccountAvailable(String memberId, String bic) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";
        String token = "";
        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT b.bic,t.token FROM bankaccount_details AS b JOIN token_master AS t ON b.bank_accountid=t.bank_accountid WHERE t.toid=? AND isactive = 'Y'";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                if (bic.equals(PzEncryptor.decryptPAN(rs.getString("bic"))))
                {
                    token = rs.getString("token");
                    break;
                }
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isNewCard()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isNewCard()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return token;
    }

    public boolean isCardholderRegistered(String toId, String cardholderId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";
        boolean status = false;
        try
        {
           // conn = Database.getConnection();
            conn=Database.getRDBConnection();
            query = "select cardholderid from cardholder_master where toid=? and cardholderid=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, toId);
            pstmt.setString(2, cardholderId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                status = true;
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isCardholderRegistered()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isCardholderRegistered()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public boolean isCardholderRegisteredWithPartner(String partnerId, String cardholderId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";
        boolean status = false;
        try
        {
            conn = Database.getRDBConnection();
            query = "select cardholderid from cardholder_master where partnerid=? and cardholderid=? and toid IS NULL ";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, partnerId);
            pstmt.setString(2, cardholderId);
            logger.debug("isCardholderRegisteredWithPartner qry-->" + pstmt);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                status = true;
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isCardholderRegisteredWithPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isCardholderRegisteredWithPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public TokenResponseVO createToken(TokenRequestVO tokenRequestVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = "";
        String token = "";
        String status = "";
        CommCardDetailsVO commCardDetailsVO = tokenRequestVO.getCommCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = tokenRequestVO.getAddressDetailsVO();
        TokenResponseVO tokenResponseVO = new TokenResponseVO();
        try
        {
            conn = Database.getConnection();
            query = "insert into token_master(tokenid,token,toid,trackingid,merchant_orderid,cnum,expiry_date,cardholder_firstname,cardholder_lastname,cardholderemail,terminalid,country,city,state,street,zip,telnocc,telno,birthdate,language,isactive,cardholderid,tokencreationon,generatedBy)values(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?)";
            pstmt = conn.prepareStatement(query);
            token = TokenManager.generateToken();
            pstmt.setString(1, token);
            pstmt.setString(2, tokenRequestVO.getMemberId());
            pstmt.setString(3, tokenRequestVO.getTrackingId());
            pstmt.setString(4, tokenRequestVO.getDescription());
            pstmt.setString(5, PzEncryptor.encryptPAN(commCardDetailsVO.getCardNum()));
            pstmt.setString(6, PzEncryptor.encryptExpiryDate(commCardDetailsVO.getExpMonth() + "/" + commCardDetailsVO.getExpYear()));
            pstmt.setString(7, addressDetailsVO.getFirstname());
            pstmt.setString(8, addressDetailsVO.getLastname());
            pstmt.setString(9, addressDetailsVO.getEmail());
            pstmt.setString(10, tokenRequestVO.getTerminalId());
            pstmt.setString(11, addressDetailsVO.getCountry());
            pstmt.setString(12, addressDetailsVO.getCity());
            pstmt.setString(13, addressDetailsVO.getState());
            pstmt.setString(14, addressDetailsVO.getStreet());
            pstmt.setString(15, addressDetailsVO.getZipCode());
            pstmt.setString(16, addressDetailsVO.getTelnocc());
            pstmt.setString(17, addressDetailsVO.getPhone());
            pstmt.setString(18, addressDetailsVO.getBirthdate());
            pstmt.setString(19, addressDetailsVO.getLanguage());
            pstmt.setString(20, "Y");
            pstmt.setString(21,addressDetailsVO.getCustomerid());
            pstmt.setString(22, tokenRequestVO.getGeneratedBy());
            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                status = "success";
                TokenDetailsVO tokenDetails = getTokenDetails(tokenRequestVO.getMemberId(), token);
                tokenResponseVO.setValidDays(tokenDetails.getTokenValidDays());
                tokenResponseVO.setTokenId(tokenDetails.getTokenId());

            }
        }
        catch (SystemError se)
        {
            status = "failed";
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createToken()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            status = "failed";
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createToken()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        tokenResponseVO.setToken(token);
        tokenResponseVO.setStatus(status);
        return tokenResponseVO;
    }

    public TokenResponseVO createTokenNew(TokenRequestVO tokenRequestVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = "";
        String token = "";
        String status = "";
        CommCardDetailsVO commCardDetailsVO = tokenRequestVO.getCommCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = tokenRequestVO.getAddressDetailsVO();
        TokenResponseVO tokenResponseVO = new TokenResponseVO();
        Functions functions = new Functions();
        try
        {
            conn = Database.getConnection();
            query = "insert into token_master(tokenid,token,toid,trackingid,merchant_orderid,cnum,expiry_date,cardholder_firstname,cardholder_lastname,cardholderemail,terminalid,country,city,state,street,zip,telnocc,telno,birthdate,language,isactive,tokencreationon,cardholderid,partnerid,generatedBy)values(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?)";
            pstmt = conn.prepareStatement(query);
            token = TokenManager.generateToken();
            pstmt.setString(1, token);
            if (functions.isValueNull(tokenRequestVO.getMemberId()))
            {
                pstmt.setString(2, tokenRequestVO.getMemberId());
            }
            else
            {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, tokenRequestVO.getTrackingId());
            pstmt.setString(4, tokenRequestVO.getDescription());
            pstmt.setString(5, PzEncryptor.encryptPAN(commCardDetailsVO.getCardNum()));
            pstmt.setString(6, PzEncryptor.encryptExpiryDate(commCardDetailsVO.getExpMonth() + "/" + commCardDetailsVO.getExpYear()));
            pstmt.setString(7, addressDetailsVO.getFirstname());
            pstmt.setString(8, addressDetailsVO.getLastname());
            pstmt.setString(9, addressDetailsVO.getEmail());
            pstmt.setString(10, tokenRequestVO.getTerminalId());
            pstmt.setString(11, addressDetailsVO.getCountry());
            pstmt.setString(12, addressDetailsVO.getCity());
            pstmt.setString(13, addressDetailsVO.getState());
            pstmt.setString(14, addressDetailsVO.getStreet());
            pstmt.setString(15, addressDetailsVO.getZipCode());
            pstmt.setString(16, addressDetailsVO.getTelnocc());
            pstmt.setString(17, addressDetailsVO.getPhone());
            pstmt.setString(18, addressDetailsVO.getBirthdate());
            pstmt.setString(19, addressDetailsVO.getLanguage());
            pstmt.setString(20, "Y");
            pstmt.setString(21, tokenRequestVO.getCardholderId());
            pstmt.setString(22, tokenRequestVO.getPartnerId());
            pstmt.setString(23, tokenRequestVO.getGeneratedBy());
            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                status = "success";
                TokenDetailsVO tokenDetails = getTokenDetails(tokenRequestVO.getMemberId(), token);
                tokenResponseVO.setValidDays(tokenDetails.getTokenValidDays());
                tokenResponseVO.setTokenDetailsVO(tokenDetails);
                //tokenResponseVO.setValidDays(30);
            }
        }
        catch (SystemError se)
        {
            status = "failed";
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createTokenNew()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            status = "failed";
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createTokenNew()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        tokenResponseVO.setToken(token);
        tokenResponseVO.setStatus(status);
        return tokenResponseVO;
    }

    public TokenResponseVO createNewToken(TokenRequestVO tokenRequestVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = "";
        String token = "";
        String status = "";
        GenericCardDetailsVO commCardDetailsVO = tokenRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = tokenRequestVO.getAddressDetailsVO();
        TokenResponseVO tokenResponseVO = new TokenResponseVO();
        Functions functions = new Functions();
        try
        {
            conn = Database.getConnection();
            query = "insert into token_master(tokenid,token,toid,trackingid,merchant_orderid,cnum,expiry_date,cardholder_firstname,cardholder_lastname,cardholderemail,terminalid,country,city,state,street,zip,telnocc,telno,birthdate,language,isactive,tokencreationon,cardholderid,partnerid,generatedBy)values(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?)";
            pstmt = conn.prepareStatement(query);
            token = TokenManager.generateToken();
            pstmt.setString(1, token);
            if (functions.isValueNull(tokenRequestVO.getMemberId()))
            {
                pstmt.setString(2, tokenRequestVO.getMemberId());
            }
            else
            {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, tokenRequestVO.getTrackingId());
            pstmt.setString(4, tokenRequestVO.getDescription());
            pstmt.setString(5, PzEncryptor.encryptPAN(commCardDetailsVO.getCardNum()));
            pstmt.setString(6, PzEncryptor.encryptExpiryDate(commCardDetailsVO.getExpMonth() + "/" + commCardDetailsVO.getExpYear()));
            pstmt.setString(7, addressDetailsVO.getFirstname());
            pstmt.setString(8, addressDetailsVO.getLastname());
            pstmt.setString(9, addressDetailsVO.getEmail());
            pstmt.setString(10, tokenRequestVO.getTerminalId());
            pstmt.setString(11, addressDetailsVO.getCountry());
            pstmt.setString(12, addressDetailsVO.getCity());
            pstmt.setString(13, addressDetailsVO.getState());
            pstmt.setString(14, addressDetailsVO.getStreet());
            pstmt.setString(15, addressDetailsVO.getZipCode());
            pstmt.setString(16, addressDetailsVO.getTelnocc());
            pstmt.setString(17, addressDetailsVO.getPhone());
            pstmt.setString(18, addressDetailsVO.getBirthdate());
            pstmt.setString(19, addressDetailsVO.getLanguage());
            pstmt.setString(20, "Y");
            pstmt.setString(21, tokenRequestVO.getCardholderId());
            pstmt.setString(22, tokenRequestVO.getPartnerId());
            pstmt.setString(23, tokenRequestVO.getGeneratedBy());
            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                status = "success";
                TokenDetailsVO tokenDetails = getTokenDetails(tokenRequestVO.getMemberId(), token);
                tokenResponseVO.setValidDays(tokenDetails.getTokenValidDays());
                tokenResponseVO.setTokenDetailsVO(tokenDetails);
            }
        }
        catch (SystemError se)
        {
            status = "failed";
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createTokenNew()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            status = "failed";
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createTokenNew()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        tokenResponseVO.setToken(token);
        tokenResponseVO.setStatus(status);
        return tokenResponseVO;
    }

    public String createTokenWithAccount(String bankAccountId, TokenRequestVO tokenRequestVO) throws PZDBViolationException
    {
        Connection conn = null;
        GenericAddressDetailsVO addressDetailsVO = tokenRequestVO.getAddressDetailsVO();
        PreparedStatement pstmt = null;
        String query = "";
        String token = "";
        String tokenId = "";
        try
        {
            conn = Database.getConnection();
            query = "insert into token_master(tokenid,token,toid,trackingid,merchant_orderid,cardholder_firstname,cardholder_lastname,cardholderemail,country,city,state,street,zip,telnocc,telno,birthdate,isactive,tokencreationon,partnerid,generatedBy,bank_accountid)values(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?)";
            pstmt = conn.prepareStatement(query);
            token = TokenManager.generateToken();

            pstmt.setString(1, token);
            pstmt.setString(2, tokenRequestVO.getMemberId());
            pstmt.setString(3, tokenRequestVO.getTrackingId());
            pstmt.setString(4, tokenRequestVO.getDescription());
            pstmt.setString(5, addressDetailsVO.getFirstname());
            pstmt.setString(6, addressDetailsVO.getLastname());
            pstmt.setString(7, addressDetailsVO.getEmail());
            pstmt.setString(8, addressDetailsVO.getCountry());
            pstmt.setString(9, addressDetailsVO.getCity());
            pstmt.setString(10, addressDetailsVO.getState());
            pstmt.setString(11, addressDetailsVO.getStreet());
            pstmt.setString(12, addressDetailsVO.getZipCode());
            pstmt.setString(13, addressDetailsVO.getTelnocc());
            pstmt.setString(14, addressDetailsVO.getPhone());
            pstmt.setString(15, addressDetailsVO.getBirthdate());
            pstmt.setString(16, "Y");
            pstmt.setString(17, tokenRequestVO.getPartnerId());
            pstmt.setString(18, tokenRequestVO.getGeneratedBy());
            pstmt.setString(19, bankAccountId);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if(rs.next())
            {
                tokenId = rs.getString(1);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createTokenWithAccount()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createTokenWithAccount()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenId;
    }

    private TokenResponseVO createAccountTokenByPartner(String bankAccountId, TokenRequestVO tokenRequestVO) throws PZDBViolationException
    {
        Connection conn = null;
        TokenResponseVO tokenResponseVO = new TokenResponseVO();
        GenericCardDetailsVO cardDetailsVO = tokenRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = tokenRequestVO.getAddressDetailsVO();
        PreparedStatement pstmt = null;
        String query = "";
        String token = "";
        String status = "fail";
        try
        {
            conn = Database.getConnection();
            query = "insert into token_master(tokenid,token,toid,trackingid,merchant_orderid,cnum,expiry_date,cardholder_firstname,cardholder_lastname,cardholderemail,terminalid,country,city,state,street,zip,telnocc,telno,birthdate,language,isactive,tokencreationon,cardholderid,partnerid,generatedBy,bank_accountid)values(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?)";
            pstmt = conn.prepareStatement(query);
            token = TokenManager.generateToken();
            pstmt.setString(1, token);
            pstmt.setNull(2, Types.INTEGER);
            pstmt.setString(3, tokenRequestVO.getTrackingId());
            pstmt.setString(4, tokenRequestVO.getDescription());
            pstmt.setString(5, PzEncryptor.encryptPAN(cardDetailsVO.getCardNum()));
            pstmt.setString(6, PzEncryptor.encryptExpiryDate(cardDetailsVO.getExpMonth() + "/" + cardDetailsVO.getExpYear()));
            pstmt.setString(7, addressDetailsVO.getFirstname());
            pstmt.setString(8, addressDetailsVO.getLastname());
            pstmt.setString(9, addressDetailsVO.getEmail());
            pstmt.setString(10, tokenRequestVO.getTerminalId());
            pstmt.setString(11, addressDetailsVO.getCountry());
            pstmt.setString(12, addressDetailsVO.getCity());
            pstmt.setString(13, addressDetailsVO.getState());
            pstmt.setString(14, addressDetailsVO.getStreet());
            pstmt.setString(15, addressDetailsVO.getZipCode());
            pstmt.setString(16, addressDetailsVO.getTelnocc());
            pstmt.setString(17, addressDetailsVO.getPhone());
            pstmt.setString(18, addressDetailsVO.getBirthdate());
            pstmt.setString(19, addressDetailsVO.getLanguage());
            pstmt.setString(20, "Y");
            pstmt.setString(21, tokenRequestVO.getCardholderId());
            pstmt.setString(22, tokenRequestVO.getPartnerId());
            pstmt.setString(23, "");
            pstmt.setString(24, bankAccountId);

            logger.debug("insert into token_master--"+pstmt);
            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                status = "success";
                TokenDetailsVO tokenDetails = getTokenDetailsByPartner(tokenRequestVO.getPartnerId(), token);
                tokenResponseVO.setToken(tokenDetails.getToken());
                tokenResponseVO.setTokenDetailsVO(tokenDetails);
            }
        }
        catch (SystemError se)
        {
            status = "fail";
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createToken()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            status = "fail";
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createToken()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        tokenResponseVO.setStatus(status);
        return tokenResponseVO;
    }

    public String insertBankAccountDetails(TokenRequestVO tokenRequestVO) throws PZDBViolationException
    {
        String bankAccountId = "";
        String query = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        try
        {
            conn = Database.getConnection();
            query = "INSERT INTO bankaccount_details(bank_accountid,bic,iban,accountNumber,accountType,routingNumber)VALUES(NULL,?,?,?,?,?)";
            pstmt = conn.prepareStatement(query);

            pstmt.setString(1, tokenRequestVO.getCardDetailsVO().getBIC());
            pstmt.setString(2, tokenRequestVO.getCardDetailsVO().getIBAN());
//            pstmt.setString(3, tokenRequestVO.getAddressDetailsVO().getFirstname() + " " + tokenRequestVO.getAddressDetailsVO().getLastname());
//            pstmt.setString(4, tokenRequestVO.getAddressDetailsVO().getCountry());
//            pstmt.setString(5, tokenRequestVO.getAddressDetailsVO().getCity());
//            pstmt.setString(6, tokenRequestVO.getAddressDetailsVO().getState());
//            pstmt.setString(7, tokenRequestVO.getAddressDetailsVO().getStreet());
//            pstmt.setString(8, tokenRequestVO.getAddressDetailsVO().getZipCode());
//            pstmt.setString(9, tokenRequestVO.getAddressDetailsVO().getTelnocc());
//            pstmt.setString(10, tokenRequestVO.getAddressDetailsVO().getPhone());
            pstmt.setString(3, tokenRequestVO.getReserveField2VO().getAccountNumber());
            pstmt.setString(4, tokenRequestVO.getReserveField2VO().getAccountType());
            pstmt.setString(5, tokenRequestVO.getReserveField2VO().getRoutingNumber());

            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if(rs.next())
            {
                bankAccountId = rs.getString(1);
            }
        }
        catch (SQLException e)
        {
            logger.error("SystemError::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "insertBankAccountDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "insertBankAccountDetails()", null, "common", "SystemError Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return bankAccountId;
    }

    public String createTokenNewForRegistration(TokenRequestVO tokenRequestVO) throws PZDBViolationException
    {
        Connection conn = null;
        GenericAddressDetailsVO addressDetailsVO = tokenRequestVO.getAddressDetailsVO();
        PreparedStatement pstmt = null;
        String query = "";
        String registrationToken = "";
        String status = "";
        try
        {
            conn = Database.getConnection();
            query = "INSERT INTO registration_master(registration_id,toid,registration_token,token,trackingid,paymodeid,cardtypeid,terminalid,isactive,tokencreation_date,partnerid,customerid) VALUES(NULL,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?)";
            pstmt = conn.prepareStatement(query);
            registrationToken = TokenManager.generateTokenRegestration();

            if (functions.isValueNull(tokenRequestVO.getMemberId()))
            {
                pstmt.setString(1, tokenRequestVO.getMemberId());
            }
            else
            {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setString(2, registrationToken);
            pstmt.setString(3, tokenRequestVO.getToken());
            pstmt.setString(4, tokenRequestVO.getTrackingId());
            pstmt.setString(5, tokenRequestVO.getPaymentType());
            pstmt.setString(6, tokenRequestVO.getCardType());
            pstmt.setString(7, tokenRequestVO.getTerminalId());
            pstmt.setString(8, "Y");
            pstmt.setString(9, tokenRequestVO.getPartnerId());
            pstmt.setString(10, tokenRequestVO.getCustomerId());

            pstmt.executeUpdate();
        }
        catch (SystemError se)
        {
            status = "failed";
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createTokenNewForRegistration()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            status = "failed";
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createTokenNewForRegistration()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return registrationToken;
    }

    public TokenResponseVO createTokenByPartner(TokenRequestVO tokenRequestVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = "";
        String token = "";
        String status = "";
        CommCardDetailsVO commCardDetailsVO = tokenRequestVO.getCommCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = tokenRequestVO.getAddressDetailsVO();
        TokenResponseVO tokenResponseVO = new TokenResponseVO();
        try
        {
            conn = Database.getConnection();
            query = "insert into token_master(tokenid,token,toid,trackingid,merchant_orderid,cnum,expiry_date,cardholder_firstname,cardholder_lastname,cardholderemail,terminalid,country,city,state,street,zip,telnocc,telno,birthdate,language,isactive,tokencreationon,cardholderid,partnerid,generatedBy)values(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?)";
            pstmt = conn.prepareStatement(query);
            token = TokenManager.generateToken();
            pstmt.setString(1, token);
            pstmt.setNull(2, Types.INTEGER);
            pstmt.setString(3, tokenRequestVO.getTrackingId());
            pstmt.setString(4, tokenRequestVO.getDescription());
            pstmt.setString(5, PzEncryptor.encryptPAN(commCardDetailsVO.getCardNum()));
            pstmt.setString(6, PzEncryptor.encryptExpiryDate(commCardDetailsVO.getExpMonth() + "/" + commCardDetailsVO.getExpYear()));
            pstmt.setString(7, addressDetailsVO.getFirstname());
            pstmt.setString(8, addressDetailsVO.getLastname());
            pstmt.setString(9, addressDetailsVO.getEmail());
            pstmt.setString(10, tokenRequestVO.getTerminalId());
            pstmt.setString(11, addressDetailsVO.getCountry());
            pstmt.setString(12, addressDetailsVO.getCity());
            pstmt.setString(13, addressDetailsVO.getState());
            pstmt.setString(14, addressDetailsVO.getStreet());
            pstmt.setString(15, addressDetailsVO.getZipCode());
            pstmt.setString(16, addressDetailsVO.getTelnocc());
            pstmt.setString(17, addressDetailsVO.getPhone());
            pstmt.setString(18, addressDetailsVO.getBirthdate());
            pstmt.setString(19, addressDetailsVO.getLanguage());
            pstmt.setString(20, "Y");
            pstmt.setString(21, tokenRequestVO.getCardholderId());
            pstmt.setString(22, tokenRequestVO.getPartnerId());
            pstmt.setString(23, "");
            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                status = "success";
                TokenDetailsVO tokenDetails = getTokenDetailsByPartner(tokenRequestVO.getPartnerId(), token);
                tokenResponseVO.setValidDays(tokenDetails.getTokenValidDays());
                tokenResponseVO.setStatus("success");
                tokenResponseVO.setTokenDetailsVO(tokenDetails);
            }
        }
        catch (SystemError se)
        {
            status = "failed";
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createTokenByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            status = "failed";
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createTokenByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        tokenResponseVO.setToken(token);
        tokenResponseVO.setStatus(status);
        return tokenResponseVO;
    }

    public TokenResponseVO createRestTokenByPartner(TokenRequestVO tokenRequestVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = "";
        String token = "";
        String status = "";
        CommCardDetailsVO commCardDetailsVO = tokenRequestVO.getCommCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = tokenRequestVO.getAddressDetailsVO();
        TokenResponseVO tokenResponseVO = new TokenResponseVO();
        try
        {
            conn = Database.getConnection();
            query = "insert into token_master(tokenid,token,toid,trackingid,merchant_orderid,cnum,expiry_date,cardholder_firstname,cardholder_lastname,cardholderemail,terminalid,country,city,state,street,zip,telnocc,telno,birthdate,language,isactive,tokencreationon,cardholderid,partnerid,generatedBy)values(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?)";
            pstmt = conn.prepareStatement(query);
            token = TokenManager.generateToken();
            pstmt.setString(1, token);
            pstmt.setNull(2, Types.INTEGER);
            pstmt.setString(3, tokenRequestVO.getTrackingId());
            pstmt.setString(4, tokenRequestVO.getDescription());
            pstmt.setString(5, PzEncryptor.encryptPAN(commCardDetailsVO.getCardNum()));
            pstmt.setString(6, PzEncryptor.encryptExpiryDate(commCardDetailsVO.getExpMonth() + "/" + commCardDetailsVO.getExpYear()));
            pstmt.setString(7, addressDetailsVO.getFirstname());
            pstmt.setString(8, addressDetailsVO.getLastname());
            pstmt.setString(9, addressDetailsVO.getEmail());
            pstmt.setString(10, tokenRequestVO.getTerminalId());
            pstmt.setString(11, addressDetailsVO.getCountry());
            pstmt.setString(12, addressDetailsVO.getCity());
            pstmt.setString(13, addressDetailsVO.getState());
            pstmt.setString(14, addressDetailsVO.getStreet());
            pstmt.setString(15, addressDetailsVO.getZipCode());
            pstmt.setString(16, addressDetailsVO.getTelnocc());
            pstmt.setString(17, addressDetailsVO.getPhone());
            pstmt.setString(18, addressDetailsVO.getBirthdate());
            pstmt.setString(19, addressDetailsVO.getLanguage());
            pstmt.setString(20, "Y");
            pstmt.setString(21, tokenRequestVO.getCardholderId());
            pstmt.setString(22, tokenRequestVO.getPartnerId());
            pstmt.setString(23, "");
            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                status = "success";
                TokenDetailsVO tokenDetails = getTokenDetailsByPartner(tokenRequestVO.getPartnerId(), token);
//                tokenResponseVO.setValidDays(tokenDetails.getTokenValidDays());
                tokenResponseVO.setTokenDetailsVO(tokenDetails);
            }
        }
        catch (SystemError se)
        {
            status = "failed";
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createTokenByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            status = "failed";
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createTokenByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        tokenResponseVO.setToken(token);
        tokenResponseVO.setStatus(status);
        return tokenResponseVO;
    }

    public CardholderResponseVO registerCardholder(CardHolderRequestVO cardHolderRequestVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = "";
        String cardholderId = "";
        String status = "N";
        String statusMsg = "";
        Functions functions = new Functions();
        CardholderResponseVO cardholderResponseVO = new CardholderResponseVO();
        try
        {
            conn = Database.getConnection();
            query = "INSERT INTO cardholder_master (cardholderid,toid,firstname,lastname,email,birthdate,telno,gender,zip,registeredtime,partnerid) VALUES (NULL,?,?,?,?,?,?,?,?,UNIX_TIMESTAMP(NOW()),?)";
            pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            if (functions.isValueNull(cardHolderRequestVO.getToId()))
            {
                pstmt.setString(1, cardHolderRequestVO.getToId());
            }
            else
            {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setString(2, cardHolderRequestVO.getFirstName());
            pstmt.setString(3, cardHolderRequestVO.getLastName());
            pstmt.setString(4, cardHolderRequestVO.getEmail());
            pstmt.setString(5, cardHolderRequestVO.getBirthDate());
            pstmt.setString(6, cardHolderRequestVO.getTelNo());
            pstmt.setString(7, cardHolderRequestVO.getGender());
            pstmt.setString(8, cardHolderRequestVO.getZip());
            pstmt.setString(9, cardHolderRequestVO.getPartnerId());
            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next())
                {
                    cardholderId = String.valueOf(rs.getInt(1));
                    status = "Y";
                    statusMsg = "Cardholder registered successfully.";
                }
            }
        }
        catch (SystemError se)
        {
            status = "N";
            statusMsg = "Internal error while processing your request.";
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "registerCardholder()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            status = "N";
            statusMsg = "Internal error while processing your request.";
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "registerCardholder()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        cardholderResponseVO.setCardholderId(cardholderId);
        cardholderResponseVO.setStatus(status);
        cardholderResponseVO.setStatusDescription(statusMsg);
        return cardholderResponseVO;
    }

    public CardholderResponseVO registerCardholderByPartner(CardHolderRequestVO cardHolderRequestVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = "";
        String cardholderId = "";
        String status = "N";
        String statusMsg = "";
        CardholderResponseVO cardholderResponseVO = new CardholderResponseVO();
        try
        {
            conn = Database.getConnection();
            query = "INSERT INTO cardholder_master (cardholderid,toid,firstname,lastname,email,birthdate,telno,gender,zip,registeredtime,partnerid) VALUES (NULL,?,?,?,?,?,?,?,?,UNIX_TIMESTAMP(NOW()),?)";
            pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstmt.setNull(1, Types.INTEGER);
            pstmt.setString(2, cardHolderRequestVO.getFirstName());
            pstmt.setString(3, cardHolderRequestVO.getLastName());
            pstmt.setString(4, cardHolderRequestVO.getEmail());
            pstmt.setString(5, cardHolderRequestVO.getBirthDate());
            pstmt.setString(6, cardHolderRequestVO.getTelNo());
            pstmt.setString(7, cardHolderRequestVO.getGender());
            pstmt.setString(8, cardHolderRequestVO.getZip());
            pstmt.setString(9, cardHolderRequestVO.getPartnerId());
            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next())
                {
                    cardholderId = String.valueOf(rs.getInt(1));
                    status = "Y";
                    statusMsg = "Cardholder registered successfully.";
                }
            }
        }
        catch (SystemError se)
        {
            status = "N";
            statusMsg = "Internal error while processing your request.";
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "registerCardholderByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            status = "N";
            statusMsg = "Internal error while processing your request.";
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "registerCardholderByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        cardholderResponseVO.setCardholderId(cardholderId);
        cardholderResponseVO.setStatus(status);
        cardholderResponseVO.setStatusDescription(statusMsg);
        return cardholderResponseVO;
    }

    public TokenDetailsVO getTokenDetails(String memberId, String token, String terminalid) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";
        TokenDetailsVO tokenDetailsVO = null;
        CommCardDetailsVO commCardDetailsVO = null;
        GenericAddressDetailsVO addressDetailsVO = null;
        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT tm.tokenid,tm.token,tm.toid,tm.trackingid,tm.merchant_orderid,tm.cnum,tm.expiry_date,tm.cardholder_firstname,tm.cardholder_lastname,tm.cardholderemail,tm.terminalid,tm.country,tm.city,tm.state,tm.street," +
                    "tm.zip,tm.telnocc,tm.telno,tm.birthdate,tm.language,tm.isactive,FROM_UNIXTIME(tm.tokencreationon) as creationtime,m.tokenvaliddays,m.isAddrDetailsRequired FROM token_master as tm join members as m on tm.toid=m.memberid  and tm.toid=? and tm.token=? and tm.terminalid=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, memberId);
            pstmt.setString(2, token);
            pstmt.setString(3, terminalid);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                tokenDetailsVO = new TokenDetailsVO();
                commCardDetailsVO = new CommCardDetailsVO();
                addressDetailsVO = new GenericAddressDetailsVO();

                addressDetailsVO.setFirstname(rs.getString("cardholder_firstname"));
                addressDetailsVO.setLastname(rs.getString("cardholder_lastname"));
                addressDetailsVO.setEmail(rs.getString("cardholderemail"));
                addressDetailsVO.setCountry(rs.getString("country"));
                addressDetailsVO.setCity(rs.getString("city"));
                addressDetailsVO.setState(rs.getString("state"));
                addressDetailsVO.setStreet(rs.getString("street"));
                addressDetailsVO.setZipCode(rs.getString("zip"));
                addressDetailsVO.setTelnocc(rs.getString("telnocc"));
                addressDetailsVO.setPhone(rs.getString("telno"));
                addressDetailsVO.setBirthdate(rs.getString("birthdate"));
                addressDetailsVO.setLanguage(rs.getString("language"));


                commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(rs.getString("cnum")));
                String expiryDate[] = PzEncryptor.decryptExpiryDate(rs.getString("expiry_date")).split("/");
                commCardDetailsVO.setExpMonth(expiryDate[0]);
                commCardDetailsVO.setExpYear(expiryDate[1]);

                tokenDetailsVO.setTokenId(rs.getString("tokenid"));
                tokenDetailsVO.setToken(rs.getString("token"));
                tokenDetailsVO.setMemberId(rs.getString("toid"));
                tokenDetailsVO.setTerminalId(rs.getString("terminalid"));
                tokenDetailsVO.setDescription(rs.getString("merchant_orderid"));
                tokenDetailsVO.setTrackingId(rs.getString("trackingid"));
                tokenDetailsVO.setCreationOn(rs.getString("creationtime"));
                tokenDetailsVO.setIsActive(rs.getString("isactive"));
                tokenDetailsVO.setTokenValidDays(rs.getInt("tokenvaliddays"));
                tokenDetailsVO.setIsAddrDetailsRequired(rs.getString("isAddrDetailsRequired"));

                tokenDetailsVO.setCommCardDetailsVO(commCardDetailsVO);
                tokenDetailsVO.setAddressDetailsVO(addressDetailsVO);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getTokenDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getTokenDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVO;
    }

    public TokenDetailsVO getInitialTokenDetailsWithMemebrId(String token, String memberId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";
        TokenDetailsVO tokenDetailsVO = null;

        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT rt.memberid, tm.bank_accountid, rt.paymodeid, rt.cardtypeid, rt.currency, rt.isactive AS is_active_reg, tm.isactive AS is_active_token, rt.terminalid, FROM_UNIXTIME(rt.tokencreation_date) AS creationtime,rt.notificationUrl " +
                    "FROM registration_master AS rt, token_master AS tm WHERE rt.registration_token = ? AND rt.memberid = ? AND rt.tokenid = tm.tokenid";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, token);
            pstmt.setString(2, memberId);
            rs = pstmt.executeQuery();
            logger.debug("getInitialTokenDetails QRY---->" + pstmt);
            if (rs.next())
            {
                tokenDetailsVO = new TokenDetailsVO();
                tokenDetailsVO.setMemberId(memberId);
                tokenDetailsVO.setTokenAccountId(rs.getString("bank_accountid"));
                tokenDetailsVO.setPaymentType(rs.getString("paymodeid"));
                tokenDetailsVO.setCardType(rs.getString("cardtypeid"));
                tokenDetailsVO.setCurrency(rs.getString("currency"));
                tokenDetailsVO.setIsActiveReg(rs.getString("is_active_reg"));
                tokenDetailsVO.setIsActive(rs.getString("is_active_token"));
                tokenDetailsVO.setTerminalId(rs.getString("terminalid"));
                tokenDetailsVO.setNotificationUrl(rs.getString("notificationUrl"));
                tokenDetailsVO.setToken(token);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::",e);
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getInitialTokenDetailsWithMemebrId()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::",se);
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getInitialTokenDetailsWithMemebrId()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVO;
    }

    public TokenDetailsVO getInitialTokenDetailsWithPartnerId(String token, String partnerId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";
        TokenDetailsVO tokenDetailsVO = null;

        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT rt.memberid, tm.bank_accountid, rt.paymodeid, rt.cardtypeid, rt.currency, rt.isactive AS isactive_reg, tm.isActive AS isactive_token, rt.terminalid, FROM_UNIXTIME(rt.tokencreation_date) AS creationtime,rt.notificationUrl FROM registration_master AS rt, token_master AS tm WHERE rt.registration_token=? AND rt.partnerid=? AND rt.memberid IS NULL AND rt.tokenid = tm.tokenid";

            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, token);
            pstmt.setString(2, partnerId);
            rs = pstmt.executeQuery();
            logger.debug("getInitialTokenDetails QRY---->" + pstmt);
            if (rs.next())
            {
                tokenDetailsVO = new TokenDetailsVO();
                tokenDetailsVO.setTokenAccountId(rs.getString("bank_accountid"));
                tokenDetailsVO.setPaymentType(rs.getString("paymodeid"));
                tokenDetailsVO.setCardType(rs.getString("cardtypeid"));
                tokenDetailsVO.setCurrency(rs.getString("currency"));
                tokenDetailsVO.setIsActiveReg(rs.getString("isactive_reg"));
                tokenDetailsVO.setIsActive(rs.getString("isactive_token"));
                tokenDetailsVO.setTerminalId(rs.getString("terminalid"));
                tokenDetailsVO.setNotificationUrl(rs.getString("notificationUrl"));
                tokenDetailsVO.setToken(token);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getInitialTokenDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getInitialTokenDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVO;
    }

    public TokenDetailsVO getBankAccountTokenDetailsWithMemberId(String bankAccountId, String token, String memberId, CommonValidatorVO commonValidatorVO, TokenDetailsVO tokenDetailsVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";
        CommCardDetailsVO commCardDetailsVO = null;
        GenericAddressDetailsVO addressDetailsVO = null;
        ReserveField2VO reserveField2VO = null;
        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT tm.merchant_orderid,rt.firstname,rt.lastname,rt.email,rt.terminalid,rt.country,rt.city,rt.state,rt.street,rt.zip,rt.telnocc,rt.telno,rt.birthdate,rt.language,FROM_UNIXTIME(rt.tokencreation_date) AS creationtime,bd.bic,bd.iban,rt.paymodeid,\n" +
                    "rt.cardtypeid, bd.routingNumber, bd.accountNumber, bd.accountType ,rt.notificationUrl FROM token_master AS tm, registration_master AS rt, bankaccount_details AS bd WHERE tm.bank_accountid=bd.bank_accountid  AND tm.bank_accountid = ? AND rt.registration_token = ? AND rt.memberid = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, bankAccountId);
            pstmt.setString(2, token);
            pstmt.setString(3, memberId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                logger.debug("query-----"+pstmt);
                commCardDetailsVO = new CommCardDetailsVO();
                addressDetailsVO = new GenericAddressDetailsVO();
                reserveField2VO = new ReserveField2VO();

                tokenDetailsVO.setDescription(rs.getString("merchant_orderid"));
                if((!functions.isValueNull(addressDetailsVO.getCity())) || (!functions.isValueNull(addressDetailsVO.getCountry())) || (!functions.isValueNull(addressDetailsVO.getZipCode())) || (!functions.isValueNull(addressDetailsVO.getState())) || (!functions.isValueNull(addressDetailsVO.getStreet())))
                {
                    addressDetailsVO.setCountry(rs.getString("country"));
                    addressDetailsVO.setCity(rs.getString("city"));
                    addressDetailsVO.setState(rs.getString("state"));
                    addressDetailsVO.setStreet(rs.getString("street"));
                    addressDetailsVO.setZipCode(rs.getString("zip"));
                    addressDetailsVO.setBirthdate(rs.getString("birthdate"));
                    addressDetailsVO.setLanguage(rs.getString("language"));
                    addressDetailsVO.setFirstname(rs.getString("firstname"));
                    addressDetailsVO.setLastname(rs.getString("lastname"));
                    addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getIp());
                }
                if(!functions.isValueNull(addressDetailsVO.getEmail()))
                    addressDetailsVO.setEmail(rs.getString("email"));
                if(!functions.isValueNull(addressDetailsVO.getTelnocc()))
                    addressDetailsVO.setTelnocc(rs.getString("telnocc"));
                if(!functions.isValueNull(addressDetailsVO.getPhone()))
                    addressDetailsVO.setPhone(rs.getString("telno"));
                tokenDetailsVO.setCreationOn(rs.getString("creationtime"));
                commCardDetailsVO.setBIC(rs.getString("bic"));
                commCardDetailsVO.setIBAN(rs.getString("iban"));
                tokenDetailsVO.setPaymentType(rs.getString("paymodeid"));
                tokenDetailsVO.setCardType(rs.getString("cardtypeid"));
                reserveField2VO.setRoutingNumber(rs.getString("routingNumber"));
                reserveField2VO.setAccountNumber(rs.getString("accountNumber"));
                reserveField2VO.setAccountType(rs.getString("accountType"));

                tokenDetailsVO.setNotificationUrl(rs.getString("notificationUrl"));
                tokenDetailsVO.setCommCardDetailsVO(commCardDetailsVO);
                tokenDetailsVO.setAddressDetailsVO(addressDetailsVO);
                tokenDetailsVO.setReserveField2VO(reserveField2VO);
            }
            if (tokenDetailsVO.getAddressDetailsVO() == null)
            {
                addressDetailsVO = commonValidatorVO.getAddressDetailsVO();
                tokenDetailsVO.setAddressDetailsVO(addressDetailsVO);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::", se);
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getBankAccountTokenDetailsWithMemberId()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::", e);
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getBankAccountTokenDetailsWithMemberId()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVO;
    }

    public TokenDetailsVO getBankAccountTokenDetailsWithPartnerId(String bankAccountId, String token, String partnerId, CommonValidatorVO commonValidatorVO, TokenDetailsVO tokenDetailsVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";
        CommCardDetailsVO commCardDetailsVO = null;
        GenericAddressDetailsVO addressDetailsVO = null;
        ReserveField2VO reserveField2VO = null;
        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT tm.merchant_orderid,rt.firstname,rt.lastname,rt.email,rt.terminalid,rt.country,rt.city,rt.state,rt.street,rt.zip,rt.telnocc,rt.telno,rt.birthdate,rt.language,FROM_UNIXTIME(rt.tokencreation_date) AS creationtime,bd.bic,bd.iban,rt.paymodeid,rt.cardtypeid, bd.routingNumber, \n" +
                    "bd.accountNumber, bd.accountType,rt.notificationUrl FROM token_master AS tm, registration_master AS rt, bankaccount_details AS bd WHERE tm.bank_accountid=bd.bank_accountid  AND tm.bank_accountid=? AND rt.registration_token=? AND rt.partnerid=? AND memberid IS NULL";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, bankAccountId);
            pstmt.setString(2, token);
            pstmt.setString(3, partnerId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                logger.debug("query-----"+pstmt);
                commCardDetailsVO = new CommCardDetailsVO();
                addressDetailsVO = new GenericAddressDetailsVO();
                reserveField2VO = new ReserveField2VO();

                tokenDetailsVO.setDescription(rs.getString("merchant_orderid"));
                if((!functions.isValueNull(addressDetailsVO.getCity())) || (!functions.isValueNull(addressDetailsVO.getCountry())) || (!functions.isValueNull(addressDetailsVO.getZipCode())) || (!functions.isValueNull(addressDetailsVO.getState())) || (!functions.isValueNull(addressDetailsVO.getStreet())))
                {
                    addressDetailsVO.setCountry(rs.getString("country"));
                    addressDetailsVO.setCity(rs.getString("city"));
                    addressDetailsVO.setState(rs.getString("state"));
                    addressDetailsVO.setStreet(rs.getString("street"));
                    addressDetailsVO.setZipCode(rs.getString("zip"));
                    addressDetailsVO.setBirthdate(rs.getString("birthdate"));
                    addressDetailsVO.setLanguage(rs.getString("language"));
                    addressDetailsVO.setFirstname(rs.getString("firstname"));
                    addressDetailsVO.setLastname(rs.getString("lastname"));
                    addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getIp());
                }
                if(!functions.isValueNull(addressDetailsVO.getEmail()))
                    addressDetailsVO.setEmail(rs.getString("email"));
                if(!functions.isValueNull(addressDetailsVO.getTelnocc()))
                    addressDetailsVO.setTelnocc(rs.getString("telnocc"));
                if(!functions.isValueNull(addressDetailsVO.getPhone()))
                    addressDetailsVO.setPhone(rs.getString("telno"));
                tokenDetailsVO.setCreationOn(rs.getString("creationtime"));
                commCardDetailsVO.setBIC(rs.getString("bic"));
                commCardDetailsVO.setIBAN(rs.getString("iban"));
                tokenDetailsVO.setPaymentType(rs.getString("paymodeid"));
                tokenDetailsVO.setCardType(rs.getString("cardtypeid"));
                reserveField2VO.setRoutingNumber(rs.getString("routingNumber"));
                reserveField2VO.setAccountNumber(rs.getString("accountNumber"));
                reserveField2VO.setAccountType(rs.getString("accountType"));

                tokenDetailsVO.setNotificationUrl(rs.getString("notificationUrl"));
                tokenDetailsVO.setCommCardDetailsVO(commCardDetailsVO);
                tokenDetailsVO.setAddressDetailsVO(addressDetailsVO);
                tokenDetailsVO.setReserveField2VO(reserveField2VO);

            }

            if (tokenDetailsVO.getAddressDetailsVO() == null)
            {
                addressDetailsVO = commonValidatorVO.getAddressDetailsVO();
                tokenDetailsVO.setAddressDetailsVO(addressDetailsVO);

            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::", se);
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getBankAccountTokenDetailsWithPartnerId()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::", e);
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getBankAccountTokenDetailsWithPartnerId()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVO;
    }

    public TokenDetailsVO getTokenDetails(String memberId, String token, CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";
        TokenDetailsVO tokenDetailsVO = null;
        CommCardDetailsVO commCardDetailsVO = null;
        GenericAddressDetailsVO addressDetailsVO = null;
        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT tm.tokenid,tm.token,tm.toid,tm.trackingid,tm.merchant_orderid,tm.cnum,tm.expiry_date,tm.cardholder_firstname,tm.cardholder_lastname,tm.cardholderemail,tm.terminalid,tm.country,tm.city,tm.state,tm.street," +
                    "tm.zip,tm.telnocc,tm.telno,tm.birthdate,tm.language,tm.isactive,FROM_UNIXTIME(tm.tokencreationon) as creationtime,m.tokenvaliddays,m.isAddrDetailsRequired,tm.bank_accountid FROM token_master as tm join members as m on tm.toid=m.memberid  and tm.toid=? and tm.token=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, memberId);
            pstmt.setString(2, token);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                //System.out.println("inside 1 if----");
                tokenDetailsVO = new TokenDetailsVO();
                commCardDetailsVO = new CommCardDetailsVO();
                addressDetailsVO = new GenericAddressDetailsVO();

                addressDetailsVO.setFirstname(rs.getString("cardholder_firstname"));
                addressDetailsVO.setLastname(rs.getString("cardholder_lastname"));

                addressDetailsVO.setCountry(rs.getString("country"));
                addressDetailsVO.setCity(rs.getString("city"));
                addressDetailsVO.setState(rs.getString("state"));
                addressDetailsVO.setStreet(rs.getString("street"));
                addressDetailsVO.setZipCode(rs.getString("zip"));
                addressDetailsVO.setTelnocc(rs.getString("telnocc"));
                addressDetailsVO.setPhone(rs.getString("telno"));
                addressDetailsVO.setBirthdate(rs.getString("birthdate"));
                addressDetailsVO.setLanguage(rs.getString("language"));
                addressDetailsVO.setEmail(rs.getString("cardholderemail"));

                if (functions.isValueNull(rs.getString("cnum")))
                {
                    commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(rs.getString("cnum")));
                    String expiryDate[] = PzEncryptor.decryptExpiryDate(rs.getString("expiry_date")).split("/");
                    commCardDetailsVO.setExpMonth(expiryDate[0]);
                    commCardDetailsVO.setExpYear(expiryDate[1]);
                }
                commCardDetailsVO.setCardHolderFirstName(rs.getString("cardholder_firstname"));
                commCardDetailsVO.setCardHolderSurname(rs.getString("cardholder_lastname"));

                tokenDetailsVO.setTokenAccountId(rs.getString("bank_accountid"));
                tokenDetailsVO.setTokenId(rs.getString("tokenid"));
                tokenDetailsVO.setToken(rs.getString("token"));
                tokenDetailsVO.setMemberId(rs.getString("toid"));
                tokenDetailsVO.setTerminalId(rs.getString("terminalid"));
                tokenDetailsVO.setDescription(rs.getString("merchant_orderid"));
                tokenDetailsVO.setTrackingId(rs.getString("trackingid"));
                tokenDetailsVO.setCreationOn(rs.getString("creationtime"));
                tokenDetailsVO.setIsActive(rs.getString("isactive"));
                tokenDetailsVO.setTokenValidDays(rs.getInt("tokenvaliddays"));
                tokenDetailsVO.setIsAddrDetailsRequired(rs.getString("isAddrDetailsRequired"));

                tokenDetailsVO.setCommCardDetailsVO(commCardDetailsVO);
                tokenDetailsVO.setAddressDetailsVO(addressDetailsVO);
            }

            if (tokenDetailsVO.getAddressDetailsVO() == null)
            {
                addressDetailsVO = commonValidatorVO.getAddressDetailsVO();
                tokenDetailsVO.setAddressDetailsVO(addressDetailsVO);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getTokenDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getTokenDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVO;
    }

    public TokenDetailsVO getTokenDetails(String memberId, String token) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";
        TokenDetailsVO tokenDetailsVO = null;
        CommCardDetailsVO commCardDetailsVO = null;
        GenericAddressDetailsVO addressDetailsVO = null;
        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT tm.tokenid,tm.token,tm.toid,tm.trackingid,tm.merchant_orderid,tm.cnum,tm.expiry_date,tm.cardholder_firstname,tm.cardholder_lastname,tm.cardholderemail,tm.terminalid,tm.country,tm.city,tm.state,tm.street," +
                    "tm.zip,tm.telnocc,tm.telno,tm.birthdate,tm.language,tm.isactive,FROM_UNIXTIME(tm.tokencreationon) as creationtime,m.tokenvaliddays,m.isAddrDetailsRequired,tm.bank_accountid FROM token_master as tm join members as m on tm.toid=m.memberid  and tm.toid=? and tm.token=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, memberId);
            pstmt.setString(2, token);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                tokenDetailsVO = new TokenDetailsVO();
                commCardDetailsVO = new CommCardDetailsVO();
                addressDetailsVO = new GenericAddressDetailsVO();

                addressDetailsVO.setFirstname(rs.getString("cardholder_firstname"));
                addressDetailsVO.setLastname(rs.getString("cardholder_lastname"));

                addressDetailsVO.setCountry(rs.getString("country"));
                addressDetailsVO.setCity(rs.getString("city"));
                addressDetailsVO.setState(rs.getString("state"));
                addressDetailsVO.setStreet(rs.getString("street"));
                addressDetailsVO.setZipCode(rs.getString("zip"));
                addressDetailsVO.setTelnocc(rs.getString("telnocc"));
                addressDetailsVO.setPhone(rs.getString("telno"));
                addressDetailsVO.setBirthdate(rs.getString("birthdate"));
                addressDetailsVO.setLanguage(rs.getString("language"));
                addressDetailsVO.setEmail(rs.getString("cardholderemail"));

                if (functions.isValueNull(rs.getString("cnum")))
                {
                    commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(rs.getString("cnum")));
                    String expiryDate[] = PzEncryptor.decryptExpiryDate(rs.getString("expiry_date")).split("/");
                    commCardDetailsVO.setExpMonth(expiryDate[0]);
                    commCardDetailsVO.setExpYear(expiryDate[1]);
                }
                commCardDetailsVO.setCardHolderFirstName(rs.getString("cardholder_firstname"));
                commCardDetailsVO.setCardHolderSurname(rs.getString("cardholder_lastname"));

                /*tokenDetailsVO.setPaymentType(rs.getString("paymodeid"));
                tokenDetailsVO.setCardType(rs.getString("cardtypeid"));*/
                tokenDetailsVO.setTokenAccountId(rs.getString("bank_accountid"));
                tokenDetailsVO.setTokenId(rs.getString("tokenid"));
                tokenDetailsVO.setToken(rs.getString("token"));
                tokenDetailsVO.setMemberId(rs.getString("toid"));
                tokenDetailsVO.setTerminalId(rs.getString("terminalid"));
                tokenDetailsVO.setDescription(rs.getString("merchant_orderid"));
                tokenDetailsVO.setTrackingId(rs.getString("trackingid"));
                tokenDetailsVO.setCreationOn(rs.getString("creationtime"));
                tokenDetailsVO.setIsActive(rs.getString("isactive"));
                tokenDetailsVO.setTokenValidDays(rs.getInt("tokenvaliddays"));
                tokenDetailsVO.setIsAddrDetailsRequired(rs.getString("isAddrDetailsRequired"));

                tokenDetailsVO.setCommCardDetailsVO(commCardDetailsVO);
                tokenDetailsVO.setAddressDetailsVO(addressDetailsVO);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getTokenDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getTokenDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVO;
    }

    public TokenDetailsVO getTokenDetailsByPartner(String partnerId, String token) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";
        TokenDetailsVO tokenDetailsVO = null;
        CommCardDetailsVO commCardDetailsVO = null;
        GenericAddressDetailsVO addressDetailsVO = null;
        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT tm.tokenid,tm.token,tm.toid,tm.trackingid,tm.merchant_orderid,tm.cnum,tm.expiry_date,tm.cardholder_firstname,tm.cardholder_lastname,tm.cardholderemail,tm.terminalid,tm.country,tm.city,tm.state,tm.street," +
                    "tm.zip,tm.telnocc,tm.telno,tm.birthdate,tm.language,tm.isactive,FROM_UNIXTIME(tm.tokencreationon) as creationtime,p.tokenValidDays,p.isAddressRequiredForTokenTransaction FROM token_master as tm join partners as p on tm.partnerid=p.partnerId  and tm.partnerid=? and tm.token=? and toid IS NULL";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, partnerId);
            pstmt.setString(2, token);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                tokenDetailsVO = new TokenDetailsVO();
                commCardDetailsVO = new CommCardDetailsVO();
                addressDetailsVO = new GenericAddressDetailsVO();

                addressDetailsVO.setFirstname(rs.getString("cardholder_firstname"));
                addressDetailsVO.setLastname(rs.getString("cardholder_lastname"));

                addressDetailsVO.setCountry(rs.getString("country"));
                addressDetailsVO.setCity(rs.getString("city"));
                addressDetailsVO.setState(rs.getString("state"));
                addressDetailsVO.setStreet(rs.getString("street"));
                addressDetailsVO.setZipCode(rs.getString("zip"));
                addressDetailsVO.setTelnocc(rs.getString("telnocc"));
                addressDetailsVO.setPhone(rs.getString("telno"));
                addressDetailsVO.setBirthdate(rs.getString("birthdate"));
                addressDetailsVO.setLanguage(rs.getString("language"));
                addressDetailsVO.setEmail(rs.getString("cardholderemail"));

                commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(rs.getString("cnum")));
                String expiryDate[] = PzEncryptor.decryptExpiryDate(rs.getString("expiry_date")).split("/");
                commCardDetailsVO.setExpMonth(expiryDate[0]);
                commCardDetailsVO.setExpYear(expiryDate[1]);
                commCardDetailsVO.setCardHolderFirstName(rs.getString("cardholder_firstname"));
                commCardDetailsVO.setCardHolderSurname(rs.getString("cardholder_lastname"));

                tokenDetailsVO.setTokenId(rs.getString("tokenid"));
                tokenDetailsVO.setToken(rs.getString("token"));
                tokenDetailsVO.setMemberId(rs.getString("toid"));
                tokenDetailsVO.setTerminalId(rs.getString("terminalid"));
                tokenDetailsVO.setDescription(rs.getString("merchant_orderid"));
                tokenDetailsVO.setTrackingId(rs.getString("trackingid"));
                tokenDetailsVO.setCreationOn(rs.getString("creationtime"));
                tokenDetailsVO.setIsActive(rs.getString("isactive"));
                tokenDetailsVO.setTokenValidDays(rs.getInt("tokenValidDays"));
                tokenDetailsVO.setIsAddrDetailsRequired(rs.getString("isAddressRequiredForTokenTransaction"));

                tokenDetailsVO.setCommCardDetailsVO(commCardDetailsVO);
                tokenDetailsVO.setAddressDetailsVO(addressDetailsVO);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getTokenDetailsByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getTokenDetailsByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVO;
    }

    public List<TokenDetailsVO> getCustomerStoredCards(String toId, String cardholderId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";
        TokenDetailsVO tokenDetailsVO = null;
        List<TokenDetailsVO> tokenDetailsVOList = new ArrayList();
        CommCardDetailsVO commCardDetailsVO = null;
        GenericAddressDetailsVO addressDetailsVO = null;
        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT tm.tokenid,tm.token,tm.toid,tm.trackingid,tm.merchant_orderid,tm.cnum,tm.expiry_date,tm.cardholder_firstname,tm.cardholder_lastname,tm.cardholderemail,tm.terminalid,tm.country,tm.city,tm.state,tm.street," +
                    "tm.zip,tm.telnocc,tm.telno,tm.birthdate,tm.language,tm.isactive,FROM_UNIXTIME(tm.tokencreationon) as creationtime,m.tokenvaliddays,m.isAddrDetailsRequired FROM token_master as tm join members as m on tm.toid=m.memberid  and tm.toid=? and tm.cardholderid=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, toId);
            pstmt.setString(2, cardholderId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                tokenDetailsVO = new TokenDetailsVO();
                commCardDetailsVO = new CommCardDetailsVO();
                addressDetailsVO = new GenericAddressDetailsVO();

                addressDetailsVO.setFirstname(rs.getString("cardholder_firstname"));
                addressDetailsVO.setLastname(rs.getString("cardholder_lastname"));

                addressDetailsVO.setCountry(rs.getString("country"));
                addressDetailsVO.setCity(rs.getString("city"));
                addressDetailsVO.setState(rs.getString("state"));
                addressDetailsVO.setStreet(rs.getString("street"));
                addressDetailsVO.setZipCode(rs.getString("zip"));
                addressDetailsVO.setTelnocc(rs.getString("telnocc"));
                addressDetailsVO.setPhone(rs.getString("telno"));
                addressDetailsVO.setBirthdate(rs.getString("birthdate"));
                addressDetailsVO.setLanguage(rs.getString("language"));
                addressDetailsVO.setEmail(rs.getString("cardholderemail"));

                commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(rs.getString("cnum")));
                String expiryDate[] = PzEncryptor.decryptExpiryDate(rs.getString("expiry_date")).split("/");
                commCardDetailsVO.setExpMonth(expiryDate[0]);
                commCardDetailsVO.setExpYear(expiryDate[1]);
                commCardDetailsVO.setCardHolderFirstName(rs.getString("cardholder_firstname"));
                commCardDetailsVO.setCardHolderSurname(rs.getString("cardholder_lastname"));

                tokenDetailsVO.setTokenId(rs.getString("tokenid"));
                tokenDetailsVO.setToken(rs.getString("token"));
                tokenDetailsVO.setMemberId(rs.getString("toid"));
                tokenDetailsVO.setTerminalId(rs.getString("terminalid"));
                tokenDetailsVO.setDescription(rs.getString("merchant_orderid"));
                tokenDetailsVO.setTrackingId(rs.getString("trackingid"));
                tokenDetailsVO.setCreationOn(rs.getString("creationtime"));
                tokenDetailsVO.setIsActive(rs.getString("isactive"));
                tokenDetailsVO.setTokenValidDays(rs.getInt("tokenvaliddays"));
                tokenDetailsVO.setIsAddrDetailsRequired(rs.getString("isAddrDetailsRequired"));

                tokenDetailsVO.setCommCardDetailsVO(commCardDetailsVO);
                tokenDetailsVO.setAddressDetailsVO(addressDetailsVO);

                tokenDetailsVOList.add(tokenDetailsVO);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getTokenDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getTokenDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVOList;
    }

    public List<TokenDetailsVO> getCustomerStoredCardsWithoutCardholderId(String toId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";
        TokenDetailsVO tokenDetailsVO = null;
        List<TokenDetailsVO> tokenDetailsVOList = new ArrayList();
        CommCardDetailsVO commCardDetailsVO = null;
        GenericAddressDetailsVO addressDetailsVO = null;
        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT tm.tokenid,tm.token,tm.toid,tm.trackingid,tm.merchant_orderid,tm.cnum,tm.expiry_date,tm.cardholder_firstname,tm.cardholder_lastname,tm.cardholderemail,tm.terminalid,tm.country,tm.city,tm.state,tm.street," +
                    "tm.zip,tm.telnocc,tm.telno,tm.birthdate,tm.language,tm.isactive,FROM_UNIXTIME(tm.tokencreationon) as creationtime,m.tokenvaliddays,m.isAddrDetailsRequired FROM token_master as tm join members as m on tm.toid=m.memberid  and tm.toid=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, toId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                tokenDetailsVO = new TokenDetailsVO();
                commCardDetailsVO = new CommCardDetailsVO();
                addressDetailsVO = new GenericAddressDetailsVO();

                addressDetailsVO.setFirstname(rs.getString("cardholder_firstname"));
                addressDetailsVO.setLastname(rs.getString("cardholder_lastname"));

                addressDetailsVO.setCountry(rs.getString("country"));
                addressDetailsVO.setCity(rs.getString("city"));
                addressDetailsVO.setState(rs.getString("state"));
                addressDetailsVO.setStreet(rs.getString("street"));
                addressDetailsVO.setZipCode(rs.getString("zip"));
                addressDetailsVO.setTelnocc(rs.getString("telnocc"));
                addressDetailsVO.setPhone(rs.getString("telno"));
                addressDetailsVO.setBirthdate(rs.getString("birthdate"));
                addressDetailsVO.setLanguage(rs.getString("language"));
                addressDetailsVO.setEmail(rs.getString("cardholderemail"));

                commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(rs.getString("cnum")));
                String expiryDate[] = PzEncryptor.decryptExpiryDate(rs.getString("expiry_date")).split("/");
                commCardDetailsVO.setExpMonth(expiryDate[0]);
                commCardDetailsVO.setExpYear(expiryDate[1]);
                commCardDetailsVO.setCardHolderFirstName(rs.getString("cardholder_firstname"));
                commCardDetailsVO.setCardHolderSurname(rs.getString("cardholder_lastname"));

                tokenDetailsVO.setTokenId(rs.getString("tokenid"));
                tokenDetailsVO.setToken(rs.getString("token"));
                tokenDetailsVO.setMemberId(rs.getString("toid"));
                tokenDetailsVO.setTerminalId(rs.getString("terminalid"));
                tokenDetailsVO.setDescription(rs.getString("merchant_orderid"));
                tokenDetailsVO.setTrackingId(rs.getString("trackingid"));
                tokenDetailsVO.setCreationOn(rs.getString("creationtime"));
                tokenDetailsVO.setIsActive(rs.getString("isactive"));
                tokenDetailsVO.setTokenValidDays(rs.getInt("tokenvaliddays"));
                tokenDetailsVO.setIsAddrDetailsRequired(rs.getString("isAddrDetailsRequired"));

                tokenDetailsVO.setCommCardDetailsVO(commCardDetailsVO);
                tokenDetailsVO.setAddressDetailsVO(addressDetailsVO);

                tokenDetailsVOList.add(tokenDetailsVO);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getCustomerStoredCardsWithoutCardholderId()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getCustomerStoredCardsWithoutCardholderId()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVOList;
    }

    public List<TokenDetailsVO> getCustomerStoredCardsByPartner(String partnerId, String cardholderId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";
        TokenDetailsVO tokenDetailsVO = null;
        List<TokenDetailsVO> tokenDetailsVOList = new ArrayList();
        CommCardDetailsVO commCardDetailsVO = null;
        GenericAddressDetailsVO addressDetailsVO = null;
        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT tm.tokenid,tm.token,tm.toid,tm.trackingid,tm.merchant_orderid,tm.cnum,tm.expiry_date,tm.cardholder_firstname,tm.cardholder_lastname,tm.cardholderemail,tm.terminalid,tm.country,tm.city,tm.state,tm.street," +
                    "tm.zip,tm.telnocc,tm.telno,tm.birthdate,tm.language,tm.isactive,FROM_UNIXTIME(tm.tokencreationon) AS creationtime,p.tokenValidDays,p.isAddressRequiredForTokenTransaction FROM token_master AS tm JOIN partners AS p ON tm.partnerid=p.partnerid  AND tm.partnerid=? AND tm.cardholderid=? and toid IS NULL";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, partnerId);
            pstmt.setString(2, cardholderId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                tokenDetailsVO = new TokenDetailsVO();
                commCardDetailsVO = new CommCardDetailsVO();
                addressDetailsVO = new GenericAddressDetailsVO();

                addressDetailsVO.setFirstname(rs.getString("cardholder_firstname"));
                addressDetailsVO.setLastname(rs.getString("cardholder_lastname"));

                addressDetailsVO.setCountry(rs.getString("country"));
                addressDetailsVO.setCity(rs.getString("city"));
                addressDetailsVO.setState(rs.getString("state"));
                addressDetailsVO.setStreet(rs.getString("street"));
                addressDetailsVO.setZipCode(rs.getString("zip"));
                addressDetailsVO.setTelnocc(rs.getString("telnocc"));
                addressDetailsVO.setPhone(rs.getString("telno"));
                addressDetailsVO.setBirthdate(rs.getString("birthdate"));
                addressDetailsVO.setLanguage(rs.getString("language"));
                addressDetailsVO.setEmail(rs.getString("cardholderemail"));

                commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(rs.getString("cnum")));
                String expiryDate[] = PzEncryptor.decryptExpiryDate(rs.getString("expiry_date")).split("/");
                commCardDetailsVO.setExpMonth(expiryDate[0]);
                commCardDetailsVO.setExpYear(expiryDate[1]);
                commCardDetailsVO.setCardHolderFirstName(rs.getString("cardholder_firstname"));
                commCardDetailsVO.setCardHolderSurname(rs.getString("cardholder_lastname"));

                tokenDetailsVO.setTokenId(rs.getString("tokenid"));
                tokenDetailsVO.setToken(rs.getString("token"));
                tokenDetailsVO.setMemberId(rs.getString("toid"));
                tokenDetailsVO.setTerminalId(rs.getString("terminalid"));
                tokenDetailsVO.setDescription(rs.getString("merchant_orderid"));
                tokenDetailsVO.setTrackingId(rs.getString("trackingid"));
                tokenDetailsVO.setCreationOn(rs.getString("creationtime"));
                tokenDetailsVO.setIsActive(rs.getString("isactive"));
                tokenDetailsVO.setTokenValidDays(rs.getInt("tokenValidDays"));
                tokenDetailsVO.setIsAddrDetailsRequired(rs.getString("isAddressRequiredForTokenTransaction"));

                tokenDetailsVO.setCommCardDetailsVO(commCardDetailsVO);
                tokenDetailsVO.setAddressDetailsVO(addressDetailsVO);

                tokenDetailsVOList.add(tokenDetailsVO);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getCustomerStoredCardsByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getCustomerStoredCardsByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVOList;
    }

    public List<TokenDetailsVO> getCustomerStoredCardsByPartnerWithoutCardholderId(String partnerId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";
        TokenDetailsVO tokenDetailsVO = null;
        List<TokenDetailsVO> tokenDetailsVOList = new ArrayList();
        CommCardDetailsVO commCardDetailsVO = null;
        GenericAddressDetailsVO addressDetailsVO = null;
        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT tm.tokenid,tm.token,tm.toid,tm.trackingid,tm.merchant_orderid,tm.cnum,tm.expiry_date,tm.cardholder_firstname,tm.cardholder_lastname,tm.cardholderemail,tm.terminalid,tm.country,tm.city,tm.state,tm.street," +
                    "tm.zip,tm.telnocc,tm.telno,tm.birthdate,tm.language,tm.isactive,FROM_UNIXTIME(tm.tokencreationon) AS creationtime,p.tokenValidDays,p.isAddressRequiredForTokenTransaction FROM token_master AS tm JOIN partners AS p ON tm.partnerid=p.partnerid  AND tm.partnerid=? and toid IS NULL";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, partnerId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                tokenDetailsVO = new TokenDetailsVO();
                commCardDetailsVO = new CommCardDetailsVO();
                addressDetailsVO = new GenericAddressDetailsVO();

                addressDetailsVO.setFirstname(rs.getString("cardholder_firstname"));
                addressDetailsVO.setLastname(rs.getString("cardholder_lastname"));

                addressDetailsVO.setCountry(rs.getString("country"));
                addressDetailsVO.setCity(rs.getString("city"));
                addressDetailsVO.setState(rs.getString("state"));
                addressDetailsVO.setStreet(rs.getString("street"));
                addressDetailsVO.setZipCode(rs.getString("zip"));
                addressDetailsVO.setTelnocc(rs.getString("telnocc"));
                addressDetailsVO.setPhone(rs.getString("telno"));
                addressDetailsVO.setBirthdate(rs.getString("birthdate"));
                addressDetailsVO.setLanguage(rs.getString("language"));
                addressDetailsVO.setEmail(rs.getString("cardholderemail"));

                commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(rs.getString("cnum")));
                String expiryDate[] = PzEncryptor.decryptExpiryDate(rs.getString("expiry_date")).split("/");
                commCardDetailsVO.setExpMonth(expiryDate[0]);
                commCardDetailsVO.setExpYear(expiryDate[1]);
                commCardDetailsVO.setCardHolderFirstName(rs.getString("cardholder_firstname"));
                commCardDetailsVO.setCardHolderSurname(rs.getString("cardholder_lastname"));

                tokenDetailsVO.setTokenId(rs.getString("tokenid"));
                tokenDetailsVO.setToken(rs.getString("token"));
                tokenDetailsVO.setMemberId(rs.getString("toid"));
                tokenDetailsVO.setTerminalId(rs.getString("terminalid"));
                tokenDetailsVO.setDescription(rs.getString("merchant_orderid"));
                tokenDetailsVO.setTrackingId(rs.getString("trackingid"));
                tokenDetailsVO.setCreationOn(rs.getString("creationtime"));
                tokenDetailsVO.setIsActive(rs.getString("isactive"));
                tokenDetailsVO.setTokenValidDays(rs.getInt("tokenValidDays"));
                tokenDetailsVO.setIsAddrDetailsRequired(rs.getString("isAddressRequiredForTokenTransaction"));

                tokenDetailsVO.setCommCardDetailsVO(commCardDetailsVO);
                tokenDetailsVO.setAddressDetailsVO(addressDetailsVO);

                tokenDetailsVOList.add(tokenDetailsVO);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getCustomerStoredCardsByPartnerWithoutCardholderId()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getCustomerStoredCardsByPartnerWithoutCardholderId()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVOList;
    }

    public Hashtable<String, String> getMerchantRegistrationCardList(String memberId, String fdtstamp, String tdtstamp, String description, String firstName, String lastName, String email, int records, int pageno, String role, String useraccname) throws PZDBViolationException
    {
        StringBuilder query = null;
        StringBuilder countQuery = null;
        Connection connection = null;

        Hashtable hash = new Hashtable();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Functions functions = new Functions();

        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;

        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            query = new StringBuilder("select from_unixtime(tokencreationon) as creationtime,tokenid,token,toid,terminalid,merchant_orderid,cardholder_firstname,cardholder_lastname,cardholderemail,isactive,generatedBy,cnum from token_master where toid>0");
            countQuery = new StringBuilder("select count(*) from token_master where toid>0");

            if (role.equalsIgnoreCase("submerchant"))
            {
                query.append(" and toid ='" + memberId + "' and generatedBy='" + useraccname + "'");
                countQuery.append(" and toid ='" + memberId + "' and generatedBy='" + useraccname + "'");
            }
            else
            {
                query.append(" and toid='" + ESAPI.encoder().encodeForSQL(me, memberId) + "'");
                countQuery.append(" and toid='" + ESAPI.encoder().encodeForSQL(me, memberId) + "'");
            }
            if (functions.isValueNull(description))
            {
                query.append(" and merchant_orderid='" + ESAPI.encoder().encodeForSQL(me, description) + "'");
                countQuery.append(" and merchant_orderid='" + ESAPI.encoder().encodeForSQL(me, description) + "'");
            }
            if (functions.isValueNull(firstName))
            {
                query.append(" and cardholder_firstname='" + ESAPI.encoder().encodeForSQL(me, firstName) + "'");
                countQuery.append(" and cardholder_firstname='" + ESAPI.encoder().encodeForSQL(me, firstName) + "'");
            }
            if (functions.isValueNull(lastName))
            {
                query.append(" and cardholder_lastname='" + ESAPI.encoder().encodeForSQL(me, lastName) + "'");
                countQuery.append(" and cardholder_lastname='" + ESAPI.encoder().encodeForSQL(me, lastName) + "'");
            }
            if (functions.isValueNull(email))
            {
                query.append(" and cardholderemail='" + ESAPI.encoder().encodeForSQL(me, email) + "'");
                countQuery.append(" and cardholderemail='" + ESAPI.encoder().encodeForSQL(me, email) + "'");
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and from_unixtime(tokencreationon)>='" + fdtstamp + "'");
                countQuery.append(" and from_unixtime(tokencreationon)>='" + fdtstamp + "'");
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and from_unixtime(tokencreationon)<='" + tdtstamp + "'");
                countQuery.append(" and from_unixtime(tokencreationon)<='" + tdtstamp + "'");
            }

            query.append(" ORDER BY FROM_UNIXTIME(tokencreationon) DESC limit " + start + "," + end);

            logger.debug("Date from Registration History------>"+query);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), connection));
            ResultSet rs = Database.executeQuery(countQuery.toString(), connection);
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
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getMerchantRegistrationCardList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getMerchantRegistrationCardList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return hash;
    }
    //new



    public List<TokenDetailsVO> getMerchantRegistrationList(String memberId,String fdtstamp,String tdtstamp,String description,String firstName,String lastName,String email, PaginationVO paginationVO)throws PZDBViolationException
    {
        logger.debug("inside getMerchantRegistrationList===");
        List<TokenDetailsVO> tokenDetailsVOList = new ArrayList();
        TokenDetailsVO tokenDetailsVO = null;
        GenericAddressDetailsVO addressDetailsVO = null;
        Functions functions = new Functions();
        StringBuffer query = null;
        StringBuffer countQuery = null;
        Connection connection = null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        PreparedStatement pstmtQry = null;
        PreparedStatement pstmtCount = null;

        try
        {
            connection = Database.getRDBConnection();
            query = new StringBuffer("SELECT FROM_UNIXTIME(tokencreationon) AS creationtime ,rm.memberid,rm.registration_id,rm.registration_token,rm.firstName,rm.lastName,rm.email,rm.isActive,tm.merchant_orderid,tm.token,tm.bank_accountid,tm.generatedBy,FROM_UNIXTIME(tm.tokencreationon) AS tokencreationon,tm.tokenid FROM registration_master AS rm, token_master AS tm WHERE rm.tokenid=tm.tokenid AND rm.isActive='Y' AND tm.isactive='Y'");
            countQuery = new StringBuffer("SELECT count(*) FROM registration_master AS rm, token_master AS tm WHERE rm.tokenid=tm.tokenid AND rm.isActive='Y' AND tm.isactive='Y'");

            if(functions.isValueNull(memberId))
            {
                query.append(" and memberid='" + ESAPI.encoder().encodeForSQL(me, memberId) + "'");
                countQuery.append(" and memberid='" + ESAPI.encoder().encodeForSQL(me, memberId) + "'");
            }
            if (functions.isValueNull(description))
            {
                query.append(" and merchant_orderid='" + ESAPI.encoder().encodeForSQL(me, description) + "'");
                countQuery.append(" and merchant_orderid='" + ESAPI.encoder().encodeForSQL(me, description) + "'");
            }
            if (functions.isValueNull(firstName))
            {
                query.append(" and firstname='" + ESAPI.encoder().encodeForSQL(me, firstName) + "'");
                countQuery.append(" and firstname='" + ESAPI.encoder().encodeForSQL(me, firstName) + "'");
            }
            if (functions.isValueNull(lastName))
            {
                query.append(" and lastname='" + ESAPI.encoder().encodeForSQL(me, lastName) + "'");
                countQuery.append(" and lastname='" + ESAPI.encoder().encodeForSQL(me, lastName) + "'");
            }
            if (functions.isValueNull(email))
            {
                query.append(" and email='" + ESAPI.encoder().encodeForSQL(me, email) + "'");
                countQuery.append(" and email='" + ESAPI.encoder().encodeForSQL(me, email) + "'");
            }
            if (functions.isValueNull(fdtstamp))
            {
                logger.debug("fdate=="+fdtstamp);
                query.append(" and tokencreation_date>='" + fdtstamp + "'");
                countQuery.append(" and tokencreation_date>='" + fdtstamp + "'");
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and tokencreation_date<='" + tdtstamp + "'");
                countQuery.append(" and tokencreation_date<='" + tdtstamp + "'");
            }

            query.append(" ORDER BY FROM_UNIXTIME(tokencreationon) DESC limit " + paginationVO.getStart() + "," + paginationVO.getEnd());
            countQuery.append(" ORDER BY FROM_UNIXTIME(tokencreationon) DESC");
            logger.debug("Date from Registration History------>"+query);
            logger.debug("record query=="+countQuery);
            ResultSet rsQry = Database.executeQuery(query.toString(), connection);
            ResultSet rsCount = Database.executeQuery(countQuery.toString(), connection);

            pstmtQry = connection.prepareStatement(query.toString());
            rsQry=pstmtQry.executeQuery();

            pstmtCount = connection.prepareStatement(countQuery.toString());
            rsCount = pstmtCount.executeQuery();
            logger.debug("pstmtCount--->"+pstmtCount);

            while(rsQry.next())
            {
                tokenDetailsVO = new TokenDetailsVO();
                addressDetailsVO =  new GenericAddressDetailsVO();
                logger.debug("inside if of resultsetdata==");
                tokenDetailsVO.setRegistrationId(rsQry.getString("registration_id"));
                tokenDetailsVO.setRegistrationToken(rsQry.getString("registration_token"));
                addressDetailsVO.setFirstname(rsQry.getString("firstname"));
                addressDetailsVO.setLastname(rsQry.getString("lastname"));
                addressDetailsVO.setEmail(rsQry.getString("email"));
                tokenDetailsVO.setIsActive(rsQry.getString("isActive"));
                tokenDetailsVO.setDescription(rsQry.getString("merchant_orderid"));
                tokenDetailsVO.setToken(rsQry.getString("token"));
                tokenDetailsVO.setGeneratedBy(rsQry.getString("generatedBy"));
                tokenDetailsVO.setCreationOn(rsQry.getString("creationtime"));
                tokenDetailsVO.setTokenId(rsQry.getString("tokenid"));
                tokenDetailsVO.setBankAccountId(rsQry.getString("bank_accountid"));
                tokenDetailsVO.setAddressDetailsVO(addressDetailsVO);

                if(rsCount.next())
                {
                    paginationVO.setTotalRecords(rsCount.getInt(1));
                    tokenDetailsVO.setPaginationVO(paginationVO);
                }
                tokenDetailsVOList.add(tokenDetailsVO);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getMerchantRegistrationList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getMerchantRegistrationList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return tokenDetailsVOList;
    }

    public List<TokenDetailsVO> getMerchantActiveTokens(String memberId) throws PZDBViolationException
    {
        List<TokenDetailsVO> tokenDetailsVOList = new ArrayList();
        TokenDetailsVO tokenDetailsVO = null;
        StringBuilder query = null;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            query = new StringBuilder("select from_unixtime(tokencreationon) as creationtime,tokenid,tokenvaliddays from token_master as tm join members as m on tm.toid=m.memberid and tm.toid=?");
            pstmt = connection.prepareStatement(query.toString());
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                tokenDetailsVO = new TokenDetailsVO();
                tokenDetailsVO.setCreationOn(rs.getString("creationtime"));
                tokenDetailsVO.setTokenId(rs.getString("tokenid"));
                tokenDetailsVO.setTokenValidDays(rs.getInt("tokenvaliddays"));
                tokenDetailsVOList.add(tokenDetailsVO);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getMerchantActiveTokens()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getMerchantActiveTokens()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return tokenDetailsVOList;
    }

    public String manageTokenTransactionDetails(TokenTransactionDetailsVO tokenTransactionDetailsVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = "";
        String status = "failed";
        try
        {
            conn = Database.getConnection();
            query = "insert into token_transaction_details(id,toid,trackingid,amount,tokenid,tokentranstime)values(null,?,?,?,?,unix_timestamp(now()))";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, tokenTransactionDetailsVO.getToid());
            pstmt.setString(2, tokenTransactionDetailsVO.getTrackingid());
            pstmt.setString(3, tokenTransactionDetailsVO.getAmount());
            pstmt.setString(4, tokenTransactionDetailsVO.getTokenId());
            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                status = "success";
            }
        }
        catch (SystemError se)
        {
            status = "failed";
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "manageTokenTransactionDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            status = "failed";
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "manageTokenTransactionDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public String manageRegistrationTransactionDetails(TokenTransactionDetailsVO tokenTransactionDetailsVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = "";
        String status = "failed";
        try
        {
            conn = Database.getConnection();
            query = "insert into registration_transaction_details(id,toid,trackingid,amount,registrationid,tokentranstime)values(null,?,?,?,?,unix_timestamp(now()))";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, tokenTransactionDetailsVO.getToid());
            pstmt.setString(2, tokenTransactionDetailsVO.getTrackingid());
            pstmt.setString(3, tokenTransactionDetailsVO.getAmount());
            pstmt.setString(4, tokenTransactionDetailsVO.getRegistrationId());
            int k = pstmt.executeUpdate();
            if(k == 1)
            {
                status = "success";
            }
        }
        catch (SystemError se)
        {
            status = "failed";
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "manageTokenTransactionDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            status = "failed";
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "manageTokenTransactionDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public String doTokenInactive(String tokenId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = "";
        String status = "failed";
        try
        {
            conn = Database.getConnection();
            query = "update token_master set isactive='N' where tokenid=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, tokenId);
            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                status = "success";
            }
        }
        catch (SystemError se)
        {
            status = "failed";
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "makeTokenInactive()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            status = "failed";
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "makeTokenInactive()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public TokenDetailsVO getRegisteredTokenDetails(String memberId, String token, String terminalId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer query = new StringBuffer();
        TokenDetailsVO tokenDetailsVO = null;
        try
        {
            conn = Database.getRDBConnection();
            query.append("SELECT terminalid,trackingid FROM registration_master WHERE toid=? AND registration_token=?");
            if (functions.isValueNull(terminalId))
            {
                query.append(" AND terminalid = ").append( terminalId);
            }
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, memberId);
            pstmt.setString(2, token);
            rs = pstmt.executeQuery();
            logger.debug("query---" + pstmt);
            logger.debug("tokenDetailsVO before rs---->" + tokenDetailsVO);
            if (rs.next())
            {
                tokenDetailsVO = new TokenDetailsVO();
                logger.debug("tokenDetailsVO after rs---->" + tokenDetailsVO);

                tokenDetailsVO.setTerminalId(rs.getString("terminalid"));
                tokenDetailsVO.setTrackingId(rs.getString("trackingid"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "makeTokenInactive()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "makeTokenInactive()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVO;
    }

    public String doMerchantRegisteredTokenInactive(String memberId, String token) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        StringBuffer query = new StringBuffer();
        String status = "failed";
        try
        {
            conn = Database.getConnection();
            query.append("UPDATE registration_master SET isactive='N', tokeninactive_date = (UNIX_TIMESTAMP(NOW())) where registration_token=?");
            if (functions.isValueNull(memberId))
            {
                query.append("and memberid=? ");
            }
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, token);
            if (functions.isValueNull(memberId))
            {
                pstmt.setString(2, memberId);
            }
                logger.debug("query---" + pstmt);
            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                status = "success";
            }
        }
        catch (SQLException e)
        {
            status = "failed";
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "makeTokenInactive()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            status = "failed";
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "makeTokenInactive()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public String doPartnerRegisteredTokenInactive(String partnerId, String token) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        StringBuffer query = new StringBuffer();
        String status = "failed";
        try
        {
            conn = Database.getConnection();
            query.append("UPDATE registration_master SET isactive='N', tokeninactive_date = (UNIX_TIMESTAMP(NOW())) WHERE partnerid=? AND registration_token=?");
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, partnerId);
            pstmt.setString(2, token);
            logger.debug("query---" + pstmt);
            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                status = "success";
            }
        }
        catch (SQLException e)
        {
            status = "failed";
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "makeTokenInactive()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            status = "failed";
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "makeTokenInactive()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public String doRestTokenDelete(String token) throws PZDBViolationException
    {
        Connection connection = null;
        String status = "failed";
        PreparedStatement preparedStatement=null;
        try
        {
            connection = Database.getConnection();
            String query = "DELETE FROM token_master WHERE token=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, token);
            int k = preparedStatement.executeUpdate();
            if (k == 1)
            {
                status = "success";
            }

        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "doRestTokenDelete()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "doRestTokenDelete()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return status;
    }

    public String doTokenDeleteWithAccount(String token) throws PZDBViolationException
    {
        Connection connection = null;
        String status = "failed";
        PreparedStatement preparedStatement=null;
        try
        {
            connection = Database.getConnection();
            String query = "DELETE t,b FROM token_master t JOIN bankaccount_details b ON t.bank_accountid=b.bank_accountid WHERE t.token=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, token);
            int k = preparedStatement.executeUpdate();
            if (k > 0)
            {
                status = "success";
            }

        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "doRestTokenDelete()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "doRestTokenDelete()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return status;
    }

    public String updateTokenMaster(TokenTransactionDetailsVO tokenTransactionDetailsVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = "";
        String status = "failed";
        try
        {
            conn = Database.getConnection();
            query = "update token_master set trackingid=?,merchant_orderid=? where tokenid=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, tokenTransactionDetailsVO.getTrackingid());
            pstmt.setString(2, tokenTransactionDetailsVO.getDescription());
            pstmt.setString(3, tokenTransactionDetailsVO.getTokenId());
            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                status = "success";
            }
        }
        catch (SystemError se)
        {
            status = "failed";
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "updateTokenMaster()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            status = "failed";
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "updateTokenMaster()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public String createTokenByTrackingId(String trackingId) throws PZDBViolationException
    {
        return "";
    }

    public String createTokenByTrackingId(String trackingId, TransactionDetailsVO transactionDetailsVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = "";
        String token = "";
        String successToken = "";

        try
        {
            conn = Database.getConnection();
            query = "insert into token_master(tokenid,token,toid,trackingid,merchant_orderid,cnum,expiry_date,cardholder_firstname,cardholder_lastname,cardholderemail,terminalid,country,city,state,street,zip,telnocc,telno,birthdate,language,isactive,tokencreationon,cardholderid)values(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?)";
            pstmt = conn.prepareStatement(query);
            token = TokenManager.generateToken();
            pstmt.setString(1, token);
            pstmt.setString(2, transactionDetailsVO.getToid());
            pstmt.setString(3, trackingId);
            pstmt.setString(4, transactionDetailsVO.getDescription());
            pstmt.setString(5, transactionDetailsVO.getCcnum());
            pstmt.setString(6, transactionDetailsVO.getExpdate());
            pstmt.setString(7, transactionDetailsVO.getFirstName());
            pstmt.setString(8, transactionDetailsVO.getLastName());
            pstmt.setString(9, transactionDetailsVO.getEmailaddr());
            pstmt.setString(10, "0");
            pstmt.setString(11, transactionDetailsVO.getCountry());
            pstmt.setString(12, transactionDetailsVO.getCity());
            pstmt.setString(13, transactionDetailsVO.getState());
            pstmt.setString(14, transactionDetailsVO.getStreet());
            pstmt.setString(15, transactionDetailsVO.getZip());
            pstmt.setString(16, transactionDetailsVO.getTelcc());
            pstmt.setString(17, transactionDetailsVO.getTelno());
            pstmt.setString(18, transactionDetailsVO.getBirthDate());
            pstmt.setString(19, transactionDetailsVO.getLanguage());
            pstmt.setString(20, "Y");
            pstmt.setString(21, "0");
            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                successToken = token;
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createTokenByTrackingId()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createTokenByTrackingId()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return successToken;
    }


    //SEPA MANDATE

    /**
     * This to check whether the sepa is active and available in the system
     *
     * @param sepaMandateToken
     */
    public boolean isMandateAvailableAndActive(String sepaMandateToken) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String query = "SELECT isactive from sepa_token_master where isactive='Y' AND mandate=?";
        try
        {
            con = Database.getRDBConnection();
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, sepaMandateToken);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                return true;
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError::::::", systemError);
            PZExceptionHandler.raiseDBViolationException(TokenDAO.class.getName(), "isMandateAvailableAndActive()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::", e);
            PZExceptionHandler.raiseDBViolationException(TokenDAO.class.getName(), "isMandateAvailableAndActive()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return false;
    }


    public String getMandateIDFromMandate(String sepaMandateToken) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = "SELECT id from sepa_token_master where isactive='Y' AND mandate=?";

        try
        {
            con = Database.getRDBConnection();
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, sepaMandateToken);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
            {
                return resultSet.getString("id");
            }

        }
        catch (SystemError systemError)
        {
            logger.error("SystemError::::::", systemError);
            PZExceptionHandler.raiseDBViolationException(TokenDAO.class.getName(), "isMandateAvailableAndActive()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::", e);
            PZExceptionHandler.raiseDBViolationException(TokenDAO.class.getName(), "isMandateAvailableAndActive()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return null;
    }

    //
    public String insertMandateDetails(String sepaMandateId, String toid, String trackingid, String mandateURL, String revokeMandateURL, boolean isRecurring) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        int resultSet = 0;

        String query = "insert into sepa_token_master(mandate,toid,trackingid,mandateURL,revokeMandateURL,isRecurring,mandatecreationtime) VALUES(?,?,?,?,?,?,UNIX_TIMESTAMP())";//TODO CHANGE

        try
        {
            con = Database.getConnection();
            preparedStatement = con.prepareStatement(query);

            preparedStatement.setString(1, sepaMandateId);
            preparedStatement.setString(2, toid);
            preparedStatement.setString(3, trackingid);
            if (functions.isValueNull(mandateURL))
            {
                preparedStatement.setString(4, mandateURL);
            }
            else
            {
                preparedStatement.setNull(4, Types.VARCHAR);
            }
            if (functions.isValueNull(mandateURL))
            {
                preparedStatement.setString(5, revokeMandateURL);
            }
            else
            {
                preparedStatement.setNull(5, Types.VARCHAR);
            }
            preparedStatement.setString(6, isRecurring ? "Y" : "N");
            resultSet = preparedStatement.executeUpdate();
            if (resultSet > 0)
            {
                ResultSet resultSet1 = preparedStatement.getGeneratedKeys();
                if (resultSet1.next())
                {
                    return resultSet1.getString(1);
                }
            }

        }
        catch (SystemError systemError)
        {
            logger.error("SystemError::::::", systemError);
            PZExceptionHandler.raiseDBViolationException(TokenDAO.class.getName(), "insertMandateDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(TokenDAO.class.getName(), "insertMandateDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return null;
    }

    //
    public boolean insertMandateTransactionHistory(String sepaMandateId, String trackingid) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        int resultSet = 0;

        String query = "insert into sepa_transaction_history(mandateId,trackingId) VALUES(?,?)";//TODO CHANGE

        try
        {
            con = Database.getConnection();
            preparedStatement = con.prepareStatement(query);

            preparedStatement.setString(1, sepaMandateId);
            preparedStatement.setString(2, trackingid);

            resultSet = preparedStatement.executeUpdate();

            if (resultSet > 0)
            {
                return true;
            }

        }
        catch (SystemError systemError)
        {
            logger.error("SystemError::::::", systemError);
            PZExceptionHandler.raiseDBViolationException(TokenDAO.class.getName(), "insertMandateDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::", e);
            PZExceptionHandler.raiseDBViolationException(TokenDAO.class.getName(), "insertMandateDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return false;
    }

    //
    public boolean updateMandateDetails(String sepaMandateId, boolean isActive) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        int resultSet = 0;

        String query = "UPDATE sepa_token_master set isactive=? where mandate=?";//TODO CHANGE

        try
        {
            con = Database.getConnection();
            preparedStatement = con.prepareStatement(query);

            preparedStatement.setString(1, isActive ? "Y" : "N");
            preparedStatement.setString(2, sepaMandateId);

            resultSet = preparedStatement.executeUpdate();

            if (resultSet > 0)
            {
                return true;
            }

        }
        catch (SystemError systemError)
        {
            logger.error("SystemError::::::", systemError);
            PZExceptionHandler.raiseDBViolationException(TokenDAO.class.getName(), "updateMandateDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::", e);
            PZExceptionHandler.raiseDBViolationException(TokenDAO.class.getName(), "updateMandateDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return false;
    }

    public void updateTrackingIdInTokenMaster(String token, String trackingid, String toid)
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int resultSet = 0;

        try
        {
            connection = Database.getConnection();
            String query = "UPDATE token_master SET trackingid=? WHERE token=? AND toid = ? AND isActive = 'Y'";
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, trackingid);
            preparedStatement.setString(2, token);
            preparedStatement.setString(3, toid);

            resultSet = preparedStatement.executeUpdate();
        }
        catch (SystemError e)
        {
            logger.error("SysytemError", e);
        }
        catch (SQLException e)
        {
            logger.error("SQLException", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    public TokenDetailsVO getAccountDetails(TokenDetailsVO tokenDetailsVO, String memberId, String token) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";
        CommCardDetailsVO commCardDetailsVO = tokenDetailsVO.getCommCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = tokenDetailsVO.getAddressDetailsVO();
        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT t.partnerid,t.generatedBy,b.bic,b.iban,b.accountType,b.accountNumber,b.routingNumber,b.holder,b.country,b.city,b.state,b.street,b.zip,b.telno FROM token_master AS t, registration_master AS rt, bankaccount_details AS b WHERE t.bank_accountid=b.bank_accountid AND rt.memberid=? AND t.tokenid = rt.tokenid AND rt.registration_token=?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, memberId);
            pstmt.setString(2, token);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                String holder = rs.getString("holder");
                String[] holder1 = holder.split("\\s", 2);

                if (holder1.length >= 2)
                {
                    addressDetailsVO.setFirstname(holder1[0]);
                    addressDetailsVO.setLastname(holder1[1]);
                }
                else
                {
                    addressDetailsVO.setFirstname(rs.getString("holder"));
                }

                addressDetailsVO.setCountry(rs.getString("country"));
                addressDetailsVO.setCity(rs.getString("city"));
                addressDetailsVO.setState(rs.getString("state"));
                addressDetailsVO.setStreet(rs.getString("street"));
                addressDetailsVO.setZipCode(rs.getString("zip"));
                addressDetailsVO.setPhone(rs.getString("telno"));

                if (functions.isValueNull(rs.getString("bic")))
                {
                    commCardDetailsVO.setBIC(rs.getString("bic"));
                    commCardDetailsVO.setIBAN(rs.getString("iban"));
                }
                else
                {
                    commCardDetailsVO.setAccountType(rs.getString("accountType"));
                    commCardDetailsVO.setAccountNumber(rs.getString("accountnumber"));
                    commCardDetailsVO.setRoutingNumber(rs.getString("routingNumber"));
                }
                commCardDetailsVO.setCardHolderName(rs.getString("holder"));

                tokenDetailsVO.setParetnerId(rs.getString("partnerid"));

                tokenDetailsVO.setCommCardDetailsVO(commCardDetailsVO);
                tokenDetailsVO.setAddressDetailsVO(addressDetailsVO);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getTokenDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getTokenDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVO;
    }

    public TokenDetailsVO getAccountDetailsForPaymitco(TokenDetailsVO tokenDetailsVO, String memberId, String token) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";
        CommCardDetailsVO commCardDetailsVO = tokenDetailsVO.getCommCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = tokenDetailsVO.getAddressDetailsVO();
        ReserveField2VO reserveField2VO = tokenDetailsVO.getReserveField2VO();
        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT t.partnerid,t.generatedBy,b.accountNumber,b.accountType,b.routingNumber,b.holder,b.country,b.city,b.state,b.street,b.zip,b.telno, rt.toid FROM token_master AS t, registration_master AS rt, bankaccount_details AS b WHERE t.bank_accountid=b.bank_accountid AND rt.toid= ? AND rt.toid=t.toid AND rt.registration_token= ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, memberId);
            pstmt.setString(2, token);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                String holder = rs.getString("holder");
                String[] holder1 = holder.split("\\s", 2);

                if (holder1.length >= 2)
                {
                    addressDetailsVO.setFirstname(holder1[0]);
                    addressDetailsVO.setLastname(holder1[1]);
                }
                else
                {
                    addressDetailsVO.setFirstname(rs.getString("holder"));
                }

                addressDetailsVO.setCountry(rs.getString("country"));
                addressDetailsVO.setCity(rs.getString("city"));
                addressDetailsVO.setState(rs.getString("state"));
                addressDetailsVO.setStreet(rs.getString("street"));
                addressDetailsVO.setZipCode(rs.getString("zip"));
                addressDetailsVO.setPhone(rs.getString("telno"));

                reserveField2VO.setAccountNumber(rs.getString("accountNumber"));
                reserveField2VO.setAccountType(rs.getString("accountType"));
                reserveField2VO.setRoutingNumber(rs.getString("routingNumber"));
                commCardDetailsVO.setCardHolderName(rs.getString("holder"));

                tokenDetailsVO.setParetnerId(rs.getString("partnerid"));

                tokenDetailsVO.setCommCardDetailsVO(commCardDetailsVO);
                tokenDetailsVO.setAddressDetailsVO(addressDetailsVO);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getTokenDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getTokenDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVO;
    }

    public void updateTrackingIdForRegistrationTokenByMerchant(String token, String trackingid, String toid) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try
        {
            connection = Database.getConnection();
            String query = "UPDATE registration_member_mapping AS r JOIN registration_master AS rm ON r.registration_tokenid = registration_id SET r.tracking_id=? WHERE rm.registration_token=? AND r.toid=?";
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, trackingid);
            preparedStatement.setString(2, token);
            preparedStatement.setString(3, toid);
            logger.debug("Update Qry-->" + preparedStatement);
            preparedStatement.executeUpdate();
        }
        catch (SystemError e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "updateTrackingIdForRegistrationToken()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "updateTrackingIdForRegistrationToken()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
    }

    public void insertTrackingIdForRegistrationTokenByMerchant(CommonValidatorVO commonValidatorVO, TokenDetailsVO tokenDetailsVO, String trackingId) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try
        {
            connection = Database.getConnection();
            String query = "INSERT INTO `registration_transaction_details`(toid,trackingid,amount,registrationid,tokentranstime) VALUES(?,?,?,?,UNIX_TIMESTAMP(NOW()))";
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, commonValidatorVO.getMerchantDetailsVO().getMemberId());
            preparedStatement.setString(2, trackingId);
            preparedStatement.setString(3, commonValidatorVO.getTransDetailsVO().getAmount());
            preparedStatement.setString(4, tokenDetailsVO.getTokenId());
            logger.debug("Update Qry-->" + preparedStatement);
            preparedStatement.executeUpdate();
        }
        catch (SystemError e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "updateTrackingIdForRegistrationToken()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "updateTrackingIdForRegistrationToken()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
    }


    public void updateTrackingIdForRegistrationTokenByPartner(String token, String trackingid, String partnerId) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try
        {
            connection = Database.getConnection();
            String query = "UPDATE registration_member_mapping AS r JOIN registration_master AS rm ON r.registration_tokenid = rm.registration_id SET r.tracking_id=? WHERE rm.registration_token=? AND rm.partnerid=?";
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, trackingid);
            preparedStatement.setString(2, token);
            preparedStatement.setString(3, partnerId);
            logger.debug("Update Qry-->" + preparedStatement);
            preparedStatement.executeUpdate();
        }
        catch (SystemError e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "updateTrackingIdForRegistrationTokenByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "updateTrackingIdForRegistrationTokenByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
    }

    public TokenDetailsVO createNewTokenRegistrationByMember(TokenRequestVO tokenRequestVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        TokenManager tokenManager = new TokenManager();
        TokenDetailsVO tokenDetailsVO = new TokenDetailsVO();
        String reg_token = "";
        try
        {
            conn = Database.getConnection();
            StringBuffer qry = new StringBuffer("INSERT INTO registration_master(registration_id,registration_token,tokenid,paymodeid,cardtypeid,currency,terminalid,isActive,partnerid,customerid,memberid,tokencreation_date,country,city,state,street,zip,telno,firstname,lastname,telnocc,email,birthdate,language,generatedBy,notificationUrl) VALUES(null,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pstmt = conn.prepareStatement(qry.toString());
            reg_token = TokenManager.generateTokenRegestration();
            pstmt.setString(1, reg_token);
            pstmt.setString(2, tokenRequestVO.getTokenId());
            pstmt.setString(3, tokenRequestVO.getPaymentType());
            pstmt.setString(4, tokenRequestVO.getCardType());
            pstmt.setString(5, tokenRequestVO.getTransDetailsVO().getCurrency());
            pstmt.setString(6, tokenRequestVO.getTerminalId());
            pstmt.setString(7, tokenRequestVO.getIsActive());
            pstmt.setString(8, tokenRequestVO.getPartnerId());
            pstmt.setString(9, tokenRequestVO.getCustomerId());
            pstmt.setString(10, tokenRequestVO.getMemberId());
            pstmt.setString(11, tokenRequestVO.getAddressDetailsVO().getCountry());
            pstmt.setString(12, tokenRequestVO.getAddressDetailsVO().getCity());
            pstmt.setString(13, tokenRequestVO.getAddressDetailsVO().getState());
            pstmt.setString(14, tokenRequestVO.getAddressDetailsVO().getStreet());
            pstmt.setString(15, tokenRequestVO.getAddressDetailsVO().getZipCode());
            pstmt.setString(16, tokenRequestVO.getAddressDetailsVO().getPhone());
            pstmt.setString(17, tokenRequestVO.getAddressDetailsVO().getFirstname());
            pstmt.setString(18, tokenRequestVO.getAddressDetailsVO().getLastname());
            if(functions.isValueNull(tokenRequestVO.getAddressDetailsVO().getTelnocc()))
                pstmt.setString(19, tokenRequestVO.getAddressDetailsVO().getTelnocc());
            else
                pstmt.setString(19, null);
            pstmt.setString(20, tokenRequestVO.getAddressDetailsVO().getEmail());
            pstmt.setString(21, tokenRequestVO.getAddressDetailsVO().getBirthdate());
            pstmt.setString(22, tokenRequestVO.getAddressDetailsVO().getLanguage());
            pstmt.setString(23, tokenRequestVO.getRegistrationGeneratedBy());
            pstmt.setString(24, tokenRequestVO.getNotificationUrl());
            pstmt.executeUpdate();
            logger.error("Query:::"+pstmt);
            rs = pstmt.getGeneratedKeys();
            if(rs.next())
            {
                tokenDetailsVO.setRegistrationId(rs.getString(1));
                tokenDetailsVO.setRegistrationToken(reg_token);
                tokenDetailsVO.setStatus("success");
            }

            /*if("Y".equalsIgnoreCase(tokenRequestVO.getMerchantDetailsVO().getIsCvvStore()))
            {
                String cvv = PzEncryptor.encryptCVV(tokenRequestVO.getCardDetailsVO().getcVV());
                StringBuffer stringBuffer = new StringBuffer("Insert into token_cvv(registrationId,cvv) values(?,?)");
                PreparedStatement preparedStatement = conn.prepareStatement(stringBuffer.toString());
                preparedStatement.setString(1, tokenDetailsVO.getRegistrationId());
                preparedStatement.setString(2, cvv);
                int k = preparedStatement.executeUpdate();
                if (k > 0)
                {
                    tokenDetailsVO.setStatus("success");
                }
            }*/
            logger.error("query for token----" + pstmt);
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "crreateNewTokenRegistration()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SystemError::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "crreateNewTokenRegistration()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVO;
    }

    public TokenDetailsVO createNewTokenRegistrationByPartner(TokenRequestVO tokenRequestVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        TokenManager tokenManager = new TokenManager();
        TokenDetailsVO tokenDetailsVO = new TokenDetailsVO();
        String reg_token = "";
        try
        {
            conn = Database.getConnection();
            StringBuffer qry = new StringBuffer("INSERT INTO registration_master(registration_token,tokenid,paymodeid,cardtypeid,currency,terminalid,isActive,partnerid,customerid,tokencreation_date,country,city,state,street,zip,telno,firstname,lastname,telnocc,email,birthdate,language,generatedBy,notificationUrl) VALUES(?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pstmt = conn.prepareStatement(qry.toString());
            reg_token = TokenManager.generateTokenRegestration();
            pstmt.setString(1, reg_token);
            pstmt.setString(2, tokenRequestVO.getTokenId());
            pstmt.setString(3, tokenRequestVO.getPaymentType());
            pstmt.setString(4, tokenRequestVO.getCardType());
            pstmt.setString(5, tokenRequestVO.getTransDetailsVO().getCurrency());
            pstmt.setString(6, tokenRequestVO.getTerminalId());
            pstmt.setString(7, "Y");
            pstmt.setString(8, tokenRequestVO.getPartnerId());
            pstmt.setString(9, tokenRequestVO.getCustomerId());
            pstmt.setString(10, tokenRequestVO.getAddressDetailsVO().getCountry());
            pstmt.setString(11, tokenRequestVO.getAddressDetailsVO().getCity());
            pstmt.setString(12, tokenRequestVO.getAddressDetailsVO().getState());
            pstmt.setString(13, tokenRequestVO.getAddressDetailsVO().getStreet());
            pstmt.setString(14, tokenRequestVO.getAddressDetailsVO().getZipCode());
            pstmt.setString(15, tokenRequestVO.getAddressDetailsVO().getPhone());
            pstmt.setString(16, tokenRequestVO.getAddressDetailsVO().getFirstname());
            pstmt.setString(17, tokenRequestVO.getAddressDetailsVO().getLastname());
            pstmt.setString(18, tokenRequestVO.getAddressDetailsVO().getTelnocc());
            pstmt.setString(19, tokenRequestVO.getAddressDetailsVO().getEmail());
            pstmt.setString(20, tokenRequestVO.getAddressDetailsVO().getBirthdate());
            pstmt.setString(21, tokenRequestVO.getAddressDetailsVO().getLanguage());
            pstmt.setString(22, tokenRequestVO.getRegistrationGeneratedBy());
            pstmt.setString(23, tokenRequestVO.getNotificationUrl());
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if(rs.next())
            {
                tokenDetailsVO.setRegistrationId(rs.getString(1));
                tokenDetailsVO.setRegistrationToken(reg_token);
                tokenDetailsVO.setStatus("success");
            }
            logger.debug("query for token by partner----"+pstmt);
            String cvv= PzEncryptor.encryptCVV(tokenRequestVO.getCardDetailsVO().getcVV());
            /*StringBuffer stringBuffer=new StringBuffer("Insert into token_cvv(registrationId,cvv) values(?,?)");
            PreparedStatement preparedStatement=conn.prepareStatement(stringBuffer.toString());
            preparedStatement.setString(1,tokenDetailsVO.getRegistrationId());
            preparedStatement.setString(2, cvv);
            int k=preparedStatement.executeUpdate();
            if(k>0){
                tokenDetailsVO.setStatus("success");
            }
            logger.debug("query for token cvv----"+preparedStatement);*/
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "crreateNewTokenRegistration()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SystemError::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "crreateNewTokenRegistration()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVO;
    }

    public String newTokenRegistrationMemberMappingEntry(String registrationTokenId, String memberId, String trackingId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String status = null;
        try
        {
            conn = Database.getConnection();
            StringBuffer qry = new StringBuffer("INSERT INTO registration_member_mapping (id, registration_tokenid, toid, tracking_id) VALUES(null,?,?,?)");
            pstmt = conn.prepareStatement(qry.toString());
            pstmt.setString(1, registrationTokenId);
            pstmt.setString(2, memberId);
            pstmt.setString(3, trackingId);
            logger.debug("qry--->"+pstmt);
            int i = pstmt.executeUpdate();
            if (i == 1)
            {
                status = "success";
            }
        }
        catch (SystemError se)
        {
            status = "fail";
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "crreateNewTokenRegistration()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            status = "fail";
            logger.error("SystemError::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "crreateNewTokenRegistration()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public String createTokenForRegistrationByMember(TokenRequestVO tokenRequestVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        StringBuffer query = new StringBuffer();
        GenericCardDetailsVO commCardDetailsVO = tokenRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = tokenRequestVO.getAddressDetailsVO();
        String newToken = null;
        String tokenId = null;
        try
        {
            conn = Database.getConnection();
            query.append("insert into token_master(tokenid,token,toid,cnum,expiry_date,cardholder_firstname,cardholder_lastname,cardholderemail,country,city,state,street,zip,telnocc,telno,birthdate,language,isactive,tokencreationon,cardholderid,partnerid,generatedBy,bank_accountid)values(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?)");
            pstmt = conn.prepareStatement(query.toString());
            newToken = TokenManager.generateToken();
            pstmt.setString(1, newToken);
            if (functions.isValueNull(tokenRequestVO.getMemberId()))
            {
                pstmt.setString(2, tokenRequestVO.getMemberId());
            }
            else
            {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, PzEncryptor.encryptPAN(commCardDetailsVO.getCardNum()));
            pstmt.setString(4, PzEncryptor.encryptExpiryDate(commCardDetailsVO.getExpMonth() + "/" + commCardDetailsVO.getExpYear()));
            pstmt.setString(5, addressDetailsVO.getFirstname());
            pstmt.setString(6, addressDetailsVO.getLastname());
            if (addressDetailsVO.getEmail() == null)
            {
                pstmt.setString(7, "");
            }
            else
            {
                pstmt.setString(7, addressDetailsVO.getEmail());
            }
            pstmt.setString(8, addressDetailsVO.getCountry());
            pstmt.setString(9, addressDetailsVO.getCity());
            pstmt.setString(10, addressDetailsVO.getState());
            pstmt.setString(11, addressDetailsVO.getStreet());
            pstmt.setString(12, addressDetailsVO.getZipCode());
            pstmt.setString(13, addressDetailsVO.getTelnocc());
            pstmt.setString(14, addressDetailsVO.getPhone());
            pstmt.setString(15, addressDetailsVO.getBirthdate());
            pstmt.setString(16, addressDetailsVO.getLanguage());
            pstmt.setString(17, tokenRequestVO.getIsActive());
            pstmt.setString(18, tokenRequestVO.getCardholderId());
            pstmt.setString(19, tokenRequestVO.getPartnerId());
            pstmt.setString(20, tokenRequestVO.getGeneratedBy());
            pstmt.setString(21, tokenRequestVO.getBankAccountId());

            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if(rs.next())
            {
                tokenId = rs.getString(1);
            }

            logger.error("query for card registration---" + pstmt);
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createTokenWithRegistration()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createTokenWithRegistration()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return tokenId;
    }

    public boolean validateTokenAndMember(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        String query = "";
        boolean verifyToken = false;
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        try
        {
            connection = Database.getRDBConnection();
            query = "SELECT toid FROM registration_master WHERE registration_token = ? and toid=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, commonValidatorVO.getToken());
            preparedStatement.setString(2, merchantDetailsVO.getMemberId());
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                verifyToken = false;
            }
            else
            {
                verifyToken = true;
            }

        }
        catch (SQLException e)
        {
            logger.error("SystemError::::::", e);
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "validateTokenAndMember()", null, "common", "System error Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError e)
        {
            logger.error("SystemError::::::", e);
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "validateTokenAndMember()", null, "common", "System error Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return verifyToken;
    }

    public TokenDetailsVO getRegisteredTokenDetailsByMerchant(String memberId, String token, CommonValidatorVO commonValidatorVO, TokenDetailsVO tokenDetailsVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer query = new StringBuffer();
        CommCardDetailsVO commCardDetailsVO = null;
        GenericAddressDetailsVO addressDetailsVO = commonValidatorVO.getAddressDetailsVO();

        try
        {
            conn = Database.getRDBConnection();
            query.append("SELECT rt.registration_id,rt.registration_token,rt.memberid,rt.terminalid,rt.paymodeid,rt.cardtypeid,rt.isactive AS isactive_reg,FROM_UNIXTIME(rt.tokencreation_date) AS creationtime,tm.cnum,tm.expiry_date,rt.firstname,rt.lastname,rt.email,rt.country,rt.city,rt.state,rt.street,rt.zip,rt.telnocc,\n" +
                    "rt.telno,rt.birthdate,rt.language,tm.bank_accountid,tm.isactive AS isactive_token,m.tokenvaliddays,rt.currency,tm.cardholder_firstname,tm.cardholder_lastname,rt.notificationUrl,rt.customerid FROM registration_master AS rt, members AS m, token_master AS tm WHERE rt.registration_token=BINARY('"+token+"') AND rt.tokenid=tm.tokenid AND rt.isactive='Y'");

            if (functions.isValueNull(memberId))
            {
                query.append(" AND rt.memberid="+memberId + " AND rt.memberid=tm.toid AND tm.toid = m.memberid");
            }
            pstmt = conn.prepareStatement(String.valueOf(query));
            //pstmt.setString(1, memberId);
            //pstmt.setString(2, token);
            logger.debug("qry-->" + pstmt);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                commCardDetailsVO = new CommCardDetailsVO();

                if((!functions.isValueNull(addressDetailsVO.getCity())) || (!functions.isValueNull(addressDetailsVO.getCountry())) || (!functions.isValueNull(addressDetailsVO.getZipCode())) || (!functions.isValueNull(addressDetailsVO.getState())) || (!functions.isValueNull(addressDetailsVO.getStreet())))
                {
                    addressDetailsVO.setCountry(rs.getString("country"));
                    addressDetailsVO.setCity(rs.getString("city"));
                    addressDetailsVO.setState(rs.getString("state"));
                    addressDetailsVO.setStreet(rs.getString("street"));
                    addressDetailsVO.setZipCode(rs.getString("zip"));
                    addressDetailsVO.setBirthdate(rs.getString("birthdate"));
                    addressDetailsVO.setLanguage(rs.getString("language"));
                    addressDetailsVO.setFirstname(rs.getString("firstname"));
                    addressDetailsVO.setLastname(rs.getString("lastname"));
                    addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getIp());
                }
                if(!functions.isValueNull(addressDetailsVO.getEmail()))
                    addressDetailsVO.setEmail(rs.getString("email"));
                if(!functions.isValueNull(addressDetailsVO.getTelnocc()))
                    addressDetailsVO.setTelnocc(rs.getString("telnocc"));
                if(!functions.isValueNull(addressDetailsVO.getPhone()))
                    addressDetailsVO.setPhone(rs.getString("telno"));

                if(functions.isValueNull(rs.getString("cnum")))
                {
                    commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(rs.getString("cnum")));
                    String expiryDate[] = PzEncryptor.decryptExpiryDate(rs.getString("expiry_date")).split("/");
                    commCardDetailsVO.setExpMonth(expiryDate[0]);
                    commCardDetailsVO.setExpYear(expiryDate[1]);
                }
                commCardDetailsVO.setCardHolderFirstName(rs.getString("cardholder_firstname"));
                commCardDetailsVO.setCardHolderSurname(rs.getString("cardholder_lastname"));

                tokenDetailsVO.setNotificationUrl(rs.getString("notificationUrl"));
                tokenDetailsVO.setCustomerId(rs.getString("customerid"));
                tokenDetailsVO.setPaymentType(rs.getString("paymodeid"));
                tokenDetailsVO.setCardType(rs.getString("cardtypeid"));
                tokenDetailsVO.setTokenAccountId(rs.getString("bank_accountid"));
                tokenDetailsVO.setTokenId(rs.getString("registration_id"));
                tokenDetailsVO.setRegistrationToken(rs.getString("registration_token"));
                tokenDetailsVO.setMemberId(rs.getString("memberid"));
                tokenDetailsVO.setTerminalId(rs.getString("terminalid"));
                tokenDetailsVO.setCreationOn(rs.getString("creationtime"));
                tokenDetailsVO.setIsActiveReg(rs.getString("isactive_reg"));
                tokenDetailsVO.setIsActive(rs.getString("isactive_token"));
                tokenDetailsVO.setTokenValidDays(rs.getInt("tokenvaliddays"));
                tokenDetailsVO.setCurrency(rs.getString("currency"));
                tokenDetailsVO.setToken(token);

                //query for cvv
                if(commonValidatorVO.getCardDetailsVO()!=null)
                {
                    /*if (!functions.isValueNull(commonValidatorVO.getCardDetailsVO().getcVV()))
                    {
                        StringBuffer stringBuffer = new StringBuffer("SELECT cvv FROM token_cvv AS tc, registration_master AS rm WHERE tc.registrationId=rm.registration_id AND rm.registration_token=?");
                        PreparedStatement preparedStatement = conn.prepareStatement(stringBuffer.toString());
                        preparedStatement.setString(1, tokenDetailsVO.getRegistrationToken());
                        ResultSet rs1 = preparedStatement.executeQuery();
                        logger.debug("Query for cvv:::::" + preparedStatement);
                        if (rs1.next())
                        {
                            commCardDetailsVO.setcVV(PzEncryptor.decryptCVV(rs1.getString("cvv")));
                        }
                    }
                    else
                    {*/
                        commCardDetailsVO.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());
                    //}
                }
                tokenDetailsVO.setCommCardDetailsVO(commCardDetailsVO);
                tokenDetailsVO.setAddressDetailsVO(addressDetailsVO);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" , se);
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getRegisteredTokenDetailsByMerchant()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" , e);
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getRegisteredTokenDetailsByMerchant()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVO;
    }

    public TokenDetailsVO getRegisteredTokenDetailsByMerchant(String memberId, String token, CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";
        TokenDetailsVO tokenDetailsVO = null;

        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT rt.registration_id,rt.registration_token,rt.memberid,rt.terminalid,rt.paymodeid,rt.cardtypeid,rt.isactive AS isactive_reg,FROM_UNIXTIME(rt.tokencreation_date) AS creationtime,tm.cnum,tm.expiry_date,\n" +
                    "tm.bank_accountid,tm.isactive AS isactive_token,m.tokenvaliddays,rt.currency FROM registration_master AS rt, members AS m, token_master AS tm WHERE rt.memberid=? AND rt.registration_token=BINARY(?) AND rt.tokenid=tm.tokenid AND rt.memberid=tm.toid AND tm.toid = m.memberid";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, memberId);
            pstmt.setString(2, token);
            logger.debug("qry-->"+pstmt);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                tokenDetailsVO = new TokenDetailsVO();
                tokenDetailsVO.setPaymentType(rs.getString("paymodeid"));
                tokenDetailsVO.setCardType(rs.getString("cardtypeid"));
                tokenDetailsVO.setTokenAccountId(rs.getString("bank_accountid"));
                tokenDetailsVO.setTokenId(rs.getString("registration_id"));
                tokenDetailsVO.setRegistrationToken(rs.getString("registration_token"));
                tokenDetailsVO.setMemberId(rs.getString("memberid"));
                tokenDetailsVO.setTerminalId(rs.getString("terminalid"));
                tokenDetailsVO.setCreationOn(rs.getString("creationtime"));
                tokenDetailsVO.setIsActiveReg(rs.getString("isactive_reg"));
                tokenDetailsVO.setIsActive(rs.getString("isactive_token"));
                tokenDetailsVO.setTokenValidDays(rs.getInt("tokenvaliddays"));
                tokenDetailsVO.setCurrency(rs.getString("currency"));
                tokenDetailsVO.setToken(token);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getRegisteredTokenDetailsByMerchant()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getRegisteredTokenDetailsByMerchant()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVO;
    }

    public TokenDetailsVO getRegisteredTokenDetailsByPartner(String partnerId, String token, CommonValidatorVO commonValidatorVO/*, TokenDetailsVO tokenDetailsVO*/) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";
        CommCardDetailsVO commCardDetailsVO = null;
        GenericAddressDetailsVO addressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        TokenDetailsVO tokenDetailsVO = new TokenDetailsVO();

        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT rt.registration_id,rt.registration_token,rt.memberid,rt.terminalid,tm.merchant_orderid,tm.cnum,tm.expiry_date,rt.firstname,rt.lastname,rt.email,rt.country,rt.city,rt.state,rt.street,rt.zip,rt.telnocc,rt.telno,rt.birthdate,rt.language,rt.isactive AS isactive_reg,FROM_UNIXTIME(rt.tokencreation_date) AS creationtime,tm.bank_accountid,rt.paymodeid,\n" +
                    "rt.cardtypeid,tm.isactive AS isactive_token,rt.currency,tm.cardholder_firstname,tm.cardholder_lastname,rt.notificationUrl FROM registration_master AS rt,token_master AS tm, members AS m WHERE rt.partnerid=? AND rt.registration_token=BINARY(?) AND tm.partnerid=rt.partnerid AND tm.tokenid=rt.tokenid AND tm.partnerid = m.partnerId AND rt.isactive='Y' AND rt.registration_id LIMIT 1";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, partnerId);
            pstmt.setString(2, token);
            logger.debug("qry-->" + pstmt);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                commCardDetailsVO = new CommCardDetailsVO();

                if((!functions.isValueNull(addressDetailsVO.getCity())) || (!functions.isValueNull(addressDetailsVO.getCountry())) || (!functions.isValueNull(addressDetailsVO.getZipCode())) || (!functions.isValueNull(addressDetailsVO.getState())) || (!functions.isValueNull(addressDetailsVO.getStreet())))
                {
                    addressDetailsVO.setCountry(rs.getString("country"));
                    addressDetailsVO.setCity(rs.getString("city"));
                    addressDetailsVO.setState(rs.getString("state"));
                    addressDetailsVO.setStreet(rs.getString("street"));
                    addressDetailsVO.setZipCode(rs.getString("zip"));
                    addressDetailsVO.setBirthdate(rs.getString("birthdate"));
                    addressDetailsVO.setLanguage(rs.getString("language"));
                    addressDetailsVO.setFirstname(rs.getString("firstname"));
                    addressDetailsVO.setLastname(rs.getString("lastname"));
                    addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getIp());
                }
                if(!functions.isValueNull(addressDetailsVO.getEmail()))
                    addressDetailsVO.setEmail(rs.getString("email"));
                if(!functions.isValueNull(addressDetailsVO.getTelnocc()))
                    addressDetailsVO.setTelnocc(rs.getString("telnocc"));
                if(!functions.isValueNull(addressDetailsVO.getPhone()))
                    addressDetailsVO.setPhone(rs.getString("telno"));

                if(functions.isValueNull(rs.getString("cnum")))
                {
                    commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(rs.getString("cnum")));
                    String expiryDate[] = PzEncryptor.decryptExpiryDate(rs.getString("expiry_date")).split("/");
                    commCardDetailsVO.setExpMonth(expiryDate[0]);
                    commCardDetailsVO.setExpYear(expiryDate[1]);
                }
                commCardDetailsVO.setCardHolderFirstName(rs.getString("cardholder_firstname"));
                commCardDetailsVO.setCardHolderSurname(rs.getString("cardholder_lastname"));
                tokenDetailsVO.setNotificationUrl(rs.getString("notificationUrl"));
                tokenDetailsVO.setPaymentType(rs.getString("paymodeid"));
                tokenDetailsVO.setCardType(rs.getString("cardtypeid"));
                tokenDetailsVO.setTokenAccountId(rs.getString("bank_accountid"));
                tokenDetailsVO.setTokenId(rs.getString("registration_id"));
                tokenDetailsVO.setRegistrationToken(rs.getString("registration_token"));
                tokenDetailsVO.setMemberId(rs.getString("memberid"));
                tokenDetailsVO.setTerminalId(rs.getString("terminalid"));
                tokenDetailsVO.setDescription(rs.getString("merchant_orderid"));
                tokenDetailsVO.setCreationOn(rs.getString("creationtime"));
                tokenDetailsVO.setIsActiveReg(rs.getString("isactive_reg"));
                tokenDetailsVO.setIsActive(rs.getString("isactive_token"));
                tokenDetailsVO.setCurrency(rs.getString("currency"));
                tokenDetailsVO.setToken(token);

                //query for cvv
                /*if(!functions.isValueNull(commonValidatorVO.getCardDetailsVO().getcVV())){
                    StringBuffer stringBuffer=new StringBuffer("SELECT cvv FROM token_cvv AS tc, registration_master AS rm WHERE tc.registrationId=rm.registration_id AND rm.registration_token=?");
                    PreparedStatement preparedStatement=conn.prepareStatement(stringBuffer.toString());
                    preparedStatement.setString(1,tokenDetailsVO.getRegistrationToken());
                    ResultSet rs1=preparedStatement.executeQuery();
                    logger.debug("Query for cvv:::::"+preparedStatement);
                    if(rs1.next())
                    {
                        commCardDetailsVO.setcVV(PzEncryptor.decryptCVV(rs1.getString("cvv")));
                    }
                }else {*/
                    commCardDetailsVO.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());
                //}
                tokenDetailsVO.setCommCardDetailsVO(commCardDetailsVO);
                tokenDetailsVO.setAddressDetailsVO(addressDetailsVO);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getRegisteredTokenDetailsByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getRegisteredTokenDetailsByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVO;
    }

    public List<RegistrationDetailVO> getRegistrationsByMerchantAndCustomer(String toId, String cardholderId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer query = new StringBuffer();
        RegistrationDetailVO registrationDetailVO = null;
        TokenDetailsVO tokenDetailsVO = null;
        CommCardDetailsVO commCardDetailsVO = null;
        GenericAddressDetailsVO addressDetailsVO = null;
        List<RegistrationDetailVO> registrationDetailVOList = new ArrayList();
        try
        {
            conn = Database.getRDBConnection();
            query.append("SELECT rm.registration_token,rm.customerid,tm.cardholderid,rm.paymodeid,rm.cardtypeid,tm.tokenid,tm.token,rm.memberid,tm.merchant_orderid,tm.cnum,tm.expiry_date,tm.cardholder_firstname,tm.cardholder_lastname,tm.cardholderemail,rm.terminalid,tm.country,tm.city,\n" +
                    "tm.state,tm.street,tm.zip,tm.telnocc,tm.telno,tm.birthdate,tm.language,tm.isactive AS isActiveToken,rm.isActive AS isActiveReg,FROM_UNIXTIME(rm.tokencreation_date) AS creationtime,m.tokenvaliddays,m.isAddrDetailsRequired,tm.bank_accountid \n" +
                    "FROM token_master AS tm, registration_master AS rm, members AS m, cardholder_master AS cm WHERE tm.tokenid=rm.tokenid AND rm.memberid=m.memberid AND rm.memberid=? AND cm.`cardholderid`=rm.customerid AND rm.customerid=? AND rm.isactive='Y' AND tm.isActive='Y'");

            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, toId);
            pstmt.setString(2, cardholderId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                tokenDetailsVO = new TokenDetailsVO();
                registrationDetailVO = new RegistrationDetailVO();
                commCardDetailsVO = new CommCardDetailsVO();
                addressDetailsVO = new GenericAddressDetailsVO();

                addressDetailsVO.setFirstname(rs.getString("cardholder_firstname"));
                addressDetailsVO.setLastname(rs.getString("cardholder_lastname"));

                addressDetailsVO.setCountry(rs.getString("country"));
                addressDetailsVO.setCity(rs.getString("city"));
                addressDetailsVO.setState(rs.getString("state"));
                addressDetailsVO.setStreet(rs.getString("street"));
                addressDetailsVO.setZipCode(rs.getString("zip"));
                addressDetailsVO.setTelnocc(rs.getString("telnocc"));
                addressDetailsVO.setPhone(rs.getString("telno"));
                addressDetailsVO.setBirthdate(rs.getString("birthdate"));
                addressDetailsVO.setLanguage(rs.getString("language"));
                addressDetailsVO.setEmail(rs.getString("cardholderemail"));

                commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(rs.getString("cnum")));
                if (functions.isValueNull(rs.getString("expiry_date")))
                {
                    String expiryDate[] = PzEncryptor.decryptExpiryDate(rs.getString("expiry_date")).split("/");
                    commCardDetailsVO.setExpMonth(expiryDate[0]);
                    commCardDetailsVO.setExpYear(expiryDate[1]);
                }
                commCardDetailsVO.setCardHolderFirstName(rs.getString("cardholder_firstname"));
                commCardDetailsVO.setCardHolderSurname(rs.getString("cardholder_lastname"));

                tokenDetailsVO.setBankAccountId(rs.getString("bank_accountid"));

                registrationDetailVO.setRegistration_token(rs.getString("registration_token"));
                registrationDetailVO.setIsActive(rs.getString("isActiveReg"));
                registrationDetailVO.setTerminalId("terminalid");
                registrationDetailVO.setToid(rs.getString("memberid"));
                registrationDetailVO.setTokenId(rs.getString("tokenid"));
                registrationDetailVO.setCustomerId(rs.getString("customerid"));

                registrationDetailVO.setCommCardDetailsVO(commCardDetailsVO);
                registrationDetailVO.setAddressDetailsVO(addressDetailsVO);
                registrationDetailVO.setTokenDetailsVO(tokenDetailsVO);

                registrationDetailVOList.add(registrationDetailVO);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getRegistrationsByMerchantAndCustomer()", null, "common", "SystemError Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getRegistrationsByMerchantAndCustomer()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return registrationDetailVOList;
    }

    public List<RegistrationDetailVO> getRegistrationsByMerchant(String toId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer query = new StringBuffer();
        RegistrationDetailVO registrationDetailVO = null;
        TokenDetailsVO tokenDetailsVO = null;
        CommCardDetailsVO commCardDetailsVO = null;
        GenericAddressDetailsVO addressDetailsVO = null;
        List<RegistrationDetailVO> registrationDetailVOList = new ArrayList();
        try
        {
            conn = Database.getRDBConnection();
            query.append("SELECT rm.registration_token,rm.customerid,tm.cardholderid,rm.paymodeid,rm.cardtypeid,tm.tokenid,tm.token,rm.memberid,tm.merchant_orderid,tm.cnum,tm.expiry_date,tm.cardholder_firstname,tm.cardholder_lastname,tm.cardholderemail,rm.terminalid,tm.country,tm.city,\n" +
                    "tm.state,tm.street,tm.zip,tm.telnocc,tm.telno,tm.birthdate,tm.language,tm.isactive AS isActiveToken,rm.isActive AS isActiveReg,FROM_UNIXTIME(rm.tokencreation_date) AS creationtime,m.tokenvaliddays,m.isAddrDetailsRequired,tm.bank_accountid \n" +
                    "FROM token_master AS tm, registration_master AS rm, members AS m WHERE tm.tokenid=rm.tokenid AND rm.memberid=m.memberid AND rm.memberid=? AND rm.isactive='Y' AND tm.isActive='Y'");

            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, toId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                tokenDetailsVO = new TokenDetailsVO();
                registrationDetailVO = new RegistrationDetailVO();
                commCardDetailsVO = new CommCardDetailsVO();
                addressDetailsVO = new GenericAddressDetailsVO();

                addressDetailsVO.setFirstname(rs.getString("cardholder_firstname"));
                addressDetailsVO.setLastname(rs.getString("cardholder_lastname"));

                addressDetailsVO.setCountry(rs.getString("country"));
                addressDetailsVO.setCity(rs.getString("city"));
                addressDetailsVO.setState(rs.getString("state"));
                addressDetailsVO.setStreet(rs.getString("street"));
                addressDetailsVO.setZipCode(rs.getString("zip"));
                addressDetailsVO.setTelnocc(rs.getString("telnocc"));
                addressDetailsVO.setPhone(rs.getString("telno"));
                addressDetailsVO.setBirthdate(rs.getString("birthdate"));
                addressDetailsVO.setLanguage(rs.getString("language"));
                addressDetailsVO.setEmail(rs.getString("cardholderemail"));

                commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(rs.getString("cnum")));
                if (functions.isValueNull(rs.getString("expiry_date")))
                {
                    String expiryDate[] = PzEncryptor.decryptExpiryDate(rs.getString("expiry_date")).split("/");
                    commCardDetailsVO.setExpMonth(expiryDate[0]);
                    commCardDetailsVO.setExpYear(expiryDate[1]);
                }
                commCardDetailsVO.setCardHolderFirstName(rs.getString("cardholder_firstname"));
                commCardDetailsVO.setCardHolderSurname(rs.getString("cardholder_lastname"));

                tokenDetailsVO.setBankAccountId(rs.getString("bank_accountid"));

                registrationDetailVO.setRegistration_token(rs.getString("registration_token"));
                registrationDetailVO.setIsActive(rs.getString("isActiveReg"));
                registrationDetailVO.setTerminalId("terminalid");
                registrationDetailVO.setToid(rs.getString("memberid"));
                registrationDetailVO.setTokenId(rs.getString("tokenid"));
                registrationDetailVO.setCustomerId(rs.getString("customerid"));

                registrationDetailVO.setCommCardDetailsVO(commCardDetailsVO);
                registrationDetailVO.setAddressDetailsVO(addressDetailsVO);
                registrationDetailVO.setTokenDetailsVO(tokenDetailsVO);

                registrationDetailVOList.add(registrationDetailVO);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getRegistrationsByMerchant()", null, "common", "SystemError Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getRegistrationsByMerchant()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return registrationDetailVOList;
    }

    public List<RegistrationDetailVO> getCustomerRegistrationDetailsByPartner(String partnerId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer query = new StringBuffer();
        TokenDetailsVO tokenDetailsVO = null;
        RegistrationDetailVO registrationDetailVO = null;
        List<RegistrationDetailVO> registrationDetailVOList = new ArrayList();
        CommCardDetailsVO commCardDetailsVO = null;
        GenericAddressDetailsVO addressDetailsVO = null;
        try
        {
            conn = Database.getRDBConnection();
            query.append("SELECT rm.registration_token,rm.customerid,rm.cardtypeid,rm.paymodeid,tm.tokenid,rm.tokenid,rm.memberid,tm.merchant_orderid,tm.cnum,tm.expiry_date,tm.cardholder_firstname,tm.cardholder_lastname,tm.cardholderemail,rm.terminalid,tm.country,tm.city,tm.state,tm.street,tm.zip,\n" +
                    "tm.telnocc,tm.telno,tm.birthdate,tm.language,tm.isactive AS isActiveToken, rm.`isActive` AS isActiveReg,FROM_UNIXTIME(rm.tokencreation_date) AS creationtime,p.tokenValidDays,p.isAddressRequiredForTokenTransaction,tm.bank_accountid \n" +
                    "FROM token_master AS tm, registration_master AS rm, partners AS p WHERE tm.tokenid=rm.tokenid AND tm.partnerid = p.partnerid AND tm.partnerid=? AND rm.memberid IS NULL AND rm.isactive='Y'");

            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, partnerId);

            rs = pstmt.executeQuery();
            while (rs.next())
            {
                tokenDetailsVO = new TokenDetailsVO();
                registrationDetailVO = new RegistrationDetailVO();
                commCardDetailsVO = new CommCardDetailsVO();
                addressDetailsVO = new GenericAddressDetailsVO();

                addressDetailsVO.setFirstname(rs.getString("cardholder_firstname"));
                addressDetailsVO.setLastname(rs.getString("cardholder_lastname"));
                addressDetailsVO.setCountry(rs.getString("country"));
                addressDetailsVO.setCity(rs.getString("city"));
                addressDetailsVO.setState(rs.getString("state"));
                addressDetailsVO.setStreet(rs.getString("street"));
                addressDetailsVO.setZipCode(rs.getString("zip"));
                addressDetailsVO.setTelnocc(rs.getString("telnocc"));
                addressDetailsVO.setPhone(rs.getString("telno"));
                addressDetailsVO.setBirthdate(rs.getString("birthdate"));
                addressDetailsVO.setLanguage(rs.getString("language"));
                addressDetailsVO.setEmail(rs.getString("cardholderemail"));

                commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(rs.getString("cnum")));
                if (functions.isValueNull(rs.getString("expiry_date")))
                {
                    String expiryDate[] = PzEncryptor.decryptExpiryDate(rs.getString("expiry_date")).split("/");
                    commCardDetailsVO.setExpMonth(expiryDate[0]);
                    commCardDetailsVO.setExpYear(expiryDate[1]);
                }
                commCardDetailsVO.setCardHolderFirstName(rs.getString("cardholder_firstname"));
                commCardDetailsVO.setCardHolderSurname(rs.getString("cardholder_lastname"));

                tokenDetailsVO.setTokenId(rs.getString("tokenid"));
                tokenDetailsVO.setCreationOn(rs.getString("creationtime"));
                tokenDetailsVO.setIsActive(rs.getString("isActiveToken"));
                tokenDetailsVO.setTokenValidDays(rs.getInt("tokenValidDays"));
                tokenDetailsVO.setIsAddrDetailsRequired(rs.getString("isAddressRequiredForTokenTransaction"));
                tokenDetailsVO.setBankAccountId(rs.getString("bank_accountid"));

                registrationDetailVO.setRegistration_token(rs.getString("registration_token"));
                registrationDetailVO.setIsActive(rs.getString("isActiveReg"));
                registrationDetailVO.setCardtypeid(rs.getString("cardtypeid"));
                registrationDetailVO.setPaymodeid(rs.getString("paymodeid"));
                registrationDetailVO.setTerminalId(rs.getString("terminalid"));
                registrationDetailVO.setToid(rs.getString("memberid"));
                registrationDetailVO.setTokenId(rs.getString("tokenid"));
                registrationDetailVO.setTokencreation_date(rs.getString("creationtime"));
                registrationDetailVO.setCustomerId(rs.getString("customerid"));

                registrationDetailVO.setTokenDetailsVO(tokenDetailsVO);
                registrationDetailVO.setCommCardDetailsVO(commCardDetailsVO);
                registrationDetailVO.setAddressDetailsVO(addressDetailsVO);

                registrationDetailVOList.add(registrationDetailVO);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getCustomerRegistrationDetailsByPartner()", null, "common", "SystemError Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getCustomerRegistrationDetailsByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return registrationDetailVOList;
    }

    public List<RegistrationDetailVO> getRegistrationsByPartnerAndCustomer(String partnerId, String customerId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer query = new StringBuffer();
        TokenDetailsVO tokenDetailsVO = null;
        RegistrationDetailVO registrationDetailVO = null;
        List<RegistrationDetailVO> registrationDetailVOList = new ArrayList();
        CommCardDetailsVO commCardDetailsVO = null;
        GenericAddressDetailsVO addressDetailsVO = null;
        try
        {
            conn = Database.getRDBConnection();
            query.append("SELECT rm.registration_token,rm.customerid,rm.cardtypeid,rm.paymodeid,tm.tokenid,rm.tokenid,rm.memberid,tm.merchant_orderid,tm.cnum,tm.expiry_date,tm.cardholder_firstname,tm.cardholder_lastname,tm.cardholderemail,rm.terminalid,tm.country,tm.city,tm.state,tm.street,tm.zip,\n" +
                    "tm.telnocc,tm.telno,tm.birthdate,tm.language,tm.isactive AS isActiveToken, rm.`isActive` AS isActiveReg,FROM_UNIXTIME(rm.tokencreation_date) AS creationtime,p.tokenValidDays,p.isAddressRequiredForTokenTransaction,tm.bank_accountid \n" +
                    "FROM token_master AS tm, registration_master AS rm, partners AS p, cardholder_master AS ch WHERE tm.tokenid=rm.tokenid AND tm.partnerid = p.partnerid AND rm.partnerid = ch.partnerid AND rm.customerid=ch.cardholderid AND tm.partnerid=? AND rm.customerid=? AND rm.memberid IS NULL AND rm.isactive='Y'");

            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, partnerId);
            pstmt.setString(2, customerId);
            logger.debug("getRegistrationsByPartnerAndCustomer-->" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                tokenDetailsVO = new TokenDetailsVO();
                registrationDetailVO = new RegistrationDetailVO();
                commCardDetailsVO = new CommCardDetailsVO();
                addressDetailsVO = new GenericAddressDetailsVO();

                addressDetailsVO.setFirstname(rs.getString("cardholder_firstname"));
                addressDetailsVO.setLastname(rs.getString("cardholder_lastname"));
                addressDetailsVO.setCountry(rs.getString("country"));
                addressDetailsVO.setCity(rs.getString("city"));
                addressDetailsVO.setState(rs.getString("state"));
                addressDetailsVO.setStreet(rs.getString("street"));
                addressDetailsVO.setZipCode(rs.getString("zip"));
                addressDetailsVO.setTelnocc(rs.getString("telnocc"));
                addressDetailsVO.setPhone(rs.getString("telno"));
                addressDetailsVO.setBirthdate(rs.getString("birthdate"));
                addressDetailsVO.setLanguage(rs.getString("language"));
                addressDetailsVO.setEmail(rs.getString("cardholderemail"));

                commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(rs.getString("cnum")));
                if (functions.isValueNull(rs.getString("expiry_date")))
                {
                    String expiryDate[] = PzEncryptor.decryptExpiryDate(rs.getString("expiry_date")).split("/");
                    commCardDetailsVO.setExpMonth(expiryDate[0]);
                    commCardDetailsVO.setExpYear(expiryDate[1]);
                }
                commCardDetailsVO.setCardHolderFirstName(rs.getString("cardholder_firstname"));
                commCardDetailsVO.setCardHolderSurname(rs.getString("cardholder_lastname"));

                tokenDetailsVO.setTokenId(rs.getString("tokenid"));
                tokenDetailsVO.setCreationOn(rs.getString("creationtime"));
                tokenDetailsVO.setIsActive(rs.getString("isActiveToken"));
                tokenDetailsVO.setTokenValidDays(rs.getInt("tokenValidDays"));
                tokenDetailsVO.setIsAddrDetailsRequired(rs.getString("isAddressRequiredForTokenTransaction"));
                tokenDetailsVO.setBankAccountId(rs.getString("bank_accountid"));

                registrationDetailVO.setRegistration_token(rs.getString("registration_token"));
                registrationDetailVO.setIsActive(rs.getString("isActiveReg"));
                registrationDetailVO.setCardtypeid(rs.getString("cardtypeid"));
                registrationDetailVO.setPaymodeid(rs.getString("paymodeid"));
                registrationDetailVO.setTerminalId(rs.getString("terminalid"));
                registrationDetailVO.setToid(rs.getString("memberid"));
                registrationDetailVO.setTokenId(rs.getString("tokenid"));
                registrationDetailVO.setTokencreation_date(rs.getString("creationtime"));
                registrationDetailVO.setCustomerId(rs.getString("customerid"));

                registrationDetailVO.setTokenDetailsVO(tokenDetailsVO);
                registrationDetailVO.setCommCardDetailsVO(commCardDetailsVO);
                registrationDetailVO.setAddressDetailsVO(addressDetailsVO);

                registrationDetailVOList.add(registrationDetailVO);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getRegistrationsByPartnerAndCustomer()", null, "common", "SystemError Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getRegistrationsByPartnerAndCustomer()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return registrationDetailVOList;
    }

    public boolean isCustomerRegisteredWithMerchant(String toId, String customerId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer query = new StringBuffer();
        boolean status = false;
        try
        {
            conn = Database.getRDBConnection();
            query.append("SELECT customerid FROM registration_master WHERE memberid=? AND customerid=?");
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, toId);
            pstmt.setString(2, customerId);
            logger.debug("isCustomerRegisteredWithMerchant qry--->"+pstmt);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                status = true;
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isCustomerRegisteredWithMerchant()", null, "common", "SystemError Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isCustomerRegisteredWithMerchant()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public boolean isCustomerRegisteredWithPartner(String partnerId, String customerId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer query = new StringBuffer();
        boolean status = false;
        try
        {
            conn = Database.getRDBConnection();
            query.append("SELECT customerid FROM registration_master WHERE partnerid=? AND customerid=? AND memberid IS NULL");
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, partnerId);
            pstmt.setString(2, customerId);
            logger.debug("Customer validation qry-->" + pstmt);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                status = true;
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isCustomerRegisteredWithPartner()", null, "common", "SystemError Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isCustomerRegisteredWithPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public List<RegistrationDetailVO> getBankAccountDetails(String bankAccountId, List<RegistrationDetailVO> registrationDetailVOList) throws PZDBViolationException
    {
        RegistrationDetailVO registrationDetailVO = null;
        ReserveField2VO reserveField2VO = null;
        CommCardDetailsVO cardDetailsVO = null;
        GenericAddressDetailsVO addressDetailsVO = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer query = new StringBuffer();

        try
        {
            conn = Database.getRDBConnection();
            query.append("SELECT rm.registration_token,rm.isActive AS isActiveReg,rm.paymodeid,rm.cardtypeid,rm.terminalid,rm.memberid,rm.customerid,ba.bic,ba.iban,ba.bankname,\n" +
                    "ba.accountNumber,ba.bankCode,ba.country,ba.city,ba.state,ba.street,ba.zip,ba.telnocc,ba.telno,ba.mandate_id,ba.mandate_dateOfSignature,ba.transactionDueDate,ba.routingNumber,ba.accountType\n" +
                    "FROM bankaccount_details AS ba JOIN token_master AS tm ON ba.bank_accountid=tm.bank_accountid JOIN registration_master AS rm ON rm.tokenid=tm.tokenid AND ba.bank_accountid IN(?)");

            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, bankAccountId);

            logger.debug("Qry-->" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                registrationDetailVO = new RegistrationDetailVO();
                reserveField2VO = new ReserveField2VO();
                cardDetailsVO = new CommCardDetailsVO();
                addressDetailsVO = new GenericAddressDetailsVO();

                cardDetailsVO.setBIC(rs.getString("bic"));
                cardDetailsVO.setIBAN(rs.getString("iban"));
                cardDetailsVO.setBankName(rs.getString("bankname"));
                cardDetailsVO.setMandateId(rs.getString("mandate_id"));

                addressDetailsVO.setCountry(rs.getString("country"));
                addressDetailsVO.setCity(rs.getString("city"));
                addressDetailsVO.setState(rs.getString("state"));
                addressDetailsVO.setStreet(rs.getString("street"));
                addressDetailsVO.setZipCode(rs.getString("zip"));
                addressDetailsVO.setTelnocc(rs.getString("telnocc"));
                addressDetailsVO.setPhone(rs.getString("telno"));

                reserveField2VO.setAccountNumber(rs.getString("accountNumber"));
                reserveField2VO.setRoutingNumber(rs.getString("routingNumber"));
                reserveField2VO.setAccountType(rs.getString("accountType"));
                reserveField2VO.setCardDetailsVO(cardDetailsVO);

                registrationDetailVO.setRegistration_token(rs.getString("registration_token"));
                registrationDetailVO.setIsActive(rs.getString("isActiveReg"));
                registrationDetailVO.setPaymodeid(rs.getString("paymodeid"));
                registrationDetailVO.setCardType(rs.getString("cardtypeid"));
                registrationDetailVO.setTerminalId(rs.getString("terminalid"));
                registrationDetailVO.setMemberId(rs.getString("memberid"));
                registrationDetailVO.setCustomerId(rs.getString("customerid"));

                registrationDetailVO.setAddressDetailsVO(addressDetailsVO);
                registrationDetailVO.setReserveField2VO(reserveField2VO);

                registrationDetailVOList.add(registrationDetailVO);
            }
        }
        catch (SystemError e)
        {
            logger.error("SystemError::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getBankAccountDetails()", null, "common", "DB_CONNECTION_ISSUE Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getBankAccountDetails()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return registrationDetailVOList;
    }

    public TokenResponseVO createTokenRegistrationByPartner(TokenRequestVO tokenRequestVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        TokenResponseVO tokenResponseVO = new TokenResponseVO();
        TokenManager tokenManager = new TokenManager();
        String reg_token = "";
        try
        {
            conn = Database.getConnection();
            StringBuffer qry = new StringBuffer("INSERT INTO registration_master(registration_id,registration_token,token,paymodeid,cardtypeid,currency,terminalid,toid,trackingid,isActive,partnerid,customerid,tokencreation_date) VALUES(null,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()))");
            pstmt = conn.prepareStatement(qry.toString());
            reg_token = TokenManager.generateTokenRegestration();
            pstmt.setString(1, reg_token);
            pstmt.setString(2, tokenRequestVO.getToken());
            pstmt.setString(3, tokenRequestVO.getPaymentType());
            pstmt.setString(4, tokenRequestVO.getCardType());
            pstmt.setString(5, tokenRequestVO.getTransDetailsVO().getCurrency());

            pstmt.setString(6, tokenRequestVO.getTerminalId());
            pstmt.setString(7, tokenRequestVO.getMemberId());
            pstmt.setString(8, tokenRequestVO.getTrackingId());
            pstmt.setString(9, "Y");
            pstmt.setString(10, tokenRequestVO.getPartnerId());
            pstmt.setString(11, tokenRequestVO.getCustomerId());
            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                TokenDetailsVO tokenDetailsVO = getRegistrationDetailsByPartner(tokenRequestVO.getPartnerId(),reg_token);
                tokenResponseVO.setTokenDetailsVO(tokenDetailsVO);
                tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                tokenResponseVO.setStatus("success");
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createTokenRegistrationByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SystemError::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createTokenRegistrationByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenResponseVO;
    }

    private TokenDetailsVO getRegistrationDetailsByPartner(String partnerId, String registrationToken) throws PZDBViolationException
    {
        TokenDetailsVO tokenDetailsVO = new TokenDetailsVO();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer query = new StringBuffer();

        try
        {
            conn = Database.getRDBConnection();
            query.append("SELECT registration_token,isActive,token FROM registration_master WHERE partnerid=? AND registration_token=?");
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, partnerId);
            pstmt.setString(2, registrationToken);
            rs = pstmt.executeQuery();
            if(rs.next())
            {
                tokenDetailsVO.setRegistrationToken(rs.getString("registration_token"));
                tokenDetailsVO.setIsActiveReg(rs.getString("isActive"));
                tokenDetailsVO.setToken(rs.getString("token"));
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getRegistrationDetailsByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getRegistrationDetailsByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVO;
    }

    public String getExistingTokenByPartner(String partnerId, String cardNum) throws PZDBViolationException
    {
        String existingTokenId = "";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer query = new StringBuffer();

        try
        {
            //System.out.println("inside method=====");
            conn = Database.getRDBConnection();
            query.append("SELECT tokenid,token,cnum FROM token_master WHERE partnerid=?");
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, partnerId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                if(cardNum.equals(PzEncryptor.decryptPAN(rs.getString("cnum"))))
                {
                    existingTokenId = rs.getString("tokenid");
                }
            }
            logger.debug("query for existing token id-----" + pstmt);
            //System.out.println("query for existing token id-----"+pstmt);
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getExistingTokenByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getExistingTokenByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return existingTokenId;
    }

    public String getExistingTokenForAccountByPartner(TokenRequestVO tokenRequestVO) throws PZDBViolationException
    {
        GenericCardDetailsVO cardDetailsVO = tokenRequestVO.getCardDetailsVO();
        ReserveField2VO reserveField2VO = tokenRequestVO.getReserveField2VO();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String token = "";
        StringBuffer query = new StringBuffer();
        try
        {
            conn = Database.getRDBConnection();
            query.append("SELECT tm.token,bad.bic,bad.iban,bad.accountNumber,bad.accountType,bad.routingNumber FROM token_master AS tm JOIN bankaccount_details AS bad WHERE tm.bank_accountid = bad.bank_accountid AND tm.partnerid = ? AND tm.isactive = 'Y'");
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, tokenRequestVO.getPartnerDetailsVO().getPartnerId());
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                if (functions.isValueNull(rs.getString("bic")) && functions.isValueNull(rs.getString("iban")))
                {
                    if (cardDetailsVO.getBIC().equals(rs.getString("bic")) && cardDetailsVO.getIBAN().equals(rs.getString("iban")))
                    {
                        token = rs.getString("token");
                    }
                }
                else if (functions.isValueNull(rs.getString("accountNumber")) && functions.isValueNull(rs.getString("routingNumber")) && functions.isValueNull(rs.getString("accountType")))
                {
                    if (reserveField2VO.getAccountNumber().equals(rs.getString("accountNumber")) && reserveField2VO.getAccountType().equals(rs.getString("accountType")) && reserveField2VO.getRoutingNumber().equals(rs.getString("routingNumber")))
                    {
                        token = rs.getString("token");
                    }
                }
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getExistingTokenForAccountByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getExistingTokenForAccountByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return token;
    }

    public boolean isNewAccountForPartner(TokenRequestVO tokenRequestVO) throws PZDBViolationException
    {
        Connection conn = null;
        GenericCardDetailsVO cardDetailsVO = tokenRequestVO.getCardDetailsVO();
        ReserveField2VO reserveField2VO = tokenRequestVO.getReserveField2VO();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean status = true;
        StringBuffer query = new StringBuffer();
        try
        {
            conn = Database.getRDBConnection();
            query.append("SELECT tm.token,bad.bic,bad.iban,bad.accountNumber,bad.accountType,bad.routingNumber FROM token_master AS tm JOIN bankaccount_details AS bad WHERE tm.bank_accountid = bad.bank_accountid AND tm.partnerid =? AND tm.isactive = 'Y'");
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, tokenRequestVO.getPartnerDetailsVO().getPartnerId());
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                if (functions.isValueNull(rs.getString("bic")) && functions.isValueNull(rs.getString("iban")))
                {
                    if (cardDetailsVO.getBIC().equals(rs.getString("bic")) && cardDetailsVO.getIBAN().equals(rs.getString("iban")))
                    {
                        status = false;
                    }
                }
                else if (functions.isValueNull(rs.getString("accountNumber")) && functions.isValueNull(rs.getString("routingNumber")) && functions.isValueNull(rs.getString("accountType")))
                {
                    if (reserveField2VO.getAccountNumber().equals(rs.getString("accountNumber")) && reserveField2VO.getAccountType().equals(rs.getString("accountType")) && reserveField2VO.getRoutingNumber().equals(rs.getString("routingNumber")))
                    {
                        status = false;
                    }
                }
            }
        }
        catch (SystemError se)
        {
            status = false;
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isNewAccountForPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            status = false;
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isNewAccountForPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public TokenResponseVO createTokenAccountByPartner(TokenRequestVO tokenRequestVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        StringBuffer query = new StringBuffer();
        String bankAccountId = "";
        GenericAddressDetailsVO addressDetailsVO = tokenRequestVO.getAddressDetailsVO();
        ReserveField2VO reserveField2VO = tokenRequestVO.getReserveField2VO();
        TokenResponseVO tokenResponseVO = new TokenResponseVO();

        try
        {
            conn = Database.getConnection();
            query.append("INSERT INTO bankaccount_details(bank_accountid,bic,iban,holder,country,city,state,street,zip,telnocc,telno,accountNumber,accountType,routingNumber)VALUES(NULL,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pstmt = conn.prepareStatement(query.toString());

            pstmt.setString(1, tokenRequestVO.getCardDetailsVO().getBIC());
            pstmt.setString(2, tokenRequestVO.getCardDetailsVO().getIBAN());
            pstmt.setString(3, tokenRequestVO.getCardDetailsVO().getCardHolderName());
            pstmt.setString(4, tokenRequestVO.getAddressDetailsVO().getCountry());
            pstmt.setString(5, addressDetailsVO.getCity());
            pstmt.setString(6, addressDetailsVO.getState());
            pstmt.setString(7, addressDetailsVO.getStreet());
            pstmt.setString(8, addressDetailsVO.getZipCode());
            pstmt.setString(9, addressDetailsVO.getTelnocc());
            pstmt.setString(10, addressDetailsVO.getPhone());
            pstmt.setString(11, reserveField2VO.getAccountNumber());
            pstmt.setString(12, reserveField2VO.getAccountType());
            pstmt.setString(13, reserveField2VO.getRoutingNumber());
            logger.debug("bankDetails entry in bankaccpint_details.." + pstmt);

            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if (rs.next())
            {
                logger.debug("inside If--");
                bankAccountId = rs.getString(1);
                tokenResponseVO = createAccountTokenByPartner(bankAccountId, tokenRequestVO);
                tokenResponseVO.setStatus("success");
            }
        }
        catch (SystemError se)
        {
            tokenResponseVO.setStatus("fail");
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createTokenAccountByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            tokenResponseVO.setStatus("fail");
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "createTokenAccountByPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenResponseVO;
    }

    public TokenDetailsVO getRegistrationTrackingId(String memberId, TokenDetailsVO tokenDetailsVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "";

        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT tracking_id FROM registration_member_mapping AS rmm, registration_master AS rm WHERE rmm.toid= ? AND rm.registration_token = ? AND rm.registration_id=rmm.registration_tokenid";

            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, memberId);
            pstmt.setString(2, tokenDetailsVO.getToken());
            rs = pstmt.executeQuery();
            logger.debug("getInitialTokenDetails QRY---->" + pstmt);
            if (rs.next())
            {
                tokenDetailsVO.setTrackingId(rs.getString("tracking_id"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getRegistrationTrackingId()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getRegistrationTrackingId()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return tokenDetailsVO;
    }

    public boolean isTokenMappedWithTrackingId(String registrationId, String memberId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isTokenMappedWithTrackingId = false;
        StringBuffer query = new StringBuffer();

        try
        {
            conn = Database.getRDBConnection();
            query.append("SELECT tracking_id FROM registration_member_mapping WHERE registration_tokenid = ? AND toid = ?");

            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, registrationId);
            pstmt.setString(2, memberId);
            rs = pstmt.executeQuery();
            logger.debug("isTokenMappedWithTrackingId QRY---->" + pstmt);
            if (rs.next())
            {
                isTokenMappedWithTrackingId = true;
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isTokenMappedWithTrackingId()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "isTokenMappedWithTrackingId()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return isTokenMappedWithTrackingId;
    }

    //new
    public List<TokenDetailsVO> getAdminRegistrationList(String memberId,String fdtstamp,String tdtstamp,String description,String firstName,String lastName,String email, PaginationVO paginationVO, String partnerid,String city,String state, String street, String country, String zipCode, String telnocc, String telno)throws PZDBViolationException
    {
        logger.debug("inside getMerchantRegistrationList===");
        List<TokenDetailsVO> tokenDetailsVOList = new ArrayList();
        TokenDetailsVO tokenDetailsVO = null;
        GenericAddressDetailsVO addressDetailsVO = null;
        Functions functions = new Functions();
        StringBuffer query = null;
        StringBuffer countQuery = null;
        Connection connection = null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        PreparedStatement pstmtQry = null;
        PreparedStatement pstmtCount = null;

        try
        {
            connection = Database.getRDBConnection();
           // query = new StringBuffer("SELECT FROM_UNIXTIME(rm.tokencreation_date) AS creationtime , rm.memberid,rm.registration_id,rm.registration_token,rm.partnerid,rm.firstName,rm.lastName,rm.email,rm.isActive,tm.merchant_orderid,tm.bank_accountid,tm.token,tm.generatedBy,FROM_UNIXTIME(tm.tokencreationon) AS tokencreationon,tm.tokenid FROM registration_master AS rm, token_master AS tm WHERE rm.tokenid=tm.tokenid AND rm.isActive='Y' AND tm.isactive='Y'");
            query = new StringBuffer("SELECT DISTINCT FROM_UNIXTIME(rm.tokencreation_date) AS creationtime ,rm.memberid,rm.registration_id,rm.registration_token,rm.partnerid,rm.firstName,rm.lastName,rm.email,rm.isActive,tm.merchant_orderid,tm.bank_accountid,tm.token,tm.generatedBy,tm.cnum,FROM_UNIXTIME(tm.tokencreationon) AS tokencreationon,tm.tokenid,rm.generatedBy AS regGeneratedBy,rm.customerid,rm.city,rm.state,rm.street,rm.country,rm.zip,rm.telnocc,rm.telno FROM registration_master AS rm JOIN token_master AS tm ON rm.tokenid=tm.tokenid WHERE rm.isActive='Y' AND tm.isactive='Y'");
            countQuery = new StringBuffer("SELECT count(*) FROM registration_master AS rm, token_master AS tm WHERE rm.tokenid=tm.tokenid AND rm.isActive='Y' AND tm.isactive='Y'");

            if(functions.isValueNull(memberId))
            {
                query.append(" and rm.memberid='" + ESAPI.encoder().encodeForSQL(me, memberId) + "'");
                countQuery.append(" and rm.memberid='" + ESAPI.encoder().encodeForSQL(me, memberId) + "'");
            }
            if (functions.isValueNull(description))
            {
                query.append(" and merchant_orderid='" + ESAPI.encoder().encodeForSQL(me, description) + "'");
                countQuery.append(" and merchant_orderid='" + ESAPI.encoder().encodeForSQL(me, description) + "'");
            }
            if (functions.isValueNull(firstName))
            {
                query.append(" and firstname='" + ESAPI.encoder().encodeForSQL(me, firstName) + "'");
                countQuery.append(" and firstname='" + ESAPI.encoder().encodeForSQL(me, firstName) + "'");
            }
            if (functions.isValueNull(lastName))
            {
                query.append(" and lastname='" + ESAPI.encoder().encodeForSQL(me, lastName) + "'");
                countQuery.append(" and lastname='" + ESAPI.encoder().encodeForSQL(me, lastName) + "'");
            }
            if (functions.isValueNull(email))
            {
                query.append(" and email='" + ESAPI.encoder().encodeForSQL(me, email) + "'");
                countQuery.append(" and email='" + ESAPI.encoder().encodeForSQL(me, email) + "'");
            }
            if (functions.isValueNull(city))
            {
                query.append(" and city='" + ESAPI.encoder().encodeForSQL(me, city) + "'");
                countQuery.append(" and city='" + ESAPI.encoder().encodeForSQL(me,city) + "'");
            }
            if (functions.isValueNull(state))
            {
                query.append(" and state='" + ESAPI.encoder().encodeForSQL(me, state) + "'");
                countQuery.append(" and state='" + ESAPI.encoder().encodeForSQL(me,state) + "'");
            }
            if (functions.isValueNull(street))
            {
                query.append(" and street='" + ESAPI.encoder().encodeForSQL(me,street) + "'");
                countQuery.append(" and street='" + ESAPI.encoder().encodeForSQL(me,street) + "'");
            }
            if (functions.isValueNull(country))
            {
                query.append(" and country='" + ESAPI.encoder().encodeForSQL(me,country) + "'");
                countQuery.append(" and country='" + ESAPI.encoder().encodeForSQL(me,country) + "'");
            }
            if (functions.isValueNull(zipCode))
            {
                query.append(" and zip='" + ESAPI.encoder().encodeForSQL(me,zipCode) + "'");
                countQuery.append(" and zip='" + ESAPI.encoder().encodeForSQL(me,zipCode) + "'");
            }
            if (functions.isValueNull(telnocc))
            {
                query.append(" and telnocc='" + ESAPI.encoder().encodeForSQL(me,telnocc) + "'");
                countQuery.append(" and telnocc='" + ESAPI.encoder().encodeForSQL(me,telnocc)+ "'");
            }
            if (functions.isValueNull(telno))
            {
                query.append(" and telno='" + ESAPI.encoder().encodeForSQL(me,telno) + "'");
                countQuery.append(" and telno='" + ESAPI.encoder().encodeForSQL(me,telno) + "'");
            }
            if (functions.isValueNull(partnerid))
            {
                query.append(" and rm.partnerid='" + ESAPI.encoder().encodeForSQL(me, partnerid) + "'");
                countQuery.append(" and rm.partnerid='" + ESAPI.encoder().encodeForSQL(me, partnerid) + "'");
            }
            if (functions.isValueNull(fdtstamp))
            {
                logger.debug("fdate=="+fdtstamp);
                query.append(" and tokencreation_date>='" + fdtstamp + "'");
                countQuery.append(" and tokencreation_date>='" + fdtstamp + "'");
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and tokencreation_date<='" + tdtstamp + "'");
                countQuery.append(" and tokencreation_date<='" + tdtstamp + "'");
            }
            query.append(" ORDER BY rm.registration_id DESC limit " + paginationVO.getStart() + "," + paginationVO.getEnd());
            countQuery.append(" ORDER BY rm.registration_id DESC");

            ResultSet rsQry = Database.executeQuery(query.toString(), connection);
            ResultSet rsCount = Database.executeQuery(countQuery.toString(), connection);

            pstmtQry = connection.prepareStatement(query.toString());
            rsQry=pstmtQry.executeQuery();


            pstmtCount = connection.prepareStatement(countQuery.toString());
            rsCount = pstmtCount.executeQuery();

            logger.debug("Date from Registration History------>" + pstmtQry);
            logger.debug("record query==" + pstmtQry);

            while(rsQry.next())
            {
                tokenDetailsVO = new TokenDetailsVO();
                addressDetailsVO = new GenericAddressDetailsVO();
                logger.debug("inside if of resultsetdata==");
                tokenDetailsVO.setRegistrationId(rsQry.getString("registration_id"));
                tokenDetailsVO.setMemberId(rsQry.getString("memberid"));
                tokenDetailsVO.setRegistrationToken(rsQry.getString("registration_token"));
                addressDetailsVO.setFirstname(rsQry.getString("firstName"));
                addressDetailsVO.setLastname(rsQry.getString("lastName"));
                addressDetailsVO.setEmail(rsQry.getString("email"));
                tokenDetailsVO.setIsActive(rsQry.getString("isActive"));
                tokenDetailsVO.setDescription(rsQry.getString("merchant_orderid"));
                tokenDetailsVO.setToken(rsQry.getString("token"));
                tokenDetailsVO.setGeneratedBy(rsQry.getString("generatedBy"));
                tokenDetailsVO.setRegistrationGeneratedBy(rsQry.getString("regGeneratedBy"));
                tokenDetailsVO.setCreationOn(rsQry.getString("creationtime"));
                tokenDetailsVO.setTokenId(rsQry.getString("tokenid"));
                tokenDetailsVO.setBankAccountId(rsQry.getString("bank_accountid"));
                tokenDetailsVO.setPartnerId(rsQry.getString("partnerid"));
                tokenDetailsVO.setCardNum(rsQry.getString("cnum"));
                addressDetailsVO.setCustomerid(rsQry.getString("customerid"));
                addressDetailsVO.setCity(rsQry.getString("city"));
                addressDetailsVO.setState(rsQry.getString("state"));
                addressDetailsVO.setStreet(rsQry.getString("street"));
                addressDetailsVO.setCountry(rsQry.getString("country"));
                addressDetailsVO.setZipCode(rsQry.getString("zip"));
                addressDetailsVO.setTelnocc(rsQry.getString("telnocc"));
                addressDetailsVO.setPhone(rsQry.getString("telno"));
                tokenDetailsVO.setAddressDetailsVO(addressDetailsVO);

                if(rsCount.next())
                {
                    paginationVO.setTotalRecords(rsCount.getInt(1));
                    tokenDetailsVO.setPaginationVO(paginationVO);

                }
                tokenDetailsVOList.add(tokenDetailsVO);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getMerchantRegistrationList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getMerchantRegistrationList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return tokenDetailsVOList;
    }
    //new
    public Hashtable<String, String> getMerchantRegistrationCardLists(String memberId, String fdtstamp, String tdtstamp, String description, String firstName, String lastName, String email, int records, int pageno,String partnerid, String useraccname) throws PZDBViolationException
    {
        StringBuffer query = null;
        StringBuffer countQuery = null;
        Connection connection = null;
        ResultSet rs=null;

        Hashtable hash = new Hashtable();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Functions functions = new Functions();

        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;

        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
           // query = new StringBuffer("select from_unixtime(tokencreationon) as creationtime,tokenid,token,toid,terminalid,merchant_orderid,cardholder_firstname,cardholder_lastname,cardholderemail,isactive,partnerid,generatedBy from token_master where toid>0");
            query = new StringBuffer("SELECT DISTINCT FROM_UNIXTIME(tokencreationon) creationtime,tm.tokenid,tm.token,tm.toid,tm.terminalid,tm.merchant_orderid,tm.cardholder_firstname,tm.cardholder_lastname,cardholderemail,tm.isactive,tm.partnerid,tm.generatedBy,p.isTokenizationAllowed,m.isTokenizationAllowed FROM token_master AS tm, partners AS p, members AS m WHERE tm.partnerid=p.partnerId AND m.partnerId=p.partnerId AND p.isTokenizationAllowed ='Y' AND m.isTokenizationAllowed='Y' AND tm.toid>0");
           // countQuery = new StringBuffer("select count(*) from token_master where toid>0");
            countQuery = new StringBuffer("select count(DISTINCT tokenid) FROM token_master AS tm, partners AS p, members AS m WHERE tm.partnerid=p.partnerId AND m.partnerId=p.partnerId AND p.isTokenizationAllowed ='Y' AND m.isTokenizationAllowed='Y' AND tm.toid>0");

            if(functions.isValueNull(memberId))
            {
                query.append(" and tm.toid='" + ESAPI.encoder().encodeForSQL(me, memberId) + "'");
                countQuery.append(" and tm.toid='" + ESAPI.encoder().encodeForSQL(me, memberId) + "'");
            }
            if (functions.isValueNull(description))
            {
                query.append(" and tm.merchant_orderid='" + ESAPI.encoder().encodeForSQL(me, description) + "'");
                countQuery.append(" and tm.merchant_orderid='" + ESAPI.encoder().encodeForSQL(me, description) + "'");
            }
            if(functions.isValueNull(partnerid))
            {
                query.append(" and tm.partnerid='" + ESAPI.encoder().encodeForSQL(me, partnerid) + "'");
                countQuery.append(" and tm.partnerid='" + ESAPI.encoder().encodeForSQL(me, partnerid) + "'");
            }
            if (functions.isValueNull(firstName))
            {
                query.append(" and tm.cardholder_firstname='" + ESAPI.encoder().encodeForSQL(me, firstName) + "'");
                countQuery.append(" and tm.cardholder_firstname='" + ESAPI.encoder().encodeForSQL(me, firstName) + "'");
            }
            if (functions.isValueNull(lastName))
            {
                query.append(" and tm.cardholder_lastname='" + ESAPI.encoder().encodeForSQL(me, lastName) + "'");
                countQuery.append(" and tm.cardholder_lastname='" + ESAPI.encoder().encodeForSQL(me, lastName) + "'");
            }
            if (functions.isValueNull(email))
            {
                query.append(" and tm.cardholderemail='" + ESAPI.encoder().encodeForSQL(me, email) + "'");
                countQuery.append(" and tm.cardholderemail='" + ESAPI.encoder().encodeForSQL(me, email) + "'");
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and tm.tokencreationon>='" + fdtstamp + "'");
                countQuery.append(" and tm.tokencreationon>='" + fdtstamp + "'");
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and tm.tokencreationon<='" + tdtstamp + "'");
                countQuery.append(" and tm.tokencreationon<='" + tdtstamp + "'");
            }

            query.append(" ORDER BY FROM_UNIXTIME(tokencreationon) DESC limit " + start + "," + end);

            logger.debug("Date from Registration History------>"+query);
            logger.debug("count query from Registration History------>"+countQuery);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), connection));
            rs = Database.executeQuery(countQuery.toString(), connection);
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
        }
        catch (SystemError se)
        {
            logger.error("SystemError::::::" + se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getMerchantRegistrationCardList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::" + e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TokenDAO.java", "getMerchantRegistrationCardList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(connection);
        }
        return hash;
    }
    public EmiVO getEmiCountWithTerminalId(CommonValidatorVO commonValidatorVO)
    {
        Connection conn = null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        String memberid=commonValidatorVO.getMerchantDetailsVO().getMemberId();
        String terminalid=commonValidatorVO.getTerminalId();
        EmiVO emiVO=null;
        if(functions.isValueNull(memberid))
        {
            logger.debug("---inside--=-getDetails--");
            try
            {
                conn = Database.getConnection();
                //String sql = "select startdate ,enddate,emi_period from emi_configuration where memberId=?";
                String sql = "SELECT FROM_UNIXTIME(startdate,\"%Y-%m-%d %H:%i:%s\") AS startDate, FROM_UNIXTIME(enddate,\"%Y-%m-%d %H:%i:%s\") AS endDate,emi_period FROM emi_configuration WHERE memberId=? and terminalid=? and enable='Y'";

                ps = conn.prepareStatement(sql);
                ps.setString(1, memberid);
                ps.setString(2, terminalid);
                rs = ps.executeQuery();
                while (rs.next())
                {
                    emiVO=new EmiVO();
                    emiVO.setStartDate(rs.getString("startdate"));
                    emiVO.setEndDate(rs.getString("enddate"));
                    emiVO.setEmiPeriod(rs.getString("emi_period"));
                }
                logger.debug("query-----" + ps);
            }
            catch (SystemError se)
            {
                logger.error("SystemError-----", se);
            }
            catch (SQLException e)
            {
                logger.error("SQLException-----", e);
            }
            finally
            {
                Database.closeConnection(conn);
                Database.closePreparedStatement(ps);
                Database.closeResultSet(rs);
            }
        }
        return emiVO;
    }
    public TokenDetailsVO getTokenDetailsByUsingTrackingId(String toid,String trackingId)
    {
        TokenDetailsVO tokenDetailsVO=null;
        Connection con=null;
        PreparedStatement ps=null;
        ResultSet rs=null;

        try
        {
            con = Database.getConnection();
            String query="SELECT rm.`registration_id`,rm.`registration_token`,rm.`tokenid`,rm.isActive FROM `registration_master` rm ,`registration_member_mapping` rmm WHERE rm.`registration_id`=rmm.`registration_tokenid` AND rmm.`toid`=? AND rmm.`tracking_id`=?";
            ps=con.prepareStatement(query);
            ps.setString(1,toid);
            ps.setString(2,trackingId);
            logger.error("getTokenDetailsByUsingTrackingId ps-->"+ps);
            rs=ps.executeQuery();
            if(rs.next())
            {
                tokenDetailsVO=new TokenDetailsVO();
                tokenDetailsVO.setRegistrationId(rs.getString("registration_id"));
                tokenDetailsVO.setRegistrationToken(rs.getString("registration_token"));
                tokenDetailsVO.setTokenId(rs.getString("tokenid"));
                tokenDetailsVO.setIsActive(rs.getString("isActive"));
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError-----", se);
        }
        catch (SQLException e)
        {
            logger.error("SQLException-----", e);
        }
        finally
        {
            Database.closeConnection(con);
            Database.closePreparedStatement(ps);
            Database.closeResultSet(rs);
        }
        return tokenDetailsVO;
    }
    public boolean activate3DTransactionToken(TokenDetailsVO tokenDetailsVO)
    {
        Connection con=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        boolean isUpdated=false;
        try
        {
            con = Database.getConnection();
            con.setAutoCommit(false);
            String query="update token_master set isActive='Y' where tokenid=?";
            ps=con.prepareStatement(query);
            ps.setString(1,tokenDetailsVO.getTokenId());
            logger.error("token_master ps-->"+ps);
            int i=ps.executeUpdate();
            if(i>0)
            {
                String query2="UPDATE `registration_master` SET isActive='Y' WHERE `registration_id`=?";
                ps=con.prepareStatement(query2);
                ps.setString(1,tokenDetailsVO.getRegistrationId());
                logger.error("registration_master ps-->"+ps);
                ps.executeUpdate();
            }
            con.commit();
            isUpdated=true;
        }
        catch (SystemError se)
        {
            logger.error("SystemError-----", se);
        }
        catch (SQLException e)
        {
            logger.error("SQLException-----", e);
        }
        finally
        {
            Database.closeConnection(con);
            Database.closePreparedStatement(ps);
            Database.closeResultSet(rs);
        }
        return isUpdated;
    }

    public String deleteTokenFromRegistration(String token)
    {
        Connection con=null;
        PreparedStatement ps=null;
        String isDelete= null;
        try
        {
            con= Database.getConnection();
            String query="Delete from registration_master where registration_token=?";
            ps=con.prepareStatement(query);
            ps.setString(1,token);
            logger.error("DELETE QUERY ++++ "+ps);
            int k= ps.executeUpdate();
            if(k==1)
            {
                isDelete= "Record deleted successfully";
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError from deleteTokenFromRegistration:: ",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException from deleteTokenFromRegistration:: ",e);
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return isDelete;
    }
}