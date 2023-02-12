package com.payment.cupUPI;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.CurrencyCodeISO4217;
import com.payment.cupUPI.unionpay.acp.sdk.AcpService;
import com.payment.cupUPI.unionpay.acp.sdk.DemoBase;
import com.payment.cupUPI.unionpay.acp.sdk.SDKConfig;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Jitendra on 05-Jul-19.
 */
public class UnionPayInternationalUtils
{
    private static UnionPayInternationalLogger transactionLogger= new UnionPayInternationalLogger(UnionPayInternationalLogger.class.getName());
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.UnionPayInt");

    public Boolean checkBinFromFile(String bin)
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean checkBin_Result = false;

        try
        {
            con = Database.getConnection();
            String sql = "select bin from bin_upi_details where bin=?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, bin);
            transactionLogger.error("checkBinFromFile query ---->" + pstmt);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                checkBin_Result = true;
            }
        }
        catch (SQLException e)
        {
          transactionLogger.error("SQLException While checkBinFromFile --->"+e);
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError While checkBinFromFile --->" + se);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return checkBin_Result;
    }

    public Boolean isEnrolledCard(String cardNumber,String phoneNumber) throws PZDBViolationException
    {
        Connection con = null;
        ResultSet rs=null;
        PreparedStatement pstmt=null;

        boolean isEnrolledCard = false;
        try
        {
            con = Database.getConnection();
            String sql = "select card_number,phone_number from bin_upi_enrollment where card_number=? and phone_number=?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, cardNumber);
            pstmt.setString(2, phoneNumber);
            transactionLogger.error("isEnrolledCard query ---" + pstmt);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                isEnrolledCard = true;
            }
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException While isEnrolledCard --->"+e);
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError While isEnrolledCard --->" + se);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
        return isEnrolledCard;
    }

    public boolean isValidTicket(String memberId, String terminalId, String amount) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isValidTicket = false;
        try
        {
            con = Database.getConnection();
            String sql = "select id from member_amount_mapping where memberid=? and terminalid=? and amount=?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.setString(2, terminalId);
            pstmt.setString(3, amount);
            transactionLogger.error("isValidTicket query ---" + pstmt);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                isValidTicket = true;
            }
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException While isValidTicket --->" + e);
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError While isValidTicket --->" + se);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
        return isValidTicket;
    }

    public static String insertCardForEnrollment(String cardNumber,String phoneNumber) throws SystemError, SQLException
    {
        Connection conn=null;
        PreparedStatement ps=null;
        String queryResult = "";

        try
        {
            conn =Database.getConnection();
            String qry = "insert into bin_upi_enrollment (card_number,phone_number) values(?,?)";
            ps = conn.prepareStatement(qry);
            ps.setString(1,cardNumber);
            ps.setString(2,phoneNumber);
            transactionLogger.error("insertCardForEnrollment query ---" + ps);
            int num =ps.executeUpdate();
            if (num == 1)
            {
                queryResult = "success";
            }
            else
            {
                queryResult = "failed";
            }
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException While insertCardForEnrollment --->" + e);
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError While insertCardForEnrollment --->" + se);
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return  queryResult;
    }

    public String getSecurePayhtml(CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("Inside getSecurePayhtml --->");
        Functions functions=new Functions();
        String accountId=commonValidatorVO.getMerchantDetailsVO().getAccountId();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String txnType="01";
        String orderId="000"+commonValidatorVO.getTrackingid();
        transactionLogger.error("orderId -------->"+orderId);
        String isService=commonValidatorVO.getMerchantDetailsVO().getIsService();
        String txnAmt="";
        if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getAmount()))
        {
            txnAmt=getCentAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        }
        if (isService.equalsIgnoreCase("N"))
        {
            txnType="02";
        }
        transactionLogger.error("txnType ---" + txnType);
        String merId =  gatewayAccount.getMerchantId();
        String txnTime =getCurrentTime();
        String currencyId = CurrencyCodeISO4217.getNumericCurrencyCode(commonValidatorVO.getTransDetailsVO().getCurrency());

        String propertyPath = RB.getString("PROPERTY_PATH");
        transactionLogger.error("propertyPath ------->"+propertyPath);
        SDKConfig.getConfig().loadPropertiesFromPath(propertyPath);
        transactionLogger.error("Property loaded");
        Map<String, String> contentData = new HashMap<String, String>();
        contentData.put("version", DemoBase.version);
        contentData.put("encoding", DemoBase.encoding);
        contentData.put("signMethod", SDKConfig.getConfig().getSignMethod());
        contentData.put("txnType",txnType);
        contentData.put("txnSubType", "01");
        contentData.put("bizType", "000201");
        contentData.put("accessType", "1");
        contentData.put("channelType", "07");
        contentData.put("acqInsCode", "38990826");
        contentData.put("merCatCode", "4511");
        contentData.put("merName", "OneStopMoneyManager");
        contentData.put("merAbbr", "OSMM");
        contentData.put("merId", merId);
        contentData.put("orderId", orderId);
        contentData.put("txnTime", txnTime);
        contentData.put("currencyCode",currencyId);
        contentData.put("txnAmt", txnAmt);
        contentData.put("payTimeout", "");
        transactionLogger.error("Before back url -----");
        contentData.put("backUrl", RB.getString("acpsdk_backUrl")+commonValidatorVO.getTrackingid());
        contentData.put("frontUrl", RB.getString("acpsdk_frontUrl")+commonValidatorVO.getTrackingid());
        transactionLogger.error("after back url -------");

        Map<String, String> submitFromData = AcpService.sign(contentData,DemoBase.encoding);
        transactionLogger.error("Request for SecurePay " + submitFromData);

       // String requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl();
        String requestFrontUrl="";
        if (isTest)
        {
           // requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl();
            requestFrontUrl=RB.getString("acpsdk_frontTransUrl_Test");
        }
        else
        {
            //requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl();
            requestFrontUrl=RB.getString("acpsdk_frontTransUrl_Live");
        }

        transactionLogger.error("requestFrontUrl ---"+requestFrontUrl);
        String html = AcpService.createAutoFormHtml(requestFrontUrl, submitFromData,DemoBase.encoding);

        transactionLogger.error("Print the request HTML, which is a request message and the basis for problem joint debugging and troubleshooting"+html);
        return  html;
    }

    public static String getCentAmount(String amount)
    {
        BigDecimal bigDecimal = new BigDecimal(amount);
        bigDecimal = bigDecimal.multiply(new BigDecimal(100));
        Long newAmount = bigDecimal.longValue();
        return newAmount.toString();
    }

    public String getCurrentTime()
    {
        DateFormat dateFormat = new SimpleDateFormat("YYYYMMddHHmmss");
        Date date = new Date();
        String time="";
        time=String.valueOf(dateFormat.format(date));
        return time;
    }

    public HashMap getTransactionStatus( String trackingId)
    {
        HashMap<String,String> hp= new HashMap();
        String dbStatus="";
        String transactionTime="";
        TransactionManager transactionManager = new TransactionManager();
        TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
        if (transactionDetailsVO != null)
        {
            dbStatus = transactionDetailsVO.getStatus();
            transactionTime=transactionDetailsVO.getTransactionTime();
        }
        hp.put("dbStatus",dbStatus);
        hp.put("transactionTime",transactionTime);

        return hp;
    }


}
