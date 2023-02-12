<%--
<!DOCTYPE html>
--%>
<%@ page language="java" %>
<%@ page import="com.directi.pg.DefaultUser,
                 com.directi.pg.Functions,
                 com.directi.pg.LoadProperties,
                 com.logicboxes.util.ApplicationProperties" %>
<%@ page import="net.partner.PartnerFunctions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.manager.dao.PartnerDAO" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>

<%String loginType = ApplicationProperties.getProperty("LOGIN_TYPE");
%>


<%

    session.setAttribute("valid", "true");

    String ctoken= request.getParameter("ctoken");
    User user =  (User)session.getAttribute("Anonymous");
    if(ctoken !=null)
    {
        if(!Functions.validateCSRFAnonymos(ctoken,user))
        {
            response.sendRedirect("/partner/sessionout.jsp");
            return;
        }

    }
    else if(user!=null)
    {

        ctoken = user.getCSRFToken();

    }
    else
    {
        user =   new DefaultUser(User.ANONYMOUS.getName());
        ctoken = (user).resetCSRFToken();

    }

    Functions functions = new Functions();

    String logoHeight="38";
    String logoWidth="140";
    String partner_id ="";


    PartnerDAO partnerDAO = new PartnerDAO();
    PartnerFunctions partnerFunctions = new PartnerFunctions();

    if(partnerFunctions.isValueNull(request.getParameter("partnerid"))){
        partner_id = request.getParameter("partnerid");
    }else{
        partner_id = (String) session.getAttribute("partnerid");
    }
    PartnerDetailsVO partnerDetailsVOMain = null;
    if(functions.isValueNull(partner_id))
    {
        partnerDetailsVOMain = partnerDAO.geturl(partner_id);
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

    session.setAttribute("Anonymous", user);

    String icon = "";
    String logo = "";
    String favicon = "";

    Map<String, String> logoMap = partnerFunctions.getPartnerLogoAndIcon(partner_id);
    if (partnerFunctions.isValueNull(logoMap.get("logoName")))
    {
        logo = logoMap.get("logoName");
    }
    else if (partnerFunctions.isValueNull((String) session.getAttribute("logo")))
    {
        logo = (String) session.getAttribute("logo");
    }
    /*else
    {
        logo = "pay2.png";
    }*/
    if (partnerFunctions.isValueNull(logoMap.get("iconName")))
    {
        icon = logoMap.get("iconName");
    }
    else if (partnerFunctions.isValueNull((String) session.getAttribute("icon")))
    {
        icon = (String) session.getAttribute("icon");
    }
    if (partnerFunctions.isValueNull(logoMap.get("faviconName")))
    {
        favicon = logoMap.get("faviconName");
    }
    session.setAttribute("favicon",favicon);
    /*else
    {
        icon = "Pay_Icon.png";
    }*/
    String PrivacyUrl = "";
    String URL = partnerFunctions.getPrivacyURL(partner_id);
    if (partnerFunctions.isValueNull(URL))
    {
        PrivacyUrl = URL;
    }
    String getCookiesURL ="";
    String cookies = partnerFunctions.getCookiesURL(partner_id);
    if (partnerFunctions.isValueNull(cookies))
    {
        getCookiesURL = cookies;
    }

    final ResourceBundle RBundel1 = LoadProperties.getProperty("com.directi.pg.CertificateLink");
    String pciurl= RBundel1.getString("PCILINK");
%>

<!DOCTYPE html>
<html lang="en" class="body-full-height">
<head>
    <title>Partner Administration Login</title>

    <!-- META SECTION -->
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />

    <!-- END META SECTION -->

    <!-- CSS INCLUDE -->
    <link rel = "icon" type = "image/png"  href="/images/partner/<%=favicon%>">
    <link rel="stylesheet" type="text/css" id="theme" href="/partner/NewCss/theme-default.css"/>
    <link rel="stylesheet" type="text/css"  href="/partner/NewCss/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.2.0/css/all.css" integrity="sha384-hWVjflwFxL6sNzntih27bfxkr27PmbbK/iSvJ+a4+0owXq79v+lsFkW54bOGbiDQ" crossorigin="anonymous">
    <%--    <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fontawesome/font-awesome.min.css"/>
        <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fonts/fontawesome-webfont.eot"/>
        <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fonts/fontawesome-webfont.svg"/>
        <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fonts/fontawesome-webfont.woff"/>
        <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fonts/glyphicons-halflings-regular.eot"/>
        <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fonts/glyphicons-halflings-regular.svg"/>
        <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fonts/glyphicons-halflings-regular"/>
        <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fonts/glyphicons-halflings-regular.woff"/>--%>
    <!-- EOF CSS INCLUDE -->
    <script src="/merchant/javascript/hidde.js"></script>
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
        .login-body {

            margin-top: 10px;
        }

    </style>


    <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
    <script type="text/javascript" src="/partner/javascript/jquery.jcryption.js?ver=1"></script>
    <%--<script type="text/javascript">
        $(document).ready(function() {

            document.getElementById('submit').disabled =  false;
            $("#submit").click(function() {
                var encryptedString =  $.jCryption.encrypt($("#password").val(), $("#ctoken").val());
                document.getElementById('password').value =  encryptedString;
                document.getElementById('isEncrypted').value =  true;
            });

        });
    </script>--%>
</head>
<body>

<div class="login-container lightmode">
    <div class="widget-header transparent">
        <div class="main-topheader colorize">
            <div class="container">
                <div class="row">
                    <div class="col-md-12 clearfix">
                        <a href="#" title="<%=logo%>" class="main-logo" ><img src="/images/partner/<%=logo%>"  style="width: <%=logoWidth%>px;height: <%=logoHeight%>px;object-fit: contain;"></a>

                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="login-box animated fadeInDown">
        <%--        <img src="/partner/images/Pay_Icon.png" border="0" width="15%" style=" margin-left: 42%;">
                <div class="login-logo" style="
                     background: url(/partner/images/pay2.png) top center no-repeat;
                width: 100%;
                height: 50px;
                float: left;
                margin-bottom: 10px;">--%>


        <%--<div class="login-logo" style="--%>
            <%--/*background: url(/images/partner/Pay_Icon.png) top center no-repeat;*/--%>
            <%--background: url(/images/partner/<%=icon%>) top center no-repeat;--%>
            <%--width: 100%;--%>
            <%--height: 70px;--%>
            <%--float: left;--%>
            <%--background-size: contain;--%>
            <%--margin-bottom: 10px">--%>
        <%--</div>--%>

        <%--<div class="login-logo" style="--%>
            <%--/*background: url(/images/partner/pay2.png) top center no-repeat;*/--%>
            <%--background: url(/images/partner/<%=logo%>) top center no-repeat;--%>
            <%--width: 100%;--%>
            <%--height: 50px;--%>
            <%--float: left;--%>
            <%--background-size: contain;--%>
            <%--margin-bottom: 20px;">--%>
        <%--</div>--%>

           <%-- <%
                if(partnerFunctions.isValueNull(icon))
                {
            %>
            <div class="login-logo-icon">
                <img src="/images/partner/<%=icon%>" alt="<%=icon%>"/>
            </div>
            <%
                }
            %>--%>


           <%-- <div class="login-logo">
                <img src="/images/partner/<%=logo%>" alt="<%=logo%>"/>
            </div>--%>



        <div class="login-body">
            <div class="login-title" style="margin-bottom: 10px;text-align: center;">Login to <%=loginType%> Partner Account</div>
            <form autocomplete="off" action="/partner/net/Login?ctoken=<%=ctoken%>" method="post" class="form-horizontal">
                <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
                <input  name="logo_reset" type="hidden" value="<%=logo%>" >
                <input  name="icon_reset" type="hidden" value="<%=icon%>" >

                <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >


                <%
                    String action = ESAPI.encoder().encodeForHTML(request.getParameter("action"));
                    if (action != null)
                    {
                %>
                <table width="100%">
                    <tr>
                        <td class="text" align="center">

                            <%
                                if (action.equals("F"))
                                {
                                    out.println("*Invalid Credentials.");
                                }
                                if (action.equals("L"))
                                {
                                    out.println("*Your Partner Account has been Locked. Kindly Contact the admin Support Desk.");
                                }
                                if (action.equals("D"))
                                {
                                    out.println("*Your Partner Account has been Disabled. Kindly Contact the admin Support Desk.");
                                }
                                if (action.equals("E"))
                                {
                                    //out.println("*Invalid Username and Password.");
                                    out.println("*Username and Password should not be empty.");
                                }
                                if (action.equals("IP"))
                                {
                                    out.println("*Your IP is not white listed with us. Kindly Contact the admin Support Desk.");
                                }
                                if (action.equals("AD"))
                                {
                                    out.println("*Access Denied . Kindly Contact the admin Support Desk.");
                                }
                                if(action.equals("A"))
                                {
                                    //out.println("*Unauthorized User.");
                                    out.println("*Invalid Credentials.");
                                }
                            %>

                        </td></tr></table>
                <br>
                <%
                    }
                %>
                <div class="form-group">
                    <div class="col-md-12">
                        <%--<i class="fa fa-user overlay"></i>--%>
                        <input type="text" name="username" class="form-control" placeholder="UserName"/>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-md-12">
                        <%--<i class="fa fa-user overlay"></i>--%>
                        <input id="password" name="password" type="Password" maxlength="125" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');"
                               class="form-control" placeholder="Password" oninput="showicon('password','showHidepass')"><span toggle="#password-field" class="hide far fa-eye-slash field-icon toggle-password" onmousedown="mouseoverPass('showHidepass','password')"  id="showHidepass" ></span>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-md-12">
                        <%--<form action="/merchant/forgotpwd.jsp">
                            <button class="btn btn-info btn-block btn-facebook" style="padding:6px 7px "><span class="fa fa-facebook"></span> Forgot your password?</button>
                        </form>--%>
                        <a href="/partner/partnerfpassword.jsp" class="btn btn-link btn-block" style="font-family: 'Open Sans', sans-serif;font-size: 12px">Forgot your password?</a>
                    </div>
                    <%--<div class="col-md-6">
                        <button id="submit" type=submit class="btn btn-info btn-block" style="font-family: 'Open Sans', sans-serif;font-size: 12px;background-color: #29AAA1;border-color:#29AAA1; ">Log In</button>
                    </div>--%>
                </div>
                <div class="login-subtitle" style="line-height: 35px;">
                    <button id="submit" type=submit class="btn btn-info btn-block" style="font-family: 'Open Sans', sans-serif;/*font-size: 12px;*/background-color: #29AAA1;border-color:#29AAA1; ">Log In</button>
                </div>
               <%-- <hr>--%>
            </form>
            <br>
            <p class="indexInstruction" style="font-size: 13px;text-align: justify;">
                <input type="hidden" name="cookiesurl" class="form-control" id="cookiesurl" value="<%=getCookiesURL%>"/>
                By logging into this site you agree to the following terms:
                This site may only be accessed by authorised personnel.
                This site may set cookie(s) for the purpose of tracking your login and to provide access credentials.
                For more information please see our <a href=<%=PrivacyUrl%> class="link" target="_">Privacy Policy</a>.
            </p>
        </div>
        <div class="login-footer">
            <%--<div class="pull-left">
                &copy; 2016 admin
            </div>--%>
            <%-- <div class="pull-right">
                 <img src="/partner/images/browsericon-firefox.png" alt="Firefox" title="Firefox">&nbsp;|
                 <img src="/partner/images/browsericon-chrome.png" alt="Chrome" title="Chrome">&nbsp;|
                 <img src="/partner/images/browsericon-safari.png" alt="Safari" title="Safari">&nbsp;|
                 <img src="/partner/images/browsericon-opera.png" alt="Opera" title="Opera">&nbsp;|
                 <img src="/partner/images/browsericon-explorer.png" alt="Internet Explorer 8+" title="Internet Explorer 8+">
             </div>--%>

        </div>
        <div class="login-footer" >

            <a target="_blank" href="<%=pciurl%>">
                <IMG  src="/partner/images/Certificatelogo1.png" border=0 style="width: 100px;height:65px;margin-left: 40%">
                <%--<div class="login-logo" style="width:244px;height: 41px;
                        background: url(/merchant/images/Certificatelogo1.png) top center no-repeat;
                        width: 100%;
                        height: 50px;
                        float: left;
                        margin-bottom: 10px;">
                    </div>--%>
            </a>

            <%--<div class="login-logo" style="width:244px;height: 41px;
                    background: url(/merchant/images/Certificatelogo1.png) top center no-repeat;
                    width: 100%;
                    height: 50px;
                    float: left;
                    margin-bottom: 10px;">


                <%--<img src="/merchant/images/<%=session.getAttribute("logo")%>" border="0">--%>
                <%--<img src="/merchant/images/admin1.png" >--%>
            <%--</div>--%>

        </div>
    </div>
</div>

<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">

</body>
</html>