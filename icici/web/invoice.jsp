<%@ page
        import="com.directi.pg.Functions,com.invoice.dao.InvoiceEntry,com.logicboxes.util.ApplicationProperties,
                 org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ include file="/index.jsp" %>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>

<%String company = ESAPI.encoder().encodeForHTML(ApplicationProperties.getProperty("COMPANY"));%>
<%int invoiceno11 = 0;
    if(request.getAttribute("invoiceno")!=null)
    {
        invoiceno11 = (Integer) request.getAttribute("invoiceno");
    }

%>
<%int remindercounter = 0;
    if(request.getAttribute("remindercounter")!=null)
    {
        remindercounter = (Integer) request.getAttribute("remindercounter");
    }

%>
<%
    String currency = (String) session.getAttribute("currency");

    InvoiceEntry invoiceEntry = new InvoiceEntry();
    Hashtable statushash = invoiceEntry.getStatusHash();
    SortedMap sortedMap =invoiceEntry.getSortedMap();

    String orderid = Functions.checkStringNull(request.getParameter("orderid"))==null?"":request.getParameter("orderid");
    String orderdesc = Functions.checkStringNull(request.getParameter("orderdesc"))==null?"":request.getParameter("orderdesc");

    String memberid= Functions.checkStringNull(request.getParameter("memberid"))==null?"":request.getParameter("memberid");
    String trackingid = Functions.checkStringNull(request.getParameter("trackingid"))==null?"":request.getParameter("trackingid");
    String invoiceno = Functions.checkStringNull(request.getParameter("invoiceno"))==null?"":request.getParameter("invoiceno");
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

    }

    String status = Functions.checkStringNull(request.getParameter("status"))==null?"":request.getParameter("status");




    Calendar rightNow = Calendar.getInstance();
    String str = "";

    String currentyear= ""+rightNow.get(rightNow.YEAR);


    if (fdate == null) fdate = "" + 1;
    if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
    if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
    if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
    if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
    if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

    if (fdate != null) str = str + "fdate=" + fdate;
    if (tdate != null) str = str + "&tdate=" + tdate;
    if (fmonth != null) str = str + "&fmonth=" + fmonth;
    if (tmonth != null) str = str + "&tmonth=" + tmonth;
    if (fyear != null) str = str + "&fyear=" + fyear;
    if (tyear != null) str = str + "&tyear=" + tyear;
    if (orderid != null) str = str + "&orderid=" + orderid;
    if (orderdesc != null) str = str + "&orderdesc=" + orderdesc;
    if (memberid != null) str = str + "&memberid=" +memberid;
    if (trackingid != null) str = str + "&trackingid=" +trackingid;
    if(invoiceno != null) str = str + "&invoiceno=" + invoiceno;
    if(status != null) str = str + "&status=" + status;

    str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();


    int pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
    int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

    str = str + "&SRecords=" + pagerecords;
%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title>Merchants Management > Merchant Invoice</title>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
        function cancelreason(invoiceno)
        {
            var r= window.prompt("Please Enter Reason why to cancel the Invoice No:"+invoiceno,"");
            if(r!="")
            {
                document.getElementById("cancelreason"+invoiceno).value=r+"";
                //window.alert(document.getElementById("cancelreason"+invoiceno).value);
            }
            else
            {  window.alert("Please Enter a Valid Reason");
                cancelreason(invoiceno);
            }
        }
        function confirmsubmit(invoiceno)
        {
            var r= window.confirm("Are You Sure you want to cancel the Invoice No:"+invoiceno);
            if(r==true)
            {
                cancelreason(invoiceno);

                document.getElementById("cancelform"+invoiceno).submit();
            }
            else
            {

            }
        }
        function confirmsubmitreg(invoiceno)
        {
            var r= window.confirm("Are You Sure you want to Regenerate the Invoice No:"+invoiceno +"");
            if(r==true)
            {
                document.getElementById("regenerateform"+invoiceno).submit();

            }
            else
            {

            }
        }
    </script>
</head>

<body>
<form name="form" method="post" action="/icici/servlet/Invoice?ctoken=<%=ctoken%>">
<div class="row">
<div class="col-lg-12">
<div class="panel panel-default" >
<div class="panel-heading" >
     Merchant Invoice
</div>
<input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
<table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">
<tr><td colspan="4">&nbsp;</td></tr>
<tr>
<td>
<%
    String errormsg1 = (String) request.getAttribute("errormsg");
    if (errormsg1 != null)
    {
        out.println("<center><font class=\"textb\"><b>"+errormsg1+"<br></b></font></center>");
    }

%>

<table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


<tr><td colspan="4">&nbsp;</td></tr>

    <!--- Select Data Option Start --->
    <tr>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >From</td>
    <td width="3%" class="textb"></td>
    <td width="12%" class="textb">

        <select size="1" name="fdate" class="textBoxes" style="font-size:12px; " >
            <%
                if (fdate != null)
                    out.println(Functions.dayoptions(1, 31, fdate));
                else
                    out.println(Functions.printoptions(1, 31));
            %>
        </select>
        <select size="1" name="fmonth" class="textBoxes" style="color:#151B54;font-size:12px;" >
            <%
                if (fmonth != null)
                    out.println(Functions.newmonthoptions(1, 12, fmonth));
                else
                    out.println(Functions.printoptions(1, 12));
            %>
        </select>
        <select size="1" name="fyear" class="textBoxes" style="color:#151B54;font-size:12px;">
            <%
                if (fyear != null)
                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), fyear));
                else
                    out.println(Functions.printoptions(2005, 2013));
            %>
        </select>
    </td>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >To</td>
    <td width="3%" class="textb"></td>
    <td width="12%" class="textb">
        <select size="1" name="tdate" style="color:#151B54;font-size:12px; ">
            <%
                if (tdate != null)
                    out.println(Functions.dayoptions(1, 31, tdate));
                else
                    out.println(Functions.printoptions(1, 31));
            %>
        </select>
        <select size="1" name="tmonth" class="textBoxes" style="color:#151B54;font-size:12px;">
            <%
                if (tmonth != null)
                    out.println(Functions.newmonthoptions(1, 12, tmonth));
                else
                    out.println(Functions.printoptions(1, 12));
            %>
        </select>
        <select size="1" name="tyear" class="textBoxes" style="color:#151B54;font-size:12px;width:54px;">
            <%
                if (tyear != null)
                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                else
                    out.println(Functions.printoptions(2005, 2013));
            %>
        </select>
    </td>
    <!--- Select Data Option End  --->


        <!--- Select Data Option Status Select Option   --->


    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >Status</td>
    <td width="3%" class="textb"></td>
    <td width="10%" class="textb">
        <select size="1" name="status"  class="txtbox">
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
            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(status1)%>"
                    <%=select%>>
                <%=ESAPI.encoder().encodeForHTML(status2)%>
            </option>
            <%
                    }
                }
            %>
        </select>
    </td>
    </td>

</tr>
    <!--- Select Data Select Option End  --->

    <tr>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">&nbsp;</td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">&nbsp;</td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">&nbsp;</td>
</tr>
<tr>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >Tracking ID</td>
    <td width="3%" class="textb"></td>
    <td width="12%" class="textb">
        <input type=text name="trackingid" class="txtbox" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>">
    </td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb">Invoice NO</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <input type=text name="invoiceno"  class="txtbox" maxlength="20" value="<%=ESAPI.encoder().encodeForHTMLAttribute(invoiceno)%>" size="15">
    </td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb">Order ID</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <input type=text name="orderid" maxlength="100" class="txtbox" value="<%=ESAPI.encoder().encodeForHTMLAttribute(orderid)%>">
    </td>


</tr>
<tr>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">&nbsp;</td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">&nbsp;</td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">&nbsp;</td>

</tr>
<tr>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" colspan="2">Order Description</td>
    <td width="12%" class="textb">
        <input type=text name="orderdesc" maxlength="100"  size="15" class="txtbox" value="<%=ESAPI.encoder().encodeForHTMLAttribute(orderdesc)%>">
    </td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" for="mid">Member ID</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <%--<input type="text" name="memberid" maxlength="100"  size="15" class="txtbox" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>">--%>
            <input name="memberid" id="mid" value="<%=memberid%>" class="txtbox" autocomplete="on" >
    </td>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >Rows/pages</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <input type="text" maxlength="3"  size="15" class="txtbox" value="<%=ESAPI.encoder().encodeForHTMLAttribute(new Integer(pagerecords).toString())%>" name="SRecords" size="2" />
    </td>

</tr>
<tr>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">&nbsp;</td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">&nbsp;</td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">&nbsp;</td>

</tr>
<tr>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">&nbsp;</td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">&nbsp;</td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">

        <button type="submit" class="buttonform" name="B1">
            <i class="fa fa-clock-o"></i>
            Search
        </button>
    </td>
</tr>
<tr>
    <td width="4%" class="textb">&nbsp;</td>

</tr>
</table>
</td>
</tr>
</table>

</div>
</div>
</div>
</form>

<div class="reporttable">
<% if(request.getAttribute("error")==null && invoiceno11!=0 && remindercounter!=0)
{
    int temp= 8 - remindercounter;
    out.println("<font class=\"textb\">An E-Mail has Been Sent Again to the Customers E-Mail Address for Invoice Number"+invoiceno11+" <br> You have "+temp+" Retries Left to send This Email</font>");
}
else
{
    if(request.getAttribute("error")!=null){
%>

<%=(String)request.getAttribute("error")%>

<%}}%>
</center>
<br>
<%

    Hashtable hash = (Hashtable) request.getAttribute("transactionsdetails");
    Hashtable temphash = null;
    //out.println(hash);

    String error=(String ) request.getAttribute("errormessage");
    if(error !=null)
    {
        out.println(error);

    }

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
    String ext = "light";
    String style1 = "class=\"textb\"";
    if (records > 0)
    {
%>

<table align=center border="0" cellspacing="0" cellpadding="2" bordercolor="#FFFFFF" color="#FFFFFF" width="100%">
    <tr>
        <td>
            <table border="0" cellpadding="5" cellspacing="2" width="100%" color="#ffffff" align="center" class="table table-striped table-bordered table-hover table-green dataTable">
                <thead>
                <tr>
                    <td width="10%" class="th0">Date</td>
                    <td width="10%" class="th1">Invoice No</td>
                    <td width="10%" class="th1">Member ID</td>
                    <%--<td width="10%" class="th0">Tracking ID</td>--%>
                    <td width="15%" class="th1">OrderID</td>
                    <td width="20%" class="th0">Order Description</td>
                    <td width="10%" class="th1">Amount </td>
                    <td width="10%" class="th0">Status</td>
                    <td width="15%" class="th1">Action</td>
                </tr>
                </thead>
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <%


                    for (int pos = 1; pos <= records; pos++)
                    {
                        String id = Integer.toString(pos);

                        int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);

                        style = "class=\"tr" + (pos + 1) % 2 + "\"";

                        temphash = (Hashtable) hash.get(id);

                        String date = Functions.convertDtstamptoDate((String)temphash.get("dtstamp"));

                        orderid = (String)temphash.get("orderid");
                        orderdesc = (String)temphash.get("orderdescription");
                        trackingid =(String)temphash.get("trackingid");
                        invoiceno = (String)temphash.get("invoiceno");
                        memberid=(String) temphash.get("memberid");
                        ctoken= request.getParameter("ctoken");
                        String amount =(String) temphash.get("amount");
                        status = (String) temphash.get("status");
                        String accountid = (String) temphash.get("accountid");

                        ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date1 = new Date();


                        /*long generatedTime = (dateFormat.parse(temphash.get("timestamp").toString()).getTime())/(60*60*1000);
                        long currentTime = (dateFormat.parse(String.valueOf(dateFormat.format(date1))).getTime())/(60*60*1000);
                        long diffTime = currentTime - generatedTime;
                        String expPeriod = (String) temphash.get("expirationPeriod");*/

                        long expiredTime=Functions.getInvoiceExpiredTime(temphash.get("timestamp").toString(),temphash.get("expirationPeriod").toString());
                        Date d=new Date();
                        long currentTime=d.getTime();

                        String data = "";

                        if(expiredTime<=currentTime)
                        {
                            data = status.equals("mailsent") || status.equals("generated") ? "expired" : status;
                        }
                        else
                        {
                            data = status;
                        }

                        out.println("<tr " + style + ">");
                        out.println("<td align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(date) + "</td>");
                        out.println("<td align=\"center\"" + style + "><form action=\"InvoiceDetails?ctoken="+ctoken+"\"  method=\"post\"><input type=\"hidden\" name=\"invoiceno\" value=\""+invoiceno+"\"><input type=\"submit\" class=\"goto\" name=\"submit\" value=\""+invoiceno+"\"></form></td>");
                        out.println("<td align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(memberid) + "</td>");
                        /*if (trackingid == null)
                            out.println("<td align=\"center\"" + style + ">"+"-"+"</td>");
                        else
                            out.println("<td align=\"center\"" + style + "><form action=\"TransactionDetails?ctoken="+ctoken+"\" method=\"post\"><input type=\"hidden\" name=\"action\" value=\"TransactionDetails\"><input type=\"hidden\" name=\"STrackingid\" value=\""+trackingid+"\"><input type=\"hidden\" name=\"ctoken\" value=\""+ctoken+"\"><input type=\"hidden\" name=\"archive\" value=\"false\"><input type=\"hidden\" name=\"accountid\" value=\""+accountid+"\"><input type=\"submit\" class=\"goto\" name=\"submit\" value=\""+trackingid+"\"></form></td>");
*/
                        out.println("<td align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(orderid) + "</td>");
                        if(orderdesc == null)
                            out.println("<td align=\"center\"" + style + ">"+"-"+"</td>");
                        else
                        out.println("<td align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(orderdesc) + "</td>");

                        out.println("<td align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(amount) + "</td>");
                        out.println("<td align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(data) + "</td>");
                        out.println("<td align=\"center\"" + style + "> ");

                        if(ESAPI.encoder().encodeForHTML(status).equals("generated"))
                        {
                %>
              <table>
                  <tr <%=style%>>

                    <%
                        if(Integer.parseInt((String)((Hashtable)(new InvoiceEntry()).getInvoiceDetails(invoiceno)).get("remindercounter"))<8)
                        {
                    %>
                      <!-- Expire Status Not Required Start-->
                    <%--<td>--%>

                        <%--<form name=remindform action="RemindInvoice?ctoken=<%=ctoken%>" method="post" style="margin-bottom: inherit;">--%>
                            <%--<input type="hidden" name=invoiceno value=<%=invoiceno%> >--%>
                            <%--<input type="submit" name="Remind"value="Remind" class=button>--%>
                        <%--</form>--%>
                    <%--</td>--%>
                      <!-- Expire Status Not Required End-->

                      <td>
                      <form name=regenerateform<%=invoiceno%> id="regenerateform<%=invoiceno%>" style="margin-bottom: inherit;" action="RegenerateInvoiceConfirm?ctoken=<%=ctoken%>" method="post">
                          <input type="hidden" name="invoiceno" value=<%=invoiceno%> >
                          <input type="button" name="Regenerate" value="Regenerate" class="button" onclick=confirmsubmitreg(<%=invoiceno%>)>
                      </form>
                      </td>
                    <% } %>
                      <!-- Expire Status Not Required Start-->
                    <%--<td>&nbsp;</td>--%>
                    <%--<td>--%>
                    <%--<form name=cancelform<%=invoiceno%> id="cancelform<%=invoiceno%>" style="margin-bottom: inherit;" action="CancelInvoice?ctoken=<%=ctoken%>" method="post" >--%>
                        <%--<input type="hidden" name=invoiceno value=<%=invoiceno%> >--%>
                        <%--<input type="hidden" name=cancelreason id=cancelreason<%=invoiceno%>  >--%>
                        <%--<input type="button" name="Cancel"value="Cancel" class=button onclick=confirmsubmit(<%=invoiceno%>)>--%>
                    <%--</form>--%>
                <%--</td>--%>
                      <!-- Expire Status Not Required End-->
                </tr>
                </table>

                <%
                    }
                    if(ESAPI.encoder().encodeForHTML(status).equals("mailsent"))
                    {
                %>   <table><tr <%=style%>>
                <%
                    if(Integer.parseInt((String)((Hashtable)(new InvoiceEntry()).getInvoiceDetails(invoiceno)).get("remindercounter"))<8)
                    {
                %>

                <td>

                    <%

                        if(expiredTime>=currentTime)
                        {
                    %>
                    <form name=remindform action="RemindInvoice?ctoken=<%=ctoken%>" method="post" style="margin-bottom: inherit;">
                        <input type="hidden" name=invoiceno value=<%=invoiceno%> >

                        <input type="submit" name="Remind" value="Remind" class="button" >
                    </form>
                    <%
                    }
                    else
                    {
                    %>
                        <!-- Expire Status Changes Start-->
                  <form name=regenerateform<%=invoiceno%> id="regenerateform<%=invoiceno%>" style="margin-bottom: inherit;" action="RegenerateInvoiceConfirm?ctoken=<%=ctoken%>" method="post">
                        <input type="hidden" name="invoiceno" value=<%=invoiceno%> >
                        <input type="button" name="Regenerate" value="Regenerate" class="button" onclick=confirmsubmitreg(<%=invoiceno%>)>
                    </form>
                    <!-- Expire Status Changes Start-->

                    <%
                        }
                    %>
                </td>

                <% }%>

                <td>

                    <%
                        if(expiredTime>=currentTime)
                        {

                    %>
                    <form name=cancelform<%=invoiceno%> id="cancelform<%=invoiceno%>" style="margin-bottom: inherit;" action="CancelInvoice?ctoken=<%=ctoken%>" method="post" >
                        <input type="hidden" name=invoiceno value=<%=invoiceno%> >
                        <input type="hidden" name=cancelreason id=cancelreason<%=invoiceno%> >
                        <input type="button" name="Cancel" value="Cancel" class="button" onclick=confirmsubmit(<%=invoiceno%>) ></form>
                    <%
                        }
                    %>


                </td>


                <%
                    if(Integer.parseInt((String)((Hashtable)(new InvoiceEntry()).getInvoiceDetails(invoiceno)).get("remindercounter"))>=100)
                    {


                %>
                <td>
                    <form name=regenerateform<%=invoiceno%> id="regenerateform<%=invoiceno%>" style="margin-bottom: inherit;" action="RegenerateInvoiceConfirm?ctoken=<%=ctoken%>" method="post">
                        <input type="hidden" name="invoiceno" value=<%=invoiceno%> >
                        <input type="button" name="Regenerate" value="Regenerate" class="button" onclick=confirmsubmitreg(<%=invoiceno%>)>
                    </form> </td>
                <% } %>

            </tr>
            </table>


                <%
                    }
                    if(ESAPI.encoder().encodeForHTML(status).equals("processed"))
                    {
                %>
                <table>
                <tr <%=style%>>
                <td>
                    <form name=regenerateform<%=invoiceno%> id="regenerateform<%=invoiceno%>" style="margin-bottom: inherit;" action="RegenerateInvoiceConfirm?ctoken=<%=ctoken%>" method="post">
                        <input type="hidden" name=invoiceno value=<%=invoiceno%> >
                        <input type="button" name="Regenerate"value="Regenerate" class=button onclick=confirmsubmitreg(<%=invoiceno%>)>
                    </form>
                </td>


            </tr>

                </table>
                <%}
                    if(ESAPI.encoder().encodeForHTML(status).equals("expired"))
                    {
                %>
                <!-- Expire Status Changes Start -->
                <table>
                    <tr <%=style%>>--%>
                          <td>
                        <form name=regenerateform<%=invoiceno%> id="regenerateform<%=invoiceno%>" style="margin-bottom: inherit;" action="RegenerateInvoiceConfirm?ctoken=<%=ctoken%>" method="post">
                            <input type="hidden" name=invoiceno value=<%=invoiceno%> >
                           <input type="button" name="Regenerate" value="Regenerate" class=button onclick=confirmsubmitreg(<%=invoiceno%>)>
                        </form>
                          </td>

                            <%--<form name=regenerateform<%=invoiceno%> id="regenerateform<%=invoiceno%>" style="margin-bottom: inherit;" action="RegenerateInvoiceConfirm?ctoken=<%=ctoken%>" method="post">--%>
                                <%--<input type="hidden" name=invoiceno value=<%=invoiceno%> >--%>
                                <%--<input type="button" name="Regenerate"value="Regenerate" align="middle" class=button onclick=confirmsubmitreg(<%=invoiceno%>)>--%>
                            <%--</form> </td>--%>


                        <%--<form name=remindform action="RemindInvoice?ctoken=<%=ctoken%>" method="post" style="margin-bottom: inherit;">--%>
                            <%--<input type="hidden" name=invoiceno value=<%=invoiceno%> >--%>
                            <%--<input type="submit" name="Remind"value="Remind" class=button>--%>
                        <%--</form>--%>
                       <%--</td>--%>
                       <%--</tr>--%>

                        <%--<td>--%>

                            <%--<form name=remindform action="RemindInvoice?ctoken=<%=ctoken%>" method="post" style="margin-bottom: inherit;">--%>
                                <%--<input type="hidden" name=invoiceno value=<%=invoiceno%> >--%>
                                <%--<input type="submit" name="Remind"value="Remind" class=button>--%>
                            <%--</form>--%>
                        <%--</td>--%>

                        <%--<td>&nbsp;</td>--%>
                        <%--<td>--%>
                            <%--<form name=cancelform<%=invoiceno%> id="cancelform<%=invoiceno%>" style="margin-bottom: inherit;" action="CancelInvoice?ctoken=<%=ctoken%>" method="post" >--%>
                                <%--<input type="hidden" name=invoiceno value=<%=invoiceno%> >--%>
                                <%--<input type="hidden" name=cancelreason id=cancelreason<%=invoiceno%>  >--%>
                                <%--<input type="button" name="Cancel"value="Cancel" class=button onclick=confirmsubmit(<%=invoiceno%>)>--%>
                            <%--</form>--%>
                        <%--</td>--%>
                    <%--</tr>--%>
                        <!-- Expire Status Changes End  -->
                </table>


                <%
                        }

                        if(ESAPI.encoder().encodeForHTML(status).equals("cancelled"))
                        {
                %>
                <table><tr <%=style%>>
                <td>
                    <form name=regenerateform<%=invoiceno%> id="regenerateform<%=invoiceno%>" style="margin-bottom: inherit;" action="RegenerateInvoiceConfirm?ctoken=<%=ctoken%>" method="post">
                        <input type="hidden" name=invoiceno value=<%=invoiceno%> >
                        <input type="button" name="Regenerate"value="Regenerate" align="middle" class=button onclick=confirmsubmitreg(<%=invoiceno%>)>
                    </form> </td>

            </tr>
            </table>


                <%
                        }
                    }
                %>
                <thead>
                <tr>
                    <td  align="left" class="th0">Total Records : <%=totalrecords%></td>
                    <td  align="right" class="th0">Page No : <%=pageno%></td>
                    <td class="th0">&nbsp;</td>
                    <td class="th0">&nbsp;</td>
                    <td class="th0">&nbsp;</td>
                    <td class="th0">&nbsp;</td>
                    <td class="th0">&nbsp;</td>
                    <td class="th0">&nbsp;</td>
                    <td class="th0">&nbsp;</td>
                </tr>
                </thead>
            </table>
        </td></tr>
</table>

<center>
    <jsp:include page="page.jsp" flush="true">

        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
        <jsp:param name="numrows" value="<%=pagerecords%>"/>
        <jsp:param name="pageno" value="<%=pageno%>"/>
        <jsp:param name="str" value="<%=str%>"/>
        <jsp:param name="page" value="Invoice"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
    </jsp:include>

    <%
        }
        else
        {
            int fmonthnum = Integer.parseInt(fmonth)+1;
            int tmonthnum = Integer.parseInt(tmonth)+1;
            out.println(Functions.NewShowConfirmation("Sorry", "No records found.<br><br>Date :<br>From " + fdate + "/" + fmonthnum + "/" + fyear + "<br>To " + tdate + "/" + tmonthnum + "/" + tyear));
        }
    %>
</center>
</div>
</body>

</html>