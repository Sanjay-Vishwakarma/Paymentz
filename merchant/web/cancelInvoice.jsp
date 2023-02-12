<%@ page import="java.util.Enumeration" %>
<%--
  Created by IntelliJ IDEA.
  User: Dhiresh
  Date: 30/12/12
  Time: 12:46 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="Top.jsp"%>
<html>
<head>
    <title></title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <%--<link rel="stylesheet" type="text/css" href="/merchant/css/styles123.css">
    <script type="text/javascript" src='/merchant/css/new/html5shiv.min.js'></script>
    <script type="text/javascript" src='/merchant/css/new/respond.min.js'></script>

    <link rel="stylesheet" href=" /merchant/transactionCSS/css/main.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script type="text/javascript" src='/merchant/css/new/html5.js'></script>--%>
</head>
<body class="pace-done widescreen fixed-left-void">
<%int invoiceno = (Integer) request.getAttribute("invoiceno");
    String reason= (String) request.getAttribute("cancelreason");
%>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">

            <div class="row">
                <div class="col-sm-3 col-sm-offset-9">
                    <%--<form class="form-horizontal" role="form">--%>
                    <%-- <div class="col-sm-9 col-sm-offset-3">
                         <div class="form-group form-search search-box has-feedback">

                             <form action="/merchant/invoice.jsp?ctoken=<%=ctoken%>" method="post" name="form">
                                 <%

                                     Enumeration<String> aName=request.getParameterNames();
                                     while(aName.hasMoreElements())
                                     {
                                         String name=aName.nextElement();
                                         String value = request.getParameter(name);
                                         if(value==null || value.equals("null"))
                                         {
                                             value = "";
                                         }
                                 %>
                                 <input type=hidden name=<%=name%> value=<%=value%>>
                                 <%
                                     }
                                 %>
                             </form>

                             <button class="btn-xs" type="submit" name="submit" name="B1" style="background: transparent;border: 0;">
                                 &lt;%&ndash;<i class="fa fa-sign-in"></i>&ndash;%&gt;
                                 <img style="height: 35px;" src="/merchant/images/goBack.png">
                                 &lt;%&ndash;&nbsp;Go Back&ndash;%&gt;
                             </button>
                         </div>
                     </div>--%>
                    <%--</form>--%>

                    <div class="pull-right">
                        <div class="btn-group">
                            <form action="/merchant/servlet/Invoice?ctoken=<%=ctoken%>" name="form" method="post">
                                <%
                                    Enumeration<String> stringEnumeration=request.getParameterNames();
                                    while(stringEnumeration.hasMoreElements())
                                    {
                                        String name=stringEnumeration.nextElement();

                                        out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                                    }
                                %>
                                <%--<button class="btn-xs" type="submit" name="B2" style="background: transparent;border: 0;">
                                    <img style="height: 35px;" src="/merchant/images/goBack.png">
                                </button>--%>
                                <button type="submit" name="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-arrow-circle-left" aria-hidden="true"></i>&nbsp;&nbsp;Go Back</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            <br><br>

            <div class="row">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Cancel Invoice</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content">
                            <div class="bg-info"><i class="fa fa-info-circle"></i>&nbsp;&nbsp;
                                The Invoice with Invoice Number <%=invoiceno%> has been cancelled <br><br>
                                &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;The Reason Mentioned was<b> <%=reason%></b>
                            </div>
                            <%--<div class="rowcontainer-fluid " >
                                <div class="row rowadd" >
                                    <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">
                                        <div class="form foreground bodypanelfont_color panelbody_color">
                                            <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="color:#34495e" ><i class="fa fa-th-large"></i>&nbsp;&nbsp; Invoice Cancelled</h2>
                                            <hr class="hrform">
                                        </div>
                                        <br><br>
                                        <table align=center width="100%" border="0" cellspacing="0" cellpadding="2" bordercolor="#E4D6C9" bgcolor="#ffffff">
                                            <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>

                                            <tr>
                                                <td rowspan=3 colspan=3 align=center class="alert alert-danger alert-dismissable">
                                                    The Invoice with Invoice Number <%=invoiceno%> has been cancelled <br> The Reason Mentioned was <%=reason%>
                                                </td></tr>
                                            <tr></tr><tr></tr><tr></tr><tr></tr>
                                            <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
                                            <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
                                            <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
                                            <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>

                                            <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
                                            <tr><td align=center colspan=3 class="textb">&nbsp; Click <a href="/merchant/servlet/Invoice?ctoken=<%=ctoken%>" > Here </a> to Go Back &nbsp;</td></tr>
                                            <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
                                        </table>
                                    </div>
                                </div>
                            </div>--%>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
