<%@ page import="com.directi.pg.Functions,com.directi.pg.LoadProperties,com.directi.pg.TransactionEntry,com.directi.pg.core.GatewayAccountService" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ include file="top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 10/8/13
  Time: 2:47 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String company          = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    session.setAttribute("submit","TransactionsLogs");
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (partner.isLoggedInPartner(session))
    {
%>
<style type="text/css">
    .table#myTable > thead > tr > th {font-weight: inherit;text-align: center;}

    .hide_label{
        color: transparent;
        user-select: none;
    }
    .table-condensed a.btn{
        padding: 0;
    }

    .table-condensed .separator{
        padding-left: 0;
        padding-right: 0;
    }
    #ui-id-2
    {
        overflow: auto;
        max-height: 350px;
    }

</style>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">

    <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>--%>
    <%--<script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
    <script type="text/javascript" language="JavaScript" src="/partner/javascript/autocomplete_partner_memberid.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>

    <%--FontAwesome CDN--%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css" integrity="sha512-KfkfwYDsLkIlwQp6LFnl8zNdLGxu9YAA1QvwINks4PhcElQSvqcyVLLD9aMhXd13uQjoXtEKNosOWaZqXgel0g==" crossorigin="anonymous" referrerpolicy="no-referrer" />


    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <script src="/merchant/datepicker/datetimepicker/moment-with-locales.js"></script>
    <script src="/merchant/datepicker/datetimepicker/bootstrap-datetimepicker.min.js"></script>

    <title><%=company%> Transaction Management> Transactions Logs</title>

</head>
<body>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">
                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Transactions Logs </strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <%
                                LinkedList<String> fileNameList      = null;
                                String errorMessage                 = "";
                                Functions functions                 = new Functions();
                                if(request.getAttribute("fileNameList") != null){
                                    fileNameList = (LinkedList<String>) request.getAttribute("fileNameList");
                                }
                                if(request.getAttribute("error") != null){
                                    errorMessage = (String)request.getAttribute("error");
                                }

                                if(functions.isValueNull(errorMessage))
                                {
                                    out.println("<div class=\"bg-info\" style=\"text-align:center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;"+ errorMessage +"</div>");
                                }
                                if(fileNameList != null && fileNameList.size() > 0 ){
                            %>

                            <div style="width:100%; height:50%; overflow:auto;">
                                <table id="myTable" class="display table table-striped table-bordered"
                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;margin-top: 6px;">
                                    <thead style="color: white;">
                                    <tr>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>File Name</b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Action</b></td>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <%
                                        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                                        for( String logFileName : fileNameList){
                                            String logggerDisplayName ="";

                                            if(logFileName.equalsIgnoreCase("facilero.log")){
                                                String dateStr          = formater.format(new Date()).toString();
                                                logggerDisplayName      = logFileName+"."+dateStr;
                                            }else{
                                                logggerDisplayName = logFileName;
                                            }

                                            out.println("<tr>");
                                            out.println("<td style=\"text-align: center\" data-label=\"logFileName\" align=\"center\">&nbsp;" + logggerDisplayName + "</td>");
                                            out.println("<td style=\"text-align: center\" >" +
                                                    "<form action=\"/partner/net/TransactionsLogs?ctoken="+ctoken+"\" method=\"POST\" name=\"formAction\" >" +
                                                    "<input type=\"hidden\" name=\"fileName\" value=\""+logFileName+"\">" +
                                                    "<input type=\"hidden\" name=\"action\" value=\"download\">" +
                                                    "<button type=\"submit\" name=\"Download\" value=\"download\" class=\"btn btn-default\">"+
                                                    "Download"+
                                                    "</button>" +
                                                    "</form></td>");
                                            out.println("</tr>");
                                        }
                                    %>
                                    </tbody>
                                </table>
                            </div>
                            <%
                                }
                            %>
                        </div>
                    </div>
    <%
        }
        else
        {
            response.sendRedirect("/partner/logout.jsp");
            return;
        }
    %>
                </div>


        </div>
    </div>
</div>
<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
</body>
</html>