<%--
  Created by IntelliJ IDEA.
  User: Dhiresh
  Date: 3/1/13
  Time: 3:39 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/Top.jsp"%>
<div id="page-content-wrapper">
    <div class="container-fluid xyz">
        <html>
        <head>
            <%--<link rel="stylesheet" type="text/css" href="/merchant/css/styles123.css">--%>
           <%-- <script type="text/javascript" src='/merchant/css/new/html5shiv.min.js'></script>
            <script type="text/javascript" src='/merchant/css/new/respond.min.js'></script>--%>
            <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
       <%--     <link rel="stylesheet" href=" /merchant/transactionCSS/css/main.css">
            <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
            <script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>
            <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
            <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
            <script type="text/javascript" src='/merchant/css/new/html5.js'></script>--%>

            <title>Invoice Management System</title>


        </head>
        <body class="pace-done widescreen fixed-left-void bodybackground">
        <%int invoiceno = (Integer) request.getAttribute("invoiceno");
            ResourceBundle rb1 = null;
            String language_property1 = (String)session.getAttribute("language_property");
            rb1 = LoadProperties.getProperty(language_property1);
            String remindInvoice_send = StringUtils.isNotEmpty(rb1.getString("remindInvoice_send"))?rb1.getString("remindInvoice_send"): "An E-Mail has Been Sent Again to the Customers E-Mail Address for Invoice Number";
            String remindInvoice_You = StringUtils.isNotEmpty(rb1.getString("remindInvoice_You"))?rb1.getString("remindInvoice_You"): "You have";
            String remindInvoice_retries = StringUtils.isNotEmpty(rb1.getString("remindInvoice_retries"))?rb1.getString("remindInvoice_retries"): "Retries Left to send This Email";
            String remindInvoice_Invoice_Reminder = StringUtils.isNotEmpty(rb1.getString("remindInvoice_Invoice_Reminder"))?rb1.getString("remindInvoice_Invoice_Reminder"): "Invoice Reminder";

        %>
        <%int remindercounter = (Integer) request.getAttribute("remindercounter");
        %>
        <div class="rowcontainer-fluid " >
            <div class="row rowadd" >
                <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">
                    <div class="form foreground bodypanelfont_color panelbody_color">
                        <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="color:#34495e" ><i class="fa fa-th-large"></i>&nbsp;&nbsp; <%=remindInvoice_Invoice_Reminder%></h2>
                        <hr class="hrform">
                    </div>
                    <br><br>
                    <table align=center width="750" border="0" cellspacing="0" cellpadding="2" bordercolor="#E4D6C9" >
                        <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
                        <tr>
                            <td rowspan=3 colspan=3 align=center ><h5 class="bg-infoorange" style="text-align: center;"><i class="fa fa-exclamation-triangle"></i>&nbsp;&nbsp;
                                <% if(request.getAttribute("error")==null)
                                { %>

                                <%=remindInvoice_send%><%=invoiceno%> <br> <%=remindInvoice_You%> <%=8-remindercounter%> <%=remindInvoice_retries%>
                                <% }
                                else
                                { %>

                                <%=(String)request.getAttribute("error")%>

                                <%}%>
                                </h5>
                            </td></tr>
                        <tr></tr><tr></tr><tr></tr><tr></tr>
                        <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
                        <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
                        <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
                        <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>

                        <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
                        <tr><td align=center class="textb" colspan=3>&nbsp; Click <a href="/merchant/servlet/Invoice?ctoken=<%=ctoken%>" > Here </a> to Go Back &nbsp;</td></tr>
                        <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>


                    </table>
                </div>
            </div>
        </div>
        </body>
        </html>
    </div>
</div>