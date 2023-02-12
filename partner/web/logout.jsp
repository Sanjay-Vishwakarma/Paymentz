<%String company = ApplicationProperties.getProperty("COMPANY");%>

<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="net.partner.PartnerFunctions" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%@ page import="com.manager.dao.PartnerDAO" %>
<%@ include file="ietest.jsp" %>
<%
    String com=(String) session.getAttribute("company");
    String partnerid=(String) session.getAttribute("merchantid");
    User user = (User) session.getAttribute("ESAPIUserSessionKey");
    //System.out.println("merchantid----"+partnerid);

    PartnerFunctions partnerFunctions = new PartnerFunctions();
    String icon = "";
    String logo = "";
    String partner_id="";
    if(partnerFunctions.isValueNull(request.getParameter("partnerid"))){
        partner_id = request.getParameter("partnerid");
    }else{
        partner_id =partnerid;
    }
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
   /* else
    {
        icon = "Pay_Icon.png";
    }*/
    String logoHeight="38";
    String logoWidth="140";

    PartnerDAO partnerDAOMain = new PartnerDAO();
    String pId= String.valueOf(session.getAttribute("partnerid"));
    if(pId !=null && !pId.equals(""))
    {
        pId = String.valueOf(session.getAttribute("partnerid"));
    }
    PartnerDetailsVO partnerDetailsVOMain = null;
    if(partnerFunctions.isValueNull(partner_id))
    {
        partnerDetailsVOMain = partnerDAOMain.geturl(pId);
    }

    if(partnerDetailsVOMain != null)
    {
        if(partnerFunctions.isValueNull(partnerDetailsVOMain.getLogoHeight()))
        {
            logoHeight = partnerDetailsVOMain.getLogoHeight();
        }
        if(partnerFunctions.isValueNull(partnerDetailsVOMain.getLogoWidth()))
        {
            logoWidth = partnerDetailsVOMain.getLogoWidth();
        }
    }

    session.invalidate();
    ESAPI.authenticator().logout();

%>
<html>
<head>
    <title>Partner Administration Logout</title>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />

    <!-- CSS INCLUDE -->
    <link href="/partner/NewCss/css/style.css" rel="stylesheet" type="text/css" />
    <link rel="stylesheet" type="text/css" id="theme" href="/partner/NewCss/theme-default.css"/>
    <link rel="stylesheet" type="text/css"  href="/partner/NewCss/bootstrap.min.css"/>
    <%--<link rel="stylesheet" type="text/css"  href="/partner/NewCss/fontawesome/font-awesome.min.css"/>
    <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fonts/fontawesome-webfont.eot"/>
    <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fonts/fontawesome-webfont.svg"/>
    <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fonts/fontawesome-webfont.woff"/>
    <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fonts/glyphicons-halflings-regular.eot"/>
    <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fonts/glyphicons-halflings-regular.svg"/>
    <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fonts/glyphicons-halflings-regular"/>
    <link rel="stylesheet" type="text/css"  href="/partner/NewCss/fonts/glyphicons-halflings-regular.woff"/>--%>
    <!-- EOF CSS INCLUDE -->
    <script src="/partner/NewCss/js/init.js"></script>
    <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
    <script type="text/javascript" src="/partner/javascript/jquery.jcryption.js?ver=1"></script>
    <%--<link rel="stylesheet" type="text/css" href="/partner/style1/styles123.css">--%>

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

</head>

<body>
<!-- Begin page -->
<form action="/partner/login.jsp" method=post>

    <div class="login-container lightmode">
        <div class="widget-header transparent">
            <div class="main-topheader colorize">
                <div class="container">
                    <div class="row">
                        <div class="col-md-12 clearfix">
                            <a href="#" title="" class="main-logo" ><img src="/images/partner/<%=logo%>" style="width: <%=logoWidth%>px;height: <%=logoHeight%>px;object-fit: contain;"></a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="login-box animated fadeInDown">

            <%--<div class="login-logo" style="background: url(/partner/images/pay2.png) top center no-repeat;width: 100%;height: 70px;float: left;/*background-size: contain;*/margin-bottom: 10px"></div>--%>
            <%--<%
                if(functions.isValueNull(partnerIcon))
                {

            %>

            <div class="login-logo" style="
                    background: url(/partner/images/<%=partnerIcon%>) top center no-repeat;
                    width: 100%;
                    height: 70px;
                    float: left;
                    background-size: contain;
                    margin-bottom: 10px">
            </div>

            <div class="login-logo" style="
                    background: url(/partner/images/<%=logo%>) top center no-repeat;
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
                    background: url(/partner/images/<%=logo%>) top center no-repeat;
                    width: 100%;
                    height: 50px;
                    float: left;
                    /*background-size: contain;*/
                    margin-bottom: 20px;">
            </div>
            <%
                }

            %>--%>


            <%--<div class="login-logo" style="background: url(/images/partner/<%=icon%>) top center no-repeat;--%>
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

               <%-- <%
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
                </div>--%>


            <div class="login-body">
                <div class="login-title" style="text-align: center;"><strong>Partner Administration Module</strong></div>


                <div class="form-group">
                    <div class="login-subtitle" style="line-height: 35px;">
                        <div class="col-md-12">
                            <p>Thank you for visiting <strong><%--<%=user.getAccountName()%>'s--%> Partner Administration Module</strong></p>
                        </div>
                    </div>
                    <%--                    <div class="col-md-6">
                                            <button id="submit" type=submit class="btn btn-info btn-block" style="font-family: 'Open Sans', sans-serif;font-size: 12px;background-color: #29AAA1;border-color:#29AAA1; ">Log In</button>
                                        </div>--%>
                </div>


                <hr>


                <div class="login-subtitle" style="line-height: 35px;">

                    <p> Click here</p>
                    <form action="/partner/login.jsp" method="post"><input type="hidden"  value="<%=partnerid%>" name="partnerid">
                        <input type="hidden" value="<%=com%>" name="fromtype"><input type="submit" value="LOGIN" name="submit" class="btn btn-info btn-block" style="font-family: 'Open Sans', sans-serif;font-size: 12px; background-color: #29AAA1; border-color: #29AAA1;"></form> <p> to go back to the <strong>Partner Login Page</strong></p>
                </div>
            </div>
        </div>
    </div>


</form>
<!-- End of page -->

</body>
</html>


