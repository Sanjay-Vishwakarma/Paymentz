<%@ page import="java.util.*,
                 com.directi.pg.Merchants,
                 com.directi.pg.Admin"%>
<%@ include file="functions.jsp"%>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp"%>
<html>
<head>

<script language="javascript">
       function ToggleAll(checkbox)
         {
             flag = checkbox.checked;
             var checkboxes = document.getElementsByName("icicitransid");
          	 var total_boxes = checkboxes.length;

            for(i=0; i<total_boxes; i++ )
            {
          	    checkboxes[i].checked =flag;
            }
          }

        function DoReverse()
        {
            //if (isNaN(document.reversalform.numrows.value))
                //alert("please enter valid page number");

            var checkboxes = document.getElementsByName("icicitransid");
            var total_boxes = checkboxes.length;
            flag = false;

            for(i=0; i<total_boxes; i++ )
            {
          	    if(checkboxes[i].checked)
                    {
                        flag= true;
                        break;
                    }
            }

            if(!flag)
                {
                    alert("Select at least one transaction");
                    return false;
                 }

            if (confirm("Do you really want to reverse all selected transaction."))
            {
                document.reversalform.submit();
            }
        }

    </script>
</head>
<body>
<%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (Admin.isLoggedIn(session))
    {
    %>
<br><br>
<form action="/icici/servlet/AdminFraudList?ctoken=<%=ctoken%>" method="post" name="F1" >
<%  String errormsg1 = (String)request.getAttribute("cbmessage");
        if (errormsg1 == null)
        {
        errormsg1 = "";
        }
        else
        {
        out.println("<table align=\"center\"  ><tr><td valign=\"middle\"><font class=\"text\" >");

        out.println(errormsg1);

        out.println("</font></td></tr></table>");
        }
%>
<%
    String errormsg = ESAPI.encoder().encodeForHTML((String) request.getAttribute("error"));
    if (errormsg != null)
    {
        out.println("<table align=\"center\"  ><tr><td><font class=\"text\" ></font>");
        out.println("<br><font face=\"arial\" color=\"red\" size=\"2\">");
        out.println(errormsg);
        out.println("</font>");
        out.println("</td></tr></table>");
    }
%>
<input type="hidden" value="<%=ctoken%>" name="ctoken">
	<table class="search" align=center border="1" cellpadding="2" cellspacing="0" width="60%" bordercolorlight="#000000" bordercolordark="#FFFFFF">
		<tr>
		<td width="692" bgcolor="#2379A5" colspan="3"><font color="#FFFFFF" size="1" face="Verdana, Arial"><b>Search</b></font></td>
		</tr>
		<tr>
			<td class=search width="20%" align="right">Tracking Id</td>
			<td><input name="STrakingid" maxlength="10"  size="10" value="<%=request.getParameter("STrakingid")==null?"":ESAPI.encoder().encodeForHTML(request.getParameter("STrakingid"))%>"></td>
		</tr>
		<tr>
			<td class=search width="20%" align="right">Description</td>
			<td><input name="SDescription" maxlength="100"  size="10" value="<%=request.getParameter("SDescription")==null?"":ESAPI.encoder().encodeForHTML(request.getParameter("SDescription"))%>"></td>
		</tr>
		<!--
		<tr>
			<td class=search width="20%" align="right">Credicard Number</td>
			<td><input name="SCc" size="10"></td>
		</tr>
		-->
		<tr>
			<td class=search colspan=2 align=center>
			<input type="submit" value="Search"></td>
		</tr>
	</table>
</form>
<br><br>


<%
	Hashtable hash=(Hashtable)request.getAttribute("transdetails");
	Hashtable temphash=null;
	//int pageno=1;
	//int records=30;
	//out.println(hash);
	int hashsize=hash.size();

	int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
	int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

	int records=0;
	int totalrecords=0;

	String currentblock=ESAPI.encoder().encodeForHTML(request.getParameter("currentblock"));

	if(currentblock==null)
	currentblock="1";

    String str="";
    str = str + "ctoken="+ctoken;
        str = str + "&SRecords=" + pagerecords;
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
<font class="info">Total records <%=totalrecords%></font>
<form name="reversalform" action="DoReverseFraudTransaction?ctoken=<%=ctoken%>" method="post">
<table align=center>
<tr>
	<td valign="middle" align="center" bgcolor="#008BBA"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
	<td valign="middle" align="center" bgcolor="#008BBA">TrackingId</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Memberid</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Company Name</td>
	<td valign="middle" align="center" bgcolor="#008BBA">SBM MerchantId</td>
    <td valign="middle" align="center" bgcolor="#008BBA">Captured Amount</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Refund Amount</td>
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
			out.println("<td "+style+">&nbsp;<input type=\"checkbox\" name=\"icicitransid\" value=\""+temphash.get("icicitransid")+"\"></td>");
			out.println("<td "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String)temphash.get("icicitransid"))+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("toid"))+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("company_name"))+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("merchantid"))+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("amount"))+"</td>");
			out.println("<td "+style+">&nbsp; <input class=\"textBoxes\" type=\"Text\" value=\""+ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("amount"))+"\" name=\"refundamount_"+ESAPI.encoder().encodeForHTML((String) temphash.get("icicitransid"))+"\" size=\"30\"> </td>");
			out.println("</tr>");
	}
%>
</table>
<br>
<center>
<input class="button" type="button" value="Reverse Selected"  onClick="return DoReverse();" >
</center>
</form>

<table align=center valign=top><tr>
<td align=center>
<jsp:include page="page.jsp" flush="true" >
	<jsp:param name="numrecords" value="<%=totalrecords%>" />
 	<jsp:param name="numrows" value="<%=pagerecords%>"/>
	<jsp:param name="pageno" value="<%=pageno%>"/>
	<jsp:param name="str" value="<%=ESAPI.encoder().encodeForURL(str)%>"/>
	<jsp:param name="page" value="AdminFraudList"/>
	<jsp:param name="currentblock" value="1"/>
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