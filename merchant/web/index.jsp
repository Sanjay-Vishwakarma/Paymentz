<%--
<!DOCTYPE html>
--%>
<%
    Merchants.refresh();
%>
<%@ page language="java" %>
<%@ page import="com.directi.pg.DefaultUser,com.directi.pg.Functions,com.directi.pg.LoadProperties,com.directi.pg.Merchants" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="com.manager.dao.PartnerDAO" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%@ page import="com.payment.exceptionHandler.PZDBViolationException" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="java.io.File" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.Logger" %>
<%--<%@ include file="ietest.jsp" %>--%>
<%String loginType = ApplicationProperties.getProperty("LOGIN_TYPE");%>
<%!
    private Functions functions = new Functions();
    private final static String CSS_PATH = ApplicationProperties.getProperty("CSS_PATH");
    Logger  logger  = new Logger("index.jsp");

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
        ctoken = (user).resetCSRFToken();

    }
    session.setAttribute("Anonymous", user);
    String partnerid="";
    if(session.getAttribute("partnerid")!=null && !session.getAttribute("partnerid").equals(""))
    {
        partnerid = (String)session.getAttribute("partnerid");
    }
    String partnerIcon = (String) session.getAttribute("icon");
    String logo = (String) session.getAttribute("logo");
    String currentTheme=(String) session.getAttribute("currenttheme");
    String defaultTheme=(String) session.getAttribute("defaulttheme");
    String pciLogo = (String)session.getAttribute("ispcilogo");
    String liveUrl = (String) session.getAttribute("hosturl");
    String PartnerFavicon=(String)session.getAttribute("favicon");

    String privacyurl="";
    String logoHeight="38";
    String logoWidth="140";
    String target="";
    PartnerDAO partnerDAO = new PartnerDAO();
    PartnerDetailsVO partnerDetailsVO = null;
    try
    {
        partnerDetailsVO = partnerDAO.geturl(partnerid);
    }
    catch (PZDBViolationException e)
    {
        logger.error("PZDBViolationException in index.jsp merhcant",e);
    }

    privacyurl=partnerDetailsVO.getPrivacyurl();
    if (functions.isValueNull(privacyurl))
    {
        privacyurl=partnerDetailsVO.getPrivacyurl();
        target="_blank";

    }
    else
    {
        privacyurl="#";
        target="";
    }
    if(functions.isValueNull(partnerDetailsVO.getLogoHeight()))
    {
        logoHeight = partnerDetailsVO.getLogoHeight();
    }
    if(functions.isValueNull(partnerDetailsVO.getLogoWidth()))
    {
        logoWidth = partnerDetailsVO.getLogoWidth();
    }
    String getCookiesURL ="";
    String cookies = partnerDAO.getCookiesURL(partnerid);
    if (functions.isValueNull(cookies))
    {
        getCookiesURL = cookies;

    }

%>

<%
    ResourceBundle rb = null;
    String themevalue="";
    try
    {
        if (functions.isEmptyOrNull(defaultTheme) && functions.isEmptyOrNull(currentTheme))
        {
            rb = LoadProperties.getProperty("com.directi.pg.ColorTheme", "pz");
            themevalue = rb.getString("pz");
        }
        else if (functions.isEmptyOrNull(currentTheme) && functions.isValueNull(defaultTheme))
        {
            rb = LoadProperties.getProperty("com.directi.pg.ColorTheme", defaultTheme);
            themevalue = rb.getString(defaultTheme);
        }
        else if (functions.isValueNull(currentTheme) && functions.isValueNull(defaultTheme))
        {
            rb = LoadProperties.getProperty("com.directi.pg.ColorTheme", currentTheme);
            themevalue = rb.getString(currentTheme);
        }
        else if (functions.isValueNull(currentTheme) && functions.isEmptyOrNull(defaultTheme))
        {
            rb = LoadProperties.getProperty("com.directi.pg.ColorTheme", currentTheme);
            themevalue = rb.getString(currentTheme);
        }
    }catch(Exception e){
        e.getMessage();
    }

    final ResourceBundle RBundel1 = LoadProperties.getProperty("com.directi.pg.CertificateLink");
    String pciurl= RBundel1.getString("PCILINK");
    ResourceBundle rb1 = null;
    String language_property = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property);

    String index_UserName = !functions.isEmptyOrNull(rb1.getString("index_UserName"))?rb1.getString("index_UserName"): "UserName";
    String index_Password = !functions.isEmptyOrNull(rb1.getString("index_Password"))?rb1.getString("index_Password"): "Password";
    String index_Forgot_password = !functions.isEmptyOrNull(rb1.getString("index_Forgot_password"))?rb1.getString("index_Forgot_password"): "Forgot your password?";
    String index_Log_In = !functions.isEmptyOrNull(rb1.getString("index_Log_In"))?rb1.getString("index_Log_In"): "Log In";
    String index_Dont_have_an_account_yet = !functions.isEmptyOrNull(rb1.getString("index_Dont_have_an_account_yet"))?rb1.getString("index_Dont_have_an_account_yet"): "Don't have an account yet?";
    String index_Create_an_account = !functions.isEmptyOrNull(rb1.getString("index_Create_an_account"))?rb1.getString("index_Create_an_account"): "Create an account";
    String index_instructions = !functions.isEmptyOrNull(rb1.getString("index_instructions"))?rb1.getString("index_instructions"): "By logging into this site you agree to the following terms: This site may only be accessed by authorised personnel. This site may set cookie(s) for the purpose of tracking your login and to provide access credentials.For more information please see our";
    String index_Privacy_Policy = !functions.isEmptyOrNull(rb1.getString("index_Privacy_Policy"))?rb1.getString("index_Privacy_Policy"): "Privacy Policy";
    String index_login_to = !functions.isEmptyOrNull(rb1.getString("index_login_to"))?rb1.getString("index_login_to"): "Login to";
    String index_Merchant_Account = !functions.isEmptyOrNull(rb1.getString("index_Merchant_Account"))?rb1.getString("index_Merchant_Account"): "Merchant Account";
    String index_support_desk = !functions.isEmptyOrNull(rb1.getString("index_support_desk"))?rb1.getString("index_support_desk"): "Support Desk";
    String index_error_msg1 = !functions.isEmptyOrNull(rb1.getString("index_error_msg1"))?rb1.getString("index_error_msg1"): "*Invalid Credentias";
    String index_error_msg2 = !functions.isEmptyOrNull(rb1.getString("index_error_msg2"))?rb1.getString("index_error_msg2"): "*Your Merchant Account has been Locked. Kindly Contact the";
    String index_error_msg3 = !functions.isEmptyOrNull(rb1.getString("index_error_msg3"))?rb1.getString("index_error_msg3"): "*Your Merchant Account has been Disabled. Kindly Contact the";
    String index_error_msg4 = !functions.isEmptyOrNull(rb1.getString("index_error_msg4"))?rb1.getString("index_error_msg4"): "*Your IP is not white listed with us. Kindly Contact the";
    String index_error_msg5 = !functions.isEmptyOrNull(rb1.getString("index_error_msg5"))?rb1.getString("index_error_msg5"): "*Access Denied . Kindly contact the";
%>
<!DOCTYPE html>
<html lang="en" class="body-full-height">
<head>
    <title><%=session.getAttribute("company")%> Merchant Login</title>

    <!-- META SECTION -->
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />

    <link rel="icon" href="/images/merchant/<%=PartnerFavicon%>" type="image/x-icon" />
    <!-- END META SECTION -->
    <!-- CSS INCLUDE -->
    <link rel="stylesheet" type="text/css" id="theme" href="/merchant/NewCss/theme-default.css"/>
    <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.2.0/css/all.css" integrity="sha384-hWVjflwFxL6sNzntih27bfxkr27PmbbK/iSvJ+a4+0owXq79v+lsFkW54bOGbiDQ" crossorigin="anonymous">
    <%-- <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fontawesome/font-awesome.min.css"/>
     <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/fontawesome-webfont.eot"/>
     <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/fontawesome-webfont.svg"/>
     <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/fontawesome-webfont.woff"<%--<link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/glyphicons-halflings-regular.eot"/>
     <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/glyphicons-halflings-regular.svg"/>
     <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/glyphicons-halflings-regular"/>
     <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/glyphicons-halflings-regular.woff"/>--%>
    <!-- EOF CSS INCLUDE -->

    <%--<script type="text/javascript" src="/merchant/javascript/jquery-1.7.1.js?ver=1"></script>--%>
    <script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
    <script type="text/javascript" src="/merchant/javascript/jquery.jcryption.js?ver=1"></script>
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

            margin-top: 55px;
        }


        .header-scrolled {
            background-color: #fff;
        }




    </style>
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
    <%
        File file=null;

        if(functions.isEmptyOrNull(defaultTheme) && functions.isEmptyOrNull(currentTheme))
        {
    %>
    <%--<link href="/merchant/NewCss/css/pz.css" rel="stylesheet" type="text/css" />--%>
    <%
    }
    else if(functions.isEmptyOrNull(currentTheme) && functions.isValueNull(defaultTheme))
    {
        file=new File(CSS_PATH+defaultTheme+".css");
        if (!file.exists()){
            defaultTheme="pz";
        }
    %>
    <link href="/merchant/NewCss/css/<%=defaultTheme%>.css" rel="stylesheet" type="text/css" />
    <%
    }
    else if(functions.isValueNull(currentTheme) && functions.isValueNull(defaultTheme))
    {
        file=new File(CSS_PATH+currentTheme+".css");
        if (!file.exists()){
            currentTheme="pz";
        }
    %>
    <link href="/merchant/NewCss/css/<%=currentTheme%>.css" rel="stylesheet" type="text/css" />
    <%
    }
    else if(functions.isValueNull(currentTheme) && functions.isEmptyOrNull(defaultTheme))
    {
        file=new File(CSS_PATH+currentTheme+".css");
        if (!file.exists()){
            currentTheme="pz";
        }
    %>
    <link href="/merchant/NewCss/css/<%=currentTheme%>.css" rel="stylesheet" type="text/css" />
    <%
        }
    %>


</head>

<body>
<div class="login-container lightmode" id="login_new">
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
                        <a href="#" title="<%=session.getAttribute("company")%>" class="main-logo" ><img class="custom" src="/merchant/images/pay1.png" style="width: 140px;height: 38px;object-fit: contain;"></a>
                            <%}else{
                            %>
                            <a href="#" title="<%=session.getAttribute("company")%>" class="main-logo" ><img src="/images/merchant/<%=logo%>" style="width: <%=logoWidth%>px;height: <%=logoHeight%>px;object-fit: contain;"></a>
                            <%}%>

                        <ul class="top-nav pull-right clearfix list-inline">
                            <p class="NewPlatformLabel" style="font-weight:bold;/*display: inline;*/color:#ffffff;">New to Platform? </p>

                            <li>
                                <a href="/merchant/signup.jsp?partnerid=<%=session.getAttribute("partnerid")%>&fromtype=<%=session.getAttribute("company")%>" class="btn btn-green-3" style="font-family: 'Open Sans', sans-serif;background-color: #29AAA1;border-color:#29AAA1; width: 85px;font-weight: 600;">Sign Up</a>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="login-box animated fadeInDown">

        <%--
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
        </div>--%>


        <div class="login-body" id="log_align">
            <div class="login-title" style="margin-bottom: 12px;text-align: center;"><%=index_login_to%> <%=loginType%> <%=index_Merchant_Account%></div>
            <form autocomplete="off" action="/merchant/servlet/Login?ctoken=<%=ctoken%>" class="form-horizontal" method="post">
                <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
                <input name="languageproperty" id="languageproperty"  type="hidden" value="<%=language_property%>" >
                <input name="defaultTheme" id="widgetcolor_default"  type="hidden" value="<%=defaultTheme%>" >
                <input name="currentTheme" id="widgetcolor_current" type="hidden" value="<%=currentTheme%>" >
                <input name="favicon" id="favicon" type="hidden" value="<%=PartnerFavicon%>" >
                <input type="hidden" value="" id="colorcode" name="colorcode">


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
                                    out.println(index_error_msg1);
                                }
                                if (action.equals("L"))
                                {
                                    out.println(index_error_msg2 +" " +session.getAttribute("company") +" " + index_support_desk);
                                }
                                if (action.equals("D"))
                                {
                                    out.println(index_error_msg3 +" " + session.getAttribute("company") +" " + index_support_desk);
                                }
                                if (action.equals("E"))
                                {
                                    //out.println("*Invalid Username and Password.");
                                    out.println(index_error_msg1);
                                }
                                if (action.equals("IP"))
                                {
                                    out.println(index_error_msg4 +" " + session.getAttribute("company")+" " + index_support_desk);
                                }
                                if (action.equals("AD"))
                                {
                                    out.println(index_error_msg5 +" " + session.getAttribute("company")+ " " + index_support_desk);
                                }
                                if(action.equals("A"))
                                {
                                    //out.println("*Unauthorized User.");
                                    out.println(index_error_msg1);
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
                        <input type="text" name="username" class="form-control" placeholder= "UserName or Email">
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-md-12">
                        <%--<i class="fa fa-user overlay"></i>--%>
                        <input id="password" name="password" type="Password" maxlength="125" class="form-control" autocomplete="off" readonly onfocus="this.removeAttribute('readonly')" placeholder="<%=index_Password%>" oninput="showicon('password','showHidepass')" />
                            <span toggle="#password-field" class="<%--hide--%> far fa-eye-slash field-icon toggle-password" <%--onmousedown="mouseoverPass('showHidepass','password')" onmouseup="mouseoutPass('showHidepass','password')"--%> id="showHidepass" onclick="hideshowpass('showHidepass','password')" ></span>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-md-12">
                        <%--<form action="/merchant/forgotpwd.jsp">
                            <button class="btn btn-info btn-block btn-facebook" style="padding:6px 7px "><span class="fa fa-facebook"></span> Forgot your password?</button>
                        </form>--%>
                        <a href="/merchant/forgotpwd.jsp" class="btn btn-link btn-block" style="font-family: 'Open Sans', sans-serif;font-size: 13px"><%=index_Forgot_password%></a>
                    </div>
                    <%--<div class="col-md-6">
                      <button id="submit" type=submit class="btn btn-info btn-block" style="font-family: 'Open Sans', sans-serif;font-size: 12px;background-color: #29AAA1;border-color:#29AAA1; "><%=index_Log_In%></button>
                    </div>--%>
                </div>

                <div class="login-subtitle" style="line-height: 35px;">
                    <button id="submit" type=submit class="btn btn-info btn-block" style="font-family: 'Open Sans', sans-serif;/*font-size: 12px;*/background-color: #29AAA1;border-color:#29AAA1; "><%=index_Log_In%></button>
                </div>
            </form>
            <br>
            <p class="indexInstruction" style="font-size: 13px;
            text-align: justify;">
                <input type="hidden" name="cookiesurl" class="form-control" id="cookiesurl" value="<%=getCookiesURL%>"/>
                <%=index_instructions %> <a href="<%=privacyurl%>" target=<%=target%>><%=index_Privacy_Policy %></a>.
            </p>
        </div>

        <div class="login-footer">
        </div>
        <%
            if ("Y".equalsIgnoreCase(pciLogo))
            {
        %>
        <div class="login-footer" >
            <a target="_blank" href="<%=pciurl%>">
                <IMG src="/merchant/images/pci_dss_logo.png" border=0
                     style="width:100px;height:65px;margin-left: 40%">
            </a>
            <%--<div class="login-logo" style="width:244px;height: 41px;
                    background: url(/merchant/images/Certificatelogo1.png) top center no-repeat;
                    width: 100%;
                    height: 50px;
                    float: left;
                    margin-bottom: 10px;">
                <%--<img src="/merchant/images/<%=session.getAttribute("logo")%>" border="0">--%>
        </div>
    </div>
    <%
        }
    %>
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
<%--<script>--%>

    <%--var custom_nav = document.querySelector(".main-topheader");--%>


    <%--$('body').scroll(function (event) {--%>
        <%--var scroll = $('body').scrollTop();--%>
        <%--// Do something--%>
        <%--if(scroll > 50 ) {--%>
            <%--custom_nav.classList.add("header-scrolled");--%>
            <%--$('.main-topheader img').attr('src','/merchant/images/pay2.png');--%>

        <%--}else {--%>
            <%--custom_nav.classList.remove("header-scrolled");--%>
            <%--$('.main-topheader img').attr('src','/merchant/images/pay1.png');--%>
        <%--}--%>
    <%--});--%>
<%--</script>--%>
</body>



</html>
<%--
<script type="text/javascript" src="/merchant/NewCss/css/colorcode.js"></script>--%>
