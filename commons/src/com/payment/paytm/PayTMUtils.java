package com.payment.paytm;

import com.directi.pg.*;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.io.IOException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;

/**
 * Created by Admin on 3/8/2021.
 */
public class PayTMUtils
{

    private static TransactionLogger transactionlogger      = new TransactionLogger(PayTMUtils.class.getName());
    Functions functions = new Functions();

    private static final String ALGTHM_TYPE_AES			= "AES";
    private static final String ALGTHM_CBC_PAD_AES		= "AES/CBC/PKCS5PADDING";
    private static final String ALGTHM_PROVIDER_BC		= "SunJCE";
    private static final byte[] ivParamBytes            = new byte[] { 64, 64, 64, 64, 38, 38, 38, 38, 35, 35, 35, 35, 36, 36, 36, 36 };
    static ArrayList<String> errorCodeList                     =  new ArrayList<>();



    public static String  getPaymentType(String paymentMode)
    {
        String payMode = "";
        if("CC".equalsIgnoreCase(paymentMode))
            payMode = "CC";
        if("DC".equalsIgnoreCase(paymentMode))
            payMode = "DEBIT_CARD";
        if("NBI".equalsIgnoreCase(paymentMode))
            payMode = "NET_BANKING";
        if("UPI".equalsIgnoreCase(paymentMode))
            payMode = "UPI";
        if("EWI".equalsIgnoreCase(paymentMode))
            payMode = "WALLET";

        return payMode;
    }


    public static String getPaymentBrand(String paymentMode)
    {
        String payBrand = "";
        if("1".equalsIgnoreCase(paymentMode))
            payBrand = "VISA";
        if("2".equalsIgnoreCase(paymentMode))
            payBrand = "MAST";
        if("23".equalsIgnoreCase(paymentMode))
            payBrand = "RUPAY";
       return payBrand;
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
            if(connection!=null){
                Database.closeConnection(connection);
            }
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
            if(connection!=null){
                Database.closeConnection(connection);
            }
        }
    }

    public Boolean updateTransaction (String trackingid, String  customerId ){

        transactionlogger.error("in side  updateTransaction----------->"+customerId);
        Connection con = null;
        PreparedStatement psUpdateTransaction = null;
        boolean isUpdate=false;
        try
        {

            con                 = Database.getConnection();
            String update       = "update transaction_common set customerId= ? where trackingid=?";
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
            if(con!=null){
                Database.closeConnection(con);
            }
        }
        return isUpdate;

    }

    public void updateRRNMainTableEntry(String RRN, String trackingid)
    {
        Connection connection = null;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET rrn=? WHERE trackingid=?";
            PreparedStatement ps2   = connection.prepareStatement(updateQuery1);
            ps2.setString(1, RRN);
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
            if(connection != null){
                Database.closeConnection(connection);
            }
        }
    }

    public static CommRequestVO getPayTMRequestVO(CommonValidatorVO commonValidatorVO)
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
    static String doPostHttpUrlConnection(String request, String url )
    {
        String result = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.setRequestHeader("Content-Type", "application/json");

            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
           // transactionlogger.error("response-->" + result);
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
    public static String doGetHTTPSURLConnectionClient(String url,String request,String base64Credentials) throws PZTechnicalViolationException
    {
        String result   = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestHeader("Authorization", "Basic "+base64Credentials);

            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException he)
        {
            transactionlogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("PayUUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionlogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("PayUUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {
            post.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }

    public static String encrypt(final String input, final String key) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String encryptedValue           = "";
        final Cipher cipher             = Cipher.getInstance("AES/CBC/PKCS5PADDING", "SunJCE");
        cipher.init(1, new SecretKeySpec(key.getBytes(), "AES"), new IvParameterSpec(ivParamBytes));
        final byte[] baseEncodedByte    = Base64.getEncoder().encode(cipher.doFinal(input.getBytes()));
        encryptedValue                  = new String(baseEncodedByte);
        return encryptedValue;
    }

    public static String decrypt(final String input, final String key) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, IOException, BadPaddingException, IllegalBlockSizeException {
        String decryptedValue   = "";
        final Cipher cipher     = Cipher.getInstance("AES/CBC/PKCS5PADDING", "SunJCE");
        cipher.init(2, new SecretKeySpec(key.getBytes(), "AES"), new IvParameterSpec(ivParamBytes));
        final byte[] baseDecodedByte    = Base64.getDecoder().decode(input);
        decryptedValue                  = new String(cipher.doFinal(baseDecodedByte));
        return decryptedValue;
    }

    public static String generateSignature(final TreeMap<String, String> params, final String key) throws Exception {
        return generateSignature(getStringByParams(params), key);
    }

    public static String generateSignature(final String params, final String key) throws Exception {
        final String salt = generateRandomString(4);
        return calculateChecksum(params, key, salt);
    }

    public static boolean verifySignature(final TreeMap<String, String> params, final String key, final String checksum) throws Exception {
        return verifySignature(getStringByParams(params), key, checksum);
    }

    public static boolean verifySignature(final String params, final String key, final String checksum) throws Exception {
        final String paytm_hash = decrypt(checksum, key);
        final String salt = paytm_hash.substring(paytm_hash.length() - 4);
        final String calculatedHash = calculateHash(params, salt);
        return paytm_hash.equals(calculatedHash);
    }

    private static String generateRandomString(final int length) {
        final String ALPHA_NUM = "9876543210ZYXWVUTSRQPONMLKJIHGFEDCBAabcdefghijklmnopqrstuvwxyz!@#$&_";
        final StringBuilder random = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            final int ndx = (int)(Math.random() * ALPHA_NUM.length());
            random.append(ALPHA_NUM.charAt(ndx));
        }
        return random.toString();
    }

    private static String getStringByParams(final TreeMap<String, String> params) {
        if (params == null) {
            return "";
        }
        final Set<String> keys = params.keySet();
        final StringBuilder string = new StringBuilder();
        final TreeSet<String> parameterSet = new TreeSet<String>(keys);
        for (final String paramName : parameterSet) {
            final String value = (params.get(paramName) == null) ? "" : params.get(paramName);
            string.append(value).append("|");
        }
        return string.substring(0, string.length() - 1);
    }

    private static String calculateChecksum(final String params, final String key, final String salt) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException {
        final String hashString = calculateHash(params, salt);
        String checksum = encrypt(hashString, key);
        if (checksum != null) {
            checksum = checksum.replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", "");
        }
        return checksum;
    }

    private static String calculateHash(final String params, final String salt) throws NoSuchAlgorithmException {
        final String finalString = params + "|" + salt;
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        final Formatter hash = new Formatter();
        for (final byte b : messageDigest.digest(finalString.getBytes())) {
            hash.format("%02x", b);
        }
        return hash.toString().concat(salt);
    }

    public static String doPostHTTPSURLConnectionClient(String post_data,String REQUEST_URL) throws PZTechnicalViolationException
    {
        String responseData = "";
        HttpURLConnection connection = null;
        try {
            URL url        = new URL(REQUEST_URL);
            connection    = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
            requestWriter.writeBytes(post_data);
            requestWriter.close();

            InputStream is                  = connection.getInputStream();
            BufferedReader responseReader   = new BufferedReader(new InputStreamReader(is));
            if ((responseData = responseReader.readLine()) != null) {
                //System.out.append("responseData " + responseData);
            }
            responseReader.close();
        } catch (HttpException he)
        {
            transactionlogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("PayUUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionlogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("PayUUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {

        }
        return responseData ;

    }

    public static String doPostURLConnectionClient(String post_data,String REQUEST_URL,String x_mid,String x_checksum) throws PZTechnicalViolationException
    {
        String responseData = "";
        HttpURLConnection connection = null;
        try {
            URL url        = new URL(REQUEST_URL);
            connection    = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("x-mid", x_mid);
            connection.setRequestProperty("x-checksum", x_checksum);
            connection.setDoOutput(true);

            DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
            requestWriter.writeBytes(post_data);
            requestWriter.close();

            InputStream is                  = connection.getInputStream();
            BufferedReader responseReader   = new BufferedReader(new InputStreamReader(is));
            if ((responseData = responseReader.readLine()) != null) {
                System.out.append("responseData " + responseData);
            }
            responseReader.close();
        } catch (HttpException he)
        {
            transactionlogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("PayUUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionlogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("PayUUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {

        }
        return responseData ;

    }


    static {
        errorCodeList.add("DE_010");// parameter is mandatory and it's can't be null or blank.
        errorCodeList.add("DE_011");// parameter doesn't contain valid value.
        errorCodeList.add("DE_012");// Amount must be a positive number with maximal 2 decimal places.
        errorCodeList.add("DE_013");// Param doesn't contain valid length, It should be <length> or less.
        errorCodeList.add("DE_014");// PPBL account no should be 12 digits and starts with 91.
        errorCodeList.add("DE_015");// Purpose not valid.
        errorCodeList.add("DE_016");// Month not valid.
        errorCodeList.add("DE_017");// Year not valid.
        errorCodeList.add("DE_018");// You can disburse the amount up to 6 months before from the current month.
        errorCodeList.add("DE_019");// MID is not valid.
        errorCodeList.add("DE_020");// Uploaded file is blank
        errorCodeList.add("DE_021");// Disburse amount should be Rs. 1 or more.
        errorCodeList.add("DE_022");// Disburse amount should not be more than Rs. 200000.
        errorCodeList.add("DE_023");// Mid is not active.
        errorCodeList.add("DE_024");// Account no should be alphanumeric with length between 9 to 18 digits.
        errorCodeList.add("DE_025");// IFSC code is invalid.
        errorCodeList.add("DE_026");// Uploaded file must be of csv format
        errorCodeList.add("DE_034");// Invalid Pay Mode.
        errorCodeList.add("DE_039");// No record found.
        errorCodeList.add("DE_040");// Duplicate order id.
        errorCodeList.add("DE_041");// Unable to process your request. Please try after some time.
        errorCodeList.add("DE_042");// Date format is not correct as it should be yyyy-MM-dd.
        errorCodeList.add("DE_050");// Callback URL passed is Invalid.
        errorCodeList.add("DE_051");// Disbursal to other bank accounts not supported
        errorCodeList.add("DE_052");// Bank Transfer Mode not valid
        errorCodeList.add("DE_053");// IMPS not allowed for amount more than 2 lakhs
        errorCodeList.add("DE_054");// SubwalletGuid is not correct, Please check and try again
        errorCodeList.add("DE_055");// Unable to disburse from given subWalletGuid. Please use another or create a new one
        errorCodeList.add("DE_056");// UPI not allowed for amount more than 1 lakhs
        errorCodeList.add("DE_057");// Invalid request
        errorCodeList.add("DE_400");// Invalid Request Parameter.
        errorCodeList.add("DE_401");// Authentication Parameters Required.
        errorCodeList.add("DE_402");// Access Denied.
        errorCodeList.add("DE_403");// API Not Found.
        errorCodeList.add("DE_404");// Unable to authenticate request.
        errorCodeList.add("DE_405");//Remote IP not whitelist. Please ask your admin to whitelist Remote IP: <IP Address> from Payouts dashboard under developer's settings.
        errorCodeList.add("DE_406");// Invalid Client-Token.
        errorCodeList.add("DE_407");// Client-Token Required.
        errorCodeList.add("DE_408");// Checksum Required.
        errorCodeList.add("DE_409");// Checksum Verification Failed.
        errorCodeList.add("DE_500");// System Error.
        errorCodeList.add("DE_602");// Disbursal to bank account failed. Please try after some time.
        errorCodeList.add("DE_603");// Disbursal to bank account failed.
        errorCodeList.add("DE_606");// Invalid account number.
        errorCodeList.add("DE_607");// Error at bank end.
        errorCodeList.add("DE_609");// Unable to fetch details.
        errorCodeList.add("DE_612");// Account blocked/frozen.
        errorCodeList.add("DE_613");// Invalid IFSC/MICR.
        errorCodeList.add("DE_614");// Account is closed.
        errorCodeList.add("DE_615");// Invalid account number or IFSC code.
        errorCodeList.add("DE_616");// Daily allowed amount limit for the beneficiary is exceeded.
        errorCodeList.add("DE_684");// Credit adjustment
        errorCodeList.add("DE_617");// Beneficiary account is NRE account.
        errorCodeList.add("DE_618");// Invalid NBIN (IFSC code).
        errorCodeList.add("DE_619");// NPCI rejects as net debit cap is exceeded for remitter.
        errorCodeList.add("DE_620");// Card is restricted at beneficiary account.
        errorCodeList.add("DE_621");// Transaction declined, Bank not enabled for P2A functionality.
        errorCodeList.add("DE_622");// Maximum limit amount exceeded
        errorCodeList.add("DE_623");// Exceeded maximum allowed number of transfers per day.
        errorCodeList.add("DE_625");// Invalid IFSC code.
        errorCodeList.add("DE_640");//Transaction has been declined as it exceeds the limits set by beneficiary bank.
        errorCodeList.add("DE_626");// Transaction is declined based on From account type and To account type.
        errorCodeList.add("DE_627");// Transaction is declined based on transaction limit imposed on the From account type and To account type.
        errorCodeList.add("DE_628");// Beneficiary PPI is Gift prepaid and this is not the first transaction.
        errorCodeList.add("DE_629");// Acquiring bank has not implemented customer initiated person-to-merchant transaction.
        errorCodeList.add("DE_631");// Amount is greater than 2 Lakhs (Applicable for Bank where NDC is greater than 2 Lakhs).
        errorCodeList.add("DE_632");// Invalid payment reference.
        errorCodeList.add("DE_634");// Invalid transaction.
        errorCodeList.add("DE_636");// Unable to process.
        errorCodeList.add("DE_641");// No record found.
        errorCodeList.add("DE_643");// Remmiter and beneficiary accounts are same.
        errorCodeList.add("DE_646");// Fund transfer not allowed for this transaction type.
        errorCodeList.add("DE_647");// Refund not allowed for this transaction type.
        errorCodeList.add("DE_648");// Product Service Unavailable.
        errorCodeList.add("DE_649");// Transaction cannot be processed as current account limits will be breached.
        errorCodeList.add("DE_650");// CA Form 60 Val Current Account Form 60 Daily deposit limit breached.
        errorCodeList.add("DE_651");// Fraud verification Maquette suspicious response.
        errorCodeList.add("DE_652");// Nodal IMPS Nodal IMPS Amount Limit Breached.
        errorCodeList.add("DE_653");// Nodal IMPS Beneficiary Does Not exist.
        errorCodeList.add("DE_656");// Credit account freeze.
        errorCodeList.add("DE_657");// Host (CBS) offline / Beneficiary node offline
        errorCodeList.add("DE_658");// Account does not exist in CBS
        errorCodeList.add("DE_659");// Bank is not enabled for IMPS P2U
        errorCodeList.add("DE_660");// This IFSC is no longer valid. Please update account details
        errorCodeList.add("DE_661");// Invalid payee mobile
        errorCodeList.add("DE_662");// Invalid virtual address
        errorCodeList.add("DE_663");// Money transfer is not allowed to receiver account
        errorCodeList.add("DE_664");// Money transfer request declined by the receiver bank. Please try again
        errorCodeList.add("DE_665");// Payee bank UPI not supported
        errorCodeList.add("DE_666");// Money transfer request declined by the remitter bank
        errorCodeList.add("DE_667");// Bank account is not active
        errorCodeList.add("DE_668");// UPI user not found
        errorCodeList.add("DE_669");// UPI not registered or UPI registered but bank a/c not linked
        errorCodeList.add("DE_670");// Your request was unsuccessful. Please try again
        errorCodeList.add("DE_671");// You have exceeded the daily transaction amount limit set by your bank
        errorCodeList.add("DE_672");// You can not send money to this UPI ID
        errorCodeList.add("DE_673");// Invalid UPI id
        errorCodeList.add("DE_674");// Beneficiary bank account details are not correct
        errorCodeList.add("DE_675");// Something went wrong at the receiver bank servers. Please try again
        errorCodeList.add("DE_676");// Beneficiary account does not exist
        errorCodeList.add("DE_677");// Request declined by the receiver bank
        errorCodeList.add("DE_678");// Invalid payer UPI address
        errorCodeList.add("DE_679");// Beneficiary bank rejected as account type is not supported
        errorCodeList.add("DE_680");// Beneficiary bank rejected credit for a reason. Eg: name missing/does not match
        errorCodeList.add("DE_681");// User is not a Paytm UPI registered user
        errorCodeList.add("DE_682");// UPI VPA does not exist
        errorCodeList.add("DE_683");// Money transfer declined by UPI payment service provider
        errorCodeList.add("DE_701");// Merchant does not exists.
        errorCodeList.add("DE_702");// User doesn't exist
        errorCodeList.add("DE_703");// Wallet could not found, please verify walletGuid.
        errorCodeList.add("DE_704");// Sub wallet not found.
        errorCodeList.add("DE_705");// Agent wallet balance can't be negative. Please fund your wallet and try again.
        errorCodeList.add("DE_027");// File must contain 4 headers
        errorCodeList.add("DE_028");// Cannot exceed columns more than <length>
        errorCodeList.add("DE_029");// Total amount to be disbursed is more than the subwallet balance
        errorCodeList.add("DE_030");// File already exists with this name
        errorCodeList.add("DE_035");// Your request has been rejected by your configured Approver.
        errorCodeList.add("DE_036");// Uploaded file contains records more than <param> records
        errorCodeList.add("DE_043");// Duplicate Account Number Found in the file
        errorCodeList.add("DE_058");// Merchant Commission not configured
        errorCodeList.add("DE_065");// Velocity limit exceeded
        errorCodeList.add("DE_110");//RE VPA Validation Failed
        errorCodeList.add("DE_610");// No fds available
        errorCodeList.add("DE_633");// Invalid amount
        errorCodeList.add("DE_637");// Blocked/Lost/Hotlisted card
        errorCodeList.add("DE_638");// Expired card
        errorCodeList.add("DE_639");// Debit Request Failure
        errorCodeList.add("DE_654");// Refund Processing Service No successful parent transaction found for request.
        errorCodeList.add("DE_655");// Refund Processing service Refund amount is greater than max amount available for refund
        errorCodeList.add("DE_685");// Fresh reversal
        errorCodeList.add("DE_686");// Credit adjustment
        errorCodeList.add("DE_689");// Inactive/Dormant Account
        errorCodeList.add("DE_690");// Money transfer request declined by the receiver bank
        errorCodeList.add("DE_691");//Disbursal to bank account failed. Please try after some time.
        errorCodeList.add("DE_706");//Refund is not possible as refund possibly already done or current refund request may cause refund to exceed actual transaction amount
        errorCodeList.add("DE_707");//Refund amount should be equal to the transaction amount
        errorCodeList.add("DE_708");//Sub wallet guid is not associated with this merchant
        errorCodeList.add("DE_709");//Invalid Transaction Amount
        errorCodeList.add("DE_121");// Could not find details for input beneficiary contactRefId
        errorCodeList.add("DE_121");// Could not fetch details for the contactRefId. Please try again
        errorCodeList.add("DE_122");// Multiple matches found for given beneficiary contactRefId
        errorCodeList.add("DE_692");// Beneficiary bank doe not allow transactions for lower amounts

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
   public static boolean isErrorCodeExists(String errorCode){
       if(errorCodeList.contains(errorCode)) {
           return  true;
       }
       return  false;
    }

}
