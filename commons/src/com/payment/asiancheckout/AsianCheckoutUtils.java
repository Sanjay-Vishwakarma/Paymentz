package com.payment.asiancheckout;

import com.directi.pg.Database;
import com.directi.pg.LoadProperties;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpStatus;

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
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Admin on 5/3/2021.
 */
public class AsianCheckoutUtils
{


        private final static ResourceBundle BPBANKSID           = LoadProperties.getProperty("com.directi.pg.BPBANKSID");
        private static TransactionLogger transactionlogger      = new TransactionLogger(AsianCheckoutUtils.class.getName());
        public static final String ALGO                         = "AES";

        private static Stack<MessageDigest> stack   = new Stack<MessageDigest>();
        private final static String separator       = "~";
        private final static String equator         = "=";
        private final static String hashingAlgo     = "SHA-256";
        private static final Charset ASCII          = Charset.forName("UTF-8");

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

        public CommRequestVO getAsianCheckoutRequestVO(CommonValidatorVO commonValidatorVO)
        {
            CommRequestVO commRequestVO                 = new CommRequestVO();
            CommAddressDetailsVO addressDetailsVO       = new CommAddressDetailsVO();
            CommTransactionDetailsVO transDetailsVO     = new CommTransactionDetailsVO();
            CommCardDetailsVO cardDetailsVO             = new CommCardDetailsVO();
            CommMerchantVO commMerchantVO               = new CommMerchantVO();

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



        public String getPaymentTypeS2S(String paymentMode)
        {
            String payMode = "";

            if("CC".equalsIgnoreCase(paymentMode))
                payMode = "CARD";
            if("DC".equalsIgnoreCase(paymentMode))
                payMode = "CARD";
            if("NBI".equalsIgnoreCase(paymentMode))
                payMode = "NB";
            if("EWI".equalsIgnoreCase(paymentMode))
                payMode = "WL";
            if("UPI".equalsIgnoreCase(paymentMode))
                payMode = "UP";

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
            boolean flag            = false;
            String generatedHash    = generateCheckSum(responseParameters, secretKey);

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
            Double dObj2    = Double.valueOf(amount);
            dObj2           = dObj2 * 100;
            String amt      = d.format(dObj2);

            return amt;
        }

    public static String getDecimalAmount(String amountString)
    {
        String amt = "0.00";

        if(amountString != null && !amountString.isEmpty()){
            DecimalFormat d = new DecimalFormat("#.00");
            Double dObj2    = Double.valueOf(amountString);
            dObj2           = dObj2 / 100;
            amt             = d.format(dObj2);
        }

        return amt;
    }

        public String doGetHTTPSURLConnectionClient(String url) throws PZTechnicalViolationException
        {
            //transactionlogger.error("url--->" + url);
            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();

            HttpClient client   = new HttpClient();
            GetMethod method    = new GetMethod(url);
            String result       = "";
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


                byte [] iv          = "B6B9B57E71DF7B52".getBytes(ASCII);
                byte [] keyBytes    = key.getBytes(ASCII);

                SecretKey aesKey    = new SecretKeySpec(keyBytes, "AES");

                Cipher cipher       = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));

                byte[] result       = cipher.doFinal(data.getBytes());
                ciphertext          = new String(org.apache.commons.net.util.Base64.encodeBase64(result));


            }catch (Exception e){
                transactionlogger.error("Exception--->",e);
            }
            return ciphertext;
        }

        public static String dencrypt(String data,String key){
            String finalresult = "";
            try{
                byte [] cipherBytes = org.apache.commons.net.util.Base64.decodeBase64(data);
                byte [] iv          = "B6B9B57E71DF7B52".getBytes(ASCII);
                byte [] keyBytes    = key.getBytes(ASCII);

                SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");

                Cipher cipher = Cipher.getInstance("AES/CBC/NOPADDING");
                cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));

                byte[] result   = cipher.doFinal(cipherBytes);
                finalresult     = new String(result);


            }catch (Exception e){
                e.printStackTrace();
            }
            return finalresult;
        }



        public String doPostHttpUrlConnection(String url ,String request)
        {
            String result   = "";
            PostMethod post = new PostMethod(url);
            try
            {

                SSLUtils.setupSecurityProvider();
                SSLUtils.trustAllHttpsCertificates();
                //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
                System.setProperty("https.protocols", "TLSv1.2");
                HttpClient httpClient = new HttpClient();
                post.setRequestHeader("Content-Type", "application/json");
                post.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
                post.setRequestHeader("IPIMEI","192.168.1.8");
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
                connection              = Database.getConnection();
                String updateQuery1     = "UPDATE transaction_common SET remark=? WHERE trackingid=?";
                PreparedStatement ps2   = connection.prepareStatement(updateQuery1);

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
                connection              = Database.getConnection();
                String updateQuery1     = "UPDATE transaction_common SET authorization_code=? WHERE trackingid=?";
                PreparedStatement ps2   = connection.prepareStatement(updateQuery1);

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
            Connection con                          = null;
            PreparedStatement psUpdateTransaction   = null;
            boolean isUpdate                        = false;
            try
            {

                con                 = Database.getConnection();
                String update       = "update transaction_common set customerId= ? where trackingid=?";
                psUpdateTransaction = con.prepareStatement(update.toString());

                psUpdateTransaction.setString(1, customerId);
                psUpdateTransaction.setString(2, trackingid);

                transactionlogger.error("transaction common query----"+psUpdateTransaction);
                int i   = psUpdateTransaction.executeUpdate();
                if(i > 0)
                {
                    isUpdate = true;
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

    // new encryption logics

    public String encrypt1(String data, String generatedKey, String payId) throws Exception {
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

    public String decrypt1(String data,String generatedKey, String payId) throws Exception {
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

    }
