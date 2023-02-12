<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 4/14/15
  Time: 4:57 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page errorPage="error.jsp"
         import="com.directi.pg.Functions,com.directi.pg.TransactionEntry,com.logicboxes.util.ApplicationProperties,
                 org.owasp.esapi.errors.ValidationException" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.vo.RecurringBillingVO" %>

<%@ include file="Top.jsp" %>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <%--<script language="JavaScript" src="/merchant/FusionCharts/FusionCharts.js?ver=1"></script>
    <link rel="stylesheet" type="text/css" href="/merchant/FusionCharts/style.css" >

    <link rel="stylesheet" href=" /merchant/transactionCSS/css/main.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>--%>
    <script type="text/javascript">
        function checkValue()
        {
            var hat = document.getElementById("rec_interval").value.trim();
            if(hat!='')
            {
                var hatto = document.getElementById("interval").options.length;
                for(i=0;i<hatto;i++)
                {
                    var intervalList = document.getElementById("interval").options[i].value.trim();
                    if(hat==intervalList)
                    {
                        document.getElementById("interval").options[i].selected=true;
                    }
                }
            }

            var mHat = document.getElementById("rec_frequency").value.trim();
            if(mHat!='')
            {
                var mHatto = document.getElementById("frequency").options.length;
                for(i=0;i<mHatto;i++)
                {
                    var frequencyList = document.getElementById("frequency").options[i].value.trim();
                    if(mHat==frequencyList)
                    {
                        document.getElementById("frequency").options[i].selected=true;
                    }
                }
            }

            var rHat = document.getElementById("rec_rundate").value.trim();
            if(rHat!='')
            {
                var rHatto = document.getElementById("runDate").options.length;
                for(i=0;i<rHatto;i++)
                {
                    var runList = document.getElementById("runDate").options[i].value.trim();
                    if(rHat==runList)
                    {
                        document.getElementById("runDate").options[i].selected=true;
                    }
                }
            }

            var aHat = document.getElementById("act").value.trim();
            if(aHat!='')
            {
                var aHatto = document.getElementById("actDeact").options.length;
                for(i=0;i<aHatto;i++)
                {
                    var actList = document.getElementById("actDeact").options[i].value.trim();
                    if(aHat==actList)
                    {
                        document.getElementById("actDeact").options[i].selected=true;
                    }
                }
            }
        }
    </script>
    <style type="text/css">

        #main{background-color: #ffffff}

        :target:before {
            content: "";
            display: block;
            height: 50px;
            margin: -50px 0 0;
        }

        .table > thead > tr > th {font-weight: inherit;}

        :target:before {
            content: "";
            display: block;
            height: 90px;
            margin: -50px 0 0;
        }

        footer{border-top:none;margin-top: 0;padding: 0;}

        /********************Table Responsive Start**************************/

        @media (max-width: 640px){

            table {border: 0;}

            /*table tr {
                padding-top: 20px;
                padding-bottom: 20px;
                display: block;
            }*/

            table thead { display: none;}

            tr:nth-child(odd), tr:nth-child(even) {background: #ffffff;}

            table td {
                display: block;
                border-bottom: none;
                padding-left: 0;
                padding-right: 0;
            }

            table td:before {
                content: attr(data-label);
                float: left;
                width: 100%;
                font-weight: bold;
            }

        }

        table {
            width: 100%;
            max-width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
            display: table;
            border-collapse: separate;
            border-color: grey;
        }

        thead {
            display: table-header-group;
            vertical-align: middle;
            border-color: inherit;

        }

        tr:nth-child(odd) {background: #F9F9F9;}

        tr {
            display: table-row;
            vertical-align: inherit;
            border-color: inherit;
        }

        th {padding-right: 1em;text-align: left;font-weight: bold;}

        td, th {display: table-cell;vertical-align: inherit;}

        tbody {
            display: table-row-group;
            vertical-align: middle;
            border-color: inherit;
        }

        td {
            padding-top: 6px;
            padding-bottom: 6px;
            padding-left: 10px;
            padding-right: 10px;
            vertical-align: top;
            border-bottom: none;
        }

        .table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td, .table>tbody>tr>td, .table>tfoot>tr>td{border-top: 1px solid #ddd;}

        /********************Table Responsive Ends**************************/

    </style>
</head>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
%>
<title> <%=company%> | Recurring Billing Modification</title>
<body class="pace-done widescreen fixed-left-void">
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">

            <div class="pull-right">
                <div class="btn-group">
                    <form action="/merchant/servlet/RecurringModule?ctoken=<%=ctoken%>" name="form" method="post">

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
            <%--<div class="rowcontainer-fluid " >
        <div class="row rowadd" >
            <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">
                <div class="form foreground bodypanelfont_color panelbody_color">
                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="color:#34495e" ><i class="fa fa-th-large"></i>&nbsp;&nbsp; Recurring Module</h2>
                    <hr class="hrform">
                </div>

                <form action="/merchant/servlet/RecurringModule?ctoken=<%=ctoken%>" name="form" method="post" >
                    <input type="hidden" value="<%=ctoken%>" name="ctoken">

                    <%
                        String errormsg1 = (String) request.getAttribute("error");
                        if (errormsg1 != null)
                        {
                            out.println("<center><font class=\"alert alert-danger alert-dismissable\"><b>"+errormsg1+"<br></b></font></center>");
                        }

                    %>

                    <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                    &lt;%&ndash;<div class="form-group col-md-4 has-feedback">
                        <label >Recurring Billing Id</label>
                        <input name="rbid" size="10" class="form-control">
                    </div>

                    <div class="form-group col-md-4 has-feedback">
                        <label >Tracking ID</label>
                        <input name="trackingid" size="10" class="form-control">
                    </div>

                    <div class="form-group col-md-4 has-feedback">
                        <label >Card Holder Name</label>
                        <input name="name" size="10" class="form-control">
                    </div>

                    <div class="form-group col-md-4 has-feedback">
                        <label >First Six</label>
                        <input name="firstsix" size="10" class="form-control">
                    </div>

                    <div class="form-group col-md-4 has-feedback">
                        <label >Last Four</label>
                        <input name="lastfour" size="10" class="form-control">
                    </div>

                    <div class="form-group col-md-9 has-feedback">&nbsp;</div>
                    <div class="form-group col-md-3">
                        <button type="submit" class="btnblue" style="margin-left:100px;padding-right: 50px;background: rgb(126, 204, 173);">
                            <i class="fa fa-save"></i>
                            &nbsp;&nbsp;Search
                        </button>

                    </div>&ndash;%&gt;
                </form>
            </div>
        </div>
    </div>--%>

            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Update Recurring Billing</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <%--<div class="rowcontainer-fluid " >
                                    <div class="row rowadd" >
                                        <div style="margin-left: -3px;margin-right: -50px;">
                                            <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">
                                                <div class="form foreground bodypanelfont_color panelbody_color">
                                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="color:#34495e" >&nbsp;&nbsp; Update Recurring Billing</h2>
                                                    <hr class="hrform">
                                                </div>--%>
                                <form name="form" method = "post" action="/merchant/servlet/ModifyRecurringBilling?ctoken=<%=ctoken%>" id="form1" name="myformname">
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                    <div class="form-group col-md-3 has-feedback">
                                        <label>Interval</label>
                                        <select name="interval" id="interval" class="form-control">
                                            <%--<option VALUE="" ><%=request.getParameter("interval")%></option>--%>
                                            <option VALUE="Day">Day</option>
                                            <option VALUE="Month">Month</option>
                                            <option VALUE="Year">Year</option>

                                        </select>
                                        <input type="hidden" id="rec_interval" value="<%=request.getParameter("interval")%>">
                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label>Frequency</label>
                                        <select name="frequency" id="frequency" class="form-control" value="">
                                            <%--<option VALUE="" selected><%=request.getParameter("frequency")%></option>--%>
                                            <option VALUE="1">1</option>
                                            <option VALUE="2">2</option>
                                            <option VALUE="3">3</option>
                                            <option VALUE="4">4</option>
                                            <option VALUE="5">5</option>
                                            <option VALUE="6">6</option>
                                            <option VALUE="7">7</option>
                                            <option VALUE="8">8</option>
                                            <option VALUE="9">9</option>
                                            <option VALUE="10">10</option>
                                            <option VALUE="11">11</option>
                                            <option VALUE="12">12</option>
                                            <option VALUE="13">13</option>
                                            <option VALUE="14">14</option>
                                            <option VALUE="15">15</option>
                                            <option VALUE="16">16</option>
                                            <option VALUE="17">17</option>
                                            <option VALUE="18">18</option>
                                            <option VALUE="19">19</option>
                                            <option VALUE="20">20</option>
                                            <option VALUE="21">21</option>
                                            <option VALUE="22">22</option>
                                            <option VALUE="23">23</option>
                                            <option VALUE="24">24</option>
                                            <option VALUE="25">25</option>
                                            <option VALUE="26">26</option>
                                            <option VALUE="27">27</option>
                                            <option VALUE="28">28</option>
                                            <option VALUE="29">29</option>
                                            <option VALUE="30">30</option>
                                            <option VALUE="31">31</option>
                                        </select>
                                        <input type="hidden" id="rec_frequency" value="<%=request.getParameter("frequency")%>">
                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label>Run Date :</label>
                                        <select name="runDate" id="runDate" class="form-control" value="">
                                            <%--<option VALUE="" selected><%=request.getParameter("rundate")%></option>--%>
                                            <option VALUE="1">1</option>
                                            <option VALUE="2">2</option>
                                            <option VALUE="3">3</option>
                                            <option VALUE="4">4</option>
                                            <option VALUE="5">5</option>
                                            <option VALUE="6">6</option>
                                            <option VALUE="7">7</option>
                                            <option VALUE="8">8</option>
                                            <option VALUE="9">9</option>
                                            <option VALUE="10">10</option>
                                            <option VALUE="11">11</option>
                                            <option VALUE="12">12</option>
                                            <option VALUE="13">13</option>
                                            <option VALUE="14">14</option>
                                            <option VALUE="15">15</option>
                                            <option VALUE="16">16</option>
                                            <option VALUE="17">17</option>
                                            <option VALUE="18">18</option>
                                            <option VALUE="19">19</option>
                                            <option VALUE="20">20</option>
                                            <option VALUE="21">21</option>
                                            <option VALUE="22">22</option>
                                            <option VALUE="23">23</option>
                                            <option VALUE="24">24</option>
                                            <option VALUE="25">25</option>
                                            <option VALUE="26">26</option>
                                            <option VALUE="27">27</option>
                                            <option VALUE="28">28</option>
                                        </select>
                                        <input type="hidden" id="rec_rundate" value="<%=request.getParameter("rundate")%>">
                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label >Amount :</label>
                                        <input type="text" value="<%=request.getParameter("amount")%>" class="form-control" name="amount"> </td>
                                    </div>

                                    <div class="form-group col-md-9 has-feedback">&nbsp;</div>

                                    <div class="form-group col-md-3">
                                        <input type="submit" class="btn btn-default" value="Update"></td>

                                    </div>

                                    <input type="hidden" name="rbid" value="<%=request.getParameter("rbid")%>">
                                    <input type="hidden" name="trackingid" value="<%=request.getParameter("trackingid")%>">
                                    <input type="hidden" name="action" value="<%=request.getParameter("action")%>">
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>