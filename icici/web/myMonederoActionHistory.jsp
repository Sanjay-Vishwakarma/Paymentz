<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.errors.ValidationException"%>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Nishant
  Date: 11/8/12
  Time: 3:09 PM
  To change this template use File | Settings | File Templates.
--%>
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

<center><h2>MyMonedero Interface </h2></center>
<br>
<center><b>Action History</b> &nbsp;&nbsp;&nbsp;<a href="/icici/getMMStatus.jsp?ctoken=<%=ctoken%>">Get Status  </a>

    <form action="/icici/servlet/MyMonederoActionHistory?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
            <%

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
                <td width="692" bgcolor="#2379A5" colspan="3" align="center"><font color="#FFFFFF" size="1" face="Verdana, Arial" ><b>Action History</b></font></td>
            </tr>
            <tr>
                <td align=center>
                    <font color="black"> ToID:</font> <input maxlength="15" type="text" name="toid"  value="">
                </td>
                <td colspan="2" align=center><input type="submit" value="Search"></td>

            </tr>

            <tr>
                <td align=center colspan="1">
                    <font color="black">TrackingID:</font> <input maxlength="15" type="text" name="trackingid"  value="">
                </td>
                <td colspan="2" valign="middle" align="center" >
                &nbsp;&nbsp;Rows / Page:
                <input type="text" maxlength="5"  value="<%=pagerecords%>" name="SRecords" size="2" class="textBoxes">

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

    <%
    String errormsg = (String) request.getAttribute("error");
    if(errormsg!=null)
    {
            out.println("<table align=\"center\"  >");
            out.println("<tr><td algin=\"center\" ><font face=\"arial\" color=\"red\"  size=\"2\">");
            errormsg = errormsg.replace("&lt;BR&gt;","<BR>");
            out.println(errormsg);
            out.println("</font>");
            out.println("</td></tr></table>");
    }
    else
    {
        errormsg = "";
    }
     if(request.getAttribute("transactionHistory")!=null )
    {
	Hashtable hash=(Hashtable)request.getAttribute("transactionHistory");

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
if(records>0)
{
%>
<table align=center width="50%"  >
    <tr>
        <td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >Sr No</font></td>
        <td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >Trackingid</font></td>
        <td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >Amount</font></td>
        <td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >Currency</font></td>
        <td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >Action</font></td>
        <td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >AccountId</font></td>
        <td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF">Status in DB</font></td>
        <td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >Timestamp</font></td>
        <td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >WC-Txn-ID</font></td>
        <td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >RedirectURL</font></td>
        <td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >SourceID</font></td>
        <td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >DestinationID</font></td>

    </tr>
    <%
        String style="class=td11";
        String ext="light";
        String previousicicitransid ="";
        String previousStyle="class=td22";
        String previousExt="dark";
        String currentStyle="class=td11";
        String currentExt="light";

        for(int pos=1;pos<=records;pos++)
        {
            String id=Integer.toString(pos);

            int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);
            temphash=(Hashtable)hash.get(id);
            out.println("<tr>");
            if(!previousicicitransid.equals((String) temphash.get("TrackingId")))
            {
                String tempStyle="";
                String tempExt="";

                previousicicitransid = (String) temphash.get("TrackingId");

                tempStyle = previousStyle;
                tempExt =  previousExt;

                previousStyle=currentStyle;
                previousExt=currentExt;

                currentStyle=tempStyle;
                currentExt=tempExt;
            }
            style=currentStyle;
            ext=currentExt;
            out.println("<td "+style+">&nbsp;"+srno+ "</td>");
            out.println("<td "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("TrackingId"))+"</a></td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("amount"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("currency"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("action"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("accountid"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("status"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("timestamp"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("wctxnid"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("wcredirecturl"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("sourceid"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("destid"))+"</td>");

        }

    %>
    <tr>
        <td valign="middle" align="center" bgcolor="#008BBA" colspan="6" ><font color="#FFFFFF" >Total records <%=totalrecords%></font></td>
    </tr>
</table>

<table align=center valign=top><tr>
    <td align=center>
        <jsp:include page="page.jsp" flush="true">
            <jsp:param name="numrecords" value="<%=totalrecords%>"/>
            <jsp:param name="numrows" value="<%=pagerecords%>"/>
            <jsp:param name="pageno" value="<%=pageno%>"/>
            <jsp:param name="str" value="<%=str%>"/>
            <jsp:param name="page" value="MyMonederoActionHistory"/>
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
	out.println(ShowConfirmation("Sorry","No records found."));


}
   }

%>
    
    
    
    
    
    
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