package com.payment.airtel;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpStatus;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Created by admin on 01-Mar-22.
 */
public class AirtelUgandaUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(AirtelUgandaUtils.class.getName());
    protected static String DEFAULT_ENCRYPTION_ALGORITHM = "RSA";
    protected static String DEFAULT_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static HashMap<String, String> countryCodeHash = new HashMap<>();
    private static Functions functions = new Functions();


    public static PublicKey getPublicKey(String base64PublicKey) {
        PublicKey publicKey = null;
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance(DEFAULT_ENCRYPTION_ALGORITHM);
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }
    public static String encrypt(String data, String publicKey) throws BadPaddingException, IllegalBlockSizeException,
            InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance(DEFAULT_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }


    public static String doGetHTTPSURLConnectionClient(String url, String country, String currency, String authToken) throws PZTechnicalViolationException
    {
        //transactionlogger.error("url--->" + url);
        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();

        HttpClient client   = new HttpClient();
        GetMethod method    = new GetMethod(url);
        String result       = "";
        try
        {
            method.setRequestHeader("Content-Type", "application/json");
            method.setRequestHeader("X-Country", country);
            method.setRequestHeader("X-Currency", currency);
            method.setRequestHeader("Authorization", authToken);


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

    public static String doPostHTTPSURLConnectionClient(String strURL, String request, String country, String currency, String authToken) throws PZTechnicalViolationException
    {

        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
            post.setRequestHeader("Content-Type", "application/json");
            post.setRequestHeader("X-Country", country);
            post.setRequestHeader("X-Currency", currency);
            post.setRequestHeader("Authorization", authToken);
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException httpException)
        {
            transactionLogger.error("AirtelMoneyUtils HttpException----- ", httpException);

        }
        catch (IOException ioException)
        {
            transactionLogger.error("AirtelMoneyUtils IOException----- ", ioException);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String doPostHTTPSURLConnectionClientAuthToken(String strURL, String request) throws PZTechnicalViolationException
    {

        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
//            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestHeader("Content-Type", "application/json");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException httpException)
        {
            transactionLogger.error("AirtelMoneyUtils HttpException----- ", httpException);

        }
        catch (IOException ioException)
        {
            transactionLogger.error("AirtelMoneyUtils IOException----- ", ioException);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String getAuthToken(String url, String clientId, String clientSecret, String grantType) throws PZTechnicalViolationException, JSONException
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("client_id", clientId);
        jsonObject.put("client_secret", clientSecret);
        jsonObject.put("grant_type", grantType);
        String response = doPostHTTPSURLConnectionClientAuthToken(url, jsonObject.toString());
        String authToken = "";

        if(functions.isJSONValid(response))
        {
            JSONObject jsonResponse = new JSONObject(response);
            if(jsonResponse.has("access_token") && functions.isValueNull(jsonResponse.getString("access_token")))
                authToken = jsonResponse.getString("access_token");
        }

        return authToken;
    }

    public static CommRequestVO getCommRequestFromUtils(CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("inside AirtelUgandaUtils getCommRequestFromUtils");
        CommRequestVO commRequestVO = new CommRequestVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();

        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();

        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();

        addressDetailsVO.setFirstname(genericAddressDetailsVO.getFirstname());
        addressDetailsVO.setLastname(genericAddressDetailsVO.getLastname());
        addressDetailsVO.setStreet(genericAddressDetailsVO.getStreet());
        addressDetailsVO.setCity(genericAddressDetailsVO.getCity());
        addressDetailsVO.setPhone(genericAddressDetailsVO.getPhone());
        addressDetailsVO.setState(genericAddressDetailsVO.getState());
        addressDetailsVO.setIp(genericAddressDetailsVO.getIp());
        addressDetailsVO.setEmail(genericAddressDetailsVO.getEmail());
        addressDetailsVO.setZipCode(genericAddressDetailsVO.getZipCode());
        addressDetailsVO.setCountry(genericAddressDetailsVO.getCountry());

        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setCustomerId(commonValidatorVO.getCustomerId());

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }


    static
    {
        countryCodeHash.put("ALA","AX");
        countryCodeHash.put("AFG","AF");
        countryCodeHash.put("ALB","AL");
        countryCodeHash.put("DZA","DZ");
        countryCodeHash.put("ASM","AS");
        countryCodeHash.put("AND","AD");
        countryCodeHash.put("AGO","AO");
        countryCodeHash.put("AIA","AI");
        countryCodeHash.put("ATA","AQ");
        countryCodeHash.put("ATG","AG");
        countryCodeHash.put("ARG","AR");
        countryCodeHash.put("ARM","AM");
        countryCodeHash.put("ABW","AW");
        countryCodeHash.put("AUS","AU");
        countryCodeHash.put("AUT","AT");
        countryCodeHash.put("AZE","AZ");
        countryCodeHash.put("BHS","BS");
        countryCodeHash.put("BHR","BH");
        countryCodeHash.put("BGD","BD");
        countryCodeHash.put("BRB","BB");
        countryCodeHash.put("BLR","BY");
        countryCodeHash.put("BEL","BE");
        countryCodeHash.put("BLZ","BZ");
        countryCodeHash.put("BEN","BJ");
        countryCodeHash.put("BMU","BM");
        countryCodeHash.put("BTN","BT");
        countryCodeHash.put("BOL","BO");
        countryCodeHash.put("BIH","BA");
        countryCodeHash.put("BWA","BW");
        countryCodeHash.put("BVT","BV");
        countryCodeHash.put("BRA","BR");
        countryCodeHash.put("IOT","IO");
        countryCodeHash.put("BRN","BN");
        countryCodeHash.put("BGR","BG");
        countryCodeHash.put("BFA","BF");
        countryCodeHash.put("BDI","BI");
        countryCodeHash.put("KHM","KH");
        countryCodeHash.put("CMR","CM");
        countryCodeHash.put("CAN","CA");
        countryCodeHash.put("CPV","CV");
        countryCodeHash.put("CYM","KY");
        countryCodeHash.put("CAF","CF");
        countryCodeHash.put("TCD","TD");
        countryCodeHash.put("CHL","CL");
        countryCodeHash.put("CHN","CN");
        countryCodeHash.put("CXR","CX");
        countryCodeHash.put("CCK","CC");
        countryCodeHash.put("COL","CO");
        countryCodeHash.put("COM","KM");
        countryCodeHash.put("COD","CD");
        countryCodeHash.put("COG","CG");
        countryCodeHash.put("COK","CK");
        countryCodeHash.put("CRI","CR");
        countryCodeHash.put("CIV","CI");
        countryCodeHash.put("HRV","HR");
        countryCodeHash.put("CUB","CU");
        countryCodeHash.put("CYP","CY");
        countryCodeHash.put("CZE","CZ");
        countryCodeHash.put("DNK","DK");
        countryCodeHash.put("DJI","DJ");
        countryCodeHash.put("DMA","DM");
        countryCodeHash.put("DOM","DO");
        countryCodeHash.put("ECU","EC");
        countryCodeHash.put("EGY","EG");
        countryCodeHash.put("SLV","SV");
        countryCodeHash.put("GNQ","GQ");
        countryCodeHash.put("ERI","ER");
        countryCodeHash.put("EST","EE");
        countryCodeHash.put("ETH","ET");
        countryCodeHash.put("FLK","FK");
        countryCodeHash.put("FRO","FO");
        countryCodeHash.put("FJI","FJ");
        countryCodeHash.put("FIN","FI");
        countryCodeHash.put("FRA","FR");
        countryCodeHash.put("GUF","GF");
        countryCodeHash.put("PYF","PF");
        countryCodeHash.put("ATF","TF");
        countryCodeHash.put("GAB","GA");
        countryCodeHash.put("GMB","GM");
        countryCodeHash.put("GEO","GE");
        countryCodeHash.put("DEU","DE");
        countryCodeHash.put("GHA","GH");
        countryCodeHash.put("GIB","GI");
        countryCodeHash.put("GRC","GR");
        countryCodeHash.put("GRL","GL");
        countryCodeHash.put("GRD","GD");
        countryCodeHash.put("GLP","GP");
        countryCodeHash.put("GUM","GU");
        countryCodeHash.put("GTM","GT");
        countryCodeHash.put("GIN","GN");
        countryCodeHash.put("GNB","GW");
        countryCodeHash.put("GUY","GY");
        countryCodeHash.put("HTI","HT");
        countryCodeHash.put("HMD","HM");
        countryCodeHash.put("HND","HN");
        countryCodeHash.put("HKG","HK");
        countryCodeHash.put("HUN","HU");
        countryCodeHash.put("ISL","IS");
        countryCodeHash.put("IND","IN");
        countryCodeHash.put("IDN","ID");
        countryCodeHash.put("IRN","IR");
        countryCodeHash.put("IRQ","IQ");
        countryCodeHash.put("IRL","IE");
        countryCodeHash.put("ISR","IL");
        countryCodeHash.put("ITA","IT");
        countryCodeHash.put("JAM","JM");
        countryCodeHash.put("JPN","JP");
        countryCodeHash.put("JOR","JO");
        countryCodeHash.put("KAZ","KZ");
        countryCodeHash.put("KEN","KE");
        countryCodeHash.put("KIR","KI");
        countryCodeHash.put("PRK","KP");
        countryCodeHash.put("KOR","KR");
        countryCodeHash.put("KWT","KW");
        countryCodeHash.put("KGZ","KG");
        countryCodeHash.put("LAO","LA");
        countryCodeHash.put("LVA","LV");
        countryCodeHash.put("LBN","LB");
        countryCodeHash.put("LSO","LS");
        countryCodeHash.put("LBR","LR");
        countryCodeHash.put("LBY","LY");
        countryCodeHash.put("LIE","LI");
        countryCodeHash.put("LTU","LT");
        countryCodeHash.put("LUX","LU");
        countryCodeHash.put("MAC","MO");
        countryCodeHash.put("MKD","MK");
        countryCodeHash.put("MDG","MG");
        countryCodeHash.put("MWI","MW");
        countryCodeHash.put("MYS","MY");
        countryCodeHash.put("MDV","MV");
        countryCodeHash.put("MLI","ML");
        countryCodeHash.put("MLT","MT");
        countryCodeHash.put("MHL","MH");
        countryCodeHash.put("MTQ","MQ");
        countryCodeHash.put("MRT","MR");
        countryCodeHash.put("MUS","MU");
        countryCodeHash.put("MYT","YT");
        countryCodeHash.put("MEX","MX");
        countryCodeHash.put("FSM","FM");
        countryCodeHash.put("MDA","MD");
        countryCodeHash.put("MCO","MC");
        countryCodeHash.put("MNG","MN");
        countryCodeHash.put("MSR","MS");
        countryCodeHash.put("MAR","MA");
        countryCodeHash.put("MOZ","MZ");
        countryCodeHash.put("MMR","MM");
        countryCodeHash.put("NAM","NA");
        countryCodeHash.put("NRU","NR");
        countryCodeHash.put("NPL","NP");
        countryCodeHash.put("NLD","NL");
        countryCodeHash.put("ANT","AN");
        countryCodeHash.put("NCL","NC");
        countryCodeHash.put("NZL","NZ");
        countryCodeHash.put("NIC","NI");
        countryCodeHash.put("NER","NE");
        countryCodeHash.put("NGA","NG");
        countryCodeHash.put("NIU","NU");
        countryCodeHash.put("NFK","NF");
        countryCodeHash.put("MNP","MP");
        countryCodeHash.put("NOR","NO");
        countryCodeHash.put("OMN","OM");
        countryCodeHash.put("PAK","PK");
        countryCodeHash.put("PLW","PW");
        countryCodeHash.put("PSE","PS");
        countryCodeHash.put("PAN","PA");
        countryCodeHash.put("PNG","PG");
        countryCodeHash.put("PRY","PY");
        countryCodeHash.put("PER","PE");
        countryCodeHash.put("PHL","PH");
        countryCodeHash.put("PCN","PN");
        countryCodeHash.put("POL","PL");
        countryCodeHash.put("PRT","PT");
        countryCodeHash.put("PRI","PR");
        countryCodeHash.put("QAT","QA");
        countryCodeHash.put("REU","RE");
        countryCodeHash.put("ROU","RO");
        countryCodeHash.put("RUS","RU");
        countryCodeHash.put("RWA","RW");
        countryCodeHash.put("SHN","SH");
        countryCodeHash.put("KNA","KN");
        countryCodeHash.put("LCA","LC");
        countryCodeHash.put("SPM","PM");
        countryCodeHash.put("VCT","VC");
        countryCodeHash.put("WSM","WS");
        countryCodeHash.put("SMR","SM");
        countryCodeHash.put("STP","ST");
        countryCodeHash.put("SAU","SA");
        countryCodeHash.put("SEN","SN");
        countryCodeHash.put("SCG","CS");
        countryCodeHash.put("SYC","SC");
        countryCodeHash.put("SLE","SL");
        countryCodeHash.put("SGP","SG");
        countryCodeHash.put("SVK","SK");
        countryCodeHash.put("SVN","SI");
        countryCodeHash.put("SLB","SB");
        countryCodeHash.put("SOM","SO");
        countryCodeHash.put("ZAF","ZA");
        countryCodeHash.put("SGS","GS");
        countryCodeHash.put("ESP","ES");
        countryCodeHash.put("LKA","LK");
        countryCodeHash.put("SDN","SD");
        countryCodeHash.put("SUR","SR");
        countryCodeHash.put("SJM","SJ");
        countryCodeHash.put("SWZ","SZ");
        countryCodeHash.put("SWE","SE");
        countryCodeHash.put("CHE","CH");
        countryCodeHash.put("SYR","SY");
        countryCodeHash.put("TWN","TW");
        countryCodeHash.put("TJK","TJ");
        countryCodeHash.put("TZA","TZ");
        countryCodeHash.put("THA","TH");
        countryCodeHash.put("TLS","TL");
        countryCodeHash.put("TGO","TG");
        countryCodeHash.put("TKL","TK");
        countryCodeHash.put("TON","TO");
        countryCodeHash.put("TTO","TT");
        countryCodeHash.put("TUN","TN");
        countryCodeHash.put("TUR","TR");
        countryCodeHash.put("TKM","TM");
        countryCodeHash.put("TCA","TC");
        countryCodeHash.put("TUV","TV");
        countryCodeHash.put("UGA","UG");
        countryCodeHash.put("UKR","UA");
        countryCodeHash.put("ARE","AE");
        countryCodeHash.put("GBR","GB");
        countryCodeHash.put("USA","US");
        countryCodeHash.put("UGA","UG");
        countryCodeHash.put("UMI","UM");
        countryCodeHash.put("URY","UY");
        countryCodeHash.put("UZB","UZ");
        countryCodeHash.put("VUT","VU");
        countryCodeHash.put("VAT","VA");
        countryCodeHash.put("VEN","VE");
        countryCodeHash.put("VNM","VN");
        countryCodeHash.put("VGB","VG");
        countryCodeHash.put("VIR","VI");
        countryCodeHash.put("WLF","WF");
        countryCodeHash.put("ESH","EH");
        countryCodeHash.put("YEM","YE");
        countryCodeHash.put("ZMB","ZM");
        countryCodeHash.put("ZWE","ZW");
    }


    public static String getCountryCodeHash(String countryCode)
    {
        return countryCodeHash.get(countryCode);
    }

    public static Boolean updateTransaction (String trackingid, String errorCode){

        transactionLogger.error("in side  updateTransaction----------->");
        Connection con = null;
        PreparedStatement psUpdateTransaction = null;
        boolean isUpdate=false;
        try
        {

            con= Database.getConnection();
            String update = "update transaction_common_details set responsecode = ? where trackingid=? and status = 'authstarted'" ;
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,errorCode);
            psUpdateTransaction.setString(2,trackingid);
            transactionLogger.error("transaction common query----"+psUpdateTransaction);
            int i=psUpdateTransaction.executeUpdate();
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

    public static Boolean updatePayoutTransaction (String trackingid, String telnocc, String telno, String country){

        transactionLogger.error("in side updateTransaction----------->");
        Connection con = null;
        PreparedStatement psUpdateTransaction = null;
        boolean isUpdate=false;
        try
        {

            con= Database.getConnection();
            String update = "update transaction_common set telnocc = ?, telno = ?, country = ? where trackingid=?" ;
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,telnocc);
            psUpdateTransaction.setString(2,telno);
            psUpdateTransaction.setString(3,country);
            psUpdateTransaction.setString(4,trackingid);
            transactionLogger.error("transaction common query----"+psUpdateTransaction);
            int i=psUpdateTransaction.executeUpdate();
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


