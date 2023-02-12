<%@ page language="java" import="com.logicboxes.util.ApplicationProperties,
                                 org.owasp.esapi.User,
                                 com.directi.pg.Functions,
                                 com.directi.pg.DefaultUser" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Merchants" %>
<%@ page import="org.owasp.esapi.errors.AuthenticationException" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ include file="ietest.jsp" %>
<%
        Merchants.refresh();

    String partnerIcon = (String) session.getAttribute("icon");
    //System.out.println("partnericon---"+partnerIcon);

    String logo = (String) session.getAttribute("logoName");
    //System.out.println("logo---"+logo);
    Logger logger  = new Logger("verifyMail.jsp");

%>
<%
    String ctoken= request.getParameter("ctoken");
    User user =  (User)session.getAttribute("Anonymous");
    session.setAttribute("valid", "true");
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
        try
        {
            ctoken = (user).resetCSRFToken();
        }
        catch (AuthenticationException e)
        {
            logger.error("AuthenticationException :::::::::::::::",e);
        }

    }
    session.setAttribute("Anonymous", user);
    String currentTheme=(String) session.getAttribute("default_theme");
    String defaultTheme=(String) session.getAttribute("current_theme");
    //String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
    String liveUrl = (String) session.getAttribute("hosturl");


    String partnerid=(String) session.getAttribute("partnerid");
    String com=(String) session.getAttribute("partnerName");
    session.setAttribute("Anonymous", user);
    //System.out.println("live url----"+liveUrl);
    String loginurl="https://"+liveUrl+"/merchant/index.jsp";
    //System.out.println("after url----"+loginurl);
    Functions functions = new Functions();
%>


<html>
<head>
  <title>Merchant Email Verification</title>

  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <meta http-equiv="X-UA-Compatible" content="IE=edge" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />

  <link href="/merchant/css/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet">

  <!-- CSS INCLUDE -->
  <link href="/merchant/NewCss/css/style.css" rel="stylesheet" type="text/css" />
  <link rel="stylesheet" type="text/css" id="theme" href="/merchant/NewCss/theme-default.css"/>
  <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/bootstrap.min.css"/>
  <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fontawesome/font-awesome.min.css"/>
  <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/fontawesome-webfont.eot"/>
  <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/fontawesome-webfont.svg"/>
  <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/fontawesome-webfont.woff"/>
  <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/glyphicons-halflings-regular.eot"/>
  <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/glyphicons-halflings-regular.svg"/>
  <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/glyphicons-halflings-regular"/>
  <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/glyphicons-halflings-regular.woff"/>


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

  </style>
</head>
<div class="login-container lightmode">
  <body>
  <div class="login-box animated fadeInDown" style="padding-top: 55px;">
    <%--<h1><img src="/merchant/images/<%=session.getAttribute("logo")%>" border="0"></h1>--%>

    <%
      if(functions.isValueNull(partnerIcon))
      {

    %>
    <div class="login-logo" style="
            background: url(/images/partner/<%=partnerIcon%>) top center no-repeat;
            width: 100%;
            height: 70px;
            float: left;
            background-size: contain;
            margin-bottom: 10px">
    </div>

    <div class="login-logo" style="
            background: url(/images/partner/<%=logo%>) top center no-repeat;
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
            background: url(/images/partner/<%=logo%>) top center no-repeat;
            width: 100%;
            height: 50px;
            float: left;
            background-size: contain;
            margin-bottom: 20px;">
    </div>
    <%
      }
        %>


        <%
            String action =request.getParameter("action");
            //System.out.println("action---"+request.getParameter("action"));
            if (!action.equals("E")&&(action.equals("S")))
            {

    %>
    <div class="login-body">
      <p class="text-lightblue-2">
      <h2 style="font-size: 28px;color: #FFFFFF"; align="center"><b>Email Verification</b></h2>
      <h4 style="font-size: 17px;color: #FFFFFF"; align="center">Thank you <%=ESAPI.encoder().encodeForHTML((String) session.getAttribute("login"))%>  for registering with <%=session.getAttribute("partnerName")%>.</a>Your Email is Verified Successfully.</h4><br>

      <h4 style="font-size: 17px;color: #FFFFFF; font-family:Open Sans;" align="center">
        Please <a href="<%=loginurl%>" class="link" target="_new">Click Here</a> to Login into your account. <span class="textb" style="font-size: 14px;color: #428bca; font-family:Open Sans";></span>

      </h4>
      </p>
    </div>
        <%
            }
            else
            {
        %>
        <div class="login-body">
            <p class="text-lightblue-2">
            <h2 style="font-size: 28px;color: #FFFFFF"; align="center"><b>Email Verification</b></h2>
            <h4 style="font-size: 17px;color: #FFFFFF"; align="center">Your Email is already Verified.</h4><br>

            <h4 style="font-size: 17px;color: #FFFFFF; font-family:Open Sans;" align="center">
                Please <a href="<%=loginurl%>" class="link" target="_new">Click Here</a> to Login into your account. <span class="textb" style="font-size: 14px;color: #428bca; font-family:Open Sans";></span>

            </h4>
            </p>
        </div>
        <%
            }
        %>
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
