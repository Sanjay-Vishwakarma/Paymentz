<%@ page import="com.directi.pg.TransactionEntry" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>

<%
    session.invalidate();
    ESAPI.authenticator().logout();
%>
<html>
<head>
    <title>Session Expired</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
    <script src="/merchant/NewCss/libs/bootstrap/js/bootstrap.min.js"></script>
    <%-- <script src="/merchant/NewCss/libs/jqueryui/jquery-ui-1.10.4.custom.min.js"></script>
    -<script src="/merchant/NewCss/libs/jquery-ui-touch/jquery.ui.touch-punch.min.js"></script>
     <script src="/merchant/NewCss/libs/jquery-detectmobile/detect.js"></script>
     <script src="/merchant/NewCss/libs/jquery-animate-numbers/jquery.animateNumbers.js"></script>
     <script src="/merchant/NewCss/libs/ios7-switch/ios7.switch.js"></script>
     <script src="/merchant/NewCss/libs/fastclick/fastclick.js"></script>
     <script src="/merchant/NewCss/libs/jquery-blockui/jquery.blockUI.js"></script>
     <script src="/merchant/NewCss/libs/bootstrap-bootbox/bootbox.min.js"></script>
     <script src="/merchant/NewCss/libs/jquery-slimscroll/jquery.slimscroll.js"></script>--%>
    <%--<script src="/merchant/NewCss/libs/jquery-sparkline/jquery-sparkline.js"></script>
    <script src="/merchant/NewCss/libs/nifty-modal/js/classie.js"></script>
    <script src="/merchant/NewCss/libs/nifty-modal/js/modalEffects.js"></script>
    <script src="/merchant/NewCss/libs/sortable/sortable.min.js"></script>
    <script src="/merchant/NewCss/libs/bootstrap-fileinput/bootstrap.file-input.js"></script>
    <script src="/merchant/NewCss/libs/bootstrap-select/bootstrap-select.min.js"></script>
    <script src="/merchant/NewCss/libs/bootstrap-select2/select2.min.js"></script>
    <script src="/merchant/NewCss/libs/magnific-popup/jquery.magnific-popup.min.js"></script>--%>
    <script src="/merchant/NewCss/libs/pace/pace.min.js"></script>
    <%--<script src="/merchant/NewCss/libs/bootstrap-datepicker/js/bootstrap-datepicker.js"></script>
    <script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <script src="/merchant/NewCss/js/init.js"></script>--%>
    <!-- Base Css Files -->
    <%--<link href="/merchant/NewCss/libs/jqueryui/ui-lightness/jquery-ui-1.10.4.custom.min.css" rel="stylesheet" />--%>
    <link href="/merchant/NewCss/libs/bootstrap/css/bootstrap.min.css" rel="stylesheet" />
    <%--<link href="/merchant/NewCss/libs/font-awesome/css/font-awesome.min.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/fontello/css/fontello.css" rel="stylesheet" />--%>
    <link href="/merchant/NewCss/libs/animate-css/animate.min.css" rel="stylesheet" />
    <%--<link href="/merchant/NewCss/libs/nifty-modal/css/component.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/magnific-popup/magnific-popup.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/ios7-switch/ios7-switch.css" rel="stylesheet" />--%>
    <link href="/merchant/NewCss/libs/pace/pace.css" rel="stylesheet" />
    <%--<link href="/merchant/NewCss/libs/sortable/sortable-theme-bootstrap.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/bootstrap-datepicker/css/datepicker.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/jquery-icheck/skins/all.css" rel="stylesheet" />--%>
    <!-- Code Highlighter for Demo -->
    <%--<link href="/merchant/NewCss/libs/prettify/github.css" rel="stylesheet" />--%>

    <!-- Extra CSS Libraries Start -->
   <%-- <link href="/merchant/NewCss/libs/rickshaw/rickshaw.min.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/libs/morrischart/morris.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/libs/jquery-jvectormap/css/jquery-jvectormap-1.2.2.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/libs/jquery-clock/clock.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/libs/bootstrap-calendar/css/bic_calendar.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/libs/sortable/sortable-theme-bootstrap.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/libs/jquery-weather/simpleweather.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/libs/bootstrap-xeditable/css/bootstrap-editable.css" rel="stylesheet" type="text/css" />--%>
    <link href="/merchant/NewCss/css/style.css" rel="stylesheet" type="text/css" />
    <!-- Extra CSS Libraries End -->
    <%--<link href="/merchant/NewCss/css/style-responsive.css" rel="stylesheet" />--%>

</head>
<body class="fixed-left widescreen pace-done">

<div class="container">
    <div class="full-content-center animated flipInX">
        <h1><%--<img src="/merchant/images/<%=logo%>" border="0">--%></h1>
        <h1 style="font-size: 28px;">Session Expired.</h1>
        <h3>Suggestion *For security reasons, we have disabled double clicks and Back, Forward and Refresh tabs of the browser. Also &nbsp;the&nbsp;session will expire automatically if the browser window is idle for a long period of time. <br><br>*If the problem persists, please try again after clearing the <strong>Temporary Files </strong>from your web browser.</h3><br>


    </div>
</div>

</body>
</html>

