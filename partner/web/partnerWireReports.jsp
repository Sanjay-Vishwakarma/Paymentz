<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.vo.payoutVOs.WireReportsVO" %>
<%@ page import="com.manager.vo.payoutVOs.WireVO" %>
<%@ page import="com.payment.exceptionHandler.PZExceptionHandler" %>
<%@ page import="com.payment.exceptionHandler.operations.PZOperations" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 8/11/14
  Time: 3:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="ietest.jsp"%>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    session.setAttribute("submit","PartnerReports");
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String partnerWireReports_Generate_Partner_Payout = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_Generate_Partner_Payout")) ? rb1.getString("partnerWireReports_Generate_Partner_Payout") : "Generate Partner Payout Report";
    String partnerWireReports_Partner_Commission = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_Partner_Commission")) ? rb1.getString("partnerWireReports_Partner_Commission") : "Partner Commission";
    String partnerWireReports_Partner_Payout_Reports = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_Partner_Payout_Reports")) ? rb1.getString("partnerWireReports_Partner_Payout_Reports") : "Partner Payout Reports";
    String partnerWireReports_From = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_From")) ? rb1.getString("partnerWireReports_From") : "From";
    String partnerWireReports_To = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_To")) ? rb1.getString("partnerWireReports_To") : "To";
    String partnerWireReports_PartnerID = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_PartnerID")) ? rb1.getString("partnerWireReports_PartnerID") : "Partner ID";
    String partnerWireReports_is_Paid = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_is_Paid")) ? rb1.getString("partnerWireReports_is_Paid") : "is Paid";
    String partnerWireReports_All = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_All")) ? rb1.getString("partnerWireReports_All") : "All";
    String partnerWireReports_Paid = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_Paid")) ? rb1.getString("partnerWireReports_Paid") : "Paid";
    String partnerWireReports_Unpaid = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_Unpaid")) ? rb1.getString("partnerWireReports_Unpaid") : "Unpaid";
    String partnerWireReports_Path = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_Path")) ? rb1.getString("partnerWireReports_Path") : "Path";
    String partnerWireReports_Search = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_Search")) ? rb1.getString("partnerWireReports_Search") : "Search";
    String partnerWireReports_Wire_Reports = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_Wire_Reports")) ? rb1.getString("partnerWireReports_Wire_Reports") : "Wire Reports";
    String partnerWireReports_SrNo = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_SrNo")) ? rb1.getString("partnerWireReports_SrNo") : "Sr No";
    String partnerWireReports_Settled_Date = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_Settled_Date")) ? rb1.getString("partnerWireReports_Settled_Date") : "Settled Date";
    String partnerWireReports_First_Date = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_First_Date")) ? rb1.getString("partnerWireReports_First_Date") : "First Date";
    String partnerWireReports_Last_Date = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_Last_Date")) ? rb1.getString("partnerWireReports_Last_Date") : "Last Date";
    String partnerWireReports_Partner_Funded_Amount = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_Partner_Funded_Amount")) ? rb1.getString("partnerWireReports_Partner_Funded_Amount") : "Partner Funded Amount";
    String partnerWireReports_Unpaid_Amount = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_Unpaid_Amount")) ? rb1.getString("partnerWireReports_Unpaid_Amount") : "Unpaid Amount";
    String partnerWireReports_Currency = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_Currency")) ? rb1.getString("partnerWireReports_Currency") : "Currency";
    String partnerWireReports_Status = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_Status")) ? rb1.getString("partnerWireReports_Status") : "Status";
    String partnerWireReports_Settlement_Report_Path = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_Settlement_Report_Path")) ? rb1.getString("partnerWireReports_Settlement_Report_Path") : "Settlement Report Path";
    String partnerWireReports_Showing_Page = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_Showing_Page")) ? rb1.getString("partnerWireReports_Showing_Page") : "Showing Page";
    String partnerWireReports_of = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_of")) ? rb1.getString("partnerWireReports_of") : "of";
    String partnerWireReports_records = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_records")) ? rb1.getString("partnerWireReports_records") : "records";
    String partnerWireReports_Sorry = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_Sorry")) ? rb1.getString("partnerWireReports_Sorry") : "Sorry";
    String partnerWireReports_no = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_no")) ? rb1.getString("partnerWireReports_no") : "No records found.";
    String partnerWireReports_Filter = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_Filter")) ? rb1.getString("partnerWireReports_Filter") : "Filter";
    String partnerWireReports_please = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_please")) ? rb1.getString("partnerWireReports_please") : "Please provide the Data for getting reports list.";
    String partnerWireReports_page_no = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_page_no")) ? rb1.getString("partnerWireReports_page_no") : "Page number";
    String partnerWireReports_total_no_of_records = StringUtils.isNotEmpty(rb1.getString("partnerWireReports_total_no_of_records")) ? rb1.getString("partnerWireReports_total_no_of_records") : "Total number of records";
%>
<html>
<head>
    <title><%=company%> Partner> Payout Reports</title>
</head>
<body class="bodybackground">
<%@ include file="top.jsp" %>
<link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
<script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
<script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
<script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
<%--<script>
    $(document).ready(function(){
        $('#myTable').dataTable();
    });
</script>--%>
<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>

<title><%=company%> Merchant Transactions</title>
<script language="javascript">
    function isint(form)
    {
        if (isNaN(form.numrows.value))
            return false;
        else
            return true;
    }
</script>
<script type="text/javascript">

    $('#sandbox-container input').datepicker({
    });
</script>
<script>
    $(function() {
        $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
    });
</script>
<script>
    function download()
    {
        var answer = confirm("do you want to Download File?");
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
<%!
    private static Logger logger = new Logger("partnerWireReports.jsp");
    private static Functions functions= new Functions();
%>
<%
    session.setAttribute("submit","PartnerReports");
    String partnerid= String.valueOf(session.getAttribute("partnerId"));
    try
    {
        String fromdate = null;
        String todate = null;

        Date date = new Date();
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

        String Date = originalFormat.format(date);
        date.setDate(1);
        String fromDate = originalFormat.format(date);

        fromdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDate : request.getParameter("fromdate");
        todate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");
        String roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
        String partnerId=Functions.checkStringNull(request.getParameter("partnerId"))==null?"":request.getParameter("partnerId");

        String str = "";
        str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        if(partnerId!=null)str = str + "&partnerId=" + partnerId;
        else
            partnerId="";
%>

<div class="content-page">
    <div class="content">
        <div class="page-heading">
            <%
                if(roles.contains("superpartner"))
                {
            %>
            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/partnercommissioncron.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button class="btn-lg" style="background-color: #98A3A3; border-radius: 25px;color: white;padding: 12px 16px;font-size: 16px;cursor: pointer;"><%=partnerWireReports_Generate_Partner_Payout%>
                        </button>
                    </form>
                </div>
            </div>

            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/listPartnerCommission.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button class="btn-lg" style="background-color: #98A3A3; border-radius: 25px;color: white;padding: 12px 16px;font-size: 16px;cursor: pointer;"><%=partnerWireReports_Partner_Commission%>
                        </button>
                    </form>
                </div>
            </div>
            <%
                }
            %>

            <form  name="form" method="post" action="/partner/net/PartnerWireReports?ctoken=<%=ctoken%>">
                <input type="hidden" value="<%=partnerid%>" name="superAdminId" id="partnerid">
                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=partnerWireReports_Partner_Payout_Reports%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>



                            <input type="hidden" value="<%=ctoken%>" name="ctoken">
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
                                                //out.println("<center><font class=\"textb\">" + ve.getMessage() + "</font></center>");
                                                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + ve.getMessage() + "</h5>");
                                            }
                                        }
                                        else if(request.getAttribute("catchError")!=null)
                                        {
                                            //out.println("<center><font class=\"textb\">" + request.getAttribute("catchError") + "</font></center>");
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + request.getAttribute("catchError") + "</h5>");
                                        }
                                    }
                                    else if(mes.equals("FILEERR"))
                                    {
                                        String errorF= (String) request.getAttribute("file");
                                        if("no".equals(errorF))
                                        {
                                            //out.println("<center><font class=\"textb\">File not found</font></center>");
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;File not found</h5>");
                                        }
                                        else
                                        {
                                            ValidationErrorList error= (ValidationErrorList) request.getAttribute("error");
                                            for(Object errorList : error.errors())
                                            {
                                                ValidationException ve = (ValidationException) errorList;
                                                //out.println("<center><font class=\"textb\">" + ve.getMessage() + "</font></center>");
                                                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                                            }
                                        }
                                    }
                                }
                            %>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                            <div class="widget-content padding">
                                <div id="horizontal-form">

                                    <div class="form-group col-md-3">
                                        <label><%=partnerWireReports_From%></label>
                                        <input type="text" size="16" name="fromdate" class="datepicker form-control" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromdate)%>" readonly="readonly" style="width: auto">
                                    </div>

                                    <div class="form-group col-md-3">
                                        <label><%=partnerWireReports_To%></label>
                                        <input type="text" size="16" name="todate" class="datepicker form-control" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;" value="<%=ESAPI.encoder().encodeForHTMLAttribute(todate)%>" readonly="readonly" style="width: auto">
                                    </div>

                                    <div class="ui-widget form-group col-md-4 has-feedback">
                                        <label for="PID"><%=partnerWireReports_PartnerID%></label>
                                        <input name="partnerId" id="PID" value="<%=partnerId%>" class="form-control" autocomplete="on">
                                    </div>

                                    <div class="form-group col-md-3">
                                        <label><%=partnerWireReports_is_Paid%></label>
                                        <select size="1" name="paid" class="form-control">
                                            <option value="all"><%=partnerWireReports_All%></option>
                                            <option value="paid"><%=partnerWireReports_Paid%></option>
                                            <option value="unpaid"><%=partnerWireReports_Unpaid%></option>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-3">
                                        <label style="color: transparent;"><%=partnerWireReports_Path%></label>
                                        <button type="submit" class="btn btn-default" style="display:block;">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;<%=partnerWireReports_Search%>
                                        </button>
                                    </div>

                                </div>
                            </div>

                        </div>
                    </div>
                </div>
            </form>


            <div class="row reporttable">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerWireReports_Wire_Reports%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
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
                                <tr style="background-color: #7eccad !important;color: white;">
                                    <th style="text-align: center"><%=partnerWireReports_SrNo%></th>
                                    <th style="text-align: center"><%=partnerWireReports_Settled_Date%></th>
                                    <th style="text-align: center"><%=partnerWireReports_First_Date%></th>
                                    <th style="text-align: center"><%=partnerWireReports_Last_Date%></th>
                                    <th style="text-align: center"><%=partnerWireReports_Partner_Funded_Amount%></th>
                                    <th style="text-align: center"><%=partnerWireReports_Unpaid_Amount%></th>
                                    <th style="text-align: center"><%=partnerWireReports_Currency%></th>
                                    <th style="text-align: center"><%=partnerWireReports_Status%></th>
                                    <th style="text-align: center"><%=partnerWireReports_Settlement_Report_Path%></th>
                                </tr>
                                </thead>
                                <tbody>
                                <%
                                    for(WireVO wireVO : wireReportsList)
                                    {
                                        TerminalVO TerminalVO=wireVO.getTerminalVO();
                                %>
                                <tr>
                                    <td style="text-align: center" data-label="Sr No"><%=srno%></td>

                                    <%--<%


                                    if (recurringBillingVO.getInterval()!=null)
                                    {
                                    out.println("<td align=center class=\"textb\" data-label=\"Interval\">&nbsp;" + recurringBillingVO.getInterval() + "</td>");
                                    }
                                    else if (recurringBillingVO.getInterval()==null)
                                    {
                                    out.println("<td align=center class=\"textb\" data-label=\"Interval\">&nbsp;"+"-"+"</td>");
                                    }--%>
                                    <td style="text-align: center" data-label="Settled Date"><%=functions.isValueNull(wireVO.getSettleDate())?wireVO.getSettleDate():"-"%></td>

                                    <td style="text-align: center" data-label="First Date"><%=wireVO.getFirstDate()%></td>
                                    <td style="text-align: center" data-label="Last Date"><%=wireVO.getLastDate()%></td>
                                    <td style="text-align: center" data-label="Partner Funded Amount"><%=wireVO.getNetFinalAmount()%></td>
                                    <td style="text-align: center" data-label="Unpaid Amount"><%=wireVO.getUnpaidAmount()%></td>
                                    <td style="text-align: center" data-label="Currency"><%=wireVO.getCurrency()%></td>
                                    <td style="text-align: center" data-label="Status"><%=wireVO.getStatus()%></td>
                                    <td style="text-align: center" data-label="Settlement Report Path"><form  action="/partner/net/SendPartnerFile?ctoken=<%=ctoken%>" method="post" onsubmit="return(download())"><button type="submit" value="<%=wireVO.getSettlementReportFilePath()%>" name="file"  class="btn btn-default" style="width:100%;margin-left:0 ;margin-top:0px; background-color: transparent; border-color: transparent; padding: 0;"><img width="50" height="50" border="0" src="/partner/images/pdflogo.png"></button></form></td>
                                </tr>
                                <%
                                        srno++;
                                    }
                                %>
                                </tbody>
                            </table>

                            <%
                                int TotalPageNo;
                                if(paginationVO.getTotalRecords()%paginationVO.getRecordsPerPage()!=0)
                                {
                                    TotalPageNo=paginationVO.getTotalRecords()/paginationVO.getRecordsPerPage()+1;
                                }
                                else
                                {
                                    TotalPageNo=paginationVO.getTotalRecords()/paginationVO.getRecordsPerPage();
                                }
                            %>
                            <div id="showingid"><strong><%=partnerWireReports_page_no%> <%=paginationVO.getPageNo()%>  <%=partnerWireReports_of%>  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
                            <div id="showingid"><strong><%=partnerWireReports_total_no_of_records%>   <%=paginationVO.getTotalRecords()%> </strong></div>
                            <div>
                                <jsp:include page="page.jsp" flush="true">
                                    <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
                                    <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                                    <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
                                    <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
                                    <jsp:param name="page" value="PartnerWireReports"/>
                                    <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
                                    <jsp:param name="orderby" value=""/>
                                </jsp:include>
                            </div>

                            <%
                                        }
                                        else
                                        {
                                            out.println(Functions.NewShowConfirmation1(partnerWireReports_Sorry,partnerWireReports_no ));

                                        }
                                    }
                                    else
                                    {
                                        out.println(Functions.NewShowConfirmation1(partnerWireReports_Filter,partnerWireReports_please ));
                                    }

                                }
                                catch(Exception e)
                                {
                                    logger.error("Generic exception in transaction summary",e);
                                    out.println("<br>");
                                    out.println("<br>");
                                    out.println("<br>");
                                    out.println("<div class=\"reporttable\" style=\"margin-top:100px\">");
                                    out.println(Functions.NewShowConfirmation1("Sorry","Kindly check Wire reports after some time."));
                                    out.println("</div>");
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