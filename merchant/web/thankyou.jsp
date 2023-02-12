<%@ page language="java" import="com.logicboxes.util.ApplicationProperties,
                                 org.owasp.esapi.User,
                                 com.directi.pg.Functions,
                                 com.directi.pg.DefaultUser" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.manager.PartnerManager" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%@ include file="ietest.jsp" %>
<%

    String supportUrl = ApplicationProperties.getProperty("COMPANY_SUPPORT_URL");
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
    String logo=(String)session.getAttribute("logo");
    String com=(String) session.getAttribute("company");

    //System.out.println("company---"+com);
    String partnerid=(String) session.getAttribute("partnerid");
    session.setAttribute("Anonymous", user);
    //String twuk = "http://www."+com+".co.uk";
    String support="";
    String Docs= "";

    PartnerManager partnerManager = new PartnerManager();
    PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();

    partnerDetailsVO = partnerManager.getPartnerDetails(partnerid);


    /*if("TransactWorldUK".equals(com))
    {
        support="http://www.transactworld.co.uk";
        Docs= "http://docs.transactworld.co.uk";

    }
    else if("Paynetics".equals(com))
    {
        support="https://paynetics.digital";
        Docs= "https://docs.e-comm.paynetics.digital";
    }
    else
    {
        support="http://www."+com+".com";
        Docs= "http://docs."+com+".com";
    }*/

    support=partnerDetailsVO.getSupporturl();
    Docs=partnerDetailsVO.getDocumentationurl();

    String hostname = partnerDetailsVO.getHostUrl();

    String partnerIcon = (String) session.getAttribute("icon");
    String currentTheme=(String) session.getAttribute("currenttheme");
    String defaultTheme=(String) session.getAttribute("defaulttheme");
    String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
    String loginurl = "";
    String forwardedhostname =Functions.getForwardedHost(request);

    Functions functions = new Functions();

    if (functions.isValueNull(hostname) && hostname.equalsIgnoreCase(forwardedhostname))
        loginurl="https://"+hostname+"/merchant/index.jsp";
    /*else if(!partnerid.equals("1"))
        loginurl=""+liveUrl+"merchant/index.jsp?"+"partnerid="+partnerid+"&"+"fromtype="+com;*/
    else
        loginurl=""+liveUrl+"merchant/index.jsp?"+"partnerid="+partnerid+"&"+"fromtype="+com;
%>


<html>
<head>
    <title>Merchant Administration Registration</title>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />

    <link href="/merchant/css/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet">

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

                        <a href="#" title="<%=session.getAttribute("company")%>" class="main-logo" ><img src="/images/merchant/<%=logo%>" style="width: 140px;height: 38px;object-fit: contain;"></a>

                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="login-box animated fadeInDown">
        <%--<h1><img src="/merchant/images/<%=session.getAttribute("logo")%>" border="0"></h1>--%>

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
            </div>--%>



        <div class="login-body">
            <p class="text-lightblue-2">
            <h2 style="font-size: 28px;<%--color: #FFFFFF"--%>" align="center"><b>New Merchant Signup</b></h2>
            <h4 style="font-size: 17px;<%--color: #FFFFFF";--%>" align="center">Thank you <%=ESAPI.encoder().encodeForHTML((String) request.getAttribute("username"))%> for registering with <%=session.getAttribute("company")%>.Please Check our <a href="<%=Docs%>" class="link" target="_new">Documentation</a> page to get your integration kits.</h4><br>
            <h4 style="font-size: 17px;/*color: #FFFFFF;*/ font-family:Open Sans;" align="center">
                Please <a href="<%=loginurl%>" class="link" target="_new">Click Here</a> to Login into your account. <span class="textb" style="font-size: 14px;color: #428bca; font-family:Open Sans";></span>

            </h4>
            <%--<h2>Your temporary password has been sent to you by email to your mailing address set
                on our server. Please use the login and password to logon to the <%=com%> System within one
                hour.</h2><br>--%>


            <%--<h3>Click here</h3> <form action="/merchant/index.jsp" method="post">
            <input type="hidden"  value="<%=partnerid%>" name="partnerid">
            <input type="hidden" value="<%=com%>" name="fromtype">
            <input type="submit" value="LOGIN" name="submit" class="btn btn-primary btn-sm" >
        </form>
            <h3> to go back to the
                Merchant login page</h3>--%>

            <h4 style="font-size: 17px;/*color: #FFFFFF;*/ font-family:Open Sans;" align="center">
                Please <a href="<%=support%>" class="link" target="_new">Contact Us</a> to get your account activated. <span class="textb" style="font-size: 14px;color: #428bca; font-family:Open Sans";></span>

            </h4>
            </p>
        </div>
        <br>
    </div>
    <br><br>
    <%--  <div class="login-box animated fadeInDown">

              <div class="login-body" style="margin-top: 15px">
                  <div class="bg-info" style="background: rgba(63,186,228,0.2)"><i class="fa fa-info-circle">&nbsp;&nbsp;</i>
                      <b>Note&nbsp;:&nbsp;</b>The Merchant Integration kit contains pages that need to be integrated with your
                      website to begin testing and integration on our Payment Gateway Service.<br>
                      Integrate your website with the <%=session.getAttribute("company")%> Server using the API by following instructions provided with
                      the integration kit. At this point you will be in test mode. All transactions will be routed
                      through the test database. This is explained in the  <a href="<%=Docs%>" class="link" target="_new">Documentation</a>.

                      Once the integration is complete you will be switched to the live mode and you can do live transactions through your
                      interface.
                  </div>
              </div>
          </div>
  --%>


    </body>
</div>
<script>
    (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
        (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
            m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
    })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

    ga('create', 'UA-40647071-1', 'auto');
    ga('send', 'pageview');

</script>
</html>
