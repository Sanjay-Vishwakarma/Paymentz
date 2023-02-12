<%@ page errorPage="error.jsp" import="com.directi.pg.Functions,com.directi.pg.TransactionEntry,
                 com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.*" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.enums.TemplatePreference" %>
<%@ page import="com.directi.pg.*" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="java.io.File" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>

<script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
<script src="/merchant/NewCss/js/jquery-ui.min.js"></script>
<script src="/merchant/javascript/autocomplete_merchant_terminalid.js"></script>
<script language="javascript">

    /*$(document).ready(function()
     {

     var terminalid=$("#terminalid option:selected").text()
     console.log(terminalid + "print");
     if(terminalid.includes("-CP-"))
     {
     $("#cardpresent").show();
     $("#cardtype").val("CP");
     $("#cardtype1").val("CP");
     }
     else {
     $("#cardpresent").hide();
     $("#cardtype").val("CNP");
     $("#cardtype1").val("CNP");
     }
     });

     function cphideshow(sel)
     {

     var terminalid=sel.options[sel.selectedIndex].text;
     if(terminalid.includes("-CP-"))
     {
     $("#cardpresent").show();
     $("#cardtype").val("CP");
     $("#cardtype1").val("CP");
     }
     else {
     $("#cardpresent").hide();
     $("#cardtype").val("CNP");
     $("#cardtype").val("CNP");
     }
     };*/
</script>
<script type="text/javascript">
    function ValidateEmail() {
        var email = document.getElementById("txtEmail").value;
        var lblError = document.getElementById("lblError");
        lblError.innerHTML = "";
        var expr = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
        if (!expr.test(email)) {
            lblError.innerHTML = "Invalid email address.";
        }
    }
</script>

<%--FontAwesome CDN--%>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css" integrity="sha512-KfkfwYDsLkIlwQp6LFnl8zNdLGxu9YAA1QvwINks4PhcElQSvqcyVLLD9aMhXd13uQjoXtEKNosOWaZqXgel0g==" crossorigin="anonymous" referrerpolicy="no-referrer" />


<%--<%@include file="ietest.jsp"%>--%>
<%@ include file="Top.jsp" %>

<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    session.setAttribute("submit","Transactions");
%>
<%
    HashMap<String, String> statusFlagHash = new LinkedHashMap();

    statusFlagHash.put("isChargeback", "Chargeback");
    statusFlagHash.put("isFraud", "Fraud");
    statusFlagHash.put("isRefund", "Refund");
    statusFlagHash.put("isRollingReserveKept", "RollingReserveKept");
    statusFlagHash.put("isRollingReserveReleased", "RollingReserveReleased");
    statusFlagHash.put("isSettled", "Settled");
    statusFlagHash.put("isSuccessful", "Successful");

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

    HashMap<String, String> transactionModeHM = new LinkedHashMap();
    String   transactionModeStr = "";
    if(request.getAttribute("transactionMode") != null){
        transactionModeStr  = (String) request.getAttribute("transactionMode");
    }else{
        transactionModeStr  = "";
    }
    transactionModeHM.put("3Dv1","3Dv1");
    transactionModeHM.put("3Dv2","3Dv2");
    transactionModeHM.put("Non-3D","Non-3D");

    ActionEntry entry   = new ActionEntry();
    String uId          = "";
    if(session.getAttribute("role").equals("submerchant"))
    {
        uId = (String) session.getAttribute("userid");
    }
    else
    {
        uId = (String) session.getAttribute("merchantid");
    }

    Merchants merchants1    = new Merchants();
    String currency         = (String) session.getAttribute("currency");
    String bank             = (String) session.getAttribute("bank");
    TransactionEntry transactionentry = (TransactionEntry) session.getAttribute("transactionentry");
    //  Hashtable statushash = transactionentry.getStatusHash();

    SortedMap sortedMap = transactionentry.getSortedMap();

    String desc         = Functions.checkStringNull(request.getParameter("desc")) == null ? "" : request.getParameter("desc");
    String customerId   = Functions.checkStringNull(request.getParameter("customerid")) == null ? "" : request.getParameter("customerid");
    String trackingid   = Functions.checkStringNull(request.getParameter("trackingid"))==null?"":request.getParameter("trackingid");
    String userTerminals    = Functions.checkStringNull(request.getParameter("terminalbuffer"))==null?"":request.getParameter("terminalbuffer");
    String cardtype         = Functions.checkStringNull(request.getParameter("cardtype"))==null?"":request.getParameter("cardtype");


    String fromdate = null;
    String todate   = null;
    Date date                       = new Date();
    SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

    String Date     = originalFormat.format(date);
    date.setDate(1);
    String fromDate = originalFormat.format(date);

    fromdate            = Functions.checkStringNull(request.getParameter("fdate")) == null ? fromDate : request.getParameter("fdate");
    todate              = Functions.checkStringNull(request.getParameter("tdate")) == null ? Date : request.getParameter("tdate");
    String statusflag   = Functions.checkStringNull(request.getParameter("statusflag"));
    String startTime    = Functions.checkStringNull(request.getParameter("starttime"));
    String endTime      = Functions.checkStringNull(request.getParameter("endtime"));
    if (startTime == null) startTime = "00:00:00";
    if (endTime == null) endTime = "23:59:59";

    String terminalid   = null;
    String paymentId    = null;
    String firstName        = Functions.checkStringNull(request.getParameter("firstname"));
    String lastName         = Functions.checkStringNull(request.getParameter("lastname"));
    String emailAddress     = Functions.checkStringNull(request.getParameter("emailaddr"));
    paymentId               = Functions.checkStringNull(request.getParameter("paymentid"))==null?"":request.getParameter("paymentid");
    terminalid              = Functions.checkStringNull(request.getParameter("terminalid"))==null?"":request.getParameter("terminalid");
    String issuingBank      = Functions.checkStringNull(request.getParameter("issuingbank"))==null?"":request.getParameter("issuingbank");
    String dateType         = Functions.checkStringNull(request.getParameter("datetype"));
    String flaghash         = Functions.checkStringNull(request.getParameter("statusflag"));
    String timezone         = Functions.checkStringNull(request.getParameter("timezone"))==null?"":request.getParameter("timezone");
    String firstsix         = Functions.checkStringNull(request.getParameter("firstsix"))==null?"":request.getParameter("firstsix");
    String lastfour         = Functions.checkStringNull(request.getParameter("lastfour"))==null?"":request.getParameter("lastfour");

    if (dateType == null) dateType = "";


    String status = Functions.checkStringNull(request.getParameter("status"));

    Calendar rightNow = Calendar.getInstance();
    String str = "";
    if (fromdate == null) fromdate = "" + 1;
    if (todate == null) todate = "" + rightNow.get(Calendar.DATE);
    String currentyear = "" + rightNow.get(Calendar.YEAR);

    if (fromdate != null) str = str + "fdate=" + fromdate;
    if (todate != null) str = str + "&tdate=" + todate;
    if (desc != null) str = str + "&desc=" + desc;
    if (dateType != null) str = str + "&datetype=" + dateType;
    if (startTime != null) str = str + "&starttime=" + startTime;
    if (endTime != null) str = str + "&endtime=" + endTime;

    if(timezone!=null)str = str + "&timezone=" + timezone;
    else
        timezone="";

    if (customerId != null) str = str + "&customerid=" + customerId;
    if (trackingid != null) str = str + "&trackingid=" + trackingid;
    if(firstName!=null)str = str + "&firstname=" + firstName;
    else
        firstName="";
    if(lastName!=null)str = str + "&lastname=" + lastName;
    else
        lastName="";
    if(emailAddress!=null)str = str + "&emailaddr=" + emailAddress;
    else
        emailAddress="";
    if(paymentId!=null) str=str+"&paymentid="+paymentId;
    if(terminalid!=null)str =str + "&terminalid=" + terminalid;
    else
        terminalid="";
    if(issuingBank != null)str =str + "&issuingbank=" + issuingBank;
    else
        issuingBank="";
    if(userTerminals!=null)str =str + "&terminalbuffer=" + userTerminals;
    else
        userTerminals="";
    if(firstsix!=null) str=str + "&firstsix="+firstsix;
    else
        firstsix="";
    if(lastfour!=null) str=str + "&lastfour="+lastfour;
    else
        lastfour="";

    str             = str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    boolean archive = Boolean.valueOf(request.getParameter("archive")).booleanValue();
    str = str + "&archive=" + archive;
    str = str+"&status="+request.getParameter("status");
    str = str+"&statusflag="+request.getParameter("statusflag");
    str = str+"&transactionMode="+transactionModeStr;

    String archivalString   = "Archived";
    String currentString    = "Current";
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
    int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
    int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

    str = str + "&SRecords=" + pagerecords;
    str = str + "&bank=" + bank;
    ResourceBundle rb1                  = null;
    String language_property1           = (String)session.getAttribute("language_property");
    rb1                                 = LoadProperties.getProperty(language_property1);
    String transactions_transactions    = StringUtils.isNotEmpty(rb1.getString("transactions_transactions"))?rb1.getString("transactions_transactions"): "Transactions";
    String transactions_form            = StringUtils.isNotEmpty(rb1.getString("transactions_form"))?rb1.getString("transactions_form"): "From";
    String transactions_form1           = StringUtils.isNotEmpty(rb1.getString("transactions_form1"))?rb1.getString("transactions_form1"): "From";
    String transactions_to              = StringUtils.isNotEmpty(rb1.getString("transactions_to"))?rb1.getString("transactions_to"): "To";
    String transactions_to1             = StringUtils.isNotEmpty(rb1.getString("transactions_to1"))?rb1.getString("transactions_to1"): "To ";
    String transactions_time_zone       = StringUtils.isNotEmpty(rb1.getString("transactions_time_zone"))?rb1.getString("transactions_time_zone"): "Time Zone";
    String transactions_select_zone     = StringUtils.isNotEmpty(rb1.getString("transactions_select_zone"))?rb1.getString("transactions_select_zone"): "--Select Time Zone--";
    String transactions_tracking        = StringUtils.isNotEmpty(rb1.getString("transactions_tracking"))?rb1.getString("transactions_tracking"): "Tracking ID";
    String transactions_terminalid      = StringUtils.isNotEmpty(rb1.getString("transactions_terminalid"))?rb1.getString("transactions_terminalid"): "Terminal ID";
    String transactions_all             = StringUtils.isNotEmpty(rb1.getString("transactions_all"))?rb1.getString("transactions_all"): "All";
    String transactions_no_terminal     = StringUtils.isNotEmpty(rb1.getString("transactions_no_terminal"))?rb1.getString("transactions_no_terminal"): "No Terminals Allocated";
    String transactions_customerid      = StringUtils.isNotEmpty(rb1.getString("transactions_customerid"))?rb1.getString("transactions_customerid"): "Customer Id";
    String transactions_first_name      = StringUtils.isNotEmpty(rb1.getString("transactions_first_name"))?rb1.getString("transactions_first_name"): "First Name";
    String transactions_last_name       = StringUtils.isNotEmpty(rb1.getString("transactions_last_name"))?rb1.getString("transactions_last_name"): "Last Name";
    String transactions_data            = StringUtils.isNotEmpty(rb1.getString("transactions_data"))?rb1.getString("transactions_data"): "Data Source";
    String transactions_archieve        = StringUtils.isNotEmpty(rb1.getString("transactions_archieve"))?rb1.getString("transactions_archieve"): "Archieve";
    String transactions_current         = StringUtils.isNotEmpty(rb1.getString("transactions_current"))?rb1.getString("transactions_current"): "Current";
    String transactions_email_id        = StringUtils.isNotEmpty(rb1.getString("transactions_email_id"))?rb1.getString("transactions_email_id"): "Email ID";
    String transactions_rows            = StringUtils.isNotEmpty(rb1.getString("transactions_rows"))?rb1.getString("transactions_rows"): "Rows/Pages";
    String transactions_status          = StringUtils.isNotEmpty(rb1.getString("transactions_status"))?rb1.getString("transactions_status"): "Status";
    String transactions_all1            = StringUtils.isNotEmpty(rb1.getString("transactions_all1"))?rb1.getString("transactions_all1"): "All";
    String transactions_order_id        = StringUtils.isNotEmpty(rb1.getString("transactions_order_id"))?rb1.getString("transactions_order_id"): "Order Id";
    String transactions_issusing        = StringUtils.isNotEmpty(rb1.getString("transactions_issusing"))?rb1.getString("transactions_issusing"): "Issuing Bank";
    String transactions_data_type       = StringUtils.isNotEmpty(rb1.getString("transactions_data_type"))?rb1.getString("transactions_data_type"): "Date Type";
    String transactions_transaction     = StringUtils.isNotEmpty(rb1.getString("transactions_transaction"))?rb1.getString("transactions_transaction"): "Transaction Date";
    String transactions_last_updated    = StringUtils.isNotEmpty(rb1.getString("transactions_last_updated"))?rb1.getString("transactions_last_updated"): "Last Updated Date";
    String transactions_status_flag     = StringUtils.isNotEmpty(rb1.getString("transactions_status_flag"))?rb1.getString("transactions_status_flag"): "Status Flag";
    String transactions_all2            = StringUtils.isNotEmpty(rb1.getString("transactions_all2"))?rb1.getString("transactions_all2"): "All";
    String transactions_first_six       = StringUtils.isNotEmpty(rb1.getString("transactions_first_six"))?rb1.getString("transactions_first_six"): "First Six";
    String transactions_last_four           = StringUtils.isNotEmpty(rb1.getString("transactions_last_four"))?rb1.getString("transactions_last_four"): "Last Four";
    String transactions_card_present        = StringUtils.isNotEmpty(rb1.getString("transactions_card_present"))?rb1.getString("transactions_card_present"): "Card Present";
    String transactions_yes                 = StringUtils.isNotEmpty(rb1.getString("transactions_yes"))?rb1.getString("transactions_yes"): "Yes";
    String transactions_no                  = StringUtils.isNotEmpty(rb1.getString("transactions_no"))?rb1.getString("transactions_no"): "No";
    String transactions_search              = StringUtils.isNotEmpty(rb1.getString("transactions_search"))?rb1.getString("transactions_search"): "Search";
    String transactions_sorry               = StringUtils.isNotEmpty(rb1.getString("transactions_sorry"))?rb1.getString("transactions_sorry"): "Sorry";
    String transactions_no_records          = StringUtils.isNotEmpty(rb1.getString("transactions_no_records"))?rb1.getString("transactions_no_records"): "No records found for given search criteria.";
    String transactions_filter              = StringUtils.isNotEmpty(rb1.getString("transactions_filter"))?rb1.getString("transactions_filter"): "Filter";
    String transactions_search1             = StringUtils.isNotEmpty(rb1.getString("transactions_search1"))?rb1.getString("transactions_search1"): "Please provide the appropriate data for the search of Transaction";
    String transactions_SummaryData         = StringUtils.isNotEmpty(rb1.getString("transactions_SummaryData"))?rb1.getString("transactions_SummaryData"): "Summary Data";
    String transactions_note                = StringUtils.isNotEmpty(rb1.getString("transactions_note"))?rb1.getString("transactions_note"): "Note: Click on the Tracking ID to see the Transaction Details";
    String transactions_please              = StringUtils.isNotEmpty(rb1.getString("transactions_please"))?rb1.getString("transactions_please"): "Please save your";
    String transactions_selection           = StringUtils.isNotEmpty(rb1.getString("transactions_selection"))?rb1.getString("transactions_selection"): "selection.";
    String transactions_Select_All          = StringUtils.isNotEmpty(rb1.getString("transactions_Select_All"))?rb1.getString("transactions_Select_All"): "Select All";
    String transactions_Save                = StringUtils.isNotEmpty(rb1.getString("transactions_Save"))?rb1.getString("transactions_Save"): "Save";
    String transactions_Transaction_Date    = StringUtils.isNotEmpty(rb1.getString("transactions_Transaction_Date"))?rb1.getString("transactions_Transaction_Date"): "Transaction Date";
    String transactions_TrackingID          = StringUtils.isNotEmpty(rb1.getString("transactions_TrackingID"))?rb1.getString("transactions_TrackingID"): "Tracking ID";
    String transactions_OrderID             = StringUtils.isNotEmpty(rb1.getString("transactions_OrderID"))?rb1.getString("transactions_OrderID"): "Order ID";
    String transactions_OrderDescription    = StringUtils.isNotEmpty(rb1.getString("transactions_OrderDescription"))?rb1.getString("transactions_OrderDescription"): "Order Description";
    String transactions_card                = StringUtils.isNotEmpty(rb1.getString("transactions_card"))?rb1.getString("transactions_card"): "Card Holder's Name";
    String transactions_CustomerEmail       = StringUtils.isNotEmpty(rb1.getString("transactions_CustomerEmail"))?rb1.getString("transactions_CustomerEmail"): "Customer Email";
    String transactions_CustomerID          = StringUtils.isNotEmpty(rb1.getString("transactions_CustomerID"))?rb1.getString("transactions_CustomerID"): "Customer ID";
    String transactions_PaymentMode         = StringUtils.isNotEmpty(rb1.getString("transactions_PaymentMode"))?rb1.getString("transactions_PaymentMode"): "Payment Mode";
    String transactions_PaymentBrand        = StringUtils.isNotEmpty(rb1.getString("transactions_PaymentBrand"))?rb1.getString("transactions_PaymentBrand"): "Payment Brand";
    String transactions_Amount              = StringUtils.isNotEmpty(rb1.getString("transactions_Amount"))?rb1.getString("transactions_Amount"): "Amount";
    String transactions_IssuingBank         = StringUtils.isNotEmpty(rb1.getString("transactions_IssuingBank"))?rb1.getString("transactions_IssuingBank"): "Issuing Bank";
    String transactions_RefundAmount        = StringUtils.isNotEmpty(rb1.getString("transactions_RefundAmount"))?rb1.getString("transactions_RefundAmount"): "Refund Amount";
    String transactions_Currency            = StringUtils.isNotEmpty(rb1.getString("transactions_Currency"))?rb1.getString("transactions_Currency"): "Currency";
    String transactions_Status              = StringUtils.isNotEmpty(rb1.getString("transactions_Status"))?rb1.getString("transactions_Status"): "Status";
    String transactions_Remark              = StringUtils.isNotEmpty(rb1.getString("transactions_Remark"))?rb1.getString("transactions_Remark"): "Remark";
    String transactions_TerminalID          = StringUtils.isNotEmpty(rb1.getString("transactions_TerminalID"))?rb1.getString("transactions_TerminalID"): "Terminal ID";
    String transactions_Last_Update_Date    = StringUtils.isNotEmpty(rb1.getString("transactions_Last_Update_Date"))?rb1.getString("transactions_Last_Update_Date"): "Last Update Date";
    String transactions_Last_Showing_Page   = StringUtils.isNotEmpty(rb1.getString("transactions_Last_Showing_Page"))?rb1.getString("transactions_Last_Showing_Page"): "Showing Page";
    String transactions_of                  = StringUtils.isNotEmpty(rb1.getString("transactions_of"))?rb1.getString("transactions_of"): "of";
    String transactions_records             = StringUtils.isNotEmpty(rb1.getString("transactions_records"))?rb1.getString("transactions_records"): "records";
    String transactions_Export_To_Excel     = StringUtils.isNotEmpty(rb1.getString("transactions_Export_To_Excel"))?rb1.getString("transactions_Export_To_Excel"): "Export To Excel";
    String transactions_Select_The_Fields   = StringUtils.isNotEmpty(rb1.getString("transactions_Select_The_Fields"))?rb1.getString("transactions_Select_The_Fields"): "Select The Fields";
    String transactions_page_no             = StringUtils.isNotEmpty(rb1.getString("transactions_page_no"))?rb1.getString("transactions_page_no"):"Page number";
    String transactions_total_no_of_records = StringUtils.isNotEmpty(rb1.getString("transactions_total_no_of_records"))?rb1.getString("transactions_total_no_of_records"):"Total number of records";
    String transactions_mode                = StringUtils.isNotEmpty(rb1.getString("transactions_mode")) ? rb1.getString("transactions_mode") : "Transaction Mode";

%>
<input type="hidden" id="pageno" name="pageno" value="<%=pageno%>">
<link rel="stylesheet" href=" /merchant/transactionCSS/css/transactions.css">
<html lang="en">
<script language="javascript">
    function isint(form)
    {
        if (isNaN(form.numrows.value))
            return false;
        else
            return true;
    }
    function validateccnum()
    {

        var firstsix = document.form.firstsix.value;
        var lastfour= document.form.lastfour.value;
        if(firstsix.length==0 && lastfour.length==0 )
            return true;
        if(firstsix.length<5)
        {
            alert("Enter first six of card number");
            return false;
        }

        if( lastfour.length<4)
        {
            alert("Enter last four of card number");
            return false;
        }

    }

    function getExcelFile()
    {
        if(document.getElementById("containrecord"))
        {
            document.exportform.submit();
        }
    }

    function copyToClipboard(element) {
        var $temp = $("<input>");
        $("body").append($temp);
        $temp.val(element).select();
        document.execCommand("copy");
        $temp.remove();
    }


</script>
<head>

    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
    <script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>

    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>

    <script src="/merchant/datepicker/datetimepicker/moment-with-locales.js"></script>
    <script src="/merchant/datepicker/datetimepicker/bootstrap-datetimepicker.min.js"></script>

    <title><%=company%> Merchant Transaction Management > Transactions</title>

    <script src="/merchant/transactionCSS/js/transactions.js"></script>

</head>

<body>

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">

                <div class="col-sm-12 portlets ui-sortable">

                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=transactions_transactions%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>



                        <form onsubmit="validateccnum()" name="form" method="post" action="/merchant/servlet/Transactions?ctoken=<%=ctoken%>">
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                            <input type="hidden" value="<%=uId%>" name="merchantid" id="merchantid">

                            <%
                                StringBuffer terminalBuffer = new StringBuffer();
                                if("ERR".equals(request.getParameter("MES")))
                                {
                                    ValidationErrorList error = (ValidationErrorList) request.getAttribute("validationErrorList");
                                    for (ValidationException errorList : error.errors())
                                    {
                                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errorList.getMessage() + "</h5>");
                                    }
                                }
                                if("err".equals(request.getParameter("date"))&& request.getAttribute("dateRangeIsValid")!=null)
                                {
                                    String dateRangeIsValid= (String) request.getAttribute("dateRangeIsValid");

                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + dateRangeIsValid + "</h5>");

                                }
                                if(request.getAttribute("error")!=null)
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + request.getAttribute("error") + "</h5>");

                                }
                                if(request.getAttribute("catchError")!=null)
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + request.getAttribute("catchError") + "</h5>");

                                }

                            %>
                            <div class="widget-content padding">
                                <div id="horizontal-form">
                                    <div class="form-group col-xs-12 col-sm-6 col-md-3 col-lg-3">

                                        <div class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                                            <label><%=transactions_form%></label>
                                            <input type="text" name="fdate" id="From_dt" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">
                                        </div>

                                        <div <%--id="From_div"--%> class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-4" style="padding: 0;/*width: inherit;*/">
                                            <div class="form-group">
                                                <label class="hide_label"><%=transactions_form1%></label>
                                                <div class='input-group date' >
                                                    <input type='text' id='datetimepicker12' class="form-control" placeholder="HH:MM:SS" name="starttime" maxlength="8" value="<%=startTime%>" style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                                                </div>
                                            </div>
                                        </div>

                                    </div>

                                    <div class="form-group col-xs-12 col-sm-6 col-md-3 col-lg-3">

                                        <div class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                                            <label><%=transactions_to%></label>
                                            <input type="text" name="tdate" id="To_dt" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(todate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">

                                        </div>


                                        <div <%--id="From_div"--%> class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-4" style="padding: 0;/*width: inherit;*/">
                                            <div class="form-group">
                                                <label class="hide_label"><%=transactions_to1%></label>
                                                <div class='input-group date' >
                                                    <input type='text' id='datetimepicker13' class="form-control" placeholder="HH:MM:SS" name="endtime" maxlength="8" value="<%=endTime%>" style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                                                </div>
                                            </div>
                                        </div>

                                    </div>

                                    <div class="form-group col-xs-12 col-sm-6 col-md-3 col-lg-3 has-feedback">
                                        <label ><%=transactions_time_zone%></label>
                                        <select size="1"  name="timezone" class="form-control">
                                            <option value=""><%=transactions_select_zone%></option>
                                            <%

                                                Functions functions = new Functions();
                                                if (functions.isEmptyOrNull(timezone))
                                                {
                                                    timezone = merchants1.getTimeZone((String) session.getAttribute("merchantid"));
                                                }
                                                Set timezoneSet = timezoneHash.keySet();
                                                Iterator itr = timezoneSet.iterator();
                                                String selected4 = "";
                                                String timezonekey = "";
                                                String timezonevalue = "";
                                                while (itr.hasNext())
                                                {
                                                    timezonekey = (String) itr.next();
                                                    timezonevalue = timezoneHash.get(timezonekey);
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

                                    <div class="form-group col-xs-12 col-sm-6 col-md-3 col-lg-3 has-feedback">
                                        <label ><%=transactions_tracking%></label>
                                        <input type=text name="trackingid"  maxlength="20" class="form-control" size="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>" >
                                    </div>

                                    <div class="form-group col-md-12 has-feedback" style="margin:0; padding: 0;"></div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback" <%--style="margin-left: 96px;"--%>>
                                        <label><%=transactions_terminalid%></label>
                                        <select size="1" name="terminalid" id ="terminalid" <%--onchange="cphideshow(this)"--%> class="form-control">
                                            <%
                                                terminalBuffer.append("(");
                                                TerminalManager terminalManager=new TerminalManager();
                                                List<TerminalVO> terminalVOList=terminalManager.getMemberandUserTerminalList(uId, String.valueOf(session.getAttribute("role")));
                                                if(terminalVOList.size()>0)
                                                {
                                            %>
                                            <option value="all"><%=transactions_all%></option>
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
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(terminalVO.getTerminalId())%>" <%=str1%>> <%=ESAPI.encoder().encodeForHTML(terminalVO.getTerminalId() + "-" + terminalVO.getPaymentName() + "-" + terminalVO.getCardType() + "-" + terminalVO.getCurrency() + "-" + active)%> </option>
                                            <%
                                                }
                                                terminalBuffer.append(")");
                                            }
                                            else
                                            {
                                            %>
                                            <option value="NoTerminals"><%=transactions_no_terminal%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>

                                    <div class="form-group col-sm-12 col-md-3 has-feedback">
                                        <label><%=transactions_customerid%></label>
                                        <input type=text name="customerid" maxlength="100"  class="form-control"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(customerId)%>">
                                    </div>

                                    <div class="form-group col-sm-12 col-md-3 has-feedback">
                                        <label ><%=transactions_first_name%></label>
                                        <input type=text name="firstname" maxlength="100"  class="form-control"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(firstName)%>">
                                    </div>

                                    <div class="form-group col-sm-12 col-md-3 has-feedback">
                                        <label><%=transactions_last_name%></label>
                                        <input type=text name="lastname" maxlength="100"  class="form-control"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(lastName)%>">
                                    </div>

                                    <div class="form-group col-sm-12 col-md-3 has-feedback">
                                        <label><%=transactions_data%></label>
                                        <select size="1" name="archive" class="form-control" >
                                            <option value="true" <%=selectedArchived%>><%=transactions_archieve%></option>
                                            <option value="false" <%=selectedCurrent%>><%=transactions_current%></option>
                                        </select>
                                    </div>

                                    <div class="form-group col-sm-12 col-md-3 has-feedback">
                                        <label><%=transactions_email_id%></label>
                                        <input type="text"   class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(emailAddress)%>" name="emailaddr">
                                    </div>
                                    <div class="form-group col-sm-12 col-md-3 has-feedback">
                                        <label ><%=transactions_rows%></label>
                                        <input type="text" maxlength="3"  size="15" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(new Integer(pagerecords).toString())%>" name="SRecords" size="2" class="textBoxes" />
                                    </div>

                                    <div class="form-group col-sm-12 col-md-3 has-feedback">
                                        <label ><%=transactions_status%></label>
                                        <select size="1" name="status"  class="form-control" >
                                            <option value=""><%=transactions_all1%></option>
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
                                                        if(status1.equalsIgnoreCase(status))
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

                                    <div class="form-group col-sm-12 col-md-3 has-feedback" style="display: flow-root;">
                                        <label><%=transactions_order_id%></label>
                                        <input type=text name="desc" maxlength="100"  class="form-control"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>">
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=transactions_issusing%> </label>
                                        <input name="issuingbank" id="ibank" value="<%=ESAPI.encoder().encodeForHTMLAttribute(issuingBank)%>" class="form-control" autocomplete="on">
                                    </div>

                                    <div class="form-group col-sm-12 col-md-3 has-feedback">
                                        <label ><%=transactions_data_type%></label>
                                        <select size="1" name="datetype" class="form-control">
                                            <%
                                                if("TIMESTAMP".equals(dateType))
                                                {%>
                                            <option value="DTSTAMP"><%=transactions_transaction%></option>
                                            <option value="TIMESTAMP" SELECTED><%=transactions_last_updated%></option>
                                            <%}
                                            else
                                            {%>
                                            <option value="DTSTAMP" SELECTED><%=transactions_transaction%></option>
                                            <option value="TIMESTAMP"><%=transactions_last_updated%></option>
                                            <%}
                                            %>
                                        </select>
                                    </div>
                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=transactions_status_flag%></label>
                                        <select size="1" name="statusflag" class="form-control">
                                            <option value="all"><%=transactions_all2%></option>
                                            <%
                                                Set statusflagSet = statusFlagHash.keySet();
                                                Iterator it = statusflagSet.iterator();
                                                String selected3 = "";
                                                String key3 = "";
                                                String value3 = "";
                                                while (it.hasNext())
                                                {
                                                    key3 = (String) it.next();
                                                    value3 = statusFlagHash.get(key3);

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

                                    <div class="form-group col-sm-12 col-md-3 has-feedback">
                                        <label ><%=transactions_first_six%></label>
                                        <input type=text name="firstsix" maxlength="6"  class="form-control"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(firstsix)%>">
                                    </div>

                                    <div class="form-group col-sm-12 col-md-3 has-feedback">
                                        <label><%=transactions_last_four%></label>
                                        <input type=text name="lastfour" maxlength="4"  class="form-control"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(lastfour)%>">
                                    </div>

                                    <%
                                        HashMap terminalList= (HashMap) session.getAttribute("terminallist");
                                        String Status = "";
                                        if(terminalList!=null)
                                        {
                                            Iterator it1 = terminalList.entrySet().iterator();
                                            while (it1.hasNext())
                                            {

                                                Map.Entry pair = (Map.Entry) it1.next();
                                                MerchantTerminalVo merchantTerminalVo = (MerchantTerminalVo) pair.getValue();

                                                if("CP".equals(merchantTerminalVo.getPaymodeName()))
                                                {
                                                    Status = "true";
                                                    continue;
                                                }
                                            }
                                        }
                                        if(Status.equals("true")){
                                    %>
                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback"  id="cardpresent">
                                        <label><%=transactions_card_present%></label>
                                        <select size="1" name="cardtype" class="form-control" id="cardtype" >
                                            <%
                                                if("CP".equals(cardtype))
                                                {%>
                                            <option value="CP" SELECTED><%=transactions_yes%></option>
                                            <option value="CNP"><%=transactions_no%></option>
                                            <%}
                                            else
                                            {%>
                                            <option value="CNP" SELECTED><%=transactions_no%></option>
                                            <option value="CP"><%=transactions_yes%></option>
                                            <%}
                                            %>
                                        </select>
                                        <%--<input type="hidden" name="cardtype" maxlength="4"  id="cardtype1" value="">--%>
                                    </div>
                                    <%
                                        }

                                    %>
                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback"  id="cardpresent">
                                        <label><%=transactions_mode%></label>
                                        <select size="1" name="transactionMode" class="form-control" >
                                            <option value="">All</option>
                                            <%
                                                for(String trnsModeKey : transactionModeHM.keySet()){

                                                    String modStr = transactionModeHM.get(trnsModeKey);
                                                    if(modStr.equalsIgnoreCase(transactionModeStr)){
                                            %>
                                            <option value="<%=modStr%>" SELECTED  ><%=modStr%></option>
                                            <% }else{ %>
                                            <option value="<%=modStr%>" ><%=modStr%></option>
                                            <% }}
                                            %>
                                        </select>
                                    </div>




                                    <div class="form-group col-sm-3 col-md-3 has-feedback">
                                        <label >&nbsp;</label>
                                        <button type="submit" name="B1" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=transactions_search%></button>
                                    </div>
                                    <input type="hidden" value="<%=terminalBuffer.toString()%>" name="terminalbuffer">
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>



            <div class="row">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=transactions_SummaryData%> <%--<font id="info_checkbox">(To view transaction summary data, please select appropriate checbox.)</font>--%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>


                        <div class="widget-content padding" style="overflow-y: auto;">
                            <%----------------------Report Start-----------------------------------%>


                            <%


                                if(request.getAttribute("transactionsdetails")!=null)
                                {
                                    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

                                    Hashtable hash = (Hashtable) request.getAttribute("transactionsdetails");
                                    //HashMap trackinghash    = (HashMap) request.getAttribute("TrackingIDList1");
//                                    System.out.println("get attribute data +++"+trackinghash);
                                    List<String> trackingList = new ArrayList<String>();
                                    HashMap trackinghash=null;

                                    Hashtable temphash = null;
                                    HashMap temphash1=null;
                                    str = str + "&terminalbuffer=" + terminalBuffer;
                                    str = str + "&ctoken=" + ctoken;
                                    str = str + "&terminalid=" + terminalid;
                                    str = str + "&paymentid=" + paymentId;
                                    int records = 0;
                                    int totalrecords = 0,trackingRecords=0;
                                    int currentblock = 1;

                                    try
                                    {
                                        if(request.getAttribute("TrackingIDList1")!=null){
                                            trackinghash    = (HashMap) request.getAttribute("TrackingIDList1");
                                           // trackingRecords = Integer.parseInt((String) trackinghash.get("records"));
                                            totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
                                            System.out.println("totalrecords >>>>> "+totalrecords);
                                            if(totalrecords > 0)
                                            {
                                                for (int pos = 1; pos <= totalrecords; pos++)
                                                {
                                                    String id = Integer.toString(pos);

                                                    temphash1           = (HashMap) trackinghash.get(id);
                                                    String icicitransid =(String)temphash1.get("transid");
                                                    trackingList.add(icicitransid);
                                                }
                                            }
                                        }
                                        records = Integer.parseInt((String) hash.get("records"));
                                        //totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
                                        currentblock = Integer.parseInt(request.getParameter("currentblock"));
                                    }
                                    catch (Exception ex)
                                    {

                                    }
                                    String style = "class=tr0";

                                    if (records > 0)
                                    {

                            %>
                            <%

                                Map<String,Object> merchantTemplateSetting  = null;
                                if(request.getAttribute("merchantTemplateSetting") != null)
                                {
                                    merchantTemplateSetting = (Map<String, Object>) request.getAttribute("merchantTemplateSetting");
                            %>

                            <form action="/merchant/servlet/SetColumnConfig?ctoken=<%=ctoken%>" method=post style="margin: 0;">
                                <div class="showingid"><strong><%=transactions_note%></strong></div>
                                <div class="pull-right" style="margin-left: 6px;">
                                    <ul class="nav navbar-nav navbar-right" id="configbtn_ul" style="margin-bottom: 10px;">
                                        <ul class="nav navbar-nav">
                                            <li class="dropdown btn-default" style="color: white"><a class="dropdown-toggle" data-toggle="dropdown" href="#" id="dropdown_btn"><i class="fa fa-cogs" style="color: white;"></i>&nbsp;&nbsp<%=transactions_Select_The_Fields%>&nbsp;&nbsp;<span class="caret" style="color: white;"></span></a>

                                                <ul class="dropdown-menu" id="grpChkBox">
                                                    <p style="font-weight: 700;"><%=transactions_please%> <br> <%=transactions_selection%></p>
                                                    <li><valign><b><input type="checkbox" onClick="ToggleAll(this);" name="alltrans" name="id"></b>&nbsp;&nbsp<%=transactions_Select_All%>&nbsp;&nbsp;</valign></li>
                                                    <br>
                                                    <li><input type="checkbox" class="id" id="Transactions_Date" name='<%=TemplatePreference.Transactions_Date.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_Date.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_Date.toString()):""%>"/>Transaction Date</li>
                                                    <input class="id_1" type="hidden" id="Transactions_Date_hidden" name='<%=TemplatePreference.Transactions_Date.toString()%>'  value>
                                                    <br>
                                                    <li><input type="checkbox" class="id" id="Transactions_TimeZone" name='<%=TemplatePreference.Transactions_TimeZone.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_TimeZone.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_TimeZone.toString()):""%>"/>Time Zone</li>
                                                    <input class="id_1" type="hidden" id="Transactions_TimeZone_hidden" name='<%=TemplatePreference.Transactions_TimeZone.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" class="id" id="Transactions_TrackingID" name='<%=TemplatePreference.Transactions_TrackingID.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_TrackingID.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_TrackingID.toString()):""%>"/>Tracking ID</li>
                                                    <input class="id_1" type="hidden" id="Transactions_TrackingID_hidden" name='<%=TemplatePreference.Transactions_TrackingID.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" class="id" id="Transactions_OrderId" name='<%=TemplatePreference.Transactions_OrderId.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_OrderId.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_OrderId.toString()):""%>"/>Order Id</li>
                                                    <input class="id_1" type="hidden" id="Transactions_OrderId_hidden" name='<%=TemplatePreference.Transactions_OrderId.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" class="id" id="Transactions_OrderDescription" name='<%=TemplatePreference.Transactions_OrderDescription.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_OrderDescription.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_OrderDescription.toString()):""%>"/>Order Description</li>
                                                    <input class="id_1" type="hidden" id="Transactions_OrderDescription_hidden" name='<%=TemplatePreference.Transactions_OrderDescription.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" class="id" id="Transactions_CardHoldername" name='<%=TemplatePreference.Transactions_CardHoldername.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_CardHoldername.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_CardHoldername.toString()):""%>"/>Card Holder's Name</li>
                                                    <input class="id_1" type="hidden" id="Transactions_CardHoldername_hidden" name='<%=TemplatePreference.Transactions_CardHoldername.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" class="id" id="Transactions_CustomerEmail" name='<%=TemplatePreference.Transactions_CustomerEmail.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_CustomerEmail.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_CustomerEmail.toString()):""%>"/>Customer Email</li>
                                                    <input class="id_1" type="hidden" id="Transactions_CustomerEmail_hidden" name='<%=TemplatePreference.Transactions_CustomerEmail.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" class="id" id="Transactions_CustomerID" name='<%=TemplatePreference.Transactions_CustomerID.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_CustomerID.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_CustomerID.toString()):""%>"/>Customer ID</li>
                                                    <input class="id_1" type="hidden" id="Transactions_CustomerID_hidden" name='<%=TemplatePreference.Transactions_CustomerID.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" class="id" id="Transactions_PayMode" name='<%=TemplatePreference.Transactions_PayMode.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_PayMode.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_PayMode.toString()):""%>"/>Payment Mode</li>
                                                    <input class="id_1" type="hidden" id="Transactions_PayMode_hidden" name='<%=TemplatePreference.Transactions_PayMode.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" class="id" id="Transactions_CardType" name='<%=TemplatePreference.Transactions_CardType.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_CardType.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_CardType.toString()):""%>"/>Payment Brand</li>
                                                    <input class="id_1" type="hidden" id="Transactions_CardType_hidden" name='<%=TemplatePreference.Transactions_CardType.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" class="id" id="Transactions_IssuingBank" name='<%=TemplatePreference.Transactions_IssuingBank.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_IssuingBank.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_IssuingBank.toString()):""%>"/>Issuing Bank</li>
                                                    <input class="id_1" type="hidden" id="Transactions_IssuingBank_hidden" name='<%=TemplatePreference.Transactions_IssuingBank.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" class="id" id="Transactions_Amount" name='<%=TemplatePreference.Transactions_Amount.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_Amount.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_Amount.toString()):""%>"/>Amount</li>
                                                    <input class="id_1" type="hidden" id="Transactions_Amount_hidden" name='<%=TemplatePreference.Transactions_Amount.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" class="id" id="Transactions_RefundedAmt" name='<%=TemplatePreference.Transactions_RefundedAmt.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_RefundedAmt.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_RefundedAmt.toString()):""%>"/>Refund Amount</li>
                                                    <input class="id_1" type="hidden" id="Transactions_RefundedAmt_hidden" name='<%=TemplatePreference.Transactions_RefundedAmt.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" class="id" id="Transactions_Currency" name='<%=TemplatePreference.Transactions_Currency.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_Currency.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_Currency.toString()):""%>"/>Currency</li>
                                                    <input class="id_1" type="hidden" id="Transactions_Currency_hidden" name='<%=TemplatePreference.Transactions_Currency.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" class="id" id="Transactions_Status" name='<%=TemplatePreference.Transactions_Status.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_Status.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_Status.toString()):""%>"/>Status</li>
                                                    <input class="id_1" type="hidden" id="Transactions_Status_hidden" name='<%=TemplatePreference.Transactions_Status.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" class="id" id="Transactions_Remark" name='<%=TemplatePreference.Transactions_Remark.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_Remark.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_Remark.toString()):""%>"/>Remark</li>
                                                    <input class="id_1" type="hidden" id="Transactions_Remark_hidden" name='<%=TemplatePreference.Transactions_Remark.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" class="id" id="Transactions_Terminal" name='<%=TemplatePreference.Transactions_Terminal.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_Terminal.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_Terminal.toString()):""%>"/>Terminal ID</li>
                                                    <input class="id_1" type="hidden" id="Transactions_Terminal_hidden" name='<%=TemplatePreference.Transactions_Terminal.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" class="id" id="Transactions_LastUpdateDate" name='<%=TemplatePreference.Transactions_LastUpdateDate.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_LastUpdateDate.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_LastUpdateDate.toString()):""%>"/>Last Update Date</li>
                                                    <input class="id_1" type="hidden" id="Transactions_LastUpdateDate_hidden" name='<%=TemplatePreference.Transactions_LastUpdateDate.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" class="id" id="Transactions_Mode" name='<%=TemplatePreference.Transactions_Mode.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_Mode.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_Mode.toString()):""%>"/>Transactions Mode</li>
                                                    <input class="id_1" type="hidden" id="Transactions_Mode_hidden" name='<%=TemplatePreference.Transactions_Mode.toString()%>' value>
                                                    <br>

                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>" name="desc">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>" name="trackingid">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(status)%>" name="status">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromdate)%>" name="fdate">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(todate)%>" name="tdate">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(startTime)%>" name="starttime">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(endTime)%>" name="endtime">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(dateType)%>" name="datetype">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(statusflag)%>" name="statusflag">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(firstsix)%>" name="firstsix">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(lastfour)%>" name="lastfour">
                                                    <input type="hidden" value="<%=archive%>" name="archive">
                                                    <input type="hidden" value="<%=customerId%>" name="customerid">
                                                    <input type="hidden" value="<%=firstName%>" name="firstname">
                                                    <input type="hidden" value="<%=lastName%>" name="lastname">
                                                    <input type="hidden" value="<%=emailAddress%>" name="emailaddr">
                                                    <input type="hidden" value="<%=terminalid%>" name="terminalid">
                                                    <input type="hidden" value="<%=terminalBuffer%>" name="terminalbuffer">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(bank)%>" name="bank">
                                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">

                                                    <button type="submit" value="Save" class="buttonform" name="Save" id="Save" onclick="mySave()">
                                                        <%=transactions_Save%>
                                                    </button>
                                                </ul>

                                            </li>
                                        </ul>
                                    </ul>
                                </div>

                            </form>
                            <div class="pull-right">
                                <div class="btn-group">
                                    <form name ="exportHistory" action="ExportActionHistory?ctoken=<%=ctoken%>" method="post" style="padding-left: 6px;" >
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>" name="trackingid">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromdate)%> " name="fdate">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(todate)%>" name="tdate">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(status)%>" name="status">
                                        <button type="submit" name="exportHistory" value="" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-history" aria-hidden="true"></i>
                                            Export History</button>

                                    </form>
                                </div>
                            </div>
                            <form name="exportform" method="post" action="ExportTransactions?ctoken=<%=ctoken%>" >
                                <div class="pull-right">
                                    <div class="btn-group">


                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>" name="desc">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>" name="trackingid">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(status)%>" name="status">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromdate)%>" name="fdate">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(todate)%>" name="tdate">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(startTime)%>" name="starttime">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(endTime)%>" name="endtime">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(dateType)%>" name="datetype">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(flaghash)%>" name="statusflag">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(firstsix)%>" name="firstsix">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(lastfour)%>" name="lastfour">
                                        <input type="hidden" value="<%=archive%>" name="archive">
                                        <input type="hidden" value="<%=customerId%>" name="customerid">
                                        <input type="hidden" value="<%=firstName%>" name="firstname">
                                        <input type="hidden" value="<%=lastName%>" name="lastname">
                                        <input type="hidden" value="<%=emailAddress%>" name="emailaddr">
                                        <input type="hidden" value="<%=terminalid%>" name="terminalid">
                                        <input type="hidden" value="<%=timezone%>" name="timezone">
                                        <input type="hidden" value="<%=terminalBuffer%>" name="terminalbuffer">
                                        <input type="hidden" value="<%=issuingBank%>" name="issuingbank">
                                        <input type="hidden" value="<%=cardtype%>" name="cardtype">
                                        <input type="hidden" value="<%=transactionModeStr%>" name="transactionMode">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(bank)%>" name="bank">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <button type="submit" name="Excel" id="Excel" onclick="myExcel()" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-file-excel-o"></i>&nbsp;&nbsp<%=transactions_Export_To_Excel%></button>

                                    </div>
                                </div>
                            </form>
                            <div class="widget-content padding" id="table_div" style="overflow-x: auto;">

                                <table id="myTable" class="display table table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    <tr style="color: white;">
                                        <th class="Transactions_Date" style="text-align: -webkit-center;"><%=transactions_Transaction_Date%></th>
                                        <%
                                            if("CP".equals(cardtype))
                                            {%>
                                        <th class="Transactions_Date" style="text-align: -webkit-center;">Transaction Fetched Date</th>
                                        <%
                                            }
                                        %>

                                        <th class="Transactions_TimeZone" style="text-align: -webkit-center;">
                                            <%=timezone =="" || timezone ==null?"TimeZone":timezone%>
                                        </th>
                                        <th class="Transactions_TrackingID" style="text-align: -webkit-center;padding-right: 40px;"><%=transactions_TrackingID%></th>
                                        <th width="0%"></th>
                                        <th class="Transactions_OrderId" style="text-align: -webkit-center;"><%=transactions_OrderID%></th>
                                        <th class="Transactions_OrderDescription" style="text-align: -webkit-center;"><%=transactions_OrderDescription%></th>
                                        <th class="Transactions_CardHoldername" style="text-align: -webkit-center;"><%=transactions_card%></th>
                                        <th class="Transactions_CustomerEmail" style="text-align: -webkit-center;"><%=transactions_CustomerEmail%></th>
                                        <th class="Transactions_CustomerID" style="text-align: -webkit-center;"><%=transactions_CustomerID%></th>
                                        <th class="Transactions_PayMode" style="text-align: -webkit-center;"><%=transactions_PaymentMode%></th>
                                        <th class="Transactions_CardType" style="text-align: -webkit-center;"><%=transactions_PaymentBrand%></th>
                                        <th class="Transactions_Amount" style="text-align: -webkit-center;"><%=transactions_Amount%></th>
                                        <th class="Transactions_IssuingBank" style="text-align: -webkit-center;"><%=transactions_IssuingBank%></th>
                                        <th class="Transactions_RefundedAmt" style="text-align: -webkit-center;"><%=transactions_RefundAmount%></th>
                                        <th class="Transactions_Currency" style="text-align: -webkit-center;"><%=transactions_Currency%></th>
                                        <th class="Transactions_Status" style="text-align: -webkit-center;"><%=transactions_Status%></th>
                                        <th class="Transactions_Remark" style="text-align: -webkit-center;"><%=transactions_Remark%></th>
                                        <th class="Transactions_Terminal" style="text-align: -webkit-center;"><%=transactions_TerminalID%></th>
                                        <th class="Transactions_Mode" style="text-align: -webkit-center;"><%=transactions_mode%></th>
                                        <th class="Transactions_LastUpdateDate" style="text-align: -webkit-center;"><%=transactions_Last_Update_Date%></th>
                                        <%--<th class="First_Six" style="text-align: -webkit-center;">First Six</th>
                                        <th class="Last_Four" style="text-align: -webkit-center;">Last Four</th>--%>
                                    </tr>
                                    </thead>
                                    <tbody>
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
                                            {
                                                requestParameter.append("<input type='hidden' name='"+name+"' value=\""+request.getParameter(name)+"\"/>");
                                            }

                                        }
                                        requestParameter.append("<input type='hidden' name='SPageno' name='SPageno' value='"+pageno+"'/>");
                                        requestParameter.append("<input type='hidden' name='SRecords' value='"+pagerecords+"'/>");

                                        for (int pos = 1; pos <= records; pos++)
                                        {
                                            String id = Integer.toString(pos);
                                            int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);

                                            style = "class=\"tr" + (pos + 1) % 2 + "\"";
                                            temphash = (Hashtable) hash.get(id);

                                            // Functions functions     = new Functions();
                                            String date1            = Functions.convertDtstampToDateTime((String) temphash.get("dtstamp"));
                                            String description      = (String) temphash.get("description");
                                            String orderDescription = (String) temphash.get("orderdescription");
                                            String icicitransid     = (String) temphash.get("transid");
                                            String name             = (String) temphash.get("name");
                                            ctoken                  = request.getParameter("ctoken");
                                            if (name == null) name = "-";
                                            String amount = (String) temphash.get("amount");
                                            //String fromtype = (String) temphash.get("fromtype");
                                            String refundamount     = (String) temphash.get("refundamount");
                                            String tempstatus       = (String) sortedMap.get((String) temphash.get("status"));
                                            String paymodeid        = (String) temphash.get("paymodeid");
                                            String cardtypeid       = (String) temphash.get("cardtypeid");
                                            //String cardtype = (String) temphash.get("cardtype");
                                            String transCurrency    = (String) temphash.get("currency");
                                            String issuing_bank     = (String) temphash.get("issuing_bank");
                                            if (functions.isEmptyOrNull(issuing_bank)) issuing_bank = "-";

                                            if ("JPY".equalsIgnoreCase(transCurrency))
                                            {
                                                amount          = functions.printNumber(Locale.JAPAN, amount);
                                                refundamount    = functions.printNumber(Locale.JAPAN, refundamount);
                                            }
                                            else if ("EUR".equalsIgnoreCase(transCurrency))
                                            {
                                                amount          = functions.printNumber(Locale.FRANCE, amount);
                                                refundamount    = functions.printNumber(Locale.FRANCE, refundamount);
                                            }
                                            else if ("GBP".equalsIgnoreCase(transCurrency))
                                            {
                                                amount          = functions.printNumber(Locale.UK, amount);
                                                refundamount    = functions.printNumber(Locale.UK, refundamount);
                                            }
                                            else if ("USD".equalsIgnoreCase(transCurrency))
                                            {
                                                amount          = functions.printNumber(Locale.US, amount);
                                                refundamount    = functions.printNumber(Locale.US, refundamount);
                                            }
                                            String accountid        = (String) temphash.get("accountid");
                                            String tempterminalid   = (String) temphash.get("terminalid");
                                            String customerId1      = (String) temphash.get("customerId");
                                            String customerEmail    = (String) temphash.get("emailaddr");
                                            String lastUpdateDate   = (String)temphash.get("timestamp");
                                            String remark1          = (String) temphash.get("remark");
                                            if (tempterminalid == null) tempterminalid = "-";

                                            String tz1 = "-";

                                            String transactionMode = (String) temphash.get("transaction_mode");
                                            if (!functions.isValueNull(transactionMode)){
                                                transactionMode = "--";
                                            }

                                            /*String firstsix1=(String) temphash.get("firstsix1");
                                            if (firstsix1==null) firstsix1 = "-";

                                            String lastfour1=(String) temphash.get("lastfour1");
                                            if (lastfour1==null) lastfour1 = "-";*/
                                           /* String dt = (String) temphash.get("timestamp");
                                            if (functions.isValueNull(timezone))
                                            {
                                                //timezone = timezone.substring(0,timezone.indexOf("|"));
                                                if (timezone.indexOf("|")!= -1)
                                                {
                                                    tz1 = functions.convertDateTimeToTimeZone(dt, timezone.substring(0,timezone.indexOf("|")));
                                                }
                                                else
                                                {
                                                    tz1 = functions.convertDateTimeToTimeZone(dt, timezone);
                                                }
                                            }*/
                                            String dt = Functions.convertDtstampToDateTimeforTimezone((String) temphash.get("dtstamp"));

                                            if (functions.isValueNull(timezone))
                                            {
                                                if (timezone.indexOf("|")!= -1)
                                                {
                                                    tz1 = functions.convertDateTimeToTimeZone(dt, timezone.substring(0,timezone.indexOf("|")));
                                                }
                                                else
                                                {
                                                    tz1 = functions.convertDateTimeToTimeZone(dt, timezone);
                                                }
                                            }


                                            out.println("<tr " + style + ">");

                                            if("CP".equals(cardtype))
                                            {
                                                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                                                java.util.Date t    = ft.parse((String) temphash.get("transactionTime"));
                                                ft.applyPattern("d MMM yyyy HH:mm:ss");
                                                out.println("<td valign=\"middle\" data-label=\"Date\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(ft.format(t)) + "</td>");
                                            }else{
                                                out.println("<td data-label=\"Date\" class=\"Transactions_Date\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(date1) + "</td>");
                                            }
                                            if("CP".equals(cardtype))
                                            {
                                                out.println("<td valign=\"middle\" data-label=\"Date\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(date1) + "</td>");
                                            }

                                            out.println("<td data-label=\"Time Zone\" class=\"Transactions_TimeZone\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(tz1) + "</td>");
                                            out.println("<td data-label=\"Tracking ID\" class=\"Transactions_TrackingID\" align=\"center\"" + style + "><form action=\"TransactionDetails?ctoken=" + ctoken + "\" method=\"post\" ><input type=\"hidden\" name=\"accountid\" value=\"" + accountid + "\"><input type=\"hidden\" name=\"cardtypeid\" value=\"" + cardtypeid + "\"><input type=\"hidden\" name=\"trackingid\" value=\"" + icicitransid + "\"><input type=\"hidden\" name=\"ctoken\" value=\"" + ctoken + "\"><input type=\"hidden\" name=\"archive\" value=\"" + archive + "\"><input type=\"hidden\" name=\"tStatus\" value=\"" + tempstatus + "\"><input type=\"hidden\" name=\"buttonValue\" value=\"transaction\"><input type=\"hidden\" name=\"terminalbuffer\" value=\"" + terminalBuffer.toString() + "\"><input type=\"hidden\" name=\"cardtype\" value=" + cardtype + "><input type=\"submit\" class=\"btn btn-default\" name=\"submit\"  value=\"" + icicitransid +  "\">");
                                            out.println("<td valign=\"middle\" width=\"0%\" align=\"center\" " + style + "><span title=\"copy\" onclick=\"copyToClipboard("+icicitransid+")\" ><i style=\"position: relative;left: -13px;top: 11px;\" class=\"fa-solid fa-copy\"></i></span></td>");
                                            out.println(requestParameter.toString());
                                            out.print("</form></td>");
                                            out.println("<td data-label=\"Description\" class=\"Transactions_OrderId\" align=\"center\">" + ESAPI.encoder().encodeForHTML(description) + "</td>");
                                            if (orderDescription == null || orderDescription.equals(""))
                                            {
                                                out.println("<td data-label=\"Order Description\" class=\"Transactions_OrderDescription\" align=\"center\"" + style + ">" + "-" + "</td>");
                                            }
                                            else
                                            {
                                                out.println("<td data-label=\"Order Description\" class=\"Transactions_OrderDescription\" align=\"center\">" + ESAPI.encoder().encodeForHTML(orderDescription) + "</td>");
                                            }
                                            out.println("<td data-label=\"Card Holder's Name\" class=\"Transactions_CardHoldername\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(name) + "</td>");
                                            out.println("<td data-label=\"Customer Email\" class=\"Transactions_CustomerEmail\" align=\"center\"" + style + ">" + functions.getEmailMasking(customerEmail) + "</td>");
                                            if (!functions.isValueNull(customerId1))
                                            {
                                                out.println("<td data-label=\"Customer ID\" class=\"Transactions_CustomerID\" align=\"center\"" + style + ">" +"-"+ "</td>");
                                            }
                                            else
                                            {
                                                out.println("<td data-label=\"Customer ID\" class=\"Transactions_CustomerID\" align=\"center\"" + style + ">" + functions.maskVpaAddress(customerId1) + "</td>");
                                               // out.println("<td data-label=\"Customer ID\" class=\"Transactions_CustomerID\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(customerId1) + "</td>");
                                            }
                                            if (!functions.isValueNull(paymodeid) || "0".equals(paymodeid))
                                            {
                                                out.println("<td data-label=\"PayMode\" class=\"Transactions_PayMode\" align=\"center\"" + style + ">" + "-" + "</td>");
                                            }
                                            else
                                            {
                                                out.println("<td data-label=\"PayMode\" class=\"Transactions_PayMode\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(paymodeid) + "</td>");
                                            }

                                            if (!functions.isValueNull(cardtypeid))
                                            {
                                                out.println("<td data-label=\"CardType\" class=\"Transactions_CardType\" align=\"center\"" + style + ">&nbsp;" + "-" + "</td>");
                                            }
                                            else
                                            {
                                                cardtypeid = GatewayAccountService.getCardType(cardtypeid);

                                                String propFileName = ApplicationProperties.getProperty("PARTNER_LOGO_PATH_MERCHANT_INTERFACE");

                                                File file = new File(propFileName + ESAPI.encoder().encodeForHTML(cardtypeid) + ".png");

                                                if (functions.isValueNull(cardtypeid) && file.exists())
                                                {
                                                    out.println("<td data-label=\"CardType\" class=\"Transactions_CardType\" align=\"center\"" + style + "> <img src=\"/merchant/images/cardtype/" + ESAPI.encoder().encodeForHTML(cardtypeid)+".png\" style=\"height:57%;\"></td>");
                                                }
                                                else if(functions.isValueNull(cardtypeid) && !file.exists())
                                                {
                                                    out.println("<td data-label=\"CardType\" class=\"Transactions_CardType\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(cardtypeid) + "</td>");
                                                }
                                                else
                                                {
                                                    out.println("<td data-label=\"CardType\" class=\"Transactions_CardType\" align=\"center\"" + style + ">&nbsp;" + "-" + "</td>");
                                                }
                                            }
                                            out.println("<td data-label=\"Amount\" class=\"Transactions_Amount\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(amount) + "</td>");
                                            out.println("<td data-label=\"Issuing Bank\" class=\"Transactions_IssuingBank\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(issuing_bank) + "</td>");
                                            out.println("<td data-label=\"Refund Amount\" class=\"Transactions_RefundedAmt\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(refundamount) + "</td>");
                                            if (transCurrency == null || transCurrency.equals(""))
                                            {
                                                out.println("<td data-label=\"Currency\" class=\"Transactions_Currency\" align=\"center\"" + style + ">" + "" + "</td>");

                                            }
                                            else
                                            {
                                                out.println("<td data-label=\"Currency\" class=\"Transactions_Currency\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(transCurrency) + "</td>");
                                            }

                                            out.println("<td valign=\"middle\" data-label=\"Status\" class=\"Transactions_Status\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(tempstatus) + "</td>");

                                            if (!functions.isValueNull(remark1))
                                            {
                                                out.println("<td data-label=\"Remark\" class=\"Transactions_Remark\" align=\"center\"" + style + ">" +"-"+ "</td>");
                                            }
                                            else
                                            {
                                                out.println("<td valign=\"middle\" data-label=\"Remark\" class=\"Transactions_Remark\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(remark1) + "</td>");
                                            }

                                            if (!functions.isValueNull(tempterminalid) || tempterminalid.equals("0") || tempterminalid == null)
                                            {
                                                out.println("<td valign=\"middle\" data-label=\"Terminal ID\" class=\"Transactions_Terminal\" align=\"center\"" + style + ">" + "-" + "</td>");
                                            }
                                            else
                                            {
                                                out.println("<td valign=\"middle\" data-label=\"Terminal ID\" class=\"Transactions_Terminal\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(tempterminalid) + "</td>");
                                            }
                                            out.println("<td valign=\"middle\" data-label=\"Terminal ID\" class=\"Transactions_Mode\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(transactionMode) + " </td>");
                                            out.println("<td valign=\"middle\" data-label=\"Last Update Date\" class=\"Transactions_LastUpdateDate\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(lastUpdateDate) + "</td>");
                                            //out.println("<td valign=\"middle\" data-label=\"First Six\" class=\"First_Six\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(firstsix1) + "</td>");
                                            //out.println("<td valign=\"middle\" data-label=\"Last Four\" class=\"Last_Four\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(lastfour1) + "</td>");
                                            out.println("</tr>");

                                            session.setAttribute("TrackingIDList", trackingList);
                                        }
                                    %>
                                    </tbody>
                                </table>
                            </div>
                            <%
                                int TotalPageNo;
                                if(totalrecords%pagerecords!=0)
                                {
                                    TotalPageNo =totalrecords/pagerecords+1;
                                }
                                else
                                {
                                    TotalPageNo=totalrecords/pagerecords;
                                }
                            %>

                            <div id="showingid"><strong><%=transactions_page_no%> <%=pageno%> of <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
                            <div id="showingid"><strong><%=transactions_total_no_of_records%>   <%=totalrecords%> </strong></div>


                            <jsp:include page="page.jsp" flush="true">
                                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                                <jsp:param name="pageno" value="<%=pageno%>"/>
                                <jsp:param name="str" value="<%=str%>"/>
                                <jsp:param name="page" value="Transactions"/>
                                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                                <jsp:param name="orderby" value=""/>
                            </jsp:include>

                            <%
                                    }
                                }
                                else
                                {
                                    out.println(Functions.NewShowConfirmation1(transactions_sorry, transactions_no_records));
                                }

                            %>

                        </div>
                    </div>
                </div>
            </div>

            <%
                }
                else
                {
                    out.println(Functions.NewShowConfirmation1(transactions_filter,transactions_search1));
                }
            %>

        </div>
    </div>
</div>
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




