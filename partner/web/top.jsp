<%@ page import="com.manager.dao.PartnerDAO" %>
<%@ page import="com.manager.enums.PartnerModuleEnum" %>
<%@ page import="net.partner.PartnerFunctions" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>

<%
    String buttonvalue = request.getParameter("submit");
    if (buttonvalue == null){
        buttonvalue = (String) session.getAttribute("submit");

    }
    //System.out.println("buttonvalue::" + buttonvalue);
    User user       = (User) session.getAttribute("ESAPIUserSessionKey");
    String ctoken   = null;
    if (user != null){
        ctoken = user.getCSRFToken();
    }

    PartnerFunctions partner    = new PartnerFunctions();


    String logoHeight           = "38";
    String logoWidth            = "140";

    PartnerDAO partnerDAOMain   = new PartnerDAO();
    String pId                  = String.valueOf(session.getAttribute("partnerid"));
    if(pId != null && !pId.equals("")){
        pId = String.valueOf(session.getAttribute("partnerid"));
    }
    PartnerDetailsVO partnerDetailsVOMain = null;
    if(partner.isValueNull(pId)){
        partnerDetailsVOMain = partnerDAOMain.geturl(pId);
    }

    if(partnerDetailsVOMain != null)
    {
        if(partner.isValueNull(partnerDetailsVOMain.getLogoHeight()))
        {
            logoHeight = partnerDetailsVOMain.getLogoHeight();
        }
        if(partner.isValueNull(partnerDetailsVOMain.getLogoWidth()))
        {
            logoWidth = partnerDetailsVOMain.getLogoWidth();
        }
    }

    String language_property = "com.directi.pg.PartnerLanguage_en";
    session.setAttribute("language_property",language_property);
    if (!partner.isLoggedInPartner(session))
    {
%>
<html>
<head>
    <title>Partner Administration Logout</title>
    <%@ include file="ietest.jsp" %>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <script src='/partner/css/menu.js'></script>
    <script type='text/javascript' src='/partner/css/menu_jquery.js'></script>
    <script src='/partner/css/bootstrap.min.js'></script>
    <link href="/partner/css/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <script type="text/javascript" src='/js/jQuery.dPassword.js'></script>
    <script type="text/javascript" src='/js/iPhonePassword.js'></script>
    <link rel="stylesheet" type="text/css" href="/partner/style/styles123.css">
    <link rel="stylesheet" type="text/css" href="/partner/style1/styles123.css">
</head>
<body>
<form action="index.jsp" method=post>
    <br><br>
    <br>
    <table class=search border="0" cellpadding="0" cellspacing="0" width="50%" align=center valign="center">
        <tr>
            <td align="center" style="background-color: #ffffff;">
                <img src="/images/partner/pay2.png" border="0">
            </td>
        </tr>
    </table>
    <br><br>
    <table class=search border="0" bgcolor="#F1EDE0" cellpadding="2" cellspacing="0" width="600"
           bordercolorlight="#000000" bordercolordark="#FFFFFF" align=center valign="center">
        <tr>
            <td bgcolor="#9A1305" class="label" align="left">&nbsp;&nbsp;Partner Administration Module</td>
        </tr>
        <tr>
            <td>&nbsp;</td>
        </tr>
        <tr align="center">
            <td class="text">&nbsp;&nbsp;Your session has expired</td>
        </tr>
        <tr>
            <td>&nbsp;</td>
        </tr>
        <tr align="center">
            <td class="text">&nbsp;&nbsp;Click <a href="/login.jsp" class="link">here</a> to go to the Partner login
                page
            </td>
        </tr>
        <tr>
            <td>&nbsp;</td>
        </tr>
    </table>
</form>
</body>
</html>
<%
        return;
    }
    Set<String> moduleSet       = (Set) session.getAttribute("moduleset");

    String emiConfiguration     = String .valueOf(session.getAttribute("emiConfiguration"));
    Map<String, String> logoMap = partner.getPartnerLogo(pId);
    String icon                 = "";
    String logo                 = "";
    String favicon              = "";
    if (partner.isValueNull(logoMap.get("logoName"))){
        logo = logoMap.get("logoName");
    }
    if (partner.isValueNull(logoMap.get("iconName"))){
        icon = logoMap.get("iconName");
    }
    if (partner.isValueNull(logoMap.get("faviconName"))){
        favicon = logoMap.get("faviconName");
    }
    session.setAttribute("favicon",favicon);

%>
<%@ include file="ietest.jsp" %>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
<script src="/partner/NewCss/libs/bootstrap/js/umd/popper.js"></script>
<script src="/partner/NewCss/libs/bootstrap/js/bootstrap.min.js"></script>
<script src="/partner/NewCss/libs/jqueryui/jquery-ui-custom.min.js"></script>
<script src="/partner/NewCss/libs/jquery-ui-touch/jquery.ui.touch-punch.min.js"></script>
<script src="/partner/NewCss/libs/jquery-detectmobile/detect.js"></script>
<script src="/partner/NewCss/libs/jquery-animate-numbers/jquery.animateNumbers.js"></script>
<script src="/partner/NewCss/libs/ios7-switch/ios7.switch.js"></script>
<script src="/partner/NewCss/libs/fastclick/fastclick.js"></script>
<%--<script src="/partner/NewCss/libs/jquery-blockui/jquery.blockUI.js"></script>
<script src="/partner/NewCss/libs/bootstrap-bootbox/bootbox.min.js"></script>--%>
<script src="/partner/NewCss/libs/jquery-sparkline/jquery-sparkline.js"></script>
<%--<script src="/partner/NewCss/libs/nifty-modal/js/classie.js"></script>
<script src="/partner/NewCss/libs/nifty-modal/js/modalEffects.js"></script>
<script src="/partner/NewCss/libs/sortable/sortable.min.js"></script>--%>
<script src="/partner/NewCss/libs/bootstrap-fileinput/bootstrap.file-input.js"></script>
<script src="/partner/NewCss/libs/bootstrap-select/bootstrap-select.min.js"></script>
<script src="/partner/NewCss/libs/bootstrap-select2/select2.min.js"></script>
<%--<script src="/partner/NewCss/libs/magnific-popup/jquery.magnific-popup.min.js"></script>--%>
<script src="/partner/NewCss/libs/pace/pace.min.js"></script>
<script src="/partner/NewCss/libs/bootstrap-datepicker/js/bootstrap-datepicker.js"></script>
<script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
<script src="/partner/NewCss/js/init.js"></script>

<script src="/partner/NewCss/libs/jquery-datatables/js/jquery.dataTables.min.js"></script>
<script src="/partner/NewCss/libs/jquery-datatables/js/dataTables.bootstrap.js"></script>
<script src="/partner/NewCss/libs/jquery-datatables/extensions/TableTools/js/dataTables.tableTools.min.js"></script>
<%--<script src="/partner/NewCss/js/pages/datatables.js"></script>--%>

<script src="/partner/NewCss/jquery-slimscroll/jquery.slimscroll.js"></script>

<%--<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>--%>

<!-- Base Css Files -->
<link href="/partner/NewCss/libs/jqueryui/ui-lightness/jquery-ui-1.10.4.custom.min.css" rel="stylesheet"/>
<link href="/partner/NewCss/libs/bootstrap/css/bootstrap.min.css" rel="stylesheet"/>
<link href="/partner/NewCss/libs/font-awesome/css/font-awesome.min.css" rel="stylesheet"/>
<%--<link href="/partner/NewCss/libs/fontello/css/fontello.css" rel="stylesheet" />--%>
<link href="/partner/NewCss/libs/animate-css/animate.min.css" rel="stylesheet"/>
<%--<link href="/partner/NewCss/libs/nifty-modal/css/component.css" rel="stylesheet" />
<link href="/partner/NewCss/libs/magnific-popup/magnific-popup.css" rel="stylesheet" />--%>
<link href="/partner/NewCss/libs/ios7-switch/ios7-switch.css" rel="stylesheet"/>
<link href="/partner/NewCss/libs/pace/pace.css" rel="stylesheet"/>
<link href="/partner/NewCss/libs/sortable/sortable-theme-bootstrap.css" rel="stylesheet"/>
<link href="/partner/NewCss/libs/bootstrap-datepicker/css/datepicker.css" rel="stylesheet"/>
<link href="/partner/NewCss/libs/jquery-icheck/skins/all.css" rel="stylesheet"/>
<!-- Code Highlighter for Demo -->
<%--<link href="/partner/NewCss/libs/prettify/github.css" rel="stylesheet" />--%>

<!-- Extra CSS Libraries Start -->
<%--<link href="/partner/NewCss/libs/rickshaw/rickshaw.min.css" rel="stylesheet" type="text/css" />--%>
<link href="/partner/NewCss/libs/morrischart/morris.css" rel="stylesheet" type="text/css"/>
<%--<link href="/partner/NewCss/libs/jquery-jvectormap/css/jquery-jvectormap-1.2.2.css" rel="stylesheet" type="text/css" />
<link href="/partner/NewCss/libs/jquery-clock/clock.css" rel="stylesheet" type="text/css" />
<link href="/partner/NewCss/libs/bootstrap-calendar/css/bic_calendar.css" rel="stylesheet" type="text/css" />
<link href="/partner/NewCss/libs/sortable/sortable-theme-bootstrap.css" rel="stylesheet" type="text/css" />
<link href="/partner/NewCss/libs/jquery-weather/simpleweather.css" rel="stylesheet" type="text/css" />
<link href="/partner/NewCss/libs/bootstrap-xeditable/css/bootstrap-editable.css" rel="stylesheet" type="text/css" />--%>
<link href="/partner/NewCss/css/style.css" rel="stylesheet" type="text/css"/>
<!-- Extra CSS Libraries End -->
<link href="/partner/NewCss/css/style-responsive.css" rel="stylesheet"/>

<script src="/partner/NewCss/libs/morrischart/morris.min.js"></script>
<%--<script src="/partner/NewCss/js/pages/morris-charts.js"></script>--%>

<link href="/partner/NewCss/libs/jquery-datatables/css/dataTables.bootstrap.css" rel="stylesheet" type="text/css">
<link href="/partner/NewCss/libs/jquery-datatables/extensions/TableTools/css/dataTables.tableTools.css" rel="stylesheet"
      type="text/css">
<div class="md-overlay"></div>
<style type="text/css">
    #sidebar-menu > ul > li > form > button {
        width: 100%;
        height: 40px;
        display: block;
        color: #555;
        text-indent: 13px;
        font-family: "Open Sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
        font-size: 12px;
        background: #ffffff;
        /*border: 1px solid #000000;*/
        margin-left: 0px;
        text-align: left;
        margin-top: 0px;
    }

    /*#sidebar-menu > ul > li > form > button > i{
        font-size: initial;
    }*/

    /***********************DatePicker Responsive Css Starts*********************/

    .table-condensed tr:nth-child(odd) {
        background: #ffffff !important;
    }

    @media (max-width: 640px) {
        .table-condensed tr:nth-child(odd) {
            background: #ffffff !important;
        }

        .table-condensed thead {
            display: block !important;
        }

        .datepicker-days .table-condensed td {
            display: inline-block !important;
        }

        .datepicker-months .table-condensed td {
            display: inherit !important;
        }

        .datepicker-years .table-condensed td {
            display: inherit !important;
        }

    }

    /***********************DatePicker Responsive Css Starts*********************/
    /***********************Pagination Responsive Starts***************************/

    table#paginateid {
        margin-bottom: 0;
        display: inherit;
        border-collapse: inherit;
        border-color: transparent;
    }

    table#paginateid tr:nth-child(odd) {
        background: transparent !important;
    }

    table#paginateid td {
        vertical-align: inherit;
    }

    @media (max-width: 640px) {
        table#paginateid td {
            display: inline-block;
        }
    }

    @media (max-width: 640px) {
        table#paginateid tr:nth-child(odd) {
            background: transparent !important;
        }
    }

    @media (max-width: 640px) {
        tr:nth-child(odd), tr:nth-child(even) {
            background: transparent !important;
        }
    }

    /***********************Pagination Responsive Ends***************************/

    #maindata > td {
        vertical-align: middle;
    }

    #mytabletr td:first-child {
        vertical-align: middle !important;
        border: 1px solid #ddd !important;
        text-align: center !important;
    }

    #wrapper.enlarged .left.side-menu #sidebar-menu ul > li > ul > li {
        margin-left: -50px;
    }

    #wrapper.enlarged .left.side-menu #sidebar-menu ul > li:hover > form > button {
        text-align: center;
    }

    #wrapper.enlarged .left.side-menu #sidebar-menu ul > li > form > button {
        text-align: center;
    }

    #wrapper.enlarged .left.side-menu {
        position: absolute;
    }

    .left.side-menu .slimscrollleft {
        height: 80% !important;
    }

    #wrapper.enlarged .left.side-menu .slimscrollleft {
        height: inherit !important;
    }

    .table > thead > tr > th {
        font-weight: inherit;
    }

    /********************Table Responsive Start**************************/

    @media (max-width: 640px) {

        table {
            border: 0;
        }

        /*table tr {
            padding-top: 20px;
            padding-bottom: 20px;
            display: block;
        }*/
        table thead {
            display: none;
        }

        tr:nth-child(odd), tr:nth-child(even) {
            background: #ffffff;
        }

        table td {
            display: block;
            border-bottom: none;
            padding-left: 0;
            padding-right: 0;
        }

        table td:before {
            content: attr(data-label);
            float: left;
            width: 100%;
            font-weight: bold;
        }

        table tr:nth-child(odd) {
            background: #cacaca !important;
        }

    }

    table {
        width: 100%;
        max-width: 100%;
        border-collapse: collapse;
        margin-bottom: 20px;
        display: table;
        border-collapse: separate;
        border-color: grey;
    }

    thead {
        display: table-header-group;
        vertical-align: middle;
        border-color: inherit;

    }

    tr:nth-child(odd) {
        background: #F9F9F9;
    }

    tr {
        display: table-row;
        vertical-align: inherit border-color : inherit;
    }

    th {
        padding-right: 1em;
        text-align: left;
        font-weight: bold;
    }

    td, th {
        display: table-cell;
        vertical-align: inherit;
    }

    tbody {
        display: table-row-group;
        vertical-align: middle;
        border-color: inherit;
    }

    td {
        padding-top: 6px;
        padding-bottom: 6px;
        padding-left: 10px;
        padding-right: 10px;
        vertical-align: top;
        border-bottom: none;
    }

    .table > thead > tr > th, .table > tbody > tr > th, .table > tfoot > tr > th, .table > thead > tr > td, .table > tbody > tr > td, .table > tfoot > tr > td {
        border-top: 1px solid #ddd;
    }

    /********************Table Responsive Ends**************************/

    #showingid {
        float: left;
        font-family: Open Sans, Helvetica Neue, Helvetica, Arial, sans-serif;
        font-size: 12px;
        padding: 10px;
    }

    @media (max-width: 640px) {
        #showingid {
            text-align: center;
            float: inherit;
        }
    }

    .profile-image {
        border: 4px double rgba(0, 0, 0, 0.0);
        border-radius: inherit !important;
    }

    .rounded-image img .rounded-image img {
        width: 82%;
        margin-left: 10px;
    }

    #sidebar-menu > ul > form > li > button > i {
        font-size: initial;
        /*color: rgba(0,0,0,0.4);*/
    }

    /*
        #sidebar-menu > ul > li > form >  button.active, #sidebar-menu > ul > li > form > button.active.subdrop {
            color: #fff;
            background: #7eccad !important;
            font-weight: 600;
            border-left: 0px solid rgba(0,0,0,0.3);
        }*/

    #sidebar-menu > ul > li.has_sub > button.active,
    #sidebar-menu > ul > li.has_sub > button.active.subdrop {
        color: #fff;
        background: #7eccad !important;
        font-weight: 600;
        border-left: 0px solid rgba(0, 0, 0, 0.3);
    }

    .navbar-nav > li.language_bar > .dropdown-menu:before, .navbar-nav > li > .dropdown-menu.grid-dropdown:before {
        border-bottom: 6px solid #7A868F !important;
    }

    #wrapper.enlarged .left.side-menu #sidebar-menu ul > li {
        white-space: inherit !important;
    }

    @media (min-width: 768px) {
        .navbar-right {
            margin-right: 0px !important;
        }
    }

    .icon-ccw-1 {
        display: none !important;
    }

    /*    @media (max-width: 640px) {
            .widget .additional-btn {
                float: right;
                position: inherit;
            }
        }*/

    @media (max-width: 991px) {
        .additional-btn {
            float: left;
            margin-left: 30px;
            margin-top: 10px;
            position: inherit !important;
        }
    }

    .apperrormsg {
        color: red;
        display: block;
        text-align: right;
    }

</style>
<script>
    var resizefunc = [];
    /*$(function(){
     alert("resize");
     $('#inner-content-div').slimScroll({
     height: '250px'
     });
     });*/
</script>
<script type="text/javascript">

    function readCookie(name)
    {
        var nameEQ = name + "=";
        var ca = document.cookie.split(';');
        for (var i = 0; i < ca.length; i++)
        {
            var c = ca[i];
            while (c.charAt(0) == ' ') c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
        }
        return null;
    }

    function eraseCookie(name)
    {
        createCookie(name, "", -1);
    }
    var backColor = new Array();

    backColor[0] = '#34495E';
    backColor[1] = '#73A4D9';
    backColor[2] = '#CCCCCC';
    backColor[3] = '#CCCCFF';
    backColor[4] = '#FFFFFF';
    backColor[5] = '#CCCCCC';
    function changeBG(whichColor){
        document.body.style.backgroundColor = backColor[whichColor];
        document.getElementById("cssmenu").style.background = "blue";
        document.getElementById("cssmenu").style.backgroundColor = backColor[whichColor];
        $('.toppanel').css("backgroundColor", backColor[whichColor]);
    }
    if (readCookie('backColor'))
        document.write('<style type="text/css">body {background-color: ' + backColor[readCookie("backColor")] + ';}<\/style>');
</script>
<%--top content--%>

<!-- Begin page -->
<div id="wrapper" class="wrapmenu">
    <div class="topbar">
        <div class="topbar-left">
            <div class="logo" style="width:auto; height:55px;">
                <a
                <%--href="#"--%>><%--<img src="/merchant/images/ForWebsiteSagarNew3.png" alt="Logo" style="margin-right: 30px;">--%>
                    <%--<img src="/merchant/images/<%=session.getAttribute("logo")%>" style="margin-right: 30px;height: 57%;">--%>
                    <%--<img src="/images/partner/pay2.png" style="margin-right: 30px;height: 57%;">--%>
                    <img src="/images/partner/<%=logo%>" style="width: <%=logoWidth%>px;height:100%;object-fit:contain;margin-left:30px;">
                </a>
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
                            <a class="dropdown-toggle" data-toggle="dropdown" style="cursor:pointer;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;English (US) <%--<i
                                    class="fa fa-caret-down"></i>--%></a>
                            <%--<ul class="dropdown-menu pull-right" style="background: #7A868F;">
                                <li><a style="color: #fff;cursor:pointer;">German</a></li>
                                <li><a style="color: #fff;cursor:pointer;">French</a></li>
                                <li><a style="color: #fff;cursor:pointer;">Italian</a></li>
                                <li><a style="color: #fff;cursor:pointer;">Spanish</a></li>
                            </ul>--%>
                        </li>
                    </ul>
                    <ul class="nav navbar-nav navbar-right top-navbar">

                        <li class="dropdown iconify hide-phone"><a style="cursor: pointer;"
                                                                   onclick="javascript:toggle_fullscreen()"><i
                                class="icon-resize-full-2"></i></a></li>
                        <li class="dropdown topbar-profile">
                            <%--<a  class="dropdown-toggle" data-toggle="dropdown" style="cursor: pointer;"><span class="rounded-image topbar-profile-image"><img src="/partner/images/p.png"></span><strong><%=user.getAccountName()%></strong> <i class="fa fa-caret-down"></i></a>--%>
                            <a class="dropdown-toggle" data-toggle="dropdown" style="cursor: pointer;"><span
                                    class="rounded-image topbar-profile-image"><img
                                    src="/partner/images/<%=user.getAccountName().substring(0,1).toLowerCase()%>.png"></span><strong><%=user.getAccountName()%>
                            </strong> <i class="fa fa-caret-down"></i></a>

                            <%
                                if (!user.getRoles().contains("partner"))
                                {
                            %>
                            <ul class="dropdown-menu">
                                <li style="margin-top: 10px;">
                                    <form action="/partner/partnerChngpassword.jsp?ctoken=<%=ctoken%>"
                                          style="margin-bottom: 1em;">

                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <button type="submit" name="submit" class="md-trigger"
                                                style="background: #7A868F;border: #7A868F;width: 100%;text-align: left;text-indent: 4px;">
                                            Change Password
                                        </button>
                                    </form>
                                </li>
                                <li class="divider"></li>
                                <%--<li><a ><i class="icon-help-2"></i> Help</a></li>--%>
                                <%--<li><a ><i class="icon-lock-1"></i> Lock me</a></li>--%>
                                <li>
                                    <form action="/partner/logout.jsp?ctoken=<%=ctoken%>" class="menufont"
                                          style="background-color:#ffffff;margin-bottom: 1em;">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <button type="submit" name="submit" class="md-trigger" data-modal="logout-modal"
                                                style="background: #7A868F;border: #7A868F;width: 100%;text-align: left;text-indent: 4px;">
                                            <i class="icon-logout-1"></i> Logout
                                        </button>
                                    </form>
                                </li>
                            </ul>
                            <%
                            }
                            else if (!user.getRoles().contains("subpartner"))
                            {
                            %>
                            <ul class="dropdown-menu">
                                <li style="margin-top: 10px;">
                                    <form action="/partner/net/PartnerDetailsConfig?ctoken=<%=ctoken%>"
                                          style="margin-bottom: 1em;">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <button type="submit" name="submit" class="md-trigger"
                                                style="background: #7A868F;border: #7A868F;width: 100%;text-align: left;text-indent: 4px;">
                                            Partner Profile
                                        </button>
                                    </form>
                                </li>
                                <li>
                                    <form action="/partner/partnerChngpassword.jsp?ctoken=<%=ctoken%>"
                                          style="margin-bottom: 1em;">

                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <button type="submit" name="submit" class="md-trigger"
                                                style="background: #7A868F;border: #7A868F;width: 100%;text-align: left;text-indent: 4px;">
                                            Change Password
                                        </button>
                                    </form>
                                </li>
                                <li>
                                    <form action="/partner/memberlist.jsp?ctoken=<%=ctoken%>"
                                          style="margin-bottom: 1em;">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <button type="submit" name="submit" class="md-trigger"
                                                style="background: #7A868F;border: #7A868F;width: 100%;text-align: left;text-indent: 4px;">
                                            Merchant Master

                                        </button>
                                    </form>
                                </li>
                                <li class="divider"></li>
                                <%--<li><a ><i class="icon-help-2"></i> Help</a></li>--%>
                                <%--<li><a ><i class="icon-lock-1"></i> Lock me</a></li>--%>
                                <li>

                                    <form action="/partner/logout.jsp?ctoken=<%=ctoken%>" class="menufont"
                                          style="background-color:#ffffff;margin-bottom: 1em;">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <button type="submit" name="submit" class="md-trigger" data-modal="logout-modal"
                                                style="background: #7A868F;border: #7A868F;width: 100%;text-align: left;text-indent: 4px;">
                                            <i class="icon-logout-1"></i> Logout
                                        </button>
                                    </form>

                                </li>
                            </ul>
                            <%
                                }
                            %>
                        </li>
                        <%--<li class="right-opener">
                            <a href="javascript:;" class="open-right"><i class="fa fa-angle-double-left"></i><i class="fa fa-angle-double-right"></i></a>
                        </li>--%>
                    </ul>
                </div>
                <!--/.nav-collapse -->
            </div>
        </div>
    </div>
    <div class="left side-menu">
        <br>

        <div class="profile-info">
            <div class="col-xs-4">
                <%-- <a class="rounded-image profile-image"><img src="/merchant/images/<%=user.getAccountName().substring(0,1).toLowerCase()%>.png"></a>--%>
                <%--<a class="rounded-image profile-image"><img src="/images/partner/Pay_Icon.png"></a>--%>
                <% if(partner.isValueNull(icon))
                {
                %>
                <a class="rounded-image profile-image"><img src="/images/partner/<%=icon%>"></a>
                <%
                }
                else
                {
                %>
                <a class="rounded-image profile-image"></a>
                <%
                    }
                %>
            </div>
            <div class="col-xs-8">
                <br>

                <div class="profile-text">

                    <%--Member ID : <%=(String) session.getAttribute("merchantid")%><br>--%>
                    Welcome <br>
                    User : <%=user.getAccountName()%>

                </div>
            </div>
        </div>

        <div class="clearfix"></div>
        <hr class="divider"/>
        <div class="clearfix"></div>
        <!--- Divider -->
        <%--<div class="some-content-related-div">--%>
        <%--<div id="inner-content-div" style="height:500px;">--%>
        <div id="sidebar-menu" class="slimscrollleft">

            <ul>
                <%
                    if (moduleSet.contains(PartnerModuleEnum.DASHBOARD.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                    {
                %>
                <form action="/partner/net/PartnerDashboard?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                    <li>
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="PartnerDashboard" name="submit" class="button1"
                                <%
                                    if (buttonvalue != null){
                                        if (buttonvalue.equals("PartnerDashboard"))
                                        { %>
                                style="background-color:#7eccad !important;color:#ffffff"
                                <%
                                        }
                                    }
                                %>><i class="fa fa-bar-chart-o iconstyle" style="float:left;"></i><span>DashBoard</span>
                        </button>
                    </li>
                </form>
                <%
                    }
                    /*if (moduleSet.contains(PartnerModuleEnum.GATEWAY_MASTER.name()) || (!user.getRoles().contains("superpartner")))
                    {*/
                %>
                <%--<form action="/partner/gatewayMaster.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                    <li>
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="gatewayMaster" name="submit" class="button1"
                                <%
                                    if (buttonvalue != null)
                                    {
                                        if (buttonvalue.equals("gatewayMaster"))
                                        { %>
                                style="background-color:#7eccad !important;color:#ffffff"
                                <%
                                        }
                                    }
                                %>><i class="fa fa-credit-card iconstyle"
                                      style="float:left;"></i><span>Gateway Master</span>
                        </button>
                    </li>
                </form>--%>
                <%
                    /* }*/
                    if (moduleSet.contains(PartnerModuleEnum.BANK_ACCOUNTS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                    {
                %>
                <form action="/partner/gatewayAccountInterface.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                    <li>
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="gatewayAccountInterface" name="submit" class="button1"
                                <%
                                    if (buttonvalue != null){
                                        if (buttonvalue.equals("gatewayAccountInterface"))
                                        { %>
                                style="background-color:#7eccad !important;color:#ffffff"
                                <%
                                        }
                                    }
                                %>><i class="fa fa-credit-card iconstyle"
                                      style="float:left;"></i><span>Bank Accounts</span>
                        </button>
                    </li>
                </form>
                <%
                    }
                    /*if (moduleSet.contains(PartnerModuleEnum.ACTIVITY_TRACKER.name()) *//*|| (!user.getRoles().contains("subpartner"))*//*)
                    {*/
                %>
                <%--<form action="/partner/activityTracker.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                    <li>
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="activityTracker" name="submit" class="button1"
                                <%
                                    if (buttonvalue != null){
                                        if (buttonvalue.equals("activityTracker"))
                                        { %>
                                style="background-color:#7eccad !important;color:#ffffff"
                                <%
                                        }
                                    }
                                %>>
                        <i class="fa fa-wrench"style="float:left;"></i><span>Activity Tracker</span>
                        </button>
                    </li>
                </form>--%>
                <%
                   // }
                    String status1 =null;

                    if(user.getRoles().contains("superpartner") || (user.getRoles().contains("childsuperpartner"))){
                        status1 = "true";
                    }
                    else
                        status1 = "false";
                    if (moduleSet.contains(PartnerModuleEnum.BANK_WIRE.name()) && status1.equals("true"))
                    {

                %>
                <form action="/partner/bankWireManager.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                    <li>
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="bankWire" name="submit" class="button1"
                                <%
                                    if (buttonvalue != null){
                                        if (buttonvalue.equals("bankWire"))
                                        { %>
                                style="background-color:#7eccad !important;color:#ffffff"
                                <%
                                        }
                                    }
                                %>><i class="fa fa-university"
                                      style="float:left;"></i><span>Bank Wire</span>
                        </button>
                    </li>
                </form>
                <%
                    }
                    if (moduleSet.contains(PartnerModuleEnum.MERCHANT_MANAGEMENT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                    {
                %>
                <li>
                    <%
                        if (buttonvalue!=null && ("Reports".equals(buttonvalue) ||"memberlist".equals(buttonvalue) || "MemberMappingDetails".equals(buttonvalue) || "merchanttranssummarylist".equals(buttonvalue) || "partnerMerchantCharges".equals(buttonvalue)|| "virtualcheckout".equals(buttonvalue)))
                        {
                    %>

                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-wrench" style="float:left;"></i><span>Merchant Management</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_WIRE_REPORTS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/merchantWireReports.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if ("Reports".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="Reports" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant Wire Reports
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="Reports" name="submit" class="button3">
                                    Merchant Wire Reports
                                </button>

                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_MASTER.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/memberlist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if ("memberlist".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="memberlist" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Merchant Master

                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="memberlist" name="submit" class="button3">
                                    Merchant Master

                                </button>

                                <%
                                    }
                                %>

                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_ACCOUNT_MAPPING.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/net/MemberMappingDetails?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if ("MemberMappingDetails".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="MemberMappingDetails" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Merchant Account Mapping
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="MemberMappingDetails" name="submit" class="button3">
                                    Merchant Account Mapping
                                </button>

                                <%
                                    }
                                %>

                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_VOLUME.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/merchanttranssummarylist.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if ("merchanttranssummarylist".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="merchanttranssummarylist" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Merchant Volume
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="merchanttranssummarylist" name="submit" class="button3">
                                    Merchant Volume
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CHARGES.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerMerchantCharges.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if ("partnerMerchantCharges".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="partnerMerchantCharges" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Merchant Charges
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="partnerMerchantCharges" name="submit" class="button3">
                                    Merchant Charges
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }if (moduleSet.contains(PartnerModuleEnum.VIRTUAL_CHECKOUT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                        {
                        %>
                        <li>
                            <form action="/partner/virtualCheckOut.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if ("virtualcheckout".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="virtualcheckout" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Virtual Checkout
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="virtualcheckout" name="submit" class="button3">
                                    Virtual Checkout
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%
                    }
                    else
                    {
                    %>

                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;"
                            class="button1">
                        <i class="fa fa-paste" style="float:left;"></i>
                        <span>Merchant Management</span>
                        <span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: none">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_WIRE_REPORTS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/merchantWireReports.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Reports" name="submit" class="button3">
                                    Merchant Wire Reports
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_MASTER.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/memberlist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="memberlist" name="submit" class="button3">
                                    Merchant Master
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_ACCOUNT_MAPPING.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/net/MemberMappingDetails?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="MemberMappingDetails" name="submit" class="button3">
                                    Merchant Account Mapping
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_VOLUME.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/merchanttranssummarylist.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchanttranssummarylist" name="submit" class="button3">
                                    Merchant Volume
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CHARGES.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerMerchantCharges.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerMerchantCharges" name="submit" class="button3">
                                    Merchant Charges
                                </button>
                            </form>
                        </li>
                        <%
                            } if (moduleSet.contains(PartnerModuleEnum.VIRTUAL_CHECKOUT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                        {
                        %>
                        <li>
                            <form action="/partner/virtualCheckOut.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="virtualcheckout" name="submit" class="button3">
                                    Virtual Checkout
                                </button>
                            </form>
                        </li>
                        <%
                            }

                        %>
                    </ul>
                    <%
                        }
                    %>
                </li>
                <%
                    }
                    if (moduleSet.contains(PartnerModuleEnum.TRANSACTION_MANAGEMENT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                    {
                %>
                <li>
                    <%-- <% if (buttonvalue == null)
                     {
                         System.out.println("inside button:::::::::::::::::::::::::::::::::::::"+buttonvalue);
                     %>
                     <button onclick="window.location.href='#'" id="clicker"
                             style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;margin-bottom: 0px;"
                             class="button1">
                         <i class="fa fa-paste iconstyle"
                            style="float:left;"></i><span>Transaction Management</span><span class="pull-right"><i
                             class="fa fa-caret-down"></i></span>
                     </button>
                     <ul style="display: none;">
                         <%
                             if (moduleSet.contains(PartnerModuleEnum.MERCHANT_TRANSACTIONS.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerTransactions.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="partnerTransactions" name="submit" class="button3">
                                     Merchant Transactions
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.MERCHANT_REFUND.name()) || (!user.getRoles().contains("subpartner")))
                             {
                                 if (session.getAttribute("isRefund") != null && "Y".equalsIgnoreCase((String) session.getAttribute("isRefund")))
                                 {
                         %>
                         <li>
                             <form action="/partner/partnerMerchantRefundList.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="Refund" name="submit" class="button3"
                                         <%
                                             if (buttonvalue != null)
                                             {
                                                 if (buttonvalue.equals("Refund"))
                                                 {

                                                 }
                                             }
                                         %>>Merchant Refund
                                 </button>
                             </form>
                         </li>
                         <%
                                 }
                             }
                             if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CAPTURE.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerCapture.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="partnerCapture" name="submit" class="button3">
                                     Merchant Capture
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CHARGEBACK.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerMerchantChargebackList.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="MerchantChargeback" name="submit" class="button3">
                                     Merchant Chargeback
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                         %>
                     </ul--%>

                    <%
                        // }
                        if (buttonvalue!=null && ("partnerTransactions".equals(buttonvalue) || "Refund".equals(buttonvalue) || "partnerCapture".equals(buttonvalue) || "MerchantChargeback".equals(buttonvalue) || "CardPresentTransaction".equals(buttonvalue) || "FraudAlertTransactions".equals(buttonvalue) || "MerchantPayoutTransactions".equals(buttonvalue) || "rejectedTransactionsList".equals(buttonvalue) || "recurringModule".equals(buttonvalue)))
                        {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i>
                        <span>Transaction Management</span>
                        <span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_TRANSACTIONS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerTransactions.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("partnerTransactions".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="partnerTransactions" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant Transactions
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="partnerTransactions" name="submit" class="button3">

                                    Merchant Transactions
                                </button>
                                <%
                                    }
                                %>

                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_REFUND.name()) )
                            {
                                /*if (session.getAttribute("isRefund") != null && "Y".equalsIgnoreCase((String) session.getAttribute("isRefund")))
                                {*/

                        %>
                        <li>
                            <form action="/partner/partnerMerchantRefundList.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("Refund".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="Refund" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant Refund
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="Refund" name="submit" class="button3">
                                    Merchant Refund
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            /*}*/
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CAPTURE.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerCapture.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("partnerCapture".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="partnerCapture" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant Capture
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="partnerCapture" name="submit" class="button3">
                                    Merchant Capture
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CHARGEBACK.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerMerchantChargebackList.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("MerchantChargeback".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="MerchantChargeback" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant Chargeback
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="MerchantChargeback" name="submit" class="button3">
                                    Merchant Chargeback
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_PAYOUT_TRANSACTIONS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/merchantPayoutTransactions.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("MerchantPayoutTransactions".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="MerchantPayoutTransactions" name="submit" class="button3">
                                    Merchant Payout Transactions </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="MerchantPayoutTransactions" name="submit" class="button3" >
                                    Merchant Payout Transactions </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FETCH_CARD_PRESENT_TRANSACTION.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/fetchCardPresentTransaction.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("CardPresentTransaction".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="CardPresentTransaction" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Fetch Card Present Transaction
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="CardPresentTransaction" name="submit" class="button3">
                                    Fetch Card Present Transaction
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_ALERT_TRANSACTIONS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/fraudAlertTransaction.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("FraudAlertTransactions".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="FraudAlertTransactions" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Fraud Alert Transactions
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="FraudAlertTransactions" name="submit" class="button3">
                                    Fraud Alert Transactions
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%}
                        if (moduleSet.contains(PartnerModuleEnum.TRANSACTIONS_LOGS.name())){
                        %>
                        <li>
                            <form action="/partner/net/TransactionsLogs?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <input type="hidden" value="search" name="action">
                                <%if("TransactionsLogs".equals(buttonvalue)){%>
                                <button type="submit" value="TransactionsLogs" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Transactions Logs
                                </button>
                                <%}else{%>
                                <button type="submit" value="TransactionsLogs" name="submit" class="button3">
                                    Transactions Logs
                                </button>
                                <%}%>
                            </form>
                        </li>
                        <%
                            }
                            if(moduleSet.contains(PartnerModuleEnum.REJECTED_TRANSACTION_LIST.name())){
                        %>
                        <li>
                            <form action="/partner/rejectedTransactionsList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%if("rejectedTransactionsList".equals(buttonvalue)){%>
                                <button type="submit" value="rejectedTransactionsList" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Rejected Transaction List
                                </button>
                                <%}else{%>
                                <button type="submit" value="rejectedTransactionsList" name="submit" class="button3">
                                    Rejected Transaction List
                                </button>
                                <%}%>
                            </form>
                        </li>
                        <%
                            }
                            if(moduleSet.contains(PartnerModuleEnum.RECURRING_MODULE.name())){
                        %>
                        <li>
                            <form action="/partner/recurringModule.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%if("recurringModule".equals(buttonvalue)){%>
                                <button type="submit" value="recurringModule" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Recurring Module
                                </button>
                                <%}else{%>
                                <button type="submit" value="recurringModule" name="submit" class="button3">
                                    Recurring Module
                                </button>
                                <%}%>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>



                    <%
                        /* }
                          else if ("Refund".equals(buttonvalue))
                          {*/
                    %>
                    <%-- <button onclick="window.location.href='#'" id="clicker"
                             style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                             class="button1 active">
                         <i class="fa fa-paste iconstyle"
                            style="float:left;"></i><span>Transaction Management</span><span class="pull-right"><i
                             class="fa fa-caret-down"></i></span>
                     </button>
                     <ul style="display:block;">
                         <%
                             if (moduleSet.contains(PartnerModuleEnum.MERCHANT_TRANSACTIONS.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerTransactions.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="partnerTransactions" name="submit" class="button3">
                                     Merchant Transactions
                                 </button>
                             </form>
                         </li>--%>
                    <%-- <%
                         }
                         if (moduleSet.contains(PartnerModuleEnum.MERCHANT_REFUND.name()) || (!user.getRoles().contains("subpartner")))
                         {
                             if (session.getAttribute("isRefund") != null && "Y".equalsIgnoreCase((String) session.getAttribute("isRefund")))
                             {
                     %>--%>
                    <%-- <li>
                         <form action="/partner/partnerMerchantRefundList.jsp?ctoken=<%=ctoken%>"
                               style="margin-bottom: 0px">
                             <input type="hidden" value="<%=ctoken%>" name="ctoken">
                             <button type="submit" value="Refund" name="submit" class="button3"
                                     style="background-color:#d9d9d9"
                                     <%
                                         if (buttonvalue != null)
                                         {
                                             if (buttonvalue.equals("Refund"))
                                             {

                                             }
                                         }
                                     %>>Merchant Refund
                             </button>
                         </form>
                     </li>--%>
                    <%-- <%
                             }
                         }
                         if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CAPTURE.name()) || (!user.getRoles().contains("subpartner")))
                         {
                     %>
                     <li>
                         <form action="/partner/partnerCapture.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                             <input type="hidden" value="<%=ctoken%>" name="ctoken">
                             <button type="submit" value="partnerCapture" name="submit" class="button3">
                                 Merchant Capture
                             </button>
                         </form>
                     </li>
                     <%
                         }
                         if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CHARGEBACK.name()) || (!user.getRoles().contains("subpartner")))
                         {
                     %>--%>
                    <%-- <li>
                         <form action="/partner/partnerMerchantChargebackList.jsp?ctoken=<%=ctoken%>"
                               style="margin-bottom: 0px">
                             <input type="hidden" value="<%=ctoken%>" name="ctoken">
                             <button type="submit" value="MerchantChargeback" name="submit" class="button3">
                                 Merchant Chargeback
                             </button>
                         </form>
                     </li>
                     <%
                         }
                     %>
                 </ul>--%>


                    <%-- <%
                     }
                     else if ("partnerCapture".equals(buttonvalue))
                     {
                     %>
                     <button onclick="window.location.href='#'" id="clicker"
                             style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                             class="button1 active">
                         <i class="fa fa-paste iconstyle"
                            style="float:left;"></i><span>Transaction Management</span><span class="pull-right"><i
                             class="fa fa-caret-down"></i></span>
                     </button>
                     <ul style="display:block;">
                         <%
                             if (moduleSet.contains(PartnerModuleEnum.MERCHANT_TRANSACTIONS.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerTransactions.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="partnerTransactions" name="submit" class="button3">
                                     Merchant Transactions
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.MERCHANT_REFUND.name()) || (!user.getRoles().contains("subpartner")))
                             {
                                 if (session.getAttribute("isRefund") != null && "Y".equalsIgnoreCase((String) session.getAttribute("isRefund")))
                                 {
                         %>
                         <li>
                             <form action="/partner/partnerMerchantRefundList.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="Refund" name="submit" class="button3"
                                         <%
                                             if (buttonvalue != null)
                                             {
                                                 if (buttonvalue.equals("Refund"))
                                                 {

                                                 }
                                             }
                                         %>>Merchant Refund
                                 </button>
                             </form>
                         </li>
                         <%
                                 }
                             }
                             if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CAPTURE.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerCapture.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="partnerCapture" name="submit" class="button3"
                                         style="background-color:#d9d9d9">
                                     Merchant Capture
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CHARGEBACK.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerMerchantChargebackList.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="MerchantChargeback" name="submit" class="button3">
                                     Merchant Chargeback
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                         %>
                     </ul>--%>


                    <%--  <%
                      }
                      else
                      {
                          if ("MerchantChargeback".equals(buttonvalue))
                          {
                      %>
                      <button onclick="window.location.href='#'" id="clicker"
                              style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                              class="button1 active">
                          <i class="fa fa-paste iconstyle"
                             style="float:left;"></i><span>Transaction Management</span><span class="pull-right"><i
                              class="fa fa-caret-down"></i></span>
                      </button>
                      <ul style="display:block;">
                          <%
                              if (moduleSet.contains(PartnerModuleEnum.MERCHANT_TRANSACTIONS.name()) || (!user.getRoles().contains("subpartner")))
                              {
                          %>
                          <li>
                              <form action="/partner/partnerTransactions.jsp?ctoken=<%=ctoken%>"
                                    style="margin-bottom: 0px">
                                  <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                  <button type="submit" value="partnerTransactions" name="submit" class="button3">
                                      Merchant Transactions
                                  </button>
                              </form>
                          </li>
                          <%
                              }
                              if (moduleSet.contains(PartnerModuleEnum.MERCHANT_REFUND.name()) || (!user.getRoles().contains("subpartner")))
                              {
                                  if (session.getAttribute("isRefund") != null && "Y".equalsIgnoreCase((String) session.getAttribute("isRefund")))
                                  {
                          %>
                          <li>
                              <form action="/partner/partnerMerchantRefundList.jsp?ctoken=<%=ctoken%>"
                                    style="margin-bottom: 0px">
                                  <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                  <button type="submit" value="Refund" name="submit" class="button3"
                                          <%
                                              if (buttonvalue != null)
                                              {
                                                  if (buttonvalue.equals("Refund"))
                                                  {

                                                  }
                                              }
                                          %>>Merchant Refund
                                  </button>
                              </form>
                          </li>
                          <%
                                  }
                              }
                              if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CAPTURE.name()) || (!user.getRoles().contains("subpartner")))
                              {
                          %>
                          <li>
                              <form action="/partner/partnerCapture.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                  <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                  <button type="submit" value="partnerCapture" name="submit" class="button3">
                                      Merchant Capture
                                  </button>
                              </form>
                          </li>
                          <%
                              }
                              if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CHARGEBACK.name()) || (!user.getRoles().contains("subpartner")))
                              {
                          %>
                          <li>
                              <form action="/partner/partnerMerchantChargebackList.jsp?ctoken=<%=ctoken%>"
                                    style="margin-bottom: 0px">
                                  <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                  <button type="submit" value="MerchantChargeback" name="submit" class="button3"
                                          style="background-color:#d9d9d9">
                                      Merchant Chargeback
                                  </button>
                              </form>
                          </li>
                          <%
                              }
                          %>
                      </ul>--%>
                    <%
                    }
                    else
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;"
                            class="button1">
                        <i class="fa fa-paste iconstyle"
                           style="float:left;"></i><span>Transaction Management</span><span class="pull-right"><i
                            class="fa fa-caret-down"></i></span>
                    </button>
                    <ul>
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_TRANSACTIONS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerTransactions.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerTransactions" name="submit" class="button3">
                                    Merchant Transactions
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_REFUND.name()))//*|| (!user.getRoles().contains("subpartner"))*//*)
                            {
                                /*if (session.getAttribute("isRefund") != null && "Y".equalsIgnoreCase((String) session.getAttribute("isRefund")))
                                {*/
                        %>
                        <li>
                            <form action="/partner/partnerMerchantRefundList.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Refund" name="submit" class="button3"
                                        <%
                                            if (buttonvalue != null)
                                            {
                                                if (buttonvalue.equals("Refund"))
                                                {

                                                }
                                            }
                                        %>>Merchant Refund
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            /*}*/
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CAPTURE.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerCapture.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerCapture" name="submit" class="button3">
                                    Merchant Capture
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CHARGEBACK.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                                System.out.println("inside if merchant chargeback");
                        %>
                        <li>
                            <form action="/partner/partnerMerchantChargebackList.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="MerchantChargeback" name="submit" class="button3">
                                    Merchant Chargeback
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_PAYOUT_TRANSACTIONS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                                System.out.println("merchantpayouttransactions");

                        %>
                        <li>
                            <form action="/partner/merchantPayoutTransactions.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="MerchantPayoutTransactions" name="submit" class="button3">
                                    Merchant Payout Transactions
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FETCH_CARD_PRESENT_TRANSACTION.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/fetchCardPresentTransaction.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="CardPresentTransaction" name="submit" class="button3">
                                    Fetch Card Present Transaction
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_ALERT_TRANSACTIONS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/fraudAlertTransaction.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="FraudAlertTransactions" name="submit" class="button3">
                                    Fraud Alert Transactions
                                </button>
                            </form>
                        </li>
                        <%}
                        if (moduleSet.contains(PartnerModuleEnum.TRANSACTIONS_LOGS.name())){
                        %>
                        <li>
                            <form action="/partner/net/TransactionsLogs?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <input type="hidden" value="search" name="ctoken">
                                <button type="submit" value="TransactionsLogs" name="submit" class="button3">
                                    Transactions Logs
                                </button>
                            </form>
                        </li>
                        <%}
                            if (moduleSet.contains(PartnerModuleEnum.REJECTED_TRANSACTION_LIST.name())){
                        %>
                        <li>
                            <form action="/partner/rejectedTransactionsList.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="rejectedTransactionsList" name="submit" class="button3">
                                    Rejected Transaction List
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.RECURRING_MODULE.name())){
                        %>
                        <li>
                            <form action="/partner/recurringModule.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="recurringModule" name="submit" class="button3">
                                    Recurring Module
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%
                        }
                    %>
                </li>
                <%
                    }
                %>


                <%
                    if (moduleSet.contains(PartnerModuleEnum.MERCHANT_SETTINGS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                    {
                %>
                <li>
                    <%-- <% if (buttonvalue == null)
                     {%>
                     <button onclick="window.location.href='#'" id="clicker"
                             style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;"
                             class="button1">
                         <i class="fa fa-wrench" style="float:left;"></i><span>Merchant Settings</span><span
                             class="pull-right"><i class="fa fa-caret-down"></i></span>
                     </button>
                     <ul style="display:none;">
                         <%
                             if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/merchantfraudsetting.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="merchantfraudsetting" name="submit" class="button3">
                                     Merchant Fraud Settings
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.MERCHANT_LIMIT_MANAGEMENT.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/viewmerchantrisk.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="viewmerchantrisk" name="submit" class="button3">
                                     Merchant Limit Management
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.MERCHANT_MAIL_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/merchantmailaccess.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="merchantmailaccess" name="submit" class="button3">
                                     Merchant Mail Settings
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.MERCHANT_TRANSACTION_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/transactionsprocess.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="transactionsprocess" name="submit" class="button3">
                                     Merchant Transaction Settings
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.BIN_ROUTING_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/binRouting.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="binrouting" name="submit" class="button3">
                                     Bin Routing Settings
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BACK_OFFICE_ACCESS.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/backofficeaccess.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="backofficeaccess" name="submit" class="button3">
                                     Merchant BackOffice Access
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.MERCHANT_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/net/BlockedMerchantList?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="BlockedMerchantList" name="submit" class="button3">
                                     Merchant Unblocked Account
                                 </button>
                             </form>
                         </li>
                         <%
                             }

                             if (moduleSet.contains(PartnerModuleEnum.MERCHANT_USER_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/net/MerchantUserUnblockedAccount?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="MerchantUserUnblockedAccount" name="submit"
                                         class="button3">
                                     Merchant User Unblocked
                                 </button>
                             </form>
                         </li>
                         <%
                             }

                             if (moduleSet.contains(PartnerModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/blacklist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="Black List" name="submit" class="button3">
                                     Blacklist Details
                                 </button>
                             </form>
                         </li>
                         <%
                             }

                             if (moduleSet.contains(PartnerModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="White List" name="submit" class="button3">
                                     Whitelist Details
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                         %>
                     </ul>--%>
                    <%
                        /* }
                         else
                         {*/
                        if (buttonvalue!=null && ("merchantfraudsetting".equals(buttonvalue)) || "viewmerchantrisk".equals(buttonvalue) || "merchantmailaccess".equals(buttonvalue) || "transactionsprocess".equals(buttonvalue) || "binrouting".equals(buttonvalue) ||"backofficeaccess".equals(buttonvalue) || "BlockedMerchantList".equals(buttonvalue) || "MerchantUserUnblockedAccount".equals(buttonvalue) || "MerchantUserUnblockedAccount".equals(buttonvalue) || "Black List".equals(buttonvalue) || "White List".equals(buttonvalue) || "merchantcallbacksettings".equals(buttonvalue) || "MerchantTheme".equals(buttonvalue))
                        {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-wrench" style="float:left;"></i><span>Merchant Settings</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_SETTINGS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/merchantfraudsetting.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("merchantfraudsetting".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="merchantfraudsetting" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant Fraud Settings
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="merchantfraudsetting" name="submit" class="button3">
                                    Merchant Fraud Settings
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_LIMIT_MANAGEMENT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/viewmerchantrisk.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("viewmerchantrisk".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="viewmerchantrisk" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant Limit Management
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="viewmerchantrisk" name="submit" class="button3">
                                    Merchant Limit Management
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_MAIL_SETTINGS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/merchantmailaccess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("merchantmailaccess".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="merchantmailaccess" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant Mail Settings
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="merchantmailaccess" name="submit" class="button3">
                                    Merchant Mail Settings
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_TRANSACTION_SETTINGS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/transactionsprocess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("transactionsprocess".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="transactionsprocess" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant Transaction Settings
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="transactionsprocess" name="submit" class="button3">
                                    Merchant Transaction Settings
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if(moduleSet.contains(PartnerModuleEnum.CHECKOUT_CONFIGURATION.name()))
                            {
                        %>
                        <li>
                            <form action="/partner/checkoutConfiguration.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" name="ctoken" value="<%=ctoken%>">
                                <%
                                    if("checkout_configuration".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="checkout_configuration" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Checkout Configuration
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="checkout_configuration" name="submit" class="button3">
                                    Checkout Configuration
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BIN_ROUTING_SETTINGS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/binRouting.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("binrouting".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="binrouting" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Routing Settings
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="binrouting" name="submit" class="button3">
                                    Routing Settings
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BACK_OFFICE_ACCESS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/backofficeaccess.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("backofficeaccess".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="backofficeaccess" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant BackOffice Access
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="backofficeaccess" name="submit" class="button3">
                                    Merchant BackOffice Access
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                        %>
                        <%-- <%
                             if (moduleSet.contains(PartnerModuleEnum.MERCHANT_UNBLOCKED_ACCOUNT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                             {
                         %>
                         <li>
                             <form action="/partner/net/BlockedMerchantList?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <%
                                     if("BlockedMerchantList".equals(buttonvalue))
                                     {
                                 %>
                                 <button type="submit" value="BlockedMerchantList" name="submit" class="button3"
                                         style="background-color:#d9d9d9">
                                     Merchant Unblocked Account
                                 </button>
                                 <%
                                 }
                                 else
                                 {
                                 %>
                                 <button type="submit" value="BlockedMerchantList" name="submit" class="button3">
                                     Merchant Unblocked Account
                                 </button>
                                 <%
                                     }
                                 %>
                             </form>
                         </li>
                         <%
                             }

                             if (moduleSet.contains(PartnerModuleEnum.MERCHANT_USER_UNBLOCKED_ACCOUNT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                             {
                         %>
                         <li>
                             <form action="/partner/net/MerchantUserUnblockedAccount?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <%
                                     if("MerchantUserUnblockedAccount".equals(buttonvalue))
                                     {
                                 %>
                                 <button type="submit" value="MerchantUserUnblockedAccount" name="submit" class="button3"
                                         style="background-color:#d9d9d9">
                                     Merchant User Unblocked
                                 </button>
                                 <%
                                 }
                                 else
                                 {
                                 %>
                                 <button type="submit" value="MerchantUserUnblockedAccount" name="submit"
                                         class="button3">
                                     Merchant User Unblocked
                                 </button>
                                 <%
                                     }
                                 %>
                             </form>
                         </li>
                         <%
                             }
                             %>--%>
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.BLACKLIST_DETAILS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/blacklist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("Black List".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="Black List" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Blacklist Details
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="Black List" name="submit" class="button3">
                                    Blacklist Details
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }

                            if (moduleSet.contains(PartnerModuleEnum.WHITELIST_DETAILS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("White List".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="White List" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Whitelist Details
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="White List" name="submit" class="button3">
                                    Whitelist Details
                                </button>
                                <%
                                    }
                                %>

                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CALLBACK_SETTINGS.name()))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantcallbacksettings.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("merchantcallbacksettings".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="merchantcallbacksettings" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant Callback Settings
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="merchantcallbacksettings" name="submit" class="button3">
                                    Merchant Callback Settings
                                </button>
                                <%
                                    }
                                %>

                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.THEME_CONFIGURATION.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/net/MerchantTheme?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("MerchantTheme".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="MerchantTheme" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Theme Configuration
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="MerchantTheme" name="submit" class="button3">
                                    Theme Configuration
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%--<%
                    }
                    else if ("viewmerchantrisk".equals(buttonvalue))
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-wrench" style="float:left;"></i><span>Merchant Settings</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantfraudsetting.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantfraudsetting" name="submit" class="button3">
                                    Merchant Fraud Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_LIMIT_MANAGEMENT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/viewmerchantrisk.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="viewmerchantrisk" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant Limit Management
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_MAIL_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantmailaccess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantmailaccess" name="submit" class="button3">
                                    Merchant Mail Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_TRANSACTION_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/transactionsprocess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="transactionsprocess" name="submit" class="button3">
                                    Merchant Transaction Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BIN_ROUTING_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/binRouting.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="binrouting" name="submit" class="button3">
                                    Bin Routing Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BACK_OFFICE_ACCESS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/backofficeaccess.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="backofficeaccess" name="submit" class="button3">
                                    Merchant BackOffice Access
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/BlockedMerchantList?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="BlockedMerchantList" name="submit" class="button3">
                                    Merchant Unblocked Account
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_USER_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/MerchantUserUnblockedAccount?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="MerchantUserUnblockedAccount" name="submit"
                                        class="button3">
                                    Merchant User Unblocked
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/blacklist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3">
                                    Blacklist Details
                                </button>
                            </form>
                        </li>
                        <%
                            }

                            if (moduleSet.contains(PartnerModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3">
                                    Whitelist Details
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>

                    </ul>
                    <%
                    }
                    else if ("merchantmailaccess".equals(buttonvalue))
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-wrench" style="float:left;"></i><span>Merchant Settings</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>

                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantfraudsetting.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantfraudsetting" name="submit" class="button3">
                                    Merchant Fraud Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_LIMIT_MANAGEMENT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/viewmerchantrisk.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="viewmerchantrisk" name="submit" class="button3">
                                    Merchant Limit Management
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_MAIL_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantmailaccess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantmailaccess" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant Mail Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_TRANSACTION_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/transactionsprocess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="transactionsprocess" name="submit" class="button3">
                                    Merchant Transaction Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BIN_ROUTING_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/binRouting.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="binrouting" name="submit" class="button3">
                                    Bin Routing Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BACK_OFFICE_ACCESS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/backofficeaccess.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="backofficeaccess" name="submit" class="button3">
                                    Merchant BackOffice Access
                                </button>
                            </form>
                        </li>
                        <%
                            }

                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/BlockedMerchantList?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="BlockedMerchantList" name="submit" class="button3">
                                    Merchant Unblocked Account
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_USER_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/MerchantUserUnblockedAccount?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="MerchantUserUnblockedAccount" name="submit"
                                        class="button3">
                                    Merchant User Unblocked
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/blacklist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3">
                                    Blacklist Details
                                </button>
                            </form>
                        </li>
                        <%
                            }

                            if (moduleSet.contains(PartnerModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3">
                                    Whitelist Details
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>

                    </ul>
                    <%
                    }
                    else if ("transactionsprocess".equals(buttonvalue))
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-wrench" style="float:left;"></i><span>Merchant Settings</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantfraudsetting.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantfraudsetting" name="submit" class="button3">
                                    Merchant Fraud Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_LIMIT_MANAGEMENT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/viewmerchantrisk.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="viewmerchantrisk" name="submit" class="button3">
                                    Merchant Limit Management
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_MAIL_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantmailaccess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantmailaccess" name="submit" class="button3">
                                    Merchant Mail Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_TRANSACTION_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/transactionsprocess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="transactionsprocess" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant Transaction Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BIN_ROUTING_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/binRouting.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="binrouting" name="submit" class="button3">
                                    Bin Routing Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BACK_OFFICE_ACCESS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/backofficeaccess.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="backofficeaccess" name="submit" class="button3">
                                    Merchant BackOffice Access
                                </button>
                            </form>
                        </li>
                        <%
                            }

                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/BlockedMerchantList?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="BlockedMerchantList" name="submit" class="button3">
                                    Merchant Unblocked Account
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_USER_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/MerchantUserUnblockedAccount?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="MerchantUserUnblockedAccount" name="submit"
                                        class="button3">
                                    Merchant User Unblocked
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/blacklist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3">
                                    Blacklist Details
                                </button>
                            </form>
                        </li>
                        <%
                            }

                            if (moduleSet.contains(PartnerModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3">
                                    Whitelist Details
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>

                    </ul>
                    <%
                    }
                    else if ("backofficeaccess".equals(buttonvalue))
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-wrench" style="float:left;"></i><span>Merchant Settings</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantfraudsetting.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantfraudsetting" name="submit" class="button3">
                                    Merchant Fraud Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_LIMIT_MANAGEMENT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/viewmerchantrisk.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="viewmerchantrisk" name="submit" class="button3">
                                    Merchant Limit Management
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_MAIL_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantmailaccess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantmailaccess" name="submit" class="button3">
                                    Merchant Mail Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_TRANSACTION_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/transactionsprocess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="transactionsprocess" name="submit" class="button3">
                                    Merchant Transaction Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BIN_ROUTING_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/binRouting.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="binrouting" name="submit" class="button3">
                                    Bin Routing Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BACK_OFFICE_ACCESS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/backofficeaccess.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="backofficeaccess" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant BackOffice Access
                                </button>
                            </form>
                        </li>
                        <%
                            }

                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/BlockedMerchantList?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="BlockedMerchantList" name="submit" class="button3">
                                    Merchant Unblocked Account
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_USER_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/MerchantUserUnblockedAccount?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="MerchantUserUnblockedAccount" name="submit"
                                        class="button3">
                                    Merchant User Unblocked
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/blacklist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3">
                                    Blacklist Details
                                </button>
                            </form>
                        </li>
                        <%
                            }

                            if (moduleSet.contains(PartnerModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3">
                                    Whitelist Details
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>

                    </ul>
                    <%
                    }
                    else if ("binrouting".equals(buttonvalue))
                    {%>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-wrench" style="float:left;"></i><span>Merchant Settings</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantfraudsetting.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantfraudsetting" name="submit" class="button3">
                                    Merchant Fraud Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_LIMIT_MANAGEMENT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/viewmerchantrisk.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="viewmerchantrisk" name="submit" class="button3">
                                    Merchant Limit Management
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_MAIL_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantmailaccess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantmailaccess" name="submit" class="button3">
                                    Merchant Mail Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_TRANSACTION_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/transactionsprocess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="transactionsprocess" name="submit" class="button3">
                                    Merchant Transaction Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BIN_ROUTING_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/binRouting.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="binrouting" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Bin Routing Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BACK_OFFICE_ACCESS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/backofficeaccess.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="backofficeaccess" name="submit" class="button3">
                                    Merchant BackOffice Access
                                </button>
                            </form>
                        </li>
                        <%
                            }

                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/BlockedMerchantList?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="BlockedMerchantList" name="submit" class="button3">
                                    Merchant Unblocked Account
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_USER_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/MerchantUserUnblockedAccount?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="MerchantUserUnblockedAccount" name="submit"
                                        class="button3">
                                    Merchant User Unblocked
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/blacklist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3">
                                    Blacklist Details
                                </button>
                            </form>
                        </li>
                        <%
                            }

                            if (moduleSet.contains(PartnerModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3">
                                    Whitelist Details
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>

                    </ul>
                    <%
                    }
                    else if ("BlockedMerchantList".equals(buttonvalue))
                    {%>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-wrench" style="float:left;"></i><span>Merchant Settings</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantfraudsetting.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantfraudsetting" name="submit" class="button3">
                                    Merchant Fraud Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_LIMIT_MANAGEMENT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/viewmerchantrisk.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="viewmerchantrisk" name="submit" class="button3">
                                    Merchant Limit Management
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_MAIL_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantmailaccess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantmailaccess" name="submit" class="button3">
                                    Merchant Mail Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_TRANSACTION_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/transactionsprocess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="transactionsprocess" name="submit" class="button3">
                                    Merchant Transaction Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BIN_ROUTING_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/binRouting.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="binrouting" name="submit" class="button3">
                                    Bin Routing Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BACK_OFFICE_ACCESS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/backofficeaccess.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="backofficeaccess" name="submit" class="button3">
                                    Merchant BackOffice Access
                                </button>
                            </form>
                        </li>
                        <%
                            }

                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/BlockedMerchantList?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="BlockedMerchantList" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant Unblocked Account
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_USER_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/MerchantUserUnblockedAccount?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="MerchantUserUnblockedAccount" name="submit"
                                        class="button3">
                                    Merchant User Unblocked
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/blacklist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3">
                                    Blacklist Details
                                </button>
                            </form>
                        </li>
                        <%
                            }

                            if (moduleSet.contains(PartnerModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3">
                                    Whitelist Details
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>

                    </ul>
                    <%
                    }
                    else if ("MerchantUserUnblockedAccount".equals(buttonvalue))
                    {%>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-wrench" style="float:left;"></i><span>Merchant Settings</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantfraudsetting.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantfraudsetting" name="submit" class="button3">
                                    Merchant Fraud Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_LIMIT_MANAGEMENT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/viewmerchantrisk.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="viewmerchantrisk" name="submit" class="button3">
                                    Merchant Limit Management
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_MAIL_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantmailaccess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantmailaccess" name="submit" class="button3">
                                    Merchant Mail Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_TRANSACTION_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/transactionsprocess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="transactionsprocess" name="submit" class="button3">
                                    Merchant Transaction Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BIN_ROUTING_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/binRouting.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="binrouting" name="submit" class="button3">
                                    Bin Routing Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BACK_OFFICE_ACCESS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/backofficeaccess.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="backofficeaccess" name="submit" class="button3">
                                    Merchant BackOffice Access
                                </button>
                            </form>
                        </li>
                        <%
                            }

                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/BlockedMerchantList?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="BlockedMerchantList" name="submit" class="button3">
                                    Merchant Unblocked Account
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_USER_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/MerchantUserUnblockedAccount?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="MerchantUserUnblockedAccount" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant User Unblocked
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/blacklist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3">
                                    Blacklist Details
                                </button>
                            </form>
                        </li>
                        <%
                            }

                            if (moduleSet.contains(PartnerModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3">
                                    Whitelist Details
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>

                    </ul>
                    <%
                    }
                    else if ("Black List".equals(buttonvalue))
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-wrench" style="float:left;"></i><span>Merchant Settings</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantfraudsetting.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantfraudsetting" name="submit" class="button3">
                                    Merchant Fraud Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_LIMIT_MANAGEMENT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/viewmerchantrisk.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="viewmerchantrisk" name="submit" class="button3">
                                    Merchant Limit Management
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_MAIL_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantmailaccess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantmailaccess" name="submit" class="button3">
                                    Merchant Mail Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_TRANSACTION_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/transactionsprocess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="transactionsprocess" name="submit" class="button3">
                                    Merchant Transaction Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BIN_ROUTING_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/binRouting.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="transactionsprocess" name="submit" class="button3">
                                    Bin Routing Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BACK_OFFICE_ACCESS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/backofficeaccess.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="backofficeaccess" name="submit" class="button3">
                                    Merchant BackOffice Access
                                </button>
                            </form>
                        </li>
                        <%
                            }

                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/BlockedMerchantList?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="BlockedMerchantList" name="submit" class="button3">
                                    Merchant Unblocked Account
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_USER_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/MerchantUserUnblockedAccount?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="MerchantUserUnblockedAccount" name="submit"
                                        class="button3">
                                    Merchant User Unblocked
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/blacklist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3" class="button3"
                                        style="background-color:#d9d9d9">
                                    Blacklist Details
                                </button>
                            </form>
                        </li>
                        <%
                            }

                            if (moduleSet.contains(PartnerModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3">
                                    Whitelist Details
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>

                    </ul>
                    <%
                    }

                    else if ("White List".equals(buttonvalue))
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-wrench" style="float:left;"></i><span>Merchant Settings</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantfraudsetting.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantfraudsetting" name="submit" class="button3">
                                    Merchant Fraud Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_LIMIT_MANAGEMENT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/viewmerchantrisk.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="viewmerchantrisk" name="submit" class="button3">
                                    Merchant Limit Management
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_MAIL_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantmailaccess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantmailaccess" name="submit" class="button3">
                                    Merchant Mail Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_TRANSACTION_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/transactionsprocess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="transactionsprocess" name="submit" class="button3">
                                    Merchant Transaction Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BIN_ROUTING_SETTINGS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/binRouting.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="binrouting" name="submit" class="button3">
                                    Bin Routing Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BACK_OFFICE_ACCESS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/backofficeaccess.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="backofficeaccess" name="submit" class="button3">
                                    Merchant BackOffice Access
                                </button>
                            </form>
                        </li>
                        <%
                            }

                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/BlockedMerchantList?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="BlockedMerchantList" name="submit" class="button3">
                                    Merchant Unblocked Account
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_USER_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/MerchantUserUnblockedAccount?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="MerchantUserUnblockedAccount" name="submit"
                                        class="button3">
                                    Merchant User Unblocked
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BLACKLIST_DETAILS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/blacklist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3">
                                    Blacklist Details
                                </button>
                            </form>
                        </li>
                        <%
                            }

                            if (moduleSet.contains(PartnerModuleEnum.WHITELIST_DETAILS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3" class="button3"
                                        style="background-color:#d9d9d9">
                                    Whitelist Details
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>

                    </ul>--%>
                    <%
                    }

                    else
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;"
                            class="button1">
                        <i class="fa fa-wrench" style="float:left;"></i><span>Merchant Settings</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul>
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_SETTINGS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/merchantfraudsetting.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantfraudsetting" name="submit" class="button3">
                                    Merchant Fraud Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_LIMIT_MANAGEMENT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/viewmerchantrisk.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="viewmerchantrisk" name="submit" class="button3">
                                    Merchant Limit Management
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_MAIL_SETTINGS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/merchantmailaccess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantmailaccess" name="submit" class="button3">
                                    Merchant Mail Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_TRANSACTION_SETTINGS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/transactionsprocess.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="transactionsprocess" name="submit" class="button3">
                                    Merchant Transaction Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if(moduleSet.contains(PartnerModuleEnum.CHECKOUT_CONFIGURATION.name()))
                            {
                        %>
                        <li>
                            <form action="/partner/checkoutConfiguration.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" name="ctoken" value="<%=ctoken%>">
                                <%
                                    if("checkout_configuration".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="checkout_configuration" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Checkout Configuration
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="checkout_configuration" name="submit" class="button3">
                                    Checkout Configuration
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BIN_ROUTING_SETTINGS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/binRouting.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="binrouting" name="submit" class="button3">
                                    Routing Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BACK_OFFICE_ACCESS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/backofficeaccess.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="backofficeaccess" name="submit" class="button3">
                                    Merchant BackOffice Access
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                        <%--<%

                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_UNBLOCKED_ACCOUNT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/net/BlockedMerchantList?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="BlockedMerchantList" name="submit" class="button3">
                                    Merchant Unblocked Account
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_USER_UNBLOCKED_ACCOUNT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/net/MerchantUserUnblockedAccount?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="MerchantUserUnblockedAccount" name="submit"
                                        class="button3">
                                    Merchant User Unblocked
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            %>--%>
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.BLACKLIST_DETAILS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/blacklist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Black List" name="submit" class="button3">
                                    Blacklist Details
                                </button>
                            </form>
                        </li>
                        <%
                            }

                            if (moduleSet.contains(PartnerModuleEnum.WHITELIST_DETAILS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/whitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="White List" name="submit" class="button3">
                                    Whitelist Details
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CALLBACK_SETTINGS.name()))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantcallbacksettings.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantcallbacksettings" name="submit" class="button3">
                                    Merchant Callback Settings
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.THEME_CONFIGURATION.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/net/MerchantTheme?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="MerchantTheme" name="submit" class="button3">
                                    Theme Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%
                        }
                    %>
                </li>
                <%
                    }
                %>
                <%--Start Application Manager--%>
                <%
                    if (moduleSet.contains(PartnerModuleEnum.APPLICATION_MANAGER.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                    {
                %>
                <li>
                    <%--<% if (buttonvalue == null)
                    {%>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;"
                            class="button1">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span>Application Manager</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>&lt;%&ndash;<span class="caret" style="float:right;"></span>&ndash;%&gt;
                    </button>
                    <ul style="margin-left: -10px">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.KYC_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/appKycTemplate.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="appKycTemplate" name="submit" class="button3">
                                    Kyc Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BANK_MAPPING.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantmappingbank.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantmappingbank" name="submit" class="button3">
                                    Merchant Bank Mapping
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.CONTRACTUAL_PARTNER.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/contractualPartner.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="contractualPartner" name="submit" class="button3">
                                    Contractual Partner
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_APPLICATION_FORM.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/listofapplicationmember.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="listofapplicationmember" name="submit" class="button3">
                                    Merchant Application Form
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_APPLICATION_PDF.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerbankdetailslist.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerbankdetailslist" name="submit" class="button3">
                                    Merchant Application PDF
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CONSOLIDATED_APPLICATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerconsolidatedapplication.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerconsolidatedapplication" name="submit"
                                        class="button3">
                                    Merchant Consolidated Application
                                </button>
                            </form>
                        </li>
                        &lt;%&ndash;ADd&ndash;%&gt;
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MANAGE_APPLICATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/manageBankApp.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="manageBankApp" name="submit" class="button3">
                                    Manage Application
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.CONSOLIDATED_APPLICATION_HISTORY.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/consolidatedHistory.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="consolidatedHistory" name="submit" class="button3">
                                    Consolidated Application History
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>--%>
                    <% /*}
                    else
                    {*/
                        if (buttonvalue!=null && ("appKycTemplate".equals(buttonvalue) ||"merchantmappingbank".equals(buttonvalue) || "contractualPartner".equals(buttonvalue) || "listofapplicationmember".equals(buttonvalue) || "partnerbankdetailslist".equals(buttonvalue) || "partnerconsolidatedapplication".equals(buttonvalue) || "manageBankApp".equals(buttonvalue) || "consolidatedHistory".equals(buttonvalue)))
                        {%>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff; margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span>Application Manager</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span><%--<span class="caret" style="float:right;"></span>--%>
                    </button>
                    <ul style="display: block;margin-left: -10px">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.KYC_CONFIGURATION.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/appKycTemplate.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if ("appKycTemplate".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="appKycTemplate" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Kyc Configuration
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="appKycTemplate" name="submit" class="button3">
                                    Kyc Configuration
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                        %>
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BANK_MAPPING.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/merchantmappingbank.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if ("merchantmappingbank".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="merchantmappingbank" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant Bank Mapping
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="merchantmappingbank" name="submit" class="button3">
                                    Merchant Bank Mapping
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.CONTRACTUAL_PARTNER.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/contractualPartner.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if ("contractualPartner".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="contractualPartner" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Contractual Partner
                                </button>
                                <%
                                }
                                else
                                {
                                %>

                                <button type="submit" value="contractualPartner" name="submit" class="button3">
                                    Contractual Partner
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_APPLICATION_FORM.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/listofapplicationmember.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if ("listofapplicationmember".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="listofapplicationmember" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant Application Form
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="listofapplicationmember" name="submit" class="button3">
                                    Merchant Application Form
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_APPLICATION_PDF.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerbankdetailslist.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if ("partnerbankdetailslist".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="partnerbankdetailslist" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant Application PDF
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="partnerbankdetailslist" name="submit" class="button3">
                                    Merchant Application PDF
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CONSOLIDATED_APPLICATION.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerconsolidatedapplication.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if ("partnerconsolidatedapplication".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="partnerconsolidatedapplication" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant Consolidated Application
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="partnerconsolidatedapplication" name="submit"
                                        class="button3">
                                    Merchant Consolidated Application
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MANAGE_APPLICATION.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/manageBankApp.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if ("manageBankApp".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="manageBankApp" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Manage Application
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="manageBankApp" name="submit" class="button3">
                                    Manage Application
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.CONSOLIDATED_APPLICATION_HISTORY.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/consolidatedHistory.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if ("consolidatedHistory".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="consolidatedHistory" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Consolidated Application History
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="consolidatedHistory" name="submit" class="button3">
                                    Consolidated Application History
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%--<% }
                    else
                    {
                        if ("merchantmappingbank".equals(buttonvalue))
                        {%>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff; margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span>Application Manager</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>&lt;%&ndash;<span class="caret" style="float:right;"></span>&ndash;%&gt;
                    </button>
                    <ul style="display: block;margin-left: -10px">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.KYC_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/appKycTemplate.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="appKycTemplate" name="submit" class="button3">
                                    Kyc Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BANK_MAPPING.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantmappingbank.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantmappingbank" name="submit" class="button3"
                                        style="background-color:#d9d9d9 ">
                                    Merchant Bank Mapping
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.CONTRACTUAL_PARTNER.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/contractualPartner.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="contractualPartner" name="submit" class="button3">
                                    Contractual Partner
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_APPLICATION_FORM.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/listofapplicationmember.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="listofapplicationmember" name="submit" class="button3">
                                    Merchant Application Form
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_APPLICATION_PDF.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerbankdetailslist.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerbankdetailslist" name="submit" class="button3">
                                    Merchant Application PDF
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CONSOLIDATED_APPLICATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerconsolidatedapplication.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerconsolidatedapplication" name="submit"
                                        class="button3">
                                    Merchant Consolidated Application
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MANAGE_APPLICATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/manageBankApp.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="manageBankApp" name="submit" class="button3">
                                    Manage Application
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.CONSOLIDATED_APPLICATION_HISTORY.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/consolidatedHistory.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="consolidatedHistory" name="submit" class="button3">
                                    Consolidated Application History
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>


                    </ul>
                    <% }
                    else
                    {
                        if ("contractualPartner".equals(buttonvalue))
                        {%>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff; margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span>Application Manager</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>&lt;%&ndash;<span class="caret" style="float:right;"></span>&ndash;%&gt;
                    </button>
                    <ul style="display: block;margin-left: -10px">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.KYC_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/appKycTemplate.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="appKycTemplate" name="submit" class="button3">
                                    Kyc Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BANK_MAPPING.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantmappingbank.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantmappingbank" name="submit" class="button3">
                                    Merchant Bank Mapping
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.CONTRACTUAL_PARTNER.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/contractualPartner.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="contractualPartner" name="submit" class="button3"
                                        style="background-color:#d9d9d9 ">
                                    Contractual Partner
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_APPLICATION_FORM.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/listofapplicationmember.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="listofapplicationmember" name="submit" class="button3">
                                    Merchant Application Form
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_APPLICATION_PDF.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerbankdetailslist.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerbankdetailslist" name="submit" class="button3">
                                    Merchant Application PDF
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CONSOLIDATED_APPLICATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerconsolidatedapplication.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerconsolidatedapplication" name="submit"
                                        class="button3">
                                    Merchant Consolidated Application
                                </button>
                            </form>
                        </li>
                        &lt;%&ndash;ADd&ndash;%&gt;
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MANAGE_APPLICATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/manageBankApp.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="manageBankApp" name="submit" class="button3">
                                    Manage Application
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.CONSOLIDATED_APPLICATION_HISTORY.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/consolidatedHistory.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="consolidatedHistory" name="submit" class="button3">
                                    Consolidated Application History
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%
                    }
                    else
                    {
                        if ("listofapplicationmember".equals(buttonvalue))
                        {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff; margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span>Application Manager</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>&lt;%&ndash;<span class="caret" style="float:right;"></span>&ndash;%&gt;
                    </button>
                    <ul style="display: block;margin-left: -10px">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.KYC_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/appKycTemplate.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="appKycTemplate" name="submit" class="button3">
                                    Kyc Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BANK_MAPPING.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantmappingbank.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantmappingbank" name="submit" class="button3">
                                    Merchant Bank Mapping
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.CONTRACTUAL_PARTNER.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/contractualPartner.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="contractualPartner" name="submit" class="button3">
                                    Contractual Partner
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_APPLICATION_FORM.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/listofapplicationmember.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="listofapplicationmember" name="submit" class="button3"
                                        style="background-color:#d9d9d9 ">
                                    Merchant Application Form
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_APPLICATION_PDF.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerbankdetailslist.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerbankdetailslist" name="submit" class="button3">
                                    Merchant Application PDF
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CONSOLIDATED_APPLICATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerconsolidatedapplication.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerconsolidatedapplication" name="submit"
                                        class="button3">
                                    Merchant Consolidated Application
                                </button>
                            </form>
                        </li>
                        &lt;%&ndash;ADd&ndash;%&gt;
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MANAGE_APPLICATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/manageBankApp.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="manageBankApp" name="submit" class="button3">
                                    Manage Application
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.CONSOLIDATED_APPLICATION_HISTORY.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/consolidatedHistory.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="consolidatedHistory" name="submit" class="button3">
                                    Consolidated Application History
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%
                    }
                    else
                    {
                        if ("partnerbankdetailslist".equals(buttonvalue))
                        {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff; margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span>Application Manager</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: block;margin-left: -10px">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.KYC_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/appKycTemplate.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="appKycTemplate" name="submit" class="button3">
                                    Kyc Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BANK_MAPPING.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantmappingbank.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantmappingbank" name="submit" class="button3">
                                    Merchant Bank Mapping
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.CONTRACTUAL_PARTNER.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/contractualPartner.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="contractualPartner" name="submit" class="button3">
                                    Contractual Partner
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_APPLICATION_FORM.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/listofapplicationmember.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="listofapplicationmember" name="submit" class="button3">
                                    Merchant Application Form
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_APPLICATION_PDF.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerbankdetailslist.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerbankdetailslist" name="submit" class="button3"
                                        style="background-color:#d9d9d9 ">
                                    Merchant Application PDF
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CONSOLIDATED_APPLICATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerconsolidatedapplication.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerconsolidatedapplication" name="submit"
                                        class="button3">
                                    Merchant Consolidated Application
                                </button>
                            </form>
                        </li>
                        &lt;%&ndash;ADd&ndash;%&gt;
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MANAGE_APPLICATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/manageBankApp.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="manageBankApp" name="submit" class="button3">
                                    Manage Application
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.CONSOLIDATED_APPLICATION_HISTORY.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/consolidatedHistory.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="consolidatedHistory" name="submit" class="button3">
                                    Consolidated Application History
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%
                    }
                    else
                    {
                        if ("partnerconsolidatedapplication".equals(buttonvalue))
                        {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff; margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span>Application Manager</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: block;margin-left: -10px">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.KYC_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/appKycTemplate.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="appKycTemplate" name="submit" class="button3">
                                    Kyc Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BANK_MAPPING.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantmappingbank.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantmappingbank" name="submit" class="button3">
                                    Merchant Bank Mapping
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.CONTRACTUAL_PARTNER.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/contractualPartner.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="contractualPartner" name="submit" class="button3">
                                    Contractual Partner
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_APPLICATION_FORM.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/listofapplicationmember.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="listofapplicationmember" name="submit" class="button3">
                                    Merchant Application Form
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_APPLICATION_PDF.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerbankdetailslist.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerbankdetailslist" name="submit" class="button3">
                                    Merchant Application PDF
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CONSOLIDATED_APPLICATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerconsolidatedapplication.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerconsolidatedapplication" name="submit"
                                        class="button3" style="background-color:#d9d9d9 ">
                                    Merchant Consolidated Application
                                </button>
                            </form>
                        </li>
                        &lt;%&ndash;ADd&ndash;%&gt;
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MANAGE_APPLICATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/manageBankApp.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="manageBankApp" name="submit" class="button3">
                                    Manage Application
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.CONSOLIDATED_APPLICATION_HISTORY.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/consolidatedHistory.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="consolidatedHistory" name="submit" class="button3">
                                    Consolidated Application History
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%
                    }
                    else
                    {
                        if ("manageBankApp".equals(buttonvalue))
                        {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff; margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span>Application Manager</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: block;margin-left: -10px">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.KYC_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/appKycTemplate.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="appKycTemplate" name="submit" class="button3">
                                    Kyc Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BANK_MAPPING.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantmappingbank.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantmappingbank" name="submit" class="button3">
                                    Merchant Bank Mapping
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.CONTRACTUAL_PARTNER.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/contractualPartner.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="contractualPartner" name="submit" class="button3">
                                    Contractual Partner
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_APPLICATION_FORM.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/listofapplicationmember.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="listofapplicationmember" name="submit" class="button3">
                                    Merchant Application Form
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_APPLICATION_PDF.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerbankdetailslist.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerbankdetailslist" name="submit" class="button3">
                                    Merchant Application PDF
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CONSOLIDATED_APPLICATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerconsolidatedapplication.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerconsolidatedapplication" name="submit"
                                        class="button3">
                                    Merchant Consolidated Application
                                </button>
                            </form>
                        </li>
                        &lt;%&ndash;ADd&ndash;%&gt;
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MANAGE_APPLICATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/manageBankApp.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="manageBankApp" name="submit" class="button3"
                                        style="background-color:#d9d9d9 ">
                                    Manage Application
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.CONSOLIDATED_APPLICATION_HISTORY.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/consolidatedHistory.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="consolidatedHistory" name="submit" class="button3">
                                    Consolidated Application History
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%
                    }
                    else
                    {
                        if ("consolidatedHistory".equals(buttonvalue))
                        {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff; margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span>Application Manager</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: block;margin-left: -10px">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.KYC_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/appKycTemplate.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="appKycTemplate" name="submit" class="button3">
                                    Kyc Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BANK_MAPPING.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/merchantmappingbank.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantmappingbank" name="submit" class="button3">
                                    Merchant Bank Mapping
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.CONTRACTUAL_PARTNER.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/contractualPartner.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="contractualPartner" name="submit" class="button3">
                                    Contractual Partner
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_APPLICATION_FORM.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/listofapplicationmember.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="listofapplicationmember" name="submit" class="button3">
                                    Merchant Application Form
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_APPLICATION_PDF.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerbankdetailslist.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerbankdetailslist" name="submit" class="button3">
                                    Merchant Application PDF
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CONSOLIDATED_APPLICATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerconsolidatedapplication.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerconsolidatedapplication" name="submit"
                                        class="button3">
                                    Merchant Consolidated Application
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MANAGE_APPLICATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/manageBankApp.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="manageBankApp" name="submit" class="button3">
                                    Manage Application
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.CONSOLIDATED_APPLICATION_HISTORY.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/consolidatedHistory.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="consolidatedHistory" name="submit" class="button3"
                                        style="background-color:#d9d9d9 ">
                                    Consolidated Application History
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>--%>
                    <%
                    }
                    else
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;"
                            class="button1">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span>Application Manager</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="margin-left: -10px">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.KYC_CONFIGURATION.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/appKycTemplate.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="appKycTemplate" name="submit" class="button3">
                                    Kyc Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_BANK_MAPPING.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/merchantmappingbank.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantmappingbank" name="submit" class="button3">
                                    Merchant Bank Mapping
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.CONTRACTUAL_PARTNER.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/contractualPartner.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="contractualPartner" name="submit" class="button3">
                                    Contractual Partner
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_APPLICATION_FORM.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/listofapplicationmember.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="listofapplicationmember" name="submit" class="button3">
                                    Merchant Application Form
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_APPLICATION_PDF.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerbankdetailslist.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerbankdetailslist" name="submit" class="button3">
                                    Merchant Application PDF
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_CONSOLIDATED_APPLICATION.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerconsolidatedapplication.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerconsolidatedapplication" name="submit"
                                        class="button3">
                                    Merchant Consolidated Application
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MANAGE_APPLICATION.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/manageBankApp.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="manageBankApp" name="submit" class="button3">
                                    Manage Application
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.CONSOLIDATED_APPLICATION_HISTORY.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/consolidatedHistory.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="consolidatedHistory" name="submit" class="button3">
                                    Consolidated Application History
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%
                        }
                    %>
                </li>
                <%
                    }
                %>
                <%--End Application Manager--%>
                <%
                    if (moduleSet.contains(PartnerModuleEnum.FRAUD_MANAGEMENT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                    {
                %>
                <li>
                    <%--<% if (buttonvalue == null)
                    {%>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;"
                            class="button1">
                        <i class="fa fa-shield" style="float:left;"></i><span>Fraud Management</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:none;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.INTERNAL_FRAUD_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/InternalFraudConfiguration?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Internal Fraud Configuration" name="submit" class="button3">
                                    Internal Fraud Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_RULE_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerMerchantFraudRule.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3">
                                    Fraud Rule Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_ACCOUNTS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/PartnerMerchantFraudAccountList?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Fraud Accounts" name="submit" class="button3">
                                    Merchant Fraud Accounts
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_TRANSACTION_LIST.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudTransactionList.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Transactions List" name="submit" class="button3">
                                    Fraud Transaction List
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_KYC_LIST.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudKycList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Kyc List" name="submit" class="button3">
                                    Fraud KYC List
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_UPLOAD.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/markFraudTransaction.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Upload" name="submit" class="button3">
                                    Fraud Upload
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_INTIMATION_ACTION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudNotification.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Intimation & Action" name="submit" class="button3">
                                    Fraud Intimation & Action
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>--%>
                    <%
                        /* }
                         else
                         {*/
                        if (buttonvalue!=null && ("Internal Fraud Configuration".equals(buttonvalue) || "Fraud Rule Configuration".equals(buttonvalue) || "Merchant Fraud Accounts".equals(buttonvalue) || "Fraud Transactions List".equals(buttonvalue) || "Fraud Kyc List".equals(buttonvalue) || "Fraud Upload".equals(buttonvalue) || "Fraud Intimation & Action".equals(buttonvalue) || "Fraud System Account Master".equals(buttonvalue)))
                        {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-shield" style="float:left;"></i><span>Fraud Management</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_ACCOUNT_MASTER.name()) || (user.getRoles().contains("superpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudSystemAccountList.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("Fraud System Account Master".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="Fraud System Account Master" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Fraud System Account Master
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="Fraud System Account Master" name="submit" class="button3">
                                    Fraud System Account Master
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.DEFAULT_INTERNAL_RULES.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/net/InternalFraudConfiguration?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if ("Internal Fraud Configuration".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="Internal Fraud Configuration" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Default Internal Rules
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="Internal Fraud Configuration" name="submit" class="button3">
                                    Default Internal Rules
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_RULES.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerMerchantFraudRule.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if ("Fraud Rule Configuration".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Merchant Fraud Rules
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3">
                                    Merchant Fraud Rules
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_ACCOUNTS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/net/PartnerMerchantFraudAccountList?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if ("Merchant Fraud Accounts".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="Merchant Fraud Accounts" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Merchant Fraud Accounts
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="Merchant Fraud Accounts" name="submit" class="button3">
                                    Merchant Fraud Accounts
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_TRANSACTION_LIST.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/fraudTransactionList.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if ("Fraud Transactions List".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="Fraud Transactions List" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Fraud Transaction List
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="Fraud Transactions List" name="submit" class="button3">
                                    Fraud Transaction List
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_KYC_LIST.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/fraudKycList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if ("Fraud Kyc List".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="Fraud Kyc List" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Fraud KYC List
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="Fraud Kyc List" name="submit" class="button3">
                                    Fraud KYC List
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_UPLOAD.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/markFraudTransaction.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if ("Fraud Upload".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="Fraud Upload" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Fraud Upload
                                </button>
                                <%
                                }
                                else
                                {
                                %>

                                <button type="submit" value="Fraud Upload" name="submit" class="button3">
                                    Fraud Upload
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_INTIMATION_ACTION.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/fraudNotification.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if ("Fraud Intimation & Action".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="Fraud Intimation & Action" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Fraud Intimation & Action
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="Fraud Intimation & Action" name="submit" class="button3">
                                    Fraud Intimation & Action
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%--<%
                    }
                    else if ("Fraud Rule Configuration".equals(buttonvalue))
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-shield" style="float:left;"></i><span>Fraud Management</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.INTERNAL_FRAUD_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/InternalFraudConfiguration?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Internal Fraud Configuration" name="submit" class="button3">
                                    Internal Fraud Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_RULE_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerMerchantFraudRule.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Fraud Rule Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_ACCOUNTS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/PartnerMerchantFraudAccountList?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Fraud Accounts" name="submit" class="button3">
                                    Merchant Fraud Accounts
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_TRANSACTION_LIST.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudTransactionList.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Transactions List" name="submit" class="button3">
                                    Fraud Transaction List
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_KYC_LIST.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudKycList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Kyc List" name="submit" class="button3">
                                    Fraud KYC List
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_UPLOAD.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/markFraudTransaction.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Upload" name="submit" class="button3">
                                    Fraud Upload
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_INTIMATION_ACTION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudNotification.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Intimation & Action" name="submit" class="button3">
                                    Fraud Intimation & Action
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%
                    }
                    else if ("Merchant Fraud Accounts".equals(buttonvalue))
                    {%>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-shield" style="float:left;"></i><span>Fraud Management</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.INTERNAL_FRAUD_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/InternalFraudConfiguration?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Internal Fraud Configuration" name="submit" class="button3">
                                    Internal Fraud Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_RULE_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerMerchantFraudRule.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3">
                                    Fraud Rule Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_ACCOUNTS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/PartnerMerchantFraudAccountList?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Fraud Accounts" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant Fraud Accounts
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_TRANSACTION_LIST.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudTransactionList.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Transactions List" name="submit" class="button3">
                                    Fraud Transaction List
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_KYC_LIST.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudKycList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Kyc List" name="submit" class="button3">
                                    Fraud KYC List
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_UPLOAD.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/markFraudTransaction.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Upload" name="submit" class="button3">
                                    Fraud Upload
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_INTIMATION_ACTION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudNotification.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Intimation & Action" name="submit" class="button3">
                                    Fraud Intimation & Action
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>

                    </ul>
                    <%
                    }
                    else if ("Fraud Transactions List".equals(buttonvalue))
                    {%>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-shield" style="float:left;"></i><span>Fraud Management</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.INTERNAL_FRAUD_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/InternalFraudConfiguration?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Internal Fraud Configuration" name="submit" class="button3">
                                    Internal Fraud Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_RULE_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerMerchantFraudRule.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3">
                                    Fraud Rule Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_ACCOUNTS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/PartnerMerchantFraudAccountList?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Fraud Accounts" name="submit" class="button3">
                                    Merchant Fraud Accounts
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_TRANSACTION_LIST.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudTransactionList.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Transactions List" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Fraud Transaction List
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_KYC_LIST.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudKycList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Kyc List" name="submit" class="button3">
                                    Fraud KYC List
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_UPLOAD.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/markFraudTransaction.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Upload" name="submit" class="button3">
                                    Fraud Upload
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_INTIMATION_ACTION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudNotification.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Intimation & Action" name="submit" class="button3">
                                    Fraud Intimation & Action
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%
                    }
                    else if ("Fraud Kyc List".equals(buttonvalue))
                    {%>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-shield" style="float:left;"></i><span>Fraud Management</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.INTERNAL_FRAUD_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/InternalFraudConfiguration?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Internal Fraud Configuration" name="submit" class="button3">
                                    Internal Fraud Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_RULE_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerMerchantFraudRule.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3">
                                    Fraud Rule Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_ACCOUNTS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/PartnerMerchantFraudAccountList?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Fraud Accounts" name="submit" class="button3">
                                    Merchant Fraud Accounts
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_TRANSACTION_LIST.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudTransactionList.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Transactions List" name="submit" class="button3">
                                    Fraud Transaction List
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_KYC_LIST.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudKycList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Kyc List" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Fraud KYC List
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_UPLOAD.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/markFraudTransaction.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Upload" name="submit" class="button3">
                                    Fraud Upload
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_INTIMATION_ACTION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudNotification.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Intimation & Action" name="submit" class="button3">
                                    Fraud Intimation & Action
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%
                    }
                    else if ("Fraud Upload".equals(buttonvalue))
                    {%>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-shield" style="float:left;"></i><span>Fraud Management</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.INTERNAL_FRAUD_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/InternalFraudConfiguration?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Internal Fraud Configuration" name="submit" class="button3">
                                    Internal Fraud Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_RULE_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerMerchantFraudRule.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3">
                                    Fraud Rule Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_ACCOUNTS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/PartnerMerchantFraudAccountList?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Fraud Accounts" name="submit" class="button3">
                                    Merchant Fraud Accounts
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_TRANSACTION_LIST.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudTransactionList.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Transactions List" name="submit" class="button3">
                                    Fraud Transaction List
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_KYC_LIST.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudKycList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Kyc List" name="submit" class="button3">
                                    Fraud KYC List
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_UPLOAD.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/markFraudTransaction.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Upload" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Fraud Upload
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_INTIMATION_ACTION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudNotification.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Intimation & Action" name="submit" class="button3">
                                    Fraud Intimation & Action
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%
                    }
                    else if ("Fraud Intimation & Action".equals(buttonvalue))
                    {%>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-shield" style="float:left;"></i><span>Fraud Management</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.INTERNAL_FRAUD_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/InternalFraudConfiguration?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Internal Fraud Configuration" name="submit" class="button3">
                                    Internal Fraud Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_RULE_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerMerchantFraudRule.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3">
                                    Fraud Rule Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_ACCOUNTS.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/PartnerMerchantFraudAccountList?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Fraud Accounts" name="submit" class="button3">
                                    Merchant Fraud Accounts
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_TRANSACTION_LIST.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudTransactionList.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Transactions List" name="submit" class="button3">
                                    Fraud Transaction List
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_KYC_LIST.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudKycList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Kyc List" name="submit" class="button3">
                                    Fraud KYC List
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_UPLOAD.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/markFraudTransaction.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Upload" name="submit" class="button3">
                                    Fraud Upload
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_INTIMATION_ACTION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudNotification.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Intimation & Action" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Fraud Intimation & Action
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul> --%>
                    <%
                    }
                    else
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;"
                            class="button1">
                        <i class="fa fa-shield" style="float:left;"></i>
                        <span>Fraud Management</span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:none;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_ACCOUNT_MASTER.name()) || (user.getRoles().contains("superpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/fraudSystemAccountList.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud System Account Master" name="submit" class="button3">
                                    Fraud System Account Master
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.DEFAULT_INTERNAL_RULES.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/net/InternalFraudConfiguration?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Internal Fraud Configuration" name="submit" class="button3">
                                    Default Internal Rules
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_RULES.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerMerchantFraudRule.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Rule Configuration" name="submit" class="button3">
                                    Merchant Fraud Rules
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_FRAUD_ACCOUNTS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/net/PartnerMerchantFraudAccountList?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Merchant Fraud Accounts" name="submit" class="button3">
                                    Merchant Fraud Accounts
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_TRANSACTION_LIST.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/fraudTransactionList.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Transactions List" name="submit" class="button3">
                                    Fraud Transaction List
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_KYC_LIST.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/fraudKycList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Kyc List" name="submit" class="button3">
                                    Fraud KYC List
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_UPLOAD.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/markFraudTransaction.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Upload" name="submit" class="button3">
                                    Fraud Upload
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.FRAUD_INTIMATION_ACTION.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/fraudNotification.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Fraud Intimation & Action" name="submit" class="button3">
                                    Fraud Intimation & Action
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%
                        }
                    %>
                </li>
                <%--&lt;%&ndash;End fraud Management&ndash;%&gt;--%>
                <%
                    }
                    if (moduleSet.contains(PartnerModuleEnum.MERCHANT_MONITORING.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                    {
                %>
                <li>
                    <%--<% if (buttonvalue == null)
                    {%>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left; margin-top: 0;"
                            class="button1">
                        <i class="fa fa-line-chart" style="float:left;"></i><span>Merchant Monitoring</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:none;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.RISK_RULE_MAPPING.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerRiskRuleMapping.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Risk Rule Mapping" name="submit" class="button3">
                                    Risk Rule Mapping
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.RISK_RULE_GRAPH.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerRiskRuleGraph.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Risk Rule Graph" name="submit" class="button3">
                                    Risk Rule Graph
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.RULE_LOG.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerMonitoringRuleLog.jsp?ctoken=<%=ctoken%>">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Monitoring Rule Logs" name="submit" class="button3">
                                    Rule Logs
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>--%>
                    <%
                        /* }
                         else
                         {*/
                        if (buttonvalue!=null && ("Risk Rule Mapping".equals(buttonvalue) || "Risk Rule Graph".equals(buttonvalue) || "Monitoring Rule Logs".equals(buttonvalue)))
                        {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left; margin-top: 0;"
                            class="button1 active">
                        <i class="fa fa-line-chart" style="float:left;"></i><span>Merchant Monitoring</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.RISK_RULE_MAPPING.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerRiskRuleMapping.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("Risk Rule Mapping".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="Risk Rule Mapping" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Risk Rule Mapping
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="Risk Rule Mapping" name="submit" class="button3">
                                    Risk Rule Mapping
                                </button>
                                <%
                                    }
                                %>

                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.RISK_RULE_GRAPH.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerRiskRuleGraph.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("Risk Rule Graph".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="Risk Rule Graph" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Risk Rule Graph
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="Risk Rule Graph" name="submit" class="button3">
                                    Risk Rule Graph
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.RULE_LOG.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerMonitoringRuleLog.jsp?ctoken=<%=ctoken%>">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("Monitoring Rule Logs".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="Monitoring Rule Logs" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Rule Logs
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="Monitoring Rule Logs" name="submit" class="button3">
                                    Rule Logs
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>

                        <%
                            }
                        %>
                    </ul>
                    <%-- <%
                     }
                     else if ("Risk Rule Graph".equals(buttonvalue))
                     {
                     %>
                     <button onclick="window.location.href='#'" id="clicker"
                             style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                             class="button1 active">
                         <i class="fa fa-line-chart" style="float:left;"></i><span>Merchant Monitoring</span><span
                             class="pull-right"><i class="fa fa-caret-down"></i></span>
                     </button>
                     <ul style="display:block;">
                         <%
                             if (moduleSet.contains(PartnerModuleEnum.RISK_RULE_MAPPING.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerRiskRuleMapping.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="Risk Rule Mapping" name="submit" class="button3">
                                     Risk Rule Mapping
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.RISK_RULE_GRAPH.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerRiskRuleGraph.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="Risk Rule Graph" name="submit" class="button3"
                                         style="background-color:#d9d9d9">
                                     Risk Rule Graph
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.RULE_LOG.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerMonitoringRuleLog.jsp?ctoken=<%=ctoken%>">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="Monitoring Rule Logs" name="submit" class="button3">
                                     Rule Logs
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                         %>
                     </ul>
                     <%
                     }
                     else if ("Monitoring Rule Logs".equals(buttonvalue))
                     {
                     %>
                     <button onclick="window.location.href='#'" id="clicker"
                             style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                             class="button1 active">
                         <i class="fa fa-line-chart" style="float:left;"></i><span>Merchant Monitoring</span><span
                             class="pull-right"><i class="fa fa-caret-down"></i></span>
                     </button>
                     <ul style="display:block;">
                         <%
                             if (moduleSet.contains(PartnerModuleEnum.RISK_RULE_MAPPING.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerRiskRuleMapping.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="Risk Rule Mapping" name="submit" class="button3">
                                     Risk Rule Mapping
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.RISK_RULE_GRAPH.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerRiskRuleGraph.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="Risk Rule Graph" name="submit" class="button3">
                                     Risk Rule Graph
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.RULE_LOG.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>

                         <li>
                             <form action="/partner/partnerMonitoringRuleLog.jsp?ctoken=<%=ctoken%>">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="Monitoring Rule Logs" name="submit" class="button3"
                                         style="background-color:#d9d9d9">
                                     Rule Logs
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                         %>
                     </ul>--%>


                    <%
                    }
                    else
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left; margin-top: 0;"
                            class="button1">
                        <i class="fa fa-line-chart" style="float:left;"></i><span>Merchant Monitoring</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul>
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.RISK_RULE_MAPPING.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerRiskRuleMapping.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom:0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Risk Rule Mapping" name="submit" class="button3">
                                    Risk Rule Mapping
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.RISK_RULE_GRAPH.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerRiskRuleGraph.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Risk Rule Graph" name="submit" class="button3">
                                    Risk Rule Graph
                                </button>
                            </form>
                        </li>

                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.RULE_LOG.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerMonitoringRuleLog.jsp?ctoken=<%=ctoken%>">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Monitoring Rule Logs" name="submit" class="button3">
                                    Rule Logs
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%
                        }
                    %>
                </li>
                <%
                    }
                    if (moduleSet.contains(PartnerModuleEnum.PARTNER.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                    {
                %>
                <li>
                    <%--<% if (buttonvalue == null)
                    {%>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;"
                            class="button1">
                        <i class="fa fa-users" style="float:left;"></i><span>Partner</span><span class="pull-right"><i
                            class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:none;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.PARTNER_MASTER.name()) || (user.getRoles().contains("superpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerlist.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerlist" name="submit" class="button3">
                                    Partner Master
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.PAYOUT_REPORTS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerWireReports.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="PartnerReports" name="submit" class="button3">
                                    Payout Reports
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.PROFILE.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/PartnerDetailsConfig?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="PartnerDetailsConfig" name="submit" class="button3">
                                    Profile
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.THEME_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/MerchantTheme?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="MerchantTheme" name="submit" class="button3">
                                    Theme Configuration
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.GENERATE_SECRET_KEY.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/generatePartnerKey.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="generatePartnerKey" name="submit" class="button3">
                                    Generate Secret Key
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.PARTNER_USER_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/net/PartnerUserBlockedList?ctoken=<%=ctoken%>">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerUserUnblockedAccount" name="submit" class="button3">
                                    Partner's Users Blocked Account
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>.
                    </ul>--%>
                    <%
                        /* }
                         else*/ if (buttonvalue!=null && ("partnerlist".equals(buttonvalue) || "PartnerReports".equals(buttonvalue) || "PartnerDetailsConfig".equals(buttonvalue) || "MerchantTheme".equals(buttonvalue) || "generatePartnerKey".equals(buttonvalue) || "partnerUserUnblockedAccount".equals(buttonvalue) || "partnerpreference".equals(buttonvalue) || "partnerEmailSetting".equals(buttonvalue) || "partnerIpWhitelist".equals(buttonvalue) || "activityTracker".equals(buttonvalue)))
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-users" style="float:left;"></i><span>Partner</span><span class="pull-right"><i
                            class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">

                        <%
                            String partnerstatus =null;
                            if(user.getRoles().contains("superpartner") || (user.getRoles().contains("childsuperpartner"))){
                                partnerstatus = "true";
                            }
                            else
                                partnerstatus = "false";
                            if (moduleSet.contains(PartnerModuleEnum.PARTNER_MASTER.name()) && partnerstatus.equals("true"))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerlist.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("partnerlist".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="partnerlist" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Partner Master
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="partnerlist" name="submit" class="button3">
                                    Partner Master
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.PARTNER_DEFAULT_CONFIGURATION.name()) && partnerstatus.equals("true"))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerpreference.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("partnerpreference".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="partnerpreference" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Partner Default Configuration
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="partnerpreference" name="submit" class="button3">
                                    Partner Default Configuration
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.PARTNER_EMAIL_SETTING.name()) && partnerstatus.equals("true"))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerEmailSetting.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("partnerEmailSetting".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="partnerEmailSetting" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Partner Email Settings
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="partnerEmailSetting" name="submit" class="button3">
                                    Partner Email Settings
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.PAYOUT_REPORTS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerWireReports.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("PartnerReports".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="PartnerReports" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Payout Reports
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="PartnerReports" name="submit" class="button3">
                                    Payout Reports
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.PROFILE.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/net/PartnerDetailsConfig?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("PartnerDetailsConfig".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="PartnerDetailsConfig" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Profile
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="PartnerDetailsConfig" name="submit" class="button3">
                                    Profile
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                           /* if (moduleSet.contains(PartnerModuleEnum.THEME_CONFIGURATION.name()) *//*|| (!user.getRoles().contains("subpartner"))*//*)
                            {*/
                        %>
                        <%--<li>
                            <form action="/partner/net/MerchantTheme?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("MerchantTheme".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="MerchantTheme" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Theme Configuration
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="MerchantTheme" name="submit" class="button3">
                                    Theme Configuration
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>--%>
                        <%
                           // }
                            if (moduleSet.contains(PartnerModuleEnum.GENERATE_SECRET_KEY.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/generatePartnerKey.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("generatePartnerKey".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="generatePartnerKey" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Generate Secret Key
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="generatePartnerKey" name="submit" class="button3">
                                    Generate Secret Key
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                        %>
                        <%--<%
                            if (moduleSet.contains(PartnerModuleEnum.PARTNER_USER_UNBLOCKED_ACCOUNT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/net/PartnerUserBlockedList?ctoken=<%=ctoken%>">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("partnerUserUnblockedAccount".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="partnerUserUnblockedAccount" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Blocked Partner's user Account
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="partnerUserUnblockedAccount" name="submit" class="button3">
                                    Blocked Partner's user Account
                                </button>

                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            %>--%>
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.PARTNER_WHITELIST_DETAILS.name()) && partnerstatus.equals("true"))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerIpWhitelist.jsp?ctoken=<%=ctoken%>"  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("partnerIpWhitelist".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="partnerIpWhitelist" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Partner's WhiteList Details
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="partnerIpWhitelist" name="submit" class="button3">
                                    Partner's WhiteList Details
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if(moduleSet.contains(PartnerModuleEnum.ACTIVITY_TRACKER.name()))
                            {
                        %>
                        <li>
                            <form action="/partner/activityTracker.jsp?ctoken=<%=ctoken%>">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("activityTracker".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="activityTracker" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Activity Tracker
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="activityTracker" name="submit" class="button3">
                                    Activity Tracker
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%-- <%
                         }
                         else if ("PartnerReports".equals(buttonvalue))
                         {
                     %>
                     <button onclick="window.location.href='#'" id="clicker"
                             style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                             class="button1 active">
                         <i class="fa fa-users" style="float:left;"></i><span>Partner</span><span class="pull-right"><i
                             class="fa fa-caret-down"></i></span>
                     </button>
                     <ul style="display:block;">

                         <%
                             if (moduleSet.contains(PartnerModuleEnum.PARTNER_MASTER.name()) || (user.getRoles().contains("superpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerlist.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="partnerlist" name="submit" class="button3">
                                     Partner Master
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.PAYOUT_REPORTS.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerWireReports.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="PartnerReports" name="submit" class="button3"
                                         style="background-color:#d9d9d9">
                                     Payout Reports
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.PROFILE.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/net/PartnerDetailsConfig?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="PartnerDetailsConfig" name="submit" class="button3">
                                     Profile
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.THEME_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/net/MerchantTheme?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="MerchantTheme" name="submit" class="button3">
                                     Theme Configuration
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.GENERATE_SECRET_KEY.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/generatePartnerKey.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="generatePartnerKey" name="submit" class="button3">
                                     Generate Secret Key
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.PARTNER_USER_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/net/PartnerUserBlockedList?ctoken=<%=ctoken%>">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="partnerUserUnblockedAccount" name="submit" class="button3">
                                     Blocked Partner's user Account
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                         %>
                     </ul>
                     <%
                     }
                     else if ("PartnerDetailsConfig".equals(buttonvalue))
                     {
                     %>
                     <button onclick="window.location.href='#'" id="clicker"
                             style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                             class="button1 active">
                         <i class="fa fa-users" style="float:left;"></i><span>Partner</span><span class="pull-right"><i
                             class="fa fa-caret-down"></i></span>
                     </button>
                     <ul style="display:block;">
                         <%
                             if (moduleSet.contains(PartnerModuleEnum.PARTNER_MASTER.name()) || (user.getRoles().contains("superpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerlist.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="partnerlist" name="submit" class="button3">
                                     Partner Master
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.PAYOUT_REPORTS.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerWireReports.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="PartnerReports" name="submit" class="button3">
                                     Payout Reports
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.PROFILE.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/net/PartnerDetailsConfig?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="PartnerDetailsConfig" name="submit" class="button3"
                                         style="background-color:#d9d9d9">
                                     Profile
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.THEME_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/net/MerchantTheme?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="MerchantTheme" name="submit" class="button3">
                                     Theme Configuration
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.GENERATE_SECRET_KEY.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/generatePartnerKey.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="generatePartnerKey" name="submit" class="button3">
                                     Generate Secret Key
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.PARTNER_USER_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/net/PartnerUserBlockedList?ctoken=<%=ctoken%>">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="partnerUserUnblockedAccount" name="submit" class="button3">
                                     Blocked Partner's user Account
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                         %>
                     </ul>
                     <%
                     }
                     else if ("MerchantTheme".equals(buttonvalue))
                     {
                     %>
                     <button onclick="window.location.href='#'" id="clicker"
                             style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                             class="button1 active">
                         <i class="fa fa-users" style="float:left;"></i><span>Partner</span><span class="pull-right"><i
                             class="fa fa-caret-down"></i></span>
                     </button>
                     <ul style="display:block;">

                         <%
                             if (moduleSet.contains(PartnerModuleEnum.PARTNER_MASTER.name()) || (user.getRoles().contains("superpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerlist.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="partnerlist" name="submit" class="button3">
                                     Partner Master
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.PAYOUT_REPORTS.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerWireReports.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="PartnerReports" name="submit" class="button3">
                                     Payout Reports
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.PROFILE.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/net/PartnerDetailsConfig?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="PartnerDetailsConfig" name="submit" class="button3">
                                     Profile
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.THEME_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/net/MerchantTheme?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="MerchantTheme" name="submit" class="button3"
                                         style="background-color:#d9d9d9">
                                     Theme Configuration
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.GENERATE_SECRET_KEY.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/generatePartnerKey.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="generatePartnerKey" name="submit" class="button3">
                                     Generate Secret Key
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.PARTNER_USER_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/net/PartnerUserBlockedList?ctoken=<%=ctoken%>">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="partnerUserUnblockedAccount" name="submit" class="button3">
                                     Blocked Partner's user Account
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                         %>
                     </ul>
                     <%
                     }
                     else if ("generatePartnerKey".equals(buttonvalue))
                     {
                     %>
                     <button onclick="window.location.href='#'" id="clicker"
                             style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                             class="button1 active">
                         <i class="fa fa-users" style="float:left;"></i><span>Partner</span><span class="pull-right"><i
                             class="fa fa-caret-down"></i></span>
                     </button>
                     <ul style="display:block;">

                         <%
                             if (moduleSet.contains(PartnerModuleEnum.PARTNER_MASTER.name()) || (user.getRoles().contains("superpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerlist.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="partnerlist" name="submit" class="button3">
                                     Partner Master
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.PAYOUT_REPORTS.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerWireReports.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="PartnerReports" name="submit" class="button3">
                                     Payout Reports
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.PROFILE.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/net/PartnerDetailsConfig?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="PartnerDetailsConfig" name="submit" class="button3">
                                     Profile
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.THEME_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/net/MerchantTheme?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="MerchantTheme" name="submit" class="button3">
                                     Theme Configuration
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.GENERATE_SECRET_KEY.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/generatePartnerKey.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="generatePartnerKey" name="submit" class="button3"
                                         style="background-color:#d9d9d9">
                                     Generate Secret Key
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.PARTNER_USER_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/net/PartnerUserBlockedList?ctoken=<%=ctoken%>">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="MerchantTheme" name="submit" class="button3">
                                     Blocked Partner's user Account
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                         %>
                     </ul>
                     <%
                     }
                     else if ("partnerUserUnblockedAccount".equals(buttonvalue))
                     {
                     %>
                     <button onclick="window.location.href='#'" id="clicker"
                             style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                             class="button1 active">
                         <i class="fa fa-users" style="float:left;"></i><span>Partner</span><span class="pull-right"><i
                             class="fa fa-caret-down"></i></span>
                     </button>
                     <ul style="display:block;">

                         <%
                             if (moduleSet.contains(PartnerModuleEnum.PARTNER_MASTER.name()) || (user.getRoles().contains("superpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerlist.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="partnerlist" name="submit" class="button3">
                                     Partner Master
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.PAYOUT_REPORTS.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerWireReports.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="PartnerReports" name="submit" class="button3">
                                     Payout Reports
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.PROFILE.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/net/PartnerDetailsConfig?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="PartnerDetailsConfig" name="submit" class="button3">
                                     Profile
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.THEME_CONFIGURATION.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/net/MerchantTheme?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="MerchantTheme" name="submit" class="button3">
                                     Theme Configuration
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.GENERATE_SECRET_KEY.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/generatePartnerKey.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="generatePartnerKey" name="submit" class="button3">
                                     Generate Secret Key
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.PARTNER_USER_UNBLOCKED_ACCOUNT.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/net/PartnerUserBlockedList?ctoken=<%=ctoken%>">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="partnerUserUnblockedAccount" name="submit" class="button3"
                                         style="background-color:#d9d9d9">
                                     Blocked Partner's user Account
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                         %>
                     </ul>--%>
                    <%
                    }
                    else
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;"
                            class="button1">
                        <i class="fa fa-users" style="float:left;"></i>
                        <span>Partner</span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: none">

                        <%
                            String partnerstatusnew =null;
                            if(user.getRoles().contains("superpartner") || (user.getRoles().contains("childsuperpartner"))){
                                partnerstatusnew = "true";
                            }
                            else
                                partnerstatusnew = "false";
                            if (moduleSet.contains(PartnerModuleEnum.PARTNER_MASTER.name()) && partnerstatusnew.equals("true"))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerlist.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerlist" name="submit" class="button3">
                                    Partner Master
                                </button>
                            </form>
                        </li>
                        <%
                            }if (moduleSet.contains(PartnerModuleEnum.PARTNER_DEFAULT_CONFIGURATION.name()) && partnerstatusnew.equals("true"))
                        {
                        %>
                        <li>
                            <form action="/partner/partnerpreference.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("partnerpreference".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="partnerpreference" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Partner Default Configuration
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="partnerpreference" name="submit" class="button3">
                                    Partner Default Configuration
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.PARTNER_EMAIL_SETTING.name()) && partnerstatusnew.equals("true"))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerEmailSetting.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("partnerEmailSetting".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="partnerEmailSetting" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Partner Email Settings
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="partnerEmailSetting" name="submit" class="button3">
                                    Partner Email Settings
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.PAYOUT_REPORTS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerWireReports.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="PartnerReports" name="submit" class="button3">
                                    Payout Reports
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.PROFILE.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/net/PartnerDetailsConfig?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="PartnerDetailsConfig" name="submit" class="button3">
                                    Profile
                                </button>
                            </form>
                        </li>
                        <%
                            }
                           /* if (moduleSet.contains(PartnerModuleEnum.THEME_CONFIGURATION.name()) *//*|| (!user.getRoles().contains("subpartner"))*//*)
                            {*/
                        %>
                       <%-- <li>
                            <form action="/partner/net/MerchantTheme?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="MerchantTheme" name="submit" class="button3">
                                    Theme Configuration
                                </button>
                            </form>
                        </li>--%>
                        <%
                           // }
                            if (moduleSet.contains(PartnerModuleEnum.GENERATE_SECRET_KEY.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/generatePartnerKey.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="generatePartnerKey" name="submit" class="button3">
                                    Generate Secret Key
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                        <%--<%
                            if (moduleSet.contains(PartnerModuleEnum.PARTNER_USER_UNBLOCKED_ACCOUNT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/net/PartnerUserBlockedList?ctoken=<%=ctoken%>">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerUserUnblockedAccount" name="submit" class="button3">
                                    Blocked Partner's user Account
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            %>--%>
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.PARTNER_WHITELIST_DETAILS.name()) && partnerstatusnew.equals("true"))
                            {
                        %>
                        <li>
                            <form action="/partner/partnerIpWhitelist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerIpWhitelist" name="submit" class="button3">
                                    Partner's WhiteList Details</button>
                            </form>
                        </li>
                        <%
                            }
                            if(moduleSet.contains(PartnerModuleEnum.ACTIVITY_TRACKER.name())){
                        %>
                        <li>
                            <form action="/partner/activityTracker.jsp?ctoken=<%=ctoken%>">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="activityTracker" name="submit" class="button3">
                                    Activity Tracker</button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%
                            }
                        }
                    %>
                </li>
                <%--Start Profile mgmt--%>
                <%
                    if (moduleSet.contains(PartnerModuleEnum.RULE_MANAGEMENT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                    {
                %>
                <li>
                    <%--<% if (buttonvalue == null)
                    {%>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;"
                            class="button1">
                        <i class="fa fa-file-text" style="float:left;"></i><span>Rule Management</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:none;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.USER_PROFILE.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/userProfile.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="userProfile" name="submit" class="button3">
                                    User Profile
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.RISK_PROFILE.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/riskProfile.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="riskProfile" name="submit" class="button3">
                                    Risk Profile
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BUSINESS_PROFILE.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/businessProfile.jsp?ctoken=<%=ctoken%>">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="businessProfile" name="submit" class="button3">
                                    Business Profile
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>--%>
                    <%
                        /* }
                         else
                         {*/
                        if (buttonvalue!=null && ("userProfile".equals(buttonvalue) || "riskProfile".equals(buttonvalue) || "businessProfile".equals(buttonvalue)))
                        {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-file-text" style="float:left;"></i><span>Rule Management</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.USER_PROFILE.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/userProfile.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("userProfile".equals(buttonvalue))
                                    {
                                %>

                                <button type="submit" value="userProfile" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    User Profile
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="userProfile" name="submit" class="button3">
                                    User Profile
                                </button>
                                <%
                                    }
                                %>

                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.RISK_PROFILE.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/riskProfile.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("riskProfile".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="riskProfile" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Risk Profile
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="riskProfile" name="submit" class="button3">
                                    Risk Profile
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BUSINESS_PROFILE.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/businessProfile.jsp?ctoken=<%=ctoken%>">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("businessProfile".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="businessProfile" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Business Profile
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="businessProfile" name="submit" class="button3">
                                    Business Profile
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%--<%
                    }
                    else if ("riskProfile".equals(buttonvalue))
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-file-text" style="float:left;"></i><span>Rule Management</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.USER_PROFILE.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/userProfile.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="userProfile" name="submit" class="button3">
                                    User Profile
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.RISK_PROFILE.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/riskProfile.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="riskProfile" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Risk Profile
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BUSINESS_PROFILE.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/businessProfile.jsp?ctoken=<%=ctoken%>">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="businessProfile" name="submit" class="button3">
                                    Business Profile
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%
                    }
                    else if ("businessProfile".equals(buttonvalue))
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-file-text" style="float:left;"></i><span>Rule Management</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.USER_PROFILE.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/userProfile.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="userProfile" name="submit" class="button3">
                                    User Profile
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.RISK_PROFILE.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/riskProfile.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="riskProfile" name="submit" class="button3">
                                    Risk Profile
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BUSINESS_PROFILE.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/businessProfile.jsp?ctoken=<%=ctoken%>">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="businessProfile" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Business Profile
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>--%>
                    <%
                    }
                    else
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;"
                            class="button1">
                        <i class="fa fa-file-text" style="float:left;"></i>
                        <span>Rule Management</span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: none">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.USER_PROFILE.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/userProfile.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="userProfile" name="submit" class="button3">
                                    User Profile
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.RISK_PROFILE.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/riskProfile.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="riskProfile" name="submit" class="button3">
                                    Risk Profile
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BUSINESS_PROFILE.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/businessProfile.jsp?ctoken=<%=ctoken%>">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="businessProfile" name="submit" class="button3">
                                    Business Profile
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%
                        }
                    %>
                </li>
                <%
                    }
                    if (moduleSet.contains(PartnerModuleEnum.USER_MANAGEMENT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                    {
                %>
                <li>
                    <% if (buttonvalue == null)
                    {%>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;"
                            class="button1">
                        <i class="fa fa-user" style="float:left;"></i><span>User Management</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:none;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.PARTNER_USER_MANAGEMENT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerChildList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerChildList" name="submit" class="button3">
                                    Partner User Management
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_USER_MANAGEMENT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/memberChildList.jsp?ctoken=<%=ctoken%>">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="memberChildList" name="submit" class="button3">
                                    Merchant User Management
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%
                    }
                    else
                    {
                        if (buttonvalue!=null && ("partnerChildList".equals(buttonvalue) || "memberChildList".equals(buttonvalue)))
                        {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-user" style="float:left;"></i><span>User Management</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.PARTNER_USER_MANAGEMENT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerChildList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("partnerChildList".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="partnerChildList" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Partner User Management
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="partnerChildList" name="submit" class="button3">
                                    Partner User Management
                                </button>
                                <%
                                    }
                                %>

                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_USER_MANAGEMENT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/memberChildList.jsp?ctoken=<%=ctoken%>">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("memberChildList".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="memberChildList" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant User Management
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="memberChildList" name="submit" class="button3">
                                    Merchant User Management
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%-- <%
                     }
                    else if ("memberChildList".equals(buttonvalue))
                     {
                     %>
                     <button onclick="window.location.href='#'" id="clicker"
                             style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                             class="button1 active">
                         <i class="fa fa-user" style="float:left;"></i><span>User Management</span><span
                             class="pull-right"><i class="fa fa-caret-down"></i></span>
                     </button>
                     <ul style="display:block;">
                         <%
                             if (moduleSet.contains(PartnerModuleEnum.PARTNER_USER_MANAGEMENT.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/partnerChildList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="partnerChildList" name="submit" class="button3">
                                     Partner User Management
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.MERCHANT_USER_MANAGEMENT.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/memberChildList.jsp?ctoken=<%=ctoken%>">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="memberChildList" name="submit" class="button3"
                                         style="background-color:#d9d9d9">
                                     Merchant User Management
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                         %>
                     </ul>--%>
                    <%
                    }
                    else
                    {%>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;"
                            class="button1">
                        <i class="fa fa-user" style="float:left;"></i>
                        <span>User Management</span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul>
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.PARTNER_USER_MANAGEMENT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/partnerChildList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="partnerChildList" name="submit" class="button3">
                                    Partner User Management
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_USER_MANAGEMENT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/memberChildList.jsp?ctoken=<%=ctoken%>">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="memberChildList" name="submit" class="button3">
                                    Merchant User Management
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <% }
                    }%>
                </li>
                <%
                    }
                   /* if (moduleSet.contains(PartnerModuleEnum.REJECTED_TRANSACTION_LIST.name()) *//*|| (!user.getRoles().contains("subpartner"))*//*)
                    {*/
                %>
                <%--<form action="/partner/rejectedTransactionsList.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                    <li>
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="rejectedTransactionsList" name="submit" class="button1"
                                <%
                                    if (buttonvalue != null)
                                    {
                                        if (buttonvalue.equals("rejectedTransactionsList"))
                                        { %>
                                style="background-color:#7eccad !important;color:#ffffff"
                                <%
                                        }
                                    }
                                %>><i class="fa fa-credit-card iconstyle" style="float:left;"></i><span>Rejected Transaction List</span>
                        </button>
                    </li>
                </form>--%>

                <%
                   // }

                    if (moduleSet.contains(PartnerModuleEnum.EMI_CONFIGURATION.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                    {
                %>
                <form action="/partner/emiConfiguration.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                    <li>
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="emiConfiguration" name="submit" class="button1"
                                <%
                                    if (buttonvalue != null)
                                    {
                                        if (buttonvalue.equals("emiConfiguration"))
                                        { %>
                                style="background-color:#7eccad !important;color:#ffffff"
                                <%
                                        }
                                    }
                                %>><i class="fa fa-credit-card iconstyle" style="float:left;"></i><span>EMI Configuration</span>
                        </button>
                    </li>
                </form>

                <%
                    }
                    String Agentstatus =null;
                    if(user.getRoles().contains("superpartner") || (user.getRoles().contains("childsuperpartner"))){
                        Agentstatus = "true";
                    }
                    else
                        Agentstatus = "false";
                    if (moduleSet.contains(PartnerModuleEnum.AGENT_MANAGEMENT.name()) && Agentstatus.equals("true"))
                    {
                %>
                <li>
                    <%
                        /* }
                         else*/ if (buttonvalue!=null && ("agentInterface".equals(buttonvalue)) || "merchantagentMapping".equals(buttonvalue) || "agentIpWhitelist".equals(buttonvalue))
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-users" style="float:left;"></i><span>Agent Management</span><span class="pull-right"><i
                            class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">

                        <%
                            if (moduleSet.contains(PartnerModuleEnum.AGENT_MASTER.name()))
                            {
                        %>
                        <li>
                            <form action="/partner/agentInterface.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("agentInterface".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="agentInterface" name="submit" class="button3" style="background-color:#d9d9d9">
                                    Agent Master
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="agentInterface" name="submit" class="button3">
                                    Agent Master
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_AGENT_MAPPING.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/merchantAgentMapping.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("merchantagentMapping".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="merchantagentMapping" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Merchant Agent Mapping
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="merchantagentMapping" name="submit" class="button3">
                                    Merchant Agent Mapping
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <% }
                            if (moduleSet.contains(PartnerModuleEnum.AGENT_WHITELIST_DETAILS.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/agentIpWhitelist.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("agentIpWhitelist".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="agentIpWhitelist" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Agent's WhiteList Details
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="agentIpWhitelist" name="submit" class="button3">
                                    Agent's WhiteList Details
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <% } %>
                    </ul>
                    <%
                    }
                    else
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;"
                            class="button1">
                        <i class="fa fa-users" style="float:left;"></i>
                        <span>Agent Management</span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: none">

                        <%

                            if (moduleSet.contains(PartnerModuleEnum.AGENT_MASTER.name()))
                            {
                        %>
                        <li>
                            <form action="/partner/agentInterface.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="agentInterface" name="submit" class="button3">
                                    Agent Master
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.MERCHANT_AGENT_MAPPING.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/merchantAgentMapping.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="merchantagentMapping" name="submit" class="button3">
                                    Merchant Agent Mapping
                                </button>
                            </form>
                        </li>
                        <% }
                            if (moduleSet.contains(PartnerModuleEnum.AGENT_WHITELIST_DETAILS.name()))
                            {
                        %>
                        <li>
                            <form action="/partner/agentIpWhitelist.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="agentIpWhitelist" name="submit" class="button3">
                                    Agent's WhiteList Details
                                </button>
                            </form>
                        </li>
                        <% } %>
                    </ul>
                    <%
                            }
                        }
                    %>
                </li>

                <%
                    if (moduleSet.contains(PartnerModuleEnum.SETTLEMENT_MANAGEMENT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                    {
                %>
                <li>
                    <%--<% if (buttonvalue == null)
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left; margin-top: 0;"
                            class="button1">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span>Settlement Management</span><span
                            class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:none;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MANUAL_RECON.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/manualRecon.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Manual Recon" name="submit" class="button3">
                                    Manual Recon
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.LIST_SETTLEMENT_CYCLE.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/listSettlementCycle.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="List Settlement Cycle" name="submit" class="button3">
                                    List Settlement Cycle
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.ADD_SETTLEMENT_CYCLE.name()) || (!user.getRoles().contains("subpartner")))
                            {
                        %>
                        <li>
                            <form action="/partner/addSettlementCycle.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Add Settlement Cycle" name="submit" class="button3">
                                    Add Settlement Cycle
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>--%>
                    <%
                        /* }
                         else*/ if (buttonvalue!=null && ("Manual Recon".equals(buttonvalue) || "List Settlement Cycle".equals(buttonvalue) || "Add Settlement Cycle".equals(buttonvalue)) || "Bank Transaction Report".equals(buttonvalue))
                    {
                    %>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                            class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i>
                        <span>Settlement Management</span><span class="pull-right"><i
                            class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MANUAL_RECON.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/manualRecon.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("Manual Recon".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="Manual Recon" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Manual Recon
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="Manual Recon" name="submit" class="button3">
                                    Manual Recon
                                </button>
                                <%
                                    }
                                %>

                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.LIST_SETTLEMENT_CYCLE.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/listSettlementCycle.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("List Settlement Cycle".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="List Settlement Cycle" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    List Settlement Cycle
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="List Settlement Cycle" name="submit" class="button3">
                                    List Settlement Cycle
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.ADD_SETTLEMENT_CYCLE.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/addSettlementCycle.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("Add Settlement Cycle".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="Add Settlement Cycle" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Add Settlement Cycle
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="Add Settlement Cycle" name="submit" class="button3">
                                    Add Settlement Cycle
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BANK_TRANSACTION_REPORT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/bankTransactionReport.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    if("Bank Transaction Report".equals(buttonvalue))
                                    {
                                %>
                                <button type="submit" value="Bank Transaction Report" name="submit" class="button3"
                                        style="background-color:#d9d9d9">
                                    Bank Transaction Report
                                </button>
                                <%
                                }
                                else
                                {
                                %>
                                <button type="submit" value="Bank Transaction Report" name="submit" class="button3">
                                    Bank Transaction Report
                                </button>
                                <%
                                    }
                                %>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <%-- <%
                     }
                     else if ("List Settlement Cycle".equals(buttonvalue))
                     {
                     %>
                     <button onclick="window.location.href='#'" id="clicker"
                             style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                             class="button1 active">
                         <i class="fa fa-paste iconstyle" style="float:left;"></i>
                         <span>Settlement Management</span><span class="pull-right"><i
                             class="fa fa-caret-down"></i></span>
                     </button>
                     <ul style="display:block;">
                         <%
                             if (moduleSet.contains(PartnerModuleEnum.MANUAL_RECON.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/manualRecon.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="Manual Recon" name="submit" class="button3">
                                     Manual Recon
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.LIST_SETTLEMENT_CYCLE.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/listSettlementCycle.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="List Settlement Cycle" name="submit" class="button3"
                                         style="background-color:#d9d9d9">
                                     List Settlement Cycle
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.ADD_SETTLEMENT_CYCLE.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/addSettlementCycle.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="Add Settlement Cycle" name="submit" class="button3">
                                     Add Settlement Cycle
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                         %>
                     </ul>
                     <%
                     }
                     else if ("Add Settlement Cycle".equals(buttonvalue))
                     {
                     %>
                     <button onclick="window.location.href='#'" id="clicker"
                             style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;"
                             class="button1 active">
                         <i class="fa fa-paste iconstyle" style="float:left;"></i>
                         <span>Settlement Management</span><span class="pull-right"><i
                             class="fa fa-caret-down"></i></span>
                     </button>
                     <ul style="display:block;">
                         <%
                             if (moduleSet.contains(PartnerModuleEnum.MANUAL_RECON.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/manualRecon.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="Manual Recon" name="submit" class="button3">
                                     Manual Recon
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.LIST_SETTLEMENT_CYCLE.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/listSettlementCycle.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="List Settlement Cycle" name="submit" class="button3">
                                     List Settlement Cycle
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                             if (moduleSet.contains(PartnerModuleEnum.ADD_SETTLEMENT_CYCLE.name()) || (!user.getRoles().contains("subpartner")))
                             {
                         %>
                         <li>
                             <form action="/partner/addSettlementCycle.jsp?ctoken=<%=ctoken%>"
                                   style="margin-bottom: 0px">
                                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                 <button type="submit" value="Add Settlement Cycle" name="submit" class="button3"
                                         style="background-color:#d9d9d9">
                                     Add Settlement Cycle
                                 </button>
                             </form>
                         </li>
                         <%
                             }
                         %>
                     </ul>--%>
                    <%
                    }
                    else
                    {%>
                    <button onclick="window.location.href='#'" id="clicker"
                            style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;"
                            class="button1">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i>
                        <span>Settlement Management</span><span class="pull-right"><i
                            class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:none;">
                        <%
                            if (moduleSet.contains(PartnerModuleEnum.MANUAL_RECON.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/manualRecon.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Manual Recon" name="submit" class="button3">
                                    Manual Recon
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.LIST_SETTLEMENT_CYCLE.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/listSettlementCycle.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="List Settlement Cycle" name="submit" class="button3">
                                    List Settlement Cycle
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.ADD_SETTLEMENT_CYCLE.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/addSettlementCycle.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Add Settlement Cycle" name="submit" class="button3">
                                    Add Settlement Cycle
                                </button>
                            </form>
                        </li>
                        <%
                            }
                            if (moduleSet.contains(PartnerModuleEnum.BANK_TRANSACTION_REPORT.name()) /*|| (!user.getRoles().contains("subpartner"))*/)
                            {
                        %>
                        <li>
                            <form action="/partner/bankTransactionReport.jsp?ctoken=<%=ctoken%>"
                                  style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Bank Transaction Report" name="submit" class="button3">
                                    Bank Transaction Report
                                </button>
                            </form>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                    <% }%>
                </li>
                <%
                    }
                    /*if (moduleSet.contains(PartnerModuleEnum.RECURRING_MODULE.name()) *//*|| (!user.getRoles().contains("subpartner"))*//*)
                    {*/
                %>
                <%--<form action="/partner/recurringModule.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                    <li>
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="recurringModule" name="submit" class="button1"
                                <%
                                    if (buttonvalue != null)
                                    {
                                        if (buttonvalue.equals("recurringModule"))
                                        {
                                %>
                                style="background-color:#7eccad !important;color:#ffffff"
                                <%
                                        }
                                    }
                                %>><i class="fa fa-credit-card iconstyle" style="float:left;"></i><span>Recurring Module</span>
                        </button>
                    </li>
                </form>--%>
                <%
                    //}
                %>
                <form action="/partner/partnerChngpassword.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                    <li>
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="Change Password" name="submit" class="button1"
                                <%
                                    if (buttonvalue != null)
                                    {
                                        if (buttonvalue.equals("Change Password"))
                                        { %>
                                style="background-color:#7eccad !important;color:#ffffff"
                                <%
                                        }
                                    }
                                %>><i class="fa fa-key iconstyle" style="float:left;"></i><span>Change Password</span>
                        </button>
                    </li>
                </form>
            </ul>
        </div>
    </div>


<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">