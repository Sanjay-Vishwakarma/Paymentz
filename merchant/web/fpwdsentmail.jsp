<%@ page import="com.logicboxes.util.ApplicationProperties,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.User,
                 com.directi.pg.Functions,
                 com.directi.pg.DefaultUser" %>
<%
    String ctoken= request.getParameter("ctoken");
    User user =  (User)session.getAttribute("Anonymous");
    String partnerid=(String) session.getAttribute("partnerid");
    String com=(String) session.getAttribute("company");

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
%>
<%
    String currentTheme=(String) session.getAttribute("currenttheme");
    String defaultTheme=(String) session.getAttribute("defaulttheme");
%>
<html>
<head>
    <title>Merchant Forgot Password</title>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />

    <!-- CSS INCLUDE -->
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
    <!-- EOF CSS INCLUDE -->

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
    <!-- EOF CSS INCLUDE -->

    <script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
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

        @-moz-document url-prefix() {
            .login-container.lightmode {
                position: fixed;
            }
        }

    </style>

</head>

<body>

<form action="/merchant/index.jsp" method=post>

    <div class="login-container lightmode">
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
                    <%--height: 100px;--%>
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
                    <%--height: 100px;--%>
                    <%--float: left;--%>
                    <%--background-size: contain;--%>
                    <%--margin-bottom: 20px;">--%>
            <%--</div>--%>
            <%--<%--%>
                <%--}--%>

            <%--%>--%>


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



            <div class="login-body">
                <div class="login-title"><strong>Merchant Forgot Password</strong></div>

                <div class="form-group">
                    <div class="col-md-12">
                        <p>Thank you for visiting <strong><%=com%>'s Merchant Administration Module</strong></p>
                    </div>
                    <div class="col-md-12">
                        <p>Your temporary password has been sent to you by email to your mailing address set
                            on our server. Please use the login and password to logon to the <%=com%> System within one
                            hour.</p>
                    </div>
                </div>

                <hr>

                <div class="login-subtitle" style="line-height: 35px;">
                    <p> Click here</p>
                    <form action="/merchant/index.jsp" method="post">
                        <input type="hidden"  value="<%=partnerid%>" name="partnerid">
                        <input type="hidden" value="<%=com%>" name="fromtype">
                        <input type="submit" value="LOGIN" name="submit" class="btn btn-info btn-block" style="font-family: 'Open Sans', sans-serif;font-size: 12px; background-color: #29AAA1; border-color: #29AAA1;">
                    </form>
                    <p> to go back to the <strong>Merchant Login Page</strong></p>
                </div>
            </div>

        </div>
    </div>

</form>

</body>
</html>