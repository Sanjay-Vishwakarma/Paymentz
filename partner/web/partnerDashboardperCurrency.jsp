<%@ page errorPage=""
         import="com.directi.pg.Functions,com.logicboxes.util.ApplicationProperties,java.math.BigDecimal" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.manager.vo.chartVOs.donutchart.DonutChartVO" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.util.*" %>
<%--<%@ include file="ietest.jsp" %>--%>
<%
    Functions functions = new Functions();
%>
<%
    String company =(String)session.getAttribute("partnername");
    Logger log = new Logger("partnerDashboardperCurrency.jsp");
    session.setAttribute("submit","PartnerDashboard");

%>
<%@ include file="top.jsp" %>
<html lang="en">
<head>
    <%--<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <meta name="apple-mobile-web-app-capable" content="yes" />
    <meta name="description" content="">
    <meta name="keywords" content="coco bootstrap template, coco admin, bootstrap,admin template, bootstrap admin,">
    <meta name="author" content="Huban Creative">
  --%>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>

    <%--<link href="/merchant/plugins/xcharts/xcharts.min.css" rel="stylesheet">
    &lt;%&ndash;
        <script src="/merchant/js/devoops.js"></script>
    &ndash;%&gt;
    <link href="/merchant/plugins/xcharts/xcharts.min.css" rel="stylesheet">--%>

    <title><%=company%> Partner Dashboard</title>

    <%--<script language="JavaScript" src="/merchant/FusionCharts/FusionCharts.js?ver=1"></script>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
    </script>
    <link rel="stylesheet" type="text/css" href="/merchant/FusionCharts/style.css" >--%>

    <style type="text/css">
        svg:not(:root) {
            overflow: inherit!important;
        }
    </style>

</head>

<script src="/partner/NewCss/libs/jquery-animate-numbers/jquery.animateNumbers.js"></script>
<link href="/partner/NewCss/libs/ios7-switch/ios7-switch.css" rel="stylesheet" />
<link href="/partner/NewCss/libs/pace/pace.css" rel="stylesheet" />
<link href="/partner/NewCss/css/style.css" rel="stylesheet" type="text/css" />
<link href="/partner/NewCss/css/style-responsive.css" rel="stylesheet" />
<script src="/partner/NewCss/libs/morrischart/morris.min.js"></script>
<script src="/partner/NewCss/morrisJS/raphael-min.js"></script>
<script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>

<body>


<%
    /*DonutChartVO donutChartVO=DonutChartVO.*/
%>
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
    @media (min-width: 1200px) {
        .col-lg-3 {
            width: 20%;
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
<script type="application/javascript">
    function myAllCurrency(ctoken)
    {

        var data=this.document.forms["myformname"]['currency'].value;

        if(data.length === 0){
            //submit to Dashboard
            document.myformname.action="/partner/net/PartnerDashboard?ctoken="+ctoken;
        } else{
            //submit to perCurrency
            document.myformname.action="/partner/net/PartnerDashboardperCurrency?ctoken="+ctoken;
        }
        document.getElementById("myformname").submit();


    }
</script>
<script>

    <%
        BigDecimal totalSales = (BigDecimal) request.getAttribute("countsales");

        List<BigDecimal> totalHashtable = (List<BigDecimal>) request.getAttribute("totalHashtable");

        List<BigDecimal> totalAuthSuccessfulHashtable = (List<BigDecimal>) request.getAttribute("totalAuthSuccessfulHashtable");

          BigDecimal totalAuthSuccessful = totalAuthSuccessfulHashtable.get(0);

        BigDecimal totalCapture = totalHashtable.get(0);
        BigDecimal totalRefund = totalHashtable.get(1);
        BigDecimal totalChargeback = totalHashtable.get(2);

        StringBuilder statusChartContent = (StringBuilder) request.getAttribute("statusChart");
        StringBuilder currencyChartContent = (StringBuilder) request.getAttribute("saleschart_donuts");
        String morris = (String)request.getAttribute("morrisDemo");

        String statusChartString = "";
        String salesChartString = "";
        String currencyChartString = "";

        if(statusChartContent!=null)
        {
            statusChartString=statusChartContent.toString();
        }

        if(currencyChartContent!=null)
        {
            currencyChartString=currencyChartContent.toString();
        }

        log.debug("---salesdata jsp---"+salesChartString);
        log.debug("---statusChartString jsp---"+statusChartString);
        log.debug("---currencyChartString jsp---"+currencyChartString);

        int authstarted = 0;
        int authsuccessful = 0;
        int markedforreversal = 0;
        int reversed = 0;
        int chargeback = 0;
        int settled = 0;
        int capturesuccess =0;
        int failed =0;
        int authfailed =0;

        int total = (int) request.getAttribute("total");

        Hashtable statusHash = (Hashtable) request.getAttribute("statusHash");
        Map.Entry compare = null;

        Set set = statusHash.entrySet();
        Iterator it = set.iterator();

        while(it.hasNext())
        {
            compare = (Map.Entry)it.next();

            if("authstarted".equals(compare.getKey()))
            {
                authstarted = (int) statusHash.get("authstarted");
            }

            if("authsuccessful".equals(compare.getKey()))
            {
                authsuccessful = (int) statusHash.get("authsuccessful");
            }

            if("markedforreversal".equals(compare.getKey()))
            {
                markedforreversal = (int) statusHash.get("markedforreversal");
            }

            if("reversed".equals(compare.getKey()))
            {
                reversed = (int) statusHash.get("reversed");
            }

            if("chargeback".equals(compare.getKey()))
            {
                chargeback = (int) statusHash.get("chargeback");
            }

            if("settled".equals(compare.getKey()))
            {
                settled = (int) statusHash.get("settled");
            }

            if("capturesuccess".equals(compare.getKey()))
            {
                capturesuccess = (int) statusHash.get("capturesuccess");
            }

            if("failed".equals(compare.getKey()))
            {
                failed = (int) statusHash.get("failed");
            }

            if("authfailed".equals(compare.getKey()))
            {
                authfailed = (int) statusHash.get("authfailed");
            }
        }

        int authstartedPercentage;
        int authsuccessfulPercentage;
        int markedforreversalPercentage;
        int countchargebackPercentage;
        int countrefundPercentage;
        int countsettledPercentage;
        int countcapturePercentage;
        int countfailedPercentage;
        int countauthfailedPercentage;

        if(total>0)
        {
            authstartedPercentage = (int) ((authstarted*100)/total);
            authsuccessfulPercentage = (int) ((authsuccessful*100)/total);
            markedforreversalPercentage = (int) ((markedforreversal*100)/total);
            countchargebackPercentage = (int) ((chargeback*100)/total);
            countrefundPercentage = (int) ((reversed*100)/total);
            countsettledPercentage = (int) ((settled*100)/total);
            countcapturePercentage = (int) ((capturesuccess*100)/total);
            countfailedPercentage = (int) ((failed*100)/total);
            countauthfailedPercentage = (int) ((authfailed*100)/total);
        }
        else
        {
            authstartedPercentage = 0;
            authsuccessfulPercentage = 0;
            markedforreversalPercentage = 0;
            countchargebackPercentage = 0;
            countrefundPercentage = 0;
            countsettledPercentage = 0;
            countcapturePercentage = 0;
            countfailedPercentage = 0;
            countfailedPercentage = 0;
            countauthfailedPercentage = 0;
        }

        TreeSet<String> currencyList = (TreeSet<String>) session.getAttribute("currencyList");
        String currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");

        //System.out.println("currencyDPC--"+currency);

    %>

    window.onload = function()
    {
        currencyDonut();

        donutChart();

        barChart();
    };

</script>
<script>
    $(document).ready(function() {

        $(window).resize(function() {

            window.donutChart.redraw();
            window.currencyDonut.redraw();
            window.barChart.redraw();
        });
    });
    function donutChart()
    {
        <%if(functions.isValueNull(statusChartString)){%>
        window.donutChart = Morris.Donut(<%=statusChartString%>);
        <%
            }
            else
            {
                out.println("No data to display");
            }
        %>
    }

    function barChart()
    {
        <%if(functions.isValueNull(morris)){%>
        window.barChart = Morris.Bar(<%=morris%>);
        <%
            }
            else
            {
                out.println("No data to display");
            }
        %>
    }

    function currencyDonut()
    {
        <%if(functions.isValueNull(currencyChartString)){%>
        window.currencyDonut = Morris.Donut(<%=currencyChartString%>);
        <%
            }
            else
            {
                out.println("No data to display");
            }

        %>
    }


    function allChange(ctoken)
    {
        alert("Inside my change");
        document.getElementById("myform1").submit();
        document.myform1.action="/partner/servlet/DashBoard?ctoken="+ctoken;
    }

</script>


<div class="content-page">
    <!-- ============================================================== -->
    <!-- Start Content here -->
    <!-- ============================================================== -->
    <div class="content">
        <!-- Start info box -->
        <div class="row top-summary">
            <div class="col-lg-3 col-md-6">
                <div class="widget lightblue-1 animated fadeInDown">
                    <div class="widget-content padding">
                        <div class="widget-icon">
                            <img src="/partner/images/Sales.png">
                        </div>
                        <div class="text-box">
                            <p class="maindata">TOTAL <b>SALES</b></p>
                            <h2><img src="/partner/images/<%=currency.substring(0,3).toUpperCase()%>.png">
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
                <div class="widget green-1 animated fadeInDown">
                    <div class="widget-content padding">
                        <div class="widget-icon">
                            <%--
                                                        <i class="fa fa-money" aria-hidden="true"></i>
                            --%>
                            <img src="/partner/images/Settled.png">

                        </div>
                        <div class="text-box">
                            <p class="maindata">TOTAL <b>CAPTURE</b></p>
                            <h2><img src="/partner/images/<%=currency.substring(0,3).toUpperCase()%>.png">
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
                <div class="widget orange-4 animated fadeInDown">
                    <div class="widget-content padding">
                        <div class="widget-icon">
                            <%--<i class="fa fa-undo" aria-hidden="true"></i>--%>
                            <img src="/partner/images/refund_resized1.png">
                        </div>
                        <div class="text-box">
                            <p class="maindata">TOTAL <b>REFUND</b></p>
                            <h2><img src="/partner/images/<%=currency.substring(0,3).toUpperCase()%>.png">
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
                <div class="widget darkblue-2 animated fadeInDown">
                    <div class="widget-content padding">
                        <div class="widget-icon">
                            <img src="/partner/images/Chargeback1.png">
                        </div>
                        <div class="text-box">
                            <p class="maindata">TOTAL <b>CHARGEBACK</b></p>
                            <h2><img src="/partner/images/<%=currency.substring(0,3).toUpperCase()%>.png">
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

            <div class="col-lg-3 col-md-6">
                <div class="widget darkblue-2 animated fadeInDown" style="background-color:#1e8b92 !important">
                    <div class="widget-content padding">
                        <div class="widget-icon">
                            <img src="/partner/images/auth_success.png">
                        </div>
                        <div class="text-box">
                            <p class="maindata">AUTH <b>SUCCESSFUL </b></p>
                            <h2><%--$&nbsp;--%><%--<img src="/partner/images/<%=currencyKey.substring(0,3).toUpperCase()%>.png" style="margin-top: -5px;">--%>
                                <span class="animate-number" data-value=<%=totalAuthSuccessful%> data-duration="3000">0</span></h2>
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
        <!-- End of info box -->
        <div class="row">


            <div class="col-lg-8 portlets ui-sortable">
                <div class="widget">
                    <%--<div class="col-xs-8 col-sm-4 col-md-4" id="currencyid">
                      <form name="myformname" id="myformname" action="" method="POST">
                        <div class="input-group">

                          <select name="currency" onchange="myAllCurrency('<%=ctoken%>')" class="form-control">
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
                            else
                            {
                            %>
                            <option value="noCurrency">None</option>
                            <%
                              }

                            %>
                          </select>
                          &lt;%&ndash;<span class="input-group-btn">
                            <button type="submit" class="btn btn-default">Go!</button>
                          </span>
          &ndash;%&gt;
                        </div><!-- /input-group -->




                      </form>
                    </div>--%>



                    <div class="widget-header transparent">
                        <h2><strong>Sales</strong> Chart</h2>
                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>

                    <form name="myformname" id="myformname" action="" method="POST">
                        <div class="pull-right" style="margin-right: 20px;">
                            <div class="btn-group">

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
                                    else
                                    {
                                    %>
                                    <option value="noCurrency">None</option>
                                    <%
                                        }

                                    %>
                                </select>
                                <%--<span class="input-group-btn">
                                  <button type="submit" class="btn btn-default">Go!</button>
                                </span>
                --%>            </div>
                        </div><!-- /input-group -->




                    </form>


                    <div class="widget-content padding">
                        <div id="bar-example" >

                        </div>
                    </div>
                </div>
            </div>


            <div class="col-lg-4 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong>Sales Per Currency</strong> Chart</h2>
                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding">
                        <div id="graph" style="font-size: 10px" >

                        </div>
                    </div>
                </div>
            </div>


        </div>

        <div class="row">
            <div class="col-md-8 portlets ui-sortable">
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
                        <div id="donut-chart" style="font-size: 10px" ></div>
                    </div>
                </div>
            </div>
            <div class="col-md-4 portlets ui-sortable">
                <div class="widget darkblue-3">
                    <div class="widget-header transparent">
                        <h2><i class="icon-chart-pie-1"></i> <strong>Progress</strong> Status</h2>
                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a class="hidden" id="dropdownMenu1" data-toggle="dropdown">
                                <i class="fa fa-cogs"></i>
                            </a>
                            <ul class="dropdown-menu pull-right" role="menu" aria-labelledby="dropdownMenu1">
                                <li><a href="#">Action</a></li>
                                <li><a href="#">Another action</a></li>
                                <li><a href="#">Something else here</a></li>
                                <li class="divider"></li>
                                <li><a href="#">Separated link</a></li>
                            </ul>
                            <a href="#" class="widget-popout hidden tt" title="Pop Out/In"><i class="icon-publish"></i></a>
                            <a href="#" class="widget-maximize hidden"><i class="icon-resize-full-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content">
                        <div id="website-statistic2" class="statistic-chart">
                            <div class="col-sm-12 stacked">
                                <div class="col-sm-12 status-data">

                                    <%--Start Test Data--%>

                                    <span class="pull-right" style="color: #ffffff;font-weight: bold">Authstarted</span><br>
                                    <div id="bar1" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip">Authstarted</span>
                                        </div>

                                        <span class="fill" data-percentage=<%=authstartedPercentage%>></span>
                                    </div>

                                    <span class="pull-right" style="color: #ffffff;font-weight: bold">Authsuccessful</span><br>
                                    <div id="bar2" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip"></span>
                                        </div>
                                        <span class="fill" data-percentage=<%=authsuccessfulPercentage%>></span>
                                    </div>

                                    <span class="pull-right" style="color: #ffffff; font-weight: bold">Capture Success</span><br>
                                    <div id="bar3" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip"></span>
                                        </div>
                                        <span class="fill" data-percentage=<%=countcapturePercentage%>></span>
                                    </div>

                                    <span class="pull-right" style="color: #ffffff;font-weight: bold">Chargeback</span><br>
                                    <div id="bar4" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip"></span>
                                        </div>
                                        <span class="fill" data-percentage=<%=countchargebackPercentage%>></span>
                                    </div>

                                    <span class="pull-right" style="color: #ffffff;font-weight: bold">Marked for Reversal</span><br>
                                    <div id="bar5" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip"></span>
                                        </div>
                                        <span class="fill" data-percentage=<%=markedforreversalPercentage%>></span>
                                    </div>

                                    <span class="pull-right" style="color: #ffffff;font-weight: bold">Reversed</span><br>
                                    <div id="bar6" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip"></span>
                                        </div>
                                        <span class="fill" data-percentage=<%=countrefundPercentage%>></span>
                                    </div>

                                    <span class="pull-right" style="color: #ffffff;font-weight: bold">Settled</span><br>
                                    <div id="bar7" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip"></span>
                                        </div>
                                        <span class="fill" data-percentage=<%=countsettledPercentage%>></span>
                                    </div>

                                    <span class="pull-right" style="color: #ffffff;font-weight: bold">Failed</span><br>
                                    <div id="bar8" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip"></span>
                                        </div>
                                        <span class="fill" data-percentage=<%=countfailedPercentage%>></span>
                                    </div>

                                    <span class="pull-right" style="color: #ffffff;font-weight: bold">AuthFailed</span><br>
                                    <div id="bar9" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip"></span>
                                        </div>
                                        <span class="fill" data-percentage=<%=countauthfailedPercentage%>></span>
                                    </div>
                                </div>
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
<script src="/partner/NewCss/js/jquery.barfiller.js" type="text/javascript"></script>
<link href="/partner/NewCss/css/style_BarFiller.css" rel="stylesheet" />

<script type="text/javascript">

    $(document).ready(function(){

        $('#bar1').barfiller({ barColor: '#68c39f', duration: 3000 });
        $('#bar2').barfiller({ barColor: '#900', duration: 3000 });
        $('#bar3').barfiller({ barColor: '#edce8c',duration: 3000 });
        $('#bar4').barfiller({ barColor: '#Abb7b7', duration: 3000 });
        $('#bar5').barfiller({ barColor: '#68c39f', duration: 3000 });
        $('#bar6').barfiller({ barColor: '#900', duration: 3000 });
        $('#bar7').barfiller({ barColor: '#edce8c',duration: 3000 });
        $('#bar8').barfiller({ barColor: '#Abb7b7', duration: 3000 });
        $('#bar9').barfiller({ barColor: '#Abb7b7', duration: 3000 });
        $('#bar10').barfiller({ barColor: '#Abb7b7', duration: 3000 });
        $('#bar11').barfiller({ barColor: '#Abb7b7', duration: 3000 });
        $('#bar12').barfiller({ barColor: '#Abb7b7', duration: 3000 });
        $('#bar13').barfiller({ barColor: '#Abb7b7', duration: 3000 });
        $('#bar14').barfiller({ barColor: '#Abb7b7', duration: 3000 });
        $('#bar15').barfiller({ barColor: '#Abb7b7', duration: 3000 });

    });

</script>