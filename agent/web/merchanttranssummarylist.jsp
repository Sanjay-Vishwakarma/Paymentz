<%@ page import="com.directi.pg.Functions" %>
<%@ page import="net.agent.TransReport" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.vo.ChartVolumeVO" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 3/4/14
  Time: 6:18 PM
  To change this template use File | Settings | File Templates.
  asdasdasd
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<%
    Functions functions = new Functions();
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("agentname"));
    session.setAttribute("submit","Member Wise Report");
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String agentId = (String)session.getAttribute("merchantid");
        Hashtable memberidDetails=agent.getAgentMemberDetailList(agentId);
        String memberid=nullToStr((String)request.getAttribute("toid"));
        //String agentid=(String)session.getAttribute("merchantid");
        String str="";
        String fdate=null;
        String tdate=null;
        /*String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;*/
        try
        {
            fdate = ESAPI.validator().getValidInput("fromdate",request.getParameter("fromdate"),"Days",2,true);
            tdate = ESAPI.validator().getValidInput("todate",request.getParameter("todate"),"Days",2,true);
            /*fmonth = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
            tmonth =  ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
            fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
            tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);*/
        }
        catch(ValidationException e)
        {
        }

        Date date = new Date();
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

        String Date = originalFormat.format(date);
        date.setDate(1);
        String fromDates = originalFormat.format(date);

        fdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDates : request.getParameter("fromdate");
        tdate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");

        Calendar rightNow = Calendar.getInstance();
        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
        /*if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
        if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);*/

        String currentyear = ""+rightNow.get(rightNow.YEAR);
        String charttoken = Functions.getFormattedDate("yyMMddHHmmss");

        //PerCurrency
        List<String> currencyList = agent.perCurrency(agentId);
        //HashMap<String,String> currencyList = partnerFunctions.perCurrency(partnerName);
        //System.out.println("CurrencyList in merchanttranssummarylist---->"+currencyList);

        String currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");
%>
<script>

    $(function() {

        $( ".datepicker" ).datepicker({startDate:'-1y'});
    });
</script>
<html>
<head>
    <title><%=company%> | Merchant Transaction Report</title>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <script src="/agent/NewCss/libs/morrischart/morris.min.js"></script>
    <script src="/agent/NewCss/morrisJS/morris-0.4.1.min.js"></script>
    <script src="/agent/NewCss/morrisJS/raphael-min.js"></script>
    <link href="/agent/NewCss/libs/morrischart/morris.css" rel="stylesheet" type="text/css" />
    <link href="/agent/NewCss/css/style-responsive.css" rel="stylesheet" />
    <link href="/agent/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/agent/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script src="/agent/NewCss/libs/jquery-icheck/icheck.min.js"></script>

    <%--    <script type="text/javascript">

            $('#sandbox-container input').datepicker({
            });
        </script>--%>


    <style type="text/css">
        .morris-hover.morris-default-style {
            border-radius: 10px;
            padding: 6px;
            color: #666;
            background: rgba(255, 255, 255, 0.8);
            border: solid 2px rgba(230, 230, 230, 0.8);
            font-family: sans-serif;
            font-size: 12px;
            text-align: center;
            z-index: inherit;
        }
    </style>

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
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%>'s Transaction Report</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <form action="/agent/net/MerchantTransSummaryList?ctoken=<%=ctoken%>" method="post" name="forms" >

                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                    <%
                                        String currencyError = (String) request.getAttribute("currencyError");
                                        if(currencyError!=null)
                                        {
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + currencyError + "</h5>");
                                        }
                                        else
                                        {
                                            currencyError="";
                                        }
                                        String errormsg = (String) request.getAttribute("catchError");
                                        if(errormsg!=null)
                                        {
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");
                                        }
                                        else
                                        {
                                            errormsg="";
                                        }
                                    %>

                                    <div class="form-group col-md-3">
                                        <label>From</label>
                                        <input type="text" name="fromdate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">

                                    </div>

                                    <div class="form-group col-md-3">
                                        <label>To</label>
                                        <input type="text" name="todate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">

                                    </div>

                                    <div class="form-group col-md-3">
                                        <label>Merchant ID</label>

                                        <select size="1" name="toid" class="form-control">
                                            <option value="all">Select Merchant ID</option>
                                            <%
                                                String selected3 = "";
                                                String key3 = "";
                                                String value3 = "";
                                                TreeMap treeMap = new TreeMap(memberidDetails);
                                                Iterator itr = treeMap.keySet().iterator();
                                                while (itr.hasNext())
                                                {
                                                    key3 = (String) itr.next();
                                                    value3 = treeMap.get(key3).toString();
                                                    if (key3.equals(memberid))
                                                        selected3 = "selected";
                                                    else
                                                        selected3 = "";
                                            %>
                                            <option value="<%=key3%>" <%=selected3%>><%=key3%>---<%=value3%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-3">
                                        <label>Currency*</label>

                                        <select size="1" name="currency" class="form-control">
                                            <option value="">Select Currency</option>
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
                                                            select = "selected";
                                            %>
                                            <option value="<%=singleCurrency%>" <%=select%>><%=singleCurrency%></option>
                                            <%
                                                    }
                                                }
                                            %>

                                        </select>
                                    </div>

                                    <div class="form-group col-md-9">
                                    </div>

                                    <div class="form-group col-md-3">
                                        <label>&nbsp;</label>
                                        <button type="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-clock-o"></i>&nbsp;&nbsp;Search</button>

                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <br>


            <div class="row reporttable">

                <%
                    /*String errormsg = (String) request.getAttribute("error");
                    if(errormsg!=null)
                    {
                        out.println("<font face=\"arial\" color=\"red\"  size=\"2\">");
                        out.println(errormsg);
                        out.println("</font>");
                    }
                    else
                    {
                        errormsg="";
                    }*/
                    Hashtable status_data=(Hashtable)request.getAttribute("status_report");

                    String style="class=\"tr00\"";
                    int records = 0;
                    int totalrecords = 0;

                    Hashtable temphash=new Hashtable();
                    String fdtstamp=(String)request.getAttribute("fdtstamp");
                    String tdtstamp=(String)request.getAttribute("tdtstamp");

                    try
                    {
                        records = Integer.parseInt((String) status_data.get("records"));
                        totalrecords = Integer.parseInt((String) status_data.get("totalrecords"));
                    }
                    catch (Exception ex)
                    {
                        //Initially records are empty...
                    }
                    if (records > 0 && status_data!=null)
                    {
                %>



                <%
                    TransReport transReport = new TransReport();
                    ChartVolumeVO chartVolumeVO = transReport.prepareAllChartsData(memberid, agentId, fdtstamp, tdtstamp,currency);
                %>

                <script>

                        window.onload = function()
                        {
                            salesChart();

                            refundChart();

                            chargebackChart();
                        };

                        $(document).ready(function() {

                            $(window).resize(function() {

                                window.salesChart.redraw();
                                window.refundChart.redraw();
                                window.chargebackChart.redraw();


                            });
                        });
                        function salesChart()
                        {
                            <%if(functions.isValueNull(chartVolumeVO.getSalesChart()))

                            {
                              // System.out.println("Chart token in jsp------>"+chartVolumeVO.getSalesChart());
                            %>
                            window.salesChart = Morris.Bar(<%=chartVolumeVO.getSalesChart()%>);
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
                            <%if(functions.isValueNull(chartVolumeVO.getRefundChart())){%>
                            window.refundChart = Morris.Bar(<%=chartVolumeVO.getRefundChart()%>);
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
                            <%if(functions.isValueNull(chartVolumeVO.getChargebackChart())){%>
                            window.chargebackChart = Morris.Bar(<%=chartVolumeVO.getChargebackChart()%>);
                            <%
                                }
                                else
                                {
                                out.println("No data to display");
                                }
                            %>
                        }

                    </script>

                <div class="col-md-6 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong>Sales</strong> Chart</h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding">
                            <div id="salesChart" >

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
                            <div id="refundChart">

                            </div>
                        </div>
                    </div>
                </div>

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
                            <div id="chargebackChart" >

                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-sm-6 portlets ui-sortable">

                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Merchant Transaction Report</strong></h2>
                        </div>
                        <div class="ibox-content">
                            <div class="flot-chart">
                                <table class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    <tr>
                                        <td  class="mtransreport" colspan="1" align="center">Merchant Id: <%=memberid%></td>
                                        <td  class="mtransreport" colspan="2" align="center">FromDate: <%=fdate%>&nbsp;&nbsp;ToDate: <%=tdate%></td>
                                    </tr>
                                    </thead>
                                    <thead>
                                    <tr style="background-color: #7eccad !important;color: white;">
                                        <th style="text-align: center">Status</th>
                                        <th style="text-align: center">Transaction</th>
                                        <th style="text-align: center">Total Amount</th>
                                    </tr>
                                    </thead>
                                    <tbody>


                                    <%  int tempcount=0;
                                        String tempamt=null;
                                        for (int pos = 1; pos < records; pos++)
                                        {
                                            String id = Integer.toString(pos);

                                            temphash = (Hashtable) status_data.get(id);

                                            String status=(String)temphash.get("status");
                                            String count=(String)temphash.get("count");
                                            String amount=(String)temphash.get("amount");

                                    %>
                                    <tr>
                                        <td data-label="Status" style="text-align: center"><%=status%> </td>
                                        <td data-label="Transaction" style="text-align: center"><%=count%> </td>
                                        <td data-label="Total Amount" style="text-align: center"><%=amount%> </td>
                                    </tr>
                                    <%      //tempcount = tempcount+Integer.parseInt(count);

                                    }
                                        out.println("<tr "+style+">");
                                        out.println("<td colspan=\"3\" align=\"center\" style=\"background-color: #7eccad !important;color: white;\">TOTAL Transaction:<font color=\"#FFFFF\"> "+totalrecords+"</font> </td>");
                                        out.println("</tr >");
                                        out.println("<tr "+style+">");
                                        out.println("<td colspan=\"3\" align=\"center\" style=\"background-color: #7eccad !important;color: white;\">GRAND Total Amount:<font color=\"#FFFFF\"> "+status_data.get("grandtotal")+"</font> </td>");
                                        out.println("</tr >");
                                    %>
                                    </tbody>

                                </table>


                            </div>
                        </div>
                    </div>
                </div>


            </div>


            <%     }
            else
            {
                out.println(Functions.NewShowConfirmation1("Sorry","No Record Found"));
            }
            }
            else
            {
                response.sendRedirect("/agent/logout.jsp");
                return;
            }
            %>

        </div>
    </div>
</div>

</body>
</html>
<%!
    public static String nullToStr(String str)
    {
        if(str == null)
            return "";
        return str;
    }
%>
