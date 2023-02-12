package com.payment.epay;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.vo.CommonValidatorVO;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.ResourceBundle;

/**
 * Created by Admin on 11/3/17.
 */
public class EpayPaymentProcess extends CommonPaymentProcess
{
    public static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.epay");
    private static TransactionLogger transactionLogger= new TransactionLogger(EpayPaymentProcess.class.getName());

    public static String calculateRFC2104HMAC(String data, String key)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException
    {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return toHexString(mac.doFinal(data.getBytes()));
    }

    public static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    public CommCardDetailsVO getCustomerAccountDetails(String previousTransTrackingId) throws PZDBViolationException
    {
        transactionLogger.error(":::::enter into EpayPaymentProcess:::::");
        CommCardDetailsVO commCardDetailsVO=null;
        Connection conn=null;
        PreparedStatement pstmt=null;
        try{
            conn= Database.getConnection();
            String query="SELECT cin FROM transaction_epay_details WHERE trackingid=?";
            transactionLogger.debug("query:::::"+query);
            pstmt=conn.prepareStatement(query);
            pstmt.setString(1,previousTransTrackingId);
            ResultSet rs=pstmt.executeQuery();
            if(rs.next()){
                commCardDetailsVO= new CommCardDetailsVO() ;
                commCardDetailsVO.setAccountNumber(rs.getString("cin"));
            }
        }catch(SystemError error){
            transactionLogger.error("SystemError::::::",error);
        }
        catch(SQLException e){
            transactionLogger.error("SystemError::::::",e);
        } finally{
            Database.closeConnection(conn);
        }
        return  commCardDetailsVO;
    }

    public CommonValidatorVO getExtentionDetails(CommonValidatorVO commonValidatorVO)
    {
        Connection connection = null;
        HashMap hashMap = new HashMap();
        Functions functions = new Functions();
        try
        {
            connection = Database.getConnection();
            String query = "SELECT customerId,cemail,cin FROM transaction_epay_details WHERE trackingid=?";
            PreparedStatement p = connection.prepareStatement(query);
            p.setString(1, commonValidatorVO.getTrackingid());
            ResultSet rs = p.executeQuery();
            if(rs.next())
            {
                if(functions.isValueNull(rs.getString("customerId")))
                    commonValidatorVO.setCustomerId(rs.getString("customerId"));
                if(functions.isValueNull(rs.getString("cemail")))
                    commonValidatorVO.getAddressDetailsVO().setEmail(rs.getString("cemail"));
                if(functions.isValueNull(rs.getString("cin")))
                    commonValidatorVO.setCustomerBankId(rs.getString("cin"));
            }
            transactionLogger.debug("detail table for transaction_epay_details---"+p);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("System error",systemError);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return commonValidatorVO;
    }

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.debug("inside epay===");
        AsyncParameterVO asyncParameterVO = null;
        String flowLayout = "multi_page";
        Hashtable dataHash = new Hashtable();

        String URL = "";
        String trackingid = commonValidatorVO.getTrackingid();
        String gatewayMid = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getMerchantId();
        String userName = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getFRAUD_FTP_USERNAME();
        String secretKey = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getFRAUD_FTP_PASSWORD();
        String amount = commonValidatorVO.getTransDetailsVO().getAmount();
        String currency = commonValidatorVO.getTransDetailsVO().getCurrency();
        String customerId = commonValidatorVO.getAddressDetailsVO().getCustomerid();
        String cutomerEmail = commonValidatorVO.getAddressDetailsVO().getEmail();

        String testURL = RB.getString("TEST_SALE_URL");
        String livetURL = RB.getString("LIVE_SALE_URL");

        boolean isTest = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).isTest();

        if (isTest)
        {
            transactionLogger.error(":::::inside isTest:::::");
            URL = testURL;
        }
        else
        {
            transactionLogger.error(":::::inside Live:::::");
            URL = livetURL;
        }

        try
        {


            String saleRequest = "INVOICE=" + trackingid + "\nAMOUNT=" + amount + "\nCURRENCY=" + currency + "\nMIN=" + gatewayMid + "\nMEMAIL=" + userName + "\nCIN=" + customerId + "\nCEMAIL=" + cutomerEmail + "\nEXP_TIME="+EpayUtils.expDate(RB.getString("NO_OF_DAYS"))+"";
        transactionLogger.error("saleRequest:::::"+saleRequest);

        String encoded = new String(org.apache.commons.codec.binary.Base64.encodeBase64((saleRequest).getBytes()));
        transactionLogger.error("encoded:::::"+encoded);

        String checksum = calculateRFC2104HMAC(encoded, secretKey);
        transactionLogger.error("checksum:::::"+checksum);


            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("pay_form");
            asyncParameterVO.setValue(URL);
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("PAGE");
            asyncParameterVO.setValue("paylogin");
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("ENCODED");
            asyncParameterVO.setValue(encoded);
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("CHECKSUM");
            asyncParameterVO.setValue(checksum);
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("URL_OK");
            asyncParameterVO.setValue(RB.getString("URL_OK")+trackingid);
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("URL_CANCEL");
            asyncParameterVO.setValue(RB.getString("URL_CANCEL")+trackingid);
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            directKitResponseVO.setBankRedirectionUrl(URL);


        }
        catch (Exception e)
        {
            transactionLogger.error("Exception----",e);
            //e.printStackTrace();
        }
        return directKitResponseVO;
    }




}
