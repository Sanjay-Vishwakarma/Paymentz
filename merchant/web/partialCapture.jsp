<%@ page errorPage="error.jsp" import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));%>
<%@ include file="Top.jsp" %>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title><%=company%> Merchant POD-Partial Capture</title>

    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <%--<link rel="stylesheet" href=" /merchant/transactionCSS/css/main.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>--%>

    <%--<script type="text/javascript" src='/merchant/css/new/html5shiv.min.js'></script>
    <script type="text/javascript" src='/merchant/css/new/respond.min.js'></script>--%>
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

            /*tr:nth-child(odd), tr:nth-child(even) {background: #ffffff;}*/

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

        /*tr:nth-child(odd) {background: #F9F9F9;}*/

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
<body class="pace-done widescreen fixed-left-void">

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">

            <form name="form" method="post" action="/merchant/servlet/Pod?ctoken=<%=ctoken%>">
                <div class="pull-right">
                    <div class="btn-group">

                        <input type=hidden name=partialCapture value=>
                        <input type=hidden name=trackingid value=>
                        <%
                            ResourceBundle rb1 = null;
                            String language_property1 = (String)session.getAttribute("language_property");
                            rb1 = LoadProperties.getProperty(language_property1);
                            String partialCapture_PartialCapture = StringUtils.isNotEmpty(rb1.getString("partialCapture_PartialCapture"))?rb1.getString("partialCapture_PartialCapture"): "Partial Capture";
                            String partialCapture_capture = StringUtils.isNotEmpty(rb1.getString("partialCapture_capture"))?rb1.getString("partialCapture_capture"): "You can capture as much amount as you want.Make sure that capture amount should not be greater than Transaction Amount + OverCapture% allowed by bank.This partial capture will be considered as final capture and you will not be able to capture remaining amount again.";
                            String partialCapture_TrackingID = StringUtils.isNotEmpty(rb1.getString("partialCapture_TrackingID"))?rb1.getString("partialCapture_TrackingID"): "Tracking ID";
                            String partialCapture_TransactionAmount = StringUtils.isNotEmpty(rb1.getString("partialCapture_TransactionAmount"))?rb1.getString("partialCapture_TransactionAmount"): "Transaction Amount";
                            String partialCapture_Description = StringUtils.isNotEmpty(rb1.getString("partialCapture_Description"))?rb1.getString("partialCapture_Description"): "Description";
                            String partialCapture_CaptureAmount = StringUtils.isNotEmpty(rb1.getString("partialCapture_CaptureAmount"))?rb1.getString("partialCapture_CaptureAmount"): "Capture Amount";
                            String partialCapture_POD = StringUtils.isNotEmpty(rb1.getString("partialCapture_POD"))?rb1.getString("partialCapture_POD"): "POD*";
                            String partialCapture_Capture = StringUtils.isNotEmpty(rb1.getString("partialCapture_Capture"))?rb1.getString("partialCapture_Capture"): "Capture";
                            String partialCapture_Information = StringUtils.isNotEmpty(rb1.getString("partialCapture_Information"))?rb1.getString("partialCapture_Information"): "Information";
                            String partialCapture_no = StringUtils.isNotEmpty(rb1.getString("partialCapture_no"))?rb1.getString("partialCapture_no"): "No records found.";
                            String partialCapture_Go_Back = StringUtils.isNotEmpty(rb1.getString("partialCapture_Go_Back"))?rb1.getString("partialCapture_Go_Back"): "Go Back";
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
                        <%--<button class="btn-xs" type="submit" name="B2" style="background: transparent;border: 0;">
                            <img style="height: 35px;" src="/merchant/images/goBack.png">
                        </button>--%>
                        <button type="submit" name="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-arrow-circle-left" aria-hidden="true"></i>&nbsp;&nbsp;<%=partialCapture_Go_Back%></button>

                    </div>
                </div>
            </form>
            <br><br><br>

            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=partialCapture_PartialCapture%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">                        <%-- End Radio Button--%>


                                <%

                                    Hashtable hash = (Hashtable) request.getAttribute("poddetails");

                                    //out.println(hash);

                                    Hashtable temphash = null;

                                    int records = 0;

                                    try
                                    {
                                        records = Integer.parseInt((String) hash.get("records"));
                                    }
                                    catch (Exception ex)
                                    {
                                    }

                                    String style = "class=td0";
                                    String ext = "light";

                                    if (request.getAttribute("message") != null)
                                    {
                                        out.println((String) request.getAttribute("message"));
                                    }


                                    if (records > 0 )
                                    {


                                        temphash = (Hashtable) hash.get("1");
                                        if (temphash.get("status").equals("capturesuccess") || temphash.get("status").equals("capturestarted"))
                                        {
                                            out.println(Functions.NewShowConfirmation1("Done", "This transaction has already been captured."));
                                        }

                                %>


                                <%--<table border="0" cellpadding="0" cellspacing="0" width="95%" bordercolor="#FFFFFF" align="center">
                                    <tr>
                                        <td>&nbsp;</td>
                                    </tr>
                                    <tr>
                                        &lt;%&ndash;<td align="center" class="textb">You can capture as much amount as you want.Make sure that it should not greater
                                            than transaction amount.This partial capture will be consider as final capture and you will not be able to
                                            capture remaining amount again.</td>&ndash;%&gt;
                                            <td align="center" class="textb">You can capture as much amount as you want.
                                                Make sure that capture amount should not be greater than Transaction Amount + OverCapture% allowed by bank.
                                                This partial capture will be considered as final capture and you will not be able to capture remaining amount again.</td>
                                    </tr>
                                    <tr>
                                        <td>&nbsp;</td>
                                    </tr>
                                </table>--%>

                                <label class="bg-info" style="font-family:Open Sans;font-size: 13px;font-weight: 600;text-align: center; ">
                                    <%=partialCapture_capture%>
                                </label><br>
                                <form name="f1" method="post" action="PartialCapture">


                                    <%-- <table align="center" width="90%" border="1">--%>
                                    <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <thead style="color: white;">
                                        <tr>
                                            <th style="text-align: center" ><%=partialCapture_TrackingID%></th>
                                            <th style="text-align: center" ><%=partialCapture_TransactionAmount%></th>
                                            <th style="text-align: center" ><%=partialCapture_Description%></th>
                                            <th style="text-align: center" ><%=partialCapture_CaptureAmount%></th>
                                            <th style="text-align: center" ><%=partialCapture_POD%></th>
                                        </tr>
                                        </thead>

                                        <tbody>
                                        <tr>
                                            <td data-label="Tracking ID" style="text-align: center"><%=ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))%></td>
                                            <td data-label="Transaction Amount" style="text-align: center"><%=ESAPI.encoder().encodeForHTML((String) temphash.get("amount"))%></td>

                                            <%
                                                ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                                            %>

                                            <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                            <td data-label="Description" style="text-align: center"><%=ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("description"))%></td>
                                            <td data-label="Capture Amount" style="text-align: center">
                                                <input class="form-control" type="text" name="captureamount" style="text-align: center" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("amount"))%>"></td>
                                            <td data-label="POD*" value="" style="text-align: center">
                                                <input type="text" style="text-align: center" class="form-control" name="pod" value=""></td>



                                        </tr></tbody>

                                        <%--<tr>


                                        </tr>

                                        <tr>


                                        </tr>--%>
                                    </table>
                                    <br>


                                    <div class="form-group col-md-5"></div>
                                    <div class="form-group col-md-4">
                                        <button type="submit" class="btn btn-default" name="B1" value="Capture" >
                                            <span><i class="fa fa-save"></i></span>
                                            &nbsp;&nbsp;<%=partialCapture_Capture%>
                                        </button>

                                    </div>
                                    <input type="hidden" name="icicitransid" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("trackingid"))%>">
                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("accountid"))%>" name="accountid">
                                </form>

                                <%
                                    }

                                    else
                                    {
                                        String errormsg=(String) request.getAttribute("error");

                                        out.println(Functions.NewShowConfirmation1(partialCapture_Information, partialCapture_no+errormsg));

                                    }
                                %>

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