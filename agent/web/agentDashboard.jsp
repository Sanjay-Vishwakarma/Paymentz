<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.TreeSet" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.ArrayList" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2/24/14
  Time: 4:00 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="top.jsp"%>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("agentname"));%>
<style type="text/css">
    .table#myTable > thead > tr > th {font-weight: inherit;text-align: center;}
</style>
<html>
<head>
    <script src="/agent/NewCss/libs/morrischart/morris.min.js"></script>
    <script src="/agent/NewCss/morrisJS/morris-0.4.1.js"></script>
    <script src="/agent/NewCss/morrisJS/raphael-min.js"></script>
    <link href="/agent/NewCss/libs/morrischart/morris.css" rel="stylesheet" type="text/css" />
    <link href="/agent/NewCss/css/style-responsive.css" rel="stylesheet" />
    <script src="/agent/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <title><%=company%> | Dashboard</title>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
    </script>

    <style type="text/css">
        .morris-hover {
            position:absolute;
            z-index:1000;
        }

        .morris-hover.morris-default-style {     border-radius:10px;
            padding:6px;
            color:#666;
            background:rgba(255, 255, 255, 0.8);
            border:solid 2px rgba(230, 230, 230, 0.8);
            font-family:sans-serif;
            font-size:12px;
            text-align:center;
            z-index: inherit;
        }

        .morris-hover.morris-default-style .morris-hover-row-label {
            font-weight:bold;
            margin:0.25em 0;
        }

        .morris-hover.morris-default-style .morris-hover-point {
            white-space:nowrap;
            margin:0.1em 0;
        }

        svg { width: 100%; }

        #currencyid{
            position: absolute;
            right: 0px;
            padding: 5px
        }

        @media(max-width: 620px){
            #currencyid{
                position: initial;
            }
        }

        /*  .animate-number{
              font-size: 3vh;
          }*/

        @media(min-width:1200px){
            .widget-icon img{width: 50px; margin-top: -12px;}
            .text-box h2 img{ margin-top: 4px; width: 1.5vw;}
            .animate-number{font-size: 1.5vw; margin-left: -8px;}
        }

        @media(min-width:992px) and (max-width: 1199px) {
            .widget-icon img{width: 50px; margin-top: -12px;}
            .text-box h2 img{margin-top: -1px;}
            .animate-number{font-size: 2.5vw; margin-left: -8px;}
        }

        @media(min-width:768px) and (max-width:991px){
            .widget-icon img{width: 50px; margin-top: -12px;}
            .text-box h2 img{ margin-top: 3px; width: 2.5vw;}
            .animate-number{font-size: 2.5vw; margin-left: -8px;}
        }

        @media(min-width:480px) and (max-width:767px){
            .widget-icon img{width: 50px; margin-top: -12px;}
            .text-box h2 img{ margin-top: 4px; width: 3.5vw;}
            .animate-number{font-size: 3vw; margin-left: -8px;}
        }

        @media(max-width:479px){
            .widget-icon img{width: 50px; margin-top: -12px;}
            .text-box h2 img{margin-top: 7px; width: 4.5vw;}
            .animate-number{font-size: 4.5vw; margin-left: -8px;}
        }


    </style>


    <script>
        <%
            Functions functions = new Functions();

            BigDecimal totalSales = (BigDecimal) request.getAttribute("countsales");

            List<BigDecimal> totalHashtable = (List<BigDecimal>) request.getAttribute("totalHashtable");

            BigDecimal totalCapture = totalHashtable.get(0);
            BigDecimal totalRefund = totalHashtable.get(1);
            BigDecimal totalChargeback = totalHashtable.get(2);

            StringBuffer statusChartContent = (StringBuffer) request.getAttribute("statusChart");
            StringBuffer salesChartContent = (StringBuffer) request.getAttribute("salesChart");
            StringBuffer refundChartContent = (StringBuffer) request.getAttribute("refundChart");
            StringBuffer chargebackChartContent = (StringBuffer) request.getAttribute("chargebackChart");
            StringBuffer fraudChartContent = (StringBuffer) request.getAttribute("fraudChart");

            String statusChart = "";
            String salesChart = "";
            String refundChart = "";
            String chargebackChart = "";
            String fraudChart = "";

            if(statusChartContent!=null)
            {
                statusChart = statusChartContent.toString();
            }
            if(salesChartContent!=null)
            {
                salesChart = salesChartContent.toString();
            }
            if(refundChartContent!=null)
            {
                refundChart = refundChartContent.toString();
            }
            if(chargebackChartContent!=null)
            {
                chargebackChart = chargebackChartContent.toString();
            }
            if(fraudChartContent!=null)
            {
                fraudChart = fraudChartContent.toString();
            }

            List<String> currencyList = (List<String>) session.getAttribute("currencyList");
            //System.out.println("CurrencyList here ---------->"+currencyList);
            String currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");

        %>

    </script>
    <script>

        window.onload = function()
        {
            statusChart();

            salesChart();

            refundChart();

            chargebackChart();

            fraudChart();

        };

        $(document).ready(function() {

            $(window).resize(function() {

                window.statusChart.redraw();
                window.salesChart.redraw();
                window.refundChart.redraw();
                window.chargebackChart.redraw();
                window.fraudChart.redraw();


            });
        });
        function statusChart()
        {
            <%if(functions.isValueNull(statusChart))

            {

            %>
            window.statusChart = Morris.Donut(<%=statusChart%>);
            <%
                }
                else
                {
                    out.println("No data to display");
                }
            %>
        }
        function salesChart()
        {
            <%if(functions.isValueNull(salesChart)){%>
            window.salesChart = Morris.Bar(<%=salesChart%>);


            <%
                }
                else
                {
                    out.println("No data to display");
                }
            %>
        }

        function refundChart()
        {
            <%if(functions.isValueNull(refundChart)){%>
            window.refundChart = Morris.Bar(<%=refundChart%>);
            <%
                }
                else
                {
                    out.println("No data to display");
                }
            %>
        }

        function chargebackChart()
        {
            <%if(functions.isValueNull(chargebackChart)){%>
            window.chargebackChart = Morris.Bar(<%=chargebackChart%>);
            <%
                }
                else
                {
                out.println("No data to display");
                }
            %>
        }

        function fraudChart()
        {
            <%if(functions.isValueNull(fraudChart)){%>
            window.fraudChart = Morris.Bar(<%=fraudChart%>);
            <%
                }
                else
                {
                out.println("No data to display");
                }
            %>
        }

        function myAllCurrency(ctoken)
        {

            var data=this.document.forms["myformname"]['currency'].value;

            if(data.length === 0){
                //submit to Dashboard
                document.myformname.action="/agent/net/AgentDashboard?ctoken="+ctoken;
            } else{
                //submit to perCurrency
                document.myformname.action="/agent/net/AgentDashboardPerCurrency?ctoken="+ctoken;
            }
            document.getElementById("myformname").submit();


        }

    </script>
</head>
<body class="bodybackground">
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="row top-summary">
            <div class="col-lg-3 col-md-6">
                <div class="widget green-1 animated fadeInDown">
                    <div class="widget-content padding">
                        <div class="widget-icon">
                            <img src="/agent/images/Sales.png">
                        </div>
                        <div class="text-box">
                            <p class="maindata">TOTAL <b>SALES</b></p>
                            <h2><%--$&nbsp;--%><%--<img src="/merchant/images/<%=currencyKey.substring(0,3).toUpperCase()%>.png" style="margin-top: -5px;">--%>
                                <span class="animate-number" data-value=<%=totalSales%> data-duration="3000"></span></h2>
                            <div class="clearfix"></div>
                        </div>
                    </div>
                    <div class="widget-footer">
                        <div class="row">
                            <div class="col-sm-12">

                            </div>
                        </div>
                        <div class="clearfix"></div>
                    </div>
                </div>
            </div>

            <div class="col-lg-3 col-md-6">
                <div class="widget lightblue-1 animated fadeInDown">
                    <div class="widget-content padding">
                        <div class="widget-icon">
                            <%--
                                                        <i class="fa fa-money" aria-hidden="true"></i>
                            --%>
                            <img src="/agent/images/Settled.png">

                        </div>
                        <div class="text-box">
                            <p class="maindata">TOTAL <b>CAPTURE</b></p>
                            <h2><%--$&nbsp;--%><%--<img src="/merchant/images/<%=currencyKey.substring(0,3).toUpperCase()%>.png" style="margin-top: -5px;">--%>
                                <span class="animate-number" data-value=<%=totalCapture%> data-duration="3000">0</span></h2>
                            <div class="clearfix"></div>
                        </div>
                    </div>
                    <div class="widget-footer">
                        <div class="row">
                            <div class="col-sm-12">
                            </div>
                        </div>
                        <div class="clearfix"></div>
                    </div>
                </div>
            </div>

            <div class="col-lg-3 col-md-6">
                <div class="widget darkblue-2 animated fadeInDown">
                    <div class="widget-content padding">
                        <div class="widget-icon">
                            <%--<i class="fa fa-undo" aria-hidden="true"></i>--%>
                            <img src="/agent/images/refund_resized1.png">
                        </div>
                        <div class="text-box">
                            <p class="maindata">TOTAL <b>REFUND</b></p>
                            <h2><%--$&nbsp;--%><%--<img src="/merchant/images/<%=currencyKey.substring(0,3).toUpperCase()%>.png" style="margin-top: -5px;">--%>
                                <span class="animate-number" data-value=<%=totalRefund%> data-duration="3000">0</span></h2>

                            <div class="clearfix"></div>
                        </div>
                    </div>
                    <div class="widget-footer">
                        <div class="row">
                            <div class="col-sm-12">

                            </div>
                        </div>
                        <div class="clearfix"></div>
                    </div>
                </div>
            </div>

            <div class="col-lg-3 col-md-6">
                <div class="widget orange-4 animated fadeInDown">
                    <div class="widget-content padding">
                        <div class="widget-icon">
                            <img src="/agent/images/Chargeback1.png">
                        </div>
                        <div class="text-box">
                            <p class="maindata">TOTAL <b>CHARGEBACK</b></p>
                            <h2><%--$&nbsp;--%><%--<img src="/merchant/images/<%=currencyKey.substring(0,3).toUpperCase()%>.png" style="margin-top: -5px;">--%>
                                <span class="animate-number" data-value=<%=totalChargeback%> data-duration="3000">0</span></h2>
                            <div class="clearfix"></div>
                        </div>
                    </div>
                    <div class="widget-footer">
                        <div class="row">
                            <div class="col-sm-12">
                            </div>
                        </div>
                        <div class="clearfix"></div>
                    </div>
                </div>
            </div>

        </div>

        <div class="row">
            <div class="col-md-6 portlets ui-sortable">
                <div class="widget">
                    <div class="col-xs-8 col-sm-4 col-md-4" id="currencyid">
                        <form name="myformname" id="myformname" action="" method="POST">
                            <div class="input-group">
                                <%--<input type="text" class="form-control">--%>

                                <select name="currency" onchange="myAllCurrency('<%=ctoken%>')"  style="border-radius: 5px;" class="btn btn-default btn-xs dropdown-toggle form-control">
                                    <option  value="">All Currency</option>

                                    <%
                                        if(currencyList.size()>0)
                                        {
                                            Iterator currencyIterator = currencyList.iterator();
                                            String singleCurrency = "";
                                            while (currencyIterator.hasNext())
                                            {
                                                singleCurrency = (String) currencyIterator.next();
                                                String select = "";

                                                if(currency.equalsIgnoreCase(singleCurrency))
                                                {
                                                    select = "selected";
                                                }
                                    %>
                                    <option value="<%=singleCurrency%>" <%=select%>><%=singleCurrency%></option>
                                    <%
                                            }
                                        }
                                    %>

                                </select>
                                <%--<span class="input-group-btn">
                                  <button type="submit" class="btn btn-default">Go!</button>
                                </span>--%>

                            </div><!-- /input-group -->
                        </form>
                    </div>
                    <div class="widget-header transparent">
                        <h2><strong>Sales</strong> Chart</h2>
                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding">
                        <div id="sales" >

                        </div>
                    </div>
                </div>
            </div>

            <div class="col-md-6 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong>Refund</strong> Chart</h2>
                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding">
                        <div id="refund" >

                        </div>
                    </div>
                </div>
            </div>

        </div>

        <div class="row">
            <div class="col-md-6 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong>Chargeback</strong> Chart</h2>
                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding">
                        <div id="chargeback" >

                        </div>
                    </div>
                </div>
            </div>

            <div class="col-md-6 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong>Status</strong> Chart</h2>
                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding">
                        <div id="donut-example" >

                        </div>
                    </div>
                </div>
            </div>

            <%--<div class="col-md-4 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong>Fraud</strong> Chart</h2>
                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding">
                        <div id="fraud" >

                        </div>
                    </div>
                </div>
            </div>--%>
        </div>

    </div>
</div>

</body>
</html>