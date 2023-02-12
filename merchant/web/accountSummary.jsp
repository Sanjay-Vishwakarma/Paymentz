<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Merchants" %>
<%--
  Created by IntelliJ IDEA.
  User: Sanjay
  Date: 2/14/2022
  Time: 1:06 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="ietest.jsp" %>
<%@ include file="Top.jsp" %>

<%
    String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("company"));
    session.setAttribute("submit", "Account Summary");
%>
<%!
    private static Logger logger = new Logger("accountSummary.jsp");
    Functions function = new Functions();
    TerminalManager terminalManager = new TerminalManager();
%>
<%

    HashMap<String, String> timezoneHash = new HashMap<>();
    timezoneHash.put("Etc/GMT+12|(GMT-12:00)", "(GMT-12:00) International Date Line West");
    timezoneHash.put("Pacific/Midway|(GMT-11:00", "(GMT-11:00) Midway Island, Samoa");
    timezoneHash.put("Pacific/Honolulu|(GMT-10:00)", "(GMT-10:00) Hawaii");
    timezoneHash.put("US/Alaska|(GMT-09:00)", "(GMT-09:00) Alaska");
    timezoneHash.put("America/Los_Angeles|(GMT-08:00)", "(GMT-08:00) Pacific Time (US & Canada)");
    timezoneHash.put("America/Tijuana|(GMT-08:00)", "(GMT-08:00) Tijuana, Baja California");
    timezoneHash.put("US/Arizona|(GMT-07:00)", "(GMT-07:00) Arizona");
    timezoneHash.put("America/Chihuahua|(GMT-07:00)", "(GMT-07:00) Chihuahua, La Paz, Mazatlan");
    timezoneHash.put("US/Mountain|(GMT-07:00)", "(GMT-07:00) Mountain Time (US & Canada)");
    timezoneHash.put("America/Managua|(GMT-06:00)", "(GMT-06:00) Central America");
    timezoneHash.put("US/Central|(GMT-06:00)", "(GMT-06:00) Central Time (US & Canada)");
    timezoneHash.put("America/Mexico_City|(GMT-06:00)", "(GMT-06:00) Guadalajara, Mexico City, Monterrey");
    timezoneHash.put("Canada/Saskatchewan|(GMT-06:00)", "(GMT-06:00) Saskatchewan");
    timezoneHash.put("America/Bogota|(GMT-05:00)", "(GMT-05:00) Bogota, Lima, Quito, Rio Branco");
    timezoneHash.put("US/Eastern|(GMT-05:00)", "(GMT-05:00) Eastern Time (US & Canada)");
    timezoneHash.put("US/East-Indiana|(GMT-05:00)", "(GMT-05:00) Indiana (East)");
    timezoneHash.put("Canada/Atlantic|(GMT-04:00)", "(GMT-04:00) Atlantic Time (Canada)");
    timezoneHash.put("America/Caracas|(GMT-04:00)", "(GMT-04:00) Caracas, La Paz");
    timezoneHash.put("America/Manaus|(GMT-04:00)", "(GMT-04:00) Manaus");
    timezoneHash.put("America/Santiago|(GMT-04:00)", "(GMT-04:00) Santiago");
    timezoneHash.put("Canada/Newfoundland|(GMT-03:30)", "(GMT-03:30) Newfoundland");
    timezoneHash.put("America/Sao_Paulo|(GMT-03:00)", "(GMT-03:00) Brasilia");
    timezoneHash.put("America/Argentina/Buenos_Aires|(GMT-03:00)", "(GMT-03:00) Buenos Aires, Georgetown");
    timezoneHash.put("America/Godthab|(GMT-03:00)", "(GMT-03:00) Greenland");
    timezoneHash.put("America/Montevideo|(GMT-03:00)", "(GMT-03:00) Montevideo");
    timezoneHash.put("America/Noronha|(GMT-02:00)", "(GMT-02:00) Mid-Atlantic");
    timezoneHash.put("Atlantic/Cape_Verde|(GMT-01:00)", "(GMT-01:00) Cape Verde Is.");
    timezoneHash.put("Atlantic/Azores|(GMT-01:00)", "(GMT-01:00) Azores");
    timezoneHash.put("Africa/Casablanca|(GMT+00:00)", "(GMT+00:00) Casablanca, Monrovia, Reykjavik");
    timezoneHash.put("Etc/Greenwich|(GMT+00:00)", "(GMT+00:00) Greenwich Mean Time : Dublin, Edinburgh, Lisbon, London");
    timezoneHash.put("Europe/Amsterdam|(GMT+01:00)", "(GMT+01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna");
    timezoneHash.put("Europe/Belgrade|(GMT+01:00)", "(GMT+01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague");
    timezoneHash.put("Europe/Brussels|(GMT+01:00)", "(GMT+01:00) Brussels, Copenhagen, Madrid, Paris");
    timezoneHash.put("Europe/Sarajevo|(GMT+01:00)", "(GMT+01:00) Sarajevo, Skopje, Warsaw, Zagreb");
    timezoneHash.put("Africa/Lagos|(GMT+01:00)", "(GMT+01:00) West Central Africa");
    timezoneHash.put("Asia/Amman|(GMT+02:00)", "(GMT+02:00) Amman");
    timezoneHash.put("Europe/Athens|(GMT+02:00)", "(GMT+02:00) Athens, Bucharest, Istanbul");
    timezoneHash.put("Asia/Beirut|(GMT+02:00)", "(GMT+02:00) Beirut");
    timezoneHash.put("Africa/Cairo|(GMT+02:00)", "(GMT+02:00) Cairo");
    timezoneHash.put("Africa/Harare|(GMT+02:00)", "(GMT+02:00) Harare, Pretoria");
    timezoneHash.put("Europe/Helsinki|(GMT+02:00)", "(GMT+02:00) Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius");
    timezoneHash.put("Asia/Jerusalem|(GMT+02:00)", "(GMT+02:00) Jerusalem");
    timezoneHash.put("Europe/Minsk|(GMT+02:00)", "(GMT+02:00) Minsk");
    timezoneHash.put("Africa/Windhoek|(GMT+02:00)", "(GMT+02:00) Windhoek");
    timezoneHash.put("Asia/Kuwait|(GMT+03:00)", "(GMT+03:00) Kuwait, Riyadh, Baghdad");
    timezoneHash.put("Europe/Moscow|(GMT+03:00)", "(GMT+03:00) Moscow, St. Petersburg, Volgograd");
    timezoneHash.put("Africa/Nairobi|(GMT+03:00)", "(GMT+03:00) Nairobi");
    timezoneHash.put("Asia/Tbilisi|(GMT+03:00)", "(GMT+03:00) Tbilisi");
    timezoneHash.put("Asia/Tehran|(GMT+03:30)", "(GMT+03:30) Tehran");
    timezoneHash.put("Asia/Muscat|(GMT+04:00)", "(GMT+04:00) Abu Dhabi, Muscat");
    timezoneHash.put("Asia/Baku|(GMT+04:00)", "(GMT+04:00) Baku");
    timezoneHash.put("Asia/Yerevan|(GMT+04:00)", "(GMT+04:00) Yerevan");
    timezoneHash.put("Asia/Kabul|(GMT+04:30)", "(GMT+04:30) Kabul");
    timezoneHash.put("Asia/Yekaterinburg|(GMT+05:00)", "(GMT+05:00) Yekaterinburg");
    timezoneHash.put("Asia/Karachi|(GMT+05:00)", "(GMT+05:00) Islamabad, Karachi, Tashkent");
    timezoneHash.put("Asia/Calcutta|(GMT+05:30)", "(GMT+05:30) Chennai, Kolkata, Mumbai, New Delhi");
    timezoneHash.put("Asia/Katmandu|(GMT+05:45)", "(GMT+05:45) Kathmandu");
    timezoneHash.put("Asia/Almaty|(GMT+06:00)", "(GMT+06:00) Almaty, Novosibirsk");
    timezoneHash.put("Asia/Dhaka|(GMT+06:00)", "(GMT+06:00) Astana, Dhaka");
    timezoneHash.put("Asia/Rangoon|(GMT+06:30)", "(GMT+06:30) Yangon (Rangoon)");
    timezoneHash.put("Asia/Bangkok|(GMT+07:00)", "(GMT+07:00) Bangkok, Hanoi, Jakarta");
    timezoneHash.put("Asia/Krasnoyarsk|(GMT+07:00)", "(GMT+07:00) Krasnoyarsk");
    timezoneHash.put("Asia/Hong_Kong|(GMT+08:00)", "(GMT+08:00) Beijing, Chongqing, Hong Kong, Urumqi");
    timezoneHash.put("Asia/Kuala_Lumpur|(GMT+08:00)", "(GMT+08:00) Kuala Lumpur, Singapore");
    timezoneHash.put("Asia/Irkutsk|(GMT+08:00)", "(GMT+08:00) Irkutsk, Ulaan Bataar");
    timezoneHash.put("Australia/Perth|(GMT+08:00)", "(GMT+08:00) Perth");
    timezoneHash.put("Asia/Taipei|(GMT+08:00)", "(GMT+08:00) Taipei");
    timezoneHash.put("Asia/Tokyo|(GMT+09:00)", "(GMT+09:00) Osaka, Sapporo, Tokyo");
    timezoneHash.put("Asia/Seoul|(GMT+09:00)", "(GMT+09:00) Seoul");
    timezoneHash.put("Asia/Yakutsk|GMT+09:00)", "(GMT+09:00) Yakutsk");
    timezoneHash.put("Australia/Adelaide|(GMT+09:30)", "(GMT+09:30) Adelaide");
    timezoneHash.put("Australia/Darwin|(GMT+09:30)", "(GMT+09:30) Darwin");
    timezoneHash.put("Australia/Brisbane|(GMT+10:00)", "(GMT+10:00) Brisbane");
    timezoneHash.put("Australia/Canberra|(GMT+10:00)", "(GMT+10:00) Canberra, Melbourne, Sydney");
    timezoneHash.put("Australia/Hobart|(GMT+10:00)", "(GMT+10:00) Hobart");
    timezoneHash.put("Pacific/Guam|(GMT+10:00)", "(GMT+10:00) Guam, Port Moresby");
    timezoneHash.put("Asia/Vladivostok|(GMT+10:00)", "(GMT+10:00) Vladivostok");
    timezoneHash.put("Asia/Magadan|(GMT+11:00)", "(GMT+11:00) Magadan, Solomon Is., New Caledonia");
    timezoneHash.put("Pacific/Auckland|(GMT+12:00)", "(GMT+12:00) Auckland, Wellington");
    timezoneHash.put("Pacific/Fiji|(GMT+12:00)", "(GMT+12:00) Fiji, Kamchatka, Marshall Is.");
    timezoneHash.put("Pacific/Tongatapu|(GMT+13:00)", "(GMT+13:00) Nuku'alofa");


    String uId = "";
    if (session.getAttribute("role").equals("submerchant"))
    {
        uId = (String) session.getAttribute("userid");
    }
    else
    {
        uId = (String) session.getAttribute("merchantid");
    }


    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);

    Functions functions = new Functions();
    Merchants merchants1 = new Merchants();

    String accountSummary_terminal_allocated = !functions.isEmptyOrNull(rb1.getString("accountSummary_terminal_allocated")) ? rb1.getString("accountSummary_terminal_allocated") : "No Terminals Allocated";
    String accountSummary_select_terminal_id = !functions.isEmptyOrNull(rb1.getString("accountSummary_select_terminal_id")) ? rb1.getString("accountSummary_select_terminal_id") : "Select Terminal ID";
    String transactions_select_zone = !functions.isEmptyOrNull(rb1.getString("transactions_select_zone")) ? rb1.getString("transactions_select_zone") : "--Select Time Zone--";
    String transactionSummary_sorry = !functions.isEmptyOrNull(rb1.getString("transactionSummary_sorry")) ? rb1.getString("transactionSummary_sorry") : "Sorry";
    String transactionSummary_status = !functions.isEmptyOrNull(rb1.getString("transactionSummary_status")) ? rb1.getString("transactionSummary_status") : " No records found for given search criteria.";
    String transactionSummary_from_date = !functions.isEmptyOrNull(rb1.getString("transactionSummary_from_date")) ? rb1.getString("transactionSummary_from_date") : "From";
    String transactionSummary_to_date = !functions.isEmptyOrNull(rb1.getString("transactionSummary_to_date")) ? rb1.getString("transactionSummary_to_date") : "To";
    String transactionSummary_terminal_id1 = !functions.isEmptyOrNull(rb1.getString("transactionSummary_terminal_id1")) ? rb1.getString("transactionSummary_terminal_id1") : "Terminal ID*";
    String transactionSummary_currency = !functions.isEmptyOrNull(rb1.getString("transactionSummary_currency")) ? rb1.getString("transactionSummary_currency") : "Currency";
    String transactionSummary_Search1 = !functions.isEmptyOrNull(rb1.getString("transactionSummary_Search1")) ? rb1.getString("transactionSummary_Search1") : "Search";
    String transactionSummary_Search2 = !functions.isEmptyOrNull(rb1.getString("transactionSummary_Search2")) ? rb1.getString("transactionSummary_Search2") : "Search";
    String transactionSummary_account_summary = !functions.isEmptyOrNull(rb1.getString("transactionSummary_account_summary")) ? rb1.getString("transactionSummary_account_summary") : "Account Summary";
    String transactionSummary_timezone = !functions.isEmptyOrNull(rb1.getString("transactionSummary_timezone")) ? rb1.getString("transactionSummary_timezone") : "Timezone";
    String fromdate = null;
    String todate = null;
    Date date = new Date();
    SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

    String Date = originalFormat.format(date);
    date.setDate(1);
    String fromDate = originalFormat.format(date);


    fromdate = Functions.checkStringNull(request.getParameter("fdate")) == null ? fromDate : request.getParameter("fdate");
    todate = Functions.checkStringNull(request.getParameter("tdate")) == null ? Date : request.getParameter("tdate");
    String startTime = Functions.checkStringNull(request.getParameter("starttime"));
    String endTime = Functions.checkStringNull(request.getParameter("endtime"));
    String timezone = Functions.checkStringNull(request.getParameter("timezone")) == null ? "" : request.getParameter("timezone");
    String currency = Functions.checkStringNull(request.getParameter("currency")) == null ? "" : request.getParameter("currency");
    String str = "";

    String terminalid = "";
    if (request.getAttribute("terminalid") != null)
    {
        terminalid = request.getAttribute("terminalid").toString();
    }
    if (timezone != null) str = str + "&timezone=" + timezone;
    else
        timezone = "";

    if (currency != null) str = str + "&currency=" + currency;
    else
        currency = "";

    if (startTime == null) startTime = "00:00:00";
    if (endTime == null) endTime = "23:59:59";

    Calendar rightNow = Calendar.getInstance();

    if (fromdate == null) fromdate = "" + 1;
    if (todate == null) todate = "" + rightNow.get(Calendar.DATE);

    if (fromdate != null) str = str + "fdate=" + fromdate;
    if (todate != null) str = str + "&tdate=" + todate;
    if (startTime != null) str = str + "&starttime=" + startTime;
    if (endTime != null) str = str + "&endtime=" + endTime;
    Hashtable innerhash = new Hashtable();
    Hashtable innerhash1 = new Hashtable();
    Hashtable innerhash2 = new Hashtable();
    Hashtable dataHash = (Hashtable) request.getAttribute("bankdetails");
    Hashtable hashRecived = (Hashtable) request.getAttribute("queryReceived");
    Hashtable hashPayout = (Hashtable) request.getAttribute("payoutDetails");
    Hashtable hashChargeback = (Hashtable) request.getAttribute("chargeback");
    logger.error("datahash--->" + dataHash);
    logger.error("hashRecived--->" + hashRecived);
    logger.error("hashPayout--->" + hashPayout);
    logger.error("hashChargeback--->" + hashChargeback);

    String style = "class=\"tr0\"";
    String error = (String) request.getAttribute("error");

%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><%=company%> Merchant Account Details > Account Summary</title>

    <script type="text/javascript">
        $('#sandbox-container input').datepicker({});
    </script>
    <script>
        $(function ()
        {
            $(".datepicker").datepicker({dateFormat: "yy-mm-dd"});
            /* $(".datepicker").datepicker({}).datepicker("setValue",new Date);*/
        });
    </script>

    <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
    <script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <script src="/merchant/NewCss/js/jquery-ui.min.js"></script>

    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>

    <script src="/merchant/datepicker/datetimepicker/moment-with-locales.js"></script>
    <script src="/merchant/datepicker/datetimepicker/bootstrap-datetimepicker.min.js"></script>
    <script src="/merchant/transactionCSS/js/transactions.js"></script>

</head>
<body>

<div class="content-page">
    <div class="content">
        <%--Page heading--%>
        <div class="page-heading">
            <div class="row">

                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=transactionSummary_account_summary%></strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <form name="form" method="post"
                              action="/merchant/servlet/AccountSummary?ctoken=<%=ctoken%>">
                            <input type="hidden" name="ctoken" value="<%=ctoken%>" id="ctoken">
                            <input type="hidden" name="merchantid" vlaue="<%=uId%>" id="merchantid">
                            <%
                                String dateRangeIsValid = (String) request.getAttribute("dateRangeIsValid");
                                String terminalCurrency = "";
                                StringBuffer terminalBuffer = new StringBuffer();
                                if (functions.isValueNull(error))
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error +" "+dateRangeIsValid+"</h5>");
                                }
                                else if(functions.isValueNull(dateRangeIsValid))
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+dateRangeIsValid+"</h5>");

                                }
                            %>
                            <div class="widget-content padding">
                                <div id="horizontal-form">
                                    <div class="form-group col-xs-12 col-sm-6 col-md-3 col-lg-3">
                                        <div class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                                            <label><%=transactionSummary_from_date%></label>
                                            <input type="text" name="fdate" id="From_dt" class="datepicker form-control"
                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromdate)%>"
                                                   readonly="readonly"
                                                   style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">
                                        </div>

                                        <div <%--id="From_div"--%>
                                                class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-4"
                                                style="padding: 0;/*width: inherit;*/">
                                            <div class="form-group">
                                                <label class="hide_label">&nbsp;</label>

                                                <div class='input-group date'>
                                                    <input type='text' id='datetimepicker12' class="form-control"
                                                           placeholder="HH:MM:SS" name="starttime" maxlength="8"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute(startTime)%>"
                                                           style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-6 col-md-3 col-lg-3">

                                        <div class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                                            <label><%=transactionSummary_to_date%></label>
                                            <input type="text" name="tdate" id="To_dt" class="datepicker form-control"
                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(todate)%>"
                                                   readonly="readonly"
                                                   style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">
                                        </div>

                                        <div class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-4"
                                             style="padding: 0;/*width: inherit;*/">
                                            <div class="form-group">
                                                <label class="hide_label">&nbsp;</label>

                                                <div class='input-group date'>
                                                    <input type='text' id='datetimepicker13' class="form-control"
                                                           placeholder="HH:MM:SS" name="endtime" maxlength="8"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute(endTime)%>"
                                                           style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=transactionSummary_terminal_id1%></label>
                                        <select size="1" name="terminalid" class="form-control">
                                            <%--<option value="all">All</option>--%>
                                            <%
                                                terminalBuffer.append("(");
                                                TerminalManager terminalManager = new TerminalManager();
                                                List<TerminalVO> terminalVOList = terminalManager.getMemberandUserTerminalList(uId, String.valueOf(session.getAttribute("role")));
                                                if (terminalVOList.size() > 0)
                                                {
                                            %>
                                            <option value=""><%=accountSummary_select_terminal_id%>
                                            </option>
                                            <%
                                                for (TerminalVO terminalVO : terminalVOList)
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
                                                    if (terminalid.equals(terminalVO.getTerminalId()))
                                                    {
                                                        terminalCurrency = terminalVO.getCurrency();
                                                        str1 = "selected";
                                                        terminalid = terminalVO.getTerminalId();
                                                    }
                                                    if (terminalBuffer.length() != 0 && terminalBuffer.length() != 1)
                                                    {
                                                        terminalBuffer.append(",");
                                                    }
                                                    terminalBuffer.append(terminalVO.getTerminalId());
                                            %>
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(terminalVO.getTerminalId())%>" <%=str1%>><%=ESAPI.encoder().encodeForHTML(terminalVO.getTerminalId() + "-" + terminalVO.getPaymentName() + "-" + terminalVO.getCardType() + "-" + terminalVO.getCurrency() + "-" + active)%>
                                            </option>
                                            <%
                                                }
                                                terminalBuffer.append(")");
                                            }
                                            else
                                            {
                                            %>
                                            <option value="NoTerminals"><%=accountSummary_terminal_allocated%>
                                            </option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label for="member"><%=transactionSummary_currency%> </label>
                                        <input type="text" name="currency" id="member"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(currency)%>"
                                               class="form-control">
                                    </div>

                                    <div class="form-group col-xs-12 col-md-12 has-feedback"
                                         style="margin:0; padding: 0;"></div>
                                    <div class="form-group col-xs-12 col-sm-6 col-md-3 col-lg-3">
                                        <label><%=transactionSummary_timezone%></label>
                                        <select size="1" name="timezone" class="form-control">
                                            <option value=""><%=transactions_select_zone%>
                                            </option>
                                            <%

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


                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label>&nbsp;<%=transactionSummary_Search1%></label>
                                        <button type="submit" name="button" value="search" class="btn btn-default"
                                                style="display: -webkit-box;"><i class="fa fa-search"></i>&nbsp;&nbsp;<%=transactionSummary_Search2%>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<%--Report table --%>

<div class="reporttable">
    <div class="content">
        <div class="content-page">
            <%
                if (dataHash != null)
                {
                    int total = Integer.parseInt((String) request.getAttribute("total"));
//                    System.out.println("total in jsp--->"+total);
                    int totalTransaction = Integer.parseInt((String) request.getAttribute("totalTransaction"));
                    double sumAmount = Double.parseDouble((String) request.getAttribute("sumAmount"));

            %>
            <table border="0" cellpadding="3" cellspacing="3" bordercolor="#2379A5" width="50%" align="center"
                   class="table table-striped table-bordered table-green dataTable">

                <tr>
                    <td class="text" colspan="6">
                        <center><b>Deposit during the period</b></center>
                    </td>
                </tr>
                <tr class="th0" style="text-align: center !important; background-color: #7eccad;!important;">
                    <td class="th0">Status</td>
                    <td class="th0">No. Of Transaction</td>
                    <td class="th0">Amount</td>
                    <td class="th0">Percentage(%)</td>
                </tr>

                <%
                    for (int pos = 1; pos <= dataHash.size(); pos++)
                    {
                        innerhash = (Hashtable) dataHash.get(pos + "");
                        if (pos % 2 == 0)
                        {
                            style = "class=tr0";

                        }
                        else
                        {
                            style = "class=tr1";

                        }
                        out.println("<tr  " + style + ">");

                        out.println("<td align=\"center\" >" + innerhash.get("STATUS") + " </td>");
                        out.println("<td align=\"center\" >" + innerhash.get("statusCount") + " </td>");
                        out.println("<td align=\"center\" >" + innerhash.get("amount") + " </td>");
                        out.println("<td align=\"center\" >" + Functions.round(Double.parseDouble((String) innerhash.get("statusCount")) / totalTransaction * 100, 2) + "%</td>");
                    }

                %>
                </tr>
                <tr class="th0" style="text-align: center !important; background-color: #7eccad;!important;">
                    <td class="tr0">Total</td>
                    <td class="tr0"><%=totalTransaction%>
                    </td>
                    <td class="tr0"><%=sumAmount%>
                    </td>
                    <td class="tr0">&nbsp;</td>
                </tr>
            </table>
            <%

                if (hashChargeback != null)
                {
            %>
            <table border="0" cellpadding="3" cellspacing="3" bordercolor="#2379A5" width="50%" align="center"
                   class="table table-striped table-bordered table-green dataTable" style="margin-top: 15px;">

                <tr>
                    <td class="text" colspan="6">
                        <center><b>Chargeback Received during the period</b></center>
                    </td>
                </tr>
                <tr class="th0" style="text-align: center !important; background-color: #7eccad !important;">
                    <td class="th0">Status</td>
                    <td class="th0">No. Of Transaction</td>
                    <td class="th0">Amount</td>
                </tr>

                <%
                    for (int pos = 1; pos <= hashChargeback.size(); pos++)
                    {
                        innerhash1 = (Hashtable) hashChargeback.get(pos + "");
                        if (pos % 2 == 0)
                        {
                            style = "class=tr0";

                        }
                        else
                        {
                            style = "class=tr1";

                        }
                        out.println("<tr  " + style + ">");

                        out.println("<td align=\"center\" >" + innerhash1.get("STATUS") + " </td>");
                        out.println("<td align=\"center\" >" + innerhash1.get("COUNT(*)") + " </td>");
                        out.println("<td align=\"center\" >" + innerhash1.get("amount") + " </td>");
                    }

                %>
                </tr>
            </table>
            <%

                if (hashRecived != null)
                {
            %>
            <table border="0" cellpadding="3" cellspacing="3" bordercolor="#2379A5" width="50%" align="center"
                   class="table table-striped table-bordered table-green dataTable" style="margin-top: 15px;">

                <tr>
                    <td class="text" colspan="6">
                        <center><b>Reversal Received during the period</b></center>
                    </td>
                </tr>
                <tr class="th0" style="text-align: center !important; background-color: #7eccad !important;">
                    <td class="th0">Status</td>
                    <td class="th0">No. Of Transaction</td>
                    <td class="th0">Amount</td>
                </tr>

                <%
                    for (int pos = 1; pos <= hashRecived.size(); pos++)
                    {
                        innerhash1 = (Hashtable) hashRecived.get(pos + "");
                        if (pos % 2 == 0)
                        {
                            style = "class=tr0";

                        }
                        else
                        {
                            style = "class=tr1";

                        }
                        out.println("<tr  " + style + ">");

                        out.println("<td align=\"center\" >" + innerhash1.get("STATUS") + " </td>");
                        out.println("<td align=\"center\" >" + innerhash1.get("COUNT(*)") + " </td>");
                        out.println("<td align=\"center\" >" + innerhash1.get("amount") + " </td>");
                    }

                %>
                </tr>
            </table>
            <%

                if (hashPayout != null)
                {
                    int payouttotal = Integer.parseInt((String) request.getAttribute("payouttotal"));
                    int totalTransactionPayout = Integer.parseInt((String) request.getAttribute("totalTransactionPayout"));
                    float sumAmountPayout = Float.parseFloat((String) request.getAttribute("sumAmountPayout"));

            %>
            <table border="0" cellpadding="3" cellspacing="3" bordercolor="#2379A5" width="50%" align="center"
                   class="table table-striped table-bordered table-green dataTable" style="margin-top: 15px;">

                <tr>
                    <td class="text" colspan="6">
                        <center><b>Payout during the period</b></center>
                    </td>
                </tr>
                <tr class="th0" style="text-align: center !important; background-color: #7eccad;!important;">
                    <td class="th0">Status</td>
                    <td class="th0">No. Of Transaction</td>
                    <td class="th0">Amount</td>
                    <td class="th0">Percentage(%)</td>
                </tr>

                <%
                    for (int pos = 1; pos <= hashPayout.size(); pos++)
                    {
                        innerhash2 = (Hashtable) hashPayout.get(pos + "");
                        if (pos % 2 == 0)
                        {
                            style = "class=tr0";

                        }
                        else
                        {
                            style = "class=tr1";

                        }

                        out.println("<tr  " + style + ">");

                        out.println("<td align=\"center\" >" + innerhash2.get("STATUS") + " </td>");
                        out.println("<td align=\"center\" >" + innerhash2.get("payoutCount") + " </td>");
                        out.println("<td align=\"center\" >" + innerhash2.get("amount") + " </td>");
                        out.println("<td align=\"center\" >" + Functions.round(Double.parseDouble((String) innerhash2.get("payoutCount")) / payouttotal * 100, 2) + "%</td>");
                    }

                %>
                </tr>
                <tr class="th0" style="text-align: center !important; background-color: #7eccad;!important;">
                    <td class="tr0">Total</td>
                    <td class="tr0"><%=totalTransactionPayout%>
                    </td>
                    <td class="tr0"><%=sumAmountPayout%>
                    </td>
                    <td class="tr0">&nbsp;</td>
                </tr>
            </table>
        </div>
        <%--</div>--%>
        <%
                        }
                    }
                }
            }
            else
            {
                out.println(Functions.NewShowConfirmation1(transactionSummary_sorry, transactionSummary_status));
            }
        %>
    </div>
</div>
</body>
</html>
