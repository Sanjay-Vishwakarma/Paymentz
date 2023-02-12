<%@ page import="org.owasp.esapi.ESAPI,
                 org.owasp.esapi.errors.ValidationException,
                 java.util.Calendar,
                 com.directi.pg.Functions,
                 java.util.Hashtable,
                 org.blobstreaming.lib.str"%>
                 <%@ include file="functions.jsp"%>
                 <%@ include file="index.jsp"%>
 <%--
  Created by IntelliJ IDEA.
  User: admin
  Date: Jun 29, 2012
  Time: 1:19:11 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head><title>Chargeback Report</title>
        <script type="text/javascript">

        function goBack()
          {
            document.location.href = "/icici/admin/index.jsp";

          }
        </script>

  </head>
  <body>
<%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
    %>
    <%
        int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
	    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),30);
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
        str = str + "&ctoken="+ctoken;
        str = str + "&SRecords=" + pagerecords;
        String searchType = (String)request.getAttribute("searchtype");
        String searchId = request.getParameter("SearchId");
        if((String)request.getAttribute("searchtype")!= null)
        {
        str = str + "&searchtype="+searchType;
        }
        if(request.getParameter("SearchId")!=null)
        {
        str = str + "&SearchId="+searchId;
        }
    %>
    <h1 align="center">Chargeback Report</h1>
    <form action="/icici/servlet/ChargebackReport?ctoken=<%=ctoken%>" method="post" name="forms" >
    <input type="hidden" value="<%=ctoken%>" name="ctoken">
	<table class="search" align=center border="1" cellpadding="2" cellspacing="0" width="35%" bordercolorlight="#000000" bordercolordark="#FFFFFF">

        <tr>
		<td  bgcolor="#2379A5" colspan="2" align="center"><font color="#FFFFFF" size="1" face="Verdana, Arial" ><b>Search Type</b></font></td>
        <td  bgcolor="#2379A5"  align="center"><font color="#FFFFFF" size="1" face="Verdana, Arial" ><b>Search ID</b></font></td>
		</tr>
        <tr align="center" >
			<td class=search  align="center">
            <input type="radio" name="searchtype" value="Toid"  >Toid</td>
            <td class=search  align="center">
            <input type="radio" name="searchtype" value="mid"  >MID</td>
            <input type="hidden" name="searchtype" value="<%=request.getParameter("searchtype")==null?"":request.getParameter("searchtype")%>" >
            <td><input align="center"  name="SearchId" maxlength="50"   value="<%=request.getParameter("SearchId")==null?"":request.getParameter("SearchId")%>"></td>
		</tr>
        <tr>
        <td  bgcolor="#2379A5" colspan="3" align="center"><font color="#FFFFFF" size="1" face="Verdana, Arial" ><b>Search by Date</b></font></td>
        </tr>
        <tr>
     <td valign="middle" align="left"      class="label">From:
        <select size="1" name="fdate" class="textBoxes" value="<%=request.getParameter("fdate")==null?"":request.getParameter("fdate")%>" >
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
                    out.println(Functions.printoptions(2005, 2014));
            %>
        </select>
        <%--<td valign="middle" align="right"  class="label"></td>--%>
    <td valign="middle" align="left"   class="label">To:
        <select size="1" name="tdate" class="textBoxes">

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
                    out.println(Functions.printoptions(2005, 2014));
            %>
        </select>

    </td>
    <td class=search colspan=3 align=center>
			<input type="submit" value="Search">
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

    if(request.getAttribute("report")!=null  /*&& request.getAttribute("searchtype")!=null*/)
       {
    	Hashtable hash=(Hashtable)request.getAttribute("report");
    	Hashtable temphash=null;
    	//int pageno=1;
    	int records=30;
    	//out.println(hash);
    	int hashsize=hash.size();

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


%>
<table align=center  >
<tr ><td valign="middle" colspan="11"  align="center" width="60%"  bgcolor="#008BBA"><font face="arial,verdana" size="2" color="#ffffff" >Chargeback report</font></td></tr>
<tr>

    <td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >Sr no</font></td>
	<td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >TrackingId</font></td>
	<td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >Toid</font></td>
    <td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >merchantid</font></td>
    <td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >Amount</font></td>
    <td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >CB_Description</font></td>
    <td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >CB_Partial</font></td>
    <td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >CB_Indicator</font></td>
    <td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >Status</font></td>
    <td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >CB_Date</font></td>
    <td valign="middle" align="center" bgcolor="#008BBA"><font color="#FFFFFF" >Processed</font></td>

</tr>
<%
    String style="class=td11";
	String ext="light";
    String hiddenField = "";

	for(int pos=1;pos<=records;pos++)
	{
		String id=Integer.toString(pos);
        hiddenField = "";

		int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

			if(pos%2==0)
			{
				style="class=td22";
				ext="dark";
			}
			else
			{
				style="class=td11";
				ext="light";
			}

		    temphash=(Hashtable)hash.get(id);
            out.println("<tr>");

            out.println("<td "+style+">&nbsp;"+srno+ "</td>");

			out.println("<td "+style+">&nbsp;" +temphash.get("icicitransid")+"</td>");
			out.println("<td "+style+">&nbsp;"+(String)temphash.get("toid")+"</td>");
            out.println("<td "+style+">&nbsp;"+(String)temphash.get("merchantid")+"</td>");
            out.println("<td "+style+">&nbsp;"+(String)temphash.get("amount")+"</td>");

            out.println("<td "+style+">&nbsp;"+(String)temphash.get("cb_reason")+"</td>");
            out.println("<td "+style+">&nbsp;"+(String)temphash.get("cb_partial")+"</td>");
            out.println("<td "+style+">&nbsp;"+(String)temphash.get("cb_indicator")+"</td>");
            out.println("<td "+style+">&nbsp;"+(String)temphash.get("status")+"</td>");
            out.println("<td "+style+">&nbsp;"+(String)temphash.get("cb_date")+"</td>");
            out.println("<td "+style+">&nbsp;"+(String)temphash.get("processed")+"</td>");




    }
%>
<tr><td colspan=3 valign="middle"   align="center"   bgcolor="#008BBA"> <font  color="#ffffff" >Total Records : <%=totalrecords%></font></td>
</table>
<br>

<table align=center valign=top><tr>
<td align=center>
<jsp:include page="page.jsp" flush="true">
    <jsp:param name="numrecords" value="<%=totalrecords%>"/>
    <jsp:param name="numrows" value="<%=pagerecords%>"/>
    <jsp:param name="pageno" value="<%=pageno%>"/>
    <jsp:param name="str" value="<%=ESAPI.encoder().encodeForURL(str)%>"/>
    <jsp:param name="page" value="ChargebackReport"/>
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