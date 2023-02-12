<%@ page import="com.directi.pg.Admin,
                 com.directi.pg.Functions,
                 com.directi.pg.Logger" %>
<%@ include file="functions.jsp" %>
<%@ include file="index.jsp" %>
<%@ page import="com.manager.enums.Currency" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="java.util.*" %>


<%!
    private static Logger log   = new Logger("membermappingpreference.jsp");
    Functions functions         = new Functions();
%>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid.js"></script>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title> Settings> Commercials & Limits </title>

    <link rel="stylesheet" type="text/css" href="/merchant/stylepoptuk/poptuk.css">
    <script type='text/javascript' src='/merchant/stylepoptuk/poptuk.js'></script>
    <script src="/icici/css/am_multipleselection/bootstrap-multiselect.js"></script>
    <link href="/icici/css/am_multipleselection/bootstrap-multiselect.css" rel="stylesheet"/>
    <script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <script>
        function lancer(terminal)
        {
            poptuk(document.getElementById('poptuk' + terminal).style.display = "", terminal);
        }
    </script>
    <style type="text/css">
        #checkboxes {
            display: none;
            border: 1px #dadada solid;
            position: absolute;
            width: 100%;
            background-color: #ffffff;
            z-index: 1;
            height: 130px;
            overflow-x: auto;
        }

        #checkboxes label {
            display: block;
        }

        #checkboxes label:hover {
            background-color: #1e90ff;
        }

        input[type="checkbox"] {
            width: 18px; /*Desired width*/
            height: 18px; /*Desired height*/
        }

        .icheckbox_square-aero {
            margin: 3px 5px;
        }

        /********************************************************************************/

        .multiselect-container > li {
            padding: 0;
            margin-left: 31px;
        }

        .open > #multiselect-id.dropdown-menu {
            display: block;
        }

        .multiselect-container > li > a > label {
            margin: 0;
            height: 24px;
            padding-left: 1px;
        !important;
            text-align: left;
        }

        span.multiselect-native-select {
            position: relative;
        }

        @supports (-ms-ime-align:auto) {
            span.multiselect-native-select {
                position: static !important;
            }
        }

        select[multiple], select[size] {
            height: auto;
            border-color: rgb(169, 169, 169);
        }

        .widget .btn-group {
            z-index: 1;
        }

        .btn-group, .btn-group-vertical {
            position: relative;
            vertical-align: middle;
            border-radius: 4px;
        }

        #mainbtn-id.btn-default {
            color: #333;
            background-color: #fff;
            border-color: #333;
            padding: 6px;
        }

        .btn-group > .btn:first-child {
            margin-left: 0;
        }

        .btn-group > .btn:first-child {
            margin-left: 0;
        }

        .btn-group > .btn, .btn-group-vertical > .btn {
            position: relative;
            float: left;
        }

        .multiselect-container {
            position: absolute;
            list-style-type: none;
            margin: 0;
            padding: 0;
            height: 225px;
            overflow-y: scroll;
        }

        #multiselect-id.dropdown-menu {
            position: absolute;
            top: 100%;
            left: 0;
            z-index: 500;
            display: none;
            float: left;
            min-width: 160px;
            font-size: 14px;
            font-family: "Open Sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
            list-style: none;
            background-color: #fff;
            border: 1px solid #ccc;
            border: 1px solid rgba(0, 0, 0, 0.15);
            border-radius: 4px;
            -webkit-box-shadow: 0 6px 12px rgba(0, 0, 0, 0.175);
            box-shadow: 0 6px 12px rgba(0, 0, 0, 0.175);
            background-clip: padding-box;
        }

        #mainbtn-id .multiselect-selected-text {
            font-size: 12px;
            font-family: "Open Sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
        }

        #mainbtn-id.btn-default, #mainbtn-id.btn-default:focus, #mainbtn-id.btn-default.focus, #mainbtn-id.btn-default:active, #mainbtn-id.btn-default.active, .open > .dropdown-toggle#mainbtn-id.btn-default {
            color: #333;
            /*color: #fff;*/
            background-color: white !important;
            border-color: #ddd !important;
            text-align: left;
            width: 187px;
        !important;
        }

        .multiselect-container .active > a > label {
            color: #fff;
        !important;
        }

        .btn .caret {
            position: absolute;
            display: inline-block;
            width: 0px;
            height: 1px;
            margin-left: 2px;
            vertical-align: middle;
            border-top: 7px solid;
            border-right: 4px solid transparent;
            border-left: 4px solid transparent;
            float: right;
            margin-top: 5px;
            box-sizing: inherit;
            top: 13px;
            right: 5px;
            margin-top: -2px;
        }

        .fa-chevron-down {
            position: absolute;
            right: 0px;
            top: 0px;
            margin-top: -2px;
            vertical-align: middle;
            float: right;
            font-size: 9px;
        }

        #mainbtn-id {
            overflow: hidden;
            display: block;
        }

        .cardLimitCheck, .cardAmountLimitCheck, .amountLimitCheck {
            color: #001962;
            text-valign: center;
            font-family: "Open Sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
            font-size: 12px;
            FONT-WEIGHT: normal;
            height: 25px;
            -webkit-border-radius: 4px;
            -moz-border-radius: 4px;
            border-radius: 4px;
        }

        /********************************************************************************/
    </style>
    <script>
        function ChangeFunction(Value , lable,terminal){
            console.log("terminal"+terminal);
            console.log("value"+Value);
            console.log("lable"+lable);
            var value=lable+"="+Value;
            var get_previous = document.getElementById("onchangedvalue_"+terminal).value;
            var final_value;
            if(get_previous =="")
            {
                final_value =value;
            }
            else{
                final_value =get_previous+","+value;
            }
            document.getElementById("onchangedvalue_"+terminal).value = final_value;

        }
        function ChangeFunctionWhitelistDetails(sel,lable,terminal){
            var opts = [];
                    var opt;
            var len = sel.options.length;
            var final_value;
            for (var i = 0; i < len; i++) {
                opt = sel.options[i];

                if (opt.selected) {
                    opts.push(opt.value);
                    var value=lable+"="+opt.value;
                    console.log("value______"+value);

                }
            }
                document.getElementById("onchangedvalue_" + terminal).value = lable+"="+opts;

        }
    </script>

</head>
<body>

<script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-accountid.js"></script>
<script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-accountid2.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>
<%--<br><br><br><br><br><br>
<center><h4 class="textb" style="margin-left: 15%"><b>Member Mapping Details</b></h4></center>--%>
<%
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

    if (Admin.isLoggedIn(session))
    {
        String str          = "";
        String pgtypeid     = "";
        String paymodesearch = "";
        String currency     = "";
        //String memberid   ="";
        currency            = Functions.checkStringNull(request.getParameter("currency")) == null ? "" : request.getParameter("currency");
        pgtypeid            = Functions.checkStringNull(request.getParameter("pgtypeid")) == null ? "" : request.getParameter("pgtypeid");
        String accountid2   = Functions.checkStringNull(request.getParameter("accountid")) == null ? "" : request.getParameter("accountid");
        // memberid = Functions.checkStringNull(request.getParameter("memberid"))==null?"":request.getParameter("memberid");
        //TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();
        //TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();

        String reqaccountid = "";
        if (request.getAttribute("accountid") != null)
            reqaccountid = (String) request.getAttribute("accountid");

        String memberId = "";
        if (request.getAttribute("memberid") != null)
            memberId = (String) request.getAttribute("memberid");

        if (request.getAttribute("paymodeid")!= null)
            paymodesearch = (String) request.getAttribute("paymodeid");

        if (pgtypeid != null) str = str + "&pgtypeid=" + pgtypeid;
        pgtypeid = "";
       /* if(currency!=null)str = str + "&currency=" + currency;
        else
            currency="";
        if(accountid2!=null)str = str + "&accountid=" + accountid2;
        else
            accountid2="";*/

        String CardTypeIds ="";
        if (request.getParameter("ctype1") != null)
            CardTypeIds = request.getParameter("ctype1");

        String ActiveOrInActive ="";
        if (request.getParameter("isactive") != null)
            ActiveOrInActive = request.getParameter("isactive");

        String PayoutActiveorInactive ="";
        if (request.getParameter("ispayoutactive")!= null)
            PayoutActiveorInactive= request.getParameter("ispayoutactive");

        HashMap<String,String> dropdownlist = new HashMap<String, String>();
        dropdownlist.put("All","All");
        dropdownlist.put("Y","Active");
        dropdownlist.put("N","InActive");

%>
<script type="text/javascript">
    function check()
    {
        var error               = "";
        var memberid            = document.form1.elements["memberid"].value;
        var accountid           = document.form1.elements["accountid"].value;
        var paymodeid           = document.form1.elements["paymodeid"].value;
        var cardtypeid          = document.form1.elements["cardtypeid"].value;
        var automaticrecurring  = document.form1.elements["automaticrecurring"].value;
        var manualrecurring     = document.form1.elements["manualrecurring"].value;


        if (memberid == "")
        {
            error += "Enter Valid MemberID \n";
        }
        if (accountid == "")
        {
            error += "Select Valid Account ID\n";
        }
        if (paymodeid == "")
        {
            error += "Select Valid Paymode ID\n";
        }
        if (cardtypeid == "")
        {
            error += "Select Valid Card Type ID\n";
        }
        if ((automaticrecurring == "Y" && manualrecurring == "Y"))
        {
            error += "Automatic recurring and Manual recurring cannot be Y at a time.\n";
        }
        if (error != "")
        {
            window.alert(error);
        }
        else
        {
            document.getElementById("form1").submit();
        }
    }
    function confirmsubmit(i)
    {
        var r = window.confirm("Are You Sure you want to Update The Details");
        if (r == true)
        {
            document.getElementById("charges" + i).submit();
        }
        else
        {

        }
    }
    function confirmsubmit2(terminalid)
    {
        console.log(terminalid); //please do not remove console here
        //document.getElementById(terminal).remove();
        $("[name='terminalid']").each(function ()
         {
             var checked_terminal =$(this).val();
         if ($(this).val() != terminalid)
         {
             document.getElementById(checked_terminal).checked=false;
         }
         });

        document.getElementById(terminalid).checked=true;

        console.log(document.getElementById(terminalid));

        console.log(document.getElementById("action_" + terminalid).value); //please do not remove console here
        console.log(""); //please do not remove console here
        if (document.getElementById("action_" + terminalid).value == "DELETE_" + terminalid)
        {
            var r = window.confirm("Are You Sure you want to Delete The Mapping");
            if (r == true)
            {
                document.getElementById("saveall").submit();
            }

        }
        if (document.getElementById("action_" + terminalid).value == "UPDATE_" + terminalid)
        {

            var error = "";
            var automaticrecurring1 = document.getElementById("automaticrecurring1_" + terminalid).value;
            var manualrecurring1 = document.getElementById("manualrecurring1_" + terminalid).value;

            if ((automaticrecurring1 == "Y" && manualrecurring1 == "Y"))
            {
                error += "Automatic recurring and Manual recurring cannot be Y at a time.\n";
            }
            if (error != "")
            {
                window.alert(error);
            }
            else
            {
                document.getElementById("saveall").submit();
            }
        }
        /*else
         {
         document.getElementById("saveall").submit();
         }*/
    }

    function confirmsubmit3()
    {
        var checkboxs   = document.getElementsByName("terminalid");
        var okay        = false;
        for(var i=0,l=checkboxs.length;i<l;i++)
        {
            if(checkboxs[i].checked)
            {
                okay=true;
                break;
            }
        }
        if(okay)
        {
            document.getElementById("saveall").submit();
        }
        else{
            alert("Please Select at least one transaction");
        }

        /*if (confirm("Do you really want to Save all Terminals ?"))
         {
         document.getElementById("saveall").submit();

         }
         else
         {
         return false;
         }*/
    }

    function confirmgateway()
    {
        if (document.form1.elements["accountid"].value == "")
        {
            window.alert("Please Select Account ID");
        }
        else
        {
            var accountid = form1.elements["accountid"].value;
            window.open('GetGatewayAccount?accountid=' + accountid + '&ctoken=<%=user.getCSRFToken()%>', 'newwindow', 'width=500, height=350');
            return false;
        }
    }
</script>
<script type="text/javascript">
    $(function ()
    {
        $(document).ready(function ()
        {
            $(".caret").addClass('icon2');
            $('.multiselect-selected-text').addClass("filter-option pull-left");
            firefox = navigator.userAgent.search("Firefox");
            if (firefox > -1)
            {
                $('.icon2').removeClass("caret");
                $('.icon2').addClass("fa fa-chevron-down");
                $('.icon2').css({
                    "height": "30px",
                    "width": "17px",
                    "text-align": "center",
                    "background-color": "#E6E2E2",
                    "padding-top": "6px",
                    "margin-top": "0px",
                    "border": "1px solid #C7BFBF"
                });
                $('.dropdown-toggle').css({"padding": "0px", "vertical-align": "middle", "height": "25px"});
                $('.tr0 .dropdown-toggle .icon2').css({"background-color": "white", "border": "0px"});
                $('.tr1 .dropdown-toggle .icon2').css({"background-color": "white", "border": "0px"});
                $('.multiselect-selected-text').css({
                    "padding-top": "4px",
                    "padding-bottom": "10px",
                    "padding-left": "10px",
                    "vertical-align": "middle"
                });
            }
        });
        $('#whitelisting').multiselect({
            buttonText: function (options, select)
            {
                var labels = [];
                if (options.length === 0)
                {
                    labels.pop();
                    document.getElementById('whitelistcode').value = labels;
                    return 'Select Whitelist Details';
                }
                else
                {
                    options.each(function ()
                    {
                        labels.push($(this).val());
                    });
                    document.getElementById('whitelistcode').value = labels;
                    return labels.join(', ') + '';
                }
            }
        });

        $('#paymodelist').multiselect({
            buttonText: function (options, select)
            {
                var labels = [];
                if (options.length === 0)
                {
                    labels.pop();
                    document.getElementById('paymodeid').value = labels;
                    return 'Select PayMode ID';
                }
                else
                {
                    options.each(function ()
                    {
                        labels.push($(this).val());
                    });
                    document.getElementById('paymodeid').value = labels;
                    // return labels.join(', ') + '';
                    return 'Select PayMode ID';
                }
            }
        });

        $('#cardtypelist').multiselect({
            buttonText: function (options, select)
            {
                var labels = [];
                if (options.length === 0)
                {
                    labels.pop();
                    document.getElementById('cardtypeid').value = labels;
                    return 'Select Card Type ID';
                }
                else
                {
                    options.each(function ()
                    {
                        labels.push($(this).val());
                    });
                    document.getElementById('cardtypeid').value = labels;
                    //return labels.join(', ') + '';
                    return 'Select Card Type ID';
                }
            }
        });
    });
</script>

<script>

    $(function () {
        $('#currency_convo').multiselect({

            buttonText: function(options, select) {
                if (options.length === 0) {
                    return 'Select Currency Conversion';
                }

                else {
                    var labels = [];
                    options.each(function() {
                        if($(this).val() !="") {
                            labels.push($(this).val());
                        }
                    });
                    return labels.join(', ') + '';

                    //$('#bank_account_currencies').val(labels);

                }
            },
            includeSelectAllOption: true
        });
        $('#currency_convo').change(function()
        {
            var labels = [];
            $('#currency_convo :selected').each(function(i, sel){
                labels.push($(this).val());
                //$('#bank_account_currencies').val($(sel).val());
            });
            labels.join(', ') + '';
            if(labels.length >0) {
                $('#currencycon').val(labels);
            }
            else
            {
                $('#currencycon').val("N");
            }
        });

    });
</script>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading" align="center">
                <b style="padding-left: 200px;">Member Mapping Details</b>
                <div style="float: right;">
                    <form action="/icici/copyTerminal.jsp?ctoken=<%=ctoken%>" method="POST">
                        <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                        <button type="submit" class="addnewmember" value="Copy Terminal" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Copy Terminal
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" style="margin-top: 0px">
            <form id="form1" name="form1" action="SetAccount?ctoken=<%=ctoken%>" method="POST">
                <%--<div class="reporttable">--%>
                    <%
                        String errormsg2 = (String) request.getAttribute("error1");
                        if (errormsg2 != null)
                        {
                            out.println("<table align=\"center\"><tr><td><font class=\"textb\"><b>");
                            out.println(errormsg2);
                            out.println("</b></font>");
                            out.println("</td></tr></table>");
                        }
                    %>
                    <%
                        if (request.getAttribute("success1") != null)
                        {
                            out.println("<table align=\"center\"><tr><td><font class=\"textb\"><b>");
                            out.println((String) request.getAttribute("success1"));
                            out.println("</b></font>");
                            out.println("</td></tr></table>");
                        }
                    %>


                    <table align=centder border="1" cellpadding="2" cellspacing="0" width="10%"
                           class="table table-striped table-bordered table-green dataTable">
                        <tr>
                            <td class="th0" colspan="6" align="center">
                                Create Account

                            </td>

                        </tr>
                        <tr>
                            <td class="textb" valign="middle" align="center">Member ID </td>
                            <td><input id="mid" class="txtbox" type=text name="memberid"></td>

                            <td class="textb" valign="middle" align="center">PayMode ID</td>
                            <td><select id="paymodelist" size="1" class="multiselect txtboxsmall" multiple="multiple">
                                <%
                                    LinkedHashMap<String,String> paymodeids = (LinkedHashMap) request.getAttribute("paymodeids");
                                    String paymodeid        = "";
                                    String paymodeModeName  = "";
                                    String cardTypeName     = "";
                                    String paymode          = "";
                                    String cardName         = "";
                                    System.out.println("paymodeids--->"+paymodeids.toString());
                                    for (Map.Entry<String, String> entry : paymodeids.entrySet())
                                    {
                                        paymodeid   = (String) entry.getKey();
                                        paymode     = (String) entry.getValue();
                                %>
                                <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(paymodeid)%>">
                                    <%=ESAPI.encoder().encodeForHTML(paymode)%>
                                </option>
                                <% } %>
                            </select>
                                <input type="hidden" id="paymodeid" name="paymodeid" value="">
                            </td>

                            <td class="textb" valign="middle" align="center">Card Type ID</td>
                            <td><select id="cardtypelist" size="1" class="multiselect txtboxsmall" multiple="multiple">
                                <%
                                    LinkedHashMap<String,String> cardtypeids = (LinkedHashMap) request.getAttribute("cardtypeids");
                                    String cardtypeid   = "";
                                    String cardtype     = "";
                                    for (Map.Entry<String, String> entry : cardtypeids.entrySet())
                                    {
                                        cardtypeid  = (String) entry.getKey();
                                        cardtype    = (String) entry.getValue();
                                %>
                                <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(cardtypeid)%>">
                                    <%=ESAPI.encoder().encodeForHTML(cardtype)%>
                                </option>
                                <% } %>
                            </select>
                                <input type="hidden" id="cardtypeid" name="cardtypeid" value="">

                            </td>
                        </tr>
                        <tr>
                            <td class="textb" valign="middle" align="center">Gateway</td>
                            <td>
                                <input name="pgtypeid" id="pgtypeid" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                            </td>

                            <td class="textb" valign="middle" align="center">Accounts</td>
                            <td>
                                <input name="accountid" id="accid" value="<%=reqaccountid%>" class="txtbox" autocomplete="on">
                            </td>

                        </tr>
                        <tr>
                            <td valign="middle" align="center" class="th0" colspan=6><font size=2>Limits</font></td>
                        </tr>
                        <tr>
                            <td class="textb" valign="middle" align="center">Daily Amount Limit</td>
                            <td class="textb" width="100" valign="middle" align="center">Weekly Amount Limit</td>
                            <td class="textb" WIDTH="20" align="middle" align="center">Monthly Amount Limit</td>
                            <td class="textb" valign="middle" align="center">Daily Card Limit</td>
                            <td class="textb" valign="middle" align="center">Weekly Card Limit</td>
                            <td class="textb" valign="middle" align="center">Monthly Card Limit</td>
                        </tr>
                        <tr>
                            <td align="center"><input type=text class="txtboxsmall" size=10 name='daily_amount_limit' value="100.00">
                                <select name="daily_amount_limit_check"><%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                            </td>
                            <td align="center"><input type=text class="txtboxsmall" size=10 name='weekly_amount_limit' value="100.00">
                                <select name="weekly_amount_limit_check"><%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                            </td>
                            <td align="center"><input type=text class="txtboxsmall" size=10 name='monthly_amount_limit' value="100.00">
                                <select name="monthly_amount_limit_check"><%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                            </td>
                            <td align="center"><input type=text class="txtboxsmall" size=10 name='daily_card_limit' value="5">
                                <select name="daily_card_limit_check"><%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                            </td>
                            <td align="center"><input type=text class="txtboxsmall" size=10 name='weekly_card_limit' value="10">
                                <select name="weekly_card_limit_check"><%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                            </td>
                            <td align="center"><input type=text class="txtboxsmall" size=10 name='monthly_card_limit' value="20">
                                <select name="monthly_card_limit_check"><%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                            </td>
                        </tr>
                        <tr>
                            <td class="textb" valign="middle" align="center">Daily Card Amount Limit</td>
                            <td class="textb" valign="middle" align="center">Weekly Card Amount Limit</td>
                            <td class="textb" valign="middle" align="center">Monthly Card Amount Limit</td>
                            <td class="textb" valign="middle" align="center">Daily Avg Ticket</td>
                            <td class="textb" valign="middle" align="center">Weekly Avg Ticket</td>
                            <td class="textb" valign="middle" align="center">Monthly Avg Ticket</td>
                        </tr>
                        <tr>
                            <td align="center"><input type=text class="textb" size=10 name='daily_card_amount_limit' value="1000.00">
                                <select name="daily_card_amount_limit_check"><%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                            </td>
                            <td align="center"><input type=text class="txtboxsmall" size=10 name='weekly_card_amount_limit' value="5000.00">
                                <select name="weekly_card_amount_limit_check"><%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                            </td>
                            <td align="center"><input type=text class="txtboxsmall" size=10 name='monthly_card_amount_limit' value="10000.00">
                                <select name="monthly_card_amount_limit_check"><%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                            </td>
                            <td align="center"><input type=text class="txtboxsmall" size=10 name='daily_avg_ticket' value="150.00"></td>
                            <td align="center"><input type=text class="txtboxsmall" size=10 name='weekly_avg_ticket' value="150.00"></td>
                            <td align="center"><input type=text class="txtboxsmall" size=10 name='monthly_avg_ticket' value="150.00"></td>
                        </tr>
                        <tr>
                            <td class="textb" valign="middle" align="center">Min Transaction Amount</td>
                            <td class="textb" valign="middle" align="center">Max Transaction Amount</td>
                        </tr>
                        <tr>
                            <td align="center"><input type=text class="txtboxsmall" size=10 name='min_trans_amount' value="1.00">
                            </td>
                            <td align="center"><input type=text class="txtboxsmall" size=10 name='max_trans_amount' value="1000.00">
                            </td>
                        </tr>
                        <td valign="middle" align="center" class="th0" colspan=6><font size=2>Others</font></td>
                        </tr>
                        <tr>
                            <td class="textb" valign="middle" align="center">IsActive</td>
                            <td class="textb" width="50" valign="middle" align="center">Priority</td>
                            <td class="textb" valign="middle" align="center">IsTest</td>
                            <td class="textb" valign="middle" align="center">AutomaticRecurring</td>
                            <td class="textb" valign="middle" align="center">isRestrictedTicketActive</td>
                            <td class="textb" valign="middle" align="center">isTokenizationActive</td>
                        </tr>
                        <tr>
                            <td align="center"><select name='isActive' class="txtboxsmall"><%
                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("Y")));%></select></td>
                            <td align="center"><select id="priority" name="priority" class="txtboxsmall">
                                <%for(int i=1; i<=200;i++)
                                {
                                    out.print("<option value=\"" + i + "\" default>" + i + "</option>");
                                }
                                %>
                            </select>
                            </td>
                            <td align="center"><select name='isTest' class="txtboxsmall"><%
                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select></td>
                            <td align="center"><select name='is_recurring' id="automaticrecurring" class="txtboxsmall"><%
                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select></td>
                            <td align="center"><select name='isRestrictedTicketActive' class="txtboxsmall"><%
                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select></td>
                            <td align="center"><select name='isTokenizationActive' class="txtboxsmall"><%
                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select></td>
                        </tr>
                        <tr>
                            <td class="textb" valign="middle" align="center">ManualRecurring</td>
                            <td class="textb" valign="middle" align="center">AddressDetailsDisplay</td>
                            <td class="textb" valign="middle" align="center">AddressValidation</td>
                            <td class="textb" valign="middle" align="center">Card Details Required</td>
                            <td class="textb" valign="middle" align="center">Is PSTTerminal</td>
                            <td class="textb" valign="middle" align="center">Is CardEncryptionEnable</td>
                        </tr>
                        <tr>
                            <td align="center"><select name='isManualRecurring' id="manualrecurring" class="txtboxsmall"><%
                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select></td>
                            <td align="center"><select name='addressDetails' class="txtboxsmall"><%
                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("Y")));%></select></td>
                            <td align="center"><select name='addressValidation' class="txtboxsmall"><%
                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("Y")));%></select></td>
                            <td align="center"><select name='cardDetailRequired' class="txtboxsmall"><%
                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("Y")));%></select></td>
                            <td align="center"><select name='isPSTTerminal' class="txtboxsmall"><%
                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select></td>
                            <td align="center"><select name='isCardEncryptionEnable' class="txtboxsmall"><%
                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select></td>
                        </tr>
                        <tr>
                            <td class="textb" valign="middle" align="center">Risk Rule Activation</td>
                            <td class="textb" valign="middle" align="center">Settlement Currency</td>
                            <td class="textb" valign="middle" align="center">Min Payout Amount</td>
                            <td class="textb" valign="middle" align="center">Payout Activation</td>
                            <td class="textb" valign="middle" align="center">Auto Redirect Request</td>
                            <td class="textb" valign="middle" align="center">Is BinRouting</td>
                            <%--<td class="textb" valign="middle" align="center" >Reject 3D Card</td>--%>

                        <tr>
                            <td align="center">
                                <select name='riskruleactivation' class="txtboxsmall"><%
                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%>
                                </select>
                            </td>
                            <td align="center">
                                <select name='settlementcurrency' class="txtboxsmall">
                                    <%
                                        for (Currency settlementCurrency : Currency.values())
                                        {
                                            out.println("<option value=\"" + settlementCurrency.toString() + "\">" + settlementCurrency.toString() + "</option>");
                                        }
                                    %>
                                </select>
                            </td>
                            <td align="center"><input type=text class="txtboxsmall" size=10 name='min_payout_amount' value="500.00">
                            </td>
                            <td align="center">
                                <select name='payoutactivation' class="txtboxsmall"><%
                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("Y")));%>
                                </select>
                            </td>

                            <td align="center">
                                <select name='autoRedirectRequest' class="txtboxsmall"><%
                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%>
                                </select>
                            </td>
                            <td align="center">
                                <select name='bin_routing' class="txtboxsmall"><%
                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%>
                                </select>
                            </td>
                            <%-- <td  align="center">
                                 <select name='reject3DCard' class="txtboxsmall"><%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%>
                                 </select>
                             </td>--%>
                        </tr>
                        </tr>
                        <tr>
                            <td class="textb" valign="middle" align="center">Is CardWhitelisted</td>
                            <td class="textb" valign="middle" align="center">Is EmailWhitelisted</td>
                            <td class="textb" valign="middle" align="center">Currency Conversion</td>
                            <td class="textb" valign="middle" align="center">Conversion Currency</td>
                            <td class="textb" valign="middle" align="center">Emi Support</td>
                            <td class="textb" valign="middle" align="center">Whitelist Details</td>
                        </tr>

                        <tr>
                            <td align="center">
                                <select name='isCardWhitelisted' class="txtboxsmall"><%
                                    out.println(Functions.comboval2(ESAPI.encoder().encodeForHTMLAttribute("N")));%>
                                </select>
                            </td>
                            <td align="center">
                                <select name='isEmailWhitelisted' class="txtboxsmall"><%
                                    out.println(Functions.comboval2(ESAPI.encoder().encodeForHTMLAttribute("N")));%>
                                </select>
                            </td>
                            <td align="center">
                                <select id="currency_convo" size="1" class="multiselect txtboxsmall" multiple="multiple">
                                    <option value="Y">Sale</option>
                                    <option value="R">Refund</option>
                                    <option value="P">Payout</option>
                                </select>
                                <input type="hidden" id="currencycon" name="currency_conversion" value="N">
                            </td>
                            <td align="center">
                                <select name='conversion_currency' class="txtboxsmall">
                                    <%
                                        for (Currency conversionCurrency : Currency.values())
                                        {
                                            out.println("<option value=\"" + conversionCurrency.toString() + "\">" + conversionCurrency.toString() + "</option>");
                                        }
                                    %>
                                </select>
                            </td>
                            <td align="center">
                                <select name='emi_support' class="txtboxsmall"><%
                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%>
                                </select>
                            </td>
                            <td align="center">
                                <select id="whitelisting" size="1" class="multiselect txtboxsmall" multiple="multiple">
                                    <option value="name">Card Holder Name</option>
                                    <option value="ipAddress">IP Address</option>
                                    <option value="expiryDate">Expiry Date</option>

                                </select>
                                <input type="hidden" id="whitelistcode" name="whitelistdetails" value="">
                            </td>
                        </tr>
                        <tr>
                            <td class="textb" valign="middle" align="center">Card Limit Check</td>
                            <td class="textb" valign="middle" align="center">Card Amount Limit Check</td>
                            <td class="textb" valign="middle" align="center">Amount Limit Check</td>
                            <td class="textb" valign="middle" align="center">Processor Partner ID</td>
                            <td class="textb" valign="middle" align="center">Payout Priority</td>
                        </tr>
                        <tr>
                            <td align="center">
                                <select name="cardLimitCheck" id="cardLimitCheck" class="cardLimitCheck">
                                    <option value='N' selected>N</option>
                                    <option value='terminal_Level'>Terminal Level</option>
                                    <option value='account_Level'>Account Level</option>
                                    <option value='account_member_Level'>Account Member Level</option>
                                </select>
                            </td>
                            <td align="center">
                                <select name="cardAmountLimitCheck" id="cardAmountLimitCheck" class="cardAmountLimitCheck">
                                    <option value='N' selected>N</option>
                                    <option value='terminal_Level'>Terminal Level</option>
                                    <option value='account_Level'>Account Level</option>
                                    <option value='account_member_Level'>Account Member Level</option>
                                </select>
                            </td>
                            <td align="center">
                                <select name="amountLimitCheck" id="amountLimitCheck" class="amountLimitCheck">
                                    <option value='N' selected>N</option>
                                    <option value='terminal_Level'>Terminal Level</option>
                                    <option value='account_Level'>Account Level</option>
                                    <option value='account_member_Level'>Account Member Level</option>
                                    <option value='terminal_Level_based_on_cardtype'>Terminal Level Based On Cardtype</option>
                                </select>
                            </td>
                            <td width="100" align="center" class=""><input type=text class="txtbox" align="center" size=20
                                                                           id="allpid" name='processor_partnerid' autocomplete="on">
                            </td>
                            <td align="center"><select id="payout_priority" name="payout_priority" class="txtboxsmall">
                                <option value="1" default>1</option>
                                <option value="2" default>2</option>
                                <option value="3" default>3</option>
                                <option value="4" default>4</option>
                                <option value="5" default>5</option>
                                <option value="6" default>6</option>
                                <option value="7" default>7</option>
                                <option value="8" default>8</option>
                            </select>
                            </td>
                        </tr>
                        <tr>
                            <td align=center colspan=6><input type="hidden" name="action_create" value="CREATE">
                                <button align=center type="button" onclick="check()" value="CREATE" class="buttonform">CREATE
                                </button>
                            </td>
                        </tr>
                    </table>
                <%--</div>--%>
            </form>
        </div>
    </div>
</div>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" style="margin-top: 0px">
            <div class="panel-heading">
                Search
            </div>
            <form action="MemberMappingDetails?ctoken=<%=ctoken%>" method="post" name="F1" onsubmit="">
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <%
                    String errormsg1 = (String) request.getAttribute("error");
                    if (errormsg1 != null)
                    {
                        out.println("<table align=\"center\"><tr><td><font class=\"textb\"><b>");
                        out.println(errormsg1);
                        out.println("</b></font>");
                        out.println("</td></tr></table>");
                    }
                %>
                <%
                    if (request.getAttribute("success") != null)
                    {
                        out.println("<table align=\"center\"><tr><td><font class=\"textb\"><b>");
                        out.println((String) request.getAttribute("success"));
                        out.println("</b></font>");
                        out.println("</td></tr></table>");
                    }
                %>
                <table align="center" width="100%" cellpadding="2" cellspacing="2"
                       style="margin-left:2.5%;margin-right: 2.5% ">
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Gateway</td>
                                    <td width="12%" class="textb">
                                        <input name="pgtypeid" id="gateway1" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                                    </td>

                                    <td width="4%" class="textb"></td>
                                    <td width="8%" class="textb">Account ID</td>
                                    <td width="12%" class="textb">
                                        <input name="accountid" id="accountid1" value="<%=reqaccountid%>" class="txtbox" autocomplete="on">
                                    </td>

                                    <td width="4%" class="textb"></td>
                                    <td width="8%" class="textb">Member ID</td>
                                    <td width="12%" class="textb">
                                        <input name="memberid" id="memberid1" class="txtbox" autocomplete="on" value=<%=memberId%>>
                                    </td>
                                </tr>

                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>

                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Paymode</td>
                                    <td width="12%" class="textb">
                                        <input name="paymodeid" id="paymode1" class="txtbox" autocomplete="on" value=<%=paymodesearch%>>
                                    </td>

                                    <td width="4%" class="textb"></td>
                                    <td width="8%" class="textb">Card Type</td>
                                    <td>
                                        <input name="ctype1" id="ctype1" value="<%=CardTypeIds%>"  class="txtbox" autocomplete ="on">
                                    </td>

                                    <td width="4%" class="textb"></td>
                                    <td width="4%" class="textb"> IsActive </td>
                                    <td class="textb">
                                        <select type="text" name="isactive" value="<%=ESAPI.encoder().encodeForHTML(ActiveOrInActive)%>" onchange="ChangeFunction(this.value,'IsActive','')">
                                            <%
                                                for(Map.Entry<String,String> yesNoPair : dropdownlist.entrySet())
                                                {
                                                    String selected = "";
                                                    if(yesNoPair.getKey().equals(ActiveOrInActive))
                                                        selected="selected";
                                                    out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>

                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">IsPayoutActive</td>
                                    <td width="12%" class="textb">
                                        <select type="text" name="ispayoutactive" value="<%=ESAPI.encoder().encodeForHTML(PayoutActiveorInactive)%>" onchange="ChangeFunction(this.value,'IsPayoutActive','')" >
                                            <%
                                                for (Map.Entry<String,String> payoutstatus: dropdownlist.entrySet())
                                                {
                                                    String selected="";
                                                    if (payoutstatus.getKey().equals(PayoutActiveorInactive))
                                                        selected="selected";
                                                    out.println("<option value="+payoutstatus.getKey()+" "+selected+">"+payoutstatus.getValue()+"</option>");
                                                }
                                            %>
                                        </select>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td  colspan="2" class="textb">
                                        <button type="submit" class="buttonform" name="action" value="search">
                                            <span><i class="fa fa-clock-o"></i></span>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>
                                    <td class="textb">
                                        <button type="submit" class="buttonform" name="action" value="tsummary">
                                            <span><i class="fa fa-clock-o"></i></span>
                                            Terminal Summary
                                        </button>
                                    </td>

                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>
<%
    Hashtable hash      = (Hashtable) request.getAttribute("memberdetails");

    String action       = (String) request.getAttribute("action");
    Hashtable temphash  = null;
    int records         = 0;
    int totalrecords    = 0;
    try
    {
        records         = Integer.parseInt((String) hash.get("records"));
        totalrecords    = Integer.parseInt((String) hash.get("totalrecords"));
    }
    catch (Exception e)
    {
        log.error("Records & TotalRecords is found null");
    }

%>
<%
    if (totalrecords > 0)
    {
%>


<div class="reporttable">
    <form name="exportform" method="post" action="ExportTerminalDetails?ctoken=<%=ctoken%>">

        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <%--<input type="hidden" value="Admin" name="ExportRequest">--%>
        <input type="hidden" value=<%=memberId%> name="memberId">
        <input type="hidden" value="<%=reqaccountid%>" name="accountid">
        <input type="hidden" value="<%=paymodesearch%>" name="paymodeid">
        <input type="hidden" value="<%=CardTypeIds%>" name="ctype1">
        <input type="hidden" value="<%=ActiveOrInActive%>" name="isactive">
        <input type="hidden" value="<%=PayoutActiveorInactive%>" name="ispayoutactive">

        <button type="submit" class="button3" style="width:15%;margin-left:85% ;margin-top:0px"><b></b>&nbsp;&nbsp;&nbsp;<img
                width="80%" height="100%" border="0" src="/merchant/images/excel.png"></button>
    </form>

    <font class="info">Total records <%=totalrecords%>
    </font>

    <%
        if(action.equalsIgnoreCase("search"))
        {


    %>
    <div class="scroll">
        <form id="saveall" action="/icici/servlet/SetAccount?ctoken=<%=ctoken%>" method=post>
            <table align=center border=2 class="table table-striped table-bordered table-green dataTable">
                <% String style = "td1";
                    String style1 = " display: none;border: 1px #dadada solid;position: absolute;width: 11%;background-color: #ffffff;z-index: 1;height: 100px;overflow-x: auto;";
                    for (int pos = 1; pos <= records; pos++)
                    {
                        String id = Integer.toString(pos);
                        String checkboxes = "checkboxes" + pos;

                        int srno = Integer.parseInt(id);

                        if (pos % 2 == 0)
                            style = "tr0";
                        else
                            style = "tr1";

                        temphash            = (Hashtable) hash.get(id);
                        memberId            = (String) temphash.get("memberid");
                        String accountId    = Functions.checkStringNull((String) temphash.get("accountid"));

                        paymodeid   = "N/A";
                        cardtypeid  = "N/A";
                        String monthly_amount_limit = "N/A";
                        String weekly_amount_limit  = "N/A";
                        String daily_card_limit     = "N/A";
                        String weekly_card_limit    = "N/A";
                        String monthly_card_limit   = "N/A";
                        String daily_amount_limit   = "N/A";
                        String isReadOnly           = "readonly";
                        boolean isName                  = false, isIpAddress = false, isExpiryDate = false;
                        String daily_card_amount_limit  = "", weekly_card_amount_limit = "", monthly_card_amount_limit = "", daily_amount_limit_check="",weekly_amount_limit_check="", monthly_amount_limit_check="", daily_card_limit_check="", weekly_card_limit_check="", monthly_card_limit_check="", daily_card_amount_limit_check="", weekly_card_amount_limit_check="" , monthly_card_amount_limit_check="" , min_trans_amount = "", max_trans_amount = "", isActive = "", priority = "", isTest = "", terminalid = "", isRecurring = "", isRestrictedTicketActive = "", isTokenizationActive = "", ManualRecurring = "", addressDetails = "", addressValidation = "", cardDetailRequired = "", isPSTTerminal = "", isCardEncryptionEnable = "", riskRuleActivation = "", settlementCurrency = "", daily_avg_ticket = "", weekly_avg_ticket = "", monthly_avg_ticket = "", min_payout_amount = "", payoutActivation = "", autoRedirectRequest = "", reject3DCard = "", isCardWhitelisted = "", isEmailWhitelisted = "", currencyConversion = "", conversionCurrency = "", bin_routing = "", emi_support = "", whitelisting = "", cardLimitCheck = "", cardAmountLimitCheck = "", amountLimitCheck = "", actionExecutorId = "", actionExecutorName = "", processor_partnerid = "",payout_priority="";

                        if (accountId != null)
                        {
                            isReadOnly       = "";
                            paymodeid        = ((String) temphash.get("paymodeid"));
                            paymodeModeName  = GatewayAccountService.getPaymentTypes(paymodeid);
                            if(functions.isEmptyOrNull(paymodeModeName)){
                                paymodeModeName = "";
                            }else{
                                paymodeModeName = "-"+paymodeModeName;
                            }
                            cardtypeid    = ((String) temphash.get("cardtypeid"));
                            cardTypeName  = GatewayAccountService.getPaymentBrand(cardtypeid);
                            if(functions.isEmptyOrNull(cardTypeName)){
                                cardTypeName = "";
                            }else{
                                cardTypeName = "-"+cardTypeName;
                            }
                            monthly_amount_limit    = (new BigDecimal((String) temphash.get("monthly_amount_limit"))).toString();
                            weekly_amount_limit     = (new BigDecimal((String) temphash.get("weekly_amount_limit"))).toString();
                            daily_amount_limit      = (new BigDecimal((String) temphash.get("daily_amount_limit"))).toString();
                            daily_card_limit        = (new BigDecimal((String) temphash.get("daily_card_limit"))).toString();
                            weekly_card_limit       = (new BigDecimal((String) temphash.get("weekly_card_limit"))).toString();
                            monthly_card_limit      = (new BigDecimal((String) temphash.get("monthly_card_limit"))).toString();

                            daily_card_amount_limit     = (new BigDecimal((String) temphash.get("daily_card_amount_limit"))).toString();
                            weekly_card_amount_limit    = (new BigDecimal((String) temphash.get("weekly_card_amount_limit"))).toString();
                            monthly_card_amount_limit   = (new BigDecimal((String) temphash.get("monthly_card_amount_limit"))).toString();

                            daily_avg_ticket    = (new BigDecimal((String) temphash.get("daily_avg_ticket"))).toString();
                            weekly_avg_ticket   = (new BigDecimal((String) temphash.get("weekly_avg_ticket"))).toString();
                            monthly_avg_ticket  = (new BigDecimal((String) temphash.get("monthly_avg_ticket"))).toString();

                            min_trans_amount    = (new BigDecimal((String) temphash.get("min_transaction_amount"))).toString();
                            max_trans_amount    = (new BigDecimal((String) temphash.get("max_transaction_amount"))).toString();
                            min_payout_amount   = (new BigDecimal((String) temphash.get("min_payout_amount"))).toString();

                            daily_amount_limit_check = (String) temphash.get("daily_amount_limit_check");
                            weekly_amount_limit_check = (String) temphash.get("weekly_amount_limit_check");
                            monthly_amount_limit_check = (String) temphash.get("monthly_amount_limit_check");
                            daily_card_limit_check = (String) temphash.get("daily_card_limit_check");
                            weekly_card_limit_check = (String) temphash.get("weekly_card_limit_check");
                            monthly_card_limit_check = (String) temphash.get("monthly_card_limit_check");
                            daily_card_amount_limit_check = (String) temphash.get("daily_card_amount_limit_check");
                            weekly_card_amount_limit_check = (String) temphash.get("weekly_card_amount_limit_check");
                            monthly_card_amount_limit_check = (String) temphash.get("monthly_card_amount_limit_check");

                            isActive    = (String) temphash.get("isActive");
                            priority    = (String) temphash.get("priority");
                            isTest      = (String) temphash.get("isTest");
                            terminalid  = (String) temphash.get("terminalid");
                            isRecurring = (String) temphash.get("is_recurring");
                            isRestrictedTicketActive    = (String) temphash.get("isRestrictedTicketActive");
                            isTokenizationActive        = (String) temphash.get("isTokenizationActive");
                            ManualRecurring     = (String) temphash.get("isManualRecurring");
                            addressDetails      = (String) temphash.get("addressDetails");
                            addressValidation   = (String) temphash.get("addressValidation");
                            cardDetailRequired  = (String) temphash.get("cardDetailRequired");
                            isPSTTerminal       = (String) temphash.get("isPSTTerminal");
                            isCardEncryptionEnable  = (String) temphash.get("isCardEncryptionEnable");
                            riskRuleActivation      = (String) temphash.get("riskruleactivation");
                            settlementCurrency      = (String) temphash.get("settlement_currency");
                            payoutActivation        = (String) temphash.get("payoutActivation");
                            autoRedirectRequest     = (String) temphash.get("autoRedirectRequest");
                            reject3DCard            = (String) temphash.get("reject3DCard");
                            isCardWhitelisted   = (String) temphash.get("isCardWhitelisted");
                            isEmailWhitelisted  = (String) temphash.get("isEmailWhitelisted");
                            currencyConversion  = (String) temphash.get("currency_conversion");
                            bin_routing     = (String) temphash.get("binRouting");
                            emi_support     = (String) temphash.get("emi_support");
                            whitelisting    = (String) temphash.get("whitelisting_details");
                            cardLimitCheck  = (String) temphash.get("cardLimitCheck");
                            cardAmountLimitCheck    = (String) temphash.get("cardAmountLimitCheck");
                            amountLimitCheck        = (String) temphash.get("amountLimitCheck");
                            actionExecutorId        = (String) temphash.get("actionExecutorId");
                            actionExecutorName      = (String) temphash.get("actionExecutorName");
                            conversionCurrency      = (String) temphash.get("conversion_currency");
                            processor_partnerid     = (String) temphash.get("processor_partnerid");
                            payout_priority     = (String) temphash.get("payout_priority");
                        }
                %>
                <tr>
                    <td>
                        <table>
                            <tr>
                                <td>
                                    <%--<form id="details<%=pos%>" action="/icici/servlet/SetAccount?ctoken=<%=ctoken%>"
                                          method=post name="xyz">--%>
                                    <style>
                                        #checkboxes <%=pos%> {
                                            display: none;
                                            border: 1px #dadada solid;
                                            position: absolute;
                                            width: 100%;
                                            background-color: #ffffff;
                                            z-index: 1;
                                            max-height: 100px;
                                            overflow-x: auto;
                                        }
                                    </style>
                                    <script>
                                        var expanded1 = false;
                                        function showCheckboxes1(checkbox)
                                        {
                                            console.log(checkbox)
                                            var checkboxes = document.getElementById(checkbox);
                                            if (!expanded1)
                                            {
                                                checkboxes.style.display = "block";
                                                expanded1 = true;
                                            }
                                            else
                                            {
                                                checkboxes.style.display = "none";
                                                expanded1 = false;
                                            }
                                        }
                                        function hideCheckbox(id)
                                        {
                                            console.log(" in hide --", id)
                                            var checkboxes = document.getElementById(id);
                                            checkboxes.style.display = "none";
                                            expanded1 = false;
                                        }
                                    </script>
                                    <%--<input type="hidden" value="<%=terminalid%>" name="terminalid" id="<%=terminalid%>">--%>
                                    <input type="hidden" value="<%=ctoken%>" id="ctoken" name="ctoken">
                                    <input type=hidden value="<%=accountId%>" name="accountid_<%=terminalid%>">
                                    <input type=hidden value="<%=terminalid%>" name="terminalid_<%=terminalid%>">
                                        <input type="hidden" value="" name="onchangedvalue_<%=terminalid%>" id="onchangedvalue_<%=terminalid%>">   <%--***do not remove the field*****--%>


                                        <table align=left
                                           class="table table-striped table-bordered table-hover table-green dataTable">
                                        <tr>
                                            <td width="50%" valign="middle" class="textb">
                                                <input type="checkbox" value="<%=terminalid%>" name="terminalid" id="<%=terminalid%>">
                                                &nbsp;&nbsp;&nbsp;&nbsp;<b>Terminal ID</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Member
                                                ID</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Account
                                                ID</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>PayMode
                                                ID</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Card
                                                Type ID</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb" style="min-width: 150px;"><b>Daily Amount Limit</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb" style="min-width: 150px;"><b>Weekly Amount Limit</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb" style="min-width: 150px;"><b>Monthly Amount Limit</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb" style="min-width: 150px;"><b>Daily Card Limit</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb" style="min-width: 150px;"><b>Weekly Card Limit</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb" style="min-width: 150px;"><b>Monthly Card Limit</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb">
                                                <b>Action</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb">
                                                <b>Address<BR>DetailsDisplay</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Is
                                                PSTTerminal</b></td>
                                        </tr>

                                        <tr>
                                            <td width="50" align="center" class="<%=style%>">
                                                <b><%=ESAPI.encoder().encodeForHTMLAttribute(terminalid)%>
                                                </b></td>
                                            <td width="100" align="center" class="<%=style%>"><input type=text
                                                                                                     style="width:100px;"
                                                                                                     class="txtboxsmall"
                                                                                                     name="memberid_<%=terminalid%>"
                                                                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberId)%>"
                                                                                                     readonly></td>
                                            <td width="50" align="center" class="<%=style%>">
                                                <button type="submit" class="goto"
                                                        onclick="window.open('GetGatewayAccount?accountid=<%=ESAPI.encoder().encodeForHTMLAttribute(accountId)%>&ctoken=<%=user.getCSRFToken()%>', 'newwindow', 'width=500, height=350'); return false;"><%=ESAPI.encoder().encodeForHTMLAttribute(accountId)%>
                                                </button>
                                            </td>
                                            <td width="100" align="center" class="<%=style%>"><input type=text
                                                                                                     style="width:100px;"
                                                                                                     class="txtboxsmall"
                                                                                                     align="center"
                                                                                                     size=10
                                                                                                     name='paymodeid_<%=terminalid%>'
                                                                                                     value="<%=paymodeid+""+paymodeModeName%>"
                                                                                                     readonly></td>
                                            <td width="100" align="center" class="<%=style%>"><input type=text
                                                                                                     class="txtboxsmall"
                                                                                                     style="width:100px;"
                                                                                                     name='cardtypeid_<%=terminalid%>'
                                                                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute(cardtypeid)+""+cardTypeName%>"
                                                                                                     readonly></td>
                                            <td width="100" align="center" class="<%=style%>"><input type=text
                                                                                                     class="txtboxsmall"
                                                                                                     size=10
                                                                                                     name='daily_amount_limit_<%=terminalid%>'
                                                                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute(daily_amount_limit)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Daily Amount Limit',<%=terminalid%>)">
                                                                                            <select name="daily_amount_limit_check_<%=terminalid%>" onchange="ChangeFunction(this.value,'daily_amount_limit_check',<%=terminalid%>)">
                                                                                                <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(daily_amount_limit_check)));%>
                                                                                            </select>
                                            </td>
                                            <td width="100" align="center" class="<%=style%>"><input type=text
                                                                                                     class="txtboxsmall"
                                                                                                     size=10
                                                                                                     name='weekly_amount_limit_<%=terminalid%>'
                                                                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute(weekly_amount_limit)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Weekly Amount Limit',<%=terminalid%>)">
                                                                                            <select name="weekly_amount_limit_check_<%=terminalid%>" onchange="ChangeFunction(this.value,'weekly_amount_limit_check',<%=terminalid%>)">
                                                                                                <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(weekly_amount_limit_check)));%>
                                                                                            </select>
                                            </td>
                                            <td width="100" align="center" class="<%=style%>"><input type=text
                                                                                                     class="txtboxsmall"
                                                                                                     size=10
                                                                                                     name='monthly_amount_limit_<%=terminalid%>'
                                                                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute(monthly_amount_limit)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Monthly Account Amount Limit',<%=terminalid%>)">
                                                                                            <select name="monthly_amount_limit_check_<%=terminalid%>" onchange="ChangeFunction(this.value,'monthly_amount_limit_check',<%=terminalid%>)">
                                                                                                        <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(monthly_amount_limit_check)));%>
                                                                                            </select>
                                            </td>
                                            <td width="100" align="center" class="<%=style%>"><input type=text
                                                                                                     class="txtboxsmall"
                                                                                                     size=10
                                                                                                     name='daily_card_limit_<%=terminalid%>'
                                                                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute(daily_card_limit)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Daily Card Limit',<%=terminalid%>)">
                                                                                             <select name="daily_card_limit_check_<%=terminalid%>" onchange="ChangeFunction(this.value,'daily_card_limit_check',<%=terminalid%>)">
                                                                                                        <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(daily_card_limit_check)));%>
                                                                                             </select>
                                            </td>
                                            <td width="100" align="center" class="<%=style%>"><input type=text
                                                                                                     class="txtboxsmall"
                                                                                                     size=10
                                                                                                     name='weekly_card_limit_<%=terminalid%>'
                                                                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute(weekly_card_limit)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Weekly Card Limit',<%=terminalid%>)">
                                                                                              <select name="weekly_card_limit_check_<%=terminalid%>" onchange="ChangeFunction(this.value,'weekly_card_limit_check',<%=terminalid%>)">
                                                                                                        <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(weekly_card_limit_check)));%>
                                                                                              </select>
                                            </td>
                                            <td width="100" align="center" class="<%=style%>"><input type=text
                                                                                                     class="txtboxsmall"
                                                                                                     size=10
                                                                                                     name='monthly_card_limit_<%=terminalid%>'
                                                                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute(monthly_card_limit)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Monthly Card Limit',<%=terminalid%>)">
                                                                                              <select name="monthly_card_limit_check_<%=terminalid%>" onchange="ChangeFunction(this.value,'monthly_card_limit_check',<%=terminalid%>)">
                                                                                                        <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(monthly_card_limit_check)));%>
                                                                                              </select>
                                            </td>
                                            <td width="100" align="center" class="<%=style%>"><select
                                                    class="txtboxsmall" id="action_<%=terminalid%>"
                                                    name="action_<%=terminalid%>" onchange="ChangeFunction(this.value,'Action',<%=terminalid%>)">
                                                <option value="UPDATE_<%=terminalid%>" default>UPDATE</option>
                                                <option value="DELETE_<%=terminalid%>">DELETE</option>
                                            </select></td>
                                            <td class="txtboxsmall" align="center" width="100" class="<%=style%>"><select
                                                    name='addressDetails_<%=terminalid%>' onchange="ChangeFunction(this.value,'AddressDetailsDisplay',<%=terminalid%>)"><%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(addressDetails)));%></select>
                                            </td>
                                            <td class="txtboxsmall" align="center" width="100" class="<%=style%>"><select
                                                    name='isPSTTerminal_<%=terminalid%>' onchange="ChangeFunction(this.value,'Is PSTTerminal',<%=terminalid%>)"><%=Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(isPSTTerminal))%>
                                            </select></td>
                                            <td width="100" align="center" class="<%=style%>"><input type="button"
                                                                                                     onclick="confirmsubmit2(<%=terminalid%>)"
                                                                                                     class="goto"
                                                                                                     value="Save">
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="textb" width="100" valign="middle" align="center"><b>daily_card_amount_limit</b>
                                            </td>
                                            <td class="textb" width="100" valign="middle" align="center"><b>weekly_card_amount_limit</b>
                                            </td>
                                            <td class="textb" width="100" valign="middle" align="center"><b>monthly_card_amount_limit</b>
                                            </td>
                                            <td class="textb" width="100" valign="middle" align="center"><b>min_trans_amount</b>
                                            </td>
                                            <td class="textb" width="100" valign="middle" align="center"><b>max_trans_amount</b>
                                            </td>
                                            <td class="textb" width="100" valign="middle" align="center"><b>daily_avg_ticket</b>
                                            </td>
                                            <td class="textb" width="100" valign="middle" align="center"><b>weekly_avg_ticket</b>
                                            </td>
                                            <td class="textb" width="100" valign="middle" align="center"><b>monthly_avg_ticket</b>
                                            </td>
                                            <td class="textb" width="100" valign="middle" align="center">
                                                <b>isActive</b></td>
                                            <td class="textb" width="100" valign="middle" align="center">
                                                <b>priority</b></td>
                                            <td class="textb" width="100" valign="middle" align="center">
                                                <b>isTest</b></td>
                                            <td class="textb" width="100" valign="middle" align="center"><b>AutomaticRecurring</b>
                                            </td>
                                            <td class="textb" width="100" valign="middle" align="center"><b>isRestrictedTicketActive</b>
                                            </td>
                                            <td class="textb" width="100" valign="middle" align="center"><b>isTokenizationActive</b>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td align="center" width="100" class="<%=style%>"><input type=text
                                                                                                     class="txtboxsmall"
                                                                                                     size=10
                                                                                                     name='daily_card_amount_limit_<%=terminalid%>'
                                                                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute(daily_card_amount_limit)%>" onchange="ChangeFunction(this.value,'daily_card_amount_limit',<%=terminalid%>)">
                                                                                              <select name="daily_card_amount_limit_check_<%=terminalid%>" onchange="ChangeFunction(this.value,'daily_card_amount_limit_check',<%=terminalid%>)">
                                                                                                        <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(daily_card_amount_limit_check)));%>
                                                                                              </select>
                                            </td>
                                            <td align="center" width="100" class="<%=style%>"><input type=text
                                                                                                     class="txtboxsmall"
                                                                                                     size=10
                                                                                                     name='weekly_card_amount_limit_<%=terminalid%>'
                                                                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute(weekly_card_amount_limit)%>" onchange="ChangeFunction(this.value,'weekly_card_amount_limit',<%=terminalid%>)">
                                                                                               <select name="weekly_card_amount_limit_check_<%=terminalid%>" onchange="ChangeFunction(this.value,'weekly_card_amount_limit_check',<%=terminalid%>)">
                                                                                                        <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(weekly_card_amount_limit_check)));%>
                                                                                               </select>
                                            </td>
                                            <td align="center" width="100" class="<%=style%>"><input type=text
                                                                                                     class="txtboxsmall"
                                                                                                     size=10
                                                                                                     name='monthly_card_amount_limit_<%=terminalid%>'
                                                                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute(monthly_card_amount_limit)%>" onchange="ChangeFunction(this.value,'monthly_card_amount_limit',<%=terminalid%>)">
                                                                                              <select name="monthly_card_amount_limit_check_<%=terminalid%>" onchange="ChangeFunction(this.value,'monthly_card_amount_limit_check',<%=terminalid%>)">
                                                                                                        <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(monthly_card_amount_limit_check)));%>
                                                                                              </select>
                                            </td>
                                            <td align="center" width="100" class="<%=style%>"><input type=text
                                                                                                     class="txtboxsmall"
                                                                                                     size=10
                                                                                                     name='min_trans_amount_<%=terminalid%>'
                                                                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute(min_trans_amount)%>" onchange="ChangeFunction(this.value,'min_trans_amount',<%=terminalid%>)">
                                            </td>
                                            <td align="center" width="100" class="<%=style%>"><input type=text
                                                                                                     class="txtboxsmall"
                                                                                                     size=10
                                                                                                     name='max_trans_amount_<%=terminalid%>'
                                                                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute(max_trans_amount)%>" onchange="ChangeFunction(this.value,'max_trans_amount',<%=terminalid%>)">
                                            </td>
                                            <td align="center" width="100" class="<%=style%>"><input type=text
                                                                                                     class="txtboxsmall"
                                                                                                     size=10
                                                                                                     name='daily_avg_ticket_<%=terminalid%>'
                                                                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute(daily_avg_ticket)%>" onchange="ChangeFunction(this.value,'daily_avg_ticket',<%=terminalid%>)">
                                            </td>
                                            <td align="center" width="100" class="<%=style%>"><input type=text
                                                                                                     class="txtboxsmall"
                                                                                                     size=10
                                                                                                     name='weekly_avg_ticket_<%=terminalid%>'
                                                                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute(weekly_avg_ticket)%>" onchange="ChangeFunction(this.value,'weekly_avg_ticket',<%=terminalid%>)">
                                            </td>
                                            <td align="center" width="100" class="<%=style%>"><input type=text
                                                                                                     class="txtboxsmall"
                                                                                                     size=10
                                                                                                     name='monthly_avg_ticket_<%=terminalid%>'
                                                                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute(monthly_avg_ticket)%>" onchange="ChangeFunction(this.value,'monthly_avg_ticket',<%=terminalid%>)">
                                            </td>
                                            <td align="center" width="100" class="<%=style%>"><select
                                                    name='isActive_<%=terminalid%>' onchange="ChangeFunction(this.value,'isActive',<%=terminalid%>)"><%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(isActive)));%></select>
                                            </td>
                                            <td align="center" width="100" class="<%=style%>"><select id="priority2"
                                                                                                      name="priority_<%=terminalid%>" onchange="ChangeFunction(this.value,'priority',<%=terminalid%>)">
                                                <%
                                                    TreeMap<Integer,Integer> priorityHash = new TreeMap<Integer,Integer>();
                                                    for(int i=1;i<=200;i++)
                                                    {
                                                        priorityHash.put(i,i);
                                                    }

                                                    String isDef            = "";
                                                    String priorityvalue    = "";
                                                    for (Map.Entry map : priorityHash.entrySet())
                                                    {
                                                        priorityvalue = String.valueOf(map.getValue());

                                                        if ((priorityvalue.trim()).equals(ESAPI.encoder().encodeForHTMLAttribute(priority)))
                                                        {
                                                            isDef = "selected";
                                                        }
                                                        else
                                                        {
                                                            isDef = "";
                                                        }
                                                %>
                                                <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(priorityvalue)%>" <%=isDef%>   >
                                                    <%=ESAPI.encoder().encodeForHTML(priorityvalue)%>
                                                </option>
                                                <% } %>
                                            </select>
                                            </td>
                                            <td class="txtboxsmall" align="center" width="100" class="<%=style%>"><select
                                                    name='isTest_<%=terminalid%>' onchange="ChangeFunction(this.value,'isTest',<%=terminalid%>)"><%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(isTest)));%></select>
                                            </td>
                                            <td class="txtboxsmall" align="center" width="100" class="<%=style%>"><select
                                                    name='is_recurring_<%=terminalid%>'
                                                    id="automaticrecurring1_<%=terminalid%>" onchange="ChangeFunction(this.value,'AutomaticRecurring',<%=terminalid%>)"><%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(isRecurring)));%></select>
                                            </td>
                                            <td class="txtboxsmall" align="center" width="100" class="<%=style%>"><select
                                                    name='isRestrictedTicketActive_<%=terminalid%>' onchange="ChangeFunction(this.value,'isRestrictedTicketActive',<%=terminalid%>)"><%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(isRestrictedTicketActive)));%></select>
                                            </td>
                                            <td class="txtboxsmall" align="center" width="100" class="<%=style%>"><select
                                                    name='isTokenizationActive_<%=terminalid%>' onchange="ChangeFunction(this.value,'isTokenizationActive',<%=terminalid%>)"><%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(isTokenizationActive)));%></select>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="textb" width="100" valign="middle" align="center"><b>ManualRecurring</b>
                                            </td>
                                            <td class="textb" width="100" valign="middle" align="center"><b>AddressValidation</b>
                                            </td>
                                            <td class="textb" width="100" valign="middle" align="center"><b>CardDetailRequired</b>
                                            </td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Is
                                                CardEncryptionEnable</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Risk
                                                Rule Activation</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Settlement
                                                Currency</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Min
                                                Payout Amount</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Payout
                                                Activation</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Auto
                                                Redirect Request</b></td>
                                            <%--<td width="50%"valign="middle" align="center"  class="textb"><b>Reject 3D Card</b></td>--%>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Is
                                                CardWhitelisted</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Is
                                                EmailWhitelisted</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Currency
                                                Conversion</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Conversion
                                                Currency</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Is Bin
                                                Routing</b></td>


                                        </tr>
                                        <tr>
                                            <td class="txtboxsmall" align="center" width="100" class="<%=style%>"><select
                                                    name='isManualRecurring_<%=terminalid%>'
                                                    id="manualrecurring1_<%=terminalid%>" onchange="ChangeFunction(this.value,'ManualRecurring',<%=terminalid%>)"><%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(ManualRecurring)));%></select>
                                            </td>
                                            <td class="txtboxsmall" align="center" width="100" class="<%=style%>"><select
                                                    name='addressValidation_<%=terminalid%>'onchange="ChangeFunction(this.value,'AddressValidation',<%=terminalid%>)"><%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(addressValidation)));%></select>
                                            </td>
                                            <td class="txtboxsmall" align="center" width="100" class="<%=style%>"><select
                                                    name='cardDetailRequired_<%=terminalid%>' onchange="ChangeFunction(this.value,'CardDetailRequired',<%=terminalid%>)"><%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(cardDetailRequired)));%></select>
                                            </td>
                                            <td align="center" width="100" class="<%=style%>"><select
                                                    name='isCardEncryptionEnable_<%=terminalid%>'onchange="ChangeFunction(this.value,'Is CardEncryptionEnable',<%=terminalid%>)"><%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(isCardEncryptionEnable)));%></select>
                                            </td>
                                            <td align="center" width="100" class="<%=style%>"><select
                                                    name='riskruleactivation_<%=terminalid%>' onchange="ChangeFunction(this.value,'Risk Rule Activation',<%=terminalid%>)"><%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(riskRuleActivation)));%></select>
                                            </td>
                                            <td align="center" width="100" class="<%=style%>">
                                                <select name='settlementcurrency_<%=terminalid%>' onchange="ChangeFunction(this.value,'Settlement Currency',<%=terminalid%>)">
                                                    <%
                                                        for (Currency settlementcurrency : Currency.values())
                                                        {
                                                            String selected = "";
                                                            if (settlementcurrency.toString().equals(settlementCurrency))
                                                            {
                                                                selected = "selected";
                                                            }
                                                            out.println("<option value=\"" + settlementcurrency.toString() + "\" " + selected + ">" + settlementcurrency.toString() + "</option>");
                                                        }
                                                    %>
                                                </select>
                                            </td>
                                            <td align="center" width="100" class="<%=style%>"><input type=text
                                                                                                     class="txtboxsmall"
                                                                                                     size=10
                                                                                                     name='min_payout_amount_<%=terminalid%>'
                                                                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute(min_payout_amount)%>" onchange="ChangeFunction(this.value,'Min Payout Amount',<%=terminalid%>)">
                                            </td>
                                            <td align="center" width="100" class="<%=style%>"><select
                                                    name='payoutactivation_<%=terminalid%>' onchange="ChangeFunction(this.value,'Payout Activation',<%=terminalid%>)"><%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(payoutActivation)));%></select>
                                            </td>
                                            <td align="center" width="100" class="<%=style%>"><select
                                                    name='autoRedirectRequest_<%=terminalid%>' onchange="ChangeFunction(this.value,'Auto Redirect Request',<%=terminalid%>)"><%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(autoRedirectRequest)));%></select>
                                            </td>
                                            <%--<td  align="center" width="100" class="<%=style%>"><select name='reject3DCard'><%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(reject3DCard)));%></select></td>--%>
                                            <td align="center" width="100" class="<%=style%>"><select
                                                    name='isCardWhitelisted_<%=terminalid%>' onchange="ChangeFunction(this.value,'Is CardWhitelisted',<%=terminalid%>)"><%
                                                out.println(Functions.comboval2(ESAPI.encoder().encodeForHTMLAttribute(isCardWhitelisted)));%></select>
                                            </td>
                                            <td align="center" width="100" class="<%=style%>"><select
                                                    name='isEmailWhitelisted_<%=terminalid%>' onchange="ChangeFunction(this.value,'Is EmailWhitelisted	Currency ',<%=terminalid%>)"><%
                                                out.println(Functions.comboval2(ESAPI.encoder().encodeForHTMLAttribute(isEmailWhitelisted)));%></select>
                                            </td>
                                            <td align="center" width="100" class="<%=style%>">
                                                <select id="currency_con_<%=pos%>" multiple="multiple"
                                                        value="<%=currencyConversion%>" onchange="ChangeFunction(this.value,'Currency Conversion',<%=terminalid%>)">
                                                    <option class="option_<%=pos%>" value="Y">Sale
                                                    </option>
                                                    <option class="option_<%=pos%>" value="R">Refund
                                                    </option>
                                                    <option class="option_<%=pos%>" value="P">Payout
                                                    </option>
                                                </select>
                                                <input type="hidden" id="currencycon_<%=pos%>"
                                                       name="currency_conversion_<%=terminalid%>"
                                                       value="<%=currencyConversion%>">
                                                <script>
                                                    $(function ()
                                                    {
                                                        var value = [];
                                                        var details = '<%=currencyConversion%>';
                                                        value = details.split(",");
                                                        for (var i in value)
                                                        {
                                                            $("#currency_con_<%=pos%> option[value='" + value[i] + "']").prop('selected', true);
                                                        }
                                                        $('#currency_con_<%=pos%>').multiselect({
                                                            buttonText: function (options, select)
                                                            {
                                                                var labels = [];
                                                                if (options.length === 0)
                                                                {
                                                                    console.log(" in if");
                                                                    labels.pop();
                                                                    document.getElementById('currencycon_<%=pos%>').value = "N";
                                                                    return 'Select Currency Conversion';

                                                                }

                                                                else
                                                                {
                                                                    console.log(" in else");
                                                                    options.each(function ()
                                                                    {
                                                                        labels.push($(this).val());
                                                                    });
                                                                    console.log("Label::::" + labels);
                                                                    document.getElementById('currencycon_<%=pos%>').value = labels;
                                                                    return labels.join(', ') + '';

                                                                }

                                                            },
                                                            includeSelectAllOption: true
                                                        });
                                                    });
                                                </script>
                                            </td>

                                            <td align="center" width="100" class="<%=style%>">
                                                <select name='conversion_currency_<%=terminalid%>' onchange="ChangeFunction(this.value,'Conversion Currency',<%=terminalid%>)">
                                                    <%
                                                        for (Currency conversioncurrency : Currency.values())
                                                        {
                                                            String selected = "";
                                                            if (conversioncurrency.toString().equals(conversionCurrency))
                                                            {
                                                                selected = "selected";
                                                            }
                                                            out.println("<option value=\"" + conversioncurrency.toString() + "\" " + selected + ">" + conversioncurrency.toString() + "</option>");
                                                        }
                                                    %>
                                                </select>
                                            </td>
                                            <td align="center" width="100" class="<%=style%>"><select
                                                    name='bin_routing_<%=terminalid%>' onchange="ChangeFunction(this.value,'Is Bin Routing',<%=terminalid%>)"><%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(bin_routing)));%></select>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Emi
                                                Support</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Whitelist
                                                Details</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Card
                                                Limit Check</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Card
                                                Amount Limit Check</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Amount
                                                Limit Check</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Action
                                                Executor Id</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Action
                                                Executor Name</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Processor
                                                Partner ID</b></td>
                                            <td width="50%" valign="middle" align="center" class="textb"><b>Payout
                                                Priority</b></td>


                                        </tr>
                                        <tr>
                                            <td align="left" width="100" class="<%=style%>"><select
                                                    name='emi_support_<%=terminalid%>' onchange="ChangeFunction(this.value,'Emi Support',<%=terminalid%>)"><%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(emi_support)));%></select>
                                            </td>
                                            <td align="center" width="100" class="<%=style%>">
                                                <select id="whitelist_<%=pos%>" multiple="multiple"
                                                        value="<%=whitelisting%>" onchange="ChangeFunctionWhitelistDetails(this,'Whitelist Details',<%=terminalid%>)">
                                                    <option class="option_<%=pos%>" value="name">Card Holder Name
                                                    </option>
                                                    <option class="option_<%=pos%>" value="ipAddress">IP Address
                                                    </option>
                                                    <option class="option_<%=pos%>" value="expiryDate">Expiry Date
                                                    </option>
                                                </select>
                                                <input type="hidden" id="whitelistcode_<%=pos%>"
                                                       name="whitelistdetails_<%=terminalid%>"
                                                       value="<%=whitelisting%>">
                                                <script>
                                                    $(function ()
                                                    {
                                                        var value = [];
                                                        var details = '<%=whitelisting%>';
                                                        value = details.split(",");
                                                        for (var i in value)
                                                        {
                                                            $("#whitelist_<%=pos%> option[value='" + value[i] + "']").prop('selected', true);
                                                        }
                                                        $('#whitelist_<%=pos%>').multiselect({
                                                            buttonText: function (options, select)
                                                            {
                                                                var labels = [];
                                                                if (options.length === 0)
                                                                {
                                                                    console.log(" in if");
                                                                    labels.pop();
                                                                    document.getElementById('whitelistcode_<%=pos%>').value = labels;
                                                                    return 'Select Whitelist Details';
                                                                }
                                                                else
                                                                {
                                                                    console.log(" in else");
                                                                    options.each(function ()
                                                                    {
                                                                        labels.push($(this).val());
                                                                    });
                                                                    console.log("Label::::" + labels);
                                                                    document.getElementById('whitelistcode_<%=pos%>').value = labels;
                                                                    return labels.join(', ') + '';
                                                                }
                                                            }
                                                        });
                                                    });
                                                </script>
                                            </td>
                                            <td align="center" width="100" class="<%=style%>"><select
                                                    name='cardLimitCheck_<%=terminalid%>' onchange="ChangeFunction(this.value,'Card Limit Check',<%=terminalid%>)" style="height: 20px;"><%
                                                out.println(Functions.comboval3(ESAPI.encoder().encodeForHTMLAttribute(cardLimitCheck)));%></select>
                                            </td>
                                            <td align="center" width="100" class="<%=style%>"><select
                                                    name='cardAmountLimitCheck_<%=terminalid%>' onchange="ChangeFunction(this.value,'Card Amount Limit Check',<%=terminalid%>)"
                                                    style="height: 20px;"><%
                                                out.println(Functions.comboval3(ESAPI.encoder().encodeForHTMLAttribute(cardAmountLimitCheck)));%></select>
                                            </td>
                                            <td align="center" width="100" class="<%=style%>"><select
                                                    name='amountLimitCheck_<%=terminalid%>'  onchange="ChangeFunction(this.value,'Amount Limit Check',<%=terminalid%>)
                                                    "style="height: 20px;"><%
                                                out.println(Functions.getAmountLimitCheckDropDown(ESAPI.encoder().encodeForHTMLAttribute(amountLimitCheck)));%></select>
                                            </td>
                                            <%
                                                if (!functions.isValueNull(actionExecutorId))
                                                    actionExecutorId = "-";
                                                if (!functions.isValueNull(actionExecutorName))
                                                    actionExecutorName = "-";
                                            %>
                                            <td align="center" width="100" class="<%=style%>">
                                                <b><%= actionExecutorId%>
                                                </b></td>
                                            <td align="center" width="100" class="<%=style%>">
                                                <b><%= actionExecutorName%>
                                                </b></td>
                                           <%
                                                if(!functions.isValueNull(processor_partnerid))
                                                    processor_partnerid="";
                                            %>
                                            <td width="100" align="center" class="<%=style%>"><input type=text
                                                                                                     class="txtbox"
                                                                                                     align="center"
                                                                                                     size=10
                                                                                                     name='processor_partnerid_<%=terminalid%>'
                                                                                                     id="allpid_processor"
                                                                                                     autocomplete="on"
                                                                                                     value="<%=processor_partnerid%>" onchange="ChangeFunction(this.value,'Processor Partner ID',<%=terminalid%>)">
                                            </td>
                                            <td align="center" width="100" class="<%=style%>"><select id="payout_priority2"
                                                                                                      name="payout_priority_<%=terminalid%>" onchange="ChangeFunction(this.value,'Payout Priority',<%=terminalid%>)">
                                                <%
                                                    LinkedHashMap payoutpriorityHash = new LinkedHashMap();
                                                    payoutpriorityHash.put("1", "1");
                                                    payoutpriorityHash.put("2", "2");
                                                    payoutpriorityHash.put("3", "3");
                                                    payoutpriorityHash.put("4", "4");
                                                    payoutpriorityHash.put("5", "5");
                                                    payoutpriorityHash.put("6", "6");
                                                    payoutpriorityHash.put("7", "7");
                                                    payoutpriorityHash.put("8", "8");
                                                    Set payoutpriorityset = payoutpriorityHash.keySet();

                                                    String isDefPayoutPriority    = "";
                                                    String payoutpriorityvalue    = "";
                                                    Iterator i= payoutpriorityset.iterator();
                                                    while (i.hasNext())
                                                    {
                                                        payoutpriorityvalue = (String) i.next();

                                                        if ((payoutpriorityvalue.trim()).equals(ESAPI.encoder().encodeForHTMLAttribute(payout_priority)))
                                                        {
                                                            isDefPayoutPriority = "selected";
                                                        }
                                                        else
                                                        {
                                                            isDefPayoutPriority = "";
                                                        }
                                                %>
                                                <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(payoutpriorityvalue)%>" <%=isDefPayoutPriority%>   >
                                                    <%=ESAPI.encoder().encodeForHTML(payoutpriorityvalue)%>
                                                </option>
                                                <% } %>
                                            </select>
                                            </td>
                                        </tr>

                                    </table>
                                    <%-- </form>--%>
                                        <% if(pos<records){ %>
                            <tr bgcolor="#FFFFFF" height=50>
                                <td colspan=11>
                                    &nbsp;
                                </td>
                            </tr>
                            <%
                                }
                            %>
                            </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <%
                    } //end for
                %>
            </table>
            <input type="button" onclick="confirmsubmit3()" class="goto" value="Save All"
                   style="font-size: 16px;margin-left: 681px;"></td>

        </form>
    </div>
    <%}
    else
    {%>
    <div class="scroll">
    <div style="width:100%; overflow: auto">
        <table border="1" cellpadding="5" cellspacing="0" width="750" align="center" class="table table-striped table-bordered table-hover table-green dataTable" style="width: 100%;overflow: auto;">
            <tr>
                <td width="10%" valign="middle" align="center" class="th0"><b>Terminal ID</b></td>
                <td width="8%" valign="middle" align="center" class="th0"><b>Member ID</b></td>
                <td width="9%" valign="middle" align="center" class="th0"><b>Account ID</b></td>
                <td width="15%" valign="middle" align="center" class="th0"><b>Aliasname</b></td>
                <td width="12%" valign="middle" align="center" class="th0"><b>Displayname</b></td>
                <td width="9%" valign="middle" align="center" class="th0"><b>Daily_Amount_Limit</b></td>
                <td width="9%" valign="middle" align="center" class="th0"><b>Max_Transaction_amount</b></td>
                <td width="9%" valign="middle" align="center" class="th0"><b>Min_Transaction_amount</b></td>
                <td width="10%" valign="middle" align="center" class="th0"><b>PayMode ID</b></td>
                <td width="10%" valign="middle" align="center" class="th0"><b>Card Type ID</b></td>
                <td width="10%" valign="middle" align="center" class="th0"><b>Currency</b></td>
                <td width="7%" valign="middle" align="center" class="th0"><b>Priority</b></td>
                <td width="5%" valign="middle" align="center" class="th0"><b>IsActive</b></td>
                <td width="5%" valign="middle" align="center" class="th0"><b>PayoutActivation</b></td>
                <td width="5%" valign="middle" align="center" class="th0"><b>PayoutPriority</b></td>

            </tr>

                <%
                    String isActive = "", priority = "",  terminalid = "",payout_priority="",payoutActivation="",aliasname="",iscardwhitelisted="", dailyamountlimit="",maxtransamt="",mintransamt="",displayName="";
                    for (int pos = 1; pos <= records; pos++)
                    {
                        String id           = Integer.toString(pos);
                        int srno            = Integer.parseInt(id);
                        temphash            = (Hashtable) hash.get(id);
                        memberId            = (String) temphash.get("memberid");
                        dailyamountlimit            = (String) temphash.get("daily_amount_limit");
                        maxtransamt            = (String) temphash.get("max_transaction_amount");
                        mintransamt            = (String) temphash.get("min_transaction_amount");
                        String accountId    = Functions.checkStringNull((String) temphash.get("accountid"));
                        isActive            = (String) temphash.get("isActive");
                        priority            = (String) temphash.get("priority");
                        terminalid          = (String) temphash.get("terminalid");
                        payoutActivation        = (String) temphash.get("payoutActivation");

                        payout_priority     = (String) temphash.get("payout_priority");


                        paymodeid   = "N/A";
                        cardtypeid  = "N/A";

                        if (accountId != null)
                        {

                            paymodeid        = ((String) temphash.get("paymodeid"));
                            paymodeModeName  = GatewayAccountService.getPaymentTypes(paymodeid);
                            currency         =GatewayAccountService.getGatewayAccount(accountId).getCurrency();

                            aliasname       =GatewayAccountService.getGatewayAccount(accountId).getAliasName();
                            displayName     =GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                            if(functions.isEmptyOrNull(paymodeModeName)){
                                paymodeModeName = "";
                            }else{
                                paymodeModeName = "-"+paymodeModeName;
                            }
                            cardtypeid    = ((String) temphash.get("cardtypeid"));
                            cardTypeName  = GatewayAccountService.getPaymentBrand(cardtypeid);
                            if(functions.isEmptyOrNull(cardTypeName)){
                                cardTypeName = "";
                            }else{
                                cardTypeName = "-"+cardTypeName;
                            }

                        }
                        paymodeModeName = ESAPI.encoder().encodeForHTML(paymodeid) +" "+ paymodeModeName;
                        cardTypeName    = ESAPI.encoder().encodeForHTML(cardtypeid) +" "+ cardTypeName;

                %>
            <tr class="tr1">
                    <td align=center><%=terminalid %></td>
                    <td align=center><%=memberId%></td>
                    <td align=center><%=accountId%></td>
                    <td align=center><%=aliasname%></td>
                    <td align="center"><%=displayName%></td>
                    <td align=center><%=dailyamountlimit%></td>
                    <td align=center><%=maxtransamt%></td>
                    <td align=center><%=mintransamt%></td>
                    <td align=center><%=paymodeModeName%></td>
                    <td align=center><%=cardTypeName%></td>
                    <td align=center><%=currency%></td>
                    <td align=center><%=priority%></td>
                    <td align=center><%=isActive%></td>
                    <td align=center><%=payoutActivation%></td>
                    <td align=center><%=payout_priority%></td>
            </tr>
               <%
                   }
               %>


        </table>
    </div>
    </div>
    </div>
    <%

    }
    }
%>


</div>


<%
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;

    }
%>
</body>
</html>