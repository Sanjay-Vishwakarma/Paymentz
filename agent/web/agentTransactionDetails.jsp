            <%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.ParseException" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 10/15/13
  Time: 2:17 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<%!
    private static Logger logger=new Logger("merchantWireReports.jsp");
%>
<%
    session.setAttribute("submit","agentTransactionDetails");
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("agentname"));
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    Transaction transaction = new Transaction();
    if (agent.isLoggedInAgent(session))
    {
        TransactionEntry transactionentry = new TransactionEntry();
        SortedMap sortedMap = transactionentry.getSortedMap();

        String memberid=nullToStr(request.getParameter("memberid"));
        Hashtable memberidDetails=agent.getAgentMemberDetailList((String) session.getAttribute("merchantid"));
        String desc = Functions.checkStringNull(request.getParameter("desc"))==null?"":request.getParameter("desc");
        String trackingid = nullToStr(request.getParameter("trackingid"));
        String fdate=null;
        String tdate=null;
        String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;
        try
        {
            fdate = ESAPI.validator().getValidInput("fdate",request.getParameter("fdate"),"Days",2,true);
            tdate = ESAPI.validator().getValidInput("tdate",request.getParameter("tdate"),"Days",2,true);
            fmonth = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
            tmonth =  ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
            fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
            tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);
        }
        catch(ValidationException e)
        {
            logger.error("ValidationExceptiont",e);
            out.println("<div class=\"reporttable\" style=\"margin-top:100px\">");
            out.println(Functions.NewShowConfirmation("Sorry","Internal error while accessing data."));
            out.println("</div>");
        }

        String status = Functions.checkStringNull(request.getParameter("status"));
        String iciciselected = "";
        String hdfcselected = "";

        Calendar rightNow = Calendar.getInstance();
        String str = "";
        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
        if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
        if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);
        String currentyear= ""+rightNow.get(rightNow.YEAR);

        if (fdate != null) str = str + "fdate=" + fdate;
        if (tdate != null) str = str + "&tdate=" + tdate;
        if (fmonth != null) str = str + "&fmonth=" + fmonth;
        if (tmonth != null) str = str + "&tmonth=" + tmonth;
        if (fyear != null) str = str + "&fyear=" + fyear;
        if (tyear != null) str = str + "&tyear=" + tyear;
        if (desc != null) str = str + "&desc=" + desc;
        if (memberid != null) str = str + "&memberid=" + memberid;
        str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        boolean archive = Boolean.valueOf(request.getParameter("archive")).booleanValue();
        str = str + "&archive=" + archive;
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
        //int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),30);
        str = str + "&SRecords=" + pagerecords;

%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
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
    <link href="/agent/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/agent/datepicker/datepicker/bootstrap-datepicker.js"></script>
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
</head>
<body>
<form name="form" method="post" action="AgentTransaction?ctoken=<%=ctoken%>">
    <div class="content-page">
        <div class="content">
            <!-- Page Heading Start -->
            <div class="page-heading">

                <div class="pull-right">
                    <div class="btn-group">
                        <form action="/agent/net/AgentTransaction?ctoken=<%=ctoken%>" method="post" name="form">
                            <%
                                Enumeration<String> stringEnumeration=request.getParameterNames();
                                while(stringEnumeration.hasMoreElements())
                                {
                                    String name=stringEnumeration.nextElement();
                                    if(!"memberid".equals(name))
                                    {
                                        if("trackingid".equals(name))
                                        {
                                            out.println("<input type='hidden' name='"+name+"' value='"+request.getParameterValues(name)[1]+"'/>");
                                        }
                                        else
                                            out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                                    }
                                }
                            %>
                        </form>

                        <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
                            <img style="height: 35px;" src="/agent/images/goBack.png">
                        </button>
                    </div>
                </div>
                <br><br><br>


                <%--<form name="form" method="post" action="/partner/net/PartnerTransaction?ctoken=<%=ctoken%>">
                    <div class="row">
                        <div class="col-sm-12 portlets ui-sortable">
                            <div class="widget">

                                <div class="widget-header transparent">
                                    <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%> Partner <%=archive ? archivalString : currentString%> Transactions</strong></h2>
                                    <div class="additional-btn">
                                        <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                        <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                        <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                    </div>
                                </div>
                                <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                                <div class="widget-content padding">
                                    <div id="horizontal-form">

                                        <div class="form-group col-md-4 has-feedback">
                                            <label >From</label>
                                            &lt;%&ndash;<select size="1" name="fdate" class="form-control">
                                                <%
                                                    if (fdate != null)
                                                        out.println(Functions.dayoptions(1, 31, fdate));
                                                    else
                                                        out.println(Functions.printoptions(1, 31));
                                                %>
                                            </select>
                                            <select size="1" name="fmonth" class="form-control">
                                                <%
                                                    if (fmonth != null)
                                                        out.println(Functions.newmonthoptions(1, 12, fmonth));
                                                    else
                                                        out.println(Functions.printoptions(1, 12));
                                                %>
                                            </select>
                                            <select size="1" name="fyear" class="form-control">
                                                <%
                                                    if (fyear != null)
                                                        out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), fyear));
                                                    else
                                                        out.println(Functions.printoptions(2005, 2013));
                                                %>
                                            </select>&ndash;%&gt;
                                            <input type="text" size="16" name="fdate" class="datepicker form-control" value="<%=fdate%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">

                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label>To</label>
                                            &lt;%&ndash;<select size="1" name="tdate" class="form-control">
                                                <%
                                                    if (tdate != null)
                                                        out.println(Functions.dayoptions(1, 31, tdate));
                                                    else
                                                        out.println(Functions.printoptions(1, 31));
                                                %>
                                            </select>
                                            <select size="1" name="tmonth" class="form-control">
                                                <%
                                                    if (tmonth != null)
                                                        out.println(Functions.newmonthoptions(1, 12, tmonth));
                                                    else
                                                        out.println(Functions.printoptions(1, 12));
                                                %>
                                            </select>
                                            <select size="1" name="tyear" class="form-control">
                                                <%
                                                    if (tyear != null)
                                                        out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                                                    else
                                                        out.println(Functions.printoptions(2005, 2013));
                                                %>
                                            </select>&ndash;%&gt;
                                            <input type="text" size="16" name="tdate" class="datepicker form-control" value="<%=tdate%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">

                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label>Status</label>
                                            <select size="1" name="status" class="form-control">
                                                <option value="">All</option>
                                                <%
                                                    Enumeration enu = statushash.keys();
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
                                                %>
                                                <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key)%>" <%=selected%>>
                                                    <%=ESAPI.encoder().encodeForHTML(value)%>
                                                </option>
                                                <%
                                                    }
                                                %>
                                            </select>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label >Description</label>
                                            <input type=text name="desc" maxlength="100" maxlength="100"  class="form-control" size="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>">
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label>Memberid ID</label>
                                            <select size="1" name="memberid" class="form-control">
                                                <option value="">All</option>
                                                <%
                                                    Enumeration enu3 = memberidDetails.keys();
                                                    String selected3 = "";
                                                    String key3 = "";
                                                    String value3 = "";
                                                    while (enu3.hasMoreElements())
                                                    {
                                                        key3 = (String) enu3.nextElement();
                                                        value3 = (String) memberidDetails.get(key3);
                                                        if (value3.equals(memberid))
                                                            selected3 = "selected";
                                                        else
                                                            selected3 = "";
                                                %>
                                                <option value="<%=value3%>" <%=selected3%>><%=value3%></option>
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
                                            <label >Tracking ID</label>
                                            <input type=text name="trackingid" maxlength="100" maxlength="100" class="form-control" size="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>">
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label >Rows/page</label>
                                            <input type="text" maxlength="5"  value="<%=pagerecords%>" name="SRecords" size="2" class="form-control">
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
                </form>--%>

                <%--<div class="pull-right">
                    <div class="btn-group">
                        <form action="partner/net/PartnerTransaction?ctoken=<%=ctoken%>" method="post" name="form">
                            &lt;%&ndash;<%
                                Enumeration<String> stringEnumeration=request.getParameterNames();
                                while(stringEnumeration.hasMoreElements())
                                {
                                    String name=stringEnumeration.nextElement();
                                    if("action".equals(name))
                                    {
                                        out.println("<input type='hidden' name='"+name+"' value='"+request.getParameterValues(name)[1]+"'/>");
                                    }
                                    else
                                        out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                                }
                            %>&ndash;%&gt;
                        </form>

                        <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
                            <img style="height: 35px;" src="/partner/images/goBack.png">
                        </button>
                    </div>
                </div>
                <br><br><br>--%>

                <%
                    int records = 0;

                    Hashtable actionHistory = (Hashtable) request.getAttribute("actionHistory");
                    ActionEntry entry = new ActionEntry();
                    Hashtable statusmap = entry.getStatusHash();
                    Hashtable temphash = null;

                    try
                    {
                        records=Integer.parseInt((String)actionHistory.get("records"));
                    }
                    catch(Exception ex)
                    {
                        logger.error("Exceptiont",ex);
                        out.println("<div class=\"reporttable\" style=\"margin-top:100px\">");
                        out.println(Functions.NewShowConfirmation1("Sorry","Internal error while accessing data."));
                        out.println("</div>");
                    }

                    String style = "class=textb";
                    String ext = "light";

                    if(records>0)
                    {
                %>

                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Action History</strong></h2>
                            </div>
                            <div class="widget-content padding" style="overflow-x: auto;">

                                <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    <tr>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Action</b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Billing Descriptor</b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Remark</b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Timestamp</b></td>
                                    </tr>
                                    </thead>


                                    <%
                                        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        for (int pos = 1; pos <= records; pos++)
                                        {
                                            Functions functions = new Functions();
                                            String id = Integer.toString(pos);
                                            //int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);
                                            style = "class=\"tr" + (pos + 1) % 2 + "\"";

                                            temphash = (Hashtable) actionHistory.get(id);

                                            String date = (String)temphash.get("timestamp");
                                            String billingDescriptor = (String)temphash.get("responsedescriptor");
                                            String action = (String)temphash.get("action");
                                            String remark = (String)temphash.get("remark");
                                            //String Status = (String) statusmap.get((String) temphash.get("status"));

                                            out.println("<tr " + style + ">");
                                            out.println("<td valign=\"middle\" data-label=\"Action\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(action) + "</td>");
                                            if(functions.isValueNull(billingDescriptor))
                                                out.println("<td data-label=\"BillingDescriptor\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(billingDescriptor) +  "</td>");
                                            else
                                                out.println("<td data-label=\"BillingDescriptor\" style=\"text-align: center\">-</td>");

                                            if(functions.isValueNull(remark))
                                                out.println("<td data-label=\"Remark\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(remark) +  "</td>");
                                            else
                                                out.println("<td data-label=\"Remark\" style=\"text-align: center\">-</td>");
                                            try
                                            {
                                                out.println("<td valign=\"middle\" data-label=\"Timestamp\" align=\"center\"" + style + ">" + targetFormat.format(targetFormat.parse(date)) +  "</td>");
                                            }
                                            catch (ParseException e) {
                                                logger.error("ParseException",e);
                                                out.println("<div class=\"reporttable\" style=\"margin-top:100px\">");
                                                out.println(Functions.NewShowConfirmation1("Sorry","Internal error while accessing data."));
                                                out.println("</div>");
                                            }
                                            out.println("</tr>");
                                        }
                                    %>

                                </table>
                            </div>
                        </div>
                    </div>
                </div>
                <%
                    }

                %>



                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <%
                                Hashtable transactionDetails = (Hashtable) request.getAttribute("transactionsDetails");
                                String errormsg = (String) request.getAttribute("errormsg");
                                Hashtable innerHash = (Hashtable) transactionDetails.get("1");// Since we'll get details of one transaction only
                                if(innerHash!=null)
                                {
                            %>
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Transaction Details</strong></h2>
                            </div>

                            <div class="widget-content padding" style="overflow-x: auto;">

                                <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    <tr>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Tracking ID</b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Transaction Amount</b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Date of Transaction</b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Transaction Status</b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Status Detail</b></td>

                                    </tr>
                                    </thead>


                                    <%
                                            String trackingId = (String) innerHash.get("icicitransid");
                                            String amount = (String) innerHash.get("amount");
                                            String date = (String) innerHash.get("date");
                                            String transStatus = (String) sortedMap.get((String) innerHash.get("status"));
                                            Hashtable authCodeMap = transaction.getAuthCodeMap();
                                            String transStatusDetail = "Not Available";
                                            if(Functions.checkStringNull((String) innerHash.get("authqsiresponsecode"))!=null)
                                            {
                                                transStatusDetail = (String)authCodeMap.get((String) innerHash.get("authqsiresponsecode"));
                                                if(Functions.checkStringNull(transStatusDetail)==null)
                                                    transStatusDetail=(String) innerHash.get("authqsiresponsecode");
                                            }

                                            out.println("<tr " + style + ">");
                                            out.println("<td data-label=\"Tracking ID\" valign=\"middle\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(trackingId) + "</td>");
                                            out.println("<td data-label=\"Transaction Amount\" valign=\"middle\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(amount) +  "</td>");
                                            out.println("<td data-label=\"Date of Transaction\" valign=\"middle\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(date) +  "</td>");
                                            out.println("<td data-label=\"Transaction Status\" valign=\"middle\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(transStatus) + "</td>");
                                            out.println("<td data-label=\"Status Detail\" valign=\"middle\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(transStatusDetail) +  "</td>");
                                            out.println("</tr>");
                                        }
                                        else if (errormsg!=null)
                                        {
                                            out.println(Functions.NewShowConfirmation1("Sorry", errormsg+"<br>"));
                                        }
                                        else
                                        {
                                            out.println(Functions.NewShowConfirmation1("Sorry", "No records found."));
                                        }
                                    %>



                                </table>

                            </div>
                        </div>
                    </div>
                </div>


                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <%
                                if(innerHash!=null)
                                {
                            %>
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Card Details</strong></h2>
                            </div>

                            <div class="widget-content padding" style="overflow-x: auto;">

                                <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    <tr>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Bin Brand</b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Bin Sub Brand</b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Bin Card Type</b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Bin Card Category</b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Bin Usage Type</b></td>

                                    </tr>
                                    </thead>


                                    <%
                                                Functions functions = new Functions();
                                            String bin_brand = (String) innerHash.get("bin_brand");
                                            String bin_sub_brand = (String) innerHash.get("bin_sub_brand");
                                            String bin_card_type = (String) innerHash.get("bin_card_type");
                                            String bin_card_category = (String) innerHash.get("bin_card_category");
                                            String bin_usage_type = (String) innerHash.get("bin_usage_type");

                                            if (!functions.isValueNull(bin_brand))
                                                bin_brand = "-";

                                            if (!functions.isValueNull(bin_sub_brand))
                                                bin_sub_brand = "-";

                                            if (!functions.isValueNull(bin_card_type))
                                                bin_card_type = "-";

                                            if (!functions.isValueNull(bin_card_category))
                                                bin_card_category = "-";

                                            if (!functions.isValueNull(bin_usage_type))
                                                bin_usage_type = "-";

                                            out.println("<tr " + style + ">");
                                            out.println("<td data-label=\"Bin Brand\" valign=\"middle\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(bin_brand) + "</td>");
                                            out.println("<td data-label=\"Bin Sub Brand\" valign=\"middle\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(bin_sub_brand) +  "</td>");
                                            out.println("<td data-label=\"Bin Card Type\" valign=\"middle\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(bin_card_type) +  "</td>");
                                            out.println("<td data-label=\"Bin Card Category\" valign=\"middle\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(bin_card_category) + "</td>");
                                            out.println("<td data-label=\"Bin Usage Type\" valign=\"middle\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(bin_usage_type) +  "</td>");
                                            out.println("</tr>");
                                        }
                                        else if (errormsg!=null)
                                        {
                                            out.println(Functions.NewShowConfirmation1("Sorry", errormsg+"<br>"));
                                        }
                                        else
                                        {
                                            out.println(Functions.NewShowConfirmation1("Sorry", "No records found."));
                                        }
                                    %>



                                </table>

                            </div>
                        </div>
                    </div>
                </div>


            </div>
        </div>
    </div>
</form>
</body>
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
<%
    }
    else
    {
        response.sendRedirect("/agent/logout.jsp");
        return;
    }
%>
</html>