.,<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 4/10/15
  Time: 1:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page errorPage="error.jsp" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.manager.vo.RecurringBillingVO" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.List" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ include file="ietest.jsp" %>
<%@ include file="Top.jsp" %>
<%
    PaginationVO paginationVO = new PaginationVO();
    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String recurringModule_Recurring_Module = StringUtils.isNotEmpty(rb1.getString("recurringModule_Recurring_Module"))?rb1.getString("recurringModule_Recurring_Module"): "Recurring Module";
    String recurringModule_rbid = StringUtils.isNotEmpty(rb1.getString("recurringModule_rbid"))?rb1.getString("recurringModule_rbid"): "Recurring Billing Id";
    String recurringModule_firstsix = StringUtils.isNotEmpty(rb1.getString("recurringModule_firstsix"))?rb1.getString("recurringModule_firstsix"): "First Six";
    String recurringModule_lastfour = StringUtils.isNotEmpty(rb1.getString("recurringModule_lastfour"))?rb1.getString("recurringModule_lastfour"): "Last Four";
    String recurringModule_trackingid = StringUtils.isNotEmpty(rb1.getString("recurringModule_trackingid"))?rb1.getString("recurringModule_trackingid"): " Tracking ID";
    String recurringModule_name = StringUtils.isNotEmpty(rb1.getString("recurringModule_name"))?rb1.getString("recurringModule_name"): "Card Holder Name";
    String recurringModule_terminalid = StringUtils.isNotEmpty(rb1.getString("recurringModule_terminalid"))?rb1.getString("recurringModule_terminalid"): " Terminal ID";
    String recurringModule_All = StringUtils.isNotEmpty(rb1.getString("recurringModule_All"))?rb1.getString("recurringModule_All"): "All";
    String recurringModule_NoTerminal = StringUtils.isNotEmpty(rb1.getString("recurringModule_NoTerminal"))?rb1.getString("recurringModule_NoTerminal"): "No Terminals Allocated";
    String recurringModule_Search = StringUtils.isNotEmpty(rb1.getString("recurringModule_Search"))?rb1.getString("recurringModule_Search"): "Search";
    String recurringModule_Recurring_Details = StringUtils.isNotEmpty(rb1.getString("recurringModule_Recurring_Details"))?rb1.getString("recurringModule_Recurring_Details"): "Recurring Details";
    String recurringModule_SrNo = StringUtils.isNotEmpty(rb1.getString("recurringModule_SrNo"))?rb1.getString("recurringModule_SrNo"): "Sr.No";
    String recurringModule_First_Transaction_ID = StringUtils.isNotEmpty(rb1.getString("recurringModule_First_Transaction_ID"))?rb1.getString("recurringModule_First_Transaction_ID"): "First Transaction ID";
    String recurringModule_Recurring_Subscription_Date = StringUtils.isNotEmpty(rb1.getString("recurringModule_Recurring_Subscription_Date"))?rb1.getString("recurringModule_Recurring_Subscription_Date"): "Recurring Subscription Date";
    String recurringModule_Card_Holder_Name = StringUtils.isNotEmpty(rb1.getString("recurringModule_Card_Holder_Name"))?rb1.getString("recurringModule_Card_Holder_Name"): "Search";
    String recurringModule_First_Six = StringUtils.isNotEmpty(rb1.getString("recurringModule_First_Six"))?rb1.getString("recurringModule_First_Six"): " First Six";
    String recurringModule_Last_Four = StringUtils.isNotEmpty(rb1.getString("recurringModule_Last_Four"))?rb1.getString("recurringModule_Last_Four"): "Last Four";
    String recurringModule_Recurring_Type = StringUtils.isNotEmpty(rb1.getString("recurringModule_Recurring_Type"))?rb1.getString("recurringModule_Recurring_Type"): "Recurring Type";
    String recurringModule_Recurring_Status = StringUtils.isNotEmpty(rb1.getString("recurringModule_Recurring_Status"))?rb1.getString("recurringModule_Recurring_Status"): " Recurring Status";
    String recurringModule_Terminal_Id = StringUtils.isNotEmpty(rb1.getString("recurringModule_Terminal_Id"))?rb1.getString("recurringModule_Terminal_Id"): " Terminal Id";
    String recurringModule_Action = StringUtils.isNotEmpty(rb1.getString("recurringModule_Action"))?rb1.getString("recurringModule_Action"): "Action";
    String recurringModule_Showing_Page = StringUtils.isNotEmpty(rb1.getString("recurringModule_Showing_Page"))?rb1.getString("recurringModule_Showing_Page"): "Showing Page";
    String recurringModule_records = StringUtils.isNotEmpty(rb1.getString("recurringModule_records"))?rb1.getString("recurringModule_records"): "records";
    String recurringModule_Sorry = StringUtils.isNotEmpty(rb1.getString("recurringModule_Sorry"))?rb1.getString("recurringModule_Sorry"): "Sorry";
    String recurringModule_No_records = StringUtils.isNotEmpty(rb1.getString("recurringModule_No_records"))?rb1.getString("recurringModule_No_records"): "No records found for given search criteria.";
    String recurringModule_Filter = StringUtils.isNotEmpty(rb1.getString("recurringModule_Filter"))?rb1.getString("recurringModule_Filter"): "Filter";
    String recurringModule_Msg = StringUtils.isNotEmpty(rb1.getString("recurringModule_Msg"))?rb1.getString("recurringModule_Msg"): "Please provide the data to get List of Recurring Transactions.";
    String recurringModule_rebill = StringUtils.isNotEmpty(rb1.getString("recurringModule_rebill"))?rb1.getString("recurringModule_rebill"): "Rebill";
    String recurringModule_page_no=StringUtils.isNotEmpty(rb1.getString("recurringModule_page_no"))?rb1.getString("recurringModule_page_no"):"Page number";
    String recurringModule_total_no_of_records=StringUtils.isNotEmpty(rb1.getString("recurringModule_total_no_of_records"))?rb1.getString("recurringModule_total_no_of_records"):"Total number of records";



    String str = "";
    String terminalid ="";
    String rbid = Functions.checkStringNull(request.getParameter("rbid"));
    String firstsix = Functions.checkStringNull(request.getParameter("firstsix"));
    String lastfour = Functions.checkStringNull(request.getParameter("lastfour"));
    String trackingid = Functions.checkStringNull(request.getParameter("trackingid"));
    terminalid = Functions.checkStringNull(request.getParameter("terminalid"));
    String pTerminalBuffer = Functions.checkStringNull(request.getParameter("terminalBuffer"));
    String cardname = Functions.checkStringNull(request.getParameter("name"));

    if (rbid != null) str = str + "&rbid=" + rbid;
    if (firstsix != null) str = str + "&firstsix=" + firstsix;
    if (lastfour != null) str = str + "&lastfour=" + lastfour;
    if (trackingid != null) str = str + "&trackingid=" + trackingid;
    if (terminalid != null) str = str + "&terminalid=" + terminalid;
    if (cardname != null) str = str + "&name=" + cardname;
    if (pTerminalBuffer != null) str = str + "&terminalBuffer=" + pTerminalBuffer;

    str = str + "&SRecords=" + paginationVO.getRecordsPerPage();

    int pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
    int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

%>

<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    session.setAttribute("submit","Recurring");
%>
<html>
<head>

    <script language="JavaScript" src="/merchant/FusionCharts/FusionCharts.js?ver=1"></script>
    <link rel="stylesheet" type="text/css" href="/merchant/FusionCharts/style.css" >
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
    <%--    <script>
            $(document).ready(function(){
                $('#myTable').dataTable();
            });
        </script>--%>
    <script type="text/javascript">
        function deleteRBID()
        {
            if(confirm("Do you want to delete the recurring billing?"))
            {
                return true;
            }
            return false;
        }

        function activateRBID()
        {
            if(confirm("Do you want to activate the recurring billing?"))
            {
                return true;
            }
            return false;
        }

        function deactivateRBID()
        {
            if(confirm("Do you want to deactivate the recurring billing?"))
            {
                return true;
            }
            return false;
        }
    </script>
    <%--       <style type="text/css">

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
<title> <%=company%> Merchant Recurring Billing Module</title>

<body class="pace-done widescreen fixed-left-void ">
<%--<div class="rowcontainer-fluid " >
    <div class="row rowadd" >
        <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">
            <div class="form foreground bodypanelfont_color panelbody_color">
                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="color:#34495e" ><i class="fa fa-th-large"></i>&nbsp;&nbsp;&lt;%&ndash;<%=company%>&ndash;%&gt; Recurring Module</h2>
                <hr class="hrform">
            </div>--%>

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">


            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=recurringModule_Recurring_Module%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">

                                <form action="/merchant/servlet/RecurringModule?ctoken=<%=ctoken%>" name="form" method="post" >
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                    <%
                                        String uId = "";
                                        if(session.getAttribute("role").equals("submerchant"))
                                        {
                                            uId = (String) session.getAttribute("userid");
                                        }
                                        else
                                        {
                                            uId = (String) session.getAttribute("merchantid");
                                        }
                                        terminalid="";
                                        if(request.getAttribute("terminalid")!=null)
                                        {
                                            terminalid = request.getAttribute("terminalid").toString();
                                        }

                                    %>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label ><%=recurringModule_rbid%></label>
                                        <input name="rbid" size="10" class="form-control">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label ><%=recurringModule_firstsix%></label>
                                        <input name="firstsix" size="10" class="form-control">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label ><%=recurringModule_lastfour%></label>
                                        <input name="lastfour" size="10" class="form-control">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label ><%=recurringModule_trackingid%></label>
                                        <input name="trackingid" size="10" class="form-control">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label ><%=recurringModule_name%></label>
                                        <input name="name" size="10" class="form-control">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=recurringModule_terminalid%></label>
                                        <select size="1" name="terminalid" class="form-control">
                                            <%
                                                StringBuffer terminalBuffer = new StringBuffer();
                                                terminalBuffer.append("(");
                                                TerminalManager terminalManager=new TerminalManager();
                                                List<TerminalVO> terminalVOList=terminalManager.getMemberandUserTerminalList(uId, String.valueOf(session.getAttribute("role")));

                                                if(terminalVOList.size()>0)
                                                {
                                            %>
                                            <option value="all"><%=recurringModule_All%></option>
                                            <%

                                                for (TerminalVO terminalVO:terminalVOList)
                                                {
                                                    String str1 = "";
                                                    String active = "";
                                                    if (terminalVO.getIsActive().equalsIgnoreCase("Y"))
                                                    {
                                                        active = "Active";
                                                    }
                                                    else
                                                    {
                                                        active = "InActive";
                                                    }
                                                    if(terminalid.equals(terminalVO.getTerminalId()))
                                                    {
                                                        str1= "selected";
                                                    }
                                                    if(terminalBuffer.length()!=0 && terminalBuffer.length()!=1)
                                                    {
                                                        terminalBuffer.append(",");
                                                    }
                                                    terminalBuffer.append(terminalVO.getTerminalId());
                                            %>
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(terminalVO.getTerminalId())%>" <%=str1%>> <%=ESAPI.encoder().encodeForHTML(terminalVO.getTerminalId()+"-"+terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+terminalVO.getCurrency()+"-"+active)%> </option>
                                            <%
                                                }
                                                terminalBuffer.append(")");
                                            }
                                            else
                                            {
                                            %>
                                            <option value="NoTerminal"><%=recurringModule_NoTerminal%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>

                                    <%--<div class="form-group col-md-9 has-feedback">&nbsp;</div>--%>
                                    <div class="form-group col-md-4">
                                    </div>
                                    <div class="form-group col-md-4">
                                    </div>

                                    <div class="form-group col-md-4">
                                        <button type="submit" class="btn btn-default">
                                            <i class="fa fa-save"></i>
                                            &nbsp;&nbsp;<%=recurringModule_Search%>
                                        </button>

                                    </div>

                                    <input type="hidden" value="<%=terminalBuffer.toString()%>" name="terminalbuffer">
                                    <input type="hidden" name="terminalid" value="<%=terminalid%>">
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row reporttable">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=recurringModule_Recurring_Details%></strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding">

                            <%
                                if(request.getAttribute("recurringBillingVO")!=null)
                                {
                                    List<RecurringBillingVO> rbList = (List<RecurringBillingVO>) request.getAttribute("recurringBillingVO");
                                    paginationVO =(PaginationVO) request.getAttribute("paginationVO");
                                    paginationVO.setInputs(paginationVO.getInputs() + "&ctoken="+ctoken);
                                    paginationVO.setInputs(paginationVO.getInputs() + "&terminalbuffer="+request.getParameter("terminalbuffer"));

                                    if(rbList.size() > 0)
                                    {
                                        int srno=((paginationVO.getPageNo()-1)*paginationVO.getRecordsPerPage())+1;

                            %>

                            <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead>
                                <tr style="color: white;">
                                    <th ><%=recurringModule_SrNo%></th>
                                    <th ><%=recurringModule_First_Transaction_ID%></th>
                                    <th ><%=recurringModule_Recurring_Subscription_Date%></th>
                                    <%--<td  align="center" class="th0">First<br>Transaction ID</td>--%>
                                    <th ><%=recurringModule_Card_Holder_Name%></th>
                                    <th ><%=recurringModule_First_Six%></th>
                                    <th ><%=recurringModule_Last_Four%></th>
                                    <%--<td  align="center" class="th0">Interval</td>
                                    <td  align="center" class="th0">Frequency</td>
                                    <td  align="center" class="th0">Run Date</td>--%>
                                    <th ><%=recurringModule_Recurring_Type%></th>
                                    <th ><%=recurringModule_Recurring_Status%></th>
                                    <th ><%=recurringModule_Terminal_Id%></th>
                                    <th colspan = "4" style="text-align: center;" ><%=recurringModule_Action%></th>
                                </tr>
                                </thead>
                                <tbody>
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">

                                <%

                                    StringBuffer requestParameter= new StringBuffer();
                                    Enumeration<String> requestName=request.getParameterNames();
                                    while(requestName.hasMoreElements())
                                    {
                                        String name=requestName.nextElement();
                                        if("SPageno".equals(name) || "SRecords".equals(name))
                                        {

                                        }
                                        else
                                            requestParameter.append("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                                    }
                                    requestParameter.append("<input type='hidden' name='SPageno' value='"+pageno+"'/>");
                                    requestParameter.append("<input type='hidden' name='SRecords' value='"+pagerecords+"'/>");

                                    String style="class=td1";
                                    String ext="light";
                                    int pos = 1;
                                    Functions functions = new Functions();
                                    for(RecurringBillingVO recurringBillingVO : rbList)
                                    {
                                        if(pos%2==0)
                                        {
                                            style="class=tr0";
                                            ext="dark";
                                        }
                                        else
                                        {
                                            style="class=tr1";
                                            ext="light";
                                        }

                                        String origTrackingid = ESAPI.encoder().encodeForHTML(recurringBillingVO.getOriginTrackingId());
                                        String recInterval = ESAPI.encoder().encodeForHTML(recurringBillingVO.getInterval());
                                        String recFrequency = ESAPI.encoder().encodeForHTML(recurringBillingVO.getFrequency());
                                        String recRunDate = ESAPI.encoder().encodeForHTML(recurringBillingVO.getRunDate());
                                        rbid = recurringBillingVO.getRecurring_subscrition_id();

                                        String recStatus = ESAPI.encoder().encodeForHTML(recurringBillingVO.getRecurringStatus());
                                        String recAmount = ESAPI.encoder().encodeForHTML(recurringBillingVO.getAmount());
                                        String registerDate = ESAPI.encoder().encodeForHTML(recurringBillingVO.getRecurringRegisterDate());
                                        String recurringType = ESAPI.encoder().encodeForHTML(recurringBillingVO.getRecurringType());
                                        String name = ESAPI.encoder().encodeForHTML(recurringBillingVO.getCardHolderName());
                                        String firstSix = ESAPI.encoder().encodeForHTML(recurringBillingVO.getFirstSix());
                                        String lastFour = ESAPI.encoder().encodeForHTML(recurringBillingVO.getLastFour());
                                        terminalid = ESAPI.encoder().encodeForHTML(recurringBillingVO.getTerminalid());

                                        if(functions.isValueNull(recurringBillingVO.getCardHolderName()))
                                            name = ESAPI.encoder().encodeForHTML(recurringBillingVO.getCardHolderName());
                                        if(functions.isValueNull(recurringBillingVO.getFirstSix()))
                                            firstSix = ESAPI.encoder().encodeForHTML(recurringBillingVO.getFirstSix());
                                        if(functions.isValueNull(recurringBillingVO.getLastFour()))
                                            lastFour = ESAPI.encoder().encodeForHTML(recurringBillingVO.getLastFour());

                                        out.println("<tr>");
                                        out.println("<td align=center data-label=\"Sr.No\">" +srno+"</td>");
                                        if(!functions.isValueNull(rbid))
                                        {
                                            rbid = "";
                                        }
                                        if(!functions.isValueNull(firstsix))
                                        {
                                            firstsix="";
                                        }
                                        if(!functions.isValueNull(lastfour))
                                        {
                                            lastfour="";
                                        }
                                        if(!functions.isValueNull(trackingid))
                                        {
                                            trackingid="";
                                        }
                                        if(!functions.isValueNull(name))
                                        {
                                            name="";
                                        }
                                        if(!functions.isValueNull(terminalid))
                                        {
                                            terminalid="";
                                        }
                                        if (!functions.isValueNull(recurringType))
                                        {
                                            recurringType= "";
                                        }
                                        out.println("<td align=center data-label=\"Recurring Billing\"><form action=\"RbidDetails?ctoken=" + ctoken + "\" method=\"post\"><input type=\"hidden\" name=\"rbid\" value=\"" + rbid + "\"><input type=\"hidden\" name=\"terminalid\" value=\"" + terminalid + "\"><input type=\"hidden\" name=\"SPageno\" value=\"" + pageno + "\" ><input type=\"hidden\" name=\"trackingid\" value=\"" + origTrackingid + "\" ><input type=\"hidden\" name=\"lastfour\" value=\"" + lastfour + "\" ><input type=\"submit\" class=\"btn btn-default\" name=\"submit\" value=\"" + origTrackingid + "\"></form></td>");

                                        out.println(requestParameter);
                                        out.println("</form></td>");

                                        if (!functions.isValueNull(recurringType))
                                        {
                                            out.println("<td align=center "+style+">"+"-"+"</td>");
                                        }
                                        else
                                        {
                                            out.println("<td align=center data-label=\"Recurring Subscription Date\">"+registerDate+"</td>");
                                        }
                                        //out.println("<td align=center "+style+">" +origTrackingid+"<input type=\"hidden\" name=\"trackingid\" value=\""+origTrackingid+"\""+"></td>");
                                        out.println("<td align=center data-label=\"Card Holder Name\">" +name+"<input type=\"hidden\" name=\"name\" value=\""+name+"\""+"></td>");

                                        if (recurringBillingVO.getFirstSix()!=null)
                                        {
                                            out.println("<td align=center data-label=\"First Six\">" +firstSix+"<input type=\"hidden\" name=\"firstsix\" value=\""+firstSix+"\""+"></td>");
                                        }
                                        else if (recurringBillingVO.getFirstSix()==null)
                                        {
                                            out.println("<td align=center data-label=\"First Six\">" +"-"+"<input type=\"hidden\" name=\"firstsix\" value=\""+"-"+"\""+"></td>");
                                        }

                                        if (recurringBillingVO.getLastFour() !=null)
                                        {
                                            out.println("<td align=center data-label=\"Last Four\">" +lastFour+"<input type=\"hidden\" name=\"lastfour\" value=\""+lastFour+"\""+"></td>");
                                        }

                                        else if (recurringBillingVO.getLastFour()==null)
                                        {
                                            out.println("<td align=center data-label=\"Last Four\">" +"-"+"<input type=\"hidden\" name=\"lastfour\" value=\""+"-"+"\""+"></td>");
                                        }

                                        out.println("<td align=center data-label=\"Recurring Type\">" +recurringType+"<input type=\"hidden\" name=\"recurringType\" value=\""+recurringType+"\""+"></td>");
                                        //out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML(recurringBillingVO.getRbid())+"<input type=\"hidden\" name=\"rbid\" value=\""+recurringBillingVO.getRbid()+"\""+"</td>");
                                        out.println("<td align=center data-label=\"Recurring Status\">" +recStatus+"<input type=\"hidden\" name=\"recStatus\" value=\""+recStatus+"\""+"></td>");
                                        out.println("<td align=center data-label=\"Terminal Id\">" +terminalid+"<input type=\"hidden\" name=\"terminalid\" value=\""+terminalid+"\""+"></td>");
                                        String buttonType = "";
                                        String buttonType1 = "";
                                        String buttonClass = "btn btn-default";
                                        String buttonClass1 = "btn btn-default";
                                        if ("Manual".equalsIgnoreCase(recurringType))
                                        {
                                            buttonClass = "btn btn-default";
                                            buttonType = "disabled";
                                        }

                                        if ("Automatic".equalsIgnoreCase(recurringType))
                                        {
                                            buttonClass1 = "btn btn-default";
                                            buttonType1 = "disabled";
                                        }

                                        out.println("<td data-label=\"Action\" align=\"center\"><form action=\"/merchant/modifyRecurringBilling.jsp?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"rbid\" value=\""+rbid+"\"><input type=\"hidden\" name=\"trackingid\" value=\""+origTrackingid+"\"><button type=\"submit\" name=\"submit\" "+buttonType+" class=\""+buttonClass+"\" value=\"Update\"><input type=\"hidden\" name=\"interval\" value=\""+recInterval+"\"><input type=\"hidden\" name=\"frequency\" value=\""+recFrequency+"\"><input type=\"hidden\" name=\"rundate\" value=\""+recRunDate+"\"><input type=\"hidden\" name=\"amount\" value=\""+recAmount+"\"><input type=\"hidden\" name=\"action\" value=\"update\"><i class=\"fa fa-edit\"></i></button></form></td>");
                                        out.println(requestParameter);
                                        out.println("</form></td>");
                                        out.println("<td "+style+" align=\"center\"><form action=\"ModifyRecurringBilling?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"rbid\" value=\""+rbid+"\"><input type=\"hidden\" name=\"trackingid\" value=\""+origTrackingid+"\"><button type=\"submit\" name=\"submit\" "+buttonType+" class=\""+buttonClass+"\" value=\"Delete\" onClick=\"return deleteRBID()\" ><input type=\"hidden\" name=\"action\" value=\"delete\"><i class=\"fa fa-times fa fa-white\"></i></button></form></td>");

                                        if("deactivated".equalsIgnoreCase(recStatus))
                                        {
                                            out.println("<td data-label=\"Action\" align=\"center\"><form action=\"ModifyRecurringBilling?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"rbid\" value=\""+rbid+"\"><input type=\"hidden\" name=\"trackingid\" value=\""+origTrackingid+"\"><input type=\"submit\" "+buttonType+" name=\"submit\" class=\""+buttonClass+"\" value=\"Activate\" onClick=\"return activateRBID()\"><input type=\"hidden\" name=\"rStatus\" value=\"1\"><input type=\"hidden\" name=\"action\" value=\"active\"></form></td>");
                                        }
                                        else if("activated".equalsIgnoreCase(recStatus))
                                        {
                                            out.println("<td data-label=\"Action\" align=\"center\"><form action=\"ModifyRecurringBilling?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"rbid\" value=\""+rbid+"\"><input type=\"hidden\" name=\"trackingid\" value=\""+origTrackingid+"\"><input type=\"submit\" name=\"submit\" "+buttonType+" class=\""+buttonClass+"\" value=\"Deactive\" onClick=\"return deactivateRBID()\"><input type=\"hidden\" name=\"rStatus\" value=\"0\"><input type=\"hidden\" name=\"action\" value=\"active\"></form></td>");
                                        }

                                        out.println("<td data-label=\"Action\" align=\"center\"><form action=\"/merchant/manualRebill.jsp?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"rbid\" value=\""+rbid+"\"><input type=\"hidden\" name=\"terminalbuffer\" value=\""+pTerminalBuffer+"\"><input type=\"hidden\" name=\"name\" value=\""+name+"\"><input type=\"hidden\" name=\"firstsix\" value=\""+firstSix+"\"><input type=\"hidden\" name=\"lastfour\" value=\""+lastFour+"\"><input type=\"hidden\" name=\"trackingid\" value=\""+origTrackingid+"\"><input type=\"submit\" name=\"submit\" "+buttonType1+" class=\""+buttonClass1+"\" value="+recurringModule_rebill+" ><input type=\"hidden\" name=\"action\" value=\"rebill\">");
                                        out.println(requestParameter);
                                        out.println("</form></td>");
                                        out.println("</tr>");
                                        srno++;

                                    }
                                %>


                                </tbody>
                            </table>

                        </div>

                    </div>
                </div>
            </div>

            <%
                int TotalPageNo;
                if(paginationVO.getTotalRecords()%paginationVO.getRecordsPerPage()!=0)
                {
                    TotalPageNo =paginationVO.getTotalRecords()/paginationVO.getRecordsPerPage()+1;
                }
                else
                {
                    TotalPageNo=paginationVO.getTotalRecords()/paginationVO.getRecordsPerPage();
                }
            %>
            <div id="showingid"><strong><%=recurringModule_page_no%> <%=pageno%>  of  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
            <div id="showingid"><strong><%=recurringModule_total_no_of_records%>   <%=paginationVO.getTotalRecords()%> </strong></div>

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
                        out.println(Functions.NewShowConfirmation1(recurringModule_Sorry,recurringModule_No_records ));
                    }
                }
                else
                {

                    String error=(String) request.getAttribute("error");
                    if( error!= null)
                    {
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                    }
                    else
                        out.println(Functions.NewShowConfirmation1(recurringModule_Filter,recurringModule_Msg));
                }
            %>


        </div>
    </div>
</div>
</body>
</html>