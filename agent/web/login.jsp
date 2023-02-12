<%@ page language="java" %>
<%@ page import="com.directi.pg.DefaultUser,
                 com.directi.pg.Functions,
                 com.directi.pg.LoadProperties,
                 com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ include file="ietest.jsp" %>
<%

    session.setAttribute("valid", "true");
    String ctoken= request.getParameter("ctoken");
    Logger logger = new Logger("login.jsp");
    User user =  (User)session.getAttribute("Anonymous");
    if(ctoken !=null)
    {
        if(!Functions.validateCSRFAnonymos(ctoken,user))
        {
            response.sendRedirect("/agent/sessionout.jsp");
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
    String company=(String)session.getAttribute("company");
    logger.error("company name :::"+company);
    final ResourceBundle RBundel1 = LoadProperties.getProperty("com.directi.pg.CertificateLink");
    String pciurl= RBundel1.getString("PCILINK");
    String partnerIcon = (String) session.getAttribute("icon");
    String logo= (String) session.getAttribute("logo");

    Functions functions = new Functions();
%>
<!DOCTYPE html>
<html lang="en" class="body-full-height">
<head>
    <title>Agent Administration Login</title>
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
    </style>
    <!-- META SECTION -->
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />

    <%--<link rel="icon" href="/agent/NewCss/favicon.ico" type="image/x-icon" />--%>
    <!-- END META SECTION -->

    <!-- CSS INCLUDE -->
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
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.2.0/css/all.css" integrity="sha384-hWVjflwFxL6sNzntih27bfxkr27PmbbK/iSvJ+a4+0owXq79v+lsFkW54bOGbiDQ" crossorigin="anonymous">
    <!-- EOF CSS INCLUDE -->

    <script src="/agent/javascript/jquery.min.js"></script>
    <script type="text/javascript" src="/agent/javascript/jquery.jcryption.js?ver=1"></script>
    <script type="text/javascript">
        $(document).ready(function() {

            document.getElementById('submit').disabled =  false;
            $("#submit").click(function() {
                var encryptedString =  $.jCryption.encrypt($("#password").val(), $("#ctoken").val());
                document.getElementById('password').value =  encryptedString;
                document.getElementById('isEncrypted').value =  true;
            });

        });
    </script>

</head>
<body>

<div class="login-container lightmode" onLoad='document.forms[0].username.focus();'>
    <div class="login-box animated fadeInDown" style="padding-top: 80px;">


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

        <%--<img src="/agent/images/<%=session.getAttribute("logo")%>">--%>


        <div class="login-body">
            <div class="login-title"><strong>Log In</strong> to Agent</div>
            <form autocomplete="off" action="/agent/net/Login?ctoken=<%=ctoken%>" class="form-horizontal" method="post">
                <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >

                <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >


                <%
                    if(request.getAttribute("action")!=null)
                    {
                        String action = ESAPI.encoder().encodeForHTML((String) request.getAttribute("action"));
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
                                    out.println("*Your Agent Account has been Locked. Kindly Contact the "+company+" Support Desk.");
                                }
                                if (action.equals("D"))
                                {
                                    out.println("*Your Agent Account has been Disabled. Kindly Contact the "+company+" Support Desk.");
                                }
                                if (action.equals("E"))
                                {
                                    out.println("*Invalid Username and Password.");
                                }
                                if (action.equals("IP"))
                                {
                                    out.println("*Your IP is not white listed with us. Kindly Contact the "+company+" Support Desk.");
                                }
                                if (action.equals("AD"))
                                {
                                    out.println("*ACCESS DENIED.");
                                }
                                if (action.equals("A"))
                                {
                                    out.println("*Invalid Credentials.");
                                }

                            %>
                        </td></tr></table>
                <br>
                <%
                        }
                    }
                %>
                <div class="form-group">
                    <div class="col-md-12">
                        <%--<i class="fa fa-user overlay"></i>--%>
                        <input name="username" type="Text" maxlength="100"  value="" class="form-control" placeholder="Username">
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-md-12">
                        <%--<i class="fa fa-user overlay"></i>--%>
                        <input id="password" name="password" type="Password" maxlength="125" value="" class="form-control" placeholder="password" oninput="showicon('password','showHidepass')"><span toggle="#password-field" class="hide far fa-eye-slash field-icon toggle-password" id="showHidepass" onmousedown="mouseoverPass('showHidepass','password')" onmouseup="mouseoutPass('showHidepass','password')"></span>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-md-6">
                        <%--<form action="/agent/forgotpwd.jsp">
                            <button class="btn btn-info btn-block btn-facebook" style="padding:6px 7px "><span class="fa fa-facebook"></span> Forgot your password?</button>
                        </form>--%>
                        <a href="/agent/agentfpassword.jsp" class="btn btn-link btn-block" style="font-family: 'Open Sans', sans-serif;font-size: 12px">Forgot your password?</a>
                    </div>
                    <div class="col-md-6">
                        <button id="submit" type=submit class="btn btn-info btn-block" style="font-family: 'Open Sans', sans-serif;font-size: 12px;background-color: #29AAA1;border-color:#29AAA1; ">Log In</button>
                        <%--<INPUT id="submit" type=submit  value=Login  class="btn btn-lg btn-green btn-block" disabled="disabled" style="background-color:#2c3e50;color: #ffffff;border-color:#2c3e50; ">--%>
                    </div>
                </div>
                <hr>
            </form>
            <%--<div class="login-subtitle" style="line-height: 35px;">
                Don't have an account yet?
                <form action="/agent/signup.jsp?partnerid=<%=session.getAttribute("partnerid")%>&fromtype=<%=session.getAttribute("company")%>" method="post">
                    <button class="btn btn-info btn-block btn-twitter" style="font-family: 'Open Sans', sans-serif;font-size: 12px;background-color: #29AAA1;border-color:#29AAA1; " type="submit">Create an account</button>
                </form>
            </div>--%>
        </div>
        <%--<div class="login-footer">
            <div class="pull-left">

            </div>
            <div class="pull-right">
                <img src="/agent/images/browsericon-firefox.png" alt="Firefox" title="Firefox">&nbsp;|
                <img src="/agent/images/browsericon-chrome.png" alt="Chrome" title="Chrome">&nbsp;|
                <img src="/agent/images/browsericon-safari.png" alt="Safari" title="Safari">&nbsp;|
                <img src="/agent/images/browsericon-opera.png" alt="Opera" title="Opera">&nbsp;|
                <img src="/agent/images/browsericon-explorer.png" alt="Internet Explorer 8+" title="Internet Explorer 8+">
            </div>

        </div>--%>
        <div class="login-footer" >

            <a target="_blank" href="<%=pciurl%>">
                <IMG src="/agent/images/pci_dss_logo.png" border=0
                     style="/*width:100px;*/height:75px;margin-left: 35%"></a>
                <%--<div class="login-logo" style="width:244px;height: 41px;
                        background: url(/agent/images/Certificatelogo1.png) top center no-repeat;
                        width: 100%;
                        height: 50px;
                        float: left;
                        margin-bottom: 10px;">
                    </div>--%>
            <%--</a>--%>

            <%--<div class="login-logo" style="width:244px;height: 41px;
                    background: url(/agent/images/Certificatelogo1.png) top center no-repeat;
                    width: 100%;
                    height: 50px;
                    float: left;
                    margin-bottom: 10px;">


                &lt;%&ndash;<img src="/agent/images/<%=session.getAttribute("logo")%>" border="0">&ndash;%&gt;
                &lt;%&ndash;<img src="/agent/images/pz.png" >&ndash;%&gt;
            </div>--%>

        </div>
    </div>
</div>

</body>
</html>
