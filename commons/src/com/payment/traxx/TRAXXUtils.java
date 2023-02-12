package com.payment.traxx;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.net.ssl.SSLHandshakeException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Created by admin on 9/13/2016.
 */
public class TRAXXUtils
{
    public final static String charset = "UTF-8";
    private static Logger log                           = new Logger(TRAXXUtils.class.getName());
    private static TransactionLogger transactionLogger  = new TransactionLogger(TRAXXUtils.class.getName());
    static Functions functions                          = new Functions();
    public static HashMap<String, String> stateISOHM    = new HashMap<>();
    public static HashMap<String, String> countryISOHM  = new HashMap<>();
    private static final ResourceBundle RBTemplate          = LoadProperties.getProperty("com.directi.pg.2CharCountryList");

    static {
        Set<String> keySets =  RBTemplate.keySet();

        for(String key:keySets){
            countryISOHM.put(key,RBTemplate.getString(key));
        }

        stateISOHM.put("Alberta","AB");
        stateISOHM.put("British Columbia","BC");
        stateISOHM.put("Manitoba","MB");
        stateISOHM.put("New Brunswick","NB");
        stateISOHM.put("Newfoundland","NF");
        stateISOHM.put("Northwest Territories","NT");
        stateISOHM.put("Nova Scotia","NS");
        stateISOHM.put("Nunavut","NU");
        stateISOHM.put("Ontario","ON");
        stateISOHM.put("Prince Edward Island","PE");
        stateISOHM.put("Quebec","QC");
        stateISOHM.put("Saskatchewan","SK");
        stateISOHM.put("Yukon and Northern Territories","YT");
        stateISOHM.put("Alabama","AL");
        stateISOHM.put("Alaska","AK");
        stateISOHM.put("Arizona","AZ");
        stateISOHM.put("Arkansas","AR");
        stateISOHM.put("California","CA");
        stateISOHM.put("Colorado","CO");
        stateISOHM.put("Connecticut","CT");
        stateISOHM.put("Delaware","DE");
        stateISOHM.put("District of Columbia","DC");
        stateISOHM.put("Florida","FL");
        stateISOHM.put("Georgia","GA");
        stateISOHM.put("Hawaii","HI");
        stateISOHM.put("Idaho","ID");
        stateISOHM.put("Illinois","IL");
        stateISOHM.put("Indiana","IN");
        stateISOHM.put("Iowa","IA");
        stateISOHM.put("Kansas","KS");
        stateISOHM.put("Kentucky","KY");
        stateISOHM.put("Louisiana","LA");
        stateISOHM.put("Maine","ME");
        stateISOHM.put("Marshall Islands","MH");
        stateISOHM.put("Maryland","MD");
        stateISOHM.put("Massachusetts","MA");
        stateISOHM.put("Michigan","MI");
        stateISOHM.put("Minnesota","MN");
        stateISOHM.put("Mississippi","MS");
        stateISOHM.put("Missouri","MO");
        stateISOHM.put("Montana","MT");
        stateISOHM.put("Nebraska","NE");
        stateISOHM.put("Nevada","NV");
        stateISOHM.put("New Hampshire","NH");
        stateISOHM.put("New Jersey","NJ");
        stateISOHM.put("New Mexico","NM");
        stateISOHM.put("New York","NY");
        stateISOHM.put("North Carolina","NC");
        stateISOHM.put("North Dakota","ND");
        stateISOHM.put("Ohio","OH");
        stateISOHM.put("Oklahoma","OK");
        stateISOHM.put("Oregon","OR");
        stateISOHM.put("Pennsylvania","PA");
        stateISOHM.put("Rhode Island","RI");
        stateISOHM.put("South Carolina","SC");
        stateISOHM.put("South Dakota","SD");
        stateISOHM.put("Tennessee","TN");
        stateISOHM.put("Texas","TX");
        stateISOHM.put("US Virgin Islands","VI");
        stateISOHM.put("Utah","UT");
        stateISOHM.put("Vermont","VT");
        stateISOHM.put("Virginia","VA");
        stateISOHM.put("Washington","WA");
        stateISOHM.put("West Virginia","WV");
        stateISOHM.put("Wisconsin","WI");
        stateISOHM.put("Wyoming","WY");


        countryISOHM.put("Afghanistan","AF");
        countryISOHM.put("Aland Islands","AX");
        countryISOHM.put("Albania","AL");
        countryISOHM.put("Algeria","DZ");
        countryISOHM.put("American Samoa","AS");
        countryISOHM.put("Andorra","AD");
        countryISOHM.put("Angola","AO");
        countryISOHM.put("Anguilla","AI");
        countryISOHM.put("Antarctica","AQ");
        countryISOHM.put("Antigua and Barbuda","AG");
        countryISOHM.put("Argentina","AR");
        countryISOHM.put("Armenia","AM");
        countryISOHM.put("Aruba","AW");
        countryISOHM.put("Australia","AU");
        countryISOHM.put("Austria","AT");
        countryISOHM.put("Azerbaijan","AZ");
        countryISOHM.put("Bahamas","BS");
        countryISOHM.put("Bahrain","BH");
        countryISOHM.put("Bangladesh","BD");
        countryISOHM.put("Barbados","BB");
        countryISOHM.put("Belarus","BY");
        countryISOHM.put("Belgium","BE");
        countryISOHM.put("Belize","BZ");
        countryISOHM.put("Benin","BJ");
        countryISOHM.put("Bermuda","BM");
        countryISOHM.put("Bhutan","BT");
        countryISOHM.put("Bolivia (Plurinational State of)","BO");
        countryISOHM.put("Bonaire, Sint Eustatius and Saba","BQ");
        countryISOHM.put("Bosnia and Herzegovina","BA");
        countryISOHM.put("Botswana","BW");
        countryISOHM.put("Bouvet Island","BV");
        countryISOHM.put("Brazil","BR");
        countryISOHM.put("British Indian Ocean Territory","IO");
        countryISOHM.put("Brunei Darussalam","BN");
        countryISOHM.put("Bulgaria","BG");
        countryISOHM.put("Burkina Faso","BF");
        countryISOHM.put("Lesotho","LS");
        countryISOHM.put("Liberia","LR");
        countryISOHM.put("Libya","LY");
        countryISOHM.put("Liechtenstein","LI");
        countryISOHM.put("Lithuania","LT");
        countryISOHM.put("Luxembourg","LU");
        countryISOHM.put("Macao","MO");
        countryISOHM.put("Macedonia, the former Yugoslav Republic of","MK");
        countryISOHM.put("Madagascar","MG");
        countryISOHM.put("Malawi","MW");
        countryISOHM.put("Malaysia","MY");
        countryISOHM.put("Maldives","MV");
        countryISOHM.put("Mali","ML");
        countryISOHM.put("Malta","MT");
        countryISOHM.put("Marshall Islands","MH");
        countryISOHM.put("Martinique","MQ");
        countryISOHM.put("Mauritania","MR");
        countryISOHM.put("Mauritius","MU");
        countryISOHM.put("Mayotte","YT");
        countryISOHM.put("Mexico","MX");
        countryISOHM.put("Micronesia (Federated States of)","FM");
        countryISOHM.put("Moldova, Republic of","MD");
        countryISOHM.put("Monaco","MC");
        countryISOHM.put("Mongolia","MN");
        countryISOHM.put("Montenegro","ME");
        countryISOHM.put("Montserrat","MS");
        countryISOHM.put("Morocco","MA");
        countryISOHM.put("Mozambique","MZ");
        countryISOHM.put("Myanmar","MM");
        countryISOHM.put("Namibia","NA");
        countryISOHM.put("Nauru","NR");
        countryISOHM.put("Nepal","NP");
        countryISOHM.put("Netherlands","NL");
        countryISOHM.put("New Caledonia","NC");
        countryISOHM.put("New Zealand","NZ");
        countryISOHM.put("Nicaragua","NI");
        countryISOHM.put("Cabo Verde","CV");
        countryISOHM.put("Cambodia","KH");
        countryISOHM.put("Cameroon","CM");
        countryISOHM.put("Canada","CA");
        countryISOHM.put("Cayman Islands","KY");
        countryISOHM.put("Central African Republic","CF");
        countryISOHM.put("Chad","TD");
        countryISOHM.put("Chile","CL");
        countryISOHM.put("China","CN");
        countryISOHM.put("Christmas Island","CX");
        countryISOHM.put("Cocos (Keeling) Islands","CC");
        countryISOHM.put("Colombia","CO");
        countryISOHM.put("Comoros","KM");
        countryISOHM.put("Congo","CG");
        countryISOHM.put("Congo, Democratic Republic of the","CD");
        countryISOHM.put("Cook Islands","CK");
        countryISOHM.put("Costa Rica","CR");
        countryISOHM.put("Croatia","HR");
        countryISOHM.put("Cuba","CU");
        countryISOHM.put("Curaçao","CW");
        countryISOHM.put("Cyprus","CY");
        countryISOHM.put("Czechia","CZ");
        countryISOHM.put("Denmark","DK");
        countryISOHM.put("Djibouti","DJ");
        countryISOHM.put("Dominica","DM");
        countryISOHM.put("Dominican Republic","DO");
        countryISOHM.put("Ecuador","EC");
        countryISOHM.put("Egypt","EG");
        countryISOHM.put("El Salvador","SV");
        countryISOHM.put("Equatorial Guinea","GQ");
        countryISOHM.put("Eritrea","ER");
        countryISOHM.put("Estonia","EE");
        countryISOHM.put("Eswatini","SZ");
        countryISOHM.put("Ethiopia","ET");
        countryISOHM.put("Falkland Islands (Malvinas)","FK");
        countryISOHM.put("Faroe Islands","FO");
        countryISOHM.put("Fiji","FJ");
        countryISOHM.put("Finland","FI");
        countryISOHM.put("Nigeria","NG");
        countryISOHM.put("Niue","NU");
        countryISOHM.put("Norfolk Island","NF");
        countryISOHM.put("Northern Mariana Islands","MP");
        countryISOHM.put("Norway","NO");
        countryISOHM.put("Oman","OM");
        countryISOHM.put("Pakistan","PK");
        countryISOHM.put("Palau","PW");
        countryISOHM.put("Palestine, State of","PS");
        countryISOHM.put("Panama","PA");
        countryISOHM.put("Papua New Guinea","PG");
        countryISOHM.put("Paraguay","PY");
        countryISOHM.put("Peru","PE");
        countryISOHM.put("Philippines","PH");
        countryISOHM.put("Pitcairn","PN");
        countryISOHM.put("Poland","PL");
        countryISOHM.put("Portugal","PT");
        countryISOHM.put("Puerto Rico","PR");
        countryISOHM.put("Qatar","QA");
        countryISOHM.put("Réunion","RE");
        countryISOHM.put("Romania","RO");
        countryISOHM.put("Russian Federation","RU");
        countryISOHM.put("Rwanda","RW");
        countryISOHM.put("Saint Barthélemy","BL");
        countryISOHM.put("Saint Helena, Ascension and Tristanda Cunha","SH");
        countryISOHM.put("Saint Kitts and Nevis","KN");
        countryISOHM.put("Saint Lucia","LC");
        countryISOHM.put("Saint Martin (French part)","MF");
        countryISOHM.put("Saint Pierre and Miquelon","PM");
        countryISOHM.put("Saint Vincent and the Grenadines","VC");
        countryISOHM.put("Samoa","WS");
        countryISOHM.put("San Marino","SM");
        countryISOHM.put("Sao Tome and Principe","ST");
        countryISOHM.put("Saudi Arabia","SA");
        countryISOHM.put("Senegal","SN");
        countryISOHM.put("Serbia","RS");
        countryISOHM.put("Seychelles","SC");
        countryISOHM.put("Sierra Leone","SL");
        countryISOHM.put("Singapore","SG");
        countryISOHM.put("France","FR");
        countryISOHM.put("French_Guiana","GF");
        countryISOHM.put("French_Polynesia","PF");
        countryISOHM.put("French_Southern_Territories","TF");
        countryISOHM.put("Gabon","GA");
        countryISOHM.put("Gambia","GM");
        countryISOHM.put("Georgia","GE");
        countryISOHM.put("Germany","DE");
        countryISOHM.put("Ghana","GH");
        countryISOHM.put("Gibraltar","GI");
        countryISOHM.put("Greece","GR");
        countryISOHM.put("Greenland","GL");
        countryISOHM.put("Grenada","GD");
        countryISOHM.put("Guadeloupe","GP");
        countryISOHM.put("Guam","GU");
        countryISOHM.put("Guatemala","GT");
        countryISOHM.put("Guernsey","GG");
        countryISOHM.put("Guinea","GN");
        countryISOHM.put("Guinea-Bissau","GW");
        countryISOHM.put("Guyana","GY");
        countryISOHM.put("Haiti","HT");
        countryISOHM.put("Heard_Island_and_McDonald_Islands","HM");
        countryISOHM.put("Holy_See","VA");
        countryISOHM.put("Honduras","HN");
        countryISOHM.put("Hong_Kong_SAR","HK");
        countryISOHM.put("Hungary","HU");
        countryISOHM.put("Iceland","IS");
        countryISOHM.put("India","IN");
        countryISOHM.put("Indonesia","ID");
        countryISOHM.put("Iran_(Islamic_Republic_of)","IR");
        countryISOHM.put("Iraq","IQ");
        countryISOHM.put("Ireland","IE");
        countryISOHM.put("Isle_of_Man","IM");
        countryISOHM.put("Israel","IL");
        countryISOHM.put("Italy","IT");
        countryISOHM.put("Jamaica","JM");
        countryISOHM.put("Japan","JP");
        countryISOHM.put("Jersey","JE");
        countryISOHM.put("Sint_Maarten (Dutch part)","SX");
        countryISOHM.put("Slovakia","SK");
        countryISOHM.put("Slovenia","SI");
        countryISOHM.put("Solomon Islands","SB");
        countryISOHM.put("Somalia","SO");
        countryISOHM.put("South Africa","ZA");
        countryISOHM.put("South Georgia and the South Sandwich Islands","GS");
        countryISOHM.put("South Sudan","SS");
        countryISOHM.put("Spain","ES");
        countryISOHM.put("Sri Lanka","LK");
        countryISOHM.put("Sudan","SD");
        countryISOHM.put("Suriname","SR");
        countryISOHM.put("Svalbard and Jan Mayen","SJ");
        countryISOHM.put("Sweden","SE");
        countryISOHM.put("Switzerland","CH");
        countryISOHM.put("Syrian Arab Republic","SY");
        countryISOHM.put("Taiwan, Province of China","TW");
        countryISOHM.put("Tajikistan","TJ");
        countryISOHM.put("Tanzania, United Republic of","TZ");
        countryISOHM.put("Thailand","TH");
        countryISOHM.put("Timor-Leste","TL");
        countryISOHM.put("Togo","TG");
        countryISOHM.put("Tokelau","TK");
        countryISOHM.put("Tonga","TO");
        countryISOHM.put("Trinidad and Tobago","TT");
        countryISOHM.put("Tunisia","TN");
        countryISOHM.put("Turkey","TR");
        countryISOHM.put("Turkmenistan","TM");
        countryISOHM.put("Turks and Caicos Islands","TC");
        countryISOHM.put("Tuvalu","TV");
        countryISOHM.put("Uganda","UG");
        countryISOHM.put("Ukraine","UA");
        countryISOHM.put("United Arab Emirates","AE");
        countryISOHM.put("United Kingdom of Great Britain and Northern Ireland","GB");
        countryISOHM.put("United States of America","US");
        countryISOHM.put("United States Minor Outlying Islands","UM");
        countryISOHM.put("Uruguay","UY");
        countryISOHM.put("Uzbekistan","UZ");
        countryISOHM.put("Jordan","JO");
        countryISOHM.put("Kazakhstan","KZ");
        countryISOHM.put("Kenya","KE");
        countryISOHM.put("Kiribati","KI");
        countryISOHM.put("Korea_(Democratic_People's_Republic_of)","KP");
        countryISOHM.put("Korea,_Republic_of","KR");
        countryISOHM.put("Kuwait","KW");
        countryISOHM.put("Kyrgyzstan","KG");
        countryISOHM.put("Lao_People's_Democratic_Republic","LA");
        countryISOHM.put("Latvia","LV");
        countryISOHM.put("Lebanon","LB");
        countryISOHM.put("Vanuatu","VU");
        countryISOHM.put("Venezuela_(Bolivarian_Republic_of)","VE");
        countryISOHM.put("Vietnam","VN");
        countryISOHM.put("Virgin_Islands_(British)","VG");
        countryISOHM.put("Virgin_Islands_(U.S.)","VI");
        countryISOHM.put("Wallis_and_Futuna","WF");
        countryISOHM.put("Western_Sahara","EH");
        countryISOHM.put("Yemen","YE");
        countryISOHM.put("Zambia","ZM");
        countryISOHM.put("Zimbabwe","ZW");

    }

    public static String getStateCode(String state)
    {
        String stateCode= "";
        if(stateISOHM.containsKey(state)){
            stateCode =  stateISOHM.get(state);
        }
        return stateCode;
    }
    public static String getCountryCode(String country)
    {
        String stateCode= "";
        if(countryISOHM.containsKey(country)){
            stateCode =  countryISOHM.get(country);
        }
        return stateCode;
    }

    public static String getPaymentBrand(String paymentMode)
    {
        String payBrand = "";
        if("1".equalsIgnoreCase(paymentMode))
            payBrand = "1";
        if("2".equalsIgnoreCase(paymentMode))
            payBrand = "2";
        if("4".equalsIgnoreCase(paymentMode))
            payBrand = "4";
        if("16".equalsIgnoreCase(paymentMode))
            payBrand = "3";
        return payBrand;
    }

    public  static String getLast2DigitOfExpiryYear(String ExpiryYear)
    {
        String expiryYearLast2Digit = "";
        if (functions.isValueNull(ExpiryYear)){
            expiryYearLast2Digit = ExpiryYear.substring(ExpiryYear.length()-2,ExpiryYear.length());
        }
        return expiryYearLast2Digit;
    }

    public static String doPostHTTPSURLConnection(String testURL, String request) throws PZTechnicalViolationException
    {
        String result = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        URLConnection con = null;

        try
        {
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            URL url = new URL(testURL);
            try
            {
                con = url.openConnection();
                con.setConnectTimeout(120000);
                con.setReadTimeout(120000);
            }
            catch (SSLHandshakeException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("TRAXXUtils.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, io.getMessage(), io.getCause());
            }
            if (con instanceof HttpURLConnection)
            {
                ((HttpURLConnection) con).setRequestMethod("POST");
            }
            con.setDoInput(true);
            con.setDoOutput(true);

            out = new BufferedOutputStream(con.getOutputStream());
            byte outBuf[] = request.getBytes(charset);
            out.write(outBuf);

            out.close();

            in = new BufferedInputStream(con.getInputStream());
            result = ReadByteStream(in);
        }
        catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("TRAXXUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON, null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("TRAXXUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION, null, pe.getMessage(), pe.getCause());
        }
        catch (UnsupportedEncodingException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("TRAXXUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, pe.getMessage(), pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("TRAXXUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, ex.getMessage(), ex.getCause());
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("TRAXXUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("TRAXXUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }

    static class traxx
    {
        public byte buf[];
        public int size;

        public traxx(byte b[], int s)
        {
            buf = b;
            size = s;
        }
    }

    private static String ReadByteStream(BufferedInputStream in) throws PZTechnicalViolationException
    {
        LinkedList<traxx> bufList = new LinkedList<traxx>();
        int size = 0;
        String buffer = null;
        byte buf[];
        try
        {
            do
            {
                buf = new byte[128];
                int num = in.read(buf);
                if (num == -1)
                    break;
                size += num;
                bufList.add(new traxx(buf, num));
            }
            while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<traxx> p = bufList.listIterator(); p.hasNext(); )
            {
                traxx b = p.next();
                for (int i = 0; i < b.size; )
                {
                    buf[pos] = b.buf[i];
                    i++;
                    pos++;
                }

            }
            buffer = new String(buf, charset);
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("TRAXXUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }

        return buffer;
    }

    public static String doPostFormHTTPSURLConnectionClient(String url,String request,String request_type,String username,String password,String merchant_id) throws PZTechnicalViolationException
    {
        String result   = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.addRequestHeader("request_type",request_type);
            post.addRequestHeader("username",username);
            post.addRequestHeader("password",password);
            post.addRequestHeader("merchant_id",merchant_id);

            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("TRAXXUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("TRAXXUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {
            post.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }

    public static Map<String, String> readSaleXMLReponse(String response)     {
        Map<String, String> responseMap = new HashMap<String, String>();

        Document doc    = null;
        try
        {
            doc = createDocumentFromString(response.trim());

        NodeList nList  = doc.getElementsByTagName("PPG_RESPONSE");

        System.out.println("resp_code "+doc.getElementById("resp_code"));
        System.out.println("resp_code "+doc.getElementsByTagName("PPG_RESPONSE").item(0).getChildNodes().item(0));


        responseMap.put("resp_code", getTagValue("resp_code", ((Element) nList.item(0))));
        responseMap.put("resp_desc", getTagValue("resp_desc", ((Element) nList.item(0))));
        responseMap.put("resp_time", getTagValue("resp_time", ((Element) nList.item(0))));
        responseMap.put("request_type", getTagValue("request_type", ((Element) nList.item(0))));
        responseMap.put("trxn_type", getTagValue("trxn_type", ((Element) nList.item(0))));
        responseMap.put("merchant_id", getTagValue("merchant_id", ((Element) nList.item(0))));
        responseMap.put("track_id", getTagValue("track_id", ((Element) nList.item(0))));
        responseMap.put("rrn", getTagValue("rrn", ((Element) nList.item(0))));
        responseMap.put("payment_method", getTagValue("payment_method", ((Element) nList.item(0))));
        responseMap.put("currency_code", getTagValue("currency_code", ((Element) nList.item(0))));
        responseMap.put("auth_code", getTagValue("auth_code", ((Element) nList.item(0))));
        responseMap.put("auth_msg", getTagValue("auth_msg", ((Element) nList.item(0))));
        responseMap.put("descriptor", getTagValue("descriptor", ((Element) nList.item(0))));
        responseMap.put("trxn_amount", getTagValue("trxn_amount", ((Element) nList.item(0))));
        responseMap.put("trxn_amount", getTagValue("trxn_amount", ((Element) nList.item(0))));

        }
        catch (PZTechnicalViolationException e)
        {
            return responseMap;
        }

        return responseMap;
    }

    public static Document createDocumentFromString(String xmlString) throws PZTechnicalViolationException
    {
        Document document = null;
        try
        {
            DocumentBuilderFactory docFactory   = DocumentBuilderFactory.newInstance();
            docFactory.setCoalescing(true);
            DocumentBuilder docBuilder          = docFactory.newDocumentBuilder();
            document                            = docBuilder.parse(new InputSource(new StringReader(xmlString)));

        }
        catch (ParserConfigurationException pce){
            transactionLogger.error("Exception in createDocumentFromString of TRAXXUtils", pce);
            PZExceptionHandler.raiseTechnicalViolationException("TRAXXUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e){
            transactionLogger.error("Exception in createDocumentFromString TRAXXUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("TRAXXUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.SAXEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e){
            transactionLogger.error("Exception in createDocumentFromString TRAXXUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("TRAXXUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return document;
    }

    private static String getTagValue(String Tag, Element eElement)
    {
        NodeList nlList = null;
        String value = "";
        if (eElement != null ){
            value = eElement.getAttribute(Tag);
        }
       /* if (nlList != null && nlList.item(0) != null){
            Node nValue = nlList.item(0);
            value = nValue.getNodeValue();
        }*/

        /*if (eElement != null && eElement.getElementsByTagName(Tag) != null && eElement.getElementsByTagName(Tag).item(0) != null){
            nlList = eElement.getElementsByTagName(Tag).item(0).getChildNodes();
        }
        if (nlList != null && nlList.item(0) != null){
            Node nValue = nlList.item(0);
            value = nValue.getNodeValue();
        }*/
        return value;
    }
}
