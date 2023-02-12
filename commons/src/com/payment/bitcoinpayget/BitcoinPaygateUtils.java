package com.payment.bitcoinpayget;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.vo.ReserveField2VO;
import com.payment.PayMitco.core.PayMitcoResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Jitendra on 19-Jun-19.
 */
public class BitcoinPaygateUtils
{
    static TransactionLogger transactionLogger=new TransactionLogger(BitcoinPaygateUtils.class.getName());
    private static Functions functions = new Functions();

    public String getRedirectForm(String trackingId, CommResponseVO response3D)
    {
        transactionLogger.error("redirect url---" + response3D.getRedirectUrl());
        transactionLogger.error("trackingid inside Bitcoin utils-------"+trackingId);
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(response3D.getRedirectUrl()).append("\" method=\"get\">\n");
      //  html.append("<input type=\"hidden\" name=\"transactionID\" value=\"" + response3D.getTransactionId() + "\">");
        html.append("</form>");

        transactionLogger.error("form----"+html.toString());
        return html.toString();
    }

    public static CommRequestVO getCommRequestFromUtils(CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("Inside BitcoinPaygateUtils -----");
        CommRequestVO commRequestVO = new CommRequestVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }

    public static String doPostHTTPSURLConnectionClient(String strURL, String req, String key) throws PZTechnicalViolationException
    {
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Authorization", "BASIC" + " " + key);
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Accept", "application/json");
            post.addRequestHeader("Cache-Control", "no-cache");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException--" + he.getMessage());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException--" + io.getMessage());
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String doGetHttpConnection(String url,String key)
    {
        String response="";
        HttpClient httpClient= new HttpClient();
        try
        {
            GetMethod getMethod= new GetMethod(url);
            getMethod.addRequestHeader("Authorization","BASIC" + " " + key);
            httpClient.executeMethod(getMethod);
            response=new String (getMethod.getResponseBody());
        }
        catch (IOException e)
        {
            transactionLogger.error("---Exception---"+e);
        }
        return response;
    }

    public static void updateResponseHashInfo(CommResponseVO commResponseVO, String trackingid) throws PZDBViolationException
    {
        Connection connection = null;

        PreparedStatement preparedStatement = null;
        try
        {
            connection = Database.getConnection();
            String updateRecord = "UPDATE transaction_common_details SET responsehashinfo=? WHERE trackingid=?";
            preparedStatement = connection.prepareStatement(updateRecord);
            preparedStatement.setString(1, commResponseVO.getResponseHashInfo());
            preparedStatement.setString(2, trackingid);
            int i = preparedStatement.executeUpdate();

            transactionLogger.error("updated -------" + i);
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

    public HashMap getResponsehashinfo(String trackingId)
    {
        HashMap hashMap = new HashMap();
        Connection connection = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        try
        {
            connection = Database.getConnection();
            String query = "SELECT responsehashinfo FROM transaction_common_details WHERE trackingid=? and status='authstarted'";
            p = connection.prepareStatement(query);
            p.setString(1, trackingId);
            rs = p.executeQuery();
            if (rs.next())
            {
                if (functions.isValueNull(rs.getString("responsehashinfo")))
                {
                    hashMap.put("responsehashinfo", rs.getString("responsehashinfo"));
                }
               /* if (functions.isValueNull(rs.getString("cin")))
                {
                    hashMap.put("customerBankId", rs.getString("cin"));
                }
                if (functions.isValueNull(rs.getString("customerId")))
                {
                    hashMap.put("customerId", rs.getString("customerId"));
                }*/
            }
            transactionLogger.error("getResponsehashinfo---" + p);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("System error", systemError);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException", e);
        }
        finally
        {
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
            Database.closeConnection(connection);
        }
        return hashMap;
    }

    public static int insertDetailTableEntry(String trackingId,BitcoinPaygateResponseVO bitcoinPaygateResponseVO) throws PZDBViolationException
    {
        transactionLogger.debug("---Entering insertDetailTableEntry for Bitcoin ---");
        Functions functions=new Functions();
        String amount="";
        String amount_btc="";
        String currency="";
        String currency_btc="";
        String exchange_rate="";
        String paid="";
        String paid_btc="";
        String payment_id="";
        String remaining_to_pay="";
        String remaining_to_pay_btc="";
        String resp_status="";
        // String pz_transaction_status=bitcoinPaygateResponseVO.getPz_transaction_status();
        String fiat_amount="";
        String payoutid="";

        if(bitcoinPaygateResponseVO != null  && !bitcoinPaygateResponseVO.equals(""))
        {
            amount=bitcoinPaygateResponseVO.getAmount();
            transactionLogger.debug("amount ---"+amount);
            amount_btc=bitcoinPaygateResponseVO.getAmount_btc();
            transactionLogger.debug("amount_btc ---"+amount_btc);
            currency=bitcoinPaygateResponseVO.getCurrency();
            transactionLogger.debug("currency ---"+currency);
            currency_btc=bitcoinPaygateResponseVO.getCurrency_btc();
            transactionLogger.debug("currency_btc ---"+currency_btc);
            exchange_rate=bitcoinPaygateResponseVO.getExchange_rate();
            transactionLogger.debug("exchange_rate ---"+exchange_rate);
            paid=bitcoinPaygateResponseVO.getPaid();
            transactionLogger.debug("paid ---"+paid);
            paid_btc=bitcoinPaygateResponseVO.getPaid_btc();
            transactionLogger.debug("paid_btc ---"+paid_btc);
            payment_id=bitcoinPaygateResponseVO.getPayment_id();
            transactionLogger.debug("payment_id ---"+payment_id);
            remaining_to_pay=bitcoinPaygateResponseVO.getRemaining_to_pay();
            remaining_to_pay_btc=bitcoinPaygateResponseVO.getRemaining_to_pay_btc();
            resp_status=bitcoinPaygateResponseVO.getResp_status();
           //pz_transaction_status=bitcoinPaygateResponseVO.getPz_transaction_status();
            fiat_amount=bitcoinPaygateResponseVO.getFiat_amount();
            payoutid=bitcoinPaygateResponseVO.getPayoutid();
            transactionLogger.debug("payoutid ---"+payoutid);
        }
        Connection conn = null;
        int k=0;
        try
        {
            conn = Database.getConnection();
            String sql = "insert into transaction_bitcoinpaygate_details(trackingid,amount,amount_btc,currency,currency_btc,exchange_rate,paid,paid_btc,payment_id,remaining_to_pay,remaining_to_pay_btc,resp_status,fiat_amount,payoutid) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            pstmt.setString(2,amount);
            pstmt.setString(3, amount_btc);
            pstmt.setString(4, currency);
            pstmt.setString(5,currency_btc);
            pstmt.setString(6,exchange_rate);
            pstmt.setString(7,paid);
            pstmt.setString(8,paid_btc);
            pstmt.setString(9,payment_id);
            pstmt.setString(10,remaining_to_pay);
            pstmt.setString(11,remaining_to_pay_btc);
            pstmt.setString(12,resp_status);
            pstmt.setString(13,fiat_amount);
            pstmt.setString(14,payoutid);
            transactionLogger.error("pstmt------------>"+pstmt);
            k = pstmt.executeUpdate();

        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(BitcoinPaygateUtils.class.getName(),"insertDetailTableEntry()",null,"common","Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(BitcoinPaygateUtils.class.getName(), "insertDetailTableEntry()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return k;
    }

}
