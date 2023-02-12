<%@ page import="com.directi.pg.Functions,com.directi.pg.LoadProperties,com.directi.pg.TransactionEntry,com.directi.pg.core.GatewayAccountService" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.enums.TemplatePreference" %>
<%@ include file="top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 10/8/13
  Time: 2:47 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    session.setAttribute("submit","partnerTransactions");
%>
<%


    HashMap<String, String> timezoneHash = new HashMap<>();
    timezoneHash.put("Etc/GMT+12|(GMT-12:00)","(GMT-12:00) International Date Line West");
    timezoneHash.put("Pacific/Midway|(GMT-11:00","(GMT-11:00) Midway Island, Samoa");
    timezoneHash.put("Pacific/Honolulu|(GMT-10:00)","(GMT-10:00) Hawaii");
    timezoneHash.put("US/Alaska|(GMT-09:00)","(GMT-09:00) Alaska");
    timezoneHash.put("America/Los_Angeles|(GMT-08:00)","(GMT-08:00) Pacific Time (US & Canada)");
    timezoneHash.put("America/Tijuana|(GMT-08:00)","(GMT-08:00) Tijuana, Baja California");
    timezoneHash.put("US/Arizona|(GMT-07:00)","(GMT-07:00) Arizona");
    timezoneHash.put("America/Chihuahua|(GMT-07:00)","(GMT-07:00) Chihuahua, La Paz, Mazatlan");
    timezoneHash.put("US/Mountain|(GMT-07:00)","(GMT-07:00) Mountain Time (US & Canada)");
    timezoneHash.put("America/Managua|(GMT-06:00)","(GMT-06:00) Central America");
    timezoneHash.put("US/Central|(GMT-06:00)","(GMT-06:00) Central Time (US & Canada)");
    timezoneHash.put("America/Mexico_City|(GMT-06:00)","(GMT-06:00) Guadalajara, Mexico City, Monterrey");
    timezoneHash.put("Canada/Saskatchewan|(GMT-06:00)","(GMT-06:00) Saskatchewan");
    timezoneHash.put("America/Bogota|(GMT-05:00)","(GMT-05:00) Bogota, Lima, Quito, Rio Branco");
    timezoneHash.put("US/Eastern|(GMT-05:00)","(GMT-05:00) Eastern Time (US & Canada)");
    timezoneHash.put("US/East-Indiana|(GMT-05:00)","(GMT-05:00) Indiana (East)");
    timezoneHash.put("Canada/Atlantic|(GMT-04:00)","(GMT-04:00) Atlantic Time (Canada)");
    timezoneHash.put("America/Caracas|(GMT-04:00)","(GMT-04:00) Caracas, La Paz");
    timezoneHash.put("America/Manaus|(GMT-04:00)","(GMT-04:00) Manaus");
    timezoneHash.put("America/Santiago|(GMT-04:00)","(GMT-04:00) Santiago");
    timezoneHash.put("Canada/Newfoundland|(GMT-03:30)","(GMT-03:30) Newfoundland");
    timezoneHash.put("America/Sao_Paulo|(GMT-03:00)","(GMT-03:00) Brasilia");
    timezoneHash.put("America/Argentina/Buenos_Aires|(GMT-03:00)","(GMT-03:00) Buenos Aires, Georgetown");
    timezoneHash.put("America/Godthab|(GMT-03:00)","(GMT-03:00) Greenland");
    timezoneHash.put("America/Montevideo|(GMT-03:00)","(GMT-03:00) Montevideo");
    timezoneHash.put("America/Noronha|(GMT-02:00)","(GMT-02:00) Mid-Atlantic");
    timezoneHash.put("Atlantic/Cape_Verde|(GMT-01:00)","(GMT-01:00) Cape Verde Is.");
    timezoneHash.put("Atlantic/Azores|(GMT-01:00)","(GMT-01:00) Azores");
    timezoneHash.put("Africa/Casablanca|(GMT+00:00)","(GMT+00:00) Casablanca, Monrovia, Reykjavik");
    timezoneHash.put("Etc/Greenwich|(GMT+00:00)","(GMT+00:00) Greenwich Mean Time : Dublin, Edinburgh, Lisbon, London");
    timezoneHash.put("Europe/Amsterdam|(GMT+01:00)","(GMT+01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna");
    timezoneHash.put("Europe/Belgrade|(GMT+01:00)","(GMT+01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague");
    timezoneHash.put("Europe/Brussels|(GMT+01:00)","(GMT+01:00) Brussels, Copenhagen, Madrid, Paris");
    timezoneHash.put("Europe/Sarajevo|(GMT+01:00)","(GMT+01:00) Sarajevo, Skopje, Warsaw, Zagreb");
    timezoneHash.put("Africa/Lagos|(GMT+01:00)","(GMT+01:00) West Central Africa");
    timezoneHash.put("Asia/Amman|(GMT+02:00)","(GMT+02:00) Amman");
    timezoneHash.put("Europe/Athens|(GMT+02:00)","(GMT+02:00) Athens, Bucharest, Istanbul");
    timezoneHash.put("Asia/Beirut|(GMT+02:00)","(GMT+02:00) Beirut");
    timezoneHash.put("Africa/Cairo|(GMT+02:00)","(GMT+02:00) Cairo");
    timezoneHash.put("Africa/Harare|(GMT+02:00)","(GMT+02:00) Harare, Pretoria");
    timezoneHash.put("Europe/Helsinki|(GMT+02:00)","(GMT+02:00) Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius");
    timezoneHash.put("Asia/Jerusalem|(GMT+02:00)","(GMT+02:00) Jerusalem");
    timezoneHash.put("Europe/Minsk|(GMT+02:00)","(GMT+02:00) Minsk");
    timezoneHash.put("Africa/Windhoek|(GMT+02:00)","(GMT+02:00) Windhoek");
    timezoneHash.put("Asia/Kuwait|(GMT+03:00)","(GMT+03:00) Kuwait, Riyadh, Baghdad");
    timezoneHash.put("Europe/Moscow|(GMT+03:00)","(GMT+03:00) Moscow, St. Petersburg, Volgograd");
    timezoneHash.put("Africa/Nairobi|(GMT+03:00)","(GMT+03:00) Nairobi");
    timezoneHash.put("Asia/Tbilisi|(GMT+03:00)","(GMT+03:00) Tbilisi");
    timezoneHash.put("Asia/Tehran|(GMT+03:30)","(GMT+03:30) Tehran");
    timezoneHash.put("Asia/Muscat|(GMT+04:00)","(GMT+04:00) Abu Dhabi, Muscat");
    timezoneHash.put("Asia/Baku|(GMT+04:00)","(GMT+04:00) Baku");
    timezoneHash.put("Asia/Yerevan|(GMT+04:00)","(GMT+04:00) Yerevan");
    timezoneHash.put("Asia/Kabul|(GMT+04:30)","(GMT+04:30) Kabul");
    timezoneHash.put("Asia/Yekaterinburg|(GMT+05:00)","(GMT+05:00) Yekaterinburg");
    timezoneHash.put("Asia/Karachi|(GMT+05:00)","(GMT+05:00) Islamabad, Karachi, Tashkent");
    timezoneHash.put("Asia/Calcutta|(GMT+05:30)","(GMT+05:30) Chennai, Kolkata, Mumbai, New Delhi");
    timezoneHash.put("Asia/Katmandu|(GMT+05:45)","(GMT+05:45) Kathmandu");
    timezoneHash.put("Asia/Almaty|(GMT+06:00)","(GMT+06:00) Almaty, Novosibirsk");
    timezoneHash.put("Asia/Dhaka|(GMT+06:00)","(GMT+06:00) Astana, Dhaka");
    timezoneHash.put("Asia/Rangoon|(GMT+06:30)","(GMT+06:30) Yangon (Rangoon)");
    timezoneHash.put("Asia/Bangkok|(GMT+07:00)","(GMT+07:00) Bangkok, Hanoi, Jakarta");
    timezoneHash.put("Asia/Krasnoyarsk|(GMT+07:00)","(GMT+07:00) Krasnoyarsk");
    timezoneHash.put("Asia/Hong_Kong|(GMT+08:00)","(GMT+08:00) Beijing, Chongqing, Hong Kong, Urumqi");
    timezoneHash.put("Asia/Kuala_Lumpur|(GMT+08:00)","(GMT+08:00) Kuala Lumpur, Singapore");
    timezoneHash.put("Asia/Irkutsk|(GMT+08:00)","(GMT+08:00) Irkutsk, Ulaan Bataar");
    timezoneHash.put("Australia/Perth|(GMT+08:00)","(GMT+08:00) Perth");
    timezoneHash.put("Asia/Taipei|(GMT+08:00)","(GMT+08:00) Taipei");
    timezoneHash.put("Asia/Tokyo|(GMT+09:00)","(GMT+09:00) Osaka, Sapporo, Tokyo");
    timezoneHash.put("Asia/Seoul|(GMT+09:00)","(GMT+09:00) Seoul");
    timezoneHash.put("Asia/Yakutsk|GMT+09:00)","(GMT+09:00) Yakutsk");
    timezoneHash.put("Australia/Adelaide|(GMT+09:30)","(GMT+09:30) Adelaide");
    timezoneHash.put("Australia/Darwin|(GMT+09:30)","(GMT+09:30) Darwin");
    timezoneHash.put("Australia/Brisbane|(GMT+10:00)","(GMT+10:00) Brisbane");
    timezoneHash.put("Australia/Canberra|(GMT+10:00)","(GMT+10:00) Canberra, Melbourne, Sydney");
    timezoneHash.put("Australia/Hobart|(GMT+10:00)","(GMT+10:00) Hobart");
    timezoneHash.put("Pacific/Guam|(GMT+10:00)","(GMT+10:00) Guam, Port Moresby");
    timezoneHash.put("Asia/Vladivostok|(GMT+10:00)","(GMT+10:00) Vladivostok");
    timezoneHash.put("Asia/Magadan|(GMT+11:00)","(GMT+11:00) Magadan, Solomon Is., New Caledonia");
    timezoneHash.put("Pacific/Auckland|(GMT+12:00)","(GMT+12:00) Auckland, Wellington");
    timezoneHash.put("Pacific/Fiji|(GMT+12:00)","(GMT+12:00) Fiji, Kamchatka, Marshall Is.");
    timezoneHash.put("Pacific/Tongatapu|(GMT+13:00)","(GMT+13:00) Nuku'alofa");

    HashMap<String, String> statusFlagHash = new LinkedHashMap();

    statusFlagHash.put("isChargeback", "Chargeback");
    statusFlagHash.put("isFraud", "Fraud");
    statusFlagHash.put("isRefund", "Refund");
    statusFlagHash.put("isRollingReserveKept", "RollingReserveKept");
    statusFlagHash.put("isRollingReserveReleased", "RollingReserveReleased");
    statusFlagHash.put("isSettled", "Settled");
    statusFlagHash.put("isSuccessful", "Successful");

    HashMap<String, String> cardtype= new LinkedHashMap<>();

    cardtype.put("ACBANKS","ACBANKS");
    cardtype.put("ACH", "ACH");
    cardtype.put("AllPay88", "AllPay88");
    cardtype.put("AMC", "AMC");
    cardtype.put("AMEX", "AMEX");
    cardtype.put("APBANKS", "APBANKS");
    cardtype.put("ASTROPAY", "ASTROPAY");
    cardtype.put("AVISA", "AVISA");
    cardtype.put("AXIS", "AXIS");
    cardtype.put("BCPAYGATE", "BCPAYGATE");
    cardtype.put("BDBANKS", "BDBANKS");
    cardtype.put("BDWALLETS", "BDWALLETS");
    cardtype.put("BILLDESK", "BILLDESK");
    cardtype.put("BPBANKS", "BPBANKS");
    cardtype.put("BPWALLETS", "BPWALLETS");
    cardtype.put("BanglaBanks", "BanglaBanks");
    cardtype.put("BanglaWallets", "BanglaWallets");
    cardtype.put("CARDS", "CARDS");
    cardtype.put("CBZ Bank", "CBZ Bank");
    cardtype.put("CHK", "CHK");
    cardtype.put("CLEARSETTLE", "CLEARSETTLE");
    cardtype.put("CUP", "CUP");
    cardtype.put("CupUPI", "CupUPI");
    cardtype.put("DINER", "DINER");
    cardtype.put("DIRECTDEBIT", "DIRECTDEBIT");
    cardtype.put("DISC", "DISC");
    cardtype.put("DOKU", "DOKU");
    cardtype.put("DUSPAYDD", "DUSPAYDD");
    cardtype.put("DutchBanglaBank", "DutchBanglaBank");
    cardtype.put("eBanking", "eBanking");
    cardtype.put("eCheck", "eCheck");
    cardtype.put("Ecobank", "Ecobank");
    cardtype.put("ECOCASH", "ECOCASH");
    cardtype.put("Ecocash RTGS", "Ecocash RTGS");
    cardtype.put("Ecocash USD", "Ecocash USD");
    cardtype.put("ECOSPEND", "ECOSPEND");
    cardtype.put("ELEGRO", "ELEGRO");
    cardtype.put("EPAY", "EPAY");
    cardtype.put("EPBANKS", "EPBANKS");
    cardtype.put("EZPAY", "EZPAY");
    cardtype.put("FASTPAY", "FASTPAY");
    cardtype.put("First Bank of Nigeria", "First Bank of Nigeria");
    cardtype.put("FLUTTER", "FLUTTER");
    cardtype.put("FREECHARGE", "FREECHARGE");
    cardtype.put("Giftpay", "Giftpay");
    cardtype.put("GIROPAY", "GIROPAY");
    cardtype.put("GLOBALPAY", "GLOBALPAY");
    cardtype.put("GPAY", "GPAY");
    cardtype.put("GrameenBank", "GrameenBank");
    cardtype.put("HDFC", "HDFC");
    cardtype.put("HSBC", "HSBC");
    cardtype.put("IBBL", "IBBL");
    cardtype.put("ICICI", "ICICI");
    cardtype.put("Ideal", "Ideal");
    cardtype.put("IMBANKS", "IMBANKS");
    cardtype.put("INSTAPAYMENT", "INSTAPAYMENT");
    cardtype.put("INSTANTPAYMENT", "INSTANTPAYMENT");
    cardtype.put("InPay", "InPay");
    cardtype.put("JanataBank", "JanataBank");
    cardtype.put("JCB", "JCB");
    cardtype.put("JETON", "JETON");
    cardtype.put("JETONVOUCHER", "JETONVOUCHER");
    cardtype.put("JIOMONEY", "JIOMONEY");
    cardtype.put("JPBANK", "JPBANK");
    cardtype.put("KCP3D", "KCP3D");
    cardtype.put("KOTAK", "KOTAK");
    cardtype.put("LZPBANKS", "LZPBANKS");
    cardtype.put("LZPWALLETS", "LZPWALLETS");
    cardtype.put("MAESTRO", "MAESTRO");
    cardtype.put("MC", "MC");
    cardtype.put("Mobile Banking", "Mobile Banking");
    cardtype.put("MOBIKWIK", "MOBIKWIK");
    cardtype.put("MULTIBANCO", "MULTIBANCO");
    cardtype.put("MyMonedero", "MyMonedero");
    cardtype.put("MTN Momo", "MTN Momo");
    cardtype.put("NAGAD","NAGAD");
    cardtype.put("NEOSURF", "NEOSURF");
    cardtype.put("NETELLER", "NETELLER");
    cardtype.put("NMB", "NMB");
    cardtype.put("OneMoney RTGS", "OneMoney RTGS");
    cardtype.put("OneMoney USD", "OneMoney USD");
    cardtype.put("ONLINEPAY", "ONLINEPAY");
    cardtype.put("PAYBYLINK", "PAYBYLINK");
    cardtype.put("PAYG", "PAYG");
    cardtype.put("PAYSAFECARD", "PAYSAFECARD");
    cardtype.put("PAYTM", "PAYTM");
    cardtype.put("PAYZAPP", "PAYZAPP");
    cardtype.put("PERFECTMONEY", "PERFECTMONEY");
    cardtype.put("PGBANKS", "PGBANKS");
    cardtype.put("PUBANKS", "PUBANKS");
    cardtype.put("PURPLEPAY", "PURPLEPAY");
    cardtype.put("PayBoutique", "PayBoutique");
    cardtype.put("PayDunya", "PayDunya");
    cardtype.put("PaySec", "PaySec");
    cardtype.put("PaySend", "PaySend");
    cardtype.put("PromptpayQR", "PromptpayQR");
    cardtype.put("QIWI", "QIWI");
    cardtype.put("QKBANK", "QKBANK");
    cardtype.put("QPBANKS", "QPBANKS");
    cardtype.put("QR", "QR");
    cardtype.put("REDBOXX", "REDBOXX");
    cardtype.put("REPROPAY", "REPROPAY");
    cardtype.put("ROMCARD", "ROMCARD");
    cardtype.put("RTGS", "RTGS");
    cardtype.put("RUPAY", "RUPAY");
    cardtype.put("SAFEXPAY", "SAFEXPAY");
    cardtype.put("SBI", "SBI");
    cardtype.put("SCHEDULEDPAYMENT", "SCHEDULEDPAYMENT");
    cardtype.put("SEPAEXPRESS", "SEPAEXPRESS");
    cardtype.put("SEPBANKS", "SEPBANKS");
    cardtype.put("SEPWALLETS", "SEPWALLETS");
    cardtype.put("SecurePay", "SecurePay");
    cardtype.put("SendMoney To Account", "SendMoney To Account");
    cardtype.put("SKRILL", "SKRILL");
    cardtype.put("Sofort", "Sofort");
    cardtype.put("Soanli Bank", "Soanli Bank");
    cardtype.put("STANDINGORDERS", "STANDINGORDERS");
    cardtype.put("SWIFT", "SWIFT");
    cardtype.put("TAP","TAP");
    cardtype.put("TAPMIO", "TAPMIO");
    cardtype.put("TELECASH", "TELECASH");
    cardtype.put("TOJIKA", "TOJIKA");
    cardtype.put("TPAY", "TPAY");
    cardtype.put("Triple000", "Triple000");
    cardtype.put("TRUSTLY", "TRUSTLY");
    cardtype.put("TWD", "TWD");
    cardtype.put("UNICREDIT", "UNICREDIT");
    cardtype.put("UnionPay", "UnionPay");
    cardtype.put("UPAY","UPAY");
    cardtype.put("UPI", "UPI");
    cardtype.put("VISA", "VISA");
    cardtype.put("VOUCHERMONEY", "VOUCHERMONEY");
    cardtype.put("VPBANKS", "VPBANKS");
    cardtype.put("WEBMONEY", "WEBMONEY");
    cardtype.put("WechatPay", "WechatPay");
    cardtype.put("YANDEX", "YANDEX");
    cardtype.put("YESBANK", "YESBANK");
    cardtype.put("Zhixinfu", "Zhixinfu");
    cardtype.put("ZIPIT", "ZIPIT");
    cardtype.put("ZOTA", "ZOTA");


    HashMap<String, String> transactionModeHM = new LinkedHashMap();
    transactionModeHM.put("3Dv1","3Dv1");
    transactionModeHM.put("3Dv2","3Dv2");
    transactionModeHM.put("Non-3D","Non-3D");

    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (partner.isLoggedInPartner(session))
    {
        TransactionEntry transactionentry   = new TransactionEntry();
        SortedMap sortedMap                 = transactionentry.getSortedMap();
        String memberid                     = nullToStr(request.getParameter("memberid"));
        String pid                          = nullToStr(request.getParameter("pid"));
        String Config                       = null;
        String Roles                        = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
        if(Roles.contains("superpartner")){

        }else{
            pid     = String.valueOf(session.getAttribute("merchantid"));
            Config  = "disabled";
        }
        String remark       = request.getParameter("remark");
        String partnerid    = session.getAttribute("partnerId").toString();
        String paymentId    = null;
        String str          = "";
        String desc         = Functions.checkStringNull(request.getParameter("desc"))==null?"":request.getParameter("desc");
        String trackingid   = Functions.checkStringNull(request.getParameter("trackingid"))==null?"":request.getParameter("trackingid");
        String customerId   = Functions.checkStringNull(request.getParameter("customerid")) == null ? "" : request.getParameter("customerid");
        String firstName    = Functions.checkStringNull(request.getParameter("firstname"))== null ? "" : request.getParameter("firstname");
        String lastName     = Functions.checkStringNull(request.getParameter("lastname"))== null ? "" : request.getParameter("lastname");
        String emailAddress = Functions.checkStringNull(request.getParameter("emailaddr"))==null?"":request.getParameter("emailaddr");
        paymentId           = Functions.checkStringNull(request.getParameter("paymentid"))==null?"":request.getParameter("paymentid");
        String dateType     = Functions.checkStringNull(request.getParameter("datetype"));
        String cardpresent  = Functions.checkStringNull(request.getParameter("cardpresent"));
        String accountId    = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");
        String pgtypeId     = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");
        String issuingbank  = Functions.checkStringNull(request.getParameter("issuingbank"))==null?"":request.getParameter("issuingbank");
        String flaghash     = Functions.checkStringNull(request.getParameter("statusflag"));
        String timezone     = Functions.checkStringNull(request.getParameter("timezone"))==null?"":request.getParameter("timezone");
        String typeCard     = Functions.checkStringNull(request.getParameter("cardtype"))==null?"":request.getParameter("cardtype");
        String terminalid   = Functions.checkStringNull(request.getParameter("terminalid"))==null?"":request.getParameter("terminalid");
        String fdate        = null;
        String tdate        = null;
        Date date           = new Date();
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

        String Date     = originalFormat.format(date);
        date.setDate(1);
        String fromDate = originalFormat.format(date);

        fdate               = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDate : request.getParameter("fromdate");
        tdate               = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");
        String status       = Functions.checkStringNull(request.getParameter("status"));
        String startTime    = Functions.checkStringNull(request.getParameter("starttime"));
        String endTime      = Functions.checkStringNull(request.getParameter("endtime"));
        String transactionModeStr    = "";
        if(request.getAttribute("transactionMode") != null){
            transactionModeStr = (String) request.getAttribute("transactionMode");
        }else{
            transactionModeStr     = "";
        }

        if (startTime == null) startTime = "00:00:00";
        if (endTime == null) endTime = "23:59:59";
        Calendar rightNow = Calendar.getInstance();
        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        String currentyear = "" + rightNow.get(rightNow.YEAR);

        if (dateType == null) dateType = "";
        if (dateType != null) str = str + "&datetype=" + dateType;
        if (cardpresent != null) str = str + "&cardpresent=" + cardpresent;
        if (fdate != null) str = str + "&fromdate=" + fdate;
        if (tdate != null) str = str + "&todate=" + tdate;
        if (startTime != null) str = str + "&starttime=" + startTime;
        if (endTime != null) str = str + "&endtime=" + endTime;
        if (desc != null) str = str + "&desc=" + desc;
        if (memberid != null) str = str + "&memberid=" + memberid;
        if (trackingid != null) str = str + "&trackingid=" + trackingid;
        if (accountId != null) str = str + "&accountid=" + accountId;
        if (pgtypeId != null) str = str + "&pgtypeid=" + pgtypeId;
        if (customerId != null) str = str + "&customerid=" + customerId;
        if (firstName != null) str = str + "&firstname=" + firstName;
        if (lastName != null) str = str + "&lastname=" + lastName;
        if (emailAddress != null) str = str + "&emailaddr=" + emailAddress;
        if (issuingbank != null) str = str + "&issuingbank=" + issuingbank;
        if (timezone != null) str = str + "&timezone=" + timezone;
        if (typeCard != null)str = str + "&cardtype=" +typeCard;
        if (terminalid != null)str = str + "&terminalid=" + terminalid;
        if (pid != null) str = str + "&pid=" + pid;
        if (transactionModeStr != null) str = str + "&transactionMode=" + transactionModeStr;

        str = str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        boolean archive = Boolean.valueOf(request.getParameter("archive")).booleanValue();
        str = str + "&archive=" + archive;
        String archivalString = "Archived";
        String currentString = "Current";
        if(status != null)str =str + "&status=" + status;
        else
            status  ="";
        str     = str+"&statusflag="+request.getParameter("statusflag");
        String selectedArchived = "", selectedCurrent = "";
        if (archive)
        {
            selectedArchived    = "selected";
            selectedCurrent     = "";
        }
        else
        {
            selectedArchived    = "";
            selectedCurrent     = "selected";
        }
        int pageno          = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords     = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),30);
        str                 = str + "&SRecords=" + pagerecords;

        ResourceBundle rb1          = null;
        String language_property1   = (String)session.getAttribute("language_property");
        rb1                         = LoadProperties.getProperty(language_property1);
        String partnerTransactions_Merchant         = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Merchant")) ? rb1.getString("partnerTransactions_Merchant") : "Merchant";
        String partnerTransactions_Transactions     = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Transactions")) ? rb1.getString("partnerTransactions_Transactions") : "Transaction";
        String partnerTransactions_From             = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_From")) ? rb1.getString("partnerTransactions_From") : "From";
        String partnerTransactions_To               = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_To")) ? rb1.getString("partnerTransactions_To") : "To";
        String partnerTransactions_Partner_ID       = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Partner_ID")) ? rb1.getString("partnerTransactions_Partner_ID") : "Partner ID";
        String partnerTransactions_Merchant_ID      = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Merchant_ID")) ? rb1.getString("partnerTransactions_Merchant_ID") : "Merchant ID";
        String partnerTransactions_Tracking_ID      = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Tracking_ID")) ? rb1.getString("partnerTransactions_Tracking_ID") : "Tracking ID";
        String partnerTransactions_Order_Id         = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Order_Id")) ? rb1.getString("partnerTransactions_Order_Id") : "Order Id";
        String partnerTransactions_Customer_Id      = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Customer_Id")) ? rb1.getString("partnerTransactions_Customer_Id") : "Customer Id";
        String partnerTransactions_First_Name       = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_First_Name")) ? rb1.getString("partnerTransactions_First_Name") : "First Name";
        String partnerTransactions_Last_Name        = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Last_Name")) ? rb1.getString("partnerTransactions_Last_Name") : "Last Name";
        String partnerTransactions_Data_Source      = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Data_Source")) ? rb1.getString("partnerTransactions_Data_Source") : "Data Source";
        String partnerTransactions_Archives         = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Archives")) ? rb1.getString("partnerTransactions_Archives") : "Archives";
        String partnerTransactions_Current          = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Current")) ? rb1.getString("partnerTransactions_Current") : "Current";
        String partnerTransactions_Email_ID         = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Email_ID")) ? rb1.getString("partnerTransactions_Email_ID") : "Email ID";
        String partnerTransactions_RowsPages        = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_RowsPages")) ? rb1.getString("partnerTransactions_RowsPages") : "Rows/Pages";
        String partnerTransactions_Status           = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Status")) ? rb1.getString("partnerTransactions_Status") : "Status";
        String partnerTransactions_All              = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_All")) ? rb1.getString("partnerTransactions_All") : "All";
        String partnerTransactions_Issuing_Bank     = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Issuing_Bank")) ? rb1.getString("partnerTransactions_Issuing_Bank") : "Issuing Bank";
        String partnerTransactions_Acquirer_Bank    = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Acquirer_Bank")) ? rb1.getString("partnerTransactions_Acquirer_Bank") : "Acquirer Bank";
        String partnerTransactions_Acquirer_Account_ID = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Acquirer_Account_ID")) ? rb1.getString("partnerTransactions_Acquirer_Account_ID") : "Acquirer Account ID";
        String partnerTransactions_Date_Type            = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Date_Type")) ? rb1.getString("partnerTransactions_Date_Type") : "Date Type";
        String partnerTransactions_Transaction_Date     = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Transaction_Date")) ? rb1.getString("partnerTransactions_Transaction_Date") : "Transaction Date";
        String partnerTransactions_Last_Updated_Date    = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Last_Updated_Date")) ? rb1.getString("partnerTransactions_Last_Updated_Date") : "Last Updated Date";
        String partnerTransactions_Status_Flag          = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Status_Flag")) ? rb1.getString("partnerTransactions_Status_Flag") : "Status Flag";
        String partnerTransactions_All1                 = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_All1")) ? rb1.getString("partnerTransactions_All1") : "All";
        String partnerTransactions_Time_Zone            = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Time_Zone")) ? rb1.getString("partnerTransactions_Time_Zone") : "Time Zone";
        String partnerTransactions_Select_Time_Zone     = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Select_Time_Zone")) ? rb1.getString("partnerTransactions_Select_Time_Zone") : "--Select Time Zone--";
        String partnerTransactions_Card_Present         = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Card_Present")) ? rb1.getString("partnerTransactions_Card_Present") : "Card Present";
        String partnerTransactions_Yes                  = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Yes")) ? rb1.getString("partnerTransactions_Yes") : "Yes";
        String partnerTransactions_No                   = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_No")) ? rb1.getString("partnerTransactions_No") : "No";
        String partnerTransactions_Search               = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Search")) ? rb1.getString("partnerTransactions_Search") : "Search";
        String partnerTransactions_Report_Table         = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Report_Table")) ? rb1.getString("partnerTransactions_Report_Table") : "Report Table";
        String partnerTransactions_Transaction_Date1    = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Transaction_Date1")) ? rb1.getString("partnerTransactions_Transaction_Date1") : "Transaction Date";
        String partnerTransactions_Transaction_fetched  = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Transaction_fetched")) ? rb1.getString("partnerTransactions_Transaction_fetched") : "Transaction Fetched Date";
        String partnerTransactions_TrackingID           = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_TrackingID")) ? rb1.getString("partnerTransactions_TrackingID") : "Tracking ID";
        String partnerTransactions_PaymentID            = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_PaymentID")) ? rb1.getString("partnerTransactions_PaymentID") : "Payment ID";
        String partnerTransactions_PartnerID            = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_PartnerID")) ? rb1.getString("partnerTransactions_PartnerID") : "Partner ID";
        String partnerTransactions_MerchantID           = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_MerchantID")) ? rb1.getString("partnerTransactions_MerchantID") : "Merchant ID";
        String partnerTransactions_OrderID              = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_OrderID")) ? rb1.getString("partnerTransactions_OrderID") : "Order ID";
        String partnerTransactions_OrderDescription     = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_OrderDescription")) ? rb1.getString("partnerTransactions_OrderDescription") : "Order Description";
        String partnerTransactions_Card_Holder_Name     = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Card_Holder_Name")) ? rb1.getString("partnerTransactions_Card_Holder_Name") : "Card Holder's Name";
        String partnerTransactions_Customer_Email       = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Customer_Email")) ? rb1.getString("partnerTransactions_Customer_Email") : "Customer Email";
        String partnerTransactions_CustomerID           = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_CustomerID")) ? rb1.getString("partnerTransactions_CustomerID") : "Customer ID";
        String partnerTransactions_PaymentMode          = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_PaymentMode")) ? rb1.getString("partnerTransactions_PaymentMode") : "Payment Mode";
        String partnerTransactions_PaymentBrand         = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_PaymentBrand")) ? rb1.getString("partnerTransactions_PaymentBrand") : "Payment Brand";
        String partnerTransactions_Amount               = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Amount")) ? rb1.getString("partnerTransactions_Amount") : "Amount";
        String partnerTransactions_RefundAmount         = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_RefundAmount")) ? rb1.getString("partnerTransactions_RefundAmount") : "Refund Amount";
        String partnerTransactions_Currency             = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Currency")) ? rb1.getString("partnerTransactions_Currency") : "Currency";
        String partnerTransactions_Status1              = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Status1")) ? rb1.getString("partnerTransactions_Status1") : "Status";
        String partnerTransactions_Remark               = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Remark")) ? rb1.getString("partnerTransactions_Remark") : "Remark";
        String partnerTransactions_TerminalID           = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_TerminalID")) ? rb1.getString("partnerTransactions_TerminalID") : "Terminal ID";
        String partnerTransactions_LastUpdateDate       = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_LastUpdateDate")) ? rb1.getString("partnerTransactions_LastUpdateDate") : "Last Update Date";
        String partnerTransactions_Showing_Page         = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Showing_Page")) ? rb1.getString("partnerTransactions_Showing_Page") : "Showing Page";
        String partnerTransactions_of                   = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_of")) ? rb1.getString("partnerTransactions_of") : "of";
        String partnerTransactions_records              = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_records")) ? rb1.getString("partnerTransactions_records") : "records";
        String partnerTransactions_Sorry                = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Sorry")) ? rb1.getString("partnerTransactions_Sorry") : "Sorry";
        String partnerTransactions_No_records_found     = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_No_records_found")) ? rb1.getString("partnerTransactions_No_records_found") : "No records found.";
        String partnerTransactions_page_no              = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_page_no")) ? rb1.getString("partnerTransactions_page_no") : "Page number";
        String partnerTransactions_total_no_of_records  = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_total_no_of_records")) ? rb1.getString("partnerTransactions_total_no_of_records") : "Total number of records";
        String transactions_mode                        = StringUtils.isNotEmpty(rb1.getString("transactions_mode")) ? rb1.getString("transactions_mode") : "Transaction Mode";


%>
<style type="text/css">
    .table#myTable > thead > tr > th {font-weight: inherit;text-align: center;}

    .hide_label{
        color: transparent;
        user-select: none;
    }
    .table-condensed a.btn{
        padding: 0;
    }

    .table-condensed .separator{
        padding-left: 0;
        padding-right: 0;
    }
    #ui-id-2
    {
        overflow: auto;
        max-height: 350px;
    }

</style>
<style type="text/css">
    #grpChkBox{
        z-index: 8;
        border-radius: 2px;
        -webkit-border-radius: 2px;
        padding: 15px;
        box-shadow: 0 2px 6px rgba(0,0,0,0.1);
        overflow: auto;
        height: 260px;
        width: 200px;
    }
</style>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">

    <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>--%>
    <%--<script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
    <script type="text/javascript" language="JavaScript" src="/partner/javascript/autocomplete_partner_memberid.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>

    <%--FontAwesome CDN--%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css" integrity="sha512-KfkfwYDsLkIlwQp6LFnl8zNdLGxu9YAA1QvwINks4PhcElQSvqcyVLLD9aMhXd13uQjoXtEKNosOWaZqXgel0g==" crossorigin="anonymous" referrerpolicy="no-referrer" />


    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <script src="/merchant/datepicker/datetimepicker/moment-with-locales.js"></script>
    <script src="/merchant/datepicker/datetimepicker/bootstrap-datetimepicker.min.js"></script>
    <script src="/partner/transactionCSS/js/transactions.js"></script>

    <title><%=company%> Transaction Management> Merchant Transactions</title>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
    </script>
    <script type="text/javascript">

        $('#sandbox-container input').datepicker({
        });
    </script>
    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
        });
    </script>
    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
        });

        $(function() {
            $('#datetimepicker12').datetimepicker({
                format: 'HH:mm:ss',
                useCurrent: true
            });
        });

        $(function() {
            $('#datetimepicker13').datetimepicker({
                format: 'HH:mm:ss',
                useCurrent: true
            });
        });
    </script>

    <script>

        $(document).ready(function(){

            var w = $(window).width();

            //alert(w);

            if(w > 990){
                //alert("It's greater than 990px");
                $("body").removeClass("smallscreen").addClass("widescreen");
                $("#wrapper").removeClass("enlarged");
            }
            else{
                //alert("It's less than 990px");
                $("body").removeClass("widescreen").addClass("smallscreen");
                $("#wrapper").addClass("enlarged");
                $(".left ul").removeAttr("style");
            }
        });
        function copyToClipboard(element) {
            var $temp = $("<input>");
            $("body").append($temp);
            $temp.val(element).select();
            document.execCommand("copy");
            $temp.remove();
        }

    </script>
</head>
<body>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <form name="form" method="post" action="/partner/net/PartnerTransaction?ctoken=<%=ctoken%>">
                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">
                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%> <%=partnerTransactions_Merchant%> <%=archive ? archivalString : currentString%><%=partnerTransactions_Transactions%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                            <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                            <input type="hidden" value="<%=company%>" name="partnername" id="partnername">
                            <%
                                Functions functions = new Functions();
                                String error        = (String ) request.getAttribute("error");
                                String dateRangeIsValid        = (String ) request.getAttribute("dateRangeIsValid");
                                if(functions.isValueNull(error)||functions.isValueNull(dateRangeIsValid))
                                {
                                    System.out.println("indise first if");

                                    if(functions.isValueNull(error))
                                    {
                                        System.out.println("second if ");
                                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error +"</h5>");
                                    }
                                    if(functions.isValueNull(dateRangeIsValid))
                                    {
                                        System.out.println("third if");
                                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + dateRangeIsValid +"</h5>");
                                    }
                                }
                            %>
                            <%--<div class="widget-content padding">
                                <div id="horizontal-form">
                                    <div class="form-group col-md-4 has-feedback">
                                        <label >From</label>
                                        <input type="text" size="16" name="fromdate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label>To</label>
                                        <input type="text" size="16" name="todate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label>Status</label>
                                        <select size="1" name="status" class="form-control">
                                            <option value="">All</option>
                                            <%
                                                if(sortedMap.size()>0)
                                                {
                                                    Set<String> setStatus = sortedMap.entrySet();
                                                    Iterator i = setStatus.iterator();
                                                    while(i.hasNext())
                                                    {
                                                        Map.Entry entryMap =(Map.Entry) i.next();
                                                        String status1 = (String) entryMap.getKey();
                                                        String status2 =(String) entryMap.getValue();

                                                        String select = "";
                                                        if(status.equalsIgnoreCase(status1))
                                                        {
                                                            select = "selected";
                                                        }
                                            %>
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(status1)%>" <%=select%>>
                                                <%=ESAPI.encoder().encodeForHTML(status2)%>
                                            </option>
                                            <%
                                                    }
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label >Description</label>
                                        <input type=text name="desc" maxlength="100" maxlength="100"  class="form-control" size="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>">
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <div class="ui-widget">
                                            <label for="mid">Merchant ID</label>
                                            <input name="memberid" id="mid" value="<%=memberid%>" class="form-control" autocomplete="on">
                                        </div>
                                        </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label>Data Source</label>
                                        <select size="1" name="archive" class="form-control">
                                            <option value="true" <%=ESAPI.encoder().encodeForHTMLAttribute(selectedArchived)%>>Archives</option>
                                            <option value="false" <%=ESAPI.encoder().encodeForHTMLAttribute(selectedCurrent)%>>Current</option>
                                        </select>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label >Tracking ID</label>
                                        <input type=text name="trackingid" maxlength="100" maxlength="100" class="form-control" size="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>">
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label >Date Type</label>
                                        <select size="1" name="datetype" class="form-control">
                                            <%
                                                if("TIMESTAMP".equals(dateType))
                                                {%>
                                            <option value="DTSTAMP">Transaction Date</option>
                                            <option value="TIMESTAMP" SELECTED>Last Updated Date</option>
                                            <%}
                                            else
                                            {%>
                                            <option value="DTSTAMP" SELECTED>Transaction Date</option>
                                            <option value="TIMESTAMP">Last Updated Date</option>
                                            <%}
                                            %>
                                        </select>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label >Rows/page</label>
                                        <input type="text" maxlength="5"  value="<%=pagerecords%>" name="SRecords" size="2" class="form-control">
                                    </div>
                                    <div class="form-group col-md-4"></div>
                                    <div class="form-group col-md-4"></div>

                                    <div class="form-group col-md-4">
                                        <label style="color: transparent;">Path</label>
                                        <button type="submit" class="btn btn-default" name="B1" style="display:block;">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </div>
                                </div>
                            </div>--%>
                            <div class="widget-content padding">
                                <div id="horizontal-form">

                                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-4">

                                        <div class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                                            <label><%=partnerTransactions_From%></label>
                                            <input type="text" name="fromdate" id="From_dt" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">
                                        </div>


                                        <div id="From_div" class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-4" style="padding: 0;/*width: inherit;*/">
                                            <div class="form-group">
                                                <label class="hide_label"><%=partnerTransactions_From%></label>
                                                <div class='input-group date' >
                                                    <input type='text' id='datetimepicker12' class="form-control"  placeholder="HH:MM:SS" name="starttime" maxlength="8" value="<%=startTime%>" style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                                                    <%--<div id="datetimepicker12"></div>--%>
                                                </div>
                                            </div>
                                        </div>
                                    </div>


                                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-4">

                                        <div class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                                            <label><%=partnerTransactions_To%></label>
                                            <input type="text" name="todate" id="To_dt" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">

                                        </div>


                                        <div id="From_div" class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-4" style="padding: 0;/*width: inherit;*/">
                                            <div class="form-group">
                                                <label class="hide_label"><%=partnerTransactions_To%></label>
                                                <div class='input-group date' >
                                                    <input type='text' id='datetimepicker13' class="form-control" placeholder="HH:MM:SS" name="endtime" maxlength="8" value="<%=endTime%>" style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                                                </div>
                                            </div>
                                        </div>

                                    </div>

                                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-2 has-feedback">
                                        <label ><%=partnerTransactions_Partner_ID%></label>
                                        <input type=text name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                                        <input type=hidden name="pid" value="<%=pid%>" >
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-2 has-feedback">
                                        <label for="member"><%=partnerTransactions_Merchant_ID%></label>
                                        <input name="memberid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                                    </div>

                                    <%--<div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-2 has-feedback" &lt;%&ndash;style="margin-left: 96px;"&ndash;%&gt;>
                                        <label>Terminal ID</label>
                                        &lt;%&ndash;<select size="1" name="terminalid" class="form-control">
                                            <%
                                                terminalBuffer.append("(");
                                                TerminalManager terminalManager=new TerminalManager();
                                                List<TerminalVO> terminalVOList=terminalManager.getMemberandUserTerminalList(uId, String.valueOf(session.getAttribute("role")));
                                                if(terminalVOList.size()>0)
                                                {
                                            %>
                                            <option value="all">All</option>
                                            <%
                                                for (TerminalVO terminalVO:terminalVOList)
                                                {
                                                    String str1 = "";
                                                    String active = "";
                                                    if (terminalVO.getIsActive().equalsIgnoreCase("Y"))
                                                    {
                                                        active = "Active";
                                                    }
                                                    else
                                                    {
                                                        active = "InActive";
                                                    }
                                                    if(terminalid.equals(terminalVO.getTerminalId()))
                                                    {
                                                        str1= "selected";
                                                    }
                                                    if(terminalBuffer.length()!=0 && terminalBuffer.length()!=1)
                                                    {
                                                        terminalBuffer.append(",");
                                                    }
                                                    terminalBuffer.append(terminalVO.getTerminalId());
                                            %>
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(terminalVO.getTerminalId())%>" <%=str1%>> <%=ESAPI.encoder().encodeForHTML(terminalVO.getTerminalId()+"-"+terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+terminalVO.getCurrency()+"-"+active)%> </option>
                                            <%
                                                }
                                                terminalBuffer.append(")");
                                            }
                                            else
                                            {
                                            %>
                                            <option value="NoTerminals">No Terminals Allocated</option>
                                            <%
                                                }
                                            %>
                                        </select>&ndash;%&gt;
                                    </div>--%>

                                    <div class="form-group col-xs-12 col-md-12 has-feedback" style="margin:0; padding: 0;"></div>
                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label ><%=partnerTransactions_Tracking_ID%></label>
                                        <input type=text name="trackingid"   class="form-control" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>" >
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=partnerTransactions_Order_Id%></label>
                                        <input type=text name="desc" maxlength="100"  class="form-control"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>">
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=partnerTransactions_Customer_Id%></label>
                                        <input type=text name="customerid" maxlength="100"  class="form-control"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(customerId)%>">
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label ><%=partnerTransactions_First_Name%></label>
                                        <input type=text name="firstname" maxlength="100"  class="form-control"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(firstName)%>">
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=partnerTransactions_Last_Name%></label>
                                        <input type=text name="lastname" maxlength="100"  class="form-control"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(lastName)%>">
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=partnerTransactions_Data_Source%></label>
                                        <select size="1" name="archive" class="form-control" >
                                            <option value="true" <%=selectedArchived%>><%=partnerTransactions_Archives%></option>
                                            <option value="false" <%=selectedCurrent%>><%=partnerTransactions_Current%></option>
                                        </select>
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=partnerTransactions_Email_ID%></label>
                                        <input type="text"   class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(emailAddress)%>" name="emailaddr">
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label ><%=partnerTransactions_RowsPages%></label>
                                        <input type="text" maxlength="3"  size="15" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(new Integer(pagerecords).toString())%>" name="SRecords" size="2" class="textBoxes" />
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label ><%=partnerTransactions_Status%></label>
                                        <select size="1" name="status"  class="form-control" >
                                            <option value=""><%=partnerTransactions_All%></option>
                                            <%
                                                if(sortedMap.size()>0)
                                                {
                                                    Set<String> setStatus   = sortedMap.entrySet();
                                                    Iterator i              = setStatus.iterator();
                                                    while(i.hasNext())
                                                    {
                                                        Map.Entry entryMap  = (Map.Entry) i.next();
                                                        String statusKey    = (String) entryMap.getKey();
                                                        String status2      = (String) entryMap.getValue();
                                                        String select       = "";
                                                        if(statusKey.equalsIgnoreCase(status))
                                                        {
                                                            select = "selected";
                                                        }
                                            %>
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(statusKey)%>" <%=select%>>
                                                <%=ESAPI.encoder().encodeForHTML(status2)%>
                                            </option>
                                            <%
                                                    }
                                                }
                                            %>
                                        </select>
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=partnerTransactions_Issuing_Bank%></label>
                                        <input name="issuingbank" id="ibank" value="<%=ESAPI.encoder().encodeForHTMLAttribute(issuingbank)%>" class="form-control" autocomplete="on">
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=partnerTransactions_Acquirer_Bank%></label>
                                        <input name="pgtypeid" id="gateway" value="<%=ESAPI.encoder().encodeForHTMLAttribute(pgtypeId)%>" class="form-control" autocomplete="on">
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=partnerTransactions_Acquirer_Account_ID%></label>
                                        <input name="accountid" id="accountid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(accountId)%>" class="form-control" autocomplete="on">
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label ><%=partnerTransactions_Date_Type%></label>
                                        <select size="1" name="datetype" class="form-control">
                                            <%
                                                if("TIMESTAMP".equals(dateType))
                                                {%>
                                            <option value="DTSTAMP"><%=partnerTransactions_Transaction_Date%></option>
                                            <option value="TIMESTAMP" SELECTED><%=partnerTransactions_Last_Updated_Date%></option>
                                            <%}
                                            else
                                            {%>
                                            <option value="DTSTAMP" SELECTED><%=partnerTransactions_Transaction_Date%></option>
                                            <option value="TIMESTAMP"><%=partnerTransactions_Last_Updated_Date%></option>
                                            <%}
                                            %>
                                        </select>
                                    </div>
                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=partnerTransactions_Status_Flag%></label>
                                        <select size="1" name="statusflag" class="form-control">
                                            <option value="all"><%=partnerTransactions_All1%></option>
                                            <%
                                                Set statusflagSet   = statusFlagHash.keySet();
                                                Iterator it         = statusflagSet.iterator();
                                                String selected3    = "";
                                                String key3         = "";
                                                String value3       = "";
                                                while (it.hasNext())
                                                {
                                                    key3    = (String) it.next();
                                                    value3  = statusFlagHash.get(key3);

                                                    if (key3.equals(flaghash))
                                                        selected3 = "selected";
                                                    else
                                                        selected3 = "";

                                            %>
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key3)%>" <%=selected3%>><%=ESAPI.encoder().encodeForHTML(value3)%>
                                            </option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=partnerTransactions_Time_Zone%></label>
                                        <select size="1"  name="timezone" class="form-control">
                                            <option value=""><%=partnerTransactions_Select_Time_Zone%></option>
                                            <%

                                                /*if (functions.isEmptyOrNull(timezone))
                                                {
                                                    timezone = merchants1.getTimeZone((String) session.getAttribute("merchantid"));
                                                }*/
                                                Set timezoneSet         = timezoneHash.keySet();
                                                Iterator itr            = timezoneSet.iterator();
                                                String selected4        = "";
                                                String timezonekey      = "";
                                                String timezonevalue    = "";
                                                while (itr.hasNext())
                                                {
                                                    timezonekey     = (String) itr.next();
                                                    timezonevalue   = timezoneHash.get(timezonekey);

                                                    if (timezonekey.equals(timezone))
                                                        selected4 = "selected";
                                                    else
                                                        selected4 = "";

                                            %>
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(timezonekey)%>" <%=selected4%>><%=ESAPI.encoder().encodeForHTML(timezonevalue)%>
                                            </option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                   <%-- <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label ><%=partnerTransactions_Card_Present%></label>
                                        <select size="1" name="cardpresent" class="form-control">
                                            <%
                                                if("Y".equals(cardpresent))
                                                {%>
                                            <option value="Y" SELECTED><%=partnerTransactions_Yes%></option>
                                            <option value="N"><%=partnerTransactions_No%></option>
                                            <%}
                                            else
                                            {%>
                                            <option value="N" SELECTED><%=partnerTransactions_No%></option>
                                            <option value="Y"><%=partnerTransactions_Yes%></option>
                                            <%}
                                            %>
                                        </select>
                                    </div>--%>
                                    <input type="hidden" name="cardpresent" value="N">

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label>Card Types: </label>
                                        <select size="1" name="cardtype" class="form-control">
                                            <option value="">All</option>

                                            <%
                                                Set cardType        = cardtype.keySet();
                                                Iterator it1        = cardType.iterator();
                                                String selected5    = "";
                                                String key5         = "";
                                                String value5       = "";
                                                while(it1.hasNext())
                                                {
                                                    key5    = (String)it1.next();
                                                    value5  = cardtype.get(key5);
                                                    if (key5.equals(typeCard))
                                                        selected5   = "selected";
                                                    else
                                                        selected5   = "";

                                            %>
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key5)%>"
                                                    <%=selected5%>><%=ESAPI.encoder().encodeForHTML(value5)%>
                                            </option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label>Terminal Id</label>
                                        <input name="terminalid" id="terminal" value="<%=terminalid%>" class="form-control" autocomplete="on">
                                    </div>
                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label>Transaction Mode: </label>
                                        <select size="1" name="transactionMode" class="form-control">
                                            <option value="">All</option>
                                            <%

                                                for(String trnsModeKey : transactionModeHM.keySet())
                                                {
                                                    String modStr = transactionModeHM.get(trnsModeKey);
                                                    if(modStr.equalsIgnoreCase(transactionModeStr)){
                                            %>
                                                    <option value="<%=modStr%>" SELECTED  ><%=modStr%></option>
                                            <%
                                                    }else{ %>
                                                    <option value="<%=modStr%>" ><%=modStr%></option>
                                            <%      }
                                                }
                                            %>
                                         </select>
                                    </div>

                                    <div class="form-group col-xs-3 col-sm-3 col-md-3 has-feedback">
                                        <label >&nbsp;</label>
                                        <button type="submit" name="B1" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=partnerTransactions_Search%></button>
                                    </div>
                                    <%-- <input type="hidden" value="<%=terminalBuffer.toString()%>" name="terminalbuffer">--%>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </form>

            <%--Start Report Data--%>
            <div class="row reporttable">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerTransactions_Report_Table%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding"<%-- style="overflow-x: auto;"--%>>
                            <%  String errormsg1 = (String)request.getAttribute("message");
                                if (errormsg1 == null)
                                {
                                    errormsg1 = "";
                                }
                                else
                                {
                                    out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" >");
                                    out.println(errormsg1);
                                    out.println("</font></td></tr></table>");
                                }
                            %>
                            <%
                                HashMap hash            = (HashMap) request.getAttribute("transactionsdetails");
                                HashMap trackinghash    = (HashMap) request.getAttribute("TrackingIDList1");
                                String amountcount    = (String) request.getAttribute("amountcount");
                                HashMap temphash        = null, temphash1=null;
                                List<String> trackingList = new ArrayList<String>();

                                str                 = str + "&trackingid=" +trackingid;
                                str                 = str + "&memberid=" +memberid;
                                str                 = str + "&customerid=" +customerId;
                                int records         = 0,trackingRecords=0;
                                int totalrecords    = 0;
                                int currentblock    = 1;
                                try
                                {
                                    records         = Integer.parseInt((String) hash.get("records"));
                                    //trackingRecords = Integer.parseInt((String) trackinghash.get("records"));
                                    // System.out.println("trackingRecords::: "+ trackingRecords);
                                    totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
                                    currentblock = Integer.parseInt((String) hash.get("currentblock"));
                                }
                                catch (Exception ex)
                                {
                                }

                                if(totalrecords > 0)
                                {
                                    for (int pos = 1; pos <= totalrecords; pos++)
                                    {
                                        String id = Integer.toString(pos);

                                        temphash1           = (HashMap) trackinghash.get(id);
                                        String icicitransid = (String)temphash1.get("transid");
                                        trackingList.add(icicitransid);
                                    }
                                }

                                String style = "class=tr0";
                                String ext = "light";
                                String style1 = "class=\"textb\"";
                                if (records > 0)
                                {
                            %>

                            <div id="showingid"><strong>Note: Click on the Tracking ID to see the Transaction Details</strong></div>
                            <div id="showingid"><strong>Sum Amount:  <%=amountcount%> </strong></div>
                            <%
                                Map<String,Object> merchantTemplateSetting  = null;
                                if(request.getAttribute("merchantTemplateSetting") != null)
                                {
                                    merchantTemplateSetting = (Map<String, Object>) request.getAttribute("merchantTemplateSetting");
                            %>

                            <div class="pull-right">
                                <div class="btn-group">
                                    <form action="/partner/net/SetColumnConfig?ctoken=<%=ctoken%>" method="post">
                                        <ul class="nav navbar-nav navbar-right" id="configbtn_ul" style="margin-bottom: 10px;">
                                            <ul class="nav navbar-nav">
                                                <li class="dropdown btn-default" style="color: white"><a class="dropdown-toggle" data-toggle="dropdown" href="#" id="dropdown_btn" style="color: white;"><i class="fa fa-cogs" style="color: white; font-weight: 600;"></i>&nbsp;&nbsp;Select The Fields&nbsp;&nbsp;<span class="caret" style="color: white;"></span></a>
                                                    <ul id="grpChkBox" class="dropdown-menu">
                                                        <p style="font-weight: 700; color: #000000"> Please save your selection</p>
                                                        <li style="color: #000000"><valign><b><input type="checkbox" onclick="ToggleAll(this);" name="alltrans" name="id"></b>&nbsp;&nbsp;Select all&nbsp;&nbsp;</valign></li>
                                                        <br>
                                                        <li style="color: #000000"><input type="checkbox" class="id" id="partnerTransactions_Transaction_Date1" name='<%=TemplatePreference.partnerTransactions_Transaction_Date1.toString()%>' value="<%=(merchantTemplateSetting!= null && merchantTemplateSetting.containsKey(TemplatePreference.partnerTransactions_Transaction_Date1.toString()))?merchantTemplateSetting.get(TemplatePreference.partnerTransactions_Transaction_Date1.toString()):""%>"/>Transaction Date</li>
                                                        <input type="hidden" class="id_1" id="partnerTransactions_Transaction_Date1_hidden" name='<%=TemplatePreference.partnerTransactions_Transaction_Date1.toString()%>' value>
                                                        <br>
                                                        <li style="color: #000000"><input type="checkbox" class="id" id="partnerTransactions_TimeZone" name='<%=TemplatePreference.partnerTransactions_TimeZone.toString()%>' value="<%=(merchantTemplateSetting!= null && merchantTemplateSetting.containsKey(TemplatePreference.partnerTransactions_TimeZone.toString()))?merchantTemplateSetting.get(TemplatePreference.partnerTransactions_TimeZone.toString()):""%>"/>TimeZone</li>
                                                        <input type="hidden" class="id_1" id="partnerTransactions_TimeZone_hidden" name='<%=TemplatePreference.partnerTransactions_TimeZone.toString()%>' value>
                                                        <br>
                                                        <li style="color: #000000"><input type="checkbox" class="id" id="partnerTransactions_TrackingID" name='<%=TemplatePreference.partnerTransactions_TrackingID.toString()%>' value="<%=(merchantTemplateSetting!= null && merchantTemplateSetting.containsKey(TemplatePreference.partnerTransactions_TrackingID.toString()))?merchantTemplateSetting.get(TemplatePreference.partnerTransactions_TrackingID.toString()):""%>"/>Tracking ID</li>
                                                        <input type="hidden" class="id_1" id="partnerTransactions_TrackingID_hidden" name='<%=TemplatePreference.partnerTransactions_TrackingID.toString()%>' value>
                                                        <br>
                                                        <li style="color: #000000"><input type="checkbox" class="id" id="partnerTransactions_PaymentID" name='<%=TemplatePreference.partnerTransactions_PaymentID.toString()%>' value="<%=(merchantTemplateSetting!= null && merchantTemplateSetting.containsKey(TemplatePreference.partnerTransactions_PaymentID.toString()))?merchantTemplateSetting.get(TemplatePreference.partnerTransactions_PaymentID.toString()):""%>"/>Payment ID</li>
                                                        <input type="hidden" class="id_1" id="partnerTransactions_PaymentID_hidden" name='<%=TemplatePreference.partnerTransactions_PaymentID.toString()%>' value>
                                                        <br>
                                                        <li style="color: #000000"><input type="checkbox" class="id" id="partnerTransactions_PartnerID" name='<%=TemplatePreference.partnerTransactions_PartnerID.toString()%>' value="<%=(merchantTemplateSetting!= null && merchantTemplateSetting.containsKey(TemplatePreference.partnerTransactions_PartnerID.toString()))?merchantTemplateSetting.get(TemplatePreference.partnerTransactions_PartnerID.toString()):""%>"/>Partner ID</li>
                                                        <input type="hidden" class="id_1" id="partnerTransactions_PartnerID_hidden" name='<%=TemplatePreference.partnerTransactions_PartnerID.toString()%>' value>
                                                        <br>
                                                        <li style="color: #000000"><input type="checkbox" class="id" id="partnerTransactions_MerchantID" name='<%=TemplatePreference.partnerTransactions_MerchantID.toString()%>' value="<%=(merchantTemplateSetting!= null && merchantTemplateSetting.containsKey(TemplatePreference.partnerTransactions_MerchantID.toString()))?merchantTemplateSetting.get(TemplatePreference.partnerTransactions_MerchantID.toString()):""%>"/>Merchant ID</li>
                                                        <input type="hidden" class="id_1" id="partnerTransactions_MerchantID_hidden" name='<%=TemplatePreference.partnerTransactions_MerchantID.toString()%>' value>
                                                        <br>
                                                        <li style="color: #000000"><input type="checkbox" class="id" id="partnerTransactions_OrderID" name='<%=TemplatePreference.partnerTransactions_OrderID.toString()%>' value="<%=(merchantTemplateSetting!= null && merchantTemplateSetting.containsKey(TemplatePreference.partnerTransactions_OrderID.toString()))?merchantTemplateSetting.get(TemplatePreference.partnerTransactions_OrderID.toString()):""%>"/>Order ID</li>
                                                        <input type="hidden" class="id_1" id="partnerTransactions_OrderID_hidden" name='<%=TemplatePreference.partnerTransactions_OrderID.toString()%>' value>
                                                        <br>
                                                        <li style="color: #000000"><input type="checkbox" class="id" id="partnerTransactions_OrderDescription" name='<%=TemplatePreference.partnerTransactions_OrderDescription.toString()%>' value="<%=(merchantTemplateSetting!= null && merchantTemplateSetting.containsKey(TemplatePreference.partnerTransactions_OrderDescription.toString()))?merchantTemplateSetting.get(TemplatePreference.partnerTransactions_OrderDescription.toString()):""%>"/>Order Description</li>
                                                        <input type="hidden" class="id_1" id="partnerTransactions_OrderDescription_hidden" name='<%=TemplatePreference.partnerTransactions_OrderDescription.toString()%>' value>
                                                        <br>
                                                        <li style="color: #000000"><input type="checkbox" class="id" id="partnerTransactions_Card_Holder_Name" name='<%=TemplatePreference.partnerTransactions_Card_Holder_Name.toString()%>' value="<%=(merchantTemplateSetting!= null && merchantTemplateSetting.containsKey(TemplatePreference.partnerTransactions_Card_Holder_Name.toString()))?merchantTemplateSetting.get(TemplatePreference.partnerTransactions_Card_Holder_Name.toString()):""%>"/>Card Holder's Name</li>
                                                        <input type="hidden" class="id_1" id="partnerTransactions_Card_Holder_Name_hidden" name='<%=TemplatePreference.partnerTransactions_Card_Holder_Name.toString()%>' value>
                                                        <br>
                                                        <li style="color: #000000"><input type="checkbox" class="id" id="partnerTransactions_Customer_Email" name='<%=TemplatePreference.partnerTransactions_Customer_Email.toString()%>' value="<%=(merchantTemplateSetting!= null && merchantTemplateSetting.containsKey(TemplatePreference.partnerTransactions_Customer_Email.toString()))?merchantTemplateSetting.get(TemplatePreference.partnerTransactions_Customer_Email.toString()):""%>"/>Customer Email</li>
                                                        <input type="hidden" class="id_1" id="partnerTransactions_Customer_Email_hidden" name='<%=TemplatePreference.partnerTransactions_Customer_Email.toString()%>' value>
                                                        <br>
                                                        <li style="color: #000000"><input type="checkbox" class="id" id="partnerTransactions_CustomerID" name='<%=TemplatePreference.partnerTransactions_CustomerID.toString()%>' value="<%=(merchantTemplateSetting!= null && merchantTemplateSetting.containsKey(TemplatePreference.partnerTransactions_CustomerID.toString()))?merchantTemplateSetting.get(TemplatePreference.partnerTransactions_CustomerID.toString()):""%>"/>Customer ID</li>
                                                        <input type="hidden" class="id_1" id="partnerTransactions_CustomerID_hidden" name='<%=TemplatePreference.partnerTransactions_CustomerID.toString()%>' value>
                                                        <br>
                                                        <li style="color: #000000"><input type="checkbox" class="id" id="partnerTransactions_PaymentMode" name='<%=TemplatePreference.partnerTransactions_PaymentMode.toString()%>' value="<%=(merchantTemplateSetting!= null && merchantTemplateSetting.containsKey(TemplatePreference.partnerTransactions_PaymentMode.toString()))?merchantTemplateSetting.get(TemplatePreference.partnerTransactions_PaymentMode.toString()):""%>"/>Payment Mode</li>
                                                        <input type="hidden" class="id_1" id="partnerTransactions_PaymentMode_hidden" name='<%=TemplatePreference.partnerTransactions_PaymentMode.toString()%>' value>
                                                        <br>
                                                        <li style="color: #000000"><input type="checkbox" class="id" id="partnerTransactions_PaymentBrand" name='<%=TemplatePreference.partnerTransactions_PaymentBrand.toString()%>' value="<%=(merchantTemplateSetting!= null && merchantTemplateSetting.containsKey(TemplatePreference.partnerTransactions_PaymentBrand.toString()))?merchantTemplateSetting.get(TemplatePreference.partnerTransactions_PaymentBrand.toString()):""%>"/>Payment Brand</li>
                                                        <input type="hidden" class="id_1" id="partnerTransactions_PaymentBrand_hidden" name='<%=TemplatePreference.partnerTransactions_PaymentBrand.toString()%>' value>
                                                        <br>
                                                        <li style="color: #000000"><input type="checkbox" class="id" id="transactions_mode" name='<%=TemplatePreference.transactions_mode.toString()%>' value="<%=(merchantTemplateSetting!= null && merchantTemplateSetting.containsKey(TemplatePreference.transactions_mode.toString()))?merchantTemplateSetting.get(TemplatePreference.transactions_mode.toString()):""%>"/>Transaction Mode</li>
                                                        <input type="hidden" class="id_1" id="transactions_mode_hidden" name='<%=TemplatePreference.transactions_mode.toString()%>' value>
                                                        <br>
                                                        <li style="color: #000000"><input type="checkbox" class="id" id="partnerTransactions_Amount" name='<%=TemplatePreference.partnerTransactions_Amount.toString()%>' value="<%=(merchantTemplateSetting!= null && merchantTemplateSetting.containsKey(TemplatePreference.partnerTransactions_Amount.toString()))?merchantTemplateSetting.get(TemplatePreference.partnerTransactions_Amount.toString()):""%>"/>Amount</li>
                                                        <input type="hidden" class="id_1" id="partnerTransactions_Amount_hidden" name='<%=TemplatePreference.partnerTransactions_Amount.toString()%>' value>
                                                        <br>
                                                        <li style="color: #000000"><input type="checkbox" class="id" id="partnerTransactions_RefundAmount" name='<%=TemplatePreference.partnerTransactions_RefundAmount.toString()%>' value="<%=(merchantTemplateSetting!= null && merchantTemplateSetting.containsKey(TemplatePreference.partnerTransactions_RefundAmount.toString()))?merchantTemplateSetting.get(TemplatePreference.partnerTransactions_RefundAmount.toString()):""%>"/>Refund Amount</li>
                                                        <input type="hidden" class="id_1" id="partnerTransactions_RefundAmount_hidden" name='<%=TemplatePreference.partnerTransactions_RefundAmount.toString()%>' value>
                                                        <br>
                                                        <li style="color: #000000"><input type="checkbox" class="id" id="partnerTransactions_Currency" name='<%=TemplatePreference.partnerTransactions_Currency.toString()%>' value="<%=(merchantTemplateSetting!= null && merchantTemplateSetting.containsKey(TemplatePreference.partnerTransactions_Currency.toString()))?merchantTemplateSetting.get(TemplatePreference.partnerTransactions_Currency.toString()):""%>"/>Currency</li>
                                                        <input type="hidden" class="id_1" id="partnerTransactions_Currency_hidden" name='<%=TemplatePreference.partnerTransactions_Currency.toString()%>' value>
                                                        <br>
                                                        <li style="color: #000000"><input type="checkbox" class="id" id="partnerTransactions_Status1" name='<%=TemplatePreference.partnerTransactions_Status1.toString()%>' value="<%=(merchantTemplateSetting!= null && merchantTemplateSetting.containsKey(TemplatePreference.partnerTransactions_Status1.toString()))?merchantTemplateSetting.get(TemplatePreference.partnerTransactions_Status1.toString()):""%>"/>Status</li>
                                                        <input type="hidden" class="id_1" id="partnerTransactions_Status1_hidden" name='<%=TemplatePreference.partnerTransactions_Status1.toString()%>' value>
                                                        <br>
                                                        <li style="color: #000000"><input type="checkbox" class="id" id="partnerTransactions_Remark" name='<%=TemplatePreference.partnerTransactions_Remark.toString()%>' value="<%=(merchantTemplateSetting!= null && merchantTemplateSetting.containsKey(TemplatePreference.partnerTransactions_Remark.toString()))?merchantTemplateSetting.get(TemplatePreference.partnerTransactions_Remark.toString()):""%>"/>Remark</li>
                                                        <input type="hidden" class="id_1" id="partnerTransactions_Remark_hidden" name='<%=TemplatePreference.partnerTransactions_Remark.toString()%>' value>
                                                        <br>
                                                        <li style="color: #000000"><input type="checkbox" class="id" id="partnerTransactions_TerminalID" name='<%=TemplatePreference.partnerTransactions_TerminalID.toString()%>' value="<%=(merchantTemplateSetting!= null && merchantTemplateSetting.containsKey(TemplatePreference.partnerTransactions_TerminalID.toString()))?merchantTemplateSetting.get(TemplatePreference.partnerTransactions_TerminalID.toString()):""%>"/>Terminal ID</li>
                                                        <input type="hidden" class="id_1" id="partnerTransactions_TerminalID_hidden" name='<%=TemplatePreference.partnerTransactions_TerminalID.toString()%>' value>
                                                        <br>
                                                        <li style="color: #000000"><input type="checkbox" class="id" id="partnerTransactions_LastUpdateDate" name='<%=TemplatePreference.partnerTransactions_LastUpdateDate.toString()%>' value="<%=(merchantTemplateSetting!= null && merchantTemplateSetting.containsKey(TemplatePreference.partnerTransactions_LastUpdateDate.toString()))?merchantTemplateSetting.get(TemplatePreference.partnerTransactions_LastUpdateDate.toString()):""%>"/>Last Update Date</li>
                                                        <input type="hidden" class="id_1" id="partnerTransactions_LastUpdateDate_hidden" name='<%=TemplatePreference.partnerTransactions_LastUpdateDate.toString()%>' value>
                                                        <br>
                                                        <input type="hidden" name="desc" value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>">
                                                        <input type="hidden" name="trackingid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>">
                                                        <input type="hidden" name="status" value="<%=ESAPI.encoder().encodeForHTMLAttribute(status)%>">
                                                        <input type="hidden" name="fromdate" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>">
                                                        <input type="hidden" name="todate" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>">
                                                        <input type="hidden" name="starttime" value="<%=ESAPI.encoder().encodeForHTMLAttribute(startTime)%>">
                                                        <input type="hidden" name="endtime" value="<%=ESAPI.encoder().encodeForHTMLAttribute(endTime)%>">
                                                        <input type="hidden" name="datetype" value="<%=ESAPI.encoder().encodeForHTMLAttribute(dateType)%>">
                                                        <input type="hidden" name="statusflag" value="<%=ESAPI.encoder().encodeForHTMLAttribute(flaghash)%>">
                                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cardpresent)%>" name="cardpresent">
                                                        <input type="hidden" name="archive" value="<%=archive%>">
                                                        <input type="hidden" name="customerid" value="<%=customerId%>">
                                                        <input type="hidden" name="firstname" value="<%=firstName%>">
                                                        <input type="hidden" name="lastname" value="<%=lastName%>">
                                                        <input type="hidden" name="emailaddr" value="<%=emailAddress%>">
                                                        <input type="hidden" name="terminalid" value="<%=terminalid%>">
                                                        <input type="hidden" name="issuingbank" value="<%=issuingbank%>">
                                                        <input type="hidden" name="paymentid" value="<%=paymentId%>">
                                                        <input type="hidden" name="pid" value="<%=pid%>">
                                                        <input type="hidden" name="memberid" value="<%=memberid%>">
                                                        <input type="hidden" name="cardtype" value="<%=typeCard%>">
                                                        <input type="hidden" value="<%=transactionModeStr%>" name="transactionMode">
                                                        <input type="hidden" value="<%=pgtypeId%>" name="pgtypeid">
                                                        <input type="hidden" value="<%=accountId%>" name="accountid">
                                                        <input type="hidden" name="ctoken" value="<%=ctoken%>">
                                                        <button class="buttonform" type="submit" value="Save" name="Save" id="Save" onclick="mySave()" style="font-weight: 400; color: #000000">
                                                            Save
                                                        </button>
                                                    </ul>
                                                </li>
                                            </ul>
                                        </ul>
                                    </form>
                                </div>
                            </div>

                            <div class="pull-right">
                                <div class="btn-group">
                                    <form name="exportform" method="post" action="/partner/net/ExportTransactionByPartner?ctoken=<%=ctoken%>" >
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" name="fromdate">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" name="todate">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(startTime)%>" name="starttime">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(endTime)%>" name="endtime">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(status)%>" name="status">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>" name="desc">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>" name="memberid">
                                        <input type="hidden" value="<%=pid%>" name="pid">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(dateType)%>" name="datetype">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cardpresent)%>" name="cardpresent">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(flaghash)%>" name="statusflag">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(String.valueOf(archive))%>" name="archive">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(String.valueOf(trackingid))%>" name="trackingid">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(issuingbank)%>" name="issuingbank">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(remark)%>" name="remark">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(typeCard)%>" name="cardtype">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(terminalid)%>" name="terminalid">
                                        <input type="hidden" value="<%=customerId%>" name="customerid">
                                        <input type="hidden" value="<%=firstName%>" name="firstname">
                                        <input type="hidden" value="<%=lastName%>" name="lastname">
                                        <input type="hidden" value="<%=emailAddress%>" name="emailaddr">
                                        <input type="hidden" value="<%=pgtypeId%>" name="pgtypeid">
                                        <input type="hidden" value="<%=accountId%>" name="accountid">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <input type="hidden" value="<%=timezone%>" name="timezone">
                                        <input type="hidden" value="<%=Roles%>" name="role">
                                        <input type="hidden" value="<%=transactionModeStr%>" name="transactionMode">

                                        <button class="btn-xs" type="submit" style="background: white;border: 0;">
                                            <img style="height: 40px;" src="/partner/images/exporttransaction.png">
                                        </button>
                                    </form>
                                </div>
                            </div>

                            <div class="pull-right">
                                <div class="btn-group">
                                    <form action="/partner/net/ExportActionHistoryByPartner?ctoken=<%=ctoken%>" method="post" >
                                        <%-- <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(String.valueOf(trackingLIst))%>" name="trackingLIst">--%>
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(String.valueOf(trackingid))%>" name="trackingid">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" name="fromdate">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" name="todate">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cardpresent)%>" name="cardpresent">
                                        <button class="btn-xs" type="submit" style="background: transparent;border: 0;">
                                            <img style="height: 40px;" src="/partner/images/exportHistory.png">
                                        </button>
                                    </form>
                                </div>
                            </div>

                            <div class="widget-content padding" style="overflow-x: auto;">

                                <table align=center width="50%" id="myTable" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    <tr>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" class="partnerTransactions_Transaction_Date1"><b><%=partnerTransactions_Transaction_Date1%></b></th>
                                        <%
                                            if("Y".equals(cardpresent)){
                                        %>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" class="partnerTransactions_Transaction_fetched"><b><%=partnerTransactions_Transaction_fetched%></b></th>
                                        <%
                                            }
                                        %>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" class="partnerTransactions_TimeZone"><b><%=timezone =="" || timezone ==null?"TimeZone":timezone%></b></th>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;padding-right:40px; white-space: nowrap;" class="partnerTransactions_TrackingID"><b><%=partnerTransactions_TrackingID%></b></th>
                                        <th  valign="middle" width="0%" align="center" style="background-color: #7eccad !important;color: white;"></th>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" class="partnerTransactions_PaymentID"><b><%=partnerTransactions_PaymentID%></b></th>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" class="partnerTransactions_PartnerID"><b><%=partnerTransactions_PartnerID%></b></th>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" class="partnerTransactions_MerchantID"><b><%=partnerTransactions_MerchantID%></b></th>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" class="partnerTransactions_OrderID"><b><%=partnerTransactions_OrderID%></b></th>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" class="partnerTransactions_OrderDescription"><b><%=partnerTransactions_OrderDescription%></b></th>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" class="partnerTransactions_Card_Holder_Name"><b><%=partnerTransactions_Card_Holder_Name%></b></th>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" class="partnerTransactions_Customer_Email"><b><%=partnerTransactions_Customer_Email%></b></th>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" class="partnerTransactions_CustomerID"><b><%=partnerTransactions_CustomerID%></b></th>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" class="partnerTransactions_PaymentMode"><b><%=partnerTransactions_PaymentMode%></b></th>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" class="partnerTransactions_PaymentBrand"><b><%=partnerTransactions_PaymentBrand%></b></th>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" class="transactions_mode"><b><%=transactions_mode%></b></th>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" class="partnerTransactions_Amount"><b><%=partnerTransactions_Amount%></b></th>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" class="partnerTransactions_RefundAmount"><b><%=partnerTransactions_RefundAmount%></b></th>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" class="partnerTransactions_Currency"><b><%=partnerTransactions_Currency%></b></th>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" class="partnerTransactions_Status1"><b><%=partnerTransactions_Status1%></b></th>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" class="partnerTransactions_Remark"><b><%=partnerTransactions_Remark%></b></th>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" class="partnerTransactions_TerminalID"><b><%=partnerTransactions_TerminalID%></b></th>
                                        <th  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" class="partnerTransactions_LastUpdateDate"><b><%=partnerTransactions_LastUpdateDate%></b></th>
                                    </tr>
                                    </thead>
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                    <%
                                        StringBuffer requestParameter           = new StringBuffer();
                                        Enumeration<String> stringEnumeration   = request.getParameterNames();
                                        while(stringEnumeration.hasMoreElements())
                                        {
                                            String name = stringEnumeration.nextElement();
                                            if("SPageno".equals(name) || "SRecords".equals(name))
                                            {

                                            }
                                            else
                                                requestParameter.append("<input type='hidden' name='"+name+"' value=\""+request.getParameter(name)+"\"/>");
                                        }
                                        requestParameter.append("<input type='hidden' name='SPageno' value='"+pageno+"'/>");
                                        requestParameter.append("<input type='hidden' name='SRecords' value='"+pagerecords+"'/>");

                                        for (int pos = 1; pos <= records; pos++)
                                        {
                                            String id = Integer.toString(pos);

                                            int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);

                                            style = "class=\"tr" + (pos + 1) % 2 + "\"";

                                            temphash = (HashMap) hash.get(id);


                                           /* String pattern = "d MMM yyyy HH:mm:ss";
                                            Date dateq = new Date((String) temphash.get("transactionTime"));
                                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                            String date2 = null;
                                            date2 = simpleDateFormat.parse(dateq);*/


                                            String date2        = Functions.convertDtstampToDateTime((String) temphash.get("dtstamp"));

                                            String description  = (String)temphash.get("description");
                                            String icicitransid     =(String)temphash.get("transid");
                                            // trackingList.add(icicitransid);
                                            String name     = (String)temphash.get("name");
                                            ctoken          = request.getParameter("ctoken");
                                            if (name == null) name = "-";
                                            String currency     = (String) temphash.get("currency");
                                            String amount       = (String) temphash.get("amount");
                                            String refundamount = (String) temphash.get("refundamount");
                                            if ("JPY".equalsIgnoreCase(currency))
                                            {
                                                amount          = functions.printNumber(Locale.JAPAN, amount);
                                                refundamount    = functions.printNumber(Locale.JAPAN, refundamount);
                                            }
                                            else if ("EUR".equalsIgnoreCase(currency))
                                            {
                                                amount      = functions.printNumber(Locale.FRANCE, amount);
                                                refundamount = functions.printNumber(Locale.FRANCE, refundamount);
                                            }
                                            else if ("GBP".equalsIgnoreCase(currency))
                                            {
                                                amount          = functions.printNumber(Locale.UK, amount);
                                                refundamount    = functions.printNumber(Locale.UK, refundamount);
                                            }
                                            else if ("USD".equalsIgnoreCase(currency))
                                            {
                                                amount          = functions.printNumber(Locale.US, amount);
                                                refundamount    = functions.printNumber(Locale.US, refundamount);
                                            }
                                            String captureamount    = (String) temphash.get("captureamount");
                                            String tempstatus       = (String) temphash.get("status");
                                            String paymodeid        = (String) temphash.get("paymodeid");
                                            String cardtypeid       = (String) temphash.get("cardtypeid");
                                            String accountid        = (String) temphash.get("accountid");
                                            String transactionMode  = (String) temphash.get("transaction_mode");
                                            if (!functions.isValueNull(transactionMode))
                                            {
                                                transactionMode = "--";
                                            }if (functions.isEmptyOrNull(accountid))
                                            {
                                                accountid = "0";
                                            }
                                            String toid                         = (String) temphash.get("toid");
                                            PartnerFunctions partnerfunctions   = new PartnerFunctions();
                                            String partner_id                   = partnerfunctions.getPartnerId(toid);
                                            String paymentIdFromHash            = (String) temphash.get("paymentid");
                                            String orderDescription             = (String) temphash.get("orderdescription");
                                            ctoken                              = request.getParameter("ctoken");
                                            String cardp                        = request.getParameter("cardpresent");
                                            if (name == null) name = "-";
                                            String transCurrency    = (String) temphash.get("currency");
                                            String tempterminalid   = (String) temphash.get("terminalid");
                                            String customerId1      = (String) temphash.get("customerId");
                                            String remark1          = (String) temphash.get("remark");
                                            String customerEmail    = (String) temphash.get("emailAddr");
                                            String print_email      = " ";
                                            String print_vpaAddress = " ";
                                            if(Roles.contains("superpartner")){
                                                print_email = customerEmail;
                                                print_vpaAddress=customerId1;
                                            }else{

                                                print_email = functions.getEmailMasking(customerEmail);
                                                if(functions.isValueNull(customerId1)){
                                                    print_vpaAddress=functions.maskVpaAddress(customerId1);
                                                }else{
                                                    print_vpaAddress="-";
                                                }
                                            }
                                            String lastUpdateDate = (String)temphash.get("timestamp");
                                            if (tempterminalid == null) tempterminalid = "-";
                                            if(!functions.isValueNull(paymentIdFromHash))
                                            {
                                                paymentIdFromHash   ="-";
                                            }
                                            ctoken      = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

                                            String dt   = Functions.convertDtstampToDateTimeforTimezone((String) temphash.get("dtstamp"));

                                            String tz1  = "-";
                                            if (functions.isValueNull(timezone))
                                            {
                                                if (timezone.indexOf("|") != -1)
                                                {
                                                    tz1 = functions.convertDateTimeToTimeZone(dt, timezone.substring(0,timezone.indexOf("|")));
                                                }
                                                else
                                                {
                                                    tz1 = functions.convertDateTimeToTimeZone(dt, timezone);
                                                }
                                            }

                                            out.println("<tr " + style + ">");
                                            if("Y".equals(cardpresent))
                                            {
                                                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                                                java.util.Date t    = ft.parse((String) temphash.get("transactionTime"));
                                                ft.applyPattern("d MMM yyyy HH:mm:ss");
                                                out.println("<td valign=\"middle\" data-label=\"Date\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(ft.format(t)) + "</td>");
                                            }else{
                                                out.println("<td valign=\"middle\" class=\"partnerTransactions_Transaction_Date1\" data-label=\"Date\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(date2) + "</td>");
                                            }
                                            if("Y".equals(cardpresent))
                                            {
                                                out.println("<td valign=\"middle\" data-label=\"Date\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(date2) + "</td>");
                                            }
                                            out.println("<td valign=\"middle\" class=\"partnerTransactions_TimeZone\" data-label=\"TimeZone\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(tz1) + "</td>");
                                            out.println("<td valign=\"middle\" class=\"partnerTransactions_TrackingID\" data-label=\"Tracking ID\" align=\"center\"" + style + "><form action=\"PartnerTransactionDetails?ctoken=\"+ctoken+\"method=\"post\" ><input type=\"hidden\" name=\"archive\" value=\"" + archive + "\"><input type=\"hidden\" name=\"cardpresent\" value=\"" + cardp + "\"><input type=\"hidden\" name=\"status\" value=\"" + tempstatus + "\"><input type=\"hidden\" name=\"accountid\" value=\"" + accountid + "\"><input type=\"hidden\" name=\"memberid\" value=\"" + toid + "\"><input type=\"hidden\" name=\"cardtypeid\" value=\"" + cardtypeid + "\"><input type=\"hidden\" name=\"trackingid\" value=\"" + icicitransid + "\"><input type=\"hidden\" name=\"ctoken\" value=\"" + ctoken + "\"><input type=\"submit\" class=\"btn btn-default\" name=\"action\" value=\"" + icicitransid + "\">");
                                            out.println("<td valign=\"middle\" width=\"0%\" align=\"center\" " + style + "><span title=\"copy\" onclick=\"copyToClipboard("+icicitransid+")\" ><i style=\"position: relative;right: 15px;top: 8px;\" class=\"fa-solid fa-copy\"></i></span></td>");
                                            out.println("<td valign=\"middle\" class=\"partnerTransactions_PaymentID\" data-label=\"Transaction ID\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(paymentIdFromHash) + "</td>");
                                            out.println("<td valign=\"middle\" class=\"partnerTransactions_PartnerID\" data-label=\"Merchant ID\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(partner_id) + "</td>");
                                            out.println("<td valign=\"middle\" class=\"partnerTransactions_MerchantID\" data-label=\"Merchant ID\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(toid) + "</td>");
                                            out.println(requestParameter.toString());
                                            out.print("</form></td>");
                                            out.println("<td valign=\"middle\" class=\"partnerTransactions_OrderID\" data-label=\"Description\" style=\"word-break:break-all\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(description) + "</td>");
                                            if (orderDescription==null || orderDescription.equals(""))
                                            {
                                                out.println("<td class=\"partnerTransactions_OrderDescription\" data-label=\"Order Description\" align=\"center\"" + style + ">" + "-" + "</td>");
                                            }
                                            else
                                            {
                                                out.println("<td class=\"partnerTransactions_OrderDescription\" data-label=\"Order Description\" align=\"center\">" + ESAPI.encoder().encodeForHTML(orderDescription) + "</td>");
                                            }

                                            out.println("<td valign=\"middle\" class=\"partnerTransactions_Card_Holder_Name\" data-label=\"Card Holder's Name\" align=\"center\"" + style + ">&nbsp;" +ESAPI.encoder().encodeForHTML( name) + "</td>");
                                            out.println("<td valign=\"middle\" class=\"partnerTransactions_Customer_Email\" data-label=\"Customer Email\" align=\"center\"" + style + ">" + print_email + "</td>");
                                            if (!functions.isValueNull(customerId1))
                                            {
                                               out.println("<td class=\"partnerTransactions_CustomerID\" data-label=\"Customer ID\" align=\"center\"" + style + ">" +"-"+ "</td>");
                                            }
                                            else
                                            {
                                                out.println("<td class=\"partnerTransactions_CustomerID\" data-label=\"Customer ID\" align=\"center\"" + style + ">" + print_vpaAddress + "</td>");
                                                // out.println("<td data-label=\"Customer ID\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(customerId1) + "</td>");
                                            }
                                            if(!functions.isValueNull(paymodeid) || "0".equals(paymodeid))
                                            {
                                                out.println("<td class=\"partnerTransactions_PaymentMode\" data-label=\"PayMode\" align=\"center\"" + style + ">" + "-" + "</td>");
                                            }
                                            else
                                            {
                                                out.println("<td class=\"partnerTransactions_PaymentMode\" data-label=\"PayMode\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(paymodeid) + "</td>");
                                            }

                                        /*if(cardtypeid==null || cardtypeid.equals("") )
                                        {
                                            out.println("<td valign=\"middle\" data-label=\"CardType\" align=\"center\"" + style + ">" + "-" + "</td>");
                                        }
                                        else
                                        {
                                            String cardTypeValue = GatewayAccountService.getCardType(cardtypeid);
                                            out.println("<td valign=\"middle\" data-label=\"CardType\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(cardTypeValue) + "</td>");
                                        }*/
                                            if(!functions.isValueNull(cardtypeid))
                                            {
                                                out.println("<td class=\"partnerTransactions_PaymentBrand\" data-label=\"CardType\" align=\"center\"" + style + ">&nbsp;" + "-" + "</td>");
                                            }
                                            else
                                            {
                                                cardtypeid = GatewayAccountService.getCardType(cardtypeid);
                                                if (functions.isValueNull(cardtypeid))
                                                {
                                                    out.println("<td class=\"partnerTransactions_PaymentBrand\" data-label=\"CardType\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(cardtypeid) + "</td>");
                                                }
                                                else
                                                {
                                                    out.println("<td class=\"partnerTransactions_PaymentBrand\" data-label=\"CardType\" align=\"center\"" + style + ">&nbsp;" + "-" + "</td>");
                                                }
                                            }
                                            out.println("<td valign=\"middle\" class=\"transactions_mode\" data-label=\"Transaction mode\" align=\"center\"" + style + ">"+ESAPI.encoder().encodeForHTML(transactionMode)+"</td>");
                                            out.println("<td valign=\"middle\" class=\"partnerTransactions_Amount\" data-label=\"Auth Amt\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(amount) + "</td>");
                                            out.println("<td valign=\"middle\" class=\"partnerTransactions_RefundAmount\" data-label=\"Refund Amt\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(refundamount) + "</td>");
                                            out.println("<td valign=\"middle\" class=\"partnerTransactions_Currency\" data-label=\"Currency\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(currency) + "</td>");
                                            out.println("<td valign=\"middle\" class=\"partnerTransactions_Status1\" data-label=\"Status\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(tempstatus) + "</td>");
                                            if (!functions.isValueNull(remark1))
                                            {
                                                out.println("<td class=\"partnerTransactions_Remark\" data-label=\"Remark\" align=\"center\"" + style + ">" +"-"+ "</td>");
                                            }
                                            else
                                            {
                                                out.println("<td class=\"partnerTransactions_Remark\" valign=\"middle\" data-label=\"Remark\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(remark1) + "</td>");
                                            }

                                            out.println("<td class=\"partnerTransactions_TerminalID\" valign=\"middle\" data-label=\"Terminal ID\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(tempterminalid) + "</td>");

                                            out.println("<td class=\"partnerTransactions_LastUpdateDate\" valign=\"middle\" data-label=\"Last Update Date\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(lastUpdateDate) + "</td>");

                                            out.println("</tr>");

                                        }
                                        session.setAttribute("TrackingIDList",trackingList);
                                    %>
                                </table>

                            </div>

                        </div>
                        <% int TotalPageNo;
                            if(totalrecords%pagerecords != 0)
                            {
                                TotalPageNo = totalrecords/pagerecords+1;
                            }
                            else
                            {
                                TotalPageNo = totalrecords/pagerecords;
                            }
                        %>
                        <div id="showingid"><strong><%=partnerTransactions_page_no%> <%=pageno%> <%=partnerTransactions_of%> <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
                        <div id="showingid"><strong><%=partnerTransactions_total_no_of_records%>   <%=totalrecords%> </strong></div>

                        <jsp:include page="page.jsp" flush="true">
                            <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                            <jsp:param name="numrows" value="<%=pagerecords%>"/>
                            <jsp:param name="pageno" value="<%=pageno%>"/>
                            <jsp:param name="str" value="<%=str%>"/>
                            <jsp:param name="page" value="PartnerTransaction"/>
                            <jsp:param name="currentblock" value="<%=currentblock%>"/>
                            <jsp:param name="orderby" value=""/>
                        </jsp:include>
                        <%
                                 }
                            }
                            else
                            {
                                out.println(Functions.NewShowConfirmation1(partnerTransactions_Sorry, partnerTransactions_No_records_found));
                            }
                        %>
                        <%!
                            public static String nullToStr(String str)
                            {
                                if(str == null)
                                    return "";
                                return str;
                            }
                            public static String getStatus(String str)
                            {
                                if(str.equals("Y"))
                                    return "Active";
                                else if(str.equals("N"))
                                    return "Inactive";
                                else if(str.equals("T"))
                                    return "Test";

                                return str;
                            }
                        %>
                    </div>
                </div>
            </div>
            <%
                }
                else
                {
                    response.sendRedirect("/partner/logout.jsp");
                    return;
                }
            %>
        </div>
    </div>
</div>
<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
</body>
</html>

<script language="javascript">
    $(document).ready(function () {
        var isAllChecked = 0;

        $('input:checkbox[class=id]').parent().each(function ()
        {
            if(!$(this).hasClass("checked"))
            {
                isAllChecked = 1;
            }
            if(isAllChecked == 1)
            { $('input:checkbox[name=alltrans]').parent().removeClass("checked"); }
            else
            {
                $('input:checkbox[name=alltrans]').parent().addClass("checked");
            }
        });
        $('input:checkbox[name=alltrans]').parent().children().eq(1).click(function () {

            if($('input:checkbox[name=alltrans]').parent().hasClass("checked"))
            {
                var checkboxes = document.getElementsByClassName("id");
                var total_boxes = checkboxes.length;

                for(i=0; i<total_boxes; i++ )
                {
                    checkboxes[i].checked =true;
                }
                $('input:checkbox[class=id]').parent().addClass("checked");
                $('input:checkbox[class=id]').val("Y");
                $('input:hidden[class=id_1]').val("Y");
                $('#myTable th').css('display','table-cell');
                $('#myTable tr > td').css('display','table-cell');
            }
            else
            {
                var checkboxes = document.getElementsByClassName("id");
                var total_boxes = checkboxes.length;

                for(i=0; i<total_boxes; i++ )
                {
                    checkboxes[i].checked =false;
                    //checkboxes[i].next().val("");
                }
                $('input:checkbox[class=id]').parent().removeClass("checked");
                $('input:checkbox[class=id]').val("");
                $('input:hidden[class=id_1]').val("");
                $('#myTable th').css('display','none');
                $('#myTable tr > td').css('display','none');
            }
        });
    });
</script>
