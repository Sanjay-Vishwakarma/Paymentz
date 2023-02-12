<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.*" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="com.manager.enums.MerchantModuleEnum" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.manager.dao.MerchantDAO" %>
<%@ page import="com.manager.dao.PartnerDAO" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%@ include file="ietest.jsp" %>
<%!
    private Functions functions = new Functions();
    public static String getStatus(String str)
    {
        if("Y".equals(str))
            return "Active";
        else if("N".equals(str))
            return "Inactive";
        else if("T".equals(str))
            return "Test";
        return str;
    }
%>
<%
    String uri = request.getRequestURI();

    String pageName = uri.substring(uri.lastIndexOf("/")+1);

    String buttonvalue=request.getParameter("submit");
    if(buttonvalue==null)
    {
        buttonvalue=(String)session.getAttribute("submit");
    }
    Enumeration<String> attributes = request.getSession().getAttributeNames();
    String attribute="";
    String supportUrl = ApplicationProperties.getProperty("COMPANY_SUPPORT_URL");
    User user =  (User)session.getAttribute("ESAPIUserSessionKey");

    String partnerIcon = (String)session.getAttribute("icon");
    String DefaultTheme=(String)session.getAttribute("defaulttheme");
    String CurrentTheme=(String)session.getAttribute("currenttheme");

    MerchantModuleAccessVO merchantModuleAccessVO=(MerchantModuleAccessVO)session.getAttribute("MerchantModuleAccessVO");

    ResourceBundle rb = null;
    String themevalue="";
    String logoHeight="38";
    String logoWidth="140";

    if(functions.isEmptyOrNull(DefaultTheme) && functions.isEmptyOrNull(CurrentTheme))
    {
        rb = LoadProperties.getProperty("com.directi.pg.ColorTheme", "pz");
        themevalue=rb.getString("pz");
    }
    else if(functions.isEmptyOrNull(CurrentTheme) && functions.isValueNull(DefaultTheme))
    {
        rb = LoadProperties.getProperty("com.directi.pg.ColorTheme",DefaultTheme);
        themevalue=rb.getString(DefaultTheme);
    }
    else if(functions.isValueNull(CurrentTheme) && functions.isValueNull(DefaultTheme))
    {
        rb = LoadProperties.getProperty("com.directi.pg.ColorTheme",CurrentTheme);
        themevalue=rb.getString(CurrentTheme);
    }
    else if(functions.isValueNull(CurrentTheme) && functions.isEmptyOrNull(DefaultTheme))
    {
        rb = LoadProperties.getProperty("com.directi.pg.ColorTheme",CurrentTheme);
        themevalue=rb.getString(CurrentTheme);
    }
    session.setAttribute("colorPallet",themevalue);
    PartnerDAO partnerDAO = new PartnerDAO();
    String partnerid="";
    if(session.getAttribute("partnerid")!=null && !session.getAttribute("partnerid").equals(""))
    {
        partnerid = (String)session.getAttribute("partnerid");
    }
    PartnerDetailsVO partnerDetailsVOMain = null;
    if(functions.isValueNull(partnerid))
    {
        partnerDetailsVOMain = partnerDAO.geturl(partnerid);
    }

   if(partnerDetailsVOMain != null)
    {
        if(functions.isValueNull(partnerDetailsVOMain.getLogoHeight()))
        {
            logoHeight = partnerDetailsVOMain.getLogoHeight();
        }
        if(functions.isValueNull(partnerDetailsVOMain.getLogoWidth()))
        {
            logoWidth = partnerDetailsVOMain.getLogoWidth();
        }
    }

    ResourceBundle resource_Bundle = null;
    String language_property = (String)session.getAttribute("language_property");
    session.setAttribute("language_property",language_property);
    resource_Bundle = LoadProperties.getProperty(language_property);

    String Top_Merchant_Administration_Module = StringUtils.isNotEmpty(resource_Bundle.getString("Top_Merchant_Administration_Module"))?resource_Bundle.getString("Top_Merchant_Administration_Module"): "Merchant Administration Module";
    String Top_Your_session_has_expired =StringUtils.isNotEmpty(resource_Bundle.getString("Top_Your_session_has_expired"))?resource_Bundle.getString("Top_Your_session_has_expired"): "Your session has expired";
    String Top_Click=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Click"))?resource_Bundle.getString("Top_Click"): "Click";
    String Top_here =StringUtils.isNotEmpty(resource_Bundle.getString("Top_here"))?resource_Bundle.getString("Top_here"): "here";
    String Top_merchant_login_page=StringUtils.isNotEmpty(resource_Bundle.getString("Top_merchant_login_page"))?resource_Bundle.getString("Top_merchant_login_page"): "to go to the Merchant login page";
    String Top_English =StringUtils.isNotEmpty(resource_Bundle.getString("Top_English"))?resource_Bundle.getString("Top_English"): "English (US)";
    String Top_Merchant_Profile=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Merchant_Profile"))?resource_Bundle.getString("Top_Merchant_Profile"): "Merchant Profile";
    String Top_Change_Password=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Change_Password"))?resource_Bundle.getString("Top_Change_Password"): "Change Password";
    String Top_Organisation_Profile =StringUtils.isNotEmpty(resource_Bundle.getString("Top_Organisation_Profile"))?resource_Bundle.getString("Top_Organisation_Profile"): "Organisation Profile";
    String Top_Logout=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Logout"))?resource_Bundle.getString("Top_Logout"): "Logout";
    String Top_Merchant_ID=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Merchant_ID"))?resource_Bundle.getString("Top_Merchant_ID"): "Merchant ID";
    String Top_User =StringUtils.isNotEmpty(resource_Bundle.getString("Top_User"))?resource_Bundle.getString("Top_User"): "User";
    String Top_Status =StringUtils.isNotEmpty(resource_Bundle.getString("Top_Status"))?resource_Bundle.getString("Top_Status"): "Status";
    String Top_DashBoard=StringUtils.isNotEmpty(resource_Bundle.getString("Top_DashBoard"))?resource_Bundle.getString("Top_DashBoard"): "DashBoard";
    String Top_Account_Details=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Account_Details"))?resource_Bundle.getString("Top_Account_Details"): "Account Details";
    String Top_Account_Summary=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Account_Summary"))?resource_Bundle.getString("Top_Account_Summary"): "Account Summary";
    String Top_Charges_Summary=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Charges_Summary"))?resource_Bundle.getString("Top_Charges_Summary"): "Charges Summary";
    String Top_Transaction_Summary=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Transaction_Summary"))?resource_Bundle.getString("Top_Transaction_Summary"): "Transaction Summary";
    String Top_Reports=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Reports"))?resource_Bundle.getString("Top_Reports"): "Reports";
    String Top_Settings=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Settings"))?resource_Bundle.getString("Top_Settings"): "Settings";
    String Top_Generate_Key=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Generate_Key"))?resource_Bundle.getString("Top_Generate_Key"): "Generate Key";
    String Top_Merchant_Configuration=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Merchant_Configuration"))?resource_Bundle.getString("Top_Merchant_Configuration"): "Merchant Configuration";
    String Top_Fraud_Rule_Configuration=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Fraud_Rule_Configuration"))?resource_Bundle.getString("Top_Fraud_Rule_Configuration"): "Fraud Rule Configuration";
    String Top_Whitelist_Details=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Whitelist_Details"))?resource_Bundle.getString("Top_Whitelist_Details"): "Whitelist Details";
    String Top_Blacklist_Details=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Blacklist_Details"))?resource_Bundle.getString("Top_Blacklist_Details"): "Blacklist Details";
    String Top_Transaction_Management =StringUtils.isNotEmpty(resource_Bundle.getString("Top_Transaction_Management"))?resource_Bundle.getString("Top_Transaction_Management"): "Transaction Management";
    String Top_Transactions=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Transactions"))?resource_Bundle.getString("Top_Transactions"): "Transactions";
    String Top_Capture=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Capture"))?resource_Bundle.getString("Top_Capture"): "Capture";
    String Top_Reversal=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Reversal"))?resource_Bundle.getString("Top_Reversal"): "Reversal";
    String Top_Payout=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Payout"))?resource_Bundle.getString("Top_Payout"): "Payout";
    String Top_Rejected_Transaction=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Rejected_Transaction"))?resource_Bundle.getString("Top_Rejected_Transaction"): "Rejected Transaction";
    String Top_Invoice=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Invoice"))?resource_Bundle.getString("Top_Invoice"): "Invoice";
    String Top_Generate_Invoice=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Generate_Invoice"))?resource_Bundle.getString("Top_Generate_Invoice"): "Generate Invoice";
    String Top_Invoice_History=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Invoice_History"))?resource_Bundle.getString("Top_Invoice_History"): "Invoice History";
    String Top_Invoice_Configuration=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Invoice_Configuration"))?resource_Bundle.getString("Top_Invoice_Configuration"): "Invoice Configuration";
    String Top_Token_Management=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Token_Management"))?resource_Bundle.getString("Top_Token_Management"): "Token Management";
    String Top_Registration_History=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Registration_History"))?resource_Bundle.getString("Top_Registration_History"): "Registration History";
    String Top_Card_Registration=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Card_Registration"))?resource_Bundle.getString("Top_Card_Registration"): "Card Registration";
    String Top_Register_Card=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Register_Card"))?resource_Bundle.getString("Top_Register_Card"): "Register Card";
    String Top_Virtual_Terminal=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Virtual_Terminal"))?resource_Bundle.getString("Top_Virtual_Terminal"): "Virtual Terminal";
    String Top_Terminal=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Terminal"))?resource_Bundle.getString("Top_Terminal"): "Terminal";
    String Top_Virtual_Checkout=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Virtual_Checkout"))?resource_Bundle.getString("Top_Virtual_Checkout"): "Virtual Checkout";
    String Top_Merchant_Management=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Merchant_Management"))?resource_Bundle.getString("Top_Merchant_Management"): "Merchant Management";
    String Top_User_Management=StringUtils.isNotEmpty(resource_Bundle.getString("Top_User_Management"))?resource_Bundle.getString("Top_User_Management"): "User Management";
    String Top_Merchant_Application=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Merchant_Application"))?resource_Bundle.getString("Top_Merchant_Application"): "Merchant Application";
    String Top_Recurring_Module=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Recurring_Module"))?resource_Bundle.getString("Top_Recurring_Module"): "Recurring Module";
    String Top_Active=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Active"))?resource_Bundle.getString("Top_Active"): "Active";
    String Top_Inactive=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Inactive"))?resource_Bundle.getString("Top_Inactive"): "Inactive";
    String Top_Test=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Test"))?resource_Bundle.getString("Top_Test"): "Test";
    String Top_PayoutTransaction=StringUtils.isNotEmpty(resource_Bundle.getString("Top_PayoutTransaction"))?resource_Bundle.getString("Top_PayoutTransaction"):"Payout Transactions";
    String Checkout_Config=StringUtils.isNotEmpty(resource_Bundle.getString("Checkout_Config"))?resource_Bundle.getString("Checkout_Config"):"Checkout Config";

    String ctoken= null;
    if(user!=null)
    {
        ctoken = user.getCSRFToken();
    }
    String copyiframe=(String)session.getAttribute("fileName");
    Merchants merchants = new Merchants();
    String logo=(String)session.getAttribute("logo");
    MerchantDAO merchantDAO=new MerchantDAO();
    MerchantDetailsVO merchantDetailsVO= merchantDAO.getMemberDetails((String)session.getAttribute("merchantid"));
    String isMerchantLogo=merchantDetailsVO.getIsMerchantLogoBO();
    Hashtable hashtable =merchantDAO.getMemberTemplateDetails((String) session.getAttribute("merchantid"));
    String isMerchantLogoName=(String)hashtable.get((String) session.getAttribute("merchantid"));
    if(functions.isValueNull(isMerchantLogo) && isMerchantLogo.equals("Y") && functions.isValueNull(isMerchantLogoName))
    {
        logo = isMerchantLogoName;
    }
    if (!merchants.isLoggedIn(session))
    {
%>
<html lang="en">
<head>
    <meta http-equiv='Content-Type' content='text/html; charset=utf-8' />
    <meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1' />
</head>
<body>
<form action="index.jsp" method=post>
    <br><br>
    <br>
    <table class=search border="0" cellpadding="0" cellspacing="0" width="50%" align=center valign="center">
        <tr>
            <td align="center" style="background-color: #ffffff;">
                <img src="/images/merchant/<%=session.getAttribute("logo")%>">
            </td>
        </tr>
    </table>
    <br><br>
    <table class=search border="0" bgcolor="#F1EDE0" cellpadding="2" cellspacing="0" width="600"
           bordercolorlight="#000000" bordercolordark="#FFFFFF" align=center valign="center">
        <tr>
            <td bgcolor="#9A1305" class="label" align="left">&nbsp;&nbsp;<%=Top_Merchant_Administration_Module%></td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr align="center">
            <td class="text">&nbsp;&nbsp;<%=Top_Your_session_has_expired %></td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr align="center">
            <td class="text">&nbsp;&nbsp;<%=Top_Click%> <a href="index.jsp" class="link"><%=Top_here %></a><%=Top_merchant_login_page%> </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
    </table>
</form>
</body>
</html>
<%
        return;
    }
    String isPoweredby = merchants.isPoweredBy((String)session.getAttribute("merchantid"));
    Set<String> moduleSet =(Set)session.getAttribute("moduleset");
%>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
<!--************************Color Css*************************************-->
<%--<link href="/merchant/NewCss/colors_css/redtheme.css" rel="stylesheet" />--%>
<script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
<script src="/merchant/NewCss/libs/bootstrap/js/umd/popper.js"></script>
<script src="/merchant/NewCss/libs/bootstrap/js/bootstrap.min.js"></script>
<script src="/merchant/NewCss/libs/jqueryui/jquery-ui-custom.min.js"></script>
<script src="/merchant/NewCss/libs/jquery-ui-touch/jquery.ui.touch-punch.min.js"></script>
<script src="/merchant/NewCss/libs/jquery-detectmobile/detect.js"></script>
<script src="/merchant/NewCss/libs/ios7-switch/ios7.switch.js"></script>
<script src="/merchant/NewCss/libs/fastclick/fastclick.js"></script>
<%--<script src="/merchant/NewCss/libs/jquery-blockui/jquery.blockUI.js"></script>
<script src="/merchant/NewCss/libs/bootstrap-bootbox/bootbox.min.js"></script>

<script src="/merchant/NewCss/libs/nifty-modal/js/classie.js"></script>
<script src="/merchant/NewCss/libs/nifty-modal/js/modalEffects.js"></script>
<script src="/merchant/NewCss/libs/sortable/sortable.min.js"></script>--%>
<script src="/merchant/NewCss/libs/bootstrap-fileinput/bootstrap.file-input.js"></script>
<script src="/merchant/NewCss/libs/bootstrap-select/bootstrap-select.min.js"></script>
<script src="/merchant/NewCss/libs/bootstrap-select2/select2.min.js"></script>
<%--<script src="/merchant/NewCss/libs/magnific-popup/jquery.magnific-popup.min.js"></script>--%>
<script src="/merchant/NewCss/libs/pace/pace.min.js"></script>
<script src="/merchant/NewCss/libs/bootstrap-datepicker/js/bootstrap-datepicker.js"></script>

<script src="/merchant/NewCss/js/init.js"></script>
<script src="/merchant/NewCss/jquery-slimscroll/jquery.slimscroll.min.js"></script>
<!-- Base Css Files -->
<link href="/merchant/NewCss/libs/jqueryui/ui-lightness/jquery-ui-1.10.4.custom.min.css" rel="stylesheet" />
<link href="/merchant/NewCss/libs/bootstrap/css/bootstrap.min.css" rel="stylesheet" />
<link href="/merchant/NewCss/libs/font-awesome/css/font-awesome.min.css" rel="stylesheet" />
<%--<link href="/merchant/NewCss/libs/fontello/css/fontello.css" rel="stylesheet" />
<link href="/merchant/NewCss/libs/nifty-modal/css/component.css" rel="stylesheet" />
<link href="/merchant/NewCss/libs/magnific-popup/magnific-popup.css" rel="stylesheet" />--%>
<link href="/merchant/NewCss/libs/ios7-switch/ios7-switch.css" rel="stylesheet" />
<link href="/merchant/NewCss/libs/pace/pace.css" rel="stylesheet" />
<%--<link href="/merchant/NewCss/libs/sortable/sortable-theme-bootstrap.css" rel="stylesheet" />--%>
<link href="/merchant/NewCss/libs/bootstrap-datepicker/css/datepicker.css" rel="stylesheet" />
<link href="/merchant/NewCss/libs/jquery-icheck/skins/all.css" rel="stylesheet" />
<link href="/merchant/NewCss/css/flags32.css" rel="stylesheet" />
<!-- Code Highlighter for Demo -->
<%--<link href="/merchant/NewCss/libs/prettify/github.css" rel="stylesheet" />
<link href="/merchant/NewCss/libs/jquery-jvectormap/css/jquery-jvectormap-1.2.2.css" rel="stylesheet" type="text/css" />
<link href="/merchant/NewCss/libs/jquery-clock/clock.css" rel="stylesheet" type="text/css" />
<link href="/merchant/NewCss/libs/bootstrap-calendar/css/bic_calendar.css" rel="stylesheet" type="text/css" />
<link href="/merchant/NewCss/libs/sortable/sortable-theme-bootstrap.css" rel="stylesheet" type="text/css" />
<link href="/merchant/NewCss/libs/jquery-weather/simpleweather.css" rel="stylesheet" type="text/css" />
<link href="/merchant/NewCss/libs/bootstrap-xeditable/css/bootstrap-editable.css" rel="stylesheet" type="text/css" />--%>

<link href="/merchant/NewCss/css/style.css" rel="stylesheet" type="text/css" />
<%
    if(functions.isEmptyOrNull(DefaultTheme) && functions.isEmptyOrNull(CurrentTheme))
    {
%>
<link href="/merchant/NewCss/css/pz.css" rel="stylesheet" type="text/css" />
<%
}
else if(functions.isEmptyOrNull(CurrentTheme) && functions.isValueNull(DefaultTheme))
{
%>
<link href="/merchant/NewCss/css/<%=DefaultTheme%>.css" rel="stylesheet" type="text/css" />
<%
}
else if(functions.isValueNull(CurrentTheme) && functions.isValueNull(DefaultTheme))
{
%>
<link href="/merchant/NewCss/css/<%=CurrentTheme%>.css" rel="stylesheet" type="text/css" />
<%
}
else if(functions.isValueNull(CurrentTheme) && functions.isEmptyOrNull(DefaultTheme))
{
%>
<link href="/merchant/NewCss/css/<%=CurrentTheme%>.css" rel="stylesheet" type="text/css" />
<%
    }
%>
<!-- Extra CSS Libraries End -->
<link href="/merchant/NewCss/css/style-responsive.css" rel="stylesheet" />
<div class="md-overlay"></div>
<script>
    var resizefunc = [];

</script>
<style type="text/css">

    .widget.green-1 {
        background-color: #68c39f;
        color: #fff;
    }

    .widget.lightblue-1 {
        background-color: #abb7b7;
        color: #fff;
    }

    .widget.darkblue-2 {
        background-color: #4a525f;
        color: #fff;
    }

    .widget.orange-4 {
        background-color: #edce8c;
        color: #333;
    }

    #wrapper.enlarged .left.side-menu #sidebar-menu ul > li > ul > li{margin-left:-50px;}
    #wrapper.enlarged .left.side-menu #sidebar-menu ul > li:hover > form > button{text-align:center;}
    #wrapper.enlarged .left.side-menu #sidebar-menu ul > li > form > button{text-align:center;}

    #wrapper.enlarged .left.side-menu{position: absolute;}

    .left.side-menu .slimscrollleft{height: 80%!important;}
    #wrapper.enlarged .left.side-menu .slimscrollleft{height: inherit!important;}


    /********************Table Responsive Start**************************/

    .table#myTable > thead > tr > th {font-weight: inherit;}

    @media (max-width: 640px){

        table#myTable {border: 0;}

        table#myTable thead { display: none;}

        #myTable tr:nth-child(odd), #myTable tr:nth-child(even) {background: #ffffff;}

        table#myTable td {
            display: block;
            border-bottom: none;
            padding-left: 0;
            padding-right: 0;
        }

        table#myTable td:before {
            content: attr(data-label);
            float: left;
            width: 100%;
            font-weight: bold;
        }

        #myTable tr:nth-child(odd) {background: #cacaca!important;}

        .widget table tr th, .widget table tr td{
            padding-left: inherit!important;
        }

    }

    table#myTable {
        width: 100%;
        max-width: 100%;
        border-collapse: collapse;
        margin-bottom: 20px;
        display: table;
        border-collapse: separate;
        /*border-color: grey;*/
    }

    #myTable thead {
        display: table-header-group;
        vertical-align: middle;
        border-color: inherit;

    }
    /*#myTable tr:nth-child(odd) {background: #F9F9F9;}*/

    #myTable tr {
        display: table-row;
        vertical-align: inherit;
        border-color: inherit;
    }

    #myTable th {padding-right: 1em;text-align: left;font-weight: bold;}

    #myTable td, #myTable th {display: table-cell;vertical-align: inherit;}

    #myTable tbody {
        display: table-row-group;
        vertical-align: middle;
        border-color: inherit;
    }

    #myTable td {
        padding-top: 6px;
        padding-bottom: 6px;
        padding-left: 10px;
        padding-right: 10px;
        vertical-align: top;
        border-bottom: none;
    }

    .table#myTable>thead>tr>th, .table#myTable>tbody>tr>th, .table#myTable>tfoot>tr>th, .table#myTable>thead>tr>td, .table#myTable>tbody>tr>td, .table#myTable>tfoot>tr>td{border-top: 1px solid #ddd;}

    /********************Table Responsive Ends**************************/
    #showingid{
        float: left;
        font-family:Open Sans,Helvetica Neue,Helvetica,Arial,sans-serif;
        font-size: 12px;
        padding: 10px;
    }

    @media (max-width: 640px){
        #showingid{
            text-align: center;
            float: inherit;
        }
    }

    .btn-default.disabled, .btn-default[disabled], fieldset[disabled] .btn-default, .btn-default.disabled:hover, .btn-default[disabled]:hover, fieldset[disabled] .btn-default:hover, .btn-default.disabled:focus, .btn-default[disabled]:focus, fieldset[disabled] .btn-default:focus, .btn-default.disabled:active, .btn-default[disabled]:active, fieldset[disabled] .btn-default:active, .btn-default.disabled.active, .btn-default.active[disabled], fieldset[disabled] .btn-default.active{
        color: #d6d6d6!important;
    }

    .profile-image {
        border: 4px double rgba(0,0,0,0.0);
        border-radius: inherit!important;
    }

    .rounded-image img .rounded-image img {
        width: 82%;
        margin-left: 10px;
    }

    #wrapper.enlarged .left.side-menu .rounded-image img{
        width: 100%;
        margin-left: 0px;
    }


    /***USER GUIDE***/
    .userguide{
        background:url("/merchant/images/user-guide-black.png");
        width: 28px;
        height: 28px;
        content: '';
        background-position: center!important;
        padding: 13px;
        background-repeat: no-repeat!important;
        background-size: 20px 20px!important;
        margin-right: 8px;
    }
    #sidebar-menu > ul > li > form > button.active img.userguide, #sidebar-menu > ul > li > form > button.active.subdrop img.userguide{
        background:url("/merchant/images/user-guide-white.png");
    }


    #sidebar-menu > ul > form > li > button > i{
        font-size: initial;
        /*color: rgba(0,0,0,0.4);*/
    }

    #sidebar-menu > ul > li.has_sub > button.active,
    #sidebar-menu > ul > li.has_sub > button.active.subdrop {
        color: #fff;

        font-weight: 600;
        border-left: 0px solid rgba(0,0,0,0.3);
    }


    #wrapper.enlarged .left.side-menu #sidebar-menu ul > li {
        white-space: inherit!important;
    }

    @media (min-width: 768px) {
        .navbar-right {
            margin-right: 0px!important;
        }
    }

    .icon-ccw-1{display: none!important;}

    @media (max-width: 640px) {
        .widget .additional-btn {
            float: right;
            position: inherit;
        }
    }


    .apperrormsg{
        color: red;
        display: block;
        text-align: right;
    }

    #user_initial{
        color: #ffffff;
        font-size: 28px;
        text-align: center;
        font-weight: bold;
        font-family: monospace;
        margin: 6px;
    }

</style>

<%--<body class="widescreen pace-done fixed-left">--%>

<!-- Begin page -->
<div id="wrapper" class="wrapmenu">

    <!-- Top Bar Start -->
    <div class="topbar">
        <div class="topbar-left">
            <div class="logo" style="width:auto; height:55px;">
                <%--<h1> --%>
                <a <%--href="#"--%>><%--<img src="/merchant/images/ForWebsiteSagarNew3.png" alt="Logo" style="margin-right: 30px;">--%>
                    <img src="/images/merchant/<%=logo%>" style="width: <%=logoWidth%>px;height:100%; object-fit:contain;margin-left:30px;">
                </a>
                <%--</h1>--%>
            </div>
            <button class="button-menu-mobile open-left">
                <i class="fa fa-bars"></i>
            </button>
        </div>
        <!-- Button mobile view to collapse sidebar menu -->
        <div class="navbar navbar-default" role="navigation">
            <div class="container">
                <div class="navbar-collapse2">
                    <ul class="nav navbar-nav hidden-xs">
                        <li class="language_bar dropdown hidden-xs">
                            <a class="dropdown-toggle" data-toggle="dropdown" style="cursor:pointer;"><i class="fa fa-language" aria-hidden="true"></i> Select Language <%--<i class="fa fa-caret-down"></i>--%></a>
                            <ul class="dropdown-menu pull-right f32">
                                <li style="margin-top: 10px;">
                                    <form action="/merchant/servlet/LanguageRedirect?ctoken=<%=ctoken%>">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <input type="hidden" value="<%=pageName%>" name="page_name">
                                        <input type="hidden" value="ja" name="language1">
                                        <button type="submit" name="submit" class="md-trigger" style="width: 100%;text-align: left;text-indent: 11px;">
                                            <li class="flag jp"></li> Japanese  </button>
                                    </form>
                                </li>
                                <li style="margin-top: 10px;">
                                    <form action="/merchant/servlet/LanguageRedirect?ctoken=<%=ctoken%>">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <input type="hidden" value="<%=pageName%>" name="page_name">
                                        <input type="hidden" value="en" name="language1">
                                        <button type="submit" name="submit" class="md-trigger" style="width: 100%;text-align: left;text-indent: 11px;">
                                            <li class="flag us"></li>  <%=Top_English%></button>
                                    </form>
                                </li>
                            </ul>
                        </li>
                    </ul>
                    <ul class="nav navbar-nav navbar-right top-navbar">
                        <li class="dropdown iconify hide-phone"><a style="cursor: pointer;" onclick="javascript:toggle_fullscreen()"><i class="icon-resize-full-2"></i></a></li>
                        <li class="dropdown topbar-profile">
                            <a class="dropdown-toggle" data-toggle="dropdown" style="cursor: pointer;">
                                <span class="rounded-image topbar-profile-image">
                                    <%--<img src="/merchant/images/<%=user.getAccountName().substring(0,1).toLowerCase()%>.png">--%>
                                    <p id="user_initial"><%=user.getAccountName().substring(0,1).toUpperCase()%></p>
                                </span><strong><%=user.getAccountName()%></strong> <i class="fa fa-caret-down"></i>
                            </a>
                            <ul class="dropdown-menu">
                                <% if("Y".equals(merchantModuleAccessVO.getSettingsMerchantProfileAccess()))
                                {
                                    if(moduleSet.contains(MerchantModuleEnum.MERCHANT_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                                    {
                                %>
                                <li style="margin-top: 10px;">
                                    <form action="/merchant/servlet/MerchantProfile?ctoken=<%=ctoken%>">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <button type="submit" name="submit" class="md-trigger" style="width: 100%;text-align: left;text-indent: 4px;">
                                            <%=Top_Merchant_Profile%></button>
                                    </form>
                                </li>
                                <%
                                        }
                                    }
                                %>
                                <li style="margin-top: 10px;">
                                    <form action="/merchant/chngpwd.jsp?ctoken=<%=ctoken%>">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <button type="submit" name="submit" class="md-trigger" style="width: 100%;text-align: left;text-indent: 4px;">
                                            <%=Top_Change_Password%></button>
                                    </form>
                                </li>
                                <%
                                    if("Y".equals(merchantModuleAccessVO.getSettingsOrganisationProfileAccess()))
                                    {
                                        if(moduleSet.contains(MerchantModuleEnum.ORGANISATION_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                                        {
                                %>
                                <li>
                                    <form action="/merchant/servlet/PopulateSpeedOption?ctoken=<%=ctoken%>" >
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <button type="submit" name="submit" class="md-trigger" style="width: 100%;text-align: left;text-indent: 4px;">
                                            <%=Top_Organisation_Profile%></button>
                                    </form>
                                </li>
                                <%
                                        }
                                    }
                                %>
                                <li class="divider"></li>
                                <li>
                                    <form action="/merchant/Logout.jsp?ctoken=<%=ctoken%>" class="menufont" style="">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <button type="submit" name="submit" class="md-trigger" data-modal="logout-modal" style="width: 100%;text-align: left;text-indent: 4px;">
                                            <i class="icon-logout-1"></i> <%=Top_Logout%></button>
                                    </form>
                                </li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
    <!-- Top Bar End -->
    <!-- Left Sidebar Start -->
    <div class="left side-menu">
        <br>
        <div class="profile-info">
            <%
                if(functions.isValueNull(partnerIcon))
                {
            %>
            <div class="col-xs-4">
                <a class="rounded-image profile-image"><img src="/images/merchant/<%=partnerIcon%>"></a>
            </div>
            <%
            }
            else
            {
            %>
            <div class="col-xs-4">
            </div>
            <%
                }
                String final_status="";
                String staus_activation = getStatus((String) session.getAttribute("activation"));
                if(staus_activation.equals("Active")){
                    final_status =Top_Active;
                }else  if(staus_activation.equals("Inactive")){
                    final_status =Top_Inactive;
                }else if(staus_activation.equals("Test")){
                    final_status =Top_Test;
                }

            %>
            <div class="col-xs-8">
                <br>
                <div class="profile-text">
                    <%=Top_Merchant_ID%> : <%=(String) session.getAttribute("merchantid")%> <br>
                    <%=Top_User%> : <%=user.getAccountName()%><br>
                    <%=Top_Status%> : <%=final_status%><br>
                </div>
            </div>
        </div>
        <!--- Divider -->
        <div class="clearfix"></div>
        <hr class="divider" />
        <div class="clearfix"></div>
        <!--- Divider -->
        <%--<div class="some-content-related-div">--%>
        <%--<div id="inner-content-div" style="height:500px;">--%>
        <div id="sidebar-menu" class="slimscrollleft">
            <ul>
                <%
                    if("Y".equals(merchantModuleAccessVO.getDashboardAccess()))
                    {
                        if(moduleSet.contains(MerchantModuleEnum.DASHBOARD.name()) || (!user.getRoles().contains("submerchant")))
                        {
                %>
                <form action="/merchant/servlet/DashBoard?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                    <li>
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="DashBoard" name="submit" class="button1"
                                <%
                                    if(request.getParameter("submit")!=null){
                                        if(request.getParameter("submit").equals("DashBoard"))
                                        {   %>
                                style="color:#ffffff" id="active_menu"
                                <%}
                                }
                                %>><i class="fa fa-bar-chart-o iconstyle" style="float:left;"></i><span><%=Top_DashBoard%></span>
                        </button>
                    </li>
                </form>
                <%
                        }
                    }
                    if("Y".equals(merchantModuleAccessVO.getAccountingAccess()))
                    {
                        if(moduleSet.contains(MerchantModuleEnum.ACCOUNT_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                        {
                %>
                <li>
                    <% if(buttonvalue==null)
                    {%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;margin-bottom: 0px;" class="button1">
                        <i class="fa fa-user-md iconstyle" style="float:left;"></i><span><%=Top_Account_Details%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: none;">
                        <% if("Y".equals(merchantModuleAccessVO.getAccountsAccountSummaryAccess()))
                        {
                            if(moduleSet.contains(MerchantModuleEnum.ACCOUNT_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                            {
                        %>
                        <li>
                            <form action="/merchant/accountSummary.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Account Summary" name="submit" class="button3">
                                    <%=Top_Account_Summary%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getAccountsChargesSummaryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CHARGES_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/charges.jsp?ctoken=<%=ctoken%>&copyiframe=<%=copyiframe%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Charges Summary" name="submit" class="button3">
                                    <%=Top_Charges_Summary%>
                                </button>
                                <input type="hidden" value='<%=copyiframe%>' name="copyiframe">
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getAccountsTransactionSummaryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.TRANSACTION_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/transactionSummary.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Transaction Summary" name="submit" class="button3">
                                    <%=Top_Transaction_Summary%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getAccountsReportsSummaryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.REPORTS_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/reports.jsp?ctoken=<%=ctoken%>&copyiframe=<%=copyiframe%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Reports" name="submit" class="button3">
                                    <%=Top_Reports%>
                                </button>
                                <input type="hidden" value='<%=copyiframe%>' name="copyiframe">
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                    <%
                    }
                    else
                    {if("Account Summary".equals(buttonvalue)){
                    %>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;color:#ffffff; margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span><%=Top_Account_Details%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <% if("Y".equals(merchantModuleAccessVO.getAccountsAccountSummaryAccess()))
                        {
                            if(moduleSet.contains(MerchantModuleEnum.ACCOUNT_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                            {
                        %>
                        <li>
                            <form action="/merchant/accountSummary.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Account Summary" name="submit" class="button3"  style="background-color:#d9d9d9 ">
                                    <%=Top_Account_Summary%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getAccountsChargesSummaryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CHARGES_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/charges.jsp?ctoken=<%=ctoken%>&copyiframe=<%=copyiframe%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Charges Summary" name="submit" class="button3">
                                    <%=Top_Charges_Summary%>
                                </button>
                                <input type="hidden" value='<%=copyiframe%>' name="copyiframe">
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getAccountsTransactionSummaryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.TRANSACTION_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/transactionSummary.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Transaction Summary" name="submit" class="button3">
                                    <%=Top_Transaction_Summary%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getAccountsReportsSummaryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.REPORTS_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/reports.jsp?ctoken=<%=ctoken%>&copyiframe=<%=copyiframe%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Reports" name="submit" class="button3">
                                    <%=Top_Reports%>
                                </button>
                                <input type="hidden" value='<%=copyiframe%>' name="copyiframe">
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                    <%
                    }
                    else if("Transaction Summary".equals(buttonvalue))
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;color:#ffffff; margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span><%=Top_Account_Details%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <% if("Y".equals(merchantModuleAccessVO.getAccountsAccountSummaryAccess()))
                        {
                            if(moduleSet.contains(MerchantModuleEnum.ACCOUNT_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                            {
                        %>
                        <li>
                            <form action="/merchant/accountSummary.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Account Summary" name="submit" class="button3">
                                    <%=Top_Account_Summary%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getAccountsChargesSummaryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CHARGES_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/charges.jsp?ctoken=<%=ctoken%>&copyiframe=<%=copyiframe%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Charges Summary" name="submit" class="button3">
                                    <%=Top_Charges_Summary%>
                                </button>
                                <input type="hidden" value='<%=copyiframe%>' name="copyiframe">
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getAccountsTransactionSummaryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.TRANSACTION_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/transactionSummary.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Transaction Summary" name="submit" class="button3" style="background-color:#d9d9d9">
                                    <%=Top_Transaction_Summary%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getAccountsReportsSummaryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.REPORTS_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/reports.jsp?ctoken=<%=ctoken%>&copyiframe=<%=copyiframe%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Reports" name="submit" class="button3">
                                    <%=Top_Reports%>
                                </button>
                                <input type="hidden" value='<%=copyiframe%>' name="copyiframe">
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                    <%
                    }
                    else if("Reports".equals(buttonvalue))
                    {%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;color:#ffffff; margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span><%=Top_Account_Details%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <% if("Y".equals(merchantModuleAccessVO.getAccountsAccountSummaryAccess()))
                        {
                            if(moduleSet.contains(MerchantModuleEnum.ACCOUNT_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                            {
                        %>
                        <li>
                            <form action="/merchant/accountSummary.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Account Summary" name="submit" class="button3">
                                    <%=Top_Account_Summary%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getAccountsChargesSummaryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CHARGES_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/charges.jsp?ctoken=<%=ctoken%>&copyiframe=<%=copyiframe%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Charges Summary" name="submit" class="button3">
                                    <%=Top_Charges_Summary%>
                                </button>
                                <input type="hidden" value='<%=copyiframe%>' name="copyiframe">
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getAccountsTransactionSummaryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.TRANSACTION_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/transactionSummary.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Transaction Summary" name="submit" class="button3">
                                    <%=Top_Transaction_Summary%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getAccountsReportsSummaryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.REPORTS_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/reports.jsp?ctoken=<%=ctoken%>&copyiframe=<%=copyiframe%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Reports" name="submit" class="button3"  style="background-color:#d9d9d9 ">
                                    <%=Top_Reports%>
                                </button>
                                <input type="hidden" value='<%=copyiframe%>' name="copyiframe">
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                    <%
                    }
                    else if("Charges Summary".equals(buttonvalue))
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;color:#ffffff; margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span><%=Top_Account_Details%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <% if("Y".equals(merchantModuleAccessVO.getAccountsAccountSummaryAccess()))
                        {
                            if(moduleSet.contains(MerchantModuleEnum.ACCOUNT_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                            {
                        %>
                        <li>
                            <form action="/merchant/accountSummary.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Account Summary" name="submit" class="button3">
                                    <%=Top_Account_Summary%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getAccountsChargesSummaryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CHARGES_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/charges.jsp?ctoken=<%=ctoken%>&copyiframe=<%=copyiframe%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Charges Summary" name="submit" class="button3" style="background-color:#d9d9d9 ">
                                    <%=Top_Charges_Summary%>
                                </button>
                                <input type="hidden" value='<%=copyiframe%>' name="copyiframe">
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getAccountsTransactionSummaryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.TRANSACTION_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/transactionSummary.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Transaction Summary" name="submit" class="button3">
                                    <%=Top_Transaction_Summary%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getAccountsReportsSummaryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.REPORTS_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/reports.jsp?ctoken=<%=ctoken%>&copyiframe=<%=copyiframe%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Reports" name="submit" class="button3"  >
                                    <%=Top_Reports%>
                                </button>
                                <input type="hidden" value='<%=copyiframe%>' name="copyiframe">
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                    <%
                    }else
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;" class="button1">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i>
                        <span><%=Top_Account_Details%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul>
                        <% if("Y".equals(merchantModuleAccessVO.getAccountsAccountSummaryAccess()))
                        {
                            if(moduleSet.contains(MerchantModuleEnum.ACCOUNT_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                            {
                        %>
                        <li>
                            <form action="/merchant/accountSummary.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Account Summary" name="submit" class="button3">
                                    <%=Top_Account_Summary%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getAccountsChargesSummaryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CHARGES_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/charges.jsp?ctoken=<%=ctoken%>&copyiframe=<%=copyiframe%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Charges Summary" name="submit" class="button3">
                                    <%=Top_Charges_Summary%>
                                </button>
                                <input type="hidden" value='<%=copyiframe%>' name="copyiframe">
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getAccountsTransactionSummaryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.TRANSACTION_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/transactionSummary.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Transaction Summary" name="submit" class="button3">
                                    <%=Top_Transaction_Summary%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getAccountsReportsSummaryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.REPORTS_SUMMARY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/reports.jsp?ctoken=<%=ctoken%>&copyiframe=<%=copyiframe%>&copyiframe=<%=copyiframe%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Reports" name="submit" class="button3">
                                    <%=Top_Reports%>
                                </button>
                                <input type="hidden" value='<%=copyiframe%>' name="copyiframe">
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul><%}}%>
                </li>
                <%
                        }
                    }
                    if("Y".equals(merchantModuleAccessVO.getSettingAccess()))
                    {
                        if(moduleSet.contains(MerchantModuleEnum.SETTINGS.name()) || (!user.getRoles().contains("submerchant")))
                        {
                %>
                <li>
                    <%
                        if(buttonvalue==null){%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;;margin-left: 0px;text-align: left;" class="button1">
                        <i class="fa fa-wrench iconstyle" style="float:left;"></i><span><%=Top_Settings%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span><%--<span class="caret" style="float:right;"></span>--%>
                    </button>
                    <ul>
                        <% if("Y".equals(merchantModuleAccessVO.getSettingsMerchantProfileAccess()))
                        {
                            if(moduleSet.contains(MerchantModuleEnum.MERCHANT_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                            {
                        %>
                        <li>
                            <form action="/merchant/servlet/MerchantProfile?ctoken=<%=ctoken%>"  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Profile" name="submit" class="button3">
                                    <%=Top_Merchant_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsOrganisationProfileAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.ORGANISATION_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/PopulateSpeedOption?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="speedoption" name="submit" class="button3">
                                    <%=Top_Organisation_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }

                                    if("Y".equals(merchantModuleAccessVO.getSettingsGenerateviewaccess()))
                                {
                                    if(moduleSet.contains(MerchantModuleEnum.VIEW_KEY.name()) || (!user.getRoles().contains("submerchant")))
                                    {

                            /*if("Y".equals(merchantModuleAccessVO.getSettingsCheckoutPageAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CHECKOUT_PAGE.name()) || (!user.getRoles().contains("submerchant")))
                                {*/
                        %>
                        <li >
                            <form action="/merchant/generateview.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="View Key" name="submit" class="button3">
                                    View Key
                                </button>
                            </form>
                        </li>

                        <%--<li>
                            <form action="/merchant/servlet/MerchantTemplate?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Transaction Pages" name="submit" class="button3">
                                    Checkout Page
                                </button>
                            </form>
                        </li>--%>
                        <%
                                }

                        }
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateKeyAccess()))
                            {
                                if(!user.getRoles().contains("submerchant"))
                                {
                        %>
                        <li >
                            <form action="/merchant/generatekey.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Generate Key" name="submit" class="button3">
                                    <%=Top_Generate_Key%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsMerchantConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/merchantConfigDetails.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Config Details" name="submit" class="button3">
                                    <%=Top_Merchant_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getMerchantCheckoutConfig()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CHECKOUT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/MerchantCheckoutConfig?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Checkout Config" name="submit" class="button3">
                                    <%=Checkout_Config%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsFraudRuleConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.FRAUD_RULE_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/ListMerchantFraudRule?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3">
                                    <%=Top_Fraud_Rule_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingWhiteListDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3">
                                    <%=Top_Whitelist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingBlacklistDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/blockCountry.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3">
                                    <%=Top_Blacklist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul><%}
                else{
                    if("Merchant Profile".equals(buttonvalue)){%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;color:#ffffff; margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-wrench iconstyle" style="float:left;"></i><span><%=Top_Settings%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: block;">
                        <% if("Y".equals(merchantModuleAccessVO.getSettingsMerchantProfileAccess()))
                        {
                            if(moduleSet.contains(MerchantModuleEnum.MERCHANT_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                            {
                        %>
                        <li>
                            <form action="/merchant/servlet/MerchantProfile?ctoken=<%=ctoken%>"  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Profile" name="submit" class="button3" style="background-color:#d9d9d9">
                                    <%=Top_Merchant_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsOrganisationProfileAccess()))
                            {
                                if (moduleSet.contains(MerchantModuleEnum.ORGANISATION_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/PopulateSpeedOption?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="speedoption" name="submit" class="button3">
                                    <%=Top_Organisation_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                           /* if("Y".equals(merchantModuleAccessVO.getSettingsCheckoutPageAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CHECKOUT_PAGE.name()) || (!user.getRoles().contains("submerchant")))
                                {*/

                            /*}
                        }*/
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateviewaccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.VIEW_KEY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/generateview.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="View Key" name="submit" class="button3">
                                    View Key
                                </button>
                            </form>
                        </li>
                        <%--<li>
                            <form action="/merchant/servlet/MerchantTemplate?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Transaction Pages" name="submit" class="button3">
                                    Checkout Page
                                </button>
                            </form>
                        </li>--%>
                        <%
                            /*}
                        }*/
                            }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateKeyAccess()))
                            {
                                if(!user.getRoles().contains("submerchant"))
                                {
                        %>
                        <li >
                            <form action="/merchant/generatekey.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Generate Key" name="submit" class="button3">
                                    <%=Top_Generate_Key%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsMerchantConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/merchantConfigDetails.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Config Details" name="submit" class="button3">
                                    <%=Top_Merchant_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getMerchantCheckoutConfig()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CHECKOUT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/MerchantCheckoutConfig?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Checkout Config" name="submit" class="button3">
                                    <%=Checkout_Config%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsFraudRuleConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.FRAUD_RULE_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/ListMerchantFraudRule?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3">
                                    <%=Top_Fraud_Rule_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingWhiteListDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3">
                                    <%=Top_Whitelist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingBlacklistDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/blockCountry.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3">
                                    <%=Top_Blacklist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                    <%
                    }else if("speedoption".equals(buttonvalue))
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;color:#ffffff; margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-wrench iconstyle" style="float:left;"></i><span><%=Top_Settings%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: block;">
                        <% if("Y".equals(merchantModuleAccessVO.getSettingsMerchantProfileAccess()))
                        {
                            if(moduleSet.contains(MerchantModuleEnum.MERCHANT_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                            {
                        %>
                        <li>
                            <form action="/merchant/servlet/MerchantProfile?ctoken=<%=ctoken%>"  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Profile" name="submit" class="button3">
                                    <%=Top_Merchant_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsOrganisationProfileAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.ORGANISATION_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/PopulateSpeedOption?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="speedoption" name="submit" class="button3" style="background-color:#d9d9d9">
                                    <%=Top_Organisation_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            /*if("Y".equals(merchantModuleAccessVO.getSettingsCheckoutPageAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CHECKOUT_PAGE.name()) || (!user.getRoles().contains("submerchant")))
                                {*/
                        %>
                        <%
                            /*  }
                          }*/
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateviewaccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.VIEW_KEY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/generateview.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="View Key" name="submit" class="button3">
                                    View Key
                                </button>
                            </form>
                        </li>
                        <%-- <li>
                             <form action="/merchant/servlet/MerchantTemplate?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="Transaction Pages" name="submit" class="button3">
                                     Checkout Page
                                 </button>
                             </form>
                         </li>--%>
                        <%
                            /*  }
                          }*/
                            }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateKeyAccess()))
                            {
                                if(!user.getRoles().contains("submerchant"))
                                {
                        %>
                        <li >
                            <form action="/merchant/generatekey.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Generate Key" name="submit" class="button3">
                                    <%=Top_Generate_Key%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsMerchantConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/merchantConfigDetails.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Config Details" name="submit" class="button3">
                                    <%=Top_Merchant_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getMerchantCheckoutConfig()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CHECKOUT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>


                        <li >
                            <form action="/merchant/servlet/MerchantCheckoutConfig?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Checkout Config" name="submit" class="button3">
                                    <%=Checkout_Config%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsFraudRuleConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.FRAUD_RULE_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/ListMerchantFraudRule?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3">
                                    <%=Top_Fraud_Rule_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingWhiteListDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3">
                                    <%=Top_Whitelist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingBlacklistDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/blockCountry.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3">
                                    <%=Top_Blacklist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                    <%
                    }else if("Transaction Pages".equals(buttonvalue))
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;color:#ffffff; margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-wrench iconstyle" style="float:left;"></i><span><%=Top_Settings%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: block;">
                        <% if("Y".equals(merchantModuleAccessVO.getSettingsMerchantProfileAccess()))
                        {
                            if(moduleSet.contains(MerchantModuleEnum.MERCHANT_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                            {
                        %>
                        <li>
                            <form action="/merchant/servlet/MerchantProfile?ctoken=<%=ctoken%>"  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Profile" name="submit" class="button3">
                                    <%=Top_Merchant_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsOrganisationProfileAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.ORGANISATION_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/PopulateSpeedOption?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="speedoption" name="submit" class="button3">
                                    <%=Top_Organisation_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            /*if("Y".equals(merchantModuleAccessVO.getSettingsCheckoutPageAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CHECKOUT_PAGE.name()) || (!user.getRoles().contains("submerchant")))
                                {*/
                        %>
                        <%
                            /*}
                        }*/
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateviewaccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.VIEW_KEY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/generateview.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="View Key" name="submit" class="button3">
                                    View Key
                                </button>
                            </form>
                        </li>
                        <%-- <li>
                             <form action="/merchant/servlet/MerchantTemplate?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="Transaction Pages" name="submit" class="button3" style="background-color:#d9d9d9">
                                     Checkout Page
                                 </button>
                             </form>
                         </li>--%>
                        <%
                            /*}
                        }*/
                            }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateKeyAccess()))
                            {
                                if(!user.getRoles().contains("submerchant"))
                                {
                        %>
                        <li >
                            <form action="/merchant/generatekey.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Generate Key" name="submit" class="button3">
                                    <%=Top_Generate_Key%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsMerchantConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/merchantConfigDetails.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Config Details" name="submit" class="button3">
                                    <%=Top_Merchant_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getMerchantCheckoutConfig()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CHECKOUT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/MerchantCheckoutConfig?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Checkout Config" name="submit" class="button3">
                                    <%=Checkout_Config%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsFraudRuleConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.FRAUD_RULE_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/ListMerchantFraudRule?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3">
                                    <%=Top_Fraud_Rule_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingWhiteListDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3">
                                    <%=Top_Whitelist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingBlacklistDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/blockCountry.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3">
                                    <%=Top_Blacklist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                    <%
                    }else if("View Key".equals(buttonvalue))
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;color:#ffffff; margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-wrench iconstyle" style="float:left;"></i><span><%=Top_Settings%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: block;">
                        <% if("Y".equals(merchantModuleAccessVO.getSettingsMerchantProfileAccess()))
                        {
                            if(moduleSet.contains(MerchantModuleEnum.MERCHANT_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                            {
                        %>
                        <li>
                            <form action="/merchant/servlet/MerchantProfile?ctoken=<%=ctoken%>"  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Profile" name="submit" class="button3">
                                    <%=Top_Merchant_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsOrganisationProfileAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.ORGANISATION_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/PopulateSpeedOption?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="speedoption" name="submit" class="button3">
                                    <%=Top_Organisation_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            /*if("Y".equals(merchantModuleAccessVO.getSettingsCheckoutPageAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CHECKOUT_PAGE.name()) || (!user.getRoles().contains("submerchant")))
                                {*/
                        %>
                        <%
                            /*}
                        }*/
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateviewaccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.VIEW_KEY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/generateview.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="View Key" name="submit" class="button3">
                                    View Key
                                </button>
                            </form>
                        </li>
                        <%-- <li>
                             <form action="/merchant/servlet/MerchantTemplate?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="Transaction Pages" name="submit" class="button3" style="background-color:#d9d9d9">
                                     Checkout Page
                                 </button>
                             </form>
                         </li>--%>
                        <%
                                    /*}
                                }*/
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateKeyAccess()))
                            {
                                if(!user.getRoles().contains("submerchant"))
                                {
                        %>
                        <li >
                            <form action="/merchant/generatekey.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Generate Key" name="submit" class="button3">
                                    <%=Top_Generate_Key%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsMerchantConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/merchantConfigDetails.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Config Details" name="submit" class="button3">
                                    <%=Top_Merchant_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getMerchantCheckoutConfig()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CHECKOUT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/MerchantCheckoutConfig?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Checkout Config" name="submit" class="button3">
                                    <%=Checkout_Config%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsFraudRuleConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.FRAUD_RULE_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/ListMerchantFraudRule?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3">
                                    <%=Top_Fraud_Rule_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingWhiteListDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3">
                                    <%=Top_Whitelist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingBlacklistDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/blockCountry.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3">
                                    <%=Top_Blacklist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                    <%
                    }else if("Generate Key".equals(buttonvalue))
                    {%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;color:#ffffff; margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-wrench iconstyle" style="float:left;"></i><span><%=Top_Settings%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: block;">
                        <% if("Y".equals(merchantModuleAccessVO.getSettingsMerchantProfileAccess()))
                        {
                            if(moduleSet.contains(MerchantModuleEnum.MERCHANT_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                            {
                        %>
                        <li>
                            <form action="/merchant/servlet/MerchantProfile?ctoken=<%=ctoken%>"  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Profile" name="submit" class="button3">
                                    <%=Top_Merchant_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsOrganisationProfileAccess()))
                            {
                                if (moduleSet.contains(MerchantModuleEnum.ORGANISATION_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/PopulateSpeedOption?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="speedoption" name="submit" class="button3">
                                    <%=Top_Organisation_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                          /*  if("Y".equals(merchantModuleAccessVO.getSettingsCheckoutPageAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CHECKOUT_PAGE.name()) || (!user.getRoles().contains("submerchant")))
                                {*/
                        %>
                        <%-- <li>
                             <form action="/merchant/servlet/MerchantTemplate?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="Transaction Pages" name="submit" class="button3">
                                     Checkout Page
                                 </button>
                             </form>
                         </li>--%>
                        <%
                            /*}
                        }*/
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateviewaccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.VIEW_KEY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/generateview.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="View Key" name="submit" class="button3" style="background-color:#d9d9d9">
                                    View Key
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        }
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateKeyAccess()))
                            {
                                if(!user.getRoles().contains("submerchant"))
                                {
                        %>
                        <li >
                            <form action="/merchant/generatekey.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Generate Key" name="submit" class="button3" style="background-color:#d9d9d9">
                                    <%=Top_Generate_Key%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsMerchantConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/merchantConfigDetails.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Config Details" name="submit" class="button3">
                                    <%=Top_Merchant_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getMerchantCheckoutConfig()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CHECKOUT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/MerchantCheckoutConfig?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Checkout Config" name="submit" class="button3">
                                    <%=Checkout_Config%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsFraudRuleConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.FRAUD_RULE_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/ListMerchantFraudRule?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3">
                                    <%=Top_Fraud_Rule_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingWhiteListDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3">
                                    <%=Top_Whitelist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingBlacklistDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/blockCountry.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3">
                                    <%=Top_Blacklist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                    <%
                    }else if("Merchant Config Details".equals(buttonvalue))
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;color:#ffffff; margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-wrench iconstyle" style="float:left;"></i><span><%=Top_Settings%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: block;">
                        <% if("Y".equals(merchantModuleAccessVO.getSettingsMerchantProfileAccess()))
                        {
                            if(moduleSet.contains(MerchantModuleEnum.MERCHANT_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                            {
                        %>
                        <li>
                            <form action="/merchant/servlet/MerchantProfile?ctoken=<%=ctoken%>"  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Profile" name="submit" class="button3">
                                    <%=Top_Merchant_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsOrganisationProfileAccess()))
                            {
                                if (moduleSet.contains(MerchantModuleEnum.ORGANISATION_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/PopulateSpeedOption?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="speedoption" name="submit" class="button3">
                                    <%=Top_Organisation_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            /*if("Y".equals(merchantModuleAccessVO.getSettingsCheckoutPageAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CHECKOUT_PAGE.name()) || (!user.getRoles().contains("submerchant")))
                                {*/
                        %>
                        <%--<li>
                            <form action="/merchant/servlet/MerchantTemplate?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Transaction Pages" name="submit" class="button3">
                                    Checkout Page
                                </button>
                            </form>
                        </li>--%>
                        <%
                            /*}
                        }*/
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateviewaccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.VIEW_KEY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/generateview.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="View Key" name="submit" class="button3">
                                    View Key
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        }
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateKeyAccess()))
                            {
                                if(!user.getRoles().contains("submerchant"))
                                {
                        %>
                        <li >
                            <form action="/merchant/generatekey.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Generate Key" name="submit" class="button3">
                                    <%=Top_Generate_Key%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsMerchantConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/merchantConfigDetails.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Config Details" name="submit" class="button3" style="background-color:#d9d9d9">
                                    <%=Top_Merchant_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getMerchantCheckoutConfig()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CHECKOUT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/MerchantCheckoutConfig?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Checkout Config" name="submit" class="button3">
                                    <%=Checkout_Config%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsFraudRuleConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.FRAUD_RULE_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/ListMerchantFraudRule?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3">
                                    <%=Top_Fraud_Rule_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingWhiteListDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3">
                                    <%=Top_Whitelist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingBlacklistDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/blockCountry.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3">
                                    <%=Top_Blacklist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                    <%
                    }else if("Checkout Config".equals(buttonvalue))
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;color:#ffffff; margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-wrench iconstyle" style="float:left;"></i><span><%=Top_Settings%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: block;">
                        <% if("Y".equals(merchantModuleAccessVO.getSettingsMerchantProfileAccess()))
                        {
                            if(moduleSet.contains(MerchantModuleEnum.MERCHANT_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                            {
                        %>
                        <li>
                            <form action="/merchant/servlet/MerchantProfile?ctoken=<%=ctoken%>"  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Profile" name="submit" class="button3">
                                    <%=Top_Merchant_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsOrganisationProfileAccess()))
                            {
                                if (moduleSet.contains(MerchantModuleEnum.ORGANISATION_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/PopulateSpeedOption?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="speedoption" name="submit" class="button3">
                                    <%=Top_Organisation_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            /*if("Y".equals(merchantModuleAccessVO.getSettingsCheckoutPageAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CHECKOUT_PAGE.name()) || (!user.getRoles().contains("submerchant")))
                                {*/
                        %>
                        <%--<li>
                            <form action="/merchant/servlet/MerchantTemplate?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Transaction Pages" name="submit" class="button3">
                                    Checkout Page
                                </button>
                            </form>
                        </li>--%>
                        <%
                            /*}
                        }*/
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateviewaccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.VIEW_KEY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/generateview.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="View Key" name="submit" class="button3">
                                    View Key
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        }
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateKeyAccess()))
                            {
                                if(!user.getRoles().contains("submerchant"))
                                {
                        %>
                        <li >
                            <form action="/merchant/generatekey.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Generate Key" name="submit" class="button3">
                                    <%=Top_Generate_Key%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsMerchantConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/merchantConfigDetails.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Config Details" name="submit" class="button3" >
                                    <%=Top_Merchant_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getMerchantCheckoutConfig()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CHECKOUT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/MerchantCheckoutConfig?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Checkout Config" name="submit" class="button3" style="background-color:#d9d9d9">
                                    <%=Checkout_Config%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsFraudRuleConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.FRAUD_RULE_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/ListMerchantFraudRule?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3">
                                    <%=Top_Fraud_Rule_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingWhiteListDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3">
                                    <%=Top_Whitelist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingBlacklistDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/blockCountry.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3">
                                    <%=Top_Blacklist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                    <%
                    }else if("Fraud Rule Configuration".equals(buttonvalue))
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;color:#ffffff; margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-wrench iconstyle" style="float:left;"></i><span><%=Top_Settings%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block">
                        <% if("Y".equals(merchantModuleAccessVO.getSettingsMerchantProfileAccess()))
                        {
                            if(moduleSet.contains(MerchantModuleEnum.MERCHANT_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                            {
                        %>
                        <li>
                            <form action="/merchant/servlet/MerchantProfile?ctoken=<%=ctoken%>"  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Profile" name="submit" class="button3">
                                    <%=Top_Merchant_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsOrganisationProfileAccess()))
                            {
                                if (moduleSet.contains(MerchantModuleEnum.ORGANISATION_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/PopulateSpeedOption?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="speedoption" name="submit" class="button3">
                                    <%=Top_Organisation_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            /*if("Y".equals(merchantModuleAccessVO.getSettingsCheckoutPageAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CHECKOUT_PAGE.name()) || (!user.getRoles().contains("submerchant")))
                                {*/
                        %>
                        <%--<li>
                            <form action="/merchant/servlet/MerchantTemplate?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Transaction Pages" name="submit" class="button3">
                                    Checkout Page
                                </button>
                            </form>
                        </li>--%>
                        <%
                            /*}
                        }*/
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateviewaccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.VIEW_KEY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/generateview.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="View Key" name="submit" class="button3">
                                    View Key
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        }
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateKeyAccess()))
                            {
                                if(!user.getRoles().contains("submerchant"))
                                {
                        %>
                        <li >
                            <form action="/merchant/generatekey.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Generate Key" name="submit" class="button3">
                                    <%=Top_Generate_Key%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsMerchantConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/merchantConfigDetails.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Config Details" name="submit" class="button3">
                                    <%=Top_Merchant_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getMerchantCheckoutConfig()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CHECKOUT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/MerchantCheckoutConfig?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Checkout Config" name="submit" class="button3">
                                    <%=Checkout_Config%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsFraudRuleConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.FRAUD_RULE_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/ListMerchantFraudRule?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3" style="background-color:#d9d9d9">
                                    <%=Top_Fraud_Rule_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingWhiteListDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3">
                                    <%=Top_Whitelist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingBlacklistDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/blockCountry.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3">
                                    <%=Top_Blacklist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul><%
                }else if("White List".equals(buttonvalue))
                {
                %>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;color:#ffffff; margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-wrench iconstyle" style="float:left;"></i><span><%=Top_Settings%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block">
                        <% if("Y".equals(merchantModuleAccessVO.getSettingsMerchantProfileAccess()))
                        {
                            if(moduleSet.contains(MerchantModuleEnum.MERCHANT_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                            {
                        %>
                        <li>
                            <form action="/merchant/servlet/MerchantProfile?ctoken=<%=ctoken%>"  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Profile" name="submit" class="button3">
                                    <%=Top_Merchant_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsOrganisationProfileAccess()))
                            {
                                if (moduleSet.contains(MerchantModuleEnum.ORGANISATION_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/PopulateSpeedOption?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="speedoption" name="submit" class="button3">
                                    <%=Top_Organisation_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            /*if("Y".equals(merchantModuleAccessVO.getSettingsCheckoutPageAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CHECKOUT_PAGE.name()) || (!user.getRoles().contains("submerchant")))
                                {*/
                        %>
                        <%--<li>
                            <form action="/merchant/servlet/MerchantTemplate?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Transaction Pages" name="submit" class="button3">
                                    Checkout Page
                                </button>
                            </form>
                        </li>--%>
                        <%
                            /*}
                        }*/
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateviewaccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.VIEW_KEY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/generateview.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="View Key" name="submit" class="button3">
                                    View Key
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateKeyAccess()))
                            {
                                if(!user.getRoles().contains("submerchant"))
                                {
                        %>
                        <li >
                            <form action="/merchant/generatekey.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Generate Key" name="submit" class="button3">
                                    <%=Top_Generate_Key%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsMerchantConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/merchantConfigDetails.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Config Details" name="submit" class="button3">
                                    <%=Top_Merchant_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getMerchantCheckoutConfig()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CHECKOUT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/MerchantCheckoutConfig?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Checkout Config" name="submit" class="button3">
                                    <%=Checkout_Config%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsFraudRuleConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.FRAUD_RULE_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/ListMerchantFraudRule?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3">
                                    <%=Top_Fraud_Rule_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingWhiteListDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3" style="background-color:#d9d9d9">
                                    <%=Top_Whitelist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingBlacklistDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/blockCountry.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3">
                                    <%=Top_Blacklist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                    <%
                    }else if("Black List".equals(buttonvalue))
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;color:#ffffff; margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-wrench iconstyle" style="float:left;"></i><span><%=Top_Settings%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block">
                        <% if("Y".equals(merchantModuleAccessVO.getSettingsMerchantProfileAccess()))
                        {
                            if(moduleSet.contains(MerchantModuleEnum.MERCHANT_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                            {
                        %>
                        <li>
                            <form action="/merchant/servlet/MerchantProfile?ctoken=<%=ctoken%>"  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Profile" name="submit" class="button3">
                                    <%=Top_Merchant_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsOrganisationProfileAccess()))
                            {
                                if (moduleSet.contains(MerchantModuleEnum.ORGANISATION_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/PopulateSpeedOption?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="speedoption" name="submit" class="button3">
                                    <%=Top_Organisation_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            /*if("Y".equals(merchantModuleAccessVO.getSettingsCheckoutPageAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CHECKOUT_PAGE.name()) || (!user.getRoles().contains("submerchant")))
                                {*/
                        %>
                        <%--<li>
                            <form action="/merchant/servlet/MerchantTemplate?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Transaction Pages" name="submit" class="button3">
                                    Checkout Page
                                </button>
                            </form>
                        </li>--%>
                        <%
                            /* }
                         }*/
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateviewaccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.VIEW_KEY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/generateview.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="View Key" name="submit" class="button3">
                                    View Key
                                </button>
                            </form>
                        </li>
                        <%
                             }
                         }
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateKeyAccess()))
                            {
                                if(!user.getRoles().contains("submerchant"))
                                {
                        %>
                        <li >
                            <form action="/merchant/generatekey.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Generate Key" name="submit" class="button3">
                                    <%=Top_Generate_Key%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsMerchantConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/merchantConfigDetails.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Config Details" name="submit" class="button3">
                                    <%=Top_Merchant_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getMerchantCheckoutConfig()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CHECKOUT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/MerchantCheckoutConfig?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Checkout Config" name="submit" class="button3">
                                    <%=Checkout_Config%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsFraudRuleConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.FRAUD_RULE_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/ListMerchantFraudRule?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3" >
                                    <%=Top_Fraud_Rule_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingWhiteListDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3">
                                    <%=Top_Whitelist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingBlacklistDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/blockCountry.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3" style="background-color:#d9d9d9">
                                    <%=Top_Blacklist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                    <%
                    }
                    else{
                    %>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;;margin-left: 0px;text-align: left;" class="button1">
                        <i class="fa fa-wrench iconstyle" style="float:left;"></i><span><%=Top_Settings%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span><%--<span class="caret" style="float:right;"></span>--%>
                    </button>
                    <ul>
                        <% if("Y".equals(merchantModuleAccessVO.getSettingsMerchantProfileAccess()))
                        {
                            if(moduleSet.contains(MerchantModuleEnum.MERCHANT_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                            {
                        %>
                        <li>
                            <form action="/merchant/servlet/MerchantProfile?ctoken=<%=ctoken%>"  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Profile" name="submit" class="button3">
                                    <%=Top_Merchant_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsOrganisationProfileAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.ORGANISATION_PROFILE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/PopulateSpeedOption?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="speedoption" name="submit" class="button3">
                                    <%=Top_Organisation_Profile%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                           /* if("Y".equals(merchantModuleAccessVO.getSettingsCheckoutPageAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CHECKOUT_PAGE.name()) || (!user.getRoles().contains("submerchant")))
                                {*/
                        %>
                        <%
                            /*}
                        }*/
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateviewaccess()))
                            {
                                if(!user.getRoles().contains("submerchant"))
                                {
                        %>
                        <li >
                            <form action="/merchant/generateview.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="View Key" name="submit" class="button3">
                                   View Key
                                </button>
                            </form>
                        </li>
                        <%-- <li>
                             <form action="/merchant/servlet/MerchantTemplate?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="Transaction Pages" name="submit" class="button3">
                                     Checkout Page
                                 </button>
                             </form>
                         </li>--%>
                        <%
                            }
                        }
                            if("Y".equals(merchantModuleAccessVO.getSettingsGenerateKeyAccess()))
                            {
                                if(!user.getRoles().contains("submerchant"))
                                {
                        %>
                        <li >
                            <form action="/merchant/generatekey.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Generate Key" name="submit" class="button3">
                                    <%=Top_Generate_Key%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsMerchantConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/merchantConfigDetails.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Config Details" name="submit" class="button3">
                                    <%=Top_Merchant_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getMerchantCheckoutConfig()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.MERCHANT_CHECKOUT_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/MerchantCheckoutConfig?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Checkout Config" name="submit" class="button3">
                                    <%=Checkout_Config%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsFraudRuleConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.FRAUD_RULE_CONFIG.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/servlet/ListMerchantFraudRule?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3">
                                    <%=Top_Fraud_Rule_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingWhiteListDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3">
                                    <%=Top_Whitelist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingBlacklistDetails()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li >
                            <form action="/merchant/blockCountry.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3">
                                    <%=Top_Blacklist_Details%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                    <%
                            }
                        }%>
                </li>
                <%
                        }
                    }
                    if("Y".equals(merchantModuleAccessVO.getTransactionsAccess()))
                    {
                        if(moduleSet.contains(MerchantModuleEnum.TRANSACTION_MANAGEMENT.name()) || (!user.getRoles().contains("submerchant")))
                        {
                %>
                <li>
                    <% if(buttonvalue==null){%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;margin-bottom: 0px;" class="button1">
                        <i class="fa fa-dashboard iconstyle" style="float:left;"></i><span><%=Top_Transaction_Management%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: none;">
                        <%
                            if("Y".equals(merchantModuleAccessVO.getTransMgtTransactionAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.TRANSACTIONS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/transactions.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Transactions" name="submit" class="button3">
                                    <%=Top_Transactions%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getTransMgtCaptureAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CAPTURE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/pod.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Capture" name="submit" class="button3">
                                    <%=Top_Capture%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getTransMgtReversalAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.REVERSAL.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/reverselist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Reversal" name="submit" class="button3">
                                    <%=Top_Reversal%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if(moduleSet.contains(MerchantModuleEnum.PAYOUT.name()) || (!user.getRoles().contains("submerchant")))
                            {
                                if("Y".equals(merchantModuleAccessVO.getTransMgtPayoutAccess()))
                                {
                        %>
                        <li>
                            <form action="/merchant/payoutlist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Payout" name="submit" class="button3">
                                    <%=Top_Payout%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if(moduleSet.contains(MerchantModuleEnum.PAYOUT_TRANSACTIONS.name()) || (!user.getRoles().contains("submerchant")))
                            {
                                if("Y".equals(merchantModuleAccessVO.getTransMgtPayoutTransaction()))
                                {
                        %>
                        <li>
                            <form action="/merchant/payoutTransactionList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="PayoutTransaction" name="submit" class="button3">
                                    <%=Top_PayoutTransaction%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>

                    </ul><%}else{if("Transactions".equals(buttonvalue)){%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;color:#ffffff; margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-dashboard iconstyle" style="float:left;"></i><span><%=Top_Transaction_Management%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: block;">
                        <%
                            if("Y".equals(merchantModuleAccessVO.getTransMgtTransactionAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.TRANSACTIONS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/transactions.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Transactions" name="submit" class="button3" style="background-color:#d9d9d9" >
                                    <%=Top_Transactions%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getTransMgtCaptureAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CAPTURE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/pod.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Capture" name="submit" class="button3">
                                    <%=Top_Capture%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getTransMgtReversalAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.REVERSAL.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/reverselist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Reversal" name="submit" class="button3">
                                    <%=Top_Reversal%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if(moduleSet.contains(MerchantModuleEnum.PAYOUT.name()) || (!user.getRoles().contains("submerchant")))
                            {
                                if("Y".equals(merchantModuleAccessVO.getTransMgtPayoutAccess()))
                                {
                        %>
                        <li>
                            <form action="/merchant/payoutlist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Payout" name="submit" class="button3">
                                    <%=Top_Payout%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if(moduleSet.contains(MerchantModuleEnum.PAYOUT_TRANSACTIONS.name()) || (!user.getRoles().contains("submerchant")))
                            {
                                if("Y".equals(merchantModuleAccessVO.getTransMgtPayoutTransaction()))
                                {
                        %>
                        <li>
                            <form action="/merchant/payoutTransactionList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="PayoutTransaction" name="submit" class="button3">
                                    <%=Top_PayoutTransaction%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                    <%}else if("Reversal".equals(buttonvalue)){%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;color:#ffffff; margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-dashboard iconstyle" style="float:left;"></i><span><%=Top_Transaction_Management%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: block;">
                        <%
                            if("Y".equals(merchantModuleAccessVO.getTransMgtTransactionAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.TRANSACTIONS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/transactions.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Transactions" name="submit" class="button3">
                                    <%=Top_Transactions%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getTransMgtCaptureAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CAPTURE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/pod.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Capture" name="submit" class="button3">
                                    <%=Top_Capture%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getTransMgtReversalAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.REVERSAL.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/reverselist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Reversal" name="submit" class="button3" style="background-color:#d9d9d9">
                                    <%=Top_Reversal%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getTransMgtPayoutAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.PAYOUT.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/payoutlist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Payout" name="submit" class="button3">
                                    <%=Top_Payout%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if(moduleSet.contains(MerchantModuleEnum.PAYOUT_TRANSACTIONS.name()) || (!user.getRoles().contains("submerchant")))
                            {
                                if("Y".equals(merchantModuleAccessVO.getTransMgtPayoutTransaction()))
                                {
                        %>
                        <li>
                            <form action="/merchant/payoutTransactionList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="PayoutTransaction" name="submit" class="button3">
                                    <%=Top_PayoutTransaction%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                    <%}else if("Payout".equals(buttonvalue)){%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;color:#ffffff; margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-dashboard iconstyle" style="float:left;"></i><span><%=Top_Transaction_Management%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: block;">
                        <%
                            if("Y".equals(merchantModuleAccessVO.getTransMgtTransactionAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.TRANSACTIONS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/transactions.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Transactions" name="submit" class="button3">
                                    <%=Top_Transactions%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getTransMgtCaptureAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CAPTURE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/pod.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Capture" name="submit" class="button3">
                                    <%=Top_Capture%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getTransMgtReversalAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.REVERSAL.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/reverselist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Reversal" name="submit" class="button3">
                                    <%=Top_Reversal%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getTransMgtPayoutAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.PAYOUT.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/payoutlist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Payout" name="submit" class="button3" style="background-color:#d9d9d9">
                                    <%=Top_Payout%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if(moduleSet.contains(MerchantModuleEnum.PAYOUT_TRANSACTIONS.name()) || (!user.getRoles().contains("submerchant")))
                            {
                                if("Y".equals(merchantModuleAccessVO.getTransMgtPayoutTransaction()))
                                {
                        %>
                        <li>
                            <form action="/merchant/payoutTransactionList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="PayoutTransaction" name="submit" class="button3">
                                    <%=Top_PayoutTransaction%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                    <%}else if("Capture".equals(buttonvalue)){%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;color:#ffffff; margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-dashboard iconstyle" style="float:left;"></i><span><%=Top_Transaction_Management%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: block;">
                        <%
                            if("Y".equals(merchantModuleAccessVO.getTransMgtTransactionAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.TRANSACTIONS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/transactions.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Transactions" name="submit" class="button3">
                                    <%=Top_Transactions%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getTransMgtCaptureAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CAPTURE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/pod.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Capture" name="submit" class="button3" style="background-color:#d9d9d9">
                                    <%=Top_Capture%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getTransMgtReversalAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.REVERSAL.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/reverselist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Reversal" name="submit" class="button3">
                                    <%=Top_Reversal%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getTransMgtPayoutAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.PAYOUT.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/payoutlist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Payout" name="submit" class="button3">
                                    <%=Top_Payout%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if(moduleSet.contains(MerchantModuleEnum.PAYOUT_TRANSACTIONS.name()) || (!user.getRoles().contains("submerchant")))
                            {
                                if("Y".equals(merchantModuleAccessVO.getTransMgtPayoutTransaction()))
                                {
                        %>
                        <li>
                            <form action="/merchant/payoutTransactionList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="PayoutTransaction" name="submit" class="button3">
                                    <%=Top_PayoutTransaction%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                    <%}else{%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;" class="button1">
                        <i class="fa fa-dashboard iconstyle" style="float:left;"></i>
                        <span><%=Top_Transaction_Management%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                        <%--<span class="caret" style="float:right;"></span>--%>
                    </button>
                    <ul>
                        <%
                            if("Y".equals(merchantModuleAccessVO.getTransMgtTransactionAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.TRANSACTIONS.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/transactions.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Transactions" name="submit" class="button3">
                                    <%=Top_Transactions%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getTransMgtCaptureAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.CAPTURE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/pod.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Capture" name="submit" class="button3">
                                    <%=Top_Capture%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getTransMgtReversalAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.REVERSAL.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/reverselist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Reversal" name="submit" class="button3">
                                    <%=Top_Reversal%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if(moduleSet.contains(MerchantModuleEnum.PAYOUT.name()) || (!user.getRoles().contains("submerchant")))
                            {
                                if("Y".equals(merchantModuleAccessVO.getTransMgtPayoutAccess()))
                                {
                        %>
                        <li>
                            <form action="/merchant/payoutlist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Payout" name="submit" class="button3">
                                    <%=Top_Payout%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if(moduleSet.contains(MerchantModuleEnum.PAYOUT_TRANSACTIONS.name()) || (!user.getRoles().contains("submerchant")))
                            {
                                if("Y".equals(merchantModuleAccessVO.getTransMgtPayoutTransaction()))
                                {
                        %>
                        <li>
                            <form action="/merchant/payoutTransactionList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="PayoutTransaction" name="submit" class="button3">
                                    <%=Top_PayoutTransaction%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul><%}}%>
                </li>

                <%
                        }
                    }
                    if("Y".equals(merchantModuleAccessVO.getRejectedTransaction()))
                    {
                        if(moduleSet.contains(MerchantModuleEnum.REJECTED_TRANSACTION.name()) || (!user.getRoles().contains("submerchant")))
                        {
                %>
                <form action="/merchant/rejectedTransactionsList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                    <li >
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="RejectedTransaction" name="submit" class="button1"
                                <%
                                    if(buttonvalue!=null)
                                    {
                                        if(buttonvalue.equals("RejectedTransaction"))
                                        {   %>
                                style="color:#ffffff;" id="active_menu"
                                <%}
                                }
                                %>><i class="fa fa-credit-card iconstyle" style="float:left;"></i><span> <%=Top_Rejected_Transaction%> </span>
                        </button>
                    </li>
                </form>

                <%
                        }
                    }
                    if("Y".equals(merchantModuleAccessVO.getInvoicingAccess())){
                        if(moduleSet.contains(MerchantModuleEnum.INVOICE.name()) || (!user.getRoles().contains("submerchant")) )
                        {
                %>
                <li<%--style="background-color:#ecf0f1; border-right-color:#34495E;margin-top:-11px;"--%>>
                    <% if(buttonvalue==null){%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;" class="button1">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span><%=Top_Invoice%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span><%--<span class="caret" style="float:right;"></span>--%>
                    </button>
                    <ul>
                        <%
                            if("Y".equals(merchantModuleAccessVO.getInvoiceGenerateAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.GENERATE_INVOICE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/GenerateInvoice?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Generate Invoice" name="submit" class="button3">
                                    <%=Top_Generate_Invoice%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getInvoiceHistoryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.INVOICE_HISTORY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/invoice.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="New_Invoice" name="submit" class="button3">
                                    <%=Top_Invoice_History%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsInvoiceConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.INVOICE_CONFIGURATION.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/InvoiceConfiguration?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Invoice Configuration" name="submit" class="button3">
                                    <%=Top_Invoice_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul><%}else{if("Generate Invoice".equals(buttonvalue)){%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;color:#ffffff; margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span><%=Top_Invoice%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span><%--<span class="caret" style="float:right;"></span>--%>
                    </button>
                    <ul style="display: block">
                        <%
                            if("Y".equals(merchantModuleAccessVO.getInvoiceGenerateAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.GENERATE_INVOICE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/GenerateInvoice?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Generate Invoice" name="submit" class="button3" style="background-color:#d9d9d9">
                                    <%=Top_Generate_Invoice%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getInvoiceHistoryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.INVOICE_HISTORY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/invoice.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="New_Invoice" name="submit" class="button3">
                                    <%=Top_Invoice_History%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsInvoiceConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.INVOICE_CONFIGURATION.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/InvoiceConfiguration?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Invoice Configuration" name="submit" class="button3">
                                    <%=Top_Invoice_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>

                    </ul><%}else{if("New_Invoice".equals(buttonvalue)){%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;color:#ffffff; margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span><%=Top_Invoice%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span><%--<span class="caret" style="float:right;"></span>--%>
                    </button>
                    <ul style="display: block">
                        <%
                            if("Y".equals(merchantModuleAccessVO.getInvoiceGenerateAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.GENERATE_INVOICE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/GenerateInvoice?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Generate Invoice" name="submit" class="button3">
                                    <%=Top_Generate_Invoice%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getInvoiceHistoryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.INVOICE_HISTORY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/invoice.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="New_Invoice" name="submit" class="button3" style="background-color:#d9d9d9">
                                    <%=Top_Invoice_History%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsInvoiceConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.INVOICE_CONFIGURATION.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/InvoiceConfiguration?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Invoice Configuration" name="submit" class="button3" >
                                    <%=Top_Invoice_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>


                    <%}else{if("Invoice Configuration".equals(buttonvalue)){%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;text-indent: 13px;font-size: 12px;color:#ffffff; margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span><%=Top_Invoice%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: block">
                        <%
                            if("Y".equals(merchantModuleAccessVO.getInvoiceGenerateAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.GENERATE_INVOICE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/GenerateInvoice?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Generate Invoice" name="submit" class="button3">
                                    <%=Top_Generate_Invoice%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getInvoiceHistoryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.INVOICE_HISTORY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/invoice.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="New_Invoice" name="submit" class="button3">
                                    <%=Top_Invoice_History%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsInvoiceConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.INVOICE_CONFIGURATION.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/InvoiceConfiguration?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Invoice Configuration" name="submit" class="button3" style="background-color:#d9d9d9">
                                    <%=Top_Invoice_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul><%}else{%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;" class="button1">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span><%=Top_Invoice%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul>
                        <%
                            if("Y".equals(merchantModuleAccessVO.getInvoiceGenerateAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.GENERATE_INVOICE.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/GenerateInvoice?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Generate Invoice" name="submit" class="button3">
                                    <%=Top_Generate_Invoice%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getInvoiceHistoryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.INVOICE_HISTORY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/invoice.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="New_Invoice" name="submit" class="button3">
                                    <%=Top_Invoice_History%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getSettingsInvoiceConfigAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.INVOICE_CONFIGURATION.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/servlet/InvoiceConfiguration?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Invoice Configuration" name="submit" class="button3">
                                    <%=Top_Invoice_Configuration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul><%}}}%>
                </li>
                <%
                            }
                        }
                    }
                    if("Y".equals(merchantModuleAccessVO.getPayByLink()))
                    {
                        if(moduleSet.contains(MerchantModuleEnum.PAYBYLINK.name()) || (!user.getRoles().contains("submerchant")))
                        {
                %>
                <li <%--style="background-color:#ecf0f1; border-right-color:#34495E;"--%>>

                    <%-- <% if(buttonvalue==null){%>--%>
                    <button onclick="window.location.href='#'" id="clicker"  style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;" class="button1" <%if(buttonvalue!=null && buttonvalue.contains("Terminal")){%>style="background-color: #7eccad !important"<%}%>>
                        <i class="fa fa-desktop iconstyle" style="float:left;"></i><span>Pay By Link</span><span class="pull-right"><i class="fa fa-caret-down"></i></span><%--<span class="caret" style="float:right;"></span>--%>

                    </button>
                    <ul <%if(buttonvalue!=null && buttonvalue.contains("Terminal")){%>style="display: block;"<%}%>>

                        <%
                            HashMap terminalList1= (HashMap) session.getAttribute("paybylinkterminallist");
                            String countryCodePage=(String)session.getAttribute("countrycodepage");
                            if(terminalList1!=null)
                            {
                                Iterator it = terminalList1.entrySet().iterator();
                                while (it.hasNext()) {
                                    Map.Entry pair = (Map.Entry)it.next();
                                    MerchantTerminalVo merchantTerminalVo= (MerchantTerminalVo) pair.getValue();
                                    String accountid=merchantTerminalVo.getAccountId();
                                    String cardtypeid=merchantTerminalVo.getCardType();
                                    String paymodeId=merchantTerminalVo.getPaymodeId();
                                    String cardtypeName=merchantTerminalVo.getCardTypeName();
                                    String paymodeName=merchantTerminalVo.getPaymodeName();
                                    String fileName=merchantTerminalVo.getFileName();
                                    String cCPaymentPage=merchantTerminalVo.getcCPaymentFileName();
                                    String isRecurring = merchantTerminalVo.getIsRecurring();
                                    String addressDetails = merchantTerminalVo.getAddressDetails();
                                    String addressValidation = merchantTerminalVo.getAddressValidation();
                                    String terminalCurrency = merchantTerminalVo.getCurrency();
                                    String isActive =merchantTerminalVo.getIsActive();
                                    String conf="";
                                    String title="";
                                    if("N".equals(isActive))
                                    {
                                        conf="disabled";
                                        title=Top_Terminal + "-"+pair.getKey()+" has been suspended,please contact support team.";

                                    }
                        %>
                        <li style="margin-bottom:0px" title="<%=title%>">
                            <form action="/merchant/paybylink.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" method="post">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <input type="hidden" value="<%=pair.getKey()%>" name="terminalid">
                                <input type="hidden" value="<%=accountid%>" name="accountid">
                                <input type="hidden" value="<%=cardtypeid%>" name="cardtype">
                                <input type="hidden" value="<%=paymodeId%>" name="paymodeid">
                                <input type="hidden" value="<%=cardtypeName%>" name="cardtypename">
                                <input type="hidden" value="<%=paymodeName%>" name="paymodename">
                                <input type="hidden" value="<%=fileName%>" name="filename">
                                <input type="hidden" value="<%=cCPaymentPage%>" name="ccpage">
                                <input type="hidden" value="<%=isRecurring%>" name="isrecurring">
                                <input type="hidden" value="<%=countryCodePage%>" name="countrycodepage">
                                <input type="hidden" value="<%=addressDetails%>" name="addressDetails">
                                <input type="hidden" value="<%=addressValidation%>" name="addressValidation">
                                <input type="hidden" value="<%=terminalCurrency%>" name="currency">
                                <input type="hidden" value="<%=session.getAttribute("multiCurrencySupport")%>" name="multiCurrencySupport">
                                <%
                                    if("N".equals(isActive) || (terminalCurrency.equalsIgnoreCase("ALL") && session.getAttribute("multiCurrencySupport").equals("N")))
                                    {%>

                                <button type="submit" value="Terminal<%=pair.getKey()%>"  style="background-color:#d9d9d9; pointer-events: none; cursor: not-allowed;" name="submit" class="button3" style="text-indent: 7%; font-size: 11px;" <%if(buttonvalue!=null && ("Terminal"+pair.getKey()).equals(buttonvalue)){%><%}%> <%=conf%>>

                                    <font color="#a9a9a9" > <strike> <i class="fa fa-angle-desktop"  style="float: left"></i><%=Top_Terminal%>-<%=pair.getKey()%>-<%=paymodeName%>-<%=cardtypeName%>-<%=terminalCurrency%></strike></font>

                                </button>

                                <%}
                                else
                                {%>

                                <button type="submit" value="Terminal<%=pair.getKey()%>" name="submit" style="text-indent: 7%;font-size: 11px;" class="button3" <%if(buttonvalue!=null && ("Terminal"+pair.getKey()).equals(buttonvalue)){%> style="background-color:#d9d9d9;"<%}%> <%=conf%>>
                                    <i class="fa fa-angle-desktop" style="float:left;"></i><%=Top_Terminal%>-<%=pair.getKey()%>-<%=paymodeName%>-<%=cardtypeName%>-<%=terminalCurrency%>
                                </button>
                                <%}
                                %>

                            </form>
                        </li>
                        <%     }
                        }
                        %>
                    </ul>
                </li>

                <%
                        }
                    }
                %>
                <%-- EMI Configuration--%>
                <%--                <%
                                    if("Y".equals(mercha6ntModuleAccessVO.getEmiConfiguration()))
                                    {
                                        if(moduleSet.contains(MerchantModuleEnum.EMI_CONFIGURATION.name()) || (!user.getRoles().contains("submerchant")))
                                        {
                                %>
                                <form action="/merchant/servlet/EmiConfig?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                                    <li >
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <button type="submit" value="EMI Configuration" name="submit" class="button1"
                                                <%
                                                    if(buttonvalue!=null)
                                                    {
                                                        if(buttonvalue.equals("EMI Configuration"))
                                                        {   %>
                                                style="color:#ffffff;" id="active_menu"
                                                <%}
                                                }
                                                %>><i class="fa fa-refresh" style="float:left;"></i><span>EMI Configuration</span>
                                        </button>
                                    </li>
                                </form>
                                <%
                                        }
                                    }
                                %>--%>

                <%-- if(moduleSet.contains(MerchantModuleEnum.TOKEN_MANAGEMENT.name()) || (!user.getRoles().contains("submerchant")))
                 {
                     if( "Y".equals(merchantModuleAccessVO.getTokenizationAccess()) && "Y".equals(session.getAttribute("is_rest_whitelisted")))
                     {%>

             <li>
                 <% if(buttonvalue==null)
                 {%>
                 <ul>
                     <li>
                         <form action="/merchant/listOfMerchantRegistrations.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                             <input type="hidden" value="<%=ctoken%>" name="ctoken">
                             <button type="submit" value="Registration History" name="submit" class="button3">
                                 Registration History
                             </button>
                         </form>
                     </li>
                 </ul>
                 <%}else {if("Registration History".equals(buttonvalue))
                 {%>
                 <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #ffffff;text-indent: 13px;font-size: 12px;background-color: #7eccad !importantmargin-left: 0px;text-align: left;" class="button1 active">
                     <i class="fa fa-bitbucket iconstyle" style="float:left;"></i><span><%=Top_Token_Management%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                 </button>
                 <ul style="display: block;">
                     <li>
                         <form action="/merchant/listOfMerchantRegistrations.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                             <input type="hidden" value="<%=ctoken%>" name="ctoken">
                             <button type="submit" value="Registration History" name="submit" class="button3" style="background-color:#d9d9d9 ">
                                 Registration History
                             </button>
                         </form>
                     </li>

                 </ul>
                 <%}else {if("Create Token".equals(buttonvalue))
                 {%>
                 <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #ffffff;text-indent: 13px;font-size: 12px;background-color: #7eccad !importantmargin-left: 0px;text-align: left;" class="button1 active">
                     <i class="fa fa-bitbucket iconstyle" style="float:left;"></i><span><%=Top_Token_Management%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                 </button>
                 <ul style="display: block;">
                     <li>
                         <form action="/merchant/listOfMerchantRegistrations.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                             <input type="hidden" value="<%=ctoken%>" name="ctoken">
                             <button type="submit" value="Registration History" name="submit" class="button3">
                                 Registration History
                             </button>
                         </form>
                     </li>

                 </ul><%}else{%>
                 <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;" class="button1">
                     <i class="fa fa-paste iconstyle" style="float:left;"></i><span><%=Top_Token_Management%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                 </button>
                 <ul>

                     <li>
                         <form action="/merchant/listOfMerchantRegistrations.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                             <input type="hidden" value="<%=ctoken%>" name="ctoken">
                             <button type="submit" value="Registration History" name="submit" class="button3">
                                 Registration History
                             </button>
                         </form>
                     </li>

                 </ul><%}}}%>
             </li>
             <%
             } }--%>
                <%
                    if("Y".equals(merchantModuleAccessVO.getTokenizationAccess())){
                        if(moduleSet.contains(MerchantModuleEnum.TOKEN_MANAGEMENT.name()) || (!user.getRoles().contains("submerchant"))){
                %>
                <li>
                    <% if(buttonvalue==null)
                    {%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;"  class="button1">
                        <i class="fa fa-bitbucket iconstyle" style="float:left;"></i><span><%=Top_Token_Management%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span><%--<span class="caret" style="float:right;"></span>--%>
                    </button>
                    <ul>
                        <%
                            if("Y".equals(merchantModuleAccessVO.getTokenMgtRegistrationHistoryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.REGISTRATION_HISTORY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/listmerchantregistercard.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Registration History" name="submit" class="button3">
                                    <%=Top_Registration_History%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getTokenMgtRegisterCardAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.REGISTER_CARD.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/newcardregistration.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Register Card" name="submit" class="button3">
                                    <%=Top_Card_Registration%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                    <%
                    }
                    else
                    {
                        if ("Registration History".equals(buttonvalue))
                        {%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #ffffff;text-indent: 13px;font-size: 12px;background-color: #7eccad !importantmargin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-bitbucket iconstyle" style="float:left;"></i><span><%=Top_Token_Management%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: block;">
                        <%
                            if("Y".equals(merchantModuleAccessVO.getTokenMgtRegistrationHistoryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.REGISTRATION_HISTORY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/listmerchantregistercard.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Registration History" name="submit" class="button3" style="background-color:#d9d9d9 ">
                                    <%=Top_Registration_History%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getTokenMgtRegisterCardAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.REGISTER_CARD.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/newcardregistration.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Register Card" name="submit" class="button3">
                                    <%=Top_Register_Card%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul><%
                }
                else{if("Register Card".equals(buttonvalue)){%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #ffffff;text-indent: 13px;font-size: 12px;background-color: #7eccad !importantmargin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-bitbucket iconstyle" style="float:left;"></i><span><%=Top_Token_Management%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>

                    </button>
                    <ul style="display: block">
                        <%
                            if("Y".equals(merchantModuleAccessVO.getTokenMgtRegistrationHistoryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.REGISTRATION_HISTORY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/listmerchantregistercard.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Registration History" name="submit" class="button3">
                                    <%=Top_Registration_History%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getTokenMgtRegisterCardAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.REGISTER_CARD.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/newcardregistration.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Register Card" name="submit" class="button3" style="background-color:#d9d9d9 ">
                                    <%=Top_Register_Card%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul><%
                }else
                {%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;" class="button1">
                        <i class="fa fa-bitbucket iconstyle" style="float:left;"></i><span><%=Top_Token_Management%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul>
                        <%
                            if("Y".equals(merchantModuleAccessVO.getTokenMgtRegistrationHistoryAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.REGISTRATION_HISTORY.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/listmerchantregistercard.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Registration History" name="submit" class="button3">
                                    <%=Top_Registration_History%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                            if("Y".equals(merchantModuleAccessVO.getTokenMgtRegisterCardAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.REGISTER_CARD.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/newcardregistration.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Register Card" name="submit" class="button3">
                                    <%=Top_Register_Card%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul><%}}}%>
                </li>
                <%
                        }
                    }
                    if("Y".equals(merchantModuleAccessVO.getVirtualTerminalAccess())){
                        if(moduleSet.contains(MerchantModuleEnum.VIRTUAL_TERMINAL.name()) || (!user.getRoles().contains("submerchant")) )
                        {
                %>
                <li <%--style="background-color:#ecf0f1; border-right-color:#34495E;"--%>>

                    <%-- <% if(buttonvalue==null){%>--%>
                    <button onclick="window.location.href='#'" id="clicker"  style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;" class="button1" <%if(buttonvalue!=null && buttonvalue.contains("Terminal")){%>style="background-color: #7eccad !important"<%}%>>
                        <i class="fa fa-desktop iconstyle" style="float:left;"></i><span><%=Top_Virtual_Terminal%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span><%--<span class="caret" style="float:right;"></span>--%>

                    </button>
                    <ul <%if(buttonvalue!=null && buttonvalue.contains("Terminal")){%>style="display: block;"<%}%>>

                        <%
                            HashMap terminalList= (HashMap) session.getAttribute("terminallist");
                            String countryCodePage=(String)session.getAttribute("countrycodepage");
                            if(terminalList!=null)
                            {
                                Iterator it = terminalList.entrySet().iterator();
                                while (it.hasNext()) {
                                    Map.Entry pair = (Map.Entry)it.next();
                                    MerchantTerminalVo merchantTerminalVo= (MerchantTerminalVo) pair.getValue();
                                    String accountid=merchantTerminalVo.getAccountId();
                                    String cardtypeid=merchantTerminalVo.getCardType();
                                    String paymodeId=merchantTerminalVo.getPaymodeId();
                                    String cardtypeName=merchantTerminalVo.getCardTypeName();
                                    String paymodeName=merchantTerminalVo.getPaymodeName();
                                    String fileName=merchantTerminalVo.getFileName();
                                    String cCPaymentPage=merchantTerminalVo.getcCPaymentFileName();
                                    String isRecurring = merchantTerminalVo.getIsRecurring();
                                    String addressDetails = merchantTerminalVo.getAddressDetails();
                                    String addressValidation = merchantTerminalVo.getAddressValidation();
                                    String terminalCurrency = merchantTerminalVo.getCurrency();
                                    String isActive =merchantTerminalVo.getIsActive();
                                    String conf="";
                                    String title="";
                                    if("N".equals(isActive))
                                    {
                                        conf="disabled";
                                        title=Top_Terminal + "-"+pair.getKey()+" has been suspended,please contact support team.";

                                    }
                        %>
                        <li style="margin-bottom:0px" title="<%=title%>">
                            <form action="/merchant/virtualSingleCall.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px" method="post">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <input type="hidden" value="<%=pair.getKey()%>" name="terminalid">
                                <input type="hidden" value="<%=accountid%>" name="accountid">
                                <input type="hidden" value="<%=cardtypeid%>" name="cardtype">
                                <input type="hidden" value="<%=paymodeId%>" name="paymodeid">
                                <input type="hidden" value="<%=cardtypeName%>" name="cardtypename">
                                <input type="hidden" value="<%=paymodeName%>" name="paymodename">
                                <input type="hidden" value="<%=fileName%>" name="filename">
                                <input type="hidden" value="<%=cCPaymentPage%>" name="ccpage">
                                <input type="hidden" value="<%=isRecurring%>" name="isrecurring">
                                <input type="hidden" value="<%=countryCodePage%>" name="countrycodepage">
                                <input type="hidden" value="<%=addressDetails%>" name="addressDetails">
                                <input type="hidden" value="<%=addressValidation%>" name="addressValidation">
                                <input type="hidden" value="<%=terminalCurrency%>" name="currency">
                                <input type="hidden" value="<%=session.getAttribute("multiCurrencySupport")%>" name="multiCurrencySupport">
                                <%
                                    if("N".equals(isActive) || (terminalCurrency.equalsIgnoreCase("ALL") && session.getAttribute("multiCurrencySupport").equals("N")))
                                    {%>

                                <button type="submit" value="Terminal<%=pair.getKey()%>"  style="background-color:#d9d9d9; pointer-events: none; cursor: not-allowed;" name="submit" class="button3" style="text-indent: 7%; font-size: 11px;" <%if(buttonvalue!=null && ("Terminal"+pair.getKey()).equals(buttonvalue)){%><%}%> <%=conf%>>

                                    <font color="#a9a9a9" > <strike> <i class="fa fa-angle-desktop"  style="float: left"></i><%=Top_Terminal%>-<%=pair.getKey()%>-<%=paymodeName%>-<%=cardtypeName%>-<%=terminalCurrency%></strike></font>

                                </button>

                                <%}
                                else
                                {%>

                                <button type="submit" value="Terminal<%=pair.getKey()%>" name="submit" style="text-indent: 7%;font-size: 11px;" class="button3" <%if(buttonvalue!=null && ("Terminal"+pair.getKey()).equals(buttonvalue)){%> style="background-color:#d9d9d9;"<%}%> <%=conf%>>
                                    <i class="fa fa-angle-desktop" style="float:left;"></i><%=Top_Terminal%>-<%=pair.getKey()%>-<%=paymodeName%>-<%=cardtypeName%>-<%=terminalCurrency%>
                                </button>
                                <%}
                                %>

                            </form>
                        </li>
                        <%     }
                        }
                        %>
                    </ul>
                </li>

                <%
                        }
                    }
                    if("Y".equals(merchantModuleAccessVO.getVirtualCheckOut()))
                    {
                        if(moduleSet.contains(MerchantModuleEnum.VIRTUAL_CHECKOUT.name()) || (!user.getRoles().contains("submerchant")))
                        {
                %>
                <form action="/merchant/servlet/VirtualCheckOut?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                    <li >
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="virtualCheckOut" name="submit" class="button1"
                                <%
                                    if(buttonvalue!=null)
                                    {
                                        if(buttonvalue.equals("virtualCheckOut"))
                                        {   %>
                                style="color:#ffffff;" id="active_menu"
                                <%}
                                }
                                %>><i class="fa fa-credit-card iconstyle" style="float:left;"></i><span> <%=Top_Virtual_Checkout%> </span>
                        </button>
                    </li>
                </form>

                <%
                        }
                    }
                    if("Y".equals(merchantModuleAccessVO.getMerchantMgtAccess()))
                    {
                        if(moduleSet.contains(MerchantModuleEnum.MERCHANT_MANAGEMENT.name()) || (!user.getRoles().contains("submerchant")))
                        {
                %>
                <li>
                    <% if(buttonvalue==null){%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;"  class="button1">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span><%=Top_Merchant_Management%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span><%--<span class="caret" style="float:right;"></span>--%>
                    </button>
                    <ul>
                        <%
                            if("Y".equals(merchantModuleAccessVO.getMerchantMgtUserManagementAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.USER_MANAGEMENT.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/memberChildList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken" >
                                <button type="submit" value="User Management" name="submit" class="button3">
                                    <%=Top_User_Management%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul><%}else{if("User Management".equals(buttonvalue)){%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #ffffff;text-indent: 13px;font-size: 12px;background-color: #7eccad !importantmargin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span><%=Top_Merchant_Management%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: block;">
                        <%
                            if("Y".equals(merchantModuleAccessVO.getMerchantMgtUserManagementAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.USER_MANAGEMENT.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/memberChildList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="User Management" name="submit" class="button3" style="background-color:#d9d9d9 ">
                                    <%=Top_User_Management%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                    <%}else{%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;" class="button1">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span><%=Top_Merchant_Management%></span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul>
                        <%
                            if("Y".equals(merchantModuleAccessVO.getMerchantMgtUserManagementAccess()))
                            {
                                if(moduleSet.contains(MerchantModuleEnum.USER_MANAGEMENT.name()) || (!user.getRoles().contains("submerchant")))
                                {
                        %>
                        <li>
                            <form action="/merchant/memberChildList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="User Management" name="submit" class="button3" >
                                    <%=Top_User_Management%>
                                </button>
                            </form>
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul><%}}%>
                </li>
                <%
                        }
                    }
                    if("Y".equals(merchantModuleAccessVO.getApplicationManagerAccess()))
                    {
                        if(moduleSet.contains(MerchantModuleEnum.MERCHANT_APPLICATION.name()) || (!user.getRoles().contains("submerchant")))
                        {
                %>
                <form action="/merchant/servlet/ApplicationManagerMerchant?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                    <li>
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="ApplicationManagerMerchant" name="submit" class="button1"
                                <%
                                    if(buttonvalue!=null)
                                    {
                                        if(buttonvalue.equals("ApplicationManagerMerchant"))
                                        {
                                %>
                                style="color: #ffffff" id="active_menu"
                                <%
                                        }
                                    }
                                %>>
                            <i class="fa fa-trello" style="float:left;"></i><span><%=Top_Merchant_Application%></span>
                        </button>
                        <input type="hidden" value='<%=copyiframe%>' name="copyiframe">
                    </li>
                </form>
                <%
                        }
                    }
                    if("Y".equals(merchantModuleAccessVO.getRecurringAccess()))
                    {
                        if(moduleSet.contains(MerchantModuleEnum.RECURRING_MODULE.name()) || (!user.getRoles().contains("submerchant")))
                        {
                %>
                <form action="/merchant/recurringModule.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                    <li >
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="Recurring" name="submit" class="button1"
                                <%
                                    if(buttonvalue!=null)
                                    {
                                        if(buttonvalue.equals("Recurring"))
                                        {   %>
                                style="color:#ffffff;" id="active_menu"
                                <%}
                                }
                                %>><i class="fa fa-refresh" style="float:left;"></i><span><%=Top_Recurring_Module%></span>
                        </button>
                    </li>
                </form>
                <%
                        }
                    }
                %>
                <form action="/merchant/chngpwd.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                    <li >
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="ChangePassword" name="submit" class="button1"
                                <%
                                    if(request.getParameter("submit") !=null)
                                    {
                                        if(request.getParameter("submit").equals("ChangePassword"))
                                        {   %>
                                style="color:#ffffff;" id="active_menu"
                                <%}
                                }
                                %>><i class="fa fa-key iconstyle" style="float:left;"></i><span><%=Top_Change_Password%></span>
                        </button>
                    </li>
                </form>
            </ul>
        </div>
    </div>
    <script type="text/javascript" src="/merchant/cookies/jquery.ihavecookies.js"></script>
        <%if(!functions.isValueNull(copyiframe)){%>
    <script type = "text/javascript" src = "/merchant/cookies/cookies_popup.js" ></script >
        <%}%>
    <link href="/merchant/cookies/quicksand_font.css" rel="stylesheet">
    <link href="/merchant/cookies/cookies_popup.css" rel="stylesheet">
