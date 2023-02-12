<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger"%>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.enums.Currency" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.*" %>
<%@ include file="top.jsp" %>
<%
    String company      = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    session.setAttribute("submit","MemberMappingDetails");
    Functions functions                     = new Functions();
    ArrayList<String> payoutLimitArrayList  = new ArrayList<String>();

    payoutLimitArrayList.add("1");
    payoutLimitArrayList.add("2");
    payoutLimitArrayList.add("3");
    payoutLimitArrayList.add("4");
    payoutLimitArrayList.add("5");
    payoutLimitArrayList.add("6");
    payoutLimitArrayList.add("7");
    payoutLimitArrayList.add("8");

%>
<%!
    private static Logger log=new Logger("membermappingpreference.jsp");
%>
<html>
<head>
   <%-- <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
    <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
    <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <script src="/partner/NewCss/am_multipleselection/bootstrap-multiselect.js"></script>
    <link href="/partner/NewCss/am_multipleselection/bootstrap-multiselect.css" rel="stylesheet" />
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.8.1/css/all.css">
    <style type="text/css">
        #maintitle{
            text-align: center;
            background: #7eccad;
            color: #fff;
            font-size: 14px;
        }

        @media(min-width: 640px){
            #saveid{
                position: absolute;
                background: #F9F9F9!important;
            }

            #savetable{
                padding-bottom: 25px;
            }

            table.table{
                margin-bottom: 6px !important;
            }

            table#savetable td:before{
                font-size: inherit;
            }
        }

        table#savetable td:before{
            font-size: 13px;
            font-family: Open Sans;
        }

        table.table{
            margin-bottom: 0px !important;
        }

        #saveid input{
            font-size: 16px;
            padding-right: 30px;
            padding-left: 30px;
        }


        .multiselect {
            width: 100%;

        }

        .selectBox {
            position: relative;
        }

        .selectBox select {
            width: 100%;
            font-weight: bold;
        }

        .overSelect {
            position: absolute;
            left: 0;
            right: 0;
            top: 0;
            bottom: 0;
        }

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

        #checkboxes_1 {
            display: none;
            border: 1px #dadada solid;
            position: absolute;
            width: 100%;
            background-color: #ffffff;
            z-index: 1;
            height: 130px;
            overflow-x: auto;
        }

        #checkboxes_1 label {
            display: block;
        }

        #checkboxes_1 label:hover {
            background-color: #1e90ff;
        }

        #checkboxes_2 {
            display: none;
            border: 1px #dadada solid;
            position: absolute;
            width: 100%;
            background-color: #ffffff;
            z-index: 1;
            height: 130px;
            overflow-x: auto;
        }

        #checkboxes_2 label {
            display: block;
        }

        #checkboxes_2 label:hover {
            background-color: #1e90ff;
        }

        input[type="checkbox"]{
            width: 18px; /*Desired width*/
            height: 18px; /*Desired height*/
        }

        /********************************************************************************/

        .multiselect-container>li {
            padding: 0;
            margin-left: 31px;
        }
        .open>#multiselect-id.dropdown-menu {
            display: block;
            padding-top: 5px;
            padding-bottom: 5px;
        }
        .multiselect-container>li>a>label {
            margin: 0;
            height: 28px;
            padding-left:1px; !important;
            text-align: left;
        }
        span.multiselect-native-select {
            position: relative;
        }

        @supports (-ms-ime-align:auto) {
            span.multiselect-native-select {
                position: static!important;
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
            width:100%;
            height: 30px;
            background-color: #fff;
        }
        #mainbtn-id.btn-default {
            color: #333;
            background-color: #fff;
            padding: 6px;
            border: 1px solid #b2b2b2;
            height: 33px;
        }
        .btn-group>.btn:first-child {
            margin-left: 0;
        }

        .btn-group>.btn:first-child {
            margin-left: 0;
        }

        .btn-group>.btn, .btn-group-vertical>.btn {
            position: relative;
            float: left;
        }
        .multiselect-container {
            position: absolute;
            list-style-type: none;
            margin: 0;
            padding: 0;
        }
        #multiselect-id.dropdown-menu {
            position: absolute;
            top: 100%;
            left: 0;
            z-index: 1000;
            display: none;
            float: left;
            min-width: 100%;
            font-size: 14px;
            font-family: "Open Sans","Helvetica Neue",Helvetica,Arial,sans-serif;
            list-style: none;
            background-color: #fff;
            border: 1px solid #ccc;
            border: 1px solid rgba(0,0,0,0.15);
            border-radius: 4px;
            -webkit-box-shadow: 0 6px 12px rgba(0,0,0,0.175);
            box-shadow: 0 6px 12px rgba(0,0,0,0.175);
            background-clip: padding-box;
        }
        #mainbtn-id .multiselect-selected-text{
            font-size: 12px;
            font: inherit;
            padding-right: 18px;
        }
        #mainbtn-id.btn-default, #mainbtn-id.btn-default:focus, #mainbtn-id.btn-default.focus, #mainbtn-id.btn-default:active, #mainbtn-id.btn-default.active, .open>.dropdown-toggle#mainbtn-id.btn-default {
            color: #333;
            /*color: #fff;*/
            background-color: white!important;
            text-align: left;
            width: 100%;
        }
        .tr1 .multiselect-native-select .btn-group #mainbtn-id .multiselect-selected-text , .tr0 .multiselect-native-select .btn-group #mainbtn-id .multiselect-selected-text
        {
            font-size: 13px;
            font: inherit;
        }
        .tr1 .multiselect-native-select .btn-group #mainbtn-id , .tr0 .multiselect-native-select .btn-group #mainbtn-id
        {
            border: 1px solid #ddd; !important;
        }
        .btn .caret { /*fa fa-chevron-down*/
            position: absolute;
            display: inline-block;!important;
            width: 0px;!important;
            height: 1px;!important;
            margin-left: 2px;!important;
            vertical-align: middle;!important;
            border-top: 7px solid;!important;
            border-right: 4px solid transparent;!important;
            border-left: 4px solid transparent;!important;
            float: right;!important;
            margin-top: 5px;!important;
            box-sizing: inherit;
            right: 5px;
            top: 15px;
            margin-top: -2px;
        }
        .fa-chevron-down{
            position: absolute;
            right:0px;
            top: 0px;
            margin-top: -2px;
            vertical-align: middle;
            float: right;
            font-size: 9px;
        }
        #mainbtn-id
        {
            overflow: hidden;
            display: block;
        }




    </style>
    <script>
        function ChangeFunction(Value , lable,terminal){
            console.log("terminal"+terminal);
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
    </script>
</head>
<title><%=company%> Merchant Management> Merchant Account Mapping</title>

<body>


<%
    PartnerFunctions partnerFunctions    =new PartnerFunctions();
    ctoken      = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    String str  = "";
    if (partner.isLoggedInPartner(session))
    {
        TerminalManager terminalManager                 = new TerminalManager();
        LinkedHashMap<Integer,String> memberidDetails   = null;
        TreeMap<String,String> partneriddetails         = null;
        partneriddetails                                = partner.getPartnerDetailsForUI(String.valueOf(session.getAttribute("merchantid")));
        // String Roles        = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
        String partnerid=nullToStr(request.getParameter("partnerid"));
        String Config="";
        String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
        if(Roles.contains("superpartner")){

        }else{
            partnerid = String.valueOf(session.getAttribute("merchantid"));
            Config = "disabled";
        }
        String Style1       = "";
        String Style2       = "";
        String dISABLED_Id  = "";

        String setSelected;
        HashMap<String,String> dropdown = new HashMap<String, String>();
        dropdown.put("Y","Y");
        dropdown.put("N","N");

        HashMap<String,String> dropdownlist = new HashMap<String, String>();
        dropdownlist.put("All","All");
        dropdownlist.put("Y","Active");
        dropdownlist.put("N","InActive");

        if(Roles.contains("superpartner")){
            memberidDetails = partner.getSuperPartnerMembersDetail(String.valueOf(session.getAttribute("merchantid")));
            Style1          = "style=\"display: none\"";
        }else{
            memberidDetails     = partner.getPartnerMembersDetail(String.valueOf(session.getAttribute("merchantid")));
            dISABLED_Id         = String.valueOf(session.getAttribute("merchantid"));
            Style2              = "style=\"display: none\"";
        }
        String cardtypeid   = request.getParameter("cardtypeid");
        String paymodeid    = request.getParameter("paymodeid");
        String memberid     = nullToStr(request.getParameter("memberid"));
        String partnerSid   = nullToStr(request.getParameter("partnerid"));
        String accountid    = nullToStr(request.getParameter("accountid"));
        if (memberid != null) str = str + "&memberid=" + memberid;

        String paymodesearch="";
        if (request.getAttribute("paymodeid")!= null)
            paymodesearch= (String)request.getAttribute("paymodeid");

        String cardtypesearch="";
        if (request.getAttribute("cardtypeid")!= null)
            cardtypesearch= (String)request.getAttribute("cardtypeid");

        String ActiveOrInActive ="";
        if (request.getParameter("isactive") != null)
            ActiveOrInActive = request.getParameter("isactive");

        String ispayoutactive="";
        if(request.getParameter("ispayoutactive")!= null)
            ispayoutactive= request.getParameter("ispayoutactive");
        ResourceBundle rb1 = null;
        String language_property1   = (String) session.getAttribute("language_property");
        rb1                         = LoadProperties.getProperty(language_property1);

        String membermappingpreference_Merchant_Account     = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Merchant_Account")) ? rb1.getString("membermappingpreference_Merchant_Account") : "Merchant Account Mapping";
        String membermappingpreference_create_Account       = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_create_Account")) ? rb1.getString("membermappingpreference_create_Account") : "Create Account";
        String membermappingpreference_PartnerID            = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_PartnerID")) ? rb1.getString("membermappingpreference_PartnerID") : "Partner ID";
        String membermappingpreference_select_partnerid     = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_select_partnerid")) ? rb1.getString("membermappingpreference_select_partnerid") : "Select Partner Id";
        String membermappingpreference_MerchantID           = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_MerchantID")) ? rb1.getString("membermappingpreference_MerchantID") : "Merchant ID";
        String membermappingpreference_select_merchantid    = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_select_merchantid")) ? rb1.getString("membermappingpreference_select_merchantid") : "Select Merchant Id";
        String membermappingpreference_bank_Account         = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_bank_Account")) ? rb1.getString("membermappingpreference_bank_Account") : "Bank Account";
        String membermappingpreference_select_bank          = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_select_bank")) ? rb1.getString("membermappingpreference_select_bank") : "Select Bank Account";
        String membermappingpreference_Payment_Method       = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Payment_Method")) ? rb1.getString("membermappingpreference_Payment_Method") : "Payment Method";
        String membermappingpreference_select_Method        = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_select_Method")) ? rb1.getString("membermappingpreference_select_Method") : "Select Payment Method";
        String membermappingpreference_Limits               = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Limits")) ? rb1.getString("membermappingpreference_Limits") : "Limits";
        String membermappingpreference_daily1               = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_daily1")) ? rb1.getString("membermappingpreference_daily1") : "Daily Amount Limit";
        String membermappingpreference_weekly               = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_weekly")) ? rb1.getString("membermappingpreference_weekly") : "Weekly Amount Limit";
        String membermappingpreference_monthly_limit1       = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_monthly_limit1")) ? rb1.getString("membermappingpreference_monthly_limit1") : "Monthly Amount Limit";
        String membermappingpreference_daily_card           = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_daily_card")) ? rb1.getString("membermappingpreference_daily_card") : "Daily Card Limit";
        String membermappingpreference_weekly_card          = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_weekly_card")) ? rb1.getString("membermappingpreference_weekly_card") : "Weekly Card Limit";
        String membermappingpreference_monthly_limit        = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_monthly_limit")) ? rb1.getString("membermappingpreference_monthly_limit") : "Monthly Card Limit";
        String membermappingpreference_dailycard            = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_dailycard")) ? rb1.getString("membermappingpreference_dailycard") : "Daily Card Amount Limit";
        String membermappingpreference_weeklycard           = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_weeklycard")) ? rb1.getString("membermappingpreference_weeklycard") : "Weekly Card Amount Limit";
        String membermappingpreference_monthly_card         = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_monthly_card")) ? rb1.getString("membermappingpreference_monthly_card") : "Monthly Card Amount Limit";
        String membermappingpreference_daily_ticket         = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_daily_ticket")) ? rb1.getString("membermappingpreference_daily_ticket") : "Daily Average Ticket";
        String membermappingpreference_monthly_ticket       = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_monthly_ticket")) ? rb1.getString("membermappingpreference_monthly_ticket") : "Monthly Average Ticket";
        String membermappingpreference_min                  = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_min")) ? rb1.getString("membermappingpreference_min") : "Min Transaction Amount";
        String membermappingpreference_max                  = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_max")) ? rb1.getString("membermappingpreference_max") : "Max Transaction Amount";
        String membermappingpreference_weekly_ticket        = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_weekly_ticket")) ? rb1.getString("membermappingpreference_weekly_ticket") : "Weekly Average Ticket";
        String membermappingpreference_others               = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_others")) ? rb1.getString("membermappingpreference_others") : "Others";
        String membermappingpreference_terminal             = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_terminal")) ? rb1.getString("membermappingpreference_terminal") : "Terminal Status";
        String membermappingpreference_active               = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_active")) ? rb1.getString("membermappingpreference_active") : "Active";
        String membermappingpreference_inactive             = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_inactive")) ? rb1.getString("membermappingpreference_inactive") : "Inactive";
        String membermappingpreference_terminal_priority    = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_terminal_priority")) ? rb1.getString("membermappingpreference_terminal_priority") : "Terminal Priority";
        String membermappingpreference_1                    = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_1")) ? rb1.getString("membermappingpreference_1") : "1";
        String membermappingpreference_2                    = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_2")) ? rb1.getString("membermappingpreference_2") : "2";
        String membermappingpreference_3                    = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_3")) ? rb1.getString("membermappingpreference_3") : "3";
        String membermappingpreference_4                    = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_4")) ? rb1.getString("membermappingpreference_4") : "4";
        String membermappingpreference_5                    = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_5")) ? rb1.getString("membermappingpreference_5") : "5";
        String membermappingpreference_Is_PSTTerminal       = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Is_PSTTerminal")) ? rb1.getString("membermappingpreference_Is_PSTTerminal") : "Is PSTTerminal";
        String membermappingpreference_Card_Tokenization    = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Card_Tokenization")) ? rb1.getString("membermappingpreference_Card_Tokenization") : "Card Tokenization";
        String membermappingpreference_address              = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_address")) ? rb1.getString("membermappingpreference_address") : "Address on Payment page";
        String membermappingpreference_view                 = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_view")) ? rb1.getString("membermappingpreference_view") : "View";
        String membermappingpreference_hidden               = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_hidden")) ? rb1.getString("membermappingpreference_hidden") : "Hidden";
        String membermappingpreference_Address              = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Address")) ? rb1.getString("membermappingpreference_Address") : "Address Validation";
        String membermappingpreference_Mandatory            = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Mandatory")) ? rb1.getString("membermappingpreference_Mandatory") : "Mandatory";
        String membermappingpreference_Optional             = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Optional")) ? rb1.getString("membermappingpreference_Optional") : "Optional";
        String membermappingpreference_Risk_Rule            = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Risk_Rule")) ? rb1.getString("membermappingpreference_Risk_Rule") : "Risk Rule";
        String membermappingpreference_min_payout           = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_min_payout")) ? rb1.getString("membermappingpreference_min_payout") : "Min Payout Amount ";
        String membermappingpreference_Settlement_Currency  = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Settlement_Currency")) ? rb1.getString("membermappingpreference_Settlement_Currency") : "Settlement Currency";
        String membermappingpreference_Bin_Routing          = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Bin_Routing")) ? rb1.getString("membermappingpreference_Bin_Routing") : "Bin Routing";
        String membermappingpreference_card1                = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_card1")) ? rb1.getString("membermappingpreference_card1") : "Card Whitelisting";
        String membermappingpreference_Disable              = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Disable")) ? rb1.getString("membermappingpreference_Disable") : "Disable";
        String membermappingpreference_Enable               = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Enable")) ? rb1.getString("membermappingpreference_Enable") : "Enable";
        String membermappingpreference_Email_Whitelisting   = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Email_Whitelisting")) ? rb1.getString("membermappingpreference_Email_Whitelisting") : "Email Whitelisting";
        String membermappingpreference_EMI_Support          = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_EMI_Support")) ? rb1.getString("membermappingpreference_EMI_Support") : "EMI Support";
        String membermappingpreference_Whitelisting_Checks  = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Whitelisting_Checks")) ? rb1.getString("membermappingpreference_Whitelisting_Checks") : "Whitelisting Checks";
        String membermappingpreference_IP_Address           = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_IP_Address")) ? rb1.getString("membermappingpreference_IP_Address") : "IP Address";
        String membermappingpreference_CardHolder           = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_CardHolder")) ? rb1.getString("membermappingpreference_CardHolder") : "Card Holder Name";
        String membermappingpreference_ExpiryDate           = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_ExpiryDate")) ? rb1.getString("membermappingpreference_ExpiryDate") : "Expiry Date";
        String membermappingpreference_Card_Limit           = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Card_Limit")) ? rb1.getString("membermappingpreference_Card_Limit") : "Card Limit Check";
        String membermappingpreference_Card_amount          = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Card_amount")) ? rb1.getString("membermappingpreference_Card_amount") : "Card Amount Limit Check";
        String membermappingpreference_list_amount          = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_list_amount")) ? rb1.getString("membermappingpreference_list_amount") : "Amount Limit Check";
        String membermappingpreference_Automatic_Recurring  = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Automatic_Recurring")) ? rb1.getString("membermappingpreference_Automatic_Recurring") : "Automatic Recurring";
        String membermappingpreference_Restricted_Ticket    = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Restricted_Ticket")) ? rb1.getString("membermappingpreference_Restricted_Ticket") : "Restricted Ticket";
        String membermappingpreference_ManualRecurring      = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_ManualRecurring")) ? rb1.getString("membermappingpreference_ManualRecurring") : "Manual Recurring ";
        String membermappingpreference_CardDetails          = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_CardDetails")) ? rb1.getString("membermappingpreference_CardDetails") : "Card Details";
        String membermappingpreference_Required             = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Required")) ? rb1.getString("membermappingpreference_Required") : "Required";
        String membermappingpreference_Not_Required         = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Not_Required")) ? rb1.getString("membermappingpreference_Not_Required") : "Not Required";
        String membermappingpreference_Currency             = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Currency")) ? rb1.getString("membermappingpreference_Currency") : "Currency Conversion ";
        String membermappingpreference_Conversion           = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Conversion")) ? rb1.getString("membermappingpreference_Conversion") : "Conversion Currency";
        String membermappingpreference_Payout_Activation    = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Payout_Activation")) ? rb1.getString("membermappingpreference_Payout_Activation") : "Payout Activation ";
        String membermappingpreference_auto_conversion      = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_auto_conversion")) ? rb1.getString("membermappingpreference_auto_conversion") : "Auto Redirect Request ";
        String membermappingpreference_Card_Encryption      = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Card_Encryption")) ? rb1.getString("membermappingpreference_Card_Encryption") : "Card Encryption";
        String membermappingpreference_CREATE               = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_CREATE")) ? rb1.getString("membermappingpreference_CREATE") : "CREATE";
        String membermappingpreference_Search               = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Search")) ? rb1.getString("membermappingpreference_Search") : "Search";
        String membermappingpreference_MerchantID1          = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_MerchantID1")) ? rb1.getString("membermappingpreference_MerchantID1") : "Merchant ID*";
        String membermappingpreference_Bank_Account         = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Bank_Account")) ? rb1.getString("membermappingpreference_Bank_Account") : "Bank Account";
        String membermappingpreference_all                  = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_all")) ? rb1.getString("membermappingpreference_all") : "All";
        String membermappingpreference_search               = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_search")) ? rb1.getString("membermappingpreference_search") : "Search";
        String membermappingpreference_Merchant_Reports     = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Merchant_Reports")) ? rb1.getString("membermappingpreference_Merchant_Reports") : "Merchant Account Reports";
        String membermappingpreference_total_Reports        = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_total_Reports")) ? rb1.getString("membermappingpreference_total_Reports") : "Total Records";
        String membermappingpreference_Terminal_ID          = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Terminal_ID")) ? rb1.getString("membermappingpreference_Terminal_ID") : "Terminal ID";
        String membermappingpreference_Account_ID           = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Account_ID")) ? rb1.getString("membermappingpreference_Account_ID") : "Account ID";
        String membermappingpreference_payment_ID           = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_payment_ID")) ? rb1.getString("membermappingpreference_payment_ID") : "Payment  Method ID";
        String membermappingpreference_payment_brand        = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_payment_brand")) ? rb1.getString("membermappingpreference_payment_brand") : "Payment Brand ID";
        String membermappingpreference_action               = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_action")) ? rb1.getString("membermappingpreference_action") : "Action";
        String membermappingpreference_Action_Executor      = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Action_Executor")) ? rb1.getString("membermappingpreference_Action_Executor") : "Action Executor Id";
        String membermappingpreference_Action_Executor_name = StringUtils.isNotEmpty(rb1.getString("membermappingpreference_Action_Executor_name")) ? rb1.getString("membermappingpreference_Action_Executor_name") : "Action Executor Name";
        String payoutPriority = "Payout Priority";
        String membermappingpreference_daily_amount_limit_check = "Daily Amount Limit Check";
        String membermappingpreference_weekly_amount_limit_check = "Weekly Amount Limit Check";
        String membermappingpreference_monthly_amount_limit_check = "Monthly Amount Limit Check";
        String membermappingpreference_daily_card_limit_check = "Daily Card Limit Check";
        String membermappingpreference_weekly_card_limit_check = "Weekly Card Limit Check";
        String membermappingpreference_monthly_card_limit_check = "Monthly Card Limit Check";
        String membermappingpreference_daily_card_amount_limit_check = "Daily Card Amount Limit Check";
        String membermappingpreference_weekly_card_amount_limit_check = "Weekly Card Amount Limit Check";
        String membermappingpreference_monthly_card_amount_limit_check = "Monthly Card Amount Limit Check";
%>
<script type="text/javascript">
    $(function () {
        $('[data-toggle="tooltip"]').tooltip();
    });
</script>

<script type="text/javascript">
    function check()
    {
        var error = ""
        var memberid = form1.elements["memberid"].value;
        //var accountid = form1.elements["accountid"].value;
        //var paymodeid = form1.elements["paymodeid"].value;
        var checkboxes      = $("#chkbox").find("input")
        var checkboxes1     = $("#chkbox1").find("input")
        var checkboxes2     = $("#chkbox2").find("input")
        var total_boxes     = checkboxes.length;
        var total_boxes1    = checkboxes1.length;
        var total_boxes2    = checkboxes2.length;
        var flag,i;
        for(i=0; i<total_boxes; i++ )
        {
            if(checkboxes[i].checked)
            {
                flag= true;
                break;
            }
            else
            {
                flag= false;
            }

        }
        var flag1,q;
        for(q=0; q<total_boxes1; q++ )
        {
            if(checkboxes1[q].checked)
            {
                flag1= true;
                break;
            }
            else
            {
                flag1= false;
            }

        }
        var flag2=false ,r;
        for(r=0; r<total_boxes2; r++ )
        {
            if(checkboxes2[r].checked)
            {
                flag2= true;
                break;
            }
            else
            {
                flag2= false;
            }

        }
        if (memberid == "") {
            error += "Select MemberId \n";
        }
        /*if (accountid == "") {
         error += "Select Bank Account\n";
         }
         if (paymodeid == "") {
         error += "Select PayMode\n";
         }*/
        if(flag2 == false)
        {
            error += "Select At Least One Bank Account \n";
        }
        if(flag1 == false)
        {
            error += "Select At Least One PayMode \n";
        }
        if(flag == false)
        {
            error += "Select At Least One CardType \n";
        }
        if (error != "") {
            window.alert(error);
        } else {
            document.getElementById("form1").submit();
        }
    }

    function confirmsubmit(i) {
        var r = window.confirm("Are You Sure you want to Update The Details");
        if (r == true) {
            document.getElementById("charges" + i).submit();
        } else {

        }
    }

    function confirmsubmit2(terminalid) {
        console.log(terminalid); //please do not remove console here
        //document.getElementById(terminal).remove();
        /*$("[name='terminalid']").each(function ()
         {
         if ($(this).val() != terminalid)
         {
         $(this).remove();
         }
         });*/

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
            document.getElementById("saveall").submit();
        }
    }

    function confirmsubmit3()
    {
        var checkboxs=document.getElementsByName("terminalid");
        var okay=false;
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

    function confirmgateway() {
        if (form1.elements["accountid"].value == "") {
            window.alert("Please Select Account ID");
        } else {
            var accountid = form1.elements["accountid"].value;
            window.open('GetGatewayAccount?accountid=' + accountid + '&ctoken=<%=user.getCSRFToken()%>', 'newwindow', 'width=500, height=350');
            return false;
        }
    }

    var expanded = false;
    function showCheckboxes(event)
    {
        event.stopPropagation();
        var checkboxes = document.getElementById("checkboxes");
        if ($('#checkboxes').is(':visible')) {
            checkboxes.style.display = "none";
        } else{
            checkboxes.style.display = "block";
        }
    }

    var expanded1 = false;
    function showpaymentcheckbox(event)
    {
        event.stopPropagation();
        var checkboxes = document.getElementById("checkboxes_1");
        if ($('#checkboxes_1').is(':visible')) {
            checkboxes.style.display = "none";
        } else{
            checkboxes.style.display = "block";
        }
    }

    var expanded2 = false;
    function showaccountcheckbox(event)
    {
        event.stopPropagation();
        var checkboxes = document.getElementById("checkboxes_2");
        if ($('#checkboxes_2').is(':visible')) {
            checkboxes.style.display = "none";
        } else{
            checkboxes.style.display = "block";
        }
    }

    //ONCHANGE EVENT OF PARTNERID FOR CREATE
    $(function () {
        $('#partnerid').on('change', function(request, response ) {
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data:{
                    partnerid: $('#partnerid').val(),
                    ctoken: $('#ctoken').val(),
                    method:"patnersmember",
                    term: request.term
                },
                success: function( data ) {
                    $('#memberid').find('option').not(':first').remove();
                    $.each(data.aaData,function(i,data)
                    {
                        var div_data="<option value="+data.value+">"+data.value + data.text +"</option>";
                        $(div_data).appendTo('#memberid');
                    });
                }
            } );
            minLength: 0
        });

        //ONCHANGE EVENT OF PARTNERID FOR SEARCH

        $('#spartnerid').on('change', function(request, response ) {
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data:{
                    partnerid: $('#spartnerid').val(),
                    ctoken: $('#ctoken').val(),
                    method:"patnersmember",
                    term: request.term

                },
                success: function( data ) {
                    $('#smemberid').find('option').not(':first').remove();
                    $.each(data.aaData,function(i,data)
                    {
                        var div_data="<option value="+data.value+">"+data.value + data.text +"</option>";
                        $(div_data).appendTo('#smemberid');
                    });
                }
            } );
            minLength: 0


        });


        //Change account id as per partner id changed.
        $('#partnerid').on('change', function(request, response ) {
            $.ajax({
                url: "/partner/net/GetDetails",
                dataType: "json",
                data:{
                    partnerid: $('#partnerid').val(),
                    ctoken: $('#ctoken').val(),
                    method:"loadGatewayAccounts",
                    term: request.term

                },
                success: function( data ) {
                    $("#chkbox2").html("");
                    // $('#chkbox2').find('div').not(':first').remove();
                    $('#accountid').find('option').not(':first').remove();
                    $.each(data.aaData,function(i,data)
                    {
                        var div_data="<input type=\"checkbox\" name=\"accID_"+ data.value+"\" align=\"left\" value="  +data.value+ ">" + data.text +" <br></option>";
                        $(div_data).appendTo('#chkbox2');
                    });
                }
            } );
            minLength: 0

        });

        $('#smemberid').on('change', function(request, response ) {
            var partnerid=document.getElementById("spartnerid").value;
            var session_id = $('#session_partner').val()
            if($('#spartnerid').val()==""){
                partnerid = $('#session_partner').val() ;
            }
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data:{
                    partnerid: partnerid,
                    memberid: $('#smemberid').val(),
                    ctoken: $('#ctoken').val(),
                    method:"loadMerchantsGatewayAccounts",
                    term: request.term

                },
                success: function( data ) {
                    $('#saccountid').find('option').not(':first').remove();
                    $.each(data.aaData,function(i,data)
                    {
                        var div_data="<option value="+data.value+">"+data.text +"</option>";
                        $(div_data).appendTo('#saccountid');
                    });
                }
            } );
            minLength: 0


        });

        /*$('#memberid').on('change', function(request, response ) {
         var partnerid=document.getElementById("partnerid").value;
         var session_id = $('#session_partner').val()
         if($('#partnerid').val()==""){
         partnerid = $('#session_partner').val() ;
         }
         $.ajax( {
         url: "/partner/net/GetDetails",
         dataType: "json",
         data:{
         partnerid: partnerid,
         memberid: $('#memberid').val(),
         ctoken: $('#ctoken').val(),
         method:"loadMerchantsGatewayAccounts",
         term: request.term

         },
         success: function( data ) {
         $("#chkbox2").html("");
         // $('#chkbox2').find('div').not(':first').remove();
         $('#accountid').find('option').not(':first').remove();
         $.each(data.aaData,function(i,data)
         {
         var div_data="<input type=\"checkbox\" name=\"accID_"+ data.value+"\" align=\"left\" value="  +data.value+ ">" + data.text +" <br></option>";
         $(div_data).appendTo('#chkbox2');
         });
         }
         } );
         minLength: 0


         });*/




        $(document).click( function(){
           /* var checkboxes = document.getElementById("checkboxes");
            $("#checkboxes").hover(function(){
                checkboxes.style.display = "block";
            }, function(){
                checkboxes.style.display = "none";
            });

            var checkboxes1 = document.getElementById("checkboxes_1");
            $("#checkboxes_1").hover(function(){
                checkboxes1.style.display = "block";
            }, function(){
                checkboxes1.style.display = "none";
            });

            var checkboxes2 = document.getElementById("checkboxes_2");
            $("#checkboxes_2").hover(function(){
                checkboxes2.style.display = "block";
            }, function(){
                checkboxes2.style.display = "none";
            });*/
        });



        $(function ()
        {
            $(document).ready(function(){
                $(".caret").addClass('icon');
                $('.multiselect-selected-text').addClass("filter-option pull-left");
                firefox=navigator.userAgent.search("Firefox");
                if(firefox>-1)
                {
                    $('.icon').removeClass("caret");
                    $('.icon').addClass("fa fa-chevron-down");
                    $('.icon').css({"height":"33px","width":"17px","text-align":"center","background-color":"#E6E2E2","padding-top":"10px","border":"1px solid #C7BFBF"});
                    $('.dropdown-toggle').css({"padding-right":"0px","padding-top":"0px","vertical-align":"middle"});
                    $('.multiselect-selected-text').css({"padding-top": "3px","vertical-align":"middle"});
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
        });
    });

</script>


<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">



            <form id="form1" name=form1 action="SetAccount?ctoken=<%=ctoken%>" method="POST">

                <div class="row reporttable">
                    <div class="col-sm-12 portlets ui-sortable">
                        <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                        <input type="hidden" value="<%=String.valueOf(session.getAttribute("merchantid"))%>" name="id" id="session_partner">

                        <div class="widget">

                            <%
                                String errormsg2 = (String) request.getAttribute("error1");
                                String errormsg1 = (String) request.getAttribute("error");
                                String success = (String) request.getAttribute("success1");

                                if (functions.isValueNull(errormsg2))
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg2 + "</h5>");
                                }
                                if (functions.isValueNull(errormsg1))
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg1 + "</h5>");
                                }
                                if (functions.isValueNull(success))
                                {
                                    out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + success + "</h5>");
                                }
                                String errormsg= (String)request.getAttribute("msg");
                                if (functions.isValueNull(errormsg))
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");
                                }

                            %>

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=membermappingpreference_Merchant_Account%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>

                            <div class="widget-content padding">
                                <div id="horizontal-form">
                                    <div class="widget-header transparent">
                                        <h2 id="maintitle"><strong>&nbsp;&nbsp;<%=membermappingpreference_create_Account%></strong></h2>
                                    </div>
                                    <br>
                                    <div class="form-group col-md-4 has-feedback" <%=Style2%>>
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=membermappingpreference_PartnerID%></label>
                                        <select class="form-control" name="partnerid" id="partnerid" style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <option value="" default><%=membermappingpreference_select_partnerid%></option>
                                            <%
                                                for(String pid : partneriddetails.keySet())
                                                {
                                            %>
                                            <option value="<%=pid%>" ><%=partneriddetails.get(pid)%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback" <%=Style1%>>
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=membermappingpreference_PartnerID%></label>
                                        <input  class="form-control" value="<%=dISABLED_Id%>" disabled>
                                        <input type="hidden" class="form-control" value="<%=dISABLED_Id%>" disabled>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=membermappingpreference_MerchantID%></label>
                                        <select class="form-control" name="memberid" id="memberid" style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <option value="" default><%=membermappingpreference_select_merchantid%></option>
                                            <%
                                                for(Integer mid : memberidDetails.keySet())
                                                {
                                            %>
                                            <option value="<%=mid%>" ><%=mid+"-"+memberidDetails.get(mid)%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=membermappingpreference_bank_Account%></label>
                                        <select class="form-control" id="accountid" name="accountid" onclick="showaccountcheckbox(event)" style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <option style="display: none;"><%=membermappingpreference_select_bank%></option>
                                        </select>
                                        <div id="checkboxes_2" style="width: 100%">
                                            <div id="chkbox2" align="left" class="checkboxes" style="padding-left: 15px; padding-top: 5px">
                                                <%
                                                    TreeMap<Integer,String> accountMap= (TreeMap) request.getAttribute("accountids");
                                                    for(Integer accID:accountMap.keySet())
                                                    {
                                                %>
                                                <%--<option value="<%=paymap.get(paymode)%>"> <%=paymode%></option>--%>
                                                <input type="checkbox" name="accID_<%=accID%>" align="left" value="<%=accID%>" />
                                                <%=accID%>-<%=accountMap.get(accID)%> <br>
                                                <%
                                                    }
                                                %>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=membermappingpreference_Payment_Method%></label>
                                        <select class="form-control" id="paymodeid" name="paymodeid" onclick="showpaymentcheckbox(event)" style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <option style="display: none;"><%=membermappingpreference_select_Method%></option>
                                        </select>
                                        <div id="checkboxes_1" style="width: 100%">
                                            <div id="chkbox1" align="left" class="checkboxes" style="padding-left: 15px; padding-top: 5px">
                                                <%
                                                    LinkedHashMap<String,Integer> paymap= (LinkedHashMap) request.getAttribute("paymodeids");
                                                    for(String paymode:paymap.keySet())
                                                    {
                                                %>
                                                <%--<option value="<%=paymap.get(paymode)%>"> <%=paymode%></option>--%>
                                                <input type="checkbox" name="paymode_<%=paymap.get(paymode)%>" align="left" value="<%=paymap.get(paymode)%>" />
                                                <%=paymode%> <br>
                                                <%
                                                    }
                                                %>
                                            </div>
                                        </div>
                                    </div>
                                    <%--    <div class="form-group col-md-4 has-feedback">
                                            <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">CardType</label>
                                            <div class="multiselect" tabindex="0">
                                                <div class="selectBox" onclick="showCheckboxes()" id="selectBox_btn">
                                                    <select class="form-control" id="cardtypeid" name="cardtypeid" style="border: 1px solid #b2b2b2;font-weight:bold ">
                                                        <option>Select CardType</option>
                                                    </select>
                                                    <div class="overSelect"></div>
                                                </div>
                                                <div id="checkboxes" >
                                                    <div id="chkbox" align="left" class="checkboxes" style="padding-left: 15px; padding-top: 5px " tabindex="0" onblur="abc()">
                                                        <%
                                                            TreeMap<String,Integer> cardmap= (TreeMap) request.getAttribute("cardtypeids");
                                                            for(String cardtype:cardmap.keySet())
                                                            {
                                                        %>
                                                        <input type="checkbox"  name="cardtype_<%=cardmap.get(cardtype)%>" align="left" value="<%=cardmap.get(cardtype)%>"> <%=cardtype%> <br>
                                                        <%
                                                            }
                                                        %>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>--%>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Payment Brand</label>
                                        <select class="form-control" id="select_dropdown" onclick="showCheckboxes(event)" style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <option style="display: none;">Select Payment Brand</option>
                                        </select>
                                        <div id="checkboxes" style="width: 100%">
                                            <div id="chkbox" align="left" class="checkboxes" style="padding-left: 15px; padding-top: 5px">
                                                <%
                                                    LinkedHashMap<String,Integer> cardmap= (LinkedHashMap) request.getAttribute("cardtypeids");
                                                    for(String cardtype:cardmap.keySet())
                                                    {
                                                %>
                                                <input type="checkbox" name="cardtype_<%=cardmap.get(cardtype)%>" align="left" value="<%=cardmap.get(cardtype)%>" />
                                                <%=cardtype%> <br>
                                                <%
                                                    }
                                                %>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="widget-header transparent">
                                        <h2 id="maintitle"><strong>&nbsp;&nbsp;<%=membermappingpreference_Limits%></strong></h2>
                                    </div>
                                    <br>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=membermappingpreference_daily1%></label>
                                        <div style="display: inline-flex;width: 100%;">
                                            <input type="text" class="form-control" size=10 name='daily_amount_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="0.00" />
                                            <select name='daily_amount_limit_check' style="border: 1px solid #b2b2b2;font-weight:bold"><%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                                        </div>
                                    </div>
                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=membermappingpreference_weekly%></label>
                                        <div style="display: inline-flex;width: 100%;">
                                            <input type="text" class="form-control" size=10 name='weekly_amount_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="0.00" />
                                            <select name='weekly_amount_limit_check' style="border: 1px solid #b2b2b2;font-weight:bold"><%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                                        </div>
                                    </div>
                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=membermappingpreference_monthly_limit1%></label>
                                        <div style="display: inline-flex;width: 100%;">
                                            <input type="text" class="form-control" size=10 name='monthly_amount_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="0.00" />
                                            <select name='monthly_amount_limit_check' style="border: 1px solid #b2b2b2;font-weight:bold"><%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                                        </div>
                                    </div>
                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=membermappingpreference_daily_card%></label>
                                        <div style="display: inline-flex;width: 100%;">
                                            <input type="text" class="form-control" size=10 name='daily_card_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="0" />
                                            <select name='daily_card_limit_check' style="border: 1px solid #b2b2b2;font-weight:bold"><%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                                        </div>
                                    </div>
                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=membermappingpreference_weekly_card%></label>
                                        <div style="display: inline-flex;width: 100%;">
                                            <input type="text" class="form-control" size=10 name='weekly_card_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="0" />
                                            <select name='weekly_card_limit_check' style="border: 1px solid #b2b2b2;font-weight:bold"><%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                                        </div>
                                    </div>
                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=membermappingpreference_monthly_limit%></label>
                                        <div style="display: inline-flex;width: 100%;">
                                            <input type="text" class="form-control" size=10 name='monthly_card_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="0" />
                                            <select name='monthly_card_limit_check' style="border: 1px solid #b2b2b2;font-weight:bold"><%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                                        </div>
                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight:600;"><%=membermappingpreference_dailycard%></label>
                                        <div style="display: inline-flex;width: 100%;">
                                            <input type="text" class="form-control" size=10 name='daily_card_amount_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="0.00" />
                                            <select name='daily_card_amount_limit_check' style="border: 1px solid #b2b2b2;font-weight:bold"><%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                                        </div>
                                    </div>
                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=membermappingpreference_weeklycard%></label>
                                        <div style="display: inline-flex;width: 100%;">
                                            <input type="text" class="form-control" size=10 name='weekly_card_amount_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="0.00" />
                                            <select name='weekly_card_amount_limit_check' style="border: 1px solid #b2b2b2;font-weight:bold"><%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                                        </div>
                                    </div>
                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=membermappingpreference_monthly_card%></label>
                                        <div style="display: inline-flex;width: 100%;">
                                            <input type="text" class="form-control" size=10 name='monthly_card_amount_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="0.00" />
                                            <select name='monthly_card_amount_limit_check' style="border: 1px solid #b2b2b2;font-weight:bold"><%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                                        </div>
                                    </div>
                                    <div class="form-group col-md-3 has-feedback">
                                        <div style="width: 100%;">
                                            <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"data-toggle="tooltip" data-placement="top" title="Daily average ticket of the transactions."><%=membermappingpreference_daily_ticket%> <i class="fas fa-info-circle fa-1x"></i></label>
                                            <input type="text" class="form-control" size=10 name='daily_avg_ticket' style="border: 1px solid #b2b2b2;font-weight:bold" value="150.00" />
                                        </div>
                                    </div>
                                    <div class="form-group col-md-3 has-feedback">
                                        <div style="width: 100%;">
                                            <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"data-toggle="tooltip" data-placement="top" title="Weekly average ticket of the transactions."><%=membermappingpreference_weekly_ticket%> <i class="fas fa-info-circle fa-1x"></i></label>
                                            <input type="text" class="form-control" size=10 name='weekly_avg_ticket' style="border: 1px solid #b2b2b2;font-weight:bold" value="150.00" />
                                        </div>
                                    </div>
                                    <div class="form-group col-md-3 has-feedback">
                                        <div style="width: 100%;">
                                            <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"data-toggle="tooltip" data-placement="top" title="Monthly average ticket of the transactions."><%=membermappingpreference_monthly_ticket%> <i class="fas fa-info-circle fa-1x"></i></label>
                                            <input type="text" class="form-control" size=10 name='monthly_avg_ticket' style="border: 1px solid #b2b2b2;font-weight:bold" value="150.00" />
                                        </div>
                                    </div>
                                    <div class="form-group col-md-3 has-feedback">
                                        <div style="width: 100%;">
                                            <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;" data-toggle="tooltip" data-placement="top" title="Minimum transaction amount of the transactions." ><%=membermappingpreference_min%> <i class="fas fa-info-circle fa-1x"></i></label>
                                            <input type="text" class="form-control" size=10 name='min_trans_amount' style="border: 1px solid #b2b2b2;font-weight:bold" value="1.00" />
                                        </div>
                                    </div>
                                    <div class="form-group col-md-3 has-feedback">
                                        <div style="width: 100%;">
                                            <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"data-toggle="tooltip" data-placement="top" title="Maximum transaction amount of the transactions."><%=membermappingpreference_max%> <i class="fas fa-info-circle fa-1x"></i></label>
                                            <input type="text" class="form-control" size=10 name='max_trans_amount' style="border: 1px solid #b2b2b2;font-weight:bold" value="1000.00" />
                                        </div>
                                    </div>

                                    <div class="widget-header transparent">
                                        <h2 id="maintitle"><strong>&nbsp;&nbsp;<%=membermappingpreference_others%></strong></h2>
                                    </div>
                                    <br>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures terminal is active while placing the transaction."><%=membermappingpreference_terminal%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <%--<select class="form-control" name='isActive' style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <%out.println(partnerFunctions.comboval2(ESAPI.encoder().encodeForHTMLAttribute("Active")));%>
                                        </select>--%>

                                        <select class="form-control" name="isActive" style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <option value="Y" default><%=membermappingpreference_active%></option>
                                            <option value="N" default><%=membermappingpreference_inactive%></option>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-3">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures terminal priority during the transaction."><%=membermappingpreference_terminal_priority%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select class="form-control" id="priority" name="priority" style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <option value="1" default><%=membermappingpreference_1%></option>
                                            <option value="2" default><%=membermappingpreference_2%></option>
                                            <option value="3" default><%=membermappingpreference_3%></option>
                                            <option value="4" default><%=membermappingpreference_4%></option>
                                            <option value="5" default><%=membermappingpreference_5%></option>
                                        </select>
                                    </div>

                                    <%-- <div class="form-group col-md-3 has-feedback">
                                          <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures terminal is on Test/Live mode during the transaction.">Test Terminal <i class="fas fa-info-circle fa-1x"></i></label>
                                          <select class="form-control" name='isTest' style="border: 1px solid #b2b2b2;font-weight:bold">
                                              &lt;%&ndash;<%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%>&ndash;%&gt;
                                                  <option value="Y" default>Yes</option>
                                                  <option value="N" default>No</option>
                                          </select>
                                      </div>--%>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"><%=membermappingpreference_Is_PSTTerminal%></label>
                                        <select class="form-control" name='isPSTTerminal' style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures Card tokenization is active on the Terminal."><%=membermappingpreference_Card_Tokenization%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select class="form-control" name='isTokenizationActive' style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <%--<%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%>--%>
                                            <option value="Y" default><%=membermappingpreference_active%></option>
                                            <option value="N" default><%=membermappingpreference_inactive%></option>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-3">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures the address details are displayed on the payment page."><%=membermappingpreference_address%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select name='addressDetails' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <%-- <%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%>--%>
                                            <option value="Y" default><%=membermappingpreference_view%></option>
                                            <option value="N" default><%=membermappingpreference_hidden%></option>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-3">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag enables the address validation while placing the transaction."><%=membermappingpreference_Address%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select name='addressValidation' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <%--<%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%>--%>
                                            <option value="Y" default><%=membermappingpreference_Mandatory%></option>
                                            <option value="N" default><%=membermappingpreference_Optional%></option>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-3">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures the Merchant monitoring rules are enabled on the terminal.">Risk Rule <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select name='riskRuleActivation' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <%-- <%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%>--%>
                                            <option value="N" default><%=membermappingpreference_inactive%></option>
                                            <option value="Y" ><%=membermappingpreference_active%></option>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-3">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="Minimum Payout amount for the transaction."><%=membermappingpreference_min_payout%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <input type="text" class="form-control" size=10 name='min_payout_amount' style="border: 1px solid #b2b2b2;font-weight:bold" value="500.00" />
                                    </div>

                                    <div class="form-group col-md-3">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"><%=membermappingpreference_Settlement_Currency%></label>
                                        <select class="form-control" size="1" name='settlement_currency'style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <%
                                                for (Currency settlementCurrency:Currency.values())
                                                {
                                                    out.println("<option value=\""+settlementCurrency.toString()+"\">"+settlementCurrency.toString()+"</option>");
                                                }
                                            %>
                                        </select>

                                    </div>

                                    <div class="form-group col-md-3">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag route the transactions based on the Bin range & whitelisted bin range."><%=membermappingpreference_Bin_Routing%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select name='bin_routing' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <%--<%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%>--%>
                                            <option value="N" default><%=membermappingpreference_inactive%></option>
                                            <option value="Y" ><%=membermappingpreference_active%></option>
                                        </select>
                                    </div>
                                    <div class="form-group col-md-3">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag enables the Card Whitelisting while placing the transaction."><%=membermappingpreference_card1%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select name='isCardWhitelisted' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <%--<%out.println(functions.comboval2(ESAPI.encoder().encodeForHTMLAttribute("N")));%>--%>
                                            <option value="N" default><%=membermappingpreference_Disable%></option>
                                            <option value="Y" ><%=membermappingpreference_Enable%></option>
                                            <option value="V" >VIP Cards</option>
                                        </select>
                                    </div>
                                    <div class="form-group col-md-3">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag enables the Email Whitelisting while placing the transaction."><%=membermappingpreference_Email_Whitelisting%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select name='isEmailWhitelisted' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <%--<%out.println(functions.comboval2(ESAPI.encoder().encodeForHTMLAttribute("N")));%>--%>
                                            <option value="N" default><%=membermappingpreference_Disable%></option>
                                            <option value="Y" ><%=membermappingpreference_Enable%></option>
                                        </select>
                                    </div>
                                    <div class="form-group col-md-3">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures EMI option is supported on the Terminal."><%=membermappingpreference_EMI_Support%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select name='emi_support' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <%--<%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%>--%>
                                            <option value="N" default><%=membermappingpreference_Disable%></option>
                                            <option value="Y" ><%=membermappingpreference_Enable%></option>
                                        </select>
                                    </div>
                                    <div class="form-group col-md-3">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures Whitelisting checks are enabled during the transaction."><%=membermappingpreference_Whitelisting_Checks%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <%--<div class="form-group col-md-12 has-feedback" style="padding: 0;">--%>
                                        <select id="whitelisting" size="1" class="form-group"  multiple="multiple">
                                            <option value="ipAddress"><%=membermappingpreference_IP_Address%></option>
                                            <option value="name"><%=membermappingpreference_CardHolder%></option>
                                            <option value="expiryDate"><%=membermappingpreference_ExpiryDate%></option>
                                        </select>
                                        <%--</div>--%>
                                        <input type="hidden" id="whitelistcode" name="whitelistdetails" value="">
                                    </div>
                                    <div class="form-group col-md-3">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag enables the checking of daily/weekly/monthly card limit during the transaction."><%=membermappingpreference_Card_Limit%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select name='cardLimitCheck' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <%out.println(partnerFunctions.comboval1(ESAPI.encoder().encodeForHTMLAttribute("N")));%>
                                        </select>
                                    </div>
                                    <div class="form-group col-md-3">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag enables the checking of daily/weekly/monthly card amount limit during the transaction."><%=membermappingpreference_Card_amount%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select name='cardAmountLimitCheck' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold"><%out.println(partnerFunctions.comboval1(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                                    </div>
                                    <div class="form-group col-md-3">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag enables the checking of daily/weekly/monthly amount limit during the transaction."><%=membermappingpreference_list_amount%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select name='amountLimitCheck' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold"><%out.println(partnerFunctions.comboval1(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures Automatic Recurring checks are enabled during the transaction."><%=membermappingpreference_Automatic_Recurring%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select class="form-control" name='is_recurring' style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <option value="N" default><%=membermappingpreference_Disable%></option>
                                            <option value="Y" ><%=membermappingpreference_Enable%></option>
                                        </select>
                                    </div>
                                    <%--<div class="form-group col-md-3">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures Automatic Recurring checks are enabled during the transaction.">Automatic Recurring <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select name='is_recurring' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold"><%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                                    </div>--%>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures transactions of only Restricted amounts are permitted."> <%=membermappingpreference_Restricted_Ticket%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select class="form-control" name='isRestrictedTicketActive' style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <option value="N" default><%=membermappingpreference_inactive%></option>
                                            <option value="Y" ><%=membermappingpreference_active%></option>
                                        </select>
                                    </div>
                                    <%--<div class="form-group col-md-3">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures Is Restricted TicketActive checks are enabled during the transaction.">Is Restricted TicketActive <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select name='isRestrictedTicketActive' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold"><%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                                    </div>--%>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures Manual Recurring checks are enabled during the transaction."><%=membermappingpreference_ManualRecurring%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select class="form-control" name='isManualRecurring' style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <option value="N" default><%=membermappingpreference_Disable%></option>
                                            <option value="Y" ><%=membermappingpreference_Enable%></option>
                                        </select>
                                    </div>
                                    <%--<div class="form-group col-md-3">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures Manual Recurring checks are enabled during the transaction.">Manual Recurring <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select name='isManualRecurring' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold"><%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                                    </div>--%>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures Card details are required during the transaction."><%=membermappingpreference_CardDetails%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select class="form-control" name='cardDetailRequired' style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <option value="Y" default><%=membermappingpreference_Required%></option>
                                            <option value="N" default><%=membermappingpreference_Not_Required%></option>
                                        </select>
                                    </div>
                                    <%--<div class="form-group col-md-3">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures Card Detail Required checks are enabled during the transaction.">Card Detail Required  <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select name='cardDetailRequired' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold"><%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                                    </div>--%>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures Currency Conversion checks are enabled during the transaction."><%=membermappingpreference_Currency%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select class="form-control" name='currency_conversion' style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <option value="N" default><%=membermappingpreference_Disable%></option>
                                            <option value="Y" ><%=membermappingpreference_Enable%></option>
                                        </select>
                                    </div>
                                    <%--<div class="form-group col-md-3">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures Currency Conversion checks are enabled during the transaction.">Currency Conversion  <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select name='currency_conversion' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold"><%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                                    </div>--%>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures Converted Currency is what is selected as input."><%=membermappingpreference_Conversion%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select class="form-control" style="width:160px" name='conversion_currency'>
                                            <%
                                                for (Currency conversionCurrency:Currency.values())
                                                {
                                                    out.println("<option value=\""+conversionCurrency.toString()+"\">"+conversionCurrency.toString()+"</option>");
                                                }
                                            %>


                                            <%--<%
                                                for (Currency conversioncurrency:Currency.values())
                                                {
                                                    String conversion_currency = "";
                                                    String selected="";
                                                    if(conversioncurrency.toString().equals(conversion_currency))
                                                    {
                                                        selected="selected";
                                                    }
                                                    out.println("<option value=\""+conversioncurrency.toString()+"\" "+selected+">"+conversioncurrency.toString()+"</option>");
                                                }
                                            %>--%>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures that the Payout Report can be generated for the terminal."><%=membermappingpreference_Payout_Activation%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select class="form-control" name='payoutActivation' style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <option value="Y" default><%=membermappingpreference_active%></option>
                                            <option value="N" default><%=membermappingpreference_inactive%></option>
                                        </select>
                                    </div>
                                    <%-- <div class="form-group col-md-3">
                                         <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures Payout Activation checks are enabled during the transaction.">Payout Activation  <i class="fas fa-info-circle fa-1x"></i></label>
                                         <select name='payoutActivation' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold"><%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                                     </div>--%>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;" data-toggle="tooltip" data-placement="top"
                                               title="This flag ensures the request is rediected to the Bank directly,bypassing the payment page.Additional settings required:  Address on Payment page=Hidden and Address Validation = Optional."> <%=membermappingpreference_auto_conversion%> <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select class="form-control" name='autoRedirectRequest' style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <option value="N" default><%=membermappingpreference_Disable%></option>
                                            <option value="Y" ><%=membermappingpreference_Enable%></option>
                                        </select>
                                    </div>
                                    <%--<div class="form-group col-md-3">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures Auto Redirect Request checks are enabled during the transaction.">Auto Redirect Request  <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select name='autoRedirectRequest' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold"><%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                                    </div>--%>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures that transaction can be done only by encrypted card."> <%=membermappingpreference_Card_Encryption%>  <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select class="form-control" name='isCardEncryptionEnable' style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <option value="N" default><%=membermappingpreference_Disable%></option>
                                            <option value="Y" ><%=membermappingpreference_Enable%></option>
                                        </select>
                                    </div>
                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures Payout Priority."> <%=payoutPriority%>  <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select class="form-control" id="payout_priority" name="payout_priority"  style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <option value="1" default>1</option>
                                            <option value="2" default>2</option>
                                            <option value="3" default>3</option>
                                            <option value="4" default>4</option>
                                            <option value="5" default>5</option>
                                            <option value="6" default>6</option>
                                            <option value="7" default>7</option>
                                            <option value="8" default>8</option>
                                        </select>
                                    </div>
                                    <%--<div class="form-group col-md-3">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"data-toggle="tooltip" data-placement="top" title="This flag ensures Is CardEncryption Enable checks are enabled during the transaction.">Is CardEncryption Enable  <i class="fas fa-info-circle fa-1x"></i></label>
                                        <select name='isCardEncryptionEnable' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold"><%out.println(partnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                                    </div>--%>

                                </div>
                                <div class="form-group col-md-12 has-feedback">
                                    <center>
                                        <label>&nbsp;</label>
                                        <input type="hidden" name="action_create" value="CREATE">
                                        <button align=center type="button" onclick="check()" value="CREATE" class="btn btn-default" style="display: -webkit-box;"><%=membermappingpreference_CREATE%></button>
                                    </center>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=membermappingpreference_Search%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <form action="MemberMappingDetails?ctoken=<%=ctoken%>" method="post" name="F1" onsubmit="">
                            <input type="hidden" value="<%=ctoken%>" id="ctoken" name="ctoken">
                            <input type="hidden" value="<%=String.valueOf(session.getAttribute("merchantid"))%>" name="id" id="session_partner">
                            <div class="widget-content padding">
                                <div id="horizontal-form">
                                    <%-- <div class="form-group col-md-4 has-feedback" <%=Style2%>>
                                         <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=membermappingpreference_PartnerID%></label>
                                         <select class="form-control" name="partnerid" id="partnerid_search" style="border: 1px solid #b2b2b2;font-weight:bold">
                                             <option value="" default><%=membermappingpreference_select_partnerid%></option>
                                             <%
                                                 for(String pid : partneriddetails.keySet())
                                                 {
                                                     String isSelect="";
                                                     if(String.valueOf(pid).equalsIgnoreCase(partnerSid))
                                                     {

                                                         isSelect="selected";

                                                     }
                                             %>
                                             <option value="<%=pid%>" <%=isSelect%>><%=partneriddetails.get(pid)%></option>
                                             <%
                                                 }
                                             %>
                                         </select>
                                     </div>

                                     <div class="form-group col-md-4 has-feedback" <%=Style1%>>
                                         <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=membermappingpreference_PartnerID%></label>
                                         <input  class="form-control" value="<%=dISABLED_Id%>" disabled>
                                         <input type="hidden" class="form-control" value="<%=dISABLED_Id%>" disabled>
                                     </div>
 --%>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label ><%=membermappingpreference_PartnerID%></label>
                                        <input name="partnerid" id="pid2" value="<%=partnerid%>" class="form-control" autocomplete="on" <%=Config%>>
                                    </div>



                                    <%--<div class="form-group col-md-4 has-feedback">
                                         <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=membermappingpreference_MerchantID1%></label>--%>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label ><%=membermappingpreference_MerchantID1%></label>
                                        <input name="memberid" id="member2" value="<%=memberid%>" class="form-control" autocomplete="on">


                                        <%-- <select class="form-control" size="1" name="memberid" id="smemberid" style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <option value="" selected><%=membermappingpreference_select_merchantid%></option>
                                            <%
                                                for(Integer mid : memberidDetails.keySet())
                                                {
                                                    String isSelected1="";

                                                    String contactName = memberidDetails.get(mid);
                                                    if(String.valueOf(mid).equalsIgnoreCase(memberid))
                                                    {

                                                        isSelected1="selected";

                                                    }
                                            %>
                                            <option value="<%=mid%>" <%=isSelected1%>><%=mid+"-"+contactName%></option>
                                            <%
                                                }
                                            %>
                                        </select>--%>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=membermappingpreference_Bank_Account%></label>
                                        <select class="form-control" name="accountid" id="saccountid" style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <option value=""><%=membermappingpreference_all%></option>
                                            <%

                                                TreeMap<Integer,String> accountMap1= (TreeMap) request.getAttribute("accountids");
                                                for(Integer accID:accountMap1.keySet())
                                                {
                                                    String Selected="";
                                                    if(String.valueOf(accID).equalsIgnoreCase(accountid))
                                                    {
                                                        Selected="selected";
                                                    }

                                            %>
                                            <option value="<%=accID%>" <%=Selected%>> <%=accID%>-<%=accountMap1.get(accID)%>
                                            </option>
                                            <% } %>
                                        </select>
                                    </div>
                                    <br>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">PayMode</label>
                                        <input class="form-control" name="paymodeid" id="paymode1" value="<%=paymodesearch%>" autocomplete="on">
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Card Type</label>
                                        <input class="form-control" name="cardtypeid" id="ctype1" value="<%=cardtypesearch%>" autocomplete="on">
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">IsActive</label>
                                        <select type="text" name="isactive"  class="form-control"
                                                value="<%=ESAPI.encoder().encodeForHTML(ActiveOrInActive)%>" onchange="ChangeFunction(this.value,'IsActive','')">
                                            <%
                                                for(Map.Entry<String,String> activeStatus: dropdownlist.entrySet())
                                                {
                                                    String selected="";
                                                    if (activeStatus.getKey().equals(ActiveOrInActive))
                                                        selected="selected";
                                                    out.println("<option value="+activeStatus.getKey()+" "+selected+">"+activeStatus.getValue()+"</option>");
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <br>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">IsPayoutActive</label>
                                        <select type="text" name="ispayoutactive" class="form-control"
                                                value="<%=ESAPI.encoder().encodeForHTML(ispayoutactive)%>" onchange="ChangeFunction(this.value,'IsPayoutActive','')">
                                            <%
                                                for (Map.Entry<String,String> payoutStatus: dropdownlist.entrySet())
                                                {
                                                    String selected="";
                                                    if (payoutStatus.getKey().equals(ispayoutactive))
                                                        selected= "selected";
                                                    out.println("<option value="+payoutStatus.getKey()+" "+selected+">"+payoutStatus.getValue()+"</option>");
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="form-group col-md-12 has-feedback">
                                        <center>
                                            <label >&nbsp;</label>
                                            <button type="submit" class="btn btn-default" id="submit" style="display: -webkit-box;"><i class="fa fa-clock-o"></i>&nbsp;&nbsp;<%=membermappingpreference_search%></button>
                                        </center>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <%
                Hashtable  hash = (Hashtable) request.getAttribute("memberdetails");
                Hashtable temphash = null;
                int records = 0;
                int totalrecords = 0;
                try
                {
                    records = Integer.parseInt((String) hash.get("records"));
                    totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
                }
                catch (Exception e)
                {
                    log.error("Records & TotalRecords is found null");
                }
                if(totalrecords>0)
                {
            %>
            <div class="row reporttable">


                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=membermappingpreference_Merchant_Reports%></strong>    </h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="reporttable">
                            <label class="col-md-">&nbsp;&nbsp;<strong><%=membermappingpreference_total_Reports%> <%=totalrecords%></strong></label>
                            <div class="pull-right">
                                <div class="btn-group">
                                    <form name="exportform" method="post"  action="ExportTerminalDetails?ctoken=<%=ctoken%>" >
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <input type="hidden" value=<%=memberid%> name="memberId" >
                                        <input type="hidden" value="<%=accountid%>" name="accountid">
                                        <input type="hidden" value="<%=ActiveOrInActive%>" name="isactive">
                                        <input type="hidden" value="<%=ispayoutactive%>" name="ispayoutactive">
                                        <input type="hidden" value="<%=paymodesearch%>" name="paymodeid">
                                        <input type="hidden" value="<%=cardtypesearch%>" name="cardtypeid">
                                        <%--<button type="submit" class="button3" style="width:15%;margin-left:85% ;margin-top:0px"><b></b>&nbsp;&nbsp;&nbsp;<img width="80%" height="100%" border="0" src="/merchant/images/excel.png"></button>--%>
                                        <button class="button3" type="submit" style="background: transparent;border: 0;text-indent: 0;">
                                            <img style= "height: 40px;" src="/merchant/images/excel.png">
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </div>
                        <form id="saveall" action="/partner/net/SetAccount?ctoken=<%=ctoken%>" method=post>
                            <%--
                                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                            --%>


                            <% String style = "td1";
                                for (int pos = 1; pos <= records; pos++)
                                {
                                    String id   = Integer.toString(pos);
                                    int srno    = Integer.parseInt(id) ;

                                    if (pos % 2 == 0)
                                        style = "tr0";
                                    else
                                        style = "tr1";
                                    temphash = null;
                                    temphash = (Hashtable) hash.get(id);

                                    String memberId     = (String) temphash.get("memberid");
                                    String accountId    = PartnerFunctions.checkStringNull((String) temphash.get("accountid"));

                                    paymodeid               = "N/A";
                                    cardtypeid              = "N/A";
                                    String monthly_amount_limit = "N/A";
                                    String weekly_amount_limit  = "N/A";
                                    String daily_card_limit     = "N/A";
                                    String weekly_card_limit    = "N/A";
                                    String monthly_card_limit   = "N/A";
                                    String daily_amount_limit   = "N/A";
                                    String isReadOnly           = "readonly";
                                    String daily_card_amount_limit="",weekly_card_amount_limit="",monthly_card_amount_limit="",min_trans_amount="",max_trans_amount="",isActive="",priority="",isTest="",isPSTTerminal="",autoRedirectRequest="",isTokenizationActive="",isCardEncryptionEnable="",terminalid="",daily_avg_ticket="150.00",weekly_avg_ticket="150.00",monthly_avg_ticket="150.00",addressDetails="",addressValidation="",riskRuleActivation="",min_payout_amount="",settlementCurrency="",bin_routing="",isCardWhitelisted="",isEmailWhitelisted="",emi_support="",whitelisting="",cardLimitCheck="",cardAmountLimitCheck="",amountLimitCheck="",is_recurring="",isRestrictedTicketActive="",isManualRecurring="",cardDetailRequired="",payoutActivation="",currency_conversion="",conversion_currency="",actionExecutorId="",actionExecutorName="",daily_amount_limit_check_for_terminals="",weekly_amount_limit_check_for_terminals="",monthly_amount_limit_check_for_terminals="",daily_card_limit_check_for_terminals="",weekly_card_limit_check_for_terminals="",monthly_card_limit_check_for_terminals="",daily_card_amount_limit_check_for_terminals="",weekly_card_amount_limit_check_for_terminals="",monthly_card_amount_limit_check_for_terminals="";
                                    String payout_priority = "";
                                    if (accountId != null)
                                    {
                                        isReadOnly              = "";
                                        paymodeid               = ((String) temphash.get("paymodeid"));
                                        cardtypeid              = ((String) temphash.get("cardtypeid"));
                                        monthly_amount_limit    = (new BigDecimal((String) temphash.get("monthly_amount_limit"))).toString();
                                        weekly_amount_limit     = (new BigDecimal((String) temphash.get("weekly_amount_limit"))).toString();
                                        daily_amount_limit      = (new BigDecimal((String) temphash.get("daily_amount_limit"))).toString();
                                        daily_card_limit        = (new BigDecimal((String) temphash.get("daily_card_limit"))).toString();
                                        weekly_card_limit       = (new BigDecimal((String) temphash.get("weekly_card_limit"))).toString();
                                        monthly_card_limit      = (new BigDecimal((String) temphash.get("monthly_card_limit"))).toString();

                                        daily_card_amount_limit     = (new BigDecimal((String) temphash.get("daily_card_amount_limit"))).toString();
                                        weekly_card_amount_limit    = (new BigDecimal((String) temphash.get("weekly_card_amount_limit"))).toString();
                                        monthly_card_amount_limit   = (new BigDecimal((String) temphash.get("monthly_card_amount_limit"))).toString();

                                        daily_amount_limit_check_for_terminals = String.valueOf(temphash.get("daily_amount_limit_check"));
                                        weekly_amount_limit_check_for_terminals = String.valueOf(temphash.get("weekly_amount_limit_check"));
                                        monthly_amount_limit_check_for_terminals = String.valueOf(temphash.get("monthly_amount_limit_check"));
                                        daily_card_limit_check_for_terminals = String.valueOf(temphash.get("daily_card_limit_check"));
                                        weekly_card_limit_check_for_terminals = String.valueOf(temphash.get("weekly_card_limit_check"));
                                        monthly_card_limit_check_for_terminals = String.valueOf(temphash.get("monthly_card_limit_check"));
                                        daily_card_amount_limit_check_for_terminals = String.valueOf(temphash.get("daily_card_amount_limit_check"));
                                        weekly_card_amount_limit_check_for_terminals = String.valueOf(temphash.get("weekly_card_amount_limit_check"));
                                        monthly_card_amount_limit_check_for_terminals = String.valueOf(temphash.get("monthly_card_amount_limit_check"));

                                        min_trans_amount    = (new BigDecimal((String) temphash.get("min_transaction_amount"))).toString();
                                        max_trans_amount    = (new BigDecimal((String) temphash.get("max_transaction_amount"))).toString();

                                        daily_avg_ticket    = (new BigDecimal((String) temphash.get("daily_avg_ticket"))).toString();
                                        weekly_avg_ticket   = (new BigDecimal((String) temphash.get("weekly_avg_ticket"))).toString();
                                        monthly_avg_ticket  = (new BigDecimal((String) temphash.get("monthly_avg_ticket"))).toString();
                                        min_payout_amount   = (new BigDecimal((String) temphash.get("min_payout_amount"))).toString();
                                        settlementCurrency  = (String) temphash.get("settlement_currency");
                                        isActive            = (String) temphash.get("isActive");
                                        priority            = (String) temphash.get("priority");
                                        isTest              =  (String) temphash.get("isTest");
                                        isPSTTerminal       =  (String) temphash.get("isPSTTerminal");
                                        autoRedirectRequest =(String)temphash.get("autoRedirectRequest");
                                        isTokenizationActive    = (String) temphash.get("isTokenizationActive");
                                        addressDetails          = (String) temphash.get("addressDetails");
                                        addressValidation       = (String) temphash.get("addressValidation");
                                        riskRuleActivation      = (String) temphash.get("riskruleactivation");
                                        terminalid              = (String) temphash.get("terminalid");
                                        bin_routing             = (String) temphash.get("binRouting");
                                        isCardWhitelisted       = (String) temphash.get("isCardWhitelisted");
                                        isEmailWhitelisted      = (String) temphash.get("isEmailWhitelisted");
                                        emi_support             = (String) temphash.get("emi_support");
                                        whitelisting            = (String)temphash.get("whitelisting_details");
                                        cardLimitCheck          = (String)temphash.get("cardLimitCheck");
                                        cardAmountLimitCheck    = (String)temphash.get("cardAmountLimitCheck");
                                        amountLimitCheck        = (String)temphash.get("amountLimitCheck");
                                        is_recurring            = (String)temphash.get("is_recurring");
                                        isRestrictedTicketActive    = (String)temphash.get("isRestrictedTicketActive");
                                        isManualRecurring           = (String)temphash.get("isManualRecurring");
                                        cardDetailRequired          = (String)temphash.get("cardDetailRequired");
                                        payoutActivation            = (String)temphash.get("payoutActivation");
                                        currency_conversion         = (String)temphash.get("currency_conversion");
                                        conversion_currency         = (String)temphash.get("conversion_currency");
                                        isCardEncryptionEnable      = (String)temphash.get("isCardEncryptionEnable");
                                        actionExecutorId            = (String)temphash.get("actionExecutorId");
                                        actionExecutorName          = (String)temphash.get("actionExecutorName");
                                        payout_priority             = (String)temphash.get("payout_priority");
                                    }
                            %>
                            <div class="widget-content padding" style="overflow-x: auto;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <input type=hidden value="<%=accountId%>" name="accountid_<%=terminalid%>" >
                                <input type=hidden value="<%=terminalid%>" name="terminalid_<%=terminalid%>" >
                                <input type="hidden" value="" name="onchangedvalue_<%=terminalid%>" id="onchangedvalue_<%=terminalid%>">   <%--***do not remove the field*****--%>

                                <table id="myTable" align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    <tr>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_Terminal_ID%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_MerchantID%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_Account_ID%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_payment_ID%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_payment_brand%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_daily1%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_weekly%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_monthly_limit1%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_daily_card%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_weekly_card%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_monthly_limit%></b></td>
                                        <td  <%--colspan="2" --%>valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_action%></b></td>
                                    </tr>
                                    </thead>
                                    <tr>
                                        <td valign="middle" data-label="Terminal ID" align="center" style="vertical-align: middle;" class="<%=style%>"><input type="checkbox" value="<%=terminalid%>" name="terminalid" id="<%=terminalid%>">
                                            &nbsp;&nbsp;&nbsp;&nbsp;<b><%=ESAPI.encoder().encodeForHTMLAttribute(terminalid)%></b></td>
                                        <td valign="middle" data-label="Merchant ID" align="center" class="<%=style%>"><input type=text style="width:100px;" class="form-control" name="memberid_<%=terminalid%>" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberId)%>" readonly></td>
                                        <td valign="middle" data-label="Account ID" align="center" class="<%=style%>"><button type="submit" class="btn btn-default" onclick="window.open('GetGatewayAccount?accountid=<%=ESAPI.encoder().encodeForHTMLAttribute(accountId)%>&ctoken=<%=user.getCSRFToken()%>', 'newwindow', 'width=500, height=350'); return false;"> <%=ESAPI.encoder().encodeForHTMLAttribute(accountId)%></button> </td>
                                        <td valign="middle" data-label="PayMode ID" align="center" class="<%=style%>"><input type=text style="width:100px;" class="form-control" align="center" size=10 name='paymodeid_<%=terminalid%>' value="<%=paymodeid + "-" + terminalManager.getPaymentType(paymodeid)%> " readonly></td>
                                        <td valign="middle" data-label="Card Type ID" align="center" class="<%=style%>"><input type=text class="form-control" style="width:100px;"name='cardtypeid_<%=terminalid%>' value="<%=ESAPI.encoder().encodeForHTMLAttribute(cardtypeid) + "-" + terminalManager.getCardType(cardtypeid)%>" readonly></td>
                                        <td valign="middle" data-label="Daily Amount Limit" align="center" class="<%=style%>"><input type=text class="form-control" style="width:100px;" name='daily_amount_limit_<%=terminalid%>' value="<%=ESAPI.encoder().encodeForHTMLAttribute(daily_amount_limit)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Daily Amount Limit',<%=terminalid%>)"></td>
                                        <td valign="middle" data-label="Weekly Amount Limit" align="center" class="<%=style%>"><input type=text class="form-control" style="width:100px;" name='weekly_amount_limit_<%=terminalid%>' value="<%=ESAPI.encoder().encodeForHTMLAttribute(weekly_amount_limit)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Weekly Amount Limit',<%=terminalid%>)"></td>
                                        <td valign="middle" data-label="Monthly Amount Limit" align="center" class="<%=style%>"><input type=text class="form-control" style="width:100px;" name='monthly_amount_limit_<%=terminalid%>' value="<%=ESAPI.encoder().encodeForHTMLAttribute(monthly_amount_limit)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Monthly Amount Limit',<%=terminalid%>)"></td>
                                        <td valign="middle" data-label="Daily Card Limit" align="center" class="<%=style%>"><input type=text class="form-control" style="width:100px;" name='daily_card_limit_<%=terminalid%>' value="<%=ESAPI.encoder().encodeForHTMLAttribute(daily_card_limit)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Daily Card Limit',<%=terminalid%>)"></td>
                                        <td valign="middle" data-label="Weekly Card Limit" align="center" class="<%=style%>"><input type=text class="form-control" style="width:100px;" name='weekly_card_limit_<%=terminalid%>' value="<%=ESAPI.encoder().encodeForHTMLAttribute(weekly_card_limit)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Weekly Card Limit',<%=terminalid%>)"></td>
                                        <td valign="middle" data-label="Monthly Card Limit" align="center" class="<%=style%>"><input type=text class="form-control" style="width:100px;" size=10 name='monthly_card_limit_<%=terminalid%>' value="<%=ESAPI.encoder().encodeForHTMLAttribute(monthly_card_limit)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Monthly Card Limit',<%=terminalid%>)"></td>
                                        <td valign="middle" data-label="Action" align="center" class="<%=style%>"><select class="form-control" style="width:inherit" id="action_<%=terminalid%>" name="action_<%=terminalid%>" onchange="ChangeFunction(this.value,'Action',<%=terminalid%>)"><option value="UPDATE_<%=terminalid%>" default>UPDATE</option><%--<option value="DELETE">DELETE</option>--%> </select></td>
                                    </tr>
                                    <thead>
                                    <tr>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_dailycard%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_weeklycard%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_monthly_card%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_daily_ticket%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_weekly_ticket%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_monthly_ticket%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_min%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_max%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_terminal%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=membermappingpreference_terminal_priority%></b></td>
                                        <%-- <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Test Terminal</b></td>--%>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_Is_PSTTerminal%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_auto_conversion%></b></td>


                                    </tr>
                                    </thead>
                                    <tr>
                                        <td  align="center" data-label="Daily Card Amount Limit" valign="middle" class="<%=style%>"> <input type=text class="form-control" style="width:100px;" size=10 name='daily_card_amount_limit_<%=terminalid%>' value="<%=ESAPI.encoder().encodeForHTMLAttribute(daily_card_amount_limit)%>" onchange="ChangeFunction(this.value,'Daily Card Amount Limit',<%=terminalid%>)"></td>
                                        <td  align="center" data-label="Weekly Card Amount Limit" valign="middle" class="<%=style%>"> <input type=text class="form-control" style="width:100px;" size=10 name='weekly_card_amount_limit_<%=terminalid%>' value="<%=ESAPI.encoder().encodeForHTMLAttribute(weekly_card_amount_limit)%>" onchange="ChangeFunction(this.value,'Weekly Card Amount Limit',<%=terminalid%>)"></td>
                                        <td  align="center" data-label="Monthly Card Amount Limit" valign="middle" class="<%=style%>"> <input type=text class="form-control" style="width:100px;" size=10 name='monthly_card_amount_limit_<%=terminalid%>' value="<%=ESAPI.encoder().encodeForHTMLAttribute(monthly_card_amount_limit)%>" onchange="ChangeFunction(this.value,'Monthly Card Amount Limit',<%=terminalid%>)"></td>
                                        <td  align="center" data-label="Daily Avg Ticket" valign="middle" class="<%=style%>"> <input type=text class="form-control" style="width:100px;" size=10 name='daily_avg_ticket_<%=terminalid%>' value="<%=ESAPI.encoder().encodeForHTMLAttribute(daily_avg_ticket)%>"  onchange="ChangeFunction(this.value,'Daily Average Ticket',<%=terminalid%>)"></td>
                                        <td  align="center" data-label="Weekly Avg Ticket" valign="middle" class="<%=style%>"> <input type=text class="form-control" style="width:100px;" size=10 name='weekly_avg_ticket_<%=terminalid%>' value="<%=ESAPI.encoder().encodeForHTMLAttribute(weekly_avg_ticket)%>" onchange="ChangeFunction(this.value,'Weekly Average Ticket',<%=terminalid%>)"></td>
                                        <td  align="center" data-label="Monthly Avg Ticket" valign="middle" class="<%=style%>"> <input type=text class="form-control" style="width:100px;" size=10 name='monthly_avg_ticket_<%=terminalid%>' value="<%=ESAPI.encoder().encodeForHTMLAttribute(monthly_avg_ticket)%>" onchange="ChangeFunction(this.value,'Monthly Average Ticket',<%=terminalid%>)"></td>
                                        <td  align="center" data-label="Min Trans Amount" valign="middle" class="<%=style%>"> <input type=text class="form-control" style="width:100px;" size=10 name='min_trans_amount_<%=terminalid%>' value="<%=ESAPI.encoder().encodeForHTMLAttribute(min_trans_amount)%>" onchange="ChangeFunction(this.value,'Min Transaction Amount',<%=terminalid%>)"></td>
                                        <td  align="center" data-label="Max Trans Amount" valign="middle" class="<%=style%>"> <input type=text class="form-control" style="width:100px;" size=10 name='max_trans_amount_<%=terminalid%>' value="<%=ESAPI.encoder().encodeForHTMLAttribute(max_trans_amount)%>" onchange="ChangeFunction(this.value,'Max Transaction Amount',<%=terminalid%>)"></td>
                                        <td  align="center" data-label="IsActive" valign="middle" class="<%=style%>"><select class="form-control" style="width:100px;" name='isActive_<%=terminalid%>' onchange="ChangeFunction(this.value,'Terminal Status',<%=terminalid%>)"><%out.println(PartnerFunctions.comboval2(ESAPI.encoder().encodeForHTMLAttribute(isActive)));%></select></td>
                                        <td  align="center" data-label="Priority" valign="middle" class="<%=style%>"><select class="form-control" style="width:100px;" id="priority2" name="priority_<%=terminalid%>" onchange="ChangeFunction(this.value,'Terminal Priority',<%=terminalid%>)">

                                            <%
                                                Hashtable priorityHash  = new Hashtable();
                                                priorityHash.put("1","1");
                                                priorityHash.put("2","2");
                                                priorityHash.put("3","3");
                                                priorityHash.put("4","4");
                                                priorityHash.put("5","5");
                                                Enumeration priorityenum    = priorityHash.keys();

                                                String isDef            = "";
                                                String priorityvalue    = "";
                                                while (priorityenum.hasMoreElements())
                                                {
                                                    priorityvalue   = (String)priorityenum.nextElement();

                                                    if((priorityvalue.trim()).equals(ESAPI.encoder().encodeForHTMLAttribute(priority)))
                                                    {
                                                        isDef   = "selected" ;
                                                    }
                                                    else
                                                    {
                                                        isDef   = "";
                                                    }
                                            %><option value="<%=ESAPI.encoder().encodeForHTMLAttribute(priorityvalue)%>" <%=isDef%>   >
                                            <%=ESAPI.encoder().encodeForHTML(priorityvalue)%>
                                        </option>
                                            <% } %>
                                        </select>
                                        </td>
                                        <%--<td align="center" data-label="IsTest" valign="middle" class="<%=style%>"><select class="form-control" style="width:100px" name='isTest'><%out.println(PartnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute(isTest)));%></select></td>
                                        --%><td align="center" data-label="Is PSTTerminal" valign="middle" class="<%=style%>"><select class="form-control" style="width:100px;" name='isPSTTerminal_<%=terminalid%>' onchange="ChangeFunction(this.value,'Is PSTTerminal',<%=terminalid%>)"><%out.println(PartnerFunctions.comboval(ESAPI.encoder().encodeForHTMLAttribute(isPSTTerminal)));%></select></td>
                                        <td align="center" data-label="autoRedirectRequest" valign="middle" class="<%=style%>"><select class="form-control" style="width:160px"
                                                                                                                                       name='autoRedirectRequest_<%=terminalid%>' onchange="ChangeFunction(this.value,'Auto Redirect Request',<%=terminalid%>)"><%
                                            out.println(Functions.comboval7(ESAPI.encoder().encodeForHTMLAttribute(autoRedirectRequest)));%></select></td>
                                    </tr>
                                    <thead>
                                    <tr>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b>Card Tokenization</b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_address%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_Address%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_Risk_Rule%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_min_payout%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_Settlement_Currency%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_Bin_Routing%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_card1%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_Email_Whitelisting%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_EMI_Support%> </b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_Whitelisting_Checks%> </b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_Card_Limit%></b></td>
                                    </tr>
                                    </thead>

                                    <tr>
                                        <td align="center" data-label="Is TokenizationActive" valign="middle" class="<%=style%>"><select class="form-control" style="width:100px;" name='isTokenizationActive_<%=terminalid%>' onchange="ChangeFunction(this.value,'Card Tokenization',<%=terminalid%>)"><%out.println(Functions.comboval8(ESAPI.encoder().encodeForHTMLAttribute(isTokenizationActive)));%></select></td>
                                        <td align="center" data-label="Address Details" valign="middle" class="<%=style%>"><select class="form-control" style="width:100px" name='addressDetails_<%=terminalid%>' onchange="ChangeFunction(this.value,'Address on Payment page',<%=terminalid%>)"><%out.println(Functions.comboval5(ESAPI.encoder().encodeForHTMLAttribute(addressDetails)));%></select></td>
                                        <td align="center" data-label="Address Validation" valign="middle" class="<%=style%>"><select class="form-control" style="width:100px" name='addressValidation_<%=terminalid%>' onchange="ChangeFunction(this.value,'Address Validation',<%=terminalid%>)"><%out.println(Functions.comboval6(ESAPI.encoder().encodeForHTMLAttribute(addressValidation)));%></select></td>
                                        <td align="center" data-label="Risk Rule Activation" valign="middle" class="<%=style%>"><select class="form-control" style="width:100px" name='riskRuleActivation_<%=terminalid%>' onchange="ChangeFunction(this.value,'Risk Rule',<%=terminalid%>)"><%out.println(Functions.comboval8(ESAPI.encoder().encodeForHTMLAttribute(riskRuleActivation)));%></select></td>
                                        <td valign="middle" data-label="Min Payout Amount" align="center" class="<%=style%>"><input type=text class="form-control" style="width:100px;" name='min_payout_amount_<%=terminalid%>' value="<%=ESAPI.encoder().encodeForHTMLAttribute(min_payout_amount)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Min Payout Amount',<%=terminalid%>)"></td>
                                        <td align="center" data-label="Settlement Currency" valign="middle" class="<%=style%>">
                                            <select class="form-control" style="width:100px" name='settlement_currency_<%=terminalid%>' onchange="ChangeFunction(this.value,'Settlement Currency',<%=terminalid%>)">
                                                <%
                                                    for(Currency settlementcurrency:Currency.values())
                                                    {
                                                        String selected="";
                                                        if(settlementcurrency.toString().equals(settlementCurrency))
                                                        {
                                                            selected="selected";
                                                        }
                                                        out.println("<option value=\""+settlementcurrency.toString()+"\" "+selected+">"+settlementcurrency.toString()+"</option>");
                                                    }
                                                %>
                                            </select>
                                        </td>
                                        <td align="center" data-label="Bin Routing" valign="middle" class="<%=style%>"><select class="form-control" style="width:100px" name='bin_routing_<%=terminalid%>' onchange="ChangeFunction(this.value,'Bin Routing',<%=terminalid%>)"><%out.println(Functions.comboval8(ESAPI.encoder().encodeForHTMLAttribute(bin_routing)));%></select></td>
                                        <td align="center" data-label="Is Card Whitelisted" valign="middle" class="<%=style%>"><select class="form-control" style="width:100px" name='isCardWhitelisted_<%=terminalid%>' onchange="ChangeFunction(this.value,'Card Whitelisting',<%=terminalid%>)"><%out.println(partnerFunctions.comboval3(ESAPI.encoder().encodeForHTMLAttribute(isCardWhitelisted)));%></select></td>
                                        <td align="center" data-label="Is Email Whitelisted" valign="middle" class="<%=style%>"><select class="form-control" style="width:100px" name='isEmailWhitelisted_<%=terminalid%>' onchange="ChangeFunction(this.value,'Email Whitelisting',<%=terminalid%>)"><%out.println(Functions.comboval7(ESAPI.encoder().encodeForHTMLAttribute(isEmailWhitelisted)));%></select></td>
                                        <td align="center" data-label="Emi Support" valign="middle" class="<%=style%>"><select class="form-control" style="width:100px" name='emi_support_<%=terminalid%>' onchange="ChangeFunction(this.value,'EMI Support',<%=terminalid%>)"><%out.println(Functions.comboval7(ESAPI.encoder().encodeForHTMLAttribute(emi_support)));%></select></td>
                                        <td align="center" width="100%" data-label="whitelisting" valign="middle" class="<%=style%>">
                                            <select id="whitelist_<%=pos%>" multiple="multiple"  value="<%=whitelisting%>" onchange="ChangeFunction(this.value,'Whitelisting Checks',<%=terminalid%>)">
                                                <option class="option_<%=pos%>" value="ipAddress"><%=membermappingpreference_IP_Address%></option>
                                                <option class="option_<%=pos%>" value="name"><%=membermappingpreference_CardHolder%></option>
                                                <option class="option_<%=pos%>" value="expiryDate"><%=membermappingpreference_ExpiryDate%></option>
                                            </select>
                                            <input type="hidden" id="whitelistcode_<%=pos%>" name="whitelistdetails_<%=terminalid%>" value="<%=whitelisting%>" onchange="ChangeFunction(this.value,'Card Limit Check',<%=terminalid%>)">
                                            <script>
                                                $(function ()
                                                {
                                                    var value=[];
                                                    var details='<%=whitelisting%>';
                                                    console.log("Details::::"+details);
                                                    value=details.split(",");
                                                    for(var i in value)
                                                    {
                                                        $("#whitelist_<%=pos%> option[value='"+value[i]+"']").prop('selected', true);
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
                                                                console.log("Label::::"+labels);
                                                                document.getElementById('whitelistcode_<%=pos%>').value = labels;
                                                                return labels.join(', ') + '';
                                                            }
                                                        }
                                                    });
                                                });

                                            </script>
                                        </td>
                                        <td align="center" data-label="cardLimitCheck" valign="middle" class="<%=style%>"><select class="form-control" style="width:160px" name='cardLimitCheck_<%=terminalid%>'><%out.println(Functions.comboval3(ESAPI.encoder().encodeForHTMLAttribute(cardLimitCheck)));%></select></td>
                                    </tr>
                                    </thead>

                                    <thead>
                                    <tr>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_Card_amount%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_list_amount%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_Automatic_Recurring%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_Restricted_Ticket%> </b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_ManualRecurring%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_CardDetails%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_Payout_Activation%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_Currency%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_Conversion%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b></b><%=membermappingpreference_Card_Encryption%></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b></b><%=membermappingpreference_Action_Executor%></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b></b><%=membermappingpreference_Action_Executor_name%></td>
                                    </tr>
                                    </thead>
                                    <tr>
                                        <td align="center" data-label="cardAmountLimitCheck" valign="middle" class="<%=style%>"><select class="form-control" style="width:160px" name='cardAmountLimitCheck_<%=terminalid%>' onchange="ChangeFunction(this.value,'Card Amount Limit Check',<%=terminalid%>)"><%out.println(Functions.comboval3(ESAPI.encoder().encodeForHTMLAttribute(cardAmountLimitCheck)));%></select></td>
                                        <td align="center" data-label="amountLimitCheck" valign="middle"
                                            class="<%=style%>"><select class="form-control" style="width:160px" name='amountLimitCheck_<%=terminalid%>' onchange="ChangeFunction(this.value,'Amount Limit Check',<%=terminalid%>)"><%out.println(Functions.comboval3(ESAPI.encoder().encodeForHTMLAttribute(amountLimitCheck)));%></select></td>
                                        <td align="center" data-label="is_recurring" valign="middle" class="<%=style%>"><select class="form-control" style="width:160px" name='is_recurring_<%=terminalid%>'onchange="ChangeFunction(this.value,'Automatic Recurring',<%=terminalid%>)"><%out.println(Functions.comboval7(ESAPI.encoder().encodeForHTMLAttribute(is_recurring)));%></select></td>
                                        <td align="center" data-label="isRestrictedTicketActive" valign="middle"
                                            class="<%=style%>"><select class="form-control" style="width:160px" name='isRestrictedTicketActive_<%=terminalid%>' onchange="ChangeFunction(this.value,'Restricted Ticket',<%=terminalid%>)"><%out.println(Functions.comboval8(ESAPI.encoder().encodeForHTMLAttribute(isRestrictedTicketActive)));%></select></td>
                                        <td align="center" data-label="isManualRecurring" valign="middle"
                                            class="<%=style%>"><select class="form-control" style="width:160px" name='isManualRecurring_<%=terminalid%>' onchange="ChangeFunction(this.value,'Manual Recurring',<%=terminalid%>)"><%out.println(Functions.comboval7(ESAPI.encoder().encodeForHTMLAttribute(isManualRecurring)));%></select></td>
                                        <td align="center" data-label="cardDetailRequired" valign="middle"
                                            class="<%=style%>"><select class="form-control" style="width:160px" name='cardDetailRequired_<%=terminalid%>' onchange="ChangeFunction(this.value,'Card Details',<%=terminalid%>)"><%out.println(PartnerFunctions.comboval14(ESAPI.encoder().encodeForHTMLAttribute(cardDetailRequired)));%></select></td>
                                        <td align="center" data-label="payoutActivation" valign="middle"
                                            class="<%=style%>"><select class="form-control" style="width:160px" name='payoutActivation_<%=terminalid%>' onchange="ChangeFunction(this.value,'Payout Activation',<%=terminalid%>)"><%out.println(PartnerFunctions.comboval2(ESAPI.encoder().encodeForHTMLAttribute(payoutActivation)));%></select></td>
                                        <td align="center" data-label="currency_conversion" valign="middle" class="<%=style%>"><select class="form-control" style="width:160px" name='currency_conversion_<%=terminalid%>' onchange="ChangeFunction(this.value,'Currency Conversion',<%=terminalid%>)"><%out.println(Functions.comboval7(ESAPI.encoder().encodeForHTMLAttribute(currency_conversion)));%></select></td>
                                        <td align="center" data-label="currency_conversion" valign="middle" class="<%=style%>">
                                            <select class="form-control" style="width:160px" name='conversion_currency_<%=terminalid%>' onchange="ChangeFunction(this.value,'Conversion Currency',<%=terminalid%>)">

                                                <%
                                                    for (Currency conversioncurrency:Currency.values())
                                                    {
                                                        String selected = "";
                                                        if(conversioncurrency.toString().equals(conversion_currency))
                                                        {
                                                            selected    = "selected";
                                                        }
                                                        out.println("<option value=\""+conversioncurrency.toString()+"\" "+selected+">"+conversioncurrency.toString()+"</option>");
                                                    }
                                                %>
                                            </select>
                                        </td>
                                        <td align="center" data-label="isCardEncryptionEnable" valign="middle" class="<%=style%>"><select class="form-control" style="width:160px" name='isCardEncryptionEnable_<%=terminalid%>' onchange="ChangeFunction(this.value,'Card Encryption',<%=terminalid%>)"><%out.println(Functions.comboval7(ESAPI.encoder().encodeForHTMLAttribute(isCardEncryptionEnable)));%></select></td>

                                        <% if(!functions.isValueNull(actionExecutorId))
                                            actionExecutorId    = "-";
                                            if(!functions.isValueNull(actionExecutorName))
                                                actionExecutorName  = "-";
                                        %>
                                        <td valign="middle" data-label="Action Executor Id" align="center" style="vertical-align: middle;" class="<%=style%>"><b><%=ESAPI.encoder().encodeForHTMLAttribute(actionExecutorId)%></b></td>
                                        <td valign="middle" data-label="Action Executor Name" align="center" style="vertical-align: middle;" class="<%=style%>"><b><%=ESAPI.encoder().encodeForHTMLAttribute(actionExecutorName)%></b></td>
                                    </tr>
                                    <tr>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=payoutPriority%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_daily_amount_limit_check%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_weekly_amount_limit_check%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_monthly_amount_limit_check%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_daily_card_limit_check%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_weekly_card_limit_check%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_monthly_card_limit_check%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_daily_card_amount_limit_check%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_weekly_card_amount_limit_check%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px; "><b><%=membermappingpreference_monthly_card_amount_limit_check%></b></td>
                                    </tr>
                                    <tr>
                                        <td align="center" data-label="currency_conversion" valign="middle" class="<%=style%>">
                                            <select class="form-control" style="width:160px" name='payout_priority_<%=terminalid%>' onchange="ChangeFunction(this.value,'Payout Priority',<%=terminalid%>)">
                                                <%
                                                    for (String payoutLimit : payoutLimitArrayList)
                                                    {
                                                        String selected="";
                                                        if(payout_priority.equals(payoutLimit))
                                                        {
                                                            selected="selected";
                                                        }
                                                        out.println("<option value=\""+payoutLimit+"\" "+selected+">"+payoutLimit+"</option>");
                                                    }
                                                %>
                                            </select>
                                        </td>
                                        <td align="center" data-label="currency_conversion" valign="middle" class="<%=style%>">
                                            <select class="form-control" style="width:160px" name="daily_amount_limit_check_for_terminals_<%=terminalid%>" value="<%=ESAPI.encoder().encodeForHTML(daily_amount_limit_check_for_terminals)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'daily amount limit check for terminals',<%=terminalid%>)">
                                                <%
                                                    for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                                    {
                                                        setSelected="";
                                                        if(yesNoPair.getKey().equals(daily_amount_limit_check_for_terminals))
                                                            setSelected="selected";
                                                        out.println("<option value="+yesNoPair.getKey()+" "+setSelected+">"+yesNoPair.getValue()+"</option>");
                                                    }
                                                %>
                                            </select>
                                        </td>
                                        <td align="center" data-label="currency_conversion" valign="middle" class="<%=style%>">
                                            <select class="form-control" style="width:160px" name="weekly_amount_limit_check_for_terminals_<%=terminalid%>" value="<%=ESAPI.encoder().encodeForHTML(weekly_amount_limit_check_for_terminals)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'weekly amount limit check for  terminals',<%=terminalid%>)">
                                                <%
                                                    for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                                    {
                                                        setSelected="";
                                                        if(yesNoPair.getKey().equals(weekly_amount_limit_check_for_terminals))
                                                            setSelected="selected";
                                                        out.println("<option value="+yesNoPair.getKey()+" "+setSelected+">"+yesNoPair.getValue()+"</option>");
                                                    }
                                                %>
                                            </select>
                                        </td>
                                        <td align="center" data-label="currency_conversion" valign="middle" class="<%=style%>">
                                            <select class="form-control" style="width:160px" name="monthly_amount_limit_check_for_terminals_<%=terminalid%>" value="<%=ESAPI.encoder().encodeForHTML(monthly_amount_limit_check_for_terminals)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'monthly_amount limit check for terminals',<%=terminalid%>)">
                                                <%
                                                    for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                                    {
                                                        setSelected="";
                                                        if(yesNoPair.getKey().equals(monthly_amount_limit_check_for_terminals))
                                                            setSelected="selected";
                                                        out.println("<option value="+yesNoPair.getKey()+" "+setSelected+">"+yesNoPair.getValue()+"</option>");
                                                    }
                                                %>
                                            </select>
                                        </td>
                                        <td align="center" data-label="currency_conversion" valign="middle" class="<%=style%>">
                                            <select class="form-control" style="width:160px" name="daily_card_limit_check_for_terminals_<%=terminalid%>" value="<%=ESAPI.encoder().encodeForHTML(daily_card_limit_check_for_terminals)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'daily card limit check for terminals',<%=terminalid%>)">
                                                <%
                                                    for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                                    {
                                                        setSelected="";
                                                        if(yesNoPair.getKey().equals(daily_card_limit_check_for_terminals))
                                                            setSelected="selected";
                                                        out.println("<option value="+yesNoPair.getKey()+" "+setSelected+">"+yesNoPair.getValue()+"</option>");
                                                    }
                                                %>
                                            </select>
                                        </td>
                                        <td align="center" data-label="currency_conversion" valign="middle" class="<%=style%>">
                                            <select class="form-control" style="width:160px" name="weekly_card_limit_check_for_terminals_<%=terminalid%>" value="<%=ESAPI.encoder().encodeForHTML(weekly_card_limit_check_for_terminals)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'weekly card limit check for terminals',<%=terminalid%>)">
                                                <%
                                                    for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                                    {
                                                        setSelected="";
                                                        if(yesNoPair.getKey().equals(weekly_card_limit_check_for_terminals))
                                                            setSelected="selected";
                                                        out.println("<option value="+yesNoPair.getKey()+" "+setSelected+">"+yesNoPair.getValue()+"</option>");
                                                    }
                                                %>
                                            </select>
                                        </td>
                                        <td align="center" data-label="currency_conversion" valign="middle" class="<%=style%>">
                                            <select class="form-control" style="width:160px" name="monthly_card_limit_check_for_terminals_<%=terminalid%>" value="<%=ESAPI.encoder().encodeForHTML(monthly_card_limit_check_for_terminals)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'monthly card limit check for terminals',<%=terminalid%>)">
                                                <%
                                                    for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                                    {
                                                        setSelected="";
                                                        if(yesNoPair.getKey().equals(monthly_card_limit_check_for_terminals))
                                                            setSelected="selected";
                                                        out.println("<option value="+yesNoPair.getKey()+" "+setSelected+">"+yesNoPair.getValue()+"</option>");
                                                    }
                                                %>
                                            </select>
                                        </td>
                                        <td align="center" data-label="currency_conversion" valign="middle" class="<%=style%>">
                                            <select class="form-control" style="width:160px" name="daily_card_amount_limit_check_for_terminals_<%=terminalid%>" value="<%=ESAPI.encoder().encodeForHTML(daily_card_amount_limit_check_for_terminals)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'daily card amount limit check for terminals',<%=terminalid%>)">
                                                <%
                                                    for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                                    {
                                                        setSelected="";
                                                        if(yesNoPair.getKey().equals(daily_card_amount_limit_check_for_terminals))
                                                            setSelected="selected";
                                                        out.println("<option value="+yesNoPair.getKey()+" "+setSelected+">"+yesNoPair.getValue()+"</option>");
                                                    }
                                                %>
                                            </select>
                                        </td>
                                        <td align="center" data-label="currency_conversion" valign="middle" class="<%=style%>">
                                            <select class="form-control" style="width:160px" name="weekly_card_amount_limit_check_for_terminals_<%=terminalid%>" value="<%=ESAPI.encoder().encodeForHTML(weekly_card_amount_limit_check_for_terminals)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'weekly card amount limit check for terminals',<%=terminalid%>)">
                                                <%
                                                    for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                                    {
                                                        setSelected="";
                                                        if(yesNoPair.getKey().equals(weekly_card_amount_limit_check_for_terminals))
                                                            setSelected="selected";
                                                        out.println("<option value="+yesNoPair.getKey()+" "+setSelected+">"+yesNoPair.getValue()+"</option>");
                                                    }
                                                %>
                                            </select>
                                        </td>
                                        <td align="center" data-label="currency_conversion" valign="middle" class="<%=style%>">
                                            <select class="form-control" style="width:160px" name="monthly_card_amount_limit_check_for_terminals_<%=terminalid%>" value="<%=ESAPI.encoder().encodeForHTML(monthly_card_amount_limit_check_for_terminals)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'monthly card amount limit check for terminals',<%=terminalid%>)">
                                                <%
                                                    for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                                    {
                                                        setSelected="";
                                                        if(yesNoPair.getKey().equals(monthly_card_amount_limit_check_for_terminals))
                                                            setSelected="selected";
                                                        out.println("<option value="+yesNoPair.getKey()+" "+setSelected+">"+yesNoPair.getValue()+"</option>");
                                                    }
                                                %>
                                            </select>
                                        </td>
                                    </tr>
                                    </thead>
                                </table>
                                <table id="savetable">
                                    <tr bgcolor="#FFFFFF" height=50 id="saveid" >
                                        <td valign="middle" data-label="Action" align="center" class="<%=style%>"><input type="button"onclick="confirmsubmit2(<%=terminalid%>)" class="btn btn-default" value="Save"></td>
                                    </tr>
                                </table>
                                <% if(pos<records){ %>
                                </BR>
                            </div>
                            <%
                                    }
                                } //end for
                            %>
                            <input type="button" onclick="confirmsubmit3()" class="goto" value="Save All"
                                   style="font-size: 16px;margin-left: 681px;">
                            <%
                                }
                            %>
                        </form>
                    </div>
                </div>
            </div>
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

<%--
<script src="/partner/NewCss/js/jquery-ui.min.js"></script>
--%>
<script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
</body>
</html>
<%!
    public static String nullToStr(String str)
    {
        if(str == null)
            return "";
        return str;
    }
%>