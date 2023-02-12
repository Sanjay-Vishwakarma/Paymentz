<%@ page import="com.directi.pg.Functions,org.owasp.esapi.ESAPI,org.owasp.esapi.errors.ValidationException"%>
<%@ page import="java.util.*" %>
<%@ include file="top.jsp"%>
<%@ page import="com.directi.pg.*" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="com.manager.vo.payoutVOs.AgentWireVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.payment.exceptionHandler.PZDBViolationException" %>
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
<%@include file="ietest.jsp"%>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("agentname"));
    String agentid = (String)session.getAttribute("merchantid");
    session.setAttribute("submit","Reports");
    if (agent.isLoggedInAgent(session))
    {
%>
<html>
<head>
    <title><%=company%> | Wire Reports</title>

    <style type="text/css">
        #myTable th{text-align: center;}

        #myTable td{
            font-family: Open Sans;
            font-size: 13px;
            font-weight: 600;
        }

        #myTable .button3 {
            text-indent: 0!important;
            width: 100%;
            height: inherit!important;
            display: block;
            color: #000000;
            padding: 0px;
            background: transparent!important;
            border: 0px solid #dedede;
            text-align: center!important;
            font-family: "Open Sans","Helvetica Neue",Helvetica,Arial,sans-serif;
            font-size: 12px;
        }
    </style>
</head>
<body class="bodybackground">

<%--<link rel="stylesheet" href=" /agent/transactionCSS/css/jquery.dataTables.min.css">
<script type="text/javascript" src="/agent/transactionCSS/js/jquery.dataTables.min.js"></script>--%>
<link href="/agent/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/agent/datepicker/datepicker/bootstrap-datepicker.js"></script>
<script src="/agent/NewCss/libs/jquery-icheck/icheck.min.js"></script>

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
    $('#sandbox-container input').datepicker({});
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
        var answer = confirm("Do You Really Want To Download The Selected File?");
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
    private static Logger logger = new Logger("wireReport.jsp");
    private TerminalManager terminalManager = new TerminalManager();
%>
<%
    session.setAttribute("submit","Agent Wire Report");
    try
    {
        List<TerminalVO> terminalVO = terminalManager.getTerminalListByAgent(session.getAttribute("merchantid").toString());

        //initiating parameters for date
        String fdate=null;
        String tdate=null;
        /*String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;*/

        /*try
        {
            fdate = ESAPI.validator().getValidInput("fromdate",request.getParameter("fromdate"),"Days",3,true);
            tdate = ESAPI.validator().getValidInput("todate",request.getParameter("todate"),"Days",3,true);
            *//*fmonth = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
            tmonth =  ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
            fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
            tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);*//*
        }
        catch(ValidationException e)
        {

        }*/

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
        String currentyear= ""+rightNow.get(rightNow.YEAR);

%>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <form  name="form" method="post" action="/agent/net/WireReport?ctoken=<%=ctoken%>">
                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">
                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%> Wire Reports</strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <div class="widget-content padding">
                                <div id="horizontal-form">
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                    <input type="hidden" value="<%=agentid%>" name="agentid">
                                    <%
                                        if(request.getParameter("MES")!=null)
                                        {
                                            String mes=request.getParameter("MES");
                                            if(mes.equals("ERR"))
                                            {
                                                if(request.getAttribute("error")!=null)
                                                {
                                                    //System.out.println("Errors here---->"+request.getAttribute("error"));
                                                    ValidationErrorList error= (ValidationErrorList) request.getAttribute("error");
                                                    for(Object errorList : error.errors())
                                                    {
                                                        ValidationException ve = (ValidationException) errorList;
                                                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + ve.getMessage() + "</h5>");
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
                                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;File not found</h5>");
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

                                    <div class="form-group col-md-3">
                                        <label>From</label>
                                        <input type="text" name="fromdate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">

                                    </div>

                                    <div class="form-group col-md-3">
                                        <label>To</label>
                                        <input type="text" name="todate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">

                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label>Terminal ID</label>
                                        <select size="1" name="terminalid" class="form-control">
                                            <option value="">--All--</option>
                                            <%
                                                Iterator i=terminalVO.iterator();
                                                String active="";
                                                while(i.hasNext())
                                                {
                                                    TerminalVO terminalVO1=(TerminalVO) i.next();
                                                    if (terminalVO1.getIsActive().equalsIgnoreCase("Y"))
                                                    {
                                                        active = "Active";
                                                    }
                                                    else
                                                    {
                                                        active = "InActive";
                                                    }
                                                    if(terminalVO1.getTerminalId().equals(request.getParameter("terminalid")))
                                                    {
                                            %>
                                            <option value="<%=terminalVO1.getTerminalId()%>" selected>
                                                <%=ESAPI.encoder().encodeForHTMLAttribute(terminalVO1.getMemberId())%> - <%=ESAPI.encoder().encodeForHTMLAttribute(terminalVO1.toString())%> - <%=ESAPI.encoder().encodeForHTMLAttribute(terminalVO1.getCurrency())%> -<%=active%>
                                            </option>
                                            <%}else{%>
                                            <option value="<%=terminalVO1.getTerminalId()%>" >
                                                <%=ESAPI.encoder().encodeForHTMLAttribute(terminalVO1.getMemberId())%> - <%=ESAPI.encoder().encodeForHTMLAttribute(terminalVO1.toString())%> - <%=ESAPI.encoder().encodeForHTMLAttribute(terminalVO1.getCurrency())%>  -<%=active%>
                                            </option>
                                            <%  }
                                            }
                                            %>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label>Is Paid</label>
                                        <select size="1" name="paid" class="form-control">
                                            <option value="all">All</option>
                                            <option value="paid">Paid</option>
                                            <option value="unpaid">Unpaid</option>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-9">
                                    </div>

                                    <div class="form-group col-md-3">
                                        <button type="submit" name="B1" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-clock-o"></i>&nbsp;&nbsp;Search</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </form>

            <div class="row">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Summary Data</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" style="overflow-y: auto;">
                            <%
                                List<AgentWireVO> agentWireVOList = (List<AgentWireVO>) request.getAttribute("agentWireVOList");
                                String errorMsg = (String) request.getAttribute("error");
                                if(agentWireVOList!=null)
                                {
                                    PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
                                    //System.out.println("Page Records---->"+paginationVO.getPageNo());
                                    paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);
                                    if(agentWireVOList.size()>0)
                                    {
                                        int srno=((paginationVO.getPageNo()-1)*paginationVO.getRecordsPerPage())+1;
                            %>
                            <table id="myTable" class="display table table table-striped table-bordered dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead>
                                <tr style="background-color: #7eccad !important;color: white;">
                                    <th>Sr No</th>
                                    <th>Terminal ID</th>
                                    <th>Merchant ID</th>
                                    <th>Settled Date</th>
                                    <th>Start Date</th>
                                    <th>End Date</th>
                                    <th>Funded Amount</th>
                                    <th>Unpaid Amount</th>
                                    <th>Currency</th>
                                    <th>Status</th>
                                    <th>Report File</th>
                                </tr>
                                </thead>

                                <tbody>

                                <%
                                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Functions functions = new Functions();
                                    for(AgentWireVO agentWireVO : agentWireVOList)
                                    {
                                %>


                                <tr>
                                    <td valign="middle" data-label="Sr No" align="center"><%=srno%></td>
                                    <td valign="middle" data-label="Terminal ID" align="center"><%=agentWireVO.getTerminalId()%></td>
                                    <td valign="middle" data-label="Merchant ID" align="center"><%=agentWireVO.getMemberId()%></td>
                                    <%if(functions.isValueNull(agentWireVO.getSettleDate())) { %>
                                    <td valign="middle" data-label="Settled Date" align="center"><%=targetFormat.format(targetFormat.parse(agentWireVO.getSettleDate()))%></td>
                                    <%}else { %>
                                    <td valign="middle" data-label="Settled Date" align="center">-</td>
                                    <%}%>
                                    <td valign="middle" data-label="Start Date" align="center"><%=targetFormat.format(targetFormat.parse(agentWireVO.getSettlementStartDate()))%></td>
                                    <td valign="middle" data-label="End Date" align="center"><%=targetFormat.format(targetFormat.parse(agentWireVO.getSettlementEndDate()))%></td>
                                    <td valign="middle" data-label="Funded Amount" align="center"><%=agentWireVO.getAgentTotalFundedAmount()%></td>
                                    <td valign="middle" data-label="Unpaid Amount" align="center"><%=agentWireVO.getAgentUnpaidAmount()%></td>
                                    <td valign="middle" data-label="Currency" align="center"><%=agentWireVO.getCurrency()%></td>
                                    <td valign="middle" data-label="Status" align="center"><%=agentWireVO.getStatus()%></td>
                                    <td valign="middle" data-label="Report File" align="center"><form  action="/agent/net/SendFile?ctoken=<%=ctoken%>" method="post" onsubmit="return(download())"><button type="submit" value="<%=agentWireVO.getSettlementReportFileName()%>" name="file"  class="button3" style="width:100%;margin-left:0 ;margin-top:0px"><img width="50" height="auto" border="0" src="/agent/images/pdflogo.png"></button></form></td>
                                </tr>
                                <%
                                        srno++;
                                    }
                                %>

                                </tbody>

                            </table>
                        </div>
                        <div id="showingid"><strong>Showing Page <%=paginationVO.getPageNo()%> of <%=paginationVO.getTotalRecords()%> records</strong></div>
                        <jsp:include page="page.jsp" flush="true">
                            <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
                            <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                            <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
                            <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
                            <jsp:param name="page" value="WireReport"/>
                            <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
                            <jsp:param name="orderby" value=""/>
                        </jsp:include>
                        <%--<%
                                    }
                                    else
                                    {
                                        out.println(Functions.NewShowConfirmation1("Sorry","No Records Found."));

                                    }
                                }
                                else
                                {
                                    out.println(Functions.NewShowConfirmation1("Sorry","No Records Found."));
                                }
                            }
                            catch(Exception e)
                            {
                                PZExceptionHandler.raiseAndHandleGenericViolationException("reports.jsp", "doService()", null, "Merchant", "Generic exception while getting Wire Reports Summary",null, e.getMessage(), e.getCause(), session.getAttribute("merchantid").toString(), PZOperations.Wire_Reports);
                            }
                        %>--%>

                        <%
                                    }
                                    else if(errorMsg!= null)
                                    {
                                        out.println(Functions.NewShowConfirmation1("Error",errorMsg));
                                    }
                                    else
                                    {
                                        out.println(Functions.NewShowConfirmation1("Sorry","No Record Found."));
                                    }
                                }
                                else if(errorMsg!= null)
                                {
                                    out.println(Functions.NewShowConfirmation1("Error",errorMsg));
                                }
                                else
                                {
                                    out.println(Functions.NewShowConfirmation1("Sorry","No Record Found."));
                                }
                            }
                            catch(PZDBViolationException dbe)
                            {
                                logger.error("PZDBViolationException::",dbe);
                                out.println("<br>");
                                out.println("<br>");
                                out.println("<br>");
                                out.println("<div class=\"reporttable\" style=\"margin-top:100px\">");
                                out.println(Functions.NewShowConfirmation1("Sorry","Internal error while accessing data"));
                                out.println("</div>");
                                PZExceptionHandler.handleDBCVEException(dbe, session.getAttribute("merchantid").toString(), PZOperations.Wire_Reports);
                            }
                            catch(Exception e)
                            {
                                logger.error("Exception in transaction summary",e);
                                out.println("<br>");
                                out.println("<br>");
                                out.println("<br>");
                                out.println("<div class=\"reporttable\" style=\"margin-top:100px\">");
                                out.println(Functions.NewShowConfirmation1("Sorry","Internal error while accessing data."));
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
</body>
<%
    }
    else
    {
        response.sendRedirect("/agent/logout.jsp");
        return;
    }
%>
</html>