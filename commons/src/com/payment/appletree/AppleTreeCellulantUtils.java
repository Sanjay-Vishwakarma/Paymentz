package com.payment.appletree;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.google.gson.JsonObject;
import com.manager.vo.ReserveField2VO;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.cybersource2.PKCS1EncodedKeySpec;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.Signature;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by Admin on 7/19/2021.
 */
public class AppleTreeCellulantUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(AppleTreeCellulantUtils.class.getName());
    private static HashMap<String,String> countryCodeHash=new HashMap<>();
    static {
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
    public static String getPaymentMethod(String cardType)
    {
        String paymentMethod = "";

        if ("VISA".equalsIgnoreCase(cardType))
            paymentMethod = "00";
        else if ("MC".equalsIgnoreCase(cardType))
            paymentMethod = "01";

        return paymentMethod;
    }

    public static String doPostHTTPSURLConnectionClient(String strURL, String request,String authentication,String MD,String PASS) throws PZTechnicalViolationException
    {
        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        System.out.println("authentication:::" + authentication);
        System.out.println("authentication:::" + strURL);

        System.out.println("MerchantId:::" + MD);
        System.out.println("MerchantPassword:::" + PASS);
        try
        {

            post.addRequestHeader("MerchantId", MD);
            post.addRequestHeader("MerchantPassword", PASS);
            post.addRequestHeader("Signature", authentication);
            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("AppleTreeCellulantUtils:: HttpException-----", he);
            PZExceptionHandler.raiseTechnicalViolationException(AppleTreeCellulantUtils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("AppleTreeCellulantUtils:: IOException-----", io);
            PZExceptionHandler.raiseTechnicalViolationException(AppleTreeCellulantUtils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }
    public static String doPostHTTPSURLConnectionClient(String strURL, String request) throws PZTechnicalViolationException
    {
        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("AppleTreeCellulantUtils:: HttpException-----", he);
            PZExceptionHandler.raiseTechnicalViolationException(AppleTreeCellulantUtils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("AppleTreeCellulantUtils:: IOException-----", io);
            PZExceptionHandler.raiseTechnicalViolationException(AppleTreeCellulantUtils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }
    public static String getSignature(String plainText,String privatePath) {
        BufferedReader br=null;
        try {
            transactionLogger.error("plainText---->"+plainText);
            System.out.println("plainText---->"+plainText);
            File file = new File(privatePath);

            br = new BufferedReader(new FileReader(file));

            String privateKey = "", st;

            while ((st = br.readLine()) != null) {
                privateKey += st;
            }
            String realPK = privateKey.replaceAll("-----BEGIN RSA PRIVATE KEY-----", "")
                    .replaceAll("-----END RSA PRIVATE KEY-----", "")
                    .replaceAll("\\r\\n|\\r|\\n", "");

            byte[] keyBytes = Base64.getDecoder().decode(realPK);
            PKCS1EncodedKeySpec spec = new PKCS1EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(kf.generatePrivate(spec.getKeySpec()));
            signature.update(plainText.getBytes("UTF-8"));
            byte[] signedBytes = signature.sign();

            return Base64.getEncoder().encodeToString(signedBytes);
        } catch (Exception ex) {
            transactionLogger.error("Exception--->", ex);
            System.out.println("Exception--->"+ ex);

        }finally
        {
            try
            {

                if(br!=null)
                    br.close();
            }
            catch (IOException e)
            {
                transactionLogger.error("IOException--->",e);
            }

        }
        return "";
    }


    public static String getRrn(String trackingId)
    {
        String rrn="";
        Connection con=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        try{
            con= Database.getConnection();
            String query="select rrn from transaction_common_details where status='3D_authstarted' and trackingId=?";
            ps=con.prepareStatement(query);
            ps.setString(1,trackingId);
            rs=ps.executeQuery();
            while (rs.next())
            {
                rrn=rs.getString("rrn");
            }
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError-----", systemError);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException-----", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return rrn;
    }
    public static String getCountryTwoDigit(String countryCode)
    {
        return countryCodeHash.get(countryCode);
    }

    public static StringBuffer InitialRequest(AppletreeRequestVo appletreeRequestVo){
        StringBuffer InitialRequest             = new StringBuffer();
        JSONObject jsonObject                   = new JSONObject();
        JSONObject RequestDetails               = new JSONObject();
        JSONObject CustomerDetails              = new JSONObject();
        JSONObject OrderDetails                 = new JSONObject();
        JSONObject PaymentOptionDetails         = new JSONObject();
        JSONObject PayerAuthenticationDetails   = new JSONObject();
        JSONObject MerchantDetails              = new JSONObject();
        StringBuffer OrderDetailsStr            = new StringBuffer();

        Functions functions =  new Functions();
        try
        {

                //Start RequestDetails
                /*if(functions.isValueNull(appletreeRequestVo.getRequestId())){
                    RequestDetails.put("Id",appletreeRequestVo.getRequestId());
                }else{
                    RequestDetails.put("Id","");
                }
                if(functions.isValueNull(appletreeRequestVo.getServiceCode())){
                    RequestDetails.put("ServiceCode",appletreeRequestVo.getServiceCode());
                }else{
                    RequestDetails.put("ServiceCode","");
                }

                if(functions.isValueNull(appletreeRequestVo.getProcessingStage())){
                    RequestDetails.put("ProcessingStage",Integer.parseInt(appletreeRequestVo.getProcessingStage()));
                }else{
                    RequestDetails.put("ProcessingStage",2);
                }

                if(functions.isValueNull(appletreeRequestVo.getCreationDateTime())){
                    RequestDetails.put("CreationDateTime",appletreeRequestVo.getCreationDateTime());
                }else{
                    RequestDetails.put("CreationDateTime","");
                }
                if(functions.isValueNull(appletreeRequestVo.getExpiryDateTime())){
                    RequestDetails.put("ExpiryDateTime",appletreeRequestVo.getExpiryDateTime());
                }else{
                    RequestDetails.put("ExpiryDateTime","");
                }
                //End RequestDetails

                //Start CustomerDetails
                if(functions.isValueNull(appletreeRequestVo.getFirstName())){
                    CustomerDetails.put("FirstName",appletreeRequestVo.getFirstName());
                }else{
                    CustomerDetails.put("FirstName","");
                }
                if(functions.isValueNull(appletreeRequestVo.getLastName())){
                    CustomerDetails.put("LastName",appletreeRequestVo.getLastName());
                }else{
                    CustomerDetails.put("LastName","");
                }
                if(functions.isValueNull(appletreeRequestVo.getStreetAddress())){
                    CustomerDetails.put("StreetAddress",appletreeRequestVo.getStreetAddress());
                }else{
                    CustomerDetails.put("StreetAddress","");
                }
                if(functions.isValueNull(appletreeRequestVo.getLocality())){
                    CustomerDetails.put("Locality",appletreeRequestVo.getLocality());
                }else{
                    CustomerDetails.put("Locality","");
                }
                if(functions.isValueNull(appletreeRequestVo.getState())){
                    CustomerDetails.put("AdministrativeArea",appletreeRequestVo.getState());
                }else{
                    CustomerDetails.put("AdministrativeArea","");
                }
                if(functions.isValueNull(appletreeRequestVo.getPostalcode())){
                    CustomerDetails.put("PostalCode",appletreeRequestVo.getPostalcode());
                }else{
                    CustomerDetails.put("PostalCode","");
                }
                if(functions.isValueNull(appletreeRequestVo.getCountryCode())){
                    CustomerDetails.put("Country",appletreeRequestVo.getCountryCode());
                }else{
                    CustomerDetails.put("Country","");
                }
                if(functions.isValueNull(appletreeRequestVo.getMobileNumber())){
                    CustomerDetails.put("MobileNumber",appletreeRequestVo.getMobileNumber());
                }else{
                    CustomerDetails.put("MobileNumber","");
                }
                if(functions.isValueNull(appletreeRequestVo.getEmailAddress())){
                    CustomerDetails.put("EmailAddress",appletreeRequestVo.getEmailAddress());
                }else{
                    CustomerDetails.put("EmailAddress","");
                }
                //End CustomerDetails

                //Start OrderDetails
                if(functions.isValueNull(appletreeRequestVo.getCurrency())){
                    OrderDetails.put("Currency",appletreeRequestVo.getCurrency());
                }else{
                    OrderDetails.put("Currency","");
                }
                if(functions.isValueNull(appletreeRequestVo.getAmount())){
                    //double amount = Double.parseDouble(appletreeRequestVo.getAmount());
                    OrderDetails.put("Amount",appletreeRequestVo.getAmount());
                }else{
                    OrderDetails.put("Amount",0.00);
                }
                if(functions.isValueNull(appletreeRequestVo.getOrderid())){
                    OrderDetails.put("PaymentDetails",appletreeRequestVo.getOrderid());
                }else{
                    OrderDetails.put("PaymentDetails","");
                }
                //End OrderDetails

                //Start PaymentOptionDetails
                if(functions.isValueNull(appletreeRequestVo.getOptionalCode())){
                    PaymentOptionDetails.put("PaymentOptionCode",appletreeRequestVo.getOptionalCode());
                }else{
                    PaymentOptionDetails.put("PaymentOptionCode","");
                }
                if(functions.isValueNull(appletreeRequestVo.getCardNo())){
                    PaymentOptionDetails.put("AccountNumber",appletreeRequestVo.getCardNo());
                }else{
                    PaymentOptionDetails.put("AccountNumber","");
                }
                if(functions.isValueNull(appletreeRequestVo.getExpMonth())){
                    PaymentOptionDetails.put("ExpirationMonth",appletreeRequestVo.getExpMonth());
                }else{
                    PaymentOptionDetails.put("ExpirationMonth","");
                }
                if(functions.isValueNull(appletreeRequestVo.getExpYear())){
                    PaymentOptionDetails.put("ExpirationYear",appletreeRequestVo.getExpYear());
                }else{
                    PaymentOptionDetails.put("ExpirationYear","");
                }
                if(functions.isValueNull(appletreeRequestVo.getCVV())){
                    PaymentOptionDetails.put("VerificationValue",appletreeRequestVo.getCVV());
                }else{
                    PaymentOptionDetails.put("VerificationValue","");
                }
                if(functions.isValueNull(appletreeRequestVo.getReturnURL())){
                    PaymentOptionDetails.put("ReturnURL",appletreeRequestVo.getReturnURL());
                }else{
                    PaymentOptionDetails.put("ReturnURL","");
                }

                PaymentOptionDetails.put("PaymentPanelBackgroundColor","");
                //End PaymentOptionDetails

                //Start PayerAuthenticationDetails
                if(functions.isValueNull(appletreeRequestVo.getAuthCode())){
                    PayerAuthenticationDetails.put("AuthenticationId",appletreeRequestVo.getAuthCode());
                }else{
                    PayerAuthenticationDetails.put("AuthenticationId","");
                }
                if(functions.isValueNull(appletreeRequestVo.getXID())){
                    PayerAuthenticationDetails.put("XID",appletreeRequestVo.getXID());
                }else{
                    PayerAuthenticationDetails.put("XID","");
                }
                if(functions.isValueNull(appletreeRequestVo.getACSURL())){
                    PayerAuthenticationDetails.put("ACSURL",appletreeRequestVo.getACSURL());
                }else{
                    PayerAuthenticationDetails.put("ACSURL","");
                }
                if(functions.isValueNull(appletreeRequestVo.getPAReq())){
                    PayerAuthenticationDetails.put("PAReq",appletreeRequestVo.getPAReq());
                }else{
                    PayerAuthenticationDetails.put("PAReq","");
                }
                if(functions.isValueNull(appletreeRequestVo.getPARes())){
                    PayerAuthenticationDetails.put("PARes",appletreeRequestVo.getPARes());
                }else{
                    PayerAuthenticationDetails.put("PARes","");
                }
            //End PayerAuthenticationDetails

            //Star MerchantDetails
            if(functions.isValueNull(appletreeRequestVo.getMerchantId())){
                MerchantDetails.put("MerchantId",appletreeRequestVo.getMerchantId());
            }else{
                MerchantDetails.put("MerchantId","");
            }
            if(functions.isValueNull(appletreeRequestVo.getStoreId())){
                MerchantDetails.put("StoreId",appletreeRequestVo.getStoreId());
            }else{
                MerchantDetails.put("StoreId","");
            }
            if(functions.isValueNull(appletreeRequestVo.getTerminalId())){
                MerchantDetails.put("TerminalId",appletreeRequestVo.getTerminalId());
            }else{
                MerchantDetails.put("TerminalId","");
            }
            //End MerchantDetails*/




            /*jsonObject.put("RequestDetails", RequestDetails);
            jsonObject.put("CustomerDetails",CustomerDetails);
            jsonObject.put("OrderDetails",OrderDetailsStr);
            jsonObject.put("PaymentOptionDetails",PaymentOptionDetails);
            jsonObject.put("PayerAuthenticationDetails",PayerAuthenticationDetails);
            jsonObject.put("MerchantDetails",MerchantDetails);*/

            transactionLogger.error("jsonObject------------> " + jsonObject.toString());

            InitialRequest.append("{" +
                    "\"RequestDetails\": {" +
                        "\"Id\":\"" + appletreeRequestVo.getRequestId() + "\"," +
                        "\"ProcessingStage\":" + appletreeRequestVo.getProcessingStage() + "," +
                        "\"ServiceCode\":\"" + appletreeRequestVo.getServiceCode() + "\"," +
                        "\"CreationDateTime\":\"" + appletreeRequestVo.getCreationDateTime() + "\"," +
                        "\"ExpiryDateTime\":\"" + appletreeRequestVo.getExpiryDateTime() + "\"" +
                    "}," +
                    "\"CustomerDetails\":{" +
                        "\"FirstName\":\"" + appletreeRequestVo.getFirstName() + "\"," +
                        "\"LastName\":\"" + appletreeRequestVo.getLastName() + "\"," +
                        "\"StreetAddress\":\"" + appletreeRequestVo.getStreetAddress() + "\"," +
                        "\"Locality\":\"" + appletreeRequestVo.getLocality() + "\"," +
                        "\"AdministrativeArea\": \"" + appletreeRequestVo.getState() + "\"," +
                        "\"PostalCode\": \"" + appletreeRequestVo.getPostalcode() + "\"," +
                        "\"Country\":\"" + appletreeRequestVo.getCountryCode() + "\"," +
                        "\"MobileNumber\":\"" + appletreeRequestVo.getMobileNumber() + "\"," +
                        "\"EmailAddress\":\"" + appletreeRequestVo.getEmailAddress() + "\"" +
                    "}," +
                    "\"OrderDetails\":{" +
                        "\"Currency\":\"" + appletreeRequestVo.getCurrency() + "\"," +
                        "\"Amount\":" + appletreeRequestVo.getAmount() + "," +
                        "\"PaymentDetails\":\"" + appletreeRequestVo.getOrderid() + "\"" +
                    "}," +
                    "\"PaymentOptionDetails\":{" +
                        "\"PaymentOptionCode\":\"" + appletreeRequestVo.getOptionalCode() + "\"," +
                        "\"AccountNumber\":\"" + appletreeRequestVo.getCardNo() + "\"," +
                        "\"ExpirationMonth\":\"" + appletreeRequestVo.getExpMonth() + "\"," +
                        "\"ExpirationYear\":\"" + appletreeRequestVo.getExpYear() + "\"," +
                        "\"VerificationValue\":\"" + appletreeRequestVo.getCVV() + "\"," +
                        "\"ReturnURL\":\"" + appletreeRequestVo.getReturnURL() + "\"," +
                        "\"PaymentPanelBackgroundColor\":\"\"" +
                    "}," +
                    "\"PayerAuthenticationDetails\":{" +
                        "\"AuthenticationId\":\"" + appletreeRequestVo.getAuthCode() + "\"," +
                        "\"XID\":\"" + appletreeRequestVo.getXID() + "\"," +
                        "\"ACSURL\":\"" + appletreeRequestVo.getACSURL() + "\"," +
                        "\"PAReq\":\"" + appletreeRequestVo.getPAReq() + "\"," +
                        "\"PARes\":\"" + appletreeRequestVo.getPARes() + "\"" +
                    "}," +
                    "\"MerchantDetails\":{" +
                        "\"MerchantId\":\"" + appletreeRequestVo.getMerchantId() + "\"," +
                        "\"StoreId\":\"" + appletreeRequestVo.getStoreId() + "\"," +
                        "\"TerminalId\":\"" + appletreeRequestVo.getTerminalId() + "\"" +
                        "}" +
                    "}");




        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return InitialRequest;
    }

    public static StringBuffer Initial3dRequest(AppletreeRequestVo appletreeRequestVo){
        StringBuffer InitialRequest = new StringBuffer();
        InitialRequest.append("{" +
                "\"RequestDetails\": {" +
                "\"Id\":\"" + appletreeRequestVo.getRequestId() + "\"," +
                "\"ProcessingStage\":" + appletreeRequestVo.getProcessingStage() + "," +
                "\"ServiceCode\":\"" + appletreeRequestVo.getServiceCode() + "\"," +
                "\"CreationDateTime\":\"" + appletreeRequestVo.getCreationDateTime() + "\"," +
                "\"ExpiryDateTime\":\"" + appletreeRequestVo.getExpiryDateTime() + "\"" +
                "}," +
                "\"CustomerDetails\":{" +
                "\"FirstName\":\"" + appletreeRequestVo.getFirstName() + "\"," +
                "\"LastName\":\"" + appletreeRequestVo.getLastName() + "\"," +
                "\"StreetAddress\":\"" + appletreeRequestVo.getStreetAddress() + "\"," +
                "\"Locality\":\"" + appletreeRequestVo.getLocality() + "\"," +
                "\"AdministrativeArea\": \"" + appletreeRequestVo.getState() + "\"," +
                "\"PostalCode\": \""+ appletreeRequestVo.getPostalcode()+ "\"," +
                "\"Country\":\"" + appletreeRequestVo.getCountryCode() + "\"," +
                "\"MobileNumber\":\"" + appletreeRequestVo.getMobileNumber() + "\"," +
                "\"EmailAddress\":\"" + appletreeRequestVo.getEmailAddress() + "\"" +
                "}," +
                "\"OrderDetails\":{" +
                "\"Currency\":\"" + appletreeRequestVo.getCurrency() + "\"," +
                "\"Amount\":" + appletreeRequestVo.getAmount() + "," +
                "\"PaymentDetails\":\"" + appletreeRequestVo.getOrderid() + "\"" +
                "}," +
                "\"PaymentOptionDetails\":{" +
                "\"PaymentOptionCode\":\"" + appletreeRequestVo.getOptionalCode()+ "\"," +
                "\"AccountNumber\":\"" + appletreeRequestVo.getCardNo() + "\"," +
                "\"ExpirationMonth\":\"" + appletreeRequestVo.getExpMonth() + "\"," +
                "\"ExpirationYear\":\"" + appletreeRequestVo.getExpYear() + "\"," +
                "\"VerificationValue\":\"" + appletreeRequestVo.getCVV() + "\"," +
                "\"ReturnURL\":\"" + appletreeRequestVo.getReturnURL() + "\"," +
                "\"PaymentPanelBackgroundColor\":\"\"" +
                "}," +
                "\"PayerAuthenticationDetails\":{" +
                "\"AuthenticationId\":\"" + appletreeRequestVo.getAuthCode() + "\"," +
                "\"XID\":\"" + appletreeRequestVo.getXID() + "\"," +
                "\"ACSURL\":"+appletreeRequestVo.getACSURL()+"," +
                "\"PAReq\":\""+appletreeRequestVo.getPAReq()+"\"," +
                "\"PARes\":\""+appletreeRequestVo.getPARes()+"\"" +
                "}}");
        return InitialRequest;
    }

    public static StringBuffer InitialRequestLog(AppletreeRequestVo appletreeRequestVo){
        StringBuffer InitialRequest = new StringBuffer();
        Functions functions = new Functions();
        InitialRequest.append("{" +
                "\"RequestDetails\": {" +
                    "\"Id\":\"" + appletreeRequestVo.getRequestId() + "\"," +
                "\"ProcessingStage\":" + appletreeRequestVo.getProcessingStage() + "," +
                    "\"ServiceCode\":\"" + appletreeRequestVo.getServiceCode() + "\"," +
                   "\"CreationDateTime\":\"" + appletreeRequestVo.getCreationDateTime() + "\"," +
                    "\"ExpiryDateTime\":\"" + appletreeRequestVo.getExpiryDateTime() + "\"" +
                "}," +
                "\"CustomerDetails\":{" +
                    "\"FirstName\":\"" + functions.maskingFirstName(appletreeRequestVo.getFirstName()) + "\"," +
                    "\"LastName\":\"" + functions.maskingLastName(appletreeRequestVo.getLastName()) + "\"," +
                    "\"StreetAddress\":\"" + appletreeRequestVo.getStreetAddress() + "\"," +
                    "\"Locality\":\"" + appletreeRequestVo.getLocality() + "\"," +
                    "\"AdministrativeArea\": \"" + appletreeRequestVo.getState() + "\"," +
                    "\"PostalCode\": \""+ appletreeRequestVo.getPostalcode()+ "\"," +
                    "\"Country\":\"" + appletreeRequestVo.getCountryCode() + "\"," +
                    "\"MobileNumber\":\"" + appletreeRequestVo.getMobileNumber() + "\"," +
                    "\"EmailAddress\":\"" + appletreeRequestVo.getEmailAddress() + "\"" +
                "}," +
                "\"OrderDetails\":{" +
                    "\"Currency\":\"" + appletreeRequestVo.getCurrency() + "\"," +
                    "\"Amount\":" + appletreeRequestVo.getAmount() + "," +
                    "\"PaymentDetails\":\"" + appletreeRequestVo.getOrderid() + "\"" +
                "}," +
                "\"PaymentOptionDetails\":{" +
                    "\"PaymentOptionCode\":\"" + appletreeRequestVo.getOptionalCode()+ "\"," +
                    "\"AccountNumber\":\"" + functions.maskingPan(appletreeRequestVo.getCardNo()) + "\"," +
                    "\"ExpirationMonth\":\"" + functions.maskingNumber(appletreeRequestVo.getExpMonth()) + "\"," +
                    "\"ExpirationYear\":\"" + functions.maskingNumber(appletreeRequestVo.getExpYear()) + "\"," +
                    "\"VerificationValue\":\"" + functions.maskingNumber(appletreeRequestVo.getCVV()) + "\"," +
                    "\"ReturnURL\":\"" + appletreeRequestVo.getReturnURL() + "\"," +
                    "\"PaymentPanelBackgroundColor\":\"\"" +
                "}," +
                "\"PayerAuthenticationDetails\":{" +
                    "\"AuthenticationId\":\"" + appletreeRequestVo.getAuthCode() + "\"," +
                    "\"XID\":\"" + appletreeRequestVo.getXID() + "\"," +
                    "\"ACSURL\":\""+appletreeRequestVo.getACSURL()+"\"," +
                    "\"PAReq\":\""+appletreeRequestVo.getPAReq()+"\"," +
                    "\"PARes\":\""+appletreeRequestVo.getPARes()+"\"" +
                "}," +
                "\"MerchantDetails\":{" +
                    "\"MerchantId\":\"" + appletreeRequestVo.getMerchantId() + "\"," +
                    "\"StoreId\":\"" + appletreeRequestVo.getStoreId() + "\"," +
                    "\"TerminalId\":\"" + appletreeRequestVo.getTerminalId() + "\"" +
                "}" +
                "}");
        return InitialRequest;
    }

    public static StringBuffer Initial3dRequestLog(AppletreeRequestVo appletreeRequestVo){
        StringBuffer InitialRequest = new StringBuffer();
        Functions functions = new Functions();
        InitialRequest.append("{" +
                "\"RequestDetails\": {" +
                "\"Id\":\"" + appletreeRequestVo.getRequestId() + "\"," +
                "\"ProcessingStage\":" + appletreeRequestVo.getProcessingStage() + "," +
                "\"ServiceCode\":\"" + appletreeRequestVo.getServiceCode() + "\"," +
                "\"CreationDateTime\":\"" + appletreeRequestVo.getCreationDateTime() + "\"," +
                "\"ExpiryDateTime\":\"" + appletreeRequestVo.getExpiryDateTime() + "\"" +
                "}," +
                "\"CustomerDetails\":{" +
                "\"FirstName\":\"" + functions.maskingFirstName(appletreeRequestVo.getFirstName()) + "\"," +
                "\"LastName\":\"" + functions.maskingLastName(appletreeRequestVo.getLastName()) + "\"," +
                "\"StreetAddress\":\"" + appletreeRequestVo.getStreetAddress() + "\"," +
                "\"Locality\":\"" + appletreeRequestVo.getLocality() + "\"," +
                "\"Country\":\"" + appletreeRequestVo.getCountryCode() + "\"," +
                "\"AdministrativeArea\": \"" + appletreeRequestVo.getState() + "\"," +
                "\"PostalCode\": \""+ appletreeRequestVo.getPostalcode()+ "\"," +
                "\"MobileNumber\":\"" + appletreeRequestVo.getMobileNumber() + "\"," +
                "\"EmailAddress\":\"" + appletreeRequestVo.getEmailAddress() + "\"" +
                "}," +
                "\"OrderDetails\":{" +
                "\"Currency\":\"" + appletreeRequestVo.getCurrency() + "\"," +
                "\"Amount\":" + appletreeRequestVo.getAmount() + "," +
                "\"PaymentDetails\":\"" + appletreeRequestVo.getOrderid() + "\"" +
                "}," +
                "\"PaymentOptionDetails\":{" +
                "\"PaymentOptionCode\":\"" + appletreeRequestVo.getOptionalCode()+ "\"," +
                "\"AccountNumber\":\"" + functions.maskingPan(appletreeRequestVo.getCardNo()) + "\"," +
                "\"ExpirationMonth\":\"" + functions.maskingNumber(appletreeRequestVo.getExpMonth()) + "\"," +
                "\"ExpirationYear\":\"" + functions.maskingNumber(appletreeRequestVo.getExpYear()) + "\"," +
                "\"VerificationValue\":\"" + functions.maskingNumber(appletreeRequestVo.getCVV()) + "\"," +
                "\"ReturnURL\":\"" + appletreeRequestVo.getReturnURL() + "\"," +
                "\"PaymentPanelBackgroundColor\":\"\"" +
                "}," +
                "\"PayerAuthenticationDetails\":{" +
                "\"AuthenticationId\":\"" + appletreeRequestVo.getAuthCode() + "\"," +
                "\"XID\":\"" + appletreeRequestVo.getXID() + "\"," +
                "\"ACSURL\":\""+appletreeRequestVo.getACSURL()+"\"," +
                "\"PAReq\":\""+appletreeRequestVo.getPAReq()+"\"," +
                "\"PARes\":\""+appletreeRequestVo.getPARes()+"\"" +
                "}}");
        return InitialRequest;
    }

    public static CommRequestVO getCommRequestFromUtils(CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("Inside Appletree Transfer getCommRequestFromUtils");
        CommRequestVO commRequestVO             = new CommRequestVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO           = new CommMerchantVO();
        Functions functions                     = new Functions();
        ReserveField2VO reserveField2VO         = null;
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();

        Hashtable additioanlParams = new Hashtable();

        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
//        addressDetailsVO.setEmail(commonValidatorVO.getCustEmail());
        addressDetailsVO.setFirstname(genericAddressDetailsVO.getFirstname());
        addressDetailsVO.setLastname(genericAddressDetailsVO.getLastname());
        addressDetailsVO.setStreet(genericAddressDetailsVO.getStreet());
        addressDetailsVO.setCity(genericAddressDetailsVO.getCity());
        addressDetailsVO.setPhone(genericAddressDetailsVO.getPhone());
        addressDetailsVO.setIp(genericAddressDetailsVO.getIp());
        addressDetailsVO.setEmail(genericAddressDetailsVO.getEmail());
        addressDetailsVO.setZipCode(genericAddressDetailsVO.getZipCode());
        addressDetailsVO.setCountry(genericAddressDetailsVO.getCountry());
        if(functions.isValueNull(genericAddressDetailsVO.getCustomerid()))
            addressDetailsVO.setCustomerid(genericAddressDetailsVO.getCustomerid());
        else
            addressDetailsVO.setCustomerid(commonValidatorVO.getCustomerId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());

        transDetailsVO.setCardType(commonValidatorVO.getTransDetailsVO().getCardType());
        transDetailsVO.setPaymentType(commonValidatorVO.getTransDetailsVO().getPaymentType());

        if(commonValidatorVO.getReserveField2VO() != null)
        {
            reserveField2VO = new ReserveField2VO();
            reserveField2VO.setAccountType(commonValidatorVO.getReserveField2VO().getAccountType());
            reserveField2VO.setRoutingNumber(commonValidatorVO.getReserveField2VO().getRoutingNumber());
            reserveField2VO.setAccountNumber(commonValidatorVO.getReserveField2VO().getAccountNumber());
            reserveField2VO.setCheckNumber(commonValidatorVO.getReserveField2VO().getCheckNumber());
            reserveField2VO.setBankName(commonValidatorVO.getReserveField2VO().getBankName());
            reserveField2VO.setBankAddress(commonValidatorVO.getReserveField2VO().getBankAddress());
            reserveField2VO.setBankCity(commonValidatorVO.getReserveField2VO().getBankCity());
            reserveField2VO.setBankState(commonValidatorVO.getReserveField2VO().getBankState());
            reserveField2VO.setBankZipcode(commonValidatorVO.getReserveField2VO().getBankZipcode());
        }
        if(commonValidatorVO.getProcessorName() != null){
            additioanlParams.put("paymentOtionCode",commonValidatorVO.getProcessorName());
        }
        commRequestVO.setAdditioanlParams(additioanlParams);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        commRequestVO.setReserveField2VO(reserveField2VO);

        return commRequestVO;
    }

    /*public static void main(String[] args)
    {
        String hash=getSignature("23de4621-ea24-433f-9b45-dc1e383d8c2c4564cdfd-000d-4cf4-a50d-c4988ee5a756USD10.00","D:/AppleTree/live_cybersource2_private_key.pem");
        System.out.println("hash-->"+hash);
        String hash2=getSignaturePublic("qTr7d1jBw1DAubgIcDu6WNmQ01KK7+WEXX9BvTSDnggV44J4tFLVo5/y2g6Hals6wIsz18QIDWXG7UWv4uCDWNLvmdourv5J3i9+IaUxs5hJxJxnyZQ4YSC4qGnnPd7cBlb8qv3cu7wtmZkw9Ca7j6GANGbyGRMMjKJvsw/315Fn/+EpsmO5dsIYeeA0H3a6HWPginksv+VrUr2VoJ/c1H+6ggMvQgOIf4GAIqbmFpMFm1WpTbC/QM02QWWQP9hHafmDnmNvdDez5ndtRk+jt+JJ62B0OAZV8swKsiKGg1agdMm2ihvcCgkqWbZaxK/0jYjfvjC42ToIwvSZsFmF1w==","D:/AppleTree/publickey.pem");
        System.out.println("hash2--->"+hash2);
    }*/

    public static String getPaymentMode(String paymentMode){
    HashMap<String,String> hashMap  = new HashMap<>();
    String     paymentType          = "";
    hashMap.put("1","CreditCards");
    hashMap.put("2","MobileMoneyAfrica");
    hashMap.put("3","WalletAfrica");
    hashMap.put("4","BankTransferAfrica");
    hashMap.put("5","BankTransferAfrica");//Delayed Bank Transfer
    hashMap.put("6","GiftCardAfrica");

    if(hashMap.containsKey(paymentMode)){
        paymentType =  hashMap.get(paymentMode);
    }
        return  paymentType;
    }

    public static String getOptionalCode(String paymentMode,String financialServiceName){
        HashMap<String,String> hashMap  = new HashMap<>();
        String[] OptionalCode           = paymentMode.split(",");
        transactionLogger.error("Inside getOptionalCode ");
        for(String splitStr : OptionalCode ){
           String[] finalSplit =  splitStr.split("=");
           hashMap.put(finalSplit[0],finalSplit[1]);
        }
        transactionLogger.error("Inside getOptionalCode -------> "+hashMap);
        String paymentType= "";
        if(hashMap.containsKey(financialServiceName)){
            paymentType = hashMap.get(financialServiceName);
        }

        return  paymentType;
    }
    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }
}
