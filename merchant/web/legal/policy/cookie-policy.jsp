<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.dao.PartnerDAO" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%@ page import="com.payment.exceptionHandler.PZDBViolationException" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>




<!DOCTYPE html>
<!--[if lt IE 7]> <html lang="en" class="ie ie6 lte9 lte8 lte7 os-win"> <![endif]-->
<!--[if IE 7]> <html lang="en" class="ie ie7 lte9 lte8 lte7 os-win"> <![endif]-->
<!--[if IE 8]> <html lang="en" class="ie ie8 lte9 lte8 os-win"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie ie9 lte9 os-win"> <![endif]-->
<!--[if gt IE 9]> <html lang="en" class="os-win"> <![endif]-->
<!--[if !IE]><!-->
<html lang="en">
<head>
    <meta http-equiv="content-type" content="text/html;charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <%
        Functions functions = new Functions();
        String partnerid="";
        Logger logger  = new Logger("verifyMail.jsp");
        if(session.getAttribute("partnerid")!=null && !session.getAttribute("partnerid").equals(""))
        {
            partnerid = (String)session.getAttribute("partnerid");
        }

        String logo="";
        String defaulttheme="";
        String currentTheme="";
        String partnername="";
        PartnerDAO partnerDAO = new PartnerDAO();
        PartnerDetailsVO partnerDetailsVO = null;
        try
        {
            partnerDetailsVO = partnerDAO.geturl(partnerid);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException ",e);
        }

        logo=partnerDetailsVO.getLogoName();
        defaulttheme=partnerDetailsVO.getDefaultTheme();

        currentTheme=partnerDetailsVO.getCurrentTheme();
        partnername=partnerDetailsVO.getPartnerName();
       /* System.out.println("default theme---"+defaulttheme);
        System.out.println("current theme---"+currentTheme);*/
        if (functions.isValueNull(logo))
        {
            logo=partnerDetailsVO.getLogoName();

        }
        else
        {
          logo="";
        }
        String loginurl = "";
        String com=(String) session.getAttribute("company");
        String forwardedhostname =Functions.getForwardedHost(request);
        String hostname = partnerDetailsVO.getHostUrl();
        String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
        if(!partnerid.equals("1"))
            loginurl=""+liveUrl+"merchant/index.jsp?"+"partnerid="+partnerid+"&"+"fromtype="+com;
        else if (functions.isValueNull(hostname) && hostname.equalsIgnoreCase(forwardedhostname))
            loginurl="https://"+hostname+"/merchant/index.jsp";
        else
            loginurl=""+liveUrl+"merchant/index.jsp?"+"partnerid="+partnerid+"&"+"fromtype="+com;
    %>
    <%

        if(functions.isEmptyOrNull(defaulttheme) && functions.isEmptyOrNull(currentTheme))
        {
    %>
    <link href="/merchant/NewCss/css/pz.css" rel="stylesheet" type="text/css" />
    <%
    }
    else if(functions.isEmptyOrNull(currentTheme) && functions.isValueNull(defaulttheme))
    {
    %>
    <link href="/merchant/NewCss/css/<%=defaulttheme%>.css" rel="stylesheet" type="text/css" />

    <%
    }
    else if(functions.isValueNull(currentTheme) && functions.isValueNull(defaulttheme))
    {
    %>
    <link href="/merchant/NewCss/css/<%=currentTheme%>.css" rel="stylesheet" type="text/css" />
    <%
    }
    else if(functions.isValueNull(currentTheme) && functions.isEmptyOrNull(defaulttheme))
    {
    %>
    <link href="/merchant/NewCss/css/<%=currentTheme%>.css" rel="stylesheet" type="text/css" />
    <%
        }
    %>
    <title><%=partnername.substring(0,1).toUpperCase() + partnername.substring(1)%>'s Policy</title>

    <link rel="icon" href="/merchant/legal/img/pz_icon.ico">
    <%--<script src="../../js_launch/jquery.js"></script>--%>

    <script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>

    <link href="/merchant/legal/css/style1.css" rel="stylesheet">
    <link href="/merchant/legal/css/style-global.css" rel="stylesheet">
    <link href="/merchant/legal/css/style.css" rel="stylesheet">
    <link href="/merchant/legal/css/main.css" rel="stylesheet">

</head>

<body>


<header id="layout-header" class="minimal-nav wide-nav" role="banner">

    <div id="header-anchor">
        <div id="header-banner">
            <div class="wrapper">

                <div class="header-logo-container">
                    <a class="header-logo guest " href="#" title="Policy">
                        <h2 id="in-logo" class="logo-text"><%=partnername%></h2>
                        <%--<h2 id="in-logo" class="logo-text"><%=partnername.substring(0,1).toUpperCase()%></h2>--%>
                            <span class="li-icon" aria-hidden="true" size="28dp" color="brand">
                                <img src="/images/partner/<%=logo%>"alt="Logo">
                            </span>
                    </a>
                </div>

                <nav role="navigation" id="minimal-util-nav" class="guest-nav" aria-label="Main site navigation">
                    <ul class="nav-bar">
                        <!-- <li class="nav-item nav-signin">
                            <a class="nav-link" href="#" title="Sign in">Sign in</a>
                        </li> -->
                        <li class="nav-item nav-joinnow">
                            <a class="nav-link highlight" rel="nofollow" href=<%=loginurl%> title="Website">
                                Website
                            </a>
                        </li>
                    </ul>
                </nav>

            </div>
        </div>
    </div>
</header>

<div id="body" class="" role="main">

    <div class="wrapper hp-nus-wrapper">

        <div id="main" class="legal">
            <div id="popmain" class="contain plaintext">
<%--                <div class="legal-nav-container">
                    <nav class="legal-nav">
                        <ul>
                            <!-- <li class="legal-nav-link"><a href="user-agreement">User Agreement</a></li> -->
                            <li class="legal-nav-link"><a href="privacy-policy.php">Privacy Policy</a></li>
                            <li class="legal-nav-link"><a href="cookie-policy.php">Cookie Policy</a></li>
                            <li class="legal-nav-link"><a href="terms-of-use.php">Terms Of Use</a></li>
                        </ul>
                    </nav>
                </div>--%>


                <header class="login-container lightmode header-container" style="">
            <h1 class="banner-title">Cookie Policy</h1>
        </header>
        <div class="legal-content">

            <section class="section-content">
                <p>This page describes <%=partnername.substring(0,1).toUpperCase() + partnername.substring(1)%>'s (“<%=partnername.toUpperCase()%>”, “we”, “our” or “us”) cookie policy, which applies when you use our websites, mobile sites, or mobile apps (collectively, Sites). Here you can find out more about cookies and how to control them.</p>
            </section>

            <section class="section-content">
                <h2 class="section-title">Your Consent</h2>
                <p>By using our Sites, you accept the use of cookies in accordance with this Cookie Policy. This policy applies additional to any other Terms of Use or contract provision for the particular <%=partnername.toUpperCase()%> service being used. If you do not accept the use of cookies, please disable them as explained below or please do not use this site.</p>
            </section>

            <section class="section-content">

                <h2 class="section-title">What are cookies?</h2>

                <p>Cookies are small data files that are placed on your computer or mobile device when you visit a website. Cookies are widely used by website and mobile application owners in order to make their websites and apps work, or to work more efficiently, as well as to provide reporting information.</p>

                <p>Cookies do a lot of different jobs which make your experience of the Internet much smoother and more interactive. For instance, they are used to remember your preferences on sites you visit often, to remember your user ID and the contents of your shopping baskets, and to help you navigate between pages more efficiently. They also help ensure that the advertisements that you see online are more relevant to you and your interests. Much, though not all, of the data that they collect is anonymous, though some of it is designed to detect browsing patterns and approximate geographical location to improve user experience.</p>

                <p>If you visit our website, mobile site, or use our mobile apps, we will deploy cookies and similar technologies like JavaScript to provide an online service more suited to the device you connect from, as well as to prevent and detect fraud, to help keep you secure. When you visit our website from any device (e.g. mobile device or PC), we collect information about your use of this site, such as information about the device or browser you use to access the site (including device type, screen resolution, etc.), the way you interact with this site, and the IP address your device connects from. In many instances, these technologies are reliant on cookies to function properly, and so declining cookies will impair their functioning, e.g. you may not be able to initiate or complete some activities within our secure online services unless these cookies or similar technologies are installed.</p>

            </section>

            <section class="section-content">

                <h2 class="section-title">What types of cookies are there?</h2>

                <p>Broadly speaking, there are four types of cookie: strictly necessary cookies, performance cookies, functionality cookies and targeting or advertising cookies.</p>

                <ul>
                    <li><u>Strictly necessary cookies</u> are essential to navigate around a website and use its features. Without them, you wouldn’t be able to use basic services.</li>
                    <li><u>Performance cookies</u> collect anonymous data on how visitors use a website; they can’t track users, and are only used to improve how a website works.</li>
                    <li><u>Functionality cookies</u> allow users to customize how a website looks for them: they can remember usernames, language preferences and regions, and provide more personal features. For instance, a website may be able to provide you with local weather reports or traffic news by storing in a cookie the region in which you are currently located.</li>
                    <li><u>Advertising and targeting cookies</u> are used to deliver advertisements more relevant to you, but can also limit the number of times you see an advertisement, and be used to chart the effectiveness of an ad campaign by tracking users’ clicks. They are usually placed by advertising networks with the website operator’s permission.</li>
                </ul>

                <p>Additionally, these cookies are divided further into two sub-types.</p>

                <ul>
                    <li><u>Persistent cookies</u> remain on a user’s device for a set period of time specified in the cookie. They are activated each time that the user visits the website that created that particular cookie.</li>
                    <li><u>Session cookies</u> are temporary. They allow website operators to link the actions of a user during a browser session. A browser session starts when a user opens the browser window and finishes when they close the browser window. Once you close the browser, all session cookies are deleted.</li>
                </ul>

            </section>

            <section class="section-content">
                <h2 class="section-title">How do I control and delete cookies?</h2>
                <p>The ability to disable or delete cookies can also be carried out by changing your browser’s settings. In order to do this, follow the instructions provided by your browser (usually found in the ‘Help’, ‘Edit’ or ‘Tools’ facility). If you disable all cookies, some pages may not work.  If you switch off cookies at a browser level, your device won't be able to accept cookies from any website.</p>
            </section>

            <section class="section-content">
                <h2 class="section-title">What types of cookies used by <%=partnername.toUpperCase()%>?</h2>
                <p>Below is a list of the types of cookies and similar technologies used by the <%=partnername.toUpperCase()%> across our websites, mobile sites and mobile applications. Not all of these cookies will appear on every site or application, and this list is not exhaustive.</p>

                <div style="overflow-x: auto;">

                    <table class="section-table" id="cookie-table">
                        <tbody>
                            <tr id="heading">
                                <th>Cookie <br>Name</th>
                                <th>Source</th>
                                <th>Cookie Description</th>
                                <th>Cookie <br>Purpose</th>
                                <th>Duration</th>
                            </tr>

                            <tr>
                                <td>JSESSIONID</td>
                                <td>Internal</td>
                                <td>JSESSIONID is a cookie in J2EE web application which is used in session tracking.
                                Since HTTP is a stateless protocol, we need to use any session to remember state. JSESSIONID cookie is created by web container and send along with response to client.
                                </td>
                                <td>Session</td>
                                <td>Session</td>
                            </tr>

                            <tr>
                                <td>path</td>
                                <td>Internal</td>
                                <td>Indicates a URL path that must exist in the requested resource before sending the Cookie header. The %x2F ("/") character is interpreted as a directory separator and sub directories will be matched as well (e.g. path=/docs, "/docs", "/docs/Web/", or "/docs/Web/HTTP" will all be matched).</td>
                                <td>Directory Location</td>
                                <td>--</td>
                            </tr>

                            <tr>
                                <td>Secure</td>
                                <td>Internal</td>
                                <td>A secure cookie will only be sent to the server when a request is made using SSL and the HTTPS protocol. However, confidential or sensitive information should never be stored or transmitted in HTTP Cookies as the entire mechanism is inherently insecure and this doesn't mean that any information is encrypted.</td>
                                <td>Data Security</td>
                                <td>--</td>
                            </tr>

                            <tr>
                                <td>HttpOnly</td>
                                <td>Internal</td>
                                <td>HTTP-only cookies aren't accessible via JavaScript through the Document.cookie property, the XMLHttpRequest and Request APIs to mitigate attacks against cross-site scripting (XSS).</td>
                                <td>Security</td>
                                <td>--</td>
                            </tr>

                            <tr>
                                <td>pzConsent</td>
                                <td>Internal</td>
                                <td>This cookie is used to remember your response to our consent box.</td>
                                <td>Essential Cookies</td>
                                <td>30 Days</td>
                            </tr>

                            <%--<tr>
                                <td>__cfduid</td>
                                <td>Cloudflare</td>
                                <td>This cookie is used to override any security restrictions based on the IP address the visitor is coming from and also this cookie is used to identify individual clients behind a shared I.P address and apply security settings on a per-client basis. This cookie does not store personally identifying information.</td>
                                <td>Analytics</td>
                                <td>1 Year</td>
                            </tr>

                            <tr>
                                <td>driftt_aid</td>
                                <td>Drift</td>
                                <td>These cookies are used to help remember you when you use the Drift messaging system on site.</td>
                                <td>Functional Cookies</td>
                                <td>2 Years</td>
                            </tr>

                            <tr>
                                <td>driftt_sid</td>
                                <td>Drift</td>
                                <td>These cookies are used to help remember you when you use the Drift messaging system on site.</td>
                                <td>Functional Cookies</td>
                                <td>30 Minutes</td>
                            </tr>

                            <tr>
                                <td>driftt_wmd</td>
                                <td>Drift</td>
                                <td>These cookies are used to help remember you when you use the Drift messaging system on site.</td>
                                <td>Functional Cookies</td>
                                <td>Browser Session</td>
                            </tr>

                            <tr>
                                <td>NID</td>
                                <td>google.com</td>
                                <td>Google uses cookies NID to contribute to the personalization of the ads in Google properties, like Google search. For example, we use them to store your most recent searches, your previous interactions with an advertiser's ads or search results and your visits to an advertiser's website. In this way we can show you personalized ads on Google.</td>
                                <td>Targeting Cookies</td>
                                <td>45 Days</td>
                            </tr>--%>

                            <!-- <tr>
                                <td>catAccCookies</td>
                                <td>Internal</td>
                                <td>This cookie is used to remember your response to our consent box.</td>
                                <td>Essential Cookies</td>
                                <td>30 Days</td>
                            </tr> -->

                        </tbody>
                    </table>

                </div>

            </section>


        </div>


            </div>
        </div>

    </div>
</div>


<footer class="launch-footer login-container lightmode" >
    <p>Copyright © <%=partnername.substring(0,1).toUpperCase() + partnername.substring(1)%> 2018. All Rights Reserved</p>
</footer>

</body>

</html>