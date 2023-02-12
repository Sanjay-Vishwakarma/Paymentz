package com.payment.FlutterWave;

import com.directi.pg.Database;
import com.directi.pg.FlutterWaveLogger;
import com.directi.pg.SystemError;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jitendra on 19-Apr-19.
 */
public class FlutterWaveUtils
{
    private static FlutterWaveLogger transactionLogger = new FlutterWaveLogger(FlutterWaveUtils.class.getName());

    public static String toHexStr(byte[] bytes)
    {
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < bytes.length; i++ )
        {
            builder.append(String.format("%02x", bytes[i]));
        }

        return builder.toString();
    }

    public static HashMap<String,String> getTerminalConversionDetails(String terminalID)
    {
        Connection con=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        HashMap<String,String> hashMap=null;

        try
        {
            con=Database.getConnection();
            String query="SELECT currency_conversion,conversion_currency FROM member_account_mapping WHERE terminalid=?";
            ps=con.prepareStatement(query);
            ps.setString(1,terminalID);
            rs=ps.executeQuery();
            transactionLogger.error("Query---"+ps);
            if(rs.next())
            {
                hashMap= new HashMap<>();
                hashMap.put("currency_conversion", rs.getString("currency_conversion"));
                hashMap.put("conversion_currency",rs.getString("conversion_currency"));
            }
        }catch (Exception e)
        {
            transactionLogger.error("Exception in getTerminalConversionDetails---",e);
        }
        finally
        {
            Database.closeConnection(con);
            Database.closePreparedStatement(ps);
            Database.closeResultSet(rs);
        }
        return hashMap;
    }

    public static String makeCurrencyConversion(String from_currency,String to_currency,String from_amount)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet=null;
        Double exchangeRate=null;
        Double converted_currency=null;
        try
        {
            conn = Database.getConnection();
            String query = "select exchange_rate from currency_exchange_rates where from_currency=? and to_currency=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,from_currency);
            stmt.setString(2,to_currency);
            resultSet=stmt.executeQuery();
            if(resultSet.next()){
                exchangeRate=resultSet.getDouble("exchange_rate");
                transactionLogger.error("Exchange rate-----"+exchangeRate);
                transactionLogger.error("Transaction request amount-----"+from_amount);
                transactionLogger.error("Transaction request currency-----"+from_currency);
            }
            converted_currency = Double.valueOf(from_amount) * exchangeRate;
        }
        catch(SystemError se){
            transactionLogger.error("SystemError::::::",se);
        }
        catch(SQLException e){
            transactionLogger.error("SQLException::::::",e);
        }
        finally{
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        return String.valueOf(Math.round(converted_currency * 100.0) / 100.0);
    }


    public static String getKey(String seedKey)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] hashedString = md.digest(seedKey.getBytes("utf-8"));
            byte[] subHashString = toHexStr(Arrays.copyOfRange(hashedString, hashedString.length - 12, hashedString.length)).getBytes("utf-8");
            String subSeedKey = seedKey.replace("FLWSECK-", "");
            subSeedKey = subSeedKey.substring(0, 12);   // subSeedKey is the key
            byte[] combineArray = new byte[24];
            System.arraycopy(subSeedKey.getBytes(), 0, combineArray, 0, 12);
            System.arraycopy(subHashString, subHashString.length - 12, combineArray, 12, 12);
            return new String(combineArray);
        }
        catch (NoSuchAlgorithmException ex)
        {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        }
        catch (UnsupportedEncodingException ex)
        {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String encryptData(String message, String _encryptionKey)
    {
        try
        {
            final byte[] digestOfPassword = _encryptionKey.getBytes("utf-8");
            final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

            final SecretKey key = new SecretKeySpec( keyBytes , "DESede");
            final Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final byte[] plainTextBytes = message.getBytes("utf-8");
            final byte[] cipherText = cipher.doFinal(plainTextBytes);
            return Base64.getEncoder().encodeToString(cipherText);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception :::::::::::" , e);
            return "";
        }
    }

    public static String doPostHTTPSURLConnectionClient(String strURL,String req) throws PZTechnicalViolationException
    {
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException :::::::::::",he);
        }
        catch (IOException io){
            transactionLogger.error("IOException :::::::::::",io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public String getActionExecutorName(String trackingId)
    {
        String actionExecutorName="";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try
        {
            con = Database.getConnection();
            String sql = "select actionexecutorname from transaction_common_details where status='3D_authstarted' and trackingid=?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            transactionLogger.error("getActionExecutorName query ---" + pstmt);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                actionExecutorName=rs.getString("actionexecutorname");
            }
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException While getActionExecutorName --->" + e);
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError While getActionExecutorName --->" + se);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
        return actionExecutorName;
    }
    public static String getCentAmount(String amount)
    {
        BigDecimal bigDecimal = new BigDecimal(amount);
        bigDecimal = bigDecimal.multiply(new BigDecimal(100));
        Long newAmount = bigDecimal.longValue();
        return newAmount.toString();
    }
    public CommRequestVO getFlutterPaymentRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = new CommRequestVO();
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());

        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCustomerBankId(commonValidatorVO.getProcessorName());
        transDetailsVO.setCardType(commonValidatorVO.getCardType());
        commRequestVO.setCustomerId(commonValidatorVO.getVpa_address());
        cardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
        cardDetailsVO.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());
        cardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());
        cardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }
}
