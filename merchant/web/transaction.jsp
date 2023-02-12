<%@ page errorPage="error.jsp" import="com.directi.pg.Functions,com.directi.pg.TransactionEntry,
                 com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.enums.TemplatePreference" %>
<%@ page import="com.directi.pg.ActionEntry" %>
<%--<%@include file="ietest.jsp"%>--%>
<%@ include file="Top.jsp" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    session.setAttribute("submit","Transactions");
%>
<%
    ActionEntry entry = new ActionEntry();
    String uId = "";
    if(session.getAttribute("role").equals("submerchant"))
    {
        uId = (String) session.getAttribute("userid");
    }
    else
    {
        uId = (String) session.getAttribute("merchantid");
    }

    String currency = (String) session.getAttribute("currency");
    String bank = (String) session.getAttribute("bank");
    TransactionEntry transactionentry = (TransactionEntry) session.getAttribute("transactionentry");
    //  Hashtable statushash = transactionentry.getStatusHash();

    SortedMap sortedMap = transactionentry.getSortedMap();

    String desc = Functions.checkStringNull(request.getParameter("desc")) == null ? "" : request.getParameter("desc");
    String customerId = Functions.checkStringNull(request.getParameter("customerid")) == null ? "" : request.getParameter("customerid");
    String trackingid = Functions.checkStringNull(request.getParameter("trackingid"))==null?"":request.getParameter("trackingid");
    String userTerminals = Functions.checkStringNull(request.getParameter("terminalbuffer"))==null?"":request.getParameter("terminalbuffer");

    String fromdate = null;
    String todate = null;

/*
    try
    {
        fromdate = ESAPI.validator().getValidInput("fdate",request.getParameter("fdate"),"fromDate",10,true);
        todate = ESAPI.validator().getValidInput("tdate",request.getParameter("tdate"),"fromDate",10,true);
        */
/*fmonth = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
        tmonth =  ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
        fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
        tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);*//*

    }
    catch(ValidationException e)
    {

    }
*/

    Date date = new Date();
    SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

    String Date = originalFormat.format(date);
    date.setDate(1);
    String fromDate = originalFormat.format(date);

    fromdate = Functions.checkStringNull(request.getParameter("fdate")) == null ? fromDate : request.getParameter("fdate");
    todate = Functions.checkStringNull(request.getParameter("tdate")) == null ? Date : request.getParameter("tdate");

    String startTime = Functions.checkStringNull(request.getParameter("starttime"));
    String endTime = Functions.checkStringNull(request.getParameter("endtime"));
    if (startTime == null) startTime = "00:00:00";
    if (endTime == null) endTime = "23:59:59";

   /* String fdate=null;
    String tdate=null;
    String fmonth=null;
    String tmonth=null;
    String fyear=null;
    String tyear=null;*/
    String terminalid=null;
    String paymentId=null;
    String firstName=Functions.checkStringNull(request.getParameter("firstname"));
    String lastName=Functions.checkStringNull(request.getParameter("lastname"));
    String emailAddress=Functions.checkStringNull(request.getParameter("emailaddr"));
    paymentId = Functions.checkStringNull(request.getParameter("paymentid"))==null?"":request.getParameter("paymentid");
    terminalid =Functions.checkStringNull(request.getParameter("terminalid"))==null?"":request.getParameter("terminalid");
    String dateType = Functions.checkStringNull(request.getParameter("datetype"));
    if (dateType == null) dateType = "";
    /*terminalid = request.getParameter("terminalid");*/


    String status = Functions.checkStringNull(request.getParameter("status"));

    Calendar rightNow = Calendar.getInstance();
    String str = "";
    if (fromdate == null) fromdate = "" + 1;
    if (todate == null) todate = "" + rightNow.get(Calendar.DATE);
    /*if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
    if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
    if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
    if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);*/
    String currentyear = "" + rightNow.get(Calendar.YEAR);

    if (fromdate != null) str = str + "fdate=" + fromdate;
    if (todate != null) str = str + "&tdate=" + todate;
    /*if (fmonth != null) str = str + "&fmonth=" + fmonth;
    if (tmonth != null) str = str + "&tmonth=" + tmonth;
    if (fyear != null) str = str + "&fyear=" + fyear;
    if (tyear != null) str = str + "&tyear=" + tyear;*/
    if (desc != null) str = str + "&desc=" + desc;
    if (dateType != null) str = str + "&datetype=" + dateType;
    if (startTime != null) str = str + "&starttime=" + startTime;
    if (endTime != null) str = str + "&endtime=" + endTime;

    if (customerId != null) str = str + "&customerid=" + customerId;
    if (trackingid != null) str = str + "&trackingid=" + trackingid;
    if(firstName!=null)str = str + "&firstname=" + firstName;
    else
        firstName="";
    if(lastName!=null)str = str + "&lastname=" + lastName;
    else
        lastName="";
    if(emailAddress!=null)str = str + "&emailaddr=" + emailAddress;
    else
        emailAddress="";
    if(paymentId!=null) str=str+"&paymentid="+paymentId;
    if(terminalid!=null)str =str + "&terminalid=" + terminalid;
    else
        terminalid="";
    if(userTerminals!=null)str =str + "&terminalbuffer=" + userTerminals;
    else
        userTerminals="";

    str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    boolean archive = Boolean.valueOf(request.getParameter("archive")).booleanValue();
    str = str + "&archive=" + archive;
    str = str+"&status="+request.getParameter("status");
    String archivalString = "Archived";
    String currentString = "Current";
    String selectedArchived = "", selectedCurrent = "";
    if (archive)
    {
        selectedArchived = "selected";
        selectedCurrent = "";
    }
    else
    {
        selectedArchived = "";
        selectedCurrent = "selected";
    }
    /*int pageno =1;
    int pagerecords=30;
    try
    {
        pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Spageno",request.getParameter("SPageno"),"Numbers",3,true), 1);
        pagerecords = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord",request.getParameter("SRecords"),"Numbers",3,true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
    }
    catch(ValidationException e)
    {
        pageno = 1;
        pagerecords = 30;
    }*/

    //int pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
    int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
    int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

    str = str + "&SRecords=" + pagerecords;
    str = str + "&bank=" + bank;

%>
<input type="hidden" id="pageno" name="pageno" value="<%=pageno%>">
<link rel="stylesheet" href=" /merchant/transactionCSS/css/transactions.css">
<html lang="en">
<head>

    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
    <script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>

    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>

    <script src="/merchant/datepicker/datetimepicker/moment-with-locales.js"></script>
    <%--<link href="/merchant/datepicker/datetimepicker/bootstrap-datetimepicker.css" rel="stylesheet">--%>
    <script src="/merchant/datepicker/datetimepicker/bootstrap-datetimepicker.min.js"></script>

    <title><%=company%> Merchant Transactions</title>

    <script src="/merchant/transactionCSS/js/transactions.js"></script>

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
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Transactions</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>



                        <form name="form" method="post" action="/merchant/servlet/Transactions?ctoken=<%=ctoken%>">

                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                            <%
                                StringBuffer terminalBuffer = new StringBuffer();
                                if("ERR".equals(request.getParameter("MES")))
                                {
                                    ValidationErrorList error = (ValidationErrorList) request.getAttribute("validationErrorList");
                                    for (ValidationException errorList : error.errors())
                                    {
                                        //out.println("<center><li class=\"alert alert-danger alert-dismissable\"><b>" + errorList.getMessage() + "</b></li></center>");
                                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errorList.getMessage() + "</h5>");
                                    }
                                }

                                if(request.getAttribute("error")!=null)
                                {
                                    //out.println("<center><li class=\"alert alert-danger alert-dismissable\"><b>" + (String)request.getAttribute("error") + "</b></li></center>");
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + request.getAttribute("error") + "</h5>");

                                }
                            %>
                            <div class="widget-content padding">
                                <div id="horizontal-form">

                                    <%-- <div class="form-group col-md-3 has-feedback">
                                         <label>From</label>
                                         <input type="text" size="16" name="fdate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                     </div>

                                     <div class="form-group col-md-3 has-feedback">
                                         <label>To</label>
                                         <input type="text" size="16" name="tdate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(todate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                     </div>--%>


                                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-4">

                                        <div class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                                            <label>From</label>
                                            <input type="text" name="fdate" id="From_dt" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">
                                        </div>



                                        <div <%--id="From_div"--%> class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-4" style="padding: 0;/*width: inherit;*/">
                                            <div class="form-group">
                                                <label class="hide_label">From</label>
                                                <div class='input-group date' >
                                                    <input type='text' id='datetimepicker12' class="form-control" placeholder="HH:MM:SS" name="starttime" maxlength="8" value="<%=startTime%>" style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                                                    <%--<div id="datetimepicker12"></div>--%>
                                                </div>
                                            </div>
                                        </div>

                                    </div>


                                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-4">

                                        <div class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                                            <label>To</label>
                                            <input type="text" name="tdate" id="To_dt" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(todate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">

                                        </div>


                                        <div <%--id="From_div"--%> class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-4" style="padding: 0;/*width: inherit;*/">
                                            <div class="form-group">
                                                <label class="hide_label">To</label>
                                                <div class='input-group date' >
                                                    <input type='text' id='datetimepicker13' class="form-control" placeholder="HH:MM:SS" name="endtime" maxlength="8" value="<%=endTime%>" style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                                                </div>
                                            </div>
                                        </div>

                                    </div>

                                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-2 has-feedback">
                                        <label >Tracking ID</label>
                                        <input type=text name="trackingid"  maxlength="20" class="form-control" size="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>" >
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-2 has-feedback" <%--style="margin-left: 96px;"--%>>
                                        <label>Terminal ID</label>
                                        <select size="1" name="terminalid" class="form-control">
                                            <%
                                                terminalBuffer.append("(");
                                                TerminalManager terminalManager=new TerminalManager();
                                                List<TerminalVO> terminalVOList=terminalManager.getMemberandUserTerminalList(uId, String.valueOf(session.getAttribute("role")));
                                                if(terminalVOList.size()>0)
                                                {
                                            %>
                                            <option value="all">All</option>
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
                                            <option value="NoTerminals">No Terminals Allocated</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-12 has-feedback" style="margin:0; padding: 0;"></div>

                                    <div class="form-group col-sm-12 col-md-3 has-feedback" style="display: flow-root;">
                                        <label>Order Id</label>
                                        <input type=text name="desc" maxlength="100"  class="form-control"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>">
                                    </div>

                                    <div class="form-group col-sm-12 col-md-3 has-feedback">
                                        <label>Customer Id</label>
                                        <input type=text name="customerid" maxlength="100"  class="form-control"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(customerId)%>">
                                    </div>

                                    <div class="form-group col-sm-12 col-md-3 has-feedback">
                                        <label >First Name</label>
                                        <input type=text name="firstname" maxlength="100"  class="form-control"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(firstName)%>">
                                    </div>

                                    <div class="form-group col-sm-12 col-md-3 has-feedback">
                                        <label>Last Name</label>
                                        <input type=text name="lastname" maxlength="100"  class="form-control"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(lastName)%>">
                                    </div>

                                    <div class="form-group col-sm-12 col-md-3 has-feedback">
                                        <label>Data Source</label>
                                        <select size="1" name="archive" class="form-control" >
                                            <option value="true" <%=selectedArchived%>>Archives</option>
                                            <option value="false" <%=selectedCurrent%>>Current</option>
                                        </select>
                                    </div>

                                    <div class="form-group col-sm-12 col-md-3 has-feedback">
                                        <label>Email ID</label>
                                        <input type="text"   class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(emailAddress)%>" name="emailaddr">
                                    </div>

                                    <%--<div class="form-group col-md-3 has-feedback">
                                        <label>Auth Code</label>
                                        <input type="text" size="15" class="form-control" name="paymentid" size="2" value="<%=ESAPI.encoder().encodeForHTMLAttribute(paymentId)%>">
                                    </div>--%>

                                    <div class="form-group col-sm-12 col-md-3 has-feedback">
                                        <label >Rows/Pages</label>
                                        <input type="text" maxlength="3"  size="15" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(new Integer(pagerecords).toString())%>" name="SRecords" size="2" class="textBoxes" />
                                    </div>

                                    <div class="form-group col-sm-12 col-md-3 has-feedback">
                                        <label >Status</label>
                                        <select size="1" name="status"  class="form-control" >
                                            <option value="">All</option>
                                            <%
                                                if(sortedMap.size()>0)
                                                {
                                                    Set<String> setStatus = sortedMap.entrySet();
                                                    Iterator i = setStatus.iterator();
                                                    while(i.hasNext())
                                                    {
                                                        Map.Entry entryMap =(Map.Entry) i.next();
                                                        String status1 = (String) entryMap.getKey();
                                                        String status2 =(String) entryMap.getValue();

                                                        String select = "";
                                                        if(status1.equalsIgnoreCase(status))
                                                        {
                                                            select = "selected";
                                                        }
                                            %>
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(status1)%>" <%=select%>>
                                                <%=ESAPI.encoder().encodeForHTML(status2)%>
                                            </option>
                                            <%
                                                    }
                                                }
                                            %>
                                        </select>
                                    </div>

                                    <%-- <div class="form-group col-md-3">
                                     </div>
                                     <div class="form-group col-md-3">
                                     </div>
                                     <div class="form-group col-md-3">
                                     </div>
                                     <div class="form-group col-md-3">--%>

                                    <div class="form-group col-sm-12 col-md-3 has-feedback">
                                        <label >Date Type</label>
                                        <select size="1" name="datetype" class="form-control">
                                            <%
                                                if("TIMESTAMP".equals(dateType))
                                                {%>
                                            <option value="DTSTAMP">Transaction Date</option>
                                            <option value="TIMESTAMP" SELECTED>Last Updated Date</option>
                                            <%}
                                            else
                                            {%>
                                            <option value="DTSTAMP" SELECTED>Transaction Date</option>
                                            <option value="TIMESTAMP">Last Updated Date</option>
                                            <%}
                                            %>
                                        </select>
                                    </div>

                                    <%-- <div class="form-group col-md-9 has-feedback">&nbsp;</div>--%>



                                    <div class="form-group col-sm-3 col-md-3 has-feedback">
                                        <%--<button type="submit" name="B1" class="btnblue" style="width:100px;margin-left: 20%; margin-top: 25px;background: rgb(126, 204, 173);">
                                            <i class="fa fa-save"></i>
                                            Search
                                        </button>--%>
                                        <label >&nbsp;</label>
                                        <button type="submit" name="B1" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;Search</button>
                                    </div>
                                    <input type="hidden" value="<%=terminalBuffer.toString()%>" name="terminalbuffer">
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>


            <div class="row">

                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Summary Data <%--<font id="info_checkbox">(To view transaction summary data, please select appropriate checbox.)</font>--%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>


                        <div class="widget-content padding" style="overflow-y: auto;">
                            <%--<br>--%>
                            <%--<div class="table-responsive datatable">--%>
                            <%--<input type="hidden" value="<%=terminalBuffer.toString()%>" name="terminalbuffer">--%>

                            <%----------------------Report Start-----------------------------------%>


                            <%

                                if(request.getAttribute("transactionsdetails")!=null)
                                {
                                    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

                                    Hashtable hash = (Hashtable) request.getAttribute("transactionsdetails");
                                    Hashtable temphash = null;
                                    str = str + "&terminalbuffer=" + terminalBuffer;
                                    str = str + "&ctoken=" + ctoken;
                                    str = str + "&terminalid=" + terminalid;
                                    str = str + "&paymentid=" + paymentId;
                                    int records = 0;
                                    int totalrecords = 0;
                                    int currentblock = 1;

                                    try
                                    {
                                        records = Integer.parseInt((String) hash.get("records"));
                                        totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
                                        currentblock = Integer.parseInt(request.getParameter("currentblock"));
                                    }
                                    catch (Exception ex)
                                    {
                                    }

                                    String style = "class=tr0";

                                    if (records > 0)
                                    {

                            %>
                            <%

                                Map<String,Object> merchantTemplateSetting=null;
                                if(request.getAttribute("merchantTemplateSetting")!=null)
                                {
                                    merchantTemplateSetting= (Map<String, Object>) request.getAttribute("merchantTemplateSetting");
                            %>

                            <%--<div class="bg-infoorange" id="bginfo_checkbox" style="text-align:center;"><i class="fa fa-info-circle"></i>&nbsp;&nbsp;To view transaction summary data, please select appropriate checbox.</div>--%>

                            <form action="/merchant/servlet/SetColumnConfig?ctoken=<%=ctoken%>" method=post style="margin: 0;">
                                <div class="showingid"><strong>Note: Click on the Tracking ID to see the Transaction Details</strong></div>

                                <%--<input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromdate)%>" name="fdate">
                                <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(todate)%>" name="tdate">
                                <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(startTime)%>" name="starttime">
                                <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(endTime)%>" name="endtime">
                                <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(dateType)%>" name="datetype">--%>
                                <div class="pull-right" style="margin-left: 6px;">
                                    <ul class="nav navbar-nav navbar-right" id="configbtn_ul" style="margin-bottom: 10px;">
                                        <ul class="nav navbar-nav">
                                            <li class="dropdown btn-default" style="color: white"><a class="dropdown-toggle" data-toggle="dropdown" href="#" id="dropdown_btn"><i class="fa fa-cogs" style="color: white;"></i>&nbsp;&nbsp;Select The Fields&nbsp;&nbsp;<span class="caret" style="color: white;"></span></a>

                                                <ul class="dropdown-menu" id="grpChkBox">

                                                    <p style="font-weight: 700;">Please save your <br> selection.</p>

                                                    <li><input type="checkbox" id="Transactions_Date" name='<%=TemplatePreference.Transactions_Date.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_Date.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_Date.toString()):""%>"/>Transaction Date</li>
                                                    <input type="hidden" id="Transactions_Date_hidden" name='<%=TemplatePreference.Transactions_Date.toString()%>'  value>
                                                    <br>
                                                    <li><input type="checkbox" id="Transactions_TrackingID" name='<%=TemplatePreference.Transactions_TrackingID.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_TrackingID.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_TrackingID.toString()):""%>"/>Tracking ID</li>
                                                    <input type="hidden" id="Transactions_TrackingID_hidden" name='<%=TemplatePreference.Transactions_TrackingID.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" id="Transactions_OrderId" name='<%=TemplatePreference.Transactions_OrderId.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_OrderId.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_OrderId.toString()):""%>"/>Order Id</li>
                                                    <input type="hidden" id="Transactions_OrderId_hidden" name='<%=TemplatePreference.Transactions_OrderId.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" id="Transactions_OrderDescription" name='<%=TemplatePreference.Transactions_OrderDescription.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_OrderDescription.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_OrderDescription.toString()):""%>"/>Order Description</li>
                                                    <input type="hidden" id="Transactions_OrderDescription_hidden" name='<%=TemplatePreference.Transactions_OrderDescription.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" id="Transactions_CardHoldername" name='<%=TemplatePreference.Transactions_CardHoldername.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_CardHoldername.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_CardHoldername.toString()):""%>"/>Card Holder's Name</li>
                                                    <input type="hidden" id="Transactions_CardHoldername_hidden" name='<%=TemplatePreference.Transactions_CardHoldername.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" id="Transactions_CustomerEmail" name='<%=TemplatePreference.Transactions_CustomerEmail.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_CustomerEmail.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_CustomerEmail.toString()):""%>"/>Customer Email</li>
                                                    <input type="hidden" id="Transactions_CustomerEmail_hidden" name='<%=TemplatePreference.Transactions_CustomerEmail.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" id="Transactions_CustomerID" name='<%=TemplatePreference.Transactions_CustomerID.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_CustomerID.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_CustomerID.toString()):""%>"/>Customer ID</li>
                                                    <input type="hidden" id="Transactions_CustomerID_hidden" name='<%=TemplatePreference.Transactions_CustomerID.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" id="Transactions_PayMode" name='<%=TemplatePreference.Transactions_PayMode.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_PayMode.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_PayMode.toString()):""%>"/>Payment Mode</li>
                                                    <input type="hidden" id="Transactions_PayMode_hidden" name='<%=TemplatePreference.Transactions_PayMode.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" id="Transactions_CardType" name='<%=TemplatePreference.Transactions_CardType.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_CardType.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_CardType.toString()):""%>"/>Payment Brand</li>
                                                    <input type="hidden" id="Transactions_CardType_hidden" name='<%=TemplatePreference.Transactions_CardType.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" id="Transactions_Amount" name='<%=TemplatePreference.Transactions_Amount.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_Amount.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_Amount.toString()):""%>"/>Amount</li>
                                                    <input type="hidden" id="Transactions_Amount_hidden" name='<%=TemplatePreference.Transactions_Amount.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" id="Transactions_RefundedAmt" name='<%=TemplatePreference.Transactions_RefundedAmt.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_RefundedAmt.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_RefundedAmt.toString()):""%>"/>Refund Amount</li>
                                                    <input type="hidden" id="Transactions_RefundedAmt_hidden" name='<%=TemplatePreference.Transactions_RefundedAmt.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" id="Transactions_Currency" name='<%=TemplatePreference.Transactions_Currency.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_Currency.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_Currency.toString()):""%>"/>Currency</li>
                                                    <input type="hidden" id="Transactions_Currency_hidden" name='<%=TemplatePreference.Transactions_Currency.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" id="Transactions_Status" name='<%=TemplatePreference.Transactions_Status.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_Status.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_Status.toString()):""%>"/>Status</li>
                                                    <input type="hidden" id="Transactions_Status_hidden" name='<%=TemplatePreference.Transactions_Status.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" id="Transactions_Remark" name='<%=TemplatePreference.Transactions_Remark.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_Remark.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_Remark.toString()):""%>"/>Remark</li>
                                                    <input type="hidden" id="Transactions_Remark_hidden" name='<%=TemplatePreference.Transactions_Remark.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" id="Transactions_Terminal" name='<%=TemplatePreference.Transactions_Terminal.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_Terminal.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_Terminal.toString()):""%>"/>Terminal ID</li>
                                                    <input type="hidden" id="Transactions_Terminal_hidden" name='<%=TemplatePreference.Transactions_Terminal.toString()%>' value>
                                                    <br>
                                                    <li><input type="checkbox" id="Transactions_LastUpdateDate" name='<%=TemplatePreference.Transactions_LastUpdateDate.toString()%>' value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.Transactions_LastUpdateDate.toString()))?merchantTemplateSetting.get(TemplatePreference.Transactions_LastUpdateDate.toString()):""%>"/>Last Update Date</li>
                                                    <input type="hidden" id="Transactions_LastUpdateDate_hidden" name='<%=TemplatePreference.Transactions_LastUpdateDate.toString()%>' value>
                                                    <br>

                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>" name="desc">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>" name="trackingid">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(status)%>" name="status">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromdate)%>" name="fdate">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(todate)%>" name="tdate">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(startTime)%>" name="starttime">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(endTime)%>" name="endtime">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(dateType)%>" name="datetype">
                                                    <%--<input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fmonth)%>" name="fmonth">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tmonth)%>" name="tmonth">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fyear)%>" name="fyear">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tyear)%>" name="tyear">--%>
                                                    <input type="hidden" value="<%=archive%>" name="archive">
                                                    <input type="hidden" value="<%=customerId%>" name="customerid">
                                                    <input type="hidden" value="<%=firstName%>" name="firstname">
                                                    <input type="hidden" value="<%=lastName%>" name="lastname">
                                                    <input type="hidden" value="<%=emailAddress%>" name="emailaddr">
                                                    <input type="hidden" value="<%=terminalid%>" name="terminalid">
                                                    <input type="hidden" value="<%=terminalBuffer%>" name="terminalbuffer">
                                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(bank)%>" name="bank">
                                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">

                                                    <button type="submit" value="Save" class="buttonform" name="Save" id="Save" onclick="mySave()">
                                                        Save
                                                    </button>
                                                </ul>

                                            </li>
                                        </ul>
                                    </ul>
                                </div>

                            </form>

                            <form name="exportform" method="post" action="ExportTransactions?ctoken=<%=ctoken%>" >
                                <div class="pull-right">
                                    <div class="btn-group">


                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>" name="desc">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>" name="trackingid">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(status)%>" name="status">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromdate)%>" name="fdate">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(todate)%>" name="tdate">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(startTime)%>" name="starttime">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(endTime)%>" name="endtime">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(dateType)%>" name="datetype">
                                        <%--<input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fmonth)%>" name="fmonth">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tmonth)%>" name="tmonth">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fyear)%>" name="fyear">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tyear)%>" name="tyear">--%>
                                        <input type="hidden" value="<%=archive%>" name="archive">
                                        <input type="hidden" value="<%=customerId%>" name="customerid">
                                        <input type="hidden" value="<%=firstName%>" name="firstname">
                                        <input type="hidden" value="<%=lastName%>" name="lastname">
                                        <input type="hidden" value="<%=emailAddress%>" name="emailaddr">
                                        <input type="hidden" value="<%=terminalid%>" name="terminalid">
                                        <input type="hidden" value="<%=terminalBuffer%>" name="terminalbuffer">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(bank)%>" name="bank">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">

                                        <%-- <button class="btn-xs" type="submit" style="background: white;border: 0;">
                                             <img style="height: 40px;" src="/merchant/images/excel.png">
                                         </button>--%>

                                        <button type="submit" name="Excel" id="Excel" onclick="myExcel()" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-file-excel-o"></i>&nbsp;&nbsp;Export To Excel</button>

                                    </div>
                                </div>
                            </form>
                            <div class="widget-content padding" id="table_div" style="overflow-x: auto;">

                                <table id="myTable" class="display table table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <%--<table  id="datatables-2" class="table table-striped table-bordered" cellspacing="0" width="100%">--%>
                                    <thead>
                                    <tr style="color: white;">
                                        <th class="Transactions_Date" style="text-align: -webkit-center;">Transaction Date</th>
                                        <th class="Transactions_TrackingID" style="text-align: -webkit-center;">Tracking ID</th>
                                        <th class="Transactions_OrderId" style="text-align: -webkit-center;">Order ID</th>
                                        <th class="Transactions_OrderDescription" style="text-align: -webkit-center;">Order Description</th>
                                        <th class="Transactions_CardHoldername" style="text-align: -webkit-center;">Card Holder's Name</th>
                                        <th class="Transactions_CustomerEmail" style="text-align: -webkit-center;">Customer Email</th>
                                        <th class="Transactions_CustomerID" style="text-align: -webkit-center;">Customer ID</th>
                                        <th class="Transactions_PayMode" style="text-align: -webkit-center;">Payment Mode</th>
                                        <th class="Transactions_CardType" style="text-align: -webkit-center;">Payment Brand</th>
                                        <th class="Transactions_Amount" style="text-align: -webkit-center;">Amount</th>
                                        <th class="Transactions_RefundedAmt" style="text-align: -webkit-center;">Refund Amount</th>
                                        <th class="Transactions_Currency" style="text-align: -webkit-center;">Currency 12345</th>
                                        <th class="Transactions_Status" style="text-align: -webkit-center;">Status</th>
                                        <th class="Transactions_Remark" style="text-align: -webkit-center;">Remark</th>
                                        <th class="Transactions_Terminal" style="text-align: -webkit-center;">Terminal ID</th>
                                        <th class="Transactions_LastUpdateDate" style="text-align: -webkit-center;">Last Update Date</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
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
                                            {
                                                requestParameter.append("<input type='hidden' name='"+name+"' value=\""+request.getParameter(name)+"\"/>");
                                            }

                                        }
                                        requestParameter.append("<input type='hidden' name='SPageno' name='SPageno' value='"+pageno+"'/>");
                                        requestParameter.append("<input type='hidden' name='SRecords' value='"+pagerecords+"'/>");

                                        for (int pos = 1; pos <= records; pos++)
                                        {
                                            String id = Integer.toString(pos);
                                            int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);

                                            style = "class=\"tr" + (pos + 1) % 2 + "\"";
                                            temphash = (Hashtable) hash.get(id);

                                            Functions functions = new Functions();
                                            String date1 = Functions.convertDtstampToDateTime((String) temphash.get("dtstamp"));
                                            String description = (String) temphash.get("description");
                                            String orderDescription = (String) temphash.get("orderdescription");
                                            String icicitransid = (String) temphash.get("transid");
                                            String name = (String) temphash.get("name");
                                            ctoken = request.getParameter("ctoken");
                                            if (name == null) name = "-";
                                            String transCurrency = (String) temphash.get("currency");
                                            String amount = (String) temphash.get("amount");
                                            String refundamount = (String) temphash.get("refundamount");
                                            if ("JPY".equalsIgnoreCase(transCurrency))
                                            {
                                                amount = functions.printNumber(Locale.JAPAN, amount);
                                                refundamount = functions.printNumber(Locale.JAPAN, refundamount);
                                            }
                                            else if ("EUR".equalsIgnoreCase(transCurrency))
                                            {
                                                amount = functions.printNumber(Locale.FRANCE, amount);
                                                refundamount = functions.printNumber(Locale.FRANCE, refundamount);
                                            }
                                            else if ("GBP".equalsIgnoreCase(transCurrency))
                                            {
                                                amount = functions.printNumber(Locale.UK, amount);
                                                refundamount = functions.printNumber(Locale.UK, refundamount);
                                            }
                                            else if ("USD".equalsIgnoreCase(transCurrency))
                                            {
                                                amount = functions.printNumber(Locale.US, amount);
                                                refundamount = functions.printNumber(Locale.US, refundamount);
                                            }
                                            //String captureamount = (String) temphash.get("captureamount");
                                            //String refundamount = (String) temphash.get("refundamount");
                                            //String payoutamount = (String) temphash.get("payoutamount");
                                            String tempstatus = (String) sortedMap.get((String) temphash.get("status"));
                                            String paymodeid = (String) temphash.get("paymodeid");
                                            String cardtypeid = (String) temphash.get("cardtypeid");
                                            String accountid = (String) temphash.get("accountid");
                                            String tempterminalid = (String) temphash.get("terminalid");
                                            String customerId1 = (String) temphash.get("customerId");
                                            String customerEmail=(String) temphash.get("emailaddr");
                                            String lastUpdateDate = (String)temphash.get("timestamp");
                                            String remark1=(String) temphash.get("remark");
                                            if (tempterminalid == null) tempterminalid = "-";

                                            out.println("<tr " + style + ">");
                                            out.println("<td data-label=\"Date\" class=\"Transactions_Date\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(date1) + "</td>");
                                            //out.println("<td data-label=\"Tracking ID\" align=\"center\"" + style + "><form action=\"TransactionDetails?ctoken="+ctoken+"\" method=\"post\" ><input type=\"hidden\" name=\"accountid\" value=\""+accountid+"\"><input type=\"hidden\" name=\"trackingid\" value=\""+icicitransid+"\"><input type=\"hidden\" name=\"ctoken\" value=\""+ctoken+"\"><input type=\"hidden\" name=\"archive\" value=\""+archive+"\"><input type=\"hidden\" name=\"terminalbuffer\" value=\""+terminalBuffer.toString()+"\"><input type=\"submit\" class=\"btn btn-default\" name=\"submit\" value=\"\"+icicitransid+\"\">");;
                                            out.println("<td data-label=\"Tracking ID\" class=\"Transactions_TrackingID\" align=\"center\"" + style + "><form action=\"TransactionDetails?ctoken=" + ctoken + "\" method=\"post\" ><input type=\"hidden\" name=\"accountid\" value=\"" + accountid + "\"><input type=\"hidden\" name=\"cardtypeid\" value=\"" + cardtypeid + "\"><input type=\"hidden\" name=\"trackingid\" value=\"" + icicitransid + "\"><input type=\"hidden\" name=\"ctoken\" value=\"" + ctoken + "\"><input type=\"hidden\" name=\"archive\" value=\"" + archive + "\"><input type=\"hidden\" name=\"tStatus\" value=\"" + tempstatus + "\"><input type=\"hidden\" name=\"buttonValue\" value=\"transaction\"><input type=\"hidden\" name=\"terminalbuffer\" value=\"" + terminalBuffer.toString() + "\"><input type=\"submit\" class=\"btn btn-default\" name=\"submit\"  value=\"" + icicitransid + "\">");
                                            out.println(requestParameter.toString());
                                            out.print("</form></td>");
                                            out.println("<td data-label=\"Description\" class=\"Transactions_OrderId\" align=\"center\">" + ESAPI.encoder().encodeForHTML(description) + "</td>");
                                            if (orderDescription==null || orderDescription.equals(""))
                                            {
                                                out.println("<td data-label=\"Order Description\" class=\"Transactions_OrderDescription\" align=\"center\"" + style + ">" + "-" + "</td>");
                                            }
                                            else
                                            {
                                                out.println("<td data-label=\"Order Description\" class=\"Transactions_OrderDescription\" align=\"center\">" + ESAPI.encoder().encodeForHTML(orderDescription) + "</td>");
                                            }
                                            out.println("<td data-label=\"Card Holder's Name\" class=\"Transactions_CardHoldername\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(name) + "</td>");
                                            out.println("<td data-label=\"Customer Email\" class=\"Transactions_CustomerEmail\" align=\"center\"" + style + ">" + functions.getEmailMasking(customerEmail) + "</td>");
                                            if (!functions.isValueNull(customerId1))
                                            {
                                                out.println("<td data-label=\"Customer ID\" class=\"Transactions_CustomerID\" align=\"center\"" + style + ">" +"-"+ "</td>");
                                            }
                                            else
                                            {
                                                out.println("<td data-label=\"Customer ID\" class=\"Transactions_CustomerID\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(customerId1) + "</td>");
                                            }
                                            if (!functions.isValueNull(paymodeid) || "0".equals(paymodeid))
                                            {
                                                out.println("<td data-label=\"PayMode\" class=\"Transactions_PayMode\" align=\"center\"" + style + ">" + "-" + "</td>");
                                            }
                                            else
                                            {
                                                out.println("<td data-label=\"PayMode\" class=\"Transactions_PayMode\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(paymodeid) + "</td>");
                                            }

                                            if (!functions.isValueNull(cardtypeid))
                                            {
                                                out.println("<td data-label=\"CardType\" class=\"Transactions_CardType\" align=\"center\"" + style + ">&nbsp;" + "-" + "</td>");
                                            }
                                            else
                                            {
                                                cardtypeid = GatewayAccountService.getCardType(cardtypeid);
                                                if (functions.isValueNull(cardtypeid))
                                                {
                                                    out.println("<td data-label=\"CardType\" class=\"Transactions_CardType\" align=\"center\"" + style + "> <img src=\"/merchant/images/cardtype/" + ESAPI.encoder().encodeForHTML(cardtypeid)+".png\" style=\"height:57%;\"></td>");
                                                }
                                                else
                                                {
                                                    out.println("<td data-label=\"CardType\" class=\"Transactions_CardType\" align=\"center\"" + style + ">&nbsp;" + "-" + "</td>");
                                                }
                                            }
                                            out.println("<td data-label=\"Amount\" class=\"Transactions_Amount\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(amount) + "</td>");
                                            out.println("<td data-label=\"Refund Amount\" class=\"Transactions_RefundedAmt\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(refundamount) + "</td>");
                                            if (transCurrency == null || transCurrency.equals(""))
                                            {
                                                out.println("<td data-label=\"Currency\" class=\"Transactions_Currency\" align=\"center\"" + style + ">" + "" + "</td>");

                                            }
                                            else
                                            {
                                                out.println("<td data-label=\"Currency\" class=\"Transactions_Currency\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(transCurrency) + "</td>");
                                            }

                                            out.println("<td valign=\"middle\" data-label=\"Status\" class=\"Transactions_Status\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(tempstatus) + "</td>");

                                            if (!functions.isValueNull(remark1))
                                            {
                                                out.println("<td data-label=\"Remark\" class=\"Transactions_Remark\" align=\"center\"" + style + ">" +"-"+ "</td>");
                                            }
                                            else
                                            {
                                                out.println("<td valign=\"middle\" data-label=\"Remark\" class=\"Transactions_Remark\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(remark1) + "</td>");
                                            }

                                            out.println("<td valign=\"middle\" data-label=\"Terminal ID\" class=\"Transactions_Terminal\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(tempterminalid) + "</td>");

                                            out.println("<td valign=\"middle\" data-label=\"Last Update Date\" class=\"Transactions_LastUpdateDate\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(lastUpdateDate) + "</td>");

                                        /*out.println("<td data-label=\"Card Holder's Name\" align=\"center\"" + style + ">&nbsp;" + name+ "</td>");*/
                                            out.println("</tr>");

                                        }
                                    %>
                                    </tbody>
                                    <%--<thead>
                                    <tr style="color: white;">
                                        <td  align="left" class="th0">Total Records : <%=totalrecords%></td>
                                        <td  align="right" class="th0">Page No : <%=pageno%></td>
                                        <td class="th0">&nbsp;</td>
                                        <td class="th0">&nbsp;</td>
                                        <td class="th0">&nbsp;</td>
                                        <td class="th0">&nbsp;</td>
                                        <td class="th0">&nbsp;</td>
                                        <td class="th0">&nbsp;</td>
                                        <td class="th0">&nbsp;</td>
                                        <td class="th0">&nbsp;</td>
                                        <td class="th0">&nbsp;</td>
                                        <td class="th0">&nbsp;</td>
                                    </tr>
                                    </thead>--%>
                                </table>
                            </div>


                            <div class="showingid"><strong>Showing Page <%=pageno%> of <%=totalrecords%> records</strong></div>

                            <jsp:include page="page.jsp" flush="true">
                                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                                <jsp:param name="pageno" value="<%=pageno%>"/>
                                <jsp:param name="str" value="<%=str%>"/>
                                <jsp:param name="page" value="Transactions"/>
                                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                                <jsp:param name="orderby" value=""/>
                            </jsp:include>

                            <%
                                    }
                                }
                                else
                                {
                                    out.println(Functions.NewShowConfirmation1("Sorry", "No records found for given search criteria.<br><br>Date :<br>From " + fromdate + "<br>To " + todate));
                                }

                            %>

                        </div>
                    </div>
                </div>
            </div>

            <%
                }
                else
                {
                    out.println(Functions.NewShowConfirmation1("Filter"," Please provide the appropriate data for the search of Transaction"));
                }
            %>

        </div>
    </div>
</div>
</body>
</html>

