<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%--
  Created by IntelliJ IDEA.
  User: sagar
  Date: 8/27/14
  Time: 2:15 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.Functions" %>

<html>
<head>
    <title></title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link rel="stylesheet" href="/merchant/transactionCSS/css/bootstrap.min.css">
    <link rel="stylesheet" href="/merchant/transactionCSS/css/main.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>

    <style>

        .panelheading_color
        {
        <%=session.getAttribute("panelheading_color")!=null?"background-color:"+session.getAttribute("panelheading_color").toString()+"!important":""%>;
        }
        .headpanelfont_color
        {
        <%=session.getAttribute("headpanelfont_color")!=null?"color:"+session.getAttribute("headpanelfont_color").toString()+"!important":""%>;
        }
        .bodypanelfont_color
        {
        <%=session.getAttribute("bodypanelfont_color")!=null?"color:"+session.getAttribute("bodypanelfont_color").toString()+"!important":""%>;
        }
        .panelbody_color
        {
        <%=session.getAttribute("panelbody_color")!=null?"background-color:"+session.getAttribute("panelbody_color").toString()+"!important":""%>;
        }
        .mainbackgroundcolor /*box background*/
        {
        <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
        }
        .bodybackgroundcolor
        {
        <%=session.getAttribute("bodybgcolor")!=null?"background-color:"+session.getAttribute("bodybgcolor").toString()+"!important":""%>;
        }
        .bodyforegroundcolor
        {
        <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
        }
        .navigation_font_color
        {
        <%=session.getAttribute("navigation_font_color")!=null?"background-color:"+session.getAttribute("navigation_font_color").toString()+"!important":""%>;
        }
        .textbox_color
        {
        <%=session.getAttribute("textbox_color")!=null?"background-color:"+session.getAttribute("textbox_color").toString()+"!important":""%>;
        }
        .icon_color
        {
        <%=session.getAttribute("icon_color")!=null?"background-color:"+session.getAttribute("icon_color").toString()+"!important":""%>;
        }

    </style>
</head>
<body class="bodybackgroundcolor bodyforegroundcolor">

<%
    String partnerId="";
    String logoName="";
    String error="";
    String merchantLogoFlag = "";
    String partnerLogoFlag = "";
    String mLogo = "";
    Functions functions = new Functions();

    if(request.getAttribute("error")!=null && !request.getAttribute("error").equals(""))
    {
        error = (String) request.getAttribute("error");
    }
    if(request.getAttribute("standardvo")!=null)
    {
        CommonValidatorVO partnerDetails = (CommonValidatorVO) request.getAttribute("standardvo");
        if(partnerDetails.getMerchantDetailsVO()!=null)
        {
            partnerId = partnerDetails.getMerchantDetailsVO().getPartnerId();
            logoName = partnerDetails.getMerchantDetailsVO().getMerchantLogoName();
            merchantLogoFlag = partnerDetails.getMerchantDetailsVO().getMerchantLogo();
            partnerLogoFlag = partnerDetails.getMerchantDetailsVO().getPartnerLogoFlag();
            mLogo = partnerDetails.getMerchantDetailsVO().getLogoName();

        }

    }
%>

<div class="container-fluid ">
    <div class="row">
        <div class="col-md-8 col-md-offset-2">

            <div class="form foreground mainbackgroundcolor headpanelfont_color">

                <%
                    if(functions.isValueNull(merchantLogoFlag) && merchantLogoFlag.equalsIgnoreCase("Y"))
                    {
                %>
                <br>
                <td width="10%" height="1">
                    <%--<p align="center"><a href="http://www.<compayname>.com"><img border="0"
            height="30" src="/merchant/images/Pay_icon.png" width="65"></a></p>--%>
                    <p align="center"><img src="/images/merchant/<%=session.getAttribute("merchantLogoName")%>"></p>

                </td>
                <%
                }
                else if(functions.isValueNull(partnerLogoFlag) && partnerLogoFlag.equalsIgnoreCase("Y"))
                {
                %>
                <br>
                <td width="10%" height="1">
                    <%--<p align="center"><a href="http://www.compayname.com"><img border="0"
            height="30" src="/merchant/images/Pay_icon.png" width="65"></a></p>--%>
                    <p align="center"><img src="/images/merchant/<%=mLogo%>"></p>
                </td>
                <%
                }
                else
                {
                %>
                <p align="center"><img  src="/images/merchant/<%=mLogo%>"></p>
                <%
                    }
                %>

                <%--<% if(logoName!=null && !logoName.equals("")){ %>

                <center> <img src="/images/merchant/<%=logoName%>" style="width:150px;margin-top: 20px;" border="0"></center>

                <%}%>--%>

            </div>
            <div class="form foreground mainbackgroundcolor headpanelfont_color">

                <h2 class="col-md-12 background form foreground bodypanelfont_color panelbody_color">Error!</h2>

                <%
                    Logger log = new Logger("error.jsp");
                    log.debug("error in error page");
                    log.debug("error in error page ----" + error);
                    if(error!=null)
                    {
                %>

                <center class="headpanelfont_color"><%=error%></center>
                <%
                    }
                %>
            </div>
        </div>
    </div>
    </div>

</body>
</html>
</body>
</html>