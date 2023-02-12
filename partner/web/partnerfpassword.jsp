<%@ page import="com.logicboxes.util.ApplicationProperties,
                 org.owasp.esapi.User,
                 com.directi.pg.Functions,
                 com.directi.pg.DefaultUser" %>
<%String company = (String)session.getAttribute("company");%>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="net.partner.PartnerFunctions" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.manager.dao.PartnerDAO" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%--
<%@ include file="ietest.jsp" %>--%>


<%
    String partnerid=(String) session.getAttribute("merchantid");

    String ctoken= request.getParameter("ctoken");
    User user =  (User)session.getAttribute("Anonymous");
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

    String logoHeight="38";
    String logoWidth="140";
    PartnerFunctions partnerFunctions = new PartnerFunctions();


    PartnerDAO partnerDAO = new PartnerDAO();
    String partner_id="";
    if(partnerFunctions.isValueNull(request.getParameter("partnerid"))){
        partner_id = request.getParameter("partnerid");
    }else{
        partner_id =partnerid;
    }

    PartnerDetailsVO partnerDetailsVOMain = null;
    if(partnerFunctions.isValueNull(partner_id))
    {
        partnerDetailsVOMain = partnerDAO.geturl(partner_id);
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


    String icon = "";
    String logo = "";

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
    /*else
    {
        icon = "Pay_Icon.png";
    }*/
%>

<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title> Partner Forgot Password</title>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
    </script>
    <%--<link rel="stylesheet" type="text/css" href="/partner/style1/styles123.css">--%>
    <link href="/partner/css/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet">

    <!-- META SECTION -->
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />

    <%--<link rel="icon" href="/merchant/NewCss/favicon.ico" type="image/x-icon" />--%>
    <!-- END META SECTION -->

    <!-- CSS INCLUDE -->
    <link href="/partner/NewCss/css/style.css" rel="stylesheet" type="text/css" />
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

             margin-top: 10px;
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

                        <a href="#" title="<%=logo%>" class="main-logo" ><img src="/images/partner/<%=logo%>" style="width: <%=logoWidth%>px;height: <%=logoHeight%>px;object-fit: contain;"></a>
                        <ul class="top-nav pull-right clearfix list-inline">

                            <li>
                                <a href="/partner/login.jsp?" class="btn btn-green-3" style="font-weight: 600;width: 85px;">Log In</a>
                                <%--  <button type="button" class="btn btn-green-3" style="width: 90px;" >Log In</button>--%>
                            </li>

                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
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

            <%--<%
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

        <%--<div class="login-logo" style="background: url(/partner/images/pay2.png) top center no-repeat;width: 100%;height: 50px;float: left;background-size: contain;margin-bottom: 20px;">&lt;%&ndash;<img src="/partner/images/pay2.png">&ndash;%&gt;</div>--%>

        <%--        <%
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
                        background-size: contain;
                        margin-bottom: 20px;">
                </div>
                <%
                    }

                %>--%>
        <div class="login-body">
            <div class="login-title"><p align="center" class="subtitle1" style="font-weight: bold; /*color: #FFFFFF;*/font-family: 'Open Sans', sans-serif;font-size: 18px;">Forgot Password</p></div>


            <p>
            <table border="0" width="100%" align="center">
                <%--<br><br>--%>
                <tr>
                    <td class="textb" align="center" style="/*color: #FFFFFF;*/font-family: 'Open Sans', sans-serif;font-size: 14px;">Please fill in your username. An email will be sent to you containing a new password.</td>
                    </td>
                </tr>
                <tr>
                    <td class="textb" align="center" style="/*color: #FFFFFF;*/font-family: 'Open Sans', sans-serif;font-size: 14px;">Note:Please login using the new password within one hour of receiving the email.
                    </td>
                </tr>

            </table>
            <hr class="hrnew"><%--<br>--%>
            <%
                String action = (String) request.getParameter("action");
                String error=(String)request.getAttribute("error");
                if(error!=null)
                {
                    out.println("<p><center><font class=\"textb\">"+error+"</font></center></p>");
                }
                else if (action != null)
                {
                    if (action.equalsIgnoreCase("F"))
                    {
                        out.println("<p><center><font class=\"textb\">*Invalid Credentials.</font></center></p>");
                    }
                    else if (action.equalsIgnoreCase("E"))
                    {
                        out.println("<p><center><font class=\"textb\">Invalid Input.</font></center></p>");
                    }
                }
            %>
            <form action="/partner/net/PartnerForgotPassword?ctoken=<%=ctoken%>" method="post">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <div class="form-group col-md-12 has-feedback">
                    <label  style="font-family:Open Sans;font-size: 15px;font-weight: 600; /*color: #FFFFFF;*/">Username:<br><br></label>
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

            </p>
        </div>
    </div>

    <script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
    <script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
    <link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
    <link href="/partner/cookies/cookies_popup.css" rel="stylesheet">

    </body>
</div>

</html>
