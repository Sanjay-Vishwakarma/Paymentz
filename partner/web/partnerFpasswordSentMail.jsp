<%@ page import="com.logicboxes.util.ApplicationProperties,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.User,
                 com.directi.pg.Functions,
                 com.directi.pg.DefaultUser" %>
<%@ page import="net.partner.PartnerFunctions" %>
<%@ page import="java.util.Map" %>
<%

    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
%>

<%
    String ctoken= request.getParameter("ctoken");
    User user =  (User)session.getAttribute("Anonymous");
    String partnerid=(String) session.getAttribute("merchantid");
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

    session.setAttribute("Anonymous", user);
    PartnerFunctions partnerFunctions = new PartnerFunctions();
    String icon = "";
    String logo = "";
    Map<String, String> logoMap = partnerFunctions.getPartnerLogoAndIcon(partnerid);
    if (partnerFunctions.isValueNull(logoMap.get("logoName")))
    {
        logo = logoMap.get("logoName");
    }
    else if (partnerFunctions.isValueNull((String) session.getAttribute("logo")))
    {
        logo = (String) session.getAttribute("logo");
    }
    else
    {
        logo = "pay2.png";
    }
    if (partnerFunctions.isValueNull(logoMap.get("iconName")))
    {
        icon = logoMap.get("iconName");
    }
    else if (partnerFunctions.isValueNull((String) session.getAttribute("icon")))
    {
        icon = (String) session.getAttribute("icon");
    }
    else
    {
        icon = "Pay_Icon.png";
    }
%>
<html>
<head>
    <title>Partner Administration</title>
    <%--<link rel="stylesheet" type="text/css" href="/partner/style1/styles123.css">--%>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />

    <!-- CSS INCLUDE -->
    <link rel="stylesheet" type="text/css" id="theme" href="/partner/NewCss/theme-default.css"/>
    <link rel="stylesheet" type="text/css"  href="/partner/NewCss/bootstrap.min.css"/>
<%--    <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fontawesome/font-awesome.min.css"/>
    <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fonts/fontawesome-webfont.eot"/>
    <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fonts/fontawesome-webfont.svg"/>
    <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fonts/fontawesome-webfont.woff"/>
    <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fonts/glyphicons-halflings-regular.eot"/>
    <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fonts/glyphicons-halflings-regular.svg"/>
    <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fonts/glyphicons-halflings-regular"/>
    <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fonts/glyphicons-halflings-regular.woff"/>--%>
    <!-- EOF CSS INCLUDE -->


    <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
    <script type="text/javascript" src="/icici/javascript/jquery.jcryption.js?ver=1"></script>

    <style type="text/css">
        .login-subtitle{
            font-size: 15px!important;
            text-align: center!important;
        }

        hr {
            margin-top: 0!important;
            margin-bottom: 0!important;
            border: 1!important;
            border-top: 1px solid rgba(238, 238, 238, 0)!important;
        }

        .login-container .login-box .login-body .login-title{font-size: 22px!important; text-align: center;}

        .form-group p{text-align: center; color: #fff;}

    </style>

</head>

<body>

<form action="/partner/login.jsp" method=post>

    <div class="login-container lightmode">
        <div class="login-box animated fadeInDown">

            <%--<div class="login-logo" style="--%>
                <%--background: url(/images/partner/<%=icon%>) top center no-repeat;--%>
                <%--width: 100%;--%>
                <%--height: 70px;--%>
                <%--float: left;--%>
                <%--background-size: contain;--%>
                <%--margin-bottom: 10px">--%>
            <%--</div>--%>

            <%--<div class="login-logo" style="--%>
                <%--background: url(/images/partner/<%=logo%>) top center no-repeat;--%>
                <%--width: 100%;--%>
                <%--height: 50px;--%>
                <%--float: left;--%>
                <%--background-size: contain;--%>
                <%--margin-bottom: 20px;">--%>
            <%--</div>--%>


                <%
                    if(partnerFunctions.isValueNull(icon))
                    {
                %>
                <div class="login-logo-icon">
                    <img src="/images/partner/<%=icon%>" alt="<%=icon%>"/>
                </div>
                <%
                    }
                %>


                <div class="login-logo">
                    <img src="/images/partner/<%=logo%>" alt="<%=logo%>"/>
                </div>


        <%--<div class="login-logo" style="background: url(/partner/images/pay2.png) top center no-repeat;width: 100%;height: 50px;float: left;/*background-size: contain;*/margin-bottom: 20px;"></div>--%>
            <%--<%
                if(functions.isValueNull(partnerIcon))
                {

            %>
            <div class="login-logo" style="
                    background: url(/merchant/images/<%=partnerIcon%>) top center no-repeat;
                    width: 100%;
                    height: 70px;
                    float: left;
                    background-size: contain;
                    margin-bottom: 10px">
            </div>

            <div class="login-logo" style="
                    background: url(/merchant/images/<%=logo%>) top center no-repeat;
                    width: 100%;
                    height: 50px;
                    float: left;
                    background-size: contain;
                    margin-bottom: 20px;">
            </div>
            <%
            }
            else
            {
            %>

            <div class="login-logo" style="
                    background: url(/merchant/images/<%=logo%>) top center no-repeat;
                    width: 100%;
                    height: 50px;
                    float: left;
                    /*background-size: contain;*/
                    margin-bottom: 20px;">
            </div>
            <%
                }

            %>--%>

            <div class="login-body">
                <div class="login-title"><strong><%=company%> Partner Forgot Password</strong></div>

                <div class="form-group">
                    <div class="col-md-12">
                        <p>Thank you for visiting <strong><%=company%>'s Partner Administration Module</strong></p>
                    </div>
                    <div class="col-md-12">
                        <p>Your temporary password has been sent to you by email to your mailing address set
                            on our server. Please use the login and password to logon to the <%=company%> System within one
                            hour.</p>
                    </div>
                </div>

                <hr>

                <div class="login-subtitle" style="line-height: 35px;">
                    <p> Click here</p>
                    <form action="/partner/login.jsp" method="post">
                        <input type="submit" value="LOGIN" name="submit" class="btn btn-info btn-block" style="font-family: 'Open Sans', sans-serif;font-size: 12px; background-color: #29AAA1; border-color: #29AAA1;">
                    </form>
                    <p> to go back to the <strong>Partner Login Page</strong></p>
                </div>
            </div>

        </div>
    </div>

</form>


<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">

</body>
</html>


