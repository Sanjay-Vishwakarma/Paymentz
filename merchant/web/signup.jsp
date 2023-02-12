<%@ page language="java" import="com.logicboxes.util.ApplicationProperties,java.util.Hashtable
                                 ,org.owasp.esapi.User,
                                 com.directi.pg.Functions,
                                 com.directi.pg.DefaultUser" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.reference.DefaultEncoder" %>
<%@ page import="com.manager.dao.PartnerDAO" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%@ page import="com.payment.exceptionHandler.PZDBViolationException" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ include file="ietest.jsp" %>


<%
    String currentTheme     = (String) session.getAttribute("currenttheme");
    String defaultTheme     = (String) session.getAttribute("defaulttheme");
    String partnerName      = (String) session.getAttribute("company");
    String role             = "Merchant";
    Logger logger  = new Logger("signup.jsp");
%>
<%
    String ctoken   = request.getParameter("ctoken");
    User user       = (User)session.getAttribute("Anonymous");
    if(ctoken !=null)
    {
        if(!Functions.validateCSRFAnonymos(ctoken,user))
        {
            response.sendRedirect("/merchant/sessionout.jsp");
            return;
        }

    }
    else if(user != null)
    {
        ctoken  = user.getCSRFToken();
    }
    else
    {
        user    =   new DefaultUser(User.ANONYMOUS.getName());
        ctoken  = (user).resetCSRFToken();
    }
    String partnerid    = "";
    if(session.getAttribute("partnerid")!=null && !session.getAttribute("partnerid").equals(""))
    {
        partnerid = (String)session.getAttribute("partnerid");
    }
    session.setAttribute("Anonymous", user);
    String privacyurl       = "";
    String termsurl         = "";
    String partnername      = "";
    String target           = "";
    String target1          = "";
    Functions functions = new Functions();
    PartnerDAO partnerDAO   = new PartnerDAO();
    PartnerDetailsVO partnerDetailsVO = null;
    try
    {
        partnerDetailsVO = partnerDAO.geturl(partnerid);
    }
    catch (PZDBViolationException e)
    {
        logger.error("PZDBViolationException :::::::::::: ",e);
    }
    privacyurl      = partnerDetailsVO.getPrivacyurl();
    termsurl        = partnerDetailsVO.getTermsurl();
    partnername     = partnerDetailsVO.getPartnerName();

    String partnerIcon  = (String) session.getAttribute("icon");
    String logo         = (String)session.getAttribute("logo");
    String logoHeight="38";
    String logoWidth="140";

    partnerDAO = new PartnerDAO();
    String partnerid1="";
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
    if (functions.isValueNull(privacyurl))
    {
        privacyurl  = partnerDetailsVO.getPrivacyurl();
        target1     = "_blank";
    }
    else
    {
        privacyurl  = "#";
        target1     = "";

    }
    if (functions.isValueNull(termsurl))
    {
        termsurl    = partnerDetailsVO.getTermsurl();
        target      = "_blank";
    }
    else
    {
        termsurl    = "#";
        target     = "";
    }


    Hashtable details   = (Hashtable) request.getAttribute("details");
    String username     = "";
    String passwd       = "";
    String conpasswd    = "";
    String tpasswd      = "";
    String contpasswd   = "";
    String company_name = "";
    //String company_type = "";
    String brandname        = "";
    String sitename         = "";
    String contact_emails   = "";
    String contact_persons  = "";
    String maincontact_phone    = "";
    String telno                = "";
    String faxno                = "";
    String address              = "";
    String city                 = "";
    String state                = "";
    String country              = "";
    String phonecc              = "";
    String etoken               = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
    String zip                  = "";
    String notifyemail          = "";
    String potentialbusiness    = "";
    String currency             = "";
    String mes                  = "";
    String errormsg             = ESAPI.encoder().encodeForHTML((String) request.getAttribute("error"));
    if (errormsg == null)
    {
        errormsg = "";
    }

    if (request.getParameter("MES") != null)
    {
        mes = (String) request.getParameter("MES");

        if ((String) details.get("login") != null) username = (String) details.get("login");
        if ((String) details.get("company_name") != null) company_name = (String) details.get("company_name");
        //if ((String) details.get("company_type") != null) company_type =(String) details.get("company_type");
        //if ((String) details.get("brandname") != null) brandname = (String) details.get("brandname");
        if ((String) details.get("sitename") != null) sitename = (String) details.get("sitename");
        if ((String) details.get("contact_emails") != null) contact_emails = (String) details.get("contact_emails");
        if ((String) details.get("contact_persons") != null) contact_persons = (String) details.get("contact_persons");
        if ((String) details.get("telno") != null) telno = (String) details.get("telno");
        //if ((String) details.get("faxno") != null) faxno = (String) details.get("faxno");
        //if ((String) details.get("address") != null) address = (String) details.get("address");
        //if ((String) details.get("city") != null) city = (String) details.get("city");
        //if ((String) details.get("state") != null) state = (String) details.get("state");
        if ((String) details.get("country") != null) country = (String) details.get("country");
        if ((String) details.get("phonecc") != null) phonecc = (String) details.get("phonecc");
        //if ((String) details.get("zip") != null) zip = (String) details.get("zip");
        if ((String) details.get("notifyemail") != null) notifyemail = (String) details.get("notifyemail");
        if ((String) details.get("maincontact_phone") != null) maincontact_phone = (String) details.get("maincontact_phone");
        //if ((String) details.get("potentialbusiness") != null) potentialbusiness = (String) details.get("potentialbusiness");
        //if ((String) details.get("currency") != null) currency = (String) details.get("currency");

    }
%>
<html>
<head>


    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <%--    <link rel="stylesheet" type="text/css" href="/merchant/css/styles123.css">
        <link href="/merchant/css/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet">--%>
    <%--<script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>--%>
    <script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.min.js"></script>
    <link rel="stylesheet" type="text/css" id="theme" href="/merchant/NewCss/theme-default.css"/>
    <%--<link rel="stylesheet" type="text/css"  href="/merchant/NewCss/bootstrap.min.css"/>--%>
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.2.0/css/all.css" integrity="sha384-hWVjflwFxL6sNzntih27bfxkr27PmbbK/iSvJ+a4+0owXq79v+lsFkW54bOGbiDQ" crossorigin="anonymous">

    <title><%=session.getAttribute("company")%> Merchant Settings > Merchant Profile</title>
    <script type="text/javascript" src="/merchant/javascript/jquery.jcryption.js?ver=1"></script>
    <script src="/merchant/NewCss/libs/bootstrap/js/bootstrap.min.js"></script>
    <link href="/merchant/NewCss/libs/bootstrap/css/bootstrap.min.css" rel="stylesheet" />
    <%
        if(functions.isEmptyOrNull(defaultTheme) && functions.isEmptyOrNull(currentTheme))
        {
    %>
    <link href="/merchant/NewCss/css/pz.css" rel="stylesheet" type="text/css" />
    <%
    }
    else if(functions.isEmptyOrNull(currentTheme) && functions.isValueNull(defaultTheme))
    {
    %>
    <link href="/merchant/NewCss/css/<%=defaultTheme%>.css" rel="stylesheet" type="text/css" />
    <%
    }
    else if(functions.isValueNull(currentTheme) && functions.isValueNull(defaultTheme))
    {
    %>
    <link href="/merchant/NewCss/css/<%=currentTheme%>.css" rel="stylesheet" type="text/css" />
    <%
    }
    else if(functions.isValueNull(currentTheme) && functions.isEmptyOrNull(defaultTheme))
    {
    %>
    <link href="/merchant/NewCss/css/<%=currentTheme%>.css" rel="stylesheet" type="text/css" />
    <%
        }
    %>

    <style type="text/css">
        .login-body {
            width: 100%;
            float: left;
            background: rgba(255, 255, 255, 0.2);
            padding: 20px;
            -moz-border-radius: 5px;
            -webkit-border-radius: 5px;
            border-radius: 5px;
            margin-top: 80px;
            padding-bottom: 0px;
        }

        .login-title{
            color: #FFF;
            font-size: 19px;
            font-weight: 300;
            margin-top: 15px;
        }

        .content{
            width: 35%;
            margin: 0px auto;
        }

        .form-control:focus{
            background: rgba(255, 255, 255, 0.4);
        }

        @media (max-width: 992px) {
            .content{
                width: 80%;
                margin: 0px auto;
            }
        }

        .texthead{
            background: rgba(30, 139, 146, 0.49)!important;
        }

        .textb{
            color: #fff;
        }

        .hrnew{border-top: 1px solid #fff;}

        .btn-green-3 {
            background-color: #29AAA1;
            color: #fff;
        }
        .btn-green-3:hover {
            background-color: #197b77;
            color: #fff;
        }

        .widget{
            box-shadow: none;
        }

        .form-horizontal .control-label {
            color: #fff;
            font-size: 12px;
            text-align: center;
        }

        @media (min-width: 992px) {
            .form-horizontal .control-label {
                text-align: left;
            }
        }

        .bg-infoorange {
            padding: 15px;
            margin-bottom: 20px;
            border: 1px solid transparent;
            border-radius: 4px;
            color: #FFF;
            /*background-color: #fdf7f7;
            border-left: 4px solid #f00;*/
            font-family: "Open Sans";
            font-size: 13px;
            font-weight: 600;
            text-align: center;
        }

        .login-container.lightmode {
            /*background-color: #17233b;*/
            background-size: cover;
        }

        .form-control {
            background: rgba(255, 255, 255, 0.4);
            color: #333;
            border: 0px;
            padding: 10px 15px;
            line-height: 20px;
            height: auto;
        }
       /* for country autocomplete*/
        /*!
 * Materialize v0.100.2 (http://materializecss.com)
 * Copyright 2014-2017 Materialize
 * MIT License (https://raw.githubusercontent.com/Dogfalo/materialize/master/LICENSE)
 */


        ul:not(.browser-default) {
            padding-left: 0;
            list-style-type: none;
        }

        ul:not(.browser-default) > li {
            list-style-type: none;
        }

        a {
            color: #039be5;
            text-decoration: none;
            -webkit-tap-highlight-color: transparent;
        }


        /*******************
          Utility Classes
        *******************/
        .hide {
            display: none !important;
        }

        .left-align {
            text-align: left;
        }

        .right-align {
            text-align: right;
        }

        .center, .center-align {
            text-align: center;
        }

        .left {
            float: left !important;
        }

        .right {
            float: right !important;
        }

        .no-select, input[type=range],
        input[type=range] + .thumb {
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            user-select: none;
        }

        .circle {
            border-radius: 50%;
        }

        .center-block {
            display: block;
            margin-left: auto;
            margin-right: auto;
        }

        .truncate {
            display: block;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .no-padding {
            padding: 0 !important;
        }

        span.badge {
            min-width: 3rem;
            padding: 0 6px;
            margin-left: 14px;
            text-align: center;
            font-size: 1rem;
            line-height: 22px;
            height: 22px;
            color: #757575;
            float: right;
            -webkit-box-sizing: border-box;
            box-sizing: border-box;
        }

        span.badge.new {
            font-weight: 300;
            font-size: 0.8rem;
            color: #fff;
            background-color: #26a69a;
            border-radius: 2px;
        }

        span.badge.new:after {
            content: " new";
        }

        span.badge[data-badge-caption]::after {
            content: " " attr(data-badge-caption);
        }

        /* This is needed for some mobile phones to display the Google Icon font properly */
        .material-icons {
            text-rendering: optimizeLegibility;
            -webkit-font-feature-settings: 'liga';
            -moz-font-feature-settings: 'liga';
            font-feature-settings: 'liga';
        }


        .dropdown-content {
            background-color: #fff;
            margin: 0;
            display: none;
            min-width: 100px;
            max-height: 650px;
            overflow-y: auto;
            opacity: 0;
            position: absolute;
            z-index: 999;
            will-change: width, height;
        }

        .dropdown-content li {
            clear: both;
            color: rgba(0, 0, 0, 0.87);
            cursor: pointer;
            min-height: 50px;
            line-height: 1.5rem;
            width: 100%;
            text-align: left;
            text-transform: none;
        }

        .dropdown-content li:hover, .dropdown-content li.active, .dropdown-content li.selected {
            background-color: #eee;
        }

        .dropdown-content li.active.selected {
            background-color: #e1e1e1;
        }

        .dropdown-content li.divider {
            min-height: 0;
            height: 1px;
        }

        .dropdown-content li > a, .dropdown-content li > span {
            font-size: 16px;
            color: #26a69a;
            display: block;
            line-height: 22px;
            padding: 14px 16px;
        }

        .dropdown-content li > span > label {
            top: 1px;
            left: 0;
            height: 18px;
        }

        .dropdown-content li > a > i {
            height: inherit;
            line-height: inherit;
            float: left;
            margin: 0 24px 0 0;
            width: 24px;
        }

        .input-field.col .dropdown-content [type="checkbox"] + label {
            top: 1px;
            left: 0;
            height: 18px;
        }



        /* Autocomplete */
        .autocomplete-content {
            margin-top: -5px;
            margin-bottom: 20px;
            display: block;
            opacity: 1;
            position: static;
            max-height: 350px;
        }

        .autocomplete-content li .highlight {
            color: #444;
        }

        .autocomplete-content li img {
            height: 40px;
            width: 40px;
            margin: 5px 15px;
        }


        /*.main-topheader {*/
            /*position: fixed;*/
            /*top: 0;*/
            /*left: 0;*/
            /*width: 100%;*/
            /*padding: 0;*/
            /*z-index: 10000;*/
            /*transition: all 0.2s ease-in-out;*/
            /*height: auto;*/
            /*background-color:transparent;*/

            /*text-align: center;*/
            /*line-height: 40px;*/
        /*}*/

        /*.main-topheader .active {*/
            /*background: #fff !important;*/
            /*-webkit-box-shadow: 0 1px 5px rgba(0, 0, 0, 0.25);*/
            /*-moz-box-shadow: 0 1px 5px rgba(0, 0, 0, 0.25);*/
            /*box-shadow: 0 1px 5px rgba(0, 0, 0, 0.25);*/
        /*}*/


    </style>

    <script language="javascript">
        $(document).ready(function() {
            document.getElementById('btnSubmit').disabled =  false;

            $("#btnSubmit").click(function() {
                var checkbox =  $('#check').is(":checked");

                if(!checkbox ){
                    alert('You must agree to the privacy policy and terms policy first.');
                    return;
                }

                var encryptedString1 =  $.jCryption.encrypt($("#passwd").val(), $("#ctoken").val());
                document.getElementById('passwd').value =  encryptedString1;

                var encryptedString2 =  $.jCryption.encrypt($("#conpasswd").val(), $("#ctoken").val());
                document.getElementById('conpasswd').value =  encryptedString2;

                /*var encryptedString3 =  $.jCryption.encrypt($("#tpasswd").val(), $("#ctoken").val());
                 document.getElementById('tpasswd').value =  encryptedString3;

                 var encryptedString3 =  $.jCryption.encrypt($("#contpasswd").val(), $("#ctoken").val());
                 document.getElementById('contpasswd').value =  encryptedString3;*/

                document.getElementById('isEncrypted').value =  true;

                var $Modal = $('#myModal');
                $Modal.show();
                /*if(check()){
                 var $Modal = $('#myModal');
                 $Modal.show();
                 }else{
                 return ;
                 }*/
            });

            $('#yesSubmit').click(function(){
                console.log("yesbutton");
                document.getElementById('sendEmailNotification').value =  "Y";
                $('#form1').submit();
            });
            $('#noSubmit').click(function(){
                console.log("Nobutton");
                document.getElementById('sendEmailNotification').value = "N";
                $('#form1').submit();
            });

        });


        function isint(form)
        {
            if (isNaN((form.numrows.value)))
                return false;
            else
                return true;
        }

        function check()
        {
            var msg = "Following information are missing:-\n";
            var flag = "false";
            if (document.form1.username.value.length == 0)
            {
                msg = msg + "\nPlease enter username.";
                document.form1.username.select();
                document.form1.username.focus();
                flag = "true";
            }

            if (document.form1.company_name.value.length == 0)
            {
                //alert("Please enter company name.");
                msg = msg + "\nPlease enter organisation name.";
                document.form1.company_name.select();
                document.form1.company_name.focus();
                flag = "true";
            }
            /*if (document.form1.brandname.value.length == 0)
             {
             //alert("Please enter brand name.");
             msg = msg + "\nPlease enter brand name.";
             document.form1.brandname.select();
             document.form1.brandname.focus();
             flag = "true";
             }*/

            if (document.form1.sitename.value.length == 0 || document.form1.sitename.value.indexOf(".") <= 0)
            {
                //alert("Please enter valid site name.");
                msg = msg + "\nPlease enter valid site URL.";
                document.form1.sitename.select();
                document.form1.sitename.focus();
                flag = "true";
            }
            if (document.form1.contact_emails.value.length == 0 || document.form1.contact_emails.value.indexOf(".") <= 0 || document.form1.contact_emails.value.indexOf("@") <= 0)
            {
                //alert("Please enter valid contact emailaddress.");
                msg = msg + "\nPlease enter valid contact emailaddress.";
                document.form1.contact_emails.select();
                document.form1.contact_emails.focus();
                flag = "true";
            }
            if (document.form1.contact_persons.value.length == 0)
            {
                //alert("Please enter contact person name.");
                msg = msg + "\nPlease enter contact person name.";
                document.form1.contact_persons.select();
                document.form1.contact_persons.focus();
                flag = "true";
            }
            if (document.form1.telno.value.length == 0)
            {
                //alert("Please enter numeric value Telephone number.");
                msg = msg + "\nPlease enter numeric value in Support number.";
                document.form1.telno.select();
                document.form1.telno.focus();
                flag = "true";
            }

            if (document.form1.maincontact_phone.value.length == 0)
            {
                //alert("Please enter numeric value Telephone number.");
                msg = msg + "\nPlease enter numeric value Contact Telephone number.";
                document.form1.maincontact_phone.select();
                document.form1.maincontact_phone.focus();
                flag = "true";
            }


            if (document.form1.country.value.length == 0)
            {
                //alert("Please enter name of country.");
                msg = msg + "\nPlease enter name of country.";
                //document.form1.country.select();
                document.form1.country.focus();
                flag = "true";
            }

            if (flag == "true")
            {
                alert(msg);
                return false;
            }
            else
                return true;
        }

        function test(c,p)
        {
            myjunk(c,p)
            document.form1.username.focus();
            return true;
        }

    </script>
</head>
<style type="text/css">
    .field-icon
    {
        float: right;
        margin-top: -23px;
        position: relative;
        z-index: 2;
        margin-right: 3px;
    }

    /*Header Styling*/

    .main-topheader {
        position: fixed;
        top: 0;
        padding-top: 15px;
        width: 100%;
        z-index: 9;
        transition: all 0.3s ease;
        color: #fff;
        height:70px;
    }

    .main-topheader.colorize {
        color: #8e8e8e;
        background-color: transparent;
    }


    .main-topheader.fixed .top-nav a {
        color: inherit
    }

    .main-topheader.fixed .top-nav li:last-child .right-arr #right-arr {
        fill: #8e8e8e
    }

    .main-topheader.fixed .main-logo {
        color: #43c6ac
    }

    .main-topheader .main-logo {
        color: inherit
    }

    @-webkit-keyframes shine {
        from {
            -webkit-mask-position: 150%
        }
        to {
            -webkit-mask-position: -50%
        }
    }


    .main-topheader .top-nav {
        float: right;
        margin-bottom: 0
    }

    .main-topheader .top-nav li {
        padding-left: 11px;
        padding-right: 11px;
        vertical-align: middle
    }

    .NewPlatformLabel
    {
        display: inline;
    }

    @media (max-width: 991px) {
        .main-topheader .top-nav li {
            padding-right: 8px;
            padding-left: 8px
        }
    }

    @media (max-width: 767px) {
        .main-topheader {
            padding-top: 15px
        }

        .main-topheader.colorize .top-nav .ghost-btn {
            -webkit-box-shadow: none;
            box-shadow: none;
            background-color: transparent
        }

        .main-topheader.colorize .top-nav .ghost-btn.filled {
            background-color: transparent;
            border-color: transparent
        }
        .main-topheader.fixed .top-nav li:nth-last-child(2) {
            display: none
        }
        .main-topheader.fixed .top-nav li:nth-last-child(3) {
            display: block
        }
        .main-logo {
            position: absolute;
            left: 50%;
            top: 50%;
            margin-top: -14px;
            margin-left: -60px
        }
        .main-logo img {
            width: 140px !important;
        }
        .NewPlatformLabel
        {
            display: none;
        }
    }

</style>

<body onload="test('<%=country%>','<%=phonecc%>');" class="login-container lightmode" <%--style="background-color:#ecf0f1; "--%>>
<div class="widget-header">
    <div class="main-topheader colorize">
        <div class="container">
            <div class="row">
                <div class="col-md-12 clearfix">
                    <%--<a href="#" title="<%=session.getAttribute("company")%>" class="main-logo" ><img src="/images/merchant/<%=logo%>" style="width: 140px;height: 38px;object-fit: contain;"></a>--%>
                        <%
                            if((functions.isEmptyOrNull(currentTheme) && functions.isValueNull(defaultTheme)) && (currentTheme.equalsIgnoreCase("indiaProcessing") || defaultTheme.equalsIgnoreCase("indiaProcessing")))
                            {
                        %>
                        <a href="#" title="<%=session.getAttribute("company")%>" class="main-logo" ><img src="/merchant/images/pay1.png" style="width: 140px;height: 38px;object-fit: contain;"></a>
                        <%}else{
                        %>
                        <a href="#" title="<%=session.getAttribute("company")%>" class="main-logo" ><img src="/images/merchant/<%=logo%>" style="width: <%=logoWidth%>px;height: <%=logoHeight%>px;object-fit: contain;"></a>
                        <%}%>


                        <ul class="top-nav pull-right clearfix list-inline">
                        <p class="loginLabel" style="font-weight:bold;display: inline;color:#ffffff;">Already a user? </p>

                        <li>
                            <a href="/merchant/index.jsp?partnerid=<%=session.getAttribute("partnerid")%>&fromtype=<%=session.getAttribute("company")%>" class="btn btn-green-3" style="font-weight: 600;width: 85px;">Log In</a>
                            <%--  <button type="button" class="btn btn-green-3" style="width: 90px;" >Log In</button>--%>
                        </li>

                    </ul>


                </div>
            </div>
        </div>
    </div>
</div>

<div class="content-page login-container lightmode" id="login_new">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">
                <div class="col-md-12 portlets ui-sortable">
                    <div class="widget">

                        <%-- <div class="widget-header transparent">
                           &lt;%&ndash;please do not delete it below comment if condition keeping for future reference &ndash;%&gt;
                           &lt;%&ndash;&lt;%&ndash;dash;%&gt;
                           &lt;%&ndash;if(functions.isValueNull(partnerIcon))&ndash;%&gt;
                           &lt;%&ndash;{&ndash;%&gt;

                           &lt;%&ndash;dash;%&gt;&ndash;%&gt;
                           &lt;%&ndash;<div class="login-logo" style="&ndash;%&gt;
                           &lt;%&ndash;background: url(/images/merchant/<%=partnerIcon%>) top center no-repeat;&ndash;%&gt;
                           &lt;%&ndash;width: 100%;&ndash;%&gt;
                           &lt;%&ndash;height: 70px;&ndash;%&gt;
                           &lt;%&ndash;float: left;&ndash;%&gt;
                           &lt;%&ndash;background-size: contain;&ndash;%&gt;
                           &lt;%&ndash;margin-bottom: 10px">&ndash;%&gt;
                           &lt;%&ndash;</div>&ndash;%&gt;

                           &lt;%&ndash;<div class="login-logo" style="&ndash;%&gt;
                           &lt;%&ndash;background: url(/images/merchant/<%=logo%>) top center no-repeat;&ndash;%&gt;
                           &lt;%&ndash;width: 100%;&ndash;%&gt;
                           &lt;%&ndash;height: 50px;&ndash;%&gt;
                           &lt;%&ndash;float: left;&ndash;%&gt;
                           &lt;%&ndash;background-size: contain;&ndash;%&gt;
                           &lt;%&ndash;margin-bottom: 20px;">&ndash;%&gt;
                           &lt;%&ndash;</div>&ndash;%&gt;
                           &lt;%&ndash;&lt;%&ndash;dash;%&gt;
                           &lt;%&ndash;}&ndash;%&gt;
                           &lt;%&ndash;else&ndash;%&gt;
                           &lt;%&ndash;{&ndash;%&gt;
                           &lt;%&ndash;dash;%&gt;&ndash;%&gt;

                           &lt;%&ndash;<div id="signUpBrandLogo" class="login-logo" style="&ndash;%&gt;
                           &lt;%&ndash;background: url(/images/merchant/<%=logo%>) top center no-repeat;&ndash;%&gt;
                           &lt;%&ndash;width: 100%;&ndash;%&gt;
                           &lt;%&ndash;height: 50px;&ndash;%&gt;
                           &lt;%&ndash;float: left;&ndash;%&gt;
                           &lt;%&ndash;background-size: contain;&ndash;%&gt;
                           &lt;%&ndash;margin-bottom: 20px;">&ndash;%&gt;
                           &lt;%&ndash;</div>&ndash;%&gt;
                           &lt;%&ndash;&lt;%&ndash;dash;%&gt;
                           &lt;%&ndash;}&ndash;%&gt;

                           &lt;%&ndash;dash;%&gt;&ndash;%&gt;



                           <%
                             if(functions.isValueNull(partnerIcon))
                             {
                           %>
                           <div class="login-logo-icon">
                             <img src="/images/merchant/<%=partnerIcon%>" alt="<%=partnerIcon%>"/>
                           </div>

                           <%
                             }
                           %>


                           <div class="login-logo">
                             <img src="/images/merchant/<%=logo%>" alt="<%=logo%>"/>
                           </div>

                         </div>--%>


                        <div class="login-body" align="center" id="log_alignsign">

                            <div class="login-title" style="margin-top: 0;"><p align="center">Sign up to create an account with us</p></div>

                            <%
                                if (mes.equals("F"))
                                {
                                    out.println("<table align=\"center\" width=\"60%\" ><tr><td><font class=\"text\" >You have <b>NOT FILLED</b> some of required details or some of details filled by you are incorrect. Please fill all the details completely before going for next step.</font>");
                                    out.println("</td></tr><tr><td algin=\"center\" ><font face=\"arial\" class=\"text\"  size=\"2\">");
                                    errormsg = errormsg.replace("&lt;BR&gt;","<BR>");
                                    out.println(errormsg);
                                    out.println("</font>");
                                    out.println("</td></tr></table>");

                                }
                                else if (mes.equals("X"))
                                {
                                    out.println("<table align=\"center\" width=\"60%\" ><tr><td><font class=\"text\" >You have <b>NOT FILLED</b> some of required details or some of details filled by you are incorrect. Please fill all the details completely before going for next step.<br>");
                                    //out.println("<br><font face=\"arial\" color=\"red\" size=\"2\"> align=\"center\"");
                                    errormsg = errormsg.replace("&lt;BR&gt;","<BR>");
                                    out.println(errormsg);
                                    out.println("</font>");
                                    out.println("</td></tr></table>");
                                }
                            %>


                            <form action="/merchant/servlet/NewMerchant?ctoken=<%=ctoken%>" method="post" name="form1" id="form1" <%--onsubmit="return check();"--%> class="form-horizontal" style="padding: 10px;">
                                <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>">
                                <input  name="partnerid" type="hidden" value="<%=partnerid%>">
                                <input id="isEncrypted" name="isEncrypted" type="hidden" value="false">
                                <input name="defaulttheme" type="hidden" value="<%=defaultTheme%>" >
                                <input name="currenttheme" type="hidden" value="<%=currentTheme%>" >
                                <input name="company" type="hidden" value="<%=partnerName%>" >
                                <input type="hidden" id="sendEmailNotification" value="" name="sendEmailNotification">

                                <script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
                                <script type="text/javascript" src="/merchant/javascript/jquery.jcryption.js?ver=1"></script>
                                <script language="javascript">

                                    function myjunk(country,phonecc)
                                    {

                                        var e = document.getElementById("country");
                                        var strUser = e.options[e.selectedIndex].value;
                                        if(country && phonecc)
                                        {
                                            $("#phonecc").val(phonecc);
                                            $("#country").val(country+"|"+phonecc);
                                        }
                                        else if (strUser != 'Select one')
                                        {
                                            $("#phonecc").val(strUser.split("|")[1]);
                                        }
                                        /*var hat = this.document.form1.country.selectedIndex;
                                         var hatto = this.document.form1.country.options[hat].value;
                                         var countrycd = this.document.form1.phonecc.value = hatto.split("|")[1];
                                         var telnumb = this.document.form1.telno.value;
                                         //  var cctel = countrycd.concat(telnumb);
                                         if (hatto != 'Select one') {

                                         this.document.form1.countrycode.value = hatto.split("|")[0];
                                         this.document.form1.phonecc.value = hatto.split("|")[1];
                                         this.document.form1.country.options[0].selected=false;

                                         }*/
                                    }
                                </script>

                                <div class="widget-content padding">
                                    <div class="content">
                                        <div class="form-group">
                                            <div class="modal" id="myModal" role="dialog">
                                                <div id="target" class="modal-dialog modal-dialog-centered" >
                                                    <div class="modal-content" style="color: #0d0b0b;">
                                                        <div class="modal-header">
                                                            <h4  class="modal-title">Send Notification</h4>
                                                        </div>
                                                        <div class="modal-body">
                                                            <p> <h4>Send email notification to your main contact.</h4></p>
                                                        </div>
                                                        <div class="modal-footer" >
                                                            <button style="width: 60px;" type="button" id="yesSubmit" <%--onclick="submitForm('Y')"--%> class="btn btn-default" data-dismiss="modal">Yes</button>
                                                            <button style="width: 60px;" type="button" id="noSubmit"  <%--onclick="submitForm('N')"--%> class="btn btn-default" data-dismiss="modal">No</button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <%--<label class="col-md-6 control-label">Username*<br>
                                            (Username Should Not Contain Special Characters like !@#$%)</label>--%>
                                        <div class="col-md-12">
                                            <input class="form-control" placeholder="Username or Email*" type="Text" maxlength="50"  maxlength = 50 value="<%=ESAPI.encoder().encodeForHTMLAttribute(username)%>" name="username" size="35">(Username Should Not Contain Special Characters like !#$%)
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <%-- <label class="col-md-6 control-label">Password*<br>
                                             (Passwords length should be at least 8 and should contain alphabet, numeric, and special characters like !@#$)</label>--%>
                                        <div class="col-md-12">
                                            <input id="passwd" class="form-control" placeholder="Password*" type="Password" maxlength="20"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(passwd)%>" name="passwd" size="35"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="showHidepass" onclick="hideshowpass('showHidepass','passwd')"></span>
                                            (Length should be between 8 to 20 chars and should contain alphabet, numeric, and at least 1 special chars out of: !@#$)
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <%--<label class="col-md-6 control-label">Confirm Password*<br>
                                            (Should be same as PASSWORD)</label>--%>
                                        <div class="col-md-12">
                                            <input id="conpasswd" class="form-control" placeholder="Confirm Password*" type="Password" maxlength="20" value="<%=ESAPI.encoder().encodeForHTMLAttribute(conpasswd)%>" name="conpasswd" size="35"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="conshowHidepass" onclick="hideshowpass('conshowHidepass','conpasswd')"></span>
                                        </div>
                                    </div>

                                    <%--  <hr class="hrnew">--%>

                                    <div class="form-group">
                                        <%--<label class="col-md-6 control-label">Organisation Name*</label>--%>
                                        <div class="col-md-12">
                                            <input class="form-control" type="Text" placeholder="Organisation Name*" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(company_name)%>" name="company_name" size="35">
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <%--<label class="col-md-6 control-label">Site URL*<br>
                                            (Ex. http://www.abc.com)</label>--%>
                                        <div class="col-md-12">
                                            <input class="form-control" type="Text" maxlength="100" placeholder="Site URL*"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(sitename)%>" name="sitename" size="35">(Ex. http://www.abc.com)
                                        </div>
                                    </div>

                                    <div class="form-group ">
                                        <div class="col-md-12 has-feedback">
                                             <select name="country"placeholder="Country*" id="country"  style="color: black" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>"  onchange="myjunk('<%=country%>','<%=phonecc%>');"  ><span class="pull-right"><i class="fa fa-caret-down"></i></span>
                                               <option value="" color: rgb(156, 156, 156);>Country*</option>
                                               <option value="AF|093">Afghanistan</option>
                                               <option value="AX|358">Aland Islands</option>
                                               <option value="AL|355">Albania</option>
                                               <option value="DZ|231">Algeria</option>
                                               <option value="AS|684">American Samoa</option>
                                               <option value="AD|376">Andorra</option>
                                               <option value="AO|244">Angola</option>
                                               <option value="AI|001">Anguilla</option>
                                               <option value="AQ|000">Antarctica</option>
                                               <option value="AG|001">Antigua and Barbuda</option>
                                               <option value="AR|054">Argentina</option>
                                               <option value="AM|374">Armenia</option>
                                               <option value="AW|297">Aruba</option>
                                               <option value="AU|061">Australia</option>
                                               <option value="AT|043">Austria</option>
                                               <option value="AZ|994">Azerbaijan</option>
                                               <option value="BS|001">Bahamas</option>
                                               <option value="BH|973">Bahrain</option>
                                               <option value="BD|880">Bangladesh</option>
                                               <option value="BB|001">Barbados</option>
                                               <option value="BY|375">Belarus</option>
                                               <option value="BE|032">Belgium</option>
                                               <option value="BZ|501">Belize</option>
                                               <option value="BJ|229">Benin</option>
                                               <option value="BM|001">Bermuda</option>
                                               <option value="BT|975">Bhutan</option>
                                               <option value="BO|591">Bolivia</option>
                                               <option value="BA|387">Bosnia and Herzegovina</option>
                                               <option value="BW|267">Botswana</option>
                                               <option value="BV|000">Bouvet Island</option>
                                               <option value="BR|055">Brazil</option>
                                               <option value="IO|246">British Indian Ocean Territory</option>
                                               <option value="VG|001">British Virgin Islands</option>
                                               <option value="BN|673">Brunei</option>
                                               <option value="BG|359">Bulgaria</option>
                                               <option value="BF|226">Burkina Faso</option>
                                               <option value="BI|257">Burundi</option>
                                               <option value="KH|855">Cambodia</option>
                                               <option value="CM|237">Cameroon</option>
                                               <option value="CA|001">Canada</option>
                                               <option value="CV|238">Cape Verde</option>
                                               <option value="KY|001">Cayman Islands</option>
                                               <option value="CF|236">Central African Republic</option>
                                               <option value="TD|235">Chad</option>
                                               <option value="CL|056">Chile</option>
                                               <option value="CN|086">China</option>
                                               <option value="CX|061">Christmas Island</option>
                                               <option value="CC|061">Cocos (Keeling) Islands</option>
                                               <option value="CO|057">Colombia</option>
                                               <option value="KM|269">Comoros</option>
                                               <option value="CK|682">Cook Islands</option>
                                               <option value="CR|506">Costa Rica</option>
                                               <option value="CI|225">Cote d'Ivoire</option>
                                               <option value="HR|385">Croatia</option>
                                               <option value="CU|053">Cuba</option>
                                               <option value="CUW|599">Curacao</option>
                                               <option value="CY|357">Cyprus</option>
                                               <option value="CZ|420">Czech Republic</option>
                                               <option value="CD|243">Democratic Republic of the Congo</option>
                                               <option value="DK|045">Denmark</option>
                                               <option value="DJ|253">Djibouti</option>
                                               <option value="DM|001">Dominica</option>
                                               <option value="DO|001">Dominican Republic</option>
                                               <option value="EC|593">Ecuador</option>
                                               <option value="EG|020">Egypt</option>
                                               <option value="SV|503">El Salvador</option>
                                               <option value="GQ|240">Equatorial Guinea</option>
                                               <option value="ER|291">Eritrea</option>
                                               <option value="EE|372">Estonia</option>
                                               <option value="ET|251">Ethiopia</option>
                                               <option value="FK|500">Falkland Islands</option>
                                               <option value="FO|298">Faroe Islands</option>
                                               <option value="FJ|679">Fiji</option>
                                               <option value="FI|358">Finland</option>
                                               <option value="FR|033">France</option>
                                               <option value="GF|594">French Guiana</option>
                                               <option value="PF|689">French Polynesia</option>
                                               <option value="TF|000">French Southern and Antarctic Lands</option>
                                               <option value="GA|241">Gabon</option>
                                               <option value="GM|220">Gambia</option>
                                               <option value="GE|995">Georgia</option>
                                               <option value="DE|049">Germany</option>
                                               <option value="GH|233">Ghana</option>
                                               <option value="GI|350">Gibraltar</option>
                                               <option value="GR|030">Greece</option>
                                               <option value="GL|299">Greenland</option>
                                               <option value="GD|001">Grenada</option>
                                               <option value="GP|590">Guadeloupe</option>
                                               <option value="GU|001">Guam</option>
                                               <option value="GT|502">Guatemala</option>
                                               <option value="GG|000">Guernsey</option>
                                               <option value="GN|224">Guinea</option>
                                               <option value="GW|245">Guinea-Bissau</option>
                                               <option value="GY|592">Guyana</option>
                                               <option value="HT|509">Haiti</option>
                                               <option value="HM|672">Heard Island & McDonald Islands</option>
                                               <option value="HN|504">Honduras</option>
                                               <option value="HK|852">Hong Kong</option>
                                               <option value="HU|036">Hungary</option>
                                               <option value="IS|354">Iceland</option>
                                               <option value="IN|091">India</option>
                                               <option value="ID|062">Indonesia</option>
                                               <option value="IR|098">Iran</option>
                                               <option value="IQ|964">Iraq</option>
                                               <option value="IE|353">Ireland</option>
                                               <option value="IL|972">Israel</option>
                                               <option value="IT|039">Italy</option>
                                               <option value="JM|001">Jamaica</option>
                                               <option value="JP|081">Japan</option>
                                               <option value="JE|044">Jersey</option>
                                               <option value="JO|962">Jordan</option>
                                               <option value="KZ|007">Kazakhstan</option>
                                               <option value="KE|254">Kenya</option>
                                               <option value="KI|686">Kiribati</option>
                                               <option value="KW|965">Kuwait</option>
                                               <option value="KG|996">Kyrgyzstan</option>
                                               <option value="LA|856">Laos</option>
                                               <option value="LV|371">Latvia</option>
                                               <option value="LB|961">Lebanon</option>
                                               <option value="LS|266">Lesotho</option>
                                               <option value="LR|231">Liberia</option>
                                               <option value="LY|218">Libya</option>
                                               <option value="LI|423">Liechtenstein</option>
                                               <option value="LT|370">Lithuania</option>
                                               <option value="LU|352">Luxembourg</option>
                                               <option value="MO|853">Macau, China</option>
                                               <option value="MK|389">Macedonia</option>
                                               <option value="MG|261">Madagascar</option>
                                               <option value="MW|265">Malawi</option>
                                               <option value="MY|060">Malaysia</option>
                                               <option value="MV|960">Maldives</option>
                                               <option value="ML|223">Mali</option>
                                               <option value="MT|356">Malta</option>
                                               <option value="MH|692">Marshall Islands</option>
                                               <option value="MQ|596">Martinique</option>
                                               <option value="MR|222">Mauritania</option>
                                               <option value="MU|230">Mauritius</option>
                                               <option value="YT|269">Mayotte</option>
                                               <option value="MX|052">Mexico</option>
                                               <option value="FM|691">Micronesia, Federated States of</option>
                                               <option value="MD|373">Moldova</option>
                                               <option value="MC|377">Monaco</option>
                                               <option value="MN|976">Mongolia</option>
                                               <option value="ME|382">Montenegro</option>
                                               <option value="MS|001">Montserrat</option>
                                               <option value="MA|212">Morocco</option>
                                               <option value="MZ|258">Mozambique</option>
                                               <option value="MM|095">Myanmar</option>
                                               <option value="NA|264">Namibia</option>
                                               <option value="NR|674">Nauru</option>
                                               <option value="NP|977">Nepal</option>
                                               <option value="AN|599">Netherlands Antilles</option>
                                               <option value="NL|031">Netherlands</option>
                                               <option value="NC|687">New Caledonia</option>
                                               <option value="NZ|064">New Zealand</option>
                                               <option value="NI|505">Nicaragua</option>
                                               <option value="NE|227">Niger</option>
                                               <option value="NG|234">Nigeria</option>
                                               <option value="NU|683">Niue</option>
                                               <option value="NF|672">Norfolk Island</option>
                                               <option value="KP|850">North Korea</option>
                                               <option value="MP|001">Northern Mariana Islands</option>
                                               <option value="NO|047">Norway</option>
                                               <option value="OM|968">Oman</option>
                                               <option value="PK|092">Pakistan</option>
                                               <option value="PW|680">Palau</option>
                                               <option value="PS|970">Palestinian Authority</option>
                                               <option value="PA|507">Panama</option>
                                               <option value="PG|675">Papua New Guinea</option>
                                               <option value="PY|595">Paraguay</option>
                                               <option value="PE|051">Peru</option>
                                               <option value="PH|063">Philippines</option>
                                               <option value="PN|064">Pitcairn Islands</option>
                                               <option value="PL|048">Poland</option>
                                               <option value="PT|351">Portugal</option>
                                               <option value="PR|001">Puerto Rico</option>
                                               <option value="QA|974">Qatar</option>
                                               <option value="CG|242">Republic of the Congo</option>
                                               <option value="RE|262">Reunion</option>
                                               <option value="RO|040">Romania</option>
                                               <option value="RU|007">Russia</option>
                                               <option value="RW|250">Rwanda</option>
                                               <option value="BL|590">Saint Barthelemy</option>
                                               <option value="SH|290">Saint Helena, Ascension & Tristan daCunha</option>
                                               <option value="KN|001">Saint Kitts and Nevis</option>
                                               <option value="LC|001">Saint Lucia</option>
                                               <option value="MF|590">Saint Martin</option>
                                               <option value="PM|508">Saint Pierre and Miquelon</option>
                                               <option value="VC|001">Saint Vincent and Grenadines</option>
                                               <option value="WS|685">Samoa</option>
                                               <option value="SM|378">San Marino</option>
                                               <option value="ST|239">Sao Tome and Principe</option>
                                               <option value="SA|966">Saudi Arabia</option>
                                               <option value="SN|221">Senegal</option>
                                               <option value="RS|381">Serbia</option>
                                               <option value="SC|248">Seychelles</option>
                                               <option value="SL|232">Sierra Leone</option>
                                               <option value="SG|065">Singapore</option>
                                               <option value="SK|421">Slovakia</option>
                                               <option value="SI|386">Slovenia</option>
                                               <option value="SB|677">Solomon Islands</option>
                                               <option value="SO|252">Somalia</option>
                                               <option value="ZA|027">South Africa</option>
                                               <option value="GS|000">South Georgia & South Sandwich Islands</option>
                                               <option value="KR|082">South Korea</option>
                                               <option value="ES|034">Spain</option>
                                               <option value="LK|094">Sri Lanka</option>
                                               <option value="SD|249">Sudan</option>
                                               <option value="SR|597">Suriname</option>
                                               <option value="SJ|047">Svalbard and Jan Mayen</option>
                                               <option value="SZ|268">Swaziland</option>
                                               <option value="SE|046">Sweden</option>
                                               <option value="CH|041">Switzerland</option>
                                               <option value="SY|963">Syria</option>
                                               <option value="TW|886">Taiwan</option>
                                               <option value="TJ|992">Tajikistan</option>
                                               <option value="TZ|255">Tanzania</option>
                                               <option value="TH|066">Thailand</option>
                                               <option value="TL|670">Timor-Leste</option>
                                               <option value="TG|228">Togo</option>
                                               <option value="TK|690">Tokelau</option>
                                               <option value="TO|676">Tonga</option>
                                               <option value="TT|001">Trinidad and Tobago</option>
                                               <option value="TN|216">Tunisia</option>
                                               <option value="TR|090">Turkey</option>
                                               <option value="TM|993">Turkmenistan</option>
                                               <option value="TC|001">Turks and Caicos Islands</option>
                                               <option value="TV|688">Tuvalu</option>
                                               <option value="UG|256">Uganda</option>
                                               <option value="UA|380">Ukraine</option>
                                               <option value="AE|971">United Arab Emirates</option>
                                               <option value="GB|044">United Kingdom</option>
                                               <option value="US|001">United States</option>
                                               <option value="VI|001">United States Virgin Islands</option>
                                               <option value="UY|598">Uruguay</option>
                                               <option value="UZ|998">Uzbekistan</option>
                                               <option value="VU|678">Vanuatu</option>
                                               <option value="VA|379">Vatican City</option>
                                               <option value="VE|058">Venezuela</option>
                                               <option value="VN|084">Vietnam</option>
                                               <option value="WF|681">Wallis and Futuna</option>
                                               <option value="EH|212">Western Sahara</option>
                                               <option value="YE|967">Yemen</option>
                                               <option value="ZM|260">Zambia</option>
                                               <option value="ZW|263">Zimbabwe</option>
                                             </select>

                                          <%--  <input type="text" class="form-control country" placeholder="Country" id="country-input" class="country" autocomplete="false" onchange="countryhide('country-input','country','phonecc','phonecc');" required>
                                            <input type="hidden" id="country" name="country" value='<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>' id="getcountval" type="hidden">--%>

                                        </div>

                                        <%--<div class="form-group col-md-4 has-feedback">
                                            <input type="hidden" class="form-control" name="countrycode" &lt;%&ndash;style="width: 50px"&ndash;%&gt; size="10" >
                                        </div>--%>

                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-3">
                                            <input type="text" class="form-control" name="phonecc" id="phonecc"  size="5" style="color:black;" placeholder="CC" ></div>

                                        <div class="col-md-9">
                                            <input class="form-control" type="Text" maxlength="20" placeholder="Support Number*"  name="telno" >(Allows only numeric value)
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <%--<label class="col-md-6 control-label">Contact Email Address*</label>--%>
                                        <div class="col-md-12">
                                            <input class="form-control" type="Text" placeholder="Contact Email Address*" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_emails)%>" name="contact_emails" size="35">
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <%--<label class="col-md-6 control-label">Contact Person's Name*</label>--%>
                                        <div class="col-md-12">
                                            <input class="form-control" type="Text" placeholder="Contact Person's Name*" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_persons)%>" name="contact_persons" size="35">
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <%--<label class="col-md-6 control-label">Contact Person's Name*</label>--%>
                                            <div class="col-md-3">
                                                <input type="text" class="form-control" name="mainContactcc" id="mainContactcc" maxlength="3"  onkeypress="return isNumberKey(event)" size="5" style="color:black;" placeholder="CC" ></div>
                                        <div class="col-md-9">
                                            <input class="form-control" name="maincontact_phone" type="Text" placeholder="Contact Telephone Number*" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(maincontact_phone)%>"  size="16">
                                        </div>
                                        <div class="col-md-8">

                                            (Allows only numeric value,+,-)
                                        </div>
                                    </div>

                                    <div class="form-group" style="text-align:center;border-left: 4px solid #34495e;border: #ffffff">
                                        <i class="fa fa-right"></i>&nbsp;&nbsp;
                                        <input type="checkbox" name="checkbox" id="check" value="check">&nbsp;<b>I accept that all personal data I submit may be processed in the manner and purposes described in <%=partnername%>'s<br> <a href=<%=privacyurl%> target=<%=target1%>  >Privacy Policy</a> and <a href=<%=termsurl%> target=<%=target%>>Terms of use.</a> </b>
                                    </div>

                                    <div class="form-group" style="text-align:center;color: #ffffff;border-left: 4px solid #34495e;border: #ffffff;display: none;">
                                        <i class="fa fa-right"></i>&nbsp;&nbsp;
                                        <input type="hidden" name="consent" id="consent" value="I accept that all personal data I submit may be processed in the manner and purposes described in <%=partnername%>'s Privacy Policy and Terms of use." ></b>
                                    </div>

                                    <%--    <div class="form-group">
                                            &lt;%&ndash;<label class="col-md-6 control-label">Country*</label>&ndash;%&gt;
                                            <div class="col-md-12">
                                                <input class="form-control" type="Text" maxlength="25" placeholder="Country*" value="<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>" name="country" size="35">
                                            </div
                                        </div>--%>

                                    <input type="hidden" name="emailtoken" value="<%=etoken%>">

                                    <input type="hidden" value="-" name="bussinessdevexecutive">


                                </div>
                                <input type="hidden" value="1" name="step">


                                <div class="login-subtitle" style="line-height: 35px;">
                                    <button type="button"  id="btnSubmit" class="btn btn-info btn-block" style="font-size:14px !important;font-family: 'Open Sans', sans-serif;font-size: 12px;background-color: #29AAA1;border-color:#29AAA1; ">Submit</button>
                                </div>
                            </form>

                        </div>

                       <%-- <div class="form-group col-md-12 has-feedback btn btn-green-3" style="width: 100%;">
                            <input type="hidden" value="1" name="step">
                            <label >&nbsp;</label>
                            <input type="hidden" value="1" name="step">
                            &lt;%&ndash;<a id="prev" style="cursor: pointer; font-weight:600;display: inline;font-size:16px; opacity: inherit; user-select: inherit; pointer-events: inherit;" class="btn btn-green-3" title="Previous"><i class="fa-solid fa-less-than"></i>&nbsp;&nbsp;Previous</a>&ndash;%&gt;
                            <button type="button" class="btn btn-green-3" id="btnSubmit" style="font-size:16px;font-weight:600;display: inline;width: 150px;" >&lt;%&ndash;<i class="fa fa-save"></i>&ndash;%&gt;&nbsp;&nbsp;Submit</button>
                            &lt;%&ndash;<a id="next" style="display: inline;font-size:16px;cursor: pointer; opacity: inherit;font-weight:600; user-select: inherit; pointer-events: inherit;" class="btn btn-green-3" title="Next">Next&nbsp;&nbsp;<i class="fa-solid fa-greater-than"></i></a>&ndash;%&gt;
                        </div>--%>


                    </div>
                    <%--<h5 class="bg-infoorange" style="text-align: center;"><i class="fa fa-exclamation-triangle"></i>&nbsp;&nbsp;KINDLY KEEP JAVASCRIPT ENABLED IN YOUR BROWSER FOR SECURITY PURPOSES.</h5>--%>
                </div>
            </div>
        </div>
    </div>
</div>

<%
    if((functions.isEmptyOrNull(currentTheme) && functions.isValueNull(defaultTheme)) && (currentTheme.equalsIgnoreCase("indiaProcessing") || defaultTheme.equalsIgnoreCase("indiaProcessing")))
    {
%>
<div id="footer" class="footer-new">

    <div class="container">
        <div class="row">
            <div class="col-md-6 one">
                <div class="policy_section">
                    <a href="https://hrm.paymentz.com/paymentz/?page_id=7533" target="_blank">Cookie Policy</a>
                    <a href="https://hrm.paymentz.com/paymentz/?page_id=7335" target="_blank">Privacy Policy</a>
                    <a href="https://hrm.paymentz.com/paymentz/?page_id=7227" target="_blank">Terms of Use</a>
                </div>
            </div>
            <div class="col-md-6 two">
                <div class="reserve_section">
                    &copy;&nbsp; <a href="https://hrm.paymentz.com/paymentz/" target="_blank">PAYMENTZ</a>&nbsp;  2022 All Rights Reserved
                </div>
            </div>
        </div>
    </div>
</div>
<%
    }
%>


<script type="text/javascript" src="/merchant/NewCss/js/materialize.min.js"></script>
<script type="text/javascript" src="/merchant/javascript/countrieslist.js" charset="utf-8"></script>
<script src="/merchant/javascript/hidde.js"></script>

</body>
<script>
    (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
        (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
            m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
    })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

    ga('create', 'UA-40647071-1', 'auto');
    ga('send', 'pageview');

</script>

<script>

    var custom_nav = document.querySelector(".main-topheader");

   /* window.onscroll = function(){
        if(document.documentElement.scrollTop > 50 ) {
            custom_nav.classList.add("header-scrolled");
        }else {
            custom_nav.classList.remove("header-scrolled");
        }
    }*/

    $(window).scroll(function (event) {
        var scroll = $(window).scrollTop();
        // Do something
        if(scroll > 50 ) {
            custom_nav.classList.add("header-scrolled");
            $('.main-topheader img').attr('src','/merchant/images/pay2.png');

        }else {
            custom_nav.classList.remove("header-scrolled");
            $('.main-topheader img').attr('src','/merchant/images/pay1.png');
        }
    });




    function isNumberKey(evt)
    {
        var charCode = (evt.which) ? evt.which : event.keyCode
        if (charCode > 31 && (charCode < 48 || charCode > 57))
            return false;

        return true;
    }

</script>


</html>
