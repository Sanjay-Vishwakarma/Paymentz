package com.payment.lyra;

import com.directi.pg.*;
import com.payment.common.core.*;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Admin on 12/02/2022.
 */
public class LyraPaymentUtils
{
    static TransactionLogger transactionlogger = new TransactionLogger(LyraPaymentUtils.class.getName());
    Functions functions = new Functions();

    static String doGetHttpUrlConnection(String url,String publickey,String secretkey )
    {
        String result = "";
        GetMethod get = new GetMethod(url);
        try
        {

            String userPassword = publickey + ":" + secretkey;
            String encodedCredentials = Base64.encode(userPassword.getBytes());

            HttpClient httpClient = new HttpClient();
            get.addRequestHeader("Content-Type", "application/json");
            get.addRequestHeader("Authorization", "Basic " + encodedCredentials);

            httpClient.executeMethod(get);
            String response = new String(get.getResponseBody());
            result = response;
            transactionlogger.error("response-->" + result);

        }
        catch (HttpException e)
        {
            transactionlogger.error("HttpException--------->" + e);

        }
        catch (IOException e)
        {
            transactionlogger.error("IOException--------->"+e);
        }
        finally
        {
            get.releaseConnection();
        }
        return (result);

    }

    public static String doPostHttpUrlConnection(String url ,String request ,String publickey,String secretkey )
    {
        String result   = "";
        PostMethod post = new PostMethod(url);
        try
        {

            String userPassword         = publickey + ":" + secretkey;
            String encodedCredentials   = Base64.encode(userPassword.getBytes());

            HttpClient httpClient   = new HttpClient();
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Authorization", "Basic " + encodedCredentials);
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result          = response;
            System.out.println("encodedCredentials-->"+encodedCredentials);
            transactionlogger.error("encodedCredentials-->" + encodedCredentials);
            transactionlogger.error("response-->" + result);
        }
        catch (HttpException e)
        {
            transactionlogger.error("HttpException--------->" , e);

        }
        catch (IOException e)
        {
            transactionlogger.error("IOException--------->" , e);
        }finally
        {
            post.releaseConnection();
        }


        return result;
    }

    public static String doPostInitHttpUrlConnection(String url ,String request ,String publickey,String secretkey )
    {
        String result   = "";
        PostMethod post = new PostMethod(url);
        try
        {


            HttpClient httpClient   = new HttpClient();
            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException e)
        {
            transactionlogger.error("HttpException--------->" , e);

        }
        catch (IOException e)
        {
            transactionlogger.error("IOException--------->" , e);
        }finally
        {
            post.releaseConnection();
        }


        return result;
    }
    static Boolean updateTransaction (String trackingid, String paymentid){

        transactionlogger.error("in side  updateTransaction----------->");
        Connection con      = null;
        PreparedStatement psUpdateTransaction = null;
        boolean isUpdate    = false;
        try
        {

            con= Database.getConnection();
            String update = "update transaction_common set paymentid = ? where trackingid=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,paymentid);
            psUpdateTransaction.setString(2,trackingid);
            transactionlogger.error("transaction common query----"+psUpdateTransaction);
            int i=psUpdateTransaction.executeUpdate();
            if(i>0)
            {
                isUpdate=true;
            }
        }

        catch (SQLException e)
        {
            transactionlogger.error("SQLException----",e);

        }
        catch (SystemError systemError)
        {
            transactionlogger.error("SystemError----", systemError);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeConnection(con);
        }
        return isUpdate;

    }

    public CommRequestVO getLyraPaymentRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO             = new CommRequestVO();
        CommAddressDetailsVO addressDetailsVO   = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO         = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO           = new CommMerchantVO();

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

        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }
    static String getPaymentMethod(String paymentType){

        String instrumentId = "";
        if("DC".equalsIgnoreCase(paymentType) ||"CC".equalsIgnoreCase(paymentType))
            instrumentId = "CARD";
        else if("UPI".equalsIgnoreCase(paymentType))
            instrumentId = "UPI";
        else if("EWI".equalsIgnoreCase(paymentType))
            instrumentId = "WALLET";
        else if("NBI".equalsIgnoreCase(paymentType))
            instrumentId="NET_BANKING";

        return instrumentId;
    }

    public  void doHttpPostConnection(CommonValidatorVO commonValidatorVO, String url ,String billing, String status)
    {

        transactionlogger.error("inside lyra utils doHttpPostConnection --------->"+url);

        String checkSum=null;
        Functions functions=new Functions();
        String respStatus="N";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String timeStamp = String.valueOf(dateFormat.format(date));
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        Transaction transaction = new Transaction();
        String firstName="";
        String lastName="";
        String cardholderName="";
        String eci="";
        String token="";
        String redirectMethod="";
        PostMethod post = new  PostMethod(url);
        if(!functions.isValueNull(billing))
            billing="";

        String paymentMode = commonValidatorVO.getPaymentType();
        String paymentBrand = commonValidatorVO.getCardType();
        String pType = transaction.getPaymentModeForRest(paymentMode);
        String cType = transaction.getPaymentBrandForRest(paymentBrand);
        try
        {
            if(functions.isValueNull(commonValidatorVO.getEci()))
                eci=commonValidatorVO.getEci();
            else
                eci="";

            if(functions.isValueNull(commonValidatorVO.getToken())){
                token=commonValidatorVO.getToken();
            }else {
                token="";
            }
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname()))
                firstName=commonValidatorVO.getAddressDetailsVO().getFirstname();
            else
                firstName="";
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname()))
                lastName=commonValidatorVO.getAddressDetailsVO().getLastname();
            else
                lastName="";
            if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getRedirectMethod()))
                redirectMethod=commonValidatorVO.getTransDetailsVO().getRedirectMethod();


            if (functions.isValueNull(commonValidatorVO.getErrorName()))
            {
                errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.valueOf(commonValidatorVO.getErrorName()));
                if (errorCodeVO == null)
                {
                    errorCodeVO = errorCodeUtils.getSystemErrorCode(ErrorName.valueOf(commonValidatorVO.getErrorName()));
                }
            }
            else if (functions.isValueNull(status) && (status.contains("Successful") || status.contains("successful") || status.contains("success")))
            {
                respStatus="Y";
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.TRANSACTION_SUCCEED);
            }
            else if (functions.isValueNull(status) && (status.contains("Pending") || status.contains("pending")))
            {
                respStatus="P";
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFULL_PENDING_TRANSACTION);
            }
            else if (functions.isValueNull(status) && (status.contains("Cancel") || status.contains("cancel")))
            {
                respStatus="C";
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.CANCEL_SUCCESSFUL);
            }
            else
            {
                respStatus="N";
                if(functions.isValueNull(commonValidatorVO.getErrorMsg())){
                    errorCodeVO.setApiDescription(commonValidatorVO.getErrorMsg());
                }else{
                    errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.TRANSACTION_REJECTED);
                }
            }
            try
            {
                checkSum = Checksum.generateChecksumForStandardKit(getValue(commonValidatorVO.getTrackingid()), getValue(commonValidatorVO.getTransDetailsVO().getOrderId()), getValue(commonValidatorVO.getTransDetailsVO().getAmount()), getValue(respStatus), getValue(commonValidatorVO.getMerchantDetailsVO().getKey()));

            }
            catch (NoSuchAlgorithmException e)
            {
                respStatus = "N";
            }
            String cardBin="";
            String cardLast4Digits="";
            if(null!=commonValidatorVO.getCardDetailsVO() && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {
                cardBin = commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6);
                cardLast4Digits = commonValidatorVO.getCardDetailsVO().getCardNum().substring((commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4), commonValidatorVO.getCardDetailsVO().getCardNum().length());

            }
            ;
            String body = "";

            body="trackingId="+commonValidatorVO.getTrackingid()+"&status="+respStatus+"&splitTransaction="+getValue(commonValidatorVO.getFailedSplitTransactions())+
                    "&firstName="+getValue(firstName)+"&lastName="+getValue(lastName)+"&checksum="+checkSum+"&desc="+getValue(commonValidatorVO.getTransDetailsVO().getOrderDesc())+
                    "&currency="+getValue(commonValidatorVO.getTransDetailsVO().getCurrency())+"&amount="+getValue(commonValidatorVO.getTransDetailsVO().getAmount())
                    +"&tmpl_currency="+getValue(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())+"&tmpl_amount="+getValue(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())+
                    "&timestamp="+getValue(timeStamp)+"&resultCode="+getValue(errorCodeVO.getApiCode())+"&resultDescription="+getValue(errorCodeVO.getApiDescription())+
                    "&cardBin="+getValue(cardBin)+"&cardLast4Digits="+getValue(cardLast4Digits)+"&custEmail="+getValue(commonValidatorVO.getAddressDetailsVO().getEmail())+
                    "&paymentMode="+getValue(pType)+"&paymentBrand="+getValue(cType)+"&eci="+getValue(eci);
            transactionlogger.error("body--------->"+body);
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(body);
            httpClient.executeMethod(post);
            transactionlogger.error("after post result--------->"+ post.getStatusCode());
        }
        catch (HttpException e)
        {
            transactionlogger.error("HttpException--------->",e);

        }
        catch (IOException e)
        {
            transactionlogger.error("IOException--------->",e);
        }

        finally
        {
            post.releaseConnection();
        }

    }
    public String getValue(String value){
        if(functions.isValueNull(value))
            return value;
        else
            return "";
    }
    public static String getAmount(String amount)
    {
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2    = Double.valueOf(amount);
        dObj2           = dObj2 * 100;
        String amt      = d.format(dObj2);
        return amt;
    }

}
