<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.errors.ValidationException"%>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <title></title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>

<center><h2>All Transactions Interface </h2></center>
<br>
<center><b>Inquiry</b> &nbsp;&nbsp;&nbsp;<a href="/icici/?ctoken=<%=ctoken%>">Reconcilation cron</a>&nbsp;&nbsp;&nbsp;<a href="/icici/?ctoken=<%=ctoken%>">Chargeback Cron</a>&nbsp;&nbsp;&nbsp;<a href="/icici/GenericSettlementCron.jsp?ctoken=<%=ctoken%>">Settle Cron</a>&nbsp;&nbsp;&nbsp;<a href="/icici/servlet/GenericRefundList?ctoken=<%=ctoken%>">Refund</a>&nbsp;&nbsp;&nbsp;<a href="/icici/servlet/TransactionDetailList?ctoken=<%=ctoken%>">Details</a>&nbsp;&nbsp;&nbsp;<a href="/icici/genericchargebacklist.jsp?ctoken=<%=ctoken%>">Chargeback</a><br><br></center>

<form action="/icici/servlet/TransactionCheckList?ctoken=<%=ctoken%>" method="post" name="forms" >
    <input type="hidden" value="<%=ctoken%>" name="ctoken">
    <%
        Hashtable statushash = new Hashtable();

        statushash.put("begun", "Begun Processing");
        statushash.put("authstarted", "Auth Started");
        statushash.put("proofrequired", "Proof Required");
        statushash.put("authsuccessful", "Auth Successful");
        statushash.put("authfailed", "Auth Failed");
        statushash.put("capturestarted", "Capture Started");
        statushash.put("capturesuccess", "Capture Successful");
        statushash.put("capturefailed", "Capture Failed");
        statushash.put("podsent", "POD Sent ");
        statushash.put("settled", "Settled");
        statushash.put("markedforreversal", "Reversal Request Sent");
        statushash.put("reversed", "Reversed");
        statushash.put("chargeback", "Chargeback");
        statushash.put("failed", "Validation Failed");
        statushash.put("cancelled", "Cancelled Transaction");
        statushash.put("authcancelled", "Authorisation Cancelled");

        String str="";
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
        Calendar rightNow = Calendar.getInstance();
        String currentyear= "" + rightNow.get(rightNow.YEAR);
        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
        if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
        if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);
        str = str + "ctoken=" + ctoken;
        if (fdate != null) str = str + "&fdate=" + fdate;
        if (tdate != null) str = str + "&tdate=" + tdate;
        if (fmonth != null) str = str + "&fmonth=" + fmonth;
        if (tmonth != null) str = str + "&tmonth=" + tmonth;
        if (fyear != null) str = str + "&fyear=" + fyear;
        if (tyear != null) str = str + "&tyear=" + tyear;

        int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);



    %>

    <table class="search" align=center border="1" cellpadding="2" cellspacing="0" width="50%" bordercolorlight="#000000" bordercolordark="#FFFFFF">

        <tr>
            <td width="692" bgcolor="#2379A5" colspan="3" align="center"><font color="#FFFFFF" size="1" face="Verdana, Arial" ><b>Inquiry</b></font></td>
        </tr>
        <tr>
            <td align=center>
                <font color="black"> ToID:</font> <input maxlength="15" type="text" name="toid"  value="">
            </td>
            <td align=center>
                <font color="black"> MID:</font> <input maxlength="15" type="text" name="mid"  value="">
            </td>
            <td align=center><input type="submit" value="Search"></td>

        </tr>

        <tr>
            <td valign="middle" align=center class="label">
                <font color="black">Status:</font>
                <select size="1" name="status" class="textBoxes">
                    <option value="">All</option>
                    <%
                        java.util.Enumeration enu = statushash.keys();
                        String key = "";
                        String value = "";


                        while (enu.hasMoreElements())
                        {
                            key = (String) enu.nextElement();
                            value = (String) statushash.get(key);

                    %>
                    <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key)%>"><%=ESAPI.encoder().encodeForHTML(value)%></option>
                    <%
                        }
                    %>
                </select>
            </td>
            <td align=center>
                <font color="black">TrackingID:</font> <input maxlength="15" type="text" name="trackingid"  value="">
            </td>
        </tr>
        <tr>
            <%--<td valign="middle" align="right"    class="label">--%>
            <td valign="middle" align="left"     class="label">
                <font color="black"> From:</font><select size="1" name="fdate" class="textBoxes" value="<%=request.getParameter("fdate")==null?"":request.getParameter("fdate")%>" >
                    <%
                        if (fdate != null)
                            out.println(Functions.dayoptions(1, 31, fdate));
                        else
                            out.println(Functions.printoptions(1, 31));
                    %>
                </select>
                <select size="1" name="fmonth" class="textBoxes" value="<%=request.getParameter("fmonth")==null?"":request.getParameter("fmonth")%>" >
                    <%
                        if (fmonth != null)
                            out.println(Functions.newmonthoptions(1, 12, fmonth));
                        else
                            out.println(Functions.printoptions(1, 12));
                    %>
                </select>
                <select size="1" name="fyear" class="textBoxes" value="<%=request.getParameter("fyear")==null?"":request.getParameter("fyear")%>" >
                    <%
                        if (fyear != null)
                            out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), fyear));
                        else
                            out.println(Functions.printoptions(2005, 2020));
                    %>
                </select>
                <%--<td valign="middle" align="right"  class="label"></td>--%>
            <td valign="middle" align="left" class="label">
                <font color="black"> To:</font> <select size="1" name="tdate" class="textBoxes">

                    <%
                        if (tdate != null)
                            out.println(Functions.dayoptions(1, 31, tdate));
                        else
                            out.println(Functions.printoptions(1, 31));
                    %>
                </select>

                <select size="1" name="tmonth" class="textBoxes">

                    <%
                        if (tmonth != null)
                            out.println(Functions.newmonthoptions(1, 12, tmonth));
                        else
                            out.println(Functions.printoptions(1, 12));
                    %>
                </select>

                <select size="1" name="tyear" class="textBoxes">

                    <%
                        if (tyear != null)
                            out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                        else
                            out.println(Functions.printoptions(2005, 2020));
                    %>
                </select>

            </td>
            <td class=search align=center>

                <input type="button" value="Back" onclick="goBack()" />
            </td>


        </tr>

    </table>

</form>
<%  Hashtable hash = (Hashtable)request.getAttribute("transdetails");

    Hashtable temphash=null;
    int records=0;
    int totalrecords=0;



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
    if(hash!=null)
    {
        hash = (Hashtable)request.getAttribute("transdetails");
    }
    if(records>0)
    {
%>
<table align=center width="50%">
    <tr>
        <td valign="middle" align="center" bgcolor="#008BBA">Sr no</td>
        <td valign="middle" align="center" bgcolor="#008BBA">TrackingID</td>
        <td valign="middle" align="center" bgcolor="#008BBA">toid</td>
        <td valign="middle" align="center" bgcolor="#008BBA">MID</td>
        <td valign="middle" align="center" bgcolor="#008BBA">Description</td>
        <td valign="middle" align="center" bgcolor="#008BBA">status</td>
        <td valign="middle" align="center" bgcolor="#008BBA">Time</td>
        <td valign="middle" align="center" bgcolor="#008BBA">Payment Order No</td>
        <td valign="middle" align="center" bgcolor="#008BBA">Amount</td>
        <%--<td valign="middle" align="center" bgcolor="#008BBA">Refund Amount</td>--%>

        </td>
    </tr>
    <%
        String style="class=td1";
        String ext="light";

        for(int pos=1;pos<=records;pos++)
        {
            String id=Integer.toString(pos);

            int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

            if(pos%2==0)
            {
                style="class=td2";
                ext="dark";
            }
            else
            {
                style="class=td1";
                ext="light";
            }

            temphash=(Hashtable)hash.get(id);
            out.println("<tr>");
            out.println("<td "+style+">&nbsp;"+srno+ "</td>");
            out.println("<td "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"</a></td>");

            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("toid"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("fromid"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("description"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("status"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("timestamp"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("amount"))+"</td>");
            //out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)hash.get("refundamount"))+"</td>");
            out.println("<td>&nbsp;</td>");
            out.println("<td " + style + ">&nbsp;<font face=\"arial,verdana,helvetica\"  size=\"1\" ><a href=\"/icici/servlet/GenericInquiry?trackingid=" + ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("trackingid"))+"&description="+(String) temphash.get("description")+"&ctoken="+ctoken+"&fromid="+temphash.get("fromid")+"&accountid="+temphash.get("accountid")+"\"> <font class=\"textb\"> Transection Inquiry </font></a></font></td>");
            out.println("</tr>");
        }
    %>
</table>

<%
    }
    else
    {
        out.println(ShowConfirmation("Sorry","No records found."));
    }


%>
<table align=center valign=top><tr>
    <td align=center>
<jsp:include page="page.jsp" flush="true">
    <jsp:param name="numrecords" value="<%=totalrecords%>"/>
    <jsp:param name="numrows" value="<%=pagerecords%>"/>
    <jsp:param name="pageno" value="<%=pageno%>"/>
    <jsp:param name="str" value="<%=str%>"/>
    <jsp:param name="page" value="TransactionCheckList"/>
    <jsp:param name="currentblock" value="<%=currentblock%>"/>
    <jsp:param name="orderby" value=""/>
</jsp:include>
    </td>
</tr>
</table>
<%
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</body>
</html>