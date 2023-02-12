<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.*" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.manager.vo.payoutVOs.WireReportsVO" %>
<%@ page import="com.manager.vo.payoutVOs.WireVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.directi.pg.*" %>
<%@ page import="com.payment.exceptionHandler.PZExceptionHandler" %>
<%@ page import="com.payment.exceptionHandler.operations.PZOperations" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 8/11/14
  Time: 3:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    session.setAttribute("submit","Reports");
    response.setHeader("X-Frame-Options", "ALLOWALL");
    session.setAttribute("X-Frame-Options", "ALLOWALL");
%>

<html lan="en">
<head>
    <title><%=company%> Merchant Account Details >Reports</title>
    <%--    <style type="text/css">

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

        </style>--%>

</head>
<%@ include file="Top.jsp" %>
<link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
<script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
<script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>
<%--<script>
    $(document).ready(function(){
        $('#myTable').dataTable();
    });
</script>--%>
<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>

<script type="text/javascript">
    $('#sandbox-container input').datepicker({
    });
</script>
<script>
    $(function() {
        $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
        /* $(".datepicker").datepicker({}).datepicker("setValue",new Date);*/
    });
</script>
<script>
    function download()
    {
        var answer = confirm("Do you want to download file?");
        if(answer == true)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
</script>
<body class="pace-done widescreen fixed-left">

<%!
    private static Logger logger = new Logger("reports.jsp");
    private Functions function = new Functions();
    private TerminalManager terminalManager = new TerminalManager();
%>
<%
    int pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
    int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

    session.setAttribute("submit","Reports");

    String uId = "";
    if(session.getAttribute("role").equals("submerchant"))
    {
        uId = (String) session.getAttribute("userid");
    }
    else
    {
        uId = (String) session.getAttribute("merchantid");
    }
    try
    {
        List<TerminalVO> terminalVO = terminalManager.getTerminalsByMerchantId(uId, String.valueOf(session.getAttribute("role")));
       // List<TerminalVO> terminalVO = terminalManager.getTerminalsByMerchantId(session.getAttribute("merchantid").toString());
        String fromdate = null;
        String todate = null;
        String terminalid = null;

        Date date = new Date();
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

        String Date = originalFormat.format(date);
        date.setDate(1);
        String fromDate = originalFormat.format(date);
        String firstdate="";
        String lastdate="";
        String netfinalamount="";
        String unpaidamount="";
        String currency="";
        String timestamp="";
        firstdate=Functions.checkStringNull(request.getParameter("firstdate"))==null ? firstdate : request.getParameter("firstdate");
        lastdate=Functions.checkStringNull(request.getParameter("lastdate"))==null ? firstdate : request.getParameter("lastdate");
        netfinalamount=Functions.checkStringNull(request.getParameter("netfinalamount"))==null ? netfinalamount : request.getParameter("netfinalamount");
        fromdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDate : request.getParameter("fromdate");
        todate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");
        terminalid =Functions.checkStringNull(request.getParameter("terminalid"))==null?"":request.getParameter("terminalid");
        unpaidamount =Functions.checkStringNull(request.getParameter("unpaidamount"))==null?"":request.getParameter("unpaidamount");
        currency =Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");
        timestamp =Functions.checkStringNull(request.getParameter("timestamp"))==null?"":request.getParameter("timestamp");
        ResourceBundle rb1 = null;
        String language_property1 = (String)session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);

        String reports_reports = !function.isEmptyOrNull(rb1.getString("reports_reports"))?rb1.getString("reports_reports"): "Reports";
        String reports_from_date = !function.isEmptyOrNull(rb1.getString("reports_from_date"))?rb1.getString("reports_from_date"): "From";
        String reports_to_date = !function.isEmptyOrNull(rb1.getString("reports_to_date"))?rb1.getString("reports_to_date"): "To";
        String reports_terminal_id = !function.isEmptyOrNull(rb1.getString("reports_terminal_id"))?rb1.getString("reports_terminal_id"): "Terminal ID*";
        String reports_Select_Terminal_ID = !function.isEmptyOrNull(rb1.getString("reports_Select_Terminal_ID"))?rb1.getString("reports_Select_Terminal_ID"): "Select Terminal ID";
        String reports_paid = !function.isEmptyOrNull(rb1.getString("reports_paid"))?rb1.getString("reports_paid"): "is Paid";
        String reports_all = !function.isEmptyOrNull(rb1.getString("reports_all"))?rb1.getString("reports_all"): "All";
        String reports_paid1 = !function.isEmptyOrNull(rb1.getString("reports_paid1"))?rb1.getString("reports_paid1"): "Paid";
        String reports_unpaid1 = !function.isEmptyOrNull(rb1.getString("reports_unpaid1"))?rb1.getString("reports_unpaid1"): "Unpaid";
        String reports_search = !function.isEmptyOrNull(rb1.getString("reports_search"))?rb1.getString("reports_search"): "Search";
        String reports_report_table = !function.isEmptyOrNull(rb1.getString("reports_report_table"))?rb1.getString("reports_report_table"): "Report Table";
        String reports_no_records = !function.isEmptyOrNull(rb1.getString("reports_no_records"))?rb1.getString("reports_no_records"): "No records found for given search criteria.";
        String reports_search1 = !function.isEmptyOrNull(rb1.getString("reports_search1"))?rb1.getString("reports_search1"): "Search";
        String reports_SrNo = !function.isEmptyOrNull(rb1.getString("reports_SrNo"))?rb1.getString("reports_SrNo"): "Sr No";
        String reports_TerminalID = !function.isEmptyOrNull(rb1.getString("reports_TerminalID"))?rb1.getString("reports_TerminalID"): "Terminal ID";
        String reports_Settled_Date = !function.isEmptyOrNull(rb1.getString("reports_Settled_Date"))?rb1.getString("reports_Settled_Date"): "Settled Date";
        String reports_First_Date = !function.isEmptyOrNull(rb1.getString("reports_First_Date"))?rb1.getString("reports_First_Date"): "First Date";
        String reports_Last_Date = !function.isEmptyOrNull(rb1.getString("reports_Last_Date"))?rb1.getString("reports_Last_Date"): "Last Date";
        String reports_Net_Final_Amount = !function.isEmptyOrNull(rb1.getString("reports_Net_Final_Amount"))?rb1.getString("reports_Net_Final_Amount"): "Net Final Amount";
        String reports_Unpaid_Amount = !function.isEmptyOrNull(rb1.getString("reports_Unpaid_Amount"))?rb1.getString("reports_Unpaid_Amount"): "Unpaid Amount";
        String reports_Currency = !function.isEmptyOrNull(rb1.getString("reports_Currency"))?rb1.getString("reports_Currency"): "Currency";
        String reports_Status = !function.isEmptyOrNull(rb1.getString("reports_Status"))?rb1.getString("reports_Status"): "Status";
        String reports_Report_File = !function.isEmptyOrNull(rb1.getString("reports_Report_File"))?rb1.getString("reports_Report_File"): "Report File";
        String reports_Transaction_File = !function.isEmptyOrNull(rb1.getString("reports_Transaction_File"))?rb1.getString("reports_Transaction_File"): "Transaction File";
        String reports_Action = !function.isEmptyOrNull(rb1.getString("reports_Action"))?rb1.getString("reports_Action"): "Action";
        String reports_Charges = !function.isEmptyOrNull(rb1.getString("reports_Charges"))?rb1.getString("reports_Charges"): "Charges";
        String terminal_id = !function.isEmptyOrNull(rb1.getString("Invalid_terminalid"))?rb1.getString("Invalid_terminalid"): "Invalid terminalid";

        if (function.isValueNull(rb1.getString("Invalid_terminalid"))){
            System.out.println("rb1============================"+rb1.getString("Invalid_terminalid"));
            System.out.println("terminal_id============================"+terminal_id);
        }
%>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=reports_reports%></strong></h2>
                           < <div class="additional-btn">
                               <%-- <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>--%>
                            </div>
                        </div>

                        <form  name="form" method="post" action="/merchant/servlet/WireReports?ctoken=<%=ctoken%>&copyiframe=<%=copyiframe%>">

                            <%--<table  align="center" width="82%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
                                <tr>
                                    <td>--%>
                            <%
                                if(request.getParameter("MES")!=null)
                                {
                                    String mes=request.getParameter("MES");
                                    if(mes.equals("ERR"))
                                    {
                                        if(request.getAttribute("error")!=null)
                                        {
                                            ValidationErrorList error= (ValidationErrorList) request.getAttribute("error");
                                            for(Object errorList : error.errors())
                                            {
                                                ValidationException ve = (ValidationException) errorList;
                                                System.out.println("veeeeeeeee"+ve.getMessage());
                                                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + terminal_id + "</h5>");
                                            }
                                        }
                                        else if(request.getAttribute("catchError")!=null)
                                        {
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + request.getAttribute("catchError") + "</h5>");
                                        }
                                    }
                                    else if(mes.equals("FILEERR"))
                                    {
                                        String errorF= (String) request.getAttribute("file");
                                        if("no".equals(errorF))
                                        {
                                            out.println("</i><h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;File not found</h5>");
                                        }
                                        else
                                        {
                                            ValidationErrorList error= (ValidationErrorList) request.getAttribute("error");
                                            for(Object errorList : error.errors())
                                            {
                                                ValidationException ve = (ValidationException) errorList;
                                                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + ve.getMessage() + "</h5>");
                                            }
                                        }
                                    }
                                }
                            %>

                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                            <div class="widget-content padding">
                                <div id="horizontal-form">

                                    <div class="form-group col-md-3">
                                        <label><%=reports_from_date%></label>
                                        <input type="text" size="16" name="fromdate" class="datepicker form-control" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromdate)%>" readonly="readonly" style="width: auto">
                                    </div>

                                    <div class="form-group col-md-3">
                                        <label><%=reports_to_date%></label>
                                        <input type="text" size="16" name="todate" class="datepicker form-control" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;" value="<%=ESAPI.encoder().encodeForHTMLAttribute(todate)%>" readonly="readonly" style="width: auto">
                                    </div>

                                    <div class="form-group col-md-3">
                                        <label><%=reports_terminal_id%></label>
                                        <select size="1" name="terminalid" class="form-control">
                                            <option value=""><%=reports_Select_Terminal_ID%></option>
                                            <%
                                                Iterator i=terminalVO.iterator();
                                                while(i.hasNext())
                                                {
                                                    TerminalVO terminalVO1=(TerminalVO) i.next();
                                                    String active = "";
                                                    String selected = "";
                                                    if (terminalid.equals(terminalVO1.getTerminalId())){
                                                        selected="selected";
                                                    }

                                                    if (terminalVO1.getIsActive().equalsIgnoreCase("Y")){
                                                        active = "Active";
                                                    }
                                                    else{
                                                        active = "InActive";
                                                    }
                                            %>
                                            <option value="<%=terminalVO1.getTerminalId()%>" <%=selected%>>
                                                <%=ESAPI.encoder().encodeForHTMLAttribute(terminalVO1.toString()+"-"+terminalVO1.getCurrency())%>-<%=active%>
                                            </option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-3">
                                        <label><%=reports_paid%></label>
                                        <select size="1" name="paid" class="form-control">
                                            <option value="all"><%=reports_all%></option>
                                            <option value="paid"><%=reports_paid1%></option>
                                            <option value="unpaid"><%=reports_unpaid1%></option>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-9">

                                    </div>

                                    <div class="form-group col-md-3">
                                        <button type="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=reports_search%></button>
                                    </div>

                                    </div>
                                </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12">
            <div class="widget">
                <div class="widget-header">
                    <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=reports_report_table%></strong></h2>
                    <div class="additional-btn">
                        <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                        <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                        <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                    </div>
                </div>



                <%  String errormsg1 = (String)request.getAttribute("message");
                    if (errormsg1 == null)
                    {
                        errormsg1 = "";
                    }
                    else
                    {
                        out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" >");
                        out.println(errormsg1);
                        out.println("</font></td></tr></table>");
                    }
                %>
                <div class="widget-content padding" style="overflow-y: auto;">
                    <br>

                    <%--<div class="table-responsive">--%>
                    <%
                        WireReportsVO wireReports = (WireReportsVO)request.getAttribute("wireReports");
                        if(wireReports!=null)
                        {
                            List<WireVO> wireReportsList =wireReports.getWireReportsList();
                            PaginationVO paginationVO = wireReports.getPaginationVO();
                            paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);
                            if(wireReportsList.size()>0)
                            {
                                int srno=((paginationVO.getPageNo()-1)*paginationVO.getRecordsPerPage())+1;
                    %>

                    <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                        <thead>
                        <tr style="color: white;">
                            <th style="text-align: center"><%=reports_SrNo%></th>
                            <th style="text-align: center"><%=reports_TerminalID%></th>
                            <th style="text-align: center"><%=reports_Settled_Date%></th>
                            <th style="text-align: center"><%=reports_First_Date%></th>
                            <th style="text-align: center"><%=reports_Last_Date%></th>
                            <th style="text-align: center"><%=reports_Net_Final_Amount%></th>
                            <th style="text-align: center"><%=reports_Unpaid_Amount%></th>
                            <th style="text-align: center"><%=reports_Currency%></th>
                            <th style="text-align: center"><%=reports_Status%></th>
                            <th style="text-align: center"><%=reports_Report_File%></th>
                            <th style="text-align: center"><%=reports_Transaction_File%></th>
                            <th style="text-align: center"><%=reports_Action%></th>
                        </tr>
                        </thead>
                        <tbody>
                        <%
                            StringBuffer requestParameter = new StringBuffer();
                            Enumeration<String> stringEnumeration = request.getParameterNames();
                            while(stringEnumeration.hasMoreElements())
                            {
                                String name=stringEnumeration.nextElement();
                                if("SPageno".equals(name) || "SRecords".equals(name))
                                {

                                }
                                else
                                    requestParameter.append("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                            }
                            requestParameter.append("<input type='hidden' name='SPageno' value='"+pageno+"'/>");
                            requestParameter.append("<input type='hidden' name='SRecords' value='"+pagerecords+"'/>");
                        %>

                        <%
                            for(WireVO wireVO : wireReportsList)
                            {
                                TerminalVO TerminalVO=wireVO.getTerminalVO();

                                String settleDate = "";
                                if(wireVO.getSettleDate()!=null)
                                {
                                    settleDate = wireVO.getSettleDate();
                                }
                        %>
                        <form name="exportform" method="post" action="/merchant/servlet/ExportReports?ctoken=<%=ctoken%>" >
                            <div class="pull-right">
                                <div class="btn-group">
                                    <input type="hidden" value="<%=wireVO.getFirstDate()%>" name="firstdate">
                                    <input type="hidden" value="<%=wireVO.getLastDate()%>" name="lastdate">
                                    <input type="hidden" value="<%=wireVO.getNetFinalAmount()%>" name="netfinalamount">
                                    <input type="hidden" value="<%=wireVO.getUnpaidAmount()%>" name="unpaidamount">
                                    <input type="hidden" value="<%=wireVO.getCurrency()%>" name="currency">
                                    <input type="hidden" value="<%=wireVO.getTimestamp()%>" name="timestamp">
                                    <input type="hidden" value="<%=TerminalVO.getTerminalId()%>" name="terminalid">
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                    <button class="btn-xs" type="submit" style="background: white;border: 0;">
                                        <img style="height: 40px;" src="/merchant/images/excel.png">
                                    </button>


                                </div>
                            </div>
                        </form>
                        <tr>
                            <td data-label="Sr No" style="text-align: center"><%=srno%></td>
                            <td data-label="Terminal ID" style="text-align: center"><%=TerminalVO.getTerminalId()%></td>
                            <td data-label="Settled Date" style="text-align: center"><%=settleDate%></td>
                            <td data-label="First Date" style="text-align: center"><%=wireVO.getFirstDate()%></td>
                            <td data-label="Last Date" style="text-align: center"><%=wireVO.getLastDate()%></td>
                            <td data-label="Net Final Amount" style="text-align: center"><%=wireVO.getNetFinalAmount()%></td>
                            <td data-label="Unpaid Amount" style="text-align: center"><%=wireVO.getUnpaidAmount()%></td>
                            <td data-label="Currency" style="text-align: center"><%=wireVO.getCurrency()%></td>
                            <td data-label="Status" style="text-align: center"><%=wireVO.getStatus()%></td>
                            <td data-label="Report File" style="text-align: center"><form  action="/merchant/servlet/SendFile?ctoken=<%=ctoken%>&copyiframe=<%=copyiframe%>" method="post" onsubmit="return(download())"><button type="submit" value="<%=wireVO.getSettlementReportFilePath()%>" name="file"  <%--class="button3"--%> style="margin-left:0 ;margin-top:0px;border:0;background: transparent;"><img style="height: 40px;width: 40px;" src="/merchant/images/pdflogo.png"></button></form></td>
                            <td data-label="Transaction File" style="text-align: center"><form  action="/merchant/servlet/SendFile?ctoken=<%=ctoken%>&copyiframe=<%=copyiframe%>" method="post" onsubmit="return(download())"><button type="submit" value="<%=wireVO.getSettledTransactionFilePath()%>" name="file"   style="margin-left:0 ;margin-top:0px;border:0;background: transparent;"><img style="height: 40px;width: 40px;" src="/merchant/images/excel.jpg"></button></form></td>
                            <td data-label="Action" style="text-align: center"><form action="/merchant/servlet/ListWireReportsRandomCharges?ctoken=<%=ctoken%>&copyiframe=<%=copyiframe%>" method="post"><input type="hidden" name="memberid" value="<%=wireVO.getMemberId()%>"><input type="hidden" name="terminalid" value="<%=wireVO.getTerminalId()%>"><input type="hidden" name="bankwireid" value="<%=wireVO.getSettlementCycleNo()%>"><input type="submit" class="btn btn-default" value="<%=reports_Charges%>">
                                <%
                                    out.println(requestParameter);
                                %>
                            </form></td><%--Random Charges--%>
                        </tr>
                        <%
                                srno++;
                            }
                        %>
                        </tbody>
                    </table>
                    <div>
                        <jsp:include page="page.jsp" flush="true">
                            <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
                            <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                            <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
                            <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
                            <jsp:param name="page" value="<%=paginationVO.getPage()%>"/>
                            <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
                            <jsp:param name="orderby" value=""/>
                        </jsp:include>
                    </div>
                    <%
                                }
                                else
                                {
                                    out.println(Functions.NewShowConfirmation1(reports_search1,reports_no_records));

                                }
                            }
                            else
                            {
                                out.println(Functions.NewShowConfirmation1(reports_search1,reports_no_records));
                            }
                        }
                        catch(Exception e)
                        {
                            PZExceptionHandler.raiseAndHandleGenericViolationException("reports.jsp", "doService()", null, "Merchant", "Generic exception while getting Wire Reports Summary",null, e.getMessage(), e.getCause(), session.getAttribute("merchantid").toString(), PZOperations.Wire_Reports);
                        }
                    %>
                </div>
            </div>
        </div>
    </div>
</div>
</div>
</div>
</body>
</html>
