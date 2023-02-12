<%String company = ApplicationProperties.getProperty("COMPANY");%>

<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ include file="ietest.jsp" %>
<%
    String com=(String) session.getAttribute("company");
    String partnerid=(String) session.getAttribute("partnerid");
    String logo= (String) session.getAttribute("logo");
    String partnerIcon = (String) session.getAttribute("icon");
    session.invalidate();
    ESAPI.authenticator().logout();
    Functions functions = new Functions();


%>
<html>
<head>
    <title>Agent Administration Logout</title>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />

    <!-- CSS INCLUDE -->
    <link href="/agent/NewCss/css/style.css" rel="stylesheet" type="text/css" />
    <link rel="stylesheet" type="text/css" id="theme" href="/agent/NewCss/theme-default.css"/>
    <link rel="stylesheet" type="text/css"  href="/agent/NewCss/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css"  href="/agent/NewCss/fontawesome/font-awesome.min.css"/>
    <link rel="stylesheet" type="text/css"  href="/agent/NewCss/fonts/fontawesome-webfont.eot"/>
    <link rel="stylesheet" type="text/css"  href="/agent/NewCss/fonts/fontawesome-webfont.svg"/>
    <link rel="stylesheet" type="text/css"  href="/agent/NewCss/fonts/fontawesome-webfont.woff"/>
    <link rel="stylesheet" type="text/css"  href="/agent/NewCss/fonts/glyphicons-halflings-regular.eot"/>
    <link rel="stylesheet" type="text/css"  href="/agent/NewCss/fonts/glyphicons-halflings-regular.svg"/>
    <link rel="stylesheet" type="text/css"  href="/agent/NewCss/fonts/glyphicons-halflings-regular"/>
    <link rel="stylesheet" type="text/css"  href="/agent/NewCss/fonts/glyphicons-halflings-regular.woff"/>
    <!-- EOF CSS INCLUDE -->
    <script src="/agent/NewCss/js/init.js"></script>
    <script src="/agent/javascript/jquery.min.js"></script>
    <script type="text/javascript" src="/agent/javascript/jquery.jcryption.js?ver=1"></script>
    <%--<link rel="stylesheet" type="text/css" href="/agent/style1/styles123.css">--%>

    <style type="text/css">
        .login-subtitle{
            font-size: 15px!important;
            text-align: center!important;
        }

        @-moz-document url-prefix() {
            .login-container.lightmode {
                position: fixed;
            }
        }

        hr {
            margin-top: 0!important;
            margin-bottom: 0!important;
            border: 1!important;
            border-top: 1px solid rgba(238, 238, 238, 0)!important;
        }

        .login-container .login-box .login-body .login-title{font-size: 22px!important;text-align: center;}
    </style>

</head>

<body>
<!-- Begin page -->
<form action="/agent/login.jsp" method=post>

    <div class="login-container lightmode">
        <div class="login-box animated fadeInDown" style="padding-top: 55px;">

            <%--<div class="login-logo" style="background: url(/agent/images/pay2.png) top center no-repeat;width: 100%;height: 70px;float: left;/*background-size: contain;*/margin-bottom: 10px"></div>--%>

                <%
                    if(functions.isValueNull(partnerIcon))
                    {
                %>
                <div class="login-logo" style="
                        background: url(/images/agent/<%=partnerIcon%>) top center no-repeat;
                        width: 100%;
                        height: 70px;
                        float: left;
                        background-size: contain;
                        margin-bottom: 10px">
                </div>
                <div class="login-logo" style="
                        background: url(/images/agent/<%=logo%>) top center no-repeat;
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
                        background: url(/images/agent/<%=logo%>) top center no-repeat;
                        width: 100%;
                        height: 50px;
                        float: left;
                        background-size: contain;
                        margin-bottom: 20px;">
                </div>
                <%
                    }
                %>


            <div class="login-body">
                <div class="login-title" style="text-align: center;"><strong>Agent Administration Module</strong></div>


                <div class="form-group">
                    <div class="login-subtitle" style="line-height: 35px;">
                        <div class="col-md-12">
                            <p>Thank you for visiting <strong><%=com%>'s Agent Administration Module</strong></p>
                        </div>
                    </div>
                    <%--                    <div class="col-md-6">
                                            <button id="submit" type=submit class="btn btn-info btn-block" style="font-family: 'Open Sans', sans-serif;font-size: 12px;background-color: #29AAA1;border-color:#29AAA1; ">Log In</button>
                                        </div>--%>
                </div>


                <hr>


                <div class="login-subtitle" style="line-height: 35px;">

                    <p> Click here</p>
                    <form action="/agent/login.jsp" method="post">
                        <input type="hidden"  value="<%=partnerid%>" name="partnerid">
                        <input type="hidden" value="<%=com%>" name="fromtype">
                        <input type="hidden" value="<%=logo%>" name="logo">
                        <input type="submit" value="LOGIN" name="submit" class="btn btn-info btn-block" style="font-family: 'Open Sans', sans-serif;font-size: 12px; background-color: #29AAA1; border-color: #29AAA1;"></form> <p> to go back to the <strong>Partner Login Page</strong></p>
                </div>
            </div>
        </div>
    </div>


</form>
<!-- End of page -->

</body>
</html>


