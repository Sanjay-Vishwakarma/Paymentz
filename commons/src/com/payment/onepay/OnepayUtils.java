package com.payment.onepay;

  import javax.crypto.Cipher;
  import javax.crypto.SecretKey;
  import javax.crypto.spec.IvParameterSpec;
  import javax.crypto.spec.SecretKeySpec;
  import java.io.IOException;
  import java.nio.charset.Charset;
  import java.security.Key;
  import java.security.MessageDigest;
  import java.security.NoSuchAlgorithmException;
  import java.sql.Connection;
  import java.sql.PreparedStatement;
  import java.sql.SQLException;
  import java.text.DateFormat;
  import java.text.DecimalFormat;
  import java.text.SimpleDateFormat;
  import java.util.*;

  import com.directi.pg.Checksum;
  import com.directi.pg.Functions;
  import com.directi.pg.Transaction;
  import com.directi.pg.TransactionLogger;
  import com.payment.common.core.*;
  import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
  import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
  import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
  import com.payment.validators.vo.CommonValidatorVO;
  import org.apache.commons.httpclient.HttpClient;
  import org.apache.commons.httpclient.HttpException;
  import org.apache.commons.httpclient.methods.PostMethod;

  import java.io.IOException;
  import java.io.UnsupportedEncodingException;
  import java.security.Key;
  import java.security.MessageDigest;
  import java.security.NoSuchAlgorithmException;

/**
 * Created by Admin on 27/12/2021.
 */
public class OnepayUtils
{
    private final static TransactionLogger transactionLogger= new TransactionLogger(OnepayUtils.class.getName());
    Functions functions = new Functions();

    public static final String ALGO = "AES";

    public String getPaymentMethod(String paymentType){

        String instrumentId = "";
        if("DC".equalsIgnoreCase(paymentType))
            instrumentId = "DC";
        else if("UPI".equalsIgnoreCase(paymentType))
            instrumentId = "UPI";
        else if("NBI".equalsIgnoreCase(paymentType))
            instrumentId="NB";

        return instrumentId;
    }

    public String getCardType(String paymentBrand)
    {
        String cardType="";
        if("VISA".equalsIgnoreCase(paymentBrand))
            cardType = "Visa";
        else if("MC".equalsIgnoreCase(paymentBrand))
            cardType = "Mastercard";
        else if("MAESTRO".equalsIgnoreCase(paymentBrand))
            cardType="Maestro";
        else if("RUPAY".equalsIgnoreCase(paymentBrand))
            cardType="Rupay";
        else if("NBI".equalsIgnoreCase(paymentBrand))
            cardType="NA";
        else if("UPI".equalsIgnoreCase(paymentBrand))
            cardType="NA";
        return cardType;
    }


    static String getHashCodeFromString(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {

        MessageDigest md = MessageDigest.getInstance("AES-256");
        md.update(str.getBytes("UTF-8"));
        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer hashCodeBuffer = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            hashCodeBuffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return hashCodeBuffer.toString().toUpperCase();
    }

    public CommRequestVO getOnepayPaymentRequestVO(CommonValidatorVO commonValidatorVO)
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

        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }

    public static String doPostHttpUrlConnection(String url)
    {
        TransactionLogger transactionlogger = new TransactionLogger(OnepayUtils.class.getName());

        String request, result = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

            //post.setRequestBody(requestData);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
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
            post.releaseConnection();
        }
        return result;

    }

    public static String encrypt1(String data, String generatedKey) throws Exception {
        try {

            // String generatedKey = (getHash(salt+payId)).substring(0,32);
            String ivString     = generatedKey.substring(0,16);
            Key keyObj          = null;
            keyObj              = new SecretKeySpec(generatedKey.getBytes(), ALGO);
            IvParameterSpec iv  = new IvParameterSpec(ivString.getBytes("UTF-8"));
//			IvParameterSpec iv = new IvParameterSpec(key.getBytes(ConfigurationConstants.DEFAULT_ENCODING_UTF_8.getValue()));
            Cipher cipher       = Cipher.getInstance("AES" + "/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, keyObj, iv);

            byte[] encValue = cipher.doFinal(data.getBytes("UTF-8"));

            Base64.Encoder base64Encoder    = Base64.getEncoder().withoutPadding();
            String base64EncodedData        = base64Encoder.encodeToString(encValue);

            return base64EncodedData;
        } catch (Exception e) {
            throw new Exception("Error during encryption process");
        }
    }

    public static String decrypt1(String data,String generatedKey) throws Exception {
        try {
            // String generatedKey = (getHash(salt+payId)).substring(0,32);
            String ivString     = generatedKey.substring(0,16);
            IvParameterSpec iv  = new IvParameterSpec(ivString.getBytes("UTF-8"));
//			IvParameterSpec iv = new IvParameterSpec(key.getBytes(ConfigurationConstants.DEFAULT_ENCODING_UTF_8.getValue()));
            Cipher cipher       = Cipher.getInstance(ALGO + "/CBC/PKCS5PADDING");
            Key keyObj          = null;
            keyObj              = new SecretKeySpec(generatedKey.getBytes(), ALGO);
            cipher.init(Cipher.DECRYPT_MODE, keyObj, iv);

            byte[] decodedData  = Base64.getDecoder().decode(data);
            byte[] decValue     = cipher.doFinal(decodedData);

            return new String(decValue);

        } catch (Exception e) {
            throw new Exception("Error during decryption process");

        }
    }

    public  void doHttpPostConnection(CommonValidatorVO commonValidatorVO, String url ,String billing, String status)
    {

        transactionLogger.error("inside onepay utils doHttpPostConnection --------->"+url);

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
            transactionLogger.error("body--------->"+body);
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(body);
            httpClient.executeMethod(post);
            transactionLogger.error("after post result--------->"+ post.getStatusCode());
        }
        catch (HttpException e)
        {
            transactionLogger.error("HttpException--------->",e);

        }
        catch (IOException e)
        {
            transactionLogger.error("IOException--------->",e);
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


}

