<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="com.directi.pg.Agent" %>
<%@ page import="net.agent.AgentFunctions" %>

<%

    String buttonvalue=request.getParameter("submit");

    if(buttonvalue==null)
    {
        buttonvalue=(String)session.getAttribute("submit");

    }
    User user =  (User)session.getAttribute("ESAPIUserSessionKey");

    String partnerIcon = (String) session.getAttribute("icon");

    String ctoken= null;
    if(user!=null)
    {
        ctoken = user.getCSRFToken();

    }

    AgentFunctions agent=new AgentFunctions();
    if (!agent.isLoggedInAgent(session))
    {



%>

<html>

<head>
    <title>Agent Administration Logout</title>

    <%@ include file="ietest.jsp" %>
<%--    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <script src='/agent/css/menu.js'></script>
    <script type='text/javascript' src='/agent/css/menu_jquery.js'></script>
    <script src='/agent/css/bootstrap.min.js'></script>
    <link href="/agent/css/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet">

    <script type="text/javascript" src='/js/jQuery.dPassword.js'></script>
    <script type="text/javascript" src='/js/iPhonePassword.js'></script>

    <link rel="stylesheet" type="text/css" href="/agent/style/styles123.css">--%>
    <%--<link rel="stylesheet" type="text/css" href="/agent/css/styles123.css">--%>



</head>

<body>
<form action="index.jsp" method=post>
    <br><br>

    <br>
    <table class=search border="0" cellpadding="0" cellspacing="0" width="50%" align=center valign="center">
        <tr>
            <td align="center" style="background-color: #ffffff;">
                <img src="/images/agent/<%=session.getAttribute("logo")%>" border="0">
            </td>
        </tr>
    </table>
    <br><br>
    <table class=search border="0" bgcolor="#F1EDE0" cellpadding="2" cellspacing="0" width="600"
           bordercolorlight="#000000" bordercolordark="#FFFFFF" align=center valign="center">
        <tr>
            <td bgcolor="#9A1305" class="label" align="left">&nbsp;&nbsp;Agent Administration Module</td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr align="center">
            <td class="text">&nbsp;&nbsp;Your session has expired </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr align="center">
            <td class="text">&nbsp;&nbsp;Click <a href="/login.jsp" class="link">here</a> to go to the Agent login
                page </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
    </table>
</form>
</body>
</html>
<%
        return;
    }

%>
<%--<%@ include file="ietest.jsp" %>--%>


<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />

<script src="/agent/javascript/jquery.min.js"></script>
<script src="/agent/NewCss/libs/bootstrap/js/umd/popper.js"></script>
<script src="/agent/NewCss/libs/bootstrap/js/bootstrap.min.js"></script>
<script src="/agent/NewCss/libs/jqueryui/jquery-ui-custom.min.js"></script>
<script src="/agent/NewCss/libs/jquery-ui-touch/jquery.ui.touch-punch.min.js"></script>
<script src="/agent/NewCss/libs/jquery-detectmobile/detect.js"></script>
<script src="/agent/NewCss/libs/jquery-animate-numbers/jquery.animateNumbers.js"></script>
<script src="/agent/NewCss/libs/ios7-switch/ios7.switch.js"></script>
<script src="/agent/NewCss/libs/fastclick/fastclick.js"></script>
<script src="/agent/NewCss/libs/jquery-blockui/jquery.blockUI.js"></script>
<script src="/agent/NewCss/libs/bootstrap-bootbox/bootbox.min.js"></script>
<%--<script src="/agent/NewCss/libs/jquery-slimscroll/jquery.slimscroll.js"></script>--%>
<script src="/agent/NewCss/libs/jquery-sparkline/jquery-sparkline.js"></script>
<script src="/agent/NewCss/libs/nifty-modal/js/classie.js"></script>
<script src="/agent/NewCss/libs/nifty-modal/js/modalEffects.js"></script>
<script src="/agent/NewCss/libs/sortable/sortable.min.js"></script>
<script src="/agent/NewCss/libs/bootstrap-fileinput/bootstrap.file-input.js"></script>
<script src="/agent/NewCss/libs/bootstrap-select/bootstrap-select.min.js"></script>
<script src="/agent/NewCss/libs/bootstrap-select2/select2.min.js"></script>
<script src="/agent/NewCss/libs/magnific-popup/jquery.magnific-popup.min.js"></script>
<script src="/agent/NewCss/libs/pace/pace.min.js"></script>
<script src="/agent/NewCss/libs/bootstrap-datepicker/js/bootstrap-datepicker.js"></script>
<%--<script src="/agent/NewCss/libs/jquery-icheck/icheck.min.js"></script>--%>
<script src="/agent/NewCss/js/init.js"></script>

<script src="/agent/NewCss/libs/jquery-datatables/js/jquery.dataTables.min.js"></script>
<script src="/agent/NewCss/libs/jquery-datatables/js/dataTables.bootstrap.js"></script>
<script src="/agent/NewCss/libs/jquery-datatables/extensions/TableTools/js/dataTables.tableTools.min.js"></script>
<%--<script src="/agent/NewCss/js/pages/datatables.js"></script>--%>

<script src="/agent/NewCss/jquery-slimscroll/jquery.slimscroll.js"></script>

<!-- Base Css Files -->
<link href="/agent/NewCss/libs/jqueryui/ui-lightness/jquery-ui-1.10.4.custom.min.css" rel="stylesheet" />
<link href="/agent/NewCss/libs/bootstrap/css/bootstrap.min.css" rel="stylesheet" />
<link href="/agent/NewCss/libs/font-awesome/css/font-awesome.min.css" rel="stylesheet" />
<link href="/agent/NewCss/libs/fontello/css/fontello.css" rel="stylesheet" />
<link href="/agent/NewCss/libs/animate-css/animate.min.css" rel="stylesheet" />
<link href="/agent/NewCss/libs/nifty-modal/css/component.css" rel="stylesheet" />
<link href="/agent/NewCss/libs/magnific-popup/magnific-popup.css" rel="stylesheet" />
<link href="/agent/NewCss/libs/ios7-switch/ios7-switch.css" rel="stylesheet" />
<link href="/agent/NewCss/libs/pace/pace.css" rel="stylesheet" />
<link href="/agent/NewCss/libs/sortable/sortable-theme-bootstrap.css" rel="stylesheet" />
<link href="/agent/NewCss/libs/bootstrap-datepicker/css/datepicker.css" rel="stylesheet" />
<%--<link href="/agent/NewCss/libs/jquery-icheck/skins/all.css" rel="stylesheet" />--%>
<!-- Code Highlighter for Demo -->
<link href="/agent/NewCss/libs/prettify/github.css" rel="stylesheet" />

<!-- Extra CSS Libraries Start -->
<link href="/agent/NewCss/libs/rickshaw/rickshaw.min.css" rel="stylesheet" type="text/css" />

<link href="/agent/NewCss/libs/jquery-jvectormap/css/jquery-jvectormap-1.2.2.css" rel="stylesheet" type="text/css" />
<link href="/agent/NewCss/libs/jquery-clock/clock.css" rel="stylesheet" type="text/css" />
<link href="/agent/NewCss/libs/bootstrap-calendar/css/bic_calendar.css" rel="stylesheet" type="text/css" />
<link href="/agent/NewCss/libs/sortable/sortable-theme-bootstrap.css" rel="stylesheet" type="text/css" />
<link href="/agent/NewCss/libs/jquery-weather/simpleweather.css" rel="stylesheet" type="text/css" />
<link href="/agent/NewCss/libs/bootstrap-xeditable/css/bootstrap-editable.css" rel="stylesheet" type="text/css" />
<link href="/agent/NewCss/css/style.css" rel="stylesheet" type="text/css" />
<!-- Extra CSS Libraries End -->
<link href="/agent/NewCss/css/style-responsive.css" rel="stylesheet" />

<%--<script src="/agent/NewCss/libs/morrischart/morris.min.js"></script>
<script src="/agent/NewCss/js/pages/morris-charts.js"></script>--%>

<link href="/agent/NewCss/libs/jquery-datatables/css/dataTables.bootstrap.css" rel="stylesheet" type="text/css">
<link href="/agent/NewCss/libs/jquery-datatables/extensions/TableTools/css/dataTables.tableTools.css" rel="stylesheet" type="text/css">



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
        background: #ffffff!important;
    }

    @media (max-width: 640px) {
        .table-condensed tr:nth-child(odd) {
            background: #ffffff!important;
        }

        .table-condensed thead {
            display: block!important;
        }

        .datepicker-days .table-condensed td {
            display: inline-block!important;
        }

        .datepicker-months .table-condensed td {
            display: inherit!important;
        }

        .datepicker-years .table-condensed td {
            display: inherit!important;
        }

    }


    /***********************DatePicker Responsive Css Starts*********************/


    /***********************Pagination Responsive Starts***************************/

    table#paginateid{
        margin-bottom: 0;
        display: inherit;
        border-collapse: inherit;
        border-color: transparent;
    }

    table#paginateid tr:nth-child(odd){
        background: transparent!important;
    }

    table#paginateid td{
        vertical-align: inherit;
    }

    @media (max-width: 640px) {
        table#paginateid td {
            display: inline-block;
        }
    }

    @media (max-width: 640px) {
        table#paginateid tr:nth-child(odd) {
            background: transparent!important;
        }
    }


    @media (max-width: 640px) {
        tr:nth-child(odd), tr:nth-child(even) {
            background: transparent!important;
        }
    }

    /***********************Pagination Responsive Ends***************************/

    #maindata > td{vertical-align: middle;}


    #mytabletr td:first-child {
        vertical-align: middle!important;
        border: 1px solid #ddd!important;
        text-align: center!important;
    }

    #wrapper.enlarged .left.side-menu #sidebar-menu ul > li > ul > li{margin-left:-50px;}
    #wrapper.enlarged .left.side-menu #sidebar-menu ul > li:hover > form > button{text-align:center;}
    #wrapper.enlarged .left.side-menu #sidebar-menu ul > li > form > button{text-align:center;}

    #wrapper.enlarged .left.side-menu{position: absolute;}

    .left.side-menu .slimscrollleft{height: 80%!important;}
    #wrapper.enlarged .left.side-menu .slimscrollleft{height: inherit!important;}


    .table > thead > tr > th {font-weight: inherit;}

    /********************Table Responsive Start**************************/

    @media (max-width: 640px){

        table {border: 0;}

        /*table tr {
            padding-top: 20px;
            padding-bottom: 20px;
            display: block;
        }*/

        table thead { display: none;}

        tr:nth-child(odd), tr:nth-child(even) {background: #ffffff;}

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

        table tr:nth-child(odd) {background: #cacaca!important;}

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

    tr:nth-child(odd) {background: #F9F9F9;}

    tr {
        display: table-row;
        vertical-align: inherit;
        border-color: inherit;
    }

    th {padding-right: 1em;text-align: left;font-weight: bold;}

    td, th {display: table-cell;vertical-align: inherit;}

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

    .table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td, .table>tbody>tr>td, .table>tfoot>tr>td{border-top: 1px solid #ddd;}

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


    .profile-image {
        border: 4px double rgba(0,0,0,0.0);
        border-radius: inherit!important;
    }

    .rounded-image img .rounded-image img {
        width: 82%;
        margin-left: 10px;
    }



    #sidebar-menu > ul > form > li > button > i{
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
        border-left: 0px solid rgba(0,0,0,0.3);
    }

    .navbar-nav > li.language_bar > .dropdown-menu:before, .navbar-nav > li > .dropdown-menu.grid-dropdown:before {
        border-bottom: 6px solid #7A868F!important;
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

    /*    @media (max-width: 640px) {
            .widget .additional-btn {
                float: right;
                position: inherit;
            }
        }*/

    @media(max-width: 991px) {
        .additional-btn {
            float: left;
            margin-left: 30px;
            margin-top: 10px;
            position: inherit!important;
        }
    }


    .apperrormsg{
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
        for(var i=0;i < ca.length;i++) {
            var c = ca[i];
            while (c.charAt(0)==' ') c = c.substring(1,c.length);
            if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
        }
        return null;
    }

    function eraseCookie(name) {
        createCookie(name,"",-1);
    }
    var backColor = new Array();

    backColor[0] = '#34495E';
    backColor[1] = '#73A4D9';
    backColor[2] = '#CCCCCC';
    backColor[3] = '#CCCCFF';
    backColor[4] = '#FFFFFF';
    backColor[5] = '#CCCCCC';


    function changeBG(whichColor)
    {
        document.body.style.backgroundColor = backColor[whichColor];

        document.getElementById("cssmenu").style.background="blue";

        document.getElementById("cssmenu").style.backgroundColor=backColor[whichColor];
        $('.toppanel').css( "backgroundColor",backColor[whichColor] );

    }

    if(readCookie('backColor'))
        document.write('<style type="text/css">body {background-color: ' + backColor[readCookie("backColor")] + ';}<\/style>');

</script>

<%--top content--%>
<div class="md-modal md-3d-flip-vertical" id="task-progress">
    <div class="md-content">
        <h3><strong>Task Progress</strong> Information</h3>
        <div>
            <p>CLEANING BUGS</p>
            <div class="progress progress-xs for-modal">
                <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="80" aria-valuemin="0" aria-valuemax="100" style="width: 80%">
                    <span class="sr-only">80&#37; Complete</span>
                </div>
            </div>
            <p>POSTING SOME STUFF</p>
            <div class="progress progress-xs for-modal">
                <div class="progress-bar progress-bar-warning" role="progressbar" aria-valuenow="80" aria-valuemin="0" aria-valuemax="100" style="width: 65%">
                    <span class="sr-only">65&#37; Complete</span>
                </div>
            </div>
            <p>BACKUP DATA FROM SERVER</p>
            <div class="progress progress-xs for-modal">
                <div class="progress-bar progress-bar-info" role="progressbar" aria-valuenow="80" aria-valuemin="0" aria-valuemax="100" style="width: 95%">
                    <span class="sr-only">95&#37; Complete</span>
                </div>
            </div>
            <p>RE-DESIGNING WEB APPLICATION</p>
            <div class="progress progress-xs for-modal">
                <div class="progress-bar progress-bar-primary" role="progressbar" aria-valuenow="80" aria-valuemin="0" aria-valuemax="100" style="width: 100%">
                    <span class="sr-only">100&#37; Complete</span>
                </div>
            </div>
            <p class="text-center">
                <button class="btn btn-danger btn-sm md-close">Close</button>
            </p>
        </div>
    </div>
</div>

<!-- Modal Logout -->
<div class="md-modal md-just-me" id="logout-modal">
    <div class="md-content">
        <h3><strong>Logout</strong> Confirmation</h3>
        <div>
            <p class="text-center">Are you sure want to logout from this awesome system?</p>
            <p class="text-center">
                <%--<button type="submit" name="submit" class="btn btn-danger md-close logout">
                    <form action="/merchant/logout.jsp?ctoken=<%=ctoken%>" class="menufont" style="background-color:#ffffff; ">
                         <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        Logout
                    </form>
                </button>--%>
                <%--<a href="login.html" class="btn btn-success md-close">Yeah, I'm sure</a>--%>
            </p>
        </div>
    </div>
</div>        <!-- Modal End -->
<!-- Begin page -->
<div id="wrapper" class="">
    <div class="topbar">
        <div class="topbar-left">
            <div class="logo">
                <h1><a <%--href="#"--%>><%--<img src="/merchant/images/ForWebsiteSagarNew3.png" alt="Logo" style="margin-right: 30px;">--%>
                    <%--<img src="/merchant/images/<%=session.getAttribute("logo")%>" style="margin-right: 30px;height: 57%;">--%>
                    <img src="/images/agent/<%=session.getAttribute("logo")%>" style="margin-right: 30px;height: 57%;">
                </a></h1>
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
                            <a class="dropdown-toggle" aria-expanded="false" data-bs-toggle="dropdown" style="cursor:pointer;">English (US) <i class="fa fa-caret-down"></i></a>
                            <ul class="dropdown-menu pull-right" style="background: #7A868F;">
                                <li><a style="color: #fff;cursor:pointer;">German</a></li>
                                <li><a style="color: #fff;cursor:pointer;">French</a></li>
                                <li><a style="color: #fff;cursor:pointer;">Italian</a></li>
                                <li><a style="color: #fff;cursor:pointer;">Spanish</a></li>
                            </ul>
                        </li>
                    </ul>
                    <ul class="nav navbar-nav navbar-right top-navbar">

                        <li class="dropdown iconify hide-phone"><a style="cursor: pointer;" onclick="javascript:toggle_fullscreen()"><i class="icon-resize-full-2"></i></a></li>
                        <li class="dropdown topbar-profile">
                            <a  class="dropdown-toggle" data-toggle="dropdown" style="cursor: pointer;"><span class="rounded-image topbar-profile-image"><img src="/agent/images/<%=user.getAccountName().substring(0,1).toLowerCase()%>.png"></span><strong><%=(String)session.getAttribute("agentname")%></strong> <i class="fa fa-caret-down"></i></a>
                            <ul class="dropdown-menu">
                                <li style="margin-top: 10px;">
                                    <form action="/agent/agentChngpassword.jsp?ctoken=<%=ctoken%>">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <button type="submit" name="submit" class="md-trigger" style="background: #7A868F;border: #7A868F;width: 100%;text-align: left;text-indent: 4px;">
                                            Change Password</button>
                                    </form>
                                </li>
                                <li class="divider"></li>
                                <%--<li><a ><i class="icon-help-2"></i> Help</a></li>--%>
                                <%--<li><a ><i class="icon-lock-1"></i> Lock me</a></li>--%>
                                <li>

                                    <form action="/agent/logout.jsp?ctoken=<%=ctoken%>" class="menufont" style="background-color:#ffffff; ">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <button type="submit" name="submit" class="md-trigger" data-modal="logout-modal" style="background: #7A868F;border: #7A868F;width: 100%;text-align: left;text-indent: 4px;">
                                            <i class="icon-logout-1"></i> Logout</button>
                                    </form>

                                </li>
                            </ul>
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
            <%
                if(agent.isValueNull(partnerIcon))
                {
            %>
            <div class="col-xs-4">
                <%--<a class="rounded-image profile-image"><img src="/merchant/images/<%=user.getAccountName().substring(0,1).toLowerCase()%>.png"></a>--%>
                <a class="rounded-image profile-image"><img src="/images/agent/<%=partnerIcon%>"></a>
            </div>
            <%
            }
            else
            {
            %>
            <div class="col-xs-4">
                <%--<a class="rounded-image profile-image"><img src="/merchant/images/<%=user.getAccountName().substring(0,1).toLowerCase()%>.png"></a>--%>
                <%--<a class="rounded-image profile-image"><img src="/merchant/images/<%=partnerIcon%>"></a>--%>
            </div>
            <%
                }

            %>
            <div class="col-xs-8">
                <br>
                <div class="profile-text">

                    Welcome <br>
                    User : <%=user.getAccountName()%>

                </div>
            </div>
        </div>

        <div class="clearfix"></div>
        <hr class="divider" />
        <div class="clearfix"></div>
        <!--- Divider -->
        <%--<div class="some-content-related-div">--%>
        <%--<div id="inner-content-div" style="height:500px;">--%>
        <div id="sidebar-menu" class="slimscrollleft">

            <ul>


                <form action="/agent/net/AgentDashboard?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                    <li>
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="DashBoard" name="submit" class="button1"
                                <%
                                    if(request.getParameter("submit")!=null)
                                    {
                                        if(request.getParameter("submit").equals("DashBoard"))
                                        {   %>
                                style="background-color:#7eccad !important;color:#ffffff"
                                <%}
                                }
                                %>><i class="fa fa-bar-chart-o iconstyle" style="float:left;"></i><span>DashBoard</span>
                        </button>
                    </li>
                </form>




                <form action="/agent/merchantDetails.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                    <li>
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="merchantDetails" name="submit"  class="button1"
                                <%
                                    if(buttonvalue!=null)
                                    {
                                        if(buttonvalue.equals("merchantDetails"))
                                        {   %>
                                style="background-color:#7eccad !important;color:#ffffff"
                                <%}
                                }
                                %>><i class="fa fa-credit-card iconstyle" style="float:left;"></i><span>Merchant Details</span>
                        </button>
                    </li>
                </form>



                <li>
                    <% if(buttonvalue==null){%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;margin-bottom: 0px;" class="button1">
                        <i class="fa fa-user-md iconstyle" style="float:left;"></i><span>Transaction Report</span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: none;">
                        <li>
                            <form action="/agent/merchanttranssummarylist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Member Wise Report" name="submit"  class="button3">
                                    Merchant Wise Report
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/agent/merchantTerminalTransReport.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Terminal Wise Report" name="submit" class="button3">
                                    Terminal Wise Report
                                </button>
                            </form>
                        </li>
                    </ul><%}else{if("Member Wise Report".equals(buttonvalue)){%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span>Transaction Report</span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <li>
                            <form action="/agent/merchanttranssummarylist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Member Wise Report" name="submit" class="button3"  style="background-color:#d9d9d9">
                                    Merchant Wise Report
                                </button>
                            </form>
                        </li>

                        <li>
                            <form action="/agent/merchantTerminalTransReport.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Terminal Wise Report" name="submit" class="button3">
                                    Terminal Wise Report
                                </button>
                            </form>
                        </li>
                    </ul>
                    <%}else if("Terminal Wise Report".equals(buttonvalue)){%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span>Transaction Report</span><span class="pull-right"><i class="fa fa-caret-down"></i></span>

                    </button>
                    <ul style="display:block;">
                        <li  >
                            <form action="/agent/merchanttranssummarylist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Member Wise Report" name="submit" class="button3">
                                    Merchant Wise Report
                                </button>
                            </form>
                        </li>

                        <li  >
                            <form action="/agent/merchantTerminalTransReport.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Terminal Wise Report" name="submit" class="button3"  style="background-color:#d9d9d9">
                                    Terminal Wise Report
                                </button>
                            </form>
                        </li>


                    </ul>

                    <%}else {%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;" class="button1">
                        <i class="fa fa-paste" style="float:left;"></i>
                <span>Transaction Report
                </span><span class="pull-right"><i class="fa fa-caret-down"></i></span>

                    </button>
                    <ul >
                        <li>
                            <form action="/agent/merchanttranssummarylist.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Member Wise Report" name="submit" class="button3">
                                    Merchant Wise Report
                                </button>
                            </form>
                        </li>

                        <li  >
                            <form action="/agent/merchantTerminalTransReport.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Terminal Wise Report" name="submit" class="button3">
                                    Terminal Wise Report
                                </button>
                            </form>
                        </li>

                    </ul>
                    <%}}%>
                </li>



                <form action="/agent/agentTransactions.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                    <li>
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="agentTransactions" name="submit"  class="button1"
                                <%
                                    if(buttonvalue!=null)
                                    {
                                        if(buttonvalue.equals("agentTransactions"))
                                        {   %>
                                style="background-color:#7eccad !important;color:#ffffff"
                                <%}
                                }
                                %>><i class="fa fa-credit-card iconstyle" style="float:left;"></i><span>Transactions</span>
                        </button>
                    </li>
                </form>




                <li>
                    <% if(buttonvalue==null){%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;margin-bottom: 0px;" class="button1">
                        <i class="fa fa-user-md iconstyle" style="float:left;"></i><span>Wire Report</span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display: none;">
                        <li>
                            <form action="/agent/wireReport.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Agent Wire Report" name="submit"  class="button3">
                                    Agent Wire Report
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/agent/merchantWireReports.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Member Wire Report" name="submit" class="button3">
                                    Merchant Wire Report
                                </button>
                            </form>
                        </li>
                    </ul><%}else{if("Agent Wire Report".equals(buttonvalue)){%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span>Wire Report</span><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                    </button>
                    <ul style="display:block;">
                        <li>
                            <form action="/agent/wireReport.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Agent Wire Report" name="submit" class="button3"  style="background-color:#d9d9d9">
                                    Agent Wire Report
                                </button>
                            </form>
                        </li>

                        <li>
                            <form action="/agent/merchantWireReports.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Member Wire Report" name="submit" class="button3">
                                    Merchant Wire Report
                                </button>
                            </form>
                        </li>
                    </ul>
                    <%}else if("Member Wire Report".equals(buttonvalue)){%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background:#7eccad !important;color:#ffffff;margin-left: 0px;text-align: left;" class="button1 active">
                        <i class="fa fa-paste iconstyle" style="float:left;"></i><span>Wire Report</span><span class="pull-right"><i class="fa fa-caret-down"></i></span>

                    </button>
                    <ul style="display:block;">
                        <li  >
                            <form action="/agent/wireReport.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Agent Wire Report" name="submit" class="button3">
                                    Agent Wire Report
                                </button>
                            </form>
                        </li>

                        <li  >
                            <form action="/agent/merchantWireReports.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Member Wire Report" name="submit" class="button3"  style="background-color:#d9d9d9">
                                    Merchant Wire Report
                                </button>
                            </form>
                        </li>


                    </ul>

                    <%}else {%>
                    <button onclick="window.location.href='#'" id="clicker" style="height: 40px;display: block;color: #555;text-indent: 13px;font-size: 12px;background: #ffffff;margin-left: 0px;text-align: left;" class="button1">
                        <i class="fa fa-paste" style="float:left;"></i>
                <span>Wire Report
                </span><span class="pull-right"><i class="fa fa-caret-down"></i></span>

                    </button>
                    <ul >
                        <li>
                            <form action="/agent/wireReport.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Agent Wire Report" name="submit" class="button3">
                                    Agent Wire Report
                                </button>
                            </form>
                        </li>

                        <li  >
                            <form action="/agent/merchantWireReports.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <button type="submit" value="Member Wire Report" name="submit" class="button3">
                                    Merchant Wire Report
                                </button>
                            </form>
                        </li>

                    </ul>
                    <%}}%>
                </li>



                <form action="/agent/agentChngpassword.jsp?ctoken=<%=ctoken%>" style="margin-bottom: 0px;">
                    <li>
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="agentChngpassword" name="submit"  class="button1"
                                <%
                                    if(buttonvalue!=null)
                                    {
                                        if(buttonvalue.equals("agentChngpassword"))
                                        {   %>
                                style="background-color:#7eccad !important;color:#ffffff"
                                <%}
                                }
                                %>><i class="fa fa-key iconstyle" style="float:left;"></i><span>Change Password</span>
                        </button>
                    </li>
                </form>




            </ul>
        </div>
    </div>

