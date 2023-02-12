package com.payment.Ecospend;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Admin on 30-Aug-21.
 */
public class EcospendUtils
{
    static TransactionLogger transactionLogger  = new TransactionLogger(EcospendUtils.class.getName());

    public static String getAccessToken(String strURL, String request,String requestId) throws PZTechnicalViolationException
    {
        String result           = "";
        HttpClient httpClient   = new HttpClient();
        PostMethod post         = new PostMethod(strURL);
        transactionLogger.error("EcospendUtils:: HttpException-----"+ request);


        try
        {
            post.addRequestHeader("X-Request-ID", requestId);
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("EcospendUtils::getAccessToken HttpException-----", he);
            PZExceptionHandler.raiseTechnicalViolationException(EcospendUtils.class.getName(), "getAuthentication()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("EcospendUtils::getAccessToken IOException-----", io);
            PZExceptionHandler.raiseTechnicalViolationException(EcospendUtils.class.getName(), "getAuthentication()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String doPostHTTPSURLConnectionClient(String strURL, String request,String authToken) throws PZTechnicalViolationException
    {
        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        transactionLogger.error("authToken::::::::::::::::::"+authToken);

        try
        {
            post.addRequestHeader("Authorization", "bearer "+authToken+"");
            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("EcospendUtils:: HttpException-----", he);
            PZExceptionHandler.raiseTechnicalViolationException(EcospendUtils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("EcospendUtils:: IOException-----", io);
            PZExceptionHandler.raiseTechnicalViolationException(EcospendUtils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String doGetHTTPSURLConnectionClient(String strURL,String authToken) throws PZTechnicalViolationException
    {
        String result = "";
        HttpClient httpClient = new HttpClient();
        GetMethod get = new GetMethod(strURL);
        transactionLogger.error("authToken::::::::::::::::::"+authToken);

        try
        {
            get.addRequestHeader("Authorization", "bearer " + authToken + "");
            httpClient.executeMethod(get);
            String response = new String(get.getResponseBody());
            result          = response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("EcospendUtils:: HttpException-----", he);
            PZExceptionHandler.raiseTechnicalViolationException(EcospendUtils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("EcospendUtils:: IOException-----", io);
            PZExceptionHandler.raiseTechnicalViolationException(EcospendUtils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            get.releaseConnection();
        }
        return result;
    }

    public static String GETHTTPSURLConnectionClientBanks(String strURL,String qryStr,String authToken) throws PZTechnicalViolationException
    {
        String result = "";
        HttpClient httpClient = new HttpClient();
        GetMethod getMethod = new GetMethod(strURL);

        try
        {
            getMethod.addRequestHeader("Authorization", "bearer "+authToken+"");
            getMethod.setRequestHeader("Accept", "application/json");
            getMethod.setRequestHeader("Cache-Control", "no-cache");
            getMethod.setQueryString(qryStr);

            int statusCode = httpClient.executeMethod(getMethod);

            if (statusCode != HttpStatus.SC_OK)
            {
                transactionLogger.error("Method failed: " + getMethod.getStatusLine());
            }

            byte[] response = getMethod.getResponseBody();
            result = new String(response);
        }
        catch (Exception e)
        {
            transactionLogger.error("EcospendUtils:: HttpException-----", e);
            PZExceptionHandler.raiseTechnicalViolationException(EcospendUtils.class.getName(), "GETHTTPSURLConnectionClientBanks()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            getMethod.releaseConnection();
        }
        return result;
    }

    public static String doPostHTTPSURLConnectionClientBankId(String strURL, String request,String authToken) throws PZTechnicalViolationException
    {
        String result           = "";
        HttpClient httpClient   = new HttpClient();
        GetMethod getMethod     = new GetMethod(strURL);

        try
        {
            getMethod.addRequestHeader("Authorization", "bearer "+authToken+"");
            getMethod.setRequestHeader("Accept", "application/json");
            getMethod.setRequestHeader("Cache-Control", "no-cache");
            getMethod.setPath(request);
            httpClient.executeMethod(getMethod);
            String response = new String(getMethod.getResponseBody());
            result = response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("AppleTreeCellulantUtils:: HttpException-----", he);
            PZExceptionHandler.raiseTechnicalViolationException(EcospendUtils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("AppleTreeCellulantUtils:: IOException-----", io);
            PZExceptionHandler.raiseTechnicalViolationException(EcospendUtils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            getMethod.releaseConnection();
        }
        return result;
    }

    public EcospendRequestVo getEcospendrequestVo(TransactionDetailsVO transactionDetailsVO,CommAddressDetailsVO commAddressDetailsVO,String phoneNumberNew,String Accountid,EcospendRequestVo ecospendRequestVo,CommTransactionDetailsVO transDetailsVO1,EcospendGatewayAccountVO gatewayAccountVO)
    {
        EcospendRequestVo ecospendRequestVo1    = null;
        Functions functions                     = new Functions();
        CommAddressDetailsVO addressDetailsVO   = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO merchantAccountVO        = new CommMerchantVO();

        addressDetailsVO.setFirstname(transactionDetailsVO.getFirstName());
        addressDetailsVO.setLastname(transactionDetailsVO.getLastName());
        addressDetailsVO.setCity(transactionDetailsVO.getCity());
        addressDetailsVO.setCountry(transactionDetailsVO.getCountry());
        addressDetailsVO.setTelnocc(ecospendRequestVo.getPhonecc());
        addressDetailsVO.setIp(transactionDetailsVO.getIpAddress());
        addressDetailsVO.setPhone(phoneNumberNew);
        addressDetailsVO.setEmail(ecospendRequestVo.getEmail());
        addressDetailsVO.setState(transactionDetailsVO.getState());
        addressDetailsVO.setStreet(transactionDetailsVO.getStreet());
        addressDetailsVO.setZipCode(transactionDetailsVO.getZip());
        addressDetailsVO.setCustomerid(transactionDetailsVO.getCustomerId());
        addressDetailsVO.setTmpl_amount(commAddressDetailsVO.getTmpl_amount());
        addressDetailsVO.setTmpl_currency(commAddressDetailsVO.getTmpl_currency());

        transDetailsVO.setAmount(transactionDetailsVO.getAmount());
        transDetailsVO.setCurrency(transDetailsVO1.getCurrency());
        transDetailsVO.setOrderDesc(transDetailsVO1.getOrderDesc());
        transDetailsVO.setFromtype(transDetailsVO1.getFromtype());
        transDetailsVO.setToId(transactionDetailsVO.getToid());

        merchantAccountVO.setAccountId(transactionDetailsVO.getAccountId());
        merchantAccountVO.setMerchantId(transactionDetailsVO.getToid());

        transactionLogger.error("account id  ------------" + Accountid);
        ecospendRequestVo1 = (EcospendRequestVo) PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(Accountid));
        ecospendRequestVo1.setAddressDetailsVO(addressDetailsVO);
        ecospendRequestVo1.setCommMerchantVO(merchantAccountVO);
        ecospendRequestVo1.setTransDetailsVO(transDetailsVO);
        ecospendRequestVo1.setAccessTken(ecospendRequestVo.getAccessTken());

        if (functions.isValueNull(gatewayAccountVO.getReference()))
        {
            ecospendRequestVo1.setReference(gatewayAccountVO.getReference());
        }
        else
        {
            ecospendRequestVo1.setReference(ecospendRequestVo.getReference());
        }
        ecospendRequestVo1.setBankid(ecospendRequestVo.getBankid());
        if (functions.isValueNull(gatewayAccountVO.getCreditor_account_type()))
        {
            ecospendRequestVo1.setCreditortype(gatewayAccountVO.getCreditor_account_type());
        }
        else
        {
            ecospendRequestVo1.setCreditortype(ecospendRequestVo.getCreditortype());
        }
        if (functions.isValueNull(gatewayAccountVO.getCreditor_account_identification()))
        {
            ecospendRequestVo1.setCreditorID(gatewayAccountVO.getCreditor_account_identification());
        }
        else
        {
            ecospendRequestVo1.setCreditorID(ecospendRequestVo.getCreditorID());
        }
        ecospendRequestVo1.setCreditorCurrency(gatewayAccountVO.getCreditor_account_currency());
        if (functions.isValueNull(gatewayAccountVO.getCreditor_account_owner_name()))
        {
            ecospendRequestVo1.setCreditorName(gatewayAccountVO.getCreditor_account_owner_name());
        }
        else
        {
            ecospendRequestVo1.setCreditorName(ecospendRequestVo.getCreditorName());
        }
        ecospendRequestVo1.setCreditorBic(gatewayAccountVO.getCreditor_account_bic());
        ecospendRequestVo1.setDebtortype(ecospendRequestVo.getDebtortype());
        ecospendRequestVo1.setDebtorID(ecospendRequestVo.getDebtorID());
        ecospendRequestVo1.setDebtorCurrency(ecospendRequestVo.getDebtorCurrency());
        ecospendRequestVo1.setDebtorName(ecospendRequestVo.getDebtorName());
        ecospendRequestVo1.setDebtorBic(ecospendRequestVo.getDebtorBic());
        ecospendRequestVo1.setPsuid(gatewayAccountVO.getPsu_id());
        ecospendRequestVo1.setPaymentrails(gatewayAccountVO.getPayment_rails());
        ecospendRequestVo1.setDebtoraccount(ecospendRequestVo.getDebtoraccount());
        ecospendRequestVo1.setScheduledForDate(ecospendRequestVo.getScheduledForDate());
        ecospendRequestVo1.setPaymentMethod(ecospendRequestVo.getPaymentMethod());
        ecospendRequestVo1.setSend_email_notification(gatewayAccountVO.getSend_email_notification());
        ecospendRequestVo1.setSend_sms_notification(gatewayAccountVO.getSend_sms_notification());
        ecospendRequestVo1.setNumber_of_payments(ecospendRequestVo.getNumber_of_payments());
        ecospendRequestVo1.setPeriod(ecospendRequestVo.getPeriod());
        ecospendRequestVo1.setFirst_payment_date(ecospendRequestVo.getFirst_payment_date());
        ecospendRequestVo1.setFirstPaymentAmount(ecospendRequestVo.getFirstPaymentAmount());
        ecospendRequestVo1.setLastPaymentAmount(ecospendRequestVo.getLastPaymentAmount());
        return ecospendRequestVo1;
    }
    public EcospendGatewayAccountVO getAccountDetails(String accountId)
    {
        Connection conn         = null;
        PreparedStatement stmt  = null;
        EcospendGatewayAccountVO accountDetailsVO=null;
        ResultSet rs    = null;
        try
        {
            conn            = Database.getConnection();
            String query    = "select * from gateway_accounts_ecospend where accountid=?";
            stmt            = conn.prepareStatement(query);
            stmt.setString(1,accountId);
            rs = stmt.executeQuery();
            if(rs.next())
            {
                accountDetailsVO    = new EcospendGatewayAccountVO();
                accountDetailsVO.setAccountId(rs.getString("accountid"));
              //  accountDetailsVO.setReference(rs.getString("reference"));
                accountDetailsVO.setCreditor_account_type(rs.getString("creditor_account_type"));
                accountDetailsVO.setCreditor_account_identification(rs.getString("creditor_account_identification"));
                accountDetailsVO.setCreditor_account_owner_name(rs.getString("creditor_account_owner_name"));
               // accountDetailsVO.setCreditor_account_currency(rs.getString("creditor_account_currency"));
                accountDetailsVO.setCreditor_account_bic(rs.getString("creditor_account_bic"));
                //accountDetailsVO.setScheduled_for(rs.getString("scheduled_for"));
                accountDetailsVO.setPsu_id(rs.getString("psu_id"));
                //accountDetailsVO.setPayment_rails(rs.getString("payment_rails"));
                //accountDetailsVO.setSend_email_notification(rs.getString("send_email_notification"));
                //accountDetailsVO.setSend_sms_notification(rs.getString("send_sms_notification"));
            }
        }
        catch (SystemError se){
            transactionLogger.error("SystemError::::::",se);
        }
        catch (SQLException e){
            transactionLogger.error("SQLException::::::",e);
        }
        finally{
            Database.closeResultSet(rs);
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        return accountDetailsVO;
    }

    public Boolean updateTransaction (String trackingid,String customerId){

        transactionLogger.error("in side  updateTransaction----------->");
        Connection con      = null;
        PreparedStatement psUpdateTransaction = null;
        boolean isUpdate    = false;
        try
        {

            con                 = Database.getConnection();
            String update       = "update transaction_common set  customerid=? where trackingid=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,customerId);
            psUpdateTransaction.setString(2,trackingid );
            transactionLogger.error("transaction common query----"+psUpdateTransaction);
            int i   = psUpdateTransaction.executeUpdate();
            if(i > 0)
            {
                isUpdate = true;
            }
        }

        catch (SQLException e)
        {
            transactionLogger.error("SQLException----",e);

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError----", systemError);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeConnection(con);
        }
        return isUpdate;

    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }


    public static void updateMainPaymentIdEntry(String paymentid, String trackingid)
    {
        Connection connection = null;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET paymentid=? WHERE trackingid=?";
            PreparedStatement ps2   = connection.prepareStatement(updateQuery1);
            ps2.setString(1, paymentid);
            ps2.setString(2, trackingid);
            ps2.executeUpdate();
        }
        catch (SQLException se)
        {
            transactionLogger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            transactionLogger.error("SystemError---", s);
        }
        finally
        {
            if(connection!=null){
                Database.closeConnection(connection);
            }
        }
    }

    public static boolean updateRefundAccountTransaction (String trackingId,String type,String identification,String owner_name){

        transactionLogger.error("in side  updateTransaction----------->");
        Connection con = null;
        PreparedStatement psUpdateTransaction = null;
        boolean isUpdate    = false;
        try
        {

            con                 = Database.getConnection();
            String update       = "update transaction_ecospend_detaills set  dbidentification=?,dbowner_name=?,dbtype=? where trackingid=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,identification);
            psUpdateTransaction.setString(2,owner_name );
            psUpdateTransaction.setString(3,type );
            psUpdateTransaction.setString(4,trackingId);
            transactionLogger.error("transaction common query----"+psUpdateTransaction);
            int i   =  psUpdateTransaction.executeUpdate();
            if(i>0)
            {
                isUpdate=true;
            }
        }

        catch (SQLException e)
        {
            transactionLogger.error("SQLException----",e);

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError----", systemError);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeConnection(con);
        }
        return isUpdate;

    }

}

