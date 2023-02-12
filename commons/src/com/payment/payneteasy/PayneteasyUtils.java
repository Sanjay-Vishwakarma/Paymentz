package com.payment.payneteasy;

import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.nestpay.NestPayPaymentGateway;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Krishna on 28-Apr-21.
 */
public class PayneteasyUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(PayneteasyUtils.class.getName());
    private static HashMap<String, String> countryCodeHash = new HashMap<>();
    private static HashMap<String, String> stateCodeHash = new HashMap<>();
    private static HashMap<String, String> errorListHash = new HashMap<>();

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

        stateCodeHash.put("Australian Capital Territory".toUpperCase(), "ACT");
        stateCodeHash.put("New South Wales".toUpperCase(), "NSW");
        stateCodeHash.put("Northern Territory".toUpperCase(), "NT");
        stateCodeHash.put("Queensland".toUpperCase(), "QLD");
        stateCodeHash.put("South Australia".toUpperCase(), "SA");
        stateCodeHash.put("Tasmania".toUpperCase(), "TAS");
        stateCodeHash.put("Victoria".toUpperCase(), "VIC");
        stateCodeHash.put("Western Australia".toUpperCase(), "WA");
        stateCodeHash.put("Alberta".toUpperCase(), "AB");
        stateCodeHash.put("British Columbia".toUpperCase(), "BC");
        stateCodeHash.put("Manitoba".toUpperCase(), "MB");
        stateCodeHash.put("New Brunswick".toUpperCase(), "NB");
        stateCodeHash.put("Newfoundland and Labrador".toUpperCase(), "NL");
        stateCodeHash.put("Northwest Territories".toUpperCase(), "NT");
        stateCodeHash.put("Nova Scotia".toUpperCase(), "NS");
        stateCodeHash.put("Nunavut".toUpperCase(), "NU");
        stateCodeHash.put("Ontario".toUpperCase(), "ON");
        stateCodeHash.put("Prince Edward Island".toUpperCase(), "PE");
        stateCodeHash.put("Quebec".toUpperCase(), "QC");
        stateCodeHash.put("Saskatchewan".toUpperCase(), "SK");
        stateCodeHash.put("Yukon".toUpperCase(), "YT");
        stateCodeHash.put("Alabama".toUpperCase(), "AL");
        stateCodeHash.put("Alaska".toUpperCase(), "AK");
        stateCodeHash.put("American Samoa".toUpperCase(), "AS");
        stateCodeHash.put("Arizona".toUpperCase(), "AZ");
        stateCodeHash.put("Arkansas".toUpperCase(), "AR");
        stateCodeHash.put("California".toUpperCase(), "CA");
        stateCodeHash.put("Colorado".toUpperCase(), "CO");
        stateCodeHash.put("Connecticut".toUpperCase(), "CT");
        stateCodeHash.put("Delaware".toUpperCase(), "DE");
        stateCodeHash.put("District of Columbia".toUpperCase(), "DC");
        stateCodeHash.put("Florida".toUpperCase(), "FL");
        stateCodeHash.put("Georgia".toUpperCase(), "GA");
        stateCodeHash.put("Guam".toUpperCase(), "GU");
        stateCodeHash.put("Hawaii".toUpperCase(), "HI");
        stateCodeHash.put("Idaho".toUpperCase(), "ID");
        stateCodeHash.put("Illinois".toUpperCase(), "IL");
        stateCodeHash.put("Indiana".toUpperCase(), "IN");
        stateCodeHash.put("Iowa".toUpperCase(), "IA");
        stateCodeHash.put("Kansas".toUpperCase(), "KS");
        stateCodeHash.put("Kentucky".toUpperCase(), "KY");
        stateCodeHash.put("Louisiana".toUpperCase(), "LA");
        stateCodeHash.put("Maine".toUpperCase(), "ME");
        stateCodeHash.put("Maryland".toUpperCase(), "MD");
        stateCodeHash.put("Massachusetts".toUpperCase(), "MA");
        stateCodeHash.put("Michigan".toUpperCase(), "MI");
        stateCodeHash.put("Minnesota".toUpperCase(), "MN");
        stateCodeHash.put("Mississippi".toUpperCase(), "MS");
        stateCodeHash.put("Missouri".toUpperCase(), "MO");
        stateCodeHash.put("Montana".toUpperCase(), "MT");
        stateCodeHash.put("Nebraska".toUpperCase(), "NE");
        stateCodeHash.put("Nevada".toUpperCase(), "NV");
        stateCodeHash.put("New Hampshire".toUpperCase(), "NH");
        stateCodeHash.put("New Jersey".toUpperCase(), "NJ");
        stateCodeHash.put("New Mexico".toUpperCase(), "NM");
        stateCodeHash.put("New York".toUpperCase(), "NY");
        stateCodeHash.put("North Carolina".toUpperCase(), "NC");
        stateCodeHash.put("North Dakota".toUpperCase(), "ND");
        stateCodeHash.put("Ohio".toUpperCase(), "OH");
        stateCodeHash.put("Oklahoma".toUpperCase(), "OK");
        stateCodeHash.put("Oregon".toUpperCase(), "OR");
        stateCodeHash.put("Pennsylvania".toUpperCase(), "PA");
        stateCodeHash.put("Puerto Rico".toUpperCase(), "PR");
        stateCodeHash.put("Rhode Island".toUpperCase(), "RI");
        stateCodeHash.put("South Carolina".toUpperCase(), "SC");
        stateCodeHash.put("South Dakota".toUpperCase(), "SD");
        stateCodeHash.put("Tennessee".toUpperCase(), "TN");
        stateCodeHash.put("Texas".toUpperCase(), "TX");
        stateCodeHash.put("Utah".toUpperCase(), "UT");
        stateCodeHash.put("Vermont".toUpperCase(), "VT");
        stateCodeHash.put("Virgin Islands".toUpperCase(), "VI");
        stateCodeHash.put("Virginia".toUpperCase(), "VA");
        stateCodeHash.put("Washington".toUpperCase(), "WA");
        stateCodeHash.put("West Virginia".toUpperCase(), "WV");
        stateCodeHash.put("Wisconsin".toUpperCase(), "WI");
        stateCodeHash.put("Wyoming".toUpperCase(), "WY");

        errorListHash.put("00", "Transaction was successful");
        errorListHash.put("01", "Refer to issuer");
        errorListHash.put("03", "Invalid merchant");
        errorListHash.put("04", "Pickup card");
        errorListHash.put("05", "Transaction was rejected. Do not honor");
        errorListHash.put("06", "Error");
        errorListHash.put("07", "Pickup card - fake card");
        errorListHash.put("09", "Request in progress");
        errorListHash.put("12", "Invalid transaction");
        errorListHash.put("13", "Invalid amount");
        errorListHash.put("14", "Invalid card number");
        errorListHash.put("15", "Invalid issuer");
        errorListHash.put("20", "Invalid response");
        errorListHash.put("30", "Format error");
        errorListHash.put("31", "Unsupported bank");
        errorListHash.put("33", "Expired card");
        errorListHash.put("34", "Suspended card for fraud");
        errorListHash.put("36", "Restricted card");
        errorListHash.put("40", "Function unsupported");
        errorListHash.put("41", "Lost card");
        errorListHash.put("42", "No account");
        errorListHash.put("43", "Pickup. Card reported as stolen");
        errorListHash.put("44", "Insufficient funds");
        errorListHash.put("54", "Expired date error");
        errorListHash.put("55", "Incorrect PIN");
        errorListHash.put("56", "No card record");
        errorListHash.put("57", "Transaction denied to cardholder");
        errorListHash.put("58", "Transaction denied to terminal");
        errorListHash.put("59", "Suspected fraudulent transaction");
        errorListHash.put("61", "Exceeded withdrawal limits");
        errorListHash.put("62", "Restricted card");
        errorListHash.put("63", "Security Violation");
        errorListHash.put("65", "Exceed withdrawal frequency limits");
        errorListHash.put("75", "PIN tries exceeded");
        errorListHash.put("76", "Incorrect reversal");
        errorListHash.put("77", "Lost or stolen card, for pickup");
        errorListHash.put("78", "Shop/Merchant is in blacklist");
        errorListHash.put("79", "Account status is false");
        errorListHash.put("87", "Incorrect passport");
        errorListHash.put("88", "Incorrect Date of birth");
        errorListHash.put("89", "Not approved");
        errorListHash.put("90", "Cutoff in progress");
        errorListHash.put("91", "Issuer or JCB switch is down");
        errorListHash.put("92", "Institution unavailable");
        errorListHash.put("94", "Duplicate transaction");
        errorListHash.put("96", "System Error");
    }


    public static String doPostHTTPSURLConnectionClient(String strURL, String request) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside doPostHTTPSURLConnectionClient of PayneteasyUtils ===== " + strURL);

        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException httpException)
        {
            transactionLogger.error("PayneteasyUtils:: HttpException----- " + httpException);

        }
        catch (IOException ioException)
        {
            transactionLogger.error("PayneteasyUtils:: IOException----- " + ioException);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String hashMac(String sha) throws Exception
    {
        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest messageDigest=MessageDigest.getInstance("SHA-1");
            byte[] hash=messageDigest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NestPayPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NestPayPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return hexString.toString();
    }

    //For USD
    public static String getCentAmount(String amount)
    {
        BigDecimal bigDecimal = new BigDecimal(amount);
        bigDecimal = bigDecimal.multiply(new BigDecimal(100));

        Long newAmount = bigDecimal.longValue();
        return newAmount.toString();
    }

    //for JPY
    public static String getJPYAmount(String amount)
    {
        double amt = Double.parseDouble(amount);
        double roundOff = Math.round(amt);
        int value = (int) roundOff;
        amount = String.valueOf(value);
        return amount.toString();
    }

    public static String getKWDSupportedAmount(String amount)
    {
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2 = Double.valueOf(amount);
        dObj2 = dObj2 * 1000;
        String amt = d.format(dObj2);
        return amt;
    }

    public static Map<String, String> getResponseMap(String request)
    {
        Map<String, String> resultMap = new LinkedHashMap<>();

        String[] arrResult = request.split("&");

        for(int i = 0; i < arrResult.length; i++)
        {
            String[] value = arrResult[i].split("=");
            resultMap.put(value[0].trim(), value[1].trim());
        }

        return resultMap;
    }

    public static String getCountryCodeHash(String country)
    {
        return countryCodeHash.get(country);
    }

    public static String getStateCodeHash(String state)
    {
        return stateCodeHash.get(state);
    }
    public static String getErrorCodeHash(String code)
    {
        return errorListHash.get(code);
    }
}
