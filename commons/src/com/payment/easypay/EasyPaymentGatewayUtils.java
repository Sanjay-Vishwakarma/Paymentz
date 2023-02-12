package com.payment.easypay;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;

import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.security.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Admin on 10/19/2021.
 */
public class EasyPaymentGatewayUtils
{
    private static TransactionLogger transactionLogger  = new TransactionLogger(EasyPaymentGatewayUtils.class.getName());
    public static HashMap<String, String> countryISOHM  = new HashMap<>();
    public static HashMap<String, String> currencyISOHM = new HashMap<>();
    public static HashMap<String, String> stateISOHM    = new HashMap<>();
    static Functions functions                          = new Functions();
    String merchantTransactionId;


    static
    {
        // country
        countryISOHM.put("Algeria", "DZ");
        countryISOHM.put("Angola", "AO");
        countryISOHM.put("Benin", "BJ");
        countryISOHM.put("Botswana", "BW");
        countryISOHM.put("British Indian Ocean Territory", "IO");
        countryISOHM.put("Burkina Faso", "BF");
        countryISOHM.put("Burundi", "BI");
        countryISOHM.put("Cabo Verde", "CV");
        countryISOHM.put("Cameroon", "CM");
        countryISOHM.put("Central African Republic", "CF");
        countryISOHM.put("Chad", "TD");
        countryISOHM.put("Comoros", "KM");
        countryISOHM.put("Congo", "CG");
        countryISOHM.put("Congo Democratic Republic of the", "CD");
        countryISOHM.put("Côte d'Ivoire", "CI");
        countryISOHM.put("Djibouti", "DJ");
        countryISOHM.put("Egypt", "EG");
        countryISOHM.put("Equatorial Guinea", "GQ");
        countryISOHM.put("Eritrea", "ER");
        countryISOHM.put("Eswatini", "SZ");
        countryISOHM.put("Ethiopia", "ET");
        countryISOHM.put("French Southern Territories", "TF");
        countryISOHM.put("Gabon", "GA");
        countryISOHM.put("Gambia", "GM");
        countryISOHM.put("Ghana", "GH");
        countryISOHM.put("Guinea", "GN");
        countryISOHM.put("Guinea-Bissau", "GW");
        countryISOHM.put("Kenya", "KE");
        countryISOHM.put("Lesotho", "LS");
        countryISOHM.put("Liberia", "LR");
        countryISOHM.put("Libya", "LY");
        countryISOHM.put("Madagascar", "MG");
        countryISOHM.put("Malawi", "MW");
        countryISOHM.put("Mali", "ML");
        countryISOHM.put("Mauritania", "MR");
        countryISOHM.put("Mauritius", "MU");
        countryISOHM.put("Mayotte", "YT");
        countryISOHM.put("Morocco", "MA");
        countryISOHM.put("Mozambique", "MZ");
        countryISOHM.put("Namibia", "NA");
        countryISOHM.put("Niger", "NE");
        countryISOHM.put("Nigeria", "NG");
        countryISOHM.put("Réunion", "RE");
        countryISOHM.put("Rwanda", "RW");
        countryISOHM.put("Saint Helena Ascension and Tristan da Cunha", "SH");
        countryISOHM.put("Sao Tome and Principe", "ST");
        countryISOHM.put("Senegal", "SN");
        countryISOHM.put("Seychelles", "SC");
        countryISOHM.put("Sierra Leone", "SL");
        countryISOHM.put("Somalia", "SO");
        countryISOHM.put("South Africa", "ZA");
        countryISOHM.put("South Sudan", "SS");
        countryISOHM.put("Sudan", "SD");
        countryISOHM.put("Tanzania United Republic of", "TZ");
        countryISOHM.put("Togo", "TG");
        countryISOHM.put("Tunisia", "TN");
        countryISOHM.put("Uganda", "UG");
        countryISOHM.put("Western Sahara", "EH");
        countryISOHM.put("Zambia", "ZM");
        countryISOHM.put("Zimbabwe", "ZW");
        countryISOHM.put("Anguilla", "AI	");
        countryISOHM.put("Antigua and Barbuda", "AG");
        countryISOHM.put("Argentina", "AR");
        countryISOHM.put("Aruba", "AW");
        countryISOHM.put("Bahamas", "BS");
        countryISOHM.put("Barbados", "BB");
        countryISOHM.put("Belize", "BZ");
        countryISOHM.put("Bermuda", "BM");
        countryISOHM.put("Bolivia (Plurinational State of)", "BO");
        countryISOHM.put("Bonaire Sint Eustatius and Saba", "BQ");
        countryISOHM.put("Bouvet Island", "BV");
        countryISOHM.put("Brazil", "BR");
        countryISOHM.put("Canada", "CA");
        countryISOHM.put("Cayman Islands", "KY");
        countryISOHM.put("Chile", "CL");
        countryISOHM.put("Colombia", "CO");
        countryISOHM.put("Costa Rica", "CR");
        countryISOHM.put("Cuba", "CU");
        countryISOHM.put("Curaçao", "CW");
        countryISOHM.put("Dominica", "DM");
        countryISOHM.put("Dominican Republic", "DO");
        countryISOHM.put("Ecuador", "EC");
        countryISOHM.put("El Salvador", "SV");
        countryISOHM.put("Falkland Islands (Malvinas)", "FK");
        countryISOHM.put("French Guiana", "GF");
        countryISOHM.put("Greenland", "GL");
        countryISOHM.put("Grenada", "GD");
        countryISOHM.put("Guadeloupe", "GP");
        countryISOHM.put("Guatemala", "GT");
        countryISOHM.put("Guyana", "GY");
        countryISOHM.put("Haiti", "HT");
        countryISOHM.put("Honduras", "HN");
        countryISOHM.put("Jamaica", "JM");
        countryISOHM.put("Martinique", "MQ");
        countryISOHM.put("Mexico", "MX");
        countryISOHM.put("Montserrat", "MS");
        countryISOHM.put("Nicaragua", "NI");
        countryISOHM.put("Panama", "PA");
        countryISOHM.put("Paraguay", "PY");
        countryISOHM.put("Peru", "PE");
        countryISOHM.put("Puerto Rico", "PR");
        countryISOHM.put("Saint Barthélemy", "BL");
        countryISOHM.put("Saint Kitts and Nevis", "KN");
        countryISOHM.put("Saint Lucia", "LC");
        countryISOHM.put("Saint Martin (French part)", "MF");
        countryISOHM.put("Saint Pierre and Miquelon", "PM");
        countryISOHM.put("Saint Vincent and the Grenadines", "VC");
        countryISOHM.put("Sint Maarten (Dutch part)", "SX");
        countryISOHM.put("South Georgia and the South Sandwich Islands", "GS");
        countryISOHM.put("Suriname", "SR");
        countryISOHM.put("Trinidad and Tobago", "TT");
        countryISOHM.put("Turks and Caicos Islands", "TC");
        countryISOHM.put("United States of America", "US");
        countryISOHM.put("Uruguay", "UY");
        countryISOHM.put("Venezuela (Bolivarian Republic of)", "VE");
        countryISOHM.put("Virgin Islands (British)", "VG");
        countryISOHM.put("Virgin Islands (U.S.)", "VI");
        countryISOHM.put("Afghanistan", "AF");
        countryISOHM.put("Armenia", "AM");
        countryISOHM.put("Azerbaijan", "AZ");
        countryISOHM.put("Bahrain", "BH");
        countryISOHM.put("Bangladesh", "BD");
        countryISOHM.put("Bhutan ", "BT");
        countryISOHM.put("Brunei Darussalam", "BN");
        countryISOHM.put("Cambodia", "KH");
        countryISOHM.put("China", "CN");
        countryISOHM.put("Cyprus", "CY");
        countryISOHM.put("Georgia", "GE");
        countryISOHM.put("Hong Kong", "HK");
        countryISOHM.put("India", "IN");
        countryISOHM.put("Indonesia", "ID");
        countryISOHM.put("Iran (Islamic Republic of)", "IR");
        countryISOHM.put("Iraq", "IQ");
        countryISOHM.put("Israel", "IL");
        countryISOHM.put("Japan", "JP");
        countryISOHM.put("Jordan", "JO");
        countryISOHM.put("Kazakhstan", "KZ");
        countryISOHM.put("Korea (Democratic People's Republic of)", "KP");
        countryISOHM.put("Korea Republic of", "KR");
        countryISOHM.put("Kuwait", "KW");
        countryISOHM.put("Kyrgyzstan", "KG");
        countryISOHM.put("Lao People's Democratic Republic", "LA");
        countryISOHM.put("Lebanon", "LB");
        countryISOHM.put("Macao", "MO");
        countryISOHM.put("Malaysia", "MY");
        countryISOHM.put("Maldives", "MV");
        countryISOHM.put("Mongolia", "MN");
        countryISOHM.put("Myanmar", "MM");
        countryISOHM.put("Nepal", "NP");
        countryISOHM.put("Oman", "OM");
        countryISOHM.put("Pakistan", "PK");
        countryISOHM.put("Palestine State of", "PS");
        countryISOHM.put("Philippines", "PH");
        countryISOHM.put("Qatar", "QA");
        countryISOHM.put("Saudi Arabia", "SA");
        countryISOHM.put("Singapore", "SG");
        countryISOHM.put("Sri Lanka", "LK");
        countryISOHM.put("Syrian Arab Republic", "SY");
        countryISOHM.put("Taiwan Province of China", "TW");
        countryISOHM.put("Tajikistan", "TJ");
        countryISOHM.put("Thailand", "TH");
        countryISOHM.put("Timor-Leste", "TL");
        countryISOHM.put("Turkey", "TR");
        countryISOHM.put("Turkmenistan", "TM");
        countryISOHM.put("United Arab Emirates", "AE");
        countryISOHM.put("Uzbekistan", "UZ");
        countryISOHM.put("Viet Nam", "VN");
        countryISOHM.put("Yemen", "YE");
        countryISOHM.put("Åland Islands", "AX");
        countryISOHM.put("Albania", "AL");
        countryISOHM.put("Andorra", "AD");
        countryISOHM.put("Austria", "AT");
        countryISOHM.put("Belarus", "BY");
        countryISOHM.put("Belgium", "BE");
        countryISOHM.put("Bosnia and Herzegovina", "BA");
        countryISOHM.put("Bulgaria", "BG");
        countryISOHM.put("Croatia", "HR");
        countryISOHM.put("Czechia", "CZ");
        countryISOHM.put("Denmark", "DK");
        countryISOHM.put("Estonia", "EE");
        countryISOHM.put("Faroe Islands", "FO");
        countryISOHM.put("Finland", "FI");
        countryISOHM.put("France ", "FR");
        countryISOHM.put("Germany", "DE");
        countryISOHM.put("Gibraltar", "GI");
        countryISOHM.put("Greece", "GR");
        countryISOHM.put("Guernsey", "GG");
        countryISOHM.put("Holy See", "VA");
        countryISOHM.put("Hungary", "HU");
        countryISOHM.put("Iceland", "IS");
        countryISOHM.put("Ireland", "IE");
        countryISOHM.put("Isle of Man", "IM");
        countryISOHM.put("Italy", "IT");
        countryISOHM.put("Jersey", "JE");
        countryISOHM.put("Latvia", "LV");
        countryISOHM.put("Liechtenstein", "LI");
        countryISOHM.put("Lithuania", "LT");
        countryISOHM.put("Luxembourg", "LU");
        countryISOHM.put("Malta", "MT");
        countryISOHM.put("Moldova Republic of", "MD");
        countryISOHM.put("Monaco", "MC");
        countryISOHM.put("Montenegro", "ME");
        countryISOHM.put("Netherlands", "NL");
        countryISOHM.put("North Macedonia", "MK");
        countryISOHM.put("Norway", "NO");
        countryISOHM.put("Poland", "PL");
        countryISOHM.put("Portugal", "PT");
        countryISOHM.put("Romania", "RO");
        countryISOHM.put("Russian Federation", "RU");
        countryISOHM.put("San Marino", "SM");
        countryISOHM.put("Serbia", "RS");
        countryISOHM.put("Slovakia", "SK");
        countryISOHM.put("Slovenia", "SI");
        countryISOHM.put("Spain", "ES");
        countryISOHM.put("Svalbard and Jan Mayen", "SJ");
        countryISOHM.put("Sweden", "SE");
        countryISOHM.put("Switzerland", "CH");
        countryISOHM.put("Ukraine", "UA");
        countryISOHM.put("United Kingdom of Great Britain and Northern Ireland", "GB");
        countryISOHM.put("American Samoa", "AS");
        countryISOHM.put("Australia", "AU");
        countryISOHM.put("Christmas Island", "CX");
        countryISOHM.put("Cocos (Keeling) Islands", "CC");
        countryISOHM.put("Cook Islands", "CK");
        countryISOHM.put("Fiji", "FJ");
        countryISOHM.put("French Polynesia", "PF");
        countryISOHM.put("Guam", "GU");
        countryISOHM.put("Heard Island and McDonald Islands", "HM");
        countryISOHM.put("Kiribati", "KI");
        countryISOHM.put("Marshall Islands", "MH");
        countryISOHM.put("Micronesia (Federated States of)", "FM");
        countryISOHM.put("Nauru", "NR");
        countryISOHM.put("New Caledonia ", "NC");
        countryISOHM.put("New Zealand", "NZ");
        countryISOHM.put("Niue", "NU");
        countryISOHM.put("Norfolk Island", "NF");
        countryISOHM.put("Northern Mariana Islands", "MP");
        countryISOHM.put("Palau ", "PW");
        countryISOHM.put("Papua New Guinea", "PG");
        countryISOHM.put("Pitcairn", "PN");
        countryISOHM.put("Samoa", "WS");
        countryISOHM.put("Solomon Islands", "SB");
        countryISOHM.put("Tokelau", "TK");
        countryISOHM.put("Tonga", "TO");
        countryISOHM.put("Tuvalu", "TV");
        countryISOHM.put("United States Minor Outlying Islands", "UM");
        countryISOHM.put("Vanuatu", "VU");
        countryISOHM.put("Wallis and Futuna", "WF");

        // currency
        currencyISOHM.put("AED","AED");
        currencyISOHM.put("AFN","AFN");
        currencyISOHM.put("ALL","ALL");
        currencyISOHM.put("AMD","AMD");
        currencyISOHM.put("ANG","ANG");
        currencyISOHM.put("AOA","AOA");
        currencyISOHM.put("ARS","ARS");
        currencyISOHM.put("AUD","AUD");
        currencyISOHM.put("AWG","AWG");
        currencyISOHM.put("AZN","AZN");
        currencyISOHM.put("BAM","BAM");
        currencyISOHM.put("BBD","BBD");
        currencyISOHM.put("BDT","BDT");
        currencyISOHM.put("BGN","BGN");
        currencyISOHM.put("BHD","BHD");
        currencyISOHM.put("BIF","BIF");
        currencyISOHM.put("BMD","BMD");
        currencyISOHM.put("BND","BND");
        currencyISOHM.put("BOB","BOB");
        currencyISOHM.put("BOV","BOV");
        currencyISOHM.put("BRL","BRL");
        currencyISOHM.put("BSD","BSD");
        currencyISOHM.put("BTN","BTN");
        currencyISOHM.put("BWP","BWP");
        currencyISOHM.put("BYN","BYN");
        currencyISOHM.put("BZD","BZD");
        currencyISOHM.put("CAD","CAD");
        currencyISOHM.put("CDF","CDF");
        currencyISOHM.put("CHE","CHE");
        currencyISOHM.put("CHF","CHF");
        currencyISOHM.put("CHW","CHW");
        currencyISOHM.put("CLF","CLF");
        currencyISOHM.put("CLP","CLP");
        currencyISOHM.put("CNY","CNY");
        currencyISOHM.put("COP","COP");
        currencyISOHM.put("COU","COU");
        currencyISOHM.put("CRC","CRC");
        currencyISOHM.put("CUC","CUC");
        currencyISOHM.put("CUP","CUP");
        currencyISOHM.put("CVE","CVE");
        currencyISOHM.put("CZK","CZK");
        currencyISOHM.put("DJF","DJF");
        currencyISOHM.put("DKK","DKK");
        currencyISOHM.put("DOP","DOP");
        currencyISOHM.put("DZD","DZD");
        currencyISOHM.put("EGP","EGP");
        currencyISOHM.put("ERN","ERN");
        currencyISOHM.put("ETB","ETB");
        currencyISOHM.put("EUR","EUR");
        currencyISOHM.put("FKP","FKP");
        currencyISOHM.put("GBP","GBP");
        currencyISOHM.put("GEL","GEL");
        currencyISOHM.put("GHS","GHS");
        currencyISOHM.put("GIP","GIP");
        currencyISOHM.put("GMD","GMD");
        currencyISOHM.put("GNF","GNF");
        currencyISOHM.put("GTQ","GTQ");
        currencyISOHM.put("GYD","GYD");
        currencyISOHM.put("HKD","HKD");
        currencyISOHM.put("HNL","HNL");
        currencyISOHM.put("HRK","HRK");
        currencyISOHM.put("HTG","HTG");
        currencyISOHM.put("HUF","HUF");
        currencyISOHM.put("IDR","IDR");
        currencyISOHM.put("ILS","ILS");
        currencyISOHM.put("INR","INR");
        currencyISOHM.put("IQD","IQD");
        currencyISOHM.put("IRR","IRR");
        currencyISOHM.put("ISK","ISK");
        currencyISOHM.put("JMD","JMD");
        currencyISOHM.put("JOD","JOD");
        currencyISOHM.put("JPY","JPY");
        currencyISOHM.put("KES","KES");
        currencyISOHM.put("KGS","KGS");
        currencyISOHM.put("KHR","KHR");
        currencyISOHM.put("KMF","KMF");
        currencyISOHM.put("KPW","KPW");
        currencyISOHM.put("KRW","KRW");
        currencyISOHM.put("KWD","KWD");
        currencyISOHM.put("KYD","KYD");
        currencyISOHM.put("KZT","KZT");
        currencyISOHM.put("LAK","LAK");
        currencyISOHM.put("LBP","LBP");
        currencyISOHM.put("LKR","LKR");
        currencyISOHM.put("LRD","LRD");
        currencyISOHM.put("LSL","LSL");
        currencyISOHM.put("LYD","LYD");
        currencyISOHM.put("MAD","MAD");
        currencyISOHM.put("MDL","MDL");
        currencyISOHM.put("MGA","MGA");
        currencyISOHM.put("MKD","MKD");
        currencyISOHM.put("MMK","MMK");
        currencyISOHM.put("MNT","MNT");
        currencyISOHM.put("MOP","MOP");
        currencyISOHM.put("MRU","MRU");
        currencyISOHM.put("MUR","MUR");
        currencyISOHM.put("MVR","MVR");
        currencyISOHM.put("MWK","MWK");
        currencyISOHM.put("MXN","MXN");
        currencyISOHM.put("MXV","MXV");
        currencyISOHM.put("MYR","MYR");
        currencyISOHM.put("MZN","MZN");
        currencyISOHM.put("NAD","NAD");
        currencyISOHM.put("NGN","NGN");
        currencyISOHM.put("NIO","NIO");
        currencyISOHM.put("NOK","NOK");
        currencyISOHM.put("NPR","NPR");
        currencyISOHM.put("NZD","NZD");
        currencyISOHM.put("OMR","OMR");
        currencyISOHM.put("PAB","PAB");
        currencyISOHM.put("PEN","PEN");
        currencyISOHM.put("PGK","PGK");
        currencyISOHM.put("PHP","PHP");
        currencyISOHM.put("PKR","PKR");
        currencyISOHM.put("PLN","PLN");
        currencyISOHM.put("PYG","PYG");
        currencyISOHM.put("QAR","QAR");
        currencyISOHM.put("RON","RON");
        currencyISOHM.put("RSD","RSD");
        currencyISOHM.put("RUB","RUB");
        currencyISOHM.put("RWF","RWF");
        currencyISOHM.put("SAR","SAR");
        currencyISOHM.put("SBD","SBD");
        currencyISOHM.put("SCR","SCR");
        currencyISOHM.put("SDG","SDG");
        currencyISOHM.put("SEK","SEK");
        currencyISOHM.put("SGD","SGD");
        currencyISOHM.put("SHP","SHP");
        currencyISOHM.put("SLL","SLL");
        currencyISOHM.put("SOS","SOS");
        currencyISOHM.put("SRD","SRD");
        currencyISOHM.put("SSP","SSP");
        currencyISOHM.put("STN","STN");
        currencyISOHM.put("SVC","SVC");
        currencyISOHM.put("SYP","SYP");
        currencyISOHM.put("SZL","SZL");
        currencyISOHM.put("THB","THB");
        currencyISOHM.put("TJS","TJS");
        currencyISOHM.put("TMT","TMT");
        currencyISOHM.put("TND","TND");
        currencyISOHM.put("TOP","TOP");
        currencyISOHM.put("TRY","TRY");
        currencyISOHM.put("TTD","TTD");
        currencyISOHM.put("TWD","TWD");
        currencyISOHM.put("TZS","TZS");
        currencyISOHM.put("UAH","UAH");
        currencyISOHM.put("UGX","UGX");
        currencyISOHM.put("USD","USD");
        currencyISOHM.put("UYI","UYI");
        currencyISOHM.put("UYU","UYU");
        currencyISOHM.put("UYW","UYW");
        currencyISOHM.put("UZS","UZS");
        currencyISOHM.put("VES","VES");
        currencyISOHM.put("VND","VND");
        currencyISOHM.put("VUV","VUV");
        currencyISOHM.put("WST","WST");
        currencyISOHM.put("XAF","XAF");
        currencyISOHM.put("XAG","XAG");
        currencyISOHM.put("XAU","XAU");
        currencyISOHM.put("XBA","XBA");
        currencyISOHM.put("XBB","XBB");
        currencyISOHM.put("XBC","XBC");
        currencyISOHM.put("XBD","XBD");
        currencyISOHM.put("XCD","XCD");
        currencyISOHM.put("XDR","XDR");
        currencyISOHM.put("XOF","XOF");
        currencyISOHM.put("XPD","XPD");
        currencyISOHM.put("XPF","XPF");
        currencyISOHM.put("XPT","XPT");
        currencyISOHM.put("XSU","XSU");
        currencyISOHM.put("XTS","XTS");
        currencyISOHM.put("XUA","XUA");
        currencyISOHM.put("XXX","XXX");
        currencyISOHM.put("YER","YER");
        currencyISOHM.put("ZAR","ZAR");
        currencyISOHM.put("ZMW","ZMW");
        currencyISOHM.put("ZWL","ZWL");

        //state
        stateISOHM.put("Alabama","AL");
        stateISOHM.put("Alaska","AK");
        stateISOHM.put("Arizona","AZ");
        stateISOHM.put("Arkansas","AR");
        stateISOHM.put("California","CA");
        stateISOHM.put("Colorado","CO");
        stateISOHM.put("Connecticut","CT");
        stateISOHM.put("Delaware","DE");
        stateISOHM.put("Florida","FL");
        stateISOHM.put("Georgia","GA");
        stateISOHM.put("Hawaii","HI");
        stateISOHM.put("Idaho","ID");
        stateISOHM.put("Illinois","IL");
        stateISOHM.put("Indiana","IN");
        stateISOHM.put("Iowa","IA");
        stateISOHM.put("Kansas","KS");
        stateISOHM.put("Kentucky[E]","KY");
        stateISOHM.put("Louisiana","LA");
        stateISOHM.put("Maine","ME");
        stateISOHM.put("Maryland","MD");
        stateISOHM.put("Massachusetts[E]","MA");
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
        stateISOHM.put("Pennsylvania[E]","PA");
        stateISOHM.put("Rhode Island","RI");
        stateISOHM.put("South Carolina","SC");
        stateISOHM.put("South Dakota","SD");
        stateISOHM.put("Tennessee","TN");
        stateISOHM.put("Texas","TX");
        stateISOHM.put("Utah","UT");
        stateISOHM.put("Vermont","VT");
        stateISOHM.put("Virginia[E]","VA");
        stateISOHM.put("Washington","WA");
        stateISOHM.put("West Virginia","WV");
        stateISOHM.put("Wisconsin","WI");
        stateISOHM.put("Wyoming","WY");
        stateISOHM.put("Ontario[b]","ON");
        stateISOHM.put("Quebec","QC");
        stateISOHM.put("Nova Scotia","NS");
        stateISOHM.put("New Brunswick","NB");
        stateISOHM.put("Manitoba","MB");
        stateISOHM.put("British Columbia","BC");
        stateISOHM.put("Prince Edward Island","PE");
        stateISOHM.put("Saskatchewan","SK");
        stateISOHM.put("Alberta","AB");
        stateISOHM.put("Newfoundland and Labrador","NL");
        stateISOHM.put("Australian Capital Territory","AC");
        stateISOHM.put("New South Wales","NS");
        stateISOHM.put("Northern Territory","NT");
        stateISOHM.put("Queensland","QL");
        stateISOHM.put("South Australia","SA");
        stateISOHM.put("Victoria","VI");
        stateISOHM.put("Western Australia","WA");
        stateISOHM.put("Berlin","BE");
        stateISOHM.put("Brandenburg","BR");
        stateISOHM.put("Baden-Württemberg","BW");
        stateISOHM.put("Bayern","BY");
        stateISOHM.put("Bremen","HB");
        stateISOHM.put("Hessen","HE");
        stateISOHM.put("Hamburg","HH");
        stateISOHM.put("Mecklenburg-Vorpommern","MV");
        stateISOHM.put("Niedersachsen","NI");
        stateISOHM.put("Nordrhein-Westfalen","NV");
        stateISOHM.put("Rheinland-Pfalz","RP");
        stateISOHM.put("Schleswig-Holstein","SH");
        stateISOHM.put("Saarland","SL");
        stateISOHM.put("Sachsen","SN");
        stateISOHM.put("Sachsen-Anhalt","ST");
        stateISOHM.put("Thüringen","TH");
        stateISOHM.put("Clare","CE");
        stateISOHM.put("Cork","CK");
        stateISOHM.put("Cavan","CN");
        stateISOHM.put("Carlow","CW");
        stateISOHM.put("Donegal","DL");
        stateISOHM.put("Dublin","DN");
        stateISOHM.put("Galway","GY");
        stateISOHM.put("Kildare","KE");
        stateISOHM.put("Kilkenny","KK");
        stateISOHM.put("Kerry","KY");
        stateISOHM.put("Longford","LD");
        stateISOHM.put("Louth","LH");
        stateISOHM.put("Limerick","LK");
        stateISOHM.put("Leitrim","LM");
        stateISOHM.put("Laois","LS");
        stateISOHM.put("Meath","MH");
        stateISOHM.put("Monaghan","MN");
        stateISOHM.put("Mayo","MO");
        stateISOHM.put("Offaly","OY");
        stateISOHM.put("Roscommon","RN");
        stateISOHM.put("Sligo","SO");
        stateISOHM.put("Tipperary","TY");
        stateISOHM.put("Waterford","WD");
        stateISOHM.put("Westmeath","WH");
        stateISOHM.put("Wicklow","WW");
        stateISOHM.put("Wexford","WX");
    }

    public static HashMap getCountryCodeHash()
    {
        return countryISOHM;
    }

    public static HashMap getCurrencyCodeHash()
    {
        return currencyISOHM;
    }

    public static HashMap getStateCodeHash()
    {
        return stateISOHM;
    }

    public String getMerchantTransactionId()
    {
        return merchantTransactionId;
    }

    public void setMerchantTransactionId(String merchantTransactionId)
    {
        this.merchantTransactionId = merchantTransactionId;
    }

    public  static String getLast2DigitOfExpiryYear(String ExpiryYear)
    {
        String expiryYearLast2Digit = "";
        if (functions.isValueNull(ExpiryYear)){
            expiryYearLast2Digit = ExpiryYear.substring(ExpiryYear.length()-2,ExpiryYear.length());
        }
        return expiryYearLast2Digit;
    }

    public static String getPaymentBrand(String paymentMode)
    {
        String payBrand = "";
        if("1".equalsIgnoreCase(paymentMode))
            payBrand = "Visa";
        if("2".equalsIgnoreCase(paymentMode))
            payBrand = "Mastercard";
        return payBrand;
    }

    public static String getPaymentSolution(String trackingId)
    {
        String responsehashinfo = "";
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String query = "SELECT responsehashinfo FROM transaction_common_details WHERE trackingId=? AND STATUS IN ('authstarted','3D_authstarted','authsuccessful','capturestarted','capturesuccess') AND responsehashinfo IS NOT NULL AND responsehashinfo !=''";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, trackingId);
            transactionLogger.error("Query of responsehashinfo ->"+ps);

            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                responsehashinfo = rs.getString("responsehashinfo");
            }
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException ->",e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError ->",systemError);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return responsehashinfo;
    }

    public static HashMap getSubscriptionPlan_CardNumberToken(String trackingId)
    {
        String boiledname           = "";
        String authorization_code   = "";
        HashMap<String,String> hm   = new HashMap<>();
        Connection con              = null;
        try
        {
            con = Database.getConnection();
            String query = "SELECT boiledname,authorization_code FROM transaction_common WHERE trackingId=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, trackingId);
            transactionLogger.error("Query for boiledname,authorization_code ->"+ps);

            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                boiledname          = rs.getString("boiledname");
                authorization_code  = rs.getString("authorization_code");
                hm.put("subscriptionPlan",boiledname);
                hm.put("cardNumberToken",authorization_code);
            }
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException ->",e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError ->",systemError);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return hm;
    }

    public static void updateMainTableEntry(String transactionId, String remark, String cardNumberToken, String subscriptionPlan, String trackingid)
    {
        Connection connection = null;
        try
        {
            String addParam="";
            if(functions.isValueNull(remark)){
                addParam += ",remark='"+remark+"'";
            }
            if(functions.isValueNull(cardNumberToken)){
                addParam += ",authorization_code='"+cardNumberToken+"'";
            }
            if(functions.isValueNull(subscriptionPlan)){
                addParam += ",boiledname='"+subscriptionPlan+"'";
            }

            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET paymentid='"+transactionId+"' "+addParam+" WHERE trackingid="+trackingid;
            Database.executeUpdate(updateQuery1, connection);
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

    public static Map<String, String> readEasyPayment3DRedirectionXMLReponse(String str) throws Exception
    {
        Map<String, String> responseMap = new HashMap<String, String>();

        Document doc = createDocumentFromString(str);
        doc.getDocumentElement().getNodeName();
        NodeList nList  = null;

        if (str.contains("epg-status-response")) {
            nList = doc.getElementsByTagName("epg-status-response");
        }
        else if (str.contains("payfrex-response")){
            nList = doc.getElementsByTagName("payfrex-response");
        }
        else
        {
            nList = doc.getElementsByTagName("operations");
        }

        responseMap.put("merchantTransactionId", getTagValue("merchantTransactionId", ((Element) nList.item(0))));
        responseMap.put("payFrexTransactionId", getTagValue("payFrexTransactionId", ((Element) nList.item(0))));
        responseMap.put("redirectionResponse", getTagValue("redirectionResponse", ((Element) nList.item(0))));
        responseMap.put("cardNumberToken", getTagValue("cardNumberToken", ((Element) nList.item(0))));
        responseMap.put("code", getTagValue("code", ((Element) nList.item(0))));
        responseMap.put("resultCode", getTagValue("resultCode", ((Element) nList.item(0))));
        responseMap.put("authCode", getTagValue("authCode", ((Element) nList.item(0))));
        responseMap.put("uuid", getTagValue("uuid", ((Element) nList.item(0))));
        responseMap.put("amount", getTagValue("amount", ((Element) nList.item(0))));
        responseMap.put("currency", getTagValue("currency", ((Element) nList.item(0))));
        responseMap.put("internalToken", getTagValue("internalToken", ((Element) nList.item(0))));
        responseMap.put("paymentSolution", getTagValue("paymentSolution", ((Element) nList.item(0))));
        responseMap.put("details", getTagValue("details", ((Element) nList.item(0))));
        responseMap.put("status", getTagValue("status", (Element) ((Element) nList.item(0)).getElementsByTagName("operation").item(0)));
        responseMap.put("message", getTagValue("message", (Element) ((Element) nList.item(0)).getElementsByTagName("respCode").item(0)));
        responseMap.put("operationType", getTagValue("operationType", ((Element) nList.item(0))));
        responseMap.put("issuerBank", getTagValue("issuerBank", ((Element) nList.item(0))));
        responseMap.put("paymentCode", getTagValue("paymentCode", ((Element) nList.item(0))));
        responseMap.put("version", getTagValue("version", ((Element) nList.item(0))));
        responseMap.put("paymentMethod", getTagValue("paymentMethod", ((Element) nList.item(0))));
        responseMap.put("subscriptionPlan", getTagValue("subscriptionPlan", ((Element) nList.item(0))));

        return responseMap;
    }

    public static Document createDocumentFromString(String xmlString) throws PZTechnicalViolationException
    {
        Document document = null;
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            document = docBuilder.parse(new InputSource(new StringReader(xmlString)));
        }
        catch (ParserConfigurationException pce){
            transactionLogger.error("Exception in createDocumentFromString of EasyPaymentUtils", pce);
            PZExceptionHandler.raiseTechnicalViolationException("EasyPaymentGatewayUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e){
            transactionLogger.error("Exception in createDocumentFromString EasyPaymentUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("EasyPaymentGatewayUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.SAXEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e){
            transactionLogger.error("Exception in createDocumentFromString EasyPaymentUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("EasyPaymentGatewayUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return document;
    }

    private static String getTagValue(String Tag, Element eElement)
    {
        NodeList nlList = null;
        String value = "";
        if (eElement != null && eElement.getElementsByTagName(Tag) != null && eElement.getElementsByTagName(Tag).item(0) != null){
            nlList = eElement.getElementsByTagName(Tag).item(0).getChildNodes();
        }
        if (nlList != null && nlList.item(0) != null){
            Node nValue = nlList.item(0);
            value = nValue.getNodeValue();
        }
        return value;
    }

    public static String applyMD5encryption(String data)
    {
        String result ="";
        try
        {
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(data.getBytes(Charset.forName("UTF8")));
            final byte[] resultBytes = messageDigest.digest();
            result = Hex.encodeHexString(resultBytes);
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("Exception while encrypt : "+ e);
        }
        return result;
    }

    public static IvParameterSpec generateIv()
    {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static String encryptCBC(String requestData, String secretKey, IvParameterSpec iv)
    {
        try
        {
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] cipherText = cipher.doFinal(requestData.getBytes());
            return Base64.getEncoder().encodeToString(cipherText);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception while encrypt : " + e);
        }
        return null;
    }

    public static String decryptCBC(String cipherText, String SecretKey, IvParameterSpec iv)
    {
        try
        {
            SecretKeySpec key = new SecretKeySpec(SecretKey.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(plainText);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception while decrypt  : " + e);
        }
        return null;
    }

    public static String generateSHA256Hash(String data)
    {
        StringBuffer sb = new StringBuffer();
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(data.getBytes());
            byte[] byteData = messageDigest.digest();
            for (int i = 0; i < byteData.length; i++)
            {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("Exception while generating SHA Hash : "+ e);
        }
        return sb.toString();
    }

    public static String doHttpPostRequestConnection(String urlwithQueryString, String iv) throws PZTechnicalViolationException
    {
        String response = "";
        PostMethod post = new PostMethod(urlwithQueryString);
        HttpClient httpClient = new HttpClient();
        try
        {
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.addRequestHeader("apiVersion", "3");
            post.setRequestHeader("encryptionMode", "CBC");
            post.addRequestHeader("iv", iv);
            httpClient.executeMethod(post);
            response = new String(post.getResponseBody());
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("EasyPaymentGatewayUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("EasyPaymentGatewayUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            post.releaseConnection();
        }
        return response;
    }

    public static String doHttpPostRequestConnection(String urlwithQueryString) throws PZTechnicalViolationException
    {
        String result = "";
        PostMethod post = new PostMethod(urlwithQueryString);
        HttpClient httpClient = new HttpClient();
        try
        {
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            httpClient.executeMethod(post);
            result = new String(post.getResponseBody());

        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("EasyPaymentGatewayUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("EasyPaymentGatewayUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }
        finally
        {
            post.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }


}
