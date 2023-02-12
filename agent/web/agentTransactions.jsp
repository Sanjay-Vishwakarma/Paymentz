<%@ page import="com.directi.pg.Functions,com.directi.pg.TransactionEntry,org.owasp.esapi.ESAPI,org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>

<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 10/8/13
  Time: 2:47 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<%!
    private static Logger logger=new Logger("merchantWireReports.jsp");
%>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("agentname"));
    session.setAttribute("submit","agentTransactions");
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

    if (agent.isLoggedInAgent(session))
    {
        TransactionEntry transactionentry = new TransactionEntry();
        SortedMap sortedMap = transactionentry.getSortedMap();
        Functions functions = new Functions();

        String memberid=nullToStr(request.getParameter("memberid"));
        Hashtable<String, String> memberidDetails=agent.getAgentMemberDetailList((String) session.getAttribute("merchantid"));
        String desc = Functions.checkStringNull(request.getParameter("desc"))==null?"":request.getParameter("desc");
        String trackingid = Functions.checkStringNull(request.getParameter("trackingid"))==null?"":request.getParameter("trackingid");
        String cardtype = Functions.checkStringNull(request.getParameter("cardtype"))==null?"":request.getParameter("cardtype");

        String fdate=null;
        String tdate=null;
        /*String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;*/

        String status = Functions.checkStringNull(request.getParameter("status"));
        /*String Remark = null;*/
        Date date = new Date();
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

        String Date = originalFormat.format(date);
        date.setDate(1);
        String fromDates = originalFormat.format(date);

        fdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDates : request.getParameter("fromdate");
        tdate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");

        Calendar rightNow = Calendar.getInstance();
        String str = "";
        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
        /*if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
        if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);*/
        String currentyear= ""+rightNow.get(rightNow.YEAR);
        if (fdate != null) str = str + "fromdate=" + fdate;
        if (tdate != null) str = str + "&todate=" + tdate;
        if ( cardtype!= null) str = str + "&cardtype=" + cardtype;

        /*if (fmonth != null) str = str + "&fmonth=" + fmonth;
        if (tmonth != null) str = str + "&tmonth=" + tmonth;
        if (fyear != null) str = str + "&fyear=" + fyear;
        if (tyear != null) str = str + "&tyear=" + tyear;*/
        if (desc != null) str = str + "&desc=" + desc;
        if (memberid != null) str = str + "&memberid=" + memberid;
        //if(status!=null)str=str+"&status="+status;
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

        int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),30);

        str = str + "&SRecords=" + pagerecords;
%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <link rel="stylesheet" href=" /agent/transactionCSS/css/jquery.dataTables.min.css">
    <script type="text/javascript" src="/agent/transactionCSS/js/jquery.dataTables.min.js"></script>

    <link href="/agent/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/agent/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script src="/agent/NewCss/libs/jquery-icheck/icheck.min.js"></script>

    <title><%=company%> | Merchant Transactions</title>
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
</head>
<body>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <form name="form" method="post" action="/agent/net/AgentTransaction?ctoken=<%=ctoken%>">
                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%>'s&nbsp;<%=archive ? archivalString : currentString%> Transactions</strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>

                            <%
                                String error=(String ) request.getAttribute("error");
                                if(functions.isValueNull(error))
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                                }

                                String catchError=(String ) request.getAttribute("catchError");
                                if(functions.isValueNull(catchError))
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + catchError + "</h5>");
                                }
                            %>

                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                            <div class="widget-content padding">
                                <div id="horizontal-form">
                                    <div class="form-group col-md-4">
                                        <label>From</label>
                                        <input type="text" name="fromdate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">

                                    </div>

                                    <div class="form-group col-md-4">
                                        <label>To</label>
                                        <input type="text" name="todate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">

                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label >Tracking ID</label>
                                        <input type=text name="trackingid"  maxlength="20" class="form-control" size="15"<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>" >
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label>Status</label>
                                        <select size="1" name="status"  class="form-control">
                                            <option value="">All</option>
                                            <%--<%
                                                Enumeration enu = statushash.keySet();
                                                String selected = "";
                                                String key = "";
                                                String value = "";

                                                while (enu.hasMoreElements())
                                                {
                                                    key = (String) enu.nextElement();
                                                    value = (String) statushash.get(key);

                                                    if (key.equals(status))
                                                        selected = "selected";
                                                    else
                                                        selected = "";

                                            %>--%>
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
                                            <%      }
                                            }
                                            %>
                                        </select>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label>Merchant ID</label>
                                        <select size="1" name="memberid" class="form-control">
                                            <option value="">All</option>
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
                                            <option value="<%=key3%>" <%=selected3%> > <%=key3%>---<%=value3%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label>Data Source</label>
                                        <select size="1" name="archive" class="form-control">
                                            <option value="true" <%=selectedArchived%>>Archives</option>
                                            <option value="false" <%=selectedCurrent%>>Current</option>
                                        </select>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label >Description</label>
                                        <input type=text name="desc" maxlength="100"  class="form-control"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>">
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label >Rows/page</label>
                                        <input type="text" maxlength="5"  size="15" class="form-control" value="<%=pagerecords%>" name="SRecords" size="2"/>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label>Card Present</label>
                                        <select size="1" name="cardtype" class="form-control" id="cardtype" >
                                            <%
                                                if("CP".equals(cardtype))
                                                {%>
                                            <option value="CP" SELECTED>Yes</option>
                                            <option value="CNP">No</option>
                                            <%}
                                            else
                                            {%>
                                            <option value="CNP" SELECTED>No</option>
                                            <option value="CP">Yes</option>
                                            <%}
                                            %>
                                        </select>
                                    </div>
                                    <div class="form-group col-md-4">
                                        <label style="color: transparent;">Path</label>
                                        <button type="submit" class="btn btn-default" name="B1" style="display:block;">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </form>

            <%--Start Report Data--%>
            <div class="row reporttable">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Report Table</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" style="overflow-x: auto;">
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
                                        requestParameter.append("<input type='hidden' name='"+name+"' value=\""+request.getParameter(name)+"\"/>");
                                }
                                requestParameter.append("<input type='hidden' name='SPageno' value='"+pageno+"'/>");
                                requestParameter.append("<input type='hidden' name='SRecords' value='"+pagerecords+"'/>");

                                Hashtable hash = (Hashtable) request.getAttribute("transactionsdetails");
                                error=(String) request.getAttribute("error");

                                Hashtable temphash = null;

                                int records = 0;
                                int totalrecords = 0;
                                int currentblock = 1;

                                str = str + "&trackingid=" +trackingid;
                                str = str + "&memberid=" +memberid;

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

                            <div id="showingid"><strong>Note: Click on the Tracking ID to see the Transaction Details</strong></div>
                            <%--<div class="pull-right">
                                <div class="btn-group">
                                    <form name="exportform" method="post" action="/partner/net/ExportTransactionByPartner?ctoken=<%=ctoken%>" >
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" name="fdate">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" name="tdate">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(status)%>" name="status">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>" name="desc">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>" name="memberid">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(String.valueOf(archive))%>" name="archive">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(String.valueOf(trackingid))%>" name="trackingid">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">

                                        <button class="btn-xs" type="submit" style="background: white;border: 0;">
                                            <img style="height: 40px;" src="/agent/images/excel.png">
                                        </button>
                                    </form>
                                </div>
                            </div>--%>

                            <div class="pull-right">
                                <div class="btn-group">
                                    <form name="exportform" method="post" action="/agent/net/ExportTransactionByAgent?ctoken=<%=ctoken%>" >
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" name="fromdate">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" name="todate">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(status)%>" name="status">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>" name="desc">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>" name="memberid">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cardtype)%>" name="cardtype">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(String.valueOf(archive))%>" name="archive">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(String.valueOf(trackingid))%>" name="trackingid">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <button class="btn-xs" type="submit" style="background: white;border: 0;">
                                            <img style="height: 40px;" src="/agent/images/excel.png">
                                        </button>
                                    </form>
                                </div>
                            </div>

                            <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead>
                                <tr>

                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Transaction Date</b></td>
                                    <%
                                        if("CP".equals(cardtype))
                                        {%>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Transaction Fetched Date</b></td>
                                    <%
                                        }
                                    %>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Merchant ID</b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Tracking ID</b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>PayMode</b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>CardType</b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Description</b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Auth Amt</b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Captured Amt</b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Refunded Amt</b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Currency</b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Status</b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Remark</b></td>
                                </tr>
                                </thead>
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">

                                <%
                                    for (int pos = 1; pos <= records; pos++)
                                    {
                                        String id = Integer.toString(pos);
                                        style = "class=\"tr" + (pos + 1) % 2 + "\"";
                                        temphash = (Hashtable) hash.get(id);

                                        String date1 = Functions.convertDtstamptoDate((String)temphash.get("dtstamp"));
                                        String description = (String)temphash.get("description");
                                        String icicitransid =(String)temphash.get("transid");
                                        String name = (String)temphash.get("name");
                                        if (name == null) name = "-";
                                        String amount =(String) temphash.get("amount");
                                        String captureamount = (String) temphash.get("captureamount");
                                        String refundamount = (String) temphash.get("refundamount");
                                        String tempstatus = (String) sortedMap.get((String) temphash.get("status"));
                                        String paymodeid = (String) temphash.get("paymodeid");
                                        String cardtypeid = (String) temphash.get("cardtypeid");
                                        String accountid = (String) temphash.get("accountid");
                                        String currency = (String) temphash.get("currency");
                                        String toid=(String) temphash.get("toid");
                                        String remark = (String) temphash.get("remark");
                                        ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

                                        out.println("<tr " + style + ">");
                                        if("CP".equals(cardtype))
                                        {
                                            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                                            java.util.Date t=ft.parse((String) temphash.get("transactionTime"));
                                            ft.applyPattern("d MMM yyyy HH:mm:ss");
                                            out.println("<td valign=\"middle\" data-label=\"Date\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(ft.format(t)) + "</td>");
                                        }else{
                                            out.println("<td valign=\"middle\" data-label=\"Date\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(date1) + "</td>");
                                        }
                                        if("CP".equals(cardtype))
                                        {
                                            out.println("<td valign=\"middle\" data-label=\"Date\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(date1) + "</td>");
                                        }
                                        out.println("<td valign=\"middle\" data-label=\"Merchant ID\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(toid) + "</td>");
                                        out.println("<td valign=\"middle\" data-label=\"Tracking ID\" align=\"center\"" + style + "><form action=/agent/net/AgentTransactionDetails?ctoken="+ctoken+" method=\"post\"><input type=\"hidden\" name=\"ctoken\" value=\""+ctoken+"\"><input type=\"hidden\" name=\"cardtype\" value=\""+cardtype+"\"><input type=\"hidden\" name=\"archive\" value=\""+ archive +"\"><input type=\"hidden\" name=\"status\" value=\"" + tempstatus +"\"><input type=\"hidden\" name=\"accountid\" value=\"" + accountid +"\"><input type=\"hidden\" name=\"memberid\" value=\""+toid+"\"><input type=\"submit\" name=\"trackingid\" value=\""+ ESAPI.encoder().encodeForHTML(icicitransid) + "\" class=\"gotoauto btn btn-default\">");
                                        out.println(requestParameter.toString());
                                        out.print("</form></td>");
                                        if(paymodeid==null || paymodeid.equals("") )
                                        {
                                            out.println("<td valign=\"middle\" data-label=\"PayMode\" align=\"center\"" + style + ">" + "-" + "</td>");
                                        }
                                        else
                                        {
                                            out.println("<td valign=\"middle\" data-label=\"PayMode\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(paymodeid) + "</td>");
                                        }
                                        if(cardtypeid==null || cardtypeid.equals("") )
                                        {
                                            out.println("<td valign=\"middle\" data-label=\"CardType\" align=\"center\"" + style + ">" + "-" + "</td>");
                                        }
                                        else
                                        {
                                            String cardTypeValue = GatewayAccountService.getCardType(cardtypeid);
                                            if (!functions.isValueNull(cardTypeValue)) cardTypeValue="-";
                                            out.println("<td valign=\"middle\" data-label=\"CardType\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(cardTypeValue) + "</td>");
                                        }

                                        out.println("<td valign=\"middle\" data-label=\"Description\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(description) + "</td>");
                                        out.println("<td valign=\"middle\" data-label=\"Auth Amt\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(amount) + "</td>");
                                        out.println("<td valign=\"middle\" data-label=\"Captured Amt\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(captureamount) + "</td>");
                                        out.println("<td valign=\"middle\" data-label=\"Refunded Amt\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(refundamount) + "</td>");
                                        out.println("<td valign=\"middle\" data-label=\"Currency\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(currency) + "</td>");
                                        out.println("<td valign=\"middle\" data-label=\"Status\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(tempstatus) + "</td>");
                                        if (remark==null ||remark.equals(""))
                                        {
                                            out.println("<td valign=\"middle\" data-label=\"Remark\" align=\"center\"" + style + ">" +"-" + "</td>");
                                        }
                                        else
                                        {
                                            out.println("<td valign=\"middle\" data-label=\"Remark\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(remark) + "</td>");
                                        }

                                        out.println("</tr>");
                                    }
                                %>


                            </table>
                        </div>

                        <div id="showingid"><strong>Showing Page <%=pageno%> of <%=totalrecords%> records</strong></div>
                        <jsp:include page="page.jsp" flush="true">
                            <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                            <jsp:param name="numrows" value="<%=pagerecords%>"/>
                            <jsp:param name="pageno" value="<%=pageno%>"/>
                            <jsp:param name="str" value="<%=str%>"/>
                            <jsp:param name="page" value="AgentTransaction"/>
                            <jsp:param name="currentblock" value="<%=currentblock%>"/>
                            <jsp:param name="orderby" value=""/>
                        </jsp:include>
                        <%
                            }
                            else
                            {
                                out.println(Functions.NewShowConfirmation1("Sorry", "No records found.<br><br>Date :<br>From " + fdate + " <br>To " + tdate));
                            }
                        %>
                        <%!
                            public static String nullToStr(String str)
                            {
                                if(str == null)
                                    return "";
                                return str;
                            }
                            public static String getStatus(String str)
                            {
                                if(str.equals("Y"))
                                    return "Active";
                                else if(str.equals("N"))
                                    return "Inactive";
                                else if(str.equals("T"))
                                    return "Test";

                                return str;
                            }
                        %>
                    </div>
                </div>
            </div>
            <%
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