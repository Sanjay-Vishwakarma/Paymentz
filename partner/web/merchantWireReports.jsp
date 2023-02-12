<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.vo.payoutVOs.WireReportsVO" %>
<%@ page import="com.manager.vo.payoutVOs.WireVO" %>
<%@ page import="com.payment.exceptionHandler.PZExceptionHandler" %>
<%@ page import="com.payment.exceptionHandler.operations.PZOperations" %>
<%@ page import="net.partner.PartnerFunctions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 8/11/14
  Time: 3:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    session.setAttribute("submit","Reports");
%>
<%@ include file="top.jsp" %>
<html>
<head>
    <title><%=company%> Merchant Management >Merchant Wire Reports</title>

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
        #ui-id-2
        {
            overflow: auto;
            max-height: 350px;
        }
    </style>
    <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>--%>
    <%--<script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script>

        $(document).ready(function(){

            var w = $(window).width();

            //alert(w);

            if(w > 990){
                //alert("It's greater than 990px");
                $("body").removeClass("smallscreen").addClass("widescreen");
                $("#wrapper").removeClass("enlarged");
            }
            else{
                //alert("It's less than 990px");
                $("body").removeClass("widescreen").addClass("smallscreen");
                $("#wrapper").addClass("enlarged");
                $(".left ul").removeAttr("style");
            }
        });

    </script>

</head>
<body class="bodybackground">
<%--<link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
<script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>--%>
<!--<script type="text/javascript" language="JavaScript" src="/partner/javascript/memberid_terminal.js"></script>-->
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
    private static Logger logger = new Logger("MerchantWireReports.jsp");
    private static TerminalManager terminalManager = new TerminalManager();
    Functions functions=new Functions();
    PartnerFunctions partner1=new PartnerFunctions();
%>
<%
    session.setAttribute("submit","Reports");
    try
    {
        Hashtable temphash=null;
        StringBuffer optionTag=new StringBuffer();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        Set<String> memberList=new HashSet<String>();
        List<TerminalVO> terminalVO = terminalManager.getTerminalsByPartnerId(session.getAttribute("merchantid").toString());
        TreeMap<Integer,String> memberiddetails=partner1.getPartnerMemberDetail((String) session.getAttribute("merchantid"));
        String fromdate = null;
        String todate = null;
        String terminalid = nullToStr(request.getParameter("terminalid"));
        String memberId = nullToStr(request.getParameter("memberid"));
        String pid=nullToStr(request.getParameter("pid"));
        String Config =null;
        String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
        if(Roles.contains("superpartner")){

        }else{
            pid = String.valueOf(session.getAttribute("merchantid"));
            Config = "disabled";
        }
        String paid = request.getParameter("paid");
        // String active="";

        Date date = new Date();
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

        String Date = originalFormat.format(date);
        date.setDate(1);
        String fromDate = originalFormat.format(date);

        fromdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDate : request.getParameter("fromdate");
        todate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");

        String partnerid = session.getAttribute("partnerId").toString();
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String merchantWireReports_Merchant_Wire_Reports = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Merchant_Wire_Reports")) ? rb1.getString("merchantWireReports_Merchant_Wire_Reports") : "Merchant Wire Reports";
        String merchantWireReports_From = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_From")) ? rb1.getString("merchantWireReports_From") : "From";
        String merchantWireReports_To = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_To")) ? rb1.getString("merchantWireReports_To") : "To";
        String merchantWireReports_Partner_ID = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Partner_ID")) ? rb1.getString("merchantWireReports_Partner_ID") : "Partner ID";
        String merchantWireReports_Merchant_ID = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Merchant_ID")) ? rb1.getString("merchantWireReports_Merchant_ID") : "Merchant ID";
        String merchantWireReports_Terminal_ID = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Terminal_ID")) ? rb1.getString("merchantWireReports_Terminal_ID") : "Terminal ID";
        String merchantWireReports_Is_Paid = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Is_Paid")) ? rb1.getString("merchantWireReports_Is_Paid") : "Is Paid";
        String merchantWireReports_All = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_All")) ? rb1.getString("merchantWireReports_All") : "All";
        String merchantWireReports_Paid = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Paid")) ? rb1.getString("merchantWireReports_Paid") : "Paid";
        String merchantWireReports_Unpaid = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Unpaid")) ? rb1.getString("merchantWireReports_Unpaid") : "Unpaid";
        String merchantWireReports_Search = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Search")) ? rb1.getString("merchantWireReports_Search") : "Search";
        String merchantWireReports_Summary_Data = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Summary_Data")) ? rb1.getString("merchantWireReports_Summary_Data") : "Summary Data";
        String merchantWireReports_Sr_No = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Sr_No")) ? rb1.getString("merchantWireReports_Sr_No") : "Sr No";
        String merchantWireReports_Partner_ID1 = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Partner_ID1")) ? rb1.getString("merchantWireReports_Partner_ID1") : "Partner ID";
        String merchantWireReports_Merchant_ID1 = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Merchant_ID1")) ? rb1.getString("merchantWireReports_Merchant_ID1") : "Merchant ID";
        String merchantWireReports_Terminal_ID1 = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Terminal_ID1")) ? rb1.getString("merchantWireReports_Terminal_ID1") : "Terminal ID";
        String merchantWireReports_Cycle_ID = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Cycle_ID")) ? rb1.getString("merchantWireReports_Cycle_ID") : "Cycle ID";
        String merchantWireReports_Settled_Date = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Settled_Date")) ? rb1.getString("merchantWireReports_Settled_Date") : "Settled Date";
        String merchantWireReports_First_Date = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_First_Date")) ? rb1.getString("merchantWireReports_First_Date") : "First Date";
        String merchantWireReports_Last_Date = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Last_Date")) ? rb1.getString("merchantWireReports_Last_Date") : "Last Date";
        String merchantWireReports_Net_Final_Amount = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Net_Final_Amount")) ? rb1.getString("merchantWireReports_Net_Final_Amount") : "Net Final Amount";
        String merchantWireReports_Unpaid_Amount = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Unpaid_Amount")) ? rb1.getString("merchantWireReports_Unpaid_Amount") : "Unpaid Amount";
        String merchantWireReports_Currency = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Currency")) ? rb1.getString("merchantWireReports_Currency") : "Currency";
        String merchantWireReports_Status = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Status")) ? rb1.getString("merchantWireReports_Status") : "Status";
        String merchantWireReports_Report_File = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Report_File")) ? rb1.getString("merchantWireReports_Report_File") : "Report File";
        String merchantWireReports_Transaction_File = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Transaction_File")) ? rb1.getString("merchantWireReports_Transaction_File") : "Transaction File";
        String merchantWireReports_Action = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Action")) ? rb1.getString("merchantWireReports_Action") : "Action";
        String merchantWireReports_Showing_Page = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Showing_Page")) ? rb1.getString("merchantWireReports_Showing_Page") : "Showing Page";
        String merchantWireReports_of = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_of")) ? rb1.getString("merchantWireReports_of") : "of";
        String merchantWireReports_records = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_records")) ? rb1.getString("merchantWireReports_records") : "records";
        String merchantWireReports_Sorry = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Sorry")) ? rb1.getString("merchantWireReports_Sorry") : "Sorry";
        String merchantWireReports_No_Records_Found = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_No_Records_Found")) ? rb1.getString("merchantWireReports_No_Records_Found") : "No Records Found.";
        String merchantWireReports_Random_Charges = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Random_Charges")) ? rb1.getString("merchantWireReports_Random_Charges") : "Random Charges";
        String merchantWireReports_Edit = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Edit")) ? rb1.getString("merchantWireReports_Edit") : "Edit";
        String merchantWireReports_Delete = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_Delete")) ? rb1.getString("merchantWireReports_Delete") : "Delete";
        String merchantWireReports_page_no = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_page_no")) ? rb1.getString("merchantWireReports_page_no") : "Page number";
        String merchantWireReports_total_no_of_records = StringUtils.isNotEmpty(rb1.getString("merchantWireReports_total_no_of_records")) ? rb1.getString("merchantWireReports_total_no_of_records") : "Total number of records";

     /*   for(TerminalVO terminalVO1:terminalVO)
        {
            if (terminalVO1.getIsActive().equalsIgnoreCase("Y"))
            {
                active = "Active";
            }
            else
            {
                active = "InActive";
            }
            if(terminalVO1.getTerminalId().equals(request.getParameter("terminalid")))
                optionTag.append("<option value=\""+terminalVO1.getTerminalId()+"\" selected>"+terminalVO1.toString()+"-"+terminalVO1.getCurrency()+"-"+active+"</option>");
            else
                optionTag.append("<option value=\""+terminalVO1.getTerminalId()+"\" >"+terminalVO1.toString()+"-"+terminalVO1.getCurrency()+"-"+active+"</option>");
            memberList.add(terminalVO1.getMemberId()+"-"+terminalVO1.getCompany_name());
        }*/
%>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <form  name="form" method="post" action="/partner/net/MerchantWireReports?ctoken=<%=ctoken%>">
                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">
                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%> <%=merchantWireReports_Merchant_Wire_Reports%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <div class="widget-content padding">
                                <div id="horizontal-form">
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                                    <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                                    <%--  <%
                                          String msg = (String) request.getAttribute("message");
                                          //String errormsg=(String)request.getAttribute("message");
                                          if(partner1.isValueNull(msg))
                                          {
                                              out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + msg + "</h5>");
                                          }
                                      %>--%>
                                    <%
                                        String msg = (String) request.getAttribute("message");
                                        //String errormsg=(String)request.getAttribute("message");
                                        if(partner1.isValueNull(msg))
                                        {
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + msg + "</h5>");
                                        }
                                        String status = (String) request.getAttribute("status");
                                        if (status!=null)
                                        {
                                            out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + status + "</h5>");
                                        }
                                    %>
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
                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=merchantWireReports_From%></label>
                                        <input type="text" size="16" name="fromdate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute (fromdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=merchantWireReports_To%></label>
                                        <input type="text" size="16" name="todate" class="datepicker form-control" value="<%= ESAPI.encoder().encodeForHTMLAttribute (todate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <div class="ui-widget">
                                            <label for="pid"><%=merchantWireReports_Partner_ID%></label>
                                            <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                                            <input name="pid" type="hidden" value="<%=pid%>">
                                        </div>
                                    </div>


                                    <div class="form-group col-md-4 has-feedback">
                                        <div class="ui-widget">
                                            <label for="member"><%=merchantWireReports_Merchant_ID%></label>
                                            <input name="memberid" id="member" value="<%=memberId%>" class="form-control" autocomplete="on">
                                        </div>

                                        <%--<label >Merchant ID</label>
                                        <select name="memberid" id="mid" class="form-control">
                                            <option value="0" selected>Select Merchant ID</option>
                                            <%
                                                String selected3 = "";
                                                String key3 = "";
                                                String value3 = "";
                                               // TreeMap treeMap = new TreeMap(memberiddetails);
                                              //  Iterator itr = treeMap.keySet().iterator();
                                                for(Integer mid:memberiddetails.keySet())
                                                {
                                                    key3 = String.valueOf(mid);
                                                    value3 = memberiddetails.get(mid);
                                                    if (key3.equals(memberId))
                                                        selected3 = "selected";
                                                    else
                                                        selected3 = "";
                                            %>
                                            <option value="<%=key3%>" <%=selected3%>> <%=key3%>-<%=value3%></option>
                                            <%
                                                }
                                            %>
                                        </select>--%>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <div class="ui-widget">
                                            <label for="terminal"><%=merchantWireReports_Terminal_ID%></label>
                                            <input id="terminal" name="terminalid" value="<%=terminalid%>" class="form-control" autocomplete="on">
                                        </div>
                                        <%--                                        <label>Terminal ID</label>
                                                                                <select size="1" name="terminalid" id="tid" class="form-control">
                                                                                    <option data-mid="all" value="0">---All---</option>
                                                                                    <%
                                                                                        Iterator i=terminalVO.iterator();
                                                                                        String  terminalid2="";
                                                                                        //Map memberTerminal = new HashMap();
                                                                                        String terminalSelect = "";
                                                                                        String active = "";
                                                                                        for(TerminalVO terminalVO1 : terminalVO)
                                                                                        {
                                                                                            if (terminalVO1.getTerminalId().equals(terminalid))
                                                                                                terminalSelect = "selected";
                                                                                            else
                                                                                                terminalSelect = "";

                                                                                            if (terminalVO1.getIsActive().equalsIgnoreCase("Y"))
                                                                                            {
                                                                                                active = "Active";
                                                                                            }
                                                                                            else
                                                                                            {
                                                                                                active = "InActive";
                                                                                            }
                                                                                    %>
                                                                                    <option data-mid="<%=terminalVO1.getMemberId()%>" value="<%=terminalVO1.getTerminalId()%>" <%=terminalSelect%>>
                                                                                        <%=terminalVO1.getTerminalId()%> - <%=terminalVO1.getPaymentTypeName()%> - <%=!functions.isValueNull(terminalVO1.getCardType())?"":terminalVO1.getCardType() %> - <%=terminalVO1.getCurrency()%> - <%=active%>
                                                                                    </option>
                                                                                    <%
                                                                                        }
                                                                                    %>

                                                                                </select>--%>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=merchantWireReports_Is_Paid%></label>
                                        <select size="1" name="paid" class="form-control">
                                            <%
                                                if("paid".equalsIgnoreCase(paid)){%>
                                            <option value="all"><%=merchantWireReports_All%></option>
                                            <option value="paid" selected><%=merchantWireReports_Paid%></option>
                                            <option value="unpaid"><%=merchantWireReports_Unpaid%></option>
                                            <option value="carryforward" >Carryforward</option>
                                            <option value="partialPaid">Partial Paid</option>
                                            <%}
                                            else if("unpaid".equalsIgnoreCase(paid)){%>
                                            <option value="all"><%=merchantWireReports_All%></option>
                                            <option value="paid"><%=merchantWireReports_Paid%></option>
                                            <option value="unpaid" selected><%=merchantWireReports_Unpaid%></option>
                                            <option value="carryforward" >Carryforward</option>
                                            <option value="partialPaid">Partial Paid</option>
                                            <%}else if("partialPaid".equalsIgnoreCase(paid)){%>
                                            <option value="all"><%=merchantWireReports_All%></option>
                                            <option value="paid"><%=merchantWireReports_Paid%></option>
                                            <option value="unpaid" ><%=merchantWireReports_Unpaid%></option>
                                            <option value="carryforward" >Carryforward</option>
                                            <option value="partialPaid" selected>Partial Paid</option>
                                            <%}else if("carryforward".equalsIgnoreCase(paid)){%>
                                            <option value="all"><%=merchantWireReports_All%></option>
                                            <option value="paid"><%=merchantWireReports_Paid%></option>
                                            <option value="unpaid" ><%=merchantWireReports_Unpaid%></option>
                                            <option value="carryforward" selected>Carryforward</option>
                                            <option value="partialPaid">Partial Paid</option>
                                            <%}
                                            else{%>
                                            <option value="all" selected><%=merchantWireReports_All%></option>
                                            <option value="paid"><%=merchantWireReports_Paid%></option>
                                            <option value="unpaid"><%=merchantWireReports_Unpaid%></option>
                                            <option value="carryforward">Carryforward</option>
                                            <option value="partialPaid">Partial Paid</option>
                                            <%}%>
                                        </select>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label style="color: transparent;"><%=merchantWireReports_Search%></label>
                                        <button type="submit" name="B1" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-clock-o"></i>&nbsp;&nbsp;<%=merchantWireReports_Search%></button>
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
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=merchantWireReports_Summary_Data%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" style="overflow-y: auto;">
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
                            <table id="myTable" class="display table table table-striped table-bordered dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead>
                                <tr style="background-color: #7eccad !important;color: white;">
                                    <th><%=merchantWireReports_Sr_No%></th>
                                    <th><%=merchantWireReports_Partner_ID1%></th>
                                    <th><%=merchantWireReports_Merchant_ID1%></th>
                                    <th><%=merchantWireReports_Terminal_ID1%></th>
                                    <th><%=merchantWireReports_Cycle_ID%></th>
                                    <th><%=merchantWireReports_Settled_Date%></th>
                                    <th><%=merchantWireReports_First_Date%></th>
                                    <th><%=merchantWireReports_Last_Date%></th>
                                    <th>Date of Report Generation</th>
                                    <th><%=merchantWireReports_Net_Final_Amount%></th>
                                    <th><%=merchantWireReports_Unpaid_Amount%></th>
                                    <th><%=merchantWireReports_Currency%></th>
                                    <th><%=merchantWireReports_Status%></th>
                                    <th><%=merchantWireReports_Report_File%></th>
                                    <th><%=merchantWireReports_Transaction_File%></th>
                                    <th></th>
                                    <th colspan="2"><%=merchantWireReports_Action%></th>
                                    <th></th>
                                </tr>
                                </thead>
                                <%
                                    PartnerFunctions partnerfunctions = new PartnerFunctions();
                                    for(WireVO wireVO : wireReportsList)
                                    {
                                        TerminalVO TerminalVO=wireVO.getTerminalVO();
                                        String settleDate=wireVO.getSettleDate();
                                        String wiretransferConfirmationImage=wireVO.getWireTransferConfirmationImage();
                                        if(!functions.isValueNull(settleDate))
                                        {
                                            settleDate = "-";
                                        }

                                %>
                                <tbody>
                                <%
                                    out.println("<tr>");
                                    out.println("<td valign=\"middle\" data-label=\"Sr No\" align=\"center\">" + srno + "</td>");
                                    out.println("<td valign=\"middle\" data-label=\"Partner Id\" align=\"center\">" + partnerfunctions.getPartnerId(TerminalVO.getMemberId()) + "</td>");
                                    out.println("<td valign=\"middle\" data-label=\"Merchant Id\" align=\"center\">" + TerminalVO.getMemberId() + "</td>");
                                    out.println("<td valign=\"middle\" data-label=\"Terminal Id\" align=\"center\">" + TerminalVO.getTerminalId() + "</td>");
                                    out.println("<td valign=\"middle\" data-label=\"Cycle Id\" align=\"center\">" + wireVO.getSettlementCycleNo() + "</td>");
                                    out.println("<td valign=\"middle\" data-label=\"Settled Date\" align=\"center\">" + settleDate + "</td>");
                                    out.println("<td valign=\"middle\" data-label=\"First Date\" align=\"center\">" + wireVO.getFirstDate() + "</td>");
                                    out.println("<td valign=\"middle\" data-label=\"Last Date\" align=\"center\">" + wireVO.getLastDate() + "</td>");
                                    out.println("<td valign=\"middle\" data-label=\"Date of Report Generation\" align=\"center\">" + Functions.convertDtstampToDBFormat(wireVO.getDateOfReportGeneration()) + "</td>");
                                    out.println("<td valign=\"middle\" data-label=\"Net Final Amount\" align=\"center\">" + wireVO.getNetFinalAmount() + "</td>");
                                    out.println("<td valign=\"middle\" data-label=\"Unpaid Amount\" align=\"center\">" + wireVO.getUnpaidAmount() + "</td>");
                                    out.println("<td valign=\"middle\" data-label=\"Currency\" align=\"center\">" + wireVO.getCurrency() + "</td>");
                                    out.println("<td valign=\"middle\" data-label=\"Status\" align=\"center\">" + wireVO.getStatus() + "</td>");
                                    out.println("<td align=\"center\" data-label=\"Report File\"><form  action=\"/partner/net/SendFile?ctoken="+ctoken+"\" method=\"post\" onsubmit=\"return(download())\"> <button type=\"submit\" value=\""+wireVO.getSettlementReportFilePath()+"\" name=\"file\" class=\"button3\" style=\"width:100%;margin-left:0;margin-top:0px\"><img width=\"auto\" height=\"30\" border=\"0\" src=\"/partner/images/pdflogo.png\"></button></form></td>");
                                    out.println("<td align=\"center\" data-label=\"Transaction File\"><form  action=\"/partner/net/SendFile?ctoken="+ctoken+"\" method=\"post\" onsubmit=\"return(download())\"> <button type=\"submit\" value=\""+wireVO.getSettledTransactionFilePath()+"\" name=\"file\" class=\"button3\"><img width=\"auto\" height=\"30\" border=\"0\" src=\"/partner/images/excel_new.png\"></button></form></td>");
                                    if(functions.isValueNull(wiretransferConfirmationImage)){
                                        out.println("<td align=\"center\" data-label=\"Transaction File\"><form  action=\"/partner/net/SendFile?ctoken="+ctoken+"\" method=\"post\" onsubmit=\"return(download())\"> <button type=\"submit\" value=\""+wiretransferConfirmationImage+"\" name=\"file\" class=\"button3\"><img width=\"auto\" height=\"30\" border=\"0\" src=\"/partner/images/wire_transfer.png\"></button></form></td>");
                                    }else{
                                        out.println("<td align=\"center\" data-label=\"Transaction File\"></td>");
                                    }
                                    out.println("<td align=\"center\" data-label=\"Action1\" class=\"textb\"><form action=\"/partner/net/ListMerchantWireRandomCharges?ctoken="+ctoken+"\" method=\"post\"><input type=\"hidden\" name=\"memberid\" value=\""+wireVO.getMemberId()+"\"><input type=\"hidden\" name=\"terminalid\" value=\""+wireVO.getTerminalId()+"\"><input type=\"hidden\" name=\"bankwireid\" value=\""+wireVO.getSettlementCycleNo()+"\"><input type=\"submit\" class=\"btn btn-default\" value="+merchantWireReports_Random_Charges+"></form></td>");
                                    out.println("<td align=\"center\" data-label=\"Action\" class=\"textb\"><form action=\"/partner/net/ListMerchantWireEdit?ctoken="+ctoken+"\" method=\"post\"><input type=\"hidden\" name=\"fromdate\" value=\""+fromdate+"\"><input type=\"hidden\" name=\"todate\" value=\""+todate+"\"><input type=\"hidden\" name=\"memberid\" value=\""+wireVO.getMemberId()+"\"><input type=\"hidden\" name=\"terminalid\" value=\""+wireVO.getTerminalId()+"\"><input type=\"hidden\" name=\"settledid\" value=\""+wireVO.getSettleId()+"\"><input type=\"hidden\" name=\"action\" value=\"modify\"><input type=\"submit\" class=\"btn btn-default\" value="+merchantWireReports_Edit+"></form></td>");
                                    out.println("<td align=\"center\" data-label=\"Action2\" class=\"textb\"><form action=\"/partner/net/ListMerchantWireRandomCharges?ctoken="+ctoken+"\" method=\"post\"><input type=\"hidden\" name=\"memberid\" value=\""+wireVO.getMemberId()+"\"><input type=\"hidden\" name=\"terminalid\" value=\""+wireVO.getTerminalId()+"\"><input type=\"hidden\" name=\"bankwireid\" value=\""+wireVO.getSettlementCycleNo()+"\"><input type=\"hidden\" name=\"settledid\" value=\""+wireVO.getSettleId()+"\"><input type=\"hidden\" name=\"action\" value=\"delete\"><input type=\"submit\" class=\"btn btn-default\" value="+merchantWireReports_Delete+"></form></td>");

                                %>
                                </tbody>
                                <%
                                        srno++;
                                    }
                                %>
                            </table>
                        </div>
                        <% int TotalPageNo;
                            if(paginationVO.getTotalRecords()%paginationVO.getRecordsPerPage()!=0)
                            {
                                TotalPageNo=paginationVO.getTotalRecords()/paginationVO.getRecordsPerPage()+1;
                            }
                            else
                            {
                                TotalPageNo=paginationVO.getTotalRecords()/paginationVO.getRecordsPerPage();
                            }
                        %>
                        <div id="showingid"><strong><%=merchantWireReports_page_no%> <%=paginationVO.getPageNo()%> <%=merchantWireReports_of%> <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
                        <div id="showingid"><strong><%=merchantWireReports_total_no_of_records%>   <%=paginationVO.getTotalRecords()%> </strong></div>

                        <jsp:include page="page.jsp" flush="true">
                            <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
                            <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                            <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
                            <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
                            <jsp:param name="page" value="MerchantWireReports"/>
                            <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
                            <jsp:param name="orderby" value=""/>
                        </jsp:include>
                        <%
                                    }
                                    else
                                    {
                                        out.println(Functions.NewShowConfirmation1(merchantWireReports_Sorry,merchantWireReports_No_Records_Found));

                                    }
                                }
                                else
                                {
                                    out.println(Functions.NewShowConfirmation1(merchantWireReports_Sorry,merchantWireReports_No_Records_Found));
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
<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
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