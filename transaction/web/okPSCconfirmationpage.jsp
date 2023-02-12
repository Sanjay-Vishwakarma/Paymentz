<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericTransDetailsVO" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.manager.MerchantConfigManager" %>
<%@ page import="com.manager.PartnerManager" %>
<%@ page import="com.payment.paySafeCard.GetStatus" %>
<%@ page import="com.payment.common.core.CommResponseVO" %>
<%--
  Created by IntelliJ IDEA.
  User: Nishant
  Date: 1/6/15
  Time: 2:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    response.setHeader("Cache-control","no-store"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP1.0
    response.setDateHeader("Expire",0); //prevents caching at the proxy server
%>
<html>
<head>
    <title></title>

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
        .mainbackgroundcolor
        {
        <%=session.getAttribute("mainbackgroundcolor")!=null?"background:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
        }
    </style>

    <script src='/merchant/css/menu.js'></script>
    <script type='text/javascript' src='/merchant/css/menu_jquery.js'></script>
    <script type='text/javascript' src='/transaction/javascript/jquery.min.js'></script>
    <link rel="stylesheet" type="text/css" href="/merchant/css/styles123.css">
    <link rel="stylesheet" type="text/css" href="/merchant/css/new/styles_new.css">
    <link rel="stylesheet" type="text/css" href="/merchant/newdate/styles123.css">
    <link rel="stylesheet" type="text/css" href="/merchant/style/styles123.css">
    <%--
    <link rel="stylesheet" type="text/css" href="/merchant/javascript/styleschange.css">
    --%>
    <script src='/merchant/css/bootstrap.min.js'></script>
    <link href="/merchant/css/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <script language="javascript">


        function closeMe()
        {
            window.opener = self;
            window.close();
        }
    </script>
</head>
<body class="bodybackground mainbackgroundcolor">

<%
    String trackingid=request.getParameter("trackingid");
    String orderid=request.getParameter("orderdesc");
    String amount =request.getParameter("amount");
    String currency = request.getParameter("currency");
    String toid= request.getParameter("toid");
    String redirecturl=request.getParameter("redirecturl");
    String bgcolor="txtboxconfirm";
    String merchantLogo="";
    String merchantLogoFlag="";
    CommResponseVO commResponseVO=null;
    GetStatus getStatus=new GetStatus();
    commResponseVO = getStatus.checkDispostionState(trackingid,true);
    CommonValidatorVO commonValidatorVO=new CommonValidatorVO();
    PartnerManager partnerManager=new PartnerManager();
    partnerManager.getPartnerDetailFromMemberId(toid,commonValidatorVO);
    String partnerId = commonValidatorVO.getParetnerId();
    merchantLogo = commonValidatorVO.getLogoName();
    String partnerName = commonValidatorVO.getPartnerName();
    String powerBy = "";

    String billingDesc="";
    if(commResponseVO!=null && commResponseVO.getDescriptor()!=null)
    {
        billingDesc=commResponseVO.getDescriptor();
    }
    session.invalidate();
    if(merchantLogoFlag.equalsIgnoreCase("Y"))
    {
        if(session.getAttribute("merchantLogoName")!=null)
        {
            merchantLogo = (String)session.getAttribute("merchantLogoName");
        }
    }
    else
    {
        merchantLogo = commonValidatorVO.getLogoName();
    }

%>

<form action="<%=redirecturl%>" method="post" name="form1">
    <div class="container-fluid ">
        <div class="row">
            <div class="col-md-8 col-md-offset-2">

                <div class="logo form foreground">

                    <center><img src="/merchant/images/<%=merchantLogo%>" style="width:203px;height: 60px" border="0"></center>

                    <center><%=partnerName%> PaymentGateway</center>

                </div>
                <div class="form foreground bodypanelfont_color panelbody_color">
                    <section class="creditly-wrapper" style="min-height: 310px;">
                        <div class="credit-card-wrapper" style="padding: 0px 8px;">

                            <h2 class="col-md-12 background panelheading_color headpanelfont_color">Transaction Details</h2>
                            <div class="form-group col-md-6">
                                <label for="trackingid">Tracking ID</label>
                                <input  class="form-control" disabled=""  value="<%=trackingid%>" type="Text" id="trackingid">

                            </div>
                            <div class="form-group col-md-6">
                                <label for="description">Description</label>
                                <input type="text" class="form-control" disabled=""  id="description" name="description"  value="<%=orderid%>" placeholder="Description">

                            </div>
                            <div class="form-group col-md-6">
                                <label for="amount">Amount</label>
                                <input type="textarea"  disabled="" value="<%=currency%>  <%=amount%>" class="form-control" disabled=""   placeholder="Amount" id="amount">
                            </div>
                            <div class="form-group col-md-12">
                                <label for="status">Status</label>
                                <input type="text"  value="Successful" id="status" class="form-control" disabled="">
                            </div>
                        </div>
                    </section>
                </div>
            </div>
        </div>
    </div>
</form>
<div class="container-fluid ">
    <div class="row">
        <div class="col-md-8 col-md-offset-2">

            <div class="logo form foreground bodypanelfont_color panelbody_color" >
                <%
                    if(redirecturl.trim().equalsIgnoreCase("https://secure.pz.com/merchant/"))
                    {
                %>
                <div align="center" class="textb">
                    <input type="submit" name="submit" value="Continue" class="btn btn-info" onclick="closeMe()" >
                </div>
                <%}else{%>
                <div align="center" class="textb">
                    <input type="submit" name="submit" value="Continue" class="btn btn-info">
                </div>
                <%}%>

                <div align="center" class="textb">
                    <b>Important&nbsp;:-&nbsp;</b>Note that this transaction will appear on your Credit Card Statement: <b><%=billingDesc%></b> <br><br>
                </div>


                <input type="hidden" name="trackingid" value="<%=trackingid%>">
                <input type="hidden" name="desc" value="<%=orderid%>">
                <input type="hidden" name="amount" value="<%=amount%>">
                </form>

                <table border="0" align="center" cellpadding="5" cellspacing="0" width="40%">
                    <tbody>
                    <tr>
                        <td>&nbsp;</td>
                        <td width="10%" height="1">
                            <p><a target="_blank" href="http://sisainfosec.com/site/certificate/13835412899812182686"><IMG border=0 height=90 src="/merchant/images/Certificatelogo.png" width=150></a></p>
                        </td>
                        <td>&nbsp;</td>
                        <td width="10%" height="1">
                            <p align="right"><img border="0" height="40" width="130" src="/merchant/images/<%=merchantLogo%>"></p>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>
</body>
</html>