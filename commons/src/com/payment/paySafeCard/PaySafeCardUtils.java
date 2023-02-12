package com.payment.paySafeCard;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.payment.common.core.*;
import com.payment.paySafeCard.pscservice.PSCSOAPClient;
import com.payment.validators.vo.CommonValidatorVO;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/15/15
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaySafeCardUtils
{   private static Logger log = new Logger(PaySafeCardUtils.class.getName());
    public CommRequestVO getRequestDataForDabit(String trackingId,String paymentId)
    {
        CommRequestVO commRequestVO = new CommRequestVO();
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();
        String email=null;
        String amount=null;
        String currency=null;
        String accountId=null;

        try
        {
            connection= Database.getConnection();
            String selectStmt="SELECT emailaddr,accountid,amount,currency FROM transaction_common WHERE trackingid=?";
            preparedStatement= connection.prepareStatement(selectStmt);
            preparedStatement.setString(1,trackingId);
            resultSet=preparedStatement.executeQuery();
            if(resultSet.next())
            {
                email=resultSet.getString("emailaddr");
                accountId = resultSet.getString("accountid");
                amount = resultSet.getString("amount");
                currency = resultSet.getString("currency");
            }

            log.error(email);
            addressDetailsVO.setEmail(email);
            transDetailsVO.setAmount(amount);
            transDetailsVO.setCurrency(currency);
            transDetailsVO.setDetailId(paymentId);
            commMerchantVO.setMerchantKey(accountId);
            commRequestVO.setCommMerchantVO(commMerchantVO);
            commRequestVO.setAddressDetailsVO(addressDetailsVO);
            commRequestVO.setTransDetailsVO(transDetailsVO);
        }
        catch (SystemError systemError)
        {
            log.error("SystemError getRequestDataForDabit---",systemError);  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (SQLException e)
        {
            log.error("SQLException getRequestDataForDabit---", e);  //To change body of catch statement use File | Settings | File Templates.
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeResultSet(resultSet);
            Database.closeConnection(connection);
        }
        return commRequestVO;
    }

    public HashMap<String, String> setRefundTransData(GenericRequestVO pzRefundRequest)
    {
        HashMap<String, String> transaction = new HashMap<String, String>();
        DecimalFormat df = new DecimalFormat("#.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        Date today = new Date();
        DateFormat format = new SimpleDateFormat("ddMMyyyy");

        String rTId="refund_"+format.format(today)+"_"+((int) (Math.random() * 9999 + 1));
        int mTId=0/*pzRefundRequest.getTrackingId()*/;
        String amount="0.00"/*pzRefundRequest.getRefundAmount()*/;
        String currency="USD";
        String utcOffset="+01:00";

        transaction.put("rtid", rTId);
        transaction.put("mtid", String.valueOf(mTId));
        transaction.put("amount", df.format(Double.parseDouble(amount)));
        transaction.put("currency", currency.toUpperCase());
        transaction.put("utcOffset", utcOffset);
        transaction.put("customerIdType", "EMAIL");
        transaction.put("customerId", null/*customerId*/); //emailAddr
        transaction.put("merchantClientId", null/*merchantClientId*/);

        return transaction;
    }

    public CommResponseVO refundResult(PSCSOAPClient client) throws Exception
    {
        HashMap<String, String> refundResult=new HashMap<String, String>();
        CommResponseVO genericResponseVO=new CommResponseVO();

        genericResponseVO.setStatus(client.getElement("errorCode").equals("0")&& client.getElement("resultCode").equals("0") ? "success" : "fail");
        genericResponseVO.setErrorCode(client.getElement("errorCode"));
        genericResponseVO.setDescription(client.getElement("errorCodeDescription"));

        refundResult.put("rtid", client.getElement("rtid"));
        refundResult.put("mtid", client.getElement("mtid"));
        refundResult.put("validationOnly", client.getElement("validationOnly"));

        refundResult.put("requestedCurrency", client.getElement("requestedCurrency"));
        refundResult.put("requestedAmount", client.getElement("requestedAmount"));
        refundResult.put("refundedInCurrency", client.getElement("refundedInCurrency"));
        refundResult.put("refundedAmount", client.getElement("refundedAmount"));

        refundResult.put("resultCode", client.getElement("resultCode"));
        refundResult.put("errorCode", client.getElement("errorCode"));
        refundResult.put("errorCodeDescription", client.getElement("errorCodeDescription"));

        return genericResponseVO;
    }

    public HashMap getRequestDataForPayout(String trackingId)
    {
        Connection connection=null;
        HashMap  hashMap=new HashMap();

        try
        {
            connection= Database.getConnection();
            String selectStmt="SELECT birthdate,firstname,lastname,emailid FROM transaction_paysafecard_detils WHERE trackingid=?";
            PreparedStatement preparedStatement= connection.prepareStatement(selectStmt);
            preparedStatement.setString(1,trackingId);
            ResultSet resultSet=preparedStatement.executeQuery();
            if(resultSet.next())
            {
                hashMap.put("birthdate",resultSet.getString("birthdate"));
                hashMap.put("firstname", resultSet.getString("firstname"));
                hashMap.put("lastname",resultSet.getString("lastname"));
                hashMap.put("emailid",resultSet.getString("emailid"));
            }

        }
        catch (SystemError systemError)
        {
            log.error("SystemError---->",systemError);  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (SQLException e)
        {
            log.error("SQLException---->", e);  //To change body of catch statement use File | Settings | File Templates.
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return hashMap;
    }

    public CommRequestVO getPaySafeRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();

        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String merctId = account.getMerchantId();
        String username = account.getFRAUD_FTP_USERNAME();
        String password = account.getFRAUD_FTP_PASSWORD();
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setBirthdate(commonValidatorVO.getAddressDetailsVO().getBirthdate());
        addressDetailsVO.setSex(commonValidatorVO.getAddressDetailsVO().getSex());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        addressDetailsVO.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());
        transDetailsVO.setRedirectUrl(commonValidatorVO.getTransDetailsVO().getRedirectUrl());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount()); //Amount * 100 according to the docs
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTrackingid());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setCardType(commonValidatorVO.getPaymentBrand());

        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        merchantAccountVO.setMerchantId(merctId);
        merchantAccountVO.setPassword(password);
        merchantAccountVO.setMerchantUsername(username);
        merchantAccountVO.setDisplayName(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        merchantAccountVO.setAliasName(commonValidatorVO.getMerchantDetailsVO().getSiteName());
        merchantAccountVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());

        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);

        return commRequestVO;
    }
}
