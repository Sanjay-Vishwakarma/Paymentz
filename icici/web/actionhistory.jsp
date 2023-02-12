<%@ page import="org.owasp.esapi.ESAPI,
                 java.util.Hashtable,
                 com.directi.pg.Merchants,
                 com.directi.pg.core.GatewayTypeService,
                 com.directi.pg.Functions"%>
<%@ page import="java.util.*"%>
<%@ include file="functions.jsp"%>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp"%>
 <%--
  Created by IntelliJ IDEA.
  User: admin
  Date: Jun 8, 2012
  Time: 8:27:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>

  </head>
  <body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
    %>
  <h1 align="center" >Admin Action History Search Interface</h1>
  <%

    String str="";

	int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
	int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),30);

    String searchType = request.getParameter("searchType");
    String searchId = request.getParameter("SearchId");

    str = str + "searchType="+searchType;
    str = str + "&SearchId="+searchId;
    str = str + "&SRecords=" + pagerecords;
    str = str + "&ctoken=" + ctoken;
    Hashtable gatewayHash = GatewayTypeService.getGatewayTypes();
    String gateway = Functions.checkStringNull(request.getParameter("gateway"));
    if (gateway == null)
        gateway = "";
  %>

  <form action="/icici/servlet/ActionHistory?ctoken=<%=ctoken%>" method="post" name="F1" >
   <input type="hidden" value="<%=ctoken%>" name="ctoken">
	<table class="search" align=center border="1" cellpadding="2" cellspacing="0" width="30%" bordercolorlight="#000000" bordercolordark="#FFFFFF">
		<tr>
		<td width="692" bgcolor="#2379A5" colspan="3"><font color="#FFFFFF" size="1" face="Verdana, Arial"><b>Search</b></font></td>
		</tr>
		<tr>
			<td class=search width="5%" align="center">
            <input type="radio" name="searchType" value="Tracking Id" >Tracking Id</td>


            <td class=search width="5%" align="center">
            <input type="radio" name="searchType" value="Merchant ID" >Merchant ID</td>

            <td class=search width="5%" align="center">
            <input type="radio" name="searchType" value="MID" >MID</td>
		</tr>
        <tr>

            <td colspan=3 align=center>
            <input name="SearchId" maxlength="100" ></td>
            <input type="hidden" maxlength="5"  value="<%=pagerecords%>"  name="SRecords" >

        <tr>
        <td class=search width="5%" align="center">
        Gateway:
        <select size="1" name="gatewayType" class="textBoxes">
            <option value="all">All</option>
            <%
                Enumeration enu2 = gatewayHash.keys();
                String selected2 = "";
                String key2 = "";
                String value2 = "";


                while (enu2.hasMoreElements())
                {
                    key2 = (String) enu2.nextElement();
                    value2 = (String) gatewayHash.get(key2);

                    if (key2.equals(gateway))
                        selected2 = "selected";
                    else
                        selected2 = "";

            %>
            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key2)%>" <%=selected2%>><%=ESAPI.encoder().encodeForHTML(value2)%></option>
            <%
                }
            %>
        </select>
        </td>
		</tr>
        <tr>

            <td class=search colspan=3 align=center>
                <input type="submit" value="Search"></td>
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
     if(request.getAttribute("transactionHistory")!=null && request.getAttribute("searchType")!=null)
    {
	Hashtable hash=(Hashtable)request.getAttribute("transactionHistory");

	Hashtable temphash=null;

	int hashsize=hash.size();

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
	<td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >Action</font></td>
	<td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF">Status in DB</font></td>
	<td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >Timestamp</font></td>

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
        out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("action"))+"</td>");
        out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("status"))+"</td>");
        out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("timestamp"))+"</td>");
	}

%>
<tr>
<td valign="middle" align="center" bgcolor="#008BBA" colspan="6" ><font color="#FFFFFF" >Total records <%=totalrecords%></font></td>
</tr>
</table>
</form>
<table align=center valign=top><tr>
<td align=center>
<jsp:include page="page.jsp" flush="true">
    <jsp:param name="numrecords" value="<%=totalrecords%>"/>
    <jsp:param name="numrows" value="<%=pagerecords%>"/>
    <jsp:param name="pageno" value="<%=pageno%>"/>
    <jsp:param name="str" value="<%=str%>"/>
    <jsp:param name="page" value="ActionHistory"/>
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