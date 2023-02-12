package com.payment.cashfree;

import com.directi.pg.*;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.utils.SSLUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpStatus;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Base64;

/**
 * Created by Admin on 5/15/2021.
 */
public class CashFreeUtils
{



    private final static ResourceBundle BPBANKSID = LoadProperties.getProperty("com.directi.pg.BPBANKSID");
    private static TransactionLogger transactionlogger = new TransactionLogger(CashFreeUtils.class.getName());

    private static Stack<MessageDigest> stack = new Stack<MessageDigest>();
    private final static String separator = "~";
    private final static String equator = "=";
    private final static String hashingAlgo = "SHA-256";
    private static final Charset ASCII = Charset.forName("UTF-8");
    Functions functions = new Functions();
    // Hash calculation from request map
    public static String generateCheckSum(Map<String, String> parameters, String secretKey)
            throws NoSuchAlgorithmException
    {
        Map<String, String> treeMap = new TreeMap<String, String>(parameters);

        StringBuilder allFields = new StringBuilder();
        for (String key : treeMap.keySet()) {
            allFields.append(separator);
            allFields.append(key);
            allFields.append(equator);
            allFields.append(treeMap.get(key));
        }

        allFields.deleteCharAt(0); // Remove first FIELD_SEPARATOR
        allFields.append(secretKey);

        transactionlogger.error("allFields--->"+allFields);
        System.out.println("allFields--->"+allFields);
        return getHash(allFields.toString());
    }

    public static String generateCheckSum2(Map<String, String> parameters, String secretKey)
            throws NoSuchAlgorithmException {
        Map<String, String> treeMap = new TreeMap<String, String>(parameters);

        StringBuilder allFields = new StringBuilder();
        for (String key : treeMap.keySet()) {
            allFields.append(separator);
            allFields.append(key);
            allFields.append(equator);
            allFields.append(treeMap.get(key));

        }

        allFields.deleteCharAt(0); // Remove first FIELD_SEPARATOR
        //allFields.append(secretKey);
        allFields.append(separator);
        allFields.append("HASH");
        allFields.append(equator);
        allFields.append(secretKey);

        transactionlogger.error("allFields--->"+allFields);
        return allFields.toString();
    }


    public static String generateAutoSubmitForm(String actionUrl,String msgData,String paymentMethod)
    {
        //transactionlogger.error("urll---" + actionUrl);

        String hiddenValue = "";
        if(paymentMethod.equalsIgnoreCase("UP"))
            hiddenValue = "<input type='hidden' name='txtPayCategory' value='UPI'>";

        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +actionUrl+ "\">" +
                "<input type=hidden name=msg id=msg value=\""+msgData+"\">"+
                hiddenValue+
                "</form>" +
                "<script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        return form.toString();
    }
    public CommRequestVO getCashFreeRequestVO(CommonValidatorVO commonValidatorVO)
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
        transDetailsVO.setCardType(commonValidatorVO.getProcessorName());
        commRequestVO.setCustomerId(commonValidatorVO.getVpa_address());
        transactionlogger.error("utils vpa--->" + commonValidatorVO.getVpa_address());

        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }

    public static String getBankName(String BankCode ){
        String BankName="";
        try
        {
            BankName = BPBANKSID.getString(BankCode);
        }catch (Exception e)
        {
            transactionlogger.error("Exception--->",e);
        }

        return BankName;
    }

    public String getPaymentTypeS2S(String paymentMode)
    {
        String payMode = "";
        if("CC".equalsIgnoreCase(paymentMode))
            payMode = "card";
        if("DC".equalsIgnoreCase(paymentMode))
            payMode = "card";
        if("NBI".equalsIgnoreCase(paymentMode))
            payMode = "nb";
        if("EWI".equalsIgnoreCase(paymentMode))
            payMode = "wallet";
        if("UPI".equalsIgnoreCase(paymentMode))
            payMode = "upi";

        return payMode;
    }
    public String getPaymentType(String paymentMode)
    {
        String payMode = "";
        if("CC".equalsIgnoreCase(paymentMode))
            payMode = "CC";
        if("DC".equalsIgnoreCase(paymentMode))
            payMode = "DC";
        if("NBI".equalsIgnoreCase(paymentMode))
            payMode = "NB";
        if("EWI".equalsIgnoreCase(paymentMode))
            payMode = "WL";
        if("UPI".equalsIgnoreCase(paymentMode))
            payMode = "UP";

        return payMode;
    }


    public String getPaymentBrand(String paymentMode)
    {
        String payBrand = "";
        if("1".equalsIgnoreCase(paymentMode))
            payBrand = "VI";
        if("2".equalsIgnoreCase(paymentMode))
            payBrand = "MS";
        if("23".equalsIgnoreCase(paymentMode))
            payBrand = "RU";
        return payBrand;
    }
    public String getCurrency(String currency)
    {
        String currencyStr = "";
        if("356".equalsIgnoreCase(currency))
            currencyStr = "INR";
        if("826".equalsIgnoreCase(currency))
            currencyStr = "GBP";
        if("840".equalsIgnoreCase(currency))
            currencyStr = "USD";
        if("978".equalsIgnoreCase(currency))
            currencyStr = "EUR";
        return currencyStr;
    }
    // Response hash validation
    public static boolean validateResponseChecksum(Map<String, String> responseParameters, String secretKey,
                                                   String responseHash) throws NoSuchAlgorithmException {
        boolean flag = false;
        String generatedHash = generateCheckSum(responseParameters, secretKey);
        if (generatedHash.equals(responseHash)) {
            flag = true;
        }
        return flag;
    }

    // Generate hash from the supplied string
    public static String getHash(String input) throws NoSuchAlgorithmException {
        String response = null;

        MessageDigest messageDigest = provide();
        messageDigest.update(input.getBytes());
        consume(messageDigest);

        response = new String(Hex.encodeHex(messageDigest.digest()));

        return response.toUpperCase();
    }// getSHA256Hex()

    private static MessageDigest provide() throws NoSuchAlgorithmException {
        MessageDigest digest = null;

        try {
            digest = stack.pop();
        } catch (EmptyStackException emptyStackException) {
            digest = MessageDigest.getInstance(hashingAlgo);
        }
        return digest;
    }

    private static void consume(MessageDigest digest) {
        stack.push(digest);
    }
    public static String getAmount(String amount)
    {
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;
        String amt = d.format(dObj2);
        return amt;
    }

    public String doGetHTTPSURLConnectionClient(String url) throws PZTechnicalViolationException
    {
        //transactionlogger.error("url--->" + url);
        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();

        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        String result = "";
        try
        {
            method.setRequestHeader("Accept", "application/json");
            //  method.setRequestHeader("Authorization", authType+ " " +encodedCredentials);
            method.setRequestHeader("Cache-Control", "no-cache");

            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK)
            {
                //transactionlogger.debug("Method failed: " + method.getStatusLine());
            }

            byte[] response = method.getResponseBody();
            //transactionlogger.error("Response-----" + response.toString());
            result = new String(response);

            method.releaseConnection();
        }
        catch (HttpException he)
        {
            //transactionlogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("OneRoadUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            //transactionlogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("OneRoadUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {
            method.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }

    public static String encrypt(String data,String key){
/* String plainCipher = hashString;
//String plainCipher = "amount=100~card_exp_dt=012021~card_number=4000000000000002~currency_code=356~cust_email=test@bhartipay.com~CUST_NAME=Test Merchant~CUST_PHONE=9999999999~CVV=123~ORDER_ID=BHARTID3012201304~PAYMENT_TYPE=CARD~PAY_ID=2001141020561000~RETURN_URL=http://new.rs.bp.h.teamat.work/response.php~HASH=3B4A0992F799FC857F121C8515E06FCAF8AD4C9BCC6CAA82002899406E84CACA";
*/
        String ciphertext ="";
        try{


            byte [] iv = "B6B9B57E71DF7B52".getBytes(ASCII);
            byte [] keyBytes = key.getBytes(ASCII);

            SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));

            byte[] result = cipher.doFinal(data.getBytes());

            ciphertext = new String(org.apache.commons.net.util.Base64.encodeBase64(result));


        }catch (Exception e){
            transactionlogger.error("Exception--->",e);
        }
        return ciphertext;
    }

    public static String dencrypt(String data,String key){
        String finalresult = "";
        try{
            byte [] cipherBytes = org.apache.commons.net.util.Base64.decodeBase64(data);
            byte [] iv = "B6B9B57E71DF7B52".getBytes(ASCII);
            byte [] keyBytes = key.getBytes(ASCII);

            SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/NOPADDING");
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));

            byte[] result = cipher.doFinal(cipherBytes);

            finalresult = new String(result);


        }catch (Exception e){
            e.printStackTrace();
        }
        return finalresult;
    }

   /* public static String encrypt2(String data,String skey){
        String finalresult = "";
    try{

        //String plainCipher = hashString;
        //String plainCipher = "AMOUNT=100~CARD_EXP_DT=012021~CARD_NUMBER=4000000000000002~CURRENCY_CODE=356~CUST_EMAIL=test@bhartipay.com~CUST_NAME=Test Merchant~CUST_PHONE=9999999999~CVV=123~ORDER_ID=BHARTID3012201304~PAYMENT_TYPE=CARD~PAY_ID=2001141020561000~RETURN_URL=http://new.rs.bp.h.teamat.work/response.php~HASH=3B4A0992F799FC857F121C8515E06FCAF8AD4C9BCC6CAA82002899406E84CACA";
        byte [] iv = "B6B9B57E71DF7B52".getBytes(ASCII);
       // byte [] keyBytes = "B6B9B57E71DF7B52B17D191D36259E0B".getBytes(ASCII);
        Key key= mainUtils.makeKey(skey);

       // SecretKey aesKey = new SecretKeySpec(key, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

        byte[] result = cipher.doFinal(data.getBytes());
        // System.out.println(Hex.encodeHexString(result));
        finalresult = new String(Base64.encodeBase64(result));

        System.out.println("ciphertext--->"+finalresult);
        //   String decodedText = com.sun.xml.internal.messaging.saaj.util.Base64.base64Decode(String.);

    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
        return finalresult;
    }*/

    public String doPostHttpUrlConnection(String url ,String request)
    {
        String result = "";
        PostMethod post = new PostMethod(url);
        try
        {

            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            System.setProperty("https.protocols", "TLSv1.2");
            HttpClient httpClient = new HttpClient();
            post.setRequestHeader("Content-Type", "multipart/form-data");
            post.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            post.setRequestHeader("IPIMEI","192.168.1.8");
            post.setRequestBody(request);

            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException e)
        {
            transactionlogger.error("HttpException--------->" , e);

        }
        catch (IOException e)
        {
            transactionlogger.error("IOException--------->" , e);
        }
        finally
        {
            post.releaseConnection();
        }

        return result;
    }

    public void updateMainTableEntry(String remark, String trackingid)
    {
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String updateQuery1 = "UPDATE transaction_common SET remark=? WHERE trackingid=?";
            PreparedStatement ps2 = connection.prepareStatement(updateQuery1);
            ps2.setString(1, remark);
            ps2.setString(2, trackingid);
            ps2.executeUpdate();
        }
        catch (SQLException se)
        {
            transactionlogger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            transactionlogger.error("SystemError---", s);
        }
        finally
        {
            if(connection!=null)
                Database.closeConnection(connection);
        }
    }


    public void updateOrderid(String requestorderid, String trackingid)
    {
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String updateQuery1 = "UPDATE transaction_common SET authorization_code=? WHERE trackingid=?";
            PreparedStatement ps2 = connection.prepareStatement(updateQuery1);
            ps2.setString(1, requestorderid);
            ps2.setString(2, trackingid);
            ps2.executeUpdate();
        }
        catch (SQLException se)
        {
            transactionlogger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            transactionlogger.error("SystemError---", s);
        }
        finally
        {
            if(connection!=null)
                Database.closeConnection(connection);
        }
    }

    public Boolean updateTransaction (String trackingid, String  customerId ){

        transactionlogger.error("in side  updateTransaction----------->"+customerId);
        Connection con = null;
        PreparedStatement psUpdateTransaction = null;
        boolean isUpdate=false;
        try
        {

            con= Database.getConnection();
            String update = "update transaction_common set customerId= ? where trackingid=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1, customerId);
            psUpdateTransaction.setString(2, trackingid);
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


    public String getSignature(Map <String,String>postData,String secretKey ){
        String data = "";
        String signature = "";
        try
        {
            SortedSet<String> keys = new TreeSet<String>(postData.keySet());

            for (String key : keys)
            {
                data = data + key + postData.get(key);
            }

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key_spec = new
                    SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key_spec);

            signature = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(data.getBytes()));
            transactionlogger.error("signature---->" + signature);

        }catch (Exception e){
            transactionlogger.error("cashfree utils Exception-->",e);

        }
        return signature;
    }

    public  void doHttpPostConnection(CommonValidatorVO commonValidatorVO, String url ,String billing, String status)
    {

        transactionlogger.error("inside cashfree utils doHttpPostConnection --------->"+url);

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
                checkSum = Checksum.generateChecksumForStandardKit(getValue(commonValidatorVO.getTrackingid()),getValue(commonValidatorVO.getTransDetailsVO().getOrderId()), getValue(commonValidatorVO.getTransDetailsVO().getAmount()), getValue(respStatus),getValue(commonValidatorVO.getMerchantDetailsVO().getKey()));

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

    public static String  doGetHttpUrlConnection(String REQUEST_URL,String client_id,String clientSecret) throws Exception {
        HttpClient httpClient   = new HttpClient();
        GetMethod getMethod     = new GetMethod(REQUEST_URL);
        String body             = null;
        try {
            getMethod.addRequestHeader("Content-Type", "application/json");
            getMethod.addRequestHeader("x-client-id", client_id);
            getMethod.addRequestHeader("x-client-secret",clientSecret);
            getMethod.addRequestHeader("x-api-version", "2021-05-21");
            getMethod.setDoAuthentication(true);
            httpClient.executeMethod(getMethod);
            body        = getMethod.getResponseBodyAsString();
        } catch (HttpException he)
        {
            transactionlogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("CashFreeUtils.java", "doGetPostHTTPUrlConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionlogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("CashFreeUtils.java", "doGetPostHTTPUrlConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }finally {
            getMethod.releaseConnection();
        }
        return body;
    }


}