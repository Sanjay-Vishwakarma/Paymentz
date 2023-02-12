<%@ page import="com.logicboxes.util.ApplicationProperties,
                 org.owasp.esapi.User,
                 com.directi.pg.Functions,
                 com.directi.pg.DefaultUser" %>
<%String company = (String)session.getAttribute("company");%>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%@ page import="com.manager.dao.PartnerDAO" %>
<%
    String currentTheme=(String) session.getAttribute("currenttheme");
    String defaultTheme=(String) session.getAttribute("defaulttheme");
%>


<%


    String ctoken= request.getParameter("ctoken");
    User user =  (User)session.getAttribute("Anonymous");
    if(ctoken !=null)
    {
        if(!Functions.validateCSRFAnonymos(ctoken,user))
        {
            response.sendRedirect("/merchant/sessionout.jsp");
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

    session.setAttribute("Anonymous", user);

    String partnerIcon = (String) session.getAttribute("icon");
    String logo=(String)session.getAttribute("logo");
    Functions functions = new Functions();

    String logoHeight="38";
    String logoWidth="140";

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
    //  System.out.println("-----Logo-----"+logo+"-----Icon------"+partnerIcon);
%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title> Merchant Forgot Password</title>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
    </script>

    <%--<link href="/merchant/css/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet">--%>

    <!-- META SECTION -->
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />

    <%--<link rel="icon" href="/merchant/NewCss/favicon.ico" type="image/x-icon" />--%>
    <!-- END META SECTION -->

    <!-- CSS INCLUDE -->
    <link href="/merchant/NewCss/css/style.css" rel="stylesheet" type="text/css" />
    <link rel="stylesheet" type="text/css" id="theme" href="/merchant/NewCss/theme-default.css"/>
    <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/bootstrap.min.css"/>
    <%--<link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fontawesome/font-awesome.min.css"/>
    <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/fontawesome-webfont.eot"/>
    <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/fontawesome-webfont.svg"/>
    <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/fontawesome-webfont.woff"/>
    <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/glyphicons-halflings-regular.eot"/>
    <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/glyphicons-halflings-regular.svg"/>
    <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/glyphicons-halflings-regular"/>
    <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/glyphicons-halflings-regular.woff"/>--%>


    <%
        if(functions.isEmptyOrNull(defaultTheme) && functions.isEmptyOrNull(currentTheme))
        {
    %>
    <link href="/merchant/NewCss/css/pz.jsp.css" rel="stylesheet" type="text/css" />
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

    <!-- EOF CSS INCLUDE -->

    <style type="text/css">
        @-moz-document url-prefix() {
            .login-container.lightmode {
                position: fixed;
            }
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

            margin-top: 55px;
        }
    </style>

</head>
<div class="login-container lightmode">
<body>
    <div class="widget-header transparent">
        <div class="main-topheader colorize">
            <div class="container">
                <div class="row">
                    <div class="col-md-12 clearfix">
                        <%
                            if((functions.isEmptyOrNull(currentTheme) && functions.isValueNull(defaultTheme)) && (currentTheme.equalsIgnoreCase("indiaProcessing") || defaultTheme.equalsIgnoreCase("indiaProcessing")))
                            {
                        %>
                        <a href="#" title="<%=session.getAttribute("company")%>" class="main-logo" ><img src="/images/merchant/pay1.png" style="width: <%=logoWidth%>px;height: <%=logoHeight%>px;object-fit: contain;"></a>
                       <%
                           }else{
                       %>
                        <a href="#" title="<%=session.getAttribute("company")%>" class="main-logo" ><img src="/images/merchant/<%=logo%>" style="width: <%=logoWidth%>px;height: <%=logoHeight%>px;object-fit: contain;"></a>

                        <%
                            }
                        %>
                        <ul class="top-nav pull-right clearfix list-inline">

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
    <div class="login-box animated fadeInDown">
        <%--<%--%>
        <%--if(functions.isValueNull(partnerIcon))--%>
        <%--{--%>

        <%--%>--%>
        <%--<div class="login-logo" style="--%>
        <%--background: url(/images/merchant/<%=partnerIcon%>) top center no-repeat;--%>
        <%--width: 100%;--%>
        <%--height: 70px;--%>
        <%--float: left;--%>
        <%--background-size: contain;--%>
        <%--margin-bottom: 10px">--%>
        <%--</div>--%>

        <%--<div class="login-logo" style="--%>
        <%--background: url(/images/merchant/<%=logo%>) top center no-repeat;--%>
        <%--width: 100%;--%>
        <%--height: 50px;--%>
        <%--float: left;--%>
        <%--background-size: contain;--%>
        <%--margin-bottom: 20px;">--%>
        <%--</div>--%>
        <%--<%--%>
        <%--}--%>
        <%--else--%>
        <%--{--%>
        <%--%>--%>

        <%--<div class="login-logo" style="--%>
        <%--background: url(/images/merchant/<%=logo%>) top center no-repeat;--%>
        <%--width: 100%;--%>
        <%--height: 50px;--%>
        <%--float: left;--%>
        <%--background-size: contain;--%>
        <%--margin-bottom: 20px;">--%>
        <%--</div>--%>
        <%--<%--%>
        <%--}--%>

        <%--%>--%>

        <%--<%
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
--%>


        <div class="login-body" id="log_align">
            <div class="login-title"><p align="center" class="subtitle1" style="font-weight: bold;/* color: #FFFFFF;*/font-family: 'Open Sans', sans-serif;font-size: 18px;">Forgot Password</p></div>


            <p>
            <table border="0" width="100%" align="center">
                <%-- <br><br>--%>
                <tr>
                    <td class="textb" align="center" style="/*color: #FFFFFF;*/font-family: 'Open Sans', sans-serif;font-size: 14px;">Please fill in your username. An email will be sent to you containing a new password.</td>
                    </td>
                </tr>
                <tr>
                    <td class="textb" align="center" style="/*color: #FFFFFF;*/font-family: 'Open Sans', sans-serif;font-size: 14px;">Note:Please login using the new password within one hour of receiving the email.
                    </td>
                </tr>

            </table>
            <hr class="hrnew">
            <%
                String error=(String)request.getAttribute("error");
                if(error!=null)
                {
                    out.println("<p><center><b>"+error+"</b></center></p>");
                }
            %>
            <form action="/merchant/servlet/ForgotPwd?ctoken=<%=ctoken%>" method="post">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <%--<div class="form-group col-md-4 has-feedback">
                <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Forgot Password > Email<br><br></label>
                </div>--%>
                <div class="form-group col-md-12 has-feedback">
                    <label  style="font-family:Open Sans;font-size: 15px;font-weight: 600; /*color: #FFFFFF;*/">Username or Email:<br><br></label>
                    <input class="form-control" type="text"   value="" name="username">
                </div>
                <div class="form-group col-md-12 has-feedback">
                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"></label>
                    <button type="submit" name="submit" class="btn btn-info btn-block">
                        <span><i class="fa fa-mail-forward"></i></span>
                        Send Mail
                    </button>
                </div>

            </form>
            <br>

            </p>
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


<script type="text/javascript" src="/merchant/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/merchant/cookies/cookies_popup.js"></script>
<link href="/merchant/cookies/quicksand_font.css" rel="stylesheet">
<link href="/merchant/cookies/cookies_popup.css" rel="stylesheet">

<script>

    var custom_nav = document.querySelector(".main-topheader");


    $('body').scroll(function (event) {
        var scroll = $('body').scrollTop();
        // Do something
        if(scroll > 50 ) {
            custom_nav.classList.add("header-scrolled");
            $('.main-topheader img').attr('src','/merchant/images/pay2.png');

        }else {
            custom_nav.classList.remove("header-scrolled");
            $('.main-topheader img').attr('src','/merchant/images/pay1.png');
        }
    });
</script>

</body>


</html>