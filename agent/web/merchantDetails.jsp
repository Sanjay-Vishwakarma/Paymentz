<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.directi.pg.Logger" %><%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<%!
    private static Logger logger=new Logger("merchantWireReports.jsp");
%>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("agentname"));%>

<style type="text/css">
    .table#myTable > thead > tr > th {font-weight: inherit;text-align: center;}
</style>

<html lang="en">
<head>

    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <%--<link rel="stylesheet" href=" /agent/transactionCSS/css/jquery.dataTables.min.css">
    <script type="text/javascript" src="/agent/transactionCSS/js/jquery.dataTables.min.js"></script>--%>

    <link href="/agent/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/agent/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script src="/agent/NewCss/libs/jquery-icheck/icheck.min.js"></script>

    <script type="text/javascript">

        $('#sandbox-container input').datepicker({
        });
    </script>
    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
        });
    </script>

    <title><%=company%> | Merchant Details</title>


    <%
        session.setAttribute("submit","merchantDetails");
        ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        if (agent.isLoggedInAgent(session))
        {
            Hashtable memberidDetails=agent.getAgentMemberDetailList((String)session.getAttribute("merchantid"));
            String memberid=nullToStr(request.getParameter("memberid"));
            String fdate=null;
            String tdate=null;

            /*String fmonth=null;
            String tmonth=null;
            String fyear=null;
            String tyear=null;*/
            try
            {
                fdate = ESAPI.validator().getValidInput("fromdate",(String)request.getParameter("fromdate"),"Days",3,true);
                tdate = ESAPI.validator().getValidInput("todate",(String)request.getParameter("todate"),"Days",3,true);
                /*fmonth = ESAPI.validator().getValidInput("fmonth",(String)request.getParameter("fmonth"),"Months",2,true);
                tmonth = ESAPI.validator().getValidInput("tmonth",(String)request.getParameter("tmonth"),"Months",2,true);
                fyear = ESAPI.validator().getValidInput("fyear",(String)request.getParameter("fyear"),"Years",4,true);
                tyear = ESAPI.validator().getValidInput("tyear",(String)request.getParameter("tyear"),"Years",4,true);*/
            }
            catch(ValidationException e)
            {
                /*logger.error("ValidationException from merchantDetails.jsp", e);
                out.println("<div class=\"reporttable\" style=\"margin-top:100px\">");
                out.println(Functions.NewShowConfirmation1("Sorry","Internal error while accessing data."));
                out.println("</div>");*/
            }
            String str="";
            String ignoredates=Functions.checkStringNull(request.getParameter("ignoredates"));

            Date date = new Date();
            SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

            String Date = originalFormat.format(date);
            date.setDate(1);
            String fromDates = originalFormat.format(date);

            fdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDates : request.getParameter("fromdate");
            tdate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");

            Calendar rightNow = Calendar.getInstance();
            String currentyear= ""+rightNow.get(rightNow.YEAR);

            rightNow.setTime(new Date());
            if(fdate==null)fdate=""+1;
            if(tdate==null)tdate=""+rightNow.get(rightNow.DATE);
            /*if(fmonth==null)fmonth=""+rightNow.get(rightNow.MONTH);
            if(tmonth==null)tmonth=""+rightNow.get(rightNow.MONTH);
            if(fyear==null)fyear=""+rightNow.get(rightNow.YEAR);
            if(tyear==null)tyear=""+rightNow.get(rightNow.YEAR);*/

            if(fdate!=null)str=str+"fromdate="+fdate;
            if(tdate!=null)str=str+"&todate="+tdate;
            /*if(fmonth!=null)str=str+"&fmonth="+fmonth;
            if(tmonth!=null)str=str+"&tmonth="+tmonth;
            if(fyear!=null)str=str+"&fyear="+fyear;
            if(tyear!=null)str=str+"&tyear="+tyear;*/
            if(memberid!=null)str=str+"&memberid="+memberid;
            if(ignoredates!=null)str=str+"&ignoredates="+ignoredates;
            str=str+"&ctoken="+ctoken;

            int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
            int pagerecords=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),30);
    %>



    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
    </script>


</head>

<%--
<link href="/agent/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/agent/datepicker/datepicker/bootstrap-datepicker.js"></script>--%>
<body>

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">

                <div class="col-sm-12 portlets ui-sortable">

                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%>'s Merchant Details</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>



                        <form name="form" method="post" action="/agent/net/MerchantDetails?ctoken=<%=ctoken%>">

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
                            <div class="widget-content padding">
                                <div id="horizontal-form">

                                    <div class="form-group col-md-3">
                                        <label>From</label>
                                        <input type="text" name="fromdate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">

                                    </div>

                                    <div class="form-group col-md-3">
                                        <label>To</label>
                                        <input type="text" name="todate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">

                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
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
                                            <option value="<%=key3%>" <%=selected3%> > <%=key3%>---<%=ESAPI.encoder().encodeForHTMLAttribute(value3)%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>


                                    <div class="form-group col-md-3">
                                        <label style="color: transparent;">Path</label>
                                        <button type="submit" name="B1" class="btn btn-default" style="display:block;">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </div>


                                    <%--<div class="form-group col-md-9 has-feedback">&nbsp;</div>

                                    <div class="form-group col-md-3">
                                        &lt;%&ndash;<button type="submit" name="B1" class="btnblue" style="width:100px;margin-left: 20%; margin-top: 25px;background: rgb(126, 204, 173);">
                                            <i class="fa fa-save"></i>
                                            Search
                                        </button>&ndash;%&gt;
                                        <button type="submit" name="B1" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;Search</button>
                                    </div>--%>
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
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Summary Data</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" style="overflow-y: auto;">
                            <br>
                            <%--<div class="table-responsive datatable">--%>
                            <%--<input type="hidden" value="<%=terminalBuffer.toString()%>" name="terminalbuffer">--%>

                            <%----------------------Report Start-----------------------------------%>


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


                                Hashtable hash = (Hashtable)request.getAttribute("transdetails");
                                Functions functions = new Functions();
                                Hashtable temphash=null;
                                int records=0;
                                int totalrecords=0;
                                String error=(String) request.getAttribute("error");
                                String currentblock=request.getParameter("currentblock");

                                if(currentblock==null)
                                    currentblock="1";

                                try
                                {
                                    records=Integer.parseInt((String)hash.get("records"));
                                    totalrecords=Integer.parseInt((String)hash.get("totalrecords"));
                                }
                                catch(Exception ex)
                                {

                                }

                                if(records>0 && hash!=null)
                                {
                            %>

                            <div id="showingid"><strong>Note: Click on the Merchant ID to see the Merchant Details</strong></div>
                            <table id="myTable" class="display table table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <%--<table  id="datatables-2" class="table table-striped table-bordered" cellspacing="0" width="100%">--%>
                                <thead>
                                <tr style="background-color: #7eccad !important;color: white;">
                                    <th>Sr No</th>
                                    <th>Merchant ID</th>
                                    <th>Company Name</th>
                                    <th>Contact Persons</th>
                                    <th>Contact Email</th>
                                    <th>Country</th>
                                    <th>Currency</th>
                                    <th>Terminal ID</th>
                                    <th>Is Terminal Active</th>
                                    <th>Descriptor</th>
                                </tr>
                                </thead>
                                <tbody>


                                <%
                                    String style="class=td1";
                                    String ext="light";

                                    for(int pos=1;pos<=records;pos++)
                                    {
                                        String id=Integer.toString(pos);
                                        int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);
                                        style = "class=\"tr" + (pos + 1) % 2 + "\"";
                                        temphash=(Hashtable)hash.get(id);

                                        out.println("<tr>");
                                        out.println("<td data-label=\"Sr no\" align=\"center\" "+style+">&nbsp;"+srno+ "</td>");
                                        out.println("<td data-label=\"Merchant ID\" align=\"center\" "+style+"><form action=\"/agent/net/AgentMerchantDetailConfig?ctoken="+ctoken+"\" method=\"post\" ><input type=\"hidden\" name=\"toid\" value=\""+temphash.get("memberid")+"\"><input type=\"hidden\" name=\"terminalid\" value=\""+temphash.get("terminalid")+"\"><input type=\"submit\" class=\"btn btn-default\" name=\"merchantid\" value=\""+temphash.get("memberid")+"\">");
                                        out.println(requestParameter.toString());
                                        out.print("</form></td>");
                                        out.println("<td data-label=\"Company Name\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("company_name"))+"</td>");
                                        out.println("<td data-label=\"Contact Persons\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("contact_persons"))+"<input type=\"hidden\" name=\"toid\" value=\""+temphash.get("toid")+"\"></td>");
                                        out.println("<td data-label=\"Contact Email\" align=\"center\" "+style+">&nbsp;"+functions.getEmailMasking((String)temphash.get("contact_emails"))+"<input type=\"hidden\" name=\"description\" value=\""+temphash.get("description")+"\"></td>");
                                        out.println("<td data-label=\"Country\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("country"))+"</td>");
                                        out.println("<td data-label=\"Currency\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("currency"))+"</td>");
                                        out.println("<td data-label=\"Terminal ID\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("terminalid"))+"</td>");
                                        out.println("<td data-label=\"Is Terminal Active\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("isActive"))+"</td>");
                                        out.println("<td data-label=\"Descriptor\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("displayname"))+"</td>");
                                        out.println("</tr>");
                                    }
                                %>


                                </tbody>
                            </table>


                        </div>
                    </div>
                </div>
            </div>


            <div id="showingid"><strong>Showing Page <%=pageno%> of <%=totalrecords%> records</strong></div>

            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="MerchantDetails"/>
                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                <jsp:param name="orderby" value=""/>
            </jsp:include>

            <%
                }
                else if(functions.isValueNull(error))
                {
                    out.println(Functions.NewShowConfirmation1("Error",error));
                }
                else
                {
                    out.println(Functions.NewShowConfirmation1("Sorry", "No Record Found."));
                }
            %>

        </div>
    </div>
</div>
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